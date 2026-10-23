# 基础平台(base-postgresql-vue3) 详细设计(LLD) — `springboot4-extend-flowable` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot4-extend-flowable`** 分支
> 前置依赖:《架构方案》v0.1、《需求文档》v0.1、《概要设计》v0.1
> 本分支 = **`springboot4` 基线(SB 4.1.1 / JDK 21 / Jackson 3 / Security 7)+ `extend-flowable` 集成 Flowable 8.0.0**。
> **平台部分与 `springboot4` 逐字节一致**(包结构骨架 / 实体 / 平台 Service / Jackson 3 机制 / 审计 / 平台错误码 / 并发模型 / TC-01~TC-82 全部继承,本文档不重复);**本分支新增 §1.2 extend-flowable 包结构与文件台账、§2.4 流程领域模型与枚举、§3.11 Flowable 装配、§4.7 流程引擎九大组件、§9.2 流程错误码、§11.3 TC-83~TC-112**。
> 🔶 = 本分支新增;⬆️ = `springboot4` 继承。

---

## 1. 总体包结构

### 1.1 后端包结构(⬆️ 平台骨架 + 🔶 extend-flowable)

```
com.basic
├── BasicApplication / BasicServletInitializer              ⬆️
├── web/**                                                  ⬆️ 平台 Controller
├── framework/**                                            ⬆️ Security 7 / Jackson 3 / Redis / Druid
├── common/**                                               ⬆️ JsonUtils / RedisCache / LoginUser
├── system/** (basic-admin-core)                            ⬆️
├── quartz/** (extend-quartz)                               ⬆️
├── generator/** (extend-generator)                          ⬆️
│
├── constant/                                               🔶 节点扩展属性常量
│   └── ExtensionConstant                                   (IdmAssignee / IdmPermission / boundary* ...)
├── enm/                                                    🔶 流程/意见枚举
│   ├── CommentTypeEnum
│   └── ProcessStatusEnum
│
├── flowable/                                               🔶 引擎层(与 Flowable API 直接交互)
│   ├── common/
│   │   ├── constant/ProcessConstants                       流程状态 key / 边界事件扩展属性名
│   │   └── enums/
│   │       ├── BoundaryEventTypeEnum                       8 型边界事件
│   │       ├── ProcessStatus                               running/reject/return/completed/canceled/delete
│   │       ├── FormType
│   │       └── FlowComment
│   ├── config/
│   │   └── FlowBpmnConfig                                  EngineConfigurationConfigurer<SpringProcessEngineConfiguration>
│   ├── flow/
│   │   ├── CustomProcessDiagramGenerator                   PNG 生成(Activiti BpmnLayout 排版)
│   │   ├── CustomProcessDiagramCanvas                      四色绘制
│   │   ├── FindNextNodeUtil                                下一节点推导
│   │   └── FlowableUtils                                   Flowable 工具集
│   ├── listener/
│   │   ├── GlobalEventListener                             全局事件(PROCESS_STARTED/COMPLETED/CANCELLED/TASK_*)
│   │   ├── ProcessDueTimeListener                          到期监听
│   │   └── UserTaskListener                                任务创建时解析 IdmAssignee / IsSkip 等
│   └── utils/
│       ├── ModelUtils / ProcessFormUtils / ProcessUtils
│       ├── TaskUtils / UUIDGenerator / ExtansionPropertiesUtil
│       └── (安全 XML 工具)
│
└── workflow/                                               🔶 业务层
    ├── controller/                                         13 个 Wf* Controller
    │   ├── WfBoundaryEventController
    │   ├── WfCategoryController
    │   ├── WfDeployController
    │   ├── WfExpressionController
    │   ├── WfFormCategoryController
    │   ├── WfFormController
    │   ├── WfFormTemplateController
    │   ├── WfModelController
    │   ├── WfProcessClearController
    │   ├── WfProcessController
    │   ├── WfProcessMigrateController
    │   ├── WfTaskController
    │   └── WfTaskFormController
    ├── service/
    │   ├── I*Service                                        接口
    │   ├── impl/                                            19 个 ServiceImpl
    │   │   ├── WfModelServiceImpl
    │   │   ├── WfDeployServiceImpl / WfDeployFormServiceImpl
    │   │   ├── WfFormServiceImpl / WfFormCategoryServiceImpl / WfFormTemplateServiceImpl
    │   │   ├── WfCategoryServiceImpl / WfExpressionServiceImpl
    │   │   ├── WfProcessServiceImpl / WfProcessClearServiceImpl / WfProcessMigrateServiceImpl
    │   │   ├── WfTaskServiceImpl / WfFlowableTaskServiceImpl / WfTaskFormServiceImpl
    │   │   ├── WfBoundaryEventServiceImpl / WfCopyServiceImpl / WfCommentInfoServiceImpl
    │   │   ├── WfBpmnModelServiceImpl / IWfExtendProcinstServiceImpl
    │   │   └── ...
    │   ├── wfModel/                                         🔶 模型 XML 读写
    │   │   ├── WfModelSaveXmlService                        保存 BPMN XML
    │   │   └── WfModelQueryXmlService                       读取 BPMN XML
    │   ├── wfProcessInfo/ wfProcessTask/                    细粒度子服务
    │   └── migrate/
    │       ├── WfProcessMigratePlan
    │       ├── WfProcessMigrateResult
    │       └── WfProcessMigrateException
    ├── cmd/                                                 🔶 Flowable Command
    │   ├── AbstractDynamicInjectionCmd
    │   ├── InjectUserTaskInProcessInstanceCmd               前/后加签
    │   ├── ReplaceUserTaskInProcessInstanceCmd
    │   ├── AddHisCommentCmd                                 意见双写
    │   ├── RollbackCmd                                      退回
    │   └── rollBack/
    │       ├── RollbackOperateStrategy(接口)/ RollbackParamsTemplate
    │       ├── DefaultTaskNextGatewayRollbackOperateStrategy
    │       ├── ActiveMultiInstanceTaskRollbackOperateStrategy
    │       ├── CompletedMultiInstanceTaskAndNextDefaultTaskRollbackOperateStrategy
    │       ├── CompletedMultiInstanceTaskAndNextDefaultGateWayTaskRollbackOperateStrategy
    │       ├── NextCallActivityRollbackOperateStrategy
    │       ├── NextSubProcessRollbackOperateStrategy
    │       └── DefaultRollbackStrategyFactoryBean
    ├── domain/                                              实体 / BO / VO
    ├── mapper/                                              🔶 MyBatis-Plus(含直接操作 act_* 的 SQL)
    ├── builder/DynamicUserTaskBuilder                       动态任务构建
    ├── handler/                                             多实例处理
    └── customCache/CustomDeploymentCache                    部署缓存
```

### 1.2 🔶 `extend-flowable` 文件台账

| 分类 | 数量 | 说明 |
|---|---|---|
| Java 文件总 | **223** | — |
| Controller | **13** | 见 §1.1 |
| ServiceImpl | **19** | 见 §1.1 |
| 退回策略 | **6** | + 接口 + 工厂 + 参数模板 |
| Command | **5** | 加签 2 / 替换 1 / 意见 1 / 回滚 1 |
| 监听器 | **3** | 全局 / 到期 / 用户任务 |
| 枚举 | **5+** | 边界事件 / 流程状态 / 表单类型 / 意见 / 状态 |
| Mapper XML | 若干 | 含 `act_*` 只读 SQL |

### 1.3 前端结构(🔶)

```
web-ui/src/
├── views/basic-extend/workflow/**     21 个 .vue(model / process / form / category / expression / deploy)
├── plugins/package/**                 bpmn-js 17.11.1 + properties-panel 5.28
│   ├── designer/                      BpmnModeler 封装
│   └── penal/                         属性面板(UserTask / BoundaryEvent 自定义属性)
└── api/workflow/**                    13 组 API
```

---

## 2. 领域模型与枚举

### 2.1 平台实体(⬆️ 与 `springboot4` 一致)

16 个核心实体(`SysUser` / `SysRole` / `SysMenu` / `SysDept` / `SysPost` / `SysDictType` / `SysDictData` / `SysNotice` / `SysNoticeRead` / `SysJob` / `SysJobLog` / `SysOperLog` / `SysLogininfor` / `SysFiles` / `SysI18n` / `Demo`),主键 `ASSIGN_ID`,Jackson 注解仍 `com.fasterxml.jackson.annotation.*`。

### 2.2 平台枚举(⬆️ 一致)

```java
public enum BusinessType { OTHER, INSERT, UPDATE, DELETE, GRANT, EXPORT, IMPORT, FORCE, GENCODE, CLEAN }
public enum BusinessStatus { SUCCESS(0), FAIL(1); }
public enum OperatorType { OTHER, MANAGE, MOBILE }
public enum DataSourceType { MASTER, SLAVE }
public enum UserStatus { OK("0","正常"), DISABLE("1","停用"), DELETED("2","删除"); }
public enum HttpMethod { GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE, ALL }
public enum LimitType { DEFAULT, IP, CLUSTER }
```

### 2.3 平台状态流转(⬆️ 一致)

| 对象 | 状态 | 流转 |
|---|---|---|
| 定时任务 | `0=运行` ↔ `1=暂停` | changeStatus |
| 用户 | `0=正常` / `1=停用` / `2=删除` | add/changeStatus/remove |

### 2.4 🔶 流程领域模型与枚举(本分支新增)

#### 2.4.1 `ProcessStatus`(流程状态)

```java
public enum ProcessStatus {
    RUNNING("running"),        // 进行中
    REJECT("reject"),          // 已终止(驳回)
    COMPLETED("completed"),    // 已完成
    RETURN("return"),          // 已退回
    CANCELED("canceled"),      // 已取消(撤回/废除)
    DELETE("delete");          // 删除

    private final String status;
    public String getStatus() { return status; }
}
```

> 状态写入 `runtimeService` 流程变量 `processStatus`(`ProcessConstants.PROCESS_STATUS_KEY`)。

#### 2.4.2 `BoundaryEventTypeEnum`(8 型边界事件)

```java
public enum BoundaryEventTypeEnum {
    TIMER("timer"),
    MESSAGE("message"),
    SIGNAL("signal"),
    ERROR("error"),
    ESCALATION("escalation"),
    CANCEL("cancel"),
    COMPENSATION("compensation"),
    CONDITIONAL("conditional");
}
```

> 触发后状态前缀:`ProcessConstants.BOUNDARY_TRIGGER_STATUS_PREFIX = "boundary_"` → `boundary_timer` 等。

#### 2.4.3 `ProcessConstants`(关键常量)

| 常量 | 值 | 用途 |
|---|---|---|
| `PROCESS_STATUS_KEY` | `processStatus` | 流程变量名 |
| `BOUNDARY_TRIGGER_STATUS_PREFIX` | `boundary_` | 边界触发状态前缀 |
| `BOUNDARY_EXT_TYPE` | `boundaryType` | 边界事件扩展属性:类型 |
| `BOUNDARY_EXT_TIMER_DURATION` | — | timer 时长 |
| `BOUNDARY_EXT_ERROR_CODE` | — | error code |
| `BOUNDARY_EXT_SIGNAL_CODE` | — | signal code |
| `BOUNDARY_EXT_MESSAGE_ID` | — | message id |
| `BOUNDARY_EXT_CANCEL_ACTIVITY` | — | cancel 目标活动 |
| `SUFFIX` | `.bpmn20.xml` | 部署资源名后缀 |

#### 2.4.4 `ExtensionConstant`(节点扩展属性名)

| 常量 | BPMN 属性 | 用途 |
|---|---|---|
| `IDM_ASSIGNEE` | `flowable:IdmAssignee` | 指定审批人(用户/角色/部门表达式) |
| `IDM_CANDIDATE_USERS` | `flowable:IdmCandidateUsers` | 候选用户 |
| `IDM_CANDIDATE_GROUPS` | `flowable:IdmCandidateGroups` | 候选组 |
| `IDM_PERMISSION` | `flowable:IdmPermission` | 节点授权功能(JSON 数组,18 选子集) |
| `IS_SKIP` / `IS_NULL_SKIP` | — | 跳过策略(无审批人时) |
| `IS_EXAMINED` / `IS_ROLE_REPLACE` | — | 审批人是否已审 / 角色替换 |
| `IS_REVOKE` | — | 是否允许撤回 |
| `IS_USER_SCREEN` / `IS_ORG_SCREEN` / `IS_DEPT_OR_ORG` | — | 选人范围 |

#### 2.4.5 流程业务实体(9 张 `wf_*` + 1 张 `tbl_*`)

| 实体 | 表 | 关键字段 | 说明 |
|---|---|---|---|
| `WfCategory` | `wf_category` | `code` / `parentId` / `orderNum` | 流程分类(树) |
| `WfCategoryOrgLink` | `wf_category_org_link` | `categoryId` / `orgId` | 分类与组织关联 |
| `WfForm` | `wf_form` | `formCode` / `content`(vform3 JSON) | 表单定义 |
| `WfFormCategory` | `wf_form_category` | `code` / `parentId` | 表单分类 |
| `WfFormTemplate` | `wf_form_template` | `code` / `content` | 表单模板 |
| `WfDeployForm` | `wf_deploy_form` | `deployId` / `formId` / `content` | **部署快照** |
| `WfTaskForm` | `wf_task_form` | `procInsId` / `taskId` / `formContent` | 节点填写(**⚠️ 脚手架**) |
| `WfCommentInfo` | `wf_comment_info` | `procInsId` / `taskId` / `type` / `comment` | 审批意见(业务侧) |
| `WfCopy` | `wf_copy` | `procInsId` / `userId` / `readFlag` | 抄送 |
| `ExtendProcinst` | `tbl_flow_extend_procinst` | `procInsId` / `startUserId` / `processStatus` | 流程实例扩展 |
| `WfExpression` | `flw_expression` | `key` / `value` | 表达式字典 |

---

## 3. 框架机制

### 3.1 ~ 3.10 (⬆️ 与 `springboot4` 一致)

| 小节 | 内容 | 状态 |
|---|---|---|
| 3.1 启动与包扫描 | `@SpringBootApplication` + `@MapperScan` | ⬆️ 一致(**已覆盖 `com.basic.flowable` / `com.basic.workflow`**) |
| 3.2 动态数据源 | `DataSourceAspect` | ⬆️ |
| 3.3 事务边界 | `@Transactional(rollbackFor)` / `AsyncManager` | ⬆️ |
| 3.4 审计切面 | `LogAspect` | ⬆️ |
| 3.5 重复提交 | `SameUrlDataInterceptor` | ⬆️ |
| 3.6 限流 | `RateLimiterAspect` | ⬆️ |
| 3.7 XSS | `XssFilter` | ⬆️ |
| 3.8 字段填充 | `MyMetaObjectHandler` | ⬆️ |
| 3.9 SB3 升级机制 | jakarta / Security 6 / springdoc 2.9 | ⬆️ |
| 3.10 SB4 升级机制 | **Jackson 3** / Security 7 / starter 4 / 循环依赖 | ⬆️ 见 `docs/springboot4-详细设计.md` §3.10 |

### 3.11 🔶 Flowable 引擎装配(本分支新增)

#### 3.11.1 依赖

```xml
<!-- 父 pom -->
<properties>
    <flowable.version>8.0.0</flowable.version>
</properties>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.flowable</groupId>
            <artifactId>flowable-spring-boot-starter-process</artifactId>
            <version>${flowable.version}</version>
        </dependency>
    </dependencies>
</dependencyManagement>

<!-- extend-flowable/pom.xml -->
<dependency>
    <groupId>org.flowable</groupId>
    <artifactId>flowable-spring-boot-starter-process</artifactId>
</dependency>
```

> **只用** `flowable-spring-boot-starter-process`(BPMN 流程);**不引** DMN / CMMN / Form / IDM / REST 模块。

#### 3.11.2 引擎配置类

```java
package com.basic.flowable.config;

import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlowBpmnConfig
        implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

    @Override
    public void configure(SpringProcessEngineConfiguration cfg) {
        // 本项目不改引擎默认行为(history / schema 由 application.yml 配)
        // 如需定制:cfg.setAsyncExecutorActivate(...) / cfg.setProcessDefinitionCache(...)
    }
}
```

> **关键**:`EngineConfigurationConfigurer` 是 Flowable 官方扩展点;**不要**直接 `new SpringProcessEngineConfiguration()`(会绕过自动装配)。

#### 3.11.3 配置文件(★ 踩坑高发区)

```yaml
spring:
  flowable:
    database-schema-update: true          # 启动时自动建/升级表
    history-level: audit                  # 记录活动 + 意见(流程图/迁移需要)
    check-process-definitions: false       # 不扫 classpath:processes/ 目录
    database-schema: basicdb_flowable8     # ★ 必须与 JDBC currentSchema 一致
    idm:
      enabled: false                       # ★ 正确键;别写 flowable.common.app.idm-enabled
    form-field-validation-enabled: false   # 关闭 Flowable 表单字段校验
  main:
    allow-circular-references: true        # extend-flowable 内 Wf*Service 相互依赖

  datasource:
    url: jdbc:postgresql://localhost:5432/basicdb-vue3?currentSchema=basicdb_flowable8
    #                                                                    ^^^^^^^^^^^^^^^^ 必须同值
```

**配置键正确性对照**:

| 写法 | 是否正确 | 后果 |
|---|---|---|
| `spring.flowable.idm.enabled: false` | ✅ | 生效 |
| `flowable.common.app.idm-enabled: false` | ❌ | **静默忽略**,IDM 仍启用 |
| `spring.flowable.database-schema` | ✅ | 生效 |
| `flowable.database-schema` | ❌ | 静默忽略,表建到 `public` 或默认 schema |

> ⚠️ **Flowable 的配置键以 jar 内 `spring-configuration-metadata.json` 为准;写错的键不会报错,只会被忽略。** 判断是否生效:看启动日志 + `information_schema`。

#### 3.11.4 启动校验清单

```
启动后依次确认:
① 日志出现 Flowable 引擎初始化(无 exception)
② basicdb_flowable8 有 90 表
③ act_* = 39(其中 act_re_model / act_re_procdef / act_ru_task / act_hi_procinst 必须存在)
④ flw_* = 7(含 flw_expression)
⑤ wf_* = 9
⑥ tbl_flow_extend_procinst = 1
⑦ 无 act_de_* 表(Flowable 8 无 designER)
⑧ act_id_* 可能被建(9 张,**可无视**)
```

#### 3.11.5 引擎层关键组件

| 组件 | 类 | 职责 |
|---|---|---|
| 引擎配置 | `FlowBpmnConfig` | 扩展点(本项目基本留空) |
| 全局事件 | `GlobalEventListener` | 监听 `PROCESS_STARTED` / `PROCESS_COMPLETED` / `PROCESS_CANCELLED` / `TASK_CREATED` / `TASK_COMPLETED` → 更新 `processStatus`、触发通知 |
| 到期监听 | `ProcessDueTimeListener` | 任务到期提醒 |
| 用户任务监听 | `UserTaskListener` | 任务创建时解析 `IdmAssignee` / `IdmCandidateUsers` / `IdmCandidateGroups` / `IsSkip` |
| 流程图 | `CustomProcessDiagramGenerator` + `CustomProcessDiagramCanvas` | PNG 生成 + 四色绘制 |
| 节点推导 | `FindNextNodeUtil` | 计算下一节点(加签/退回用) |
| 部署缓存 | `CustomDeploymentCache` | 实现 `DeploymentCache<T>` |
| 引擎工具 | `ActivitiEngineUtil` | 借 Activiti 6 的 BpmnLayout 做自动排版 |

> ⚠️ **`ActivitiEngineUtil`**:流程图排版复用 Activiti 6 的 `BpmnLayout`,与 Flowable 8 共存需保证类路径无冲突(R43)。

---

## 4. 核心引擎详细设计

### 4.1 ~ 4.6 平台与 Jackson(⬆️ 与 `springboot4` 一致)

| 小节 | 内容 | 状态 |
|---|---|---|
| 4.1 登录引擎 | kaptcha + JWT | ⬆️ |
| 4.2 锁屏/解锁 | 复用 `/login` | ⬆️ |
| 4.3 定时任务 | Quartz starter + RAMJobStore | ⬆️ |
| 4.4 代码生成 | Velocity 模板 | ⬆️ |
| 4.5 动态路由/菜单 | `buildMenus` | ⬆️ |
| 4.6 Jackson 3 引擎 | `JsonMapper` / Module / Redis | ⬆️ 见 `docs/springboot4-详细设计.md` §4.6 |

### 4.7 🔶 流程引擎九大组件(本分支新增)

#### 4.7.1 模型保存与部署(`WfModelSaveXmlService`)

```java
/**
 * 保存模型(Flowable 8:存 BPMN XML,不存设计器 JSON)
 */
@Service
public class WfModelSaveXmlService {

    @Autowired private RepositoryService repositoryService;
    @Autowired private ProcessValidatorFactory validatorFactory;

    @Transactional(rollbackFor = Exception.class)
    public void saveXml(String modelId, String bpmnXml, String name, String key, String category) {
        // ① BPMN 合规校验
        BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(
                new BytesStreamSource(bpmnXml.getBytes(StandardCharsets.UTF_8)), false, false);
        List<ValidationError> errors = validatorFactory.createDefaultProcessValidator().validate(bpmnModel);
        if (!errors.isEmpty()) {
            throw new ServiceException("BPMN 校验失败:" + errors.stream()
                    .map(ValidationError::getProblem).collect(Collectors.joining("; ")));
        }

        // ② 写 act_re_model
        Model model = repositoryService.getModel(modelId);
        if (model == null) {
            model = repositoryService.newModel();
            model.setKey(key);
            repositoryService.saveModel(model);
        }
        model.setName(name);
        model.setCategory(category);
        repositoryService.saveModel(model);

        // ③ BPMN XML → act_ge_bytearray(editor source)
        repositoryService.addModelEditorSource(model.getId(),
                bpmnXml.getBytes(StandardCharsets.UTF_8));
    }
}
```

```java
/**
 * 部署模型 + 表单快照
 */
@Transactional(rollbackFor = Exception.class)
public String deploy(String modelId) {
    Model model = repositoryService.getModel(modelId);
    byte[] xmlBytes = repositoryService.getModelEditorSource(modelId);
    String bpmnXml = new String(xmlBytes, StandardCharsets.UTF_8);

    // ① 再校验一次(防手工改库)
    BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(
            new BytesStreamSource(xmlBytes), false, false);
    List<ValidationError> errors = validatorFactory.createDefaultProcessValidator().validate(bpmnModel);
    if (!errors.isEmpty()) throw new ServiceException("部署失败:" + errors);

    // ② 部署
    Deployment deployment = repositoryService.createDeployment()
            .name(model.getName())
            .key(model.getKey())
            .addString(model.getKey() + ProcessConstants.SUFFIX, bpmnXml)  // .bpmn20.xml
            .deploy();

    // ③ 🔶 表单快照(必须!否则审批读不到表单)
    List<WfDeployForm> forms = wfDeployFormService.listByModelId(modelId);
    for (WfDeployForm f : forms) {
        f.setDeployId(deployment.getId());
        wfDeployFormMapper.updateById(f);
    }
    return deployment.getId();
}
```

#### 4.7.2 读取模型(`WfModelQueryXmlService`)

```java
public String getXml(String modelId) {
    byte[] bytes = repositoryService.getModelEditorSource(modelId);
    return bytes == null ? null : new String(bytes, StandardCharsets.UTF_8);
}
```

> Flowable 8 无 `flowable-json-converter`;前端拿到的直接是 BPMN XML,bpmn-js 可直接 `importXML`。

#### 4.7.3 发起流程(`WfProcessServiceImpl.startProcess`)

```java
@Transactional(rollbackFor = Exception.class)
public String startProcess(String procDefKey, Map<String, Object> variables, JSONObject formData) {
    // ① 流程定义必须已部署且激活
    ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
            .processDefinitionKey(procDefKey)
            .latestVersion()
            .active()
            .singleResult();
    if (pd == null) throw new ServiceException("流程定义未部署或已挂起");

    // ② 业务扩展记录
    ExtendProcinst ext = new ExtendProcinst();
    ext.setProcDefKey(procDefKey);
    ext.setProcDefName(pd.getName());
    ext.setStartUserId(SecurityUtils.getUserId());
    ext.setStartUserName(SecurityUtils.getUsername());
    ext.setProcessStatus(ProcessStatus.RUNNING.getStatus());
    extendProcinstMapper.insert(ext);

    // ③ 让 Flowable 记录发起人(关键!)
    Authentication.setAuthenticatedUserId(SecurityUtils.getUsername());
    try {
        // ④ 启动(businessKey = ext.id)
        ProcessInstance pi = runtimeService.startProcessInstanceByKey(
                procDefKey, String.valueOf(ext.getId()), variables);

        // ⑤ 首节点表单 → wf_task_form(⚠️ 脚手架用法)
        WfTaskForm tf = new WfTaskForm();
        tf.setProcInsId(pi.getId());
        tf.setUserId(SecurityUtils.getUserId());
        tf.setFormContent(formData);
        wfTaskFormMapper.insert(tf);

        // ⑥ 回写 proc_ins_id
        ext.setProcInsId(pi.getId());
        extendProcinstMapper.updateById(ext);

        return pi.getId();
    } finally {
        Authentication.setAuthenticatedUserId(null);   // 清理 ThreadLocal
    }
}
```

#### 4.7.4 审批意见双写(`AddHisCommentCmd`)

```java
public class AddHisCommentCmd implements Command<Void> {

    private final Task task;
    private final String comment;
    private final CommentTypeEnum type;

    @Override
    public Void execute(CommandContext commandContext) {
        // ① 业务侧(wf_comment_info)—— 便于列表查询,不 join 引擎表
        WfCommentInfo info = new WfCommentInfo();
        info.setProcInsId(task.getProcessInstanceId());
        info.setTaskId(task.getId());
        info.setUserId(SecurityUtils.getUserId());
        info.setType(type.name());          // normal / reject / return / addSignBefore / ...
        info.setComment(comment);
        wfCommentInfoMapper.insert(info);

        // ② 引擎侧(act_hi_comment)—— 流程图/历史展示用
        commandContext.getTaskEntityManager()
                .addComment(task.getId(), task.getProcessInstanceId(), type.name(), comment);

        return null;
    }
}
```

> **为什么双写**:① 业务查询快(自己的表、自己的索引);② 流程图/历史需要引擎表。**两者在同一个 ProcessEngine 事务内**,保证一致。

#### 4.7.5 退回(RollbackCmd + 6 策略)

```java
public class RollbackCmd implements Command<Void> {

    private final RollbackOperateStrategy strategy;
    private final RollbackParamsTemplate params;

    @Override
    public Void execute(CommandContext commandContext) {
        // ① 关闭当前任务
        TaskEntity task = commandContext.getTaskEntityManager().findById(params.getTaskId());
        commandContext.getTaskEntityManager().deleteTask(task, "rollback", false);

        // ② 执行策略(跳到目标节点)
        strategy.rollback(params, commandContext);

        return null;
    }
}
```

**策略接口**:

```java
public interface RollbackOperateStrategy {
    void rollback(RollbackParamsTemplate params, CommandContext commandContext);
}
```

**6 种策略实现要点**:

| 策略 | 关键逻辑 |
|---|---|
| `DefaultTaskNextGatewayRollbackOperateStrategy` | 取目标节点的 incoming flow,`execution.setCurrentFlowElement(targetNode)` |
| `ActiveMultiInstanceTaskRollbackOperateStrategy` | 拆解 multiInstance(删除 `nrOfActiveInstances` 相关),退回到单 UserTask |
| `CompletedMultiInstanceTaskAndNextDefaultTaskRollbackOperateStrategy` | 跳过多实例容器,直接定位后续 Task |
| `CompletedMultiInstanceTaskAndNextDefaultGateWayTaskRollbackOperateStrategy` | 跳过多实例 + Gateway,定位目标 |
| `NextCallActivityRollbackOperateStrategy` | 目标在 CallActivity 之后:改跳转路径,不进入子流程 |
| `NextSubProcessRollbackOperateStrategy` | 目标在 SubProcess 内:先退出子流程再跳 |

**策略选择(工厂)**:

```java
public class DefaultRollbackStrategyFactoryBean {
    @Bean
    public RollbackStrategyFactory rollbackStrategyFactory() {
        RollbackStrategyFactory f = new RollbackStrategyFactory();
        f.register(NodeType.USER_TASK,              new DefaultTaskNextGatewayRollbackOperateStrategy());
        f.register(NodeType.MULTI_INSTANCE_ACTIVE,  new ActiveMultiInstanceTaskRollbackOperateStrategy());
        f.register(NodeType.MULTI_INSTANCE_DONE_TASK,     new CompletedMultiInstanceTaskAndNextDefaultTaskRollbackOperateStrategy());
        f.register(NodeType.MULTI_INSTANCE_DONE_GATEWAY,  new CompletedMultiInstanceTaskAndNextDefaultGateWayTaskRollbackOperateStrategy());
        f.register(NodeType.CALL_ACTIVITY,          new NextCallActivityRollbackOperateStrategy());
        f.register(NodeType.SUB_PROCESS,            new NextSubProcessRollbackOperateStrategy());
        return f;
    }
}
```

**退回前置校验**:

```java
// 目标必须是「历史经过的节点」
List<HistoricActivityInstance> his = historyService.createHistoricActivityInstanceQuery()
        .processInstanceId(procInsId)
        .finished()
        .list();
Set<String> passed = his.stream().map(HistoricActivityInstance::getActivityId).collect(toSet());
if (!passed.contains(targetActivityId)) {
    throw new ServiceException("只能退回到已走过的节点");
}
```

> 前端 `/workflow/task/returnList` **只返回历史节点**,从源头上限制;后端再校验一次。

#### 4.7.6 加签(动态注入)

**前加签**:

```java
public class InjectUserTaskInProcessInstanceCmd implements Command<Void> {

    private final String procInsId;
    private final String currentTaskId;
    private final List<String> userIds;
    private final String comment;
    private final boolean before;      // true=前加签 / false=后加签

    @Override
    public Void execute(CommandContext ctx) {
        // ① 定位当前 task 的 execution
        TaskEntity task = ctx.getTaskEntityManager().findById(currentTaskId);
        ExecutionEntity execution = task.getExecution();

        // ② 加载 BpmnModel
        BpmnModel bpmnModel = ProcessHelper.getBpmnModel(ctx, execution.getProcessDefinitionId());

        // ③ 构建动态 UserTask(继承原节点的 IdmPermission / IsSkip 等)
        FlowNode newNode = DynamicUserTaskBuilder.builder()
                .name(task.getName() + (before ? "·前加签" : "·后加签"))
                .id("dynamic_" + UUIDGenerator.next())
                .assignee(userIds.get(0))
                .inherit(task, bpmnModel)
                .build();

        // ④ 注入
        if (before) {
            injectBefore(ctx, execution, newNode);
        } else {
            injectAfter(ctx, execution, newNode);
        }

        // ⑤ 意见
        new AddHisCommentCmd(task, comment,
                before ? CommentTypeEnum.BEFORE_ADD_SIGN : CommentTypeEnum.AFTER_ADD_SIGN)
                .execute(ctx);

        return null;
    }
}
```

**注入基类**:

```java
public abstract class AbstractDynamicInjectionCmd implements Command<Void> {

    /** 在当前节点之前插入:currentNode.incoming → newNode → currentNode */
    protected void injectBefore(CommandContext ctx, ExecutionEntity exec, FlowNode newNode) {
        FlowNode current = (FlowNode) exec.getCurrentFlowElement();
        // ① 取 current 的 incoming flows
        List<SequenceFlow> incoming = new ArrayList<>(current.getIncomingFlows());
        // ② 新建 flow: previous → newNode
        for (SequenceFlow sf : incoming) {
            sf.setTargetRef(newNode.getId());
        }
        // ③ 新建 flow: newNode → current
        SequenceFlow toCurrent = createFlow(newNode, current);
        newNode.getOutgoingFlows().add(toCurrent);
        current.getIncomingFlows().clear();
        current.getIncomingFlows().add(toCurrent);
        // ④ 激活 newNode
        ctx.getAgenda().planContinueProcessOperation(exec);
    }
}
```

> ⚠️ **动态注入的本质是"改内存中的 BpmnModel"** —— 只对当前流程实例生效,**不落库**;重启后动态节点消失(这是 Flowable 动态注入的固有特性)。

#### 4.7.7 边界事件

**配置侧**(设计器写 `flowable:boundary*` 扩展属性 → `ProcessConstants.BOUNDARY_EXT_*`)。

**触发侧**(外部接口):

```java
@Transactional(rollbackFor = Exception.class)
public void trigger(String activityId, String type, JSONObject config) {
    // ① 类型校验
    BoundaryEventTypeEnum t = BoundaryEventTypeEnum.getByShortName(type);
    if (t == null) throw new ServiceException("不支持的边界事件类型:" + type);

    // ② 定位活动对应的执行
    List<Execution> execs = runtimeService.createExecutionQuery()
            .activityId(activityId)
            .list();
    if (execs.isEmpty()) throw new ServiceException("该节点无运行中实例");

    // ③ 引擎触发(timer/message/signal 等由引擎自身处理;此处做业务记录/通知)
    //    真实边界分支执行由 Flowable 完成

    // ④ 状态标记
    runtimeService.setVariable(execs.get(0).getId(),
            ProcessConstants.PROCESS_STATUS_KEY,
            ProcessConstants.BOUNDARY_TRIGGER_STATUS_PREFIX + type);
}
```

#### 4.7.8 流程迁移

```java
@Transactional(rollbackFor = Exception.class)
public WfProcessMigrateResult migrate(WfProcessMigrateBo bo) {
    // ① 目标定义
    ProcessDefinition target = repositoryService.createProcessDefinitionQuery()
            .processDefinitionId(bo.getTargetProcDefId()).singleResult();
    if (target == null) throw new WfProcessMigrateException("目标流程定义不存在");

    // ② 构建迁移计划
    MigrationPlan plan = processMigrationService.createMigrationPlan(
            bo.getSourceProcDefId(), bo.getTargetProcDefId());

    // ③ 节点映射(自动镜像 + 手动指定)
    Map<String, String> mapping = new HashMap<>();
    // 自动:同名节点
    // 手动:bo.getActivityMapping() 覆盖

    // ④ 逐个实例执行
    List<ProcessInstance> running = runtimeService.createProcessInstanceQuery()
            .processDefinitionId(bo.getSourceProcDefId()).list();
    int ok = 0; List<String> failed = new ArrayList<>();
    for (ProcessInstance pi : running) {
        try {
            plan.processInstance(pi.getId())
                .addActivityMigrationMappings(mapping);
            ok++;
        } catch (Exception e) {
            failed.add(pi.getId() + ":" + e.getMessage());
        }
    }
    if (!failed.isEmpty()) {
        throw new WfProcessMigrateException("迁移失败:" + failed);   // → 事务回滚
    }
    plan.execute();
    return new WfProcessMigrateResult(ok, failed);
}
```

> **事务要点**:校验 + 执行都在同一 `@Transactional` 内;`WfProcessMigrateException` 是 `RuntimeException` → 触发回滚。

#### 4.7.9 流程清盘(46 表)

```java
@Transactional(rollbackFor = Exception.class)
public void clearAll(String confirmPhrase) {
    // ① 权限
    if (!SecurityUtils.hasPermi("workflow:process:clearAll"))
        throw new ServiceException("无清盘权限");
    // ② 确认短语
    if (!CLEAR_CONFIRM_PHRASE.equals(confirmPhrase))
        throw new ServiceException("确认短语不正确");

    // ③ 按依赖倒序清 46 张流程表
    for (String table : CLEAR_TABLE_ORDER) {          // 常量数组,顺序固定
        jdbcTemplate.execute("TRUNCATE TABLE " + table + " CASCADE");
    }
}
```

**清盘表顺序(关键)**:

```
act_hi_*(10) → act_ru_*(13) → act_re_*(部署/模型/定义) → act_ge_*(字节/属性)
→ act_procdef_info / act_evt_log → flw_*(7) → wf_*(9) → tbl_flow_extend_procinst
```

> **平台表(`sys_*` / `qrtz_*` / `gen_*`)不清**。

#### 4.7.10 流程图生成

```java
public InputStream getFlowViewer(String procInsId) {
    // ① 取流程实例(可能在 runtime 或 history)
    ProcessInstance pi = runtimeService.createProcessInstanceQuery()
            .processInstanceId(procInsId).singleResult();
    String procDefId = (pi != null)
            ? pi.getProcessDefinitionId()
            : historyService.createHistoricProcessInstanceQuery()
                  .processInstanceId(procInsId).singleResult().getProcessDefinitionId();

    BpmnModel model = repositoryService.getBpmnModel(procDefId);

    // ② 计算高亮集合
    List<HistoricActivityInstance> his = historyService.createHistoricActivityInstanceQuery()
            .processInstanceId(procInsId).list();
    Set<String> completed = his.stream()
            .filter(h -> h.getEndTime() != null)
            .map(HistoricActivityInstance::getActivityId).collect(Collectors.toSet());
    Set<String> active = (pi == null) ? Set.of()
            : taskService.createTaskQuery().processInstanceId(procInsId).list().stream()
                .map(Task::getTaskDefinitionKey).collect(Collectors.toSet());

    // ③ 生成(已办绿 / 待办蓝 / 驳回红 / 跳过灰)
    return customProcessDiagramGenerator.generateDiagram(
            model, procInsId,
            new ArrayList<>(active),
            new ArrayList<>(completed),
            rejectedActivityIds);
}
```

**`CustomProcessDiagramCanvas` 配色**:

| 状态 | 颜色 |
|---|---|
| 已完成 | 绿 |
| 进行中(待办) | 蓝 |
| 已驳回 | 红 |
| 未走到(跳过) | 灰 |

---

## 5. 校验引擎

### 5.1 L1 / 5.2 L2(⬆️ 与 `springboot4` 一致)

平台字段校验(`jakarta.validation`)与跨业务校验(用户名唯一 / 角色 key 唯一 等)全部与 `springboot4` 一致。

### 5.3 🔶 流程校验(本分支新增)

| 校验项 | 位置 | 失败动作 |
|---|---|---|
| BPMN XML 合规 | `WfModelSaveXmlService.saveXml` / `deploy` | `ServiceException` + 错误列表 |
| modelKey 唯一 | `WfModelServiceImpl` | `ServiceException` |
| formCode 唯一 | `WfFormServiceImpl` | `ServiceException` |
| categoryCode 唯一 | `WfCategoryServiceImpl` | `ServiceException` |
| expressionKey 唯一 + **被引用不可删** | `WfExpressionServiceImpl` | `ServiceException` |
| 流程定义已部署且激活 | `startProcess` | `ServiceException` |
| 审批人必须是 assignee / candidate | `WfTaskServiceImpl` | `ServiceException` |
| **`IdmPermission` 必须含该动作** | `WfTaskServiceImpl` | `ServiceException` |
| **退回目标必须是历史节点** | `returnTask` + `/returnList` | `ServiceException` / 列表不返回 |
| 加签用户必须存在 | `WfTaskServiceImpl` | `ServiceException` |
| 迁移目标定义必须存在 | `WfProcessMigrateServiceImpl` | `WfProcessMigrateException` |
| 迁移映射必须覆盖所有运行节点 | 同上 | `WfProcessMigrateException` |
| 清盘确认短语 + 权限 | `WfProcessClearServiceImpl` | `ServiceException` |
| 边界事件类型 ∈ 8 型 | `WfBoundaryEventServiceImpl` | `ServiceException` |

---

## 6. 业务模块实现

### 6.1 ~ 6.6 平台业务(⬆️ 与 `springboot4` 一致)

用户管理 / 角色管理 / 菜单管理 / 通知已读 / Demo 业务 —— 全部一致。

### 6.7 🔶 流程业务实现要点(本分支新增)

| 模块 | 关键实现 |
|---|---|
| **流程分类** | `buildTree` 递归(与 `SysMenuService` 同模式) |
| **流程模型** | 保存 XML → 校验 → `act_re_model`;部署 → `Deployment` + 表单快照 |
| **部署管理** | 列表查 `act_re_deployment` join `act_re_procdef`;挂起/激活改 `act_re_procdef.SUSPENSION_STATE_` |
| **表单** | vform3 JSON 存 `content`;`byCode` 供节点绑定;被引用不可删 |
| **表达式** | `flw_expression` 字典;删除前查引用 |
| **流程实例** | 待办/已办/已发/抄送四类列表;流程图生成;删除/废除 |
| **任务** | 18 类动作;每类都先校验权限 → 写意见 → 执行 → 抄送 → 事件 |
| **节点表单** | `wf_task_form` 存填写内容(⚠️ 脚手架) |
| **边界事件** | 8 型校验 + 执行定位 + 状态标记 |
| **迁移** | 计划构建 + 事务执行 |
| **清盘** | 46 表倒序 TRUNCATE + 双重确认 |

---

## 7. 审批旁路(**本分支即审批落地**)

> 本分支**就是**流程引擎的落地分支;`BusinessType.GRANT` 等钩子已在实际审批链路中使用,不再需要"预留位点"。

---

## 8. 审计/报表/Web/安全

### 8.1 审计(⬆️ + 🔶 流程审计)

| 项 | 实现 |
|---|---|
| 平台写操作 | `@Log` + `LogAspect` → `sys_oper_log`(⬆️) |
| 登录事件 | `AsyncManager` → `sys_logininfor`(⬆️) |
| **流程审批意见** | **`wf_comment_info` + `act_hi_comment` 双写**(🔶) |
| **流程事件** | `GlobalEventListener` 监听 → 更新 `processStatus`(🔶) |
| **流程图** | 历史活动实例 → 四色渲染(🔶) |

### 8.2 报表(⬆️ + 🔶)

| 报表 | 说明 |
|---|---|
| 操作/登录日志 | ⬆️ 一致 |
| 服务器监控 | ⬆️ 一致 |
| **我的待办 / 已办 / 已发 / 抄送** | 🔶 四类列表(含分页/筛选) |
| **流程监控** | 🔶 按流程定义/发起人/状态筛选运行中实例 |

### 8.3 Web 端(`web-ui`,🔶 新增工作流)

| 项 | 内容 |
|---|---|
| 平台页面 | ⬆️ `views/basic-admin/**` 未改 |
| 工作流页面 | 🔶 `views/basic-extend/workflow/**`(21 vue) |
| 设计器 | 🔶 `plugins/package/**`(bpmn-js 17.11.1) |
| 表单设计器 | 🔶 vform3-builds 3.0.10 |
| API | 🔶 `api/workflow/**` |
| 自定义属性面板 | 🔶 `IdmAssignee` / `IdmPermission` / 边界事件 |

### 8.4 移动端(`web-mp`,⬆️ 零改动)

仅基础平台页面(登录 / 通知 / 个人中心);**无工作流页面**(A-23)。

### 8.5 安全(⬆️ + 🔶 流程权限)

| 项 | 实现 |
|---|---|
| 平台安全 | ⬆️ BCrypt / RSA / JWT / XSS / 限流 / CORS / Security 7 |
| **流程接口鉴权** | 🔶 **`workflow:*` 权限串覆盖 13 Controller** |
| **审批人校验** | 🔶 assignee / candidate 双重校验 |
| **节点授权功能** | 🔶 `IdmPermission`(18 动作子集) |
| **清盘/废除** | 🔶 独立权限串 + 双重确认 |
| **多态反序列化** | ⬆️ Redis PTV 白名单 |

---

## 9. 错误码

### 9.1 平台错误码(⬆️ 与 `springboot4` 完全一致)

| 类别 | code | 含义 |
|---|---|---|
| HTTP | 200 / 400 / 401 / 403 / 404 / 429 / 500 | 同 `springboot4` |
| 业务 | `1001`~`1012` | 验证码 / 密码 / 角色 key / 用户名 / 超管 / 菜单 / Cron / 已读 等 |

### 9.2 🔶 流程错误码(本分支新增)

| # | 场景 | 异常 | 提示 |
|---|---|---|---|
| 1 | BPMN 校验失败 | `ServiceException` | `BPMN 校验失败:{错误列表}` |
| 2 | modelKey 重复 | `ServiceException` | `模型标识已存在` |
| 3 | 流程定义未部署/已挂起 | `ServiceException` | `流程定义未部署或已挂起` |
| 4 | 任务不存在 | `ServiceException` | `任务不存在` |
| 5 | 非 assignee | `ServiceException` | `无权处理该任务,请先签收` |
| 6 | `IdmPermission` 不含该动作 | `ServiceException` | `当前节点不允许该操作` |
| 7 | 退回目标非历史节点 | `ServiceException` | `只能退回到已走过的节点` |
| 8 | 加签用户不存在 | `ServiceException` | `加签用户不存在` |
| 9 | 表单被引用不可删 | `ServiceException` | `表单已被流程引用,不能删除` |
| 10 | 表达式被引用不可删 | `ServiceException` | `表达式已被引用,不能删除` |
| 11 | 迁移目标定义不存在 | `WfProcessMigrateException` | `目标流程定义不存在` |
| 12 | 迁移映射不完整 | `WfProcessMigrateException` | `节点映射未覆盖:{节点列表}` |
| 13 | 迁移执行失败 | `WfProcessMigrateException` | `迁移失败:{实例明细}`(事务回滚) |
| 14 | 清盘权限不足 | `ServiceException` | `无清盘权限` |
| 15 | 清盘短语错误 | `ServiceException` | `确认短语不正确` |
| 16 | 边界事件类型不支持 | `ServiceException` | `不支持的边界事件类型:{type}` |
| 17 | 边界事件无运行实例 | `ServiceException` | `该节点无运行中实例` |
| 18 | schema 配错 | 启动/运行异常 | 见 `docs/springboot4-详细设计.md` §9.2 + 本分支排障 |

### 9.3 🔶 流程排障表

| 现象 | 根因 | 处置 |
|---|---|---|
| Flowable 表建到 `public` / 默认 schema | `database-schema` 与 `currentSchema` 不一致 | 两处改同值后重建 |
| `idm.enabled: false` 不生效 | 用了旧键 `flowable.common.app.idm-enabled` | 改 `spring.flowable.idm.enabled` |
| 模型保存报 JSON 相关错误 | 旧代码用 json-converter(Flowable 8 已删) | 改存 BPMN XML |
| 部署后审批看不到表单 | `wf_deploy_form` 无快照 | 部署时补快照逻辑 |
| 改表单不生效 | 在跑实例读旧快照 | 重新部署 |
| `act_de_model*` 表不存在(预期) | Flowable 8 无 designER | 正常,不是错误 |
| 启动报循环依赖 | `allow-circular-references` 未开 | 加 `spring.main.allow-circular-references: true` |
| 流程图排版错乱 | Activiti BpmnLayout 与 Flowable 8 模型兼容 | 检查 `ActivitiEngineUtil`;必要时手工坐标 |
| 多实例退回报错 | 策略选错 | 确认目标节点类型;检查工厂注册 |
| 迁移后实例卡住 | 节点映射漏配 | 用 `preview` 接口先看映射 |

---

## 10. 并发与线程模型(⬆️ + 🔶)

| 资源 | 位置 | 配置 |
|---|---|---|
| Tomcat | `application.yml` | `max-threads=800` |
| Druid | `application-druid.yml` | `min-idle=5` / `max-active=20` |
| **Quartz** | starter | **RAMJobStore**(⬆️) |
| **Flowable 异步执行器** | `spring.flowable.async-executor-activate` | 🔶 默认线程池;**Job 落 `act_ru_job`** |
| **Flowable 定时器** | `act_ru_timer_job` | 🔶 **落库**,重启可恢复 |
| Redis | `RedisCache` | 30min |
| Jackson | `JsonMapper` | 只读线程安全 |

**并发要点**:

| 要点 | 说明 |
|---|---|
| 引擎事务 | 所有 Command 在 Flowable 的 `txManager` 下执行,与 Spring 事务绑定 |
| 多实例 | BPMN `multiInstanceLoopCharacteristics` 由引擎保证原子性 |
| 任务抢占 | `taskService.complete` 对同一 task 并发调用会抛 `FlowableOptimisticLockingException` |
| 清盘并发 | `TRUNCATE ... CASCADE` 需在低峰期执行 |
| **多实例部署** | Flowable 定时器靠 `act_ru_job` 的 `lock_owner` 抢占;Quartz RAMJobStore **会重复**(见 P37) |

---

## 11. 测试关注点

### 11.1 ~ 11.2 平台与 SB3/SB4 专项(⬆️ TC-01~TC-82,与 `springboot4` 一致)

| 范围 | 内容 |
|---|---|
| TC-01~TC-20 | 平台业务(登录/Token/权限/限流/XSS/字典/定时/代码生成/日志/移动端/锁屏/通知/上传/停机) |
| TC-21~TC-32 | SB3 升级专项 |
| TC-63~TC-82 | SB4 升级专项(Jackson 3 / Security 7 / 大数 / Redis / Quartz / springdoc) |

### 11.3 🔶 流程引擎专项(TC-83~TC-112)

| # | 场景 | 重点 |
|---|---|---|
| **TC-83** | 8 模块 `mvn clean compile`(JDK 21) | 0 ERROR |
| **TC-84** | 启动 + Flowable 初始化 | 无异常 |
| **TC-85** | `basicdb_flowable8` 表数量 | **90 表 + 2 视图** |
| **TC-86** | `act_*` / `flw_*` / `wf_*` / `tbl_*` 数量 | 39 / 7 / 9 / 1 |
| **TC-87** | **无 `act_de_*`** | 0 |
| **TC-88** | 配置键生效 | `idm.enabled: false` 后无 IDM 初始化日志 |
| **TC-89** | 模型保存 | BPMN XML 写入 `act_ge_bytearray`;`act_re_model` 有记录 |
| **TC-90** | BPMN 校验失败 | 返回错误列表,不写库 |
| **TC-91** | 部署 | `act_re_deployment` / `act_re_procdef`;`wf_deploy_form` 有快照 |
| **TC-92** | 发起 | `tbl_flow_extend_procinst` + 首节点 `wf_task_form`;`act_hi_procinst.startUserId` 正确 |
| **TC-93** | 同意 | 意见双写;流程推进 |
| **TC-94** | 驳回 | `processStatus=reject` |
| **TC-95** | **退回 6 场景** | 每场景至少跑通 1 次 |
| **TC-96** | 退回非历史节点 | 报错 |
| **TC-97** | 转办 / 委派 | `act_ru_identitylink` 变化正确 |
| **TC-98** | 前加签 / 后加签 | `act_ru_task` 出现动态节点;名称含标记 |
| **TC-99** | 变更处理人 | `assignee_` 改 |
| **TC-100** | 抄送 | `wf_copy` 落库;已读置 1 |
| **TC-101** | **8 类边界事件** | 至少 timer + message 实际触发;其余可配 |
| **TC-102** | 边界事件外部触发 | 4 个接口可用;状态 `boundary_<type>` |
| **TC-103** | 流程图 | 四色正确 |
| **TC-104** | 迁移成功 | 实例迁到新定义 |
| **TC-105** | 迁移失败回滚 | `WfProcessMigrateException` + 数据无变化 |
| **TC-106** | 清盘 | 46 表清空;`sys_*` 不受影响 |
| **TC-107** | 清盘短语错误 | 报错,不清 |
| **TC-108** | 表达式被引用删除 | 报错 |
| **TC-109** | 改表单不重部署 | 在跑实例仍读旧快照 |
| **TC-110** | `workflow:*` 权限 | 无权限 → 403 |
| **TC-111** | Swagger | `Wf*` 分组可见 |
| **TC-112** | 前端 | 21 个 workflow vue 可访问;bpmn-js 可打开模型;vform3 可渲染表单 |

---

## 12. 需求变更记录

| 需求 | 落点 |
|---|---|
| 平台功能(锁屏/移动端/PostgreSQL/日志/限流/XSS) | ⬆️ 与 `springboot4` 一致 |
| ⬆️ SB3 / SB4 升级 | ⬆️ §3.9 / §3.10 |
| **🔶 Flowable 8.0.0 集成** | **§1.2 文件台账 + §2.4 领域模型 + §3.11 引擎装配 + §4.7 九大组件 + §9.2 流程错误码 + §11.3 TC-83~TC-112** |
| **🔶 BPMN XML 模型存储** | §3.11.3 / §4.7.1 / §4.7.2 |
| **🔶 8 型边界事件** | §2.4.2 / §4.7.7 |
| **🔶 6 种退回策略** | §4.7.5 |
| **🔶 加签动态注入** | §4.7.6 |
| **🔶 迁移事务回滚** | §4.7.8 |
| **🔶 清盘 46 表** | §4.7.9 |
| **🔶 流程图四色** | §4.7.10 |
| **🔶 `wf_task_form` 脚手架警示** | §2.4.5 / A-28 |

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `springboot4` 详细设计(v0.1)为基线;平台与 SB3/SB4 部分标注「与 springboot4 一致」;新增 §1.1 完整包结构(flowable 引擎层 + workflow 业务层)、§1.2 文件台账(223 Java / 13 Controller / 19 ServiceImpl)、§2.4 流程领域模型(ProcessStatus / 8 型枚举 / ProcessConstants / ExtensionConstant / 11 实体表)、§3.11 Flowable 装配(依赖 / FlowBpmnConfig / **配置键正确性对照** / 启动校验 8 步 / 8 组件)、§4.7 九大组件完整实现(模型保存部署 / 读取 / 发起 / 意见双写 / 退回 6 策略 / 加签注入 / 边界事件 / 迁移 / 清盘 / 流程图)、§5.3 流程校验 14 项、§9.2 流程错误码 18 项 + §9.3 排障表 10 类、§10 并发要点、§11.3 TC-83~TC-112 |
