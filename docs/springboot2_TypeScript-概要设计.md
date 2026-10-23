# 基础平台(base-postgresql-vue3) 概要设计(HLD) — `springboot2_TypeScript` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot2_TypeScript`** 分支
> 前置依赖:《架构方案》v0.1、《需求文档》v0.1
> 本分支相对 `master` 的差异仅在**前端 TypeScript 工程结构**;后端逻辑架构、核心流程、数据设计、接口清单、部署视图与 `master` **完全一致**,本文档不重复,只新增 TS 专题章节(§2.4 / §3.6 / §6 HD-09~HD-15 / §8 P8~P13)。

---

## 1. 设计目标与约束回顾

| 来源 | 关键约束 |
|---|---|
| 架构方案 §2 | JDK 11 + Spring Boot 2.5.14 锁死,数据库 PostgreSQL ≥ 15,统一 `basicdb` schema(34 表) |
| 架构方案 §2 | **前端全量 TypeScript**;`web-ui` `strict: true`,`web-mp` `strict: false` 起步;`vue-tsc` 检查 |
| 架构方案 §2 | JWT 30min + BCrypt;XSS + 重复提交 + 限流;操作/登录日志不可关闭 |
| 需求文档 §7 | 列表 1000 条 < 1s;登录 < 500ms;单一可执行 jar;TS 运行时零开销 |
| 需求文档 §10 | AC-01~AC-22(其中 AC-15~AC-22 为 TS 专项) |

**勿重复大段正文**,仅作引子。

---

## 2. 逻辑架构与模块划分

### 2.1 工程结构(本分支改动以 **🔷** 标出)

```
base-postgresql-vue3/
├── pom.xml                 # 父 pom,7 子模块(🔷 与 master 一致,未改)
├── basic-admin-api/        # 启动入口 + 全部 Controller(🔷 未改)
├── basic-admin-core/       # 业务核心(sys_*/gen_*/qrtz_*/demo)(🔷 未改)
├── basic-framework/        # 框架能力(Security/Redis/MyBatis/AOP/Filter)(🔷 未改)
├── basic-common/           # 工具/注解/枚举/基类(🔷 未改)
├── business-core/          # 业务扩展点(com.core 包)(🔷 未改)
├── extend-quartz/          # Quartz 集成(🔷 未改)
├── extend-generator/       # Velocity 代码生成(🔷 未改)
├── file-sql/               # 8 个 SQL(basicdb-vue3.sql 为唯一运行入口)(🔷 未改)
├── file-bin/  file-doc/    # 静态资源
├── web-ui/                 # PC(Vue3 + ElementPlus)🔷 TypeScript
│   ├── tsconfig.json       🔷 新增(strict: true)
│   ├── vite.config.ts      🔷 由 .js 改名
│   └── src/**               🔷 79 个 .ts + 6 个 .js(generator 模板)
└── web-mp/                 # 移动(uni-app3 + uview-plus3)🔷 TypeScript
    ├── tsconfig.json       🔷 新增(strict: false)
    ├── vite.config.ts      🔷 由 .js 改名
    └── src/**               🔷 19 个 .ts + 0 个 .js
```

### 2.2 后端组件职责(与 `master` 一致)

| 模块 | 关键类 | 职责 |
|---|---|---|
| `basic-admin-api` | `BasicApplication` | 启动类,扫描 `com.basic` + `com.core`;`@MapperScan("com.core.**.mapper")` |
| | `BasicServletInitializer` | 外部 Tomcat war 启动适配 |
| | `com.basic.web.controller.**` | 26 个 Controller |
| `basic-admin-core` | `com.basic.system.service.impl.**` | 业务 Service 实现 |
| | `com.basic.system.mapper.**` | MyBatis-Plus Mapper 接口 |
| | `com.basic.system.domain.**` | 实体 + VO |
| `basic-framework` | `SecurityConfig` | Spring Security 配置(JWT 无状态、BCrypt) |
| | `JwtAuthenticationTokenFilter` | 解析 token → SecurityContext |
| | `MybatisPlusConfig` / `MyBatisConfig` | MyBatis-Plus 分页、Mapper 扫描 |
| | `DataScopeAspect` / `LogAspect` / `RateLimiterAspect` | 数据权限/操作日志/限流切面 |
| | `RepeatSubmitInterceptor` | 重复提交拦截 |
| | `SysLoginService` / `TokenService` | 登录 + Token |
| `basic-common` | `R` / `AjaxResult` / `TableDataInfo` | 统一返回结构 |
| | `BaseEntity` / `TreeEntity` | 实体基类 |
| | `SecurityUtils` / `StringUtils` / `DateUtils` | 通用工具 |
| | `@Log` / `@RepeatSubmit` / `@RateLimiter` / `@DataScope` / `@Anonymous` | 业务注解 |
| `extend-quartz` | `ScheduleConfig` / `ScheduleUtils` / `AbstractQuartzJob` | Quartz 集成 |
| `extend-generator` | `GenController` / `VelocityUtils` | 代码生成 |
| `business-core` | `Demo` / `DemoController` | 业务示例 |

### 2.3 扩展 SPI(与 `master` 一致)

| SPI | 接入方式 | 用例 |
|---|---|---|
| 自定义 Job | 实现 `@Component("ryTask")` + `public void ryXxx(...)` | `ryTask.ryNoParams` / `ryParams` |
| 自定义权限注解 | 沿用 `@PreAuthorize("@ss.hasPermi('...')")` | `SysPermissionService.hasPermi` |
| 字典加载 | 在 `DictUtils` 增枚举/缓存 key | — |
| 数据源 | 注解 `@DataSource(DataSourceType.SLAVE)` | 多数据源 |
| 代码生成模板 | 新增 `vm/java/...` 模板 | — |
| **🔷 前端类型扩展** | 在 `web-ui/src/types/api/**` 新增 interface,并在 `types/api/index.ts` re-export | 后端新增接口时配套 |

### 2.4 🔷 前端 TypeScript 组件职责(本分支新增)

#### 2.4.1 web-ui(PC)

| 分层 | 路径 | 数量 | 职责 | 约束 |
|---|---|---|---|---|
| 类型契约 | `src/types/api/**` | 19 | 接口请求/响应 interface | 镜像后端模块路径;禁业务逻辑 |
| 类型出口 | `src/types/api/index.ts` | 1 | 统一 re-export | 新增类型必须登记 |
| 全局组件类型 | `src/types/components.d.ts` | 1 | 全局组件声明 | **自动生成,禁止手工改** |
| 全局声明 | `src/types/global.d.ts` | 1 | `window` / `ComponentCustomProperties` | 手写 |
| 类型入口 | `src/types/index.ts` | 1 | 对外统一入口 | — |
| API 层 | `src/api/**` | 19 | HTTP 调用,显式返回类型 | 必须 `Promise<XxxResult>` |
| 工具层 | `src/utils/**` | 13 `.ts` + 6 `.js` | 通用工具 | `generator/` 保留 JS(ADR-15) |
| 状态层 | `src/store/**` | 8 | Pinia store(含 `lock.ts`) | `defineStore` + 泛型 |
| 插件层 | `src/plugins/**` | 6 | `$tab` / `$modal` / `$auth` / `$cache` / `$download` | 挂载方法须在 `global.d.ts` 声明 |
| 指令层 | `src/directive/**` | 4 | `v-hasPermi` / `v-hasRole` / `v-copyText` | — |
| 路由 | `src/router/index.ts` | 1 | 动态路由 | `RouteRecordRaw` |
| 视图 | `src/views/**/*.vue` | 95 | 页面 | `<script setup lang="ts">` |

**总量**:79 个 `.ts` + 6 个 `.js` + 95 个 `.vue`。

#### 2.4.2 web-mp(移动)

| 分层 | 路径 | 数量 | 职责 |
|---|---|---|---|
| API 层 | `src/api/**` | 5 | `index` / `login` / `system/{dept,role,user}` |
| 状态层 | `src/store/**` | 4 | `index` + `modules/{app,permission,user}` |
| 工具层 | `src/utils/**` | 5 | `auth` / `config` / `dict` / `jsencrypt` / `request` |
| 指令 | `src/directive/permission.ts` | 1 | `v-hasPermi` |
| 全局声明 | `src/types/global.d.ts` | 1 | uni-app 全局扩展 |
| 入口 | `src/main.ts` / `src/permission.ts` / `src/config.ts` | 3 | 启动 / 路由守卫 / 配置 |
| 页面 | `src/pages/**/*.vue` | — | `<script setup lang="ts">` |

**总量**:19 个 `.ts` + 0 个 `.js`。

#### 2.4.3 两端 tsconfig 差异对照

| 配置项 | `web-ui` | `web-mp` | 说明 |
|---|---|---|---|
| `target` | `esnext` | `ES2020` | mp 端受小程序运行时限制 |
| `module` | `esnext` | `ESNext` | — |
| `moduleResolution` | `bundler` | `Bundler` | — |
| `strict` | **`true`** | **`false`** | ADR-13:mp 端类型定义不全 |
| `noImplicitAny` | 继承 `strict` = true | **`false`** | 同上 |
| `isolatedModules` | 未设 | **`true`** | 要求 `import type` |
| `skipLibCheck` | `true` | `true` | 第三方库类型不全时不阻塞 |
| `allowJs` | `true` | 未设 | web-ui 允许 `.js` 共存 |
| `noEmit` | `true` | 未设(由 `vue-tsc --noEmit` 控制) | — |
| `types` | `["node","vite/client","element-plus/global"]` | `[]` | — |
| `include` | `src/**/*.ts` / `src/**/*.vue` / `vite/**/*.ts` / `auto-imports.d.ts` / `vite.config.ts` | `src/**/*.ts` / `src/**/*.d.ts` / `src/**/*.tsx` / `src/**/*.vue` / `vite.config.ts` | — |
| `exclude` | `node_modules` / `dist` / **`src/**/*.js`** | `node_modules` / `dist` / `unpackage` / `scripts` | web-ui 靠 `exclude` 隔离 generator |
| `paths` | `"@/*": ["src/*"]` | `"@/*": ["src/*"]` | 两端一致 |
| 基础配置继承 | `@vue/tsconfig@0.5.1` | 无(全部内联) | web-ui 通过 `@vue/tsconfig` 简化 |

#### 2.4.4 类型检查执行链

```
开发者本地
   │
   ├─ web-ui: npx vue-tsc --noEmit      (package.json 无 typecheck 脚本,见 A-8)
   │
   └─ web-mp: pnpm typecheck            (package.json: "typecheck": "vue-tsc --noEmit")
         │
         ▼
   读 tsconfig.json → 收集 include → 解析 .ts / .vue(SFC)
         │
         ├─ 解析 @/types/** 契约
         ├─ 解析 @vue/tsconfig 基础规则(仅 web-ui)
         └─ 跳过 node_modules 类型(skipLibCheck)
         │
         ▼
   输出类型错误到终端(不产出文件)
         │
         ▼
   ⚠️ 不阻塞 vite build(ADR-16);构建只跑 vite build
```

---

## 3. 核心流程设计

### 3.1 登录流程(与 `master` 一致,TS 标注为 🔷)

**状态机(单次登录会话)**

```
[匿名]
   │  GET /captchaImage
   ▼
[验证码已获取] ──(过期/未通过)──▶ [失败]
   │  POST /login(账号/密文密码/验证码/uuid)
   ▼
[校验中]
   │  通过        │ 失败
   ▼              ▼
[签发 Token]   [失败(原因落 sys_logininfor)]
   │
   ▼
[已登录] ── 30min 过期 ──▶ [匿名]
   │  POST /logout
   ▼
[已注销(清除 SecurityContext + 删除 redis cache)]
```

**时序(ASCII,前端侧类型标注为 🔷)**

```
[PC/Mobile]        [Controller]      [SysLoginService]      [Redis/DB]
   │  getCodeImg(): Promise<CaptchaInfoResult>        🔷
   │ ────────────────▶  CaptchaController
   │                                 │ 生成 Kaptcha(uuid)
   │ ◀──────────────── {uuid,img}    │
   │                                 │
   │  login(u,p,code,uuid): Promise<LoginInfoResult>  🔷
   │ ────────────────▶  SysLoginController
   │                       │  login(LoginBody)
   │                       │ ────────▶  ① RSA 解密
   │                       │         │  ② checkCaptcha(uuid,code)
   │                       │         │  ③ loadUserByUsername
   │                       │         │  ④ BCrypt.matches
   │                       │         │  ⑤ TokenService.createToken
   │                       │         │  ⑥ AsyncManager.execute
   │ ◀──────────────── {token}        │  写 sys_logininfor
```

> 🔷 **TS 化不改调用时序**,只在 `api/login.ts` 处加返回类型。运行时字节码等价。

### 3.2 锁屏/解锁(与 `master` 一致)

```
PC store/modules/lock.ts 触发 lock 状态 → 锁屏页
   │
   ▼ 提交 unlockPassword
   │  POST /login(reuse token, only password)
   ▼
SysLoginService.login: 短路验证码检查;校验密码;
   成功 → 返回原 token;失败 → UserPasswordNotMatchException
```

> 🔷 `lock.ts` 新增 `LockState` interface;`unlockScreen(password: string)` 显式返回类型。

### 3.3 定时任务调度(与 `master` 一致)

```
新增任务:
  SysJobController.add → SysJobServiceImpl.add
    → ScheduleUtils.createScheduleJob → Quartz Scheduler
      → 写 qrtz_* 持久化

触发:
  Cron 到点 → AbstractQuartzJob.execute
    → JobInvokeUtil.invokeMethod(ryTask.ryXxx)
      → try/catch → sys_job_log
```

### 3.4 代码生成(与 `master` 一致)

```
导入表 → GenTableServiceImpl.importGenTable
  → GenTableColumnServiceImpl.importGenTableCol
  → 写 gen_table / gen_table_column

生成:
  download(genTableIds) →
    VelocityEngine 渲染 .vm 模板(java/Mapper/Service/Controller/Vue/sql/js)
    → 打包 zip → 浏览器下载
```

> ⚠️ **🔷 本分支已知缺口(R18)**:`extend-generator` 的 Velocity 模板在后端,**本分支未改**,生成的 `.vue` 仍是 JS 风格(`<script setup>` 无 `lang="ts"`),需开发者手工 ts 化。

### 3.5 移动端登录(与 `master` 一致)

```
web-mp/pages/login/index.vue
  getCodeImg(): Promise<CaptchaInfoResult>   🔷
  login(): Promise<LoginInfoResult>          🔷
  ↓  Basic-Token 存到 uni.storage
getInfo()                                    🔷
getRouters()   (本分支先消费即可,后续转 pages.json)
```

### 3.6 🔷 TypeScript 开发与迁移流程(本分支新增)

#### 3.6.1 新增功能的标准流程(强制)

```
① 后端新增/变更接口
      │
      ▼
② web-ui/src/types/api/<模块>/<实体>.ts 定义 interface
      │  （镜像后端 DTO/VO 字段）
      ▼
③ web-ui/src/types/api/index.ts 登记 re-export
      │
      ▼
④ web-ui/src/api/<模块>/<实体>.ts 写函数
      │  export function xxx(query: Q): Promise<ListResult<T>>
      │  ⚠️ 必须显式标注返回类型
      ▼
⑤ views/**/*.vue 用 <script setup lang="ts">
      │  const list = ref<T[]>([])
      ▼
⑥ 本地 npx vue-tsc --noEmit → 0 error
      │
      ▼
⑦ 提 PR
```

#### 3.6.2 后端字段变更的传导路径(TS 化核心价值)

```
后端 DB 字段改名
   │
   ▼
后端 DTO/VO 改名（人工）
   │
   ▼
⚠️ types/api/**/*.ts 同步改名（人工，唯一手动环节）
   │
   ▼
vue-tsc 扫描
   │
   ├─ api/*.ts 函数签名不匹配 → 报错
   ├─ views/*.vue 引用旧字段 → 报错
   └─ store/*.ts 引用旧字段 → 报错
   │
   ▼
逐个修复 → 0 error → 提交
```

> **关键**:`types/` 是前后端契约的"唯一锚点"。若类型文件忘记同步,**TS 无法自动发现**(因为类型自身仍自洽)。因此该环节必须列入 code review checklist。

#### 3.6.3 迁移期的一次性流程(已完成,记录备查)

```
步骤 1: package.json 加 typescript / vue-tsc / @vue/tsconfig
步骤 2: 新建 tsconfig.json(strict 按端分级)
步骤 3: vite.config.js → vite.config.ts
步骤 4: src/**/*.js → src/**/*.ts(逐个改后缀 + 补类型)
步骤 5: 新建 src/types/** 目录，抽取 interface
步骤 6: api/*.ts 逐个加显式返回类型
步骤 7: .vue 批量改 <script setup lang="ts">
步骤 8: 处理 typecheck 报错（补 declare module / 类型断言）
步骤 9: 删除遗留 .js（除 web-ui/src/utils/generator/ 6 个）
步骤 10: npx vue-tsc --noEmit 归零
```

---

## 4. 数据设计(概要)

### 4.1 Schema 布局(与 `master` 完全一致)

| schema | 表数 | 内容 |
|---|---|---|
| `basicdb` | **34** | 全部业务表(下分 `gen_*` 2 / `qrtz_*` 11 / `sys_*` 21) |
| `public` | 0(本项目不使用) | — |

> 🔷 **本分支不改任何表结构、不新增表、不新增字段、不改索引**。

### 4.2 核心表(ER 简化,与 `master` 一致)

```
sys_user ──┬── sys_user_role ── sys_role
           │                      │
           │                      ├── sys_role_menu ── sys_menu
           │                      └── sys_role_dept ── sys_dept
           ├── sys_user_post ── sys_post
           └── dept ── dept (自引用,parent_id)

sys_dict_type 1 ── n sys_dict_data
sys_config(单 KV)
sys_files(单表)
sys_notice ── sys_notice_read ── sys_user
sys_oper_log, sys_logininfor(单表流水)
gen_table 1 ── n gen_table_column
qrtz_*(Quartz 内部)
```

### 4.3 关键索引/查询(与 `master` 一致)

| 关键查询 | 命中索引 |
|---|---|
| `select * from sys_user where user_name = ?` | `uk_user_name`(逻辑唯一) |
| `select * from sys_role where role_key = ? and del_flag=0` | `uk_role_key` |
| `select * from sys_menu where status=0 and perms is not null` | 全表扫描(菜单量小) |
| `select * from sys_user_role where user_id=?` | `(user_id, role_id)` 复合 |
| `select * from sys_role_menu where role_id=?` | `(role_id, menu_id)` 复合 |
| `select * from qrtz_triggers where TRIGGER_GROUP=?` | Quartz 自带索引 |

---

## 5. 接口清单(与 `master` 完全一致)

> 🔷 **本分支 HTTP 接口零变更**:路径、方法、入参、出参、权限字符全部与 `master` 一致。TS 化只在前端加类型标注,不新增/不删除/不修改任何接口。
>
> 完整清单见 `docs/base-postgresql-vue3-概要设计.md` §5(`master` 分支),此处仅保留分类索引:
>
> - **匿名**:`GET /captchaImage` / `POST /login` / `POST /register` / `POST /monitor/shutdown`
> - **已登录**:`POST /logout` / `GET /getInfo` / `GET /getRouters` / `GET /system/dict/type/{dictType}` / `GET /common/download` / `POST /common/upload`
> - **系统管理**:`/system/{user,role,menu,dept,post,config,notice,dict/*}` 各 list/add/edit/remove/export
> - **监控**:`/monitor/{operlog,logininfor,online,job,jobLog,server,cache,druid}`
> - **工具**:`/tool/gen/{list,importTable,download,synchDb,preview}`
> - **Swagger**:`GET /swagger-ui.html`

### 5.1 🔷 新增的"类型契约清单"(非 HTTP 接口,但属于本分支交付物)

| 类型文件 | 对应后端 | 状态 |
|---|---|---|
| `types/api/common.ts` | `R` / `AjaxResult` / `TableDataInfo` / `PageQuery` | ✅ |
| `types/api/login.ts` | `LoginBody` / `LoginInfoResult` / `UserInfoResult` / `CaptchaInfoResult` | ✅ |
| `types/api/menu.ts` | `RouterVo` / `MetaVo` | ✅ |
| `types/api/monitor/{cache,job,jobLog,logininfor,online,operlog,server}.ts` | 监控 7 模块 | ✅ |
| `types/api/system/{config,dept,dict,menu,notice,post,role,user}.ts` | 系统 8 模块 | ✅ |
| `types/api/tool/gen.ts` | `GenTable` / `GenTableColumn` | ✅ |
| `types/api/index.ts` | 统一 re-export | ✅ |
| `types/components.d.ts` | 全局组件(自动生成) | ✅ |
| `types/global.d.ts` | `window` / `ComponentCustomProperties` | ✅ |
| `types/index.ts` | 对外入口 | ✅ |

---

## 6. 关键技术决策(HD-)

| # | 决策 | 理由 | 影响 |
|---|---|---|---|
| HD-01 | Spring Security `STATELESS`,基于 JWT | 无状态横向扩展;避免 CSRF | Token 失效前无法强制下线(已用 Redis 缓解) |
| HD-02 | 异步日志走 `AsyncManager` + 独立线程池 | 不污染主链路 | 日志丢失不会回滚主事务 |
| HD-03 | MyBatis-Plus + 自定义 XML 双轨 | 通用 CRUD 用 MP,复杂 SQL 走 XML | 维护两套 |
| HD-04 | 数据权限通过切面 + SQL 拼接 | 透明、对业务代码侵入小 | 必须确保 SQL 不要把 `where` 拼错 |
| HD-05 | 限流走 `RateLimiter` + Redis(令牌桶) | 单机也能用 Guava RateLimiter 兜底 | — |
| HD-06 | Token 30min + Redis 缓存 | 主动失效可实现强退 | Redis 不可用时降级到仅 JWT |
| HD-07 | 移动端使用 uni-app `up-` 组件统一注册 | 显式注册避免 H5/小程序差异 | `main.ts` 维护一份注册表 |
| HD-08 | 移动端 `pages.json` 静态优先 | 小程序/原生端必须存在 | 动态路由留迭代 |
| **HD-09** | **两端独立 `tsconfig.json`,不共享** | web-ui 跑浏览器(Vite 6 / `esnext`),web-mp 跑 uni-app(Vite 5 / `ES2020`);共享会引入 `types` 冲突(`node` vs `[]`) | 两份配置需分别维护,升级 TS 时各自验证 |
| **HD-10** | **类型集中在 `src/types/`,按后端模块镜像** | `api/*.ts` 保持精简;跨模块复用(如 `ListResult<T>`)方便;跳转定位清晰 | 需在 `types/api/index.ts` 统一登记,漏登记会导致导入失败 |
| **HD-11** | **`strict` 按端分级(web-ui true / web-mp false)** | uni-app 3 alpha 类型定义不全,一次性 strict 会引入大量 `@ts-ignore`,失去意义 | mp 端类型保护弱;M4 收紧 |
| **HD-12** | **类型检查不阻塞构建(独立 `typecheck`)** | 迁移期强校验阻塞开发;Vite 打包不需要类型信息 | 类型错误可能进生产(R11);M5 并入 CI |
| **HD-13** | **`utils/generator/*.js` 保留 JS** | 代码生成器模板用大量字符串拼接生成 HTML/Vue 代码,类型化收益极低;改 TS 易引 bug | 与 TS 源码混存(R14);靠 `tsconfig.exclude` 隔离 |
| **HD-14** | **`<script setup lang="ts">` 强制** | 组合式 API 类型推导最好;Options API 的 `this` 类型推断在复杂场景不可靠 | 存量 Options API 组件需重构(本分支已全量迁移) |
| **HD-15** | **全局挂载方法用 `ComponentCustomProperties` 声明** | `app.config.globalProperties.useDict` 等在模板里要用,不声明会报错 | `types/global.d.ts` 需与 `main.ts` 挂载清单同步维护 |

---

## 7. 部署视图(与 `master` 一致,前端构建标注为 🔷)

```
        ┌────────────────────────────┐
        │  Nginx(可选)              │
        │  /api/*  → 8080           │
        │  /        → web-ui dist   │  🔷 dist 由 vite build 从 TS 源码产出
        └─────────────┬──────────────┘
                      │
                      ▼
        ┌────────────────────────────┐
        │  basic-admin-api jar      │
        │  端口:8080                │
        │  profile: dev / prod      │
        │  🔷 与 master 相同        │
        └──────┬──────────────┬─────┘
               │              │
               ▼              ▼
        ┌────────────┐   ┌────────────┐
        │PostgreSQL  │   │  Redis     │
        │15+(basicdb)│   │ (可选)     │
        │34 表       │   │            │
        └────────────┘   └────────────┘

        web-mp:  🔷 src 为 TS,编译产物同 master
        ┌────────────────────────────┐
        │  HBuilder / 微信开发者工具  │
        │  编译产物:dist/dev/mp-weixin│
        │  🔷 类型在编译期擦除        │
        └────────────────────────────┘
```

### 7.1 启动顺序

1. PostgreSQL 启动 + `psql -f basicdb-vue3.sql` 恢复库(34 表)
2. (可选)Redis 启动
3. `mvn spring-boot:run -pl basic-admin-api` 启动后端
4. **🔷 (可选)类型检查**:`cd web-ui && npx vue-tsc --noEmit`;**`cd web-mp && pnpm typecheck`**
5. `pnpm dev` 启动 web-ui;`pnpm dev:h5` 启动 web-mp
6. 默认账号 `admin / admin123`,登录后改密

### 7.2 构建产物对比(🔷 本分支校验项)

| 项 | `master`(JS) | `springboot2_TypeScript` | 预期差异 |
|---|---|---|---|
| web-ui 源码 | `.js` | `.ts` | — |
| web-ui 产物 | `dist/` | `dist/` | **体积差异 < 5%**(类型擦除,不产出代码) |
| web-mp 源码 | `.js` | `.ts` | — |
| web-mp 产物 | `dist/dev/mp-weixin` | 同左 | 同上 |
| 运行时行为 | — | — | **完全等价** |

### 7.3 并发与恢复

- Tomcat `max-threads=800` 限流保护
- 优雅停机:`actuator/shutdown` 接收 POST(匿名白名单)
- 数据库连接池 Druid:`min-idle=5` / `max-active=20`
- 定时任务持久化到 `qrtz_*`,应用重启后 Quartz 自动恢复
- **🔷 前端无状态**:TS 化不影响任何并发/恢复语义

---

## 8. 风险与待定项

| 编号 | 项 | 影响 | 归属文档 |
|---|---|---|---|
| P1 | 操作日志大表清理策略 | 长期保留 → 表膨胀 | 《详细设计》§8 审计 |
| P2 | 移动端无 refresh token | 30min 后强制重登 | 《需求文档》A-3 |
| P3 | springfox 3.0 与 SB 2.5 兼容性 | 偶发 `/swagger-ui` 加载失败 | 运维文档(待补) |
| P4 | `web-mp` uni-app 3 alpha 稳定性 | DCloud 官方未 GA | 架构 R3 |
| P5 | `com.basic` + `com.core` 双包混用 | 学习成本 | onboarding 文档(待补) |
| P6 | 国际化文案缺失 | 移动端仅中文 | 架构 R8 |
| P7 | Quartz 任务无热加载(误判) | — | — |
| **P8** | **`vue-tsc` 不阻塞构建,类型错误可能进生产** | **中** | 架构 R11;《需求文档》A-9 |
| **P9** | **`web-mp` `strict: false`,类型保护弱** | **中** | 架构 R12;《需求文档》A-10 |
| **P10** | **第三方库类型缺失**(uview-plus / unplugin 系) | 中 | 架构 R13 |
| **P11** | **`types/` 契约与后端不同步时 TS 无法发现** | **中** | 《需求文档》场景 S8;需 review checklist |
| **P12** | **`utils/generator/*.js` 与 TS 混存易误改** | 低 | 架构 R14;《需求文档》A-13 |
| **P13** | **代码生成器产出的前端代码仍为 JS 风格** | **中** | 架构 R18;《需求文档》A-11 |
| **P14** | **`web-ui` 无 `typecheck` 脚本,需手敲 `npx vue-tsc`** | 低 | 《需求文档》A-8 |

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `master` 概要设计为基线,新增「§2.4 前端 TS 组件职责(分层/数量/两端 tsconfig 差异/检查执行链)」「§3.6 TS 开发与迁移流程(新增功能标准流程/字段变更传导路径/迁移步骤)」「§5.1 类型契约清单」+ HD-09~HD-15 + P8~P14 |
