# 基础平台(base-postgresql-vue3) 详细设计(LLD) — `springboot2-tianai-captcha` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot2-tianai-captcha`** 分支
> 前置依赖:《架构方案》v0.1、《需求文档》v0.1、《概要设计》v0.1
> 本文档细化到「类 / 方法 / 关键逻辑」,开发可直接编码,测试据此写用例。
> 本分支相对 `master` 的差异仅在「行为验证码」专题;**其它模块细节(包结构/枚举/框架机制/核心引擎/业务实现/审计/错误码/线程模型/测试关注点)与 `master` 完全一致**,本文件不重复,详见 `docs/base-postgresql-vue3-详细设计.md`(在 `master` 分支生成)。

---

## 1. 总体包结构(本分支独有改动以 **🔸** 标出)

```
com.basic
├── BasicApplication / BasicServletInitializer
├── web
│   ├── config/SwaggerConfig
│   └── controller
│       ├── common
│       │   ├── CaptchaController          (沿用 master 接口,内部改 tianai-captcha)
│       │   ├── CaptchaCheckController 🆕  (本分支新增,POST /captcha/check)
│       │   └── CommonController
│       ├── monitor
│       ├── system
│       └── tool
├── framework
│   ├── aspectj (沿用)
│   ├── config
│   │   ├── ApplicationConfig
│   │   ├── DruidConfig
│   │   ├── FastJson2JsonRedisSerializer
│   │   ├── FilterConfig
│   │   ├── JacksonConfig
│   │   ├── KaptchaTextCreator            ❌ (本分支删除)
│   │   ├── CaptchaConfig                 ❌ (本分支删除)
│   │   ├── MybatisPlusConfig
│   │   ├── MyBatisConfig
│   │   ├── RedisConfig
│   │   ├── ResourcesConfig
│   │   ├── SecurityConfig                (沿用 + 🆕 放行 /captcha/**)
│   │   ├── ServerConfig
│   │   ├── ThreadPoolConfig
│   │   ├── properties/*
│   │   ├── 🔸 TianaiCaptchaConfig        (本分支新增,@Order(0) 资源加载)
│   │   └── 🔸 TypeAwareImageCaptchaValidator (本分支新增,ROTATE 容差 0.10)
│   ├── datasource (沿用)
│   ├── interceptor (沿用)
│   ├── jackson (沿用)
│   ├── manager (沿用)
│   ├── security
│   │   ├── filter/JwtAuthenticationTokenFilter
│   │   └── handle/* (沿用)
│   └── web
│       ├── domain/server/* (沿用)
│       ├── exception/GlobalExceptionHandler
│       └── service
│           ├── PermissionService
│           ├── SysLoginService            (沿用接口,内部加 validateTianaiCaptcha)
│           ├── SysPermissionService
│           ├── SysRegisterService
│           ├── TokenService
│           └── UserDetailsServiceImpl
├── common
│   ├── annotation (沿用)
│   ├── config (沿用)
│   ├── constant (沿用)
│   ├── core
│   │   ├── controller/BaseController
│   │   ├── domain/{AjaxResult, BaseEntity, R, TableDataInfo, TreeEntity, ...}
│   │   │   └── model
│   │   │       ├── LoginBody              (沿用字段,🔸 新增 captchaData)
│   │   │       ├── LoginUser
│   │   │       └── RegisterBody
│   │   ├── page/* (沿用)
│   │   ├── redis/RedisCache                (🔸 新增凭证 key-value 用法)
│   │   └── text/* (沿用)
│   ├── enums (沿用)
│   ├── exception/user
│   │   ├── CaptchaException
│   │   ├── CaptchaExpireException
│   │   └── UserException + ...
│   ├── filter (沿用)
│   ├── utils (沿用)
│   └── xss (沿用)
├── quartz (extend-quartz,沿用)
├── generator (extend-generator,沿用)
└── system (basic-admin-core,沿用)
```

> **包命名约定**:沿用 `master`。新增文件全部在已有包内,不新增包结构。
>
> **删除的文件**:`CaptchaConfig.java` / `KaptchaTextCreator.java`(均为 kaptcha 字符验证码配置类)。`pom.xml` 中 `kaptcha` 依赖保留为死引用,见架构 R1。

---

## 2. 领域模型与枚举

> 沿用 `master`(16 个核心实体 + 11 个状态枚举,详见 `docs/base-postgresql-vue3-详细设计.md` §2)。
>
> 本分支**没有新增领域实体**(验证码实例不落库,只存 Redis 凭证)。

### 2.1 新增的 DTO/参数

```java
// LoginBody (com.basic.common.core.domain.model) — 沿用 master,新增 1 字段
public class LoginBody {
    private String username;
    private String password;       // RSA 密文
    private String code;            // 行为验证码模式下作占位符(兼容 kaptcha)
    private String uuid;            // 验证码 id(由 /captchaImage 返回)
    private String captchaData;     // 🆕 行为验证码轨迹数据(ImageCaptchaTrack 的 JSON 字符串)
    // getter/setter ...
}

// ImageCaptchaTrack (cloud.tianai.captcha.validator.common.model.dto) — tianai-captcha 自带
// 不需要在本仓库定义;前端的轨迹 JSON 序列化后赋值给 LoginBody.captchaData
```

### 2.2 状态枚举(沿用 master)

> 验证码无独立状态枚举(运行期对象由 tianai-captcha 内部维护;凭证由 Redis TTL 自动过期)。

### 2.3 状态合法流转(行为验证码)

| 对象 | 状态 | 合法转换 |
|---|---|---|
| 验证码实例 | `已生成` → `已校验(Redis 凭证)` / `已消费(matching销毁)` | 凭证 120s TTL |
| 凭证 | `已存在(TTL 内)` / `已过期` | Redis 自动过期 |
| 验证码类型 | `SLIDER` / `ROTATE` / `WORD_IMAGE_CLICK` | 用户切换;`CONCAT` 自动回退 `SLIDER` |

---

## 3. 框架机制

### 3.1 启动与包扫描(沿用 master)

```java
@SpringBootApplication(
    scanBasePackages = {"com.basic", "com.core"},
    exclude = { DataSourceAutoConfiguration.class }
)
@MapperScan({"com.core.**.mapper"})
public class BasicApplication { ... }
```

### 3.2 tianai-captcha 装配

```java
// TianaiCaptchaConfig (basic-framework/src/main/java/com/basic/framework/config/)
@Configuration
@Order(0)
public class TianaiCaptchaConfig {

    private static final String TYPE_SLIDER = "SLIDER";
    private static final String TYPE_ROTATE = "ROTATE";
    private static final String TYPE_WORD_CLICK = "WORD_IMAGE_CLICK";

    private static final String DEFAULT_TEMPLATE_PATH = "classpath:META-INF/cut-image/template";
    private static final String DEFAULT_BACKGROUND_PATH = "classpath:META-INF/cut-image/resource";

    @Bean
    @Primary
    public ResourceStore tianaiResourceStore(ApplicationContext applicationContext) throws Exception {
        LocalMemoryResourceStore store = new LocalMemoryResourceStore();
        try {
            // 1. 加载默认模板(SLIDER/ROTATE)
            new DefaultBuiltInResources(DEFAULT_TEMPLATE_PATH).addDefaultTemplate(store);
            log.info("[tianai-captcha] default templates (SLIDER/ROTATE) loaded");

            // 2. 加载背景图(社区版仅 1.jpg 一张,本仓库 resource/1.jpg + pic1.jpg 共用)
            for (String name : {"1.jpg", "pic1.jpg"}) {
                Resource resource = new Resource();
                resource.setName(name);
                resource.setType(...);
                resource.setResource(...);
                store.addResource(TYPE_SLIDER, resource);
                store.addResource(TYPE_ROTATE, resource);
                store.addResource(TYPE_WORD_CLICK, resource);
            }
            log.info("[tianai-captcha] background resources loaded");

            // 3. 注入 WORD_IMAGE_CLICK 中文字体
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            for (String fontPath : FONT_PATHS) {
                Font font = Font.createFont(Font.TRUETYPE_FONT, applicationContext.getResource(fontPath).getInputStream());
                ge.registerFont(font);
            }
            log.info("[tianai-captcha] WORD_IMAGE_CLICK fonts loaded");
        } catch (Exception e) {
            log.error("[tianai-captcha] 资源加载失败", e);
        }
        return store;
    }
}
```

- `@Order(0)` 确保比 starter 默认 `ResourceStore` 优先装配
- `@Primary` 覆盖 starter 的默认 Bean
- 资源加载失败**不阻断启动**(try/catch);只是运行时无法生成对应类型

### 3.3 容差校验器(本分支关键定制)

```java
// TypeAwareImageCaptchaValidator (basic-framework/src/main/java/com/basic/framework/config/)
public class TypeAwareImageCaptchaValidator extends SimpleImageCaptchaValidator {

    private static final float ROTATE_TOLERANT = 0.10f;  // ±36°

    public TypeAwareImageCaptchaValidator() { super(); }
    public TypeAwareImageCaptchaValidator(float defaultTolerant) { super(defaultTolerant); }

    @Override
    public boolean doValidSliderCaptcha(ImageCaptchaTrack imageCaptchaTrack, AnyMap imageCaptchaValidData,
                                        Float tolerant, String type) {
        float effective = tolerant == null ? defaultTolerant : tolerant;
        if (CaptchaTypeConstant.ROTATE.equals(type) && effective < ROTATE_TOLERANT) {
            effective = ROTATE_TOLERANT;
        }
        return super.doValidSliderCaptcha(imageCaptchaTrack, imageCaptchaValidData, effective, type);
    }
}
```

- 继承 `SimpleImageCaptchaValidator`(tianai-captcha 默认校验器)
- 仅在 `type=ROTATE` 且入参容差 < 0.10 时放宽到 0.10;其他类型保持默认 0.02
- Spring 自动替换默认 Validator(无显式 `@Bean` 配置,tianai-captcha starter 通过 SPI 注入)

### 3.4 SecurityConfig 放行

```java
// SecurityConfig.configure(HttpSecurity)
httpSecurity
    .csrf().disable()
    .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
    .authorizeRequests()
    .antMatchers("/login", "/register", "/captchaImage", "/captcha/**", "/monitor/shutdown").anonymous()  // 🆕 /captcha/**
    .antMatchers(HttpMethod.GET, "/", "/*.html", "/**/*.html", "/**/*.css", "/**/*.js", "/profile/**").permitAll()
    .antMatchers("/swagger-ui.html", "/swagger-resources/**", "/webjars/**", "/*/api-docs").permitAll()
    .anyRequest().authenticated()
    .and()
    .headers().frameOptions().disable();
```

- **关键改动**:`/captcha/**` 加入白名单(原只放行 `/captchaImage` 单路径)
- `/captcha/check` 是 `/captcha/**` 下的新接口,因此自动放行

### 3.5 事务边界(沿用 master)

- **写操作事务**:Service `@Transactional(rollbackFor = Exception.class)`
- **登录主流程**:`SysLoginService.login` 上无 `@Transactional`;内部 matching / 密码校验 / Token 创建 是顺序操作
- **异步日志**:`AsyncManager.me().execute(...)` 隔离

### 3.6 Redis 凭证机制(本分支新增)

```java
// 写入(CaptchaCheckController.check)
private static final String VERIFIED_KEY_PREFIX = "captcha:verified:";
private static final int VERIFIED_EXPIRE_SECONDS = 120;

redisCache.setCacheObject(VERIFIED_KEY_PREFIX + id, "1", Duration.ofSeconds(VERIFIED_EXPIRE_SECONDS));

// 读取(SysLoginService.validateTianaiCaptcha)
String verifiedKey = "captcha:verified:" + captchaId;
try {
    String verified = redisCache.getCacheObject(verifiedKey);
    if ("1".equals(verified)) {
        // 命中:跳过 matching
        return;
    }
} catch (Exception e) {
    log.warn("Redis 异常,降级到 matching", e);
    // 继续走 matching 兜底
}
// 未命中:走 matching
```

- **降级策略**:Redis 异常 → catch → 走 matching 兜底(不阻断登录)
- **凭证唯一值**:固定 `"1"`;不存细节(因为 matching 已经把轨迹消费掉了)
- **TTL 120s**:适合调试/独立校验场景;过短/过长可改 `VERIFIED_EXPIRE_SECONDS`

### 3.7 kaptcha 隔离(本分支关键清理)

```xml
<!-- pom.xml (basic-framework/pom.xml) -->
<!-- ❌ 严禁新增引用:本分支已用 tianai-captcha 取代 -->
<dependency>
    <groupId>com.github.penggle</groupId>
    <artifactId>kaptcha</artifactId>
    <version>${kaptcha.version}</version>  <!-- 死引用,无 module 实际用 -->
</dependency>
```

- **保留理由**:快速回滚
- **隔离手段**:
  - `grep -r "kaptcha" basic-*/src` 应 0 命中(源码级)
  - `grep -r "kaptcha" basic-*/src/main` 应 0 命中(资源级)
  - 启动日志无 `kaptcha` 关键字
- **删除后果**:`mvn dependency:tree` 输出无 `kaptcha` 节点,但**回滚时需手工恢复 pom.xml**

### 3.8 其它框架机制(沿用 master)

- AOP 切面 / 限流 / 重复提交 / XSS / MyBatis-Plus 字段填充 — 全部沿用
- JwtAuthenticationTokenFilter / TokenService / AsyncManager — 全部沿用

---

## 4. 核心引擎详细设计

### 4.1 验证码生成引擎(CaptchaController)

```java
@RestController
public class CaptchaController {
    @Autowired private ImageCaptchaApplication imageCaptchaApplication;
    @Autowired private ISysConfigService configService;

    @GetMapping("/captchaImage")
    public AjaxResult getCode(@RequestParam(required = false) String type) throws IOException {
        // 1. 读总开关
        boolean captchaOnOff = configService.selectCaptchaOnOff();

        // 2. 类型兜底
        if (StringUtils.isEmpty(type)) {
            type = SpringUtils.getRequiredProperty("captcha.default-type");
        }
        // CONCAT 已下线,回退 SLIDER
        if (CaptchaTypeConstant.CONCAT.equals(type)) {
            type = CaptchaTypeConstant.SLIDER;
        }

        // 3. 生成实例
        CaptchaResponse<ImageCaptchaVO> response = imageCaptchaApplication.generate(type);

        // 4. 扁平化(兼容性 + 安全:绝不下发 expand)
        Map<String, Object> data = new HashMap<>();
        data.put("id", response.getId());
        // data.put("backgroundImage", response.getData().getBackgroundImage());
        // data.put("templateImage", response.getData().getTemplateImage());
        // data.put("sliderImage", response.getData().getSliderImage());
        // ... 业务侧按需

        // 5. 兼容旧字段
        Map<String, Object> result = new HashMap<>();
        result.put("captchaOnOff", captchaOnOff);
        result.put("uuid", response.getId());
        result.put("img", null);            // 行为验证码无单张图
        result.put("type", type);
        result.put("data", data);
        return AjaxResult.success(result);
    }
}
```

- **关键点**:`uuid` = `response.getId()`(行为验证码 id,不是 kaptcha 时代的 uuid)
- **CONCAT 兜底**:防止前端误传
- **expand 不下发**:避免泄露后端细节

### 4.2 旁路校验引擎(CaptchaCheckController)

```java
@RestController
public class CaptchaCheckController {
    @Autowired private ImageCaptchaApplication imageCaptchaApplication;
    @Autowired private RedisCache redisCache;

    public static final String VERIFIED_KEY_PREFIX = "captcha:verified:";
    public static final int VERIFIED_EXPIRE_SECONDS = 120;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/captcha/check")
    public AjaxResult check(@RequestBody Map<String, Object> body) {
        // 1. 校验入参
        if (body == null) return AjaxResult.error("请求体为空");
        Object idObj = body.get("id");
        Object dataObj = body.get("data");
        if (idObj == null || dataObj == null) return AjaxResult.error("id 和 data 不能为空");

        // 2. 反序列化轨迹
        ImageCaptchaTrack track;
        try {
            if (dataObj instanceof Map) {
                track = objectMapper.convertValue(dataObj, ImageCaptchaTrack.class);
            } else if (dataObj instanceof String) {
                track = objectMapper.readValue((String) dataObj, ImageCaptchaTrack.class);
            } else {
                return AjaxResult.error("data 格式错误, 需要 Map 或 JSON 字符串");
            }
        } catch (Exception e) {
            return AjaxResult.error("轨迹数据格式错误: " + e.getMessage());
        }

        // 3. 校验必要字段
        if (track.getTrackList() == null || track.getTrackList().isEmpty()) {
            return AjaxResult.error("trackList 不能为空");
        }
        if (track.getBgImageWidth() == null || track.getBgImageHeight() == null) {
            return AjaxResult.error("bgImageWidth / bgImageHeight 不能为空");
        }
        if (track.getStartTime() == null || track.getStopTime() == null) {
            return AjaxResult.error("startTime / stopTime 不能为空");
        }

        // 4. matching(消费验证码缓存)
        ApiResponse<?> result;
        try {
            result = imageCaptchaApplication.matching(idObj.toString(), track);
        } catch (Exception e) {
            return AjaxResult.error("校验失败: " + e.getMessage());
        }

        // 5. 写 Redis 凭证(120s)
        if (result != null && result.isSuccess()) {
            redisCache.setCacheObject(
                VERIFIED_KEY_PREFIX + idObj.toString(), "1",
                Duration.ofSeconds(VERIFIED_EXPIRE_SECONDS));
            return AjaxResult.success("校验成功");
        } else {
            String msg = result != null && result.getMsg() != null ? result.getMsg() : "校验失败";
            return AjaxResult.error(msg);
        }
    }
}
```

- **入参容错**:`data` 支持 Map / JSON 字符串 两种形态(前端两种序列化方式都能接)
- **matching() 会消费** tianai 内部缓存:同一 `id` 全局只能匹配 1 次
- **Redis 凭证 120s**:供后续 `/login` 跳过 matching

### 4.3 登录主流程(SysLoginService)

```java
@Service
public class SysLoginService {
    @Autowired private UserDetailsService userDetailsService;
    @Autowired private TokenService tokenService;
    @Autowired private ISysConfigService configService;
    @Autowired private RedisCache redisCache;
    @Autowired private ImageCaptchaApplication imageCaptchaApplication;
    @Autowired private AsyncManager asyncManager;

    /**
     * 登录验证(行为验证码 tianai-captcha 版本)
     * @param code        行为验证码 id(由 /captchaImage 返回的 uuid;兼容老 kaptcha 接口,作为占位符)
     * @param captchaData 行为验证码轨迹 JSON 字符串(ImageCaptchaTrack);4 种类型都用该字段
     */
    public String login(String username, String password, String code, String uuid, String captchaData) {
        // 1. 总开关
        boolean captchaOnOff = configService.selectCaptchaOnOff();
        if (captchaOnOff) {
            validateTianaiCaptcha(username, uuid, captchaData);
        }

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
        return token;
    }

    /**
     * 校验行为验证码(tianai-captcha)
     * @param captchaId   验证码 id(由 /captchaImage 返回的 uuid)
     * @param captchaData 行为验证码轨迹数据 JSON 字符串(ImageCaptchaTrack 序列化结果)
     */
    public void validateTianaiCaptcha(String username, String captchaId, String captchaData) {
        // 1. 必要字段
        if (StringUtils.isEmpty(captchaId)) {
            asyncFactory.recordLogininfor(username, FAIL, "验证码 id 缺失");
            throw new CaptchaException("user.jcaptcha.error");
        }
        if (StringUtils.isEmpty(captchaData)) {
            asyncFactory.recordLogininfor(username, FAIL, "验证码轨迹缺失");
            throw new CaptchaException("user.jcaptcha.error");
        }

        // 2. 优先读 Redis 凭证(/captcha/check 写过)
        String verifiedKey = "captcha:verified:" + captchaId;
        try {
            String verified = redisCache.getCacheObject(verifiedKey);
            if ("1".equals(verified)) {
                return;  // 命中:跳过 matching
            }
        } catch (Exception e) {
            log.warn("Redis 凭证查询异常,降级到 matching", e);
        }

        // 3. 未命中 / 异常 → matching 兜底
        ImageCaptchaTrack track = jsonToTrack(captchaData);
        ApiResponse<?> result = imageCaptchaApplication.matching(captchaId, track);
        if (result == null || !result.isSuccess()) {
            String msg = (result != null && result.getMsg() != null) ? result.getMsg() : MessageUtils.message("user.jcaptcha.error");
            asyncFactory.recordLogininfor(username, FAIL, "验证码错误");
            throw new CaptchaException(msg);
        }
    }

    private ImageCaptchaTrack jsonToTrack(String json) {
        try {
            return new ObjectMapper().readValue(json, ImageCaptchaTrack.class);
        } catch (Exception e) {
            throw new ServiceException("轨迹数据格式错误: " + e.getMessage());
        }
    }
}
```

- **关键改进**(相对 master):
  - `login()` 方法签名增加 `captchaData` 参数
  - 新增 `validateTianaiCaptcha` 私有方法
  - **优先读 Redis 凭证**(旁路)
  - **matching 兜底**(主流程)
  - **失败落 `sys_logininfor`**(沿用 master)
- **Redis 降级**:异常 → 走 matching 兜底,不阻断

### 4.4 其它核心引擎(沿用 master)

- TokenService / UserDetailsServiceImpl / AsyncManager / LogAspect / DataScopeAspect / RepeatSubmitInterceptor / RateLimiterAspect / XssFilter — 全部沿用
- 详见 `docs/base-postgresql-vue3-详细设计.md` §4

---

## 5. 校验引擎

### 5.1 L1 字段级校验

- 入口:`@Validated` + `javax.validation.constraints.*`(沿用 master)
- `LoginBody.captchaData`:Jackson 反序列化校验(必填 / JSON 合法)
- 失败处理:`GlobalExceptionHandler` 包装

### 5.2 L2 跨业务校验(本分支独有)

| 校验项 | 位置 | 失败动作 |
|---|---|---|
| 验证码 id 必须存在 | `imageCaptchaApplication.generate/matching` | 抛 `CaptchaException("user.jcaptcha.expire")` |
| 轨迹数据 `trackList` 非空 | `CaptchaCheckController.check` / `SysLoginService.validateTianaiCaptcha` | 抛 `AjaxResult.error` / `CaptchaException` |
| 轨迹 `bgImageWidth/Height` > 0 | 同上 | 同上 |
| 轨迹 `startTime/stopTime` 合法 | 同上 | 同上 |
| 凭证 `captcha:verified:<id>` TTL 120s 内 | `validateTianaiCaptcha` 读 | 命中即跳过 matching;未命中走 matching |
| matching 失败 | `imageCaptchaApplication.matching` 返回非 `success` | 抛 `CaptchaException` + 落 `sys_logininfor` |
| `sys.account.captchaEnabled=false` | `SysLoginService.login` | 跳过 `validateTianaiCaptcha` |
| `CONCAT` 类型自动回退 | `CaptchaController.getCode` | 类型替换为 `SLIDER` |

---

## 6. 业务模块实现

### 6.1 本分支独有改动

| 文件 | 改动 |
|---|---|
| `basic-framework/.../TianaiCaptchaConfig.java` | **新增**:`@Order(0)` 资源加载 |
| `basic-framework/.../TypeAwareImageCaptchaValidator.java` | **新增**:ROTATE 容差 0.10 |
| `basic-admin-api/.../CaptchaController.java` | **重写**:返回结构适配 tianai-captcha,扁平化 `viewData` |
| `basic-admin-api/.../CaptchaCheckController.java` | **新增**:`POST /captcha/check` 旁路 |
| `basic-admin-api/.../SysLoginController.java` | 沿用(只调 `SysLoginService.login`) |
| `basic-common/.../LoginBody.java` | **新增字段**:`captchaData` |
| `basic-framework/.../SecurityConfig.java` | **新增放行**:`/captcha/**` |
| `basic-framework/.../SysLoginService.java` | **修改**:`login` 签名加 `captchaData`;新增 `validateTianaiCaptcha` |
| `basic-framework/.../CaptchaConfig.java` | **删除**(kaptcha) |
| `basic-framework/.../KaptchaTextCreator.java` | **删除**(kaptcha) |
| `basic-framework/src/main/resources/META-INF/cut-image/` | **新增资源**:1.jpg + pic1.jpg(背景图) |
| `basic-framework/pom.xml` | **修改**:`kaptcha` 依赖改注释;新增 `tianai-captcha-springboot-starter:1.5.0` |
| `web-ui/src/components/TianaiCaptcha/index.vue` | **新增**:自实现 3 类型组件 |
| `web-mp/src/components/tianai-captcha/tianai-captcha.vue` | **新增**:自实现 3 类型组件 |
| `web-ui/src/api/login.js` + `web-mp/src/api/login.js` | **修改**:POST /login 携带 `captchaData` |
| `web-ui/src/views/login/index.vue` + `web-mp/src/pages/login/index.vue` | **修改**:接入 TianaiCaptcha 组件,支持类型切换 |

### 6.2 删除的类(本分支)

```java
// ❌ CaptchaConfig.java (master kaptcha 配置) — 本分支删除
@Configuration
public class CaptchaConfig {
    @Bean public Producer kaptchaProducer() { ... }  // 字符验证码
    @Bean public ServletRegistrationBean kaptchaServlet() { ... }
}

// ❌ KaptchaTextCreator.java (master kaptcha 文本生成器) — 本分支删除
public class KaptchaTextCreator extends DefaultTextCreator { ... }
```

**删除原因**:kaptcha 字符验证码被 tianai-captcha 行为验证码整体取代。

**回滚方法**(紧急):从 master 分支 `git checkout master -- basic-framework/src/main/java/com/basic/framework/config/CaptchaConfig.java basic-framework/src/main/java/com/basic/framework/config/KaptchaTextCreator.java`,并 `git revert` 本分支的相关 commit。

### 6.3 前端组件实现要点(web-ui)

```vue
<!-- web-ui/src/components/TianaiCaptcha/index.vue 关键逻辑 -->
<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const captchaType = ref('SLIDER')  // SLIDER / ROTATE / WORD_IMAGE_CLICK
const captchaData = ref(null)
const trackList = ref([])  // 轨迹点
const props = defineProps({ modelValue: String })

const fetchCaptcha = async () => {
  const res = await axios.get(`/dev-api/captchaImage?type=${captchaType.value}`)
  captchaData.value = res.data
  // 反机器人轨迹构造
  trackList.value = [
    { x: 0, y: 0, t: 0 },  // 首条补原点
    ...generateJitterTrack(),  // y 微抖动
    { x: targetX, y: targetY, t: totalTime, slowdown: true }  // 末段减速
  ]
}

const onVerify = () => {
  emit('update:modelValue', JSON.stringify({
    trackList: trackList.value,
    bgImageWidth: captchaData.value.data.bgImageWidth,
    bgImageHeight: captchaData.value.data.bgImageHeight,
    startTime: Date.now() - totalTime,
    stopTime: Date.now()
  }))
}
</script>
```

**反机器人策略**(注释摘自源码):
- **首条补原点**:`{x: 0, y: 0, t: 0}`(tianai 期望起点)
- **y 微抖动**:模拟人手在 y 轴的不稳定
- **末段减速**:模拟人手在接近目标时的减速
- **百分比坐标**:避免首帧缩放比未就绪导致的尺寸跳动

### 6.4 移动端组件实现要点(web-mp)

```vue
<!-- web-mp/src/components/tianai-captcha/tianai-captcha.vue 关键逻辑(同 web-ui 核心) -->
<script setup>
// 适配 uni-app:用 uni.request 替代 axios
// 适配 uview-plus:用 up- 组件
// 核心轨迹构造 + 百分比定位 与 web-ui 一致
</script>
```

### 6.5 其它业务模块(沿用 master)

> 用户/角色/菜单/部门/字典/参数/通知/定时任务/代码生成/锁屏 全部沿用,详见 `docs/base-postgresql-vue3-详细设计.md` §6。

---

## 7. 审批旁路(不适用)

> 本分支不引入工作流引擎。`springboot2-extend-flowable` 分支提供,详见该分支文档。

---

## 8. 审计/报表/Web/安全

### 8.1 审计

- **写操作**:`@Log` 注解 + `LogAspect` 切面 → `sys_oper_log`(沿用 master)
- **登录事件**:`AsyncManager` 异步 → `sys_logininfor`(沿用 master;**`msg` 不区分 tianai vs kaptcha**)
- **验证码失败**:仅落 `sys_logininfor`,不入 `sys_oper_log`
- **失败原因枚举**:`"验证码 id 缺失"` / `"验证码轨迹缺失"` / `"验证码错误"` / `"密码错误"` / `"账号停用"`

### 8.2 报表

- 登录日志:`/monitor/logininfor/list`(沿用 master)
- 操作日志:`/monitor/operlog/list`(沿用 master)
- 验证码相关无独立报表(从 `sys_logininfor` 过滤)

### 8.3 Web 端(`web-ui`)

- 入口:`web-ui/src/main.js` + `permission.js`(沿用)
- 登录页改造:`web-ui/src/views/login/index.vue` 接入 `<TianaiCaptcha />` 组件
- 类型切换:登录页 UI 提供 SLIDER / ROTATE / WORD_IMAGE_CLICK 三选一
- 请求:`/api/login`(对应 `/dev-api/login`,由 Vite 代理)

### 8.4 移动端(`web-mp`)

- 登录页改造:`web-mp/src/pages/login/index.vue` 接入 `<tianai-captcha />` 组件
- 类型切换:同上
- 网络:同 PC(`/login` 路径)

### 8.5 安全

| 项 | 实现 |
|---|---|
| 密码 | BCrypt |
| 登录传输 | 前端 `jsencrypt` RSA 加密 password |
| Token | HS512 JWT,30 分钟;Redis 缓存 |
| 行为验证码 | tianai-captcha 1.5.0;SLIDER/ROTATE/WORD_IMAGE_CLICK 3 类型 |
| 容差策略 | ROTATE 0.10(±36°),其余 0.02(±12px 或 ±7.2°) |
| 凭证 | `captcha:verified:<id>`,120s TTL;Redis 不可用降级 |
| `/captcha/check` | 旁路,matching + 写凭证;**仅供调试/独立校验** |
| `/captcha/**` | `SecurityConfig` `anonymous()` 放行 |
| 总开关 | `sys_config.sys.account.captchaEnabled`,`false` 跳过验证码 |
| XSS / 重复提交 / 限流 / 限流 | 沿用 master |

---

## 9. 错误码(本分支独有)

| 类别 | code | 含义 | 触发场景 |
|---|---|---|---|
| 业务 | `3001` | 验证码 id 缺失 | `LoginBody.uuid` 为空 |
| 业务 | `3002` | 验证码轨迹缺失 | `LoginBody.captchaData` 为空 |
| 业务 | `3003` | 验证码错误 | `imageCaptchaApplication.matching` 失败 |
| 业务 | `3004` | 验证码过期 | `matching` 报 `expired`(已超过 tianai 内部 TTL) |
| 业务 | `3005` | 轨迹数据格式错误 | Jackson 反序列化失败 |
| 业务 | `3006` | Redis 凭证查询异常 | 降级到 matching(不阻断) |
| 业务 | `3007` | `CONCAT` 类型已下线 | `CaptchaController` 自动回退 SLIDER(不报错) |

> 沿用 master 的 12 类错误码(详见 `docs/base-postgresql-vue3-详细设计.md` §9)。

---

## 10. 并发与线程模型(沿用 master + 本分支新增)

| 资源 | 位置 | 配置 |
|---|---|---|
| Tomcat 线程池 | `application.yml` | `max-threads=800` / `min-spare=10` |
| 异步任务线程池 | `ThreadPoolConfig` | `corePoolSize=10` / `maxPoolSize=50` |
| Druid 连接池 | `application-druid.yml` | `min-idle=5` / `max-active=20` |
| Quartz 线程池 | `ScheduleConfig` | 25 |
| Redis 缓存 | `RedisCache` | TTL 30min(字典/Token);**TTL 120s**(凭证) |
| tianai-captcha | `imageCaptchaApplication` | 默认线程池(starter 装配) |

**锁使用**:沿用 master(qrtz_locks)。

---

## 11. 测试关注点(本分支独有)

| # | 场景 | 重点 |
|---|---|---|
| TC-01 | PC 滑动验证码登录成功 | 轨迹合法 + 凭证未过期 |
| TC-02 | PC 旋转验证码登录成功 | ROTATE 容差 0.10 通过 |
| TC-03 | PC 文字点选登录成功 | 文字点选轨迹合法 |
| TC-04 | 移动端 3 种验证码登录成功 | uni-app 适配 |
| TC-05 | ROTATE 容差边界:±36° 通过 | 0.10 阈值 |
| TC-06 | ROTATE 容差边界:±7.2° 失败 | 0.02 阈值(默认) |
| TC-07 | 验证码类型切换(登录页 UI) | 重新生成实例 |
| TC-08 | `CONCAT` 类型自动回退 | 不报错,SLIDER |
| TC-09 | `/captcha/check` 旁路:先调成功 → 再 `/login` 跳过 matching | 凭证命中 |
| TC-10 | `/captcha/check` 失败:不写 Redis 凭证 | `/login` 走 matching 失败 |
| TC-11 | Redis 不可用:`/login` 走 matching 兜底 | 降级日志可见 |
| TC-12 | `sys.account.captchaEnabled=false` 跳过验证码 | 总开关 |
| TC-13 | 验证码失败 3 次 → 重试机制 | 重试 3 次后锁定账号(沿用 master) |
| TC-14 | 启动日志含 3 条 `[tianai-captcha] ... loaded` | 资源加载成功 |
| TC-15 | `grep -r "kaptcha" basic-*/src` 0 命中 | 死引用隔离 |
| TC-16 | kaptcha 字符验证码 0 出现 | 业务无 kaptcha 接口 |
| TC-17 | 失败落 `sys_logininfor` | 沿用 master |
| TC-18 | 凭证 TTL 过期 → 120s 后需重新调 `/captcha/check` | Redis 自动过期 |
| TC-19 | 类型切换时前次未消费实例是否复用 | 复用,无副作用 |
| TC-20 | 移动端 H5 旋转验证码适配 | 跟 PC 行为一致 |

---

## 12. 需求变更记录

| 需求 | 落点 |
|---|---|
| 行为验证码替换 kaptcha | §3.2 + §4.3 |
| 3 种类型可切换 | §3.5 + §4.1 |
| ROTATE 容差 0.10 | §3.3 + §4.3 |
| `LoginBody.captchaData` 字段 | §2.1 + §4.3 |
| `/captcha/check` 旁路 | §3.3 + §4.2 |
| Redis 凭证 120s | §3.6 + §4.2 |
| Redis 降级 matching | §3.4 + §4.3 |
| `SecurityConfig` 放行 `/captcha/**` | §3.4 |
| 前端自实现 3 类型组件 | §6.3 + §6.4 |
| kaptcha 死引用隔离 | §3.7 |
| `CONCAT` 自动回退 SLIDER | §4.1 |
| `sys.account.captchaEnabled` 总开关 | §4.3 |

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:覆盖 3 类型 / 2 接口 / 2 前端组件 / ROTATE 容差 0.10 / 凭证 120s / Redis 降级 / 20 TC |
