<!--
  取消边界事件（Cancel Boundary Event）属性面板
  - 仅在 transaction / call activity 子流程边界有效
  - 不能挂在 UserTask 上（Flowable 会忽略）
  - 此处只做"是否中断宿主"配置 + 提示
-->
<template>
  <el-form label-width="110px" size="small" @submit.prevent>
    <el-form-item label="是否中断宿主">
      <el-switch v-model="cancelActivity" @change="onChangeCancelActivity"/>
    </el-form-item>
    <div style="color:#e6a23c; font-size: 12px; line-height: 1.6">
      <strong>使用约束：</strong>取消边界仅在 transaction / call activity 子流程边界有效，
      挂在 UserTask 上时运行期不会触发。外部可通过
      <code>runtimeService.deleteProcessInstance(procInsId, reason)</code>
      触发子流程的取消边界。
    </div>
  </el-form>
</template>

<script>
/**
 * 取消边界事件配置面板
 * @author integration-supplement
 */
export default {
  name: 'CancelBoundary',
  props: {
    id: String,
    type: String,
    businessObject: Object
  },
  data() {
    return {
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
      this.cancelActivity = this.businessObject.cancelActivity !== false
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
