# Basic Admin (Spring Boot 4.1.1 + Vue 3 + Element Plus + TypeScript)

基于若依 RuoYi-Vue 3.9.2 改造,与 `springboot4` 分支**唯一区别**:**前端两端(`web-ui` / `web-mp`)从 JavaScript 升级到 TypeScript**;后端完全保持 `springboot4` 的 SB 4.1.1 / JDK 21 / Security 7 / Jackson 3 / Jakarta 6 升级。

> 当前分支:`springboot4_TypeScript` ——「**springboot4 + 前端全量 TypeScript 化**」(仓库最完整栈,7 条分支里技术栈最新最全)。
> 仓库另有 `master` / `master-extend-flowable` / `springboot2_TypeScript` / `springboot3` / `springboot3_TypeScript` / `springboot4` 等分支,本分支**只描述 springboot4_TypeScript**。

---

## 前端说明

本分支前端**两端都是 TypeScript 版**(区别于 `springboot4` 的 JS 版):

### 1.1 PC 管理端 `web-ui/`

- 框架:Vue 3.5.26 + Element Plus 2.13.1
- 构建:Vite 6.4.1
- 语言:**TypeScript 5.6.3**(`tsconfig.json` 存在,`vue-tsc: 2.1.10` 做类型检查)
- 配置:`@vue/tsconfig: 0.5.1` 作为 tsconfig 基础
- 自动导入:`auto-imports.d.ts` / `components.d.ts` / `global.d.ts`
- API 层:`src/api/**` 全部从 `.js` → `.ts`,并新增 `src/types/api/**.ts` 全量类型定义
- vite 配置:`vite.config.ts`(TS 版,跟 `springboot4` 原 `vite.config.js` 中文乱码版不同)

> JS 版在 `springboot3` / `springboot4` 分支,本分支**未涉及**。

### 1.2 移动端 `web-mp/`(本分支新增,与 `springboot4` 一致,但额外做 TS 化)

- 框架:**uni-app 3.0 (alpha)** + Vue 3.4.21(组合式 API `<script setup lang="ts">`)
- UI:**uview-plus** 3.3.62(uView 的 Vue3 版本,组件名带 `up-` 前缀)
- 语言:**TypeScript ^5.4.5**(`tsconfig.json` 已配,`vue-tsc ^2.0.19` 做类型检查)
- 构建:Vite 5.2.8
- 一份代码同时跑 **H5 / 微信小程序 / Android / iOS / 其它小程序**
- 状态管理:Pinia 2.0.36
- 加密:jsencrypt(对接后端 RSA 登录)

> 详细说明见下文**「十一、移动端 `web-mp`」** 章节。

---

## 一、当前后端状态(基于 `git show HEAD:pom.xml`)

> 本分支**后端与 `springboot4` 完全一致**:Spring Boot 4.1.1 + JDK 21 + Spring 7 + Security 7 + Jackson 3 + Jakarta 6 + springdoc 3.1。

| 组件 | 版本 | 与 master 的差异 |
|---|---|---|
| **JDK** | **21** | ★★ 由 11 升到 21 |
| Spring Boot | **4.1.1** | ★★ 由 2.5.14 升到 4.1.1 |
| Spring Framework | **7.0.9** | ★★ 由 5.3.x 升到 7.x |
| Spring Security | **7.1.1** | ★★ 由 5.5.x 升到 7.x(严格化,`getAuthorities()` 不能返回 null) |
| Spring Data Redis | **4.1.1** | |
| MyBatis-Plus | **3.5.17** | + `mybatis-plus-spring-boot4-starter` |
| MyBatis-Spring-Boot | **4.1.0** | ★★ starter 名带 `-4-` |
| PageHelper starter | **4.1.1** | |
| Druid | **1.2.28** | starter `druid-spring-boot-4-starter`,包名 `boot4.autoconfigure` |
| Springdoc-openapi | **3.1.1** | ★ 由 springfox 3.0.0 升到 3.1.1 |
| Hutool | hutool-core 5.8.27 | |
| yauaa | 8.2.0 | |
| Lombok | 1.18.46 | |
| **Jackson** | **3.1.5** (`tools.jackson.*`,全包名换) | ★★ **本次最大坑** |
| jakarta.servlet | **6.1.0** | ★★ javax.servlet → jakarta.servlet |
| fastjson2 | **未引入** | (若依原版用,我们不用,改 Jackson 3) |
| Quartz | **spring-boot-starter-quartz**(自动装配 `SchedulerFactoryBean`) | ★ 取代手写的 `quartz:2.5.2` 依赖 |

> 完整的 SB 2 → SB 3 → SB 4 升级细节(Jackson 3 适配 / Redis 序列化 / 循环依赖等)
> 与 `springboot4` 分支**完全相同**,本 README 不重复,见 `springboot4` 分支的对应章节。

---

## 二、本分支是什么

`springboot4_TypeScript` 分支以 **`springboot4` 分支**为基线,只做了一件事:

> **复用 `springboot3_TypeScript` 分支的前端 TS 化成果**(整 `web-ui` 目录替换),
> 把前端两端(`web-ui` / `web-mp`)都从 JavaScript 升级到 TypeScript;
> 后端、所有依赖、数据库脚本**完全保持 `springboot4` 的状态**。

具体而言:

1. **`web-ui` 整目录从 `springboot3_TypeScript` 替换进来**(~197 个文件改动)
2. **`web-ui/src/api/**` 全量 `.js` → `.ts`**:login / menu / monitor.* / system.* / tool.* 共 18+ 个 API 文件
3. **新增 `web-ui/src/types/` 目录**:全量 TypeScript 类型定义(API 参数 / 返回 / 通用 R / AjaxResult / TableDataInfo 等)
4. **新增 `web-ui/src/utils/auth.ts`**:从原 `.js` 改写并加类型
5. **`web-ui/package.json` 新增 TS 工具链**:`typescript@5.6.3` / `vue-tsc@2.1.10` / `@vue/tsconfig@0.5.1`
6. **`vite.config.js` 升级为 `vite.config.ts`**:本分支**额外改善**——`springboot4` 原 `vite.config.js` 有中文乱码,
   TS 版重写时用 TS + 干净中文
7. **`web-ui/src/assets/styles/basic.scss` 主题色从 `springboot3_TypeScript` 继承保留**:
   本仓库自定义的 `--el-color-primary: #1890ff` 蓝色品牌色不丢失
8. **`web-mp` 也跟着 TS 化**:`tsconfig.json` 配好(`strict: false` 起步,`@/*` 路径别名),
   `vite.config.js` 升级为 `vite.config.ts`,`package.json` 加 `typescript: ^5.4.5` / `vue-tsc: ^2.0.19`,
   并配 `typecheck` 脚本
9. **环境/配置微调**:`.npmrc` / `auto-imports.d.ts` / 各种 `.vue` `<script setup lang="ts">` 类型标注

`springboot4_TypeScript` 与 `springboot4` 的**唯一差异**就是上面 1–9 的前端 TS 化;后端代码、
SQL 脚本、`pom.xml`、业务逻辑**完全相同**。

---

## 三、相对 `springboot4` 的具体改动

| 改动 | 涉及文件 / 位置 |
|---|---|
| `web-ui` 整目录从 `springboot3_TypeScript` 替换 | `web-ui/` 全部(整目录替换) |
| `web-ui` API 层 TS 化 | `web-ui/src/api/**` 下所有 `*.js` → `*.ts` |
| `web-ui` 新增类型目录 | `web-ui/src/types/api/**.ts`(全量 API 类型)+ `types/components.d.ts` / `global.d.ts` / `index.ts` |
| `web-ui/utils/auth` TS 化 | `web-ui/src/utils/auth.ts` |
| `web-ui` 入口 / 路由 / store TS 化 | `web-ui/src/main.ts` / `permission.ts` / `router/index.ts` / `store/**` / `directive/**` / `plugins/**` |
| `web-ui` 主题色保留 | `web-ui/src/assets/styles/basic.scss`(从 `springboot3_TypeScript` 继承,本仓库主题色) |
| `web-ui` TS 工具链 | `web-ui/package.json` 新增 `typescript` / `vue-tsc` / `@vue/tsconfig` |
| `web-ui` 工程配置 | `web-ui/.npmrc`(pnpm 镜像源)/ `tsconfig.json` / `auto-imports.d.ts` |
| `web-ui` vite 配置 | `web-ui/vite.config.js` → `vite.config.ts`(★ 本分支额外改善,解决 `springboot4` 中文乱码) |
| `web-mp` TS 化 | `web-mp/tsconfig.json`(新增) + `web-mp/vite.config.ts`(`vite.config.js` 改名) |
| `web-mp` 入口 / 守卫 TS 化 | `web-mp/src/main.ts` / `web-mp/src/permission.ts` |
| `web-mp` TS 工具链 | `web-mp/package.json` 新增 `typescript: ^5.4.5` / `vue-tsc: ^2.0.19` / `typecheck` 脚本 |
| `web-mp` 其它 `*.ts` 化 | `web-mp/src/api/**` / `store/**` / `utils/**` 等 |
| 数据库脚本 | `file-sql/basicdb-vue3.sql`(与 `springboot4` 同步,PostgreSQL) |

> 后端代码(`SysLoginController` / `SysLoginService` / 若干 Mapper / `SysRole` / `pom.xml` / Jackson 3 适配代码 等)
> 与 `springboot4` **完全一致**,不重复。

---

## 四、与 `springboot4` 共享的部分

下面这些**与 `springboot4` 完全一致**,本分支**不重复**:

| 维度 | 内容 |
|---|---|
| **后端** | SB 4.1.1 + JDK 21 + Spring 7 + Security 7 + Jackson 3 + Jakarta 6 + springdoc 3.1 全部继承 |
| **7 个子模块 `parent.version`** | `4.0.0` |
| **Jackson 2 → 3 适配** | `JsonUtils` / `BigNumberSerializer` / `BigNumberModuleConfig` / `RedisConfig`(用 `GenericJacksonJsonRedisSerializer`)/ `ResourcesConfig` 精简 等 |
| **Spring Security 7 NPE 修复** | `LoginUser.getAuthorities()` 不返回 null |
| **Spring Framework 7 API 迁移** | `getPatternsCondition()` → `getPatternValues()` |
| **`RedisCache` 脏数据自愈** | `getCacheObject` 反序列化失败时自动删失效 key |
| **`Quartz` 自动装配** | 用 `spring-boot-starter-quartz` 替代手写 `quartz:2.5.2` 依赖 |
| **Druid 监控页** | `statViewServlet.enabled: true` |
| **业务功能** | 锁屏解锁、SQL bug 修复等与 master 共同的改动全部继承 |
| **数据库脚本** | `file-sql/basicdb-vue3.sql`(34 张表 + 5 函数,`basicdb` schema) |
| **目录结构** | 7 个后端 maven module + `web-ui/` + `web-mp/` |
| **`application.yml` 数据源** | PostgreSQL `jdbc:postgresql://localhost:5432/basicdb?currentSchema=basicdb` |

---

## 五、运行前 checklist

- [x] **JDK 21 安装** ★★ 与 master / `springboot3` 都不一样
- [x] Node.js + pnpm(前端)
- [x] `cd web-ui && pnpm install` 拉依赖(PC 端,lock 文件来自 `springboot3_TypeScript`,可能要重装)
- [x] `cd web-mp && pnpm install` 拉依赖(移动端)
- [ ] `pnpm dev` 验证 PC 端启动(`web-ui`)
- [ ] `pnpm dev:h5` 验证移动端 H5 启动(`web-mp`)
- [ ] `pnpm typecheck` 验证 TS 类型(`web-ui` / `web-mp`)
- [x] 7 个子模块 `parent.version` 一致为 **`4.0.0`** ★★ 与 master / `springboot3` 都不一样
- [x] IDE 工程的 Project SDK 设为 **JDK 21**,Language level **21** ★★ 与 master / `springboot3` 都不一样
- [x] `mvn clean compile` 0 ERROR
- [x] `mvn package -DskipTests` 0 ERROR
- [ ] 应用启动成功
- [ ] admin 登录成功(PC / 移动端)
- [ ] Druid 监控页 `/druid/index.html` 可访问(`statViewServlet.enabled: true`,账号 `basic` / 密码 `123456`)
- [ ] Redis 缓存读写正常(验证 Jackson 3 序列化 + defaultTyping)

---

## 六、风险评估 / 注意事项

- **后端 API 兼容**:`springboot3_TypeScript` 实际是 SB 3.5.16 后端调通的前端代码,
  但本分支后端是 SB 4.1.1(Jackson 3 时代)。API URL 没变,响应格式**基本兼容**(若依 API 是稳定契约)。
  **潜在差异**:
  - `Long` 大数字:Jackson 3 默认输出 long(可能 JS 端丢精度),由 `BigNumberSerializer` 处理
  - `LocalDateTime` / `LocalDate`:Jackson 3 默认输出 ISO-8601 字符串(JS 可解析)
  - `null` 字段:Jackson 3 默认也包含 `@JsonInclude` 行为
- **Jackson 2 → 3 序列化差异**:SB 4 的 `JsonMapper` 默认用 Jackson 3,序列化格式与 Jackson 2 略有差异
  (主要是 `writeBigDecimalAsPlain`、日期等)。前端如果依赖具体 JSON 字段顺序或格式,要验证。
- **`basic.scss` 主题色**:已从 `springboot3_TypeScript` 保留,本仓库主题色不丢失。
- **`vite.config.ts`**:`springboot4` 原 `vite.config.js` 有中文乱码,本分支用 TS 重写时已用干净中文,
  并把代理 target 等关键配置显式注释。
- **`pnpm-lock.yaml`**:`web-ui` 的 lock 文件用 `springboot3_TypeScript` 的版本,
  可能与本仓库之前装的依赖不完全一致。建议 `cd web-ui && pnpm install` 重新拉一遍。
- **TS 严格度**:`tsconfig.json` 默认 `strict: false` / `noImplicitAny: false` 起步,
  避免迁移时大量 `any` 报错;后续可逐步收紧。

---

## 七、未做 / 后续可优化

- [ ] `WebMvcConfigurer` 接口在 Spring Framework 7 已 `@Deprecated`,长期要看官方替代
- [ ] `fastjson2` 是否真不引:看业务缓存对象复杂度,如果都是基础类型,Jackson 3 + defaultTyping + 白名单足够
- [ ] Springdoc 3.1.1 是否完整兼容 SB 4:目前用起来没崩,但 OpenAPI 3.1 / Springdoc 2.7 vs 3.0+ 跟 SB 4 的细节未全部测过
- [ ] MyBatis-Plus 3.5.17 的 LambdaQuery 链式调用是否完整(目前只测了基础 CRUD)
- [ ] `Quartz` RAMJobStore 切换到 JDBC JobStore 用 PG(若依做法是用 RAMJobStore 避免 PG `varchar(1)` 字段雷)
- [ ] 生产环境 Druid 监控 `statViewServlet` 必须关或换强密码 + IP 白名单
- [ ] `MyBatis-Plus 3.5.7 → 3.5.17` 跨多个小版本的中间变更未逐个核对,只测了用到的 API
- [ ] `springdoc 2.9 → 3.1` 跨多个小版本的 OpenAPI Schema 兼容性未验证
- [ ] `pagehelper 2.1 → 4.1` 大版本跳变,分页参数兼容性是否完全保留未测
- [ ] `tsconfig.json` 严格度调高(目前 `strict: false`,逐步收紧)
- [ ] Pinia store / Router / Axios 二次封装的类型补全
- [ ] 集成 Flowable 流程引擎(已经在 `master-extend-flowable` 分支做了)

---

## 八、其他分支(参考)

本仓库其他分支(切到对应分支看其 README):

| 分支 | 做什么 |
|---|---|
| `master` | RuoYi-Vue 3.8.2 **最小改造基线**(后端 SB 2.5.14,JDK 11,前端 JS,加 web-mp) |
| `master-extend-flowable` | master + **Flowable 6.8.0 流程引擎**集成(新增 `extend-flowable` 子模块),数据库从 MySQL 迁到 PostgreSQL(schema `basicdb_flowable`,86 张表 + 2 视图);并增加流程建模/部署/发起/审批/退回/转办/委派/加签/抄送等业务 |
| `springboot2_TypeScript` | master + 前端两端(`web-ui` / `web-mp`)**全量 TypeScript 化**;后端保持 SB 2.5.14 |
| `springboot3` | 后端 SB 2.5.14 → **3.5.16**(JDK 11 → 17),Jakarta EE + Spring Security 6 + springdoc;前端保持 JS |
| `springboot3_TypeScript` | `springboot3` + 前端 TS 化 |
| `springboot4` | 后端 SB 3.5.16 → **4.1.1**(JDK 17 → 21),Jackson 3 + Spring Security 7 + Spring Framework 7 + ...;前端保持 JS |

> 本分支 `springboot4_TypeScript` ≈ `springboot4` + 前端 TS 化,可以视作"`springboot4` 的 TS 版",
> 也是**仓库最完整栈**(后端最新 SB 4.1.1 + 前端 TypeScript)。

---

## 九、技术栈演进时间线

```
master                          SB 2.5.14 / JDK 11 / Security 5 / javax / springfox / Jackson 2 / 前端 JS
  └── springboot2_TypeScript    SB 2.5.14 / JDK 11 / Security 5 / javax / springfox / Jackson 2 / 前端 TS
  └── springboot3               SB 3.5.16 / JDK 17 / Security 6 / jakarta / springdoc / Jackson 2 / 前端 JS
  └── springboot3_TypeScript    SB 3.5.16 / JDK 17 / Security 6 / jakarta / springdoc / Jackson 2 / 前端 TS
  └── springboot4               SB 4.1.1  / JDK 21 / Security 7 / jakarta 6 / springdoc 3.1 / Jackson 3 / 前端 JS
  └── springboot4_TypeScript    SB 4.1.1  / JDK 21 / Security 7 / jakarta 6 / springdoc 3.1 / Jackson 3 / 前端 TS  ← 本分支
  └── master-extend-flowable    master + Flowable 6.8.0 + 数据库迁到 PostgreSQL(schema basicdb_flowable, 86 张表 + 2 视图)
```

---

## 十、应用方式(从 `springboot4` 切出本分支的命令)

```bash
# 在 springboot4 分支基础上,整 web-ui 用 springboot3_TypeScript 的 TS 版替换
git rm -r web-ui/
git checkout springboot3_TypeScript -- web-ui/
# springboot3_TypeScript 已经保留 basic.scss,不需要再单独恢复
# web-mp 整目录也用 springboot3_TypeScript 的 TS 版替换(或在 springboot4 JS 版基础上单独 TS 化)
```

---

## 十一、移动端 `web-mp/`

> 本分支与 `springboot4` 同样包含 `web-mp/` 移动端,**额外做了 TS 化**:
> 新增 `tsconfig.json`,`vite.config.js` → `vite.config.ts`,`package.json` 加 TS 工具链,
> 入口 / 守卫等 `.js` → `.ts`。
> 本节是 `web-mp/README.md` 的摘要,完整内容以子目录的 README 为准。

### 11.1 技术栈

| 项 | 版本 |
|---|---|
| uni-app | 3.0 (alpha,`@dcloudio/uni-app@3.0.0-4060620250520001`) |
| Vue | 3.4.21(**组合式 API** `<script setup lang="ts">`) |
| Vite | 5.2.8 |
| UI | **uview-plus** 3.3.62(uView 的 Vue3 版本,组件名带 `up-` 前缀) |
| 语言 | **TypeScript ^5.4.5**(`tsconfig.json` 配 `strict: false` 起步) |
| 类型检查 | **vue-tsc ^2.0.19**(`pnpm typecheck` 触发) |
| 状态管理 | Pinia 2.0.36 |
| 加密 | jsencrypt(对接后端 RSA 登录) |
| 时间 | dayjs 1.11.x |
| 国际化 | vue-i18n 9.13.1(已引入,词条待补) |
| 包管理 | pnpm |

### 11.2 与 web-ui / 后端的关系

- 与 `web-ui/`(PC 端)**同级目录**,**不**是 web-ui 的子项目,各自独立维护
- 不参与 maven 编译(`pom.xml` 的 module 列表里没有 `web-mp`)
- 对接后端:**直接打**本仓库的 `basic-admin-api`(Spring Boot 4.1 + Spring Security 7 + Jackson 3 + Jakarta EE 6),
  接口复用 `/login` / `/logout` / `/captchaImage` / `/getInfo` / `/getRouters` 等
- 默认账号:`admin / admin123`

### 11.3 目录结构

```
web-mp/
├── index.html                 # H5 入口(vite 用)
├── tsconfig.json              # ★ 本分支新增:TypeScript 配置
├── vite.config.ts             # ★ 本分支:.js 升级为 .ts
├── package.json
├── config.js                  # 全局配置(双份,同步到 src/)
├── manifest.json              # 应用清单(双份,同步到 src/)
├── pages.json                 # 页面注册(双份,同步到 src/)
├── sync-config.cjs            # 双份同步脚本
├── scripts/
│   └── fixup-mp-components.cjs  # 微信小程序组件 fixup 脚本
└── src/
    ├── main.ts                # ★ 本分支:.js 升级为 .ts
    ├── App.vue                # 根组件(<script setup lang="ts">)
    ├── uni.scss               # 全局 SCSS 变量入口
    ├── pages/                 # 业务页面
    │   ├── login/index.vue
    │   ├── index/index.vue    # 首页(TabBar)
    │   ├── workbench/index.vue
    │   ├── user/index.vue
    │   └── common/{notice,profile}/...
    ├── api/                   # 接口层(.ts)
    ├── store/                 # Pinia(.ts)
    ├── utils/                 # 工具(.ts)
    ├── directive/             # 权限指令(.ts)
    ├── permission.ts          # ★ 本分支:.js 升级为 .ts
    └── static/                # 静态资源
```

### 11.4 关键设计

- **登录流程**:`/captchaImage` → `/login`(RSA 公钥加密密码)→ `/getInfo` → `/getRouters`
- **Token 存储**:`uni.getStorageSync('Basic-Token')`
- **请求封装**:`utils/request.ts` 实现拦截器(401 跳登录)
- **权限指令**:`v-hasPermi`(uniapp 3 编译时不支持自定义指令,用 `v-if="hasPermi([...])"` 替代)
- **双份 manifest/pages**:CLI build 找 `src/manifest.json`,dev 找根 `manifest.json`,
  所以两边各放一份,`sync-config.cjs` 同步
- **环境配置**:`src/config.js` 维护 `baseUrl`(默认 `http://localhost:8080`,
  H5 通过 `vite.config.ts` 代理到 `/dev-api` → 8080;小程序/真机需改成局域网 IP)
- **TypeScript**:`tsconfig.json` 配 `@/*` 路径别名 → `src/*`;`strict: false` 起步,
  避免迁移时大量 `any` 报错;`include` 覆盖 `src/**/*.{ts,d.ts,tsx,vue}` + `vite.config.ts`

### 11.5 运行

```bash
cd web-mp
pnpm install

# 类型检查
pnpm typecheck              # vue-tsc --noEmit

# 开发
pnpm dev:h5            # 浏览器访问 http://localhost:8081
pnpm dev:mp-weixin     # 用微信开发者工具打开 dist/dev/mp-weixin
pnpm dev:app           # 编译到 unpackage/dist/dev/app

# 生产构建
pnpm build:h5          # 产物在 dist/build/h5
pnpm build:mp-weixin   # 产物在 dist/build/mp-weixin
pnpm build:app
```

### 11.6 待办(摘自子 README)

- [ ] 接入动态菜单(`/getRouters` → pages.json)
- [ ] 国际化(`vue-i18n` 已引入,词条待补)
- [ ] 消息推送 / WebSocket
- [ ] 修改密码页 + 个人中心编辑
- [ ] 锁屏(手势/PIN)
- [ ] TabBar 图标(`src/static/tabbar/` 需放 6 张 81x81 png)
- [ ] `tsconfig.json` 严格度调高(目前 `strict: false`,逐步收紧)

---

## 十二、数据库脚本 (`file-sql/basicdb-vue3.sql`)

> 本分支运行所需的 **PostgreSQL 全库结构 + 初始化数据** 都在 `file-sql/basicdb-vue3.sql`。
> 其它 `file-sql/*.sql` 是历史中间产物,本分支**不直接使用**;真正要执行的就是这一个。

### 12.1 脚本基本信息

| 项 | 值 |
|---|---|
| 文件路径 | `file-sql/basicdb-vue3.sql` |
| 来源 | `pg_dump` 导出(导出自 PostgreSQL 15.1,导出工具 pg_dump 17.0) |
| 数据库角色 | 所有对象 `OWNER TO system`(导入前**必须**先有 `system` 角色) |
| Schema | `basicdb`(脚本里所有表/函数/序列都在该 schema 下) |
| 表数量 | **34 张**(`gen_*` 2 + `qrtz_*` 11 + `sys_*` 21) |
| 函数数量 | 5 个(`date_format` / `exec` / `find_in_set` / `ifnull` / `tabledef`) |
| 数据加载方式 | `COPY ... FROM stdin;`(原生 psql `\copy` 流式导入,**不**走 `INSERT`) |
| PostgreSQL 要求 | **>= 15.1** |

### 12.2 表清单(共 34 张,均位于 `basicdb` schema)

#### 代码生成(`gen_*`,2 张)

| 表 | 用途 |
|---|---|
| `gen_table` | 代码生成业务表配置 |
| `gen_table_column` | 代码生成字段配置 |

#### 定时任务(`qrtz_*`,11 张)

`qrtz_blob_triggers` / `qrtz_calendars` / `qrtz_cron_triggers` / `qrtz_fired_triggers` /
`qrtz_job_details` / `qrtz_locks` / `qrtz_paused_trigger_grps` / `qrtz_scheduler_state` /
`qrtz_simple_triggers` / `qrtz_simprop_triggers` / `qrtz_triggers`

#### 业务系统(`sys_*`,21 张)

| 表 | 用途 |
|---|---|
| `sys_config` | 全局参数 |
| `sys_demo` | 演示样例 |
| `sys_dept` | 部门 |
| `sys_dict_data` | 字典值 |
| `sys_dict_type` | 字典类型 |
| `sys_files` | 文件 |
| `sys_i18n` | 国际化文案 |
| `sys_job` | 定时任务 |
| `sys_job_log` | 定时任务日志 |
| `sys_logininfor` | 登录日志 |
| `sys_menu` | 菜单(含 `is_resource` / `show_tags` / `menu_name_en` / `menu_name_zh_hant` 等扩展列) |
| `sys_notice` | 通知公告 |
| `sys_oper_log` | 操作日志 |
| `sys_post` | 岗位 |
| `sys_role` | 角色 |
| `sys_role_dept` | 角色-部门关联 |
| `sys_role_menu` | 角色-菜单关联 |
| `sys_sms_message` | 短信发送记录 |
| `sys_user` | 用户 |
| `sys_user_post` | 用户-岗位关联 |
| `sys_user_role` | 用户-角色关联 |

### 12.3 恢复步骤(全新数据库)

```bash
# 1) 建 system 角色和 basicdb 库(SQL 脚本本身不建角色,必须先建好)
psql -U postgres -h localhost -c "CREATE ROLE system LOGIN PASSWORD 'system';"
psql -U postgres -h localhost -c "CREATE DATABASE basicdb OWNER system;"

# 2) 导入 schema + 数据
psql -U system -h localhost -d basicdb -f file-sql/basicdb-vue3.sql

# 3) 验证(应返回 34)
psql -U system -h localhost -d basicdb -c \
  "SELECT count(*) FROM information_schema.tables WHERE table_schema='basicdb';"
```

如果本地 PostgreSQL 实例没有 `system` 角色,直接执行第 2 步会报
`ERROR: role "system" does not exist`,补一步 1) 即可。

### 12.4 重新备份成同款脚本

```bash
pg_dump -U system -h localhost -d basicdb \
  --schema=basicdb --no-owner --no-privileges \
  --file=file-sql/basicdb-vue3.sql
```

- 加 `--no-owner --no-privileges` 后,导出的脚本**不再**带 `OWNER TO system`、可移植到任意环境。
- 不加这两个开关,导出结果与本仓库现存的 `basicdb-vue3.sql` 格式一致。
- 脚本里所有 `data` 段都用了 `COPY ... FROM stdin;`,导入端必须用 `psql`。

### 12.5 默认账号

| 用户名 | 密码(明文) | 备注 |
|---|---|---|
| `admin` | `admin123` | 超级管理员,登录后请立刻改密 |

要重置可执行同目录的 `reset-admin-password.sql`。

### 12.6 与应用连接配置

```yaml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/basicdb?currentSchema=basicdb
    username: system
    password: system
```

> `currentSchema=basicdb` 必须加,否则 MyBatis 找不到 `sys_user` / `sys_menu` 等表
> (脚本里所有表都建在 `basicdb` schema 下,不是 `public`)。

### 12.7 其它 `file-sql/*.sql` 说明(本分支**不直接使用**,留作历史)

| 文件 | 角色 |
|---|---|
| `basic-sql-2023-03-02.sql` | 早期 MySQL 5.7 全库脚本,**非 PostgreSQL**,本分支不要混用 |
| `quartz.sql` | 单独的 Quartz 建表脚本,**已被 `basicdb-vue3.sql` 内置的 11 张 `qrtz_*` 表覆盖** |
| `ry_20210908.sql` | RuoYi 3.8.2 原始 MySQL 全库脚本,**非 PostgreSQL**,对照用 |
| `reset-admin-password.sql` | 把 `admin` 密码重置回 `admin123` 的小工具,按需手动跑 |
| `reset-all-passwords.sql` | 把**所有**用户密码统一重置回默认的小工具,慎用 |
| `fix-sys-menu-component.sql` | 修 `sys_menu.component` 路径里 `system` 写错的,按需手动跑 |
| `create-sys-notice-read.sql` | 补建 `sys_notice_read`(用户已读记录)表,按需手动跑 |

> 一句话:**运行本项目,只跑 `basicdb-vue3.sql` 这一个文件就够了**。
> (注:`master-extend-flowable` 分支已经把上述 7 个历史脚本里的部分文件删掉了,
> 本分支**仍保留**。)

---

## 十三、目录速查

```
base-postgresql-vue3/
├── pom.xml                              # 父 pom(7 个后端子模块,无 web-mp)
├── README.md                            # 本文档
├── ry.sh / ry.bat                       # RuoYi 启动脚本(原版遗留)
├── basic-admin-api/                     # Spring Boot 启动模块(SB 4.1.1)
├── basic-admin-core/                    # 业务核心(system 模块)
├── basic-framework/                     # 框架配置(Security 7 / Jakarta 6 / Jackson 3)
├── basic-common/                        # 通用工具/基类
├── business-core/                       # 业务扩展点
├── extend-quartz/                       # Quartz 定时任务扩展(用 spring-boot-starter-quartz)
├── extend-generator/                    # 代码生成扩展
├── extend-flowable/                     # (仅 master-extend-flowable 分支)
├── file-sql/                            # SQL 脚本
├── file-bin/                            # 二进制资源(jar 内置)
├── file-doc/                            # 文档资源(jar 内置)
├── web-ui/                              # PC 端前端(Vue3 + Element Plus + ★ TypeScript)
└── web-mp/                              # 移动端前端(uni-app3 + uview-plus + ★ TypeScript)
```

> **本分支与 `springboot4` 的差异**:`web-ui` 和 `web-mp` 都从 JS → TS;
> `web-mp` 还多出 `tsconfig.json` 与 `vite.config.ts`,`web-ui` 多出 `src/types/` 目录;
> `web-ui/vite.config.js` → `vite.config.ts` 顺便解决了 `springboot4` 的中文乱码。
> `web-ui/src/assets/styles/basic.scss`(本仓库主题色)从 `springboot3_TypeScript` 继承保留,避免 TS 版替换时丢失品牌色。
