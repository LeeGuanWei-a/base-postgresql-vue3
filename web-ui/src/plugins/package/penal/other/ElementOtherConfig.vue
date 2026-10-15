<!--设计器其他模块-->
<template>
  <div class="panel-tab__content">
    <div class="element-property input-property">
      <div class="element-property__label">元素文档：</div>
      <div class="element-property__value">
        <el-input
          type="textarea"
          v-model="documentation"
          size="small"
          resize="vertical"
          :autosize="{ minRows: 2, maxRows: 4 }"
          @input="updateDocumentation"
          @blur="updateDocumentation"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { markRaw } from 'vue'
export default {
  name: "ElementOtherConfig",
  props: {
    id: String
  },
  data() {
    return {
      documentation: ""
    };
  },
  watch: {
    id: {
      immediate: true,
      handler: function (id) {
        if (id && id.length) {
          this.$nextTick(() => {
            const documentations = window.bpmnInstances.bpmnElement.businessObject?.documentation;
            this.documentation = documentations && documentations.length ? documentations[0].text : "";
          });
        } else {
          this.documentation = "";
        }
      }
    }
  },
  methods: {
    updateDocumentation() {
      (this.bpmnElement && this.bpmnElement.id === this.id) || (this.bpmnElement = markRaw(window.bpmnInstances.elementRegistry.get(this.id)));
      const documentation = window.bpmnInstances.bpmnFactory.create("bpmn:Documentation", {text: this.documentation});
      window.bpmnInstances.modeling.updateProperties(this.bpmnElement, {
        documentation: [documentation]
      });
    }
  },
  beforeUnmount() {
    this.bpmnElement = null;
  }
};
</script>
