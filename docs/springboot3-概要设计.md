# 基础平台(base-postgresql-vue3) 概要设计(HLD) — `springboot3` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot3`** 分支
> 前置依赖:《架构方案》v0.1、《需求文档》v0.1
> 本分支相对 `master` 的差异仅在**后端技术栈升级**(JDK 17 + SB 3.5.16 + Jakarta EE + Security 6 + springdoc);业务逻辑架构、核心流程、数据设计、接口清单、部署拓扑与 `master` **一致**,本文档不重复,只新增升级专题章节(§2.4 / §3.6 / §6 HD-09~HD-16 / §8 P8~P15)。
> 本分支改动以 **🔶** 标出。

---

## 1. 设计目标与约束回顾

| 来源 | 关键约束 |
|---|---|
| 架构方案 §2 | **JDK 17 + Spring Boot 3.5.16 锁死**;Jakarta EE(`javax.*` → `jakarta.*`);springdoc 替代 springfox;Security 6 `SecurityFilterChain`;starter 换 `-boot-3-` 变体 |
| 架构方案 §2 | 数据库 PostgreSQL ≥ 15,统一 `basicdb` schema(**34 表零变更**);前端两端保持 JS |
| 需求文档 §7 | 列表 1000 条 < 1s;登录 < 500ms;单一可执行 jar;**JDK 17 下 0 ERROR 可编译、可启动** |
| 需求文档 §10 | AC-01~AC-24(其中 AC-14~AC-24 为升级专项) |

**勿重复大段正文**,仅作引子。

---

## 2. 逻辑架构与模块划分

### 2.1 工程结构(结构与 `master` 一致;🔶 版本/依赖变化)

```
base-postgresql-vue3/
├── pom.xml                 # 父 pom,7 子模块;🔶 java.version=17 / spring-boot.version=3.5.16
├── basic-admin-api/        # 启动入口 + 全部 Controller;🔶 SwaggerConfig 重写
├── basic-admin-core/       # 业务核心(sys_*/gen_*/qrtz_*/demo);🔶 jakarta import
├── basic-framework/        # 框架能力;🔶 改动静最大(Security 6 / Filter jakarta / druid-3-starter / UserAgentConfig)
├── basic-common/           # 工具/注解/枚举/基类;🔶 -webmvc / hutool-core / jaxb-api / jakarta
├── business-core/          # 业务扩展点(com.core 包);🔶 jakarta import
├── extend-quartz/          # Quartz 集成;🔶 quartz 单独依赖 + jakarta
├── extend-generator/       # Velocity 代码生成;🔶 druid-3-starter + Velocity 2.3 + jakarta
├── file-sql/               # 8 个 SQL(basicdb-vue3.sql 为唯一运行入口)🔶 零变更
├── file-bin/  file-doc/    # 静态资源
├── web-ui/                 # PC(Vue3 + ElementPlus,JS)🔶 零改动
└── web-mp/                 # 移动(uni-app3 + uview-plus3,JS)🔶 零改动
```

### 2.2 组件职责(🔶 标出升级后结构变化)

| 模块 | 关键类 | 职责 | 🔶 升级变化 |
|---|---|---|---|
| `basic-admin-api` | `BasicApplication` | 启动类,扫描 `com.basic` + `com.core`;`@MapperScan("com.core.**.mapper")` | 无结构变化 |
| | `BasicServletInitializer` | 外部 Tomcat war 启动适配 | `javax.servlet` → `jakarta.servlet` |
| | `com.basic.web.config.SwaggerConfig` | API 文档配置 | **完全重写**:`Docket`(springfox)→ `@Bean OpenAPI`(springdoc) |
| | `com.basic.web.controller.**` | 26 个 Controller | swagger 注解换 `io.swagger.v3.oas.annotations` + jakarta |
| `basic-admin-core` | `com.basic.system.service.impl.**` | 业务 Service 实现 | jakarta validation import;缓存注解微调 |
| | `com.basic.system.mapper.**` | MyBatis-Plus Mapper 接口 | MP 3.5.7 兼容(无破坏性变化) |
| | `com.basic.system.domain.**` | 实体 + VO | `javax.validation.*` → `jakarta.validation.*` |
| | `com.basic.system.mapper.*.xml` | 自定义 SQL 片段 | 锁屏解锁关联微调(与 `master` 同源) |
| `basic-framework` | `SecurityConfig` | Spring Security 配置 | **结构重写**:`extends WebSecurityConfigurerAdapter` → `@Bean SecurityFilterChain filterChain(HttpSecurity)`;`authorizeRequests`→`authorizeHttpRequests`;`antMatchers`→`requestMatchers` |
| | `JwtAuthenticationTokenFilter` | 解析 token → SecurityContext | `OncePerRequestFilter`(jakarta);逻辑不变 |
| | `AuthenticationEntryPointImpl` / `LogoutSuccessHandlerImpl` | 鉴权失败 / 注销成功 | jakarta |
| | `MybatisPlusConfig` / `MyBatisConfig` | MyBatis-Plus 分页、Mapper 扫描 | 无变化 |
| | `DataScopeAspect` | 数据权限 SQL 注入 | 无变化 |
| | `LogAspect` | 操作日志切面 | jakarta |
| | `RateLimiterAspect` | 限流切面 | 无变化 |
| | `RepeatSubmitInterceptor` | 重复提交拦截 | jakarta |
| | `RedisConfig` / `DruidConfig` / `JacksonConfig` | 配置 | `DruidConfig` 包名 → `com.alibaba.druid.spring.boot3.autoconfigure` |
| | `ResourcesConfig` | 静态资源映射 | `WebMvcConfigurerAdapter` → **`WebMvcConfigurer`**;新增 springdoc 静态资源 |
| | **`UserAgentConfig`** | **新增**:yauaa `UserAgentAnalyzer` 单例 Bean | 全新(替代 bitwalker) |
| | `SysLoginService` / `TokenService` | 登录 + Token | jakarta + yauaa API 调用 |
| `basic-common` | `R` / `AjaxResult` / `TableDataInfo` | 统一返回结构 | 无变化 |
| | `BaseEntity` / `TreeEntity` | 实体基类 | 无变化 |
| | `SecurityUtils` / `StringUtils` / `DateUtils` | 通用工具 | `ServletUtils` / `IpUtils` jakarta |
| | `@Log` / `@RepeatSubmit` / `@RateLimiter` / `@DataScope` / `@Anonymous` | 业务注解 | 无变化 |
| | `ExcelUtil` / `poi` | Excel 导入导出 | poi 4.1.2 → **5.5.1**(API 微调) |
| | `Xss` / `XssValidator` | XSS 校验 | jakarta |
| `extend-quartz` | `ScheduleConfig` / `ScheduleUtils` / `AbstractQuartzJob` | Quartz 集成 | quartz 单独依赖 + jakarta |
| `extend-generator` | `GenController` / `VelocityUtils` | 代码生成 | Velocity 2.3 + druid-3-starter + jakarta;⚠️ 模板未产 SB3 代码(R20) |
| `business-core` | `Demo` / `DemoController` | 业务示例 | jakarta |

### 2.3 扩展 SPI(与 `master` 一致)

| SPI | 接入方式 | 用例 |
|---|---|---|
| 自定义 Job | 实现 `@Component("ryTask")` + `public void ryXxx(...)` | `ryTask.ryNoParams` / `ryParams` |
| 自定义权限注解 | 沿用 `@PreAuthorize("@ss.hasPermi('...')")` | `SysPermissionService.hasPermi` |
| 字典加载 | 在 `DictUtils` 增枚举/缓存 key | — |
| 数据源 | 注解 `@DataSource(DataSourceType.SLAVE)` | 多数据源 |
| 代码生成模板 | 新增 `vm/java/...` 模板 | — |
| **🔶 自定义 OpenAPI 分组** | 在 `SwaggerConfig` 增 `GroupedOpenApi` Bean | 接口分组展示 |
| **🔶 自定义 UA 规则** | 在 `UserAgentConfig` 调整 `UserAgentAnalyzer` 配置 | UA 解析粒度 |

### 2.4 🔶 升级后的关键结构对照(本分支核心)

#### 2.4.1 命名空间:`javax.*` → `jakarta.*`

| 旧(`master`,SB 2) | 新(本分支,SB 3) | 影响面 |
|---|---|---|
| `javax.servlet.http.HttpServletRequest` | `jakarta.servlet.http.HttpServletRequest` | Filter / Interceptor / 工具类 |
| `javax.servlet.http.HttpServletResponse` | `jakarta.servlet.http.HttpServletResponse` | 同上 |
| `javax.servlet.http.HttpSession` | `jakarta.servlet.http.HttpSession` | 同上 |
| `javax.servlet.Filter` / `FilterChain` | `jakarta.servlet.Filter` / `FilterChain` | `XssFilter` / `RepeatableFilter` / `JwtAuthenticationTokenFilter` |
| `javax.servlet.ServletException` | `jakarta.servlet.ServletException` | 全局异常 / 工具类 |
| `javax.validation.constraints.*`(`@NotBlank` / `@Size` / `@Pattern` / `@Email`) | `jakarta.validation.constraints.*` | 所有 `Sys*` 实体 / `LoginBody` / 各种 BO |
| `javax.validation.Valid` / `Validator` | `jakarta.validation.Valid` / `Validator` | Controller 入参 / `BeanValidators` |
| `javax.annotation.Resource` / `@PostConstruct` | `jakarta.annotation.Resource` / `@PostConstruct` | 各处 Bean |
| `javax.xml.bind.*`(JAXB) | **保持 `javax.xml.bind`(引入 `jaxb-api` 2.3.1)** | **例外,不迁移** |

**实测口径**:114 处 `jakarta.*` import,**0** 处 `import javax.servlet`。

#### 2.4.2 Spring Security 5 → 6 配置结构

```
【master / Security 5】
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {          ← 已删除
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()
            .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
            .sessionManagement().sessionCreationPolicy(STATELESS).and()
            .authorizeRequests()                                            ← 已废弃
            .antMatchers("/login", ...).anonymous()                         ← 已废弃
            .anyRequest().authenticated()
            .and().headers().frameOptions().disable();
    }
}

【本分支 / Security 6】
@Configuration
public class SecurityConfig {
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {   ← Bean
        httpSecurity
            .csrf(csrf -> csrf.disable())                                   ← Lambda DSL
            .exceptionHandling(e -> e.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(requests -> {                            ← 新入口
                permitAllUrl.getUrls().forEach(u -> requests.requestMatchers(u).permitAll());
                requests.requestMatchers("/login","/register","/captchaImage","/monitor/shutdown").permitAll()
                        .requestMatchers(HttpMethod.GET, "/","/*.html","/**/*.html","/**/*.css","/**/*.js","/profile/**").permitAll()
                        .requestMatchers("/swagger-ui.html","/swagger-ui/**","/v3/api-docs/**","/druid/**").permitAll()
                        .anyRequest().authenticated();
            })
            .headers(h -> h.frameOptions(f -> f.disable()));
        return httpSecurity.build();
    }
}
```

**白名单变化对照**:

| 用途 | `master`(springfox) | 本分支(springdoc) |
|---|---|---|
| Swagger UI | `/swagger-ui.html` | `/swagger-ui.html` + **`/swagger-ui/**`** |
| OpenAPI JSON | `/swagger-resources/**` | **`/v3/api-docs/**`** |
| WebJars | `/webjars/**` | 不再需要 |
| Druid 监控 | (未显式白名单) | **`/druid/**`** |

#### 2.4.3 API 文档:springfox → springdoc

| 维度 | `master`(springfox 3.0.0) | 本分支(springdoc 2.9.0) |
|---|---|---|
| 依赖 | `springfox-boot-starter` + `swagger-models` | `springdoc-openapi-starter-webmvc-ui` |
| 配置入口 | `Docket` Bean | `@Bean OpenAPI customOpenAPI()` |
| 模型 API | `springfox.documentation.*` | `io.swagger.v3.oas.models.*` |
| 接口注解 | `@Api` / `@ApiOperation` / `@ApiImplicitParam` | **`@Tag` / `@Operation` / `@Parameter`** |
| 字段注解 | `@ApiModelProperty` | **`@Schema`** |
| UI 路径 | `/swagger-ui.html` | **`/swagger-ui/index.html`** |
| JSON 路径 | `/v2/api-docs` | **`/v3/api-docs`** |
| 配置文件 | `application.yml` 的 `swagger:` 节点 | **`springdoc:` 节点** |
| 分组 | `Docket.groupName(...)` | `GroupedOpenApi` Bean |

#### 2.4.4 依赖矩阵(starter 变体对照)

| 能力 | `master`(SB 2) | 本分支(SB 3) | 若用错的后果 |
|---|---|---|---|
| 连接池 | `druid-spring-boot-starter` 1.2.8 | **`druid-spring-boot-3-starter` 1.2.28** | 数据源 Bean 不注册 → 启动失败 |
| ORM | `mybatis-plus-boot-starter` 3.5.1 | **`mybatis-plus-spring-boot3-starter` 3.5.7** | Mapper 不扫描 → `NoSuchBeanDefinition` |
| MyBatis 基础 | (注释) | **`mybatis-spring-boot-starter` 3.0.5** | MP 缺底层 |
| Web | `spring-boot-starter-web` | **`spring-boot-starter-webmvc`** | 若依 3.9.2 原版即用 webmvc |
| 分页 | `pagehelper-spring-boot-starter` 1.4.1 | **2.1.1** | 分页失效 |
| UA | `bitwalker UserAgentUtils` 1.21 | **`yauaa` 8.2.0** | — |
| JAXB | (JDK 11 自带) | **`javax.xml.bind:jaxb-api` 2.3.1** | POI/工具类 `ClassNotFoundException` |
| Lombok | 未显式声明 | **1.18.46**(显式) | JDK 17 下注解处理异常 |

#### 2.4.5 请求链路对照(前后)

```
【master】
HTTP → CorsFilter(javax) → XssFilter(javax) → JwtAuthenticationTokenFilter(javax)
     → Security 5 过滤链(WebSecurityConfigurerAdapter)
     → DispatcherServlet → Controller

【本分支】
HTTP → CorsFilter(jakarta) → XssFilter(jakarta) → JwtAuthenticationTokenFilter(jakarta)
     → Security 6 过滤链(SecurityFilterChain Bean,authorizeHttpRequests + requestMatchers)
     → DispatcherServlet → Controller
```

> **行为等价**:过滤顺序与放行规则一致,只是命名空间与配置写法变化。

---

## 3. 核心流程设计

### 3.1 登录流程(与 `master` 一致;🔶 内部实现换 jakarta/yauaa)

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
   │ ────────────────▶  CaptchaController(kaptcha 2.3.3)   │
   │ ◀────────────────  {uuid,img}   │                      │
   │                                 │                      │
   │  POST /login(username,pwd,code,uuid)                  │
   │ ────────────────▶  SysLoginController                 │
   │                       │  login(LoginBody)              │
   │                       │ ────────▶  ① RSA 解密
   │                       │         │  ② validateCaptcha
   │                       │         │  ③ loadUserByUsername
   │                       │         │  ④ BCrypt.matches
   │                       │         │  ⑤ TokenService.createToken
   │                       │         │  ⑥ 🔶 AsyncFactory + yauaa
   │                       │         │     解析 UA(browser/os)
   │ ◀──────────────── {token}        │  写 sys_logininfor  │
```

### 3.2 锁屏/解锁(与 `master` 一致)

```
PC store/lock.js 触发 lock 状态 → 锁屏页
   │
   ▼ 提交 unlockPassword
   │  POST /login(reuse token, only password)
   ▼
SysLoginService.login: 短路验证码检查;校验密码;
   成功 → 返回原 token;失败 → UserPasswordNotMatchException
```

> 🔶 前端 `lock.js` 未改;后端仅 import 换 jakarta。

### 3.3 定时任务调度(与 `master` 一致;🔶 quartz 单独依赖)

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

### 3.4 代码生成(与 `master` 一致;⚠️ 模板未适配 SB3)

```
导入表 → GenTableServiceImpl.importGenTable
  → GenTableColumnServiceImpl.importGenTableCol
  → 写 gen_table / gen_table_column

生成:
  download(genTableIds) →
    VelocityEngine(**2.3**) 渲染 .vm 模板(java/Mapper/Service/Controller/Vue/sql/js)
    → 打包 zip → 浏览器下载
```

> ⚠️ **🔶 R20**:模板产出的是 **SB 2 风格代码**(`javax.*` import + springfox 注解),生成后需手工改 `jakarta.*` 与 `io.swagger.v3`。见 A-9。

### 3.5 移动端登录(与 `master` 一致,前端零改动)

```
web-mp/pages/login/index.vue(JS)
  GET /captchaImage        (utils/request.js)
  POST /login              (RSA + 验证码)
  ↓  Basic-Token 存到 uni.storage
GET /getInfo                (用户/角色/权限)
GET /getRouters             (本分支先消费即可,后续转 pages.json)
```

### 3.6 🔶 升级改造流程(本分支新增,已执行完毕,记录备查)

#### 3.6.1 推荐执行顺序(依赖先行,代码后随)

```
步骤 1:父 pom.xml
        - java.version 11 → 17
        - springboot.version → spring-boot.version,值 2.5.14 → 3.5.16
        - 移除 springfox / bitwalker / fastjson / hutool-bom
        - 新增 springdoc / hutool-core / yauaa / jaxb-api / lombok
        - starter 换名:druid-spring-boot-3-starter / mybatis-plus-spring-boot3-starter
        - 启用 mybatis-spring-boot-starter 3.0.5
        ↓
步骤 2:7 个子模块 pom.xml
        - parent.version 3.8.2 → 3.9.2
        - basic-common:spring-boot-starter-web → -webmvc;hutool-core;+jaxb-api
        - basic-framework:删 javax.servlet:servlet-api;druid-spring-boot-3-starter
        - extend-quartz:quartz 单独依赖
        - extend-generator:druid-spring-boot-3-starter
        ↓
步骤 3:mvn dependency:resolve 验证依赖可解析(不含 javax.servlet 传递依赖)
        ↓
步骤 4:批量 import 替换
        - javax.servlet.*      → jakarta.servlet.*
        - javax.validation.*   → jakarta.validation.*
        - javax.annotation.*   → jakarta.annotation.*
        - (例外:javax.xml.bind.* 保持)
        ↓
步骤 5:SecurityConfig 重写(WebSecurityConfigurerAdapter → SecurityFilterChain Bean)
        ↓
步骤 6:SwaggerConfig 重写(Docket → OpenAPI Bean)+ 注解批量替换
        - @Api → @Tag
        - @ApiOperation → @Operation
        - @ApiModelProperty → @Schema
        - io.swagger.annotations.* → io.swagger.v3.oas.annotations.*
        ↓
步骤 7:新增 UserAgentConfig(yauaa),改 AsyncFactory / SysLoginService 的 UA 调用
        ↓
步骤 8:配置文件迁移
        - application.yml:删 swagger:,加 springdoc:,删 xxl.job:
        - application-{dev,prod}.yml:spring.redis.* → spring.data.redis.*
        - application-druid.yml:statViewServlet.url-pattern
        ↓
步骤 9:mvn clean compile(JDK 17)→ 0 ERROR
        ↓
步骤 10:启动 + 全链路回归(S7 场景)
```

#### 3.6.2 升级验证链(每步的检查点)

```
① 编译通过 ────────────▶ grep javax.servlet = 0
                          grep io.swagger.annotations = 0
② 依赖正确 ────────────▶ mvn dependency:tree | grep javax.servlet = 空
                          grep "boot-3-starter\|spring-boot3-starter" = 命中
③ 启动成功 ────────────▶ 日志无 NoClassDefFoundError
                          日志无 "Failed to configure a DataSource"
④ 安全链正常 ──────────▶ 登录需鉴权 / 白名单可匿名访问
⑤ 文档可用 ────────────▶ /swagger-ui/index.html 200
                          /v3/api-docs 返回合法 JSON
⑥ 业务回归 ────────────▶ 登录/CRUD/锁屏/定时任务/代码生成 全通过
```

#### 3.6.3 常见踩坑与处置(实测)

| 症状 | 根因 | 处置 |
|---|---|---|
| `NoClassDefFoundError: javax/servlet/Filter` | jakarta 迁移不彻底 | `grep javax.servlet` 清零 |
| 启动报「Failed to configure a DataSource」 | 用了 SB2 的 druid starter | 换 `druid-spring-boot-3-starter` |
| `NoSuchBeanDefinitionException: SqlSessionFactory` | 用了 `mybatis-plus-boot-starter` | 换 `mybatis-plus-spring-boot3-starter` |
| `/swagger-ui/index.html` 404 | 白名单漏放行 / 依赖缺失 | 检查 `requestMatchers("/swagger-ui/**","/v3/api-docs/**")` + springdoc 依赖 |
| `@Api` 编译报错 | springfox 已移除 | 换 `@Tag` |
| 启动告警 `spring.redis.*` deprecated | 配置键未迁 | 改 `spring.data.redis.*` |
| POI 导出报 `ClassNotFoundException: javax.xml.bind.*` | JDK 17 无 JAXB | 加 `jaxb-api` 2.3.1 |
| 登录日志 `browser`/`os` 为 null | UA 解析未改 yauaa | 新增 `UserAgentConfig` 并改调用点 |

---

## 4. 数据设计(概要)

### 4.1 Schema 布局(与 `master` 完全一致)

| schema | 表数 | 内容 |
|---|---|---|
| `basicdb` | **34** | 全部业务表(下分 `gen_*` 2 / `qrtz_*` 11 / `sys_*` 21) |
| `public` | 0(本项目不使用) | — |

> **🔶 本分支数据库零变更**:不新增表、不删表、不改字段、不改索引、不改函数。升级 SB 不涉及 DDL。

### 4.2 核心表(ER 简化,与 `master` 一致)

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

### 4.3 关键索引/查询(与 `master` 一致)

| 关键查询 | 命中索引 |
|---|---|
| `select * from sys_user where user_name = ?` | `uk_user_name`(逻辑唯一) |
| `select * from sys_role where role_key = ? and del_flag=0` | `uk_role_key` |
| `select * from sys_menu where status=0 and perms is not null` | 全表扫描(菜单量小) |
| `select * from sys_user_role where user_id=?` | `(user_id, role_id)` 复合 |
| `select * from sys_role_menu where role_id=?` | `(role_id, menu_id)` 复合 |
| `select * from qrtz_triggers where TRIGGER_GROUP=?` | Quartz 自带索引 |

---

## 5. 接口清单(与 `master` 完全一致)

> **🔶 本分支 HTTP 接口零变更**:路径、方法、入参、出参、权限字符全部与 `master` 一致。升级只改内部实现与文档注解。
>
> 完整清单见 `docs/base-postgresql-vue3-概要设计.md` §5(`master` 分支),此处仅保留分类索引:
>
> - **匿名**:`GET /captchaImage` / `POST /login` / `POST /register` / `POST /monitor/shutdown`
> - **已登录**:`POST /logout` / `GET /getInfo` / `GET /getRouters` / `GET /system/dict/type/{dictType}` / `GET /common/download` / `POST /common/upload`
> - **系统管理**:`/system/{user,role,menu,dept,post,config,notice,dict/*}` 各 list/add/edit/remove/export
> - **监控**:`/monitor/{operlog,logininfor,online,job,jobLog,server,cache,druid}`
> - **工具**:`/tool/gen/{list,importTable,download,synchDb,preview}`
> - **🔶 文档**:`GET /swagger-ui/index.html` / `GET /v3/api-docs`(原 `/swagger-ui.html` / `/v2/api-docs`)

### 5.1 🔶 非业务端点变化(升级带来的路径变更)

| 端点 | `master` | 本分支 | 说明 |
|---|---|---|---|
| Swagger UI | `/swagger-ui.html` | **`/swagger-ui/index.html`** | `/swagger-ui.html` 仍会重定向 |
| OpenAPI JSON | `/v2/api-docs` | **`/v3/api-docs`** | OpenAPI 3 规范 |
| Swagger 资源 | `/swagger-resources/**` | ~~不再存在~~ | springfox 专有 |
| Druid 监控 | 由 druid 默认路径 | `/druid/*`(显式声明) | `statViewServlet.url-pattern` |

### 5.2 内部 SPI(同 §2.3)

---

## 6. 关键技术决策(HD-)

| # | 决策 | 理由 | 影响 |
|---|---|---|---|
| HD-01 | Spring Security `STATELESS`,基于 JWT | 无状态横向扩展;避免 CSRF | Token 失效前无法强制下线(已用 Redis 缓解) |
| HD-02 | 异步日志走 `AsyncManager` + 独立线程池 | 不污染主链路 | 日志丢失不会回滚主事务 |
| HD-03 | MyBatis-Plus + 自定义 XML 双轨 | 通用 CRUD 用 MP,复杂 SQL 走 XML | 维护两套 |
| HD-04 | 数据权限通过切面 + SQL 拼接 | 透明、对业务代码侵入小 | 必须确保 SQL 不要把 `where` 拼错 |
| HD-05 | 限流走 `RateLimiter` + Redis(令牌桶) | 单机也能用 Guava RateLimiter 兜底 | — |
| HD-06 | Token 30min + Redis 缓存 | 主动失效可实现强退 | Redis 不可用时降级到仅 JWT |
| HD-07 | 移动端使用 uni-app `up-` 组件统一注册 | 显式注册避免 H5/小程序差异 | `main.js` 维护一份注册表 |
| HD-08 | 移动端 `pages.json` 静态优先 | 小程序/原生端必须存在 | 动态路由留迭代 |
| **HD-09** | **一次性升到 SB 3.5.16(而非 3.0.x 渐进)** | 3.0 → 3.5 的破坏性变更少但兼容矩阵宽;一次到位避免二次迁移 | JDK 17 强制;不能停在 3.0 |
| **HD-10** | **Java EE → Jakarta 全量硬切,不做兼容层** | 兼容层(如 `javax.servlet` shim)会引入两套 API 冲突,且 SB 3.2+ 已不支持 | 114 处一次性替换;`grep javax.servlet` = 0 |
| **HD-11** | **springfox → springdoc(不修 springfox)** | springfox 3.0 官方停维护且与 Spring 6 不兼容,修补成本高于迁移 | 全部注解与配置类重写 |
| **HD-12** | **Security 6 用 `SecurityFilterChain` Bean + Lambda DSL** | `WebSecurityConfigurerAdapter` 在 SB 3 已删除;Lambda DSL 是官方唯一推荐写法 | `SecurityConfig` 结构重写 |
| **HD-13** | **starter 必用 SB3 变体,不做自定义 autoconfigure** | 自行写 autoconfigure 维护成本高且易踩 SPI 注册 | Druid/MP 各换 1~2 处 |
| **HD-14** | **UA 解析换 yauaa(不保留 bitwalker)** | bitwalker `UserAgentUtils` 多年未更新,新版浏览器识别错误 | 新增 `UserAgentConfig`,`AsyncFactory` 调用点改写 |
| **HD-15** | **JAXB 显式引入 `jaxb-api` 2.3.1(保持 `javax.xml.bind` 包名)** | JDK 17 移除 JAXB;JAXB 规范本身未随 Jakarta 改名 | POI 等依赖可正常使用 |
| **HD-16** | **数据库零变更、前端零改动** | SB 升级是应用层变化,不触及数据契约与前端契约 | 上线风险收敛到后端;回滚只需回退 jar |

---

## 7. 部署视图(与 `master` 一致;🔶 运行时要求 JDK 17)

```
        ┌────────────────────────────┐
        │  Nginx(可选)              │
        │  /api/*  → 8080           │
        │  /        → web-ui dist   │
        └─────────────┬──────────────┘
                      │
                      ▼
        ┌────────────────────────────────┐
        │  basic-admin-api jar           │
        │  🔶 运行时:JDK 17              │
        │  端口:8080                     │
        │  profile: dev / prod           │
        │  🔶 内嵌 Tomcat 10.1+(jakarta) │
        └──────┬──────────────┬──────────┘
               │              │
               ▼              ▼
        ┌────────────┐   ┌──────────────┐
        │PostgreSQL  │   │  Redis       │
        │15+(basicdb)│   │ (可选)       │
        │34 表       │   │ 🔶 spring.data.redis.*
        └────────────┘   └──────────────┘

        web-mp:  🔶 与 master 完全一致(JS,无改动)
        ┌────────────────────────────┐
        │  HBuilder / 微信开发者工具  │
        │  编译产物:dist/dev/mp-weixin│
        └────────────────────────────┘
```

### 7.1 启动顺序

1. **安装 JDK 17**,IDE Project SDK / Language level 设为 17
2. PostgreSQL 启动 + `psql -f basicdb-vue3.sql` 恢复库(34 表,与升级前一致)
3. (可选)Redis 启动
4. `mvn clean compile` → 确认 0 ERROR
5. `mvn spring-boot:run -pl basic-admin-api` 启动后端
6. 访问 `/swagger-ui/index.html` 验证文档;访问 `/druid/` 验证监控
7. `pnpm dev` 启动 web-ui;`pnpm dev:h5` 启动 web-mp(前端零改动)
8. 默认账号 `admin / admin123`,登录后改密

### 7.2 并发与恢复(与 `master` 一致)

- Tomcat `max-threads=800` 限流保护
- 优雅停机:`actuator/shutdown` 接收 POST(匿名白名单)
- 数据库连接池 Druid:`min-idle=5` / `max-active=20`(可调)
- 定时任务持久化到 `qrtz_*`,应用重启后 Quartz 自动恢复

### 7.3 🔶 回滚策略(升级特有)

| 场景 | 回滚动作 | 数据影响 |
|---|---|---|
| 升级后出现阻塞性缺陷 | 回退到 `master` 的 jar(**DB 无需回滚**) | **无**(库结构未变) |
| 部分接口异常 | 同上,或热修单个类 | 无 |
| 前端异常 | 前端零改动,**不可能是前端问题** | 无 |

> **这是本分支最大优势**:数据库与前端都没动,回滚成本极低。

---

## 8. 风险与待定项

| 编号 | 项 | 影响 | 归属文档 |
|---|---|---|---|
| P1 | 操作日志大表清理策略 | 长期保留 → 表膨胀 | 《详细设计》§8 审计 |
| P2 | 移动端无 refresh token | 30min 后强制重登 | 《需求文档》A-3 |
| P3 | ~~springfox 3.0 与 SB 2.5 兼容性~~ **已消除**(换 springdoc) | — | 关闭 |
| P4 | `web-mp` uni-app 3 alpha 稳定性 | DCloud 官方未 GA | 架构 R3 |
| P5 | `com.basic` + `com.core` 双包混用 | 学习成本 | onboarding 文档(待补) |
| P6 | 国际化文案缺失 | 移动端仅中文 | 架构 R8 |
| P7 | Quartz 任务无热加载(误判) | — | — |
| **P8** | **`javax.*` 残留导致运行时 `NoClassDefFoundError`** | **高** | 架构 R11;《需求文档》AC-16 |
| **P9** | **三方库隐式依赖 `javax.servlet`** | 中 | 架构 R12;升级依赖需复验 |
| **P10** | **springfox 注解残留导致编译失败** | 中 | 架构 R13;《需求文档》AC-17 |
| **P11** | **starter 用错变体导致 Bean 不注册** | **高** | 架构 R14;HD-13;《需求文档》AC-18 |
| **P12** | **SB 3 `PathPatternParser` 替代 `AntPathMatcher`,`/**` 语义差异** | 中 | 架构 R15;上线前回归白名单 |
| **P13** | **`spring.data.redis.*` 迁移不彻底(启动告警)** | 低 | 架构 R16;《需求文档》AC-19 |
| **P14** | **代码生成器模板仍产 SB2 代码(需手工改 jakarta)** | **中** | 架构 R20;《需求文档》A-9 |
| **P15** | **deprecation 警告未清理** | 低 | 架构 R19;《需求文档》A-8 |

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `master` 概要设计(v0.1)为基线,叠加 SB 2→3 升级专题:§2.4 升级后结构对照(javax→jakarta 映射表 / Security 5→6 配置对照 / springfox→springdoc 对照 / starter 变体矩阵 / 请求链路前后对照)、§3.6 升级改造流程(10 步 + 验证链 + 8 类踩坑处置)、§5.1 端点变化、HD-09~HD-16、§7.3 回滚策略、P8~P15 |
