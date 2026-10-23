# 基础平台(base-postgresql-vue3) 概要设计(HLD) — `springboot3-tianai-captcha` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot3-tianai-captcha`** 分支
> 前置依赖:《架构方案》v0.1、《需求文档》v0.1
> 本分支 = **`springboot3` 基线(SB 3.5.16 / JDK 17 / Jakarta / Security 6 / springdoc)+ tianai-captcha 1.5.2 行为验证码**。
> 业务逻辑架构、核心业务流程、数据设计、接口清单、部署拓扑与 `springboot3` **一致**(本文档不重复);**新增 §2.5 验证码组件职责、§3.7 验证码流程、§6 HD-17~HD-26、§8 P16~P25**。
> 本分支改动以 **🔶** 标出;`springboot3` 继承项以 **⬆️** 标出。

---

## 1. 设计目标与约束回顾

| 来源 | 关键约束 |
|---|---|
| 架构方案 §2 | **JDK 17 + SB 3.5.16**(⬆️ 继承);Jakarta EE(实测 112 处 jakarta / 0 处 javax.servlet);**tianai-captcha 1.5.2**(SB3/jakarta 兼容);**3 种验证码类型**;**ROTATE 容差 0.10**;**凭证 120s 一次性** |
| 架构方案 §2 | 数据库 PostgreSQL ≥ 15,统一 `basicdb` schema(**34 表零变更**);前端两端保持 JS;kaptcha 死引用严禁新增 |
| 需求文档 §7 | `/captchaImage` < 200ms;`/captcha/check` < 300ms;`/login` < 500ms;资源加载失败不阻断启动;Redis 不可用降级 |
| 需求文档 §10 | AC-01~AC-42(其中 **AC-25~AC-42 为验证码专项 18 项**) |

**勿重复大段正文**,仅作引子。

---

## 2. 逻辑架构与模块划分

### 2.1 工程结构(🔶 = 本分支改动)

```
base-postgresql-vue3/
├── pom.xml                 # 父 pom,7 子模块
│                            🔶 tianai.version=1.5.2(新增)
│                            ⬆️ java.version=17 / spring-boot.version=3.5.16
├── basic-admin-api/        # 启动入口 + 全部 Controller
│   └── .../controller/common/
│       ├── CaptchaController.java       🔶 重写(tianai 版)
│       └── CaptchaCheckController.java  🔶 新增
├── basic-admin-core/       # 业务核心(⬆️ SB3 升级,🔶 无改动)
├── basic-framework/        # 框架能力(⬆️ SB3 升级 + 🔶 验证码)
│   └── .../config/
│       ├── TianaiCaptchaConfig.java             🔶 新增
│       ├── TypeAwareImageCaptchaValidator.java  🔶 新增
│       ├── CaptchaConfig.java                   🔶 ❌ 删除(kaptcha)
│       └── KaptchaTextCreator.java              🔶 ❌ 删除(kaptcha)
│   └── .../web/service/SysLoginService.java     🔶 加 validateTianaiCaptcha
│   └── src/main/resources/META-INF/cut-image/   🔶 6 背景图 + 3 模板
├── basic-common/           # 通用(⬆️ + 🔶 LoginBody 加 captchaData)
├── business-core/          # 业务扩展点(⬆️,🔶 无改动)
├── extend-quartz/          # Quartz(⬆️,🔶 无改动)
├── extend-generator/       # 代码生成(⬆️,🔶 无改动;⚠️ 模板未适配 SB3)
├── file-sql/               # 8 个 SQL(🔶 零变更)
├── web-ui/                 # PC(JS)🔶 新增 TianaiCaptcha 组件 + 登录页接入
└── web-mp/                 # 移动(JS)🔶 新增 tianai-captcha 组件 + 登录页接入
```

### 2.2 后端组件职责(⬆️ 继承 `springboot3`,🔶 本分支)

| 模块 | 关键类 | 职责 | 本分支 |
|---|---|---|---|
| `basic-admin-api` | `BasicApplication` | 启动类 | ⬆️ 无变化 |
| | `web.config.SwaggerConfig` | springdoc 配置 | ⬆️ 继承(springfox → springdoc 已完成) |
| | `web.controller.common.CaptchaController` | **验证码生成** | **🔶 重写**(kaptcha → tianai) |
| | `web.controller.common.CaptchaCheckController` | **验证码即时校验 + 写凭证** | **🔶 新增** |
| | `web.controller.system.SysLoginController` | 登录入口 | ⬆️ 仅内部方法签名透传 `captchaData` |
| `basic-framework` | `config.TianaiCaptchaConfig` | **tianai 资源初始化** | **🔶 新增** |
| | `config.TypeAwareImageCaptchaValidator` | **ROTATE 容差放宽** | **🔶 新增** |
| | `config.SecurityConfig` | Security 6 过滤链 | ⬆️ 继承;**🔶 放行 `/captcha/**`** |
| | `web.service.SysLoginService` | 登录 + 验证码校验 | **🔶 加 `validateTianaiCaptcha`** |
| | `manager.factory.AsyncFactory` | 异步日志(UA 解析) | ⬆️ 继承(yauaa) |
| `basic-common` | `core.domain.model.LoginBody` | 登录入参 | **🔶 加 `captchaData`** |
| | `exception.user.CaptchaException` | 验证码异常 | ⬆️ 继承(复用) |
| `basic-admin-core` | `system.service.impl.**` | 业务 Service | ⬆️ 继承 |
| `extend-quartz` | `ScheduleConfig` / `ScheduleUtils` | Quartz | ⬆️ 继承 |
| `extend-generator` | `GenController` / `VelocityUtils` | 代码生成 | ⬆️ 继承(⚠️ 模板未适配 SB3) |

### 2.3 扩展 SPI(⬆️ 继承 + 🔶 验证码新增)

| SPI | 接入方式 | 用例 |
|---|---|---|
| ⬆️ 自定义 Job | `@Component("ryTask")` + `public void ryXxx(...)` | `ryTask.ryNoParams` |
| ⬆️ 自定义权限注解 | `@PreAuthorize("@ss.hasPermi('...')")` | — |
| ⬆️ 字典加载 | `DictUtils` 增枚举/缓存 key | — |
| ⬆️ 数据源 | `@DataSource(DataSourceType.SLAVE)` | 多数据源 |
| ⬆️ 代码生成模板 | `vm/java/...` | — |
| **🔶 自定义校验器** | 继承 `SimpleImageCaptchaValidator` 并重写 `doValidSliderCaptcha` | 按类型调容差(本分支已实现 ROTATE) |
| **🔶 自定义 ResourceStore** | 实现 `ResourceStore` + `@Primary` | 远程背景图 / 动态模板 |
| **🔶 自定义字体** | 在 `TianaiCaptchaConfig` 中注册 `type="font"` 资源(1.5.2 方式) | 替换点选字体 |
| **🔶 凭证 TTL 调整** | 改 `CaptchaCheckController.VERIFIED_EXPIRE_SECONDS` | 生产收紧到 30s |

### 2.4 数据设计(概要,⬆️ 与 `springboot3` 完全一致)

| schema | 表数 | 内容 |
|---|---|---|
| `basicdb` | **34** | `gen_*` 2 + `qrtz_*` 11 + `sys_*` 21 |
| `public` | 0 | 不使用 |

> **🔶 本分支数据库零变更**;**🔶 验证码不落库**(实例在 tianai 内存缓存,凭证在 Redis)。

**运行期非 DB 存储(🔶 新增 1 项)**:

| 存储 | Key | TTL | 本分支 |
|---|---|---|---|
| Redis | `login_tokens:<uuid>` | 30min | ⬆️ 继承 |
| Redis | `sys_dict::<dictType>` | — | ⬆️ 继承 |
| Redis | `REPEAT_SUBMIT:*` | 500ms | ⬆️ 继承 |
| **Redis** | **`captcha:verified:<id>`** | **120s** | **🔶 新增** |
| 本地磁盘 | `D:/ruoyi/uploadPath` | — | ⬆️ 继承 |

### 2.5 🔶 验证码组件职责与依赖关系(本分支核心)

#### 2.5.1 组件清单

| 组件 | 路径 | 类型 | 职责 |
|---|---|---|---|
| `TianaiCaptchaConfig` | `basic-framework/.../config/` | `@Configuration @Order(0)` | 提供 `@Bean @Primary ResourceStore`;启动时加载 3 套模板 + 6 张背景图 + 字体 |
| `TypeAwareImageCaptchaValidator` | `basic-framework/.../config/` | 继承 `SimpleImageCaptchaValidator` | 重写 `doValidSliderCaptcha`;ROTATE 容差 → 0.10 |
| `ImageCaptchaApplication` | tianai starter 自动装配 | 框架 Bean | `generate(type)` / `matching(id, track)` |
| `CaptchaController` | `basic-admin-api/.../controller/common/` | `@RestController` | `GET /captchaImage`;类型兜底;扁平化返回 |
| `CaptchaCheckController` | `basic-admin-api/.../controller/common/` | `@RestController` | `POST /captcha/check`;matching + 写 Redis 凭证 |
| `SysLoginService` | `basic-framework/.../web/service/` | `@Service` | `login(..., captchaData)`;`validateTianaiCaptcha`(凭证优先 → DEL → matching 兜底) |
| `LoginBody` | `basic-common/.../core/domain/model/` | POJO | `captchaData` 字段承载轨迹 JSON |
| `SecurityConfig` | `basic-framework/.../config/` | `@Configuration` | 放行 `/captchaImage` + `/captcha/**` |
| `RedisCache` | `basic-common/.../core/redis/` | 框架 Bean | 凭证读写(`setCacheObject(key, val, ttl, unit)` / `getCacheObject` / `deleteObject`) |
| `ISysConfigService` | `basic-admin-core/...` | `@Service` | `selectCaptchaOnOff()` 读 `sys.account.captchaEnabled` |
| `TianaiCaptcha/index.vue` | `web-ui/src/components/` | Vue SFC | PC 端 3 类型组件(28.1 KB) |
| `tianai-captcha.vue` | `web-mp/src/components/tianai-captcha/` | Vue SFC | 移动端 3 类型组件(21.9 KB) |

#### 2.5.2 依赖关系图

```
                    ┌────────────────────────────────┐
                    │  SecurityConfig                │
                    │  放行 /captchaImage /captcha/** │
                    └───────────────┬────────────────┘
                                    │ 允许访问
              ┌─────────────────────┴─────────────────────┐
              ▼                                           ▼
   ┌───────────────────────┐               ┌──────────────────────────┐
   │  CaptchaController    │               │  CaptchaCheckController  │
   │  GET /captchaImage    │               │  POST /captcha/check     │
   └──────────┬────────────┘               └───────────┬──────────────┘
              │ 依赖                                    │ 依赖
              ▼                                         ▼
   ┌───────────────────────┐               ┌──────────────────────────┐
   │ ImageCaptchaApplication│◀─────────────│ ImageCaptchaApplication  │
   │  .generate(type)      │               │  .matching(id, track)    │
   └──────────┬────────────┘               └───────────┬──────────────┘
              │ 使用                                    │ 成功后写
              ▼                                         ▼
   ┌───────────────────────┐               ┌──────────────────────────┐
   │ TianaiCaptchaConfig   │               │  RedisCache              │
   │  @Primary             │               │  captcha:verified:<id>=1 │
   │  ResourceStore        │               │  EX 120                  │
   │  (模板/背景图/字体)    │               └───────────┬──────────────┘
   └───────────────────────┘                           │ 读取
                                                       ▼
                                          ┌──────────────────────────┐
                                          │  SysLoginService         │
                                          │  validateTianaiCaptcha   │
                                          │  ① GET 凭证 → 命中则 DEL │
                                          │  ② 未命中 → matching()   │
                                          └──────────┬───────────────┘
                                                     │ 调用
                                                     ▼
                                          ┌──────────────────────────┐
                                          │ TypeAwareImageCaptcha-   │
                                          │ Validator(ROTATE 0.10)   │
                                          └──────────────────────────┘
```

#### 2.5.3 关键对象生命周期

```
验证码实例(tianai 内部缓存)
  生成 ──▶ 存活 ──▶ matching() 调用 ──▶ **销毁**(消费)
   │              │
   │              └─ 长时间不校验 → tianai 内部 TTL 过期
   └─ 前端切换类型 → 新实例(旧的自然过期)

凭证(Redis)
  /captcha/check 成功 ──▶ SET (TTL 120s)
       │
       ├─ /login 命中 ──▶ **DEL**(一次性)──▶ 后续不可再用
       └─ 120s 未用 ──▶ 自动过期
```

#### 2.5.4 组件初始化顺序

```
应用启动
  │
  ├─ 1. Spring 装配 tianai starter 的 ImageCaptchaApplication
  │
  ├─ 2. TianaiCaptchaConfig(@Order(0))执行:
  │      ├─ new DefaultBuiltInResources(template).addDefaultTemplate(store)
  │      ├─ registerDefaultBackgrounds(store)   ← 6 张 jpg
  │      └─ 注册 type="font" 资源 → FontCache 监听 onAddResource
  │      └─ 每步失败仅 log.warn(不阻断)
  │
  ├─ 3. @Primary ResourceStore 覆盖 starter 默认
  │
  ├─ 4. TypeAwareImageCaptchaValidator 替换默认 Validator
  │
  └─ 5. 启动日志:
         [tianai-captcha] default templates (SLIDER/ROTATE) loaded
         [tianai-captcha] background resources loaded
         [tianai-captcha] WORD_IMAGE_CLICK fonts loaded
```

> ⚠️ `@Order(0)` 必须 **早于** starter 默认装配,否则被覆盖。

---

## 3. 核心流程设计

### 3.1 登录流程(SB3 基线 ⬆️ + 验证码 🔶)

**状态机(完整登录会话,含验证码)**

```
[匿名]
   │  GET /captchaImage?type=X
   ▼
[实例已生成] ──(用户在组件内完成交互)──▶ [待校验]
   │                                        │ POST /captcha/check
   │                                        ▼
   │                                 [matching 校验]
   │                                    │通过      │失败
   │                                    ▼          ▼
   │                            [写凭证 120s]   [提示重试 + 重新生成]
   │                                    │
   │  POST /login(uuid + captchaData)   │
   ▼                                    ▼
[校验中] ──① 凭证命中 → DEL 并按通过 ──┘
   │      ──② 未命中 → matching() 兜底
   │
   ├─ 验证码失败 ──▶ [失败 + sys_logininfor]
   ├─ 密码失败   ──▶ [失败 + sys_logininfor](凭证已 DEL,需重新验证码)
   ▼
[签发 Token] ──▶ [已登录] ──30min──▶ [匿名]
```

**时序(主链路:check + login)**

```
[前端]                    [CaptchaCheckController]   [Redis]      [SysLoginService]      [DB]
  │ POST /captcha/check          │                     │                │                │
  │ {id, data}                   │                     │                │                │
  │ ─────────────────────────▶   │                     │                │                │
  │                              │ matching(id,track)  │                │                │
  │                              │ (消费缓存)           │                │                │
  │                              │ ───────────────────▶│ SET verified=1 │                │
  │ ◀─────────────────────────   │                     │ EX 120         │                │
  │                              │                     │                │                │
  │ POST /login {u,p,code,uuid,captchaData}            │                │                │
  │ ──────────────────────────────────────────────────────────────────▶ │                │
  │                                                    │ GET verified   │                │
  │                                                    │ ◀──────────────│                │
  │                                                    │ DEL verified   │                │
  │                                                    │ ◀──────────────│                │
  │                                                    │  (跳过 matching)│                │
  │                                                                     │ BCrypt         │
  │                                                                     │ ──────────────▶│
  │                                                                     │ createToken    │
  │ ◀────────────────────────────────────────────────────────────────── {token}          │
```

**时序(兜底链路:直接 login)**

```
[前端]                                              [SysLoginService]         [Redis]
  │ POST /login {u,p,code,uuid,captchaData}                  │                   │
  │ ──────────────────────────────────────────────────────▶  │                   │
  │                                                          │ GET verified      │
  │                                                          │ ◀──── miss/异常 ──│
  │                                                          │ matching(id,track)│
  │                                                          │ (一次性校验)       │
  │ ◀────────────────────────────────────────────────────── {token} / 失败        │
```

### 3.2 验证码生成流程(🔶)

```
[前端]                      [CaptchaController]                [ImageCaptchaApplication]
  │ GET /captchaImage?type=SLIDER        │                              │
  │ ─────────────────────────────────▶   │                              │
  │                                      │ ① captchaOnOff =             │
  │                                      │    configService.selectCaptchaOnOff()
  │                                      │ ② type 为空 → 默认 SLIDER
  │                                      │ ③ type=CONCAT → 回退 SLIDER
  │                                      │ ④ generate(type)             │
  │                                      │ ───────────────────────────▶ │
  │                                      │ ◀─────────────────────────── │
  │                                      │    CaptchaResponse<ImageCaptchaVO>
  │                                      │ ⑤ 扁平化:只取 {id, 背景图, 模板图, 滑块图}
  │                                      │    **绝不下发 expand**
  │                                      │ ⑥ 组装兼容字段 img=null
  │ ◀─────────────────────────────────   │
  │  {captchaOnOff, uuid, img, type, data}
```

### 3.3 锁屏/解锁(⬆️ 继承 + 🔶 验证码约束)

```
PC store/modules/lock.js 触发 lock 状态 → 锁屏页
   │
   ▼ 提交 unlockPassword
   │  POST /login({username, password, code:'LOCK_BYPASS', uuid:'LOCK', captchaData:''})
   ▼
SysLoginService.login:
   ├─ captchaOnOff=true → 仍走 validateTianaiCaptcha
   │      └─ uuid='LOCK' / captchaData 空 → **抛 CaptchaException**
   │         ⚠️ 即当前实现下锁屏解锁会被验证码拦下(见 A-17 / R31)
   └─ 校验密码 → 成功返回原 token
```

> ⚠️ **已知问题**:锁屏解锁复用 `/login`,而验证码开关开启时会因 `uuid='LOCK'` 无凭证而失败。这需要业务确认(见需求文档 A-17):
> - 方案 A:锁屏解锁时也弹验证码(体验重但安全)
> - 方案 B:`SysLoginService` 对 `code=LOCK_BYPASS` 短路验证码(需评估风险:绕过验证码的通道会被发现)

### 3.4 定时任务调度(⬆️ 继承)

```
新增任务:
  SysJobController.add → SysJobServiceImpl.add
    → ScheduleUtils.createScheduleJob → Quartz Scheduler
      → 写 qrtz_* 持久化

触发:
  Cron 到点 → AbstractQuartzJob.execute
    → JobInvokeUtil.invokeMethod(ryTask.ryXxx)
      → try/catch → sys_job_log
```

### 3.5 代码生成(⬆️ 继承;⚠️ 模板未适配 SB3)

```
导入表 → GenTableServiceImpl.importGenTable
  → GenTableColumnServiceImpl.importGenTableCol
  → 写 gen_table / gen_table_column

生成:
  download(genTableIds) →
    VelocityEngine(2.3) 渲染 .vm 模板
    → 打包 zip → 浏览器下载
```

> ⚠️ 生成的前端代码是 **JS 风格**(本分支前端本身就是 JS,无影响);生成的 **Java 代码是 SB 2 风格**(`javax.*` + springfox 注解),需手工改 jakarta + springdoc。

### 3.6 移动端登录(⬆️ 继承 + 🔶 验证码)

```
web-mp/pages/login/index.vue(JS)
  GET /captchaImage?type=X        (utils/request.js)
  POST /captcha/check             (组件内部)
  POST /login (含 captchaData)    (RSA + 行为验证码)
  ↓  Basic-Token 存到 uni.storage
GET /getInfo                      (用户/角色/权限)
GET /getRouters                   (本分支先消费即可)
```

### 3.7 🔶 验证码改造实施流程(已执行,记录备查)

#### 3.7.1 执行顺序

```
步骤 1:父 pom.xml
        - 新增 <tianai.version>1.5.2</tianai.version>
        - dependencyManagement 加 tianai-captcha-springboot-starter 1.5.2
        - 保留 kaptcha 死引用(便于回滚)
        ↓
步骤 2:basic-framework/pom.xml 引入 tianai starter
        ↓
步骤 3:删除 kaptcha 类
        - CaptchaConfig.java
        - KaptchaTextCreator.java
        ↓
步骤 4:新增 TianaiCaptchaConfig
        - @Order(0) + @Bean @Primary ResourceStore
        - 模板 / 6 张背景图 / 字体(1.5.2 用 type="font" 资源)
        ↓
步骤 5:新增 TypeAwareImageCaptchaValidator
        - 继承 SimpleImageCaptchaValidator
        - ROTATE 容差 0.10
        ↓
步骤 6:重写 CaptchaController
        - GET /captchaImage?type=...
        - CONCAT 回退 / 扁平化 / img=null
        ↓
步骤 7:新增 CaptchaCheckController
        - POST /captcha/check
        - matching 成功后写 captcha:verified:<id> EX 120
        ↓
步骤 8:改 LoginBody
        - 新增 captchaData 字段
        ↓
步骤 9:改 SysLoginService
        - login(...) 加 captchaData 参数
        - 新增 validateTianaiCaptcha(凭证优先 → DEL → matching 兜底)
        ↓
步骤 10:改 SecurityConfig
        - requestMatchers("/captchaImage", "/captcha/**").permitAll()
        ↓
步骤 11:放资源
        - META-INF/cut-image/resource/ 6 张 jpg
        - META-INF/cut-image/template/ slider_1, slider_2, rotate_1
        ↓
步骤 12:前端
        - web-ui: 新增 TianaiCaptcha/index.vue + 改 login.vue + api/login.js
        - web-mp: 新增 tianai-captcha.vue + 改登录页 + api/login.js
        ↓
步骤 13:验证
        - 启动日志 3 条 [tianai-captcha]
        - 3 类型均可登录
```

#### 3.7.2 验证链

```
① 依赖正确 ──▶ mvn dependency:tree | grep tianai = 1.5.2
                无 javax.servlet 传递依赖
② 资源加载 ──▶ 启动日志 3 条 [tianai-captcha] loaded
③ 生成可用 ──▶ /captchaImage 三种 type 均返回合法 data
④ 校验可用 ──▶ /captcha/check 成功后 Redis 出现 captcha:verified:<id>
⑤ 登录可用 ──▶ /login 三种类型均可成功;凭证被 DEL
⑥ 降级可用 ──▶ 停 Redis → /login 走 matching 兜底仍可登录
⑦ 旧码下线 ──▶ grep kaptcha 源码仅 2 处 javadoc;两个 kaptcha 类已删
```

#### 3.7.3 常见踩坑与处置

| 症状 | 根因 | 处置 |
|---|---|---|
| 启动报 `NoSuchMethodError: getFontWrappers` | 用了 1.5.0 的字体 API | 改 1.5.2 方式:`type="font"` 资源注入 |
| `WORD_IMAGE_CLICK` 生成失败 / 无汉字 | 字体未加载到 `FontCache` | 确认字体资源在 `ResourceStore` 中注册且 `type="font"` |
| ROTATE 用户总是失败 | 容差仍是默认 0.02(±7.2°) | 确认 `TypeAwareImageCaptchaValidator` 已生效 |
| `/captcha/check` 第二次必失败 | `matching()` 已消费缓存 | 前端失败后必须重新 `GET /captchaImage` |
| `/login` 报"验证码 id 缺失" | `uuid` 未回填 | 检查组件 `v-model:captchaId` 绑定 |
| 前端拿到 `data` 但渲染不出图 | 后端下发了 `expand`/嵌套结构 | 确认 `CaptchaController` 已扁平化 |
| 锁屏解锁卡在验证码 | 锁屏复用 `/login` 但无凭证 | 见 A-17(需业务决策) |
| 背景图不生效 | 文件不在 `resource/` 或扩展名非 jpg | 按 `推荐格式.txt` 放图 |

---

## 4. 接口清单(⬆️ 与 `springboot3` 一致,🔶 验证码相关变更)

> **业务接口零变更**:路径/方法/入参/出参/权限字符全部与 `springboot3` 一致。
> 完整业务清单见 `docs/springboot3-概要设计.md` §5。下表只列**验证码相关**。

### 4.1 🔶 验证码接口(新增/改造)

| 方法 | 路径 | 状态 | 说明 | 权限 |
|---|---|---|---|---|
| GET | `/captchaImage` | **改造** | 生成行为验证码;`?type=SLIDER\|ROTATE\|WORD_IMAGE_CLICK`;`CONCAT` 回退 | `permitAll` |
| POST | `/captcha/check` | **新增** | 即时校验 + 写 Redis 凭证(120s) | `permitAll` |
| POST | `/login` | **改造** | 请求体新增 `captchaData`;内部走 `validateTianaiCaptcha` | `permitAll` |

### 4.2 🔶 接口契约变化明细

| 接口 | 变化 |
|---|---|
| `GET /captchaImage` | 返回新增 `type` / `data`;`img` 固定 `null`;`uuid` 语义 = 验证码 id |
| `POST /login` | 请求体新增 `captchaData`(必填,验证码开启时) |
| `POST /captcha/check` | **全新接口** |
| 其它所有接口 | **零变更** |

### 4.3 内部 SPI(同 §2.3)

---

## 5. 关键技术决策(HD-)

> **HD-01~HD-16 继承自 `springboot3`**(STATELESS JWT / AsyncManager / MP+XML 双轨 / 数据权限切面 / 限流 / Token+Redis / uni-app 组件 / pages.json / 一次升到 3.5.16 / Jakarta 硬切 / springdoc / SecurityFilterChain / starter 变体 / yauaa / jaxb / DB+前端零变更),详见 `docs/springboot3-概要设计.md` §6。下表为**本分支新增 HD-17~HD-26**。

| # | 决策 | 理由 | 影响 |
|---|---|---|---|
| **HD-17** | **tianai-captcha 1.5.2(而非 1.5.0 / 2.x)** | 1.5.2 是明确兼容 **SB 3 / jakarta.servlet** 的版本;1.5.0 有兼容风险;2.x 需重新评估 | 版本需与 SB 小版本一起复验(R21) |
| **HD-18** | **字体走 `ResourceStore` `type="font"` 资源注入** | 1.5.2 **移除了** `MultiImageCaptchaGenerator.getFontWrappers/setFontWrappers`;旧写法编译不过 | 字体加载依赖 `FontCache` 监听 `onAddResource`;失败仅 warn |
| **HD-19** | **校验拆成两步(主链路),同时保留一步(兜底)** | 两步:前端能即时反馈"验证码错了",不必等 `/login` 才报错;兜底:保证 Redis 故障 / 未调 check 时仍能登录 | 需维护两条路径;文档必须说明 `matching()` 只能调一次 |
| **HD-20** | **凭证一次性(命中即 `DEL`)** | 若非一次性,同一验证码可通过可反复尝试密码 → 沦为爆破通道 | 密码输错后必须重新过验证码(A-17 相关体验问题) |
| **HD-21** | **凭证 TTL 120s** | 足够用户"验证码通过 → 输密码 → 点登录";过长增加重放窗口 | 生产可收紧到 30s(A-15) |
| **HD-22** | **ROTATE 容差 0.10 只对 ROTATE 生效** | 统一放宽会让 SLIDER 也失去精度;只调 ROTATE 兼顾体验与安全 | 由 `TypeAwareImageCaptchaValidator` 按 `type` 分支 |
| **HD-23** | **`LoginBody` 单字段 `captchaData`** | 3 种类型轨迹结构同构(都是 `ImageCaptchaTrack`),无需按类型拆字段 | 扩展新类型不改契约 |
| **HD-24** | **前端自实现组件(不用官方 web-sdk)** | 官方 SDK 体积大、难以与 `v-model:captchaId` / `v-model:captchaData` 双向绑定对齐 | 需自维护两端组件(28.1 KB / 21.9 KB) |
| **HD-25** | **返回结构扁平化,绝不下发 `expand`** | `expand` 含内部校验数据,下发等于泄漏答案 | `CaptchaController` 显式挑字段 |
| **HD-26** | **kaptcha 死引用保留、类文件删除** | 保留 pom 便于回滚;删类确保运行时不加载 | grep 校验:源码仅 2 处 javadoc(R29) |

---

## 6. 部署视图(⬆️ 与 `springboot3` 一致)

```
        ┌────────────────────────────┐
        │  Nginx(可选)              │
        │  /api/*  → 8080           │
        │  /        → web-ui dist   │
        └─────────────┬──────────────┘
                      │
                      ▼
        ┌────────────────────────────────────┐
        │  basic-admin-api jar               │
        │  ⬆️ 运行时 JDK 17 / Tomcat 10.1+   │
        │  🔶 tianai-captcha 1.5.2 内嵌       │
        │  🔶 资源:META-INF/cut-image/**      │
        │  端口:8080 / profile: dev|prod      │
        └──────┬──────────────┬──────────────┘
               │              │
               ▼              ▼
        ┌────────────┐   ┌──────────────────────────┐
        │PostgreSQL  │   │  Redis(可选)             │
        │15+(basicdb)│   │  ⬆️ spring.data.redis.*   │
        │34 表       │   │  🔶 captcha:verified:*   │
        └────────────┘   └──────────────────────────┘

        web-mp:  🔶 组件变化,其余与 springboot3 一致
        ┌────────────────────────────┐
        │  HBuilder / 微信开发者工具  │
        │  编译产物:dist/dev/mp-weixin│
        └────────────────────────────┘
```

### 6.1 启动顺序

1. **JDK 17** + IDE 设为 17
2. PostgreSQL 启动 + `psql -f file-sql/basicdb-vue3.sql`(34 表,与升级前一致)
3. **Redis 启动(强烈建议)** —— 否则验证码凭证机制失效,退化为每次 `/login` 都 `matching()`
4. `mvn clean compile` → 0 ERROR
5. `mvn spring-boot:run -pl basic-admin-api` 启动后端
6. **检查启动日志 3 条 `[tianai-captcha] ... loaded`**
7. 访问 `/swagger-ui/index.html` 确认 `/captchaImage` / `/captcha/check` 可见
8. `pnpm dev` / `pnpm dev:h5` 启动前端
9. `admin / admin123` 用行为验证码登录

### 6.2 并发与恢复

- Tomcat `max-threads=800`
- 优雅停机:`actuator/shutdown`
- Druid `min-idle=5` / `max-active=20`
- 定时任务持久化到 `qrtz_*`,重启自动恢复
- **🔶 验证码**:实例在内存(tianai 缓存),**重启后全部失效**(用户需重新生成,无影响);凭证在 Redis,重启后仍有效至 TTL 到期

### 6.3 回滚策略

| 场景 | 回滚动作 | 数据影响 |
|---|---|---|
| 验证码整体不可用 | 回退到 `springboot3` 的 jar(恢复 kaptcha) | **无**(DB 未变) |
| 只想临时关验证码 | 改 `sys_config.sys.account.captchaEnabled=false` | 无 |
| 某种类型不可用 | 前端不提供该类型入口 / `CaptchaController` 强制回退 | 无 |
| 凭证机制异常 | 前端不调 `/captcha/check`,直接 `/login` 走兜底链路 | 无 |

> **优势**:验证码改造**零 DB 改动**,回滚只需换 jar + 恢复 2 个被删的 kaptcha 类(或直接回退到 `springboot3`)。

---

## 7. 风险与待定项

> **P1~P15 继承自 `springboot3`**(日志大表 / refresh token / uni-app alpha / 双包前缀 / 国际化 / javax 残留 / starter 变体 / PathPatternParser / 配置键 / 生成器 SB3 模板 / deprecation 等),详见 `docs/springboot3-概要设计.md` §8。下表为**本分支新增**。

| 编号 | 项 | 影响 | 归属文档 |
|---|---|---|---|
| **P16** | **tianai 1.5.2 与 SB 3.5.16 兼容性** | 中 | 架构 R21 |
| **P17** | **1.5.2 字体注入 API 变更**(旧 API 已删) | 中 | 架构 R22;HD-18 |
| **P18** | **行为验证码仍可被高级 OCR / 轨迹模拟绕过** | 中 | 架构 R23 |
| **P19** | **`matching()` 消费缓存导致"同 id 不能二次校验"** | 中 | 架构 R24;需求 AC-36 相关 |
| **P20** | **凭证一次性 → 密码输错需重新过验证码** | 中 | 架构 R25;HD-20;A-17 |
| **P21** | **Redis 不可用时凭证机制失效(仅降级 matching)** | 低 | 架构 R26 |
| **P22** | **`/captcha/**` 匿名放行,无专门限流** | 低 | 架构 R27;A-18 |
| **P23** | **背景图仅 6 张** | 低 | 架构 R28 |
| **P24** | **kaptcha 死引用可能被误加** | 中 | 架构 R29 |
| **P25** | **锁屏解锁被验证码拦下(当前实现)** | **中** | 架构 R31;A-17;§3.3 已标注 |

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `springboot3` 概要设计(v0.1)为基线(HD-01~16 / P1~15 继承),叠加「tianai-captcha 1.5.2」专题:§2.5 验证码组件职责(依赖关系图 / 实例与凭证生命周期 / 初始化顺序)、§3.1 登录双链路(主/兜底完整时序 + 状态机)、§3.2 生成流程、§3.3 锁屏的验证码冲突(已知问题)、§3.7 改造实施流程(13 步 + 验证链 + 8 类踩坑)、§4.1~4.2 接口契约变化、HD-17~HD-26、§6.3 回滚策略、P16~P25 |
