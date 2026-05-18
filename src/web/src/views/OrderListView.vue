<template>
  <div class="page-shell">
    <div class="page-header-card">
      <h2 class="page-title">我的订单</h2>
    </div>

    <div class="filter-bar">
      <div class="filter-label">
        <svg viewBox="0 0 20 20" fill="none"><path d="M3 5h14M7 10h6M9 15h2" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
        <span>筛选</span>
      </div>
      <el-select v-model="filterStatus" placeholder="订单状态" clearable @change="loadOrders" class="filter-select">
        <el-option label="全部状态" value="" />
        <el-option label="待确认" value="WAITING_CONFIRM" />
        <el-option label="进行中" value="IN_PROGRESS" />
        <el-option label="已完成" value="COMPLETED" />
        <el-option label="已取消" value="CANCELLED" />
        <el-option label="售后中" value="AFTER_SALE" />
      </el-select>
      <span class="filter-count" v-if="filterStatus">
        当前：{{ statusMap[filterStatus] || filterStatus }}
        <button class="clear-btn" @click="filterStatus = ''; loadOrders()">清除</button>
      </span>
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
            <span>租赁时长：{{ order.rentDays }} 天</span>
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
  background: transparent;
  color: #1f2937;
  padding: 24px;
}

.page-header-card {
  background: rgba(255, 255, 255, 0.88);
  border: 1px solid rgba(226, 232, 240, 0.9);
  border-radius: 22px;
  padding: 24px 28px;
  margin-bottom: 16px;
}

.page-title {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #1f2937;
  letter-spacing: 1px;
}

.filter-bar {
  background: rgba(255, 255, 255, 0.84);
  border: 1px solid rgba(226, 232, 240, 0.9);
  border-radius: 20px;
  padding: 14px 20px;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 14px;
  flex-wrap: wrap;
}

.filter-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 600;
  color: #9b5d00;
  flex-shrink: 0;
}

.filter-label svg {
  width: 16px;
  height: 16px;
  color: #c57a00;
}

.filter-select {
  width: 200px;
}

.filter-count {
  font-size: 12px;
  color: #9b5d00;
  display: flex;
  align-items: center;
  gap: 8px;
}

.clear-btn {
  background: rgba(255, 196, 32, 0.12);
  border: 1px solid rgba(255, 196, 32, 0.25);
  color: #c57a00;
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.clear-btn:hover {
  background: rgba(255, 196, 32, 0.25);
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-card {
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid #e5e7eb;
  border-radius: 20px;
  padding: 16px 20px;
  cursor: pointer;
  transition: all 0.2s;
}

.order-card:hover {
  background: #fffdf7;
  border-color: #ffd46b;
  transform: translateY(-2px);
  box-shadow: 0 14px 28px rgba(181, 145, 41, 0.12);
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
.status-IN_PROGRESS { background: rgba(255, 196, 32, 0.15); color: #c57a00; }
.status-COMPLETED { background: rgba(34, 197, 94, 0.15); color: #4ade80; }
.status-CANCELLED { background: rgba(100, 116, 139, 0.15); color: #94a3b8; }
.status-AFTER_SALE { background: rgba(239, 68, 68, 0.15); color: #f87171; }

.order-item {
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
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
  color: #64748b;
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
.empty-title { font-size: 18px; font-weight: 600; color: #1f2937; margin-bottom: 8px; }
.empty-desc { font-size: 14px; color: #64748b; margin-bottom: 20px; }

.empty-btn {
  background: rgba(255, 196, 32, 0.1) !important;
  border: 1px solid rgba(255, 196, 32, 0.25) !important;
  color: #c57a00 !important;
}

.pagination-bar {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

:deep(.el-input__wrapper) {
  background: #fff !important;
  box-shadow: 0 0 0 1px #dbe3ef inset !important;
  border-radius: 10px !important;
}

:deep(.el-input__inner) { color: #1f2937 !important; }

:deep(.el-select__wrapper) {
  background: #fff !important;
  border-radius: 10px !important;
  box-shadow: 0 0 0 1px #e2d5a8 inset !important;
  padding: 8px 12px !important;
}

:deep(.el-select__placeholder) {
  color: #94a3b8 !important;
  font-size: 13px !important;
}

:deep(.el-select__selected-item) {
  font-size: 13px !important;
  color: #5a3c00 !important;
}

:deep(.el-pagination button) { background: rgba(255,255,255,0.05) !important; border-color: rgba(255,196,32,0.15) !important; color: #94a3b8 !important; }
:deep(.el-pagination button:hover) { background: rgba(255,196,32,0.15) !important; color: #c57a00 !important; }
:deep(.el-pager li) { background: rgba(255,255,255,0.05) !important; border-color: rgba(255,196,32,0.15) !important; color: #94a3b8 !important; }
:deep(.el-pager li:hover) { color: #c57a00 !important; }
:deep(.el-pager li.is-active) { background: linear-gradient(135deg, #ffe057 0%, #ffc420 100%) !important; border-color: #ffc420 !important; color: #5a3c00 !important; }
</style>
