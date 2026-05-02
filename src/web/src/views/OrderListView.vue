<template>
  <div class="page-container">
    <div class="page-header">
      <h2>我的订单</h2>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-select v-model="filterStatus" placeholder="订单状态" clearable @change="loadOrders">
        <el-option label="全部" value="" />
        <el-option label="待确认" value="WAITING_CONFIRM" />
        <el-option label="进行中" value="IN_PROGRESS" />
        <el-option label="已完成" value="COMPLETED" />
        <el-option label="已取消" value="CANCELLED" />
        <el-option label="售后中" value="APPEAL" />
      </el-select>
    </div>

    <!-- 订单列表 -->
    <div v-if="orders.length > 0" class="order-list">
      <div v-for="order in orders" :key="order.orderNo" class="order-card" @click="goDetail(order.orderNo)">
        <div class="order-header">
          <span class="order-no">{{ order.orderNo }}</span>
          <span :class="['status-tag', `status-${order.status}`]">{{ statusText(order.status) }}</span>
        </div>
        <div class="order-body">
          <div class="order-item">{{ order.item }}</div>
          <div class="order-meta">
            <span>租赁时长：{{ order.rentHours }}小时</span>
            <span>金额：{{ order.amount }}</span>
          </div>
          <div class="order-time">下单时间：{{ order.createdAt }}</div>
        </div>
      </div>
    </div>

    <el-empty v-else description="暂无订单，去看看有哪些账号可租吧" />

    <!-- 分页 -->
    <div v-if="total > 0" class="pagination">
      <el-pagination
        v-model:current-page="page"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="loadOrders"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

interface Order {
  orderNo: string;
  item: string;
  rentHours: number;
  amount: string;
  status: string;
  createdAt: string;
}

const router = useRouter();
const orders = ref<Order[]>([]);
const filterStatus = ref('');
const page = ref(1);
const pageSize = 10;
const total = ref(0);

const statusMap: Record<string, string> = {
  WAITING_CONFIRM: '待确认',
  IN_PROGRESS: '进行中',
  COMPLETED: '已完成',
  CANCELLED: '已取消',
  APPEAL: '售后中'
};

function statusText(status: string) {
  return statusMap[status] || status;
}

function goDetail(orderNo: string) {
  router.push(`/orders/${orderNo}`);
}

function loadOrders() {
  // TODO: 调用后端接口
  // Mock 数据
  orders.value = [
    {
      orderNo: 'DR20260423001',
      item: '高战账号 A01',
      rentHours: 2,
      amount: '¥56.00',
      status: 'WAITING_CONFIRM',
      createdAt: '2026-04-23 14:30:00'
    },
    {
      orderNo: 'DR20260422001',
      item: '活动账号 B02',
      rentHours: 1,
      amount: '¥18.00',
      status: 'COMPLETED',
      createdAt: '2026-04-22 10:00:00'
    }
  ];
  total.value = orders.value.length;
}

onMounted(() => {
  loadOrders();
});
</script>

<style scoped>
.page-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.filter-bar {
  margin-bottom: 16px;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px 20px;
  cursor: pointer;
  transition: box-shadow 0.2s;
}

.order-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.order-no {
  font-size: 14px;
  color: #64748b;
}

.status-tag {
  font-size: 12px;
  padding: 2px 10px;
  border-radius: 20px;
  font-weight: 500;
}

.status-WAITING_CONFIRM { background: #fef3c7; color: #d97706; }
.status-IN_PROGRESS { background: #dbeafe; color: #2563eb; }
.status-COMPLETED { background: #d1fae5; color: #059669; }
.status-CANCELLED { background: #f3f4f6; color: #6b7280; }
.status-APPEAL { background: #fee2e2; color: #dc2626; }

.order-item {
  font-size: 16px;
  font-weight: 600;
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
  color: #94a3b8;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>