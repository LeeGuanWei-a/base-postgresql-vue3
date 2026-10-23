# 基础平台(base-postgresql-vue3) 架构方案 — `springboot3-extend-flowable` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot3-extend-flowable`** 分支
> 前置依赖:《需求文档》v0.1
> 父分支:`springboot3`(继承其架构)
> 兄弟分支:`springboot2-extend-flowable`(用于横向对比,本方案 §7 列出差异)

---

## 1. 文档信息与版本

| 版本 | 日期 | 变更 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:在 `springboot3` 架构方案基础上,展开「§6 流程引擎架构」(模块 / 依赖 / 启动器 / 数据源 / 事务) + 「§7 与 sb2 差异」 |

---

## 2. 系统总览

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                              base-postgresql-vue3                             │
├──────────────────────────────────────────────────────────────────────────────┤
│  web-ui (Vue 3)                      web-mp (uni-app)                         │
│  ├─ bpmn-js 17.11 流程设计器          └─ 移动审批(后续)                       │
│  ├─ vform3 3.0.10 表单设计器                                                  │
│  └─ 22 个 workflow 视图 + 13 个 workflow API                                  │
├──────────────────────────────────────────────────────────────────────────────┤
│                              basic-admin-api                                  │
│  ├─ Flowable 7.2.0 启动器                                                     │
│  ├─ extend-flowable 业务层 (193 个 java 文件)                                  │
│  ├─ 13 个 Wf*Controller                                                      │
│  └─ 集成父分支 RBAC + 定时任务 + 代码生成                                     │
├──────────────────────────────────────────────────────────────────────────────┤
│                       extend-flowable (新模块)                                 │
│  ├─ controller  Wf*Controller  (13)                                          │
│  ├─ service     Wf*Service / Wf*ServiceImpl  (24 impl)                        │
│  ├─ service/wfModel          流程模型业务                                      │
│  ├─ service/wfProcessInfo    流程实例业务                                      │
│  ├─ service/wfProcessTask    任务业务                                          │
│  ├─ service/migrate          迁移业务                                          │
│  ├─ builder                  BPMN 模型构建器                                  │
│  ├─ cmd                      自定义 Flowable Command                          │
│  ├─ config                   Flowable 7 兼容配置 + 全局处理器                  │
│  ├─ customCache              自定义 MyBatis 缓存                              │
│  ├─ domain / mapper          10 张业务表 DTO/MyBatis Mapper                    │
│  ├─ dynamic                  动态表单/动态 bean                               │
│  ├─ flow                     flowable Service 二次封装                         │
│  └─ handler                  全局事件处理器 (TaskCreated / ProcessStarted ...) │
├──────────────────────────────────────────────────────────────────────────────┤
│                       basic-admin-core (继承)                                 │
│  └─ SysFiles(扩展流程字段) + ISysUserService(扩展)                             │
├──────────────────────────────────────────────────────────────────────────────┤
│                       basic-common / basic-framework (继承)                   │
└──────────────────────────────────────────────────────────────────────────────┘
                                       │
                                       ▼
┌──────────────────────────────────────────────────────────────────────────────┐
│                            PostgreSQL 15.1                                    │
│   schema: basicdb              ←  RBAC/系统表                                 │
│   schema: basicdb_flowable7    ←  ACT_*/FLW_*(80+) + wf_*(10) + sys_*(继承)   │
│   url: jdbc:postgresql://192.168.170.125:54321/basicdb-vue3                  │
│         ?useSSL=false&currentSchema=basicdb_flowable7                         │
└──────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. 技术栈选型

| 层 | 选型 | 版本 | 理由 |
|---|---|---|---|
| 流程引擎 | Flowable | 7.2.0 | BPMN 2.0 规范、SB 3 兼容、Jakarta 命名空间、活跃社区(2024-2026) |
| 启动器 | flowable-spring-boot-starter-process | 7.2.0 | 单 process 引擎够用;task/cmmn/event engine 按需加 starter |
| 流程设计器 | bpmn-js | 17.11.1 | 与 SB 4 master sb2 保持版本一致(后端 7.2.0 配套) |
| 表单设计器 | vform3-builds | 3.0.10 | 成熟 Vue 3 + Element Plus 兼容,体积 1.5MB |
| BPMN moddle | camunda-bpmn-moddle | 4.4.1 | bpmn-js 解析 Flowable 扩展属性 |
| 持久层 | MyBatis-Plus | 3.5.7 | 继承父分支;extend-flowable 业务表用其 + Flowable 自己的 MyBatis |
| 数据库 | PostgreSQL | 15.1 | 继承父分支 |
| 缓存 | Redis / Ehcache | 父分支选型 | 暂用,流程引擎默认不依赖外部缓存 |
| 事务 | Spring @Transactional | 6.x | 流程业务表与 RBAC 同一事务管理器 |
| 安全 | Spring Security 6 + JWT | 父分支选型 | perms 字符从 token 解析 |

**不选用**:
- ❌ Flowable 6.x(无 Jakarta 适配)
- ❌ Camunda 7(协议更严,过度设计)
- ❌ Activiti(Flowable fork,社区弱)
- ❌ Osworkflow / JBPM(老旧)
- ❌ 自研状态机(无 BPMN 兼容,可视化困难)

---

## 4. 模块依赖关系

```
basic-admin-api
    └─ extend-flowable
         └─ basic-admin-core (用其 SysFiles / ISysUserService)
              └─ basic-common + basic-framework
```

`pom.xml` 父级加 extend-flowable:
```xml
<modules>
  <module>basic-admin-api</module>
  <module>basic-admin-core</module>
  <module>basic-framework</module>
  <module>basic-common</module>
  <module>extend-quartz</module>
  <module>extend-generator</module>
  <module>extend-flowable</module>   <!-- 新增 -->
  <module>business-core</module>
</modules>
```

`basic-admin-api/pom.xml` 依赖 extend-flowable:
```xml
<dependency>
  <groupId>com.basic</groupId>
  <artifactId>extend-flowable</artifactId>
  <version>${revision}</version>
</dependency>
```

---

## 5. Flowable 7 适配关键点

### 5.1 启动器精简

```xml
<dependency>
  <groupId>org.flowable</groupId>
  <artifactId>flowable-spring-boot-starter-process</artifactId>
  <version>7.2.0</version>
</dependency>
```

**去掉** `flowable-spring-boot-starter-task`(7.x 已并入 process engine starter)。

### 5.2 数据库 schema 隔离

```yaml
flowable:
  database-schema: basicdb_flowable7    # 与业务表同 schema
  database-schema-update: true           # 自动建表(AC-3)
  async-executor-default-async-job-acquire-wait-time: 5000
  history-level: audit                   # 不存全部 detail,省空间
  form-field-validation-enabled: false   # 关闭内置校验,改 vform3
  check-process-definitions: false       # 关闭启动时的 process 校验(性能)
  idm:
    enabled: false                       # 关掉 Flowable 自带用户/组(用系统 RBAC)
```

### 5.3 循环依赖解决方案(本分支关键改造)

**问题**:`WfModelServiceBase` ↔ `WfProcessInfoBase` ↔ `WfProcessTaskBase` 三个 base 类相互 `@Resource` 注入各自的子 service 形成循环。

**本分支方案**(不依赖全局开关):
```java
public abstract class WfModelServiceBase<SELF extends IService<T>, T> {
  @Lazy   // 关键: 注入点 Lazy 打破循环
  @Resource
  protected WfProcessInfoService processInfoService;
}
```

`@Lazy` 让 spring 在第一次调用时才真正注入——spring 关闭了 bean 立即创建的检查,三个 base 类可以相互持有引用不报错。

**未使用** `spring.main.allow-circular-references=true` 全局开关(用户明确拒绝)。

### 5.4 MyBatis-Plus 路径迁移

sb2 的 `com.baomidou.mybatisplus.spring.service.IService` 在 3.5.x 已弃用,本分支全量替换为 `com.baomidou.mybatisplus.extension.service.IService`。涉及 extend-flowable 12 个文件 + basic-common / basic-admin-core 部分类。

### 5.5 Flowable 7 vs 6 关键 API 差异

| 6.x 用法 | 7.x 用法 |
|---|---|
| `runtimeService.startProcessInstanceByKey()` | 同名(向后兼容) |
| `taskService.complete(taskId)` | 同名 |
| `repositoryService.createDeployment()` | 同名,但 `addBpmnModel` 改用 `addProcessModel` |
| `historicProcessInstanceQuery` | 移到 `historyService.createHistoricProcessInstanceQuery` |
| `FormService` 已弱化 | 用自定义 `wf_form` + vform3 替代 |
| `idm-engine` | 关掉 idm,RBAC 用 RuoYi |
| 内部 `Candidate` 实现 | 改用 `FlowableTaskServiceImpl` 自定义包装 |

---

## 6. 流程引擎架构

### 6.1 业务包分布(193 java 文件)

```
com.basic.workflow
├── builder/           BPMN 模型构建器(2 文件)
│   ├── WfBpmnModelBuilder           bpmn-js ↔ Flowable 内部模型转换
│   └── WfModelAttributeConvert
├── cmd/               Flowable Command(1 文件)
│   └── WfFlowableCommand
├── config/            7.x 兼容配置(3 文件)
│   ├── FlowBpmnConfig               bpmn-js ↔ Flowable JSON 解析
│   ├── FlowGlobalHandlerConfig      TaskCreated/Completed 事件
│   └── FlowableIdmConfig
├── controller/        13 个 REST 入口
├── customCache/       MyBatis 缓存(1)
│   └── WfMybatisCache
├── domain/            10 张业务表 DTO(20+)
├── dynamic/           动态表单(3)
│   ├── DynamicFormField
│   ├── DynamicFormParser
│   └── DynamicFormRuntimeService
├── flow/              Flowable 二次封装(8)
│   ├── WfFlowableTaskService
│   ├── WfFlowableProcessService
│   └── ...
├── handler/           全局事件(5)
│   ├── WfProcessStartedHandler
│   ├── WfTaskCreatedHandler
│   ├── WfTaskCompletedHandler
│   ├── WfProcessCompletedHandler
│   └── WfBoundaryEventTrigger
├── mapper/            MyBatis Mapper(10)
├── service/           Service 接口 + impl(48)
│   ├── impl/
│   ├── wfModel/       流程模型 base + service
│   ├── wfProcessInfo/ 流程实例 base + service
│   ├── wfProcessTask/ 任务 base + service
│   └── migrate/       迁移业务
```

### 6.2 关键 service 依赖图

```
WfProcessController
    └─ WfProcessService (impl)
         ├─ WfProcessInfoBase (持有)
         │    ├─ @Resource @Lazy processInfoService
         │    ├─ @Resource @Lazy processTaskService
         │    └─ @Resource @Lazy formService
         ├─ WfProcessTaskBase (持有)
         │    ├─ @Resource @Lazy processInfoService
         │    ├─ @Resource @Lazy processTaskService
         │    └─ @Resource @Lazy formService
         └─ WfModelInfoBase (持有)
              └─ ...
```

3 个 base 互注,`@Lazy` 打破循环(详见 §5.3)。

---

## 7. 与 springboot2-extend-flowable 关键差异

| 维度 | sb2 | sb3 (本分支) |
|---|---|---|
| Spring Boot | 2.5.14 | 3.5.16 |
| Jakarta | javax.* | jakarta.* |
| Flowable | 6.8.0 | 7.2.0 |
| MyBatis-Plus | spring service path | extension.service path |
| 循环依赖 | 全局开关 | @Lazy 注解 |
| schema | basicdb_flowable | basicdb_flowable7 |
| 表 | 7 张 | 10 张 (+ wf_form_template / wf_deploy_form / wf_task_form) |

详见《需求文档》§7 表格。

---

## 8. 部署架构

```
┌─────────────────────────────────────────────┐
│  浏览器                                       │
│  ├─ http://localhost (web-ui vite dev)        │
│  └─ http://localhost:3080 (web-mp)            │
└──────────────────┬──────────────────────────┘
                   │ HTTP / JWT
                   ▼
┌─────────────────────────────────────────────┐
│  Nginx (生产环境)                             │
│  ├─ / → web-ui dist                          │
│  └─ /dev-api → 8080                          │
└──────────────────┬──────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────┐
│  Spring Boot basic-admin-api (单实例)        │
│  -Xms512m -Xmx2g                             │
│  JVM_OPTS: -Dfile.encoding=UTF-8             │
└──────────────────┬──────────────────────────┘
                   │ JDBC
                   ▼
┌─────────────────────────────────────────────┐
│  PostgreSQL 15.1 (单实例, 后续可主从)         │
│  - basicdb schema  (RBAC)                    │
│  - basicdb_flowable7 schema  (workflow)      │
└─────────────────────────────────────────────┘
```

**未使用**:
- 集群部署 / 共享 session(单实例足够)
- 异步执行器外部化(默认内嵌线程池)

