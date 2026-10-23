# 基础平台(base-postgresql-vue3) 详细设计(LLD) — `springboot2-extend-flowable` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot2-extend-flowable`** 分支
> 前置依赖:《架构方案》v0.1、《需求文档》v0.1、《概要设计》v0.1
> 本文档细化到「类 / 方法 / 关键逻辑」,开发可直接编码,测试据此写用例。
> 同主题文档:`docs/流程引擎使用说明.md`(使用手册,本文件是集成层面)

---

## 1. 总体包结构(本分支新增 `com.basic.flowable.*` + `com.basic.workflow.*`)

```
com.basic
├── BasicApplication / BasicServletInitializer
├── web
│   ├── config/SwaggerConfig
│   └── controller (master 已有的 26 个 + 沿用)
├── framework (沿用下游基线 master,不动)
├── common (沿用下游基线 master,不动)
├── quartz (extend-quartz,沿用)
├── generator (extend-generator,沿用)
├── system (basic-admin-core,沿用)
├── flowable 🆕(本分支新增)
│   ├── common
│   │   ├── constant/{ProcessConstants, TaskConstants}
│   │   └── enums/{BoundaryEventTypeEnum, FlowComment, FormType, ProcessStatus}
│   ├── config/{FlowBpmnConfig, FlowExtensionConfig, GlobalEventListenerConfig, modeler/{DatabaseConfiguration, ModelerBeanConfig}}
│   ├── core/{FormConf, domain/ProcessQuery}
│   ├── factory/{ActivitiEngineUtil, FlowServiceFactory}
│   ├── flow/{CustomProcessDiagramCanvas, CustomProcessDiagramGenerator, FindNextNodeUtil, FlowableConfig, FlowableUtils}
│   ├── listener/{GlobalEventListener, ProcessDueTimeListener, UserTaskListener}
│   ├── model/FlowableModelRepositoryImpl
│   ├── tool/{ReturnCode, ReturnVo}
│   └── utils/{ExtansionPropertiesUtil, ModelUtils, ProcessFormUtils, ProcessUtils, TaskUtils, UUIDGenerator}
├── workflow 🆕(本分支新增)
│   ├── builder/DynamicUserTaskBuilder
│   ├── cmd
│   │   ├── AbstractDynamicInjectionCmd
│   │   ├── AddHisCommentCmd
│   │   ├── InjectUserTaskInProcessInstanceCmd
│   │   ├── ReplaceUserTaskInProcessInstanceCmd
│   │   └── rollBack (退回策略,7 文件)
│   ├── controller (13 个)
│   ├── customCache/CustomDeploymentCache
│   ├── domain (10+ 实体)
│   ├── domain/bo (8+ BO)
│   ├── domain/dto (6+ DTO)
│   ├── domain/vo (5+ VO)
│   ├── mapper (15+ XML)
│   ├── service (接口 + 23 个 Impl)
│   └── service/impl (见 §6)
├── constant (本分支新增,extend-flowable 内部)
│   ├── ExtensionConstant
│   └── FlowConstant
├── converter 🆕
│   ├── BruceBoundaryEventJsonConverter
│   ├── BruceCallActivityJsonConverter
│   ├── BruceSubProcessJsonConverter
│   ├── BruceUserTaskJsonConverter
│   └── CustomBpmnJsonConverter
└── enm 🆕
    ├── CommentTypeEnum
    └── ProcessStatusEnum
```

> **包命名约定**:`com.basic.flowable` = 引擎层;`com.basic.workflow` = 业务层;`com.basic.constant` = 跨层常量;`com.basic.converter` = 序列化;`com.basic.enm` = 枚举(注意拼写 en**m**,与子包 `flowable.common.enums` 不同)。业务侧要扩展时,**优先在 `com.basic.workflow` 新增**,不要修改引擎层核心类。

---

## 2. 领域模型与枚举

### 2.1 关键实体(本分支新增 / 改造)

| 实体 | 类 | 主键策略 | 关键状态字段 |
|---|---|---|---|
| 流程模型 | `act_re_model` (Flowable 内部) | — | `version_` 递增 |
| 流程定义 | `act_re_procdef` (Flowable 内部) | — | `suspension_state_` 1=激活 2=挂起 |
| 流程实例扩展 | `ExtendProcinst` | MP `ASSIGN_ID` | `processStatus` 详见 §2.2 |
| 流程分类 | `WfCategory` | MP `ASSIGN_ID` | `status:0/1` |
| 表单定义 | `WfForm` | MP `ASSIGN_ID` | `formType` |
| 表单部署快照 | `WfDeployForm` | MP `ASSIGN_ID` | — |
| 节点填写 | `WfTaskForm` | MP `ASSIGN_ID` | — |
| 审批意见 | `WfCommentInfo` | MP `ASSIGN_ID` | `type` |
| 抄送 | `WfCopy` | MP `ASSIGN_ID` | `readFlag:0/1` |
| 表单分类 | `WfFormCategory` | MP `ASSIGN_ID` | — |
| 表单模板 | `WfFormTemplate` | MP `ASSIGN_ID` | — |
| 表达式字典 | `flw_expression` (Flowable 内部表) | — | — |
| 边界事件 | `WfBoundaryEventConfig` (内存/动态,不落表) | — | `type` + 6 类扩展属性 |
| BO 集合 | `WfTaskBo` / `WfModelBo` / `WfFormBo` / `WfCopyBo` / `WfBoundaryTriggerBo` / `WfDesignerBo` / `WfProcessMigrateBo` | — | 入参/出参 |
| DTO 集合 | `WfCommentDto` / `WfMetaInfoDto` / `WfUserTaskDto` | — | 内部传参 |
| VO 集合 | `CommentVo` / `FlowElementVo` / `StartProcessInstanceVo` | — | 返回值 |

### 2.2 状态枚举

```java
// 流程审批状态(写 processStatus 变量)
public enum ProcessStatus {
    RUNNING("running"),         // 进行中
    REJECT("reject"),           // 已终止(驳回)
    COMPLETED("completed"),     // 已完成
    RETURN("return"),           // 已退回
    CANCELED("canceled"),       // 已取消(撤回/废除)
    DELETE("delete");           // 删除
}

// 边界事件类型
public enum BoundaryEventTypeEnum {
    TIMER("timer"),
    ERROR("error"),
    SIGNAL("signal"),
    MESSAGE("message");
}

// 节点「授权功能」(10 项,2025-Q4 改)
public static final String[] ACTION_PERMISSIONS = {
    "complete", "reject", "return", "transfer", "delegate",
    "copy", "mulitiSequentialUnComplete",
    "addSignBefore", "addSignAfter", "stop"
};

// 退回策略 6 种(impl 类名见 §6.7)
```

### 2.3 状态合法流转

| 对象 | 状态 | 合法转换 |
|---|---|---|
| 流程实例 | `running` ↔ (`reject` / `return` / `completed` / `canceled`) / `delete` | 见《需求文档》§6.4 状态变量表 |
| 流程定义 | `1=激活` ↔ `2=挂起` | `act_re_procdef.SUSPENSION_STATE_` |
| 任务 | 创建 / 认领(claim) / 完成(complete) / 退回(return) / 委派(delegate) / 加签(addSign) / 终止(stop) | 引擎自动 |
| 节点「授权功能」 | 10 个值 | 节点 `flowable:IdmPermission` JSON 数组(可子集)|
| 边界事件触发 | `running` 期间触发 → 状态 `boundary_<type>` | `BOUNDARY_TRIGGER_STATUS_PREFIX` |
| 抄送已读 | `0=未读` → `1=已读` | 用户点开即改 |

---

## 3. 框架机制

### 3.1 启动与包扫描

```java
// BasicApplication(沿用下游基线 master,不变)
@SpringBootApplication(
    scanBasePackages = {"com.basic", "com.core"},
    exclude = { DataSourceAutoConfiguration.class }
)
@MapperScan({"com.core.**.mapper"})
public class BasicApplication { ... }
```

- `extend-flowable` 的 `mapperLocations` 在 `application.yml` 追加:
  ```yaml
  mybatis-plus:
    mapperLocations: classpath*:mapper/**/*Mapper.xml,classpath*:mapper/*Mapper.xml,
                    classpath*:/META-INF/flow-mybatis-mappings/*/xml/*.xml,
                    classpath*:/META-INF/modeler-mybatis-mappings/*.xml
  ```

### 3.2 Flowable 引擎装配

```java
// FlowBpmnConfig(extend-flowable 内部)
@Configuration
public class FlowBpmnConfig implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {
    @Process @Bean public TaskExecutor processTaskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }
    @Bean public ProcessValidatorFactory processValidator() { return new ProcessValidatorFactory(); }
    @Bean public ObjectMapper objectMapper() { return new ObjectMapper(); }
    @Bean public TypeConverter typeConverter() { return new TypeConverterImpl(); }
    @Bean public BpmnXMLConverter createBpmnXMLConverter() { return new BpmnXMLConverter(); }
    @Bean public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() { ... }
    @Override public void configure(SpringProcessEngineConfiguration cfg) { /* 留空 */ }
}
```

- `FlowExtensionConfig` 注册 `CustomBpmnJsonConverter` Bean
- `GlobalEventListenerConfig` 注册全局事件监听

### 3.3 事务边界

- **Flowable Command 事务**:`RollbackCmd` / `AddHisCommentCmd` / `InjectUserTaskInProcessInstanceCmd` / `ReplaceUserTaskInProcessInstanceCmd` 全部继承 `Command<T>`,引擎在 `ProcessEngineConfiguration.txManager` 管控下执行
- **业务事务**:`Wf*ServiceImpl` 上 `@Transactional(rollbackFor = Exception.class)`
- **跨表写入**:`WfModelServiceImpl.save` 内部 BPMN 校验 + `act_re_model` + `act_de_model` 写,在同一事务;失败回滚全部
- **审批意见双写**:`AddHisCommentCmd.execute` 内先写 `wf_comment_info` 再 `taskService.addComment`(写 `act_hi_comment`);二者均在 ProcessEngine 事务内,一致

### 3.4 BPMN 序列化

```java
// CustomBpmnJsonConverter
public class CustomBpmnJsonConverter extends BpmnJsonConverter {
    public CustomBpmnJsonConverter() {
        // 注册自定义 Jackson Module
        objectMapper.registerModule(...);
    }
}

// 4 个 Bruce* 转换器
@Component
public class BruceUserTaskJsonConverter extends UserTaskJsonConverter { ... }  // 认识 flowable:Idm*
@Component public class BruceSubProcessJsonConverter extends SubProcessJsonConverter { ... }
@Component public class BruceCallActivityJsonConverter extends CallActivityJsonConverter { ... }
@Component public class BruceBoundaryEventJsonConverter extends BoundaryEventJsonConverter { ... }  // 认识 boundary* 扩展
```

### 3.5 自定义流程图

```java
// CustomProcessDiagramGenerator(基于 Activiti 6.0.0 BpmnLayout)
public class CustomProcessDiagramGenerator {
    public InputStream generateDiagram(BpmnModel bpmnModel, String procInsId, List<String> activityIds,
                                        List<String> completedActivityIds, List<String> rejectedActivityIds) {
        // 1. Activiti BpmnLayout 自动排版
        // 2. CustomProcessDiagramCanvas 画已办(绿)/待办(蓝)/驳回(红)/跳过(灰)
        return canvas.generateImage();
    }
}

// CustomProcessDiagramCanvas
public class CustomProcessDiagramCanvas extends DefaultProcessDiagramCanvas {
    @Override
    protected void drawActivity(...) {
        if (completedActivityIds.contains(activity.getId())) setColor(GREEN);
        else if (rejectedActivityIds.contains(activity.getId())) setColor(RED);
        else if (activeActivityIds.contains(activity.getId())) setColor(BLUE);
        else setColor(GRAY);  // 跳过
        super.drawActivity(...);
    }
}
```

### 3.6 退回策略工厂

```java
// DefaultRollbackStrategyFactoryBean + RollbackStrategyFactory
@Component
public class DefaultRollbackStrategyFactoryBean {
    @Bean public RollbackStrategyFactory rollbackStrategyFactory() {
        RollbackStrategyFactory f = new RollbackStrategyFactory();
        f.register(UserTask, new DefaultTaskNextGatewayRollbackOperateStrategy());
        f.register(MultiInstance_Active, new ActiveMultiInstanceTaskRollbackOperateStrategy());
        f.register(MultiInstance_Completed_Next_Task, new CompletedMultiInstanceTaskAndNextDefaultTaskRollbackOperateStrategy());
        f.register(MultiInstance_Completed_Next_Gateway, new CompletedMultiInstanceTaskAndNextDefaultGateWayRollbackOperateStrategy());
        f.register(CallActivity, new NextCallActivityRollbackOperateStrategy());
        f.register(SubProcess, new NextSubProcessRollbackOperateStrategy());
        return f;
    }
}
```

### 3.7 自定义部署缓存

```java
// CustomDeploymentCache(实现 DeploymentCache)
public class CustomDeploymentCache<T> implements DeploymentCache<T> {
    private final Map<String, T> cache = new ConcurrentHashMap<>();
    @Override public T get(String id) { return cache.get(id); }
    @Override public void add(String id, T obj) { cache.put(id, obj); }
    @Override public void remove(String id) { cache.remove(id); }
    @Override public void clear() { cache.clear(); }
}
```

### 3.8 引擎事件监听

```java
// GlobalEventListener
@Component
public class GlobalEventListener implements FlowableEventListener {
    @Override public void onEvent(FlowableEvent event) {
        switch (event.getType()) {
            case PROCESS_STARTED: handleStart((ProcessStartedEvent) event); break;
            case PROCESS_COMPLETED: handleComplete((ProcessCompletedEvent) event); break;
            case TASK_CREATED: handleTaskCreate((FlowableTaskCreatedEvent) event); break;
            case TASK_COMPLETED: handleTaskComplete((FlowableTaskCompletedEvent) event); break;
            case PROCESS_CANCELLED: handleCancel((ProcessCancelledEvent) event); break;
        }
    }
    private void handleComplete(FlowableTaskCompletedEvent e) {
        // 写 processStatus='running' 或 'completed' 取决于是否最终节点
    }
}
```

### 3.9 UserTaskListener(节点创建时)

```java
// UserTaskListener
@Component
public class UserTaskListener implements TaskListener {
    @Override public void notify(DelegateTask task) {
        // 1. 解析 flowable:IdmAssignee / IdmCandidateUsers / IdmCandidateGroups
        // 2. IsSkip / IsNullSkip / IsExamined / IsRoleReplace
        // 3. IsUserScreen / IsOrgScreen / IsDeptOrOrg
        // 4. IsRevoke
    }
}
```

---

## 4. 核心引擎详细设计

### 4.1 建模 → 部署引擎

**WfModelServiceImpl.save**

```java
@Override
@Transactional(rollbackFor = Exception.class)
public void save(WfModelBo bo) {
    // 1. 校验 BPMN 合法
    String bpmnXml = CustomBpmnJsonConverter.convertToXml(bo.getBpmnJson());
    BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(
        new InputStreamSource(new ByteArrayInputStream(bpmnXml.getBytes())), false, false);
    ProcessValidator validator = ProcessValidatorFactory.createDefaultProcessValidator();
    List<ValidationError> errors = validator.validate(bpmnModel);
    if (!errors.isEmpty()) throw new ServiceException("BPMN 校验失败:" + errors);

    // 2. act_re_model 写/更新
    Model model = repositoryService.getModel(bo.getModelId());
    if (model == null) {
        model = repositoryService.newModel();
        model.setKey(bo.getModelKey());
        model.setName(bo.getModelName());
        repositoryService.saveModel(model);
    }
    repositoryService.addModelEditorSource(model.getId(), bo.getBpmnJson().getBytes(StandardCharsets.UTF_8));

    // 3. act_de_model 写(供 designER 用)
    actDeModelMapper.insert(toActDeModel(model, bo));

    // 4. saveModel 的 BpmnModel 给后续部署用
}
```

**WfModelServiceImpl.deploy**

```java
@Override
@Transactional
public String deploy(String modelId) {
    Model model = repositoryService.getModel(modelId);
    byte[] bpmnJsonBytes = repositoryService.getModelEditorSource(modelId);
    BpmnModel bpmnModel = CustomBpmnJsonConverter.convertToBpmnModel(new String(bpmnJsonBytes));

    // 校验
    List<ValidationError> errors = ProcessValidatorFactory.createDefaultProcessValidator().validate(bpmnModel);
    if (!errors.isEmpty()) throw new ServiceException("部署失败:" + errors);

    Deployment deployment = repositoryService.createDeployment()
        .name(model.getName())
        .key(model.getKey())
        .addBpmnModel(model.getKey() + ProcessConstants.SUFFIX, bpmnModel)
        .deploy();

    // 拷 wf_deploy_form 快照
    List<WfDeployForm> deployForms = wfDeployFormService.listByModelId(modelId);
    for (WfDeployForm f : deployForms) {
        f.setDeployId(deployment.getId());
        wfDeployFormMapper.updateById(f);
    }

    return deployment.getId();
}
```

### 4.2 发起引擎

**WfProcessServiceImpl.startProcess**

```java
@Override
@Transactional
public String startProcess(String procDefKey, Map<String, Object> variables, JSONObject formData) {
    // 1. 流程定义必须激活
    ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
        .processDefinitionKey(procDefKey).latestVersion().active().singleResult();
    if (pd == null) throw new ServiceException("流程定义未部署或未激活");

    // 2. 写 tbl_flow_extend_procinst 扩展
    ExtendProcinst ext = new ExtendProcinst();
    ext.setProcDefKey(procDefKey);
    ext.setProcDefName(pd.getName());
    ext.setStartUserId(SecurityUtils.getUserId());
    ext.setStartUserName(SecurityUtils.getUsername());
    ext.setProcessStatus(ProcessStatus.RUNNING.getStatus());
    extendProcinstMapper.insert(ext);

    // 3. 启动
    Authentication.setAuthenticatedUserId(SecurityUtils.getUsername());
    ProcessInstance pi = runtimeService.startProcessInstanceByKey(procDefKey, ext.getId(), variables);

    // 4. ⚠️ 脚手架:保存表单数据到 wf_task_form
    WfTaskForm wfTaskForm = new WfTaskForm();
    wfTaskForm.setProcInsId(pi.getId());
    wfTaskForm.setUserId(SecurityUtils.getUserId());
    wfTaskForm.setFormContent(formData);
    wfTaskFormMapper.insert(wfTaskForm);

    // 5. 同步 ext.proc_ins_id
    ext.setProcInsId(pi.getId());
    extendProcinstMapper.updateById(ext);

    return pi.getId();
}
```

### 4.3 审批引擎

**WfTaskServiceImpl.complete**

```java
@Override
@Transactional
public void complete(String taskId, String comment, Map<String, Object> variables) {
    // 1. 当前用户必须是 task 的 assignee
    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    if (task == null) throw new ServiceException("任务不存在");
    if (!task.getAssignee().equals(SecurityUtils.getUsername())) {
        // 若是 candidate → 需先 claim
        throw new ServiceException("无权操作,请先签收");
    }

    // 2. 写审批意见(双写)
    AddHisCommentCmd cmd = new AddHisCommentCmd(task, comment, CommentTypeEnum.NORMAL);
    managementService.executeCommand(cmd);

    // 3. 完成
    taskService.complete(taskId, variables);

    // 4. 抄送(若有 copyUserIds)
    if (variables != null && variables.containsKey("copyUserIds")) {
        List<Long> copyUserIds = (List<Long>) variables.get("copyUserIds");
        wfCopyService.add(task.getProcessInstanceId(), taskId, copyUserIds);
    }

    // 5. GlobalEventListener 自动写 processStatus
}
```

**AddHisCommentCmd**

```java
public class AddHisCommentCmd implements Command<Void> {
    private final Task task;
    private final String comment;
    private final CommentTypeEnum type;

    @Override
    public Void execute(CommandContext commandContext) {
        // 1. 写 wf_comment_info
        WfCommentInfo info = new WfCommentInfo();
        info.setProcInsId(task.getProcessInstanceId());
        info.setTaskId(task.getId());
        info.setUserId(SecurityUtils.getUserId());
        info.setType(type.name());
        info.setComment(comment);
        wfCommentInfoMapper.insert(info);

        // 2. 写 act_hi_comment
        taskService.addComment(task.getId(), task.getProcessInstanceId(), type.name(), comment);
        return null;
    }
}
```

### 4.4 退回引擎

**WfTaskServiceImpl.returnTask**

```java
@Override
@Transactional
public void returnTask(String taskId, String returnToActivityId, String comment) {
    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    if (task == null) throw new ServiceException("任务不存在");

    // 1. 选策略
    BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
    FlowNode targetNode = (FlowNode) bpmnModel.getFlowElement(returnToActivityId);
    if (targetNode == null) throw new ServiceException("目标节点不存在");

    RollbackParamsTemplate params = new RollbackParamsTemplate(task, targetNode, comment);
    RollbackOperateStrategy strategy = rollbackStrategyFactory.getStrategy(targetNode, bpmnModel);

    // 2. 执行(在 Command 内)
    managementService.executeCommand(new RollbackCmd(strategy, params));

    // 3. 写意见
    AddHisCommentCmd cmd = new AddHisCommentCmd(task, comment, CommentTypeEnum.RETURN);
    managementService.executeCommand(cmd);
}
```

**RollbackCmd**

```java
public class RollbackCmd implements Command<Void> {
    private final RollbackOperateStrategy strategy;
    private final RollbackParamsTemplate params;

    @Override
    public Void execute(CommandContext commandContext) {
        // 1. 当前 task 完成 + 写 processStatus='return'
        // 2. 策略.rollback(params)
        strategy.rollback(params, commandContext);
        // 3. RollbackEventListener 通知
        return null;
    }
}
```

**6 种策略**

| 策略 | 场景 | 关键逻辑 |
|---|---|---|
| `DefaultTaskNextGatewayRollbackOperateStrategy` | 默认 UserTask + Gateway | 关闭当前 task + 跳到目标节点 |
| `ActiveMultiInstanceTaskRollbackOperateStrategy` | 进行中的多实例 | 拆解 multiInstance → 单 UserTask |
| `CompletedMultiInstanceTaskAndNextDefaultTaskRollbackOperateStrategy` | 多实例已完成 + 后接 Task | 跳过多实例后跳目标 |
| `CompletedMultiInstanceTaskAndNextDefaultGateWayRollbackOperateStrategy` | 多实例已完成 + 后接 Gateway | 跳过 multiInstance + Gateway |
| `NextCallActivityRollbackOperateStrategy` | CallActivity | 改跳转路径 |
| `NextSubProcessRollbackOperateStrategy` | SubProcess | 退出子流程 + 跳目标 |

### 4.5 加签引擎(前/后)

**InjectUserTaskInProcessInstanceCmd(前加签)**

```java
public class InjectUserTaskInProcessInstanceCmd implements Command<Void> {
    private final String procInsId;
    private final String currentTaskId;
    private final List<String> userIds;
    private final String comment;

    @Override
    public Void execute(CommandContext ctx) {
        // 1. 取当前 task 的 execution
        Task task = TaskHelper.findTaskById(ctx, currentTaskId);
        ExecutionEntity execution = (ExecutionEntity) task.getExecution();

        // 2. 加载 BpmnModel
        BpmnModel bpmnModel = ProcessHelper.getBpmnModel(ctx, execution.getProcessDefinitionId());

        // 3. 构建新 UserTask
        FlowNode newNode = DynamicUserTaskBuilder.builder()
            .name(task.getName() + "·前加签")
            .id("dynamic_" + UUIDGenerator.next())
            .assignee(userIds.get(0))
            .inherit(task, bpmnModel)        // 继承 IdmPermission/IsSkip 等
            .build();

        // 4. 注入到当前节点之前
        DynamicInjectionHelper.injectBefore(ctx, execution, newNode);

        // 5. 写意见
        new AddHisCommentCmd(task, comment, CommentTypeEnum.BEFORE_ADD_SIGN).execute(ctx);
        return null;
    }
}
```

**AbstractDynamicInjectionCmd(基类)**

```java
public abstract class AbstractDynamicInjectionCmd implements Command<Void> {
    protected void injectBefore(CommandContext ctx, ExecutionEntity exec, FlowNode newNode) {
        // 1. 找到 exec 当前 FlowNode
        FlowNode currentNode = (FlowNode) exec.getBpmnModelElement();
        // 2. 改 sequenceFlow: currentNode.incoming → newNode
        // 3. newNode.outgoing → currentNode
        // 4. activate newNode
    }
}
```

### 4.6 抄送引擎

**WfCopyServiceImpl.add**

```java
@Override
@Transactional
public void add(String procInsId, String taskId, List<Long> userIds) {
    if (userIds == null || userIds.isEmpty()) return;
    List<WfCopy> copies = new ArrayList<>(userIds.size());
    for (Long uid : userIds) {
        WfCopy c = new WfCopy();
        c.setProcInsId(procInsId);
        c.setTaskId(taskId);
        c.setUserId(uid);
        c.setReadFlag("0");
        copies.add(c);
    }
    wfCopyMapper.insertBatch(copies);
    // 通知机制(本仓库不实现)
}
```

### 4.7 边界事件触发(动态)

**WfBoundaryEventServiceImpl.trigger**

```java
@Override
@Transactional
public void trigger(String activityId, String type, JSONObject config) {
    // 1. 校验 type 与 config
    BoundaryEventTypeEnum typeEnum = BoundaryEventTypeEnum.getByShortName(type);
    if (typeEnum == null) throw new ServiceException("未知 boundary 类型");

    // 2. 找到该 activityId 对应的 running execution
    List<Execution> executions = runtimeService.createExecutionQuery()
        .activityId(activityId).onlyChildExecutions().list();

    // 3. 触发(由 Flowable 引擎处理 timer/error/signal/message)
    // 这里只是 trigger 配置记录,不直接调用 engine
    // 真实触发由 boundary 自身完成
    // 业务侧可在此发通知

    // 4. 写状态变量
    runtimeService.setVariable(executions.get(0).getId(),
        ProcessConstants.PROCESS_STATUS_KEY,
        ProcessConstants.BOUNDARY_TRIGGER_STATUS_PREFIX + type);
}
```

### 4.8 动态表单/快照

**WfDeployFormServiceImpl.deployForm**

```java
@Override
@Transactional
public void deployForm(String modelId, Long formId) {
    WfForm form = wfFormMapper.selectById(formId);
    WfDeployForm snap = new WfDeployForm();
    snap.setFormId(formId);
    snap.setContent(form.getContent());   // 拷快照
    wfDeployFormMapper.insert(snap);
}
```

### 4.9 流程图生成

**WfProcessServiceImpl.getFlowViewer**

```java
@Override
public InputStream getFlowViewer(String procInsId) {
    // 1. 取流程实例 + 历史活动
    ProcessInstance pi = runtimeService.createProcessInstanceQuery()
        .processInstanceId(procInsId).singleResult();
    if (pi == null) pi = historyService.createHistoricProcessInstanceQuery()
        .processInstanceId(procInsId).singleResult();

    BpmnModel bpmnModel = repositoryService.getBpmnModel(pi.getProcessDefinitionId());

    // 2. 已办(complete) / 待办(active) / 驳回(reject)
    List<HistoricActivityInstance> his = historyService.createHistoricActivityInstanceQuery()
        .processInstanceId(procInsId).list();
    Set<String> completed = his.stream()
        .filter(h -> h.getEndTime() != null).map(HistoricActivityInstance::getActivityId).collect(toSet());
    Set<String> active = pi.isEnded() ? Set.of() : Collections.singleton(
        taskService.createTaskQuery().processInstanceId(procInsId).singleResult().getTaskDefinitionKey());

    // 3. 自定义生成器
    return customProcessDiagramGenerator.generateDiagram(
        bpmnModel, procInsId, new ArrayList<>(active), new ArrayList<>(completed), List.of());
}
```

---

## 5. 校验引擎

### 5.1 L1 字段级校验

- 入口:`@Validated` + `javax.validation.constraints.*`(沿用下游基线 master)
- BO/DTO 字段上加 `@NotBlank` / `@Size` / `@Pattern` 等
- 失败处理:由 `GlobalExceptionHandler` 包装

### 5.2 L2 跨业务校验

| 校验项 | 位置 | 失败动作 |
|---|---|---|
| BPMN XML 合法 | `WfModelServiceImpl.save` / `deploy` | 抛 `ServiceException` + 错误列表 |
| modelKey 全局唯一 | `WfModelServiceImpl.checkModelKeyUnique` | 抛 `ServiceException` |
| formCode 全局唯一 | `WfFormServiceImpl.checkFormCodeUnique` | 抛 `ServiceException` |
| categoryCode 唯一 | `WfCategoryServiceImpl.checkCategoryCodeUnique` | 抛 `ServiceException` |
| expressionKey 唯一 + 被引用不能删 | `WfExpressionServiceImpl` | 抛 `ServiceException` |
| 审批人必须是 task 的 assignee / candidate | `WfTaskServiceImpl.X` | 抛 `ServiceException` |
| 退回目标必须是历史经过 | `/workflow/task/returnList` 只返历史 | 列表里没的不能选 |
| 加签用户必须存在 | `WfTaskServiceImpl.beforeAddSignTask` | 抛 `ServiceException` |
| 流程迁移目标版本必须存在 | `WfProcessMigrateServiceImpl` | 抛 `ServiceException` |
| 流程清理:仅测试数据 | `WfProcessClearServiceImpl.clear`(双重确认)| 抛 `ServiceException` |

---

## 6. 业务模块实现(节选)

### 6.1 表单管理(WfFormServiceImpl)

```java
@Override
@Transactional
public void saveForm(WfFormBo bo) {
    WfForm form = BeanUtils.copy(bo, WfForm.class);
    checkFormCodeUnique(form);
    if (form.getId() == null) wfFormMapper.insert(form);
    else wfFormMapper.updateById(form);
}

@Override
public WfForm getFormByCode(String code) {
    return wfFormMapper.selectOne(new LambdaQueryWrapper<WfForm>().eq(WfForm::getFormCode, code));
}
```

### 6.2 流程分类(WfCategoryServiceImpl)

```java
@Override
public List<WfCategory> tree() {
    List<WfCategory> all = wfCategoryMapper.selectList(
        new LambdaQueryWrapper<WfCategory>().eq(WfCategory::getDelFlag, "0"));
    return buildTree(all, 0L);
}
private List<WfCategory> buildTree(List<WfCategory> all, Long parentId) {
    return all.stream().filter(c -> parentId.equals(c.getParentId()))
        .peek(c -> c.setChildren(buildTree(all, c.getId()))).collect(Collectors.toList());
}
```

### 6.3 表达式字典(WfExpressionServiceImpl)

```java
@Override
@Transactional
public void add(WfExpression expr) {
    checkKeyUnique(expr);
    wfExpressionMapper.insert(expr);
}

@Override
@Transactional
public void remove(Long id) {
    // 校验是否被引用(act_ru_variable / 模型 JSON)
    long refCount = wfModelService.countByExpressionId(id);
    if (refCount > 0) throw new ServiceException("该表达式已被引用,不能删除");
    wfExpressionMapper.deleteById(id);
}
```

### 6.4 流程迁移(WfProcessMigrateServiceImpl)

```java
@Override
@Transactional
public void migrate(WfProcessMigrateBo bo) {
    // 1. 校验源 + 目标定义
    ProcessDefinition targetPd = repositoryService.createProcessDefinitionQuery()
        .processDefinitionKey(bo.getTargetKey()).latestVersion().singleResult();
    if (targetPd == null) throw new ServiceException("目标流程定义不存在");

    // 2. 创建迁移计划
    MigrationPlan plan = processMigrationService.createMigrationPlan(
        bo.getSourceProcDefId(), targetPd.getId());

    // 3. 对每个运行中的实例迁移
    List<ProcessInstance> running = runtimeService.createProcessInstanceQuery()
        .processDefinitionId(bo.getSourceProcDefId()).list();
    for (ProcessInstance pi : running) {
        Map<String, String> activityMapping = bo.getActivityMapping();
        plan.processInstance(pi.getId()).addActivityMigrationMappings(activityMapping);
    }
    plan.execute();
}
```

### 6.5 流程清理(WfProcessClearServiceImpl)

```java
@Override
@Transactional
public void clear(List<String> procInsIds) {
    // 1. 强校验:仅清理已结束的实例(防止误删)
    List<ProcessInstance> running = runtimeService.createProcessInstanceQuery()
        .processInstanceIds(procInsIds).onlyActive().list();
    if (!running.isEmpty()) throw new ServiceException("含运行中实例,不能清理");

    // 2. 物理删
    for (String id : procInsIds) {
        historyService.deleteHistoricProcessInstance(id);
        wfTaskFormMapper.delete(new LambdaQueryWrapper<WfTaskForm>().eq(WfTaskForm::getProcInsId, id));
        wfCommentInfoMapper.delete(new LambdaQueryWrapper<WfCommentInfo>().eq(WfCommentInfo::getProcInsId, id));
        wfCopyMapper.delete(new LambdaQueryWrapper<WfCopy>().eq(WfCopy::getProcInsId, id));
        extendProcinstMapper.delete(new LambdaQueryWrapper<ExtendProcinst>().eq(ExtendProcinst::getProcInsId, id));
    }
}
```

### 6.6 流程废除(WfTaskServiceImpl.abolishProcess)

```java
@Override
@Transactional
public void abolishProcess(String procInsId, String comment) {
    // 1. 强校验:流程中心管理员才可(业务权限)
    if (!SecurityUtils.hasPermi("workflow:task:abolish")) {
        throw new ServiceException("无废除权限");
    }
    // 2. 写 processStatus='canceled'
    runtimeService.setVariable(
        runtimeService.createExecutionQuery().processInstanceId(procInsId).singleResult().getId(),
        ProcessConstants.PROCESS_STATUS_KEY, ProcessStatus.CANCELED.getStatus());
    // 3. 删 runtime
    runtimeService.deleteProcessInstance(procInsId, comment);
}
```

### 6.7 23 个 ServiceImpl 全景

| 序号 | Service | 关键能力 |
|---|---|---|
| 1 | `WfModelServiceImpl` | save / deploy / suspend / activate / publish |
| 2 | `WfBpmnModelServiceImpl` | 内部 BPMN 转换 |
| 3 | `WfDeployServiceImpl` | 部署管理 |
| 4 | `WfDeployFormServiceImpl` | 部署快照 |
| 5 | `WfFormServiceImpl` | 表单 CRUD |
| 6 | `WfFormCategoryServiceImpl` | 表单分类 |
| 7 | `WfFormTemplateServiceImpl` | 表单模板 |
| 8 | `WfCategoryServiceImpl` | 流程分类 |
| 9 | `WfExpressionServiceImpl` | 表达式 |
| 10 | `WfFlowableTaskServiceImpl` | 任务查询 |
| 11 | `WfTaskServiceImpl` | 13 类审批 |
| 12 | `WfTaskFormServiceImpl` | 节点表单 |
| 13 | `WfProcessServiceImpl` | 实例 / 待办 / 已办 / 已发 / 抄送 / 流程图 |
| 14 | `WfProcessMigrateServiceImpl` | 流程迁移 |
| 15 | `WfProcessClearServiceImpl` | 清理 |
| 16 | `WfCopyServiceImpl` | 抄送 |
| 17 | `WfCommentInfoServiceImpl` | 意见 |
| 18 | `IWfExtendProcinstServiceImpl` | 流程实例扩展 |
| 19 | `IWfCommentInfoServiceImpl` | 意见接口实现(同 17) |
| 20 | `WfBoundaryEventServiceImpl` | 边界事件 |
| 21 | `InjectUserTaskInProcessInstanceService` | 前加签 |
| 22 | `ExtensionElementsService` | 扩展元素处理 |
| 23 | (3 个 rollBack 策略 impl + 1 个 listener)| 见 §4.4 |

---

## 7. 审批旁路(本分支即此)

> **本分支 = 审批旁路落地点**。所有 13 类审批动作、退回、加签、抄送、边界事件,均通过 `WfTaskServiceImpl` 与 Flowable 引擎交互实现;不需要新写独立模块。

---

## 8. 审计/报表/Web/安全

### 8.1 审计

- **审批意见双写**:`act_hi_comment`(引擎)+ `wf_comment_info`(业务),`AddHisCommentCmd`
- **操作日志**:`@Log` 注解 + `LogAspect`(沿用下游基线 master),写 `sys_oper_log`
- **流程事件**:`GlobalEventListener` 监听 PROCESS_STARTED/COMPLETED/CANCELLED + TASK_CREATED/COMPLETED,写 `processStatus` 变量
- **异常堆栈**:由 `GlobalExceptionHandler`(沿用)统一处理

### 8.2 报表

- 流程中心 5 个 Tab(待办/已办/已发/抄送/待签收)分页查询
- 流程图:高亮已办/待办/驳回/跳过
- 流程监控:`act_ru_task.due_date_` < now() = 逾期任务
- 审批意见导出:基于 `wf_comment_info` + `act_hi_comment` 联合

### 8.3 Web 端(`web-ui`)

- 入口:`web-ui/src/main.js` + `permission.js`(沿用)
- 流程设计器:`web-ui/src/plugins/package/**` + `views/workflow/model/**`
- 流程中心:`web-ui/src/views/workflow/process/**`
- 表单设计器:`vform3` 集成(独立项目,JSON 落 `wf_form.content`)
- 节点「授权功能」:3 处文件**必须同步改**:
  - `web-ui/src/plugins/package/penal/task/task-components/UserTask.vue`
  - `web-ui/src/views/workflow/process/components/actionMeta.js`
  - `web-ui/src/views/workflow/process/components/TaskDialog.vue`
- 流程图渲染:bpmn.js viewer + 静态 PNG(后端 `/getFlowViewer` 返回)

### 8.4 移动端(`web-mp`)

- 待办 / 已办 / 已发 / 抄送 4 个 Tab(沿用下游基线 master)
- 流程图:用 web-view 加载 bpmn.js(首屏慢,**A-3 待办**)
- 审批弹窗:与 PC 端 13 类按钮一致

### 8.5 安全

| 项 | 实现 |
|---|---|
| 密码 | BCrypt |
| 登录传输 | RSA + 图形验证码 |
| Token | JWT 30min + Redis |
| 鉴权 | `@PreAuthorize("@ss.hasPermi('workflow:task:complete')")` |
| 流程接口 | `workflow:*` 权限点矩阵(需求 §8.1) |
| 节点「授权功能」 | 节点 `flowable:IdmPermission` JSON 数组 |
| 审批人解析 | `UserTaskListener` 解析 `flowable:IdmAssignee` / `IdmCandidateUsers` / `IdmCandidateGroups` |
| 撤回/废除 | 前端按钮按 `wfTaskVo.createBy` 过滤;后端 Service 再校 |

---

## 9. 错误码

| 类别 | code | 含义 | 触发场景 |
|---|---|---|---|
| 引擎 | `2001` | BPMN 校验失败 | `WfModelServiceImpl.save` |
| 引擎 | `2002` | 流程定义未部署/未激活 | `WfProcessServiceImpl.startProcess` |
| 引擎 | `2003` | 任务不存在 | `WfTaskServiceImpl.X` |
| 引擎 | `2004` | 无权操作(非 assignee) | `WfTaskServiceImpl.complete` |
| 引擎 | `2005` | 退回目标节点不存在 | `WfTaskServiceImpl.returnTask` |
| 引擎 | `2006` | 退回目标不是历史经过 | `WfTaskServiceImpl.returnList` |
| 业务 | `2010` | modelKey 已存在 | `WfModelServiceImpl` |
| 业务 | `2011` | formCode 已存在 | `WfFormServiceImpl` |
| 业务 | `2012` | categoryCode 已存在 | `WfCategoryServiceImpl` |
| 业务 | `2013` | 表达式被引用不能删 | `WfExpressionServiceImpl` |
| 业务 | `2014` | 加签用户不存在 | `WfTaskServiceImpl.beforeAddSignTask` |
| 业务 | `2015` | 流程清理含运行实例 | `WfProcessClearServiceImpl.clear` |
| 业务 | `2016` | 无废除权限 | `WfTaskServiceImpl.abolishProcess` |
| 业务 | `2017` | 流程迁移目标定义不存在 | `WfProcessMigrateServiceImpl` |
| 边界 | `2030` | 未知 boundary 类型 | `WfBoundaryEventServiceImpl.trigger` |

---

## 10. 并发与线程模型

| 资源 | 位置 | 配置 |
|---|---|---|
| Tomcat 线程池 | `application.yml` | `max-threads=800` / `min-spare=10` |
| 异步任务线程池 | `ThreadPoolConfig` | `corePoolSize=10` / `maxPoolSize=50` |
| Druid 连接池 | `application-druid.yml` | `min-idle=5` / `max-active=20` |
| **Flowable Async Executor** | `FlowBpmnConfig.processTaskExecutor` | `SimpleAsyncTaskExecutor`(无界,**⚠️ 风险**) |
| **Flowable Job Acquire Threads** | `application.yml` | 默认 3 |
| Quartz 线程池 | `ScheduleConfig` | 25 |
| Redis 缓存 | `RedisCache` | 30min |
| 限流 | Guava / Redis | `@RateLimiter` |
| 重复提交 | Redis | `@RepeatSubmit` |

**锁使用**:
- `qrtz_locks` Quartz
- `act_ru_*` 由 Flowable 维护
- `@DisallowConcurrentExecution` 注解禁止同任务并发

**`SimpleAsyncTaskExecutor` 警告**:无界线程池,生产应改为有界 + 队列。

---

## 11. 测试关注点

| # | 场景 | 重点 |
|---|---|---|
| TC-01 | 建模 → 保存 → 部署 | BPMN 校验失败报错 |
| TC-02 | 发起(无业务表) | `wf_task_form.form_content` 有数据(脚手架) |
| TC-03 | 同意 | `act_hi_comment` + `wf_comment_info` 双写 |
| TC-04 | 驳回 | `processStatus='reject'` |
| TC-05 | 退回(任意节点) | 6 种策略各跑通 |
| TC-06 | 退回目标非历史经过 | `/returnList` 不返回 |
| TC-07 | 转办 | `act_ru_identitylink` 改 |
| TC-08 | 委派 | 同上,加 OWNER 关系 |
| TC-09 | 前加签 | `act_ru_task` 出现新节点 |
| TC-10 | 后加签 | 原 task 关闭,新 task 出现 |
| TC-11 | 抄送 | `wf_copy` 落记录 |
| TC-12 | 撤回(发起人) | `processStatus='canceled'` |
| TC-13 | 废除(流程中心) | 同上 |
| TC-14 | 终止 | `processStatus='reject'` |
| TC-15 | 跳过节点 | `act_ru_task` 跳过 |
| TC-16 | 变更处理人 | `act_ru_task.assignee_` 改 |
| TC-17 | 流程图高亮 | 4 色区分 |
| TC-18 | 移动端审批 | web-mp 待办可见 |
| TC-19 | 边界事件 timer 触发 | `processStatus='boundary_timer'` |
| TC-20 | 流程清理(已结束) | 物理删 ok |
| TC-21 | 流程清理(含运行) | 报错 |
| TC-22 | 流程迁移 | 跨版本实例迁移成功 |
| TC-23 | 并发会签(过半) | `nrOfCompletedInstances/nrOfInstances >= 0.5` 触发完成 |
| TC-24 | 节点「授权功能」10 项 | 3 处前端文件 + 后端 `IdmPermission` 一致 |
| TC-25 | 业务表改造(若做) | `wf_task_form` 不再存业务字段 |

---

## 12. 需求变更记录

| 需求 | 落点 |
|---|---|
| Flowable 6.8.0 集成 | §3.2 + §4.1~§4.9 |
| 13 Controller | §6 + 概要 §5 |
| 13 类审批动作 | §4.3 + §6.6 |
| 退回 6 种策略 | §4.4 + §6.7 |
| 加签(前/后)| §4.5 |
| 抄送 | §4.6 + 概要 §3.5 |
| 边界事件 | §4.7 + 需求 §6.5 |
| 流程图高亮 | §4.9 |
| `wf_task_form` 脚手架警示 | §4.2 + README §7.8 + 架构 ADR-11 |
| 节点「授权功能」10 项 | §2.2 + 需求 §8.2 |
| `workflow:*` 权限点 | §8.5 + 需求 §8.1 |

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:覆盖 com.basic.flowable + com.basic.workflow 全包结构 / 23 ServiceImpl / 6 退回策略 / 4 边界事件 / 25 TC |
