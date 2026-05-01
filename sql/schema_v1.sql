CREATE DATABASE IF NOT EXISTS `delta_trade` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `delta_trade`;

CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `phone` VARCHAR(20) NOT NULL,
  `password_hash` VARCHAR(255) NOT NULL,
  `nickname` VARCHAR(50) DEFAULT NULL,
  `status` TINYINT NOT NULL DEFAULT 1,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_phone` (`phone`)
);

CREATE TABLE IF NOT EXISTS `sys_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `role_code` VARCHAR(50) NOT NULL,
  `role_name` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_role_code` (`role_code`)
);

CREATE TABLE IF NOT EXISTS `sys_user_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `role_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `rental_product` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `category` VARCHAR(50) DEFAULT NULL,
  `tag_text` VARCHAR(255) DEFAULT NULL,
  `hour_price` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  `coin_amount_text` VARCHAR(100) DEFAULT NULL,
  `equipment_level_text` VARCHAR(100) DEFAULT NULL,
  `warehouse_value_text` VARCHAR(100) DEFAULT NULL,
  `status` VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
  `description` TEXT,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `rental_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_no` VARCHAR(40) NOT NULL,
  `user_id` BIGINT NOT NULL,
  `product_id` BIGINT NOT NULL,
  `rent_hours` INT NOT NULL,
  `order_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  `contact_info` VARCHAR(100) DEFAULT NULL,
  `delivery_note` VARCHAR(255) DEFAULT NULL,
  `status` VARCHAR(30) NOT NULL DEFAULT 'WAITING_CONFIRM',
  `start_time` DATETIME DEFAULT NULL,
  `end_time` DATETIME DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_rental_order_order_no` (`order_no`)
);

CREATE TABLE IF NOT EXISTS `notice` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(120) NOT NULL,
  `content` TEXT NOT NULL,
  `status` TINYINT NOT NULL DEFAULT 1,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `appeal_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_type` VARCHAR(30) NOT NULL,
  `order_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `content` TEXT NOT NULL,
  `status` VARCHAR(30) NOT NULL DEFAULT 'PENDING',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `operation_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `operator_id` BIGINT DEFAULT NULL,
  `module_name` VARCHAR(50) NOT NULL,
  `action_name` VARCHAR(100) NOT NULL,
  `detail` TEXT,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);

INSERT INTO `sys_user` (`id`, `phone`, `password_hash`, `nickname`, `status`)
VALUES
  (1, '13800000000', '$2b$10$XDmeodVhzAZhIq16Pkc32eGSeWJNiCwR2ouqTXWqvAAFFW0cFF0C', 'Admin Demo User', 1),
  (2, '13900000000', '$2b$10$XDmeodVhzAZhIq16Pkc32eGSeWJNiCwR2ouqTXWqvAAFFW0cFF0C', 'Rental Demo User', 1)
ON DUPLICATE KEY UPDATE
  `password_hash` = VALUES(`password_hash`),
  `nickname` = VALUES(`nickname`),
  `status` = VALUES(`status`);

INSERT INTO `sys_role` (`id`, `role_code`, `role_name`)
VALUES
  (1, 'ADMIN', 'Administrator'),
  (2, 'USER', 'Normal User')
ON DUPLICATE KEY UPDATE
  `role_name` = VALUES(`role_name`);

INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`)
VALUES
  (1, 1, 1),
  (2, 2, 2)
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `role_id` = VALUES(`role_id`);

INSERT INTO `rental_product`
  (`id`, `name`, `category`, `tag_text`, `hour_price`, `coin_amount_text`, `equipment_level_text`, `warehouse_value_text`, `status`, `description`)
VALUES
  (1001, 'High Rank Account A01', 'premium', 'Full warehouse', 18.00, '12,000,000 coins', 'Advanced equipment set', 'High-value warehouse', 'AVAILABLE', 'Demo rental account for the main order flow.'),
  (1002, 'Event Account B02', 'event', 'Rare skins', 18.00, '3,400,000 coins', 'Mid-high equipment', 'Event collection resources', 'AVAILABLE', 'Demo rental account with event resources.'),
  (1003, 'Beginner Trial Account C03', 'trial', 'Beginner trial', 8.00, '800,000 coins', 'Basic equipment', 'Entry resources', 'MAINTENANCE', 'Demo account currently under maintenance.')
ON DUPLICATE KEY UPDATE
  `name` = VALUES(`name`),
  `category` = VALUES(`category`),
  `tag_text` = VALUES(`tag_text`),
  `hour_price` = VALUES(`hour_price`),
  `coin_amount_text` = VALUES(`coin_amount_text`),
  `equipment_level_text` = VALUES(`equipment_level_text`),
  `warehouse_value_text` = VALUES(`warehouse_value_text`),
  `status` = VALUES(`status`),
  `description` = VALUES(`description`);

INSERT INTO `notice` (`id`, `title`, `content`, `status`)
VALUES
  (1, 'Demo notice', 'This is a course prototype environment. All data is for testing only.', 1),
  (2, 'Rental rule', 'Orders enter WAITING_CONFIRM after submission and should be handled by admin staff.', 1)
ON DUPLICATE KEY UPDATE
  `title` = VALUES(`title`),
  `content` = VALUES(`content`),
  `status` = VALUES(`status`);
