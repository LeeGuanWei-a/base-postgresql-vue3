# 基础平台(base-postgresql-vue3) 需求文档(PRD) — `springboot4-extend-flowable` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot4-extend-flowable`** 分支(全栈 + 流程引擎)
> 前置依赖:《架构方案》v0.1
> 本分支 = **`springboot4` 基线(JDK 21 / SB 4.1.1 / Jackson 3 / Security 7)+ `extend-flowable` 集成 Flowable 8.0.0**。
> **平台基础需求、SB3/SB4 升级需求与 `springboot4` 一致**(本文档不重复);**本分支新增的只有「流程引擎」这一大块需求**。
> 使用手册见 **`docs/流程引擎使用说明.md`**;本文档是**需求与验收**视角。

---

## 1. 文档信息与版本

- 当前版本:**v0.1** — 初稿,基于 README §十二 + 代码扫描抽取
- 变更历史

| 版本 | 日期 | 变更 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `springboot4` PRD 为基线,新增「§6.11 流程引擎需求」(13 小节:13 Controller / 18 类审批动作 / 8 类边界事件 / 6 种退回 / 抄送 / 迁移 / 清盘 / 表单 / 表达式 / 流程图 / 权限串)+ 场景 S16~S24 + AC-43~AC-66 + 待定项 A-23~A-32 |

---

## 2. 背景与目标

### 2.1 为什么做

`springboot4` 已交付「JDK 21 + SB 4.1.1 + Jackson 3 + Security 7」的 RBAC 基础平台,但**没有流程能力** —— 而业务侧(请假、报销、用印、采购等)普遍需要审批流。历史上有两个选择:

- `springboot2-extend-flowable`(**Flowable 6.8.0 + SB 2.5.14**):可用但**技术栈老旧**(SB2 + JDK 11 + `javax.*`),与公司 JDK 21 基线脱节
- 自研审批流:**重复造轮子**,且 BPMN 标准能力(网关/多实例/边界事件/子流程)自研成本极高

因此本分支把 **Flowable 8.0.0**(支持 Jakarta / SB 3+ 的唯一大版本线)集成到 `springboot4`,并配套**自研定制层 + 前端设计器**,形成可开箱使用的流程平台。

### 2.2 目标清单

| 目标 | 成功标准 |
|---|---|
| G1~G6(继承 `master`) | RBAC / 定时任务 / 代码生成 / PC+移动 / 接口兼容 / PostgreSQL |
| G7~G12(继承 `springboot3`) | JDK 17 + SB 3.5.16 / Jakarta / springdoc |
| G13~G19(继承 `springboot4`) | JDK 21 + SB 4.1.1 / **Jackson 3** / Security 7 / starter 4 / Quartz starter / 契约零变更 |
| **G20(本分支)集成 Flowable 8.0.0** | `extend-flowable` 模块可启动;`act_*` 39 表在 `basicdb_flowable8` 正常创建/使用 |
| **G21(本分支)建模即可部署** | bpmn-js 设计器保存 BPMN XML → 合规校验 → 一键部署;`act_re_procdef` 有版本记录 |
| **G22(本分支)发起与审批闭环** | 发起 → 待办 → 审批(同意/驳回/退回/转办/委派/加签/终止/撤回/跳过/变更处理人)→ 结束,全链路可跑通 |
| **G23(本分支)退回支持 6 种场景** | 普通节点 / 进行中多实例 / 已完成多实例+Task / 已完成多实例+Gateway / CallActivity / SubProcess |
| **G24(本分支)边界事件 8 种可用** | timer / message / signal / error / escalation / cancel / compensation / conditional;外部可触发 |
| **G25(本分支)抄送可用** | 随审批动作提交;「抄送我的」列表 + 标记已读 |
| **G26(本分支)流程迁移可用** | 运行中实例迁到新流程定义;自动镜像或手动指定节点;失败回滚 |
| **G27(本分支)流程清盘可用** | 一键清空流程数据(46 张表);两步确认 + 确认短语;权限串管控 |
| **G28(本分支)自定义表单可用** | vform3 设计表单 → 存 `wf_form` → 部署时快照 `wf_deploy_form` → 审批时渲染/填写 |
| **G29(本分支)流程图可视化** | 已办(绿)/ 待办(蓝)/ 驳回(红)/ 跳过(灰)四色高亮 |
| **G30(本分支)权限与菜单齐备** | `workflow:*` 权限串覆盖 13 Controller;1500~1699 菜单已导入 |

### 2.3 非目标(本分支不做)

- 前端 TypeScript 化(走 `springboot4_TypeScript`)
- 行为验证码(走 tianai 系列)
- 移动端工作流页面(仅基础平台页面;M4 候选)
- 多租户 / SSO 联邦 / 微服务拆分
- **Flowable 外部任务(External Worker)**
- **Flowable 多租户**
- **Flowable 表单引擎替代 vform3**
- **流程引擎表归档策略**(M6 候选)
- **Quartz JDBC JobStore**(仍 RAMJobStore,继承 `springboot4`)
- **流程设计器 UI 大改**(沿用 bpmn-js 17 + 自定义属性面板)

---

## 3. 术语表

| 术语 | 含义 |
|---|---|
| **Flowable** | 开源 BPMN 流程引擎;本分支用 **8.0.0**(支持 Jakarta / SB 3+) |
| **BPMN 2.0 XML** | 流程定义标准格式;**本分支模型存储格式**(Flowable 7 起弃用设计器 JSON) |
| **`act_*`** | Flowable 引擎表(39 张):`act_re_*` 模型部署 / `act_ru_*` 运行时 / `act_hi_*` 历史 / `act_ge_*` 通用 |
| **`flw_*`** | Flowable 事件注册表(7 张)+ **`flw_expression`**(本项目的表达式字典) |
| **`wf_*`** | 本项目工作流业务表(9 张):分类/表单/部署快照/节点填写/意见/抄送等 |
| **`tbl_flow_extend_procinst`** | 流程实例业务扩展表(发起人 / 状态 / 业务键) |
| **`basicdb_flowable8`** | 本分支的 PostgreSQL schema(**与 JDBC `currentSchema` 必须一致**) |
| **`act_de_*`** | 旧 designER 建模器表;**Flowable 8 无此系列** |
| **部署快照(`wf_deploy_form`)** | 部署时拷贝的表单内容;审批读快照而非 `wf_form` |
| **`processStatus`** | 流程状态变量:`running` / `reject` / `return` / `completed` / `canceled` / `delete` / `boundary_<type>` |
| **`IdmAssignee` / `IdmPermission`** | 节点扩展属性:指定审批人 / 节点授权功能 |
| **边界事件** | BPMN 附着在活动上的事件;本分支支持 8 种 |
| **多实例(multiInstance)** | 会签/或签;BPMN 的 `multiInstanceLoopCharacteristics` |
| **CallActivity / SubProcess** | 调用外部流程 / 内嵌子流程;退回时策略不同 |
| **流程迁移(Migration)** | 把运行中实例从旧流程定义迁到新定义 |
| **流程清盘(Clear)** | 一键清空流程数据(46 张表) |
| **vform3** | 本项目使用的表单设计器(`vform3-builds 3.0.10`),产出 JSON |
| **bpmn-js** | 前端 BPMN 设计器(`17.11.1`)+ 属性面板(`bpmn-js-properties-panel 5.28`) |

---

## 4. 用户角色与使用场景

### 4.1 角色

| 角色 | 关键动作 | 频率 |
|---|---|---|
| **超级管理员(`admin`)** | 全部功能(含流程清盘 / 废除) | 高 |
| **流程设计者(业务侧)** | 建流程模型 / 配表单 / 配审批人 / 部署 | 中 |
| **发起人** | 发起流程 / 撤回 / 查看进度 / 抄送 | 高 |
| **审批人** | 同意 / 驳回 / 退回 / 转办 / 委派 / 加签 / 终止 | **高** |
| **抄送接收人** | 查看抄送 / 标记已读 | 中 |
| **业务管理员** | 受数据权限约束的 CRUD | 中 |
| **审计员** | 查看操作/登录日志 | 低 |
| **开发者** | 代码生成 / 维护引擎集成 | 低 |

### 4.2 关键场景走查

> **S1~S15** 与 `master` / `springboot3` / `springboot4` 一致(登录、角色赋权、代码生成、移动端登录、锁屏、定时任务、SB3/SB4 升级回归、Swagger、starter 选型、生成器修正、Jackson 迁移、Security 7 等),详见 `docs/springboot4-需求文档.md` §4.2。下表为**本分支新增的流程场景**。

#### 🔶 场景 S16:流程设计者建模型并部署

1. 「流程管理 → 流程模型」→ 新增,填 modelKey / 名称 / 分类
2. 打开 **bpmn-js 设计器**,拖拽 开始事件 → 用户任务 → 网关 → 结束事件
3. 选中用户任务,在属性面板配置:
   - `IdmAssignee`(指定审批人 / 角色 / 部门)
   - `IdmPermission`(节点授权功能:同意/驳回/退回/转办/委派/抄送/加签/终止)
   - 表单绑定(`wf_form` 的 formCode)
4. 点「保存」→ 前端提交 **BPMN XML** → 后端 `WfModelSaveXmlService` 校验 + 写 `act_re_model` + `act_ge_bytearray`
5. 点「部署」→ `RepositoryService.deploy()` → `act_re_deployment` / `act_re_procdef`
6. **系统自动拷贝 `wf_deploy_form` 快照**(必须,否则审批读到空表单)
7. 流程可被发起(新版本号 +1)

> ⚠️ **Flowable 8 关键差异**:保存的是 **BPMN XML**,不是 6.8 分支的设计器 JSON。

#### 🔶 场景 S17:发起流程

1. 「我的发起 → 发起流程」→ 选分类 → 选流程定义
2. 渲染**首节点表单**(vform3)→ 填写
3. 提交 → `POST /workflow/process/start`
4. 后端:写 `tbl_flow_extend_procinst`(发起人 / `running`)→ `startProcessInstanceByKey` → 表单存 `wf_task_form`
5. 进入下一节点审批人待办

#### 🔶 场景 S18:审批人审批(同意 / 驳回)

**同意**
1. 「我的待办」→ 打开任务 → 看到表单 + 审批意见框
2. 「同意」→ `POST /workflow/task/complete`
3. 后端:校验 assignee → **双写意见**(`act_hi_comment` + `wf_comment_info`)→ `taskService.complete` → 抄送(若有)→ 事件监听更新 `processStatus`

**驳回**
1. 「驳回」→ 填意见 → 提交
2. 后端:`processStatus` 置 `reject`;流程终止(需发起人重新发起或由管理员处理)

#### 🔶 场景 S19:退回(6 种场景之一)

1. 审批人点「退回」→ 弹窗列出**历史经过节点**(仅可退回已走过的节点)
2. 选目标节点 + 填意见 → 提交
3. 后端按目标节点类型**自动选策略**:
   - 普通 UserTask + Gateway → `DefaultTaskNextGatewayRollbackOperateStrategy`
   - 进行中多实例 → `ActiveMultiInstanceTaskRollbackOperateStrategy`
   - 已完成多实例 + 后接 Task → `CompletedMultiInstanceTaskAndNextDefaultTaskRollbackOperateStrategy`
   - 已完成多实例 + 后接 Gateway → `CompletedMultiInstanceTaskAndNextDefaultGateWayTaskRollbackOperateStrategy`
   - CallActivity → `NextCallActivityRollbackOperateStrategy`
   - SubProcess → `NextSubProcessRollbackOperateStrategy`
4. 执行 `RollbackCmd` → 回到目标节点;`processStatus` 置 `return`

#### 🔶 场景 S20:转办 / 委派 / 加签 / 变更处理人

| 动作 | 效果 |
|---|---|
| **转办** | 任务 assignee 改为他人(原审批人不再持有) |
| **委派** | 任务委派给他人办理,办理完回到原审批人(需再确认) |
| **前加签** | 在当前节点**之前**插入新审批节点(动态注入) |
| **后加签** | 在当前节点**之后**插入新审批节点 |
| **变更处理人** | 直接把当前任务 assignee 改人(不改流程结构) |

#### 🔶 场景 S21:边界事件触发

1. 设计时在用户任务上挂边界事件(如 timer 30 分钟 / message / signal / error / escalation / cancel / compensation / conditional)
2. 引擎内部触发(timer 到期 / message 到达 / signal 广播)
3. 或调用**外部触发接口**(`POST /workflow/boundary/**`,4 个)
4. 触发后 `processStatus` 置 `boundary_<type>`,流程走边界分支

#### 🔶 场景 S22:流程迁移(运行中实例换新定义)

1. 流程定义改版并部署(新版本)
2. 「流程管理 → 流程迁移」→ 选源定义 + 目标定义
3. 选择节点映射(**自动镜像同名节点** 或 **手动指定**)
4. 提交 → 后端创建 `MigrationPlan` → 对每个运行中实例执行
5. **事务内失败回滚**(`WfProcessMigrateException`)

#### 🔶 场景 S23:流程清盘(高危操作)

1. 「流程管理 → 流程清盘」→ 弹窗提示「将清空 46 张流程表」
2. **第一步确认** + **第二步输入确认短语**
3. 提交 → 后端清空流程数据(保留平台 `sys_*`)
4. 权限串 `workflow:process:clearAll` 管控(仅管理员)

#### 🔶 场景 S24:抄送与流程图

**抄送**
1. 审批人提交时勾选抄送人 → 后端写 `wf_copy`
2. 被抄送人在「抄送我的」看到 → 点开标记已读(`readFlag=1`)

**流程图**
1. 打开流程详情 → 调 `GET /workflow/process/flowViewer/{procInsId}`
2. 后端生成 PNG:**已办(绿)/ 待办(蓝)/ 驳回(红)/ 跳过(灰)**
3. 前端展示(可选叠加审批意见时间线)

---

## 5. 总体需求

### 5.1 范围图(🔶 = 本分支新增;⬆️ = `springboot4` 继承)

```
                    ┌──────────────────────────────────────────┐
                    │   平台能力(本仓库)                      │
                    │  ┌────────────────┐  ┌────────────────┐  │
                    │  │ PC Web         │  │ 移动 H5/小程序  │  │
                    │  │ ⬆️ 基础页面     │  │ ⬆️(未改)        │  │
                    │  │ 🔶 工作流 21 vue│  │                │  │
                    │  │ 🔶 bpmn-js 设计器│  │                │  │
                    │  └───────┬────────┘  └───────┬────────┘  │
                    │          │  HTTP/JSON         │           │
                    │  ┌───────┴───────────────────┴────────┐  │
                    │  │   Spring Boot 4.1.1(⬆️ JDK 21)     │  │
                    │  │   🔶 Flowable 8.0.0               │  │
                    │  │      + extend-flowable(223 java) │  │
                    │  │      + 13 Controller              │  │
                    │  │      + 19 ServiceImpl             │  │
                    │  └───────┬───────────────────────────┘  │
                    │          │  MyBatis-Plus 3.5.17          │
                    │  ┌───────┴────────────────────────────┐  │
                    │  │  PostgreSQL                        │  │
                    │  │  🔶 basicdb_flowable8              │  │
                    │  │  🔶 90 表 + 2 视图                 │  │
                    │  └────────────────────────────────────┘  │
                    └──────────────────────────────────────────┘
```

### 5.2 核心规则总表(**流程引擎部分**)

> 平台部分(用户/角色/菜单/部门/字典/通知/定时任务/代码生成)与 `springboot4` 一致,见 `docs/springboot4-需求文档.md` §5.2。

| 模块 | 关键字段 | L1 校验 | L2 校验 |
|---|---|---|---|
| **流程模型** | `modelKey` / `name` / `category` | 必填;`modelKey` 唯一 | BPMN XML **合规校验**通过;所属分类存在 |
| **流程部署** | `deploymentId` / `procDefKey` | — | 模型必须已保存且校验通过;部署后**必须**有 `wf_deploy_form` 快照 |
| **表单** | `formCode` / `content` | 必填;`formCode` 唯一 | vform3 JSON 可解析;被引用时不可删 |
| **表单分类/模板** | `code` / `name` | 必填;`code` 唯一 | 分类树无环 |
| **流程分类** | `code` / `parentId` | 必填;`code` 唯一 | 树无环;`parentId≠自己` |
| **表达式** | `key` / `value` | 必填;`key` 唯一 | **被引用时不可删** |
| **发起** | `procDefKey` / 表单数据 | 流程定义必须**已部署且激活** | 首节点表单字段校验 |
| **审批** | `taskId` / `comment` | 操作人必须是 **assignee 或 candidate** | `IdmPermission` 必须包含该动作 |
| **退回** | `targetActivityId` | 必填 | **必须是历史经过的节点**(`/returnList` 只返历史) |
| **加签** | `userIds` | 必填、非空 | 用户必须存在 |
| **抄送** | `userIds` | 可空 | 用户存在性 |
| **迁移** | `sourceProcDefId` / `targetProcDefId` | 必填 | 目标定义必须存在;节点映射覆盖所有运行中活动节点 |
| **清盘** | 确认短语 | 必填且匹配 | **两步确认**;仅 `workflow:process:clearAll` 权限 |
| **边界事件** | `activityId` / `type` | `type` ∈ 8 种 | `activityId` 必须存在且挂有该边界事件 |

---

## 6. 功能需求

> **平台功能需求**(文件上传、用户/角色/菜单/部门/字典/通知/定时任务/代码生成、L1/L2、审计、数据权限)与 `master` **完全一致**,见 `docs/base-postgresql-vue3-需求文档.md` §6 / §8。
> **SB3 / SB4 升级需求**见 `docs/springboot3-需求文档.md` §6.9 / `docs/springboot4-需求文档.md` §6.10。
> 本节只展开 **§6.11 流程引擎需求**。

### 6.11 🔶 流程引擎需求(本分支核心)

#### 6.11.1 模块与规模

| 项 | 数值 | 说明 |
|---|---|---|
| Maven 子模块 | **8 个**(7 + `extend-flowable`) | — |
| `extend-flowable` Java 文件 | **223** | — |
| Controller | **13** | 见 6.11.2 |
| ServiceImpl | **19** | 模型/部署/流程/任务/表单/分类/表达式/边界/迁移/清盘等 |
| 包根 | `com.basic.flowable`(引擎层)+ `com.basic.workflow`(业务层) | — |
| 前端页面 | **21 个 `.vue`**(`views/basic-extend/workflow/**`) | — |
| 前端设计器 | `plugins/package/**`(bpmn-js **17.11.1** + properties-panel **5.28**) | — |
| 表单引擎 | `vform3-builds` **3.0.10** | — |
| 菜单权限 | **1500~1699** 段 | — |

#### 6.11.2 13 个 Controller 与能力矩阵

| # | Controller | 能力 | 主要权限串 |
|---|---|---|---|
| 1 | `WfCategoryController` | 流程分类(树形 CRUD) | `workflow:category:*` |
| 2 | `WfModelController` | 流程模型(CRUD / 保存 XML / 部署 / 激活 / 删除) | `workflow:model:*` |
| 3 | `WfDeployController` | 部署管理(列表 / 删除 / 挂起 / 激活) | `workflow:deploy:*` |
| 4 | `WfFormController` | 表单(CRUD / 按 formCode 查) | `workflow:form:*` |
| 5 | `WfFormCategoryController` | 表单分类(树) | `workflow:formCategory:*` |
| 6 | `WfFormTemplateController` | 表单模板 | `workflow:formTemplate:*` |
| 7 | `WfExpressionController` | 表达式字典(CRUD + 引用校验) | `workflow:expression:*` |
| 8 | `WfProcessController` | 流程实例(发起 / 我的发起 / 待办 / 已办 / 抄送 / 流程图 / 删除 / 废除) | `workflow:process:*` |
| 9 | `WfTaskController` | 任务(**18 类审批动作**) | `workflow:task:*` |
| 10 | `WfTaskFormController` | 节点表单(保存 / 查询节点填写) | `workflow:taskForm:*` |
| 11 | `WfBoundaryEventController` | 边界事件(查询 / 4 个外部触发) | `workflow:boundary:*` |
| 12 | `WfProcessMigrateController` | 流程迁移(预览 / 执行) | `workflow:process:migrate` |
| 13 | `WfProcessClearController` | 流程清盘(46 表) | `workflow:process:clearAll` |

#### 6.11.3 18 类审批动作(`WfTaskController`)

| # | 动作 | 说明 |
|---|---|---|
| 1 | **同意**(complete) | 推进到下一节点 |
| 2 | **驳回**(reject) | 终止流程(状态 `reject`) |
| 3 | **退回**(return) | 回到历史节点(6 种策略) |
| 4 | **转办**(transfer) | 改 assignee,原审批人不再持有 |
| 5 | **委派**(delegate) | 委派他人办理,完成后回到原审批人 |
| 6 | **前加签**(addSignBefore) | 当前节点之前插入 |
| 7 | **后加签**(addSignAfter) | 当前节点之后插入 |
| 8 | **会签回退**(mulitiSequentialUnComplete) | 多实例场景回退会签 |
| 9 | **终止**(stop) | 强制结束流程 |
| 10 | **撤回**(revoke) | 发起人撤回 |
| 11 | **跳过节点**(skip) | 按 `IsSkip` 配置跳过 |
| 12 | **变更处理人**(changeAssignee) | 直接改 assignee |
| 13 | **抄送**(copy) | 提交时附带抄送人 |
| 14 | **废除**(abolishProcess) | 管理员强制作废 |
| 15 | **删除流程**(deleteProcessByIds) | 物理删除历史+业务数据 |
| 16 | **认领**(claim) | candidate 签收 |
| 17 | **取消认领**(unclaim) | 退回候选池 |
| 18 | **加意见**(addComment) | 仅记录意见不推进 |

#### 6.11.4 8 类边界事件

| # | 类型 | 触发方式 | 说明 |
|---|---|---|---|
| 1 | `timer` | 引擎定时器 | 到期触发(可配 duration / date / cycle) |
| 2 | `message` | 消息到达 | 需 `messageRef` |
| 3 | `signal` | 信号广播 | 全局广播 |
| 4 | `error` | 错误捕获 | 需 `errorRef` |
| 5 | `escalation` | 升级 | 需 `escalationRef` |
| 6 | `cancel` | 取消 | 仅事务子流程 |
| 7 | `compensation` | 补偿 | 需补偿处理器 |
| 8 | `conditional` | 条件 | 变量满足条件即触发 |

**外部触发接口(4 个)**:由 `WfBoundaryEventController` 暴露,用于业务侧主动触发(如「升级」「外部信号」)。

#### 6.11.5 6 种退回策略

| # | 策略类 | 适用场景 |
|---|---|---|
| 1 | `DefaultTaskNextGatewayRollbackOperateStrategy` | 普通 UserTask + Gateway |
| 2 | `ActiveMultiInstanceTaskRollbackOperateStrategy` | 进行中的多实例(会签未完成) |
| 3 | `CompletedMultiInstanceTaskAndNextDefaultTaskRollbackOperateStrategy` | 多实例已完成 + 后接 Task |
| 4 | `CompletedMultiInstanceTaskAndNextDefaultGateWayTaskRollbackOperateStrategy` | 多实例已完成 + 后接 Gateway |
| 5 | `NextCallActivityRollbackOperateStrategy` | 目标在 CallActivity 之后 |
| 6 | `NextSubProcessRollbackOperateStrategy` | 目标在 SubProcess 内部/之后 |

> **策略选择由工厂自动完成**,前端只需传目标节点 id。

#### 6.11.6 抄送需求

| 项 | 规则 |
|---|---|
| 提交时机 | 随审批动作(同意/驳回等)一并提交 `copyUserIds` |
| 存储 | `wf_copy`(procInsId / taskId / userId / title / readFlag) |
| 列表 | 「抄送我的」按当前用户查 |
| 已读 | 点开置 `readFlag='1'` |
| 通知 | **本分支不实现站内信/推送**(仅列表可见) |

#### 6.11.7 流程迁移需求

| 项 | 规则 |
|---|---|
| 前提 | 源定义有运行中实例;目标定义已部署 |
| 节点映射 | **自动镜像**(同名节点直接映射)或**手动指定** |
| 事务 | 整批迁移在同一事务;**任一失败全量回滚** |
| 异常 | `WfProcessMigrateException`(含失败节点/实例信息) |
| 结果 | `WfProcessMigrateResult`(成功数 / 失败数 / 明细) |

#### 6.11.8 流程清盘需求(高危)

| 项 | 规则 |
|---|---|
| 范围 | **46 张流程相关表**(`act_*` / `flw_*` / `wf_*` / `tbl_flow_extend_procinst`) |
| 保留 | 平台表(`sys_*` / `qrtz_*` / `gen_*`)不动 |
| 确认 | **两步确认** + 输入确认短语 |
| 权限 | `workflow:process:clearAll`(仅管理员) |
| 用途 | 测试/演示环境重置;**生产慎用** |

#### 6.11.9 表单与表达式

| 项 | 规则 |
|---|---|
| 表单设计 | vform3 产出 JSON → 存 `wf_form.content` |
| 表单绑定 | 节点通过 `formCode` 绑定 |
| **部署快照** | 部署时拷贝到 `wf_deploy_form`;**审批读快照** |
| 改表单 | 改 `wf_form` 后**必须重新部署**,否则在跑实例仍用旧快照 |
| 节点填写 | 审批人填写的业务数据存 `wf_task_form`(**⚠️ 脚手架用法,生产建议落业务表**) |
| 表达式 | `flw_expression` 字典;**被引用时不可删** |

#### 6.11.10 流程图需求

| 项 | 规则 |
|---|---|
| 生成方式 | 后端 PNG(`CustomProcessDiagramGenerator` + Activiti BpmnLayout 排版) |
| 配色 | **已办(绿)/ 待办(蓝)/ 驳回(红)/ 跳过(灰)** |
| 接口 | `GET /workflow/process/flowViewer/{procInsId}` |
| 缓存 | 不缓存(每次实时生成) |

#### 6.11.11 关键配置要求

| 配置 | 值 | 说明 |
|---|---|---|
| `spring.flowable.database-schema-update` | `true` | 自动建/升级表 |
| `spring.flowable.history-level` | `audit` | 记录活动 + 意见 |
| `spring.flowable.check-process-definitions` | `false` | 不在启动时扫 `processes/` 目录 |
| **`spring.flowable.database-schema`** | **`basicdb_flowable8`** | **必须与 JDBC `currentSchema` 一致** |
| `spring.flowable.idm.enabled` | `false` | 关闭内置 IDM,用平台 `sys_*` |
| `spring.flowable.form-field-validation-enabled` | `false` | 关闭 Flowable 表单字段校验 |
| `spring.main.allow-circular-references` | `true` | `Wf*Service` 相互依赖 |

#### 6.11.12 数据库要求

| 项 | 数值 |
|---|---|
| schema | **`basicdb_flowable8`**(**不是** `basicdb`) |
| 表 | **90 张**(`act_*` 39 / `flw_*` 7 / `wf_*` 9 / `tbl_*` 1 / `sys_*` 21 / `qrtz_*` 11 / `gen_*` 2) |
| 视图 | **2 张**(`list_table` / `list_column`) |
| 脚本 | `file-sql/basicdb-vue3.sql`(376.8 KB) |
| `act_de_*` | **不存在**(Flowable 8 无 designER 建模器表) |
| 函数 | 5 个(与平台一致) |

#### 6.11.13 前端需求

| 项 | 要求 |
|---|---|
| 工作流页面 | `web-ui/src/views/basic-extend/workflow/**`(**21 个 `.vue`**) |
| API 层 | `web-ui/src/api/workflow/**` |
| 设计器 | `web-ui/src/plugins/package/**`(bpmn-js 17.11.1 + properties-panel 5.28) |
| 表单设计器 | vform3-builds 3.0.10 |
| 自定义属性面板 | 必须支持 `IdmAssignee` / `IdmPermission` / 边界事件配置 |
| 语言 | **JavaScript**(非 TS) |
| 移动端 | **不做工作流页面**(仅基础平台) |

### 6.12 其它功能需求(与 `springboot4` 一致)

> 平台基础功能、SB3/SB4 升级相关要求 —— 全部与 `springboot4` 一致,不再重复。

---

## 7. 非功能需求

| 属性 | 目标 |
|---|---|
| 一致性 | 审批意见双写;迁移事务内回滚;部署快照保证在跑实例稳定 |
| 可靠性 | 引擎异常不拖垮平台;清盘有双重确认 |
| 性能 | 待办列表 1000 条 < 1s;流程图生成 < 2s;发起 < 1s |
| 审计合规 | 90 天操作日志;审批意见永久保留(除非清盘) |
| 安全 | `workflow:*` 权限串全接口覆盖;审批人校验 assignee/candidate |
| 可用性 | 单实例 99.9% |
| 部署 | 单 jar;运行时 JDK 21 |
| **🔶 schema 正确性** | Flowable 90 表必须全在 `basicdb_flowable8` |
| **🔶 历史可追溯** | `history-level: audit`,审批意见/流程图可回溯 |

---

## 8. 权限与角色

> 平台 RBAC(15 模块权限矩阵 + 数据权限)与 `springboot4` 一致,见 `docs/base-postgresql-vue3-需求文档.md` §8。
> **本分支新增 `workflow:*` 权限串**,覆盖 13 个 Controller:

| 模块 | 权限串(摘要) |
|---|---|
| 流程分类 | `workflow:category:list/add/edit/remove` |
| 流程模型 | `workflow:model:list/add/edit/remove/save/deploy/activate` |
| 部署管理 | `workflow:deploy:list/remove/suspend/activate` |
| 表单 | `workflow:form:list/add/edit/remove/query` |
| 表单分类 | `workflow:formCategory:*` |
| 表单模板 | `workflow:formTemplate:*` |
| 表达式 | `workflow:expression:list/add/edit/remove` |
| 流程实例 | `workflow:process:list/start/myList/copyList/readCopy/flowViewer/delete/abolishProcess/migrate/clearAll` |
| 任务 | `workflow:task:list/complete/reject/return/transfer/delegate/addSign/stop/...` |
| 节点表单 | `workflow:taskForm:save/query` |
| 边界事件 | `workflow:boundary:query/trigger` |

**菜单**:1500~1699 段,已随 SQL 脚本导入。

---

## 9. 边界与不做清单

| 项 | 说明 |
|---|---|
| 前端 TS 化 | 走 `springboot4_TypeScript` |
| 行为验证码 | 走 tianai 系列 |
| 移动端工作流 | 不做(M4 候选) |
| 多租户 / SSO / 微服务 | 不做 |
| **Flowable 外部任务(External Worker)** | 不做 |
| **Flowable 多租户** | 不做 |
| **用 Flowable 原生 Form 替代 vform3** | 不做 |
| **`act_hi_*` 归档策略** | 未做(M6) |
| **Quartz JDBC JobStore** | 未做(继承 `springboot4`) |
| **流程设计器 UI 大改** | 不做 |
| **流程引擎表按月分表** | 不做 |
| **站内信/推送通知**(抄送) | 不做(仅列表可见) |
| **wf_task_form 存业务数据** | ⚠️ **脚手架用法**;生产建议落业务表(见 A-28) |

---

## 10. 验收标准

> **AC-01~AC-42 继承自 `springboot3` / `springboot4`**(编译 / 启动 / 表数量 / 登录 / CRUD / 锁屏 / 定时任务 / 代码生成 / Jackson 3 / Security 7 / Swagger 等),见 `docs/springboot4-需求文档.md` §10。下表为**本分支新增的流程引擎 AC**。

| # | 验收项 | 验收方法 |
|---|---|---|
| AC-01~AC-42 | 见 `springboot4` PRD(其中 AC-41 的表数量需按本分支改为 **90 表 + 2 视图**) | 同左 |
| **AC-43** | `mvn clean compile`(JDK 21)0 ERROR(8 模块) | 终端 |
| **AC-44** | 启动成功,Flowable 引擎初始化无异常 | 终端日志 |
| **AC-45** | `information_schema.tables WHERE table_schema='basicdb_flowable8'` = **90**;视图 = **2** | 终端 |
| **AC-46** | `act_*` 表数量 = **39**;`flw_*` = **7**;`wf_*` = **9**;`tbl_flow_extend_procinst` 存在 | 终端 |
| **AC-47** | **无 `act_de_*` 表**(Flowable 8 特性) | 终端 |
| **AC-48** | 「流程模型」新建 → bpmn-js 打开 → 保存 BPMN XML 成功 | 浏览器 |
| **AC-49** | 「部署」成功;`act_re_procdef` 有记录;`wf_deploy_form` 有快照 | 浏览器 + DB |
| **AC-50** | 发起流程 → 待办出现 | 浏览器 |
| **AC-51** | 同意 / 驳回 / 退回 均可执行;**意见双写**(`act_hi_comment` + `wf_comment_info`) | 浏览器 + DB |
| **AC-52** | 转办 / 委派 / 前加签 / 后加签 / 变更处理人 均生效 | 浏览器 |
| **AC-53** | **6 种退回场景**各跑通至少 1 次(普通/多实例进行中/多实例完成+Task/多实例完成+Gateway/CallActivity/SubProcess) | 浏览器 |
| **AC-54** | **8 类边界事件**均可配置;至少 timer + message 可实际触发 | 浏览器 + 日志 |
| **AC-55** | 抄送:提交时附抄送 → 「抄送我的」可见 → 标记已读 | 浏览器 |
| **AC-56** | 流程图:四色高亮正确(已办绿/待办蓝/驳回红/跳过灰) | 浏览器 |
| **AC-57** | 流程迁移:运行中实例成功迁移;节点不匹配时**事务回滚** | 浏览器 + DB |
| **AC-58** | 流程清盘:两步确认 + 短语;清后流程表为空、平台表不受影响 | 浏览器 + DB |
| **AC-59** | 表单:改 `wf_form` 后**未重新部署** → 在跑实例仍读旧快照(行为符合预期) | 浏览器 |
| **AC-60** | 表达式:被引用的表达式**不可删除**(报错) | 浏览器 |
| **AC-61** | `workflow:*` 权限串:无权限用户访问 13 Controller 的接口 → 403 | 浏览器 |
| **AC-62** | 菜单 1500~1699 段已导入,流程菜单可见 | 浏览器 + DB |
| **AC-63** | `/swagger-ui/index.html` 中可见 `Wf*` 接口分组 | 浏览器 |
| **AC-64** | 前端基础平台页面零改动(`git diff springboot4 -- web-ui/src/views/basic-admin` 为空或仅菜单差异) | 终端 |
| **AC-65** | 移动端基础页面正常(不含工作流) | 浏览器 |
| **AC-66** | `docs/流程引擎使用说明.md` 与实际功能一致(抽查 5 个章节) | 人工 |

---

## 附录A 待确认问题清单

| 编号 | 问题 | 建议默认 | 阻塞 |
|---|---|---|---|
| A-1~A-22 | 继承项(移动端 / refresh / 日志保留 / 互踢 / 富文本 / deprecation / 生成器模板 / actuator / druid IP / Quartz 持久化 / Redis 清空 / fastjson2 等) | 见 `springboot4` PRD | 否 |
| **A-23** | **移动端是否需要工作流(待办/审批)?** | 建议 M4 排期;业务方要求强则优先 | 否 |
| **A-24** | **`act_hi_*` 历史表何时归档?** | 建议 M6;阈值:单表 > 500 万行 | 否 |
| **A-25** | **流程清盘是否要在生产禁用?** | 建议通过 `sys_config` 开关 + 独立权限双重管控 | 否 |
| **A-26** | **是否需要 Flowable 外部任务(External Worker)?** | 当前不做;若接入 AI/外部系统再评估 | 否 |
| **A-27** | **是否需要流程多租户?** | 当前不做(平台无多租户) | 否 |
| **A-28** | **`wf_task_form` 存业务数据的脚手架用法是否要在生产整改?** | **建议整改**:业务字段落独立业务表 + `proc_ins_id` 软关联 | 否 |
| **A-29** | **抄送是否需要站内信/邮件/企微通知?** | 当前仅列表;若需要再加 | 否 |
| **A-30** | **流程图是否需要支持节点耗时/审批人叠加显示?** | 当前只有四色;可增强 | 否 |
| **A-31** | **bpmn-js 升级策略(17.11.1 是否长期锁定)?** | 锁版本;升级需回归自定义属性面板 | 否 |
| **A-32** | **是否需要给 `springboot4-extend-flowable` 也做 TS 化分支?** | 目前仓库无此分支;按需新建 | 否 |

---

## 附录B 业务回填模板

```markdown
### 模块: <名称>
| 字段 | 类型 | 必填 | 校验 |
|---|---|---|---|
| ... | ... | ... | ... |
```

---

## 附录C 流程引擎交付检查清单

- [ ] 8 个 Maven 子模块(`extend-flowable` 已注册)
- [ ] `spring.flowable.database-schema` = **`basicdb_flowable8`**,与 JDBC `currentSchema` 一致
- [ ] `spring.flowable.idm.enabled: false`(用 `spring.flowable.*` 前缀)
- [ ] `history-level: audit` / `check-process-definitions: false` / `form-field-validation-enabled: false`
- [ ] `spring.main.allow-circular-references: true`
- [ ] 启动后 `basicdb_flowable8` 有 **90 表 + 2 视图**
- [ ] **无 `act_de_*` 表**
- [ ] 13 个 `Wf*Controller` 均可访问
- [ ] 19 个 ServiceImpl 已装配
- [ ] 模型保存产 **BPMN XML**(非 JSON)
- [ ] 部署后 `wf_deploy_form` 有快照
- [ ] 18 类审批动作可用
- [ ] 6 种退回策略可用
- [ ] 8 类边界事件可配 + 4 个外部触发接口可用
- [ ] 抄送 / 流程图 / 迁移 / 清盘 可用
- [ ] `workflow:*` 权限串覆盖全接口
- [ ] 菜单 1500~1699 已导入
- [ ] 前端 21 个 workflow vue + bpmn-js 17.11.1 + vform3 3.0.10

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `springboot4` PRD(v0.1)为基线(G1~G19 / AC-01~AC-42 / A-1~A-22 继承),新增 G20~G30 流程引擎目标、§6.11 流程引擎需求(13 小节:模块规模 / 13 Controller 能力矩阵 / 18 类审批动作 / 8 类边界事件 / 6 种退回策略 / 抄送 / 迁移 / 清盘 / 表单与表达式 / 流程图 / 关键配置 / 数据库 / 前端)、场景 S16~S24、AC-43~AC-66、A-23~A-32、附录C 交付检查清单 19 项 |
