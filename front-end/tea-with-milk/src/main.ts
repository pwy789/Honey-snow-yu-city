import '../main.css'
import 'element-plus/dist/index.css';
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import  'bootstrap'
import 'bootstrap/dist/css/bootstrap.css'; // 引入 bootstrap 的 CSS
import App from './App.vue'
import router from './router'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import persist from 'pinia-plugin-persistedstate'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import ECharts from '@/utils/echarts'
const app = createApp(App)
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
const pinia = createPinia()
pinia.use(persist)
app.use(pinia)
app.use(router)
app.use(ElementPlus, {
  locale: zhCn,
})
app.mount('#app')
app.component('VChart',ECharts)
