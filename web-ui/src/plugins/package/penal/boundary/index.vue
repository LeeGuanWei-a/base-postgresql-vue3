<template>
  <div>
    <!--定时边界事件-->
    <TimeBoundary v-if="elementType==='bpmn:TimerEventDefinition'" :business-object="businessObject" :id="id"
                  :type="type"/>
  </div>
</template>

<script>
import TimeBoundary from "./boundary-components/TimeBoundary.vue";

/**
 *@author yan bin
 * 边界事件处理
 */
export default {
  name: "index",
  components: {TimeBoundary},
  props: {
    id: String,
    type: String,
    businessObject: Object,
  },
  data() {
    return {
      bpmnElement: undefined,
      elementType: undefined,
    }
  },
  watch: {
    id: {
      immediate: true,
      handler(val) {
        this.bpmnElement = markRaw(window.bpmnInstances.bpmnElement);
        val && val.length && this.$nextTick(() => this.resetBoundary());
      }
    }
  },
  methods: {
    /**
     * 初始化
     */
    resetBoundary() {
      this.elementType = undefined;
      if (!!this.businessObject.eventDefinitions) {
        this.elementType = this.businessObject.eventDefinitions[0].$type;
      }
    },
  }
}
</script>


<style scoped lang="scss">

</style>
