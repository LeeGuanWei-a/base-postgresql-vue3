<!--流程设计器常规模块-->
<template>
  <div class="panel-tab__content">
    <el-form size="small" label-width="90px" @submit.prevent>
      <el-form-item label="ID">
        <el-input
          v-model="elementBaseInfo.id"
          :disabled=isAble
          @change="updateBaseInfo('id')"
        />
      </el-form-item>
      <el-form-item label="名称">
        <el-input v-model="elementBaseInfo.name" clearable @change="updateBaseInfo('name')"/>
        <a v-if="elementBaseInfo.$type === 'bpmn:UserTask'" @click="handleSubmitter">
          <el-tag size="small">设置为提交人</el-tag>
        </a>
      </el-form-item>
      <el-form-item label="跳过表达式" v-if="elementBaseInfo.$type === 'bpmn:UserTask'">
        <el-input v-model="elementBaseInfo.skipExpression" clearable @change="updateBaseInfo('skipExpression')"/>
      </el-form-item>
      <el-form-item label="流程启动人" v-if="elementBaseInfo.$type === 'bpmn:Process'">
        <el-input v-model="elementBaseInfo.processUser" clearable @change="updateBaseInfo('processUser')"/>
      </el-form-item>
      <!--流程的基础属性-->
      <template v-if="elementBaseInfo.$type === 'bpmn:Process'">
        <el-form-item label="版本标签">
          <el-input v-model="elementBaseInfo.versionTag" clearable @change="updateBaseInfo('versionTag')"/>
        </el-form-item>
        <el-form-item label="可执行">
          <el-switch v-model="elementBaseInfo.isExecutable" active-text="是" inactive-text="否"
                     @change="updateBaseInfo('isExecutable')"/>
        </el-form-item>
      </template>

    </el-form>
  </div>
</template>
<script>
import { markRaw } from 'vue'

export default {
  name: "ElementBaseInfo",
  props: {
    businessObject: Object,
    type: String,
    idEditDisabled: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      elementBaseInfo: {},
      isAble: true,
    };
  },
  watch: {
    businessObject: {
      immediate: false,
      handler: function (val) {
        if (val) {
          if (val.$type === "bpmn:UserTask" || val.$type === "bpmn:StartEvent" || val.$type === "bpmn:EndEvent") {
            this.isAble = false;
          } else {
            this.isAble = true;
          }
          this.$nextTick(() => this.resetBaseInfo());
        }
      }
    },
    elementBaseInfo: {
      handler(newVal, oldVal) {
        if (!newVal.id) {
          this.$modal.msgWarning("模块ID不能为空!!!");
        }
      },
      deep: true
    }
  },
  methods: {
    resetBaseInfo() {
      this.bpmnElement = markRaw(window?.bpmnInstances?.bpmnElement);
      // console.log("/**提交人**")
      // console.log(this.bpmnElement)
      if (this.bpmnElement.businessObject.flowElements) {
        this.bpmnElement.businessObject.flowElements.forEach(item => {
          if (item.$type == 'bpmn:UserTask' && item.skipExpression) {
          }
        })
      }
      this.elementBaseInfo = JSON.parse(JSON.stringify(this.bpmnElement.businessObject));
    },

    // 设置为提交人
    handleSubmitter() {
      this.elementBaseInfo.name = '提交人';
      this.elementBaseInfo.skipExpression = '${initiator == \'\'}';
      this.$nextTick(() => {
        this.updateBaseInfo('name');
        this.updateBaseInfo('skipExpression')
      })
    },

    updateBaseInfo(key) {
      const attrObj = Object.create(null);
      attrObj[key] = this.elementBaseInfo[key];
      if (key === "id") {
        window.bpmnInstances.modeling.updateProperties(this.bpmnElement, {
          id: this.elementBaseInfo[key],
          di: {id: `${this.elementBaseInfo[key]}_di`}
        });
      } else {
        window.bpmnInstances.modeling.updateProperties(this.bpmnElement, attrObj);
      }
    }
  },
  beforeUnmount() {
    this.bpmnElement = null;
  }
};
</script>
