# 三角洲行动账号租赁管理系统

本仓库用于“信息系统实践”课程项目开发，项目目标是在课程要求范围内完成一个可运行、可演示、可扩展的网站系统原型。

## 仓库结构

```text
docs/       课程文档、设计、测试与说明书
sql/        数据库脚本
src/server/ Spring Boot 后端
src/web/    Vue 3 前端
ui/         原型图与界面资源
scripts/    辅助脚本
课程信息/    课程原始资料与老师给定模板
```

## 当前可运行状态

- 前端页面已接入 API 请求
- 已提供本机可直接启动的 Mock API 服务，便于先联调页面与业务流程
- Java 后端骨架仍保留在 `src/server/`，用于后续替换 Mock 服务

## 当前技术选型

- 前端：Vue 3 + TypeScript + Vite + Pinia + Vue Router + Element Plus
- 本机联调：Node.js Mock API
- 后端骨架：Spring Boot 2.7 + Java 8 兼容代码结构
- 数据库设计：MySQL 8
- 缓存设计：Redis

说明：

- 你的项目计划书采用的是 `Java 17 + Spring Boot 3` 设计目标，这个方向没有问题
- 但当前本机环境缺少可编译运行 Spring Boot 3 所需的 JDK / 构建工具，因此仓库先补齐了“Vue 前端 + Mock API”的可联调链路
- 后续一旦补齐 JDK 17 和 Gradle 或 Maven Wrapper，就可以把 Mock API 平滑替换为 Java 后端

## 启动方式

先启动 Mock API：

```bash
npm run dev:mock
```

再启动前端：

```bash
npm run dev:web
```

前端默认地址：

- `http://localhost:5173`

Mock API 默认地址：

- `http://localhost:8080/api`

Windows 下也可以直接运行：

```powershell
.\scripts\start-dev.ps1
```

如果要启动 Java 后端，请先准备好本地 Gradle，再执行：

```powershell
.\scripts\start-java-backend.ps1
```

当前脚本会优先使用：

```text
D:\ProgrammingLanguage\Java\Jdk-21
```

两套联调模式不要同时占用 `8080`：

- 如果你启动了 `npm run dev:mock`，就不要再启动 Java 后端
- 如果你启动了 `npm run dev:server`，前端会直接代理到 Spring Boot

## 建议开发顺序

1. 先完成数据库设计和接口清单
2. 完成后端用户、商品、订单三个核心模块
3. 完成前台门户、登录页、账号列表、下单页、后台管理壳
4. 再补充统计、售后、消息等增强功能

## Git 说明

本地仓库已按课程协作方式设计目录结构。若后续要绑定 GitHub 远端，建议在 `JWLuo0719` 账号下创建仓库后执行：

```bash
git remote add origin <你的仓库地址>
git branch -M main
git push -u origin main
git push -u origin dev
```
