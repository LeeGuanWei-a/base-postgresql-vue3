# 基础平台(base-postgresql-vue3) 需求文档(PRD) — `springboot2-tianai-captcha` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot2-tianai-captcha`** 分支
> 前置依赖:《架构方案》v0.1
> 本分支相对 `master` 的**唯一功能级差异** = 登录链路使用 **tianai-captcha 行为验证码**(滑动 / 旋转 / 文字点选 3 种类型可切换),取代 kaptcha 字符验证码。其余需求与 `master` 一致,本文档不重复,仅在「六、功能需求」中展开行为验证码专题。

---

## 1. 文档信息与版本

- 当前版本:**v0.1** — 初稿
- 变更历史

| 版本 | 日期 | 变更 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:覆盖 tianai-captcha 行为验证码专题(3 类型 / 2 接口 / 2 前端组件 / 容差策略 / 凭证 120s / 旁路校验);其余模块(用户/角色/菜单/部门/字典/参数/通知/定时任务/代码生成/锁屏)与 `master` 一致 |

---

## 2. 背景与目标

### 2.1 为什么做

`master` 分支默认使用 kaptcha 字符验证码,安全强度有限(OCR 即可破解),且体验差。本分支把字符验证码整体替换为 **tianai-captcha 行为验证码**:

- **滑动(SLIDER)**:拖动拼图到缺口位置 — 最常用
- **旋转(ROTATE)**:拖动滑块旋转图片到正向 — 中等
- **文字点选(WORD_IMAGE_CLICK)**:按顺序点击图中的汉字 — 最强抗 OCR

业务可在登录页切换任一类型,后端通过 `LoginBody.captchaData` 一个字段统一承载轨迹数据。

### 2.2 目标清单

| 目标 | 成功标准 |
|---|---|
| G1 替换 kaptcha 字符验证码为 tianai-captcha 行为验证码 | 登录链路全部走 tianai,无 kaptcha 引用 |
| G2 支持 3 种类型可切换 | 登录页 UI 切换 SLIDER/ROTATE/WORD_IMAGE_CLICK,后端 1 个字段适配 |
| G3 ROTATE 容差合理化 | 0.10(±36°),不是默认 0.02(±7.2°) |
| G4 保持 `master` 其它能力不破坏 | 用户/角色/菜单/锁屏/通知/定时任务/代码生成/移动端 全部沿用 |
| G5 调试/独立校验场景可旁路 | `/captcha/check` 显式校验,写 Redis 凭证 120s,供后续 `/login` 跳过 matching |
| G6 PC + 移动双端一致 | web-ui + web-mp 各 1 组件,统一走 `/captchaImage` + `/login` |

### 2.3 非目标(本分支不做)

- 业务流程引擎(走 `springboot2-extend-flowable` 分支)
- 多租户 / SaaS 化
- SSO 联邦
- Spring Boot 2 → 3/4 升级
- 验证码命中规则升级(同 IP 失败 N 次切难类型 — M2 候选)
- 验证码 UI 主题完全统一(M3 候选)

---

## 3. 术语表

| 术语 | 含义 |
|---|---|
| 行为验证码 | 区别于字符验证码,通过用户行为(拖动 / 旋转 / 点击)完成验证,OCR 难以自动化 |
| tianai-captcha | 开源 Java 验证码库,本仓库使用其 1.5.0 `springboot-starter` |
| SLIDER | 滑动验证码(本仓库支持) |
| ROTATE | 旋转验证码(本仓库支持) |
| WORD_IMAGE_CLICK | 文字点选验证码(本仓库支持) |
| CONCAT | 滑动还原验证码(已下线,传入自动回退 SLIDER) |
| ImageCaptchaTrack | tianai-captcha 轨迹数据结构(包含 `trackList` 滑动点序列 / `bgImageWidth/Height` / `startTime/stopTime`) |
| `LoginBody.captchaData` | ImageCaptchaTrack 的 JSON 字符串,4 种类型都用该字段 |
| `captcha:verified:<id>` | Redis 凭证,表示该验证码 id 已被 `/captcha/check` 显式校验通过;`/login` 优先读,命中即跳过 matching |
| `matching()` | tianai-captcha 内置方法,执行校验;**会消费验证码缓存**(同一 id 只能调一次) |
| ROTATE 容差 | tianai 默认 0.02(±7.2°),用户体验差;本分支用 `TypeAwareImageCaptchaValidator` 放宽到 0.10(±36°) |

---

## 4. 用户角色与使用场景

### 4.1 角色

| 角色 | 关键动作 | 频率 |
|---|---|---|
| **超级管理员(`admin`)** | 全部功能,含 `sys_config` 中 `sys.account.captchaEnabled` 开关 | 高 |
| **业务管理员** | 受限于「数据权限」的 CRUD | 中 |
| **审计员** | 仅看 `sys_oper_log` / `sys_logininfor`,无写操作 | 低 |
| **业务运营(PC)** | 登录 / 查看通知 | 高 |
| **业务运营(移动)** | 登录 / 查看通知 / 个人中心 | 中 |
| **开发者** | 调整验证码类型 / 容差 / 凭证 TTL | 低 |

### 4.2 关键场景走查(行为验证码专题)

#### 场景 S1:首次登录(PC 滑动)

1. 打开登录页 → 后端 `GET /captchaImage?type=SLIDER` 返回 `{uuid, type: "SLIDER", data: {id, 背景图, 模板图, 滑块图}, ...}`
2. 前端渲染滑动组件;用户拖动滑块到缺口 → 采集轨迹
3. 提交登录 → `POST /login {username, password, code, uuid, captchaData}`(RSA 加密 password)
4. 后端 `SysLoginService.login` → `validateTianaiCaptcha(username, uuid, captchaData)`:
   - 查 Redis `captcha:verified:<uuid>` → 命中,跳过 matching,直接走密码校验
   - 未命中 → `imageCaptchaApplication.matching(uuid, track)` 校验
5. 校验通过 → `TokenService.createToken` → 返回 `{token}`

#### 场景 S2:切换到旋转验证码

1. 登录页 UI 切换类型到 ROTATE
2. `GET /captchaImage?type=ROTATE` → 后端生成 ROTATE 实例
3. 用户旋转图片到正向
4. 提交 → 同 S1 链路
5. 后端 `TypeAwareImageCaptchaValidator` 自动用 0.10 容差(±36°)

#### 场景 S3:切换到文字点选

1. 登录页 UI 切换到 WORD_IMAGE_CLICK
2. `GET /captchaImage?type=WORD_IMAGE_CLICK` → 后端生成点选实例
3. 用户按顺序点击图中汉字 → 采集点击序列
4. 提交 → 同 S1 链路(后端字段复用,无需切换)

#### 场景 S4:调试 / 独立校验(旁路)

1. 前端先调 `POST /captcha/check {id, data}` → `imageCaptchaApplication.matching()` → 写 `captcha:verified:<id>`(120s)
2. 再调 `POST /login` → `validateTianaiCaptcha` 读 Redis 凭证命中 → 跳过 matching
3. 适合:前端想在 UI 上拆出"独立验证 + 登录"两步

#### 场景 S5:验证码失败重试

1. 用户拖错位置 → 后端 `matching` 失败 → `CaptchaException`
2. 前端弹窗"验证码错误,请重试" → 自动调 `/captchaImage` 拿新实例
3. 用户重试 → 链路正常

#### 场景 S6:Redis 不可用

1. `SysLoginService.validateTianaiCaptcha` 读 Redis 抛异常 → catch 后降级
2. 直接走 `imageCaptchaApplication.matching()` 兜底
3. 功能不中断,只是 `/captcha/check` 旁路无效

---

## 5. 总体需求

### 5.1 范围图(本分支独有改动以 **🔸** 标出)

```
                       ┌────────────────────────────┐
                       │  本分支新增                 │
                       │  ┌──────────────────────┐  │
                       │  │  行为验证码           │  │
                       │  │  (tianai-captcha)    │  │
                       │  │  - 3 种类型可切换    │  │
                       │  │  - 2 个 REST 接口    │  │
                       │  │  - 2 套前端组件      │  │
                       │  │  - Redis 凭证 120s   │  │
                       │  └──────────┬───────────┘  │
                       │             │              │
                       │  ┌──────────┴───────────┐  │
                       │  │  basic-framework     │  │
                       │  │  + basic-admin-api   │  │
                       │  │  + basic-common      │  │
                       │  │  + 7 模块不变        │  │
                       │  └──────────┬───────────┘  │
                       │             │ schema=basicdb (34 张表)
                       │  ┌──────────┴───────────┐  │
                       │  │  PostgreSQL + Redis  │  │
                       │  └──────────────────────┘  │
                       └────────────────────────────┘
```

### 5.2 核心规则总表(本分支独有)

| 模块 | 关键字段 | L1 校验 | L2 校验 |
|---|---|---|---|
| **行为验证码实例** | `id` / `type` / `data` / `expireTime` | `id` 非空;`type ∈ {SLIDER, ROTATE, WORD_IMAGE_CLICK}`(`CONCAT` 自动回退 `SLIDER`) | `id` 必须在 tianai-captcha 缓存中存在 |
| **轨迹数据** | `trackList` / `bgImageWidth/Height` / `startTime/stopTime` | 必填、轨迹点非空、宽高 > 0、起止时间合法 | 必须在 `captcha:verified:<id>` 凭证 TTL 内 / `matching()` 通过 |
| **已校验凭证** | `captcha:verified:<id>` | 必填 | 120s TTL 内;`/login` 命中即跳过 matching |
| **LoginBody.captchaData** | 上述轨迹数据的 JSON 字符串 | 必填、JSON 合法 | 4 种类型都用该字段(类型由 `type` 决定) |
| `sys_config.sys.account.captchaEnabled` | `true` / `false` | boolean | `true` → 走验证码;`false` → 跳过(测试用) |

---

## 6. 功能需求

> **非验证码模块**(用户/角色/菜单/部门/字典/参数/通知/定时任务/代码生成/锁屏/操作日志)完全沿用 `master`,本节不重复。详见 `docs/base-postgresql-vue3-需求文档.md`(在 `master` 分支生成)。

### 6.1 行为验证码专题

#### 6.1.1 生成验证码 `GET /captchaImage?type=<type>`

| 字段 | 值 |
|---|---|
| 权限 | `anonymous`(`SecurityConfig` 显式放行) |
| 路径 | `/captchaImage` |
| Query | `type`(可选,默认 `application.yml` 中 `captcha.default-type`) |
| 响应 | `{captchaOnOff, uuid, img, type, data: {id, 背景图, 模板图, 滑块图, ...}}` |
| 类型 | `SLIDER` / `ROTATE` / `WORD_IMAGE_CLICK`;`CONCAT` 自动回退 `SLIDER` |

#### 6.1.2 即时校验 `POST /captcha/check`(旁路)

| 字段 | 值 |
|---|---|
| 权限 | `anonymous` |
| 路径 | `/captcha/check` |
| 请求体 | `{id: "验证码id", data: <ImageCaptchaTrack 或其 JSON 字符串>}` |
| 行为 | 内部 `imageCaptchaApplication.matching(id, track)` → 写 Redis `captcha:verified:<id>`(120s) |
| 适用 | 调试 / 独立校验场景;**主流程不需要**(主流程在 `/login` 内部 matching) |
| 注意 | `matching()` 会**消费**验证码缓存;同一 id 只能匹配 1 次 |

#### 6.1.3 登录主流程 `POST /login`(已含验证码)

| 字段 | 值 |
|---|---|
| 权限 | `anonymous` |
| 路径 | `/login` |
| 请求体 `LoginBody` | `username` / `password`(RSA 密文)/ `code`(兼容占位)/ `uuid`(验证码 id)/ **`captchaData`**(轨迹 JSON) |
| 行为 | ① RSA 解密 → ② `validateTianaiCaptcha` → ③ BCrypt 比对 → ④ `TokenService.createToken` |
| 失败 | 验证码错 / 密码错 / 账号停用,均抛对应异常,前端 toast |

#### 6.1.4 `LoginBody` 字段表

| 字段 | 类型 | 必填 | 校验 |
|---|---|---|---|
| `username` | String | Y | 非空,长度 ≤ 30 |
| `password` | String | Y | RSA 密文,服务端解密 |
| `code` | String | Y(兼容占位) | 行为验证码模式下不校验,仅占位 |
| `uuid` | String | Y | 验证码 id |
| **`captchaData`** | String | Y | ImageCaptchaTrack 的 JSON 字符串;`trackList` / `bgImageWidth/Height` / `startTime/stopTime` 均非空 |

#### 6.1.5 行为验证码类型

| 类型 | 含义 | 模板 | 背景图来源 |
|---|---|---|---|
| `SLIDER` | 滑动拼图 | tianai-captcha starter 内置 | `classpath:META-INF/cut-image/resource/1.jpg` 或 `pic1.jpg` |
| `ROTATE` | 旋转 | tianai-captcha starter 内置 | 同上 |
| `WORD_IMAGE_CLICK` | 文字点选 | 程序化生成,需中文字体 | starter 自动加载 |
| `CONCAT` | 滑动还原 | **已下线** | 传入自动回退 `SLIDER` |

#### 6.1.6 容差策略

| 类型 | 默认容差 | 本仓库容差 | 说明 |
|---|---|---|---|
| `SLIDER` | 0.02(±12px @ 600px 背景) | 0.02(沿用) | 默认即可 |
| `ROTATE` | 0.02(±7.2°) | **0.10(±36°)** | `TypeAwareImageCaptchaValidator` 放宽 |
| `WORD_IMAGE_CLICK` | 0.02(±12px) | 0.02(沿用) | 默认即可 |

#### 6.1.7 总开关 `sys.account.captchaEnabled`

- KV 存 `sys_config` 表(沿用 RuoYi 机制)
- 值:`true`(默认)/ `false`(测试用,关闭后 `/login` 不校验验证码)
- 由 `ISysConfigService.selectCaptchaOnOff()` 读取

#### 6.1.8 前端组件

| 端 | 组件 | 说明 |
|---|---|---|
| PC `web-ui` | `src/components/TianaiCaptcha/index.vue` | 自实现,不走官方 web-sdk;支持 3 种类型切换;轨迹构造遵循 tianai 反机器人规则(首条补原点 / y 微抖动 / 末段减速);坐标用百分比避免缩放跳动 |
| 移动 `web-mp` | `src/components/tianai-captcha/tianai-captcha.vue` | 同上,适配 uni-app + uview-plus |

### 6.2 跨业务校验(L2)

| 校验项 | 位置 | 失败动作 |
|---|---|---|
| 验证码 id 必须存在 | `CaptchaController.generate` / `imageCaptchaApplication.generate` | 抛 `CaptchaException("user.jcaptcha.expire")` |
| 轨迹数据非空 | `SysLoginService.validateTianaiCaptcha` | 抛 `CaptchaException("user.jcaptcha.error")` |
| matching 失败 | `imageCaptchaApplication.matching` 返回非 `success` | 抛 `CaptchaException` + 落 `sys_logininfor.status='1'` |
| Redis 凭证命中(可选) | `validateTianaiCaptcha` 查 `captcha:verified:<id>` | 命中即跳过 matching;未命中 / 抛异常降级到 matching |

### 6.3 状态机

| 对象 | 状态 | 流转 |
|---|---|---|
| 验证码实例 | `已生成` → `已校验(凭证)` → `已消费(matching销毁)` | 凭证 120s TTL |
| 验证码类型 | `SLIDER` / `ROTATE` / `WORD_IMAGE_CLICK` | 用户切换 |

### 6.4 审计

- 登录成功/失败 → `sys_logininfor`(`msg` 不区分 tianai vs kaptcha,统一"验证码错误")
- 写操作 → `sys_oper_log`(`@Log` 注解)
- 验证码失败不入 `sys_oper_log`(只入 `sys_logininfor`)

---

## 7. 非功能需求

| 属性 | 目标 |
|---|---|
| 一致性 | 关系型事务;`/login` 内部 matching 与 `TokenService.createToken` 在同事务 |
| 可靠性 | Redis 不可用 → 降级到 matching(不阻断) |
| 性能 | `/login` 链路 < 500ms(含 matching);`/captchaImage` < 200ms |
| 审计合规 | 90 天登录日志可查询;`sys_logininfor` 不可关闭 |
| 安全 | RSA 加密登录密码;BCrypt 散列;JWT 30min;行为验证码抗 OCR;凭证 120s 防重放 |
| 可用性 | 单实例 99.9%;Redis 故障自动降级 |
| 部署 | 单一可执行 jar |

---

## 8. 权限与角色

> 沿用 `master` 的 5×15 权限矩阵(详见 `docs/base-postgresql-vue3-需求文档.md` §8)。本分支无新增权限点。
>
> 验证码接口(`/captchaImage` / `/captcha/check`)`SecurityConfig` 显式 `anonymous()` 放行,无需权限。

---

## 9. 边界与不做清单

| 项 | 说明 |
|---|---|
| 业务流程 | 走 `springboot2-extend-flowable` 分支 |
| 多租户 | 不做 |
| SSO 联邦 | 不做 |
| SB 2 → 3 升级 | 走 `springboot3` / `springboot3-tianai-captcha` 分支 |
| TS 化 | 走 `springboot2_TypeScript` 分支 |
| 移动端完整业务 | master 仅做登录 + 通知 + 个人中心,业务流程不做 |
| 验证码命中规则升级 | M2 候选,同 IP 失败 N 次切难类型 |
| kaptcha 字符验证码 | **已下线**;`pom.xml` 留死引用仅为回滚,**严禁新增引用** |

---

## 10. 验收标准

| # | 验收项 | 验收方法 |
|---|---|---|
| AC-01 | `mvn clean compile` 0 ERROR(7 个子模块) | 终端 |
| AC-02 | `mvn spring-boot:run` 启动成功,日志含 `[tianai-captcha] ... loaded` | 终端 |
| AC-03 | 7 个子模块 `parent.version` 一致为 `3.8.2` | `mvn validate` |
| AC-04 | `psql -f file-sql/basicdb-vue3.sql` 恢复 34 张表 | 终端 |
| AC-05 | admin/admin123 PC 端滑动验证码登录成功 | 浏览器 |
| AC-06 | admin/admin123 PC 端**旋转**验证码登录成功 | 浏览器 |
| AC-07 | admin/admin123 PC 端**文字点选**验证码登录成功 | 浏览器 |
| AC-08 | admin/admin123 移动端 H5 登录成功 | 浏览器 |
| AC-09 | 锁屏 → 输入密码 → 解锁成功(沿用 master) | 浏览器 |
| AC-10 | 三种验证码类型可在 UI 切换,无报错 | 浏览器 |
| AC-11 | ROTATE 容差 ±36°(0.10)通过, ±7.2°(0.02)失败 | 单元测试或手工 |
| AC-12 | `/captcha/check` 旁路:先调成功 → 再调 `/login` 跳过 matching | 浏览器开发者工具 |
| AC-13 | Redis 不可用时 `/login` 仍能走 matching 兜底 | 模拟 Redis 宕机 |
| AC-14 | `grep -r "kaptcha" basic-*/src` 0 命中(除 `pom.xml` 死引用) | 终端 |
| AC-15 | 操作日志可导出 Excel(沿用 master) | 浏览器 |

---

## 附录A 待确认问题清单

| 编号 | 问题 | 建议默认 | 阻塞 |
|---|---|---|---|
| A-1 | 移动端是否需要 ROTATE 类型? | 已支持;按用户反馈再优化 | 否 |
| A-2 | 凭证 TTL 120s 是否过短/过长? | 120s 适合调试,生产可改 30s | 否 |
| A-3 | `sys_logininfor.msg` 是否区分 tianai vs kaptcha? | 不区分,统一"验证码错误" | 否 |
| A-4 | 验证码背景图只有 2 张,生产是否扩充? | 当前 demo 够用,生产建议 6+ | 否 |
| A-5 | 是否需要"同 IP 失败 N 次切难类型"? | M2 排期 | 否 |
| A-6 | tianai-captcha 1.5.0 → 2.x 升级? | 锁版本,待 SB 2.5 兼容版本发布 | 否 |
| A-7 | `pom.xml` kaptcha 死引用是否彻底删? | 保留(快速回滚),严格 grep 校验 | 否 |

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:覆盖 3 种验证码类型 / 2 接口 / 2 前端组件 / 容差策略 / 凭证 / 旁路 / 7 待定项 / 15 AC |
