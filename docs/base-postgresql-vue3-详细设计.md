# 基础平台(base-postgresql-vue3) 详细设计(LLD)

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 `master` 分支
> 前置依赖:《架构方案》v0.1、《需求文档》v0.1、《概要设计》v0.1
> 本文档细化到「类 / 方法 / 关键逻辑」,开发可直接编码,测试据此写用例。

---

## 1. 总体包结构

```
com.basic
├── BasicApplication                # 启动类
├── BasicServletInitializer         # war 启动适配
├── web
│   ├── config/SwaggerConfig
│   └── controller
│       ├── common/{CaptchaController, CommonController}
│       ├── monitor/{CacheController, ServerController, SysLogininforController, SysOperlogController, SysUserOnlineController}
│       ├── system/{SysConfigController, SysDeptController, SysDictDataController, SysDictTypeController, SysIndexController, SysLoginController, SysMenuController, SysNoticeController, SysPostController, SysProfileController, SysRegisterController, SysRoleController, SysUserController}
│       └── tool/{SwaggerController, TestController}
├── framework
│   ├── aspectj/{DataScopeAspect, DataSourceAspect, LogAspect, RateLimiterAspect}
│   ├── config/{ApplicationConfig, CaptchaConfig, DruidConfig, FastJson2JsonRedisSerializer, FilterConfig, JacksonConfig, KaptchaTextCreator, MyBatisConfig, MybatisPlusConfig, RedisConfig, ResourcesConfig, SecurityConfig, ServerConfig, ThreadPoolConfig, properties/*}
│   ├── datasource/{DynamicDataSource, DynamicDataSourceContextHolder}
│   ├── interceptor/{RepeatSubmitInterceptor, impl/SameUrlDataInterceptor}
│   ├── jackson/BigNumberSerializer
│   ├── manager/{AsyncManager, ShutdownManager, factory/AsyncFactory}
│   ├── security
│   │   ├── filter/JwtAuthenticationTokenFilter
│   │   └── handle/{AuthenticationEntryPointImpl, LogoutSuccessHandlerImpl, MyMetaObjectHandler}
│   ├── web
│   │   ├── domain/{Server, server/{Cpu, Jvm, Mem, Sys, SysFile}}
│   │   ├── exception/GlobalExceptionHandler
│   │   └── service/{PermissionService, SysLoginService, SysPermissionService, SysRegisterService, TokenService, UserDetailsServiceImpl}
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
├── quartz (来自 extend-quartz)
│   ├── config/ScheduleConfig
│   ├── controller/{SysJobController, SysJobLogController}
│   ├── domain/{SysJob, SysJobLog}
│   ├── mapper/{SysJobMapper, SysJobLogMapper}
│   ├── service/{ISysJobService, ISysJobLogService, impl/*}
│   ├── task/RyTask
│   └── util/{AbstractQuartzJob, CronUtils, JobInvokeUtil, QuartzDisallowConcurrentExecution, QuartzJobExecution, ScheduleUtils}
├── generator (来自 extend-generator)
│   ├── config/GenConfig
│   ├── controller/GenController
│   ├── domain/{GenTable, GenTableColumn}
│   ├── mapper/{GenTableMapper, GenTableColumnMapper}
│   ├── service/{IGenTableService, IGenTableColumnService, impl/*}
│   └── util/{GenUtils, VelocityInitializer, VelocityUtils}
└── system (来自 basic-admin-core)
    ├── domain/{SysConfig, SysFiles, SysLogininfor, SysNotice, SysNoticeRead, SysOperLog, SysPost, SysRoleDept, SysRoleMenu, SysUserOnline, SysUserPost, SysUserRole, vo/{MetaVo, RouterVo}}
    ├── mapper/{SysConfigMapper, SysDeptMapper, SysDictDataMapper, SysDictTypeMapper, SysFilesMapper, SysLogininforMapper, SysMenuMapper, SysNoticeMapper, SysNoticeReadMapper, SysOperLogMapper, SysPostMapper, SysRoleDeptMapper, SysRoleMapper, SysRoleMenuMapper, SysUserMapper, SysUserPostMapper, SysUserRoleMapper}
    └── service/{ISysConfigService, ISysDeptService, ISysDictDataService, ISysDictTypeService, ISysFilesService, ISysLogininforService, ISysMenuService, ISysNoticeReadService, ISysNoticeService, ISysOperLogService, ISysPostService, ISysRoleService, ISysUserOnlineService, ISysUserService, impl/*}

com.core (来自 business-core)
├── controller/DemoController
├── domain/Demo
├── mapper/DemoMapper
└── service/{DemoService, impl/DemoServiceImpl}
```

> **包命名约定**:`com.basic` = 平台层,`com.core` = 业务层(由 `BasicApplication` 显式扫)。业务侧要扩展时,**优先新增模块**(或新加包),不要修改平台层核心类。

---

## 2. 领域模型与枚举

### 2.1 关键实体(节选)

> 完整字段见《数据库设计》,此处只列关键属性与状态字段。

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

### 2.2 状态枚举

```java
public enum BusinessType {
    OTHER, INSERT, UPDATE, DELETE, GRANT,
    EXPORT, IMPORT, FORCE, GENCODE, CLEAN
}

public enum BusinessStatus {
    SUCCESS(0), FAIL(1);
}

public enum OperatorType {
    OTHER, MANAGE, MOBILE
}

public enum DataSourceType {
    MASTER, SLAVE
}

public enum UserStatus {
    OK("0", "正常"), DISABLE("1", "停用"), DELETED("2", "删除");
}

public enum HttpMethod { GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE, ALL }

public enum LimitType {
    DEFAULT, IP, CLUSTER
}
```

### 2.3 状态合法流转

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

### 3.1 启动与包扫描

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

### 3.2 动态 Schema 路由

```java
@Aspect
@Component
public class DataSourceAspect {
    @Before("@annotation(ds)")
    public void before(DataSource ds) {
        DynamicDataSourceContextHolder.setDataSourceType(ds.value().name());
    }
    @After("@annotation(ds)")
    public void after(DataSource ds) {
        DynamicDataSourceContextHolder.clearDataSourceType();
    }
}
```

- 失败/异常路径由 `@After` 兜底清空,不会污染 ThreadLocal
- 默认走 `MASTER`

### 3.3 事务边界

- **写操作事务**:Service 实现类上 `@Transactional(rollbackFor = Exception.class)`
- **异步日志**:`AsyncManager.me().execute(AsyncFactory.recordOper(...))` 在 `AsyncFactory` 内部单独 `@Transactional`,与主业务事务隔离
- **跨表写入**(角色赋菜单 + 角色赋部门):`SysRoleServiceImpl` 入口单事务,内部 `updateRole -> deleteRoleMenu -> batchRoleMenu -> deleteRoleDept -> batchRoleDept`,任一失败整体回滚

### 3.4 审计切面

```java
@Aspect
@Component
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
        } finally {
            // 耗时统计
        }
    }
}
```

- 切面**不**捕获异常,只记录,异常仍往外抛
- `handleLog` 内通过 `AsyncManager` 异步落 `sys_oper_log`,避免阻塞
- 排除条件:`@Log(ignoreParam=true)` / `exclusionAttribute` 白名单(`password` / `oldPassword` / `newPassword` / `confirmPassword`)

### 3.5 重复提交拦截

```java
@Component
public class SameUrlDataInterceptor extends RepeatSubmitInterceptor {
    @Override
    boolean isRepeatSubmit(HttpServletRequest req, String nowParams) {
        String cacheKey = "REPEAT_SUBMIT:" + req.getSession().getId() + ":" + req.getRequestURI();
        String prev = redisCache.getCacheObject(cacheKey);
        if (prev != null && prev.equals(nowParams)) {
            long interval = redisCache.getCacheExpire(cacheKey);
            return interval > 0; // 仍在过期时间窗内 → 重复
        }
        redisCache.setCacheObject(cacheKey, nowParams, intervalSeconds);
        return false;
    }
}
```

- 间隔默认 500ms(由 `@RepeatSubmit(interval = 500)` 配置)
- 命中重复 → 抛 `ServiceException("请勿重复提交")`,由 `GlobalExceptionHandler` 包装为 429

### 3.6 限流

```java
@Aspect
@Component
public class RateLimiterAspect {
    @Before("@annotation(rateLimiter)")
    public void doBefore(RateLimiter rateLimiter) {
        RateType type = RateType.valueOf(rateLimiter.rateType().name());
        RateLimiter limiter = ... ;
        if (!limiter.tryAcquire(...)) throw ServiceException("访问频繁");
    }
}
```

- `LimitType.DEFAULT` → Guava 单机
- `LimitType.IP` → 按 IP 维度
- `LimitType.CLUSTER` → 走 Redis 分布式

### 3.7 XSS

- `XssFilter` + `XssHttpServletRequestWrapper` 包装 `HttpServletRequest`
- 仅对 `application/x-www-form-urlencoded` 与 `application/json` 过滤
- 通过 `XssValidator(JSR-303)` 配合 `@Xss` 在字段上做入参校验(双重保险)

### 3.8 MyBatis-Plus 字段填充

```java
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override public void insertFill(MetaObject meta) {
        this.strictInsertFill(meta, "createTime", Date.class, new Date());
        this.strictInsertFill(meta, "createBy", String.class, SecurityUtils.getUsername());
    }
    @Override public void updateFill(MetaObject meta) {
        this.strictUpdateFill(meta, "updateTime", Date.class, new Date());
        this.strictUpdateFill(meta, "updateBy", String.class, SecurityUtils.getUsername());
    }
}
```

---

## 4. 核心引擎详细设计

### 4.1 登录引擎(`SysLoginService`)

**核心方法签名**

```java
public class SysLoginService {
    public LoginUser login(String username, String password, String code, String uuid) {
        validateCaptcha(uuid, code);                // 1. 校验图形验证码
        // 2. 加载用户
        UserDetails u = userDetailsService.loadUserByUsername(username);
        if (u == null) throw UserPasswordNotMatchException("账号不存在");
        // 3. 校验密码
        if (!bCrypt.matches(password, u.getPassword())) {
            asyncFactory.recordLogininfor(username, FAIL, "密码错误");
            throw UserPasswordNotMatchException();
        } else if (UserStatus.DISABLE.code.equals(u.getStatus())) {
            asyncFactory.recordLogininfor(username, FAIL, "账号停用");
            throw new ServiceException("账号已被停用,请联系管理员");
        }
        // 4. 签发 token
        LoginUser loginUser = (LoginUser) u;
        tokenService.createToken(loginUser);
        asyncFactory.recordLogininfor(username, SUCCESS, "登录成功");
        return loginUser;
    }
    public void logout(LoginUser loginUser) { tokenService.delLoginUser(loginUser.getToken()); }
}
```

**`TokenService.createToken`**

```java
public String createToken(LoginUser loginUser) {
    String token = IdUtils.fastUUID();
    loginUser.setToken(token);
    setUserAgent(loginUser);                     // 解析 UA → 浏览器/OS
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

**`JwtAuthenticationTokenFilter`**

```java
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) {
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

### 4.2 锁屏/解锁实现

> **不新增接口**。`SysLoginService` 内部已支持「复用 token」场景(从前端 `lock-password` 传明文,后端仍走 BCrypt 比对)。
>
> 实际路径:`web-ui/store/modules/lock.js` 维护 `locked` 状态,提交时直接调 `/login`,**后端不感知是锁屏解锁**。

```js
// store/modules/lock.js(节选)
unlock(password) {
  return login({ username: this.userName, password, code: 'LOCK_BYPASS', uuid: 'LOCK' })
    .then(() => { this.locked = false; ... });
}
```

> 锁屏前端的 `_self.lock` 状态、`Basic-Token` 一直保留在 cookie;不重新签发。

### 4.3 定时任务引擎

**新增任务**

```java
// SysJobServiceImpl
@Override
@Transactional
public int add(SysJob job) {
    job.setStatus(ScheduleConstants.Status.PAUSE.getValue());
    int rows = jobMapper.insert(job);
    if (rows > 0) ScheduleUtils.createScheduleJob(scheduler, job);
    return rows;
}

@Override
@Transactional
public int changeStatus(SysJob job) {
    int rows = jobMapper.updateById(job);
    if (rows > 0) {
        if (ScheduleConstants.Status.NORMAL.getValue().equals(job.getStatus()))
            ScheduleUtils.resumeJob(scheduler, job);
        else ScheduleUtils.pauseJob(scheduler, job);
    }
    return rows;
}

@Override
@Transactional
public boolean run(SysJob job) {
    boolean result = ScheduleUtils.run(scheduler, job);
    if (result) {
        SysJobLog log = new SysJobLog();
        BeanUtils.copy(job, log);
        log.setStatus(Constants.SUCCESS);
        log.setJobMessage("执行成功");
        jobLogMapper.insert(log);
    }
    return result;
}
```

**Job 执行抽象**

```java
public abstract class AbstractQuartzJob implements Job {
    @Override public void execute(JobExecutionContext context) {
        SysJob sysJob = (SysJob) context.getMergedJobDataMap().get(ScheduleConstants.TASK_PROPERTIES);
        try {
            before(context, sysJob);
            doExecute(context, sysJob);
            after(context, sysJob, null);
        } catch (Exception e) {
            after(context, sysJob, e);
            throw new TaskException(...);
        }
    }
    protected void before(JobExecutionContext ctx, SysJob job) { ... }   // 记录开始时间
    protected abstract void doExecute(JobExecutionContext ctx, SysJob job) throws Exception;
    protected void after(JobExecutionContext ctx, SysJob job, Exception e) { ... }  // 写 sys_job_log
}
```

**反射调用**

```java
public class JobInvokeUtil {
    public static void invokeMethod(SysJob sysJob) throws Exception {
        String invokeTarget = sysJob.getInvokeTarget();
        String beanName = StringUtils.substringBefore(invokeTarget, ".");
        String methodName = StringUtils.substringAfter(invokeTarget, ".");
        Object bean = SpringUtils.getBean(beanName);
        if (StringUtils.contains(methodName, "(")) {
            // 带参数:ryTask.ryParams('a','b')
            // 用 java.text.SimpleExpression 或自实现解析
            Method m = ...; m.invoke(bean, args);
        } else {
            Method m = bean.getClass().getMethod(methodName);
            m.invoke(bean);
        }
    }
}
```

**自定义 Job 示例**(`extend-quartz/task/RyTask.java`)

```java
@Component("ryTask")
public class RyTask {
    public void ryNoParams() { log.info("执行无参方法"); }
    public void ryParams(String s, Integer i) { log.info("执行有参方法:{},{}", s, i); }
    public void ryMultipleParams(String s, Long l, Double d) { ... }
}
```

> 在管理界面配置 `invokeTarget=ryTask.ryNoParams` 即可。

### 4.4 代码生成引擎

**导入表**

```java
@Override
@Transactional
public void importGenTable(String tableName) {
    GenTable table = genTableMapper.selectDbTableListByNames(Collections.singletonList(tableName)).get(0);
    initTable(table, "sys");
    int row = genTableMapper.insert(table);
    if (row > 0) {
        List<GenTableColumn> columns = genTableColumnMapper.selectDbTableColumnsByName(tableName);
        for (GenTableColumn c : columns) initColumnField(c, table);
        genTableColumnMapper.insertBatch(columns);
    }
}
```

**生成**

```java
@Override
public byte[] downloadCode(String[] tableNames) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ZipOutputStream zip = new ZipOutputStream(out);
    for (String name : tableNames) {
        GenTable t = genTableMapper.selectGenTableByName(name);
        generatorCode(t, zip);   // VelocityUtils 渲染 .vm
    }
    IOUtils.closeQuietly(zip);
    return out.toByteArray();
}
```

模板位于 `main/resources/vm/`:
- `vm/java/Controller.java.vm`
- `vm/java/Service.java.vm` / `ServiceImpl.java.vm`
- `vm/java/Mapper.java.vm` / `Mapper.xml.vm`
- `vm/vue/index.vue.vm` / `api.js.vm` / `index-tree.vue.vm` / `index.vue.vm`
- `vm/sql/sql.vm` / `vm/js/api.js.vm`

### 4.5 动态路由/菜单(`SysMenuServiceImpl`)

```java
public List<RouterVo> buildMenus(List<SysMenu> menus) {
    List<RouterVo> routers = new LinkedList<>();
    for (SysMenu menu : menus.stream().filter(m -> "C".equals(m.getMenuType())).collect(Collectors.toList())) {
        RouterVo v = new RouterVo();
        v.setName(menu.getRouteName());
        v.setPath(menu.getRouterPath());
        v.setComponent(StringUtils.isEmpty(menu.getComponent()) ? Layout : menu.getComponent());
        v.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), !menu.getIsCache(), menu.getPath()));
        List<SysMenu> cMenus = menus.stream().filter(m -> menu.getMenuId().equals(m.getParentId()))
            .collect(Collectors.toList());
        if (!cMenus.isEmpty() && "M".equals(menu.getMenuType())) v.setAlwaysShow(true);
        v.setChildren(buildMenus(cMenus));
        routers.add(v);
    }
    return routers;
}
```

---

## 5. 校验引擎

### 5.1 L1 字段级校验

- 入口:`@Validated` + `javax.validation.constraints.*`
- 规则实现:`XssValidator`(自定义)+ 内置 `@NotBlank` / `@Email` / `@Size` / `@Pattern` / `@Excel`
- 失败处理:由 `GlobalExceptionHandler` 捕获 `MethodArgumentNotValidException` / `ConstraintViolationException`,落 400 + msg

### 5.2 L2 跨业务校验(均在 Service 显式实现)

| 校验项 | 位置 | 失败动作 |
|---|---|---|
| 用户名唯一 | `SysUserServiceImpl.checkUserNameUnique` | 抛 `UserException("user.register.not.unique")` |
| 角色 key 唯一 | `SysRoleServiceImpl.checkRoleKeyUnique` | 抛 `ServiceException` |
| 菜单不能关联自身为子 | `SysMenuServiceImpl` | 抛 `ServiceException` |
| 部门父级链路不能形成环 | `SysDeptServiceImpl.checkParentIdExist` | 抛 `ServiceException` |
| 通知发布必须先选 `status=1` | `SysNoticeServiceImpl` | 抛 `ServiceException` |
| 定时 invokeTarget 合法 | `ScheduleUtils` | 抛 `TaskException` |
| 字典值类型内唯一 | `SysDictDataServiceImpl.checkDictValueUnique` | 抛 `ServiceException` |
| 已读记录唯一 | `SysNoticeReadServiceImpl` | 走 `insertOrUpdate` / 业务上幂等 |

> L2 失败**不会触发 `sys_oper_log` 失败状态**(因为是 throw);但**异步日志会记 SUCCESS**(切面在 finally 块?不,在 around 正常 return 时记 SUCCESS,异常时记 FAIL,需根据切面实际行为校准)。

---

## 6. 业务模块实现(节选)

### 6.1 用户管理

`SysUserServiceImpl` 关键方法

```java
@Override
@Transactional
public int addUser(SysUser user) {
    checkUserNameUnique(user);
    if (StringUtils.isNotEmpty(user.getPhonenumber())) checkPhoneUnique(user);
    if (StringUtils.isNotEmpty(user.getEmail())) checkEmailUnique(user);
    user.setPassword(bCrypt.encode("123456"));             // 默认密码
    return userMapper.insert(user);
}

@Override
@Transactional
public int updateUser(SysUser user) {
    // 1) 校验唯一
    // 2) 不允许修改超级管理员
    if (User.isAdmin(user.getUserId())) throw ServiceException("不允许修改超级管理员");
    // 3) 清空 redis 缓存
    redisCache.deleteObject(CacheConstants.SYS_USER_CACHE_KEY + user.getUserId());
    // 4) 更新 user / user_role / user_post
    userMapper.updateById(user);
    userRoleMapper.deleteUserRoleByUserId(user.getUserId());
    if (user.getRoleIds() != null) insertUserRole(user);
    userPostMapper.deleteUserPostByUserId(user.getUserId());
    if (user.getPostIds() != null) insertUserPost(user);
    return 1;
}

@Override
@Transactional
public int removeUserByIds(Long[] ids) {
    if (ArrayUtils.contains(ids, 1L)) throw ServiceException("不允许删除超级管理员");
    int rows = userMapper.deleteBatchIds(Arrays.asList(ids));
    // 级联删 user_role / user_post
    userRoleMapper.deleteUserRole(ids);
    userPostMapper.deleteUserPost(ids);
    return rows;
}

@Override
public int resetPwd(SysUser user) {
    user.setPassword(bCrypt.encode(user.getPassword()));
    return userMapper.updateById(user);
}
```

### 6.2 角色管理

```java
@Override
@Transactional
public boolean checkRoleKeyUnique(SysRole role) {
    Long id = StringUtils.isNull(role.getRoleId()) ? -1L : role.getRoleId();
    SysRole info = roleMapper.checkRoleKeyUnique(role.getRoleKey());
    if (info != null && !info.getRoleId().equals(id)) throw ServiceException("角色键已存在");
    return true;
}

@Override
@Transactional
public int authDataScope(SysRole role) {
    int rows = roleMapper.updateById(role);
    if (rows > 0) {
        roleDeptMapper.deleteRoleDeptByRoleId(role.getRoleId());
        insertRoleDept(role);
    }
    return rows;
}
```

### 6.3 菜单管理

```java
@Override
public List<SysMenu> treeselectByUserId(Long userId) {
    List<SysMenu> menus;
    if (User.isAdmin(userId)) menus = menuMapper.selectMenuTreeAll();
    else menus = menuMapper.selectMenuTreeByUserId(userId);
    return menuMapper.buildMenuTree(menus);
}
```

### 6.4 通知已读(`SysNoticeRead`)

```java
@Override
public int readOnce(Long userId, Long noticeId) {
    SysNoticeRead read = new SysNoticeRead();
    read.setUserId(userId);
    read.setNoticeId(noticeId);
    read.setReadTime(new Date());
    // 唯一索引 (user_id, notice_id) 兜底
    return noticeReadMapper.insertOrUpdate(read);
}
```

### 6.5 Demo 业务(`business-core`)

```java
@RestController
@RequestMapping("/demo")
public class DemoController extends BaseController {
    @Autowired private IDemoService demoService;
    @PreAuthorize("@ss.hasPermi('demo:demo:list')")
    @GetMapping("/list")
    public TableDataInfo list(Demo d) { startPage(); return getDataTable(demoService.list(d)); }
    @PreAuthorize("@ss.hasPermi('demo:demo:add')")
    @PostMapping
    public AjaxResult add(@RequestBody Demo d) { return toAjax(demoService.add(d)); }
}
```

---

## 7. 审批旁路(本分支不实现)

> `master-extend-flowable` 分支提供:`extend-flowable` 模块 + Flowable 6.8.0 + `basicdb_flowable` schema。
> 本分支保留以下钩子位点(无实现):
>
> - `BusinessType.GRANT` 注解预留给工作流触发
> - `extend-*` 模式:把新模块挂到 `dependencyManagement` 即插即用
> - `gen_table` 字段预留 `is_workflow` 标志(待定)

---

## 8. 审计/报表/Web/安全

### 8.1 审计(`LogAspect` + `AsyncManager`)

- 切面签名:`@Log(title="用户管理", businessType=BusinessType.UPDATE, operatorType=OperatorType.MANAGE)`
- 排除参数:`@Log(ignoreParam=true)` 时,JSON 参数整体不存
- 保存字段:operId / title / businessType / method / requestMethod / operatorType / operName / deptName / operUrl / operIp / operParam / jsonResult / status / errorMsg / operTime / costTime

### 8.2 报表

- 操作日志列表:`PageHelper.startPage()` → `SysOperLogMapper.selectOperLogList`
- 导出:`@Excel` 注解 + `ExcelUtil<>.exportExcel(response, list, "操作日志")`
- 监控:`ServerController` 实时返回 CPU/内存/磁盘/JVM(`oshi 6.1.6`)

### 8.3 Web 端(`web-ui`)

- 入口:`web-ui/src/main.js` 引导 `permission.js` → `router.beforeEach` 拦截
- 鉴权:
  - 路由级:`meta.roles` / `meta.perms`
  - 指令级:`v-hasPermi` / `v-hasRole`(由 `directive/permission/*` 注册)
  - 工具:`hasPermi(value)` / `hasRole(value)`(由 `utils/permission.js` 提供)
- 网络:`utils/request.js` 用 axios 拦截器统一处理 token / 401 / 错误码
- 锁屏:`store/modules/lock.js` + 路由级 `screen-lock.vue`
- 上传:`components/ImageUpload` / `FileUpload` → `POST /common/upload` → 返回 OSS/本地路径

### 8.4 移动端(`web-mp`)

- 入口:`web-mp/src/main.js` → `createSSRApp` + Pinia + 显式注册 `up-` 组件
- 鉴权:
  - 路由:`pages.json` 静态 + `permission.js` 全局守卫
  - 权限判断:`v-if="hasPermi([...])"`(uni-app 3 编译期不支持自定义指令,故用函数判断)
- 网络:`utils/request.js` 拦截器(401 跳登录)
- 加密:`utils/jsencrypt.js` 用公钥(`BasicConfig.rsaPublicKey`)加密 password
- 存储:token 存 `uni.setStorageSync('Basic-Token')`
- 配置:`src/config.js` 维护 `baseUrl`(默认 `http://localhost:8080`)

### 8.5 安全

| 项 | 实现 |
|---|---|
| 密码 | BCrypt(内置 `BCryptPasswordEncoder`) |
| 登录传输 | 前端 `jsencrypt` RSA 加密 password |
| Token | HS512 JWT,`token.header=Authorization`,`token.expireTime=30` 分钟 |
| Token 存储 | Redis,key=`login_tokens:<uuid>`,与 JWT 解析后比对 |
| 防重放 | 同一 token 在 30min 内可用,期间被 `delLoginUser` 强退 |
| 跨域 | `CorsFilter`(框架) + `SecurityConfig` 显式白名单 |
| XSS | `XssFilter` 包装 request;`@Xss` 注解字段级 |
| SQL 注入 | MyBatis-Plus `#{}` 参数化 + `SqlUtil.filterKeyword` 校验 |
| 防爆破 | `RateLimiter` 注解(默认 5 QPS/10s) |
| 重复提交 | `RepeatSubmit` 注解 + `SameUrlDataInterceptor` |
| 操作日志 | 不可关闭 |
| 登录日志 | 不可关闭 |

---

## 9. 错误码

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

> 前端通过 `utils/errorCode.js` 做错误码 → 提示文案映射。

---

## 10. 并发与线程模型

| 资源 | 位置 | 配置 |
|---|---|---|
| Tomcat 线程池 | `application.yml` | `max-threads=800` / `min-spare=10` / `accept-count=1000` |
| 异步任务线程池 | `ThreadPoolConfig` | `corePoolSize=10` / `maxPoolSize=50` / `queueCapacity=200` |
| Druid 连接池 | `application-druid.yml` | `min-idle=5` / `max-active=20`(可调) |
| Quartz 线程池 | `ScheduleConfig` | 默认 25 线程 |
| Redis 缓存 | `RedisCache` | TTL 30min(可调) |
| 限流(单机) | Guava | `@RateLimiter(time=10, count=5)` |
| 限流(集群) | Redis | `@RateLimiter(type=CLUSTER)` |
| 重复提交 | Redis | 默认 500ms |

**锁使用**:
- `qrtz_locks` 由 Quartz 维护
- 业务无显式分布式锁(单机场景);若部署多实例,`qrtz_*` 自动 + Quartz 自带 Job 互斥(`@DisallowConcurrentExecution`)

---

## 11. 测试关注点

| # | 场景 | 重点 |
|---|---|---|
| TC-01 | 登录:账号+密码+验证码 三要素 | 错误码 1001/1002/1003 |
| TC-02 | 登录:密码 RSA 加密错误 → 后端解密失败 | 500 + 明确 msg |
| TC-03 | Token 失效:30min 后访问 | 401 |
| TC-04 | 强退:`monitor/online/forceLogout` → 该 token 立即失效 | 401 |
| TC-05 | 角色赋菜单:删菜单 → 已有角色下次登录路由消失 | 列表 200 + 路由变 |
| TC-06 | 数据权限:`dataScope=4(本人)` | SQL 注入 `create_by=?` |
| TC-07 | 重复提交 | 500ms 内 → 429 |
| TC-08 | 限流 | 5QPS/10s 第 6 次 → 拒绝 |
| TC-09 | XSS 注入 | `<script>alert(1)</script>` 入参被过滤 |
| TC-10 | 字典缓存 | 修改字典后调用 `DictUtils.clearCache()` → 下次拿到新值 |
| TC-11 | 定时任务:启用/暂停/执行 | Quartz 状态切换 |
| TC-12 | 定时任务:并发禁止 | `concurrent=1` 同一时刻只跑一份 |
| TC-13 | 代码生成:导入 `sys_demo` → 下载 zip → 解压 | 路径、字段、模板正确 |
| TC-14 | 操作日志:写操作成功/失败均落库 | 异步落库不阻塞 |
| TC-15 | 移动端 H5 登录 | token 写入 uni.storage |
| TC-16 | 移动端 Token 失效 | 拦截器跳登录 |
| TC-17 | 锁屏解锁 | 密码错 → 锁屏;对 → 解锁 |
| TC-18 | 通知已读 | 同一通知重复点 → 不重复加已读 |
| TC-19 | 文件上传:超 10MB | `FileSizeLimitExceededException` |
| TC-20 | 优雅停机:`POST /monitor/shutdown` | 应用退出且无残留事务 |

---

## 12. 需求变更记录

| 需求 | 落点 |
|---|---|
| 锁屏解锁 | §4.2 + §6.1 + §8.3(PC store/lock.js) |
| 移动端双前端 | §8.4 |
| 数据库 PostgreSQL | §3.1 + §8.5 |
| 国际化(词条待补) | §8.4(vue-i18n 引入) |
| 操作日志 | §3.4 + §8.1 |
| 重复提交 | §3.5 |
| 限流 | §3.6 |
| XSS | §3.7 |
| Quartz 动态任务 | §4.3 |
| 代码生成 | §4.4 |
| 数据权限 | §3 + 《需求文档》§8.2 |

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:覆盖包结构/枚举/框架机制/核心引擎/校验/业务实现/审计/错误码/线程/测试 |
