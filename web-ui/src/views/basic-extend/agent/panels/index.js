/**
 * AgentScope 能力面板注册表。
 *
 * 后端能力清单（/agentscope/capability/list）是动态的，前端面板是静态的：
 * - 后端有、前端有 → 渲染对应面板
 * - 后端有、前端无 → 渲染通用兜底面板
 */
import ChatPanel from './ChatPanel.vue'
import ToolPanel from './ToolPanel.vue'
import ToolGroupPanel from './ToolGroupPanel.vue'
import StructuredPanel from './StructuredPanel.vue'
import TaskListPanel from './TaskListPanel.vue'
import SubAgentPanel from './SubAgentPanel.vue'
import SessionPanel from './SessionPanel.vue'
import MemoryPanel from './MemoryPanel.vue'
import RagPanel from './RagPanel.vue'
import PermissionPanel from './PermissionPanel.vue'
import InterruptPanel from './InterruptPanel.vue'
import HookPanel from './HookPanel.vue'
import MiddlewarePanel from './MiddlewarePanel.vue'
import FailoverPanel from './FailoverPanel.vue'
import StateStorePanel from './StateStorePanel.vue'
import MetaToolPanel from './MetaToolPanel.vue'
import ModelPanel from './ModelPanel.vue'
import McpPanel from './McpPanel.vue'
import ChannelPanel from './ChannelPanel.vue'
import SandboxPanel from './SandboxPanel.vue'
import SchedulerPanel from './SchedulerPanel.vue'

export const capabilityPanels = {
  chat: ChatPanel,
  tool: ToolPanel,
  'tool-group': ToolGroupPanel,
  structured: StructuredPanel,
  'task-list': TaskListPanel,
  'sub-agent': SubAgentPanel,
  session: SessionPanel,
  memory: MemoryPanel,
  rag: RagPanel,
  permission: PermissionPanel,
  interrupt: InterruptPanel,
  hook: HookPanel,
  middleware: MiddlewarePanel,
  failover: FailoverPanel,
  'state-store': StateStorePanel,
  'meta-tool': MetaToolPanel,
  model: ModelPanel,
  mcp: McpPanel,
  channel: ChannelPanel,
  sandbox: SandboxPanel,
  scheduler: SchedulerPanel
}

export default capabilityPanels
