# 基础平台(base-postgresql-vue3) 详细设计(LLD) — `springboot3-tianai-captcha` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot3-tianai-captcha`** 分支
> 前置依赖:《架构方案》v0.1、《需求文档》v0.1、《概要设计》v0.1
> 本分支 = **`springboot3` 基线(SB 3.5.16 / JDK 17 / Jakarta / Security 6 / springdoc)+ tianai-captcha 1.5.2 行为验证码**。
> **业务实现与 `springboot3` 完全一致**(不重复);本分支新增 **§1.2 改动文件台账、§3.10 验证码机制(6 小节)、§4.7 验证码引擎(3 个类完整实现)、§4.8 容差校验器、§6.6 前端组件、§9.2 验证码错误码、§11.2 TC-33~TC-48**。
> 改动以 **🔶** 标出;`springboot3` 继承项以 **⬆️** 标出。

---

## 1. 总体包结构

### 1.1 包结构(⬆️ 与 `springboot3` 一致,🔶 验证码相关文件变化)

```
com.basic
├── BasicApplication                        # 启动类(⬆️)
├── BasicServletInitializer                # war 适配(⬆️ jakarta)
├── web
│   ├── config/SwaggerConfig               # ⬆️ springdoc
│   └── controller
│       ├── common
│       │   ├── CaptchaController          🔶 重写(kaptcha → tianai)
│       │   ├── CaptchaCheckController     🔶 新增(POST /captcha/check)
│       │   └── CommonController           ⬆️
│       ├── monitor/{Cache,Server,SysLogininfor,SysOperlog,SysUserOnline}Controller   ⬆️
│       ├── system/{SysConfig,SysDept,SysDictData,SysDictType,SysIndex,SysLogin,SysMenu,SysNotice,SysPost,SysProfile,SysRegister,SysRole,SysUser}Controller  ⬆️
│       └── tool/{Swagger,Test}Controller   ⬆️
├── framework
│   ├── aspectj/{DataScopeAspect, DataSourceAspect, LogAspect, RateLimiterAspect}   ⬆️
│   ├── config
│   │   ├── ApplicationConfig / DruidConfig / FilterConfig / JacksonConfig          ⬆️
│   │   ├── FastJson2JsonRedisSerializer / MyBatisConfig / MybatisPlusConfig        ⬆️
│   │   ├── RedisConfig / ResourcesConfig / SecurityConfig / ServerConfig           ⬆️(SecurityConfig 🔶 放行 /captcha/**)
│   │   ├── ThreadPoolConfig / UserAgentConfig(yauaa)                              ⬆️
│   │   ├── TianaiCaptchaConfig                    🔶 新增
│   │   ├── TypeAwareImageCaptchaValidator         🔶 新增
│   │   ├── CaptchaConfig                          🔶 ❌ 已删除(kaptcha)
│   │   └── KaptchaTextCreator                     🔶 ❌ 已删除(kaptcha)
│   ├── datasource/{DynamicDataSource, DynamicDataSourceContextHolder}              ⬆️
│   ├── interceptor/{RepeatSubmitInterceptor, impl/SameUrlDataInterceptor}          ⬆️
│   ├── jackson/BigNumberSerializer                                                 ⬆️
│   ├── manager/{AsyncManager, ShutdownManager, factory/AsyncFactory}               ⬆️
│   ├── security
│   │   ├── filter/JwtAuthenticationTokenFilter                                     ⬆️
│   │   └── handle/{AuthenticationEntryPointImpl, LogoutSuccessHandlerImpl, MyMetaObjectHandler}  ⬆️
│   └── web
│       ├── domain/{Server, server/*}                                               ⬆️
│       ├── exception/GlobalExceptionHandler                                        ⬆️
│       └── service
│           ├── PermissionService / SysPermissionService / SysRegisterService       ⬆️
│           ├── SysLoginService            🔶 加 captchaData 参数 + validateTianaiCaptcha
│           ├── TokenService                                                                ⬆️
│           └── UserDetailsServiceImpl                                                      ⬆️
├── common
│   ├── annotation/{Anonymous, DataScope, DataSource, Excel, Excels, Log, RateLimiter, RepeatSubmit}  ⬆️
│   ├── config/BasicConfig                                                          ⬆️
│   ├── constant/{Constants, GenConstants, HttpStatus, ScheduleConstants, UserConstants}  ⬆️
│   ├── core
│   │   ├── controller/BaseController                                               ⬆️
│   │   ├── domain/{AjaxResult, BaseEntity, R, TableDataInfo, TreeEntity, TreeSelect, YjyBaseEntity, entity/*}  ⬆️
│   │   │   └── model
│   │   │       ├── LoginBody              🔶 新增 captchaData 字段
│   │   │       ├── LoginUser                                                       ⬆️
│   │   │       └── RegisterBody                                                    ⬆️
│   │   ├── page/{PageDomain, TableSupport}                                         ⬆️
│   │   ├── redis/RedisCache               🔶 新增凭证读写用法
│   │   └── text/{CharsetKit, Convert, StrFormatter}                                ⬆️
│   ├── enums/*                                                                     ⬆️
│   ├── exception
│   │   ├── base/BaseException                                                      ⬆️
│   │   ├── file/*                                                                  ⬆️
│   │   ├── job/TaskException                                                       ⬆️
│   │   ├── user/{CaptchaException, CaptchaExpireException, UserException, ...}     ⬆️ 复用
│   │   └── {DemoModeException, GlobalException, ServiceException, UtilException}   ⬆️
│   ├── filter/{RepeatableFilter, RepeatedlyRequestWrapper, XssFilter, XssHttpServletRequestWrapper}  ⬆️
│   ├── utils/*                                                                     ⬆️
│   └── xss/{Xss, XssValidator}                                                     ⬆️
├── quartz (extend-quartz)                                                          ⬆️
├── generator (extend-generator)                                                     ⬆️(⚠️ 模板未适配 SB3)
└── system (basic-admin-core)                                                        ⬆️

com.core (business-core)                                                             ⬆️

resources/
├── META-INF/cut-image/
│   ├── resource/    🔶 1.jpg  pic1.jpg  pic2.jpg  pic3.jpg  pic4.jpg  pic5.jpg  推荐格式.txt
│   └── template/    🔶 slider_1/{active,fixed}.png  slider_2/{active,fixed}.png  rotate_1/{active,fixed}.png
└── (tianai jar 内置) META-INF/fonts/SIMSUN.TTC   ← 字体来源
```

### 1.2 🔶 本分支改动文件台账

#### 1.2.1 后端新增(4 个)

| 文件 | 说明 |
|---|---|
| `basic-framework/.../config/TianaiCaptchaConfig.java` | 138 行;`@Order(0)`;`@Bean @Primary ResourceStore` + `@Bean @Primary ImageCaptchaValidator` |
| `basic-framework/.../config/TypeAwareImageCaptchaValidator.java` | 43 行;继承 `SimpleImageCaptchaValidator`;`ROTATE_TOLERANT = 0.10f` |
| `basic-admin-api/.../controller/common/CaptchaCheckController.java` | `POST /captcha/check`;`VERIFIED_KEY_PREFIX` / `VERIFIED_EXPIRE_SECONDS=120` |
| (资源)`basic-framework/src/main/resources/META-INF/cut-image/**` | 6 张背景图 + 3 套模板 + `推荐格式.txt` |

#### 1.2.2 后端修改(4 个)

| 文件 | 改动 |
|---|---|
| `basic-admin-api/.../controller/common/CaptchaController.java` | 170 行;**完全重写**:`/captchaImage?type=`、`CONCAT` 回退、扁平化、`img=null` |
| `basic-admin-api/.../controller/system/SysLoginController.java` | 登录调用透传 `captchaData` |
| `basic-framework/.../web/service/SysLoginService.java` | 227 行;`login(...)` 加 `captchaData`;新增 `validateTianaiCaptcha` + `jsonToTrack` |
| `basic-framework/.../config/SecurityConfig.java` | `requestMatchers("/captchaImage", "/captcha/**").permitAll()` |
| `basic-common/.../core/domain/model/LoginBody.java` | 新增 `captchaData` 字段 + getter/setter |
| `pom.xml`(父) | 新增 `<tianai.version>1.5.2</tianai.version>` + `dependencyManagement` 条目 |
| `basic-framework/pom.xml` | 引入 `tianai-captcha-springboot-starter` |

#### 1.2.3 后端删除(2 个)

| 文件 | 删除原因 |
|---|---|
| `basic-framework/.../config/CaptchaConfig.java` | kaptcha `Producer` / `ServletRegistrationBean` |
| `basic-framework/.../config/KaptchaTextCreator.java` | kaptcha 文本生成器 |

#### 1.2.4 前端(2 端)

| 文件 | 端 | 改动 |
|---|---|---|
| `web-ui/src/components/TianaiCaptcha/index.vue` | PC | **新增**(28.1 KB) |
| `web-ui/src/views/login.vue` | PC | 接入组件;`loginForm` 加 `captchaData` |
| `web-ui/src/api/login.js` | PC | `login()` 携带 `captchaData` |
| `web-mp/src/components/tianai-captcha/tianai-captcha.vue` | 移动 | **新增**(21.9 KB) |
| `web-mp/src/pages/login/index.vue` | 移动 | 接入组件 |
| `web-mp/src/api/login.js` | 移动 | `login()` 携带 `captchaData` |

#### 1.2.5 零改动

| 范围 | 结论 |
|---|---|
| `file-sql/**` | **未改动**(34 表 + 5 函数) |
| 其它后端业务类 | **未改动** |
| 菜单数据 / 权限点 | **未改动** |

---

## 2. 领域模型与枚举

### 2.1 后端实体(⬆️ 与 `springboot3` 一致)

> 16 个核心实体(`SysUser` / `SysRole` / `SysMenu` / `SysDept` / `SysPost` / `SysDictType` / `SysDictData` / `SysNotice` / `SysNoticeRead` / `SysJob` / `SysJobLog` / `SysOperLog` / `SysLogininfor` / `SysFiles` / `SysI18n` / `Demo`)+ 主键策略 `ASSIGN_ID`(雪花)+ 状态字段,全部与 `springboot3` 一致,详见 `docs/springboot3-详细设计.md` §2.1。

> **🔶 本分支没有新增领域实体**:验证码实例由 tianai 内部缓存持有(非 DB),凭证在 Redis。

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

### 2.3 🔶 验证码相关类型(本分支新增/复用)

#### 2.3.1 `LoginBody`(🔶 新增 `captchaData`)

```java
public class LoginBody {
    /** 用户名 */
    private String username;

    /** 用户密码(RSA 密文) */
    private String password;

    /**
     * 验证码
     * <p>行为验证码模式下<b>不校验此值</b>,仅作占位(锁屏传 LOCK_BYPASS)</p>
     */
    private String code;

    /**
     * 唯一标识
     * <p>行为验证码模式下:存验证码 id(由 /captchaImage 返回的 uuid);兼容老 kaptcha 模式</p>
     */
    private String uuid;

    /**
     * 行为验证码滑动轨迹数据
     * <p>tianai-captcha 的 ImageCaptchaTrack JSON 字符串,3 种类型都用这个字段</p>
     */
    private String captchaData;      // 🔶 新增

    // getter / setter ...
}
```

#### 2.3.2 数据类型来源(tianai 库,不在本仓库定义)

| 类型 | 包 | 用途 |
|---|---|---|
| `ImageCaptchaApplication` | `cloud.tianai.captcha.application` | `generate(type)` / `matching(id, track)` |
| `CaptchaResponse<ImageCaptchaVO>` | `cloud.tianai.captcha.application.vo` | 生成结果 |
| `ImageCaptchaVO` | `cloud.tianai.captcha.application.vo` | 验证码视图(背景图/模板图/滑块图) |
| `ImageCaptchaTrack` | `cloud.tianai.captcha.validator.common.model.dto` | 轨迹数据 |
| `ApiResponse<?>` | `cloud.tianai.captcha.common.response` | 校验结果(`isSuccess()` / `getMsg()`) |
| `CaptchaTypeConstant` | `cloud.tianai.captcha.common.constant` | `SLIDER` / `ROTATE` / `WORD_IMAGE_CLICK` / `CONCAT` |
| `SimpleImageCaptchaValidator` | `cloud.tianai.captcha.validator.impl` | 默认校验器(被本分支继承) |
| `ImageCaptchaValidator` | `cloud.tianai.captcha.validator` | 校验器接口 |
| `ResourceStore` / `LocalMemoryResourceStore` / `DefaultBuiltInResources` / `Resource` | `cloud.tianai.captcha.resource.*` | 资源管理 |

### 2.4 🔶 验证码状态流转(详见概要设计 §2.5.3)

| 对象 | 状态 | 合法转换 |
|---|---|---|
| 验证码实例 | `已生成` → `已消费(matching)` / `已过期(tianai TTL)` | 消费后不可复用 |
| 已校验凭证 | `存在(TTL 120s)` → `已删除(/login 命中)` / `已过期` | 一次性 |
| 验证码类型 | `SLIDER` / `ROTATE` / `WORD_IMAGE_CLICK` | 用户切换;`CONCAT` 自动回退 `SLIDER` |

---

## 3. 框架机制

### 3.1 启动与包扫描(⬆️ 与 `springboot3` 一致)

```java
@SpringBootApplication(
    scanBasePackages = {"com.basic", "com.core"},
    exclude = { DataSourceAutoConfiguration.class }
)
@MapperScan({"com.core.**.mapper"})
public class BasicApplication { ... }
```

### 3.2 ~ 3.8 (⬆️ 全部继承 `springboot3`)

| 小节 | 内容 | 状态 |
|---|---|---|
| 3.2 动态 Schema 路由 | `DataSourceAspect` + `DynamicDataSourceContextHolder` | ⬆️ 一致 |
| 3.3 事务边界 | `@Transactional(rollbackFor)` / `AsyncManager` 隔离 | ⬆️ 一致 |
| 3.4 审计切面 | `LogAspect` `@Around("@annotation(log)")` | ⬆️ 一致 |
| 3.5 重复提交拦截 | `SameUrlDataInterceptor`(jakarta) | ⬆️ 一致 |
| 3.6 限流 | `RateLimiterAspect`(Guava / Redis) | ⬆️ 一致 |
| 3.7 XSS | `XssFilter` + `@Xss` | ⬆️ 一致 |
| 3.8 字段填充 | `MyMetaObjectHandler` | ⬆️ 一致 |
| 3.9 SB3 升级机制 | jakarta / Security 6 / springdoc / yauaa / starter / 配置 | ⬆️ 见 `docs/springboot3-详细设计.md` §3.9 |

### 3.10 🔶 验证码机制详细设计(本分支核心)

#### 3.10.1 tianai 装配与资源初始化

```java
package com.basic.framework.config;

import cloud.tianai.captcha.resource.DefaultBuiltInResources;
import cloud.tianai.captcha.resource.ResourceStore;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.impl.LocalMemoryResourceStore;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

/**
 * tianai-captcha 1.5.2 资源初始化
 *
 * 启动时主动加载:
 *   1. SLIDER / ROTATE 模板(社区版 jar 内置, 路径 classpath:META-INF/cut-image/template)
 *   2. 各类型的背景图资源(扫 resource 目录下所有 jpg/jpeg/png)
 *   3. WORD_IMAGE_CLICK 所需的中文字体 (1.5.2 改为通过 ResourceStore 注入, FontCache 会自动监听)
 *
 * 1.5.x 行为变更: MultiImageCaptchaGenerator 不再暴露 getFontWrappers/setFontWrappers,
 *   字体改成作为 ResourceStore 中 type="font" 的资源注入, FontCache 监听 onAddResource 事件.
 */
@Configuration
@Order(0)
public class TianaiCaptchaConfig {

    private static final Logger log = LoggerFactory.getLogger(TianaiCaptchaConfig.class);

    private static final String TYPE_SLIDER      = "SLIDER";
    private static final String TYPE_ROTATE      = "ROTATE";
    private static final String TYPE_WORD_CLICK  = "WORD_IMAGE_CLICK";

    /** 1.5.2 中字体作为普通资源注册,type 固定为 "font" */
    private static final String FONT_TYPE = "font";

    private static final String DEFAULT_TEMPLATE_PATH   = "classpath:META-INF/cut-image/template";
    private static final String DEFAULT_BACKGROUND_PATH = "classpath:META-INF/cut-image/resource";

    /** 自定义 ResourceStore: 加载默认模板 + 各类型背景图 + 字体 */
    @Bean
    @Primary
    public ResourceStore tianaiResourceStore(ApplicationContext applicationContext) throws Exception {
        LocalMemoryResourceStore store = new LocalMemoryResourceStore();

        try {
            new DefaultBuiltInResources(DEFAULT_TEMPLATE_PATH).addDefaultTemplate(store);
            log.info("[tianai-captcha] default templates (SLIDER/ROTATE) loaded");
        } catch (Exception e) {
            log.warn("[tianai-captcha] failed to load default templates: {}", e.getMessage());
        }

        try {
            registerDefaultBackgrounds(store, applicationContext);
        } catch (Exception e) {
            log.warn("[tianai-captcha] failed to register default backgrounds: {}", e.getMessage());
        }

        try {
            registerDefaultFonts(store);
        } catch (Exception e) {
            log.warn("[tianai-captcha] failed to register default fonts: {}", e.getMessage());
        }

        return store;
    }

    /** 扫描 classpath:META-INF/cut-image/resource/* 下所有图片,注册给 3 种类型共用 */
    private void registerDefaultBackgrounds(LocalMemoryResourceStore store, ApplicationContext ctx) throws Exception {
        org.springframework.core.io.Resource[] files = ctx.getResources(DEFAULT_BACKGROUND_PATH + "/*");
        int count = 0;
        for (org.springframework.core.io.Resource f : files) {
            String fileName = f.getFilename();
            if (fileName == null) continue;
            String lower = fileName.toLowerCase();
            if (!lower.endsWith(".jpg") && !lower.endsWith(".jpeg") && !lower.endsWith(".png")) continue;

            // ClassPathResourceProvider 用 data.getData() 直接 getResourceAsStream, 不带 classpath: 前缀
            String pureClasspathPath = "META-INF/cut-image/resource/" + fileName;
            Resource bgResource = new Resource("classpath", pureClasspathPath, null, null);
            store.addResource(TYPE_SLIDER,     bgResource);
            store.addResource(TYPE_ROTATE,     bgResource);
            store.addResource(TYPE_WORD_CLICK, bgResource);
            count++;
        }
        log.info("[tianai-captcha] registered {} default background(s) for 3 captcha types", count);
    }

    /**
     * 1.5.2 字体注册: 通过 store.addResource("font", resource) 注入, FontCache 监听器会自动构建字体映射.
     *
     * 注意: 字体 data 必须是 TTF/TTC 实际文件路径;tianai 内部用
     *   Font.createFont(Font.TRUETYPE_FONT, InputStream) 加载.
     *   "system" provider 是 1.5.0 的写法, 1.5.2 已不再支持.
     *
     * tag 必须设为 "default" —— StandardWordClickImageCaptchaGenerator 通过
     *   requiredRandomGetResource("font", "default") 查找字体, 写死 "default".
     */
    private void registerDefaultFonts(LocalMemoryResourceStore store) {
        // tianai 1.5.2 自带 SIMSUN.TTC(META-INF/fonts/SIMSUN.TTC, 中文字符集)
        Resource simsun = new Resource("classpath", "META-INF/fonts/SIMSUN.TTC", "default", null);
        store.addResource(FONT_TYPE, simsun);
        log.info("[tianai-captcha] registered classpath:META-INF/fonts/SIMSUN.TTC for WORD_IMAGE_CLICK (tag=default)");
    }

    /**
     * 覆盖 starter 默认的 SimpleImageCaptchaValidator
     * (starter 里是 @ConditionalOnMissingBean, 定义自己的 Bean 即可生效)
     */
    @Bean
    @Primary
    public cloud.tianai.captcha.validator.ImageCaptchaValidator tianaiImageCaptchaValidator() {
        return new TypeAwareImageCaptchaValidator();
    }
}
```

**关键约束(逐条对应踩坑)**:

| 约束 | 原因(不满足的后果) |
|---|---|
| `@Order(0)` | 必须早于 starter 默认 `ResourceStore` 装配,否则被覆盖 → 资源全丢 |
| `@Primary` | 同上;starter 用 `@ConditionalOnMissingBean` |
| 背景图 `data` **不带 `classpath:` 前缀** | `ClassPathResourceProvider` 直接 `getResourceAsStream(data)` |
| 字体 `tag="default"` | `StandardWordClickImageCaptchaGenerator` 写死 `requiredRandomGetResource("font","default")`;tag 错了 → 点选验证码生成失败 |
| 字体用 `META-INF/fonts/SIMSUN.TTC` | 1.5.2 内置唯一中文字体;`"system"` provider 在 1.5.2 **已不支持** |
| 每步 try/catch | 资源缺失不应阻断启动(只 warn,运行时对应类型不可用) |
| `@Bean @Primary ImageCaptchaValidator` | 覆盖 starter 的 `SimpleImageCaptchaValidator`,使 ROTATE 容差生效 |

#### 3.10.2 容差校验器

```java
package com.basic.framework.config;

import cloud.tianai.captcha.common.AnyMap;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import cloud.tianai.captcha.validator.impl.SimpleImageCaptchaValidator;

/**
 * 按验证码类型区分容差的校验器
 *
 * tianai 默认容差是 0.02(百分比):
 *   - SLIDER : 0.02 * 600px = 12px, 用户拖到缺口附近即可, 合理
 *   - ROTATE : 0.02 * 360°  = 7.2°, 要求用户用滑块精确到 7 度以内, 极其困难
 *
 * 所以这里只把 ROTATE 放宽到 0.10(±36°), 其它类型保持默认值.
 */
public class TypeAwareImageCaptchaValidator extends SimpleImageCaptchaValidator {

    /** 旋转验证码的容差(百分比). 0.10 => 约 ±36 度 */
    private static final float ROTATE_TOLERANT = 0.10f;

    public TypeAwareImageCaptchaValidator() { super(); }

    public TypeAwareImageCaptchaValidator(float defaultTolerant) { super(defaultTolerant); }

    @Override
    public boolean doValidSliderCaptcha(ImageCaptchaTrack imageCaptchaTrack,
                                        AnyMap imageCaptchaValidData,
                                        Float tolerant,
                                        String type) {
        float effective = tolerant == null ? defaultTolerant : tolerant;
        if (CaptchaTypeConstant.ROTATE.equals(type) && effective < ROTATE_TOLERANT) {
            effective = ROTATE_TOLERANT;
        }
        return super.doValidSliderCaptcha(imageCaptchaTrack, imageCaptchaValidData, effective, type);
    }
}
```

**生效条件**:`TianaiCaptchaConfig#tianaiImageCaptchaValidator()` 必须注册为 `@Bean @Primary`,否则 starter 的默认 `SimpleImageCaptchaValidator` 生效,ROTATE 仍是 0.02。

#### 3.10.3 Redis 凭证机制

```java
// CaptchaCheckController(写入侧)
public static final String VERIFIED_KEY_PREFIX = "captcha:verified:";
public static final int    VERIFIED_EXPIRE_SECONDS = 120;

// matching 通过后:
redisCache.setCacheObject(
    VERIFIED_KEY_PREFIX + String.valueOf(idObj),
    "1",
    VERIFIED_EXPIRE_SECONDS,
    java.util.concurrent.TimeUnit.SECONDS);
```

```java
// SysLoginService(读取侧)
String verifiedKey = "captcha:verified:" + captchaId;
try {
    Object verifiedFlag = redisCache.getCacheObject(verifiedKey);
    if (verifiedFlag != null) {
        redisCache.deleteObject(verifiedKey);   // 🔶 一次性,命中即删
        return;                                  // 跳过 matching
    }
} catch (Exception ignore) {
    // redis 不可用时走下面的 matching 兜底
}
```

**语义要点**:

| 要点 | 说明 |
|---|---|
| 写入时机 | 仅 `/captcha/check` 的 `matching()` **成功**后 |
| 读取时机 | `/login` → `validateTianaiCaptcha` |
| 命中动作 | **先 `deleteObject` 再 `return`** → 同一凭证只能用于一次登录 |
| Redis 异常 | `catch (Exception ignore)` **静默降级**到 `matching()`(不影响登录) |
| 与 `matching()` 的关系 | `/captcha/check` 已消费 tianai 缓存,`/login` 若再 `matching` 必失败 → **所以凭证命中必须跳过 matching** |

#### 3.10.4 `matching()` 的一次性语义(必须理解)

```
tianai 内部缓存:
  generate() → 写入 captchaId → validData
  matching(captchaId, track) → 校验 + **删除** captchaId 缓存
                              → 第二次调用同 id: 查不到 → 失败
```

**推论**:

| 场景 | 结果 |
|---|---|
| 前端调 `/captcha/check`(成功) → `/login`(有凭证) | ✅ 正常 |
| 前端调 `/captcha/check`(成功) → `/login`(凭证已过期 > 120s) | ❌ matching 失败(缓存已消费)→ 需重新生成 |
| 前端调 `/captcha/check`(失败) → 同 id 再调 `/check` | ❌ 失败(缓存已消费)→ 必须重新 `GET /captchaImage` |
| 前端不调 `/check`,直接 `/login`(带 captchaData) | ✅ 兜底链路,`matching` 一次校验成功 |
| `/login` 密码错 → 用户重试登录 | ❌ 凭证已 DEL + 缓存已消费 → 需重新验证码 |

#### 3.10.5 SecurityConfig 放行

```java
requests.requestMatchers("/login", "/register", "/captchaImage", "/monitor/shutdown").permitAll()
        // 🔶 /captcha/** 覆盖 /captcha/check
        .requestMatchers("/captcha/**").permitAll()
        .requestMatchers(HttpMethod.GET, "/", "/*.html", "/**/*.html", "/**/*.css", "/**/*.js", "/profile/**").permitAll()
        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/druid/**").permitAll()
        .anyRequest().authenticated();
```

> ⚠️ 三个验证码接口均 `permitAll`,**无专门限流**(见 R27 / A-18)。

#### 3.10.6 资源目录规范

```
basic-framework/src/main/resources/META-INF/cut-image/
├── resource/                         ← 背景图(丢图即生效,无需改代码)
│   ├── 1.jpg      (19074 B)
│   ├── pic1.jpg
│   ├── pic2.jpg
│   ├── pic3.jpg
│   ├── pic4.jpg
│   ├── pic5.jpg
│   └── 推荐格式.txt                  ← 尺寸/格式建议(不参与加载,非图片扩展名自动跳过)
└── template/                         ← 模板(需成对 active + fixed)
    ├── slider_1/{active.png, fixed.png}
    ├── slider_2/{active.png, fixed.png}
    └── rotate_1/{active.png, fixed.png}
```

| 规则 | 说明 |
|---|---|
| 支持的扩展名 | `.jpg` / `.jpeg` / `.png`(大小写不敏感) |
| 其它文件 | 自动跳过(`推荐格式.txt` 即因此被忽略) |
| 背景图复用 | 同一张图注册给 **3 种类型**共用 |
| 模板要求 | 每套模板**必须成对**有 `active.png` + `fixed.png` |
| 字体 | **不在本目录**,用 tianai jar 内置 `META-INF/fonts/SIMSUN.TTC` |

---

## 4. 核心引擎详细设计

### 4.1 登录引擎(`SysLoginService`,🔶 含验证码校验)

#### 4.1.1 `login(...)`

```java
/**
 * 登录验证(行为验证码 tianai-captcha 版本)
 *
 * @param username    用户名
 * @param password    密码(RSA 密文,前端已加密)
 * @param code        验证码;行为验证码模式下为占位符(兼容老 kaptcha 接口)
 * @param uuid        验证码 id(由 /captchaImage 返回)
 * @param captchaData 行为验证码轨迹 JSON 字符串(ImageCaptchaTrack);3 种类型都用该字段
 */
public String login(String username, String password, String code, String uuid, String captchaData) {
    // 1. 验证码总开关
    boolean captchaOnOff = configService.selectCaptchaOnOff();
    if (captchaOnOff) {
        validateTianaiCaptcha(username, uuid, captchaData);      // 🔶
    }

    // 2. 加载用户
    UserDetails u = userDetailsService.loadUserByUsername(username);
    if (u == null) throw UserPasswordNotMatchException("账号不存在");

    // 3. 校验密码
    if (!bCrypt.matches(password, u.getPassword())) {
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, LOGIN_FAIL, "密码错误"));
        throw UserPasswordNotMatchException();
    } else if (UserStatus.DISABLE.code.equals(u.getStatus())) {
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, LOGIN_FAIL, "账号停用"));
        throw new ServiceException("账号已被停用,请联系管理员");
    }

    // 4. 签发 token
    LoginUser loginUser = (LoginUser) u;
    tokenService.createToken(loginUser);
    AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, LOGIN_SUCCESS, "登录成功"));
    return loginUser.getToken();
}
```

#### 4.1.2 `validateTianaiCaptcha(...)`(本分支核心新增)

```java
/**
 * 校验行为验证码(tianai-captcha)
 *
 * @param username    用户名(用于失败日志)
 * @param captchaId   验证码 id(由 /captchaImage 返回的 uuid)
 * @param captchaData 行为验证码轨迹数据 JSON 字符串(ImageCaptchaTrack 序列化结果)
 */
public void validateTianaiCaptcha(String username, String captchaId, String captchaData) {

    // ① 必要字段校验(缺失 → CaptchaExpireException)
    if (StringUtils.isEmpty(captchaId)) {
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(
            username, Constants.LOGIN_FAIL, "行为验证码 id 缺失"));
        throw new CaptchaExpireException();
    }
    if (StringUtils.isEmpty(captchaData)) {
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(
            username, Constants.LOGIN_FAIL, "行为验证码轨迹缺失"));
        throw new CaptchaExpireException();
    }

    // ② 优先读 Redis 凭证(/captcha/check 已 matching 过一次并写下标记)
    //    命中则直接放行并删除,保证同一个验证码只能用一次
    String verifiedKey = "captcha:verified:" + captchaId;
    try {
        Object verifiedFlag = redisCache.getCacheObject(verifiedKey);
        if (verifiedFlag != null) {
            redisCache.deleteObject(verifiedKey);       // 一次性
            return;                                     // 跳过 matching
        }
    } catch (Exception ignore) {
        // redis 不可用时走下面的 matching 兜底
    }

    // ③ 反向序列化轨迹
    ImageCaptchaTrack track;
    try {
        track = jsonToTrack(captchaData);
    } catch (Exception e) {
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(
            username, Constants.LOGIN_FAIL, "行为验证码轨迹解析失败"));
        throw new CaptchaException();
    }

    // ④ matching() 兜底校验(⚠️ 会消费 tianai 缓存)
    ApiResponse<?> result = imageCaptchaApplication.matching(captchaId, track);
    if (result == null || !result.isSuccess()) {
        String msg = (result != null && result.getMsg() != null)
            ? result.getMsg()
            : MessageUtils.message("user.jcaptcha.error");
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(
            username, Constants.LOGIN_FAIL, msg));
        throw new CaptchaException();
    }
}
```

**异常与日志矩阵**:

| 条件 | 抛异常 | `sys_logininfor.msg` |
|---|---|---|
| `captchaId` 为空 | `CaptchaExpireException` | `行为验证码 id 缺失` |
| `captchaData` 为空 | `CaptchaExpireException` | `行为验证码轨迹缺失` |
| 凭证命中 | —(直接 return) | — |
| 轨迹 JSON 解析失败 | `CaptchaException` | `行为验证码轨迹解析失败` |
| `matching()` 失败 | `CaptchaException` | tianai 返回的 `msg`,或 `user.jcaptcha.error` |

#### 4.1.3 `jsonToTrack(...)`

```java
/**
 * 把前端提交的轨迹 JSON 字符串反序列化为 ImageCaptchaTrack
 * 使用 Jackson 以兼容 application/json 格式
 */
private ImageCaptchaTrack jsonToTrack(String json) throws java.io.IOException {
    com.fasterxml.jackson.databind.ObjectMapper mapper =
        new com.fasterxml.jackson.databind.ObjectMapper();
    // startTime/stopTime 可能是 long 毫秒,也可能是 ISO 字符串
    mapper.configure(
        com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return mapper.readValue(json, ImageCaptchaTrack.class);
}
```

> **要点**:`FAIL_ON_UNKNOWN_PROPERTIES=false` —— 前端可能多传字段(如 `type` / `token`),不能因未知字段解析失败。

#### 4.1.4 ⬆️ `TokenService` / `JwtAuthenticationTokenFilter` / `UnlockScreen`

与 `springboot3` 一致(见 `docs/springboot3-详细设计.md` §4.1)。**仅** `login` 签名与验证码段变化。

### 4.2 验证码生成引擎(`CaptchaController`,🔶 完整重写)

```java
@RestController
public class CaptchaController {

    @Autowired private ImageCaptchaApplication imageCaptchaApplication;
    @Autowired private ISysConfigService configService;

    /**
     * 生成行为验证码
     * <p>GET /captchaImage?type=SLIDER</p>
     * <p>type 可选值: SLIDER / ROTATE / WORD_IMAGE_CLICK;不传则用默认</p>
     *
     * <p>返回结构(兼容旧 captchaImage 接口字段):</p>
     * {
     *   "captchaOnOff": true,      // 验证码总开关
     *   "uuid": "<captchaId>",     // 行为验证码 id,前端登录时回传
     *   "img": null,               // 兼容旧字段,行为验证码无单张图,填空
     *   "type": "SLIDER",          // 当前类型
     *   "data": { "id": "...", 背景图, 模板图, 滑块图 }  // 扁平化,绝不下发 expand
     * }
     */
    @GetMapping("/captchaImage")
    public AjaxResult getCode(@RequestParam(required = false) String type) {
        // ① 总开关
        boolean captchaOnOff = configService.selectCaptchaOnOff();

        // ② 类型兜底
        if (StringUtils.isEmpty(type)) {
            type = CaptchaTypeConstant.SLIDER;
        }
        // ③ CONCAT 已下线,自动回退 SLIDER(不报错)
        if (CaptchaTypeConstant.CONCAT.equals(type)) {
            type = CaptchaTypeConstant.SLIDER;
        }

        // ④ 生成实例
        CaptchaResponse<ImageCaptchaVO> response = imageCaptchaApplication.generate(type);

        // ⑤ 扁平化:只取必要字段,绝不下发 expand(含校验答案)
        Map<String, Object> data = new HashMap<>();
        data.put("id", response.getId());
        // 背景图 / 模板图 / 滑块图 等按 ImageCaptchaVO 实际字段展开
        // ...

        // ⑥ 组装兼容字段
        Map<String, Object> result = new HashMap<>();
        result.put("captchaOnOff", captchaOnOff);
        result.put("uuid",        response.getId());
        result.put("img",         null);
        result.put("type",        type);
        result.put("data",        data);
        return AjaxResult.success(result);
    }
}
```

**实现要点**:

| 要点 | 说明 |
|---|---|
| `uuid` = `response.getId()` | 不是 kaptcha 时代的随机 UUID,而是 tianai 的 captchaId |
| `img=null` | 保留字段仅为兼容旧前端;行为验证码是多张图 |
| 扁平化 | **禁止**下发 `expand`(含正确位置等答案数据) |
| `CONCAT` 回退 | 1.5.2 已下线,传入不报错 |
| 总开关 | 即便关闭也返回结构(前端据 `captchaOnOff` 隐藏) |

### 4.3 旁路校验引擎(`CaptchaCheckController`,🔶 新增)

```java
/**
 * 行为验证码(tianai-captcha)二次校验接口
 *
 * <p>POST /captcha/check</p>
 * <p>请求体:</p>
 * {
 *   "id":   "验证码 id(由 /captchaImage 返回)",
 *   "data": { ...ImageCaptchaTrack 字段... }   // 滑动轨迹, 必须包含 trackList
 * }
 *
 * <p>说明: 登录主流程已经把校验合并到 /login, 本接口用于「先验证码、后输密码」的
 * 前端交互, 通过后写 Redis 一次性凭证, /login 命中即可跳过 matching()</p>
 */
@RestController
public class CaptchaCheckController {

    @Autowired private ImageCaptchaApplication imageCaptchaApplication;
    @Autowired private RedisCache redisCache;

    /** Redis key prefix for "this captcha id has already been verified" */
    public static final String VERIFIED_KEY_PREFIX = "captcha:verified:";

    /** How long the verified flag stays valid (seconds) */
    public static final int VERIFIED_EXPIRE_SECONDS = 120;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/captcha/check")
    public AjaxResult check(@RequestBody Map<String, Object> body) {
        // ① 入参校验
        if (body == null) return AjaxResult.error("请求体为空");
        Object idObj   = body.get("id");
        Object dataObj = body.get("data");
        if (idObj == null || dataObj == null) return AjaxResult.error("id 和 data 不能为空");

        // ② 反序列化轨迹(支持 Map 与 JSON 字符串两种形态)
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

        // ③ 必要字段校验
        if (track.getTrackList() == null || track.getTrackList().isEmpty()) {
            return AjaxResult.error("trackList 不能为空");
        }
        if (track.getBgImageWidth() == null || track.getBgImageHeight() == null) {
            return AjaxResult.error("bgImageWidth / bgImageHeight 不能为空");
        }
        if (track.getStartTime() == null || track.getStopTime() == null) {
            return AjaxResult.error("startTime / stopTime 不能为空");
        }

        // ④ matching()(⚠️ 会消费验证码缓存, 同一 id 只能调一次)
        ApiResponse<?> result;
        try {
            result = imageCaptchaApplication.matching(String.valueOf(idObj), track);
        } catch (Exception e) {
            return AjaxResult.error("校验失败: " + e.getMessage());
        }

        if (result != null && result.isSuccess()) {
            try {
                redisCache.setCacheObject(
                    VERIFIED_KEY_PREFIX + String.valueOf(idObj),
                    "1",
                    VERIFIED_EXPIRE_SECONDS,
                    java.util.concurrent.TimeUnit.SECONDS);
            } catch (Exception ignore) {
                // redis unavailable: just return success, /login will fall back to matching()
            }
            return AjaxResult.success("校验成功");
        }

        String msg = (result != null && result.getMsg() != null) ? result.getMsg() : "校验失败";
        return AjaxResult.error(msg);
    }
}
```

**容错设计**:

| 场景 | 行为 |
|---|---|
| `data` 是 Map | `convertValue` 反序列化 |
| `data` 是 JSON 字符串 | `readValue` 反序列化 |
| `data` 是其它类型 | 返回「data 格式错误」 |
| Redis 写失败 | **仍返回成功**(`/login` 会走 matching 兜底) |
| `matching()` 抛异常 | 返回「校验失败: msg」 |

### 4.4 ~ 4.5 (⬆️ 继承 `springboot3`)

| 小节 | 内容 | 状态 |
|---|---|---|
| 4.2 锁屏/解锁 | `store/modules/lock.js` 复用 `/login` | ⬆️ 但**与验证码冲突**(见 §4.6) |
| 4.3 定时任务引擎 | `SysJobServiceImpl` / `AbstractQuartzJob` / `JobInvokeUtil` | ⬆️ 一致 |
| 4.4 代码生成引擎 | `GenTableServiceImpl` / Velocity 模板 | ⬆️ 一致(⚠️ 模板未适配 SB3) |
| 4.5 动态路由/菜单 | `SysMenuServiceImpl.buildMenus` | ⬆️ 一致 |

### 4.6 🔶 锁屏解锁与验证码的冲突(已知问题)

```js
// web-ui/store/modules/lock.js(未改动)
unlock(password) {
  return login({ username: this.userName, password, code: 'LOCK_BYPASS', uuid: 'LOCK' })
```

**问题链**:

```
锁屏解锁 → POST /login { code:'LOCK_BYPASS', uuid:'LOCK', captchaData:'' }
   │
   ├─ captchaOnOff = true
   ▼
validateTianaiCaptcha(username, 'LOCK', '')
   │
   ├─ captchaData 为空 → CaptchaExpireException("行为验证码轨迹缺失")
   ▼
解锁失败 ❌
```

**三种处理方案**(需业务决策,见需求 A-17):

| 方案 | 实现 | 优缺点 |
|---|---|---|
| **A. 锁屏也过验证码** | 锁屏页接入 `TianaiCaptcha` 组件 | 安全但体验重(解锁要滑一次) |
| **B. 短路验证码** | `SysLoginService` 检测 `code=LOCK_BYPASS` → 跳过 `validateTianaiCaptcha` | 体验好,但**开了验证码绕过通道**(需评估:锁屏态本身已持有效 token) |
| **C. 独立解锁接口** | 新增 `/unlockscreen`,只校密码 | 最干净;但要改前端 + 新增接口(超出「验证码改造」范围) |

> 当前代码为**未特殊处理**,即方案 A 的默认行为(锁屏解锁会被拦)。`springboot2-tianai-captcha` 分支同样存在此问题。

---

## 5. 校验引擎

### 5.1 L1 字段级校验(⬆️ 与 `springboot3` 一致)

- 入口:`@Validated` + `jakarta.validation.constraints.*`
- 失败处理:`GlobalExceptionHandler` → 400 + msg

### 5.2 L2 跨业务校验(⬆️ 一致)

用户名唯一 / 角色 key 唯一 / 菜单自引用 / 部门环 / 通知状态 / invokeTarget / 字典值唯一 / 已读唯一 —— 全部与 `springboot3` 一致。

### 5.3 🔶 验证码校验(本分支新增)

| 校验项 | 位置 | 失败动作 |
|---|---|---|
| `type` 合法性 | `CaptchaController` | `CONCAT` → 回退 `SLIDER`(不报错) |
| `id` 存在性 | `imageCaptchaApplication.matching` | 返回失败 msg |
| `trackList` 非空 | `CaptchaCheckController` / `validateTianaiCaptcha` | `AjaxResult.error` / `CaptchaException` |
| `bgImageWidth/Height` 非空 | 同上 | 同上 |
| `startTime/stopTime` 非空 | 同上 | 同上 |
| 轨迹 JSON 可解析 | `jsonToTrack` | `CaptchaException` + 日志「轨迹解析失败」 |
| 容差判定 | `TypeAwareImageCaptchaValidator` | ROTATE 用 0.10,其余 0.02 |
| 凭证 TTL | Redis | 120s 后自动失效 |
| 凭证一次性 | `validateTianaiCaptcha` | 命中即 `DEL` |

---

## 6. 业务模块实现

### 6.1 ~ 6.5 (⬆️ 与 `springboot3` 一致)

用户管理 / 角色管理 / 菜单管理 / 通知已读 / Demo 业务 —— 全部一致,不重复。

### 6.6 🔶 前端验证码组件实现(本分支新增)

#### 6.6.1 PC 端 `web-ui/src/components/TianaiCaptcha/index.vue`(28.1 KB)

**组件接口**:

| Props / Events | 类型 | 说明 |
|---|---|---|
| `v-model:captchaId` | `String` | 验证码 id,写入 `loginForm.uuid` |
| `v-model:captchaData` | `String` | 轨迹 JSON,写入 `loginForm.captchaData` |
| `@success` | `Function` | 组件内部 `/captcha/check` 成功回调(`payload` 含 id/data) |
| `ref` | — | 暴露 `refresh()` 等方法供父组件重置 |

**登录页接入**(实测 `web-ui/src/views/login.vue`):

```vue
<el-form-item v-if="captchaEnabled" prop="code">
  <TianaiCaptcha
    ref="captchaRef"
    v-model:captchaId="loginForm.uuid"
    v-model:captchaData="loginForm.captchaData"
    @success="onCaptchaSuccess" />
</el-form-item>
```

```js
import TianaiCaptcha from '@/components/TianaiCaptcha/index.vue'

const loginForm = reactive({
  username: '',
  password: '',
  code: '',
  uuid: '',          // 行为验证码:tianai-captcha 模式下 uuid 字段就是验证码 id
  captchaData: ''    // tianai-captcha 滑动轨迹(3 种类型都用)
})

const captchaRef = ref(null)
const captchaEnabled = ref(true)   // 由组件内部从后端 /captchaImage 读取

// 行为验证码成功回调:tianai-captcha 内部已通过 /captcha/check 校验
function onCaptchaSuccess(payload) { /* 可在此启用登录按钮 */ }
```

**内部关键逻辑**:

| 环节 | 实现要点 |
|---|---|
| 拉取实例 | `GET /captchaImage?type=<currentType>` |
| 类型切换 | 切换后**重新拉实例**(旧实例不复用) |
| 轨迹采集 | 记录 `{x, y, t}` 序列 + `bgImageWidth/Height` + `startTime/stopTime` |
| 反机器人构造 | **首条补原点** `{x:0,y:0,t:0}`;**y 微抖动**;末段减速 |
| 坐标定位 | **百分比**(避免首帧缩放比未就绪导致尺寸跳动) |
| 校验提交 | 松手后 `POST /captcha/check {id, data}` |
| 失败处理 | 提示 + 自动重新拉实例(同 id 不可重试) |

#### 6.6.2 移动端 `web-mp/src/components/tianai-captcha/tianai-captcha.vue`(21.9 KB)

与 PC 端核心一致,差异点:

| 项 | PC | 移动 |
|---|---|---|
| HTTP | axios(`utils/request.ts`) | `uni.request`(`utils/request.js`) |
| UI 库 | Element Plus | uview-plus(`up-` 前缀) |
| 触摸事件 | `mousedown/mousemove/mouseup` | `touchstart/touchmove/touchend` |
| 组件注册 | 局部 `import` | 局部 `import`(uni-app 3 不支持自定义指令) |

#### 6.6.3 API 层改动

```js
// web-ui/src/api/login.js(移动端同名文件同理)
export function login(username, password, code, uuid, captchaData) {
  return request({
    url: '/login',
    headers: { isToken: false, repeatSubmit: false },
    method: 'post',
    data: { username, password, code, uuid, captchaData }   // 🔶 新增 captchaData
  })
}
```

---

## 7. 审批旁路(本分支不实现)

> `springboot3-extend-flowable` 分支提供:`extend-flowable` 模块 + Flowable + `basicdb_flowable` schema。
> 本分支保留钩子位点(无实现):`BusinessType.GRANT` / `extend-*` 模式 / `gen_table.is_workflow`(待定)。

---

## 8. 审计/报表/Web/安全

### 8.1 审计(⬆️ + 🔶 验证码失败日志)

- 写操作:`@Log` + `LogAspect` → `sys_oper_log`(⬆️)
- 登录事件:`AsyncManager` → `sys_logininfor`(⬆️)
- **🔶 验证码失败的 4 类 msg**(均落 `sys_logininfor`):

| msg | 触发点 |
|---|---|
| `行为验证码 id 缺失` | `validateTianaiCaptcha` 第 ① 段 |
| `行为验证码轨迹缺失` | 同上 |
| `行为验证码轨迹解析失败` | `jsonToTrack` 抛异常 |
| tianai 返回 msg / `user.jcaptcha.error` | `matching()` 失败 |

> ⚠️ **`msg` 不区分 tianai vs 旧 kaptcha 数据**(历史数据混在一起,见 R30)。

### 8.2 报表(⬆️ 一致)

操作日志 / 登录日志 / 服务器监控 —— 与 `springboot3` 一致。
**🔶 验证码专项查询**:`select * from sys_logininfor where msg like '%验证码%'` 可筛出所有验证码失败。

### 8.3 Web 端(`web-ui`,🔶 验证码相关)

| 项 | 内容 |
|---|---|
| 登录页 | `views/login.vue` 接入 `<TianaiCaptcha>`,与 `el-form` 校验联动 |
| 组件 | `components/TianaiCaptcha/index.vue`(28.1 KB) |
| API | `api/login.js` 的 `login()` 加 `captchaData` 参数 |
| 类型切换 | 登录页 UI 提供 3 选 1(由组件内部管理) |
| 其余 | 路由守卫 / 权限指令 / 锁屏 / 上传 —— ⬆️ 与 `springboot3` 一致 |

### 8.4 移动端(`web-mp`,🔶 验证码相关)

| 项 | 内容 |
|---|---|
| 登录页 | `pages/login/index.vue` 接入组件 |
| 组件 | `components/tianai-captcha/tianai-captcha.vue`(21.9 KB) |
| API | `api/login.js` 加 `captchaData` |
| 其余 | ⬆️ 与 `springboot3` 一致 |

### 8.5 安全(⬆️ + 🔶 验证码强化)

| 项 | 实现 |
|---|---|
| 密码 | BCrypt |
| 登录传输 | RSA 加密 password |
| Token | HS512 JWT,30min |
| **🔶 验证码** | **tianai 1.5.2 行为验证码**,3 类型;ROTATE 容差 0.10 |
| **🔶 凭证** | `captcha:verified:<id>` 120s **一次性** |
| **🔶 抗 OCR** | 行为验证码(拖动/旋转/点选)对 OCR 不敏感 |
| **🔶 抗重放** | 凭证命中即删 + `matching()` 消费缓存 |
| XSS / 重复提交 / 限流 / CORS | ⬆️ 一致 |
| **⚠️ 缺口** | `/captcha/**` 无专门限流(R27);无无障碍替代(A-19) |

---

## 9. 错误码

### 9.1 业务错误码(⬆️ 与 `springboot3` 一致)

| 类别 | code | 含义 |
|---|---|---|
| HTTP | 200 / 400 / 401 / 403 / 404 / 429 / 500 | 同 `springboot3` |
| 业务 | `1001` | 验证码错误(`CaptchaException`) |
| 业务 | `1002` | 验证码过期(`CaptchaExpireException`) |
| 业务 | `1003` | 用户名或密码错误 |
| 业务 | `1004`~`1012` | 账号停用 / 角色键 / 用户名 / 超管保护 / 菜单自引用 / Cron / invokeTarget / 已读唯一 |

### 9.2 🔶 验证码专项错误响应(本分支)

| 场景 | 异常 | HTTP / 响应 | 前端提示 |
|---|---|---|---|
| `uuid` 为空 | `CaptchaExpireException` | 500 + `1002` | 「验证码已失效,请刷新」 |
| `captchaData` 为空 | `CaptchaExpireException` | 500 + `1002` | 同上 |
| 轨迹 JSON 解析失败 | `CaptchaException` | 500 + `1001` | 「验证码错误」 |
| `matching()` 失败 | `CaptchaException` | 500 + `1001` | tianai msg,或「验证码错误」 |
| `/captcha/check` 缺 `id`/`data` | — | 200 + `AjaxResult.error("id 和 data 不能为空")` | 组件内提示 |
| `/captcha/check` `trackList` 空 | — | 200 + `error("trackList 不能为空")` | 同上 |
| `/captcha/check` 宽高缺失 | — | 200 + `error("bgImageWidth / bgImageHeight 不能为空")` | 同上 |
| `/captcha/check` 时间缺失 | — | 200 + `error("startTime / stopTime 不能为空")` | 同上 |
| `/captcha/check` `data` 类型错 | — | 200 + `error("data 格式错误, 需要 Map 或 JSON 字符串")` | 同上 |
| `/captcha/check` matching 抛异常 | — | 200 + `error("校验失败: msg")` | 同上 |

> **注意**:`/captcha/check` 用 `AjaxResult.error(...)` 返回 **HTTP 200 + 业务失败**,而 `/login` 走异常(`GlobalExceptionHandler` 转 500)。前端需分别处理。

---

## 10. 并发与线程模型

### 10.1 后端(⬆️ 与 `springboot3` 一致,🔶 新增验证码相关)

| 资源 | 位置 | 配置 |
|---|---|---|
| Tomcat 线程池 | `application.yml` | `max-threads=800` |
| 异步任务线程池 | `ThreadPoolConfig` | `core=10` / `max=50` |
| Druid | `application-druid.yml` | `min-idle=5` / `max-active=20` |
| Quartz | `ScheduleConfig` | 25 |
| Redis | `RedisCache` | 30min(字典/Token);**🔶 120s(验证码凭证)** |
| **🔶 tianai 生成/校验** | `ImageCaptchaApplication` | starter 默认线程模型;生成是 CPU 密集型(图像裁剪) |
| **🔶 资源存储** | `LocalMemoryResourceStore` | 内存持有 6 张背景图 + 3 模板(启动时加载一次) |
| **🔶 字体缓存** | `FontCache` | 监听 `onAddResource` 构建;`SIMSUN.TTC` 内存常驻 |

**并发安全要点**:

| 要点 | 说明 |
|---|---|
| 验证码实例缓存 | tianai 内部线程安全;`matching()` 消费是原子操作 |
| 凭证一次性 | `get` + `delete` **非原子**(极端并发下可能双放行,窗口极小) |
| 建议增强 | 若需严格一次性,改用 `Redis DEL` 返回值判定(`deleteObject` 返回 boolean)或用 Lua 脚本 |

> ⚠️ **已知微小竞态**:`getCacheObject` → `deleteObject` 两步之间若有并发同 `captchaId` 请求,理论上可双双通过。实际影响极小(需拿到同一 captchaId + 同一时刻),但严格场景应改原子删除。

### 10.2 前端

| 资源 | 说明 |
|---|---|
| 组件状态 | 每实例独立;切换类型重建 |
| 请求并发 | 一次只提交一次 `/captcha/check`(防重复消费) |
| 图像渲染 | 百分比坐标避免重排 |

---

## 11. 测试关注点

### 11.1 业务回归(TC-01~TC-32,⬆️ 与 `springboot3` 一致)

| # | 场景 | 重点 |
|---|---|---|
| TC-01 | 登录三要素 | **本分支改为行为验证码**,见 TC-33+ |
| TC-02 | 密码 RSA 加密错误 | 500 + msg |
| TC-03 | Token 30min 失效 | 401 |
| TC-04 | 强退 `forceLogout` | 该 token 立即失效 |
| TC-05 | 角色赋菜单 | 下次登录路由变化 |
| TC-06 | 数据权限 `dataScope=4` | SQL 注入 `create_by` |
| TC-07 | 重复提交 | 500ms 内 → 429 |
| TC-08 | 限流 | 5QPS/10s 第 6 次拒绝 |
| TC-09 | XSS | `<script>` 被过滤 |
| TC-10 | 字典缓存 | 清缓存后取新值 |
| TC-11 | 定时任务启停/执行 | Quartz 状态 |
| TC-12 | 并发禁止 | `concurrent=1` 只跑一份 |
| TC-13 | 代码生成 | 导入→下载→解压 |
| TC-14 | 操作日志 | 成功/失败均落库 |
| TC-15 | 移动端登录 | 写 uni.storage |
| TC-16 | 移动端 Token 失效 | 跳登录 |
| TC-17 | 锁屏解锁 | **本分支需注意验证码冲突(见 TC-45)** |
| TC-18 | 通知已读 | 不重复 |
| TC-19 | 上传超 10MB | 异常 |
| TC-20 | 优雅停机 | 无残留事务 |
| TC-21~TC-32 | SB3 升级专项(jakarta / springdoc / starter / 配置 / 编译 / 启动) | 见 `docs/springboot3-详细设计.md` §11.2 |

### 11.2 🔶 验证码专项(TC-33~TC-48)

| # | 场景 | 重点 |
|---|---|---|
| **TC-33** | 启动日志 | 3 条 `[tianai-captcha]`(templates / backgrounds / fonts) |
| **TC-34** | 背景图注册数量 | 日志 `registered 6 default background(s) for 3 captcha types` |
| **TC-35** | 字体注册 | 日志含 `SIMSUN.TTC ... (tag=default)` |
| **TC-36** | SLIDER 生成 | `GET /captchaImage?type=SLIDER` 返回 `uuid` + `data`;`img=null` |
| **TC-37** | ROTATE 生成 | 同上,`type=ROTATE` |
| **TC-38** | WORD_IMAGE_CLICK 生成 | **能出汉字**(字体生效;若字体缺失则此用例失败) |
| **TC-39** | `CONCAT` 回退 | 传入后返回 `type=SLIDER`,不报错 |
| **TC-40** | SLIDER 主链路登录 | `/captcha/check` 成功 → Redis 有 `captcha:verified:<id>` → `/login` 成功 |
| **TC-41** | ROTATE 容差 | ±36° 内通过;**验证 0.02 时同轨迹失败**(证明 Validator 生效) |
| **TC-42** | 凭证一次性 | `/login` 成功后 Redis key **已不存在**;再次用同 uuid 登录失败 |
| **TC-43** | 密码错后凭证失效 | `/check` 成功 → 密码故意错 → 再登录需重新验证码 |
| **TC-44** | 兜底链路 | 不调 `/check`,直接 `/login` 带 `captchaData` → 成功 |
| **TC-45** | **锁屏解锁 vs 验证码** | 复现 §4.6 问题:锁屏解锁被 `CaptchaExpireException` 拦下(确认现状) |
| **TC-46** | Redis 不可用降级 | 停 Redis → `/check` 仍返回成功(不写凭证)→ `/login` 走 `matching()` 成功 |
| **TC-47** | `captchaEnabled=false` | 改 `sys_config` → 跳过验证码直接登录 |
| **TC-48** | kaptcha 下线校验 | `grep -r "kaptcha" basic-*/src` **仅 2 处 javadoc**;两个 kaptcha 类文件不存在 |
| **TC-49** | `/captcha/check` 入参容错 | `data` 传 Map / JSON 字符串 均可用;传数字返回「格式错误」 |
| **TC-50** | `matching` 二次调用 | 同 uuid 调两次 `/check`,第二次失败(证明缓存被消费) |

---

## 12. 需求变更记录

| 需求 | 落点 |
|---|---|
| 锁屏解锁 | §4.6(⚠️ 与验证码冲突)+ §6.1 + §8.3 |
| 移动端双前端 | §8.4 |
| 数据库 PostgreSQL | §3.1 + §8.5(**零变更**) |
| 操作日志 / 重复提交 / 限流 / XSS | §3.4~3.7 + §8.1 |
| Quartz 动态任务 | §4.3 |
| 代码生成 | §4.4(⚠️ 模板未适配 SB3) |
| 数据权限 | §3 + 《需求文档》§8 |
| ⬆️ **SB 3 升级** | §1.2.5 / §3.9(见 `docs/springboot3-详细设计.md`) |
| **🔶 tianai-captcha 1.5.2 替换 kaptcha** | **§1.2 文件台账 + §3.10 机制 6 小节 + §4.2/4.3 引擎 + §4.1.2 校验 + §6.6 前端 + §9.2 错误 + §11.2 TC-33~TC-50** |
| **🔶 字体注入 1.5.2 新方式** | §3.10.1(`type="font"` + `SIMSUN.TTC` + `tag=default`) |
| **🔶 ROTATE 容差 0.10** | §3.10.2 + §4.1.2 |
| **🔶 Redis 凭证 120s 一次性** | §3.10.3 + §3.10.4 |
| **🔶 锁屏与验证码冲突** | §4.6(待决 A-17) |

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `springboot3` 详细设计(v0.1)为基线(TC-01~TC-32 继承),叠加 tianai-captcha 1.5.2 专题:§1.2 改动文件台账(4 新增 / 7 修改 / 2 删除 / 6 前端)、§3.10 机制 6 小节(装配与资源初始化含完整 `TianaiCaptchaConfig` / 容差校验器 / Redis 凭证 / matching 一次性语义 / SecurityConfig 放行 / 资源目录规范)、§4.1.2 `validateTianaiCaptcha` 完整实现 + 异常日志矩阵、§4.2 `CaptchaController`、§4.3 `CaptchaCheckController`、§4.6 锁屏冲突(3 方案对比)、§6.6 前端组件(接口 / 接入 / 内部要点)、§9.2 验证码错误响应表(10 行)、§10.1 凭证并发竞态提示、TC-33~TC-50 |
