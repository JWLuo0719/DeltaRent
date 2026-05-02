import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import AppLayout from '@/views/layouts/AppLayout.vue';
import PortalView from '@/views/PortalView.vue';
import LoginView from '@/views/LoginView.vue';
import RegisterView from '@/views/RegisterView.vue';
import RentalOrderCreateView from '@/views/RentalOrderCreateView.vue';
import RentalListView from '@/views/RentalListView.vue';
import AdminDashboardView from '@/views/AdminDashboardView.vue';
import OrderListView from '@/views/OrderListView.vue';
import OrderDetailView from '@/views/OrderDetailView.vue';
import ProfileView from '@/views/ProfileView.vue';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    // ==================== 登录/注册（无顶栏）====================
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: { roles: [] }
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView,
      meta: { roles: [] }
    },
    // ==================== 共享布局路由 ====================
    {
      path: '/',
      component: AppLayout,
      children: [
        {
          path: '',
          redirect: '/home'
        },
        {
          path: 'home',
          name: 'portal',
          component: PortalView,
          meta: { roles: [] }
        },
        {
          path: 'rentals',
          name: 'rentals',
          component: RentalListView,
          meta: { roles: [] }
        },
        // ==================== 需要登录的路由 ====================
        {
          path: 'orders/create',
          name: 'rental-create',
          component: RentalOrderCreateView,
          meta: { roles: ['USER', 'ADMIN', 'CS'], requireAuth: true }
        },
        {
          path: 'orders',
          name: 'orders',
          component: OrderListView,
          meta: { roles: ['USER', 'ADMIN', 'CS'], requireAuth: true }
        },
        {
          path: 'orders/:id',
          name: 'order-detail',
          component: OrderDetailView,
          meta: { roles: ['USER', 'ADMIN', 'CS'], requireAuth: true }
        },
        {
          path: 'profile',
          name: 'profile',
          component: ProfileView,
          meta: { roles: ['USER', 'ADMIN', 'CS'], requireAuth: true }
        },
        // ==================== 后台管理路由（ADMIN / CS）====================
        {
          path: 'admin',
          name: 'admin',
          component: AdminDashboardView,
          meta: { roles: ['ADMIN', 'CS'], requireAuth: true }
        }
      ]
    }
  ]
});

// 路由守卫
router.beforeEach((to, _from, next) => {
  const auth = useAuthStore();
  const { requireAuth, roles } = to.meta as { requireAuth?: boolean; roles?: string[] };

  if (!requireAuth) {
    next();
    return;
  }

  if (requireAuth && !auth.isLoggedIn) {
    next({ name: 'login', query: { redirect: to.fullPath } });
    return;
  }

  if (roles && roles.length > 0 && auth.user) {
    if (roles.includes(auth.user.role)) {
      next();
    } else {
      next({ name: 'portal' });
    }
    return;
  }

  next();
});

export default router;
