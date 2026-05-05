<template>
  <div class="app-layout">
    <!-- 统一顶栏 -->
    <header class="top-nav">
      <div class="nav-brand">
        <span class="brand-icon">◆</span>
        <span class="brand-name">DeltaRent</span>
      </div>
      <nav class="nav-links">
        <router-link to="/home" class="nav-link">首页</router-link>
        <router-link to="/rentals" class="nav-link">账号列表</router-link>
        <router-link v-if="auth.isLoggedIn" to="/orders" class="nav-link">我的订单</router-link>
        <router-link v-if="auth.isLoggedIn" to="/profile" class="nav-link">个人资料</router-link>
        <router-link v-if="canShowAdmin" to="/admin" class="nav-link admin-link">后台管理</router-link>
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

    <!-- 页面内容 -->
    <main class="layout-main">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useAuthStore } from '@/stores/auth';

const auth = useAuthStore();
const router = useRouter();

const canShowAdmin = computed(() => auth.user?.role === 'ADMIN' || auth.user?.role === 'CS');

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
</script>

<style scoped>
.app-layout {
  min-height: 100vh;
  background: #060d1a;
}

.top-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  height: 60px;
  background: #1a1a2e;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.2);
  position: sticky;
  top: 0;
  z-index: 100;
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
  color: #fff;
  font-size: 18px;
  font-weight: 600;
}

.nav-links {
  display: flex;
  gap: 4px;
}

.nav-link {
  padding: 6px 16px;
  border-radius: 6px;
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  font-size: 14px;
  transition: all 0.2s;
}

.nav-link:hover,
.nav-link.router-link-active {
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
}

.admin-link {
  color: #e6a23c;
}

.nav-auth {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-name {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
}

.layout-main {
  padding: 24px;
}
</style>
