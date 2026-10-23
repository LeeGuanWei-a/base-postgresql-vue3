# 基础平台(base-postgresql-vue3) 架构方案 — `springboot4_TypeScript` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot4_TypeScript`** 分支(基于 RuoYi-Vue **3.9.2** 改造)
> 上游依赖:`springboot4`(**Spring Boot 4.1.1 / JDK 21 / Jackson 3 / Security 7**)→ `springboot3` → `master`
> 本分支 = **`springboot4` 基线 + 前端两端全量 TypeScript 化**;**后端代码与数据库零改动**。
> **本分支是仓库技术栈最新最全的组合**(JDK 21 + SB 4.1.1 + Jackson 3 + Security 7 + 前端 TS)。

---

## 1. 系统定位

**一句话定位**:面向中小团队的后台管理基础平台,提供「用户/角色/菜单/部门/字典/参数/定时任务/代码生成/监控」一整套开箱即用的 RBAC 后台能力,并提供 uni-app 移动端;**本分支在 Spring Boot 4.1.1 + JDK 21 的最新后端基线上,把 PC + 移动两端前端从 JavaScript 全量改写为 TypeScript**,用编译期类型契约约束前后端接口。

| 维度 | 内容 |
|---|---|
| 使用方 | 内部业务开发者(Java 后端 + Web 前端 + 移动端开发者),最终被业务方运营人员使用 |
| 触发方式 | PC 浏览器、微信小程序、Android/iOS App,均通过 HTTP/JSON 调用后端 REST |
| 数据形态 | PostgreSQL(34 张表) + 本地文件上传 + Redis(Jackson 3 序列化) |
| 范围边界 | RBAC 系统管理、字典/参数/通知、定时任务、代码生成、登录/锁屏、操作日志、PC + 移动两套 **TypeScript** 前端 |
| 不做清单(本分支) | 业务流程引擎(走 `springboot4-extend-flowable`)、行为验证码(走 tianai 系列)、多租户、SSO 联邦、微服务拆分 |

### 1.1 核心用户动作必做项

- 登录 / 注销(账号 + 密码 + RSA + **kaptcha 2.3.3** 字符验证码)
- 用户/角色/菜单/部门/岗位/字典 基础数据维护
- 动态路由 + 按钮级权限
- 通知公告 + 文件上传下载
- 定时任务(Cron)在线管理
- 代码生成
- 锁屏 → 解锁
- PC 端 + 移动端 同一套后端接口
- **两端前端均通过 `vue-tsc` 类型检查**

---

## 2. 约束与质量属性

| 属性 | 目标 | 标注 |
|---|---|---|
| **JDK** | **JDK 21**(⬆️ 继承 `springboot4`) | 强约束 |
| **Spring Boot** | **4.1.1**(⬆️ 继承) | 强约束 |
| **Spring Framework** | **7.0.9**(⬆️ 继承) | 强约束 |
| **Spring Security** | **7.1.1**;`getAuthorities()` 不得返回 null(⬆️ 继承) | 强约束 |
| **Jackson** | **3.1.5**;核心/绑定 `tools.jackson.*`,**注解仍 `com.fasterxml.jackson.annotation.*`**(⬆️ 继承) | 强约束 |
| **Jakarta** | `jakarta.servlet` **6.1.0**;实测 **114 处 jakarta import,0 处 `javax.servlet`** | ⬆️ 继承 |
| **API 文档** | springdoc-openapi **3.1.1**;`/swagger-ui/index.html` + `/v3/api-docs` | ⬆️ 继承 |
| **前端语言** | **全量 TypeScript**;`web-ui`:`typescript@5.6.3` + `vue-tsc@2.1.10` + `@vue/tsconfig@0.5.1`;`web-mp`:`typescript@^5.4.5` + `vue-tsc@^2.0.19` | **本分支唯一新增强约束** |
| **TS 严格度** | `web-ui`:`strict: true`;`web-mp`:`strict: false` + `noImplicitAny: false` + `isolatedModules: true` | 分端分级(ADR-31) |
| **持久层** | MyBatis-Plus **3.5.17**(`spring-boot4-starter`)+ MyBatis-Spring-Boot **4.1.0** + PageHelper **4.1.1** | ⬆️ 继承 |
| **连接池** | Druid **1.2.28**(`druid-spring-boot-4-starter`) | ⬆️ 继承 |
| **数据库** | PostgreSQL ≥ 15.1,统一 `basicdb` schema,**34 张表 + 5 函数** | **本分支零变更** |
| **定时任务** | **`spring-boot-starter-quartz` + RAMJobStore** | ⬆️ 继承 |
| 验证码 | kaptcha **2.3.3** | ⬆️ 继承 |
| 鉴权 | JWT(无状态)+ BCrypt | Token 30 分钟 |
| 性能 | TS 类型**编译期擦除,运行时零开销** | |
| 审计 | 写操作落 `sys_oper_log`,登录事件落 `sys_logininfor` | 不可关闭 |
| 部署 | 单体 jar,`tomcat.max-threads=800`;**运行时 JDK 21** | |
| 模块数 | **7 个 Maven 子模块**(无 `extend-flowable`) | |

**最强约束**:① **JDK 21 + SB 4.1.1 + Jackson 3 + Security 7 不可回退**;② **前端不再新增 `.js` 源文件**(`web-ui/src/utils/generator/*.js` 6 个为唯一例外);③ **数据库零变更**;④ **后端与 `springboot4` 逐字节一致**;⑤ **接口契约零变更**(TS 化只加类型,不改 HTTP)。

---

## 3. 总体架构

### 3.1 容器视图(🔶 = 本分支;⬆️ = `springboot4` 继承)

```
┌───────────────────────────────────────────────────────────────┐
│  用户端(🔶 两端 TypeScript)                                     │
│  ┌──────────────────────────┐  ┌────────────────────────────┐ │
│  │  web-ui (PC)             │  │  web-mp (移动:uni-app 3)    │ │
│  │  Vue 3.5.26              │  │  H5/小程序/Android/iOS      │ │
│  │  Element Plus 2.13.1     │  │  uview-plus 3.3.62         │ │
│  │  Vite 6.4.1              │  │  Vite 5.2.8                │ │
│  │  🔶 TypeScript 5.6.3     │  │  🔶 TypeScript ^5.4.5      │ │
│  │  🔶 vue-tsc 2.1.10       │  │  🔶 vue-tsc ^2.0.19        │ │
│  │     (strict: true)       │  │     (strict: false 起步)    │ │
│  │  🔶 79 .ts + 6 .js       │  │  🔶 19 .ts + 0 .js         │ │
│  │  🔶 types/ 22 个类型文件  │  │  🔶 types/global.d.ts      │ │
│  └────────┬─────────────────┘  └──────────────┬─────────────┘ │
│           │ HTTPS / JSON(⬆️ Jackson 3 序列化)                │
└───────────┼───────────────────────────────────┼───────────────┘
            ▼                                   ▼
┌───────────────────────────────────────────────────────────────┐
│  ⬆️ Spring Boot 4.1.1 (basic-admin-api) · JDK 21              │
│  ┌─────────────────────────────────────────────────────────┐  │
│  │  ⬆️ Filter 链(jakarta 6.1.0)                            │  │
│  │  ⬆️ Security 7.1.1(SecurityFilterChain + getAuthorities)│  │
│  │  Controller(24 个平台 Controller)                       │  │
│  │  ↓                                                       │  │
│  │  Service(basic-admin-core / extend-quartz / business)   │  │
│  │  ↓                                                       │  │
│  │  ⬆️ MyBatis-Plus 3.5.17 + PageHelper 4.1.1 + Druid      │  │
│  └────────────────────────┬────────────────────────────────┘  │
│  ┌────────────────────────┴────────────────────────────────┐  │
│  │ ⬆️ AOP:LogAspect / DataScopeAspect / RateLimiter /       │  │
│  │       RepeatSubmit / DataSource                          │  │
│  │ ⬆️ Jackson 3.1.5(tools.jackson.*)                       │  │
│  │      + BigNumberModuleConfig(JsonMapperBuilderCustomizer)│  │
│  │ ⬆️ springdoc 3.1.1 · yauaa 8.2.0 · kaptcha 2.3.3        │  │
│  │ ⬆️ Quartz(RAMJobStore)                                   │  │
│  └─────────────────────────────────────────────────────────┘  │
└──────────┬──────────────────────────────────┬──────────────────┘
           ▼                                  ▼
   ┌──────────────────┐              ┌──────────────────────────┐
   │ PostgreSQL ≥15.1 │              │ Redis(可选)              │
   │ schema=basicdb   │              │ 字典/Token/限流           │
   │ 34 张表 + 5 函数 │              │ ⬆️ Jackson 3 + PTV 白名单 │
   │ 🔶 与 springboot4 │              │                          │
   │    完全一致       │              │                          │
   └──────────────────┘              └──────────────────────────┘
```

### 3.2 模块清单(7 个 Maven 子模块,⬆️ 与 `springboot4` 一致)

| 模块 | Maven artifact | 职责 | 本分支 |
|---|---|---|---|
| `basic-admin-api` | `basic-admin-api` | 启动入口 + 24 个平台 Controller | ⬆️ 零改动 |
| `basic-admin-core` | `basic-admin-core` | 业务 Service/Mapper/Domain | ⬆️ 零改动 |
| `basic-framework` | `basic-framework` | Security 7 / Jackson 3 / Redis / MP / springdoc | ⬆️ 零改动 |
| `basic-common` | `basic-common` | 工具/注解/枚举/基类(`JsonUtils` Jackson 3) | ⬆️ 零改动 |
| `business-core` | `business-core` | 业务扩展点(`Demo`) | ⬆️ 零改动 |
| `extend-quartz` | `extend-quartz` | Quartz(starter + RAMJobStore) | ⬆️ 零改动 |
| `extend-generator` | `extend-generator` | 代码生成(⚠️ 模板仍产 SB2 风格) | ⬆️ 零改动 |
| **`web-ui`** | (非 Maven) | PC 前端 | **🔶 全量 TS**(79 `.ts` / 6 `.js` / 95 `.vue`) |
| **`web-mp`** | (非 Maven) | 移动前端 | **🔶 全量 TS**(19 `.ts` / 0 `.js`) |

### 3.3 数据模型核心表(详细见《数据库设计》)

> **🔶 本分支与 `springboot4` / `master` 完全相同**:34 张表(`gen_*` 2 + `qrtz_*` 11 + `sys_*` 21),`basicdb` schema,5 个函数。
> **前端 TS 化不产生任何数据库变更**。

| 域 | 表 | 说明 |
|---|---|---|
| 鉴权 | `sys_user` / `sys_role` / `sys_menu` / `sys_user_role` / `sys_role_menu` / `sys_role_dept` / `sys_user_post` | RBAC |
| 组织 | `sys_dept` / `sys_post` | 部门树 + 岗位 |
| 系统 | `sys_config` / `sys_dict_type` / `sys_dict_data` / `sys_files` / `sys_i18n` | 参数/字典/文件/国际化 |
| 通知 | `sys_notice` / `sys_notice_read` | 公告 + 已读 |
| 审计 | `sys_oper_log` / `sys_logininfor` | 操作/登录日志 |
| 示例 | `sys_demo` | `business-core` 示例 |
| 定时 | `qrtz_*`(11 张) | **RAMJobStore 不使用**(表保留) |
| 代码生成 | `gen_table` / `gen_table_column` | 元数据 |

### 3.4 关键流程(⬆️ 与 `springboot4` 一致;🔶 前端调用处加类型)

#### 登录流程(kaptcha + TS 类型标注)

```
[前端 TS]                      [后端 SB 4.1.1]                    [DB / Redis]
  │  getCodeImg(): Promise<CaptchaInfoResult>          🔶
  │ ─────────────────────────▶  CaptchaController(kaptcha 2.3.3)
  │ ◀─────────────────────────  {uuid, img, captchaEnabled}
  │                              (⬆️ Jackson 3 序列化)
  │  用户输入图像字符
  │
  │  login(u, p, code, uuid): Promise<LoginInfoResult> 🔶
  │ ─────────────────────────▶  SysLoginController
  │                             │ ① RSA 解密
  │                             │ ② validateCaptcha
  │                             │ ③ loadUserByUsername ────▶│
  │                             │ ④ BCrypt.matches
  │                             │ ⑤ createToken(⬆️ Jackson 3 写 Redis)
  │                             │ ⑥ AsyncFactory(yauaa 解析 UA)
  │ ◀─────────────────────────  {token}                  ────▶ sys_logininfor
  │
  │  getInfo(): Promise<UserInfoResult>                 🔶
  │ ─────────────────────────▶  ⬆️ getAuthorities() 返回空集合
  │  getRouters(): Promise<RouterVO[]>                  🔶
```

> **关键**:本分支是 **kaptcha 字符验证码**(`LoginBody` 只有 `username`/`password`/`code`/`uuid`,**无** `captchaData`);`CaptchaInfoResult` 含 `uuid` / `img` / `captchaEnabled`。

#### 锁屏 / 解锁(⬆️ 一致;🔶 `lock.ts`)

```
PC 端 store/modules/lock.ts 维护 lock 状态      🔶 (.ts)
  触发:Header 操作菜单 → 锁定
  解锁:输入密码 → POST /login(复用 token,只校验密码)
```

#### 定时任务调度(⬆️ 一致:starter + RAMJobStore)

```
前端 TS → SysJobController.changeStatus / run / list
        ↓
SysJobServiceImpl → ScheduleUtils → SchedulerFactoryBean(⬆️ starter 自动装配)
        ↓
RAMJobStore(内存);任务定义仍落 sys_job;执行日志落 sys_job_log
```

#### 🔶 请求链路(两处变化:Jackson 3 + 前端 TS 类型)

```
HTTP 请求
   │
   ▼
⬆️ CorsFilter → XssFilter → JwtFilter(jakarta 6)
   │
   ▼
⬆️ Security 7 链(getAuthorities() 空集合兜底)
   │
   ▼
DispatcherServlet → Controller(⬆️ 后端零改动)
   │
   ▼ JSON 响应
⬆️ Jackson 3 HttpMessageConverter(JacksonJsonHttpMessageConverter)
   │   · BigNumberModuleConfig 注入 Long/BigInteger/BigDecimal 序列化器
   ▼
🔶 前端 types/api/** 的 interface 校验字段(编译期)
   │
   ▼
🔶 views/**/*.vue <script setup lang="ts"> 获得类型提示
```

### 3.5 状态机(⬆️ 与 `springboot4` 一致)

| 对象 | 状态 | 流转 |
|---|---|---|
| 定时任务 | `NORMAL` / `PAUSED` | changeStatus |
| 用户 | `0=正常` / `1=停用` | changeStatus |
| 通知已读 | 未读 / 已读 | `sys_notice_read` 唯一 |
| **🔶 TS 编译产物** | `dev(.ts 源码)` → `bundle(编译后)` | `vite build` 编译期擦除类型 |

### 3.6 🔶 前端 TypeScript 工程结构(本分支核心)

#### 3.6.1 web-ui(PC,`strict: true`)

```
web-ui/
├── tsconfig.json                  🔶 新增(target esnext / moduleResolution bundler
│                                      / strict true / @/* → src/*
│                                      / types: [node, vite/client, element-plus/global]
│                                      / include: src/**/*.ts|vue, vite/**, vite.config.ts
│                                      / exclude: node_modules, dist, src/**/*.js)
├── auto-imports.d.ts              🔶 新增(unplugin-auto-import 生成)
├── vite.config.ts                 🔶 由 vite.config.js 改名
├── package.json                   🔶 devDeps:typescript@5.6.3 / vue-tsc@2.1.10
│                                      / @vue/tsconfig@0.5.1(均精确版本)
└── src/
    ├── main.ts / permission.ts / settings.ts        🔶 (原 .js)
    ├── api/                       🔶 19 个 .ts(login / menu / monitor.* / system.* / tool.gen)
    ├── types/                     🔶 22 个文件(新增目录)
    │   ├── api/                   → common / index / login / menu
    │   │                          → monitor/{cache,job,jobLog,logininfor,online,operlog}
    │   │                          → system/{config,dept,dict,menu,notice,post,role,user}
    │   │                          → tool/gen
    │   ├── components.d.ts        → 全局组件类型(自动生成,禁改)
    │   ├── global.d.ts            → window / ComponentCustomProperties
    │   └── index.ts               → 统一 re-export 出口
    ├── utils/                     🔶 13 个 .ts(auth / common / dict / dynamicTitle
    │   │                              / errorCode / index / jsencrypt / passwordRule
    │   │                              / permission / request / scroll-to / theme / validate)
    │   └── generator/             ⚠️ 6 个 .js 保留(config / css / drawingDefault / html / js / render)
    ├── store/                     🔶 8 个 .ts(index + modules/{app,dict,lock,permission,settings,tagsView,user})
    ├── plugins/                   🔶 6 个 .ts(auth / cache / download / index / modal / tab)
    ├── directive/                 🔶 4 个 .ts(index / common/copyText / permission/{hasPermi,hasRole})
    ├── router/index.ts            🔶
    ├── layout/components/index.ts 🔶
    ├── components/
    │   ├── IconSelect/requireIcons.ts   🔶
    │   └── SvgIcon/svgicon.ts           🔶
    ├── assets/styles/basic.scss   🔶 从 springboot4 单独恢复(--el-color-primary: #1890ff)
    └── views/**/*.vue             🔶 95 个 .vue,全部 <script setup lang="ts">
```

**统计口径(实测)**:`web-ui/src` = **79 个 `.ts`** + **6 个 `.js`**(全在 `utils/generator/`)+ **95 个 `.vue`**;`web-ui/src/types` = **22 个文件**。

#### 3.6.2 web-mp(移动,`strict: false` 起步)

```
web-mp/
├── tsconfig.json              🔶 新增(target ES2020 / moduleResolution Bundler
│                                  / strict false / noImplicitAny false
│                                  / isolatedModules true / types []
│                                  / include: src/**/*.ts|d.ts|tsx|vue, vite.config.ts
│                                  / exclude: node_modules, dist, unpackage, scripts)
├── vite.config.ts             🔶 由 vite.config.js 改名
├── sync-config.cjs            (保留 .cjs:Node 脚本,不参与类型检查)
├── package.json               🔶 devDeps:typescript@^5.4.5 / vue-tsc@^2.0.19
│                                  scripts.typecheck = "vue-tsc --noEmit"
└── src/                       🔶 19 个 .ts · 0 个 .js
    ├── main.ts / permission.ts / config.ts        🔶
    ├── api/                   🔶 5 个(index / login / system/{dept,role,user})
    ├── store/                 🔶 4 个(index + modules/{app,permission,user})
    ├── utils/                 🔶 5 个(auth / config / dict / jsencrypt / request)
    ├── directive/permission.ts 🔶
    ├── types/global.d.ts      🔶
    └── pages/**/*.vue         🔶 全部 <script setup lang="ts">
```

**统计口径(实测)**:`web-mp/src` = **19 个 `.ts`** + **0 个 `.js`**。

#### 3.6.3 类型分层策略

```
                   ┌────────────────────────────────┐
                   │  types/index.ts(统一出口)      │
                   └────────────┬───────────────────┘
                                │ re-export
      ┌─────────────────────────┼─────────────────────────┐
      ▼                         ▼                         ▼
┌──────────────┐        ┌──────────────┐         ┌─────────────────┐
│ types/api/   │        │ types/       │         │ types/global.d.ts│
│ 19 个契约文件 │        │ components   │         │ 全局挂载 / window │
│ (镜像后端模块)│        │ .d.ts(自动)  │         │ 扩展声明          │
└──────┬───────┘        └──────────────┘         └─────────────────┘
       │ 被 import
       ▼
┌──────────────────────────────────────────────────────────────┐
│  api/*.ts(19 个,函数签名显式返回类型)                         │
│    export function login(...): Promise<LoginInfoResult>      │
│    export function listUser(q: SysUserQuery): Promise<ListResult<SysUser>> │
└──────────────────────┬───────────────────────────────────────┘
                       │ 被调用
                       ▼
┌──────────────────────────────────────────────────────────────┐
│  views/**/*.vue  <script setup lang="ts">  → ref<T>() / 类型提示 │
└──────────────────────────────────────────────────────────────┘
```

**规则**:① `types/api/**` 只放接口契约;② `components.d.ts` 自动生成,**禁手工改**;③ `global.d.ts` 声明 `app.config.globalProperties` 挂载方法;④ `api/**` 函数**必须显式标注返回类型**;⑤ `.vue` 统一 `<script setup lang="ts">`。

### 3.7 🔶 与 `springboot4` 的边界(必须明确)

| 维度 | `springboot4` | 本分支 | 是否改后端 |
|---|---|---|---|
| 后端 7 模块 | JDK 21 / SB 4.1.1 / Jackson 3 / Security 7 | **完全相同** | ❌ |
| 数据库 34 表 | `basicdb` | **完全相同** | ❌ |
| HTTP 接口契约 | — | **完全相同** | ❌ |
| `web-ui` 语言 | JS | **TS**(79 `.ts` / 6 `.js` / 95 `.vue`) | ❌ |
| `web-mp` 语言 | JS | **TS**(19 `.ts` / 0 `.js`) | ❌ |
| 验证码 | kaptcha 2.3.3 | **kaptcha 2.3.3**(未换 tianai) | ❌ |
| 主题色 | `--el-color-primary: #1890ff` | **相同**(从 `springboot4` 单独恢复) | ❌ |

---

## 4. 关键决策 ADR

> **ADR-01~ADR-30 继承自 `springboot4`**(基础底座 / Jackson 3 / Security 7 / starter 4 / Quartz / 不引 fastjson2 等),详见 `docs/springboot4-架构方案.md` §4。下表为本分支**新增 ADR-31~ADR-42**。

| # | 决策点 | 采纳方案 | 备选 + 放弃原因 | 是否演进锁死 |
|---|---|---|---|---|
| **ADR-31** | **前端语言** | **全量 TypeScript**(两端都换) | 保持 JS:无类型保护;只换 web-ui:两端不一致;JSDoc + `checkJs`:类型能力弱 | **是** — 本分支主线 |
| **ADR-32** | **TS 成果来源** | **复用 `springboot3_TypeScript` 的 `web-ui` 整目录**(直接替换) | 在 `springboot4` 上重做一遍 TS 化:重复劳动、两套实现易漂移 | 否 |
| **ADR-33** | **严格度分级** | `web-ui`:`strict: true`;`web-mp`:`strict: false` + `noImplicitAny: false` + `isolatedModules: true` | 两端都 strict:uni-app 3 alpha 类型不全,会引入大量 `@ts-ignore`;两端都宽松:PC 端失去类型价值 | 否 — web-mp 后续可收紧 |
| **ADR-34** | **类型定义位置** | 独立 `src/types/`,**按后端模块镜像分层** | 写在 `api/*.ts` 内:文件臃肿;放 `@types/`:与源码分离 | **是** |
| **ADR-35** | **`.vue` 写法** | `<script setup lang="ts">` 组合式 API | Options API + `defineComponent`:样板多、类型推导差 | **是** |
| **ADR-36** | **代码生成器模板文件** | `web-ui/src/utils/generator/*.js` **保留 JS**(6 个) | 改 TS:大量字符串拼接,类型化收益极低;`tsconfig` 用 `exclude: ["src/**/*.js"]` 隔离 | 否 |
| **ADR-37** | **类型检查时机** | **不阻塞 `vite dev` / `vite build`**;`vue-tsc` 独立命令 | 构建时强校验:迁移期阻塞开发;完全不做:失去 TS 意义 | 否 — 后续可并入 CI |
| **ADR-38** | **自动导入的类型化** | `unplugin-auto-import` 生成 `auto-imports.d.ts`,`tsconfig.include` 显式纳入 | 手工维护:易漂移 | 否 |
| **ADR-39** | **全局挂载方法类型** | `types/global.d.ts` 的 `ComponentCustomProperties` 声明 | 各组件 `@ts-ignore`:污染源码 | 否 |
| **ADR-40** | **主题色保留** | 从 `springboot4` 单独 checkout `basic.scss`(`#1890ff`) | 直接用 `springboot3_TypeScript` 的样式:丢失蓝色品牌色 | 否 |
| **ADR-41** | **Jackson 3 契约不变** | 前端 `types/` 只镜像 **JSON 结构**,不关心 Jackson 版本 | 试图在前端"适配 Jackson 3":无意义(JSON 结构未变) | **是** |
| **ADR-42** | **不做 Flowable / 验证码改造** | 保持平台纯 RBAC | 顺带做:分支职责重叠,不利于按需取用 | **是** |

---

## 5. 里程碑

| 阶段 | 内容 | 验收 | 触发升级的信号 |
|---|---|---|---|
| **M0(已交付,`master`)** | RuoYi 3.8.2 基础 + SQL 迁 PostgreSQL + 锁屏 | 7 模块;34 表可恢复 | — |
| **M1(已交付,`springboot3`)** | SB 2.5.14 → 3.5.16 / JDK 11 → 17(Jakarta / Security 6 / springdoc 2.9) | JDK 17 下 0 ERROR | — |
| **M2(已交付,`springboot4`)** | SB 3.5.16 → 4.1.1 / JDK 17 → 21(**Jackson 3** / Security 7 / starter 4 / Quartz starter) | JDK 21 下 0 ERROR;`grep com.fasterxml.jackson` 仅 annotation | — |
| **M3(已交付,本分支)** | **前端两端全量 TypeScript 化**:`web-ui` 79 `.ts` + `web-mp` 19 `.ts`;两端 `tsconfig.json` + `vite.config.ts`;`types/` 22 个;`vue-tsc` 工具链;保留 6 个 generator `.js`;恢复 `basic.scss` 主题色 | `vue-tsc --noEmit` 0 error(两端);`pnpm dev` / `dev:h5` 可启动;登录/CRUD 全链路回归 | — |
| **M4(下一轮)** | 移动端:动态菜单 / 国际化 / WebSocket 推送 / 锁屏 / 个人中心编辑 | 见 web-mp 待办 | 业务方提出 >5 个角色/菜单变更需求 |
| **M5(可选)** | **`web-mp` 严格度收紧**:`strict: false` → `true` | `vue-tsc --noEmit` 0 error | uni-app 类型完善 / 团队熟练度提升 |
| **M6(可选)** | **`vue-tsc` 并入 CI** + `web-ui` 加 `typecheck` 脚本 | pre-commit 阻断类型错误 | 团队规模扩大需要硬门禁 |
| **M7(可选)** | **代码生成器加 TS 模板**(否则每次生成需手工 ts 化) | 生成物直接是 TS | 生成模块频率上升 |
| **M8(可选)** | deprecation 警告清理(SB4 侧残留) | 编译告警归零 | 团队对告警容忍度下降 |
| **M9(分支)** | 流程引擎(Flowable 8) | 单独分支 `springboot4-extend-flowable` | 出现审批流需求 |
| **M10(分支)** | 行为验证码(tianai) | 单独分支(目前无 `springboot4-tianai-captcha`) | 需要更强验证码 |
| **M11(分支)** | SSO:接入 keycloak 联邦 | 账号互通 | 出现第二个业务系统需统一登录 |

**本次不做,直到有信号**:微服务拆分、K8s 化、灰度发布、APM 探针、消息中心抽象、GraalVM 原生镜像、前端单元测试。

---

## 6. 审查与遗留风险

### 6.1 已覆盖

| 维度 | 设计 |
|---|---|
| 失败设计 | `GlobalExceptionHandler` + `R/AjaxResult`;`ServiceException` / `UserPasswordNotMatchException` / `CaptchaException` |
| 幂等 | `@RepeatSubmit` + `RepeatSubmitInterceptor` |
| 权限 | 菜单级 + 按钮级 + 数据级;Security 7 严格化已适配 |
| 一致性 | 关系型事务;异步日志 `AsyncManager` 隔离 |
| 可观测 | `sys_oper_log` / `sys_logininfor` / springdoc 3.1 / Druid / `actuator/shutdown` |
| **🔶 类型安全** | 两端 `tsconfig` + `vue-tsc`;`types/` 分层;`api` 显式返回类型;`global.d.ts` |

### 6.2 风险清单

> **R1~R34 继承自 `springboot4`**(Jackson 2/3 混用 / 运行时注册 Module / 循环依赖 / 异常体系 / `NumberSerializer` / Security 7 NPE / Quartz RAMJobStore / Redis 脏数据 / 多态安全 / springdoc 兼容 / MP-pagehelper 跨版本 / `WebMvcConfigurer` 废弃 / Druid 暴露 / 生成器模板 等),详见 `docs/springboot4-架构方案.md` §6.2。下表为本分支**新增 R35~R45**。

| # | 风险 | 影响 | 缓解 |
|---|---|---|---|
| **R35** | **`vue-tsc` 不阻塞构建,类型错误可能进生产** | **中** | ADR-37;M6 计划入 CI;当前靠本地自觉 |
| **R36** | **`web-mp` `strict: false`,类型保护弱于 PC 端** | **中** | ADR-33 取舍;M5 计划收紧 |
| **R37** | **第三方库类型缺失**(uview-plus / unplugin 系) | 中 | `types/global.d.ts` 补 `declare module`;`skipLibCheck: true` |
| **R38** | **`utils/generator/*.js` 6 个 JS 与 TS 混存,易被误改** | 低 | ADR-36 + `tsconfig.exclude`;README 已列出 |
| **R39** | **旧 `.js` 未删净会留双份源码** | 低 | 已核验:`web-ui/src` 仅 6 个 `.js`(全 generator),`web-mp/src` 0 个 |
| **R40** | **`@vue/tsconfig@0.5.1` 与 `typescript@5.6.3` 版本漂移** | 低 | 锁精确版本;升级需同步验证 |
| **R41** | **`isolatedModules: true`(web-mp)要求类型导入用 `import type`** | 低 | 统一 `import type` |
| **R42** | **`web-ui` 无 `typecheck` npm script** | 低 | M6 计划补齐 |
| **R43** | **`types/` 契约与后端不同步时 TS 无法发现** | **中** | 字段变更必须同步改契约;列入 code review checklist |
| **R44** | **代码生成器产出的前端仍是 JS 风格** | **中** | `extend-generator` 模板未改(R34);生成后需手工 ts 化;M7 |
| **R45** | **整目录复制可能带入 SB3 时代前端差异** | 中 | 已恢复 `basic.scss`;建议逐目录 diff 复核 |

---

## 7. 参考来源

- `README.md`(仓库根,本分支)
- **上游基线文档**:
  - `docs/springboot4-架构方案.md` — 本分支的 SB4 基线(JDK 21 / Jackson 3 / Security 7),**ADR-01~30 与 R1~R34 均在彼处**
  - `docs/springboot3_TypeScript-架构方案.md` — 同主题的 **SB3 版本**(本分支 TS 成果的来源),可对比两代差异
  - `docs/springboot4-extend-flowable-架构方案.md` — 同基线的 Flowable 分支
- 兄弟分支:`springboot4`(本分支去掉 TS 化)、`springboot3_TypeScript`(SB3 的 TS 版)、`springboot4-extend-flowable`(本分支 + Flowable)
- TypeScript 5.6 / vue-tsc 2.1 / `@vue/tsconfig` 0.5 官方文档
- Jackson 3 迁移指南(⬆️ 后端已迁,前端只关心 JSON 结构)

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `springboot4` 架构方案(v0.1)为基线(ADR-01~30 / R1~R34 继承),叠加「前端全量 TypeScript 化」专题:§2 约束(严格度分端 / 文件统计口径)、§3.1 容器视图(SB4 标 ⬆️ + TS 标 🔶)、§3.4 登录流程(kaptcha + TS 标注)、§3.6 前端 TS 工程结构(两端目录树 / 类型分层图 / 实测统计)、§3.7 与 springboot4 的边界(7 行)、ADR-31~ADR-42 共 12 条、M3 TS 里程碑 + M5/M6/M7 收紧计划、R35~R45 共 11 条 |
