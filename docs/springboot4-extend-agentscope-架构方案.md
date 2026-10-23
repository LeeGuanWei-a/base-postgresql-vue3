# 基础平台(base-postgresql-vue3) 架构方案 — `springboot4-extend-agentscope` 分支

> 文档版本:v0.2 · 状态:全量能力集成完成
> 适用范围:仓库 **`springboot4-extend-agentscope`** 分支
> 上游依赖:`springboot4` 分支(**Spring Boot 4.1.1 / JDK 21 / Jackson 3**)
> 本分支 = **`springboot4` 基线 + AgentScope Java 2.0.4 智能体框架集成 + 全量能力展厅**

---

## 1. 架构定位

**一句话定位**:在已升级到 Spring Boot 4.1.1 / JDK 21 的基础平台之上,**以独立子模块形式接入 AgentScope Java 2.0.4**,并以**「能力注册表 + 通用端点 + 前端展厅」**的模式把框架的**全部能力**逐个做成可运行、可从前端点击调用的实例。

| 维度 | 内容 |
|---|---|
| 使用方 | 平台管理员(配模型)、业务开发者(写 Agent / 注册工具)、终端用户(前端能力展厅) |
| 触发方式 | HTTP/JSON 调用后端 REST;同步 `call()` + 流式 SSE `streamEvents()`;前端左侧导航由后端能力清单动态驱动 |
| 数据形态 | 业务数据仍为 PostgreSQL 34 张表;**新增 2 条 `sys_menu` 菜单记录**;AgentScope 会话/记忆/状态默认内存态 |
| 范围边界 | 框架集成底座 + **21 个能力实例** + 能力注册表 + 前端能力展厅 |
| 不做清单 | 业务专属 Agent、会话记忆落库、真实 IM 接入、真实沙箱容器(均保留装配骨架与开关) |

---

## 2. 约束与质量属性

| 属性 | 目标 | 标注 |
|---|---|---|
| **JDK** | **21**(继承 `springboot4`) | **最强约束**;AgentScope 2.x 最低要求 17,本分支用 21 |
| **Spring Boot** | **4.1.1**(继承) | **最强约束**;AgentScope starter 内部声明 4.0.1,BOM 下由项目 4.1.1 覆盖 |
| **Jackson** | **3.1.5**(`tools.jackson.*`) | **不得**因 AgentScope 传递依赖降级回 Jackson 2 |
| **AgentScope** | **2.0.4**(`io.agentscope`) | 2026-10-08 发布的最新稳定版;2.1.0 仍为 BETA,不采用 |
| **Spring Security** | **7.1.1**(继承) | Agent 接口走既有 JWT 过滤链 |
| **数据库** | PostgreSQL,`basicdb_agentscope` schema,34 张表 | **本分支零 DDL 变更**(仅 2 条菜单数据) |
| **依赖管理** | 根 `pom.xml` 单一属性 `agentscope.version` + 2 个 BOM import | 传递依赖全部锁定 |
| **构建** | 8 个子模块(7 → 8) | `extend-agentscope` 加入 `<modules>` |
| **鉴权** | JWT(30 分钟) | `/ping` 匿名,`/chat` `/stream` 能力端点需登录 |
| **安全** | API Key 走环境变量;不硬编码;不回显 | |
| **能力注册表** | 21 个能力(17 核心 + 4 外部依赖型) | 能力 id 是前后端唯一契约 |

**最强约束**:① **JDK 21 + SB 4.1.1 + Jackson 3 三件套不可回退**;② **AgentScope 版本锁 2.0.4**,不混用 2.1.0-BETA;③ **既有 7 模块源码零改动**;④ **不得重复声明与自动装配同类型的 Bean**;⑤ **能力 id 一经发布即是对外契约**,前端按 id 渲染面板,后端不可随意改名。

---

## 3. 总体架构

### 3.1 容器视图(🔶 = 本分支新增)

```
┌────────────────────────────────────────────────────────────────┐
│  用户端(⬆️ 继承 springboot4,前端 JS 不动)                       │
│  ┌────────────────┐   ┌──────────────────────────────────┐      │
│  │  web-ui (PC)   │   │  web-mp (uni-app 3 移动端)        │      │
│  │  Vue 3.5.26    │   │  Vue 3.4.21                      │      │
│  └────────────────┘   └──────────────────────────────────┘      │
└────────────────────────────────────────────────────────────────┘
                              │ HTTP/JSON
┌─────────────────────────────▼──────────────────────────────────┐
│  basic-admin-api(Spring Boot 4.1.1 / JDK 21 / Jackson 3)       │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  Security 过滤链(JWT)      PermitAllUrlProperties        │   │
│  │  /agentscope/ping  → @Anonymous(匿名)                   │   │
│  │  /agentscope/chat  → 需 JWT                              │   │
│  │  /agentscope/stream→ 需 JWT(SSE)                        │   │
│  └──────────────────────────────────────────────────────────┘   │
│  ┌───────────────────────🔶──────────────────────────────────┐  │
│  │  extend-agentscope(com.basic:extend-agentscope:4.0.0)     │  │
│  │  ┌────────────────────────────────────────────────────┐  │  │
│  │  │ com.basic.agentscope.web.AgentController           │  │  │
│  │  │   @Autowired ReActAgent  → call() / streamEvents()  │  │  │
│  │  ├────────────────────────────────────────────────────┤  │  │
│  │  │ com.basic.agentscope.config.AgentScopeConfig       │  │  │
│  │  │   业务扩展点:注册自定义 Toolkit 工具                  │  │  │
│  │  └────────────────────────────────────────────────────┘  │  │
│  └──────────────────────────────────────────────────────────┘   │
│       ▲ 自动装配(agentscope-spring-boot-starter)                │
│       │                                                         │
│  ┌────┴──────────────────────────────────────────────────┐    │
│  │  io.agentscope:agentscope-core:2.0.4                   │    │
│  │    io.agentscope:agentscope-spring-boot-starter:2.0.4  │    │
│  │      ├─ AgentscopeAutoConfiguration                    │    │
│  │      │    ├─ agentscopeMemory   (Memory)               │    │
│  │      │    ├─ agentscopeToolkit  (Toolkit)              │    │
│  │      │    └─ agentscopeReActAgent (ReActAgent)        │    │
│  │    io.agentscope:agentscope-extensions-model-openai    │    │
│  │      └─ OpenAIChatModel(OpenAI 兼容协议)              │    │
│  │    io.agentscope:agentscope-openai-spring-boot-starter │    │
│  │      └─ OpenAIAutoConfiguration                       │    │
│  │           └─ openAIChatModel (OpenAIChatModel)         │    │
│  └───────────────────────────────────────────────────────┘    │
│       │                                                             
│  ┌────▼─────────────────┐   ┌────────────────────────────────┐      
│  │ PostgreSQL(34 表)    │   │ 外部 LLM(OpenAI 兼容协议)      │      
│  │ basicdb_agentscope   │   │  DeepSeek / Kimi / 智谱 / ...  │      
│  └──────────────────────┘   └────────────────────────────────┘      
└────────────────────────────────────────────────────────────────┘
```

### 3.2 模块依赖调整

**新增模块 `extend-agentscope`**:

| 依赖 | scope | 说明 |
|---|---|---|
| `io.agentscope:agentscope-spring-boot-starter` | compile | 自动装配 `ReActAgent` / `Memory` / `Toolkit`;内部 SB 4.0.1 |
| `io.agentscope:agentscope-extensions-model-openai` | compile | `OpenAIChatModel` 实现,位于 `io.agentscope.extensions.model.openai` |
| `io.agentscope:agentscope-openai-spring-boot-starter` | compile | `OpenAIAutoConfiguration` + `OpenAIProperties` |
| `com.basic:basic-common` | compile | 复用 `AjaxResult` / `@Anonymous` |
| `org.springframework.boot:spring-boot-starter-web` | compile | `@RestController` / SSE |

**根 `pom.xml` 变更**:

| 位置 | 变更 |
|---|---|
| `<properties>` | 新增 `<agentscope.version>2.0.4</agentscope.version>` —— **全仓库唯一版本来源** |
| `<dependencyManagement>` | import `agentscope-dependencies-bom`(第三方依赖版本) + `agentscope-bom`(官方模块版本);声明 `agentscope-spring-boot-starter`;声明 `com.basic:extend-agentscope:${basic.version}` |
| `<modules>` | 新增 `<module>extend-agentscope</module>` |

**`basic-admin-api/pom.xml` 变更**:新增 `com.basic:extend-agentscope`(版本由 dependencyManagement 提供)。

**依赖树影响面**:AgentScope 传递引入 Reactor 3.6+、Jackson 2.x(databind)等。**Jackson 2 与项目 Jackson 3(`tools.jackson.*`)包名不同,共存不冲突**;项目源码已全量迁移到 `tools.jackson.*`,不受影响。

### 3.3 配置架构

```
application.yml
└── agentscope                    ← io.agentscope.spring.boot.properties.AgentscopeProperties
    ├── agent:                    ← AgentProperties
    │   ├── enabled: boolean
    │   ├── name: String
    │   ├── sys-prompt: String
    │   └── max-iters: int
    ├── model:
    │   └── provider: String      ← ModelProperties(当前固定 openai)
    └── openai:                   ← io.agentscope.spring.boot.openai.OpenAIProperties
        ├── enabled: boolean
        ├── base-url: String      ← 默认 https://api.openai.com/v1
        ├── api-key: String       ← 必须走环境变量
        ├── model-name: String
        ├── endpoint-path: String
        └── stream: boolean
```

**厂商切换矩阵**:

| 厂商 | base-url | model-name | 额外依赖 |
|---|---|---|---|
| OpenAI | `https://api.openai.com/v1` | `gpt-4o-mini` | 无(已集成) |
| DeepSeek | `https://api.deepseek.com/v1` | `deepseek-chat` | 无 |
| Kimi | `https://api.moonshot.cn/v1` | `moonshot-v1-8k` | 无 |
| 智谱 GLM | `https://open.bigmodel.cn/api/paas/v4` | `glm-4-flash` | 无 |
| 阿里云百炼(兼容模式) | `https://dashscope.aliyuncs.com/compatible-mode/v1` | `qwen-plus` | 无 |
| Azure OpenAI | `https://<resource>.openai.azure.com/openai` | 部署名 | 无 |
| Anthropic Claude | — | — | 需加 `agentscope-extensions-model-anthropic` + 对应 starter |
| 通义千问(原生) | — | — | 需加 `agentscope-extensions-model-dashscope` + 对应 starter |

### 3.4 能力展厅架构(v0.2 新增)

**设计目标**:把 AgentScope 2.x 的能力**逐个变成可运行、可从前端点击调用**的实例,同时避免"每个能力写一个 Controller"造成的重复。

**三层结构**:

```
┌────────────────────────────────────────────────────────────────────────┐
│  前端 web-ui/src/views/basic-extend/agent/                             │
│  ┌────────────────┐  ┌──────────────────────────────────────────┐    │
│  │ index.vue      │  │ panels/*.vue  (21 个能力面板 + 通用兜底)   │    │
│  │ 左侧导航        │  │ components/CapabilityChatBox.vue          │    │
│  │ ← 能力清单驱动   │  │ components/CapabilityDetailCard.vue       │    │
│  └────────────────┘  └──────────────────────────────────────────┘    │
└────────────────────────────────────────────────────────────────────────┘
                 │  GET  /agentscope/capability/list      (导航数据源)
                 │  POST /agentscope/capability/{id}/chat  (通用对话)
                 │  GET  /agentscope/capability/{id}/stream(SSE)
                 │  GET  /agentscope/capability/{id}/detail(能力详情)
                 ▼
┌────────────────────────────────────────────────────────────────────────┐
│  extend-agentscope                                                      │
│  ┌──────────────────────────────────────────────────────────────────┐ │
│  │ CapabilityRegistry   ← 汇总所有 CapabilityProvider              │ │
│  │ CapabilityController ← 通用 chat / stream / detail / list        │ │
│  └──────────────────────────────────────────────────────────────────┘ │
│  ┌──────────────────────────────────────────────────────────────────┐ │
│  │ capability/<id>/<Id>Capability  × 21                             │ │
│  │   implements CapabilityProvider                                  │ │
│  │   ├─ @Bean("<id>CapabilityAgent")  独立的 ReActAgent             │ │
│  │   ├─ descriptor()  → CapabilityInfo (id/name/icon/summary/params) │ │
│  │   ├─ agentBeanName() → 供通用控制器按名取 Agent(避免按类型歧义)   │ │
│  │   └─ detail()       → 能力自身的运行状态/清单                    │ │
│  └──────────────────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────────────────────┘
```

**关键机制**:

| 机制 | 说明 |
|---|---|
| **能力即 Bean** | 每个能力是一个 `@Configuration`,同时声明自己的 `ReActAgent` Bean(唯一 bean 名)与 `CapabilityProvider` 实现 |
| **按名取 Agent** | 通用控制器用 `ApplicationContext.getBean(beanName, ReActAgent.class)` 取 Agent,**绝不按类型注入**(21 个 Agent 同类型) |
| **能力清单驱动前端** | 后端 `/agentscope/capability/list` 返回 id/name/category/icon/summary/params/requires/enabled,前端左侧导航完全由它渲染 |
| **前端按 id 映射面板** | `panels/index.js` 维护 `id → 组件` 映射;后端新增能力而前端无专用面板时,回落到 `GenericPanel` |
| **外部依赖型留开关** | 4 个外部依赖型能力默认 `enabled=false`,`requires` 写明缺什么,`detail()` 返回配置示例与就绪状态 |
| **Mock 模型兜底** | `agentscope.mock.enabled=true` 时用内置 Mock 模型,**真实发起工具调用**,无 Key 也能跑通整条 ReAct 链路 |

### 3.5 能力清单(id 即契约)

| # | id | 中文名 | icon | 类别 | 核心 API 演示点 |
|---|---|---|---|---|---|
| 1 | `chat` | 基础对话 | ChatDotRound | 核心 | 默认智能体 `basicAssistantAgent` + 多轮会话 |
| 2 | `tool` | 工具调用 | MagicStick | 核心 | `@Tool` + Toolkit + 真实查 PostgreSQL |
| 3 | `tool-group` | 工具分组 | FolderOpened | 核心 | `createToolGroup` / `enableTools` / `disableTools` |
| 4 | `structured` | 结构化输出 | Document | 核心 | `call(prompt, Class)` 结构化返回 |
| 5 | `task-list` | 任务清单 | List | 核心 | `enableTaskList()` |
| 6 | `sub-agent` | 子 Agent | Connection | 核心 | 子 Agent 注册为工具 / `@Tool` 委派 |
| 7 | `session` | 多会话 | ChatLineRound | 核心 | `stateStore` + `RuntimeContext.sessionId` |
| 8 | `memory` | 长期记忆 | Notebook | 核心 | `longTermMemory` + `LongTermMemoryMode` |
| 9 | `rag` | RAG 知识库 | Search | 核心 | `knowledge` + `ragMode` + `RetrieveConfig` |
| 10 | `permission` | 权限控制 | Lock | 核心 | `permissionContext` + `PermissionMode` + `stopOnReject` |
| 11 | `interrupt` | 中断运行 | VideoPause | 核心 | `agent.interrupt()` |
| 12 | `hook` | Hook 拦截 | Bell | 核心 | `Hook.onEvent` 旁路审计 |
| 13 | `middleware` | Middleware 拦截 | SetUp | 核心 | `MiddlewareBase.onSystemPrompt/onModelCall` |
| 14 | `failover` | 模型故障转移 | RefreshRight | 核心 | `fallbackModel` + `maxRetries` |
| 15 | `state-store` | 状态持久化 | Coin | 核心 | `AgentStateStore` 落地文件 |
| 16 | `meta-tool` | 元工具 | Tools | 核心 | `enableMetaTool(true)` |
| 17 | `model` | 多厂商模型 | Cpu | 核心 | OpenAI 兼容协议换 base-url / model-name |
| 18 | `mcp` | MCP 客户端 | Cpu | 外部依赖型 | `Toolkit.registerMcpClient(McpClientWrapper)` |
| 19 | `channel` | 渠道接入 | ChatRound | 外部依赖型 | 渠道注册表 + webhook 回调骨架 |
| 20 | `sandbox` | 沙箱执行 | Box | 外部依赖型 | 受限命令白名单执行 + 容器接入说明 |
| 21 | `scheduler` | 调度器 | Timer | 外部依赖型 | 进程内 cron 调度 + 与项目 Quartz 打通说明 |

---

## 4. 关键架构决策(ADR)

| # | 决策 | 备选 | 选择理由 |
|---|---|---|---|
| **ADR-31** | AgentScope 版本锁 **2.0.4 稳定版** | 2.1.0-BETA1 / 1.1.0-RC2 | 2.1.0 为预发,`agentscope-service` / CLI 等新组件 API 未稳;1.x 已进入维护尾期;2.0.4 是 2026-10-08 发布的最新 GA |
| **ADR-32** | 走 **Maven 坐标 + BOM** 集成,不 vendor 源码 | git submodule / 源码内嵌 | AgentScope 迭代快,源码内嵌将导致每次升级都要解冲突;BOM 锁定版本,升级只改一个属性 |
| **ADR-33** | 新建 **独立子模块** `extend-agentscope` | 直接放进 `basic-framework` / `basic-admin-api` | 与 `extend-quartz` / `extend-generator` / `extend-flowable` 约定一致;AgentScope 传递依赖树较大,隔离后不影响其他模块;未来可整体摘除 |
| **ADR-34** | **完全依赖 starter 自动装配**,业务侧不重复声明 `ReActAgent` / `Toolkit` Bean | 手写 `@Configuration` 装配 | 官方 starter 已提供 `AgentscopeAutoConfiguration` / `OpenAIAutoConfiguration`;重复声明同类型 Bean 会导致按类型注入歧义,**并进一步触发循环依赖**(实测 `agentScopeConfig` ↔ `agentscopeReActAgent` 互相依赖,启动失败) |
| **ADR-35** | 模型走 **OpenAI 兼容协议** 统一入口 | 分别集成 DashScope / Anthropic 原生 SDK | 6 家主流厂商都提供 OpenAI 兼容端点,一个 `base-url` 即可切换;原生协议仅在需要厂商专有能力时再叠加 |
| **ADR-36** | `/agentscope/ping` 用项目既有 **`@Anonymous`** 放行 | 在 `SecurityConfig` 加白名单 | 复用 `PermitAllUrlProperties` 注解扫描机制,零侵入 `basic-framework`;探活端点不含任何敏感信息 |
| **ADR-37** | **同步与流式两个端点并存** | 只做同步 | AgentScope 2.x 基于 Reactor 响应式(`Mono<Msg>` / `Flux<AgentEvent>`);长任务用流式可避免阻塞 Web 线程(见 RK-4) |
| **ADR-38** | 会话记忆**先用内存实现**,不立即落库 | 直接接 PostgreSQL 扩展 | 最小可用优先;落库涉及表结构与清理策略,属独立方案(见《数据库设计》§4) |
| **ADR-39** | **不引入** `agentscope-extensions-rag` / `-sandbox` / `-channel` | 一次全量引入 | 这些扩展各自牵涉向量库 / 容器运行时 / IM 平台开放配置,应按业务需求分批引入,避免无谓的依赖与配置面膨胀 |
| **ADR-40** | 能力用 **「注册表 + 通用端点」** 建模,而非一个能力一个 Controller | 每个能力各写 Controller | 21 个能力的对话/流式/详情逻辑 95% 相同;通用端点把重复降到 1 份,新能力只写 `CapabilityProvider` |
| **ADR-41** | **能力 id 是前后端唯一契约**,前端按 id 映射面板 | 前端写死能力列表 | 后端加能力即自动出现在前端导航;前端无专用面板时回落到 `GenericPanel`,后端与前端可各自独立演进 |
| **ADR-42** | Agent **一律按 bean 名获取**(`getBean(name, ReActAgent.class)`) | 按类型注入 | 21 个 `ReActAgent` 同类型,按类型注入必然歧义导致启动失败;bean 名同时是能力 id 的实现细节隔离层 |
| **ADR-43** | 引入 **Mock 模型**,让无 Key 也能跑通完整 ReAct 链路 | 强制要求配置 API Key | Mock 模型会**真实发起工具调用**,工具查的是真实 PostgreSQL,能验证"模型决策→工具→结果回灌→最终回答"整条链路;接上 Key 后行为完全一致 |
| **ADR-44** | 外部依赖型能力**默认关闭**,但**代码完整 + 装配路径清晰** + 状态面板可见 | 直接不实现 | MCP / IM / 容器 / 调度器都依赖外部基础设施;给出可运行的装配骨架 + `status` 接口 + 配置示例,既不阻塞交付,又不丢失后续接入口 |
| **ADR-45** | 菜单走 **`sys_menu` 动态路由**(而非前端写死路由) | 前端静态路由 | 与 RuoYi 既有权限模型一致,菜单可见性/权限点由后台统一管控;新增 2 条 `sys_menu` 记录即完成接入 |
| **ADR-46** | SSE 前端用 **fetch + ReadableStream** 手工解析 | `EventSource` | `EventSource` 不支持自定义请求头,无法携带 `Authorization`;fetch 可带 header,代价是需自行解析 SSE 分帧 |

---

## 5. 数据流

### 5.1 同步问答链路

```
客户端 ──JWT──▶ Security Filter Chain ──▶ AgentController.chat(prompt)
                                              │
                                              ▼
                                   ReActAgent.call(String)
                                              │
                                    ┌─────────┴─────────┐
                                    ▼                   ▼
                              Memory.addMessage   Model(OpenAIChatModel)
                                    │                   │
                                    │              HTTP POST {base-url}/chat/completions
                                    │                   │
                                    │                   ▼
                                    │              LLM 返回 assistant Msg
                                    │                   │
                                    └─────────┬─────────┘
                                              ▼
                                    ReAct 循环(有工具调用则继续)
                                              ▼
                                    Mono<Msg> ──.block()──▶ AjaxResult.success(msg, text)
```

**关键点**:`ReActAgent` 在 `call()` 内部管理完整 ReAct 循环(模型思考 → 是否调工具 → 执行工具 → 结果回灌 → 再思考),直至模型不再要求调工具。**Toolkit 默认为空**,因此当前链路等价于普通对话。

### 5.2 流式问答链路

```
客户端 ──▶ GET /agentscope/stream?prompt=... (Accept: text/event-stream)
              │
              ▼
      ReActAgent.streamEvents(String) → Flux<AgentEvent>
              │
              ▼
      Spring MVC SSE 逐事件推送 ──▶ 客户端
```

事件类型含:模型增量输出、工具调用、工具结果、最终回复等。**注意**:`Flux` 订阅在客户端断连时自动取消,AgentScope 侧会收到中断信号。

### 5.3 配置装配链路

```
application.yml(agentscope.*)
        │
        ▼
AgentscopeProperties  ←── @ConfigurationProperties("agentscope")
        │
        ├──▶ AgentscopeAutoConfiguration
        │      ├─ agentscopeMemory()   : Memory        (@Scope prototype)
        │      ├─ agentscopeToolkit()  : Toolkit       (@Scope prototype)
        │      └─ agentscopeReActAgent(Model, Memory, Toolkit, AgentscopeProperties)
        │              ⚠ @ConditionalOnMissingBean(ReActAgent) —— 本项目自建 Agent 后该 Bean 消失
        │
        └──▶ OpenAIProperties ←── @ConfigurationProperties("agentscope.openai")
                │
                ▼
        OpenAIAutoConfiguration
          └─ openAIChatModel(OpenAIProperties, ObjectProvider<OpenAIChatModelBuilderCustomizer>)
```

**本项目的实际装配(覆盖 starter 默认)**:

```
AgentscopeAutoConfiguration
  ├─ agentscopeMemory()  → prototype,本项目不使用
  ├─ agentscopeToolkit() → prototype,被 businessToolkit 取代
  └─ agentscopeReActAgent() → 因本项目已存在 ReActAgent Bean 而被 @ConditionalOnMissingBean 抑制
                                ↓ 由 AgentScopeConfig.basicAssistantAgent 显式补齐

AgentScopeConfig
  ├─ businessToolkit  (单例 Toolkit,注册 PlatformTools / SystemTools)
  └─ basicAssistantAgent (ModelResolver.resolve() + businessToolkit + agentscope.agent.*)

ModelResolver
  ├─ hasRealModel()  → @Qualifier("openAIChatModel")   (OpenAI 兼容)
  └─ 否则            → MockChatModel(非 Bean 单例,避免 Model 类型歧义)
```

### 5.4 能力展厅调用链路

```
浏览器  ──GET──▶  /agentscope/capability/list
                   │
                   ▼
           CapabilityRegistry.list()
                   │  ← 汇总 21 个 CapabilityProvider.descriptor()
                   ▼
           AjaxResult.success(capabilities[])
                   │
                   ▼
        前端渲染左侧导航(按 category 分组)

用户点击能力 → 面板渲染 → CapabilityChatBox 发起
                   │
                   ▼
   POST /agentscope/capability/{id}/chat  { prompt, sessionId, options }
                   │
                   ▼
        CapabilityController.doChat(id, request)
                   │  ├─ registry.getProvider(id)  → 找不到返回 404 语义
                   │  ├─ resolveAgent(provider)    → 按 bean 名取 ReActAgent
                   │  └─ agent.call(prompt, ctx).block(120s)
                   ▼
        AgentChatResponse { text, toolCalls, structured, elapsedMs, modelName, mock, ... }
                   │
                   ▼
        前端展示:文本 / 工具调用表格(name+input+output) / 结构化 JSON
```

---

## 6. 质量属性设计

| 属性 | 设计手段 | 验证 |
|---|---|---|
| **兼容性** | JDK 21 / SB 4.1.1 / Jackson 3 三件套不变;AgentScope 传递的 Jackson 2 与 `tools.jackson.*` 包名不同,共存 | 启动实测通过 |
| **零侵入** | 既有 7 模块源码 0 改动;新模块单向依赖 `basic-common` | `git diff` 可证 |
| **依赖卫生** | 版本只在根 `pom.xml` 一处;BOM 锁定传递依赖 | 传递树可控 |
| **启动性能** | AgentScope 装配 3 个轻量 Bean,无网络预热 | 冷启动 10.268s(与基线持平) |
| **安全** | Key 走环境变量;`/chat` `/stream` 强制鉴权;`/ping` 只回版本 | 401 验证通过 |
| **可观测** | `io.agentscope` logger 可单独调级;沿用 `sys_oper_log` 体系 | — |
| **可维护** | 子模块边界清晰;升级只改 `agentscope.version` | — |
| **可扩展** | 工具注册点、Memory 替换、更多模型厂商均可插拔;**新能力 = 新增一个 `CapabilityProvider` 实现,零改动通用端点与前端容器** | ADR-34/35/38/39/40/41 |
| **可演示** | 21 个能力全部可从前端点击触发;Mock 模型让无 Key 也能看到真实工具查库结果 | 端到端实测 |

---

## 7. 部署与运行

| 项 | 内容 |
|---|---|
| 产物 | 单体 fat jar `basic-admin-api.jar`(约 114 MB,含 4 个 AgentScope jar) |
| 运行时 | **JDK 21** |
| 端口 | 8080(可配) |
| 环境变量 | `OPENAI_BASE_URL` / `OPENAI_API_KEY` / `OPENAI_MODEL`(生产必配 `OPENAI_API_KEY`);`AGENTSCOPE_MOCK_ENABLED`(无 Key 时保持 `true`) |
| 探活 | `GET /agentscope/ping` → `{"msg":"AgentScope Java v2.0.4 ready","code":200}` |
| 能力清单 | `GET /agentscope/capability/list` → 21 个能力的 id/name/icon/summary/params |
| 前端入口 | 菜单「扩展功能 → AgentScope 能力展厅」→ `/basic-extend/agent` |
| 数据库 | 导入 `file-sql/basicdb-vue3.sql`(34 表 + 2 视图 + 2 条 `sys_menu` 菜单记录,已内联) |
| 启动前置 | **必须 `mvn clean`**,否则 `target/classes` 残留的旧 `.class` 会触发 `VerifyError`(见 RK-5) |

---

## 8. 后续演进路线

| 阶段 | 内容 | 前置条件 | 状态 |
|---|---|---|---|
| **M1** ✅ | 框架集成底座 + 最小可用端点 | 已完成 | ✅ |
| **M2** ✅ | 业务工具注册(系统用户查询 / 字典查询 / 操作日志统计) | 明确首批业务场景 | ✅ |
| **M3** ✅ | 能力注册表 + 21 个能力实例 + 前端能力展厅 | 本轮需求 | ✅ |
| **M4** | 会话记忆落库(接入 `agentscope-extensions-postgresql`) | 表结构与清理策略定稿 | ⏳ |
| **M5** | RAG 接真实向量库(替换 `SimpleKeywordKnowledge`) | 向量库选型 | ⏳ |
| **M6** | 接入真实 IM 渠道(`agentscope-extensions-channel-*`) | IM 平台应用凭证 | ⏳ |
| **M7** | 沙箱接入容器运行时(`agentscope-extensions-sandbox-*`) | K8s / Docker 环境 | ⏳ |
| **M8** | 调度器与项目既有 Quartz 打通 | 替换进程内 cron 实现 | ⏳ |
| **M9** | 异步任务化 + 会话落库,缓解同步 `call()` 阻塞(RK-4) | 压测数据 | ⏳ |

---

## 9. 参考来源

- AgentScope Java 仓库:<https://github.com/agentscope-ai/agentscope-java>
- 分支与标签:`main`(2.1.0-SNAPSHOT)、`2.0.x`(2.0.5-SNAPSHOT)、`1.x`(1.1.0-SNAPSHOT);标签 `v2.0.4`
- 官方文档:<https://java.agentscope.io/v2/zh/docs/quickstart>、<https://java.agentscope.io/v2/zh/integration/overview>
- 上游基线:`docs/springboot4-架构方案.md`(ADR-01~ADR-30 继承,本分支新增 ADR-31~ADR-39)
- 兄弟分支:`springboot4-extend-flowable`、`springboot4-extend-channel`

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `springboot4` 架构方案为基线,叠加「AgentScope Java 2.0.4 集成」专题:§2 约束、§3.1 容器视图、§3.2 模块依赖调整、§3.3 配置架构 + 厂商切换矩阵、§4 ADR-31~ADR-39 共 9 条、§5 三条数据流、§6 质量属性、§7 部署、§8 M1~M7 路线 |
| v0.2 | 2026-10-23 | 能力展厅专题:§1 定位升级为「全量能力集成」、§2 新增能力注册表约束、§3.4 能力展厅三层架构、§3.5 21 个能力 id 契约表、§4 新增 ADR-40~ADR-46 共 7 条、§5.3 补实际装配图 + §5.4 能力展厅调用链路、§7 补能力清单/前端入口/菜单 SQL、§8 路线扩展到 M1~M9 |
