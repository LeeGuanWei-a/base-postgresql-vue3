# 基础平台(base-postgresql-vue3) 数据库设计 — `springboot3-extend-flowable` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot3-extend-flowable`** 分支
> 前置依赖:《架构方案》v0.1,《需求文档》v0.1
> 父分支:`springboot3`(继承其 34 张系统表)

---

## 1. 文档信息与版本

| 版本 | 日期 | 变更 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:在 `springboot3` 数据库设计基础上,展开「§3 流程引擎数据模型」(schema 划分 / 10 张业务表 / 80+ 张 Flowable 表 / 视图 / 索引) |

---

## 2. Schema 划分

### 2.1 父分支 `springboot3` 的 schema

父分支默认 **单 schema `basicdb`**,所有 34 张表(sys_user / sys_role / sys_menu / sys_dict_*/quartz 表)都在这里。

### 2.2 本分支 schema 划分

| schema | 包含 | 备注 |
|---|---|---|
| `basicdb` | 父分支的 34 张表 | RBAC / 定时任务 / 代码生成 |
| `basicdb_flowable7` | Flowable 80+ 张 ACT_/FLW_* + 本分支 10 张 wf_* + **sys_menu(2000-2099 流程菜单) + sys_dict_data(dict_type=wf_process_permission) + sys_role_menu(workflow 角色绑定)** | 与 sb2 的"独立 schema 模式"对齐 |

**为什么 2000-2099 菜单也放 basicdb_flowable7?**

新导出的 `basicdb-vue3.sql`(363 KB)已经把所有 sys_* 表放到了 `basicdb_flowable7`(经 pg_dump 验证)。这种"业务系统统一一个 schema"模式与 sb2-extend-flowable 的 `basicdb_flowable` 一致——本分支的 JDBC URL `currentSchema=basicdb_flowable7` 也是这个意思。

### 2.3 JDBC URL

```yaml
spring:
  datasource:
    url: jdbc:postgresql://192.168.170.125:54321/basicdb-vue3
         ?useSSL=false&currentSchema=basicdb_flowable7
```

`currentSchema=basicdb_flowable7` 让后续 SQL 不写 schema 前缀,直接 `SELECT * FROM sys_menu` 即可。

---

## 3. 业务表设计(extend-flowable)

### 3.1 总览

| # | 表名 | 中文 | 字段数 | 说明 |
|---|---|---|---|---|
| 1 | wf_category | 流程分类 | 12 | 树形分类,支持排序/状态/编码唯一 |
| 2 | wf_form | 自定义表单 | 13 | 存 vform3 JSON 内容 |
| 3 | wf_form_category | 表单分类 | 10 | 表单的二级分类(与 wf_category 区分) |
| 4 | wf_form_template | 表单模板 | 11 | 模板池,可被多个流程复用 |
| 5 | wf_deploy_form | 部署-表单关联 | 6 | procdefId ↔ formId 映射 |
| 6 | wf_task_form | 任务-表单关联 | 7 | taskDefKey ↔ formId 映射 |
| 7 | wf_copy | 流程抄送 | 16 | 抄送接收人/状态/原始/已读时间 |
| 8 | wf_comment_info | 审批意见 | 12 | 同意/驳回/退回/转办等动作记录 |
| 9 | tbl_flow_extend_procinst | 流程实例拓展 | 15 | procdefId 关联业务数据 / 业务单号 |
| 10 | sys_files (扩展) | 附件 | +5 字段 | 加 processInstanceId / taskId / activityId / url / uploadType |

### 3.2 wf_category 流程分类

```sql
CREATE TABLE wf_category (
    category_id    BIGINT PRIMARY KEY,           -- 主键
    category_name  VARCHAR(64) NOT NULL,         -- 分类名
    parent_id      BIGINT DEFAULT 0,             -- 父分类 id
    order_num      INT DEFAULT 0,                -- 显示顺序
    category_code  VARCHAR(64) UNIQUE,           -- 编码(唯一)
    icon           VARCHAR(255) DEFAULT '#',     -- icon 名称
    is_resource    CHAR(1) DEFAULT '0',          -- 是否资源分类
    status         CHAR(1) DEFAULT '0',          -- 0 正常 1 停用
    create_by      VARCHAR(64),
    create_time    TIMESTAMP,
    update_by      VARCHAR(64),
    update_time    TIMESTAMP,
    remark         VARCHAR(500)
);
CREATE INDEX idx_wf_category_parent ON wf_category(parent_id);
CREATE INDEX idx_wf_category_code ON wf_category(category_code);
```

**与 sb2 差异**:加 `category_code` 唯一索引(业务规则:同一编码不可重复)。

### 3.3 wf_form 自定义表单

```sql
CREATE TABLE wf_form (
    form_id        BIGINT PRIMARY KEY,           -- 表单主键(用 UUID 字符串)
    form_name      VARCHAR(64) NOT NULL,         -- 表单名
    form_code      VARCHAR(64) UNIQUE,           -- 业务编码(被 procdef 引用)
    form_type      VARCHAR(32) DEFAULT 'custom', -- custom/template/system
    designer_content TEXT,                       -- vform3 JSON(可能 1-2MB)
    version        INT DEFAULT 1,                -- 乐观锁 + 草稿版本
    is_deployed    CHAR(1) DEFAULT '0',          -- 是否被部署引用
    status         CHAR(1) DEFAULT '0',          -- 0 草稿 1 已发布
    create_by      VARCHAR(64),
    create_time    TIMESTAMP,
    update_by      VARCHAR(64),
    update_time    TIMESTAMP,
    remark         VARCHAR(500)
);
CREATE INDEX idx_wf_form_code ON wf_form(form_code);
```

**关键设计**:
- `designer_content` 是 TEXT 类型,实际存 vform3 JSON 字符串
- `is_deployed='1'` 时不允许删除(防止误删线上表单)
- `form_code` 是业务唯一标识,被 `wf_deploy_form` 引用

### 3.4 wf_deploy_form 部署-表单关联

```sql
CREATE TABLE wf_deploy_form (
    id             BIGINT PRIMARY KEY,
    deploy_id      VARCHAR(64) NOT NULL,          -- act_re_deployment.id_
    form_id        BIGINT NOT NULL,               -- wf_form.form_id
    procdef_id     VARCHAR(64) NOT NULL,          -- act_re_procdef.id_
    form_type      VARCHAR(32) DEFAULT 'custom',  -- 表单类型
    create_by      VARCHAR(64),
    create_time    TIMESTAMP
);
CREATE INDEX idx_wf_deploy_form_procdef ON wf_deploy_form(procdef_id);
CREATE INDEX idx_wf_deploy_form_form ON wf_deploy_form(form_id);
```

### 3.5 wf_task_form 任务-表单关联

```sql
CREATE TABLE wf_task_form (
    id             BIGINT PRIMARY KEY,
    procdef_id     VARCHAR(64) NOT NULL,          -- act_re_procdef.id_
    task_def_key   VARCHAR(64) NOT NULL,          -- bpmn:UserTask 的 id
    form_id        BIGINT NOT NULL,               -- wf_form.form_id
    create_by      VARCHAR(64),
    create_time    TIMESTAMP,
    update_by      VARCHAR(64),
    update_time    TIMESTAMP
);
CREATE UNIQUE INDEX uk_wf_task_form ON wf_task_form(procdef_id, task_def_key);
```

**警告**:`wf_task_form` **不应存业务数据**——只存"任务节点 → 表单模板"的映射。业务实例数据走 `wf_comment_info.form_data` (TEXT)。

### 3.6 wf_copy 流程抄送

```sql
CREATE TABLE wf_copy (
    copy_id        BIGINT PRIMARY KEY,
    process_id     VARCHAR(64) NOT NULL,          -- 流程定义 ID
    process_name   VARCHAR(255),                  -- 流程名(冗余)
    instance_id    VARCHAR(64) NOT NULL,          -- 流程实例 ID
    task_id        VARCHAR(64),                   -- 被抄送时所在任务
    user_id        BIGINT NOT NULL,               -- 抄送接收人
    origin_user_id BIGINT NOT NULL,               -- 谁抄送的
    title          VARCHAR(255),                  -- 抄送标题
    copy_type      CHAR(1) DEFAULT '0',           -- 0 抄送 1 传阅
    is_read        CHAR(1) DEFAULT '0',           -- 0 未读 1 已读
    read_time      TIMESTAMP,                     -- 已读时间
    create_by      VARCHAR(64),
    create_time    TIMESTAMP,
    update_by      VARCHAR(64),
    update_time    TIMESTAMP
);
CREATE INDEX idx_wf_copy_user ON wf_copy(user_id, is_read);
CREATE INDEX idx_wf_copy_instance ON wf_copy(instance_id);
```

### 3.7 wf_comment_info 审批意见

```sql
CREATE TABLE wf_comment_info (
    id             BIGINT PRIMARY KEY,
    proc_ins_id    VARCHAR(64) NOT NULL,
    task_id        VARCHAR(64),
    comment_type   VARCHAR(32) NOT NULL,          -- approve / reject / back / ...
    comment        TEXT,                          -- 审批意见文本
    form_data      TEXT,                          -- 业务表单数据 JSON
    attachment_ids VARCHAR(2000),                 -- sys_files id 列表(逗号分隔)
    user_id        BIGINT NOT NULL,
    nick_name      VARCHAR(64),                   -- 冗余
    create_by      VARCHAR(64),
    create_time    TIMESTAMP
);
CREATE INDEX idx_wf_comment_procins ON wf_comment_info(proc_ins_id);
CREATE INDEX idx_wf_comment_task ON wf_comment_info(task_id);
```

### 3.8 tbl_flow_extend_procinst 流程实例拓展

```sql
CREATE TABLE tbl_flow_extend_procinst (
    id                BIGINT PRIMARY KEY,
    proc_ins_id       VARCHAR(64) NOT NULL,
    proc_def_id       VARCHAR(64),
    business_key      VARCHAR(64),               -- 业务单号
    business_type     VARCHAR(64),               -- 业务模块
    process_name      VARCHAR(255),              -- 冗余
    process_status    VARCHAR(32),               -- running / suspended / completed
    start_user_id     BIGINT,
    start_nick_name   VARCHAR(64),
    start_time        TIMESTAMP,
    end_time          TIMESTAMP,
    duration          BIGINT,                    -- 毫秒
    create_by         VARCHAR(64),
    create_time       TIMESTAMP
);
CREATE INDEX idx_tbl_extend_procins ON tbl_flow_extend_procinst(proc_ins_id);
CREATE UNIQUE INDEX uk_tbl_extend_business ON tbl_flow_extend_procinst(business_type, business_key);
```

### 3.9 sys_files 扩展字段

父分支 `sys_files` 加 5 个字段(放 `basicdb_flowable7` schema):

```sql
ALTER TABLE sys_files ADD COLUMN process_instance_id VARCHAR(64);
ALTER TABLE sys_files ADD COLUMN task_id            VARCHAR(64);
ALTER TABLE sys_files ADD COLUMN user_id            BIGINT;
ALTER TABLE sys_files ADD COLUMN nick_name          VARCHAR(64);
ALTER TABLE sys_files ADD COLUMN activity_id        VARCHAR(64);
ALTER TABLE sys_files ADD COLUMN url                VARCHAR(500);
ALTER TABLE sys_files ADD COLUMN upload_type        VARCHAR(32);
CREATE INDEX idx_sys_files_proc ON sys_files(process_instance_id);
CREATE INDEX idx_sys_files_task ON sys_files(task_id);
```

---

## 4. Flowable 内置表(80+ 张,自动建)

由 `flowable.database-schema-update: true` 启动器自动建,无需手写 DDL。

### 4.1 RE(Repository)存储

```
act_re_deployment       部署记录
act_re_model            BPMN 模型(由本分支 extend-flowable 维护)
act_re_procdef          流程定义(已部署的)
```

### 4.2 RU(Runtime)运行时

```
act_ru_execution        执行实例
act_ru_task             任务实例
act_ru_variable         流程变量
act_ru_identitylink     候选人/办理人
act_ru_job              异步作业
act_ru_timer_job        定时作业
act_ru_suspended_job    挂起作业
act_ru_deadletter_job   死信作业
act_ru_event_subscr     事件订阅
act_ru_actinst          活动实例
act_ru_entitylink
act_ru_history_job
act_ru_external_job
```

### 4.3 HI(History)历史

```
act_hi_procinst         流程实例历史
act_hi_taskinst         任务历史
act_hi_actinst          活动历史
act_hi_varinst          变量历史
act_hi_detail           详情
act_hi_comment          评论
act_hi_attachment       附件历史
act_hi_identitylink     候选人历史
act_hi_entitylink
act_hi_tsk_log
```

### 4.4 GE(General)通用

```
act_ge_bytearray        字节数组(存 bpmn.xml, attachment)
act_ge_property         属性(下次的 ddl 戳记)
act_evt_log             事件日志
act_procdef_info        procdef 元数据
```

### 4.5 FLW 7.x 新增

```
flw_event_definition    事件定义
flw_event_deployment
flw_event_resource
flw_channel_definition  通道定义
flw_ru_batch            批处理
flw_ru_batch_part
flw_expression          表达式存储
```

---

## 5. 字典与菜单

### 5.1 字典 wf_process_permission

`sys_dict_type.dict_type = 'wf_process_permission'`,`sys_dict_data` 9 项:

| dict_value | dict_label | list_class | 用途 |
|---|---|---|---|
| approve | 同意 | primary | 正常通过 |
| reject | 驳回 | danger | 驳回到上一节点/发起人 |
| back | 退回 | warning | 任意节点退回 |
| transfer | 转办 | info | 任务转给他人 |
| delegate | 委派 | info | 委派(完成后回自己) |
| countersignBack | 会签回退 | warning | 顺序会签场景 |
| beforeAddSign | 前加签 | success | 当前节点前加签 |
| afterAddSign | 后加签 | success | 当前节点后加签 |
| stop | 终止 | danger | 终止整个流程实例 |

### 5.2 菜单 2000-2099

`sys_menu` 38 条,父 id 都是 2000(流程引擎),详细见《架构方案》§6.2。`sys_role_menu` 全部绑给 super_admin(role_id=1)。

---

## 6. 数据流

### 6.1 启动流程

```
1. 用户在 web-ui 流程中心 / 模型列表点「发起」
2. web-ui router.push('/workflow/process/start', { deployId })
3. start.vue 调 GET /workflow/model/getCategoryToModel 拿到所有已部署 procdef
4. 用户填表(自定义表单 / 系统表单)
5. POST /workflow/model/publishModel
6. 后端 WfModelService.publishModel → WfFlowableProcessService.startProcessInstanceByKey
7. 返回 procInsId,前端 router.push('/workflow/process/detail', { procInsId })
8. 详情页调 GET /workflow/process/detail?procInsId 拿到流转数据
9. 同时写 tbl_flow_extend_procinst(业务数据)
```

### 6.2 审批流程

```
1. 待办列表 GET /workflow/process/todoList
2. 点任务 → 跳 TaskDialog
3. TaskDialog 渲染 wf_form JSON(用 FormRender 组件)
4. 用户填 + 选动作 → POST /workflow/process/complete(或 reject / transfer ...)
5. WfProcessService.completeTask → WfFlowableTaskService.complete(taskId, variables, comment)
6. 写 wf_comment_info + sys_files
7. 触发 WfTaskCompletedHandler(下一个节点的 TaskCreated 事件)
8. 下一个候选人 → 待办列表自动出现
```

---

## 7. 索引与性能

| 表 | 索引 | 用途 |
|---|---|---|
| act_ru_task | proc_inst_id_, task_def_key_ | 任务列表按实例过滤 |
| act_hi_procinst | proc_inst_id_ | 历史实例查询 |
| act_hi_taskinst | proc_inst_id_, assignee_ | 个人已办 |
| wf_copy | (user_id, is_read) | 抄送列表 |
| wf_comment_info | proc_ins_id_ | 审批意见列表 |
| tbl_flow_extend_procinst | (business_type, business_key) | 业务单号反向查流程 |

**未使用**:PG 全文索引(后续可对 wf_form.designer_content 加 GIN,本分支暂不优化)。

---

## 8. 数据备份与恢复

```bash
# 全量备份(包含 basicdb_flowable7 schema)
pg_dump -h 192.168.170.125 -p 54321 -U system -d basicdb-vue3 -n basicdb_flowable7 \
  -f backup-flowable7-$(date +%Y%m%d).sql

# 全量恢复
psql -h 192.168.170.125 -p 54321 -U system -d basicdb-vue3 \
  -f backup-flowable7-20261023.sql
```

**警告**:流程数据量随历史实例数增长,**1 万实例 ≈ 100MB**,**10 万实例 ≈ 1GB**——定期归档 `act_hi_*`。

