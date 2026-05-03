# DeltaTrade 数据库初始化脚本

## 文件说明

- `schema_v1.sql` - 完整数据库建表脚本（包含初始数据）

## 使用方法

```bash
mysql -u root -p < schema_v1.sql
```

或直接在 MySQL 客户端中执行：

```sql
source schema_v1.sql
```

## 数据库配置

配置位于 `src/server/src/main/resources/application.yml`：

- 数据库名：`delta_trade`
- 用户名：`root`
- 密码：`123456`
- 地址：`localhost:3306`

## 测试账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | 管理员 |
| demo_user | 123456 | 普通用户 |

注意：密码已哈希存储，以上为明文密码仅用于演示环境。