-- ================================================================
-- DeltaRent 数据库迁移 v2
-- 修复：补充 sys_user 表缺失的 updated_at 和 password_updated_at 列
-- 原因：旧版 schema 创建的表缺少这些列，导致密码重置等功能异常
-- 执行方式：在 MySQL 中执行此文件，或逐条执行
-- ================================================================

USE `deltarent`;

-- 存储过程：安全添加列（列存在则跳过）
DROP PROCEDURE IF EXISTS `add_column_if_not_exists`;

DELIMITER $$
CREATE PROCEDURE `add_column_if_not_exists`(
    IN table_name_param VARCHAR(64),
    IN column_name_param VARCHAR(64),
    IN column_def_param VARCHAR(256)
)
BEGIN
    DECLARE column_count INT DEFAULT 0;

    SELECT COUNT(*) INTO column_count
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = table_name_param
      AND COLUMN_NAME = column_name_param;

    IF column_count = 0 THEN
        SET @sql = CONCAT('ALTER TABLE `', table_name_param, '` ADD COLUMN `', column_name_param, '` ', column_def_param);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$
DELIMITER ;

-- sys_user 表：添加 updated_at
CALL add_column_if_not_exists('sys_user', 'updated_at',
    'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP');

-- sys_user 表：添加 password_updated_at
CALL add_column_if_not_exists('sys_user', 'password_updated_at',
    'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP');

-- rental_order 表：添加 deleted_at（如果缺失）
CALL add_column_if_not_exists('rental_order', 'deleted_at',
    'DATETIME DEFAULT NULL');

-- rental_product 表：添加 deleted_at（如果缺失）
CALL add_column_if_not_exists('rental_product', 'deleted_at',
    'DATETIME DEFAULT NULL');

-- 清理存储过程
DROP PROCEDURE IF EXISTS `add_column_if_not_exists`;

-- 验证：查看 sys_user 表结构
SELECT COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE, COLUMN_DEFAULT
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'sys_user'
ORDER BY ORDINAL_POSITION;
