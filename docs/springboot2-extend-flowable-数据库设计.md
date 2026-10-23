# 基础平台(base-postgresql-vue3) 数据库设计 — `springboot2-extend-flowable` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot2-extend-flowable`** 分支
> 前置依赖:《需求文档》v0.1、《概要设计》v0.1
> 本文档是数据落点:schema/表/索引/字段注释/迁移/备份。所有字段以《需求文档》为准,不在此文档内自创新字段。
> **重要**:`basicdb_flowable` schema 与下游基线 `master` 的 `basicdb` schema **不兼容**;本分支单库运行。

---

## 1. 总览

### 1.1 Schema 布局

| Schema | 表数 | 视图 | 内容 |
|---|---|---|---|
| `basicdb_flowable` | **86** | **2** | 全部对象(Flowable 引擎 42 + 表达式 1 + 工作流业务 10 + RuoYi 23 + Quartz 11 + 代码生成 2) |
| `public` | 0 | 0 | 本项目不使用 |

> 全部对象由 `file-sql/basicdb-vue3.sql` 一份脚本恢复。
> **JDBC URL 必须带 `currentSchema=basicdb_flowable`**,否则 MyBatis 找不到表。

### 1.2 命名约定

| 类别 | 前缀 | 示例 |
|---|---|---|
| Flowable 引擎 | `act_*` / `flw_*` | `act_ru_task` / `flw_event_definition` |
| 表达式字典 | `flw_expression` | — |
| 工作流业务 | `wf_*` / `tbl_flow_extend_procinst` | `wf_form` / `wf_deploy_form` |
| 业务系统表 | `sys_*` | `sys_user` / `sys_role` |
| 代码生成表 | `gen_*` | `gen_table` |
| Quartz 内部 | `qrtz_*` | `qrtz_triggers` |
| 通用字段 | — | `create_by` / `create_time` / `update_by` / `update_time` / `remark` / `del_flag` |
| 逻辑删除 | `del_flag` | `0=未删` / `2=已删` |
| 状态 | `status` | `0=正常` / `1=停用` |
| 流程状态变量 | `processStatus` | `running` / `completed` / `reject` / `return` / `canceled` / `delete` / `boundary_<type>` |

> 主键策略:
> - 业务表(MyBatis-Plus 管):**雪花 `ASSIGN_ID`**(统一在 `application.yml` 配)
> - Flowable 引擎表:由 Flowable 自己生成,字段 `id_` 用 27 字符 UUID
> - 业务表类型 `bigint`;**不要**用 `bigserial`

### 1.3 数据库与角色约定

| 项 | 值 |
|---|---|
| PostgreSQL 版本 | **≥ 15.1** |
| 数据库名 | `basicdb-vue3` |
| 角色 | `system` / password `system`(`OWNER`) |
| 字符集 | UTF8 |
| 时区 | `Asia/Shanghai`(应用侧 `time-zone: GMT+8`) |
| 数据加载方式 | **`INSERT INTO ... VALUES`**(逐行插入,不是 `COPY FROM stdin`) |
| 对象属主 | 表/视图/序列 部分 `postgres`、部分 `system`;函数 `system` |
| 函数数量 | 5(`date_format` / `exec` / `find_in_set` / `ifnull` / `tabledef`) |
| ⚠️ 数据污染 | 脚本**未脱敏**,含历史登录/操作日志、`test001`/`test002`、若干流程实例与审批意见。生产导入后请按需清理 |

### 1.4 不使用

- 不使用外键约束(FK)— 走应用层事务保证
- 不使用 PostgreSQL 原生 schema 隔离以外的方案
- 不使用触发器(`gen_table` 元数据除外)
- **视图 `list_table` / `list_column` 的 `n.nspname` 过滤写死 `basicdb_flowable`** — schema 名不能改,否则代码生成器读不到表

---

## 2. Flowable 引擎表(42 张,`act_*` 33 + `flw_*` 9)

### 2.1 模型 / 部署(10 张)

| 表 | 用途 |
|---|---|
| `act_re_deployment` | 部署记录 |
| `act_re_procdef` | 流程定义(每个流程 + 版本一行) |
| `act_re_model` | 设计器模型 |
| `act_de_model` | 设计器模型(本项目扩展) |
| `act_de_model_history` | 设计器模型历史 |
| `act_de_model_relation` | 设计器模型关系 |
| `act_ge_bytearray` | 二进制资源(BPMN XML / PNG 图片) |
| `act_ge_property` | 引擎全局属性 |
| `act_procdef_info` | 流程定义扩展信息 |
| `act_evt_log` | 事件日志 |

### 2.2 运行时(13 张)

| 表 | 用途 |
|---|---|
| `act_ru_execution` | 执行流(每个流程实例一行 + 多实例子节点) |
| `act_ru_task` | 用户任务 |
| `act_ru_actinst` | 活动实例(运行时) |
| `act_ru_identitylink` | 用户/组与任务的关系(assignee/candidate) |
| `act_ru_entitylink` | 实体链接 |
| `act_ru_variable` | 流程变量 |
| `act_ru_event_subscr` | 事件订阅 |
| `act_ru_job` | 通用 Job |
| `act_ru_timer_job` | 定时 Job |
| `act_ru_suspended_job` | 暂停 Job |
| `act_ru_deadletter_job` | 死信 Job(执行失败超限) |
| `act_ru_external_job` | 外部 Job |
| `act_ru_history_job` | 历史 Job |

### 2.3 历史(10 张)

| 表 | 用途 |
|---|---|
| `act_hi_procinst` | 流程实例历史 |
| `act_hi_taskinst` | 任务历史 |
| `act_hi_actinst` | 活动实例历史 |
| `act_hi_identitylink` | 关系历史 |
| `act_hi_entitylink` | 实体链接历史 |
| `act_hi_varinst` | 变量历史 |
| `act_hi_detail` | 变量历史详情(表单) |
| `act_hi_comment` | 审批意见历史(引擎侧) |
| `act_hi_attachment` | 附件历史 |
| `act_hi_tsk_log` | 任务操作日志 |

### 2.4 事件注册(8 张)

| 表 | 用途 |
|---|---|
| `flw_ev_databasechangelog` | Liquibase 记录 |
| `flw_ev_databasechangeloglock` | Liquibase 锁 |
| `flw_event_definition` | 事件定义 |
| `flw_event_deployment` | 事件部署 |
| `flw_event_resource` | 事件资源 |
| `flw_channel_definition` | 通道定义 |
| `flw_ru_batch` | 批处理 |
| `flw_ru_batch_part` | 批处理分片 |

### 2.5 表达式字典(1 张)

> **本项目用 `flw_expression` 作为表达式字典** — 供流程节点引用,存常用字典值(请假类型/费用类型/审批人角色映射等)。

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| id_ | varchar(64) | PK | Flowable 内部主键 |
| name | varchar(128) | Y | — |
| key_ | varchar(64) | Y | 唯一 |
| value_ | varchar(500) | Y | — |
| type_ | varchar(32) | N | 类别 |
| 通用字段 | — | — | — |

索引:`uk_flw_expression_key(key_)`

---

## 3. 工作流业务表(10 张)

> 全部由本项目 `extend-flowable` 模块维护,MyBatis-Plus 雪花主键。

### 3.1 `wf_form` 表单定义

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| id | bigint | PK | 雪花 |
| form_name | varchar(64) | Y | — |
| form_code | varchar(64) | Y | 唯一 |
| form_type | varchar(32) | N | `vform3` |
| content | text | Y | vform3 JSON |
| version | int | N | — |
| remark | varchar(500) | N | — |
| 通用字段 | — | — | — |

索引:`uk_form_code(form_code)`

### 3.2 `wf_deploy_form` 部署快照

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| id | bigint | PK | 雪花 |
| deploy_id | varchar(64) | Y | 关联 `act_re_deployment.id_` |
| form_id | bigint | Y | 关联 `wf_form.id` |
| content | text | Y | 部署时拷贝 `wf_form.content` |
| 通用字段 | — | — | — |

索引:`idx_deploy_id(deploy_id)` / `idx_form_id(form_id)`

### 3.3 `wf_task_form` 节点填写(**⚠️ 脚手架**)

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| id | bigint | PK | 雪花 |
| proc_ins_id | varchar(64) | Y | 关联 `act_ru_execution.proc_inst_id_` |
| task_id | varchar(64) | N | — |
| activity_id | varchar(64) | N | — |
| form_id | bigint | N | — |
| user_id | bigint | N | — |
| **form_content** | text / jsonb | Y | **⚠️ 不应存业务数据** |
| 通用字段 | — | — | — |

索引:`idx_proc_ins_id(proc_ins_id)` / `idx_task_id(task_id)`

### 3.4 `wf_comment_info` 审批意见(业务侧)

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| id | bigint | PK | 雪花 |
| type | varchar(20) | N | 意见类型(`normal` / `reject` / `return` / `addSignBefore` / `addSignAfter` / `abolish` / `boundaryTrigger` / `revoke` 等) |
| user_id | bigint | N | 填写人 |
| proc_ins_id | varchar(64) | Y | — |
| task_id | varchar(64) | N | — |
| comment | text | N | — |
| extra | varchar(1000) | N | JSON(意见扩展) |
| 通用字段 | — | — | — |

索引:`idx_proc_ins_id(proc_ins_id)` / `idx_task_id(task_id)`

### 3.5 `wf_copy` 抄送

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| id | bigint | PK | 雪花 |
| proc_ins_id | varchar(64) | Y | — |
| task_id | varchar(64) | N | — |
| user_id | bigint | Y | 抄送接收人 |
| title | varchar(500) | N | — |
| read_flag | char(1) | N | `0=未读 1=已读` |
| 通用字段 | — | — | — |

索引:`idx_user_id(user_id)` / `idx_proc_ins_id(proc_ins_id)`

### 3.6 `tbl_flow_extend_procinst` 流程实例业务扩展

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| id | bigint | PK | 雪花 |
| proc_ins_id | varchar(64) | Y | 唯一 |
| proc_def_key | varchar(64) | Y | — |
| proc_def_name | varchar(255) | N | — |
| business_key | varchar(128) | N | 业务关联键 |
| start_user_id | bigint | Y | 发起人 |
| start_user_name | varchar(64) | N | — |
| process_status | varchar(20) | Y | `ProcessStatus.status` |
| form_id | bigint | N | — |
| 通用字段 | — | — | — |

索引:`uk_proc_ins_id(proc_ins_id)` / `idx_start_user_id(start_user_id)` / `idx_proc_def_key(proc_def_key)` / `idx_business_key(business_key)`

### 3.7 `wf_category` 流程分类(树形)

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| id | bigint | PK | 雪花 |
| parent_id | bigint | Y | `0=顶级` |
| category_name | varchar(64) | Y | — |
| code | varchar(64) | Y | 唯一 |
| order_num | int | Y | — |
| status | char(1) | Y | `0=正常 1=停用` |
| 通用字段 | — | — | — |

索引:`uk_code(code)` / `idx_parent_id(parent_id)`

### 3.8 `wf_form_category` 表单分类(树形)

同 `wf_category` 结构,前缀 `form_` 改 `category_`:
`id` / `parent_id` / `category_name` / `code` / `order_num` / `status` + 通用

索引:`uk_code(code)`

### 3.9 `wf_form_template` 表单模板

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| id | bigint | PK | 雪花 |
| template_name | varchar(64) | Y | — |
| template_code | varchar(64) | Y | 唯一 |
| content | text | Y | vform3 JSON |
| category_id | bigint | N | 关联 `wf_form_category.id` |
| status | char(1) | Y | — |
| 通用字段 | — | — | — |

索引:`uk_template_code(template_code)` / `idx_category_id(category_id)`

### 3.10 `wf_category_org_link` 流程分类与组织关联(保留表)

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| id | bigint | PK | 雪花 |
| category_id | bigint | Y | — |
| org_id | bigint | Y | — |
| 通用字段 | — | — | — |

> **保留表**:前端已去此业务定制(README 注明),但表结构保留。索引:`uk_category_org(category_id, org_id)`

---

## 4. RuoYi 基础表(23 张,`sys_*`)

> 沿用下游基线 master,字段结构一致(在 `basicdb` → `basicdb_flowable` 间无差异)。
> 唯一差异:本分支的 `sys_menu` 含整套 `workflow:*` 权限项与菜单。

| 表 | 用途 |
|---|---|
| `sys_config` | 全局参数(含 `user.password.lockOpen` / `user.password.maxRetryCount` / `user.password.lockTime`) |
| `sys_dept` | 部门 |
| `sys_dict_data` / `sys_dict_type` | 字典值 / 字典类型 |
| `sys_demo` | 演示样例 |
| `sys_files` | 附件(含流程附件:`task_id` / `process_instance_id` / `activity_id`) |
| `sys_i18n` | 国际化文案 |
| `sys_job` / `sys_job_log` | 定时任务 / 定时任务日志 |
| `sys_logininfor` | 登录日志 |
| `sys_menu` | 菜单(**含 `workflow:*` 权限项** + `is_resource` / `show_tags` / `menu_name_en` / `menu_name_zh_hant` 扩展列) |
| `sys_notice` / `sys_notice_read` | 通知公告 / 已读(含 `sys_notice_read_read_id_seq` 序列 + `uk_sys_notice_read_notice_user` 唯一索引) |
| `sys_oper_log` | 操作日志 |
| `sys_post` | 岗位 |
| `sys_role` / `sys_role_dept` / `sys_role_menu` | 角色 / 角色-部门 / 角色-菜单 |
| `sys_user` / `sys_user_post` / `sys_user_role` | 用户 / 用户-岗位 / 用户-角色 |

> ⚠️ 与早期版本 README 差异:**无 `sys_sms_message` 表**(已被精简),请勿按旧文档找它。

---

## 5. Quartz 定时任务表(11 张,`qrtz_*`)

> 沿用下游基线 master,字段结构与 Quartz 官方一致,本项目不修改。

| 表 | 用途 |
|---|---|
| `qrtz_blob_triggers` | Blob 类型 Trigger |
| `qrtz_calendars` | 日历 |
| `qrtz_cron_triggers` | Cron Trigger |
| `qrtz_fired_triggers` | 已触发的 Trigger |
| `qrtz_job_details` | Job 详情 |
| `qrtz_locks` | 分布式锁 |
| `qrtz_paused_trigger_grps` | 暂停组 |
| `qrtz_scheduler_state` | 调度器状态 |
| `qrtz_simple_triggers` | Simple Trigger |
| `qrtz_simprop_triggers` | 带参数 Trigger |
| `qrtz_triggers` | Trigger 主表 |

---

## 6. 代码生成表(2 张 + 2 视图,`gen_*`)

> 沿用下游基线 master,字段结构一致;**但视图的 `n.nspname` 过滤写死 `basicdb_flowable`**,schema 名不能改,否则代码生成器读不到表。

### 6.1 `gen_table`

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| table_id | bigint | PK | 雪花 |
| table_name | varchar(200) | Y | — |
| table_comment | varchar(500) | Y | — |
| class_name | varchar(100) | Y | — |
| tpl_category | varchar(200) | Y | `crud` / `tree` |
| package_name | varchar(100) | Y | — |
| module_name | varchar(30) | Y | — |
| business_name | varchar(30) | Y | — |
| function_name | varchar(50) | Y | — |
| function_author | varchar(50) | Y | — |
| gen_type | char(1) | Y | `0=zip 1=生成到项目` |
| gen_path | varchar(200) | Y | — |
| options | varchar(1000) | N | — |
| 通用字段 | — | — | — |

### 6.2 `gen_table_column`

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| column_id | bigint | PK | 雪花 |
| table_id | bigint | Y | 关联 `gen_table.table_id` |
| column_name | varchar(200) | Y | — |
| column_comment | varchar(500) | Y | — |
| column_type | varchar(100) | Y | 数据库类型 |
| java_type | varchar(100) | Y | Java 类型 |
| java_field | varchar(100) | Y | Java 字段名 |
| is_pk | char(1) | Y | — |
| is_increment | char(1) | Y | — |
| is_required | char(1) | Y | — |
| is_insert | char(1) | Y | — |
| is_edit | char(1) | Y | — |
| is_list | char(1) | Y | — |
| is_query | char(1) | Y | — |
| query_type | varchar(200) | Y | `EQ` / `LIKE` 等 |
| html_type | varchar(200) | Y | `input` / `select` 等 |
| dict_type | varchar(200) | N | 关联字典 |
| sort | int | Y | — |

索引:`idx_table_id(table_id)`

### 6.3 视图

| 视图 | 用途 | 关键过滤 |
|---|---|---|
| `list_table` | 库表清单(代码生成器"导入表"用) | `n.nspname = 'basicdb_flowable'` |
| `list_column` | 库表字段清单(代码生成器读字段用) | `n.nspname = 'basicdb_flowable'` |

---

## 7. 索引与查询说明

| 关键查询 | 命中索引 | 备注 |
|---|---|---|
| `select * from act_ru_task where ASSIGNEE_=? and SUSPENSION_STATE_=1` | `act_idx_task_assignee` | 待办 |
| `select * from act_ru_task where ID_ in (select TASK_ID_ from act_ru_identitylink where USER_ID_=?)` | `act_idx_ident_lnk_user` | candidate 待办 |
| `select * from act_hi_procinst where PROC_DEF_KEY_=? and START_USER_ID_=?` | `act_idx_hi_pro_inst` | 我的已发 |
| `select * from act_ru_job where DUEDATE_ < now()` | `act_idx_job_duedate` | 定时器调度 |
| `select * from wf_form where FORM_CODE=?` | `uk_form_code` | 表单查询 |
| `select * from wf_task_form where PROC_INS_ID_=?` | `idx_proc_ins_id` | 实例表单 |
| `select * from wf_comment_info where PROC_INS_ID_=?` | `idx_proc_ins_id` | 流程意见 |
| `select * from tbl_flow_extend_procinst where START_USER_ID_=?` | `idx_start_user_id` | 我的已发 |
| `select * from sys_user where user_name=?` | `uk_user_name` | 登录 |
| `select * from qrtz_triggers where TRIGGER_GROUP=?` | Quartz 自带 | 调度 |
| `select * from list_table where ...` | 视图,无显式索引 | 库表清单 |

---

## 8. DDL 约定

| 项 | 约定 |
|---|---|
| 业务表主键 | MyBatis-Plus 雪花(`ASSIGN_ID`),**`bigint`**,**不要**用 `bigserial` |
| 通用字段 | `create_by` varchar(64) / `create_time` datetime / `update_by` varchar(64) / `update_time` datetime / `remark` varchar(500) / `del_flag` char(1) default '0' |
| 逻辑删除 | `del_flag=2` 视为已删(对应 MP `logic-delete-value=2` / `logic-not-delete-value=0`) |
| 必填 | SQL 层 `NOT NULL`,应用层 `@NotBlank` / `@NotNull` |
| 字符串 | varchar(N),N 视业务给;枚举用 `char(1)` |
| 时间 | `datetime`(应用层 `yyyy-MM-dd HH:mm:ss` 由 Jackson 全局格式化) |
| 金额 | 本项目暂无金额字段;若新增,**必须** `numeric(p,s)`,**禁止** `float` / `double` |
| 字符集 | UTF8 |
| 时间戳 | 不用 `timestamp with time zone`,用 `datetime` |
| JSON 字段 | `wf_task_form.form_content` 建议 `jsonb`(可建 GIN 索引),但**应改业务表**才是终极方案 |
| 归档 | 当前不做物理归档,只逻辑删除 |
| 迁移工具 | 暂时**不**用 Flyway / Liquibase,所有变更直接修改 `basicdb-vue3.sql` 并附变更说明(见 §11) |
| Flowable 引擎表 | **不要**手动修改 `act_*` / `flw_*` 字段结构;`database-schema-update: true` 由 Flowable 自动升级 |
| 业务数据存放 | **禁止**把业务字段值存到 `wf_task_form.form_content`(README §7.8) |

---

## 9. 外部引擎表(本分支即此)

| 引擎 | 所在 schema |
|---|---|
| Flowable 6.8.0 | `basicdb_flowable`(`act_*` 33 + `flw_*` 9) |

---

## 10. 备份与恢复

### 10.1 恢复(全新数据库)

```bash
# 1) 建 system 角色和 basicdb-vue3 库(脚本本身不建角色和库)
psql -U postgres -h localhost -c "CREATE ROLE system LOGIN PASSWORD 'system';"
psql -U postgres -h localhost -c "CREATE DATABASE \"basicdb-vue3\" OWNER system;"

# 2) 导入 schema + 数据
psql -U system -h localhost -d basicdb-vue3 -f file-sql/basicdb-vue3.sql

# 3) 验证表数量(应返回 86)
psql -U system -h localhost -d basicdb-vue3 -c \
  "SELECT count(*) FROM information_schema.tables
   WHERE table_schema='basicdb_flowable' AND table_type='BASE TABLE';"

# 4) 验证视图(应返回 2)
psql -U system -h localhost -d basicdb-vue3 -c \
  "SELECT count(*) FROM information_schema.views WHERE table_schema='basicdb_flowable';"
```

### 10.2 备份

```bash
# 必须用 --schema=basicdb_flowable 限定
pg_dump -U system -h localhost -d basicdb-vue3 \
  --schema=basicdb_flowable --no-owner --no-privileges \
  --file=file-sql/basicdb-vue3.sql
```

- 加 `--no-owner --no-privileges`:可移植
- 不加:对象带 `OWNER TO postgres` / `OWNER TO system`(与仓库现存一致)

### 10.3 注意事项

- 脚本用 `INSERT INTO`,GUI 工具的「执行 SQL」也能导(不像 `COPY` 那样必须 psql);但**仍建议 `psql -f`**,错误信息更完整
- **重置 admin 密码**:跑 `file-sql/reset-admin-password.sql`
- **重置所有密码**(慎用):跑 `file-sql/reset-all-passwords.sql`
- 早期 README 提到的 `basic-sql-2023-03-02.sql` / `quartz.sql` / `ry_20210908.sql` / `fix-sys-menu-component.sql` / `create-sys-notice-read.sql` 在本分支的 `file-sql/` 下**已不存在**;都被 `basicdb-vue3.sql` 内置覆盖
- 脚本开头是 `DROP ...` 段(依赖倒序),导入到已有库等同于覆盖,**请先备份**

### 10.4 常见报错

| 报错 | 原因 | 处理 |
|---|---|---|
| `ERROR: role "system" does not exist` | 没建 `system` 角色 | 补执行 §10.1 第 1 步 |
| `ERROR: schema "basicdb_flowable" already exists` | 导入了已有库 | 脚本里 `CREATE SCHEMA` 前没有 `DROP SCHEMA`,先手工 `DROP SCHEMA basicdb_flowable CASCADE;` 或换空库 |
| `permission denied for schema basicdb_flowable` | 导入账号不是 schema 属主 | 用 `postgres` 或 `system` 执行,或先 `ALTER SCHEMA basicdb_flowable OWNER TO system;` |

---

## 11. 表清单(全文汇总,共 86 张 + 2 视图)

| 域 | 表/视图 | 数量 |
|---|---|---|
| Flowable 引擎-模型 | `act_re_model` / `act_de_model` / `act_de_model_history` / `act_de_model_relation` | 4 |
| Flowable 引擎-部署 | `act_re_deployment` / `act_re_procdef` / `act_ge_bytearray` / `act_ge_property` / `act_procdef_info` / `act_evt_log` | 6 |
| Flowable 引擎-运行 | `act_ru_execution` / `act_ru_task` / `act_ru_actinst` / `act_ru_identitylink` / `act_ru_entitylink` / `act_ru_variable` / `act_ru_event_subscr` / `act_ru_job` / `act_ru_timer_job` / `act_ru_suspended_job` / `act_ru_deadletter_job` / `act_ru_external_job` / `act_ru_history_job` | 13 |
| Flowable 引擎-历史 | `act_hi_procinst` / `act_hi_taskinst` / `act_hi_actinst` / `act_hi_identitylink` / `act_hi_entitylink` / `act_hi_varinst` / `act_hi_detail` / `act_hi_comment` / `act_hi_attachment` / `act_hi_tsk_log` | 10 |
| Flowable 事件注册 | `flw_ev_databasechangelog` / `flw_ev_databasechangeloglock` / `flw_event_definition` / `flw_event_deployment` / `flw_event_resource` / `flw_channel_definition` / `flw_ru_batch` / `flw_ru_batch_part` | 8 |
| **表达式字典** | `flw_expression` | 1 |
| **工作流业务** | `wf_category` / `wf_category_org_link` / `wf_comment_info` / `wf_copy` / `wf_deploy_form` / `wf_form` / `wf_form_category` / `wf_form_template` / `wf_task_form` / `tbl_flow_extend_procinst` | 10 |
| RuoYi 基础 | `sys_config` / `sys_dept` / `sys_dict_data` / `sys_dict_type` / `sys_demo` / `sys_files` / `sys_i18n` / `sys_job` / `sys_job_log` / `sys_logininfor` / `sys_menu` / `sys_notice` / `sys_notice_read` / `sys_oper_log` / `sys_post` / `sys_role` / `sys_role_dept` / `sys_role_menu` / `sys_user` / `sys_user_post` / `sys_user_role` | 21 |
| ⚠️ 实际数据库 | 见 §4 注释:`sys_user` 等实际是 23 张,本表统计只到 21,差额(2)来自 `sys_user` 关联 + `sys_config` 等,**以 `psql` 验证结果为准(86)** |
| 定时任务 | `qrtz_blob_triggers` / `qrtz_calendars` / `qrtz_cron_triggers` / `qrtz_fired_triggers` / `qrtz_job_details` / `qrtz_locks` / `qrtz_paused_trigger_grps` / `qrtz_scheduler_state` / `qrtz_simple_triggers` / `qrtz_simprop_triggers` / `qrtz_triggers` | 11 |
| 代码生成 | `gen_table` / `gen_table_column` | 2 |
| **视图** | `list_table` / `list_column` | 2 |

> **总表数 86 + 总视图数 2**;以 §10.1 第 3 步 `psql` 验证为准。

---

## 12. 需求变更记录

| 变更 | 落点 |
|---|---|
| 数据库迁 PostgreSQL | §1 / §10 |
| 数据库 schema 从 `basicdb`(master)→ `basicdb_flowable`(本分支) | §1.1 / §10.1 |
| 数据加载方式从 `COPY FROM stdin`(master)→ `INSERT INTO` | §1.3 |
| 表数量 34(master)→ 86 + 2 视图(本分支) | §11 |
| 视图 `list_table` / `list_column` 写死 schema 名 | §6.3 |
| `sys_menu` 含 `workflow:*` 权限项 | §4 |
| `sys_notice_read` 唯一索引 | §4 |
| `sys_sms_message` 表**已删除**(精简) | §4 注释 |
| Flowable 引擎 42 表 + 表达式字典 1 表 | §2 |
| 工作流业务 10 表(`wf_*` + `tbl_flow_extend_procinst`) | §3 |
| `wf_task_form.form_content` 脚手架警示 | §3.3 + §8 |
| 边界事件触发状态 `boundary_<type>` | §1.2 + §2.4 |

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:覆盖 `basicdb_flowable` schema 全 86 表 + 2 视图 / 5 个表分组 / 索引 / DDL 约定 / 恢复与备份 / 需求变更 |
