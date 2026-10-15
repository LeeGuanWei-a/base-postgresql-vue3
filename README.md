# Basic Admin (Spring Boot 2.5.14 + Vue 3 + Element Plus)

基于若依 RuoYi-Vue 3.8.2 改造,**后端版本未升级**(仍是 Spring Boot 2.5.14 / JDK 11),
在基线之上完成了:前端 **Vue2 → Vue3**、新增**锁屏解锁**、数据库**迁移到 PostgreSQL**、
集成 **Flowable 6.8.0 流程引擎**、新增 **`web-mp` 移动端**。

> **当前检出分支:`master-extend-flowable`**。
> 它和 `master` 指向**同一个提交**,内容完全一致;这个分支名表示在若依基线之上做了
> 「前端 Vue3 + 流程引擎 + 移动端 + PostgreSQL」这套扩展,详见[第二章](#二项目演进readme-描述的范围)。
> 同一仓库的其它分支用不同技术栈组合实现同样的功能(见[第五章](#五其他分支参考)),
> 本文档**只描述当前分支**的内容,不描述其它分支。

**文档导航**

| 文档 | 内容 |
|---|---|
| 本 README | 技术栈、分支差异、数据库脚本(第六章)、流程引擎集成(第七章) |
| [docs/流程引擎使用说明.md](docs/流程引擎使用说明.md) | **怎么用流程引擎**:建模 → 配审批人 → 发起 → 审批(同意/驳回/退回/转办/委派/加签/抄送) → 结束,以及排障 |

---

## 前端说明

本分支的前端有两套,均为 **JavaScript 版**(无 TypeScript):

| 端 | 目录 | 技术栈 | 说明 |
|---|---|---|---|
| PC / 管理端 | `web-ui` | Vue 3.5.26 + Element Plus 2.13.1 + Vite 6.4.1 | 含流程设计器、表单设计器 |
| 移动端 | `web-mp` | Vue 3.4.21 + uview-plus 3.3.x + uniapp 3（Vite 5.2.x） | 一套代码编译到 H5 / 微信小程序 / App |

- 代码为纯 `.js` / `.vue`,**`web-ui/package.json` 不含 `typescript` 依赖**
- TS 版在 `springboot2_TypeScript` / `springboot3_TypeScript` / `springboot4_TypeScript` 分支,本分支**未涉及**
- 移动端工程约定(目录结构、CLI 命令)见 [web-mp/README.md](web-mp/README.md)

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
| **Flowable** | **6.8.0**(`flowable-spring-boot-starter-process`,见第七章) |
| PostgreSQL | 15.1(driver `org.postgresql` + Druid) |

---

## 二、项目演进(README 描述的范围)

本项目从 **若依 RuoYi-Vue 3.8.2**(Spring Boot 2.5.14 / JDK 11 + Vue2 的原始版本)起步,当前分支共 9 个提交:

```
5b344b7  first commit                       ← 若依 RuoYi-Vue 3.8.2 原始
f3062b7  修复sql bug,锁屏解锁功能
9c2f6e5  md
ed72bbe  md                                 ← README 补写项目说明
c9d21e6  sql                                ← 交付 PostgreSQL 全库脚本 file-sql/basicdb-vue3.sql
e3b3356  md                                 ← README 补写数据库脚本说明
8fec771  feat(web-mp): 新增 Vue3 + uniapp 3 移动端，支持 H5 / 微信小程序同步
ab58b0c  chore(web-mp): 移除无效的 .env 文件（无代码引用）
876512c  chore(sql): 重新导出 basicdb-vue3.sql      ← 当前 HEAD
```

四个关键演进阶段:

| 阶段 | 做了什么 | 对应提交 |
|---|---|---|
| ① 前端 Vue3 化 | 前端由 Vue2 升级为 Vue3 + Element Plus + Vite(纯 JS) | `f3062b7` 前后 |
| ② PostgreSQL 交付 | 数据库由 MySQL 迁到 PostgreSQL,单文件脚本 `file-sql/basicdb-vue3.sql` 交付 | `c9d21e6`、`876512c` |
| ③ 流程引擎 | 接入 Flowable 6.8.0(`extend-flowable` 子模块 + `wf_*` 业务表 + `workflow:*` 权限) | 见下方说明 |
| ④ 移动端 | 新增 `web-mp` 移动端(Vue3 + uniapp 3,一套代码跑 H5 / 微信小程序 / App) | `8fec771`、`ab58b0c` |

> **后端版本线没有跟着升级**:仍是 Spring Boot 2.5.14 / JDK 11 / `javax.servlet`。
> 升级到 SB3(Jakarta EE、Spring Security 6)或 SB4 的版本在其它分支(见[第五章](#五其他分支参考))。
>
> **关于阶段 ③(流程引擎)**:Flowable 集成与配套的 `wf_*` 建表、`workflow:*` 菜单权限,
> 在当前分支的提交记录里**没有单独的 commit**——工作区里这些文件已存在但尚未提交
> (见第三章末尾的提示)。本文档按**工作区实际内容**描述。

---

## 三、本分支相对 RuoYi-Vue 3.8.2 原始版的改动

按改动性质分四类(不带具体 commit,只描述结果):

### 3.1 前端:Vue2 → Vue3

- 技术栈换成 Vue 3.5 + Element Plus 2.13 + Vite 6(纯 JavaScript,无 TS 依赖)
- 配套改造:Vuex → Pinia、Element UI 组件/图标写法迁移、`dicts` 选项改为 `useDict()` 组合式调用
- 流程设计器/表单设计器相关页面(`plugins/package/**`、`views/workflow/**`)全部按 Vue3 重写

### 3.2 功能:锁屏解锁

| 位置 | 内容 |
|---|---|
| 前端 | 导航栏「锁定屏幕」入口、`views/lock.vue` 锁屏页、路由守卫(`permission.js`)拦截未解锁访问、`store/modules/lock.js` 维护锁屏状态 |
| 后端 | `SysLoginController#unlockscreen`(`POST /unlockscreen`)+ `SysLoginService#unlockScreen`,校验当前登录用户密码后解锁 |
| 配置 | `sys_config` 里的 `user.password.lockOpen` / `user.password.maxRetryCount` / `user.password.lockTime` 控制**账号级**密码错误锁定(与上面的**页面锁屏**是两件事) |

### 3.3 数据库:MySQL → PostgreSQL

- 单文件交付:`file-sql/basicdb-vue3.sql`(schema `basicdb_flowable`,86 张表 + 2 视图)
- 配套修了 RuoYi 原生 SQL 在 PostgreSQL 下的语法问题(Mapper 中分页、`limit`、函数等差异)
- 细节见[第六章](#六数据库脚本-file-sqlbasicdb-vue3sql)

### 3.4 新增:Flowable 6.8.0 流程引擎

- 新增 Maven 子模块 `extend-flowable`,新增 `wf_*` / `tbl_flow_extend_procinst` 业务表
- 新增「流程引擎」菜单及整套 `workflow:*` 权限项
- 集成结构与扩展点见[第七章](#七flowable-流程引擎集成),使用说明见 [docs/流程引擎使用说明.md](docs/流程引擎使用说明.md)
- 节点「授权功能」可选值从原 7 项扩到 10 项(+ 终止 / 前加签 / 后加签;另含 `copy` 抄送标志,但抄送不渲染独立按钮,随同意/驳回一起提交);完整列表与设计器使用见 [§5.3](docs/流程引擎使用说明.md#53-授权功能决定审批时有哪些按钮)

### 3.5 新增:移动端 web-mp

- 与 `web-ui`(PC 端)并列的移动端应用:**Vue 3 + uview-plus 3 + uniapp 3**,同样是纯 JavaScript
- 一套代码可编译到 **H5 / 微信小程序 / App(Android、iOS)**
- 全部走 CLI(`pnpm dev:h5` / `pnpm dev:mp-weixin` / `pnpm dev:app`),不依赖 HBuilderX
- 工程说明见 [web-mp/README.md](web-mp/README.md)

> 第四章的「运行前 checklist」里带 `[ ]` 的条目表示**尚未验证通过**,请按你的实际环境勾选。

---

> ⚠️ **关于提交状态(重要)**
>
> 上面 3.3~3.5 描述的是**工作区(current checkout)的实际内容**,但它与 HEAD 提交**并不一致**:
>
> | 对象 | HEAD(`876512c`) | 工作区 |
> |---|---|---|
> | `file-sql/basicdb-vue3.sql` | schema `basicdb` / 33 张表 / 无 Flowable | schema `basicdb_flowable` / **86 张表 + 2 视图** / 含 Flowable |
> | `extend-flowable/`、`wf_*` 建表 | **未提交** | 已存在 |
> | `web-mp/` | 已提交(`8fec771`) | 已存在 |
>
> 也就是说:**你从 git 上 clone 到的版本,不含流程引擎和工作流表**。
> 本文档(以及 [docs/流程引擎使用说明.md](docs/流程引擎使用说明.md))按工作区内容编写;
> 若要把这套流程引擎能力交付出去,需要先把工作区改动提交(尤其
> `extend-flowable/`、`basicdb-vue3.sql`、`application*.yml` 与相关 Java 改动)。

---

## 四、运行前 checklist

- [x] JDK 11 安装
- [x] Node.js + pnpm(前端)
- [x] `cd web-ui && pnpm install` 拉依赖
- [ ] `pnpm dev` 验证启动
- [x] 7 个子模块 `parent.version` 一致为 `3.8.2`
- [x] IDE 工程的 Project SDK 设为 JDK 11,Language level 11
- [x] `mvn clean compile` 0 ERROR
- [ ] 应用启动成功
- [ ] admin 登录成功
- [ ] 锁屏功能可用(导航栏「锁定屏幕」→ 输入密码解锁,见 3.2)
- [ ] PostgreSQL 已导入 `file-sql/basicdb-vue3.sql`(86 张表 + 2 视图,见第六章)
- [ ] 流程引擎可用(菜单「流程引擎」能打开;能画一条流程并发起,见第七章)
- [ ] 移动端可启动(`cd web-mp && pnpm dev:h5`,见 3.5)

---

## 五、其他分支(参考)

同一仓库用**分支**承载不同技术栈组合,各分支的 `sql` 提交内容一致(同一份 PostgreSQL 脚本):

| 分支 | 相对当前分支的区别 |
|---|---|
| `master` | 与 `master-extend-flowable` 指向同一提交(内容相同) |
| `springboot2_TypeScript` | 后端同为 SB 2.5.14 / JDK 11,前端 `web-ui` 由 JS 改为 **TypeScript** |
| `springboot3` | 后端 SB 2.5.14 → 3.5.16(JDK 11 → 17),Jakarta EE + Spring Security 6 + springdoc 等 |
| `springboot3_TypeScript` | `springboot3` + 前端 TS 化 |
| `springboot4` | 后端 SB 3.5.16 → 4.1.1(JDK 17 → 21),Jackson 3 + Spring Security 7 + Spring Framework 7 等 |
| `springboot4_TypeScript` | `springboot4` + 前端 TS 化(技术栈最完整) |

> **移动端与流程引擎不是这些分支的差异点**:`web-mp` 与 `extend-flowable` 在当前分支内维护,
> 其它分支主要差在**后端版本线**与**前端是否 TS 化**上。切到对应分支看其 README 以获得准确说明。

---

## 六、数据库脚本 (`file-sql/basicdb-vue3.sql`)

> 本分支运行所需的 **PostgreSQL 全库结构 + 初始化数据** 都打在一个脚本里,统一用
> `file-sql/basicdb-vue3.sql`(原名 `dump-basicdb-vue3-202609301413.sql`,已重命名去掉时间戳)。
> 其它 `file-sql/*.sql` 是历史中间产物,本分支**不直接使用**;真正要执行的就是这一个。

> 脚本内容是**整个 `basicdb_flowable` schema 的结构 + 初始化数据**(包含 Flowable 引擎表、
> 工作流业务表、RuoYi 基础表、Quartz 表、代码生成器视图),用 `psql` 一次性导入即可,
> 不需要再单独跑 `quartz.sql` / `create-*.sql` 之类的补充脚本。

### 6.1 脚本基本信息

| 项 | 值 |
|---|---|
| 文件路径 | `file-sql/basicdb-vue3.sql` |
| 行数 | 9519 行 |
| 来源 | `pg_dump` 导出(导出自 PostgreSQL 15.1,导出工具 pg_dump 17.0) |
| 导出时间 | 2026-10-15 15:34:19(`-- Completed on` 行) |
| 数据库 | `basicdb-vue3` |
| Schema | **`basicdb_flowable`**(脚本里所有表/视图/函数/序列都在该 schema 下,**不是 `public`**) |
| 表数量 | **86 张** + **2 个视图** |
| 数据加载方式 | `INSERT INTO ... VALUES`(逐行插入,**不是** `COPY ... FROM stdin`) |
| 对象属主 | 表/视图/序列:部分 `postgres`、部分 `system`;函数:`system`。导入前**必须**先有 `system` 角色 |
| 函数数量 | 5 个(`date_format` / `exec` / `find_in_set` / `ifnull` / `tabledef`) |
| PostgreSQL 要求 | **>= 15.1**(用到了 `to_char` / `format` / `pg_get_partkeydef` 等 14+ 才稳定的写法) |
| 数据污染提醒 | 脚本**未脱敏**,含历史业务数据(`sys_logininfor` 登录日志、`sys_oper_log` 操作日志、`sys_user` 里的 `test001`/`test002`、若干流程实例与审批意见)。生产环境导入后请按需清理 |

> 脚本开头是 `DROP ...` 段(依赖倒序),**导入会先删掉同名对象再重建**。
> 导入到已有库等同于覆盖,请先备份。

### 6.2 对象清单(共 86 张表 + 2 个视图,均在 `basicdb_flowable` schema)

#### 流程引擎 —— Flowable 引擎表(`act_*`,33 张)

部署与模型:`act_re_deployment` / `act_re_procdef` / `act_re_model` / `act_ge_bytearray` /
`act_ge_property` / `act_procdef_info` / `act_evt_log` /
`act_de_model` / `act_de_model_history` / `act_de_model_relation`

运行时:`act_ru_execution` / `act_ru_task` / `act_ru_actinst` / `act_ru_identitylink` /
`act_ru_entitylink` / `act_ru_variable` / `act_ru_event_subscr` /
`act_ru_job` / `act_ru_timer_job` / `act_ru_suspended_job` / `act_ru_deadletter_job` /
`act_ru_external_job` / `act_ru_history_job`

历史:`act_hi_procinst` / `act_hi_taskinst` / `act_hi_actinst` / `act_hi_identitylink` /
`act_hi_entitylink` / `act_hi_varinst` / `act_hi_detail` / `act_hi_comment` /
`act_hi_attachment` / `act_hi_tsk_log`

#### 流程引擎 —— Flowable 事件注册表(`flw_*`,9 张)

`flw_ev_databasechangelog` / `flw_ev_databasechangeloglock` / `flw_event_definition` /
`flw_event_deployment` / `flw_event_resource` / `flw_channel_definition` /
`flw_ru_batch` / `flw_ru_batch_part` / `flw_expression`(本项目用作**表达式字典**)

#### 工作流业务表(本项目自研,10 张)

| 表 | 用途 |
|---|---|
| `wf_category` | 流程分类(树形) |
| `wf_category_org_link` | 流程分类与组织关联(保留表,前端已去此业务定制) |
| `wf_comment_info` | 审批意见(与 `act_hi_comment` 同步写) |
| `wf_copy` | 流程抄送记录 |
| `wf_deploy_form` | **部署时的表单快照**(审批读的是这里,不是 `wf_form`) |
| `wf_form` | 流程表单定义(vForm3 JSON) |
| `wf_form_category` | 表单分类 |
| `wf_form_template` | 表单模板(JSON 素材库) |
| `wf_task_form` | 每个实例/节点填写的表单数据(**脚手架兜底做法**,生产应改存业务表,见 [§7.8](#78-wf_task_form-不应存业务数据)) |
| `tbl_flow_extend_procinst` | 流程实例业务扩展(发起人、流程状态、业务 key 等) |

#### 系统基础表(`sys_*`,23 张)

| 表 | 用途 |
|---|---|
| `sys_config` | 全局参数 |
| `sys_dept` | 部门 |
| `sys_dict_data` / `sys_dict_type` | 字典值 / 字典类型 |
| `sys_demo` | 演示样例 |
| `sys_files` | 附件(含流程附件:`task_id` / `process_instance_id` / `activity_id`) |
| `sys_i18n` | 国际化文案 |
| `sys_job` / `sys_job_log` | 定时任务 / 定时任务日志 |
| `sys_logininfor` | 登录日志 |
| `sys_menu` | 菜单(含 `is_resource` / `show_tags` / `menu_name_en` / `menu_name_zh_hant` 等扩展列,并含整套 `workflow:*` 权限项与菜单) |
| `sys_notice` | 通知公告 |
| `sys_notice_read` | 公告已读记录(含序列 `sys_notice_read_read_id_seq`、唯一索引 `uk_sys_notice_read_notice_user`) |
| `sys_oper_log` | 操作日志 |
| `sys_post` | 岗位 |
| `sys_role` | 角色 |
| `sys_role_dept` / `sys_role_menu` | 角色-部门 / 角色-菜单关联 |
| `sys_user` / `sys_user_post` / `sys_user_role` | 用户 / 用户-岗位 / 用户-角色关联 |

> ⚠️ 与早期版本 README 的差异:脚本里**没有** `sys_sms_message` 表,请勿按旧文档找它。

#### 定时任务(`qrtz_*`,11 张)

`qrtz_blob_triggers` / `qrtz_calendars` / `qrtz_cron_triggers` / `qrtz_fired_triggers` /
`qrtz_job_details` / `qrtz_locks` / `qrtz_paused_trigger_grps` / `qrtz_scheduler_state` /
`qrtz_simple_triggers` / `qrtz_simprop_triggers` / `qrtz_triggers`

#### 代码生成(`gen_*`,2 张 + 2 视图)

| 对象 | 用途 |
|---|---|
| `gen_table` | 代码生成业务表配置 |
| `gen_table_column` | 代码生成字段配置 |
| **视图** `list_table` | 库表清单(生成器"导入表"用) |
| **视图** `list_column` | 库表字段清单(生成器读字段用) |

> 视图的 `n.nspname` 过滤条件写死为 `basicdb_flowable`,**schema 名不能改**,
> 否则代码生成器读不到表。

### 6.3 恢复步骤(全新数据库)

> 以下命令假设 PostgreSQL 监听 `localhost:5432`,超级用户 `postgres`,密码在环境变量
> `PGPASSWORD` 里。**整套流程只会创建 `system` 角色 + `basicdb-vue3` 库 + `basicdb_flowable`
> schema + 表/视图/函数/数据**,不会触碰 `postgres` 自带的对象。

```bash
# 1) 建 system 角色和 basicdb-vue3 库(SQL 脚本本身不建角色和库,必须先建好)
psql -U postgres -h localhost -c "CREATE ROLE system LOGIN PASSWORD 'system';"
psql -U postgres -h localhost -c "CREATE DATABASE \"basicdb-vue3\" OWNER system;"

# 2) 导入 schema + 数据
psql -U system -h localhost -d basicdb-vue3 -f file-sql/basicdb-vue3.sql

# 3) 验证(应返回 86)
psql -U system -h localhost -d basicdb-vue3 -c \
  "SELECT count(*) FROM information_schema.tables WHERE table_schema='basicdb_flowable' AND table_type='BASE TABLE';"

# 4) 顺便确认两个生成器视图也在(应返回 2)
psql -U system -h localhost -d basicdb-vue3 -c \
  "SELECT count(*) FROM information_schema.views WHERE table_schema='basicdb_flowable';"
```

常见报错:

| 报错 | 原因 | 处理 |
|---|---|---|
| `ERROR: role "system" does not exist` | 没建 `system` 角色 | 补执行第 1 步的 `CREATE ROLE` |
| `ERROR: schema "basicdb_flowable" already exists` | 导入了已有库 | 脚本里 `CREATE SCHEMA` 前没有 `DROP SCHEMA`,先手工 `DROP SCHEMA basicdb_flowable CASCADE;` 或换空库 |
| 建视图报 `permission denied for schema basicdb_flowable` | 导入账号不是 schema 属主 | 用 `postgres` 或 `system` 执行,或先 `ALTER SCHEMA basicdb_flowable OWNER TO system;` |

### 6.4 备份该数据库的命令(导出成同款脚本)

```bash
pg_dump -U system -h localhost -d basicdb-vue3 \
  --schema=basicdb_flowable --no-owner --no-privileges \
  --file=file-sql/basicdb-vue3.sql
```

- 导出时**必须**用 `--schema=basicdb_flowable` 限定的方式;脚本里所有对象都带 schema 前缀,
  靠 `search_path` 是导不出这种形态的。
- 加 `--no-owner --no-privileges` 后,导出的脚本不再带 `OWNER TO ...` / `GRANT ...`,可移植性更好
  (但要保证导入账号对 `basicdb_flowable` 有 ALL 权限)。仓库现存的脚本是**不加**这两个开关导出的,
  因此带 `OWNER TO postgres` / `OWNER TO system`,导入前必须先有 `system` 角色。
- 脚本用的是 `INSERT INTO`,因此用 GUI 工具的「执行 SQL」也能导(不像 `COPY` 那样必须走 `psql`);
  但仍建议用 `psql -f`,错误信息更完整。

### 6.5 默认账号

| 用户名 | 密码(明文) | 备注 |
|---|---|---|
| `admin` | `admin123` | 超级管理员,登录后请立刻改密 |

(`admin` 账号的密文在 `sys_user.password`,默认与 RuoYi-Vue 3.8.2 原始脚本一致;
要重置可执行同目录的 `reset-admin-password.sql`。)

### 6.6 与应用连接配置(实际生效的是 `application-dev.yml`)

后端连接的关键配置在 [basic-admin-api/src/main/resources/application-dev.yml](basic-admin-api/src/main/resources/application-dev.yml):

```yaml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    druid:
      master:
        url: jdbc:postgresql://<host>:<port>/basicdb-vue3?useSSL=false&currentSchema=basicdb_flowable
        username: system
        password: '<密码>'
        validationQuery: SELECT 1
```

> **`currentSchema=basicdb_flowable` 必须加**,否则 MyBatis 找不到 `sys_user` / `sys_menu` /
> `act_ru_task` 等表(所有对象都建在该 schema 下,不是 `public`)。
>
> `application-prod.yml` 里目前还是 **MySQL** 连接串(`jdbc:mysql://.../yjy_base`),
> 部署 PostgreSQL 环境时请勿直接沿用,需按上面的形式改成 PostgreSQL。

### 6.7 其它 `file-sql/*.sql` 说明

| 文件 | 角色 |
|---|---|
| `basicdb-vue3.sql` | **唯一需要执行的脚本**(库结构 + 初始化数据 + 流程引擎对象) |
| `reset-admin-password.sql` | 把 `admin` 密码重置回 `admin123` 的小工具,按需手动跑 |
| `reset-all-passwords.sql` | 把**所有**用户密码统一重置回默认的小工具,慎用 |

> 早期文档里提到过的 `basic-sql-2023-03-02.sql` / `quartz.sql` / `ry_20210908.sql` /
> `fix-sys-menu-component.sql` / `create-sys-notice-read.sql` 在当前分支的 `file-sql/` 下
> **已不存在**:Quartz 建表、`sys_notice_read` 补建、菜单 component 修复**都已被
> `basicdb-vue3.sql` 内置覆盖**,不需要再手工执行。

> 一句话:**运行本项目,只跑 `basicdb-vue3.sql` 这一个文件就够了。**

---

## 七、Flowable 流程引擎集成

引擎:**Flowable 6.8.0**。流程模块是独立 Maven 子模块 `extend-flowable`,由 `basic-admin-api` 依赖并装配。

> **使用层面**的操作说明(怎么画流程、怎么审批、怎么退回/转办/委派/前加签/后加签/会签回退/终止)
> 已单独成文:**[docs/流程引擎使用说明.md](docs/流程引擎使用说明.md)**。
> 本章只讲集成结构、配置与扩展点,面向二次开发与部署运维。

### 7.1 技术栈与依赖

| 项 | 内容 |
|---|---|
| 引擎 | `flowable-spring-boot-starter-process` 6.8.0 |
| 建模 | `flowable-json-converter`、`flowable-dmn-json-converter` |
| Modeler 复用 | `flowable-ui-common`、`flowable-ui-modeler-logic`(复用 Flowable 官方建模器逻辑与 MyBatis 映射) |
| 监控 | `flowable-spring-boot-starter-actuator` |
| 图形 | `org.activiti:activiti-bpmn-layout` + `activiti-engine`(自定义流程图的自动排版) |
| 版本管理 | 父 `pom.xml` 统一声明 `<flowable.version>6.8.0</flowable.version>` |

依赖清单见 [extend-flowable/pom.xml](extend-flowable/pom.xml),子模块注册在 [pom.xml](pom.xml) 的 `<modules>` 中。

### 7.2 引擎配置(application.yml)

```yaml
flowable:
  async-executor-activate: true    # 开启异步执行器(定时器任务、异步服务任务依赖它)
  database-schema-update: true     # 启动时自动升级引擎表结构到该版本所需形态
  idm:
    enabled: false                 # 关闭 Flowable 自带 IDM:用户/角色用 RuoYi 的 sys_user/sys_role
  common:
    app:
      idm-url: http://localhost:8080/flowable-demo
      idm-admin:
        user: test
        password: test
```

另外还有两处与流程相关的配套配置:

| 配置 | 位置 | 作用 |
|---|---|---|
| `spring.cache.ehcache.config: classpath:/ehcache/flow-ehcache.xml` | `application.yml` | 流程定义/模型缓存 |
| `mybatis-plus.mapperLocations` 追加 `classpath:/META-INF/flow-mybatis-mappings/*/xml/*.xml`、`classpath:/META-INF/modeler-mybatis-mappings/*.xml` | `application.yml` | 加载 Flowable Modeler 的 MyBatis 映射 |
| `configuration-properties: {blobType: BLOB, boolValue: TRUE}` | `application.yml` | Modeler 映射里引用的占位符(PG 用 `BLOB`/`TRUE`) |

> `idm.enabled: false` 是**关键决策**:审批人体系完全走 `sys_user` / `sys_role`,
> 节点上的人员信息以 Flowable 扩展属性(`flowable:IdmAssignee` / `IdmCandidateUsers` /
> `IdmCandidateGroups`)保存,由本项目自己的 Service 解析成引擎的 assignee / candidate 关系。

### 7.3 分层架构

```
前端 web-ui/src/views/workflow/*            10 个页面(模型/表单/分类/表达式/待办/已办/已发/抄送/待签收/流程中心)
   │   /workflow/**  REST
   ▼
Controller  com.basic.workflow.controller   WfModelController / WfDeployController / WfFormController /
                                            WfFormCategoryController / WfFormTemplateController /
                                            WfExpressionController (+ basic-admin-api 的 WfTaskController / WfProcessController)
   ▼
Service     com.basic.workflow.service.*    WfModelService / WfTaskService / WfProcessDetailService /
                                            WfToDoTaskService / WfCopyService / WfFormService /
                                            rollBack(退回策略) / wfModel.save(设计器保存与校验)
   ▼
引擎门面    FlowServiceFactory              统一暴露 RepositoryService / RuntimeService /
                                            TaskService / HistoryService / ManagementService / ProcessEngine
   ▼
Flowable 6.8.0 引擎  →  basicdb_flowable.act_* / flw_*
```

### 7.4 关键类与职责

| 类 | 路径 | 职责 |
|---|---|---|
| `FlowServiceFactory` | `flowable/factory/FlowServiceFactory.java` | 引擎各 Service 的统一注入门面 |
| `ActivitiEngineUtil` | `flowable/factory/ActivitiEngineUtil.java` | 引擎工具方法 |
| `FlowableConfig` | `flowable/flow/FlowableConfig.java` | 覆盖引擎默认行为(如流程图字体、生成器) |
| `FlowBpmnConfig` / `FlowExtensionConfig` | `flowable/config/` | BPMN 解析与自定义扩展属性注册 |
| `GlobalEventListenerConfig` / `GlobalEventListener` | `flowable/config/`、`flowable/listener/` | 全局事件监听:流程/任务事件统一记录审批意见等 |
| `UserTaskListener` | `flowable/listener/UserTaskListener.java` | 用户任务创建时的处理人解析、跳过/为空跳过等扩展逻辑 |
| `ProcessDueTimeListener` | `flowable/listener/ProcessDueTimeListener.java` | 任务到期时间 |
| `CustomProcessDiagramGenerator` / `CustomProcessDiagramCanvas` | `flowable/flow/` | 生成带高亮的流程图(已办/待办/驳回不同颜色) |
| `FlowableUtils` / `TaskUtils` / `ProcessFormUtils` | `flowable/flow/`、`flowable/utils/` | 流程与任务通用工具 |
| `WfModelSaveXmlService` | `workflow/service/wfModel/save/` | **设计器保存入口**:BPMN 合规校验 + 保存 + 部署 |
| `RollbackCmd` + `RollbackOperateStrategy` 系列 | `workflow/cmd/rollBack/` | **退回/回退**的策略实现(普通节点、多实例、网关、调用活动、子流程各有策略) |
| `AddHisCommentCmd` | `workflow/cmd/AddHisCommentCmd.java` | 写审批意见 |
| `InjectUserTaskInProcessInstanceCmd` / `ReplaceUserTaskInProcessInstanceCmd` | `workflow/cmd/` | 运行中**加签**(前加签注入、后加签替换) |
| `DynamicUserTaskBuilder` / `BaseDynamicSubProcessInjectUtil` | `workflow/builder/`、`workflow/dynamic/` | 动态构建/注入用户任务节点(加签、动态子流程) |
| `Bruce*JsonConverter` / `CustomBpmnJsonConverter` | `converter/` | 让 Flowable Modeler 的 JSON ↔ BPMN 转换认识本项目的自定义扩展属性 |
| `DatabaseConfiguration` / `ModelerBeanConfig` / `FlowableModelRepositoryImpl` | `flowable/config/modeler/`、`flowable/model/` | 复用 Flowable Modeler 的仓储与数据源配置 |

**前端关键文件(节点「授权功能」+ 审批按钮)**:三类必须同步改,详见 [docs/流程引擎使用说明.md §5.3](docs/流程引擎使用说明.md#53-授权功能决定审批时有哪些按钮):

| 文件 | 作用 |
|---|---|
| [`web-ui/src/plugins/package/penal/task/task-components/UserTask.vue`](web-ui/src/plugins/package/penal/task/task-components/UserTask.vue) | 流程设计器"节点 → 授权功能"复选框候选项(字典没配时走这里兜底) |
| [`web-ui/src/views/workflow/process/components/actionMeta.js`](web-ui/src/views/workflow/process/components/actionMeta.js) | 审批按钮元数据(标签/颜色/提示/权限);`buildActionButtons()` 把节点 `idmPermission` 翻译成按钮 |
| [`web-ui/src/views/workflow/process/components/TaskDialog.vue`](web-ui/src/views/workflow/process/components/TaskDialog.vue) | 统一审批弹窗(意见/原因/人员/退回节点);`switch (action)` 把按钮映射到具体 API |



### 7.5 自定义扩展点(节点上的业务语义)

流程节点上除了标准 BPMN 属性,还挂了一批 `flowable:` 命名空间的自定义扩展属性,定义在
[ExtensionConstant.java](extend-flowable/src/main/java/com/basic/constant/ExtensionConstant.java):

| 扩展属性 | 业务含义 |
|---|---|
| `flowable:IdmPermission` | 节点「授权功能」(可用操作按钮的 JSON 数组),前端据此渲染审批按钮。可选值 `complete` / `reject` / `return` / `transfer` / `delegate` / `copy` / `mulitiSequentialUnComplete` / **`addSignBefore` / `addSignAfter` / `stop`**(后 3 项并入"授权功能"是 2025-Q4 改的;前端元数据见 [`actionMeta.js`](web-ui/src/views/workflow/process/components/actionMeta.js)) |
| `flowable:IdmAssignee` / `IdmCandidateUsers` / `IdmCandidateGroups` / `IdmAuthority` | 审批人来源(单个 / 候选人 / 候选角色 / 固定值角色) |
| `flowable:IsSkip` / `IsNullSkip` | 处理人自动跳过 / 为空跳过 |
| `flowable:IsExamined` | 同一处理人只需审批一次 |
| `flowable:IsRoleReplace` | 连续节点处理人相同则自动跳过 |
| `flowable:IsRevoke` | 允许撤回已有人审的会签节点 |
| `flowable:IsUserScreen` / `IsOrgScreen` / `IsDeptOrOrg` | 人员按组织/部门筛选 |

流程级自定义变量在 [ProcessConstants.java](extend-flowable/src/main/java/com/basic/flowable/common/constant/ProcessConstants.java):

| 常量 | 用途 |
|---|---|
| `processStatus` | 流程状态变量(`running` / `finished` / `stop` / `cancel`),列表与流程中心都读它 |
| `_FLOWABLE_SKIP_EXPRESSION_ENABLED` | 节点跳过表达式开关 |
| `skipExpression` | 跳过表达式 |
| `userList` | 会签人员集合变量 |
| `SUFFIX = ".bpmn"` | 流程定义 key 与资源名的后缀约定 |

### 7.6 后端接口清单(主要)

| 分组 | 前缀 | 典型接口 |
|---|---|---|
| 流程模型 | `/workflow/model` | `list`、`listModelLaunch`(可发起)、`save`、`deploy`、`bpmnXml`、`historyList`、`latest`、`suspended`、`activate`、`publishModel`(发起) |
| 流程定义 | `/workflow/deploy` | `list`、`publishList`、`changeState`、`deployForm` |
| 流程实例/任务 | `/workflow/process` | `todoList`、`finishedList`、`ownList`、`copyList`、`claimList`、`detail`、`getFlowViewer`、`historyProcNodeList`、`getProcessCommentInfo`、`selectProcessPrcInstance`(流程中心) |
| 任务操作(审批动作) | `/workflow/task` | 见下方「审批动作 ↔ 接口」对照表 |
| 节点表单 | `/workflow/taskForm` | `saveProcessForm`、`selectFormDataByProcInsIdToList`、`selectFormJsonDataByTaskIdAndActIdToList`、`delProcessForm` |
| 表单 | `/workflow/form` | `list`、`getFormByCode`、`saveForm`、`delForm`、`getVFormWidgetList` |
| 表单分类/模板 | `/system/category`(表单分类)、`/workflow/formTemplate` | `list`、`saveFormTemplate`、`delFormTemplate` |
| 流程分类 | `/workflow/category` | `list`、`listAll`、`getCategoryOption`、`checkCategoryCodeUnique` |
| 表达式 | `/workflow/expression` | `list`、`export` |

#### 审批动作 ↔ 后端接口(完整)

| 动作 | 前端 action | 节点「授权功能」值 | 后端接口 | 备注 |
|---|---|---|---|---|
| 同意 | `complete` | `complete` | `POST /workflow/task/complete` | 同时可填意见/变量/抄送 |
| 驳回 | `reject` | `reject` | `POST /workflow/task/reject` | 状态 → 已驳回 |
| 退回 | `return` | `return` | `POST /workflow/task/return` + `/returnList` | 退回节点由下拉给出 |
| 转办 | `transfer` | `transfer` | `POST /workflow/task/transfer` | — |
| 委派 | `delegate` | `delegate` | `POST /workflow/task/delegate` | — |
| 会签回退 | `mulitiSequentialUnComplete` | `mulitiSequentialUnComplete` | `POST /workflow/task/mulitiSequentialUnComplete` | 仅顺序会签 |
| 前加签 | `addSignBefore` | `addSignBefore` | `POST /workflow/task/beforeAddSignTask` | — |
| 后加签 | `addSignAfter` | `addSignAfter` | `POST /workflow/task/afterAddSignTask` | — |
| 终止 | `stop` | `stop` | `POST /workflow/task/stopProcess` | 流程中心也可批量调 |
| 默认驳回上一节点 | — | — | `POST /workflow/task/rejectLastTask` | 快捷驳回 |
| 跳过节点 | — | — | `POST /workflow/task/skipUserTask` | 需 `skipActivityId` + `procInsId` |
| 变更处理人 | — | — | `POST /workflow/task/editProcessTaskAssignee` | 改 `taskId` + `assignee` |
| 修改审批意见 | — | — | `POST /workflow/task/updateTaskComment` | 已办意见纠错 |
| 删除流程实例 | — | — | `DELETE /workflow/task/deleteProcessByIds/{ids}` | 测试数据清理 |
| 删除单个任务 | — | — | `POST /workflow/task/deleteProcessTask` | 测试数据清理 |
| 撤回 | — | — | `POST /workflow/task/revokeProcess` | 发起人在「我的已发」 |
| 废除 | — | — | `POST /workflow/task/abolishProcess` | 流程中心 |
| 签收/取消签收 | — | — | `POST /workflow/task/claim` / `unClaim` | — |

> 任务类接口的请求体统一携带 `currentUserId` / `currentUserName`(前端 `api/workflow/task.js`
> 的 `withUser()` 自动注入),后端用它写操作人与审批意见。
>
> 「授权功能」列表示**该动作必须在节点「授权功能」里勾上**,按钮才会出现在审批页(详见 [docs/流程引擎使用说明.md §5.3](docs/流程引擎使用说明.md#53-授权功能决定审批时有哪些按钮))。

### 7.7 数据库表对照

| 分组 | 表 | 说明 |
|---|---|---|
| 引擎-模型 | `act_de_model*`、`act_re_model` | 设计器模型(含历史版本) |
| 引擎-部署 | `act_re_deployment`、`act_re_procdef`、`act_ge_bytearray`、`act_ge_property`、`act_procdef_info` | 部署产物与 BPMN/PNG 资源 |
| 引擎-运行 | `act_ru_*` | 执行流、任务、变量、任务关系、各类 Job |
| 引擎-历史 | `act_hi_*` | 实例、任务、节点、变量、审批意见、附件、参与人 |
| 业务 | `wf_category`、`wf_comment_info`、`wf_copy`、`wf_deploy_form`、`wf_form`、`wf_form_category`、`wf_form_template`、`wf_task_form`、`tbl_flow_extend_procinst`、`flw_expression` | 见 6.2 节对象清单 |

**注意表分工**:审批时的表单读 `wf_deploy_form`(部署快照),填写值存 `wf_task_form`;
审批意见同时写 `act_hi_comment`(引擎)和 `wf_comment_info`(业务)。

> ⚠️ **`wf_task_form` 不应存业务数据**(脚手架的临时兜底,生产项目应改造)。
> 详见 [§7.8](#78-wf_task_form-不应存业务数据)。

### 7.8 `wf_task_form` 不应存业务数据

> **本节是架构警示**:脚手架把整个表单 JSON 塞进 `wf_task_form.form_content` 是为了**快速跑通流程**的**临时兜底**,
> **生产项目应改造**——业务字段值落各自的业务表,`wf_task_form` 只存流程视角的元数据。
> 完整论述与改造示例见 [docs/流程引擎使用说明.md §6.3.1](docs/流程引擎使用说明.md#631-重要业务数据应存业务表不要塞进-wf_task_form)。

#### 7.8.1 为什么不能把业务数据存到 `wf_task_form`

| 问题 | 影响 |
|---|---|
| `form_content` 是 JSON 文本,业务字段无类型约束 | `days` 字段理论上能存 `"abc"`,数据库不会拒绝 |
| 无法用 B-tree 索引,业务查询全走 `jsonb ->> '字段'` 解析 | PG 虽能建 GIN 索引,但 SELECT 性能比普通列差一个数量级 |
| 无法 JOIN | 业务表(请假/报销)与流程表(用户/部门)JOIN 困难,关联查询全卡在 JSON 解析上 |
| 数据膨胀快 | 一条流程 5 个节点 = 5 行 JSON,业务上只算 1 条数据;表会迅速变大 |
| 业务流程结束后,数据还赖在 `wf_task_form` | 这表是「节点级」粒度,大部分行是历史垃圾,清理困难 |
| 业务报表/聚合查询全部要解析 JSON | `SELECT AVG((form_content->>'days')::int) FROM wf_task_form WHERE ...` |

#### 7.8.2 正确架构

- **业务数据**(请假天数 / 报销金额 / 出差原因 / 附件 ID 等)→ **存到各自的业务表**
  (例如 `sys_leave` / `sys_expense_report` 等),用普通列、`NOT NULL` 约束、外键、索引,
  业务查询 `SELECT * FROM sys_leave WHERE ...` 直接走索引
- **业务表与流程的关联**用 `proc_ins_id` 字段做「软关联」
- **`wf_task_form` 表只放流程视角需要的元数据**:
  节点 ID、任务 ID、当时填写的**控件名清单**(`activityId` / `taskId` / `userId` / 控件路径),
  或者干脆**只放 `proc_ins_id` + `form_id` + 业务表主键 ID 的引用**,
  **不要把业务字段值塞进 JSON 文本**

#### 7.8.3 业务查询对照

```sql
-- ❌ 错的(走 JSON 解析,无索引,慢)
SELECT * FROM wf_task_form
WHERE form_content::jsonb ->> 'status' = 'approved';

-- ✅ 对的(走业务表普通列,带索引,快)
SELECT * FROM sys_leave
WHERE approval_status = 'approved';
```

#### 7.8.4 改造示例(脚手架之外的代码,需自行实现)

```java
// ❌ 脚手架的兜底做法(把所有控件值塞进 JSON)
WfTaskForm wfTaskForm = new WfTaskForm(taskId, formDataJson, procInsId, ...);
wfTaskFormService.saveFormData(wfTaskForm);

// ✅ 推荐做法:业务数据落业务表,流程表只存关联
LeaveRequest leave = new LeaveRequest();
leave.setUserId(userId);
leave.setDays(formData.getInteger("days"));      // 直接存到业务表
leave.setReason(formData.getString("reason"));   // 直接存到业务表
leave.setProcInsId(procInsId);                    // 软关联
leaveService.save(leave);

WfTaskForm wfTaskForm = new WfTaskForm();
wfTaskForm.setTaskId(taskId);
wfTaskForm.setProcInsId(procInsId);
wfTaskForm.setFormId(formId);
wfTaskForm.setActivityId(activityId);
wfTaskForm.setUserId(userId);
// 不再 setFormContent
wfTaskFormService.saveFormData(wfTaskForm);
```

#### 7.8.5 表名速查

| 表 | 存什么 | 改造后 |
|---|---|---|
| `wf_form.content` | 表单定义(vForm3 JSON 源) | 不变 |
| `wf_deploy_form.content` | 部署时的表单快照(实例实际用这份) | 不变 |
| `wf_task_form.form_content` | 整个表单 JSON 文本 | **应改造**:只存元数据(`taskId` / `activityId` / `userId` / 控件名清单)或干脆不放 |
| `sys_leave` / `sys_expense_report` / ... | — | **新增业务表**:`proc_ins_id` 软关联 + 普通列存业务字段 |

> 💡 **一句话**:`wf_task_form` 是流程引擎通用表,**不是**业务数据的主存位置;
> 业务字段值存业务表(走索引),`wf_task_form` 只存流程视角的元数据。
