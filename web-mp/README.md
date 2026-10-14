# 基础平台 - 移动端 (web-mp)

Vue 3 + uview-plus 3 + uniapp 3 移动端应用，**纯 JavaScript**。
一份代码同时跑 **H5 / 微信小程序 / Android / iOS / 其它小程序**。

> 关键约定：
> - `src/main.js` / `src/App.vue` / `src/pages.json` / `src/manifest.json` / `src/uni.scss` 在 `src/` 子目录（uniapp 3 alpha 工具链要求）
> - `index.html` `vite.config.js` `package.json` `config.js`（双份）`manifest.json`（双份）`pages.json`（双份）在项目根
> - **不走 HBuilderX**，全部走 CLI：`pnpm dev:h5` / `pnpm dev:mp-weixin` / `pnpm dev:app`

## 一、技术栈

| 项 | 版本 |
|---|---|
| uni-app | 3.0 (alpha) |
| Vue | 3.4.21（**组合式 API** `<script setup>`） |
| Vite | 5.2.x |
| UI | **uview-plus** 3.3.x（uView 的 Vue3 版本，组件名带 `up-` 前缀） |
| 状态管理 | Pinia 2.x |
| 加密 | jsencrypt（RSA 登录） |
| 语言 | **JavaScript**（无 TypeScript） |
| 包管理 | pnpm |

## 二、目录结构

```
web-mp/
├── index.html                 # H5 入口（vite 用）
├── vite.config.js             # Vite 配置（含 /dev-api 代理到 8080）
├── package.json
├── config.js                  # 全局配置（双份，同步到 src/）
├── manifest.json              # 应用清单（双份，同步到 src/）
├── pages.json                 # 页面注册（双份，同步到 src/）
├── sync-config.cjs            # 双份同步脚本
├── src/
│   ├── main.js                # 入口：createSSRApp + Pinia + 显式注册 up- 组件
│   ├── App.vue                # 根组件（@import uview-plus 全局样式）
│   ├── uni.scss               # 全局 SCSS 变量入口
│   ├── pages/                 # 业务页面
│   │   ├── login/index.vue
│   │   ├── index/index.vue    # 首页（TabBar）
│   │   ├── workbench/index.vue
│   │   ├── user/index.vue
│   │   └── common/
│   │       ├── notice/{index,detail}.vue
│   │       └── profile/index.vue
│   ├── api/                   # 接口层
│   │   ├── index.js
│   │   ├── login.js
│   │   └── system/{user,role,dept}.js
│   ├── store/                 # Pinia
│   │   ├── index.js
│   │   └── modules/{user,permission,app}.js
│   ├── utils/                 # 工具
│   │   ├── config.js          # 读 @/config
│   │   ├── auth.js            # storage 封装
│   │   ├── request.js         # uni.request 封装 + 拦截器
│   │   ├── dict.js            # useDict 字典
│   │   └── jsencrypt.js       # RSA 加密
│   ├── directive/permission.js  # v-hasPermi / v-hasRole
│   └── permission.js          # 全局导航守卫
└── unpackage/                 # 编译产物
```

## 三、与后端对接

`src/config.js` 里维护一份 `baseUrl`：

```js
export default {
  baseUrl: '/dev-api',  // H5 走 vite proxy → http://localhost:8080
  rsa: true,            // 后端启用 RSA 加密
  captcha: true,
  timeout: 30000
}
```

后端就是本仓库的 `basic-admin-api`（Spring Boot 2.5 + Spring Security），对接的接口：
`/login` `/logout` `/captchaImage` `/getInfo` `/getRouters` 等。

### 默认账号
- `admin / admin123`

## 四、运行

```bash
cd web-mp
pnpm install

# 开发
pnpm dev:h5            # 浏览器访问 http://localhost:8081
pnpm dev:mp-weixin     # 用微信开发者工具打开 dist/dev/mp-weixin
pnpm dev:app           # 编译到 unpackage/dist/dev/app

# 生产构建
pnpm build:h5          # 产物在 dist/build/h5
pnpm build:mp-weixin   # 产物在 dist/build/mp-weixin
pnpm build:app
```

> H5 走 `/dev-api` 由 `vite.config.js` 代理到 `http://localhost:8080`。
> 小程序/真机调试时需要把 `src/config.js` 的 `baseUrl` 改成局域网 IP，并在小程序后台加 request 合法域名。

## 五、关键设计

- **登录流程**：`/captchaImage` → `/login`（RSA 公钥加密密码）→ `/getInfo` → `/getRouters`
- **Token 存储**：`uni.getStorageSync('Basic-Token')`
- **请求封装**：`utils/request.js` 实现拦截器（401 跳登录）
- **权限指令**：`v-hasPermi`（uniapp 3 编译时不支持自定义指令，用 `v-if="hasPermi([...])"` 替代）
- **双份 manifest/pages**：CLI build 找 `src/manifest.json`，dev 找根 `manifest.json`，所以两边各放一份，`sync-config.cjs` 同步

## 六、待办

- [ ] 接入动态菜单（`/getRouters` → pages.json）
- [ ] 国际化（`vue-i18n` 已引入，词条待补）
- [ ] 消息推送 / WebSocket
- [ ] 修改密码页 + 个人中心编辑
- [ ] 锁屏（手势/PIN）
- [ ] TabBar 图标（src/static/tabbar/ 需放 6 张 81x81 png）
