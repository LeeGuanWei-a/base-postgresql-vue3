# 基础平台(base-postgresql-vue3) 详细设计(LLD) — `springboot3_TypeScript` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot3_TypeScript`** 分支
> 前置依赖:《架构方案》v0.1、《需求文档》v0.1、《概要设计》v0.1
> 本文档细化到「类 / 方法 / 关键逻辑」。
> **后端与 `springboot3` 逐字节一致**(包结构 / 实体 / 框架机制 / 核心引擎 / 校验 / 业务实现 / 审计 / 错误码 / 线程模型 / 测试 TC-01~TC-32 全部继承,本文档不再重复);**本分支新增 §1.2 前端 TS 目录结构、§2.4 前端类型模型、§3.10 前端构建与类型检查机制、§4.6 前端核心引擎(TS 重写)、§6.6 前端模块实现、§9.2 编译期错误码、§11.3 TC-51~TC-62**。
> 🔶 = 本分支改动;⬆️ = `springboot3` 继承。

---

## 1. 总体包结构

### 1.1 后端包结构(⬆️ 与 `springboot3` **逐字节一致,本分支未改一行**)

> 完整包树见 `docs/springboot3-详细设计.md` §1.1。此处仅列关键骨架与**本分支相关的 2 个类**:

```
com.basic
├── BasicApplication                       # 启动类(⬆️)
├── BasicServletInitializer               # war 适配(⬆️ jakarta)
├── web
│   ├── config/SwaggerConfig              # ⬆️ springdoc
│   └── controller
│       ├── common/CaptchaController      # ⬆️ kaptcha 2.3.3 生成(本分支未改)
│       └── system/SysLoginController     # ⬆️ 登录入口(本分支未改)
├── framework
│   ├── config/{SecurityConfig, UserAgentConfig, ResourcesConfig, DruidConfig, ...}   # ⬆️ SB3
│   ├── security/filter/JwtAuthenticationTokenFilter                                   # ⬆️ jakarta
│   ├── web/service/{SysLoginService, TokenService, ...}                               # ⬆️
│   └── manager/factory/AsyncFactory                                                   # ⬆️ yauaa
├── common
│   ├── core/domain/model/LoginBody       # ⬆️ 仅 username/password/code/uuid(无 captchaData)
│   ├── core/redis/RedisCache             # ⬆️
│   ├── exception/user/{CaptchaException, CaptchaExpireException}                      # ⬆️
│   └── utils/**                                                                       # ⬆️
├── quartz (extend-quartz)                                                             # ⬆️
├── generator (extend-generator)                                                       # ⬆️(⚠️ 模板未适配 SB3)
└── system (basic-admin-core)                                                          # ⬆️

com.core (business-core)                                                               # ⬆️
```

> **🔶 本分支未新增/未删除/未修改任何 Java 文件**;`pom.xml`、`application*.yml`、`file-sql/**` 亦未改。

### 1.2 🔶 前端 TypeScript 目录结构(本分支核心新增)

#### 1.2.1 web-ui(PC)

```
web-ui/
├── tsconfig.json                          🔶 新增 · strict: true
├── auto-imports.d.ts                      🔶 新增 · unplugin-auto-import 生成
├── vite.config.ts                         🔶 由 vite.config.js 改名
├── package.json                           🔶 devDeps:
│                                              typescript@5.6.3(pin)
│                                              vue-tsc@2.1.10(pin)
│                                              @vue/tsconfig@0.5.1(pin)
└── src/
    ├── main.ts                            🔶 原 main.js
    ├── permission.ts                      🔶 原 permission.js
    ├── settings.ts                        🔶 原 settings.js
    ├── App.vue                            🔶 <script setup lang="ts">
    │
    ├── types/                             🔶 新增目录(22 文件)
    │   ├── index.ts                       → 对外统一入口
    │   ├── api/
    │   │   ├── index.ts                   → 全部 re-export
    │   │   ├── common.ts                  → AjaxResult / R<T> / ListResult<T>
    │   │   │                                 / PageQuery / TableDataInfo / TreeSelect
    │   │   ├── login.ts                   → LoginForm / RegisterForm
    │   │   │                                 / LoginInfoResult / UserInfoResult
    │   │   │                                 / CaptchaInfoResult(kaptcha 语义)
    │   │   ├── menu.ts                    → RouterVO / MetaVO
    │   │   ├── monitor/                   → cache / job / jobLog
    │   │   │                                 / logininfor / online / operlog  (6)
    │   │   ├── system/                    → config / dept / dict / menu
    │   │   │                                 / notice / post / role / user        (8)
    │   │   └── tool/gen.ts                → GenTable / GenTableColumn
    │   ├── components.d.ts                → 全局组件类型(自动生成,禁改)
    │   └── global.d.ts                    → window / ComponentCustomProperties
    │
    ├── api/                               🔶 19 个 .ts
    │   ├── login.ts  menu.ts
    │   ├── monitor/{cache,job,jobLog,logininfor,online,operlog}.ts
    │   ├── system/{config,dept,dict/data,dict/type,menu,notice,post,role,user}.ts
    │   └── tool/gen.ts
    │
    ├── utils/                             🔶 13 个 .ts
    │   ├── auth.ts  common.ts  dict.ts  dynamicTitle.ts  errorCode.ts
    │   ├── index.ts  jsencrypt.ts  passwordRule.ts  permission.ts
    │   ├── request.ts  scroll-to.ts  theme.ts  validate.ts
    │   └── generator/                     ⚠️ 6 个 .js 保留(ADR-24)
    │       └── config.js  css.js  drawingDefault.js  html.js  js.js  render.js
    │
    ├── store/                             🔶 8 个 .ts
    │   ├── index.ts
    │   └── modules/{app,dict,lock,permission,settings,tagsView,user}.ts
    │
    ├── plugins/                           🔶 6 个 .ts
    │   └── {auth,cache,download,index,modal,tab}.ts
    │
    ├── directive/                         🔶 4 个 .ts
    │   ├── index.ts
    │   ├── common/copyText.ts
    │   └── permission/{hasPermi,hasRole}.ts
    │
    ├── router/index.ts                    🔶 原 router/index.js
    ├── layout/components/index.ts         🔶 原 index.js
    ├── components/
    │   ├── IconSelect/requireIcons.ts     🔶
    │   └── SvgIcon/svgicon.ts             🔶
    ├── assets/
    │   └── styles/basic.scss              🔶 从 springboot3 单独恢复(主题色 #1890ff)
    └── views/**/*.vue                     🔶 95 个,全部 <script setup lang="ts">
```

**统计口径**:`web-ui/src` = **79 `.ts`** + **6 `.js`** + **95 `.vue`**;`web-ui/src/types` = **22 文件**。

#### 1.2.2 web-mp(移动)

```
web-mp/
├── tsconfig.json                     🔶 新增 · strict: false / isolatedModules: true
├── vite.config.ts                    🔶 由 vite.config.js 改名
├── sync-config.cjs                   (保留 · Node 脚本,不在 include)
├── scripts/fixup-mp-components.cjs   (保留 · 同上)
├── package.json                      🔶 devDeps:typescript@^5.4.5 / vue-tsc@^2.0.19
│                                        scripts.typecheck = "vue-tsc --noEmit"
└── src/                              🔶 19 个 .ts · 0 个 .js
    ├── main.ts                       🔶
    ├── permission.ts                 🔶
    ├── config.ts                     🔶
    ├── api/
    │   ├── index.ts  login.ts
    │   └── system/{dept,role,user}.ts
    ├── store/
    │   ├── index.ts
    │   └── modules/{app,permission,user}.ts
    ├── utils/{auth,config,dict,jsencrypt,request}.ts
    ├── directive/permission.ts
    ├── types/global.d.ts
    └── pages/**/*.vue                🔶 <script setup lang="ts">
```

**统计口径**:`web-mp/src` = **19 `.ts`** + **0 `.js`**。

> **命名约定**(前端):源文件一律 `.ts` / `.vue`(**禁止新增 `.js`**);类型集中在 `src/types/`;`api/` 镜像后端 Controller 路径;`utils/generator/*.js` 是唯一例外。

---

## 2. 领域模型与枚举

### 2.1 后端实体(⬆️ 与 `springboot3` 一致,未修改)

> 16 个核心实体(`SysUser` / `SysRole` / `SysMenu` / `SysDept` / `SysPost` / `SysDictType` / `SysDictData` / `SysNotice` / `SysNoticeRead` / `SysJob` / `SysJobLog` / `SysOperLog` / `SysLogininfor` / `SysFiles` / `SysI18n` / `Demo`),主键 `ASSIGN_ID`(雪花),状态字段见 `docs/springboot3-详细设计.md` §2.1。

### 2.2 后端状态枚举(⬆️ 一致)

```java
public enum BusinessType { OTHER, INSERT, UPDATE, DELETE, GRANT, EXPORT, IMPORT, FORCE, GENCODE, CLEAN }
public enum BusinessStatus { SUCCESS(0), FAIL(1); }
public enum OperatorType { OTHER, MANAGE, MOBILE }
public enum DataSourceType { MASTER, SLAVE }
public enum UserStatus { OK("0","正常"), DISABLE("1","停用"), DELETED("2","删除"); }
public enum HttpMethod { GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE, ALL }
public enum LimitType { DEFAULT, IP, CLUSTER }
```

### 2.3 后端状态合法流转(⬆️ 一致)

| 对象 | 状态 | 合法转换 |
|---|---|---|
| 任务 | `0=运行` ↔ `1=暂停` | `changeStatus(0↔1)`;`run(→ 触发一次)` |
| 角色 / 菜单 / 通知 / 字典类型 / 用户 | `0=正常` / `1=停用` / (`2=删除`) | `add→0`;`update→0/1`;`remove→2` |

### 2.4 🔶 前端类型模型(本分支新增)

#### 2.4.1 通用响应类型(`types/api/common.ts`)

```ts
/** 后端统一返回(基础) */
export interface AjaxResult<T = any> {
  code: number
  msg: string
  data?: T
}

/** 泛型返回 */
export interface R<T = any> {
  code: number
  msg: string
  data?: T
}

/** 列表返回(PageHelper) */
export interface ListResult<T> {
  code: number
  msg: string
  rows: T[]
  total: number
}

/** 分页查询入参基类 */
export interface PageQuery {
  pageNum?: number
  pageSize?: number
  orderByColumn?: string
  isAsc?: 'asc' | 'desc'
}

/** 表格分页结构 */
export interface TableDataInfo<T> {
  total: number
  rows: T[]
  code: number
  msg: string
}

/** 树形选择 */
export interface TreeSelect {
  id: number | string
  label: string
  children?: TreeSelect[]
}
```

#### 2.4.2 登录类型(`types/api/login.ts`,🔶 kaptcha 语义)

```ts
import type { AjaxResult } from './common'
import type { SysUser } from './system/user'

/** 登录响应 */
export interface LoginInfoResult extends AjaxResult {
  token: string
}

/** 用户信息响应 */
export interface UserInfoResult extends AjaxResult {
  user: SysUser
  roles: string[]
  permissions: string[]
  isDefaultModifyPwd?: boolean
  isPasswordExpired?: boolean
}

/**
 * 验证码响应
 * ⚠️ 本分支是 kaptcha 字符验证码:
 *   - uuid 是随机串(不是 tianai 的 captchaId)
 *   - img 是 Base64 图片(**不是 null**)
 *   - 没有 captchaData 字段(那是 springboot3-tianai-captcha 分支)
 */
export interface CaptchaInfoResult extends AjaxResult {
  /** 验证码缓存 key */
  uuid: string
  /** 验证码图片 Base64 */
  img: string
  /** 验证码开关 */
  captchaEnabled: boolean
}

/** 注册提交 */
export interface RegisterForm {
  username: string
  password: string
  confirmPassword: string
  code: string
  uuid: string
}

/** 登录提交(kaptcha 版:code + uuid,无 captchaData) */
export interface LoginForm {
  username: string
  password: string
  rememberMe?: boolean | string
  code: string
  uuid: string
}
```

> 🔶 **与 `springboot3-tianai-captcha` 分支的差异**:
> | 字段 | 本分支(kaptcha) | tianai 分支 |
> |---|---|---|
> | `CaptchaInfoResult.img` | `string`(Base64) | `null` 固定 |
> | `CaptchaInfoResult.uuid` | 随机串 | 验证码 id |
> | `CaptchaInfoResult.data` | 无 | 有(背景图/模板图/滑块图) |
> | `LoginForm.captchaData` | **无** | 有(必填) |

#### 2.4.3 路由类型(`types/api/menu.ts`)

```ts
export interface MetaVO {
  title: string
  icon: string
  noCache: boolean
  link?: string
}

export interface RouterVO {
  name: string
  path: string
  hidden: boolean
  redirect?: string
  component?: string
  alwaysShow?: boolean
  meta?: MetaVO
  children?: RouterVO[]
}
```

#### 2.4.4 实体类型示例(`types/api/system/user.ts`)

```ts
import type { PageQuery } from '../common'

export interface SysUser {
  userId: number
  deptId: number
  userName: string
  nickName: string
  email?: string
  phonenumber?: string
  sex?: '0' | '1' | '2'
  avatar?: string
  password?: string
  status: '0' | '1'
  delFlag?: '0' | '2'
  loginIp?: string
  loginDate?: string
  dept?: SysDept
  roles?: SysRole[]
  roleIds?: number[]
  postIds?: number[]
  createBy?: string
  createTime?: string
  remark?: string
}

export interface SysUserQuery extends PageQuery {
  userName?: string
  phonenumber?: string
  status?: '0' | '1'
  deptId?: number
}
```

#### 2.4.5 全局挂载声明(`types/global.d.ts`)

```ts
export {}

declare module 'vue' {
  interface ComponentCustomProperties {
    useDict: (...args: string[]) => Record<string, DictDataOption[]>
    download: (url: string, params?: Record<string, any>, filename?: string) => void
    parseTime: (time: any, pattern?: string) => string | null
    resetForm: (refName: string) => void
    handleTree: <T>(data: T[], id?: string, parentId?: string) => T[]
    addDateRange: (params: any, dateRange: string[], propName?: string) => any
    getConfigKey: (key: string) => Promise<AjaxResult>
    selectDictLabel: (datas: any[], value: any) => string
    selectDictLabels: (datas: any[], values: any[], separator?: string) => string
  }
}

declare global {
  interface Window {
    // 如需挂载全局对象在此声明
  }
}
```

#### 2.4.6 🔶 类型 ↔ 后端契约映射表

| 前端类型 | 后端来源 | 文件 |
|---|---|---|
| `AjaxResult<T>` / `R<T>` / `TableDataInfo<T>` | `com.basic.common.core.domain.{AjaxResult,R,TableDataInfo}` | `types/api/common.ts` |
| `PageQuery` | `BaseController.startPage()` + `PageDomain` | `types/api/common.ts` |
| `LoginForm` | `LoginBody`(username / password / code / uuid) | `types/api/login.ts` |
| `LoginInfoResult` | `AjaxResult` + `token` | `types/api/login.ts` |
| `UserInfoResult` | `SysLoginController.getInfo()` 返回 Map | `types/api/login.ts` |
| `CaptchaInfoResult` | `CaptchaController.getCode()` 返回 Map(⬆️ kaptcha 版) | `types/api/login.ts` |
| `SysUser` / `SysRole` / `SysMenu` / `SysDept` / `SysPost` | `com.basic.system.domain.*` | `types/api/system/*.ts` |
| `SysDictType` / `SysDictData` | 同上 | `types/api/system/dict.ts` |
| `SysConfig` / `SysNotice` | 同上 | `types/api/system/{config,notice}.ts` |
| `SysOperLog` / `SysLogininfor` / `SysJob` / `SysJobLog` / `SysUserOnline` / `ServerInfo` | 同上 + `Server`/`Cpu`/`Mem`/`Jvm`/`Sys`/`SysFile` | `types/api/monitor/*.ts` |
| `GenTable` / `GenTableColumn` | `com.basic.generator.domain.*` | `types/api/tool/gen.ts` |
| `RouterVO` / `MetaVO` | `com.basic.system.domain.vo.{RouterVo,MetaVo}` | `types/api/menu.ts` |

---

## 3. 框架机制

### 3.1 ~ 3.9 后端帧机制(⬆️ **全部继承 `springboot3`**)

| 小节 | 内容 | 状态 |
|---|---|---|
| 3.1 启动与包扫描 | `@SpringBootApplication` + `@MapperScan` | ⬆️ 一致 |
| 3.2 动态 Schema 路由 | `DataSourceAspect` + `DynamicDataSourceContextHolder` | ⬆️ 一致 |
| 3.3 事务边界 | `@Transactional(rollbackFor)` / `AsyncManager` 隔离 | ⬆️ 一致 |
| 3.4 审计切面 | `LogAspect` `@Around` | ⬆️ 一致 |
| 3.5 重复提交 | `SameUrlDataInterceptor`(jakarta) | ⬆️ 一致 |
| 3.6 限流 | `RateLimiterAspect`(Guava / Redis) | ⬆️ 一致 |
| 3.7 XSS | `XssFilter` + `@Xss` | ⬆️ 一致 |
| 3.8 字段填充 | `MyMetaObjectHandler` | ⬆️ 一致 |
| 3.9 SB3 升级机制 | jakarta 迁移 / Security 6 / springdoc / yauaa / starter / ResourcesConfig / yml | ⬆️ 见 `docs/springboot3-详细设计.md` §3.9 |

### 3.10 🔶 前端构建与类型检查机制(本分支新增)

#### 3.10.1 web-ui 打包链

```
pnpm dev / pnpm build:prod
   │
   ▼
vite.config.ts(TS 配置,由 Vite 用 esbuild 直接执行)
   │
   ├─ @vitejs/plugin-vue        解析 .vue SFC
   ├─ unplugin-auto-import      生成 auto-imports.d.ts(供 TS 识别全局 API)
   ├─ unplugin-vue-components   生成 components.d.ts(供 TS 识别全局组件)
   ├─ unplugin-vue-setup-extend-plus  支持 <script setup name="Xxx">
   ├─ vite-plugin-svg-icons     图标
   ├─ vite-plugin-compression   gzip
   └─ sass-embedded             SCSS(含 basic.scss 主题色)
   │
   ▼
esbuild / rollup 转译(⚠️ 只擦除类型,不做类型检查)
   │
   ▼
dist/
```

> **关键认知**:Vite 用 esbuild 转译 TS,**不做类型检查**。类型错误只有 `vue-tsc` 能发现(ADR-25 / HD-20)。

#### 3.10.2 web-mp 打包链

```
pnpm dev:h5 / dev:mp-weixin / build:*
   │
   ├─ predev:h5 → node ./sync-config.cjs(同步配置,.cjs 不参与 TS)
   ├─ predev:mp-weixin → sync-config.cjs + fixup-mp-components.cjs --watch(后台)
   │
   ▼
vite.config.ts + @dcloudio/vite-plugin-uni
   │
   ▼
dist/dev/h5 | dist/dev/mp-weixin | dist/build/*
   │
   └─ postdev:mp-weixin → fixup-mp-components.cjs(修组件路径)
```

#### 3.10.3 类型检查链(独立于构建)

```bash
# web-ui(package.json 无 typecheck 脚本 → 直接调 vue-tsc)
cd web-ui && npx vue-tsc --noEmit

# web-mp(有 typecheck 脚本)
cd web-mp && pnpm typecheck        # → vue-tsc --noEmit
```

**`vue-tsc` 工作方式**:
1. 读 `tsconfig.json`
2. 按 `include` 收集 `.ts` / `.d.ts` / `.vue`
3. 遇到 `.vue` 时用 `@vue/language-core` 解析 SFC,把 `<script setup lang="ts">` 当 TS 模块
4. `skipLibCheck: true` → 跳过 `node_modules` 的 `.d.ts` 内部检查
5. `web-ui` 额外继承 `@vue/tsconfig@0.5.1` 的基础规则
6. 输出错误到 stdout,**不产出文件**

#### 3.10.4 严格度矩阵

| 检查项 | web-ui | web-mp | 说明 |
|---|---|---|---|
| 变量隐式 `any` | ❌ 报错(strict) | ✅ 允许 | mp 端 uni-api 类型不全 |
| `null` / `undefined` 检查 | ❌ 报错(strict) | ✅ 允许 | mp 端大量可选参数 |
| 函数参数双变检查 | ❌ 报错(strict) | ✅ 允许 | — |
| `this` 隐式 `any` | ❌ 报错(strict) | ✅ 允许 | — |
| `isolatedModules` | 未开 | ✅ 开启 | mp 端要求 `import type` |
| 类型导入强制 `import type` | 否 | ✅ 是 | 由 `isolatedModules` 决定 |
| 第三方库内部类型 | 跳过 | 跳过 | `skipLibCheck: true` |

#### 3.10.5 类型错误的常见处置

| 报错类型 | 场景 | 处置 |
|---|---|---|
| `TS2307: Cannot find module '@/xxx'` | 路径别名未生效 | 检查 `tsconfig.paths` + `vite.resolve.alias` 是否一致 |
| `TS2339: Property 'xxx' does not exist` | 类型缺字段 | 补 `types/api/**` interface(不要用 `as any`) |
| `TS7016: Could not find declaration file` | 三方库无类型 | `types/global.d.ts` 加 `declare module 'xxx' { ... }` |
| `TS1484: 'X' is a type and must be imported using 'import type'` | mp 端 `isolatedModules` | 改 `import type { X }` |
| `TS2532: Object is possibly 'undefined'` | strict 空检查 | 加 `?.` / `??` / 早期 `if` 守卫 |
| `TS2322: Type 'X' is not assignable to type 'Y'` | 字段类型不匹配 | 核对后端 DTO;必要时收窄联合类型 |
| `TS2345: Argument of type ... not assignable` | 函数入参 | 补 interface 或显式标注 |
| `TS6133: 'X' is declared but never used` | 冗余变量 | 删除或使用 |

---

## 4. 核心引擎详细设计

### 4.1 ~ 4.5 后端核心引擎(⬆️ **全部继承 `springboot3`**)

| 小节 | 内容 | 状态 |
|---|---|---|
| 4.1 登录引擎 | `SysLoginService.login(username, password, code, uuid)` + `validateCaptcha`(**kaptcha 文本比对**) | ⬆️ 一致 |
| 4.2 锁屏/解锁 | `web-ui/store/modules/lock.js` → 本分支 `lock.ts` | 🔶 仅前端 TS 化 |
| 4.3 定时任务 | `SysJobServiceImpl` / `AbstractQuartzJob` / `JobInvokeUtil` / `RyTask` | ⬆️ 一致 |
| 4.4 代码生成 | `GenTableServiceImpl` + Velocity 2.3 模板(⚠️ 产出 SB2 风格 + JS 风格) | ⬆️ 一致 |
| 4.5 动态路由/菜单 | `SysMenuServiceImpl.buildMenus` | ⬆️ 一致 |

> ⚠️ **本分支验证码是 kaptcha**:
> ```java
> // SysLoginService(⬆️ 未改,示意)
> public String login(String username, String password, String code, String uuid) {
>     boolean captchaOnOff = configService.selectCaptchaOnOff();
>     if (captchaOnOff) {
>         validateCaptcha(username, code, uuid);   // kaptcha:Redis 取文本比对
>     }
>     // ... BCrypt / token
> }
> ```
> **没有** `captchaData` 参数、**没有** `validateTianaiCaptcha`(那是 `springboot3-tianai-captcha` 分支)。

### 4.6 🔶 前端核心引擎(TS 重写,本分支新增)

#### 4.6.1 `utils/request.ts` — axios 封装(类型化)

```ts
import axios, { type AxiosInstance, type AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import { getToken } from '@/utils/auth'
import { tansParams } from '@/utils/common'
import errorCode from '@/utils/errorCode'
import type { AjaxResult } from '@/types'

const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use((config: any) => {
  const isToken = config.headers?.isToken === false
  if (getToken() && !isToken) {
    config.headers['Authorization'] = 'Bearer ' + getToken()
  }
  // GET 参数序列化
  if (config.method === 'get' && config.params) {
    let url = config.url + '?' + tansParams(config.params)
    url = url.slice(0, -1)
    config.params = {}
    config.url = url
  }
  return config
})

// 响应拦截器(🔶 返回类型收敛为 AjaxResult)
service.interceptors.response.use(
  (res: AxiosResponse<AjaxResult>): any => {
    const code = res.data.code || 200
    const msg = errorCode[code] || res.data.msg || errorCode['default']
    if (code === 401) {
      return Promise.reject('无效的会话,或者会话已过期,请重新登录。')
    } else if (code === 500) {
      ElMessage({ message: msg, type: 'error' })
      return Promise.reject(new Error(msg))
    } else if (code !== 200) {
      ElMessage({ message: msg, type: 'error' })
      return Promise.reject('error')
    }
    return res.data
  },
  (error: any) => {
    ElMessage({ message: error.message, type: 'error' })
    return Promise.reject(error)
  }
)

export default service
```

> **关键约束**:`request()` 返回值经拦截器已解包为 `AxiosResponse.data`(即 `AjaxResult`),故 `api/*.ts` 可直接写 `Promise<LoginInfoResult>`。

#### 4.6.2 `utils/dict.ts` — `useDict` 组合式函数(类型化)

```ts
import { ref, type Ref } from 'vue'
import { getDicts } from '@/api/system/dict/data'
import type { SysDictData } from '@/types'

/** 🔶 字典选项(下拉用) */
export interface DictDataOption {
  label: string
  value: string
  elTagType?: string
  elTagClass?: string
}

/**
 * 获取字典数据
 * @param args 字段名列表,如 useDict('sys_normal_disable', 'sys_user_sex')
 * @returns 每项为 Ref<DictDataOption[]>
 */
export function useDict(...args: string[]): Record<string, Ref<DictDataOption[]>> {
  const res: Record<string, Ref<DictDataOption[]>> = {}
  args.forEach((dictType: string) => {
    res[dictType] = ref<DictDataOption[]>([])
    getDicts(dictType).then((resp: any) => {
      const data: SysDictData[] = resp.data ?? []
      res[dictType].value = data.map((p) => ({
        label: p.dictLabel,
        value: p.dictValue,
        elTagType: p.listClass,
        elTagClass: p.cssClass
      }))
    })
  })
  return res
}
```

#### 4.6.3 `permission.ts` — 路由守卫(类型化)

```ts
import router from './router'
import type { RouteRecordRaw } from 'vue-router'
import { getToken } from '@/utils/auth'
import { useUserStore } from '@/store/modules/user'
import { usePermissionStore } from '@/store/modules/permission'

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  const permissionStore = usePermissionStore()

  if (getToken()) {
    if (to.path === '/login') {
      next({ path: '/' })
    } else if (userStore.roles.length === 0) {
      try {
        await userStore.getInfo()
        const accessRoutes: RouteRecordRaw[] = await permissionStore.generateRoutes()
        accessRoutes.forEach((r) => router.addRoute(r))
        next({ ...to, replace: true })
      } catch (err) {
        await userStore.logOut()
        next(`/login?redirect=${to.fullPath}`)
      }
    } else {
      next()
    }
  } else {
    if (isWhiteList(to.path)) next()
    else next(`/login?redirect=${to.fullPath}`)
  }
})
```

#### 4.6.4 `api/login.ts` — 显式返回类型范例(kaptcha 版)

```ts
import request from '@/utils/request'
import type { SysRegister, LoginInfoResult, UserInfoResult, CaptchaInfoResult, AjaxResult } from '@/types'

/** 登录(kaptcha:code + uuid) */
export function login(username: string, password: string, code: string, uuid: string): Promise<LoginInfoResult> {
  const data = { username, password, code, uuid }
  return request({
    url: '/login',
    headers: { isToken: false, repeatSubmit: false },
    method: 'post',
    data
  })
}

/** 注册 */
export function register(data: SysRegister): Promise<AjaxResult> {
  return request({ url: '/register', headers: { isToken: false }, method: 'post', data })
}

/** 用户信息 */
export function getInfo(): Promise<UserInfoResult> {
  return request({ url: '/getInfo', method: 'get' })
}

/** 解锁屏 */
export function unlockScreen(password: string): Promise<AjaxResult> {
  return request({ url: '/unlockscreen', method: 'post', data: { password } })
}

/** 注销 */
export function logout(): Promise<AjaxResult> {
  return request({ url: '/logout', method: 'post' })
}

/** 图形验证码(kaptcha:返回 Base64 img) */
export function getCodeImg(): Promise<CaptchaInfoResult> {
  return request({ url: '/captchaImage', headers: { isToken: false }, method: 'get', timeout: 20000 })
}
```

#### 4.6.5 `web-mp` 的 `utils/request.ts`(差异点)

```ts
// web-mp 用 uni.request 而非 axios;泛型 <T> 显式标注
import { getToken } from '@/utils/auth'

interface UniRequestConfig {
  url: string
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  data?: Record<string, any>
  header?: Record<string, string>
  timeout?: number
}

export default function request<T = any>(config: UniRequestConfig): Promise<T> {
  return new Promise((resolve, reject) => {
    uni.request({
      url: import.meta.env.VITE_APP_BASE_API + config.url,
      method: config.method ?? 'GET',
      data: config.data,
      header: {
        ...(getToken() ? { Authorization: 'Bearer ' + getToken() } : {}),
        ...config.header
      },
      success: (res: any) => {
        const data = res.data
        if (data.code === 200) resolve(data as T)
        else if (data.code === 401) { uni.reLaunch({ url: '/pages/login/index' }); reject(data) }
        else { uni.showToast({ title: data.msg, icon: 'none' }); reject(data) }
      },
      fail: (err) => { uni.showToast({ title: '网络异常', icon: 'none' }); reject(err) }
    })
  })
}
```

#### 4.6.6 `store/modules/lock.ts`(锁屏,类型化)

```ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login } from '@/api/login'

/** 🔶 锁屏状态模型 */
export interface LockState {
  locked: boolean
  userName: string
  password: string
  lockTime: number
}

export const useLockStore = defineStore('lock', () => {
  const locked = ref<boolean>(false)
  const userName = ref<string>('')

  function lock(name: string): void {
    userName.value = name
    locked.value = true
  }

  /** 解锁:复用 /login 入口(code/uuid 传占位值) */
  async function unlock(password: string): Promise<void> {
    await login(userName.value, password, 'LOCK_BYPASS', 'LOCK')
    locked.value = false
  }

  return { locked, userName, lock, unlock }
})
```

> ⚠️ **本分支的锁屏是 kaptcha 版**,`code='LOCK_BYPASS'` / `uuid='LOCK'` 由后端 `validateCaptcha` 短路(kaptcha 分支的既有行为),**不存在** `springboot3-tianai-captcha` 的 `captchaData` 缺失问题。

---

## 5. 校验引擎

### 5.1 L1 字段级校验(⬆️ 与 `springboot3` 一致)

- 入口:`@Validated` + `jakarta.validation.constraints.*`
- 失败处理:`GlobalExceptionHandler` → 400 + msg

### 5.2 L2 跨业务校验(⬆️ 一致)

用户名唯一 / 角色 key 唯一 / 菜单自引用 / 部门环 / 通知状态 / invokeTarget / 字典值唯一 / 已读唯一 —— 全部与 `springboot3` 一致。

### 5.3 🔶 前端编译期校验(本分支新增)

| 校验项 | 工具 | 失败动作 |
|---|---|---|
| 类型一致性 | `vue-tsc --noEmit` | 终端报错(**不阻断构建**) |
| 路径别名 | `tsconfig.paths` + `vite.alias` 双端一致 | `TS2307` |
| 类型导入规范 | `isolatedModules`(mp) | `TS1484` |
| 未使用变量 | 需 ESLint(本分支未引入,A-17) | — |
| 接口契约存在性 | 人工 review(`types/api/**` 是否同步) | — |

---

## 6. 业务模块实现

### 6.1 ~ 6.5 后端业务实现(⬆️ 与 `springboot3` 一致)

用户管理 / 角色管理 / 菜单管理 / 通知已读 / Demo 业务 —— 全部一致,不重复。

### 6.6 🔶 前端模块实现(TS 版,本分支新增)

#### 6.6.1 用户管理页(`views/basic-admin/system/user/index.vue`)

```vue
<script setup lang="ts" name="User">
import { ref, reactive, onMounted } from 'vue'
import { listUser } from '@/api/system/user'
import type { SysUser, SysUserQuery } from '@/types'
import type { FormInstance, FormRules } from 'element-plus'

const userList = ref<SysUser[]>([])
const total = ref<number>(0)
const loading = ref<boolean>(true)
const queryParams = reactive<SysUserQuery>({ pageNum: 1, pageSize: 10, userName: undefined, status: undefined })
const formRef = ref<FormInstance>()

async function getList(): Promise<void> {
  loading.value = true
  try {
    const res = await listUser(queryParams)
    userList.value = res.rows
    total.value = res.total
  } finally {
    loading.value = false
  }
}

onMounted(() => { getList() })
</script>
```

#### 6.6.2 登录页(`views/login.vue`,kaptcha 版)

```vue
<script setup lang="ts" name="Login">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCodeImg, login } from '@/api/login'
import { encrypt } from '@/utils/jsencrypt'
import type { LoginForm, CaptchaInfoResult } from '@/types'

const router = useRouter()
const loginForm = reactive<LoginForm>({
  username: '', password: '', rememberMe: false,
  code: '', uuid: ''            // ⚠️ kaptcha 版:无 captchaData
})
const captchaEnabled = ref<boolean>(true)
const codeUrl = ref<string>('')

async function getCode(): Promise<void> {
  const res: CaptchaInfoResult = await getCodeImg()
  captchaEnabled.value = res.captchaEnabled
  if (res.captchaEnabled) {
    codeUrl.value = 'data:image/gif;base64,' + res.img
    loginForm.uuid = res.uuid
  }
}

async function handleLogin(): Promise<void> {
  await login(loginForm.username, encrypt(loginForm.password) as string, loginForm.code, loginForm.uuid)
  await router.push('/')
}
</script>
```

#### 6.6.3 移动端登录页(`web-mp/src/pages/login/index.vue`)

```vue
<script setup lang="ts">
import { ref, reactive } from 'vue'
import { getCodeImg, login } from '@/api/login'
import { encrypt } from '@/utils/jsencrypt'
import type { LoginForm } from '@/api/login'

const loginForm = reactive<LoginForm>({ username: '', password: '', code: '', uuid: '', rememberMe: false })
const captchaEnabled = ref<boolean>(true)

async function handleLogin(): Promise<void> {
  const res = await login(loginForm.username, encrypt(loginForm.password) as string, loginForm.code, loginForm.uuid)
  uni.setStorageSync('Basic-Token', res.token)
  uni.reLaunch({ url: '/pages/index/index' })
}
</script>
```

---

## 7. 审批旁路(本分支不实现)

> `springboot3-extend-flowable` 分支提供:`extend-flowable` 模块 + Flowable + `basicdb_flowable` schema。
> 本分支保留钩子位点(无实现):`BusinessType.GRANT` / `extend-*` 模式 / `gen_table.is_workflow`(待定)。

---

## 8. 审计/报表/Web/安全

### 8.1 审计(⬆️ 一致)

- 写操作:`@Log` + `LogAspect` → `sys_oper_log`
- 登录事件:`AsyncManager` → `sys_logininfor`(⬆️ yauaa 解析 `browser`/`os`)
- 排除参数:`@Log(ignoreParam=true)` / 密码类白名单

### 8.2 报表(⬆️ 一致)

操作日志 / 登录日志 / 服务器监控(oshi 7.4.4)—— 与 `springboot3` 一致。

### 8.3 🔶 Web 端(`web-ui`,TypeScript 版)

| 项 | 内容 |
|---|---|
| 入口 | `src/main.ts` → `createApp(App)`;`app.use(ElementPlus, {...})` 处仍需 `// @ts-ignore`(类型冲突) |
| 路由守卫 | `src/permission.ts`,产出 `RouteRecordRaw[]` |
| 鉴权 | 路由级 `meta.roles`/`meta.perms`;指令级 `v-hasPermi`/`v-hasRole`(`directive/permission/*.ts`);工具 `hasPermi()`/`hasRole()` |
| 网络 | `utils/request.ts`(axios 拦截器);响应解包为 `AjaxResult` |
| 锁屏 | `store/modules/lock.ts`(`LockState`)+ `views/lock.vue` |
| 字典 | `utils/dict.ts` 的 `useDict(...)` → `Record<string, Ref<DictDataOption[]>>` |
| 上传 | `components/{ImageUpload,FileUpload}/index.vue` → `POST /common/upload` |
| 全局组件 | `Pagination` / `RightToolbar` / `Editor` / `FileUpload` / `ImageUpload` / `ImagePreview` / `DictTag`;类型由 `types/components.d.ts` 提供 |
| 全局方法 | `useDict` / `download` / `parseTime` / `resetForm` / `handleTree` / `addDateRange` / `getConfigKey` / `selectDictLabel` / `selectDictLabels`;类型在 `types/global.d.ts` |
| 自动导入 | `unplugin-auto-import` 生成 `auto-imports.d.ts`(`ref`/`computed`/`watch` 等) |
| 主题色 | `assets/styles/basic.scss`(`--el-color-primary: #1890ff`) |
| 类型检查 | `npx vue-tsc --noEmit`(无 npm script,A-14) |

### 8.4 🔶 移动端(`web-mp`,TypeScript 版)

| 项 | 内容 |
|---|---|
| 入口 | `src/main.ts` → `createSSRApp` + Pinia + 显式注册 `up-` 组件 |
| 路由 | `pages.json` 静态 + `permission.ts` 全局守卫 |
| 鉴权 | `v-if="hasPermi([...])"`(uni-app 3 编译期不支持自定义指令) |
| 网络 | `utils/request.ts`(`uni.request` 泛型封装) |
| 加密 | `utils/jsencrypt.ts`(`BasicConfig.rsaPublicKey`) |
| 存储 | `uni.setStorageSync('Basic-Token', token)` |
| 配置 | `src/config.ts`(`baseUrl`) |
| API | `api/{index,login}.ts` + `api/system/{dept,role,user}.ts` |
| Store | `store/index.ts` + `modules/{app,permission,user}.ts` |
| 类型检查 | `pnpm typecheck`(`vue-tsc --noEmit`,`strict: false`) |
| 特殊 | `sync-config.cjs` / `scripts/*.cjs` 保留 `.cjs`(不在 `include`) |

### 8.5 安全(⬆️ 一致)

| 项 | 实现 |
|---|---|
| 密码 | BCrypt |
| 登录传输 | RSA 加密 password |
| Token | HS512 JWT,30min |
| Token 存储 | Redis `login_tokens:<uuid>` |
| 验证码 | **kaptcha 2.3.3**(⬆️ 未换 tianai) |
| XSS / 重复提交 / 限流 / CORS | ⬆️ 一致 |
| 操作/登录日志 | 不可关闭 |
| **🔶 前端类型安全** | `strict`(PC)+ `vue-tsc`;**不构成安全边界**(类型擦除,运行时不校验) |

---

## 9. 错误码

### 9.1 业务错误码(⬆️ 与 `springboot3` 完全一致)

| 类别 | code | 含义 |
|---|---|---|
| HTTP | 200 / 400 / 401 / 403 / 404 / 429 / 500 | 同 `springboot3` |
| 业务 | `1001` | 验证码错误(`CaptchaException`) |
| 业务 | `1002` | 验证码过期(`CaptchaExpireException`) |
| 业务 | `1003` | 用户名或密码错误 |
| 业务 | `1004`~`1012` | 账号停用 / 角色键 / 用户名 / 超管保护 / 菜单自引用 / Cron / invokeTarget / 已读唯一 |

> 🔶 **对外错误码零变化**:前端 `utils/errorCode.ts` 的映射无需调整。

### 9.2 🔶 前端编译期错误码(TS 编号,非 HTTP)

| TS 编号 | 含义 | 典型场景 | 处置 |
|---|---|---|---|
| `TS2307` | 找不到模块 | `@/xxx` 别名未配 | 查 `tsconfig.paths` + `vite.alias` |
| `TS2339` | 属性不存在 | 契约缺字段 | 补 `types/api/**` |
| `TS2322` | 类型不可赋值 | 字段类型不符 | 核对后端 DTO |
| `TS2345` | 实参类型不匹配 | 函数入参 | 补 interface |
| `TS2532` | 对象可能为 undefined | strict 空检查 | `?.` / `??` / 守卫 |
| `TS7016` | 缺声明文件 | 三方库无类型 | `declare module` |
| `TS1484` | 类型须用 `import type` | mp 端 `isolatedModules` | 改 `import type` |
| `TS6133` | 声明未使用 | 冗余变量 | 删除或使用 |

---

## 10. 并发与线程模型

### 10.1 后端(⬆️ 与 `springboot3` 一致)

| 资源 | 位置 | 配置 |
|---|---|---|
| Tomcat | `application.yml` | `max-threads=800` / `min-spare=10` |
| 异步线程池 | `ThreadPoolConfig` | `core=10` / `max=50` / `queue=200` |
| Druid | `application-druid.yml` | `min-idle=5` / `max-active=20` |
| Quartz | `ScheduleConfig` | 25 |
| Redis | `RedisCache` | TTL 30min |
| 限流 | Guava / Redis | 5 QPS/10s |
| 重复提交 | Redis | 500ms |
| UA 解析 | `UserAgentAnalyzer` | `withCache(1000)` |

### 10.2 🔶 前端

| 资源 | 说明 |
|---|---|
| 主线程 | 浏览器/小程序 JS 单线程;TS 类型在编译期擦除,**运行时不增加任何开销** |
| 构建并发 | Vite 用 esbuild(多线程)/ rollup;`vue-tsc` 独立进程(**不参与** dev/build) |
| web-mp 后台进程 | `predev:mp-weixin` 会 `spawn` 一个 `--watch` 的 `fixup-mp-components.cjs`(detached) |
| 请求并发 | axios 默认无并发限制;`request.ts` 未加队列 |

---

## 11. 测试关注点

### 11.1 ~ 11.2 后端与 SB3 专项(⬆️ TC-01~TC-32,与 `springboot3` 一致)

| # | 场景 | 重点 |
|---|---|---|
| TC-01 | 登录三要素(kaptcha) | 错误码 1001/1002/1003 |
| TC-02 | 密码 RSA 加密错误 | 500 + msg |
| TC-03 | Token 30min 失效 | 401 |
| TC-04 | 强退 | token 立即失效 |
| TC-05 | 角色赋菜单 | 路由变化 |
| TC-06 | 数据权限 | SQL 注入 `create_by` |
| TC-07 | 重复提交 | 429 |
| TC-08 | 限流 | 第 6 次拒绝 |
| TC-09 | XSS | 过滤 |
| TC-10 | 字典缓存 | 清缓存生效 |
| TC-11 | 定时任务 | Quartz 状态 |
| TC-12 | 并发禁止 | 只跑一份 |
| TC-13 | 代码生成 | 导入→下载→解压 |
| TC-14 | 操作日志 | 落库 |
| TC-15 | 移动端登录 | uni.storage |
| TC-16 | 移动端 Token 失效 | 跳登录 |
| TC-17 | 锁屏解锁 | kaptcha 短路(`LOCK_BYPASS`) |
| TC-18 | 通知已读 | 不重复 |
| TC-19 | 上传超 10MB | 异常 |
| TC-20 | 优雅停机 | 无残留事务 |
| TC-21~TC-32 | SB3 升级专项(jakarta / springdoc / starter / 配置 / 编译 / 启动) | 见 `docs/springboot3-详细设计.md` §11.2 |

### 11.3 🔶 前端 TypeScript 专项(TC-51~TC-62)

| # | 场景 | 重点 |
|---|---|---|
| **TC-51** | `web-ui` 执行 `npx vue-tsc --noEmit` | **0 error** |
| **TC-52** | `web-mp` 执行 `pnpm typecheck` | **0 error** |
| **TC-53** | 文件统计:`web-ui/src` `.ts`=**79** / `.js`=**6**(均在 `utils/generator/`) | 无遗漏、无双份 |
| **TC-54** | 文件统计:`web-mp/src` `.ts`=**19** / `.js`=**0** | 无遗漏 |
| **TC-55** | `web-ui/src/types` = **22 个文件**;`api/index.ts` 全部 re-export | 导入无缺失 |
| **TC-56** | 两端 `vite.config.ts` 存在,无 `vite.config.js` 残留 | 文件检查 |
| **TC-57** | **故意改错一个 `api` 返回类型 → `vue-tsc` 必须报错** | 验证检查真的生效 |
| **TC-58** | `pnpm dev`(web-ui)/ `pnpm dev:h5`(web-mp)启动 | 控制台无 TS 报错(esbuild 转译) |
| **TC-59** | 登录全链路回归(PC + H5) | `CaptchaInfoResult` / `LoginInfoResult` 字段与后端一致(**kaptcha 版**) |
| **TC-60** | 抽查 5 个 `.vue` 均 `<script setup lang="ts">` | 无 `<script>`(无 lang)残留 |
| **TC-61** | 打包产物体积对比 `springboot3` | 差异 < 5%(类型不产出代码) |
| **TC-62** | 主题色保留 | `basic.scss` 含 `--el-color-primary: #1890ff`;页面实际渲染为蓝色 |

---

## 12. 需求变更记录

| 需求 | 落点 |
|---|---|
| 锁屏解锁 | §4.6.6(`store/modules/lock.ts`)+ §8.3 |
| 移动端双前端 | §8.4 |
| 数据库 PostgreSQL | §3.1 + §8.5(**零变更**) |
| 国际化(词条待补) | §8.4(vue-i18n) |
| 操作日志 / 重复提交 / 限流 / XSS | §3.4~3.7 + §8.1 |
| Quartz 动态任务 | §4.3 |
| 代码生成 | §4.4(⚠️ 产出需手工 ts 化) |
| 数据权限 | §3 + 《需求文档》§8 |
| ⬆️ **SB 3 升级** | §3.9(见 `docs/springboot3-详细设计.md`) |
| **🔶 前端全量 TS 化** | **§1.2 目录结构 + §2.4 类型模型 + §3.10 构建与类型检查 + §4.6 前端核心引擎 + §6.6 前端模块 + §8.3/§8.4 两端实现 + §9.2 编译期错误码 + §11.3 TC-51~TC-62** |
| **🔶 类型契约分层** | §2.4.6 类型↔后端映射表(12 行) |
| **🔶 kaptcha 契约(澄清)** | §2.4.2(`CaptchaInfoResult` / `LoginForm` 无 `captchaData`) |
| **🔶 代码生成产物需 ts 化** | §4.4 + R30/A-18 |
| **🔶 主题色恢复** | §1.2.1 / §8.3 / TC-62 |

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `springboot3` 详细设计(v0.1)为基线;后端 §1.1/§2.1~2.3/§3.1~3.9/§4.1~4.5/§5.1~5.2/§6.1~6.5/§7/§8.1~8.2/§8.5/§9.1/§10.1/§11.1~11.2 标注「与 springboot3 一致」;新增 §1.2 前端目录(两端)· §2.4 类型模型(6 小节 + kaptcha/tianai 对比表 + 映射表)· §3.10 构建与类型检查(5 小节)· §4.6 前端核心引擎(6 小节:request / dict / permission / login / web-mp request / lock)· §5.3 前端编译期校验 · §6.6 前端模块实现(3 个完整 SFC)· §8.3/§8.4 两端 TS 实现 · §9.2 编译期错误码 · §10.2 前端并发 · §11.3 TC-51~TC-62 |
