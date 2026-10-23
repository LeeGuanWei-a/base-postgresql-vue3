# Basic Admin (Spring Boot 4.1.1 + Vue 3 + Element Plus)

基于若依 RuoYi-Vue 3.9.2 改造,从 `springboot4` 继承并叠加**企业微信 / 公众号 / 小程序 / 支付宝**渠道集成脚手架(SDK 装配 + Redis token 持久化 + POC 联通性验证),**未实现任何业务功能**(通讯录同步 / 消息推送 / 回调 / 支付)。

> 当前分支:`springboot4-extend-channel` ——「`springboot4` + 渠道集成(POC 阶段)」。
> 本分支基线是 `springboot4`(SB 4.1.1 / JDK 21 / Jackson 3 / Security 7)。
> 仓库另有 `master` / `springboot2-extend-flowable` / `springboot2-tianai-captcha` / `springboot2_TypeScript` / `springboot3` / `springboot3-tianai-captcha` / `springboot3_TypeScript` / `springboot4` / `springboot4_TypeScript` / `springboot4-extend-flowable` 等分支,本分支**只描述 springboot4-extend-channel**。
> 渠道集成架构设计见 `docs/渠道集成-架构方案.md`。

---

## 前端说明

本分支前端**两端都是 JavaScript 版**(与 `springboot3` / `master` 一致,区别于 `springboot4_TypeScript`):

### 1.1 PC 管理端 `web-ui/`

- 框架:Vue 3.5.26 + Element Plus 2.13.1
- 构建:Vite 6.4.1
- 代码:纯 `.js` / `.vue`,**无 `typescript` 依赖**(package.json 不含 `typescript`)

> TS 版在 `springboot2_TypeScript` / `springboot3_TypeScript` / `springboot4_TypeScript` 分支,本分支**未涉及**。

### 1.2 移动端 `web-mp/`(本分支新增,与 `springboot3` 一致)

- 框架:**uni-app 3.0 (alpha)** + Vue 3.4.21(组合式 API `<script setup>`)
- UI:**uview-plus** 3.3.62(uView 的 Vue3 版本,组件名带 `up-` 前缀)
- 构建:Vite 5.2.8
- 一份代码同时跑 **H5 / 微信小程序 / Android / iOS / 其它小程序**
- 状态管理:Pinia 2.0.36
- 加密:jsencrypt(对接后端 RSA 登录)
- 语言:**JavaScript**(无 TypeScript)

> 详细说明见下文**「九、移动端 `web-mp`」** 章节。

---

## 一、当前后端状态(基于 `git show HEAD:pom.xml`)

> 本分支**后端升级到 SB 4.1.1**;前端、SQL 脚本、业务逻辑与 `springboot3` 保持一致。

| 组件 | 版本 | 与 `springboot3` 的差异 |
|---|---|---|
| **JDK** | **21** | ★ 由 17 升到 21 |
| Spring Boot | **4.1.1** | ★ 由 3.5.16 升到 4.1.1 |
| Spring Framework | **7.0.9** | ★ 由 6.x 升到 7.x |
| Spring Security | **7.1.1** | ★ 由 6.x 升到 7.x(严格化,`getAuthorities()` 不能返回 null) |
| Spring Data Redis | **4.1.1** | |
| MyBatis-Plus | **3.5.17** | + `mybatis-plus-spring-boot4-starter` |
| MyBatis-Spring-Boot | **4.1.0** | ★ starter 名带 `-4-` |
| PageHelper starter | **4.1.1** | |
| Druid | **1.2.28** | starter 改名为 `druid-spring-boot-4-starter`,包名 `boot3.autoconfigure` → `boot4.autoconfigure` |
| Springdoc-openapi | **3.1.1** | ★ 由 2.9.0 升到 3.1.1 |
| Hutool | hutool-core 5.8.27 | |
| yauaa | 8.2.0 | |
| Lombok | 1.18.46 | |
| **Jackson** | **3.1.5** (`tools.jackson.*`,全包名换) | ★★ **本次最大坑** |
| jakarta.servlet | **6.1.0** | ★ 由 5.x 升到 6.x |
| fastjson2 | **未引入** | (若依原版用,我们不用,改 Jackson 3) |
| Quartz | **spring-boot-starter-quartz**(自动装配 `SchedulerFactoryBean`) | ★ 取代手写的 `quartz:2.5.2` 依赖 |

> 完整的 SB 3 → SB 4 升级细节(Jackson 3 适配 / Redis 序列化 / 循环依赖等)见下文
> 第二节起的细节章节。

---

## 二、本分支是什么

`springboot4` 分支以 **`springboot3` 分支**为基线,只做了一件事:

> **把后端从 Spring Boot 3.5.16 一次性升级到 Spring Boot 4.1.1**,
> 前端两端(`web-ui` / `web-mp`)、业务功能、数据库脚本**完全不动**。

具体而言:

1. **JDK 17 → 21**
2. **Spring Boot 3.5.16 → 4.1.1**
3. **Spring Framework 6.x → 7.0.9**
4. **Spring Security 6.x → 7.1.1**(`getAuthorities()` 不能再返回 null,会直接 NPE)
5. **Jackson 2 → Jackson 3**(`com.fasterxml.jackson.*` → `tools.jackson.*`,全包名换,本次**最大坑**)
6. **Spring Boot starter 重命名 / 子模块拆分**:
   - `spring-boot-starter-web` → `spring-boot-starter-webmvc`
   - `spring-boot-starter-aop` → `spring-boot-starter-aspectj`
   - `druid-spring-boot-3-starter` → `druid-spring-boot-4-starter`
   - `mybatis-plus-spring-boot3-starter` → `mybatis-plus-spring-boot4-starter`
   - `spring-boot-jdbc` / `spring-boot-jackson` / `spring-boot-cache` / `spring-boot-http-converter` / `spring-boot-validation` 拆出独立 jar
7. **Druid 包名换**:`com.alibaba.druid.spring.boot3.autoconfigure` → `com.alibaba.druid.spring.boot4.autoconfigure`
8. **Spring Data Redis 序列化器从 `Jackson2JsonRedisSerializer` 改 `GenericJacksonJsonRedisSerializer`**(Jackson 3 配套)
9. **Redis 端用独立 JsonMapper + 启 `defaultTyping` + 白名单**(`com.basic.*` / `java.util.*` / `java.lang.*` / `java.time.*`),
   Web 端 JsonMapper **不带** defaultTyping(否则 API 返回值会被包成 `[类名, {...}]` 数组,前端 JS 解析不了)
10. **显式声明 `CacheManager` Bean**(SB 4.1.1 缺口:`spring-boot-cache` 拆出来后 `RedisCacheConfiguration` 不再自动装配)
11. **`@JsonMapperBuilderCustomizer` 拆到独立 `BigNumberModuleConfig` 类**,
    避免与 `ResourcesConfig`(`RepeatSubmitInterceptor` → `RedisCache` → `RedisTemplate` → `JsonMapper`)形成循环依赖
12. **`Jackson2ObjectMapperBuilderCustomizer` 整段删除**(SB 4 已删),时区由 `application.yml` 的 `spring.jackson.time-zone: GMT+8` 配
13. **Spring Framework 7 API 迁移**:`getPatternsCondition().getPatterns()` → `getPatternValues()`(返回 `Set<String>`)
14. **MyBatis-Plus 3.5.13+ 显式依赖**:`mybatis-plus-extension` + `mybatis-plus-jsqlparser-4.9`(后者 SB 4 默认 `<optional>true</optional>` 不传递)
15. **Springdoc 2.9.0 → 3.1.1**
16. **Quartz 改用 `spring-boot-starter-quartz` 自动装配**,不手写 `quartz:2.5.2` 依赖
17. **web-mp 移动端** 与 `springboot3` 一致(JS 版)

`springboot4` 与 `springboot3` 的**唯一差异**就是上面 1–16 的后端升级;前端代码、
SQL 脚本、业务功能、目录结构**完全相同**。

---

## 三、相对 `springboot3` 的具体改动(后端)

### 3.1 父 `pom.xml`

```xml
<!-- properties -->
<java.version>17 → 21</java.version>
<spring-boot.version>3.5.16 → 4.1.1</spring-boot.version>
<mybatis-spring-boot.version>3.0.5 → 4.1.0</mybatis-spring-boot.version>
<mybatis-plus-boot-starter.version>3.5.7 → 3.5.17</mybatis-plus-boot-starter.version>
<pagehelper.boot.version>2.1.1 → 4.1.1</pagehelper.boot.version>
<springdoc.version>2.9.0 → 3.1.1</springdoc.version>

<!-- dependencyManagement -->
- spring-boot-starter-web
+ spring-boot-starter-webmvc
- spring-boot-starter-aop
+ spring-boot-starter-aspectj
- druid-spring-boot-3-starter
+ druid-spring-boot-4-starter
- mybatis-plus-spring-boot3-starter
+ mybatis-plus-spring-boot4-starter
+ mybatis-plus-extension                       ← 新增(显式)
+ mybatis-plus-jsqlparser-4.9                  ← 新增(SB 4 默认不传递)
+ jackson-datatype-jsr310                      ← 新增(SB 4 starter-json 不再传递)
+ tools.jackson.core:jackson-databind          ← 新增(Jackson 3 替换 Jackson 2)
```

### 3.2 7 个子模块 `pom.xml`

- 7 个子模块的 `parent.version` 从 `3.9.2` → **`4.0.0`**
- `basic-common/pom.xml`:
  - `mybatis-plus-spring-boot3-starter` → `spring-boot4-starter`
  - `com.fasterxml.jackson.core:jackson-databind` → `tools.jackson.core:jackson-databind`
- `basic-framework/pom.xml`:
  - `spring-boot-starter-web` → `spring-boot-starter-webmvc`
  - `spring-boot-starter-aop` → `spring-boot-starter-aspectj`
  - `druid-spring-boot-4-starter`
  - 新增:`mybatis-plus-extension` / `mybatis-plus-jsqlparser-4.9` / `jackson-datatype-jsr310`
- `extend-quartz/pom.xml`:手写 `quartz:2.5.2` 依赖**移除**,改用 `spring-boot-starter-quartz`(SB 自动装配 `SchedulerFactoryBean`,RAMJobStore 避免 PG `varchar(1)` 字段雷)
- `extend-generator/pom.xml`:`druid-spring-boot-4-starter`

### 3.3 `application-dev.yml` / `application.yml`

- `datasource.druid.statViewServlet.enabled: false` → `true`(启用 Druid 监控页)
- `spring.data.redis.*` 已在 `springboot3` 分支改好,本次不动

---

## 四、Java 代码关键修改

### 4.1 Jackson 2 → Jackson 3 全面切换(**本次最大坑**)

| 概念 | Jackson 2 (SB 3) | Jackson 3 (SB 4) |
|---|---|---|
| ObjectMapper | `com.fasterxml.jackson.databind.ObjectMapper` | `tools.jackson.databind.ObjectMapper` |
| JSON 异常 | `JsonProcessingException` extends `IOException` | `JacksonException` extends `RuntimeException` |
| 序列化器基类 | `JsonSerializer<T>` / `StdSerializer<T>` | `ValueSerializer<T>` / `StdSerializer<T>` |
| 上下文参数 | `SerializerProvider` | `SerializationContext` |
| 模块类 | `com.fasterxml.jackson.databind.Module` | `tools.jackson.databind.JacksonModule` |
| `SimpleModule` | `com.fasterxml.jackson.databind.module.SimpleModule` | `tools.jackson.databind.module.SimpleModule` |
| HttpMessageConverter | `MappingJackson2HttpMessageConverter`(Jackson 2) | `JacksonJsonHttpMessageConverter`(Jackson 3) |
| `@JsonFormat` / `@JsonIgnore` / `@JsonInclude` annotation | `com.fasterxml.jackson.annotation.*` | **没变**(annotation 包名还是 `com.fasterxml.jackson`) |
| Module 注册 API | `ObjectMapper.registerModule(Module)` **运行时注册** | `MapperBuilder.addModule(JacksonModule)` **构建时注册** |
| defaultTyping | `mapper.activateDefaultTyping(ptv, ...)` | `builder.activateDefaultTyping(ptv, ...)`(在 `JsonMapper$Builder` 上) |
| `NumberSerializer` | `ser.std.NumberSerializer` 存在 | **不存在**,需自己写 |

> ⚠️ **重要**:Jackson 3 的 Module **只能在构建时通过 `JsonMapper.builder().addModule(...)` 注册**;
> 运行时调 `ObjectMapper.registerModule(...)` 不存在。
> Spring Boot 4 提供 `JsonMapperBuilderCustomizer` 钩子来介入默认 mapper 的构建。

### 4.2 `JsonUtils.java`(Jackson 3)

- `com.fasterxml.jackson.core.JsonProcessingException` → `tools.jackson.core.JacksonException`
- `com.fasterxml.jackson.core.type.TypeReference` → `tools.jackson.core.type.TypeReference`
- `com.fasterxml.jackson.databind.ObjectMapper` → `tools.jackson.databind.ObjectMapper`
- 所有 `catch (IOException e)` 改 `catch (JacksonException e)`(因为 Jackson 3 异常是 `RuntimeException`,不是 `IOException`)

### 4.3 `BigNumberSerializer.java`(Jackson 3)

原继承 `com.fasterxml.jackson.databind.ser.std.NumberSerializer`(Jackson 2 旧类)——
**Jackson 3 的 `ser/std/` 已经没有 `NumberSerializer`**,改成直接继承
`tools.jackson.databind.ValueSerializer<Number>`,自己写 `serialize()` 逻辑:

```java
public class BigNumberSerializer extends ValueSerializer<Number> {
    public static final BigNumberSerializer INSTANCE = new BigNumberSerializer();
    @Override
    public void serialize(Number value, JsonGenerator gen, SerializationContext ctxt)
            throws JacksonException {
        if (value.longValue() > MIN_SAFE_INTEGER && value.longValue() < MAX_SAFE_INTEGER) {
            gen.writeNumber(value.longValue());
        } else {
            gen.writeString(value.toString());
        }
    }
}
```

### 4.4 `ResourcesConfig.java`(精简 + 拆配置)

- 删除 `Jackson2ObjectMapperBuilderCustomizer` Bean(SB 4 已删)
- 改用 `JsonMapperBuilderCustomizer`(Spring Boot 4 官方推荐)注册 BigNumber/ToString Module
- 关键:这个 Customizer **必须**放在**独立类**(`BigNumberModuleConfig`)里,否则会和
  `ResourcesConfig`(依赖 `RepeatSubmitInterceptor` → `RedisCache` → `RedisTemplate` →
  `JsonMapper` Bean 创建)形成**循环依赖**

### 4.5 新增 `BigNumberModuleConfig.java`

```java
@Configuration
public class BigNumberModuleConfig {
    @Bean
    public JsonMapperBuilderCustomizer bigNumberJsonMapperBuilderCustomizer() {
        JacksonModule module = new SimpleModule()
                .addSerializer(Long.class, BigNumberSerializer.INSTANCE)
                .addSerializer(Long.TYPE, BigNumberSerializer.INSTANCE)
                .addSerializer(BigInteger.class, BigNumberSerializer.INSTANCE)
                .addSerializer(BigDecimal.class, ToStringSerializer.instance);
        return builder -> builder.addModule(module);
    }
    public static class ToStringSerializer extends StdScalarSerializer<Object> { ... }
}
```

> Web 端不加 defaultTyping,只加 Module;Redis 端在 `RedisConfig` 里单独构造带 defaultTyping 的 mapper。

### 4.6 `RedisConfig.java`

- 删 fastjson2(若依的方案),改用 `GenericJacksonJsonRedisSerializer`(Spring Data Redis 4.1.1 配套 Jackson 3 的序列化器)
- 构造独立的 `buildRedisJsonMapper()`,启 `activateDefaultTyping` + 白名单 PTV(`com.basic.*`、`java.util.*`、`java.lang.*`、`java.time.*`)
- `redisTemplate` 注入 `JsonMapper`(改用)→ 改成内部构造,避免循环
- 显式声明 `CacheManager` Bean(SB 4 缺口)

### 4.7 `DruidConfig.java`

- `com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceBuilder` → `com.alibaba.druid.spring.boot4.autoconfigure.DruidDataSourceBuilder`
- `com.alibaba.druid.spring.boot3.autoconfigure.properties.DruidStatProperties` → `com.alibaba.druid.spring.boot4.autoconfigure.properties.DruidStatProperties`

### 4.8 `BasicApplication.java`

- import 改 `org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration` →
  `org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration`(SB 4 拆到 `spring-boot-jdbc`)

### 4.9 `LoginUser.java`

- 改 `getAuthorities()` 不返回 null(Spring Security 7 严格化,return null 直接 NPE):

```java
@Override
public Collection<? extends GrantedAuthority> getAuthorities() {
    return permissions == null || permissions.isEmpty()
        ? Collections.emptyList()
        : permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
}
```

### 4.10 `PermitAllUrlProperties.java`

- `info.getPatternsCondition().getPatterns()` → `info.getPatternValues()`(Spring Framework 7 弃用老 API,返回 `Set<String>`)

### 4.11 `MyBatis-Plus` 3.5.13+ 包名微调

- 拦截器 `BlockAttackInnerInterceptor` / `PaginationInnerInterceptor` 等迁到 `mybatis-plus-jsqlparser-4.9` 子模块,
  且官方标 `<optional>true</optional>`,**不传递**,必须在使用方显式引入
- 显式依赖:`mybatis-plus-extension` + `mybatis-plus-jsqlparser-4.9`
- 使用 `IService` / `ServiceImpl` 时 import 不变(`com.baomidou.mybatisplus.extension.service.IService`)

### 4.12 `RedisCache.java`(处理升级时脏数据)

`getCacheObject` 加 try-catch,反序列化失败时**自动删失效 key** + 返回 null(处理升级时 Redis 脏数据):

```java
public <T> T getCacheObject(final String key) {
    try {
        return redisTemplate.opsForValue().get(key);
    } catch (Exception e) {
        log.error("从 Redis 读取 key={} 失败, 自动删除失效 key", key, e);
        try { redisTemplate.delete(key); } catch (Exception ignore) { }
        return null;
    }
}
```

### 4.13 `TokenService.java`

- `getLoginUser` 的 catch 块不再静默,记录错误日志 + 主动 `redisCache.deleteObject(getTokenKey(uuid))` 清旧 key

### 4.14 `ApplicationConfig.java`

- 删除 `Jackson2ObjectMapperBuilderCustomizer` Bean 整段(SB 4 已删;时区由 `application.yml` 的 `spring.jackson.time-zone: GMT+8` 配)

### 4.15 `JacksonConfig.java`

- **整个文件删除**,Jackson 逻辑迁移到 `BigNumberModuleConfig` + `RedisConfig`

---

## 五、本项目**不引 fastjson2** 的决策

若依 SB 4.1.0 用了 `FastJson2JsonRedisSerializer`(基于 `com.alibaba.fastjson2:fastjson2:2.0.64`)。

**我们没用**,因为:

- fastjson2 历史漏洞:CVE-2023-52425(≤ 2.0.34 RCE)、CVE-2022-25845 系列。
  2.0.64 修了大半但仍有零星 CVE 报告
- 默认 autoType 模式,白名单若配错就出 RCE
- SB 4 生态已经为 Jackson 3 适配好了,直接用 `GenericJacksonJsonRedisSerializer` 更省事

---

## 六、与 `springboot3` 共享的部分

下面这些**与 `springboot3` 完全一致**,本分支**不重复**:

| 维度 | 内容 |
|---|---|
| **前端 web-ui** | 与 `springboot3` 一致(JS 版,Vue 3.5.26 + Element Plus 2.13.1 + Vite 6.4.1) |
| **前端 web-mp** | 与 `springboot3` 一致(uni-app 3 + uview-plus 3 + Vite 5.2.8,JS 版) |
| **业务功能** | 锁屏解锁、SQL bug 修复等与 master 共同的改动全部继承 |
| **数据库脚本** | `file-sql/basicdb-vue3.sql`(34 张表 + 5 函数,`basicdb` schema) |
| **目录结构** | 7 个后端 maven module + `web-ui/` + `web-mp/` |
| **`application.yml` 数据源** | PostgreSQL `jdbc:postgresql://localhost:5432/basicdb?currentSchema=basicdb` |
| **Spring Security 5→6 改动** | `WebSecurityConfigurerAdapter` → `SecurityFilterChain` Bean、`antMatchers` → `requestMatchers` 等(SB 3 时代已做) |
| **Jakarta EE 改造** | `javax.*` → `jakarta.*` 全部 import(SB 3 时代已做) |
| **springfox → springdoc** | `SwaggerConfig` 重写(SB 3 时代已做) |

---

## 七、运行前 checklist

- [x] **JDK 21 安装** ★★ 与 master / `springboot3` 都不一样
- [x] Node.js + pnpm(前端)
- [x] `cd web-ui && pnpm install` 拉依赖(PC 端)
- [x] `cd web-mp && pnpm install` 拉依赖(移动端)
- [ ] `pnpm dev` 验证 PC 端启动(`web-ui`)
- [ ] `pnpm dev:h5` 验证移动端 H5 启动(`web-mp`)
- [x] 7 个子模块 `parent.version` 一致为 **`4.0.0`** ★★ 与 master / `springboot3` 都不一样
- [x] IDE 工程的 Project SDK 设为 **JDK 21**,Language level **21** ★★ 与 master / `springboot3` 都不一样
- [x] `mvn clean compile` 0 ERROR
- [x] `mvn package -DskipTests` 0 ERROR
- [ ] 应用启动成功
- [ ] admin 登录成功(PC / 移动端)
- [ ] Druid 监控页 `/druid/index.html` 可访问(`statViewServlet.enabled: true`)
- [ ] Redis 缓存读写正常(验证 Jackson 3 序列化 + defaultTyping)

---

## 八、未做 / 后续可优化

> 这些事项是本分支**没做完**或**没做**的,留作下一阶段(也就是 `springboot4_TypeScript` 分支)处理。

- [ ] 前端 TypeScript 化(已经在 `springboot4_TypeScript` 分支做了)
- [ ] `WebMvcConfigurer` 接口在 Spring Framework 7 已 `@Deprecated`,
  长期要看官方替代(目前没有 GA 的接口继承方案,只能继续用)
- [ ] `fastjson2` 是否真不引:看业务缓存对象复杂度,如果都是基础类型,Jackson 3 + defaultTyping + 白名单足够;
  如果包含复杂对象集合,可能需要 `Jackson2ObjectMapperBuilder` 配 `WRITE_DATES_AS_TIMESTAMPS` 之类
- [ ] Springdoc 3.1.1 是否完整兼容 SB 4:目前用起来没崩,但 OpenAPI 3.1 / Springdoc 2.7 vs 3.0+ 跟 SB 4 的细节未全部测过
- [ ] MyBatis-Plus 3.5.17 的 LambdaQuery 链式调用是否完整(目前只测了基础 CRUD)
- [ ] `Quartz` RAMJobStore 切换到 JDBC JobStore 用 PG(若依做法是用 RAMJobStore 避免 PG `varchar(1)` 字段雷;
  如果业务需要持久化,得自己改)
- [ ] 生产环境 Druid 监控 `statViewServlet` 必须关或换强密码 + IP 白名单
- [ ] `MyBatis-Plus 3.5.7 → 3.5.17` 跨多个小版本的中间变更未逐个核对,只测了用到的 API
- [ ] `springdoc 2.9 → 3.1` 跨多个小版本的 OpenAPI Schema 兼容性未验证
- [ ] `pagehelper 2.1 → 4.1` 大版本跳变,分页参数兼容性是否完全保留未测
- [ ] 集成 Flowable 流程引擎(已经在 `springboot2-extend-flowable` 分支做了)

---

## 九、其他分支(参考)

本仓库其他分支(切到对应分支看其 README):

| 分支 | 做什么 |
|---|---|
| `master` | RuoYi-Vue 3.8.2 **最小改造基线**(后端 SB 2.5.14 / JDK 11,前端 JS,加 web-mp,**kaptcha 字符验证码**) |
| `springboot2_TypeScript` | `master` + 前端两端(`web-ui` / `web-mp`)**全量 TypeScript 化**;后端保持 SB 2.5.14 |
| `springboot2-tianai-captcha` | `master` + **tianai-captcha 1.5.0** 行为验证码(后端仍 SB 2.5.14) |
| `springboot2-extend-flowable` | `master` + **Flowable 6.8.0 流程引擎**(新增 `extend-flowable` 子模块),schema 改 `basicdb_flowable`(86 表 + 2 视图) |
| `springboot3` | 后端 SB 2.5.14 → **3.5.16**(JDK 11 → 17),Jakarta EE + Security 6 + springdoc 2.9;前端保持 JS |
| `springboot3-tianai-captcha` | `springboot3` + **tianai-captcha 1.5.2** 行为验证码 |
| `springboot3_TypeScript` | `springboot3` + 前端 TS 化 |
| `springboot3-extend-flowable` | `springboot3` + `springboot2-extend-flowable` 的 Flowable 集成 |
| `springboot4` | 后端 SB 3.5.16 → **4.1.1**(JDK 17 → **21**),**Jackson 3**(`tools.jackson.*`)+ Security 7 + Spring Framework 7 + springdoc 3.1 + MP 3.5.17;前端 **JS 不动**、DB 34 表不动 |
| `springboot4_TypeScript` | `springboot4` + 前端 TS 化(**技术栈最完整**) |
| `springboot4-extend-flowable` | `springboot4` + `springboot2-extend-flowable` 的 Flowable 集成 |
| `springboot4-extend-channel` 🟢 | **当前分支**:`springboot4` + 渠道集成 **POC**(`extend-wecom` 调 [WxJava 4.8.6.B](https://github.com/binarywang/WxJava) 全量封装企业微信 / 公众号 / 小程序 / 开放平台;`extend-alipay` 调 [alipay-sdk-java 4.40.1044.ALL](https://github.com/alipay/alipay-sdk-java-all) 全量封装支付宝 OpenAPI;access_token 走 Redis 持久化;提供 `/wecom/health/{agentId}` 与 `/alipay/health/{appId}` 两个联通性验证端点)。**未实现任何业务功能**;后续按 M1~M6 阶段推进,详见 `docs/渠道集成-架构方案.md` |

> 命名约定:`springboot{N}` = 后端 SB 大版本基线;`_TypeScript` = 该基线 + 前端 TS 化;
> `-tianai-captcha` = 该基线 + 行为验证码(替代 kaptcha);`-extend-flowable` = 该基线 + Flowable 流程引擎;
> `-extend-channel` = 该基线 + 微信生态(企业微信/公众号/小程序)+ 支付宝渠道集成。
> 本分支 `springboot4-extend-channel` = `springboot4` + 渠道对接层(POC),**不引入** Flowable / 行为验证码、**不做**前端 TS 化、**未实现**业务功能。

---

## 十、移动端 `web-mp/`

> 本分支与 `springboot3` 同样包含 `web-mp/` 移动端,**没有做 TS 化**(`vite.config.js`、`.js` 源文件),
> 与 `springboot4_TypeScript` 分支有别。本节是 `web-mp/README.md` 的摘要,
> 完整内容以子目录的 README 为准。

### 10.1 技术栈

| 项 | 版本 |
|---|---|
| uni-app | 3.0 (alpha,`@dcloudio/uni-app@3.0.0-4060620250520001`) |
| Vue | 3.4.21(**组合式 API** `<script setup>`) |
| Vite | 5.2.8 |
| UI | **uview-plus** 3.3.62(uView 的 Vue3 版本,组件名带 `up-` 前缀) |
| 状态管理 | Pinia 2.0.36 |
| 加密 | jsencrypt(对接后端 RSA 登录) |
| 时间 | dayjs 1.11.x |
| 国际化 | vue-i18n 9.13.1(已引入,词条待补) |
| 语言 | **JavaScript**(无 TypeScript) |
| 包管理 | pnpm |

### 10.2 与 web-ui / 后端的关系

- 与 `web-ui/`(PC 端)**同级目录**,**不**是 web-ui 的子项目,各自独立维护
- 不参与 maven 编译(`pom.xml` 的 module 列表里没有 `web-mp`)
- 对接后端:**直接打**本仓库的 `basic-admin-api`(Spring Boot 4.1 + Spring Security 7 + Jakarta EE 6),
  接口复用 `/login` / `/logout` / `/captchaImage` / `/getInfo` / `/getRouters` 等
- 默认账号:`admin / admin123`

### 10.3 目录结构

```
web-mp/
├── index.html                 # H5 入口(vite 用)
├── vite.config.js             # Vite 配置
├── package.json
├── config.js                  # 全局配置(双份,同步到 src/)
├── manifest.json              # 应用清单(双份,同步到 src/)
├── pages.json                 # 页面注册(双份,同步到 src/)
├── sync-config.cjs            # 双份同步脚本
├── scripts/
│   └── fixup-mp-components.cjs  # 微信小程序组件 fixup 脚本
└── src/
    ├── main.js                # 入口:createSSRApp + Pinia + 显式注册 up- 组件
    ├── App.vue                # 根组件(@import uview-plus 全局样式)
    ├── uni.scss               # 全局 SCSS 变量入口
    ├── pages/                 # 业务页面
    │   ├── login/index.vue
    │   ├── index/index.vue    # 首页(TabBar)
    │   ├── workbench/index.vue
    │   ├── user/index.vue
    │   └── common/{notice,profile}/...
    ├── api/                   # 接口层(login / system.* ...)
    ├── store/                 # Pinia(user / permission / app)
    ├── utils/                 # config / auth / request / dict / jsencrypt
    ├── directive/permission.js  # v-hasPermi / v-hasRole
    ├── permission.js          # 全局导航守卫
    └── static/                # 静态资源(TabBar 图标等)
```

### 10.4 关键设计

- **登录流程**:`/captchaImage` → `/login`(RSA 公钥加密密码)→ `/getInfo` → `/getRouters`
- **Token 存储**:`uni.getStorageSync('Basic-Token')`
- **请求封装**:`utils/request.js` 实现拦截器(401 跳登录)
- **权限指令**:`v-hasPermi`(uniapp 3 编译时不支持自定义指令,用 `v-if="hasPermi([...])"` 替代)
- **双份 manifest/pages**:CLI build 找 `src/manifest.json`,dev 找根 `manifest.json`,
  所以两边各放一份,`sync-config.cjs` 同步
- **环境配置**:`src/config.js` 维护 `baseUrl`(默认 `http://localhost:8080`,
  H5 通过 `vite.config.js` 代理到 `/dev-api` → 8080;小程序/真机需改成局域网 IP)

### 10.5 运行

```bash
cd web-mp
pnpm install

# 开发
pnpm dev:h5            # 浏览器访问 http://localhost:8081
pnpm dev:mp-weixin     # 用微信开发者工具打开 dist/dev/mp-weixin
pnpm dev:app           # 编译到 unpackage/dist/dev/app

# 生产构建
pnpm build:h5          # 产物在 dist/build/h5
pnpm build:mp-weixin   # 产物在 dist/build/mp-weixin
pnpm build:app
```

### 10.6 待办(摘自子 README)

- [ ] 接入动态菜单(`/getRouters` → pages.json)
- [ ] 国际化(`vue-i18n` 已引入,词条待补)
- [ ] 消息推送 / WebSocket
- [ ] 修改密码页 + 个人中心编辑
- [ ] 锁屏(手势/PIN)
- [ ] TabBar 图标(`src/static/tabbar/` 需放 6 张 81x81 png)

---

## 十一、数据库脚本 (`file-sql/basicdb-vue3.sql`)

> 本分支运行所需的 **PostgreSQL 全库结构 + 初始化数据** 都在 `file-sql/basicdb-vue3.sql`。
> 其它 `file-sql/*.sql` 是历史中间产物,本分支**不直接使用**;真正要执行的就是这一个。

### 11.1 脚本基本信息

| 项 | 值 |
|---|---|
| 文件路径 | `file-sql/basicdb-vue3.sql` |
| 来源 | `pg_dump` 导出(导出自 PostgreSQL 15.1,导出工具 pg_dump 17.0) |
| 数据库角色 | 所有对象 `OWNER TO system`(导入前**必须**先有 `system` 角色) |
| Schema | `basicdb`(脚本里所有表/函数/序列都在该 schema 下) |
| 表数量 | **34 张**(`gen_*` 2 + `qrtz_*` 11 + `sys_*` 21) |
| 函数数量 | 5 个(`date_format` / `exec` / `find_in_set` / `ifnull` / `tabledef`) |
| 数据加载方式 | `COPY ... FROM stdin;`(原生 psql `\copy` 流式导入,**不**走 `INSERT`) |
| PostgreSQL 要求 | **>= 15.1** |

### 11.2 表清单(共 34 张,均位于 `basicdb` schema)

#### 代码生成(`gen_*`,2 张)

| 表 | 用途 |
|---|---|
| `gen_table` | 代码生成业务表配置 |
| `gen_table_column` | 代码生成字段配置 |

#### 定时任务(`qrtz_*`,11 张)

`qrtz_blob_triggers` / `qrtz_calendars` / `qrtz_cron_triggers` / `qrtz_fired_triggers` /
`qrtz_job_details` / `qrtz_locks` / `qrtz_paused_trigger_grps` / `qrtz_scheduler_state` /
`qrtz_simple_triggers` / `qrtz_simprop_triggers` / `qrtz_triggers`

#### 业务系统(`sys_*`,21 张)

| 表 | 用途 |
|---|---|
| `sys_config` | 全局参数 |
| `sys_demo` | 演示样例 |
| `sys_dept` | 部门 |
| `sys_dict_data` | 字典值 |
| `sys_dict_type` | 字典类型 |
| `sys_files` | 文件 |
| `sys_i18n` | 国际化文案 |
| `sys_job` | 定时任务 |
| `sys_job_log` | 定时任务日志 |
| `sys_logininfor` | 登录日志 |
| `sys_menu` | 菜单(含 `is_resource` / `show_tags` / `menu_name_en` / `menu_name_zh_hant` 等扩展列) |
| `sys_notice` | 通知公告 |
| `sys_oper_log` | 操作日志 |
| `sys_post` | 岗位 |
| `sys_role` | 角色 |
| `sys_role_dept` | 角色-部门关联 |
| `sys_role_menu` | 角色-菜单关联 |
| `sys_sms_message` | 短信发送记录 |
| `sys_user` | 用户 |
| `sys_user_post` | 用户-岗位关联 |
| `sys_user_role` | 用户-角色关联 |

### 11.3 恢复步骤(全新数据库)

```bash
# 1) 建 system 角色和 basicdb 库(SQL 脚本本身不建角色,必须先建好)
psql -U postgres -h localhost -c "CREATE ROLE system LOGIN PASSWORD 'system';"
psql -U postgres -h localhost -c "CREATE DATABASE basicdb OWNER system;"

# 2) 导入 schema + 数据
psql -U system -h localhost -d basicdb -f file-sql/basicdb-vue3.sql

# 3) 验证(应返回 34)
psql -U system -h localhost -d basicdb -c \
  "SELECT count(*) FROM information_schema.tables WHERE table_schema='basicdb';"
```

如果本地 PostgreSQL 实例没有 `system` 角色,直接执行第 2 步会报
`ERROR: role "system" does not exist`,补一步 1) 即可。

### 11.4 重新备份成同款脚本

```bash
pg_dump -U system -h localhost -d basicdb \
  --schema=basicdb --no-owner --no-privileges \
  --file=file-sql/basicdb-vue3.sql
```

- 加 `--no-owner --no-privileges` 后,导出的脚本**不再**带 `OWNER TO system`、可移植到任意环境。
- 不加这两个开关,导出结果与本仓库现存的 `basicdb-vue3.sql` 格式一致。
- 脚本里所有 `data` 段都用了 `COPY ... FROM stdin;`,导入端必须用 `psql`。

### 11.5 默认账号

| 用户名 | 密码(明文) | 备注 |
|---|---|---|
| `admin` | `admin123` | 超级管理员,登录后请立刻改密 |

要重置可执行同目录的 `reset-admin-password.sql`。

### 11.6 与应用连接配置

```yaml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/basicdb?currentSchema=basicdb
    username: system
    password: system
```

> `currentSchema=basicdb` 必须加,否则 MyBatis 找不到 `sys_user` / `sys_menu` 等表
> (脚本里所有表都建在 `basicdb` schema 下,不是 `public`)。

### 11.7 其它 `file-sql/*.sql` 说明(本分支**不直接使用**,留作历史)

| 文件 | 角色 |
|---|---|
| `basic-sql-2023-03-02.sql` | 早期 MySQL 5.7 全库脚本,**非 PostgreSQL**,本分支不要混用 |
| `quartz.sql` | 单独的 Quartz 建表脚本,**已被 `basicdb-vue3.sql` 内置的 11 张 `qrtz_*` 表覆盖** |
| `ry_20210908.sql` | RuoYi 3.8.2 原始 MySQL 全库脚本,**非 PostgreSQL**,对照用 |
| `reset-admin-password.sql` | 把 `admin` 密码重置回 `admin123` 的小工具,按需手动跑 |
| `reset-all-passwords.sql` | 把**所有**用户密码统一重置回默认的小工具,慎用 |
| `fix-sys-menu-component.sql` | 修 `sys_menu.component` 路径里 `system` 写错的,按需手动跑 |
| `create-sys-notice-read.sql` | 补建 `sys_notice_read`(用户已读记录)表,按需手动跑 |

> 一句话:**运行本项目,只跑 `basicdb-vue3.sql` 这一个文件就够了**。
> (注:`springboot2-extend-flowable` 分支已经把上述 7 个历史脚本里的部分文件删掉了,
> 本分支**仍保留**。)

---

## 十二、目录速查

```
base-postgresql-vue3/
├── pom.xml                              # 父 pom(7 个后端子模块,无 web-mp)
├── README.md                            # 本文档
├── ry.sh / ry.bat                       # RuoYi 启动脚本(原版遗留)
├── basic-admin-api/                     # Spring Boot 启动模块(SB 4.1.1)
├── basic-admin-core/                    # 业务核心(system 模块)
├── basic-framework/                     # 框架配置(Security 7 / Jakarta 6 / Jackson 3)
├── basic-common/                        # 通用工具/基类
├── business-core/                       # 业务扩展点
├── extend-quartz/                       # Quartz 定时任务扩展(用 spring-boot-starter-quartz)
├── extend-generator/                    # 代码生成扩展
├── extend-flowable/                     # (仅 springboot2-extend-flowable 分支)
├── file-sql/                            # SQL 脚本
├── file-bin/                            # 二进制资源(jar 内置)
├── file-doc/                            # 文档资源(jar 内置)
├── web-ui/                              # PC 端前端(Vue3 + Element Plus,JavaScript)
└── web-mp/                              # 移动端前端(uni-app3 + uview-plus,JavaScript)
```

> **本分支与 `springboot3` 的差异**:仅后端从 SB 3.5.16(JDK 17 / Security 6 / Jackson 2 / Jakarta 5)
> 升级到 SB 4.1.1(JDK 21 / Security 7 / **Jackson 3** / Jakarta 6);
> 前端两端、SQL 脚本、`web-mp` 移动端、目录结构**完全相同**。
