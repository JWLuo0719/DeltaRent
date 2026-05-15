CREATE DATABASE IF NOT EXISTS `deltarent` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `deltarent`;

-- 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `phone` VARCHAR(20) NOT NULL,
  `password_hash` VARCHAR(255) NOT NULL,
  `nickname` VARCHAR(50) DEFAULT NULL,
  `status` TINYINT NOT NULL DEFAULT 1,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `password_updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted_at` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_username` (`username`),
  UNIQUE KEY `uk_sys_user_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 角色表
CREATE TABLE IF NOT EXISTS `sys_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `role_code` VARCHAR(50) NOT NULL,
  `role_name` VARCHAR(50) NOT NULL,
  `description` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `sys_user_role` (
  `user_id` BIGINT NOT NULL,
  `role_id` BIGINT NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`),
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 租赁产品表
CREATE TABLE IF NOT EXISTS `rental_product` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `category` VARCHAR(50) DEFAULT NULL,
  `tag_text` VARCHAR(255) DEFAULT NULL,
  `hour_price` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  `coin_amount` BIGINT DEFAULT NULL,
  `equipment_level_text` VARCHAR(100) DEFAULT NULL,
  `warehouse_value_text` VARCHAR(100) DEFAULT NULL,
  `status` VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
  `description` TEXT,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_rental_product_status` (`status`),
  KEY `idx_rental_product_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 租赁订单表
CREATE TABLE IF NOT EXISTS `rental_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_no` VARCHAR(40) NOT NULL,
  `user_id` BIGINT NOT NULL,
  `product_id` BIGINT NOT NULL,
  `unit_price` DECIMAL(10,2) NOT NULL,
  `rent_hours` INT NOT NULL,
  `order_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  `contact_info` VARCHAR(100) DEFAULT NULL,
  `delivery_note` VARCHAR(255) DEFAULT NULL,
  `status` VARCHAR(30) NOT NULL DEFAULT 'WAITING_CONFIRM',
  `start_time` DATETIME DEFAULT NULL,
  `end_time` DATETIME DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_rental_order_order_no` (`order_no`),
  KEY `idx_rental_order_status` (`status`),
  KEY `idx_rental_order_user_id` (`user_id`),
  KEY `idx_rental_order_product_id` (`product_id`),
  CONSTRAINT `fk_rental_order_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_rental_order_product` FOREIGN KEY (`product_id`) REFERENCES `rental_product` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 公告表
CREATE TABLE IF NOT EXISTS `notice` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(120) NOT NULL,
  `content` TEXT NOT NULL,
  `author_id` BIGINT DEFAULT NULL,
  `status` TINYINT NOT NULL DEFAULT 1,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_notice_author` FOREIGN KEY (`author_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 短信验证码表
CREATE TABLE IF NOT EXISTS `sms_verify_code` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `phone` VARCHAR(20) NOT NULL,
  `code` VARCHAR(6) NOT NULL,
  `type` VARCHAR(20) NOT NULL,
  `expire_time` DATETIME NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `used_at` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_sms_verify_code_phone_created` (`phone`, `created_at`),
  KEY `idx_sms_verify_code_phone_type_created` (`phone`, `type`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 申诉记录表
CREATE TABLE IF NOT EXISTS `appeal_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_type` VARCHAR(30) NOT NULL,
  `order_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `content` TEXT NOT NULL,
  `status` VARCHAR(30) NOT NULL DEFAULT 'PENDING',
  `handler_id` BIGINT DEFAULT NULL,
  `handler_remark` TEXT DEFAULT NULL,
  `handled_at` DATETIME DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_appeal_record_order` (`order_type`, `order_id`),
  KEY `idx_appeal_record_user_id` (`user_id`),
  KEY `idx_appeal_record_handler_id` (`handler_id`),
  CONSTRAINT `fk_appeal_record_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_appeal_record_handler` FOREIGN KEY (`handler_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 操作日志表
CREATE TABLE IF NOT EXISTS `operation_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `operator_id` BIGINT DEFAULT NULL,
  `module_name` VARCHAR(50) NOT NULL,
  `action_name` VARCHAR(100) NOT NULL,
  `ip_address` VARCHAR(45) DEFAULT NULL,
  `detail` TEXT,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_operation_log_operator_id` (`operator_id`),
  CONSTRAINT `fk_operation_log_operator` FOREIGN KEY (`operator_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==================== 初始数据 ====================

INSERT INTO `sys_user` (`id`, `username`, `phone`, `password_hash`, `nickname`, `status`)
VALUES
  (1, 'admin', '13800000000', '$2b$12$3rHydvFkXa.nLRBnuknsAOLAEDgJLtepE7zZfbmuUEYCSVMOffQ4C', '平台管理员', 1),
  (2, 'demo_user', '13900000000', '$2b$12$3rHydvFkXa.nLRBnuknsAOLAEDgJLtepE7zZfbmuUEYCSVMOffQ4C', '租赁演示用户', 1),
  (3, 'cs_demo', '13700000000', '$2b$12$3rHydvFkXa.nLRBnuknsAOLAEDgJLtepE7zZfbmuUEYCSVMOffQ4C', '客服演示账号', 1)
ON DUPLICATE KEY UPDATE
  `username` = VALUES(`username`),
  `password_hash` = VALUES(`password_hash`),
  `nickname` = VALUES(`nickname`),
  `status` = VALUES(`status`);

INSERT INTO `sys_role` (`id`, `role_code`, `role_name`, `description`)
VALUES
  (1, 'ADMIN', '管理员', '拥有后台用户、角色、账号、订单和公告管理权限。'),
  (2, 'USER', '普通用户', '可浏览账号、创建订单、查看订单和提交售后申诉。'),
  (3, 'CS', '客服', '负责账号维护、订单确认和售后处理。')
ON DUPLICATE KEY UPDATE
  `role_name` = VALUES(`role_name`),
  `description` = VALUES(`description`);

INSERT INTO `sys_user_role` (`user_id`, `role_id`)
VALUES
  (1, 1),
  (2, 2),
  (3, 3)
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `role_id` = VALUES(`role_id`);

INSERT INTO `rental_product`
  (`id`, `name`, `category`, `tag_text`, `hour_price`, `coin_amount`, `equipment_level_text`, `warehouse_value_text`, `status`, `description`)
VALUES
  (1001, '高战满仓号 A01', '高配冲分', '满仓库,毕业装,高段位', 28.00, 12000000, '六套毕业装', '高价值仓库', 'AVAILABLE', '适合排位冲分和高强度作战，客服确认后交付。'),
  (1002, '活动收藏号 B02', '活动收藏', '稀有外观,活动资源,中高配', 18.00, 3400000, '中高配作战装', '活动收藏资源', 'AVAILABLE', '含活动收藏资源和常用作战配置，适合体验稀有外观。'),
  (1003, '新手体验号 C03', '低价体验', '新手试用,基础装备,低价', 8.00, 800000, '基础装备', '入门资源', 'MAINTENANCE', '当前维护中，适合后续演示维护状态筛选。'),
  (1004, '烽火地带冲分号 D04', '高配冲分', '高段位,满仓库,六套毕业装', 32.00, 15800000, '满配毕业装', '顶级仓库', 'AVAILABLE', '主打高段位冲分，仓库资源充足，适合长时段租赁。'),
  (1005, '周末娱乐号 E05', '低价体验', '娱乐体验,基础装备,可租', 9.90, 1200000, '基础作战装', '轻量仓库', 'AVAILABLE', '适合短时间体验和课程演示下单流程。'),
  (1006, '稀有皮肤收藏号 F06', '活动收藏', '稀有外观,收藏号,活动资源', 22.00, 4600000, '进阶装备', '外观收藏仓库', 'AVAILABLE', '包含多套活动外观和收藏资源，适合展示账号详情。'),
  (1007, '哈夫币储备号 G07', '资源储备', '哈夫币充足,仓库价值高,可租', 25.00, 22000000, '高阶装备', '资源储备仓库', 'AVAILABLE', '哈夫币储备较高，适合演示资源筛选和价格排序。'),
  (1008, '战术入门号 H08', '低价体验', '入门体验,基础装备,低价', 6.00, 500000, '新手基础装', '入门仓库', 'AVAILABLE', '低价体验账号，适合游客浏览和快速租赁演示。'),
  (1009, '客服测试号 I09', '运营测试', '测试账号,订单演示,维护中', 12.00, 2000000, '中阶装备', '测试仓库', 'RENTED', '用于演示已租出状态和后台状态维护。')
ON DUPLICATE KEY UPDATE
  `name` = VALUES(`name`),
  `category` = VALUES(`category`),
  `tag_text` = VALUES(`tag_text`),
  `hour_price` = VALUES(`hour_price`),
  `coin_amount` = VALUES(`coin_amount`),
  `equipment_level_text` = VALUES(`equipment_level_text`),
  `warehouse_value_text` = VALUES(`warehouse_value_text`),
  `status` = VALUES(`status`),
  `description` = VALUES(`description`);

INSERT INTO `notice` (`id`, `title`, `content`, `author_id`, `status`)
VALUES
  (1, '租赁须知', '下单前请确认租赁时长、联系方式和账号资源说明。订单提交后将进入待确认状态，由客服进行核验。', 1, 1),
  (2, '课程演示说明', '当前系统为课程实践原型，账号、订单和价格均为测试数据，不接入真实支付和真实账号自动化交付。', 1, 1),
  (3, '售后处理规则', '如租赁过程中出现账号异常，请在订单详情中提交申诉并附上问题描述，客服将在后台进行处理。', 1, 1)
ON DUPLICATE KEY UPDATE
  `title` = VALUES(`title`),
  `content` = VALUES(`content`),
  `author_id` = VALUES(`author_id`),
  `status` = VALUES(`status`);
