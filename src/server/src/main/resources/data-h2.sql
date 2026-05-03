MERGE INTO sys_user (id, username, password_hash, nickname, phone, status, password_updated_at)
  KEY (id) VALUES
  (1, 'admin', '$2a$10$2BaCTh/DRdPHcSc8RfNccuMh4ZFHPJTxyNneHgnitGnc3UiwRHH42', '管理员示例用户', '13800000000', 1, CURRENT_TIMESTAMP),
  (2, 'demo_user', '$2a$10$2BaCTh/DRdPHcSc8RfNccuMh4ZFHPJTxyNneHgnitGnc3UiwRHH42', '租赁演示用户', '13900000000', 1, CURRENT_TIMESTAMP);

MERGE INTO sys_role (id, role_code, role_name, description)
  KEY (id) VALUES
  (1, 'ADMIN', '管理员', '拥有系统全部管理权限。'),
  (2, 'USER', '普通用户', '平台注册租赁用户。'),
  (3, 'CS', '客服', '负责订单确认与售后处理。');

MERGE INTO sys_user_role (id, user_id, role_id)
  KEY (id) VALUES
  (1, 1, 1),
  (2, 2, 2);

MERGE INTO rental_product (id, name, category, tag_text, hour_price, coin_amount_text, equipment_level_text, warehouse_value_text, status, description)
  KEY (id) VALUES
  (1001, '高战账号 A01', 'premium', '满配仓库,稀有外观', 28.00, '1200万哈夫币', '六套毕业装', '高价值仓库', 'AVAILABLE', '顶级作战账号，仓库满配，适合高强度对局。'),
  (1002, '活动账号 B02', 'event', '活动道具,进阶配置', 18.00, '340万哈夫币', '进阶套装', '活动资源仓库', 'AVAILABLE', '活动资源丰富，适合体验版本活动内容。'),
  (1003, '新手试用账号 C03', 'trial', '新手试用,基础资源', 8.00, '80万哈夫币', '基础装备', '入门资源仓库', 'MAINTENANCE', '当前账号正在维护中，暂不可租赁。');

MERGE INTO notice (id, title, content, status)
  KEY (id) VALUES
  (1, '平台公告', '当前为课程项目演示环境，所有数据仅用于联调与测试。', 1),
  (2, '租赁须知', '订单提交后会进入待确认状态，请等待客服完成账号交付。', 1);
