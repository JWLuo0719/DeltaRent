import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import AppLayout from '@/views/layouts/AppLayout.vue';

const PortalView = () => import('@/views/PortalView.vue');
const LoginView = () => import('@/views/LoginView.vue');
const RegisterView = () => import('@/views/RegisterView.vue');
const RentalOrderCreateView = () => import('@/views/RentalOrderCreateView.vue');
const RentalListView = () => import('@/views/RentalListView.vue');
const AdminDashboardView = () => import('@/views/AdminDashboardView.vue');
const OrderListView = () => import('@/views/OrderListView.vue');
const OrderDetailView = () => import('@/views/OrderDetailView.vue');
const ProfileView = () => import('@/views/ProfileView.vue');

const router = createRouter({
  history: createWebHistory(),
  routes: [
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

router.beforeEach((to, _from, next) => {
  const auth = useAuthStore();
  const { requireAuth, roles } = to.meta as { requireAuth?: boolean; roles?: string[] };

  if (!requireAuth) {
    next();
    return;
  }

  if (!auth.isLoggedIn) {
    next({ name: 'login', query: { redirect: to.fullPath } });
    return;
  }

  if (roles !== undefined && roles.length > 0 && auth.user) {
    if (roles.includes(auth.user.role)) {
      next();
    } else {
      next({ name: 'portal' });
    }
    return;
  }

  if (roles !== undefined && auth.user && requireAuth) {
    next({ name: 'portal' });
    return;
  }
});

export default router;
