# 基础平台(base-postgresql-vue3) 需求文档(PRD) — `springboot2-extend-flowable` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot2-extend-flowable`** 分支
> 前置依赖:《架构方案》v0.1
> 同主题文档:`docs/流程引擎使用说明.md`(使用手册,本文档覆盖业务需求/权限/验收/未决)

---

## 1. 文档信息与版本

- 当前版本:**v0.1** — 初稿
- 变更历史

| 版本 | 日期 | 变更 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:覆盖 Flowable 集成(建模/部署/发起/审批/退回/加签/抄送/会签/废除)+ 13 Controller 业务需求 + 权限矩阵 + 15 验收项 + 7 待定 |

---

## 2. 背景与目标

### 2.1 为什么做

master 分支提供 RBAC + 字典 + 定时任务 + 代码生成,够日常管理,**但缺业务流程能力**。企业内任何业务系统(请假/报销/合同审批/IT 工单)都需要可视化建模 + 灵活审批 + 移动办公,本分支在 master 之上:

- 集成 Flowable 6.8.0,获得完整 BPMN 2.0 引擎
- 自研 13 个 Controller + 23 个 Service,覆盖 13 个流程场景
- 提供可视化建模器(PC 端 bpmn.js)+ 表单设计器(PC 端 vform3)
- 移动端提供「待办/已办/已发/抄送/待签收」5 个 Tab,审批弹窗与 PC 一致
- 业务数据存储**默认走 `wf_task_form` 兜底**;**生产应改业务表**(README §7.8 已警示)

### 2.2 目标清单

| 目标 | 成功标准 |
|---|---|
| G1 提供可视化流程建模 | 在 PC 端拖拽节点、保存、部署,部署后流程定义可发起 |
| G2 提供动态表单 | 表单设计器配置 JSON,部署快照写 `wf_deploy_form`,实例化读快照 |
| G3 提供 13 类审批场景 | 同意/驳回/退回(任意节点)/转办/委派/前加签/后加签/会签回退/终止/撤回/废除/签收/跳过节点/变更处理人 |
| G4 提供 PC + 移动双端审批 | PC web-ui + 移动 web-mp 共享 `/workflow/**` REST;流程图均可看 |
| G5 提供流程图高亮 | 已办节点(绿)/待办(蓝)/驳回(红)/跳过(灰)不同色 |
| G6 完整审计与可观测 | `act_hi_*` 引擎历史 + `wf_comment_info` 业务历史 + `sys_oper_log` 操作日志 + Flowable actuator |
| G7 退回/加签策略完备 | 6 种 Rollback 策略覆盖普通节点/多实例/网关/调用活动/子流程 |

### 2.3 非目标(本分支不做)

- 多租户(全租户共享 `sys_user`)
- SSO 联邦
- Spring Boot 2 → 3/4 升级
- 业务表脚手架自动生成(走 `extend-generator` 即可,非本分支范围)
- Flowable 7 升级
- 自研 Form 渲染(依赖 vForm3)

---

## 3. 术语表

| 术语 | 含义 |
|---|---|---|
| BPMN | Business Process Model and Notation,业务流程建模标准 |
| 流程定义 | `act_re_procdef` 中一行,key+version 唯一;由 `act_re_deployment` 部署生成 |
| 流程实例 | `act_ru_execution` 运行时 + `act_hi_procinst` 历史,业务一次发起对应一个 |
| 任务 | `act_ru_task`,每个 UserTask 一行 |
| 候选人 / 候选组 | `act_ru_identitylink`,认领前不分配;签收后转 assignee |
| 节点「授权功能」 | 节点 `flowable:IdmPermission` JSON 数组,决定审批页可点哪些按钮 |
| 加签 | 流程运行中动态插入 UserTask(前加签 = 插入到当前节点前,后加签 = 替换当前) |
| 退回 | 流程回退到任意历史节点,经由 `RollbackCmd` + 6 种策略 |
| 抄送 | 不参与审批,仅 `wf_copy` 落记录 + 通知 |
| 会签 | 多实例 UserTask,顺序/并行由 `multiInstance` 元素决定 |
| 边界事件 | BPMN BoundaryEvent,挂在 UserTask 上,timer/error/signal/message 四类,触发后流程状态写 `boundary_<type>` |
| 部署快照 | `wf_deploy_form.content`,实例化读这份,`wf_form` 修改不影响已发流程 |
| `_FLOWABLE_SKIP_EXPRESSION_ENABLED` | 节点跳过表达式开关(ProcessConstants) |
| vForm3 | 前端独立表单设计器项目,输出 JSON 落 `wf_form.content` |
| IDM | Flowable 自带 Identity Management;本项目 **关闭** |

---

## 4. 用户角色与使用场景

### 4.1 角色

| 角色 | 关键动作 | 频率 |
|---|---|---|
| **流程管理员** | 建模/部署/分类/表单/表达式维护 | 中 |
| **业务发起人** | 在业务页面发起流程,跟踪已发 | 高 |
| **审批人** | 在待办/已签收 Tab 处理 | 高 |
| **抄送人** | 只读 + 知晓,不参与决策 | 中 |
| **流程中心管理员** | 批量废除/终止/清理测试数据 | 低 |
| **移动端用户** | 在手机/小程序审批 | 高 |
| **业务开发** | 自定义 Rollback / 加签 / 边界事件 | 低 |

### 4.2 关键场景走查(以请假为例)

#### 场景 S1:管理员建模请假流程

1. 登录 web-ui → 流程引擎 → 流程模型 → 新建
2. 拖拽:开始 → 部门经理审批 → HR 审批 → 结束
3. 部门经理节点「授权功能」勾:同意 / 驳回 / 退回 / 转办 / 抄送
4. 部门经理节点「审批人」:候选人=本部门 leader 角色(`flowable:IdmCandidateGroups`)
5. 保存 → 后端 `WfModelServiceImpl.save` 校验 BPMN 合法 → 写 `act_re_model` / `act_de_model`
6. 部署 → 写 `act_re_deployment` / `act_re_procdef` / `act_ge_bytearray`(BPMN+PNG)

#### 场景 S2:发起人发起请假

1. 业务页面 → 发起 → 选「请假流程」→ 填表单(开始日期/天数/原因)
2. 提交 → `POST /workflow/process/start` → 写 `tbl_flow_extend_procinst` + `act_ru_execution` + `act_ru_task`(第一节点)
3. 「我的已发」可见
4. 移动端推送待办给部门经理

#### 场景 S3:审批人同意(移动端)

1. web-mp → 待办 → 选「张三请假 3 天」
2. 弹窗:意见(可选)→ 点同意
3. 调 `POST /workflow/task/complete { taskId, comment, variables }`
4. 后端:写 `act_hi_comment` + `wf_comment_info` → `taskService.complete` → 流转到 HR 节点

#### 场景 S4:审批人退回

1. 弹窗 → 点退回 → 选退回节点(只能选历史经过的节点)
2. 调 `POST /workflow/task/return { taskId, returnToActivityId, comment }`
3. 后端:`RollbackCmd` 选策略(普通节点 → `DefaultTaskNextGatewayRollbackOperateStrategy`)
4. 状态变量 `processStatus='return'`,流程跳回目标节点

#### 场景 S5:前加签(本部门副经理会签)

1. 部门经理点「前加签」→ 选副经理
2. 调 `POST /workflow/task/beforeAddSignTask`
3. 后端:`InjectUserTaskInProcessInstanceCmd` + `DynamicUserTaskBuilder` 注入 UserTask
4. 副经理收到待办,完成后流程回原节点

#### 场景 S6:抄送(HR 备案)

1. 部门经理同意时勾「抄送 HR」→ 选人
2. 调 `POST /workflow/task/copy` → 写 `wf_copy`
3. HR 收到抄送消息(系统通知/邮件,本仓库仅落表,通知机制后续接)

#### 场景 S7:边界事件(超时自动跳过)

1. 建模时在 HR 审批节点上挂 BoundaryEvent → 选 timer → duration=`P2D`(2 天)
2. 节点 `flowable:IsSkip` / `IsNullSkip` 配置跳过策略
3. 2 天未审批 → timer 触发 → `processStatus='boundary_timer'` → 自动跳过

#### 场景 S8:会签(多人审批,过半通过)

1. 财务节点配置多实例:completionCondition=`${nrOfCompletedInstances/nrOfInstances >= 0.5}`
2. 三个人任一过半同意 → 流转

#### 场景 S9:流程清理(测试数据)

1. 流程中心 → 数据清理 → 选流程实例 → 物理删 `act_ru_*` + `act_hi_*` + `wf_*`
2. 调 `DELETE /workflow/task/deleteProcessByIds/{ids}`

#### 场景 S10:废弃流程

1. 流程中心 → 选已发流程 → 点废除
2. 调 `POST /workflow/task/abolishProcess`
3. 状态置 `canceled`,引擎任务全部结束

---

## 5. 总体需求

### 5.1 范围图(在 master 之上增量)

```
              ┌────────────────────────────────────┐
              │ 本分支新增                          │
              │  ┌────────────────────────────┐    │
              │  │ 流程引擎(extend-flowable)  │    │
              │  │ - 建模/部署/发起/审批       │    │
              │  │ - 退回/加签/抄送/会签       │    │
              │  │ - 13 Controller            │    │
              │  │ - 10 wf_* + 1 flw_expr     │    │
              │  │ - 边界事件(BoundaryEvent)  │    │
              │  └────────────┬───────────────┘    │
              │               │                    │
              │  ┌────────────┴───────────────┐    │
              │  │  Flowable 6.8.0 + Activiti │    │
              │  │  6.0.0(BpmnLayout 排版)    │    │
              │  └────────────┬───────────────┘    │
              │               │  schema=basicdb_flowable
              │  ┌────────────┴───────────────┐    │
              │  │  33 act_* + 9 flw_*        │    │
              │  └────────────────────────────┘    │
              └────────────────────────────────────┘
```

### 5.2 核心规则总表

| 模块 | 关键字段 | L1 校验(字段级) | L2 校验(跨模块) |
|---|---|---|---|
| 流程模型 | `modelId` / `modelKey` / `modelName` | 必填、长度、key 唯一 | key 与已部署 key 不冲突 |
| 表单定义 | `formId` / `formName` / `content(vForm3 JSON)` | 必填、JSON 合法 | formKey 唯一 |
| 表单分类 | `categoryId` / `categoryName` / `code` | 必填 | code 唯一 |
| 表达式 | `expressionKey` / `expressionName` | 必填 | key 唯一;不能删除被引用的 |
| 流程分类 | `categoryId` / `categoryName` / `code` | 必填 | code 唯一 |
| 发起 | `procDefKey` / `formData` / `variables` | 必填 | procDefKey 必须已部署且激活 |
| 审批 | `taskId` / `comment` / `variables` | 必填、comment 长度 | 当前用户必须是 task 的 assignee / candidate |
| 退回 | `taskId` / `returnToActivityId` | 必填 | 目标节点必须是历史经过的节点 |
| 加签 | `taskId` / `assignees[]` | 必填、assignees 非空 | 目标用户必须存在 |
| 抄送 | `taskId` / `copyUserIds[]` | 必填 | 目标用户必须存在 |
| 流程实例清理 | `ids[]` | 必填 | 仅测试环境的实例;生产需二次确认 |
| 边界事件 | `activityId` / `type` / `duration` / `cycle` / `date` / `errorCode` / `signalCode` / `messageId` | 按类型必填 | 类型与值匹配(timer→duration/cycle/date;error→errorCode;...) |

---

## 6. 功能需求

### 6.1 13 个 Controller 业务需求

#### 6.1.1 流程模型 `WfModelController`(`/workflow/model`)

| 接口 | 方法 | 权限 | 描述 |
|---|---|---|---|
| `list` | GET | `workflow:model:list` | 模型分页列表 |
| `listModelLaunch` | GET | — | 可发起的模型(已发布) |
| `save` | POST | `workflow:model:save` | 保存草稿(BPMN JSON + 流程属性) |
| `deploy` | POST | `workflow:model:deploy` | 部署 |
| `bpmnXml` | GET | — | 读 BPMN XML |
| `historyList` | GET | — | 模型历史版本 |
| `latest` | GET | — | 最新版本 |
| `suspended` | POST | `workflow:model:suspend` | 挂起定义 |
| `activate` | POST | `workflow:model:activate` | 激活定义 |
| `publishModel` | POST | `workflow:model:publish` | 发起(走 `/workflow/process/start` 入口,此处为快捷发起) |

#### 6.1.2 流程定义 `WfDeployController`(`/workflow/deploy`)

| 接口 | 方法 | 权限 | 描述 |
|---|---|---|---|
| `list` | GET | `workflow:deploy:list` | 部署分页 |
| `publishList` | GET | `workflow:deploy:list` | 已发布可发起 |
| `changeState` | POST | `workflow:deploy:state` | 激活/挂起 |
| `deployForm` | POST | `workflow:deploy:form` | 上传表单快照 |

#### 6.1.3 流程实例/任务 `WfProcessController`(`/workflow/process`)

| 接口 | 方法 | 描述 |
|---|---|---|
| `todoList` | GET | 待办 |
| `finishedList` | GET | 已办 |
| `ownList` | GET | 我的已发 |
| `copyList` | GET | 抄送给我 |
| `claimList` | GET | 待签收(candidate) |
| `detail` | GET | 流程详情 |
| `getFlowViewer` | GET | 流程图(高亮) |
| `historyProcNodeList` | GET | 历史节点 |
| `getProcessCommentInfo` | GET | 审批意见列表 |
| `selectProcessPrcInstance` | GET | 流程中心分页 |

#### 6.1.4 任务操作 `WfTaskController`(`/workflow/task`)

| 动作 | 接口 | action | 节点授权值 | 备注 |
|---|---|---|---|---|
| 同意 | `POST /complete` | `complete` | `complete` | 同时可填意见/变量/抄送 |
| 驳回 | `POST /reject` | `reject` | `reject` | 状态 → `reject` |
| 退回(任意节点) | `POST /return` + `/returnList` | `return` | `return` | 退回节点由下拉给出 |
| 转办 | `POST /transfer` | `transfer` | `transfer` | — |
| 委派 | `POST /delegate` | `delegate` | `delegate` | — |
| 会签回退 | `POST /mulitiSequentialUnComplete` | `mulitiSequentialUnComplete` | `mulitiSequentialUnComplete` | 仅顺序会签 |
| 前加签 | `POST /beforeAddSignTask` | `addSignBefore` | `addSignBefore` | — |
| 后加签 | `POST /afterAddSignTask` | `addSignAfter` | `addSignAfter` | — |
| 终止 | `POST /stopProcess` | `stop` | `stop` | 流程中心也可批量 |
| 默认驳回上一节点 | `POST /rejectLastTask` | — | — | 快捷驳回 |
| 跳过节点 | `POST /skipUserTask` | — | — | `skipActivityId` + `procInsId` |
| 变更处理人 | `POST /editProcessTaskAssignee` | — | — | `taskId` + `assignee` |
| 修改审批意见 | `POST /updateTaskComment` | — | — | 已办意见纠错 |
| 删除流程实例 | `DELETE /deleteProcessByIds/{ids}` | — | — | 测试数据清理 |
| 删除单个任务 | `POST /deleteProcessTask` | — | — | 测试数据清理 |
| 撤回 | `POST /revokeProcess` | — | — | 发起人「我的已发」 |
| 废除 | `POST /abolishProcess` | — | — | 流程中心 |
| 签收 | `POST /claim` / `unClaim` | — | — | — |
| 流程迁移 | `POST /processMigrate` | — | — | `WfProcessMigrateController` |

> 所有任务类接口统一携带 `currentUserId` / `currentUserName`(前端 `api/workflow/task.js` 的 `withUser()` 自动注入)。

#### 6.1.5 节点表单 `WfTaskFormController`(`/workflow/taskForm`)

| 接口 | 方法 | 描述 |
|---|---|---|
| `saveProcessForm` | POST | 保存节点填写的表单数据(默认落 `wf_task_form.form_content`)**⚠️ 脚手架** |
| `selectFormDataByProcInsIdToList` | GET | 读流程实例所有节点的表单数据 |
| `selectFormJsonDataByTaskIdAndActIdToList` | GET | 读某任务某节点的表单 |
| `delProcessForm` | DELETE | 删除表单数据 |

#### 6.1.6 表单 `WfFormController`(`/workflow/form`)

| 接口 | 方法 | 权限 | 描述 |
|---|---|---|---|
| `list` | GET | `workflow:form:list` | 表单列表 |
| `getFormByCode` | GET | — | 按编码查 |
| `saveForm` | POST | `workflow:form:save` | 保存 |
| `delForm` | DELETE | `workflow:form:remove` | 删除 |
| `getVFormWidgetList` | GET | — | vform 控件清单 |

#### 6.1.7 表单分类/模板

| Controller | 前缀 | 权限 |
|---|---|---|
| `WfFormCategoryController` | `/workflow/formCategory` | `workflow:formCategory:*` |
| `WfFormTemplateController` | `/workflow/formTemplate` | `workflow:formTemplate:*` |

#### 6.1.8 流程分类 `WfCategoryController`(`/workflow/category`)

| 接口 | 方法 | 描述 |
|---|---|---|
| `list` / `listAll` / `getCategoryOption` | GET | 树形 / 全量 / 下拉 |
| `checkCategoryCodeUnique` | GET | 唯一性预校验 |

#### 6.1.9 表达式 `WfExpressionController`(`/workflow/expression`)

| 接口 | 方法 | 描述 |
|---|---|---|
| `list` | GET | 表达式字典分页 |
| `export` | GET | 导出 |

#### 6.1.10 边界事件 `WfBoundaryEventController`(`/workflow/boundaryEvent`)

| 接口 | 方法 | 描述 |
|---|---|---|
| `trigger` | POST | 手动触发(测试) |
| `list` | GET | 触发记录 |
| `config` | POST | 配置(类型/duration/errorCode/signalCode/messageId/cancelActivity) |

#### 6.1.11 流程迁移 `WfProcessMigrateController`(`/workflow/processMigrate`)

| 接口 | 方法 | 描述 |
|---|---|---|
| `migrate` | POST | 跨版本流程实例迁移 |

#### 6.1.12 流程清理 `WfProcessClearController`(`/workflow/processClear`)

| 接口 | 方法 | 描述 |
|---|---|---|
| `clear` | POST | 清空测试数据(物理) |

### 6.2 业务流程字段表

#### 6.2.1 `wf_form` 表单定义

| 字段 | 类型 | 必填 | 校验 |
|---|---|---|---|
| id | bigint | PK | 雪花 |
| form_name | varchar(64) | Y | 长度 1-64 |
| form_code | varchar(64) | Y | 唯一,英文数字下划线 |
| form_type | varchar(32) | N | `vform3` |
| content | text | Y | vform3 JSON |
| version | int | N | — |
| remark | varchar(500) | N | — |
| 通用字段 | — | — | — |

#### 6.2.2 `wf_deploy_form` 部署快照

| 字段 | 类型 | 必填 | 校验 |
|---|---|---|---|
| id | bigint | PK | 雪花 |
| deploy_id | varchar(64) | Y | 关联 `act_re_deployment.id_` |
| form_id | bigint | Y | 关联 `wf_form.id` |
| content | text | Y | 部署时拷贝 `wf_form.content` |
| 通用字段 | — | — | — |

#### 6.2.3 `wf_task_form` 节点填写(脚手架)

| 字段 | 类型 | 必填 | 校验 |
|---|---|---|---|
| id | bigint | PK | 雪花 |
| proc_ins_id | varchar(64) | Y | — |
| task_id | varchar(64) | N | — |
| activity_id | varchar(64) | N | — |
| form_id | bigint | N | — |
| user_id | bigint | N | — |
| form_content | jsonb / text | Y | **⚠️ 不应存业务数据** |
| 通用字段 | — | — | — |

#### 6.2.4 `wf_comment_info` 审批意见(业务侧)

| 字段 | 类型 | 必填 | 校验 |
|---|---|---|---|
| id | bigint | PK | 雪花 |
| type | varchar(20) | N | 意见类型 |
| user_id | bigint | N | — |
| proc_ins_id | varchar(64) | Y | — |
| task_id | varchar(64) | N | — |
| comment | text | N | — |
| extra | varchar(1000) | N | JSON(意见扩展) |
| 通用字段 | — | — | — |

#### 6.2.5 `wf_copy` 抄送

| 字段 | 类型 | 必填 | 校验 |
|---|---|---|---|
| id | bigint | PK | 雪花 |
| proc_ins_id | varchar(64) | Y | — |
| task_id | varchar(64) | N | — |
| user_id | bigint | Y | 抄送接收人 |
| title | varchar(500) | N | — |
| read_flag | char(1) | N | `0=未读 1=已读` |
| 通用字段 | — | — | — |

#### 6.2.6 `tbl_flow_extend_procinst` 流程实例扩展

| 字段 | 类型 | 必填 | 校验 |
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

#### 6.2.7 `flw_expression` 表达式字典

| 字段 | 类型 | 必填 | 校验 |
|---|---|---|---|
| id | bigint | PK | 雪花 |
| name | varchar(128) | Y | — |
| key | varchar(64) | Y | 唯一 |
| value | varchar(500) | Y | — |
| type | varchar(32) | N | — |
| 通用字段 | — | — | — |

#### 6.2.8 `wf_category` / `wf_form_category` 分类(树形)

`parent_id` / `category_name` / `code` / `order_num` / `status` + 通用字段;`code` 全局唯一。

#### 6.2.9 `wf_form_template` 表单模板

`template_name` / `template_code` / `content` / `category_id` / `status` + 通用字段。

### 6.3 跨业务校验(L2)

| 校验项 | 位置 | 失败动作 |
|---|---|---|
| modelKey 全局唯一 | `WfModelServiceImpl.save` | 抛 `ServiceException` |
| formCode 全局唯一 | `WfFormServiceImpl.checkFormCodeUnique` | 抛 `ServiceException` |
| categoryCode 唯一 | `WfCategoryServiceImpl` | 抛 `ServiceException` |
| expressionKey 唯一 + 被引用时不能删 | `WfExpressionServiceImpl` | 抛 `ServiceException` |
| 审批人必须是 task 的 assignee / candidate | `WfTaskServiceImpl.X` | 抛 `ServiceException` |
| 退回目标节点必须是历史经过的 | `WfTaskServiceImpl.returnList` 只返历史节点 | 列表里没的不能选 |
| 加签用户必须存在 | `WfTaskServiceImpl.beforeAddSignTask` | 抛 `ServiceException` |
| 流程迁移:目标定义版本必须存在 | `WfProcessMigrateServiceImpl` | 抛 `ServiceException` |

### 6.4 审批动作 ↔ 状态变量

| 动作 | processStatus 变量 | tbl_flow_extend_procinst.process_status |
|---|---|---|
| 同意 | `running`(继续) | `running` |
| 驳回 | `reject` | `reject` |
| 退回(任意节点) | `return` | `return` |
| 撤回 | `canceled` | `canceled` |
| 废除 | `canceled` | `canceled` |
| 终止 | `reject` | `reject` |
| 自然结束 | `completed` | `completed` |
| 边界事件触发 | `boundary_<type>` | `boundary_<type>` |
| 物理删除(清理) | (无) | `delete` |

### 6.5 边界事件类型枚举(`BoundaryEventTypeEnum`)

| 类型 | 必填扩展属性 | 说明 |
|---|---|---|
| `timer` | `boundaryTimerDuration` / `boundaryTimerCycle` / `boundaryTimerDate` 三选一 | 定时器 |
| `error` | `boundaryErrorCode` | 错误事件 |
| `signal` | `boundarySignalCode` | 信号 |
| `message` | `boundaryMessageId` | 消息 |
| (通用) | `boundaryCancelActivity` | `true`=中断宿主活动,`false`=非中断 |

### 6.6 退回策略(6 种 `RollbackOperateStrategy`)

| 策略 | 场景 |
|---|---|
| `DefaultTaskNextGatewayRollbackOperateStrategy` | 默认:目标节点是 UserTask,中间有 Gateway |
| `ActiveMultiInstanceTaskRollbackOperateStrategy` | 目标节点是进行中的多实例 |
| `CompletedMultiInstanceTaskAndNextDefaultTaskRollbackOperateStrategy` | 多实例已完成,后接默认 Task |
| `CompletedMultiInstanceTaskAndNextDefaultGateWayTaskRollbackOperateStrategy` | 多实例已完成,后接 Gateway |
| `NextCallActivityRollbackOperateStrategy` | 目标节点是 CallActivity |
| `NextSubProcessRollbackOperateStrategy` | 目标节点是 SubProcess |

### 6.7 统计与监控

- 流程中心(我的已发/已办/抄送/待签收)5 个 Tab
- 流程图:高亮已办(绿)/待办(蓝)/驳回(红)/跳过(灰)
- 流程监控:流程实例数、运行时任务数、超时任务数(`act_ru_task.due_date_`)
- 流程清理:测试数据物理删除

### 6.8 审计

- 审批意见双写 `act_hi_comment` + `wf_comment_info`
- 操作日志 `sys_oper_log`(沿用 master,通过 `@Log` 注解)
- 流程启动/结束/驳回/退回/废除 全部走 `GlobalEventListener` 监听并记录

---

## 7. 非功能需求

| 属性 | 目标 |
|---|---|
| 一致性 | Flowable Command 事务 + RuoYi 业务事务隔离;意见双写保证业务侧不被引擎升级影响 |
| 可靠性 | 边界事件由 Flowable 重试机制保证;Job 异常转 `act_ru_deadletter_job` |
| 性能 | 列表分页 < 1s;流程图渲染 < 2s(PC);< 3s(移动) |
| 审计合规 | 90 天流程历史可查询;`act_hi_*` 由 Flowable 自动保留 |
| 安全 | JWT 30min + BCrypt + 流程接口 `@PreAuthorize`;`workflow:*` 权限项 |
| 可用性 | 单实例 99.9% |
| 部署 | 单一可执行 jar,`tomcat.max-threads=800`;Ehcache 缓存流程定义 |

---

## 8. 权限与角色(本分支新增)

### 8.1 workflow:* 权限点矩阵

| 模块 | 权限字符 | 操作 |
|---|---|---|
| 流程模型 | `workflow:model:list` / `add` / `edit` / `remove` / `save` / `deploy` / `export` / `import` / `suspend` / `activate` / `publish` | 增删改查 + 部署 + 挂起激活 + 发布 |
| 流程定义 | `workflow:deploy:list` / `import` / `remove` / `state` / `form` / `export` | 部署管理 |
| 流程发起 | `workflow:process:start` / `ownList` / `detail` | 发起 + 已发 + 详情 |
| 我的待办 | `workflow:task:todoList` | 待办列表 |
| 我的已办 | `workflow:task:finishedList` | 已办列表 |
| 任务操作 | `workflow:task:complete` / `reject` / `return` / `transfer` / `delegate` / `addSign` / `stop` / `claim` / `unClaim` / `revoke` / `abolish` / `updateComment` / `skip` / `editAssignee` | 13 类审批动作 |
| 表单 | `workflow:form:list` / `add` / `edit` / `remove` / `export` / `import` | 增删改查 + 导入导出 |
| 表单分类 | `workflow:formCategory:list` / `add` / `edit` / `remove` | 增删改查 |
| 表单模板 | `workflow:formTemplate:list` / `add` / `edit` / `remove` | 增删改查 |
| 流程分类 | `workflow:category:list` / `add` / `edit` / `remove` | 增删改查 |
| 表达式 | `workflow:expression:list` / `add` / `edit` / `remove` / `export` | 增删改查 + 导出 |
| 流程迁移 | `workflow:processMigrate:migrate` | 跨版本迁移 |
| 流程清理 | `workflow:processClear:clear` | 测试数据清理 |
| 边界事件 | `workflow:boundaryEvent:list` / `config` / `trigger` | 触发记录 + 配置 + 测试触发 |
| 抄送 | `workflow:copy:list` / `add` | 抄送记录 |

### 8.2 节点「授权功能」枚举(10 项,2025-Q4 改)

| 值 | 含义 | 渲染按钮 |
|---|---|---|
| `complete` | 同意 | ✓ |
| `reject` | 驳回 | ✓ |
| `return` | 退回 | ✓ |
| `transfer` | 转办 | ✓ |
| `delegate` | 委派 | ✓ |
| `copy` | 抄送 | **不渲染独立按钮**,随同意/驳回一起提交 |
| `mulitiSequentialUnComplete` | 会签回退(仅顺序会签) | ✓ |
| **`addSignBefore`** | 前加签 | ✓ |
| **`addSignAfter`** | 后加签 | ✓ |
| **`stop`** | 终止 | ✓ |

> 「授权功能」勾上后,前端 `actionMeta.js` 的 `buildActionButtons()` 才翻译出对应按钮。3 处文件必须同步:`actionMeta.js` / `UserTask.vue` / `TaskDialog.vue`。

### 8.3 数据权限(沿用 master)

`@DataScope` + `DataScopeAspect` 适用;流程业务表 `wf_*` 当前未挂注解,可按需加。

---

## 9. 边界与不做清单

| 项 | 说明 |
|---|---|---|
| 业务表脚手架 | 走 `extend-generator` 生成;**不**本分支内置 |
| SSO 联邦 | 不做 |
| 微服务化 | 不做 |
| 多租户 | 不做 |
| Flowable 7 升级 | 走新分支 |
| 流程监控大屏 | 不做(简单列表足够) |
| 消息推送(WebSocket/邮件/短信) | `wf_copy` 落表后,**通知机制由业务侧接**;本分支不实现 |
| 流程 KPI 报表 | 不做 |

---

## 10. 验收标准

| # | 验收项 | 验收方法 |
|---|---|---|
| AC-01 | `mvn clean compile` 0 ERROR(8 个子模块) | 终端 |
| AC-02 | 应用启动成功,8 个 starter 装配 | 终端 |
| AC-03 | 7 个原有模块 + 1 个 `extend-flowable` 全部 `parent.version=3.8.2` | `mvn validate` |
| AC-04 | PostgreSQL `basicdb_flowable` schema 86 表 + 2 视图 | `psql -c "SELECT count(*) FROM information_schema.tables WHERE table_schema='basicdb_flowable'"` → 86 |
| AC-05 | admin/admin123 登录,看到「流程引擎」菜单 | 浏览器 |
| AC-06 | 新建一条简单流程(开始→审批→结束),部署,发起,审批,完成 | 浏览器 |
| AC-07 | 退回(任意节点)成功 | 浏览器 |
| AC-08 | 前加签 / 后加签成功 | 浏览器 |
| AC-09 | 会签(过半通过)成功 | 浏览器 |
| AC-10 | 抄送成功,`wf_copy` 有记录 | DB + 浏览器 |
| AC-11 | 移动端 web-mp 看到待办,审批后 PC 端可见 | 浏览器(双端) |
| AC-12 | 流程图高亮已办(绿)/待办(蓝) | 浏览器 |
| AC-13 | 边界事件 timer 2 天后触发,`processStatus='boundary_timer'` | DB |
| AC-14 | 流程清理:测试数据物理删除 | DB + 浏览器 |
| AC-15 | `act_hi_comment` 与 `wf_comment_info` 同步 | DB |

---

## 附录A 待确认问题清单

| 编号 | 问题 | 建议默认 | 阻塞 |
|---|---|---|---|
| A-1 | 业务数据是否落业务表(改 `wf_task_form` 脚手架)? | 阶段 1:沿用脚手架;阶段 2:新业务必须落业务表 | 否 |
| A-2 | 流程通知机制(WebSocket/邮件/短信)? | 阶段 1:抄送落 `wf_copy`,通知由业务侧接;阶段 2:WebSocket 集成 | 否 |
| A-3 | 流程图移动端用 web-view 还是静态 PNG? | 阶段 1:web-view(兼容性差);阶段 2:静态 PNG(已存 `act_ge_bytearray`) | 否 |
| A-4 | 流程版本号语义? | 严格递增(部署一次 +1) | 否 |
| A-5 | 跨租户是否需要? | 否 | 否 |
| A-6 | 流程监控大屏? | 不做;后台列表够用 | 否 |
| A-7 | 抄送人是否需要「知会」按钮触发? | 暂不支持,仅在审批时勾选 | 否 |

---

## 附录B 业务回填模板

```markdown
### 业务: <名称,如请假/报销>
- 流程模型 key: <modelKey>
- 节点列表: <节点 + 审批人 + 授权功能>
- 表单: <formCode, 字段表>
- 业务表: <sys_leave / sys_expense_report ...>
- 触发入口: <业务页面 URL>
- 关联接口: <BFF 或 Controller>
```

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:覆盖 13 Controller + 9 类工作流业务表 + 10 项授权功能 + 6 种退回策略 + 4 类边界事件 + 15 AC |
