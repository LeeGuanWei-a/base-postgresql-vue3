# Basic Admin (Spring Boot 2.5.14 + Vue 3 + Element Plus + TypeScript)

基于若依 RuoYi-Vue 3.8.2 改造,在 `first commit` 基础上**只做了前端 JS → TS 改造**,**后端没动**(仍是 Spring Boot 2.5.14 + JDK 11)。

> 本分支 `TypeScript` 对应 commit `e10e3ca 前端`(`5b344b7 first commit` 的 JS 版本基础上,加 commit `567b5a5 前端替换成ts` 和后续的 `e10e3ca 前端` 微调)。
>
> 仓库另有 `springboot3` / `springboot4` 分支,后端升级到 SB 3.5.16 / SB 4.1.1,**与本分支无关**。

---

## 前端说明

本仓库前端为 **TypeScript 版**:

- 框架:Vue 3.5.26 + Element Plus 2.13.1
- 构建:Vite 6.4.1
- 语言:**TypeScript 5.6.3**(`tsconfig.json` 存在,`vue-tsc: 2.1.10` 做类型检查)
- 配置:`@vue/tsconfig: 0.5.1` 作为 tsconfig 基础
- 自动导入:`auto-imports.d.ts` / `components.d.ts` / `global.d.ts`

> 仓库另有 `springboot3` / `springboot4` 分支,前端是 **JavaScript 版**(`package.json` 不含 `typescript`),本分支**未涉及**。

---

## 一、当前后端状态(基于 `git show HEAD:pom.xml`)

> 本分支**后端没升级**。基于若依 RuoYi-Vue 3.8.2 原始 SB 2.x 配置。

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

## 二、git 历史(5 个 commit)

```
e10e3ca  前端                       ← 当前 HEAD
1a06798  md                         ← README
14b851a  修复sql bug,锁屏解锁功能
567b5a5  前端替换成ts               ← 本次 TS 化主 commit
5b344b7  first commit               ← 若依 RuoYi 3.8.2 原始
```

---

## 三、前端 JS → TS 改造详情(基于 `git show 567b5a5` 和 `e10e3ca`)

### 3.1 主 commit `567b5a5 前端替换成ts`

把 `web-ui/src/api/**` 下所有 `*.js` 改名为 `*.ts`,并加 `types/` 目录。

| 旧文件 | 新文件 | 内容 |
|---|---|---|
| `src/api/login.js` | `src/api/login.ts` | 登录 / 注销 / 获取用户信息 |
| `src/api/menu.js` | `src/api/menu.ts` | 获取路由菜单 |
| `src/api/monitor/cache.js` | `src/api/monitor/cache.ts` | 缓存监控 |
| `src/api/monitor/job.js` | `src/api/monitor/job.ts` | 定时任务 |
| `src/api/monitor/jobLog.js` | `src/api/monitor/jobLog.ts` | 任务日志 |
| `src/api/monitor/online.js` | `src/api/monitor/online.ts` | 在线用户 |
| `src/api/monitor/operlog.js` | `src/api/monitor/operlog.ts` | 操作日志 |
| `src/api/monitor/server.js` | `src/api/monitor/server.ts` | 服务器监控 |
| `src/api/system/config.js` | `src/api/system/config.ts` | 参数配置 |
| `src/api/system/dept.js` | `src/api/system/dept.ts` | 部门管理 |
| `src/api/system/dict/data.js` | `src/api/system/dict/data.ts` | 字典数据 |
| `src/api/system/dict/type.js` | `src/api/system/dict/type.ts` | 字典类型 |
| `src/api/system/menu.js` | `src/api/system/menu.ts` | 菜单管理 |
| `src/api/system/notice.js` | `src/api/system/notice.ts` | 通知公告 |
| `src/api/system/post.js` | `src/api/system/post.ts` | 岗位管理 |
| `src/api/system/role.js` | `src/api/system/role.ts` | 角色管理 |
| `src/api/system/user.js` | `src/api/system/user.ts` | 用户管理 |
| `src/api/tool/gen.js` | `src/api/tool/gen.ts` | 代码生成 |

`src/utils/auth.ts` 也被改写(从 `.js` 转 `.ts`)。

### 3.2 新增 `web-ui/src/types/` 目录(全量 TypeScript 类型)

```
src/types/
├── api/
│   ├── common.ts        ← AjaxResult / TableDataInfo / R<T> / PageQuery 等通用类型
│   ├── index.ts
│   ├── login.ts         ← LoginBody / LoginResult / UserInfo
│   ├── menu.ts
│   ├── monitor/
│   │   ├── cache.ts
│   │   ├── job.ts
│   │   ├── jobLog.ts
│   │   ├── logininfor.ts
│   │   ├── online.ts
│   │   └── operlog.ts
│   ├── system/
│   │   ├── config.ts
│   │   ├── dept.ts
│   │   ├── dict.ts
│   │   ├── menu.ts
│   │   ├── notice.ts
│   │   ├── post.ts
│   │   ├── role.ts
│   │   └── user.ts
│   └── tool/
│       └── gen.ts
├── components.d.ts      ← unplugin-auto-import / vite-plugin-components 的自动生成
├── global.d.ts          ← 全局 window / module 声明
└── index.ts             ← 类型统一出口
```

每个 `types/api/**.ts` 定义了**对应的 API 函数**的**参数类型**和**返回类型**(从后端 RuoYi 的 `AjaxResult` / `TableDataInfo` 风格入手)。

### 3.3 `package.json` 新增依赖

```json
{
  "devDependencies": {
    "@vue/tsconfig": "0.5.1",      // Vue 3 推荐 tsconfig 基础
    "typescript": "5.6.3",          // TS 编译器
    "vue-tsc": "2.1.10"             // .vue 文件的类型检查(走 tsc 也能过 .ts)
  }
}
```

> 注意:Spring Boot 2 时代的 JS 版分支,后端是 SB 2.5.14,所以 `package.json` 里的 `devDependencies` **没动**到与 SB 相关的部分(只是加了 TS 工具链)。

### 3.4 后续 commit `e10e3ca 前端`

在 `567b5a5` 基础上又做了一次小调整:
- `web-ui/.env.development` / `.env.staging`:Vite 环境变量
- `web-ui/.gitignore` / `web-ui/.npmrc`:Node 工程配置
- `web-ui/src/App.vue`:根组件微调
- `web-ui/src/assets/images/pay.png`:新增 140 KB 图片(可能是支付二维码)
- `web-ui/README.md`:**删除**(原 web-ui 自己的 README 删了,改用根目录 README)

> 这个 commit 主要补一些环境配置和图片,跟 TS 化**没直接关系**。

### 3.5 后端代码改动

> 本分支**后端没动**(仍 SB 2.5.14 + JDK 11 + JS 化之前的原始版本)。

仅 `SysLoginController.java` / `SysDeptMapper.java` / `SysMenuMapper.java` 在 commit `14b851a 修复sql bug,锁屏解锁功能` 中改过,**但那是 SB 2.5.14 时代的事**。

---

## 四、运行前 checklist

- [x] JDK 11 安装
- [x] Maven 用 `D:\MavenRepository5` 本地仓库
- [x] Node.js + pnpm(前端用)
- [x] `cd web-ui && pnpm install && pnpm dev` 启动前端
- [x] 后端 mvn 启动(SB 2.5.14 时代,默认 `8080` 端口)
- [x] 前端通过 Vite 代理访问后端(`vite.config.ts` 配 `proxy`)

---

## 五、未做 / 后续可优化

> 这些是**本分支没做**的,如果想继续演进,可以参考 `springboot3` / `springboot4` 分支的 diff。

- [ ] 后端 SB 2.5.14 → 3.5.16(见 `springboot3` 分支 README)
- [ ] 后端 SB 3.5.16 → 4.1.1(见 `springboot4` 分支 README)
- [ ] 前端如果从 TS 改成 `<script setup lang="ts">` 全量 Composition API 风格(本分支只换了 `.js` → `.ts`,业务代码 `<script>` 风格没全部改)
- [ ] `tsconfig.json` 严格度调高(`strict: true` 开启后会有大量 `any` 报错需要修)
- [ ] Pinia store / Router / Axios 二次封装的类型补全(目前 `src/store/modules/app.ts` 已有,其他 store 待补)
- [ ] `vite.config.ts` 配 proxy 时硬编码了 IP,改用环境变量

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

> 一句话:**运行本项目,只跑 `basicdb-vue3.sql` 这一个文件就够了。**
