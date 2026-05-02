<template>
  <div class="page-shell stack">

    <!-- Hero 顶栏 -->
    <section class="hero-card">
      <div class="hero-left">
        <h1 class="page-title">账号租赁大厅</h1>
        <p class="page-subtitle">浏览所有可租账号，选择心仪套餐，快速完成租赁</p>
      </div>
      <div class="hero-stat">
        <span class="stat-num">{{ products.length }}</span>
        <span class="stat-label">个账号在架</span>
      </div>
    </section>

    <!-- 筛选工具栏 -->
    <section class="filter-bar">
      <div class="filter-row">
        <el-input
          v-model="keyword"
          placeholder="搜索账号名称"
          class="filter-input"
          clearable
          @input="onKeywordChange"
        />
        <el-select v-model="selectedTags" multiple collapse-tags collapse-tags-tooltip placeholder="标签筛选" class="filter-select">
          <el-option v-for="tag in allTags" :key="tag" :label="tag" :value="tag" />
        </el-select>
        <el-select v-model="selectedLevel" placeholder="装备等级" clearable class="filter-select">
          <el-option label="入门" value="Basic" />
          <el-option label="中级" value="Mid" />
          <el-option label="高级" value="Advanced" />
          <el-option label="满配" value="Full" />
        </el-select>
        <el-select v-model="selectedStatus" placeholder="可租状态" clearable class="filter-select">
          <el-option label="仅看可租" value="AVAILABLE" />
          <el-option label="全部" value="" />
        </el-select>
        <el-select v-model="sortBy" class="filter-select sort-select">
          <el-option label="默认排序" value="default" />
          <el-option label="价格 ↑ 低到高" value="price_asc" />
          <el-option label="价格 ↓ 高到低" value="price_desc" />
        </el-select>
        <el-button class="refresh-btn" @click="loadRentals">刷新</el-button>
      </div>
    </section>

    <!-- 账号卡片网格 -->
    <section class="cards-section">
      <!-- 骨架屏 -->
      <div v-if="loading" class="product-grid">
        <div v-for="i in 6" :key="i" class="product-card skeleton-card">
          <div class="sk-pulse sk-line" style="height:14px; width:40%; margin-bottom:10px;" />
          <div class="sk-pulse sk-line" style="height:18px; width:75%; margin-bottom:6px;" />
          <div class="sk-pulse sk-line" style="height:12px; width:90%; margin-bottom:5px;" />
          <div class="sk-pulse sk-line" style="height:12px; width:85%; margin-bottom:5px;" />
          <div class="sk-pulse sk-line" style="height:12px; width:70%; margin-bottom:16px;" />
          <div class="sk-pulse sk-block" style="height:32px; width:100%;" />
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else-if="filteredProducts.length === 0" class="empty-state">
        <div class="empty-icon">📭</div>
        <div class="empty-title">暂无可租账号</div>
        <div class="empty-desc">当前没有符合条件的账号</div>
        <el-button class="empty-btn" @click="clearFilters">清除筛选条件</el-button>
      </div>

      <!-- 账号卡片列表 -->
      <div v-else class="product-grid">
        <div
          v-for="p in filteredProducts"
          :key="p.id"
          class="product-card"
          @click="openDrawer(p)"
        >
          <!-- 卡片头部：状态标签 -->
          <div class="card-header">
            <div class="card-tags">
              <span class="card-status-tag" :class="getStatusClass(p.status)">
                {{ getStatusText(p.status) }}
              </span>
              <span v-if="p.isHot" class="card-hot-tag">热门</span>
            </div>
          </div>

          <!-- 账号名称 -->
          <div class="card-name">{{ p.name }}</div>

          <!-- 标签行 -->
          <div class="card-tag-row">
            <span v-for="(tag, i) in splitTags(p.tagText)" :key="i" class="card-tag-pill">
              {{ tag }}
            </span>
          </div>

          <!-- 资源属性 -->
          <div class="card-attrs">
            <div class="attr-row">
              <span class="attr-k">装备等级</span>
              <span class="attr-v">{{ p.equipmentLevelText }}</span>
            </div>
            <div class="attr-row">
              <span class="attr-k">仓库价值</span>
              <span class="attr-v">{{ p.warehouseValueText }}</span>
            </div>
            <div class="attr-row">
              <span class="attr-k">哈夫币</span>
              <span class="attr-v">{{ p.coinAmountText }}</span>
            </div>
          </div>

          <!-- 价格 + 操作 -->
          <div class="card-footer">
            <div class="card-price">
              <span class="price-num">¥{{ p.hourPrice }}</span>
              <span class="price-unit">/小时</span>
            </div>
            <span class="card-action">查看详情 →</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 分页 -->
    <div v-if="!loading && filteredProducts.length > 0" class="pagination-bar">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        background
      />
    </div>

    <!-- 账号详情弹窗 -->
    <el-dialog
      v-model="drawerVisible"
      title=""
      width="580px"
      class="account-modal"
      :show-close="false"
      align-center
    >
      <template v-if="selectedProduct">
        <div class="modal-inner">
          <!-- 关闭按钮 -->
          <button class="modal-close" @click="drawerVisible = false">✕</button>

          <!-- 图片区（预留） -->
          <div class="modal-image-placeholder">
            <div class="img-placeholder-icon">🎮</div>
            <div class="img-placeholder-text">账号截图示意</div>
          </div>

          <!-- 账号基本信息 -->
          <div class="modal-header">
            <div class="modal-name">{{ selectedProduct.name }}</div>
            <div class="modal-tags">
              <span v-for="(tag, i) in splitTags(selectedProduct.tagText)" :key="i" class="card-tag-pill">
                {{ tag }}
              </span>
              <span class="card-status-tag" :class="getStatusClass(selectedProduct.status)">
                {{ getStatusText(selectedProduct.status) }}
              </span>
            </div>
          </div>

          <!-- 分隔线 -->
          <div class="modal-divider" />

          <!-- 账号资源 -->
          <div class="modal-section">
            <div class="modal-section-title">账号资源</div>
            <div class="modal-resource-table">
              <div class="res-row">
                <span class="res-k">装备等级</span>
                <span class="res-v">{{ selectedProduct.equipmentLevelText }}</span>
              </div>
              <div class="res-row">
                <span class="res-k">仓库价值</span>
                <span class="res-v">{{ selectedProduct.warehouseValueText }}</span>
              </div>
              <div class="res-row">
                <span class="res-k">哈夫币</span>
                <span class="res-v">{{ selectedProduct.coinAmountText }}</span>
              </div>
              <div class="res-row">
                <span class="res-k">分类</span>
                <span class="res-v">{{ selectedProduct.category }}</span>
              </div>
            </div>
          </div>

          <!-- 租赁说明 -->
          <div v-if="selectedProduct.description" class="modal-section">
            <div class="modal-section-title">租赁说明</div>
            <p class="modal-desc">{{ selectedProduct.description }}</p>
          </div>

          <!-- 选择时长 -->
          <div class="modal-section">
            <div class="modal-section-title">选择租赁时长</div>
            <div class="duration-grid">
              <div
                v-for="d in durations"
                :key="d.hours"
                class="duration-card"
                :class="{ active: selectedDuration === d.hours, disabled: selectedProduct.status !== 'AVAILABLE' }"
                @click="selectDuration(d.hours)"
              >
                <div class="d-hours">{{ d.hours }}h</div>
                <div class="d-price">¥{{ calcPrice(d.hours) }}</div>
                <div v-if="d.discount < 1" class="d-tag">{{ Math.round(d.discount * 10) }}折</div>
              </div>
            </div>
            <div v-if="selectedDuration" class="price-summary">
              应付金额：<span class="summary-price">¥{{ calcPrice(selectedDuration) }}</span>
              <span class="summary-info">（{{ selectedDuration }}小时{{ getDiscount(selectedDuration) < 1 ? '，' + Math.round(getDiscount(selectedDuration) * 10) + '折' : '' }}）</span>
            </div>
          </div>

          <!-- 下单按钮 -->
          <div class="modal-footer">
            <el-button
              v-if="auth.isLoggedIn"
              type="primary"
              class="order-btn"
              :disabled="selectedProduct.status !== 'AVAILABLE' || !selectedDuration"
              @click="goToOrder"
            >
              立即下单
            </el-button>
            <el-button v-else type="primary" class="order-btn" @click="goToLogin">
              登录后下单
            </el-button>
          </div>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { getRentals } from '@/api';
import { useAuthStore } from '@/stores/auth';
import type { RentalProduct } from '@/types/api';

const auth = useAuthStore();
const router = useRouter();

// ---- 数据状态 ----
const loading = ref(false);
const products = ref<RentalProduct[]>([]);
const keyword = ref('');
const selectedTags = ref<string[]>([]);
const selectedLevel = ref('');
const selectedStatus = ref('');
const sortBy = ref('default');
const currentPage = ref(1);
const pageSize = ref(12);
const total = computed(() => filteredList.value.length);

// ---- 详情 Drawer ----
const drawerVisible = ref(false);
const selectedProduct = ref<RentalProduct | null>(null);
const selectedDuration = ref<number | null>(null);

// ---- 时长选项 ----
const durations = [
  { hours: 1, discount: 1 },
  { hours: 6, discount: 0.9 },
  { hours: 12, discount: 0.8 },
  { hours: 24, discount: 0.7 }
];

function calcPrice(hours: number) {
  if (!selectedProduct.value) return 0;
  const d = durations.find(x => x.hours === hours);
  const discount = d ? d.discount : 1;
  return (selectedProduct.value.hourPrice * hours * discount).toFixed(2);
}

function getDiscount(hours: number) {
  return durations.find(x => x.hours === hours)?.discount ?? 1;
}

function selectDuration(h: number) {
  if (selectedProduct.value?.status !== 'AVAILABLE') return;
  selectedDuration.value = selectedDuration.value === h ? null : h;
}

// ---- 所有标签聚合 ----
const allTags = computed(() => {
  const tags = new Set<string>();
  products.value.forEach(p => {
    (p.tagText ?? '').split(',').forEach(t => { const t2 = t.trim(); if (t2) tags.add(t2); });
  });
  return Array.from(tags);
});

// ---- 筛选 + 排序 ----
const filteredList = computed(() => {
  let list = [...products.value];

  if (keyword.value) {
    const kw = keyword.value.toLowerCase();
    list = list.filter(p =>
      p.name.toLowerCase().includes(kw) ||
      (p.tagText ?? '').toLowerCase().includes(kw) ||
      (p.equipmentLevelText ?? '').toLowerCase().includes(kw)
    );
  }

  if (selectedTags.value.length) {
    list = list.filter(p =>
      selectedTags.value.every(st =>
        (p.tagText ?? '').split(',').map(t => t.trim()).includes(st)
      )
    );
  }

  if (selectedLevel.value) {
    list = list.filter(p => (p.equipmentLevelText ?? '').includes(selectedLevel.value));
  }

  if (selectedStatus.value) {
    list = list.filter(p => p.status === selectedStatus.value);
  }

  if (sortBy.value === 'price_asc') {
    list.sort((a, b) => a.hourPrice - b.hourPrice);
  } else if (sortBy.value === 'price_desc') {
    list.sort((a, b) => b.hourPrice - a.hourPrice);
  }

  return list;
});

// 分页
const filteredProducts = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return filteredList.value.slice(start, start + pageSize.value);
});

function clearFilters() {
  keyword.value = '';
  selectedTags.value = [];
  selectedLevel.value = '';
  selectedStatus.value = '';
  sortBy.value = 'default';
}

// ---- 加载数据 ----
async function loadRentals() {
  loading.value = true;
  try {
    const res = await getRentals({ keyword: keyword.value || undefined });
    if (res.data.success) {
      products.value = res.data.data;
    } else {
      ElMessage.error(res.data.message || '账号列表加载失败');
    }
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '账号列表加载失败');
  } finally {
    loading.value = false;
  }
}

// ---- 详情 Drawer ----
function openDrawer(p: RentalProduct) {
  selectedProduct.value = p;
  selectedDuration.value = null;
  drawerVisible.value = true;
}

// ---- 跳转下单 ----
function goToOrder() {
  if (!selectedProduct.value) return;
  router.push({
    path: '/orders/create',
    query: { accountId: String(selectedProduct.value.id) }
  });
  drawerVisible.value = false;
}

function goToLogin() {
  router.push({ path: '/login', query: { redirect: '/orders/create?accountId=' + selectedProduct.value?.id } });
  drawerVisible.value = false;
}

// ---- 工具函数 ----
function splitTags(tagText: string | undefined): string[] {
  return (tagText ?? '').split(',').map(t => t.trim()).filter(Boolean);
}

function getStatusText(status: string) {
  return { AVAILABLE: '可租', RENTED: '已租出', MAINTENANCE: '维护中' }[status] ?? status;
}

function getStatusClass(status: string) {
  return { AVAILABLE: 'status-available', RENTED: 'status-rented', MAINTENANCE: 'status-maintenance' }[status] ?? '';
}

// ---- 防抖搜索 ----
let debounceTimer: ReturnType<typeof setTimeout> | null = null;
function onKeywordChange() {
  if (debounceTimer) clearTimeout(debounceTimer);
  debounceTimer = setTimeout(() => {
    currentPage.value = 1;
    loadRentals();
  }, 400);
}

// ---- 重置分页 ----
watch([selectedTags, selectedLevel, selectedStatus, sortBy], () => {
  currentPage.value = 1;
});

onMounted(loadRentals);
</script>

<script lang="ts">
export default { name: 'RentalListView' };
</script>

<style scoped>
/* ==================== 页面整体 ==================== */
.page-shell {
  min-height: 100vh;
  background: linear-gradient(135deg, #060d1a 0%, #0f1c33 50%, #0a1525 100%);
  color: #e2e8f0;
  padding: 24px;
}

/* ==================== Hero 顶栏 ==================== */
.hero-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(96, 165, 250, 0.12);
  border-radius: 20px;
  padding: 32px 36px;
  margin-bottom: 20px;
}

.hero-left { display: flex; flex-direction: column; gap: 8px; }

.page-title {
  margin: 0;
  font-size: 28px;
  font-weight: 800;
  color: #f1f5f9;
  letter-spacing: 2px;
}

.page-subtitle {
  margin: 0;
  font-size: 14px;
  color: #64748b;
}

.hero-stat {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.stat-num {
  font-size: 36px;
  font-weight: 800;
  color: #60a5fa;
  line-height: 1;
}

.stat-label {
  font-size: 12px;
  color: #64748b;
  margin-top: 4px;
}

/* ==================== 筛选工具栏 ==================== */
.filter-bar {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(96, 165, 250, 0.1);
  border-radius: 16px;
  padding: 16px 20px;
  margin-bottom: 20px;
}

.filter-row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.filter-input { width: 200px; }
.filter-select { width: 150px; }
.sort-select { width: 160px; }

.refresh-btn {
  background: rgba(96, 165, 250, 0.1) !important;
  border: 1px solid rgba(96, 165, 250, 0.25) !important;
  color: #60a5fa !important;
  margin-left: auto;
}

.refresh-btn:hover {
  background: rgba(96, 165, 250, 0.2) !important;
}

/* ==================== 账号卡片网格 ==================== */
.cards-section { min-height: 200px; }

.product-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.product-card {
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(96, 165, 250, 0.1);
  border-radius: 16px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.25s;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.product-card:hover {
  transform: translateY(-4px);
  background: rgba(96, 165, 250, 0.08);
  border-color: rgba(96, 165, 250, 0.3);
  box-shadow: 0 8px 24px rgba(96, 165, 250, 0.12);
}

.product-card.skeleton-card { cursor: default; }
.product-card.skeleton-card:hover { transform: none; box-shadow: none; }

/* 卡片头部 */
.card-header { display: flex; align-items: center; }
.card-tags { display: flex; gap: 6px; flex-wrap: wrap; }

.card-status-tag {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 8px;
}

.status-available {
  background: rgba(34, 197, 94, 0.12);
  border: 1px solid rgba(34, 197, 94, 0.3);
  color: #4ade80;
}

.status-rented {
  background: rgba(251, 191, 36, 0.12);
  border: 1px solid rgba(251, 191, 36, 0.3);
  color: #fbbf24;
}

.status-maintenance {
  background: rgba(100, 116, 139, 0.12);
  border: 1px solid rgba(100, 116, 139, 0.3);
  color: #94a3b8;
}

.card-hot-tag {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 8px;
  background: rgba(239, 68, 68, 0.12);
  border: 1px solid rgba(239, 68, 68, 0.3);
  color: #f87171;
}

/* 账号名称 */
.card-name {
  font-size: 15px;
  font-weight: 600;
  color: #f1f5f9;
  line-height: 1.4;
}

/* 标签行 */
.card-tag-row { display: flex; gap: 6px; flex-wrap: wrap; }

.card-tag-pill {
  font-size: 11px;
  padding: 2px 10px;
  border-radius: 12px;
  background: rgba(96, 165, 250, 0.1);
  border: 1px solid rgba(96, 165, 250, 0.18);
  color: #93c5fd;
}

/* 资源属性 */
.card-attrs { display: flex; flex-direction: column; gap: 5px; }

.attr-row {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
}

.attr-k { color: #64748b; }
.attr-v { color: #94a3b8; }

/* 价格行 */
.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 10px;
  border-top: 1px solid rgba(96, 165, 250, 0.08);
}

.card-price { display: flex; align-items: baseline; gap: 2px; }
.price-num { font-size: 18px; font-weight: 700; color: #fbbf24; }
.price-unit { font-size: 12px; color: #64748b; }

.card-action {
  font-size: 12px;
  color: #60a5fa;
  transition: opacity 0.2s;
}

.product-card:hover .card-action { opacity: 0.75; }

/* 骨架屏 */
.sk-pulse {
  background: linear-gradient(90deg, rgba(255,255,255,0.04) 0%, rgba(255,255,255,0.08) 50%, rgba(255,255,255,0.04) 100%);
  background-size: 200% 100%;
  border-radius: 6px;
  animation: sk-shimmer 1.6s infinite;
}

@keyframes sk-shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

.sk-line { display: block; }
.sk-block { display: block; }

/* 空状态 */
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

/* 分页 */
.pagination-bar {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

/* ==================== 详情弹窗 Modal ==================== */
:deep(.el-overlay) {
  background: rgba(6, 13, 26, 0.75) !important;
  backdrop-filter: blur(4px);
}

:deep(.el-dialog) {
  position: fixed !important;
  left: 50% !important;
  top: 50% !important;
  transform: translate(-50%, -50%) !important;
  background: #0f1c33 !important;
  border: 1px solid rgba(96, 165, 250, 0.2) !important;
  border-radius: 20px !important;
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.7) !important;
  padding: 0 !important;
  margin: 0 !important;
  max-width: 580px !important;
  width: 580px !important;
}

:deep(.el-dialog__header) { display: none !important; }
:deep(.el-dialog__body) { padding: 0 !important; }
:deep(.el-dialog__footer) { display: none !important; }

.modal-inner {
  padding: 28px 28px 24px;
  max-height: 85vh;
  overflow-y: auto;
  box-sizing: border-box;
  position: relative;
}

.modal-close {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(96, 165, 250, 0.15);
  color: #64748b;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  z-index: 1;
}

.modal-close:hover { background: rgba(255, 255, 255, 0.12); color: #f1f5f9; }

/* 图片占位 */
.modal-image-placeholder {
  width: 100%;
  height: 160px;
  border-radius: 14px;
  background: rgba(96, 165, 250, 0.06);
  border: 1px solid rgba(96, 165, 250, 0.12);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 20px;
}

.img-placeholder-icon { font-size: 40px; }
.img-placeholder-text { font-size: 12px; color: #475569; }

/* 账号信息头部 */
.modal-header { margin-bottom: 16px; }

.modal-name {
  font-size: 20px;
  font-weight: 700;
  color: #f1f5f9;
  margin-bottom: 10px;
  letter-spacing: 1px;
}

.modal-tags { display: flex; gap: 6px; flex-wrap: wrap; }

/* 分隔线 */
.modal-divider {
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(96, 165, 250, 0.15), transparent);
  margin-bottom: 20px;
}

/* 区块 */
.modal-section { margin-bottom: 20px; }

.modal-section-title {
  font-size: 13px;
  font-weight: 600;
  color: #60a5fa;
  letter-spacing: 1px;
  margin-bottom: 12px;
}

/* 资源表格 */
.modal-resource-table { display: flex; flex-direction: column; gap: 8px; }

.res-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 14px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 10px;
  border: 1px solid rgba(96, 165, 250, 0.08);
}

.res-k { font-size: 12px; color: #64748b; }
.res-v { font-size: 13px; font-weight: 500; color: #e2e8f0; }

/* 说明 */
.modal-desc {
  font-size: 13px;
  color: #94a3b8;
  line-height: 1.7;
  margin: 0;
  padding: 12px 14px;
  background: rgba(96, 165, 250, 0.04);
  border-radius: 10px;
  border: 1px solid rgba(96, 165, 250, 0.08);
}

/* 时长选择 */
.duration-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
  margin-bottom: 14px;
}

.duration-card {
  padding: 12px 8px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(96, 165, 250, 0.12);
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.duration-card:hover { background: rgba(96, 165, 250, 0.08); border-color: rgba(96, 165, 250, 0.3); }

.duration-card.active {
  background: rgba(30, 64, 175, 0.2) !important;
  border-color: #1e40af !important;
  box-shadow: 0 0 12px rgba(30, 64, 175, 0.3);
}

.duration-card.disabled { opacity: 0.4; cursor: not-allowed; }
.duration-card.disabled:hover { background: rgba(255,255,255,0.03); border-color: rgba(96,165,250,0.12); }

.d-hours { font-size: 14px; font-weight: 700; color: #f1f5f9; margin-bottom: 4px; }
.d-price { font-size: 13px; font-weight: 600; color: #fbbf24; }
.d-tag {
  position: absolute;
  top: -6px;
  right: -4px;
  font-size: 9px;
  padding: 1px 5px;
  border-radius: 6px;
  background: rgba(239, 68, 68, 0.8);
  color: #fff;
}

.price-summary {
  text-align: center;
  font-size: 14px;
  color: #94a3b8;
  padding: 10px;
  background: rgba(96, 165, 250, 0.05);
  border-radius: 10px;
  border: 1px solid rgba(96, 165, 250, 0.1);
}

.summary-price { font-size: 18px; font-weight: 700; color: #fbbf24; }
.summary-info { font-size: 12px; color: #64748b; }

/* 下单按钮 */
.modal-footer { margin-top: 8px; }

.order-btn {
  width: 100%;
  background: linear-gradient(135deg, #1e40af 0%, #1e3a8a 100%) !important;
  border: none !important;
  color: #fff !important;
  padding: 12px !important;
  border-radius: 12px !important;
  font-size: 15px !important;
  font-weight: 600 !important;
  box-shadow: 0 4px 14px rgba(30, 64, 175, 0.4);
}

.order-btn:hover:not(:disabled) {
  box-shadow: 0 6px 18px rgba(30, 64, 175, 0.5) !important;
  transform: translateY(-1px);
}

.order-btn:disabled {
  background: rgba(255,255,255,0.06) !important;
  color: #475569 !important;
  box-shadow: none !important;
}

/* ==================== Element Plus 深色覆盖 ==================== */
:deep(.el-input__wrapper) {
  background: #1e293b !important;
  border-color: rgba(96, 165, 250, 0.2) !important;
  box-shadow: none !important;
}

:deep(.el-input__inner) { color: #e2e8f0 !important; }
:deep(.el-input__inner::placeholder) { color: #475569 !important; }
:deep(.el-input__suffix) { color: #64748b !important; }

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
:deep(.el-select__tags) { display: none; }
:deep(.el-select-dropdown) {
  background: #1e293b !important;
  border: 1px solid rgba(96, 165, 250, 0.2) !important;
  border-radius: 12px !important;
}

:deep(.el-select-dropdown__item) { color: #e2e8f0 !important; }
:deep(.el-select-dropdown__item.hover),
:deep(.el-select-dropdown__item:hover) { background: rgba(96, 165, 250, 0.1) !important; }
:deep(.el-select-dropdown__item.is-selected) { color: #60a5fa !important; background: rgba(96, 165, 250, 0.08) !important; }
:deep(.el-select-dropdown__item.is-hovering) { background: rgba(96, 165, 250, 0.05) !important; }

:deep(.el-pagination) { --el-pagination-bg-color: transparent !important; }
:deep(.el-pagination button) { background: rgba(255,255,255,0.05) !important; border-color: rgba(96,165,250,0.15) !important; color: #94a3b8 !important; }
:deep(.el-pagination button:hover) { background: rgba(96,165,250,0.15) !important; color: #60a5fa !important; }
:deep(.el-pager li) { background: rgba(255,255,255,0.05) !important; border-color: rgba(96,165,250,0.15) !important; color: #94a3b8 !important; }
:deep(.el-pager li:hover) { color: #60a5fa !important; }
:deep(.el-pager li.is-active) { background: #1e40af !important; border-color: #1e40af !important; color: #fff !important; }

/* ==================== 响应式 ==================== */
@media (max-width: 900px) {
  .product-grid { grid-template-columns: repeat(2, 1fr); }
  .filter-row { flex-direction: column; align-items: stretch; }
  .filter-input, .filter-select, .sort-select { width: 100%; }
  .refresh-btn { margin-left: 0; }
  .hero-card { flex-direction: column; align-items: flex-start; gap: 16px; }
  .hero-stat { align-items: flex-start; }
}

@media (max-width: 600px) {
  .product-grid { grid-template-columns: 1fr; }
  .duration-grid { grid-template-columns: repeat(2, 1fr); }
  .page-shell { padding: 12px; }
  .hero-card { padding: 20px 16px; }
  .page-title { font-size: 22px; }
}
</style>