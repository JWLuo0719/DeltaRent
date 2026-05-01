<template>
  <div class="page-container">
    <el-button text @click="$router.back()">← 返回</el-button>

    <div v-if="order" class="order-detail">
      <div class="detail-header">
        <h2>{{ order.item }}</h2>
        <span :class="['status-tag', `status-${order.status}`]">{{ statusText(order.status) }}</span>
      </div>

      <el-descriptions :column="2" border class="detail-table">
        <el-descriptions-item label="订单号">{{ order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ order.createdAt }}</el-descriptions-item>
        <el-descriptions-item label="租赁时长">{{ order.rentHours }} 小时</el-descriptions-item>
        <el-descriptions-item label="订单金额">{{ order.amount }}</el-descriptions-item>
        <el-descriptions-item label="账号" :span="2">{{ order.item }}</el-descriptions-item>
        <el-descriptions-item label="联系方式">{{ order.contactInfo }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ order.remark || '无' }}</el-descriptions-item>
      </el-descriptions>

      <!-- 订单状态时间线 -->
      <div class="timeline-section">
        <h3>订单进度</h3>
        <el-timeline>
          <el-timeline-item
            v-for="(event, index) in order.events"
            :key="index"
            :timestamp="event.time"
            :type="index === 0 ? 'primary' : ''"
          >
            {{ event.content }}
          </el-timeline-item>
        </el-timeline>
      </div>

      <!-- 操作按钮 -->
      <div class="actions">
        <el-button v-if="order.status === 'WAITING_CONFIRM'" type="danger" @click="handleCancel">取消订单</el-button>
        <el-button v-if="order.status === 'IN_PROGRESS'" type="warning" @click="handleAppeal">申请售后</el-button>
        <el-button v-if="order.status === 'COMPLETED'" type="primary" @click="handleRentAgain">再次租赁</el-button>
      </div>
    </div>

    <el-empty v-else description="订单不存在" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';

interface OrderEvent {
  time: string;
  content: string;
}

interface OrderDetail {
  orderNo: string;
  item: string;
  rentHours: number;
  amount: string;
  status: string;
  contactInfo: string;
  remark: string;
  createdAt: string;
  events: OrderEvent[];
}

const route = useRoute();
const router = useRouter();
const order = ref<OrderDetail | null>(null);

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

function loadOrder() {
  const orderNo = route.params.id as string;
  // TODO: 调用后端接口
  // Mock 数据
  order.value = {
    orderNo,
    item: '高战账号 A01',
    rentHours: 2,
    amount: '¥56.00',
    status: 'IN_PROGRESS',
    contactInfo: '138****8000',
    remark: '',
    createdAt: '2026-04-23 14:30:00',
    events: [
      { time: '2026-04-23 14:30:00', content: '订单已提交' },
      { time: '2026-04-23 14:35:00', content: '客服已确认，账号交付中' },
      { time: '2026-04-23 14:40:00', content: '账号已交付，租赁进行中' }
    ]
  };
}

function handleCancel() {
  ElMessageBox.confirm('确定要取消该订单吗？', '提示', { type: 'warning' })
    .then(() => {
      ElMessage.success('订单已取消');
      router.push('/orders');
    })
    .catch(() => {});
}

function handleAppeal() {
  ElMessageBox.prompt('请输入申诉原因', '申请售后', { type: 'warning' })
    .then(({ value }) => {
      if (value) {
        ElMessage.success('申诉已提交');
        loadOrder();
      }
    })
    .catch(() => {});
}

function handleRentAgain() {
  router.push('/rentals');
}

onMounted(() => {
  loadOrder();
});
</script>

<style scoped>
.page-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px;
}

.order-detail {
  margin-top: 16px;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.detail-header h2 {
  margin: 0;
  font-size: 20px;
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

.detail-table {
  margin-bottom: 24px;
}

.timeline-section {
  margin-bottom: 24px;
}

.timeline-section h3 {
  margin: 0 0 12px;
  font-size: 16px;
}

.actions {
  display: flex;
  gap: 12px;
}
</style>