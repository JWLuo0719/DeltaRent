<template>
  <div class="page-shell">
    <section class="hero-card">
      <button class="back-btn" @click="$router.push('/rentals')">
        <span class="back-icon">←</span> 返回账号列表
      </button>
      <h1 class="page-title">租用账号</h1>
      <p class="page-subtitle">确认账号信息后直接提交，客服将尽快联系您完成交付</p>
    </section>

    <section class="main-content">
      <!-- 账号信息卡 -->
      <div v-if="selectedAccount" class="product-card">
        <div class="product-cover">
          <img v-if="selectedAccount.coverImageUrl" :src="selectedAccount.coverImageUrl" alt="" />
          <div v-else class="cover-placeholder">
            <span>{{ selectedAccount.rankText || '三角洲' }}</span>
            <strong>{{ selectedAccount.insuranceBoxText || '账号' }}</strong>
          </div>
          <div class="product-price-badge">¥{{ selectedAccount.hourPrice }}</div>
        </div>

        <div class="product-info">
          <h2 class="product-name">{{ selectedAccount.name }}</h2>

          <div v-if="accountTags.length" class="product-tags">
            <span v-for="tag in accountTags" :key="tag" class="tag">{{ tag }}</span>
          </div>

          <div class="product-attrs">
            <div v-if="selectedAccount.rankText" class="attr-item">
              <span class="attr-label">段位</span>
              <span class="attr-value">{{ selectedAccount.rankText }}</span>
            </div>
            <div v-if="selectedAccount.insuranceBoxText" class="attr-item">
              <span class="attr-label">保险箱</span>
              <span class="attr-value">{{ selectedAccount.insuranceBoxText }}</span>
            </div>
            <div v-if="selectedAccount.coinAmount" class="attr-item">
              <span class="attr-label">哈夫币</span>
              <span class="attr-value">{{ formatCoin(selectedAccount.coinAmount) }}</span>
            </div>
            <div v-if="selectedAccount.loginMethod" class="attr-item">
              <span class="attr-label">上号方式</span>
              <span class="attr-value">{{ selectedAccount.loginMethod }}</span>
            </div>
            <div v-if="selectedAccount.rentalDays" class="attr-item">
              <span class="attr-label">租用天数</span>
              <span class="attr-value">{{ selectedAccount.rentalDays }}天</span>
            </div>
            <div v-if="selectedAccount.staminaText || selectedAccount.weightText" class="attr-item">
              <span class="attr-label">体力/负重</span>
              <span class="attr-value">{{ selectedAccount.staminaText }}/{{ selectedAccount.weightText }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 账号选择 -->
      <div v-if="!accountLocked" class="select-card">
        <div class="select-label">
          <span>选择账号</span>
          <span class="select-hint">共 {{ accounts.length }} 个可租账号</span>
        </div>
        <el-select
          v-model="form.accountId"
          placeholder="请选择账号"
          filterable
          :loading="accountsLoading"
          class="account-select"
        >
          <template #prefix>
            <span class="select-prefix-icon">☰</span>
          </template>
          <el-option
            v-for="item in accounts"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          >
            <div class="option-item">
              <span class="option-name">{{ item.name }}</span>
              <span class="option-meta">
                <span v-if="item.rankText">{{ item.rankText }}</span>
                <span class="option-price">¥{{ item.hourPrice }}</span>
              </span>
            </div>
          </el-option>
        </el-select>
      </div>

      <!-- 操作区 -->
      <div class="action-card">
        <div class="order-summary">
          <div class="summary-row">
            <span>账号租金</span>
            <span class="summary-value">¥{{ formatPrice(rentalAmount) }}</span>
          </div>
          <div class="summary-row">
            <span>押金</span>
            <span class="summary-sub-value">¥{{ formatPrice(depositAmount) }}</span>
          </div>
          <div class="summary-row">
            <span>服务费（5%）</span>
            <span class="summary-sub-value">¥{{ formatPrice(serviceFee) }}</span>
          </div>
          <div class="summary-row summary-row-total">
            <span>应付合计</span>
            <span class="summary-total-value">¥{{ formatPrice(totalAmount) }}</span>
          </div>
        </div>

        <div class="price-guide-card">
          <div class="guide-title">上架金额仅为账号纯币租金，其余额外物品使用价格如下</div>
          <div class="guide-grid">
            <div v-for="item in extraPriceGuide" :key="item.label" class="guide-item">
              <span>{{ item.label }}</span>
              <strong>{{ item.price }}</strong>
            </div>
          </div>
        </div>

        <el-button
          type="primary"
          size="large"
          :loading="submitting"
          class="submit-btn"
          @click="submitOrder"
        >
          {{ submitting ? '提交中...' : '立即租用' }}
        </el-button>

        <p class="submit-tip">提交后客服将在 10 分钟内联系您</p>
      </div>

      <!-- 成功提示 -->
      <transition name="slide-up">
        <div v-if="resultMessage" class="success-card">
          <div class="success-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <circle cx="12" cy="12" r="11" stroke="currentColor" stroke-width="1.5"/>
              <path d="M8 12l3 3 5-5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <div class="success-body">
            <h3>订单已提交</h3>
            <p>{{ resultMessage }}</p>
          </div>
          <button class="success-close" @click="resultMessage = ''">×</button>
        </div>
      </transition>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import { createOrder, getRentals } from '@/api';
import type { CreateOrderPayload, RentalProduct } from '@/types/api';

const router = useRouter();
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

function formatCoin(value: number | undefined) {
  const amount = Number(value || 0);
  if (amount >= 10000) return `${(amount / 10000).toFixed(2)}亿`;
  return `${amount}万`;
}

function formatPrice(value: number) {
  return Number(value || 0).toFixed(2);
}

const requestedAccountId = parseQueryNumber(route.query.accountId);
const accountLocked = requestedAccountId > 0;

const accounts = ref<RentalProduct[]>([]);
const accountsLoading = ref(false);
const submitting = ref(false);
const resultMessage = ref('');

const form = reactive<CreateOrderPayload>({
  accountId: requestedAccountId,
  rentDays: 1,
  contactInfo: '',
  remark: ''
});

const selectedAccount = computed(
  () => accounts.value.find(item => item.id === form.accountId) ?? null
);
const accountTags = computed(() => splitTags(selectedAccount.value?.tagText));
const rentalAmount = computed(() => Number(selectedAccount.value?.hourPrice || 0));
const depositAmount = computed(() => Number(selectedAccount.value?.deposit || 0));
const serviceFee = computed(() => Number((rentalAmount.value * 0.05).toFixed(2)));
const totalAmount = computed(() => Number((rentalAmount.value + depositAmount.value + serviceFee.value).toFixed(2)));

const extraPriceGuide = [
  { label: 'AWM', price: '¥0.8/发' },
  { label: '6头', price: '¥2/个' },
  { label: '6甲', price: '¥3/个' },
  { label: '其他红蛋', price: '¥6/组' },
  { label: '咖啡豆', price: '¥3/个' },
  { label: '乌龟包', price: '¥1/个' },
  { label: '9格体验卡', price: '¥5/天' }
];

async function loadAccounts() {
  accountsLoading.value = true;
  try {
    const response = await getRentals({ status: 'AVAILABLE', page: 1, pageSize: 1000 });
    if (response.data.success) {
      accounts.value = response.data.data?.list ?? [];

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

  submitting.value = true;
  try {
    const response = await createOrder(form);
    if (response.data.success) {
      const result = response.data.data;
      resultMessage.value = `订单号 ${result.orderNo}，金额 ¥${result.amount}，状态：${result.status}`;
      ElMessage.success('订单提交成功');
      setTimeout(() => { router.push('/orders'); }, 1800);
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
  background: #fffdf4;
  padding: 40px 24px;
}

.hero-card {
  max-width: 680px;
  margin: 0 auto 28px;
  background: rgba(255, 255, 255, 0.92);
  border-radius: 20px;
  padding: 32px 36px;
  border: 1px solid rgba(226, 232, 240, 0.9);
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: rgba(255, 245, 214, 0.5);
  border: 1px solid rgba(255, 214, 107, 0.4);
  border-radius: 10px;
  color: #9b5d00;
  font-size: 13px;
  padding: 7px 16px;
  cursor: pointer;
  margin-bottom: 18px;
  transition: all 0.2s;
}

.back-btn:hover {
  background: #fff7e6;
  border-color: #ffd46b;
}

.back-icon { font-size: 15px; }

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #3c2b00;
  margin: 0 0 8px;
  letter-spacing: 1px;
}

.page-subtitle {
  font-size: 14px;
  color: #9b5d00;
  margin: 0;
}

.main-content {
  max-width: 680px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

/* 产品卡 */
.product-card {
  display: grid;
  grid-template-columns: 200px 1fr;
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid rgba(255, 214, 107, 0.35);
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(181, 145, 41, 0.1);
}

.product-cover {
  position: relative;
  min-height: 220px;
}

.product-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.cover-placeholder {
  height: 100%;
  min-height: 220px;
  background: linear-gradient(135deg, #1a1a2e 0%, #2d2d44 100%);
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 16px;
  color: #fff;
}

.cover-placeholder span {
  font-size: 11px;
  opacity: 0.7;
  letter-spacing: 1px;
}

.cover-placeholder strong {
  font-size: 22px;
  margin-top: 4px;
}

.product-price-badge {
  position: absolute;
  top: 14px;
  right: 14px;
  background: linear-gradient(135deg, #ffe057 0%, #ffc420 100%);
  color: #5a3c00;
  font-size: 15px;
  font-weight: 700;
  padding: 5px 12px;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(255, 196, 32, 0.3);
}

.product-info {
  padding: 20px 22px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.product-name {
  font-size: 18px;
  font-weight: 700;
  color: #3c2b00;
  margin: 0;
}

.product-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.tag {
  background: rgba(255, 196, 32, 0.12);
  border: 1px solid rgba(255, 196, 32, 0.25);
  color: #c57a00;
  font-size: 11px;
  padding: 2px 10px;
  border-radius: 12px;
}

.product-attrs {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px 16px;
}

.attr-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: rgba(255, 245, 214, 0.5);
  border-radius: 10px;
}

.attr-label {
  font-size: 11px;
  color: #9b5d00;
}

.attr-value {
  font-size: 13px;
  font-weight: 600;
  color: #5a3c00;
}

/* 选择卡 */
.select-card {
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid rgba(255, 214, 107, 0.35);
  border-radius: 20px;
  padding: 20px 24px;
}

.select-label {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}

.select-label > span:first-child {
  font-size: 14px;
  font-weight: 600;
  color: #5a3c00;
}

.select-hint {
  font-size: 12px;
  color: #9b5d00;
}

.account-select {
  width: 100%;
}

.select-prefix-icon {
  font-size: 14px;
  color: #c57a00;
}

/* 操作卡 */
.action-card {
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid rgba(255, 214, 107, 0.35);
  border-radius: 20px;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-summary {
  padding: 14px 18px;
  background: rgba(255, 245, 214, 0.5);
  border-radius: 12px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 0;
}

.summary-row > span:first-child {
  font-size: 14px;
  color: #9b5d00;
}

.summary-value {
  font-size: 20px;
  font-weight: 700;
  color: #f05b2c;
}

.summary-sub-value {
  font-size: 16px;
  font-weight: 700;
  color: #c57a00;
}

.summary-row-total {
  margin-top: 4px;
  padding-top: 12px;
  border-top: 1px dashed rgba(226, 213, 168, 0.9);
}

.summary-total-value {
  font-size: 24px;
  font-weight: 800;
  color: #ef4444;
}

.price-guide-card {
  padding: 18px 18px 6px;
  border-radius: 14px;
  background: rgba(255, 245, 214, 0.35);
  border: 1px dashed rgba(255, 196, 32, 0.35);
}

.guide-title {
  color: #ef4444;
  font-size: 14px;
  font-weight: 700;
  margin-bottom: 14px;
}

.guide-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.guide-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-radius: 12px;
  padding: 12px 14px;
  background: rgba(255, 255, 255, 0.92);
}

.guide-item span {
  color: #5a3c00;
  font-weight: 600;
}

.guide-item strong {
  color: #ef4444;
}

.submit-btn {
  background: linear-gradient(135deg, #ffe057 0%, #ffc420 100%) !important;
  border: none !important;
  color: #5a3c00 !important;
  font-size: 16px !important;
  font-weight: 700 !important;
  letter-spacing: 2px;
  padding: 14px !important;
  border-radius: 14px !important;
  box-shadow: 0 6px 20px rgba(255, 196, 32, 0.35);
  transition: all 0.2s;
  width: 100%;
}

.submit-btn:hover {
  box-shadow: 0 8px 24px rgba(255, 196, 32, 0.45) !important;
  transform: translateY(-1px);
}

.submit-tip {
  text-align: center;
  font-size: 12px;
  color: #9b5d00;
  margin: 0;
}

/* 成功提示 */
.success-card {
  display: flex;
  align-items: center;
  gap: 16px;
  background: rgba(34, 197, 94, 0.08);
  border: 1px solid rgba(34, 197, 94, 0.25);
  border-radius: 20px;
  padding: 20px 24px;
}

.success-icon {
  width: 44px;
  height: 44px;
  color: #4ade80;
  flex-shrink: 0;
}

.success-icon svg {
  width: 100%;
  height: 100%;
}

.success-body {
  flex: 1;
}

.success-body h3 {
  margin: 0 0 4px;
  font-size: 16px;
  font-weight: 700;
  color: #16a34a;
}

.success-body p {
  margin: 0;
  font-size: 13px;
  color: #86efac;
  line-height: 1.5;
}

.success-close {
  background: none;
  border: none;
  color: #94a3b8;
  font-size: 22px;
  cursor: pointer;
  padding: 4px;
  line-height: 1;
}

.success-close:hover { color: #16a34a; }

/* 动画 */
.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.3s ease;
}

.slide-up-enter-from,
.slide-up-leave-to {
  opacity: 0;
  transform: translateY(16px);
}

/* Element Plus 覆盖 */
:deep(.el-select__wrapper) {
  background: #fff !important;
  border-radius: 12px !important;
  box-shadow: 0 0 0 1px #e2d5a8 inset !important;
  padding: 10px 14px !important;
  min-height: 48px !important;
}

:deep(.el-select__placeholder) {
  color: #94a3b8 !important;
  font-size: 14px !important;
}

:deep(.el-select__selected-item) {
  font-size: 14px !important;
  color: #3c2b00 !important;
}

/* 下拉选项 */
.option-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4px 0;
}

.option-name {
  font-size: 14px;
  color: #3c2b00;
}

.option-meta {
  display: flex;
  gap: 8px;
  font-size: 12px;
  color: #9b5d00;
}

.option-price {
  color: #f05b2c;
  font-weight: 600;
}

@media (max-width: 640px) {
  .page-shell { padding: 24px 16px; }
  .hero-card { padding: 24px 20px; }
  .product-card { grid-template-columns: 1fr; }
  .product-cover { min-height: 180px; }
  .cover-placeholder { min-height: 180px; }
  .product-attrs { grid-template-columns: 1fr; }
  .guide-grid { grid-template-columns: 1fr; }
}
</style>
