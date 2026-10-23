# 基础平台(base-postgresql-vue3) 架构方案 — `springboot4` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot4`** 分支(基于 RuoYi-Vue **3.9.2** 改造)
> 上游依赖:`springboot3` 分支(**Spring Boot 3.5.16 / JDK 17**)→ `master`(SB 2.5.14 / JDK 11)
> 本分支 = **`springboot3` 基线 + 后端 Spring Boot 3.5.16 → 4.1.1 升级(JDK 17 → 21)**;**前端两端保持 JavaScript、数据库 34 张表、业务功能全部不动**。
> **本分支最大技术差异 = Jackson 2 → Jackson 3 全包名迁移**(`com.fasterxml.jackson.*` → `tools.jackson.*`)。

---

## 1. 系统定位

**一句话定位**:面向中小团队的后台管理基础平台,提供「用户/角色/菜单/部门/字典/参数/定时任务/代码生成/监控」一整套开箱即用的 RBAC 后台能力,并提供 uni-app 移动端;**本分支把后端推到最新体系(JDK 21 + Spring Boot 4.1.1 + Spring Framework 7 + Spring Security 7 + Jackson 3)**,前端与数据层保持 `springboot3` 原样。

| 维度 | 内容 |
|---|---|
| 使用方 | 内部业务开发者(Java 后端 + Web 前端 + 移动端开发者),最终被业务方运营人员使用 |
| 触发方式 | PC 浏览器、微信小程序、Android/iOS App,均通过 HTTP/JSON 调用后端 REST |
| 数据形态 | PostgreSQL(34 张表) + 本地文件上传 + Redis(Jackson 3 序列化) |
| 范围边界 | RBAC 系统管理、字典/参数/通知、定时任务、代码生成、登录/锁屏、操作日志、PC + 移动两套前端(JS) |
| 不做清单(本分支) | 前端 TS 化(走 `springboot4_TypeScript`)、业务流程引擎(走 `springboot4-extend-flowable`)、行为验证码(走到 `springboot3-tianai-captcha` 系列)、多租户、SSO 联邦 |

### 1.1 核心用户动作必做项

- 登录 / 注销(账号 + 密码 + RSA 加密 + 图形验证码 **kaptcha 2.3.3**)
- 用户/角色/菜单/部门/岗位/字典 等基础数据维护
- 动态路由 + 按钮级权限(基于角色 + 权限字符串)
- 通知公告 + 文件上传下载
- 定时任务(Cron 表达式)在线管理
- 数据表反向生成 Controller/Service/Mapper/前端
- 锁屏 → 解锁
- PC 端 + 移动端 同一套后端接口
- **升级后的工程规范**:JDK 21 编译 + Jackson 3 API + Security 7 严格化

---

## 2. 约束与质量属性

| 属性 | 目标 | 标注 |
|---|---|---|
| **JDK** | **严格锁定 JDK 21**(`java.version=21`,Language level 21) | **最强约束**;`springboot3` 是 17 |
| **Spring Boot** | **4.1.1** | **最强约束**;不可回退 3.5.16 |
| **Spring Framework** | **7.0.9**(SB 4.1.1 自带) | ⬆️ 由 6.x 升 |
| **Spring Security** | **7.1.1**;**`getAuthorities()` 不允许返回 `null`**(严格化) | **本分支关键坑之一** |
| **Jackson** | **3.1.5**;核心/数据绑定 `tools.jackson.*`,**注解仍是 `com.fasterxml.jackson.annotation.*`** | **★ 本分支最大坑** |
| **MyBatis-Plus** | **3.5.17**(`mybatis-plus-spring-boot4-starter`)+ MyBatis-Spring-Boot **4.1.0** + PageHelper **4.1.1** | starter 必须用 `-4-` 变体 |
| **连接池** | Druid **1.2.28**(`druid-spring-boot-4-starter`,包名 `com.alibaba.druid.spring.boot4.autoconfigure`) | starter 必须用 `-boot-4-` |
| **API 文档** | **springdoc-openapi 3.1.1**;`/swagger-ui/index.html` + `/v3/api-docs` | ⬆️ 由 2.9.0 升 |
| **Jakarta EE** | `jakarta.servlet` **6.1.0**;实测 **114 处 jakarta import,0 处 `javax.servlet`** | ⬆️ 继承 |
| **Quartz** | **`spring-boot-starter-quartz`**(自动装配 `SchedulerFactoryBean`),取代手写 `quartz:2.5.2` 依赖 | **RAMJobStore**(未切 JDBC JobStore) |
| **JSON 库决策** | **不引入 fastjson2**(若依原版用,本项目统一 Jackson 3) | 见 ADR-21 |
| **数据库** | PostgreSQL ≥ 15.1,统一 `basicdb` schema,**34 张表 + 5 函数** | **本分支零变更** |
| 验证码 | kaptcha 2.3.3(未换 tianai) | ⬆️ 继承 |
| 鉴权 | JWT(无状态)+ BCrypt | Token 30 分钟 |
| 一致性 | 关系型事务 + 异步日志(`@Async` 落 `sys_oper_log` / `sys_logininfor`) | 操作日志失败不影响主链路 |
| 性能 | MyBatis-Plus + PageHelper;Jackson 3 序列化性能较 2.x 有提升 | |
| 审计 | 写操作落 `sys_oper_log`,登录事件落 `sys_logininfor` | 不可关闭 |
| 安全 | XSS + 重复提交 + 限流 + CORS 白名单 | ⬆️ 继承 |
| 部署 | 单体 jar,`tomcat.max-threads=800`;**运行时 JDK 21** | |
| 构建 | 7 个子模块 | ⬆️ 结构不变 |

**最强约束**:① **JDK 21 + SB 4.1.1 不可回退**;② **Jackson 3 的 `Module` 只能在 `JsonMapper.builder()` 构建时注册**(运行时 `registerModule` 不存在);③ **`com.fasterxml.jackson.*` 只允许出现在 `annotation` 子包**(核心/数据绑定必须 `tools.jackson.*`);④ **`getAuthorities()` 不得返回 null**;⑤ **数据库零变更**;⑥ **前端零改动**。

---

## 3. 总体架构

### 3.1 容器视图(🔶 = 本分支改动;⬆️ = `springboot3` 继承)

```
┌───────────────────────────────────────────────────────────────┐
│  用户端(⬆️ 与 springboot3 完全一致,JS)                          │
│  ┌──────────────────┐    ┌──────────────────────────────┐    │
│  │  web-ui (PC)     │    │  web-mp (移动:uni-app 3)     │    │
│  │  Vue 3.5.26      │    │  H5/小程序/Android/iOS        │    │
│  │  Element Plus    │    │  uview-plus 3.3.62           │    │
│  │  Vite 6.4.1 · JS │    │  Vite 5.2.8 · JS             │    │
│  └────────┬─────────┘    └──────────────┬───────────────┘    │
│           │ HTTPS / JSON(🔶 Jackson 3 序列化)                 │
└───────────┼─────────────────────────────┼─────────────────────┘
            ▼                             ▼
┌───────────────────────────────────────────────────────────────┐
│  🔶 Spring Boot 4.1.1 (basic-admin-api) · JDK 21              │
│  ┌─────────────────────────────────────────────────────────┐  │
│  │  ⬆️ Filter 链(jakarta.servlet 6.1.0)                    │  │
│  │  🔶 Security 7.1.1:SecurityFilterChain                  │  │
│  │      + @EnableMethodSecurity                            │  │
│  │      + getAuthorities() 不得为 null                     │  │
│  │      放行:/login /register /captchaImage                 │  │
│  │            /swagger-ui/** /v3/api-docs/** /druid/**      │  │
│  │  Controller (basic-admin-api / extend-*)                 │  │
│  │  ↓                                                       │  │
│  │  Service (basic-admin-core / extend-quartz / business)  │  │
│  │  ↓                                                       │  │
│  │  🔶 MyBatis-Plus 3.5.17(spring-boot4-starter)           │  │
│  │  🔶 MyBatis-Spring-Boot 4.1.0 + PageHelper 4.1.1        │  │
│  │  🔶 Druid 1.2.28(boot-4-starter)                        │  │
│  └────────────────────────┬────────────────────────────────┘  │
│  ┌────────────────────────┴────────────────────────────────┐  │
│  │ AOP:LogAspect / DataScopeAspect / RateLimiter /          │  │
│  │     RepeatSubmit / DataSource                            │  │
│  │ 🔶 JSON:Jackson 3.1.5(tools.jackson.*)                   │  │
│  │      JsonMapperBuilderCustomizer + JacksonModule         │  │
│  │ 🔶 文档:springdoc-openapi 3.1.1                          │  │
│  │ ⬆️ UA 解析:yauaa 8.2.0                                   │  │
│  │ ⬆️ 验证码:kaptcha 2.3.3                                  │  │
│  │ 🔶 定时:spring-boot-starter-quartz(自动装配)             │  │
│  └─────────────────────────────────────────────────────────┘  │
└──────────┬──────────────────────────────────┬──────────────────┘
           ▼                                  ▼
   ┌──────────────────┐              ┌──────────────────────────┐
   │ PostgreSQL ≥15.1 │              │ Redis(可选)              │
   │ schema=basicdb   │              │ 字典/Token/限流           │
   │ 34 张表 + 5 函数 │              │ 🔶 Jackson 3 序列化       │
   │ 🔶 与 springboot3 │              │    + defaultTyping       │
   │    完全一致       │              │    + 多态类型白名单       │
   └──────────────────┘              └──────────────────────────┘
```

### 3.2 模块清单(7 个 Maven 子模块;⬆️ 结构不变,🔶 依赖升级)

| 模块 | Maven artifact | 职责 | 🔶 本分支调整 |
|---|---|---|---|
| `basic-admin-api` | `basic-admin-api` | 启动入口 + 26 Controller | `SwaggerConfig` 适配 springdoc 3.1;`BasicApplication` 微调 |
| `basic-admin-core` | `basic-admin-core` | 业务 Service/Mapper/Domain | 实体注解保持 `com.fasterxml.jackson.annotation`;`RouterVo` / `SysLogininfor` / `SysOperLog` 微调 |
| `basic-framework` | `basic-framework` | Security/Redis/MP/Swagger/Filter/AOP/Token/限流 | **改动静最大**:`BigNumberModuleConfig`(新增)/ `BigNumberSerializer`(重写)/ `RedisConfig`(Jackson 3)/ `JacksonConfig` / `ResourcesConfig`(精简+拆)/ `DruidConfig`(boot4 包名)/ `SecurityConfig`(Security 7)/ `PermitAllUrlProperties` / `TokenService` / `ApplicationConfig` |
| `basic-common` | `basic-common` | 工具/注解/枚举/基类 | `JsonUtils`(Jackson 3)/ `RedisCache`(脏数据处理)/ `LoginUser`(`getAuthorities()`)/ 基类实体 |
| `business-core` | `business-core` | 业务扩展点 | ⬆️ 基本不变 |
| `extend-quartz` | `extend-quartz` | 定时任务 | **改用 `spring-boot-starter-quartz`**(自动 `SchedulerFactoryBean`);`SysJob` 注解 |
| `extend-generator` | `extend-generator` | 代码生成 | `VelocityUtils` Jackson 3 适配;⚠️ 模板仍产 SB2 风格代码(R20) |

### 3.3 数据模型核心表(详细见《数据库设计》)

> **🔶 本分支与 `springboot3` / `master` 完全相同**:34 张表(`gen_*` 2 + `qrtz_*` 11 + `sys_*` 21),`basicdb` schema,5 个函数。
> **SB 4 升级不需要任何数据库变更**(与 SB 3 升级同理)。

| 域 | 表 | 说明 |
|---|---|---|
| 鉴权 | `sys_user` / `sys_role` / `sys_menu` / `sys_user_role` / `sys_role_menu` / `sys_role_dept` / `sys_user_post` | RBAC |
| 组织 | `sys_dept` / `sys_post` | 部门树 + 岗位 |
| 系统 | `sys_config` / `sys_dict_type` / `sys_dict_data` / `sys_files` / `sys_i18n` | 参数/字典/文件/国际化 |
| 通知 | `sys_notice` / `sys_notice_read` | 公告 + 已读 |
| 审计 | `sys_oper_log` / `sys_logininfor` | 操作/登录日志 |
| 示例 | `sys_demo` | `business-core` 示例 |
| 定时 | `qrtz_*`(11 张) | Quartz 任务表 |
| 代码生成 | `gen_table` / `gen_table_column` | 元数据 |

> ⚠️ **Quartz 特别注意**:本分支用 **RAMJobStore**(内存),`qrtz_*` 表**存在但不被使用**。若需持久化任务须切 JDBC JobStore(见 P 系列风险)。

### 3.4 关键流程

#### 登录流程(前端零改动;🔶 内部 Jackson 3 序列化 + Security 7)

```
[前端 JS]                 [后端 SB 4.1.1]                       [DB / Redis]
  │  GET /captchaImage      │                                      │
  │ ─────────────────────▶  │ CaptchaController(kaptcha 2.3.3)     │
  │ ◀─────────────────────  │ {uuid, img, captchaEnabled}          │
  │                         │  🔶 响应经 Jackson 3 序列化           │
  │                         │                                      │
  │  POST /login            │ SysLoginController                   │
  │  {username,password,    │  ① RSA 解密                           │
  │   code,uuid}            │  ② validateCaptcha(kaptcha)          │
  │ ─────────────────────▶  │  ③ loadUserByUsername ──────────────▶│
  │                         │  ④ BCrypt.matches                    │
  │                         │  ⑤ TokenService.createToken          │
  │                         │     🔶 写 Redis 用 Jackson 3          │
  │                         │        (defaultTyping + 白名单)       │
  │                         │  ⑥ AsyncFactory(yauaa 解析 UA)        │
  │ ◀─────────────────────  │ {token}                              │
  │                         │                                      │
  │  GET /getInfo           │ 🔶 LoginUser.getAuthorities()        │
  │ ─────────────────────▶  │    不得返回 null(Security 7)         │
  │  GET /getRouters        │ 动态路由树                            │
```

#### 🔶 请求进出链路(SB4 差异点标出)

```
HTTP 请求
   │
   ▼
⬆️ CorsFilter → XssFilter → JwtAuthenticationTokenFilter(jakarta)
   │
   ▼
🔶 Security 7 过滤链(SecurityFilterChain + requestMatchers)
   │   · getAuthorities() 返回空集合而非 null
   ▼
DispatcherServlet → Controller
   │
   ▼
Service / Mapper(MP 3.5.17)
   │
   ▼ JSON 响应
🔶 Jackson 3 HttpMessageConverter(JacksonJsonHttpMessageConverter)
   │   · BigNumber / ToString 模块由 JsonMapperBuilderCustomizer 注入
   ▼
前端 JS(零改动)
```

#### 定时任务调度(🔶 改用 starter)

```
新增任务:
  SysJobController.add → SysJobServiceImpl.add
    → ScheduleUtils.createScheduleJob → 🔶 SchedulerFactoryBean(SB4 starter 自动装配)
      → RAMJobStore(**内存,不落 qrtz_* 表**)

触发:
  Cron 到点 → AbstractQuartzJob.execute
    → JobInvokeUtil.invokeMethod(ryTask.ryXxx)
      → try/catch → sys_job_log
```

### 3.5 状态机(⬆️ 与 `springboot3` 一致)

| 对象 | 状态 | 流转 |
|---|---|---|
| 定时任务 | `NORMAL` / `PAUSED` | changeStatus 切换 |
| 用户 | `0=正常` / `1=停用` | 新建=0,管理员停用=1 |
| 通用枚举 | `BusinessType` 增删改/导出/导入/强退/清空/生成代码 | `@Log(...)` |
| 通知已读 | 未读 / 已读 | `sys_notice_read(user_id, notice_id)` 唯一 |

### 3.6 🔶 Jackson 3 架构级影响(本分支核心,详见《详细设计》§3.10)

| 维度 | Jackson 2(`springboot3`) | **Jackson 3(本分支)** |
|---|---|---|
| 核心包 | `com.fasterxml.jackson.core.*` | **`tools.jackson.core.*`** |
| 数据绑定 | `com.fasterxml.jackson.databind.*` | **`tools.jackson.databind.*`** |
| **注解包** | `com.fasterxml.jackson.annotation.*` | **不变!仍是 `com.fasterxml.jackson.annotation.*`** |
| 异常体系 | `JsonProcessingException extends IOException` | **`JacksonException extends RuntimeException`**(非受检) |
| 序列化器基类 | `JsonSerializer<T>` / `StdSerializer<T>` | **`ValueSerializer<T>`** / `StdSerializer<T>` |
| 上下文参数 | `SerializerProvider` | **`SerializationContext`** |
| 模块基类 | `databind.Module` | **`JacksonModule`** |
| 模块注册时机 | 运行时 `ObjectMapper.registerModule()` | **只能构建时 `JsonMapper.builder().addModule()`** |
| 默认类型 | `mapper.activateDefaultTyping()` | **builder 上** `activateDefaultTyping()` |
| HTTP 转换器 | `MappingJackson2HttpMessageConverter` | **`JacksonJsonHttpMessageConverter`** |
| `NumberSerializer` | `ser.std.NumberSerializer` 存在 | **不存在**,须自写 `ValueSerializer<Number>` |
| Spring Boot 钩子 | `Jackson2ObjectMapperBuilderCustomizer` | **`JsonMapperBuilderCustomizer`** |

**架构级约束(3 条)**:

1. **注解类"不变"是设计,不是遗漏** —— 全仓仅 4 个唯一注解 import(`@JsonFormat` / `@JsonIgnore` / `@JsonIgnoreProperties` / `@JsonInclude`)留在 `com.fasterxml.jackson.annotation`,分布在 11 个实体/工具类中;**这是 Jackson 3 官方保留的注解包**,不需要迁移。
2. **所有 Module 必须在构建期注册** —— 运行时注册 API 已删除,任何"启动后动态加序列化器"的写法都会编译失败。
3. **`JsonMapperBuilderCustomizer` 必须放在独立配置类** —— 否则与 `ResourcesConfig → RepeatSubmitInterceptor → RedisCache → RedisTemplate → JsonMapper` 形成**循环依赖**(本分支已拆出 `BigNumberModuleConfig`)。

---

## 4. 关键决策 ADR

> **ADR-01~ADR-18 继承自 `springboot3`**(基础底座 / 数据库 / 鉴权 / ORM / 缓存 / 前端双轨 / Jakarta 迁移 / springdoc / Security 6 / starter 变体 / yauaa / jaxb 等),详见 `docs/springboot3-架构方案.md` §4。下表为本分支**新增 ADR-19~ADR-30**。

| # | 决策点 | 采纳方案 | 备选 + 放弃原因 | 是否演进锁死 |
|---|---|---|---|---|
| **ADR-19** | **升级跨度** | **一次从 SB 3.5.16 升到 SB 4.1.1 + JDK 21** | 分两步(3.5 → 4.0 → 4.1):4.0 是首个 GA,4.1 已稳定;分两步徒增两次回归成本 | **是** |
| **ADR-20** | **Jackson 版本** | **Jackson 3.1.5**(SB 4 默认) | 保留 Jackson 2:SB 4 已移除 Jackson 2 自动配置,需大量手工装配 | **是** |
| **ADR-21** | **JSON 库选型** | **统一 Jackson 3,不引 fastjson2** | 引 fastjson2(若依原版做法):多一套序列化栈、安全史有漏洞、与 Redis 序列化混用风险 | **是** |
| **ADR-22** | **Jackson 注解处置** | **不动** `com.fasterxml.jackson.annotation.*` | 全部改 `tools.jackson.annotation.*`:**该包在 Jackson 3 不存在**,改了必然编译失败 | **是** — 官方设计 |
| **ADR-23** | **Module 注册方式** | **构建时注册**:`JsonMapper.builder().addModule(...)`;Spring 侧用 **`JsonMapperBuilderCustomizer`** | 运行时注册:API 已删除;自定义 `ObjectMapper` Bean:会覆盖 SB4 默认配置,丢失其它定制 | **是** |
| **ADR-24** | **Customizer 落位** | **独立配置类 `BigNumberModuleConfig`**,不放在 `ResourcesConfig` | 放 `ResourcesConfig`:`ResourcesConfig → RepeatSubmitInterceptor → RedisCache → RedisTemplate → JsonMapper` **循环依赖**,启动即失败 | **是** |
| **ADR-25** | **大数序列化** | 自写 `BigNumberSerializer extends ValueSerializer<Number>`;安全区间外输出**字符串** | 用 `NumberSerializer`(Jackson 2 旧类):**Jackson 3 已删除**;默认输出:JS 端 `Long` 精度丢失 | **是** |
| **ADR-26** | **Redis 序列化** | Jackson 3 + **`defaultTyping`(在 builder 上)+ `BasicPolymorphicTypeValidator` 白名单** | 用 JDK 序列化:不可读、跨版本脆弱;用 String 序列化:复杂对象不可存 | **是** |
| **ADR-27** | **Security 7 适配** | `getAuthorities()` **返回空集合**,绝不返回 `null` | 返回 `null`:Security 7 严格化后**直接 NPE / 启动失败** | **是** |
| **ADR-28** | **Quartz 依赖** | **`spring-boot-starter-quartz`**(自动装配 `SchedulerFactoryBean`),**RAMJobStore** | 手工 `org.quartz-scheduler:quartz`(SB3 用法):与 SB4 自动装配冲突;JDBC JobStore:PG `varchar(1)` 字段有雷(若依已知问题) | 否 — 可切 JDBC |
| **ADR-29** | **Druid / MP starter** | Druid **`-boot-4-starter`** + MP **`spring-boot4-starter`** | 沿用 SB3 变体:自动装配 SPI 路径不匹配,Bean 不注册 | **是** |
| **ADR-30** | **前端是否同步 TS 化** | **不做**,保持在 `springboot4_TypeScript` | 顺带做:两分支职责重叠,且 TS 化与 SB4 升级正交 | **是** |

---

## 5. 里程碑

| 阶段 | 内容 | 验收 | 触发升级的信号 |
|---|---|---|---|
| **M0(已交付,`master`)** | RuoYi 3.8.2 基础 + SQL 迁 PostgreSQL + 锁屏 | 7 模块;34 表可恢复 | — |
| **M1(已交付,`springboot3`)** | SB 2.5.14 → 3.5.16 / JDK 11 → 17:Jakarta / Security 6 / springdoc 2.9 / yauaa | JDK 17 下 0 ERROR;`javax.servlet`=0 | — |
| **M2(已交付,本分支)** | **SB 3.5.16 → 4.1.1 + JDK 17 → 21**:Jackson 3 全量迁移、Security 7 严格化、springdoc 3.1、MP 3.5.17 / `-boot4-` starter、Quartz 改 starter、`JsonMapperBuilderCustomizer` + 循环依赖拆分、`BigNumberSerializer` 重写 | JDK 21 下 `mvn clean compile` 0 ERROR;启动成功;登录/CRUD/锁屏/定时任务/代码生成/Redis 缓存全链路回归;`grep com.fasterxml.jackson` 仅剩 `annotation` | — |
| **M3(下一轮)** | 移动端:动态菜单 / 国际化 / WebSocket 推送 / 锁屏 / 个人中心编辑 | 见 web-mp README 待办 | 业务方提出 >5 个角色/菜单变更需求 |
| **M4(可选)** | **Quartz 切 JDBC JobStore** | `qrtz_*` 表被真实使用;重启任务不丢 | 需要任务持久化 / 多实例调度 |
| **M5(可选)** | **springdoc 3.1 全量兼容验证** | OpenAPI 3.1 schema 与 SB4 全接口校验 | 前端/三方用 OpenAPI 生成 SDK |
| **M6(可选)** | **`WebMvcConfigurer` 废弃替代** | Spring Framework 7 给出 GA 替代方案后再迁 | 官方发布新接口 |
| **M7(可选)** | MP 3.5.7 → 3.5.17 / pagehelper 2.1 → 4.1 的中间变更逐项核对 | 全 API 回归 | 出现分页/链式调用异常 |
| **M8(分支)** | 前端 TS 化 | 单独分支 `springboot4_TypeScript` | 需要类型保护 |
| **M9(分支)** | 流程引擎(Flowable) | 单独分支 `springboot4-extend-flowable` | 出现审批流需求 |
| **M10(分支)** | SSO:接入 keycloak 联邦 | 账号互通 | 出现第二个业务系统需统一登录 |

**本次不做,直到有信号**:微服务拆分、K8s 化、灰度发布、APM 探针、消息中心抽象、GraalVM 原生镜像、fastjson2 引入。

---

## 6. 审查与遗留风险

### 6.1 已覆盖

| 维度 | 设计 |
|---|---|
| 失败设计 | `GlobalExceptionHandler` + `R/AjaxResult`;`ServiceException` / `UserPasswordNotMatchException` / `CaptchaException` |
| 幂等 | `@RepeatSubmit` + `RepeatSubmitInterceptor` |
| 权限 | 菜单级 + 按钮级 + 数据级(`@DataScope`);**Security 7 严格化已适配** |
| 一致性 | 关系型事务;异步日志 `AsyncManager` 隔离 |
| 可观测 | `sys_oper_log` / `sys_logininfor` / springdoc 3.1 / Druid / `actuator/shutdown` |
| **🔶 序列化** | Jackson 3 + 构建时 Module 注册 + 大数安全 + Redis 多态白名单 |

### 6.2 风险清单

> **R1~R20 继承自 `springboot3`**(uni-app alpha / 双包前缀 / 国际化 / refresh token / javax 残留 / starter 变体 / PathPatternParser / Jackson 3 留待 / Lombok / deprecation / 生成器模板未适配 等),详见 `docs/springboot3-架构方案.md` §6.2。下表为本分支**新增 R21~R34**。

| # | 风险 | 影响 | 缓解 |
|---|---|---|---|
| **R21** | **Jackson 2/3 混用残留**:若新代码误用 `com.fasterxml.jackson.databind.*` 会**编译失败** | **高** | 全仓 grep:仅允许 `com.fasterxml.jackson.annotation.*`(4 个唯一 import);Code review + 编译兜底 |
| **R22** | **运行时注册 Module** 的旧写法(`mapper.registerModule`)在 Jackson 3 **不存在** | **高** | ADR-23;所有 Module 走 `JsonMapper.builder()` 或 `JsonMapperBuilderCustomizer` |
| **R23** | **`JsonMapperBuilderCustomizer` 循环依赖**(Customizer → RedisTemplate → JsonMapper) | **高** | ADR-24 已拆 `BigNumberModuleConfig`;新增 Customizer 必须独立类 |
| **R24** | **Jackson 3 异常是 `RuntimeException`**,旧 `catch (IOException)` 捕获不到 | **中** | `JsonUtils` 已改 `catch (JacksonException)`;**新代码必须注意** |
| **R25** | **`NumberSerializer` 已删除**,依赖它的自定义序列化器会编译失败 | 中 | ADR-25 已自写 `ValueSerializer<Number>` |
| **R26** | **Security 7 `getAuthorities()` 返回 null → NPE/启动失败** | **高** | `LoginUser` 已改返回空集合(ADR-27) |
| **R27** | **Quartz 用 RAMJobStore,任务不持久化**:重启后动态任务丢失 | **中** | 已知取舍(ADR-28);需持久化则切 JDBC JobStore(M4) |
| **R28** | **Redis 升级期脏数据**:旧 Jackson 2 序列化的缓存对象无法被 Jackson 3 反序列化 | **中** | `RedisCache` 已加脏数据处理(反序列化失败则当未命中);升级时建议**清空 Redis** |
| **R29** | **Redis defaultTyping 多态反序列化安全**(反序列化 gadget 风险) | **中** | `BasicPolymorphicTypeValidator` 白名单,只允许业务包 |
| **R30** | **springdoc 3.1.1 与 SB 4 的兼容性未全量验证** | 中 | 目前"能用不崩";M5 计划全量校验 |
| **R31** | **MP 3.5.7 → 3.5.17 / pagehelper 2.1 → 4.1 跨多版本**,中间变更未逐项核对 | 中 | 只测了基础 CRUD + 分页;M7 计划全 API 回归 |
| **R32** | **`WebMvcConfigurer` 在 Spring Framework 7 已 `@Deprecated`** | 低 | 暂无 GA 替代;继续用,关注官方 |
| **R33** | **Druid 监控 `statViewServlet` 生产暴露风险** | **中** | 生产必须关或强密码 + IP 白名单(A-12) |
| **R34** | **代码生成器模板仍产 SB2 风格代码**(`javax.*` + springfox) | 中 | 继承 R20;生成后需手工改 jakarta + `io.swagger.v3`;彻底方案加 SB4 模板 |

---

## 7. 参考来源

- `README.md`(仓库根,本分支,**已含 SB4 升级细节 §4.1~§4.15**)
- **上游基线文档**:
  - `docs/springboot3-架构方案.md` — 本分支的 SB3 基线(Jakarta / Security 6 / springdoc 2.9 / yauaa),**ADR-01~18 与 R1~R20 均在彼处**
  - `docs/springboot2-extend-flowable-架构方案.md` — 上游 Flowable 分支
  - `docs/base-postgresql-vue3-架构方案.md`(在 `master`)— 原始基线
- 兄弟分支:`springboot3`(本分支去掉 SB4 升级)、`springboot4_TypeScript`(本分支 + 前端 TS)、`springboot4-extend-flowable`(本分支 + Flowable)
- Jackson 3 官方迁移指南;Spring Boot 4.1 / Spring Framework 7 / Spring Security 7 官方文档;springdoc 3.x 文档

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `springboot3` 架构方案(v0.1)为基线(ADR-01~18 / R1~R20 继承),叠加「SB 3.5.16 → 4.1.1 + JDK 17 → 21」升级专题:§2 约束(JDK21/Jackson3/Security7/starter 4/Quartz/不引 fastjson2)、§3.1 容器视图、§3.2 模块逐项调整、§3.4 登录/请求链路/定时任务、§3.6 Jackson 3 架构级影响(12 项对照 + 3 条架构约束)、ADR-19~ADR-30 共 12 条、M2 升级里程碑 + M4/M5/M6/M7 后续、R21~R34 共 14 条 |
