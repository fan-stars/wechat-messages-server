/*
 Fan base schema — mp module tables
 Split from fan_base.sql / ruoyi-vue-pro.sql
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
-- ----------------------------
-- Table structure for mp_account
-- ----------------------------
-- ----------------------------
DROP TABLE IF EXISTS `mp_account`;
CREATE TABLE `mp_account`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '公众号名称',
  `account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '公众号账号',
  `app_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '公众号 appid',
  `app_secret` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '公众号密钥',
  `token` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '公众号 token',
  `aes_key` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '消息加解密密钥',
  `qr_code_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '二维码图片 URL',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '公众号账号表' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Records of mp_account
-- ----------------------------

-- ----------------------------
-- Table structure for mp_user
-- ----------------------------
DROP TABLE IF EXISTS `mp_user`;
CREATE TABLE `mp_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `openid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '粉丝标识',
  `union_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '微信生态唯一标识',
  `subscribe_status` tinyint NOT NULL COMMENT '关注状态',
  `subscribe_time` datetime NULL DEFAULT NULL COMMENT '关注时间',
  `unsubscribe_time` datetime NULL DEFAULT NULL COMMENT '取消关注时间',
  `nickname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称',
  `head_image_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像地址',
  `language` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '语言',
  `country` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '国家',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '城市',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `tag_ids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标签编号数组',
  `account_id` bigint NOT NULL COMMENT '公众号账号的编号',
  `app_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公众号 appId',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_app_id_openid`(`app_id` ASC, `openid` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '微信公众号粉丝表' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Records of mp_user
-- ----------------------------

-- ----------------------------
-- Table structure for mp_tag
-- ----------------------------
DROP TABLE IF EXISTS `mp_tag`;
CREATE TABLE `mp_tag`  (
  `id` bigint NOT NULL COMMENT '主键',
  `tag_id` bigint NOT NULL COMMENT '公众号标签 id',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名',
  `count` int NOT NULL DEFAULT 0 COMMENT '此标签下粉丝数',
  `account_id` bigint NOT NULL COMMENT '公众号账号的编号',
  `app_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公众号 appId',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_account_id`(`account_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '公众号标签表' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Records of mp_tag
-- ----------------------------

-- ----------------------------
-- Table structure for mp_menu
-- ----------------------------
DROP TABLE IF EXISTS `mp_menu`;
CREATE TABLE `mp_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `account_id` bigint NOT NULL COMMENT '公众号账号的编号',
  `app_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公众号 appId',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '菜单名称',
  `menu_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '菜单标识',
  `parent_id` bigint NOT NULL COMMENT '父菜单编号',
  `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '按钮类型',
  `url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '网页链接',
  `mini_program_app_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '小程序的 appId',
  `mini_program_page_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '小程序的页面路径',
  `article_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '跳转图文的媒体编号',
  `reply_message_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '回复的消息类型',
  `reply_content` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '回复的消息内容',
  `reply_media_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '回复的媒体 id',
  `reply_media_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '回复的媒体 URL',
  `reply_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '回复的标题',
  `reply_description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '回复的描述',
  `reply_thumb_media_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '回复的缩略图的媒体 id',
  `reply_thumb_media_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '回复的缩略图的媒体 URL',
  `reply_articles` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '回复的图文消息数组',
  `reply_music_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '回复的音乐链接',
  `reply_hq_music_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '回复的高质量音乐链接',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_account_id`(`account_id` ASC) USING BTREE,
  INDEX `idx_app_id_menu_key`(`app_id` ASC, `menu_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '公众号菜单表' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Records of mp_menu
-- ----------------------------

-- ----------------------------
-- Table structure for mp_material
-- ----------------------------
DROP TABLE IF EXISTS `mp_material`;
CREATE TABLE `mp_material`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `account_id` bigint NOT NULL COMMENT '公众号账号的编号',
  `app_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公众号 appId',
  `media_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公众号素材 id',
  `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件类型',
  `permanent` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否永久',
  `url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件服务器的 URL',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '名字',
  `mp_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '公众号文件 URL',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '视频素材的标题',
  `introduction` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '视频素材的描述',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_account_id`(`account_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '公众号素材表' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Records of mp_material
-- ----------------------------

-- ----------------------------
-- Table structure for mp_message
-- ----------------------------
DROP TABLE IF EXISTS `mp_message`;
CREATE TABLE `mp_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `msg_id` bigint NULL DEFAULT NULL COMMENT '微信公众号消息 id',
  `account_id` bigint NOT NULL COMMENT '公众号账号的 ID',
  `app_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公众号 appid',
  `user_id` bigint NOT NULL COMMENT '公众号粉丝的编号',
  `openid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公众号粉丝标志',
  `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息类型',
  `send_from` tinyint NOT NULL COMMENT '消息来源',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '消息内容',
  `media_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '媒体文件的编号',
  `media_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '媒体文件的 URL',
  `recognition` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '语音识别后文本',
  `format` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '语音格式',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标题',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `thumb_media_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '缩略图的媒体 id',
  `thumb_media_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '缩略图的媒体 URL',
  `url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '点击图文消息跳转链接',
  `location_x` double NULL DEFAULT NULL COMMENT '地理位置维度',
  `location_y` double NULL DEFAULT NULL COMMENT '地理位置经度',
  `scale` double NULL DEFAULT NULL COMMENT '地图缩放大小',
  `label` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '详细地址',
  `articles` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '图文消息数组',
  `music_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '音乐链接',
  `hq_music_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '高质量音乐链接',
  `event` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '事件类型',
  `event_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '事件 Key',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_account_id`(`account_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '公众号消息表' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Records of mp_message
-- ----------------------------

-- ----------------------------
-- Table structure for mp_message_template
-- ----------------------------
DROP TABLE IF EXISTS `mp_message_template`;
CREATE TABLE `mp_message_template`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `account_id` bigint NOT NULL COMMENT '公众号账号的编号',
  `app_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公众号 appId',
  `template_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公众号模板 ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '模板内容',
  `example` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '模板示例',
  `primary_industry` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '模板所属行业的一级行业',
  `deputy_industry` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '模板所属行业的二级行业',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_account_id`(`account_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '公众号模版消息表' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Records of mp_message_template
-- ----------------------------

-- ----------------------------
-- Table structure for mp_auto_reply
-- ----------------------------
DROP TABLE IF EXISTS `mp_auto_reply`;
CREATE TABLE `mp_auto_reply`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `account_id` bigint NOT NULL COMMENT '公众号账号的编号',
  `app_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公众号 appId',
  `type` tinyint NOT NULL COMMENT '回复类型',
  `request_keyword` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '请求的关键字',
  `request_match` tinyint NULL DEFAULT NULL COMMENT '请求的关键字的匹配',
  `request_message_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '请求的消息类型',
  `response_message_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '回复的消息类型',
  `response_content` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '回复的消息内容',
  `response_media_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '回复的媒体 id',
  `response_media_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '回复的媒体 URL',
  `response_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '回复的标题',
  `response_description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '回复的描述',
  `response_thumb_media_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '回复的缩略图的媒体 id',
  `response_thumb_media_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '回复的缩略图的媒体 URL',
  `response_articles` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '回复的图文消息',
  `response_music_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '回复的音乐链接',
  `response_hq_music_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '回复的高质量音乐链接',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_account_id`(`account_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '公众号消息自动回复表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mp_auto_reply
-- ----------------------------

-- ----------------------------
-- 字典类型 / 字典数据 / 菜单（mp 模块）
-- 依赖 system 模块已创建 system_dict_type、system_dict_data、system_menu 表
-- ----------------------------

-- system_dict_type
INSERT INTO `system_dict_type` (`id`, `name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `deleted_time`) VALUES (3001, '公众号自动回复的请求关键字匹配模式', 'mp_auto_reply_request_match', 0, '公众号自动回复的请求关键字匹配模式', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0', NULL);
INSERT INTO `system_dict_type` (`id`, `name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `deleted_time`) VALUES (3002, '公众号的消息类型', 'mp_message_type', 0, '公众号的消息类型', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0', NULL);

-- system_dict_data
INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3001, 1, '完全匹配', '1', 'mp_auto_reply_request_match', 0, 'primary', '', '公众号自动回复的请求关键字匹配模式 - 完全匹配', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3002, 2, '半匹配', '2', 'mp_auto_reply_request_match', 0, 'success', '', '公众号自动回复的请求关键字匹配模式 - 半匹配', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3003, 1, '文本', 'text', 'mp_message_type', 0, 'default', '', '公众号的消息类型 - 文本', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3004, 2, '图片', 'image', 'mp_message_type', 0, 'default', '', '公众号的消息类型 - 图片', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3005, 3, '语音', 'voice', 'mp_message_type', 0, 'default', '', '公众号的消息类型 - 语音', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3006, 4, '视频', 'video', 'mp_message_type', 0, 'default', '', '公众号的消息类型 - 视频', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3007, 5, '小视频', 'shortvideo', 'mp_message_type', 0, 'default', '', '公众号的消息类型 - 小视频', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3008, 6, '图文', 'news', 'mp_message_type', 0, 'default', '', '公众号的消息类型 - 图文', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3009, 7, '音乐', 'music', 'mp_message_type', 0, 'default', '', '公众号的消息类型 - 音乐', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3010, 8, '地理位置', 'location', 'mp_message_type', 0, 'default', '', '公众号的消息类型 - 地理位置', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3011, 9, '链接', 'link', 'mp_message_type', 0, 'default', '', '公众号的消息类型 - 链接', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3012, 10, '事件', 'event', 'mp_message_type', 0, 'default', '', '公众号的消息类型 - 事件', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');

-- system_menu
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3001, '公众号管理', '', 1, 100, 0, '/mp', 'ep:compass', NULL, NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3002, '账号管理', '', 2, 1, 3001, 'account', 'fa:user', 'mp/account/index', 'MpAccount', 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3003, '新增账号', 'mp:account:create', 3, 1, 3002, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3004, '修改账号', 'mp:account:update', 3, 2, 3002, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3005, '查询账号', 'mp:account:query', 3, 0, 3002, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3006, '删除账号', 'mp:account:delete', 3, 3, 3002, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3007, '生成二维码', 'mp:account:qr-code', 3, 4, 3002, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3008, '清空 API 配额', 'mp:account:clear-quota', 3, 5, 3002, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3009, '数据统计', 'mp:statistics:query', 2, 2, 3001, 'statistics', 'ep:trend-charts', 'mp/statistics/index', 'MpStatistics', 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3010, '标签管理', '', 2, 3, 3001, 'tag', 'ep:collection-tag', 'mp/tag/index', 'MpTag', 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3011, '查询标签', 'mp:tag:query', 3, 0, 3010, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3012, '新增标签', 'mp:tag:create', 3, 1, 3010, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3013, '修改标签', 'mp:tag:update', 3, 2, 3010, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3014, '删除标签', 'mp:tag:delete', 3, 3, 3010, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3015, '同步标签', 'mp:tag:sync', 3, 4, 3010, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3016, '粉丝管理', '', 2, 4, 3001, 'user', 'fa:user-secret', 'mp/user/index', 'MpUser', 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3017, '查询粉丝', 'mp:user:query', 3, 0, 3016, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3018, '修改粉丝', 'mp:user:update', 3, 1, 3016, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3019, '同步粉丝', 'mp:user:sync', 3, 2, 3016, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3020, '消息管理', '', 2, 5, 3001, 'message', 'ep:message', 'mp/message/index', 'MpMessage', 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3021, '图文发表记录', '', 2, 10, 3001, 'free-publish', 'ep:edit-pen', 'mp/freePublish/index', 'MpFreePublish', 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3022, '查询发布列表', 'mp:free-publish:query', 3, 1, 3021, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3023, '发布草稿', 'mp:free-publish:submit', 3, 2, 3021, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3024, '删除发布记录', 'mp:free-publish:delete', 3, 3, 3021, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3025, '图文草稿箱', '', 2, 9, 3001, 'draft', 'ep:edit', 'mp/draft/index', 'MpDraft', 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3026, '新建草稿', 'mp:draft:create', 3, 1, 3025, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3027, '修改草稿', 'mp:draft:update', 3, 2, 3025, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3028, '查询草稿', 'mp:draft:query', 3, 0, 3025, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3029, '删除草稿', 'mp:draft:delete', 3, 3, 3025, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3030, '素材管理', '', 2, 8, 3001, 'material', 'ep:basketball', 'mp/material/index', 'MpMaterial', 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3031, '上传临时素材', 'mp:material:upload-temporary', 3, 1, 3030, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3032, '上传永久素材', 'mp:material:upload-permanent', 3, 2, 3030, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3033, '删除素材', 'mp:material:delete', 3, 3, 3030, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3034, '上传图文图片', 'mp:material:upload-news-image', 3, 4, 3030, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3035, '查询素材', 'mp:material:query', 3, 5, 3030, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3036, '菜单管理', '', 2, 6, 3001, 'menu', 'ep:menu', 'mp/menu/index', 'MpMenu', 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3037, '自动回复', '', 2, 7, 3001, 'auto-reply', 'fa-solid:republican', 'mp/autoReply/index', 'MpAutoReply', 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3038, '查询回复', 'mp:auto-reply:query', 3, 0, 3037, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3039, '新增回复', 'mp:auto-reply:create', 3, 1, 3037, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3040, '修改回复', 'mp:auto-reply:update', 3, 2, 3037, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3041, '删除回复', 'mp:auto-reply:delete', 3, 3, 3037, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3042, '查询菜单', 'mp:menu:query', 3, 0, 3036, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3043, '保存菜单', 'mp:menu:save', 3, 1, 3036, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3044, '删除菜单', 'mp:menu:delete', 3, 2, 3036, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3045, '查询消息', 'mp:message:query', 3, 0, 3020, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3046, '发送消息', 'mp:message:send', 3, 1, 3020, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3047, '模版消息', '', 2, 5, 3001, 'message-template', 'ep:notebook', 'mp/messageTemplate/index', 'MpMessageTemplate', 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3048, '查询模版消息', 'mp:message-template:query', 3, 1, 3047, '', '', '', '', 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3049, '删除模版消息', 'mp:message-template:delete', 3, 2, 3047, '', '', '', '', 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3050, '同步公众号模板', 'mp:message-template:sync', 3, 3, 3047, '', '', '', '', 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3051, '给粉丝发送模版消息', 'mp:message-template:send', 3, 4, 3047, '', '', '', '', 0, b'1', b'1', b'1', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, b'0');

SET FOREIGN_KEY_CHECKS = 1;
