# 基础平台(base-postgresql-vue3) 详细设计(LLD) — `springboot4` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot4`** 分支
> 前置依赖:《架构方案》v0.1、《需求文档》v0.1、《概要设计》v0.1
> 本文档细化到「类 / 方法 / 关键逻辑」。
> **业务逻辑与 `springboot3` 完全一致**(包结构骨架 / 实体 / 枚举 / 业务 Service / 校验 / 审计 / 错误码 / 并发模型 / TC-01~TC-32 全部继承,本文档不重复);**本分支新增 §1.2 升级文件台账、§3.10 升级机制(9 小节)、§4.6 Jackson 3 引擎详解、§9.2 升级期排障表、§11.3 TC-63~TC-80**。
> 🔶 = 本分支改动;⬆️ = `springboot3` 继承。

---

## 1. 总体包结构

### 1.1 后端包结构(⬆️ 骨架与 `springboot3` 一致;🔶 标出改动文件)

```
com.basic
├── BasicApplication                        🔶 import 改(SB4 拆 spring-boot-jdbc)
├── BasicServletInitializer                 ⬆️
├── web
│   ├── config/SwaggerConfig                🔶 适配 springdoc 3.1
│   └── controller
│       ├── common/{CaptchaController, CommonController}          ⬆️
│       ├── monitor/{Cache,Server,SysLogininfor,SysOperlog,SysUserOnline}Controller  ⬆️
│       ├── system/{SysConfig,SysDept,SysDictData,SysDictType,SysIndex,SysLogin,SysMenu,SysNotice,SysPost,SysProfile,SysRegister,SysRole,SysUser}Controller  ⬆️
│       └── tool/{Swagger,Test}Controller                         ⬆️
├── framework
│   ├── aspectj/{DataScopeAspect, DataSourceAspect, LogAspect, RateLimiterAspect}   ⬆️
│   ├── config
│   │   ├── ApplicationConfig               🔶 删 Jackson2ObjectMapperBuilderCustomizer 整段
│   │   ├── BigNumberModuleConfig           🔶 新增(独立 Customizer)
│   │   ├── DruidConfig                     🔶 boot4.autoconfigure 包名
│   │   ├── FilterConfig                    ⬆️
│   │   ├── JacksonConfig                   🔶 ❌ **整个文件删除**(逻辑迁 BigNumberModuleConfig + RedisConfig)
│   │   ├── MyBatisConfig / MybatisPlusConfig 🔶 MP 3.5.17 + jsqlparser 拆包
│   │   ├── RedisConfig                     🔶 GenericJacksonJsonRedisSerializer + defaultTyping + 白名单
│   │   ├── ResourcesConfig                 🔶 精简(删 Jackson Customizer)
│   │   ├── SecurityConfig                  🔶 Security 7
│   │   ├── ServerConfig / ThreadPoolConfig ⬆️
│   │   ├── UserAgentConfig(yauaa)          ⬆️
│   │   └── properties/*
│   │       └── PermitAllUrlProperties      🔶 getPatternValues()
│   ├── datasource/{DynamicDataSource, DynamicDataSourceContextHolder}   ⬆️
│   ├── interceptor/{RepeatSubmitInterceptor, impl/SameUrlDataInterceptor}  🔶 微调(避免循环依赖)
│   ├── jackson/BigNumberSerializer         🔶 重写为 ValueSerializer<Number>
│   ├── manager/{AsyncManager, ShutdownManager, factory/AsyncFactory}   ⬆️
│   ├── security
│   │   ├── filter/JwtAuthenticationTokenFilter                    ⬆️
│   │   └── handle/{AuthenticationEntryPointImpl, LogoutSuccessHandlerImpl, MyMetaObjectHandler}  ⬆️
│   └── web
│       ├── domain/{Server, server/*}                               ⬆️
│       ├── exception/GlobalExceptionHandler                        ⬆️
│       └── service
│           ├── PermissionService / SysPermissionService / SysRegisterService  ⬆️
│           ├── SysLoginService                                     ⬆️
│           ├── TokenService                                        🔶 Jackson 3 + 主动删脏 key
│           └── UserDetailsServiceImpl                              ⬆️
├── common
│   ├── annotation/*                                                ⬆️
│   ├── config/BasicConfig                                          ⬆️
│   ├── constant/*                                                  ⬆️
│   ├── core
│   │   ├── controller/BaseController                               ⬆️
│   │   ├── domain
│   │   │   ├── {AjaxResult, R, TableDataInfo, TreeEntity}           ⬆️
│   │   │   ├── BaseEntity                                          ⬆️(annotation 保持)
│   │   │   ├── YjyBaseEntity / TreeSelect                          ⬆️(annotation 保持)
│   │   │   ├── entity/{SysUser, SysRole}                           ⬆️(annotation 保持)
│   │   │   └── model
│   │   │       ├── LoginUser                                       🔶 getAuthorities() 返回空集合
│   │   │       ├── LoginBody / RegisterBody                        ⬆️
│   │   ├── page/*                                                  ⬆️
│   │   ├── redis/RedisCache                                        🔶 反序列化容错 + 自动删 key
│   │   └── text/*                                                  ⬆️
│   ├── enums/*                                                     ⬆️
│   ├── exception/**                                                ⬆️
│   ├── filter/{RepeatableFilter, RepeatedlyRequestWrapper, XssFilter, XssHttpServletRequestWrapper}  ⬆️
│   ├── utils
│   │   ├── JsonUtils                                               🔶 Jackson 3
│   │   └── **                                                    ⬆️
│   └── xss/{Xss, XssValidator}                                     ⬆️
├── quartz (extend-quartz)
│   ├── config/ScheduleConfig                🔶 适配 spring-boot-starter-quartz
│   ├── controller/{SysJobController, SysJobLogController}          ⬆️
│   ├── domain/{SysJob, SysJobLog}           ⬆️(SysJob annotation 保持)
│   ├── mapper/{SysJobMapper, SysJobLogMapper}                      ⬆️
│   ├── service/{ISysJobService, ISysJobLogService, impl/*}         ⬆️
│   ├── task/RyTask                                                 ⬆️
│   └── util/{AbstractQuartzJob, CronUtils, JobInvokeUtil, ...}     ⬆️
├── generator (extend-generator)
│   ├── config/GenConfig                     ⬆️
│   ├── controller/GenController             ⬆️
│   ├── domain/{GenTable, GenTableColumn}    ⬆️
│   ├── mapper/*                             ⬆️
│   ├── service/*                            ⬆️
│   └── util/VelocityUtils                   🔶 Jackson 3 适配
└── system (basic-admin-core)
    ├── domain/{SysConfig, SysFiles, SysLogininfor, SysNotice, SysNoticeRead, SysOperLog, ...}  🔶 部分 annotation / Jackson 3
    ├── domain/vo/{MetaVo, RouterVo}          🔶 RouterVo Jackson 3
    ├── mapper/*                              ⬆️
    └── service/*                             ⬆️

com.core (business-core)                      ⬆️
```

### 1.2 🔶 本分支改动文件台账

#### 1.2.1 pom 层(8 个)

| 文件 | 关键改动 |
|---|---|
| 父 `pom.xml` | `java.version` 17→**21**;`spring-boot.version` 3.5.16→**4.1.1**;`springdoc` 2.9.0→**3.1.1**;`mybatis-spring-boot` 3.0.5→**4.1.0**;Druid→`druid-spring-boot-4-starter`;MP→`mybatis-plus-spring-boot4-starter` 3.5.17;PageHelper 4.1.1 |
| `basic-common/pom.xml` | 版本对齐;新增 `mybatis-plus-extension` + `mybatis-plus-jsqlparser-4.9`(MP 3.5.13+ 必需) |
| `basic-framework/pom.xml` | Druid **boot4** starter;`mybatis-plus-jsqlparser-4.9`;版本对齐 |
| `basic-admin-api/pom.xml` | springdoc 3.1;版本对齐 |
| `basic-admin-core/pom.xml` | 版本对齐 |
| `business-core/pom.xml` | 版本对齐 |
| `extend-quartz/pom.xml` | **移除 `org.quartz-scheduler:quartz:2.5.2`**;新增 **`spring-boot-starter-quartz`** |
| `extend-generator/pom.xml` | 版本对齐;Druid boot4 starter |

#### 1.2.2 Jackson 3 核心(4 个文件,15 处 `tools.jackson` import)

| 文件 | 改动 |
|---|---|
| `basic-common/.../utils/JsonUtils.java` | `ObjectMapper`→`tools.jackson.databind`;`JsonProcessingException`→`JacksonException`;`TypeReference`→`tools.jackson.core.type` |
| `basic-framework/.../jackson/BigNumberSerializer.java` | **重写**:`extends ValueSerializer<Number>`;`serialize(Number, JsonGenerator, SerializationContext)` |
| `basic-framework/.../config/BigNumberModuleConfig.java` | **新增**:`@Bean JsonMapperBuilderCustomizer` + `JacksonModule`/`SimpleModule` |
| `basic-framework/.../config/RedisConfig.java` | **改造**:`GenericJacksonJsonRedisSerializer` + `buildRedisJsonMapper()`(defaultTyping + PTV 白名单)+ 显式 `CacheManager` |

#### 1.2.3 Jackson 3 配套(5 个文件)

| 文件 | 改动 |
|---|---|
| `basic-framework/.../config/JacksonConfig.java` | **❌ 整个文件删除** |
| `basic-framework/.../config/ApplicationConfig.java` | 删 `Jackson2ObjectMapperBuilderCustomizer` Bean 整段(时区改 `spring.jackson.time-zone: GMT+8`) |
| `basic-framework/.../config/ResourcesConfig.java` | 精简(删 Jackson 定制) |
| `basic-framework/.../web/service/TokenService.java` | `getLoginUser` catch 块记录日志 + 主动 `deleteObject` 清脏 key |
| `extend-generator/.../util/VelocityUtils.java` | Jackson 3 适配 |

#### 1.2.4 Security 7 / SB4 API(4 个文件)

| 文件 | 改动 |
|---|---|
| `basic-common/.../core/domain/model/LoginUser.java` | **`getAuthorities()` 不返回 null**(返回 `Collections.emptyList()`) |
| `basic-framework/.../config/SecurityConfig.java` | Security 7 确认(`SecurityFilterChain` + `@EnableMethodSecurity` + `requestMatchers`) |
| `basic-framework/.../config/properties/PermitAllUrlProperties.java` | `info.getPatternsCondition().getPatterns()` → **`info.getPatternValues()`** |
| `basic-admin-api/.../BasicApplication.java` | `org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration` → **`org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration`**(SB4 拆到 `spring-boot-jdbc`) |

#### 1.2.5 持久层 / 连接池 / 缓存(4 个文件)

| 文件 | 改动 |
|---|---|
| `basic-framework/.../config/DruidConfig.java` | `boot3.autoconfigure` → **`boot4.autoconfigure`**(含 `properties.DruidStatProperties`) |
| `basic-framework/.../config/MybatisPlusConfig.java` | MP 3.5.17:`PaginationInnerInterceptor` / `BlockAttackInnerInterceptor` 来自 `mybatis-plus-jsqlparser-4.9` |
| `basic-common/.../core/redis/RedisCache.java` | `getCacheObject` 加 try-catch:反序列化失败 → **自动删失效 key** + 返回 null |
| `basic-framework/.../config/ScheduleConfig.java` | 适配 `spring-boot-starter-quartz` 自动装配 |

#### 1.2.6 未改动

| 范围 | 结论 |
|---|---|
| `file-sql/**` | **未改动**(34 表 + 5 函数) |
| `web-ui/**` / `web-mp/**` | **未改动**(JS) |
| 业务 Service / Mapper / Controller 逻辑 | **未改动** |
| 菜单数据 / 权限点 | **未改动** |

---

## 2. 领域模型与枚举

### 2.1 后端实体(⬆️ 与 `springboot3` 一致;🔶 annotation 包保持)

| 实体 | 类 | 主键策略 | 🔶 Jackson 相关 |
|---|---|---|---|
| 用户 | `SysUser` | MP `ASSIGN_ID` | `@JsonIgnore` / `@JsonFormat`(annotation 包**不变**) |
| 角色 | `SysRole` | MP `ASSIGN_ID` | 同上 |
| 菜单 | `SysMenu` | MP `ASSIGN_ID` | — |
| 部门 | `SysDept` | MP `ASSIGN_ID` | — |
| 岗位 | `SysPost` | MP `ASSIGN_ID` | — |
| 字典类型/值 | `SysDictType` / `SysDictData` | MP `ASSIGN_ID` | — |
| 通知 / 已读 | `SysNotice` / `SysNoticeRead` | MP `ASSIGN_ID` | — |
| 定时任务 / 日志 | `SysJob` / `SysJobLog` | MP `ASSIGN_ID` | `SysJob` 有 `@JsonFormat` |
| 操作日志 / 登录日志 | `SysOperLog` / `SysLogininfor` | MP `ASSIGN_ID` | `@JsonFormat` |
| 登录用户 | `LoginUser` | — | **`getAuthorities()` 已改** |
| 路由 | `RouterVo` / `MetaVo` | — | `@JsonInclude` |
| 基类 | `BaseEntity` / `YjyBaseEntity` / `TreeSelect` | — | `@JsonFormat` / `@JsonIgnore` |

> **🔶 关键**:上述实体的 Jackson **注解 import 保持 `com.fasterxml.jackson.annotation.*`** —— 这不是遗漏,是 Jackson 3 的官方设计(注解包未迁移)。

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

### 2.4 🔶 Jackson 3 类型体系对照(本分支新增)

| Java 类型 | Jackson 2(旧 import) | **Jackson 3(本分支 import)** |
|---|---|---|
| `ObjectMapper` | `com.fasterxml.jackson.databind.ObjectMapper` | **`tools.jackson.databind.ObjectMapper`** |
| `JsonMapper` | —(2.10+ 有,少用) | **`tools.jackson.databind.json.JsonMapper`** |
| `JsonMapper.Builder` | — | **`JsonMapper.Builder`**(`builder.addModule(...)`) |
| `TypeReference<T>` | `com.fasterxml.jackson.core.type.TypeReference` | **`tools.jackson.core.type.TypeReference`** |
| `JsonGenerator` | `com.fasterxml.jackson.core.JsonGenerator` | **`tools.jackson.core.JsonGenerator`** |
| `JacksonException` | `JsonProcessingException`(extends `IOException`) | **`tools.jackson.core.JacksonException`**(extends **`RuntimeException`**) |
| `ValueSerializer<T>` | `JsonSerializer<T>` | **`tools.jackson.databind.ValueSerializer<T>`** |
| `StdScalarSerializer<T>` | `com.fasterxml.jackson.databind.ser.std.StdScalarSerializer` | **`tools.jackson.databind.ser.std.StdScalarSerializer`** |
| `SerializationContext` | `SerializerProvider` | **`tools.jackson.databind.SerializationContext`** |
| `JacksonModule` | `com.fasterxml.jackson.databind.Module` | **`tools.jackson.databind.JacksonModule`** |
| `SimpleModule` | `com.fasterxml.jackson.databind.module.SimpleModule` | **`tools.jackson.databind.module.SimpleModule`** |
| `DefaultTyping` | `com.fasterxml.jackson.annotation.JsonTypeInfo`(方式) | **`tools.jackson.databind.DefaultTyping`** |
| `BasicPolymorphicTypeValidator` | `...databind.jsontype.BasicPolymorphicTypeValidator` | **`tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator`** |
| `PolymorphicTypeValidator` | 同上包 | **`tools.jackson.databind.jsontype.PolymorphicTypeValidator`** |
| `JacksonStdImpl` | `...databind.annotation.JacksonStdImpl` | **`tools.jackson.databind.annotation.JacksonStdImpl`** |
| **注解** `@JsonFormat` / `@JsonIgnore` / `@JsonIgnoreProperties` / `@JsonInclude` | `com.fasterxml.jackson.annotation.*` | **不变** ⚠️ |

---

## 3. 框架机制

### 3.1 ~ 3.9 (⬆️ 与 `springboot3` 一致)

| 小节 | 内容 | 状态 |
|---|---|---|
| 3.1 启动与包扫描 | `@SpringBootApplication` + `@MapperScan` | 🔶 import 改(SB4 拆包) |
| 3.2 动态 Schema 路由 | `DataSourceAspect` + `DynamicDataSourceContextHolder` | ⬆️ |
| 3.3 事务边界 | `@Transactional(rollbackFor)` / `AsyncManager` | ⬆️ |
| 3.4 审计切面 | `LogAspect` `@Around` | ⬆️ |
| 3.5 重复提交 | `SameUrlDataInterceptor` | 🔶 微调(避免循环依赖) |
| 3.6 限流 | `RateLimiterAspect` | ⬆️ |
| 3.7 XSS | `XssFilter` + `@Xss` | ⬆️ |
| 3.8 字段填充 | `MyMetaObjectHandler` | ⬆️ |
| 3.9 SB3 升级机制 | jakarta / Security 6 / springdoc / yauaa | ⬆️ 见 `docs/springboot3-详细设计.md` §3.9 |

### 3.10 🔶 SB4 升级机制详细设计(本分支核心)

#### 3.10.1 starter 与依赖替换

**父 `pom.xml` 关键节点**:

```xml
<properties>
    <java.version>21</java.version>                        <!-- 🔶 17 → 21 -->
    <spring-boot.version>4.1.1</spring-boot.version>        <!-- 🔶 3.5.16 → 4.1.1 -->
    <mybatis-spring-boot.version>4.1.0</mybatis-spring-boot.version>   <!-- 🔶 3.0.5 → 4.1.0 -->
    <mybatis-plus-boot-starter.version>3.5.17</mybatis-plus-boot-starter.version>  <!-- 🔶 3.5.7 -->
    <pagehelper.boot.version>4.1.1</pagehelper.boot.version>            <!-- 🔶 2.1.1 -->
    <druid.version>1.2.28</druid.version>                              <!-- 版本不变,starter 变 -->
    <springdoc.version>3.1.1</springdoc.version>                        <!-- 🔶 2.9.0 -->
</properties>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>${spring-boot.version}</version>
            <type>pom</type><scope>import</scope>
        </dependency>

        <!-- 🔶 starter 换 SB4 变体 -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-4-starter</artifactId>        <!-- -3- → -4- -->
            <version>${druid.version}</version>
        </dependency>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-spring-boot4-starter</artifactId>  <!-- spring-boot3 → spring-boot4 -->
            <version>${mybatis-plus-boot-starter.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>${springdoc.version}</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

**`extend-quartz/pom.xml`**:

```xml
<!-- ❌ 移除(SB3 手工依赖) -->
<!-- <dependency>
    <groupId>org.quartz-scheduler</groupId>
    <artifactId>quartz</artifactId>
    <version>2.5.2</version>
</dependency> -->

<!-- ✅ 改为 SB4 starter(自动装配 SchedulerFactoryBean) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-quartz</artifactId>
</dependency>
```

**MP 3.5.13+ 必需的 jsqlparser 拆包**(`basic-common` / `basic-framework`):

```xml
<!-- MP 3.5.13+ 把拦截器迁到独立模块且标的 optional=true(不传递),必须显式引入 -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-extension</artifactId>
</dependency>
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-jsqlparser-4.9</artifactId>
</dependency>
```

> ⚠️ 不引入 `mybatis-plus-jsqlparser-4.9` → `PaginationInnerInterceptor` / `BlockAttackInnerInterceptor` **类找不到**,分页直接失效。

#### 3.10.2 Jackson 3 迁移:四类替换 + 一个不改

**① 核心/数据绑定包替换**

```java
// ❌ Jackson 2
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;

// ✅ Jackson 3
import tools.jackson.databind.ObjectMapper;
import tools.jackson.core.type.TypeReference;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JacksonException;
```

**② 异常体系替换(★ 最易漏)**

```java
// ❌ 旧:JsonProcessingException 是受检异常,可被 IOException 捕获
try {
    objectMapper.readValue(json, X.class);
} catch (IOException e) {          // Jackson 3 下抓不到!
    ...
}

// ✅ 新:JacksonException 是 RuntimeException
try {
    objectMapper.readValue(json, X.class);
} catch (JacksonException e) {     // 注意:不是 IOException
    ...
}
```

**③ 序列化器基类替换**

```java
// ❌ Jackson 2
public class XxxSerializer extends JsonSerializer<Xxx> {
    @Override
    public void serialize(Xxx value, JsonGenerator gen, SerializerProvider provider) throws IOException { ... }
}

// ✅ Jackson 3
public class XxxSerializer extends ValueSerializer<Xxx> {
    @Override
    public void serialize(Xxx value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException { ... }
}
```

**④ 模块基类替换**

```java
// ❌ Jackson 2
import com.fasterxml.jackson.databind.Module;
public class MyModule extends Module { ... }

// ✅ Jackson 3
import tools.jackson.databind.JacksonModule;
public class MyModule extends JacksonModule { ... }
```

**⑤ 注解包**:**不改**

```java
// ✅ 保持原样(Jackson 3 官方保留注解包)
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
```

> **实测**:全仓仅这 4 个唯一 annotation import,分布在 **11 个文件**:
> `BaseEntity` / `YjyBaseEntity` / `TreeSelect` / `LoginUser` / `SysUser`(entity)/ `SysRole`(entity)/ `SysLogininfor` / `SysOperLog` / `RouterVo` / `SysJob` / `VelocityUtils`

**校验命令**:

```bash
# ① 只允许 annotation
grep -rn "com\.fasterxml\.jackson" --include=*.java . | grep -v "annotation\."    # 应为空

# ② Jackson 3 应命中
grep -rn "import tools\.jackson" --include=*.java . | wc -l                        # 期望 ≥ 15

# ③ 运行时注册应为空
grep -rn "registerModule" --include=*.java .                                       # 应为空

# ④ 旧 Customizer 应为空
grep -rn "Jackson2ObjectMapperBuilderCustomizer" --include=*.java .                # 应为空

# ⑤ NumberSerializer 应为空
grep -rn "NumberSerializer" --include=*.java . | grep -v "BigNumberSerializer"      # 应为空
```

#### 3.10.3 Module 构建时注册(★ 架构级变化)

**错误写法(Jackson 2 习惯,Javon 3 编译失败)**:

```java
// ❌ Jackson 3 中 ObjectMapper / JsonMapper 是不可变的,运行时注册 API 已删除
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new SimpleModule().addSerializer(...));   // 编译不过
```

**正确写法 A:Spring 侧(Web 请求响应)**

```java
@Configuration
public class BigNumberModuleConfig {       // 🔶 必须是独立类,见 3.10.4

    @Bean
    public JsonMapperBuilderCustomizer bigNumberJsonMapperBuilderCustomizer() {
        JacksonModule module = new SimpleModule()
                .addSerializer(Long.class,     BigNumberSerializer.INSTANCE)
                .addSerializer(Long.TYPE,      BigNumberSerializer.INSTANCE)
                .addSerializer(BigInteger.class, BigNumberSerializer.INSTANCE)
                .addSerializer(BigDecimal.class, ToStringSerializer.instance);
        return builder -> builder.addModule(module);    // ✅ 构建时注册
    }

    public static class ToStringSerializer extends StdScalarSerializer<Object> {
        public static final ToStringSerializer instance = new ToStringSerializer();
        public ToStringSerializer() { super(Object.class); }
        @Override
        public void serialize(Object value, JsonGenerator gen, SerializationContext ctxt)
                throws JacksonException {
            gen.writeString(value.toString());
        }
    }
}
```

> **Web 端只加 Module,不加 defaultTyping**;Redis 端单独构造带 defaultTyping 的 mapper(见 3.10.5)。

**正确写法 B:独立 `JsonMapper`(Redis 侧)**

```java
JsonMapper mapper = JsonMapper.builder()
        .activateDefaultTyping(ptv, DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY)  // ✅ builder 上
        .addModule(module)                                                              // ✅ 构建时
        .build();
```

#### 3.10.4 循环依赖:Customizer 必须独立类(★ 启动失败高发点)

**失败链路**:

```
ResourcesConfig(含 Customizer Bean)
    │ 依赖
    ▼
RepeatSubmitInterceptor
    │ 依赖
    ▼
RedisCache
    │ 依赖
    ▼
RedisTemplate
    │ 需要
    ▼
JsonMapper Bean
    │ 创建时需要所有 JsonMapperBuilderCustomizer
    └──────────────▶ 回到 ResourcesConfig  ❌ 循环依赖 → 启动失败
```

**本分支解法**:

| 类 | 职责 | 依赖 |
|---|---|---|
| `BigNumberModuleConfig` | **只提供** `JsonMapperBuilderCustomizer` | **无业务依赖**(纯 Jackson) |
| `ResourcesConfig` | 静态资源映射 + 拦截器注册 | `RepeatSubmitInterceptor` → `RedisCache` |

> **规则**:任何新增的 `JsonMapperBuilderCustomizer` **都必须**放在不含 `RedisTemplate` 依赖的独立类中。

#### 3.10.5 Redis 序列化(Jackson 3 + 白名单)

**`RedisConfig` 关键实现**:

```java
@Configuration
public class RedisConfig {

    /**
     * 构造 Redis 专用 JsonMapper:
     *  - 启 defaultTyping(为了存 LoginUser 这类多态对象)
     *  - 白名单 PTV(只允许业务包 + JDK 常用包) ← 安全关键
     *  - 注意:Redis 端不加 BigNumber Module(避免缓存值类型漂移)
     */
    private JsonMapper buildRedisJsonMapper() {
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("com.basic.")     // 业务包
                .allowIfSubType("com.core.")      // 业务扩展包
                .allowIfSubType("java.util.")     // 集合
                .allowIfSubType("java.lang.")     // 基础类型
                .allowIfSubType("java.time.")     // 时间
                .build();

        return JsonMapper.builder()
                .activateDefaultTyping(ptv, DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY)
                .build();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        // 🔶 内部构造,不注入外部 JsonMapper(避免循环依赖)
        JsonMapper redisMapper = buildRedisJsonMapper();
        GenericJacksonJsonRedisSerializer serializer =
                new GenericJacksonJsonRedisSerializer(redisMapper);   // 🔶 Spring Data Redis 4.1.1 配套 Jackson 3

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }

    /** 🔶 SB4 缺口:必须显式声明 CacheManager */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        // 按需构造 RedisCacheManager
        ...
    }
}
```

**与 SB3 的差异**:

| 项 | `springboot3` | **本分支** |
|---|---|---|
| 序列化器 | `Jackson2JsonRedisSerializer` / `FastJson2JsonRedisSerializer` | **`GenericJacksonJsonRedisSerializer`**(Jackson 3) |
| defaultTyping 位置 | `ObjectMapper` 实例上 | **`JsonMapper.Builder` 上** |
| 多态校验器 | `LaissezFaireSubTypeValidator`(**无白名单,有风险**) | **`BasicPolymorphicTypeValidator`(白名单)** |
| JsonMapper 来源 | 注入或 `new ObjectMapper()` | **内部构造**(防循环依赖) |
| CacheManager | 自动配置 | **显式声明**(SB4 缺口) |

#### 3.10.6 `BigNumberSerializer` 重写(Jackson 3 删除 `NumberSerializer`)

```java
package com.basic.framework.jackson;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.annotation.JacksonStdImpl;

/**
 * 大数序列化:超出 JS 安全整数范围的数值输出为字符串,避免前端精度丢失
 *
 * Jackson 3 的 ser/std/ 已无 NumberSerializer,故直接继承 ValueSerializer<Number> 自写。
 */
@JacksonStdImpl
public class BigNumberSerializer extends ValueSerializer<Number> {

    public static final BigNumberSerializer INSTANCE = new BigNumberSerializer();

    /** JS Number.MAX_SAFE_INTEGER = 2^53 - 1 */
    private static final long MAX_SAFE_INTEGER = 9007199254740991L;
    private static final long MIN_SAFE_INTEGER = -9007199254740991L;

    @Override
    public void serialize(Number value, JsonGenerator gen, SerializationContext ctxt)
            throws JacksonException {
        if (value == null) {
            gen.writeNull();
            return;
        }
        long l = value.longValue();
        if (l > MIN_SAFE_INTEGER && l < MAX_SAFE_INTEGER) {
            gen.writeNumber(l);              // 安全区间 → 数字
        } else {
            gen.writeString(value.toString());  // 超出 → 字符串
        }
    }
}
```

> **行为与 SB3 一致**:前端拿到的仍是"小数字、大字符串",无需改动。

#### 3.10.7 Security 7:`getAuthorities()` 严格化

**`LoginUser` 实现**:

```java
@Override
public Collection<? extends GrantedAuthority> getAuthorities() {
    return permissions == null || permissions.isEmpty()
        ? Collections.emptyList()                                   // 🔶 绝不返回 null
        : permissions.stream()
              .map(SimpleGrantedAuthority::new)
              .collect(Collectors.toList());
}
```

**后果对照**:

| 实现 | Security 6(SB3) | **Security 7(SB4)** |
|---|---|---|
| 返回 `null` | 能跑(内部当空处理) | **NPE / 启动失败** |
| 返回空集合 | 能跑 | **能跑** ✅ |

#### 3.10.8 Quartz 改 starter

**`extend-quartz` 结构调整**:

| 项 | SB3 | **本分支** |
|---|---|---|
| 依赖 | `org.quartz-scheduler:quartz:2.5.2`(手写) | **`spring-boot-starter-quartz`** |
| `SchedulerFactoryBean` | 手动 `@Bean` 配置(DataSource / JobStore) | **starter 自动装配** |
| JobStore | 手配 | **`RAMJobStore`**(默认,内存) |
| `qrtz_*` 表 | 可能被使用 | **不使用**(表保留) |
| 重启行为 | 取决于 JobStore | **动态任务丢失**(`sys_job` 定义仍在) |

```java
// ScheduleConfig 精简后(示例)
@Configuration
public class ScheduleConfig {
    // SchedulerFactoryBean 由 spring-boot-starter-quartz 自动提供,
    // 此处只做业务侧定制(如需要)
}
```

#### 3.10.9 SB4 其它 API 变更

| 文件 | 变更 |
|---|---|
| `BasicApplication` | `org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration` → **`org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration`**(SB4 拆到 `spring-boot-jdbc`) |
| `PermitAllUrlProperties` | `info.getPatternsCondition().getPatterns()` → **`info.getPatternValues()`**(Framework 7 弃用老 API,返回 `Set<String>`) |
| `DruidConfig` | `com.alibaba.druid.spring.boot3.autoconfigure.*` → **`...spring.boot4.autoconfigure.*`**(含 `properties.DruidStatProperties`) |
| `MybatisPlusConfig` | `PaginationInnerInterceptor` / `BlockAttackInnerInterceptor` 来自 **`mybatis-plus-jsqlparser-4.9`** |
| `ApplicationConfig` | **删** `Jackson2ObjectMapperBuilderCustomizer` 整段;时区由 `spring.jackson.time-zone: GMT+8` 配 |
| `JacksonConfig` | **❌ 整个文件删除** |
| `RedisCache.getCacheObject` | 加 try-catch:反序列化失败 → 删失效 key + 返回 null |
| `TokenService.getLoginUser` | catch 块记录日志 + 主动 `redisCache.deleteObject(getTokenKey(uuid))` |

**`RedisCache` 容错实现**:

```java
public <T> T getCacheObject(final String key) {
    try {
        return redisTemplate.opsForValue().get(key);
    } catch (Exception e) {
        log.error("从 Redis 读取 key={} 失败, 自动删除失效 key", key, e);
        try {
            redisTemplate.delete(key);       // 🔶 自动清理 Jackson 2 时代脏数据
        } catch (Exception ignore) { }
        return null;                          // 当未命中
    }
}
```

**`TokenService` 清理实现**:

```java
public LoginUser getLoginUser(HttpServletRequest request) {
    String token = getToken(request);
    if (StringUtils.isNotEmpty(token)) {
        try {
            Claims claims = parseToken(token);
            String uuid = (String) claims.get(Constants.LOGIN_USER_KEY);
            String userKey = getTokenKey(uuid);
            return redisCache.getCacheObject(userKey);
        } catch (Exception e) {
            log.error("获取用户信息异常", e);                       // 🔶 不再静默
            String uuid = ...;
            redisCache.deleteObject(getTokenKey(uuid));             // 🔶 主动清脏 key
        }
    }
    return null;
}
```

---

## 4. 核心引擎详细设计

### 4.1 ~ 4.5 后端核心引擎(⬆️ 与 `springboot3` 一致)

| 小节 | 内容 | 🔶 本分支 |
|---|---|---|
| 4.1 登录引擎 | `SysLoginService.login(username, password, code, uuid)` + kaptcha | ⬆️ 逻辑不变;`TokenService` 序列化换 Jackson 3 |
| 4.2 锁屏/解锁 | 前端 `store/modules/lock.js` 复用 `/login` | ⬆️ 不变(前端零改动) |
| 4.3 定时任务 | `SysJobServiceImpl` / `AbstractQuartzJob` / `JobInvokeUtil` | 🔶 `SchedulerFactoryBean` 由 starter 提供;RAMJobStore |
| 4.4 代码生成 | `GenTableServiceImpl` + Velocity 模板 | 🔶 `VelocityUtils` Jackson 3;⚠️ 模板仍产 SB2 代码 |
| 4.5 动态路由/菜单 | `SysMenuServiceImpl.buildMenus` | ⬆️ 不变(`RouterVo` annotation 保持) |

### 4.6 🔶 Jackson 3 引擎详解(本分支新增)

#### 4.6.1 Web 请求/响应序列化链路

```
HTTP 响应体
   │
   ▼
🔶 JacksonJsonHttpMessageConverter(Spring Boot 4 自动配置)
   │ 使用 ├─ 默认 JsonMapper(SB4 构建)
   │      └─ 应用所有 JsonMapperBuilderCustomizer
   │           └─ BigNumberModuleConfig(🔶 注入 Long/BigInteger/BigDecimal 序列化器)
   ▼
JSON 文本 → 前端 JS
   │
   └─ 大数(超 ±2^53-1)→ 字符串;其余 → 数字
```

#### 4.6.2 Redis 序列化链路(独立于 Web)

```
RedisCache.setCacheObject(key, value)
   │
   ▼
RedisTemplate<String, Object>
   │ valueSerializer = 🔶 GenericJacksonJsonRedisSerializer
   │      └─ JsonMapper(内部构造)
   │           ├─ activateDefaultTyping(NON_FINAL, As.PROPERTY)
   │           └─ BasicPolymorphicTypeValidator 白名单
   │                (com.basic. / com.core. / java.util. / java.lang. / java.time.)
   ▼
Redis(带 @class 类型信息)

RedisCache.getCacheObject(key)
   │
   ▼
反序列化
   ├─ 成功 → 返回对象
   └─ 失败(Jackson 2 脏数据 / 类已删)→ 🔶 catch → 删 key → 返回 null
```

#### 4.6.3 `JsonUtils` 工具类(Jackson 3 版)

```java
public class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /** 对象 → JSON 字符串 */
    public static String toJsonString(Object object) {
        return OBJECT_MAPPER.writeValueAsString(object);
    }

    /** JSON 字符串 → 对象 */
    public static <T> T parseObject(String text, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(text, clazz);
        } catch (JacksonException e) {                       // 🔶 不是 IOException
            log.error("JSON 解析失败: {}", text, e);
            return null;
        }
    }

    /** JSON 字符串 → 泛型对象 */
    public static <T> T parseObject(String text, TypeReference<T> typeReference) {
        try {
            return OBJECT_MAPPER.readValue(text, typeReference);   // 🔶 tools.jackson.core.type.TypeReference
        } catch (JacksonException e) {
            log.error("JSON 解析失败: {}", text, e);
            return null;
        }
    }
}
```

#### 4.6.4 新增序列化器的标准做法(供后续扩展)

```
① 确定是「Web 方向」还是「Redis 方向」还是「都要」
      │
      ├─ Web 方向 → 写 JsonMapperBuilderCustomizer,放**独立配置类**
      │     └─ 不启 defaultTyping
      │
      ├─ Redis 方向 → 在 RedisConfig.buildRedisJsonMapper() 里加(自带 defaultTyping + 白名单)
      │
      └─ 都要 → 分别加;注意 Redis 侧不要加 BigNumber(避免缓存值类型漂移)
      │
      ▼
② 序列化器继承 ValueSerializer<T>,方法签名 (T, JsonGenerator, SerializationContext)
      │
      ▼
③ 编译 → 启动 → 验证
```

#### 4.6.5 常见序列化器写法对照

| 需求 | Jackson 2 写法 | Jackson 3 写法 |
|---|---|---|
| 标量转字符串 | `extends JsonSerializer<T>` | `extends StdScalarSerializer<T>` 或 `ValueSerializer<T>` |
| 数字特殊处理 | `extends NumberSerializer` | **无对应类**,`extends ValueSerializer<Number>` |
| 枚举转码 | `@JsonValue` | `@JsonValue`(注解不变) |
| 忽略字段 | `@JsonIgnore` | `@JsonIgnore`(注解不变) |
| 自定义日期 | `@JsonFormat` | `@JsonFormat`(注解不变) |
| 模块注册 | `mapper.registerModule()` | `builder.addModule()` |

---

## 5. 校验引擎

### 5.1 L1 字段级校验(⬆️ 与 `springboot3` 一致)

- 入口:`@Validated` + `jakarta.validation.constraints.*`
- 失败处理:`GlobalExceptionHandler` → 400 + msg

### 5.2 L2 跨业务校验(⬆️ 一致)

用户名唯一 / 角色 key 唯一 / 菜单自引用 / 部门环 / 通知状态 / invokeTarget / 字典值唯一 / 已读唯一。

### 5.3 🔶 编译期校验(SB4 特有)

| 校验项 | 手段 | 失败表现 |
|---|---|---|
| Jackson 2 核心包残留 | `mvn clean compile` + grep | `cannot find symbol` |
| 运行时注册 Module | 编译 | 方法不存在 |
| `NumberSerializer` 残留 | 编译 | 类不存在 |
| Jackson 2 异常捕获 | 逻辑 | 异常穿透(不被 catch) |
| SB3 starter 残留 | 启动 | Bean 不注册 |
| `getAuthorities()` 返回 null | 启动/运行 | NPE |
| Customizer 循环依赖 | 启动 | `BeanCurrentlyInCreationException` |

---

## 6. 业务模块实现(⬆️ 与 `springboot3` 一致)

用户管理 / 角色管理 / 菜单管理 / 通知已读 / Demo 业务 —— 全部一致,不重复。
> 🔶 **唯一相关变化**:实体上的 Jackson 注解包保持 `com.fasterxml.jackson.annotation`(不需改);`LoginUser` 的 `getAuthorities()` 已改。

---

## 7. 审批旁路(本分支不实现)

> `springboot4-extend-flowable` 分支提供:`extend-flowable` 模块 + Flowable + `basicdb_flowable` schema。
> 本分支保留钩子位点(无实现):`BusinessType.GRANT` / `extend-*` 模式 / `gen_table.is_workflow`(待定)。

---

## 8. 审计/报表/Web/安全

### 8.1 审计(⬆️ 一致)

- 写操作:`@Log` + `LogAspect` → `sys_oper_log`
- 登录事件:`AsyncManager` → `sys_logininfor`(yauaa 解析 UA)

### 8.2 报表(⬆️ 一致)

操作日志 / 登录日志 / 服务器监控(oshi 7.4.4)。

### 8.3 Web 端(`web-ui`,⬆️ **零改动**)

入口 `main.js` / `permission.js` / `utils/request.js` 均未改;路由守卫、权限指令、锁屏、上传逻辑与 SB3 一致。

> 🔶 **唯一可能相关**:若前端硬编码了 Swagger 旧路径需改;本仓库未硬编码。

### 8.4 移动端(`web-mp`,⬆️ **零改动**)

入口 `main.js` / `permission.js` / `utils/request.js` 均未改;`pages.json` 静态路由不变。

### 8.5 安全(⬆️ + 🔶 SB4 强化)

| 项 | 实现 |
|---|---|
| 密码 | BCrypt |
| 登录传输 | RSA 加密 password |
| Token | HS512 JWT,30min |
| Token 存储 | Redis `login_tokens:<uuid>`(**Jackson 3 序列化**) |
| 验证码 | kaptcha 2.3.3 |
| XSS / 重复提交 / 限流 / CORS | ⬆️ 一致 |
| **🔶 Security 7** | `getAuthorities()` 非 null;**严格化已适配** |
| **🔶 Redis 多态** | `BasicPolymorphicTypeValidator` 白名单(**替代 SB3 的 LaissezFaire**) |
| **🔶 不引 fastjson2** | 规避 fastjson 历史 CVE(CVE-2023-52425 / CVE-2022-25845 系列) |
| 操作/登录日志 | 不可关闭 |

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

> 🔶 **对外错误码零变化**:前端 `utils/errorCode.js` 无需调整。

### 9.2 🔶 升级期启动/运行错误(排障用,非业务码)

| 现象(异常/日志) | 根因 | 处置 |
|---|---|---|
| `cannot find symbol: com.fasterxml.jackson.databind.ObjectMapper` | Jackson 3 换包 | 改 `tools.jackson.databind.ObjectMapper` |
| `cannot find symbol: method registerModule(...)` | Jackson 3 删除运行时注册 | 改 `JsonMapper.builder().addModule()` / `JsonMapperBuilderCustomizer` |
| `cannot find symbol: class NumberSerializer` | Jackson 3 删除该类 | 自写 `ValueSerializer<Number>` |
| `BeanCurrentlyInCreationException` / `circular dependency` | Customizer 与 `RedisTemplate` 同链路 | 拆独立配置类(`BigNumberModuleConfig`) |
| `NullPointerException` at `getAuthorities()` | Security 7 不允许 null | 返回 `Collections.emptyList()` |
| `ClassNotFoundException: com.alibaba.druid.spring.boot3.autoconfigure.*` | Druid 包名未改 | 改 `boot4.autoconfigure` |
| `Failed to configure a DataSource` | 用了 SB3 的 Druid starter | 换 `druid-spring-boot-4-starter` |
| `NoSuchBeanDefinitionException: SqlSessionFactory` | 用了 SB3 的 MP starter | 换 `mybatis-plus-spring-boot4-starter` |
| `NoClassDefFoundError: PaginationInnerInterceptor` | 未引 `mybatis-plus-jsqlparser-4.9` | 显式引入该依赖 |
| `cannot find symbol: DataSourceAutoConfiguration`(旧包) | SB4 拆包 | 改 `org.springframework.boot.jdbc.autoconfigure.*` |
| `Cannot deserialize` / Redis 读取异常 | Jackson 2 脏数据 | `RedisCache` 已容错;**建议清空 Redis** |
| 定时任务重启后消失 | RAMJobStore | 已知取舍;需持久化切 JDBC JobStore |
| `JSON parse error` 但代码有 `catch (IOException)` | `JacksonException` 是 `RuntimeException` | 改 `catch (JacksonException)` |
| 分页总数不对 | pagehelper 2.1 → 4.1 行为差异 | 回归分页;必要时显式配方言 |
| 启动报 `spring-boot-starter-quartz` 冲突 | 旧手写 quartz 依赖未移除 | 移除 `org.quartz-scheduler:quartz` |

---

## 10. 并发与线程模型(⬆️ 与 `springboot3` 一致)

| 资源 | 位置 | 配置 |
|---|---|---|
| Tomcat | `application.yml` | `max-threads=800` / `min-spare=10` |
| 异步线程池 | `ThreadPoolConfig` | `core=10` / `max=50` / `queue=200` |
| Druid | `application-druid.yml` | `min-idle=5` / `max-active=20` |
| **Quartz** | starter 自动装配 | **RAMJobStore**(内存);线程数按默认 |
| Redis | `RedisCache` | TTL 30min |
| 限流 | Guava / Redis | 5 QPS/10s |
| 重复提交 | Redis | 500ms |
| UA 解析 | `UserAgentAnalyzer` | `withCache(1000)` |
| **🔶 Jackson** | `JsonMapper` | 线程安全(只读);Module 构建期固定 |

**锁使用**:`qrtz_locks` **不再使用**(RAMJobStore);多实例定时任务**会重复执行**(无分布式锁),需注意。

---

## 11. 测试关注点

### 11.1 ~ 11.2 业务与 SB3 专项(⬆️ TC-01~TC-32,与 `springboot3` 一致)

| # | 场景 | 重点 |
|---|---|---|
| TC-01~TC-20 | 登录 / Token / 强退 / 数据权限 / 重复提交 / 限流 / XSS / 字典 / 定时任务 / 代码生成 / 日志 / 移动端 / 锁屏 / 通知 / 上传 / 停机 | 同 `springboot3` |
| TC-21~TC-32 | SB3 升级专项(jakarta / springdoc / starter / 配置 / 编译 / 启动) | 见 `docs/springboot3-详细设计.md` §11.2 |

### 11.3 🔶 SB4 专项(TC-63~TC-80)

| # | 场景 | 重点 |
|---|---|---|
| **TC-63** | JDK 21 下 `mvn clean compile` | **0 ERROR**;确认 Maven/IDE 均用 JDK 21 |
| **TC-64** | `mvn spring-boot:run` 启动 | 无循环依赖 / 无 NPE / 日志「启动成功」 |
| **TC-65** | `grep "com\.fasterxml\.jackson"`(排除 `annotation.`) | **空** |
| **TC-66** | `grep "import tools\.jackson"` | **≥ 15 处**(4 个文件) |
| **TC-67** | `grep "registerModule"` | **0** |
| **TC-68** | `grep "Jackson2ObjectMapperBuilderCustomizer"` | **0**;`JsonMapperBuilderCustomizer` 有命中 |
| **TC-69** | `grep "NumberSerializer"`(排除 `BigNumberSerializer`) | **0** |
| **TC-70** | `grep "druid-spring-boot-3-starter"` / `mybatis-plus-spring-boot3-starter` | **0** |
| **TC-71** | `grep "fastjson"` | **0** |
| **TC-72** | **大数序列化**:构造 `Long` = `9007199254740992`(2^53)→ 响应为**字符串** | 验证 `BigNumberSerializer` |
| **TC-73** | **小整数**:`Long` = `123` → 响应为**数字**(不误转字符串) | 同上 |
| **TC-74** | **`BigDecimal`** → 字符串 | `ToStringSerializer` |
| **TC-75** | **Security 7**:无权限用户访问受保护接口 → **403**(非 500 / NPE) | `getAuthorities()` |
| **TC-76** | **Redis 写读**:存 `LoginUser` → 取回类型正确(多态还原) | defaultTyping + 白名单 |
| **TC-77** | **Redis 脏数据**:手工塞一个 Jackson 2 格式的 key → 读取返回 null 且 key 被删 | `RedisCache` 容错 |
| **TC-78** | **Redis 白名单**:塞一个非白名单类的 key → 反序列化被拒(不执行 gadget) | PTV |
| **TC-79** | **Quartz**:新建 Cron → 启动 → 立即执行 → `sys_job_log` 有记录;**重启后动态任务消失**(确认 RAMJobStore 行为) | starter + RAMJobStore |
| **TC-80** | **前端零改动**:`git diff springboot3 -- web-ui web-mp` 为空;两端登录/CRUD 正常 | 终端 + 浏览器 |
| **TC-81** | **分页**:各列表页总数与 SB3 一致 | pagehelper 4.1 |
| **TC-82** | **Swagger**:`/swagger-ui/index.html` + `/v3/api-docs` 正常(springdoc 3.1) | 浏览器 |

---

## 12. 需求变更记录

| 需求 | 落点 |
|---|---|
| 锁屏解锁 | ⬆️ §4.2(前端零改动) |
| 移动端双前端 | §8.4(**零改动**) |
| 数据库 PostgreSQL | §3.1 + §8.5(**零变更**) |
| 操作日志 / 重复提交 / 限流 / XSS | §3.4~3.7 + §8.1 |
| Quartz 动态任务 | §4.3 + §3.10.8(**改 starter + RAMJobStore**) |
| 代码生成 | §4.4(⚠️ 模板未适配) |
| 数据权限 | §3 + 《需求文档》§8 |
| ⬆️ **SB 3 升级** | §3.9(见 `docs/springboot3-详细设计.md`) |
| **🔶 SB 3.5.16 → 4.1.1 + JDK 21** | **§1.2 文件台账 + §3.10 升级机制 9 小节 + §9.2 排障表 + §11.3 TC-63~TC-82** |
| **🔶 Jackson 2 → 3** | §2.4 类型对照 + §3.10.2 四类替换 + §3.10.3 Module 注册 + §4.6 Jackson 3 引擎详解 |
| **🔶 循环依赖拆分** | §3.10.4(链路图 + 规则) |
| **🔶 Redis 序列化** | §3.10.5(`GenericJacksonJsonRedisSerializer` + 白名单) |
| **🔶 `BigNumberSerializer` 重写** | §3.10.6(完整源码) |
| **🔶 Security 7** | §3.10.7(`getAuthorities()`) |
| **🔶 Quartz 改 starter** | §3.10.8 |
| **🔶 SB4 其它 API 变更** | §3.10.9(DataSourceAutoConfiguration / getPatternValues / Druid 包名 / jsqlparser / JacksonConfig 删除 / RedisCache 容错 / TokenService) |

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `springboot3` 详细设计(v0.1)为基线;业务与 SB3 部分标注「与 springboot3 一致」;新增 §1.2 改动文件台账(8 pom + 4 Jackson 核心 + 5 Jackson 配套 + 4 Security/SB4 API + 4 持久层缓存)、§2.4 Jackson 3 类型体系对照(16 行)、§3.10 升级机制 9 小节(starter 替换 / 四类替换+一个不改 / Module 构建时注册 / 循环依赖链路 / Redis 序列化 / BigNumberSerializer 完整源码 / Security 7 / Quartz starter / 其它 API 变更)、§4.6 Jackson 3 引擎详解(5 小节)、§9.2 排障表(15 类)、§11.3 TC-63~TC-82 |
