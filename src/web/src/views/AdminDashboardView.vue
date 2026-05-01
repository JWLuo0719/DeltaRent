<template>
  <div class="page-shell stack">
    <section class="hero-card">
      <h1 class="page-title">后台运营看板</h1>
      <p class="page-subtitle">当前已接入后台概览接口，用于联调统计卡片、菜单结构和最近订单列表。</p>
    </section>

    <section class="grid-3">
      <article v-for="item in overview.metrics" :key="item.label" class="metric">
        <h3>{{ item.label }}</h3>
        <strong>{{ item.value }}</strong>
      </article>
    </section>

    <section class="grid-2">
      <article class="panel-card">
        <h2 class="section-title">待开发后台菜单</h2>
        <el-menu default-active="1">
          <el-menu-item v-for="(item, index) in overview.menus" :key="item" :index="String(index + 1)">
            {{ item }}
          </el-menu-item>
        </el-menu>
      </article>
      <article class="panel-card">
        <h2 class="section-title">最近订单</h2>
        <div class="detail-list">
          <div v-for="order in overview.recentOrders" :key="order.orderNo" class="detail-item">
            <strong>{{ order.orderNo }} · {{ order.status }}</strong>
            <span>{{ order.user }} / {{ order.item }}</span>
          </div>
        </div>
      </article>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive } from 'vue';
import { ElMessage } from 'element-plus';
import { getDashboardOverview } from '@/api';

const overview = reactive<DashboardOverview>({
  metrics: [],
  menus: [],
  recentOrders: []
});

async function loadOverview() {
  try {
    const response = await getDashboardOverview();
    if (response.data.success) {
      Object.assign(overview, response.data.data);
      return;
    }

    ElMessage.error(response.data.message || '后台数据加载失败');
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '后台数据加载失败');
  }
}

onMounted(loadOverview);
</script>
