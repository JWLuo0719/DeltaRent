# 后端工程说明

当前后端采用：

- Spring Boot 3
- Java 21
- MySQL 8
- Redis
- Spring Security

## 当前状态

- 已具备基础启动类
- 已提供统一返回体
- 已预留健康检查接口：`GET /api/health`
- 已放入跨域配置与基础安全配置
- 已补齐与前端联调一致的占位接口：
  - `POST /api/auth/login`
  - `GET /api/portal/summary`
  - `GET /api/rentals`
  - `GET /api/dashboard/overview`
  - `POST /api/orders`

## 本地运行要求

- JDK 21
- Gradle 8+

当前仓库约定优先使用以下 JDK：

```text
D:\ProgrammingLanguage\Java\Jdk-21
```

当前 `start-java-backend.ps1` 会以 `local` profile 启动：

- 先绕过 MySQL / Redis 自动配置
- 便于在原型阶段直接启动接口联调
- 后续接入数据库后，可切回默认 profile 或新增 `dev` profile

## 后续建议优先级

1. 用户认证与 JWT
2. RBAC 权限模型
3. 账号商品模块
4. 租赁订单模块
5. 公告与售后模块
