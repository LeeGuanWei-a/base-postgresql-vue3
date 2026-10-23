<!--
  边界事件（BoundaryEvent）属性面板 - 入口组件
  依据 businessObject.eventDefinitions[0].$type 派发到 8 个子配置组件
-->
<template>
  <div>
    <!--timer-->
    <TimeBoundary v-if="eventType === 'bpmn:TimerEventDefinition'"
                  :business-object="businessObject" :id="id" :type="elementType"/>
    <!--message-->
    <MessageBoundary v-else-if="eventType === 'bpmn:MessageEventDefinition'"
                     :business-object="businessObject" :id="id" :type="elementType"/>
    <!--signal-->
    <SignalBoundary v-else-if="eventType === 'bpmn:SignalEventDefinition'"
                    :business-object="businessObject" :id="id" :type="elementType"/>
    <!--error-->
    <ErrorBoundary v-else-if="eventType === 'bpmn:ErrorEventDefinition'"
                   :business-object="businessObject" :id="id" :type="elementType"/>
    <!--escalation-->
    <EscalationBoundary v-else-if="eventType === 'bpmn:EscalationEventDefinition'"
                        :business-object="businessObject" :id="id" :type="elementType"/>
    <!--cancel-->
    <CancelBoundary v-else-if="eventType === 'bpmn:CancelEventDefinition'"
                    :business-object="businessObject" :id="id" :type="elementType"/>
    <!--compensation-->
    <CompensationBoundary v-else-if="eventType === 'bpmn:CompensateEventDefinition'"
                          :business-object="businessObject" :id="id" :type="elementType"/>
    <!--conditional-->
    <ConditionalBoundary v-else-if="eventType === 'bpmn:ConditionalEventDefinition'"
                         :business-object="businessObject" :id="id" :type="elementType"/>
    <!--未匹配到 eventDefinitions 时给出友好提示 + 类型选择器（不阻断保存）-->
    <div v-else style="padding: 4px 0">
      <div style="color:#e6a23c; font-size: 12px; line-height: 1.7; margin-bottom: 8px">
        <i style="margin-right: 4px">⚠</i>当前 boundary 节点尚未指定具体事件类型
      </div>
      <el-form size="small" label-width="100px" @submit.prevent>
        <el-form-item label="事件类型">
          <el-select v-model="selectedType" placeholder="选择一种类型" clearable
                     @change="onChangeBoundaryType">
            <el-option v-for="opt in TYPE_OPTIONS" :key="opt.value" :value="opt.value"
                       :label="opt.label"/>
          </el-select>
        </el-form-item>
      </el-form>
      <div style="color:#909399; font-size: 12px; line-height: 1.6; margin-top: 4px">
        未指定类型时本面板不显示配置项，但保存不受阻断。
      </div>
    </div>
  </div>
</template>

<script>
import { markRaw } from 'vue'
import TimeBoundary from './boundary-components/TimeBoundary.vue'
import MessageBoundary from './boundary-components/MessageBoundary.vue'
import SignalBoundary from './boundary-components/SignalBoundary.vue'
import ErrorBoundary from './boundary-components/ErrorBoundary.vue'
import EscalationBoundary from './boundary-components/EscalationBoundary.vue'
import CancelBoundary from './boundary-components/CancelBoundary.vue'
import CompensationBoundary from './boundary-components/CompensationBoundary.vue'
import ConditionalBoundary from './boundary-components/ConditionalBoundary.vue'

/**
 * 边界事件属性面板入口
 *
 * <p>原版 bug：template 里用 {@code elementType}，但 props 只声明了 {@code type}，且内部 data 也没有 elementType
 * 所以旧版本永远渲染空白。本版从 businessObject.eventDefinitions[0].$type 解析事件类型后派发到 8 个子组件。</p>
 *
 * @author integration-supplement
 */
export default {
  name: 'FlowBoundaryConfig',
  components: {
    TimeBoundary,
    MessageBoundary,
    SignalBoundary,
    ErrorBoundary,
    EscalationBoundary,
    CancelBoundary,
    CompensationBoundary,
    ConditionalBoundary
  },
  props: {
    id: String,
    type: String,
    businessObject: Object
  },
  data() {
    return {
      eventType: undefined,
      // markRaw 避免被 reactive 化
      bpmnElement: markRaw(window.bpmnInstances ? window.bpmnInstances.bpmnElement : null),
      elementType: undefined,
      // 未指定类型时的下拉选择器 v-model
      selectedType: ''
    }
  },
  watch: {
    id: {
      immediate: true,
      handler(val) {
        this.bpmnElement = markRaw(window.bpmnInstances ? window.bpmnInstances.bpmnElement : null)
        val && val.length && this.$nextTick(() => this.resetBoundary())
      }
    },
    businessObject: {
      immediate: true,
      deep: true,
      handler() {
        this.resetBoundary()
      }
    }
  },
  computed: {
    /**
     * 8 种可选类型。value 用 bpmn-js moddle $type，便于直接 moddle.create。
     */
    TYPE_OPTIONS() {
      return [
        { value: 'bpmn:TimerEventDefinition', label: '定时器' },
        { value: 'bpmn:MessageEventDefinition', label: '消息' },
        { value: 'bpmn:SignalEventDefinition', label: '信号' },
        { value: 'bpmn:ErrorEventDefinition', label: '错误' },
        { value: 'bpmn:EscalationEventDefinition', label: '升级' },
        { value: 'bpmn:CancelEventDefinition', label: '取消' },
        { value: 'bpmn:CompensateEventDefinition', label: '补偿' },
        { value: 'bpmn:ConditionalEventDefinition', label: '条件' }
      ]
    }
  },
  methods: {
    resetBoundary() {
      this.eventType = undefined
      this.elementType = undefined
      if (this.businessObject && this.businessObject.eventDefinitions
          && this.businessObject.eventDefinitions.length > 0) {
        this.eventType = this.businessObject.eventDefinitions[0].$type
      }
      // elementType 仍保留（如子组件要用 BPMN 元素类型，可从这里取）
      this.elementType = (this.type || '').split(':').pop() || this.type
      // 同步下拉框（用户可能在画布上选了另一个 boundary）
      this.selectedType = this.eventType || ''
    },
    /**
     * 用户在面板里切换 boundary 事件类型：通过 moddle.create 创建对应 EventDefinition，
     * 替换 businessObject.eventDefinitions，并触发 modeling 重绘。
     * <p>等价于 bpmn-js context-pad "Change to" 菜单的行为，但在这里直接做，避免依赖右键菜单。</p>
     */
    onChangeBoundaryType(newType) {
      if (!newType) {
        // 清空：直接把 eventDefinitions 设为空数组
        this.applyEventDefinitions([])
        return
      }
      try {
        const moddle = window.bpmnInstances.moddle
        const newDef = moddle.create(newType, {})
        this.applyEventDefinitions([newDef])
      } catch (e) {
        console.error('[boundary] 创建 eventDefinition 失败', newType, e)
        this.$message && this.$message.error('切换 boundary 类型失败：' + (e && e.message ? e.message : e))
      }
    },
    /**
     * 真正写入 moddle 并触发 modeler 重绘。
     * <p>优先用 bpmn-js modeling.setHelper / updateProperties；直接改 businessObject.eventDefinitions
     * 在 bpmn-js 7+ 已能触发重绘（modeling 内部监听 eventDefinitions 变化）。</p>
     */
    applyEventDefinitions(newEventDefinitions) {
      const el = window.bpmnInstances.bpmnElement
      if (!el) return
      // 用 modeling.updateProperties 一次性更新 + 触发重绘；
      // updateProperties 第二个参数是 patch，会通过 BpmnUpdater 命令应用到 moddle + 触发重绘
      window.bpmnInstances.modeling.updateProperties(el, {
        eventDefinitions: newEventDefinitions
      })
    }
  }
}
</script>

<style scoped lang="scss">
</style>
