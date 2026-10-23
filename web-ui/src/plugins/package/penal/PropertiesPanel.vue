<template>
  <div class="process-panel__container" :style="{ width: `${width}px` }">
    <div style="cursor: pointer; margin-left: -3px" :title="width === 580 ? '收起' : '展开'" @click="handleWidth()">
      <div style="width: 40px; line-height: 40px; font-size: 20px">
        <el-icon v-if="width === 580"><CaretRight /></el-icon>
        <el-icon v-else><CaretLeft /></el-icon>
      </div>
    </div>
    <el-collapse v-model="activeTab" style="overflow-y: auto; height: calc(100vh - 40px)" v-show="width === 580">
      <div>
        <el-collapse-item name="base">
          <template #title>
            <div class="panel-tab__title"><el-icon><InfoFilled /></el-icon>常规</div>
          </template>
          <element-base-info
            :id-edit-disabled="idEditDisabled"
            :business-object="elementBusinessObject"
            :type="elementType"
          />
        </el-collapse-item>

        <el-collapse-item name="message" v-if="elementType === 'Process'" key="message">
          <template #title>
            <div class="panel-tab__title"><el-icon><ChatDotSquare /></el-icon>消息与信号</div>
          </template>
          <signal-and-massage />
        </el-collapse-item>

        <el-collapse-item name="condition" v-if="conditionFormVisible" key="condition">
          <template #title>
            <div class="panel-tab__title"><el-icon><Promotion /></el-icon>流转条件</div>
          </template>
          <flow-condition :business-object="elementBusinessObject" :type="elementType" />
        </el-collapse-item>

        <el-collapse-item name="form" v-if="formVisible" key="form">
          <template #title>
            <div class="panel-tab__title"><el-icon><Document /></el-icon>表单</div>
          </template>
          <element-form :id="elementId" :type="elementType" />
        </el-collapse-item>

        <el-collapse-item name="task" v-if="elementType.indexOf('Task') !== -1" key="task">
          <template #title>
            <div class="panel-tab__title"><el-icon><Tickets /></el-icon>任务</div>
          </template>
          <element-task :id="elementId" :type="elementType" />
        </el-collapse-item>

        <el-collapse-item name="multiInstance" v-if="elementType.indexOf('Task') !== -1" key="multiInstance">
          <template #title>
            <div class="panel-tab__title"><el-icon><QuestionFilled /></el-icon>多实例</div>
          </template>
          <element-multi-instance :business-object="elementBusinessObject" :type="elementType" />
        </el-collapse-item>

        <el-collapse-item name="boundary" v-if="elementType.indexOf('BoundaryEvent') !== -1" key="boundary">
          <template #title>
            <div class="panel-tab__title"><el-icon><CirclePlus /></el-icon>边界事件</div>
          </template>
          <flow-boundary-config
            :business-object="elementBusinessObject"
            :id="elementId"
            :type="elementType"
          />
        </el-collapse-item>

        <el-collapse-item name="listeners" key="listeners">
          <template #title>
            <div class="panel-tab__title"><el-icon><Message /></el-icon>执行监听器</div>
          </template>
          <element-listeners :id="elementId" :type="elementType" />
        </el-collapse-item>

        <el-collapse-item name="taskListeners" v-if="elementType === 'UserTask'" key="taskListeners">
          <template #title>
            <div class="panel-tab__title"><el-icon><Message /></el-icon>任务监听器</div>
          </template>
          <user-task-listeners :id="elementId" :type="elementType" />
        </el-collapse-item>

        <el-collapse-item name="extensions" key="extensions">
          <template #title>
            <div class="panel-tab__title"><el-icon><CirclePlus /></el-icon>扩展属性</div>
          </template>
          <element-properties :id="elementId" :type="elementType" />
        </el-collapse-item>

        <el-collapse-item name="other" key="other">
          <template #title>
            <div class="panel-tab__title"><el-icon><Promotion /></el-icon>其他</div>
          </template>
          <element-other-config :id="elementId" />
        </el-collapse-item>
      </div>
    </el-collapse>
  </div>
</template>

<script>
import {
  CaretRight,
  CaretLeft,
  InfoFilled,
  ChatDotSquare,
  Promotion,
  Document,
  Tickets,
  QuestionFilled,
  CirclePlus,
  Message
} from '@element-plus/icons-vue'
import ElementBaseInfo from './base/ElementBaseInfo'
import ElementOtherConfig from './other/ElementOtherConfig'
import ElementTask from './task/ElementTask'
import ElementMultiInstance from './multi-instance/ElementMultiInstance'
import FlowCondition from './flow-condition/FlowCondition'
import SignalAndMassage from './signal-message/SignalAndMessage'
import ElementListeners from './listeners/ElementListeners'
import ElementProperties from './properties/ElementProperties'
import UserTaskListeners from './listeners/UserTaskListeners'
import ElementForm from '@/plugins/package/penal/form/ElementForm.vue'
import FlowBoundaryConfig from '@/plugins/package/penal/boundary/index.vue'

/**
 * 流程设计器 - 右侧属性面板（业务定制）
 *
 * 机制说明：
 *   本组件是 penal 面板体系的「引擎」，职责有三：
 *   1. 把 bpmnModeler 的各 service 挂到 window.bpmnInstances，供所有子面板调用
 *      （子面板统一通过 window.bpmnInstances.moddle/modeling/... 操作流程图）
 *   2. 监听 bpmn-js 的 selection.changed / element.changed / import.done，
 *      维护「当前选中元素」
 *   3. 依据元素类型决定显示哪些折叠分组（常规/表单/任务/多实例/边界事件/监听器…）
 *
 * @Author MiyueFE
 * @Home https://github.com/miyuesc
 */
export default {
  name: 'BpmnPropertiesPanel',
  components: {
    FlowBoundaryConfig,
    ElementForm,
    UserTaskListeners,
    ElementProperties,
    ElementListeners,
    SignalAndMassage,
    FlowCondition,
    ElementMultiInstance,
    ElementTask,
    ElementOtherConfig,
    ElementBaseInfo
  },
  componentName: 'BpmnPropertiesPanel',
  props: {
    bpmnModeler: Object,
    prefix: {
      type: String,
      default: 'camunda'
    },
    idEditDisabled: {
      type: Boolean,
      default: false
    }
  },
  provide() {
    return {
      prefix: this.prefix,
      // 子面板（ElementListeners / UserTaskListeners / ElementProperties /
      // ElementForm / TimeBoundary / UserTask）会 inject width 用于 el-drawer 的 size
      width: this.width
    }
  },
  data() {
    return {
      activeTab: ['base'],
      elementId: '',
      elementType: '',
      // 元素 businessObject 镜像，提供给需要做判断的子组件
      elementBusinessObject: {},
      conditionFormVisible: false,
      formVisible: false,
      width: 580
    }
  },
  watch: {
    elementId: {
      handler() {
        this.activeTab = ['base']
      }
    }
  },
  created() {
    this.initModels()
  },
  unmounted() {
    window.bpmnInstances = null
    if (this.timer) clearTimeout(this.timer)
  },
  methods: {
    // 收缩面板
    handleWidth() {
      this.width = this.width === 580 ? 30 : 580
    },

    initModels() {
      // 流程图可能尚未加载完成，轮询等待
      if (!this.bpmnModeler) {
        this.timer = setTimeout(() => this.initModels(), 10)
        return
      }
      if (this.timer) clearTimeout(this.timer)
      window.bpmnInstances = {
        modeler: this.bpmnModeler,
        modeling: this.bpmnModeler.get('modeling'),
        moddle: this.bpmnModeler.get('moddle'),
        eventBus: this.bpmnModeler.get('eventBus'),
        bpmnFactory: this.bpmnModeler.get('bpmnFactory'),
        elementFactory: this.bpmnModeler.get('elementFactory'),
        elementRegistry: this.bpmnModeler.get('elementRegistry'),
        replace: this.bpmnModeler.get('replace'),
        selection: this.bpmnModeler.get('selection')
      }
      this.getActiveElement()
    },

    getActiveElement() {
      // 初始默认选中 bpmn:Process
      this.initFormOnChanged(null)
      this.bpmnModeler.on('import.done', () => {
        this.initFormOnChanged(null)
      })
      // 选择变化 → 切换当前元素
      this.bpmnModeler.on('selection.changed', ({ newSelection }) => {
        this.initFormOnChanged(newSelection[0] || null)
      })
      this.bpmnModeler.on('element.changed', ({ element }) => {
        if (element.businessObject.$type === 'bpmn:UserTask') {
          this.activeTab = ['base', 'task']
        }
        // 修改"默认流转路径"等会同时改动多个元素，这里保证表单与当前选中元素一致
        if (element && element.id === this.elementId) {
          this.initFormOnChanged(element)
        }
      })
    },

    initFormOnChanged(element) {
      let activatedElement = element
      if (!activatedElement) {
        activatedElement =
          window.bpmnInstances.elementRegistry.find((el) => el.type === 'bpmn:Process') ??
          window.bpmnInstances.elementRegistry.find((el) => el.type === 'bpmn:Collaboration')
      }
      if (!activatedElement) return

      window.bpmnInstances.bpmnElement = activatedElement
      this.elementId = activatedElement.id
      this.elementType = activatedElement.type.split(':')[1] || ''
      this.elementBusinessObject = JSON.parse(JSON.stringify(activatedElement.businessObject))
      // 流转条件：连线且源节点不是开始事件
      this.conditionFormVisible = !!(
        this.elementType === 'SequenceFlow' &&
        activatedElement.source &&
        activatedElement.source.type.indexOf('StartEvent') === -1
      )
      // 表单配置：用户任务或开始事件
      this.formVisible = this.elementType === 'UserTask' || this.elementType === 'StartEvent'
    }
  }
}
</script>
