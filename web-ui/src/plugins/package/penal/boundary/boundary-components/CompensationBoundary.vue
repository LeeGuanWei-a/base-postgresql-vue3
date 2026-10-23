<!--
  补偿边界事件（Compensation Boundary Event）属性面板
  - 用于"事务回滚"：当宿主活动被 rollback / cancel 时触发补偿活动
  - activityRef：可选，指定具体要补偿的 activity；为空时补偿最近完成的 activity
-->
<template>
  <el-form label-width="110px" size="small" @submit.prevent>
    <el-form-item label="补偿活动">
      <el-select v-model="activityRef" clearable filterable
                 placeholder="不选则补偿最近完成的 activity" @change="onChangeActivityRef">
        <el-option v-for="item in activityOptions" :key="item.id"
                   :value="item.id" :label="item.label"/>
      </el-select>
    </el-form-item>
    <el-form-item label="是否等待补偿">
      <el-switch v-model="waitForCompletion" @change="onChangeWaitForCompletion"/>
    </el-form-item>
    <div style="color:#909399; font-size: 12px; line-height: 1.6">
      补偿边界：用于事务回滚场景。当宿主活动需要被撤销时（比如子流程被取消），
      触发本边界并执行补偿活动。常与 throw compensation 配对使用。
    </div>
  </el-form>
</template>

<script>
/**
 * 补偿边界事件配置面板
 * @author integration-supplement
 */
export default {
  name: 'CompensationBoundary',
  props: {
    id: String,
    type: String,
    businessObject: Object
  },
  data() {
    return {
      activityRef: '',
      waitForCompletion: true,
      activityOptions: []
    }
  },
  watch: {
    businessObject: {
      immediate: true,
      handler() {
        this.initFromBusiness()
        this.collectActivityOptions()
      }
    }
  },
  methods: {
    initFromBusiness() {
      if (!this.businessObject) return
      if (this.businessObject.eventDefinitions && this.businessObject.eventDefinitions.length > 0) {
        const def = this.businessObject.eventDefinitions[0]
        this.activityRef = def.activityRef || ''
        this.waitForCompletion = def.waitForCompletion !== false
      }
    },
    collectActivityOptions() {
      // 收集同流程内所有 UserTask / SubProcess 作为可补偿的候选
      try {
        const registry = window.bpmnInstances.elementRegistry
        const all = registry.getAll()
        this.activityOptions = all
          .filter(el => el.type === 'bpmn:UserTask' || el.type === 'bpmn:SubProcess' || el.type === 'bpmn:CallActivity')
          .map(el => ({ id: el.id, label: `${el.id} (${el.businessObject.name || el.type.split(':').pop()})` }))
      } catch (e) {
        this.activityOptions = []
      }
    },
    onChangeActivityRef(val) {
      const el = window.bpmnInstances.bpmnElement
      if (!el || !el.businessObject.eventDefinitions || el.businessObject.eventDefinitions.length === 0) return
      const def = el.businessObject.eventDefinitions[0]
      window.bpmnInstances.modeling.updateModdleProperties(el, def, { activityRef: val || null })
    },
    onChangeWaitForCompletion(val) {
      const el = window.bpmnInstances.bpmnElement
      if (!el || !el.businessObject.eventDefinitions || el.businessObject.eventDefinitions.length === 0) return
      const def = el.businessObject.eventDefinitions[0]
      window.bpmnInstances.modeling.updateModdleProperties(el, def, { waitForCompletion: val })
    }
  }
}
</script>

<style scoped lang="scss">
</style>
