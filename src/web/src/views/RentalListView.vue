<template>
  <div class="rental-page">
    <div class="page-inner">
      <section class="hero-board">
        <aside class="hero-side">
          <article
            v-for="promo in sidePromos"
            :key="promo.title"
            class="promo-card"
            :class="promo.theme"
          >
            <div class="promo-badge">{{ promo.badge }}</div>
            <h3>{{ promo.title }}</h3>
            <p>{{ promo.desc }}</p>
            <button class="promo-action" type="button" @click="handlePromoAction(promo.action)">
              {{ promo.actionText }}
            </button>
          </article>
        </aside>

        <section class="hero-banner">
          <div class="banner-mark">DeltaRent</div>
          <div class="banner-content">
            <div class="banner-eyebrow">{{ currentBanner.eyebrow }}</div>
            <h1>{{ currentBanner.title }}</h1>
            <p>{{ currentBanner.desc }}</p>
            <div class="banner-actions">
              <button class="banner-primary" type="button" @click="handleBannerAction(currentBanner.action)">
                {{ currentBanner.actionText }}
              </button>
              <button class="banner-secondary" type="button" @click="advancedVisible = true">
                高级筛选
              </button>
            </div>
          </div>
          <div class="banner-stats">
            <div class="banner-stat">
              <span class="banner-stat-value">{{ availableCount }}</span>
              <span class="banner-stat-label">可租账号</span>
            </div>
            <div class="banner-stat">
              <span class="banner-stat-value">{{ totalLoaded }}</span>
              <span class="banner-stat-label">当前资源</span>
            </div>
            <div class="banner-stat">
              <span class="banner-stat-value">{{ hotCount }}</span>
              <span class="banner-stat-label">热门推荐</span>
            </div>
          </div>
          <div class="banner-dots">
            <button
              v-for="(_, index) in bannerSlides"
              :key="index"
              class="banner-dot"
              :class="{ active: currentBannerIndex === index }"
              type="button"
              @click="currentBannerIndex = index"
            />
          </div>
        </section>

        <aside class="notice-panel">
          <div class="notice-head">
            <span class="notice-icon">公告</span>
            <strong>租号大厅说明</strong>
          </div>
          <button
            v-for="notice in noticeItems"
            :key="notice.id"
            class="notice-item"
            type="button"
            @click="openNotice(notice)"
          >
            <span class="notice-title">{{ notice.title }}</span>
            <span class="notice-arrow">›</span>
          </button>
        </aside>
      </section>

      <section class="zone-strip">
        <button
          v-for="zone in zoneCards"
          :key="zone.key"
          class="zone-card"
          :class="[zone.theme, { active: activeZone === zone.key }]"
          type="button"
          @click="applyZone(zone.key)"
        >
          <div class="zone-copy">
            <strong>{{ zone.title }}</strong>
            <span>{{ zone.desc }}</span>
          </div>
          <div class="zone-meta">
            <span class="zone-count">{{ zone.count }}</span>
            <span class="zone-go">GO</span>
          </div>
        </button>
      </section>

      <section class="toolbar-panel">
        <div class="toolbar-top">
          <div class="sort-tabs">
            <button
              v-for="option in sortOptions"
              :key="option.value"
              class="sort-tab"
              :class="{ active: sortBy === option.value }"
              type="button"
              @click="sortBy = option.value"
            >
              {{ option.label }}
            </button>
          </div>
          <div class="toolbar-ops">
            <button class="ghost-op" type="button" @click="toggleAvailableOnly">
              {{ selectedStatus === 'AVAILABLE' ? '取消秒上号' : '秒上号' }}
            </button>
            <button class="ghost-op danger" type="button" @click="clearFilters">
              重置筛选
            </button>
            <button class="ghost-op" type="button" @click="toggleDisplayMode">
              切换{{ displayMode === 'list' ? '卡片' : '列表' }}
            </button>
            <button class="primary-op" type="button" @click="advancedVisible = true">
              高级筛选
            </button>
          </div>
        </div>

        <div class="filter-grid">
          <el-input
            v-model="keyword"
            placeholder="搜索账号名称、分类、标签"
            clearable
            class="filter-item keyword-item"
          />
          <el-select v-model="selectedCategory" clearable placeholder="账号分类" class="filter-item">
            <el-option label="全部分类" value="" />
            <el-option
              v-for="category in allCategories"
              :key="category"
              :label="categoryText(category)"
              :value="category"
            />
          </el-select>
          <el-select v-model="selectedLevel" clearable placeholder="装备等级" class="filter-item">
            <el-option label="入门" value="Basic" />
            <el-option label="进阶" value="Mid" />
            <el-option label="高配" value="Advanced" />
            <el-option label="满配" value="Full" />
          </el-select>
          <el-select v-model="selectedStatus" clearable placeholder="上号状态" class="filter-item">
            <el-option label="可租" value="AVAILABLE" />
            <el-option label="维护中" value="MAINTENANCE" />
            <el-option label="已租出" value="RENTED" />
          </el-select>
          <el-select
            v-model="selectedTags"
            multiple
            collapse-tags
            collapse-tags-tooltip
            clearable
            placeholder="资源标签"
            class="filter-item wide-item"
          >
            <el-option v-for="tag in allTags" :key="tag" :label="tag" :value="tag" />
          </el-select>
          <el-select v-model="selectedPriceRange" clearable placeholder="价格区间" class="filter-item">
            <el-option label="10 元以下" value="low" />
            <el-option label="10 - 20 元" value="mid" />
            <el-option label="20 元以上" value="high" />
          </el-select>
        </div>

        <div class="filter-summary">
          <div class="summary-left">
            <span class="summary-text">共筛出 {{ total }} 个账号</span>
            <span v-if="activeZone !== 'all'" class="summary-chip highlighted">{{ currentZoneLabel }}</span>
            <span v-if="selectedCategory" class="summary-chip">{{ categoryText(selectedCategory) }}</span>
            <span v-if="selectedStatus" class="summary-chip">{{ getStatusText(selectedStatus) }}</span>
            <span v-if="selectedPriceRange" class="summary-chip">{{ priceRangeLabel(selectedPriceRange) }}</span>
            <span v-if="hasAdvancedFilters" class="summary-chip">高级筛选已启用</span>
          </div>
          <div class="summary-right">
            <span class="summary-view">{{ displayMode === 'list' ? '列表视图' : '卡片视图' }}</span>
          </div>
        </div>
      </section>

      <section class="list-panel">
        <template v-if="loading">
          <div v-if="displayMode === 'list'" class="row-list">
            <article v-for="i in 4" :key="i" class="row-card skeleton-row">
              <div class="skeleton-cover sk-pulse" />
              <div class="skeleton-body">
                <div class="sk-pulse sk-line w-40" />
                <div class="sk-pulse sk-line w-90" />
                <div class="skeleton-metrics">
                  <span v-for="n in 6" :key="n" class="sk-pulse sk-box" />
                </div>
              </div>
              <div class="skeleton-side">
                <div class="sk-pulse sk-line w-60" />
                <div class="sk-pulse sk-button" />
              </div>
            </article>
          </div>
          <div v-else class="card-grid">
            <article v-for="i in 8" :key="i" class="grid-card skeleton-grid">
              <div class="sk-pulse grid-cover-skeleton" />
              <div class="grid-body">
                <div class="sk-pulse sk-line w-50" />
                <div class="sk-pulse sk-line w-85" />
                <div class="sk-pulse sk-line w-70" />
              </div>
            </article>
          </div>
        </template>

        <div v-else-if="total === 0" class="empty-state">
          <div class="empty-icon">∅</div>
          <h3>当前没有符合条件的账号</h3>
          <p>可以先清空筛选，或者切换专区后重新查看。</p>
          <el-button type="warning" @click="clearFilters">清空筛选</el-button>
        </div>

        <div v-else-if="displayMode === 'list'" class="row-list">
          <article
            v-for="product in paginatedProducts"
            :key="product.id"
            class="row-card"
            @click="openDrawer(product)"
          >
            <div class="row-cover" :class="coverTheme(product)">
              <div class="cover-status" :class="statusTheme(product.status)">
                {{ getStatusText(product.status) }}
              </div>
              <div class="cover-category">{{ categoryText(product.category) }}</div>
              <div class="cover-name">{{ shortProductName(product.name) }}</div>
              <div class="cover-tags">
                <span v-for="tag in splitTags(product.tagText).slice(0, 3)" :key="tag">{{ tag }}</span>
              </div>
            </div>

            <div class="row-main">
              <h3 class="row-title">{{ product.name }}</h3>
              <p class="row-meta">
                {{ categoryText(product.category) }} ｜ {{ product.equipmentLevelText || '未填写装备等级' }} ｜ {{ product.description || '客服确认后交付，适合课堂演示完整租赁流程。' }}
              </p>

              <div class="metric-grid">
                <div class="metric-box emphasis">
                  <strong>{{ formatCoinAmount(product.coinAmount) }}</strong>
                  <span>哈夫币数量</span>
                </div>
                <div class="metric-box emphasis">
                  <strong>¥{{ formatPrice(product.hourPrice) }}</strong>
                  <span>小时单价</span>
                </div>
                <div class="metric-box">
                  <strong>{{ product.ratioText || '待补充' }}</strong>
                  <span>比例</span>
                </div>
                <div class="metric-box">
                  <strong>{{ product.insuranceBoxText || '待补充' }}</strong>
                  <span>保险箱</span>
                </div>
                <div class="metric-box">
                  <strong>{{ product.rankText || product.equipmentLevelText || '待补充' }}</strong>
                  <span>段位</span>
                </div>
                <div class="metric-box">
                  <strong>{{ product.staminaText || product.weightText || recommendText(product) }}</strong>
                  <span>体力 / 负重</span>
                </div>
              </div>
            </div>

            <div class="row-side">
              <div class="side-top">
                <span class="side-price-label">参考租金</span>
                <strong class="side-price">¥{{ formatPrice(product.hourPrice) }}</strong>
                <span class="side-price-unit">/ 小时</span>
              </div>
              <div class="side-tags">
                <span class="side-pill">{{ getStatusText(product.status) }}</span>
                <span v-if="product.isHot" class="side-pill hot">热门</span>
              </div>
              <div class="side-actions">
                <button class="time-chip" type="button">{{ durations.length }} 档时长</button>
                <button class="rent-btn" type="button" @click.stop="openDrawer(product)">立即查看</button>
              </div>
            </div>
          </article>
        </div>

        <div v-else class="card-grid">
          <article
            v-for="product in paginatedProducts"
            :key="product.id"
            class="grid-card"
            @click="openDrawer(product)"
          >
            <div class="grid-cover" :class="coverTheme(product)">
              <div class="grid-cover-top">
                <span class="grid-badge">{{ categoryText(product.category) }}</span>
                <span class="grid-status" :class="statusTheme(product.status)">{{ getStatusText(product.status) }}</span>
              </div>
              <div class="grid-cover-bottom">
                <strong>{{ formatCoinAmount(product.coinAmount) }}</strong>
                <span>资源概览</span>
              </div>
            </div>
            <div class="grid-body">
              <h3>{{ product.name }}</h3>
              <p>{{ product.description || '客服确认后交付，适合短租体验和课堂演示。' }}</p>
              <div class="grid-tags">
                <span v-for="tag in splitTags(product.tagText).slice(0, 4)" :key="tag">{{ tag }}</span>
              </div>
              <div class="grid-footer">
                <div class="grid-price">
                  <strong>¥{{ formatPrice(product.hourPrice) }}</strong>
                  <span>/ 小时</span>
                </div>
                <button class="grid-action" type="button">立即查看</button>
              </div>
            </div>
          </article>
        </div>
      </section>

      <div v-if="!loading && total > 0" class="pagination-wrap">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          background
        />
      </div>

      <el-dialog
        v-model="advancedVisible"
        width="760px"
        align-center
        class="advanced-dialog"
        :show-close="false"
      >
        <div class="advanced-panel">
          <div class="advanced-head">
            <div>
              <h3>高级筛选</h3>
              <p>基于现有账号字段做组合过滤，适合快速收敛结果。</p>
            </div>
            <button class="dialog-close" type="button" @click="advancedVisible = false">×</button>
          </div>

          <div class="advanced-grid">
            <div class="advanced-field">
              <label>哈夫币区间</label>
              <div class="range-row">
                <el-input-number v-model="advancedForm.minCoin" :min="0" :controls="false" placeholder="最低值" />
                <span>至</span>
                <el-input-number v-model="advancedForm.maxCoin" :min="0" :controls="false" placeholder="最高值" />
              </div>
            </div>

            <div class="advanced-field">
              <label>价格区间</label>
              <div class="range-row">
                <el-input-number v-model="advancedForm.minPrice" :min="0" :controls="false" placeholder="最低值" />
                <span>至</span>
                <el-input-number v-model="advancedForm.maxPrice" :min="0" :controls="false" placeholder="最高值" />
              </div>
            </div>

            <div class="advanced-field">
              <label>仓库关键字</label>
              <el-input v-model="advancedForm.warehouseKeyword" placeholder="例如：高价值、满配、金装" clearable />
            </div>

            <div class="advanced-field">
              <label>段位关键字</label>
              <el-input v-model="advancedForm.rankKeyword" placeholder="例如：黑鹰、北极星、赛伊德" clearable />
            </div>

            <div class="advanced-field">
              <label>保险箱</label>
              <el-input v-model="advancedForm.insuranceBoxKeyword" placeholder="例如：4格、9格" clearable />
            </div>

            <div class="advanced-field">
              <label>常用地区</label>
              <el-input v-model="advancedForm.loginRegionKeyword" placeholder="例如：四川、福建、江西" clearable />
            </div>

            <div class="advanced-field">
              <label>附加标签</label>
              <el-select
                v-model="advancedForm.includeTags"
                multiple
                collapse-tags
                collapse-tags-tooltip
                clearable
                placeholder="按标签进一步收敛"
              >
                <el-option v-for="tag in allTags" :key="tag" :label="tag" :value="tag" />
              </el-select>
            </div>
          </div>

          <div class="advanced-foot">
            <button class="advanced-reset" type="button" @click="resetAdvancedFilters">重置筛选</button>
            <button class="advanced-confirm" type="button" @click="applyAdvancedFilters">确认</button>
          </div>
        </div>
      </el-dialog>

      <el-dialog
        v-model="drawerVisible"
        width="760px"
        align-center
        class="detail-dialog"
        :show-close="false"
      >
        <template v-if="selectedProduct">
          <div class="detail-panel">
            <button class="dialog-close detail-close" type="button" @click="drawerVisible = false">×</button>

            <div class="detail-top">
              <div class="detail-cover" :class="coverTheme(selectedProduct)">
                <div class="detail-cover-status" :class="statusTheme(selectedProduct.status)">
                  {{ getStatusText(selectedProduct.status) }}
                </div>
                <div class="detail-cover-category">{{ categoryText(selectedProduct.category) }}</div>
                <h3>{{ shortProductName(selectedProduct.name) }}</h3>
                <p>{{ selectedProduct.equipmentLevelText || '装备等级待补充' }}</p>
                <div class="detail-cover-tags">
                  <span v-for="tag in splitTags(selectedProduct.tagText).slice(0, 4)" :key="tag">{{ tag }}</span>
                </div>
              </div>

              <div class="detail-summary">
                <div class="detail-title-row">
                  <div>
                    <h2>{{ selectedProduct.name }}</h2>
                    <p>{{ selectedProduct.description || '客服确认后交付，适合课堂演示和短租体验。' }}</p>
                  </div>
                  <div class="detail-price-box">
                    <span>小时价</span>
                    <strong>¥{{ formatPrice(selectedProduct.hourPrice) }}</strong>
                  </div>
                </div>

                <div class="detail-metrics">
                  <div class="detail-metric">
                    <strong>{{ formatCoinAmount(selectedProduct.coinAmount) }}</strong>
                    <span>哈夫币</span>
                  </div>
                  <div class="detail-metric">
                    <strong>{{ selectedProduct.rankText || '待补充' }}</strong>
                    <span>段位</span>
                  </div>
                  <div class="detail-metric">
                    <strong>{{ selectedProduct.insuranceBoxText || '待补充' }}</strong>
                    <span>保险箱</span>
                  </div>
                  <div class="detail-metric">
                    <strong>{{ selectedProduct.staminaText || selectedProduct.weightText || '待补充' }}</strong>
                    <span>体力 / 负重</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="detail-block">
              <div class="block-title">账号资源</div>
              <div class="resource-grid">
                <div class="resource-item">
                  <span>分类</span>
                  <strong>{{ categoryText(selectedProduct.category) }}</strong>
                </div>
                <div class="resource-item">
                  <span>状态</span>
                  <strong>{{ getStatusText(selectedProduct.status) }}</strong>
                </div>
                <div class="resource-item">
                  <span>装备等级</span>
                  <strong>{{ selectedProduct.equipmentLevelText || '待补充' }}</strong>
                </div>
                <div class="resource-item">
                  <span>仓库估值</span>
                  <strong>{{ selectedProduct.warehouseValueText || '待补充' }}</strong>
                </div>
                <div class="resource-item">
                  <span>比例 / 保险箱</span>
                  <strong>{{ `${selectedProduct.ratioText || '待补充'} / ${selectedProduct.insuranceBoxText || '待补充'}` }}</strong>
                </div>
                <div class="resource-item">
                  <span>常用地区</span>
                  <strong>{{ selectedProduct.loginRegion || '待补充' }}</strong>
                </div>
                <div class="resource-item">
                  <span>武器皮肤</span>
                  <strong>{{ selectedProduct.weaponSkinText || '待补充' }}</strong>
                </div>
                <div class="resource-item">
                  <span>干员皮肤</span>
                  <strong>{{ selectedProduct.characterSkinText || recommendText(selectedProduct) }}</strong>
                </div>
              </div>
            </div>

            <div class="detail-block">
              <div class="block-title">选择租赁时长</div>
              <div class="duration-grid">
                <button
                  v-for="duration in durations"
                  :key="duration.hours"
                  class="duration-card"
                  :class="{ active: selectedDuration === duration.hours, disabled: selectedProduct.status !== 'AVAILABLE' }"
                  type="button"
                  @click="selectDuration(duration.hours)"
                >
                  <strong>{{ duration.hours }} 小时</strong>
                  <span>¥{{ calcPrice(duration.hours) }}</span>
                  <em v-if="duration.discount < 1">{{ Math.round(duration.discount * 10) }} 折</em>
                </button>
              </div>

              <div v-if="selectedDuration" class="detail-total">
                应付金额：<strong>¥{{ calcPrice(selectedDuration) }}</strong>
                <span>已选 {{ selectedDuration }} 小时</span>
              </div>
            </div>

            <div class="detail-actions">
              <el-button
                v-if="auth.isLoggedIn"
                type="warning"
                class="detail-order-btn"
                :disabled="selectedProduct.status !== 'AVAILABLE' || !selectedDuration"
                @click="goToOrder"
              >
                立即下单
              </el-button>
              <el-button v-else type="warning" class="detail-order-btn" @click="goToLogin">
                登录后下单
              </el-button>
            </div>
          </div>
        </template>
      </el-dialog>

      <el-dialog
        v-model="noticeVisible"
        width="520px"
        align-center
        class="notice-dialog"
        :show-close="false"
      >
        <template v-if="selectedNotice">
          <div class="notice-detail">
            <button class="dialog-close" type="button" @click="noticeVisible = false">×</button>
            <div class="notice-detail-badge">平台公告</div>
            <h3>{{ selectedNotice.title }}</h3>
            <p>{{ selectedNotice.content }}</p>
          </div>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, reactive, ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { getPortalSummary, getRentals } from '@/api';
import { useAuthStore } from '@/stores/auth';
import type { PortalNotice, RentalProduct } from '@/types/api';

type SortValue = 'default' | 'price_asc' | 'price_desc';
type DisplayMode = 'list' | 'grid';

interface BannerSlide {
  eyebrow: string;
  title: string;
  desc: string;
  action: string;
  actionText: string;
}

interface PromoCard {
  badge: string;
  title: string;
  desc: string;
  actionText: string;
  action: string;
  theme: string;
}

interface ZoneCard {
  key: string;
  title: string;
  desc: string;
  count: number;
  theme: string;
}

const auth = useAuthStore();
const router = useRouter();

const loading = ref(true);
const rawProducts = ref<RentalProduct[]>([]);
const keyword = ref('');
const selectedTags = ref<string[]>([]);
const selectedCategory = ref('');
const selectedLevel = ref('');
const selectedStatus = ref('');
const selectedPriceRange = ref('');
const sortBy = ref<SortValue>('default');
const displayMode = ref<DisplayMode>('list');
const activeZone = ref('all');
const currentPage = ref(1);
const pageSize = 8;
const allTags = ref<string[]>([]);
const allCategories = ref<string[]>([]);
const currentBannerIndex = ref(0);
const bannerTimer = ref<number | null>(null);

const drawerVisible = ref(false);
const selectedProduct = ref<RentalProduct | null>(null);
const selectedDuration = ref<number | null>(null);

const advancedVisible = ref(false);
const advancedForm = reactive({
  minCoin: undefined as number | undefined,
  maxCoin: undefined as number | undefined,
  minPrice: undefined as number | undefined,
  maxPrice: undefined as number | undefined,
  warehouseKeyword: '',
  rankKeyword: '',
  insuranceBoxKeyword: '',
  loginRegionKeyword: '',
  includeTags: [] as string[]
});

const noticeVisible = ref(false);
const selectedNotice = ref<PortalNotice | null>(null);
const notices = ref<PortalNotice[]>([]);

const durations = [
  { hours: 1, discount: 1 },
  { hours: 6, discount: 0.92 },
  { hours: 12, discount: 0.86 },
  { hours: 24, discount: 0.78 }
];

const sidePromos: PromoCard[] = [
  {
    badge: '平台交易',
    title: '按分类快速找号',
    desc: '适合课堂演示和账号展示，先筛分类再看资源细节。',
    actionText: '查看全部',
    action: 'zone:all',
    theme: 'mint'
  },
  {
    badge: '优先推荐',
    title: '高配账号优先看',
    desc: '高等级资源、较高哈夫币和热门标签会优先露出。',
    actionText: '高配专区',
    action: 'zone:premium',
    theme: 'sky'
  }
];

const sortOptions = [
  { value: 'default' as SortValue, label: '默认排序' },
  { value: 'price_asc' as SortValue, label: '价格升序' },
  { value: 'price_desc' as SortValue, label: '价格降序' }
];

const bannerSlides = computed<BannerSlide[]>(() => [
  {
    eyebrow: '新版租号大厅',
    title: '按专区、价格和标签快速筛选账号',
    desc: '默认采用案例 2 的信息密集型列表布局，同时保留案例 1 的卡片视图切换。',
    action: 'zone:all',
    actionText: '浏览全部'
  },
  {
    eyebrow: '高价值资源',
    title: '优先展示高配与高哈夫币账号',
    desc: `当前可租 ${availableCount.value} 个账号，适合快速演示筛选、查看和下单流程。`,
    action: 'zone:premium',
    actionText: '进入高配专区'
  },
  {
    eyebrow: '一键收敛结果',
    title: '高级筛选支持价格、哈夫币和标签组合',
    desc: '现有后端字段不足以完全复刻参考站的复杂筛选，所以这里做了前端兼容增强版。',
    action: 'advanced',
    actionText: '打开高级筛选'
  }
]);

const currentBanner = computed(() => bannerSlides.value[currentBannerIndex.value] ?? bannerSlides.value[0]);

const availableCount = computed(() => rawProducts.value.filter(item => item.status === 'AVAILABLE').length);
const totalLoaded = computed(() => rawProducts.value.length);
const hotCount = computed(() => rawProducts.value.filter(item => item.isHot).length);

const noticeItems = computed<PortalNotice[]>(() => {
  if (notices.value.length > 0) {
    return notices.value.slice(0, 4);
  }
  return [
    {
      id: 1,
      title: '发布与下单流程说明',
      content: '当前页面已支持按分类、标签、价格和状态组合筛选，详情弹窗中可直接选择租赁时长。'
    },
    {
      id: 2,
      title: '账号资源字段说明',
      content: '现阶段演示数据以分类、标签、哈夫币、装备等级、仓库价值为主，复杂展示数据将逐步补齐。'
    },
    {
      id: 3,
      title: '订单交付提示',
      content: '账号下单后由客服确认并交付，适合课程演示完整业务闭环。'
    },
    {
      id: 4,
      title: '筛选逻辑升级说明',
      content: '新版租号大厅默认使用横向列表布局，并保留卡片视图切换。'
    }
  ];
});

const zoneCards = computed<ZoneCard[]>(() => {
  const products = rawProducts.value;
  return [
    {
      key: 'all',
      title: '全部账号',
      desc: '综合浏览区',
      count: products.length,
      theme: 'peach'
    },
    {
      key: 'premium',
      title: '高配专区',
      desc: '高等级资源',
      count: products.filter(isPremiumProduct).length,
      theme: 'gold'
    },
    {
      key: 'high_coin',
      title: '高哈夫币',
      desc: '资源型账号',
      count: products.filter(item => Number(item.coinAmount || 0) >= 100000).length,
      theme: 'green'
    },
    {
      key: 'budget',
      title: '低价体验',
      desc: '适合流程演示',
      count: products.filter(item => Number(item.hourPrice || 0) < 10).length,
      theme: 'violet'
    },
    {
      key: 'hot',
      title: '热门可租',
      desc: '优先推荐区',
      count: products.filter(item => item.status === 'AVAILABLE' && item.isHot).length,
      theme: 'cyan'
    }
  ];
});

const currentZoneLabel = computed(() => zoneCards.value.find(item => item.key === activeZone.value)?.title ?? '全部账号');

const hasAdvancedFilters = computed(() =>
  advancedForm.minCoin !== undefined ||
  advancedForm.maxCoin !== undefined ||
  advancedForm.minPrice !== undefined ||
  advancedForm.maxPrice !== undefined ||
  advancedForm.warehouseKeyword.trim().length > 0 ||
  advancedForm.rankKeyword.trim().length > 0 ||
  advancedForm.insuranceBoxKeyword.trim().length > 0 ||
  advancedForm.loginRegionKeyword.trim().length > 0 ||
  advancedForm.includeTags.length > 0
);

const filteredProducts = computed(() => {
  let list = rawProducts.value.slice();

  list = list.filter(matchesZone);

  if (selectedPriceRange.value) {
    list = list.filter(product => {
      const price = Number(product.hourPrice || 0);
      if (selectedPriceRange.value === 'low') return price < 10;
      if (selectedPriceRange.value === 'mid') return price >= 10 && price <= 20;
      if (selectedPriceRange.value === 'high') return price > 20;
      return true;
    });
  }

  if (advancedForm.minCoin !== undefined) {
    list = list.filter(product => Number(product.coinAmount || 0) >= advancedForm.minCoin!);
  }
  if (advancedForm.maxCoin !== undefined) {
    list = list.filter(product => Number(product.coinAmount || 0) <= advancedForm.maxCoin!);
  }
  if (advancedForm.minPrice !== undefined) {
    list = list.filter(product => Number(product.hourPrice || 0) >= advancedForm.minPrice!);
  }
  if (advancedForm.maxPrice !== undefined) {
    list = list.filter(product => Number(product.hourPrice || 0) <= advancedForm.maxPrice!);
  }
  if (advancedForm.warehouseKeyword.trim()) {
    const text = advancedForm.warehouseKeyword.trim().toLowerCase();
    list = list.filter(product =>
      containsText(product.warehouseValueText, text) ||
      containsText(product.description, text) ||
      containsText(product.name, text)
    );
  }
  if (advancedForm.rankKeyword.trim()) {
    const text = advancedForm.rankKeyword.trim().toLowerCase();
    list = list.filter(product =>
      containsText(product.rankText, text) ||
      containsText(product.equipmentLevelText, text)
    );
  }
  if (advancedForm.insuranceBoxKeyword.trim()) {
    const text = advancedForm.insuranceBoxKeyword.trim().toLowerCase();
    list = list.filter(product => containsText(product.insuranceBoxText, text));
  }
  if (advancedForm.loginRegionKeyword.trim()) {
    const text = advancedForm.loginRegionKeyword.trim().toLowerCase();
    list = list.filter(product => containsText(product.loginRegion, text));
  }
  if (advancedForm.includeTags.length > 0) {
    list = list.filter(product => {
      const tags = splitTags(product.tagText).map(tag => tag.toLowerCase());
      return advancedForm.includeTags.every(tag => tags.includes(tag.toLowerCase()));
    });
  }

  return list;
});

const total = computed(() => filteredProducts.value.length);
const paginatedProducts = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  return filteredProducts.value.slice(start, start + pageSize);
});

function containsText(raw: string | undefined, expected: string) {
  return (raw ?? '').toLowerCase().includes(expected);
}

function splitTags(tagText: string | undefined): string[] {
  return (tagText ?? '')
    .split(',')
    .map(item => item.trim())
    .filter(Boolean);
}

function categoryText(category: string | undefined) {
  return category || '综合账号';
}

function formatCoinAmount(value: number | undefined) {
  const amount = Number(value || 0);
  if (!amount) return '未录入';
  if (amount >= 100000000) return `${(amount / 100000000).toFixed(2)}亿`;
  if (amount >= 10000) return `${(amount / 10000).toFixed(amount >= 1000000 ? 0 : 1)}万`;
  return amount.toLocaleString();
}

function formatPrice(value: number | string | undefined) {
  const amount = Number(value || 0);
  return amount.toFixed(2);
}

function getStatusText(status: string) {
  return {
    AVAILABLE: '可租',
    RENTED: '已租出',
    MAINTENANCE: '维护中'
  }[status] ?? status;
}

function statusTheme(status: string) {
  return {
    AVAILABLE: 'available',
    RENTED: 'rented',
    MAINTENANCE: 'maintenance'
  }[status] ?? '';
}

function shortProductName(name: string) {
  return name.length > 16 ? `${name.slice(0, 16)}...` : name;
}

function recommendText(product: RentalProduct) {
  const price = Number(product.hourPrice || 0);
  if (isPremiumProduct(product)) return '高配冲分';
  if (price < 10) return '低价体验';
  if (splitTags(product.tagText).length >= 4) return '资源展示';
  return '综合租用';
}

function isPremiumProduct(product: RentalProduct) {
  const levelText = product.equipmentLevelText?.toLowerCase() ?? '';
  const price = Number(product.hourPrice || 0);
  return levelText.includes('高') || levelText.includes('满') || price >= 20;
}

function matchesZone(product: RentalProduct) {
  if (activeZone.value === 'all') return true;
  if (activeZone.value === 'premium') return isPremiumProduct(product);
  if (activeZone.value === 'high_coin') return Number(product.coinAmount || 0) >= 100000;
  if (activeZone.value === 'budget') return Number(product.hourPrice || 0) < 10;
  if (activeZone.value === 'hot') return product.status === 'AVAILABLE' && !!product.isHot;
  return true;
}

function priceRangeLabel(range: string) {
  return {
    low: '10 元以下',
    mid: '10 - 20 元',
    high: '20 元以上'
  }[range] ?? range;
}

function coverTheme(product: RentalProduct) {
  if (product.status === 'AVAILABLE' && isPremiumProduct(product)) return 'theme-gold';
  if (product.status === 'AVAILABLE') return 'theme-green';
  if (product.status === 'MAINTENANCE') return 'theme-slate';
  return 'theme-blue';
}

function calcPrice(hours: number) {
  if (!selectedProduct.value) return '0.00';
  const discount = durations.find(item => item.hours === hours)?.discount ?? 1;
  return (Number(selectedProduct.value.hourPrice || 0) * hours * discount).toFixed(2);
}

function selectDuration(hours: number) {
  if (selectedProduct.value?.status !== 'AVAILABLE') return;
  selectedDuration.value = selectedDuration.value === hours ? null : hours;
}

function openDrawer(product: RentalProduct) {
  selectedProduct.value = product;
  selectedDuration.value = null;
  drawerVisible.value = true;
}

function openNotice(notice: PortalNotice) {
  selectedNotice.value = notice;
  noticeVisible.value = true;
}

function applyZone(zone: string) {
  activeZone.value = zone;
}

function toggleAvailableOnly() {
  selectedStatus.value = selectedStatus.value === 'AVAILABLE' ? '' : 'AVAILABLE';
}

function toggleDisplayMode() {
  displayMode.value = displayMode.value === 'list' ? 'grid' : 'list';
}

function clearFilters() {
  keyword.value = '';
  selectedTags.value = [];
  selectedCategory.value = '';
  selectedLevel.value = '';
  selectedStatus.value = '';
  selectedPriceRange.value = '';
  sortBy.value = 'default';
  activeZone.value = 'all';
  resetAdvancedFilters();
  currentPage.value = 1;
  loadRentals();
}

function resetAdvancedFilters() {
  advancedForm.minCoin = undefined;
  advancedForm.maxCoin = undefined;
  advancedForm.minPrice = undefined;
  advancedForm.maxPrice = undefined;
  advancedForm.warehouseKeyword = '';
  advancedForm.rankKeyword = '';
  advancedForm.insuranceBoxKeyword = '';
  advancedForm.loginRegionKeyword = '';
  advancedForm.includeTags = [];
}

function applyAdvancedFilters() {
  advancedVisible.value = false;
  currentPage.value = 1;
}

function handleBannerAction(action: string) {
  if (action === 'advanced') {
    advancedVisible.value = true;
    return;
  }
  if (action.startsWith('zone:')) {
    applyZone(action.replace('zone:', ''));
  }
}

function handlePromoAction(action: string) {
  handleBannerAction(action);
}

function goToOrder() {
  if (!selectedProduct.value || !selectedDuration.value) return;
  sessionStorage.setItem('detailProduct', JSON.stringify(selectedProduct.value));
  sessionStorage.setItem('detailDuration', String(selectedDuration.value));
  router.push({
    path: '/orders/create',
    query: {
      accountId: String(selectedProduct.value.id),
      duration: String(selectedDuration.value)
    }
  });
  drawerVisible.value = false;
}

function goToLogin() {
  sessionStorage.setItem('detailProduct', JSON.stringify(selectedProduct.value ?? {}));
  sessionStorage.setItem('detailDuration', String(selectedDuration.value ?? ''));
  router.push({
    path: '/login',
    query: {
      redirect: `/orders/create?accountId=${selectedProduct.value?.id ?? ''}&duration=${selectedDuration.value ?? ''}`
    }
  });
  drawerVisible.value = false;
}

async function loadRentals() {
  loading.value = true;
  try {
    const response = await getRentals({
      keyword: keyword.value || undefined,
      tags: selectedTags.value.length ? selectedTags.value.join(',') : undefined,
      category: selectedCategory.value || undefined,
      level: selectedLevel.value || undefined,
      status: selectedStatus.value || undefined,
      sortBy: sortBy.value !== 'default' ? sortBy.value : undefined,
      page: 1,
      pageSize: 1000
    });

    if (!response.data.success) {
      ElMessage.error(response.data.message || '租号列表加载失败');
      return;
    }

    rawProducts.value = response.data.data.list;
    allTags.value = response.data.data.allTags ?? [];
    allCategories.value = response.data.data.allCategories ?? [];
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '租号列表加载失败');
  } finally {
    loading.value = false;
  }
}

async function loadNotices() {
  try {
    const response = await getPortalSummary();
    if (response.data.success) {
      notices.value = response.data.data.notices ?? [];
    }
  } catch {
    notices.value = [];
  }
}

function startBannerRotation() {
  stopBannerRotation();
  bannerTimer.value = window.setInterval(() => {
    currentBannerIndex.value = (currentBannerIndex.value + 1) % bannerSlides.value.length;
  }, 4500);
}

function stopBannerRotation() {
  if (bannerTimer.value !== null) {
    window.clearInterval(bannerTimer.value);
    bannerTimer.value = null;
  }
}

let keywordTimer: ReturnType<typeof setTimeout> | null = null;

watch(keyword, () => {
  if (keywordTimer) clearTimeout(keywordTimer);
  keywordTimer = setTimeout(() => {
    currentPage.value = 1;
    loadRentals();
  }, 350);
});

watch([selectedTags, selectedCategory, selectedLevel, selectedStatus, sortBy], () => {
  currentPage.value = 1;
  loadRentals();
}, { deep: true });

watch(
  [
    activeZone,
    selectedPriceRange,
    () => advancedForm.minCoin,
    () => advancedForm.maxCoin,
    () => advancedForm.minPrice,
    () => advancedForm.maxPrice,
    () => advancedForm.warehouseKeyword,
    () => advancedForm.rankKeyword,
    () => advancedForm.insuranceBoxKeyword,
    () => advancedForm.loginRegionKeyword,
    () => advancedForm.includeTags.join(','),
    displayMode
  ],
  () => {
    currentPage.value = 1;
  }
);

watch(total, value => {
  const maxPage = Math.max(1, Math.ceil(value / pageSize));
  if (currentPage.value > maxPage) {
    currentPage.value = maxPage;
  }
});

onMounted(() => {
  loadRentals();
  loadNotices();
  startBannerRotation();

  const saved = sessionStorage.getItem('detailProduct');
  if (saved) {
    try {
      selectedProduct.value = JSON.parse(saved);
      const savedDuration = sessionStorage.getItem('detailDuration');
      selectedDuration.value = savedDuration ? Number(savedDuration) : null;
      drawerVisible.value = true;
    } catch {
      // ignore session parse errors
    }
    sessionStorage.removeItem('detailProduct');
    sessionStorage.removeItem('detailDuration');
  }
});

onUnmounted(() => {
  if (keywordTimer) {
    clearTimeout(keywordTimer);
  }
  stopBannerRotation();
});
</script>

<script lang="ts">
export default { name: 'RentalListView' };
</script>

<style scoped>
.rental-page {
  margin: 0;
  min-height: calc(100vh - 64px);
  background:
    radial-gradient(circle at top left, rgba(255, 221, 111, 0.35), transparent 24%),
    radial-gradient(circle at top right, rgba(119, 221, 255, 0.2), transparent 20%),
    linear-gradient(180deg, #fff7d8 0%, #fffdf4 26%, #fff9ec 100%);
  color: #1d232f;
}

.page-inner {
  max-width: 1240px;
  margin: 0 auto;
  padding: 28px 24px 40px;
}

.hero-board {
  display: grid;
  grid-template-columns: 260px minmax(0, 1fr) 320px;
  gap: 16px;
  margin-bottom: 18px;
}

.hero-side {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.promo-card,
.notice-panel,
.toolbar-panel,
.list-panel,
.advanced-panel,
.detail-panel,
.notice-detail {
  border-radius: 22px;
  border: 1px solid rgba(243, 194, 76, 0.16);
  box-shadow: 0 18px 40px rgba(181, 145, 41, 0.08);
}

.promo-card {
  min-height: 170px;
  padding: 18px 18px 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  text-align: left;
}

.promo-card.mint {
  background: linear-gradient(145deg, #d8fff5 0%, #eefef8 100%);
}

.promo-card.sky {
  background: linear-gradient(145deg, #e2f4ff 0%, #f5fbff 100%);
}

.promo-card h3 {
  margin: 0;
  font-size: 22px;
  font-weight: 800;
  line-height: 1.2;
}

.promo-card p {
  margin: 0;
  font-size: 13px;
  line-height: 1.7;
  color: #586171;
}

.promo-badge {
  display: inline-flex;
  align-items: center;
  width: fit-content;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.72);
  color: #ff7b21;
  font-size: 12px;
  font-weight: 700;
}

.promo-action {
  margin-top: auto;
  height: 38px;
  border: 0;
  border-radius: 999px;
  background: linear-gradient(135deg, #ffb626 0%, #ff8b11 100%);
  color: #fff;
  font-weight: 700;
  cursor: pointer;
}

.hero-banner {
  position: relative;
  overflow: hidden;
  min-height: 356px;
  padding: 34px 34px 26px;
  border-radius: 26px;
  background:
    radial-gradient(circle at 20% 25%, rgba(255, 255, 255, 0.28), transparent 14%),
    radial-gradient(circle at 82% 24%, rgba(255, 226, 144, 0.34), transparent 16%),
    linear-gradient(135deg, #ff9617 0%, #ff7f11 38%, #ff9c3c 100%);
  color: #fffdf7;
  box-shadow: 0 22px 50px rgba(255, 141, 29, 0.26);
}

.banner-mark {
  position: absolute;
  top: 18px;
  right: 22px;
  font-size: 52px;
  font-weight: 900;
  color: rgba(255, 255, 255, 0.12);
  letter-spacing: 2px;
  pointer-events: none;
}

.banner-content {
  position: relative;
  z-index: 1;
  max-width: 620px;
}

.banner-eyebrow {
  display: inline-flex;
  align-items: center;
  height: 30px;
  padding: 0 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.18);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 1px;
}

.hero-banner h1 {
  margin: 20px 0 16px;
  font-size: 42px;
  line-height: 1.14;
  font-weight: 900;
}

.hero-banner p {
  margin: 0;
  max-width: 560px;
  font-size: 16px;
  line-height: 1.9;
  color: rgba(255, 250, 241, 0.92);
}

.banner-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 22px;
}

.banner-primary,
.banner-secondary {
  height: 44px;
  padding: 0 18px;
  border-radius: 14px;
  border: 0;
  font-size: 14px;
  font-weight: 800;
  cursor: pointer;
}

.banner-primary {
  background: #fffef9;
  color: #ff831b;
}

.banner-secondary {
  background: rgba(255, 255, 255, 0.18);
  color: #fff;
  border: 1px solid rgba(255, 255, 255, 0.26);
}

.banner-stats {
  display: flex;
  gap: 12px;
  margin-top: 28px;
}

.banner-stat {
  min-width: 110px;
  padding: 12px 14px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(4px);
}

.banner-stat-value {
  display: block;
  font-size: 26px;
  font-weight: 900;
  line-height: 1;
}

.banner-stat-label {
  display: block;
  margin-top: 6px;
  font-size: 12px;
  color: rgba(255, 251, 244, 0.84);
}

.banner-dots {
  position: absolute;
  left: 34px;
  bottom: 22px;
  display: flex;
  gap: 8px;
}

.banner-dot {
  width: 30px;
  height: 4px;
  border: 0;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.34);
  cursor: pointer;
}

.banner-dot.active {
  background: #fff9ef;
}

.notice-panel {
  background: rgba(255, 255, 255, 0.9);
  padding: 18px;
}

.notice-head {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
  font-size: 15px;
}

.notice-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 30px;
  padding: 0 12px;
  border-radius: 999px;
  background: linear-gradient(135deg, #fff0ad 0%, #ffd15b 100%);
  color: #5b4100;
  font-size: 12px;
  font-weight: 800;
}

.notice-item {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 18px 4px;
  border: 0;
  border-bottom: 1px solid #f0f1f4;
  background: transparent;
  text-align: left;
  cursor: pointer;
}

.notice-item:last-child {
  border-bottom: 0;
}

.notice-title {
  font-size: 16px;
  line-height: 1.4;
  color: #1f2430;
}

.notice-arrow {
  font-size: 24px;
  color: #a4aab5;
}

.zone-strip {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 14px;
  margin-bottom: 18px;
}

.zone-card {
  min-height: 98px;
  padding: 16px 18px;
  border-radius: 20px;
  border: 1px solid transparent;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  cursor: pointer;
  transition: transform 0.22s ease, box-shadow 0.22s ease, border-color 0.22s ease;
}

.zone-card:hover,
.zone-card.active {
  transform: translateY(-3px);
  box-shadow: 0 18px 30px rgba(181, 145, 41, 0.14);
  border-color: rgba(255, 179, 46, 0.45);
}

.zone-card.peach {
  background: linear-gradient(145deg, #ffe9da 0%, #fff7f0 100%);
}

.zone-card.gold {
  background: linear-gradient(145deg, #fff3c9 0%, #fffdf1 100%);
}

.zone-card.green {
  background: linear-gradient(145deg, #defae7 0%, #f5fff8 100%);
}

.zone-card.violet {
  background: linear-gradient(145deg, #f1e8ff 0%, #fbf7ff 100%);
}

.zone-card.cyan {
  background: linear-gradient(145deg, #ddf7ff 0%, #f4fdff 100%);
}

.zone-copy {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.zone-copy strong {
  font-size: 18px;
  line-height: 1.1;
}

.zone-copy span {
  font-size: 13px;
  color: #687180;
}

.zone-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 6px;
}

.zone-count {
  font-size: 28px;
  font-weight: 900;
  line-height: 1;
}

.zone-go {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 26px;
  border-radius: 999px;
  background: #fff;
  color: #ff8b11;
  font-size: 12px;
  font-weight: 900;
}

.toolbar-panel {
  margin-bottom: 18px;
  background: rgba(255, 255, 255, 0.84);
  padding: 18px;
}

.toolbar-top,
.filter-summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.sort-tabs,
.toolbar-ops,
.summary-left {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.sort-tab,
.ghost-op,
.primary-op {
  height: 38px;
  padding: 0 14px;
  border-radius: 12px;
  border: 1px solid #eceef3;
  background: #fff;
  color: #334155;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
}

.sort-tab.active {
  background: #fff1d9;
  border-color: #ffc04c;
  color: #cf7700;
}

.ghost-op.danger {
  color: #e85151;
}

.primary-op {
  background: linear-gradient(135deg, #fff0af 0%, #ffd339 100%);
  border-color: rgba(255, 183, 0, 0.34);
  color: #5f4300;
}

.filter-grid {
  display: grid;
  grid-template-columns: 2fr repeat(4, minmax(0, 1fr)) 1.15fr;
  gap: 12px;
  margin: 16px 0;
}

.keyword-item {
  min-width: 0;
}

.wide-item {
  min-width: 0;
}

.summary-text,
.summary-view {
  font-size: 13px;
  color: #70798a;
}

.summary-chip {
  display: inline-flex;
  align-items: center;
  height: 28px;
  padding: 0 10px;
  border-radius: 999px;
  background: #f4f6fb;
  color: #536071;
  font-size: 12px;
  font-weight: 700;
}

.summary-chip.highlighted {
  background: #fff1d9;
  color: #cf7700;
}

.list-panel {
  background: rgba(255, 255, 255, 0.72);
  padding: 18px;
}

.row-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.row-card {
  display: grid;
  grid-template-columns: 220px minmax(0, 1fr) 210px;
  gap: 16px;
  border-radius: 22px;
  border: 1px solid #f0e7c8;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(249, 255, 253, 0.92) 100%);
  padding: 18px;
  cursor: pointer;
  transition: transform 0.22s ease, box-shadow 0.22s ease;
}

.row-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 18px 36px rgba(173, 140, 44, 0.12);
}

.row-cover,
.detail-cover,
.grid-cover {
  position: relative;
  overflow: hidden;
  border-radius: 20px;
  color: #fffefb;
}

.row-cover {
  min-height: 180px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
}

.theme-gold {
  background:
    radial-gradient(circle at 76% 18%, rgba(255, 255, 255, 0.26), transparent 18%),
    linear-gradient(145deg, #121824 0%, #735412 42%, #f0b53c 100%);
}

.theme-green {
  background:
    radial-gradient(circle at 76% 18%, rgba(255, 255, 255, 0.26), transparent 18%),
    linear-gradient(145deg, #0f1d1c 0%, #0f6c60 46%, #6ce0c3 100%);
}

.theme-slate {
  background:
    radial-gradient(circle at 76% 18%, rgba(255, 255, 255, 0.18), transparent 18%),
    linear-gradient(145deg, #1e2430 0%, #445161 52%, #8fa1ba 100%);
}

.theme-blue {
  background:
    radial-gradient(circle at 76% 18%, rgba(255, 255, 255, 0.2), transparent 18%),
    linear-gradient(145deg, #111c31 0%, #294d8b 48%, #70a7ff 100%);
}

.cover-status,
.grid-status,
.detail-cover-status {
  position: absolute;
  top: 14px;
  right: 14px;
  display: inline-flex;
  align-items: center;
  height: 28px;
  padding: 0 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 800;
  backdrop-filter: blur(4px);
}

.available {
  background: rgba(22, 163, 74, 0.24);
  color: #dfffea;
}

.rented {
  background: rgba(255, 190, 11, 0.22);
  color: #fff7d2;
}

.maintenance {
  background: rgba(148, 163, 184, 0.24);
  color: #f3f6fa;
}

.cover-category,
.detail-cover-category {
  display: inline-flex;
  width: fit-content;
  align-items: center;
  height: 30px;
  padding: 0 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.18);
  font-size: 12px;
  font-weight: 800;
}

.cover-name {
  margin-top: 14px;
  font-size: 28px;
  font-weight: 900;
  line-height: 1.1;
}

.cover-tags,
.detail-cover-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 10px;
}

.cover-tags span,
.detail-cover-tags span {
  display: inline-flex;
  align-items: center;
  height: 26px;
  padding: 0 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.16);
  font-size: 11px;
  font-weight: 700;
}

.row-main {
  min-width: 0;
}

.row-title {
  margin: 4px 0 10px;
  font-size: 28px;
  line-height: 1.15;
  font-weight: 900;
  color: #1f2430;
}

.row-meta {
  margin: 0 0 16px;
  color: #616b7a;
  font-size: 14px;
  line-height: 1.8;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

.metric-box,
.detail-metric,
.resource-item {
  min-width: 0;
  border-radius: 16px;
  background: #f4f7fb;
  padding: 14px 16px;
}

.metric-box.emphasis {
  background: linear-gradient(180deg, #fff7dc 0%, #fffbee 100%);
}

.metric-box strong,
.detail-metric strong,
.resource-item strong {
  display: block;
  font-size: 24px;
  font-weight: 900;
  line-height: 1.15;
  color: #1f2430;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.metric-box span,
.detail-metric span,
.resource-item span {
  display: block;
  margin-top: 6px;
  font-size: 12px;
  color: #6c7686;
}

.row-side {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 14px;
  padding-left: 6px;
  border-left: 1px solid #f0f1f4;
}

.side-top {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.side-price-label {
  font-size: 13px;
  color: #7c8495;
}

.side-price {
  font-size: 38px;
  line-height: 1;
  font-weight: 900;
  color: #f05b2c;
}

.side-price-unit {
  font-size: 13px;
  color: #7c8495;
}

.side-tags,
.side-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.side-pill,
.time-chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 34px;
  padding: 0 12px;
  border-radius: 12px;
  background: #fff7dc;
  color: #af6700;
  font-size: 12px;
  font-weight: 800;
}

.side-pill.hot {
  background: #ffe4db;
  color: #f05b2c;
}

.time-chip {
  border: 0;
}

.rent-btn,
.grid-action {
  height: 40px;
  padding: 0 16px;
  border-radius: 12px;
  border: 0;
  background: linear-gradient(135deg, #ffe057 0%, #ffc420 100%);
  color: #533b00;
  font-size: 14px;
  font-weight: 900;
  cursor: pointer;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.grid-card {
  overflow: hidden;
  border-radius: 22px;
  border: 1px solid #f0e7c8;
  background: rgba(255, 255, 255, 0.95);
  cursor: pointer;
  transition: transform 0.22s ease, box-shadow 0.22s ease;
}

.grid-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 36px rgba(173, 140, 44, 0.12);
}

.grid-cover {
  min-height: 170px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.grid-cover-top,
.grid-cover-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.grid-badge {
  display: inline-flex;
  align-items: center;
  height: 28px;
  padding: 0 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.18);
  font-size: 12px;
  font-weight: 800;
}

.grid-cover-bottom {
  align-items: flex-end;
}

.grid-cover-bottom strong {
  font-size: 28px;
  font-weight: 900;
}

.grid-cover-bottom span {
  font-size: 12px;
}

.grid-body {
  padding: 16px 16px 18px;
}

.grid-body h3 {
  margin: 0 0 10px;
  font-size: 18px;
  line-height: 1.35;
  font-weight: 800;
  color: #1f2430;
}

.grid-body p {
  margin: 0;
  min-height: 42px;
  color: #646d7d;
  font-size: 13px;
  line-height: 1.6;
}

.grid-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin: 14px 0 16px;
}

.grid-tags span {
  display: inline-flex;
  align-items: center;
  height: 28px;
  padding: 0 10px;
  border-radius: 999px;
  background: #f5f7fb;
  color: #516071;
  font-size: 12px;
  font-weight: 700;
}

.grid-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.grid-price {
  display: flex;
  align-items: baseline;
  gap: 6px;
}

.grid-price strong {
  font-size: 26px;
  line-height: 1;
  color: #f05b2c;
}

.grid-price span {
  color: #7c8495;
  font-size: 12px;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 22px;
}

.empty-state {
  min-height: 340px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.empty-icon {
  font-size: 58px;
  line-height: 1;
  color: #ffb020;
}

.empty-state h3 {
  margin: 18px 0 10px;
  font-size: 24px;
}

.empty-state p {
  margin: 0 0 18px;
  color: #677181;
}

.sk-pulse {
  background: linear-gradient(90deg, #f2f4f8 0%, #f8fafc 50%, #f2f4f8 100%);
  background-size: 200% 100%;
  animation: shimmer 1.4s infinite linear;
  border-radius: 12px;
}

.skeleton-row,
.skeleton-grid {
  cursor: default;
}

.skeleton-row:hover,
.skeleton-grid:hover {
  transform: none;
  box-shadow: none;
}

.skeleton-cover,
.grid-cover-skeleton {
  min-height: 180px;
  border-radius: 18px;
}

.skeleton-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-top: 8px;
}

.skeleton-metrics {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

.skeleton-side {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 18px;
}

.sk-line {
  height: 16px;
}

.sk-box {
  height: 72px;
}

.sk-button {
  height: 42px;
}

.w-40 {
  width: 40%;
}

.w-50 {
  width: 50%;
}

.w-60 {
  width: 60%;
}

.w-70 {
  width: 70%;
}

.w-85 {
  width: 85%;
}

.w-90 {
  width: 90%;
}

@keyframes shimmer {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

.advanced-panel,
.detail-panel,
.notice-detail {
  position: relative;
  background: #fffdf7;
  padding: 24px;
}

.advanced-head,
.detail-title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.advanced-head h3,
.notice-detail h3 {
  margin: 0;
  font-size: 28px;
  line-height: 1.2;
}

.advanced-head p {
  margin: 8px 0 0;
  color: #6c7686;
  font-size: 13px;
}

.dialog-close {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  border: 0;
  background: #f5f7fb;
  color: #7b8494;
  font-size: 24px;
  cursor: pointer;
}

.advanced-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px 24px;
  margin-top: 22px;
}

.advanced-field label,
.block-title {
  display: block;
  margin-bottom: 10px;
  font-size: 14px;
  font-weight: 800;
  color: #1f2430;
}

.range-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 30px minmax(0, 1fr);
  align-items: center;
  gap: 8px;
}

.range-row span {
  text-align: center;
  color: #7d8595;
}

.advanced-foot,
.detail-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}

.advanced-reset,
.advanced-confirm {
  min-width: 120px;
  height: 42px;
  border-radius: 14px;
  border: 0;
  font-size: 14px;
  font-weight: 800;
  cursor: pointer;
}

.advanced-reset {
  background: #f5f7fb;
  color: #596577;
}

.advanced-confirm {
  background: linear-gradient(135deg, #ffe057 0%, #ffc420 100%);
  color: #533b00;
}

.detail-top {
  display: grid;
  grid-template-columns: 260px minmax(0, 1fr);
  gap: 18px;
}

.detail-cover {
  min-height: 260px;
  padding: 18px;
}

.detail-cover h3 {
  margin: 18px 0 8px;
  font-size: 34px;
  line-height: 1.1;
  font-weight: 900;
}

.detail-cover p {
  margin: 0;
  color: rgba(255, 253, 247, 0.86);
}

.detail-summary h2 {
  margin: 0;
  font-size: 28px;
  line-height: 1.2;
}

.detail-summary p {
  margin: 10px 0 0;
  color: #667180;
  line-height: 1.8;
}

.detail-price-box {
  flex-shrink: 0;
  min-width: 140px;
  padding: 16px;
  border-radius: 18px;
  background: linear-gradient(180deg, #fff2cd 0%, #fffdf0 100%);
  text-align: center;
}

.detail-price-box span {
  display: block;
  font-size: 12px;
  color: #8d7426;
}

.detail-price-box strong {
  display: block;
  margin-top: 8px;
  font-size: 34px;
  line-height: 1;
  color: #f05b2c;
}

.detail-metrics,
.resource-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 18px;
}

.detail-block {
  margin-top: 22px;
  border-radius: 20px;
  background: #fff;
  border: 1px solid #f2ecd8;
  padding: 18px;
}

.duration-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
}

.duration-card {
  position: relative;
  min-height: 92px;
  border-radius: 18px;
  border: 1px solid #eceef3;
  background: #f8fafc;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
}

.duration-card strong {
  font-size: 16px;
  color: #1f2430;
}

.duration-card span {
  font-size: 15px;
  font-weight: 800;
  color: #f05b2c;
}

.duration-card em {
  position: absolute;
  top: 10px;
  right: 10px;
  font-style: normal;
  display: inline-flex;
  align-items: center;
  height: 22px;
  padding: 0 8px;
  border-radius: 999px;
  background: #ffe4db;
  color: #f05b2c;
  font-size: 11px;
  font-weight: 800;
}

.duration-card.active {
  background: #fff2cd;
  border-color: #ffc64c;
}

.duration-card.disabled {
  cursor: not-allowed;
  opacity: 0.56;
}

.detail-total {
  margin-top: 14px;
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: #616b7a;
}

.detail-total strong {
  font-size: 26px;
  color: #f05b2c;
}

.detail-order-btn {
  min-width: 180px;
  height: 44px;
  border-radius: 14px;
  font-weight: 800;
}

.notice-detail-badge {
  display: inline-flex;
  align-items: center;
  height: 30px;
  padding: 0 12px;
  border-radius: 999px;
  background: #fff1d9;
  color: #cf7700;
  font-size: 12px;
  font-weight: 800;
}

.notice-detail p {
  margin: 16px 0 0;
  color: #616b7a;
  line-height: 1.9;
  font-size: 15px;
}

:deep(.el-dialog) {
  border-radius: 24px !important;
  overflow: hidden !important;
  background: transparent !important;
  box-shadow: 0 24px 60px rgba(22, 28, 45, 0.18) !important;
}

:deep(.el-dialog__header) {
  display: none !important;
}

:deep(.el-dialog__body) {
  padding: 0 !important;
}

:deep(.el-overlay) {
  background: rgba(35, 39, 48, 0.36) !important;
  backdrop-filter: blur(4px);
}

:deep(.el-input__wrapper),
:deep(.el-select__wrapper),
:deep(.el-textarea__inner) {
  min-height: 40px;
  background: #fff !important;
  box-shadow: 0 0 0 1px #eceef3 inset !important;
  border-radius: 12px !important;
}

:deep(.el-input__inner),
:deep(.el-select__placeholder) {
  color: #1f2430 !important;
}

:deep(.el-input-number) {
  width: 100%;
}

:deep(.el-pagination.is-background .btn-next),
:deep(.el-pagination.is-background .btn-prev),
:deep(.el-pagination.is-background .el-pager li) {
  background: #fff !important;
  border: 1px solid #eceef3;
  color: #506071 !important;
}

:deep(.el-pagination.is-background .el-pager li.is-active) {
  background: #ffe57b !important;
  border-color: #ffd242 !important;
  color: #6a4a00 !important;
}

@media (max-width: 1180px) {
  .hero-board {
    grid-template-columns: 1fr;
  }

  .hero-side {
    order: 2;
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .notice-panel {
    order: 3;
  }

  .zone-strip {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .filter-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .row-card {
    grid-template-columns: 200px minmax(0, 1fr);
  }

  .row-side {
    grid-column: 1 / -1;
    border-left: 0;
    border-top: 1px solid #f0f1f4;
    padding-left: 0;
    padding-top: 14px;
  }

  .card-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 900px) {
  .page-inner {
    padding: 18px 14px 30px;
  }

  .hero-banner {
    min-height: auto;
    padding: 24px 22px 48px;
  }

  .hero-banner h1 {
    font-size: 32px;
  }

  .hero-side {
    grid-template-columns: 1fr;
  }

  .zone-strip,
  .filter-grid,
  .advanced-grid,
  .card-grid,
  .detail-top,
  .duration-grid,
  .detail-metrics,
  .resource-grid {
    grid-template-columns: 1fr;
  }

  .toolbar-top,
  .filter-summary {
    flex-direction: column;
    align-items: stretch;
  }

  .row-card {
    grid-template-columns: 1fr;
  }

  .metric-grid,
  .skeleton-metrics {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .detail-price-box {
    min-width: 0;
  }
}

@media (max-width: 640px) {
  .hero-banner h1 {
    font-size: 26px;
  }

  .banner-stats {
    flex-direction: column;
  }

  .metric-grid,
  .skeleton-metrics {
    grid-template-columns: 1fr;
  }

  .card-grid {
    grid-template-columns: 1fr;
  }

  .advanced-panel,
  .detail-panel,
  .notice-detail {
    padding: 18px;
  }
}
</style>
