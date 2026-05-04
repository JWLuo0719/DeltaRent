<template>
  <div class="page-container">
    <el-button text @click="$router.back()">← 返回</el-button>

    <div v-if="loading" class="loading-wrap">
      <el-skeleton :rows="6" animated />
    </div>

    <div v-else-if="order" class="order-detail">
      <div class="detail-header">
        <h2>{{ order.item }}</h2>
        <span :class="['status-tag', `status-${order.status}`]">{{ statusText(order.status) }}</span>
      </div>

      <el-descriptions :column="2" border class="detail-table">
        <el-descriptions-item label="订单号">{{ order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ order.createdAt }}</el-descriptions-item>
        <el-descriptions-item label="租赁时长">{{ order.rentHours }} 小时</el-descriptions-item>
        <el-descriptions-item label="订单金额">￥{{ formatAmount(order.amount) }}</el-descriptions-item>
        <el-descriptions-item label="账号" :span="2">{{ order.item }}</el-descriptions-item>
        <el-descriptions-item label="联系方式">{{ order.contactInfo || '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ order.remark || '无' }}</el-descriptions-item>
      </el-descriptions>

      <div class="timeline-section">
        <h3>订单进度</h3>
        <el-timeline>
          <el-timeline-item
            v-for="(event, index) in order.events ?? []"
            :key="index"
            :timestamp="event.time"
            :type="index === 0 ? 'primary' : ''"
          >
            {{ event.content }}
          </el-timeline-item>
        </el-timeline>
      </div>

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
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { cancelOrder, getOrderDetail, submitAppeal } from '@/api';
import type { OrderDetail } from '@/types/api';

const route = useRoute();
const router = useRouter();
const loading = ref(false);
const order = ref<OrderDetail | null>(null);

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

async function loadOrder() {
  loading.value = true;
  try {
    const orderNo = route.params.id as string;
    const response = await getOrderDetail(orderNo);
    if (response.data.success) {
      order.value = response.data.data;
      return;
    }
    ElMessage.error(response.data.message || '订单加载失败');
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '订单加载失败');
  } finally {
    loading.value = false;
  }
}

function handleCancel() {
  if (!order.value) return;
  ElMessageBox.confirm('确定要取消该订单吗？', '提示', { type: 'warning' })
    .then(async () => {
      const response = await cancelOrder(order.value!.orderNo);
      if (response.data.success) {
        ElMessage.success('订单已取消');
        await loadOrder();
        return;
      }
      ElMessage.error(response.data.message || '取消失败');
    })
    .catch(() => {
      ElMessage.error('取消失败，请重试');
    });
}

function handleAppeal() {
  if (!order.value?.id) return;
  ElMessageBox.prompt('请输入申请售后的原因', '申请售后', { type: 'warning' })
    .then(async ({ value }) => {
      if (!value) return;
      const response = await submitAppeal({
        orderType: 'RENTAL',
        orderId: order.value!.id,
        content: value
      });
      if (response.data.success) {
        ElMessage.success('申诉已提交');
        await loadOrder();
        return;
      }
      ElMessage.error(response.data.message || '申诉提交失败');
    })
    .catch(() => {});
}

function handleRentAgain() {
  router.push('/rentals');
}

onMounted(loadOrder);
</script>

<style scoped>
.page-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px;
}

.loading-wrap {
  margin-top: 16px;
  background: #fff;
  border-radius: 12px;
  padding: 20px;
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
.status-AFTER_SALE { background: #fee2e2; color: #dc2626; }

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
