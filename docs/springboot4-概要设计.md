# 基础平台(base-postgresql-vue3) 概要设计(HLD) — `springboot4` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot4`** 分支
> 前置依赖:《架构方案》v0.1、《需求文档》v0.1
> 本分支 = **`springboot3` 基线 + 后端 SB 3.5.16 → 4.1.1(JDK 17 → 21)**。
> 业务逻辑架构、核心流程、数据设计、接口清单、部署拓扑与 `springboot3` **一致**(本文档不重复);**新增 §2.4 升级后结构对照(Jackson 3 / Security 7 / starter / Quartz)、§3.6 升级改造流程、§6 HD-17~HD-28、§8 P16~P30**。
> 🔶 = 本分支改动;⬆️ = `springboot3` 继承。

---

## 1. 设计目标与约束回顾

| 来源 | 关键约束 |
|---|---|
| 架构方案 §2 | **JDK 21 + SB 4.1.1 锁死**;Jackson **3.1.5**;Security **7.1.1**(`getAuthorities()` 不得 null);starter 用 `-boot-4-` / `spring-boot4-`;Quartz 用 starter;不引 fastjson2 |
| 架构方案 §2 | 数据库 `basicdb` / **34 表零变更**;前端两端零改动;`com.fasterxml.jackson.*` **仅允许 `annotation`** |
| 需求文档 §7 | 列表 1000 条 < 1s;登录 < 500ms;**JDK 21 下 0 ERROR 可编译可启动**;JSON 结构与 SB3 一致 |
| 需求文档 §10 | AC-01~AC-42(其中 **AC-25~AC-42 为 SB4 专项 18 项**) |

**勿重复大段正文**,仅作引子。

---

## 2. 逻辑架构与模块划分

### 2.1 工程结构(结构与 `springboot3` 一致;🔶 依赖/实现升级)

```
base-postgresql-vue3/
├── pom.xml                 # 父 pom,7 子模块
│                            🔶 java.version=21 / spring-boot.version=4.1.1
│                            🔶 springdoc 3.1.1 / mybatis-spring-boot 4.1.0
├── basic-admin-api/        # 启动入口 + 26 Controller
│   └── .../config/SwaggerConfig     🔶 适配 springdoc 3.1
├── basic-admin-core/       # 业务核心
│   ├── .../domain/vo/RouterVo       🔶 annotation import 保持
│   ├── .../domain/SysLogininfor     🔶 同上
│   └── .../domain/SysOperLog        🔶 同上
├── basic-framework/        # 框架能力(改动最集中)
│   ├── .../config/BigNumberModuleConfig.java   🔶 新增(独立 Customizer)
│   ├── .../config/ResourcesConfig.java         🔶 精简 + 拆出 Customizer
│   ├── .../config/RedisConfig.java             🔶 Jackson 3 + defaultTyping
│   ├── .../config/JacksonConfig.java           🔶 Jackson 3
│   ├── .../config/DruidConfig.java             🔶 boot4.autoconfigure 包名
│   ├── .../config/SecurityConfig.java          🔶 Security 7
│   ├── .../config/PermitAllUrlProperties.java  🔶 微调
│   ├── .../jackson/BigNumberSerializer.java    🔶 重写为 ValueSerializer<Number>
│   ├── .../web/service/TokenService.java       🔶 Jackson 3
│   ├── .../manager/factory/AsyncFactory.java   ⬆️
│   └── .../web/service/SysLoginService.java    ⬆️
├── basic-common/           # 通用
│   ├── .../utils/JsonUtils.java                🔶 Jackson 3
│   ├── .../core/redis/RedisCache.java          🔶 脏数据容错
│   ├── .../core/domain/model/LoginUser.java    🔶 getAuthorities() 非 null
│   ├── .../core/domain/BaseEntity.java         ⬆️ annotation
│   └── .../core/domain/{YjyBaseEntity,TreeSelect}  ⬆️ annotation
├── business-core/          # 业务扩展点                                    ⬆️
├── extend-quartz/          # 定时任务
│   ├── pom.xml                                 🔶 spring-boot-starter-quartz
│   ├── .../config/ScheduleConfig.java          🔶 适配 starter
│   └── .../domain/SysJob.java                  ⬆️ annotation
├── extend-generator/       # 代码生成
│   └── .../util/VelocityUtils.java             🔶 Jackson 3 适配
├── file-sql/               # 8 个 SQL(🔶 零变更)
├── web-ui/                 # PC(JS)🔶 零改动
└── web-mp/                 # 移动(JS)🔶 零改动
```

### 2.2 组件职责(🔶 标出本分支变化)

| 模块 | 关键类 | 🔶 本分支变化 |
|---|---|---|
| `basic-admin-api` | `BasicApplication` | 微调(Jackson 3 / 自动装配) |
| | `SwaggerConfig` | 适配 springdoc **3.1.1**(OpenAPI 3.1) |
| | `CaptchaController`(kaptcha) | ⬆️ 无变化 |
| `basic-admin-core` | `Sys*ServiceImpl` / `Sys*Mapper` | ⬆️ 业务逻辑不变 |
| | 实体(`SysLogininfor` / `SysOperLog` / `RouterVo` 等) | annotation import 保持 `com.fasterxml.jackson.annotation` |
| `basic-framework` | **`BigNumberModuleConfig`** | **🔶 新增**:`JsonMapperBuilderCustomizer`,`builder.addModule(...)`;独立类防循环依赖 |
| | **`BigNumberSerializer`** | **🔶 重写**:`extends ValueSerializer<Number>`,`serialize(Number, JsonGenerator, SerializationContext)` |
| | **`RedisConfig`** | **🔶 改造**:`JsonMapper.builder()` + `defaultTyping` + `BasicPolymorphicTypeValidator` 白名单 |
| | `JacksonConfig` | **🔶 改造**:Jackson 3 配置 |
| | `ResourcesConfig` | **🔶 精简**:删 `Jackson2ObjectMapperBuilderCustomizer`;拆出 Customizer |
| | `DruidConfig` | **🔶 包名**:`com.alibaba.druid.spring.boot4.autoconfigure` |
| | `SecurityConfig` | **🔶 Security 7**:`SecurityFilterChain` + `@EnableMethodSecurity` + `requestMatchers` |
| | `PermitAllUrlProperties` | 微调 |
| | `TokenService` | **🔶 Jackson 3** 序列化 LoginUser 入 Redis |
| | `JwtAuthenticationTokenFilter` | ⬆️ jakarta |
| `basic-common` | **`JsonUtils`** | **🔶 Jackson 3**:`JacksonException` / `TypeReference` / `ObjectMapper` |
| | **`RedisCache`** | **🔶 脏数据容错**:反序列化失败当未命中 |
| | **`LoginUser`** | **🔶 `getAuthorities()` 返回空集合**(Security 7) |
| | `BaseEntity` / `YjyBaseEntity` / `TreeSelect` | annotation import 保持 |
| `extend-quartz` | `ScheduleConfig` / `ScheduleUtils` / `AbstractQuartzJob` | **🔶 改用 `spring-boot-starter-quartz`** 自动装配;RAMJobStore |
| `extend-generator` | `VelocityUtils` | **🔶 Jackson 3 适配**;⚠️ 模板仍产 SB2 风格代码(R34) |
| `business-core` | `DemoController` | ⬆️ 不变 |

### 2.3 扩展 SPI(⬆️ 继承 + 🔶 Jackson 3 相关)

| SPI | 接入方式 | 用例 |
|---|---|---|
| ⬆️ 自定义 Job | `@Component("ryTask")` + `public void ryXxx(...)` | `ryTask.ryNoParams` |
| ⬆️ 自定义权限注解 | `@PreAuthorize("@ss.hasPermi('...')")` | — |
| ⬆️ 字典加载 | `DictUtils` 增枚举/缓存 key | — |
| ⬆️ 数据源 | `@DataSource(DataSourceType.SLAVE)` | 多数据源 |
| ⬆️ 代码生成模板 | `vm/java/...` | — |
| **🔶 自定义 JSON 序列化** | 实现 `JsonMapperBuilderCustomizer`,**独立 `@Configuration`** 内 `builder.addModule(new SimpleModule().addSerializer(...))` | 大数 / 枚举 / 日期定制 |
| **🔶 自定义 JacksonModule** | `extends JacksonModule`(非 `databind.Module`) | 复杂定制 |

### 2.4 🔶 升级后的关键结构对照(本分支核心)

#### 2.4.1 Jackson 2 → 3 全量映射表

| 概念 | Jackson 2(`springboot3`) | **Jackson 3(本分支)** | 备注 |
|---|---|---|---|
| 核心包 | `com.fasterxml.jackson.core.*` | **`tools.jackson.core.*`** | `JsonGenerator` / `TypeReference` / `JacksonException` |
| 数据绑定 | `com.fasterxml.jackson.databind.*` | **`tools.jackson.databind.*`** | `ObjectMapper` / `JsonMapper` / `JacksonModule` |
| **注解包** | `com.fasterxml.jackson.annotation.*` | **不变** | `@JsonFormat` / `@JsonIgnore` / `@JsonIgnoreProperties` / `@JsonInclude` |
| 异常 | `JsonProcessingException extends IOException` | **`JacksonException extends RuntimeException`** | **`catch (IOException)` 抓不到** |
| 序列化器基类 | `JsonSerializer<T>` / `StdSerializer<T>` | **`ValueSerializer<T>`** / `StdSerializer<T>` | — |
| 序列化上下文 | `SerializerProvider` | **`SerializationContext`** | 方法签名变化 |
| 模块基类 | `databind.Module` | **`JacksonModule`** | — |
| `SimpleModule` | `databind.module.SimpleModule` | **`tools.jackson.databind.module.SimpleModule`** | — |
| Module 注册 | 运行时 `mapper.registerModule(m)` | **构建时 `JsonMapper.builder().addModule(m)`** | **运行时 API 已删** |
| defaultTyping | `mapper.activateDefaultTyping(...)` | **`builder.activateDefaultTyping(...)`** | 位置从实例移到 builder |
| `NumberSerializer` | `ser.std.NumberSerializer` | **不存在** | 自写 `ValueSerializer<Number>` |
| HTTP 转换器 | `MappingJackson2HttpMessageConverter` | **`JacksonJsonHttpMessageConverter`** | — |
| SB 定制钩子 | `Jackson2ObjectMapperBuilderCustomizer` | **`JsonMapperBuilderCustomizer`** | — |
| `@JsonComponent` | 支持 | **支持**(Jackson 3 仍保留) | — |

**本分支实测落地**:

| 项 | 实测 |
|---|---|
| `import tools.jackson` | **15 处 / 4 个文件** |
| `import com.fasterxml.jackson` | **13 处 / 11 个文件**,全部是 `annotation.*` 的 4 类唯一 import |
| `registerModule` | **0 处** |
| `Jackson2ObjectMapperBuilderCustomizer` | **0 处** |
| `NumberSerializer` | **0 处** |

#### 2.4.2 Jackson 3 的 Module 注册机制(架构级变化)

```
【Jackson 2 / SB 3】
@Configuration
public class JacksonConfig {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> builder.modules(new BigNumberModule());   // 运行时/构建期均可
    }
}
// 或直接:
objectMapper.registerModule(new BigNumberModule());                 // 运行时注册(已删)

【Jackson 3 / SB 4 — 本分支】
@Configuration
public class BigNumberModuleConfig {                                 ← 必须独立类(防循环依赖)
    @Bean
    public JsonMapperBuilderCustomizer bigNumberCustomizer() {
        return builder -> builder.addModule(bigNumberModule());      // 构建时注册(唯一方式)
    }

    private JacksonModule bigNumberModule() {                        ← 注意基类是 JacksonModule
        SimpleModule module = new SimpleModule("bigNumber");
        module.addSerializer(Long.class, BigNumberSerializer.INSTANCE);
        module.addSerializer(Long.TYPE, BigNumberSerializer.INSTANCE);
        return module;
    }
}
```

**为什么必须独立类**:SB 4 的 `JsonMapperBuilderCustomizer` 会在构建 `JsonMapper` Bean 时被调用;若它位于 `ResourcesConfig`,而 `ResourcesConfig` 又依赖 `RepeatSubmitInterceptor` → `RedisCache` → `RedisTemplate` → `JsonMapper`,就形成:

```
ResourcesConfig
   │ 需要
   ▼
RepeatSubmitInterceptor
   │ 需要
   ▼
RedisCache
   │ 需要
   ▼
RedisTemplate
   │ 需要
   ▼
JsonMapper  ←── 需要 Customizer ←── 又回到 ResourcesConfig ❌ 循环依赖
```

拆成独立 `BigNumberModuleConfig`(无业务依赖)后环被打断。

#### 2.4.3 Security 6 → 7 结构对照

| 项 | Security 6(`springboot3`) | **Security 7(本分支)** |
|---|---|---|
| 配置方式 | `SecurityFilterChain` Bean | **同**(不变) |
| 授权入口 | `authorizeHttpRequests` | **同** |
| 路径匹配 | `requestMatchers` | **同** |
| 方法级安全 | `@EnableMethodSecurity` | **同** |
| **`getAuthorities()`** | 可返回 `null`(有隐患) | **不得返回 `null`** ← 唯一硬约束 |
| CSRF / Session | Lambda DSL | 同 |
| 白名单 | 同 SB3 | 同 SB3 |

> **唯一必须改的业务代码**:`LoginUser.getAuthorities()`。

```java
// ✅ 本分支实现(无权限返回空集合)
@Override
public Collection<? extends GrantedAuthority> getAuthorities() {
    if (permissions == null || permissions.isEmpty()) {
        return Collections.emptyList();      // 🔶 绝不返回 null
    }
    return permissions.stream()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
}
```

#### 2.4.4 starter 变体矩阵

| 能力 | SB 3(`springboot3`) | **SB 4(本分支)** | 若用错的后果 |
|---|---|---|---|
| 连接池 | `druid-spring-boot-3-starter` | **`druid-spring-boot-4-starter`** | 数据源 Bean 不注册 → 启动失败 |
| ORM | `mybatis-plus-spring-boot3-starter` 3.5.7 | **`mybatis-plus-spring-boot4-starter` 3.5.17** | Mapper 不扫描 |
| MyBatis 基础 | `mybatis-spring-boot-starter` 3.0.5 | **4.1.0** | 与 MP 不匹配 |
| 分页 | `pagehelper-spring-boot-starter` 2.1.1 | **4.1.1** | 分页失效 |
| **Quartz** | 手写 `org.quartz-scheduler:quartz` | **`spring-boot-starter-quartz`** | 与 SB4 自动装配冲突 |
| Druid 包名 | `com.alibaba.druid.spring.boot3.autoconfigure` | **`...spring.boot4.autoconfigure`** | `ClassNotFoundException` |

#### 2.4.5 Redis 序列化结构

```
【springboot3】
RedisTemplate
   └─ Jackson2JsonRedisSerializer(valueSerializer: ObjectMapper / Jackson 2)
        · mapper.activateDefaultTyping(LaissezFaireSubTypeValidator, NON_FINAL, As.PROPERTY)
        · ⚠️ LaissezFaire 无白名单 → 反序列化 gadget 风险

【本分支 / Jackson 3】
RedisTemplate
   └─ Jackson 3 序列化(valueSerializer: JsonMapper)
        · JsonMapper.builder()
              .activateDefaultTyping(                    ← 在 builder 上
                  BasicPolymorphicTypeValidator.builder()  ← 白名单(必需)
                      .allowIfSubType("com.basic.")
                      .allowIfSubType("com.core.")
                      .build(),
                  DefaultTyping.NON_FINAL,
                  JsonTypeInfo.As.PROPERTY)
              .build()
        · ✅ 只允许业务包;不可用 LaissezFaire
```

**升级期脏数据**:旧 Jackson 2 写入的缓存对象,Jackson 3 反序列化会抛 `JacksonException`;`RedisCache` 已 catch 并当"未命中"处理(不阻断业务),但**建议升级时清空 Redis**。

#### 2.4.6 Quartz 装配对照

```
【springboot3】手工依赖 + 手工装配
extend-quartz/pom.xml:  org.quartz-scheduler:quartz:2.5.2
ScheduleConfig:         @Bean SchedulerFactoryBean(手工配置 DataSource/JobStore)

【本分支】starter 自动装配
extend-quartz/pom.xml:  spring-boot-starter-quartz(版本由 SB4 BOM 管理)
ScheduleConfig:         仅做业务侧定制(如有);SchedulerFactoryBean 由 starter 提供
JobStore:               RAMJobStore(内存)
副作用:                 qrtz_* 表存在但**不被使用**;重启丢任务
```

#### 2.4.7 请求链路对照(前后)

```
【springboot3】
HTTP → CorsFilter → XssFilter → JwtFilter(jakarta)
     → Security 6 链 → DispatcherServlet → Controller
     → Jackson 2 HttpMessageConverter(MappingJackson2HttpMessageConverter)
     → JSON(Jackson 2)

【本分支】
HTTP → CorsFilter → XssFilter → JwtFilter(jakarta)
     → Security 7 链(getAuthorities() 空集合兜底) → DispatcherServlet → Controller
     → Jackson 3 HttpMessageConverter(JacksonJsonHttpMessageConverter)
     → JSON(Jackson 3,结构与 Jackson 2 输出**保持一致**)
```

---

## 3. 核心流程设计

### 3.1 登录流程(⬆️ 与 `springboot3` 一致;🔶 内部序列化换 Jackson 3)

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

**时序(ASCII,🔶 为 Jackson 3 差异点)**

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
   │                       │         │     🔶 JsonMapper(Jackson 3)
   │                       │         │        序列化 LoginUser → Redis
   │                       │         │        (defaultTyping + 白名单)
   │                       │         │  ⑥ AsyncFactory(yauaa)
   │ ◀──────────────── {token}        │  写 sys_logininfor
   │                                 │
   │  GET /getInfo                   │
   │ ────────────────▶               │  🔶 LoginUser.getAuthorities()
   │                                  │     无权限 → Collections.emptyList()
```

### 3.2 锁屏/解锁(⬆️ 一致)

```
PC store/modules/lock.js 触发 lock 状态 → 锁屏页
   │
   ▼ 提交 unlockPassword
   │  POST /login(复用 token,只校验密码)
   ▼
SysLoginService.login: 短路验证码检查;校验密码;
   成功 → 返回原 token;失败 → UserPasswordNotMatchException
```

### 3.3 定时任务调度(🔶 改 starter + RAMJobStore)

```
新增任务:
  SysJobController.add → SysJobServiceImpl.add
    → ScheduleUtils.createScheduleJob → 🔶 SchedulerFactoryBean(starter 自动装配)
      → RAMJobStore(内存,**不写 qrtz_***)
      → 业务定义仍写 sys_job

触发:
  Cron 到点 → AbstractQuartzJob.execute
    → JobInvokeUtil.invokeMethod(ryTask.ryXxx)
      → try/catch → 🔶 sys_job_log 照常落库

重启后:
  ⚠️ RAMJobStore 清空 → 动态任务丢失(需重新启用)
  ✅ 但 sys_job 定义还在 → 可在管理页重新 changeStatus 恢复
```

**异常分支**:Job 异常被 `AbstractQuartzJob` 捕获,Scheduler 不挂;`misfirePolicy` 仍生效。

### 3.4 代码生成(⬆️ 一致;⚠️ 模板未适配)

```
导入表 → GenTableServiceImpl.importGenTable
  → GenTableColumnServiceImpl.importGenTableCol
  → 写 gen_table / gen_table_column

生成:
  download(genTableIds) →
    VelocityEngine 渲染 .vm 模板(🔶 VelocityUtils 已适配 Jackson 3)
    → 打包 zip → 浏览器下载
```

> ⚠️ **R34**:模板产出仍是 SB 2 风格(`javax.*` + springfox),生成后需手工改 `jakarta.*` + `io.swagger.v3`。

### 3.5 移动端登录(⬆️ 一致,前端零改动)

```
web-mp/pages/login/index.vue(JS,未改)
  GET /captchaImage
  POST /login(RSA + kaptcha)
  ↓  Basic-Token 存 uni.storage
GET /getInfo
GET /getRouters
```

### 3.6 🔶 SB4 升级改造流程(已执行,记录备查)

#### 3.6.1 执行顺序(依赖先行 → Jackson 3 → 业务适配)

```
步骤 1:父 pom.xml
        - java.version 17 → 21
        - spring-boot.version 3.5.16 → 4.1.1
        - springdoc 2.9.0 → 3.1.1
        - mybatis-spring-boot 3.0.5 → 4.1.0
        - druid 换 druid-spring-boot-4-starter
        - MP 换 mybatis-plus-spring-boot4-starter(3.5.17)
        - pagehelper 4.1.1
        - 移除手写 quartz 依赖
        ↓
步骤 2:7 个子模块 pom.xml
        - basic-framework:Druid boot4 starter;spring-boot-starter-quartz
        - basic-common / basic-admin-core / extend-*:版本对齐
        - extend-quartz:移除 quartz:2.5.2,加 spring-boot-starter-quartz
        ↓
步骤 3:mvn dependency:resolve(JDK 21)验证可解析
        ↓
步骤 4:Jackson 3 核心迁移(4 个文件)
        - JsonUtils          (JacksonException / TypeReference / ObjectMapper)
        - BigNumberSerializer(ValueSerializer / SerializationContext / JacksonStdImpl)
        - BigNumberModuleConfig(新增:JacksonModule / SimpleModule / JsonMapper)
        - RedisConfig        (JsonMapper / DefaultTyping / BasicPolymorphicTypeValidator)
        ↓
步骤 5:Jackson 3 配套改造
        - ResourcesConfig     删 Jackson2ObjectMapperBuilderCustomizer,拆出 Customizer
        - JacksonConfig       Jackson 3 化
        - TokenService        序列化走 JsonMapper
        - VelocityUtils       Jackson 3 适配
        ↓
步骤 6:Security 7 适配
        - LoginUser.getAuthorities() → 返回空集合
        - SecurityConfig 确认 @EnableMethodSecurity + requestMatchers
        ↓
步骤 7:Druid / Quartz 适配
        - DruidConfig 包名 → boot4.autoconfigure
        - ScheduleConfig 适配 starter 自动装配
        ↓
步骤 8:Redis 幂等性
        - RedisCache 加反序列化容错(失败当未命中)
        - 升级时清空 Redis(避免 Jackson 2 脏数据)
        ↓
步骤 9:mvn clean compile(JDK 21)→ 0 ERROR
        ↓
步骤 10:启动 + 全链路回归(S11 场景)
```

#### 3.6.2 升级验证链

```
① 编译通过 ──▶ grep com.fasterxml.jackson 仅 annotation
                grep registerModule = 0
                grep NumberSerializer = 0
② 依赖正确 ──▶ mvn dependency:tree 无 SB3 starter 变体
                无 fastjson
③ 启动成功 ──▶ 无循环依赖(No circular dependency)
                无 getAuthorities() NPE
④ 安全链正常 ─▶ 无权限用户 → 403(非 500)
⑤ JSON 正常 ──▶ 响应结构同 SB3;大数为字符串
⑥ 缓存正常 ──▶ Redis 写读 OK;清空后不报错
⑦ 定时任务 ──▶ 新建/启动/执行/日志 OK
⑧ 业务回归 ──▶ 登录/CRUD/锁屏/代码生成 全通过
```

#### 3.6.3 常见踩坑与处置(实测)

| 症状 | 根因 | 处置 |
|---|---|---|
| `cannot find symbol: com.fasterxml.jackson.databind.ObjectMapper` | Jackson 3 无该类 | 改 `tools.jackson.databind.ObjectMapper` |
| `ObjectMapper.registerModule` 编译不过 | Jackson 3 运行时注册 API 已删 | 改 `JsonMapper.builder().addModule()` / `JsonMapperBuilderCustomizer` |
| 启动报 `circular dependency` | Customizer 与 `RedisTemplate` 同链路 | Customizer 拆独立配置类 |
| `cannot find symbol: NumberSerializer` | Jackson 3 删除该类 | 自写 `ValueSerializer<Number>` |
| `catch (IOException)` 抓不到 JSON 异常 | `JacksonException` 是 `RuntimeException` | 改 `catch (JacksonException)` |
| 启动/运行 NPE,栈在 `getAuthorities()` | Security 7 不允许返回 null | 返回 `Collections.emptyList()` |
| `ClassNotFoundException: ...spring.boot3.autoconfigure...` | Druid 包名未改 | 改 `boot4.autoconfigure` |
| 启动报 "Failed to configure a DataSource" | 用了 SB3 的 Druid starter | 换 `-boot-4-starter` |
| Redis 反序列化报错 | 旧 Jackson 2 脏数据 | 清空 Redis;或依赖 `RedisCache` 容错 |
| 分页总数不对 | pagehelper 2.1 → 4.1 行为差异 | 回归分页;必要时显式配方言 |
| 定时任务重启后消失 | RAMJobStore | 已知取舍;需持久化切 JDBC JobStore |

---

## 4. 数据设计(概要,⬆️ 与 `springboot3` 完全一致)

| schema | 表数 | 内容 |
|---|---|---|
| `basicdb` | **34** | `gen_*` 2 + `qrtz_*` 11 + `sys_*` 21 |
| `public` | 0 | 不使用 |

> **🔶 本分支数据库零变更**;**🔶 无新增存储**(Redis 只是序列化方式从 Jackson 2 换 3)。

**运行期非 DB 存储(⬆️ 一致,🔶 序列化方式变)**:

| 存储 | Key | TTL | 🔶 本分支 |
|---|---|---|---|
| Redis | `login_tokens:<uuid>` | 30min | **Jackson 3 序列化** |
| Redis | `sys_dict::<dictType>` | — | 同上 |
| Redis | `REPEAT_SUBMIT:*` | 500ms | 同上 |
| 本地磁盘 | `D:/ruoyi/uploadPath` | — | 不变 |

> ⚠️ **`qrtz_*` 表**:存在但 **RAMJobStore 不使用**(见 §2.4.6)。

---

## 5. 接口清单(⬆️ 与 `springboot3` 完全一致)

> **🔶 本分支 HTTP 接口零变更**:路径、方法、入参、出参、权限字符全部与 `springboot3` 一致。
>
> 完整清单见 `docs/springboot3-概要设计.md` §5,此处仅保留分类索引:
>
> - **匿名**:`GET /captchaImage` / `POST /login` / `POST /register` / `POST /monitor/shutdown`
> - **已登录**:`POST /logout` / `GET /getInfo` / `GET /getRouters` / `GET /system/dict/type/{dictType}` / `GET /common/download` / `POST /common/upload`
> - **系统管理**:`/system/{user,role,menu,dept,post,config,notice,dict/*}`
> - **监控**:`/monitor/{operlog,logininfor,online,job,jobLog,server,cache,druid}`
> - **工具**:`/tool/gen/{list,importTable,download,synchDb,preview}`
> - **文档**:`GET /swagger-ui/index.html` / `GET /v3/api-docs`(springdoc **3.1.1**)

### 5.1 🔶 JSON 响应兼容性要求

| 项 | 要求 |
|---|---|
| 字段名 | **完全不变**(Jackson 3 默认命名策略同 2.x) |
| 日期格式 | 不变(`@JsonFormat` 注解包未变,行为一致) |
| `null` 处理 | 不变(`@JsonInclude` 行为一致) |
| **大数** | 超出 JS 安全整数范围的 `Long` 输出**字符串**(与 SB3 一致) |
| 枚举 | 不变 |
| 循环引用 | 不变(`@JsonIgnoreProperties` 生效) |

> **结论**:前端零改动可对接(AC-40)。

---

## 6. 关键技术决策(HD-)

> **HD-01~HD-16 继承自 `springboot3`**(STATELESS JWT / AsyncManager / MP+XML 双轨 / 数据权限切面 / 限流 / Token+Redis / uni-app 组件 / pages.json / 一次升到 3.5.16 / Jakarta 硬切 / springdoc / SecurityFilterChain / starter 变体 / yauaa / jaxb / DB+前端零变更),详见 `docs/springboot3-概要设计.md` §6。下表为本分支**新增 HD-17~HD-28**。

| # | 决策 | 理由 | 影响 |
|---|---|---|---|
| **HD-17** | **一次升到 SB 4.1.1 + JDK 21** | 4.0 是首个 GA,4.1 已稳定;分两步徒增回归成本 | JDK 21 强制;不能停在 4.0 |
| **HD-18** | **Jackson 3 全量迁移,保留 `com.fasterxml.jackson.annotation`** | Jackson 3 只在核心/绑定换包,**注解包故意不变**;强行改会编译失败 | 全仓仅 4 类注解 import 留在旧包(官方设计) |
| **HD-19** | **Module 只能构建时注册** | Jackson 3 删除了运行时 `registerModule`;build-then-immutable 是其核心设计 | 所有定制走 builder / Customizer |
| **HD-20** | **Customizer 放独立配置类** | 否则与 `RedisTemplate → JsonMapper` 形成循环依赖,启动失败 | 新增 `BigNumberModuleConfig` |
| **HD-21** | **`BigNumberSerializer` 自写 `ValueSerializer<Number>`** | Jackson 3 删除 `NumberSerializer`;自写可控且不丢 JS 安全整数语义 | 大数输出字符串 |
| **HD-22** | **Redis 用 Jackson 3 + defaultTyping + 白名单** | 需要多态存 `LoginUser`;白名单防 gadget | 不用 `LaissezFaire`(SB3 的隐患) |
| **HD-23** | **`getAuthorities()` 返回空集合** | Security 7 硬约束;返回 null 会 NPE | 唯一必须改的业务代码 |
| **HD-24** | **Quartz 改用 `spring-boot-starter-quartz` + RAMJobStore** | starter 自动装配省手工;RAMJobStore 避开 PG `varchar(1)` 坑 | 任务不持久化(已知取舍) |
| **HD-25** | **不引 fastjson2** | 避免双序列化栈;Jackson 3 + defaultTyping 已够 | 若业务复杂再评估(A-20) |
| **HD-26** | **`RedisCache` 加反序列化容错** | 升级期旧 Jackson 2 脏数据不应阻断业务 | 反序列化失败当未命中 |
| **HD-27** | **`ResourcesConfig` 精简** | SB4 删了 `Jackson2ObjectMapperBuilderCustomizer`;职责拆到独立类更清晰 | 配置类数量 +1 |
| **HD-28** | **前端不做 TS、DB 不改** | SB4 升级是应用层变化;TS 化与 SB4 正交 | 回滚只换 jar |

---

## 7. 部署视图(⬆️ 与 `springboot3` 一致;🔶 运行时 JDK 21)

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
        │  🔶 运行时:JDK 21              │
        │  🔶 Jackson 3.1.5              │
        │  🔶 Security 7.1.1             │
        │  端口:8080 / profile: dev|prod │
        │  🔶 Quartz RAMJobStore(内存)   │
        └──────┬──────────────┬──────────┘
               │              │
               ▼              ▼
        ┌────────────┐   ┌──────────────────────────┐
        │PostgreSQL  │   │  Redis(可选)             │
        │15+(basicdb)│   │  🔶 Jackson 3 序列化      │
        │34 表       │   │     + defaultTyping      │
        │(qrtz_* 不用)│   │     + 白名单              │
        └────────────┘   └──────────────────────────┘

        web-mp:  🔶 与 springboot3 完全一致(JS,零改动)
        ┌────────────────────────────┐
        │  HBuilder / 微信开发者工具  │
        │  编译产物:dist/dev/mp-weixin│
        └────────────────────────────┘
```

### 7.1 启动顺序

1. **JDK 21** + IDE 设为 21
2. PostgreSQL 启动 + `psql -f file-sql/basicdb-vue3.sql`(34 表)
3. **🔶 建议清空 Redis**(避免 Jackson 2 脏数据)
4. Redis 启动
5. `mvn clean compile` → 0 ERROR
6. `mvn spring-boot:run -pl basic-admin-api` 启动
7. 访问 `/swagger-ui/index.html`(springdoc 3.1);`/druid/` 监控
8. `pnpm dev` / `pnpm dev:h5` 启动前端(零改动)
9. `admin / admin123` 登录

### 7.2 并发与恢复

- Tomcat `max-threads=800`
- 优雅停机:`actuator/shutdown`
- Druid `min-idle=5` / `max-active=20`
- **🔶 定时任务**:RAMJobStore,重启后动态任务丢失(需在管理页重新启用;`sys_job` 定义仍在)
- **🔶 Redis**:Jackson 3 序列化;重启后缓存可重建

### 7.3 🔶 回滚策略

| 场景 | 回滚动作 | 数据影响 |
|---|---|---|
| SB4 升级后出现阻塞缺陷 | 回退到 `springboot3` 的 jar(**DB 无需回滚**) | **无**(库结构未变) |
| Redis 反序列化异常 | 清空 Redis(缓存可重建) | 仅丢缓存 |
| 定时任务丢失 | 管理页重新启用;或回退 jar | 无 |
| 前端异常 | **不可能是前端问题**(零改动) | 无 |

> **优势**:数据库与前端都没动,回滚成本极低(与 SB3 升级同理)。

---

## 8. 风险与待定项

> **P1~P15 继承自 `springboot3`**(日志大表 / refresh token / uni-app alpha / 双包前缀 / 国际化 / javax 残留 / PathPatternParser / 配置键 / 生成器模板未适配 / deprecation 等),详见 `docs/springboot3-概要设计.md` §8。下表为**本分支新增 P16~P30**。

| 编号 | 项 | 影响 | 归属文档 |
|---|---|---|---|
| **P16** | **Jackson 2/3 混用残留 → 编译失败** | **高** | 架构 R21;AC-28/AC-29 |
| **P17** | **运行时注册 Module 的旧写法已失效** | **高** | 架构 R22;HD-19;AC-30 |
| **P18** | **`JsonMapperBuilderCustomizer` 循环依赖** | **高** | 架构 R23;HD-20;§2.4.2 |
| **P19** | **Jackson 3 异常是 `RuntimeException`,`catch IOException` 失效** | 中 | 架构 R24 |
| **P20** | **`NumberSerializer` 已删** | 中 | 架构 R25;HD-21;AC-32 |
| **P21** | **Security 7 `getAuthorities()` 返回 null → NPE** | **高** | 架构 R26;HD-23;AC-35 |
| **P22** | **Quartz RAMJobStore,任务不持久化** | **中** | 架构 R27;HD-24;A-14;AC-36 |
| **P23** | **Redis 升级期脏数据** | **中** | 架构 R28;HD-26;A-15;AC-37 |
| **P24** | **Redis defaultTyping 多态反序列化安全** | **中** | 架构 R29;HD-22;§2.4.5 |
| **P25** | **springdoc 3.1 兼容性未全量验证** | 中 | 架构 R30;A-17;AC-39 |
| **P26** | **MP 3.5.17 / pagehelper 4.1 跨版本中间变更未核对** | 中 | 架构 R31;A-18 |
| **P27** | **`WebMvcConfigurer` 在 Framework 7 已废弃** | 低 | 架构 R32;A-16 |
| **P28** | **Druid 监控生产暴露** | **中** | 架构 R33;A-21 |
| **P29** | **代码生成器模板仍产 SB2 代码** | 中 | 架构 R34;A-19 |
| **P30** | **fastjson2 例外条件未界定** | 低 | A-20 |

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `springboot3` 概要设计(v0.1)为基线(HD-01~16 / P1~15 继承),叠加 SB4 升级专题:§2.4 升级后结构对照(7 张对照:Jackson 映射表 14 行 + 实测落地 / Module 注册机制 + 循环依赖链路图 / Security 6→7 / starter 矩阵 / Redis 序列化结构 / Quartz 装配 / 请求链路)、§3.6 改造流程(10 步 + 8 项验证链 + 11 类踩坑)、§5.1 JSON 兼容性要求、HD-17~HD-28、§7.3 回滚策略、P16~P30 |
