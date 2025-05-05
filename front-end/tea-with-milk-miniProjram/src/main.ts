import { createSSRApp } from "vue";
import App from "./App.vue";
import '@/main.css'
import * as Pinia from 'pinia';
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
export function createApp() {
  const app = createSSRApp(App);
 const pinia= Pinia.createPinia()
 pinia.use(piniaPluginPersistedstate)
  app.use(pinia);
  return {
    app,
    Pinia
  };
}

