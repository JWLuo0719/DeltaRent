<template>
  <div class="panel-card">
    <h2 class="section-title">数据看板</h2>
    <div class="stats-grid">
      <div v-for="item in stats.metrics" :key="item.label" class="metric-card">
        <div class="metric-label">{{ item.label }}</div>
        <div class="metric-value">{{ item.value }}</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue';
import { ElMessage } from 'element-plus';
import { getDashboardOverview } from '@/api';

const stats = reactive({
  metrics: [] as { label: string; value: string }[]
});

async function loadStats() {
  try {
    const response = await getDashboardOverview();
    if (response.data.success) {
      stats.metrics = response.data.data.metrics;
    }
  } catch {
    ElMessage.error('数据加载失败');
  }
}

loadStats();
</script>

<style scoped>
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.metric-card {
  background: linear-gradient(135deg, #1e3a5f 0%, #1e40af 100%);
  border: 1px solid rgba(96, 165, 250, 0.2);
  border-radius: 12px;
  padding: 20px;
  color: #fff;
}

.metric-label {
  font-size: 13px;
  color: rgba(255,255,255,0.7);
  margin-bottom: 8px;
}

.metric-value {
  font-size: 28px;
  font-weight: 700;
}
</style>