import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

const sameOriginProxy = target => ({
  target,
  changeOrigin: true,
  configure(proxy) {
    // 浏览器只访问 Vite 同源地址；转发到本地后端时不应继续携带 5173 的 Origin，
    // 否则后端会把 POST/PUT/DELETE 当成跨域写请求并返回 403。
    proxy.on('proxyReq', proxyRequest => {
      proxyRequest.removeHeader('origin')
    })
  }
})

export default defineConfig({
  plugins: [vue()],
  build: { rollupOptions: { output: { manualChunks: { vue: ['vue', 'vue-router'], element: ['element-plus'], axios: ['axios'] } } } },
  server: {
    host: '0.0.0.0', port: 5173,
    proxy: {
      '/api/app': { ...sameOriginProxy('http://localhost:8080'), rewrite: path => path.replace(/^\/api\/app/, '') },
      '/api/user': { ...sameOriginProxy('http://localhost:8083'), rewrite: path => path.replace(/^\/api\/user/, '') },
      '/api/console': { ...sameOriginProxy('http://localhost:8081'), rewrite: path => path.replace(/^\/api\/console/, '') }
    }
  }
})
