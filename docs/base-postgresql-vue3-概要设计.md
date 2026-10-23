# 基础平台(base-postgresql-vue3) 概要设计(HLD)

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 `master` 分支
> 前置依赖:《架构方案》v0.1、《需求文档》v0.1

---

## 1. 设计目标与约束回顾

| 来源 | 关键约束 |
|---|---|
| 架构方案 §2 | JDK 11 + Spring Boot 2.5.14 锁死,数据库 PostgreSQL ≥ 15,统一 `basicdb` schema |
| 架构方案 §2 | JWT 30min + BCrypt;XSS + 重复提交 + 限流;操作/登录日志不可关闭 |
| 需求文档 §7 | 列表 1000 条 < 1s;登录 < 500ms;单一可执行 jar |
| 需求文档 §10 | AC-01~AC-14 |

**勿重复大段正文**,仅作引子。

---

## 2. 逻辑架构与模块划分

### 2.1 工程结构

```
base-postgresql-vue3/
├── pom.xml                 # 父 pom,7 子模块
├── basic-admin-api/        # 启动入口 + 全部 Controller
├── basic-admin-core/       # 业务核心(sys_*/gen_*/qrtz_*/demo)
├── basic-framework/        # 框架能力(Security/Redis/MyBatis/AOP/Filter)
├── basic-common/           # 工具/注解/枚举/基类
├── business-core/          # 业务扩展点(com.core 包)
├── extend-quartz/          # Quartz 集成
├── extend-generator/       # Velocity 代码生成
├── file-sql/               # 8 个 SQL(basicdb-vue3.sql 为唯一运行入口)
├── file-bin/  file-doc/    # 静态资源
├── web-ui/                 # PC(Vue3 + ElementPlus,JS)
└── web-mp/                 # 移动(uni-app3 + uview-plus3,JS)
```

### 2.2 组件职责

| 模块 | 关键类 | 职责 |
|---|---|---|
| `basic-admin-api` | `BasicApplication` | 启动类,扫描 `com.basic` + `com.core`;`@MapperScan("com.core.**.mapper")` |
| | `BasicServletInitializer` | 外部 Tomcat war 启动适配 |
| | `com.basic.web.controller.**` | 26 个 Controller |
| `basic-admin-core` | `com.basic.system.service.impl.**` | 业务 Service 实现 |
| | `com.basic.system.mapper.**` | MyBatis-Plus Mapper 接口 |
| | `com.basic.system.domain.**` | 实体 + VO |
| | `com.basic.system.mapper.*.xml` | 自定义 SQL 片段 |
| `basic-framework` | `SecurityConfig` | Spring Security 配置(JWT 无状态、BCrypt) |
| | `JwtAuthenticationTokenFilter` | 解析 token → SecurityContext |
| | `AuthenticationEntryPointImpl` / `LogoutSuccessHandlerImpl` | 鉴权失败 / 注销成功 |
| | `MybatisPlusConfig` / `MyBatisConfig` | MyBatis-Plus 分页、Mapper 扫描 |
| | `DataScopeAspect` | 数据权限 SQL 注入 |
| | `LogAspect` | 操作日志切面 |
| | `RateLimiterAspect` | 限流切面 |
| | `RepeatSubmitInterceptor` | 重复提交拦截 |
| | `RedisConfig` / `DruidConfig` / `JacksonConfig` | 配置 |
| | `DynamicDataSource` | 多数据源(可选) |
| | `SysLoginService` / `TokenService` | 登录 + Token |
| `basic-common` | `R` / `AjaxResult` / `TableDataInfo` | 统一返回结构 |
| | `BaseEntity` / `TreeEntity` / `YjyBaseEntity` | 实体基类 |
| | `SecurityUtils` / `StringUtils` / `DateUtils` | 通用工具 |
| | `@Log` / `@RepeatSubmit` / `@RateLimiter` / `@DataScope` / `@Anonymous` | 业务注解 |
| | `DictUtils` / `RedisCache` | 字典缓存 |
| `extend-quartz` | `ScheduleConfig` | Quartz 启动 |
| | `ScheduleUtils` | Cron 增删改与状态切换 |
| | `AbstractQuartzJob` / `JobInvokeUtil` | Job 抽象 + 反射 |
| | `SysJobServiceImpl` / `SysJobController` | 业务 + Web |
| `extend-generator` | `GenController` / `GenTableServiceImpl` | 生成器 |
| | `VelocityInitializer` / `VelocityUtils` | 模板引擎 |
| | `GenTable` / `GenTableColumn` | 元数据 |
| `business-core` | `Demo` / `DemoController` / `DemoService` | 业务示例 |

### 2.3 扩展 SPI

| SPI | 接入方式 | 用例 |
|---|---|---|
| 自定义 Job | 实现 `@Component("ryTask")` + `public void ryXxx(...)` | `extend-quartz` 的 `ryTask.ryNoParams` / `ryParams` |
| 自定义权限注解 | 沿用 `@PreAuthorize("@ss.hasPermi('...')")` | `SysPermissionService.hasPermi` |
| 字典加载 | 在 `DictUtils` 增枚举/缓存 key | — |
| 数据源 | 注解 `@DataSource(DataSourceType.SLAVE)` + `DynamicDataSourceContextHolder` | 多数据源 |
| 代码生成 | 新增 `vm/java/...` 模板 | — |

---

## 3. 核心流程设计

### 3.1 登录流程(状态机 + 时序)

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

**时序(ASCII)**

```
[PC/Mobile]        [Controller]      [SysLoginService]      [Redis/DB]
   │  GET /captchaImage              │                       │
   │ ────────────────▶  CaptchaController                  │
   │                                 │ 生成 Kaptcha(uuid)    │
   │ ◀────────────────  {uuid,img}   │                      │
   │                                 │                      │
   │  POST /login(username,pwd,code,uuid)                  │
   │ ────────────────▶  SysLoginController                 │
   │                       │         │                      │
   │                       │  login(LoginBody)              │
   │                       │ ────────▶  ① RSA 解密(前端已加密)
   │                       │         │  ② checkCaptcha(uuid,code)
   │                       │         │  ③ UserDetailsService.loadUserByUsername
   │                       │         │  ④ BCrypt.matches(raw, dbPwd)
   │                       │         │  ⑤ TokenService.createToken(loginUser)
   │                       │         │     → 写 redis cache,UUID-as-key
   │                       │         │  ⑥ AsyncManager.execute(loginLog task)
   │ ◀──────────────── {token}        │  写 sys_logininfor  │
```

### 3.2 锁屏/解锁

```
PC store/lock.js 触发 lock 状态 → 锁屏页
   │
   ▼ 提交 unlockPassword
   │  POST /login(reuse token, only password)
   ▼
SysLoginService.login: 短路验证码检查;校验密码;
   成功 → 返回原 token;失败 → UserPasswordNotMatchException
```

> **不新增接口**;SysLoginService 内部识别「锁屏解锁场景」。

### 3.3 定时任务调度

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

**异常分支**:Job 异常被 `AbstractQuartzJob` 捕获,不会让 Scheduler 整体挂掉;`misfirePolicy` 控制错过触发时的策略。

### 3.4 代码生成

```
导入表 → GenTableServiceImpl.importGenTable
  → GenTableColumnServiceImpl.importGenTableCol
  → 写 gen_table / gen_table_column

生成:
  download(genTableIds) →
    VelocityEngine 渲染 .vm 模板(java/Mapper/Service/Controller/Vue/sql/js)
    → 打包 zip → 浏览器下载
```

### 3.5 移动端登录

```
web-mp/pages/login/index.vue
  GET /captchaImage        (utils/request.js)
  POST /login              (RSA + 验证码)
  ↓  Basic-Token 存到 uni.storage
GET /getInfo                (用户/角色/权限)
GET /getRouters             (本分支先消费即可,后续转 pages.json)
```

---

## 4. 数据设计(概要)

### 4.1 Schema 布局

| schema | 表数 | 内容 |
|---|---|---|
| `basicdb` | 34 | 全部业务表(下分 `gen_*` 2 / `qrtz_*` 11 / `sys_*` 21) |
| `public` | 0(本项目不使用) | — |

### 4.2 核心表(ER 简化)

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

> 详细字段/索引见《数据库设计》。

### 4.3 关键索引/查询

| 关键查询 | 命中索引 |
|---|---|
| `select * from sys_user where user_name = ?` | `uk_user_name`(逻辑唯一) |
| `select * from sys_role where role_key = ? and del_flag=0` | `uk_role_key` |
| `select * from sys_menu where status=0 and perms is not null` | 全表扫描(菜单量小) |
| `select * from sys_user_role where user_id=?` | `(user_id, role_id)` 复合 |
| `select * from sys_role_menu where role_id=?` | `(role_id, menu_id)` 复合 |
| `select * from qrtz_triggers where TRIGGER_GROUP=?` | Quartz 自带索引 |

---

## 5. 接口清单(摘录,详见 Controller 类)

### 5.1 对外 REST

| 方法 | 路径 | 说明 | 权限 |
|---|---|---|---|
| GET | `/captchaImage` | 拿图形验证码 | anonymous |
| POST | `/login` | 登录 | anonymous |
| POST | `/logout` | 注销 | authenticated |
| POST | `/register` | 注册 | anonymous |
| GET | `/getInfo` | 当前用户/角色/权限 | authenticated |
| GET | `/getRouters` | 动态路由树 | authenticated |
| GET | `/system/user/list` | 用户列表 | `system:user:list` |
| POST | `/system/user` | 新增 | `system:user:add` |
| PUT | `/system/user` | 修改 | `system:user:edit` |
| DELETE | `/system/user/{ids}` | 删除 | `system:user:remove` |
| PUT | `/system/user/resetPwd` | 重置密码 | `system:user:resetPwd` |
| PUT | `/system/user/changeStatus` | 启停 | `system:user:edit` |
| GET | `/system/user/authRole/{userId}` | 已分配角色 | `system:user:list` |
| PUT | `/system/user/authRole` | 授权角色 | `system:user:edit` |
| ... | `.../role` / `.../menu` / `.../dept` / `.../post` / `.../dict*` | 同上 | 同 §8 矩阵 |
| GET | `/system/config/list` | 参数列表 | `system:config:list` |
| GET | `/system/dict/type/{dictType}` | 字典值 | authenticated |
| GET | `/system/notice/list` | 通知列表 | `system:notice:list` |
| GET | `/monitor/operlog/list` | 操作日志 | `monitor:operlog:list` |
| GET | `/monitor/logininfor/list` | 登录日志 | `monitor:logininfor:list` |
| GET | `/monitor/online/list` | 在线用户 | `monitor:online:list` |
| POST | `/monitor/online/forceLogout` | 强退 | `monitor:online:forceLogout` |
| GET | `/monitor/job/list` | 任务列表 | `monitor:job:list` |
| POST | `/monitor/job` | 新增任务 | `monitor:job:add` |
| PUT | `/monitor/job/changeStatus` | 启停 | `monitor:job:changeStatus` |
| PUT | `/monitor/job/run` | 立即执行 | `monitor:job:run` |
| GET | `/monitor/jobLog/list` | 任务日志 | `monitor:job:query` |
| GET | `/tool/gen/list` | 代码生成列表 | `tool:gen:list` |
| POST | `/tool/gen/importTable` | 导入表 | `tool:gen:import` |
| GET | `/tool/gen/download` | 下载 zip | `tool:gen:code` |
| GET | `/tool/gen/synchDb` | 同步 | `tool:gen:edit` |
| GET | `/common/download?resource=...` | 文件下载 | authenticated |
| POST | `/common/upload` | 文件上传 | authenticated |
| GET | `/swagger-ui.html` | Swagger | permitAll |
| POST | `/monitor/shutdown` | 优雅停机 | anonymous(在白名单中) |

### 5.2 内部 SPI

- `com.basic.framework.datasource.DynamicDataSource` — 多数据源
- `com.basic.framework.security.handle.MyMetaObjectHandler` — MyBatis-Plus 字段自动填充
- `com.basic.framework.web.service.SysPermissionService` / `SysRegisterService` — 权限/注册服务
- `com.basic.common.utils.poi.ExcelUtil` — Excel 导入导出
- `com.basic.generator.util.VelocityUtils` — 代码生成模板

---

## 6. 关键技术决策(HD-)

| # | 决策 | 理由 | 影响 |
|---|---|---|---|
| HD-01 | Spring Security `STATELESS`,基于 JWT | 无状态横向扩展;避免 CSRF | Token 失效前无法强制下线(已用 Redis 缓解) |
| HD-02 | 异步日志走 `AsyncManager` + 独立线程池 | 不污染主链路 | 日志丢失不会回滚主事务 |
| HD-03 | MyBatis-Plus + 自定义 XML 双轨 | 通用 CRUD 用 MP,复杂 SQL 走 XML | 维护两套 |
| HD-04 | 数据权限通过切面 + SQL 拼接 | 透明、对业务代码侵入小 | 必须确保 SQL 不要把 `where` 拼错 |
| HD-05 | 限流走 `RateLimiter` + Redis(令牌桶) | 单机也能用 Guava RateLimiter 兜底 | — |
| HD-06 | Token 30min + Redis 缓存 | 主动失效可实现强退 | Redis 不可用时降级到仅 JWT(无法强退) |
| HD-07 | 移动端使用 uni-app `up-` 组件统一注册 | 显式注册避免 H5/小程序差异 | main.js 维护一份注册表 |
| HD-08 | 移动端 `pages.json` 静态优先 | 小程序/原生端必须存在 | 动态路由留迭代 |

---

## 7. 部署视图

```
        ┌────────────────────────────┐
        │  Nginx(可选)              │
        │  /api/*  → 8080           │
        │  /        → web-ui dist   │
        └─────────────┬──────────────┘
                      │
                      ▼
        ┌────────────────────────────┐
        │  basic-admin-api jar      │
        │  端口:8080                │
        │  profile: dev / prod      │
        └──────┬──────────────┬─────┘
               │              │
               ▼              ▼
        ┌────────────┐   ┌────────────┐
        │PostgreSQL  │   │  Redis     │
        │15+(basicdb)│   │ (可选)     │
        └────────────┘   └────────────┘

        web-mp:
        ┌────────────────────────────┐
        │  HBuilder / 微信开发者工具  │
        │  编译产物:dist/dev/mp-weixin│
        │  网络:可访问 8080 后端     │
        └────────────────────────────┘
```

### 7.1 启动顺序

1. PostgreSQL 启动 + `psql -f basicdb-vue3.sql` 恢复库
2. (可选)Redis 启动
3. `mvn spring-boot:run -pl basic-admin-api` 启动后端
4. `pnpm dev` 启动 web-ui;`pnpm dev:h5` 启动 web-mp
5. 默认账号 `admin / admin123`,登录后改密

### 7.2 并发与恢复

- Tomcat `max-threads=800` 限流保护
- 优雅停机:`actuator/shutdown` 接收 POST(匿名白名单)
- 数据库连接池 Druid:`min-idle=5` / `max-active=20`(可调)
- 定时任务持久化到 `qrtz_*`,应用重启后 Quartz 自动恢复

---

## 8. 风险与待定项

| 编号 | 项 | 影响 | 归属文档 |
|---|---|---|---|
| P1 | 操作日志大表清理策略 | 长期保留 → 表膨胀 | 《详细设计》§8 审计 |
| P2 | 移动端无 refresh token | 30min 后强制重登 | 《需求文档》A-3 |
| P3 | springfox 3.0 与 SB 2.5 兼容性 | 偶发 /swagger-ui 加载失败 | 运维文档(待补) |
| P4 | `web-mp` uni-app 3 alpha 稳定性 | DCloud 官方未 GA | 风险登记(R3) |
| P5 | `com.basic` + `com.core` 双包混用 | 学习成本 | onboarding 文档(待补) |
| P6 | 国际化文案缺失 | 移动端仅中文 | 《需求文档》A-8 |
| P7 | Quartz 任务无热加载:动态新增需重启才生效(实际不用重启,Quartz 已支持) | 误判 | — |

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:含登录/锁屏/调度/代码生成/移动端 5 大主流程,接口清单 30+ |
