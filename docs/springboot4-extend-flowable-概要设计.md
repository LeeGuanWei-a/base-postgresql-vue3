# 基础平台(base-postgresql-vue3) 概要设计(HLD) — `springboot4-extend-flowable` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot4-extend-flowable`** 分支
> 前置依赖:《架构方案》v0.1、《需求文档》v0.1
> 本分支 = **`springboot4` 基线(SB 4.1.1 / JDK 21 / Jackson 3 / Security 7)+ `extend-flowable` 集成 Flowable 8.0.0**。
> 平台部分(接口清单 / 平台数据设计 / 部署拓扑 / 平台并发模型)与 `springboot4` **一致**(本文档不重复);**新增 §2.5 extend-flowable 模块结构、§3.7 流程引擎八大流程、§6 HD-29~HD-42、§8 P31~P46**。
> 🔶 = 本分支新增;⬆️ = `springboot4` 继承。

---

## 1. 设计目标与约束回顾

| 来源 | 关键约束 |
|---|---|
| 架构方案 §2 | **JDK 21 + SB 4.1.1 + Jackson 3 + Security 7**(⬆️);**Flowable 8.0.0**;**schema `basicdb_flowable8`**;**90 表 + 2 视图**;**BPMN XML 模型存储**;**无 `act_de_*`** |
| 架构方案 §2 | `idm.enabled: false`;`history-level: audit`;`allow-circular-references: true`;前端 bpmn-js 17.11.1 + vform3 3.0.10 |
| 需求文档 §7 | 待办 1000 条 < 1s;流程图 < 2s;发起 < 1s;审批意见双写;迁移事务回滚 |
| 需求文档 §10 | AC-01~AC-66(其中 **AC-43~AC-66 为流程引擎专项 24 项**) |

**勿重复大段正文**,仅作引子。

---

## 2. 逻辑架构与模块划分

### 2.1 工程结构(🔶 本分支;⬆️ 继承)

```
base-postgresql-vue3/
├── pom.xml                 # 父 pom,🔶 8 子模块(⬆️ 原 7 + extend-flowable)
│                            ⬆️ java.version=21 / spring-boot.version=4.1.1
│                            🔶 flowable.version=8.0.0
├── basic-admin-api/        # 启动入口 + 平台 Controller                       ⬆️
├── basic-admin-core/       # 平台业务核心                                    ⬆️
├── basic-framework/        # 框架能力(Security 7 / Jackson 3 / Redis / MP)  ⬆️
├── basic-common/           # 工具/注解/枚举/基类                             ⬆️
├── business-core/          # 业务扩展点(Demo)                              ⬆️
├── extend-quartz/          # Quartz(RAMJobStore)                            ⬆️
├── extend-generator/       # 代码生成                                       ⬆️
├── extend-flowable/        # 🔶 新增:流程引擎集成 + 定制层(223 Java)
│   ├── pom.xml             #   flowable-spring-boot-starter-process 8.0.0
│   └── src/main/java/com/basic/
│       ├── constant/       #   节点扩展属性常量(ExtensionConstant)
│       ├── enm/            #   流程/意见枚举
│       ├── flowable/       #   引擎级封装
│       │   ├── common/{constant,enums}
│       │   ├── config/     #     引擎启动校验
│       │   ├── flow/       #     流程图生成(画布)、节点推导、Flowable 工具
│       │   ├── listener/   #     全局事件 / 到期 / 用户任务监听
│       │   └── utils/      #     模型/表单/任务/安全 XML/ID 工具
│       └── workflow/       #   业务层
│           ├── controller/ #     13 个 Wf* Controller
│           ├── service/    #     接口 + 19 impl + wfModel + wfProcessInfo/Task 子服务
│           ├── service/migrate/  # 迁移(Plan/Result/Exception)
│           ├── cmd/        #     Flowable Command(动态注入/加意见/回滚 + 6 策略)
│           ├── domain/     #     实体 / BO / VO
│           ├── mapper/     #     MyBatis-Plus(含直接操作 act_* 的 SQL)
│           └── handler|builder  # 多实例处理 / 动态任务构建
├── file-sql/               # 🔶 basicdb-vue3.sql(376.8 KB,90 表 + 2 视图)
├── docs/                   # ⬆️ 流程引擎使用说明.md(本分支已有使用手册)
├── web-ui/                 # PC(JS)
│   ├── src/views/basic-admin/**        ⬆️ 平台页面(未改)
│   ├── src/views/basic-extend/workflow/**  🔶 21 个 .vue(工作流)
│   ├── src/plugins/package/**          🔶 bpmn-js 17.11.1 + properties-panel 5.28
│   └── src/api/workflow/**             🔶 工作流 API
└── web-mp/                 # 移动(JS)⬆️ 零改动(无工作流)
```

### 2.2 平台组件职责(⬆️ 全部继承 `springboot4`,**本分支未改一行**)

| 模块 | 关键类 | 本分支 |
|---|---|---|
| `basic-admin-api` | `BasicApplication` / `SwaggerConfig` / 24 个平台 Controller | ⬆️ 零改动 |
| `basic-admin-core` | `Sys*ServiceImpl` / Mapper / 实体 | ⬆️ 零改动 |
| `basic-framework` | `SecurityConfig`(7)/ `BigNumberModuleConfig` / `RedisConfig` / `DruidConfig` / `UserAgentConfig` | ⬆️ 零改动 |
| `basic-common` | `JsonUtils`(Jackson 3)/ `RedisCache` / `LoginUser`(`getAuthorities()`) | ⬆️ 零改动 |
| `extend-quartz` | `ScheduleConfig`(starter + RAMJobStore) | ⬆️ 零改动 |
| `extend-generator` | `GenController` / `VelocityUtils` | ⬆️ 零改动 |

### 2.3 扩展 SPI(⬆️ 继承 + 🔶 流程新增)

| SPI | 接入方式 | 用例 |
|---|---|---|
| ⬆️ 自定义 Job | `@Component("ryTask")` | `ryTask.ryNoParams` |
| ⬆️ 自定义权限注解 | `@PreAuthorize("@ss.hasPermi(...)")` | — |
| ⬆️ 自定义 JSON 序列化 | `JsonMapperBuilderCustomizer`(独立类) | 大数 |
| **🔶 自定义节点扩展属性** | 在 `ExtensionConstant` 加常量 + 前端属性面板 + 转换器 | 新审批人类型 |
| **🔶 自定义事件监听** | 实现 `FlowableEventListener` 并注册到全局监听 | 业务事件 |
| **🔶 自定义退回策略** | 实现 `RollbackOperateStrategy` + 注册到工厂 | 特殊节点类型 |
| **🔶 自定义边界事件类型** | 扩展 `BoundaryEventTypeEnum`(需 BPMN 支持) | — |

### 2.4 数据设计(概要)

| schema | 表 | 视图 | 内容 |
|---|---|---|---|
| **`basicdb_flowable8`** | **90** | **2** | `act_*` 39 / `flw_*` 7 / `wf_*` 9 / `tbl_*` 1 / `sys_*` 21 / `qrtz_*` 11 / `gen_*` 2 |

> 🔶 **与 `springboot4` 的本质差异**:schema 从 `basicdb`(34 表)换成 `basicdb_flowable8`(90 表 + 2 视图);平台 34 张表全部保留,新增 56 个对象。

**运行期非 DB 存储**:

| 存储 | Key | TTL | 备注 |
|---|---|---|---|
| Redis | `login_tokens:<uuid>` | 30min | ⬆️ Jackson 3 |
| Redis | `sys_dict::<dictType>` | — | ⬆️ |
| Redis | `REPEAT_SUBMIT:*` | 500ms | ⬆️ |
| **进程内存** | Quartz RAMJobStore | 进程 | ⬆️ 任务运行态 |
| **进程内存** | **Flowable 异步执行器 / Job 队列** | 进程 | **🔶 定时器/异步任务运行态** |

> ⚠️ **多实例风险**:Quartz RAMJobStore + Flowable 内存 Job 队列,多实例部署需验证(见 P37)。

### 2.5 🔶 `extend-flowable` 模块结构(本分支核心)

#### 2.5.1 两层结构

```
com.basic.flowable  ── 引擎层(与 Flowable API 直接交互)
   ├── common/constant   ProcessConstants(流程状态 key、边界事件扩展属性名)
   ├── common/enums      BoundaryEventTypeEnum(8 型)/ ProcessStatus / FormType / FlowComment
   ├── config            FlowBpmnConfig(EngineConfigurationConfigurer)
   ├── flow              CustomProcessDiagramGenerator / Canvas、FindNextNodeUtil、FlowableUtils
   ├── listener          GlobalEventListener(全局事件)/ ProcessDueTimeListener(到期)/ UserTaskListener(创建)
   └── utils             ModelUtils / ProcessFormUtils / ProcessUtils / TaskUtils / UUIDGenerator

com.basic.workflow  ── 业务层(平台业务 + 定制)
   ├── controller        13 个 Wf* Controller
   ├── service           接口 + impl
   │   ├── wfModel       WfModelSaveXmlService(写 BPMN XML)/ WfModelQueryXmlService(读)
   │   ├── wfProcessInfo / wfProcessTask   细粒度子服务
   │   └── migrate       WfProcessMigratePlan / Result / Exception
   ├── cmd               AbstractDynamicInjectionCmd / InjectUserTaskInProcessInstanceCmd
   │                     AddHisCommentCmd / RollbackCmd
   │                     rollBack/  6 种策略 + 工厂
   ├── domain            实体 / BO / VO
   ├── mapper            MyBatis-Plus(含直接操作 act_* 的 SQL)
   └── handler|builder   多实例处理 / DynamicUserTaskBuilder
```

#### 2.5.2 13 个 Controller 与 19 个 ServiceImpl 的对应关系

| Controller | 主要 Service | 职责 |
|---|---|---|
| `WfCategoryController` | `WfCategoryServiceImpl` | 流程分类树 |
| `WfModelController` | `WfModelServiceImpl` + `WfModelSaveXmlService` / `WfModelQueryXmlService` | 模型 CRUD / 保存 XML / 部署 / 激活 |
| `WfDeployController` | `WfDeployServiceImpl` + `WfDeployFormServiceImpl` | 部署管理 + 表单快照 |
| `WfFormController` | `WfFormServiceImpl` | 表单 CRUD |
| `WfFormCategoryController` | `WfFormCategoryServiceImpl` | 表单分类 |
| `WfFormTemplateController` | `WfFormTemplateServiceImpl` | 表单模板 |
| `WfExpressionController` | `WfExpressionServiceImpl` | 表达式字典 |
| `WfProcessController` | `WfProcessServiceImpl` + `WfProcessClearServiceImpl` | 实例 / 发起 / 待办 / 已办 / 抄送 / 流程图 / 清理 |
| `WfTaskController` | `WfTaskServiceImpl` + `WfFlowableTaskServiceImpl` | **18 类审批动作** |
| `WfTaskFormController` | `WfTaskFormServiceImpl` | 节点表单 |
| `WfBoundaryEventController` | `WfBoundaryEventServiceImpl` | 边界事件查询 / 4 个触发接口 |
| `WfProcessMigrateController` | `WfProcessMigrateServiceImpl` | 迁移 |
| `WfProcessClearController` | `WfProcessClearServiceImpl` | 清盘(46 表) |

> 其余 ServiceImpl 含:`WfBpmnModelServiceImpl` / `WfCopyServiceImpl` / `WfCommentInfoServiceImpl` / `IWfExtendProcinstServiceImpl` 等。

#### 2.5.3 引擎层与业务层的边界

| 层 | 允许做 | **禁止做** |
|---|---|---|
| `com.basic.flowable` | 调 Flowable API、管 BPMN 模型、生成流程图、注册监听器 | **不写平台业务表**(除只读) |
| `com.basic.workflow` | 写 `wf_*` / `tbl_flow_extend_procinst`、编排事务、暴露 REST | **不直接操作 Flowable 内部表**(走 Service)例外:部分 Mapper 有 `act_*` 只读 SQL |

#### 2.5.4 前端结构

```
web-ui/src/
├── views/basic-extend/workflow/     🔶 21 个 .vue
│   ├── model/        流程模型(列表 / 设计器容器)
│   ├── process/      流程实例(发起 / 待办 / 已办 / 抄送 / 详情 / 流程图)
│   ├── form/         表单(列表 / 设计器容器)
│   ├── category/     分类
│   ├── expression/   表达式
│   └── deploy/       部署
├── plugins/package/   🔶 bpmn-js 17.11.1
│   ├── designer/     设计器(BpmnModeler 封装)
│   ├── penal/        属性面板(含 UserTask / BoundaryEvent 自定义属性)
│   └── ...
└── api/workflow/      🔶 13 组 API(与 Controller 对应)
```

---

## 3. 核心流程设计

### 3.1 ~ 3.6 平台流程(⬆️ 与 `springboot4` 一致)

| 小节 | 内容 | 状态 |
|---|---|---|
| 3.1 登录流程 | kaptcha + JWT + Security 7 | ⬆️ 一致 |
| 3.2 锁屏/解锁 | 复用 `/login` | ⬆️ 一致 |
| 3.3 定时任务 | Quartz starter + RAMJobStore | ⬆️ 一致 |
| 3.4 代码生成 | Velocity 模板 | ⬆️ 一致 |
| 3.5 移动端登录 | H5/小程序 | ⬆️ 一致 |
| 3.6 SB4 升级流程 | Jackson 3 / Security 7 | ⬆️ 见 `docs/springboot4-概要设计.md` §3.6 |

### 3.7 🔶 流程引擎八大流程(本分支新增)

#### 3.7.1 建模 → 保存 → 校验 → 部署(BPMN XML)

```
[前端 bpmn-js 17]
   │ 用户在 canvas 建模;属性面板配:
   │   IdmAssignee / IdmCandidateUsers / IdmCandidateGroups
   │   IdmPermission(节点授权功能)
   │   IsSkip / IsNullSkip / IsExamined / IsRoleReplace / IsRevoke
   │   formCode(绑定表单)
   │   边界事件(timer/message/signal/...)
   ▼
POST /workflow/model/save  (BPMN XML 字符串)
   │
   ▼
🔶 WfModelSaveXmlService
   │  ① ProcessValidator 校验 BPMN 合规(有错 → 返回错误列表)
   │  ② act_re_model 写入(name / key / category / version)
   │  ③ BPMN XML → act_ge_bytearray(editor source)
   ▼
POST /workflow/model/deploy
   │
   ▼
repositoryService.createDeployment()
   .name(...).key(...)
   .addString(modelKey + ".bpmn20.xml", bpmnXml)
   .deploy()
   │  → act_re_deployment / act_re_procdef / act_ge_bytearray
   ▼
🔶 WfDeployFormServiceImpl:拷贝 wf_form.content → wf_deploy_form(快照)
   │
   ▼
✅ 可发起(procDefKey 最新版本)

⚠️ 与 6.8 分支差异:
   6.8 存「设计器 JSON」,读时用 flowable-json-converter 转 BPMN
   8.0 直接存 BPMN XML(Flowable 7 起删除了 json-converter)
```

**校验失败处理**:

| 失败点 | 返回 | 前端 |
|---|---|---|
| BPMN 不合法 | `AjaxResult.error(错误列表)` | 高亮问题节点 + 提示 |
| modelKey 重复 | `AjaxResult.error("key 已存在")` | 提示改名 |
| 表单 formCode 不存在 | `AjaxResult.error("表单不存在")` | 提示先建表单 |

#### 3.7.2 发起流程

```
POST /workflow/process/start {procDefKey, variables, formData}
   │
   ▼
🔶 WfProcessServiceImpl
   │  ① 查询最新版且激活的 ProcessDefinition(无 → 报错)
   │  ② 写 tbl_flow_extend_procinst(procDefKey / startUser / processStatus=running)
   │  ③ Authentication.setAuthenticatedUserId(当前用户)   ← 关键:让 Flowable 记录发起人
   │  ④ runtimeService.startProcessInstanceByKey(procDefKey, businessKey=extId, variables)
   │  ⑤ 首节点表单 → wf_task_form
   │  ⑥ 回写 proc_ins_id 到 ext 表
   ▼
✅ 返回 procInsId;下一节点审批人待办出现
```

#### 3.7.3 审批(同意 / 驳回)

```
POST /workflow/task/complete {taskId, comment, variables, copyUserIds}
   │
   ▼
🔶 WfTaskServiceImpl.complete
   │  ① 校验:task.assignee == 当前用户(否则要求先 claim)
   │  ② 校验:节点 IdmPermission 含 "complete"
   │  ③ 🔶 AddHisCommentCmd:
   │        · 写 wf_comment_info
   │        · taskService.addComment(act_hi_comment)
   │       （同一 ProcessEngine 事务内 → 一致）
   │  ④ taskService.complete(taskId, variables)
   │  ⑤ 抄送:copyUserIds 非空 → wf_copy 批量写
   │  ⑥ 🔶 GlobalEventListener 触发:
   │        PROCESS_COMPLETED → processStatus=completed
   │        TASK_CREATED     → 下一节点通知/待办
   ▼
✅ 流程推进 / 结束

POST /workflow/task/reject {taskId, comment}
   │  ① 写意见(type=reject)
   │  ② runtimeService.setVariable(processStatus=reject)
   │  ③ 结束流程(或按配置回到发起人)
   ▼
processStatus=reject
```

#### 3.7.4 退回(6 策略)

```
POST /workflow/task/return {taskId, targetActivityId, comment}
   │
   ▼
🔶 WfTaskServiceImpl.returnTask
   │  ① 校验:targetActivityId ∈ 历史经过节点(查 act_hi_actinst)
   │  ② 取 BpmnModel + targetFlowNode
   │  ③ 🔶 策略工厂按「目标节点 + 当前节点 + 是否多实例」选策略:
   │       DefaultTaskNextGateway / ActiveMultiInstanceTask
   │       / CompletedMultiInstance+Task / CompletedMultiInstance+Gateway
   │       / NextCallActivity / NextSubProcess
   │  ④ managementService.executeCommand(new RollbackCmd(strategy, params))
   │  ⑤ 写意见(type=return)
   ▼
✅ 回到目标节点(processStatus=return 后由监听器修正)
```

**策略选择矩阵**:

| 当前/目标上下文 | 策略 |
|---|---|
| 普通 UserTask,目标在其之前(经 Gateway) | `DefaultTaskNextGatewayRollbackOperateStrategy` |
| 当前在多实例中(会签未完成) | `ActiveMultiInstanceTaskRollbackOperateStrategy` |
| 当前在多实例之后且下接 Task | `CompletedMultiInstanceTaskAndNextDefaultTaskRollbackOperateStrategy` |
| 当前在多实例之后且下接 Gateway | `CompletedMultiInstanceTaskAndNextDefaultGateWayTaskRollbackOperateStrategy` |
| 目标在 CallActivity 之后 | `NextCallActivityRollbackOperateStrategy` |
| 目标在 SubProcess 内部/之后 | `NextSubProcessRollbackOperateStrategy` |

#### 3.7.5 加签(前 / 后,动态注入)

```
【前加签】POST /workflow/task/beforeAddSign {taskId, userIds, comment}
   │
   ▼
🔶 InjectUserTaskInProcessInstanceCmd
   │  ① 取当前 task 的 ExecutionEntity
   │  ② 🔶 DynamicUserTaskBuilder 构建新 UserTask:
   │        name = "原名称·前加签", id = dynamic_<uuid>
   │        assignee = userIds[0],继承 IdmPermission / IsSkip 等
   │  ③ 🔶 AbstractDynamicInjectionCmd.injectBefore():
   │        改 sequenceFlow:currentNode.incoming → newNode → currentNode
   │        激活 newNode
   │  ④ AddHisCommentCmd(type=addSignBefore)
   ▼
✅ 新节点出现在当前节点之前

【后加签】逻辑对称(injectAfter),原 task 完成后进入新节点
```

#### 3.7.6 边界事件(8 型)

```
【配置】设计器挂边界事件 → 写 flowable:boundary* 扩展属性
   │
   ▼
【引擎触发】(timer 到期 / message 到达 / signal 广播 / error / escalation
             / cancel / compensation / conditional)
   │
   ├─ 引擎自动完成:创建边界事件执行、走边界分支
   ▼
【外部触发】POST /workflow/boundary/**  (4 个接口)
   │
   ▼
🔶 WfBoundaryEventServiceImpl
   │  ① 校验 type ∈ 8 型
   │  ② 定位 activityId 对应执行
   │  ③ 记录触发(可发通知)
   │  ④ 设变量 processStatus = boundary_<type>
   ▼
✅ 边界分支执行
```

#### 3.7.7 流程迁移

```
POST /workflow/process/migrate {sourceProcDefId, targetProcDefId, activityMapping}
   │
   ▼
🔶 WfProcessMigrateServiceImpl
   │  ① 校验目标定义存在(且是最新/指定版本)
   │  ② 🔶 WfProcessMigratePlan 构建:
   │        · 自动镜像同名节点
   │        · 手动指定差异节点(activityMapping)
   │        · 校验覆盖所有运行中活动节点(漏 → 报错)
   │  ③ 对每个运行中实例执行迁移
   │  ④ 🔶 全过程在同一事务;任一失败 → WfProcessMigrateException + 全量回滚
   ▼
✅ 返回 WfProcessMigrateResult(成功/失败明细)
```

#### 3.7.8 流程清盘(46 表)

```
POST /workflow/process/clearAll {confirmPhrase}
   │
   ▼
🔶 WfProcessClearServiceImpl
   │  ① 校验权限 workflow:process:clearAll
   │  ② 校验确认短语(前端两步确认)
   │  ③ 按依赖倒序清空 46 张流程表:
   │        act_hi_*(10) → act_ru_*(13) → act_re_* / act_ge_* / flw_* / wf_* / tbl_*
   │  ④ 平台表(sys_* / qrtz_* / gen_*)**不清**
   ▼
✅ 流程数据归零,平台可继续使用
```

---

## 4. 接口清单(⬆️ 平台部分 + 🔶 流程部分)

### 4.1 🔶 流程引擎接口(13 组,摘要)

| 组 | 前缀 | 代表接口 |
|---|---|---|
| 流程分类 | `/workflow/category` | list / tree / add / edit / remove |
| 流程模型 | `/workflow/model` | list / get / save(XML)/ deploy / activate / remove |
| 部署 | `/workflow/deploy` | list / remove / suspend / activate |
| 表单 | `/workflow/form` | list / get / add / edit / remove / byCode |
| 表单分类 | `/workflow/formCategory` | list / tree / CRUD |
| 表单模板 | `/workflow/formTemplate` | list / CRUD |
| 表达式 | `/workflow/expression` | list / CRUD |
| 流程实例 | `/workflow/process` | start / myList / todoList / doneList / copyList / readCopy / **flowViewer** / delete / abolishProcess / migrate / clearAll |
| 任务 | `/workflow/task` | list / **complete** / **reject** / **return** / **returnList** / **transfer** / **delegate** / **beforeAddSign** / **afterAddSign** / **stop** / **revoke** / skip / changeAssignee / claim / unclaim / addComment |
| 节点表单 | `/workflow/taskForm` | save / query |
| 边界事件 | `/workflow/boundary` | query / **4 个 trigger 接口** |
| 迁移 | `/workflow/process/migrate` | preview / execute |
| 清盘 | `/workflow/process/clearAll` | execute |

### 4.2 ⬆️ 平台接口(与 `springboot4` 完全一致)

`/login` `/logout` `/captchaImage` `/getInfo` `/getRouters` `/system/*` `/monitor/*` `/tool/gen/*` `/common/*` `/swagger-ui/**` `/v3/api-docs` —— 路径/入参/出参**零变更**。

### 4.3 内部 SPI(同 §2.3)

---

## 5. 关键技术决策(HD-)

> **HD-01~HD-28 继承自 `springboot4`**(STATELESS JWT / AsyncManager / Jackson 3 / Security 7 / starter 4 / Quartz / Redis 白名单 等),详见 `docs/springboot4-概要设计.md` §6。下表为本分支**新增 HD-29~HD-42**。

| # | 决策 | 理由 | 影响 |
|---|---|---|---|
| **HD-29** | **Flowable 8.0.0(非 6.8)** | 6.8 只支持 SB 2 / `javax.*`;8.0 支持 Jakarta / SB 3+ | 模型格式、`act_de_*`、配置键全变 |
| **HD-30** | **独立 `extend-flowable` 模块 + 独立 schema** | 平台与流程解耦;清盘/迁移风险隔离 | 8 模块;schema `basicdb_flowable8` |
| **HD-31** | **模型存 BPMN XML** | Flowable 7 起删除 `flowable-json-converter`;8.0 只能存 XML | 旧 JSON 模型不可直接导入 |
| **HD-32** | **无 `act_de_*` 表** | Flowable 8 不含 designER 建模器 | 模型只落 `act_re_model` + `act_ge_bytearray` |
| **HD-33** | **关闭 IDM,复用 `sys_*`** | 避免双用户体系 | `idm.enabled: false` + 平台组织 |
| **HD-34** | **`history-level: audit`** | 需审批意见 / 流程图 / 迁移 | `act_hi_*` 数据量增大(R40) |
| **HD-35** | **退回用策略模式(6 种)** | 多实例 / CallActivity / SubProcess 的 `moveActivityIdTo` 行为不符 | 工厂 + `RollbackCmd` |
| **HD-36** | **审批意见双写** | 业务查询快(不 join 引擎表)+ 流程图有意见 | `act_hi_comment` + `wf_comment_info` |
| **HD-37** | **部署时表单快照** | 改表单不应影响在跑实例 | 必须重新部署才对新实例生效(R39) |
| **HD-38** | **流程图后端生成 PNG** | 前端渲染需完整模型+状态,payload 大 | 依赖 Activiti BpmnLayout 排版(R43) |
| **HD-39** | **加签用动态注入 Command** | BPMN 静态定义无法在运行时插节点 | `DynamicUserTaskBuilder` + `injectBefore/After` |
| **HD-40** | **迁移用 Flowable `MigrationPlan` + 事务** | 失败必须全量回滚,不能半迁移 | `WfProcessMigratePlan/Result/Exception` |
| **HD-41** | **清盘按依赖倒序清 46 表** | 直接 `TRUNCATE` 会因外键顺序失败 | 两步确认 + 确认短语 |
| **HD-42** | **`allow-circular-references: true`** | `Wf*Service` 相互依赖(如 Task ↔ Process) | 已知取舍;可重构解耦(R42) |

---

## 6. 部署视图(⬆️ 与 `springboot4` 一致;🔶 schema 变)

```
        ┌────────────────────────────┐
        │  Nginx(可选)              │
        │  /api/*  → 8080           │
        │  /        → web-ui dist   │
        └─────────────┬──────────────┘
                      │
                      ▼
        ┌────────────────────────────────────────┐
        │  basic-admin-api jar                   │
        │  ⬆️ 运行时 JDK 21 / Jackson 3           │
        │  🔶 + extend-flowable(Flowable 8.0.0)  │
        │  🔶 异步执行器 / Job 队列(内存)        │
        │  ⬆️ Quartz RAMJobStore                  │
        │  端口:8080 / profile: dev|prod          │
        └──────┬──────────────────┬──────────────┘
               │                  │
               ▼                  ▼
   ┌──────────────────────────┐  ┌──────────────────────┐
   │ PostgreSQL ≥15.1          │  │ Redis(可选)          │
   │ 🔶 basicdb_flowable8      │  │ ⬆️ Jackson 3 序列化   │
   │ 🔶 90 表 + 2 视图         │  │                      │
   │  act_*39 flw_*7 wf_*9     │  │                      │
   │  tbl_*1 sys_*21 qrtz_*11  │  │                      │
   │  gen_*2                   │  │                      │
   └──────────────────────────┘  └──────────────────────┘

        web-mp:  ⬆️ 与 springboot4 完全一致(无工作流)
        ┌────────────────────────────┐
        │  HBuilder / 微信开发者工具  │
        └────────────────────────────┘
```

### 6.1 启动顺序

1. **JDK 21** + IDE 设为 21
2. PostgreSQL 启动 + `psql -f file-sql/basicdb-vue3.sql`(**90 表 + 2 视图**,schema `basicdb_flowable8`)
3. (可选)清空 Redis(⬆️ Jackson 2 脏数据,若从 SB3 升上来)
4. Redis 启动
5. `mvn clean compile`(8 模块)→ 0 ERROR
6. `mvn spring-boot:run -pl basic-admin-api` → **检查 Flowable 引擎初始化日志**
7. 校验 `information_schema`:`basicdb_flowable8` 有 90 表 / 2 视图 / 无 `act_de_*`
8. `/swagger-ui/index.html` 可见 `Wf*` 分组
9. `pnpm dev` 启动前端 → 流程菜单可见(1500~1699)
10. `admin / admin123` 登录 → 建模型 → 部署 → 发起 → 审批全链路

### 6.2 并发与恢复

| 资源 | 配置 | 备注 |
|---|---|---|
| Tomcat | `max-threads=800` | ⬆️ |
| Druid | `min-idle=5` / `max-active=20` | ⬆️ |
| Quartz | RAMJobStore | ⬆️ 重启丢任务运行态 |
| **Flowable 异步执行器** | `async-executor-activate` | **🔶 内存 Job 队列** |
| **Flowable 定时器** | `act_ru_timer_job`(DB) | 🔶 定时器**落库**,重启可恢复 |
| Redis | TTL 30min | ⬆️ |

> ⚠️ **重启恢复差异**:Flowable 的**定时器 Job 落库**(`act_ru_timer_job`),重启可恢复;但 **Quartz 的 RAMJobStore 任务不恢复**。两者独立。

### 6.3 回滚策略

| 场景 | 回滚动作 | 数据影响 |
|---|---|---|
| Flowable 集成出问题 | 回退到 `springboot4` 的 jar | **需注意**:schema 从 `basicdb_flowable8` 回到 `basicdb`(平台数据在旧 schema 中) |
| 仅流程功能异常 | 停用流程菜单 / 权限串 | 平台可用 |
| 流程数据脏 | 流程清盘(46 表) | 流程数据归零,平台保留 |

---

## 7. 风险与待定项

> **P1~P30 继承自 `springboot4`**(Jackson 2/3 / 循环依赖 / Security 7 / RAMJobStore / Redis 脏数据 等),详见 `docs/springboot4-概要设计.md` §8。下表为**本分支新增**。

| 编号 | 项 | 影响 | 归属文档 |
|---|---|---|---|
| **P31** | **schema 配错 → Flowable 在默认 schema 建表** | **高** | 架构 R35;AC-45 |
| **P32** | **配置键静默忽略**(`idm.enabled` 写错) | **中** | 架构 R36 |
| **P33** | **旧「模型 JSON」不可导入** | **中** | 架构 R37 |
| **P34** | **`act_id_*` 仍可能被建** | 低 | 架构 R38;AC-47 |
| **P35** | **改 `wf_form` 不重部署 → 旧快照** | **中** | 架构 R39;HD-37;AC-59 |
| **P36** | **`act_hi_*` 膨胀** | **中** | 架构 R40;A-24 |
| **P37** | **多实例下 Flowable 定时器/异步重复触发** | **中** | 架构 R41;§6.2 |
| **P38** | **`allow-circular-references` 掩盖设计问题** | 低 | 架构 R42;HD-42 |
| **P39** | **流程图依赖 Activiti 6 BpmnLayout** | 中 | 架构 R43;HD-38 |
| **P40** | **bpmn-js 17 + properties-panel 5.28 自定义属性维护** | 中 | 架构 R44;A-31 |
| **P41** | **vform3 JSON 结构演进** | 中 | 架构 R45 |
| **P42** | **清盘误操作** | **中** | 架构 R46;HD-41;A-25 |
| **P43** | **迁移节点不匹配** | **中** | 架构 R47;AC-57 |
| **P44** | **`wf_task_form` 存业务数据的脚手架用法** | **中** | A-28 |
| **P45** | **移动端无工作流** | 中 | A-23 |
| **P46** | **`extend-flowable` 223 文件未走代码生成模板** | 低 | 架构 R48 |

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `springboot4` 概要设计(v0.1)为基线(HD-01~28 / P1~30 继承),叠加 Flowable 8.0.0 集成专题:§2.5 extend-flowable 模块结构(两层结构 / 13 Controller↔Service 映射 / 层边界 / 前端结构)、§3.7 流程引擎八大流程(建模部署 / 发起 / 审批 / 退回 6 策略 / 加签 / 边界事件 / 迁移 / 清盘)、§4.1 流程接口 13 组、HD-29~HD-42、§6.2 重启恢复差异、§6.3 回滚策略、P31~P46 |
