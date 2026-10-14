# 基础平台 - 移动端 (web-mp)

Vue 3 + uview-plus 3 + uniapp 3 移动端应用，**TypeScript**。
一份代码同时跑 **H5 / 微信小程序 / Android / iOS / 其它小程序**。

> 关键约定：
> - `src/main.ts` / `src/App.vue` / `src/pages.json` / `src/manifest.json` / `src/uni.scss` 在 `src/` 子目录（uniapp 3 alpha 工具链要求）
> - `index.html` `vite.config.ts` `package.json` `tsconfig.json` `config.js`（双份）`manifest.json`（双份）`pages.json`（双份）在项目根
> - **不走 HBuilderX**，全部走 CLI：`pnpm dev:h5` / `pnpm dev:mp-weixin` / `pnpm dev:app`
> - `sync-config.cjs` `fixup-mp-components.cjs` Node 脚本保持 .cjs（不依赖 ts-node）

## 一、技术栈

| 项 | 版本 |
|---|---|
| uni-app | 3.0 (alpha) |
| Vue | 3.4.21（**组合式 API** `<script setup lang="ts">`） |
| Vite | 5.2.x |
| UI | **uview-plus** 3.3.x（uView 的 Vue3 版本，组件名带 `up-` 前缀） |
| 状态管理 | Pinia 2.x |
| 加密 | jsencrypt（RSA 登录） |
| 语言 | **TypeScript** |
| 包管理 | pnpm |

## 二、目录结构

```
web-mp/
├── index.html                 # H5 入口（vite 用）
├── vite.config.ts             # Vite 配置（含 mp fixup 插件）
├── tsconfig.json              # TypeScript 配置
├── package.json
├── config.js                  # 全局配置（双份，同步到 src/）
├── manifest.json              # 应用清单（双份，同步到 src/）
├── pages.json                 # 页面注册（双份，同步到 src/）
├── sync-config.cjs            # 双份同步脚本
├── scripts/
│   └── fixup-mp-components.cjs  # mp build 后注入 usingComponents
└── src/
    ├── main.ts                # 入口：createSSRApp + Pinia + 显式注册 up- 组件
    ├── App.vue                # 根组件（lang="ts" options API）
    ├── types/
    │   └── global.d.ts        # 全局类型（AjaxResult / UserInfo / PageResult / DictItem / ...）
    ├── uni.scss               # 全局 SCSS 变量入口
    ├── pages/                 # 业务页面（<script setup lang="ts">）
    │   ├── login/index.vue
    │   ├── index/index.vue    # 首页（TabBar）
    │   ├── workbench/index.vue
    │   ├── user/index.vue
    │   └── common/
    │       ├── notice/{index,detail}.vue
    │       └── profile/index.vue
    ├── api/                   # 接口层
    │   ├── index.ts
    │   ├── login.ts
    │   └── system/{user,role,dept}.ts
    ├── store/                 # Pinia
    │   ├── index.ts
    │   └── modules/{user,permission,app}.ts
    ├── utils/                 # 工具
    │   ├── config.ts          # 读 @/config
    │   ├── auth.ts            # storage 封装
    │   ├── request.ts         # uni.request 封装 + 拦截器
    │   ├── dict.ts            # useDict 字典
    │   └── jsencrypt.ts       # RSA 加密
    ├── directive/permission.ts  # v-hasPermi / v-hasRole
    └── permission.ts          # 全局导航守卫
```

## 三、与后端对接

`src/config.ts` 里维护一份 `baseUrl`：

```ts
export default {
  baseUrl: 'http://localhost:8080',  // 改成你的局域网 IP
  rsa: true,
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

# 类型检查
pnpm typecheck         # vue-tsc --noEmit

# 生产构建
pnpm build:h5          # 产物在 dist/build/h5
pnpm build:mp-weixin   # 产物在 dist/build/mp-weixin
pnpm build:app
```

> H5 走 src/config.ts 里的真实 URL（dev 推荐 局域网 IP 8080）。
> 小程序/真机调试时需要把 `src/config.ts` 的 `baseUrl` 改成局域网 IP，并在小程序后台加 request 合法域名。

## 五、关键设计

- **登录流程**：`/captchaImage` → `/login`（RSA 公钥加密密码）→ `/getInfo` → `/getRouters`
- **Token 存储**：`uni.getStorageSync('Basic-Token')`
- **请求封装**：`utils/request.ts` 实现拦截器（401 跳登录）
- **权限判断**：`v-if="hasPermi([...])"` 替代自定义指令（uniapp 3 vite 不支持编译时指令）
- **双份 manifest/pages**：CLI build 找 `src/manifest.json`，dev 找根 `manifest.json`
- **mp build 注入 usingComponents**：`scripts/fixup-mp-components.cjs` 编译后扫 dist 把 uview 组件路径注入每个 page 的 index.json（解决 uniapp 3 alpha easycom 失效）

## 六、待办

- [ ] 接入动态菜单（`/getRouters` → pages.json）
- [ ] 国际化（`vue-i18n` 已引入，词条待补）
- [ ] 消息推送 / WebSocket
- [ ] 修改密码页 + 个人中心编辑
- [ ] 锁屏（手势/PIN）
- [ ] TabBar 图标（src/static/tabbar/ 需放 4 张 81x81 png）
