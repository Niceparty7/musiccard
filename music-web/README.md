# MusicCard 前端

基于 Vue 3、Element Plus、Axios 与 Vite 的 MusicCard 双端前端。一个 SPA 同时提供响应式 App 端与 Console 管理端，共享品牌视觉，但保持各自的信息密度和交互方式。

## 入口

- App 端：`http://localhost:5173/app/explore`
- Console 登录：`http://localhost:5173/console/login`
- Console 总览：`http://localhost:5173/console/dashboard`

## 本地运行

```bash
npm install
npm run dev
```

开发模式默认启用可视化 Mock 数据。需要联调后端时，将 `.env.development` 中的 `VITE_USE_MOCK` 改为 `false`。Vite 会代理：

- `/api/app/*` → `http://localhost:8080/*`
- `/api/user/*` → `http://localhost:8083/*`（App 登录/注册）
- `/api/console/*` → `http://localhost:8081/*`

## 构建

```bash
npm run build
```

生产接口地址在 `.env.production` 中配置。App 登录后将后端返回的 `sign` 写入本地存储，并通过 `sign` 请求头发送；Console Axios 开启 `withCredentials`，使用后端 Cookie/Session。

## 目录

- `src/api`：Axios 客户端、接口封装和开发 Mock
- `src/layouts`：两端壳层、导航与响应式结构
- `src/views/app`：发现瀑布流、分类、详情、账户
- `src/views/console`：登录、总览、音乐、分类、标签、短信
- `src/components/AlbumArtwork.vue`：品牌唱片封面生成器
- `scripts/visual-qa.cjs`：桌面与手机视口截图检查

## 联调注意

Console 登录依赖 Session Cookie；跨域部署时后端需正确配置 CORS、凭证和 Cookie 的 SameSite/Secure。列表接口的 `wp` 是不透明游标，前端只原样保存和回传；搜索词变化会清空旧游标。

App 的音乐详情页需要登录：未登录访问会先跳转到 `/app/account`，登录成功后自动回到原详情页。Console 除登录页外均需要登录，浏览器会保存后端 Session Cookie 和本地会话标记。App 列表接近页面底部时会使用 `wp` 自动请求下一页，不提供手动“加载更多”按钮。
