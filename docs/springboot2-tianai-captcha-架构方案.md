# 基础平台(base-postgresql-vue3) 架构方案 — `springboot2-tianai-captcha` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot2-tianai-captcha`** 分支(Spring Boot 2.5.14 + tianai-captcha 1.5.0)
> 上游依赖:`master` 分支(RuoYi-Vue 3.8.2 / Spring Boot 2.5.14 / PostgreSQL 15+)
> 本分支相对 master 的**唯一重大差异** = kaptcha 字符验证码 → tianai-captcha 行为验证码;后端模块数、数据库、其它能力完全一致。

---

## 1. 系统定位

**一句话定位**:在 `master` 最小基线之上,**把 kaptcha 字符验证码整体替换为 tianai-captcha 行为验证码**(滑动 / 旋转 / 文字点选 三种类型可切换),其它能力(用户/角色/菜单/部门/字典/参数/通知/定时任务/代码生成/锁屏)全部沿用 `master`,并保留 PC + 移动双端。

| 维度 | 内容 |
|---|---|
| 使用方 | 内部业务开发者(Java 后端 + Web 前端 + 移动端开发者),最终被业务方运营人员使用 |
| 触发方式 | PC 浏览器、微信小程序、Android/iOS App,均通过 HTTP/JSON 调用后端 REST |
| 数据形态 | PostgreSQL(34 张表) + 本地文件上传 + Redis 缓存(运行期,新增"验证码已校验"凭证) |
| 范围边界 | RBAC 系统管理、字典/参数/通知、定时任务、代码生成、登录/锁屏、操作日志、PC + 移动两套前端、**行为验证码(tianai-captcha 1.5.0)** |
| 不做清单(本分支) | 业务流程引擎(走 `springboot2-extend-flowable` 分支)、多租户、SSO 联邦、Spring Boot 2 → 3/4 升级(走 `springboot3` / `springboot4` 分支) |

### 1.1 核心用户动作必做项

- 登录 / 注销:**行为验证码** + RSA 加密密码(验证码类型可在前端切换)
- 用户/角色/菜单/部门/岗位/字典 等基础数据维护
- 通知公告 + 文件上传下载
- 定时任务(Cron 表达式)在线管理
- 数据表反向生成 Controller/Service/Mapper/前端
- 锁屏 → 解锁
- PC 端 + 移动端 同一套后端接口

---

## 2. 约束与质量属性

| 属性 | 目标 | 标注 |
|---|---|---|
| **JDK / Spring Boot** | 严格锁定 **JDK 11 + Spring Boot 2.5.14**(`javax.servlet`) | **最强约束**,与 `master` 一致 |
| **行为验证码** | **tianai-captcha 1.5.0**(`tianai-captcha-springboot-starter`,适配 SB 2.5 / `javax.servlet`);前端 SDK 1.5.6(自实现组件,不走官方 web-sdk) | **本分支最关键决策**;取代原 kaptcha 字符验证码 |
| **数据库** | PostgreSQL ≥ 15.1,所有对象落在 `basicdb` schema(与 `master` **完全一致**) | JDBC URL 必须带 `currentSchema=basicdb` |
| 鉴权 | JWT(无状态) + BCrypt 密码散列 + **行为验证码** | Token 默认 30 分钟 |
| 一致性 | 关系型事务 + 异步日志(`@Async` 落 `sys_oper_log` / `sys_logininfor`) | 操作日志失败不影响主链路 |
| 性能 | 列表接口走 MyBatis-Plus + PageHelper,Redis 缓存"验证码已校验"凭证(默认 120s) | |
| 审计 | 所有写操作通过 `LogAspect` 切面落 `sys_oper_log`,登录事件落 `sys_logininfor` | 不可关闭 |
| 安全 | XSS 过滤 + 重复提交拦截 + 限流 + CORS 显式白名单 | |
| 部署 | 单体 jar(可执行 war),`tomcat.max-threads=800` | |

**最强约束**:① **JDK 11 + Spring Boot 2.5.14 不升级**;② **数据库全量统一在 `basicdb` schema**;③ **kaptcha 已下线,所有登录场景统一走 tianai-captcha**;④ `pom.xml` 中 `<kaptcha.version>` / `kaptcha` 依赖项是**死引用**(无 module 实际引用),保留仅为方便回滚,**严禁**新增对它的引用。

---

## 3. 总体架构

### 3.1 容器视图(本分支独有改动以 **🔸** 标出)

```
┌───────────────────────────────────────────────────────────────┐
│  用户端                                                        │
│  ┌──────────────────┐    ┌──────────────────────────────┐    │
│  │  web-ui (PC)     │    │  web-mp (移动:uni-app 3)     │    │
│  │  Vue3 + Element  │    │  H5/小程序/Android/iOS        │    │
│  │  Plus + Vite 6   │    │  uview-plus 3 + Pinia         │    │
│  │  🔸TianaiCaptcha │    │  🔸tianai-captcha 自实现     │    │
│  └────────┬─────────┘    └──────────────┬───────────────┘    │
│           │ HTTPS / JSON                │ HTTP/JSON           │
└───────────┼─────────────────────────────┼─────────────────────┘
            ▼                             ▼
┌───────────────────────────────────────────────────────────────┐
│  Spring Boot 2.5.14 (basic-admin-api)                          │
│  ┌─────────────────────────────────────────────────────────┐  │
│  │  Filter 链:CORS → JwtAuthenticationTokenFilter → ...    │  │
│  │  Controller (basic-admin-api / extend-*)                 │  │
│  │  ↓                                                       │  │
│  │  Service (basic-admin-core / extend-quartz / business)  │  │
│  │  ↓                                                       │  │
│  │  MyBatis-Plus 3.5.1 Mapper (XML/注解双轨)              │  │
│  │  ↓                                                       │  │
│  │  MyBatis-Plus PageHelper + Druid 连接池                 │  │
│  └────────────────────────┬────────────────────────────────┘  │
│  ┌────────────────────────┴────────────────────────────────┐  │
│  │  AOP 切面:LogAspect / DataScopeAspect / RateLimiter /    │  │
│  │          RepeatSubmit / DataSource(动态数据源)            │  │
│  │  🔸 验证码:TianaiCaptchaConfig + TypeAwareImageCaptcha-  │  │
│  │     Validator + CaptchaController + CaptchaCheckController│  │
│  │     + SysLoginService.validateTianaiCaptcha             │  │
│  └─────────────────────────────────────────────────────────┘  │
└──────────┬──────────────────────────────────┬──────────────────┘
           ▼                                  ▼
   ┌──────────────────┐              ┌──────────────────┐
   │ PostgreSQL ≥15.1 │              │ Redis(可选)      │
   │ schema=basicdb   │              │ 字典/Token/限流   │
   │ 34 张表 + 5 函数 │              │ 🔸captcha:       │
   │ (与 master 相同) │              │   verified:<id>  │
   └──────────────────┘              └──────────────────┘
```

### 3.2 模块清单(7 个 Maven 子模块,与 `master` 完全一致)

| 模块 | Maven artifact | 职责 | 关键要点 |
|---|---|---|---|
| `basic-admin-api` | `basic-admin-api` | Spring Boot 启动入口 + 全部 Controller(含 `CaptchaController` / `CaptchaCheckController`) | 扫描 `com.basic` + `com.core` |
| `basic-admin-core` | `basic-admin-core` | 业务核心:用户/角色/菜单/部门/字典/参数/通知/日志 等的 Service+Mapper+Domain | 21 张 `sys_*` 表的全部 CRUD |
| `basic-framework` | `basic-framework` | 框架机制:Spring Security、Redis、MyBatis-Plus、Swagger、Filter、AOP、Token、限流、🔸 **TianaiCaptchaConfig / TypeAwareImageCaptchaValidator** | 提供 starter 风格的能力,被 `basic-admin-api` 装配 |
| `basic-common` | `basic-common` | 通用工具/注解/枚举/基类:AjaxResult/R/Excel/XSS/Log/SecurityUtils 等 | 几乎所有模块都依赖;🔸 `LoginBody.captchaData` 字段新增 |
| `business-core` | `business-core` | 业务扩展点(目前是 `Demo` 示例) | 包前缀 `com.core` |
| `extend-quartz` | `extend-quartz` | 定时任务:Quartz 集成 + Cron 在线管理 | 11 张 `qrtz_*` 表 |
| `extend-generator` | `extend-generator` | 代码生成:Velocity 模板 + 表结构反查 | 2 张 `gen_*` 表 |

### 3.3 数据模型核心表

> **完全同 `master`**:34 张表(`gen_*` 2 + `qrtz_*` 11 + `sys_*` 21),均在 `basicdb` schema。详细见《数据库设计》。
>
> 本分支**没有新增任何数据库对象**。`sys_config` 中 `sys.account.captchaEnabled` 是控制验证码总开关的现有 KV;`Redis` 里的 `captcha:verified:<id>` 是运行期临时凭证,不落库。

### 3.4 关键流程

#### 登录流程(行为验证码版)

```
[前端]                          [后端]                                                  [DB / Redis]
  │  GET /captchaImage?type=SLIDER                                                       │
  │ ──────────────────────────▶  CaptchaController                                       │
  │                              │ imageCaptchaApplication.generate(type)               │
  │                              │ → {uuid, type, data, ...}                              │
  │ ◀──────────────────────────                                                        │
  │                                                                                       │
  │  POST /login {username, password, code, uuid, captchaData}                          │
  │ ──────────────────────────▶  SysLoginController → SysLoginService.login             │
  │                              │ ① RSA 解密 → BCrypt 比对                              │
  │                              │ ② validateTianaiCaptcha(username, uuid, captchaData)│
  │                              │     ├─ 优先查 Redis "captcha:verified:<uuid>"        │
  │                              │     │   命中 → 跳过 matching                           │
  │                              │     └─ 未命中 → imageCaptchaApplication.matching() │
  │                              │ ③ TokenService.createToken(loginUser)                 │
  │                              │ ④ AsyncManager.recordLogininfor                      │
  │ ◀──────────────────────────  {token}                                                  │
```

> **`/captcha/check` 旁路**:调试/独立校验场景可显式调用 `CaptchaCheckController.check()`,内部 `imageCaptchaApplication.matching()` → 写 `captcha:verified:<id>` 凭证(120s);后续 `/login` 看到该凭证即跳过 matching,避免重复校验。注意:**`matching()` 会消费验证码缓存**,所以旁路调用后该验证码只能被同一会话的 `/login` 消费一次。

#### 锁屏/解锁
同 `master`,不重复。

#### 定时任务调度
同 `master`,不重复。

### 3.5 状态机

| 对象 | 状态 | 流转 |
|---|---|---|
| 行为验证码实例 | `已生成` / `已校验(Redis 凭证)` / `已消费` | `生成 → 校验 → 凭证 → 消费(matching 后销毁) / 凭证过期 120s` |
| 行为验证码类型 | `SLIDER` / `ROTATE` / `WORD_IMAGE_CLICK`(`CONCAT` 已下线,自动回退到 `SLIDER`) | 前端选 |
| 用户/角色/菜单/通知/任务 | 同 `master` | — |

---

## 4. 关键决策 ADR

| # | 决策点 | 采纳方案 | 备选 + 放弃原因 | 是否演进锁死 |
|---|---|---|---|---|
| ADR-01 | 基础平台底座 | `master` 分支(锁 JDK 11 + SB 2.5.14) | 升 SB3 走独立分支 | **是** |
| ADR-02 | 数据库 schema | `basicdb`(与 `master` 完全一致) | — | **是** |
| ADR-03 | 数据导入方式 | `pg_dump` + `COPY ... FROM stdin` | 与 `master` 一致 | 否 |
| ADR-04 | 鉴权方案 | JWT + Spring Security + BCrypt | 同 `master` | 否 |
| ADR-05 | 验证码方案 | **tianai-captcha 1.5.0 行为验证码**(滑动/旋转/文字点选 3 种类型) | kaptcha 字符验证码(已替换);hCaptcha/geetest 第三方:依赖外网,不安全;自研:成本不划算 | **是**(本分支主线) |
| ADR-06 | 验证码类型切换 | 前端组件支持 3 种类型切换;后端只接受 `ImageCaptchaTrack` JSON,`LoginBody.captchaData` 一个字段适配 4 种类型 | 每种类型单独入参:字段多、扩展差 | **是** |
| ADR-07 | ROTATE 容差 | **0.10**(±36°),自定义 `TypeAwareImageCaptchaValidator` | 默认 0.02(±7.2°):用户体验差 | **是** |
| ADR-08 | 校验时机合并 | `imageCaptchaApplication.matching()` 在 `/login` 内部调用,前端只调一次 `/login` | 前端先调 `/captcha/check` 再调 `/login`:多一次往返,且会过早消费 matching | 否 |
| ADR-09 | 已校验凭证(旁路) | Redis `captcha:verified:<id>`,120s TTL;`/captcha/check` 写,`/login` 读 | 不留凭证:`/login` 必走 matching(慢);存 DB:不必要 | 否 |
| ADR-10 | 模板与背景图 | `classpath:META-INF/cut-image/`(starter 自带 1.jpg + 本仓库 `resource/1.jpg` + `pic1.jpg`) | 远程下载:首启失败风险 | 否 |
| ADR-11 | 前端组件 | **自实现** 3 种验证码组件(不依赖官方 web-sdk) | 官方 web-sdk:体积大、定制性差、本仓库 `LoginBody.captchaData` 字段对齐自实现更顺 | 否 |
| ADR-12 | kaptcha 依赖处置 | `pom.xml` 留 `<kaptcha.version>` 死引用(无 module 用),**严禁新增引用** | 彻底删:无法快速回滚;改为 active:会和 tianai 冲突 | **是** |
| ADR-13 | 包命名 | `com.basic` + `com.core`(业务)双前缀,不变 | — | 否 |
| ADR-14 | 前端双轨 | PC(`web-ui`)+ 移动(`web-mp`)并立 | 同 `master` | **是** |
| ADR-15 | Springfox 3.0 | 沿用 `master` 旧方案 | — | 否 |

---

## 5. 里程碑

| 阶段 | 内容 | 验收 | 触发升级的信号 |
|---|---|---|---|
| **M0(已交付,master)** | RuoYi 3.8.2 基础 + SQL 迁 PostgreSQL + 锁屏 + web-mp + kaptcha 字符验证码 | 详见 `master` 文档 | — |
| **M1(已交付,本分支)** | 在 `master` 基础上把 kaptcha 整体替换为 tianai-captcha 行为验证码(SLIDER/ROTATE/WORD_IMAGE_CLICK 3 种类型可切换);后端 / 前端 / 资源 全部就绪 | `mvn clean compile` 0 ERROR;启动日志 `[tianai-captcha] ... loaded`;3 种验证码均可通过校验;`/login` 链路 < 500ms | — |
| **M2(可选)** | 验证码命中规则:同 IP 失败 N 次后强切更难类型 | 后端配置项 | 黑产攻击增多 |
| **M3(可选)** | 移动端 H5 旋转验证码适配 | web-mp 增加 ROTATE 引导 | 用户反馈 |
| **M4(分支)** | Spring Boot 3.5 + JDK 17 + Jakarta | 单独分支 `springboot3` / `springboot3-tianai-captcha` | 公司层面升级 JDK 基线 |
| **M5(分支)** | Spring Boot 4 + JDK 21 | 单独分支 `springboot4` | — |

**本次不做,直到有信号**:微服务拆分、K8s、APM、消息中心抽象。

---

## 6. 审查与遗留风险

### 6.1 已覆盖

| 维度 | 设计 |
|---|---|
| 失败设计 | `GlobalExceptionHandler` + `R/AjaxResult` 包装;`ServiceException` / `UserPasswordNotMatchException` / `CaptchaException` |
| 幂等 | `@RepeatSubmit` 注解 + `RepeatSubmitInterceptor` |
| 权限 | 菜单级 + 按钮级(`@PreAuthorize("@ss.hasPermi(...)")`)+ 数据级(`@DataScope`) |
| 一致性 | 关系型事务;异步日志通过 `AsyncManager` 隔离失败 |
| 可观测 | `sys_oper_log` / `sys_logininfor` / Swagger / Druid |
| **行为验证码** | **3 种类型可切换 / 容差按类型调优 / 凭证 120s / matching 消费即销毁** |
| kaptcha 隔离 | 依赖保留但无引用;`pom.xml` 显式标注;运行时不加载 |

### 6.2 风险清单

| # | 风险 | 影响 | 缓解 |
|---|---|---|---|
| R1 | `pom.xml` 中 `kaptcha` 是死引用(无 module 用),新成员可能误加 | 中 | README + 文档明确**严禁新增引用**;grep 校验: `grep -r "kaptcha" basic-*/src` 应 0 命中 |
| R2 | tianai-captcha 1.5.0 已较旧,2.x 修复若干 bug | 中 | 锁版本;升 2.x 走新分支(需适配 SB 2.5) |
| R3 | 行为验证码可被 OCR / 模拟轨迹绕过 | 中 | 实际威胁小(对应是自动化脚本);反爬加强:轨迹分析 + 行为画像(M2 候选) |
| R4 | Redis 不可用时 `/login` 走 matching 兜底,但 matching 失败无凭证可重试 | 低 | `SysLoginService.validateTianaiCaptcha` 内部 try/catch,Redis 异常降级到 matching 路径 |
| R5 | `/captcha/check` 旁路文档化不足,易被前端误用 | 低 | 文档与 `CaptchaCheckController` 注释明确"仅供调试/独立校验场景" |
| R6 | Spring Security 5.5 EOL | 高 | 走 SB3 分支;**master/tianai 分支锁版本** |
| R7 | `pom.xml` kaptcha 引用在 `dependencyManagement` 中,**不主动删**,依赖树可能让 `mvn dependency:tree` 输出混淆 | 低 | 接受;以运行时 `NoClassDefFoundError` 风险为 0 为准 |
| R8 | 模板/背景图(1.jpg, pic1.jpg)只 2 张,长期可能重复 | 低 | starter 内置 1 张 + 本仓库 1 张 = 2 张;M1 已够 demo,生产需扩充 |
| R9 | 移动端 `tianai-captcha.vue` 是单文件实现,后续类型扩展需手改 | 低 | 走通用组件抽象(M3 候选) |
| R10 | 行为验证码 UI 风格与 Element Plus 主题不完全统一 | 低 | web-ui 组件已用 `--el-color-primary` 跟随;微调可后续 |
| R11 | 失败行为入库:`sys_logininfor.status='1'` 但 `msg` 不区分 tianai vs kaptcha | 低 | 当前不区分,失败原因记通用"验证码错误" |
| R12 | 切换验证码类型会重新生成实例,前次未消费的不复用 | 低 | 前端不主动切换,用户无感 |

---

## 7. 参考来源

- `README.md`(仓库根)
- 同主题基线文档:`docs/base-postgresql-vue3-架构方案.md`(在 `master` 分支生成,描述 `master` 自身)— 本文件以 `master` 为基线,**只描述本分支(`springboot2-tianai-captcha`)的差异点**:行为验证码完全替换 kaptcha
- 兄弟分支 `springboot2-extend-flowable`:`docs/springboot2-extend-flowable-架构方案.md`(描述 Flowable 集成)— 本分支**不引入** Flowable
- tianai-captcha 1.5.0 官方文档

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿,基于 `springboot2-tianai-captcha` 分支实际扫描(7 个子模块,与 master 一致;差异点仅行为验证码) |
