USE `deltarent`;

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

CALL add_column_if_not_exists('rental_product', 'ratio_text', 'VARCHAR(50) DEFAULT NULL');
CALL add_column_if_not_exists('rental_product', 'insurance_box_text', 'VARCHAR(50) DEFAULT NULL');
CALL add_column_if_not_exists('rental_product', 'stamina_text', 'VARCHAR(50) DEFAULT NULL');
CALL add_column_if_not_exists('rental_product', 'weight_text', 'VARCHAR(50) DEFAULT NULL');
CALL add_column_if_not_exists('rental_product', 'rank_text', 'VARCHAR(50) DEFAULT NULL');
CALL add_column_if_not_exists('rental_product', 'login_region', 'VARCHAR(50) DEFAULT NULL');
CALL add_column_if_not_exists('rental_product', 'weapon_skin_text', 'VARCHAR(255) DEFAULT NULL');
CALL add_column_if_not_exists('rental_product', 'character_skin_text', 'VARCHAR(255) DEFAULT NULL');
CALL add_column_if_not_exists('rental_product', 'cover_image_url', 'VARCHAR(255) DEFAULT NULL');

DROP PROCEDURE IF EXISTS `add_column_if_not_exists`;
