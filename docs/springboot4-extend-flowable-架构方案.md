# 基础平台(base-postgresql-vue3) 架构方案 — `springboot4-extend-flowable` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot4-extend-flowable`** 分支(基于 RuoYi-Vue **3.9.2** 改造)
> 上游依赖:`springboot4`(**Spring Boot 4.1.1 / JDK 21 / Jackson 3**)→ `springboot3`(SB 3.5.16 / JDK 17)→ `master`(SB 2.5.14 / JDK 11)
> 本分支 = **`springboot4` 基线 + `extend-flowable` 子模块集成 Flowable 8.0.0**;**前端两端保持 JavaScript、业务基础功能不动**。
> **本分支最大技术差异 = Flowable 8.0.0 的模型存储从「设计器 JSON」改为「BPMN XML」**(Flowable 7 起移除 json-converter)。

---

## 1. 系统定位

**一句话定位**:在 `springboot4`(JDK 21 + SB 4.1.1 + Jackson 3 + Security 7)的 RBAC 基础平台之上,**叠加 Flowable 8.0.0 流程引擎与一整套自研工作流定制层**(建模/部署/发起/审批/退回/加签/抄送/边界事件/迁移/清盘),配套 bpmn-js 17 设计器与 vform3 自定义表单,形成**可开箱使用的「后台管理 + 流程引擎」一体化平台**。

| 维度 | 内容 |
|---|---|
| 使用方 | 内部业务开发者(Java 后端 + Web 前端)、流程设计者(业务侧配置流程)、最终审批人与发起人 |
| 触发方式 | PC 浏览器(建模/审批主场景)、移动端(登录 + 通知);均通过 HTTP/JSON 调用后端 REST |
| 数据形态 | PostgreSQL(**`basicdb_flowable8` schema**,**90 张表 + 2 视图**) + 本地文件上传 + Redis(Jackson 3 序列化) |
| 范围边界 | RBAC 系统管理 + 字典/参数/通知 + 定时任务 + 代码生成 + 登录/锁屏 + 操作日志 + **流程分类/模型/表单/表达式/部署/发起/审批/退回/转办/委派/加签/抄送/边界事件/迁移/清盘** |
| 不做清单(本分支) | 前端 TS 化(走 `springboot4_TypeScript`)、行为验证码(走 tianai 系列)、多租户、SSO 联邦、微服务拆分 |

### 1.1 核心用户动作必做项

**平台基础(继承 `springboot4`)**

- 登录 / 注销(账号 + 密码 + RSA + kaptcha 2.3.3)
- 用户/角色/菜单/部门/岗位/字典 基础数据维护
- 动态路由 + 按钮级权限
- 通知公告 + 文件上传下载
- 定时任务(Cron)在线管理
- 代码生成
- 锁屏 → 解锁

**流程引擎(本分支新增)**

- **流程分类**(树形)与**流程模型**(bpmn-js 设计器建模 → 保存 BPMN XML → 校验 → 部署)
- **表单定义**(vform3)、**表单分类**、**表单模板**、**部署表单快照**
- **表达式字典**(节点条件表达式)
- **发起流程** / **审批**(同意、驳回、退回、转办、委派、前加签、后加签、会签回退、终止、撤回、跳过节点、变更处理人)
- **抄送**(随审批动作提交 + 「抄送我的」)
- **边界事件**(8 种类型 + 4 个外部触发接口)
- **流程迁移**(运行中实例迁到新定义)
- **流程清盘**(一键清空流程数据,两步确认)

---

## 2. 约束与质量属性

> **⬆️ 继承自 `springboot4` 的约束**(JDK 21 / SB 4.1.1 / Jackson 3 / Security 7 / starter 4)全部继续有效;下表把**本分支新增的 Flowable 相关约束**一并列出。

| 属性 | 目标 | 标注 |
|---|---|---|
| **JDK** | **JDK 21**(⬆️ 继承) | 强约束 |
| **Spring Boot** | **4.1.1**(⬆️ 继承) | 强约束 |
| **Jackson** | **3.1.5**;**注解仍 `com.fasterxml.jackson.annotation.*`**(⬆️ 继承) | 强约束;**Flowable 8 自身也用 Jackson 3** |
| **Spring Security** | **7.1.1**;`getAuthorities()` 不得返回 null(⬆️ 继承) | 强约束 |
| **Flowable** | **8.0.0**(`flowable-spring-boot-starter-process`) | **本分支核心**;Flowable 7+ 才支持 Jakarta / SB 3+ |
| **数据库 schema** | **`basicdb_flowable8`**(**不是** `basicdb`) | **最强约束**:`spring.flowable.database-schema` 必须与 JDBC `currentSchema` 一致 |
| **数据库对象** | **90 张表 + 2 视图**(`act_*` 39 / `flw_*` 7 / `wf_*` 9 / `tbl_*` 1 / `sys_*` 21 / `qrtz_*` 11 / `gen_*` 2;视图 `list_table` / `list_column`) | 实测核对 |
| **模型存储格式** | **BPMN XML**(`act_re_model` + `act_ge_bytearray`);**无 `act_de_*` 表** | Flowable 7 起移除 `flowable-json-converter` |
| **IDM** | **关闭**(`spring.flowable.idm.enabled: false`) | 人员/组织用平台自身的 `sys_*` |
| **历史级别** | `audit` | 支持审批意见 / 流程图 / 迁移 |
| **循环依赖** | `spring.main.allow-circular-references: true` | `extend-flowable` 内 `Wf*Service` 相互依赖 |
| **表单校验** | `form-field-validation-enabled: false` | 避免 Flowable 表单字段校验干扰业务表单 |
| **前端** | 两端 **JavaScript**;新增 **bpmn-js 17.11.1** + `bpmn-js-properties-panel 5.28` + **vform3-builds 3.0.10** | ⬆️ 基础平台前端零改动,仅**新增**工作流页面 |
| **模块数** | **8 个 Maven 子模块**(7 + `extend-flowable`) | ⬆️ 由 7 增到 8 |
| 验证码 | kaptcha 2.3.3(未换 tianai) | ⬆️ 继承 |
| 部署 | 单体 jar,运行时 JDK 21 | |
| 构建 | 8 个子模块 | |

**最强约束**:① **schema 必须是 `basicdb_flowable8`**(写错会导致 Flowable 在默认 schema 建表);② **Flowable 8 的模型是 BPMN XML**,旧分支(6.8)导出的「模型 JSON」不能直接导入;③ **`idm.enabled: false`** 且配置键用 `spring.flowable.idm.enabled`(写旧键会被**静默忽略**);④ **审批读的是 `wf_deploy_form` 快照**,改 `wf_form` 必须重新部署才对新实例生效;⑤ **Jackson 3 / Security 7 / Jakarta** 全部继承 SB4 约束。

---

## 3. 总体架构

### 3.1 容器视图(🔶 = 本分支新增;⬆️ = `springboot4` 继承)

```
┌───────────────────────────────────────────────────────────────┐
│  用户端(⬆️ 基础平台 JS;🔶 新增工作流页面)                       │
│  ┌──────────────────────────────┐  ┌────────────────────────┐ │
│  │  web-ui (PC)                 │  │  web-mp (移动)          │ │
│  │  Vue 3.5.26 + Element Plus   │  │  uni-app 3 + uview-plus │ │
│  │  Vite 6.4.1 · JS             │  │  Vite 5.2.8 · JS        │ │
│  │  🔶 views/basic-extend/      │  │  (基础平台页面,未改)     │ │
│  │      workflow/**(21 vue)     │  │                        │ │
│  │  🔶 plugins/package/**       │  │                        │ │
│  │      (bpmn-js 17.11.1 设计器) │  │                        │ │
│  │  🔶 api/workflow/**          │  │                        │ │
│  └────────┬─────────────────────┘  └───────────┬────────────┘ │
│           │ HTTPS / JSON                        │              │
└───────────┼─────────────────────────────────────┼──────────────┘
            ▼                                     ▼
┌───────────────────────────────────────────────────────────────┐
│  ⬆️ Spring Boot 4.1.1 · JDK 21                                │
│  ┌─────────────────────────────────────────────────────────┐  │
│  │  ⬆️ Filter 链(jakarta 6.1.0)· Security 7.1.1            │  │
│  │     🔶 放行扩展:/workflow/** 走权限串(非匿名)            │  │
│  │  Controller                                                │  │
│  │   ├─ ⬆️ basic-admin-api(24 个平台 Controller)           │  │
│  │   └─ 🔶 extend-flowable(13 个 Wf* Controller)           │  │
│  │  ↓                                                         │  │
│  │  Service                                                   │  │
│  │   ├─ ⬆️ basic-admin-core / extend-quartz / business      │  │
│  │   └─ 🔶 extend-flowable(19 个 ServiceImpl)              │  │
│  │        ├─ com.basic.flowable.*(引擎层封装)              │  │
│  │        └─ com.basic.workflow.*(业务层)                  │  │
│  │  ↓                                                         │  │
│  │  🔶 Flowable 8.0.0(spring-boot-starter-process)          │  │
│  │       RepositoryService / RuntimeService / TaskService    │  │
│  │       / HistoryService / ManagementService                │  │
│  │  ⬆️ MyBatis-Plus 3.5.17 / PageHelper 4.1.1 / Druid 1.2.28│  │
│  └────────────────────────┬────────────────────────────────┘  │
│  ┌────────────────────────┴────────────────────────────────┐  │
│  │ ⬆️ AOP:LogAspect / DataScopeAspect / RateLimiter /       │  │
│  │       RepeatSubmit / DataSource                          │  │
│  │ ⬆️ Jackson 3.1.5(tools.jackson.*)· springdoc 3.1.1       │  │
│  │ 🔶 流程图生成(CustomProcessDiagramGenerator + 画布)      │  │
│  │ 🔶 全局事件监听 / 到期监听 / 用户任务监听                 │  │
│  │ 🔶 退回策略(6 种)/ 边界事件(8 型)/ 多实例处理          │  │
│  └─────────────────────────────────────────────────────────┘  │
└──────────┬──────────────────────────────────┬──────────────────┘
           ▼                                  ▼
   ┌────────────────────────────┐    ┌──────────────────────────┐
   │ PostgreSQL ≥15.1            │    │ Redis(可选)              │
   │ 🔶 schema=basicdb_flowable8 │    │ 字典/Token/限流           │
   │ 🔶 90 张表 + 2 视图         │    │ ⬆️ Jackson 3 序列化       │
   │  act_*39 flw_*7 wf_*9       │    │                          │
   │  tbl_*1 sys_*21 qrtz_*11    │    │                          │
   │  gen_*2                     │    │                          │
   └────────────────────────────┘    └──────────────────────────┘
```

### 3.2 模块清单(**8 个** Maven 子模块;⬆️ 继承,🔶 新增)

| 模块 | Maven artifact | 职责 | 本分支 |
|---|---|---|---|
| `basic-admin-api` | `basic-admin-api` | 启动入口 + 平台 Controller | ⬆️ 零改动(仅启动类扫描已覆盖) |
| `basic-admin-core` | `basic-admin-core` | 业务 Service/Mapper/Domain | ⬆️ 零改动 |
| `basic-framework` | `basic-framework` | Security/Redis/MP/Swagger/Filter/AOP/Token/限流 | ⬆️ 零改动 |
| `basic-common` | `basic-common` | 工具/注解/枚举/基类 | ⬆️ 零改动 |
| `business-core` | `business-core` | 业务扩展点(`Demo`) | ⬆️ 零改动 |
| `extend-quartz` | `extend-quartz` | Quartz 定时任务 | ⬆️ 零改动 |
| `extend-generator` | `extend-generator` | 代码生成 | ⬆️ 零改动 |
| **`extend-flowable`** | `extend-flowable` | **流程引擎集成 + 定制层** | **🔶 新增(223 个 Java 文件)** |

**`extend-flowable` 规模实测**:

| 项 | 数量 |
|---|---|
| Java 文件 | **223** |
| Controller | **13** |
| ServiceImpl | **19** |
| 包根 | `com.basic.flowable`(引擎层)+ `com.basic.workflow`(业务层) |

### 3.3 数据模型(**`basicdb_flowable8`**,90 表 + 2 视图)

> **🔶 与 `springboot4` 的关键差异**:schema 从 `basicdb`(34 表)换成 **`basicdb_flowable8`(90 表 + 2 视图)**。
> 基础平台 34 张表**全部保留**(`sys_*` 21 + `qrtz_*` 11 + `gen_*` 2),**新增 56 个流程相关对象**。

| 域 | 表 | 数量 | 说明 |
|---|---|---|---|
| **Flowable 引擎** | `act_*` | **39** | 模型(`act_re_model`)/ 部署(`act_re_deployment` / `act_re_procdef`)/ 运行时(`act_ru_*`)/ 历史(`act_hi_*`)/ 通用(`act_ge_*`)/ 事件日志(`act_evt_log`) |
| **Flowable 事件注册** | `flw_*` | **7** | `flw_event_definition` / `flw_event_deployment` / `flw_event_resource` / `flw_channel_definition` / `flw_ru_batch` / `flw_ru_batch_part` / **`flw_expression`**(表达式字典) |
| **工作流业务** | `wf_*` | **9** | `wf_category` / `wf_category_org_link` / `wf_comment_info` / `wf_copy` / `wf_deploy_form` / `wf_form` / `wf_form_category` / `wf_form_template` / `wf_task_form` |
| **流程实例扩展** | `tbl_flow_extend_procinst` | **1** | 发起人 / 流程状态 / 业务键 |
| **平台系统** | `sys_*` | **21** | ⬆️ 与 `springboot4` 完全一致 |
| **定时任务** | `qrtz_*` | **11** | ⬆️ 存在(Quartz 仍 RAMJobStore) |
| **代码生成** | `gen_*` | **2** | ⬆️ 一致 |
| **视图** | `list_table` / `list_column` | **2** | 代码生成器读库表元数据(写死 `basicdb_flowable8`) |
| **合计** | — | **90 表 + 2 视图** | |

> ⚠️ **无 `act_de_*` 表**:Flowable 8 不含 designER 建模器的 `act_de_model*` 系列(6.8 分支有);模型只落 `act_re_model` + `act_ge_bytearray`。

### 3.4 关键流程

#### 流程建模 → 部署(🔶 本分支核心,Flowable 8 用 BPMN XML)

```
[前端 bpmn-js 设计器]
   │ 用户在 canvas 拖拽建模,属性面板配 IdmAssignee / IdmPermission 等
   ▼
POST /workflow/model/save (提交 BPMN XML)
   │
   ▼
🔶 WfModelSaveXmlService
   │  ① BPMN XML 合规校验(ProcessValidator)
   │  ② 写入 act_re_model(name / key / version)
   │  ③ BPMN XML 存 act_ge_bytearray(editor source)
   ▼
POST /workflow/model/deploy
   │
   ▼
RepositoryService.createDeployment()
   .addString(bpmn.xml)
   .deploy()
   │  → act_re_deployment / act_re_procdef / act_ge_bytearray
   ▼
🔶 拷贝 wf_deploy_form 表单快照(必须!)
   │
   ▼
新流程定义可被发起
```

> ⚠️ **与 6.8 分支的本质差异**:6.8 用 `flowable-json-converter` 存**设计器 JSON**;Flowable 7 起**移除该组件**,8.0 必须存 **BPMN XML**。旧分支导出的「模型 JSON」不能直接导入。

#### 发起流程

```
POST /workflow/process/start
   │
   ▼
🔶 WfProcessServiceImpl
   │  ① 校验流程定义已部署且激活
   │  ② 写 tbl_flow_extend_procinst(发起人 / 状态 running)
   │  ③ runtimeService.startProcessInstanceByKey(...)
   │  ④ 保存首节点表单到 wf_task_form
   │  ⑤ 回写 proc_ins_id
   ▼
进入待办
```

#### 审批(18 类动作)

```
POST /workflow/task/complete | reject | return | transfer | delegate | ...
   │
   ▼
🔶 WfTaskServiceImpl
   │  ① 权限校验(assignee / candidate)
   │  ② 写审批意见(wf_comment_info + act_hi_comment 双写)
   │  ③ 执行引擎动作
   │  ④ 抄送(若有)→ wf_copy
   │  ⑤ 全局事件监听更新 processStatus
   ▼
流程推进 / 结束
```

#### 退回(6 种策略)

```
POST /workflow/task/return {taskId, targetActivityId}
   │
   ▼
🔶 RollbackCmd + 策略工厂
   ├─ DefaultTaskNextGatewayRollbackOperateStrategy(普通 UserTask + Gateway)
   ├─ ActiveMultiInstanceTaskRollbackOperateStrategy(进行中多实例)
   ├─ CompletedMultiInstanceTaskAndNextDefaultTaskRollbackOperateStrategy
   ├─ CompletedMultiInstanceTaskAndNextDefaultGateWayTaskRollbackOperateStrategy
   ├─ NextCallActivityRollbackOperateStrategy(CallActivity)
   └─ NextSubProcessRollbackOperateStrategy(SubProcess)
   ▼
回到目标节点
```

#### 边界事件(8 种类型)

```
支持类型(timer / message / signal / error / escalation / cancel / compensation / conditional)
   │
   ├─ 引擎内部触发(timer 到期 / message 到达 / signal 广播 ...)
   └─ 🔶 外部触发接口(POST /workflow/boundary/**)—— 4 个
   ▼
状态标记 boundary_<type>
```

#### 流程图生成

```
GET /workflow/process/flowViewer/{procInsId}
   │
   ▼
🔶 CustomProcessDiagramGenerator
   │  ① 取 BpmnModel + 历史活动实例
   │  ② 计算 已办(绿)/ 待办(蓝)/ 驳回(红)/ 跳过(灰)
   │  ③ CustomProcessDiagramCanvas 绘制
   ▼
PNG 流 → 前端展示
```

### 3.5 状态机

#### 流程实例状态(🔶 本分支新增)

| 状态值 | 含义 | 触发 |
|---|---|---|
| `running` | 进行中 | 发起 / 审批推进 |
| `completed` | 已完成 | 到达结束事件 |
| `reject` | 已驳回(终止) | 审批人驳回 |
| `return` | 已退回 | 退回上一节点/指定节点 |
| `canceled` | 已取消(撤回/废除) | 发起人撤回 / 管理员废除 |
| `delete` | 已删除 | 物理删除 |
| `boundary_<type>` | 边界事件触发 | 8 种边界事件 |

#### 边界事件类型

| 类型 | 说明 |
|---|---|
| `timer` | 定时触发 |
| `message` | 消息触发 |
| `signal` | 信号广播 |
| `error` | 错误捕获 |
| `escalation` | 升级 |
| `cancel` | 取消(事务子流程) |
| `compensation` | 补偿 |
| `conditional` | 条件触发 |

#### 平台对象(⬆️ 与 `springboot4` 一致)

| 对象 | 状态 | 流转 |
|---|---|---|
| 定时任务 | `NORMAL` / `PAUSED` | changeStatus |
| 用户 | `0=正常` / `1=停用` | changeStatus |
| 通知已读 | 未读 / 已读 | `sys_notice_read` 唯一 |

---

## 4. 关键决策 ADR

> **ADR-01~ADR-30 继承自 `springboot4`**(基础底座 / Jackson 3 / Security 7 / starter 4 / Quartz / 不引 fastjson2 等),详见 `docs/springboot4-架构方案.md` §4。下表为本分支**新增 ADR-31~ADR-42**。

| # | 决策点 | 采纳方案 | 备选 + 放弃原因 | 是否演进锁死 |
|---|---|---|---|---|
| **ADR-31** | **流程引擎选型** | **Flowable 8.0.0** | Activiti 7:社区活跃度低于 Flowable;Camunda 7:JBoss 系依赖重;Flowable 6.8(旧分支):**不支持 SB 4 / JDK 21 / Jakarta 6** | **是** |
| **ADR-32** | **模块隔离** | 独立 Maven 子模块 **`extend-flowable`**(8 个模块),不侵入 `basic-*` | 直接塞进 `basic-admin-core`:平台与流程耦合,无法按需裁剪 | **是** |
| **ADR-33** | **schema 隔离** | **`basicdb_flowable8`**(独立 schema) | 用 `basicdb`:Flowable 39 张 `act_*` 与平台表混在一起,清盘/迁移风险大 | **是** |
| **ADR-34** | **模型存储格式** | **BPMN XML**(`act_ge_bytearray`) | 设计器 JSON(**Flowable 7 已移除 `flowable-json-converter`**,不可用);自研序列化:成本高 | **是** |
| **ADR-35** | **IDM 关闭** | `spring.flowable.idm.enabled: false`,人员/组织复用平台 `sys_*` | 启用 Flowable IDM:与平台用户体系双写,数据不一致 | **是** |
| **ADR-36** | **历史级别** | `history-level: audit` | `none`:无审批意见/流程图;`full`:变量全量存,表膨胀 | 否 |
| **ADR-37** | **表单方案** | **vform3**(`vform3-builds 3.0.10`)生成 JSON 存 `wf_form.content`,部署时快照到 `wf_deploy_form` | 用 Flowable Form:与平台表单引擎割裂;自研表单设计器:成本高 | 否 |
| **ADR-38** | **流程图渲染** | 后端生成 PNG(`CustomProcessDiagramGenerator` + Activiti BpmnLayout) | 前端 bpmn-js 渲染:需传完整模型 + 状态,信息量大;SVG 生成:自研成本高 | 否 |
| **ADR-39** | **退回实现** | **策略模式 + 6 种策略**(RollbackCmd) | 统一 `moveActivityIdTo`:多实例/CallActivity/SubProcess 场景行为不符 | **是** |
| **ADR-40** | **审批意见双写** | `act_hi_comment` + `wf_comment_info` 双写 | 只写引擎表:业务查询需 join 引擎表;只写业务表:流程图意见缺失 | 否 |
| **ADR-41** | **循环依赖** | `spring.main.allow-circular-references: true` | 重构解耦 `Wf*Service` 依赖:工作量大且易引入回归 | 否 — 可重构 |
| **ADR-42** | **流程清盘范围** | 一键清空 **46 张流程相关表**,两步确认 + 确认短语 | 不做:测试/演示环境脏数据难清;只清部分:残留脏数据导致后续异常 | 否 |

---

## 5. 里程碑

| 阶段 | 内容 | 验收 | 触发升级的信号 |
|---|---|---|---|
| **M0(已交付,`master`)** | RuoYi 3.8.2 基础 + SQL 迁 PostgreSQL + 锁屏 | 7 模块;34 表可恢复 | — |
| **M1(已交付,`springboot3`)** | SB 2.5.14 → 3.5.16 / JDK 11 → 17(Jakarta / Security 6 / springdoc 2.9) | JDK 17 下 0 ERROR | — |
| **M2(已交付,`springboot4`)** | SB 3.5.16 → 4.1.1 / JDK 17 → 21(**Jackson 3** / Security 7 / starter 4 / Quartz starter) | JDK 21 下 0 ERROR;`grep com.fasterxml.jackson` 仅 annotation | — |
| **M3(已交付,本分支)** | **集成 Flowable 8.0.0**:`extend-flowable` 模块(223 Java / 13 Controller / 19 ServiceImpl)+ schema `basicdb_flowable8`(90 表 + 2 视图)+ BPMN XML 模型存储 + bpmn-js 17 设计器 + vform3 表单 + 21 个工作流页面 + 1500~1699 菜单权限 | 启动成功;建模→部署→发起→审批→流程图全链路通过;8 类边界事件 / 6 种退回 / 迁移 / 清盘 均可用 | — |
| **M4(下一轮)** | 移动端工作流(待办/审批) | 见 web-mp 待办 | 业务方要求移动审批 |
| **M5(可选)** | **Flowable 异步执行器调优** + 定时任务持久化(Quartz JDBC JobStore) | 多实例可用 | 并发量上升 / 需要任务持久化 |
| **M6(可选)** | **流程引擎表归档策略**(`act_hi_*` 膨胀) | 历史表定期归档 | 历史表 > 千万行 |
| **M7(可选)** | **流程版本兼容治理**(旧实例迁移到新定义) | 迁移工具全覆盖 | 流程频繁改版 |
| **M8(分支)** | 前端 TS 化 | 单独分支(目前无 `springboot4-extend-flowable_TypeScript`) | 需要类型保护 |
| **M9(分支)** | SSO:接入 keycloak 联邦 | 账号互通 | 出现第二个业务系统需统一登录 |

**本次不做,直到有信号**:微服务拆分、K8s 化、灰度发布、APM 探针、GraalVM 原生镜像、Flowable 多租户、Flowable 外部任务(External Worker)。

---

## 6. 审查与遗留风险

### 6.1 已覆盖

| 维度 | 设计 |
|---|---|
| 失败设计 | `GlobalExceptionHandler` + `R/AjaxResult`;**流程迁移事务内失败回滚**;`WfProcessMigrateException` |
| 幂等 | `@RepeatSubmit`;**部署时表单快照**(避免改表单影响在跑实例) |
| 权限 | 菜单级 + 按钮级 + 数据级;**`workflow:*` 权限串覆盖 13 Controller** |
| 一致性 | 关系型事务;审批意见**双写**(`act_hi_comment` + `wf_comment_info`);异步日志隔离 |
| 可观测 | `sys_oper_log` / `sys_logininfor` / springdoc / Druid / **流程图可视化** |
| **🔶 引擎隔离** | 独立 schema + 独立模块;清盘有确认短语 |

### 6.2 风险清单

> **R1~R34 继承自 `springboot4`**(Jackson 2/3 混用 / 运行时注册 Module / 循环依赖 / 异常体系 / `NumberSerializer` / Security 7 NPE / Quartz RAMJobStore / Redis 脏数据 / 多态安全 / springdoc 兼容 / MP-pagehelper 跨版本 / `WebMvcConfigurer` 废弃 / Druid 暴露 / 生成器模板 等),详见 `docs/springboot4-架构方案.md` §6.2。下表为**本分支新增 R35~R48**。

| # | 风险 | 影响 | 缓解 |
|---|---|---|---|
| **R35** | **schema 配错**(`database-schema` 与 JDBC `currentSchema` 不一致)→ Flowable 在默认 schema 建表 | **高** | 两处必须同值;启动后查 `information_schema` 校验 90 表都在 `basicdb_flowable8` |
| **R36** | **Flowable 配置键写错被静默忽略**(如 `flowable.common.app.idm-enabled`) | **中** | 以 `spring-configuration-metadata.json` 为准;以日志/表结构验证;文档已列正确键 |
| **R37** | **旧分支「模型 JSON」无法导入**(Flowable 8 只认 BPMN XML) | **中** | 迁移需重新建模或手工转 BPMN 2.0 XML |
| **R38** | **`act_id_*` 表仍可能被创建**(即使 `idm.enabled: false`) | 低 | 不影响运行;如介意可手工 drop |
| **R39** | **改 `wf_form` 不重新部署 → 在跑实例仍用旧快照** | **中** | 部署时快照到 `wf_deploy_form`;改表单后**必须重新部署**;文档已警示 |
| **R40** | **`act_hi_*` 历史表膨胀**(`history-level: audit`) | **中** | 清盘工具(46 表)/ M6 归档策略 |
| **R41** | **多实例部署下 Flowable 定时器/异步执行器重复触发** | **中** | Flowable 自带 `act_ru_job` 抢占机制可缓解;仍需压测验证;Quartz 侧另有 RAMJobStore 重复执行问题(R27) |
| **R42** | **`allow-circular-references: true` 掩盖设计问题** | 低 | 已知取舍(ADR-41);可重构解耦 |
| **R43** | **流程图生成依赖 Activiti 6 的 BpmnLayout** | 中 | 与 Flowable 8 混用需验证(实际已验证可用);若升级需复查 |
| **R44** | **bpmn-js 17 + properties-panel 5.28 与自定义属性(Idm\* / boundary\*)** | 中 | 自定义属性面板需维护;升级 bpmn-js 时需回归 |
| **R45** | **表单引擎 vform3 的 JSON 结构演进** | 中 | JSON 存 `wf_form.content`;升级 vform3 需验证旧 JSON 可渲染 |
| **R46** | **流程清盘误操作**(一键清 46 表) | **中** | 两步确认 + 确认短语;仅管理员权限串 `workflow:process:clearAll` |
| **R47** | **迁移运行中实例时节点不匹配** | **中** | 支持自动镜像 / 手动指定;事务内失败回滚 |
| **R48** | **extend-flowable 223 个文件的代码生成模板未适配** | 中 | 代码生成器模板(`extend-generator`)仍产 SB2 风格(继承 R34);流程模块自身手写 |

---

## 7. 参考来源

- `README.md`(仓库根,本分支,**§十二 已含 Flowable 集成概览 / 模块结构 / 能力清单 / 配置与坑**)
- **`docs/流程引擎使用说明.md`**(本分支已有的**使用手册**:怎么建模型、配审批人、边界事件/迁移/清盘怎么用)—— 本文档是**设计文档**,与该手册互补
- **上游基线文档**:
  - `docs/springboot4-架构方案.md` — 本分支的 SB4 基线(**ADR-01~30 与 R1~R34 均在彼处**)
  - `docs/springboot2-extend-flowable-架构方案.md` — 同主题的 **Flowable 6.8 版本**,可对比两代差异(模型格式 / schema / act_de_* / bpmn-js 版本)
  - `docs/springboot3-架构方案.md`(SB3 基线)
- 兄弟分支:`springboot4`(本分支去掉 Flowable)、`springboot4_TypeScript`(本分支 + 前端 TS)、`springboot3-extend-flowable` / `springboot2-extend-flowable`(旧引擎版本)
- Flowable 8.0.0 官方文档(迁移指南、BPMN XML、Async Executor);bpmn-js 17 / vform3 官方文档

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `springboot4` 架构方案(v0.1)为基线(ADR-01~30 / R1~R34 继承),叠加「Flowable 8.0.0 集成」专题:§2 约束(Flowable 8 / schema `basicdb_flowable8` / 90 表 + 2 视图 / BPMN XML / IDM 关闭 / 循环依赖 / 前端 bpmn-js 17 + vform3)、§3.1 容器视图、§3.2 8 模块 + extend-flowable 规模实测、§3.3 数据模型 7 域分解、§3.4 六大关键流程(建模部署/发起/审批/退回/边界事件/流程图)、§3.5 流程状态机 + 8 类边界事件、ADR-31~ADR-42 共 12 条、M3 集成里程碑 + M5~M7 后续、R35~R48 共 14 条 |
