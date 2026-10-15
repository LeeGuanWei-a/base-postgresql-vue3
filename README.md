# Basic Admin (Spring Boot 2.5.14 + Vue 3 + Element Plus + TypeScript)

基于若依 RuoYi-Vue 3.8.2 改造,与 `master` 分支**唯一区别**:**前端从 JavaScript 改成 TypeScript**(包括 `web-ui` 和 `web-mp` 两端),**后端完全没动**(仍是 Spring Boot 2.5.14 + JDK 11 + Spring Security 5.5)。

> 当前分支:`springboot2_TypeScript` ——「**master + 前端全量 TypeScript 化**」。
> 仓库另有 `master` / `master-extend-flowable` / `springboot3` / `springboot3_TypeScript` / `springboot4` / `springboot4_TypeScript` 等分支,本分支**只描述 springboot2_TypeScript**。

---

## 前端说明

本分支前端**两端都是 TypeScript 版**(区别于 master 的 JS 版):

### 1.1 PC 管理端 `web-ui/`

- 框架:Vue 3.5.26 + Element Plus 2.13.1
- 构建:Vite 6.4.1
- 语言:**TypeScript 5.6.3**(`tsconfig.json` 存在,`vue-tsc: 2.1.10` 做类型检查)
- 配置:`@vue/tsconfig: 0.5.1` 作为 tsconfig 基础
- 自动导入:`auto-imports.d.ts` / `components.d.ts` / `global.d.ts`
- API 层:`src/api/**` 全部从 `.js` → `.ts`,并新增 `src/types/api/**.ts` 全量类型定义

> JS 版在 `master` 分支,本分支**未涉及**。

### 1.2 移动端 `web-mp/`(本分支新增,与 master 一致)

- 框架:**uni-app 3.0 (alpha)** + Vue 3.4.21(组合式 API `<script setup lang="ts">`)
- UI:**uview-plus** 3.3.62(uView 的 Vue3 版本,组件名带 `up-` 前缀)
- 语言:**TypeScript ^5.4.5**(`tsconfig.json` 已配,`vue-tsc ^2.0.19` 做类型检查)
- 构建:Vite 5.2.8
- 一份代码同时跑 **H5 / 微信小程序 / Android / iOS / 其它小程序**
- 状态管理:Pinia 2.0.36
- 加密:jsencrypt(对接后端 RSA 登录)

> 详细说明见下文**「七、移动端 `web-mp`」** 章节。

---

## 一、当前后端状态(基于 `git show HEAD:pom.xml`)

> 本分支**后端没升级**,与 master 完全一致。基于若依 RuoYi-Vue 3.8.2 原始 SB 2.x 配置。

| 组件 | 版本 |
|---|---|
| **JDK** | **11** |
| Spring Boot | **2.5.14** |
| Spring Framework | 5.3.x |
| Spring Security | 5.5.x |
| MyBatis-Plus | 3.5.1 |
| Druid | 1.2.8(`druid-spring-boot-starter`,无 `-3-` 写法) |
| PageHelper | 1.4.1 |
| swagger | springfox 3.0.0 |
| Hutool | hutool-bom 5.8.3(bom 模式) |
| bitwalker | 1.21(用户代理字符串解析) |
| kaptcha | 2.3.2 |
| poi | 4.1.2 |
| commons-io | 2.11.0 |
| oshi | 6.1.6 |
| jakarta.servlet | (用 javax.servlet) |

---

## 二、本分支是什么

`springboot2_TypeScript` 分支以 **master 分支**为基线,只做了一件事:

> **把前端两端的代码从 JavaScript 全部改写成 TypeScript**(`web-ui/` 和 `web-mp/`),
> 后端、后端依赖、数据库脚本、SQL 配置**完全不动**。

具体而言:

1. **`web-ui/src/api/**` 全量 `.js` → `.ts`**:login / menu / monitor.* / system.* / tool.* 共 18+ 个 API 文件
2. **新增 `web-ui/src/types/` 目录**:全量 TypeScript 类型定义(API 参数 / 返回 / 通用 R / AjaxResult / TableDataInfo 等)
3. **新增 `web-ui/src/utils/auth.ts`**:从原 `.js` 改写并加类型
4. **`package.json` 新增 TS 工具链**:`typescript@5.6.3` / `vue-tsc@2.1.10` / `@vue/tsconfig@0.5.1`
5. **`web-mp` 也跟着 TS 化**:`tsconfig.json` 配好(`strict: false` 起步,`@/*` 路径别名),
   `vite.config.js` 升级为 `vite.config.ts`,`package.json` 加 `typescript: ^5.4.5` / `vue-tsc: ^2.0.19`,
   并配 `typecheck` 脚本
6. **环境/配置微调**:`.env.development` / `.env.staging` / `.gitignore` / `.npmrc` 等补充
7. **数据库脚本**:`file-sql/basicdb-vue3.sql` 与 master 同步(34 张表 + 5 函数,`basicdb` schema)

`springboot2_TypeScript` 与 `master` 的**唯一差异**就是上面 1–6 的前端 TS 化;后端代码、
SQL 脚本、`pom.xml`、业务逻辑**完全相同**。

---

## 三、相对 master 的具体改动

| 改动 | 涉及文件 / 位置 |
|---|---|
| `web-ui` API 层 TS 化 | `web-ui/src/api/**` 下所有 `*.js` → `*.ts` |
| `web-ui` 新增类型目录 | `web-ui/src/types/api/**.ts`(全量 API 类型)+ `types/components.d.ts` / `global.d.ts` / `index.ts` |
| `web-ui/utils/auth` TS 化 | `web-ui/src/utils/auth.ts` |
| `web-ui` TS 工具链 | `web-ui/package.json` 新增 `typescript` / `vue-tsc` / `@vue/tsconfig` |
| `web-mp` TS 化 | `web-mp/tsconfig.json`(新增) + `web-mp/vite.config.ts`(`vite.config.js` 改名) |
| `web-mp` TS 工具链 | `web-mp/package.json` 新增 `typescript: ^5.4.5` / `vue-tsc: ^2.0.19` / `typecheck` 脚本 |
| `web-ui` 环境/工程配置 | `web-ui/.env.development` / `.env.staging` / `.gitignore` / `.npmrc` / `src/App.vue` 微调 / `assets/images/pay.png` 新增 |
| 数据库脚本 | `file-sql/basicdb-vue3.sql`(与 master 同步,PostgreSQL) |

> 后端代码(`SysLoginController` / `SysLoginService` / 若干 Mapper / `SysRole` 等)
> 与 master **完全一致**,不重复。

---

## 四、运行前 checklist

- [x] JDK 11 安装
- [x] Node.js + pnpm(前端)
- [x] `cd web-ui && pnpm install` 拉依赖(PC 端)
- [x] `cd web-mp && pnpm install` 拉依赖(移动端)
- [ ] `pnpm dev` 验证 PC 端启动(`web-ui`)
- [ ] `pnpm dev:h5` 验证移动端 H5 启动(`web-mp`)
- [ ] `pnpm typecheck` 验证 TS 类型(`web-ui` / `web-mp`)
- [x] 7 个子模块 `parent.version` 一致为 `3.8.2`
- [x] IDE 工程的 Project SDK 设为 JDK 11,Language level 11
- [x] `mvn clean compile` 0 ERROR
- [ ] 应用启动成功
- [ ] admin 登录成功(PC / 移动端)

---

## 五、其他分支(参考)

本仓库其他分支(切到对应分支看其 README):

| 分支 | 做什么 |
|---|---|
| `master` | RuoYi-Vue 3.8.2 **最小改造基线**(后端零升级,前端 JS,加 web-mp) |
| `master-extend-flowable` | master + **Flowable 6.8.0 流程引擎**集成(新增 `extend-flowable` 子模块),数据库从 MySQL 迁到 PostgreSQL(schema `basicdb_flowable`,86 张表 + 2 视图);并增加流程建模/部署/发起/审批/退回/转办/委派/加签/抄送等业务 |
| `springboot3` | 后端 SB 2.5.14 → 3.5.16(JDK 11 → 17),Jakarta EE + Spring Security 6 + springdoc + ... |
| `springboot3_TypeScript` | `springboot3` + 前端 TS 化 |
| `springboot4` | 后端 SB 3.5.16 → 4.1.1(JDK 17 → 21),Jackson 3 + Spring Security 7 + Spring Framework 7 + ... |
| `springboot4_TypeScript` | `springboot4` + 前端 TS 化(最完整栈) |

> 本分支 `springboot2_TypeScript` ≈ master + 前端 TS 化,可以视作"master 的 TS 版"。

---

## 六、数据库脚本 (`file-sql/basicdb-vue3.sql`)

> 本分支运行所需的 **PostgreSQL 全库结构 + 初始化数据** 都在 `file-sql/basicdb-vue3.sql`。
> 其它 `file-sql/*.sql` 是历史中间产物,本分支**不直接使用**;真正要执行的就是这一个。

### 6.1 脚本基本信息

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

### 6.2 表清单(共 34 张,均位于 `basicdb` schema)

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

### 6.3 恢复步骤(全新数据库)

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

### 6.4 重新备份成同款脚本

```bash
pg_dump -U system -h localhost -d basicdb \
  --schema=basicdb --no-owner --no-privileges \
  --file=file-sql/basicdb-vue3.sql
```

- 加 `--no-owner --no-privileges` 后,导出的脚本**不再**带 `OWNER TO system`、可移植到任意环境。
- 不加这两个开关,导出结果与本仓库现存的 `basicdb-vue3.sql` 格式一致。
- 脚本里所有 `data` 段都用了 `COPY ... FROM stdin;`,导入端必须用 `psql`。

### 6.5 默认账号

| 用户名 | 密码(明文) | 备注 |
|---|---|---|
| `admin` | `admin123` | 超级管理员,登录后请立刻改密 |

要重置可执行同目录的 `reset-admin-password.sql`。

### 6.6 与应用连接配置

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

### 6.7 其它 `file-sql/*.sql` 说明(本分支**不直接使用**,留作历史)

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

## 七、移动端 `web-mp/`

> 本分支与 master 同样包含 `web-mp/` 移动端,**额外做了 TS 化**:
> 新增 `tsconfig.json`,`vite.config.js` → `vite.config.ts`,`package.json` 加 TS 工具链。
> 本节是 `web-mp/README.md` 的摘要,完整内容以子目录的 README 为准。

### 7.1 技术栈

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

### 7.2 与 web-ui / 后端的关系

- 与 `web-ui/`(PC 端)**同级目录**,**不**是 web-ui 的子项目,各自独立维护
- 不参与 maven 编译(`pom.xml` 的 module 列表里没有 `web-mp`)
- 对接后端:**直接打**本仓库的 `basic-admin-api`(Spring Boot 2.5 + Spring Security),
  接口复用 `/login` / `/logout` / `/captchaImage` / `/getInfo` / `/getRouters` 等
- 默认账号:`admin / admin123`

### 7.3 目录结构

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
    ├── api/                   # 接口层
    ├── store/                 # Pinia
    ├── utils/                 # 工具
    ├── directive/             # 权限指令
    ├── permission.ts          # ★ 本分支:.js 升级为 .ts
    └── static/                # 静态资源
```

### 7.4 关键设计

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

### 7.5 运行

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

### 7.6 待办(摘自子 README)

- [ ] 接入动态菜单(`/getRouters` → pages.json)
- [ ] 国际化(`vue-i18n` 已引入,词条待补)
- [ ] 消息推送 / WebSocket
- [ ] 修改密码页 + 个人中心编辑
- [ ] 锁屏(手势/PIN)
- [ ] TabBar 图标(`src/static/tabbar/` 需放 6 张 81x81 png)
- [ ] `tsconfig.json` 严格度调高(目前 `strict: false`,逐步收紧)

---

## 八、目录速查

```
base-postgresql-vue3/
├── pom.xml                              # 父 pom(7 个后端子模块,无 web-mp)
├── README.md                            # 本文档
├── ry.sh / ry.bat                       # RuoYi 启动脚本(原版遗留)
├── basic-admin-api/                     # Spring Boot 启动模块
├── basic-admin-core/                    # 业务核心(system 模块)
├── basic-framework/                     # 框架配置
├── basic-common/                        # 通用工具/基类
├── business-core/                       # 业务扩展点
├── extend-quartz/                       # Quartz 定时任务扩展
├── extend-generator/                    # 代码生成扩展
├── extend-flowable/                     # (仅 master-extend-flowable 分支)
├── file-sql/                            # SQL 脚本
├── file-bin/                            # 二进制资源(jar 内置)
├── file-doc/                            # 文档资源(jar 内置)
├── web-ui/                              # PC 端前端(Vue3 + Element Plus + ★ TypeScript)
└── web-mp/                              # 移动端前端(uni-app3 + uview-plus + ★ TypeScript)
```

> **本分支与 master 的差异**:`web-ui` 和 `web-mp` 都从 JS → TS;
> `web-mp` 还多出 `tsconfig.json` 与 `vite.config.ts`,`web-ui` 多出 `src/types/` 目录。
