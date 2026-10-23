# 基础平台(base-postgresql-vue3) 详细设计 — `springboot4-extend-agentscope` 分支

> 文档版本:v0.2 · 状态:全量能力集成完成
> 适用范围:仓库 **`springboot4-extend-agentscope`** 分支
> 上游依赖:`springboot4` 分支(**Spring Boot 4.1.1 / JDK 21 / Jackson 3**)
> 配套:《需求文档》《架构方案》《概要设计》《数据库设计》

---

## 1. 变更清单

| # | 文件 | 类型 | 说明 |
|---|---|---|---|
| 1 | `pom.xml` | 修改 | 新增 `agentscope.version` 属性、2 个 BOM import、starter 声明、`extend-agentscope` 管理项与 `<module>` |
| 2 | `basic-admin-api/pom.xml` | 修改 | 新增 `com.basic:extend-agentscope` 依赖 |
| 3 | `basic-admin-api/src/main/resources/application.yml` | 修改 | 新增 `agentscope.*` 配置块(含 `mock.enabled` 与 4 个外部依赖型开关) |
| 4 | `extend-agentscope/pom.xml` | **新增** | 子模块定义 |
| 5 | `extend-agentscope/src/main/java/com/basic/agentscope/config/AgentScopeConfig.java` | **新增** | `businessToolkit` + `basicAssistantAgent` |
| 6 | `extend-agentscope/src/main/java/com/basic/agentscope/config/ModelResolver.java` | **新增** | 模型统一出口(真实 / Mock) |
| 7 | `extend-agentscope/src/main/java/com/basic/agentscope/config/AgentScopePropertiesConfig.java` | **新增** | 外部依赖型能力的配置项绑定 |
| 8 | `extend-agentscope/src/main/java/com/basic/agentscope/mock/MockChatModel.java` | **新增** | Mock 模型(非 Bean) |
| 9 | `extend-agentscope/src/main/java/com/basic/agentscope/tool/PlatformTools.java` | **新增** | 业务查询工具 |
| 10 | `extend-agentscope/src/main/java/com/basic/agentscope/tool/SystemTools.java` | **新增** | 通用工具 |
| 11 | `extend-agentscope/src/main/java/com/basic/agentscope/dto/AgentChatRequest.java` | **新增** | 通用请求体 |
| 12 | `extend-agentscope/src/main/java/com/basic/agentscope/dto/AgentChatResponse.java` | **新增** | 通用响应体 |
| 13 | `extend-agentscope/src/main/java/com/basic/agentscope/capability/CapabilityInfo.java` | **新增** | 能力描述模型 |
| 14 | `extend-agentscope/src/main/java/com/basic/agentscope/capability/CapabilityCategory.java` | **新增** | 能力分类枚举 |
| 15 | `extend-agentscope/src/main/java/com/basic/agentscope/capability/CapabilityProvider.java` | **新增** | 能力接口 |
| 16 | `extend-agentscope/src/main/java/com/basic/agentscope/capability/CapabilityRegistry.java` | **新增** | 能力注册表 |
| 17 | `extend-agentscope/src/main/java/com/basic/agentscope/capability/CapabilityController.java` | **新增** | 能力通用出口 |
| 18 | `extend-agentscope/src/main/java/com/basic/agentscope/capability/<id>/**` | **新增** | **21 个能力实例** |
| 19 | `extend-agentscope/src/main/java/com/basic/agentscope/web/AgentController.java` | **新增** | 基础 HTTP 出口(ping / chat / stream) |
| 20 | `extend-agentscope/src/test/java/com/basic/agentscope/smoke/AgentscopeSmokeApp.java` | **新增** | 独立冒烟验证入口 |
| 21 | `extend-agentscope/src/test/resources/application.yml` | **新增** | 冒烟测试配置 |
| 22 | `web-ui/src/api/agentscope.js` | **新增** | 前端接口封装(含 SSE 解析) |
| 23 | `web-ui/src/views/basic-extend/agent/index.vue` | **新增** | 能力展厅容器 |
| 24 | `web-ui/src/views/basic-extend/agent/panels/*.vue` | **新增** | 21 个能力面板 + `GenericPanel` 兜底 |
| 25 | `web-ui/src/views/basic-extend/agent/panels/index.js` | **新增** | 能力 id → 面板映射表 |
| 26 | `web-ui/src/views/basic-extend/agent/components/CapabilityChatBox.vue` | **新增** | 通用对话外壳组件 |
| 27 | `web-ui/src/views/basic-extend/agent/components/CapabilityDetailCard.vue` | **新增** | 通用详情卡片组件 |
| 28 | `file-sql/basicdb-vue3.sql` | **修改** | 全库导出,内含 2 条 `sys_menu` 菜单记录;schema 为 `basicdb_agentscope` |

---

## 2. 根 `pom.xml` 变更详解

### 2.1 属性

```xml
<!-- AgentScope Java 集成（v2.0.4 稳定版，Java 17+ / Spring Boot 4） -->
<agentscope.version>2.0.4</agentscope.version>
```

> 全仓库**唯一**的 AgentScope 版本来源。升级只改这一行。

### 2.2 `dependencyManagement`

```xml
<!-- AgentScope Java - 依赖 BOM（统一管理 agentscope-core / harness / extensions 版本） -->
<dependency>
    <groupId>io.agentscope</groupId>
    <artifactId>agentscope-dependencies-bom</artifactId>
    <version>${agentscope.version}</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>

<!-- AgentScope Java - 业务模块 BOM（核心 + harness + 全部发布到中央仓库的扩展） -->
<dependency>
    <groupId>io.agentscope</groupId>
    <artifactId>agentscope-bom</artifactId>
    <version>${agentscope.version}</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>

<!-- AgentScope Java - Spring Boot 4 starter（auto-configuration + properties） -->
<dependency>
    <groupId>io.agentscope</groupId>
    <artifactId>agentscope-spring-boot-starter</artifactId>
    <version>${agentscope.version}</version>
</dependency>

<!-- AgentScope Java 集成子模块（与 extend-quartz / extend-generator 同级） -->
<dependency>
    <groupId>com.basic</groupId>
    <artifactId>extend-agentscope</artifactId>
    <version>${basic.version}</version>
</dependency>
```

**两个 BOM 的分工**:

| BOM | 作用 |
|---|---|
| `agentscope-dependencies-bom` | 锁定 AgentScope 的**第三方传递依赖**(Reactor、Jackson 2、SLF4J 等)版本 |
| `agentscope-bom` | 锁定 AgentScope **自身各模块**(core / harness / extensions / starters)的版本 |

**BOM import 的优先级**:`dependencyManagement` 中 `import` 进来的 BOM,其约束优先级**低于**当前 POM 中直接声明的条目。因此项目根 POM 显式声明的 `spring-boot-dependencies:4.1.1`、`mybatis-plus`、Druid 等**全部优先于** AgentScope BOM —— 这正是 `agentscope-spring-boot-starter` 内部声明的 `spring-boot.version=4.0.1` 不会把项目降级到 4.0.1 的原因。

### 2.3 `<modules>`

```xml
<module>basic-admin-api</module>
<module>basic-admin-core</module>
<module>basic-framework</module>
<module>basic-common</module>
<module>extend-quartz</module>
<module>extend-generator</module>
<module>extend-agentscope</module>   <!-- 新增 -->
<module>business-core</module>
```

---

## 3. `extend-agentscope/pom.xml`

```xml
<parent>
    <artifactId>basic</artifactId>
    <groupId>com.basic</groupId>
    <version>4.0.0</version>
</parent>
<artifactId>extend-agentscope</artifactId>
```

**依赖**(全部不带 `<version>`,由根 BOM 管理):

| 依赖 | 作用 |
|---|---|
| `org.springframework.boot:spring-boot-starter-web` | `@RestController`、SSE(`text/event-stream`) |
| `com.basic:basic-common` | `AjaxResult`、`@Anonymous` |
| `io.agentscope:agentscope-spring-boot-starter` | `AgentscopeAutoConfiguration` |
| `io.agentscope:agentscope-extensions-model-openai` | `OpenAIChatModel` |
| `io.agentscope:agentscope-openai-spring-boot-starter` | `OpenAIAutoConfiguration` |

> **不引入** `agentscope-harness`:`harness` 提供会话 / 工作区 / 沙箱 / Skill / 子 Agent 等运行时能力,体积与概念负担都较重。当前需求只需 ReAct + Toolkit,后续按需再加。

---

## 4. Java 代码详细设计

### 4.1 `AgentController`

```java
@RestController
@RequestMapping("/agentscope")
public class AgentController {

    @Autowired
    private ReActAgent agentscopeReActAgent;

    @Anonymous
    @GetMapping("/ping")
    public AjaxResult ping() {
        return AjaxResult.success("AgentScope Java v" + Version.VERSION + " ready");
    }

    @GetMapping("/chat")
    public AjaxResult chat(@RequestParam(defaultValue = "用一句话介绍 Spring Boot 4") String prompt) {
        Msg reply = agentscopeReActAgent.call(prompt).block(Duration.ofSeconds(60));
        return AjaxResult.success("ok", reply == null ? "" : reply.getTextContent());
    }

    @GetMapping(value = "/stream", produces = "text/event-stream")
    public Flux<AgentEvent> stream(@RequestParam(defaultValue = "讲个笑话") String prompt) {
        return agentscopeReActAgent.streamEvents(prompt);
    }
}
```

#### 4.1.1 依赖注入

| 字段 | 类型 | 来源 | 解析方式 |
|---|---|---|---|
| `agentscopeReActAgent` | `ReActAgent` | `AgentscopeAutoConfiguration#agentscopeReActAgent` | **按类型唯一候选** → 直接注入 |

> ⚠️ **不可**再声明第二个 `ReActAgent` 类型的 `@Bean`(会歧义 + 循环依赖,详见 §4.2)。

#### 4.1.2 `Version.VERSION`

`io.agentscope.core.Version` 是框架内置的版本解析器,三级回退:

1. classpath 上 `META-INF/agentscope/version.properties`(Maven resource filtering 注入)
2. jar manifest 的 `Implementation-Version`
3. 字面量 `"unknown"`(直接从 IDE 的 `target/classes` 运行时,并打 warn 日志)

本项目走 Maven 构建,故能拿到真实版本号。**实测返回 `v2.0.4`**。

#### 4.1.3 `/chat` 的阻塞与超时

| 项 | 说明 |
|---|---|
| 底层 | `ReActAgent.call(String)` → `Mono<Msg>`(Reactor) |
| 阻塞 | `.block(Duration.ofSeconds(60))` |
| 为何用 String 重载 | `call(List<Msg>, RuntimeContext)` 需显式构造 `RuntimeContext`,`call(String)` 重载由框架内部创建默认上下文,适合简单调用 |
| 超时语义 | `block` 超时抛 `IllegalStateException`,由项目 `GlobalExceptionHandler` 统一转错误响应 |

**Msg → 文本**:`Msg.getTextContent()`。`Msg` 的内容块可能是 `TextBlock` / `ToolUseBlock` / `ToolResultBlock` / `ImageBlock` 等混合,`getTextContent()` 只取文本部分。

#### 4.1.4 `/stream` 的流式

| 项 | 说明 |
|---|---|
| 底层 | `ReActAgent.streamEvents(String)` → `Flux<AgentEvent>` |
| 产出 | `produces = "text/event-stream"`,Spring MVC 自动按 SSE 逐条推送 |
| 事件类型 | `io.agentscope.core.event.AgentEvent` 体系:模型增量输出 / 工具调用 / 工具结果 / 最终回复 |
| 取消 | 客户端断连 → Spring 取消订阅 → 框架收到中断信号 |
| 鉴权 | 走 Security 过滤链,需 JWT |

#### 4.1.5 鉴权处理

| 端点 | 注解 | 效果 |
|---|---|---|
| `/ping` | `@Anonymous`(类 `com.basic.common.annotation.Anonymous`) | `PermitAllUrlProperties` 在 `afterPropertiesSet()` 扫描 `@RequestMapping` 处理器方法,发现 `@Anonymous` 后把该 URL 加入 permitAll 列表,`SecurityConfig` 据此放行 |
| `/chat` `/stream` | 无 | 走 JWT 过滤链,未登录 → 401 |

> **实测**:`/ping` → `200 {"msg":"AgentScope Java v2.0.4 ready","code":200}`;`/chat` 无 token → `{"msg":"请求访问：/agentscope/chat，认证失败，无法访问系统资源","code":401}`。

### 4.2 `AgentScopeConfig` —— 循环依赖踩坑与规避

#### 4.2.1 问题现象

初版实现:

```java
@Configuration
public class AgentScopeConfig {
    @Autowired
    private ReActAgent agentscopeReActAgent;      // ① 按类型注入

    @Bean("assistantAgent")
    @Primary
    public ReActAgent assistantAgent() {           // ② 同类型 Bean
        return agentscopeReActAgent;
    }
}
```

启动报错:

```
***************************
APPLICATION FAILED TO START
***************************
The dependencies of some of the beans in the application context form a cycle:

┌──->──┐
|  agentScopeConfig (field private io.agentscope.core.ReActAgent com.basic.agentscope.config.AgentScopeConfig.agentscopeReActAgent)
└──<-──┘
```

#### 4.2.2 成因

1. 容器中 `ReActAgent` 类型有**两个**候选:自动装配的 `agentscopeReActAgent`、本类声明的 `assistantAgent`;
2. `@Autowired` 按类型注入时,Spring 优先选取**优先级最高**的候选 —— `@Primary` 标在 `assistantAgent` 上,于是选中了它;
3. 但 `assistantAgent` 正处于"创建中"(它属于 `agentScopeConfig` 这个正在创建的配置类)→ 回头去取 `agentScopeConfig` → 自我依赖 → 循环。

**所以问题不是"字段注入"本身,而是"声明了与自动装配同类型的 Bean"。**

#### 4.2.3 正确做法

```java
@Configuration
public class AgentScopeConfig implements InitializingBean {

    /** 自动装配的默认 Toolkit(Bean 名:agentscopeToolkit) */
    @Autowired
    private Toolkit toolkit;

    @Override
    public void afterPropertiesSet() {
        // 在这里注册业务自定义工具(待 M2 实施)
    }
}
```

| 规则 | 说明 |
|---|---|
| **R1** | **不**声明与自动装配同类型的 `@Bean`(`ReActAgent` / `Toolkit` / `Memory` / `OpenAIChatModel`) |
| **R2** | 需要"业务别名"时,用 `@Qualifier("agentscopeReActAgent")` 注入,而非新建 Bean |
| **R3** | 确需替换某个自动装配 Bean 时,声明**同类型** Bean 即可覆盖(Spring Boot 允许自定义 Bean 覆盖自动配置),但**只能有一个** |
| **R4** | 工具注册放在 `afterPropertiesSet()`,此时 `Toolkit` 已注入完毕 |

> 本项目 `Toolkit` 类型只有自动装配的 `agentscopeToolkit` 一个候选,因此 `@Autowired Toolkit` 注入安全。

### 4.3 AgentScope Java 2.x 核心 API 速查(实测自 v2.0.4 jar)

> 以下签名均通过 `javap` 直接反编译 `agentscope-core-2.0.4.jar` / `agentscope-extensions-model-openai-2.0.4.jar` 确认。

#### 4.3.1 `ReActAgent`(`io.agentscope.core.ReActAgent`)

```java
public class ReActAgent extends AgentBase implements AutoCloseable {
    public static ReActAgent.Builder builder();
    public Mono<Msg>  call(String userInput);
    public Mono<Msg>  call(String userInput, Class<T> structuredOutputType);
    public Mono<Msg>  call(List<Msg> msgs);
    public Mono<Msg>  call(List<Msg> msgs, RuntimeContext ctx);
    public Flux<AgentEvent> streamEvents(String userInput);
    public Flux<AgentEvent> streamEvents(Msg msg);
    public Flux<AgentEvent> streamEvents(List<Msg> msgs);
    public Flux<AgentEvent> streamEvents(String userInput, RuntimeContext ctx);
    public Flux<Event> stream(List<Msg> msgs, StreamOptions opts, RuntimeContext ctx);
    public AgentRun<Msg> prepareCall(List<Msg> msgs, RuntimeContext ctx);
    public Toolkit getToolkit();
    public Model getModel();
    public String getSysPrompt();
    public int getMaxIters();
    public AgentState getAgentState();
    public void interrupt();
    public void clearStateCache();
}
```

**Builder 常用方法**:`name(String)` / `description(String)` / `sysPrompt(String)` / `model(Model)` / `model(String providerId)` / `toolkit(Toolkit)` / `maxIters(int)` / `generateOptions(GenerateOptions)` / `hook(...)` / `middleware(...)` / `stateStore(...)`。

#### 4.3.2 消息模型(`io.agentscope.core.message`)

| 类 | 用途 | 关键 API |
|---|---|---|
| `Msg` | 消息容器 | `Msg.builder()` → `.role(MsgRole)` `.name(String)` `.id(String)` `.content(ContentBlock)` `.content(List<ContentBlock>)` `.textContent(String)` `.metadata(Map)` `.timestamp(String)` `.usage(ChatUsage)` → `.build()`;`getTextContent()` |
| `MsgRole` | 角色枚举 | `USER` / `ASSISTANT` / `SYSTEM` / `TOOL` |
| `TextBlock` | 纯文本内容块 | `TextBlock.builder().text(String).build()`;`getText()` |
| `ContentBlock` | 内容块基类 | `TextBlock` / `ToolUseBlock` / `ToolResultBlock` / `ImageBlock` / `AudioBlock` / `VideoBlock` / `Base64Source` 等 |

> ⚠️ **v1 → v2 破坏性变更**:v1 的 `Message` / `Message.Role` 在 v2 中已改为 `Msg` / `MsgRole`;v1 的 `Message.of(...)` 静态工厂在 v2 中不存在。

#### 4.3.3 模型

| 类 | 包 | 说明 |
|---|---|---|
| `OpenAIChatModel` | `io.agentscope.extensions.model.openai` | **不在 core 里**;`extends ChatModelBase` |
| `OpenAIClient` | `io.agentscope.extensions.model.openai` | HTTP 客户端 |
| `OpenAIModelProvider` | `io.agentscope.extensions.model.openai` | `implements ModelProvider`;`providerId()` / `supports(String)` / `create(String)` |
| `Model` | `io.agentscope.core.model` | 模型抽象接口,`ReActAgent.Builder.model()` 接受此类型 |
| `GenerateOptions` | `io.agentscope.core.model` | 生成参数(温度、max tokens 等) |

`OpenAIChatModel.Builder` 方法:`apiKey` / `modelName` / `baseUrl` / `endpointPath` / `stream` / `generateOptions` / `formatter` / `httpTransport` / `proxy` / `contextWindowSize` / `nativeStructuredOutput` / `nativeStructuredOutputWithTools`。

> 本分支**不**手工构建 `OpenAIChatModel`——由 `OpenAIAutoConfiguration` 按 `OpenAIProperties` 构建,并支持 `OpenAIChatModelBuilderCustomizer` 扩展点。

#### 4.3.4 记忆与工具

| 类 | 包 | 说明 |
|---|---|---|
| `Memory` | `io.agentscope.core.memory` | 记忆接口(**已标记 deprecated**,属过渡态) |
| `InMemoryMemory` | `io.agentscope.core.memory` | 内存实现;无参构造;`addMessage` / `getMessages` / `clear` |
| `LongTermMemory` | `io.agentscope.core.memory` | 长期记忆接口 |
| `Toolkit` | `io.agentscope.core.tool` | 工具注册中心;`registerTool` 等 |

#### 4.3.5 自动装配类(来自 starter)

| 类 | 产出的 Bean |
|---|---|
| `io.agentscope.spring.boot.AgentscopeAutoConfiguration` | `agentscopeMemory`(`Memory`)/ `agentscopeToolkit`(`Toolkit`)/ `agentscopeReActAgent`(`ReActAgent`) |
| `io.agentscope.spring.boot.openai.OpenAIAutoConfiguration` | `openAIChatModel`(`OpenAIChatModel`) |

**Bean 名很重要**:业务侧按类型注入可行,但如需按名区分,必须用上表中的确切 bean 名。

---

### 4.4 能力注册表体系(v0.2 新增)

#### 4.4.1 `CapabilityInfo` —— 能力描述模型

```java
public record CapabilityInfo(
        String id,             // 能力唯一标识(前后端契约)
        String name,           // 中文名
        String category,       // core / external
        String categoryLabel,  // 核心能力 / 外部依赖型
        String summary,        // 一句话说明
        String icon,           // Element Plus 图标名
        String requires,       // 外部依赖说明(核心能力为 null)
        boolean enabled,       // 是否就绪
        boolean supportsStream,// 是否支持 SSE
        String chatEndpoint,   // 专用对话地址(通用接口为 null)
        String detailEndpoint, // 详情地址
        List<ParamInfo> params // 入参说明
) { ... }
```

`ParamInfo` 为嵌套 record,提供 `required` / `text` / `textarea` / `select` 四个工厂方法,
让每个能力的入参声明保持一行内可读。

#### 4.4.2 `CapabilityProvider` —— 能力接口

```java
public interface CapabilityProvider {
    CapabilityInfo descriptor();                 // 必实现
    default String agentBeanName() { return null; } // 无 Agent 的能力返回 null
    default Map<String, Object> detail() { return Map.of(); }
}
```

#### 4.4.3 `CapabilityRegistry` —— 注册表

实现 `InitializingBean`,在 `afterPropertiesSet()` 中:

1. 取容器内全部 `CapabilityProvider`(`@Autowired List<CapabilityProvider>`);
2. 按 `category` → `id` 排序;
3. 存入 `LinkedHashMap<String, CapabilityInfo> byId` 与 `Map<String, CapabilityProvider> providerById`。

> **实现坑**:`Collection` 没有 `sort()`。必须先 `new ArrayList<>(byId.values())` 再 `sort(...)`,
> 最后回填 `LinkedHashMap` —— 不能对 `Map.values()` 直接排序,也不能对它 `stream()` 后再 `entrySet()`。

#### 4.4.4 `CapabilityController` —— 通用出口

关键方法:

```java
private ReActAgent resolveAgent(CapabilityProvider provider) {
    String beanName = provider.agentBeanName();
    if (beanName == null) return null;
    return applicationContext.getBean(beanName, ReActAgent.class);   // 按名取,绝不按类型
}
```

工具调用轨迹提取(`extractToolCalls`):把 `ToolUseBlock`(调用)与 `ToolResultBlock`(返回)
**按 id 配对合并**成一条记录,前端表格可直接渲染 `name / input / output` 三列。

> `ToolResultBlock.getOutput()` 返回的是 `List<ContentBlock>`,需要自行提取文本:
> 逐块判断 `instanceof TextBlock` 取 `getText()`。

#### 4.4.5 能力实例模板

```java
@Configuration
public class XxxCapability implements CapabilityProvider {

    public static final String AGENT_BEAN = "xxxCapabilityAgent";

    @Bean(AGENT_BEAN)
    public ReActAgent xxxCapabilityAgent(ModelResolver modelResolver) {
        return ReActAgent.builder()
                .name("xxx-agent")
                .sysPrompt("...")
                .model(modelResolver.resolve())   // 统一模型出口
                .maxIters(5)
                .build();
    }

    @Override public CapabilityInfo descriptor() { ... }
    @Override public String agentBeanName() { return AGENT_BEAN; }
    @Override public Map<String, Object> detail() { ... }
}
```

#### 4.4.6 `ModelResolver` —— 模型统一出口

| 场景 | 取值 |
|---|---|
| `agentscope.openai.enabled=true` 且配了 `api-key` | `@Qualifier("openAIChatModel")`(OpenAI 兼容协议) |
| 否则 / `agentscope.mock.enabled=true` | 内置 `MockChatModel` 单例(**非 Spring Bean**) |

> ⚠️ `MockChatModel` **不能**注册成 Spring Bean:一旦容器里出现第二个 `Model` 类型 Bean,
> starter 的 `@ConditionalOnMissingBean(Model.class)` 就不再创建 `openAIChatModel`,
> `AgentscopeAutoConfiguration` 随即因 `Model` 注入歧义而启动失败。

#### 4.4.7 `MockChatModel` —— 无 Key 跑通链路

`Model` SPI 实现:`Flux<ChatResponse> stream(List<Msg>, List<ToolSchema>, GenerateOptions)`。

行为序列:

| 轮次 | 判定条件 | 行为 |
|---|---|---|
| 1 | 上下文含 JSON Schema 片段 | 直接合成一个**合法 JSON 对象**返回(支撑结构化输出演示) |
| 2 | 无 `ToolResultBlock` 且有工具 | 返回一个 `ToolUseBlock` → **真实触发工具执行** |
| 3 | 已有工具结果 | 把工具结果汇总成最终文本 |

工具选择策略:先查**关键词表**(工具名 → 触发词,如 `query_sys_user` → `用户/账号/管理员`),
未命中再退化为"工具名 + 描述"与问题的 2 字滑窗匹配,最后兜底取第一个工具。

入参生成:读工具的 JSON Schema `properties`,按参数名给语义化默认值
(`keyword/username/name` → `admin`,`pageNum` → `1`,`pageSize` → `10`,其余 string → `mock`)。

> **为什么 Mock 值得做**:它把"模型决策"换成"关键词匹配",但**工具执行、结果回灌、ReAct 循环全是真实的**。
> 没有 LLM Key 也能验证整条链路与前端交互。

---

### 4.5 前端能力展厅设计(v0.2 新增)

#### 4.5.1 目录结构

```
web-ui/src/views/basic-extend/agent/
├── index.vue                     # 容器:左侧导航 + 右侧面板
├── panels/
│   ├── index.js                  # 能力 id → 面板组件 映射表
│   ├── GenericPanel.vue          # 兜底面板(后端有能力但前端无专用面板)
│   ├── ChatPanel.vue / ToolPanel.vue / ... (21 个)
└── components/
    ├── CapabilityChatBox.vue     # 通用对话外壳(输入/会话/SSE/结果)
    └── CapabilityDetailCard.vue  # 通用详情卡片
```

#### 4.5.2 容器与导航

- 左侧 `el-menu` 按 `categoryLabel` 分组(`el-menu-item-group`),数据来自 `/agentscope/capability/list`;
- `!enabled` 的能力在菜单项后加"未就绪"标签;
- 右侧 `<component :is="currentPanel" :capability="current" />`,由 `panels/index.js` 的映射决定;
- 映射缺失时回落 `GenericPanel`,**不得白屏**。

#### 4.5.3 `CapabilityChatBox` —— 通用对话外壳

| 能力 | 说明 |
|---|---|
| `v-model:prompt` | `defineModel('prompt')`,父组件可预填 / 回读 |
| `defaultPrompt` | 未传 v-model 时的初始值 |
| `endpoint` | 覆盖默认的 `/agentscope/capability/{id}/chat` |
| `supportsStream` | 是否显示"SSE 流式"开关 |
| `extraOptions` | 随请求下发的额外 `options` |
| `showSession` | 是否显示会话 ID 输入 |

结果区展示:模型标签(Mock / 真实)、agent 名、模型名、耗时、会话 ID、回答文本、
**工具调用表格**(name / input / output)、结构化输出 JSON。

#### 4.5.4 SSE 解析(`web-ui/src/api/agentscope.js`)

`EventSource` **不支持自定义请求头**,无法携带 `Authorization`,因此改用 `fetch` + `ReadableStream`:

```js
const response = await fetch(url, { headers: { Authorization: 'Bearer ' + getToken() }, signal })
const reader = response.body.getReader()
const decoder = new TextDecoder('utf-8')
let buffer = ''
for (;;) {
  const { done, value } = await reader.read()
  if (done) break
  buffer += decoder.decode(value, { stream: true })
  let idx
  while ((idx = buffer.indexOf('\n\n')) !== -1) {   // SSE 以空行分帧
    const rawEvent = buffer.slice(0, idx)
    buffer = buffer.slice(idx + 2)
    const dataLines = rawEvent.split('\n').filter(l => l.startsWith('data:')).map(l => l.substring(5).trim())
    if (dataLines.length) onMessage(dataLines.join('\n'))
  }
}
```

> `AbortController` 用于"停止"按钮,取消底层 fetch。

---

## 5. 配置详细设计

```yaml
# ============================================================
# AgentScope Java 2.x 集成配置
# （属性名由 io.agentscope.spring.boot.properties.*Properties 定义）
# ============================================================
agentscope:
  # 总开关：true 时装配 agentscopeReActAgent / Memory / Toolkit Bean
  agent:
    enabled: true
    name: basic-assistant
    sys-prompt: 你是一个企业级 Java 基础平台的智能助手，请简洁准确地回答用户问题。
  # 模型侧：OpenAI 兼容协议（DeepSeek / Kimi / 智谱 / Azure OpenAI 都用它）
  model:
    provider: openai
  openai:
    enabled: true
    base-url: ${OPENAI_BASE_URL:https://api.openai.com/v1}
    api-key: ${OPENAI_API_KEY:sk-replace-me-in-env}
    model-name: ${OPENAI_MODEL:gpt-4o-mini}
    stream: true
  # Mock 模型开关：true 时不调用真实 LLM，用内置 Mock 模型把
  # 「工具调用 → ReAct 循环 → 结果回灌 → 最终回答」整条链路跑通。
  # 配好真实 OPENAI_API_KEY 后置为 false 即可切到真实模型。
  mock:
    enabled: ${AGENTSCOPE_MOCK_ENABLED:true}
  # ---- 以下 4 项为「外部依赖型」能力开关，默认关闭 ----
  mcp:
    enabled: false        # 需要可用的 MCP Server 地址
  channel:
    enabled: false        # 需要引入 agentscope-extensions-channel-* 并配置 IM 凭证
  sandbox:
    enabled: false        # 需要容器运行时(Docker / K8s)做真正隔离
  scheduler:
    enabled: false        # 需要与项目既有 Quartz 打通(本分支为进程内实现)
```

### 5.1 属性绑定类

| Properties 类 | 前缀 | 字段 |
|---|---|---|
| `io.agentscope.spring.boot.properties.AgentscopeProperties` | `agentscope` | `agent`(`AgentProperties`) / `model`(`ModelProperties`) |
| `io.agentscope.spring.boot.properties.AgentProperties` | `agentscope.agent` | `enabled` / `name` / `sysPrompt` / `maxIters` |
| `io.agentscope.spring.boot.properties.ModelProperties` | `agentscope.model` | `provider` |
| `io.agentscope.spring.boot.openai.OpenAIProperties` | `agentscope.openai` | `enabled` / `apiKey` / `modelName` / `baseUrl` / `endpointPath` / `stream` |
| `com.basic.agentscope.config.AgentScopePropertiesConfig`(本项目) | `agentscope.mcp` / `agentscope.channel` / `agentscope.sandbox` / `agentscope.scheduler` | 各外部依赖型能力的开关与参数 |

### 5.2 `provider` 与 `enabled` 的配套关系

`AgentscopeAutoConfiguration.agentscopeReActAgent(Model, ...)` 需要一个 `Model` Bean。`Model` 由 `OpenAIAutoConfiguration.openAIChatModel(...)` 产出,该 Bean 的创建条件依赖 `agentscope.openai.enabled` 与 `agentscope.model.provider=openai`。

> **配置铁律**:`agentscope.model.provider` 与 `agentscope.openai.enabled` **必须成对出现**。只配 provider 不配 openai.enabled(或反之),会导致 `Model` Bean 缺失 → `agentscopeReActAgent` 创建失败 → **应用启动失败**。

> **Mock 模式说明**:`ModelResolver` 在没有真实模型可用时回落到内置 `MockChatModel`,
> 因此 `agentscope.openai.enabled=false` **不会**导致启动失败(只要 provider/openai 仍配齐或 Mock 生效)。
> `agentscope.mock.enabled=false` 且没有真实 Key 时,能力对话会返回明确的错误提示。

### 5.3 环境变量

| 变量 | 用途 | 必填 |
|---|---|---|
| `OPENAI_BASE_URL` | API 根地址 | 生产必填 |
| `OPENAI_API_KEY` | API 密钥 | **生产必填** |
| `OPENAI_MODEL` | 模型名 | 按需 |
| `AGENTSCOPE_MOCK_ENABLED` | Mock 模型开关 | 否(默认 `true`) |

---

## 6. 升级与迁移指引

### 6.1 升级 AgentScope 版本

| 步骤 | 操作 |
|---|---|
| 1 | 查 [Releases](https://github.com/agentscope-ai/agentscope-java/releases) 确认目标版本为 **GA**(非 BETA) |
| 2 | 改根 `pom.xml` 的 `<agentscope.version>` |
| 3 | `mvn clean install` —— **必须 clean**(见 §7 RK-5) |
| 4 | 核对 §4.3 API 速查表是否有签名变更 |
| 5 | 启动 + `GET /agentscope/ping` 验证 |

### 6.2 v1 → v2 迁移映射(若从 v1 迁移)

| v1 | v2 |
|---|---|
| `io.agentscope.core.message.Message` | `io.agentscope.core.message.Msg` |
| `Message.Role.USER` | `MsgRole.USER` |
| `Message.of(userInput)` | `Msg.builder().role(MsgRole.USER).textContent(userInput).build()` 或 `agent.call(String)` |
| `io.agentscope.core.model.OpenAIModel` | `io.agentscope.extensions.model.openai.OpenAIChatModel` |
| `io.agentscope.core.model.OpenAIChatModel` | 同上(包名变更) |
| `Memory` 接口 | 仍在,但已 deprecated |

### 6.3 切换模型厂商

见《架构方案》§3.3 厂商切换矩阵。**OpenAI 兼容厂商零代码改动**;非兼容协议需加对应 `agentscope-extensions-model-*` 与 starter 依赖。

---

## 7. 故障排查

| # | 现象 | 根因 | 处理 |
|---|---|---|---|
| **F-1** | 启动报 `VerifyError: Bad type on operand stack`,指向 `com/basic/framework/config/JacksonConfig` | `basic-framework/target/classes` **残留了旧分支的 `JacksonConfig.class`**(当前源码已改为 `BigNumberModuleConfig`,无 `JacksonConfig.java`) | **`mvn clean install`**,清掉陈旧 class |
| **F-2** | 启动报 `form a cycle: agentScopeConfig` | 重复声明了与自动装配同类型的 `ReActAgent` Bean | 见 §4.2,删掉重复声明 |
| **F-3** | 启动报 `NoSuchBeanDefinitionException: Model` | `agentscope.model.provider` 与 `agentscope.openai.enabled` 未成对配置 | 见 §5.2 |
| **F-4** | 启动报 `ClassNotFoundException` / `UnsupportedClassVersionError` | 用 JDK 11 构建 AgentScope 2.x(需 17+) | 用 JDK 21 构建(`JAVA_HOME=C:\Users\admin\.jdks\ms-21.0.9`) |
| **F-5** | `/ping` 返回 401 | `@Anonymous` 未加或被移除 | 确认 `AgentController#ping` 上有 `@Anonymous` |
| **F-6** | `/ping` 返回 `vunknown` | 从 IDE 的 `target/classes` 直接运行,Maven resource filtering 未执行 | 用 Maven 构建产物运行 |
| **F-7** | `/chat` 长时间无响应 | 模型端点不可达 / 密钥无效 | 核对 `base-url` / `api-key`;确认网络可达 |
| **F-8** | 启动报 `No qualifying bean of type 'io.agentscope.core.ReActAgent'` | **自建 `ReActAgent` Bean 触发了 starter 的 `@ConditionalOnMissingBean`,导致默认智能体消失** | 显式声明 `basicAssistantAgent` 补齐默认智能体(见 §4.4.5) |
| **F-9** | 工具注册了但 Agent 调不到 | starter 的 `agentscopeToolkit` 是 **`@Scope("prototype")`**,工具注册在某个注入点上,Agent 拿到的是另一个实例 | 自建**单例** `businessToolkit` 并在 Agent 上显式 `.toolkit(businessToolkit)` |
| **F-10** | 启动报 `NoUniqueBeanDefinitionException: Model` | `MockChatModel` 被注册成了 Spring Bean,容器里出现两个 `Model` | Mock 模型**不注册为 Bean**,由 `ModelResolver` 持有单例 |
| **F-11** | 启动报 `NoUniqueBeanDefinitionException: ReActAgent` | 有代码按**类型**注入了 `ReActAgent` | 一律加 `@Qualifier`,或用 `getBean(beanName, ReActAgent.class)` |
| **F-12** | 前端能力展厅空白 / 侧边栏空 | 菜单 `sys_menu` 未插入,或 `component` 路径写错 | 确认已导入 `file-sql/basicdb-vue3.sql`(含 1067/1068 两条菜单);`component` 须为 `basic-extend/agent/index` |
| **F-13** | 前端控制台 `panels/index.js` 导入报错 | 映射表里列了组件但文件没建 | 补齐文件;或临时改用 `GenericPanel` 兜底 |
| **F-14** | SSE 一直转圈不出字 | 前端用 `EventSource` 发请求 | `EventSource` 不支持请求头,改用 `fetch` + `ReadableStream`(见 §4.5.4) |
| **F-15** | 能力对话返回"能力不可用" | 外部依赖型能力默认无 Agent Bean | 属预期行为;配置对应依赖后由 `reload` 接口重新装配 |

---

## 8. 构建与验证

```powershell
# JDK 21
$env:JAVA_HOME = 'C:\Users\admin\.jdks\ms-21.0.9'
$env:Path = "$env:JAVA_HOME\bin;D:\software\apache-maven-3.8.1-bin\apache-maven-3.8.1\bin;$env:Path"

# 全量构建(必须 clean)
mvn -B clean install -DskipTests

# 启动
cd basic-admin-api
java -jar target\basic-admin-api.jar --server.port=8080

# 探活
curl http://127.0.0.1:8080/agentscope/ping
# → {"msg":"AgentScope Java v2.0.4 ready","code":200}

# 能力清单(需 JWT)
curl -H "Authorization: Bearer <token>" http://127.0.0.1:8080/agentscope/capability/list

# 单个能力对话(需 JWT)
curl -X POST -H "Authorization: Bearer <token>" -H "Content-Type: application/json" `
  -d '{"prompt":"系统里有多少用户？"}' `
  http://127.0.0.1:8080/agentscope/capability/tool/chat

# 前端构建
cd web-ui
pnpm run build:prod
```

**独立冒烟入口**(`src/test`):`com.basic.agentscope.smoke.AgentscopeSmokeApp` —— 只加载 `extend-agentscope` 自身上下文,打印 AgentScope 版本、Agent / Model / Toolkit 实际类型,用于排除项目其他模块干扰后快速验证框架接入。

---

## 9. 参考来源

- 《需求文档》《架构方案》《概要设计》《数据库设计》(同目录)
- AgentScope Java v2.0.4 源码:<https://github.com/agentscope-ai/agentscope-java/tree/v2.0.4>
- 官方文档:<https://java.agentscope.io/v2/zh/docs/quickstart>

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:8 项变更清单、根 pom 逐段详解(双 BOM 分工与优先级)、子模块依赖、Java 代码详细设计(注入 / 版本解析 / 阻塞超时 / SSE / 鉴权)、**循环依赖踩坑完整复盘与 4 条规避规则**、AgentScope 2.x API 速查(基于 `javap` 实测反编译)、配置属性绑定类与配套关系铁律、升级迁移指引(v1→v2 映射表)、7 类故障排查、构建验证步骤 |
| v0.2 | 2026-10-23 | 能力展厅专题:§1 变更清单扩到 28 项;新增 §4.4 能力注册表体系(`CapabilityInfo` / `CapabilityProvider` / `CapabilityRegistry` / `CapabilityController` / 能力实例模板 / `ModelResolver` / `MockChatModel` 七小节);新增 §4.5 前端能力展厅设计(目录结构 / 容器 / `CapabilityChatBox` / SSE 解析);§5 配置补 `mock.enabled` 与 4 个外部依赖型开关、Mock 模式说明;§7 故障排查由 7 条扩到 15 条(新增 F-8~F-15);§8 补能力清单与单能力对话的验证命令 |
