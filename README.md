# 三角洲哈夫币回收与资源号租赁管理系统

本仓库用于“信息系统实践”课程项目开发，项目目标是在课程要求范围内完成一个可运行、可演示、可扩展的网站系统原型。

## 仓库结构

```text
docs/       课程文档、设计、测试与说明书
sql/        数据库脚本
src/server/ Spring Boot 后端
src/web/    Vue 3 前端
ui/         原型图与界面资源
scripts/    辅助脚本
项目信息/    课程原始资料与老师给定模板
```

## 当前技术选型

- 前端：Vue 3 + TypeScript + Vite + Pinia + Vue Router
- 后端：Spring Boot 2.7 + Java 8
- 数据库：MySQL 8
- 缓存：Redis

之所以采用 `Spring Boot 2.7 + Java 8`，是因为当前本机 Java 环境为 1.8，先保证课程开发可顺利起步。后续若升级到 JDK 17，可再平滑迁移到 Spring Boot 3。

## 建议开发顺序

1. 先完成数据库设计和接口清单
2. 完成后端用户、商品、订单三个核心模块
3. 完成前台门户、登录页、后台管理壳
4. 再补充统计、售后、消息等增强功能

## Git 说明

本地仓库已按课程协作方式设计目录结构。若后续要绑定 GitHub 远端，建议在 `JWLuo0719` 账号下创建仓库后执行：

```bash
git remote add origin <你的仓库地址>
git branch -M main
git push -u origin main
```
