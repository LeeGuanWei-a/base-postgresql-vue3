# 基础平台(base-postgresql-vue3) 概要设计(HLD) — `springboot2-tianai-captcha` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot2-tianai-captcha`** 分支
> 前置依赖:《架构方案》v0.1、《需求文档》v0.1
> 本分支相对 `master` 的差异仅在「行为验证码」专题;其它模块概要与 `master` 一致,本文档不重复。

---

## 1. 设计目标与约束回顾

| 来源 | 关键约束 |
|---|---|
| 架构方案 §2 | JDK 11 + SB 2.5.14 锁死;`basicdb` schema;tianai-captcha 1.5.0;ROTATE 容差 0.10;Redis 凭证 120s;kaptcha 死引用严禁新增 |
| 需求文档 §7 | `/login` < 500ms;`/captchaImage` < 200ms;Redis 故障降级 matching;凭证 TTL 120s |
| 需求文档 §10 | AC-01~AC-15 |

**勿重复大段正文**,仅作引子。

---

## 2. 逻辑架构与模块划分

### 2.1 工程结构(7 个 Maven 子模块,与 `master` 完全一致)

```
base-postgresql-vue3/
├── pom.xml                         # 父 pom(7 子模块)
├── basic-admin-api/                # 启动入口 + 全部 Controller
├── basic-admin-core/               # 业务核心(sys_*/gen_*/qrtz_*/demo)
├── basic-framework/                # 框架能力(Security/Redis/MyBatis/AOP/Filter + 🔸 TianaiCaptcha)
├── basic-common/                   # 工具/注解/枚举/基类 + 🔸 LoginBody.captchaData
├── business-core/                  # 业务扩展点(com.core)
├── extend-quartz/                  # Quartz 集成
├── extend-generator/               # Velocity 代码生成
├── file-sql/                       # 8 个 SQL(basicdb-vue3.sql 唯一运行入口)
├── file-bin/  file-doc/            # 静态资源
├── web-ui/                         # PC(Vue3 + ElementPlus) + 🔸 TianaiCaptcha 组件
└── web-mp/                         # 移动(uni-app3 + uview-plus3) + 🔸 tianai-captcha 组件
```

### 2.2 验证码模块组件职责

| 组件 | 路径 | 职责 |
|---|---|---|
| `TianaiCaptchaConfig` | `basic-framework/src/main/java/com/basic/framework/config/` | 启动时主动加载 SLIDER / ROTATE 模板、扫描 `classpath:META-INF/cut-image/resource` 背景图、注入 WORD_IMAGE_CLICK 字体;`@Order(0)` |
| `TypeAwareImageCaptchaValidator` | `basic-framework/src/main/java/com/basic/framework/config/` | 继承 `SimpleImageCaptchaValidator`;重写 `doValidSliderCaptcha`:ROTATE 类型时把容差从默认 0.02 放宽到 **0.10**(±36°) |
| `ImageCaptchaApplication` | tianai-captcha starter 自动装配 | 提供 `generate(type)` / `matching(id, track)` / `validity(id)` 等核心 API |
| `CaptchaController` | `basic-admin-api/src/main/java/com/basic/web/controller/common/` | `GET /captchaImage?type=...` 生成实例;返回 `{captchaOnOff, uuid, type, data, ...}`(兼容旧 `img` 字段填空) |
| `CaptchaCheckController` | `basic-admin-api/src/main/java/com/basic/web/controller/common/` | `POST /captcha/check` 旁路校验:internal `matching()` → 写 Redis 凭证 `captcha:verified:<id>`(120s) |
| `SysLoginService` | `basic-framework/src/main/java/com/basic/framework/web/service/` | 登录主流程;`validateTianaiCaptcha` 优先读 Redis 凭证,未命中走 `matching()` |
| `LoginBody` | `basic-common/src/main/java/com/basic/common/core/domain/model/` | 新增 `captchaData`(轨迹 JSON);`uuid` 兼容老 kaptcha 模式 |
| `SecurityConfig` | `basic-framework/src/main/java/com/basic/framework/config/` | 放行 `/captcha/**` 路径(原只放行 `/captchaImage`) |
| `RedisCache` | `basic-common/src/main/java/com/basic/common/core/redis/` | 凭证 key-value 存储,TTL 120s |
| `ISysConfigService.selectCaptchaOnOff()` | `basic-admin-core` | 读 `sys.account.captchaEnabled` 总开关 |

### 2.3 前端组件

| 端 | 组件 | 路径 | 关键能力 |
|---|---|---|---|
| PC | `TianaiCaptcha/index.vue` | `web-ui/src/components/TianaiCaptcha/` | 3 类型切换;轨迹构造反机器人规则(首条补原点/y 微抖动/末段减速);百分比定位;不走官方 web-sdk |
| 移动 | `tianai-captcha/tianai-captcha.vue` | `web-mp/src/components/tianai-captcha/` | 同 PC 核心,适配 uni-app + uview-plus |

### 2.4 扩展 SPI

| SPI | 接入方式 | 用例 |
|---|---|---|
| 自定义 Validator | 继承 `SimpleImageCaptchaValidator`,Spring 自动替换 | 容差/规则扩展 |
| 自定义 ResourceStore | 实现 `ResourceStore`,`@Primary` 覆盖 | 远程背景图/动态模板 |
| 自定义 Captcha 字体 | 改 `TianaiCaptchaConfig.tianaiResourceStore` | 替换中文字体 |
| 旁路校验时长 | 改 `CaptchaCheckController.VERIFIED_EXPIRE_SECONDS` | TTL 调整 |

---

## 3. 核心流程设计

### 3.1 行为验证码生成

```
[前端]                              [CaptchaController]                  [TianaiCaptchaConfig]            [imageCaptchaApplication]            [Redis]
   │  GET /captchaImage?type=SLIDER   │                                      │                                  │                                │
   │ ──────────────────────────────▶   │                                      │                                  │                                │
   │                                  │  configService.selectCaptchaOnOff()  │                                  │                                │
   │                                  │  imageCaptchaApplication.generate(type)                                  │
   │                                  │ ────────────────────────────────────────────────────────────────────▶        │
   │                                  │                                      │  (启动时已加载模板/背景图/字体)   │                                │
   │                                  │ ◀────────────────────────────────────────────────────────────────────        │
   │                                  │  CaptchaResponse<ImageCaptchaVO>     │                                  │                                │
   │                                  │  → {uuid=captchaId, type, data:{id, ...}}                                │
   │ ◀─────────────────────────────    │                                      │                                  │                                │
```

### 3.2 主登录链路(行为验证码版)

```
[PC/Mobile]                       [SysLoginController]      [SysLoginService]                          [imageCaptchaApplication]                [Redis]            [DB]
   │  POST /login {username, pwd, code, uuid, captchaData}                                                                                                  │
   │ ──────────────────────────────▶  login()              │                                                                                              │
   │                                  │ ──────────────────▶  │  ① RSA decrypt password                                                                          │
   │                                  │                      │  ② captchaOnOff = configService.selectCaptchaOnOff()                                            │
   │                                  │                      │  ③ if captchaOnOff → validateTianaiCaptcha(username, uuid, captchaData)                         │
   │                                  │                      │     ├─ Redis GET "captcha:verified:<uuid>"  ─────────────────────────────────────────────────▶  │
   │                                  │                      │     │   命中 → 跳过 matching                                                                       │
   │                                  │                      │     │   未命中 / 异常 → 降级                                                                        │
   │                                  │                      │     │                                                                                             │
   │                                  │                      │     │   ImageCaptchaTrack track = jsonToTrack(captchaData)                                          │
   │                                  │                      │     │   ApiResponse<?> result = imageCaptchaApplication.matching(uuid, track)                      │
   │                                  │                      │     │     ──────────────────────────────────────────────────────────────────────────────────────────▶ │
   │                                  │                      │     │   (TypeAwareImageCaptchaValidator:ROTATE 时容差 0.10,其余 0.02)                              │
   │                                  │                      │     │   失败 → 抛 CaptchaException + 落 sys_logininfor                                              │
   │                                  │                      │     └─ 通过                                                                                       │
   │                                  │                      │  ④ UserDetailsService.loadUserByUsername ─────────────────────────────────────────────────────▶ │
   │                                  │                      │  ⑤ BCrypt.matches(password, dbPwd)                                                              │
   │                                  │                      │  ⑥ TokenService.createToken(loginUser)                                                           │
   │                                  │                      │  ⑦ AsyncManager.recordLogininfor ───────────────────────────────────────────────────────────────▶ │
   │ ◀─────────────────────────────  {token}                │                                                                                              │
```

### 3.3 旁路校验 `/captcha/check`

```
[前端/调试]                       [CaptchaCheckController]                [imageCaptchaApplication]            [Redis]
   │  POST /captcha/check {id, data}                                                                                          │
   │ ──────────────────────────────▶  check()                                                                                 │
   │                                  │  ① 解析 ImageCaptchaTrack(支持 Map / JSON String 两种入参)                                 │
   │                                  │  ② 校验 trackList/bgImageWidth-Height/startTime-stopTime 非空                            │
   │                                  │  ③ imageCaptchaApplication.matching(id, track)                                          │
   │                                  │     ──────────────────────────────────────────────────────────────────────────▶         │
   │                                  │  ④ 成功 → Redis SET "captcha:verified:<id>" = "1", EX 120                            │
   │                                  │ ─────────────────────────────────────────────────────────────────────────────────────────▶│
   │                                  │  ⑤ 失败 → 抛 ServiceException + 落 sys_logininfor                                   │
   │ ◀─────────────────────────────  AjaxResult.ok()                                                                            │
```

### 3.4 Redis 不可用降级

```
SysLoginService.validateTianaiCaptcha
   │
   ├─ try { redisCache.getCacheObject(VERIFIED_KEY) }
   │    catch (Exception e) { log.warn("Redis 异常,降级到 matching", e); }
   │
   ├─ 命中 → 跳过 matching, return
   │
   └─ 未命中 / 异常 → 走 matching()(同 3.2 步骤 ③ 后半段)
```

> **注意**:`matching()` 会消费 tianai 内部缓存,同一 `id` 只能匹配 1 次。Redis 凭证机制允许调试/独立校验场景在匹配成功后,**旁路** `/login` 仍能复用(因为 `matching` 已被 `/captcha/check` 调用过)。但 `matching` 调用过 1 次后再调 `matching` 会失败。

### 3.5 切换验证码类型

```
前端 UI 切换 type:
   │  SLIDER → ROTATE → WORD_IMAGE_CLICK
   ▼
GET /captchaImage?type=<new>  (重新生成实例)
   │
   ▼
用户操作新类型 → 重新采集轨迹 → POST /login
```

`CONCAT` 类型已被 tianai-captcha 1.5.0 标记为 deprecated;`CaptchaController` 收到 `CONCAT` 自动回退为 `SLIDER`。

### 3.6 资源加载(启动时)

```
TianaiCaptchaConfig.tianaiResourceStore(应用启动时执行,@Order(0))
   │
   ├─ DefaultBuiltInResources(DEFAULT_TEMPLATE_PATH).addDefaultTemplate(store)
   │   加载 SLIDER/ROTATE 模板(社区版 starter 内置,classpath:META-INF/cut-image/template)
   │
   ├─ 扫描 DEFAULT_BACKGROUND_PATH(classpath:META-INF/cut-image/resource)
   │   加载 1.jpg / pic1.jpg(本仓库 resources 提供)
   │   4 种类型共用
   │
   └─ 注入 WORD_IMAGE_CLICK 中文字体
       GraphicsEnvironment.registerFont(...)
```

---

## 4. 数据设计(概要)

### 4.1 Schema 布局

> **完全同 `master`**:所有对象在 `basicdb` schema(34 张表 + 5 函数);本分支**没有新增任何数据库对象**。

| schema | 表数 | 内容 |
|---|---|---|
| `basicdb` | 34 | gen_* 2 + qrtz_* 11 + sys_* 21 |
| `public` | 0 | 不使用 |

### 4.2 核心表(行为验证码相关)

> 验证码本身**不写库**,仅运行期 Redis 凭证。`sys_config` 中 `sys.account.captchaEnabled` 是总开关(沿用 master)。

| KV Key | 类型 | 默认 | 说明 |
|---|---|---|---|
| `sys.account.captchaEnabled` | boolean | `true` | 总开关;`false` 时 `/login` 不校验验证码(测试用) |

### 4.3 关键索引/查询

| 关键查询 | 命中索引 |
|---|---|
| `select * from sys_user where user_name=?` | `uk_user_name` |
| `select * from sys_config where config_key='sys.account.captchaEnabled'` | `uk_config_key` |
| `Redis GET captcha:verified:<id>` | 内存 |

---

## 5. 接口清单

> 沿用 `master` 的全部接口(`/login` / `/logout` / `/system/*` / `/monitor/*` / `/tool/*` / `/workflow/*` 不适用),本节只列**本分支新增 / 修改**。

### 5.1 行为验证码

| 方法 | 路径 | 说明 | 权限 |
|---|---|---|---|
| GET | `/captchaImage` | 生成行为验证码实例 | anonymous(`SecurityConfig` 放行) |
| GET | `/captchaImage?type=SLIDER` | 显式指定类型 | anonymous |
| GET | `/captchaImage?type=ROTATE` | 同上 | anonymous |
| GET | `/captchaImage?type=WORD_IMAGE_CLICK` | 同上 | anonymous |
| GET | `/captchaImage?type=CONCAT` | **已下线**,自动回退 SLIDER | anonymous |
| POST | `/captcha/check` | 旁路校验:matching + 写 Redis 凭证 120s | anonymous |
| POST | `/login` | 主登录链路,内部含 `validateTianaiCaptcha` | anonymous |

### 5.2 接口变更(相对 `master`)

| 接口 | 变更 |
|---|---|
| `/login` | 请求体 `LoginBody` 新增 `captchaData` 字段(必填) |
| `/captchaImage` | 返回结构改造:`img` 字段填空(行为验证码无单张图);新增 `data` / `type` 字段;`captchaOnOff` 沿用 |
| `SecurityConfig` | 放行 `/captcha/**`(原只放行 `/captchaImage`);`/captcha/check` 是新加的子路径 |

### 5.3 删除的接口

> 无。kaptcha 相关配置类删除(`CaptchaConfig` / `KaptchaTextCreator`),但**没有对应的 HTTP 接口**被删除(它们是内部配置 Bean)。

### 5.4 内部 SPI(同 §2.4)

---

## 6. 关键技术决策(HD-)

| # | 决策 | 理由 | 影响 |
|---|---|---|---|
| HD-01 | tianai-captcha 取代 kaptcha | 行为验证码抗 OCR;3 种类型可切换 | 后端 / 前端 / 资源全面替换 |
| HD-02 | `LoginBody.captchaData` 单一字段 | 4 种类型(实际 3 种 + 1 兜底)用同一字段;扩展只需加类型不需加字段 | 前端按 type 决定 `data` 内容 |
| HD-03 | ROTATE 容差 0.10 | 默认 0.02(±7.2°)用户体验差 | `TypeAwareImageCaptchaValidator` 重写 |
| HD-04 | `matching()` 在 `/login` 内部调用 | 减少前后端往返;前端只调 1 次 | 旁路需显式调 `/captcha/check` |
| HD-05 | Redis 凭证 120s | 调试/独立校验场景;`/login` 命中跳过 matching | Redis 不可用 → 降级 |
| HD-06 | `/captcha/check` 内部 matching() | 单一调用点;**会消费** tianai 缓存 | 同一 `id` 只能匹配 1 次(全局唯一) |
| HD-07 | 前端**自实现**组件 | 不依赖官方 web-sdk(体积大);轨迹构造可定制 | 需自维护 web-ui + web-mp 各 1 组件 |
| HD-08 | 模板/背景图 classpath 加载 | 启动时主动加载;`@Order(0)` 优先 | 丢图即生效(M1 文档化) |
| HD-09 | kaptcha 死引用保留 | 快速回滚;`pom.xml` 显式标注 | grep 校验防误加(R1) |
| HD-10 | `CONCAT` 自动回退 `SLIDER` | 1.5.0 已 deprecated | 不报错,降级 |
| HD-11 | `sys_config` 总开关 | 沿用 RuoYi 机制 | `false` 时不校验(测试用) |
| HD-12 | 失败落 `sys_logininfor` | 登录审计统一(沿用 master) | `msg` 不区分 tianai vs kaptcha |
| HD-13 | 不做"失败切难类型" | M2 候选 | — |
| HD-14 | 单一可执行 jar | 沿用 | — |
| HD-15 | Springfox 3.0 | 沿用 `master` | 与 tianai 1.5.0 不冲突 |

---

## 7. 部署视图

```
        ┌────────────────────────────┐
        │  Nginx(可选)              │
        │  /api/*  → 8080           │
        │  /        → web-ui dist   │
        └─────────────┬──────────────┘
                      │
                      ▼
        ┌────────────────────────────┐
        │  basic-admin-api jar      │
        │  端口:8080                │
        │  profile: dev / prod      │
        │  日志含:                  │
        │   [tianai-captcha] default templates (SLIDER/ROTATE) loaded │
        │   [tianai-captcha] background resources loaded       │
        │   [tianai-captcha] WORD_IMAGE_CLICK fonts loaded    │
        └──────┬──────────────┬─────┘
               │              │
               ▼              ▼
        ┌────────────┐   ┌────────────┐
        │PostgreSQL  │   │  Redis     │
        │15+(basicdb)│   │ (可选)     │
        │34 张表     │   │  凭证 120s │
        └────────────┘   └────────────┘

        web-mp:
        ┌────────────────────────────┐
        │  HBuilder / 微信开发者工具  │
        │  编译产物:dist/dev/mp-weixin│
        │  网络:可访问 8080 后端     │
        └────────────────────────────┘
```

### 7.1 启动顺序

1. PostgreSQL 启动 + `psql -f basicdb-vue3.sql` 恢复库
2. (可选)Redis 启动
3. `mvn spring-boot:run -pl basic-admin-api` 启动后端
4. 检查启动日志出现 `[tianai-captcha] ... loaded` 三条
5. `pnpm dev` 启动 web-ui;`pnpm dev:h5` 启动 web-mp
6. 默认账号 `admin / admin123`,登录后改密

### 7.2 并发与恢复

- Tomcat `max-threads=800`
- Druid `min-idle=5` / `max-active=20`
- 凭证 120s 过期自动清理(无需手动)
- Redis 不可用 → `/login` 走 matching 兜底(降级日志可见)

---

## 8. 风险与待定项

| 编号 | 项 | 影响 | 归属文档 |
|---|---|---|---|
| P1 | `pom.xml` kaptcha 死引用 | 中 | 架构 R1;需求 A-7 |
| P2 | tianai-captcha 1.5.0 较旧 | 中 | 架构 R2;需求 A-6 |
| P3 | 行为验证码可被 OCR / 模拟轨迹绕过 | 中 | 架构 R3;需求 A-5 |
| P4 | Redis 不可用降级 | 低 | 架构 R4 |
| P5 | `/captcha/check` 旁路文档化 | 低 | 架构 R5 |
| P6 | Spring Security 5.5 EOL | 高 | 架构 R6 |
| P7 | 模板/背景图 2 张不足 | 低 | 架构 R8;需求 A-4 |
| P8 | 移动端单文件实现扩展性 | 低 | 架构 R9 |
| P9 | UI 风格与 Element Plus 主题不完全统一 | 低 | 架构 R10 |
| P10 | `sys_logininfor.msg` 不区分 tianai vs kaptcha | 低 | 架构 R11;需求 A-3 |
| P11 | 类型切换重新生成实例 | 低 | 架构 R12 |
| P12 | 凭证 TTL 120s 是否合理 | 低 | 需求 A-2 |
| P13 | 移动端是否需要 ROTATE | 低 | 需求 A-1 |
| P14 | 背景图扩充 | 低 | 需求 A-4 |
| P15 | 命中规则升级 | 低 | 需求 A-5 |

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:覆盖 3 种验证码类型 / 2 接口 / 2 前端组件 / 容差 0.10 / 凭证 120s / Redis 降级 / 15 HD / 15 P 风险 |
