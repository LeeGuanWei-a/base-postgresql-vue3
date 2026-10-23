# 基础平台(base-postgresql-vue3) 需求文档(PRD) — `springboot2_TypeScript` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot2_TypeScript`** 分支(全栈:后端 + web-ui + web-mp)
> 前置依赖:《架构方案》v0.1
> 本分支相对 `master` 的**唯一需求级差异** = **前端两端全量 TypeScript 化**;后端功能需求、数据库、接口契约与 `master` **完全一致**,本文档不重复后端业务细节,只展开 TS 化专题。

---

## 1. 文档信息与版本

- 当前版本:**v0.1** — 初稿,基于 README + 代码扫描抽取
- 变更历史

| 版本 | 日期 | 变更 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `master` PRD 为基线,新增「§6.9 前端 TypeScript 化需求」(覆盖两端 tsconfig / 类型分层 / vue-tsc 检查 / 严格度分级)+ 场景 S7 + AC-15~AC-22 |

---

## 2. 背景与目标

### 2.1 为什么做

本仓库在 RuoYi-Vue 3.8.2 基础上,以「**后端零升级 + 数据库迁 PostgreSQL + 增加移动端**」为最小改动原则,提供一套对内/对外可复用的后台管理基础平台。`master` 已交付该基线。**本分支(`springboot2_TypeScript`)解决的是前端工程化问题**:

- `master` 的 `web-ui` / `web-mp` 全为 JavaScript,后端返回结构变化时**前端无法在编译期发现**,只能运行时踩坑
- 接口字段增加/改名时,靠人肉全局搜索,`api/*.js` 无契约约束
- 大范围重构(如 2025-Q4 的 `views/` 按业务域重组)缺少类型保护,回归成本高
- 新成员接手时,`res.data.rows` 里有什么全靠读后端代码猜

**结论**:前端全量 TypeScript 化,**不改变任何接口与后端行为**,只在前端加一层编译期类型契约。

### 2.2 目标清单

| 目标 | 成功标准 |
|---|---|
| G1 提供完整的 RBAC 后台管理能力 | 角色/菜单/部门/岗位/用户/字典/参数 全部可在线管理(与 `master` 一致) |
| G2 提供定时任务在线编排 | 通过 Web 配置 Cron,执行可观测(`sys_job_log`) |
| G3 提供数据表反向代码生成 | 选择表 → 一键生成 Controller/Service/Mapper/Vue 页面 |
| G4 提供 PC + 移动双端 | web-ui 满足桌面运营;web-mp 满足移动办公 |
| G5 保证接口兼容 RuoYi 3.8.2 | `/login` `/logout` `/captchaImage` `/getInfo` `/getRouters` 命名/行为与原版一致 |
| G6 数据库统一在 PostgreSQL `basicdb` schema | 一份 `pg_dump` 脚本可恢复整套 34 张表 |
| **G7(本分支)前端两端全量 TypeScript 化** | `web-ui/src` 79 个 `.ts`、`web-mp/src` 19 个 `.ts`,遗留 `.js` 仅 `web-ui/src/utils/generator/` 6 个模板文件 |
| **G8(本分支)接口契约类型化** | 每个 `api/*.ts` 函数**显式标注返回类型**;所有响应体在 `src/types/api/**` 有对应 interface |
| **G9(本分支)类型检查可执行** | `web-ui` / `web-mp` 均可跑 `vue-tsc --noEmit`,`pnpm typecheck` 0 error |
| **G10(本分支)迁移不破坏运行时** | 登录/CRUD/锁屏/代码生成全部回归通过;打包产物与 `master` 功能等价 |

### 2.3 非目标(本分支不做)

- 业务流程建模 / 审批(走 `springboot2-extend-flowable` 分支)
- 行为验证码(走 `springboot2-tianai-captcha` 分支)
- 多租户 / SaaS 化
- SSO 联邦(走 keycloak 集成项目)
- Spring Boot 2 → 3/4 升级(走 `springboot3` / `springboot4` 分支)
- 微服务拆分 / K8s 化 / 灰度发布
- 国际化完整词条(仅引入 vue-i18n,词条待补)
- **后端任何改动**(`pom.xml` / Java 源码 / `application*.yml` / SQL 脚本)
- **`web-mp` 一次性达到 `strict: true`**(ADR-13,分阶段收紧,见 M4)

---

## 3. 术语表

| 术语 | 含义 |
|---|---|
| RBAC | Role-Based Access Control,基于角色的访问控制 |
| 数据权限 | 在功能权限之上,基于「本人 / 本部门 / 本部门及子 / 全部 / 自定义」的数据范围过滤 |
| 菜单权限 | 决定是否能看到菜单项(对应路由元数据 `meta.roles` / `perms`) |
| 按钮权限 | 决定页面上按钮是否显示(`v-hasPermi` / `@PreAuthorize`) |
| 锁屏 | 离开座位时手动锁定前端,需重新输入密码解锁;不销毁 Token |
| Kaptcha | 后端图形验证码,基于 `kaptcha 2.3.2` |
| `basicdb` | PostgreSQL schema,本项目所有表所在 |
| `qrtz_*` | Quartz 表,共 11 张 |
| `gen_*` | 代码生成元数据表,共 2 张 |
| uview-plus | uView 的 Vue3 版本,组件前缀 `up-` |
| **TypeScript(TS)** | 微软开源的 JS 超集,提供静态类型;编译期擦除,运行时无开销 |
| **`vue-tsc`** | Vue 官方 TS 类型检查器(SFC 感知);本项目用 `--noEmit` 只检查不产出 |
| **`tsconfig.json`** | TS 编译配置文件;本项目两前端各 1 个 |
| **`strict`** | TS 严格模式总开关(含 `strictNullChecks` / `noImplicitAny` 等);`web-ui` 开启,`web-mp` 关闭 |
| **`<script setup lang="ts">`** | Vue 3 组合式 API + TS 的标准 SFC 写法;本分支 `.vue` 统一采用 |
| **`types/api/**`** | 本分支新增的接口契约类型目录,按后端模块镜像分层 |
| **`auto-imports.d.ts`** | `unplugin-auto-import` 自动生成的全局 API 类型声明 |
| **`components.d.ts`** | `unplugin-vue-components` 自动生成的全局组件类型声明 |
| **`global.d.ts`** | 手写全局声明(`window` 扩展 / `app.config.globalProperties` 挂载方法) |
| **`import type`** | 仅导入类型不导入值的语法;`isolatedModules: true` 下必需 |

---

## 4. 用户角色与使用场景

### 4.1 角色

| 角色 | 关键动作 | 频率 |
|---|---|---|
| **超级管理员(`admin`)** | 全部功能,含角色/菜单/用户/部门维护、代码生成、监控查看 | 高 |
| **业务管理员** | 受限于「数据权限」的 CRUD,通常负责本部门数据 | 中 |
| **审计员** | 仅看 `sys_oper_log` / `sys_logininfor`,无写操作 | 低 |
| **业务运营(PC)** | 通知/字典/参数查询,通知公告发布 | 高 |
| **业务运营(移动)** | 登录、查看通知、个人中心、移动端特有页面 | 中 |
| **开发者** | 用代码生成器建表/生成模块,导入字典;**本分支额外要求:新增前端代码必须写 TS** | 中 |
| **前端开发者** | 维护 `types/` 契约、跑 `pnpm typecheck`、处理类型报错 | 高(本分支) |

### 4.2 关键场景走查

> S1~S6 与 `master` **完全一致**(登录、角色赋权、代码生成、移动端登录、锁屏、定时任务),此处仅保留索引:
>
> - **S1** 运营首次登录并查看通知(PC)
> - **S2** 管理员新增角色并赋菜单
> - **S3** 开发者用代码生成器建模块
> - **S4** 移动端登录(H5)
> - **S5** 锁屏与解锁(PC)
> - **S6** 定时任务 Cron 在线配置
>
> 详细步骤见 `docs/base-postgresql-vue3-需求文档.md` §4.2(`master` 分支)。

#### 🔷 场景 S7:前端开发者新增一个 API 并保证类型正确(本分支特有)

1. 后端新增接口 `GET /system/foo/list`,返回 `{ code, msg, rows, total }`
2. 前端先在 `web-ui/src/types/api/system/foo.ts` 定义:
   ```ts
   export interface SysFoo {
     fooId: number
     fooName: string
     status: string
   }
   export interface SysFooQuery extends PageQuery {
     fooName?: string
   }
   ```
3. 在 `web-ui/src/types/api/index.ts` 中 re-export:`export * from './system/foo'`
4. 写 `web-ui/src/api/system/foo.ts`:
   ```ts
   export function listFoo(query: SysFooQuery): Promise<ListResult<SysFoo>> { ... }
   ```
   **必须显式标注返回类型**,不允许裸 `return request(...)`
5. 页面 `web-ui/src/views/basic-admin/system/foo/index.vue` 用 `<script setup lang="ts">`,`ref<SysFoo[]>([])`
6. 本地执行 `pnpm typecheck`(`vue-tsc --noEmit`)→ 0 error
7. 提 PR;评审关注点:**是否新增了 `.js` 文件**(禁止)、**返回类型是否显式**

#### 🔷 场景 S8:后端字段改名,前端编译期发现(本分支核心价值)

1. 后端把 `sys_foo.foo_name` 改为 `sys_foo.foo_label`
2. 前端 `types/api/system/foo.ts` 的 `SysFoo.fooName` **保持不变**
3. 前端不做改动 → `pnpm typecheck` **不会报错**(类型仍自洽)
4. **正确流程**:后端字段变更 → 同步改 `types/` 契约 → `pnpm typecheck` → 所有用到该字段的 `.vue`/`.ts` **编译期报错** → 逐个修复
5. 结论:**类型文件是前后端的"契约锚点"**,必须与后端同步维护(列入 code review checklist)

#### 🔷 场景 S9:`web-mp` 严格度差异导致的类型绕过(本分支已知取舍)

1. `web-mp` `strict: false`,某 uni-app 回调参数为 `any`
2. 开发者直接 `res.rows.forEach(...)` 不校验 → 运行时报 `undefined`
3. **约定**:`web-mp` 里凡是**后端返回体**,仍必须在 `api/*.ts` 显式标注 `Promise<XxxResult>`;`strict: false` 只放宽局部推断,不放宽接口契约
4. M4 计划把 `web-mp` 收紧到 `strict: true`

---

## 5. 总体需求

### 5.1 范围图(本分支改动以 **🔷** 标出)

```
                    ┌────────────────────────────────────┐
                    │   平台能力(本仓库)                │
                    │  ┌──────────┐  ┌───────────────┐  │
                    │  │ PC Web   │  │ 移动 H5/      │  │
                    │  │ (Vue3)   │  │ 小程序/App     │  │
                    │  │ 🔷TS     │  │ 🔷TS          │  │
                    │  └────┬─────┘  └─────┬─────────┘  │
                    │       │  HTTP/JSON   │            │
                    │  ┌────┴───────────────┴─────────┐  │
                    │  │   Spring Boot API            │  │
                    │  │  (basic-admin-api)           │  │
                    │  │  🔷 零修改                   │  │
                    │  └────┬─────────────────────────┘  │
                    │       │  MyBatis-Plus              │
                    │  ┌────┴─────────────────────────┐  │
                    │  │  PostgreSQL basicdb          │  │
                    │  │  (34 表 + 5 函数)            │  │
                    │  │  🔷 零修改                   │  │
                    │  └──────────────────────────────┘  │
                    └────────────────────────────────────┘
```

### 5.2 核心规则总表

| 模块 | 关键字段 | L1 校验(字段级) | L2 校验(跨模块) |
|---|---|---|---|
| 用户 | `userName` / `password` / `phonenumber` | 必填、长度、邮箱格式、手机号正则 | 用户名全局唯一;同一部门下不能重名 |
| 角色 | `roleName` / `roleKey` / `dataScope` | 必填、`roleKey` 英文+数字、显示顺序 0-999 | `roleKey` 全局唯一;`admin` 角色不可删 |
| 菜单 | `menuName` / `menuType(M/C/F)` / `perms` | 必填、类型枚举、组件路径合法 | 上级菜单 + 路由组件 + 权限字符(若 type=F) |
| 部门 | `deptName` / `parentId` | 必填、parentId 不能等于自己 | 父级链路不能形成环 |
| 岗位 | `postCode` / `postName` | 必填、长度 | 全局唯一 `postCode` |
| 字典 | `dictType` / `dictLabel` | 必填 | 字典类型存在性校验;`dictValue` 类型内唯一 |
| 通知 | `noticeTitle` / `noticeType` / `status` | 必填、长度 | 发布前必须已选 `status=1` 才对外可见 |
| 定时任务 | `invokeTarget` / `cronExpression` | 必填、Cron 合法 | `invokeTarget` 必须对应 `@Component("ryTask")` 内方法 |
| 代码生成 | `tableName` | 必填、英文+下划线 | 表必须已存在 `basicdb` schema |
| **🔷 TS 源文件** | 文件扩展名 | **前端源码禁止新增 `.js`**(`web-ui/src/utils/generator/*.js` 6 个例外) | `tsconfig.json` 的 `exclude: ["src/**/*.js"]` 自动隔离 |
| **🔷 API 函数** | 返回类型 | **必须显式标注** `Promise<XxxResult>` | 响应 interface 必须存在于 `types/api/**` 并有 re-export |
| **🔷 类型定义** | 位置 | 只放 `src/types/**` | `types/api/**` 镜像后端模块路径;禁止在 `api/*.ts` 内定义 interface |

---

## 6. 功能需求

> **后端业务需求**(文件上传、用户/角色/菜单/部门/字典/通知/定时任务/代码生成、L1/L2 校验、审计、权限点矩阵、数据权限)与 `master` **完全一致**,详见 `docs/base-postgresql-vue3-需求文档.md` §6 / §8。本节只展开 **TS 化专题**。

### 6.9 🔷 前端 TypeScript 化需求(本分支核心)

#### 6.9.1 web-ui TS 工程配置

| 项 | 要求 | 现状 |
|---|---|---|
| `tsconfig.json` | 必须存在;`target: esnext` / `module: esnext` / `moduleResolution: bundler` | ✅ 已配 |
| `strict` | **`true`**(完整严格模式) | ✅ |
| `baseUrl` + `paths` | `"@/*": ["src/*"]` | ✅ |
| `include` | `src/**/*.ts` / `src/**/*.vue` / `vite/**/*.ts` / `auto-imports.d.ts` / `vite.config.ts` | ✅ |
| `exclude` | `node_modules` / `dist` / **`src/**/*.js`** | ✅(隔离 generator 的 6 个 `.js`) |
| `types` | `["node", "vite/client", "element-plus/global"]` | ✅ |
| `skipLibCheck` | `true`(第三方库类型不全时不阻塞) | ✅ |
| `noEmit` | `true`(只检查不产出,Vite 负责打包) | ✅ |
| `allowJs` | `true`(允许 `.js` 与 `.ts` 混存,受 `exclude` 约束) | ✅ |

#### 6.9.2 web-mp TS 工程配置

| 项 | 要求 | 现状 |
|---|---|---|
| `tsconfig.json` | 必须存在;`target: ES2020` / `moduleResolution: Bundler` | ✅ |
| `strict` | **`false`**(起步),`noImplicitAny: false` | ✅ 已知取舍,见 ADR-13 |
| `isolatedModules` | **`true`**(要求类型导入用 `import type`) | ✅ |
| `baseUrl` + `paths` | `"@/*": ["src/*"]` | ✅ |
| `include` | `src/**/*.ts` / `src/**/*.d.ts` / `src/**/*.tsx` / `src/**/*.vue` / `vite.config.ts` | ✅ |
| `exclude` | `node_modules` / `dist` / `unpackage` / `scripts` | ✅ |
| `types` | `[]`(不引 node 类型,避免与 uni-app 冲突) | ✅ |

#### 6.9.3 类型分层要求

| 目录 | 职责 | 要求 |
|---|---|---|
| `web-ui/src/types/api/common.ts` | 通用响应体:`AjaxResult` / `R<T>` / `ListResult<T>` / `PageQuery` / `TableDataInfo` | **禁止**放业务字段 |
| `web-ui/src/types/api/login.ts` | 登录相关:`LoginForm` / `LoginInfoResult` / `UserInfoResult` / `CaptchaInfoResult` / `RegisterForm` | — |
| `web-ui/src/types/api/menu.ts` | 路由/菜单:`RouteRecord` / `Meta` / `RouterVO` | — |
| `web-ui/src/types/api/monitor/**` | 监控模块:`cache` / `job` / `jobLog` / `logininfor` / `online` / `operlog` / `server` | 与 `api/monitor/**` **一一对应** |
| `web-ui/src/types/api/system/**` | 系统模块:`config` / `dept` / `dict` / `menu` / `notice` / `post` / `role` / `user` | 与 `api/system/**` **一一对应** |
| `web-ui/src/types/api/tool/gen.ts` | 代码生成 | — |
| `web-ui/src/types/api/index.ts` | **统一出口**,re-export 所有类型 | 新增类型必须在此登记 |
| `web-ui/src/types/components.d.ts` | 全局组件类型(`unplugin-vue-components` 生成) | **禁止手工修改** |
| `web-ui/src/types/global.d.ts` | 全局声明:`window` 扩展 / `ComponentCustomProperties`(挂载 `useDict` / `download` / `parseTime` / `resetForm` / `handleTree` / `addDateRange` / `getConfigKey` / `selectDictLabel` / `selectDictLabels`) | 手工维护 |
| `web-mp/src/types/global.d.ts` | 移动端全局声明 | — |

#### 6.9.4 API 层类型化要求

| 要求 | 说明 | 示例 |
|---|---|---|
| **显式返回类型** | 每个导出函数必须标注 `Promise<...>` | `export function login(...): Promise<LoginInfoResult>` |
| **入参类型化** | 有 query/body 的函数必须用 interface | `export function listUser(query: SysUserQuery): Promise<ListResult<SysUser>>` |
| **禁止 `any` 返回值** | 不允许 `Promise<any>` 作为公开 API 签名 | — |
| **`import type`** | 仅导入类型时用 `import type` | `import type { AjaxResult } from '@/types'` |
| **统一从 `@/types` 导入** | 不直接写深层路径 | ✅ `import type { SysUser, ListResult } from '@/types'` |

**已类型化的 API 文件**(web-ui 19 个):`login` / `menu` / `monitor/{cache,job,jobLog,logininfor,online,operlog,server}` / `system/{config,dept,dict/data,dict/type,menu,notice,post,role,user}` / `tool/gen`。

#### 6.9.5 单文件组件要求

| 要求 | 说明 |
|---|---|
| `<script setup lang="ts">` | 所有新增/改动 `.vue` 必须带 `lang="ts"` 且用组合式 API |
| 禁止 Options API | 与 TS 类型推导配合差,统一组合式 |
| `ref<T>()` / `reactive<T>()` | 泛型显式标注,避免 `never[]` 推断 |
| Props/Emits 类型化 | `defineProps<{...}>()` / `defineEmits<{...}>()` |
| 全局方法调用 | 通过 `types/global.d.ts` 的 `ComponentCustomProperties` 声明,不写 `@ts-ignore` |

#### 6.9.6 类型检查执行要求

| 端 | 命令 | 脚本位置 | 是否阻塞构建 |
|---|---|---|---|
| `web-ui` | `vue-tsc --noEmit`(本地手动) | `package.json` **无** `typecheck` 脚本 → 需 `npx vue-tsc --noEmit` | **否**(`build:prod` 只跑 `vite build`) |
| `web-mp` | `pnpm typecheck` | `package.json` → `"typecheck": "vue-tsc --noEmit"` | **否** |

> ADR-16 取舍:**类型检查独立于构建**。迁移期强校验会阻塞开发;当前靠开发者本地自觉执行,M5 计划并入 CI。

#### 6.9.7 迁移完整性要求

| 要求 | 验收口径 |
|---|---|
| `web-ui/src` 遗留 `.js` 仅 `utils/generator/` 下 6 个 | `Get-ChildItem web-ui/src -Recurse -Include *.js` 结果集 = 6 |
| `web-mp/src` 无任何 `.js` | 结果集 = 0 |
| 无 `main.js` / `permission.js` / `settings.js` 残留 | 已全部改为 `.ts` |
| 无重复实现(同一模块同时存在 `.js` 和 `.ts`) | 逐目录核对 |
| `vite.config.js` 已改名 `vite.config.ts` | 两端各自核对 |

#### 6.9.8 与后端的契约边界

| 规则 | 说明 |
|---|---|
| **后端零感知** | TS 化不改任何 HTTP 契约;`/login` 等接口路径/入参/出参字节级不变 |
| **类型来源** | `types/api/**` 的字段必须**镜像后端 DTO/VO**;后端变更时同步改类型 |
| **不做运行时校验** | TS 类型是编译期的;运行时不新增校验逻辑(校验仍在后端) |
| **代码生成器协同** | `extend-generator` 生成的前端模板目前产出 JS 风格代码 → 需手工 ts 化,已列 R18 风险 |

### 6.10 其它功能需求(与 `master` 一致)

#### 6.10.1 文件上传与下载

| 项 | 规则 |
|---|---|
| 单文件大小 | ≤ 10MB(`spring.servlet.multipart.max-file-size`) |
| 单次请求 | ≤ 20MB(`max-request-size`) |
| 存储位置 | `D:/ruoyi/uploadPath`(可通过 `BasicConfig.profile` 修改) |
| 访问路径 | `/profile/**`(走 `ResourcesConfig`) |
| 允许扩展名 | 白名单控制(`MimeTypeUtils`) |
| 失败处理 | 上传/下载失败抛出 `FileException` / `InvalidExtensionException`,前端弹窗提示 |

#### 6.10.2 自动校验(L1)

后端入口通过 `@Validated` + `javax.validation` 注解完成字段级校验,失败由 `GlobalExceptionHandler` 包装为 `R.fail(HttpStatus.BAD_REQUEST, msg)`。

#### 6.10.3 错误明细与重跑

- 登录失败:`sys_logininfor` 记录原因(账号/密码/验证码),前端提示。
- 业务校验失败:返回 `R.fail(code, msg)`,前端 toast。
- 定时任务失败:`sys_job_log` 记录异常堆栈,可在「日志」页查看。
- **🔷 前端类型错误**:`vue-tsc` 输出到终端;不落库、不上报后端。

#### 6.10.4 业务模块需求(按模块细化)

> 用户/角色/菜单/部门/字典/通知/定时任务/代码生成 的字段与校验规则 **与 `master` 完全一致**,详见 `docs/base-postgresql-vue3-需求文档.md` §6.4。本分支不新增任何字段。

#### 6.10.5 跨业务比对(L2)

- 用户名/角色 key 全局唯一:在 `Service` 显式 `checkUnique*` 调用
- 字典值类型内唯一:`dictType` 维度下 `dictValue` 唯一
- 通知已读:`(user_id, notice_id)` 唯一索引

#### 6.10.6 审批复核(本分支不涉及)

> 走 `springboot2-extend-flowable` 分支实现,见该分支 PRD。

#### 6.10.7 统计报表

- 操作日志:按时间段/模块/操作人筛选,导出 Excel(`@Excel` 注解)
- 登录日志:同上
- 服务器监控:`ServerController` 实时返回 CPU/内存/磁盘/JVM

#### 6.10.8 审计

- 写操作:`@Log(title, businessType = BusinessType.UPDATE)` → 切面 → `sys_oper_log`
- 登录:`AsyncManager` 异步 → `sys_logininfor`(成功/失败)
- 异常:全局 `GlobalExceptionHandler` 落日志,可选落库(默认仅控制台)
- **🔷 TS 类型错误不进入审计**(编译期问题,不产生运行时事件)

---

## 7. 非功能需求

| 属性 | 目标 |
|---|---|
| 一致性 | 单服务事务边界在 Service 层,异步日志/统计不污染主链路 |
| 可靠性 | 操作日志失败不影响主业务;定时任务异常隔离(每个 Job 独立 try/catch) |
| 性能 | 列表分页 1000 条数据 < 1s;监控实时接口 < 200ms;登录链路 < 500ms;**🔷 TS 类型在编译期擦除,运行时零开销** |
| 审计合规 | 90 天操作日志可查询、可导出 |
| 安全 | RSA 加密登录密码;BCrypt 散列存储;JWT 30min 过期;XSS 过滤;重复提交拦截;限流;显式 CORS 白名单 |
| 可用性 | 单实例 99.9%(运维保障) |
| 部署 | 单一可执行 jar,`tomcat.max-threads=800`;前端 `vite build` 产物 |
| **🔷 可维护性** | 新增前端代码必须 TS;`types/api/**` 契约与后端同步;`vue-tsc` 0 error |
| **🔷 构建体积** | TS 化后 bundle 体积与 `master` 差异 < 5%(类型不产出代码) |

---

## 8. 权限与角色

> RBAC 权限点矩阵与数据权限 **与 `master` 完全一致**(15 个模块 × 增删改查导出导入),详见 `docs/base-postgresql-vue3-需求文档.md` §8。本分支**不新增任何权限点、不改任何菜单数据**。

---

## 9. 边界与不做清单

| 项 | 说明 |
|---|---|
| 业务流程 | 走 `springboot2-extend-flowable` |
| 行为验证码 | 走 `springboot2-tianai-captcha` |
| 微服务化 | 不做 |
| SSO 联邦 | 不做(后续 keycloak 集成) |
| SB 2 → 3 升级 | 走 `springboot3` 分支 |
| **TS 化** | **本分支已做**(`web-ui` + `web-mp` 全量);`springboot3_TypeScript` / `springboot4_TypeScript` 是 SB3/SB4 基线上的 TS 版 |
| 移动端完整业务 | 仅做登录 + 通知 + 个人中心,业务流程/审批不做 |
| 国际化词条 | 已引入,词条待补 |
| **`web-mp` strict 模式** | 本分支 `strict: false`;收紧到 `strict: true` 列 M4 |
| **`vue-tsc` 并入 CI** | 本分支未做,列 M5 |
| **代码生成器模板 TS 化** | 后端 Velocity 模板未改(R18) |
| **前端单元测试** | 本分支不引入 Vitest/Playwright |

---

## 10. 验收标准

| # | 验收项 | 验收方法 |
|---|---|---|
| AC-01 | `mvn clean compile` 0 ERROR | 终端命令 |
| AC-02 | `mvn spring-boot:run` 启动成功,看到「基础平台启动成功」 | 终端 |
| AC-03 | 7 个子模块 `parent.version` = 3.8.2 | `mvn validate` 或 IDE |
| AC-04 | `psql -f file-sql/basicdb-vue3.sql` 恢复后,`information_schema.tables WHERE table_schema='basicdb'` 返回 34 | 终端 |
| AC-05 | admin/admin123 登录 PC 端成功,看到完整菜单 | 浏览器 |
| AC-06 | admin/admin123 登录 web-mp H5 成功,看到工作台 | 浏览器 |
| AC-07 | 锁屏→输入密码→解锁 成功 | 浏览器 |
| AC-08 | 新建角色→分配菜单→绑定用户→用户重新登录可见新菜单 | 浏览器 |
| AC-09 | 工具/代码生成 → 导入 `sys_demo` → 下载 zip 解压到对应目录 → 重启可访问 | 终端 + 浏览器 |
| AC-10 | 监控/定时任务 → 新建 Cron → 启动 → 任务日志可见 | 浏览器 |
| AC-11 | 操作日志可导出 Excel | 浏览器 |
| AC-12 | XSS 测试:输入 `<script>alert(1)</script>` → 后端拒绝 | 终端/浏览器 |
| AC-13 | 重复提交测试:同 URL 同 body 500ms 内两次 → 第二次被拒绝 | 浏览器 |
| AC-14 | swagger 可访问:`/swagger-ui.html` | 浏览器 |
| **AC-15** | **`web-ui` `npx vue-tsc --noEmit` 0 error** | 终端(`web-ui/`) |
| **AC-16** | **`web-mp` `pnpm typecheck` 0 error** | 终端(`web-mp/`) |
| **AC-17** | **`web-ui/src` `.ts` 数量 = 79;`.js` 数量 = 6 且全部在 `utils/generator/`** | 终端计数 |
| **AC-18** | **`web-mp/src` `.ts` 数量 = 19;`.js` 数量 = 0** | 终端计数 |
| **AC-19** | **`web-ui` 与 `web-mp` 均有 `tsconfig.json`,且 `vite.config.ts` 存在(无 `.js` 版)** | 文件检查 |
| **AC-20** | **`pnpm dev`(web-ui)/ `pnpm dev:h5`(web-mp)可启动,控制台无 TS 相关报错** | 终端 |
| **AC-21** | **抽查 5 个 `api/*.ts`,均有显式 `Promise<...>` 返回类型** | 代码走查 |
| **AC-22** | **抽查 5 个 `.vue`,均为 `<script setup lang="ts">`** | 代码走查 |

---

## 附录A 待确认问题清单

| 编号 | 问题 | 建议默认 | 阻塞 |
|---|---|---|---|
| A-1 | 移动端是否需要走完整动态路由? | 暂用本地 `pages.json`,动态路由留待后续 | 否 |
| A-2 | web-mp 是否实现锁屏? | 暂不实现,留待后续 | 否 |
| A-3 | web-mp Token 失效后是否自动重登? | 先做 401 跳转登录,refresh token 留待后续 | 否 |
| A-4 | 操作日志保留周期? | 90 天 | 否 |
| A-5 | 多端登录是否互踢? | 当前 Token 无设备指纹,不互踢 | 否 |
| A-6 | 通知公告是否支持富文本(图片/附件)? | 暂为 `text`,富文本编辑器已就位 | 否 |
| A-7 | 代码生成器是否支持主子表? | 暂不支持 | 否 |
| **A-8** | **`web-ui` 是否要加 `typecheck` 脚本?** | **建议加**(与 web-mp 对齐),当前无 | 否 |
| **A-9** | **`vue-tsc` 何时并入 CI?** | 建议 M5,先跑 1~2 个迭代观察误报率 | 否 |
| **A-10** | **`web-mp` 何时收紧 `strict: true`?** | 建议 M4,依赖 uni-app 类型定义完善 | 否 |
| **A-11** | **代码生成器模板是否 TS 化?** | 建议后端 `extend-generator` 加一套 TS 模板(R18) | 否 |
| **A-12** | **是否引入 ESLint + `@typescript-eslint`?** | 建议后续引入,当前无 lint 配置 | 否 |
| **A-13** | **`utils/generator/*.js` 6 个文件是否长期保留 JS?** | 建议长期保留(ADR-15),收益极低 | 否 |
| **A-14** | **是否引入前端单元测试(Vitest)?** | 建议后续,独立于 TS 化任务 | 否 |

---

## 附录B 业务回填模板

```markdown
### 模块: <名称>
| 字段 | 类型 | 必填 | 校验 |
|---|---|---|---|
| ... | ... | ... | ... |
```

---

## 附录C TS 化检查清单(交付前逐项打勾)

- [ ] `web-ui/tsconfig.json` 存在,`strict: true`
- [ ] `web-mp/tsconfig.json` 存在,`strict: false` + `isolatedModules: true`
- [ ] 两端 `vite.config.ts` 存在(无 `vite.config.js`)
- [ ] `web-ui/src/types/` 23 个文件齐全
- [ ] `web-ui/src` 79 个 `.ts`,遗留 `.js` 仅 `utils/generator/` 6 个
- [ ] `web-mp/src` 19 个 `.ts`,0 个 `.js`
- [ ] `npx vue-tsc --noEmit`(web-ui)0 error
- [ ] `pnpm typecheck`(web-mp)0 error
- [ ] 抽查 `api/*.ts` 有显式返回类型
- [ ] 抽查 `.vue` 均 `<script setup lang="ts">`
- [ ] 登录/CRUD/锁屏/代码生成 全链路回归通过

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `master` PRD(v0.1)为基线,新增 G7~G10 目标、§6.9 前端 TS 化需求(工程配置/类型分层/API 类型化/SFC/检查执行/迁移完整性/契约边界)、场景 S7~S9、AC-15~AC-22、待定项 A-8~A-14、附录C 检查清单 |
