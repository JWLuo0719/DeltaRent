import { defineStore } from 'pinia';

export const useAppStore = defineStore('app', {
  state: () => ({
    projectName: '三角洲行动账号租赁管理系统',
    apiBaseUrl: '/api'
  })
});
