<template>
  <div class="page-shell stack">
    <section class="hero-card">
      <el-space direction="vertical" :size="20" fill>
        <div>
          <p class="page-title">{{ summary.heroTitle }}</p>
          <p class="page-subtitle">{{ summary.heroSubtitle }}</p>
        </div>
        <div class="toolbar">
          <el-button type="primary" size="large" @click="$router.push('/orders/create')">立即下单</el-button>
          <el-button size="large" @click="$router.push('/rentals')">查看账号列表</el-button>
          <el-button size="large" @click="$router.push('/admin')">后台预览</el-button>
        </div>
      </el-space>
    </section>

    <section class="grid-3">
      <article v-for="item in summary.metrics" :key="item.label" class="metric">
        <h3>{{ item.label }}</h3>
        <strong>{{ item.value }}</strong>
      </article>
    </section>

    <section class="grid-2">
      <article class="panel-card">
        <h2 class="section-title">一期核心模块</h2>
        <el-tag v-for="item in summary.modules" :key="item" class="tag-item" effect="plain">{{ item }}</el-tag>
      </article>
      <article class="panel-card">
        <h2 class="section-title">最新公告</h2>
        <div class="notice-list">
          <div v-for="notice in summary.notices" :key="notice.id" class="notice-item">
            <strong>{{ notice.title }}</strong>
            <span>{{ notice.content }}</span>
          </div>
        </div>
      </article>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive } from 'vue';
import { ElMessage } from 'element-plus';
import { getPortalSummary } from '@/api';

const summary = reactive<PortalSummary>({
  heroTitle: '三角洲行动账号租赁管理系统',
  heroSubtitle: '正在加载首页摘要...',
  metrics: [],
  modules: [],
  notices: []
});

async function loadSummary() {
  try {
    const response = await getPortalSummary();
    if (response.data.success) {
      Object.assign(summary, response.data.data);
      return;
    }

    ElMessage.error(response.data.message || '首页数据加载失败');
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '首页数据加载失败');
  }
}

onMounted(loadSummary);
</script>

<style scoped>
.tag-item {
  margin-right: 10px;
  margin-bottom: 10px;
}
</style>
