<!--
  消息边界事件（Message Boundary Event）属性面板
  - messageRef：从根元素 bpmn:Message 列表中选
  - correlationKey：可选，路由到指定 execution（不填则取 processInstanceId）
-->
<template>
  <el-form label-width="110px" size="small" @submit.prevent>
    <el-form-item label="消息 ID">
      <el-select v-model="messageId" filterable allow-create clearable
                 placeholder="选择已注册消息或输入新 id" @change="onChangeMessageId">
        <el-option v-for="item in messageList" :key="item.id" :value="item.id"
                   :label="item.name ? `${item.id} (${item.name})` : item.id"/>
      </el-select>
    </el-form-item>
    <el-form-item label="是否中断宿主">
      <el-switch v-model="cancelActivity" @change="onChangeCancelActivity"/>
    </el-form-item>
    <div style="color:#909399; font-size: 12px; line-height: 1.6">
      消息边界：与 signal 不同，消息按 execution 精准投递。
      外部调用
      <code>runtimeService.messageEventReceived(messageId, executionId)</code>
      时触发。
    </div>
  </el-form>
</template>

<script>
/**
 * 消息边界事件配置面板
 * @author integration-supplement
 */
export default {
  name: 'MessageBoundary',
  props: {
    id: String,
    type: String,
    businessObject: Object
  },
  data() {
    return {
      messageId: '',
      cancelActivity: true,
      messageList: []
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
    this.loadMessageList()
  },
  methods: {
    initFromBusiness() {
      if (!this.businessObject) return
      if (this.businessObject.eventDefinitions && this.businessObject.eventDefinitions.length > 0) {
        const def = this.businessObject.eventDefinitions[0]
        this.messageId = def.messageRef || def.messageExpression || ''
      }
      this.cancelActivity = this.businessObject.cancelActivity !== false
    },
    loadMessageList() {
      try {
        const rootElements = window.bpmnInstances.modeler.getDefinitions().rootElements
        this.messageList = (rootElements || [])
          .filter(el => el.$type === 'bpmn:Message')
          .map(el => ({ id: el.id, name: el.name }))
      } catch (e) {
        this.messageList = []
      }
    },
    onChangeMessageId(val) {
      const el = window.bpmnInstances.bpmnElement
      if (!el || !el.businessObject.eventDefinitions || el.businessObject.eventDefinitions.length === 0) return
      const def = el.businessObject.eventDefinitions[0]
      window.bpmnInstances.modeling.updateModdleProperties(el, def, { messageRef: val || null })
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
