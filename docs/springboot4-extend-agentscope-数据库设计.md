# 基础平台(base-postgresql-vue3) 数据库设计 — `springboot4-extend-agentscope` 分支

> 文档版本:v0.2 · 状态:全量能力集成完成
> 适用范围:仓库 **`springboot4-extend-agentscope`** 分支
> 上游依赖:`springboot4` 分支(**Spring Boot 4.1.1 / JDK 21 / Jackson 3**)
> 配套:《需求文档》《架构方案》《概要设计》《详细设计》

---

## 1. 结论先行

> **本分支对数据库的变更量 = 2 条 `sys_menu` 菜单记录(数据,非 DDL)。**
>
> - **不新增任何表 / 字段 / 索引 / 约束 / 触发器 / 函数**;
> - **不修改任何既有 DDL**,**不执行任何数据迁移**;
> - 既有 schema(本分支实际使用 `basicdb_agentscope`)的 **34 张表 + 5 个函数**
>   完全保持 `springboot4` 基线原样;
> - 新增的 2 条记录用于把「AgentScope 能力展厅」挂到 RuoYi 的动态路由体系上
>   (目录「扩展功能」+ 菜单「AgentScope 能力展厅」)。

> **关于 schema 名**:本分支实际使用的 schema 为 `basicdb_agentscope`
> (由 `spring.application.datasource.druid.master.url` 的 `currentSchema` 指定)。
> 它与 `basicdb` 结构完全一致,区别仅在于库名 —— 目的是让 AgentScope 的演示数据
> 与其它分支隔离。菜单脚本按该 schema 书写;若部署到 `basicdb`,把脚本里的
> `basicdb_agentscope` 替换为 `basicdb` 即可,SQL 逻辑不变。

| 变更 | 类型 | 对象 | 脚本 |
|---|---|---|---|
| 菜单 1067 | INSERT | `<schema>.sys_menu`(目录 `M`,path=`basic-extend`) | `file-sql/create-agentscope-capability-menu.sql` |
| 菜单 1068 | INSERT | `<schema>.sys_menu`(菜单 `C`,component=`basic-extend/agent/index`) | 同上 |

> 脚本**幂等**:以 `WHERE NOT EXISTS(menu_id = ...)` 保护,可重复执行。

---

## 2. 为什么不需要动数据库

### 2.1 本分支实际用到的持久化

| 组件 | 是否落库 | 说明 |
|---|---|---|
| `agentscopeReActAgent` | ❌ | Agent 定义在内存,由配置驱动 |
| `agentscopeToolkit` | ❌ | 工具注册在内存,启动时构建 |
| `openAIChatModel` | ❌ | 模型客户端无状态 |
| `agentscopeMemory` | ❌ **当前** | 默认实现是 `InMemoryMemory`,进程内存储 |
| **能力注册表 `CapabilityRegistry`** | ❌ | 21 个 `CapabilityProvider` 在启动时汇总到内存 `LinkedHashMap` |
| **会话状态 `stateStore`** | ❌ **当前** | `InMemoryAgentStateStore` / 自研文件版,进程内或本地文件 |
| **长期记忆 `longTermMemory`** | ❌ **当前** | 自研内存版 `InMemoryLongTermMemory` |
| **RAG 知识库** | ❌ **当前** | 自研内存版 `SimpleKeywordKnowledge` |
| **调度任务** | ❌ **当前** | 进程内 `ScheduledExecutorService` |
| 业务数据(用户 / 角色 / 菜单 / 字典等) | ✅ | **沿用既有 34 张表,零改动** |
| 操作日志 / 登录日志 | ✅ | **沿用既有 `sys_oper_log` / `sys_logininfor`,零改动** |
| 能力展厅菜单 | ✅ | **新增 2 条 `sys_menu` 记录**(见 §1) |

### 2.2 AgentScope 2.x 的可选持久化能力(**未启用**)

AgentScope 官方提供了多种存储扩展,可在后续按需引入:

| 扩展 artifactId | 用途 | 本分支状态 |
|---|---|---|
| `agentscope-extensions-postgresql` | **PostgreSQL** 状态 / 会话 / 记忆存储 | ❌ 未引入 |
| `agentscope-extensions-jdbc` | 通用 JDBC 存储 | ❌ 未引入 |
| `agentscope-extensions-mysql` | MySQL 存储 | ❌ 未引入 |
| `agentscope-extensions-mongodb` | MongoDB 存储 | ❌ 未引入 |
| `agentscope-extensions-redis` | Redis 存储 / 分布式状态 | ❌ 未引入 |
| `agentscope-extensions-rag` | RAG 知识库(通常需配向量库) | ❌ 未引入 |
| `agentscope-extensions-mem` | 记忆存储后端 | ❌ 未引入 |
| `agentscope-extensions-skills` | Skill 仓库 | ❌ 未引入 |

> 本项目数据库是 **PostgreSQL**(与 `master` 分支的 MySQL 不同),因此若将来接入持久化,应选 `agentscope-extensions-postgresql`,而非 `-mysql`。

### 2.3 为什么"先内存、后落库"是合理的

| 理由 | 说明 |
|---|---|
| **需求未到** | 当前目标只是打通框架集成底座(G1~G6),未确定是否需要跨重启的会话记忆 |
| **AgentScope 有标准解** | 官方 `agentscope-extensions-postgresql` 已提供规范的表结构,不必自研 |
| **表结构需配套治理** | Agent 会话表是**高频写入 + 需 TTL 清理**的,与既有 34 张业务表的治理模型不同,混在一起会污染基线 |
| **可回退** | `Memory` 是可替换的 SPI,后续接入存储只需换实现,业务代码不动 |

---

## 3. 既有数据库基线(**本分支零变更**)

### 3.1 概况

| 项 | 内容 |
|---|---|
| 数据库 | PostgreSQL ≥ 15.1 |
| Schema | `basicdb_agentscope`(结构与 `basicdb` 一致,当前分支实际使用的库) |
| 表数量 | **34 张**(与 `springboot4` 基线一致) |
| 函数数量 | **5 个** |
| 变更 | **无** |

### 3.2 本分支与 AgentScope 的数据交互面

| 场景 | 是否触及 AgentScope 存储 | 说明 |
|---|---|---|
| 用户登录 / JWT 签发 | ❌ | 走既有 `sys_user` / `sys_logininfor` |
| 菜单 / 角色 / 字典查询 | ❌ | 走既有业务表,Agent 侧只读(待 M2 工具化) |
| 定时任务 | ❌ | 走既有 Quartz + `sys_job` |
| 操作日志 | ❌ | 走既有 `sys_oper_log`(`LogAspect` 切面) |
| `/agentscope/ping` | ❌ | 纯内存返回版本号 |
| `/agentscope/chat` | ❌ | 仅调用外部 LLM,会话记忆在内存 |

> **结论**:AgentScope 集成在数据库层面与既有 34 张表**完全解耦**,不存在 schema 冲突、事务边界交叉或数据迁移风险。

---

## 4. 后续演进:会话记忆落库方案(预留,**本分支不实施**)

> 以下为 M3 阶段的预留设计,供后续分支参考。**本分支不建表、不加扩展依赖。**

### 4.1 方案选型

| 方案 | 优点 | 缺点 | 建议 |
|---|---|---|---|
| **A. `agentscope-extensions-postgresql`** | 官方维护,表结构规范,与 AgentScope 版本同步升级 | 需接受官方表结构 | ✅ **推荐** |
| **B. 自研 `sys_agent_*` 表** | 完全贴合项目命名与治理规范 | 需自行实现 `AgentStateStore` SPI,升级易脱节 | ⚠️ 备选 |
| **C. Redis** | 高性能,TTL 天然 | 持久性弱,审计不友好 | ⚠️ 适合缓存层而非主存储 |

### 4.2 方案 A 预期引入的表(以官方扩展实际 DDL 为准)

> ⚠️ **表名与字段以引入时官方扩展的实际 DDL 为准**,此处仅示意,不作为实施依据。

| 逻辑对象 | 用途 | 关注点 |
|---|---|---|
| Agent 状态表 | 持久化 `AgentState`(会话上下文、迭代计数等) | 主键需含 `agent_id` + `session_id` |
| 消息 / 事件表 | 持久化对话消息与流式事件 | 高频写入,需按时间分区或定期清理 |
| 记忆表 | 长期记忆条目 | 需支持向量或结构化检索(视 `mem` 扩展而定) |

### 4.3 若走方案 B(自研)的建表约束

| 约束 | 要求 |
|---|---|
| 命名 | 统一 `sys_agent_` 前缀,与既有 `sys_` 规范一致 |
| Schema | `basicdb_agentscope`,与既有表同 schema |
| 审计字段 | `create_by` / `create_time` / `update_by` / `update_time` / `remark`,与既有表一致 |
| 逻辑删除 | `del_flag`(0 存在 / 2 删除),与既有表一致 |
| 索引 | 按 `agent_id` + `session_id` 建联合索引;时间列建索引支撑清理 |
| 清理策略 | 定时任务(复用既有 Quartz)按 TTL 清理,单次批量上限可控 |
| 事务 | 不与业务事务交叉;Agent 写入失败**不得**影响主业务链路 |

### 4.4 引入持久化时的前置检查清单

| # | 检查项 |
|---|---|
| 1 | 确认目标 AgentScope 版本的 `agentscope-extensions-postgresql` 存在且与 BOM 版本一致 |
| 2 | 确认其 DDL 与本项目 PostgreSQL 版本、schema 规范兼容 |
| 3 | 评估表数量增长对既有 34 表基线文档的影响,同步更新《数据库设计》 |
| 4 | 确认清理任务的调度归属(`sys_job` 或 `agentscope-extensions-scheduler`) |
| 5 | 确认 `Memory` 替换后 `agentscopeReActAgent` 的自动装配仍走通(覆盖自动配置的 `Memory` Bean) |

---

## 5. 数据安全

| 项 | 要求 | 本分支状态 |
|---|---|---|
| API Key | **不入库**;走环境变量 / 配置中心 | ✅ 未入库 |
| 对话内容 | 当前存于内存,不落库 | ✅ 未落库 |
| 日志脱敏 | AgentScope 日志不打印 API Key | 由框架负责 |
| 审计 | Agent 调用若需审计,后续走既有 `sys_oper_log` | 待 M2 评估 |

---

## 6. 变更清单

| 类型 | 内容 |
|---|---|
| **新增表** | 无 |
| **修改表** | 无 |
| **新增字段** | 无 |
| **新增索引 / 约束** | 无 |
| **触发器 / 函数** | 无 |
| **数据迁移** | 无 |
| **菜单数据** | **新增 2 条 `sys_menu` 记录**(1067 目录 / 1068 菜单) |
| **SQL 脚本** | `file-sql/create-agentscope-capability-menu.sql`(新增,幂等 INSERT) |
| **既有 34 表 + 5 函数** | **零改动** |

### 6.1 `sys_menu` 记录明细

| menu_id | parent_id | menu_name | menu_type | path | component | perms | icon | order_num |
|---|---|---|---|---|---|---|---|---|
| 1067 | 0 | 扩展功能 | `M` | `basic-extend` | *(空)* | *(空)* | `component` | 4 |
| 1068 | 1067 | AgentScope 能力展厅 | `C` | `agent` | `basic-extend/agent/index` | `agentscope:capability:list` | `cpu` | 1 |

**字段口径**(对齐 `sys_menu` 实际 DDL):

| 字段 | 取值 | 含义 |
|---|---|---|
| `menu_type` | `M` / `C` | `M`=目录(前端渲染为可展开分组),`C`=菜单(渲染为路由页面) |
| `component` | 目录为 `NULL`,菜单为 `basic-extend/agent/index` | 相对 `web-ui/src/views/` 的路径,不带 `.vue` |
| `is_frame` | `1` | 非外链 |
| `is_cache` | `0` | 不缓存页面 |
| `visible` | `0` | 显示 |
| `status` | `0` | 正常 |
| `perms` | `agentscope:capability:list` | 权限标识(能力清单的读取权限) |

> 目录 `path=basic-extend` 与前端 `web-ui/src/views/basic-extend/` 目录名对应;
> 子菜单 `path=agent` 最终路由为 **`/basic-extend/agent`**。

---

## 7. 验收

| # | 验收项 | 方法 | 结果 |
|---|---|---|---|
| A-1 | 无新增 DDL | `git diff` 不含任何 `CREATE TABLE` / `ALTER TABLE` | ✅ 通过 |
| A-2 | 既有表结构未变 | `git diff` 不含既有表的 DDL 变更 | ✅ 通过 |
| A-3 | 应用不因 AgentScope 启动失败 | 启动日志 `Started BasicApplication in ...` | ✅ 通过 |
| A-4 | Agent 调用不触发业务表写入 | 观察 `sys_oper_log` 无新增 Agent 记录 | ✅ 通过 |
| A-5 | 菜单脚本可重复执行 | 连续执行 `create-agentscope-capability-menu.sql` 两次,`sys_menu` 仍只有 1067/1068 两条 | ✅ 通过 |
| A-6 | 菜单可见且路由可达 | 登录后 `/getRouters` 含 `basic-extend/agent`,前端可打开能力展厅 | ✅ 通过 |

---

## 8. 参考来源

- 《需求文档》《架构方案》《概要设计》《详细设计》(同目录)
- 上游基线:`docs/springboot4-数据库设计.md`(34 表 + 5 函数基线)
- AgentScope Java 官方仓库:<https://github.com/agentscope-ai/agentscope-java>
- 官方集成文档(分布式存储):<https://java.agentscope.io/v2/zh/integration/distributed/jdbc>

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:明确「本分支数据库零变更」结论;§2 论证无需动库 + 列出 8 个可选存储扩展(标注未引入);§3 既有 34 表 + 5 函数基线与数据交互面分析;§4 预留 M3 落库方案(A 官方扩展 / B 自研 / C Redis 三选一 + 自研建表约束 + 引入前置检查清单);§5 数据安全;§6 变更清单(全空);§7 验收 4 项 |
| v0.2 | 2026-10-23 | 能力展厅专题:§1 结论改为「DDL 零变更 + 2 条 `sys_menu` 菜单数据」并列出明细表;§2.1 补齐能力注册表/会话状态/长期记忆/RAG/调度器 5 类能力的落库现状;§6 变更清单补菜单数据与 SQL 脚本、新增 §6.1 `sys_menu` 记录明细与字段口径;§7 验收由 4 项扩到 6 项 |
