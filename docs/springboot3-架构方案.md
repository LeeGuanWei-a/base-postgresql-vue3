# 基础平台(base-postgresql-vue3) 架构方案 — `springboot3` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot3`** 分支(`base-postgresql-vue3`,基于 RuoYi-Vue 3.8.2 改造)
> 上游依赖:`master` 分支(RuoYi-Vue 3.8.2 / Spring Boot 2.5.14 / JDK 11 / PostgreSQL 15+)
> 本分支相对 `master` 的**唯一重大差异 = 后端 Spring Boot 2.5.14 → 3.5.16 升级(JDK 11 → 17)**;**前端两端保持 JavaScript、数据库 34 张表、业务功能全部不动**。

---

## 1. 系统定位

**一句话定位**:面向中小团队的后台管理基础平台,提供「用户/角色/菜单/部门/字典/参数/定时任务/代码生成/监控」一整套开箱即用的 RBAC 后台能力,并在 Web 管理端之外提供 uni-app 移动端(H5 / 微信小程序 / Android / iOS);**本分支完成后端从 Spring Boot 2 到 Spring Boot 3 的体系化升级**(JDK 17 + Jakarta EE + Spring Security 6 + springdoc),前端与数据层保持 `master` 原样。

| 维度 | 内容 |
|---|---|
| 使用方 | 内部业务开发者(Java 后端 + Web 前端 + 移动端开发者),最终被业务方运营人员使用 |
| 触发方式 | PC 浏览器、微信小程序、Android/iOS App,均通过 HTTP/JSON 调用后端 REST |
| 数据形态 | PostgreSQL(34 张表) + 本地文件上传 + Redis 缓存(运行期) |
| 范围边界 | RBAC 系统管理、字典/参数/通知、定时任务、代码生成、登录/锁屏、操作日志、PC + 移动两套前端(JS) |
| 不做清单(本分支) | 前端 TS 化(走 `springboot3_TypeScript`)、业务流程引擎(走 `springboot3-extend-flowable` / `springboot2-extend-flowable`)、行为验证码(走 `springboot3-tianai-captcha`)、JDK 17 → 21 与 SB 4 升级(走 `springboot4`)、多租户、SSO 联邦 |

### 1.1 核心用户动作必做项

- 登录 / 注销(账号 + 密码 + RSA 加密 + 图形验证码 kaptcha 2.3.3)
- 用户/角色/菜单/部门/岗位/字典 等基础数据维护
- 动态路由 + 按钮级权限(基于角色 + 权限字符串)
- 通知公告 + 文件上传下载
- 定时任务(Cron 表达式)在线管理
- 数据表反向生成 Controller/Service/Mapper/前端
- 锁屏 → 解锁
- PC 端 + 移动端 同一套后端接口
- **升级后的工程规范**:JDK 17 编译 + `mvn clean compile` 0 ERROR + Swagger UI 走 springdoc

---

## 2. 约束与质量属性

| 属性 | 目标 | 标注 |
|---|---|---|
| **JDK** | **严格锁定 JDK 17**(`java.version=17`,Language level 17) | **最强约束**;`master` 是 11,升级 JDK 21 走 `springboot4` |
| **Spring Boot** | **3.5.16**(`spring-boot.version`,注意属性名从 `springboot.version` 改名) | **最强约束**;不可回退 2.5.14 |
| **Jakarta EE** | 全部 `javax.servlet.*` / `javax.validation.*` / `javax.annotation.*` → **`jakarta.*`** | **最强约束**:实测 **114 处 jakarta import,0 处 `javax.servlet` 残留**;不可混用 |
| **数据库** | PostgreSQL ≥ 15.1,统一 `basicdb` schema,**34 张表 + 5 函数** | **本分支零变更**(`file-sql/basicdb-vue3.sql` 与 `master` 一致) |
| **API 文档** | **springdoc-openapi 2.9.0** 替代 springfox 3.0.0;注解 `io.swagger.v3.oas.annotations.*` | UI 路径 `/swagger-ui/index.html`,JSON 路径 `/v3/api-docs` |
| **安全框架** | **Spring Security 6.x**:`SecurityFilterChain` Bean 替代 `WebSecurityConfigurerAdapter`;`authorizeHttpRequests()` + `requestMatchers()` | `WebSecurityConfigurerAdapter` 已废弃,不可回退 |
| **持久层** | MyBatis-Plus **3.5.7**(`mybatis-plus-spring-boot3-starter`)+ MyBatis-Spring-Boot **3.0.5** + PageHelper **2.1.1** | starter 必须用 `-spring-boot3-` 变体 |
| **连接池** | Druid **1.2.28**(`druid-spring-boot-3-starter`,包名 `com.alibaba.druid.spring.boot3.autoconfigure`) | starter 必须用 `-boot-3-` 变体 |
| 鉴权 | JWT(无状态)+ BCrypt 密码散列 + kaptcha 图形验证码 | Token 默认 30 分钟 |
| 一致性 | 关系型事务 + 异步日志(`@Async` 落 `sys_oper_log` / `sys_logininfor`) | 操作日志失败不影响主链路 |
| 性能 | 列表接口走 MyBatis-Plus + PageHelper | 升级不改变运行时语义 |
| 审计 | 所有写操作通过 `LogAspect` 切面落 `sys_oper_log`,登录事件落 `sys_logininfor` | 不可关闭 |
| 安全 | XSS 过滤 + 重复提交拦截 + 限流 + CORS 显式白名单 | 过滤链实现换 jakarta,行为不变 |
| 部署 | 单体 jar(可执行 war),`tomcat.max-threads=800` | war 部署路径不变 |
| 配置 | Redis 配置从 `spring.redis.*` → **`spring.data.redis.*`** | SB 3 警告、SB 4 强制,本次已迁移 |
| 构建 | 7 个子模块 `parent.version` 统一 **3.9.2**(原 3.8.2) | 与若依 3.9.2 对齐 |

**最强约束**:① **JDK 17 + Spring Boot 3.5.16 不可回退**;② **`javax.*` 一律不得出现在新代码里**(改用 `jakarta.*`);③ **starter 必须用 `-boot-3-` 变体**(Druid / MyBatis-Plus);④ **数据库 schema 与表结构零变更**;⑤ **前端两端保持 JavaScript**(TS 化在 `springboot3_TypeScript`)。

---

## 3. 总体架构

### 3.1 容器视图(本分支改动以 **🔶** 标出)

```
┌───────────────────────────────────────────────────────────────┐
│  用户端(🔶 与 master 完全一致,JS)                              │
│  ┌──────────────────┐    ┌──────────────────────────────┐    │
│  │  web-ui (PC)     │    │  web-mp (移动:uni-app 3)     │    │
│  │  Vue 3.5.26      │    │  H5/小程序/Android/iOS        │    │
│  │  Element Plus    │    │  uview-plus 3.3.62           │    │
│  │  Vite 6.4.1 · JS │    │  Vite 5.2.8 · JS             │    │
│  └────────┬─────────┘    └──────────────┬───────────────┘    │
│           │ HTTPS / JSON                │ HTTP/JSON           │
└───────────┼─────────────────────────────┼─────────────────────┘
            ▼                             ▼
┌───────────────────────────────────────────────────────────────┐
│  🔶 Spring Boot 3.5.16 (basic-admin-api)  ·  JDK 17            │
│  ┌─────────────────────────────────────────────────────────┐  │
│  │  🔶 Filter 链(jakarta.servlet.*)                        │  │
│  │     CorsFilter → JwtAuthenticationTokenFilter → ...      │  │
│  │  🔶 SecurityFilterChain Bean(Security 6,无 WebSecurity- │  │
│  │     ConfigurerAdapter)                                  │  │
│  │  Controller (basic-admin-api / extend-*)                 │  │
│  │  ↓                                                       │  │
│  │  Service (basic-admin-core / extend-quartz / business)  │  │
│  │  ↓                                                       │  │
│  │  🔶 MyBatis-Plus 3.5.7(spring-boot3-starter)+ MyBatis   │  │
│  │     Spring Boot 3.0.5 Mapper(XML/注解双轨)              │  │
│  │  ↓                                                       │  │
│  │  PageHelper 2.1.1 + 🔶 Druid 1.2.28(boot-3-starter)     │  │
│  └────────────────────────┬────────────────────────────────┘  │
│  ┌────────────────────────┴────────────────────────────────┐  │
│  │ AOP 切面:LogAspect / DataScopeAspect / RateLimiter /    │  │
│  │          RepeatSubmit / DataSource(动态数据源)            │  │
│  │ 🔶 文档:springdoc-openapi 2.9.0(/swagger-ui, /v3/api-docs)│
│  │ 🔶 UA 解析:yauaa 8.2.0(替代 bitwalker 1.21)             │  │
│  └─────────────────────────────────────────────────────────┘  │
└──────────┬──────────────────────────────────┬──────────────────┘
           ▼                                  ▼
   ┌──────────────────┐              ┌──────────────────────┐
   │ PostgreSQL ≥15.1 │              │ Redis(可选)          │
   │ schema=basicdb   │              │ 字典/Token/限流       │
   │ 34 张表 + 5 函数 │              │ 🔶 配置键改名:        │
   │ (与 master 相同) │              │ spring.data.redis.*   │
   └──────────────────┘              └──────────────────────┘
```

### 3.2 模块清单(7 个 Maven 子模块,结构与 `master` 一致;🔶 版本升级)

| 模块 | Maven artifact | 职责 | 🔶 本分支调整 |
|---|---|---|---|
| `basic-admin-api` | `basic-admin-api` | Spring Boot 启动入口 + 全部 Controller | `parent.version` 3.8.2→**3.9.2**;`SwaggerConfig` 重写;Controller swagger 注解换 `io.swagger.v3` |
| `basic-admin-core` | `basic-admin-core` | 业务核心:用户/角色/菜单/部门/字典/参数/通知/日志 等 Service+Mapper+Domain | jakarta validation import;缓存注解微调;Mapper XML 微调 |
| `basic-framework` | `basic-framework` | 框架机制:Security、Redis、MyBatis-Plus、Swagger、Filter、AOP、Token、限流 | **改动静最大**:`SecurityConfig` 重写为 `SecurityFilterChain`;删 `javax.servlet:servlet-api`;`druid-spring-boot-3-starter`;新增 `UserAgentConfig`(yauaa) |
| `basic-common` | `basic-common` | 通用工具/注解/枚举/基类:AjaxResult/R/Excel/XSS/Log/SecurityUtils 等 | `spring-boot-starter-web`→**`-webmvc`**;hutool-bom→**`hutool-core`**;加 **`jaxb-api`**;jakarta import |
| `business-core` | `business-core` | 业务扩展点(目前是 `Demo` 示例) | jakarta import |
| `extend-quartz` | `extend-quartz` | 定时任务:Quartz 集成 + Cron 在线管理 | 单独依赖 `org.quartz-scheduler:quartz`;jakarta import |
| `extend-generator` | `extend-generator` | 代码生成:Velocity 模板 + 表结构反查 | `druid-spring-boot-3-starter`;Velocity **2.3**;jakarta import |

### 3.3 数据模型核心表(详细见《数据库设计》)

> **🔶 本分支与 `master` 完全相同**:34 张表(`gen_*` 2 + `qrtz_*` 11 + `sys_*` 21),均在 `basicdb` schema,5 个兼容函数(`date_format` / `exec` / `find_in_set` / `ifnull` / `tabledef`)。
> **升级 Spring Boot 不需要任何数据库变更** —— 这是本分支的关键结论。

| 域 | 表 | 说明 |
|---|---|---|
| 鉴权 | `sys_user` / `sys_role` / `sys_menu` / `sys_user_role` / `sys_role_menu` / `sys_role_dept` / `sys_user_post` | RBAC 主体 + 关联表 |
| 组织 | `sys_dept` / `sys_post` | 部门树 + 岗位 |
| 系统 | `sys_config` / `sys_dict_type` / `sys_dict_data` / `sys_files` / `sys_i18n` | 全局参数/字典/文件/国际化 |
| 通知 | `sys_notice` / `sys_notice_read` | 公告 + 已读 |
| 审计 | `sys_oper_log` / `sys_logininfor` | 操作/登录日志 |
| 示例 | `sys_demo` | `business-core` 示例表 |
| 定时 | `qrtz_*`(11 张) | Quartz 任务表 |
| 代码生成 | `gen_table` / `gen_table_column` | 表/字段元数据 |

### 3.4 关键流程

#### 登录流程(PC / 移动共用;🔶 内部实现换 jakarta + yauaa,时序不变)

```
[前端 JS]                 [后端 SB 3.5.16]                    [DB]
  │  GET /captchaImage      │                                  │
  │ ─────────────────────▶  │ CaptchaController(kaptcha 2.3.3) │
  │ ◀─────────────────────  │ {uuid, img, captchaEnabled}      │
  │                         │                                  │
  │  POST /login            │ SysLoginController               │
  │  {username,password,    │  ① RSA 解密                       │
  │   code,uuid}            │  ② validateCaptcha(kaptcha)      │
  │ ─────────────────────▶  │  ③ loadUserByUsername ──────────▶│
  │                         │  ④ BCrypt.matches                │
  │                         │  ⑤ TokenService.createToken      │
  │                         │  ⑥ 🔶 AsyncFactory 用 yauaa 解析  │
  │                         │     User-Agent(替代 bitwalker)   │
  │ ◀─────────────────────  │ {token}                          │
  │                         │                                  │
  │  GET /getInfo           │ 🔶 JwtAuthenticationTokenFilter   │
  │ ─────────────────────▶  │    (jakarta Filter)              │
  │  GET /getRouters        │ 动态路由树                        │
```

#### 锁屏 / 解锁(与 `master` 一致)

```
PC 端 store/modules/lock.js 维护 lock 状态
  触发:Header 操作菜单 → 锁定
  解锁:输入密码 → POST /login(同登录入口,SysLoginService 内部识别复用 token)
```

> 🔶 前端 `lock.js` 未改(仍是 JS);后端解锁逻辑未改,只是 import 换 jakarta。

#### 定时任务调度(与 `master` 一致,🔶 Quartz 依赖显式声明)

```
前端 JS → SysJobController.changeStatus / run / list
        ↓
SysJobServiceImpl → ScheduleUtils → Quartz Scheduler
        ↓
qrtz_* 表持久化 → JobInvokeUtil 反射调 @Component("ryTask").ryXxx()
```

#### 🔶 请求进入链路(jakarta + Security 6 版)

```
HTTP 请求
   │
   ▼
🔶 CorsFilter(jakarta.servlet.Filter)
   │
   ▼
🔶 XssFilter → RepeatedlyRequestWrapper(jakarta 包装)
   │
   ▼
🔶 JwtAuthenticationTokenFilter(OncePerRequestFilter,jakarta)
   │
   ▼
🔶 SecurityFilterChain(Security 6):
   │   authorizeHttpRequests(requests -> {
   │       permitAllUrl.getUrls().forEach(u -> requests.requestMatchers(u).permitAll());
   │       requests.requestMatchers("/login","/register","/captchaImage","/monitor/shutdown").permitAll()
   │               .requestMatchers(GET, "/","/*.html","/**/*.html","/**/*.css","/**/*.js","/profile/**").permitAll()
   │               .requestMatchers("/swagger-ui.html","/swagger-ui/**","/v3/api-docs/**","/druid/**").permitAll()
   │               .anyRequest().authenticated();
   │   })
   ▼
DispatcherServlet → Controller
```

### 3.5 状态机(与 `master` 一致)

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
| ADR-01 | 基础平台底座 | RuoYi-Vue 3.8.2 骨架 + **Spring Boot 3.5.16** | 留在 SB 2.5.14:Spring Security 5.5 已 EOL、无安全补丁;直接上 SB 4:JDK 21 + Jackson 3 变动更大,另开 `springboot4` | **是** — 锁死 JDK 17 + SB 3.5.16 |
| ADR-02 | 数据库 | PostgreSQL 15 + 统一 `basicdb` schema(34 表) | 继续 MySQL:失去生态对齐;**SB 3 升级本就不改 DB,无需迁移** | **是** — 不再回到 MySQL |
| ADR-03 | 数据导入方式 | `pg_dump` 单脚本(`basicdb-vue3.sql`,`COPY ... FROM stdin`) | 一张表一个文件:运维体验差 | 否 |
| ADR-04 | 鉴权方案 | JWT + **Spring Security 6** + BCrypt | 传统 session:横向扩展成本高;OAuth2:目前无需求 | 否 |
| ADR-05 | ORM | **MyBatis-Plus 3.5.7**(`mybatis-plus-spring-boot3-starter`)+ MyBatis-Spring-Boot 3.0.5 | JPA:与 RuoYi 生态偏离;留 MP 3.5.1 + SB2 starter:与 SB 3 不兼容 | 否 |
| ADR-06 | 缓存 | 可选 Redis(配置键迁 `spring.data.redis.*`) | 强制 Redis:增加本地开发成本 | 否 |
| ADR-07 | 前端双轨 | PC(`web-ui`)+ 移动(`web-mp`)并立,**均为 JS** | 只做 PC:无法覆盖移动场景;本分支顺带做 TS:与 `springboot3_TypeScript` 职责重叠 | **是** |
| ADR-08 | 移动端 UI 库 | uview-plus 3.3.62(组件名带 `up-` 前缀) | Vant:非 uni-app 原生;uni-ui:组件少 | 否 |
| ADR-09 | 移动端路由策略 | 本地 `pages.json` 注册为主(动态路由仅预留) | 直接全动态:小程序/App 端反而复杂 | 否 |
| ADR-10 | 业务包命名 | `com.basic` + `com.core`(business) 双前缀 | 全 `com.basic`:无法区分平台/业务 | 否 |
| **ADR-11** | **命名空间迁移** | **Java EE → Jakarta EE**:`javax.servlet` / `javax.validation` / `javax.annotation` 全量换 **`jakarta.*`** | 保留 javax:SB 3 内嵌 Tomcat 10+ 只认 jakarta,启动即失败;**禁止 javax/jakarta 混用** | **是** — 全仓 114 处已换,0 处 javax.servlet |
| **ADR-12** | **API 文档方案** | **springdoc-openapi 2.9.0**(`springdoc-openapi-starter-webmvc-ui`);注解 `io.swagger.v3.oas.annotations.*`;模型 `io.swagger.v3.oas.models.*` | 留 springfox 3.0.0:官方已停止维护,且与 Spring 6 不兼容;手写文档:无在线调试能力 | **是** |
| **ADR-13** | **安全框架写法** | **`SecurityFilterChain` @Bean + Lambda DSL**:`authorizeHttpRequests()` + `requestMatchers()` | 留 `WebSecurityConfigurerAdapter`:SB 3/Security 6 中已删除;`authorizeRequests()` + `antMatchers()`:已废弃 | **是** |
| **ADR-14** | **starter 命名** | Druid 用 `druid-spring-boot-3-starter`;MyBatis-Plus 用 `mybatis-plus-spring-boot3-starter` | 沿用 SB2 starter:自动装配 SPI 文件路径不同,Bean 不注册 | **是** |
| **ADR-15** | **UA 解析库** | **yauaa 8.2.0**(`nl.basjes.parse.useragent`),`UserAgentConfig` 单例 `UserAgentAnalyzer` | 留 bitwalker `UserAgentUtils` 1.21:多年未维护、不兼容新 UA 串 | 否 |
| **ADR-16** | **XML 绑定** | 显式引入 **`javax.xml.bind:jaxb-api` 2.3.1** | 不引入:JDK 17 已移除 JAXB,POI / 部分工具运行时报 `ClassNotFoundException` | **是** |
| **ADR-17** | **Jackson 版本** | 保留 **Jackson 2.x**(SB 3 默认) | 上 Jackson 3:SB 4 才默认,本分支不做 | 否 — `springboot4` 处理 |
| **ADR-18** | **前端是否同步 TS 化** | **不做**,保持在 `springboot3_TypeScript` | 顺带做:两个分支职责重叠,不利于按需取用 | **是** — 本分支只做后端 |

---

## 5. 里程碑

| 阶段 | 内容 | 验收 | 触发升级的信号 |
|---|---|---|---|
| **M0(已交付,`master`)** | RuoYi 3.8.2 基础 + SQL 迁 PostgreSQL + 锁屏解锁 | mvn compile 0 ERROR;7 模块 parent.version=3.8.2;`basicdb-vue3.sql` 可恢复 34 表 | — |
| **M1(已交付,`master`)** | 新增 web-mp(uni-app 3 + uview-plus 3) | H5/小程序/App 三端可登录 | — |
| **M2(已交付,本分支)** | **后端 SB 2.5.14 → 3.5.16 + JDK 11 → 17**:jakarta 全量迁移(114 处)、Security 6 重写、springdoc 替代 springfox、Druid/MP starter 换名、yauaa 替代 bitwalker、Redis 配置键迁移、jaxb-api 引入、Lombok 1.18.46 | JDK 17 下 `mvn clean compile` 0 ERROR;`mvn spring-boot:run` 启动成功;登录/CRUD/锁屏/定时任务/代码生成全链路回归;`/swagger-ui/index.html` 可访问;`grep javax.servlet` = 0 | — |
| **M3(下一轮)** | 移动端:动态菜单 / 国际化 / WebSocket 推送 / 锁屏 / 个人中心编辑 | 见 web-mp README §7.6 待办清单 | 业务方在移动端提出超过 5 个角色/菜单变更需求时排期 |
| **M4(可选)** | deprecation 警告清理(Security 6 / Spring 6 的 `@Deprecated` API) | 编译告警归零 | 团队对告警容忍度下降 |
| **M5(分支)** | 前端 TS 化 | 单独分支 `springboot3_TypeScript` | 需要类型保护 |
| **M6(分支)** | 行为验证码(tianai-captcha) | 单独分支 `springboot3-tianai-captcha` | 需要更强验证码 |
| **M7(分支)** | 流程引擎(Flowable) | 单独分支 `springboot3-extend-flowable` | 出现审批流/业务流程需求 |
| **M8(分支)** | SSO:接入 keycloak 联邦 | 账号互通 | 出现第二个业务系统需统一登录 |
| **M9(分支)** | **SB 4.1.1 + JDK 21 + Jackson 3 + Security 7** | 单独分支 `springboot4` / `springboot4_TypeScript` | 公司层面升级 JDK 基线 |

**本次不做,直到有信号**:微服务拆分、K8s 化、灰度发布、APM 探针、消息中心抽象、GraalVM 原生镜像。

---

## 6. 审查与遗留风险

### 6.1 已覆盖(失败设计 / 幂等 / 权限 / 一致性 / 可观测)

| 维度 | 设计 |
|---|---|
| 失败设计 | 统一 `GlobalExceptionHandler` + `R/AjaxResult` 包装;`ServiceException` / `UserPasswordNotMatchException` / `CaptchaException` 等业务异常基类 |
| 幂等 | `@RepeatSubmit` 注解 + `RepeatSubmitInterceptor`(`SameUrlDataInterceptor` 实现) |
| 权限 | 菜单级 + 按钮级(`@PreAuthorize("@ss.hasPermi('system:user:list')")`)+ 数据级(`@DataScope` + `DataScopeAspect`) |
| 一致性 | 关系型事务边界下沉到 Service;异步日志通过 `AsyncManager` 隔离失败 |
| 可观测 | `sys_oper_log` / `sys_logininfor` / 🔶 springdoc(`/swagger-ui`) / Druid 监控(`/druid`) / `actuator/shutdown` |
| 🔶 升级完整性 | jakarta import 全量(114)/ 0 处 `javax.servlet` / starter 全部换 `-boot-3-` 变体 / springfox 依赖已移除 |

### 6.2 风险清单

| # | 风险 | 影响 | 缓解 |
|---|---|---|---|
| R1 | **Spring Security 5.5 已 EOL**(本分支已升到 6,但仍有 6.x 内部旧 API) | 低 | 已升级;后续跟随 SB 4 到 Security 7 |
| R2 | ~~springfox 3.0 兼容性问题~~ **已消除**(换 springdoc 2.9.0) | — | 关闭 |
| R3 | `web-mp` 使用 `uni-app 3.0 (alpha)`,DCloud 官方尚未 GA | 中 | 锁版本 `@dcloudio/uni-app@3.0.0-4060620250520001` |
| R4 | `file-sql/` 多份历史脚本混在一起 | 低 | README 已注明:运行只用 `basicdb-vue3.sql` |
| R5 | `com.basic` + `com.core` 双包前缀,新人易混 | 低 | 文档明确:`com.basic`=平台,`com.core`=业务 |
| R6 | `springboot2-extend-flowable` 流程分支与 `master` 已分叉 | 中 | 各自维护;不强行合并 |
| R7 | 操作日志若 DB 不可用,主链路要不要回滚? | 低 | `AsyncManager` 内部 `try/catch` |
| R8 | 移动端无国际化文案 | 低 | vue-i18n 已引入,词条待补 |
| R9 | 锁屏接口复用 `/login` — 移动端未实现 | 低 | web-mp 暂只做登录,待办已挂 |
| R10 | Token 30min 过期,移动端无 refresh 机制 | 中 | 待办:`utils/request.js` 加 401 自动重登 |
| **R11** | **`javax.*` 残留会在 SB 3 运行时崩溃**(Tomcat 10+ 只认 jakarta) | **高** | 已全量迁移(实测 0 处 `javax.servlet`);**新增代码禁止 javax**,code review + `grep` 双重校验 |
| **R12** | **第三方库若隐式依赖 `javax.servlet`**,会与 jakarta 冲突 | 中 | 已识别:kaptcha 2.3.3 / poi 5.5.1 / commons-fileupload 1.5 均无 servlet 依赖;升级依赖时需复验 |
| **R13** | **`springfox` 注解残留**(`io.swagger.annotations.*`)会编译失败 | 中 | 已全量换 `io.swagger.v3.oas.annotations`;`grep io.swagger.annotations` 应为 0 |
| **R14** | **starter 用错变体**(如仍写 `mybatis-plus-boot-starter`)导致 Bean 不注册 | 高 | ADR-14;已核对 4 处(druid ×2、MP、mybatis-spring) |
| **R15** | **SB 3 默认路径匹配策略变更**(`PathPatternParser` 替代 `AntPathMatcher`),`/**` 语义差异 | 中 | `PermitAllUrlProperties` 已注入 `antPathMatcher` 并微调;上线前回归所有白名单 URL |
| **R16** | **`spring.data.redis.*` 迁移不彻底**时启动告警 | 低 | 已迁移 `host`/`port`/`database`/`password`;`application-{dev,prod}.yml` 需一并核对 |
| **R17** | **Jackson 仍是 2.x**,SB 4 时需迁 3.x(包名 `com.fasterxml.jackson` → `tools.jackson`) | 低 | 本分支不动;`springboot4` 处理 |
| **R18** | **Lombok 1.18.46 与 JDK 17 版本敏感** | 低 | 已显式锁版本;升级 JDK 21 时需同步升 Lombok |
| **R19** | **deprecation 警告未清理**(Security 6 / Spring 6 旧 API) | 低 | 列 M4;不影响功能 |
| **R20** | **`extend-generator` 的 Velocity 模板产出 SB2 风格代码**(`javax.*` import) | 中 | 代码生成器生成的 Java 代码仍需手工改 jakarta;彻底方案:模板加 SB3 变体 |

---

## 7. 参考来源

- `README.md`(仓库根,本分支)
- 同主题基线文档:`docs/base-postgresql-vue3-架构方案.md`(在 `master` 分支生成,描述 `master` 自身)— 本文件以 `master` 为基线,**只描述本分支(`springboot3`)的差异点**:后端 SB 2 → SB 3 升级
- 兄弟分支:
  - `springboot3_TypeScript`(本分支 + 前端 TS 化)
  - `springboot3-tianai-captcha`(本分支 + 行为验证码)
  - `springboot3-extend-flowable`(本分支 + Flowable)
  - `springboot4` / `springboot4_TypeScript`(SB 4 + JDK 21)
- 升级参考:Spring Boot 3.0 Migration Guide、Spring Security 6 Migration、Jakarta EE 9/10 命名空间迁移、springdoc-openapi 官方文档

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `master` 架构方案(v0.1)为基线,叠加「SB 2.5.14 → 3.5.16 / JDK 11 → 17」升级专题:§2 约束(JDK17/Jakarta/springdoc/Security6/starter 变体)、§3.1 容器视图(jakarta 链路 + springdoc)、§3.2 模块逐项升级说明、§3.4 新增请求链路图、ADR-11~ADR-18 共 8 条升级决策、M2 升级里程碑、R11~R20 共 10 条升级风险 |
