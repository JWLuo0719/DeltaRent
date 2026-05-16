<template>
  <div class="register-shell">
    <!-- 粒子背景 -->
    <canvas ref="canvasRef" class="particle-canvas" />

    <!-- 左侧品牌区 -->
    <aside class="brand-panel">
      <!-- 战术网格背景 -->
      <div class="tactical-grid" />

      <div class="brand-inner">
        <div class="brand-logo">
          <svg width="56" height="56" viewBox="0 0 64 64" fill="none">
            <rect width="64" height="64" rx="8" fill="rgba(255,255,255,0.08)" stroke="rgba(255,255,255,0.15)"/>
            <path d="M16 48V20l14-8v36l-14-0z" fill="white"/>
            <path d="M30 12l14 8v24l-14-8V12z" fill="white" opacity="0.65"/>
            <circle cx="44" cy="20" r="5" fill="#f59e0b" stroke="white" stroke-width="1.5"/>
          </svg>
        </div>
        <h1 class="brand-name">三角洲账号租赁</h1>
        <p class="brand-slogan">三角洲行动 · 战术租赁平台</p>

        <!-- 战术标签 -->
        <div class="tactical-tags">
          <span class="tag">
            <span class="tag-dot" />
            账号租赁
          </span>
          <span class="tag">
            <span class="tag-dot" />
            安全交易
          </span>
          <span class="tag">
            <span class="tag-dot" />
            极速交付
          </span>
        </div>

        <!-- 任务列表 -->
        <div class="mission-list">
          <div class="mission-item">
            <span class="mission-icon">◆</span>
            <span>高战满配账号</span>
          </div>
          <div class="mission-item">
            <span class="mission-icon">◆</span>
            <span>哈夫币仓库</span>
          </div>
          <div class="mission-item">
            <span class="mission-icon">◆</span>
            <span>稀有外观收藏</span>
          </div>
        </div>

        <div class="version-info">
          VER 1.0.0 · 三角洲行动
        </div>
      </div>
    </aside>

    <!-- 右侧表单区 -->
    <main class="form-panel">
      <div class="form-card">
        <div class="form-header">
          <h2>注册</h2>
          <p>创建账号，开始您的租赁之旅</p>
        </div>

        <el-form label-position="top" @submit.prevent="handleRegister">
          <div class="input-group">
            <label>手机号</label>
            <el-input
              v-model="form.phone"
              placeholder="用于登录"
              size="large"
              @keyup.enter="handleRegister"
            />
          </div>

          <div class="input-row">
            <div class="input-group">
              <label>密码</label>
              <el-input
                v-model="form.password"
                type="password"
                show-password
                placeholder="请输入密码"
                size="large"
                @keyup.enter="handleRegister"
              />
            </div>
            <div class="input-group">
              <label>确认密码</label>
              <el-input
                v-model="form.confirmPassword"
                type="password"
                show-password
                placeholder="请再次输入"
                size="large"
                @keyup.enter="handleRegister"
              />
            </div>
          </div>

          <div class="input-group">
            <label>昵称（选填）</label>
            <el-input
              v-model="form.nickname"
              placeholder="显示名称"
              size="large"
              @keyup.enter="handleRegister"
            />
          </div>

          <el-button
            type="primary"
            :loading="submitting"
            size="large"
            class="register-btn"
            @click="handleRegister"
          >
            注 册
          </el-button>
        </el-form>

        <div class="form-footer">
          <span>已有账号？</span>
          <el-button link type="primary" @click="$router.push('/login')">立即登录</el-button>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { register } from '@/api';

const router = useRouter();
const canvasRef = ref<HTMLCanvasElement | null>(null);
let animationId: number | null = null;
const reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

const form = reactive({
  phone: '',
  password: '',
  confirmPassword: '',
  nickname: ''
});

const submitting = ref(false);

async function handleRegister() {
  if (!form.phone.trim()) {
    ElMessage.warning('请输入手机号');
    return;
  }
  if (!/^1[3-9]\d{9}$/.test(form.phone)) {
    ElMessage.warning('请输入正确的手机号');
    return;
  }
  if (!form.password) {
    ElMessage.warning('请输入密码');
    return;
  }
  if (form.password !== form.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致');
    return;
  }
  if (form.password.length < 6) {
    ElMessage.warning('密码长度不能少于6位');
    return;
  }

  submitting.value = true;
  try {
    const response = await register({
      phone: form.phone.trim(),
      password: form.password,
      nickname: form.nickname.trim() || undefined
    });
    if (response.data.success) {
      ElMessage.success('注册成功，请登录');
      localStorage.setItem('remembered_phone', form.phone.trim());
      router.push('/login');
      return;
    }
    ElMessage.error(response.data.message || '注册失败');
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '注册失败');
  } finally {
    submitting.value = false;
  }
}

// 粒子动画
interface Particle {
  x: number;
  y: number;
  vx: number;
  vy: number;
  size: number;
  opacity: number;
  highlight: boolean;
}

function initParticles(canvas: HTMLCanvasElement, particles: Particle[]) {
  const count = Math.floor((canvas.width * canvas.height) / 6000);
  for (let i = 0; i < count; i++) {
    const highlight = Math.random() < 0.08;
    particles.push({
      x: Math.random() * canvas.width,
      y: Math.random() * canvas.height,
      vx: (Math.random() - 0.5) * 0.5,
      vy: (Math.random() - 0.5) * 0.5,
      size: highlight ? Math.random() * 3 + 2 : Math.random() * 2.5 + 1,
      opacity: highlight ? Math.random() * 0.4 + 0.5 : Math.random() * 0.4 + 0.25,
      highlight
    });
  }
}

function animateParticles(canvas: HTMLCanvasElement, particles: Particle[]) {
  const ctx = canvas.getContext('2d');
  if (!ctx) return;

  ctx.clearRect(0, 0, canvas.width, canvas.height);

  particles.forEach(p => {
    if (!reducedMotion) {
      p.x += p.vx;
      p.y += p.vy;

      if (p.x < 0) p.x = canvas.width;
      if (p.x > canvas.width) p.x = 0;
      if (p.y < 0) p.y = canvas.height;
      if (p.y > canvas.height) p.y = 0;
    }

    if (p.highlight) {
      const r = p.size * 4;
      const gradient = ctx.createRadialGradient(p.x, p.y, 0, p.x, p.y, r);
      gradient.addColorStop(0, `rgba(245, 158, 11, ${p.opacity})`);
      gradient.addColorStop(0.4, `rgba(245, 158, 11, ${p.opacity * 0.4})`);
      gradient.addColorStop(1, 'rgba(245, 158, 11, 0)');
      ctx.beginPath();
      ctx.arc(p.x, p.y, r, 0, Math.PI * 2);
      ctx.fillStyle = gradient;
      ctx.fill();
    } else {
      ctx.beginPath();
      ctx.arc(p.x, p.y, p.size, 0, Math.PI * 2);
      ctx.fillStyle = `rgba(255, 214, 107, ${p.opacity})`;
      ctx.fill();
    }
  });

  // 连接近的粒子
  particles.forEach((p1, i) => {
    particles.slice(i + 1).forEach(p2 => {
      const dx = p1.x - p2.x;
      const dy = p1.y - p2.y;
      const dist = Math.sqrt(dx * dx + dy * dy);
      const maxDist = 130;
      if (dist < maxDist) {
        ctx.beginPath();
        ctx.moveTo(p1.x, p1.y);
        ctx.lineTo(p2.x, p2.y);
        const lineOpacity = 0.25 * (1 - dist / maxDist);
        ctx.strokeStyle = `rgba(255, 214, 107, ${lineOpacity})`;
        ctx.lineWidth = 0.6;
        ctx.stroke();
      }
    });
  });

  if (!reducedMotion) {
    animationId = requestAnimationFrame(() => animateParticles(canvas, particles));
  }
}

const particles: Particle[] = [];
let handleResize: (() => void) | null = null;

onMounted(() => {
  const canvas = canvasRef.value;
  if (!canvas) return;

  canvas.width = window.innerWidth;
  canvas.height = window.innerHeight;

  initParticles(canvas, particles);
  animateParticles(canvas, particles);

  handleResize = () => {
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
    particles.length = 0;
    initParticles(canvas, particles);
  };
  window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
  if (animationId) cancelAnimationFrame(animationId);
  if (handleResize) window.removeEventListener('resize', handleResize);
});
</script>

<style>
/* 全局页面背景 - 暖色渐变 */
html,
body,
#app {
  background: linear-gradient(180deg, #fff8df 0%, #fffdf4 24%, #f8fafc 100%);
  min-height: 100vh;
}
</style>

<style scoped>
.register-shell {
  display: flex;
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  background: linear-gradient(180deg, #fff8df 0%, #fffdf4 30%, #f8fafc 100%);
}

/* 粒子画布 */
.particle-canvas {
  position: fixed;
  inset: 0;
  z-index: 1;
  pointer-events: none;
}

/* 左侧品牌区 */
.brand-panel {
  width: 42%;
  flex-shrink: 0;
  background: linear-gradient(160deg, rgba(255, 245, 214, 0.86) 0%, rgba(255, 226, 144, 0.82) 100%);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 2;
}

/* 战术网格背景 */
.tactical-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(245, 158, 11, 0.06) 1px, transparent 1px),
    linear-gradient(90deg, rgba(245, 158, 11, 0.06) 1px, transparent 1px);
  background-size: 50px 50px;
}

.brand-inner {
  position: relative;
  z-index: 1;
  color: white;
  padding: 60px 48px;
  text-align: center;
}

.brand-logo {
  margin-bottom: 20px;
}

.brand-name {
  margin: 0 0 8px;
  font-size: 28px;
  font-weight: 700;
  letter-spacing: 2px;
  color: #5a3c00;
}

.brand-slogan {
  margin: 0 0 28px;
  font-size: 13px;
  color: #7c6b39;
  letter-spacing: 2px;
}

/* 战术标签 */
.tactical-tags {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-bottom: 32px;
}

.tag {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #6b7280;
  letter-spacing: 1px;
}

.tag-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #f59e0b;
  box-shadow: 0 0 8px rgba(245, 158, 11, 0.45);
}

/* 任务列表 */
.mission-list {
  background: rgba(255, 255, 255, 0.46);
  border: 1px solid rgba(255, 214, 107, 0.5);
  border-radius: 8px;
  padding: 16px 20px;
  margin-bottom: 24px;
  text-align: left;
}

.mission-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 0;
  font-size: 13px;
  color: #4b5563;
  border-bottom: 1px solid rgba(255, 214, 107, 0.35);
}

.mission-item:last-child {
  border-bottom: none;
}

.mission-icon {
  color: #f59e0b;
  font-size: 10px;
}

/* 版本信息 */
.version-info {
  font-size: 11px;
  color: #7c6b39;
  letter-spacing: 1px;
}

/* 右侧表单区 */
.form-panel {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  position: relative;
  z-index: 2;
  background: transparent;
}

.form-card {
  width: 100%;
  max-width: 460px;
  padding: 44px 36px;
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(12px);
  border-radius: 16px;
  box-shadow:
    0 20px 40px rgba(181, 145, 41, 0.12),
    0 0 0 1px rgba(255, 214, 107, 0.2);
}

.form-header {
  margin-bottom: 32px;
}

.form-header h2 {
  margin: 0 0 6px;
  font-size: 24px;
  font-weight: 700;
  color: #0f172a;
}

.form-header p {
  margin: 0;
  color: #64748b;
  font-size: 14px;
}

/* 输入框组 */
.input-group {
  margin-bottom: 14px;
}

.input-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 13px;
  font-weight: 500;
  color: #374151;
  transition: color 0.2s;
}

.input-group:focus-within label {
  color: #c57a00;
}

.input-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}

:deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 0 0 1px #e5e7eb;
  transition: all 0.2s;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #d1d5db;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px #f3c347;
}

:deep(.el-input__inner) {
  font-size: 15px;
  height: 44px;
}

.register-btn {
  width: 100%;
  height: 46px;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 3px;
  background: linear-gradient(135deg, #ffe057 0%, #ffc420 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(255, 196, 32, 0.3);
  color: #5a3c00;
  transition: all 0.2s;
  margin-top: 6px;
}

.register-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(255, 196, 32, 0.38);
}

.register-btn:active:not(:disabled) {
  transform: translateY(0);
}

.form-footer {
  text-align: center;
  margin-top: 20px;
  color: #64748b;
  font-size: 14px;
}

@media (max-width: 900px) {
  .register-shell {
    flex-direction: column;
  }

  .brand-panel {
    width: 100%;
    min-height: 45vh;
    padding: 40px 24px;
  }

  .mission-list {
    display: none;
  }

  .form-card {
    padding: 32px 24px;
  }

  .input-row {
    grid-template-columns: 1fr;
  }
}
</style>
