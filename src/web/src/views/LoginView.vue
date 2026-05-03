<template>
  <!-- 粒子背景 - 放最外层确保覆盖全屏 -->
  <canvas ref="canvasRef" class="particle-canvas" />

  <div class="login-shell">
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
            <circle cx="44" cy="20" r="5" fill="#60a5fa" stroke="white" stroke-width="1.5"/>
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

        <!-- 任务简报 -->
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
          <h2>登录</h2>
          <p>欢迎回来，请登录您的账号</p>
        </div>

        <el-form label-position="top" @submit.prevent="handleLogin">
          <div class="input-group">
            <label>手机号</label>
            <el-input
              v-model="form.phone"
              placeholder="请输入手机号"
              size="large"
              @keyup.enter="handleLogin"
            />
          </div>

          <div class="input-group">
            <label>密码</label>
            <el-input
              v-model="form.password"
              type="password"
              show-password
              placeholder="请输入密码"
              size="large"
              @keyup.enter="handleLogin"
            />
          </div>

          <div class="form-options">
            <el-checkbox v-model="rememberMe">记住我</el-checkbox>
            <el-button link type="primary" size="small" @click="showForgetDialog">忘记密码？</el-button>
          </div>

          <el-button
            type="primary"
            :loading="submitting"
            size="large"
            class="login-btn"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form>

        <div class="form-footer">
          <span>还没有账号？</span>
          <el-button link type="primary" @click="$router.push('/register')">立即注册</el-button>
        </div>

        <div class="divider">
          <span>或者</span>
        </div>

        <el-button text size="large" class="guest-btn" @click="$router.push('/home')">
          游客访问 →
        </el-button>
      </div>
    </main>

    <!-- 忘记密码弹窗 -->
    <el-dialog v-model="showForgetModal" title="找回密码" width="400px" :close-on-click-modal="false">
      <el-form label-position="top">
        <el-form-item label="手机号">
          <el-input
            v-model="forgetForm.phone"
            placeholder="请输入注册手机号"
            size="large"
          />
        </el-form-item>
        <el-form-item label="验证码">
          <div style="display: flex; gap: 12px;">
            <el-input
              v-model="forgetForm.verifyCode"
              placeholder="请输入验证码"
              size="large"
              style="flex: 1;"
            />
            <el-button
              size="large"
              :loading="sendVerifyLoading"
              :disabled="verifyCountdown > 0"
              @click="handleSendVerifyCode"
            >
              {{ verifyCountdown > 0 ? `${verifyCountdown}s` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
        <el-form-item label="新密码">
          <el-input
            v-model="forgetForm.newPassword"
            type="password"
            show-password
            placeholder="请输入新密码（至少6位）"
            size="large"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showForgetModal = false">取消</el-button>
        <el-button type="primary" @click="handleResetPassword">确认重置</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { login, sendVerifyCode as sendVerifyCodeApi, resetPassword as resetPasswordApi } from '@/api';
import { useAuthStore } from '@/stores/auth';
import type { LoginPayload } from '@/types/api';

const router = useRouter();
const auth = useAuthStore();
const canvasRef = ref<HTMLCanvasElement | null>(null);
let animationId: number | null = null;

const submitting = ref(false);
const rememberMe = ref(!!localStorage.getItem('remembered_phone'));

const form = reactive<LoginPayload>({
  phone: localStorage.getItem('remembered_phone') || '',
  password: ''
});


const forgetForm = reactive({
  phone: '',
  verifyCode: '',
  newPassword: ''
});
const showForgetModal = ref(false);
const sendVerifyLoading = ref(false);
const verifyCountdown = ref(0);
let countdownTimer: number | null = null;

function clearCountdown() {
  if (countdownTimer) {
    clearInterval(countdownTimer);
    countdownTimer = null;
  }
}

async function handleLogin() {
  if (!form.phone || !form.password) {
    ElMessage.warning('请输入手机号和密码');
    return;
  }
  if (!/^1[3-9]\d{9}$/.test(form.phone)) {
    ElMessage.warning('请输入正确的手机号');
    return;
  }

  submitting.value = true;
  try {
    const response = await login(form);
    if (response.data.success) {
      const result = response.data.data;
      // 同步到 auth store（role 类型断言）
      auth.setAuth(result.token, {
        ...result.user,
        role: result.user.role as 'ADMIN' | 'USER' | 'CS'
      });
      if (rememberMe.value) {
        localStorage.setItem('remembered_phone', form.phone);
      } else {
        localStorage.removeItem('remembered_phone');
      }
      ElMessage.success(`欢迎回来，${result.user.displayName}`);
      router.push('/home');
      return;
    }
    ElMessage.error(response.data.message || '登录失败');
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '登录失败');
  } finally {
    submitting.value = false;
  }
}

function showForgetDialog() {
  showForgetModal.value = true;
  forgetForm.phone = '';
  forgetForm.verifyCode = '';
  forgetForm.newPassword = '';
}

async function handleSendVerifyCode() {
  if (!forgetForm.phone) {
    ElMessage.warning('请输入手机号');
    return;
  }
  if (!/^1[3-9]\d{9}$/.test(forgetForm.phone)) {
    ElMessage.warning('请输入正确的手机号');
    return;
  }
  sendVerifyLoading.value = true;
  try {
    await sendVerifyCodeApi({ phone: forgetForm.phone, type: 'reset_password' });
    ElMessage.success('验证码已发送');
    verifyCountdown.value = 60;
    clearCountdown();
    countdownTimer = window.setInterval(() => {
      verifyCountdown.value--;
      if (verifyCountdown.value <= 0) {
        clearCountdown();
      }
    }, 1000);
  } catch (error) {
    ElMessage.error('发送失败，请重试');
  } finally {
    sendVerifyLoading.value = false;
  }
}

async function handleResetPassword() {
  if (!forgetForm.phone) {
    ElMessage.warning('请输入手机号');
    return;
  }
  if (!forgetForm.verifyCode) {
    ElMessage.warning('请输入验证码');
    return;
  }
  if (!forgetForm.newPassword) {
    ElMessage.warning('请输入新密码');
    return;
  }
  if (forgetForm.newPassword.length < 6) {
    ElMessage.warning('密码长度不能少于6位');
    return;
  }
  try {
    await resetPasswordApi({
      phone: forgetForm.phone,
      verifyCode: forgetForm.verifyCode,
      newPassword: forgetForm.newPassword
    });
    clearCountdown();
    ElMessage.success('密码重置成功，请使用新密码登录');
    showForgetModal.value = false;
  } catch (error) {
    clearCountdown();
    ElMessage.error('重置失败，请重试');
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
    p.x += p.vx;
    p.y += p.vy;

    if (p.x < 0) p.x = canvas.width;
    if (p.x > canvas.width) p.x = 0;
    if (p.y < 0) p.y = canvas.height;
    if (p.y > canvas.height) p.y = 0;

    if (p.highlight) {
      const r = p.size * 4;
      const gradient = ctx.createRadialGradient(p.x, p.y, 0, p.x, p.y, r);
      gradient.addColorStop(0, `rgba(96, 165, 250, ${p.opacity})`);
      gradient.addColorStop(0.4, `rgba(96, 165, 250, ${p.opacity * 0.4})`);
      gradient.addColorStop(1, 'rgba(96, 165, 250, 0)');
      ctx.beginPath();
      ctx.arc(p.x, p.y, r, 0, Math.PI * 2);
      ctx.fillStyle = gradient;
      ctx.fill();
    } else {
      ctx.beginPath();
      ctx.arc(p.x, p.y, p.size, 0, Math.PI * 2);
      ctx.fillStyle = `rgba(147, 197, 253, ${p.opacity})`;
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
        ctx.strokeStyle = `rgba(147, 197, 253, ${lineOpacity})`;
        ctx.lineWidth = 0.6;
        ctx.stroke();
      }
    });
  });

  animationId = requestAnimationFrame(() => animateParticles(canvas, particles));
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
  if (countdownTimer) clearInterval(countdownTimer);
  if (handleResize) window.removeEventListener('resize', handleResize);
});
</script>

<style>
/* 全局页面背景 - 深色渐变衬托粒子 */
html,
body,
#app {
  background: linear-gradient(135deg, #060d1a 0%, #0f1c33 40%, #0a1525 100%);
  min-height: 100vh;
}
</style>

<style scoped>
.login-shell {
  display: flex;
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  background: transparent;
}

/* 粒子画布 */
.particle-canvas {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: 1;
  pointer-events: none;
}

/* 左侧品牌区 */
.brand-panel {
  width: 42%;
  flex-shrink: 0;
  background: linear-gradient(160deg, rgba(12, 25, 41, 0.85) 0%, rgba(30, 58, 95, 0.85) 100%);
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
    linear-gradient(rgba(96, 165, 250, 0.06) 1px, transparent 1px),
    linear-gradient(90deg, rgba(96, 165, 250, 0.06) 1px, transparent 1px);
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
  color: #f1f5f9;
}

.brand-slogan {
  margin: 0 0 28px;
  font-size: 13px;
  color: #64748b;
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
  color: #94a3b8;
  letter-spacing: 1px;
}

.tag-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #60a5fa;
  box-shadow: 0 0 8px #60a5fa;
}

/* 任务列表 */
.mission-list {
  background: rgba(96, 165, 250, 0.05);
  border: 1px solid rgba(96, 165, 250, 0.1);
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
  color: #cbd5e1;
  border-bottom: 1px solid rgba(96, 165, 250, 0.08);
}

.mission-item:last-child {
  border-bottom: none;
}

.mission-icon {
  color: #60a5fa;
  font-size: 10px;
}

/* 版本信息 */
.version-info {
  font-size: 11px;
  color: #475569;
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
  max-width: 380px;
  padding: 44px 36px;
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(12px);
  border-radius: 16px;
  box-shadow:
    0 20px 40px rgba(0, 0, 0, 0.15),
    0 0 0 1px rgba(96, 165, 250, 0.1);
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
  margin-bottom: 18px;
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
  color: #2563eb;
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
  box-shadow: 0 0 0 2px #2563eb;
}

:deep(.el-input__inner) {
  font-size: 15px;
  height: 44px;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.login-btn {
  width: 100%;
  height: 46px;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 3px;
  background: linear-gradient(135deg, #1e40af 0%, #1e3a8a 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(30, 64, 175, 0.3);
  transition: all 0.2s;
}

.login-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(30, 64, 175, 0.4);
}

.login-btn:active:not(:disabled) {
  transform: translateY(0);
}

.form-footer {
  text-align: center;
  margin-top: 20px;
  color: #64748b;
  font-size: 14px;
}

.divider {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 18px 0;
  color: #9ca3af;
  font-size: 12px;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: #e5e7eb;
}

.guest-btn {
  width: 100%;
  color: #64748b;
  font-size: 13px;
  transition: color 0.2s;
}

.guest-btn:hover {
  color: #2563eb;
}

@media (max-width: 900px) {
  .login-shell {
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
}
</style>