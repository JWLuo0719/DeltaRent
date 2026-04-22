<template>
  <div class="page-shell stack">
    <section class="hero-card">
      <el-space direction="vertical" :size="20" fill>
        <div>
          <p class="page-title">三角洲哈夫币回收与资源号租赁系统</p>
          <p class="page-subtitle">
            一期目标聚焦“前台展示 + 订单闭环 + 后台运营”，兼顾课程交付和后续商用扩展。
          </p>
        </div>
        <div class="toolbar">
          <el-button type="primary" size="large" @click="$router.push('/recycle')">提交回收申请</el-button>
          <el-button size="large" @click="$router.push('/rentals')">查看资源号</el-button>
          <el-button size="large" @click="$router.push('/admin')">后台预览</el-button>
        </div>
      </el-space>
    </section>

    <section class="grid-3">
      <article class="metric">
        <h3>今日回收申请</h3>
        <strong>28</strong>
      </article>
      <article class="metric">
        <h3>可租资源号</h3>
        <strong>116</strong>
      </article>
      <article class="metric">
        <h3>订单完成率</h3>
        <strong>96.2%</strong>
      </article>
    </section>

    <section class="grid-2">
      <article class="panel-card">
        <h2 class="section-title">一期核心模块</h2>
        <el-tag v-for="item in modules" :key="item" class="tag-item" effect="plain">{{ item }}</el-tag>
      </article>
      <article class="panel-card">
        <h2 class="section-title">技术骨架</h2>
        <p class="page-subtitle">
          前端采用 Vue 3，后端采用 Spring Boot，当前保留 RBAC、订单状态流转、日志审计和异步事件接口。
        </p>
      </article>
    </section>
  </div>
</template>

<script setup lang="ts">
const modules = [
  '用户认证',
  '哈夫币回收',
  '资源号租赁',
  '订单中心',
  '售后申诉',
  '后台审核',
  '公告管理',
  '数据看板'
];
</script>

<style scoped>
.tag-item {
  margin-right: 10px;
  margin-bottom: 10px;
}
</style>
