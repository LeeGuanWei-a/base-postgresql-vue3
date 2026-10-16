<!--
  信号边界事件（Signal Boundary Event）属性面板
  - signalRef：必须从根元素 bpmn:Signal 列表中选；可手动输入未注册的 signalCode
  - cancelActivity：true=中断宿主活动（默认）；false=非中断（流程继续）
-->
<template>
  <el-form label-width="110px" size="small" @submit.prevent>
    <el-form-item label="信号 code">
      <el-select v-model="signalCode" filterable allow-create clearable
                 placeholder="选择已注册信号或输入新 code" @change="onChangeSignalCode">
        <el-option v-for="item in signalList" :key="item.id" :value="item.id"
                   :label="item.name ? `${item.id} (${item.name})` : item.id"/>
      </el-select>
    </el-form-item>
    <el-form-item label="是否中断宿主">
      <el-switch v-model="cancelActivity" @change="onChangeCancelActivity"/>
    </el-form-item>
    <div style="color:#909399; font-size: 12px; line-height: 1.6">
      信号边界：在流程中抛出
      <code>runtimeService.signalEventReceived(signalCode)</code>
      时触发。同一信号可被多个 boundary 订阅（广播语义）。
    </div>
  </el-form>
</template>

<script>
import { markRaw } from 'vue'

/**
 * 信号边界事件配置面板
 *
 * @author integration-supplement
 */
export default {
  name: 'SignalBoundary',
  props: {
    id: String,
    type: String,
    businessObject: Object
  },
  data() {
    return {
      signalCode: '',
      cancelActivity: true,
      signalList: []
    }
  },
  watch: {
    businessObject: {
      immediate: true,
      handler() {
        this.initFromBusiness()
      }
    }
  },
  mounted() {
    this.loadSignalList()
  },
  methods: {
    initFromBusiness() {
      if (!this.businessObject) return
      // signalCode 直接存在 businessObject 上（Flowable 标准字段）
      if (this.businessObject.eventDefinitions && this.businessObject.eventDefinitions.length > 0) {
        const def = this.businessObject.eventDefinitions[0]
        this.signalCode = def.signalRef || def.signalExpression || ''
      }
      // cancelActivity 在 BoundaryEvent 自身
      this.cancelActivity = this.businessObject.cancelActivity !== false
    },
    loadSignalList() {
      // 从根元素里收集 bpmn:Signal
      try {
        const rootElements = window.bpmnInstances.modeler.getDefinitions().rootElements
        this.signalList = (rootElements || [])
          .filter(el => el.$type === 'bpmn:Signal')
          .map(el => ({ id: el.id, name: el.name }))
      } catch (e) {
        this.signalList = []
      }
    },
    onChangeSignalCode(val) {
      this.applyEventDefinition({ signalRef: val || null })
    },
    onChangeCancelActivity(val) {
      // 改 BoundaryEvent 自身属性
      const el = window.bpmnInstances.bpmnElement
      window.bpmnInstances.modeling.updateProperties(el, { cancelActivity: val })
    },
    /**
     * 更新 bpmn:SignalEventDefinition 的 signalRef
     * @param {{signalRef: string|null}} patch
     */
    applyEventDefinition(patch) {
      const el = window.bpmnInstances.bpmnElement
      if (!el || !el.businessObject.eventDefinitions || el.businessObject.eventDefinitions.length === 0) return
      const def = el.businessObject.eventDefinitions[0]
      window.bpmnInstances.modeling.updateModdleProperties(el, def, patch)
    }
  }
}
</script>

<style scoped lang="scss">
</style>
