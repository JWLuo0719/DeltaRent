-- ================================================================
-- DeltaRent 数据库 schema v2
-- 合并 migration_v2 / migration_v3，并系统性优化字段设计
-- 适用于全新部署，执行此文件即可完成建库建表+初始数据
-- ================================================================

CREATE DATABASE IF NOT EXISTS `deltarent` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `deltarent`;

-- ----------------------------
-- 用户表
-- ----------------------------
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

-- ----------------------------
-- 角色表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `role_code` VARCHAR(50) NOT NULL,
  `role_name` VARCHAR(50) NOT NULL,
  `description` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- 用户-角色关联表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_user_role` (
  `user_id` BIGINT NOT NULL,
  `role_id` BIGINT NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`),
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- 租赁商品表（三角洲行动账号）
-- ----------------------------
CREATE TABLE IF NOT EXISTS `rental_product` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL COMMENT '账号标题',
  `owner_id` BIGINT DEFAULT NULL COMMENT '发布者用户ID',
  `category` VARCHAR(50) DEFAULT NULL COMMENT '分类：高配冲分/活动收藏/低价体验/资源储备',
  `tag_text` VARCHAR(255) DEFAULT NULL COMMENT '标签，逗号分隔',
  `price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '租金(元)',
  `deposit` DECIMAL(10,2) DEFAULT NULL COMMENT '押金(元)',
  `rental_days` INT DEFAULT NULL COMMENT '租期(天)',
  `coin_amount` BIGINT DEFAULT NULL COMMENT '哈夫币数额，单位：万',
  `login_method` VARCHAR(20) DEFAULT NULL COMMENT '上号方式：QQ扫码/QQ账密/微信扫码',
  `insurance_box` VARCHAR(10) DEFAULT NULL COMMENT '保险格数：2格/4格/6格/9格',
  `stamina_level` TINYINT DEFAULT NULL COMMENT '体力等级：1-7',
  `weight_level` TINYINT DEFAULT NULL COMMENT '负重等级：1-7',
  `rank_text` VARCHAR(20) DEFAULT NULL COMMENT '段位：青铜/白银/黄金/铂金/钻石/黑鹰/三角洲巅峰',
  `kd` DECIMAL(4,2) DEFAULT NULL COMMENT 'KD值，如2.20',
  `diving_level` TINYINT DEFAULT NULL COMMENT '潜水等级：1-3',
  `ratio_text` VARCHAR(20) DEFAULT NULL COMMENT '比例，如1:35',
  `login_region` VARCHAR(50) DEFAULT NULL COMMENT '常用登录地区',
  `trade_time_text` VARCHAR(100) DEFAULT NULL COMMENT '方便交易时间，如00:00-24:00',
  `knife_skin_text` VARCHAR(100) DEFAULT NULL COMMENT '刀皮名称',
  `weapon_skin_text` VARCHAR(255) DEFAULT NULL COMMENT '武器皮肤/额外资源',
  `character_skin_text` VARCHAR(100) DEFAULT NULL COMMENT '干员红皮名称',
  `level` INT DEFAULT NULL COMMENT '游戏等级',
  `helmet_count` INT DEFAULT 0 COMMENT '6级头数量',
  `armor_count` INT DEFAULT 0 COMMENT '6级甲数量',
  `awm_ammo_count` INT DEFAULT 0 COMMENT 'AWM子弹数量',
  `nine_grid_trial_card_count` INT DEFAULT 0 COMMENT '9格体验卡数量',
  `recent_ban_record` VARCHAR(10) DEFAULT NULL COMMENT '近90天有无封禁记录',
  `cover_image_url` VARCHAR(500) DEFAULT NULL COMMENT '封面图URL',
  `warehouse_value_text` VARCHAR(100) DEFAULT NULL COMMENT '仓库价值简述',
  `description` TEXT COMMENT '备注描述',
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/AVAILABLE/RENTED/MAINTENANCE/OFF_SHELF',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_rental_product_status` (`status`),
  KEY `idx_rental_product_category` (`category`),
  KEY `idx_rental_product_owner` (`owner_id`),
  KEY `idx_rental_product_price` (`price`),
  KEY `idx_rental_product_coin` (`coin_amount`),
  CONSTRAINT `fk_rental_product_owner` FOREIGN KEY (`owner_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- 租赁订单表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `rental_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_no` VARCHAR(40) NOT NULL,
  `user_id` BIGINT NOT NULL COMMENT '下单用户',
  `product_id` BIGINT NOT NULL,
  `unit_price` DECIMAL(10,2) NOT NULL COMMENT '下单时租金快照',
  `rent_days` INT NOT NULL COMMENT '租赁天数',
  `order_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '订单总额',
  `deposit_amount` DECIMAL(10,2) DEFAULT NULL COMMENT '押金快照',
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

-- ----------------------------
-- 公告表
-- ----------------------------
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

-- ----------------------------
-- 短信验证码表
-- ----------------------------
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

-- ----------------------------
-- 申诉记录表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `appeal_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_type` VARCHAR(30) NOT NULL,
  `order_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `content` TEXT NOT NULL,
  `reason` VARCHAR(50) DEFAULT NULL COMMENT '售后原因：ACCOUNT_MISMATCH/LOGIN_FAILURE/ACCOUNT_RECLAIMED/SERVICE_QUALITY/OTHER',
  `status` VARCHAR(30) NOT NULL DEFAULT 'PENDING',
  `handler_id` BIGINT DEFAULT NULL,
  `handler_remark` TEXT DEFAULT NULL,
  `refund_amount` DECIMAL(10,2) DEFAULT NULL COMMENT '退款金额',
  `compensation` VARCHAR(255) DEFAULT NULL COMMENT '赔偿/补偿说明',
  `handled_at` DATETIME DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_appeal_record_order` (`order_type`, `order_id`),
  KEY `idx_appeal_record_user_id` (`user_id`),
  KEY `idx_appeal_record_status` (`status`),
  CONSTRAINT `fk_appeal_record_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_appeal_record_handler` FOREIGN KEY (`handler_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- 操作日志表
-- ----------------------------
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

-- ================================================================
-- 初始数据
-- ================================================================

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
VALUES (1, 1), (2, 2), (3, 3)
ON DUPLICATE KEY UPDATE `user_id` = VALUES(`user_id`), `role_id` = VALUES(`role_id`);

INSERT INTO `rental_product`
  (`id`, `name`, `owner_id`, `category`, `tag_text`, `price`, `deposit`, `rental_days`,
   `coin_amount`, `login_method`, `insurance_box`, `stamina_level`, `weight_level`,
   `rank_text`, `kd`, `diving_level`, `ratio_text`, `login_region`, `trade_time_text`,
   `knife_skin_text`, `weapon_skin_text`, `character_skin_text`, `cover_image_url`,
   `level`, `helmet_count`, `armor_count`, `awm_ammo_count`, `nine_grid_trial_card_count`,
   `recent_ban_record`, `warehouse_value_text`, `status`, `description`)
VALUES
  (1001, '黑鹰 暗星+怜悯 4格6体7负', 2, '高配冲分', '高段位,暗星刀皮,怜悯刀皮,蚀金玫瑰,高KD',
   1580.00, 500.00, 30, 18300, 'QQ扫码', '4格', 6, 7,
   '黑鹰', 3.20, 2, '1:35', '江西省', '00:00-24:00',
   '暗星,怜悯', 'AWM子弹117发,六级子弹4组', '蚀金玫瑰,午夜邮差', 'https://picsum.photos/seed/delta-1001/800/800',
   60, 1, 6, 117, 1, '无',
   '1.83亿纯币，黑鹰高KD，双刀皮双红皮，适合冲分。', 'AVAILABLE', '号主在线时间稳定，默认只可使用纯哈夫币，额外仓库物资需群内确认。'),

  (1002, '钻石 赤枭+北极星 9格7体7负', 2, '活动收藏', '稀有外观,活动资源,赤枭刀皮,天际线红皮',
   980.00, 300.00, 14, 3400, 'QQ账密', '9格', 7, 7,
   '钻石', 2.50, 3, '1:32', '福建省', '09:00-23:00',
   '赤枭,北极星', 'AWM子弹81发,咖啡豆12袋', '天际线', 'https://picsum.photos/seed/delta-1002/800/800',
   58, 3, 2, 81, 2, '无',
   '9格体验卡2张，活动收藏资源较多。', 'AVAILABLE', '包含活动收藏资源和常用作战配置，适合体验稀有外观。'),

  (1003, '青铜 黑海新手体验 4格5体6负', 3, '低价体验', '新手试用,低价,黑海刀皮,基础装备',
   280.00, 100.00, 7, 800, '微信扫码', '4格', 5, 6,
   '青铜', 1.10, 1, '1:40', '四川省', '18:00-22:00',
   '黑海', '基础弹药', NULL, 'https://picsum.photos/seed/delta-1003/800/800',
   35, 0, 1, 8, 0, '无',
   '入门资源', 'MAINTENANCE', '当前维护中，适合后续演示维护状态筛选。'),

  (1004, '钻石 龙牙+北极星 9格7体7负', 2, '高配冲分', '高段位,满仓库,龙牙刀皮,北极星刀皮,维什戴尔红皮',
   2200.00, 800.00, 48, 15800, 'QQ扫码', '9格', 7, 7,
   '钻石', 4.10, 3, '1:37', '四川省', '00:00-24:00',
   '龙牙,北极星', 'AWM子弹41发,高级子弹零件20个', '维什戴尔,蚀金玫瑰', 'https://picsum.photos/seed/delta-1004/800/800',
   60, 2, 7, 41, 1, '无',
   '顶级仓库', 'AVAILABLE', '主打高段位冲分，仓库资源充足，适合长时段租赁。'),

  (1005, '白银 周末娱乐 4格5体6负', 2, '低价体验', '娱乐体验,低价,可租',
   199.00, 50.00, 3, 1200, '微信扫码', '4格', 5, 6,
   '白银', 1.30, 1, '1:29', '湖北省', '10:00-22:00',
   '信条', '练习弹药', NULL, 'https://picsum.photos/seed/delta-1005/800/800',
   42, 0, 0, 3, 0, '无',
   '轻量仓库', 'AVAILABLE', '适合短时间体验和课程演示下单流程。'),

  (1006, '黄金 影锋+坠星者双红皮 6格6体6负', 2, '活动收藏', '稀有外观,收藏号,红皮,影锋刀皮,坠星者刀皮',
   680.00, 200.00, 14, 4600, 'QQ扫码', '6格', 6, 6,
   '黄金', 1.80, 2, '1:31', '广东省', '08:00-24:00',
   '影锋,坠星者', '稀有武器皮肤,AWM子弹26发', '蚀金玫瑰,水墨云图', 'https://picsum.photos/seed/delta-1006/800/800',
   55, 1, 2, 26, 1, '无',
   '外观收藏仓库', 'AVAILABLE', '包含多套活动外观和收藏资源，适合展示账号详情。'),

  (1007, '三角洲巅峰 处刑者+龙牙 9格7体7负', 2, '资源储备', '哈夫币充足,高仓库,巅峰段位,处刑者刀皮,龙牙刀皮',
   3500.00, 1000.00, 30, 22000, 'QQ扫码', '9格', 7, 7,
   '三角洲巅峰', 5.20, 3, '1:33', '浙江省', '00:00-24:00',
   '处刑者,龙牙', '高配武器弹药,AWM子弹99发', '午夜邮差,维什戴尔', 'https://picsum.photos/seed/delta-1007/800/800',
   60, 4, 8, 99, 3, '无',
   '资源储备仓库', 'AVAILABLE', '哈夫币储备较高，适合演示资源筛选和价格排序。'),

  (1008, '青铜 入门体验 2格4体5负', 3, '低价体验', '入门体验,低价,新手',
   120.00, 50.00, 3, 500, '微信扫码', '2格', 4, 5,
   '青铜', 0.80, 1, '1:20', '安徽省', '19:00-22:00',
   NULL, '入门武器资源', NULL, 'https://picsum.photos/seed/delta-1008/800/800',
   28, 0, 0, 0, 0, '无',
   '入门仓库', 'AVAILABLE', '低价体验账号，适合游客浏览和快速租赁演示。'),

  (1009, '黄金 电锯客服测试 4格6体6负', 3, '运营测试', '测试账号,订单演示,电锯刀皮,已租出',
   450.00, 150.00, 10, 2000, 'QQ账密', '4格', 6, 6,
   '黄金', 1.50, 2, '1:28', '测试区', '00:00-24:00',
   '电锯', '测试武器皮肤', NULL, 'https://picsum.photos/seed/delta-1009/800/800',
   48, 1, 1, 10, 0, '有',
   '测试仓库', 'RENTED', '用于演示已租出状态和后台状态维护。'),

  (1010, '铂金 赤枭+信条 6格6体7负', 2, '平衡租用', '铂金,赤枭刀皮,信条刀皮,天际线红皮,稳定号',
   760.00, 220.00, 14, 6200, 'QQ扫码', '6格', 6, 7,
   '铂金', 2.20, 2, '1:34', '湖南省', '12:00-24:00',
   '赤枭,信条', 'AWM子弹35发,5级全装包2个', '天际线,水墨云图', 'https://picsum.photos/seed/delta-1010/800/800',
   52, 1, 3, 35, 0, '无',
   '6格平衡账号，外观和资源都够用。', 'AVAILABLE', '适合日常排位、活动任务和中等预算租用。'),

  (1011, '黑鹰 坠星者+暗星 9格7体6负', 2, '红皮收藏', '黑鹰,坠星者刀皮,暗星刀皮,多红皮,9格',
   1880.00, 600.00, 30, 9800, 'QQ账密', '9格', 7, 6,
   '黑鹰', 3.60, 3, '1:36', '江苏省', '00:00-24:00',
   '坠星者,暗星', 'AWM子弹73发,45格红包5个', '蚀金玫瑰,天际线,午夜邮差', 'https://picsum.photos/seed/delta-1011/800/800',
   60, 2, 4, 73, 2, '无',
   '多红皮多刀皮收藏号，9格体验卡2张。', 'AVAILABLE', '主打外观展示和冲分，租前请确认红皮展示需求。'),

  (1012, '钻石 怜悯+处刑者 4格7体7负', 3, '高KD短租', '钻石,怜悯刀皮,处刑者刀皮,高KD,短租',
   1180.00, 350.00, 7, 7200, '微信扫码', '4格', 7, 7,
   '钻石', 4.80, 3, '1:38', '重庆市', '20:00-24:00',
   '怜悯,处刑者', 'AWM子弹60发,六级子弹6组', '维什戴尔', 'https://picsum.photos/seed/delta-1012/800/800',
   59, 1, 6, 60, 0, '无',
   '高KD短租号，7体7负，适合晚间冲分。', 'AVAILABLE', '晚间在线配合较好，额外消耗按群内记录结算。'),

  (1013, '白银 黑海+电锯 4格5体5负', 3, '低价外观', '白银,黑海刀皮,电锯刀皮,低价外观',
   260.00, 80.00, 7, 1800, 'QQ扫码', '4格', 5, 5,
   '白银', 1.20, 1, '1:30', '广西省', '10:00-20:00',
   '黑海,电锯', 'AWM子弹12发', '水墨云图', 'https://picsum.photos/seed/delta-1013/800/800',
   38, 0, 1, 12, 0, '无',
   '低价外观体验号，标题、刀皮和红皮标签一致。', 'AVAILABLE', '适合低预算用户体验筛选、详情和下单流程。')
ON DUPLICATE KEY UPDATE
  `name` = VALUES(`name`),
  `category` = VALUES(`category`),
  `tag_text` = VALUES(`tag_text`),
  `price` = VALUES(`price`),
  `deposit` = VALUES(`deposit`),
  `rental_days` = VALUES(`rental_days`),
  `coin_amount` = VALUES(`coin_amount`),
  `login_method` = VALUES(`login_method`),
  `insurance_box` = VALUES(`insurance_box`),
  `stamina_level` = VALUES(`stamina_level`),
  `weight_level` = VALUES(`weight_level`),
  `rank_text` = VALUES(`rank_text`),
  `kd` = VALUES(`kd`),
  `diving_level` = VALUES(`diving_level`),
  `ratio_text` = VALUES(`ratio_text`),
  `login_region` = VALUES(`login_region`),
  `trade_time_text` = VALUES(`trade_time_text`),
  `knife_skin_text` = VALUES(`knife_skin_text`),
  `weapon_skin_text` = VALUES(`weapon_skin_text`),
  `character_skin_text` = VALUES(`character_skin_text`),
  `level` = VALUES(`level`),
  `helmet_count` = VALUES(`helmet_count`),
  `armor_count` = VALUES(`armor_count`),
  `awm_ammo_count` = VALUES(`awm_ammo_count`),
  `nine_grid_trial_card_count` = VALUES(`nine_grid_trial_card_count`),
  `recent_ban_record` = VALUES(`recent_ban_record`),
  `cover_image_url` = VALUES(`cover_image_url`),
  `warehouse_value_text` = VALUES(`warehouse_value_text`),
  `status` = VALUES(`status`),
  `description` = VALUES(`description`);

INSERT INTO `notice` (`id`, `title`, `content`, `author_id`, `status`)
VALUES
  (1, '租用须知', '下单前请确认账号配置、押金、租金、上号方式和仓库物品使用规则。订单提交后将由客服拉群核验并协助交付。', 1, 1),
  (2, '平台说明', '当前系统为课程实践原型，账号、订单和价格均为测试数据，不接入真实支付和真实账号自动化交付。', 1, 1),
  (3, '售后处理规则', '如租用过程中出现账号异常或描述不符，请在验号时间内保留截图证据并联系客服处理。', 1, 1)
ON DUPLICATE KEY UPDATE
  `title` = VALUES(`title`),
  `content` = VALUES(`content`),
  `author_id` = VALUES(`author_id`),
  `status` = VALUES(`status`);
