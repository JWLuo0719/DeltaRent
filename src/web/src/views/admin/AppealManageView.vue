<template>
  <div class="admin-card">
    <h2 class="section-title">申诉工单管理</h2>

    <div class="filter-bar">
      <el-select v-model="filterStatus" placeholder="处理状态" clearable @change="loadAppeals">
        <el-option label="全部" value="" />
        <el-option label="待处理" value="PENDING" />
        <el-option label="已解决" value="RESOLVED" />
        <el-option label="已驳回" value="REJECTED" />
      </el-select>
      <el-select v-model="filterReason" placeholder="售后原因" clearable @change="loadAppeals">
        <el-option label="全部" value="" />
        <el-option label="账号不符" value="ACCOUNT_MISMATCH" />
        <el-option label="无法登录" value="LOGIN_FAILURE" />
        <el-option label="账号被找回" value="ACCOUNT_RECLAIMED" />
        <el-option label="服务质量" value="SERVICE_QUALITY" />
        <el-option label="其他" value="OTHER" />
      </el-select>
    </div>

    <el-table v-loading="loading" :data="filteredAppeals" @row-click="showDetail">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="用户" width="100">
        <template #default="{ row }">{{ row.userName || '-' }}</template>
      </el-table-column>
      <el-table-column label="关联账号" min-width="140">
        <template #default="{ row }">{{ row.productName || '-' }}</template>
      </el-table-column>
      <el-table-column label="售后原因" width="110">
        <template #default="{ row }">
          <el-tag size="small" :type="reasonType(row.reason)">{{ reasonText(row.reason) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="content" label="申诉内容" min-width="160">
        <template #default="{ row }">
          <span class="content-preview">{{ truncate(row.content, 40) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="处理人" width="100">
        <template #default="{ row }">{{ row.handlerName || '-' }}</template>
      </el-table-column>
      <el-table-column label="退款金额" width="100">
        <template #default="{ row }">
          <span v-if="row.refundAmount" class="refund-amount">¥{{ Number(row.refundAmount).toFixed(2) }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="更新时间" width="160">
        <template #default="{ row }">{{ row.updatedAt || '-' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="90" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.status === 'PENDING'"
            size="small"
            type="warning"
            @click.stop="openHandleDialog(row)"
          >
            处理
          </el-button>
          <span v-else class="muted-text">-</span>
        </template>
      </el-table-column>
    </el-table>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="申诉详情" width="600px">
      <div v-if="detailRow" class="detail-body">
        <div class="detail-row">
          <span class="detail-label">申诉编号</span>
          <span class="detail-value">{{ detailRow.id }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">申诉用户</span>
          <span class="detail-value">{{ detailRow.userName || '-' }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">关联账号</span>
          <span class="detail-value">{{ detailRow.productName || '-' }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">订单金额</span>
          <span class="detail-value price">¥{{ Number(detailRow.orderAmount || 0).toFixed(2) }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">售后原因</span>
          <span class="detail-value">{{ reasonText(detailRow.reason) }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">当前状态</span>
          <el-tag :type="statusType(detailRow.status)" size="small">{{ statusText(detailRow.status) }}</el-tag>
        </div>
        <div class="detail-section">
          <div class="detail-label">申诉内容</div>
          <div class="detail-text">{{ detailRow.content }}</div>
        </div>
        <template v-if="detailRow.status !== 'PENDING'">
          <div class="detail-section">
            <div class="detail-label">处理备注</div>
            <div class="detail-text">{{ detailRow.handlerRemark || '无' }}</div>
          </div>
          <div class="detail-row">
            <span class="detail-label">处理人</span>
            <span class="detail-value">{{ detailRow.handlerName || '-' }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">退款金额</span>
            <span class="detail-value price">{{ detailRow.refundAmount ? '¥' + Number(detailRow.refundAmount).toFixed(2) : '无' }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">赔偿说明</span>
            <span class="detail-value">{{ detailRow.compensation || '无' }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">处理时间</span>
            <span class="detail-value">{{ detailRow.handledAt || '-' }}</span>
          </div>
        </template>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button v-if="detailRow?.status === 'PENDING'" type="warning" @click="openHandleDialog(detailRow); detailVisible = false">去处理</el-button>
      </template>
    </el-dialog>

    <!-- 处理弹窗 -->
    <el-dialog v-model="handleVisible" title="处理申诉" width="520px">
      <div v-if="handleRow" class="handle-body">
        <div class="handle-summary">
          <div class="summary-item"><strong>申诉人：</strong>{{ handleRow.userName || '-' }}</div>
          <div class="summary-item"><strong>关联账号：</strong>{{ handleRow.productName || '-' }}</div>
          <div class="summary-item"><strong>订单金额：</strong>¥{{ Number(handleRow.orderAmount || 0).toFixed(2) }}</div>
          <div class="summary-item"><strong>原因：</strong>{{ reasonText(handleRow.reason) }}</div>
          <div class="summary-item"><strong>内容：</strong>{{ handleRow.content }}</div>
        </div>

        <el-divider />

        <el-form label-width="80px">
          <el-form-item label="处理结果" required>
            <el-radio-group v-model="handleForm.status">
              <el-radio value="RESOLVED">已解决（通过申诉）</el-radio>
              <el-radio value="REJECTED">驳回申诉</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="退款金额">
            <el-input-number
              v-model="handleForm.refundAmount"
              :min="0"
              :max="Number(handleRow.orderAmount || 0)"
              :precision="2"
              placeholder="不填则无退款"
              style="width: 200px"
            />
            <span class="form-hint">最大可退 ¥{{ Number(handleRow.orderAmount || 0).toFixed(2) }}</span>
          </el-form-item>
          <el-form-item label="赔偿说明">
            <el-input v-model="handleForm.compensation" placeholder="如：补偿优惠券、延长租期等" />
          </el-form-item>
          <el-form-item label="处理备注" required>
            <el-input
              v-model="handleForm.handlerRemark"
              type="textarea"
              :rows="3"
              placeholder="请填写处理说明..."
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="handleVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitHandle">确认处理</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { getAppeals, handleAppeal } from '@/api';
import type { AppealRecord } from '@/types/api';

const loading = ref(true);
const submitting = ref(false);
const appeals = ref<AppealRecord[]>([]);
const filterStatus = ref('');
const filterReason = ref('');

const detailVisible = ref(false);
const detailRow = ref<AppealRecord | null>(null);

const handleVisible = ref(false);
const handleRow = ref<AppealRecord | null>(null);
const handleForm = reactive({
  status: 'RESOLVED' as 'RESOLVED' | 'REJECTED',
  handlerRemark: '',
  refundAmount: null as number | null,
  compensation: ''
});

const statusMap: Record<string, string> = {
  PENDING: '待处理',
  RESOLVED: '已解决',
  REJECTED: '已驳回'
};

const reasonMap: Record<string, string> = {
  ACCOUNT_MISMATCH: '账号不符',
  LOGIN_FAILURE: '无法登录',
  ACCOUNT_RECLAIMED: '账号被找回',
  SERVICE_QUALITY: '服务质量',
  OTHER: '其他'
};

function statusText(s: string) { return statusMap[s] || s; }
function reasonText(r: string) { return reasonMap[r] || r || '其他'; }

function statusType(s: string) {
  return s === 'PENDING' ? 'warning' : s === 'RESOLVED' ? 'success' : 'info';
}

function reasonType(r: string) {
  const map: Record<string, string> = {
    ACCOUNT_MISMATCH: 'danger',
    LOGIN_FAILURE: 'warning',
    ACCOUNT_RECLAIMED: 'danger',
    SERVICE_QUALITY: 'info',
    OTHER: ''
  };
  return map[r] || '';
}

function truncate(text: string, max: number) {
  if (!text) return '';
  return text.length > max ? text.slice(0, max) + '...' : text;
}

const filteredAppeals = computed(() => {
  let list = appeals.value;
  if (filterStatus.value) {
    list = list.filter(a => a.status === filterStatus.value);
  }
  if (filterReason.value) {
    list = list.filter(a => a.reason === filterReason.value);
  }
  return list;
});

async function loadAppeals() {
  loading.value = true;
  try {
    const response = await getAppeals();
    if (response.data.success) {
      appeals.value = response.data.data;
      return;
    }
    ElMessage.error(response.data.message || '加载失败');
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '加载失败');
  } finally {
    loading.value = false;
  }
}

function showDetail(row: AppealRecord) {
  detailRow.value = row;
  detailVisible.value = true;
}

function openHandleDialog(row: AppealRecord) {
  handleRow.value = row;
  handleForm.status = 'RESOLVED';
  handleForm.handlerRemark = '';
  handleForm.refundAmount = null;
  handleForm.compensation = '';
  handleVisible.value = true;
}

async function submitHandle() {
  if (!handleRow.value) return;
  if (!handleForm.handlerRemark.trim()) {
    ElMessage.warning('请填写处理备注');
    return;
  }
  submitting.value = true;
  try {
    const response = await handleAppeal(handleRow.value.id, {
      status: handleForm.status,
      handlerRemark: handleForm.handlerRemark,
      refundAmount: handleForm.refundAmount,
      compensation: handleForm.compensation
    });
    if (response.data.success) {
      ElMessage.success('处理完成');
      handleVisible.value = false;
      await loadAppeals();
      return;
    }
    ElMessage.error(response.data.message || '处理失败');
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '处理失败');
  } finally {
    submitting.value = false;
  }
}

onMounted(loadAppeals);
</script>

<style scoped>
.admin-card {
  background: rgba(255, 255, 255, 0.88);
  border: 1px solid rgba(226, 232, 240, 0.9);
  border-radius: 22px;
  padding: 24px;
  box-shadow: 0 16px 36px rgba(15, 23, 42, 0.06);
}

.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.content-preview {
  color: #64748b;
  font-size: 13px;
}

.refund-amount {
  color: #f59e0b;
  font-weight: 500;
}

.muted-text {
  color: #94a3b8;
  font-size: 13px;
}

.detail-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-row {
  display: flex;
  align-items: center;
  gap: 16px;
}

.detail-label {
  font-size: 13px;
  color: #64748b;
  min-width: 70px;
  flex-shrink: 0;
}

.detail-value {
  font-size: 14px;
  color: #1f2937;
}

.detail-value.price {
  color: #f59e0b;
  font-weight: 500;
}

.detail-section {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.detail-text {
  font-size: 14px;
  color: #1f2937;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 10px 14px;
  line-height: 1.6;
}

.handle-body {
  display: flex;
  flex-direction: column;
}

.handle-summary {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.summary-item {
  font-size: 14px;
  color: #1f2937;
}

.form-hint {
  margin-left: 10px;
  font-size: 12px;
  color: #94a3b8;
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
:deep(.el-dialog) {
  border-radius: 16px !important;
}
:deep(.el-radio-group) {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
:deep(.el-button--primary) {
  background: linear-gradient(135deg, #ffe057 0%, #ffc420 100%) !important;
  border: none !important;
  color: #5a3c00 !important;
}
:deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #ffd95a 0%, #ffb81f 100%) !important;
}
:deep(.el-button--warning) {
  background: rgba(251, 191, 36, 0.1) !important;
  border-color: rgba(251, 191, 36, 0.3) !important;
  color: #f59e0b !important;
}
:deep(.el-button--warning:hover) {
  background: rgba(251, 191, 36, 0.2) !important;
}
</style>
