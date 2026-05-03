import axios from 'axios';
import { useAuthStore } from '@/stores/auth';
import router from '@/router';

const http = axios.create({
  baseURL: '/api',
  timeout: 10000
});

// 请求拦截器：自动带上 Authorization header
http.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('auth_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 响应拦截器：401 时清除状态并跳转登录
http.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      useAuthStore().logout();
      router.push('/login');
    }
    return Promise.reject(error);
  }
);

export default http;
