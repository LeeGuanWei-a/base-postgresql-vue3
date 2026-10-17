# Basic Admin (Spring Boot 4.1.1 + Flowable 8.0.0 + Vue 3 + Element Plus)

基于若依 RuoYi-Vue 3.9.2 改造。本分支在 `springboot4`(后端 SB 4.1.1 / JDK 21)的基础上,**新增 `extend-flowable` 子模块集成 Flowable 8.0.0 流程引擎**,并配齐流程建模 / 发起 / 审批的前端页面与菜单权限。

> 当前分支:`springboot4-extend-flowable`。
> 与 `springboot4` 的**唯一区别**:多出 `extend-flowable` 模块 + 工作流前端页面/菜单 + 数据库 schema 换成 `basicdb_flowable8`;`springboot4` 自身的升级(Jackson 3 / Spring Security 7 / Jakarta 6 等)本分支**全部继承**,本文前半部分即这些内容。
> 仓库另有 `master` / `master-extend-flowable` / `springboot2_TypeScript` / `springboot3` / `springboot3_TypeScript` / `springboot4` / `springboot4_TypeScript` 等分支。

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

`springboot4-extend-flowable` = **`springboot4`(后端 SB 4.1.1 / JDK 21)+ `extend-flowable` 流程引擎模块**。

具体而言,本分支在 `springboot4` 之上只做了一件事:

> **新增 `extend-flowable` 子模块,集成 Flowable 8.0.0 流程引擎**,
> 并把数据库 schema 换成 `basicdb_flowable8`、补上工作流的前端页面(建模 / 发起 / 审批 / 流程图 / 迁移)与 1500～1699 菜单权限。

`springboot4` 那部分(JDK 17 → 21、SB 3.5.16 → 4.1.1、Jackson 2 → 3 等)**原样继承**,改动清单如下:

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

### 3.2 8 个子模块 `pom.xml`

- 8 个子模块的 `parent.version` 从 `3.9.2` → **`4.0.0`**
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
| **目录结构** | 8 个后端 maven module + `web-ui/` + `web-mp/` |
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
- [x] 8 个子模块 `parent.version` 一致为 **`4.0.0`** ★★ 与 master / `springboot3` 都不一样
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
- [x] 集成 Flowable 流程引擎(Flowable **8.0.0** + 自定义表单,使用手册见 [docs/流程引擎使用说明.md](./docs/流程引擎使用说明.md))

---

## 九、其他分支(参考)

本仓库其他分支(切到对应分支看其 README):

| 分支 | 做什么 |
|---|---|
| `master` | RuoYi-Vue 3.8.2 **最小改造基线**(后端 SB 2.5.14,JDK 11,前端 JS,加 web-mp) |
| `master-extend-flowable` | master + **Flowable 6.8.0 流程引擎**集成(新增 `extend-flowable` 子模块),数据库从 MySQL 迁到 PostgreSQL(schema `basicdb_flowable`,86 张表 + 2 视图);并增加流程建模/部署/发起/审批/退回/转办/委派/加签/抄送等业务 |
| `springboot2_TypeScript` | master + 前端两端(`web-ui` / `web-mp`)**全量 TypeScript 化**;后端保持 SB 2.5.14 |
| `springboot3` | 后端 SB 2.5.14 → **3.5.16**(JDK 11 → 17),Jakarta EE + Spring Security 6 + springdoc;前端保持 JS |
| `springboot3_TypeScript` | `springboot3` + 前端 TS 化 |
| `springboot4_TypeScript` | `springboot4` + 前端 TS 化 |
| `springboot4-extend-flowable` | **本分支**:`springboot4` + **Flowable 8.0.0** 流程引擎(新增 `extend-flowable` 子模块),含自定义表单(vform3)、bpmn-js 流程设计器、全链路工作流页面与菜单;数据库 schema `basicdb_flowable8` |

> 本分支 `springboot4` ≈ `springboot3` + 后端 SB 升级,可以视作"`springboot3` 的 SB 4 升级版"。

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

> 本分支运行所需的 **PostgreSQL 全库结构 + 初始化数据** 都在 `file-sql/basicdb-vue3.sql`(schema `basicdb_flowable8`,含 Flowable 引擎表与工作流业务表)。
> `file-sql/` 下现在只剩它和 `reset-admin-password.sql` 两个文件,其余历史脚本已清理(见 §11.7)。

### 11.1 脚本基本信息

| 项 | 值 |
|---|---|
| 文件路径 | `file-sql/basicdb-vue3.sql` |
| 来源 | `pg_dump` 导出(导出自 PostgreSQL 15.1,导出工具 pg_dump 17.0) |
| 数据库角色 | 所有对象 `OWNER TO system`(导入前**必须**先有 `system` 角色) |
| Schema | **`basicdb_flowable8`**(脚本里所有表/函数/序列都在该 schema 下,不是 `public`) |
| 表数量 | **建表语句 90 张**;应用启动后实际 **92 张**(多出的 `list_table` / `list_column` 是后补的,见 §11.2) |
| 表构成 | Flowable 引擎 `act_*` **39** + 事件/批处理 `flw_*` **7** + 工作流业务 `wf_*` **9** + `tbl_flow_extend_procinst` **1** + 若依 `sys_*` **21** + `qrtz_*` **11** + `gen_*` **2** + `list_*` **2** |
| 函数数量 | 5 个(`date_format` / `exec` / `find_in_set` / `ifnull` / `tabledef`) |
| 数据加载方式 | `COPY ... FROM stdin;`(原生 psql `\copy` 流式导入,**不**走 `INSERT`) |
| PostgreSQL 要求 | **>= 15.1** |

### 11.2 表清单(共 92 张,均位于 `basicdb_flowable8` schema)

#### Flowable 引擎表(`act_*`,39 张)

| 前缀 | 张数 | 用途 |
|---|---|---|
| `act_re_*` + `act_ge_*` + `act_procdef_info` | 6 | 部署、流程定义、模型、通用资源(含 BPMN XML / 流程图 PNG) |
| `act_ru_*` | 13 | **运行中**:执行流、任务、变量、身份链接、各类作业(定时器 / 死信 / 挂起) |
| `act_hi_*` | 10 | **历史**:实例、任务、节点、变量、意见、附件、身份链接 |
| `act_id_*` | 9 | Flowable 自带 IDM 表。本项目已 `flowable.idm.enabled=false`,这些表**存在但不用** |
| `act_evt_log` | 1 | 引擎事件日志 |

#### 工作流业务表(`wf_*` 9 张 + `tbl_flow_extend_procinst` 1 张 + `flw_expression`)

`wf_category` / `wf_category_org_link` / `wf_comment_info` / `wf_copy` / `wf_deploy_form` /
`wf_form` / `wf_form_category` / `wf_form_template` / `wf_task_form` / `tbl_flow_extend_procinst` / `flw_expression`

> 每张表的字段含义、对应界面与排查 SQL,见 [docs/流程引擎使用说明.md 第十一章](./docs/流程引擎使用说明.md#十一表数据对应关系)。

#### 事件与批处理(`flw_*`,7 张)

`flw_channel_definition` / `flw_event_definition` / `flw_event_deployment` / `flw_event_resource` /
`flw_ru_batch` / `flw_ru_batch_part` / `flw_expression`(最后这张被本项目复用为「表达式字典」)

#### 若依基础表(`sys_*`,21 张)

`sys_config` / `sys_demo` / `sys_dept` / `sys_dict_data` / `sys_dict_type` / `sys_files` / `sys_i18n` /
`sys_job` / `sys_job_log` / `sys_logininfor` / `sys_menu` / `sys_notice` / `sys_notice_read` /
`sys_oper_log` / `sys_post` / `sys_role` / `sys_role_dept` / `sys_role_menu` / `sys_user` /
`sys_user_post` / `sys_user_role`

> `sys_menu` 含 `is_resource` / `show_tags` / `menu_name_en` / `menu_name_zh_hant` 等扩展列;
> 工作流菜单在 1500～1699 区段(1 目录 + 11 页面 + 117 按钮)。

#### 定时任务(`qrtz_*`,11 张)

`qrtz_blob_triggers` / `qrtz_calendars` / `qrtz_cron_triggers` / `qrtz_fired_triggers` /
`qrtz_job_details` / `qrtz_locks` / `qrtz_paused_trigger_grps` / `qrtz_scheduler_state` /
`qrtz_simple_triggers` / `qrtz_simprop_triggers` / `qrtz_triggers`

#### 代码生成与增补(`gen_*` 2 + `list_*` 2)

| 表 | 用途 |
|---|---|
| `gen_table` | 代码生成业务表配置 |
| `gen_table_column` | 代码生成字段配置 |
| `list_table` / `list_column` | 后补的列表配置表(**不在 dump 的 90 张里**,当前库中存在) |

### 11.3 恢复步骤(全新数据库)

```bash
# 1) 建 system 角色和库(SQL 脚本本身不建角色,必须先建好)
psql -U postgres -h localhost -c "CREATE ROLE system LOGIN PASSWORD 'system';"
psql -U postgres -h localhost -c "CREATE DATABASE \"basicdb-vue3\" OWNER system;"

# 2) 导入 schema + 数据(脚本内含 CREATE SCHEMA basicdb_flowable8,无需手工建 schema)
psql -U system -h localhost -d basicdb-vue3 -f file-sql/basicdb-vue3.sql

# 3) 验证(应返回 90)
psql -U system -h localhost -d basicdb-vue3 -c \
  "SELECT count(*) FROM information_schema.tables WHERE table_schema='basicdb_flowable8';"
```

如果本地 PostgreSQL 实例没有 `system` 角色,直接执行第 2 步会报
`ERROR: role "system" does not exist`,补一步 1) 即可。

> 库名里带 `-`,在 SQL 里引用要加双引号(`"basicdb-vue3"`);JDBC URL 里直接写 `basicdb-vue3` 即可。

### 11.4 重新备份成同款脚本

```bash
pg_dump -U system -h localhost -d basicdb-vue3 \
  --schema=basicdb_flowable8 --no-owner --no-privileges \
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
    url: jdbc:postgresql://localhost:5432/basicdb-vue3?useSSL=false&currentSchema=basicdb_flowable8
    username: system
    password: system
  flowable:
    database-schema: basicdb_flowable8   # 必须与 currentSchema 一致
```

> `currentSchema=basicdb_flowable8` 必须加,否则 MyBatis 找不到 `sys_user` / `sys_menu` 等表
> (脚本里所有表都建在 `basicdb_flowable8` schema 下,不是 `public`)。
> Flowable 的 `spring.flowable.database-schema` 也要写成同一个值,否则引擎建表/查表会落到别的 schema。

### 11.7 `file-sql/` 目录现状

| 文件 | 角色 |
|---|---|
| `basicdb-vue3.sql` | **运行本项目只需要这一个**(含 `basicdb_flowable8` 全部 90 张表结构与初始化数据) |
| `reset-admin-password.sql` | 把 `admin` 密码重置回 `admin123` 的小工具,按需手动跑 |

> 与 `master-extend-flowable` / 早期分支不同,本分支已经把 `basic-sql-2023-03-02.sql` / `quartz.sql` /
> `ry_20210908.sql` / `reset-all-passwords.sql` / `fix-sys-menu-component.sql` / `create-sys-notice-read.sql`
> 等历史中间脚本删掉了(`sys_notice_read` 表已并入主脚本);`flowable8-*.sql` 系列补丁脚本也已合并进主脚本。

> 一句话:**运行本项目,只跑 `basicdb-vue3.sql` 这一个文件就够了**。

---

## 十二、Flowable 流程引擎集成

> 本章是**集成概览**;面向使用者的操作手册(怎么建模型、怎么配审批人、边界事件/迁移/清盘怎么用)见
> **[docs/流程引擎使用说明.md](./docs/流程引擎使用说明.md)**。

### 12.1 是什么

本分支在 `springboot4` 基础上新增 `extend-flowable` 子模块,集成 **Flowable 8.0.0**,并配套一套自研的流程定制层与前端页面:

| 层 | 内容 |
|---|---|
| 引擎 | Flowable 8.0.0(`flowable-spring-boot-starter-process`),只用它的 `RepositoryService` / `RuntimeService` / `TaskService` / `HistoryService` |
| 定制层 | `extend-flowable/`(223 个 Java 文件):模型/部署/流程/任务/表单/分类/表达式/边界事件/迁移/清盘 |
| 前端 | `web-ui/src/views/basic-extend/workflow/**`(21 个 vue)+ `web-ui/src/plugins/package/**`(bpmn-js 设计器、属性面板) |
| 数据库 | schema **`basicdb_flowable8`**,92 张表(`act_*` 39 + `flw_*` 7 + `wf_*` 9 + `tbl_flow_extend_procinst` 1 + `sys_*` 21 + `qrtz_*` 11 + `gen_*` 2 + `list_*` 2) |

### 12.2 与 `master-extend-flowable`(Flowable 6.8.0)的差异

| 维度 | `master-extend-flowable` | 本分支 |
|---|---|---|
| 引擎 | Flowable 6.8.0 | **Flowable 8.0.0** |
| 平台 | Spring Boot 2.5.14 + JDK 11 | Spring Boot **4.1.1** + JDK **21** |
| schema | `basicdb_flowable`(86 张表 + 2 视图) | **`basicdb_flowable8`**(92 张表) |
| 模型存储 | 设计器 **JSON**(`flowable-json-converter`) | Flowable 7 起移除该转换器,模型编辑器源改为 **BPMN XML**(`WfModelSaveXmlService` 写、`WfModelQueryXmlService` 读) |
| 建模器表 | 有 `act_de_*` | **无 `act_de_*`**,模型只在 `act_re_model` + `act_ge_bytearray` |
| 前端目录 | `views/workflow/**` | 按业务域重组为 **`views/basic-extend/workflow/**`** |
| 前端依赖 | 旧版 bpmn-js | **bpmn-js 17.11.1** + `bpmn-js-properties-panel 5.28` + **vform3-builds 3.0.10** |

### 12.3 模块结构(`extend-flowable/`)

```
extend-flowable/src/main/java/com/basic/
├── constant/             # 节点扩展属性常量(ExtensionConstant)
├── enm/                  # 流程/意见枚举
├── flowable/             # 引擎级封装
│   ├── common/           #   常量(ProcessConstants)、枚举(边界事件类型、流程状态、表单类型)
│   ├── config/           #   引擎启动校验
│   ├── flow/             #   流程图生成(自定义画布)、节点推导(FindNextNodeUtil)、Flowable 工具集
│   ├── listener/         #   全局事件监听、到期监听、用户任务监听
│   └── utils/            #   模型/表单/任务/安全 XML/ID 工具
└── workflow/             # 业务层
    ├── controller/       #   13 个 Controller(模型/部署/流程/任务/表单/分类/表达式/边界/迁移/清盘)
    ├── service/          #   接口 + impl + wfModel(保存/部署/激活/删除)+ wfProcessInfo/wfProcessTask 细粒度子服务
    ├── service/migrate/  #   运行中实例迁移(WfProcessMigratePlan / Result / Exception)
    ├── cmd/              #   Flowable Command:动态注入用户任务、添加意见、回滚(RollbackCmd + 6 种策略)
    ├── domain/           #   实体 / BO / VO
    ├── mapper/           #   MyBatis-Plus Mapper(含直接操作 act_* 的 SQL)
    └── handler|builder   #   多实例处理、动态任务构建
```

### 12.4 能力清单

| 能力 | 说明 | 权限串(摘要) |
|---|---|---|
| 流程分类 / 模型 / 表单 / 表达式 | 增删改查与树形结构 | `workflow:category:*` / `workflow:model:*` / `workflow:form:*` / `workflow:expression:*` |
| 建模即部署 | 设计器保存 BPMN XML → 合规校验 → 自动部署 | `workflow:model:save` / `deploy` |
| 发起 / 审批 | 同意、驳回、退回、转办、委派、前加签、后加签、会签回退、终止、撤回、跳过节点、变更处理人 | `workflow:process:*` |
| 抄送 | 随审批动作提交,「抄送我的」查看与标记已读 | `workflow:process:copyList` / `readCopy` |
| 边界事件 | timer / message / signal / error / escalation / cancel / compensation / conditional 8 种;4 个外部触发接口 | `workflow:boundary:trigger` / `query` |
| 流程迁移 | 把运行中实例迁到新流程定义(自动镜像 / 手动指定节点),事务内失败回滚 | `workflow:process:migrate` |
| 流程清盘 | 一键清空全部流程数据(46 张表),两步确认 + 确认短语 | `workflow:process:clearAll` |
| 废除 / 删除 | 终止 + 物理删除历史与业务数据 | `workflow:process:abolishProcess` / `deleteProcessByIds` |

### 12.5 关键配置与坑

```yaml
spring:
  flowable:
    database-schema-update: true
    history-level: audit
    check-process-definitions: false
    database-schema: basicdb_flowable8   # 必须与 JDBC currentSchema 一致
    idm:
      enabled: false                     # Flowable 7/8 的正确键,别写 flowable.common.app.idm-enabled
    form-field-validation-enabled: false
  main:
    allow-circular-references: true      # extend-flowable 里 Wf*Service 相互依赖
```

| 坑 | 说明 |
|---|---|
| 配置键写错不报错 | Flowable 的键以 `spring-configuration-metadata.json` 为准,写错的键被**静默忽略**,以日志与表结构为准 |
| `act_id_*` 表仍会建 | 即使 `idm.enabled: false`,历史版本或首次初始化仍可能建出 `act_id_*`(9 张),不影响运行 |
| 模型是 BPMN XML | Flowable 8 没有 json-converter,旧分支导出的「模型 JSON」不能直接导入,要按 BPMN 2.0 XML 处理 |
| 表单快照 | 审批读的是 `wf_deploy_form`,改了 `wf_form` 必须重新部署才对新实例生效 |

---

## 十三、目录速查

```
base-postgresql-vue3/
├── pom.xml                              # 父 pom(8 个后端子模块,无 web-mp)
├── README.md                            # 本文档
├── docs/                                # 项目文档
│   └── 流程引擎使用说明.md               # ★ Flowable 8 使用手册
├── ry.sh / ry.bat                       # RuoYi 启动脚本(原版遗留)
├── basic-admin-api/                     # Spring Boot 启动模块(SB 4.1.1)
├── basic-admin-core/                    # 业务核心(system 模块)
├── basic-framework/                     # 框架配置(Security 7 / Jakarta 6 / Jackson 3)
├── basic-common/                        # 通用工具/基类
├── business-core/                       # 业务扩展点
├── extend-quartz/                       # Quartz 定时任务扩展(用 spring-boot-starter-quartz)
├── extend-generator/                    # 代码生成扩展
├── extend-flowable/                      # ★ Flowable 8 流程引擎(本分支新增)
├── file-sql/                            # SQL 脚本(只需 basicdb-vue3.sql)
├── file-bin/                            # 二进制资源(jar 内置)
├── file-doc/                            # 文档资源(jar 内置)
├── web-ui/                              # PC 端前端(Vue3 + Element Plus + bpmn-js + vform3,JavaScript)
└── web-mp/                              # 移动端前端(uni-app3 + uview-plus,JavaScript)
```

> **本分支与 `springboot4` 的差异**:多出 `extend-flowable` 模块(含 13 个 Controller、流程设计与审批页面、
> 1500～1699 菜单区段)与 `docs/流程引擎使用说明.md`;数据库 schema 换成 `basicdb_flowable8`。
> `springboot4` 自身的后端升级(JDK 21 / SB 4.1.1 / Security 7 / Jackson 3 / Jakarta 6)本分支完全一致。
