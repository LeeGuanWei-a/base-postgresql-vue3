# 基础平台(base-postgresql-vue3) 详细设计(LLD) — `springboot3-extend-flowable` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot3-extend-flowable`** 分支
> 前置依赖:《需求文档》《架构方案》《数据库设计》《概要设计》

---

## 1. 文档信息与版本

| 版本 | 日期 | 变更 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:extend-flowable 模块 193 个 java 文件 / web-ui 90 个文件 / SQL 38 菜单的关键类级详细设计 |

---

## 2. extend-flowable 模块

### 2.1 包结构(全部 193 文件)

```
com.basic.workflow
├── builder      2 文件
├── cmd          1
├── config       3
├── controller  13
├── customCache  1
├── domain      20
├── dynamic      3
├── flow         8
├── handler      5
├── mapper      10
├── service     49 (含 impl/ + 3 个 base 业务子包)
└── service
    ├── impl/         24 impl
    ├── migrate/      1
    ├── wfModel/      4 (base + service + impl)
    ├── wfProcessInfo/ 4
    └── wfProcessTask/ 4
```

---

## 3. Controller 详细设计(13 个)

### 3.1 WfProcessController `/workflow/process`

| 方法 | HTTP | URL | perms | 说明 |
|---|---|---|---|---|
| 我的已发 | GET | `/ownList` | `process:ownList` | 分页 |
| 我的已发列表 | GET | `/getOwnProcessList` | `process:getOwnProcessList` | 全量 |
| 我的待办 | GET | `/todoList` | `process:todoList` | 分页 |
| 我的待办全量 | GET | `/getTodoModel` | `process:getTodoModel` | |
| 待签收 | GET | `/claimList` | `process:claimList` | 分页 |
| 我的已办 | GET | `/finishedList` | `process:finishedList` | |
| 已办全量 | GET | `/selectFinishedProcessList` | `process:selectFinishedProcessList` | |
| 抄送列表 | GET | `/copyList` | `process:copyList` | |
| 抄送全量 | GET | `/getCopyProcessList` | `process:getCopyProcessList` | |
| 标记已读 | PUT | `/readCopy` | `process:readCopy` | |
| 导出已发 | POST | `/ownExport` | `process:ownExport` | |
| 导出待办 | POST | `/todoExport` | `process:todoExport` | |
| 导出待签 | POST | `/claimExport` | `process:claimExport` | |
| 导出已办 | POST | `/finishedExport` | `process:finishedExport` | |
| 导出抄送 | POST | `/copyExport` | `process:copyExport` | |
| 流程表单 | GET | `/getProcessForm` | `process:getProcessForm` | 返回 vform3 JSON |
| 流程 BPMN | GET | `/bpmnXml` | `process:bpmnXml` | 返回 XML |
| 流程详情 | GET | `/detail` | `process:detail` | 包含流转 + 表单数据 |
| 流程 KEY | GET | `/getProcessModelKey` | `process:getProcessModelKey` | |
| 流程是否结束 | GET | `/checkFlowIsEnd` | `process:checkFlowIsEnd` | |
| 全部实例 | GET | `/selectHisProcInsProcess` | `process:selectHisProcInsProcess` | admin |
| 业务拓展 | GET | `/getProcInsIdToExtends` | `process:getProcInsIdToExtends` | |
| 历史节点 | GET | `/historyProcNodeList` | `process:historyProcNodeList` | |
| 流程表单列表 | GET | `/processFormList` | `process:processFormList` | |
| 流程图 | GET | `/getFlowViewer` | `process:getFlowViewer` | |
| 当前任务 | GET | `/getProcInsIdToTask` | `process:getProcInsIdToTask` | |
| 当前节点详情 | GET | `/getCurrentTaskInfo` | `process:getCurrentTaskInfo` | |
| 全部流程 | GET | `/selectProcessPrcInstance` | `process:selectProcessPrcInstance` | |
| 全部任务 | GET | `/selectProcessAllTask` | `process:selectProcessAllTask` | |
| 用户相关任务 | GET | `/selectProcessTaskByUser` | `process:selectProcessTaskByUser` | |
| 待办+抄送合并 | GET | `/getTaskTodoAndCopy` | `process:getTaskTodoAndCopy` | |
| 任务变量 | GET | `/getTaskVariables` | `process:getTaskVariables` | |
| 流程变量 | GET | `/getProcessVariables` | `process:getProcessVariables` | |
| 流程附件 | GET | `/getFilesByProcessInstanceId` | `process:getFilesByProcessInstanceId` | |
| 流程流转记录 | GET | `/getProcessCommentInfo` | `process:getProcessCommentInfo` | |

### 3.2 WfTaskController `/workflow/task`

约 30 个接口,核心:

| 方法 | perms | 说明 |
|---|---|---|
| 同意 / 完成 | `process:complete` | 核心审批动作 |
| 驳回 | `process:reject` | 驳回到上一节点/发起人 |
| 退回 | `process:return` | 任意节点退回 |
| 转办 | `process:transfer` | |
| 委派 | `process:delegate` | |
| 前加签 | `process:beforeAddSignTask` | |
| 终止 | `process:cancel` | |
| 撤回 | `process:revokeProcess` | 发起人撤回 |
| 签收 | (默认无 perms) | claim 任务 |
| 流程图 | (无 perms) | 渲染 BPMN XML |
| 委派列表 | `process:getDelegateList` | |
| 前加签列表 | `process:getBeforeAddSignTaskList` | |
| 转办人候选 | `process:getTransferableUserList` | |
| 节点候选人 | `process:getTaskAssigneeList` | |
| 任务操作日志 | `process:getHistoryTaskRun` | |

### 3.3 WfModelController `/workflow/model`

| 方法 | perms |
|---|---|
| 列表 | `model:list` |
| 列表(可发起) | `model:listModelLaunch` |
| 历史 | `model:historyList` |
| 详情 | `model:getInfo` |
| BPMN XML | `model:bpmnXml` |
| 新增 | `model:add` |
| 修改 | `model:edit` |
| 保存(模型) | `model:save` |
| 最新版本 | `model:latest` |
| 删除 | `model:remove` |
| 部署 | `model:deploy` |
| 导出 | `model:export` |
| 发布/启动 | `model:publishModel` |
| 挂起 | `model:suspended` |
| 激活 | `model:activate` |
| 分类下已部署 | `model:getCategoryToModel` |

### 3.4 WfFormController `/workflow/form`

| 方法 | perms |
|---|---|
| 列表 | `form:list` |
| 详情 | `form:getInfo` |
| 按编码查 | `form:getFormByCode` |
| 保存 | `form:saveForm` |
| 删除 | `form:delForm` |
| 控件列表 | `form:getVFormWidgetList` |

### 3.5 WfFormCategoryController `/system/category`

> 注意:路径是 `/system/category` (非 `/workflow/...`)

| 方法 | perms |
|---|---|
| 列表 | `systemCategory:list` |
| 新增 | `systemCategory:add` |
| 修改 | `systemCategory:edit` |
| 删除 | `systemCategory:remove` |

### 3.6 WfFormTemplateController `/workflow/formTemplate`

| 方法 | perms |
|---|---|
| 列表 | `formTemplate:list` |
| 全量 | `formTemplate:allList` |
| 详情 | `formTemplate:getInfo` |
| 保存 | `formTemplate:saveForm` |
| 删除 | `formTemplate:delFormTemplate` |

### 3.7 WfCategoryController `/workflow/category`

| 方法 | perms |
|---|---|
| 列表 | `category:list` |
| 列表无分页 | `category:listWithoutPage` |
| 全量 | `category:list` |
| 详情 | `category:query` |
| 新增 | `category:add` |
| 修改 | `category:edit` |
| 删除 | `category:remove` |
| 选项 | `category:getCategoryOption` |

### 3.8 WfExpressionController `/workflow/expression`

| 方法 | perms |
|---|---|
| 列表 | `expression:list` |
| 导出 | `expression:export` |
| (新增/修改/删除 同 sb2) | `expression:add/edit/remove` |

### 3.9 WfBoundaryEventController `/workflow/boundary`

| 方法 | perms |
|---|---|
| 触发 signal | `boundary:trigger` |
| 触发 message | `boundary:trigger` |
| 触发 error | `boundary:trigger` |
| 触发 cancel | `boundary:trigger` |
| 列表查询 | `boundary:query` |

### 3.10 WfProcessMigrateController `/workflow/process/migrate`

| 方法 | perms |
|---|---|
| 迁移 | `process:migrate` |
| 校验 | `process:migrate` |
| 节点列表 | `process:migrate` |
| 诊断 | `process:migrate` |
| 状态 | `process:migrate` |

### 3.11 WfProcessClearController `/workflow/process/clear`

| 方法 | perms |
|---|---|
| 预览 | `process:clearAll` |
| 执行 | `process:clearAll` |

### 3.12 WfTaskFormController `/workflow/taskForm`

任务-表单关联 CRUD,perms 与 form 类似。

### 3.13 WfDeployController `/workflow/deploy`

部署历史查询与删除,继承 sb2 接口。

---

## 4. Service 层设计

### 4.1 Base 三件套 + @Lazy

```java
// WfModelInfoBase
public abstract class WfModelInfoBase<SELF extends IService<T>, T> {
    @Lazy
    @Resource
    protected WfProcessInfoService processInfoService;

    @Lazy
    @Resource
    protected WfProcessTaskService processTaskService;

    @Lazy
    @Resource
    protected WfFormService formService;
}

// WfProcessInfoBase 类似
// WfProcessTaskBase 类似
```

**3 个 base 互相 @Resource 注入**——`@Lazy` 让 spring 在第一次调用时才创建代理,避免启动期循环依赖。

### 4.2 WfProcessServiceImpl 核心方法

```java
@Service
public class WfProcessServiceImpl extends WfProcessInfoBase<WfProcessServiceImpl, WfProcessVo> 
    implements IWfProcessService {

    public TableDataInfo<WfTaskVo> selectPageTodoProcessList(WfProcessQuery query, PageQuery page) {
        // 1. 查 act_ru_task 当前用户的 assignee 或候选组
        // 2. 联 act_ru_execution + act_re_procdef
        // 3. 转 WfTaskVo(含 permissionJson 字符串)
        // 4. 包装 TableDataInfo 返回
    }

    public AjaxResult completeTask(WfTaskCompleteDto dto) {
        // 1. @PreAuthorize 校验 perms
        // 2. 校验任务是否属于当前用户
        // 3. 调 WfFlowableTaskService.complete(taskId, dto.variables, dto.comment)
        // 4. 触发 WfTaskCompletedHandler
        // 5. 写 wf_comment_info + 附件关联
    }
}
```

### 4.3 WfFlowableTaskService

```java
@Service
public class WfFlowableTaskServiceImpl implements IWfFlowableTaskService {
    public void complete(String taskId, Map<String, Object> variables, String comment) {
        // 1. taskService.addComment(taskId, procInsId, comment, type)
        // 2. taskService.complete(taskId, variables, true)
        // 3. 返回新产生的 nextTask
    }

    public void delegate(String taskId, String userId) {
        taskService.delegateTask(taskId, userId);
    }
    
    public void transfer(String taskId, String userId) {
        taskService.setAssignee(taskId, userId);
    }
}
```

---

## 5. Handler 设计

### 5.1 WfTaskCreatedHandler

```java
@Component
public class WfTaskCreatedHandler implements TaskCreatedEventHandler {
    public void onEvent(FlowableTaskCreatedEvent event) {
        Task task = event.getEntity();
        // 1. 写 wf_comment_info(type='task_created', 触发人=null)
        // 2. 发送通知(邮件/IM,后续可接)
        // 3. 更新 tbl_flow_extend_procinst 当前节点
    }
}
```

注册:`FlowGlobalHandlerConfig.addEventListener`

### 5.2 WfTaskCompletedHandler

```java
public void onEvent(FlowableTaskCompletedEvent event) {
    // 1. 写 wf_comment_info(type='approve/reject/...')
    // 2. 处理附件 transfer
}
```

### 5.3 WfProcessStartedHandler

```java
public void onEvent(FlowableProcessStartedEvent event) {
    // 1. 写 tbl_flow_extend_procinst
    // 2. 写 wf_comment_info(type='start')
}
```

### 5.4 WfProcessCompletedHandler

```java
public void onEvent(FlowableProcessCompletedEvent event) {
    // 1. 更新 tbl_flow_extend_procinst status='completed', end_time
    // 2. 写 wf_comment_info(type='process_end')
}
```

### 5.5 WfBoundaryEventTrigger

```java
@Service
public class WfBoundaryEventTrigger {
    public boolean triggerSignal(TriggerBo bo) {
        runtimeService.signalEventReceived(bo.getSignalName(), bo.getExecutionId());
    }
    public boolean triggerMessage(...) {}
    public boolean triggerError(...) {}
    public boolean triggerCancel(...) {
        // cancel 与 delete 区别:cancel 走 runtimeService,delete 走历史服务
    }
}
```

---

## 6. Config 设计

### 6.1 FlowBpmnConfig

```java
@Configuration
public class FlowBpmnConfig {
    @Bean
    public BpmnJsonConverter bpmnJsonConverter() {
        // 自定义 Jackson 2 (不是 Jackson 3) 的 ObjectMapper
        return new BpmnJsonConverter();
    }
}
```

### 6.2 FlowGlobalHandlerConfig

```java
@Configuration
public class FlowGlobalHandlerConfig {
    @Bean
    public FlowableEventListener taskCreatedListener(WfTaskCreatedHandler handler) {
        return new FlowableEventListener(handler);
    }
    // ... 其他 4 个 listener
}
```

### 6.3 FlowableIdmConfig

```java
@Configuration
@ConditionalOnProperty(name = "flowable.idm.enabled", havingValue = "false", matchIfMissing = true)
public class FlowableIdmConfig {
    // 关掉 Flowable 自带 user/group,统一走 RuoYi RBAC
}
```

---

## 7. Mapper 设计(MyBatis)

10 个 mapper,example:

```java
public interface WfCategoryMapper extends BaseMapperPlus<WfCategory, WfCategoryVo> {
    default List<WfCategoryVo> selectByParentId(Long parentId) {
        return selectList(new LambdaQueryWrapper<WfCategory>()
            .eq(WfCategory::getParentId, parentId)
            .orderByAsc(WfCategory::getOrderNum));
    }
}
```

`BaseMapperPlus` 是父分支 basic-common 的扩展,封装了 LambdaQueryWrapper 通用方法。

---

## 8. Domain 设计(10 张表 DTO)

DTO 与 VO 分离(经典 DDD 风格):
- `WfCategory` 实体 → `WfCategoryBo` 业务对象 → `WfCategoryVo` 视图对象

例:
```java
@Data
public class WfCategoryBo extends BaseEntity {
    @NotNull(groups = {AddGroup.class, EditGroup.class})
    private Long categoryId;
    private String categoryName;
    private Long parentId;
    // ...
}
```

---

## 9. Builder / CustomCache / Cmd

### 9.1 WfBpmnModelBuilder

BPMN 2.0 模型构建器,封装 bpmn-js JSON ↔ Flowable BpmnModel 转换:

```java
public class WfBpmnModelBuilder {
    public BpmnModel fromJson(String bpmnJson) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(bpmnJson, BpmnModel.class);
    }
    public String toXml(BpmnModel model) {
        return new BpmnXMLConverter().convertToXML(model);
    }
}
```

### 9.2 WfMybatisCache

自定义 MyBatis 缓存(可关闭,默认用系统缓存)。

### 9.3 WfFlowableCommand

自定义 Flowable Command,用于流程迁移:
```java
public class WfFlowableCommand implements Command<Void> {
    private MigrationPlan plan;
    public Void execute(CommandContext ctx) {
        // 自定义迁移逻辑
    }
}
```

---

## 10. web-ui 关键文件

### 10.1 API 客户端(13 个)

`web-ui/src/api/workflow/*.js`,每个 export 6-8 个 request 函数。例如 `form.js`:
```js
export function listForm(query) { return request({ url: '/workflow/form/list', method: 'get', params: query }) }
export function getForm(formId) { return request({ url: '/workflow/form/' + formId, method: 'get' }) }
export function getFormByCode(code) { return request({ url: '/workflow/form/getFormByCode/' + code, method: 'get' }) }
export function saveForm(data) { return request({ url: '/workflow/form/saveForm', method: 'post', data }) }
export function delForm(formIds) { return request({ url: '/workflow/form/delForm/' + formIds, method: 'delete' }) }
export function getVFormWidgetList() { return request({ url: '/workflow/form/getVFormWidgetList', method: 'get' }) }
```

### 10.2 视图(22 个)

| 路径 | 用途 |
|---|---|
| `views/basic-extend/workflow/category/index.vue` | 流程分类树表 |
| `views/basic-extend/workflow/category/dataForm.vue` | 分类编辑弹窗(隐藏路由) |
| `views/basic-extend/workflow/center/index.vue` | 流程中心 dashboard |
| `views/basic-extend/workflow/expression/index.vue` | 表达式管理 |
| `views/basic-extend/workflow/form/index.vue` | 表单列表 |
| `views/basic-extend/workflow/form/design.vue` | vform3 设计器 |
| `views/basic-extend/workflow/formCategory/index.vue` | 表单分类 |
| `views/basic-extend/workflow/formTemplate/index.vue` | 表单模板 |
| `views/basic-extend/workflow/migrate/index.vue` | 流程迁移 |
| `views/basic-extend/workflow/model/index.vue` | 流程模型列表 |
| `views/basic-extend/workflow/model/design.vue` | bpmn-js 设计器 |
| `views/basic-extend/workflow/process/start.vue` | 流程发起 |
| `views/basic-extend/workflow/process/detail.vue` | 流程详情 |
| `views/basic-extend/workflow/process/components/ApprovalTimeline.vue` | 审批时间线 |
| `views/basic-extend/workflow/process/components/ProcessViewer.vue` | 流程图渲染 |
| `views/basic-extend/workflow/process/components/TaskDialog.vue` | 审批弹窗 |
| `views/basic-extend/workflow/process/components/actionMeta.js` | 8 项动作元数据 |
| `views/basic-extend/workflow/task/todo.vue` | 我的待办 |
| `views/basic-extend/workflow/task/finished.vue` | 我的已办 |
| `views/basic-extend/workflow/task/own.vue` | 我的已发 |
| `views/basic-extend/workflow/task/copy.vue` | 抄送我的 |
| `views/basic-extend/workflow/task/claim.vue` | 待签收 |

### 10.3 公共组件(4 个)

- `FormRender/index.vue` vform3 渲染器封装(异步校验)
- `ProcessUser/index.vue` 选人(单/多)
- `ProcessRole/index.vue` 选角色
- `ProcessStandUser/index.vue` 候选人(指定/多级/发起人/发起部门)

### 10.4 bpmn-js 设计器(51 文件)

`plugins/package/` 下:
- `designer/ProcessDesigner.vue` 主设计器(486 行)
- `designer/plugins/content-pad/` 自定义 ContextPad(把 Append task 改为 UserTask)
- `designer/plugins/extension-moddle/{activiti,camunda,flowable}/` BPMN 扩展
- `designer/plugins/translate/{customTranslate,zh}.js` 中文翻译
- `designer/plugins/palette/paletteProvider.js` 调色板
- `designer/plugins/descriptor/*.json` 三引擎元数据(activiti / camunda / flowable)
- `penal/PropertiesPanel.vue` 属性面板
- `penal/task/ElementTask.vue` + `task-components/{UserTask,ReceiveTask,ScriptTask}.vue`
- `penal/boundary/index.vue` + `boundary-components/{Time,Error,Message,Signal,Escalation,Conditional,Cancel,Compensation}Boundary.vue`
- `penal/{flow-condition,form,listeners,multi-instance,other,properties,signal-message}/`
- `theme/*.scss` 4 个 scss 主题
- `utils.js` + `highlight/index.js`

### 10.5 Router hidden 路由(6 个)

`web-ui/src/router/index.js` `constantRoutes` 末尾 hardcode 6 个 hidden 路由,绕过 dynamicRoutes 的 permission 过滤,所有登录用户可访问:

```js
{ path: '/workflow/category/dataForm', hidden: true, ... }
{ path: '/workflow/model/design', hidden: true, ... }
{ path: '/workflow/process/start', hidden: true, ... }
{ path: '/workflow/migrate', hidden: true, ... }
{ path: '/workflow/process/detail', hidden: true, ... }
{ path: '/workflow/form/design', hidden: true, ... }
```

### 10.6 vform3 全局注册(main.js)

```js
import VForm3 from 'vform3-builds'
import 'vform3-builds/dist/designer.style.css'
// ...
app.use(VForm3)  // 注册 v-form-designer / v-form-render
```

Vite 配置:`optimizeDeps.include: [..., 'vform3-builds']` 避免 UMD 转换失败。

---

## 11. SQL 文件

| 文件 | 用途 | 状态 |
|---|---|---|
| `file-sql/basicdb-vue3.sql` | 完整库 dump(含 2000-2099 流程菜单 38 条 + wf_* / flw_* / act_* 80+ 表) | **新导出,直接用** |
| `file-sql/reset-admin-password.sql` | 重置 admin 密码 | 保留 |
| `file-sql/reset-all-passwords.sql` | 重置所有用户密码 | 保留 |

**已废弃**:
- ❌ basicdb-vue3-flowable7.sql(已被新导出覆盖)
- ❌ basicdb-vue3-flowable7-menu.sql(同上)
- ❌ basicdb-vue3-flowable7-menu-v2.sql(同上)
- ❌ basicdb-vue3-flowable7-menu-cleanup.sql(同上)

---

## 12. 启动顺序

1. **数据库**: 跑 `psql -f file-sql/basicdb-vue3.sql`(首次)/ 启动 basic-admin-api(`database-schema-update: true` 自动增量)
2. **后端**: `mvn -DskipTests package` 9 模块全绿 → `java -jar basic-admin-api/target/basic-admin-api.jar`
3. **前端**: `cd web-ui && pnpm install && pnpm dev`
4. **登录**: admin / 123456(默认;非默认用 reset-admin-password.sql 改)

---

## 13. 已知问题

| 问题 | 现象 | 临时方案 |
|---|---|---|
| bpmn-js properties-panel 中文翻译偶发空白 | bpmnjs.io/bpmn-js-properties-panel 中文 | 强制 zh-CN |
| vform3 设计器与 bpmn-js 设计器 CSS 冲突 | 类名 `.el-container` | 双方都用 scoped 隔离 |
| sys_files 加字段后老数据 url 为 null | 历史附件 url 字段空 | 加默认值 url='/default.png' |
| TaskListener 触发 webhook 无重试 | 通知丢失 | 后续接 MQ |

