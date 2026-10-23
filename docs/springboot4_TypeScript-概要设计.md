# 基础平台(base-postgresql-vue3) 概要设计(HLD) — `springboot4_TypeScript` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot4_TypeScript`** 分支
> 前置依赖:《架构方案》v0.1、《需求文档》v0.1
> 本分支 = **`springboot4` 基线(JDK 21 / SB 4.1.1 / Jackson 3 / Security 7)+ 前端两端全量 TypeScript 化**。
> 后端逻辑架构、核心流程、数据设计、接口清单、部署拓扑与 `springboot4` **一致**(本文档不重复);**新增 §2.5 前端 TS 组件职责、§3.7 TS 开发与迁移流程、§6 HD-29~HD-40、§8 P31~P45**。
> 🔶 = 本分支;⬆️ = `springboot4` 继承。

---

## 1. 设计目标与约束回顾

| 来源 | 关键约束 |
|---|---|
| 架构方案 §2 | **JDK 21 + SB 4.1.1 + Jackson 3 + Security 7**(⬆️);jakarta 6.1.0;springdoc 3.1.1;Quartz RAMJobStore |
| 架构方案 §2 | **前端全量 TypeScript**:`web-ui` `strict: true` / `web-mp` `strict: false` + `isolatedModules: true`;`vue-tsc` 检查 |
| 架构方案 §2 | 数据库 `basicdb` / **34 表零变更**;后端零改动;接口契约零变更 |
| 需求文档 §7 | 列表 1000 条 < 1s;登录 < 500ms;**TS 运行时零开销**;bundle 体积差异 < 5% |
| 需求文档 §10 | AC-01~AC-80(其中 **AC-67~AC-80 为 TS 专项 14 项**) |

**勿重复大段正文**,仅作引子。

---

## 2. 逻辑架构与模块划分

### 2.1 工程结构(🔶 本分支;⬆️ 继承)

```
base-postgresql-vue3/
├── pom.xml                 # 父 pom,7 子模块(⬆️ 与 springboot4 逐字节一致)
├── basic-admin-api/        # 启动入口 + 24 平台 Controller                       ⬆️
├── basic-admin-core/       # 平台业务核心                                       ⬆️
├── basic-framework/        # Security 7 / Jackson 3 / Redis / MP / springdoc    ⬆️
├── basic-common/           # 工具/注解/枚举/基类(JsonUtils Jackson 3)          ⬆️
├── business-core/          # 业务扩展点(Demo)                                 ⬆️
├── extend-quartz/          # Quartz(starter + RAMJobStore)                      ⬆️
├── extend-generator/       # 代码生成(⚠️ 模板仍产 SB2 风格)                    ⬆️
├── file-sql/               # basicdb-vue3.sql(⬆️ 零变更,34 表)                ⬆️
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

### 2.2 后端组件职责(⬆️ 全部继承 `springboot4`,**本分支未改一行**)

| 模块 | 关键类 | 职责 |
|---|---|---|
| `basic-admin-api` | `BasicApplication` / `SwaggerConfig`(springdoc 3.1)/ 24 Controller | 启动 + 平台接口 |
| `basic-admin-core` | `Sys*ServiceImpl` / Mapper / 实体 | 业务 |
| `basic-framework` | `SecurityConfig`(7)/ **`BigNumberModuleConfig`** / `RedisConfig`(Jackson 3 + 白名单)/ `DruidConfig`(boot4)/ `UserAgentConfig`(yauaa) | 框架 |
| `basic-common` | **`JsonUtils`**(Jackson 3)/ **`RedisCache`**(脏数据容错)/ **`LoginUser`**(`getAuthorities()` 非 null) | 通用 |
| `extend-quartz` | `ScheduleConfig`(starter + RAMJobStore) | 定时 |
| `extend-generator` | `GenController` / `VelocityUtils` | 代码生成 |

> **🔶 本分支 7 个后端模块零修改**:`pom.xml`、Java 源码、`application*.yml`、`file-sql/**` 与 `springboot4` **逐字节一致**。

### 2.3 扩展 SPI(⬆️ 继承)

| SPI | 接入方式 | 用例 |
|---|---|---|
| 自定义 Job | `@Component("ryTask")` | `ryTask.ryNoParams` |
| 自定义权限注解 | `@PreAuthorize("@ss.hasPermi(...)")` | — |
| 自定义 JSON 序列化 | `JsonMapperBuilderCustomizer`(**独立类**) | 大数 |
| 数据源 | `@DataSource(DataSourceType.SLAVE)` | 多数据源 |
| 代码生成模板 | `vm/java/...` | — |
| **🔶 前端类型扩展** | 在 `types/api/**` 新增 interface + `types/api/index.ts` re-export | 后端新增接口时配套 |

### 2.4 数据设计(概要,⬆️ 与 `springboot4` 完全一致)

| schema | 表数 | 内容 |
|---|---|---|
| `basicdb` | **34** | `gen_*` 2 + `qrtz_*` 11 + `sys_*` 21 |
| `public` | 0 | 不使用 |

**运行期非 DB 存储**:

| 存储 | Key | TTL | 备注 |
|---|---|---|---|
| Redis | `login_tokens:<uuid>` | 30min | ⬆️ Jackson 3 序列化 |
| Redis | `sys_dict::<dictType>` | — | ⬆️ |
| Redis | `REPEAT_SUBMIT:*` | 500ms | ⬆️ |
| 进程内存 | Quartz RAMJobStore | 进程 | ⬆️ 任务运行态 |
| 本地磁盘 | `D:/ruoyi/uploadPath` | — | ⬆️ |

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
| 工具层 | `src/utils/**` | 13 `.ts` + 6 `.js` | 通用工具 | `generator/` 保留 JS(ADR-36) |
| 状态层 | `src/store/**` | 8 | Pinia(含 `lock.ts`) | `defineStore` + 泛型 |
| 插件层 | `src/plugins/**` | 6 | `$tab` / `$modal` / `$auth` / `$cache` / `$download` | 挂载方法须在 `global.d.ts` 声明 |
| 指令层 | `src/directive/**` | 4 | `v-hasPermi` / `v-hasRole` / `v-copyText` | — |
| 路由 | `src/router/index.ts` | 1 | 动态路由 | `RouteRecordRaw` |
| 入口 | `src/main.ts` / `permission.ts` / `settings.ts` | 3 | 启动 / 守卫 / 配置 | — |
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
| **`strict`** | **`true`** | **`false`** | ADR-33:mp 端类型定义不全 |
| `noImplicitAny` | 继承 `strict` = true | **`false`** | 同上 |
| `isolatedModules` | 未设 | **`true`** | 要求 `import type` |
| `skipLibCheck` | `true` | `true` | 三方库类型不全时不阻塞 |
| `allowJs` | `true` | 未设 | web-ui 允许 `.js` 共存 |
| `noEmit` | `true` | 未设(由 `vue-tsc --noEmit` 控制) | — |
| `types` | `["node","vite/client","element-plus/global"]` | `[]` | — |
| `include` | `src/**/*.ts` / `*.vue` / `vite/**/*.ts` / `auto-imports.d.ts` / `vite.config.ts` | `src/**/*.ts` / `*.d.ts` / `*.tsx` / `*.vue` / `vite.config.ts` | — |
| `exclude` | `node_modules` / `dist` / **`src/**/*.js`** | `node_modules` / `dist` / `unpackage` / `scripts` | web-ui 靠 `exclude` 隔离 generator |
| `paths` | `"@/*": ["src/*"]` | `"@/*": ["src/*"]` | 两端一致 |
| 基础配置继承 | `@vue/tsconfig@0.5.1` | 无(全部内联) | web-ui 通过 `@vue/tsconfig` 简化 |

#### 2.5.4 类型检查执行链

```
开发者本地
   │
   ├─ web-ui: npx vue-tsc --noEmit      (package.json 无 typecheck 脚本,A-33)
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
   ⚠️ 不阻塞 vite build(ADR-37);构建只跑 vite build
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
api/*.ts(19 个,函数签名显式返回类型)
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

#### 2.5.6 与 Jackson 3 的关系(⬆️ 后端,🔶 前端无感)

| 层 | 变化 | 前端影响 |
|---|---|---|
| 后端序列化 | Jackson 2 → 3(`tools.jackson.*`) | **无**:JSON 结构不变(字段名/日期格式/null/大数策略一致) |
| `bigint` 大数 | `BigNumberSerializer` 超 `±2^53-1` 输出**字符串** | **有**:前端类型用 `string`(与 SB3 一致) |
| 注解包 | 仍是 `com.fasterxml.jackson.annotation.*` | **无**(前端不涉及) |
| 前端类型 | 只镜像 JSON | **无需为 Jackson 3 做任何调整**(ADR-41) |

---

## 3. 核心流程设计

### 3.1 ~ 3.6 平台流程(⬆️ 与 `springboot4` 一致)

| 小节 | 内容 | 状态 |
|---|---|---|
| 3.1 登录流程 | kaptcha + JWT + Security 7 | ⬆️ 一致(🔶 前端加类型) |
| 3.2 锁屏/解锁 | `store/modules/lock.ts` 复用 `/login` | ⬆️ 一致(🔶 `.ts`) |
| 3.3 定时任务 | Quartz starter + RAMJobStore | ⬆️ 一致 |
| 3.4 代码生成 | Velocity 模板(⚠️ 产出需手工 ts 化) | ⬆️ 一致 |
| 3.5 移动端登录 | H5/小程序 | ⬆️ 一致(🔶 类型) |
| 3.6 SB4 升级流程 | Jackson 3 / Security 7 / 循环依赖 | ⬆️ 见 `docs/springboot4-概要设计.md` §3.6 |

### 3.7 🔶 TypeScript 开发与迁移流程(本分支核心)

#### 3.7.1 新增功能的标准流程(强制)

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

#### 3.7.2 后端字段变更的传导路径(TS 化核心价值)

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

> **关键**:`types/` 是前后端契约的"唯一锚点"。若类型文件忘记同步,**TS 无法自动发现**(类型自身仍自洽)。该环节**必须列入 code review checklist**(R43)。

#### 3.7.3 迁移期的一次性流程(已完成,记录备查)

```
步骤 1: web-ui 整目录从 springboot3_TypeScript 替换进来
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

#### 3.7.4 迁移期注意事项(实测)

| 注意点 | 说明 |
|---|---|
| `basic.scss` 必须从 `springboot4` 单独 checkout | `springboot3_TypeScript` 可能缺该文件,会导致丢失 `--el-color-primary: #1890ff` 蓝色品牌色(ADR-40) |
| `web-mp` 的 `sync-config.cjs` / `scripts/*.cjs` 保留 `.cjs` | Node 脚本,不在 `tsconfig.include` 内 |
| `web-ui` 的 `utils/generator/*.js` 6 个保留 | ADR-36,由 `tsconfig.exclude` 隔离 |
| 两端 `types` 配置不同 | web-ui 需 `node` + `element-plus/global`;web-mp 必须 `[]`,否则与 uni-app 冲突 |
| `isolatedModules`(web-mp) | 所有类型导入必须 `import type`,否则 `TS1484` |
| **Jackson 3 无需前端适配** | JSON 结构未变;只有大数仍按字符串处理(与 SB3 一致) |

---

## 4. 接口清单(⬆️ 与 `springboot4` 完全一致)

> **🔶 本分支 HTTP 接口零变更**:路径、方法、入参、出参、权限字符全部与 `springboot4` 一致。TS 化只在前端加类型标注。
>
> 完整清单见 `docs/springboot4-概要设计.md` §5,此处仅保留分类索引:
>
> - **匿名**:`GET /captchaImage` / `POST /login` / `POST /register` / `POST /monitor/shutdown`
> - **已登录**:`POST /logout` / `GET /getInfo` / `GET /getRouters` / `GET /system/dict/type/{dictType}` / `GET /common/download` / `POST /common/upload`
> - **系统管理**:`/system/{user,role,menu,dept,post,config,notice,dict/*}`
> - **监控**:`/monitor/{operlog,logininfor,online,job,jobLog,server,cache,druid}`
> - **工具**:`/tool/gen/{list,importTable,download,synchDb,preview}`
> - **文档**:`GET /swagger-ui/index.html` / `GET /v3/api-docs`(springdoc **3.1.1**)

### 4.1 🔶 新增的"类型契约清单"(非 HTTP 接口,但属本分支交付物)

| 类型文件 | 对应后端 | 状态 |
|---|---|---|
| `types/api/common.ts` | `AjaxResult` / `R` / `TableDataInfo` / `PageQuery` / `TreeSelect` | ✅ |
| `types/api/login.ts` | `LoginBody` / `LoginInfoResult` / `UserInfoResult` / `CaptchaInfoResult`(**kaptcha 语义**) | ✅ |
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

> **HD-01~HD-28 继承自 `springboot4`**(STATELESS JWT / Jackson 3 / Module 构建时注册 / Security 7 / starter 4 / Quartz / Redis 白名单 等),详见 `docs/springboot4-概要设计.md` §6。下表为本分支**新增 HD-29~HD-40**。

| # | 决策 | 理由 | 影响 |
|---|---|---|---|
| **HD-29** | **两端独立 `tsconfig.json`,不共享** | web-ui 跑浏览器(Vite 6 / `esnext` + `element-plus/global`),web-mp 跑 uni-app(Vite 5 / `ES2020` + `types: []`);共享会引入 `types` 冲突 | 两份配置需分别维护 |
| **HD-30** | **类型集中在 `src/types/`,按后端模块镜像** | `api/*.ts` 保持精简;跨模块复用(如 `ListResult<T>`)方便 | 需在 `types/api/index.ts` 统一登记 |
| **HD-31** | **`strict` 按端分级** | uni-app 3 alpha 类型定义不全,一次性 strict 会引入大量 `@ts-ignore` | mp 端类型保护弱(M5 收紧) |
| **HD-32** | **类型检查不阻塞构建** | 迁移期强校验阻塞开发;Vite 打包不需要类型信息 | 类型错误可能进生产(R35);M6 入 CI |
| **HD-33** | **`utils/generator/*.js` 保留 JS** | 用大量字符串拼接生成 HTML/Vue,类型化收益极低 | 与 TS 源码混存(R38);`tsconfig.exclude` 隔离 |
| **HD-34** | **`<script setup lang="ts">` 强制** | 组合式 API 类型推导最好 | 存量组件已全量迁移 |
| **HD-35** | **全局挂载方法用 `ComponentCustomProperties` 声明** | `app.config.globalProperties.useDict` 等在模板里要用 | `global.d.ts` 需与 `main.ts` 挂载清单同步 |
| **HD-36** | **复用 `springboot3_TypeScript` 的 `web-ui` 整目录** | 避免重复劳动与两套实现漂移 | 需手工恢复 `basic.scss`(ADR-40)+ 逐目录复核(R45) |
| **HD-37** | **`web-mp` 保留 `sync-config.cjs` / `scripts/*.cjs`** | Node 脚本,不参与前端类型检查 | 靠 `tsconfig.exclude` 的 `scripts` 项隔离 |
| **HD-38** | **TS 化不改后端契约** | 前端类型只是镜像;改契约会牵动后端与 DB | 后端与 DB 零改动(AC-79) |
| **HD-39** | **Jackson 3 不需要前端适配** | JSON 结构未变(字段名/日期/null/大数策略一致) | 前端只需沿用 SB3 的类型定义(ADR-41) |
| **HD-40** | **不引入 ESLint(当前)** | 未配置 lint;避免一次性引入过多工具链 | 代码风格靠 review(A-37 后续可加) |

---

## 6. 部署视图(⬆️ 与 `springboot4` 一致;🔶 前端产物)

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
        │  ⬆️ 运行时 JDK 21 / Jackson 3   │
        │  ⬆️ Security 7 / springdoc 3.1  │
        │  ⬆️ Quartz RAMJobStore          │
        │  端口:8080 / profile: dev|prod  │
        └──────┬──────────────┬──────────┘
               │              │
               ▼              ▼
        ┌────────────┐   ┌──────────────────────┐
        │PostgreSQL  │   │  Redis(可选)         │
        │15+(basicdb)│   │  ⬆️ Jackson 3 + PTV  │
        │34 表       │   │                      │
        └────────────┘   └──────────────────────┘

        web-mp:  🔶 src 为 TS,编译产物与 springboot4 同构
        ┌────────────────────────────┐
        │  HBuilder / 微信开发者工具  │
        │  编译产物:dist/dev/mp-weixin│
        │  🔶 类型在编译期擦除        │
        └────────────────────────────┘
```

### 6.1 启动顺序

1. **JDK 21** + IDE 设为 21
2. PostgreSQL 启动 + `psql -f file-sql/basicdb-vue3.sql`(34 表)
3. (可选)Redis 启动
4. `mvn clean compile`(7 模块)→ 0 ERROR
5. `mvn spring-boot:run -pl basic-admin-api`
6. **(可选)类型检查**:`cd web-ui && npx vue-tsc --noEmit`;`cd web-mp && pnpm typecheck`
7. `pnpm dev` / `pnpm dev:h5` 启动前端
8. `admin / admin123` 登录(kaptcha 字符验证码)

### 6.2 构建产物对比(🔶 校验项)

| 项 | `springboot4`(JS) | 本分支(TS) | 预期差异 |
|---|---|---|---|
| web-ui 源码 | `.js` | `.ts` | — |
| web-ui 产物 | `dist/` | `dist/` | **体积差异 < 5%**(类型擦除) |
| web-mp 源码 | `.js` | `.ts` | — |
| web-mp 产物 | `dist/dev/mp-weixin` | 同左 | 同上 |
| 运行时行为 | — | — | **完全等价** |

### 6.3 并发与恢复(⬆️ 一致)

- Tomcat `max-threads=800`;Druid `min-idle=5` / `max-active=20`
- Quartz **RAMJobStore** → 重启后动态任务丢失(⬆️ 已知取舍)
- 优雅停机:`actuator/shutdown`
- **🔶 前端无状态**:TS 化不影响任何并发/恢复语义

---

## 7. 风险与待定项

> **P1~P30 继承自 `springboot4`**(Jackson 3 / 循环依赖 / Security 7 / RAMJobStore / Redis 脏数据 等),详见 `docs/springboot4-概要设计.md` §8。下表为**本分支新增**。

| 编号 | 项 | 影响 | 归属文档 |
|---|---|---|---|
| **P31** | **`vue-tsc` 不阻塞构建,类型错误可能进生产** | **中** | 架构 R35;A-34 |
| **P32** | **`web-mp` `strict: false`,类型保护弱** | **中** | 架构 R36;A-35 |
| **P33** | **第三方库类型缺失** | 中 | 架构 R37 |
| **P34** | **`types/` 契约与后端不同步时 TS 无法发现** | **中** | 架构 R43;场景 S26 |
| **P35** | **`utils/generator/*.js` 与 TS 混存易误改** | 低 | 架构 R38;A-38 |
| **P36** | **旧 `.js` 未删净会留双份** | 低 | 架构 R39 |
| **P37** | **版本漂移** | 低 | 架构 R40 |
| **P38** | **`isolatedModules` 要求 `import type`** | 低 | 架构 R41 |
| **P39** | **`web-ui` 无 `typecheck` 脚本** | 低 | 架构 R42;A-33 |
| **P40** | **代码生成器产出的前端仍是 JS 风格** | **中** | 架构 R44;A-36 |
| **P41** | **整目录复制可能带入 SB3 时代差异** | 中 | 架构 R45;A-40 |
| **P42** | **未引入 ESLint,风格靠 review** | 低 | HD-40;A-37 |
| **P43** | **`web-ui` strict 下历史 `@ts-ignore` 数量未统计** | 低 | 建议统计并逐步消除 |
| **P44** | **移动端无工作流/完整业务** | 中 | 继承(仅登录+通知+个人中心) |
| **P45** | **前端无单元测试** | 中 | A-39 |

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `springboot4` 概要设计(v0.1)为基线(HD-01~28 / P1~30 继承),叠加「前端全量 TS 化」专题:§2.5 前端 TS 组件职责(两端分层表 / **14 项 tsconfig 差异** / 检查执行链 / 依赖关系图 / **与 Jackson 3 的关系**)、§3.7 TS 开发与迁移流程(标准流程 / 字段变更传导 / 迁移 10 步 / 6 条注意)、§4.1 类型契约清单(22 个)、HD-29~HD-40、§6.2 产物对比、§6.3 并发、P31~P45 |
