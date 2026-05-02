MERGE INTO sys_user (id, username, password_hash, nickname, phone, status)
  KEY (id) VALUES
  (1, 'admin', '123456', 'Admin Demo User', '13800000000', 1),
  (2, 'demo_user', '123456', 'Rental Demo User', '13900000000', 1);

MERGE INTO sys_role (id, role_code, role_name)
  KEY (id) VALUES
  (1, 'ADMIN', 'Administrator'),
  (2, 'USER', 'Normal User');

MERGE INTO sys_user_role (id, user_id, role_id)
  KEY (id) VALUES
  (1, 1, 1),
  (2, 2, 2);

MERGE INTO rental_product (id, name, category, tag_text, hour_price, coin_amount_text, equipment_level_text, warehouse_value_text, status, description)
  KEY (id) VALUES
  (1001, 'High Rank Account A01', 'premium', 'Full warehouse', 18.00, '12,000,000 coins', 'Advanced equipment set', 'High-value warehouse', 'AVAILABLE', 'Demo rental account for the main order flow.'),
  (1002, 'Event Account B02', 'event', 'Rare skins', 18.00, '3,400,000 coins', 'Mid-high equipment', 'Event collection resources', 'AVAILABLE', 'Demo rental account with event resources.'),
  (1003, 'Beginner Trial Account C03', 'trial', 'Beginner trial', 8.00, '800,000 coins', 'Basic equipment', 'Entry resources', 'MAINTENANCE', 'Demo account currently under maintenance.');

MERGE INTO notice (id, title, content, status)
  KEY (id) VALUES
  (1, 'Demo notice', 'This is a course prototype environment. All data is for testing only.', 1),
  (2, 'Rental rule', 'Orders enter WAITING_CONFIRM after submission and should be handled by admin staff.', 1);
