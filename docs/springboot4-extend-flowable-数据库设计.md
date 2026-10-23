# 基础平台(base-postgresql-vue3) 数据库设计 — `springboot4-extend-flowable` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot4-extend-flowable`** 分支
> 前置依赖:《需求文档》v0.1、《概要设计》v0.1
> 本文档是数据落点:schema / 表 / 索引 / 字段注释 / 迁移 / 备份。
> **本分支相对 `springboot4` 的核心变化 = schema 从 `basicdb`(34 表)换成 `basicdb_flowable8`(90 表 + 2 视图)**,新增 56 个流程相关对象;平台 34 张表全部保留且结构不变。

---

## 1. 总览

### 1.1 Schema 布局

| Schema | 表 | 视图 | 内容 |
|---|---|---|---|
| **`basicdb_flowable8`** | **90** | **2** | 全部对象(Flowable 引擎 39 + 事件注册 7 + 工作流业务 9 + 实例扩展 1 + 平台 34) |
| `public` | 0(本项目不使用) | 0 | — |

> 全部对象由 `file-sql/basicdb-vue3.sql`(376.8 KB)一份脚本恢复。
> 🔶 **JDBC URL 必须带 `currentSchema=basicdb_flowable8`**,且 `spring.flowable.database-schema` **必须同值** —— 两处不一致会导致 Flowable 把 39 张 `act_*` 建到默认 schema。
> 🔶 **已实测核对**:`^CREATE TABLE` = **90**;`^CREATE VIEW` = **2**。

### 1.2 对象分布(实测)

| 前缀 | 数量 | 归属 | 说明 |
|---|---|---|---|
| **`act_*`** | **39** | Flowable 引擎 | 模型/部署/运行时/历史/通用/事件日志 |
| **`flw_*`** | **7** | Flowable 事件注册 + 本项目表达式 | `flw_event_*` / `flw_channel_definition` / `flw_ru_batch*` / **`flw_expression`** |
| **`wf_*`** | **9** | **本项目工作流业务** | 分类/表单/部署快照/节点填写/意见/抄送 |
| **`tbl_flow_extend_procinst`** | **1** | **本项目** | 流程实例业务扩展 |
| `sys_*` | 21 | 平台(⬆️ 不变) | RBAC / 字典 / 通知 / 审计 |
| `qrtz_*` | 11 | Quartz(⬆️ 存在,RAMJobStore 不用) | — |
| `gen_*` | 2 | 代码生成(⬆️ 不变) | — |
| **合计表** | **90** | | |
| **视图** | **2** | 代码生成器 | `list_table` / `list_column` |

### 1.3 命名约定

| 类别 | 前缀 | 示例 |
|---|---|---|
| Flowable 引擎 | `act_*` / `flw_*` | `act_re_model` / `act_ru_task` / `flw_event_definition` |
| **本项目工作流** | `wf_*` / `tbl_flow_*` | `wf_form` / `wf_deploy_form` / `tbl_flow_extend_procinst` |
| 平台系统 | `sys_*` | `sys_user` / `sys_role` |
| 定时任务 | `qrtz_*` | `qrtz_triggers` |
| 代码生成 | `gen_*` | `gen_table` |
| 通用字段 | — | `create_by` / `create_time` / `update_by` / `update_time` / `remark` / `del_flag` |
| 逻辑删除 | `del_flag` | `0=未删` / `2=已删` |
| 流程状态 | `process_status` | `running` / `reject` / `return` / `completed` / `canceled` / `delete` / `boundary_<type>` |

> 主键策略:
> - **平台/工作流业务表**:MyBatis-Plus 雪花 `ASSIGN_ID`(bigint)
> - **Flowable 引擎表**:由 Flowable 自己维护(`id_ varchar(64)`,UUID 风格)
> - **不要**手改 `act_*` / `flw_*` 的主键或加 `bigserial`

### 1.4 数据库与角色约定

| 项 | 值 |
|---|---|
| PostgreSQL 版本 | **≥ 15.1** |
| 角色 | `system` / password `system`(`OWNER`) |
| 库 | `basicdb-vue3` |
| **schema** | **`basicdb_flowable8`** |
| 字符集 | 默认 UTF8 |
| 时区 | `Asia/Shanghai`(应用侧 `time-zone: GMT+8`) |
| 数据加载方式 | `COPY ... FROM stdin`(**只能**用 `psql` 导入) |
| 函数数量 | **5**(`date_format` / `exec` / `find_in_set` / `ifnull` / `tabledef`) |
| 视图 | **2**(`list_table` / `list_column`) |

### 1.5 🔶 本分支独有说明 — **相对 `springboot4` 的变化**

| 项 | `springboot4` | **本分支** | 变化 |
|---|---|---|---|
| schema | `basicdb` | **`basicdb_flowable8`** | **🔶 换名** |
| 表数量 | 34 | **90** | **🔶 +56** |
| 视图 | 0 | **2** | **🔶 +2** |
| `act_*` | 0 | **39** | **🔶 新增** |
| `flw_*` | 0 | **7** | **🔶 新增** |
| `wf_*` | 0 | **9** | **🔶 新增** |
| `tbl_flow_extend_procinst` | 0 | **1** | **🔶 新增** |
| `sys_*` / `qrtz_*` / `gen_*` | 34 | 34 | **不变(结构一致)** |
| `act_de_*` | — | **无** | Flowable 8 无 designER 建模器 |
| 函数 | 5 | 5 | 不变 |
| SQL 脚本 | `basicdb-vue3.sql` | 同(内容重建,376.8 KB) | **🔶 重导** |

### 1.6 🔶 为什么换 schema 而不是共用 `basicdb`

| 方案 | 优点 | 缺点 | 选择 |
|---|---|---|---|
| 共用 `basicdb`(34 + 56 混在一起) | 少一份 schema | **清盘/迁移风险高**(误删平台表);Flowable 表与平台表命名混;备份粒度粗 | ❌ |
| **独立 `basicdb_flowable8`** | 流程数据隔离;**清盘只动流程表**;备份/迁移可分开;多环境可只重置流程 | 需要两处配置一致(`currentSchema` + `database-schema`) | ✅ **采用** |
| 独立数据库 | 物理隔离最强 | 跨库事务不可用;运维成本翻倍 | ❌ |

---

## 2. Flowable 引擎表 — `act_*`(39 张)

> 字段结构由 **Flowable 8.0.0 官方 DDL** 决定,本项目**不改**;`database-schema-update: true` 时由引擎自动建/升级。

### 2.1 模型与部署(6 张)

| 表 | 用途 | 🔶 本分支要点 |
|---|---|---|
| `act_re_model` | 流程模型(modelKey / name / version / category) | **模型主表**(BPMN XML 存在 `act_ge_bytearray`) |
| `act_re_deployment` | 部署记录 | 每次 deploy 一行 |
| `act_re_procdef` | 流程定义(每版本一行) | `SUSPENSION_STATE_` 1=激活 / 2=挂起 |
| `act_ge_bytearray` | 二进制资源 | **BPMN XML + 生成的 PNG** |
| `act_ge_property` | 引擎全局属性 | 版本号等 |
| `act_procdef_info` | 流程定义扩展 | — |

> ⚠️ **无 `act_de_model` / `act_de_model_history` / `act_de_model_relation`** —— 这是 6.8 分支的 designER 表,Flowable 8 已移除(AC-47)。
> 本项目的"模型"完全靠 `act_re_model` + `act_ge_bytearray` 组合表达。

### 2.2 运行时(13 张)

| 表 | 用途 | 🔶 本分支要点 |
|---|---|---|
| `act_ru_execution` | 执行流(流程实例 + 子执行) | 多实例 / 子流程会增行 |
| `act_ru_task` | 用户任务(待办) | **待办列表主表**;`ASSIGNEE_` / `TASK_DEF_KEY_` |
| `act_ru_actinst` | 活动实例(运行时) | — |
| `act_ru_identitylink` | 用户/组与任务关系 | assignee / candidate / owner |
| `act_ru_entitylink` | 实体链接 | — |
| `act_ru_variable` | 流程变量 | **`processStatus` 存在这里** |
| `act_ru_event_subscr` | 事件订阅 | 边界事件(signal/message)订阅 |
| `act_ru_job` | 通用 Job | 异步任务 |
| **`act_ru_timer_job`** | **定时 Job** | 🔶 **边界事件 timer 落这里,重启可恢复** |
| `act_ru_suspended_job` | 暂停 Job | — |
| `act_ru_deadletter_job` | 死信 Job | 执行失败超限 |
| `act_ru_external_job` | 外部 Job | 本项目不用 External Worker |
| `act_ru_history_job` | 历史 Job | 异步历史 |

### 2.3 历史(10 张)

| 表 | 用途 | 🔶 本分支要点 |
|---|---|---|
| `act_hi_procinst` | 流程实例历史 | **`START_USER_ID_` 记录发起人**(依赖 `Authentication.setAuthenticatedUserId`) |
| `act_hi_taskinst` | 任务历史 | 已办列表主表 |
| `act_hi_actinst` | 活动历史 | **退回校验"是否走过"依赖此表** |
| `act_hi_identitylink` | 关系历史 | — |
| `act_hi_entitylink` | 实体链接历史 | — |
| `act_hi_varinst` | 变量历史 | — |
| `act_hi_detail` | 变量详情(表单) | — |
| **`act_hi_comment`** | **审批意见(引擎侧)** | 🔶 **与 `wf_comment_info` 双写** |
| `act_hi_attachment` | 附件历史 | — |
| `act_hi_tsk_log` | 任务操作日志 | — |

> 🔶 **`history-level: audit`** 决定了上述历史表的填充粒度;改为 `none` 会导致流程图/意见缺失。

### 2.4 其它(10 张)

| 表 | 用途 |
|---|---|
| `act_evt_log` | 事件日志 |
| `act_id_*`(9 张,**可能被建**) | Flowable IDM 用户/组表;`idm.enabled: false` 后**仍可能被建**(历史版本/首次初始化),**不影响运行** |

> ⚠️ `act_id_*` 不在 39 张统计内(它们属于 IDM 模块,本项目关闭)。若被建出,**可无视**,也可手工 drop。

### 2.5 🔶 `act_*` 表的使用红线

| 禁止 | 原因 |
|---|---|
| 手工 `INSERT` / `UPDATE` `act_ru_*` | 会破坏引擎内存状态一致性 |
| 手工改 `act_re_procdef` 的 `VERSION_` | 版本链断裂 |
| 直接 `DELETE` `act_ru_task` | 任务不消失,流程卡死 |
| 给 `act_*` 加外键 | 与引擎删除顺序冲突 |
| 改 `act_*` 字段类型 | `database-schema-update` 可能升级失败 |

**允许**:只读查询(本项目的 `mapper` 里有部分 `act_*` 只读 SQL);清盘时 `TRUNCATE`(走专用 Service)。

---

## 3. Flowable 事件注册表 — `flw_*`(7 张)

| 表 | 用途 | 🔶 本分支 |
|---|---|---|
| `flw_event_definition` | 事件定义 | 引擎自动 |
| `flw_event_deployment` | 事件部署 | 引擎自动 |
| `flw_event_resource` | 事件资源 | 引擎自动 |
| `flw_channel_definition` | 通道定义 | 引擎自动 |
| `flw_ru_batch` | 批处理 | 引擎自动 |
| `flw_ru_batch_part` | 批处理分片 | 引擎自动 |
| **`flw_expression`** | **表达式字典(本项目自有)** | **🔶 业务表** |

### 3.1 `flw_expression`(表达式字典,本项目自有)

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| id_ | varchar(64) | PK | 主键 |
| name | varchar(128) | Y | 名称 |
| key_ | varchar(64) | Y | 唯一标识 |
| value_ | varchar(500) | Y | 表达式值 |
| type_ | varchar(32) | N | 类型 |
| 通用字段 | — | — | — |

索引:`uk_flw_expression_key(key_)`

> 🔶 **删除保护**:被流程模型引用的表达式**不可删**(`WfExpressionServiceImpl` 校验,AC-60)。

---

## 4. 本项目工作流业务表 — `wf_*`(9 张)

> 全部由 `extend-flowable` 维护,MyBatis-Plus 雪花主键。

### 4.1 `wf_category` 流程分类(树形)

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| id | bigint | PK | 雪花 |
| parent_id | bigint | Y | `0=顶级` |
| category_name | varchar(64) | Y | 名称 |
| code | varchar(64) | Y | 唯一 |
| order_num | int | Y | 排序 |
| status | char(1) | Y | `0=正常 1=停用` |
| 通用字段 | — | — | — |

索引:`uk_wf_category_code(code)` / `idx_parent_id(parent_id)`

### 4.2 `wf_category_org_link` 分类与组织关联

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| id | bigint | PK | 雪花 |
| category_id | bigint | Y | 分类 |
| org_id | bigint | Y | 组织(部门) |
| 通用字段 | — | — | — |

索引:`uk_category_org(category_id, org_id)`

### 4.3 `wf_form` 表单定义

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| id | bigint | PK | 雪花 |
| form_name | varchar(64) | Y | 表单名 |
| **form_code** | varchar(64) | Y | **唯一**,节点通过它绑定 |
| form_type | varchar(32) | N | `vform3` |
| **content** | text | Y | **vform3 JSON** |
| version | int | N | 版本 |
| remark | varchar(500) | N | — |
| 通用字段 | — | — | — |

索引:`uk_wf_form_code(form_code)`

> ⚠️ **改 `content` 后必须重新部署**,否则在跑实例仍用 `wf_deploy_form` 旧快照(AC-59 / R39)。

### 4.4 `wf_deploy_form` 部署表单快照

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| id | bigint | PK | 雪花 |
| **deploy_id** | varchar(64) | Y | 关联 `act_re_deployment.id_` |
| form_id | bigint | Y | 关联 `wf_form.id` |
| **content** | text | Y | **部署时拷贝的 `wf_form.content`** |
| 通用字段 | — | — | — |

索引:`idx_wf_deploy_form_deploy_id(deploy_id)` / `idx_wf_deploy_form_form_id(form_id)`

> 🔶 **审批读这张表,不读 `wf_form`** —— 这是"快照隔离"的关键设计(HD-37)。

### 4.5 `wf_task_form` 节点填写(**⚠️ 脚手架用法**)

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| id | bigint | PK | 雪花 |
| proc_ins_id | varchar(64) | Y | 关联 `act_ru_execution.proc_inst_id_` |
| task_id | varchar(64) | N | 关联 `act_ru_task.id_` |
| activity_id | varchar(64) | N | 节点 id |
| form_id | bigint | N | 关联 `wf_form.id` |
| user_id | bigint | N | 填写人 |
| **form_content** | text | Y | **⚠️ 存的是业务数据** |
| 通用字段 | — | — | — |

索引:`idx_wf_task_form_proc_ins(proc_ins_id)` / `idx_wf_task_form_task_id(task_id)`

> ⚠️ **脚手架警示(A-28 / P44)**:本表把**业务数据**(请假天数、报销金额…)以 JSON 存在 `form_content` 里。这在演示/轻量场景可用,但**生产环境有 4 个问题**:
> 1. **无法用 SQL 做业务统计**(要解析 JSON)
> 2. **无字段类型约束**(JSON 里什么都能填)
> 3. **无业务索引**(查"金额 > 1000 的请假"要全表扫 + JSON 解析)
> 4. **表单改版会导致历史 JSON 结构不一致**
>
> **建议整改**:业务字段落**独立业务表**(如 `biz_leave`),通过 `proc_ins_id` 软关联;`wf_task_form` 只存"表单渲染快照"而非业务数据。

### 4.6 `wf_comment_info` 审批意见(业务侧)

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| id | bigint | PK | 雪花 |
| proc_ins_id | varchar(64) | Y | — |
| task_id | varchar(64) | N | — |
| user_id | bigint | N | 填写人 |
| **type** | varchar(32) | N | `normal` / `reject` / `return` / `addSignBefore` / `addSignAfter` / `abolish` / `boundaryTrigger` / `revoke` 等 |
| comment | varchar(1000) | N | 意见内容 |
| extra | varchar(1000) | N | 扩展 JSON |
| 通用字段 | — | — | — |

索引:`idx_wf_comment_proc_ins(proc_ins_id)` / `idx_wf_comment_task_id(task_id)`

> 🔶 **与 `act_hi_comment` 双写**(同一事务);业务查询走本表(快),流程图/历史走引擎表。

### 4.7 `wf_copy` 抄送

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| id | bigint | PK | 雪花 |
| proc_ins_id | varchar(64) | Y | — |
| task_id | varchar(64) | N | — |
| **user_id** | bigint | Y | 抄送接收人 |
| title | varchar(255) | N | 标题 |
| **read_flag** | char(1) | Y | `0=未读 1=已读` |
| 通用字段 | — | — | — |

索引:`idx_wf_copy_user(user_id)` / `idx_wf_copy_proc_ins(proc_ins_id)`

> 🔶 「抄送我的」按 `user_id` 查;点开置 `read_flag='1'`。

### 4.8 `wf_form_category` 表单分类(树)

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| id | bigint | PK | 雪花 |
| parent_id | bigint | Y | `0=顶级` |
| category_name | varchar(64) | Y | — |
| code | varchar(64) | Y | 唯一 |
| order_num | int | Y | — |
| status | char(1) | Y | — |
| 通用字段 | — | — | — |

索引:`uk_wf_form_category_code(code)`

### 4.9 `wf_form_template` 表单模板

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| id | bigint | PK | 雪花 |
| template_name | varchar(64) | Y | — |
| template_code | varchar(64) | Y | 唯一 |
| content | text | Y | vform3 JSON |
| category_id | bigint | N | 所属分类 |
| status | char(1) | Y | — |
| 通用字段 | — | — | — |

索引:`uk_wf_form_template_code(template_code)` / `idx_wf_form_template_category(category_id)`

---

## 5. 流程实例扩展表 — `tbl_flow_extend_procinst`(1 张)

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| id | bigint | PK | 雪花 |
| **proc_ins_id** | varchar(64) | Y | 唯一,关联 `act_ru_execution.proc_inst_id_` |
| proc_def_key | varchar(64) | Y | 流程定义 key |
| proc_def_name | varchar(255) | N | 流程名 |
| **business_key** | varchar(128) | N | 业务键 |
| **start_user_id** | bigint | Y | 发起人 |
| start_user_name | varchar(64) | N | 发起人名 |
| **process_status** | varchar(32) | Y | `running` / `reject` / `return` / `completed` / `canceled` / `delete` / `boundary_<type>` |
| form_id | bigint | N | 首节点表单 |
| 通用字段 | — | — | — |

索引:`uk_tbl_flow_extend_procinst(proc_ins_id)` / `idx_start_user(start_user_id)` / `idx_proc_def_key(proc_def_key)` / `idx_business_key(business_key)` / `idx_process_status(process_status)`

> 🔶 **「我的发起」列表主表**:按 `start_user_id` 查,比查 `act_hi_procinst` 快且能带业务字段。
> 🔶 `process_status` 由 `GlobalEventListener` 维护;与 `act_ru_variable` 里的 `processStatus` 变量**双份**(业务表 + 引擎变量)。

---

## 6. 平台表 — `sys_*` / `qrtz_*` / `gen_*`(34 张,⬆️ 与 `springboot4` 完全一致)

> 结构与 `springboot4` **逐字段一致**(仅 schema 换了)。要点回顾:

| 域 | 表 | 数量 | 备注 |
|---|---|---|---|
| 系统-鉴权 | `sys_user` / `sys_role` / `sys_menu` / `sys_user_role` / `sys_role_menu` / `sys_role_dept` / `sys_user_post` | 7 | **`sys_menu` 含 1500~1699 流程菜单** |
| 系统-组织 | `sys_dept` / `sys_post` | 2 | 流程审批人解析会用到 |
| 系统-字典参数 | `sys_config` / `sys_dict_type` / `sys_dict_data` | 3 | 验证码开关 |
| 系统-通知 | `sys_notice` / `sys_notice_read` | 2 | — |
| 系统-审计 | `sys_oper_log` / `sys_logininfor` | 2 | **流程操作也落这里** |
| 系统-文件国际化 | `sys_files` / `sys_i18n` | 2 | — |
| 系统-定时任务 | `sys_job` / `sys_job_log` | 2 | **RAMJobStore,但定义与日志落库** |
| 系统-示例 | `sys_demo` | 1 | — |
| Quartz | `qrtz_*` | 11 | **RAMJobStore 不使用** |
| 代码生成 | `gen_table` / `gen_table_column` | 2 | — |

> 🔶 **`sys_menu` 的流程菜单**:1500~1699 段,`perms` 为 `workflow:*`。这是本分支相对 `springboot4` 的**唯一数据变化**(菜单数据行,business data not schema)。

### 6.1 视图(2 张)

| 视图 | 用途 | 关键点 |
|---|---|---|
| `list_table` | 库表清单(代码生成器"导入表") | **`n.nspname = 'basicdb_flowable8'` 写死** |
| `list_column` | 库表字段清单 | 同上 |

> ⚠️ **schema 名不能改**:视图里写死了 `basicdb_flowable8`,改名会导致代码生成器读不到表。

---

## 7. 函数(5 个)

| 函数 | 用途 |
|---|---|
| `date_format` | 兼容 MySQL 日期格式化 |
| `exec` | 动态执行 SQL(代码生成器查元数据) |
| `find_in_set` | 兼容 MySQL 逗号串查找 |
| `ifnull` | 兼容 MySQL NULL 兜底 |
| `tabledef` | 取表 DDL |

> 🔶 与 `springboot4` 相同(5 个),schema 换到 `basicdb_flowable8`。

---

## 8. 索引与关键查询

### 8.1 平台查询(⬆️ 与 `springboot4` 一致)

`uk_user_name` / `uk_role_key` / `idx_parent_id` / `idx_dict_type` / `uk_config_key` / `idx_oper_time` / `idx_login_time` / `uk_user_notice` —— 全部不变。

### 8.2 🔶 流程关键查询

| 查询 | 命中 | 说明 |
|---|---|---|
| 我的待办 | `act_ru_task`(ASSIGNEE_ 索引)+ `act_ru_identitylink`(candidate) | Flowable 自带索引 |
| 我的已办 | `act_hi_taskinst`(ASSIGNEE_)+ `idx_wf_comment_proc_ins` | — |
| 我的发起 | **`idx_start_user(start_user_id)`** | 走 `tbl_flow_extend_procinst`,不查引擎表 |
| 抄送我的 | **`idx_wf_copy_user(user_id)`** | — |
| 流程意见 | **`idx_wf_comment_proc_ins(proc_ins_id)`** | 不走 `act_hi_comment` |
| 退回可选节点 | `act_hi_actinst`(按 `proc_inst_id_`) | Flowable 索引 |
| 流程图 | `act_hi_actinst` + `act_ge_bytearray` | — |
| 表达式被引用检查 | `flw_expression.key_` + 模型 XML 文本匹配 | 无索引,靠 `uk_flw_expression_key` + 全表扫(量小) |
| 定时器触发 | **`act_ru_timer_job`(DUEDATE_)** | 引擎自带索引 |
| 部署列表 | `act_re_deployment` + `act_re_procdef` | — |

### 8.3 🔶 建议补充的索引(可选优化)

| 场景 | 建议 |
|---|---|
| 待办量大(> 10 万) | 确认 `act_ru_task` 的 `ASSIGNEE_` 索引存在(Flowable 默认有) |
| 意见量大 | `wf_comment_info` 已有 `proc_ins_id` / `task_id` 索引 |
| 业务统计 | 若按 `business_key` 查,`idx_business_key` 已建 |
| **不建议** | 给 `act_*` 加索引(引擎升级可能冲突) |

---

## 9. 备份与恢复

### 9.1 恢复(全新数据库)

```bash
# 1) 建角色与库
psql -U postgres -h localhost -c "CREATE ROLE system LOGIN PASSWORD 'system';"
psql -U postgres -h localhost -c "CREATE DATABASE \"basicdb-vue3\" OWNER system;"

# 2) 导入(必须 psql,含 COPY FROM stdin;376.8 KB)
psql -U system -h localhost -d basicdb-vue3 -f file-sql/basicdb-vue3.sql

# 3) 验证表数量(期望 90)
psql -U system -h localhost -d basicdb-vue3 -c \
  "SELECT count(*) FROM information_schema.tables
   WHERE table_schema='basicdb_flowable8' AND table_type='BASE TABLE';"

# 4) 验证视图(期望 2)
psql -U system -h localhost -d basicdb-vue3 -c \
  "SELECT count(*) FROM information_schema.views WHERE table_schema='basicdb_flowable8';"

# 5) 验证 Flowable 关键表
psql -U system -h localhost -d basicdb-vue3 -c \
  "SELECT count(*) FROM information_schema.tables
   WHERE table_schema='basicdb_flowable8' AND table_name LIKE 'act_%';"   -- 期望 39
```

### 9.2 备份

```bash
# 必须用 --schema=basicdb_flowable8 限定
pg_dump -U system -h localhost -d basicdb-vue3 \
  --schema=basicdb_flowable8 --no-owner --no-privileges \
  --file=file-sql/basicdb-vue3.sql
```

### 9.3 注意事项

- 脚本**只能**用 `psql` 执行(含 `COPY ... FROM stdin`);GUI 的「执行 SQL」可能失败
- 重置 admin 密码:跑 `file-sql/reset-admin-password.sql`
- **历史脚本已合并**:`basic-sql-2023-03-02.sql` / `quartz.sql` / `ry_20210908.sql` / `reset-all-passwords.sql` / `fix-sys-menu-component.sql` / `create-sys-notice-read.sql` / `flowable8-*.sql` 全部并入主脚本(**本分支已删除**这些历史文件)
- 🔶 **清盘 ≠ 备份**:清盘(46 表)是**删除**;备份是 `pg_dump`

### 9.4 🔶 流程数据的特殊备份策略

| 场景 | 建议 |
|---|---|
| 流程上线前 | 单独备份 `wf_*` + `flw_expression`(业务配置) |
| 流程大改版前 | 备份全 schema(含 `act_re_*` 历史定义) |
| **测试环境重置** | 用**流程清盘**而非重导全库(快) |
| 生产回档 | 全 schema `pg_dump` 恢复;**注意 `act_ru_*` 恢复到旧时点会丢运行态** |

---

## 10. 表清单(全文汇总)

### 10.1 流程相关(56)

| 域 | 表 | 数量 |
|---|---|---|
| Flowable 模型部署 | `act_re_model` / `act_re_deployment` / `act_re_procdef` / `act_ge_bytearray` / `act_ge_property` / `act_procdef_info` | 6 |
| Flowable 运行时 | `act_ru_*`(13 张,见 §2.2) | 13 |
| Flowable 历史 | `act_hi_*`(10 张,见 §2.3) | 10 |
| Flowable 其它 | `act_evt_log` + 模型/部署/定义补充(见 §2.1/2.4) | 10 |
| Flowable 事件注册 | `flw_event_*` / `flw_channel_definition` / `flw_ru_batch*` | 6 |
| **表达式字典** | `flw_expression` | 1 |
| **工作流业务** | `wf_*`(9 张,见 §4) | 9 |
| **实例扩展** | `tbl_flow_extend_procinst` | 1 |
| **小计** | | **56** |

### 10.2 平台(34)

| 域 | 数量 |
|---|---|
| `sys_*` | 21 |
| `qrtz_*` | 11 |
| `gen_*` | 2 |
| **小计** | **34** |

### 10.3 合计

| 项 | 数量 |
|---|---|
| **表** | **56 + 34 = 90** ✅ |
| **视图** | **2**(`list_table` / `list_column`) |
| 函数 | 5 |

> 🔶 `act_id_*`(9 张 IDM 表)**不计入** 90 —— `idm.enabled: false`,可能被建但不使用。

---

## 11. 流程清盘涉及的表(46 张)

> `WfProcessClearServiceImpl` 按依赖倒序 `TRUNCATE ... CASCADE`。

| 组 | 表 | 数量 |
|---|---|---|
| `act_hi_*` | 历史 10 张 | 10 |
| `act_ru_*` | 运行时 13 张 | 13 |
| `act_re_*` / `act_ge_*` / `act_procdef_info` / `act_evt_log` | 模型与部署 6~7 张 | 7 |
| `flw_*` | 事件注册 + 表达式 7 张 | 7 |
| `wf_*` | 业务 9 张 | 9 |
| `tbl_flow_extend_procinst` | 1 | 1 |
| **合计** | | **约 46**(实际按实现数组) |

> **不清**:`sys_*`(21)/ `qrtz_*`(11)/ `gen_*`(2) —— 平台数据保留。

---

## 12. 需求变更记录

| 变更 | 落点 |
|---|---|
| 数据库迁 PostgreSQL | §1 / §9 |
| 平台 34 表 | §6(⬆️ 与 `springboot4` 一致) |
| ⬆️ SB2→3 / SB3→4 升级 | `springboot3` / `springboot4` DBD |
| **🔶 集成 Flowable 8.0.0** | **§1.5(相对 springboot4 的 8 项变化)+ §1.6(为何独立 schema)+ §2(39 张 act_*)+ §3(7 张 flw_*)+ §4(9 张 wf_*)+ §5(tbl_flow_extend_procinst)+ §8.2(流程查询)+ §9.4(流程备份策略)+ §10(90 表清单)+ §11(清盘 46 表)** |
| **🔶 无 `act_de_*`** | §2.1 |
| **🔶 BPMN XML 存 `act_ge_bytearray`** | §2.1 |
| **🔶 表单快照隔离** | §4.4 |
| **🔶 `wf_task_form` 脚手架警示** | §4.5 / A-28 |
| **🔶 视图写死 schema 名** | §6.1 |
| **🔶 历史脚本已合并** | §9.3 |

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `springboot4` 数据库设计(v0.1)为基线,标注本分支**schema 换为 `basicdb_flowable8` + 表数 34 → 90 + 2 视图**(§1.5);新增「§1.6 为何独立 schema(3 方案对比)」、§2 Flowable 引擎 39 张表(4 组 + 使用红线)、§3 `flw_*` 7 张(含自有 `flw_expression`)、§4 `wf_*` 9 张逐表字段、§5 `tbl_flow_extend_procinst`、§8.2 流程关键查询 10 条 + §8.3 索引建议、§9.4 流程备份策略、§10 90 表清单(56 + 34)、§11 清盘 46 表;实测核对 90 表 / 2 视图 / 5 函数 |
