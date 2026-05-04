<template>
  <div class="page-shell">
    <div class="page-header-card">
      <h2 class="page-title">我的订单</h2>
    </div>

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

    <div v-if="loading" class="order-card">
      <el-skeleton :rows="4" animated />
    </div>

    <div v-else-if="orders.length > 0" class="order-list">
      <div v-for="order in pagedOrders" :key="order.orderNo" class="order-card" @click="goDetail(order.orderNo)">
        <div class="order-header">
          <span class="order-no">{{ order.orderNo }}</span>
          <span :class="['status-tag', `status-${order.status}`]">{{ statusText(order.status) }}</span>
        </div>
        <div class="order-body">
          <div class="order-item">{{ order.item }}</div>
          <div class="order-meta">
            <span>租赁时长：{{ order.rentHours }} 小时</span>
            <span>金额：￥{{ formatAmount(order.amount) }}</span>
          </div>
          <div class="order-time">下单时间：{{ order.createdAt }}</div>
        </div>
      </div>
    </div>

    <div v-else class="empty-state">
      <div class="empty-icon">📭</div>
      <div class="empty-title">暂无订单</div>
      <div class="empty-desc">去看看有哪些账号可租吧</div>
      <el-button class="empty-btn" @click="$router.push('/rentals')">浏览账号</el-button>
    </div>

    <div v-if="total > pageSize" class="pagination-bar">
      <el-pagination
        v-model:current-page="page"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        background
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { getMyOrders } from '@/api';
import type { OrderSummary } from '@/types/api';

const router = useRouter();
const loading = ref(true);
const orders = ref<OrderSummary[]>([]);
const filterStatus = ref('');
const page = ref(1);
const pageSize = 10;

const total = computed(() => orders.value.length);
const pagedOrders = computed(() => {
  const start = (page.value - 1) * pageSize;
  return orders.value.slice(start, start + pageSize);
});

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

function formatAmount(amount: number) {
  return Number(amount || 0).toFixed(2);
}

function goDetail(orderNo: string) {
  router.push(`/orders/${orderNo}`);
}

async function loadOrders() {
  loading.value = true;
  try {
    const response = await getMyOrders({ status: filterStatus.value || undefined });
    if (response.data.success) {
      orders.value = response.data.data;
      page.value = 1;
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

<style scoped>
.page-shell {
  min-height: 100vh;
  background-color: #0f1c33;
  background-image: linear-gradient(135deg, #060d1a 0%, #0f1c33 50%, #0a1525 100%);
  color: #e2e8f0;
  padding: 24px;
}

.page-header-card {
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(96, 165, 250, 0.12);
  border-radius: 16px;
  padding: 24px 28px;
  margin-bottom: 16px;
}

.page-title {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #f1f5f9;
  letter-spacing: 1px;
}

.filter-bar {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(96, 165, 250, 0.1);
  border-radius: 16px;
  padding: 14px 20px;
  margin-bottom: 16px;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-card {
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(96, 165, 250, 0.1);
  border-radius: 16px;
  padding: 16px 20px;
  cursor: pointer;
  transition: all 0.2s;
}

.order-card:hover {
  background: rgba(96, 165, 250, 0.06);
  border-color: rgba(96, 165, 250, 0.25);
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.order-no {
  font-size: 13px;
  color: #64748b;
}

.status-tag {
  font-size: 11px;
  padding: 2px 10px;
  border-radius: 20px;
  font-weight: 500;
}

.status-WAITING_CONFIRM { background: rgba(251, 191, 36, 0.15); color: #fbbf24; }
.status-IN_PROGRESS { background: rgba(96, 165, 250, 0.15); color: #60a5fa; }
.status-COMPLETED { background: rgba(34, 197, 94, 0.15); color: #4ade80; }
.status-CANCELLED { background: rgba(100, 116, 139, 0.15); color: #94a3b8; }
.status-AFTER_SALE { background: rgba(239, 68, 68, 0.15); color: #f87171; }

.order-item {
  font-size: 15px;
  font-weight: 600;
  color: #f1f5f9;
  margin-bottom: 8px;
}

.order-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #64748b;
  margin-bottom: 6px;
}

.order-time {
  font-size: 12px;
  color: #475569;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  text-align: center;
}

.empty-icon { font-size: 56px; margin-bottom: 16px; }
.empty-title { font-size: 18px; font-weight: 600; color: #f1f5f9; margin-bottom: 8px; }
.empty-desc { font-size: 14px; color: #64748b; margin-bottom: 20px; }

.empty-btn {
  background: rgba(96, 165, 250, 0.1) !important;
  border: 1px solid rgba(96, 165, 250, 0.25) !important;
  color: #60a5fa !important;
}

.pagination-bar {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

:deep(.el-input__wrapper) {
  background: #1e293b !important;
  border-color: rgba(96, 165, 250, 0.2) !important;
  box-shadow: none !important;
}

:deep(.el-input__inner) { color: #e2e8f0 !important; }
:deep(.el-input__inner::placeholder) { color: #475569 !important; }

:deep(.el-select .el-input__wrapper) {
  background: #1e293b !important;
  border-color: rgba(96, 165, 250, 0.2) !important;
}

:deep(.el-select__wrapper) {
  background: #1e293b !important;
  border-color: rgba(96, 165, 250, 0.2) !important;
  box-shadow: none !important;
}

:deep(.el-select__placeholder) { color: #64748b !important; }
:deep(.el-select-dropdown) {
  background: #1e293b !important;
  border: 1px solid rgba(96, 165, 250, 0.2) !important;
  border-radius: 12px !important;
}

:deep(.el-select-dropdown__item) { color: #e2e8f0 !important; }
:deep(.el-select-dropdown__item:hover) { background: rgba(96, 165, 250, 0.1) !important; }
:deep(.el-select-dropdown__item.is-selected) { color: #60a5fa !important; }

:deep(.el-pagination button) { background: rgba(255,255,255,0.05) !important; border-color: rgba(96,165,250,0.15) !important; color: #94a3b8 !important; }
:deep(.el-pagination button:hover) { background: rgba(96,165,250,0.15) !important; color: #60a5fa !important; }
:deep(.el-pager li) { background: rgba(255,255,255,0.05) !important; border-color: rgba(96,165,250,0.15) !important; color: #94a3b8 !important; }
:deep(.el-pager li:hover) { color: #60a5fa !important; }
:deep(.el-pager li.is-active) { background: #1e40af !important; border-color: #1e40af !important; color: #fff !important; }
</style>
