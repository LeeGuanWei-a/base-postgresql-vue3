# 基础平台(base-postgresql-vue3) 架构方案

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 `master` 分支(`base-postgresql-vue3`,基于 RuoYi-Vue 3.8.2 改造)
> 上游依赖:RuoYi-Vue 3.8.2 / Spring Boot 2.5.14 / PostgreSQL 15+

---

## 1. 系统定位

**一句话定位**:面向中小团队的后台管理基础平台,提供「用户/角色/菜单/部门/字典/参数/定时任务/代码生成/监控」一整套开箱即用的 RBAC 后台能力,并在 Web 管理端之外,新增一套 uni-app 移动端,可同时发布为 H5 / 微信小程序 / Android / iOS。

| 维度 | 内容 |
|---|---|
| 使用方 | 内部业务开发者(Java 后端 + Web 前端 + 移动端开发者),最终被业务方运营人员使用 |
| 触发方式 | PC 浏览器、微信小程序、Android/iOS App,均通过 HTTP/JSON 调用后端 REST |
| 数据形态 | PostgreSQL(34 张表) + 本地文件上传 + Redis 缓存(运行期) |
| 范围边界 | RBAC 系统管理、字典/参数/通知、定时任务、代码生成、登录/锁屏、操作日志、PC + 移动两套前端 |
| 不做清单(本分支) | 业务流程引擎(走 `master-extend-flowable` 分支)、多租户(走后续版本)、SSO 联邦(走 keycloak 集成项目)、Spring Boot 2 → 3/4 升级(走 `springboot3` / `springboot4` 分支) |

### 1.1 核心用户动作必做项

- 登录 / 注销(账号+密码+RSA 加密+图形验证码)
- 用户/角色/菜单/部门/岗位/字典 等基础数据维护
- 动态路由 + 按钮级权限(基于角色 + 权限字符串)
- 通知公告 + 文件上传下载
- 定时任务(Cron 表达式)在线管理
- 数据表反向生成 Controller/Service/Mapper/前端
- 锁屏 → 解锁
- PC 端 + 移动端 同一套后端接口

---

## 2. 约束与质量属性

| 属性 | 目标 | 标注 |
|---|---|---|
| **JDK / Spring Boot** | 严格锁定 **JDK 11 + Spring Boot 2.5.14** | **最强约束**,与 RuoYi 3.8.2 对齐;升级 JDK 17/SB3 走独立分支 |
| 数据库 | PostgreSQL ≥ 15.1,所有对象落在 `basicdb` schema | 不能回退到 MySQL;JDBC URL 必须带 `currentSchema=basicdb` |
| 鉴权 | JWT(无状态)+ BCrypt 密码散列 + 图形验证码 | Token 默认 30 分钟(`token.expireTime=30`) |
| 一致性 | 关系型事务 + 异步日志(`@Async` 落 `sys_oper_log` / `sys_logininfor`) | 操作日志失败不影响主链路 |
| 性能 | 列表接口走 MyBatis-Plus + PageHelper,前端 PageHelper 自动识别方言 | 监控项 Redis 缓存可降低热点字典 DB 压力 |
| 审计 | 所有写操作通过 `LogAspect` 切面落 `sys_oper_log`,登录事件落 `sys_logininfor` | 不可关闭 |
| 安全 | XSS 过滤(`XssFilter` + `XssHttpServletRequestWrapper`)+ 重复提交拦截(`RepeatSubmitInterceptor`)+ 限流(`RateLimiterAspect`)+ CORS 显式白名单 | |
| 部署 | 单体 jar(可执行 war),`tomcat.max-threads=800` | 暂不上 K8s;与 RuoYi 原始部署方式一致 |
| 成本 | 单节点 PostgreSQL + 单实例 Redis + 单 jar,最低 2C4G 可起 | |

**最强约束**:① **JDK 11 + Spring Boot 2.5.14 不升级**;② **数据库全量统一在 `basicdb` schema**;③ **后端接口与 RuoYi 3.8.2 保持兼容**,Web 与移动端只复用既有接口,不引入新后端服务。

---

## 3. 总体架构

### 3.1 容器视图

```
┌───────────────────────────────────────────────────────────────┐
│  用户端                                                        │
│  ┌──────────────────┐    ┌──────────────────────────────┐    │
│  │  web-ui (PC)     │    │  web-mp (移动:uni-app 3)     │    │
│  │  Vue3 + Element  │    │  H5/小程序/Android/iOS        │    │
│  │  Plus + Vite 6   │    │  uview-plus 3 + Pinia         │    │
│  └────────┬─────────┘    └──────────────┬───────────────┘    │
│           │ HTTPS / JSON                │ HTTP/JSON           │
└───────────┼─────────────────────────────┼─────────────────────┘
            ▼                             ▼
┌───────────────────────────────────────────────────────────────┐
│  Spring Boot 2.5.14 (basic-admin-api)                          │
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
   └──────────────────┘              └──────────────────┘
```

### 3.2 模块清单

| 模块 | Maven artifact | 职责 | 关键要点 |
|---|---|---|---|
| `basic-admin-api` | `basic-admin-api` | Spring Boot 启动入口 + 全部 Controller | `BasicApplication` 扫描 `com.basic` + `com.core`,排除 `DataSourceAutoConfiguration`(由 `DruidConfig` 接管) |
| `basic-admin-core` | `basic-admin-core` | 业务核心:用户/角色/菜单/部门/字典/参数/通知/日志 等的 Service+Mapper+Domain | 21 张 `sys_*` 表的全部 CRUD |
| `basic-framework` | `basic-framework` | 框架机制:Spring Security、Redis、MyBatis-Plus、Swagger、Filter、AOP、Token、限流 | 提供 starter 风格的能力,被 `basic-admin-api` 装配 |
| `basic-common` | `basic-common` | 通用工具/注解/枚举/基类:AjaxResult/R/Excel/XSS/Log/SecurityUtils 等 | 几乎所有模块都依赖 |
| `business-core` | `business-core` | 业务扩展点(目前是 `Demo` 示例) | 包前缀 `com.core`,与 `com.basic` 并列;MapperScan 在 `BasicApplication` 中显式声明 |
| `extend-quartz` | `extend-quartz` | 定时任务:Quartz 集成 + Cron 在线管理 | 11 张 `qrtz_*` 表 |
| `extend-generator` | `extend-generator` | 代码生成:Velocity 模板 + 表结构反查 | 2 张 `gen_*` 表 |

### 3.3 数据模型核心表(详细见《数据库设计》)

| 域 | 表 | 说明 |
|---|---|---|
| 鉴权 | `sys_user` / `sys_role` / `sys_menu` / `sys_user_role` / `sys_role_menu` / `sys_role_dept` / `sys_user_post` | RBAC 主体 + 关联表 |
| 组织 | `sys_dept` / `sys_post` | 部门树 + 岗位 |
| 系统 | `sys_config` / `sys_dict_type` / `sys_dict_data` / `sys_files` / `sys_i18n` | 全局参数/字典/文件/国际化 |
| 通知 | `sys_notice` / `sys_notice_read` | 公告 + 已读(后者由 `create-sys-notice-read.sql` 补建) |
| 审计 | `sys_oper_log` / `sys_logininfor` | 操作/登录日志 |
| 示例 | `sys_demo` | `business-core` 示例表 |
| 定时 | `qrtz_*` (11 张) | Quartz 任务表 |
| 代码生成 | `gen_table` / `gen_table_column` | 表/字段元数据 |

### 3.4 关键流程

#### 登录流程(PC / 移动共用)

```
[前端]                    [后端]                          [DB]
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

> PC 与移动端**完全复用**这套接口。差异只在:① 移动端把 `Basic-Token` 存到 `uni.storage`;② PC 端除 `/login` 外还接前端 `jsencrypt` 走 RSA。

#### 锁屏 / 解锁

```
PC 端 store/modules/lock.js 维护 lock 状态
  触发:Header 操作菜单 → 锁定
  解锁:输入密码 → POST /login(同登录入口,SysLoginService 内部识别 reuse-token)
```

> 锁屏不是独立接口,直接复用 `SysLoginService.login(...)`;解锁时若已有 token 则只校验密码,失败抛 `UserPasswordNotMatchException`。

#### 定时任务调度

```
前端 → SysJobController.changeStatus / run / list
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

---

## 4. 关键决策 ADR

| # | 决策点 | 采纳方案 | 备选 + 放弃原因 | 是否演进锁死 |
|---|---|---|---|---|
| ADR-01 | 基础平台底座 | RuoYi-Vue 3.8.2(零升级) | 自研框架:成本/成熟度不划算;直接升 SB 3.5:JDK 17 + Jakarta 改名,影响面大,放分支迭代 | **是** — master 锁死 SB 2.5.14 |
| ADR-02 | 数据库 | PostgreSQL 15 + 统一 `basicdb` schema | 继续 MySQL:失去与其它项目(PostgreSQL)生态对齐;分 schema(按模块):跨表 JOIN 麻烦 | **是** — 不再回到 MySQL |
| ADR-03 | 数据导入方式 | `pg_dump` 单脚本(`basicdb-vue3.sql`,`COPY ... FROM stdin`) | 一张表一个文件:运维和开发体验都差;`INSERT` 模式脚本:文件 10× 大 | 否 — 备份脚本格式可演进 |
| ADR-04 | 鉴权方案 | JWT + Spring Security + BCrypt | 传统 session:横向扩展成本高;OAuth2:目前无第三方登录需求 | 否 — 后续 keycloak 集成可在网关层引入 |
| ADR-05 | ORM | MyBatis-Plus 3.5.1(注解 + XML 双轨) | JPA:与 RuoYi 生态偏离;纯 MyBatis:CRUD 模板代码多 | 否 |
| ADR-06 | 缓存 | 可选 Redis(默认不开时降级到本地) | 强制 Redis:增加本地开发成本;完全无缓存:列表翻页慢 | 否 |
| ADR-07 | 前端双轨 | PC(`web-ui`,Vue3+ElementPlus)与移动(`web-mp`,uni-app3+uview-plus)并立 | 只做 PC:无法覆盖移动场景;只做移动:运营桌面效率低 | **是** — master 同时维护两份 |
| ADR-08 | 移动端 UI 库 | uview-plus 3.3.62(组件名带 `up-` 前缀) | Vant:Vue3 支持但非 uni-app 原生;uni-ui:组件少,业务写死 | 否 |
| ADR-09 | 移动端路由策略 | 本地 `pages.json` 注册为主(动态路由仅预留) | 直接全动态:小程序 / App 端 pages.json 必须存在,反而复杂 | 否 |
| ADR-10 | 业务包命名 | `com.basic` + `com.core`(business) 双前缀 | 全 `com.basic`:无法清晰区分平台/业务;全 `com.core`:与平台包混 | 否 |

---

## 5. 里程碑

| 阶段 | 内容 | 验收 | 触发升级的信号 |
|---|---|---|---|
| **M0(已交付,master)** | RuoYi 3.8.2 基础 + SQL 迁 PostgreSQL + 锁屏解锁 | mvn compile 0 ERROR;7 模块 parent.version=3.8.2;`basicdb-vue3.sql` 可恢复 34 表 | — |
| **M1(已交付,master)** | 新增 web-mp(uni-app 3 + uview-plus 3) | H5/小程序/App 三端可登录;复用 `/login` `/getInfo` `/getRouters` | — |
| **M2(下一轮)** | 移动端:动态菜单 / 国际化 / WebSocket 推送 / 锁屏(手势/PIN) / 个人中心编辑 | 见 web-mp README §7.6 待办清单 | 业务方在移动端提出超过 5 个角色/菜单变更需求时即应排期 |
| **M3(可选)** | SSO:接入 keycloak 联邦 | 账号互通,组织/角色从 keycloak 拉 | 出现第二个业务系统需要统一登录 |
| **M4(分支)** | Spring Boot 3.5 + JDK 17 + Jakarta | 单独分支 `springboot3` | 公司层面升级 JDK 基线 |
| **M5(分支)** | 流程引擎(Flowable 6.8) | 单独分支 `master-extend-flowable` | 出现审批流/业务流程需求 |

**本次不做,直到有信号**:微服务拆分、K8s 化、灰度发布、APM 探针、消息中心抽象。

---

## 6. 审查与遗留风险

### 6.1 已覆盖(失败设计 / 幂等 / 权限 / 一致性 / 可观测)

| 维度 | 设计 |
|---|---|
| 失败设计 | 统一 `GlobalExceptionHandler` + `R/AjaxResult` 包装;`ServiceException` / `UserPasswordNotMatchException` / `CaptchaException` 等业务异常基类 |
| 幂等 | `@RepeatSubmit` 注解 + `RepeatSubmitInterceptor`(`SameUrlDataInterceptor` 实现) |
| 权限 | 菜单级 + 按钮级(`@PreAuthorize("@ss.hasPermi('system:user:list')")`) + 数据级(`@DataScope` + `DataScopeAspect`) |
| 一致性 | 关系型事务边界下沉到 Service;异步日志通过 `AsyncManager` 隔离失败 |
| 可观测 | `sys_oper_log` / `sys_logininfor` / Swagger(springfox 3.0) / Druid 监控 / `actuator/shutdown` |

### 6.2 风险清单

| # | 风险 | 影响 | 缓解 |
|---|---|---|---|
| R1 | Spring Security 5.5 已 EOL,无安全补丁 | 高 | 走 `springboot3` 分支,网关层补 WAF;**master 锁死版本不动** |
| R2 | springfox 3.0 与 spring 5.3 兼容性问题 | 中 | 已在 `application.yml` 关闭 / 视情况切 springdoc |
| R3 | `web-mp` 使用 `uni-app 3.0 (alpha)`,DCloud 官方尚未 GA | 中 | 锁版本 `@dcloudio/uni-app@3.0.0-4060620250520001`;`pdm-lock` 不引入更多 alpha |
| R4 | `file-sql/` 多份历史脚本混在一起 | 低 | README 已注明:运行只用 `basicdb-vue3.sql`;历史脚本列入归档 |
| R5 | `com.basic` + `com.core` 双包前缀,新人易混 | 低 | 文档明确:`com.basic`=平台,`com.core`=业务 |
| R6 | `master-extend-flowable` 流程分支与 master 已分叉 | 中 | 各自维护;不强行合并 |
| R7 | 操作日志若 DB 不可用,主链路要不要回滚? | 低 | `AsyncManager` 内部 `try/catch`,不污染主链路 |
| R8 | 移动端无国际化文案 | 低 | vue-i18n 已引入,词条待补 |
| R9 | 锁屏接口复用 `/login` — 移动端未实现 | 低 | web-mp 暂只做登录,锁屏待办已挂 |
| R10 | Token 30min 过期,移动端无 refresh 机制 | 中 | 待办:`utils/request.js` 加 401 自动重登 |

---

## 7. 参考来源

- `docs/README.md`(`base-postgresql-vue3` 根目录)
- `pom.xml` / `file-sql/basicdb-vue3.sql`
- Spring Boot 2.5 / Spring Security 5.5 / MyBatis-Plus 3.5 官方文档
- 模板/案例参考:awesome-architecture 教程、模板与案例(详见 `arch_read_offline`)

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿,基于 README + 模块扫描生成 |
