# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

DeltaRent 是三角洲行动账号租赁管理系统，前后端分离架构，覆盖用户注册登录、账号浏览、租赁下单、订单管理、后台管理等核心流程。

## 常用命令

### 后端（Spring Boot）

```bash
# 启动后端（默认使用 H2 内存数据库）
cd src/server && .\gradlew.bat bootRun

# 运行测试
cd src/server && .\gradlew.bat test

# 清理构建
cd src/server && .\gradlew.bat clean
```

### 前端（Vue 3 + Vite）

```bash
# 安装依赖
npm --prefix src/web install

# 启动开发服务器（端口 5173）
npm --prefix src/web run dev

# 类型检查
npm --prefix src/web run typecheck

# 生产构建
npm --prefix src/web run build
```

### 数据库初始化

```sql
-- MySQL 中执行
source sql/schema_v2.sql;
```

### 一键启动（Windows）

```bash
.\_start_all.bat
```

## 架构概览

### 后端结构（`src/server/src/main/java/com/jwluo0719/deltatrade/`）

```
├── common/          通用工具（ApiResponse 统一响应、JwtUtil 令牌工具）
├── config/          配置类（SecurityConfig、JwtAuthFilter、CorsConfig）
├── controller/      REST 控制器（Auth、Order、Rental、Admin 等）
├── domain/          实体类（SysUser、RentalOrder、RentalProduct、SysRole 等）
├── mapper/          MyBatis Mapper 接口
├── modules/order/   订单模块枚举（OrderStatus）
├── service/         业务逻辑层
└── DeltaTradeApplication.java  启动类
```

### 前端结构（`src/web/src/`）

```
├── api/             HTTP 请求封装（http.ts 带 JWT 拦截器）
├── constants/       常量定义
├── router/          Vue Router 路由配置（含角色守卫）
├── stores/          Pinia 状态管理（auth.ts 认证状态）
├── views/           页面组件
│   ├── layouts/     布局组件（AppLayout）
│   └── admin/       后台管理页面
└── styles/          样式文件
```

### 认证机制

- **JWT 令牌**：`JwtUtil` 使用 HMAC-SHA256 签名，有效期 24 小时
- **请求拦截**：`JwtAuthFilter`（后端）和 `http.ts` 拦截器（前端）自动处理认证
- **角色体系**：USER（普通用户）、ADMIN（管理员）、CS（客服）
- **密码版本**：token 中包含 `pwdUpdatedAt`，修改密码后旧 token 自动失效

### API 路由规则

- 公开接口：`/api/health`、`/api/auth/**`、`/api/portal/**`
- GET 请求：`/api/rentals`、`/api/notices`（公开）
- 订单接口：需认证，普通用户只能操作自己的订单
- 管理接口：`/api/admin/**`、`/api/dashboard/**` 需 ADMIN 角色
- 前端开发代理：`/api` 前缀自动代理到 `http://localhost:8080`

### 数据库

- **生产**：MySQL 8，连接 `localhost:3306/deltarent`
- **开发/测试**：H2 内存数据库（`bootRun` 默认使用 `h2` profile）
- **MyBatis 配置**：`map-underscore-to-camel-case: true`（下划线自动转驼峰）

## 测试约定

后端测试使用纯 JUnit 5 + InMemory Mapper（无 Spring 上下文），测试文件位于：

```
src/server/src/test/java/com/jwluo0719/deltatrade/service/OrderServiceTest.java
```

测试覆盖：订单金额计算、不可租账号拦截、重复下单拦截、状态流转合法性校验。

## 环境要求

- Node.js 20+
- JDK 21
- MySQL 8（如使用 H2 则无需）
- Windows PowerShell 或 Git Bash

## 演示账号（密码均为 `123456`）

| 角色 | 手机号 |
|------|--------|
| 管理员 | 13800000000 |
| 普通用户 | 13900000000 |
| 客服 | 13700000000 |
