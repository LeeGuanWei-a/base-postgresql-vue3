# 基础平台(base-postgresql-vue3) 架构方案 — `springboot2_TypeScript` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot2_TypeScript`** 分支(`base-postgresql-vue3`,基于 RuoYi-Vue 3.8.2 改造)
> 上游依赖:`master` 分支(RuoYi-Vue 3.8.2 / Spring Boot 2.5.14 / PostgreSQL 15+)
> 本分支相对 `master` 的**唯一差异 = 前端全量 TypeScript 化**(`web-ui` + `web-mp`),**后端 / 数据库 / 接口零修改**。

---

## 1. 系统定位

**一句话定位**:面向中小团队的后台管理基础平台,提供「用户/角色/菜单/部门/字典/参数/定时任务/代码生成/监控」一整套开箱即用的 RBAC 后台能力;在 Web 管理端之外新增 uni-app 移动端(可发布 H5 / 微信小程序 / Android / iOS);**本分支把 PC + 移动两端前端从 JavaScript 全量改写为 TypeScript**,后端保持 `master` 原样。

| 维度 | 内容 |
|---|---|
| 使用方 | 内部业务开发者(Java 后端 + Web 前端 + 移动端开发者),最终被业务方运营人员使用 |
| 触发方式 | PC 浏览器、微信小程序、Android/iOS App,均通过 HTTP/JSON 调用后端 REST |
| 数据形态 | PostgreSQL(34 张表) + 本地文件上传 + Redis 缓存(运行期) |
| 范围边界 | RBAC 系统管理、字典/参数/通知、定时任务、代码生成、登录/锁屏、操作日志、PC + 移动两套**TypeScript** 前端 |
| 不做清单(本分支) | 业务流程引擎(走 `springboot2-extend-flowable` 分支)、行为验证码(走 `springboot2-tianai-captcha` 分支)、多租户、SSO 联邦、Spring Boot 2 → 3/4 升级(走 `springboot3` / `springboot4` 分支) |

### 1.1 核心用户动作必做项

- 登录 / 注销(账号+密码+RSA 加密+图形验证码)
- 用户/角色/菜单/部门/岗位/字典 等基础数据维护
- 动态路由 + 按钮级权限(基于角色 + 权限字符串)
- 通知公告 + 文件上传下载
- 定时任务(Cron 表达式)在线管理
- 数据表反向生成 Controller/Service/Mapper/前端
- 锁屏 → 解锁
- PC 端 + 移动端 同一套后端接口
- **两端前端代码均通过 `vue-tsc` 类型检查**

---

## 2. 约束与质量属性

| 属性 | 目标 | 标注 |
|---|---|---|
| **JDK / Spring Boot** | 严格锁定 **JDK 11 + Spring Boot 2.5.14** | **最强约束**,与 RuoYi 3.8.2 对齐;升级 JDK 17/SB3 走独立分支 |
| **数据库** | PostgreSQL ≥ 15.1,所有对象落在 `basicdb` schema(34 张表) | 本分支**未改任何表结构**;JDBC URL 必须带 `currentSchema=basicdb` |
| **前端语言** | **全量 TypeScript**;`web-ui` 用 `typescript@5.6.3` + `vue-tsc@2.1.10` + `@vue/tsconfig@0.5.1`;`web-mp` 用 `typescript@^5.4.5` + `vue-tsc@^2.0.19` | **本分支唯一新增的强约束**;见 §3.6 |
| **TS 严格度** | `web-ui`:`strict: true`;`web-mp`:`strict: false` 起步(`noImplicitAny: false`) | 移动端 uni-app 类型定义不全,先宽松后收紧 |
| 鉴权 | JWT(无状态)+ BCrypt 密码散列 + 图形验证码 | Token 默认 30 分钟(`token.expireTime=30`) |
| 一致性 | 关系型事务 + 异步日志(`@Async` 落 `sys_oper_log` / `sys_logininfor`) | 操作日志失败不影响主链路 |
| 性能 | 列表接口走 MyBatis-Plus + PageHelper | TS 化**不影响运行时**(编译期擦除) |
| 审计 | 所有写操作通过 `LogAspect` 切面落 `sys_oper_log`,登录事件落 `sys_logininfor` | 不可关闭 |
| 安全 | XSS 过滤 + 重复提交拦截 + 限流 + CORS 显式白名单 | 与 `master` 一致 |
| 部署 | 单体 jar(可执行 war),`tomcat.max-threads=800` | 前端 bundle 体积与 `master` 基本持平 |
| 成本 | 单节点 PostgreSQL + 单实例 Redis + 单 jar,最低 2C4G 可起 | |

**最强约束**:① **JDK 11 + Spring Boot 2.5.14 不升级**;② **数据库全量统一在 `basicdb` schema 且本分支不改表**;③ **后端接口与 `master` 完全一致**(`/login` `/getInfo` `/getRouters` 等契约不变,前端 TS 化只是类型层);④ **前端不再新增 `.js` 源文件**(`web-ui/src/utils/generator/*.js` 6 个模板文件为唯一例外)。

---

## 3. 总体架构

### 3.1 容器视图(本分支改动以 **🔷** 标出)

```
┌───────────────────────────────────────────────────────────────┐
│  用户端                                                        │
│  ┌──────────────────────────┐  ┌────────────────────────────┐ │
│  │  web-ui (PC)             │  │  web-mp (移动:uni-app 3)    │ │
│  │  Vue3 + Element Plus     │  │  H5/小程序/Android/iOS      │ │
│  │  + Vite 6                │  │  uview-plus 3 + Pinia       │ │
│  │  🔷TypeScript 5.6.3      │  │  🔷TypeScript ^5.4.5        │ │
│  │  🔷vue-tsc 2.1.10(strict)│  │  🔷vue-tsc ^2.0.19          │ │
│  │  🔷src/types/ 23 个类型   │  │  🔷src/types/global.d.ts    │ │
│  └────────┬─────────────────┘  └──────────────┬─────────────┘ │
│           │ HTTPS / JSON                      │ HTTP/JSON      │
└───────────┼───────────────────────────────────┼───────────────┘
            ▼                                   ▼
┌───────────────────────────────────────────────────────────────┐
│  Spring Boot 2.5.14 (basic-admin-api)  ← 🔷 后端零修改         │
│  ┌─────────────────────────────────────────────────────────┐  │
│  │  Filter 链:CORS → JwtAuthenticationTokenFilter → ...    │  │
│  │  Controller (basic-admin-api / extend-*)                 │  │
│  │  ↓                                                       │  │
│  │  Service (basic-admin-core / extend-quartz / business)  │  │
│  │  ↓                                                       │  │
│  │  MyBatis-Plus 3.5.1 Mapper (XML/注解双轨)              │  │
│  │  ↓                                                       │  │
│  │  MyBatis-Plus PageHelper + Druid 连接池                 │  │
│  └────────────────────────┬────────────────────────────────┘  │
│  ┌────────────────────────┴────────────────────────────────┐  │
│  │ AOP 切面:LogAspect / DataScopeAspect / RateLimiter /    │  │
│  │          RepeatSubmit / DataSource(动态数据源)            │  │
│  └─────────────────────────────────────────────────────────┘  │
└──────────┬──────────────────────────────────┬──────────────────┘
           ▼                                  ▼
   ┌──────────────────┐              ┌──────────────────┐
   │ PostgreSQL ≥15.1 │              │ Redis(可选)      │
   │ schema=basicdb   │              │ 字典/Token/限流   │
   │ 34 张表 + 5 函数 │              │                  │
   │ 🔷 与 master 相同 │              │ 🔷 与 master 相同 │
   └──────────────────┘              └──────────────────┘
```

### 3.2 模块清单(7 个 Maven 子模块,与 `master` **完全一致**)

| 模块 | Maven artifact | 职责 | 关键要点 |
|---|---|---|---|
| `basic-admin-api` | `basic-admin-api` | Spring Boot 启动入口 + 全部 Controller | `BasicApplication` 扫描 `com.basic` + `com.core`,排除 `DataSourceAutoConfiguration`(由 `DruidConfig` 接管) |
| `basic-admin-core` | `basic-admin-core` | 业务核心:用户/角色/菜单/部门/字典/参数/通知/日志 等 Service+Mapper+Domain | 21 张 `sys_*` 表的全部 CRUD |
| `basic-framework` | `basic-framework` | 框架机制:Spring Security、Redis、MyBatis-Plus、Swagger、Filter、AOP、Token、限流 | 提供 starter 风格的能力,被 `basic-admin-api` 装配 |
| `basic-common` | `basic-common` | 通用工具/注解/枚举/基类:AjaxResult/R/Excel/XSS/Log/SecurityUtils 等 | 几乎所有模块都依赖 |
| `business-core` | `business-core` | 业务扩展点(目前是 `Demo` 示例) | 包前缀 `com.core`,与 `com.basic` 并列 |
| `extend-quartz` | `extend-quartz` | 定时任务:Quartz 集成 + Cron 在线管理 | 11 张 `qrtz_*` 表 |
| `extend-generator` | `extend-generator` | 代码生成:Velocity 模板 + 表结构反查 | 2 张 `gen_*` 表 |

> **🔷 本分支 7 个后端模块零修改**:`pom.xml`、Java 源码、`application*.yml` 与 `master` 逐字节一致。

### 3.3 数据模型核心表(详细见《数据库设计》)

> **🔷 本分支与 `master` 完全相同**:34 张表(`gen_*` 2 + `qrtz_*` 11 + `sys_*` 21),均在 `basicdb` schema。本分支**不新增、不删除、不修改任何表/字段/索引**。

| 域 | 表 | 说明 |
|---|---|---|
| 鉴权 | `sys_user` / `sys_role` / `sys_menu` / `sys_user_role` / `sys_role_menu` / `sys_role_dept` / `sys_user_post` | RBAC 主体 + 关联表 |
| 组织 | `sys_dept` / `sys_post` | 部门树 + 岗位 |
| 系统 | `sys_config` / `sys_dict_type` / `sys_dict_data` / `sys_files` / `sys_i18n` | 全局参数/字典/文件/国际化 |
| 通知 | `sys_notice` / `sys_notice_read` | 公告 + 已读 |
| 审计 | `sys_oper_log` / `sys_logininfor` | 操作/登录日志 |
| 示例 | `sys_demo` | `business-core` 示例表 |
| 定时 | `qrtz_*`(11 张) | Quartz 任务表 |
| 代码生成 | `gen_table` / `gen_table_column` | 表/字段元数据 |

### 3.4 关键流程(与 `master` 一致)

#### 登录流程(PC / 移动共用)

```
[前端 TS]                 [后端]                          [DB]
  │  GET /captchaImage      │                              │
  │ ─────────────────────▶  │ 生成 Kaptcha 验证码 + uuid    │
  │ ◀─────────────────────  │ {uuid, img}                  │
  │                         │                              │
  │  POST /login            │                              │
  │  {username,password,    │ RSA 解密 → BCrypt 比对        │
  │   code,uuid}            │ 通过 → TokenService 签发 JWT │
  │ ─────────────────────▶  │ ──────────────────────────▶  │
  │ ◀─────────────────────  │ {token}                      │
  │                         │                              │
  │  GET /getInfo           │ JwtFilter → LoginUser         │
  │ ─────────────────────▶  │ 权限/角色/路由元数据          │
  │  GET /getRouters        │ 动态路由树                   │
```

> PC 与移动端**完全复用**这套接口。**TS 化只在类型层**:`web-ui/src/api/login.ts` 把 `login()` 的返回值标注为 `Promise<LoginInfoResult>`,`web-mp/src/api/login.ts` 同理;运行时字节码与 `master` 等价。

#### 锁屏 / 解锁

```
PC 端 store/modules/lock.ts 维护 lock 状态
  触发:Header 操作菜单 → 锁定
  解锁:输入密码 → POST /login(同登录入口,SysLoginService 内部识别 reuse-token)
```

> 锁屏不是独立接口,直接复用 `SysLoginService.login(...)`;解锁时若已有 token 则只校验密码。**🔷 本分支 `lock.ts` 新增 `LockState` 接口类型。**

#### 定时任务调度

```
前端 TS → SysJobController.changeStatus / run / list
        ↓
SysJobServiceImpl → ScheduleUtils → Quartz Scheduler
        ↓
qrtz_* 表持久化 → JobInvokeUtil 反射调 @Component("ryTask").ryXxx()
```

### 3.5 状态机(代表性)

| 对象 | 状态 | 流转 |
|---|---|---|
| 定时任务 | `NORMAL` / `PAUSED` | changeStatus 切换 |
| 用户 | `0=正常` / `1=停用` | 新建=0,管理员停用=1 |
| 通用枚举 | `BusinessType` 增删改/导出/导入/强退/清空/生成代码 | 通过 `@Log(title, businessType = ...)` |
| 通知已读 | 未读 / 已读 | `sys_notice_read(user_id, notice_id)` 唯一 |
| **🔷 TS 编译产物** | `dev(ts 源码)` → `bundle(编译后)` | `vite build` 编译期擦除类型,产物无 `.ts` |

### 3.6 🔷 前端 TypeScript 工程结构(本分支核心新增)

#### 3.6.1 web-ui(PC 端,`strict: true`)

```
web-ui/
├── tsconfig.json                  🔷 新增(target esnext / moduleResolution bundler
│                                      / strict true / @/* → src/*
│                                      / types: [node, vite/client, element-plus/global]
│                                      / include: src/**/*.ts|vue, vite/**, vite.config.ts
│                                      / exclude: node_modules, dist, src/**/*.js)
├── auto-imports.d.ts              🔷 新增(unplugin-auto-import 生成)
├── vite.config.ts                 🔷 由 vite.config.js 改名
├── package.json                   🔷 devDependencies 增 typescript@5.6.3
│                                      / vue-tsc@2.1.10 / @vue/tsconfig@0.5.1
└── src/
    ├── main.ts                    🔷 (原 main.js)
    ├── permission.ts              🔷 (原 permission.js)
    ├── settings.ts                🔷 (原 settings.js)
    ├── api/                       🔷 19 个 .ts(login / menu / monitor.* / system.* / tool.gen)
    ├── types/                     🔷 23 个文件(本分支新增目录)
    │   ├── api/                   → common / login / menu / monitor.* / system.* / tool.gen
    │   ├── components.d.ts        → 全局组件类型声明
    │   ├── global.d.ts            → 全局变量/挂载方法声明
    │   └── index.ts               → 统一 re-export 出口
    ├── utils/                     🔷 13 个 .ts(auth / common / dict / dynamicTitle / errorCode
    │   │                              / index / jsencrypt / passwordRule / permission
    │   │                              / request / scroll-to / theme / validate)
    │   └── generator/             ⚠️ 6 个 .js 保留(config / css / drawingDefault / html / js / render)
    ├── store/                     🔷 8 个 .ts(index + modules/{app,dict,lock,permission,settings,tagsView,user})
    ├── plugins/                   🔷 6 个 .ts(auth / cache / download / index / modal / tab)
    ├── directive/                 🔷 4 个 .ts(index / common/copyText / permission/{hasPermi,hasRole})
    ├── router/index.ts            🔷 (原 router/index.js)
    ├── layout/components/index.ts 🔷 (原 index.js)
    ├── components/
    │   ├── IconSelect/requireIcons.ts  🔷
    │   └── SvgIcon/svgicon.ts          🔷
    └── views/**/*.vue             🔷 95 个 .vue 全部 `<script setup lang="ts">`
```

**统计口径**:`web-ui/src` 共 **79 个 `.ts` + 6 个 `.js` + 95 个 `.vue`**。

#### 3.6.2 web-mp(移动端,`strict: false` 起步)

```
web-mp/
├── tsconfig.json              🔷 新增(target ES2020 / moduleResolution Bundler
│                                  / strict false / noImplicitAny false
│                                  / isolatedModules true / types []
│                                  / include: src/**/*.ts|d.ts|tsx|vue, vite.config.ts
│                                  / exclude: node_modules, dist, unpackage, scripts)
├── vite.config.ts             🔷 由 vite.config.js 改名
├── sync-config.cjs            (保留 .cjs:Node 脚本,不参与类型检查)
├── package.json               🔷 devDependencies 增 typescript@^5.4.5
│                                  / vue-tsc@^2.0.19;新增 scripts.typecheck
└── src/
    ├── main.ts                🔷 (原 main.js)
    ├── permission.ts          🔷
    ├── config.ts              🔷
    ├── api/                   🔷 5 个(index / login / system/{dept,role,user})
    ├── store/                 🔷 4 个(index + modules/{app,permission,user})
    ├── utils/                 🔷 6 个(auth / config / dict / jsencrypt / request)
    ├── directive/permission.ts 🔷
    ├── types/global.d.ts      🔷
    └── pages/**/*.vue         🔷 全部 `<script setup lang="ts">`
```

**统计口径**:`web-mp/src` 共 **19 个 `.ts` + 0 个遗留 `.js`**。

#### 3.6.3 类型分层策略(本分支关键设计)

```
                   ┌────────────────────────────────┐
                   │  types/index.ts(统一出口)      │
                   └────────────┬───────────────────┘
                                │ re-export
      ┌─────────────────────────┼─────────────────────────┐
      ▼                         ▼                         ▼
┌──────────────┐        ┌──────────────┐         ┌─────────────────┐
│ types/api/   │        │ types/       │         │ types/global.d.ts│
│ common.ts    │        │ components   │         │ (全局挂载/          │
│ (R/AjaxResult│        │ .d.ts        │         │  window 扩展)     │
│  /TableData  │        │ (全局组件)    │         │                 │
│  Info/Page)  │        │              │         │                 │
└──────┬───────┘        └──────────────┘         └─────────────────┘
       │ 被 import
       ▼
┌──────────────────────────────────────────────────────────────┐
│  api/login.ts(函数签名带类型)                                │
│    export function login(...): Promise<LoginInfoResult>      │
│    export function getInfo(): Promise<UserInfoResult>         │
│    export function getCodeImg(): Promise<CaptchaInfoResult>   │
└──────────────────────┬───────────────────────────────────────┘
                       │ 被调用
                       ▼
┌──────────────────────────────────────────────────────────────┐
│  views/**/*.vue  <script setup lang="ts">  → ref<T>() / 类型提示 │
└──────────────────────────────────────────────────────────────┘
```

**规则**:
- `types/api/**` 只放**接口契约类型**(请求体 / 响应体),不放业务逻辑
- `types/components.d.ts` 由 `unplugin-vue-components` 生成(全局组件),**不手工改**
- `types/global.d.ts` 声明 `app.config.globalProperties` 上挂载的 `useDict` / `download` / `parseTime` 等
- `api/**` 的函数**必须显式标注返回类型**(如 `Promise<AjaxResult>`),禁止裸 `request()` 无类型
- 组件内 `<script setup lang="ts">`,禁止新增 `<script>`(无 lang)

---

## 4. 关键决策 ADR

| # | 决策点 | 采纳方案 | 备选 + 放弃原因 | 是否演进锁死 |
|---|---|---|---|---|
| ADR-01 | 基础平台底座 | RuoYi-Vue 3.8.2(零升级) | 自研框架:成本/成熟度不划算;直接升 SB 3.5:影响面大,放分支迭代 | **是** — 锁死 SB 2.5.14 |
| ADR-02 | 数据库 | PostgreSQL 15 + 统一 `basicdb` schema(34 表) | 继续 MySQL:失去生态对齐;分 schema:跨表 JOIN 麻烦 | **是** — 不再回到 MySQL |
| ADR-03 | 数据导入方式 | `pg_dump` 单脚本(`basicdb-vue3.sql`,`COPY ... FROM stdin`) | 一张表一个文件:运维体验差;`INSERT` 模式:文件 10× 大 | 否 |
| ADR-04 | 鉴权方案 | JWT + Spring Security + BCrypt | 传统 session:横向扩展成本高;OAuth2:目前无需求 | 否 |
| ADR-05 | ORM | MyBatis-Plus 3.5.1(注解 + XML 双轨) | JPA:与 RuoYi 生态偏离;纯 MyBatis:CRUD 模板代码多 | 否 |
| ADR-06 | 缓存 | 可选 Redis(默认不开时降级到本地) | 强制 Redis:增加本地开发成本 | 否 |
| ADR-07 | 前端双轨 | PC(`web-ui`)+ 移动(`web-mp`)并立 | 只做 PC:无法覆盖移动场景 | **是** |
| ADR-08 | 移动端 UI 库 | uview-plus 3.3.62(组件名带 `up-` 前缀) | Vant:非 uni-app 原生;uni-ui:组件少 | 否 |
| ADR-09 | 移动端路由策略 | 本地 `pages.json` 注册为主(动态路由仅预留) | 直接全动态:小程序/App 端反而复杂 | 否 |
| ADR-10 | 业务包命名 | `com.basic` + `com.core`(business) 双前缀 | 全 `com.basic`:无法区分平台/业务 | 否 |
| **ADR-11** | **前端语言** | **全量 TypeScript**(两端都换) | 保持 JS:无类型保护,重构风险高;只换 web-ui:两端不一致,维护割裂;JSDoc + checkJs:类型能力弱,不能定义 interface | **是** — 本分支主线,不再回退 JS |
| **ADR-12** | **类型定义位置** | **独立 `src/types/` 目录,按后端模块镜像分层**(`types/api/system/user.ts` 对应 `api/system/user.ts`) | 类型直接写在 `api/*.ts` 里:api 文件臃肿,跨模块复用困难;类型放 `@types/`:与源码分离,跳转不便 | **是** |
| **ADR-13** | **严格度分级** | `web-ui`:`strict: true`(完整严格);`web-mp`:`strict: false` + `noImplicitAny: false` 起步 | 两端都 `strict: true`:uni-app 3 alpha 类型定义不全,一次到位会有大量 `any` 断言/`@ts-ignore`,反而失去意义;两端都宽松:PC 端失去类型价值 | 否 — web-mp 后续可收紧 |
| **ADR-14** | **`.vue` 单文件组件** | `<script setup lang="ts">`(组合式 API + TS) | Options API + `defineComponent`:样板代码多;混用 JS/TS:类型断裂 | **是** |
| **ADR-15** | **代码生成器模板文件** | `web-ui/src/utils/generator/*.js` **保留 JS**(6 个) | 改 TS:这些文件用大量字符串拼接生成 HTML/Vue 代码,类型化收益极低且易引入 bug;`tsconfig.json` 用 `exclude: ["src/**/*.js"]` 隔离 | 否 — 可与主源码共存 |
| **ADR-16** | **类型检查时机** | **不阻塞 `vite dev` / `vite build`**;`vue-tsc` 通过 `typecheck` 脚本独立执行 | 构建时强校验:老代码迁移期会阻塞开发;完全不做:失去 TS 意义 | 否 — 后续可并入 CI |
| **ADR-17** | **自动导入的类型化** | `unplugin-auto-import` 生成 `auto-imports.d.ts`;`tsconfig.json` 的 `include` 显式纳入 | 手工维护:易漂移 | 否 |
| **ADR-18** | **全局挂载方法类型** | `types/global.d.ts` 里 `declare module 'vue' { interface ComponentCustomProperties { ... } }` | 每个组件里 `@ts-ignore`:污染源码 | 否 |

---

## 5. 里程碑

| 阶段 | 内容 | 验收 | 触发升级的信号 |
|---|---|---|---|
| **M0(已交付,`master`)** | RuoYi 3.8.2 基础 + SQL 迁 PostgreSQL + 锁屏解锁 | mvn compile 0 ERROR;7 模块 parent.version=3.8.2;`basicdb-vue3.sql` 可恢复 34 表 | — |
| **M1(已交付,`master`)** | 新增 web-mp(uni-app 3 + uview-plus 3) | H5/小程序/App 三端可登录 | — |
| **M2(已交付,本分支)** | **前端全量 TypeScript 化**:`web-ui` 79 个 `.ts` + `web-mp` 19 个 `.ts`;两端 `tsconfig.json`;`web-ui/src/types/` 23 个类型文件;两端 `vite.config.ts`;`vue-tsc` 工具链 | `pnpm typecheck` 通过;`pnpm dev` / `pnpm dev:h5` 可启动;登录/CRUD 全链路回归通过 | — |
| **M3(下一轮)** | 移动端:动态菜单 / 国际化 / WebSocket 推送 / 锁屏 / 个人中心编辑 | 见 web-mp README §7.6 待办清单 | 业务方在移动端提出超过 5 个角色/菜单变更需求时排期 |
| **M4(可选)** | **`web-mp` 严格度收紧**:`strict: false` → `strict: true`(补全 uni-app 类型定义) | `vue-tsc --noEmit` 0 error | uni-app 类型定义完善 / 团队 TS 熟练度提升 |
| **M5(可选)** | **`vue-tsc` 并入 CI**:提交前强校验 + `lint-staged` | pre-commit 阻断类型错误 | 团队规模扩大,需要硬门禁 |
| **M6(分支)** | SSO:接入 keycloak 联邦 | 账号互通 | 出现第二个业务系统需统一登录 |
| **M7(分支)** | Spring Boot 3.5 + JDK 17 + Jakarta | 单独分支 `springboot3` / `springboot3_TypeScript` | 公司层面升级 JDK 基线 |
| **M8(分支)** | 流程引擎 / 行为验证码 | 单独分支 `springboot2-extend-flowable` / `springboot2-tianai-captcha` | 出现审批流需求 / 需要更强验证码 |

**本次不做,直到有信号**:微服务拆分、K8s 化、灰度发布、APM 探针、消息中心抽象。

---

## 6. 审查与遗留风险

### 6.1 已覆盖(失败设计 / 幂等 / 权限 / 一致性 / 可观测)

| 维度 | 设计 |
|---|---|
| 失败设计 | 统一 `GlobalExceptionHandler` + `R/AjaxResult` 包装;`ServiceException` / `UserPasswordNotMatchException` / `CaptchaException` 等业务异常基类 |
| 幂等 | `@RepeatSubmit` 注解 + `RepeatSubmitInterceptor`(`SameUrlDataInterceptor` 实现) |
| 权限 | 菜单级 + 按钮级(`@PreAuthorize("@ss.hasPermi('system:user:list')")`)+ 数据级(`@DataScope` + `DataScopeAspect`) |
| 一致性 | 关系型事务边界下沉到 Service;异步日志通过 `AsyncManager` 隔离失败 |
| 可观测 | `sys_oper_log` / `sys_logininfor` / Swagger(springfox 3.0)/ Druid 监控 / `actuator/shutdown` |
| **🔷 类型安全** | 两端 `tsconfig.json` + `vue-tsc` 独立检查;`types/` 分层;`api` 函数显式返回类型;`global.d.ts` 声明全局挂载 |

### 6.2 风险清单

| # | 风险 | 影响 | 缓解 |
|---|---|---|---|
| R1 | Spring Security 5.5 已 EOL,无安全补丁 | 高 | 走 `springboot3` 分支,网关层补 WAF;**本分支锁死版本不动** |
| R2 | springfox 3.0 与 spring 5.3 兼容性问题 | 中 | 已在 `application.yml` 关闭 / 视情况切 springdoc |
| R3 | `web-mp` 使用 `uni-app 3.0 (alpha)`,DCloud 官方尚未 GA | 中 | 锁版本 `@dcloudio/uni-app@3.0.0-4060620250520001` |
| R4 | `file-sql/` 多份历史脚本混在一起 | 低 | README 已注明:运行只用 `basicdb-vue3.sql` |
| R5 | `com.basic` + `com.core` 双包前缀,新人易混 | 低 | 文档明确:`com.basic`=平台,`com.core`=业务 |
| R6 | `springboot2-extend-flowable` 流程分支与 `master` 已分叉 | 中 | 各自维护;不强行合并 |
| R7 | 操作日志若 DB 不可用,主链路要不要回滚? | 低 | `AsyncManager` 内部 `try/catch` |
| R8 | 移动端无国际化文案 | 低 | vue-i18n 已引入,词条待补 |
| R9 | 锁屏接口复用 `/login` — 移动端未实现 | 低 | web-mp 暂只做登录,待办已挂 |
| R10 | Token 30min 过期,移动端无 refresh 机制 | 中 | 待办:`utils/request.ts` 加 401 自动重登 |
| **R11** | **`vue-tsc` 不阻塞构建,类型错误可能进生产** | **中** | ADR-16 明确;M5 计划并入 CI;当前靠开发者本地 `pnpm typecheck` 自觉 |
| **R12** | **`web-mp` `strict: false`,类型保护弱于 PC 端** | **中** | ADR-13 已知取舍;M4 计划收紧;uni-app alpha 类型缺失是主因 |
| **R13** | **第三方库类型缺失**(uview-plus / unplugin-vue-setup-extend-plus 等) | 中 | `types/global.d.ts` 补 `declare module`;`skipLibCheck: true` 已开 |
| **R14** | **`utils/generator/*.js` 6 个 JS 与 TS 源码混存,易被误改** | 低 | ADR-15 + `tsconfig` `exclude`;README §三 表格显式列出 |
| **R15** | **TS 迁移后 `.js` 旧文件删除不彻底,留下双份源码** | 低 | 已核验:`web-ui/src` 仅 `generator/` 6 个 `.js`,`web-mp/src` 0 个 `.js`;新增检查项纳入 checklist |
| **R16** | **`@vue/tsconfig@0.5.1` 与 `typescript@5.6.3` 版本漂移** | 低 | 锁精确版本(非 `^`);升级需同步验证 |
| **R17** | **`isolatedModules: true`(web-mp)要求类型导入用 `import type`** | 低 | 已在类型文件统一 `import type { ... }`;新人需注意 |
| **R18** | **代码生成器生成的 `.vue` 模板可能仍产出 JS 风格** | 中 | `extend-generator` 的 Velocity 模板在后端(本分支未改);生成的前端代码需手工 ts 化,已列入待办 |

---

## 7. 参考来源

- `README.md`(仓库根,本分支)
- 同主题基线文档:`docs/base-postgresql-vue3-架构方案.md`(在 `master` 分支生成,描述 `master` 自身)— 本文件以 `master` 为基线,**只描述本分支(`springboot2_TypeScript`)的差异点**:前端全量 TypeScript 化
- 兄弟分支:
  - `springboot2-extend-flowable`(Flowable 6.8.0 集成,`basicdb_flowable` schema)
  - `springboot2-tianai-captcha`(行为验证码替换 kaptcha)
  - `springboot3` / `springboot3_TypeScript` / `springboot4` / `springboot4_TypeScript`(后端升级)
- TypeScript 5.6 / vue-tsc 2.1 / `@vue/tsconfig` 0.5 官方文档
- 模板/案例参考:awesome-architecture 教程、模板与案例

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `master` 架构方案为基线,新增「§3.6 前端 TypeScript 工程结构(两端 tsconfig / 类型分层 / 文件统计)」+ ADR-11~ADR-18 共 8 条 TS 决策 + R11~R18 共 8 条 TS 风险 + M2 TS 里程碑 |
