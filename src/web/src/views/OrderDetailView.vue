<template>
  <div class="page-shell">
    <div class="back-bar">
      <button class="back-btn" @click="$router.back()">← 返回</button>
    </div>

    <div v-if="loading" class="detail-card">
      <el-skeleton :rows="6" animated />
    </div>

    <div v-else-if="order" class="detail-content">
      <div class="detail-card detail-header-card">
        <div class="detail-title-row">
          <h2 class="detail-title">{{ order.item }}</h2>
          <span :class="['status-tag', `status-${order.status}`]">{{ statusText(order.status) }}</span>
        </div>
      </div>

      <div class="detail-card">
        <div class="info-grid">
          <div class="info-row">
            <span class="info-k">订单号</span>
            <span class="info-v">{{ order.orderNo }}</span>
          </div>
          <div class="info-row">
            <span class="info-k">下单时间</span>
            <span class="info-v">{{ order.createdAt }}</span>
          </div>
          <div class="info-row">
            <span class="info-k">租赁时长</span>
            <span class="info-v">{{ order.rentHours }} 小时</span>
          </div>
          <div class="info-row">
            <span class="info-k">订单金额</span>
            <span class="info-v price">￥{{ formatAmount(order.amount) }}</span>
          </div>
          <div class="info-row">
            <span class="info-k">联系方式</span>
            <span class="info-v">{{ order.contactInfo || '未填写' }}</span>
          </div>
          <div class="info-row">
            <span class="info-k">备注</span>
            <span class="info-v">{{ order.remark || '无' }}</span>
          </div>
        </div>
      </div>

      <div class="detail-card">
        <h3 class="section-title">订单进度</h3>
        <div class="timeline">
          <div
            v-for="(event, index) in order.events ?? []"
            :key="index"
            class="timeline-item"
          >
            <div class="timeline-dot" :class="{ latest: index === 0 }" />
            <div class="timeline-body">
              <div class="timeline-time">{{ event.time }}</div>
              <div class="timeline-content">{{ event.content }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="actions">
        <el-button v-if="order.status === 'WAITING_CONFIRM'" type="danger" @click="handleCancel">取消订单</el-button>
        <el-button v-if="order.status === 'IN_PROGRESS'" type="warning" @click="handleAppeal">申请售后</el-button>
        <el-button v-if="order.status === 'COMPLETED'" type="primary" @click="handleRentAgain">再次租赁</el-button>
      </div>
    </div>

    <div v-else class="empty-state">
      <div class="empty-icon">📭</div>
      <div class="empty-title">订单不存在</div>
    </div>
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
const loading = ref(true);
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
    .catch(() => {});
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
.page-shell {
  min-height: 100vh;
  background-color: #0f1c33;
  background-image: linear-gradient(135deg, #060d1a 0%, #0f1c33 50%, #0a1525 100%);
  color: #e2e8f0;
  padding: 24px;
}

.back-bar {
  margin-bottom: 16px;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(96, 165, 250, 0.2);
  border-radius: 8px;
  color: #93c5fd;
  font-size: 13px;
  padding: 6px 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.back-btn:hover {
  background: rgba(96, 165, 250, 0.12);
  border-color: rgba(96, 165, 250, 0.35);
  color: #bfdbfe;
}

.detail-card {
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(96, 165, 250, 0.1);
  border-radius: 16px;
  padding: 20px 24px;
  margin-bottom: 16px;
}

.detail-header-card {
  padding: 20px 24px;
}

.detail-title-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.detail-title {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #f1f5f9;
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

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px 24px;
}

.info-row {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-k {
  font-size: 12px;
  color: #64748b;
}

.info-v {
  font-size: 14px;
  font-weight: 500;
  color: #e2e8f0;
}

.info-v.price {
  color: #fbbf24;
  font-size: 15px;
}

.section-title {
  margin: 0 0 16px;
  font-size: 15px;
  font-weight: 600;
  color: #f1f5f9;
}

.timeline {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.timeline-item {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  position: relative;
  padding-bottom: 16px;
}

.timeline-item:last-child {
  padding-bottom: 0;
}

.timeline-item:not(:last-child)::before {
  content: '';
  position: absolute;
  left: 5px;
  top: 16px;
  bottom: 0;
  width: 1px;
  background: rgba(96, 165, 250, 0.15);
}

.timeline-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: rgba(96, 165, 250, 0.3);
  border: 2px solid rgba(96, 165, 250, 0.4);
  flex-shrink: 0;
  margin-top: 4px;
}

.timeline-dot.latest {
  background: #60a5fa;
  border-color: #60a5fa;
  box-shadow: 0 0 8px rgba(96, 165, 250, 0.5);
}

.timeline-body {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.timeline-time {
  font-size: 12px;
  color: #64748b;
}

.timeline-content {
  font-size: 14px;
  color: #e2e8f0;
}

.actions {
  display: flex;
  gap: 12px;
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
.empty-title { font-size: 18px; font-weight: 600; color: #f1f5f9; }

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #1e40af 0%, #1e3a8a 100%) !important;
  border: none !important;
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #2563eb 0%, #1e40af 100%) !important;
}

:deep(.el-button--danger) {
  background: rgba(239, 68, 68, 0.1) !important;
  border-color: rgba(239, 68, 68, 0.3) !important;
  color: #f87171 !important;
}

:deep(.el-button--danger:hover) {
  background: rgba(239, 68, 68, 0.2) !important;
  color: #fca5a5 !important;
}

:deep(.el-button--warning) {
  background: rgba(251, 191, 36, 0.1) !important;
  border-color: rgba(251, 191, 36, 0.3) !important;
  color: #fbbf24 !important;
}

:deep(.el-button--warning:hover) {
  background: rgba(251, 191, 36, 0.2) !important;
  color: #fde68a !important;
}
</style>
