<template>
  <div class="page-shell">
    <!-- 顶部导航 -->
    <header class="top-nav">
      <div class="nav-brand">
        <span class="brand-icon">◆</span>
        <span class="brand-name">DeltaRent</span>
      </div>
      <nav class="nav-links">
        <el-button text @click="$router.push('/home')">首页</el-button>
        <el-button text @click="$router.push('/rentals')">账号列表</el-button>
        <el-button v-if="auth.isLoggedIn" text @click="$router.push('/orders')">我的订单</el-button>
        <el-button v-if="auth.isLoggedIn" text @click="$router.push('/profile')">个人资料</el-button>
        <el-button v-if="canShowAdmin" text type="warning" @click="$router.push('/admin')">后台管理</el-button>
      </nav>
      <div class="nav-auth">
        <template v-if="auth.isLoggedIn">
          <el-tag :type="roleTagType" size="small">{{ roleText }}</el-tag>
          <span class="user-name">{{ auth.user?.displayName }}</span>
          <el-button size="small" @click="handleLogout">退出</el-button>
        </template>
        <template v-else>
          <el-button size="small" @click="$router.push('/login')">登录</el-button>
          <el-button size="small" type="primary" @click="$router.push('/register')">注册</el-button>
        </template>
      </div>
    </header>

    <!-- Hero 区域 -->
    <section class="hero-card">
      <el-space direction="vertical" :size="20" fill>
        <div>
          <p class="page-title">{{ summary.heroTitle }}</p>
          <p class="page-subtitle">{{ summary.heroSubtitle }}</p>
        </div>
        <div class="toolbar">
          <el-button v-if="auth.isLoggedIn" type="primary" size="large" @click="$router.push('/orders/create')">立即下单</el-button>
          <el-button v-else type="primary" size="large" @click="$router.push('/login')">登录后下单</el-button>
          <el-button size="large" @click="$router.push('/rentals')">查看账号列表</el-button>
        </div>
      </el-space>
    </section>

    <section class="grid-3">
      <article v-for="item in summary.metrics" :key="item.label" class="metric">
        <h3>{{ item.label }}</h3>
        <strong>{{ item.value }}</strong>
      </article>
    </section>

    <section class="grid-2">
      <article class="panel-card">
        <h2 class="section-title">一期核心模块</h2>
        <el-tag v-for="item in summary.modules" :key="item" class="tag-item" effect="plain">{{ item }}</el-tag>
      </article>
      <article class="panel-card">
        <h2 class="section-title">最新公告</h2>
        <div class="notice-list">
          <div v-for="notice in summary.notices" :key="notice.id" class="notice-item">
            <strong>{{ notice.title }}</strong>
            <span>{{ notice.content }}</span>
          </div>
        </div>
      </article>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { getPortalSummary } from '@/api';
import { useAuthStore } from '@/stores/auth';
import type { PortalSummary } from '@/types/api';

const auth = useAuthStore();
const router = useRouter();

const canShowAdmin = computed(() => {
  return auth.user?.role === 'ADMIN' || auth.user?.role === 'CS';
});

const roleText = computed(() => {
  const map: Record<string, string> = { ADMIN: '管理员', USER: '普通用户', CS: '客服' };
  return map[auth.user?.role || ''] || '';
});

const roleTagType = computed(() => {
  const map: Record<string, string> = { ADMIN: 'danger', CS: 'warning', USER: 'info' };
  return map[auth.user?.role || ''] || 'info';
});

function handleLogout() {
  auth.logout();
  router.push('/home');
  ElMessage.success('已退出登录');
}

const summary = reactive<PortalSummary>({
  heroTitle: '三角洲行动账号租赁管理系统',
  heroSubtitle: '正在加载首页摘要...',
  metrics: [],
  modules: [],
  notices: []
});

async function loadSummary() {
  try {
    const response = await getPortalSummary();
    if (response.data.success) {
      Object.assign(summary, response.data.data);
      return;
    }

    ElMessage.error(response.data.message || '首页数据加载失败');
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '首页数据加载失败');
  }
}

onMounted(loadSummary);
</script>

<style scoped>
.top-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 32px;
  background: #1a1a2e;
  color: #fff;
}

.nav-brand {
  display: flex;
  align-items: center;
  gap: 8px;
}

.brand-icon {
  color: #60a5fa;
  font-size: 20px;
}

.brand-name {
  font-size: 18px;
  font-weight: 600;
}

.nav-links {
  display: flex;
  gap: 8px;
}

.nav-links .el-button {
  color: rgba(255,255,255,0.7);
}

.nav-links .el-button:hover {
  color: #fff;
  background: rgba(255,255,255,0.1);
}

.nav-links .el-button--warning {
  color: #e6a23c;
}

.nav-auth {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-name {
  font-size: 14px;
  color: rgba(255,255,255,0.7);
}

.tag-item {
  margin-right: 10px;
  margin-bottom: 10px;
}
</style>
