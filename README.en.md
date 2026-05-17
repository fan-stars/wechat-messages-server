# WeChat Messages Server — Backend API

<!-- README-I18N:START -->

[简体中文](./README.md) | **English**（当前）

<!-- README-I18N:END -->

Spring Boot **WeChat Official Account messaging service**, derived from [ruoyi-vue-pro](https://gitee.com/zhijiantianya/ruoyi-vue-pro). This repo is the **backend API**. The admin UI lives in [wechat-messages-server-vben](https://github.com/fan-stars/wechat-messages-server-vben).

The main extension is **`fan-module-mp`**: HTTP transparent forwarding of inbound fan messages (sync/async rules, priorities, forward logs, parallel passive-reply orchestration).

## Repositories

| | Backend API (this repo) | Admin UI |
| --- | --- | --- |
| **Gitee** | [fan-stars/wechat-messages-server](https://gitee.com/fan-stars/wechat-messages-server) | [fan-stars/wechat-messages-server-vben](https://gitee.com/fan-stars/wechat-messages-server-vben) |
| **GitHub** | [fan-stars/wechat-messages-server](https://github.com/fan-stars/wechat-messages-server) | [fan-stars/wechat-messages-server-vben](https://github.com/fan-stars/wechat-messages-server-vben) |

## Acknowledgments

Backend code is based on **[ruoyi-vue-pro](https://gitee.com/zhijiantianya/ruoyi-vue-pro)**. WeChat integration uses WxJava; message forwarding orchestration is implemented in this project's `fan-module-mp` module.

## Tech stack

- Java 17+, Spring Boot 3
- MySQL, Redis (same as ruoyi-vue-pro)
- WxJava (WeChat MP SDK)
- Retrofit + OkHttp (forward HTTP client)

## MP module

### Built-in features

Accounts, menus, fans, tags, messages, auto-reply, materials, statistics, etc. (see `fan-module-mp/pom.xml`).

### Message HTTP forwarding (highlight)

Rules POST the **same** WeChat query string and raw XML to downstream `target_url`. Downstream may return passive-reply XML; the orchestrator picks the highest-priority successful sync rule when `use_response_as_reply` is enabled.

| Feature | Summary |
| --- | --- |
| Sync / async | Async may use `receive_response` for logging only; never for passive reply |
| Priority | `priority DESC, id ASC`; first successful sync rule with `use_response_as_reply` wins |
| Transparent HTTP | Same query + XML as WeChat callback |
| AES reply | Pre-encrypted downstream XML is returned as-is (no double encrypt) |
| Forward logs | `mp_message_forward_log` with success / failure / timeout / skipped |

Full design: **[fan-module-mp/docs/mp-message-forward-design.md](./fan-module-mp/docs/mp-message-forward-design.md)**

## Key code & config

| Item | Location |
| --- | --- |
| WeChat entry | `MpOpenController` |
| Orchestrator | `MpMessageReplyOrchestrator` |
| Forward execution | `MessageForwardExecuteServiceImpl`, `MpMessageForwardClientImpl` |
| Thread pool | `MpMessageHandleExecutorConfiguration` |
| Passive reply wait | `fan.mp.message-reply-wait-timeout-ms` in `fan-server/.../application.yaml` (default 10000 ms) |
| SQL init | `sql/mysql/fan-module-mp.sql` |

## Quick start

### Requirements

- JDK 17+, MySQL 8+, Redis

### Database

Import scripts under `sql/mysql/` in order (**do not** use `ruoyi-vue-pro.sql`):

1. `fan-module-system.sql`
2. `fan-module-infra.sql`
3. `fan-module-mp.sql` (MP + forward rules/logs, dicts, menus)

Optionally import `quartz.sql` if you need scheduled jobs.

### WeChat config

Configure `wx.mp` in `fan-server/src/main/resources/application-local.yaml` (appId, secret, token, aesKey).

### Run

```bash
mvn -pl fan-server -am package -DskipTests
# Run fan-server main class (default port 48080)
```

Callback URL pattern:

```text
https://your-host/admin-api/mp/open/{appId}
```

## Downstream integration

See design doc §6: full `target_url`, optional `X-Mp-Rule-Id` / `X-Mp-Message-Id`, GET not forwarded, same token/aesKey as MP.

## Admin UI

[wechat-messages-server-vben](https://github.com/fan-stars/wechat-messages-server-vben) — forward rules & logs under `mp/forward/`.

## License

Follow the license terms of this repo and upstream projects (ruoyi-vue-pro, WxJava, etc.).
