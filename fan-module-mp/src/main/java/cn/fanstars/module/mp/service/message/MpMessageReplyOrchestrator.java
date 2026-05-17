package cn.fanstars.module.mp.service.message;

import cn.fanstars.framework.tenant.core.util.TenantUtils;
import cn.fanstars.module.mp.controller.admin.open.vo.MpOpenHandleMessageReqVO;
import cn.fanstars.module.mp.dal.dataobject.account.MpAccountDO;
import cn.fanstars.module.mp.dal.dataobject.forward.rule.MessageForwardRuleDO;
import cn.fanstars.module.mp.enums.forward.MessageForwardLogStatusEnum;
import cn.fanstars.module.mp.enums.forward.MessageForwardModeEnum;
import cn.fanstars.module.mp.framework.mp.config.MpMessageHandleExecutorConfiguration;
import cn.fanstars.module.mp.framework.mp.config.MpMessageHandleProperties;
import cn.fanstars.module.mp.framework.mp.core.MpServiceFactory;
import cn.fanstars.module.mp.framework.mp.core.context.MpContextHolder;
import cn.fanstars.module.mp.service.forward.MessageForwardExecuteServiceImpl;
import cn.fanstars.module.mp.service.forward.bo.RuleForwardOutcome;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 微信回调消息多线程编排器
 * <p>
 * 并行执行：入库、同步转发 HTTP、本地 {@link WxMpMessageRouter}；主线程通过 {@link CompletableFuture#allOf}
 * 等待同步转发至 {@code fan.mp.message-reply-wait-timeout-ms}。超时向微信返回空串；未完成任务不 cancel，
 * 由 {@link #onSyncForwardComplete} 补写转发日志。
 */
@Service
@Slf4j
public class MpMessageReplyOrchestrator {

    @Resource
    private MpMessageHandleProperties mpMessageHandleProperties;
    @Resource
    private MpMessageService mpMessageService;
    @Resource
    private MpServiceFactory mpServiceFactory;
    @Resource
    private MessageForwardExecuteServiceImpl messageForwardExecuteService;
    @Resource
    @Qualifier(MpMessageHandleExecutorConfiguration.MP_MESSAGE_HANDLE_EXECUTOR)
    private ThreadPoolTaskExecutor mpMessageHandleExecutor;

    /**
     * 编排处理并返回被动回复 XML
     *
     * @param appId       公众号 appId
     * @param account     公众号账号（含 tenantId）
     * @param inMessage   解密后的入站消息
     * @param rawContent  微信 POST 原始 XML（转发透传 body）
     * @param reqVO       回调 query（验签、encrypt_type 等）
     * @param wxMpService 当前公众号 WxMpService
     * @return 被动回复 XML；无回复时返回空串（微信仍视为成功）
     */
    public String orchestrate(String appId, MpAccountDO account, WxMpXmlMessage inMessage, String rawContent,
                              MpOpenHandleMessageReqVO reqVO, WxMpService wxMpService) {
        // 多租户上下文，子线程需透传
        Long tenantId = account.getTenantId();
        int waitTimeoutMs = mpMessageHandleProperties.getMessageReplyWaitTimeoutMs();
        // 向微信返回的硬截止时间（不 cancel 超时后仍在跑的后台任务）
        long deadlineMs = System.currentTimeMillis() + waitTimeoutMs;

        // 按账号 + 消息类型筛选启用规则（已按 priority DESC 排序）
        List<MessageForwardRuleDO> matchedRules = messageForwardExecuteService.getMatchedRules(account.getId(), inMessage);
        // 参与被动回复等待的同步转发规则
        List<MessageForwardRuleDO> syncRules = matchedRules.stream()
                .filter(rule -> Objects.equals(rule.getForwardMode(), MessageForwardModeEnum.SYNC.getMode()))
                .collect(Collectors.toList());
        // 仅后台透传、不参与被动回复等待的异步规则
        List<MessageForwardRuleDO> asyncRules = matchedRules.stream()
                .filter(rule -> Objects.equals(rule.getForwardMode(), MessageForwardModeEnum.ASYNC.getMode()))
                .toList();
        if (matchedRules.isEmpty()) {
            log.info("[orchestrate][appId({}) msgType({}) 无命中转发规则，仅走本地 Router]", appId, inMessage.getMsgType());
        } else {
            log.info("[orchestrate][appId({}) msgType({}) 命中规则 sync={} async={} ids={}]",
                    appId, inMessage.getMsgType(), syncRules.size(), asyncRules.size(),
                    matchedRules.stream().map(MessageForwardRuleDO::getId).collect(Collectors.toList()));
        }

        // 已落库的 ruleId，避免主线程与 whenComplete 重复写日志
        Set<Long> loggedRuleIds = ConcurrentHashMap.newKeySet();

        // 根任务：入库并设置 MpContextHolder，供 MessageReceiveHandler 去重
        CompletableFuture<Long> persistFuture = supplyAsync(tenantId, appId, () -> {
            Long messageId = mpMessageService.receiveMessageReturnId(wxMpService, appId, inMessage);
            // Router 内 MessageReceiveHandler 据此跳过重复入库
            MpContextHolder.setMessagePersisted(true);
            // 转发日志、HTTP 追踪头使用
            MpContextHolder.setMessageId(messageId);
            return messageId;
        });

        // 异步规则：入库后在同线程池执行（避免 @Async 二次调度 + messageId 上下文丢失）
        for (MessageForwardRuleDO asyncRule : asyncRules) {
            // thenAcceptAsync 显式传入 messageId，避免子线程 MpContextHolder 为空
            persistFuture.thenAcceptAsync(messageId -> runWithContextVoid(tenantId, appId, () -> {
                log.info("[orchestrate][ruleId({}) 异步转发开始 messageId={}]", asyncRule.getId(), messageId);
                messageForwardExecuteService.executeForwardRule(account, inMessage, rawContent, reqVO, messageId, asyncRule);
            }), mpMessageHandleExecutor);
        }

        Map<Long, CompletableFuture<RuleForwardOutcome>> syncFutureMap = new HashMap<>();
        for (MessageForwardRuleDO syncRule : syncRules) {
            // 同步规则：入库完成后并行 HTTP，单规则超时受全局剩余时间约束
            CompletableFuture<RuleForwardOutcome> future = persistFuture.thenApplyAsync(messageId -> runWithContext(
                    tenantId, appId, () -> {
                        int remainingMs = (int) Math.max(0, deadlineMs - System.currentTimeMillis());
                        log.info("[orchestrate][ruleId({}) 同步转发开始 messageId={} remainingMs={}]",
                                syncRule.getId(), messageId, remainingMs);
                        return messageForwardExecuteService.forwardSyncRuleHttp(account, inMessage, rawContent, reqVO,
                                messageId, syncRule, remainingMs);
                    }), mpMessageHandleExecutor);
            // 超时后主线程可能已返回，此处补写未完成规则的日志（不 cancel HTTP）
            future.whenComplete((outcome, ex) -> onSyncForwardComplete(tenantId, appId, account, inMessage, rawContent,
                    syncRule, persistFuture, outcome, ex, loggedRuleIds));
            syncFutureMap.put(syncRule.getId(), future);
        }

        // 本地兜底：入库后并行走 WxMpMessageRouter（自动回复等）
        CompletableFuture<String> localFuture = persistFuture.thenApplyAsync(messageId -> runWithContext(tenantId, appId, () -> {
            // 再次写入上下文，确保 Router 同步 Handler 能识别已入库（Router 默认线程池不传 TTL）
            MpContextHolder.setMessagePersisted(true);
            MpContextHolder.setMessageId(messageId);
            return routeToXml(appId, inMessage);
        }), mpMessageHandleExecutor);

        // 主线程先确保 messageId 可用（写转发日志）
        Long messageId = awaitPersist(persistFuture, deadlineMs);
        // 同步规则 HTTP 结果汇总（ruleId → outcome）
        Map<Long, RuleForwardOutcome> completedOutcomes = new ConcurrentHashMap<>();
        // 优先等待同步转发回复（按 priority 选取候选 XML）
        String forwardReply = waitAndCollectForwardReply(syncRules, syncFutureMap, completedOutcomes, deadlineMs);

        if (StrUtil.isNotBlank(forwardReply)) {
            log.info("[orchestrate][appId({}) 采纳同步转发回复]", appId);
            saveSyncLogsWithSkip(account, inMessage, messageId, rawContent, syncRules, completedOutcomes, loggedRuleIds);
            // 明文或下游已 AES 包装的 XML，由 Controller 决定是否再 encrypt
            return forwardReply;
        }

        // 转发无候选回复时，在剩余 deadline 内等待本地 WxMpMessageRouter
        String localReply = awaitLocalReply(localFuture, deadlineMs);
        if (StrUtil.isNotBlank(localReply)) {
            log.info("[orchestrate][appId({}) 采纳本地 Router 回复]", appId);
        } else {
            log.info("[orchestrate][appId({}) 无有效回复，返回空串]", appId);
        }
        saveSyncLogsWithSkip(account, inMessage, messageId, rawContent, syncRules, completedOutcomes, loggedRuleIds);
        // 仍无回复则返回空串（微信视为处理成功）
        return StrUtil.nullToEmpty(localReply);
    }

    /**
     * 同步转发 Future 完成时补写日志（主线程已返回微信后仍可能触发）
     *
     * @param loggedRuleIds 与主线程 {@link #trySaveSyncRuleLog} 共用，保证每条规则只写一次日志
     */
    private void onSyncForwardComplete(Long tenantId, String appId, MpAccountDO account, WxMpXmlMessage inMessage,
                                       String rawContent, MessageForwardRuleDO syncRule,
                                       CompletableFuture<Long> persistFuture, RuleForwardOutcome outcome, Throwable ex,
                                       Set<Long> loggedRuleIds) {
        if (ex != null) {
            log.warn("[orchestrate][ruleId({}) 同步转发异常]", syncRule.getId(), ex);
            return;
        }
        if (outcome == null) {
            return;
        }
        // 主线程已返回微信后，HTTP 仍可能完成，在此补写转发日志
        runWithContextVoid(tenantId, appId, () -> {
            Long messageId = MpContextHolder.getMessageId();
            if (messageId == null) {
                // 回调线程上下文可能已 clear
                messageId = persistFuture.getNow(null);
            }
            if (messageId == null) {
                return;
            }
            trySaveSyncRuleLog(account, inMessage, messageId, rawContent, syncRule, outcome,
                    outcome.getLogStatus(), loggedRuleIds);
        });
    }

    /**
     * 按 {@code loggedRuleIds} 去重写入单条同步规则日志
     *
     * @param logStatus 可为 SKIPPED（低优先级已有回复时）
     */
    private void trySaveSyncRuleLog(MpAccountDO account, WxMpXmlMessage inMessage, Long messageId, String rawContent,
                                    MessageForwardRuleDO rule, RuleForwardOutcome outcome, Integer logStatus,
                                    Set<Long> loggedRuleIds) {
        if (messageId == null || !loggedRuleIds.add(rule.getId())) {
            return;
        }
        messageForwardExecuteService.saveSyncRuleLog(account, inMessage, messageId, rawContent, rule, outcome, logStatus);
    }

    /**
     * 主线程内按优先级写同步规则日志，并标记「已有回复」的后续规则为 SKIPPED
     *
     * @param loggedRuleIds 已写日志的 ruleId（与 {@link #onSyncForwardComplete} 去重）
     */
    private void saveSyncLogsWithSkip(MpAccountDO account, WxMpXmlMessage inMessage, Long messageId, String rawContent,
                                      List<MessageForwardRuleDO> syncRules,
                                      Map<Long, RuleForwardOutcome> completedOutcomes, Set<Long> loggedRuleIds) {
        if (messageId == null || syncRules.isEmpty()) {
            return;
        }
        // 已采纳的最高优先级回复（仅用于标记后续规则 SKIPPED）
        String adoptedReplyXml = null;
        for (MessageForwardRuleDO rule : syncRules) {
            RuleForwardOutcome outcome = completedOutcomes.get(rule.getId());
            if (outcome == null) {
                // 超时未完成或未启动
                continue;
            }
            if (loggedRuleIds.contains(rule.getId())) {
                // whenComplete 已写过
                continue;
            }
            // 已有回复则跳过低优先级候选
            boolean skipReply = adoptedReplyXml != null && outcome.isReplyCandidate();
            Integer logStatus = skipReply
                    ? MessageForwardLogStatusEnum.SKIPPED.getStatus()
                    : outcome.getLogStatus();
            trySaveSyncRuleLog(account, inMessage, messageId, rawContent, rule, outcome, logStatus, loggedRuleIds);
            if (!skipReply && outcome.isReplyCandidate() && adoptedReplyXml == null) {
                adoptedReplyXml = outcome.getReplyXml();
            }
        }
    }

    /**
     * 等待入库完成（占用全局等待预算的一部分）
     *
     * @return 消息编号；超时未完成时可能为 null
     */
    private Long awaitPersist(CompletableFuture<Long> persistFuture, long deadlineMs) {
        long remainingMs = deadlineMs - System.currentTimeMillis();
        if (remainingMs <= 0) {
            // 已超时，不阻塞
            return persistFuture.getNow(null);
        }
        try {
            // 阻塞等待入库，供写转发日志
            return persistFuture.get(remainingMs, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            log.debug("[awaitPersist][入库未在剩余时间内完成，使用已完成结果]", ex);
            // 超时后取已完成部分
            return persistFuture.getNow(null);
        }
    }

    /**
     * 在 deadline 内 {@link CompletableFuture#allOf} 等待全部同步转发结束（不 cancel），再按优先级选取回复
     *
     * @param completedOutcomes 输出：已完成的 ruleId → outcome
     * @return 采纳的被动回复 XML；无候选时返回 null
     */
    private String waitAndCollectForwardReply(List<MessageForwardRuleDO> syncRules,
                                              Map<Long, CompletableFuture<RuleForwardOutcome>> syncFutureMap,
                                              Map<Long, RuleForwardOutcome> completedOutcomes,
                                              long deadlineMs) {
        // 不 cancel 未完成的 HTTP，仅停止主线程等待
        if (!syncFutureMap.isEmpty()) {
            long remainingMs = deadlineMs - System.currentTimeMillis();
            if (remainingMs > 0) {
                CompletableFuture<?>[] allSync = syncFutureMap.values().toArray(new CompletableFuture[0]);
                try {
                    // 超时仅结束主线程等待，不 cancel 未完成的 HTTP
                    CompletableFuture.allOf(allSync).get(remainingMs, TimeUnit.MILLISECONDS);
                } catch (Exception ex) {
                    log.debug("[waitAndCollectForwardReply][等待同步转发未全部在 deadline 内完成]", ex);
                }
            }
        }
        // deadline 到达或未全部完成时，再收一轮已完成结果
        collectCompletedOutcomes(syncFutureMap, completedOutcomes);
        // 取已完成中的最高优先级候选（须全部 sync 完成后才采纳，由 pick 前 allOf 等待保证）
        return messageForwardExecuteService.pickForwardReplyByPriority(syncRules, completedOutcomes);
    }

    /**
     * 非阻塞收集已 done 的 Future 结果（含超时后仍陆续完成的项）
     */
    private static void collectCompletedOutcomes(Map<Long, CompletableFuture<RuleForwardOutcome>> syncFutureMap,
                                                 Map<Long, RuleForwardOutcome> completedOutcomes) {
        syncFutureMap.forEach((ruleId, future) -> {
            if (future.isDone() && !future.isCompletedExceptionally()) {
                try {
                    RuleForwardOutcome outcome = future.getNow(null);
                    if (outcome != null) {
                        completedOutcomes.putIfAbsent(ruleId, outcome);
                    }
                } catch (Exception ex) {
                    log.warn("[collectCompletedOutcomes][ruleId({}) 获取结果失败]", ruleId, ex);
                }
            }
        });
    }

    /**
     * 在全局 deadline 剩余时间内等待本地 Router；超时则 getNow，不 cancel
     *
     * @return 本地被动回复 XML，超时未完成时可能为 null
     */
    private String awaitLocalReply(CompletableFuture<String> localFuture, long deadlineMs) {
        long remainingMs = deadlineMs - System.currentTimeMillis();
        if (remainingMs <= 0) {
            return localFuture.getNow(null);
        }
        try {
            // 等待自动回复等 Handler 产出 XML
            return localFuture.get(remainingMs, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            log.debug("[awaitLocalReply][本地 Router 未在剩余时间内完成]", ex);
            // 超时也不 cancel Router，后台可能仍在跑
            return localFuture.getNow(null);
        }
    }

    /**
     * 本地自动回复链路
     * <p>
     * MessageReceiveHandler 须配置 {@code async(false)}，在同线程读取 {@link MpContextHolder#isMessagePersisted()}。
     */
    private String routeToXml(String appId, WxMpXmlMessage inMessage) {
        WxMpMessageRouter router = mpServiceFactory.getRequiredMpMessageRouter(appId);
        // 自动回复、菜单等 Handler 链
        WxMpXmlOutMessage outMessage = router.route(inMessage);
        // 明文被动回复，Controller 侧再 AES 包装
        return outMessage != null ? outMessage.toXml() : null;
    }

    /**
     * 在编排线程池中异步执行，并透传租户与 appId
     */
    private <T> CompletableFuture<T> supplyAsync(Long tenantId, String appId, Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(() -> runWithContext(tenantId, appId, supplier), mpMessageHandleExecutor);
    }

    /**
     * 包装租户 + appId 上下文，供线程池子任务使用
     */
    private static <T> T runWithContext(Long tenantId, String appId, Supplier<T> supplier) {
        // 子线程可见 appId
        MpContextHolder.setAppId(appId);
        try {
            // MyBatis 多租户插件
            return TenantUtils.execute(tenantId, supplier::get);
        } finally {
            // 防止线程池复用污染
            MpContextHolder.clear();
        }
    }

    /**
     * {@link #runWithContext} 的无返回值版本
     */
    private static void runWithContextVoid(Long tenantId, String appId, Runnable runnable) {
        runWithContext(tenantId, appId, () -> {
            runnable.run();
            return null;
        });
    }

}
