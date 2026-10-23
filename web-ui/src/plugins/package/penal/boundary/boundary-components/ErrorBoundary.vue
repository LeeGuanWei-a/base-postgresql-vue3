<!--
  错误边界事件（Error Boundary Event）属性面板
  - errorCode：必须配置，ServiceTask/JavaDelegate 抛 BpmnError(errorCode) 时定位到该 boundary
  - cancelActivity：true=中断宿主活动（默认）；错误边界通常为 true
  - 注：错误边界只能挂在 UserTask/SubProcess/CallActivity 上，不能挂在 StartEvent 上
-->
<template>
  <el-form label-width="110px" size="small" @submit.prevent>
    <el-form-item label="错误码" required>
      <el-input v-model="errorCode" placeholder="如：biz-validation-failed"
                clearable @change="onChangeErrorCode"/>
    </el-form-item>
    <el-form-item label="是否中断宿主">
      <el-switch v-model="cancelActivity" @change="onChangeCancelActivity"/>
    </el-form-item>
    <div style="color:#909399; font-size: 12px; line-height: 1.6">
      错误边界：节点（ServiceTask/JavaDelegate）通过
      <code>throw new BpmnError(errorCode)</code>
      抛出时，Flowable 按 errorCode 匹配到该边界并沿出向线流转。
    </div>
  </el-form>
</template>

<script>
/**
 * 错误边界事件配置面板
 * @author integration-supplement
 */
export default {
  name: 'ErrorBoundary',
  props: {
    id: String,
    type: String,
    businessObject: Object
  },
  data() {
    return {
      errorCode: '',
      cancelActivity: true
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
  methods: {
    initFromBusiness() {
      if (!this.businessObject) return
      if (this.businessObject.eventDefinitions && this.businessObject.eventDefinitions.length > 0) {
        const def = this.businessObject.eventDefinitions[0]
        // errorCode 存在 ErrorEventDefinition 上
        this.errorCode = def.errorCode || ''
      }
      this.cancelActivity = this.businessObject.cancelActivity !== false
    },
    onChangeErrorCode(val) {
      const el = window.bpmnInstances.bpmnElement
      if (!el || !el.businessObject.eventDefinitions || el.businessObject.eventDefinitions.length === 0) return
      const def = el.businessObject.eventDefinitions[0]
      window.bpmnInstances.modeling.updateModdleProperties(el, def, { errorCode: val || null })
    },
    onChangeCancelActivity(val) {
      const el = window.bpmnInstances.bpmnElement
      window.bpmnInstances.modeling.updateProperties(el, { cancelActivity: val })
    }
  }
}
</script>

<style scoped lang="scss">
</style>
