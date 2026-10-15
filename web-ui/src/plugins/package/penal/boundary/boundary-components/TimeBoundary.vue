<template>
  <div>
    <el-form label-width="120px">
      <el-form-item label="选择定时类型" style="margin-bottom: 0px" v-if="isTimer">
        <el-select v-model="dateType" @change="handleChange">
          <el-option
            v-for="item in dateTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="输入等待时间" style="margin-top: 10px" v-if="isStopTime">
        <el-input v-model="timerForm.timeDuration" clearable>
          <template #append>
            <el-button :icon="Search" @click="handleStopTime"></el-button>
          </template>
        </el-input>
      </el-form-item>
    </el-form>
    <el-dialog title="到期时间配置" v-model="stopTimeOpen" width="30%" append-to-body>
      <el-form :model="stopForm" label-width="100px">
        <div v-for="(item,index) in stopFormList" :key="item.id">
          <el-form-item :label="`${item.name}:`">
            <el-radio-group :name="item.id" v-model="stopForm.time.id" size="small" >
              <el-radio-button v-for="it in item.list" :key="it.value" :value="it.value" >
                {{ it.label }}
              </el-radio-button>
            </el-radio-group>
            <el-input-number
              v-if="stopForm.time.id === '5'&&item.id==='1'||
                stopForm.time.id === '10'&&item.id==='2'||
                stopForm.time.id === '15'&&item.id==='3'||
                stopForm.time.id === '20'&&item.id==='4'||
                stopForm.time.id === '25'&&item.id==='5'"
              :min="1" size="small"
              v-model="stopTimeCustom.time">
            </el-input-number>
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button size="small" @click="cancel">取 消</el-button>
        <el-button size="small" type="primary" @click="confirmStopForm">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { markRaw } from 'vue'
import { Search } from '@element-plus/icons-vue'

export default {
  name: "FlowBoundaryConfig",
  components: { Search },
  props: {
    id: String,
    type: String,
    businessObject: Object,
  },
  inject: {
    prefix: "prefix",
    width: "width"
  },
  data() {
    return {
      // markRaw 包裹组件，避免被 reactive 化
      Search: markRaw(Search),
      dateType: '',
      isTimer: false,//定时边界显示
      isStopTime: false,//持续时间显示
      stopTimeOpen: false,
      // 到期时间配置表单
      stopForm: {
        time: {
          id: '',
          name:
            '',
          num:
            '',
        }
      },
      //到期时间弹窗表单内容自定义
      stopTimeCustom: {
        time: '1',
      },
      // 到期时间弹窗表单内容
      stopFormList: [
        {
          id: '1',
          value: 'M',
          name: '分钟',
          list: [{
            label: '5',
            value: '1'
          }, {
            label: '10',
            value: '2'
          }, {
            label: '30',
            value: '3'
          }, {
            label: '50',
            value: '4'
          }, {
            label: '自定义',
            value: '5'
          }]
        },
        {
          id: '2',
          value: 'H',
          name: '小时',
          list: [{
            label: '4',
            value: '6'
          }, {
            label: '8',
            value: '7'
          }, {
            label: '12',
            value: '8'
          }, {
            label: '24',
            value: '9'
          }, {
            label: '自定义',
            value: '10'
          }]
        },
        {
          id: '3',
          value: 'D',
          name: '天',
          list: [{
            label: '1',
            value: '11'
          }, {
            label: '2',
            value: '12'
          }, {
            label: '3',
            value: '13'
          }, {
            label: '4',
            value: '14'
          }, {
            label: '自定义',
            value: '15'
          }]
        }, {
          id: '4',
          value: 'W',
          name: '周',
          list: [{
            label: '1',
            value: '16'
          }, {
            label: '2',
            value: '17'
          }, {
            label: '3',
            value: '18'
          }, {
            label: '4',
            value: '19'
          }, {
            label: '自定义',
            value: '20'
          }]
        }, {
          id: '5',
          value: 'M',
          name: '月',
          list: [{
            label: '1',
            value: '21'
          }, {
            label: '2',
            value: '22'
          }, {
            label: '3',
            value: '23'
          }, {
            label: '4',
            value: '24'
          }, {
            label: '自定义',
            value: '25'
          }]
        },
      ],
      dateTypeOptions: [
        {
          value: '1',
          label: "开始时间"
        },
        {
          value: '2',
          label: "持续时间"
        }, {
          value: '3',
          label: "重复时间"
        }
      ],
      timerEventDef: null,
      timerForm: {
        //指定重复周期，可用于周期性启动流程，或者为超期用户任务多次发送提醒
        timeCycle: undefined,
        //在这个时间就会触发触发器
        timeDate: undefined,
        //要定义定时器需要等待多长时间再触发
        timeDuration: undefined
      }
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
    resetBoundary() {
      this.isTimer = false;
      // console.log("resetBoundary")
      // console.log(this.businessObject)
      if (!!this.businessObject) {
        if (!!this.businessObject.eventDefinitions && this.businessObject.eventDefinitions[0].$type === "bpmn:TimerEventDefinition") {
          this.isTimer = true
          //持续时间初始化
          if (!!this.businessObject.eventDefinitions[0].timeDuration) {
            this.timerForm.timeDuration = this.businessObject.eventDefinitions[0].timeDuration.body;
            // console.log(this.timerForm.timeDuration)
            this.dateType = "2"
            this.isStopTime = true;
          }
        }
      }
    },
    updateElement() {
      //获取节点的子节点 timerEventDefinition
      let timerEventDef = this.bpmnElement.businessObject.eventDefinitions[0]
      const timeCycle = window.bpmnInstances.moddle.create("bpmn:FormalExpression", {body: this.timerForm.timeCycle});
      const timeDate = window.bpmnInstances.moddle.create("bpmn:FormalExpression", {body: this.timerForm.timeDate});
      const timeDuration = window.bpmnInstances.moddle.create("bpmn:FormalExpression", {body: this.timerForm.timeDuration});
      window.bpmnInstances.modeling.updateModdleProperties(this.bpmnElement, timerEventDef, {
        timeCycle,
        timeDate,
        timeDuration
      })
    },
    //到期时间选择确定
    confirmStopForm() {
      // 到期时间弹窗表单内容
      this.stopFormList.forEach(item => {
        item.list.forEach(it => {
          if (it.value === this.stopForm.time.id) {
            this.stopForm.time.name = item.name;
            this.stopForm.time.num = it.label;
          }
        })
      })
      // 区分自定义
      if (this.stopForm.time.id === '5' || this.stopForm.time.id === '10'
        || this.stopForm.time.id === '15' || this.stopForm.time.id === '20' || this.stopForm.time.id === '25') {
        this.stopForm.time.num = this.stopTimeCustom.time;
      }
      let day;
      if (this.stopForm.time.name === '小时') {
        day = 'H';
        this.timerForm.timeDuration = 'PT' + this.stopForm.time.num + day;
      } else if (this.stopForm.time.name === '天') {
        day = 'D'
        this.timerForm.timeDuration = 'P' + this.stopForm.time.num + day;
      } else if (this.stopForm.time.name === '周') {
        day = 'W'
        this.timerForm.timeDuration = 'P' + this.stopForm.time.num + day;
      } else if (this.stopForm.time.name === '月') {
        day = 'M'
        this.timerForm.timeDuration = 'P' + this.stopForm.time.num + day;
      } else if (this.stopForm.time.name === '分钟') {
        day = 'M'
        this.timerForm.timeDuration = 'PT' + this.stopForm.time.num + day;
      }
      console.log("******选择的到期时间是什么*******")
      console.log(this.timerForm)
      let timerEventDef = this.bpmnElement.businessObject.eventDefinitions[0]
      const timeDuration = window.bpmnInstances.moddle.create("bpmn:FormalExpression", {body: this.timerForm.timeDuration});
      window.bpmnInstances.modeling.updateModdleProperties(this.bpmnElement, timerEventDef, {
        timeDuration
      })
      this.cancel();
    },
    //取消
    cancel() {
      this.stopTimeOpen = false;
      this.stopForm = {
        time: {
          id: '',
          name: '',
          num: '',
        }
      };
      //到期时间弹窗表单内容自定义
      this.stopTimeCustom = {
        time: '1'
      };
    },
    handleChange(value) {
      //开始时间  启动时间
      if (value === '1') {

      }
      //持续时间  到期时间
      else if (value === "2") {
        this.isStopTime = true;
      }
      //重复时间
      else if (value === "3") {

      }
    },
    //打开到期时间弹框
    handleStopTime() {
      this.stopTimeOpen = true;
      var stopTime = this.timerForm.timeDuration;
      if (!stopTime) {
        return;
      }
      var start = stopTime.charAt(0);  // P-->
      var end = stopTime.substr(-1)   // 获取是 天，小时，周，月  H,D,W,M
      // console.log(end + " " + start)
      //获取时间具体-->数字  eg:P10D  获取10
      var time = stopTime.substring(stopTime.lastIndexOf(start) + 1, stopTime.lastIndexOf(end));
      var dateTime = false;
      //判断是一天以内的时间，而不是周、月、天
      if (stopTime.includes("T")) {
        start = start + "T";
        time = time.substring(1)
        dateTime = true;
      }
      //TODO 分钟
      if (dateTime && end === 'M') {
        const mList = this.stopFormList[0].list;
        let frame = false;
        mList.forEach(item => {
          if (item.label === time) {
            this.stopForm.time.id = item.value;
            frame = true;
          }
        })
        if (!frame) {
          this.stopForm.time.id = '5';
          this.stopTimeCustom.time = parseInt(time);
        }
      } else {
        for (let i = 0; i < this.stopFormList.length; i++) {
          let item = this.stopFormList[i];
          if (item.value === end) {
            var isStopTime = false; // 区别是否是自定义  false是自定义内容;true否
            item.list.forEach(it => {
              // 判断   是否是自定义
              if (it.label == time) {
                this.stopForm.time.id = it.value;
                isStopTime = true;
              }
            })
            // 自定义内容
            if (!isStopTime) {
              item.list.forEach(it => {
                if (it.label == '自定义') {
                  this.stopForm.time.id = it.value;
                  this.stopTimeCustom.time = parseInt(time);
                }
              })
            }
          }
        }
      }
      console.log(this.stopForm)
    },
  }
}
</script>


<style scoped lang="scss">

</style>
