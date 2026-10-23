# 基础平台(base-postgresql-vue3) 需求文档(PRD) — `springboot4` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot4`** 分支(全栈:后端 + web-ui + web-mp)
> 前置依赖:《架构方案》v0.1
> 本分支 = **`springboot3` 基线 + 后端 SB 3.5.16 → 4.1.1 升级(JDK 17 → 21)**。
> **业务功能需求、接口契约、数据库、前端代码与 `springboot3` 完全一致**;本分支**唯一需求级差异 = 后端技术栈升级(核心是 Jackson 2 → 3)**。本文档不重复业务细节与 SB3 升级细节,只展开「SB 4 升级」专题。

---

## 1. 文档信息与版本

- 当前版本:**v0.1** — 初稿,基于 README + 代码扫描抽取
- 变更历史

| 版本 | 日期 | 变更 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `springboot3` PRD 为基线,新增「§6.10 后端 SB4 升级需求」(8 小节:版本矩阵 / Jackson 3 / Security 7 / starter 4 / Quartz / JSON 库决策 / Redis / 数据库)+ 场景 S11~S15 + AC-25~AC-40 + 待定项 A-14~A-22 |

---

## 2. 背景与目标

### 2.1 为什么做

`springboot3` 分支已把后端升到 SB 3.5.16 / JDK 17,但还有两个驱动继续往上:

- **JDK 基线**:公司标准环境已到 JDK 21(LTS);JDK 17 属于"能用但不是最新 LTS"
- **框架代际**:Spring Framework 7 / Security 7 / **Jackson 3** 是 SB 4 的基石;Jackson 3 修掉了 2.x 的一批历史包袱(异常体系、模块注册、不可变 builder),越早迁移成本越低

因此本分支做**第二次后端体系化升级**:JDK 17 → 21、SB 3.5.16 → 4.1.1、Jackson 2 → 3、Security 6 → 7。

**关键约束**:升级**不得改变任何对外行为** —— 接口路径、入参、出参、权限字符、数据库结构、业务逻辑全部保持不变;**前端两端零改动**。

### 2.2 目标清单

| 目标 | 成功标准 |
|---|---|
| G1~G6(继承 `master`) | RBAC / 定时任务 / 代码生成 / PC+移动双端 / 接口兼容 / PostgreSQL —— 见 `master` PRD §2.2 |
| G7~G12(继承 `springboot3`) | JDK 17 + SB 3.5.16 / Jakarta / springdoc 2.9 / 契约零变更 / starter 变体 / 配置迁移 —— 已交付 |
| **G13(本分支)后端升级到 JDK 21 + SB 4.1.1** | `java.version=21`、`spring-boot.version=4.1.1`;JDK 21 下 `mvn clean compile` 0 ERROR;应用可启动 |
| **G14(本分支)Jackson 2 → 3 全量迁移** | 核心/数据绑定全部 `tools.jackson.*`;**`com.fasterxml.jackson.*` 仅允许出现在 `annotation` 子包**;`grep` 校验通过 |
| **G15(本分支)Module 构建时注册** | 所有 `JacksonModule` 通过 `JsonMapper.builder().addModule()` 或 `JsonMapperBuilderCustomizer` 注册;**无运行时 `registerModule` 调用** |
| **G16(本分支)Security 7 严格化适配** | `getAuthorities()` 返回空集合而非 `null`;`@EnableMethodSecurity` 生效;`SecurityFilterChain` 正常 |
| **G17(本分支)starter 全量换 SB4 变体** | Druid `-boot-4-starter`(包名 `boot4.autoconfigure`);MP `spring-boot4-starter`;MyBatis-Spring-Boot 4.1.0 |
| **G18(本分支)Quartz 改 starter** | 用 `spring-boot-starter-quartz` 自动装配 `SchedulerFactoryBean`;移除手写 `quartz:2.5.2` 依赖;任务可正常启停 |
| **G19(本分支)API 契约零变更 + 前端零改动** | 所有 HTTP 接口与 `springboot3` 逐项一致;`git diff springboot3 -- web-ui web-mp` 为空 |

### 2.3 非目标(本分支不做)

- 前端 TypeScript 化(走 `springboot4_TypeScript`)
- 业务流程引擎(走 `springboot4-extend-flowable`)
- 行为验证码(走 `springboot3-tianai-captcha` 系列)
- 多租户 / SaaS 化
- SSO 联邦
- 微服务拆分 / K8s 化 / 灰度发布
- 国际化完整词条
- **引入 fastjson2**(明确决策:**不引**,统一 Jackson 3)
- **数据库结构变更**(本分支零变更)
- **Quartz 切 JDBC JobStore**(本分支用 RAMJobStore,M4 可选)
- **`WebMvcConfigurer` 废弃替代**(Spring Framework 7 无 GA 替代)
- **GraalVM 原生镜像**

---

## 3. 术语表

| 术语 | 含义 |
|---|---|
| RBAC | Role-Based Access Control |
| 数据权限 | 按「本人 / 本部门 / 本部门及子 / 全部 / 自定义」过滤 |
| 锁屏 | 前端手动锁定,需重新输入密码解锁;不销毁 Token |
| Kaptcha | 后端图形验证码,**本分支仍是 kaptcha 2.3.3** |
| `basicdb` | PostgreSQL schema |
| `qrtz_*` / `gen_*` | Quartz 11 张 / 代码生成 2 张 |
| uview-plus | uView 的 Vue3 版本,组件前缀 `up-` |
| Jakarta EE | 包名 `jakarta.*`(⬆️ 继承 `springboot3`) |
| **Jackson 3** | Jackson 新大版本:核心/数据绑定移到 `tools.jackson.*`;**注解仍在 `com.fasterxml.jackson.annotation.*`** |
| **`JacksonException`** | Jackson 3 的统一异常,**继承 `RuntimeException`**(不再受检) |
| **`ValueSerializer<T>`** | Jackson 3 的序列化器基类(替代 `JsonSerializer<T>`) |
| **`SerializationContext`** | Jackson 3 的序列化上下文(替代 `SerializerProvider`) |
| **`JacksonModule`** | Jackson 3 的模块基类(替代 `databind.Module`) |
| **`JsonMapper` / `JsonMapper.Builder`** | Jackson 3 的入口;Module 只能通过 builder **构建时注册** |
| **`JsonMapperBuilderCustomizer`** | SB 4 提供的钩子,用于定制自动配置的 `JsonMapper` |
| **`JacksonJsonHttpMessageConverter`** | Jackson 3 的 HTTP 消息转换器(替代 `MappingJackson2HttpMessageConverter`) |
| **`defaultTyping`** | 多态类型序列化;Jackson 3 在 `builder` 上激活,需配 `PolymorphicTypeValidator` 白名单 |
| **RAMJobStore** | Quartz 内存 JobStore;重启丢任务(本分支采用) |
| **`spring-boot-starter-quartz`** | SB4 的 Quartz starter,自动装配 `SchedulerFactoryBean` |

---

## 4. 用户角色与使用场景

### 4.1 角色

| 角色 | 关键动作 | 频率 |
|---|---|---|
| **超级管理员(`admin`)** | 全部功能 | 高 |
| **业务管理员** | 受数据权限约束的 CRUD | 中 |
| **审计员** | 仅看日志 | 低 |
| **业务运营(PC / 移动)** | 登录 / 通知 / 字典查询 | 高 |
| **开发者** | 代码生成(**产出需手工改 jakarta + springdoc**)| 中 |
| **后端开发者(本分支重点)** | 维护升级后工程:Jackson 3 API、Security 7、starter 4、Quartz starter | 高 |

### 4.2 关键场景走查

> **S1~S10** 与 `master` / `springboot3` 一致(登录浏览、角色赋权、代码生成、移动端登录、锁屏、定时任务、SB3 升级回归、Swagger 迁移、starter 选型、生成器 jakarta 修正),详见 `docs/springboot3-需求文档.md` §4.2。下表为本分支新增/变化的场景。

#### 🔶 场景 S11:升级后首次启动与全链路回归(本分支核心)

1. 本地安装 **JDK 21**,IDE Project SDK / Language level 设为 21
2. `mvn clean compile` → **0 ERROR**(Jackson 2/3 混用会在此报错)
3. `psql -f file-sql/basicdb-vue3.sql` 恢复 34 张表(**与升级前完全一致**)
4. **建议先清空 Redis**(旧 Jackson 2 序列化的缓存对象 Jackson 3 读不了,见 §6.10.7)
5. `mvn spring-boot:run -pl basic-admin-api` → 启动成功
6. `/swagger-ui/index.html` 正常(springdoc 3.1);`/v3/api-docs` 返回合法 OpenAPI
7. `admin / admin123` 登录 → 菜单/权限/路由正常
8. 移动端 H5 登录 → 正常(前端零改动)
9. 抽查:用户 CRUD / 角色赋权 / 字典缓存 / 定时任务启停 / 代码生成 / 文件上传下载

#### 🔶 场景 S12:Jackson 2 → 3 迁移(开发者视角,最大坑)

1. 旧代码 `import com.fasterxml.jackson.databind.ObjectMapper` → 改 **`tools.jackson.databind.ObjectMapper`**
2. 旧 `catch (IOException e)` → 改 **`catch (JacksonException e)`**(Jackson 3 异常是 `RuntimeException`,**不再被 `IOException` 捕获**)
3. 旧 `extends JsonSerializer<T>` → 改 **`extends ValueSerializer<T>`**;`serialize(T, JsonGenerator, SerializerProvider)` → `serialize(T, JsonGenerator, SerializationContext)`
4. 旧 `extends NumberSerializer` → **该类已删除**,改为 `ValueSerializer<Number>` 自写
5. 旧 `mapper.registerModule(m)` → **不存在**;改 `JsonMapper.builder().addModule(m).build()` 或在 Spring 里用 `JsonMapperBuilderCustomizer`
6. 旧 `mapper.activateDefaultTyping(...)` → 移到 **builder** 上
7. **注解不动**:`@JsonFormat` / `@JsonIgnore` / `@JsonIgnoreProperties` / `@JsonInclude` 仍是 `com.fasterxml.jackson.annotation.*`
8. 编译验证

#### 🔶 场景 S13:给 Spring 默认 JsonMapper 加自定义序列化器

1. **不能**用 `@Bean ObjectMapper`(会覆盖 SB4 默认配置,丢失其它定制)
2. **不能**在运行时 `registerModule`
3. 正确做法:实现 **`JsonMapperBuilderCustomizer`**,在 `customize(JsonMapper.Builder builder)` 里 `builder.addModule(...)`
4. **必须放在独立 `@Configuration` 类**(如 `BigNumberModuleConfig`)
5. 原因:若放在 `ResourcesConfig`,会形成
   `ResourcesConfig → RepeatSubmitInterceptor → RedisCache → RedisTemplate → JsonMapper` **循环依赖**,启动即失败
6. 编译 + 启动验证

#### 🔶 场景 S14:Security 7 的 `getAuthorities()` 严格化

1. 升级后启动报错 / 运行 NPE,栈指向 `getAuthorities()`
2. 根因:Security 7 要求 `getAuthorities()` **不得返回 `null`**
3. 修复:`LoginUser.getAuthorities()` 在无权限时返回 **`Collections.emptyList()`**
4. 验证:无权限用户登录 + 访问受保护接口 → 返回 403(而非 NPE)

#### 🔶 场景 S15:Quartz 从手写依赖改 starter

1. 旧:`extend-quartz/pom.xml` 显式依赖 `org.quartz-scheduler:quartz:2.5.2`
2. 新:`spring-boot-starter-quartz`(SB4 管理版本,自动装配 `SchedulerFactoryBean`)
3. 配置:`spring.quartz.*` 走 starter 约定;**JobStore 用 `RAMJobStore`(内存)**
4. 验证:新建 Cron → 启动 → 执行 → 日志;`sys_job` / `sys_job_log` 正常
5. ⚠️ **已知取舍**:`qrtz_*` 表存在但**不被使用**;重启后动态任务丢失(需持久化则切 JDBC JobStore,M4)

---

## 5. 总体需求

### 5.1 范围图(🔶 = 本分支;⬆️ = `springboot3` 继承)

```
                    ┌────────────────────────────────────────┐
                    │   平台能力(本仓库)                    │
                    │  ┌──────────┐  ┌───────────────────┐  │
                    │  │ PC Web   │  │ 移动 H5/小程序/App │  │
                    │  │ (Vue3)   │  │                   │  │
                    │  │ 🔶 零改动 │  │ 🔶 零改动         │  │
                    │  └────┬─────┘  └─────────┬─────────┘  │
                    │       │  HTTP/JSON       │            │
                    │  ┌────┴───────────────────┴─────────┐  │
                    │  │   Spring Boot 4.1.1              │  │
                    │  │  🔶 JDK 21                       │  │
                    │  │  🔶 Jackson 3(tools.jackson.*)  │  │
                    │  │  🔶 Security 7 / Framework 7     │  │
                    │  │  🔶 springdoc 3.1 / MP 3.5.17    │  │
                    │  └────┬─────────────────────────────┘  │
                    │       │  MyBatis-Plus 3.5.17           │
                    │  ┌────┴─────────────────────────────┐  │
                    │  │  PostgreSQL basicdb(34 表)      │  │
                    │  │  🔶 零改动                        │  │
                    │  └──────────────────────────────────┘  │
                    └────────────────────────────────────────┘
```

### 5.2 核心规则总表

> **业务规则**与 `master` / `springboot3` 一致(用户/角色/菜单/部门/岗位/字典/通知/定时任务/代码生成的 L1/L2 校验),详见 `docs/springboot3-需求文档.md` §5.2。下表为本分支**新增的升级相关规则**。

| 模块 | 关键字段 | L1 校验 | L2 校验 |
|---|---|---|---|
| **🔶 Jackson import** | import 语句 | 核心/数据绑定**必须** `tools.jackson.*` | `com.fasterxml.jackson.*` **仅允许** `annotation` 子包 |
| **🔶 Module 注册** | 序列化模块 | **禁止**运行时 `registerModule` | 必须 `JsonMapper.builder()` 或 `JsonMapperBuilderCustomizer` |
| **🔶 Customizer 落位** | 配置类 | **禁止**与 `RedisTemplate` 同链路 | 必须独立 `@Configuration`(防循环依赖) |
| **🔶 Security** | `getAuthorities()` | 返回值**不得为 `null`** | 空集合 `Collections.emptyList()` |
| **🔶 starter** | pom 依赖 | Druid 必须 `-boot-4-`;MP 必须 `spring-boot4-` | `mvn dependency:tree` 无 SB3 变体残留 |
| **🔶 JSON 库** | pom 依赖 | **禁止引入 fastjson2** | 统一 Jackson 3 |
| **🔶 Redis** | 序列化 | Jackson 3 + `defaultTyping` | 必须配 `PolymorphicTypeValidator` 白名单 |

---

## 6. 功能需求

> **业务功能需求**与 `master` **完全一致**(文件上传、用户/角色/菜单/部门/字典/通知/定时任务/代码生成、L1/L2 校验、审计、权限点矩阵、数据权限),见 `docs/base-postgresql-vue3-需求文档.md` §6 / §8。
> **SB3 升级需求**已在 `springboot3` 交付(JDK 17 / Jakarta / Security 6 / springdoc 2.9 / starter 3 / yauaa),见 `docs/springboot3-需求文档.md` §6.9。
> 本节只展开 **§6.10 后端 SB4 升级专题**。

### 6.10 🔶 后端 SB4 升级需求(本分支核心)

#### 6.10.1 版本矩阵要求

| 组件 | `springboot3` | **本分支** | 要求 |
|---|---|---|---|
| JDK | 17 | **21** | 必须 21;编译与运行一致 |
| Spring Boot | 3.5.16 | **4.1.1** | — |
| Spring Framework | 6.1.x | **7.0.9** | 由 BOM 管理 |
| Spring Security | 6.x | **7.1.1** | **`getAuthorities()` 不得返回 null** |
| Spring Data Redis | — | **4.1.1** | 由 BOM 管理 |
| **Jackson** | 2.x | **3.1.5** | **核心/绑定 `tools.jackson.*`;注解 `com.fasterxml.jackson.annotation.*` 不变** |
| jakarta.servlet | 5.x/6.x | **6.1.0** | — |
| MyBatis-Plus | 3.5.7 | **3.5.17** | starter → `mybatis-plus-spring-boot4-starter` |
| MyBatis-Spring-Boot | 3.0.5 | **4.1.0** | starter 名带 `-4-` |
| PageHelper starter | 2.1.1 | **4.1.1** | 大版本跳变 |
| Druid | 1.2.28(`-boot-3-`) | 1.2.28(**`-boot-4-`**) | 包名 `boot4.autoconfigure` |
| springdoc-openapi | 2.9.0 | **3.1.1** | — |
| Quartz | 手写 `quartz:2.5.2` | **`spring-boot-starter-quartz`** | 自动装配 `SchedulerFactoryBean` |
| Hutool / yauaa / Lombok | 5.8.27 / 8.2.0 / 1.18.46 | **不变** | — |
| kaptcha | 2.3.3 | **2.3.3** | 不变(未换 tianai) |
| fastjson2 | 未引 | **未引(明确决策)** | 统一 Jackson 3 |

#### 6.10.2 Jackson 3 迁移要求(★ 最高优先级)

| # | 要求 | 口径 |
|---|---|---|
| 1 | **核心/数据绑定包迁移** | `com.fasterxml.jackson.{core,databind}.*` → **`tools.jackson.{core,databind}.*`** |
| 2 | **注解包不动** | `com.fasterxml.jackson.annotation.{JsonFormat,JsonIgnore,JsonIgnoreProperties,JsonInclude}` **保持原样**(实测全仓仅 4 个唯一 import) |
| 3 | **异常体系** | `JsonProcessingException` → **`JacksonException`**(继承 `RuntimeException`);`catch (IOException)` **必须改** |
| 4 | **序列化器基类** | `JsonSerializer<T>` → **`ValueSerializer<T>`** |
| 5 | **上下文参数** | `SerializerProvider` → **`SerializationContext`** |
| 6 | **模块基类** | `databind.Module` → **`JacksonModule`** |
| 7 | **模块注册时机** | **只允许构建时**:`JsonMapper.builder().addModule(...)`;Spring 侧用 `JsonMapperBuilderCustomizer`;运行时 `registerModule` **已删除** |
| 8 | **defaultTyping** | 在 **`JsonMapper.Builder`** 上激活,不在 `ObjectMapper` 实例上 |
| 9 | **HTTP 转换器** | `MappingJackson2HttpMessageConverter` → **`JacksonJsonHttpMessageConverter`** |
| 10 | **`NumberSerializer`** | **Jackson 3 已删除**;须自写 `ValueSerializer<Number>`(本分支 `BigNumberSerializer`) |
| 11 | **Customizer 落位** | `Jackson2ObjectMapperBuilderCustomizer` → **`JsonMapperBuilderCustomizer`**;**必须独立配置类**(防循环依赖) |
| 12 | **校验命令** | `grep -rn "com\.fasterxml\.jackson" --include=*.java` 结果**只允许** `annotation.*` 四类 |

**本分支已迁移的 4 个文件**(实测):

| 文件 | Jackson 3 import 数 | 迁移内容 |
|---|---|---|
| `basic-common/.../utils/JsonUtils.java` | 4 | `JacksonException` / `TypeReference` / `ObjectMapper` |
| `basic-framework/.../jackson/BigNumberSerializer.java` | 6 | `ValueSerializer` / `SerializationContext` / `JsonGenerator` / `JacksonStdImpl` |
| `basic-framework/.../config/BigNumberModuleConfig.java` | 6 | `JacksonModule` / `SimpleModule` / `JsonMapper` / `JacksonStdImpl` |
| `basic-framework/.../config/RedisConfig.java` | 4 | `JsonMapper` / `DefaultTyping` / `BasicPolymorphicTypeValidator` / `PolymorphicTypeValidator` |

**仍保留 `com.fasterxml.jackson.annotation.*` 的 11 个文件**(预期,不需改):

`BaseEntity` / `YjyBaseEntity` / `TreeSelect` / `LoginUser` / `SysUser`(entity)/ `SysRole`(entity)/ `SysLogininfor` / `SysOperLog` / `RouterVo` / `SysJob` / `VelocityUtils`

#### 6.10.3 Security 7 适配要求

| 要求 | 口径 |
|---|---|
| 配置方式 | **`SecurityFilterChain` Bean + `authorizeHttpRequests` + `requestMatchers`**(⬆️ SB3 已改) |
| 方法级安全 | **`@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)`**(⬆️ 已改) |
| **`getAuthorities()`** | **不得返回 `null`**;无权限时返回 `Collections.emptyList()` |
| 白名单 | `/login` `/register` `/captchaImage` `/monitor/shutdown` `/swagger-ui.html` `/swagger-ui/**` `/v3/api-docs/**` `/druid/**` + GET 静态资源 |
| CSRF | 关闭(Lambda DSL) |
| 会话 | `STATELESS` |

#### 6.10.4 starter 变体要求

| 能力 | `springboot3` | **本分支** | 若用错的后果 |
|---|---|---|---|
| 连接池 | `druid-spring-boot-3-starter` | **`druid-spring-boot-4-starter`** | 数据源 Bean 不注册 → 启动失败 |
| ORM | `mybatis-plus-spring-boot3-starter` | **`mybatis-plus-spring-boot4-starter`** | Mapper 不扫描 → `NoSuchBeanDefinition` |
| MyBatis 基础 | `mybatis-spring-boot-starter` 3.0.5 | **4.1.0** | 与 MP 版本不匹配 |
| 分页 | `pagehelper-spring-boot-starter` 2.1.1 | **4.1.1** | 分页失效 |
| Quartz | 手工 `quartz` 依赖 | **`spring-boot-starter-quartz`** | 与 SB4 自动装配冲突 |

#### 6.10.5 Quartz 要求

| 要求 | 口径 |
|---|---|
| 依赖 | **`spring-boot-starter-quartz`**(移除手写 `org.quartz-scheduler:quartz`) |
| 装配 | 由 starter 自动装配 `SchedulerFactoryBean` |
| JobStore | **`RAMJobStore`**(内存) |
| 后果 | `qrtz_*` 表**存在但不被使用**;重启后动态任务丢失 |
| 持久化方案 | 切 JDBC JobStore(M4 可选);⚠️ 注意 PG `varchar(1)` 字段兼容问题(若依已知坑) |
| 业务表 | `sys_job` / `sys_job_log` **照常使用**(任务定义与执行日志仍落库) |

#### 6.10.6 JSON 库决策

| 项 | 决策 |
|---|---|
| 是否引入 fastjson2 | **不引入**(若依原版用,本项目统一 Jackson 3) |
| 理由 | ① 多一套序列化栈增加认知与漏洞面 ② fastjson 历史安全问题多 ③ Jackson 3 的 `defaultTyping` + 白名单已满足 Redis 多态需求 |
| 例外条件 | 若业务缓存对象复杂度超预期(复杂泛型集合),再评估(见 A-20) |

#### 6.10.7 Redis 序列化要求

| 要求 | 口径 |
|---|---|
| 序列化器 | **Jackson 3**(`JsonMapper`) |
| 多态 | `defaultTyping` 在 **builder** 上激活,配合 **`BasicPolymorphicTypeValidator` 白名单** |
| 白名单范围 | 只允许业务包(如 `com.basic.**` / `com.core.**`) |
| **升级期脏数据** | 旧 Jackson 2 序列化的缓存对象 **Jackson 3 读不了**;`RedisCache` 已加容错(反序列化失败当未命中);**升级时建议清空 Redis** |
| 风险 | 多态反序列化有 gadget 风险 → 必须白名单(不可用 `LaissezFaireSubTypeValidator`) |

#### 6.10.8 数据库要求

| 要求 | 口径 |
|---|---|
| schema | `basicdb`(**不变**) |
| 表数量 | **34 张**(**不变**) |
| 函数数量 | **5 个**(**不变**) |
| 脚本 | `file-sql/basicdb-vue3.sql`(**不变**) |
| DDL / 索引 / 数据迁移 | **全部不需要** |
| `qrtz_*` 表 | **存在但 RAMJobStore 不使用**(不算变更) |

> **核心结论**:SB 3 → SB 4 是**纯应用层升级**,数据库零改动。回滚只需回退 jar。

#### 6.10.9 前端影响要求

| 端 | 影响 |
|---|---|
| `web-ui` | **零改动**(Vue 3.5.26 + Element Plus 2.13.1 + Vite 6.4.1,纯 JS) |
| `web-mp` | **零改动**(uni-app 3 + uview-plus 3.3.62 + Vite 5.2.8,纯 JS) |
| 唯一潜在可见差异 | **大数序列化**:`BigNumberSerializer` 对超出 JS 安全整数范围的值输出**字符串**(与 SB3 行为一致,前端本就按字符串处理);其余 JSON 结构不变 |
| Token / 鉴权 | 完全不变 |

### 6.11 其它功能需求(与 `springboot3` 一致)

> 文件上传下载、L1/L2 校验、错误明细、各业务模块字段与校验、审计、权限点矩阵、数据权限、验证码(kaptcha)—— 全部与 `springboot3` 一致,不再重复。

---

## 7. 非功能需求

| 属性 | 目标 |
|---|---|
| 一致性 | 事务边界在 Service;异步日志不污染主链路 |
| 可靠性 | 操作日志失败不影响主业务;定时任务异常隔离;**RAMJobStore 取舍已知** |
| 性能 | 列表 1000 条 < 1s;登录 < 500ms;**Jackson 3 序列化性能优于 2.x** |
| 审计合规 | 90 天操作日志可查询、可导出 |
| 安全 | RSA / BCrypt / JWT 30min / XSS / 重复提交 / 限流 / CORS;**Security 7 严格化**;**Redis 多态白名单** |
| 可用性 | 单实例 99.9% |
| 部署 | 单一可执行 jar,`tomcat.max-threads=800`;**运行时 JDK 21** |
| **🔶 可编译性** | JDK 21 下 `mvn clean compile` **0 ERROR** |
| **🔶 可启动性** | 启动无 `NoClassDefFoundError` / 无循环依赖 / 无 `getAuthorities()` NPE |
| **🔶 API 兼容性** | 接口契约与 `springboot3` 逐项一致;前端零改动可对接 |
| **🔶 JSON 兼容性** | 响应 JSON 结构与 `springboot3` 一致(大数仍为字符串) |

---

## 8. 权限与角色

> RBAC 权限点矩阵与数据权限 **与 `springboot3` / `master` 完全一致**(15 个模块 × 增删改查导出导入),详见 `docs/base-postgresql-vue3-需求文档.md` §8。本分支**不新增任何权限点、不改任何菜单数据**。
>
> **🔶 Security 7 相关**:`getAuthorities()` 严格化后,**无权限用户**应正常返回 **403**(而非 NPE);这是本分支必须回归的权限侧行为。

---

## 9. 边界与不做清单

| 项 | 说明 |
|---|---|
| 前端 TS 化 | 走 `springboot4_TypeScript` |
| 业务流程 | 走 `springboot4-extend-flowable` |
| 行为验证码 | 走 `springboot3-tianai-captcha` 系列 |
| 微服务化 / K8s / 灰度 | 不做 |
| SSO 联邦 | 不做 |
| **数据库变更** | **零变更** |
| **前端改动** | **零改动** |
| **fastjson2** | **不引入**(明确决策) |
| **Quartz JDBC JobStore** | 不做(M4 可选) |
| **`WebMvcConfigurer` 替代** | 不做(Spring Framework 7 无 GA 替代) |
| **springdoc 3.1 全量兼容验证** | 未做(仅"能用不崩",M5) |
| **MP / pagehelper 跨版本中间变更** | 未逐项核对(M7) |
| **代码生成器 SB4 模板** | 未做(R34) |
| 移动端完整业务 | 仅登录 + 通知 + 个人中心 |
| GraalVM 原生镜像 | 不做 |

---

## 10. 验收标准

> **AC-01~AC-24 继承自 `springboot3`**(编译 / 启动 / 表数量 / 登录 / CRUD / 锁屏 / 代码生成 / 定时任务 / 导出 / XSS / 重复提交 / Swagger / jakarta 残留 / springdoc 注解 / starter 变体 / 配置键等),见 `docs/springboot3-需求文档.md` §10。下表为本分支**新增 SB4 专项 AC**。

| # | 验收项 | 验收方法 |
|---|---|---|
| AC-01~AC-24 | 见 `springboot3` PRD | 同左 |
| **AC-25** | JDK 21 下 `mvn clean compile` **0 ERROR** | 终端(JDK 21) |
| **AC-26** | `mvn spring-boot:run` 启动成功,**无循环依赖 / 无 NPE** | 终端 |
| **AC-27** | `java --version` 为 21,且与 `pom.xml` `<java.version>21</java.version>` 一致 | 终端 |
| **AC-28** | `grep -rn "com\.fasterxml\.jackson" --include=*.java` **仅命中 `annotation.*`**(4 类唯一 import) | 终端 |
| **AC-29** | `grep -rn "import tools\.jackson" --include=*.java` **命中 ≥ 15 处**(4 个文件) | 终端 |
| **AC-30** | `grep -rn "registerModule" --include=*.java` 返回 **0**(Module 只能构建时注册) | 终端 |
| **AC-31** | `grep -rn "Jackson2ObjectMapperBuilderCustomizer"` 返回 **0**;`JsonMapperBuilderCustomizer` 有命中 | 终端 |
| **AC-32** | `grep -rn "NumberSerializer" --include=*.java` 返回 **0**(Jackson 3 已删该类) | 终端 |
| **AC-33** | `grep -rn "druid-spring-boot-3-starter\|mybatis-plus-spring-boot3-starter"` 返回 **0** | 终端 |
| **AC-34** | `grep -rn "fastjson" --include=*.java --include=pom.xml` 返回 **0** | 终端 |
| **AC-35** | **Security 7**:无权限用户访问受保护接口 → **403**(非 500/NPE) | 浏览器 |
| **AC-36** | **Quartz**:新建 Cron → 启动 → 立即执行 → `sys_job_log` 有记录 | 浏览器 |
| **AC-37** | **Redis**:写读缓存对象正常(Jackson 3 序列化 + defaultTyping);故意清空后不报错 | 浏览器 + Redis |
| **AC-38** | **大数序列化**:`Long` 超 JS 安全整数范围时响应为**字符串** | 浏览器 / `curl` |
| **AC-39** | `/swagger-ui/index.html` 与 `/v3/api-docs` 正常(springdoc 3.1) | 浏览器 |
| **AC-40** | **前端零改动**:`git diff springboot3 -- web-ui web-mp` 为空;两端登录/CRUD 正常 | 终端 + 浏览器 |
| **AC-41** | **数据库零改动**:`git diff springboot3 -- file-sql` 为空;`information_schema` 返回 34 表 | 终端 |
| **AC-42** | 随机抽查 10 个接口的响应 JSON 结构与 `springboot3` 一致 | 代码走查 / 对比 |

---

## 附录A 待确认问题清单

| 编号 | 问题 | 建议默认 | 阻塞 |
|---|---|---|---|
| A-1~A-13 | 继承项(移动端动态路由 / 锁屏 / refresh / 日志保留 / 互踢 / 富文本 / 主子表 / deprecation / 生成器模板 / actuator / druid IP / virtual threads / 校验分组) | 见 `springboot3` PRD | 否 |
| **A-14** | **Quartz 是否需要持久化(切 JDBC JobStore)?** | 当前 RAMJobStore 够用;若需重启不丢任务再切(M4);⚠️ 注意 PG `varchar(1)` 坑 | 否 |
| **A-15** | **升级时是否强制清空 Redis?** | **建议是**(避免 Jackson 2 脏数据);或依赖 `RedisCache` 容错 | 否 |
| **A-16** | **`WebMvcConfigurer` 废弃后如何演进?** | 等 Spring Framework 7 给 GA 替代;当前继续用 | 否 |
| **A-17** | **springdoc 3.1 是否需全量 OpenAPI 校验?** | 建议 M5;若前端/三方用 OpenAPI 生成 SDK 则必须 | 否 |
| **A-18** | **MP 3.5.7 → 3.5.17 / pagehelper 2.1 → 4.1 是否需全 API 回归?** | 建议 M7;重点是分页 + LambdaQuery 链式 | 否 |
| **A-19** | **是否需要给 `extend-generator` 加 SB4 模板?** | **建议加**(否则每次生成要手工改 jakarta + springdoc,R34) | 否 |
| **A-20** | **fastjson2 的例外条件如何界定?** | 若业务缓存出现复杂泛型集合且 Jackson 3 难表达,再评估;当前不引 | 否 |
| **A-21** | **Druid 监控 `statViewServlet` 生产策略?** | 生产必须关或强密码 + IP 白名单 | 否 |
| **A-22** | **是否上 GraalVM 原生镜像?** | 不做;若要做需评估 Jackson 3 反射配置 | 否 |

---

## 附录B 业务回填模板

```markdown
### 模块: <名称>
| 字段 | 类型 | 必填 | 校验 |
|---|---|---|---|
| ... | ... | ... | ... |
```

---

## 附录C SB4 升级交付检查清单

- [ ] `<java.version>21</java.version>`
- [ ] `<spring-boot.version>4.1.1</spring-boot.version>`
- [ ] `grep "com\.fasterxml\.jackson"` 仅命中 `annotation.*`(4 类)
- [ ] `grep "import tools\.jackson"` ≥ 15 处(JsonUtils / BigNumberSerializer / BigNumberModuleConfig / RedisConfig)
- [ ] `grep "registerModule"` = 0
- [ ] `grep "Jackson2ObjectMapperBuilderCustomizer"` = 0;`JsonMapperBuilderCustomizer` 已用
- [ ] `grep "NumberSerializer"` = 0(`BigNumberSerializer` 已是 `ValueSerializer<Number>`)
- [ ] `grep "druid-spring-boot-3-starter"` / `mybatis-plus-spring-boot3-starter` = 0
- [ ] `grep "fastjson"` = 0
- [ ] `getAuthorities()` 返回空集合(非 null)
- [ ] `spring-boot-starter-quartz` 已引入;手写 `quartz` 依赖已移除
- [ ] `BigNumberModuleConfig` 为独立配置类(防循环依赖)
- [ ] Redis 序列化用 Jackson 3 + defaultTyping + 白名单
- [ ] `mvn clean compile`(JDK 21)0 ERROR
- [ ] 启动成功 + 全链路回归(登录/CRUD/锁屏/定时任务/代码生成/Redis)
- [ ] 前端与 DB 零改动(`git diff springboot3` 为空)

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `springboot3` PRD(v0.1)为基线(G1~G12 / AC-01~AC-24 / A-1~A-13 继承),新增 G13~G19 SB4 目标、§6.10 SB4 升级需求(9 小节:版本矩阵 / Jackson 3 十二条要求 + 已迁移文件清单 / Security 7 / starter 4 / Quartz / JSON 库决策 / Redis / 数据库 / 前端影响)、场景 S11~S15、AC-25~AC-42、A-14~A-22、附录C 检查清单 16 项 |
