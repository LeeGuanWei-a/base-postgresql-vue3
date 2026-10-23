# 基础平台(base-postgresql-vue3) 需求文档(PRD) — `springboot3` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot3`** 分支(全栈:后端 + web-ui + web-mp)
> 前置依赖:《架构方案》v0.1
> 本分支相对 `master` 的**唯一需求级差异** = **后端技术栈升级(Spring Boot 2.5.14 → 3.5.16 / JDK 11 → 17)**。业务功能需求、接口契约、数据库、前端代码与 `master` **完全一致**,本文档不重复业务细节,只展开「升级」专题。

---

## 1. 文档信息与版本

- 当前版本:**v0.1** — 初稿,基于 README + 代码扫描抽取
- 变更历史

| 版本 | 日期 | 变更 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `master` PRD 为基线,新增「§6.9 后端升级需求」(JDK 17 / SB 3.5.16 / Jakarta EE / Security 6 / springdoc / starter 变体 / 配置迁移)+ 场景 S7~S10 + AC-15~AC-24 + 待定项 A-8~A-13 |

---

## 2. 背景与目标

### 2.1 为什么做

`master` 分支锁定 Spring Boot 2.5.14 + JDK 11,长期存在两类问题:

- **安全**:Spring Security 5.5 已 EOL,不再提供安全补丁;Spring Framework 5.3 也已进入维护尾期
- **生态**:Spring Boot 3 已是主流,大量新库(starter)只发 `spring-boot3` 变体;新同事上手也默认 SB 3

因此本分支做**一次性后端体系化升级**:JDK 11 → 17、SB 2.5.14 → 3.5.16、Java EE → Jakarta EE、Spring Security 5 → 6、springfox → springdoc。

**关键约束**:升级**不得改变任何对外行为** —— 接口路径、入参、出参、权限字符、数据库结构、业务逻辑全部保持不变;前端两端零改动。

### 2.2 目标清单

| 目标 | 成功标准 |
|---|---|
| G1 提供完整的 RBAC 后台管理能力 | 角色/菜单/部门/岗位/用户/字典/参数 全部可在线管理(与 `master` 一致) |
| G2 提供定时任务在线编排 | 通过 Web 配置 Cron,执行可观测(`sys_job_log`) |
| G3 提供数据表反向代码生成 | 选择表 → 一键生成 Controller/Service/Mapper/Vue 页面 |
| G4 提供 PC + 移动双端 | web-ui 满足桌面运营;web-mp 满足移动办公 |
| G5 保证接口兼容 RuoYi 3.8.2 | `/login` `/logout` `/captchaImage` `/getInfo` `/getRouters` 命名/行为与原版一致 |
| G6 数据库统一在 PostgreSQL `basicdb` schema | 一份 `pg_dump` 脚本可恢复整套 34 张表 |
| **G7(本分支)后端升级到 JDK 17 + Spring Boot 3.5.16** | `java.version=17`、`spring-boot.version=3.5.16`;JDK 17 下 `mvn clean compile` 0 ERROR;应用可启动 |
| **G8(本分支)完成 Jakarta EE 命名空间迁移** | 全仓 `javax.servlet` / `javax.validation` / `javax.annotation` → `jakarta.*`;**`grep import javax.servlet` = 0** |
| **G9(本分支)API 文档迁移到 springdoc** | springfox 依赖完全移除;`/swagger-ui/index.html` 可访问;`/v3/api-docs` 返回合法 OpenAPI JSON;注解全量换 `io.swagger.v3` |
| **G10(本分支)API 契约零变更** | 所有 HTTP 接口的路径/方法/入参/出参/权限字符与 `master` **逐项一致**;前端无需改动即可对接 |
| **G11(本分支)starter 全量换 SB3 变体** | Druid 用 `druid-spring-boot-3-starter`;MyBatis-Plus 用 `mybatis-plus-spring-boot3-starter`;MyBatis-Spring-Boot 3.0.5 |
| **G12(本分支)配置项迁移完成** | Redis 配置从 `spring.redis.*` → `spring.data.redis.*`;`swagger:` 节点删除、`springdoc:` 节点新增 |

### 2.3 非目标(本分支不做)

- 前端 TypeScript 化(走 `springboot3_TypeScript` 分支)
- 行为验证码(走 `springboot3-tianai-captcha` 分支)
- 业务流程引擎(走 `springboot3-extend-flowable` / `springboot2-extend-flowable` 分支)
- 多租户 / SaaS 化
- SSO 联邦(走 keycloak 集成项目)
- JDK 17 → 21、SB 3 → 4、Jackson 2 → 3、Security 6 → 7(走 `springboot4` 分支)
- 微服务拆分 / K8s 化 / 灰度发布
- 国际化完整词条(仅引入 vue-i18n,词条待补)
- **数据库结构变更**(本分支零变更)
- **前端任何改动**(`web-ui` / `web-mp` 保持 JS)
- deprecation 警告清理(M4 可选)

---

## 3. 术语表

| 术语 | 含义 |
|---|---|
| RBAC | Role-Based Access Control,基于角色的访问控制 |
| 数据权限 | 在功能权限之上,基于「本人 / 本部门 / 本部门及子 / 全部 / 自定义」的数据范围过滤 |
| 菜单权限 | 决定是否能看到菜单项(对应路由元数据 `meta.roles` / `perms`) |
| 按钮权限 | 决定页面上按钮是否显示(`v-hasPermi` / `@PreAuthorize`) |
| 锁屏 | 离开座位时手动锁定前端,需重新输入密码解锁;不销毁 Token |
| Kaptcha | 后端图形验证码,本分支为 **kaptcha 2.3.3** |
| `basicdb` | PostgreSQL schema,本项目所有表所在 |
| `qrtz_*` | Quartz 表,共 11 张 |
| `gen_*` | 代码生成元数据表,共 2 张 |
| uview-plus | uView 的 Vue3 版本,组件前缀 `up-` |
| **Jakarta EE** | Java EE 改名后由 Eclipse 基金会维护的规范;包名前缀 `jakarta.*`(原 `javax.*`) |
| **`jakarta.servlet`** | Servlet 6.0 规范包;SB 3 内嵌 Tomcat 10+ 只认 jakarta |
| **springdoc-openapi** | SB 3 生态的 OpenAPI 3 文档库(2.9.0),替代已停维护的 springfox |
| **`SecurityFilterChain`** | Spring Security 6 推荐的配置方式(Bean + Lambda DSL),替代已删除的 `WebSecurityConfigurerAdapter` |
| **starter 变体** | SB 3 专用 starter 名,如 `druid-spring-boot-**3**-starter`、`mybatis-plus-**spring-boot3**-starter` |
| **yauaa** | `nl.basjes.parse.useragent` 8.2.0,User-Agent 解析库,替代 bitwalker `UserAgentUtils` 1.21 |
| **jaxb-api** | `javax.xml.bind:jaxb-api` 2.3.1;JDK 17 已移除 JAXB,需显式引入 |
| **`spring.data.redis.*`** | SB 3 的 Redis 配置前缀(原 `spring.redis.*`) |

---

## 4. 用户角色与使用场景

### 4.1 角色

| 角色 | 关键动作 | 频率 |
|---|---|---|
| **超级管理员(`admin`)** | 全部功能,含角色/菜单/用户/部门维护、代码生成、监控查看 | 高 |
| **业务管理员** | 受限于「数据权限」的 CRUD,通常负责本部门数据 | 中 |
| **审计员** | 仅看 `sys_oper_log` / `sys_logininfor`,无写操作 | 低 |
| **业务运营(PC)** | 通知/字典/参数查询,通知公告发布 | 高 |
| **业务运营(移动)** | 登录、查看通知、个人中心、移动端特有页面 | 中 |
| **开发者** | 用代码生成器建表/生成模块,导入字典;**本分支额外要求:新依赖必须选 SB3 变体** | 中 |
| **后端开发者(本分支重点)** | 维护升级后的工程:jakarta 规范、Security 6 写法、springdoc 注解 | 高 |

### 4.2 关键场景走查

> S1~S6 与 `master` **完全一致**(登录、角色赋权、代码生成、移动端登录、锁屏、定时任务),仅保留索引:
>
> - **S1** 运营首次登录并查看通知(PC)
> - **S2** 管理员新增角色并赋菜单
> - **S3** 开发者用代码生成器建模块
> - **S4** 移动端登录(H5)
> - **S5** 锁屏与解锁(PC)
> - **S6** 定时任务 Cron 在线配置
>
> 详细步骤见 `docs/base-postgresql-vue3-需求文档.md` §4.2(`master` 分支)。

#### 🔶 场景 S7:升级后首次启动与全链路回归(本分支核心)

1. 本地安装 **JDK 17**,IDE Project SDK 与 Language level 均设为 17
2. `mvn clean compile` → **0 ERROR**(若有 `javax.*` 残留会在此报错)
3. `psql -f file-sql/basicdb-vue3.sql` 恢复 34 张表(**与升级前完全一致**)
4. `mvn spring-boot:run -pl basic-admin-api` → 启动日志出现「基础平台启动成功」
5. 打开 `/swagger-ui/index.html` → 确认 springdoc UI 正常渲染,接口分组可见
6. 打开 `/v3/api-docs` → 确认返回合法 OpenAPI JSON
7. `admin / admin123` 登录 PC 端 → 菜单/权限/路由全部正常
8. 切到移动端 H5 登录 → 正常(前端代码零改动)
9. 抽查:用户 CRUD / 角色赋权 / 字典缓存 / 定时任务启停 / 代码生成下载 → 全部通过

#### 🔶 场景 S8:Swagger 注解从 springfox 迁到 springdoc

1. 升级前:`@Api` / `@ApiOperation` / `@ApiModelProperty`(`io.swagger.annotations.*`)
2. 升级后:`@Tag` / `@Operation` / `@Schema`(`io.swagger.v3.oas.annotations.*`)
3. 模型类:`io.swagger.v3.oas.models.{OpenAPI, Info, Contact, Components, SecurityScheme, SecurityRequirement}`
4. `SwaggerConfig` **完全重写**:不再用 `Docket`,改为 `@Bean OpenAPI customOpenAPI()`
5. UI 路径变更:`/swagger-ui.html`(springfox)→ **`/swagger-ui/index.html`**(springdoc)
6. `SecurityConfig` 白名单同步放行 `/swagger-ui.html`、`/swagger-ui/**`、`/v3/api-docs/**`

#### 🔶 场景 S9:新增依赖时的 starter 选择(易错点)

1. 需要引入某个第三方库
2. **先查该库是否提供 `spring-boot3` 变体**
3. 例:引入连接池 → 必须写 `druid-spring-boot-3-starter`(写 `druid-spring-boot-starter` 会**不注册 Bean**,启动后数据源为空)
4. 例:引入 MP → 必须写 `mybatis-plus-spring-boot3-starter`
5. 若库内部依赖 `javax.servlet` → **不可引入**(会与 jakarta 冲突,运行时报 `NoClassDefFoundError`)
6. 提交前 `mvn dependency:tree` 复验,确认无 `javax.servlet` 传递依赖

#### 🔶 场景 S10:代码生成器产出代码的 jakarta 修正(已知缺口 R20)

1. 用代码生成器生成一套 CRUD(`Controller` / `Service` / `Mapper` / `.vue`)
2. 生成的 Java 代码中 import 仍是 **`javax.servlet.*`**(因为 `extend-generator` 的 Velocity 模板未改)
3. 手工把 `javax.*` 改成 `jakarta.*`
4. 若模板产出 springfox 注解 → 一并改 `io.swagger.v3.oas.annotations`
5. 编译验证
6. **长期方案**:给 `extend-generator` 增加 SB3 版模板(A-9 待定)

---

## 5. 总体需求

### 5.1 范围图(本分支改动以 **🔶** 标出)

```
                    ┌────────────────────────────────────┐
                    │   平台能力(本仓库)                │
                    │  ┌──────────┐  ┌───────────────┐  │
                    │  │ PC Web   │  │ 移动 H5/      │  │
                    │  │ (Vue3)   │  │ 小程序/App     │  │
                    │  │ 🔶 零改动 │  │ 🔶 零改动     │  │
                    │  └────┬─────┘  └─────┬─────────┘  │
                    │       │  HTTP/JSON   │            │
                    │  ┌────┴───────────────┴─────────┐  │
                    │  │   Spring Boot 3.5.16         │  │
                    │  │  (basic-admin-api)           │  │
                    │  │  🔶 JDK 17 + Jakarta EE      │  │
                    │  │  🔶 Security 6 + springdoc   │  │
                    │  └────┬─────────────────────────┘  │
                    │       │  MyBatis-Plus 3.5.7         │
                    │  ┌────┴─────────────────────────┐  │
                    │  │  PostgreSQL basicdb          │  │
                    │  │  (34 表 + 5 函数)            │  │
                    │  │  🔶 零改动                   │  │
                    │  └──────────────────────────────┘  │
                    └────────────────────────────────────┘
```

### 5.2 核心规则总表(业务规则与 `master` 一致)

| 模块 | 关键字段 | L1 校验(字段级) | L2 校验(跨模块) |
|---|---|---|---|
| 用户 | `userName` / `password` / `phonenumber` | 必填、长度、邮箱格式、手机号正则 | 用户名全局唯一;同一部门下不能重名 |
| 角色 | `roleName` / `roleKey` / `dataScope` | 必填、`roleKey` 英文+数字、显示顺序 0-999 | `roleKey` 全局唯一;`admin` 角色不可删 |
| 菜单 | `menuName` / `menuType(M/C/F)` / `perms` | 必填、类型枚举、组件路径合法 | 上级菜单 + 路由组件 + 权限字符(若 type=F) |
| 部门 | `deptName` / `parentId` | 必填、parentId 不能等于自己 | 父级链路不能形成环 |
| 岗位 | `postCode` / `postName` | 必填、长度 | 全局唯一 `postCode` |
| 字典 | `dictType` / `dictLabel` | 必填 | 字典类型存在性校验;`dictValue` 类型内唯一 |
| 通知 | `noticeTitle` / `noticeType` / `status` | 必填、长度 | 发布前必须已选 `status=1` 才对外可见 |
| 定时任务 | `invokeTarget` / `cronExpression` | 必填、Cron 合法 | `invokeTarget` 必须对应 `@Component("ryTask")` 内方法 |
| 代码生成 | `tableName` | 必填、英文+下划线 | 表必须已存在 `basicdb` schema |
| **🔶 命名空间** | import 语句 | **禁止 `javax.servlet` / `javax.validation` / `javax.annotation`** | 全仓 grep 必须为 0 |
| **🔶 starter** | pom 依赖 | **Druid / MP 必须用 `-boot-3-` / `-spring-boot3-` 变体** | `mvn dependency:tree` 无 javax.servlet 传递依赖 |
| **🔶 API 文档注解** | `io.swagger.*` | **禁止 `io.swagger.annotations.*`(v2)** | 统一 `io.swagger.v3.oas.annotations.*` |

---

## 6. 功能需求

> **业务功能需求**(文件上传、用户/角色/菜单/部门/字典/通知/定时任务/代码生成、L1/L2 校验、审计、权限点矩阵、数据权限)与 `master` **完全一致**,详见 `docs/base-postgresql-vue3-需求文档.md` §6 / §8。本节只展开 **升级专题**。

### 6.9 🔶 后端升级需求(本分支核心)

#### 6.9.1 版本矩阵要求

| 组件 | `master` | **本分支** | 要求 |
|---|---|---|---|
| JDK | 11 | **17** | 必须 17;编译与运行一致 |
| Spring Boot | 2.5.14 | **3.5.16** | 属性名 `spring-boot.version` |
| Spring Framework | 5.3.x | **6.1.x**(SB 3.5 自带) | 由 BOM 管理 |
| Spring Security | 5.5.x | **6.x**(SB 3.5 自带) | 由 BOM 管理 |
| Servlet API | `javax.servlet` | **`jakarta.servlet` 6.x** | Tomcat 10.1+ 提供 |
| MyBatis-Plus | 3.5.1(`mybatis-plus-boot-starter`) | **3.5.7**(`mybatis-plus-spring-boot3-starter`) | starter 必须换名 |
| MyBatis-Spring-Boot | (注释) | **3.0.5** | 新启用 |
| Druid | 1.2.8(`druid-spring-boot-starter`) | **1.2.28**(`druid-spring-boot-3-starter`) | starter 必须换名 |
| PageHelper | 1.4.1 | **2.1.1** | — |
| API 文档 | springfox 3.0.0 | **springdoc-openapi 2.9.0** | springfox 移除 |
| UA 解析 | bitwalker 1.21 | **yauaa 8.2.0** | bitwalker 移除 |
| Hutool | `hutool-bom` 5.8.3 | **`hutool-core` 5.8.27** | 单 artifact |
| Lombok | 无 | **1.18.46** | 显式锁版本 |
| JAXB | 无(JDK 11 自带) | **`javax.xml.bind:jaxb-api` 2.3.1** | JDK 17 需显式引入 |
| kaptcha | 2.3.2 | **2.3.3** | — |
| poi | 4.1.2 | **5.5.1** | — |
| commons-io | 2.11.0 | **2.22.0** | — |
| oshi | 6.1.6 | **7.4.4** | — |
| commons-fileupload | — | **1.5** | 新增 |
| fastjson | 2.0.4 | **移除** | 不再使用 |
| 子模块 parent.version | 3.8.2 | **3.9.2** | 7 个模块统一 |

#### 6.9.2 Jakarta EE 迁移要求

| 要求 | 口径 |
|---|---|
| 迁移范围 | `javax.servlet.*` / `javax.validation.*` / `javax.annotation.*` → `jakarta.*` |
| 已完成量 | **114 处 jakarta import** |
| 残留量 | **`javax.servlet` = 0**(实测) |
| 涉及文件 | `XssFilter` / `RepeatableFilter` / `RepeatedlyRequestWrapper` / `XssHttpServletRequestWrapper` / `JwtAuthenticationTokenFilter` / `AuthenticationEntryPointImpl` / `LogoutSuccessHandlerImpl` / `AsyncFactory` / `Xss` / `XssValidator` / `ServletUtils` / `IpUtils` / `HttpHelper` / `ExcelUtil` / `BeanValidators` + 各 `Sys*` 实体(`@NotBlank` 等)+ 多个 Controller |
| 例外 | `javax.xml.bind:jaxb-api`(JAXB 规范仍在 `javax` 命名空间,**不迁移**) |
| 校验手段 | `grep -r "import javax.servlet" --include=*.java` 必须返回空 |

#### 6.9.3 Spring Security 6 改写要求

| 项 | Security 5(旧) | **Security 6(本分支)** |
|---|---|---|
| 配置载体 | `extends WebSecurityConfigurerAdapter` | **`@Bean SecurityFilterChain`** |
| 授权入口 | `authorizeRequests()` | **`authorizeHttpRequests()`** |
| 路径匹配 | `antMatchers(...)` | **`requestMatchers(...)`** |
| 关闭 CSRF | `csrf().disable()` | `csrf(csrf -> csrf.disable())`(Lambda DSL) |
| 无状态 | `sessionManagement().sessionCreationPolicy(STATELESS)` | Lambda DSL |
| 白名单放行 | `antMatchers("/x").anonymous()/permitAll()` | `requestMatchers("/x").permitAll()` |
| 方法级权限 | `@PreAuthorize("@ss.hasPermi('...')")` | **不变** |

**必须放行的路径**(springdoc 迁移后):

```
/login, /register, /captchaImage, /monitor/shutdown
/swagger-ui.html, /swagger-ui/**, /v3/api-docs/**, /druid/**
GET: /, /*.html, /**/*.html, /**/*.css, /**/*.js, /profile/**
+ @Anonymous 注解扫描出的 URL(PermitAllUrlProperties)
```

#### 6.9.4 springdoc 迁移要求

| 项 | 要求 |
|---|---|
| 依赖 | `org.springdoc:springdoc-openapi-starter-webmvc-ui:2.9.0`(替代 springfox-boot-starter) |
| 配置类 | `SwaggerConfig` 重写为 `@Bean OpenAPI customOpenAPI()`,含 `Info` / `Contact` / `SecurityScheme(Bearer JWT)` |
| 注解 | `@Tag` / `@Operation` / `@Parameter` / `@Schema`(`io.swagger.v3.oas.annotations`) |
| 模型 | `io.swagger.v3.oas.models.*` |
| UI 路径 | **`/swagger-ui/index.html`**(`/swagger-ui.html` 重定向) |
| JSON 路径 | **`/v3/api-docs`** |
| 配置项 | `application.yml` 新增 `springdoc:` 节点;删除 `swagger:` 节点 |
| 静态资源 | `ResourcesConfig` 加 springdoc 静态资源映射 |

#### 6.9.5 配置迁移要求

| 配置文件 | 变更 |
|---|---|
| `application.yml` | 删 `swagger:` 节点;加 `springdoc:` 节点;删 `xxl.job:` 节点(本就不使用) |
| `application-dev.yml` / `application-prod.yml` | `spring.redis.host/port/database/password` → **`spring.data.redis.*`** |
| `application-druid.yml` | `statViewServlet.url-pattern: /druid/*` 显式声明;包名 `com.alibaba.druid.spring.boot3.autoconfigure` |
| 数据源 URL | **不变**:`jdbc:postgresql://localhost:5432/basicdb?currentSchema=basicdb` |

#### 6.9.6 数据库要求

| 要求 | 口径 |
|---|---|
| schema | `basicdb`(**不变**) |
| 表数量 | **34 张**(**不变**) |
| 函数数量 | **5 个**(**不变**) |
| 脚本 | `file-sql/basicdb-vue3.sql`(**不变**,无需任何升级改动) |
| DDL 变更 | **无** |
| 索引变更 | **无** |
| 数据迁移 | **不需要** |

> **核心结论**:SB 2 → SB 3 是**纯应用层升级**,数据库零改动。这与 MySQL → PostgreSQL 那种迁移完全不同。

#### 6.9.7 前端影响要求

| 端 | 影响 |
|---|---|
| `web-ui` | **零改动**(仍是 Vue 3.5.26 + Element Plus 2.13.1 + Vite 6.4.1,纯 JS) |
| `web-mp` | **零改动**(仍是 uni-app 3 + uview-plus 3.3.62 + Vite 5.2.8,纯 JS) |
| 唯一可见变化 | 若前端硬编码了 Swagger 旧路径 `/swagger-ui.html`,需改为 `/swagger-ui/index.html`(本仓库前端未硬编码,无需改动) |
| Token/鉴权 | 完全不变(`Basic-Token` 头 + JWT 30min) |

### 6.10 其它功能需求(与 `master` 一致)

> 文件上传下载、自动校验(L1)、错误明细、各业务模块字段与校验、跨业务比对(L2)、审计规则 —— 全部与 `master` 一致,详见 `docs/base-postgresql-vue3-需求文档.md` §6。

---

## 7. 非功能需求

| 属性 | 目标 |
|---|---|
| 一致性 | 单服务事务边界在 Service 层,异步日志/统计不污染主链路 |
| 可靠性 | 操作日志失败不影响主业务;定时任务异常隔离(每个 Job 独立 try/catch) |
| 性能 | 列表分页 1000 条 < 1s;监控实时接口 < 200ms;登录链路 < 500ms;**升级后性能不低于 `master`** |
| 审计合规 | 90 天操作日志可查询、可导出 |
| 安全 | RSA 加密登录密码;BCrypt 散列;JWT 30min;XSS 过滤;重复提交拦截;限流;显式 CORS 白名单;**Security 6.x(Security 5.5 EOL 风险已消除)** |
| 可用性 | 单实例 99.9%(运维保障) |
| 部署 | 单一可执行 jar,`tomcat.max-threads=800`;**运行时要求 JDK 17** |
| **🔶 可编译性** | JDK 17 下 `mvn clean compile` **0 ERROR**;7 模块 `parent.version=3.9.2` |
| **🔶 可启动性** | `mvn spring-boot:run` 启动成功,无 `NoClassDefFoundError: javax/servlet/*` |
| **🔶 API 兼容性** | 接口契约与 `master` 逐项一致;前端零改动可对接 |
| **🔶 文档可用性** | `/swagger-ui/index.html` + `/v3/api-docs` 均可访问 |

---

## 8. 权限与角色

> RBAC 权限点矩阵与数据权限 **与 `master` 完全一致**(15 个模块 × 增删改查导出导入),详见 `docs/base-postgresql-vue3-需求文档.md` §8。本分支**不新增任何权限点、不改任何菜单数据**。
>
> 🔶 唯一相关变化:`SecurityConfig` 白名单新增 `/swagger-ui/**`、`/v3/api-docs/**`(原 springfox 的 `/swagger-resources/**`、`/webjars/**` 不再需要)。

---

## 9. 边界与不做清单

| 项 | 说明 |
|---|---|
| 前端 TS 化 | 走 `springboot3_TypeScript` |
| 行为验证码 | 走 `springboot3-tianai-captcha` |
| 业务流程 | 走 `springboot3-extend-flowable` / `springboot2-extend-flowable` |
| 微服务化 | 不做 |
| SSO 联邦 | 不做(后续 keycloak 集成) |
| **JDK 17 → 21** | 走 `springboot4` |
| **SB 3 → 4** | 走 `springboot4` |
| **Jackson 2 → 3** | 走 `springboot4` |
| **Security 6 → 7** | 走 `springboot4` |
| **数据库变更** | 本分支**零变更** |
| **前端改动** | 本分支**零改动** |
| 移动端完整业务 | 仅做登录 + 通知 + 个人中心 |
| 国际化词条 | 已引入,词条待补 |
| **deprecation 清理** | 本分支未做,列 M4 |
| **代码生成器 SB3 模板** | 未做(R20 / A-9) |
| **GraalVM 原生镜像** | 不做 |
| **前端单元测试** | 不引入 |

---

## 10. 验收标准

| # | 验收项 | 验收方法 |
|---|---|---|
| AC-01 | JDK 17 下 `mvn clean compile` 0 ERROR | 终端(JDK 17) |
| AC-02 | `mvn spring-boot:run` 启动成功,看到「基础平台启动成功」 | 终端 |
| AC-03 | 7 个子模块 `parent.version` = **3.9.2** | `mvn validate` 或 IDE |
| AC-04 | `psql -f file-sql/basicdb-vue3.sql` 恢复后,`information_schema.tables WHERE table_schema='basicdb'` 返回 **34** | 终端 |
| AC-05 | admin/admin123 登录 PC 端成功,看到完整菜单 | 浏览器 |
| AC-06 | admin/admin123 登录 web-mp H5 成功,看到工作台 | 浏览器 |
| AC-07 | 锁屏→输入密码→解锁 成功 | 浏览器 |
| AC-08 | 新建角色→分配菜单→绑定用户→用户重新登录可见新菜单 | 浏览器 |
| AC-09 | 工具/代码生成 → 导入 `sys_demo` → 下载 zip 解压 → 重启可访问(**生成的 Java 需手工改 jakarta**) | 终端 + 浏览器 |
| AC-10 | 监控/定时任务 → 新建 Cron → 启动 → 任务日志可见 | 浏览器 |
| AC-11 | 操作日志可导出 Excel | 浏览器 |
| AC-12 | XSS 测试:输入 `<script>alert(1)</script>` → 后端拒绝 | 终端/浏览器 |
| AC-13 | 重复提交测试:同 URL 同 body 500ms 内两次 → 第二次被拒绝 | 浏览器 |
| **AC-14** | **`/swagger-ui/index.html` 可访问,接口分组与标注正常** | 浏览器 |
| **AC-15** | **`/v3/api-docs` 返回合法 OpenAPI 3 JSON** | 浏览器 / `curl` |
| **AC-16** | **全仓 `grep "import javax.servlet" --include=*.java` 返回 0 条** | 终端 |
| **AC-17** | **全仓 `grep "io.swagger.annotations" --include=*.java` 返回 0 条** | 终端 |
| **AC-18** | **全仓 `grep "mybatis-plus-boot-starter\|druid-spring-boot-starter"`(非 SB3 变体)返回 0 条** | 终端 |
| **AC-19** | **`grep "spring.redis\." --include=*.yml` 返回 0 条(应为 `spring.data.redis.`)`** | 终端 |
| **AC-20** | **`java --version` 与 `pom.xml` 的 `<java.version>17</java.version>` 一致** | 终端 |
| **AC-21** | **前端两端零改动(`git diff master -- web-ui web-mp` 应为空)** | 终端 |
| **AC-22** | **接口契约零变更:随机抽查 10 个接口的路径/方法/入参/出参与 `master` 一致** | 代码走查 |
| **AC-23** | **`mvn dependency:tree` 中无 `javax.servlet` 传递依赖** | 终端 |
| **AC-24** | **登录日志的 browser/os 字段正常解析(yauaa 生效)** | 浏览器 + DB 查询 |

---

## 附录A 待确认问题清单

| 编号 | 问题 | 建议默认 | 阻塞 |
|---|---|---|---|
| A-1 | 移动端是否需要走完整动态路由? | 暂用本地 `pages.json` | 否 |
| A-2 | web-mp 是否实现锁屏? | 暂不实现 | 否 |
| A-3 | web-mp Token 失效后是否自动重登? | 先做 401 跳转登录 | 否 |
| A-4 | 操作日志保留周期? | 90 天 | 否 |
| A-5 | 多端登录是否互踢? | 当前 Token 无设备指纹,不互踢 | 否 |
| A-6 | 通知公告是否支持富文本(图片/附件)? | 暂为 `text` | 否 |
| A-7 | 代码生成器是否支持主子表? | 暂不支持 | 否 |
| **A-8** | **deprecation 警告何时清理?** | 建议 M4,不影响功能 | 否 |
| **A-9** | **`extend-generator` 是否提供 SB3 版 Velocity 模板?** | **建议提供**(否则每次生成都要手工改 jakarta,见 R20) | 否 |
| **A-10** | **是否引入 `spring-boot-starter-actuator` 并开放 health?** | 当前仅用 `shutdown`;如需 K8s 探针再引入 | 否 |
| **A-11** | **`/druid/**` 是否应限制访问 IP?** | 当前 `permitAll`,生产建议加 IP 白名单 | 否 |
| **A-12** | **是否启用 SB 3 的 virtual threads(JDK 21 才正式)?** | 不做,JDK 21 在 `springboot4` | 否 |
| **A-13** | **是否统一 `jakarta.validation` 的分组校验?** | 当前未用分组,保持 | 否 |

---

## 附录B 业务回填模板

```markdown
### 模块: <名称>
| 字段 | 类型 | 必填 | 校验 |
|---|---|---|---|
| ... | ... | ... | ... |
```

---

## 附录C 升级交付检查清单

- [ ] `<java.version>17</java.version>`
- [ ] `<spring-boot.version>3.5.16</spring-boot.version>`
- [ ] 7 个子模块 `parent.version` = `3.9.2`
- [ ] `grep "import javax.servlet" --include=*.java` = 0
- [ ] `grep "io.swagger.annotations" --include=*.java` = 0
- [ ] `druid-spring-boot-3-starter` 已替换(2 处)
- [ ] `mybatis-plus-spring-boot3-starter` 已替换
- [ ] `jaxb-api` 已引入
- [ ] `yauaa` 已引入 + `UserAgentConfig` 已新增
- [ ] `springdoc-openapi-starter-webmvc-ui` 已引入 + `SwaggerConfig` 已重写
- [ ] `SecurityConfig` 已改 `SecurityFilterChain` + `requestMatchers`
- [ ] `/swagger-ui/**` + `/v3/api-docs/**` 已放行
- [ ] `spring.data.redis.*` 已迁移(dev/prod 两个 profile)
- [ ] `swagger:` 节点已删、`springdoc:` 节点已加
- [ ] `mvn clean compile`(JDK 17)0 ERROR
- [ ] 应用启动成功 + 全链路回归通过

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `master` PRD(v0.1)为基线,新增 G7~G12 升级目标、§6.9 后端升级需求(7 小节:版本矩阵/Jakarta/Security 6/springdoc/配置/数据库/前端影响)、场景 S7~S10、AC-14~AC-24、待定项 A-8~A-13、附录C 升级检查清单 |
