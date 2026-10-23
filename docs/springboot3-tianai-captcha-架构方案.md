# 基础平台(base-postgresql-vue3) 架构方案 — `springboot3-tianai-captcha` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot3-tianai-captcha`** 分支(`base-postgresql-vue3`,基于 RuoYi-Vue 3.8.2 改造)
> 上游依赖:`springboot3` 分支(RuoYi-Vue 3.8.2 + **Spring Boot 3.5.16 / JDK 17**)→ 再上游 `master`(SB 2.5.14 / JDK 11)
> 本分支 = **`springboot3` 基线 + tianai-captcha 1.5.2 行为验证码**(整体替换 kaptcha);**前端两端保持 JavaScript、数据库 34 张表零变更、业务功能全部不动**。

---

## 1. 系统定位

**一句话定位**:面向中小团队的后台管理基础平台,提供「用户/角色/菜单/部门/字典/参数/定时任务/代码生成/监控」一整套开箱即用的 RBAC 后台能力,并提供 uni-app 移动端(H5 / 微信小程序 / Android / iOS);**本分支在 Spring Boot 3.5.16 基线上,把字符验证码升级为行为验证码(滑动 / 旋转 / 文字点选),显著提升抗 OCR 与自动化攻击能力**。

| 维度 | 内容 |
|---|---|
| 使用方 | 内部业务开发者(Java 后端 + Web 前端 + 移动端开发者),最终被业务方运营人员使用 |
| 触发方式 | PC 浏览器、微信小程序、Android/iOS App,均通过 HTTP/JSON 调用后端 REST |
| 数据形态 | PostgreSQL(34 张表) + 本地文件上传 + Redis(运行期:字典/Token/限流/**验证码凭证**) |
| 范围边界 | RBAC 系统管理、字典/参数/通知、定时任务、代码生成、登录/锁屏、操作日志、PC + 移动两套前端(JS)、**tianai-captcha 行为验证码** |
| 不做清单(本分支) | 前端 TS 化(走 `springboot3_TypeScript`)、业务流程引擎(走 `springboot3-extend-flowable`)、JDK 17 → 21 与 SB 4 升级(走 `springboot4`)、多租户、SSO 联邦 |

### 1.1 核心用户动作必做项

- 登录 / 注销(账号 + 密码 + RSA 加密 + **行为验证码:滑动 / 旋转 / 文字点选**)
- 用户/角色/菜单/部门/岗位/字典 等基础数据维护
- 动态路由 + 按钮级权限(基于角色 + 权限字符串)
- 通知公告 + 文件上传下载
- 定时任务(Cron 表达式)在线管理
- 数据表反向生成 Controller/Service/Mapper/前端
- 锁屏 → 解锁
- PC 端 + 移动端 同一套后端接口
- **验证码类型可切换**:PC 与移动端都能在 SLIDER / ROTATE / WORD_IMAGE_CLICK 之间切换

---

## 2. 约束与质量属性

| 属性 | 目标 | 标注 |
|---|---|---|
| **JDK** | **JDK 17**(继承 `springboot3`) | 强约束;JDK 21 走 `springboot4` |
| **Spring Boot** | **3.5.16**(继承 `springboot3`) | 强约束 |
| **Jakarta EE** | 全部 `jakarta.*`(继承 `springboot3`);实测 **112 处 jakarta import,0 处 `javax.servlet`** | 强约束 |
| **行为验证码** | **tianai-captcha 1.5.2**(`tianai-captcha-springboot-starter`),**Spring Boot 3 / `jakarta.servlet` 兼容版本** | **本分支最关键决策** |
| **验证码类型** | `SLIDER` / `ROTATE` / `WORD_IMAGE_CLICK` 三选一(`CONCAT` 已下线,自动回退 `SLIDER`) | 前端可切换 |
| **ROTATE 容差** | **0.10(±36°)**;其余类型保持 tianai 默认 0.02 | 自定义 `TypeAwareImageCaptchaValidator` |
| **数据库** | PostgreSQL ≥ 15.1,统一 `basicdb` schema,**34 张表 + 5 函数** | **本分支零变更** |
| **API 文档** | springdoc-openapi 2.9.0(继承 `springboot3`);`/swagger-ui/index.html` + `/v3/api-docs` | 与 `springboot3` 一致 |
| **安全框架** | Spring Security 6.x(`SecurityFilterChain` Bean + `requestMatchers`) | 与 `springboot3` 一致 |
| 鉴权 | JWT(无状态)+ BCrypt + **行为验证码** | Token 默认 30 分钟 |
| 一致性 | 关系型事务 + 异步日志(`@Async` 落 `sys_oper_log` / `sys_logininfor`) | 操作日志失败不影响主链路 |
| 性能 | `/login` < 500ms(含 `matching()`);`/captchaImage` < 200ms | 行为验证码比字符验证码略重,已用 Redis 凭证缓解 |
| 审计 | 写操作落 `sys_oper_log`,登录事件落 `sys_logininfor`(**`msg` 不区分 tianai vs kaptcha**) | 不可关闭 |
| 安全 | XSS + 重复提交 + 限流 + CORS 白名单;**行为验证码抗 OCR / 抗脚本** | |
| 部署 | 单体 jar,`tomcat.max-threads=800`;**运行时 JDK 17** | |
| 成本 | 单节点 PostgreSQL + 单实例 Redis + 单 jar,最低 2C4G 可起 | |

**最强约束**:① **JDK 17 + SB 3.5.16 不可回退**;② **`javax.*` 不得出现在新代码**;③ **kaptcha 已下线,所有登录场景统一走 tianai-captcha**;④ **`pom.xml` 中 `kaptcha.version` / `kaptcha` 依赖是死引用**(无 module 引用,便于回滚),**严禁新增引用**;⑤ **数据库 schema 与表结构零变更**;⑥ **前端两端保持 JavaScript**。

---

## 3. 总体架构

### 3.1 容器视图(本分支改动以 **🔶** 标出;`springboot3` 继承项以 **⬆️** 标出)

```
┌───────────────────────────────────────────────────────────────┐
│  用户端(⬆️ SB3 基线,JS 不变)                                    │
│  ┌──────────────────────────┐  ┌────────────────────────────┐ │
│  │  web-ui (PC)             │  │  web-mp (移动:uni-app 3)    │ │
│  │  Vue 3.5.26              │  │  H5/小程序/Android/iOS      │ │
│  │  Element Plus 2.13.1     │  │  uview-plus 3.3.62         │ │
│  │  Vite 6.4.1 · JS         │  │  Vite 5.2.8 · JS           │ │
│  │  🔶 TianaiCaptcha/index  │  │  🔶 tianai-captcha.vue     │ │
│  │     .vue(28.1 KB)       │  │     (21.9 KB)              │ │
│  └────────┬─────────────────┘  └──────────────┬─────────────┘ │
│           │ HTTPS / JSON                      │ HTTP/JSON      │
└───────────┼───────────────────────────────────┼───────────────┘
            ▼                                   ▼
┌───────────────────────────────────────────────────────────────┐
│  ⬆️ Spring Boot 3.5.16 (basic-admin-api) · JDK 17             │
│  ┌─────────────────────────────────────────────────────────┐  │
│  │  ⬆️ Filter 链(jakarta.servlet.*)                        │  │
│  │     CorsFilter → JwtAuthenticationTokenFilter → ...      │  │
│  │  ⬆️ SecurityFilterChain Bean(Security 6)                │  │
│  │      放行:/login /register /captchaImage                 │  │
│  │            🔶 /captcha/**                                │  │
│  │            /swagger-ui/** /v3/api-docs/** /druid/**      │  │
│  │  Controller (basic-admin-api / extend-*)                 │  │
│  │  ↓                                                       │  │
│  │  Service (basic-admin-core / extend-quartz / business)  │  │
│  │  ↓                                                       │  │
│  │  ⬆️ MyBatis-Plus 3.5.7(spring-boot3-starter)            │  │
│  │  ⬆️ PageHelper 2.1.1 + Druid 1.2.28(boot-3-starter)     │  │
│  └────────────────────────┬────────────────────────────────┘  │
│  ┌────────────────────────┴────────────────────────────────┐  │
│  │ AOP:LogAspect / DataScopeAspect / RateLimiter /          │  │
│  │     RepeatSubmit / DataSource                            │  │
│  │ ⬆️ 文档:springdoc-openapi 2.9.0                          │  │
│  │ ⬆️ UA 解析:yauaa 8.2.0                                   │  │
│  │ 🔶 验证码:tianai-captcha 1.5.2                           │  │
│  │     TianaiCaptchaConfig(@Order(0) @Primary ResourceStore)│  │
│  │     TypeAwareImageCaptchaValidator(ROTATE 容差 0.10)     │  │
│  │     CaptchaController(GET /captchaImage)                 │  │
│  │     CaptchaCheckController(POST /captcha/check)          │  │
│  └─────────────────────────────────────────────────────────┘  │
└──────────┬──────────────────────────────────┬──────────────────┘
           ▼                                  ▼
   ┌──────────────────┐              ┌──────────────────────────┐
   │ PostgreSQL ≥15.1 │              │ Redis(可选)              │
   │ schema=basicdb   │              │ 字典/Token/限流           │
   │ 34 张表 + 5 函数 │              │ 🔶 captcha:verified:<id> │
   │ 🔶 与 master 相同 │              │     120s,一次性          │
   └──────────────────┘              └──────────────────────────┘
```

### 3.2 模块清单(7 个 Maven 子模块;⬆️ 继承 `springboot3` 升级,🔶 本分支改动)

| 模块 | Maven artifact | 职责 | 本分支状态 |
|---|---|---|---|
| `basic-admin-api` | `basic-admin-api` | 启动入口 + 全部 Controller | ⬆️ SB3 升级;**🔶 `CaptchaController` 重写、新增 `CaptchaCheckController`** |
| `basic-admin-core` | `basic-admin-core` | 业务核心:用户/角色/菜单/部门/字典/参数/通知/日志 等 | ⬆️ SB3 升级;🔶 无改动 |
| `basic-framework` | `basic-framework` | 框架机制:Security、Redis、MyBatis-Plus、Swagger、Filter、AOP、Token、限流 | ⬆️ SB3 升级;**🔶 新增 `TianaiCaptchaConfig` / `TypeAwareImageCaptchaValidator`、删除 `CaptchaConfig` / `KaptchaTextCreator`、`SysLoginService` 加 `validateTianaiCaptcha`** |
| `basic-common` | `basic-common` | 通用工具/注解/枚举/基类 | ⬆️ SB3 升级;**🔶 `LoginBody` 加 `captchaData` 字段** |
| `business-core` | `business-core` | 业务扩展点(`Demo` 示例) | ⬆️ SB3 升级;🔶 无改动 |
| `extend-quartz` | `extend-quartz` | 定时任务:Quartz 集成 + Cron 在线管理 | ⬆️ SB3 升级;🔶 无改动 |
| `extend-generator` | `extend-generator` | 代码生成:Velocity 模板 + 表结构反查 | ⬆️ SB3 升级;🔶 无改动(⚠️ 模板仍产 SB2 风格代码) |

### 3.3 数据模型核心表(详细见《数据库设计》)

> **🔶 本分支与 `master` / `springboot3` 完全相同**:34 张表(`gen_*` 2 + `qrtz_*` 11 + `sys_*` 21),均在 `basicdb` schema,5 个兼容函数。
> **引入行为验证码不需要任何数据库变更** —— 验证码实例由 tianai 内部缓存持有,校验凭证放 Redis。

| 域 | 表 | 说明 |
|---|---|---|
| 鉴权 | `sys_user` / `sys_role` / `sys_menu` / `sys_user_role` / `sys_role_menu` / `sys_role_dept` / `sys_user_post` | RBAC 主体 + 关联表 |
| 组织 | `sys_dept` / `sys_post` | 部门树 + 岗位 |
| 系统 | `sys_config` / `sys_dict_type` / `sys_dict_data` / `sys_files` / `sys_i18n` | 全局参数(含 `sys.account.captchaEnabled` 总开关)/字典/文件/国际化 |
| 通知 | `sys_notice` / `sys_notice_read` | 公告 + 已读 |
| 审计 | `sys_oper_log` / `sys_logininfor` | 操作/登录日志(验证码失败落此表) |
| 示例 | `sys_demo` | `business-core` 示例表 |
| 定时 | `qrtz_*`(11 张) | Quartz 任务表 |
| 代码生成 | `gen_table` / `gen_table_column` | 表/字段元数据 |

### 3.4 关键流程

#### 登录流程(行为验证码版,PC / 移动共用)

```
[前端 JS]                      [后端 SB 3.5.16]                        [Redis / DB]
  │  GET /captchaImage?type=SLIDER
  │ ─────────────────────────▶  CaptchaController
  │                             │ ① configService.selectCaptchaOnOff()
  │                             │ ② CONCAT → SLIDER 兜底
  │                             │ ③ imageCaptchaApplication.generate(type)
  │                             │ ④ 扁平化 data,绝不下发 expand
  │ ◀─────────────────────────  {captchaOnOff, uuid, type, data}
  │
  │  用户在组件内完成滑动/旋转/点选
  │ ─────────────────────────▶  POST /captcha/check {id, data}
  │                             │ ① 解析 ImageCaptchaTrack
  │                             │ ② matching() ← 消费验证码缓存
  │                             │ ③ 成功 → SET captcha:verified:<id>=1 EX 120
  │ ◀─────────────────────────  ok                              ─────▶ Redis
  │
  │  POST /login {username, password, code, uuid, captchaData}
  │ ─────────────────────────▶  SysLoginController → SysLoginService.login
  │                             │ ① RSA 解密 → ② validateTianaiCaptcha
  │                             │     ├─ GET captcha:verified:<uuid>  ───▶ Redis
  │                             │     │   命中 → **DEL 该 key**(一次性)  ───▶ Redis
  │                             │     │         → 跳过 matching
  │                             │     └─ 未命中/Redis 异常 → matching() 兜底
  │                             │ ③ BCrypt.matches
  │                             │ ④ TokenService.createToken
  │                             │ ⑤ AsyncFactory(⬆️ yauaa 解析 UA)
  │ ◀─────────────────────────  {token}                        ─────▶ sys_logininfor
```

> **两条链路都支持**:
> - **主链路**(推荐):前端先 `/captcha/check` 校验,再 `/login`(凭证命中即跳过 `matching`,避免重复消费)
> - **兜底链路**:前端直接把 `captchaData` 随 `/login` 提交,后端 `matching()` 一次校验(适合不拆两步的场景)

#### 锁屏 / 解锁(与 `springboot3` 一致)

```
PC 端 store/modules/lock.js 维护 lock 状态
  触发:Header 操作菜单 → 锁定
  解锁:输入密码 → POST /login(同登录入口,SysLoginService 内部识别复用 token)
```

> 🔶 锁屏解锁**同样受验证码开关约束**:`sys.account.captchaEnabled=true` 时仍需验证码;若希望锁屏免验证码,需在 `lock.js` 传占位值并让后端短路(当前实现未特殊处理)。

#### 定时任务调度(与 `springboot3` 一致)

```
前端 JS → SysJobController.changeStatus / run / list
        ↓
SysJobServiceImpl → ScheduleUtils → Quartz Scheduler
        ↓
qrtz_* 表持久化 → JobInvokeUtil 反射调 @Component("ryTask").ryXxx()
```

#### 验证码类型切换

```
登录页 UI 切换 type(SLIDER → ROTATE → WORD_IMAGE_CLICK)
   │
   ▼
GET /captchaImage?type=<new>   重新生成实例(旧实例不复用)
   │
   ▼
用户操作新类型 → 采集轨迹 → POST /captcha/check → POST /login
```

### 3.5 状态机

| 对象 | 状态 | 流转 |
|---|---|---|
| **验证码实例** | `已生成` → `已校验(matching 通过)` → `已消费(matching 消费缓存)` | tianai 内部 TTL 控制 |
| **已校验凭证** | `存在(TTL 内)` → `已删除(/login 命中后一次性消费)` / `已过期(120s)` | Redis |
| **验证码类型** | `SLIDER` / `ROTATE` / `WORD_IMAGE_CLICK`(`CONCAT` 自动回退 `SLIDER`) | 前端切换 |
| 定时任务 | `NORMAL` / `PAUSED` | changeStatus 切换 |
| 用户 | `0=正常` / `1=停用` | 新建=0,管理员停用=1 |
| 通知已读 | 未读 / 已读 | `sys_notice_read(user_id, notice_id)` 唯一 |

---

## 4. 关键决策 ADR

> **ADR-01~ADR-18 继承自 `springboot3` 分支**(基础底座 / 数据库 / 鉴权 / ORM / 缓存 / 前端双轨 / Jakarta 迁移 / springdoc / Security 6 / starter 变体 / yauaa / jaxb 等),详见 `docs/springboot3-架构方案.md` §4。下表列出**本分支新增的 ADR-19~ADR-26**。

| # | 决策点 | 采纳方案 | 备选 + 放弃原因 | 是否演进锁死 |
|---|---|---|---|---|
| **ADR-19** | **验证码方案** | **tianai-captcha 1.5.2 行为验证码**(SLIDER / ROTATE / WORD_IMAGE_CLICK) | 留 kaptcha 字符验证码:OCR 即可破解,安全强度不足;hCaptcha / geetest 第三方:依赖外网、数据出境;自研:成本不划算 | **是** — 本分支主线 |
| **ADR-20** | **tianai 版本选型** | **1.5.2**(SB 3 / jakarta 兼容) | 1.5.0(`springboot2-tianai-captcha` 用的版本):与 jakarta 有兼容风险;**2.x**:SB 2.5 侧不兼容,且 1.5.2 已够用 | **是** |
| **ADR-21** | **字体注入方式** | **1.5.2 新方式**:字体作为 `ResourceStore` 中 `type="font"` 的资源注入,依赖 `FontCache` 监听 `onAddResource` | 1.5.0 旧方式(`MultiImageCaptchaGenerator.getFontWrappers/setFontWrappers`):**1.5.2 已移除该 API**,沿用会编译失败 | **是** |
| **ADR-22** | **ROTATE 容差** | **0.10(±36°)** via `TypeAwareImageCaptchaValidator` | tianai 默认 0.02(±7.2°):用户几乎不可能拖准,体验崩坏;再放宽到 0.2(±72°):失去验证意义 | **是** |
| **ADR-23** | **校验时机:合并 vs 分离** | **两者都支持**:主链路 `/captcha/check` + `/login`;兜底链路 `/login` 内 `matching()` | 只做 `/login` 内校验:前端无法即时反馈"验证码错了",体验差;只做分离:多一次往返且需额外约定 | 否 |
| **ADR-24** | **凭证是否一次性** | **一次性**:`/login` 命中 `captcha:verified:<id>` 后立即 **`DEL`** | 不删:同一凭证可重复用于暴力破解密码(安全隐患) | **是** — 安全要求 |
| **ADR-25** | **字段设计** | `LoginBody` 单字段 **`captchaData`** 承载 4 种类型的轨迹 JSON;`uuid` 语义改为「验证码 id」 | 每种类型单独入参:字段爆炸、扩展差 | **是** |
| **ADR-26** | **前端组件实现** | **自实现** 3 类型组件(`TianaiCaptcha/index.vue` 28.1 KB / `tianai-captcha.vue` 21.9 KB),不依赖官方 web-sdk | 官方 web-sdk:体积大、定制性差、与 `captchaData` 字段对齐困难 | 否 |
| **ADR-27** | **背景图与模板来源** | `classpath:META-INF/cut-image/`:**6 张背景图**(1.jpg + pic1~pic5)+ **3 套模板**(slider_1 / slider_2 / rotate_1) | 只用 starter 内置 1 张:容易重复、易被记忆;远程下载:首启失败风险 | 否 |
| **ADR-28** | **kaptcha 依赖处置** | `pom.xml` 保留 `kaptcha.version` / `kaptcha` 死引用(无 module 用),**严禁新增引用**;源码仅 2 处 javadoc 提及 | 彻底删:无法快速回滚;改为 active:与 tianai 冲突 | **是** |

---

## 5. 里程碑

| 阶段 | 内容 | 验收 | 触发升级的信号 |
|---|---|---|---|
| **M0(已交付,`master`)** | RuoYi 3.8.2 基础 + SQL 迁 PostgreSQL + 锁屏 | 7 模块 parent.version=3.8.2;`basicdb-vue3.sql` 可恢复 34 表 | — |
| **M1(已交付,`master`)** | 新增 web-mp(uni-app 3 + uview-plus 3) | H5/小程序/App 三端可登录 | — |
| **M2(已交付,`springboot3`)** | 后端 SB 2.5.14 → 3.5.16 / JDK 11 → 17:jakarta 全量迁移、Security 6、springdoc、starter 换名、yauaa | JDK 17 下 0 ERROR;`grep javax.servlet` = 0 | — |
| **M3(已交付,本分支)** | **tianai-captcha 1.5.2 替换 kaptcha**:3 类型行为验证码 + 2 个 REST 接口 + 2 套前端组件 + 6 张背景图 + Redis 凭证(120s 一次性)+ ROTATE 容差 0.10 | 3 种验证码均可登录;`pnpm dev` / `dev:h5` 正常;启动日志含 3 条 `[tianai-captcha] ... loaded`;`grep kaptcha` 源码 0 命中(除 2 处 javadoc) | — |
| **M4(下一轮)** | 移动端:动态菜单 / 国际化 / WebSocket 推送 / 锁屏 / 个人中心编辑 | 见 web-mp README 待办清单 | 业务方在移动端提出超过 5 个角色/菜单变更需求时排期 |
| **M5(可选)** | **验证码命中规则**:同 IP 失败 N 次切更难类型(SLIDER → ROTATE → WORD_IMAGE_CLICK) | 配置项 + 拦截器 | 黑产攻击增多 / 撞库 |
| **M6(可选)** | deprecation 警告清理 + `vue-tsc` 等工具链对齐 | 编译告警归零 | 团队对告警容忍度下降 |
| **M7(分支)** | 前端 TS 化 | 单独分支 `springboot3_TypeScript` | 需要类型保护 |
| **M8(分支)** | 流程引擎(Flowable) | 单独分支 `springboot3-extend-flowable` | 出现审批流/业务流程需求 |
| **M9(分支)** | SSO:接入 keycloak 联邦 | 账号互通 | 出现第二个业务系统需统一登录 |
| **M10(分支)** | **SB 4.1.1 + JDK 21 + Jackson 3 + Security 7** | 单独分支 `springboot4` | 公司层面升级 JDK 基线 |

**本次不做,直到有信号**:微服务拆分、K8s 化、灰度发布、APM 探针、消息中心抽象、GraalVM 原生镜像。

---

## 6. 审查与遗留风险

### 6.1 已覆盖

| 维度 | 设计 |
|---|---|
| 失败设计 | 统一 `GlobalExceptionHandler` + `R/AjaxResult`;`CaptchaException` / `CaptchaExpireException` / `UserPasswordNotMatchException` |
| 幂等 | `@RepeatSubmit` + `RepeatSubmitInterceptor` |
| 权限 | 菜单级 + 按钮级(`@PreAuthorize`)+ 数据级(`@DataScope`) |
| 一致性 | 关系型事务;异步日志通过 `AsyncManager` 隔离失败 |
| 可观测 | `sys_oper_log` / `sys_logininfor` / springdoc / Druid / `actuator/shutdown` |
| **行为验证码** | 3 类型可切换 / 容差按类型调优 / 凭证 120s 一次性 / `matching()` 消费即销毁 / Redis 不可用降级 |
| kaptcha 隔离 | 依赖保留但无引用;运行时不加载 |

### 6.2 风险清单

> **R1~R20 继承自 `springboot3`**(Security EOL 已消除 / uni-app alpha / 双包前缀 / javax 残留 / starter 变体 / PathPatternParser / 配置键 / Jackson 3 留待 SB4 / Lombok / deprecation / 代码生成模板未适配 SB3 等),详见 `docs/springboot3-架构方案.md` §6.2。下表为**本分支新增风险**。

| # | 风险 | 影响 | 缓解 |
|---|---|---|---|
| **R21** | **tianai-captcha 1.5.2 与 SB 3.5.16 的兼容性** | **中** | 已在 CI 环境验证(启动 + 3 类型生成 + 校验);升级 SB 小版本时需复验 tianai starter |
| **R22** | **1.5.2 字体注入 API 变更**(旧 `getFontWrappers` 已移除) | 中 | ADR-21 已按新方式实现;**升级 tianai 版本必须重读 changelog** |
| **R23** | **行为验证码仍可被高级 OCR / 轨迹模拟绕过** | 中 | 威胁模型是自动化脚本;反爬加强:轨迹分析 + 行为画像(M5 候选) |
| **R24** | **`matching()` 会消费缓存**,若前端重复调 `/captcha/check` 会失败 | 中 | 前端只在用户完成后调一次;失败则重新 `GET /captchaImage` 取新实例 |
| **R25** | **凭证一次性删除后,若 `/login` 后续步骤失败**(如密码错),用户需重新过验证码 | 中 | 这是**有意的安全设计**(防凭证复用爆破);UI 需给出明确重试引导 |
| **R26** | **Redis 不可用时降级到 `matching()`** | 低 | `SysLoginService` 内 try/catch;但此时 `/captcha/check` 的凭证机制失效(会走兜底链路) |
| **R27** | **`/captcha/**` 被 `SecurityConfig` 放行**(匿名可访问) | 低 | 必要设计;但需限流防刷(M5 候选),避免被当作免费图片生成服务 |
| **R28** | **背景图仅 6 张,长期可能被记忆** | 低 | 已比 `springboot2` 版(2 张)丰富;生产建议持续补充(丢图即生效) |
| **R29** | **`kaptcha` 死引用可能被误加** | 中 | README + 文档明确**严禁新增**;`grep -r "kaptcha" basic-*/src` 应仅 2 处 javadoc |
| **R30** | **`sys_logininfor.msg` 不区分 tianai vs kaptcha**(历史数据混在一起) | 低 | 失败原因统一记「验证码错误」;如需区分可加前缀(会影响前端映射) |
| **R31** | **锁屏解锁也需过验证码**,体验略重 | 低 | 当前未特殊短路;若业务反馈强烈,可在 `SysLoginService` 对 `code=LOCK_BYPASS` 短路(需评估安全性) |
| **R32** | **移动端组件单文件 21.9 KB,类型扩展需手改** | 低 | 走通用组件抽象(M4 候选) |

---

## 7. 参考来源

- `README.md`(仓库根,本分支)
- **上游基线文档**:
  - `docs/springboot3-架构方案.md` — 描述本分支的 SB3 基线(JDK 17 / Jakarta / Security 6 / springdoc / yauaa),**ADR-01~18 与 R1~R20 均在彼处**
  - `docs/base-postgresql-vue3-架构方案.md`(在 `master` 分支)— 更上游的原始基线
  - `docs/springboot2-tianai-captcha-架构方案.md` — 同主题的 **SB 2.5.14 版本**(tianai **1.5.0**),可对比两代差异(字体注入 API、版本号)
- 兄弟分支:
  - `springboot3`(本分支去掉验证码改造)
  - `springboot3_TypeScript`(本分支 + 前端 TS)
  - `springboot3-extend-flowable`(本分支 + Flowable)
  - `springboot4` / `springboot4_TypeScript`(SB 4 + JDK 21)
- tianai-captcha 1.5.2 官方文档 / changelog(字体注入变更、`matching()` 消费语义)

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `springboot3` 架构方案(v0.1)为基线(ADR-01~18 / R1~R20 继承),叠加「tianai-captcha 1.5.2 行为验证码」专题:§2 约束(tianai 1.5.2 / 3 类型 / ROTATE 0.10 / 凭证 120s)、§3.1 容器视图(SB3 继承标 ⬆️ + tianai 标 🔶)、§3.4 登录双链路(主链路 check+login / 兜底 login 内 matching)、§3.5 验证码状态机(含凭证一次性)、ADR-19~ADR-28 共 10 条、M3 验证码里程碑、R21~R32 共 12 条 |
