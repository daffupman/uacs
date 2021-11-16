/*
 Navicat Premium Data Transfer

 Source Server         : mysql30
 Source Server Type    : MySQL
 Source Server Version : 80020
 Source Host           : 192.168.65.30:3306
 Source Schema         : uacs

 Target Server Type    : MySQL
 Target Server Version : 80020
 File Encoding         : 65001

 Date: 07/08/2021 17:15:20
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for app_info
-- ----------------------------
DROP TABLE IF EXISTS `app_info`;
CREATE TABLE `app_info`  (
  `id` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '表主键',
  `app_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '客户端名称',
  `app_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '客户端编号',
  `app_secret` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '客户端密钥',
  `grant_type` char(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '授权模式（第几位-授权模式）：1 - authorization_code, 2 - implicit, 3 - password, 4 - client_credentials',
  `scope` char(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限范围：增、删、改、查',
  `redirect_url` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '重定向地址',
  `description` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `status` tinyint(0) NOT NULL COMMENT '状态：-1 待审核，0 禁用，1，启用，2 已拒绝',
  `create_by` bigint(0) NOT NULL COMMENT '创建人sso_id',
  `update_by` bigint(0) NOT NULL COMMENT '修改人sso_id',
  `create_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '第三方客户端信息表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of app_info
-- ----------------------------
INSERT INTO `app_info` VALUES ('f46ce1247802f2e6d6ee2c38e2914ef8', 'uacs', 'uacs', 'uacs12345', '1111', '1111', 'https://www.baidu.com', NULL, 1, 50588864271749120, 50588864271749120, '2021-05-25 21:09:55', '2021-05-25 21:21:07');

-- ----------------------------
-- Table structure for domain
-- ----------------------------
DROP TABLE IF EXISTS `domain`;
CREATE TABLE `domain`  (
  `id` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '域名',
  `desc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '描述',
  `detail` json NULL COMMENT '详情信息，可拓展',
  `deleted` tinyint(0) NOT NULL COMMENT '是否删除。0：否，1：是',
  `create_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `create_by` bigint(0) NOT NULL COMMENT '创建人',
  `update_by` bigint(0) NOT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_name`(`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '域' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of domain
-- ----------------------------

-- ----------------------------
-- Table structure for hierarchy
-- ----------------------------
DROP TABLE IF EXISTS `hierarchy`;
CREATE TABLE `hierarchy`  (
  `id` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '层级名称',
  `desc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '描述',
  `seq` int(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '对于同一层级可通过seq自定义顺序',
  `parent_id` int(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '父层级id',
  `detail` json NULL COMMENT '详细信息，可拓展',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除。0：否，1：是',
  `domain_id` char(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'domain表的主键',
  `create_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `create_by` bigint(0) NOT NULL COMMENT '创建人',
  `update_by` bigint(0) NOT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_name`(`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '层级表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hierarchy
-- ----------------------------

-- ----------------------------
-- Table structure for operation_log
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log`  (
  `id` bigint(0) UNSIGNED NOT NULL COMMENT '主键',
  `biz_id` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '业务id',
  `table` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作的表名称',
  `operation_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作类型。c：增，u：改，d：删除',
  `values` json NOT NULL COMMENT '操作前后值的变化',
  `desc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '描述',
  `undo` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否已撤销。0：否，1：是',
  `create_by` bigint(0) UNSIGNED NOT NULL COMMENT '创建人',
  `update_by` bigint(0) NOT NULL COMMENT '修改人',
  `create_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_at` datetime(0) NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_biz`(`biz_id`) USING BTREE,
  INDEX `idx_table`(`table`) USING BTREE,
  INDEX `idx_operation`(`operation_type`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of operation_log
-- ----------------------------

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission`  (
  `id` char(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限标识',
  `desc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限描述',
  `type` tinyint(0) NOT NULL DEFAULT 0 COMMENT '权限类型。0：功能权限，1：数据权限',
  `state` tinyint(0) NOT NULL DEFAULT 1 COMMENT '状态。-1：删除，0：禁用，1：启用',
  `detail` json NULL COMMENT '详情信息，可拓展',
  `create_by` bigint(0) NOT NULL COMMENT '创建人',
  `update_by` bigint(0) NOT NULL COMMENT '修改人',
  `create_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_code`(`code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of permission
-- ----------------------------

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `id` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `desc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '角色描述',
  `type` tinyint(0) NOT NULL DEFAULT 1 COMMENT '角色类型。0：管理员，1：普通角色',
  `detail` json NULL COMMENT '详情信息，可拓展',
  `create_by` bigint(0) NOT NULL COMMENT '创建人',
  `update_by` bigint(0) NOT NULL COMMENT '修改人',
  `create_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_name`(`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES ('3562ac8701044ea1beccca923b7b0f65', '普通用户', '使用系统的普通', 1, NULL, 100000000, 100000000, '2021-05-19 22:24:23', '2021-05-19 22:25:06');
INSERT INTO `role` VALUES ('8d924baf0d314b4ab14c6db44cccafbd', '测试用户', '测试', 1, NULL, 100000000, 100000000, '2021-05-20 20:25:27', '2021-05-20 20:25:27');
INSERT INTO `role` VALUES ('e77be2184f554becb5fbc03836c59a10', '超级管理员', '系统的最高权限拥有者', 0, NULL, 100000000, 100000000, '2021-05-19 20:22:32', '2021-05-19 20:22:32');

-- ----------------------------
-- Table structure for role_permission_mapping
-- ----------------------------
DROP TABLE IF EXISTS `role_permission_mapping`;
CREATE TABLE `role_permission_mapping`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_id` char(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'role表的主键',
  `permission_id` char(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'permission表的主键',
  `create_by` bigint(0) NOT NULL COMMENT '创建人',
  `create_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_id_permission_id`(`role_id`, `permission_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色权限关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role_permission_mapping
-- ----------------------------

-- ----------------------------
-- Table structure for user_role_mapping
-- ----------------------------
DROP TABLE IF EXISTS `user_role_mapping`;
CREATE TABLE `user_role_mapping`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(0) UNSIGNED NOT NULL COMMENT 'user_things表主键',
  `role_id` char(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'role表主键',
  `create_by` bigint(0) UNSIGNED NOT NULL COMMENT '创建人',
  `create_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id_role_id`(`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_role_mapping
-- ----------------------------
INSERT INTO `user_role_mapping` VALUES (1, 50588864271749120, 'e77be2184f554becb5fbc03836c59a10', 50588864271749120, '2021-05-26 22:03:07');

-- ----------------------------
-- Table structure for user_things
-- ----------------------------
DROP TABLE IF EXISTS `user_things`;
CREATE TABLE `user_things`  (
  `id` bigint(0) NOT NULL COMMENT '主键',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名称',
  `nick_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '用户昵称',
  `mobile_phone_no` char(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '邮箱',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '密码',
  `salt` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '盐',
  `avatar_url` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '用户头像url',
  `hierarchy_id` char(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户所属层级',
  `top_hierarchy` char(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户所属顶层层级',
  `status` tinyint(0) NOT NULL DEFAULT 1 COMMENT '状态。-1：删除，0：禁用，1：正常，2：锁定，3：过期',
  `mobile_phone_no_verified` bit(1) NOT NULL DEFAULT b'1' COMMENT '手机号是否认证',
  `email_verified` bit(1) NOT NULL DEFAULT b'1' COMMENT '邮箱是否认证',
  `detail` json NULL COMMENT '详细信息，可拓展',
  `create_by` bigint(0) NOT NULL COMMENT '创建人',
  `update_by` bigint(0) NOT NULL COMMENT '修改人',
  `create_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_name`(`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_things
-- ----------------------------
INSERT INTO `user_things` VALUES (50588864271749120, 'zhengjin.wang', 'daff', '18761699090', 'wzjin85@foxmail.com', 'kYSrq5t8vxxNftCPF+za+92oOq0BB0gmDwo2QP8Ueoqy/eAUyubtpyJwdBDK3KlThd2t9WXCxliKAYPvolFPvA==', '$2a$12$SPl3o29R3FEKh7HXOTUGae', '', 'h001', 'h001', 1, b'1', b'1', NULL, 100000000, 100000000, '2021-05-20 22:22:17', '2021-05-23 06:33:15');

SET FOREIGN_KEY_CHECKS = 1;
