/*
 Fan base schema — infra module tables
 Split from fan_base.sql
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for infra_api_access_log
-- ----------------------------
DROP TABLE IF EXISTS `infra_api_access_log`;
CREATE TABLE `infra_api_access_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `trace_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '链路追踪编号',
  `user_id` bigint NOT NULL DEFAULT 0 COMMENT '用户编号',
  `user_type` tinyint NOT NULL DEFAULT 0 COMMENT '用户类型',
  `application_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '应用名',
  `request_method` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '请求方法名',
  `request_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '请求地址',
  `request_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '请求参数',
  `response_body` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '响应结果',
  `user_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户 IP',
  `user_agent` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '浏览器 UA',
  `operate_module` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作模块',
  `operate_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作名',
  `operate_type` tinyint NULL DEFAULT 0 COMMENT '操作分类',
  `begin_time` datetime NOT NULL COMMENT '开始请求时间',
  `end_time` datetime NOT NULL COMMENT '结束请求时间',
  `duration` int NOT NULL COMMENT '执行时长',
  `result_code` int NOT NULL DEFAULT 0 COMMENT '结果码',
  `result_msg` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '结果提示',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'API 访问日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of infra_api_access_log
-- ----------------------------

-- ----------------------------
-- Table structure for infra_api_error_log
-- ----------------------------
DROP TABLE IF EXISTS `infra_api_error_log`;
CREATE TABLE `infra_api_error_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `trace_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '链路追踪编号',
  `user_id` bigint NOT NULL DEFAULT 0 COMMENT '用户编号',
  `user_type` tinyint NOT NULL DEFAULT 0 COMMENT '用户类型',
  `application_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '应用名',
  `request_method` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '请求方法名',
  `request_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '请求地址',
  `request_params` varchar(8000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '请求参数',
  `user_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户 IP',
  `user_agent` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '浏览器 UA',
  `exception_time` datetime NOT NULL COMMENT '异常发生时间',
  `exception_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '异常名',
  `exception_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '异常导致的消息',
  `exception_root_cause_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '异常导致的根消息',
  `exception_stack_trace` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '异常的栈轨迹',
  `exception_class_name` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '异常发生的类全名',
  `exception_file_name` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '异常发生的类文件',
  `exception_method_name` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '异常发生的方法名',
  `exception_line_number` int NOT NULL COMMENT '异常发生的方法所在行',
  `process_status` tinyint NOT NULL COMMENT '处理状态',
  `process_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `process_user_id` int NULL DEFAULT 0 COMMENT '处理用户编号',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统异常日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of infra_api_error_log
-- ----------------------------

-- ----------------------------
-- Table structure for infra_codegen_column
-- ----------------------------
DROP TABLE IF EXISTS `infra_codegen_column`;
CREATE TABLE `infra_codegen_column`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` bigint NOT NULL COMMENT '表编号',
  `column_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字段名',
  `data_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字段类型',
  `column_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字段描述',
  `nullable` bit(1) NOT NULL COMMENT '是否允许为空',
  `primary_key` bit(1) NOT NULL COMMENT '是否主键',
  `ordinal_position` int NOT NULL COMMENT '排序',
  `java_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Java 属性类型',
  `java_field` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Java 属性名',
  `dict_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '字典类型',
  `example` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '数据示例',
  `create_operation` bit(1) NOT NULL COMMENT '是否为 Create 创建操作的字段',
  `update_operation` bit(1) NOT NULL COMMENT '是否为 Update 更新操作的字段',
  `list_operation` bit(1) NOT NULL COMMENT '是否为 List 查询操作的字段',
  `list_operation_condition` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '=' COMMENT 'List 查询操作的条件类型',
  `list_operation_result` bit(1) NOT NULL COMMENT '是否为 List 查询操作的返回字段',
  `html_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '显示类型',
  `col_width` bigint DEFAULT '0' COMMENT '列宽，前端表格使用',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '代码生成表字段定义' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of infra_codegen_column
-- ----------------------------

-- ----------------------------
-- Table structure for infra_codegen_table
-- ----------------------------
DROP TABLE IF EXISTS `infra_codegen_table`;
CREATE TABLE `infra_codegen_table`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `data_source_config_id` bigint NOT NULL COMMENT '数据源配置的编号',
  `scene` tinyint NOT NULL DEFAULT 1 COMMENT '生成场景',
  `table_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '表名称',
  `table_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '表描述',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `module_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模块名',
  `business_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '业务名',
  `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '类名称',
  `class_comment` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类描述',
  `author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '作者',
  `template_type` tinyint NOT NULL DEFAULT 1 COMMENT '模板类型',
  `front_type` tinyint NOT NULL COMMENT '前端类型',
  `parent_menu_id` bigint NULL DEFAULT NULL COMMENT '父菜单编号',
  `master_table_id` bigint NULL DEFAULT NULL COMMENT '主表的编号',
  `sub_join_column_id` bigint NULL DEFAULT NULL COMMENT '子表关联主表的字段编号',
  `sub_join_many` bit(1) NULL DEFAULT NULL COMMENT '主表与子表是否一对多',
  `tree_parent_column_id` bigint NULL DEFAULT NULL COMMENT '树表的父字段编号',
  `tree_name_column_id` bigint NULL DEFAULT NULL COMMENT '树表的名字字段编号',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '代码生成表定义' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of infra_codegen_table
-- ----------------------------

-- ----------------------------
-- Table structure for infra_config
-- ----------------------------
DROP TABLE IF EXISTS `infra_config`;
CREATE TABLE `infra_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '参数分组',
  `type` tinyint NOT NULL COMMENT '参数类型',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '参数键名',
  `value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '参数键值',
  `visible` bit(1) NOT NULL COMMENT '是否可见',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 101 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '参数配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of infra_config
-- ----------------------------
INSERT INTO `infra_config` VALUES (1, 'biz', 1, '用户管理-账号初始密码', 'system.user.init-password', '123456', b'0', '初始化密码 123456', 'admin', '2021-01-05 17:03:48', '1', '2024-07-20 17:22:47', b'0');
INSERT INTO `infra_config` VALUES (2, 'url', 2, 'MySQL 监控的地址', 'url.druid', '', b'1', '', '1', '2023-04-07 13:41:16', '1', '2023-04-07 14:33:38', b'0');
INSERT INTO `infra_config` VALUES (3, 'url', 2, 'SkyWalking 监控的地址', 'url.skywalking', '', b'1', '', '1', '2023-04-07 13:41:16', '1', '2023-04-07 14:57:03', b'0');
INSERT INTO `infra_config` VALUES (4, 'url', 2, 'Spring Boot Admin 监控的地址', 'url.spring-boot-admin', '', b'1', '', '1', '2023-04-07 13:41:16', '1', '2023-04-07 14:52:07', b'0');
INSERT INTO `infra_config` VALUES (5, 'url', 2, 'Swagger 接口文档的地址', 'url.swagger', '', b'1', '', '1', '2023-04-07 13:41:16', '1', '2023-04-07 14:59:00', b'0');
INSERT INTO `infra_config` VALUES (6, 'ui', 2, '腾讯地图 key', 'tencent.lbs.key', 'TVDBZ-TDILD-4ON4B-PFDZA-RNLKH-VVF6E', b'1', '腾讯地图 key', '1', '2023-06-03 19:16:27', '1', '2023-06-03 19:16:27', b'0');
INSERT INTO `infra_config` VALUES (7, '用户管理-账号初始密码', 2, '用户管理-注册开关', 'system.user.register-enabled', 'true', b'0', '', '1', '2025-04-26 17:23:41', '1', '2025-04-26 17:23:41', b'0');

-- ----------------------------
-- Table structure for infra_data_source_config
-- ----------------------------
DROP TABLE IF EXISTS `infra_data_source_config`;
CREATE TABLE `infra_data_source_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键编号',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '参数名称',
  `url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '数据源连接',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '密码',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '数据源配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of infra_data_source_config
-- ----------------------------

-- ----------------------------
-- Table structure for infra_file
-- ----------------------------
DROP TABLE IF EXISTS `infra_file`;
CREATE TABLE `infra_file`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文件编号',
  `config_id` bigint NULL DEFAULT NULL COMMENT '配置编号',
  `name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件名',
  `path` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件路径',
  `url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件 URL',
  `type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件类型',
  `size` int NOT NULL COMMENT '文件大小',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of infra_file
-- ----------------------------

-- ----------------------------
-- Table structure for infra_file_config
-- ----------------------------
DROP TABLE IF EXISTS `infra_file_config`;
CREATE TABLE `infra_file_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置名',
  `storage` tinyint NOT NULL COMMENT '存储器',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `master` bit(1) NOT NULL COMMENT '是否为主配置',
  `config` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '存储配置',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 101 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文件配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of infra_file_config
-- ----------------------------
INSERT INTO `infra_file_config` VALUES (1, '数据库（示例）', 1, '我是数据库', b'0', '{\"@class\":\"cn.fanstars.module.infra.framework.file.core.client.db.DBFileClientConfig\",\"domain\":\"http://127.0.0.1:48080\"}', '1', '2022-03-15 23:56:24', '1', '2025-08-18 08:35:14', b'0');
INSERT INTO `infra_file_config` VALUES (2, '七牛存储器（示例）', 20, '请换成你自己的密钥！！！', b'1', '{\"@class\":\"cn.fanstars.module.infra.framework.file.core.client.s3.S3FileClientConfig\",\"endpoint\":\"s3.cn-south-1.qiniucs.com\",\"domain\":\"http://test.fan.iocoder.cn\",\"bucket\":\"ruoyi-vue-pro\",\"accessKey\":\"3TvrJ70gl2Gt6IBe7_IZT1F6i_k0iMuRtyEv4EyS\",\"accessSecret\":\"wd0tbVBYlp0S-ihA8Qg2hPLncoP83wyrIq24OZuY\",\"enablePathStyleAccess\":false,\"enablePublicAccess\":true}', '1', '2024-01-13 22:11:12', '1', '2025-08-18 08:35:14', b'0');
INSERT INTO `infra_file_config` VALUES (3, '腾讯云存储（示例）', 20, '请换成你的密钥！！！', b'0', '{\"@class\":\"cn.fanstars.module.infra.framework.file.core.client.s3.S3FileClientConfig\",\"endpoint\":\"https://cos.ap-shanghai.myqcloud.com\",\"domain\":\"http://tengxun-oss.iocoder.cn\",\"bucket\":\"aoteman-1255880240\",\"accessKey\":\"AKIDAF6WSh1uiIjwqtrOsGSN3WryqTM6cTMt\",\"accessSecret\":\"X\",\"enablePathStyleAccess\":false,\"enablePublicAccess\":true}', '1', '2024-11-09 16:03:22', '1', '2025-08-18 08:35:14', b'0');
INSERT INTO `infra_file_config` VALUES (4, '阿里云存储（示例）', 20, '', b'0', '{\"@class\":\"cn.fanstars.module.infra.framework.file.core.client.s3.S3FileClientConfig\",\"endpoint\":\"oss-cn-beijing.aliyuncs.com\",\"domain\":\"http://ali-oss.iocoder.cn\",\"bucket\":\"yunai-aoteman\",\"accessKey\":\"LTAI5tEQLgnDyjh3WpNcdMKA\",\"accessSecret\":\"X\",\"enablePathStyleAccess\":false,\"enablePublicAccess\":true}', '1', '2024-11-09 16:47:08', '1', '2025-08-18 08:35:14', b'0');
INSERT INTO `infra_file_config` VALUES (5, '火山云存储（示例）', 20, '', b'0', '{\"@class\":\"cn.fanstars.module.infra.framework.file.core.client.s3.S3FileClientConfig\",\"endpoint\":\"tos-s3-cn-beijing.volces.com\",\"domain\":null,\"bucket\":\"yunai\",\"accessKey\":\"AKLTZjc3Zjc4MzZmMjU3NDk0ZTgxYmIyMmFkNTIwMDI1ZGE\",\"accessSecret\":\"X==\",\"enablePathStyleAccess\":false,\"enablePublicAccess\":true}', '1', '2024-11-09 16:56:42', '1', '2025-08-18 08:35:14', b'0');
INSERT INTO `infra_file_config` VALUES (6, '华为云存储（示例）', 20, '', b'0', '{\"@class\":\"cn.fanstars.module.infra.framework.file.core.client.s3.S3FileClientConfig\",\"endpoint\":\"obs.cn-east-3.myhuaweicloud.com\",\"domain\":\"\",\"bucket\":\"fan\",\"accessKey\":\"PVDONDEIOTW88LF8DC4U\",\"accessSecret\":\"X\",\"enablePathStyleAccess\":false}', '1', '2024-11-09 17:18:41', '1', '2025-08-18 08:35:14', b'0');
INSERT INTO `infra_file_config` VALUES (7, 'MinIO 存储（示例）', 20, '', b'0', '{\"@class\":\"cn.fanstars.module.infra.framework.file.core.client.s3.S3FileClientConfig\",\"endpoint\":\"http://127.0.0.1:9000\",\"domain\":\"http://127.0.0.1:9000/fan\",\"bucket\":\"fan\",\"accessKey\":\"admin\",\"accessSecret\":\"password\",\"enablePathStyleAccess\":false,\"enablePublicAccess\":true}', '1', '2024-11-09 17:43:10', '1', '2025-08-18 08:35:14', b'0');
INSERT INTO `infra_file_config` VALUES (8, '本地存储（示例）', 10, '仅适合 mac 或 windows', b'0', '{\"@class\":\"cn.fanstars.module.infra.framework.file.core.client.local.LocalFileClientConfig\",\"basePath\":\"/Users/yunai/tmp/file\",\"domain\":\"http://127.0.0.1:48080\"}', '1', '2025-05-02 11:25:45', '1', '2025-08-18 08:35:14', b'0');
INSERT INTO `infra_file_config` VALUES (9, 'SFTP 存储（示例）', 12, '', b'0', '{\"@class\":\"cn.fanstars.module.infra.framework.file.core.client.sftp.SftpFileClientConfig\",\"basePath\":\"/upload\",\"domain\":\"http://127.0.0.1:48080\",\"host\":\"127.0.0.1\",\"port\":2222,\"username\":\"foo\",\"password\":\"pass\"}', '1', '2025-05-02 16:34:10', '1', '2025-08-18 08:35:14', b'0');
INSERT INTO `infra_file_config` VALUES (10, '七牛云存储【私有】（示例）', 20, '请换成你自己的密钥！！！', b'0', '{\"@class\":\"cn.fanstars.module.infra.framework.file.core.client.s3.S3FileClientConfig\",\"endpoint\":\"s3.cn-south-1.qiniucs.com\",\"domain\":\"http://t151glocd.hn-bkt.clouddn.com\",\"bucket\":\"ruoyi-vue-pro-private\",\"accessKey\":\"3TvrJ70gl2Gt6IBe7_IZT1F6i_k0iMuRtyEv4EyS\",\"accessSecret\":\"wd0tbVBYlp0S-ihA8Qg2hPLncoP83wyrIq24OZuY\",\"enablePathStyleAccess\":false,\"enablePublicAccess\":false}', '1', '2025-08-17 21:22:00', '1', '2025-08-18 08:35:55', b'0');

-- ----------------------------
-- Table structure for infra_file_content
-- ----------------------------
DROP TABLE IF EXISTS `infra_file_content`;
CREATE TABLE `infra_file_content`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `config_id` bigint NOT NULL COMMENT '配置编号',
  `path` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件路径',
  `content` mediumblob NOT NULL COMMENT '文件内容',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of infra_file_content
-- ----------------------------

-- ----------------------------
-- Table structure for infra_job
-- ----------------------------
DROP TABLE IF EXISTS `infra_job`;
CREATE TABLE `infra_job`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务编号',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务名称',
  `status` tinyint NOT NULL COMMENT '任务状态',
  `handler_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '处理器的名字',
  `handler_param` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '处理器的参数',
  `cron_expression` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'CRON 表达式',
  `retry_count` int NOT NULL DEFAULT 0 COMMENT '重试次数',
  `retry_interval` int NOT NULL DEFAULT 0 COMMENT '重试间隔',
  `monitor_timeout` int NOT NULL DEFAULT 0 COMMENT '监控超时时间',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 101 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '定时任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of infra_job
-- ----------------------------
INSERT INTO `infra_job` (`id`, `name`, `status`, `handler_name`, `handler_param`, `cron_expression`, `retry_count`, `retry_interval`, `monitor_timeout`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (1, '访问日志清理 Job', 2, 'accessLogCleanJob', '', '0 0 0 * * ?', 3, 0, 0, '1', '2023-10-03 10:59:41', '1', '2025-08-23 17:04:56', b'0');
INSERT INTO `infra_job` (`id`, `name`, `status`, `handler_name`, `handler_param`, `cron_expression`, `retry_count`, `retry_interval`, `monitor_timeout`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (2, '错误日志清理 Job', 2, 'errorLogCleanJob', '', '0 0 0 * * ?', 3, 0, 0, '1', '2023-10-03 11:00:43', '1', '2025-08-23 17:04:56', b'0');
INSERT INTO `infra_job` (`id`, `name`, `status`, `handler_name`, `handler_param`, `cron_expression`, `retry_count`, `retry_interval`, `monitor_timeout`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3, '任务日志清理 Job', 2, 'jobLogCleanJob', '', '0 0 0 * * ?', 3, 0, 0, '1', '2023-10-03 11:01:33', '1', '2025-08-23 17:04:56', b'0');

-- ----------------------------
-- Table structure for infra_job_log
-- ----------------------------
DROP TABLE IF EXISTS `infra_job_log`;
CREATE TABLE `infra_job_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志编号',
  `job_id` bigint NOT NULL COMMENT '任务编号',
  `handler_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '处理器的名字',
  `handler_param` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '处理器的参数',
  `execute_index` tinyint NOT NULL DEFAULT 1 COMMENT '第几次执行',
  `begin_time` datetime NOT NULL COMMENT '开始执行时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束执行时间',
  `duration` int NULL DEFAULT NULL COMMENT '执行时长',
  `status` tinyint NOT NULL COMMENT '任务状态',
  `result` varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '结果数据',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '定时任务日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of infra_job_log
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
