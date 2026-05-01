import { createApp } from 'vue';
import { createPinia } from 'pinia';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import App from './App.vue';
import router from './router';
import './styles/index.css';

const app = createApp(App); // 创建 Vue 应用实例

//安装插件
app.use(createPinia());  
app.use(router);    
app.use(ElementPlus);

//挂载应用
app.mount('#app');
