<template>
  <div class="rental-page">
    <div class="page-inner">
      <section class="hero-board">
        <div class="hero-banner">
          <div class="hero-copy">
            <div class="hero-badge">三角洲租号大厅</div>
            <h1>{{ currentBanner.title }}</h1>
            <p>{{ currentBanner.desc }}</p>
            <div class="hero-actions">
              <button class="primary-btn" type="button" @click="goToPublish">
                我要上架
              </button>
              <button class="ghost-btn" type="button" @click="advancedVisible = true">
                高级筛选
              </button>
            </div>
          </div>

          <div class="hero-stats">
            <div class="hero-stat">
              <strong>{{ availableCount }}</strong>
              <span>可租账号</span>
            </div>
            <div class="hero-stat">
              <strong>{{ totalLoaded }}</strong>
              <span>全部资源</span>
            </div>
            <div class="hero-stat">
              <strong>{{ pendingCount }}</strong>
              <span>待审核</span>
            </div>
          </div>

          <div class="hero-dots">
            <button
              v-for="(_, index) in bannerSlides"
              :key="index"
              class="hero-dot"
              :class="{ active: index === currentBannerIndex }"
              type="button"
              @click="currentBannerIndex = index"
            />
          </div>
        </div>

        <aside class="notice-panel">
          <div class="notice-head">
            <span class="notice-badge">公告</span>
            <strong>大厅说明</strong>
          </div>
          <button
            v-for="notice in noticeItems"
            :key="notice.id"
            class="notice-item"
            type="button"
            @click="openNotice(notice)"
          >
            <span>{{ notice.title }}</span>
            <em>›</em>
          </button>
        </aside>
      </section>

      <section class="zone-strip">
        <button
          v-for="zone in zoneCards"
          :key="zone.key"
          class="zone-card"
          :class="{ active: activeZone === zone.key }"
          type="button"
          @click="applyZone(zone.key)"
        >
          <div class="zone-text">
            <strong>{{ zone.title }}</strong>
            <span>{{ zone.desc }}</span>
          </div>
          <b>{{ zone.count }}</b>
        </button>
      </section>

      <section class="toolbar-panel">
        <div class="toolbar-top">
          <div class="sort-tabs">
            <button
              v-for="sort in sortOptions"
              :key="sort.key"
              class="sort-tab"
              :class="{ active: isSortActive(sort.key) }"
              type="button"
              @click="toggleSort(sort.key)"
            >
              {{ sort.label }}
              <span v-if="sort.key !== 'default'" class="sort-arrow">{{ getSortArrow(sort.key) }}</span>
            </button>
          </div>

          <div class="toolbar-actions">
            <button class="danger-btn" type="button" @click="clearFilters">重置筛选</button>
            <button class="ghost-btn" type="button" @click="displayMode = displayMode === 'list' ? 'grid' : 'list'">
              切换{{ displayMode === 'list' ? '卡片' : '列表' }}
            </button>
            <button class="primary-btn small" type="button" @click="advancedVisible = true">高级筛选</button>
          </div>
        </div>

        <div class="filter-row">
          <el-input
            v-model="keyword"
            placeholder="搜索标题、刀皮、红皮、段位"
            clearable
            class="filter-item filter-search"
          />
          <el-select v-model="selectedPriceRange" clearable placeholder="价格" class="filter-item">
            <el-option v-for="item in PRICE_RANGE_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
          <el-select v-model="selectedStatus" clearable placeholder="状态" class="filter-item">
            <el-option label="可租" value="AVAILABLE" />
            <el-option label="待审核 / 维护" value="MAINTENANCE" />
            <el-option label="已租出" value="RENTED" />
          </el-select>
        </div>

        <div class="filter-row">
          <el-select v-model="selectedCoinRange" clearable placeholder="哈夫币" class="filter-item">
            <el-option v-for="item in COIN_RANGE_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
          <el-select v-model="selectedInsuranceBox" clearable placeholder="保险箱" class="filter-item">
            <el-option v-for="item in INSURANCE_OPTIONS" :key="item" :label="item" :value="item" />
          </el-select>
          <el-select v-model="selectedRank" clearable placeholder="段位" class="filter-item">
            <el-option v-for="item in RANK_OPTIONS" :key="item" :label="item" :value="item" />
          </el-select>
          <el-select v-model="selectedLoginMethod" clearable placeholder="上号方式" class="filter-item">
            <el-option v-for="item in LOGIN_METHOD_OPTIONS" :key="item" :label="item" :value="item" />
          </el-select>
        </div>

        <div class="filter-row">
          <el-select v-model="selectedKnifeSkin" clearable placeholder="刀皮" class="filter-item">
            <el-option v-for="item in KNIFE_SKIN_OPTIONS" :key="item" :label="item" :value="item" />
          </el-select>
          <el-select v-model="selectedOperatorSkin" clearable placeholder="红皮" class="filter-item">
            <el-option v-for="item in OPERATOR_SKIN_OPTIONS" :key="item" :label="item" :value="item" />
          </el-select>
          <el-select v-model="selectedStamina" clearable placeholder="体力" class="filter-item">
            <el-option v-for="item in STAMINA_WEIGHT_OPTIONS" :key="item" :label="item" :value="item" />
          </el-select>
          <el-select v-model="selectedWeight" clearable placeholder="负重" class="filter-item">
            <el-option v-for="item in STAMINA_WEIGHT_OPTIONS" :key="item" :label="item" :value="item" />
          </el-select>
        </div>
      </section>

      <section class="list-panel">
        <template v-if="loading">
          <div class="row-list">
            <article v-for="i in 4" :key="i" class="row-card skeleton-row">
              <div class="cover-skeleton sk" />
              <div class="content-skeleton">
                <div class="sk line w60" />
                <div class="sk line w90" />
                <div class="metrics-skeleton">
                  <span v-for="j in 8" :key="j" class="sk metric" />
                </div>
              </div>
            </article>
          </div>
        </template>

        <div v-else-if="total === 0" class="empty-state">
          <div class="empty-icon">∅</div>
          <h3>没有符合条件的账号</h3>
          <p>可以重置筛选，或者先去“我要上架”发布一个账号。</p>
          <el-button type="warning" @click="clearFilters">重置筛选</el-button>
        </div>

        <div v-else-if="displayMode === 'list'" class="row-list">
          <article
            v-for="product in paginatedProducts"
            :key="product.id"
            class="row-card"
            @click="openDrawer(product)"
          >
            <div class="row-cover">
              <img v-if="showCover(product)" :src="product.coverImageUrl" alt="" @error="handleCoverError(product)" />
              <div v-else class="cover-fallback">
                <span>{{ product.rankText || '三角洲' }}</span>
                <strong>{{ product.insuranceBoxText || '账号' }}</strong>
              </div>
            </div>

            <div class="row-main">
              <h3>{{ product.name }}</h3>
              <p class="sub-line">
                {{ product.loginMethod || '未填写上号方式' }} ｜ {{ product.tradeTimeText || '待补充方便交易时间' }} ｜ {{ product.loginRegion || '未填写登录地区' }}
              </p>

              <div class="metric-grid">
                <div
                  v-for="metric in listAccountMetrics(product)"
                  :key="metric.label"
                  class="metric-box"
                  :class="{ 'metric-highlight': metric.highlight }"
                >
                  <strong>{{ metric.value }}</strong>
                  <span>{{ metric.label }}</span>
                </div>
              </div>
            </div>

            <div class="row-side">
              <div class="price-block">
                <span>押金：{{ formatMoney(product.deposit) }}</span>
                <strong>租金：{{ formatMoney(product.hourPrice) }}</strong>
              </div>
              <div class="action-block">
                <button class="rent-btn" type="button" @click.stop="openDrawer(product)">查看详情</button>
              </div>
            </div>
          </article>
        </div>

        <div v-else class="grid-list">
          <article
            v-for="product in paginatedProducts"
            :key="product.id"
            class="grid-card"
            @click="openDrawer(product)"
          >
            <div class="grid-cover">
              <img v-if="showCover(product)" :src="product.coverImageUrl" alt="" @error="handleCoverError(product)" />
              <div v-else class="cover-fallback">
                <span>{{ product.rankText || '三角洲' }}</span>
                <strong>{{ product.insuranceBoxText || '账号' }}</strong>
              </div>
            </div>
            <div class="grid-body">
              <h3>{{ product.name }}</h3>
              <p>{{ product.loginMethod || '未填写上号方式' }} ｜ {{ product.loginRegion || '未填写登录地区' }}</p>
              <div class="grid-metrics">
                <span v-for="metric in accountMetrics(product)" :key="metric.label">
                  {{ metric.value }} {{ metric.label }}
                </span>
              </div>
              <div class="grid-footer">
                <strong>{{ formatMoney(product.hourPrice) }}</strong>
                <el-tag size="small" :type="product.status === 'AVAILABLE' ? 'success' : 'warning'">
                  {{ product.status === 'AVAILABLE' ? '可租' : '不可租' }}
                </el-tag>
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

      <el-dialog v-model="advancedVisible" width="860px" align-center :show-close="false">
        <div class="dialog-panel">
          <div class="dialog-head">
            <div>
              <h3>高级筛选</h3>
              <p>按你给的三角洲字段做扩展过滤。</p>
            </div>
            <button class="close-btn" type="button" @click="advancedVisible = false">×</button>
          </div>

          <div class="advanced-grid">
            <div class="advanced-field">
              <label>比例</label>
              <div class="range-row">
                <el-input v-model="advancedForm.ratioMin" placeholder="最低值" />
                <span>至</span>
                <el-input v-model="advancedForm.ratioMax" placeholder="最高值" />
              </div>
            </div>
            <div class="advanced-field">
              <label>KD</label>
              <div class="range-row">
                <el-input v-model="advancedForm.kdMin" placeholder="最低值" />
                <span>至</span>
                <el-input v-model="advancedForm.kdMax" placeholder="最高值" />
              </div>
            </div>
            <div class="advanced-field">
              <label>哈夫币(万)</label>
              <div class="range-row">
                <el-input-number v-model="advancedForm.coinMin" :min="0" :controls="false" placeholder="最低值" />
                <span>至</span>
                <el-input-number v-model="advancedForm.coinMax" :min="0" :controls="false" placeholder="最高值" />
              </div>
            </div>
            <div class="advanced-field">
              <label>价格</label>
              <div class="range-row">
                <el-input-number v-model="advancedForm.priceMin" :min="0" :controls="false" placeholder="最低值" />
                <span>至</span>
                <el-input-number v-model="advancedForm.priceMax" :min="0" :controls="false" placeholder="最高值" />
              </div>
            </div>
            <div class="advanced-field">
              <label>潜水等级</label>
              <el-select v-model="advancedForm.divingLevel" clearable placeholder="请选择潜水等级">
                <el-option v-for="item in DIVING_LEVEL_OPTIONS" :key="item" :label="item" :value="item" />
              </el-select>
            </div>
            <div class="advanced-field">
              <label>刀皮</label>
              <el-select v-model="advancedForm.knifeSkin" clearable placeholder="请选择刀皮">
                <el-option v-for="item in KNIFE_SKIN_OPTIONS" :key="item" :label="item" :value="item" />
              </el-select>
            </div>
            <div class="advanced-field">
              <label>红皮</label>
              <el-select v-model="advancedForm.operatorSkin" clearable placeholder="请选择红皮">
                <el-option v-for="item in OPERATOR_SKIN_OPTIONS" :key="item" :label="item" :value="item" />
              </el-select>
            </div>
            <div class="advanced-field">
              <label>常用登录地区</label>
              <el-input v-model="advancedForm.loginRegion" placeholder="例如 四川省" />
            </div>
          </div>

          <div class="dialog-actions">
            <button class="ghost-btn" type="button" @click="resetAdvancedFilters">重置筛选</button>
            <button class="primary-btn small" type="button" @click="advancedVisible = false">确认</button>
          </div>
        </div>
      </el-dialog>

      <el-dialog v-model="drawerVisible" width="1180px" align-center :show-close="false" class="rental-detail-dialog">
        <template v-if="selectedProduct">
          <div class="dialog-panel detail-panel">
            <button class="close-btn fixed" type="button" @click="drawerVisible = false">×</button>
            <div class="detail-hero">
              <div class="detail-cover detail-cover-large">
                <img v-if="showCover(selectedProduct)" :src="selectedProduct.coverImageUrl" alt="" @error="handleCoverError(selectedProduct)" />
                <div v-else class="cover-fallback">
                  <span>{{ selectedProduct.rankText || '三角洲' }}</span>
                  <strong>{{ selectedProduct.insuranceBoxText || '账号' }}</strong>
                </div>
              </div>
              <div class="detail-summary">
                <div class="detail-kicker">账号详情 #{{ selectedProduct.id }}</div>
                <h2>{{ selectedProduct.name }}</h2>
                <div class="detail-warning-list">
                  <p>平台严禁向未成年人提供游戏账号租借服务，请勿将账号密码分享给未成年人！</p>
                  <p>不得使用或浏览器外挂等第三方软件，若违反平台规则将进行处罚。</p>
                </div>
                <div class="detail-meta-grid">
                  <div v-for="row in detailMetaRows(selectedProduct)" :key="row.label" class="detail-meta-item">
                    <span>{{ row.label }}</span>
                    <strong>{{ row.value }}</strong>
                  </div>
                </div>
                <div class="skin-preview-row">
                  <span v-for="tag in accountTags(selectedProduct)" :key="tag">{{ tag }}</span>
                </div>
                <div class="detail-actions detail-actions-hero">
                  <div class="price-inline">
                    <span>押金：{{ formatMoney(selectedProduct.deposit) }}</span>
                    <strong>租金：{{ formatMoney(selectedProduct.hourPrice) }}</strong>
                  </div>
                  <div class="action-block">
                    <button class="rent-btn" type="button" @click="auth.isLoggedIn ? goToOrder() : goToLogin()">
                      {{ auth.isLoggedIn ? '联系客服租用' : '登录后租用' }}
                    </button>
                    <button class="ghost-btn" type="button" @click="notices[0] && openNotice(notices[0])">查看大厅公告</button>
                  </div>
                </div>
              </div>
            </div>

            <section class="detail-section owner-note">
              <div>
                <span class="section-eyebrow">号主备注</span>
                <h3>{{ selectedProduct.warehouseValueText || selectedProduct.name }}</h3>
              </div>
              <p>{{ selectedProduct.description || '号主暂未补充更多备注，租用前请通过客服拉群确认在线时间和仓库物品使用规则。' }}</p>
            </section>

            <section class="detail-section">
              <div class="section-title-row">
                <div>
                  <span class="section-eyebrow">Account Metrics</span>
                  <h3>账号信息</h3>
                </div>
                <span class="status-pill">{{ statusText(selectedProduct.status) }}</span>
              </div>
              <div class="detail-grid">
                <div
                  v-for="metric in accountMetrics(selectedProduct)"
                  :key="metric.label"
                  class="metric-box"
                  :class="{ 'metric-highlight': metric.highlight }"
                >
                  <strong>{{ metric.value }}</strong>
                  <span>{{ metric.label }}</span>
                </div>
              </div>
            </section>

            <section class="detail-section">
              <div class="section-title-row">
                <div>
                  <span class="section-eyebrow">Skin Tags</span>
                  <h3>皮肤信息</h3>
                </div>
                <span>{{ skinTags(selectedProduct).length }} 个外观标签</span>
              </div>
              <div class="skin-group-list">
                <div v-for="group in skinGroups(selectedProduct)" :key="group.title" class="skin-group">
                  <span>{{ group.title }}</span>
                  <div>
                    <template v-if="group.tags.length">
                      <b v-for="tag in group.tags" :key="tag">{{ tag }}</b>
                    </template>
                    <strong v-else>暂无</strong>
                  </div>
                </div>
              </div>
            </section>

            <section class="detail-section">
              <div class="section-title-row">
                <div>
                  <span class="section-eyebrow">Screenshots</span>
                  <h3>账号截图</h3>
                </div>
                <span>截图用于验号参考，实际以客服拉群核验为准</span>
              </div>
              <div v-if="screenshotUrls(selectedProduct).length" class="screenshot-grid">
                <img v-for="url in screenshotUrls(selectedProduct)" :key="url" :src="url" alt="账号截图" />
              </div>
              <div v-else class="screenshot-empty">暂无账号截图，租用前请联系号主或客服补充核验图。</div>
            </section>

            <section class="detail-section">
              <div class="section-title-row">
                <div>
                  <span class="section-eyebrow">Description</span>
                  <h3>账号描述</h3>
                </div>
              </div>
              <div class="description-grid">
                <div v-for="row in descriptionRows(selectedProduct)" :key="row.label">
                  <span>{{ row.label }}</span>
                  <strong>{{ row.value }}</strong>
                </div>
              </div>
            </section>

            <section class="detail-section notice-rules">
              <div class="section-title-row">
                <div>
                  <span class="section-eyebrow">Rules</span>
                  <h3>租号须知</h3>
                </div>
              </div>
              <div class="notice-rule-list">
                <article v-for="rule in rentalNoticeSections" :key="rule.title">
                  <h4>{{ rule.title }}</h4>
                  <p>{{ rule.content }}</p>
                </article>
              </div>
            </section>
          </div>
        </template>
      </el-dialog>

      <el-dialog v-model="noticeVisible" width="520px" align-center :show-close="false">
        <template v-if="selectedNotice">
          <div class="dialog-panel">
            <button class="close-btn fixed" type="button" @click="noticeVisible = false">×</button>
            <div class="hero-badge">大厅公告</div>
            <h3 class="notice-title">{{ selectedNotice.title }}</h3>
            <p class="notice-content">{{ selectedNotice.content }}</p>
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
import {
  COIN_RANGE_OPTIONS,
  DIVING_LEVEL_OPTIONS,
  INSURANCE_OPTIONS,
  KNIFE_SKIN_OPTIONS,
  LOGIN_METHOD_OPTIONS,
  OPERATOR_SKIN_OPTIONS,
  PRICE_RANGE_OPTIONS,
  QUICK_ZONE_OPTIONS,
  RANK_OPTIONS,
  STAMINA_WEIGHT_OPTIONS
} from '@/constants/rental';

type DisplayMode = 'list' | 'grid';
type SortKey = 'default' | 'price' | 'ratio';
type SortValue = 'default' | 'price_asc' | 'price_desc' | 'ratio_asc' | 'ratio_desc';
type DetailRow = { label: string; value: string };

const auth = useAuthStore();
const router = useRouter();

const loading = ref(true);
const rawProducts = ref<RentalProduct[]>([]);
const keyword = ref('');
const displayMode = ref<DisplayMode>('list');
const sortBy = ref<SortValue>('default');
const currentPage = ref(1);
const pageSize = 6;
const currentBannerIndex = ref(0);
const activeZone = ref('all');

const selectedLoginMethod = ref('');
const selectedCoinRange = ref('');
const selectedInsuranceBox = ref('');
const selectedStamina = ref('');
const selectedWeight = ref('');
const selectedKnifeSkin = ref('');
const selectedOperatorSkin = ref('');
const selectedPriceRange = ref('');
const selectedRank = ref('');
const selectedStatus = ref('');

const advancedVisible = ref(false);
const advancedForm = reactive({
  ratioMin: '',
  ratioMax: '',
  kdMin: '',
  kdMax: '',
  coinMin: undefined as number | undefined,
  coinMax: undefined as number | undefined,
  priceMin: undefined as number | undefined,
  priceMax: undefined as number | undefined,
  divingLevel: '',
  knifeSkin: '',
  operatorSkin: '',
  loginRegion: ''
});

const drawerVisible = ref(false);
const selectedProduct = ref<RentalProduct | null>(null);
const failedCovers = ref<Set<number>>(new Set());

function showCover(product: RentalProduct) {
  return !!product.coverImageUrl && !failedCovers.value.has(product.id);
}

function handleCoverError(product: RentalProduct) {
  const next = new Set(failedCovers.value);
  next.add(product.id);
  failedCovers.value = next;
}

const noticeVisible = ref(false);
const selectedNotice = ref<PortalNotice | null>(null);
const notices = ref<PortalNotice[]>([]);

const bannerSlides = [
  {
    title: '筛选三角洲账号，按刀皮、红皮、段位和哈夫币快速定位',
    desc: '大厅默认采用横向信息卡视图，同时支持切换卡片视图。'
  },
  {
    title: '玩家可直接提交上架信息，后台审核后同步到大厅',
    desc: '上架页已支持登录方式、押金、保险箱、刀皮、红皮、KD、潜水等级等字段。'
  },
  {
    title: '现在所有筛选项都改成三角洲字段口径',
    desc: '去掉了无关文案，前台展示和后台录入使用同一套字段体系。'
  }
];

const currentBanner = computed(() => bannerSlides[currentBannerIndex.value] ?? bannerSlides[0]);

const noticeItems = computed(() => {
  if (notices.value.length) return notices.value.slice(0, 4);
  return [
    { id: 1, title: '发布与审核说明', content: '玩家提交上架后默认进入待审核状态，管理员确认后上架。' },
    { id: 2, title: '字段说明', content: '哈夫币单位为“万”，100M 请填写 10000。' },
    { id: 3, title: '图片说明', content: '封面图当前为 URL 形式，后续可以接文件上传。' },
    { id: 4, title: '大厅筛选说明', content: '筛选项已统一切换为三角洲相关字段。' }
  ];
});

const zoneCards = computed(() => QUICK_ZONE_OPTIONS.map(zone => ({
  ...zone,
  count: rawProducts.value.filter(item => matchesZone(item, zone.key)).length
})));

const sortOptions = [
  { label: '默认排序', key: 'default' as SortKey },
  { label: '价格排序', key: 'price' as SortKey },
  { label: '比例排序', key: 'ratio' as SortKey }
];

const rentalNoticeSections = [
  {
    title: '1. 验号与沟通',
    content: '拉群后请优先和号主确认在线时间、仓库物品使用规则。发现账号内容不符请在上号后20分钟内指出并保留证据，逾期默认验号通过；租号期间纠纷由平台客服依据证据处理。'
  },
  {
    title: '2. 仓库物资规则',
    content: '号主默认仅提供纯哈夫币使用，不可擅自动用纯币以外物品。默认参考：AWM子弹0.8元/发、咖啡豆4元/袋、高级子弹零件4元/个、满耐六级头1.5元、六级甲2.5元、45格红包2元/个、非45格红包1.5元/个、5级全装包5元/个、6级子弹10元/组、九格体验卡5元/天，实际以群内协商为准。'
  },
  {
    title: '3. 退款与违约',
    content: '账号租用为虚拟商品，下单后原则上不支持无理由退换。因非卖家问题申请退款，需按规则扣除违约金，最低20元，由平台方与无责方各半收取。'
  },
  {
    title: '4. 活动与新增道具',
    content: '租期内主动做活动或任务获得的道具可免费使用，但通行证奖励或活动直接赠送物品不包含在内，未经协商擅自动用按仓库物资规则赔付。'
  },
  {
    title: '5. 押金退还',
    content: '交易确认账号无问题且无经济纠纷后，押金立即退还。若租期内存在短封记录，押金延长至24小时退还；押金主要用于封禁、洗号等违规赔付。'
  },
  {
    title: '6. 失联与上号配合',
    content: '号主超过12小时失联，或人脸/上号两次以上无法在一小时内配合，可申请取消订单并按消耗金额80%结算；凌晨1点至9点不计入失联时间。'
  },
  {
    title: '7. 沟通纪律',
    content: '平台不偏袒任何一方，双方应在群内友好协商。禁止辱骂、私聊或私加好友，违反规则可能扣款、强制结单或拉黑处理。'
  },
  {
    title: '8. 登录边界',
    content: '租客不得登录出租账号的其他游戏；号主租期内原则上不得擅自登录三角洲或通过小号功能登录其他端游。未经同意顶号造成损失由责任方承担。'
  },
  {
    title: '账号封禁赔付篇',
    content: '1-30天封禁按消耗进度、免责期与超期费计算，封禁30天押金扣除上限为300元；10年封禁基本视为外挂/DMA等严重违规，订单到手金额及全额押金给号主并拉黑；租用后7天内追封、追缴，经核实由租用期间行为导致的仍需补偿。'
  },
  {
    title: '免责声明',
    content: '未成年人不得参与游戏账号租售交易。请勿脱离平台交易，否则损失平台概不负责。平台数字化商品不支持七天无理由退货及三包服务，租用默认视为已阅读并同意本须知。'
  }
];

const totalLoaded = computed(() => rawProducts.value.length);
const availableCount = computed(() => rawProducts.value.filter(item => item.status === 'AVAILABLE').length);
const pendingCount = computed(() => rawProducts.value.filter(item => item.status === 'MAINTENANCE').length);

const filteredProducts = computed(() => {
  let list = rawProducts.value.slice().filter(item => matchesZone(item, activeZone.value));

  if (keyword.value.trim()) {
    const text = keyword.value.trim().toLowerCase();
    list = list.filter(item =>
      [item.name, item.rankText, item.knifeSkinText, item.characterSkinText, item.weaponSkinText]
        .some(value => (value || '').toLowerCase().includes(text))
    );
  }

  if (selectedLoginMethod.value) {
    list = list.filter(item => item.loginMethod === selectedLoginMethod.value);
  }
  if (selectedInsuranceBox.value) {
    list = list.filter(item => item.insuranceBoxText === selectedInsuranceBox.value);
  }
  if (selectedKnifeSkin.value) {
    list = list.filter(item => hasTag(item.knifeSkinText, selectedKnifeSkin.value));
  }
  if (selectedOperatorSkin.value) {
    list = list.filter(item => hasTag(item.characterSkinText, selectedOperatorSkin.value));
  }
  if (selectedStamina.value) {
    list = list.filter(item => item.staminaText === selectedStamina.value);
  }
  if (selectedWeight.value) {
    list = list.filter(item => item.weightText === selectedWeight.value);
  }
  if (selectedRank.value) {
    list = list.filter(item => item.rankText === selectedRank.value);
  }
  if (selectedStatus.value) {
    list = list.filter(item => item.status === selectedStatus.value);
  }
  if (selectedCoinRange.value) {
    list = list.filter(item => matchCoinRange(item.coinAmount, selectedCoinRange.value));
  }
  if (selectedPriceRange.value) {
    list = list.filter(item => matchPriceRange(Number(item.hourPrice || 0), selectedPriceRange.value));
  }

  if (advancedForm.coinMin !== undefined) {
    list = list.filter(item => Number(item.coinAmount || 0) >= advancedForm.coinMin!);
  }
  if (advancedForm.coinMax !== undefined) {
    list = list.filter(item => Number(item.coinAmount || 0) <= advancedForm.coinMax!);
  }
  if (advancedForm.priceMin !== undefined) {
    list = list.filter(item => Number(item.hourPrice || 0) >= advancedForm.priceMin!);
  }
  if (advancedForm.priceMax !== undefined) {
    list = list.filter(item => Number(item.hourPrice || 0) <= advancedForm.priceMax!);
  }
  if (advancedForm.divingLevel) {
    list = list.filter(item => item.divingLevelText === advancedForm.divingLevel);
  }
  if (advancedForm.knifeSkin) {
    list = list.filter(item => hasTag(item.knifeSkinText, advancedForm.knifeSkin));
  }
  if (advancedForm.operatorSkin) {
    list = list.filter(item => hasTag(item.characterSkinText, advancedForm.operatorSkin));
  }
  if (advancedForm.loginRegion.trim()) {
    const region = advancedForm.loginRegion.trim().toLowerCase();
    list = list.filter(item => (item.loginRegion || '').toLowerCase().includes(region));
  }
  if (advancedForm.kdMin || advancedForm.kdMax) {
    list = list.filter(item => matchFloatRange(item.kdText, advancedForm.kdMin, advancedForm.kdMax));
  }
  if (advancedForm.ratioMin || advancedForm.ratioMax) {
    list = list.filter(item => matchRatioRange(item.ratioText, advancedForm.ratioMin, advancedForm.ratioMax));
  }

  if (sortBy.value === 'price_asc') {
    list = list.slice().sort((a, b) => Number(a.hourPrice || 0) - Number(b.hourPrice || 0));
  } else if (sortBy.value === 'price_desc') {
    list = list.slice().sort((a, b) => Number(b.hourPrice || 0) - Number(a.hourPrice || 0));
  } else if (sortBy.value === 'ratio_asc') {
    list = list.slice().sort((a, b) => parseRatio(a.ratioText) - parseRatio(b.ratioText));
  } else if (sortBy.value === 'ratio_desc') {
    list = list.slice().sort((a, b) => parseRatio(b.ratioText) - parseRatio(a.ratioText));
  }

  return list;
});

const total = computed(() => filteredProducts.value.length);
const paginatedProducts = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  return filteredProducts.value.slice(start, start + pageSize);
});

function matchesZone(item: RentalProduct, zone: string) {
  if (zone === 'all') return true;
  if (zone === 'cheap') return Number(item.hourPrice || 0) < 300;
  if (zone === 'rich') return Number(item.coinAmount || 0) >= 10000;
  if (zone === 'hawk') return item.rankText === '黑鹰' || item.rankText === '三角洲巅峰';
  if (zone === 'skin') return !!item.characterSkinText;
  return true;
}

function matchCoinRange(value: number, key: string) {
  const coin = Number(value || 0);
  if (key === 'coin_lt_5000') return coin < 5000;
  if (key === 'coin_3000_15000') return coin >= 3000 && coin <= 15000;
  if (key === 'coin_10000_30000') return coin >= 10000 && coin <= 30000;
  if (key === 'coin_gt_30000') return coin > 30000;
  return true;
}

function matchPriceRange(value: number, key: string) {
  if (key === 'price_lt_300') return value < 300;
  if (key === 'price_300_1000') return value >= 300 && value <= 1000;
  if (key === 'price_1000_2000') return value > 1000 && value <= 2000;
  if (key === 'price_gt_2000') return value > 2000;
  return true;
}

function matchFloatRange(raw: string | undefined, min: string, max: string) {
  const value = Number(raw || 0);
  const minValue = min ? Number(min) : undefined;
  const maxValue = max ? Number(max) : undefined;
  if (minValue !== undefined && !Number.isNaN(minValue) && value < minValue) return false;
  if (maxValue !== undefined && !Number.isNaN(maxValue) && value > maxValue) return false;
  return true;
}

function parseRatio(raw: string | undefined) {
  if (!raw) return 9999;
  const [, right] = raw.split(':');
  return Number(right || raw);
}

function matchRatioRange(raw: string | undefined, min: string, max: string) {
  const value = parseRatio(raw);
  const minValue = min ? Number(min) : undefined;
  const maxValue = max ? Number(max) : undefined;
  if (minValue !== undefined && !Number.isNaN(minValue) && value < minValue) return false;
  if (maxValue !== undefined && !Number.isNaN(maxValue) && value > maxValue) return false;
  return true;
}

function formatCoinDisplay(value: number | undefined) {
  const amount = Number(value || 0);
  if (amount >= 10000) return `${(amount / 10000).toFixed(2)}亿`;
  return `${amount}万`;
}

function formatMoney(value: number | string | undefined) {
  return `¥${Number(value || 0).toFixed(0)}`;
}

function formatStaminaWeight(item: RentalProduct) {
  const stamina = item.staminaText || '-';
  const weight = item.weightText || '-';
  return `${stamina}/${weight}`;
}

function formatHeadArmor(item: RentalProduct) {
  return `${item.helmetCount ?? 0}/${item.armorCount ?? 0}`;
}

function formatRankKd(item: RentalProduct) {
  return `${item.rankText || '-'}/${item.kdText || '-'}`;
}

function splitTags(raw: string | undefined | null) {
  return Array.from(new Set((raw || '')
    .split(/[,\uFF0C\u3001/|\s]+/)
    .map(tag => tag.trim())
    .filter(Boolean)));
}

function hasTag(raw: string | undefined | null, target: string) {
  const text = (raw || '').trim();
  if (!text) return false;
  const tags = splitTags(text);
  return tags.includes(target) || text.includes(target);
}

function statusText(status: string) {
  const map: Record<string, string> = {
    AVAILABLE: '可租用',
    RENTED: '已租出',
    MAINTENANCE: '维护中',
    PENDING: '待审核',
    OFF_SHELF: '已下架'
  };
  return map[status] || status || '-';
}

function formatBanRecord(item: RentalProduct) {
  return item.recentBanRecord || '无';
}

function accountMetrics(item: RentalProduct) {
  return [
    { label: '哈夫币数量', value: formatCoinDisplay(item.coinAmount), highlight: true },
    { label: '比例', value: item.ratioText || '-', highlight: true },
    { label: '保险箱', value: item.insuranceBoxText || '-', highlight: false },
    { label: '特勤处', value: formatStaminaWeight(item), highlight: false },
    { label: '头/甲', value: formatHeadArmor(item), highlight: false },
    { label: 'AWM子弹', value: String(item.awmAmmoCount ?? 0), highlight: false },
    { label: '等级', value: String(item.level ?? '-'), highlight: false },
    { label: '段位/kd', value: formatRankKd(item), highlight: false },
    { label: '9格体验卡', value: `${item.nineGridTrialCardCount ?? 0}张`, highlight: false },
    { label: '近90天封禁', value: formatBanRecord(item), highlight: false }
  ];
}

function listAccountMetrics(item: RentalProduct) {
  return accountMetrics(item).filter(metric => !['9格体验卡', '近90天封禁'].includes(metric.label));
}

function skinTags(item: RentalProduct) {
  return [
    ...splitTags(item.knifeSkinText),
    ...splitTags(item.characterSkinText),
    ...splitTags(item.weaponSkinText)
  ];
}

function accountTags(item: RentalProduct) {
  return Array.from(new Set([
    item.rankText,
    item.insuranceBoxText,
    item.ratioText,
    ...splitTags(item.tagText),
    ...skinTags(item)
  ].filter(Boolean) as string[])).slice(0, 12);
}

function skinGroups(item: RentalProduct) {
  return [
    { title: '刀皮', tags: splitTags(item.knifeSkinText) },
    { title: '红皮', tags: splitTags(item.characterSkinText) },
    { title: '武器皮肤 / 资源', tags: splitTags(item.weaponSkinText) }
  ];
}

function detailMetaRows(item: RentalProduct): DetailRow[] {
  return [
    { label: '上号方式', value: item.loginMethod || '-' },
    { label: '商品编号', value: String(item.id) },
    { label: '登录地区', value: item.loginRegion || '-' },
    { label: '交易时间', value: item.tradeTimeText || '-' },
    { label: '商品类型', value: item.category || '-' },
    { label: '近90天封禁', value: formatBanRecord(item) }
  ];
}

function descriptionRows(item: RentalProduct): DetailRow[] {
  return [
    { label: '账号标题', value: item.name || '-' },
    { label: '仓库价值 / 简介', value: item.warehouseValueText || '-' },
    { label: '标签', value: splitTags(item.tagText).join('、') || '-' },
    { label: '刀皮', value: splitTags(item.knifeSkinText).join('、') || '-' },
    { label: '红皮', value: splitTags(item.characterSkinText).join('、') || '-' },
    { label: '武器皮肤 / 额外资源', value: item.weaponSkinText || '-' },
    { label: '常用登录地区', value: item.loginRegion || '-' },
    { label: '号主说明', value: item.description || '-' }
  ];
}

function screenshotUrls(item: RentalProduct) {
  const urls = ((item as RentalProduct & { imageUrls?: string[] }).imageUrls || [])
    .concat(showCover(item) && item.coverImageUrl ? [item.coverImageUrl] : [])
    .filter((url): url is string => !!url);
  return Array.from(new Set(urls)).slice(0, 6);
}

function isSortActive(key: SortKey) {
  if (key === 'default') return sortBy.value === 'default';
  return sortBy.value.startsWith(`${key}_`);
}

function getSortArrow(key: Exclude<SortKey, 'default'>) {
  if (sortBy.value === `${key}_asc`) return '↑';
  if (sortBy.value === `${key}_desc`) return '↓';
  return '↕';
}

function toggleSort(key: SortKey) {
  if (key === 'default') {
    sortBy.value = 'default';
    return;
  }

  if (sortBy.value === `${key}_asc`) {
    sortBy.value = `${key}_desc`;
    return;
  }

  sortBy.value = `${key}_asc`;
}

function applyZone(zone: string) {
  activeZone.value = zone;
}

function clearFilters() {
  keyword.value = '';
  selectedLoginMethod.value = '';
  selectedCoinRange.value = '';
  selectedInsuranceBox.value = '';
  selectedStamina.value = '';
  selectedWeight.value = '';
  selectedKnifeSkin.value = '';
  selectedOperatorSkin.value = '';
  selectedPriceRange.value = '';
  selectedRank.value = '';
  selectedStatus.value = '';
  sortBy.value = 'default';
  resetAdvancedFilters();
  currentPage.value = 1;
}

function resetAdvancedFilters() {
  advancedForm.ratioMin = '';
  advancedForm.ratioMax = '';
  advancedForm.kdMin = '';
  advancedForm.kdMax = '';
  advancedForm.coinMin = undefined;
  advancedForm.coinMax = undefined;
  advancedForm.priceMin = undefined;
  advancedForm.priceMax = undefined;
  advancedForm.divingLevel = '';
  advancedForm.knifeSkin = '';
  advancedForm.operatorSkin = '';
  advancedForm.loginRegion = '';
}

function openNotice(notice: PortalNotice) {
  selectedNotice.value = notice;
  noticeVisible.value = true;
}

function openDrawer(product: RentalProduct) {
  selectedProduct.value = product;
  drawerVisible.value = true;
}

function goToOrder() {
  if (!selectedProduct.value) return;
  sessionStorage.setItem('detailProduct', JSON.stringify(selectedProduct.value));
  router.push({
    path: '/orders/create',
    query: {
      accountId: String(selectedProduct.value.id)
    }
  });
  drawerVisible.value = false;
}

function goToLogin() {
  router.push('/login');
}

function goToPublish() {
  if (!auth.isLoggedIn) {
    router.push('/login');
    return;
  }
  router.push('/rentals/publish');
}

function loadBannerRotation() {
  const timer = window.setInterval(() => {
    currentBannerIndex.value = (currentBannerIndex.value + 1) % bannerSlides.length;
  }, 4200);
  return timer;
}

async function loadRentals() {
  loading.value = true;
  try {
    const response = await getRentals({
      page: 1,
      pageSize: 1000
    });
    if (!response.data.success) {
      ElMessage.error(response.data.message || '租号列表加载失败');
      return;
    }
    rawProducts.value = response.data.data?.list ?? [];
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

let bannerTimer: number | null = null;

watch(
  [
    keyword, activeZone, sortBy,
    selectedLoginMethod, selectedCoinRange, selectedInsuranceBox,
    selectedStamina, selectedWeight, selectedKnifeSkin, selectedOperatorSkin,
    selectedPriceRange, selectedRank, selectedStatus
  ],
  () => { currentPage.value = 1; }
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
  bannerTimer = loadBannerRotation();
});

onUnmounted(() => {
  if (bannerTimer !== null) clearInterval(bannerTimer);
});
</script>

<script lang="ts">
export default { name: 'RentalListView' };
</script>

<style scoped>
.rental-page {
  min-height: calc(100vh - 64px);
}

.page-inner {
  max-width: 1240px;
  margin: 0 auto;
  padding: 28px 24px 40px;
}

.hero-board {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 300px;
  gap: 16px;
  margin-bottom: 18px;
}

.hero-banner,
.notice-panel,
.toolbar-panel,
.list-panel,
.dialog-panel {
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(226, 232, 240, 0.9);
  border-radius: 24px;
  box-shadow: 0 16px 36px rgba(15, 23, 42, 0.06);
}

.hero-banner {
  position: relative;
  overflow: hidden;
  min-height: 300px;
  padding: 28px 30px 24px;
  background: linear-gradient(135deg, #fff2bf 0%, #ffd96a 100%);
  border: none;
}

.hero-copy h1 {
  margin: 16px 0 10px;
  font-size: 36px;
  line-height: 1.18;
  color: #4a3200;
}

.hero-copy p {
  margin: 0;
  max-width: 560px;
  color: #6b7280;
  line-height: 1.8;
}

.hero-badge,
.notice-badge {
  display: inline-flex;
  height: 30px;
  align-items: center;
  padding: 0 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.72);
  color: #9b5d00;
  font-size: 12px;
  font-weight: 800;
}

.hero-actions,
.toolbar-actions,
.dialog-actions,
.detail-actions,
.action-block {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.hero-actions {
  margin-top: 22px;
}

.primary-btn,
.ghost-btn,
.danger-btn,
.rent-btn,
.confirm-btn {
  height: 42px;
  padding: 0 16px;
  border-radius: 14px;
  border: 0;
  font-size: 14px;
  font-weight: 800;
  cursor: pointer;
}

.primary-btn,
.rent-btn {
  background: linear-gradient(135deg, #ffe057 0%, #ffc420 100%);
  color: #5a3c00;
}

.primary-btn.small {
  height: 38px;
}

.ghost-btn {
  background: #fff;
  border: 1px solid #e2e8f0;
  color: #64748b;
}

.danger-btn {
  background: #fff1f2;
  color: #ef4444;
}

.confirm-btn {
  background: #fff5ef;
  color: #ff8a4c;
}

.hero-stats {
  display: flex;
  gap: 12px;
  margin-top: 26px;
}

.hero-stat {
  padding: 12px 14px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.55);
  min-width: 110px;
}

.hero-stat strong {
  display: block;
  font-size: 28px;
  color: #5a3c00;
}

.hero-stat span {
  display: block;
  margin-top: 4px;
  font-size: 12px;
  color: #7c6b39;
}

.hero-dots {
  position: absolute;
  left: 30px;
  bottom: 18px;
  display: flex;
  gap: 8px;
}

.hero-dot {
  width: 30px;
  height: 4px;
  border: 0;
  border-radius: 999px;
  background: rgba(90, 60, 0, 0.2);
}

.hero-dot.active {
  background: #5a3c00;
}

.notice-panel {
  padding: 18px;
}

.notice-head {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.notice-item {
  width: 100%;
  padding: 18px 4px;
  border: 0;
  border-bottom: 1px solid #f1f5f9;
  background: transparent;
  display: flex;
  align-items: center;
  justify-content: space-between;
  text-align: left;
  cursor: pointer;
}

.notice-item em {
  font-style: normal;
  color: #94a3b8;
}

.zone-strip {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 10px;
  margin-bottom: 14px;
}

.zone-card {
  min-height: 72px;
  border: 1px solid rgba(226, 232, 240, 0.9);
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.92);
  padding: 12px 14px;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  cursor: pointer;
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.03);
}

.zone-card.active {
  border-color: #ffd46b;
  background: #fff7e6;
}

.zone-text {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.zone-text strong {
  font-size: 18px;
}

.zone-text span {
  color: #64748b;
  font-size: 13px;
}

.zone-card b {
  font-size: 26px;
  color: #c57a00;
}

.toolbar-panel {
  padding: 14px 18px;
  margin-bottom: 14px;
}

.toolbar-top,
.filter-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 10px;
  flex-wrap: wrap;
}

.filter-search {
  flex: 1.6 1 320px;
  min-width: 160px;
}

.filter-item {
  flex: 1 1 140px;
  min-width: 140px;
}

.sort-tabs {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.sort-tab {
  height: 38px;
  padding: 0 14px;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  background: #fff;
  color: #64748b;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
}

.sort-tab.active {
  background: #fff0d0;
  border-color: #ffd46b;
  color: #9b5d00;
}

.sort-arrow {
  margin-left: 6px;
  font-size: 12px;
  line-height: 1;
}

.filter-row.multi {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 10px;
}

.keyword-item {
  grid-column: span 2;
}

.list-panel {
  padding: 18px;
}

.row-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.row-card {
  display: grid;
  grid-template-columns: 200px minmax(0, 1fr) 168px;
  gap: 14px;
  padding: 14px;
  border: 1px solid #f0e7c8;
  border-radius: 22px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.96) 0%, rgba(240, 255, 248, 0.86) 100%);
  cursor: pointer;
  align-items: center;
}

.row-cover,
.grid-cover,
.detail-cover {
  border-radius: 18px;
  overflow: hidden;
  background: #f8fafc;
}

.row-cover,
.grid-cover {
  aspect-ratio: 1 / 1;
  align-self: center;
}

.row-cover img,
.grid-cover img,
.detail-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.cover-fallback {
  min-height: 170px;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 16px;
  background: linear-gradient(135deg, #111827 0%, #374151 100%);
  color: #fff;
}

.cover-fallback strong {
  font-size: 28px;
  line-height: 1.1;
}

.row-main h3,
.grid-body h3,
.detail-main h2 {
  margin: 0;
  color: #1f2937;
}

.row-main h3 {
  font-size: 24px;
  line-height: 1.25;
}

.sub-line,
.grid-body p,
.detail-main p,
.notice-content {
  color: #64748b;
  line-height: 1.75;
}

.sub-line {
  margin: 10px 0 16px;
}

.metric-grid,
.detail-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
}

.metric-box {
  padding: 12px 14px;
  border-radius: 16px;
  background: #f5f7fb;
}

.metric-highlight {
  background: linear-gradient(180deg, #fff7dc 0%, #fffbee 100%);
}

.metric-box strong {
  display: block;
  font-size: 22px;
  color: #1f2937;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.metric-box span {
  display: block;
  margin-top: 6px;
  color: #64748b;
  font-size: 12px;
}

.row-side {
  border-left: 1px solid #f1f5f9;
  padding-left: 12px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 18px;
}

.price-block strong {
  display: block;
  margin-top: 6px;
  font-size: 34px;
  color: #f05b2c;
}

.price-block span {
  color: #64748b;
}

.grid-list {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.grid-card {
  border-radius: 22px;
  overflow: hidden;
  border: 1px solid #f0e7c8;
  background: rgba(255, 255, 255, 0.96);
  cursor: pointer;
}

.grid-cover {
  min-height: 180px;
}

.grid-body {
  padding: 16px;
}

.grid-body p {
  margin: 10px 0 14px;
}

.grid-metrics {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 14px;
}

.grid-metrics span {
  display: inline-flex;
  height: 28px;
  align-items: center;
  padding: 0 10px;
  border-radius: 999px;
  background: #f5f7fb;
  font-size: 12px;
  color: #475569;
}

.grid-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.grid-footer strong {
  font-size: 28px;
  color: #f05b2c;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 22px;
}

.empty-state {
  min-height: 320px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.empty-icon {
  font-size: 56px;
}

.dialog-panel {
  position: relative;
  padding: 24px;
  background: #fffef9;
}

.dialog-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.dialog-head h3,
.notice-title {
  margin: 0;
  font-size: 28px;
  color: #1f2937;
}

.dialog-head p {
  margin: 8px 0 0;
  color: #64748b;
}

.close-btn {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  border: 0;
  background: #f5f7fb;
  color: #94a3b8;
  font-size: 24px;
  cursor: pointer;
}

.close-btn.fixed {
  position: absolute;
  top: 18px;
  right: 18px;
}

.advanced-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px 22px;
  margin-top: 22px;
}

.advanced-field label {
  display: block;
  margin-bottom: 10px;
  font-size: 14px;
  font-weight: 800;
}

.range-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 26px minmax(0, 1fr);
  gap: 8px;
  align-items: center;
}

.range-row span {
  text-align: center;
  color: #94a3b8;
}

.detail-panel {
  padding-top: 48px;
  max-height: 86vh;
  overflow-y: auto;
}

.detail-hero {
  display: grid;
  grid-template-columns: minmax(320px, 420px) minmax(0, 1fr);
  gap: 22px;
  align-items: stretch;
}

.detail-cover-large {
  min-height: 330px;
  box-shadow: inset 0 0 0 1px rgba(15, 23, 42, 0.06);
}

.detail-cover-large .cover-fallback {
  min-height: 330px;
}

.detail-summary {
  min-width: 0;
  padding: 4px 0;
}

.detail-kicker,
.section-eyebrow {
  display: inline-flex;
  align-items: center;
  color: #b77900;
  font-size: 12px;
  font-weight: 900;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.detail-summary h2 {
  margin: 8px 0 12px;
  color: #1f2937;
  font-size: 34px;
  line-height: 1.18;
}

.detail-warning-list {
  display: grid;
  gap: 8px;
  margin-bottom: 16px;
}

.detail-warning-list p {
  margin: 0;
  border-radius: 14px;
  padding: 10px 12px;
  background: #fff7ed;
  color: #c2410c;
  font-size: 13px;
  font-weight: 700;
}

.detail-meta-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

.detail-meta-item {
  border-radius: 16px;
  padding: 12px 14px;
  background: #f8fafc;
}

.detail-meta-item span,
.section-title-row > span,
.skin-group > span,
.description-grid span {
  display: block;
  color: #64748b;
  font-size: 12px;
}

.detail-meta-item strong {
  display: block;
  margin-top: 5px;
  font-size: 15px;
  color: #1f2937;
}

.skin-preview-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 14px;
}

.skin-preview-row span,
.status-pill,
.skin-group b {
  display: inline-flex;
  align-items: center;
  min-height: 28px;
  padding: 0 10px;
  border-radius: 999px;
  background: #111827;
  color: #fff;
  font-size: 12px;
  font-weight: 800;
}

.detail-actions-hero {
  align-items: center;
  justify-content: space-between;
  margin-top: 18px;
  padding-top: 18px;
  border-top: 1px solid #f1f5f9;
}

.detail-section {
  margin-top: 20px;
  padding: 18px;
  border: 1px solid #edf2f7;
  border-radius: 22px;
  background: #fff;
}

.owner-note {
  display: grid;
  grid-template-columns: 240px minmax(0, 1fr);
  gap: 18px;
  background: linear-gradient(135deg, #fffaf0 0%, #ffffff 100%);
}

.owner-note h3,
.section-title-row h3 {
  margin: 4px 0 0;
  color: #1f2937;
  font-size: 22px;
}

.owner-note p {
  margin: 0;
  color: #475569;
  line-height: 1.8;
}

.section-title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 14px;
  margin-bottom: 14px;
}

.status-pill {
  background: #e0f2fe;
  color: #0369a1;
}

.skin-group-list {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.skin-group {
  min-height: 112px;
  border-radius: 18px;
  padding: 14px;
  background: #f8fafc;
}

.skin-group div {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.skin-group b {
  background: #fff7ed;
  color: #c2410c;
}

.skin-group strong {
  color: #94a3b8;
}

.screenshot-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.screenshot-grid img,
.screenshot-empty {
  width: 100%;
  min-height: 220px;
  border-radius: 18px;
  background: #f8fafc;
  object-fit: cover;
}

.screenshot-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
  font-weight: 800;
}

.description-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.description-grid div {
  border-radius: 16px;
  padding: 14px 16px;
  background: #f8fafc;
}

.description-grid strong {
  display: block;
  margin-top: 6px;
  color: #1f2937;
  line-height: 1.6;
}

.notice-rules {
  background: #fffef9;
}

.notice-rule-list {
  display: grid;
  gap: 12px;
}

.notice-rule-list article {
  border-radius: 18px;
  padding: 14px 16px;
  background: #f8fafc;
}

.notice-rule-list h4 {
  margin: 0 0 8px;
  color: #1f2937;
}

.notice-rule-list p {
  margin: 0;
  color: #475569;
  line-height: 1.85;
}

.duration-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
  margin-top: 20px;
}

.duration-card {
  min-height: 88px;
  border-radius: 18px;
  border: 1px solid #e2e8f0;
  background: #f8fafc;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
}

.duration-card.active {
  border-color: #ffd46b;
  background: #fff7e6;
}

.duration-card.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.price-inline {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.price-inline strong {
  font-size: 28px;
  color: #f05b2c;
}

.sk {
  background: linear-gradient(90deg, #f2f4f8 0%, #f8fafc 50%, #f2f4f8 100%);
  background-size: 200% 100%;
  animation: shimmer 1.4s infinite linear;
  border-radius: 12px;
}

.cover-skeleton {
  min-height: 180px;
}

.content-skeleton {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-top: 6px;
}

.line {
  height: 16px;
}

.metrics-skeleton {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
}

.metric {
  height: 76px;
}

.w60 { width: 60%; }
.w90 { width: 90%; }

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

:deep(.el-input__wrapper),
:deep(.el-select__wrapper),
:deep(.el-textarea__inner) {
  background: #fff !important;
  border-radius: 12px !important;
  box-shadow: 0 0 0 1px #dbe3ef inset !important;
}

:deep(.rental-detail-dialog) {
  max-width: calc(100vw - 28px);
}

@media (max-width: 1180px) {
  .hero-board {
    grid-template-columns: 1fr;
  }

  .zone-strip {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .filter-search,
  .filter-item {
    flex: 1 1 calc(33.33% - 10px);
    min-width: 140px;
  }

  .row-card {
    grid-template-columns: 200px minmax(0, 1fr);
  }

  .row-side {
    grid-column: 1 / -1;
    border-left: 0;
    border-top: 1px solid #f1f5f9;
    padding-left: 0;
    padding-top: 14px;
  }

  .detail-hero,
  .owner-note {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 900px) {
  .page-inner {
    padding: 18px 14px 30px;
  }

  .zone-strip,
  .advanced-grid,
  .detail-hero,
  .detail-meta-grid,
  .skin-group-list,
  .screenshot-grid,
  .description-grid,
  .metric-grid,
  .detail-grid,
  .duration-grid,
  .grid-list {
    grid-template-columns: 1fr;
  }

  .detail-panel {
    padding: 48px 16px 18px;
  }

  .detail-summary h2 {
    font-size: 26px;
  }

  .filter-search,
  .filter-item {
    flex: 1 1 100%;
    min-width: 0;
  }

  .keyword-item {
    grid-column: auto;
  }

  .toolbar-top {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
