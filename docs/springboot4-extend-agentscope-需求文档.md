# 基础平台(base-postgresql-vue3) 需求文档 — `springboot4-extend-agentscope` 分支

> 文档版本:v0.2 · 状态:全量能力集成完成
> 适用范围:仓库 **`springboot4-extend-agentscope`** 分支
> 上游依赖:`springboot4` 分支(**Spring Boot 4.1.1 / JDK 21 / Jackson 3**)
> 本分支 = **`springboot4` 基线 + AgentScope Java 2.0.4 多智能体框架集成 + 全量能力展厅**

---

## 1. 背景与目标

### 1.1 背景

`springboot4` 分支把后端推到了 JDK 21 + Spring Boot 4.1.1 + Jackson 3 的最新体系,业务功能保持 RuoYi-Vue 3.9.2 原样。平台目前只有 RBAC 管理能力,缺少「让业务方用自然语言驱动平台」的能力:数据查询、系统运维、流程审批都需要人工在菜单里翻。

[AgentScope Java](https://github.com/agentscope-ai/agentscope-java)(阿里巴巴开源,Apache-2.0)是面向 Java 的智能体(Agent)编程框架,提供:

- **ReAct Agent** —— 推理 + 工具调用循环
- **Toolkit** —— 函数调用(function calling)工具注册与 JSON Schema 自动生成
- **Memory** —— 会话记忆(内存 / JDBC / Redis / MongoDB 等多种实现)
- **Model Provider** —— OpenAI / Anthropic / DashScope / Gemini / Ollama 等多厂商统一抽象
- **Harness** —— 会话、工作区、沙箱、Skill、子 Agent、计划模式等运行时能力

### 1.2 目标

| # | 目标 | 验收标准 |
|---|---|---|
| **G1** | 在 `springboot4` 分支上集成 AgentScope Java **2.x 最新稳定版** | 依赖解析成功,`mvn clean install` 0 ERROR |
| **G2** | 保持与项目既有技术栈完全兼容 | JDK 21 + Spring Boot 4.1.1 + Jackson 3 不变,**不降级、不冲突** |
| **G3** | 以独立 Maven 子模块承载,不影响其他 7 个模块 | `extend-agentscope` 与 `extend-quartz`/`extend-generator` 同级 |
| **G4** | 暴露可用的 HTTP 接口(同步 + 流式) | `/agentscope/ping`、`/agentscope/chat`、`/agentscope/stream` |
| **G5** | 模型厂商可配置、可切换 | 仅改 `base-url` / `api-key` / `model-name` 即可切换 DeepSeek / Kimi / 智谱等 |
| **G6** | 沿用项目既有鉴权体系 | `/agentscope/chat` 需 JWT;`/agentscope/ping` 匿名可探活 |
| **G7** | **框架能力全量集成**,每个能力一个**可运行实例** | 21 个能力各有独立 Agent Bean 与能力描述,覆盖 2.x 的核心能力面 |
| **G8** | **前端可调用**:能力实例在前端可点击触发 | 菜单「扩展功能 → AgentScope 能力展厅」,左侧按能力清单导航,右侧逐个面板演示 |
| **G9** | **外部依赖型能力留开关** | MCP / 渠道 / 沙箱 / 调度器 4 个能力默认关闭,但装配骨架、状态接口与配置示例齐备 |
| **G10** | **无 Key 也能把链路跑通** | `agentscope.mock.enabled=true` 时,Mock 模型真实发起工具调用并返回真实业务数据 |

### 1.3 非目标(本分支明确不做)

| 不做项 | 原因 | 归属分支 / 后续 |
|---|---|---|
| ~~前端对话 UI~~ → **已做能力展厅** | v0.2 起前端在 `web-ui/src/views/basic-extend/agent/` 落地 | ✅ 本分支已完成 |
| 业务专属 Agent(知识库问答 / 流程编排 / 运维 Agent) | 本分支交付**通用能力实例**,不含业务语义 | 后续迭代 |
| Agent 会话持久化落库 | 依赖 `agentscope-extensions-postgresql`,属持久化方案选型 | 见《数据库设计》§4 |
| 真实 IM 渠道接入(钉钉 / 飞书 / 企微) | 需 IM 平台应用凭证;本分支提供**装配骨架 + webhook 协议模拟** | 独立分支 |
| 真实向量库 / RAG 检索增强 | 依赖 `agentscope-extensions-rag`;本分支提供**内存版关键词检索 + 知识管理接口** | 后续迭代 |
| 真实沙箱容器执行 | 依赖 `agentscope-extensions-sandbox`(Docker / K8s);本分支提供**受限白名单执行 + 接入说明** | 后续迭代 |
| 微服务拆分 / K8s 化 | 与 `springboot4` 基线策略一致,本分支不涉及 | 远期 |

---

## 2. 角色与用例

### 2.1 角色

| 角色 | 描述 | 权限 |
|---|---|---|
| **平台管理员** | 维护 `agentscope.*` 配置(模型地址、密钥、Agent 人设) | 配置中心 / 部署环境 |
| **业务开发者** | 在 `extend-agentscope` 中开发业务 Agent、注册自定义工具 | 代码仓库 |
| **终端用户** | 通过前端调用 Agent 接口提问 | 需 JWT,按菜单权限 |

### 2.2 核心用例

| UC | 名称 | 参与者 | 主流程 | 备选流 / 例外 |
|---|---|---|---|---|
| **UC-1** | 健康检查 | 运维 / K8s 探针 | `GET /agentscope/ping` → 返回 AgentScope 版本 | — |
| **UC-2** | 同步问答 | 终端用户 | 携带 JWT `GET /agentscope/chat?prompt=...` → 阻塞返回文本 | 401 未登录;模型超时返回 5xx |
| **UC-3** | 流式问答 | 终端用户 | 携带 JWT `GET /agentscope/stream?prompt=...` → SSE 事件流 | 客户端断连 → 取消订阅 |
| **UC-4** | 注册业务工具 | 业务开发者 | 在 `AgentScopeConfig` 中向自动装配的 `Toolkit` 注册工具方法 | — |
| **UC-5** | 切换模型厂商 | 平台管理员 | 改 `agentscope.openai.base-url` / `model-name` / 环境变量 `OPENAI_*` | — |
| **UC-6** | 浏览能力清单 | 终端用户 | 打开「扩展功能 → AgentScope 能力展厅」→ 左侧按类别展示 21 个能力 | 外部依赖型能力标注「未就绪」 |
| **UC-7** | 体验某个能力 | 终端用户 | 点选能力 → 右侧面板 → 输入问题 → 发送 → 看到回答/工具调用/结构化结果 | 能力缺少 Agent 时提示"能力不可用" |
| **UC-8** | 查看能力运行状态 | 终端用户 / 业务开发者 | 面板内的「能力详情」卡片展示工具清单、会话列表、事件流水、依赖就绪状态等 | — |
| **UC-9** | 无 Key 环境跑通链路 | 平台管理员 | 保持 `agentscope.mock.enabled=true` → Mock 模型真实调工具查真实库 | 配好 `OPENAI_API_KEY` 后置 false 切真实模型 |

---

## 3. 功能需求

### FR-1 框架集成底座

| 项 | 需求 | 优先级 |
|---|---|---|
| FR-1.1 | 引入 AgentScope Java `2.0.4`,坐标 `io.agentscope:*`,经 Maven Central 解析 | **P0** |
| FR-1.2 | 通过 `agentscope-dependencies-bom` + `agentscope-bom` 统一锁定全部传递依赖版本 | **P0** |
| FR-1.3 | 新建 Maven 子模块 `extend-agentscope`,与 `extend-quartz` / `extend-generator` 同级 | **P0** |
| FR-1.4 | 子模块只依赖 `agentscope-spring-boot-starter` + `agentscope-extensions-model-openai` + `agentscope-openai-spring-boot-starter` + `basic-common` + `spring-boot-starter-web` | **P0** |
| FR-1.5 | 不修改 `basic-framework` / `basic-admin-core` / `business-core` 任何既有源码 | **P0** |

### FR-2 自动装配

| 项 | 需求 | 优先级 |
|---|---|---|
| FR-2.1 | 引入 `agentscope-spring-boot-starter`,由 `AgentscopeAutoConfiguration` 自动装配 `ReActAgent` / `Memory` / `Toolkit` | **P0** |
| FR-2.2 | 引入 `agentscope-openai-spring-boot-starter`,由 `OpenAIAutoConfiguration` 自动装配 `OpenAIChatModel` | **P0** |
| FR-2.3 | **不得**重复声明与自动装配同类型的 `@Bean`(会导致按类型注入歧义 / 循环依赖) | **P0** |
| FR-2.4 | 提供 `AgentScopeConfig` 作为业务扩展点(注册自定义工具),但**不**重新声明 `ReActAgent` / `Toolkit` / `Memory` Bean | **P0** |

### FR-3 配置项

| 项 | 需求 | 优先级 |
|---|---|---|
| FR-3.1 | `agentscope.agent.enabled` / `.name` / `.sys-prompt` / `.max-iters` 可配置 | **P0** |
| FR-3.2 | `agentscope.model.provider` 指定模型厂商(本分支固定 `openai`) | **P0** |
| FR-3.3 | `agentscope.openai.enabled` / `.base-url` / `.api-key` / `.model-name` / `.endpoint-path` / `.stream` 可配置 | **P0** |
| FR-3.4 | API Key **必须**支持环境变量注入(`${OPENAI_API_KEY:...}`),生产环境不得硬编码 | **P0** |

### FR-4 HTTP 接口

| 项 | 需求 | 优先级 |
|---|---|---|
| FR-4.1 | `GET /agentscope/ping` — 返回 AgentScope 版本,标注 `@Anonymous` 允许匿名探活 | **P0** |
| FR-4.2 | `GET /agentscope/chat?prompt=...` — 同步问答,返回 `AjaxResult` | **P0** |
| FR-4.3 | `GET /agentscope/stream?prompt=...` — SSE 流式,`produces = text/event-stream` | **P0** |
| FR-4.4 | 接口走项目既有 Security 过滤链(JWT),**不**新增白名单例外(除 `/ping`) | **P0** |
| FR-4.5 | 使用项目既有 `AjaxResult` 响应包装,保持前后端契约一致 | **P0** |

### FR-5 模型厂商可切换

| 项 | 需求 | 优先级 |
|---|---|---|
| FR-5.1 | 仅通过 `base-url` + `api-key` + `model-name` 即可切换任意 OpenAI 兼容厂商 | **P0** |
| FR-5.2 | 需接入非 OpenAI 协议厂商(Claude / 通义)时,新增对应 `agentscope-extensions-model-*` + starter 依赖,不动业务代码 | **P1** |

### FR-6 能力全量集成(能力注册表)

| 项 | 需求 | 优先级 |
|---|---|---|
| FR-6.1 | 提供 `CapabilityProvider` 能力接口 + `CapabilityRegistry` 注册表,启动时汇总所有能力 | **P0** |
| FR-6.2 | 每个能力提供 `descriptor()`(id / 名称 / 分类 / 图标 / 说明 / 入参 / 依赖)与 `detail()`(运行状态) | **P0** |
| FR-6.3 | 每个核心能力一个**独立 `ReActAgent` Bean**,覆盖 2.x 核心能力面:工具调用 / 工具分组 / 结构化输出 / 任务清单 / 子 Agent / 多会话 / 长期记忆 / RAG / 权限 / 中断 / Hook / Middleware / 故障转移 / 状态持久化 / 元工具 / 多厂商模型 | **P0** |
| FR-6.4 | 外部依赖型能力(MCP / 渠道 / 沙箱 / 调度器)**默认关闭**,但需提供状态接口、装配骨架与配置示例 | **P0** |
| FR-6.5 | 多个 `ReActAgent` 并存时,一律**按 bean 名**获取,杜绝按类型注入歧义 | **P0** |
| FR-6.6 | 能力 id 为前后端唯一契约,后端新增能力自动出现在前端导航 | **P0** |

### FR-7 通用 HTTP 接口

| 项 | 需求 | 优先级 |
|---|---|---|
| FR-7.1 | `GET /agentscope/capability/list` — 能力清单,前端导航数据源 | **P0** |
| FR-7.2 | `POST /agentscope/capability/{id}/chat` — 通用同步对话,请求体 `{ prompt, sessionId, options }` | **P0** |
| FR-7.3 | `GET /agentscope/capability/{id}/stream` — 通用 SSE 流式对话 | **P0** |
| FR-7.4 | `GET /agentscope/capability/{id}/detail` — 能力详情(工具清单 / 会话列表 / 事件流水 / 依赖状态) | **P0** |
| FR-7.5 | 特殊能力可另建 Controller(如结构化输出 `/agentscope/structured/call`),并在 `CapabilityInfo.chatEndpoint` 中声明 | **P0** |
| FR-7.6 | 能力不存在 → 返回明确的"未找到";能力无 Agent → 返回"能力不可用",不抛 500 | **P0** |

### FR-8 前端能力展厅

| 项 | 需求 | 优先级 |
|---|---|---|
| FR-8.1 | 新增前端目录 `web-ui/src/views/basic-extend/agent/`,单一路由承载全部能力 | **P0** |
| FR-8.2 | 左侧导航由 `/agentscope/capability/list` 动态渲染,按核心 / 外部依赖型分组 | **P0** |
| FR-8.3 | 每个能力一个面板组件,统一走 `CapabilityChatBox` / `CapabilityDetailCard` 两个共享组件 | **P0** |
| FR-8.4 | 后端有能力而前端无专用面板时,回落到通用兜底面板,不得白屏 | **P0** |
| FR-8.5 | SSE 用 `fetch` + `ReadableStream` 解析(携带 `Authorization` 头) | **P0** |
| FR-8.6 | 走 RuoYi 动态路由:新增 `sys_menu` 记录,菜单「扩展功能 → AgentScope 能力展厅」 | **P0** |
| FR-8.7 | 前端构建通过(`pnpm run build:prod` 0 ERROR) | **P0** |

### FR-9 无 Key 跑通链路

| 项 | 需求 | 优先级 |
|---|---|---|
| FR-9.1 | `agentscope.mock.enabled`(默认 `true`)控制是否使用内置 Mock 模型 | **P0** |
| FR-9.2 | Mock 模型必须**真实发起工具调用**,使"模型决策→工具→结果回灌→最终回答"整条链路可验证 | **P0** |
| FR-9.3 | Mock 模型按问题关键词选择工具,便于演示不同业务工具 | **P1** |
| FR-9.4 | 配好真实 `OPENAI_API_KEY` 后置 `false` 即切真实模型,业务代码零改动 | **P0** |

---

## 4. 非功能需求

| 类别 | 要求 |
|---|---|
| **兼容性** | **不得**降级 `springboot4` 的 JDK 21 / Spring Boot 4.1.1 / Jackson 3;**不得**引入 javax 命名空间 |
| **依赖卫生** | 全部 AgentScope 依赖走 BOM,版本号只在根 `pom.xml` 一处定义(`agentscope.version`) |
| **零侵入** | 既有 7 个模块源码零改动;新模块对既有模块只有 `basic-common` 一处依赖 |
| **启动性能** | AgentScope 自动装配为轻量 Bean,冷启动增量 < 300ms |
| **安全** | API Key 走环境变量;`/chat` `/stream` 强制鉴权;`/ping` 只回版本号不回任何密钥;沙箱能力仅允许白名单命令 |
| **可观测** | 沿用项目 `sys_oper_log` / `sys_logininfor` 体系;AgentScope 侧日志走 `io.agentscope` logger |
| **可维护** | 遵循项目子模块约定:`extend-*` 独立打包,`basic-admin-api` 单一依赖引入 |
| **可演示** | 21 个能力全部可从前端点击触发,无 Key 也能看到真实工具查库结果 |

---

## 5. 验收清单

| # | 验收项 | 结果 |
|---|---|---|
| A-1 | `mvn clean install` 全模块 BUILD SUCCESS(JDK 21) | ✅ 通过 |
| A-2 | fat jar 内含 `extend-agentscope-4.0.0.jar` + 4 个 AgentScope 2.0.4 jar | ✅ 通过 |
| A-3 | 应用启动成功(`Started BasicApplication in ...`) | ✅ 通过 |
| A-4 | `GET /agentscope/ping` → `{"msg":"AgentScope Java v2.0.4 ready","code":200}` | ✅ 通过 |
| A-5 | `GET /agentscope/chat` 无 token → 401(鉴权生效) | ✅ 通过 |
| A-6 | `grep com.fasterxml.jackson.databind` 无新增命中(未破坏 Jackson 3 约束) | ✅ 通过 |
| A-7 | 既有 7 个模块源码 0 改动 | ✅ 通过 |
| A-8 | `GET /agentscope/capability/list` 返回 21 个能力(id / name / icon / summary / params) | ✅ 通过 |
| A-9 | `POST /agentscope/capability/tool/chat` 触发**真实工具调用**,返回真实 PostgreSQL 数据 | ✅ 通过 |
| A-10 | 菜单「扩展功能 → AgentScope 能力展厅」可见且路由可达 | ✅ 通过 |
| A-11 | `pnpm run build:prod` 前端构建 0 ERROR | ✅ 通过 |

---

## 6. 已知风险与约束

| # | 风险 | 影响 | 缓解 |
|---|---|---|---|
| **RK-1** | AgentScope 2.x API 处于演进期(v1→v2 有 `Message`→`Msg` 等破坏性变更) | 中 | 锁 `2.0.4` 稳定版;升级时按《详细设计》§6 迁移清单逐项核对 |
| **RK-2** | `agentscope-spring-boot-starter` 内部声明 `spring-boot.version = 4.0.1`,与项目 4.1.1 不一致 | 低 | BOM `import` 作用域下,项目根 `spring-boot-dependencies` 4.1.1 优先;已实测启动正常 |
| **RK-3** | 默认 `Memory` 为内存实现,重启会话丢失 | 中 | 需要持久化时按《数据库设计》§4 接入 `agentscope-extensions-postgresql` |
| **RK-4** | 同步 `call()` 阻塞 Web 线程 | 中 | 长任务改用 `/stream`;或后续接入 `agentscope-extensions-scheduler` 异步化 |
| **RK-5** | 框架 `target/classes` 残留旧 `.class` 会导致启动期 `VerifyError` | 中 | 切换分支后**必须** `mvn clean`,不可直接 `mvn package` |
| **RK-6** | 重复声明自动装配同类型 Bean 会触发循环依赖 | 高 | 见《详细设计》§4.2;已通过「不重复声明」规避 |
| **RK-7** | **自建 `ReActAgent` Bean 会让 starter 的默认智能体消失**(`@ConditionalOnMissingBean` 抑制) | 高 | 由 `AgentScopeConfig.basicAssistantAgent` 显式补齐默认智能体,替代 starter 产物 |
| **RK-8** | starter 的 `Toolkit` 是 **`@Scope("prototype")`**,在其上注册的工具在别处不可见 | 高 | 改为自建单例 `businessToolkit` 承载业务工具 |
| **RK-9** | 多个 `ReActAgent` 同类型共存,按类型注入必然歧义 | 高 | 一律 `@Qualifier` / `getBean(name, ReActAgent.class)` |
| **RK-10** | AgentScope 内部使用 **Jackson 2**,项目已迁移到 **Jackson 3** | 中 | 结构化输出目标 POJO 保持"普通 JavaBean",不用任何 Jackson 注解 |
| **RK-11** | 同步 `call()` 阻塞 Tomcat 线程(120s 超时) | 中 | 展厅默认 120s 超时;长任务走 SSE;后续异步化(M9) |
| **RK-12** | 21 个能力全部驻留内存(状态/记忆/知识/调度) | 低 | 均为演示级实现,生产化路径见《架构方案》§8 路线 |

---

## 7. 需求追溯矩阵

| 需求 | 实现位置 | 验证 |
|---|---|---|
| FR-1.1 / FR-1.2 | 根 `pom.xml` `dependencyManagement`(`agentscope-dependencies-bom` + `agentscope-bom`) | A-1 |
| FR-1.3 | 根 `pom.xml` `<modules>` | A-1 |
| FR-1.4 | `extend-agentscope/pom.xml` | A-1 |
| FR-2.1 / FR-2.2 | `extend-agentscope/pom.xml` 依赖 starter | A-2 / A-3 |
| FR-2.3 / FR-2.4 | `extend-agentscope/.../config/AgentScopeConfig.java` | A-3(修复过循环依赖) |
| FR-3.1 ~ FR-3.4 | `basic-admin-api/src/main/resources/application.yml` | A-3 |
| FR-4.1 ~ FR-4.5 | `extend-agentscope/.../web/AgentController.java` | A-4 / A-5 |
| FR-5.1 / FR-5.2 | `agentscope.openai.*` 配置项 | A-4 |
| FR-6.1 / FR-6.2 | `extend-agentscope/.../capability/CapabilityProvider.java`、`CapabilityRegistry.java`、`CapabilityInfo.java` | A-8 |
| FR-6.3 | `extend-agentscope/.../capability/<id>/<Id>Capability.java`(17 个核心能力) | A-8 / A-9 |
| FR-6.4 | `extend-agentscope/.../capability/{mcp,channel,sandbox,scheduler}/` | A-8(标 `enabled=false`) |
| FR-6.5 | `CapabilityController.resolveAgent()` 按 bean 名取 Agent | A-3 |
| FR-6.6 | `CapabilityInfo.id` ↔ `web-ui/.../panels/index.js` | A-8 / A-10 |
| FR-7.1 ~ FR-7.6 | `extend-agentscope/.../capability/CapabilityController.java` | A-8 / A-9 |
| FR-8.1 ~ FR-8.7 | `web-ui/src/views/basic-extend/agent/**`、`web-ui/src/api/agentscope.js`、`file-sql/create-agentscope-capability-menu.sql` | A-10 / A-11 |
| FR-9.1 ~ FR-9.4 | `extend-agentscope/.../mock/MockChatModel.java`、`.../config/ModelResolver.java`、`application.yml` | A-9 |

---

## 8. 参考来源

- AgentScope Java 官方仓库:<https://github.com/agentscope-ai/agentscope-java>
- v2.0.4 Release:<https://github.com/agentscope-ai/agentscope-java/releases/tag/v2.0.4>
- 官方文档(简体中文):<https://java.agentscope.io/v2/zh/docs/quickstart>
- 上游基线文档:`docs/springboot4-需求文档.md`、`docs/springboot4-架构方案.md`

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:§1 背景与目标 G1~G6 + 非目标清单;§2 角色与 UC-1~UC-5;§3 FR-1~FR-5;§4 非功能需求;§5 验收 A-1~A-7;§6 风险 RK-1~RK-6;§7 追溯矩阵 |
| v0.2 | 2026-10-23 | 全量能力集成专题:§1.2 目标扩到 G1~G10;§1.3 非目标逐条标注"本分支已做/仍不做";§2.2 新增 UC-6~UC-9;§3 新增 FR-6 能力注册表 / FR-7 通用 HTTP 接口 / FR-8 前端能力展厅 / FR-9 无 Key 跑通链路;§4 补可演示维度;§5 验收扩到 A-11;§6 风险扩到 RK-12;§7 追溯矩阵补 FR-6~FR-9 |
