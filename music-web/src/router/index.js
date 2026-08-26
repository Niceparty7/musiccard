import { createRouter, createWebHistory } from 'vue-router'
import AppLayout from '../layouts/AppLayout.vue'
import ConsoleLayout from '../layouts/ConsoleLayout.vue'

const routes = [
  { path: '/', redirect: '/app/explore' },
  { path: '/app', component: AppLayout, children: [
    { path: '', redirect: '/app/explore' },
    { path: 'explore', component: () => import('../views/app/ExploreView.vue') },
    { path: 'categories', component: () => import('../views/app/CategoryView.vue') },
    { path: 'music/:id', component: () => import('../views/app/MusicDetailView.vue'), meta: { requiresAppAuth: true } },
    { path: 'account', component: () => import('../views/app/AccountView.vue') }
  ]},
  { path: '/console/login', component: () => import('../views/console/ConsoleLoginView.vue') },
  { path: '/console', component: ConsoleLayout, children: [
    { path: '', redirect: '/console/dashboard' },
    { path: 'dashboard', component: () => import('../views/console/DashboardView.vue') },
    { path: 'music', component: () => import('../views/console/MusicManageView.vue') },
    { path: 'categories', component: () => import('../views/console/TaxonomyView.vue'), props: { mode: 'category' } },
    { path: 'tags', component: () => import('../views/console/TaxonomyView.vue'), props: { mode: 'tag' } },
    { path: 'sms', component: () => import('../views/console/SmsLabView.vue') }
  ]},
  { path: '/:pathMatch(.*)*', redirect: '/app/explore' }
]

const router=createRouter({ history: createWebHistory(), routes, scrollBehavior: () => ({ top: 0 }) })

router.beforeEach(to=>{
  if(to.matched.some(record=>record.meta.requiresAppAuth)&&!localStorage.getItem('musiccard_sign')){
    return { path: '/app/account', query: { redirect: to.fullPath } }
  }
  // Console 的真实登录态存放在 HttpOnly Cookie + Redis Session 中，
  // JavaScript 无法也不应该用 localStorage 判断。接口返回 1002/401/403 时
  // 再由 Axios 统一跳转登录页，避免缓存存在但 Session 已失效造成假登录。
})

export default router
