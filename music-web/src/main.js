import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './styles/tokens.css'
import './styles/global.css'
import './styles/infinite-scroll.css'
import './styles/ambient-motion.css'
import './styles/required-fields.css'
import App from './App.vue'
import router from './router'

createApp(App).use(router).use(ElementPlus).mount('#app')
