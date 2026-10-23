# 基础平台(base-postgresql-vue3) 需求文档(PRD) — `springboot3_TypeScript` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot3_TypeScript`** 分支(全栈:后端 + web-ui + web-mp)
> 前置依赖:《架构方案》v0.1
> 本分支 = **`springboot3` 基线(SB 3.5.16 / JDK 17 / Jakarta / Security 6 / springdoc)+ 前端两端全量 TypeScript 化**。
> **业务功能需求、接口契约、数据库、后端代码与 `springboot3` 完全一致**;本分支**唯一需求级差异 = 前端语言从 JS 换为 TS**。本文档不重复业务细节与 SB3 升级细节,只展开「前端 TypeScript 化」专题。

---

## 1. 文档信息与版本

- 当前版本:**v0.1** — 初稿,基于 README + 代码扫描抽取
- 变更历史

| 版本 | 日期 | 变更 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `springboot3` PRD 为基线,新增「§6.10 前端 TypeScript 化需求」(9 小节)+ 场景 S11~S14 + AC-25~AC-38 + 待定项 A-14~A-21 |

---

## 2. 背景与目标

### 2.1 为什么做

`springboot3` 分支已完成**后端** SB 3 升级,但前端两端仍是 JavaScript,带来三类问题:

- **契约漂移**:`springboot3` 已把后端升到 SB 3 / springdoc,接口字段结构没变但前端仍无类型约束,**后端改字段前端编译期发现不了**
- **重复排查**:`res.data.rows` 里有什么、`meta.roles` 是什么形状,全靠读后端代码或运行时打印
- **重构风险**:`views/` 按业务域重组这类大改,没有类型保护,回归成本高

因此本分支把 PC + 移动两端前端**全量改写为 TypeScript**,在不动后端与数据库的前提下,给前端加一层**编译期类型契约**。

### 2.2 目标清单

| 目标 | 成功标准 |
|---|---|
| G1~G6(继承 `master`) | RBAC / 定时任务 / 代码生成 / PC+移动双端 / 接口兼容 / PostgreSQL —— 见 `master` PRD §2.2 |
| G7~G12(继承 `springboot3`) | JDK 17 + SB 3.5.16 / Jakarta / springdoc / 契约零变更 / starter 变体 / 配置迁移 —— 已交付 |
| **G13(本分支)`web-ui` 全量 TS 化** | `web-ui/src` **79 个 `.ts`**;遗留 `.js` **仅 6 个**(全在 `utils/generator/`) |
| **G14(本分支)`web-mp` 全量 TS 化** | `web-mp/src` **19 个 `.ts`**,**0 个遗留 `.js`** |
| **G15(本分支)接口契约类型化** | 每个 `api/*.ts` 函数**显式标注返回类型**;响应体在 `src/types/api/**` 有对应 interface |
| **G16(本分支)类型检查可执行** | `web-mp` 可跑 `pnpm typecheck`;`web-ui` 可跑 `npx vue-tsc --noEmit`;均 0 error |
| **G17(本分支)迁移不破坏运行时** | 登录 / CRUD / 锁屏 / 代码生成 / 定时任务全部回归;打包产物功能与 `springboot3` 等价 |
| **G18(本分支)后端与 DB 零改动** | `git diff springboot3 -- basic-* extend-* business-core pom.xml file-sql` 应为空;DB 仍 34 表 |

### 2.3 非目标(本分支不做)

- 行为验证码(走 `springboot3-tianai-captcha`)
- 业务流程引擎(走 `springboot3-extend-flowable`)
- JDK 17 → 21 / SB 3 → 4 / Jackson 3 / Security 7(走 `springboot4` / `springboot4_TypeScript`)
- 多租户 / SaaS 化
- SSO 联邦
- 微服务拆分 / K8s 化 / 灰度发布
- 国际化完整词条(仅引入 vue-i18n,词条待补)
- **后端任何改动**(`pom.xml` / Java 源码 / `application*.yml` / SQL)
- **数据库任何变更**(本分支零变更)
- **`web-mp` 一次性达到 `strict: true`**(ADR-21,分阶段收紧,见 M4)
- **`vue-tsc` 并入 CI**(列 M5)
- **代码生成器模板 TS 化**(`extend-generator` 的 Velocity 模板未改,R30)
- **前端单元测试**(不引入 Vitest / Playwright)
- **`utils/generator/*.js` TS 化**(ADR-24 明确保留)

---

## 3. 术语表

| 术语 | 含义 |
|---|---|
| RBAC | Role-Based Access Control,基于角色的访问控制 |
| 数据权限 | 按「本人 / 本部门 / 本部门及子 / 全部 / 自定义」过滤数据范围 |
| 锁屏 | 前端手动锁定,需重新输入密码解锁;不销毁 Token |
| Kaptcha | 后端图形验证码,**本分支仍是 kaptcha 2.3.3**(未换 tianai) |
| `basicdb` | PostgreSQL schema,本项目所有表所在 |
| `qrtz_*` / `gen_*` | Quartz 11 张 / 代码生成 2 张 |
| uview-plus | uView 的 Vue3 版本,组件前缀 `up-` |
| Jakarta EE | Java EE 改名后规范,包名 `jakarta.*`(⬆️ 继承 `springboot3`) |
| springdoc-openapi | SB 3 生态的 OpenAPI 3 文档库(⬆️ 继承) |
| `SecurityFilterChain` | Security 6 配置方式(⬆️ 继承) |
| **TypeScript(TS)** | JS 超集,提供静态类型;编译期擦除,运行时零开销 |
| **`vue-tsc`** | Vue 官方 TS 类型检查器(SFC 感知);用 `--noEmit` 只检查不产出 |
| **`tsconfig.json`** | TS 编译配置;本项目两前端各 1 个 |
| **`strict`** | TS 严格模式总开关;`web-ui` 开启,`web-mp` 关闭 |
| **`isolatedModules`** | 要求类型导入用 `import type`;仅 `web-mp` 开启 |
| **`<script setup lang="ts">`** | Vue 3 组合式 API + TS 的标准 SFC 写法 |
| **`types/api/**`** | 新增的接口契约类型目录,按后端模块镜像分层 |
| **`auto-imports.d.ts`** | `unplugin-auto-import` 生成的全局 API 类型声明 |
| **`components.d.ts`** | `unplugin-vue-components` 生成的全局组件类型声明 |
| **`global.d.ts`** | 手写全局声明(`window` / `app.config.globalProperties`) |
| **`import type`** | 仅导入类型不导入值;`isolatedModules: true` 下必需 |

---

## 4. 用户角色与使用场景

### 4.1 角色

| 角色 | 关键动作 | 频率 |
|---|---|---|
| **超级管理员(`admin`)** | 全部功能 | 高 |
| **业务管理员** | 受数据权限约束的 CRUD | 中 |
| **审计员** | 仅看 `sys_oper_log` / `sys_logininfor` | 低 |
| **业务运营(PC)** | 登录 / 通知 / 字典查询 | 高 |
| **业务运营(移动)** | 登录 / 通知 / 个人中心 | 中 |
| **开发者** | 用代码生成器建模块(**生成的前端代码需手工 ts 化**) | 中 |
| **前端开发者(本分支重点)** | 维护 `types/` 契约、跑 `vue-tsc`、处理类型报错 | 高 |

### 4.2 关键场景走查

> **S1~S10** 与 `master` / `springboot3` 一致(登录浏览、角色赋权、代码生成、移动端登录、锁屏、定时任务、SB3 升级回归、Swagger 迁移、starter 选型、生成器 jakarta 修正),详见 `docs/springboot3-需求文档.md` §4.2。除 **S3(代码生成)** 因生成物需 ts 化而略有变化外,其余流程不变。

#### 🔶 场景 S11:前端开发者新增 API 并保证类型正确(本分支核心)

1. 后端新增接口 `GET /system/foo/list`,返回 `{code, msg, rows, total}`
2. 先在 `web-ui/src/types/api/system/foo.ts` 定义契约:
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
3. 在 `web-ui/src/types/api/index.ts` 登记:`export * from './system/foo'`
4. 写 `web-ui/src/api/system/foo.ts`:
   ```ts
   export function listFoo(query: SysFooQuery): Promise<ListResult<SysFoo>> { ... }
   ```
   **必须显式标注返回类型**,不允许裸 `return request(...)`
5. 页面 `views/.../foo/index.vue` 用 `<script setup lang="ts">`,`const list = ref<SysFoo[]>([])`
6. 本地执行 `npx vue-tsc --noEmit` → 0 error
7. 提 PR;评审关注:**是否新增了 `.js`**(禁止)、**返回类型是否显式**

#### 🔶 场景 S12:后端字段改名,前端编译期发现(本分支核心价值)

1. 后端把 `sys_foo.foo_name` 改为 `sys_foo.foo_label`
2. 前端 `types/api/system/foo.ts` 的 `SysFoo.fooName` **保持不变**
3. 前端不改 → `vue-tsc` **不会报错**(类型仍自洽)
4. **正确流程**:后端字段变更 → **同步改 `types/` 契约** → `vue-tsc` → 所有用到该字段的 `.vue` / `.ts` **编译期报错** → 逐个修复
5. 结论:**类型文件是前后端契约的"唯一锚点"**,必须与后端同步维护(列入 code review checklist,R29)

#### 🔶 场景 S13:`web-mp` 严格度差异导致的类型绕过(已知取舍)

1. `web-mp` `strict: false`,某 uni-app 回调参数为 `any`
2. 开发者直接 `res.rows.forEach(...)` 不校验 → 运行时报 `undefined`
3. **约定**:`web-mp` 里凡是**后端返回体**,仍必须在 `api/*.ts` 显式标注 `Promise<XxxResult>`;`strict: false` 只放宽局部推断,**不放宽接口契约**
4. M4 计划把 `web-mp` 收紧到 `strict: true`

#### 🔶 场景 S14:代码生成器产出的前端代码需手工 ts 化

1. 用代码生成器生成一套 CRUD → 解压到 `web-ui/src/views/<module>/`
2. 生成的 `index.vue` 是 **`<script setup>`(无 `lang="ts"`)**、`api.js` 是 **`.js` 无类型**(模板未改,R30)
3. 手工处理:
   - `api.js` → `api.ts`,补 `import type` + 显式返回类型
   - `<script setup>` → `<script setup lang="ts">`
   - 在 `types/api/<module>/<entity>.ts` 补 interface 并 re-export
4. 跑 `npx vue-tsc --noEmit` 验证
5. **长期方案**:给 `extend-generator` 增 TS 模板(A-9 / R30)

---

## 5. 总体需求

### 5.1 范围图(🔶 = 本分支;⬆️ = `springboot3` 继承)

```
                    ┌────────────────────────────────────────┐
                    │   平台能力(本仓库)                    │
                    │  ┌──────────┐  ┌───────────────────┐  │
                    │  │ PC Web   │  │ 移动 H5/小程序/App │  │
                    │  │ (Vue3)   │  │                   │  │
                    │  │ 🔶 TS    │  │ 🔶 TS             │  │
                    │  └────┬─────┘  └─────────┬─────────┘  │
                    │       │  HTTP/JSON       │            │
                    │  ┌────┴───────────────────┴─────────┐  │
                    │  │   Spring Boot 3.5.16 (⬆️ JDK 17) │  │
                    │  │   🔶 零改动(契约不变)             │  │
                    │  └────┬─────────────────────────────┘  │
                    │       │  MyBatis-Plus 3.5.7            │
                    │  ┌────┴─────────────────────────────┐  │
                    │  │  PostgreSQL basicdb(34 表)      │  │
                    │  │  🔶 零改动                        │  │
                    │  └──────────────────────────────────┘  │
                    └────────────────────────────────────────┘
```

### 5.2 核心规则总表

> **业务规则**与 `master` / `springboot3` 一致(用户/角色/菜单/部门/岗位/字典/通知/定时任务/代码生成的 L1/L2 校验),详见 `docs/springboot3-需求文档.md` §5.2。下表为本分支**新增的 TS 相关规则**。

| 模块 | 关键字段 | L1 校验 | L2 校验 |
|---|---|---|---|
| **🔶 前端源文件** | 扩展名 | **禁止新增 `.js`**(`web-ui/src/utils/generator/*.js` 6 个例外) | `tsconfig.exclude: ["src/**/*.js"]` 自动隔离 |
| **🔶 API 函数** | 返回类型 | **必须显式标注** `Promise<XxxResult>`;禁止 `Promise<any>` | 响应 interface 必须存在于 `types/api/**` 且有 re-export |
| **🔶 类型定义** | 位置 | 只放 `src/types/**` | `types/api/**` 镜像后端模块路径;**禁止**在 `api/*.ts` 内定义 interface |
| **🔶 SFC** | `<script setup>` | **必须带 `lang="ts"`** | 禁止 Options API |
| **🔶 类型导入** | `import type` | `web-mp` 因 `isolatedModules` **必须**用 | `web-ui` 也建议统一 |

---

## 6. 功能需求

> **业务功能需求**与 `master` **完全一致**(文件上传、用户/角色/菜单/部门/字典/通知/定时任务/代码生成、L1/L2 校验、审计、权限点矩阵、数据权限),见 `docs/base-postgresql-vue3-需求文档.md` §6 / §8。
> **SB3 升级需求**已在 `springboot3` 交付(JDK 17 / Jakarta / Security 6 / springdoc / starter / 配置),见 `docs/springboot3-需求文档.md` §6.9。
> 本节只展开 **§6.10 前端 TypeScript 化专题**。

### 6.10 🔶 前端 TypeScript 化需求(本分支核心)

#### 6.10.1 web-ui TS 工程配置要求

| 项 | 要求 | 实测现状 |
|---|---|---|
| `tsconfig.json` | 必须存在 | ✅ |
| `target` / `module` / `moduleResolution` | `esnext` / `esnext` / `bundler` | ✅ |
| `strict` | **`true`**(完整严格模式) | ✅ |
| `baseUrl` + `paths` | `"@/*": ["src/*"]` | ✅ |
| `include` | `src/**/*.ts` / `src/**/*.vue` / `vite/**/*.ts` / `auto-imports.d.ts` / `vite.config.ts` | ✅ |
| `exclude` | `node_modules` / `dist` / **`src/**/*.js`** | ✅(隔离 generator 的 6 个 `.js`) |
| `types` | `["node", "vite/client", "element-plus/global"]` | ✅ |
| `skipLibCheck` | `true`(三方库类型不全时不阻塞) | ✅ |
| `noEmit` | `true`(只检查不产出) | ✅ |
| `allowJs` | `true`(允许 `.js` 混存,受 `exclude` 约束) | ✅ |
| `vite.config.ts` | 必须存在(无 `.js` 版) | ✅ |
| `package.json` | 含 `typescript@5.6.3` / `vue-tsc@2.1.10` / `@vue/tsconfig@0.5.1` | ✅ |
| `typecheck` 脚本 | **当前缺失**,需手敲 `npx vue-tsc --noEmit` | ❌(A-16 / M5) |

#### 6.10.2 web-mp TS 工程配置要求

| 项 | 要求 | 实测现状 |
|---|---|---|
| `tsconfig.json` | 必须存在 | ✅ |
| `target` / `module` / `moduleResolution` | `ES2020` / `ESNext` / `Bundler` | ✅ |
| `strict` | **`false`**(起步),`noImplicitAny: false` | ✅(ADR-21 取舍) |
| `isolatedModules` | **`true`**(要求类型导入用 `import type`) | ✅ |
| `baseUrl` + `paths` | `"@/*": ["src/*"]` | ✅ |
| `include` | `src/**/*.ts` / `src/**/*.d.ts` / `src/**/*.tsx` / `src/**/*.vue` / `vite.config.ts` | ✅ |
| `exclude` | `node_modules` / `dist` / `unpackage` / `scripts` | ✅ |
| `types` | `[]`(不引 node 类型,避免与 uni-app 冲突) | ✅ |
| `vite.config.ts` | 必须存在 | ✅ |
| `package.json` | 含 `typescript@^5.4.5` / `vue-tsc@^2.0.19` / **`typecheck` 脚本** | ✅ |

#### 6.10.3 类型分层要求

| 目录 | 职责 | 要求 |
|---|---|---|
| `types/api/common.ts` | `AjaxResult` / `R<T>` / `ListResult<T>` / `PageQuery` / `TableDataInfo` | **禁止**放业务字段 |
| `types/api/login.ts` | `LoginForm` / `RegisterForm` / `LoginInfoResult` / `UserInfoResult` / `CaptchaInfoResult` | `CaptchaInfoResult` 含 `uuid` / `img` / `captchaEnabled`(**kaptcha 语义**) |
| `types/api/menu.ts` | `RouterVO` / `MetaVO` | — |
| `types/api/monitor/**`(6 个) | `cache` / `job` / `jobLog` / `logininfor` / `online` / `operlog` | 与 `api/monitor/**` **一一对应** |
| `types/api/system/**`(8 个) | `config` / `dept` / `dict` / `menu` / `notice` / `post` / `role` / `user` | 与 `api/system/**` **一一对应** |
| `types/api/tool/gen.ts` | `GenTable` / `GenTableColumn` | — |
| `types/api/index.ts` | **统一出口**,re-export 所有类型 | 新增类型必须在此登记 |
| `types/components.d.ts` | 全局组件类型(`unplugin-vue-components` 生成) | **禁止手工修改** |
| `types/global.d.ts` | `window` 扩展 / `ComponentCustomProperties`(挂载 `useDict` / `download` / `parseTime` / `resetForm` / `handleTree` / `addDateRange` / `getConfigKey` / `selectDictLabel` / `selectDictLabels`) | 手工维护 |
| `types/index.ts` | 对外统一入口 | — |
| `web-mp/src/types/global.d.ts` | 移动端全局声明 | — |
| **合计** | **22 个文件** | — |

#### 6.10.4 API 层类型化要求

| 要求 | 说明 | 示例 |
|---|---|---|
| **显式返回类型** | 每个导出函数必须标注 `Promise<...>` | `export function login(...): Promise<LoginInfoResult>` |
| **入参类型化** | 有 query/body 的函数必须用 interface | `export function listUser(query: SysUserQuery): Promise<ListResult<SysUser>>` |
| **禁止 `any` 返回值** | 不允许 `Promise<any>` 作为公开签名 | — |
| **`import type`** | 仅导入类型时必须用 | `import type { AjaxResult } from '@/types'` |
| **统一从 `@/types` 导入** | 不直接写深层路径 | ✅ |

**已类型化的 API 文件**:web-ui **19 个**(`login` / `menu` / `monitor/{cache,job,jobLog,logininfor,online,operlog}` / `system/{config,dept,dict/data,dict/type,menu,notice,post,role,user}` / `tool/gen`);web-mp **5 个**(`index` / `login` / `system/{dept,role,user}`)。

#### 6.10.5 单文件组件要求

| 要求 | 说明 |
|---|---|
| `<script setup lang="ts">` | 所有新增/改动 `.vue` 必须带 `lang="ts"` 且用组合式 API |
| 禁止 Options API | 与 TS 类型推导配合差 |
| `ref<T>()` / `reactive<T>()` | 泛型显式标注,避免 `never[]` 推断 |
| Props/Emits 类型化 | `defineProps<{...}>()` / `defineEmits<{...}>()` |
| 全局方法调用 | 通过 `types/global.d.ts` 的 `ComponentCustomProperties` 声明,不写 `@ts-ignore` |
| 数量 | web-ui **95 个 `.vue`**、web-mp `pages/**` 全部 |

#### 6.10.6 类型检查执行要求

| 端 | 命令 | 脚本 | 是否阻塞构建 |
|---|---|---|---|
| `web-ui` | `npx vue-tsc --noEmit` | **无 `typecheck` 脚本**(A-16) | **否**(`build:prod` 只跑 `vite build`) |
| `web-mp` | `pnpm typecheck` | `"typecheck": "vue-tsc --noEmit"` | **否** |

> ADR-25 取舍:**类型检查独立于构建**。迁移期强校验会阻塞开发;当前靠开发者本地自觉,M5 计划并入 CI。

#### 6.10.7 迁移完整性要求

| 要求 | 验收口径(实测) |
|---|---|
| `web-ui/src` 遗留 `.js` 仅 `utils/generator/` 下 **6 个** | `config.js` / `css.js` / `drawingDefault.js` / `html.js` / `js.js` / `render.js` |
| `web-mp/src` 无任何 `.js` | **0** |
| 无 `main.js` / `permission.js` / `settings.js` 残留 | 已全部改为 `.ts` |
| 无重复实现(同模块同时有 `.js` 和 `.ts`) | 逐目录核对 |
| `vite.config.js` 已改名 `vite.config.ts` | 两端各自核对 |
| 主题色保留 | `web-ui/src/assets/styles/basic.scss` 含 `--el-color-primary: #1890ff` |

#### 6.10.8 与后端的契约边界

| 规则 | 说明 |
|---|---|
| **后端零感知** | TS 化不改任何 HTTP 契约;`/login` 等接口路径/入参/出参字节级不变 |
| **类型来源** | `types/api/**` 字段必须**镜像后端 DTO/VO**;后端变更时同步改类型 |
| **不做运行时校验** | TS 类型是编译期的;运行时不新增校验逻辑(校验仍在后端) |
| **验证码契约** | 本分支是 **kaptcha**:`LoginForm` 只有 `code` + `uuid`;**没有** `captchaData`(那是 tianai 分支) |
| **代码生成器协同** | `extend-generator` 产出的前端代码仍是 JS 风格 → 需手工 ts 化(R30 / A-9) |

#### 6.10.9 与 `springboot3` 的差异边界(必须明确)

| 维度 | `springboot3` | 本分支 | 是否改后端 |
|---|---|---|---|
| 后端 7 模块 | SB 3.5.16 / JDK 17 | **完全相同** | ❌ |
| 数据库 34 表 | `basicdb` | **完全相同** | ❌ |
| HTTP 接口契约 | — | **完全相同** | ❌ |
| `web-ui` 语言 | JS | **TS**(79 `.ts` / 6 `.js` / 95 `.vue`) | ❌ |
| `web-mp` 语言 | JS | **TS**(19 `.ts` / 0 `.js`) | ❌ |
| 验证码 | kaptcha 2.3.3 | **kaptcha 2.3.3**(未换 tianai) | ❌ |
| 主题色 | `--el-color-primary: #1890ff` | **相同**(从 `springboot3` 单独恢复) | ❌ |

### 6.11 其它功能需求(与 `springboot3` 一致)

> 文件上传下载、L1/L2 校验、错误明细、各业务模块字段、审计、权限点矩阵、数据权限、验证码(kaptcha)—— 全部与 `springboot3` 一致,不再重复。

---

## 7. 非功能需求

| 属性 | 目标 |
|---|---|
| 一致性 | 事务边界在 Service;异步日志不污染主链路 |
| 可靠性 | 操作日志失败不影响主业务;定时任务异常隔离 |
| 性能 | 列表分页 1000 条 < 1s;登录链路 < 500ms;**TS 类型编译期擦除,运行时零开销** |
| 审计合规 | 90 天操作日志可查询、可导出 |
| 安全 | RSA / BCrypt / JWT 30min / XSS / 重复提交 / 限流 / CORS(**⬆️ 继承**) |
| 可用性 | 单实例 99.9% |
| 部署 | 单一可执行 jar;运行时 **JDK 17**;前端 `vite build` 产物 |
| **🔶 可维护性** | 新增前端代码必须 TS;`types/api/**` 契约与后端同步;`vue-tsc` 0 error |
| **🔶 构建体积** | TS 化后 bundle 体积与 `springboot3` 差异 **< 5%**(类型不产出代码) |
| **🔶 类型覆盖率** | `web-ui/src` 99.2% TS(79/85 源文件);`web-mp/src` 100% TS |

---

## 8. 权限与角色

> RBAC 权限点矩阵与数据权限 **与 `springboot3` / `master` 完全一致**(15 个模块 × 增删改查导出导入),详见 `docs/base-postgresql-vue3-需求文档.md` §8。
> 本分支**不新增任何权限点、不改任何菜单数据**;唯一相关变化是前端 `v-hasPermi` / `v-hasRole` 指令的 TS 化(`directive/permission/*.ts`)。

---

## 9. 边界与不做清单

| 项 | 说明 |
|---|---|
| 行为验证码 | 走 `springboot3-tianai-captcha` |
| 业务流程 | 走 `springboot3-extend-flowable` |
| JDK 21 / SB 4 / Jackson 3 / Security 7 | 走 `springboot4` / `springboot4_TypeScript` |
| 微服务化 / K8s / 灰度 | 不做 |
| SSO 联邦 | 不做 |
| **后端改动** | **零改动** |
| **数据库变更** | **零变更** |
| **`web-mp` strict 模式** | 本分支 `strict: false`;收紧列 M4 |
| **`vue-tsc` 并入 CI** | 未做,列 M5 |
| **`web-ui` `typecheck` 脚本** | 未加,列 A-16 / M5 |
| **代码生成器 TS 模板** | 未做,列 A-9 / R30 |
| **`utils/generator/*.js`** | 明确保留 JS(ADR-24) |
| **前端单元测试** | 不引入 |
| 移动端完整业务 | 仅登录 + 通知 + 个人中心 |
| 国际化词条 | 已引入,词条待补 |

---

## 10. 验收标准

> **AC-01~AC-24 继承自 `springboot3`**(编译 / 启动 / 表数量 / 登录 / CRUD / 锁屏 / 代码生成 / 定时任务 / 导出 / XSS / 重复提交 / Swagger / jakarta 残留 / springdoc 注解 / starter 变体 / 配置键等),见 `docs/springboot3-需求文档.md` §10。下表为本分支**新增 TS 专项 AC**。

| # | 验收项 | 验收方法 |
|---|---|---|
| AC-01~AC-24 | 见 `springboot3` PRD | 同左 |
| **AC-25** | `web-ui` **`npx vue-tsc --noEmit` 0 error** | 终端(`web-ui/`) |
| **AC-26** | `web-mp` **`pnpm typecheck` 0 error** | 终端(`web-mp/`) |
| **AC-27** | `web-ui/src` `.ts` 数量 = **79**;`.js` 数量 = **6** 且全部在 `utils/generator/` | 终端计数 |
| **AC-28** | `web-mp/src` `.ts` 数量 = **19**;`.js` 数量 = **0** | 终端计数 |
| **AC-29** | `web-ui/src/types` **22 个文件**;`types/api/index.ts` 有全部 re-export | 终端 + 代码走查 |
| **AC-30** | 两端均有 `tsconfig.json`,且 `vite.config.ts` 存在(无 `.js` 版) | 文件检查 |
| **AC-31** | `web-ui` tsconfig `strict: true`;`web-mp` tsconfig `strict: false` + `isolatedModules: true` | 读文件 |
| **AC-32** | 抽查 5 个 `api/*.ts`,均有显式 `Promise<...>` 返回类型 | 代码走查 |
| **AC-33** | 抽查 5 个 `.vue`,均为 `<script setup lang="ts">` | 代码走查 |
| **AC-34** | `pnpm dev`(web-ui)/ `pnpm dev:h5`(web-mp) 可启动,控制台无 TS 相关报错 | 终端 |
| **AC-35** | **故意改错一个 `api` 返回类型 → `vue-tsc` 必须报错**(证明检查生效) | 终端 |
| **AC-36** | 登录 / CRUD / 锁屏 / 代码生成 / 定时任务 全链路回归通过 | 浏览器 |
| **AC-37** | **后端与 DB 零改动**:`git diff springboot3 -- basic-* extend-* business-core pom.xml file-sql` 为空 | 终端 |
| **AC-38** | 主题色保留:`basic.scss` 含 `--el-color-primary: #1890ff` | 文件检查 |

---

## 附录A 待确认问题清单

| 编号 | 问题 | 建议默认 | 阻塞 |
|---|---|---|---|
| A-1~A-13 | 继承项(移动端动态路由 / 锁屏 / refresh / 日志保留 / 互踢 / 富文本 / 主子表 / deprecation / 生成器 SB3 模板 / actuator / druid IP / virtual threads / 校验分组) | 见 `springboot3` PRD | 否 |
| **A-14** | **`web-ui` 何时加 `typecheck` 脚本?** | 建议尽快加(与 web-mp 对齐),成本极低 | 否 |
| **A-15** | **`vue-tsc` 何时并入 CI?** | 建议 M5,先观察 1~2 个迭代的误报率 | 否 |
| **A-16** | **`web-mp` 何时收紧 `strict: true`?** | 建议 M4,依赖 uni-app 类型定义完善 | 否 |
| **A-17** | **是否引入 ESLint + `@typescript-eslint`?** | 当前无 lint 配置,建议后续引入 | 否 |
| **A-18** | **代码生成器是否加 TS 模板?** | **建议加**(否则每次生成都要手工 ts 化,R30) | 否 |
| **A-19** | **`utils/generator/*.js` 是否长期保留 JS?** | 建议长期保留(ADR-24),收益极低 | 否 |
| **A-20** | **是否引入前端单元测试(Vitest)?** | 建议后续,独立于 TS 化任务 | 否 |
| **A-21** | **`web-ui` 整目录复制自 `springboot2_TypeScript`,是否还有其它 SB2 时代差异?** | 已恢复 `basic.scss`;建议做一次逐目录 diff 复核(R31) | 否 |

---

## 附录B 业务回填模板

```markdown
### 模块: <名称>
| 字段 | 类型 | 必填 | 校验 |
|---|---|---|---|
| ... | ... | ... | ... |
```

---

## 附录C TS 化交付检查清单

- [ ] `web-ui/tsconfig.json` 存在,`strict: true`,`exclude` 含 `src/**/*.js`
- [ ] `web-mp/tsconfig.json` 存在,`strict: false` + `isolatedModules: true`
- [ ] 两端 `vite.config.ts` 存在(无 `.js` 版)
- [ ] `web-ui/src/types/` **22 个文件**齐全,`index.ts` 有全部 re-export
- [ ] `web-ui/src` **79 个 `.ts`**,遗留 `.js` 仅 `utils/generator/` **6 个**
- [ ] `web-mp/src` **19 个 `.ts`**,**0 个 `.js`**
- [ ] `npx vue-tsc --noEmit`(web-ui)**0 error**
- [ ] `pnpm typecheck`(web-mp)**0 error**
- [ ] 抽查 `api/*.ts` 有显式返回类型
- [ ] 抽查 `.vue` 均 `<script setup lang="ts">`
- [ ] `basic.scss` 主题色保留(`#1890ff`)
- [ ] 登录 / CRUD / 锁屏 / 代码生成 / 定时任务 回归通过
- [ ] 后端与 DB 零改动(`git diff springboot3` 为空)

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `springboot3` PRD(v0.1)为基线(G1~G12 / AC-01~AC-24 / A-1~A-13 继承),新增 G13~G18 TS 目标、§6.10 前端 TS 化需求(9 小节:两端工程配置 / 类型分层 12 行 / API 类型化 / SFC / 检查执行 / 迁移完整性 / 契约边界 / 与 springboot3 差异边界)、场景 S11~S14、AC-25~AC-38、A-14~A-21、附录C 检查清单 13 项 |
