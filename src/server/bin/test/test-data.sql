DROP TABLE IF EXISTS operation_log;
DROP TABLE IF EXISTS sms_verify_code;
DROP TABLE IF EXISTS appeal_record;
DROP TABLE IF EXISTS notice;
DROP TABLE IF EXISTS rental_order;
DROP TABLE IF EXISTS rental_product;
DROP TABLE IF EXISTS sys_user_role;
DROP TABLE IF EXISTS sys_role;
DROP TABLE IF EXISTS sys_user;

CREATE TABLE sys_user (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  nickname VARCHAR(50),
  phone VARCHAR(30),
  status TINYINT NOT NULL DEFAULT 1,
  password_updated_at TIMESTAMP NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sys_role (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  role_code VARCHAR(50) NOT NULL UNIQUE,
  role_name VARCHAR(50) NOT NULL,
  description VARCHAR(255)
);

CREATE TABLE sys_user_role (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  CONSTRAINT fk_ur_user_id FOREIGN KEY (user_id) REFERENCES sys_user(id),
  CONSTRAINT fk_ur_role_id FOREIGN KEY (role_id) REFERENCES sys_role(id)
);
CREATE INDEX idx_ur_user_id ON sys_user_role(user_id);
CREATE INDEX idx_ur_role_id ON sys_user_role(role_id);

CREATE TABLE rental_product (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  category VARCHAR(50),
  tag_text VARCHAR(255),
  hour_price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  coin_amount_text VARCHAR(100),
  equipment_level_text VARCHAR(100),
  warehouse_value_text VARCHAR(100),
  status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
  description TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_rp_status ON rental_product(status);

CREATE TABLE rental_order (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_no VARCHAR(40) NOT NULL UNIQUE,
  user_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  rent_hours INT NOT NULL,
  order_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  contact_info VARCHAR(100),
  delivery_note VARCHAR(255),
  status VARCHAR(30) NOT NULL DEFAULT 'WAITING_CONFIRM',
  start_time TIMESTAMP,
  end_time TIMESTAMP,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_ro_user_id FOREIGN KEY (user_id) REFERENCES sys_user(id),
  CONSTRAINT fk_ro_product_id FOREIGN KEY (product_id) REFERENCES rental_product(id)
);
CREATE INDEX idx_ro_user_id ON rental_order(user_id);
CREATE INDEX idx_ro_product_id ON rental_order(product_id);
CREATE INDEX idx_ro_status ON rental_order(status);

CREATE TABLE notice (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(120) NOT NULL,
  content TEXT NOT NULL,
  status TINYINT NOT NULL DEFAULT 1,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE appeal_record (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_type VARCHAR(30) NOT NULL,
  order_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  content TEXT NOT NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_ar_user_id FOREIGN KEY (user_id) REFERENCES sys_user(id)
);
CREATE INDEX idx_ar_user_id ON appeal_record(user_id);
CREATE INDEX idx_ar_status ON appeal_record(status);

CREATE TABLE sms_verify_code (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  phone VARCHAR(20) NOT NULL,
  code VARCHAR(10) NOT NULL,
  type VARCHAR(30) NOT NULL,
  expire_time TIMESTAMP NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  used_at TIMESTAMP NULL
);
CREATE INDEX idx_svc_phone ON sms_verify_code(phone);
CREATE INDEX idx_svc_phone_type ON sms_verify_code(phone, type);

CREATE TABLE operation_log (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  operator_id BIGINT,
  module_name VARCHAR(50) NOT NULL,
  action_name VARCHAR(100) NOT NULL,
  detail TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_ol_operator_id ON operation_log(operator_id);

INSERT INTO sys_user (id, username, password_hash, nickname, phone, status) VALUES
  (1, 'admin', '$2b$10$qNxEoCYs3sc2OV4FYm7uneOSIr7zPZRAPpeE.cMVr7x92vHMJJ2qe', 'Admin Demo User', '13800000000', 1),
  (2, 'demo_user', '$2b$10$qNxEoCYs3sc2OV4FYm7uneOSIr7zPZRAPpeE.cMVr7x92vHMJJ2qe', 'Rental Demo User', '13900000000', 1);

INSERT INTO sys_role (id, role_code, role_name, description) VALUES
  (1, 'ADMIN', 'Administrator', 'System administrator with full access'),
  (2, 'USER', 'Normal User', 'Regular registered user');

INSERT INTO sys_user_role (id, user_id, role_id) VALUES
  (1, 1, 1),
  (2, 2, 2);

INSERT INTO rental_product (id, name, category, tag_text, hour_price, coin_amount_text, equipment_level_text, warehouse_value_text, status, description) VALUES
  (1001, 'High Rank Account A01', 'premium', 'Full warehouse', 18.00, '12,000,000 coins', 'Advanced equipment set', 'High-value warehouse', 'AVAILABLE', 'Demo rental account for the main order flow.'),
  (1002, 'Event Account B02', 'event', 'Rare skins', 18.00, '3,400,000 coins', 'Mid-high equipment', 'Event collection resources', 'AVAILABLE', 'Demo rental account with event resources.'),
  (1003, 'Beginner Trial Account C03', 'trial', 'Beginner trial', 8.00, '800,000 coins', 'Basic equipment', 'Entry resources', 'MAINTENANCE', 'Demo account currently under maintenance.');

INSERT INTO notice (id, title, content, status) VALUES
  (1, 'Demo notice', 'This is a course prototype environment. All data is for testing only.', 1),
  (2, 'Rental rule', 'Orders enter WAITING_CONFIRM after submission and should be handled by admin staff.', 1);
