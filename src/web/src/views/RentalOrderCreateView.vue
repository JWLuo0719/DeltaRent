<template>
  <div class="page-shell stack">
    <section class="hero-card">
      <button class="back-btn" @click="$router.push('/rentals')">
        <span class="back-icon">←</span> 返回账号列表
      </button>
      <h1 class="page-title">创建账号租赁订单</h1>
      <p class="page-subtitle">当前页面已接入真实后端接口，可直接确认账号信息并提交订单。</p>
    </section>

    <section class="panel-card">
      <div v-if="selectedAccount" class="account-info-card">
        <div class="account-info-left">
          <div class="account-name">{{ selectedAccount.name }}</div>
          <div v-if="accountTags.length" class="account-tags">
            <span v-for="tag in accountTags" :key="tag" class="tag-chip">{{ tag }}</span>
          </div>
        </div>
        <div class="account-price-block">
          <span class="price-label">单价</span>
          <span class="price-value">¥{{ selectedAccount.hourPrice }}</span>
          <span class="price-unit">/小时</span>
        </div>
      </div>

      <div v-if="selectedAccount" class="discount-tip">
        <span class="discount-icon">💡</span>
        当前选择时长 {{ form.rentHours }} 小时，折扣：<strong>{{ discountLabel }}</strong>，实付：<strong>¥{{ finalPrice }}</strong>
      </div>

      <el-form label-position="top" class="order-form">
        <div class="grid-2">
          <el-form-item v-if="!accountLocked" label="账号编号">
            <el-select
              v-model="form.accountId"
              placeholder="请选择账号"
              filterable
              :loading="accountsLoading"
            >
              <el-option
                v-for="item in accounts"
                :key="item.id"
                :label="`${item.name}（￥${item.hourPrice}/小时）`"
                :value="item.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item v-if="!durationLocked" label="租赁时长">
            <el-select v-model="form.rentHours" placeholder="请选择时长">
              <el-option
                v-for="hours in durationOptions"
                :key="hours"
                :label="`${hours}小时`"
                :value="hours"
              />
            </el-select>
          </el-form-item>

          <div v-else class="duration-display-card">
            <span class="duration-label">租赁时长</span>
            <span class="duration-value">{{ form.rentHours }} 小时</span>
            <span class="duration-discount">{{ discountLabel }}</span>
          </div>
        </div>

        <el-form-item label="联系方式">
          <el-input v-model="form.contactInfo" placeholder="请输入 QQ / 微信 / 手机号" />
        </el-form-item>

        <el-form-item label="备注">
          <el-input v-model="form.remark" placeholder="可填写交付时间、使用偏好等补充信息" />
        </el-form-item>

        <el-button type="primary" :loading="submitting" @click="submitOrder">提交订单</el-button>
      </el-form>

      <div v-if="resultMessage" class="result-card">
        <div class="result-icon">✓</div>
        <div class="result-content">
          <div class="result-title">订单提交成功</div>
          <div class="result-message">{{ resultMessage }}</div>
        </div>
        <button class="result-close" @click="resultMessage = ''">×</button>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import { createOrder, getRentals } from '@/api';
import type { CreateOrderPayload, RentalProduct } from '@/types/api';

const route = useRoute();

function parseQueryNumber(value: unknown) {
  const parsed = Number(value);
  return Number.isFinite(parsed) && parsed > 0 ? parsed : 0;
}

function splitTags(tagText?: string) {
  return (tagText ?? '')
    .split(',')
    .map(tag => tag.trim())
    .filter(Boolean);
}

const requestedAccountId = parseQueryNumber(route.query.accountId);
const requestedDuration = parseQueryNumber(route.query.duration);
const accountLocked = requestedAccountId > 0;
const durationLocked = requestedDuration > 0;
const durationOptions = [1, 6, 12, 24];

const accounts = ref<RentalProduct[]>([]);
const accountsLoading = ref(false);
const submitting = ref(false);
const resultMessage = ref('');

const form = reactive<CreateOrderPayload>({
  accountId: requestedAccountId,
  rentHours: requestedDuration || 1,
  contactInfo: '',
  remark: ''
});

const selectedAccount = computed(
  () => accounts.value.find(item => item.id === form.accountId) ?? null
);
const accountTags = computed(() => splitTags(selectedAccount.value?.tagText));

const discountMap: Record<number, { label: string; factor: number }> = {
  1: { label: '无折扣', factor: 1 },
  6: { label: '9折', factor: 0.9 },
  12: { label: '8折', factor: 0.8 },
  24: { label: '7折', factor: 0.7 }
};

const discountLabel = computed(() => discountMap[form.rentHours]?.label || '无折扣');
const discountFactor = computed(() => discountMap[form.rentHours]?.factor || 1);
const finalPrice = computed(() => {
  if (!selectedAccount.value) return '0.00';
  return (selectedAccount.value.hourPrice * form.rentHours * discountFactor.value).toFixed(2);
});

async function loadAccounts() {
  accountsLoading.value = true;
  try {
    const response = await getRentals({ status: 'AVAILABLE' });
    if (response.data.success) {
      accounts.value = response.data.data;

      if (!form.accountId && accounts.value.length > 0) {
        form.accountId = accounts.value[0].id;
      }

      if (form.accountId && !selectedAccount.value) {
        ElMessage.warning(
          accountLocked ? '所选账号暂不可租赁，请返回列表重新选择' : '默认账号已失效，请重新选择'
        );
        if (!accountLocked && accounts.value.length > 0) {
          form.accountId = accounts.value[0].id;
        }
      }
      return;
    }

    ElMessage.error(response.data.message || '账号列表加载失败');
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '账号列表加载失败');
  } finally {
    accountsLoading.value = false;
  }
}

async function submitOrder() {
  if (!form.accountId || !selectedAccount.value) {
    ElMessage.warning(
      accountLocked ? '所选账号暂不可租赁，请返回列表重新选择' : '请先选择账号'
    );
    return;
  }

  if (!form.contactInfo.trim()) {
    ElMessage.warning('请先填写联系方式');
    return;
  }

  submitting.value = true;
  try {
    const response = await createOrder(form);
    if (response.data.success) {
      const result = response.data.data;
      resultMessage.value = `订单 ${result.orderNo} 已创建，状态：${result.status}，${result.estimatedDelivery}`;
      ElMessage.success(response.data.message);
      return;
    }

    ElMessage.error(response.data.message || '提交订单失败');
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '提交订单失败');
  } finally {
    submitting.value = false;
  }
}

onMounted(loadAccounts);
</script>

<style scoped>
.page-shell {
  min-height: 100vh;
  background: #070b14;
  padding: 40px 20px;
}

.hero-card {
  background: linear-gradient(135deg, #0f1c33 0%, #1a2744 100%);
  border: 1px solid rgba(64, 158, 255, 0.15);
  border-radius: 16px;
  padding: 36px 40px;
  margin-bottom: 28px;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  background: none;
  border: 1px solid rgba(64, 158, 255, 0.25);
  border-radius: 8px;
  color: #7a9cc8;
  font-size: 13px;
  padding: 6px 14px;
  cursor: pointer;
  margin-bottom: 16px;
  transition: all 0.2s;
}

.back-btn:hover {
  background: rgba(64, 158, 255, 0.1);
  color: #a8c5e8;
  border-color: rgba(64, 158, 255, 0.4);
}

.back-icon {
  font-size: 16px;
}

.page-title {
  font-size: 26px;
  font-weight: 700;
  color: #e8edf5;
  margin: 0 0 10px;
}

.page-subtitle {
  font-size: 14px;
  color: #7a8ba8;
  margin: 0;
}

.panel-card {
  background: #0f1c33;
  border: 1px solid rgba(64, 158, 255, 0.1);
  border-radius: 16px;
  padding: 32px 36px;
  max-width: 720px;
}

.account-info-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(135deg, #0a1628 0%, #0f2040 100%);
  border: 1px solid rgba(64, 158, 255, 0.2);
  border-radius: 12px;
  padding: 18px 22px;
  margin-bottom: 18px;
}

.account-info-left {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.account-name {
  font-size: 16px;
  font-weight: 600;
  color: #d8e4f0;
}

.account-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.tag-chip {
  background: rgba(64, 158, 255, 0.12);
  border: 1px solid rgba(64, 158, 255, 0.2);
  border-radius: 6px;
  padding: 2px 8px;
  font-size: 12px;
  color: #7aacc8;
}

.account-price-block {
  text-align: right;
}

.price-label {
  font-size: 12px;
  color: #6a7d98;
  display: block;
}

.price-value {
  font-size: 22px;
  font-weight: 700;
  color: #409eff;
}

.price-unit {
  font-size: 13px;
  color: #6a7d98;
}

.discount-tip {
  background: rgba(64, 158, 255, 0.08);
  border: 1px solid rgba(64, 158, 255, 0.18);
  border-radius: 10px;
  padding: 10px 16px;
  font-size: 13px;
  color: #8aaac8;
  margin-bottom: 24px;
}

.discount-tip strong {
  color: #409eff;
}

.discount-icon {
  margin-right: 4px;
}

.duration-display-card {
  display: flex;
  align-items: center;
  gap: 12px;
  background: linear-gradient(135deg, #0a1628 0%, #0f2040 100%);
  border: 1px solid rgba(64, 158, 255, 0.2);
  border-radius: 10px;
  padding: 14px 20px;
  margin-bottom: 18px;
}

.duration-label {
  font-size: 13px;
  color: #6a7d98;
  min-width: 60px;
}

.duration-value {
  font-size: 16px;
  font-weight: 600;
  color: #d8e4f0;
}

.duration-discount {
  background: rgba(64, 158, 255, 0.15);
  border: 1px solid rgba(64, 158, 255, 0.25);
  border-radius: 6px;
  padding: 2px 10px;
  font-size: 12px;
  color: #409eff;
}

.grid-2 {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

:deep(.el-form-item__label) {
  color: #8a9cba;
  font-size: 13px;
  font-weight: 500;
  padding-bottom: 6px;
}

:deep(.el-input__wrapper),
:deep(.el-select .el-input__wrapper) {
  background: #0a1120;
  border-color: rgba(64, 158, 255, 0.2);
  border-radius: 10px;
  box-shadow: none;
  padding: 0 14px;
}

:deep(.el-input__inner) {
  color: #d0d8e8;
  font-size: 14px;
}

:deep(.el-input__inner::placeholder) {
  color: #4a5a78;
}

:deep(.el-select .el-input__inner) {
  color: #d0d8e8;
}

:deep(.el-form-item) {
  margin-bottom: 22px;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #409eff 0%, #3078d0 100%);
  border: none;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  padding: 12px 32px;
  letter-spacing: 1px;
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #50b4ff 0%, #409eff 100%);
}

.result-card {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  background: linear-gradient(135deg, #0a2016 0%, #0f2e1e 100%);
  border: 1px solid rgba(64, 255, 160, 0.25);
  border-radius: 12px;
  padding: 18px 20px;
  margin-top: 20px;
}

.result-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(64, 255, 160, 0.15);
  color: #40ff9a;
  font-size: 20px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.result-content {
  flex: 1;
}

.result-title {
  font-size: 15px;
  font-weight: 600;
  color: #60ffb0;
  margin-bottom: 4px;
}

.result-message {
  font-size: 13px;
  color: #7acc9a;
  line-height: 1.5;
}

.result-close {
  background: none;
  border: none;
  color: #4a8a6a;
  font-size: 22px;
  cursor: pointer;
  padding: 0 4px;
  line-height: 1;
}

.result-close:hover {
  color: #60ffb0;
}

@media (max-width: 640px) {
  .grid-2 {
    grid-template-columns: 1fr;
  }

  .hero-card {
    padding: 24px 20px;
  }

  .panel-card {
    padding: 24px 20px;
  }

  .account-info-card {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .account-price-block {
    text-align: left;
  }
}
</style>
