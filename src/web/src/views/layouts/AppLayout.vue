<template>
  <div class="app-layout" :class="{ 'app-layout-rental': isRentalPage }">
    <header class="top-nav" :class="{ 'top-nav-rental': isRentalPage }">
      <div class="nav-brand">
        <span class="brand-icon">{{ isRentalPage ? '租' : 'Δ' }}</span>
        <span class="brand-name">{{ isRentalPage ? 'DeltaRent 租号大厅' : 'DeltaRent' }}</span>
      </div>

      <nav class="nav-links">
        <router-link to="/home" class="nav-link">首页</router-link>
        <router-link to="/rentals" class="nav-link">租号大厅</router-link>
        <router-link v-if="auth.isLoggedIn" to="/orders" class="nav-link">我的订单</router-link>
        <router-link v-if="auth.isLoggedIn" to="/profile" class="nav-link">个人中心</router-link>
        <router-link v-if="canShowAdmin" to="/admin" class="nav-link admin-link">后台管理</router-link>
      </nav>

      <div class="nav-auth">
        <template v-if="auth.isLoggedIn">
          <el-tag :type="roleTagType" size="small">{{ roleText }}</el-tag>
          <span class="user-name">{{ auth.user?.displayName }}</span>
          <el-button size="small" :type="isRentalPage ? 'warning' : 'default'" @click="handleLogout">退出</el-button>
        </template>
        <template v-else>
          <el-button size="small" type="warning" @click="$router.push('/login')">登录</el-button>
          <el-button size="small" type="warning" @click="$router.push('/register')">注册</el-button>
        </template>
      </div>
    </header>

    <main class="layout-main" :class="{ 'layout-main-rental': isRentalPage }">
      <router-view v-slot="{ Component }">
        <Transition name="page-fade" mode="out-in">
          <component :is="Component" />
        </Transition>
      </router-view>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useAuthStore } from '@/stores/auth';

const auth = useAuthStore();
const route = useRoute();
const router = useRouter();

const canShowAdmin = computed(() => auth.user?.role === 'ADMIN' || auth.user?.role === 'CS');
const isRentalPage = computed(() => route.name === 'rentals');

const roleText = computed(() => {
  const map: Record<string, string> = {
    ADMIN: '管理员',
    USER: '普通用户',
    CS: '客服'
  };
  return map[auth.user?.role || ''] || '';
});

const roleTagType = computed(() => {
  const map: Record<string, string> = {
    ADMIN: 'danger',
    CS: 'warning',
    USER: 'info'
  };
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
  background:
    radial-gradient(circle at top left, rgba(255, 221, 111, 0.22), transparent 18%),
    linear-gradient(180deg, #fff8df 0%, #fffdf4 24%, #f8fafc 100%);
  transition: background 0.25s ease;
}

.app-layout-rental {
  background:
    radial-gradient(circle at top left, rgba(255, 221, 111, 0.3), transparent 18%),
    linear-gradient(180deg, #fff7d8 0%, #fffdf4 24%, #fff9ec 100%);
}

.page-fade-enter-active {
  transition: opacity 0.15s ease;
}

.page-fade-leave-active {
  transition: opacity 0.08s ease;
}

.page-fade-enter-from,
.page-fade-leave-to {
  opacity: 0;
}

.top-nav {
  position: sticky;
  top: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  padding: 0 32px;
  background: rgba(255, 252, 244, 0.92);
  box-shadow: 0 10px 28px rgba(168, 133, 39, 0.1);
  border-bottom: 1px solid rgba(242, 212, 122, 0.28);
  backdrop-filter: blur(12px);
  transition: background 0.25s ease, box-shadow 0.25s ease;
}

.top-nav-rental {
  background: rgba(255, 252, 244, 0.92);
  box-shadow: 0 10px 28px rgba(168, 133, 39, 0.1);
  border-bottom: 1px solid rgba(242, 212, 122, 0.28);
}

.nav-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 220px;
}

.brand-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  border-radius: 10px;
  background: linear-gradient(135deg, #ffd95b 0%, #ffb62a 100%);
  color: #5a3c00;
  font-size: 16px;
  font-weight: 900;
}

.brand-name {
  color: #2a313e;
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 0.2px;
  transition: color 0.25s ease;
}

.nav-links {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  flex: 1;
}

.nav-link {
  padding: 8px 16px;
  border-radius: 999px;
  color: #5d6676;
  text-decoration: none;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.2s ease;
}

.nav-link:hover,
.nav-link.router-link-active {
  color: #9b5d00;
  background: #fff0d0;
}

.admin-link {
  color: #f4c44e;
}

.top-nav-rental .admin-link {
  color: #c57a00;
}

.nav-auth {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 10px;
  min-width: 220px;
}

.user-name {
  font-size: 14px;
  color: #5d6676;
  transition: color 0.25s ease;
}

.layout-main {
  padding: 24px;
}

.layout-main-rental {
  padding: 0;
}

@media (max-width: 980px) {
  .top-nav {
    height: auto;
    padding: 14px 16px;
    flex-wrap: wrap;
    gap: 12px;
  }

  .nav-brand,
  .nav-auth {
    min-width: 0;
  }

  .nav-links {
    order: 3;
    width: 100%;
    justify-content: flex-start;
    overflow-x: auto;
    padding-bottom: 2px;
  }

  .layout-main {
    padding: 16px;
  }

  .layout-main-rental {
    padding: 0;
  }
}
</style>
