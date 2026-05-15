# DeltaRent 三角洲行动账号租赁管理系统

DeltaRent 是一个课程实践用的账号租赁管理系统原型，采用前后端分离架构，覆盖游客浏览、用户注册登录、账号租赁下单、订单中心、个人资料、后台用户/角色/账号/订单/公告管理等核心流程。

## 技术栈

| 模块 | 技术 |
| --- | --- |
| 前端 | Vue 3、TypeScript、Vite、Vue Router、Pinia、Element Plus |
| 后端 | Spring Boot 3、Spring Security、JWT、MyBatis |
| 数据库 | MySQL 8 |
| 构建 | npm、Gradle Wrapper |

## 目录结构

```text
src/web/       前端项目
src/server/    Spring Boot 后端项目
sql/           数据库建表、初始化和迁移脚本
docs/test/     测试文档与测试截图
docs/report/   中期/结项报告材料
课程信息/      课程模板、评分细则和计划书
```

## 环境要求

- Node.js 20 或更高版本
- JDK 21
- MySQL 8
- Windows PowerShell 或兼容终端

后端默认连接本地 MySQL：

```text
数据库：deltarent
地址：localhost:3306
账号：root
密码：123456
```

如需修改数据库配置，编辑：

```text
src/server/src/main/resources/application.yml
```

## 数据库初始化

在 MySQL 中执行：

```sql
source sql/schema_v1.sql;
```

如果是旧库升级，再执行：

```sql
source sql/migration_v2.sql;
```

也可以在 Windows 下使用：

```powershell
.\sql\init.bat
```

## 启动后端

```powershell
cd src/server
.\gradlew.bat bootRun
```

后端默认地址：

```text
http://localhost:8080
```

健康检查：

```text
GET http://localhost:8080/api/health
```

## 启动前端

首次运行先安装依赖：

```powershell
npm --prefix src/web install
```

启动开发服务器：

```powershell
npm --prefix src/web run dev
```

前端默认地址：

```text
http://localhost:5173
```

## 构建与测试

前端生产构建：

```powershell
npm --prefix src/web run build
```

后端业务测试：

```powershell
cd src/server
.\gradlew.bat test
```

当前后端测试覆盖订单创建、重复下单拦截、订单状态流转和账号筛选等核心业务规则。

## 演示账号

初始密码均为：

```text
123456
```

| 角色 | 手机号 | 说明 |
| --- | --- | --- |
| 管理员 | 13800000000 | 可访问后台用户、角色、账号、订单、公告管理 |
| 普通用户 | 13900000000 | 可浏览账号、创建订单、查看订单 |
| 客服 | 13700000000 | 可处理账号与订单相关管理功能 |

## 推荐演示流程

1. 使用普通用户登录。
2. 浏览首页和账号列表。
3. 查看账号详情并提交租赁订单。
4. 进入“我的订单”查看订单详情。
5. 使用管理员或客服账号登录后台。
6. 进入订单管理，筛选订单并执行“确认订单 / 完成订单 / 取消订单”等状态流转。
7. 回到用户端查看订单状态变化。

## 说明

本项目为课程原型系统，不接入真实支付、真实短信网关和真实游戏账号自动化能力。验证码、支付和客服流程以课程演示和内部测试为目标。
