# Basic Admin (Spring Boot 4.1.1 + Vue 3 + Element Plus)

基于若依 RuoYi-Vue 3.9.2 改造,从 **Spring Boot 3.5.16** 升级到 **Spring Boot 4.1.1** 的实战记录。

> 上一次 commit `a4c182a 升级到springboot3` 已经完成了 SB 2.7 → SB 3.5.16 的升级(JDK 17、Jakarta EE 9+、Spring Security 6 等)。
> **本次升级** 是 SB 3.5.16 → SB 4.1.1。

---

## 前端说明

本仓库前端为 **JavaScript 版**(无 TypeScript):

- 框架:Vue 3.5.26 + Element Plus 2.13.1
- 构建:Vite 6.4.1
- 代码:纯 `.js` / `.vue`,**无 `typescript` 依赖**(package.json 不含 `typescript`)
- 仓库另有 **TypeScript 版**前端(在 `D:\idea\agent-keycloak\` 其他子目录),本次升级**未涉及**

> 如果你看到的是 TS 版的代码,可能拉错分支/目录;本 README 仅描述当前仓库的 JS 版。

---

## 一、升级前后对照(基于 `git show a4c182a:pom.xml`)

| 组件 | SB 3.5.16 (上次,commit a4c182a) | SB 4.1.1 (本次) |
|---|---|---|
| JDK | 17 | **21** |
| Spring Boot | 3.5.16 | **4.1.1** |
| Spring Framework | 6.x | **7.0.9** |
| Spring Security | 6.x | **7.1.1** |
| Spring Data Redis | 3.x | **4.1.1** |
| MyBatis-Spring-Boot | 3.0.5 | **4.1.0** |
| MyBatis-Plus | 3.5.7 | **3.5.17** |
| PageHelper starter | 2.1.1 | **4.1.1** |
| Druid | 1.2.28 | **1.2.28**(用 `druid-spring-boot-4-starter`,包名 `boot3.autoconfigure` → `boot4.autoconfigure`) |
| Springdoc-openapi | 2.9.0 | **3.1.1** |
| Lombok | 1.18.46 | **1.18.46** |
| **Jackson** | 2.x (`com.fasterxml.jackson`) | **3.1.5** (`tools.jackson`,全包名换) |

---

## 二、Spring Boot 4 相对 3 的主要破坏性变更(本次踩坑)

### 2.1 Starter 重命名 / 子模块拆分

| SB 3 | SB 4 | 说明 |
|---|---|---|
| `spring-boot-starter-web` | `spring-boot-starter-webmvc` | 重命名 |
| `spring-boot-starter-aop` | `spring-boot-starter-aspectj` | 重命名 |
| `druid-spring-boot-3-starter` | `druid-spring-boot-4-starter` | 包名 `com.alibaba.druid.spring.boot3.autoconfigure` → `com.alibaba.druid.spring.boot4.autoconfigure` |
| `mybatis-plus-spring-boot3-starter` | `mybatis-plus-spring-boot4-starter` | 同上 |
| `spring-boot-starter-json` 传递的 Jackson 模块 | **需显式引入**各种子模块 | Jackson 拆分严重 |
| `spring-boot-starter-quartz` | 同名不变 | 自动装配 `SchedulerFactoryBean`,**无需手写** |
| `spring-boot-starter-data-redis` | 同名,但**不自动装配 `RedisCacheManager`** | 见 2.7 |
| `Jackson2ObjectMapperBuilder` 等 | **完全删除** | 改用 `JsonMapper.builder()` 或 `ObjectMapperBuilderCustomizer` |

Jackson 子模块 SB 4 拆出独立 jar:
- `spring-boot-jackson`(`JacksonAutoConfiguration`、`JacksonProperties`)
- `spring-boot-http-converter`(`MappingJackson2HttpMessageConverter`、`Jackson2HttpMessageConvertersConfiguration`)
- `spring-boot-cache`(`CacheAutoConfiguration`,**没有** `RedisCacheConfiguration` 自动装配)
- `spring-boot-jdbc`(`DataSourceAutoConfiguration`)
- `spring-boot-validation`(`HibernateValidator`)

### 2.2 Jackson 2 → Jackson 3 全面切换(**本次最大坑**)

SB 4 默认引入的是 **Jackson 3**,包名从 `com.fasterxml.jackson` 全部迁到 `tools.jackson`。

| 概念 | Jackson 2 (SB 3) | Jackson 3 (SB 4) |
|---|---|---|
| ObjectMapper | `com.fasterxml.jackson.databind.ObjectMapper` | `tools.jackson.databind.ObjectMapper` |
| JSON 异常 | `JsonProcessingException` extends `IOException` | `JacksonException` extends `RuntimeException` |
| 序列化器基类 | `JsonSerializer<T>` / `StdSerializer<T>` | `ValueSerializer<T>` / `StdSerializer<T>` |
| 上下文参数 | `SerializerProvider` | `SerializationContext` |
| 模块类 | `com.fasterxml.jackson.databind.Module` | `tools.jackson.databind.JacksonModule` |
| `SimpleModule` | `com.fasterxml.jackson.databind.module.SimpleModule` | `tools.jackson.databind.module.SimpleModule` |
| HttpMessageConverter | `MappingJackson2HttpMessageConverter`(Jackson 2) | **`JacksonJsonHttpMessageConverter`**(Jackson 3) |
| **`@JsonFormat` / `@JsonIgnore` / `@JsonInclude` annotation** | `com.fasterxml.jackson.annotation.*` | **没变**(annotation 包名还是 com.fasterxml.jackson) |
| Module 注册 API | `ObjectMapper.registerModule(Module)` **运行时注册** | `MapperBuilder.addModule(JacksonModule)` **构建时注册** |
| defaultTyping | `mapper.activateDefaultTyping(ptv, ...)` | `builder.activateDefaultTyping(ptv, ...)`(在 `JsonMapper$Builder` 上) |
| `NumberSerializer` | `ser.std.NumberSerializer` 存在 | **不存在**,需自己写 |

> ⚠️ **重要**:Jackson 3 的 Module **只能在构建时通过 `JsonMapper.builder().addModule(...)` 注册**;运行时调 `ObjectMapper.registerModule(...)` 不存在。Spring Boot 4 提供 `JsonMapperBuilderCustomizer` 钩子来介入默认 mapper 的构建。

### 2.3 Spring Security 7 严格化

```java
@Override
public Collection<? extends GrantedAuthority> getAuthorities() {
    // SB 3 / Security 6 还能 return null,只是会有警告
    // SB 4 / Security 7 直接 NPE
    return permissions == null || permissions.isEmpty()
        ? Collections.emptyList()
        : permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
}
```

### 2.4 Spring Framework 7 API 弃用

- `WebMvcConfigurer` 接口整体 `@Deprecated` → 仍能用(自动用,加 `@SuppressWarnings("deprecation")`),长期要看 `WebMvcConfigurer` 的新实现(尚未 GA)。
- `RequestMappingInfo.getPatternsCondition()` 弃用 → 改用 `getPatternValues()`,返回 `Set<String>`。

### 2.5 Servlet 5 → 6

- `jakarta.servlet:jakarta.servlet-api:5.x` → `6.1.0`。
- 对业务代码透明,基本不用改。

### 2.6 MyBatis-Plus 3.5.7 → 3.5.17 注意事项

- 拦截器 `BlockAttackInnerInterceptor`、`PaginationInnerInterceptor` 等迁到 `mybatis-plus-jsqlparser-4.9` 子模块且官方标 `<optional>true</optional>`,**不传递**,必须在使用方显式引入。
- 项目依赖里**显式**:
  ```xml
  <dependency>
      <groupId>com.baomidou</groupId>
      <artifactId>mybatis-plus-extension</artifactId>
  </dependency>
  <dependency>
      <groupId>com.baomidou</groupId>
      <artifactId>mybatis-plus-jsqlparser-4.9</artifactId>
  </dependency>
  ```
- 3.5.13+ 部分 API 还在 `spring.service` 子模块,使用 `IService` / `ServiceImpl` 时 import 不变(`com.baomidou.mybatisplus.extension.service.IService`)。

### 2.7 SB 4.1.1 不再自动装配 `RedisCacheManager`

`spring-boot-cache-4.1.1.jar` 的 `AutoConfiguration.imports` 只有:
```
CacheAutoConfiguration
CachesEndpointAutoConfiguration
CacheMetricsAutoConfiguration
```

**没有 `RedisCacheConfiguration`**!`@EnableCaching` 启动时找不到 `CacheManager` Bean 就报错。**必须**用户自己显式声明。

> **本项目之前其实也没在用 `@Cacheable`**,但因为 `@EnableCaching` 触发查找 CacheManager 失败,启动崩。SB 3 时代会自动装配 `RedisCacheManager`,SB 4 不会。

### 2.8 配置文件 `spring.redis` 改 `spring.data.redis`

SB 3 时代 `spring.redis.host` 已被警告,SB 4 直接**失效**。`application-dev.yml` 已从 `spring.redis.*` 迁到 `spring.data.redis.*`(commit a4c182a 时已改)。

---

## 三、Redis 序列化的 Jackson 3 适配(本次最大坑)

### 3.1 不能用 `Jackson2JsonRedisSerializer`

`org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer` 在 `spring-data-redis:4.1.1` 仍然存在但**绑定 Jackson 2 的 ObjectMapper**,跟 SB 4 默认 Jackson 3 不兼容。

### 3.2 改用 `GenericJacksonJsonRedisSerializer`

`org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer`(Jackson 3 时代新类)。

但**关键**:`GenericJacksonJsonRedisSerializer` 内部用 `JacksonObjectWriter.write(mapper, value)`,实际就是 `mapper.writeValueAsBytes(value)`,**不在内部添加任何类型信息**。完全依赖传入的 ObjectMapper 自己。

### 3.3 必须给 Redis 专用 JsonMapper 启用 defaultTyping

如果 ObjectMapper 没启用 defaultTyping,序列化时**不写 `@class` 字段**,反序列化时 Object → LinkedHashMap → 转 LoginUser **ClassCastException**。

**defaultTyping 副作用**:Web API 返回值也会被包成 `[类名, {...}]`,前端 JS 解析不了,**必须** Redis 用独立的 JsonMapper,Web 用默认 JsonMapper(不带 defaultTyping)。

### 3.4 本项目最终方案

**两个 ObjectMapper**:
- **Web 端**(默认 `JsonMapper` Bean):不带 defaultTyping,只通过 `JsonMapperBuilderCustomizer` 加 BigNumber Module,API 返回值正常 `{...}`。
- **Redis 端**(`RedisConfig.buildRedisJsonMapper()`):独立构造,带 defaultTyping,白名单限制在 `com.basic.*`、`java.util.*`、`java.lang.*`、`java.time.*`,避免反序列化 RCE 漏洞(参照若依白名单思路)。

### 3.5 清理脏数据

升级时 Redis 里**旧格式的缓存**(没 defaultTyping 时写入的普通对象)读不出来。要么手动 `DEL`,要么代码里**反序列化失败时自动删 key + 返回 null**:

```java
// RedisCache.getCacheObject
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

---

## 四、本项目**不引 fastjson2** 的决策

若依 SB 4.1.0 用了 `FastJson2JsonRedisSerializer`(基于 `com.alibaba.fastjson2:fastjson2:2.0.64`)。

**我们没用**,因为:
- fastjson2 历史漏洞:CVE-2023-52425(≤ 2.0.34 RCE)、CVE-2022-25845 系列。2.0.64 修了大半但仍有零星 CVE 报告。
- 默认 autoType 模式,白名单若配错就出 RCE。
- SB 4 生态已经为 Jackson 3 适配好了,直接用 `GenericJacksonJsonRedisSerializer` 更省事。

---

## 五、配置文件关键修改

### 5.1 `pom.xml`(父 pom)

```xml
<properties>
    <java.version>17 → 21</java.version>
    <spring-boot.version>3.5.16 → 4.1.1</spring-boot.version>
    <mybatis-spring-boot.version>3.0.5 → 4.1.0</mybatis-spring-boot.version>
    <mybatis-plus-boot-starter.version>3.5.7 → 3.5.17</mybatis-plus-boot-starter.version>
    <druid.version>1.2.28(不变)</druid.version>
    <pagehelper.boot.version>2.1.1 → 4.1.1</pagehelper.boot.version>
    <springdoc.version>2.9.0 → 3.1.1</springdoc.version>
    <lombok.version>1.18.46(不变)</lombok.version>
    <basic.version>4.0.0(不变)</basic.version>
</properties>
```

dependencyManagement 中:
- `spring-boot-starter-webmvc` 替代 `spring-boot-starter-web`
- `spring-boot-starter-aspectj` 替代 `spring-boot-starter-aop`
- `druid-spring-boot-4-starter` 替代 `druid-spring-boot-3-starter`
- `mybatis-plus-spring-boot4-starter` 替代 `-3-`
- 显式 `mybatis-plus-extension` + `mybatis-plus-jsqlparser-4.9`
- 显式 `jackson-datatype-jsr310`(SB 4 starter-json 不再传递)
- 显式 `tools.jackson.core:jackson-databind`(Jackson 3 替换 Jackson 2)

### 5.2 7 个子模块 `pom.xml`

全部 `parent.version` 改为 `4.0.0`(因 PowerShell 批量替换曾破坏 UTF-8,后改用 Edit 工具逐个改)。

### 5.3 `basic-common/pom.xml`

- `mybatis-plus-spring-boot3-starter` → `spring-boot4-starter`
- `com.fasterxml.jackson.core:jackson-databind` → `tools.jackson.core:jackson-databind`

### 5.4 `basic-framework/pom.xml`

- `spring-boot-starter-web` → `spring-boot-starter-webmvc`
- `spring-boot-starter-aop` → `spring-boot-starter-aspectj`
- `druid-spring-boot-3-starter` → `druid-spring-boot-4-starter`
- 新增:`mybatis-plus-extension` / `mybatis-plus-jsqlparser-4.9` / `jackson-datatype-jsr310`

### 5.5 `extend-quartz/pom.xml`

- 单独 `quartz:2.5.2` 依赖移除,改用 `spring-boot-starter-quartz`(SB 自动装配 SchedulerFactoryBean,RAMJobStore 避免 PG `varchar(1)` 字段雷)。

### 5.6 `extend-generator/pom.xml`

- `druid-spring-boot-3-starter` → `druid-spring-boot-4-starter`

### 5.7 `application-dev.yml`

- `statViewServlet.enabled: false` → `true`(启用 Druid 监控页)
- `spring.data.redis.*` 已在 commit a4c182a 改好,本次不动

---

## 六、Java 代码关键修改

### 6.1 `basic-common/.../utils/JsonUtils.java`(Jackson 3)

- `com.fasterxml.jackson.core.JsonProcessingException` → `tools.jackson.core.JacksonException`
- `com.fasterxml.jackson.core.type.TypeReference` → `tools.jackson.core.type.TypeReference`
- `com.fasterxml.jackson.databind.ObjectMapper` → `tools.jackson.databind.ObjectMapper`
- 所有 `catch (IOException e)` 改 `catch (JacksonException e)`(因为 Jackson 3 异常是 `RuntimeException`,不是 `IOException`)
- 删掉无用的 `import java.io.IOException`

### 6.2 `basic-framework/.../jackson/BigNumberSerializer.java`(Jackson 3)

原继承 `com.fasterxml.jackson.databind.ser.std.NumberSerializer`(Jackson 2 旧类)——
**Jackson 3 的 `ser/std/` 已经没有 `NumberSerializer`**,改成直接继承 `tools.jackson.databind.ValueSerializer<Number>`,自己写 `serialize()` 逻辑:
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

### 6.3 `basic-framework/.../config/ResourcesConfig.java`(精简 + 拆配置)

- 删除 `Jackson2ObjectMapperBuilderCustomizer` Bean(SB 4 已删)
- 改用 `JsonMapperBuilderCustomizer`(Spring Boot 4 官方推荐)注册 BigNumber/ToString Module
- 关键:这个 Customizer **必须**放在**独立类**(`BigNumberModuleConfig`)里,否则会和 `ResourcesConfig`(依赖 `RepeatSubmitInterceptor` → `RedisCache` → `RedisTemplate` → `JsonMapper` Bean 创建)形成**循环依赖**。

### 6.4 新增 `basic-framework/.../config/BigNumberModuleConfig.java`

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

### 6.5 `basic-framework/.../config/RedisConfig.java`

- 删 fastjson2(若依的方案),改用 `GenericJacksonJsonRedisSerializer`(Spring Data Redis 4.1.1 配套 Jackson 3 的序列化器)
- 构造独立的 `buildRedisJsonMapper()`,启 `activateDefaultTyping` + 白名单 PTV(`com.basic.*`、`java.util.*`、`java.lang.*`、`java.time.*`)
- `redisTemplate` 注入 `JsonMapper`(改用)→ 改成内部构造,避免循环
- 显式声明 `CacheManager` Bean(SB 4 缺口,见 2.7)

### 6.6 `basic-framework/.../config/DruidConfig.java`

- `com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceBuilder` → `com.alibaba.druid.spring.boot4.autoconfigure.DruidDataSourceBuilder`
- `com.alibaba.druid.spring.boot3.autoconfigure.properties.DruidStatProperties` → `com.alibaba.druid.spring.boot4.autoconfigure.properties.DruidStatProperties`

### 6.7 `basic-admin-api/.../BasicApplication.java`

- import 改 `org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration` → `org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration`

### 6.8 `basic-common/.../core/domain/model/LoginUser.java`

- 改 `getAuthorities()` 不返回 null(见 2.3)

### 6.9 `basic-framework/.../config/properties/PermitAllUrlProperties.java`

- `info.getPatternsCondition().getPatterns()` → `info.getPatternValues()`

### 6.10 `basic-admin-core/.../DemoService.java` + `SysFilesServiceImpl.java` 等

- import 改 `com.baomidou.mybatisplus.extension.service.IService` / `impl.ServiceImpl` → `com.baomidou.mybatisplus.extension.service.IService` / `impl.ServiceImpl`(3.5.13+ 包名微调)

### 6.11 `basic-common/.../core/redis/RedisCache.java`

- `getCacheObject` 加 try-catch,反序列化失败时**自动删失效 key** + 返回 null(处理升级时 Redis 脏数据)

### 6.12 `basic-framework/.../web/service/TokenService.java`

- `getLoginUser` 的 catch 块不再静默,记录错误日志 + 主动 `redisCache.deleteObject(getTokenKey(uuid))` 清旧 key

### 6.13 `ApplicationConfig.java`

- 删除 `Jackson2ObjectMapperBuilderCustomizer` Bean 整段(SB 4 已删;时区由 `application.yml` 的 `spring.jackson.time-zone: GMT+8` 配)

### 6.14 `JacksonConfig.java`

- **整个文件删除**,Jackson 逻辑迁移到 `BigNumberModuleConfig` + `RedisConfig`

---

## 七、对照参考:本地 RuoYi-Vue 4.1.0

`D:\idea\agent-keycloak\RuoYi-Vue` 是若依官方 SB 4.1.0 master 源码,作为本次升级的**第一参考**。

| 关注点 | 若依 4.1.0 真实做法 | 本项目最终做法 | 一致? |
|---|---|---|---|
| `JsonUtils` | 不用,改用 fastjson2 | 重写为 Jackson 3 版 | ❌(我们不用 fastjson2) |
| Redis 序列化 | `FastJson2JsonRedisSerializer` | `GenericJacksonJsonRedisSerializer` + Jackson 3 专用 mapper | ❌(理由见第四节) |
| `DruidConfig` | 84 行,只配数据源 + 过滤广告 Filter | 沿用若依风格 | ✅ |
| `RedisConfig` 显式 CacheManager | **没有**(`@EnableCaching` 但不写 CacheManager) | **显式声明**(SB 4.1.1 缺口) | ❌(必须) |
| `ResourcesConfig` | 72 行,无 Jackson 配置 | 精简到 addResourceHandlers/Interceptors/corsFilter | ✅ |
| `ApplicationConfig` | 19 行,极简 | 一致 | ✅ |
| `BigNumberSerializer` | **没有** | 保留(防 JS Long 精度丢失) | ❌(业务需要) |
| `ScheduleConfig` | 整文件被注释 | 用 `spring-boot-starter-quartz` 自动装配 | ✅ |
| `getAuthorities()` | 返回基于 permissions 的非 null 列表 | 一致 | ✅ |
| `PermitAllUrlProperties` | 用 `getPatternsCondition()`(老 API,可能报警告) | 改 `getPatternValues()`(新 API) | ✅(更稳) |

> 若依 SB 4.1.0 **没遇到** CacheManager 缺失问题可能是因为它**没启用** `@EnableCaching` 的某些路径,或者**没在测试时**触发启动校验。本项目在 commit a4c182a 启用了 `@EnableCaching`,SB 4 启动校验严格化,直接报缺 CacheManager。

---

## 八、踩坑时间线(本次升级流水账)

1. `IService` 找不到 → MyBatis-Plus 3.5.13+ 包名微调 → 改 import
2. `BlockAttackInnerInterceptor` 找不到 → 在 `mybatis-plus-jsqlparser-4.9` 子模块 → 显式引入
3. `DataSourceAutoConfiguration` 找不到 → SB 4 拆到 `spring-boot-jdbc` → 改 import
4. `JacksonProperties` 找不到 → SB 4 拆到 `spring-boot-jackson` → 改 import
5. `Jackson2ObjectMapperBuilderCustomizer` 不存在 → SB 4 已删 → 删 `ApplicationConfig` 时区 Bean
6. jsr310 找不到 → 加 `jackson-datatype-jsr310` 依赖
7. Druid 包名 → `boot3.autoconfigure` → `boot4.autoconfigure`
8. PowerShell 批量替换破坏 UTF-8 中文与 `$1` 反向引用 → 改用 `Edit` 工具逐个改
9. Scheduler Bean 找不到 → 改用 `spring-boot-starter-quartz` 自动装配
10. 登录 NPE(`getAuthorities() return null`)→ Spring Security 7 严格化 → 改非 null
11. `Could not read JSON: JsonToken.START_OBJECT` → Redis 用的 JsonMapper 没启 defaultTyping / 旧数据脏 → 启用 defaultTyping + 自动清失效 key
12. `[类名, {...}]` 数组污染 Web API → Web 端 JsonMapperBuilderCustomizer 不加 defaultTyping,Redis 端独立构造带 defaultTyping 的 mapper
13. 循环依赖(`resourcesConfig ↔ redisCache`)→ `JsonMapperBuilderCustomizer` 拆到独立 `BigNumberModuleConfig`
14. CacheManager 找不到 → SB 4.1.1 缺口 → 显式声明 `CacheManager` Bean
15. `/druid/login.html` 404 → `statViewServlet.enabled: false` → 改 `true`

---

## 九、运行前 checklist

- [x] JDK 21 安装(JetBrains Runtime 21.0.9 在 `C:\Users\admin\.jdks\ms-21.0.9`)
- [x] Maven 用 `D:\MavenRepository5` 本地仓库(`C:\Users\admin\.m2\settings.xml` 固定)
- [x] 7 个子模块 `parent.version` 一致为 `4.0.0`
- [x] IDE 工程的 Project SDK 设为 JDK 21,Language level 21
- [x] `mvn clean compile` 0 ERROR
- [x] `mvn package -DskipTests` 0 ERROR
- [x] 应用启动成功(`(♥◠‿◠)ﾉﾞ  基础平台启动成功`)
- [x] admin/admin123 登录成功
- [x] `/getInfo` 200
- [x] `/monitor/online/list` 在线用户列表正常
- [x] `/druid/login.html` 监控页可达(账号 `basic` / 密码 `123456`)

---

## 十、未做 / 后续可优化

- [ ] `WebMvcConfigurer` 接口在 Spring Framework 7 已 `@Deprecated`,长期要看官方替代(目前没有 GA 的接口继承方案,只能继续用)
- [ ] `fastjson2` 是否真不引:看业务缓存对象复杂度,如果都是基础类型,Jackson 3 + defaultTyping + 白名单足够;如果包含复杂对象集合,可能需要 `Jackson2ObjectMapperBuilder` 配 `WRITE_DATES_AS_TIMESTAMPS` 之类
- [ ] Springdoc 3.1.1 是否完整兼容 SB 4:目前用起来没崩,但 OpenAPI 3.1 / Springdoc 2.7 vs 3.0+ 跟 SB 4 的细节(比如 `OperationCustomizer` 钩子)未全部测过
- [ ] MyBatis-Plus 3.5.17 的 LambdaQuery 链式调用是否完整(目前只测了基础 CRUD)
- [ ] `Quartz` RAMJobStore 切换到 JDBC JobStore 用 PG(若依做法是用 RAMJobStore 避免 PG `varchar(1)` 字段雷;如果业务需要持久化,得自己改)
- [ ] 生产环境 Druid 监控 `statViewServlet` 必须关或换强密码 + IP 白名单
- [ ] `MyBatis-Plus 3.5.7 → 3.5.17` 跨多个小版本(3.5.7→3.5.10→3.5.13→3.5.17)的中间变更未逐个核对,只测了用到的 API
- [ ] `springdoc 2.9 → 3.1` 跨多个小版本的 OpenAPI Schema 兼容性未验证(比如 `@Schema` annotation 的 deprecated 方法)
- [ ] `pagehelper 2.1 → 4.1` 大版本跳变,分页参数(`pageNum` / `pageSize` / `orderBy`)兼容性是否完全保留未测
- [ ] `druid 1.2.28`(版本号相同)内部 API 在 SB 3 → SB 4 之间是否有破坏性变更未细查
