import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

export interface UserInfo {
  id: number;
  displayName: string;
  role: 'ADMIN' | 'USER' | 'CS';
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('auth_token'));
  const user = ref<UserInfo | null>(null);
  try {
    const raw = localStorage.getItem('auth_user');
    if (raw) {
      user.value = JSON.parse(raw);
    }
  } catch {
    localStorage.removeItem('auth_user');
  }

  // 启动校验：若 token 无效，清除残留状态
  validateAuth();

  function validateAuth() {
    if (token.value && (!user.value || !user.value.id)) {
      logout();
    }
  }

  const isLoggedIn = computed(() => !!token.value && !!user.value);
  const isAdmin = computed(() => user.value?.role === 'ADMIN');
  const isCS = computed(() => user.value?.role === 'CS');
  const isUser = computed(() => user.value?.role === 'USER');

  function setAuth(newToken: string, newUser: UserInfo) {
    token.value = newToken;
    user.value = newUser;
    localStorage.setItem('auth_token', newToken);
    localStorage.setItem('auth_user', JSON.stringify(newUser));
  }

  function logout() {
    token.value = null;
    user.value = null;
    localStorage.removeItem('auth_token');
    localStorage.removeItem('auth_user');
  }

  return {
    token,
    user,
    isLoggedIn,
    isAdmin,
    isCS,
    isUser,
    setAuth,
    logout,
    validateAuth
  };
});