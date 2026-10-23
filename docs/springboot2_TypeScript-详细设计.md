# 基础平台(base-postgresql-vue3) 详细设计(LLD) — `springboot2_TypeScript` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot2_TypeScript`** 分支
> 前置依赖:《架构方案》v0.1、《需求文档》v0.1、《概要设计》v0.1
> 本文档细化到「类 / 方法 / 关键逻辑」。**后端部分与 `master` 完全一致**(未改一行);**本分支新增 §2.4 前端类型模型、§4.6 前端核心引擎(TS 重写)、§8.3/§8.4 两端 TS 实现、§11 TC-21~TC-30**。
> 本分支改动以 **🔷** 标出。

---

## 1. 总体包结构

### 1.1 后端包结构(🟰 与 `master` 逐字节一致,**未修改**)

```
com.basic
├── BasicApplication                # 启动类
├── BasicServletInitializer         # war 启动适配
├── web
│   ├── config/SwaggerConfig
│   └── controller
│       ├── common/{CaptchaController, CommonController}
│       ├── monitor/{CacheController, ServerController, SysLogininforController, SysOperlogController, SysUserOnlineController}
│       ├── system/{SysConfigController, SysDeptController, SysDictDataController, SysDictTypeController, SysIndexController, SysLoginController, SysMenuController, SysNoticeController, SysPostController, SysProfileController, SysRegisterController, SysRoleController, SysUserController}
│       └── tool/{SwaggerController, TestController}
├── framework
│   ├── aspectj/{DataScopeAspect, DataSourceAspect, LogAspect, RateLimiterAspect}
│   ├── config/{ApplicationConfig, CaptchaConfig, DruidConfig, FastJson2JsonRedisSerializer, FilterConfig, JacksonConfig, KaptchaTextCreator, MyBatisConfig, MybatisPlusConfig, RedisConfig, ResourcesConfig, SecurityConfig, ServerConfig, ThreadPoolConfig, properties/*}
│   ├── datasource/{DynamicDataSource, DynamicDataSourceContextHolder}
│   ├── interceptor/{RepeatSubmitInterceptor, impl/SameUrlDataInterceptor}
│   ├── jackson/BigNumberSerializer
│   ├── manager/{AsyncManager, ShutdownManager, factory/AsyncFactory}
│   ├── security
│   │   ├── filter/JwtAuthenticationTokenFilter
│   │   └── handle/{AuthenticationEntryPointImpl, LogoutSuccessHandlerImpl, MyMetaObjectHandler}
│   └── web
│       ├── domain/{Server, server/{Cpu, Jvm, Mem, Sys, SysFile}}
│       ├── exception/GlobalExceptionHandler
│       └── service/{PermissionService, SysLoginService, SysPermissionService, SysRegisterService, TokenService, UserDetailsServiceImpl}
├── common
│   ├── annotation/{Anonymous, DataScope, DataSource, Excel, Excels, Log, RateLimiter, RepeatSubmit}
│   ├── config/BasicConfig
│   ├── constant/{Constants, GenConstants, HttpStatus, ScheduleConstants, UserConstants}
│   ├── core
│   │   ├── controller/BaseController
│   │   ├── domain/{AjaxResult, BaseEntity, R, TableDataInfo, TreeEntity, TreeSelect, YjyBaseEntity, entity/*, model/{LoginBody, LoginUser, RegisterBody}}
│   │   ├── page/{PageDomain, TableSupport}
│   │   ├── redis/RedisCache
│   │   └── text/{CharsetKit, Convert, StrFormatter}
│   ├── enums/{BusinessStatus, BusinessType, DataSourceType, HttpMethod, LimitType, OperatorType, UserStatus}
│   ├── exception/{base/BaseException, file/*, job/TaskException, user/*, DemoModeException, GlobalException, ServiceException, UtilException}
│   ├── filter/{RepeatableFilter, RepeatedlyRequestWrapper, XssFilter, XssHttpServletRequestWrapper}
│   ├── utils/{Arith, DateUtils, DictUtils, ExceptionUtil, JsonUtils, LogUtils, MessageUtils, PageUtils, SecurityUtils, SpringUtils, StringUtils, Threads, bean/*, file/*, html/*, http/*, ip/*, poi/*, reflect/*, sign/{Base64, Md5Utils, RsaUtils}, sql/SqlUtil, uuid/*}
│   └── xss/{Xss, XssValidator}
├── quartz (extend-quartz)
│   ├── config/ScheduleConfig
│   ├── controller/{SysJobController, SysJobLogController}
│   ├── domain/{SysJob, SysJobLog}
│   ├── mapper/{SysJobMapper, SysJobLogMapper}
│   ├── service/{ISysJobService, ISysJobLogService, impl/*}
│   ├── task/RyTask
│   └── util/{AbstractQuartzJob, CronUtils, JobInvokeUtil, QuartzDisallowConcurrentExecution, QuartzJobExecution, ScheduleUtils}
├── generator (extend-generator)
│   ├── config/GenConfig
│   ├── controller/GenController
│   ├── domain/{GenTable, GenTableColumn}
│   ├── mapper/{GenTableMapper, GenTableColumnMapper}
│   ├── service/{IGenTableService, IGenTableColumnService, impl/*}
│   └── util/{GenUtils, VelocityInitializer, VelocityUtils}
└── system (basic-admin-core)
    ├── domain/{SysConfig, SysFiles, SysLogininfor, SysNotice, SysNoticeRead, SysOperLog, SysPost, SysRoleDept, SysRoleMenu, SysUserOnline, SysUserPost, SysUserRole, vo/{MetaVo, RouterVo}}
    ├── mapper/{SysConfigMapper, SysDeptMapper, SysDictDataMapper, SysDictTypeMapper, SysFilesMapper, SysLogininforMapper, SysMenuMapper, SysNoticeMapper, SysNoticeReadMapper, SysOperLogMapper, SysPostMapper, SysRoleDeptMapper, SysRoleMapper, SysRoleMenuMapper, SysUserMapper, SysUserPostMapper, SysUserRoleMapper}
    └── service/{ISysConfigService, ISysDeptService, ISysDictDataService, ISysDictTypeService, ISysFilesService, ISysLogininforService, ISysMenuService, ISysNoticeReadService, ISysNoticeService, ISysOperLogService, ISysPostService, ISysRoleService, ISysUserOnlineService, ISysUserService, impl/*}

com.core (business-core)
├── controller/DemoController
├── domain/Demo
├── mapper/DemoMapper
└── service/{DemoService, impl/DemoServiceImpl}
```

> **🔷 本分支未新增/未删除/未修改任何 Java 文件**。
> 包命名约定(与 `master` 同):`com.basic` = 平台层,`com.core` = 业务层。

### 1.2 🔷 前端 TypeScript 目录结构(本分支核心新增)

#### 1.2.1 web-ui(PC)

```
web-ui/
├── tsconfig.json                          🔷 新增 · strict: true
├── auto-imports.d.ts                      🔷 新增 · unplugin-auto-import 生成
├── vite.config.ts                         🔷 由 vite.config.js 改名
├── package.json                           🔷 devDeps 加 typescript@5.6.3
│                                              / vue-tsc@2.1.10 / @vue/tsconfig@0.5.1
└── src/
    ├── main.ts                            🔷 原 main.js
    ├── permission.ts                      🔷 原 permission.js
    ├── settings.ts                        🔷 原 settings.js
    ├── App.vue                            🔷 <script setup lang="ts">
    │
    ├── types/                             🔷 新增目录(23 文件)
    │   ├── index.ts                       → 对外统一入口
    │   ├── api/
    │   │   ├── index.ts                   → 全部 re-export
    │   │   ├── common.ts                  → AjaxResult / R<T> / ListResult<T> / PageQuery / TableDataInfo
    │   │   ├── login.ts                   → LoginForm / RegisterForm / LoginInfoResult / UserInfoResult / CaptchaInfoResult
    │   │   ├── menu.ts                    → RouterVO / MetaVO
    │   │   ├── monitor/
    │   │   │   ├── cache.ts  job.ts  jobLog.ts
    │   │   │   ├── logininfor.ts  online.ts  operlog.ts  server.ts
    │   │   ├── system/
    │   │   │   ├── config.ts  dept.ts  dict.ts  menu.ts
    │   │   │   ├── notice.ts  post.ts  role.ts  user.ts
    │   │   │   └── tool/gen.ts
    │   ├── components.d.ts                → 全局组件类型(自动生成,禁改)
    │   └── global.d.ts                    → window / ComponentCustomProperties
    │
    ├── api/                               🔷 19 个 .ts
    │   ├── login.ts  menu.ts
    │   ├── monitor/{cache,job,jobLog,logininfor,online,operlog,server}.ts
    │   ├── system/{config,dept,dict/data,dict/type,menu,notice,post,role,user}.ts
    │   └── tool/gen.ts
    │
    ├── utils/                             🔷 13 个 .ts
    │   ├── auth.ts  common.ts  dict.ts  dynamicTitle.ts  errorCode.ts
    │   ├── index.ts  jsencrypt.ts  passwordRule.ts  permission.ts
    │   ├── request.ts  scroll-to.ts  theme.ts  validate.ts
    │   └── generator/                     ⚠️ 6 个 .js 保留
    │       └── config.js  css.js  drawingDefault.js  html.js  js.js  render.js
    │
    ├── store/                             🔷 8 个 .ts
    │   ├── index.ts
    │   └── modules/{app,dict,lock,permission,settings,tagsView,user}.ts
    │
    ├── plugins/                           🔷 6 个 .ts
    │   └── {auth,cache,download,index,modal,tab}.ts
    │
    ├── directive/                         🔷 4 个 .ts
    │   ├── index.ts
    │   ├── common/copyText.ts
    │   └── permission/{hasPermi,hasRole}.ts
    │
    ├── router/index.ts                    🔷 原 router/index.js
    ├── layout/components/index.ts         🔷 原 index.js
    ├── components/
    │   ├── IconSelect/requireIcons.ts     🔷
    │   └── SvgIcon/svgicon.ts             🔷
    └── views/**/*.vue                     🔷 95 个,全部 <script setup lang="ts">
```

#### 1.2.2 web-mp(移动)

```
web-mp/
├── tsconfig.json                     🔷 新增 · strict: false / isolatedModules: true
├── vite.config.ts                    🔷 由 vite.config.js 改名
├── sync-config.cjs                   (保留 · Node 脚本,不参与类型检查)
├── package.json                      🔷 devDeps 加 typescript@^5.4.5 / vue-tsc@^2.0.19
│                                        scripts 加 "typecheck": "vue-tsc --noEmit"
└── src/                              🔷 19 个 .ts · 0 个 .js
    ├── main.ts                       🔷
    ├── permission.ts                 🔷
    ├── config.ts                     🔷
    ├── api/
    │   ├── index.ts  login.ts
    │   └── system/{dept,role,user}.ts
    ├── store/
    │   ├── index.ts
    │   └── modules/{app,permission,user}.ts
    ├── utils/{auth,config,dict,jsencrypt,request}.ts
    ├── directive/permission.ts
    ├── types/global.d.ts
    └── pages/**/*.vue                🔷 <script setup lang="ts">
```

> **命名约定**:前端文件一律 `.ts` / `.vue`(**禁止新增 `.js`**);类型集中在 `src/types/`;`api/` 镜像后端 Controller 路径;`utils/generator/*.js` 是唯一例外(ADR-15)。

---

## 2. 领域模型与枚举

### 2.1 后端关键实体(🟰 与 `master` 一致,未修改)

| 实体 | 类 | 主键策略 | 关键状态字段 |
|---|---|---|---|
| 用户 | `SysUser` | MP `ASSIGN_ID`(雪花) | `status:0/1`;`delFlag:0/2` |
| 角色 | `SysRole` | MP `ASSIGN_ID` | `status:0/1`;`dataScope:1~5`;`delFlag:0/2` |
| 菜单 | `SysMenu` | MP `ASSIGN_ID` | `menuType:M/C/F`;`visible:0/1`;`isFrame:0/1`;`status:0/1` |
| 部门 | `SysDept` | MP `ASSIGN_ID` | `status:0/1` |
| 岗位 | `SysPost` | MP `ASSIGN_ID` | `status:0/1` |
| 字典类型 | `SysDictType` | MP `ASSIGN_ID` | `status:0/1` |
| 字典值 | `SysDictData` | MP `ASSIGN_ID` | `status:0/1`;`isDefault:Y/N` |
| 通知 | `SysNotice` | MP `ASSIGN_ID` | `status:0/1`;`noticeType:1/2` |
| 定时任务 | `SysJob` | MP `ASSIGN_ID` | `status:0/1`;`concurrent:0/1`;`misfirePolicy:0~3` |
| 任务日志 | `SysJobLog` | MP `ASSIGN_ID` | `status:0/1` |
| 操作日志 | `SysOperLog` | MP `ASSIGN_ID` | `businessType:0~9`;`status:0/1` |
| 登录日志 | `SysLogininfor` | MP `ASSIGN_ID` | `status:0/1` |
| 业务示例 | `Demo` | MP `ASSIGN_ID` | — |
| 文件 | `SysFiles` | MP `ASSIGN_ID` | — |
| 国际化 | `SysI18n` | MP `ASSIGN_ID` | — |
| 已读 | `SysNoticeRead` | MP `ASSIGN_ID` | 唯一 `(user_id, notice_id)` |

### 2.2 后端状态枚举(🟰 与 `master` 一致)

```java
public enum BusinessType { OTHER, INSERT, UPDATE, DELETE, GRANT, EXPORT, IMPORT, FORCE, GENCODE, CLEAN }
public enum BusinessStatus { SUCCESS(0), FAIL(1); }
public enum OperatorType { OTHER, MANAGE, MOBILE }
public enum DataSourceType { MASTER, SLAVE }
public enum UserStatus { OK("0","正常"), DISABLE("1","停用"), DELETED("2","删除"); }
public enum HttpMethod { GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE, ALL }
public enum LimitType { DEFAULT, IP, CLUSTER }
```

### 2.3 后端状态合法流转(🟰 与 `master` 一致)

| 对象 | 状态 | 合法转换 |
|---|---|---|
| 任务 | `0=运行` ↔ `1=暂停` | `changeStatus(0↔1)`;`run(→ 触发一次,不改 status)` |
| 角色 | `0=正常` / `1=停用` / `2=删除` | `add→0`;`update→0/1`;`remove→2`(逻辑删除) |
| 菜单 | `0=正常` / `1=停用` | 同上 |
| 通知 | `0=正常` / `1=关闭` | `add→0`;`update→0/1` |
| 字典类型 | `0=正常` / `1=停用` | 同上 |
| 用户 | `0=正常` / `1=停用` / `2=删除` | `add→0`;`changeStatus→0/1`;`remove→2` |

### 2.4 🔷 前端类型模型(本分支新增)

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

#### 2.4.2 登录类型(`types/api/login.ts`)

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

/** 验证码响应(本分支为 kaptcha 版,非 tianai) */
export interface CaptchaInfoResult extends AjaxResult {
  uuid: string
  img: string
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

/** 登录提交 */
export interface LoginForm {
  username: string
  password: string
  rememberMe?: boolean | string
  code: string
  uuid: string
}
```

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

export interface SysUserForm { /* 新增/编辑表单字段 */ }
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

#### 2.4.6 🔷 类型 ↔ 后端契约映射表

| 前端类型 | 后端来源 | 文件 |
|---|---|---|
| `AjaxResult<T>` | `com.basic.common.core.domain.AjaxResult` | `types/api/common.ts` |
| `R<T>` | `com.basic.common.core.domain.R` | `types/api/common.ts` |
| `TableDataInfo<T>` | `com.basic.common.core.domain.TableDataInfo` | `types/api/common.ts` |
| `PageQuery` | `BaseController.startPage()` + `PageDomain` | `types/api/common.ts` |
| `LoginForm` | `LoginBody` | `types/api/login.ts` |
| `LoginInfoResult` | `AjaxResult` + `token` | `types/api/login.ts` |
| `UserInfoResult` | `SysLoginController.getInfo()` 返回 Map | `types/api/login.ts` |
| `CaptchaInfoResult` | `CaptchaController.getCode()` 返回 Map | `types/api/login.ts` |
| `SysUser` | `com.basic.system.domain.SysUser` | `types/api/system/user.ts` |
| `SysRole` | `SysRole` | `types/api/system/role.ts` |
| `SysMenu` | `SysMenu` | `types/api/system/menu.ts` |
| `SysDept` | `SysDept` | `types/api/system/dept.ts` |
| `SysPost` | `SysPost` | `types/api/system/post.ts` |
| `SysDictType` / `SysDictData` | `SysDictType` / `SysDictData` | `types/api/system/dict.ts` |
| `SysConfig` | `SysConfig` | `types/api/system/config.ts` |
| `SysNotice` | `SysNotice` | `types/api/system/notice.ts` |
| `SysOperLog` | `SysOperLog` | `types/api/monitor/operlog.ts` |
| `SysLogininfor` | `SysLogininfor` | `types/api/monitor/logininfor.ts` |
| `SysJob` / `SysJobLog` | `SysJob` / `SysJobLog` | `types/api/monitor/{job,jobLog}.ts` |
| `SysUserOnline` | `SysUserOnline` | `types/api/monitor/online.ts` |
| `ServerInfo` | `Server` + `Cpu`/`Mem`/`Jvm`/`Sys`/`SysFile` | `types/api/monitor/server.ts` |
| `GenTable` / `GenTableColumn` | `GenTable` / `GenTableColumn` | `types/api/tool/gen.ts` |
| `RouterVO` / `MetaVO` | `com.basic.system.domain.vo.{RouterVo,MetaVo}` | `types/api/menu.ts` |

---

## 3. 框架机制

### 3.1 启动与包扫描(🟰 与 `master` 一致)

```java
@SpringBootApplication(
    scanBasePackages = {"com.basic", "com.core"},
    exclude = { DataSourceAutoConfiguration.class }
)
@MapperScan({"com.core.**.mapper"})
public class BasicApplication { ... }
```

- `exclude = DataSourceAutoConfiguration.class`:让 `DruidConfig` 接管数据源
- `scanBasePackages`:覆盖 RuoYi 默认 `com.basic.**`,把 `com.core.**` 也纳入
- `MapperScan`:**只**显式扫 `com.core.**.mapper`;`com.basic.**.mapper` 由 `MybatisPlusConfig.@MapperScan` 负责

### 3.2 动态 Schema 路由(🟰 与 `master` 一致)

```java
@Aspect @Component
public class DataSourceAspect {
    @Before("@annotation(ds)")
    public void before(DataSource ds) { DynamicDataSourceContextHolder.setDataSourceType(ds.value().name()); }
    @After("@annotation(ds)")
    public void after(DataSource ds) { DynamicDataSourceContextHolder.clearDataSourceType(); }
}
```

### 3.3 事务边界(🟰 与 `master` 一致)

- **写操作事务**:Service 实现类上 `@Transactional(rollbackFor = Exception.class)`
- **异步日志**:`AsyncManager.me().execute(AsyncFactory.recordOper(...))` 内部单独事务,与主业务隔离
- **跨表写入**:`SysRoleServiceImpl` 入口单事务,`updateRole -> deleteRoleMenu -> batchRoleMenu -> deleteRoleDept -> batchRoleDept`,任一失败整体回滚

### 3.4 审计切面(🟰 与 `master` 一致)

```java
@Aspect @Component
public class LogAspect {
    @Around("@annotation(log)")
    public Object around(ProceedingJoinPoint pjp, Log log) throws Throwable {
        long begin = System.currentTimeMillis();
        try {
            Object result = pjp.proceed();
            handleLog(pjp, log, result, BusinessStatus.SUCCESS);
            return result;
        } catch (Throwable t) {
            handleLog(pjp, log, null, BusinessStatus.FAIL);
            throw t;
        }
    }
}
```

- 切面**不**捕获异常,只记录,异常仍往外抛
- `handleLog` 内通过 `AsyncManager` 异步落 `sys_oper_log`
- 排除参数:`@Log(ignoreParam=true)` / 白名单(`password` / `oldPassword` / `newPassword` / `confirmPassword`)

### 3.5 重复提交拦截(🟰 与 `master` 一致)

```java
@Component
public class SameUrlDataInterceptor extends RepeatSubmitInterceptor {
    @Override
    boolean isRepeatSubmit(HttpServletRequest req, String nowParams) {
        String cacheKey = "REPEAT_SUBMIT:" + req.getSession().getId() + ":" + req.getRequestURI();
        String prev = redisCache.getCacheObject(cacheKey);
        if (prev != null && prev.equals(nowParams)) {
            return redisCache.getCacheExpire(cacheKey) > 0;
        }
        redisCache.setCacheObject(cacheKey, nowParams, intervalSeconds);
        return false;
    }
}
```

### 3.6 限流 / 3.7 XSS / 3.8 字段填充(🟰 与 `master` 一致)

- 限流:`RateLimiterAspect`;`DEFAULT`→Guava 单机;`IP`→按 IP;`CLUSTER`→Redis 分布式
- XSS:`XssFilter` + `XssHttpServletRequestWrapper` + `@Xss`(JSR-303)双重
- 字段填充:`MyMetaObjectHandler` 自动填 `createBy`/`createTime`/`updateBy`/`updateTime`

### 3.9 🔷 前端 TypeScript 构建与类型检查机制(本分支新增)

#### 3.9.1 web-ui 打包链

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
   └─ sass-embedded             SCSS
   │
   ▼
esbuild / rollup 转译(⚠️ 只擦除类型,不做类型检查)
   │
   ▼
dist/
```

> **关键认知**:Vite 用 esbuild 转译 TS,**不做类型检查**。类型错误只有 `vue-tsc` 能发现(ADR-16)。

#### 3.9.2 web-mp 打包链

```
pnpm dev:h5 / dev:mp-weixin / build:*
   │
   ├─ predev:h5 → node ./sync-config.cjs(同步配置)
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

#### 3.9.3 类型检查链(独立于构建)

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

#### 3.9.4 严格度矩阵

| 检查项 | web-ui | web-mp | 说明 |
|---|---|---|---|
| 变量隐式 `any` | ❌ 报错(strict) | ✅ 允许 | mp 端 uni-api 类型不全 |
| `null` / `undefined` 检查 | ❌ 报错(strict) | ✅ 允许 | mp 端大量可选参数 |
| 函数参数双变检查 | ❌ 报错(strict) | ✅ 允许 | — |
| `this` 隐式 `any` | ❌ 报错(strict) | ✅ 允许 | — |
| `isolatedModules` | 未开 | ✅ 开启 | mp 端要求 `import type` |
| 类型导入强制 `import type` | 否 | ✅ 是 | 由 `isolatedModules` 决定 |
| 第三方库内部类型 | 跳过 | 跳过 | `skipLibCheck: true` |

#### 3.9.5 类型错误的常见处置

| 报错类型 | 场景 | 处置 |
|---|---|---|
| `TS2307: Cannot find module '@/xxx'` | 路径别名未生效 | 检查 `tsconfig.paths` + `vite.resolve.alias` 是否一致 |
| `TS2339: Property 'xxx' does not exist` | 类型缺字段 | 补 `types/api/**` interface(不要用 `as any` 绕过) |
| `TS7016: Could not find declaration file` | 第三方库无类型 | `types/global.d.ts` 加 `declare module 'xxx' { ... }` |
| `TS1484: 'X' is a type and must be imported using 'import type'` | mp 端 `isolatedModules` | 改 `import type { X }` |
| `TS2532: Object is possibly 'undefined'` | strict 空检查 | 加 `?.` / `??` / 早期 `if` 守卫 |
| `TS2322: Type 'X' is not assignable to type 'Y'` | 字段类型不匹配 | 核对后端 DTO 类型;必要时收窄联合类型 |
| `TS2345: Argument of type ... not assignable` | 函数入参类型 | 补 interface 或显式标注 |

---

## 4. 核心引擎详细设计

### 4.1 登录引擎(`SysLoginService`,🟰 与 `master` 一致)

```java
public class SysLoginService {
    public LoginUser login(String username, String password, String code, String uuid) {
        validateCaptcha(uuid, code);                // 1. 图形验证码
        UserDetails u = userDetailsService.loadUserByUsername(username);
        if (u == null) throw UserPasswordNotMatchException("账号不存在");
        if (!bCrypt.matches(password, u.getPassword())) {
            asyncFactory.recordLogininfor(username, FAIL, "密码错误");
            throw UserPasswordNotMatchException();
        } else if (UserStatus.DISABLE.code.equals(u.getStatus())) {
            asyncFactory.recordLogininfor(username, FAIL, "账号停用");
            throw new ServiceException("账号已被停用,请联系管理员");
        }
        LoginUser loginUser = (LoginUser) u;
        tokenService.createToken(loginUser);
        asyncFactory.recordLogininfor(username, SUCCESS, "登录成功");
        return loginUser;
    }
    public void logout(LoginUser loginUser) { tokenService.delLoginUser(loginUser.getToken()); }
}
```

**`TokenService.createToken` / `refreshToken` / `JwtAuthenticationTokenFilter`** — 与 `master` 逐行一致,不再重复。

### 4.2 锁屏/解锁(🔷 前端 TS 实现)

> **不新增后端接口**。`SysLoginService` 内部已支持「复用 token」场景。
> 路径:`web-ui/src/store/modules/lock.ts` 维护 `locked` 状态,提交时直接调 `/login`,**后端不感知是锁屏解锁**。

```ts
// web-ui/src/store/modules/lock.ts(本分支 TS 版)
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login } from '@/api/login'

/** 🔷 锁屏状态模型 */
export interface LockState {
  locked: boolean
  userName: string
  password: string
  lockTime: number
}

export const useLockStore = defineStore('lock', () => {
  const locked = ref<boolean>(false)
  const userName = ref<string>('')

  /** 锁定 */
  function lock(name: string): void {
    userName.value = name
    locked.value = true
  }

  /** 解锁:复用 /login 入口,code/uuid 传占位值 */
  async function unlock(password: string): Promise<void> {
    await login(userName.value, password, 'LOCK_BYPASS', 'LOCK')
    locked.value = false
  }

  return { locked, userName, lock, unlock }
})
```

> 锁屏前端的 `Basic-Token` 一直保留在 cookie;不重新签发。

### 4.3 定时任务引擎(🟰 与 `master` 一致)

新增/改状态/立即执行 三方法、`AbstractQuartzJob` 抽象、`JobInvokeUtil` 反射、`RyTask` 示例 — 与 `master` 逐行一致,不再重复。
**前端侧 🔷**:`api/monitor/job.ts` 的 `listJob(): Promise<ListResult<SysJob>>` / `changeJobStatus(jobId, status): Promise<AjaxResult>` 等已显式标注。

### 4.4 代码生成引擎(🟰 与 `master` 一致,⚠️ 模板未 TS 化)

**导入表 / 生成** 逻辑与 `master` 一致。模板位于 `main/resources/vm/`:

- `vm/java/Controller.java.vm`
- `vm/java/Service.java.vm` / `ServiceImpl.java.vm`
- `vm/java/Mapper.java.vm` / `Mapper.xml.vm`
- `vm/vue/index.vue.vm` / `api.js.vm` / `index-tree.vue.vm` / `index.vue.vm`
- `vm/sql/sql.vm` / `vm/js/api.js.vm`

> ⚠️ **🔷 本分支已知缺口(R18)**:上述 `.vm` 模板**仍产出 JS 风格前端代码**(`<script setup>` 无 `lang="ts"`、`api.js.vm` 无类型)。生成后需**手工 ts 化**:
>
> 1. `api.js` → `api.ts`,补 `import type` + 显式返回类型
> 2. `index.vue` 的 `<script setup>` → `<script setup lang="ts">`
> 3. 在 `types/api/<module>/<entity>.ts` 补 interface 并 re-export
>
> 彻底方案:在 `extend-generator` 增一套 `vm/vue-ts/` 模板(列 A-11,后续迭代)。

### 4.5 动态路由/菜单(`SysMenuServiceImpl`,🟰 与 `master` 一致)

`buildMenus(List<SysMenu>)` 逻辑一致;前端 `permission.ts` 消费 `RouterVO[]`,`🔷 已用 `RouteRecordRaw` 类型断言收敛。

### 4.6 🔷 前端核心引擎(TS 重写,本分支新增)

#### 4.6.1 `utils/request.ts` — axios 封装(类型化)

```ts
import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getToken } from '@/utils/auth'
import { tansParams, blobValidate } from '@/utils/common'
import cache from '@/plugins/cache'
import errorCode from '@/utils/errorCode'
import type { AjaxResult } from '@/types'

/** 🔷 自定义请求配置(扩展 axios 原生) */
interface RequestConfig extends AxiosRequestConfig {
  isToken?: boolean
  repeatSubmit?: boolean
  isDownload?: boolean
}

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

// 响应拦截器(🔷 返回类型收敛为 AjaxResult)
service.interceptors.response.use(
  (res: AxiosResponse<AjaxResult>): any => {
    const code = res.data.code || 200
    const msg = errorCode[code] || res.data.msg || errorCode['default']
    if (code === 401) {
      // 登录过期 → 弹窗重登
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

> **关键约束**:`request()` 的返回值经拦截器已解包为 `AxiosResponse.data`(即 `AjaxResult`),因此 `api/*.ts` 的返回类型可直接写 `Promise<LoginInfoResult>`。

#### 4.6.2 `utils/dict.ts` — `useDict` 组合式函数(类型化)

```ts
import { ref, type Ref } from 'vue'
import { getDicts } from '@/api/system/dict/data'
import type { SysDictData } from '@/types'

/** 🔷 字典选项(下拉用) */
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
import router, { type RouteRecordRaw } from './router'   // ⚠️ router 实例非类型
import { getToken } from '@/utils/auth'
import { isRelogin } from '@/utils/request'
import { useUserStore } from '@/store/modules/user'
import { usePermissionStore } from '@/store/modules/permission'
import { useSettingsStore } from '@/store/modules/settings'

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  const permissionStore = usePermissionStore()
  const settingsStore = useSettingsStore()

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
    // 免登录白名单
    if (isWhiteList(to.path)) next()
    else next(`/login?redirect=${to.fullPath}`)
  }
})
```

#### 4.6.4 `api/login.ts` — 显式返回类型范例

```ts
import request from '@/utils/request'
import type { SysRegister, LoginInfoResult, UserInfoResult, CaptchaInfoResult, AjaxResult } from '@/types'

/** 登录 */
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

/** 验证码 */
export function getCodeImg(): Promise<CaptchaInfoResult> {
  return request({ url: '/captchaImage', headers: { isToken: false }, method: 'get', timeout: 20000 })
}
```

#### 4.6.5 `web-mp` 的 `utils/request.ts`(差异点)

```ts
// web-mp 用 uni.request 而非 axios;类型同样显式标注
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

---

## 5. 校验引擎

### 5.1 L1 字段级校验(后端,🟰 与 `master` 一致)

- 入口:`@Validated` + `javax.validation.constraints.*`
- 规则实现:`XssValidator`(自定义)+ `@NotBlank` / `@Email` / `@Size` / `@Pattern` / `@Excel`
- 失败处理:`GlobalExceptionHandler` 捕获 `MethodArgumentNotValidException` / `ConstraintViolationException` → 400 + msg

### 5.2 L2 跨业务校验(后端,🟰 与 `master` 一致)

| 校验项 | 位置 | 失败动作 |
|---|---|---|
| 用户名唯一 | `SysUserServiceImpl.checkUserNameUnique` | `UserException("user.register.not.unique")` |
| 角色 key 唯一 | `SysRoleServiceImpl.checkRoleKeyUnique` | `ServiceException` |
| 菜单不能关联自身为子 | `SysMenuServiceImpl` | `ServiceException` |
| 部门父级链路不能形成环 | `SysDeptServiceImpl.checkParentIdExist` | `ServiceException` |
| 通知发布必须先选 `status=1` | `SysNoticeServiceImpl` | `ServiceException` |
| 定时 invokeTarget 合法 | `ScheduleUtils` | `TaskException` |
| 字典值类型内唯一 | `SysDictDataServiceImpl.checkDictValueUnique` | `ServiceException` |
| 已读记录唯一 | `SysNoticeReadServiceImpl` | `insertOrUpdate` 幂等 |

### 5.3 🔷 前端编译期校验(本分支新增)

| 校验项 | 工具 | 失败动作 |
|---|---|---|
| 类型一致性 | `vue-tsc --noEmit` | 终端报错(**不阻断构建**) |
| 路径别名 | `tsconfig.paths` + `vite.alias` 双端一致 | `TS2307` |
| 未使用变量 | 需 ESLint(本分支未引入,A-12) | — |
| 类型导入规范 | `isolatedModules`(mp) | `TS1484` |
| 接口契约存在性 | 人工 review(`types/api/**` 是否同步) | — |

---

## 6. 业务模块实现(节选)

### 6.1 用户管理(后端,🟰 与 `master` 一致)

`SysUserServiceImpl` 的 `addUser` / `updateUser` / `removeUserByIds` / `resetPwd` — 与 `master` 逐行一致(含默认密码 `123456`、禁改超管、级联删 `user_role`/`user_post`)。

### 6.2 角色管理(后端,🟰 与 `master` 一致)

`checkRoleKeyUnique` / `authDataScope` — 一致。

### 6.3 菜单管理(后端,🟰 与 `master` 一致)

`treeselectByUserId` — 一致。

### 6.4 通知已读(后端,🟰 与 `master` 一致)

`readOnce` 走 `insertOrUpdate` + 唯一索引兜底 — 一致。

### 6.5 Demo 业务(backend `business-core`,🟰 与 `master` 一致)

`DemoController` — 一致。

### 6.6 🔷 前端模块实现(TS 版,本分支新增)

#### 6.6.1 用户管理页(`views/basic-admin/system/user/index.vue`)

```vue
<script setup lang="ts" name="User">
import { ref, reactive, toRefs, onMounted } from 'vue'
import { listUser, getUser, delUser, addUser, updateUser, resetUserPwd, changeUserStatus } from '@/api/system/user'
import type { SysUser, SysUserQuery, SysUserForm } from '@/types'
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

const rules = reactive<FormRules<SysUserForm>>({
  userName: [{ required: true, message: '用户名称不能为空', trigger: 'blur' }],
  nickName: [{ required: true, message: '用户昵称不能为空', trigger: 'blur' }],
  password: [{ required: true, message: '用户密码不能为空', trigger: 'blur' }]
})

onMounted(() => { getList() })
</script>
```

#### 6.6.2 登录页(`views/login.vue`)

```vue
<script setup lang="ts" name="Login">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCodeImg, login } from '@/api/login'
import { encrypt, decrypt } from '@/utils/jsencrypt'
import type { LoginForm, CaptchaInfoResult } from '@/types'

const router = useRouter()
const loginForm = reactive<LoginForm>({ username: '', password: '', rememberMe: false, code: '', uuid: '' })
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
  const res = await login(loginForm.username, encrypt(loginForm.password) as string, loginForm.code, loginForm.uuid)
  // token 已由 request 拦截器 / userStore 处理
  await router.push('/')
}
</script>
```

#### 6.6.3 移动端登录页(`web-mp/src/pages/login/index.vue`)

```vue
<script setup lang="ts">
import { ref, reactive } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
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

> `springboot2-extend-flowable` 分支提供:`extend-flowable` 模块 + Flowable 6.8.0 + `basicdb_flowable` schema。
> 本分支保留以下钩子位点(无实现):
>
> - `BusinessType.GRANT` 注解预留给工作流触发
> - `extend-*` 模式:把新模块挂到 `dependencyManagement` 即插即用
> - `gen_table` 字段预留 `is_workflow` 标志(待定)

---

## 8. 审计/报表/Web/安全

### 8.1 审计(后端,🟰 与 `master` 一致)

- 切面签名:`@Log(title="用户管理", businessType=BusinessType.UPDATE, operatorType=OperatorType.MANAGE)`
- 排除参数:`@Log(ignoreParam=true)` 时 JSON 参数整体不存
- 保存字段:operId / title / businessType / method / requestMethod / operatorType / operName / deptName / operUrl / operIp / operParam / jsonResult / status / errorMsg / operTime / costTime

### 8.2 报表(🟰 与 `master` 一致)

- 操作日志列表:`PageHelper.startPage()` → `SysOperLogMapper.selectOperLogList`
- 导出:`@Excel` 注解 + `ExcelUtil<>.exportExcel(response, list, "操作日志")`
- 监控:`ServerController` 实时返回 CPU/内存/磁盘/JVM(`oshi 6.1.6`)
- **🔷 前端侧**:`api/monitor/operlog.ts` 的 `listOperlog(query: SysOperLogQuery): Promise<ListResult<SysOperLog>>` 已类型化

### 8.3 🔷 Web 端(`web-ui`,TypeScript 版)

| 项 | 内容 |
|---|---|
| 入口 | `src/main.ts` → `createApp(App)`;**`: any` 已在 `app.use(ElementPlus, ...)` 处用 `// @ts-ignore` 处理类型冲突** |
| 路由守卫 | `src/permission.ts`(TS),`router.beforeEach`,产出 `RouteRecordRaw[]` |
| 鉴权 | 路由级 `meta.roles`/`meta.perms`;指令级 `v-hasPermi`/`v-hasRole`(`directive/permission/*.ts`);工具 `hasPermi()`/`hasRole()`(`utils/permission.ts`) |
| 网络 | `utils/request.ts`(axios 拦截器,统一 token / 401 / 错误码);响应体解包为 `AjaxResult` |
| 锁屏 | `store/modules/lock.ts`(`LockState` interface)+ 路由级 `views/lock.vue` |
| 字典 | `utils/dict.ts` 的 `useDict(...)` 返回 `Record<string, Ref<DictDataOption[]>>` |
| 上传 | `components/ImageUpload/index.vue` / `FileUpload/index.vue` → `POST /common/upload` |
| 全局组件 | `components/{Pagination,RightToolbar,Editor,FileUpload,ImageUpload,ImagePreview,DictTag}` 在 `main.ts` 注册;类型由 `types/components.d.ts` 提供 |
| 全局方法 | `useDict`/`download`/`parseTime`/`resetForm`/`handleTree`/`addDateRange`/`getConfigKey`/`selectDictLabel`/`selectDictLabels` 挂到 `globalProperties`;类型在 `types/global.d.ts` 声明 |
| 自动导入 | `unplugin-auto-import` 生成 `auto-imports.d.ts`(包含 `ref`/`computed`/`watch` 等) |
| 类型检查 | `npx vue-tsc --noEmit`(无 npm script,A-8) |

### 8.4 🔷 移动端(`web-mp`,TypeScript 版)

| 项 | 内容 |
|---|---|
| 入口 | `src/main.ts` → `createSSRApp` + Pinia + 显式注册 `up-` 组件 |
| 路由 | `pages.json` 静态 + `permission.ts` 全局守卫 |
| 鉴权 | `v-if="hasPermi([...])"`(uni-app 3 编译期不支持自定义指令,故用函数) |
| 网络 | `utils/request.ts`(uni.request 封装,泛型 `<T>` 返回) |
| 加密 | `utils/jsencrypt.ts` 用 `BasicConfig.rsaPublicKey` 加密 password |
| 存储 | `uni.setStorageSync('Basic-Token', token)` |
| 配置 | `src/config.ts` 维护 `baseUrl`(默认 `http://localhost:8080`) |
| API | `api/{index,login}.ts` + `api/system/{dept,role,user}.ts` |
| Store | `store/index.ts` + `modules/{app,permission,user}.ts` |
| 类型检查 | `pnpm typecheck` → `vue-tsc --noEmit`(`strict: false`) |
| 特殊 | `sync-config.cjs` / `scripts/fixup-mp-components.cjs` 保持 `.cjs`(Node 脚本,不在 `include`) |

### 8.5 安全(🟰 与 `master` 一致)

| 项 | 实现 |
|---|---|
| 密码 | BCrypt(内置 `BCryptPasswordEncoder`) |
| 登录传输 | 前端 `jsencrypt` RSA 加密 password |
| Token | HS512 JWT,`token.header=Authorization`,`token.expireTime=30` 分钟 |
| Token 存储 | Redis,key=`login_tokens:<uuid>`,与 JWT 解析后比对 |
| 防重放 | 同一 token 在 30min 内可用,期间可 `delLoginUser` 强退 |
| 跨域 | `CorsFilter` + `SecurityConfig` 显式白名单 |
| XSS | `XssFilter` 包装 request;`@Xss` 注解字段级 |
| SQL 注入 | MyBatis-Plus `#{}` 参数化 + `SqlUtil.filterKeyword` |
| 防爆破 | `RateLimiter` 注解(默认 5 QPS/10s) |
| 重复提交 | `RepeatSubmit` 注解 + `SameUrlDataInterceptor` |
| 操作日志 | 不可关闭 |
| 登录日志 | 不可关闭 |
| **🔷 前端类型安全** | `strict`(PC)+ `vue-tsc` 检查;**不构成安全边界**(类型擦除,运行时不校验) |

---

## 9. 错误码

### 9.1 后端错误码(🟰 与 `master` 一致)

| 类别 | code | 含义 | 触发场景 |
|---|---|---|---|
| HTTP | 200 | 成功 | — |
| HTTP | 400 | 参数校验失败 | `MethodArgumentNotValidException` / `ConstraintViolationException` |
| HTTP | 401 | 未登录 / token 失效 | `AuthenticationEntryPointImpl` |
| HTTP | 403 | 无权限 | `@PreAuthorize` 拒绝 |
| HTTP | 404 | 接口不存在 | Spring 默认 |
| HTTP | 429 | 重复提交 / 限流 | `RepeatSubmitInterceptor` / `RateLimiterAspect` |
| HTTP | 500 | 业务异常 | `ServiceException` / `GlobalException` / `TaskException` |
| 业务 | `1001` | 验证码错误 | `CaptchaException` |
| 业务 | `1002` | 验证码过期 | `CaptchaExpireException` |
| 业务 | `1003` | 用户名或密码错误 | `UserPasswordNotMatchException` |
| 业务 | `1004` | 账号已停用 | `UserException("user.blocked")` |
| 业务 | `1005` | 角色键已存在 | `checkRoleKeyUnique` |
| 业务 | `1006` | 用户名已存在 | `checkUserNameUnique` |
| 业务 | `1007` | 不允许修改超级管理员 | `User.isAdmin(...)` |
| 业务 | `1008` | 不允许删除超级管理员 | `User.isAdmin(...)` |
| 业务 | `1009` | 上级目录不允许是本级或下级 | `SysMenu` 自引用 |
| 业务 | `1010` | Cron 表达式不合法 | `CronUtils.isValid` |
| 业务 | `1011` | Job 目标字符串不合法 | `ScheduleUtils.isValidInvokeTarget` |
| 业务 | `1012` | 已存在同名通知已读记录 | 唯一索引兜底 |

> 前端通过 `utils/errorCode.ts`(**本分支已 TS 化**)做错误码 → 提示文案映射。

### 9.2 🔷 前端编译期错误码(TS 编号,非 HTTP)

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

### 10.1 后端(🟰 与 `master` 一致)

| 资源 | 位置 | 配置 |
|---|---|---|
| Tomcat 线程池 | `application.yml` | `max-threads=800` / `min-spare=10` / `accept-count=1000` |
| 异步任务线程池 | `ThreadPoolConfig` | `corePoolSize=10` / `maxPoolSize=50` / `queueCapacity=200` |
| Druid 连接池 | `application-druid.yml` | `min-idle=5` / `max-active=20` |
| Quartz 线程池 | `ScheduleConfig` | 默认 25 |
| Redis 缓存 | `RedisCache` | TTL 30min |
| 限流(单机) | Guava | `@RateLimiter(time=10, count=5)` |
| 限流(集群) | Redis | `@RateLimiter(type=CLUSTER)` |
| 重复提交 | Redis | 默认 500ms |

**锁使用**:`qrtz_locks` 由 Quartz 维护;业务无显式分布式锁(单机),多实例靠 Quartz `@DisallowConcurrentExecution`。

### 10.2 🔷 前端

| 资源 | 说明 |
|---|---|
| 主线程 | 浏览器/小程序 JS 单线程;TS 类型在编译期擦除,**运行时不增加任何开销** |
| 构建并发 | Vite 用 esbuild(多线程)/ rollup;`vue-tsc` 独立进程(**不参与** dev/build) |
| web-mp 后台进程 | `predev:mp-weixin` 会 `spawn` 一个 `--watch` 的 `fixup-mp-components.cjs`(detached) |
| 请求并发 | axios 默认无并发限制;`request.ts` 未加队列 |

---

## 11. 测试关注点

### 11.1 后端与通用(TC-01~TC-20,🟰 与 `master` 一致)

| # | 场景 | 重点 |
|---|---|---|
| TC-01 | 登录:账号+密码+验证码 三要素 | 错误码 1001/1002/1003 |
| TC-02 | 登录:密码 RSA 加密错误 | 500 + 明确 msg |
| TC-03 | Token 失效:30min 后访问 | 401 |
| TC-04 | 强退:`monitor/online/forceLogout` | 该 token 立即失效 |
| TC-05 | 角色赋菜单:删菜单 → 下次登录路由消失 | 列表 200 + 路由变 |
| TC-06 | 数据权限:`dataScope=4(本人)` | SQL 注入 `create_by=?` |
| TC-07 | 重复提交 | 500ms 内 → 429 |
| TC-08 | 限流 | 5QPS/10s 第 6 次 → 拒绝 |
| TC-09 | XSS 注入 | `<script>alert(1)</script>` 被过滤 |
| TC-10 | 字典缓存 | 修改字典后 `DictUtils.clearCache()` → 新值 |
| TC-11 | 定时任务:启用/暂停/执行 | Quartz 状态切换 |
| TC-12 | 定时任务:并发禁止 | `concurrent=1` 只跑一份 |
| TC-13 | 代码生成:导入 `sys_demo` → 下载 zip | 路径/字段/模板正确 |
| TC-14 | 操作日志:成功/失败均落库 | 异步不阻塞 |
| TC-15 | 移动端 H5 登录 | token 写入 uni.storage |
| TC-16 | 移动端 Token 失效 | 拦截器跳登录 |
| TC-17 | 锁屏解锁 | 密码错 → 锁屏;对 → 解锁 |
| TC-18 | 通知已读 | 重复点 → 不重复加已读 |
| TC-19 | 文件上传:超 10MB | `FileSizeLimitExceededException` |
| TC-20 | 优雅停机:`POST /monitor/shutdown` | 应用退出无残留事务 |

### 11.2 🔷 前端 TypeScript 专项(TC-21~TC-30)

| # | 场景 | 重点 |
|---|---|---|
| **TC-21** | `web-ui` 执行 `npx vue-tsc --noEmit` | **0 error**;若有历史遗留 `@ts-ignore` 需登记 |
| **TC-22** | `web-mp` 执行 `pnpm typecheck` | **0 error** |
| **TC-23** | 文件统计:`web-ui/src` `.ts`=79 / `.js`=6(均在 `utils/generator/`) | 无遗漏、无双份 |
| **TC-24** | 文件统计:`web-mp/src` `.ts`=19 / `.js`=0 | 无遗漏 |
| **TC-25** | 两端 `vite.config.ts` 存在,无 `vite.config.js` 残留 | 文件检查 |
| **TC-26** | 故意改错一个 `api` 返回类型 → `vue-tsc` 必须报错 | 验证类型检查真的生效 |
| **TC-27** | `pnpm dev`(web-ui)/ `pnpm dev:h5`(web-mp)启动 | 控制台无 TS 报错(esbuild 转译不校验,但语法错会报) |
| **TC-28** | 登录全链路回归(PC + H5) | `LoginInfoResult` 字段与后端一致 |
| **TC-29** | 抽查 5 个 `.vue` 均 `<script setup lang="ts">` | 无 `<script>`(无 lang)残留 |
| **TC-30** | 打包产物体积对比 `master` | 差异 < 5%(类型不产出代码) |

---

## 12. 需求变更记录

| 需求 | 落点 |
|---|---|
| 锁屏解锁 | §4.2(前端 `store/modules/lock.ts` TS 版)+ §6.1 + §8.3 |
| 移动端双前端 | §8.4 |
| 数据库 PostgreSQL | §3.1 + §8.5 |
| 国际化(词条待补) | §8.4(vue-i18n) |
| 操作日志 | §3.4 + §8.1 |
| 重复提交 | §3.5 |
| 限流 | §3.6 |
| XSS | §3.7 |
| Quartz 动态任务 | §4.3 |
| 代码生成 | §4.4(⚠️ 模板未 TS 化) |
| 数据权限 | §3 + 《需求文档》§8.2 |
| **🔷 前端全量 TS 化** | **§1.2 目录结构 + §2.4 类型模型 + §3.9 构建与检查 + §4.6 前端核心引擎 + §8.3/§8.4 两端实现** |
| **🔷 类型契约分层** | §2.4.6 类型↔后端映射表 |
| **🔷 `vue-tsc` 检查机制** | §3.9.3 + §3.9.4 严格度矩阵 + §5.3 |
| **🔷 代码生成模板未 TS 化(R18)** | §4.4 警示 + A-11 |
| **🔷 前端编译期错误码** | §9.2 |
| **🔷 TS 专项测试** | §11.2 TC-21~TC-30 |

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `master` LLD(v0.1)为基线;后端 §1.1/§2.1~2.3/§3.1~3.8/§4.1~4.5/§5.1~5.2/§6.1~6.5/§7/§8.1~8.2/§8.5/§9.1/§10.1/§11.1 标注为「与 master 一致」;新增 §1.2 前端目录(两端)· §2.4 类型模型(6 小节 + 映射表)· §3.9 构建与类型检查(5 小节)· §4.6 前端核心引擎(request/dict/permission/login/web-mp)· §5.3 前端编译期校验 · §6.6 前端模块实现 · §8.3/§8.4 两端 TS 实现 · §9.2 编译期错误码 · §10.2 前端并发 · §11.2 TC-21~TC-30 |
