import { createApp } from 'vue';
import { createPinia } from 'pinia';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import App from './App.vue';
import router from './router';
import './styles/index.css';
import { useAuthStore } from '@/stores/auth';

const app = createApp(App);

//安装插件
app.use(createPinia());
app.use(router);
app.use(ElementPlus);

// 启动校验，清除残留的无效状态
useAuthStore().validateAuth();

//挂载应用
app.mount('#app');
