# 基础平台(base-postgresql-vue3) 概要设计(HLD) — `springboot2-extend-flowable` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot2-extend-flowable`** 分支
> 前置依赖:《架构方案》v0.1、《需求文档》v0.1
> 同主题文档:`docs/流程引擎使用说明.md`(使用层面,本文档是集成层面)

---

## 1. 设计目标与约束回顾

| 来源 | 关键约束 |
|---|---|
| 架构方案 §2 | JDK 11 + SB 2.5.14 锁死;`basicdb_flowable` schema;Flowable 6.8.0;IDM 关闭;`wf_task_form` 脚手架警示 |
| 需求文档 §7 | 列表 < 1s;流程图 < 2s(PC)/< 3s(移动);`workflow:*` 权限点;意见双写 |
| 需求文档 §10 | AC-01~AC-15 |

**勿重复大段正文**,仅作引子。

---

## 2. 逻辑架构与模块划分

### 2.1 工程结构(8 个 Maven 子模块,本分支比 master 多 1 个)

```
base-postgresql-vue3/
├── pom.xml                         # 父 pom(8 子模块)
├── basic-admin-api/                # 启动入口 + 全部 Controller(含 workflow)
├── basic-admin-core/               # 业务核心(sys_*/gen_*/qrtz_*/demo)
├── basic-framework/                # 框架能力(Security/Redis/MyBatis/AOP/Filter)
├── basic-common/                   # 工具/注解/枚举/基类
├── business-core/                  # 业务扩展点(com.core)
├── extend-quartz/                  # Quartz 集成
├── extend-generator/               # Velocity 代码生成
├── extend-flowable/                # 🆕 Flowable 6.8.0 + 13 Controller + 23 ServiceImpl
├── file-sql/                       # 3 个 SQL(basicdb-vue3.sql 唯一运行入口)
├── file-bin/  file-doc/            # 静态资源
├── web-ui/                         # PC(Vue3 + ElementPlus + bpmn.js + vform3)
└── web-mp/                         # 移动(uni-app3 + uview-plus3)
```

### 2.2 extend-flowable 模块组件职责

| 子包 | 关键类 | 职责 |
|---|---|---|
| `constant` | `ExtensionConstant` / `FlowConstant` | 节点扩展属性名 / 流程常量 |
| `converter` | `CustomBpmnJsonConverter` / `BruceUserTaskJsonConverter` / `BruceSubProcessJsonConverter` / `BruceCallActivityJsonConverter` / `BruceBoundaryEventJsonConverter` | 让 Flowable Modeler 认识 `flowable:Idm*` 扩展属性 |
| `enm` | `CommentTypeEnum` / `ProcessStatusEnum` | 注释类型 / 流程状态 |
| `flowable.common.constant` | `ProcessConstants` / `TaskConstants` | 流程 / 任务常量(含边界事件) |
| `flowable.common.enums` | `BoundaryEventTypeEnum` / `FlowComment` / `FormType` / `ProcessStatus` | 枚举 |
| `flowable.config` | `FlowBpmnConfig` / `FlowExtensionConfig` / `GlobalEventListenerConfig` | 引擎配置 / 扩展配置 / 全局事件 |
| `flowable.config.modeler` | `DatabaseConfiguration` / `ModelerBeanConfig` | 复用 Flowable Modeler |
| `flowable.core` | `FormConf` | 表单配置 |
| `flowable.core.domain` | `ProcessQuery` | 流程查询 |
| `flowable.factory` | `FlowServiceFactory` / `ActivitiEngineUtil` | 引擎各 Service 统一门面 |
| `flowable.flow` | `FlowableConfig` / `FlowableUtils` / `FindNextNodeUtil` / `CustomProcessDiagramGenerator` / `CustomProcessDiagramCanvas` | 引擎覆盖 / 工具 / 自定义流程图 |
| `flowable.listener` | `GlobalEventListener` / `UserTaskListener` / `ProcessDueTimeListener` | 事件 / 任务处理人解析 / 任务到期 |
| `flowable.model` | `FlowableModelRepositoryImpl` | 模型仓储 |
| `flowable.tool` | `ReturnCode` / `ReturnVo` | 返回值 |
| `flowable.utils` | `ExtansionPropertiesUtil` / `ModelUtils` / `ProcessFormUtils` / `ProcessUtils` / `TaskUtils` / `UUIDGenerator` | 工具集 |
| `workflow.builder` | `DynamicUserTaskBuilder` | 动态构建 UserTask |
| `workflow.cmd` | `AbstractDynamicInjectionCmd` / `AddHisCommentCmd` / `InjectUserTaskInProcessInstanceCmd` / `ReplaceUserTaskInProcessInstanceCmd` | 动态注入 + 意见 + 加签 |
| `workflow.cmd.rollBack` | `RollbackCmd` + `RollbackOperateStrategy`(接口)+ `DefaultRollbackStrategyFactoryBean` + `RollbackStrategyFactory` + `RollbackParamsTemplate` + 7 个 impl + 监听器 | 退回策略(6+1) |
| `workflow.controller` | 13 个 Controller(见需求 §6.1) | REST 入口 |
| `workflow.customCache` | `CustomDeploymentCache` | 自定义部署缓存 |
| `workflow.domain` | 10+ 实体 + 8+ BO + 6+ DTO + 5+ VO | 实体 |
| `workflow.service.impl` | 23 个 ServiceImpl | 业务实现 |

### 2.3 关键类对照表

| 能力 | 类 | 路径 | 备注 |
|---|---|---|---|
| 引擎门面 | `FlowServiceFactory` | `flowable/factory/` | 注入 RepositoryService / RuntimeService / TaskService / HistoryService / ManagementService / ProcessEngine |
| BPMN 解析 | `FlowBpmnConfig` | `flowable/config/` | `BpmnXMLConverter` + `ObjectMapper` + `TypeConverterImpl` |
| 自定义扩展注册 | `FlowExtensionConfig` | `flowable/config/` | `CustomBpmnJsonConverter` Bean |
| Modeler 复用 | `DatabaseConfiguration` / `ModelerBeanConfig` | `flowable/config/modeler/` | 复用 Flowable Modeler 逻辑层 |
| 全局事件 | `GlobalEventListener` / `GlobalEventListenerConfig` | `flowable/listener/` + `flowable/config/` | 流程/任务事件统一处理 |
| 任务处理人解析 | `UserTaskListener` | `flowable/listener/` | `flowable:Idm*` 解析 + 跳过策略 |
| 任务到期 | `ProcessDueTimeListener` | `flowable/listener/` | — |
| 流程图 | `CustomProcessDiagramGenerator` + `CustomProcessDiagramCanvas` | `flowable/flow/` | 基于 Activiti 6.0.0 `BpmnLayout` + 自定义画布 |
| 退回 | `RollbackCmd` + 6 策略 | `workflow/cmd/rollBack/` | 工厂模式 |
| 加签 | `InjectUserTaskInProcessInstanceCmd` + `DynamicUserTaskBuilder` | `workflow/cmd/` + `workflow/builder/` | 注入到执行流 |
| 意见 | `AddHisCommentCmd` | `workflow/cmd/` | 写 `act_hi_comment` + `wf_comment_info` |
| 自定义 JSON 转换 | `CustomBpmnJsonConverter` + 4 个 `Bruce*` | `converter/` | 认识 `flowable:Idm*` + 边界事件扩展 |

### 2.4 扩展 SPI

| SPI | 接入方式 | 用例 |
|---|---|---|
| 自定义 Rollback 策略 | 实现 `RollbackOperateStrategy` + 注册到 `DefaultRollbackStrategyFactoryBean` | 6 种内置 + 自定义 |
| 自定义 JSON 转换 | 继承 `Bruce*` 或 `CustomBpmnJsonConverter` | 新增扩展属性 |
| 自定义 BPMN 元素 | 在 `ExtensionConstant` 加属性名 + 在 `Bruce*JsonConverter` 注册 | — |
| 自定义流程状态 | 在 `ProcessStatus` 加枚举 | 列表筛选 / 状态变量 |
| 自定义事件 | 实现 `FlowableEventListener` + `@Bean` | — |

---

## 3. 核心流程设计

### 3.1 流程建模 → 部署

**状态机**

```
[未保存]  save      [草稿]      deploy      [已部署]
   │  ─────────▶    │  ─────────▶  │  ─────────▶  [已部署]
   │                │  publish     │              │
   │                │  ─────▶     [已发布]         │  suspend/activate
   │                │             │              [挂起] / [激活]
   │                │  delete     │              │
   │                │  ─────▶    [已删除]
```

**时序(ASCII)**

```
[PC web-ui]                    [WfModelController]      [WfModelServiceImpl]      [CustomBpmnJsonConverter]      [Flowable]
   │  save(BPMN JSON)            │                         │                            │                          │
   │ ────────────────────────────▶  save()                  │                            │                          │
   │                              │ ─────────────────────▶  validate BPMN                │                          │
   │                              │                         │  JSON → BPMN XML            │                          │
   │                              │                         │  BPMN XML 校验              │                          │
   │                              │                         │  act_re_model 写            │                          │
   │                              │                         │  act_de_model 写            │                          │
   │ ◀────────────────────────────  ok                      │                            │                          │
   │                              │                         │                            │                          │
   │  deploy(modelId)             │                         │                            │                          │
   │ ────────────────────────────▶  deploy()                │                            │                          │
   │                              │ ─────────────────────▶  convert + deploy            │                          │
   │                              │                         │ ────────────────────────▶  toBpmn                 │
   │                              │                         │  RepositoryService.createDeployment()
   │                              │                         │  .addBpmnModelInputStream()
   │                              │                         │  .name(...)
   │                              │                         │  .deploy()                                       │
   │                              │                         │  → act_re_deployment                             │
   │                              │                         │    act_re_procdef                                 │
   │                              │                         │    act_ge_bytearray (BPMN+PNG)                   │
   │                              │                         │  wf_deploy_form 拷快照                           │
   │ ◀────────────────────────────  ok                      │                            │                          │
```

### 3.2 流程发起 → 审批 → 结束

**状态机(单次流程实例)**

```
[未发起]
   │  POST /workflow/process/start
   ▼
[已发起 / Running] ── 节点流转 ──▶ [Running] ── 同意 ──▶ 下一节点
   │  ── 驳回 ──▶ [Reject] (终)
   │  ── 退回 ──▶ [Return] → 跳回目标节点 → [Running]
   │  ── 加签 ──▶ [Running] (临时多一节点)
   │  ── 终止 ──▶ [Reject] (终)
   │  ── 撤回(发起人) ──▶ [Canceled] (终)
   │  ── 废除(流程中心) ──▶ [Canceled] (终)
   │  ── 边界事件触发 ──▶ [Running] (status='boundary_<type>')
   │  ── 自然结束 ──▶ [Completed] (终)
   │  ── 物理清理 ──▶ [Delete]
```

**时序(发起)**

```
[发起人 web-ui/web-mp]    [WfProcessController]    [WfProcessServiceImpl]    [Flowable]    [DB]
   │  POST /process/start { procDefKey, formData, variables }                        │
   │ ────────────────────────▶                                                        │
   │                          startProcess(...)                                      │
   │                          ───────────────────▶                                    │
   │                          ├─ tbl_flow_extend_procinst 写扩展                       │ → basicdb_flowable.tbl_flow_extend_procinst
   │                          │  (start_user_id, businessKey, processStatus='running')
   │                          ├─ RuntimeService.createProcessInstanceByKey           │
   │                          │  ─────────────────────▶                                │
   │                          │  ├─ act_ru_execution                                  │ → act_ru_execution
   │                          │  ├─ act_ru_task (第一节点)                            │ → act_ru_task
   │                          │  ├─ UserTaskListener.assigneeResolver                 │
   │                          │  │   解析 flowable:IdmAssignee                       │
   │                          │  │   → act_ru_identitylink 写                         │ → act_ru_identitylink
   │                          │  └─ wf_deploy_form.content (拷快照)                   │
   │ ◀──────────────────────  procInsId                                              │
```

**时序(同意)**

```
[审批人]                  [WfTaskController]      [WfTaskServiceImpl]    [Flowable]     [DB]
   │  POST /task/complete { taskId, comment, variables }                             │
   │ ──────────────────────▶  complete()                                            │
   │                          ──────────────────▶                                     │
   │                          ├─ AddHisCommentCmd.execute                           │
   │                          │  ├─ wf_comment_info 写                                │ → wf_comment_info
   │                          │  └─ act_hi_comment 写                                │ → act_hi_comment
   │                          ├─ GlobalEventListener 记录 processStatus              │
   │                          ├─ TaskService.complete(taskId, variables)            │
   │                          │  ────────────────────▶                                │
   │                          │  ├─ act_hi_taskinst 写(complete)                     │ → act_hi_taskinst
   │                          │  ├─ act_ru_task 删                                   │ → act_ru_task
   │                          │  ├─ act_ru_task 新(下一节点)                          │ → act_ru_task
   │                          │  └─ UserTaskListener.assigneeResolver                 │
   │                          └─ 若有 act_ru_job(timer/async)                       │ → act_ru_*
   │ ◀─────────────────────  ok                                                     │
```

### 3.3 退回(任意节点)

**策略选择**

```
WfTaskServiceImpl.return
   │
   ▼
RollbackCmd.execute
   │
   ├─ 选目标 activityId (前端从 /returnList 拿,只返历史经过)
   │
   ├─ RollbackStrategyFactory.getStrategy(targetActivity, sourceActivity)
   │   根据目标节点类型返回 6 种策略之一:
   │   - UserTask:  DefaultTaskNextGatewayRollbackOperateStrategy
   │   - MultiInstance:
   │     - active → ActiveMultiInstanceTaskRollbackOperateStrategy
   │     - completed:
   │       - next = Task → CompletedMultiInstanceTaskAndNextDefaultTaskRollbackOperateStrategy
   │       - next = Gateway → CompletedMultiInstanceTaskAndNextDefaultGateWayTaskRollbackOperateStrategy
   │   - CallActivity → NextCallActivityRollbackOperateStrategy
   │   - SubProcess → NextSubProcessRollbackOperateStrategy
   │
   ├─ 选中的策略.rollback(params)
   │   ├─ 关闭当前 task → act_ru_task 删
   │   ├─ 取消后续 tasks
   │   ├─ 创建退回目标节点 task
   │   ├─ 写 processStatus='return'
   │   └─ RollbackEventListener 通知(可发消息)
   │
   └─ 写 wf_comment_info
```

### 3.4 加签(前/后)

**前加签**

```
WfTaskController.beforeAddSignTask
   │
   ▼
InjectUserTaskInProcessInstanceCmd.execute
   │
   ├─ DynamicUserTaskBuilder 构建新 UserTask BPMN
   │   ├─ id: dynamic_<uuid>
   │   ├─ name: "<原节点名>·前加签"
   │   ├─ assignee: <userId>
   │   ├─ 继承原节点的 IdmPermission + IsSkip/IsNullSkip
   │   └─ SequenceFlow 接到原节点之前
   │
   ├─ AbstractDynamicInjectionCmd 注入执行流
   │   ├─ 找到原节点所在 execution
   │   ├─ 改 outgoing → 接到新节点
   │   ├─ 新节点 outgoing → 接到原节点
   │   └─ activate 新节点
   │
   └─ 写 wf_comment_info(type='前加签')
```

**后加签**

```
WfTaskController.afterAddSignTask
   │
   ▼
ReplaceUserTaskInProcessInstanceCmd.execute
   ├─ 关闭当前 task
   ├─ DynamicUserTaskBuilder 构建"后加签"UserTask
   ├─ 注入到原任务之后
   └─ 完成后回到原执行流(若原节点还有 candidate)或结束
```

### 3.5 抄送

```
WfTaskController.copy(随同意/驳回一起提交)
   │
   ▼
WfCopyServiceImpl.add(procInsId, taskId, userIds[])
   │
   ├─ 按 userIds 批量 insert wf_copy
   │
   └─ 通知机制(本仓库不实现,落表即结束)
       业务侧可监听 wf_copy 写后做消息推送
```

### 3.6 边界事件

```
UserTask 上挂 BoundaryEvent:
   ├─ type=timer, duration=`P2D`
   ├─ type=error, errorCode=`BIZ_ERR_001`
   ├─ type=signal, signalCode=`SIG_ORDER_DONE`
   ├─ type=message, messageId=`MSG_NEW_LEAVE`
   └─ cancelActivity=true (默认)

触发后:
   ├─ Flowable 引擎按类型执行
   ├─ GlobalEventListener 监听 boundary 事件
   ├─ 写 processStatus='boundary_<type>'
   │   (ProcessConstants.BOUNDARY_TRIGGER_STATUS_PREFIX)
   └─ 写 wf_comment_info(type='boundary_trigger',
                          comment=BOUNDARY_TRIGGER_COMMENT)
```

---

## 4. 数据设计(概要)

### 4.1 Schema 布局

| schema | 表 | 内容 |
|---|---|---|
| `basicdb_flowable` | **86 张表 + 2 视图** | 全对象 |
| `public` | 0(不使用) | — |

### 4.2 表分组

| 组 | 数量 | 前缀 |
|---|---|---|
| Flowable 引擎-模型 | 4 | `act_re_model` / `act_de_model*` |
| Flowable 引擎-部署 | 6 | `act_re_deployment` / `act_re_procdef` / `act_ge_*` / `act_procdef_info` / `act_evt_log` |
| Flowable 引擎-运行 | 13 | `act_ru_*` |
| Flowable 引擎-历史 | 10 | `act_hi_*` |
| Flowable 事件注册 | 9 | `flw_ev_*` / `flw_event_*` / `flw_channel_*` / `flw_ru_batch*` |
| **表达式字典** | 1 | `flw_expression` |
| **工作流业务** | 10 | `wf_*` + `tbl_flow_extend_procinst` + `wf_category_org_link` |
| RuoYi 基础 | 23 | `sys_*` |
| 定时任务 | 11 | `qrtz_*` |
| 代码生成 + 视图 | 4 | `gen_table` / `gen_table_column` + `list_table` 视图 + `list_column` 视图 |

### 4.3 ER 简化(工作流业务)

```
act_re_model 1──1 act_de_model  (设计器模型)
act_re_model 1──n act_de_model_history  (历史)
act_re_deployment 1──n act_re_procdef  (流程定义)
act_re_deployment 1──n act_ge_bytearray  (BPMN+PNG)
act_re_procdef 1──n act_ru_execution
act_ru_execution 1──n act_ru_task
act_ru_task 1──n act_ru_identitylink  (assignee/candidate)
act_ru_task n──1 act_ru_variable
act_ru_execution 1──n act_ru_actinst  (节点历史)

wf_form 1──n wf_deploy_form  (部署快照)
wf_form_category 1──n wf_form  (分类)
wf_form_category 1──n wf_form_template  (模板)
wf_deploy_form 1──n wf_task_form  (节点填写,脚手架)
wf_category 1──n (流程分类) → 与 wf_form 互不关联

act_ru_execution 1──1 tbl_flow_extend_procinst  (业务扩展)
act_ru_task 1──n wf_comment_info  (审批意见)
act_ru_execution 1──n wf_copy  (抄送)
```

### 4.4 关键索引

| 关键查询 | 命中索引 |
|---|---|
| `select * from act_ru_task where ASSIGNEE_=? and SUSPENSION_STATE_=1` | `act_ru_task.ASSIGNEE_` |
| `select * from act_ru_task where ID_ in (select TASK_ID_ from act_ru_identitylink where USER_ID_=?)` | identitylink 复合 |
| `select * from act_hi_procinst where PROC_DEF_KEY_=? and START_USER_ID_=?` | act_hi_procinst 复合 |
| `select * from wf_form where FORM_CODE=?` | `uk_form_code` |
| `select * from wf_task_form where PROC_INS_ID_=?` | PROC_INS_ID 索引 |
| `select * from tbl_flow_extend_procinst where START_USER_ID_=?` | START_USER_ID 索引 |
| `select * from act_ru_job where DUEDATE_ < now()` | DUEDATE 索引(timer 调度) |

---

## 5. 接口清单(本分支新增)

> 沿用 master 的接口(`/login` / `/system/*` / `/monitor/*` / `/tool/*`)略,本节只列本分支新增。

### 5.1 流程模型 `/workflow/model`

| 方法 | 路径 | 说明 | 权限 |
|---|---|---|---|
| GET | `/workflow/model/list` | 模型分页 | `workflow:model:list` |
| GET | `/workflow/model/listModelLaunch` | 可发起 | — |
| POST | `/workflow/model/save` | 保存 | `workflow:model:save` |
| POST | `/workflow/model/deploy` | 部署 | `workflow:model:deploy` |
| GET | `/workflow/model/bpmnXml` | 读 BPMN | — |
| GET | `/workflow/model/historyList` | 历史版本 | — |
| GET | `/workflow/model/latest` | 最新版本 | — |
| POST | `/workflow/model/suspended` | 挂起 | `workflow:model:suspend` |
| POST | `/workflow/model/activate` | 激活 | `workflow:model:activate` |
| POST | `/workflow/model/publishModel` | 发起 | `workflow:model:publish` |

### 5.2 流程定义 `/workflow/deploy`

| 方法 | 路径 | 说明 | 权限 |
|---|---|---|---|
| GET | `/workflow/deploy/list` | 部署分页 | `workflow:deploy:list` |
| GET | `/workflow/deploy/publishList` | 可发起 | `workflow:deploy:list` |
| POST | `/workflow/deploy/changeState` | 激活/挂起 | `workflow:deploy:state` |
| POST | `/workflow/deploy/deployForm` | 部署表单 | `workflow:deploy:form` |

### 5.3 流程实例/任务 `/workflow/process`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/workflow/process/todoList` | 待办 |
| GET | `/workflow/process/finishedList` | 已办 |
| GET | `/workflow/process/ownList` | 我的已发 |
| GET | `/workflow/process/copyList` | 抄送给我 |
| GET | `/workflow/process/claimList` | 待签收 |
| GET | `/workflow/process/detail` | 流程详情 |
| GET | `/workflow/process/getFlowViewer` | 流程图(高亮) |
| GET | `/workflow/process/historyProcNodeList` | 历史节点 |
| GET | `/workflow/process/getProcessCommentInfo` | 审批意见 |
| GET | `/workflow/process/selectProcessPrcInstance` | 流程中心 |

### 5.4 任务操作(13+6 类)`/workflow/task`

| 方法 | 路径 | action | 节点授权值 |
|---|---|---|---|
| POST | `/workflow/task/complete` | `complete` | `complete` |
| POST | `/workflow/task/reject` | `reject` | `reject` |
| POST | `/workflow/task/return` + GET `/workflow/task/returnList` | `return` | `return` |
| POST | `/workflow/task/transfer` | `transfer` | `transfer` |
| POST | `/workflow/task/delegate` | `delegate` | `delegate` |
| POST | `/workflow/task/mulitiSequentialUnComplete` | `mulitiSequentialUnComplete` | `mulitiSequentialUnComplete` |
| POST | `/workflow/task/beforeAddSignTask` | `addSignBefore` | `addSignBefore` |
| POST | `/workflow/task/afterAddSignTask` | `addSignAfter` | `addSignAfter` |
| POST | `/workflow/task/stopProcess` | `stop` | `stop` |
| POST | `/workflow/task/rejectLastTask` | — | — |
| POST | `/workflow/task/skipUserTask` | — | — |
| POST | `/workflow/task/editProcessTaskAssignee` | — | — |
| POST | `/workflow/task/updateTaskComment` | — | — |
| DELETE | `/workflow/task/deleteProcessByIds/{ids}` | — | — |
| POST | `/workflow/task/deleteProcessTask` | — | — |
| POST | `/workflow/task/revokeProcess` | — | — |
| POST | `/workflow/task/abolishProcess` | — | — |
| POST | `/workflow/task/claim` / `/unClaim` | — | — |
| POST | `/workflow/task/copy` | — | `copy` |

### 5.5 节点表单 `/workflow/taskForm`

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/workflow/taskForm/saveProcessForm` | 保存节点表单(**⚠️ 脚手架**)|
| GET | `/workflow/taskForm/selectFormDataByProcInsIdToList` | 读实例所有节点 |
| GET | `/workflow/taskForm/selectFormJsonDataByTaskIdAndActIdToList` | 读某任务某节点 |
| DELETE | `/workflow/taskForm/delProcessForm` | 删除 |

### 5.6 表单 `/workflow/form` + `/workflow/formCategory` + `/workflow/formTemplate`

| 分组 | 权限 |
|---|---|
| 表单 | `workflow:form:*` |
| 表单分类 | `workflow:formCategory:*` |
| 表单模板 | `workflow:formTemplate:*` |

### 5.7 流程分类 `/workflow/category`

权限 `workflow:category:*`

### 5.8 表达式 `/workflow/expression`

权限 `workflow:expression:*`

### 5.9 边界事件 `/workflow/boundaryEvent`

权限 `workflow:boundaryEvent:*`

### 5.10 流程迁移 `/workflow/processMigrate`

权限 `workflow:processMigrate:migrate`

### 5.11 流程清理 `/workflow/processClear`

权限 `workflow:processClear:clear`

### 5.12 内部 SPI(同 §2.4)

---

## 6. 关键技术决策(HD-)

| # | 决策 | 理由 | 影响 |
|---|---|---|---|
| HD-01 | Flowable IDM 关闭 | 人员走 RuoYi 体系;数据双源不可接受 | 节点上以 `flowable:Idm*` 扩展属性挂载 |
| HD-02 | 节点「授权功能」枚举从 7 项扩到 10 项 | 业务需要:终止 / 前加签 / 后加签 | 前端 3 处文件必须同步;`flowable:IdmPermission` 字符串 |
| HD-03 | 自定义 BPMN JSON 转换器 | 默认不识别 `flowable:Idm*` | 5 个 `Bruce*` + `CustomBpmnJsonConverter` |
| HD-04 | 退回策略模式 | 6 种场景各不同 | `RollbackCmd` + 6 impl + 工厂 |
| HD-05 | 加签 = 动态注入 UserTask BPMN | 运行时改模型 | `DynamicUserTaskBuilder` + `AbstractDynamicInjectionCmd` |
| HD-06 | 流程图:Activiti 6.0.0 BpmnLayout 排版 + 自定义 Canvas 画高亮 | 默认生成器不支持高亮 | 混合两个老引擎;**风险点** |
| HD-07 | 意见双写 `act_hi_comment` + `wf_comment_info` | 业务侧不被引擎升级影响 | 写两次;事务内一致 |
| HD-08 | 部署快照 `wf_deploy_form` | 表单修改不影响已发流程 | 拷一份;存储翻倍 |
| HD-09 | `wf_task_form.form_content` 存整个 JSON | 脚手架快速跑通 | **警示**;生产应改业务表 |
| HD-10 | 6 类 Job 表分桶 | 性能 + 维护性 | 表数量 +5 |
| HD-11 | 流程资源/PNG 落库(act_ge_bytearray) | 一体化 | 长期占库;归档策略待定 |
| HD-12 | 表达式引擎 = Aviator | 业务侧需要简单表达式 | 加 `aviator` 依赖 |
| HD-13 | MyBatis-Plus 沿用 master | RuoYi 体系一致 | `wf_*` 用 MP 即可 |
| HD-14 | Swagger 1.6.2 强制覆盖 | Flowable 6.8.0 内部依赖 | `swagger-models` 显式版本 |
| HD-15 | FastJSON 1.2.83(WfTaskFormServiceImpl 旧代码) | 历史代码未改造 | 与主 POM Jackson 共存;有 CVE 风险 |

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
       │  (含 extend-flowable)     │
       │  端口:8080                │
       │  profile: dev / prod      │
       └──────┬──────────────┬─────┘
              │              │
              ▼              ▼
       ┌────────────┐   ┌────────────┐
       │PostgreSQL  │   │  Redis     │
       │15+(basicdb │   │ (可选)     │
       │  _flowable)│   │            │
       │86 表+2 视图│   │            │
       └────────────┘   └────────────┘
```

### 7.1 启动顺序

1. PostgreSQL 启动
2. `psql -c "CREATE ROLE system LOGIN PASSWORD 'system';"`
3. `psql -c "CREATE DATABASE \"basicdb-vue3\" OWNER system;"`
4. `psql -U system -d basicdb-vue3 -f file-sql/basicdb-vue3.sql`(86 表 + 2 视图)
5. (可选)Redis 启动
6. `mvn spring-boot:run -pl basic-admin-api`
7. `pnpm dev` 启动 web-ui;`pnpm dev:h5` 启动 web-mp
8. 默认账号 `admin / admin123`,登录后改密

### 7.2 并发与恢复

- Tomcat `max-threads=800`
- Druid `min-idle=5` / `max-active=20`
- Flowable 异步执行器 + Job 重试
- Quartz(沿用 master)`qrtz_locks` 维护多实例 Job
- `flowable.async-executor-activate: true` 必须开
- `database-schema-update: true` 启动时自动升级引擎表

### 7.3 回滚策略

- 单实例:`POST /monitor/shutdown`(actuator)
- 数据:`pg_dump` 备份;`wf_*` + `act_*` 全部可基于全量备份恢复

---

## 8. 风险与待定项

| 编号 | 项 | 影响 | 归属文档 |
|---|---|---|---|
| P1 | `wf_task_form.form_content` 存业务数据(脚手架) | 业务查询慢、无约束 | 《需求文档》A-1;README §7.8 |
| P2 | 流程通知机制(抄送后无 WebSocket/邮件) | 抄送落表即结束 | 《需求文档》A-2 |
| P3 | 流程图移动端:web-view 慢 | 用户体验 | 《需求文档》A-3 |
| P4 | Flowable 6.8.0 较旧 | 升级走新分支 | 风险 R3 |
| P5 | Activiti 6.0.0 混用 | 已锁版本;不改 | 风险 R4 |
| P6 | FastJSON 1.2.83 CVE | 中风险;主 POM 仍 Jackson | 风险 R5 |
| P7 | 视图 `list_table` / `list_column` 写死 schema 名 | schema 改名即失配 | 风险 R7 |
| P8 | 流程资源占库 | 长期累积 | 风险 R8 |
| P9 | 节点「授权功能」3 处前端文件必须同步 | 漏改即按钮不显示 | 风险 R9 |
| P10 | 撤回/废除权限易越权 | 双重校验(前端 + 后端) | 风险 R15 |
| P11 | `flowable.ui.common.idm-url` 占位值 | 即使 idm=false 也会读 | 风险 R12 |
| P12 | `extend-flowable` 体积大 | 单体 jar 偏大 | 风险 R13 |
| P13 | 业务数据 JSON 解析无 B-tree 索引 | P1 缓解 | — |
| P14 | 业务表脚手架缺失 | 新业务需手动加表 | 走 `extend-generator` |
| P15 | `app.idm-admin` 默认 `test/test` | 占位防泄露 | — |

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:8 子模块/86 表 + 2 视图/13 Controller/23 ServiceImpl/6 退回策略/4 边界事件/15 HD |
