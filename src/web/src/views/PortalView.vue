<template>
  <div class="page-shell warm-theme">

    <!-- Hero 区域 -->
    <section class="hero-section">
      <canvas ref="heroCanvas" class="hero-particle" />
      <div class="hero-content">
        <div class="hero-badge">战术租赁平台</div>
        <h1 class="hero-title">{{ summary.heroTitle }}</h1>
        <p class="hero-subtitle">{{ summary.heroSubtitle }}</p>
        <div class="hero-actions">
          <template v-if="auth.isLoggedIn">
            <el-button type="primary" size="large" class="btn-primary-action" @click="$router.push('/orders/create')">
              立即下单
            </el-button>
            <el-button size="large" @click="$router.push('/rentals')">浏览账号</el-button>
          </template>
          <template v-else>
            <el-button type="primary" size="large" class="btn-primary-action" @click="$router.push('/login')">
              登录下单
            </el-button>
            <el-button size="large" @click="$router.push('/rentals')">先逛逛</el-button>
          </template>
        </div>
      </div>
    </section>

    <!-- 热门账号预览 -->
    <section class="hot-section">
      <div class="section-header">
        <h2 class="section-title">热门账号</h2>
        <router-link to="/rentals" class="see-all-link">查看全部 →</router-link>
      </div>
      <div class="hot-grid">
        <template v-if="hotLoading">
          <div v-for="i in 3" :key="i" class="product-card skeleton-card">
            <div class="sk-pulse sk-line" style="height:16px; width:60%; margin-bottom:12px;" />
            <div class="sk-pulse sk-line" style="height:14px; width:90%; margin-bottom:8px;" />
            <div class="sk-pulse sk-line" style="height:14px; width:75%; margin-bottom:8px;" />
            <div class="sk-pulse sk-line" style="height:14px; width:80%;" />
          </div>
        </template>
        <template v-else-if="hotProducts.length">
          <div
            v-for="p in hotProducts"
            :key="p.id"
            class="product-card"
            @click="$router.push('/rentals')"
          >
            <div class="product-header">
              <span class="product-tag-pill">{{ p.tag }}</span>
              <el-tag size="small" :type="p.status === 'AVAILABLE' ? 'success' : 'warning'">
                {{ p.status === 'AVAILABLE' ? '可租' : '已租出' }}
              </el-tag>
            </div>
            <div class="product-name">{{ p.name }}</div>
            <div class="product-attrs">
              <div class="attr-row"><span class="attr-k">段位</span><span class="attr-v">{{ p.rankText }}</span></div>
              <div class="attr-row"><span class="attr-k">库存价值</span><span class="attr-v">{{ p.warehouseValueText }}</span></div>
              <div class="attr-row"><span class="attr-k">哈夫币</span><span class="attr-v">{{ formatCoinAmount(p.coinAmount) }}</span></div>
            </div>
            <div class="product-price-row">
              <span class="product-price">{{ p.price }}</span>
              <span class="product-cta">立即租用 →</span>
            </div>
          </div>
        </template>
        <div v-else class="hot-empty">
          暂无上架账号 · <router-link to="/rentals">查看全部</router-link>
        </div>
      </div>
    </section>

    <!-- 快捷业务入口 -->
    <section class="entry-section">
      <div class="entry-grid">
        <div
          v-for="entry in entries"
          :key="entry.label"
          class="entry-card"
          :class="{ 'entry-locked': entry.requireAuth && !auth.isLoggedIn }"
          @click="handleEntryClick(entry)"
        >
          <div class="entry-icon-wrap">
            <svg class="entry-icon-svg" viewBox="0 0 48 48" fill="none" v-html="entry.iconPath" />
          </div>
          <div class="entry-label-row">
            <span class="entry-label">{{ entry.label }}</span>
            <span v-if="entry.requireAuth && !auth.isLoggedIn" class="lock-tag">需登录</span>
          </div>
          <div class="entry-desc">{{ entry.desc }}</div>
        </div>
      </div>
    </section>

    <!-- 三步上手 -->
    <section class="steps-section">
      <h2 class="section-title centered">三步上手</h2>
      <div class="steps-row">
        <template v-for="(step, i) in steps" :key="i">
          <div class="step-card">
            <div class="step-num">{{ i + 1 }}</div>
            <div class="step-title">{{ step.title }}</div>
            <div class="step-desc">{{ step.desc }}</div>
          </div>
          <div v-if="i < steps.length - 1" class="step-arrow">→</div>
        </template>
      </div>
    </section>

    <!-- 数字指标 -->
    <section ref="metricsSection" class="metrics-section">
      <div class="metrics-grid">
        <template v-if="summary.metrics.length === 0">
          <div v-for="i in 3" :key="i" class="metric-card">
            <div class="sk-pulse sk-block" style="height:32px; width:50%; margin:0 auto 12px;" />
            <div class="sk-pulse sk-line" style="height:13px; width:40%; margin:0 auto;" />
          </div>
        </template>
        <template v-else>
          <div v-for="item in summary.metrics" :key="item.label" class="metric-card">
            <div class="metric-value" :data-raw="item.value">{{ item.value }}</div>
            <div class="metric-label">{{ item.label }}</div>
          </div>
        </template>
      </div>
    </section>

    <!-- 公告 + FAQ -->
    <section class="info-section">
      <el-tabs>
        <el-tab-pane label="平台公告">
          <div class="notice-list">
            <div v-for="notice in summary.notices" :key="notice.id" class="notice-item">
              <div class="notice-dot" />
              <div class="notice-body">
                <h4>{{ notice.title }}</h4>
                <p>{{ notice.content }}</p>
              </div>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="常见问题">
          <el-collapse>
            <el-collapse-item
              v-for="(faq, i) in faqs"
              :key="i"
              :title="faq.question"
              :name="i"
            >
              <p class="faq-answer">{{ faq.answer }}</p>
            </el-collapse-item>
          </el-collapse>
        </el-tab-pane>
      </el-tabs>
    </section>

    <!-- Footer -->
    <footer class="portal-footer">
      <div class="footer-inner">
        <div class="footer-brand">
          <span class="f-logo">◆</span>
          <span class="f-name">DeltaRent</span>
        </div>
        <div class="footer-contact">
          <span>QQ 交流群：<b>123456789</b></span>
          <span class="fsep">·</span>
          <span>在线时间：10:00 – 24:00</span>
        </div>
        <p class="footer-disclaimer">
          本平台仅提供账号租赁中介服务，租用期间账号安全由双方共同负责，请勿进行违规操作。
        </p>
        <p class="footer-copy">DeltaRent · 三角洲行动账号租赁课程原型系统</p>
      </div>
    </footer>

    <!-- 联系客服弹窗 -->
    <el-dialog
      v-model="showContactDialog"
      title=""
      width="400px"
      top="50vh"
      class="portal-contact-dialog"
      :show-close="false"
      :close-on-click-modal="true"
    >
      <div class="cs-modal">
        <div class="cs-header">
          <div class="cs-icon-wrap">
            <svg class="cs-headset-icon" viewBox="0 0 48 48" fill="none">
              <circle cx="24" cy="24" r="22" stroke="currentColor" stroke-width="1.5" stroke-dasharray="4 3"/>
              <path d="M16 28c0-5.5 3.6-10 8-10h8c4.4 0 8 4.5 8 10v6c0 5.5-3.6 10-8 10h-8l-6 4v-4H14c-4.4 0-8-4.5-8-10v-6z" stroke="currentColor" stroke-width="1.5"/>
              <circle cx="18" cy="30" r="3" fill="currentColor"/>
              <circle cx="30" cy="30" r="3" fill="currentColor"/>
            </svg>
          </div>
          <div class="cs-title-group">
            <h3 class="cs-modal-title">联系客服</h3>
            <p class="cs-modal-sub">随时为您解答疑问 · 全程服务</p>
          </div>
        </div>

        <div class="cs-divider" />

        <div class="cs-body">
          <div class="cs-methods">
            <div class="cs-method-card" @click="copyQQ">
              <div class="cs-method-left">
                <div class="cs-method-icon cs-qq-icon">Q</div>
                <div>
                  <div class="cs-method-label">QQ 交流群</div>
                  <div class="cs-method-value">123456789</div>
                </div>
              </div>
              <div class="cs-method-action" :class="{ copied: qqCopied }">
                {{ qqCopied ? '已复制' : '复制' }}
              </div>
            </div>

            <div class="cs-method-card">
              <div class="cs-method-left">
                <div class="cs-method-icon cs-time-icon">⏰</div>
                <div>
                  <div class="cs-method-label">在线时间</div>
                  <div class="cs-method-value">每日 10:00 – 24:00</div>
                </div>
              </div>
              <div class="cs-online-dot" />
            </div>
          </div>

          <div class="cs-tip-box">
            <svg class="cs-tip-icon" viewBox="0 0 20 20" fill="none">
              <circle cx="10" cy="10" r="9" stroke="#60a5fa" stroke-width="1.2"/>
              <path d="M10 9v6M10 7v.5" stroke="#60a5fa" stroke-width="1.5" stroke-linecap="round"/>
            </svg>
            <span>加群后 <strong>@ 管理员</strong> 并附上订单号，可获得优先处理</span>
          </div>
        </div>

        <div class="cs-footer">
          <el-button class="cs-close-btn" @click="showContactDialog = false">我知道了</el-button>
        </div>
      </div>
    </el-dialog>

  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, reactive, ref, watch, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { getPortalSummary, getRentals } from '@/api';
import { useAuthStore } from '@/stores/auth';
import type { PortalSummary, FaqItem, RentalProduct } from '@/types/api';

const auth = useAuthStore();
const router = useRouter();

// ---- 入口卡片 ----
interface Entry {
  label: string;
  desc: string;
  path: string;
  requireAuth?: boolean;
  contact?: boolean;
  iconPath: string;
}

const entries: Entry[] = [
  {
    label: '账号列表',
    desc: '浏览可租账号，查看配置与价格',
    path: '/rentals',
    iconPath: '<rect x="6" y="8" width="36" height="28" rx="4" stroke="currentColor" stroke-width="2"/><path d="M14 18h20M14 24h12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>'
  },
  {
    label: '租赁下单',
    desc: '选择账号和时长，快速创建订单',
    path: '/orders/create',
    requireAuth: true,
    iconPath: '<path d="M8 8h32v32H8z" stroke="currentColor" stroke-width="2" rx="4"/><path d="M20 16v16M12 24h16" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>'
  },
  {
    label: '我的订单',
    desc: '跟踪订单状态，查看历史记录',
    path: '/orders',
    requireAuth: true,
    iconPath: '<rect x="6" y="6" width="36" height="36" rx="4" stroke="currentColor" stroke-width="2"/><path d="M14 20h20M14 26h10" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>'
  },
  {
    label: '联系客服',
    desc: '人工在线，实时解答疑问',
    path: '',
    contact: true,
    iconPath: '<path d="M8 16c0-4.4 3.6-8 8-8h16c4.4 0 8 3.6 8 8v10c0 4.4-3.6 8-8 8H24l-8 6v-6H16c-4.4 0-8-3.6-8-8V16z" stroke="currentColor" stroke-width="2"/><path d="M17 22h14M17 28h8" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>'
  }
];

const showContactDialog = ref(false);
const qqCopied = ref(false);

async function copyQQ() {
  const textarea = document.createElement('textarea');
  textarea.value = '123456789';
  textarea.style.cssText = 'position:fixed;top:-9999px;left:-9999px;opacity:0;';
  document.body.appendChild(textarea);
  textarea.select();
  const ok = document.execCommand('copy');
  document.body.removeChild(textarea);
  if (ok) {
    qqCopied.value = true;
    setTimeout(() => { qqCopied.value = false; }, 2000);
  } else {
    ElMessage.warning('复制失败，请长按群号手动复制');
  }
}

function handleEntryClick(entry: Entry) {
  if (entry.contact) {
    showContactDialog.value = true;
    return;
  }
  if (entry.requireAuth && !auth.isLoggedIn) {
    ElMessage.info('请先登录后使用该功能');
    router.push('/login');
    return;
  }
  router.push(entry.path);
}

function formatCoinAmount(value: number | string | undefined) {
  const numeric = Number(value || 0);
  if (!numeric) return value || '未录入';
  if (numeric >= 10000) return `${(numeric / 10000).toFixed(numeric >= 1000000 ? 0 : 1)}万`;
  return numeric.toLocaleString();
}

// ---- 门户数据 ----
const summary = reactive<PortalSummary>({
  heroTitle: '三角洲行动账号租赁管理系统',
  heroSubtitle: '安全可靠、极速交付的专业租赁平台',
  metrics: [],
  modules: [],
  notices: []
});

// ---- 热门账号 ----
const hotProducts = ref<RentalProduct[]>([]);
const hotLoading = ref(true);

async function loadHotProducts() {
  try {
    const res = await getRentals();
    if (res.data.success) {
      const all = res.data.data.list;
      const available = all.filter(p => p.status === 'AVAILABLE');
      hotProducts.value = (available.length ? available : all).slice(0, 3);
    } else {
      ElMessage.error('热卖账号加载失败');
    }
  } catch (error) {
    ElMessage.error('热卖账号加载失败');
  } finally {
    hotLoading.value = false;
  }
}

// ---- FAQ ----
const faqs: FaqItem[] = [
  { question: '租金如何计算？', answer: '租金按小时计费，不同账号根据配置和稀有度定价不同。具体价格可在账号列表中查看。' },
  { question: '租赁时间从什么时候开始？', answer: '从客服确认订单并交付账号开始计时，租赁时长连续计算。' },
  { question: '账号在使用过程中被找回怎么办？', answer: '请立即联系客服提交售后申诉，我们会核实后为您处理退款或换号。' },
  { question: '如何联系客服？', answer: '点击首页"联系客服"入口，或加入 QQ 群联系管理员，告知订单号可获优先处理。' }
];

// ---- 三步上手 ----
const steps = [
  { title: '浏览账号', desc: '在账号列表中查看各账号配置、等级与实时价格，挑选心仪账号' },
  { title: '提交下单', desc: '登录后选择账号与租赁时长，填写联系方式，一键创建订单' },
  { title: '客服交付', desc: '客服在线核验订单，快速完成账号交付，到期自动回收' }
];

// ---- 粒子动画 ----
const heroCanvas = ref<HTMLCanvasElement | null>(null);
let animId: number | null = null;
const reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

interface P { x: number; y: number; vx: number; vy: number; size: number; opacity: number; }
const particles: P[] = [];

function initHero(canvas: HTMLCanvasElement) {
  canvas.width = canvas.offsetWidth;
  canvas.height = 280;
  const count = Math.floor((canvas.width * canvas.height) / 8000);
  for (let i = 0; i < count; i++) {
    particles.push({
      x: Math.random() * canvas.width,
      y: Math.random() * canvas.height,
      vx: (Math.random() - 0.5) * 0.4,
      vy: (Math.random() - 0.5) * 0.4,
      size: Math.random() * 2 + 1,
      opacity: Math.random() * 0.4 + 0.2
    });
  }
}

function animateHero(canvas: HTMLCanvasElement) {
  const ctx = canvas.getContext('2d');
  if (!ctx) return;
  ctx.clearRect(0, 0, canvas.width, canvas.height);
  particles.forEach(p => {
    if (!reducedMotion) {
      p.x += p.vx; p.y += p.vy;
      if (p.x < 0) p.x = canvas.width;
      if (p.x > canvas.width) p.x = 0;
      if (p.y < 0) p.y = canvas.height;
      if (p.y > canvas.height) p.y = 0;
    }
    ctx.beginPath();
    ctx.arc(p.x, p.y, p.size, 0, Math.PI * 2);
    ctx.fillStyle = `rgba(245, 158, 11, ${p.opacity})`;
    ctx.fill();
  });
  if (!reducedMotion) {
    animId = requestAnimationFrame(() => animateHero(canvas));
  }
}

function handleVisibilityChange() {
  const canvas = heroCanvas.value;
  if (!canvas) return;
  if (document.hidden) {
    if (animId) cancelAnimationFrame(animId);
    animId = null;
  } else {
    animateHero(canvas);
  }
}

// ---- 数字滚动计数 ----
const metricsSection = ref<HTMLElement | null>(null);
let metricsObserver: IntersectionObserver | null = null;

function animateCountUp(el: HTMLElement, raw: string) {
  if (reducedMotion) {
    el.textContent = raw;
    return;
  }
  const match = raw.match(/^([^\d]*)([\d,]+\.?\d*)(.*)$/);
  if (!match) return;
  const prefix = match[1];
  const target = parseFloat(match[2].replace(/,/g, ''));
  const suffix = match[3];
  const hasDecimal = match[2].includes('.');
  const decimals = hasDecimal ? (match[2].split('.')[1]?.length ?? 0) : 0;
  const duration = 1400;
  const startTime = performance.now();
  const tick = (now: number) => {
    const p = Math.min((now - startTime) / duration, 1);
    const eased = 1 - (1 - p) ** 3;
    const cur = eased * target;
    el.textContent = prefix + (hasDecimal ? cur.toFixed(decimals) : Math.round(cur).toLocaleString()) + suffix;
    if (p < 1) requestAnimationFrame(tick);
    else el.textContent = raw;
  };
  requestAnimationFrame(tick);
}

function setupMetricsObserver() {
  if (!metricsSection.value || metricsObserver) return;
  metricsObserver = new IntersectionObserver(entries => {
    if (entries[0].isIntersecting) {
      metricsSection.value!.querySelectorAll<HTMLElement>('.metric-value[data-raw]').forEach(el =>
        animateCountUp(el, el.dataset.raw!)
      );
      metricsObserver?.disconnect();
    }
  }, { threshold: 0.3 });
  metricsObserver.observe(metricsSection.value);
}

watch(() => summary.metrics.length, async (len) => {
  if (len > 0) { await nextTick(); setupMetricsObserver(); }
});

// ---- 数据加载 ----
async function loadSummary() {
  try {
    const res = await getPortalSummary();
    if (res.data.success) Object.assign(summary, res.data.data);
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '首页数据加载失败');
  }
}

onMounted(() => {
  loadSummary();
  loadHotProducts();
  const canvas = heroCanvas.value;
  if (canvas) { initHero(canvas); animateHero(canvas); }
  document.addEventListener('visibilitychange', handleVisibilityChange);
});

onUnmounted(() => {
  if (animId !== null) cancelAnimationFrame(animId);
  document.removeEventListener('visibilitychange', handleVisibilityChange);
  metricsObserver?.disconnect();
});
</script>

<script lang="ts">
export default { name: 'PortalView' };
</script>

<style scoped>
/* ==================== 整体暖色主题 ==================== */
.warm-theme {
  background:
    radial-gradient(circle at top left, rgba(255, 221, 111, 0.2), transparent 20%),
    linear-gradient(180deg, #fff8df 0%, #fffdf4 24%, #f8fafc 100%);
  min-height: 100vh;
  color: #1f2937;
}

/* ==================== Hero ==================== */
.hero-section {
  position: relative;
  border-radius: 24px;
  overflow: hidden;
  padding: 72px 48px 56px;
  background: linear-gradient(145deg, rgba(255, 243, 201, 0.96) 0%, rgba(255, 223, 122, 0.88) 100%);
  backdrop-filter: blur(12px);
  border: 0;
  margin-bottom: 24px;
  text-align: center;
}

.hero-particle {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  opacity: 0.6;
}

.hero-content { position: relative; z-index: 1; }

.hero-badge {
  display: inline-block;
  padding: 4px 16px;
  border-radius: 20px;
  border: 1px solid rgba(201, 136, 22, 0.24);
  font-size: 12px;
  color: #9b5d00;
  letter-spacing: 3px;
  margin-bottom: 20px;
  background: rgba(255, 255, 255, 0.55);
}

.hero-title {
  margin: 0 0 16px;
  font-size: 40px;
  font-weight: 800;
  font-family: "Bebas Neue", "Noto Sans SC", sans-serif;
  color: #3c2b00;
  letter-spacing: 4px;
  line-height: 1.1;
}

.hero-subtitle {
  margin: 0 0 36px;
  font-size: 16px;
  color: #6b7280;
  line-height: 1.7;
}

.hero-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  flex-wrap: wrap;
}

.btn-primary-action {
  background: linear-gradient(135deg, #ffe057 0%, #ffc420 100%);
  border: none;
  box-shadow: 0 4px 16px rgba(255, 196, 32, 0.32);
  color: #5a3c00;
}

.btn-primary-action:hover {
  box-shadow: 0 6px 20px rgba(255, 196, 32, 0.4);
  transform: translateY(-1px);
}

.hero-actions .el-button:not(.btn-primary-action) {
  background: rgba(255, 255, 255, 0.72);
  border: 1px solid rgba(255, 214, 107, 0.6);
  color: #5a3c00;
}

.hero-actions .el-button:not(.btn-primary-action):hover {
  background: #fff7e6;
  color: #9b5d00;
}

/* ==================== 通用 Section ==================== */
.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.section-title {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #1f2937;
  letter-spacing: 1px;
}

.section-title.centered { text-align: center; }

.see-all-link {
  font-size: 13px;
  color: #c57a00;
  text-decoration: none;
  transition: opacity 0.2s;
}

.see-all-link:hover { opacity: 0.75; }

/* ==================== 热门账号 ==================== */
.hot-section { margin-bottom: 24px; }

.hot-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.product-card {
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(226, 232, 240, 0.9);
  border-radius: 16px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.25s;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.product-card:hover {
  transform: translateY(-4px);
  background: #fffdf7;
  border-color: rgba(255, 196, 32, 0.5);
  box-shadow: 0 12px 28px rgba(181, 145, 41, 0.12);
}

.skeleton-card { cursor: default; }
.skeleton-card:hover { transform: none; box-shadow: none; background: rgba(255, 255, 255, 0.04); border-color: rgba(96, 165, 250, 0.1); }

.product-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.product-tag-pill {
  font-size: 11px;
  padding: 2px 10px;
  border-radius: 12px;
  background: rgba(96, 165, 250, 0.12);
  border: 1px solid rgba(96, 165, 250, 0.2);
  color: #93c5fd;
}

.product-name {
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
  line-height: 1.4;
}

.product-attrs { display: flex; flex-direction: column; gap: 6px; }

.attr-row {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
}

.attr-k { color: #64748b; }
.attr-v { color: #475569; }

.product-price-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 8px;
  border-top: 1px solid rgba(96, 165, 250, 0.08);
}

.product-price {
  font-size: 16px;
  font-weight: 700;
  color: #f05b2c;
}

.product-cta {
  font-size: 12px;
  color: #c57a00;
}

.hot-empty {
  grid-column: 1 / -1;
  text-align: center;
  color: #64748b;
  padding: 32px;
  font-size: 14px;
}

.hot-empty a { color: #60a5fa; text-decoration: none; }

/* ==================== 骨架屏 ==================== */
.sk-pulse {
  background: linear-gradient(90deg, rgba(255,255,255,0.04) 0%, rgba(255,255,255,0.08) 50%, rgba(255,255,255,0.04) 100%);
  background-size: 200% 100%;
  border-radius: 6px;
  animation: sk-shimmer 1.6s infinite;
}

.sk-line { display: block; }
.sk-block { display: block; }

@keyframes sk-shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

/* ==================== 业务入口 ==================== */
.entry-section { margin-bottom: 24px; }

.entry-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.entry-card {
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(255, 214, 107, 0.4);
  border-radius: 16px;
  padding: 28px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.25s;
}

.entry-card:hover {
  transform: translateY(-4px);
  background: #fffdf7;
  border-color: rgba(255, 196, 32, 0.55);
  box-shadow: 0 8px 30px rgba(181, 145, 41, 0.14);
}

.entry-card.entry-locked { opacity: 0.65; }
.entry-card.entry-locked:hover { opacity: 0.85; }

.entry-card:hover .entry-icon-svg {
  color: #c57a00;
  filter: drop-shadow(0 0 8px rgba(245, 158, 11, 0.4));
}

.entry-icon-wrap {
  display: flex;
  justify-content: center;
  margin-bottom: 14px;
}

.entry-icon-svg {
  width: 40px;
  height: 40px;
  color: #94a3b8;
  transition: all 0.25s;
}

.entry-label-row {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-bottom: 6px;
}

.entry-label {
  font-size: 15px;
  font-weight: 600;
  color: #3c2b00;
}

.lock-tag {
  font-size: 10px;
  padding: 1px 6px;
  border-radius: 8px;
  background: rgba(251, 191, 36, 0.12);
  border: 1px solid rgba(251, 191, 36, 0.3);
  color: #d97706;
}

.entry-desc {
  font-size: 12px;
  color: #64748b;
  line-height: 1.5;
}

/* ==================== 三步上手 ==================== */
.steps-section { margin-bottom: 24px; }

.steps-section .section-title { margin-bottom: 20px; }

.steps-row {
  display: flex;
  align-items: stretch;
  gap: 0;
}

.step-card {
  flex: 1;
  background: rgba(255, 255, 255, 0.88);
  border: 1px solid rgba(255, 214, 107, 0.4);
  border-radius: 16px;
  padding: 28px 20px;
  text-align: center;
}

.step-arrow {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  padding: 0 12px;
  font-size: 20px;
  color: rgba(245, 158, 11, 0.4);
}

.step-num {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ffe057 0%, #ffc420 100%);
  box-shadow: 0 4px 12px rgba(255, 196, 32, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 700;
  color: #5a3c00;
  margin: 0 auto 14px;
}

.step-title {
  font-size: 15px;
  font-weight: 600;
  color: #3c2b00;
  margin-bottom: 8px;
}

.step-desc {
  font-size: 12px;
  color: #64748b;
  line-height: 1.6;
}

/* ==================== 数字指标 ==================== */
.metrics-section { margin-bottom: 24px; }

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.metric-card {
  background: linear-gradient(160deg, #fff3c2 0%, #ffd760 100%);
  border: 1px solid rgba(255, 214, 107, 0.5);
  border-radius: 16px;
  padding: 28px 24px;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.metric-card::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, rgba(245, 158, 11, 0.6), transparent);
}

.metric-value {
  font-size: 30px;
  font-weight: 800;
  color: #5a3c00;
  margin-bottom: 8px;
}

.metric-label {
  font-size: 13px;
  color: #9b5d00;
  letter-spacing: 1px;
}

/* ==================== 公告 + FAQ ==================== */
.info-section {
  background: rgba(255, 255, 255, 0.88);
  border: 1px solid rgba(255, 214, 107, 0.3);
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
}

.info-section :deep(.el-tabs__header) {
  border-bottom: 1px solid rgba(255, 214, 107, 0.3);
}

.info-section :deep(.el-tabs__item) { color: #64748b; }
.info-section :deep(.el-tabs__item.is-active) { color: #c57a00; }
.info-section :deep(.el-tabs__active-bar) { background: #f3c347; }
.info-section :deep(.el-tabs__content) { color: #64748b; }
.info-section :deep(.el-collapse) { border: none; background: transparent; }
.info-section :deep(.el-collapse-item__header) { background: rgba(255, 245, 214, 0.5); color: #5a3c00; border-bottom: 1px solid rgba(255, 214, 107, 0.2); }
.info-section :deep(.el-collapse-item__wrap) { background: transparent; border-bottom: 1px solid rgba(255, 214, 107, 0.2); }
.info-section :deep(.el-collapse-item__content) { color: #64748b; }

.notice-list { display: flex; flex-direction: column; gap: 12px; }

.notice-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 14px 16px;
  background: rgba(255, 245, 214, 0.5);
  border-radius: 12px;
  border: 1px solid rgba(255, 214, 107, 0.2);
}

.notice-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #f59e0b;
  box-shadow: 0 0 8px rgba(245, 158, 11, 0.5);
  margin-top: 6px;
  flex-shrink: 0;
}

.notice-body h4 { margin: 0 0 6px; font-size: 14px; color: #5a3c00; }
.notice-body p { margin: 0; font-size: 13px; color: #64748b; line-height: 1.6; }
.faq-answer { margin: 0; font-size: 13px; color: #64748b; line-height: 1.7; }

/* ==================== Footer ==================== */
.portal-footer {
  border-top: 1px solid rgba(255, 214, 107, 0.3);
  padding: 32px 0 16px;
}

.footer-inner {
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.footer-brand {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
}

.f-logo { color: #f59e0b; font-size: 18px; }
.f-name { color: #5a3c00; }

.footer-contact {
  font-size: 13px;
  color: #9b5d00;
  display: flex;
  align-items: center;
  gap: 8px;
}

.footer-contact b { color: #5a3c00; }
.fsep { color: #d97706; }

.footer-disclaimer {
  margin: 0;
  font-size: 11px;
  color: #9b5d00;
  max-width: 480px;
  line-height: 1.6;
}

.footer-copy { font-size: 11px; color: #c57a00; margin: 0; }

/* ==================== 联系客服弹窗 ==================== */
:deep(.el-overlay) {
  background: rgba(30, 20, 0, 0.65) !important;
  backdrop-filter: blur(4px);
}

:deep(.el-dialog) {
  position: fixed !important;
  left: 50% !important;
  top: 50% !important;
  transform: translate(-50%, -50%) !important;
  background: #fffdf4 !important;
  border: 1px solid rgba(255, 214, 107, 0.5) !important;
  border-radius: 20px !important;
  box-shadow: 0 24px 60px rgba(90, 60, 0, 0.3) !important;
  padding: 0 !important;
  margin: 0 !important;
  max-width: 400px !important;
  width: 400px !important;
}

:deep(.el-dialog__header) {
  display: none !important;
}

:deep(.el-dialog__body) {
  padding: 0 !important;
  color: #64748b !important;
}

:deep(.el-dialog__footer) {
  display: none !important;
}

.cs-modal {
  padding: 28px 28px 20px;
  width: 100%;
  box-sizing: border-box;
}

/* 头部 */
.cs-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.cs-icon-wrap {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  background: linear-gradient(135deg, rgba(255, 243, 201, 0.96) 0%, rgba(255, 223, 122, 0.88) 100%);
  border: 1px solid rgba(255, 214, 107, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 0 20px rgba(245, 158, 11, 0.2);
}

.cs-headset-icon {
  width: 30px;
  height: 30px;
  color: #c57a00;
}

.cs-title-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.cs-modal-title {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #5a3c00;
  letter-spacing: 1px;
}

.cs-modal-sub {
  margin: 0;
  font-size: 12px;
  color: #9b5d00;
}

/* 分隔线 */
.cs-divider {
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255, 214, 107, 0.5), transparent);
  margin: 0 0 20px;
}

/* 主体 */
.cs-body { display: flex; flex-direction: column; gap: 12px; }

.cs-methods { display: flex; flex-direction: column; gap: 10px; }

.cs-method-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  background: rgba(255, 245, 214, 0.5);
  border: 1px solid rgba(255, 214, 107, 0.3);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.cs-method-card:first-child:hover {
  background: rgba(255, 243, 201, 0.8);
  border-color: rgba(255, 196, 32, 0.5);
}

.cs-method-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.cs-method-icon {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 700;
  flex-shrink: 0;
}

.cs-qq-icon {
  background: rgba(255, 196, 32, 0.15);
  color: #c57a00;
  font-family: serif;
  font-size: 20px;
}

.cs-time-icon { font-size: 18px; }

.cs-method-label { font-size: 11px; color: #9b5d00; margin-bottom: 2px; }
.cs-method-value { font-size: 14px; font-weight: 600; color: #5a3c00; }

.cs-method-action {
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 8px;
  border: 1px solid rgba(255, 196, 32, 0.4);
  color: #c57a00;
  background: rgba(255, 245, 214, 0.5);
  transition: all 0.2s;
  cursor: pointer;
}

.cs-method-action:hover {
  background: rgba(255, 243, 201, 0.8);
  border-color: #f3c347;
}

.cs-method-action.copied {
  background: rgba(34, 197, 94, 0.15);
  border-color: rgba(34, 197, 94, 0.4);
  color: #16a34a;
}

.cs-online-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #4ade80;
  box-shadow: 0 0 8px rgba(74, 222, 128, 0.6);
  animation: pulse-dot 2s infinite;
}

@keyframes pulse-dot {
  0%, 100% { opacity: 1; box-shadow: 0 0 8px rgba(74, 222, 128, 0.6); }
  50% { opacity: 0.6; box-shadow: 0 0 16px rgba(74, 222, 128, 0.3); }
}

/* 提示框 */
.cs-tip-box {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 10px 14px;
  background: rgba(255, 245, 214, 0.5);
  border: 1px solid rgba(255, 214, 107, 0.3);
  border-radius: 10px;
  font-size: 12px;
  color: #9b5d00;
  line-height: 1.6;
}

.cs-tip-box strong { color: #c57a00; }
.cs-tip-icon { width: 18px; height: 18px; flex-shrink: 0; margin-top: 1px; }

/* 底部按钮 */
.cs-footer {
  margin-top: 18px;
  display: flex;
  justify-content: center;
}

.cs-close-btn {
  background: linear-gradient(135deg, #ffe057 0%, #ffc420 100%) !important;
  border: none !important;
  color: #5a3c00 !important;
  padding: 8px 32px !important;
  border-radius: 10px !important;
  font-size: 14px !important;
  font-weight: 600 !important;
  box-shadow: 0 4px 14px rgba(255, 196, 32, 0.35);
}

.cs-close-btn:hover {
  box-shadow: 0 6px 18px rgba(255, 196, 32, 0.45) !important;
  transform: translateY(-1px);
}

/* ==================== 全局暖色 Element Plus 覆盖 ==================== */
:deep(.el-dialog__body) { color: #64748b; }

:deep(.el-tabs__nav-bottom-border) { border-color: rgba(255, 214, 107, 0.3); }

:deep(.el-tabs__content) { color: #64748b; }

:deep(.el-collapse) {
  border: none;
  background: transparent;
}

:deep(.el-collapse-item__header) {
  background: rgba(255, 245, 214, 0.5);
  color: #5a3c00;
  border-bottom: 1px solid rgba(255, 214, 107, 0.2);
  font-size: 14px;
}

:deep(.el-collapse-item__header:hover) {
  color: #c57a00;
  background: rgba(255, 243, 201, 0.6);
}

:deep(.el-collapse-item__wrap) {
  background: transparent;
  border-bottom: 1px solid rgba(255, 214, 107, 0.2);
}

:deep(.el-collapse-item__content) {
  color: #64748b;
  font-size: 13px;
  line-height: 1.7;
}

:deep(.el-collapse-item__arrow) {
  color: #9b5d00;
  transition: transform 0.3s;
}

:deep(.el-collapse-item__arrow.is-active) {
  color: #c57a00;
}

/* 标签 */
:deep(.el-tag) {
  background: rgba(255, 196, 32, 0.12);
  border-color: rgba(255, 196, 32, 0.3);
  color: #c57a00;
}

:deep(.el-tag--success) {
  background: rgba(34, 197, 94, 0.12);
  border-color: rgba(34, 197, 94, 0.3);
  color: #16a34a;
}

:deep(.el-tag--warning) {
  background: rgba(251, 191, 36, 0.12);
  border-color: rgba(251, 191, 36, 0.3);
  color: #d97706;
}

/* ==================== 响应式 ==================== */
@media (max-width: 900px) {
  .entry-grid { grid-template-columns: repeat(2, 1fr); }
  .hero-title { font-size: 28px; }
  .hero-section { padding: 48px 24px 40px; }
  .steps-row { flex-direction: column; align-items: stretch; }
  .step-arrow { transform: rotate(90deg); padding: 4px 0; justify-content: center; }
}

@media (max-width: 600px) {
  .hot-grid { grid-template-columns: 1fr; }
  .entry-grid { grid-template-columns: repeat(2, 1fr); }
  .metrics-grid { grid-template-columns: repeat(3, 1fr); }
  .hero-title { font-size: 22px; letter-spacing: 1px; }
  .hero-section { padding: 40px 16px 32px; border-radius: 16px; }
  .metric-value { font-size: 22px; }
  .metric-card { padding: 20px 12px; }
  .footer-contact { flex-direction: column; gap: 4px; }
  .fsep { display: none; }
}
</style>
