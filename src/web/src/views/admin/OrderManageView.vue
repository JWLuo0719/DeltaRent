<template>
  <div class="panel-card">
    <h2 class="section-title">订单管理</h2>
    <div class="filter-bar">
      <el-select v-model="filterStatus" placeholder="订单状态" clearable @change="loadOrders">
        <el-option label="全部" value="" />
        <el-option label="待确认" value="WAITING_CONFIRM" />
        <el-option label="进行中" value="IN_PROGRESS" />
        <el-option label="已完成" value="COMPLETED" />
        <el-option label="已取消" value="CANCELLED" />
      </el-select>
    </div>
    <el-table v-loading="loading" :data="orders" stripe>
      <el-table-column prop="orderNo" label="订单号" />
      <el-table-column prop="user" label="用户" />
      <el-table-column prop="item" label="账号" />
      <el-table-column prop="rentHours" label="时长" />
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { getAdminOrders } from '@/api';
import type { OrderSummary } from '@/types/api';

const filterStatus = ref('');
const loading = ref(false);
const orders = ref<OrderSummary[]>([]);

const statusMap: Record<string, string> = {
  WAITING_CONFIRM: '待确认',
  IN_PROGRESS: '进行中',
  COMPLETED: '已完成',
  CANCELLED: '已取消'
};

function statusText(status: string) {
  return statusMap[status] || status;
}

function statusType(status: string) {
  return status === 'WAITING_CONFIRM'
    ? 'warning'
    : status === 'IN_PROGRESS'
      ? 'primary'
      : status === 'CANCELLED'
        ? 'info'
        : 'success';
}

async function loadOrders() {
  loading.value = true;
  try {
    const response = await getAdminOrders({ status: filterStatus.value || undefined });
    if (response.data.success) {
      orders.value = response.data.data;
      return;
    }
    ElMessage.error(response.data.message || '订单加载失败');
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '订单加载失败');
  } finally {
    loading.value = false;
  }
}

onMounted(loadOrders);
</script>
