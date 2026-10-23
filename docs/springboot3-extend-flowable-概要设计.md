# 基础平台(base-postgresql-vue3) 概要设计(HLD) — `springboot3-extend-flowable` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot3-extend-flowable`** 分支
> 前置依赖:《需求文档》v0.1,《架构方案》v0.1,《数据库设计》v0.1
> 父分支:`springboot3`(继承其 HLD)

---

## 1. 文档信息与版本

| 版本 | 日期 | 变更 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:在 `springboot3` HLD 基础上展开流程引擎概要设计 |

---

## 2. 模块划分

### 2.1 后端模块

| 模块 | 角色 | 文件数 | 关键类 |
|---|---|---|---|
| `extend-flowable` | 流程引擎业务模块 | 193 java | 13 controller + 24 service impl + 10 mapper + 8 domain |
| `basic-admin-api` | 启动入口 | 继承父 | BasicApplication |
| `basic-admin-core` | 系统扩展(本分支加 SysFiles 流程字段) | 继承 + 增量 | SysFiles / ISysUserService |
| `basic-common` | 工具基类 | 继承 + 增量 | BaseModel / PageQuery / BaseMapperPlus |
| `basic-framework` | 框架(Web/Security) | 继承 | WebMvcConfig / SecurityConfig |

### 2.2 前端模块

| 路径 | 角色 | 文件数 |
|---|---|---|
| `web-ui/src/api/workflow/*` | 13 个 API 客户端 | 13 |
| `web-ui/src/views/basic-extend/workflow/*` | 22 个流程视图 | 22 |
| `web-ui/src/components/{FormRender,ProcessUser,ProcessRole,ProcessStandUser}` | 4 个流程公共组件 | 4 |
| `web-ui/src/plugins/package/*` | bpmn-js 设计器 + 属性面板 | 51 |

---

## 3. 关键时序图

### 3.1 启动流程

```
┌────┐      ┌─────┐      ┌──────┐        ┌──────┐       ┌─────────┐
│ UI │      │ WfModelController │   WfModelService   │   Flowable    │
└──┬─┘      └─────┬─────┘      └──────┬─┘       └─────┬───────┘
   │  publishModel(deployId)  │                │                  │
   │ ────────────>│                 │                  │                  │
   │              │  publishModel()  │                  │                  │
   │              │ ────────────────>│                  │                  │
   │              │                 │  startProcessInstanceByKey() │
   │              │                 │ ─────────────────>│                  │
   │              │                 │                  │  create procInsId │
   │              │                 │  procInsId        │                  │
   │              │                 │ <─────────────────│                  │
   │              │   procInsId     │                  │                  │
   │              │ <────────────────│                  │                  │
   │   procInsId  │                 │                  │                  │
   │ <────────────│                 │                  │                  │
   │  router.push('/workflow/process/detail') │                  │                  │
```

### 3.2 审批任务

```
┌────┐  ┌──────┐  ┌────────┐  ┌────────────┐  ┌───────┐
│ UI │  │ WfProcessController │  │ WfProcessService │  │ WfFlowableTaskService │  │ Flowable │
└──┬─┘  └────┬─┘  └────┬───┘  └────┬─┘  └────┬──┘
   │ complete(taskId, vars, comment) │          │                │                  │
   │ ────────────>│          │                │                  │
   │              │ completeTask()│                │                  │
   │              │ ───────────────>│                │                  │
   │              │          │  complete(taskId, vars)         │
   │              │          │ ─────────────────>│                  │
   │              │          │                │  TaskCompleted    │
   │              │          │                │ ────────────>     │
   │              │          │                │  触发 WfTaskCompletedHandler
   │              │          │                │  → 写 wf_comment_info
   │              │          │                │  → 触发下一节点 TaskCreated
   │              │          │                │ <─────────────────│
   │              │   ok     │                │                  │
   │              │ <─────────│                │                  │
   │   200        │          │                │                  │
   │ <────────────│          │                │                  │
```

### 3.3 设计器保存 + 部署

```
┌────┐  ┌────────┐  ┌────────┐  ┌─────────┐
│ UI │  │ design.vue │  │ model API  │  │ Flowable │
└──┬─┘  └────┬───┘  └────┬──┘  └────┬────┘
   │ bpmn-js save button │          │                │
   │ 触发 @save 事件      │          │                │
   │  bpmnXml             │          │                │
   │  POST /workflow/model/save  │          │                │
   │ ───────────────────────────────>│                │
   │                    │ saveModel(bpmnXml)│                │
   │                    │ ──────────>│                │
   │                    │          │  校验 BPMN     │
   │                    │          │  写 act_re_model│
   │                    │          │ ───────────────>│
   │                    │  ok     │                │
   │                    │ <────────│                │
   │  200 + 自动调 deployModel │          │                │
   │  POST /workflow/model/deploy│          │                │
   │ ───────────────────────────────>│                │
   │                    │ deploy() │                │
   │                    │ ──────────>│                │
   │                    │          │ 写 act_re_deployment
   │                    │          │ 写 act_re_procdef
   │                    │          │ 写 wf_deploy_form
   │  200               │          │                │
   │ <───────────────────│          │                │
   │  router.push('/workflow/model')                │
```

---

## 4. 关键流程

### 4.1 流程发起

1. **入口**: 流程中心 / 模型列表「发起」按钮
2. **路由**: `router.push('/workflow/process/start', { deployId })`
3. **页面**: 加载 vform3 表单 → 用户填表 → 提交
4. **后端**:
   - `WfModelService.publishModel` 调 Flowable `runtimeService.startProcessInstanceByKey`
   - 写 `tbl_flow_extend_procinst`(业务单号/状态)
   - 写 `wf_comment_info`(发起人意见, comment_type='start')
5. **返回**: procInsId → 跳 `/workflow/process/detail`

### 4.2 流程审批

1. **入口**: 我的待办 / 流程中心
2. **API**: `GET /workflow/process/todoList?userId=&pageNum=&pageSize=`
3. **后端**: `WfProcessService.selectPageTodoProcessList` 联表 act_ru_task + act_ru_execution + act_re_procdef
4. **页面**: 点击 → `TaskDialog` 弹窗
5. **提交**: 8 项审批动作任一 → `POST /workflow/process/complete`(perms 校验)
6. **后端**:
   - 校验当前用户是否 assignee 或候选组
   - 调 `WfFlowableTaskService.complete` → Flowable `taskService.complete`
   - 触发 `WfTaskCompletedHandler` → 写 `wf_comment_info` + 附件关联
   - 触发 `WfTaskCreatedHandler`(下一节点) → 推送通知
7. **返回**: 200,前端刷新列表

### 4.3 流程迁移

1. **入口**: 流程迁移 / 我的已发(在「在途」流程点「迁移」)
2. **诊断**: `GET /workflow/process/migrate/diagnose?processDefinitionKey=`
3. **选版本**: 用户选目标 version
4. **校验**: `POST /workflow/process/migrate/validate?procInsId=&targetVersion=`
5. **执行**: `POST /workflow/process/migrate/execute`
6. **后端**:
   - `WfProcessMigrateService.migrate` 用自定义 `WfFlowableCommand`
   - 逐节点推进 + 记录步骤
   - 失败回滚到原状态

---

## 5. 状态机

### 5.1 流程实例状态

```
            start
              │
              ▼
         ┌────────┐
         │ running │ ──suspend──> ┌──────────┐
         └────┬───┘                │ suspended │ ──activate──> ┌────────┐
              │                    └──────────┘                │ running │
              │ end_process                                     └────────┘
              ▼
         ┌──────────┐
         │ completed│
         └──────────┘

         running ──cancel──> ┌──────────┐
                             │ canceled │
                             └──────────┘
```

### 5.2 任务状态

```
created → assigned → completed
   │         │          ↑
   │         │          │
   │         └─claim─>  │
   │         (组任务)   │
   │                    │
   └─delete─> (删除任务)│
                        │
                  deleted/withdrawn
```

### 5.3 自定义状态 vs Flowable 内置

本分支**不**改 Flowable 内部状态机,只通过 `tbl_flow_extend_procinst.process_status` 业务化:

| 业务状态 | 含义 | 触发 |
|---|---|---|
| running | 流转中 | 启动时 |
| suspended | 挂起 | admin 操作 |
| completed | 已结束 | Flowable process_completed 事件 |
| canceled | 主动取消 | cancel 动作 |

---

## 6. 错误处理

| 错误码 | 场景 | 处理 |
|---|---|---|
| 401 | 未登录 | 跳 /login |
| 403 | 无权限 | 后端 @PreAuthorize 拒绝;前端 v-hasPermi 隐藏按钮 |
| 404 | 流程实例不存在 | 后端返 404;前端弹 ElMessage.error |
| 409 | 流程已结束 | 后端 checkFlowIsEnd;前端禁用按钮 |
| 500 | BPMN XML 格式错 | 后端 saveProblems 弹窗列错 |

---

## 7. 安全设计

### 7.1 认证

- 继承父分支 JWT
- `/login` 走 RuoYi 标准流程(captchaImage + bcrypt 校验)

### 7.2 授权

- **路由级**: `dynamicRoutes` 里的 workflow hidden 路由(6 个)无 perms,**所有登录用户可访问**(看业务表即可,实际操作被 API 拦截)
- **API 级**: 后端 controller 的 `@PreAuthorize("@ss.hasPermi('workflow:xxx')")`
- **按钮级**: 前端 `v-hasPermi="['workflow:xxx']"` 指令

### 7.3 数据权限

- 普通用户: 只能看自己发起的 / 自己审批的
- 流程管理员: 可看所有实例(`@ss.hasPermi('workflow:process:selectProcessPrcInstance')`)
- super_admin: 全部

### 7.4 跨域

- 继承父分支 CORS 配置
- flowable 设计器无跨域需求(都从 web-ui 内发起)

---

## 8. 性能与扩展

### 8.1 当前性能边界

- 单实例:支持 100 并发审批
- 历史实例:1 万级(< 1GB 磁盘)
- 流程定义:100+ 不同 procdef

### 8.2 优化空间(后续)

- 异步执行器外部化(Job / Timer 不阻塞业务线程)
- Redis 缓存任务列表(减少 PG 查询)
- 历史数据归档(按月分区 act_hi_*)
- bpmn-js 设计器按需懒加载(vendor chunk 已分 1.5MB)

### 8.3 集群化(后续)

- 多 basic-admin-api 实例 + Spring Session 共享
- Flowable Async Executor 集群模式
- 数据库连接池调大(单实例 20 → 集群 100)

---

## 9. 风险与约束

| 风险 | 影响 | 缓解 |
|---|---|---|
| Flowable 7.x 升级频繁 | API 漂移 | 锁 7.2.0,定期 review |
| bpmn-js 自定义扩展与官方冲突 | 设计器功能异常 | 走 bpmn-js properties-panel 标准 API |
| vform3 UMD 与 vite ESM 冲突 | 组件无法注册 | 已确认 install 函数;放 optimizeDeps |
| 循环依赖 spring 启动失败 | 无法部署 | 改用 @Lazy,不动全局开关 |
| sys_menu 写入 schema 选错 | 菜单丢失 | 写 basicdb_flowable7(与新导出 SQL 一致) |
| 业务表 7 张 vs sb2 不一致 | 跨分支数据不兼容 | 文档列差异;升级时给迁移脚本 |

---

## 10. 待办事项

- [ ] 集群模式部署文档
- [ ] 流程监控大盘(分独立分支)
- [ ] 移动审批 web-mp
- [ ] 流程导入导出(zip 包)
- [ ] 表单字段权限(节点上控制字段可读/可写/隐藏)

