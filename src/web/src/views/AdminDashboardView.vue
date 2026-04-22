<template>
  <div class="page-shell stack">
    <section class="hero-card">
      <h1 class="page-title">后台运营看板</h1>
      <p class="page-subtitle">作为一期后台入口，后续从这里继续扩展审核、商品、订单、公告和权限模块。</p>
    </section>

    <section class="grid-3">
      <article class="metric">
        <h3>待审核回收单</h3>
        <strong>14</strong>
      </article>
      <article class="metric">
        <h3>进行中租赁单</h3>
        <strong>23</strong>
      </article>
      <article class="metric">
        <h3>今日新增用户</h3>
        <strong>9</strong>
      </article>
    </section>

    <section class="grid-2">
      <article class="panel-card">
        <h2 class="section-title">待开发后台菜单</h2>
        <el-menu default-active="1">
          <el-menu-item index="1">用户管理</el-menu-item>
          <el-menu-item index="2">资源号管理</el-menu-item>
          <el-menu-item index="3">回收订单管理</el-menu-item>
          <el-menu-item index="4">租赁订单管理</el-menu-item>
          <el-menu-item index="5">公告管理</el-menu-item>
          <el-menu-item index="6">价格规则管理</el-menu-item>
        </el-menu>
      </article>
      <article class="panel-card">
        <h2 class="section-title">当前工程目标</h2>
        <p class="page-subtitle">
          先把登录认证、RBAC、订单状态流转和后台 CRUD 做扎实，再逐步加入消息通知、统计分析和售后处理。
        </p>
      </article>
    </section>
  </div>
</template>
