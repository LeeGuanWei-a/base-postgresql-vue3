# 基础平台(base-postgresql-vue3) 概要设计(HLD) — `springboot3_TypeScript` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot3_TypeScript`** 分支
> 前置依赖:《架构方案》v0.1、《需求文档》v0.1
> 本分支 = **`springboot3` 基线(SB 3.5.16 / JDK 17 / Jakarta / Security 6 / springdoc)+ 前端两端全量 TypeScript 化**。
> 后端逻辑架构、核心流程、数据设计、接口清单、部署拓扑与 `springboot3` **一致**(本文档不重复);**新增 §2.5 前端 TS 组件职责、§3.6 TS 开发与迁移流程、§6 HD-17~HD-26、§8 P16~P25**。
> 🔶 = 本分支改动;⬆️ = `springboot3` 继承。

---

## 1. 设计目标与约束回顾

| 来源 | 关键约束 |
|---|---|
| 架构方案 §2 | **JDK 17 + SB 3.5.16**(⬆️);Jakarta(114 处 jakarta / 0 处 javax.servlet);springdoc 2.9.0;Security 6 |
| 架构方案 §2 | **前端全量 TypeScript**:`web-ui` `strict: true` / `web-mp` `strict: false` + `isolatedModules: true`;`vue-tsc` 检查 |
| 架构方案 §2 | 数据库 `basicdb` / **34 表零变更**;后端零改动;接口契约零变更 |
| 需求文档 §7 | 列表 1000 条 < 1s;登录 < 500ms;**TS 运行时零开销**;bundle 体积差异 < 5% |
| 需求文档 §10 | AC-01~AC-38(其中 **AC-25~AC-38 为 TS 专项 14 项**) |

**勿重复大段正文**,仅作引子。

---

## 2. 逻辑架构与模块划分

### 2.1 工程结构(🔶 本分支;⬆️ 继承)

```
base-postgresql-vue3/
├── pom.xml                 # 父 pom,7 子模块(⬆️ 与 springboot3 逐字节一致)
├── basic-admin-api/        # 启动入口 + 26 Controller                                  ⬆️
├── basic-admin-core/       # 业务核心(sys_*/gen_*/qrtz_*/demo)                        ⬆️
├── basic-framework/        # 框架能力(Security 6 / Redis / MP / springdoc / yauaa)     ⬆️
├── basic-common/           # 工具/注解/枚举/基类(-webmvc / hutool-core / jaxb-api)     ⬆️
├── business-core/          # 业务扩展点(com.core)                                     ⬆️
├── extend-quartz/          # Quartz(单独依赖 quartz)                                  ⬆️
├── extend-generator/       # Velocity 代码生成(⚠️ 模板未适配 SB3)                     ⬆️
├── file-sql/               # 8 个 SQL(basicdb-vue3.sql 唯一运行入口)                   ⬆️ 零变更
├── file-bin/  file-doc/    # 静态资源
├── web-ui/                 # PC(Vue3 + ElementPlus)🔶 TypeScript
│   ├── tsconfig.json       🔶 新增(strict: true)
│   ├── auto-imports.d.ts   🔶 新增
│   ├── vite.config.ts      🔶 由 .js 改名
│   └── src/                🔶 79 .ts + 6 .js(generator 模板)+ 95 .vue + types/ 22 个
└── web-mp/                 # 移动(uni-app3 + uview-plus3)🔶 TypeScript
    ├── tsconfig.json       🔶 新增(strict: false + isolatedModules)
    ├── vite.config.ts      🔶 由 .js 改名
    ├── sync-config.cjs     (保留 .cjs,Node 脚本)
    └── src/                🔶 19 .ts + 0 .js
```

### 2.2 后端组件职责(⬆️ 全部继承 `springboot3`,**本分支未改一行**)

| 模块 | 关键类 | 职责 |
|---|---|---|
| `basic-admin-api` | `BasicApplication` / `BasicServletInitializer` | 启动 + war 适配 |
| | `web.config.SwaggerConfig` | springdoc 配置(⬆️ 已重写) |
| | `web.controller.**` | 26 个 Controller(kaptcha 版 `CaptchaController`) |
| `basic-admin-core` | `com.basic.system.service.impl.**` / `mapper.**` / `domain.**` | 业务 Service / Mapper / 实体 |
| `basic-framework` | `SecurityConfig` | Security 6 `SecurityFilterChain` |
| | `JwtAuthenticationTokenFilter` / `AuthenticationEntryPointImpl` / `LogoutSuccessHandlerImpl` | 鉴权链(jakarta) |
| | `MybatisPlusConfig` / `MyBatisConfig` | MP 3.5.7 分页 + 扫描 |
| | `DataScopeAspect` / `LogAspect` / `RateLimiterAspect` / `RepeatSubmitInterceptor` | 切面 |
| | `UserAgentConfig` | ⬆️ yauaa 8.2.0 单例 |
| | `SysLoginService` / `TokenService` | 登录(kaptcha 校验)+ Token |
| `basic-common` | `R` / `AjaxResult` / `TableDataInfo` / `BaseEntity` | 统一返回 / 基类 |
| | `SecurityUtils` / `StringUtils` / `DateUtils` / `ExcelUtil` | 工具(poi 5.5.1) |
| | `@Log` / `@RepeatSubmit` / `@RateLimiter` / `@DataScope` / `@Anonymous` | 注解 |
| `extend-quartz` | `ScheduleConfig` / `ScheduleUtils` / `AbstractQuartzJob` / `JobInvokeUtil` | Quartz |
| `extend-generator` | `GenController` / `VelocityUtils` | 代码生成(Velocity 2.3) |
| `business-core` | `Demo` / `DemoController` | 示例 |

> **🔶 本分支 7 个后端模块零修改**:`pom.xml`、Java 源码、`application*.yml`、`file-sql/**` 与 `springboot3` **逐字节一致**。

### 2.3 扩展 SPI(⬆️ 继承)

| SPI | 接入方式 | 用例 |
|---|---|---|
| 自定义 Job | `@Component("ryTask")` + `public void ryXxx(...)` | `ryTask.ryNoParams` |
| 自定义权限注解 | `@PreAuthorize("@ss.hasPermi('...')")` | — |
| 字典加载 | `DictUtils` 增枚举/缓存 key | — |
| 数据源 | `@DataSource(DataSourceType.SLAVE)` | 多数据源 |
| 代码生成模板 | `vm/java/...` | — |
| **🔶 前端类型扩展** | 在 `web-ui/src/types/api/**` 新增 interface,并在 `types/api/index.ts` re-export | 后端新增接口时配套 |

### 2.4 数据设计(概要,⬆️ 与 `springboot3` 完全一致)

| schema | 表数 | 内容 |
|---|---|---|
| `basicdb` | **34** | `gen_*` 2 + `qrtz_*` 11 + `sys_*` 21 |
| `public` | 0 | 不使用 |

**运行期非 DB 存储(⬆️ 一致)**:

| 存储 | Key | TTL |
|---|---|---|
| Redis | `login_tokens:<uuid>` | 30min |
| Redis | `sys_dict::<dictType>` | — |
| Redis | `REPEAT_SUBMIT:*` | 500ms |
| 本地磁盘 | `D:/ruoyi/uploadPath` | — |

> 🔶 **本分支不新增任何存储**(TS 化是纯编译期改造)。

### 2.5 🔶 前端 TypeScript 组件职责(本分支核心)

#### 2.5.1 web-ui 分层与数量

| 分层 | 路径 | 数量 | 职责 | 约束 |
|---|---|---|---|---|
| 类型契约 | `src/types/api/**` | 19 | 接口请求/响应 interface | 镜像后端模块路径;禁业务逻辑 |
| 类型出口 | `src/types/api/index.ts` | 1 | 统一 re-export | 新增类型必须登记 |
| 全局组件类型 | `src/types/components.d.ts` | 1 | 全局组件声明 | **自动生成,禁改** |
| 全局声明 | `src/types/global.d.ts` | 1 | `window` / `ComponentCustomProperties` | 手写 |
| 类型入口 | `src/types/index.ts` | 1 | 对外统一入口 | — |
| **types 合计** | `src/types/**` | **22** | — | — |
| API 层 | `src/api/**` | 19 | HTTP 调用,显式返回类型 | 必须 `Promise<XxxResult>` |
| 工具层 | `src/utils/**` | 13 `.ts` + 6 `.js` | 通用工具 | `generator/` 保留 JS(ADR-24) |
| 状态层 | `src/store/**` | 8 | Pinia store(含 `lock.ts`) | `defineStore` + 泛型 |
| 插件层 | `src/plugins/**` | 6 | `$tab` / `$modal` / `$auth` / `$cache` / `$download` | 挂载方法须在 `global.d.ts` 声明 |
| 指令层 | `src/directive/**` | 4 | `v-hasPermi` / `v-hasRole` / `v-copyText` | — |
| 路由 | `src/router/index.ts` | 1 | 动态路由 | `RouteRecordRaw` |
| 入口 | `src/main.ts` / `permission.ts` / `settings.ts` | 3 | 启动 / 路由守卫 / 配置 | — |
| 布局 | `src/layout/components/index.ts` | 1 | 布局组件注册 | — |
| 视图 | `src/views/**/*.vue` | 95 | 页面 | `<script setup lang="ts">` |
| **源文件合计** | — | **79 `.ts` + 6 `.js` + 95 `.vue`** | — | — |

#### 2.5.2 web-mp 分层与数量

| 分层 | 路径 | 数量 | 职责 |
|---|---|---|---|
| API 层 | `src/api/**` | 5 | `index` / `login` / `system/{dept,role,user}` |
| 状态层 | `src/store/**` | 4 | `index` + `modules/{app,permission,user}` |
| 工具层 | `src/utils/**` | 5 | `auth` / `config` / `dict` / `jsencrypt` / `request` |
| 指令 | `src/directive/permission.ts` | 1 | `v-hasPermi` |
| 全局声明 | `src/types/global.d.ts` | 1 | uni-app 全局扩展 |
| 入口 | `src/main.ts` / `permission.ts` / `config.ts` | 3 | 启动 / 守卫 / 配置 |
| 页面 | `src/pages/**/*.vue` | — | `<script setup lang="ts">` |
| **源文件合计** | — | **19 `.ts` + 0 `.js`** | — |

#### 2.5.3 两端 tsconfig 差异对照(14 项)

| 配置项 | `web-ui` | `web-mp` | 说明 |
|---|---|---|---|
| `target` | `esnext` | `ES2020` | mp 端受小程序运行时限制 |
| `module` | `esnext` | `ESNext` | — |
| `moduleResolution` | `bundler` | `Bundler` | — |
| **`strict`** | **`true`** | **`false`** | ADR-21:mp 端类型定义不全 |
| `noImplicitAny` | 继承 `strict` = true | **`false`** | 同上 |
| `isolatedModules` | 未设 | **`true`** | 要求 `import type` |
| `skipLibCheck` | `true` | `true` | 三方库类型不全时不阻塞 |
| `allowJs` | `true` | 未设 | web-ui 允许 `.js` 共存 |
| `noEmit` | `true` | 未设(由 `vue-tsc --noEmit` 控制) | — |
| `types` | `["node","vite/client","element-plus/global"]` | `[]` | — |
| `include` | `src/**/*.ts` / `src/**/*.vue` / `vite/**/*.ts` / `auto-imports.d.ts` / `vite.config.ts` | `src/**/*.ts` / `src/**/*.d.ts` / `src/**/*.tsx` / `src/**/*.vue` / `vite.config.ts` | — |
| `exclude` | `node_modules` / `dist` / **`src/**/*.js`** | `node_modules` / `dist` / `unpackage` / `scripts` | web-ui 靠 `exclude` 隔离 generator |
| `paths` | `"@/*": ["src/*"]` | `"@/*": ["src/*"]` | 两端一致 |
| 基础配置继承 | `@vue/tsconfig@0.5.1` | 无(全部内联) | web-ui 通过 `@vue/tsconfig` 简化 |

#### 2.5.4 类型检查执行链

```
开发者本地
   │
   ├─ web-ui: npx vue-tsc --noEmit      (package.json 无 typecheck 脚本,A-14)
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
   ⚠️ 不阻塞 vite build(ADR-25);构建只跑 vite build
```

#### 2.5.5 依赖关系图(类型 → api → 视图)

```
        types/api/common.ts(AjaxResult / R / ListResult / PageQuery)
                    │
    ┌───────────────┼────────────────┬────────────────────┐
    ▼               ▼                ▼                    ▼
types/api/      types/api/       types/api/          types/api/
login.ts        menu.ts          monitor/*.ts        system/*.ts
    │               │                │                    │
    └───────────────┴────────────────┴────────────────────┘
                    │  re-export
                    ▼
          types/api/index.ts ──▶ types/index.ts
                    │ import type
    ┌───────────────┼────────────────┬────────────────────┐
    ▼               ▼                ▼                    ▼
api/login.ts    api/menu.ts      api/monitor/*.ts    api/system/*.ts
(Promise<T>)    (Promise<T>)     (Promise<ListResult<T>>)  (Promise<ListResult<T>>)
    │               │                │                    │
    └───────────────┴────────────────┴────────────────────┘
                    │ 被调用
                    ▼
        views/**/*.vue <script setup lang="ts">
        store/**/*.ts        plugins/**/*.ts
                    │
                    ▼
        types/global.d.ts(声明 globalProperties 挂载方法)
```

---

## 3. 核心流程设计

### 3.1 登录流程(⬆️ `springboot3` 基线 + 🔶 TS 类型标注)

**状态机(单次登录会话,kaptcha 版)**

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

**时序(ASCII,🔶 为前端类型标注)**

```
[PC/Mobile TS]     [Controller]      [SysLoginService]      [Redis/DB]
   │  getCodeImg(): Promise<CaptchaInfoResult>          🔶
   │ ────────────────▶  CaptchaController(kaptcha 2.3.3)
   │ ◀────────────────  {captchaOnOff, uuid, img, captchaEnabled}
   │                                 │
   │  login(u,p,code,uuid): Promise<LoginInfoResult>    🔶
   │ ────────────────▶  SysLoginController
   │                       │  login(LoginBody)
   │                       │ ────────▶  ① RSA 解密
   │                       │         │  ② validateCaptcha(uuid, code)
   │                       │         │  ③ loadUserByUsername
   │                       │         │  ④ BCrypt.matches
   │                       │         │  ⑤ TokenService.createToken
   │                       │         │  ⑥ ⬆️ AsyncFactory + yauaa 解析 UA
   │ ◀──────────────── {token}        │  写 sys_logininfor
   │
   │  getInfo(): Promise<UserInfoResult>                🔶
   │  getRouters(): Promise<...>                        🔶
```

> 🔶 **TS 化不改调用时序**,只在 `api/login.ts` 处加返回类型;运行时字节码等价。
> ⚠️ 本分支是 **kaptcha**(`LoginBody` 只有 `username`/`password`/`code`/`uuid`,**无** `captchaData`)。

### 3.2 锁屏/解锁(⬆️ 一致;🔶 `lock.ts`)

```
PC store/modules/lock.ts 触发 lock 状态 → 锁屏页
   │
   ▼ 提交 unlockPassword
   │  POST /login(复用 token,只校验密码)
   ▼
SysLoginService.login: 短路验证码检查;校验密码;
   成功 → 返回原 token;失败 → UserPasswordNotMatchException
```

> 🔶 `lock.ts` 新增 `LockState` interface;`unlock(password: string)` 显式返回类型。

### 3.3 定时任务调度(⬆️ 一致)

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

### 3.4 代码生成(⬆️ 一致;⚠️ 产出需手工 ts 化)

```
导入表 → GenTableServiceImpl.importGenTable
  → GenTableColumnServiceImpl.importGenTableCol
  → 写 gen_table / gen_table_column

生成:
  download(genTableIds) →
    VelocityEngine(2.3) 渲染 .vm 模板
    → 打包 zip → 浏览器下载
    → 🔶 前端代码需手工 ts 化(R30)
```

### 3.5 移动端登录(⬆️ 一致;🔶 TS 类型)

```
web-mp/pages/login/index.vue(<script setup lang="ts">)
  getCodeImg(): Promise<CaptchaInfoResult>   🔶
  login(): Promise<LoginInfoResult>          🔶
  ↓  Basic-Token 存到 uni.storage
getInfo(): Promise<UserInfoResult>           🔶
getRouters()   (本分支先消费即可,后续转 pages.json)
```

### 3.6 🔶 TypeScript 开发与迁移流程(本分支核心)

#### 3.6.1 新增功能的标准流程(强制)

```
① 后端新增/变更接口
      │
      ▼
② web-ui/src/types/api/<模块>/<实体>.ts 定义 interface
      │  (镜像后端 DTO/VO 字段)
      ▼
③ web-ui/src/types/api/index.ts 登记 re-export
      │
      ▼
④ web-ui/src/api/<模块>/<实体>.ts 写函数
      │  export function xxx(q: Q): Promise<ListResult<T>>
      │  ⚠️ 必须显式标注返回类型
      ▼
⑤ views/**/*.vue 用 <script setup lang="ts">
      │  const list = ref<T[]>([])
      ▼
⑥ 本地 npx vue-tsc --noEmit → 0 error
      │
      ▼
⑦ 提 PR(评审关注:是否新增 .js / 返回类型是否显式)
```

#### 3.6.2 后端字段变更的传导路径(TS 化核心价值)

```
后端 DB 字段改名
   │
   ▼
后端 DTO/VO 改名(人工)
   │
   ▼
⚠️ types/api/**/*.ts 同步改名(人工,唯一手动环节)
   │
   ▼
vue-tsc 扫描
   │
   ├─ api/*.ts 函数签名不匹配 → 报错
   ├─ views/*.vue 引用旧字段 → 报错
   ├─ store/*.ts 引用旧字段 → 报错
   └─ plugins/*.ts 引用旧字段 → 报错
   │
   ▼
逐个修复 → 0 error → 提交
```

> **关键**:`types/` 是前后端契约的"唯一锚点"。若类型文件忘记同步,**TS 无法自动发现**(因为类型自身仍自洽)。**该环节必须列入 code review checklist**(R29)。

#### 3.6.3 迁移期的一次性流程(已完成,记录备查)

```
步骤 1: web-ui 整目录从 springboot2_TypeScript 替换进来(~196 文件)
步骤 2: 手工恢复 web-ui/src/assets/styles/basic.scss(主题色 #1890ff)
步骤 3: 核对 web-ui tsconfig.json(strict true / @vue/tsconfig / exclude src/**/*.js)
步骤 4: 核对 web-mp tsconfig.json(strict false / isolatedModules true)
步骤 5: 两端确认 vite.config.ts(无 .js 版)
步骤 6: 核对 package.json TS 工具链(web-ui 精确版本 / web-mp ^ 范围)
步骤 7: npx vue-tsc --noEmit(web-ui)→ 0 error
步骤 8: pnpm typecheck(web-mp)→ 0 error
步骤 9: 文件统计核对(79/6/95、19/0、types 22)
步骤 10: 全链路回归(登录/CRUD/锁屏/代码生成/定时任务)
```

#### 3.6.4 迁移期注意事项(实测)

| 注意点 | 说明 |
|---|---|
| `basic.scss` 必须从 `springboot3` 单独 checkout | `springboot2_TypeScript` 没这文件,直接用会丢失 `--el-color-primary: #1890ff` 蓝色品牌色(ADR-28) |
| `web-mp` 的 `sync-config.cjs` / `scripts/*.cjs` 保留 `.cjs` | Node 脚本,不在 `tsconfig.include` 内 |
| `web-ui` 的 `utils/generator/*.js` 6 个保留 | ADR-24,由 `tsconfig.exclude` 隔离 |
| 两端 `types` 配置不同 | web-ui 需要 `node` + `element-plus/global`;web-mp 必须 `[]`,否则与 uni-app 冲突 |
| `isolatedModules`(web-mp) | 所有类型导入必须 `import type`,否则 `TS1484` |

---

## 4. 接口清单(⬆️ 与 `springboot3` 完全一致)

> **🔶 本分支 HTTP 接口零变更**:路径、方法、入参、出参、权限字符全部与 `springboot3` 一致。TS 化只在前端加类型标注。
>
> 完整清单见 `docs/springboot3-概要设计.md` §5,此处仅保留分类索引:
>
> - **匿名**:`GET /captchaImage` / `POST /login` / `POST /register` / `POST /monitor/shutdown`
> - **已登录**:`POST /logout` / `GET /getInfo` / `GET /getRouters` / `GET /system/dict/type/{dictType}` / `GET /common/download` / `POST /common/upload`
> - **系统管理**:`/system/{user,role,menu,dept,post,config,notice,dict/*}`
> - **监控**:`/monitor/{operlog,logininfor,online,job,jobLog,server,cache,druid}`
> - **工具**:`/tool/gen/{list,importTable,download,synchDb,preview}`
> - **文档**:`GET /swagger-ui/index.html` / `GET /v3/api-docs`

### 4.1 🔶 新增的"类型契约清单"(非 HTTP 接口,但属本分支交付物)

| 类型文件 | 对应后端 | 状态 |
|---|---|---|
| `types/api/common.ts` | `R` / `AjaxResult` / `TableDataInfo` / `PageQuery` | ✅ |
| `types/api/login.ts` | `LoginBody` / `LoginInfoResult` / `UserInfoResult` / `CaptchaInfoResult`(kaptcha 语义) | ✅ |
| `types/api/menu.ts` | `RouterVo` / `MetaVo` | ✅ |
| `types/api/monitor/{cache,job,jobLog,logininfor,online,operlog}.ts` | 监控 6 模块 | ✅ |
| `types/api/system/{config,dept,dict,menu,notice,post,role,user}.ts` | 系统 8 模块 | ✅ |
| `types/api/tool/gen.ts` | `GenTable` / `GenTableColumn` | ✅ |
| `types/api/index.ts` | 统一 re-export | ✅ |
| `types/components.d.ts` | 全局组件(自动生成) | ✅ |
| `types/global.d.ts` | `window` / `ComponentCustomProperties` | ✅ |
| `types/index.ts` | 对外入口 | ✅ |
| **(合计)** | — | **22 个文件** |

### 4.2 内部 SPI(同 §2.3)

---

## 5. 关键技术决策(HD-)

> **HD-01~HD-16 继承自 `springboot3`**(STATELESS JWT / AsyncManager / MP+XML 双轨 / 数据权限切面 / 限流 / Token+Redis / uni-app 组件 / pages.json / 一次升到 3.5.16 / Jakarta 硬切 / springdoc / SecurityFilterChain / starter 变体 / yauaa / jaxb / DB+前端零变更),详见 `docs/springboot3-概要设计.md` §6。下表为本分支**新增 HD-17~HD-26**。

| # | 决策 | 理由 | 影响 |
|---|---|---|---|
| **HD-17** | **两端独立 `tsconfig.json`,不共享** | web-ui 跑浏览器(Vite 6 / `esnext` + `element-plus/global`),web-mp 跑 uni-app(Vite 5 / `ES2020` + `types: []`);共享会引入 `types` 冲突 | 两份配置需分别维护 |
| **HD-18** | **类型集中在 `src/types/`,按后端模块镜像** | `api/*.ts` 保持精简;跨模块复用(如 `ListResult<T>`)方便;跳转定位清晰 | 需在 `types/api/index.ts` 统一登记,漏登记会导入失败 |
| **HD-19** | **`strict` 按端分级** | uni-app 3 alpha 类型定义不全,一次性 strict 会引入大量 `@ts-ignore` | mp 端类型保护弱(M4 收紧) |
| **HD-20** | **类型检查不阻塞构建** | 迁移期强校验阻塞开发;Vite 打包不需要类型信息 | 类型错误可能进生产(R21);M5 入 CI |
| **HD-21** | **`utils/generator/*.js` 保留 JS** | 用大量字符串拼接生成 HTML/Vue 代码,类型化收益极低;改 TS 易引 bug | 与 TS 源码混存(R24);靠 `tsconfig.exclude` 隔离 |
| **HD-22** | **`<script setup lang="ts">` 强制** | 组合式 API 类型推导最好;Options API 的 `this` 推断在复杂场景不可靠 | 存量组件已全量迁移 |
| **HD-23** | **全局挂载方法用 `ComponentCustomProperties` 声明** | `app.config.globalProperties.useDict` 等在模板里要用,不声明会报错 | `types/global.d.ts` 需与 `main.ts` 挂载清单同步 |
| **HD-24** | **复用 `springboot2_TypeScript` 的 `web-ui` 整目录** | 避免重复劳动与两套实现漂移 | 需手工恢复 `basic.scss`(ADR-28)与逐目录复核(R31) |
| **HD-25** | **`web-mp` 保留 `sync-config.cjs` / `scripts/*.cjs`** | Node 脚本,不参与前端类型检查 | 靠 `tsconfig.exclude` 的 `scripts` 项隔离 |
| **HD-26** | **TS 化不改后端契约** | 前端类型只是镜像;改契约会牵动后端与 DB | 后端与 DB 零改动(AC-37) |

---

## 6. 部署视图(⬆️ 与 `springboot3` 一致;🔶 前端产物)

```
        ┌────────────────────────────┐
        │  Nginx(可选)              │
        │  /api/*  → 8080           │
        │  /        → web-ui dist   │  🔶 dist 由 vite build 从 TS 源码产出
        └─────────────┬──────────────┘
                      │
                      ▼
        ┌────────────────────────────────┐
        │  basic-admin-api jar           │
        │  ⬆️ 运行时 JDK 17 / Tomcat 10.1+│
        │  ⬆️ 与 springboot3 完全相同     │
        │  端口:8080 / profile: dev|prod  │
        └──────┬──────────────┬──────────┘
               │              │
               ▼              ▼
        ┌────────────┐   ┌──────────────────┐
        │PostgreSQL  │   │  Redis(可选)     │
        │15+(basicdb)│   │  ⬆️ spring.data.redis.*
        │34 表       │   │                  │
        └────────────┘   └──────────────────┘

        web-mp:  🔶 src 为 TS,编译产物与 springboot3 同构
        ┌────────────────────────────┐
        │  HBuilder / 微信开发者工具  │
        │  编译产物:dist/dev/mp-weixin│
        │  🔶 类型在编译期擦除        │
        └────────────────────────────┘
```

### 6.1 启动顺序

1. **JDK 17** + IDE 设为 17
2. PostgreSQL 启动 + `psql -f file-sql/basicdb-vue3.sql`(34 表)
3. (可选)Redis 启动
4. `mvn clean compile` → 0 ERROR
5. `mvn spring-boot:run -pl basic-admin-api` 启动后端
6. **(可选)类型检查**:`cd web-ui && npx vue-tsc --noEmit`;`cd web-mp && pnpm typecheck`
7. `pnpm dev` / `pnpm dev:h5` 启动前端
8. `admin / admin123` 登录(kaptcha 字符验证码)

### 6.2 构建产物对比(🔶 校验项)

| 项 | `springboot3`(JS) | 本分支(TS) | 预期差异 |
|---|---|---|---|
| web-ui 源码 | `.js` | `.ts` | — |
| web-ui 产物 | `dist/` | `dist/` | **体积差异 < 5%**(类型擦除) |
| web-mp 源码 | `.js` | `.ts` | — |
| web-mp 产物 | `dist/dev/mp-weixin` | 同左 | 同上 |
| 运行时行为 | — | — | **完全等价** |

### 6.3 并发与恢复(⬆️ 一致)

- Tomcat `max-threads=800`
- 优雅停机:`actuator/shutdown`
- Druid `min-idle=5` / `max-active=20`
- 定时任务持久化 `qrtz_*`,重启自动恢复
- **🔶 前端无状态**:TS 化不影响任何并发/恢复语义

---

## 7. 风险与待定项

> **P1~P15 继承自 `springboot3`**(日志大表 / refresh token / uni-app alpha / 双包前缀 / 国际化 / javax 残留 / starter 变体 / PathPatternParser / 配置键 / 生成器 SB3 模板 / deprecation 等),详见 `docs/springboot3-概要设计.md` §8。下表为**本分支新增**。

| 编号 | 项 | 影响 | 归属文档 |
|---|---|---|---|
| **P16** | **`vue-tsc` 不阻塞构建,类型错误可能进生产** | **中** | 架构 R21;A-15 |
| **P17** | **`web-mp` `strict: false`,类型保护弱** | **中** | 架构 R22;A-16 |
| **P18** | **第三方库类型缺失**(uview-plus / unplugin 系) | 中 | 架构 R23 |
| **P19** | **`types/` 契约与后端不同步时 TS 无法发现** | **中** | 架构 R29;场景 S12 |
| **P20** | **`utils/generator/*.js` 与 TS 混存易误改** | 低 | 架构 R24;A-19 |
| **P21** | **旧 `.js` 未删净会留双份** | 低 | 架构 R25 |
| **P22** | **版本漂移(`@vue/tsconfig` / `typescript`)** | 低 | 架构 R26 |
| **P23** | **`isolatedModules` 要求 `import type`** | 低 | 架构 R27 |
| **P24** | **`web-ui` 无 `typecheck` 脚本** | 低 | 架构 R28;A-14 |
| **P25** | **代码生成器产出的前端仍是 JS 风格** | **中** | 架构 R30;A-18 |
| **P26** | **整目录复制可能带入 SB2 时代差异** | 中 | 架构 R31;A-21 |

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `springboot3` 概要设计(v0.1)为基线(HD-01~16 / P1~15 继承),叠加「前端全量 TS 化」专题:§2.5 前端 TS 组件职责(两端分层表 / 14 项 tsconfig 差异 / 检查执行链 / 依赖关系图)、§3.6 TS 开发与迁移流程(新增标准流程 / 字段变更传导 / 迁移 10 步 / 4 条注意事项)、§4.1 类型契约清单(22 个)、HD-17~HD-26、§6.2 产物对比、P16~P26 |
