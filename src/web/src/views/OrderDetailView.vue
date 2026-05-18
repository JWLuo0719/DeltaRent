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
            <span class="info-v">{{ order.rentDays }} 天</span>
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

      <!-- 售后申诉结果 -->
      <div v-if="appeal" class="detail-card appeal-card">
        <h3 class="section-title appeal-title">
          <span>售后申诉</span>
          <el-tag :type="appealStatusType(appeal.status)" size="small">{{ appealStatusText(appeal.status) }}</el-tag>
        </h3>
        <div class="appeal-info">
          <div class="appeal-row">
            <span class="appeal-k">申诉原因</span>
            <span class="appeal-v">{{ reasonText(appeal.reason) }}</span>
          </div>
          <div class="appeal-row">
            <span class="appeal-k">问题描述</span>
            <span class="appeal-v">{{ appeal.content }}</span>
          </div>
          <template v-if="appeal.status !== 'PENDING'">
            <div class="appeal-row">
              <span class="appeal-k">处理结果</span>
              <span class="appeal-v" :class="appeal.status === 'RESOLVED' ? 'text-resolved' : 'text-rejected'">
                {{ appeal.status === 'RESOLVED' ? '已解决' : '已驳回' }}
              </span>
            </div>
            <div class="appeal-row">
              <span class="appeal-k">处理备注</span>
              <span class="appeal-v">{{ appeal.handlerRemark || '无' }}</span>
            </div>
            <div class="appeal-row">
              <span class="appeal-k">退款金额</span>
              <span class="appeal-v refund-amount">
                {{ appeal.refundAmount ? '¥' + Number(appeal.refundAmount).toFixed(2) : '无退款' }}
              </span>
            </div>
            <div class="appeal-row">
              <span class="appeal-k">赔偿说明</span>
              <span class="appeal-v">{{ appeal.compensation || '无' }}</span>
            </div>
            <div class="appeal-row">
              <span class="appeal-k">处理时间</span>
              <span class="appeal-v">{{ appeal.handledAt || '-' }}</span>
            </div>
          </template>
          <div v-else class="pending-hint">
            <span>客服正在处理你的申诉，请耐心等待...</span>
          </div>
        </div>
      </div>

      <div class="actions">
        <el-button v-if="order.status === 'WAITING_CONFIRM'" type="danger" @click="handleCancel">取消订单</el-button>
        <el-button v-if="order.status === 'IN_PROGRESS'" type="warning" @click="appealVisible = true">申请售后</el-button>
        <el-button v-if="order.status === 'COMPLETED'" type="primary" @click="handleRentAgain">再次租赁</el-button>
      </div>

      <!-- 售后申诉弹窗 -->
      <el-dialog v-model="appealVisible" title="申请售后" width="480px" @close="resetAppealForm">
        <el-form label-width="80px">
          <el-form-item label="售后原因" required>
            <el-select v-model="appealForm.reason" placeholder="请选择售后原因" style="width: 100%">
              <el-option label="账号不符（段位/KD/皮肤等）" value="ACCOUNT_MISMATCH" />
              <el-option label="账号无法登录" value="LOGIN_FAILURE" />
              <el-option label="账号中途被找回/顶号" value="ACCOUNT_RECLAIMED" />
              <el-option label="服务质量问题" value="SERVICE_QUALITY" />
              <el-option label="其他" value="OTHER" />
            </el-select>
          </el-form-item>
          <el-form-item label="问题描述" required>
            <el-input
              v-model="appealForm.content"
              type="textarea"
              :rows="4"
              placeholder="请详细描述遇到的问题..."
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="appealVisible = false">取消</el-button>
          <el-button type="primary" :loading="submittingAppeal" @click="submitAppealForm">提交申诉</el-button>
        </template>
      </el-dialog>
    </div>

    <div v-else class="empty-state">
      <div class="empty-icon">📭</div>
      <div class="empty-title">订单不存在</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { cancelOrder, getOrderDetail, getMyAppeals, submitAppeal } from '@/api';
import type { AppealRecord, OrderDetail } from '@/types/api';

const route = useRoute();
const router = useRouter();
const loading = ref(true);
const submittingAppeal = ref(false);
const order = ref<OrderDetail | null>(null);
const appeal = ref<AppealRecord | null>(null);
const appealVisible = ref(false);
const appealForm = reactive({
  reason: 'OTHER',
  content: ''
});

const statusMap: Record<string, string> = {
  WAITING_CONFIRM: '待确认',
  IN_PROGRESS: '进行中',
  COMPLETED: '已完成',
  CANCELLED: '已取消',
  AFTER_SALE: '售后中'
};

const appealStatusMap: Record<string, string> = {
  PENDING: '处理中',
  RESOLVED: '已解决',
  REJECTED: '已驳回'
};

const reasonMap: Record<string, string> = {
  ACCOUNT_MISMATCH: '账号不符（段位/KD/皮肤等）',
  LOGIN_FAILURE: '账号无法登录',
  ACCOUNT_RECLAIMED: '账号中途被找回/顶号',
  SERVICE_QUALITY: '服务质量问题',
  OTHER: '其他'
};

function statusText(status: string) { return statusMap[status] || status; }
function appealStatusText(s: string) { return appealStatusMap[s] || s; }
function reasonText(r: string) { return reasonMap[r] || r || '其他'; }

function appealStatusType(s: string) {
  return s === 'PENDING' ? 'warning' : s === 'RESOLVED' ? 'success' : 'info';
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
      await loadAppealForOrder();
      return;
    }
    ElMessage.error(response.data.message || '订单加载失败');
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '订单加载失败');
  } finally {
    loading.value = false;
  }
}

async function loadAppealForOrder() {
  if (!order.value?.id) return;
  try {
    const res = await getMyAppeals();
    if (res.data.success) {
      const list = res.data.data || [];
      appeal.value = list.find(
        a => a.orderType === 'RENTAL' && a.orderId === order.value!.id
      ) || null;
    }
  } catch {
    appeal.value = null;
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

function resetAppealForm() {
  appealForm.reason = 'OTHER';
  appealForm.content = '';
}

async function submitAppealForm() {
  if (!order.value?.id) return;
  if (!appealForm.content.trim()) {
    ElMessage.warning('请填写问题描述');
    return;
  }
  submittingAppeal.value = true;
  try {
    const response = await submitAppeal({
      orderType: 'RENTAL',
      orderId: order.value!.id,
      content: appealForm.content,
      reason: appealForm.reason
    });
    if (response.data.success) {
      ElMessage.success('申诉已提交');
      appealVisible.value = false;
      resetAppealForm();
      await loadAppealForOrder();
      return;
    }
    ElMessage.error(response.data.message || '申诉提交失败');
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '申诉提交失败');
  } finally {
    submittingAppeal.value = false;
  }
}

function handleRentAgain() {
  router.push('/rentals');
}

onMounted(loadOrder);
</script>

<style scoped>
.page-shell {
  min-height: 100vh;
  background: transparent;
  color: #1f2937;
  padding: 24px;
}

.back-bar {
  margin-bottom: 16px;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  color: #9b5d00;
  font-size: 13px;
  padding: 6px 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.back-btn:hover {
  background: #fff0d0;
  border-color: #ffd46b;
  color: #9b5d00;
}

.detail-card {
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid #e2e8f0;
  border-radius: 20px;
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
  color: #1f2937;
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
  color: #1f2937;
}

.info-v.price {
  color: #fbbf24;
  font-size: 15px;
}

.section-title {
  margin: 0 0 16px;
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
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
  background: rgba(255, 196, 32, 0.15);
}

.timeline-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: rgba(255, 196, 32, 0.3);
  border: 2px solid rgba(255, 196, 32, 0.4);
  flex-shrink: 0;
  margin-top: 4px;
}

.timeline-dot.latest {
  background: #f59e0b;
  border-color: #f59e0b;
  box-shadow: 0 0 8px rgba(245, 158, 11, 0.5);
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
  color: #1f2937;
}

.actions {
  display: flex;
  gap: 12px;
}

.appeal-card {
  border-left: 4px solid #f59e0b;
}

.appeal-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}

.appeal-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.appeal-row {
  display: flex;
  gap: 16px;
}

.appeal-k {
  font-size: 12px;
  color: #64748b;
  min-width: 65px;
  flex-shrink: 0;
}

.appeal-v {
  font-size: 14px;
  color: #1f2937;
  line-height: 1.5;
}

.appeal-v.text-resolved {
  color: #22c55e;
  font-weight: 600;
}

.appeal-v.text-rejected {
  color: #94a3b8;
  font-weight: 600;
}

.appeal-v.refund-amount {
  color: #f59e0b;
  font-weight: 600;
  font-size: 15px;
}

.pending-hint {
  font-size: 13px;
  color: #94a3b8;
  padding: 8px 0;
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
.empty-title { font-size: 18px; font-weight: 600; color: #1f2937; }

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #ffe057 0%, #ffc420 100%) !important;
  border: none !important;
  color: #5a3c00 !important;
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #ffd95a 0%, #ffb81f 100%) !important;
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
