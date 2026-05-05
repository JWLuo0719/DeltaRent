<template>
  <div class="admin-shell">
    <!-- 左侧菜单 -->
    <aside class="admin-sidebar">
      <div class="sidebar-logo">
        <span class="logo-icon">◆</span>
        <span class="logo-text">DeltaRent</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        @select="handleMenuSelect"
      >
        <el-menu-item v-for="menu in visibleMenus" :key="menu.key" :index="menu.key">
          <span class="menu-icon">{{ menu.icon }}</span>
          <span>{{ menu.label }}</span>
        </el-menu-item>
      </el-menu>
    </aside>

    <!-- 右侧内容 -->
    <div class="admin-main">
      <!-- 顶部栏 -->
      <header class="admin-topbar">
        <div class="topbar-left">
          <h2>{{ currentMenuItem?.label }}</h2>
        </div>
        <div class="topbar-right">
          <el-tag :type="roleTagType" size="small">{{ roleText }}</el-tag>
          <span class="user-name">{{ auth.user?.displayName }}</span>
          <el-button size="small" @click="handleLogout">退出</el-button>
        </div>
      </header>

      <!-- 内容区 -->
      <main class="admin-content">
        <component :is="currentView" />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watchEffect } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useAuthStore } from '@/stores/auth';
import StatsView from './admin/StatsView.vue';
import UserManageView from './admin/UserManageView.vue';
import RoleManageView from './admin/RoleManageView.vue';
import ProductManageView from './admin/ProductManageView.vue';
import OrderManageView from './admin/OrderManageView.vue';
import NoticeManageView from './admin/NoticeManageView.vue';

const router = useRouter();
const auth = useAuthStore();
const activeMenu = ref('stats');

// 菜单配置
const allMenus = [
  { key: 'stats', label: '数据看板', icon: '📊', roles: ['ADMIN'] },
  { key: 'users', label: '用户管理', icon: '👤', roles: ['ADMIN'] },
  { key: 'roles', label: '角色管理', icon: '🔐', roles: ['ADMIN'] },
  { key: 'products', label: '账号管理', icon: '🎮', roles: ['ADMIN', 'CS'] },
  { key: 'orders', label: '订单管理', icon: '📋', roles: ['ADMIN', 'CS'] },
  { key: 'notices', label: '公告管理', icon: '📢', roles: ['ADMIN'] }
];

// 根据角色过滤菜单
const visibleMenus = computed(() => {
  if (!auth.user) return [];
  return allMenus.filter(m => m.roles.includes(auth.user!.role));
});

watchEffect(() => {
  if (!visibleMenus.value.find(menu => menu.key === activeMenu.value) && visibleMenus.value.length > 0) {
    activeMenu.value = visibleMenus.value[0].key;
  }
});

// 当前选中菜单项
const currentMenuItem = computed(() =>
  allMenus.find(m => m.key === activeMenu.value)
);

// 视图映射
const viewMap: Record<string, any> = {
  stats: StatsView,
  users: UserManageView,
  roles: RoleManageView,
  products: ProductManageView,
  orders: OrderManageView,
  notices: NoticeManageView
};

const currentView = computed(() => viewMap[activeMenu.value] || StatsView);

// 角色显示
const roleText = computed(() => {
  const map: Record<string, string> = { ADMIN: '管理员', USER: '普通用户', CS: '客服' };
  return map[auth.user?.role || ''] || '';
});

const roleTagType = computed(() => {
  const map: Record<string, string> = { ADMIN: 'danger', CS: 'warning', USER: 'default' };
  return map[auth.user?.role || ''] || 'info';
});

function handleMenuSelect(key: string) {
  activeMenu.value = key;
}

function handleLogout() {
  auth.logout();
  router.push('/home');
  ElMessage.success('已退出登录');
}
</script>

<style scoped>
.admin-shell {
  display: flex;
  min-height: 100vh;
  background: #f5f7fa;
}

.admin-sidebar {
  width: 220px;
  background: #1a1a2e;
  display: flex;
  flex-direction: column;
}

.sidebar-logo {
  padding: 20px 16px;
  display: flex;
  align-items: center;
  gap: 10px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}

.logo-icon {
  color: #60a5fa;
  font-size: 20px;
}

.logo-text {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
}

.sidebar-menu {
  flex: 1;
  background: transparent;
  border: none;
}

:deep(.el-menu-item) {
  color: rgba(255,255,255,0.7);
  height: 48px;
  line-height: 48px;
}

:deep(.el-menu-item.is-active) {
  background: rgba(96, 165, 250, 0.15);
  color: #60a5fa;
}

:deep(.el-menu-item:hover) {
  background: rgba(255,255,255,0.05);
  color: #fff;
}

.menu-icon {
  margin-right: 10px;
}

.admin-main {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.admin-topbar {
  height: 60px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
}

.topbar-left h2 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-name {
  font-size: 14px;
  color: #606266;
}

.admin-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}
</style>
