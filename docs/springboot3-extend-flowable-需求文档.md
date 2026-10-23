# 基础平台(base-postgresql-vue3) 需求文档(PRD) — `springboot3-extend-flowable` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot3-extend-flowable`** 分支
> 前置依赖:《架构方案》v0.1,《数据库设计》v0.1
> 父分支:`springboot3`(继承其全部能力)
> 兄弟分支:`springboot2-extend-flowable`(Flowable 6.8.0 + SB 2.5.14 / JDK 11)

---

## 1. 文档信息与版本

- 当前版本:**v0.1** — 初稿,基于代码扫描 + 父分支 `springboot3-需求文档.md` 抽取
- 变更历史

| 版本 | 日期 | 变更 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:在 `springboot3` PRD 基础上追加「§6.10 流程引擎需求」(Flowable 7.2.0 + extend-flowable 模块 + 13 个 controller + 22 个视图 + vform3 表单设计器 + bpmn-js 流程设计器) |

---

## 2. 背景与目标

### 2.1 为什么做

父分支 `springboot3` 提供 RBAC + 定时任务 + 代码生成 + 双端 UI 的基础平台,**没有任何工作流能力**。企业级 OA / 审批场景绕不开流程引擎,因此本分支集成 **Flowable**(BPMN 2.0 标准 + Spring Boot 3 兼容),把"流程"作为基础平台的一等公民。

兄弟分支 `springboot2-extend-flowable` 已用 **Flowable 6.8.0** 完成类似集成,但**死锁在 Spring Boot 2.x / JDK 11**——与本分支的 SB 3.5.16 / JDK 17 体系无法合并。

### 2.2 目标清单

| 目标 | 成功标准 |
|---|---|
| G1 继承父分支全部能力 | 父分支 `springboot3` 文档中 G1~G12 全部 100% 适用,本分支不做回归测试 |
| **G2(本分支)集成 Flowable 7.2.0** | `flowable.version=7.2.0`;`mvn dependency:tree` 包含 44 个 `flowable-*.jar` 全部 7.2.0 |
| **G3(本分支)完成 jakarta.* 命名空间迁移** | Flowable 7.x 强制 jakarta,所有 `@Resource` 字段加 `@Lazy` 打破 `Wf*ServiceBase` 循环依赖 |
| **G4(本分支)新增 extend-flowable 子模块** | 193 个 Java 源文件,13 个 `Wf*Controller`,8 个 `Wf*Service` 业务子包,4 张业务表 + Flowable 80+ 张 ACT_/FLW_ 表 |
| **G5(本分支)流程建模器接入 web-ui** | bpmn-js 17.11 + 9 个 boundary / 8 个 task panel + 自定义 palette / context-pad |
| **G6(本分支)自定义表单设计器接入 web-ui** | vform3-builds 3.0.10 + `<v-form-designer>` / `<v-form-render>` 全局组件 |
| **G7(本分支)22 个流程视图 + 13 个 API** | `views/basic-extend/workflow/*` 路由可达,`api/workflow/*` 调用后端成功 |
| **G8(本分支)流程菜单 2000-2099** | `sys_menu` 2000(流程引擎)~2099(模板删除)共 38 条,绑给 super_admin |
| **G9(本分支)API 契约与 sb2 兼容** | `/workflow/{process,task,model,form,category,expression,boudary,...}` 路径与 sb2 逐项一致;后端 controller 的 `@PreAuthorize` perms 与 sb2 业务化 perms 粒度对齐 |

### 2.3 非目标(本分支不做)

- Flowable 7.x 升级到 8.x(走 `springboot3-extend-flowable-8` 分支)
- 表单引擎从 vform3 替换为 form-create / form-generator / Avue Form(后续按需)
- 流程监控大盘(走 `springboot3-extend-flowable-monitor` 分支)
- 移动端审批(走 web-mp 分支)
- 流程低代码画布(拖拽生成整个流程定义,非本分支范围)
- 流程性能压测 / 集群部署(部署文档另出)

---

## 3. 业务范围

### 3.1 流程定义

- **流程分类**(wf_category):树形分类,与组织(`wf_category_org_link`)解耦
- **流程模型**(act_re_model):BPMN 2.0 XML,经 bpmn-js 设计器编辑后入库
- **流程定义**(act_re_procdef):由模型部署产生,Flowable 引擎表
- **表单挂接**(`wf_deploy_form` / `wf_task_form`):模型部署时绑定的"通用表单" / 节点绑定的"动态表单"

### 3.2 流程实例

- **启动流程**(`POST /workflow/process/start` 或 `/workflow/model/publishModel`):从已部署的 procdef 启动
- **我的待办**(`GET /workflow/process/todoList`):当前用户作为候选/办理人的任务
- **我的已办**(`GET /workflow/process/finishedList`):历史已完成任务
- **我的已发**(`GET /workflow/process/ownList`):我发起过的流程实例
- **抄送我的**(`GET /workflow/process/copyList`):cc 列表
- **待签收**(`GET /workflow/process/claimList`):组任务,签收前不可办理
- **流程详情**(`GET /workflow/process/detail?procInsId&taskId`):含表单数据 / 流转历史 / 当前节点

### 3.3 审批动作(8+1 项)

| 动作 | perms | 字典 wf_process_permission |
|---|---|---|
| 同意 | `workflow:process:complete` | approve |
| 驳回 | `workflow:process:reject` | reject |
| 退回 | `workflow:process:return` | back |
| 转办 | `workflow:process:transfer` | transfer |
| 委派 | `workflow:process:delegate` | delegate |
| 会签回退 | (顺序会签节点专属) | countersignBack |
| 前加签 | `workflow:process:beforeAddSignTask` | beforeAddSign |
| 后加签 | (与前加签配对) | afterAddSign |
| 终止 | `workflow:process:cancel` | stop |

**业务规则**:
- 节点「授权功能」勾选 = 把上述字典里的 value 写进节点的 `extensionElements` / `permissionJson` 字段
- 列表接口返回 `permissionJson` 字符串,前端 `buildActionButtons()` 按其出现对应按钮
- 8 项是顺序会签场景下加的「会签回退」

### 3.4 流程迁移

- **迁移计划**(`POST /workflow/process/migrate`):同 KEY 不同版本的流程实例,把运行中实例从老定义迁到新定义
- **步骤推进**:逐节点推进,每步记录到审计日志
- **诊断接口**:`GET /workflow/process/migrate/diagnose?processDefinitionKey=...` 返回版本列表与 BPMN 匹配结果

### 3.5 边界事件(高级)

- **signal / message / error / cancel 触发**:`POST /workflow/boundary/cancel` 等
- **boundary 列表查询**:`GET /workflow/boundary/query?processDefinitionId=...`
- 用于流程异常干预 / 跨流程联动

### 3.6 数据清盘

- **预览清盘**:`POST /workflow/process/clear/preview` 返回 ACT_* 待删除数量
- **执行清盘**:`POST /workflow/process/clear/all` 一次性清空(慎用)

---

## 4. 角色与场景

| 角色 | 场景 | 关键能力 |
|---|---|---|
| **流程管理员** | 配置流程分类 / 字典 / 表达式 | 增删改查 wf_category / wf_form / wf_form_template / wf_expression |
| **流程建模员** | 设计流程并部署 | bpmn-js 设计器 + 部署按钮(部署即上线) |
| **流程发起人** | 选流程填表发起 | 我的已发列表 + 流程撤回 |
| **审批人(候选人)** | 处理待签收 | 待签收列表 + claim + complete |
| **审批人(办理人)** | 处理已分配任务 | 我的待办 + 8 项审批动作 + 自定义表单填写 |
| **抄送人** | 只读知情 | 抄送我的列表 + 流程详情 |
| **超级管理员(super_admin)** | 全部 | role_id=1,绑 2000-2099 全部菜单 38 项 |

---

## 5. 验收标准

| AC | 描述 | 验证方式 |
|---|---|---|
| AC-1 | mvn -DskipTests package 9 模块全绿 | mvn 命令 |
| AC-2 | 启动 basic-admin-api 0 ERROR | IDE console |
| AC-3 | pg_dump `basicdb-vue3` 含 80+ 张 ACT_/FLW_/wf_ 表 + 2000-2099 菜单 38 条 | `psql -c "\dt basicdb_flowable7.*"` |
| AC-4 | web-ui pnpm build:prod 0 error | pnpm 命令 |
| AC-5 | bpmn-js 设计器渲染 BPMN XML,可保存 + 部署 | 浏览器手动测 |
| AC-6 | vform3 表单设计器可拖拽控件,可保存 JSON | 浏览器手动测 |
| AC-7 | super_admin 登录后左侧菜单有「流程引擎」一级目录 + 9 个子菜单 | 浏览器手动测 |
| AC-8 | 普通用户(无 workflow 权限)访问 `/workflow/*` 返回 401/403 | curl + token |
| AC-9 | `/workflow/model/{id}/bpmnXml` 401(未登录) / 200(有 model:bpmnXml 权限) | curl + token |
| AC-10 | sb2 / sb3 controller 的 `@RequestMapping` 路径完全一致 | diff 工具 |

---

## 6. 待定项

| ID | 描述 | 决策依据 |
|---|---|---|
| A-1 | sys_menu 写到 `basicdb` 还是 `basicdb_flowable7` schema | **本分支选 basicdb_flowable7(与新导出的 basicdb-vue3.sql 一致)**,见数据库设计 |
| A-2 | 流程引擎 ID 用 `flowable` 还是 `flowable7` | 7.2.0,统一 `flowable`(省版本号让 sb2 兼容) |
| A-3 | 角色绑定的 sys_role 是否还要建 `workflow_admin` 独立角色 | 暂用 super_admin 全绑,后续按需拆 |

---

## 7. 与 `springboot2-extend-flowable` 的关键差异

| 维度 | sb2-extend-flowable | sb3-extend-flowable(本分支) |
|---|---|---|
| Spring Boot | 2.5.14 | 3.5.16 |
| JDK | 11 | 17 (兼容 21) |
| Jakarta | javax.* | jakarta.* |
| Flowable | 6.8.0 | 7.2.0 |
| 数据库 schema | `basicdb_flowable` | `basicdb_flowable7` |
| sys_menu schema | `basicdb_flowable` | `basicdb_flowable7` |
| 菜单 ID 区间 | 1500-1599 | 2000-2099(+500 避开 sb3 master 1-1066) |
| 字段(menu_name_en 等) | 有 | 有(沿用 sb2 风格) |
| 表单设计器 | vform3-builds 3.0.10 | vform3-builds 3.0.10(版本一致) |
| 流程设计器 | bpmn-js 17.11 | bpmn-js 17.11(版本一致) |
| MyBatis-Plus 路径 | `com.baomidou.mybatisplus.spring.service.IService` | `com.baomidou.mybatisplus.extension.service.IService` |
| 循环依赖 | spring.main.allow-circular-references=true | `@Lazy` 注解 |
| 视图路径 | views/basic-extend/workflow/* | views/basic-extend/workflow/*(完全一致) |
| 业务表 | basicdb_flowable.wf_* | basicdb_flowable7.wf_*(含 sb3 新增 3 张) |
| controller 数量 | 13 | 13(完全一致) |

