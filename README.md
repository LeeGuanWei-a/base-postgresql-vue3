# Basic Admin (Spring Boot 3.5.16 + Vue 3 + Element Plus)

基于若依 RuoYi-Vue 3.8.2 改造,从 **Spring Boot 2.5.14** 升级到 **Spring Boot 3.5.16** 的实战记录。

> 本分支 `springboot3` 对应 commit `a4c182a 升级到springboot3`,基于 `5b344b7 first commit`(原始若依 RuoYi 3.8.2 + SB 2.5.14)做的一次性大版本升级。
>
> **仓库另有 `springboot4` 分支**,在该分支上从 SB 3.5.16 继续升到了 SB 4.1.1(JDK 21、Jackson 3 等),**与本分支无关**。

---

## 前端说明

本仓库前端为 **JavaScript 版**(无 TypeScript):

- 框架:Vue 3.5.26 + Element Plus 2.13.1
- 构建:Vite 6.4.1
- 代码:纯 `.js` / `.vue`,**无 `typescript` 依赖**(package.json 不含 `typescript`)

> 仓库另有 `TypeScript` 分支的前端(基于若依 RuoYi-Vue TS 版),本分支**未涉及**。

---

## 一、升级前后对照(基于 `git diff 5b344b7..a4c182a`)

| 组件 | SB 2.5.14 (起点,5b344b7) | SB 3.5.16 (本次升级,a4c182a) |
|---|---|---|
| **JDK** | **11** | **17** |
| Spring Boot | 2.5.14 | **3.5.16** |
| Spring Framework | 5.3.x | **6.1.x** |
| Spring Security | 5.5.x | **6.x** |
| MyBatis-Plus | 3.5.1 | **3.5.7** |
| MyBatis-Spring-Boot-Starter | (注释掉,未启用) | **3.0.5** |
| Druid | 1.2.8 | **1.2.28**(`druid-spring-boot-starter` → `druid-spring-boot-3-starter`) |
| PageHelper | 1.4.1 | **2.1.1** |
| Springdoc-openapi | — (用 springfox 3.0.0) | **2.9.0** |
| Hutool | hutool-bom 5.8.3 | **hutool-core 5.8.27** |
| yauaa(User-Agent 解析) | bitwalker 1.21 | **yauaa 8.2.0**(换库) |
| Lombok | 未指定 | **1.18.46** |
| kaptcha | 2.3.2 | **2.3.3** |
| poi | 4.1.2 | **5.5.1** |
| commons-io | 2.11.0 | **2.22.0** |
| oshi | 6.1.6 | **7.4.4** |
| swagger | springfox 3.0.0 | **移除,springdoc 替代** |
| jakarta.servlet | (javax.servlet) | **jakarta.servlet-api 6.x** |
| fastjson | 2.0.4(注释掉,未启用) | **移除** |

---

## 二、Spring Boot 2 → 3 主要破坏性变更(本次踩坑)

### 2.1 Java EE → Jakarta EE(**最大破坏性**)

javax.* 包名**全部改为** jakarta.*:

| javax | jakarta |
|---|---|
| `javax.servlet.http.HttpServletRequest` | `jakarta.servlet.http.HttpServletRequest` |
| `javax.servlet.Filter` / `FilterChain` / `FilterConfig` | `jakarta.servlet.*` |
| `javax.annotation.*` | `jakarta.annotation.*` |
| `javax.validation.*` | `jakarta.validation.*` |
| `javax.persistence.*` | `jakarta.persistence.*` |
| `javax.sql.DataSource` | `javax.sql.DataSource`(**没变** —— Java SE) |
| `javax.crypto.*` | 不变(Java SE) |

**项目里所有 controller / filter / interceptor / utils 都要改 import**。本次涉及 ~50 个文件。

### 2.2 springfox → springdoc-openapi

- springfox 3.0.0 **不再维护**,与 SB 3 / Spring 6 兼容性差。
- 改用 `springdoc-openapi-starter-webmvc-ui:2.9.0`:
  - 访问地址 `http://host:port/swagger-ui/index.html`
  - 配置文件从 `application.yml` 里 `swagger:` 节点改到 `springdoc:` 节点
  - 注解从 `@Api` / `@ApiOperation`(io.swagger)改到 `@Operation` / `@Tag`(io.swagger.v3.oas.annotations)
  - 原 `SwaggerConfig.java` 改写

### 2.3 SwaggerConfig 重写

- 老的 `SwaggerConfig` 用了 `springfox.documentation.*`,在 SB 3 完全用不了。
- 改用 `io.swagger.v3.oas.models.OpenAPI` + `springdoc` 自己的配置方式(bean)。

### 2.4 Spring Security 5 → 6 重大变更

- `WebSecurityConfigurerAdapter` **废弃**,改用 `SecurityFilterChain` Bean。
- `authorizeRequests()` 改名为 `authorizeHttpRequests()`。
- `antMatchers()` 改名为 `requestMatchers()`(或 `requestMatchers(AntPathRequestMatcher)`)。
- `and()` 链式风格被推荐,但 `.with(...)` DSL 仍可用。
- 密码编码 `BCryptPasswordEncoder` 用法不变。
- `JwtAuthenticationTokenFilter`、`AuthenticationEntryPointImpl`、`LogoutSuccessHandlerImpl` 实现机制不变,只改 import。

### 2.5 Druid starter 改名

| SB 2 | SB 3 |
|---|---|
| `druid-spring-boot-starter` | `druid-spring-boot-3-starter` |

包名没变(`com.alibaba.druid.spring.boot3.autoconfigure` 实际是 SB 3 才有的,SB 2 用 `com.alibaba.druid.spring.boot.autoconfigure`)。

### 2.6 MyBatis-Plus starter 改名

| SB 2 | SB 3 |
|---|---|
| `mybatis-plus-boot-starter` | `mybatis-plus-spring-boot3-starter` |

### 2.7 第三方包大量升级

- Hutool:**bom 模式**(`hutool-bom` 5.8.3) → **单 artifact 模式**(`hutool-core` 5.8.27),只引 core 不再 boms。
- yauaa 8.2.0 替代 bitwalker 1.21(用户代理字符串解析)。
- oshi 7.x 系统信息采集(API 有变化,`getOperatingSystem().getManufacturer()` 等)。
- jaxb-api:**JDK 11+ 默认移除 XML 绑定**,显式 `javax.xml.bind:jaxb-api:2.3.1` 引入(给 XSS 过滤、Excel 解析等用)。

### 2.8 Lombok / annotation 处理

- Lombok **1.18.30+** 才完整支持 JDK 17 + 枚举 + `@AllArgsConstructor`,本项目锁 `1.18.46`。
- JDK 17 的 `--add-opens` 需求大部分场景 Lombok 已处理;本项目 IDE 配 `JDK 17` 即可,无需手动加 jvm args。

### 2.9 Servlet 5 + Tomcat 10

- `jakarta.servlet:jakarta.servlet-api:5.x`(SB 2 时代是 4.x)。
- 内嵌 Tomcat 10.x,HTTP/2 默认开启。
- 业务代码对 servlet API 的调用几乎不变,只改包名。

### 2.10 Spring Framework 6 一些 deprecation

- `WebMvcConfigurerAdapter` 已删除,实现 `WebMvcConfigurer` 接口(SB 2 时代也支持,SB 3 强制要求)。
- `RequestMappingHandlerMapping` 等的 `setUseSuffixPatternMatch` / `useRegisteredSuffixPatternMatch` 方法已删除。
- `MediaType.APPLICATION_JSON_UTF8` 已删除,统一用 `MediaType.APPLICATION_JSON`。

### 2.11 yauaa 替代 bitwalker 解析 UA

- bitwalker `UserAgent` API 与 yauaa `UserAgentAnalyzer` API 完全不一样。
- 项目里 `UserAgentConfig`(若依原本没这个,本次新增)用 `@Bean UserAgentAnalyzer`,把 yauaa 的 100+ MB yaml 规则文件做成单例,避免每次请求都重载。

---

## 三、本次升级真实改动(基于 `git diff 5b344b7..a4c182a`)

实际改动 **50+ 个文件**,大致分类:

### 3.1 父 `pom.xml`

```xml
<!-- properties -->
<java.version>11 → 17</java.version>
<springboot.version → spring-boot.version> 2.5.14 → 3.5.16</spring-boot.version>
<mybatis-plus-boot-starter.version> 3.5.1 → 3.5.7</mybatis-plus-boot-starter.version>
<mybatis-spring-boot.version> (注释) → 3.0.5</mybatis-spring-boot.version>
<druid.version> 1.2.8 → 1.2.28</druid.version>
<pagehelper.boot.version> 1.4.1 → 2.1.1</pagehelper.boot.version>
<oshi.version> 6.1.6 → 7.4.4</oshi.version>
<kaptcha.version> 2.3.2 → 2.3.3</kaptcha.version>
<poi.version> 4.1.2 → 5.5.1</poi.version>
<commons.io.version> 2.11.0 → 2.22.0</commons.io.version>
<!-- 新增 -->
<lombok.version>1.18.46</lombok.version>
<hutool-core.version>5.8.27</hutool-core.version>
<yauaa.version>8.2.0</yauaa.version>
<jaxb-api.version>2.3.1</jaxb-api.version>
<springdoc.version>2.9.0</springdoc.version>
<commons-fileupload.version>1.5</commons-fileupload.version>
<!-- 移除 -->
<swagger.version>3.0.0</swagger.version>  <!-- springfox 移除 -->
<bitwalker.version>1.21</bitwalker.version>  <!-- yauaa 替代 -->
<fastjson.version>2.0.4</fastjson.version>  <!-- fastjson 移除 -->
<commons.collections.version>3.2.2</commons.collections.version>
<hutool.version>5.8.3</hutool.version>  <!-- 改 hutool-core -->

<!-- dependencyManagement -->
- springfox-boot-starter, swagger-models  ← 移除
+ springdoc-openapi-starter-webmvc-ui  ← 新增
+ hutool-core  ← 新增(单 artifact 模式)
+ jaxb-api  ← 新增
+ yauaa  ← 新增
+ mybatis-spring-boot-starter  ← 新增(启用,原来注释掉)
- mybatis-plus-boot-starter  ← 移除
+ mybatis-plus-spring-boot3-starter  ← 替换
- pagehelper-spring-boot-starter 保留
- druid-spring-boot-starter
+ druid-spring-boot-3-starter
```

### 3.2 7 个子模块 `pom.xml`

- 7 个子模块的 `parent.version` 从 `3.8.2` → `3.9.2`
- `basic-common/pom.xml`:
  - `spring-boot-starter-web` 改 `spring-boot-starter-webmvc`(若依 3.9.2 原版就这样)
  - hutool 依赖从 `cn.hutool:hutool-bom:import` 改 `cn.hutool:hutool-core`
  - 加 `javax.xml.bind:jaxb-api`
- `basic-framework/pom.xml`:
  - `spring-boot-starter-aop`(若依 3.9.2 原版就这样,保留)
  - 删 `javax.servlet:servlet-api`(用 jakarta 替代)
  - `druid-spring-boot-3-starter`
- `extend-quartz/pom.xml`:`org.quartz-scheduler:quartz` 单独依赖
- `extend-generator/pom.xml`:`druid-spring-boot-3-starter`

### 3.3 `application.yml` / `application-dev.yml` / `application-prod.yml`

- `swagger:` 节点删除(springfox 没了)
- `springdoc:` 节点新增
- `spring.redis.host/port/database/password` 改 `spring.data.redis.*`(SB 3 警告,SB 4 强制)
- `datasource.druid.statViewServlet.url-pattern: /druid/*` 显式声明
- `xxl.job` 节点删除(若依原版里有,本项目不用,本次移除)

### 3.4 代码侧主要改动

| 文件 | 主要改动 |
|---|---|
| `SwaggerConfig.java` | 完全重写,springfox → springdoc-openapi v3 |
| `SecurityConfig.java` | `WebSecurityConfigurerAdapter` → `SecurityFilterChain` Bean,`authorizeRequests()` → `authorizeHttpRequests()`,`antMatchers()` → `requestMatchers()` |
| `UserAgentConfig.java` | **新增**,yauaa `UserAgentAnalyzer` 单例 Bean |
| `ResourcesConfig.java` | `WebMvcConfigurerAdapter` → `WebMvcConfigurer` |
| `DruidConfig.java` | 包名从 `com.alibaba.druid.spring.boot.autoconfigure` → `com.alibaba.druid.spring.boot3.autoconfigure` |
| `Xss.java` / `XssValidator.java` | `javax.servlet.*` → `jakarta.servlet.*` |
| `XssFilter.java` / `RepeatableFilter.java` / `RepeatedlyRequestWrapper.java` / `XssHttpServletRequestWrapper.java` | 同上 |
| `JwtAuthenticationTokenFilter.java` | 同上 |
| `AuthenticationEntryPointImpl.java` | 同上 |
| `LogoutSuccessHandlerImpl.java` | 同上 |
| `AsyncFactory.java` | 同上 |
| `PermitAllUrlProperties.java` | `antPathMatcher` 注入,`RequestMappingInfo` 处理逻辑微调 |
| `SysDept.java` / `SysDictType.java` / `SysDictData.java` / `SysMenu.java` / `SysRole.java` / `SysUser.java` | 主要是 `javax.validation.*` → `jakarta.validation.*` |
| `SysConfigController.java` / `SysDictDataController.java` / `SysDictTypeController.java` / `SysPostController.java` / `SysRoleController.java` / `SysUserController.java` / `SysOperlogController.java` / `SysLogininforController.java` / `GenController.java` / `SysJobController.java` | 主要是 swagger 注解从 io.swagger → io.swagger.v3.oas.annotations |
| `SysLoginService.java` / `TokenService.java` | 主要是 jakarta import + yauaa 替代 bitwalker 后的 API 调用 |
| `SysConfigServiceImpl.java` / `SysDictTypeServiceImpl.java` / `SysFilesServiceImpl.java` / `SysUserServiceImpl.java` | 主要是 `RedisCache.getCacheObject()` 等的 cache 注解微调 |
| `HttpHelper.java` / `IpUtils.java` / `ServletUtils.java` / `FileUtils.java` / `BeanValidators.java` / `ExcelUtil.java` / `SysLoginController.java` | 主要是 jakarta / commons-* 升级后的 API 微调 |
| `CaptchaController.java` / `CommonController.java` / `SysConfigMapper.xml` / `SysUserMapper.xml` / `SysRoleMapper.xml` / `SysDeptMapper.xml` / `SysDeptMapper.java` / `SysMenuMapper.java` | 主要是 jakarta / swagger / SQL 微调 |
| `GenTable.java` / `GenTableColumn.java` / `SysNotice.java` / `SysPost.java` | 主要是 jakarta validation |
| `SysConfig.java` / `SysFilesServiceImpl.java` 等 | `@DataSource` 等切面相关 |
| `MybatisConfig.java` / `FilterConfig.java` / `ServerConfig.java` / `SameUrlDataInterceptor.java` / `RepeatSubmitInterceptor.java` / `ShutdownManager.java` / `LogAspect.java` / `GlobalExceptionHandler.java` / `SysLoginController.java` / `SysLoginService.java` | 主要是 jakarta import + 业务无关小改 |
| `TestController.java` / `SwaggerController.java` | 主要是 swagger 注解换 io.swagger.v3 |
| `DemoController.java` / `GenTable.java` / `GenTableColumn.java` | 业务示例 |
| `pom.xml` 全部 | 父版本 + 子版本号、starter 名、第三方包版本调整 |

### 3.5 新增文件

- `UserAgentConfig.java` —— yauaa `UserAgentAnalyzer` 单例

### 3.6 删除文件

- (本次升级**没删任何**配置文件,只是改了内容)

---

## 四、未做 / 后续可优化

> 这些事项是 SB 2 → SB 3 升级时**没做完**或**没做**的,留作下一阶段(也就是 `springboot4` 分支)处理。

- [ ] JDK 17 → 21(若依原版 SB 3.5.16 还是 17,本项目跟着用 17)
- [ ] Spring Boot 3.5.16 → 4.1.1(已经在 `springboot4` 分支做完了)
- [ ] Spring Security 6 → 7(`springboot4` 分支做了)
- [ ] Jackson 2 → Jackson 3(`springboot4` 分支做了)
- [ ] Spring Framework 6 → 7(`springboot4` 分支做了)
- [ ] 各种 deprecation 警告清理:`WebMvcConfigurer` 接口新实现未 GA,继续 `@SuppressWarnings` 过渡
- [ ] `WebSecurityConfigurerAdapter` 已废弃(本项目已用 `SecurityFilterChain` Bean 替代)
- [ ] `@Deprecated` 的 `org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration` 等,在 `springboot4` 拆到 `spring-boot-jdbc` 子模块(本分支还在 `boot.autoconfigure.jdbc`,正常)

---

## 五、运行前 checklist

- [x] JDK 17 安装
- [x] 7 个子模块 `parent.version` 一致为 `3.9.2`
- [x] IDE 工程的 Project SDK 设为 JDK 17,Language level 17

---

## 六、数据库脚本 (`file-sql/basicdb-vue3.sql`)

> 本分支运行所需的 **PostgreSQL 全库结构 + 初始化数据** 都在 `file-sql/basicdb-vue3.sql`。
> 其它 `file-sql/*.sql` 是历史中间产物,本分支**不直接使用**;真正要执行的就是这一个。

### 6.1 脚本基本信息

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

### 6.2 表清单(共 34 张,均位于 `basicdb` schema)

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

### 6.3 恢复步骤(全新数据库)

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

### 6.4 重新备份成同款脚本

```bash
pg_dump -U system -h localhost -d basicdb \
  --schema=basicdb --no-owner --no-privileges \
  --file=file-sql/basicdb-vue3.sql
```

- 加 `--no-owner --no-privileges` 后,导出的脚本**不再**带 `OWNER TO system`、可移植到任意环境。
- 不加这两个开关,导出结果与本仓库现存的 `basicdb-vue3.sql` 格式一致。
- 脚本里所有 `data` 段都用了 `COPY ... FROM stdin;`,导入端必须用 `psql`。

### 6.5 默认账号

| 用户名 | 密码(明文) | 备注 |
|---|---|---|
| `admin` | `admin123` | 超级管理员,登录后请立刻改密 |

要重置可执行同目录的 `reset-admin-password.sql`。

### 6.6 与应用连接配置

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

### 6.7 其它 `file-sql/*.sql` 说明(本分支**不直接使用**,留作历史)

| 文件 | 角色 |
|---|---|
| `basic-sql-2023-03-02.sql` | 早期 MySQL 5.7 全库脚本,**非 PostgreSQL**,本分支不要混用 |
| `quartz.sql` | 单独的 Quartz 建表脚本,**已被 `basicdb-vue3.sql` 内置的 11 张 `qrtz_*` 表覆盖** |
| `ry_20210908.sql` | RuoYi 3.8.2 原始 MySQL 全库脚本,**非 PostgreSQL**,对照用 |
| `reset-admin-password.sql` | 把 `admin` 密码重置回 `admin123` 的小工具,按需手动跑 |
| `reset-all-passwords.sql` | 把**所有**用户密码统一重置回默认的小工具,慎用 |
| `fix-sys-menu-component.sql` | 修 `sys_menu.component` 路径里 `system` 写错的,按需手动跑 |
| `create-sys-notice-read.sql` | 补建 `sys_notice_read`(用户已读记录)表,按需手动跑 |

> 一句话:**运行本项目,只跑 `basicdb-vue3.sql` 这一个文件就够了。**