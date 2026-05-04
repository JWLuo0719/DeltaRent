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
  (1, 'admin', '13800000000', '$2b$12$3rHydvFkXa.nLRBnuknsAOLAEDgJLtepE7zZfbmuUEYCSVMOffQ4C', 'Admin Demo User', 1),
  (2, 'demo_user', '13900000000', '$2b$12$3rHydvFkXa.nLRBnuknsAOLAEDgJLtepE7zZfbmuUEYCSVMOffQ4C', 'Rental Demo User', 1),
  (3, 'cs_demo', '13700000000', '$2b$12$3rHydvFkXa.nLRBnuknsAOLAEDgJLtepE7zZfbmuUEYCSVMOffQ4C', 'Customer Service Demo', 1)
ON DUPLICATE KEY UPDATE
  `username` = VALUES(`username`),
  `password_hash` = VALUES(`password_hash`),
  `nickname` = VALUES(`nickname`),
  `status` = VALUES(`status`);

INSERT INTO `sys_role` (`id`, `role_code`, `role_name`, `description`)
VALUES
  (1, 'ADMIN', 'Administrator', 'System administrator with full access.'),
  (2, 'USER', 'Normal User', 'Registered rental user.'),
  (3, 'CS', 'Customer Service', 'Customer service staff for order and after-sales handling.')
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
  (1001, 'High Rank Account A01', 'premium', 'Full warehouse', 18.00, 12000000, 'Advanced equipment set', 'High-value warehouse', 'AVAILABLE', 'Demo rental account for the main order flow.'),
  (1002, 'Event Account B02', 'event', 'Rare skins', 18.00, 3400000, 'Mid-high equipment', 'Event collection resources', 'AVAILABLE', 'Demo rental account with event resources.'),
  (1003, 'Beginner Trial Account C03', 'trial', 'Beginner trial', 8.00, 800000, 'Basic equipment', 'Entry resources', 'MAINTENANCE', 'Demo account currently under maintenance.')
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
  (1, 'Demo notice', 'This is a course prototype environment. All data is for testing only.', 1, 1),
  (2, 'Rental rule', 'Orders enter WAITING_CONFIRM after submission and should be handled by admin staff.', 1, 1)
ON DUPLICATE KEY UPDATE
  `title` = VALUES(`title`),
  `content` = VALUES(`content`),
  `author_id` = VALUES(`author_id`),
  `status` = VALUES(`status`);
