import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import "./main.css"

if (typeof window !== 'undefined') {
  window.global = window.global || window;
}

const app = createApp(App)

app.use(router)

app.mount('#app')
