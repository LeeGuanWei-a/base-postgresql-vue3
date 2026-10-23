# 基础平台(base-postgresql-vue3) 需求文档(PRD) — `springboot3-tianai-captcha` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot3-tianai-captcha`** 分支(全栈:后端 + web-ui + web-mp)
> 前置依赖:《架构方案》v0.1
> 本分支 = **`springboot3` 基线(SB 3.5.16 / JDK 17)+ tianai-captcha 1.5.2 行为验证码**。
> **业务功能需求、接口契约、数据库与 `master` 完全一致**;相对 `springboot3` 的**唯一需求级差异 = 验证码方案替换**。本文档不重复业务细节与 SB3 升级细节,只展开「行为验证码」专题。

---

## 1. 文档信息与版本

- 当前版本:**v0.1** — 初稿,基于 README + 代码扫描抽取
- 变更历史

| 版本 | 日期 | 变更 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `springboot3` PRD 为基线,新增「§6.10 行为验证码需求」(3 类型 / 2 接口 / 2 前端组件 / ROTATE 容差 / Redis 凭证一次性 / 双链路)+ 场景 S11~S16 + AC-25~AC-36 + 待定项 A-14~A-21 |

---

## 2. 背景与目标

### 2.1 为什么做

`springboot3` 分支已完成后端 SB 3 升级,但验证码仍是 **kaptcha 2.3.3 字符验证码**,存在两类问题:

- **安全**:4~6 位字符图,OCR 工具识别率极高,撞库脚本可低成本绕过
- **体验**:字符模糊、刷新换图,移动端小屏尤其难辨认

因此本分支把验证码整体替换为 **tianai-captcha 行为验证码**,提供 **滑动 / 旋转 / 文字点选** 三种类型:

| 类型 | 交互 | 抗 OCR | 体验 |
|---|---|---|---|
| `SLIDER` | 拖拼图到缺口 | 中 | 最好(最常用) |
| `ROTATE` | 拖滑块把图转正 | 中 | 中(容差已放宽到 ±36°) |
| `WORD_IMAGE_CLICK` | 按顺序点图中汉字 | **高** | 中(需读字) |

**关键约束**:替换**不得改变**登录接口的对外契约(仍是 `POST /login`,只是多传一个 `captchaData` 字段),**不得**改数据库,**不得**影响其它业务。

### 2.2 目标清单

> **G1~G6 业务目标**与 `master` / `springboot3` 完全一致(RBAC / 定时任务 / 代码生成 / PC+移动双端 / 接口兼容 / PostgreSQL);**G7~G12 为 `springboot3` 已交付的后端升级目标**(继承,不再重复验收细则,见 `docs/springboot3-需求文档.md` §2.2);**G13~G18 为本分支新增**。

| 目标 | 成功标准 |
|---|---|
| G1~G6(继承) | 见 `master` PRD §2.2 |
| G7~G12(继承 `springboot3`) | JDK 17 + SB 3.5.16 / Jakarta / springdoc / 契约零变更 / starter 变体 / 配置迁移 —— 均已交付 |
| **G13(本分支)kaptcha 完整下线** | 源码中 kaptcha **仅剩 2 处 javadoc 兼容说明**;`KaptchaTextCreator` / `CaptchaConfig` 已删除;运行时不加载 kaptcha |
| **G14(本分支)3 种行为验证码可用** | SLIDER / ROTATE / WORD_IMAGE_CLICK 三类型均可生成、可完成、可通过校验 |
| **G15(本分支)类型可切换** | PC 与移动端登录页均可切换类型,切换后重新生成实例,无需刷新页面 |
| **G16(本分支)ROTATE 容差合理** | ROTATE 容差 **0.10(±36°)**;SLIDER / WORD_IMAGE_CLICK 保持默认 0.02 |
| **G17(本分支)校验链路可用且安全** | 主链路 `/captcha/check` + `/login` 可行;**凭证一次性**(命中即删);Redis 不可用时降级到 `matching()` 不阻断登录 |
| **G18(本分支)契约兼容** | `POST /login` 路径/方法不变,新增 `captchaData` 字段;`GET /captchaImage` 返回结构兼容旧字段(`captchaOnOff` / `uuid` / `img`);前端两端的 api 层仅改调用处 |

### 2.3 非目标(本分支不做)

- 前端 TypeScript 化(走 `springboot3_TypeScript`)
- 业务流程引擎(走 `springboot3-extend-flowable`)
- JDK 17 → 21 / SB 3 → 4 / Jackson 3 / Security 7(走 `springboot4`)
- 多租户 / SaaS 化
- SSO 联邦(走 keycloak 集成项目)
- 微服务拆分 / K8s 化 / 灰度发布
- 国际化完整词条(仅引入 vue-i18n,词条待补)
- **数据库变更**(本分支零变更)
- **验证码命中规则升级**(同 IP 失败 N 次切难类型 — 列 M5 候选)
- **锁屏免验证码短路**(当前未做,见 R31 / A-17)
- **第三方验证码服务**(hCaptcha / geetest — 依赖外网,弃)
- **tianai 2.x 升级**(1.5.2 已够用,且 2.x 兼容性需评估)

---

## 3. 术语表

| 术语 | 含义 |
|---|---|
| RBAC | Role-Based Access Control,基于角色的访问控制 |
| 数据权限 | 在功能权限之上,按「本人 / 本部门 / 本部门及子 / 全部 / 自定义」过滤数据范围 |
| 锁屏 | 离开座位时手动锁定前端,需重新输入密码解锁;不销毁 Token |
| **行为验证码** | 通过用户交互行为(拖动/旋转/点击)完成验证,OCR 难以自动化 |
| **tianai-captcha** | 开源 Java 行为验证码库;本分支用 **1.5.2**(SB 3 / jakarta 兼容) |
| **SLIDER** | 滑动验证码(拖动拼图到缺口) |
| **ROTATE** | 旋转验证码(拖动滑块把图转正) |
| **WORD_IMAGE_CLICK** | 文字点选验证码(按顺序点击图中汉字) |
| **CONCAT** | 滑动还原验证码;**1.5.2 已下线**,传入自动回退 `SLIDER` |
| **`ImageCaptchaTrack`** | tianai 的轨迹数据结构:`trackList`(点序列)+ `bgImageWidth/Height` + `startTime/stopTime` |
| **`LoginBody.captchaData`** | `ImageCaptchaTrack` 的 JSON 字符串;3 种类型共用该字段 |
| **`captcha:verified:<id>`** | Redis 凭证,表示该验证码已通过 `/captcha/check` 校验;**TTL 120s,`/login` 命中后立即删除(一次性)** |
| **`matching()`** | tianai 内置校验方法;**会消费验证码缓存**,同一 id 只能调用一次 |
| **ROTATE 容差** | tianai 默认 0.02(±7.2°);本分支放宽到 **0.10(±36°)** |
| **`FontCache`** | tianai 1.5.2 的字体缓存;监听 `ResourceStore.onAddResource` 事件 |
| `basicdb` | PostgreSQL schema,本项目所有表所在 |
| uview-plus | uView 的 Vue3 版本,组件前缀 `up-` |
| Jakarta EE | Java EE 改名后的规范,包名 `jakarta.*`(继承 `springboot3`) |

---

## 4. 用户角色与使用场景

### 4.1 角色

| 角色 | 关键动作 | 频率 |
|---|---|---|
| **超级管理员(`admin`)** | 全部功能;可改 `sys_config.sys.account.captchaEnabled` 总开关 | 高 |
| **业务管理员** | 受数据权限约束的 CRUD | 中 |
| **审计员** | 仅看 `sys_oper_log` / `sys_logininfor` | 低 |
| **业务运营(PC)** | 登录(**需过行为验证码**)/ 查看通知 | 高 |
| **业务运营(移动)** | 登录(需过行为验证码)/ 通知 / 个人中心 | 中 |
| **开发者** | 调整验证码类型 / 容差 / 凭证 TTL;排查验证码失败 | 低 |
| **攻击者(威胁模型)** | 撞库 / 自动化登录 → **本分支主要防护对象** | — |

### 4.2 关键场景走查

> **S1~S10** 与 `master` / `springboot3` 一致(登录浏览、角色赋权、代码生成、移动端登录、锁屏、定时任务、SB3 升级回归、Swagger 迁移、starter 选型、生成器 jakarta 修正),详见 `docs/springboot3-需求文档.md` §4.2。**其中 S1/S4/S5 的"登录"步骤在本分支改为行为验证码**,具体见下方 S11~S16。

#### 🔶 场景 S11:首次登录(PC 滑动验证码,主链路)

1. 打开登录页 → 组件自动 `GET /captchaImage?type=SLIDER`
2. 后端返回 `{captchaOnOff: true, uuid: "<id>", type: "SLIDER", data: {id, 背景图, 模板图, 滑块图, ...}}`
3. 前端渲染滑动条;用户拖动滑块到缺口
4. 松手后组件**自动** `POST /captcha/check {id, data}`(轨迹按反机器人规则构造:首条补原点 / y 微抖动 / 末段减速)
5. 后端 `matching()` 通过 → 写 `captcha:verified:<id>`(TTL 120s)→ 返回成功
6. 前端显示"验证通过",用户点登录 → `POST /login {username, password(密文), code, uuid, captchaData}`
7. 后端 `validateTianaiCaptcha` 命中 Redis 凭证 → **DEL 该 key** → 跳过 `matching` → 直接走 BCrypt
8. 登录成功返回 `token`

#### 🔶 场景 S12:切换到旋转验证码

1. 登录页类型切换器 → ROTATE
2. `GET /captchaImage?type=ROTATE` → 新实例(旧实例不复用)
3. 用户拖动旋转滑块把图转正(容差放宽到 **±36°**,不要求精确)
4. 后续同 S11 第 4~8 步

#### 🔶 场景 S13:切换到文字点选

1. 类型切到 **WORD_IMAGE_CLICK**
2. `GET /captchaImage?type=WORD_IMAGE_CLICK` → 生成含汉字的图 + 提示文字(如"请依次点击:春 风 得 意")
3. 用户按顺序点击
4. 后续同 S11 第 4~8 步

#### 🔶 场景 S14:验证码失败与重试

1. 用户操作偏差 → `/captcha/check` 返回失败(如"验证码错误"/"验证失败")
2. 前端提示 + **自动重新 `GET /captchaImage`** 取新实例
3. 用户重试 → 正常

> ⚠️ **注意**:`matching()` 已消费旧实例缓存,**不能**用同一个 `uuid` 再试一次,必须重新生成。

#### 🔶 场景 S15:兜底链路(不经 `/captcha/check` 直接登录)

1. 某些集成场景(或 Redis 不可用)前端直接把 `captchaData` 随 `/login` 提交
2. 后端 `validateTianaiCaptcha` 读 Redis 未命中(或 Redis 异常 catch)
3. 走 `imageCaptchaApplication.matching(uuid, track)` **一次性校验**
4. 通过 → 继续密码校验;失败 → `CaptchaException` + 落 `sys_logininfor`

#### 🔶 场景 S16:凭证一次性(安全验证)

1. 用户完成 `/captcha/check` → 凭证写入
2. 用户登录 → 凭证被 **DEL**
3. **若密码输错**,用户重试登录时凭证已不存在 → 后端走 `matching()` → 但缓存已被消费 → **校验失败**
4. 前端需引导用户**重新过验证码**(这是**有意的安全设计**,防止同一凭证被用于暴力破解密码)

---

## 5. 总体需求

### 5.1 范围图(🔶 = 本分支改动;⬆️ = `springboot3` 继承)

```
                    ┌────────────────────────────────────────┐
                    │   平台能力(本仓库)                    │
                    │  ┌──────────┐  ┌───────────────────┐  │
                    │  │ PC Web   │  │ 移动 H5/小程序/App │  │
                    │  │ (Vue3)   │  │                   │  │
                    │  │ 🔶验证码 │  │ 🔶验证码          │  │
                    │  └────┬─────┘  └─────────┬─────────┘  │
                    │       │  HTTP/JSON       │            │
                    │  ┌────┴───────────────────┴─────────┐  │
                    │  │   Spring Boot 3.5.16 (⬆️ JDK 17) │  │
                    │  │  🔶 tianai-captcha 1.5.2         │  │
                    │  │     /captchaImage  /captcha/check │  │
                    │  │     /login(含 validateTianai)    │  │
                    │  └────┬─────────────────────────────┘  │
                    │       │  MyBatis-Plus 3.5.7            │
                    │  ┌────┴─────────────────────────────┐  │
                    │  │  PostgreSQL basicdb(34 表)      │  │
                    │  │  🔶 零变更                        │  │
                    │  └──────────────────────────────────┘  │
                    │  ┌──────────────────────────────────┐  │
                    │  │  Redis                           │  │
                    │  │  🔶 captcha:verified:<id> 120s   │  │
                    │  └──────────────────────────────────┘  │
                    └────────────────────────────────────────┘
```

### 5.2 核心规则总表

> **业务规则**与 `master` 一致(用户/角色/菜单/部门/岗位/字典/通知/定时任务/代码生成的 L1/L2 校验),详见 `docs/springboot3-需求文档.md` §5.2。下表为**本分支验证码相关规则**。

| 模块 | 关键字段 | L1 校验(字段级) | L2 校验(跨组件) |
|---|---|---|---|
| **验证码实例** | `type` / `id` | `type ∈ {SLIDER, ROTATE, WORD_IMAGE_CLICK}`;`CONCAT` 自动回退 `SLIDER` | `id` 必须存在于 tianai 缓存 |
| **轨迹数据** | `trackList` / `bgImageWidth` / `bgImageHeight` / `startTime` / `stopTime` | 全部必填;`trackList` 非空;宽高 > 0 | 必须在实例 TTL 内;`matching()` 通过 |
| **已校验凭证** | `captcha:verified:<id>` | — | TTL 120s 内;**命中后立即删除** |
| **LoginBody** | `username` / `password` / `uuid` / `captchaData` | `username` 非空;`password` RSA 密文;**`uuid` / `captchaData` 在验证码开启时必填** | `sys.account.captchaEnabled=true` 时才校验 |
| **总开关** | `sys.account.captchaEnabled` | `true` / `false` | `false` → 跳过 `validateTianaiCaptcha`(测试用) |

---

## 6. 功能需求

> **业务功能需求**(文件上传、用户/角色/菜单/部门/字典/通知/定时任务/代码生成、审核、权限点矩阵、数据权限)与 `master` **完全一致**,见 `docs/base-postgresql-vue3-需求文档.md` §6 / §8。
> **SB3 升级需求**(JDK 17 / Jakarta / Security 6 / springdoc / starter / 配置)已在 `springboot3` 交付,见 `docs/springboot3-需求文档.md` §6.9。
> 本节只展开 **§6.10 行为验证码专题**。

### 6.10 🔶 行为验证码需求(本分支核心)

#### 6.10.1 生成验证码 `GET /captchaImage?type=<type>`

| 字段 | 值 |
|---|---|
| 权限 | `permitAll`(`SecurityConfig` 放行 `/captchaImage` 与 `/captcha/**`) |
| 路径 | `GET /captchaImage` |
| Query | `type`(可选;不传则用 tianai 默认,当前实现默认 `SLIDER`) |
| 响应结构 | `{captchaOnOff, uuid, img, type, data: {...}}` |
| `uuid` 语义 | **验证码 id**(tianai 的 `captchaId`),不是 kaptcha 时代的随机串 |
| `img` 字段 | **固定 `null`**(行为验证码无单张图),保留仅为兼容旧前端 |
| `data` 内容 | 扁平化后的 tianai `ImageCaptchaVO`(**绝不下发 `expand` 等内部字段**) |
| 类型兜底 | `CONCAT` → 自动回退 `SLIDER`(不报错) |
| 总开关 | `captchaOnOff=false` 时前端应隐藏验证码区域 |

#### 6.10.2 即时校验 `POST /captcha/check`(主链路)

| 字段 | 值 |
|---|---|
| 权限 | `permitAll` |
| 路径 | `POST /captcha/check` |
| 请求体 | `{id: "<验证码id>", data: <ImageCaptchaTrack 或 JSON 字符串>}` |
| 入参容错 | `data` 支持 **Map** 与 **JSON 字符串**两种形态 |
| 行为 | ① 解析 `ImageCaptchaTrack` ② 校验必要字段 ③ `matching()` ④ 成功写 `captcha:verified:<id>=1`(120s) |
| 失败 | 返回 `AjaxResult.error(msg)`;**不写凭证** |
| Redis 不可用 | 返回成功但**不写凭证**;后续 `/login` 会走 `matching()` 兜底(见源码注释"redis unavailable: just return success") |
| ⚠️ 语义 | `matching()` **会消费** tianai 缓存;同一 `id` 只能校验一次 |

#### 6.10.3 登录主流程 `POST /login`(含验证码)

| 字段 | 值 |
|---|---|
| 权限 | `permitAll` |
| 请求体 `LoginBody` | `username` / `password`(RSA 密文)/ `code`(兼容占位)/ `uuid`(验证码 id)/ **`captchaData`**(轨迹 JSON) |
| 行为 | ① RSA 解密 → ② `validateTianaiCaptcha`(若开关为 true)→ ③ BCrypt → ④ `createToken` → ⑤ 异步记 `sys_logininfor` |
| `validateTianaiCaptcha` 逻辑 | **优先读 Redis 凭证**;命中 → **`DEL` 后跳过 `matching`**;未命中 / Redis 异常 → **`matching()` 兜底** |
| 失败 | 抛 `CaptchaException`;落 `sys_logininfor`(`status='1'`,`msg` 如「验证码错误」) |

#### 6.10.4 `LoginBody` 字段表

| 字段 | 类型 | 必填 | 校验 |
|---|---|---|---|
| `username` | String | Y | 非空,长度 ≤ 30 |
| `password` | String | Y | RSA 密文,服务端解密 |
| `code` | String | Y(占位) | 行为验证码模式下**不校验实际值**,仅占位(锁屏传 `LOCK_BYPASS`) |
| `uuid` | String | Y(验证码开启时) | 验证码 id;空 → `CaptchaException("验证码 id 缺失")` |
| **`captchaData`** | String | Y(验证码开启时) | `ImageCaptchaTrack` JSON;空 → `CaptchaException("验证码轨迹缺失")` |

#### 6.10.5 三种类型规格

| 类型 | 交互 | 模板来源 | 背景图 | 容差 |
|---|---|---|---|---|
| `SLIDER` | 拖动拼图块到缺口 | `template/slider_1`、`slider_2` | `resource/1.jpg`、`pic1~5.jpg`(6 张) | 0.02(默认) |
| `ROTATE` | 拖动旋转整图 | `template/rotate_1` | 同上 | **0.10(±36°)** |
| `WORD_IMAGE_CLICK` | 按顺序点汉字 | 程序化生成 | 同上 | 0.02(默认) |
| `CONCAT` | — | **已下线** | — | 自动回退 `SLIDER` |

#### 6.10.6 容差策略

| 类型 | tianai 默认 | 本分支 | 说明 |
|---|---|---|---|
| `SLIDER` | 0.02 | **0.02** | 600px 背景 → ±12px,合理 |
| `ROTATE` | 0.02 | **0.10** | 默认 ±7.2° 用户几乎不可能拖准;0.10 → ±36° |
| `WORD_IMAGE_CLICK` | 0.02 | **0.02** | 点选维度容差足够 |

> 实现:`TypeAwareImageCaptchaValidator extends SimpleImageCaptchaValidator`,重写 `doValidSliderCaptcha`,**仅当 `type=ROTATE` 且入参容差 < 0.10 时提升到 0.10**,其它类型透传。

#### 6.10.7 资源与字体要求(1.5.2 变更)

| 项 | 要求 |
|---|---|
| 模板路径 | `classpath:META-INF/cut-image/template` |
| 背景图路径 | `classpath:META-INF/cut-image/resource` |
| 背景图数量 | **6 张**(`1.jpg` / `pic1.jpg`~`pic5.jpg`) |
| 字体注入 | **1.5.2 变更**:字体作为 `ResourceStore` 中 `type="font"` 的资源注入,由 `FontCache` 监听 `onAddResource`;**不再使用** `getFontWrappers/setFontWrappers`(该 API 已移除) |
| 加载时机 | `TianaiCaptchaConfig`(`@Order(0)` + `@Primary ResourceStore`)在启动时加载 |
| 失败容忍 | 任一步失败仅 `log.warn`,**不阻断启动**;启动日志应见 3 条 `[tianai-captcha] ... loaded` |
| 扩充方式 | **丢图即生效**(放入 `resource/` 目录即可,无需改代码) |

#### 6.10.8 Redis 凭证规格

| 项 | 值 |
|---|---|
| Key | `captcha:verified:<验证码id>` |
| Value | `"1"` |
| TTL | **120 秒**(`VERIFIED_EXPIRE_SECONDS`) |
| 写入方 | `CaptchaCheckController.check()`(matching 通过时) |
| 读取方 | `SysLoginService.validateTianaiCaptcha()` |
| 消费语义 | **命中后立即 `DEL`(一次性)** —— 防同一凭证被复用爆破密码 |
| Redis 不可用 | 写入跳过(仅返回成功)/ 读取 catch 后降级 `matching()` |

#### 6.10.9 前端组件要求

| 端 | 组件 | 大小 | 要求 |
|---|---|---|---|
| PC `web-ui` | `src/components/TianaiCaptcha/index.vue` | 28.1 KB | 自实现(不走官方 web-sdk);3 类型切换;轨迹按反机器人规则构造(首条补原点 / y 微抖动 / 末段减速);**坐标用百分比定位**(避免首帧缩放比未就绪导致尺寸跳动) |
| 移动 `web-mp` | `src/components/tianai-captcha/tianai-captcha.vue` | 21.9 KB | 同上核心;适配 uni-app + uview-plus(`uni.request`、`up-` 组件) |

**双向绑定约定**(实测 `login.vue` 用法):

```vue
<TianaiCaptcha
  ref="captchaRef"
  v-model:captchaId="loginForm.uuid"
  v-model:captchaData="loginForm.captchaData"
  @success="onCaptchaSuccess" />
```

| Props / Events | 说明 |
|---|---|
| `v-model:captchaId` | 验证码 id(写入 `loginForm.uuid`) |
| `v-model:captchaData` | 轨迹 JSON(写入 `loginForm.captchaData`) |
| `@success` | 组件内部 `/captcha/check` 成功回调 |

#### 6.10.10 kaptcha 下线要求

| 项 | 口径 |
|---|---|
| 删除的类 | `CaptchaConfig.java`(kaptcha `Producer` / `ServletRegistrationBean`)、`KaptchaTextCreator.java`(文本生成器) |
| 源码残留 | **仅允许 2 处 javadoc 兼容说明**(`SysLoginService` 的 `code` 参数注释、`LoginBody.uuid` 注释) |
| pom 残留 | `kaptcha.version` / `kaptcha` 依赖**保留为死引用**(无 module 引用),便于回滚 |
| 验收命令 | `grep -r "kaptcha" basic-*/src --include=*.java` 应仅 2 行(均 javadoc) |
| 严禁 | **新增任何 kaptcha 引用** |

#### 6.10.11 与 SB3 基线的兼容要求

| 项 | 要求 |
|---|---|
| servlet 命名空间 | tianai 1.5.2 必须与 **jakarta.servlet** 兼容;不得引入任何 `javax.servlet` 依赖 |
| Security 6 放行 | `requestMatchers("/captchaImage", "/captcha/**")` 必须 permitAll |
| springdoc | `CaptchaController` / `CaptchaCheckController` 需在 `/v3/api-docs` 中可见(可加 `@Tag`) |
| 与 yauaa 无关 | UA 解析在登录成功后,不影响验证码链路 |

### 6.11 其它功能需求(与 `springboot3` 一致)

> 文件上传下载、L1/L2 校验、错误明细、各业务模块字段、审计、权限点矩阵、数据权限 —— 全部与 `master` / `springboot3` 一致,不再重复。

---

## 7. 非功能需求

| 属性 | 目标 |
|---|---|
| 一致性 | 事务边界在 Service;异步日志不污染主链路 |
| 可靠性 | 验证码资源加载失败**不阻断启动**;Redis 不可用**降级 `matching()`** 不阻断登录 |
| 性能 | `GET /captchaImage` < **200ms**;`POST /captcha/check` < **300ms**;`POST /login` < **500ms**(含验证码 + BCrypt) |
| 审计合规 | 90 天登录日志可查询、可导出 |
| 安全 | RSA 加密密码;BCrypt;JWT 30min;**行为验证码抗 OCR**;**凭证一次性防重放**;XSS/重复提交/限流/CORS |
| 可用性 | 单实例 99.9% |
| 部署 | 单一可执行 jar;运行时 **JDK 17** |
| **🔶 验证码可用性** | 3 类型均可在 PC + 移动端完成;ROTATE 用 ±36° 容差,普通用户可一次通过 |
| **🔶 启动可观测** | 启动日志含 3 条 `[tianai-captcha]`(templates / backgrounds / fonts) |
| **🔶 资源可扩展** | 背景图丢入 `resource/` 即生效,无需改代码、无需重启配置 |

---

## 8. 权限与角色

> RBAC 权限点矩阵与数据权限 **与 `master` 完全一致**(15 个模块 × 增删改查导出导入),详见 `docs/base-postgresql-vue3-需求文档.md` §8。本分支**不新增任何权限点、不改任何菜单数据**。
>
> **🔶 验证码接口的访问控制**:
>
> | 接口 | 放行方式 | 说明 |
> |---|---|---|
> | `GET /captchaImage` | `permitAll` | 登录前必须可匿名访问 |
> | `POST /captcha/check` | `permitAll` | 同上 |
> | `POST /login` | `permitAll` | 同上 |
>
> ⚠️ 三个接口均匿名可访问,**需靠限流防刷**(M5 候选);当前未做专门限流。

---

## 9. 边界与不做清单

| 项 | 说明 |
|---|---|
| 前端 TS 化 | 走 `springboot3_TypeScript` |
| 业务流程 | 走 `springboot3-extend-flowable` |
| JDK 21 / SB 4 / Jackson 3 / Security 7 | 走 `springboot4` |
| 微服务化 / K8s / 灰度 | 不做 |
| SSO 联邦 | 不做(后续 keycloak) |
| **数据库变更** | **零变更** |
| **验证码命中规则**(失败 N 次切难) | 不做,列 M5 |
| **锁屏免验证码** | 不做(R31) |
| **第三方验证码服务** | 不做(hCaptcha / geetest 依赖外网) |
| **tianai 2.x 升级** | 不做(1.5.2 够用) |
| **验证码无障碍适配**(语音/图形替代) | **未做**(潜在合规缺口,见 A-19) |
| 移动端完整业务 | 仅登录 + 通知 + 个人中心 |
| 国际化词条 | 已引入,词条待补 |
| 代码生成器 SB3 模板 | 继承缺陷(R20),仍未适配 |

---

## 10. 验收标准

> **AC-01~AC-24 继承自 `master` / `springboot3`**(编译/启动/表数量/登录/CRUD/锁屏/代码生成/定时任务/导出/XSS/重复提交/Swagger/jakarta 残留/springdoc 注解/starter 变体/配置键/前端零改动等),见 `docs/springboot3-需求文档.md` §10。下表为本分支**新增验证码专项 AC**。

| # | 验收项 | 验收方法 |
|---|---|---|
| AC-01~AC-24 | 见 `springboot3` PRD(其中 AC-05/06/07 的登录需用行为验证码完成) | 同左 |
| **AC-25** | 启动日志含 3 条 `[tianai-captcha]`(templates / backgrounds / fonts loaded) | 终端 |
| **AC-26** | `GET /captchaImage?type=SLIDER` 返回 `{captchaOnOff, uuid, type:"SLIDER", data}`,`img` 为 `null` | 浏览器 / `curl` |
| **AC-27** | `GET /captchaImage?type=ROTATE` 可正常生成 | 同上 |
| **AC-28** | `GET /captchaImage?type=WORD_IMAGE_CLICK` 可正常生成(含汉字) | 同上 |
| **AC-29** | `GET /captchaImage?type=CONCAT` **自动回退为 SLIDER**(不报错) | 同上,检查返回 `type` |
| **AC-30** | PC 端 SLIDER 完成 `/captcha/check` → 再 `/login` 成功 | 浏览器 |
| **AC-31** | PC 端 ROTATE 登录成功(±36° 容差生效) | 浏览器 |
| **AC-32** | PC 端 WORD_IMAGE_CLICK 登录成功 | 浏览器 |
| **AC-33** | 移动端 H5 三种类型均可登录 | 浏览器 / 小程序 |
| **AC-34** | 登录页可切换类型,切换后重新生成实例且无报错 | 浏览器 |
| **AC-35** | **凭证一次性**:完成 `/captcha/check` → 密码输错 → 再登录时凭证已失效,提示需重新验证码 | 浏览器 + Redis 观察 |
| **AC-36** | **Redis 不可用**:停 Redis → `/login` 仍可走 `matching()` 兜底登录(日志有降级告警) | 模拟 Redis 宕机 |
| **AC-37** | `sys.account.captchaEnabled=false` 时跳过验证码可直接登录 | 改 `sys_config` |
| **AC-38** | `grep -r "kaptcha" basic-*/src --include=*.java` **仅 2 行**(均 javadoc) | 终端 |
| **AC-39** | `KaptchaTextCreator.java` / `CaptchaConfig.java` **已删除** | 文件检查 |
| **AC-40** | `/v3/api-docs` 中可见 `/captchaImage` 与 `/captcha/check` | 浏览器 |
| **AC-41** | 登录失败后 `sys_logininfor` 有记录(`status='1'`,`msg` 含"验证码") | DB 查询 |
| **AC-42** | `mvn dependency:tree` 中 tianai 为 **1.5.2**,且无 `javax.servlet` 传递依赖 | 终端 |

---

## 附录A 待确认问题清单

| 编号 | 问题 | 建议默认 | 阻塞 |
|---|---|---|---|
| A-1~A-13 | 继承项(移动端动态路由 / 锁屏 / refresh token / 日志保留 / 多端互踢 / 富文本 / 主子表 / deprecation / 生成器 SB3 模板 / actuator / druid IP 白名单 / virtual threads / 校验分组) | 见 `springboot3` PRD | 否 |
| **A-14** | **默认验证码类型是哪种?** | 当前 `SLIDER`(最易用);可按业务切换 | 否 |
| **A-15** | **凭证 TTL 120s 是否合适?** | 调试友好;生产可缩到 30s(更安全) | 否 |
| **A-16** | **是否强制只允许 SLIDER(禁用另两种)?** | 否,三种都开;若担心 ROTATE 体验可只开 SLIDER + WORD_IMAGE_CLICK | 否 |
| **A-17** | **锁屏解锁是否要免验证码?** | 当前需验证码(R31);若体验差可对 `code=LOCK_BYPASS` 短路(需评估) | 否 |
| **A-18** | **`/captcha/**` 是否要加限流?** | 建议加(防被当免费图片服务),列 M5 | 否 |
| **A-19** | **是否需要无障碍替代方案**(语音验证码 / 图形替代)? | **当前缺失**,若面向公共用户需评估合规风险 | **待定** |
| **A-20** | **失败日志是否要区分 tianai vs 旧 kaptcha 数据?** | 不区分,统一"验证码错误" | 否 |
| **A-21** | **是否升级 tianai 2.x?** | 暂不;需先验证 SB 3 兼容性与字体注入 API | 否 |

---

## 附录B 业务回填模板

```markdown
### 模块: <名称>
| 字段 | 类型 | 必填 | 校验 |
|---|---|---|---|
| ... | ... | ... | ... |
```

---

## 附录C 验证码交付检查清单

- [ ] `tianai-captcha-springboot-starter` = **1.5.2**
- [ ] `TianaiCaptchaConfig` 存在(`@Order(0)` + `@Primary ResourceStore`)
- [ ] 字体按 **1.5.2 方式**注入(`type="font"` 资源),未使用已移除的 `getFontWrappers`
- [ ] `TypeAwareImageCaptchaValidator` 存在,ROTATE 容差 = **0.10**
- [ ] `CaptchaController` 支持 3 类型 + `CONCAT` 回退
- [ ] `CaptchaCheckController` 存在,`POST /captcha/check` 写凭证 120s
- [ ] `SysLoginService.validateTianaiCaptcha` 优先读凭证、**命中即 DEL**、异常降级 `matching()`
- [ ] `LoginBody.captchaData` 字段存在
- [ ] `SecurityConfig` 放行 `/captchaImage` + `/captcha/**`
- [ ] 6 张背景图 + 3 套模板就位于 `META-INF/cut-image/`
- [ ] `web-ui/src/components/TianaiCaptcha/index.vue` 存在并接入 `login.vue`
- [ ] `web-mp/src/components/tianai-captcha/tianai-captcha.vue` 存在并接入登录页
- [ ] `KaptchaTextCreator.java` / `CaptchaConfig.java` 已删除
- [ ] `grep -r "kaptcha" basic-*/src` 仅 2 处 javadoc
- [ ] 启动日志 3 条 `[tianai-captcha]`
- [ ] PC + 移动端 3 类型登录全部通过
- [ ] 凭证一次性验证通过(AC-35)

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `springboot3` PRD(v0.1)为基线(G1~G12 / AC-01~AC-24 / A-1~A-13 继承),新增 G13~G18 验证码目标、§6.10 行为验证码需求(11 小节:2 接口 + 字段表 + 3 类型规格 + 容差 + 1.5.2 字体变更 + Redis 凭证 + 前端组件 + kaptcha 下线 + SB3 兼容)、场景 S11~S16、AC-25~AC-42、A-14~A-21、附录C 检查清单 |
