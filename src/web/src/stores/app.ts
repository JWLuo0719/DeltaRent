import { defineStore } from 'pinia';

export const useAppStore = defineStore('app', {
  state: () => ({
    projectName: '三角洲哈夫币回收与资源号租赁系统',
    apiBaseUrl: 'http://localhost:8080/api'
  })
});
