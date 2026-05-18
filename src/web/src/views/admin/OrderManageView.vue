<template>
  <div class="admin-card">
    <h2 class="section-title">订单管理</h2>
    <div class="filter-bar">
      <el-select v-model="filterStatus" placeholder="订单状态" clearable @change="loadOrders">
        <el-option label="全部" value="" />
        <el-option label="待确认" value="WAITING_CONFIRM" />
        <el-option label="进行中" value="IN_PROGRESS" />
        <el-option label="已完成" value="COMPLETED" />
        <el-option label="已取消" value="CANCELLED" />
        <el-option label="售后中" value="AFTER_SALE" />
      </el-select>
    </div>
    <el-table v-loading="loading" :data="orders">
      <el-table-column prop="orderNo" label="订单号" />
      <el-table-column label="用户">
        <template #default="{ row }">{{ row.buyer || row.user || '-' }}</template>
      </el-table-column>
      <el-table-column prop="item" label="账号" />
      <el-table-column prop="rentDays" label="天数" />
      <el-table-column prop="amount" label="金额">
        <template #default="{ row }">¥{{ Number(row.amount || 0).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="260" fixed="right">
        <template #default="{ row }">
          <div class="action-group">
            <el-button
              v-for="action in availableActions(row.status)"
              :key="action.status"
              size="small"
              :type="action.type"
              :loading="updatingId === row.id"
              @click="handleStatusChange(row, action.status)"
            >
              {{ action.label }}
            </el-button>
            <span v-if="availableActions(row.status).length === 0" class="muted-text">无可用操作</span>
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getAdminOrders, updateOrderStatus } from '@/api';
import type { OrderSummary } from '@/types/api';

const filterStatus = ref('');
const loading = ref(true);
const updatingId = ref<number | null>(null);
const orders = ref<OrderSummary[]>([]);

const statusMap: Record<string, string> = {
  WAITING_CONFIRM: '待确认',
  IN_PROGRESS: '进行中',
  COMPLETED: '已完成',
  CANCELLED: '已取消',
  AFTER_SALE: '售后中'
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

function availableActions(status: string) {
  const map: Record<string, Array<{ label: string; status: string; type: 'primary' | 'success' | 'warning' | 'danger' }>> = {
    WAITING_CONFIRM: [
      { label: '确认订单', status: 'IN_PROGRESS', type: 'primary' },
      { label: '取消订单', status: 'CANCELLED', type: 'danger' }
    ],
    IN_PROGRESS: [
      { label: '完成订单', status: 'COMPLETED', type: 'success' },
      { label: '取消订单', status: 'CANCELLED', type: 'danger' }
    ],
    COMPLETED: [
      { label: '转售后', status: 'AFTER_SALE', type: 'warning' }
    ]
  };
  return map[status] || [];
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

async function handleStatusChange(order: OrderSummary, nextStatus: string) {
  try {
    await ElMessageBox.confirm(
      `确认将订单 ${order.orderNo} 从“${statusText(order.status)}”更新为“${statusText(nextStatus)}”？`,
      '订单状态变更',
      { type: 'warning', confirmButtonText: '确认', cancelButtonText: '取消' }
    );
    updatingId.value = order.id;
    const response = await updateOrderStatus(order.id, nextStatus);
    if (!response.data.success) {
      ElMessage.error(response.data.message || '状态更新失败');
      return;
    }
    ElMessage.success('订单状态已更新');
    await loadOrders();
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error instanceof Error ? error.message : '状态更新失败');
    }
  } finally {
    updatingId.value = null;
  }
}

onMounted(loadOrders);
</script>

<style scoped>
.admin-card {
  background: rgba(255, 255, 255, 0.88);
  border: 1px solid rgba(226, 232, 240, 0.9);
  border-radius: 22px;
  padding: 24px;
  box-shadow: 0 16px 36px rgba(15, 23, 42, 0.06);
}

:deep(.el-input__wrapper) {
  background: #fff !important;
  box-shadow: 0 0 0 1px #dbe3ef inset !important;
}
:deep(.el-input__inner) { color: #1f2937 !important; }
:deep(.el-table) {
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: #fff;
  --el-table-header-bg-color: #f8fafc;
  --el-table-row-hover-bg-color: #fff7e6;
  --el-table-border-color: #e2e8f0;
  --el-table-text-color: #1f2937;
  --el-table-header-text-color: #64748b;
}
:deep(.el-table__header th) {
  background: #f8fafc !important;
}
:deep(.el-table__body tr:hover > td) {
  background: #fff7e6 !important;
}
:deep(.el-table td.el-table__cell) {
  border-bottom-color: #e2e8f0 !important;
}

.action-group {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.muted-text {
  color: #64748b;
  font-size: 13px;
}
</style>
