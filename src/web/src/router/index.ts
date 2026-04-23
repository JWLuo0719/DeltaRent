import { createRouter, createWebHistory } from 'vue-router';
import PortalView from '@/views/PortalView.vue';
import LoginView from '@/views/LoginView.vue';
import RentalOrderCreateView from '@/views/RentalOrderCreateView.vue';
import RentalListView from '@/views/RentalListView.vue';
import AdminDashboardView from '@/views/AdminDashboardView.vue';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'portal',
      component: PortalView
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/orders/create',
      name: 'rental-create',
      component: RentalOrderCreateView
    },
    {
      path: '/rentals',
      name: 'rentals',
      component: RentalListView
    },
    {
      path: '/admin',
      name: 'admin',
      component: AdminDashboardView
    }
  ]
});

export default router;
