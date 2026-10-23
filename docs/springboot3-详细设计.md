# 基础平台(base-postgresql-vue3) 详细设计(LLD) — `springboot3` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot3`** 分支
> 前置依赖:《架构方案》v0.1、《需求文档》v0.1、《概要设计》v0.1
> 本文档细化到「类 / 方法 / 关键逻辑」。**业务逻辑与 `master` 完全一致**;本分支新增 **§1.2 升级涉及文件清单、§3.9 升级机制(7 小节:jakarta 迁移 / Security 6 / springdoc / yauaa / Druid / ResourcesConfig / 配置迁移)、§4.1 登录引擎的 yauaa 改造、§9.2 升级期错误码、§11.2 TC-21~TC-32**。
> 本分支改动以 **🔶** 标出。

---

## 1. 总体包结构

### 1.1 包结构(结构与 `master` 完全相同,**无新增/无删除包**)

```
com.basic
├── BasicApplication                # 启动类(🔶 无变化)
├── BasicServletInitializer         # war 启动适配(🔶 jakarta.servlet)
├── web
│   ├── config/SwaggerConfig        # 🔶 完全重写(springdoc)
│   └── controller
│       ├── common/{CaptchaController, CommonController}
│       ├── monitor/{CacheController, ServerController, SysLogininforController, SysOperlogController, SysUserOnlineController}
│       ├── system/{SysConfigController, SysDeptController, SysDictDataController, SysDictTypeController, SysIndexController, SysLoginController, SysMenuController, SysNoticeController, SysPostController, SysProfileController, SysRegisterController, SysRoleController, SysUserController}
│       └── tool/{SwaggerController, TestController}
├── framework
│   ├── aspectj/{DataScopeAspect, DataSourceAspect, LogAspect, RateLimiterAspect}
│   ├── config/{ApplicationConfig, CaptchaConfig, DruidConfig, FastJson2JsonRedisSerializer, FilterConfig, JacksonConfig, KaptchaTextCreator, MyBatisConfig, MybatisPlusConfig, RedisConfig, ResourcesConfig, SecurityConfig, ServerConfig, ThreadPoolConfig, UserAgentConfig, properties/*}
│   │                          ▲ 🔶 新增      ▲ 🔶 包名变        ▲ 🔶 结构重写
│   ├── datasource/{DynamicDataSource, DynamicDataSourceContextHolder}
│   ├── interceptor/{RepeatSubmitInterceptor, impl/SameUrlDataInterceptor}
│   ├── jackson/BigNumberSerializer
│   ├── manager/{AsyncManager, ShutdownManager, factory/AsyncFactory}
│   ├── security
│   │   ├── filter/JwtAuthenticationTokenFilter
│   │   └── handle/{AuthenticationEntryPointImpl, LogoutSuccessHandlerImpl, MyMetaObjectHandler}
│   └── web
│       ├── domain/{Server, server/{Cpu, Jvm, Mem, Sys, SysFile}}
│       ├── exception/GlobalExceptionHandler
│       └── service/{PermissionService, SysLoginService, SysPermissionService, SysRegisterService, TokenService, UserDetailsServiceImpl}
├── common
│   ├── annotation/{Anonymous, DataScope, DataSource, Excel, Excels, Log, RateLimiter, RepeatSubmit}
│   ├── config/BasicConfig
│   ├── constant/{Constants, GenConstants, HttpStatus, ScheduleConstants, UserConstants}
│   ├── core
│   │   ├── controller/BaseController
│   │   ├── domain/{AjaxResult, BaseEntity, R, TableDataInfo, TreeEntity, TreeSelect, YjyBaseEntity, entity/*, model/{LoginBody, LoginUser, RegisterBody}}
│   │   ├── page/{PageDomain, TableSupport}
│   │   ├── redis/RedisCache
│   │   └── text/{CharsetKit, Convert, StrFormatter}
│   ├── enums/{BusinessStatus, BusinessType, DataSourceType, HttpMethod, LimitType, OperatorType, UserStatus}
│   ├── exception/{base/BaseException, file/*, job/TaskException, user/*, DemoModeException, GlobalException, ServiceException, UtilException}
│   ├── filter/{RepeatableFilter, RepeatedlyRequestWrapper, XssFilter, XssHttpServletRequestWrapper}
│   ├── utils/{Arith, DateUtils, DictUtils, ExceptionUtil, JsonUtils, LogUtils, MessageUtils, PageUtils, SecurityUtils, SpringUtils, StringUtils, Threads, bean/*, file/*, html/*, http/*, ip/*, poi/*, reflect/*, sign/{Base64, Md5Utils, RsaUtils}, sql/SqlUtil, uuid/*}
│   └── xss/{Xss, XssValidator}
├── quartz (extend-quartz)
│   ├── config/ScheduleConfig
│   ├── controller/{SysJobController, SysJobLogController}
│   ├── domain/{SysJob, SysJobLog}
│   ├── mapper/{SysJobMapper, SysJobLogMapper}
│   ├── service/{ISysJobService, ISysJobLogService, impl/*}
│   ├── task/RyTask
│   └── util/{AbstractQuartzJob, CronUtils, JobInvokeUtil, QuartzDisallowConcurrentExecution, QuartzJobExecution, ScheduleUtils}
├── generator (extend-generator)
│   ├── config/GenConfig
│   ├── controller/GenController
│   ├── domain/{GenTable, GenTableColumn}
│   ├── mapper/{GenTableMapper, GenTableColumnMapper}
│   ├── service/{IGenTableService, IGenTableColumnService, impl/*}
│   └── util/{GenUtils, VelocityInitializer, VelocityUtils}
└── system (basic-admin-core)
    ├── domain/{SysConfig, SysFiles, SysLogininfor, SysNotice, SysNoticeRead, SysOperLog, SysPost, SysRoleDept, SysRoleMenu, SysUserOnline, SysUserPost, SysUserRole, vo/{MetaVo, RouterVo}}
    ├── mapper/{SysConfigMapper, SysDeptMapper, SysDictDataMapper, SysDictTypeMapper, SysFilesMapper, SysLogininforMapper, SysMenuMapper, SysNoticeMapper, SysNoticeReadMapper, SysOperLogMapper, SysPostMapper, SysRoleDeptMapper, SysRoleMapper, SysRoleMenuMapper, SysUserMapper, SysUserPostMapper, SysUserRoleMapper}
    └── service/{ISysConfigService, ISysDeptService, ISysDictDataService, ISysDictTypeService, ISysFilesService, ISysLogininforService, ISysMenuService, ISysNoticeReadService, ISysNoticeService, ISysOperLogService, ISysPostService, ISysRoleService, ISysUserOnlineService, ISysUserService, impl/*}

com.core (business-core)
├── controller/DemoController
├── domain/Demo
├── mapper/DemoMapper
└── service/{DemoService, impl/DemoServiceImpl}
```

> **包命名约定**(与 `master` 同):`com.basic` = 平台层,`com.core` = 业务层(由 `BasicApplication` 显式扫)。

### 1.2 🔶 升级涉及文件清单(本分支改动台账)

#### 1.2.1 `pom.xml` 层(8 个文件)

| 文件 | 关键改动 |
|---|---|
| 父 `pom.xml` | `java.version` 11→17;`springboot.version`→`spring-boot.version` 2.5.14→3.5.16;移除 springfox/bitwalker/fastjson/hutool-bom;新增 springdoc/hutool-core/yauaa/jaxb-api/lombok/commons-fileupload;`druid-spring-boot-3-starter`;`mybatis-plus-spring-boot3-starter`;启用 `mybatis-spring-boot-starter` 3.0.5 |
| `basic-common/pom.xml` | `spring-boot-starter-web`→`-webmvc`;hutool-bom→`hutool-core`;新增 `jaxb-api`;`parent.version` 3.9.2 |
| `basic-framework/pom.xml` | 删 `javax.servlet:servlet-api`;`druid-spring-boot-3-starter`;新增 yauaa;`parent.version` 3.9.2 |
| `basic-admin-api/pom.xml` | springdoc 依赖;`parent.version` 3.9.2 |
| `basic-admin-core/pom.xml` | `parent.version` 3.9.2 |
| `business-core/pom.xml` | `parent.version` 3.9.2 |
| `extend-quartz/pom.xml` | 单独依赖 `org.quartz-scheduler:quartz`;`parent.version` 3.9.2 |
| `extend-generator/pom.xml` | `druid-spring-boot-3-starter`;Velocity 2.3;`parent.version` 3.9.2 |

#### 1.2.2 配置类(6 个)

| 文件 | 改动类型 | 说明 |
|---|---|---|
| `basic-admin-api/.../config/SwaggerConfig.java` | **完全重写** | `Docket`(springfox)→ `@Bean OpenAPI`(springdoc) |
| `basic-framework/.../config/SecurityConfig.java` | **结构重写** | `extends WebSecurityConfigurerAdapter` → `@Bean SecurityFilterChain`;`authorizeRequests`→`authorizeHttpRequests`;`antMatchers`→`requestMatchers`;Lambda DSL |
| `basic-framework/.../config/UserAgentConfig.java` | **新增** | yauaa `UserAgentAnalyzer` 单例 Bean |
| `basic-framework/.../config/ResourcesConfig.java` | 修改 | `WebMvcConfigurerAdapter` → `WebMvcConfigurer`;新增 springdoc 静态资源映射 |
| `basic-framework/.../config/DruidConfig.java` | 修改 | 包名 `com.alibaba.druid.spring.boot.autoconfigure` → `com.alibaba.druid.spring.boot.**3**.autoconfigure` |
| `basic-framework/.../config/FilterConfig.java` | 修改 | jakarta import |

#### 1.2.3 Filter / Interceptor / Handler(jakarta 迁移)

| 文件 | 改动 |
|---|---|
| `common/filter/XssFilter.java` | `javax.servlet.*` → `jakarta.servlet.*` |
| `common/filter/RepeatableFilter.java` | 同上 |
| `common/filter/RepeatedlyRequestWrapper.java` | 同上 |
| `common/filter/XssHttpServletRequestWrapper.java` | 同上 |
| `framework/security/filter/JwtAuthenticationTokenFilter.java` | 同上 |
| `framework/security/handle/AuthenticationEntryPointImpl.java` | 同上 |
| `framework/security/handle/LogoutSuccessHandlerImpl.java` | 同上 |
| `framework/interceptor/RepeatSubmitInterceptor.java` + `impl/SameUrlDataInterceptor.java` | 同上 |
| `framework/manager/factory/AsyncFactory.java` | jakarta + **yauaa API 调用改造** |
| `framework/config/properties/PermitAllUrlProperties.java` | 注入 `antPathMatcher`,`RequestMappingInfo` 处理微调 |
| `framework/aspectj/LogAspect.java` | jakarta import |
| `framework/web/exception/GlobalExceptionHandler.java` | jakarta import |

#### 1.2.4 工具类 / 实体(import 迁移 + 库升级适配)

| 文件 | 改动 |
|---|---|
| `common/utils/ServletUtils.java` / `IpUtils.java` / `HttpHelper.java` | jakarta import |
| `common/utils/file/FileUtils.java` | jakarta + commons-io 2.22 API |
| `common/utils/poi/ExcelUtil.java` | poi 4.1.2 → **5.5.1** API 微调 |
| `common/utils/bean/BeanValidators.java` | `javax.validation` → `jakarta.validation` |
| `common/xss/Xss.java` / `XssValidator.java` | jakarta |
| `common/core/domain/model/{LoginBody, RegisterBody}` | jakarta validation |
| `system/domain/{SysUser, SysRole, SysMenu, SysDept, SysPost, SysDictType, SysDictData, SysNotice, SysConfig, SysLogininfor, SysOperLog}` | `javax.validation.*` → `jakarta.validation.*` |
| `generator/domain/{GenTable, GenTableColumn}` | jakarta validation |
| `system/service/impl/{SysConfigServiceImpl, SysDictTypeServiceImpl, SysFilesServiceImpl, SysUserServiceImpl}` | `@Cacheable` / `@CacheEvict` 微调 |
| `framework/web/service/{SysLoginService, TokenService, UserDetailsServiceImpl}` | jakarta + yauaa |

#### 1.2.5 Controller(注解迁移)

| 文件 | 改动 |
|---|---|
| `web/controller/**` 全部(~26 个) | swagger 注解 `io.swagger.annotations.*` → `io.swagger.v3.oas.annotations.{Tag, Operation, Parameter, Schema}`;jakarta |
| `web/controller/tool/TestController.java` | 同上(`@Tag` / `@Operation` / `@Parameter` / `@Schema` / `@Parameters`) |
| `web/controller/tool/SwaggerController.java` | 跳转路径改为 springdoc UI |

#### 1.2.6 Mapper XML(微调)

| 文件 | 改动 |
|---|---|
| `SysConfigMapper.xml` / `SysUserMapper.xml` / `SysRoleMapper.xml` / `SysDeptMapper.xml` | 锁屏解锁关联微调(与 `master` 同源) |
| `SysDeptMapper.java` / `SysMenuMapper.java` | 同上 |

#### 1.2.7 配置文件(3 个)

| 文件 | 改动 |
|---|---|
| `application.yml` | 删 `swagger:`;加 `springdoc:`;删 `xxl.job:` |
| `application-dev.yml` / `application-prod.yml` | `spring.redis.*` → `spring.data.redis.*` |
| `application-druid.yml` | `statViewServlet.url-pattern: /druid/*` 显式声明 |

#### 1.2.8 未改动

| 范围 | 结论 |
|---|---|
| `file-sql/**` | **未改动**(34 表 + 5 函数) |
| `web-ui/**` | **未改动**(JS) |
| `web-mp/**` | **未改动**(JS) |
| 业务逻辑 / SQL 语义 / 接口契约 | **未改动** |

---

## 2. 领域模型与枚举

### 2.1 关键实体(🟰 与 `master` 一致;🔶 仅 import 变化)

| 实体 | 类 | 主键策略 | 关键状态字段 |
|---|---|---|---|
| 用户 | `SysUser` | MP `ASSIGN_ID`(雪花) | `status:0/1`;`delFlag:0/2` |
| 角色 | `SysRole` | MP `ASSIGN_ID` | `status:0/1`;`dataScope:1~5`;`delFlag:0/2` |
| 菜单 | `SysMenu` | MP `ASSIGN_ID` | `menuType:M/C/F`;`visible:0/1`;`isFrame:0/1`;`status:0/1` |
| 部门 | `SysDept` | MP `ASSIGN_ID` | `status:0/1` |
| 岗位 | `SysPost` | MP `ASSIGN_ID` | `status:0/1` |
| 字典类型 | `SysDictType` | MP `ASSIGN_ID` | `status:0/1` |
| 字典值 | `SysDictData` | MP `ASSIGN_ID` | `status:0/1`;`isDefault:Y/N` |
| 通知 | `SysNotice` | MP `ASSIGN_ID` | `status:0/1`;`noticeType:1/2` |
| 定时任务 | `SysJob` | MP `ASSIGN_ID` | `status:0/1`;`concurrent:0/1`;`misfirePolicy:0~3` |
| 任务日志 | `SysJobLog` | MP `ASSIGN_ID` | `status:0/1` |
| 操作日志 | `SysOperLog` | MP `ASSIGN_ID` | `businessType:0~9`;`status:0/1` |
| 登录日志 | `SysLogininfor` | MP `ASSIGN_ID` | `status:0/1` |
| 业务示例 | `Demo` | MP `ASSIGN_ID` | — |
| 文件 | `SysFiles` | MP `ASSIGN_ID` | — |
| 国际化 | `SysI18n` | MP `ASSIGN_ID` | — |
| 已读 | `SysNoticeRead` | MP `ASSIGN_ID` | 唯一 `(user_id, notice_id)` |

> 🔶 **变化点**:实体上的校验注解包名从 `javax.validation.constraints.*` 改为 `jakarta.validation.constraints.*`,字段与语义完全不变。

### 2.2 状态枚举(🟰 与 `master` 一致)

```java
public enum BusinessType { OTHER, INSERT, UPDATE, DELETE, GRANT, EXPORT, IMPORT, FORCE, GENCODE, CLEAN }
public enum BusinessStatus { SUCCESS(0), FAIL(1); }
public enum OperatorType { OTHER, MANAGE, MOBILE }
public enum DataSourceType { MASTER, SLAVE }
public enum UserStatus { OK("0","正常"), DISABLE("1","停用"), DELETED("2","删除"); }
public enum HttpMethod { GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE, ALL }
public enum LimitType { DEFAULT, IP, CLUSTER }
```

### 2.3 状态合法流转(🟰 与 `master` 一致)

| 对象 | 状态 | 合法转换 |
|---|---|---|
| 任务 | `0=运行` ↔ `1=暂停` | `changeStatus(0↔1)`;`run(→ 触发一次,不改 status)` |
| 角色 | `0=正常` / `1=停用` / `2=删除` | `add→0`;`update→0/1`;`remove→2`(逻辑删除) |
| 菜单 | `0=正常` / `1=停用` | 同上 |
| 通知 | `0=正常` / `1=关闭` | `add→0`;`update→0/1` |
| 字典类型 | `0=正常` / `1=停用` | 同上 |
| 用户 | `0=正常` / `1=停用` / `2=删除` | `add→0`;`changeStatus→0/1`;`remove→2` |

---

## 3. 框架机制

### 3.1 启动与包扫描(🟰 与 `master` 一致)

```java
@SpringBootApplication(
    scanBasePackages = {"com.basic", "com.core"},
    exclude = { DataSourceAutoConfiguration.class }
)
@MapperScan({"com.core.**.mapper"})
public class BasicApplication { ... }
```

- `exclude = DataSourceAutoConfiguration.class`:让 `DruidConfig` 接管数据源
- `scanBasePackages`:覆盖 RuoYi 默认 `com.basic.**`,把 `com.core.**` 也纳入
- `MapperScan`:**只**显式扫 `com.core.**.mapper`;`com.basic.**.mapper` 由 `MybatisPlusConfig.@MapperScan` 负责

### 3.2 动态 Schema 路由(🟰 与 `master` 一致)

```java
@Aspect @Component
public class DataSourceAspect {
    @Before("@annotation(ds)")
    public void before(DataSource ds) { DynamicDataSourceContextHolder.setDataSourceType(ds.value().name()); }
    @After("@annotation(ds)")
    public void after(DataSource ds) { DynamicDataSourceContextHolder.clearDataSourceType(); }
}
```

### 3.3 事务边界(🟰 与 `master` 一致)

- **写操作事务**:Service 实现类上 `@Transactional(rollbackFor = Exception.class)`
- **异步日志**:`AsyncManager.me().execute(AsyncFactory.recordOper(...))` 内部单独事务,与主业务隔离
- **跨表写入**:`SysRoleServiceImpl` 入口单事务,`updateRole -> deleteRoleMenu -> batchRoleMenu -> deleteRoleDept -> batchRoleDept`,任一失败整体回滚

### 3.4 审计切面(🟰 与 `master` 一致)

```java
@Aspect @Component
public class LogAspect {
    @Around("@annotation(log)")
    public Object around(ProceedingJoinPoint pjp, Log log) throws Throwable {
        long begin = System.currentTimeMillis();
        try {
            Object result = pjp.proceed();
            handleLog(pjp, log, result, BusinessStatus.SUCCESS);
            return result;
        } catch (Throwable t) {
            handleLog(pjp, log, null, BusinessStatus.FAIL);
            throw t;
        }
    }
}
```

- 切面**不**捕获异常,只记录,异常仍往外抛
- `handleLog` 内通过 `AsyncManager` 异步落 `sys_oper_log`
- 排除参数:`@Log(ignoreParam=true)` / 白名单(`password` / `oldPassword` / `newPassword` / `confirmPassword`)

### 3.5 重复提交拦截(🟰 与 `master` 一致)

```java
@Component
public class SameUrlDataInterceptor extends RepeatSubmitInterceptor {
    @Override
    boolean isRepeatSubmit(HttpServletRequest req, String nowParams) {   // 🔶 jakarta HttpServletRequest
        String cacheKey = "REPEAT_SUBMIT:" + req.getSession().getId() + ":" + req.getRequestURI();
        String prev = redisCache.getCacheObject(cacheKey);
        if (prev != null && prev.equals(nowParams)) {
            return redisCache.getCacheExpire(cacheKey) > 0;
        }
        redisCache.setCacheObject(cacheKey, nowParams, intervalSeconds);
        return false;
    }
}
```

### 3.6 限流 / 3.7 XSS / 3.8 字段填充(🟰 与 `master` 一致,🔶 jakarta)

- 限流:`RateLimiterAspect`;`DEFAULT`→Guava 单机;`IP`→按 IP;`CLUSTER`→Redis 分布式
- XSS:`XssFilter` + `XssHttpServletRequestWrapper` + `@Xss`(JSR-303)双重
- 字段填充:`MyMetaObjectHandler` 自动填 `createBy`/`createTime`/`updateBy`/`updateTime`

### 3.9 🔶 升级机制详细设计(本分支核心)

#### 3.9.1 Jakarta EE 迁移实现

**替换规则**:

```java
// ❌ 旧(SB 2)
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.Valid;
import javax.annotation.Resource;

// ✅ 新(SB 3)
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;
import jakarta.annotation.Resource;
```

**例外(不迁移)**:

```java
// JAXB 规范保持 javax 命名空间(通过 pom 显式引入 jaxb-api 2.3.1)
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.JAXBContext;
```

**校验命令**:

```bash
# 必须返回空
grep -rn "import javax.servlet" --include=*.java .
grep -rn "import javax.validation" --include=*.java .
grep -rn "import javax.annotation" --include=*.java .

# 应命中 100+ 处
grep -rn "import jakarta" --include=*.java . | wc -l
```

#### 3.9.2 Spring Security 6 配置实现

**改造前(SB 2 / Security 5)**

```java
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {          // ❌ SB 3 已删除
    @Autowired private UserDetailsService userDetailsService;
    @Autowired private AuthenticationEntryPointImpl unauthorizedHandler;
    @Autowired private LogoutSuccessHandlerImpl logoutSuccessHandler;
    @Autowired private JwtAuthenticationTokenFilter authenticationTokenFilter;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .csrf().disable()
            .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .authorizeRequests()                                            // ❌ 废弃
            .antMatchers("/login", "/register", "/captchaImage").anonymous() // ❌ 废弃
            .antMatchers(HttpMethod.GET, "/", "/*.html").permitAll()
            .anyRequest().authenticated()
            .and()
            .headers().frameOptions().disable();
        httpSecurity.logout().logoutUrl("/logout").logoutSuccessHandler(logoutSuccessHandler);
        httpSecurity.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
```

**改造后(SB 3 / Security 6)** ✅ 本分支实际实现

```java
@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)        // 🔶 新注解
public class SecurityConfig {
    @Autowired private UserDetailsService userDetailsService;
    @Autowired private AuthenticationEntryPointImpl unauthorizedHandler;
    @Autowired private LogoutSuccessHandlerImpl logoutSuccessHandler;
    @Autowired private JwtAuthenticationTokenFilter authenticationTokenFilter;
    @Autowired private PermitAllUrlProperties permitAllUrl;

    /**
     * 安全过滤链
     * - 移除 WebSecurityConfigurerAdapter（已废弃），改用 SecurityFilterChain Bean
     * - authorizeRequests() 改名为 authorizeHttpRequests()，antMatchers 改为 requestMatchers
     */
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(e -> e.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(requests -> {
                // @Anonymous 注解扫描出的 URL
                permitAllUrl.getUrls().forEach(url -> requests.requestMatchers(url).permitAll());

                requests.requestMatchers("/login", "/register", "/captchaImage", "/monitor/shutdown").permitAll()
                        .requestMatchers(HttpMethod.GET, "/", "/*.html", "/**/*.html", "/**/*.css", "/**/*.js", "/profile/**").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/druid/**").permitAll()
                        .anyRequest().authenticated();
            })
            .headers(h -> h.frameOptions(f -> f.disable()));

        httpSecurity.logout(l -> l.logoutUrl("/logout").logoutSuccessHandler(logoutSuccessHandler));
        httpSecurity.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() { return new BCryptPasswordEncoder(); }
}
```

**关键差异表**:

| Security 5 | Security 6 | 备注 |
|---|---|---|
| `extends WebSecurityConfigurerAdapter` | `@Bean SecurityFilterChain` | 类不再继承 |
| `configure(HttpSecurity)` override | `filterChain(HttpSecurity)` Bean | 方法签名变化 |
| `@EnableGlobalMethodSecurity` | **`@EnableMethodSecurity`** | 注解改名 |
| `authorizeRequests()` | **`authorizeHttpRequests()`** | — |
| `antMatchers(...)` | **`requestMatchers(...)`** | — |
| `mvcMatchers(...)` | `requestMatchers(...)` | 合并 |
| `regexMatchers(...)` | `requestMatchers(new RegexRequestMatcher(...))` | 需显式构造 |
| `.and()` 链式 | Lambda DSL | 官方推荐 |
| `headers().frameOptions().disable()` | `headers(h -> h.frameOptions(f -> f.disable()))` | — |
| `authenticationManagerBean()` override | `@Bean AuthenticationManager` | 从 `AuthenticationConfiguration` 取 |

#### 3.9.3 springdoc 配置实现

**改造后** ✅ 本分支实际实现

```java
package com.basic.web.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * springdoc-openapi 接口配置（替代原 springfox Swagger 2）
 */
@Configuration
public class SwaggerConfig {

    /** 认证头名称 */
    private static final String SECURITY_SCHEME_NAME = "Authorization";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("基础平台 API")
                .description("RuoYi-Vue 3.8.2 改造 · Spring Boot 3.5.16")
                .version("3.9.2")
                .contact(new Contact().name("basic").email("basic@example.com")))
            .components(new Components()
                .addSecuritySchemes(SECURITY_SCHEME_NAME,
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER)
                        .name(SECURITY_SCHEME_NAME)))
            .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }
}
```

**注解迁移对照**:

| 场景 | springfox(旧) | springdoc(新) |
|---|---|---|
| 类/分组 | `@Api(tags = "用户管理")` | `@Tag(name = "用户管理")` |
| 方法 | `@ApiOperation("查询用户列表")` | `@Operation(summary = "查询用户列表")` |
| 参数 | `@ApiImplicitParam(name="userId", value="用户ID")` | `@Parameter(name = "userId", description = "用户ID")` |
| 多参数 | `@ApiImplicitParams({...})` | `@Parameters({...})` |
| 字段 | `@ApiModelProperty("用户名")` | `@Schema(description = "用户名")` |
| 忽略 | `@ApiIgnore` | `@Hidden` |
| 参数位置 | `paramType = "query"` | `in = ParameterIn.QUERY` |

**典型 Controller 写法**:

```java
@Tag(name = "用户管理")
@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController {

    @Operation(summary = "查询用户列表")
    @Parameters({
        @Parameter(name = "pageNum", description = "页码", in = ParameterIn.QUERY),
        @Parameter(name = "pageSize", description = "每页条数", in = ParameterIn.QUERY)
    })
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysUser user) { ... }
}
```

**配置项**(`application.yml`):

```yaml
# 🔶 删除(springfox)
# swagger:
#   enabled: true
#   pathMapping: /dev-api

# 🔶 新增(springdoc)
springdoc:
  api-docs:
    path: /v3/api-docs
    enabled: true
  swagger-ui:
    path: /swagger-ui.html          # 访问时重定向到 /swagger-ui/index.html
    tags-sorter: alpha
    operations-sorter: alpha
  default-produces-media-type: application/json
```

#### 3.9.4 yauaa UA 解析实现(新增 `UserAgentConfig`)

```java
package com.basic.framework.config;

import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * User-Agent 解析配置（yauaa 8.2.0，替代 bitwalker UserAgentUtils 1.21）
 */
@Configuration
public class UserAgentConfig {

    @Bean
    public UserAgentAnalyzer userAgentAnalyzer() {
        return UserAgentAnalyzer
            .newBuilder()
            .hideMatcherLoadStats()                 // 不打印匹配统计
            .withCache(1000)                        // 内置 LRU 缓存,避免重复解析
            .withField("OperatingSystemName")
            .withField("OperatingSystemVersion")
            .withField("AgentName")
            .withField("AgentVersion")
            .build();
    }
}
```

**`AsyncFactory` 调用点改造**:

```java
// ❌ 旧(bitwalker)
// import nl.bitwalker.useragentutils.Browser;
// import nl.bitwalker.useragentutils.OperatingSystem;
// import nl.bitwalker.useragentutils.UserAgent;
// UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
// String browser = userAgent.getBrowser().getName();
// String os = userAgent.getOperatingSystem().getName();

// ✅ 新(yauaa)
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

@Autowired
private UserAgentAnalyzer userAgentAnalyzer;

private String[] parseUserAgent(HttpServletRequest request) {
    String ua = request.getHeader("User-Agent");
    UserAgent agent = userAgentAnalyzer.parse(ua == null ? "" : ua);
    String browser = agent.getValue("AgentName") + " " + agent.getValue("AgentVersion");
    String os = agent.getValue("OperatingSystemName") + " " + agent.getValue("OperatingSystemVersion");
    return new String[] { browser.trim(), os.trim() };
}
```

> **注意**:yauaa 的 `getValue()` 在字段未知时返回 `"Unknown"`,写入 `sys_logininfor.browser/os` 前建议 `"Unknown".equals(v) ? "" : v` 兜底。

#### 3.9.5 Druid / MyBatis-Plus starter 适配

**DruidConfig 包名变更**:

```java
// ❌ 旧(SB 2)
// import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;

// ✅ 新(SB 3)
import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceBuilder;   // 🔶 多了 "3"
```

**pom 依赖锚点**:

```xml
<!-- 父 pom dependencyManagement -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-3-starter</artifactId>   <!-- 🔶 -boot-3- -->
    <version>${druid.version}</version>                     <!-- 1.2.28 -->
</dependency>
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>  <!-- 🔶 spring-boot3 -->
    <version>${mybatis-plus-boot-starter.version}</version>     <!-- 3.5.7 -->
</dependency>
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>        <!-- 🔶 新启用 -->
    <version>${mybatis-spring-boot.version}</version>           <!-- 3.0.5 -->
</dependency>
```

#### 3.9.6 ResourcesConfig 改造

```java
// ❌ 旧(SB 2):WebMvcConfigurerAdapter 已废弃
// public class ResourcesConfig extends WebMvcConfigurerAdapter { ... }

// ✅ 新(SB 3):直接实现接口
@Configuration
public class ResourcesConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /** 本地文件上传路径 */
        registry.addResourceHandler(Constants.RESOURCE_PREFIX + "/**")
                .addResourceLocations("file:" + BasicConfig.getProfile() + "/");

        /** 🔶 springdoc-openapi 静态资源（替代 springfox） */
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(repeatSubmitInterceptor).addPathPatterns("/**");
    }
}
```

#### 3.9.7 配置文件迁移

**`application.yml`**:

```yaml
# 🔶 删除
# swagger:
#   enabled: true
#   pathMapping: /dev-api

# 🔶 新增
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    tags-sorter: alpha
    operations-sorter: alpha

# 🔶 删除(本项目不使用)
# xxl:
#   job:
#     ...

spring:
  # 🔶 迁移(dev / prod profile 同样处理)
  data:
    redis:
      host: localhost
      port: 6379
      database: 0
      password:
      timeout: 10s
      lettuce:
        pool:
          min-idle: 0
          max-idle: 8
          max-active: 8
          max-wait: -1ms
```

**`application-druid.yml`**:

```yaml
spring:
  datasource:
    druid:
      stat-view-servlet:
        enabled: true
        login-username: ruoyi
        login-password: 123456
        url-pattern: /druid/*          # 🔶 显式声明
      web-stat-filter:
        enabled: true
        url-pattern: /*
```

---

## 4. 核心引擎详细设计

### 4.1 登录引擎(`SysLoginService`,🔶 UA 解析改 yauaa)

**核心方法签名**(与 `master` 一致)

```java
public class SysLoginService {
    public LoginUser login(String username, String password, String code, String uuid) {
        validateCaptcha(uuid, code);                // 1. 校验 kaptcha 2.3.3
        UserDetails u = userDetailsService.loadUserByUsername(username);
        if (u == null) throw UserPasswordNotMatchException("账号不存在");
        if (!bCrypt.matches(password, u.getPassword())) {
            asyncFactory.recordLogininfor(username, FAIL, "密码错误");
            throw UserPasswordNotMatchException();
        } else if (UserStatus.DISABLE.code.equals(u.getStatus())) {
            asyncFactory.recordLogininfor(username, FAIL, "账号停用");
            throw new ServiceException("账号已被停用,请联系管理员");
        }
        LoginUser loginUser = (LoginUser) u;
        tokenService.createToken(loginUser);
        asyncFactory.recordLogininfor(username, SUCCESS, "登录成功");
        return loginUser;
    }
    public void logout(LoginUser loginUser) { tokenService.delLoginUser(loginUser.getToken()); }
}
```

**`TokenService.createToken` / `refreshToken`**(与 `master` 一致)

```java
public String createToken(LoginUser loginUser) {
    String token = IdUtils.fastUUID();
    loginUser.setToken(token);
    setUserAgent(loginUser);                     // 🔶 内部改用 yauaa
    refreshToken(loginUser);                     // 写 redis cache,默认 30min
    Map<String, Object> claims = new HashMap<>();
    claims.put(Constants.LOGIN_USER_KEY, token);
    return JwtUtil.createToken(claims, tokenSecret);
}

public void refreshToken(LoginUser loginUser) {
    loginUser.setLoginTime(System.currentTimeMillis());
    loginUser.setExpireTime(loginUser.getLoginTime() + expireTime * 60 * 1000);
    String key = CacheConstants.LOGIN_TOKEN_KEY + loginUser.getToken();
    redisCache.setCacheObject(key, loginUser, Duration.ofMinutes(expireTime));
}
```

**`JwtAuthenticationTokenFilter`**(🔶 jakarta)

```java
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {   // 🔶 jakarta 的 OncePerRequestFilter
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
            throws ServletException, IOException {                          // 🔶 jakarta 异常
        String token = SecurityUtils.getToken(req);
        if (StringUtils.isNotEmpty(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                Claims claims = JwtUtil.parseToken(token);
                String uuid = (String) claims.get(Constants.LOGIN_USER_KEY);
                LoginUser loginUser = redisCache.getCacheObject(CacheConstants.LOGIN_TOKEN_KEY + uuid);
                if (loginUser != null) {
                    loginUser.setToken(uuid);
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        loginUser, null, loginUser.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception e) { logger.warn("解析 token 失败", e); }
        }
        chain.doFilter(req, resp);
    }
}
```

### 4.2 锁屏/解锁实现(与 `master` 一致)

> **不新增接口**。`SysLoginService` 内部已支持「复用 token」场景。
> 实际路径:`web-ui/store/modules/lock.js`(**🔶 未改动,仍是 JS**)维护 `locked` 状态,提交时直接调 `/login`,**后端不感知是锁屏解锁**。

```js
// store/modules/lock.js(节选,本分支未改)
unlock(password) {
  return login({ username: this.userName, password, code: 'LOCK_BYPASS', uuid: 'LOCK' })
    .then(() => { this.locked = false; ... });
}
```

### 4.3 定时任务引擎(与 `master` 一致)

**新增任务 / 改状态 / 立即执行**、**`AbstractQuartzJob` 抽象**、**`JobInvokeUtil` 反射**、**`RyTask` 示例** — 与 `master` 逐行一致,不再重复。

> 🔶 唯一变化:`extend-quartz/pom.xml` 显式声明 `org.quartz-scheduler:quartz`;`ScheduleConfig` 等 import 换 jakarta。

### 4.4 代码生成引擎(与 `master` 一致;⚠️ 模板未适配 SB3)

**导入表 / 生成** 逻辑与 `master` 一致;Velocity 升级到 **2.3**。模板位于 `main/resources/vm/`:

- `vm/java/Controller.java.vm`
- `vm/java/Service.java.vm` / `ServiceImpl.java.vm`
- `vm/java/Mapper.java.vm` / `Mapper.xml.vm`
- `vm/vue/index.vue.vm` / `api.js.vm` / `index-tree.vue.vm` / `index.vue.vm`
- `vm/sql/sql.vm` / `vm/js/api.js.vm`

> ⚠️ **🔶 R20**:模板内仍是 `javax.*` import + springfox 注解。生成后需手工:
> 1. `javax.servlet/validation/annotation.*` → `jakarta.*`
> 2. `io.swagger.annotations.*` → `io.swagger.v3.oas.annotations.*`(`@Api`→`@Tag`、`@ApiOperation`→`@Operation`、`@ApiModelProperty`→`@Schema`)
> 3. 编译验证
>
> 彻底方案:在 `extend-generator` 增一套 SB3 模板(A-9)。

### 4.5 动态路由/菜单(`SysMenuServiceImpl`,与 `master` 一致)

`buildMenus(List<SysMenu>)` 逻辑一致。

### 4.6 🔶 API 文档引擎(springdoc,本分支新增)

#### 4.6.1 文档生成链路

```
启动
  │
  ├─ springdoc 扫描所有 @RestController
  │     ├─ 读 @Tag / @Operation / @Parameter / @Schema
  │     ├─ 读方法签名 → 参数/返回 schema
  │     └─ 读 SwaggerConfig 的 OpenAPI Bean(Info / SecurityScheme)
  │
  ▼
/v3/api-docs  → OpenAPI 3 JSON(运行时生成,非静态文件)
  │
  ▼
/swagger-ui/index.html  → Swagger UI 渲染(读上面的 JSON)
```

#### 4.6.2 认证配置(Bearer JWT)

```java
.components(new Components()
    .addSecuritySchemes("Authorization",
        new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .in(SecurityScheme.In.HEADER)
            .name("Authorization")))
.addSecurityItem(new SecurityRequirement().addList("Authorization"));
```

> UI 右上角出现 **Authorize** 按钮,填入 `Basic-Token` 的值即可带 token 调试。

#### 4.6.3 已知限制

| 限制 | 说明 |
|---|---|
| `@Anonymous` 注解的 URL | springdoc 不感知,文档里仍会显示需要鉴权(实际放行) |
| `PermitAllUrlProperties` 动态 URL | 同上 |
| 泛型返回 `R<T>` | 需 `@Schema` 标注或自定义 `ModelConverter`,否则显示为 `Object` |
| 文件上传接口 | 需 `@RequestPart` 才正确识别为 multipart |

---

## 5. 校验引擎

### 5.1 L1 字段级校验(🟰 与 `master` 一致,🔶 jakarta)

- 入口:`@Validated` + **`jakarta.validation.constraints.*`**
- 规则实现:`XssValidator`(自定义)+ `@NotBlank` / `@Email` / `@Size` / `@Pattern` / `@Excel`
- 失败处理:`GlobalExceptionHandler` 捕获 `MethodArgumentNotValidException` / `ConstraintViolationException` → 400 + msg

> 🔶 **易错点**:`@Validated` 在 Spring 里仍是 `org.springframework.validation.annotation.Validated`(**不变**);只有 `@Valid` / `@NotBlank` 等约束注解换 jakarta。

### 5.2 L2 跨业务校验(🟰 与 `master` 一致)

| 校验项 | 位置 | 失败动作 |
|---|---|---|
| 用户名唯一 | `SysUserServiceImpl.checkUserNameUnique` | `UserException("user.register.not.unique")` |
| 角色 key 唯一 | `SysRoleServiceImpl.checkRoleKeyUnique` | `ServiceException` |
| 菜单不能关联自身为子 | `SysMenuServiceImpl` | `ServiceException` |
| 部门父级链路不能形成环 | `SysDeptServiceImpl.checkParentIdExist` | `ServiceException` |
| 通知发布必须先选 `status=1` | `SysNoticeServiceImpl` | `ServiceException` |
| 定时 invokeTarget 合法 | `ScheduleUtils` | `TaskException` |
| 字典值类型内唯一 | `SysDictDataServiceImpl.checkDictValueUnique` | `ServiceException` |
| 已读记录唯一 | `SysNoticeReadServiceImpl` | `insertOrUpdate` 幂等 |

### 5.3 🔶 编译期校验(升级特有)

| 校验项 | 手段 | 失败表现 |
|---|---|---|
| `javax.*` 残留 | `mvn clean compile` + grep | 编译报错 / 运行时 `NoClassDefFoundError` |
| springfox 注解残留 | 编译 | `cannot find symbol: class Api` |
| starter 变体用错 | 启动 | `Failed to configure a DataSource` / `NoSuchBeanDefinition` |
| 配置键未迁移 | 启动告警 | `Property 'spring.redis.host' is deprecated` |
| JAXB 缺失 | 运行时 | `ClassNotFoundException: javax.xml.bind.*` |

---

## 6. 业务模块实现(节选,🟰 与 `master` 一致)

### 6.1 用户管理

`SysUserServiceImpl` 的 `addUser` / `updateUser` / `removeUserByIds` / `resetPwd` — 与 `master` 逐行一致(含默认密码 `123456`、禁改超管、级联删 `user_role`/`user_post`)。🔶 仅 import 换 jakarta。

### 6.2 角色管理

`checkRoleKeyUnique` / `authDataScope` — 一致。

### 6.3 菜单管理

`treeselectByUserId` — 一致。

### 6.4 通知已读(`SysNoticeRead`)

`readOnce` 走 `insertOrUpdate` + 唯一索引兜底 — 一致。

### 6.5 Demo 业务(`business-core`)

`DemoController` — 一致。

### 6.6 🔶 升级后的 Controller 范本(供新功能参照)

```java
package com.basic.web.controller.system;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;                 // 🔶 jakarta
import jakarta.validation.Valid;                                  // 🔶 jakarta
import org.springframework.security.access.prepost.PreAuthorize;  // 不变
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理")                                            // 🔶 springdoc
@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController {

    @Autowired private ISysUserService userService;

    @Operation(summary = "查询用户列表")                            // 🔶 springdoc
    @Parameters({
        @Parameter(name = "pageNum", description = "页码", in = ParameterIn.QUERY),
        @Parameter(name = "pageSize", description = "每页条数", in = ParameterIn.QUERY)
    })
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysUser user) {
        startPage();
        return getDataTable(userService.selectUserList(user));
    }

    @Operation(summary = "新增用户")
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Valid @RequestBody SysUser user) {       // 🔶 @Valid 来自 jakarta
        return toAjax(userService.insertUser(user));
    }

    @Operation(summary = "导出用户")
    @PreAuthorize("@ss.hasPermi('system:user:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysUser user) {   // 🔶 jakarta
        // ...
    }
}
```

---

## 7. 审批旁路(本分支不实现)

> `springboot2-extend-flowable` / `springboot3-extend-flowable` 分支提供:`extend-flowable` 模块 + Flowable + `basicdb_flowable` schema。
> 本分支保留以下钩子位点(无实现):
>
> - `BusinessType.GRANT` 注解预留给工作流触发
> - `extend-*` 模式:把新模块挂到 `dependencyManagement` 即插即用
> - `gen_table` 字段预留 `is_workflow` 标志(待定)

---

## 8. 审计/报表/Web/安全

### 8.1 审计(🟰 与 `master` 一致,🔶 yauaa)

- 切面签名:`@Log(title="用户管理", businessType=BusinessType.UPDATE, operatorType=OperatorType.MANAGE)`
- 排除参数:`@Log(ignoreParam=true)` 时 JSON 参数整体不存
- 保存字段:operId / title / businessType / method / requestMethod / operatorType / operName / deptName / operUrl / operIp / operParam / jsonResult / status / errorMsg / operTime / costTime
- 🔶 **登录日志的 `browser` / `os` 字段由 yauaa 解析**(原 bitwalker),`"Unknown"` 值建议置空

### 8.2 报表(🟰 与 `master` 一致;🔶 poi 5.5.1)

- 操作日志列表:`PageHelper.startPage()` → `SysOperLogMapper.selectOperLogList`
- 导出:`@Excel` 注解 + `ExcelUtil<>.exportExcel(response, list, "操作日志")`
- 监控:`ServerController` 实时返回 CPU/内存/磁盘/JVM(**oshi 7.4.4**)

> 🔶 poi 4 → 5 的 API 差异点:`getCellType()` 返回值枚举变化、`DataFormatter` 用法、`HSSFColor` 常量。本分支 `ExcelUtil` 已适配。

### 8.3 Web 端(`web-ui`,🔶 零改动)

- 入口:`web-ui/src/main.js`(**JS,未动**)→ `permission.js` → `router.beforeEach`
- 鉴权:路由级 `meta.roles`/`meta.perms`;指令级 `v-hasPermi`/`v-hasRole`;工具 `hasPermi()`/`hasRole()`
- 网络:`utils/request.js` 用 axios 拦截器统一处理 token / 401 / 错误码
- 锁屏:`store/modules/lock.js` + 路由级 `screen-lock.vue`
- 上传:`components/ImageUpload` / `FileUpload` → `POST /common/upload`

> 🔶 **前端唯一可能受影响处**:若硬编码了 `/swagger-ui.html`,需改 `/swagger-ui/index.html`。本仓库前端未硬编码。

### 8.4 移动端(`web-mp`,🔶 零改动)

- 入口:`src/main.js`(**JS,未动**)→ `createSSRApp` + Pinia + 显式注册 `up-` 组件
- 鉴权:`pages.json` 静态 + `permission.js` 全局守卫;`v-if="hasPermi([...])"`
- 网络:`utils/request.js` 拦截器(401 跳登录)
- 加密:`utils/jsencrypt.js` 用公钥加密 password
- 存储:token 存 `uni.setStorageSync('Basic-Token')`
- 配置:`src/config.js` 维护 `baseUrl`

### 8.5 安全(🟰 与 `master` 一致;🔶 Security 6)

| 项 | 实现 |
|---|---|
| 密码 | BCrypt(内置 `BCryptPasswordEncoder`) |
| 登录传输 | 前端 `jsencrypt` RSA 加密 password |
| Token | HS512 JWT,`token.header=Authorization`,`token.expireTime=30` 分钟 |
| Token 存储 | Redis,key=`login_tokens:<uuid>`,与 JWT 解析后比对 |
| 防重放 | 同一 token 在 30min 内可用,期间可 `delLoginUser` 强退 |
| 跨域 | `CorsFilter` + `SecurityConfig` 显式白名单 |
| XSS | `XssFilter` 包装 request(jakarta);`@Xss` 注解字段级 |
| SQL 注入 | MyBatis-Plus `#{}` 参数化 + `SqlUtil.filterKeyword` |
| 防爆破 | `RateLimiter` 注解(默认 5 QPS/10s) |
| 重复提交 | `RepeatSubmit` 注解 + `SameUrlDataInterceptor` |
| 操作日志 | 不可关闭 |
| 登录日志 | 不可关闭 |
| **🔶 框架版本** | **Spring Security 6.x**(Security 5.5 的 EOL 风险已消除) |

---

## 9. 错误码

### 9.1 业务错误码(🟰 与 `master` 完全一致)

| 类别 | code | 含义 | 触发场景 |
|---|---|---|---|
| HTTP | 200 | 成功 | — |
| HTTP | 400 | 参数校验失败 | `MethodArgumentNotValidException` / `ConstraintViolationException` |
| HTTP | 401 | 未登录 / token 失效 | `AuthenticationEntryPointImpl` |
| HTTP | 403 | 无权限 | `@PreAuthorize` 拒绝 |
| HTTP | 404 | 接口不存在 | Spring 默认 |
| HTTP | 429 | 重复提交 / 限流 | `RepeatSubmitInterceptor` / `RateLimiterAspect` |
| HTTP | 500 | 业务异常 | `ServiceException` / `GlobalException` / `TaskException` |
| 业务 | `1001` | 验证码错误 | `CaptchaException` |
| 业务 | `1002` | 验证码过期 | `CaptchaExpireException` |
| 业务 | `1003` | 用户名或密码错误 | `UserPasswordNotMatchException` |
| 业务 | `1004` | 账号已停用 | `UserException("user.blocked")` |
| 业务 | `1005` | 角色键已存在 | `checkRoleKeyUnique` |
| 业务 | `1006` | 用户名已存在 | `checkUserNameUnique` |
| 业务 | `1007` | 不允许修改超级管理员 | `User.isAdmin(...)` |
| 业务 | `1008` | 不允许删除超级管理员 | `User.isAdmin(...)` |
| 业务 | `1009` | 上级目录不允许是本级或下级 | `SysMenu` 自引用 |
| 业务 | `1010` | Cron 表达式不合法 | `CronUtils.isValid` |
| 业务 | `1011` | Job 目标字符串不合法 | `ScheduleUtils.isValidInvokeTarget` |
| 业务 | `1012` | 已存在同名通知已读记录 | 唯一索引兜底 |

> 🔶 **对外错误码零变化**:前端 `utils/errorCode.js`(JS,未改动)无需调整。

### 9.2 🔶 升级期启动/运行错误(非业务错误码,排障用)

| 现象(异常/日志) | 根因 | 处置 |
|---|---|---|
| `java.lang.NoClassDefFoundError: javax/servlet/Filter` | jakarta 迁移不彻底 | `grep import javax.servlet` 清零 |
| `java.lang.NoClassDefFoundError: javax/validation/...` | validation 未迁移 | 换 `jakarta.validation.*` |
| `Failed to configure a DataSource: 'url' attribute is not specified` | 用了 SB2 的 druid starter | 换 `druid-spring-boot-3-starter` |
| `NoSuchBeanDefinitionException: SqlSessionFactory` | 用了 `mybatis-plus-boot-starter` | 换 `mybatis-plus-spring-boot3-starter` |
| `cannot find symbol: class Api` | springfox 注解残留 | 换 `@Tag` |
| `ClassNotFoundException: javax.xml.bind.DatatypeConverter` | 缺 JAXB | 加 `jaxb-api` 2.3.1 |
| `ClassNotFoundException: nl.bitwalker.useragentutils.UserAgent` | bitwalker 已移除 | 改 yauaa |
| `/swagger-ui/index.html` 404 | 白名单缺失 / 依赖缺失 | 放行 `/swagger-ui/**` + `/v3/api-docs/**` |
| 启动告警 `spring.redis.host is deprecated` | 配置键未迁 | 改 `spring.data.redis.host` |
| `Unsupported class file major version 61` | 用 JDK 11 编译 JDK 17 目标 | IDE/Maven 切 JDK 17 |
| 白名单 URL 不生效(`/**` 不匹配) | SB 3 `PathPatternParser` 默认 | 检查 `PermitAllUrlProperties` 的 `antPathMatcher` 注入 |

---

## 10. 并发与线程模型(🟰 与 `master` 一致)

| 资源 | 位置 | 配置 |
|---|---|---|
| Tomcat 线程池 | `application.yml` | `max-threads=800` / `min-spare=10` / `accept-count=1000` |
| 异步任务线程池 | `ThreadPoolConfig` | `corePoolSize=10` / `maxPoolSize=50` / `queueCapacity=200` |
| Druid 连接池 | `application-druid.yml` | `min-idle=5` / `max-active=20` |
| Quartz 线程池 | `ScheduleConfig` | 默认 25 |
| Redis 缓存 | `RedisCache` | TTL 30min |
| 限流(单机) | Guava | `@RateLimiter(time=10, count=5)` |
| 限流(集群) | Redis | `@RateLimiter(type=CLUSTER)` |
| 重复提交 | Redis | 默认 500ms |
| **🔶 yauaa 解析** | `UserAgentAnalyzer` | `withCache(1000)` LRU,避免重复解析 |

**锁使用**:`qrtz_locks` 由 Quartz 维护;业务无显式分布式锁(单机),多实例靠 Quartz `@DisallowConcurrentExecution`。

---

## 11. 测试关注点

### 11.1 业务回归(TC-01~TC-20,🟰 与 `master` 一致)

| # | 场景 | 重点 |
|---|---|---|
| TC-01 | 登录:账号+密码+验证码 三要素 | 错误码 1001/1002/1003 |
| TC-02 | 登录:密码 RSA 加密错误 | 500 + 明确 msg |
| TC-03 | Token 失效:30min 后访问 | 401 |
| TC-04 | 强退:`monitor/online/forceLogout` | 该 token 立即失效 |
| TC-05 | 角色赋菜单:删菜单 → 下次登录路由消失 | 列表 200 + 路由变 |
| TC-06 | 数据权限:`dataScope=4(本人)` | SQL 注入 `create_by=?` |
| TC-07 | 重复提交 | 500ms 内 → 429 |
| TC-08 | 限流 | 5QPS/10s 第 6 次 → 拒绝 |
| TC-09 | XSS 注入 | `<script>alert(1)</script>` 被过滤 |
| TC-10 | 字典缓存 | 修改字典后 `DictUtils.clearCache()` → 新值 |
| TC-11 | 定时任务:启用/暂停/执行 | Quartz 状态切换 |
| TC-12 | 定时任务:并发禁止 | `concurrent=1` 只跑一份 |
| TC-13 | 代码生成:导入 `sys_demo` → 下载 zip | 路径/字段/模板正确 |
| TC-14 | 操作日志:成功/失败均落库 | 异步不阻塞 |
| TC-15 | 移动端 H5 登录 | token 写入 uni.storage |
| TC-16 | 移动端 Token 失效 | 拦截器跳登录 |
| TC-17 | 锁屏解锁 | 密码错 → 锁屏;对 → 解锁 |
| TC-18 | 通知已读 | 重复点 → 不重复加已读 |
| TC-19 | 文件上传:超 10MB | `FileSizeLimitExceededException` |
| TC-20 | 优雅停机:`POST /monitor/shutdown` | 应用退出无残留事务 |

### 11.2 🔶 升级专项(TC-21~TC-32)

| # | 场景 | 重点 |
|---|---|---|
| **TC-21** | JDK 17 下 `mvn clean compile` | **0 ERROR**;确认 IDE/Maven 均用 JDK 17 |
| **TC-22** | `mvn spring-boot:run` 启动 | 无 `NoClassDefFoundError`;日志「基础平台启动成功」 |
| **TC-23** | `grep "import javax.servlet" --include=*.java` | **0 命中** |
| **TC-24** | `grep "import javax.validation"` / `javax.annotation` | **0 命中**(JAXB 的 `javax.xml.bind` 允许) |
| **TC-25** | `grep "io.swagger.annotations"` | **0 命中** |
| **TC-26** | `grep "mybatis-plus-boot-starter"` / `"druid-spring-boot-starter"`(非 SB3 变体) | **0 命中** |
| **TC-27** | `grep "spring\.redis\." --include=*.yml` | **0 命中**(应为 `spring.data.redis.`) |
| **TC-28** | `mvn dependency:tree` | 无 `javax.servlet` 传递依赖 |
| **TC-29** | 访问 `/swagger-ui/index.html` | 200,分组与接口可见,Authorize 按钮存在 |
| **TC-30** | 访问 `/v3/api-docs` | 返回合法 OpenAPI 3 JSON(含 `openapi: "3.0.x"`) |
| **TC-31** | 登录后查 `sys_logininfor` | `browser` / `os` 字段有值(yauaa 生效,非 `Unknown`/空) |
| **TC-32** | 前端零改动验证 | `git diff master -- web-ui web-mp` 为空;两端登录/CRUD 正常 |

---

## 12. 需求变更记录

| 需求 | 落点 |
|---|---|
| 锁屏解锁 | §4.2(前端 `store/modules/lock.js`,**未改动**)+ §6.1 + §8.3 |
| 移动端双前端 | §8.4(**未改动**) |
| 数据库 PostgreSQL | §3.1 + §8.5 + 《数据库设计》(**零变更**) |
| 国际化(词条待补) | §8.4(vue-i18n) |
| 操作日志 | §3.4 + §8.1 |
| 重复提交 | §3.5 |
| 限流 | §3.6 |
| XSS | §3.7(jakarta) |
| Quartz 动态任务 | §4.3(quartz 单独依赖) |
| 代码生成 | §4.4(Velocity 2.3;⚠️ 模板未适配 SB3) |
| 数据权限 | §3 + 《需求文档》§8.2 |
| **🔶 JDK 11 → 17 + SB 2.5.14 → 3.5.16** | **§1.2 文件台账 + §3.9.1~3.9.7 升级机制 + §9.2 排障表 + §11.2 TC-21~TC-32** |
| **🔶 Jakarta EE 迁移** | §3.9.1(替换规则 + 例外 + 校验命令) |
| **🔶 Security 5 → 6** | §3.9.2(改造前后完整代码 + 差异表) |
| **🔶 springfox → springdoc** | §3.9.3(SwaggerConfig 实现 + 注解对照 + Controller 范本 + yml) |
| **🔶 bitwalker → yauaa** | §3.9.4(`UserAgentConfig` + `AsyncFactory` 改造) |
| **🔶 starter 换名** | §3.9.5(Druid / MP 依赖锚点 + 包名变更) |
| **🔶 ResourcesConfig 改造** | §3.9.6(`WebMvcConfigurerAdapter` → `WebMvcConfigurer`) |
| **🔶 配置键迁移** | §3.9.7(`application*.yml` 片段) |
| **🔶 API 文档引擎** | §4.6(生成链路 + Bearer 配置 + 已知限制) |

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `master` LLD(v0.1)为基线;后端 §2~§8 业务实现 / §10 并发 / §11.1 业务回归标注「与 master 一致」;新增 §1.2 升级文件台账(8 个 pom + 6 个配置类 + 11 个 Filter/Handler + 20+ 工具实体 + 26 个 Controller + 3 个 yml)、§3.9 升级机制 7 小节(jakarta 迁移 / Security 6 完整代码 / springdoc 配置与注解对照 / yauaa / starter 适配 / ResourcesConfig / yml 迁移)、§4.1 登录引擎 yauaa 改造、§4.6 springdoc 引擎、§6.6 升级后 Controller 范本、§9.2 升级期排障表(11 类)、§11.2 TC-21~TC-32 |
