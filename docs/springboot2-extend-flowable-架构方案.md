# 基础平台(base-postgresql-vue3) 架构方案 — `springboot2-extend-flowable` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot2-extend-flowable`** 分支(Spring Boot 2.5.14 + Flowable 6.8.0)
> 上游依赖:RuoYi-Vue 3.8.2 / Spring Boot 2.5.14 / Flowable 6.8.0 / PostgreSQL 15+
> 同主题文档:`docs/流程引擎使用说明.md`(使用层面,本文件是集成层面)

---

## 1. 系统定位

**一句话定位**:在 master 最小基线之上,**集成 Flowable 6.8.0 流程引擎**,提供从「流程建模 → 部署 → 发起 → 审批(同意/驳回/退回/转办/委派/加签/抄送/会签)→ 结束」全链路的可配置审批能力,并保留 PC + 移动双端。

| 维度 | 内容 |
|---|---|
| 使用方 | 业务方(模型/表单/分类/表达式维护人)+ 审批人(PC 端/移动端)+ 流程发起人 |
| 触发方式 | PC web-ui + 移动 web-mp + 流程引擎内部事件(`act_ru_*` / `act_hi_*`) |
| 数据形态 | PostgreSQL `basicdb_flowable` schema(86 张表 + 2 视图)+ 流程 BPMN/PNG 资源 + Redis 缓存 |
| 范围边界 | Flowable 引擎 + 工作流业务表(10 张) + 设计器/表单设计器(前端) + 13 个流程模块 Controller + 退回/加签/动态注入等扩展 |
| 不做清单(本分支) | Spring Boot 2 → 3/4 升级(走 `springboot3` / `springboot4` 分支);多租户;SSO;Spring Cloud 微服务化 |

### 1.1 核心用户动作必做项

- 流程模型:新建/编辑/部署/导入/导出/版本管理
- 流程分类 / 表单定义 / 表单分类 / 表单模板:CRUD
- 表达式字典(`flw_expression`):维护可在节点上引用的字典值
- 发起流程:业务方按表单填数据 → 启动 → 流程中心可见
- 审批:待办 / 已办 / 已发 / 抄送 / 待签收 五个 Tab
- 审批动作:同意 / 驳回 / 退回(可任意节点) / 转办 / 委派 / 前加签 / 后加签 / 终止 / 会签回退 / 撤回 / 废除
- 流程图:高亮展示(已办/待办/驳回/跳过)
- 移动端:待办列表、审批弹窗、流程图查看

---

## 2. 约束与质量属性

| 属性 | 目标 | 标注 |
|---|---|---|
| **JDK / Spring Boot** | 严格锁定 **JDK 11 + Spring Boot 2.5.14**(`javax.servlet`) | **最强约束**,与下游基线 `master` 一致;升 SB3 走独立分支 |
| **Flowable** | **6.8.0**(`flowable-spring-boot-starter-process`);Activiti 6.0.0(仅 `BpmnLayout` 排版库) | 版本由父 pom `<flowable.version>6.8.0</flowable.version>` 统一;不混用不同 Flowable 大版本 |
| **数据库 / Schema** | PostgreSQL ≥ 15.1,**所有对象落在 `basicdb_flowable` schema**(含 33 张 `act_*` + 9 张 `flw_*` + 10 张 `wf_*` + 23 张 `sys_*` + 11 张 `qrtz_*` + 2 张 `gen_*` + 2 视图) | **最强约束**,与下游基线 `master` 的 `basicdb` schema **不兼容**;JDBC URL 必须带 `currentSchema=basicdb_flowable` |
| **IDM 集成** | **`flowable.idm.enabled: false`** — 完全关闭 Flowable 自带用户/角色 | **关键决策**;审批人体系走 `sys_user` / `sys_role`,节点上以 `flowable:IdmAssignee` / `IdmCandidateUsers` / `IdmCandidateGroups` 扩展属性挂载 |
| 鉴权 | JWT 30min + BCrypt + 图形验证码 + Spring Security 5.5(沿用下游基线 `master`) | 与 `master` 一致 |
| 一致性 | Flowable 自带事务(ProcessEngine 事务边界下沉到 Command)+ RuoYi 事务 | 业务 Service `rollbackFor=Exception`;Command 异常向上抛 |
| 性能 | 异步执行器 `flowable.async-executor-activate: true`;Job 走 4 类 Job 表(`act_ru_job` / `act_ru_timer_job` / `act_ru_suspended_job` / `act_ru_deadletter_job` / `act_ru_external_job` / `act_ru_history_job`) | 定时器/异步任务必须开启 |
| 审计 | 审批意见同时写 `act_hi_comment`(引擎) + `wf_comment_info`(业务,便于业务查询) | 双写保证业务侧不被引擎升级影响 |
| 安全 | XSS + 重复提交 + 限流 + 显式 CORS(沿用下游基线 `master`) | — |
| 部署 | 单体 jar;`tomcat.max-threads=800`;Ehcache 缓存流程定义(`classpath:/ehcache/flow-ehcache.xml`) | 不上 K8s |

**最强约束**:① **JDK 11 + Spring Boot 2.5.14 不升级**;② **schema 锁死 `basicdb_flowable`**,绝不与下游基线 `master` 共库;③ **Flowable IDM 关闭**,人员走 RuoYi 体系;④ **`wf_task_form` 是脚手架兜底**——生产项目业务数据必须落业务表,本仓库文档已警示(README §7.8)。

---

## 3. 总体架构

### 3.1 容器视图

```
┌────────────────────────────────────────────────────────────────────┐
│  用户端                                                            │
│  ┌──────────────────┐    ┌──────────────────────────────┐         │
│  │  web-ui (PC)     │    │  web-mp (uni-app 3)           │         │
│  │  Vue3+ElementPlus│    │  含流程待办/审批/流程图        │         │
│  │  + bpmn.js 设计器│    │                              │         │
│  │  + vform3 表单   │    │                              │         │
│  └────────┬─────────┘    └──────────────┬───────────────┘         │
│           │ /workflow/** JSON            │                         │
└───────────┼──────────────────────────────┼─────────────────────────┘
            ▼                             ▼
┌────────────────────────────────────────────────────────────────────┐
│  Spring Boot 2.5.14 (basic-admin-api)                               │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │  Filter 链 + Controller(com.basic.web.controller +           │  │
│  │  com.basic.workflow.controller 13 个)                        │  │
│  └────────────────────────┬─────────────────────────────────────┘  │
│  ┌────────────────────────┴─────────────────────────────────────┐  │
│  │  Service:basic-admin-core + business-core + extend-quartz +  │  │
│  │          extend-flowable (23 个 impl) + extend-generator    │  │
│  └────────────────────────┬─────────────────────────────────────┘  │
│  ┌────────────────────────┴─────────────────────────────────────┐  │
│  │  引擎门面:FlowServiceFactory → RepositoryService /           │  │
│  │          RuntimeService / TaskService / HistoryService /     │  │
│  │          ManagementService / DynamicBpmnService              │  │
│  └────────────────────────┬─────────────────────────────────────┘  │
│  ┌────────────────────────┴─────────────────────────────────────┐  │
│  │  扩展:CustomBpmnJsonConverter / Bruce*JsonConverter /        │  │
│  │       GlobalEventListener / UserTaskListener /               │  │
│  │       RollbackCmd(退回) / InjectUserTask(加签) /             │  │
│  │       CustomProcessDiagramGenerator(流程图)                   │  │
│  └──────────────────────────────────────────────────────────────┘  │
└──────────┬──────────────────────────────────┬─────────────────────┘
           ▼                                  ▼
   ┌──────────────────────────┐       ┌──────────────────┐
   │ PostgreSQL 15.1           │       │ Redis(可选)      │
   │ schema=basicdb_flowable   │       │ 字典/Token/限流   │
   │ 86 表 + 2 视图 + 5 函数   │       │ Ehcache 流程定义  │
   └──────────────────────────┘       └──────────────────┘
```

### 3.2 模块清单(8 个 Maven 子模块,比 master 多 `extend-flowable`)

| 模块 | 职责 | 关键要点 |
|---|---|---|
| `basic-admin-api` | Spring Boot 启动 + 全部 Controller(含 workflow) | 扫描 `com.basic` + `com.core`;`@MapperScan("com.core.**.mapper")` |
| `basic-admin-core` | 业务核心(RuoYi 21 张 `sys_*` CRUD) | 沿用 master |
| `basic-framework` | 框架机制(Security/Redis/MyBatis-Plus/Filter/AOP/Token) | 沿用 master |
| `basic-common` | 通用工具/注解/枚举/基类 | 沿用 master |
| `business-core` | 业务示例(`sys_demo`) | 沿用 master |
| `extend-quartz` | 定时任务 | 沿用 master |
| `extend-generator` | 代码生成 | 沿用 master |
| **`extend-flowable`** 🆕 | **流程引擎:Flowable 6.8.0 + 13 Controller + 23 ServiceImpl + 退回/加签/动态注入/自定义 BPMN 转换/自定义流程图** | 依赖 `flowable-spring-boot-starter-process` / `flowable-json-converter` / `flowable-dmn-json-converter` / `flowable-ui-common` / `flowable-ui-modeler-logic` / `flowable-spring-boot-starter-actuator` / `org.activiti:activiti-engine`/`activiti-bpmn-layout` 6.0.0(仅排版)/ `aviator` 表达式 / `dom4j` XML / `swagger-models` 1.6.2 强制覆盖 / `fastjson` 1.2.83 |

### 3.3 数据模型核心表

| 域 | 表 | 说明 |
|---|---|---|
| **Flowable 引擎-模型** | `act_re_model` / `act_de_model` / `act_de_model_history` / `act_de_model_relation` | 设计器模型 |
| **Flowable 引擎-部署** | `act_re_deployment` / `act_re_procdef` / `act_ge_bytearray` / `act_ge_property` / `act_procdef_info` / `act_evt_log` | 部署产物 + BPMN/PNG 资源 |
| **Flowable 引擎-运行** | `act_ru_execution` / `act_ru_task` / `act_ru_actinst` / `act_ru_identitylink` / `act_ru_entitylink` / `act_ru_variable` / `act_ru_event_subscr` / `act_ru_job` / `act_ru_timer_job` / `act_ru_suspended_job` / `act_ru_deadletter_job` / `act_ru_external_job` / `act_ru_history_job` | 运行时 |
| **Flowable 引擎-历史** | `act_hi_procinst` / `act_hi_taskinst` / `act_hi_actinst` / `act_hi_identitylink` / `act_hi_entitylink` / `act_hi_varinst` / `act_hi_detail` / `act_hi_comment` / `act_hi_attachment` / `act_hi_tsk_log` | 历史 |
| **Flowable 事件注册** | `flw_ev_databasechangelog` / `flw_ev_databasechangeloglock` / `flw_event_definition` / `flw_event_deployment` / `flw_event_resource` / `flw_channel_definition` / `flw_ru_batch` / `flw_ru_batch_part` | 事件引擎 |
| **工作流业务** | `wf_category` / `wf_category_org_link`(保留)/ `wf_comment_info` / `wf_copy` / `wf_deploy_form` / `wf_form` / `wf_form_category` / `wf_form_template` / `wf_task_form` / `tbl_flow_extend_procinst` | 本项目自研 |
| **表达式字典** | `flw_expression` | 流程中可用的字典值 |
| **RuoYi 基础** | `sys_user` / `sys_role` / `sys_menu` / `sys_dept` / `sys_post` / `sys_user_role` / `sys_role_menu` / `sys_role_dept` / `sys_user_post` 等 23 张 | 沿用 master |
| **定时任务** | `qrtz_*` 11 张 | 沿用 master |
| **代码生成** | `gen_table` / `gen_table_column` + 2 视图(`list_table` / `list_column`) | 沿用 master,但**视图 `n.nspname` 过滤写死 `basicdb_flowable`**,schema 名不能改 |

### 3.4 关键流程

#### 3.4.1 流程建模 → 部署

```
前端 bpmn.js                    WfModelController           WfModelServiceImpl            Flowable 引擎
   │  save(BPMN JSON)             │                              │                            │
   │ ────────────────────────────▶  save(...)                   │                            │
   │                              │ ──────────────────────────▶  validate(BPMN)            │
   │                              │                              │ ────────────────────────▶  act_re_model
   │                              │                              │  act_de_model              │
   │ ◀────────────────────────────  ok                          │  ModelUtils.toBpmn         │
   │                              │                              │                            │
   │  deploy(modelId)             │                              │                            │
   │ ────────────────────────────▶  deploy(...)                 │                            │
   │                              │ ──────────────────────────▶  convert + deploy           │
   │                              │                              │ ────────────────────────▶  act_re_deployment
   │                              │                              │  act_re_procdef             │
   │                              │                              │  act_ge_bytearray           │
```

#### 3.4.2 发起 → 审批 → 结束

```
发起人 (PC/Mobile)
   │  POST /workflow/process/start { formData, variables }
   ▼
WfProcessController.startProcess
   ▼
WfProcessServiceImpl
   ├─ 1. 业务数据持久化(若新建业务表) ⚠️ 生产应改
   ├─ 2. tbl_flow_extend_procinst 写扩展(发起人/状态/businessKey)
   ├─ 3. RuntimeService.createProcessInstanceByKey
   │     ├─ act_ru_execution / act_ru_task
   │     └─ UserTaskListener.assigneeResolver 解析 flowable:IdmAssignee
   └─ 4. wf_deploy_form 拷表单快照(实例实际读这份)

审批人 → /workflow/task/complete
   ▼
WfTaskServiceImpl.complete
   ├─ 写 wf_comment_info + act_hi_comment(双写)
   ├─ TaskService.complete(taskId, variables)
   └─ GlobalEventListener 写 processStatus 变量

流程结束:
   │  runtimeService.deleteProcessInstance / 自然结束
   ▼
act_hi_procinst(状态=COMPLETED)
tbl_flow_extend_procinst.process_status='completed'
```

#### 3.4.3 退回(任意节点)

```
WfTaskController.return → WfTaskServiceImpl.return
   ▼
RollbackCmd.execute → RollbackOperateStrategy.rollback
   ├─ AbstractRollbackOperateStrategy 抽象
   ├─ DefaultTaskNextGatewayRollbackOperateStrategy
   ├─ ActiveMultiInstanceTaskRollbackOperateStrategy
   ├─ CompletedMultiInstanceTaskAndNextDefaultTaskRollbackOperateStrategy
   ├─ NextCallActivityRollbackOperateStrategy
   ├─ NextSubProcessRollbackOperateStrategy
   └─ RollbackEventListener 通知
```

#### 3.4.4 加签(前加签/后加签)

```
WfTaskController.beforeAddSignTask
   ▼
InjectUserTaskInProcessInstanceCmd.execute
   ├─ DynamicUserTaskBuilder 构建新 UserTask BPMN 节点
   ├─ AbstractDynamicInjectionCmd 注入引擎执行流
   └─ 写 wf_comment_info 标注"前加签"
```

### 3.5 状态机

| 对象 | 状态 | 流转 |
|---|---|---|
| 流程实例 | `running` / `completed` / `reject` / `return` / `canceled` / `delete`(`ProcessStatus` 枚举) | 写入 `processStatus` 流程变量 + `tbl_flow_extend_procinst.process_status` |
| 流程定义 | 激活 / 挂起 | `act_re_procdef.suspension_state_` 1/2 |
| 模型 | draft / published | `act_re_model.version` + `act_de_model` |
| 任务 | `act_ru_task` 创建/认领/完成/退回 | 引擎自动 |
| 节点「授权功能」 | `complete` / `reject` / `return` / `transfer` / `delegate` / `copy` / `mulitiSequentialUnComplete` / `addSignBefore` / `addSignAfter` / `stop` | 节点 `flowable:IdmPermission` JSON 数组 |
| 边界事件触发 | `running` 期间,触发 boundary → 状态 `boundary_<type>`(`ProcessConstants.BOUNDARY_TRIGGER_STATUS_PREFIX`) | 仅用于列表筛选"被 boundary 触发的流程" |

---

## 4. 关键决策 ADR

| # | 决策点 | 采纳方案 | 备选 + 放弃原因 | 是否演进锁死 |
|---|---|---|---|---|
| ADR-01 | 基础平台底座 | 沿用下游基线 `master`(锁 JDK 11 + SB 2.5.14) | 升 SB3 走独立分支;新写框架:重复造轮 | **是** |
| ADR-02 | 数据库 schema | **`basicdb_flowable`**(覆盖 RuoYi + Quartz + Flowable + 工作流业务) | 沿用 master `basicdb`:Flowable 自带表与业务表跨 schema 维护,视图 `n.nspname` 过滤写死会失配 | **是** |
| ADR-03 | 数据导入方式 | `pg_dump` + `INSERT INTO`(不用 `COPY`) | `COPY FROM stdin`:GUIs 无法导入;**当前分支选 `INSERT`** 兼容性最好,但代价是文件更大 | 否 — 下次导出可改 |
| ADR-04 | 流程引擎 | Flowable 6.8.0 | Activiti 7:同源但社区分裂;Camunda:商业化风险;自研:成本不划算 | **是** |
| ADR-05 | Flowable IDM | **`idm.enabled: false`**,人员完全走 RuoYi `sys_user`/`sys_role` | 启用 IDM:数据双源、维护双账号体系 | **是** |
| ADR-06 | 节点审批人 | 自定义扩展属性 `flowable:IdmAssignee` / `IdmCandidateUsers` / `IdmCandidateGroups` + `UserTaskListener` 解析 | 用 Flowable 原生 `assignee`/`candidateUsers`:不支持动态解析表达式/角色/候选人 | 否 |
| ADR-07 | 表单设计器 | vForm3(前端独立项目,生成 JSON 落 `wf_form.content`) | 自研拖拽:成本高;FormMaking:不活跃 | 否 |
| ADR-08 | 流程图 | **自定义** `CustomProcessDiagramGenerator` + `CustomProcessDiagramCanvas`(基于 Activiti 6.0.0 BpmnLayout) | Flowable 默认生成器:不支持高亮已办/待办/驳回 | 否 |
| ADR-09 | 退回/加签实现 | `RollbackCmd` + `RollbackOperateStrategy` 策略模式(6 种);`InjectUserTaskInProcessInstanceCmd` 动态注入 | Flowable 原生仅支持驳回上一节点 | 否 |
| ADR-10 | BPMN JSON 转换 | `CustomBpmnJsonConverter` + `Bruce*JsonConverter`(UserTask/SubProcess/CallActivity/BoundaryEvent)| 默认 `BpmnJsonConverter` 不认识 `flowable:Idm*` 扩展属性 | 否 |
| ADR-11 | 业务表落地策略 | **脚手架兜底**:`wf_task_form.form_content` 存整个 JSON;**生产应改**:业务字段值落业务表,`wf_task_form` 只存元数据 | 全部存 JSON:无类型约束、无索引、无法 JOIN;全部存业务表:本仓库需要新增业务表脚手架 | **是**(警示性)— README §7.8 已标 |
| ADR-12 | 前端双轨 | PC(`web-ui`)+ 移动(`web-mp`)并立;PC 集成 bpmn.js + vform3 | 只做 PC:移动办公断流;只做移动:运营桌面效率低 | **是** |
| ADR-13 | 包命名 | `com.basic` + `com.core`(业务)双前缀;`com.basic.workflow` 流程模块顶级包 | — | 否 |
| ADR-14 | 异常事件处理 | Flowable 6 类 Job 表分桶(`timer` / `suspended` / `deadletter` / `external` / `history`) | 单 Job 表:大表下查询/索引都退化 | 否 |
| ADR-15 | 审批意见双写 | 写 `act_hi_comment`(引擎)+ `wf_comment_info`(业务) | 只写引擎:业务侧难聚合;只写业务:引擎升级会丢 | **是** |

---

## 5. 里程碑

| 阶段 | 内容 | 验收 | 触发升级的信号 |
|---|---|---|---|
| **M0(已交付,master)** | RuoYi 3.8.2 基础 + SQL 迁 PostgreSQL + 锁屏 | 详见 master 文档 | — |
| **M1(已交付,本分支)** | 在 `master` 基础上集成 Flowable 6.8.0:引擎 + 13 Controller + 23 ServiceImpl + 10 张工作流业务表 + 42 张引擎表(act_*/flw_*) + 退回/加签/动态注入/自定义 BPMN 转换/自定义流程图 + PC 端设计器集成 + 移动端审批集成 | `mvn clean compile` 0 ERROR;`basicdb_flowable` schema 86 表 + 2 视图;能画一条流程并发起、审批、结束 | — |
| **M2(本分支 TODO)** | 业务数据落业务表改造:`wf_task_form.form_content` 不再存业务字段,改为只存元数据 | 新建业务表 `sys_leave` / `sys_expense_report` 示例,流程节点完成后写业务表 | 出现需要「按业务字段查询流程数据」的需求 |
| **M3(可选)** | 流程监控大屏:act_ru_task 实时看板,逾期任务告警 | 流程中心新增 Dashboard | 流程实例 > 1000 |
| **M4(分支)** | Spring Boot 3.5 + JDK 17 + Jakarta + springdoc | 单独分支 `springboot3` / `springboot3-extend-flowable` | 公司层面升级 JDK 基线 |
| **M5(分支)** | Spring Boot 4 + JDK 21 + Spring Security 7 | 单独分支 `springboot4` / `springboot4-extend-flowable` | — |

**本次不做,直到有信号**:多租户、SSO、微服务拆分、K8s、APM、消息中心抽象、跨库跨引擎联邦。

---

## 6. 审查与遗留风险

### 6.1 已覆盖

| 维度 | 设计 |
|---|---|
| 失败设计 | `GlobalExceptionHandler`(RuoYi)+ `RollbackCmd` 失败冒泡引擎 + `WfProcessClearController` 测试数据清理 |
| 幂等 | 部分 API `@RepeatSubmit`(沿用 master);流程发起走 `businessKey` 唯一防重 |
| 权限 | `workflow:*` 菜单 + `@PreAuthorize("@ss.hasPermi('workflow:task:complete')")` |
| 一致性 | Flowable Command 事务 + RuoYi 业务事务隔离;审批意见双写 `act_hi_comment` + `wf_comment_info` |
| 可观测 | `flowable-spring-boot-starter-actuator` + RuoYi `sys_oper_log` + 流程图高亮 |
| 退回/加签 | 6 种 Rollback 策略 + 动态注入 |
| 流程图 | 自定义生成器支持高亮 |
| 跨端审批 | PC + 移动端共用 `/workflow/**` REST |

### 6.2 风险清单

| # | 风险 | 影响 | 缓解 |
|---|---|---|---|
| R1 | Spring Security 5.5 EOL,无安全补丁 | 高 | 走 `springboot3` 分支升级;网关补 WAF;**master/flowable 分支锁版本** |
| R2 | **`wf_task_form.form_content` 存业务数据**(脚手架) | 高 | README §7.8 已警示;改造示例已附;M2 排期 |
| R3 | Flowable 6.8.0 距今较旧,7.x 已有大改 | 中 | 6.8.0 仍稳定可用;升 7.x 走新分支 |
| R4 | Activiti 6.0.0 混用(仅排版库) | 中 | 已通过显式 `<version>6.0.0</version>` 锁定;不能改主版本 |
| R5 | FastJSON 1.2.83 已知 CVE | 中 | 仅 `extend-flowable` 旧代码依赖(WfTaskFormServiceImpl 等);MasterJacksonConfig 用 Jackson;评估替换 |
| R6 | `swagger-models` 强制覆盖到 1.6.2(为兼容 Flowable 6.8.0 内部) | 低 | 已 `<version>1.6.2</version>` 锁定;不影响主 POM |
| R7 | 视图 `list_table` / `list_column` 写死 `basicdb_flowable` | 低 | schema 名不变即可;若改 schema 名,视图必须重建 |
| R8 | 流程资源/PNG 落库(act_ge_bytearray),长期占库 | 中 | 流程定义版本管理;归档策略待定 |
| R9 | 节点「授权功能」枚举从 7 项扩到 10 项(2025-Q4 改),前端三处文件需同步 | 中 | `actionMeta.js` / `UserTask.vue` / `TaskDialog.vue` 已记录;变更时需双改 |
| R10 | Flowable Modeler 用了官方 modeler 逻辑(`flowable-ui-modeler-logic`),未来 Flowable 升级 modeler 包名可能再变 | 中 | 锁版本;升级时先跑兼容性验证 |
| R11 | 移动端流程图:用 web-view 加载 bpmn.js 渲染,首屏慢 | 低 | 改用图片缓存(PNG 落库已存) |
| R12 | `app.idm-url` / `idm-admin` 默认 `http://localhost:8080/flowable-demo`/`test/test`(沿用官方) | 低 | 即使 idm.enabled=false 也会读这两个值,建议改占位值防泄露 |
| R13 | `extendsflowable` 模块体积大(Flowable 6.8.0 + Activiti 6.0.0 + ui-modeler 全套),单体 jar 偏大 | 低 | 不上 K8s 不影响;后续按需拆 `wf-core` / `wf-modeler` / `wf-runtime` |
| R14 | 业务数据 JSON 解析 `form_content::jsonb ->> '字段'` 无 B-tree 索引 | 中 | R2 缓解;临时可加 GIN 索引 |
| R15 | 撤回/废除:仅发起人可撤回 / 流程中心可废除,易越权 | 低 | 前端按钮按 `wfTaskVo.createBy` 判断;后端 `WfTaskServiceImpl.revokeProcess` 内再校一次 |

---

## 7. 参考来源

- `README.md`(仓库根,§2~§7 本分支实际内容)
- `docs/流程引擎使用说明.md`(使用手册,本文件不重复)
- `extend-flowable/pom.xml` / `extend-flowable/src/main/java/...`
- `pom.xml` / `application.yml`(Flowable 段)
- Flowable 6.8.0 / Activiti 6.0.0(BpmnLayout 排版)官方文档
- 同仓库基线分支文档:`docs/base-postgresql-vue3-架构方案.md`(在 `master` 分支生成,描述 master 自身)— 本文件以 master 为下游基线,**只描述本分支(`springboot2-extend-flowable`)的差异点**(新增 `extend-flowable` 子模块 / `basicdb_flowable` schema / Flowable 集成 / `workflow:*` 权限 / 10 张工作流业务表等)

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿,基于 `springboot2-extend-flowable` 分支实际扫描(8 个子模块 + 86 张表 + 13 Controller + 23 ServiceImpl) |
