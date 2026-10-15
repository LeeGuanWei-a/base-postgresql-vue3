<!--用户右侧任务-->
<template>
  <div>
    <h4><b>审批人设置</b></h4>
    <div style="margin-left: 35px">
      <el-tooltip content="开启后,当前节点绑定的人员或表达式会根据所选的org组织进行筛选人员"
                  placement="top-start"
                  @click.stop.prevent>
        <el-icon class="header-icon"><InfoFilled /></el-icon>
      </el-tooltip>
      <span>是否通过组织架构筛选</span>
      <el-checkbox v-model="isOrgScreen" style="margin-left: 22px" @change="handleOrgScreen">是否</el-checkbox>
    </div>
    <div style="margin-left: 35px;" v-if="isOrgScreen">
      <el-tooltip content="开启后,当前节点绑定的人员或表达式会根据所选的org组织进行筛选人员"
                  placement="top-start"
                  @click.stop.prevent>
        <el-icon class="header-icon"><InfoFilled /></el-icon>
      </el-tooltip>
      <span>筛选方式</span>
      <!--
        说明：原实现是 el-checkbox-group + 字符串 v-model（依赖 element-ui 宽松类型），
        element-plus 的 checkbox-group 要求 modelValue 必须为 Array，否则告警且点击不生效。
        这里改为两个独立 checkbox + 显式 change：
          - isDeptOrOrg 仍保持字符串（'dept' / 'org' / 'null'），
            不影响写回 BPMN 扩展属性 flowable:IsDeptOrOrg 的 body 格式；
          - 勾选任一项即设为对应值，另一项自动取消，保持原本的「近似单选」交互。
      -->
      <span style="margin-left: 22px">
        <el-checkbox
          :model-value="isDeptOrOrg === 'dept'"
          @change="(checked) => handelSelectOrgOrDept(checked ? 'dept' : 'null')"
        >部门</el-checkbox>
        <el-checkbox
          :model-value="isDeptOrOrg === 'org'"
          @change="(checked) => handelSelectOrgOrDept(checked ? 'org' : 'null')"
        >组织架构</el-checkbox>
      </span>
    </div>
    <div style="margin-left: 35px" v-if="form.dataType==='1'">
      <el-tooltip content="开启后,节点会筛选出于发起人相同公司的人员进行处理"
                  placement="top-start"
                  @click.stop.prevent>
        <el-icon class="header-icon"><InfoFilled /></el-icon>
      </el-tooltip>
      <span>根据发起人所在部门筛选</span>
      <el-checkbox v-model="isUserScreen" style="margin-left: 22px" @change="handleUserScreen">是否</el-checkbox>
    </div>
    <div style="margin-left: 35px">
      <el-tooltip content="开启后,下一节点为多实例会签节点(已有人审批但未完全审批完成)可以撤回"
                  placement="top-start"
                  @click.stop.prevent>
        <el-icon class="header-icon"><InfoFilled /></el-icon>
      </el-tooltip>
      <span>是否强制撤回多实例节点存在已审的情况</span>
      <el-checkbox v-model="isRevoke" style="margin-left: 22px" @change="handleRevoke">是否</el-checkbox>
    </div>
    <div style="margin-left: 35px">
      <el-tooltip content="开启后,已审过的节点不需要二次审批,即开启后当前审批人处理相同节点只需一次"
                  placement="top-start"
                  @click.stop.prevent>
        <el-icon class="header-icon"><InfoFilled /></el-icon>
      </el-tooltip>
      <span>审批配置</span>
      <el-checkbox v-model="isExamined" style="margin-left: 22px" @change="handleExamined">是否</el-checkbox>
    </div>
    <div style="margin-left: 35px">
      <el-tooltip content="开启后，流程审批时两个连续的节点处理人相同则自动跳过" placement="top-start"
                  @click.stop.prevent>
        <el-icon class="header-icon"><InfoFilled /></el-icon>
      </el-tooltip>
      <span>交替配置</span>
      <el-checkbox v-model="isRoleReplace" style="margin-left: 22px" @change="handleRoleReplace">是否</el-checkbox>
    </div>
    <div style="margin-left: 35px">
      <el-tooltip content="开启后，流程审批时两个连续的节点处理人相同则自动跳过" placement="top-start"
                  @click.stop.prevent>
        <el-icon class="header-icon"><InfoFilled /></el-icon>
      </el-tooltip>
      <span>处理人配置</span>
      <el-checkbox v-model="isSkip" style="margin-left: 10px" @change="handleSkip">自动跳过</el-checkbox>
    </div>
    <div style="margin-left: 35px">
      <el-tooltip content="开启后,节点处理人为空则跳过"
                  placement="top-start"
                  @click.stop.prevent>
        <el-icon class="header-icon"><InfoFilled /></el-icon>
      </el-tooltip>
      <span>为空跳过</span>
      <el-checkbox v-model="isNullSkip" style="margin-left: 22px" @change="handleNullSkip">是否</el-checkbox>
    </div>
    <el-tabs ref="editableTabsValue" :tab-position="tabPosition" v-model=" editableTabsValue"
             @tab-click="handleClickTabs" style="padding-top: 15px">
      <!--(表达式泛指)-->
      <el-tab-pane name="0">
        <template #label>
          <el-tooltip placement="left">
            <template #content>
              <span>固定值主要绑定未知的人员角色表达式(此流程可适用于多个公司)</span>
            </template>
            <span>固定值<el-icon><QuestionFilled /></el-icon></span>
          </el-tooltip>
        </template>
        <el-form label-width="80px" :model="form1">
          <el-form-item label="分配人员" style="margin-bottom: 0px">
            <el-input clearable size="small" v-model="form1.user.expression">
              <template #append>
                <el-button size="small" :icon="Search" @click="handleStand"></el-button>
              </template>
            </el-input>
          </el-form-item>
          <!--          <el-form-item label="候选人员" style="margin-bottom: 0px;">-->
          <!--            <div v-for="(item,index) in form1.standUser" :key="index" style="display: flex">-->
          <!--              <el-tag style="width: 100%;" closable @close="delStandUserTag(index)">{{ item.name }}-->
          <!--              </el-tag>-->
          <!--            </div>-->
          <!--            <el-input ref="standUserInput" v-show="isUser ==true" v-model="standUserInput" size="small"-->
          <!--                      @blur="blurStandUser" @keydown.enter="blurStandUser"></el-input>-->
          <!--            <el-button v-if="isUser==false" size="small" @click="addStandUser">添加</el-button>-->
          <!--          </el-form-item>-->
          <!--          <el-form-item label="候选角色" style="margin-bottom: 0px">-->
          <!--            <div v-for="(item,index) in form1.roleUser" :key="index" style="display: flex">-->
          <!--              <el-tag style="width: 100%;" closable @close="delRoleUserTag(index)">{{ item.name }}-->
          <!--              </el-tag>-->
          <!--            </div>-->
          <!--            <el-input ref="roleUserInput" v-show="isRole ==true" v-model="roleUserInput" size="small"-->
          <!--                      @blur="blurRoleUser" @keydown.enter="blurRoleUser"></el-input>-->
          <!--            <el-button v-if="isRole==false" size="small" @click="addRoleUser">添加</el-button>-->
          <!--          </el-form-item>-->
        </el-form>
      </el-tab-pane>
      <!--(指定人员或角色)-->
      <el-tab-pane name="1">
        <template #label>
          <el-tooltip placement="left">
            <template #content>
              <span>身份存储主要绑定已经确定的人员(此流程只适合单个公司使用)</span>
            </template>
            <span>身份存储<el-icon><QuestionFilled /></el-icon></span>
          </el-tooltip>
        </template>
        <el-form label-width="80px" :model="form2">
          <el-form-item label="分配" style="margin-bottom: 0px">
            <el-select @change="handleChangeOptions" size="small" v-model="form2.distribution">
              <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="分配人员" v-if="form2.distribution=='1'" style="margin-bottom: 0px">
            <div class="user_input" @click="handleOpenOnlyUser()" @mouseover="MouseoverOnlyUser()"
                 @mouseleave="MouseleaveOnlyUser()">
              <el-tag style="margin-left: 10px;margin-bottom: 0px" size="small" v-if="form2.onlyUser.length>0">
                {{ form2.onlyUser[0].nickName }}
              </el-tag>
              <span v-if="form2.onlyUser.length==0"
                    style="color:#91a1b7;margin-left: 10px;font-size: 12px">{{ onlyUserInfo }}</span>
              <div :style="userStyle" class="remove_all_standUser">
                <a @click.stop="clearOnlyUser()" title="清空选取人员">
                  <el-icon class="circle-close-icon" v-if="form2.onlyUser.length>0"><CircleClose /></el-icon>
                </a>
              </div>
            </div>
          </el-form-item>

          <el-form-item label="候选人员" v-if="form2.distribution=='2'" style="margin-bottom: 0px">
            <div class="user_input" @click="handleOpenStand()" @mouseover="MouseoverUser()"
                 @mouseleave="MouseleaveUser()">
              <el-tooltip placement="bottom" effect="light" popper-class="myTooltip"
                          v-for="(item,index) in form2.standUser" :key="index">
                <el-tag style="margin-left: 10px;margin-bottom: 0px" size="small" v-if="form2.standUser.length>0">
                  {{ item.nickName }}
                </el-tag>
                <template #content>
                  <el-row :gutter="10" style="display: flex">
                    <el-col :span="6" style="text-align: center;width: 70px;padding-top: 15px">
                      <div>
                        <el-avatar> {{ item.nickName }}</el-avatar>
                      </div>
                    </el-col>
                    <el-col :span="18">
                      <div>
                        工号:{{}}<br>
                        姓名:{{ item.nickName }}<br>
                        手机:{{ item.phonenumber }}<br>
                        部门:{{ item.deptName }}<br>
                        职位:{{ item.postName }}
                      </div>
                    </el-col>
                  </el-row>
                </template>
              </el-tooltip>
              <span v-if="form2.standUser.length==0"
                    style="color:#91a1b7;margin-left: 10px;font-size: 12px">{{ standUserInfo }}</span>
              <div :style="userStyle" class="remove_all_standUser">
                <a @click.stop="clearStandUser()" title="清空选取人员">
                  <el-icon class="circle-close-icon" v-if="form2.standUser.length>0"><CircleClose /></el-icon>
                </a>
              </div>
            </div>
          </el-form-item>

          <el-form-item label="候选角色" v-if="form2.distribution=='2'" style="margin-bottom: 0px">
            <div class="user_input" @click="handleOpenRole()" @mouseover="MouseoverRole()"
                 @mouseleave="MouseleaveRole()">
              <el-tag style="margin-left: 10px;margin-bottom: 0px" size="small" v-if="form2.standRole.length>0"
                      v-for="(item,index) in form2.standRole" :key="index">
                {{ item.roleName }}
              </el-tag>
              <span v-if="form2.standRole.length==0"
                    style="color:#91a1b7;margin-left: 10px;font-size: 12px">{{ standRoleInfo }}</span>
              <div :style="roleStyle" class="remove_all_standUser">
                <a @click.stop="clearStandRole()" title="清空选取角色">
                  <el-icon class="circle-close-icon" v-if="form2.standRole.length>0"><CircleClose /></el-icon>
                </a>
              </div>
            </div>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>

    <el-form label-width="90px" :model="form" ref="form" style="padding-top: 15px">
      <!--      <el-row>-->
      <!--        <el-col :span="12">-->
      <!--          <el-form-item label="节点选择" style="margin-bottom: 0px">-->
      <!--            <el-select v-model="form.node" size="small" style="width: 100%" @change="handleSelectNode">-->
      <!--              <el-option-->
      <!--                  v-for="item in nodeOptions"-->
      <!--                  :key="item.value"-->
      <!--                  :label="item.label"-->
      <!--                  :value="item.value">-->
      <!--              </el-option>-->
      <!--            </el-select>-->
      <!--          </el-form-item>-->
      <!--        </el-col>-->
      <!--        <el-col :span="12">-->
      <!--          <el-form-item label="可编辑" style="margin-bottom: 0px">-->
      <!--            <el-switch v-model="form.isEdit" @change="handleChangeSwitch">-->
      <!--            </el-switch>-->
      <!--          </el-form-item>-->
      <!--        </el-col>-->
      <!--      </el-row>-->
      <!--      <el-form-item label="到期时间" style="margin-bottom: 0px">-->
      <!--        <el-input v-model="form.stopTime.name" size="small" clearable @focus="handleStopTime"></el-input>-->
      <!--      </el-form-item>-->
      <!--      <el-form-item label="优先级" style="margin-bottom: 0px">-->
      <!--        <el-input-number v-model="form.priority" size="small" :min="1" style="width: 40%;"></el-input-number>-->
      <!--      </el-form-item>-->

      <el-form-item label="授权功能">
        <el-checkbox-group v-model="permissionList" @change="changePermission">
          <!-- 走 permissionOptions：字典 wf_process_permission 没配置时用内置兜底，避免整块空白 -->
          <el-checkbox v-for="(item,index) in permissionOptions" :key="index" :value="item.value" :name="item.value">{{ item.label }}
          </el-checkbox>
        </el-checkbox-group>
      </el-form-item>
    </el-form>

    <div
      v-if="form2.standRole.length>0||form2.standUser.length>1||form.dataType=='0'">
      <!--        v-show="form.dataType=='0'||form2.standRole.length>0||form2.standUser.length>1">-->
      <el-divider/>
      <h4>
        <b>
          <!-- 原写法把该提示挂到父级 <b> 的 label 具名插槽上，但 <b> 并没有具名插槽，vue3 下直接去掉（视觉与逻辑均无变化） -->
          <el-tooltip placement="left">
            <template #content>
              <span>若选择的固定值表达式方式请配置下方多实例中的元素变量与固定值绑定的一致</span>
            </template>
            <span>多实例审批方式<el-icon><QuestionFilled /></el-icon></span>
          </el-tooltip>
        </b>
      </h4>
      <el-row>
        <el-radio-group v-model="multiLoopType" @change="changeMultiLoopType()">
          <el-row>
            <el-radio value="Null">无</el-radio>
          </el-row>
          <el-row>
            <el-radio value="SequentialMultiInstance">会签（需所有审批人同意）</el-radio>
          </el-row>
          <el-row>
            <el-radio value="ParallelMultiInstance">或签（一名审批人同意即可）</el-radio>
          </el-row>
        </el-radio-group>
      </el-row>
      <el-row v-if="multiLoopType !== 'Null'">
        <el-tooltip content="开启后，实例需按顺序轮流审批" placement="top-start" @click.stop.prevent>
          <el-icon class="header-icon"><InfoFilled /></el-icon>
        </el-tooltip>
        <span class="custom-label">顺序审批:</span>
        <el-switch v-model="isSequential" @change="changeMultiLoopType()"/>
      </el-row>
    </div>

    <!--    到期时间自定义弹窗-->
    <el-dialog title="任务到期时间配置" v-model="stopTimeOpen" width="30%" append-to-body>
      <el-form :model="stopForm" label-width="100px">
        <div v-for="(item,index) in stopFormList" :key="item.id">
          <el-form-item :label="`${item.name}:`">
            <el-radio-group :name="item.id" v-model="stopForm.time.id" size="small">
              <el-radio-button v-for="it in item.list" :key="it.value" :value="it.value">
                {{ it.label }}
              </el-radio-button>
            </el-radio-group>
            <el-input-number
              v-if="stopForm.time.id == '5'&&item.id=='1'||
                stopForm.time.id == '10'&&item.id=='2'||
                stopForm.time.id == '15'&&item.id=='3'||
                stopForm.time.id == '20'&&item.id=='4'||
                stopForm.time.id == '25'&&item.id=='5'"
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

    <!--候选人员-->
    <ProcessUser :select-user-data="selectUserData" @updateDialogVisible="getUser"></ProcessUser>
    <!--候选角色-->
    <ProcessRole :select-role-data="selectRoleData" @updateDialogVisible="getRole"/>
    <!--分配人员--表达式-->
    <ProcessStandUser :select-stand-data="selectStandData" @updateDialogVisible="getStandUser "/>
  </div>

</template>

<script>
import { watch } from "vue";
import HeaderSearch from "@/components/HeaderSearch/index.vue";
import hour from "@/components/Crontab/hour.vue";
import ProcessUser from "@/components/ProcessUser/index.vue";
import ProcessRole from "@/components/ProcessRole/index.vue";
import ProcessStandUser from "@/components/ProcessStandUser/index.vue";
// element-plus 图标需按组件引入（element-ui 的 class 字体图标已移除）
// 用 markRaw 避免 Vue 3 把组件对象 reactive 化
import { markRaw } from 'vue'
import { InfoFilled, QuestionFilled, CircleClose, Search } from '@element-plus/icons-vue'
// vue3 版本使用 pinia 的 user store（原 vue2 的全局 store 属性已不存在）
import useUserStore from '@/store/modules/user'

const userTaskForm = {
  assignee: '',   //分配单个人员id 或流程发起人 或  分配人员(固定值)
  candidateUsers: '',   //人员id组  或者候选人员(固定值)
  candidateGroups: '',   //角色标识组(权限标识) 或候选角色(固定值)
  dueDate: '',   //到期时间
  priority: '', //优先级
}

export default {
  name: "UserTask",
  // 注意：vue2 通过 `dicts: ['wf_process_permission']` 选项注入字典，
  // vue3 版 ruoyi 已移除该选项，改为在 created 中调用 useDict 获取，见下方 created()
  computed: {
    hour() {
      return hour
    },
    /**
     * 授权功能候选项。
     * 优先用字典 wf_process_permission（可在「系统管理 → 字典管理」里维护）；
     * 字典没配置时用内置兜底，否则复选框列表为空，「授权功能」整块是空白。
     */
    permissionOptions() {
      const list = (this.dict && this.dict.type && this.dict.type.wf_process_permission) || []
      if (list.length > 0) {
        return list
      }
      return [
        { label: '同意', value: 'complete' },
        { label: '驳回', value: 'reject' },
        { label: '退回', value: 'return' },
        { label: '转办', value: 'transfer' },
        { label: '委派', value: 'delegate' },
        { label: '抄送', value: 'copy' },
        // 会签回退：只有顺序会签节点才会真正生效（审批端还会校验节点是否顺序会签）
        { label: '会签回退', value: 'mulitiSequentialUnComplete' }
      ]
    }
  },
  inject: {
    prefix: "prefix",
    width: "width"
  },
  props: {
    id: String,
    type: String
  },
  components: {HeaderSearch, ProcessUser, ProcessRole, ProcessStandUser, InfoFilled, QuestionFilled, CircleClose, Search},
  data() {
    return {
      // markRaw 包裹组件，避免被 reactive 化
      Search: markRaw(Search),
      tabPosition: 'left',
      dataType: '',
      multiLoopType: 'Null',
      isSequential: false,
      editableTabsValue: '0',
      // 身份存储  分配选择
      options: [
        //     {
        //   value: '1',
        //   label: "分配给单个人"
        // },
        {
          value: '2',
          label: "候选人员"
        }, {
          value: '3',
          label: "分配给流程发起人"
        }],
      // 节点选择数据
      nodeOptions: [
        {
          label: '普通',
          value: '1'
        },
        {
          label: '必审',
          value: '2',
        },
        {
          label: '评审',
          value: '3'
        },
        {
          label: '协同',
          value: '4'
        },
        {
          label: '不审',
          value: '5'
        },
        {
          label: '审批中',
          value: '6'
        }
      ],
      // 鼠标悬停样式
      userStyle: '',
      roleStyle: '',
      // 身份存储表单
      form2: {
        distribution: '2',
        onlyUser: [],   //仅仅分配一个人
        user: '',   //分配给发起人
        standUser: [],  // 候选人员
        standRole: [],   //候选角色
      },
      //固定值表单
      form1: {
        user: {
          expression: '',
        },
        authorityList: [],
        standUser: [],   //候选人员
        roleUser: [],   // 候选角色
      },
      // form1表是否发生了变化
      isForm1: false,
      // form2表是否发生了变化
      isForm2: false,
      //表单
      form: {
        fixedForm: {
          user: {
            expression: '',
          },
          authorityList: [],
          standUser: [],
          roleUser: [],
        },
        standForm: {
          distribution: '',
          onlyUser: [],
          standRole: [],
          standUser: [],
          user: '',
        },
        dataType: '0',
        node: '1',
        isEdit: false,
        stopTime: {
          value: {},
          name: '',
        },
        priority: '',
        //处理人自动跳过
        isSkip: false,
        //交替配置
        isRoleReplace: false,
        //审批配置
        isExamined: false,
        //是否可以强制撤回
        isRevoke: false,
        //授权功能
        permissionList: ["complete", "reject", "return", "transfer", "delegate", "copy", "mulitiSequentialUnComplete"],
        isUserScreen: false,
        isOrgScreen: false,
        //选择机构或者部门进行筛选
        isDeptOrOrg: undefined,
        isNullSkip: false,
      },
      //授权功能
      permissionList: ["complete", "reject", "return", "transfer", "delegate", "copy", "mulitiSequentialUnComplete"],
      //处理人自动跳过
      isSkip: false,
      //审批配置
      isExamined: false,
      //是否可以强制撤回
      isRevoke: false,
      //是否筛选人员
      isUserScreen: false,
      //是否根据组织筛选人员
      isOrgScreen: false,
      //选择机构或者部门进行筛选
      isDeptOrOrg: "dept",
      //交替配置
      isRoleReplace: false,
      //是否处理人为空跳过
      isNullSkip: false,
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
      // 到期时间绑定
      stopTime: '',
      // 到期时间弹窗表单内容
      stopFormList: [
        {
          id: 1,
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
          id: 2,
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
          id: 3,
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
          id: 4,
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
          id: 5,
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
      //到期时间弹窗表单内容自定义
      stopTimeCustom: {
        time: '1',
      },
      selectUserData: { //打包给组件的数据
        dialogVisible: false,//对话框开关
        type:
          "", //“doAudit”是审核状态,填其他的就是选经办人
        users:
          [] //默认选择的人，传入当前的登录人信息
      },
      selectStandData: {
        dialogVisible: false,//对话框开关
        type:
          "", //“doAudit”是审核状态,填其他的就是选经办人
        users:
          [] //默认选择的人，传入当前的登录人信息
      },
      selectRoleData: { //打包给组件的数据
        dialogVisible: false,//对话框开关
        type:
          "", //“doAudit”是审核状态,填其他的就是选经办人
        users:
          [] //默认选择的人，传入当前的登录人信息
      },
      //身份分配弹窗
      standOpen: false,
      // 到期时间配置弹窗
      stopTimeOpen: false,
      standUserInfo: '请选择候选人员',
      standRoleInfo: '请选择候选角色',
      onlyUserInfo: '请选择人员',
      //候选人员是否显示按钮输入框
      isUser: false,
      //候选角色是否显示按钮输入框
      isRole: false,
      //候选人员输入框model
      standUserInput: undefined,
      //角色人员输入框model
      roleUserInput: undefined,
      // 初始化是否进行form表单赋值的方法
      isBpmn: false,
      bpmnElement: undefined,
      // 判断初始化是否进行拓展属性的方法
      isProperty: false,
      bpmnElementPropertyList: [],
      // 字典数据：vue3 版 ruoyi 已移除 vue2 的 `dicts` 选项，
      // 这里保留模板原有的 dict.type.xxx 访问方式，数据由 created() 中的 useDict 填充
      dict: {
        type: {
          wf_process_permission: []
        }
      },
    };
  },
  created() {
    // 等价替换 vue2 的 `dicts: ['wf_process_permission']`（useDict 为全局自动导入的组合式函数）
    const { wf_process_permission } = useDict('wf_process_permission')
    watch(
      wf_process_permission,
      (val) => {
        this.dict.type.wf_process_permission = val || []
      },
      { immediate: true, deep: true }
    )
  },
  watch: {
    // 最终数据存放
    form: {
      handler(newVal, oldVal) {
        this.form = newVal;
        this.$nextTick(() => {
          this.handleUserTaskForm(newVal);
        })
      },
      deep: true, //深度监听form里的每个对象
    },
    // 固定值表单
    form1: {
      handler(newVal, oldVal) {
        // 容错：历史坏数据/初始化过程中字段可能为 undefined，
        // 直接取 .length / .expression 会抛异常并中断整个 watcher
        const user = newVal.user || { expression: '' };
        const standUser = newVal.standUser || [];
        const roleUser = newVal.roleUser || [];
        const authorityList = newVal.authorityList || [];
        this.isForm1 = true;
        if (user.expression == '' && standUser.length === 0 && roleUser.length === 0) {
          this.isForm1 = false;
        }
        this.form.fixedForm = newVal;
        if (user.expression == '') {
          if (authorityList.length > 0) {
            this.removeAttributes('flowable:IdmAuthority')
            this.form1.authorityList = [];
            this.form.fixedForm.authorityList = [];
          }
        }
      },
      deep: true,
    },
    // 身份存储表单
    form2: {
      handler(newVal, oldVal) {
        // 容错：同上，standUser / onlyUser / standRole 可能为 undefined
        const onlyUser = newVal.onlyUser || [];
        const standUser = newVal.standUser || [];
        const standRole = newVal.standRole || [];
        this.isForm2 = true;
        if (newVal.user == '' && onlyUser.length == 0 && standUser.length == 0 && standRole.length == 0) {
          this.isForm2 = false;
        }
        this.form.standForm = newVal;
      },
      deep: true,
    },
    // 选中模块UserTask
    id: {
      immediate: true,
      deep: true,
      handler() {
        // markRaw 防止 Vue 3 把 bpmn-js 的 element 对象包成 Proxy，
        // 否则 moddle 内部访问 element.businessObject.labels 等只读属性时会抛
        // 'get' on proxy: property 'labels' is a read-only and non-configurable data property 错误
        this.bpmnElement = markRaw(window.bpmnInstances.bpmnElement);
        // 节点初始化时不进行监测form变化
        this.isBpmn = true
        this.$nextTick(() => this.resetTaskForm());
      }
    },
  },
  beforeUnmount() {
    this.bpmnElement = null;
  },
  methods: {
    //配置按钮权限
    changePermission(val) {
      this.form.permission = val;
      const IdmPermission = JSON.stringify(val);
      this.isProperty = true;
      this.updateElementIdm(IdmPermission, "idmPermission")
    },
    // 处理数据
    handleUserTaskForm(form) {
      if (this.isBpmn) {
        return;
      }
      Object.keys(userTaskForm).forEach(key => {
        userTaskForm[key] = null
      });
      userTaskForm.priority = form.priority;
      userTaskForm.dueDate = form.stopTime.name;
      // 固定值 / 身份存储 的判定不能只依赖 this.isForm1 / this.isForm2：
      // 这两个标志由 form1/form2 的 watcher 异步设置，而 form 的 watcher 注册在前、先触发，
      // 于是「本次改动」里读到的还是上一轮的旧值 —— 表现为刚选完候选人却写不进 bpmn 模型，
      // 再点一次节点候选人就空了。这里按当前表单数据实时判定，保证取到的是最新值。
      const fixedForm = form.fixedForm || {};
      const standForm = form.standForm || {};
      const hasFixedData = !!(fixedForm.user && fixedForm.user.expression)
        || (fixedForm.standUser || []).length > 0
        || (fixedForm.roleUser || []).length > 0
        || (fixedForm.authorityList || []).length > 0;
      const hasStandData = !!standForm.user
        || (standForm.onlyUser || []).length > 0
        || (standForm.standUser || []).length > 0
        || (standForm.standRole || []).length > 0;
      // 固定值
      if (this.isForm1 || hasFixedData) {
        // 分配人员
        if (form.fixedForm.user.expression) {
          userTaskForm.assignee = form.fixedForm.user.expression;
        }
        // 候选人员  数组  转换JSON
        userTaskForm.candidateUsers = this.getArrToStr(form.fixedForm.standUser || [], "FIXED");
        //候选角色  数组  转换JSON
        if ((form.fixedForm.authorityList || []).length > 0) {
          userTaskForm.candidateGroups = this.getArrToStr(form.fixedForm.roleUser || [], "FIXED")
        }
        const IdmAuthority = JSON.stringify(form.fixedForm.authorityList || [], "FIXED");
        this.updateElementIdm(IdmAuthority, "IdmAuthority");
      }
      // 身份存储
      if (this.isForm2 || hasStandData) {
        // 候选角色  角色  idmCandidateGroups
        if ((form.standForm.standRole || []).length > 0) {
          userTaskForm.candidateGroups = this.getArrToStr(form.standForm.standRole, "ROLE")
          const idmCandidateGroups = JSON.stringify(form.standForm.standRole);
          this.updateElementIdm(idmCandidateGroups, "idmCandidateGroups");
        }
        // 候选人员（多人） idmCandidateUsers
        // 只在「确实选了候选人」时写入。
        // 千万不要写空数组 —— 那会在节点上留下一个空的 idmAssignee / idmCandidateUsers 扩展，
        // 重新打开节点时它会抢着把「分配」方式改成另一种（例如空的 idmAssignee 把分配改成"分配人员"），
        // 于是原来看的候选人员块被隐藏、面板显示为空，看起来就像数据丢了。
        if ((form.standForm.standUser || []).length > 0) {
          userTaskForm.candidateUsers = this.getArrToStr(form.standForm.standUser, "USER")
          this.updateElementIdm(JSON.stringify(form.standForm.standUser), "idmCandidateUsers");
        }
        // 分配人员（单人） idmAssignee：同样只在有值时才写
        if ((form.standForm.onlyUser || []).length > 0) {
          userTaskForm.assignee = form.standForm.onlyUser[0].userId
          this.updateElementIdm(JSON.stringify(form.standForm.onlyUser), "idmAssignee");
        }
        // 流程发起人
        if (form.standForm.user) {
          userTaskForm.assignee = form.standForm.user
        }
      }
      this.updateElementTask();
    },

    // 跟新节点数据
    updateElementTask() {
      const taskAttr = Object.create(null);
      // 用 Object.keys 而非 for...in，避免 Vue 3 Proxy 把原型链上的
      // read-only 属性（如 labels）带进 taskAttr，触发 modeling.updateProperties 抛错
      for (const key of Object.keys(userTaskForm)) {
        taskAttr[key] = userTaskForm[key];
      }
      if (this.bpmnElement.businessObject.loopCharacteristics && this.form.dataType === '1') {
        taskAttr.assignee = "${assignee}";
      }
      window.bpmnInstances.modeling.updateProperties(this.bpmnElement, taskAttr);
    },

    // 数组标签  创建拓展属性    item(JSON数据)   value(标识idmCandidateUsers )---标识即拓展标签
    updateElementIdm(item, value) {
      const body = item;
      // 新建属性字段
      var newPropertyObject;
      // 初始化不进行建立属性操作
      if (!this.isProperty) {
        return;
      } else if (value === 'IdmAuthority') {
        var frame = false;
        var bpmnList;
        this.bpmnElementPropertyList.forEach(item => {
          // 判断字段是否已经建立了
          if (item.$type === 'flowable:IdmAuthority') {
            frame = true;
            bpmnList = item;
          }
        })
        // 已经存在了字段，即更新字段
        if (frame) {
          window.bpmnInstances.modeling.updateModdleProperties(this.bpmnElement,
            bpmnList, {body});
        }
        // 不存在
        else {
          newPropertyObject = window.bpmnInstances.moddle.create(`${this.prefix}:IdmAuthority`, {body});
          this.updateElementExtensions(newPropertyObject);
        }
        this.resetAttributesList();
      }
      // 候选人员
      else if (value === 'idmCandidateUsers') {
        var frame = false;
        var bpmnList;
        this.bpmnElementPropertyList.forEach(item => {
          // 判断字段是否已经建立了
          if (item.$type === 'flowable:IdmCandidateUsers') {
            frame = true;
            bpmnList = item;
          }
        })
        // 已经存在了字段，即更新字段
        if (frame) {
          window.bpmnInstances.modeling.updateModdleProperties(this.bpmnElement,
            bpmnList, {body});
        }
        // 不存在
        else {
          newPropertyObject = window.bpmnInstances.moddle.create(`${this.prefix}:IdmCandidateUsers`, {body});
          this.updateElementExtensions(newPropertyObject);
        }
        this.resetAttributesList();
      }
      // 候选角色
      else if (value === 'idmCandidateGroups') {
        var frame = false;
        var bpmnList;
        this.bpmnElementPropertyList.forEach(item => {
          // 判断字段是否已经建立了
          if (item.$type === 'flowable:IdmCandidateGroups') {
            frame = true;
            bpmnList = item;
          }
        })
        // 已经存在了字段，即更新字段
        if (frame) {
          window.bpmnInstances.modeling.updateModdleProperties(this.bpmnElement,
            bpmnList, {body});
        }
        // 不存在
        else {
          newPropertyObject = window.bpmnInstances.moddle.create(`${this.prefix}:IdmCandidateGroups`, {body});
          // 新建一个属性字段的保存列表
          this.updateElementExtensions(newPropertyObject);
        }
        this.resetAttributesList();
      }
      // 授权功能
      else if (value === "idmPermission") {
        var frame = false;
        var bpmnList;
        this.bpmnElementPropertyList.forEach(item => {
          // 判断字段是否已经建立了
          if (item.$type === 'flowable:IdmPermission') {
            frame = true;
            bpmnList = item;
          }
        })
        // 已经存在了字段，即更新字段
        if (frame) {
          window.bpmnInstances.modeling.updateModdleProperties(this.bpmnElement,
            bpmnList, {body});
        }
        // 不存在
        else {
          newPropertyObject = window.bpmnInstances.moddle.create(`${this.prefix}:IdmPermission`, {body});
          // 新建一个属性字段的保存列表
          this.updateElementExtensions(newPropertyObject);
        }
        this.resetAttributesList();
      }
      // 分配人员--单个人
      else if (value === 'idmAssignee') {
        var frame = false;
        var bpmnList;
        this.bpmnElementPropertyList.forEach(item => {
          // 判断字段是否已经建立了
          if (item.$type === 'flowable:IdmAssignee') {
            frame = true;
            bpmnList = item;
          }
        })
        // 已经存在了字段，即更新字段
        if (frame) {
          window.bpmnInstances.modeling.updateModdleProperties(this.bpmnElement,
            bpmnList, {body});
        }
        // 不存在
        else {
          newPropertyObject = window.bpmnInstances.moddle.create(`${this.prefix}:IdmAssignee`, {body});
          // 新建一个属性字段的保存列表
          this.updateElementExtensions(newPropertyObject);
        }
        this.resetAttributesList();
      }
    },

    // 更新bpmn拓展属性字段属性
    updateElementExtensions(properties) {
      const extensions = window.bpmnInstances.moddle.create("bpmn:ExtensionElements", {
        values: this.otherExtensionList.concat([properties])
      });
      window.bpmnInstances.modeling.updateProperties(this.bpmnElement, {
        extensionElements: extensions
      });
    },

    // 初始拓展属性
    resetAttributesList() {
      // markRaw 防止 bpmnElement 被 Vue 3 深度 reactive 化
      this.bpmnElement = markRaw(window.bpmnInstances.bpmnElement);
      this.otherExtensionList = []; // 其他扩展配置
      // 保存所有的 扩展属性字段
      this.bpmnElementProperties =
        this.bpmnElement.businessObject?.extensionElements?.values?.filter(ex => {
          if (ex.$type !== `${this.prefix}:IdmCandidateUsers` || ex.$type !== `${this.prefix}:IdmCandidateGroups`
            || ex.$type !== `${this.prefix}:IsEditData` || ex.$type !== `${this.prefix}:NodeType`
            || ex.$type !== `${this.prefix}:AssigneeType` || ex.$type !== `${this.prefix}:IdmAuthority`
            || ex.$type !== `${this.prefix}:IsSkip` || ex.$type !== `${this.prefix}:isRoleReplace`
            || ex.$type !== `${this.prefix}:IdmAssignee` || ex.$type !== `${this.prefix}:IsExamined`
            || ex.$type !== `${this.prefix}:IsRevoke` || ex.$type !== `${this.prefix}:IdmPermission`
            || ex.$type !== `${this.prefix}:IsUserScreen` || ex.$type !== `${this.prefix}:IsOrgScreen`
            || ex.$type !== `${this.prefix}:IsDeptOrOrg` || ex.$type !== `${this.prefix}:IsNullSkip`) {
            this.otherExtensionList.push(ex);
          }
          return ex.$type === `${this.prefix}:IdmCandidateUsers` || ex.$type === `${this.prefix}:IdmCandidateGroups`
            || ex.$type === `${this.prefix}:IsEditData` || ex.$type === `${this.prefix}:NodeType`
            || ex.$type === `${this.prefix}:IdmAuthority` || ex.$type === `${this.prefix}:IsSkip`
            || ex.$type === `${this.prefix}:AssigneeType` || ex.$type === `${this.prefix}:IdmAssignee`
            || ex.$type === `${this.prefix}:isRoleReplace` || ex.$type === `${this.prefix}:IsExamined`
            || ex.$type === `${this.prefix}:IsRevoke` || ex.$type === `${this.prefix}:IdmPermission`
            || ex.$type === `${this.prefix}:IsUserScreen` || ex.$type === `${this.prefix}:IsOrgScreen`
            || ex.$type === `${this.prefix}:IsDeptOrOrg` || ex.$type === `${this.prefix}:IsNullSkip`
        }) ?? [];
      this.bpmnElementPropertyList = this.bpmnElementProperties.reduce((pre, current) => pre.concat(current), []);
    },
    //是部门筛选还是组织筛选
    handelSelectOrgOrDept(value) {
      this.form.isDeptOrOrg = value;
      const body = value
      var frame = false;
      var bpmnList;
      this.bpmnElementPropertyList.forEach(item => {
        // 判断字段是否已经建立了
        if (item.$type === 'flowable:IsDeptOrOrg') {
          frame = true;
          bpmnList = item;
        }
      })
      // 已经存在了字段，即更新字段
      if (frame) {
        window.bpmnInstances.modeling.updateModdleProperties(this.bpmnElement,
          bpmnList, {body});
      }
      // 不存在
      else {
        const newPropertyObject = window.bpmnInstances.moddle.create(`${this.prefix}:IsDeptOrOrg`, {body});
        // 新建一个属性字段的保存列表
        this.updateElementExtensions(newPropertyObject);
      }
      this.resetAttributesList();
    },
    //组织筛选
    handleOrgScreen(value) {
      this.form.isOrgScreen = value;
      const body = value
      var frame = false;
      var bpmnList;
      this.bpmnElementPropertyList.forEach(item => {
        // 判断字段是否已经建立了
        if (item.$type === 'flowable:IsOrgScreen') {
          frame = true;
          bpmnList = item;
        }
      })
      // 已经存在了字段，即更新字段
      if (frame) {
        window.bpmnInstances.modeling.updateModdleProperties(this.bpmnElement,
          bpmnList, {body});
      }
      // 不存在
      else {
        const newPropertyObject = window.bpmnInstances.moddle.create(`${this.prefix}:IsOrgScreen`, {body});
        // 新建一个属性字段的保存列表
        this.updateElementExtensions(newPropertyObject);
      }
      this.resetAttributesList();
      //默认选择部门筛选
      if (value) {
        this.handelSelectOrgOrDept("dept");
      } else {
        this.handelSelectOrgOrDept("null")
      }
    },
    //强制撤回
    handleRevoke(value) {
      this.form.isRevoke = value;
      const body = value
      var frame = false;
      var bpmnList;
      this.bpmnElementPropertyList.forEach(item => {
        // 判断字段是否已经建立了
        if (item.$type === 'flowable:IsRevoke') {
          frame = true;
          bpmnList = item;
        }
      })
      // 已经存在了字段，即更新字段
      if (frame) {
        window.bpmnInstances.modeling.updateModdleProperties(this.bpmnElement,
          bpmnList, {body});
      }
      // 不存在
      else {
        const newPropertyObject = window.bpmnInstances.moddle.create(`${this.prefix}:IsRevoke`, {body});
        // 新建一个属性字段的保存列表
        this.updateElementExtensions(newPropertyObject);
      }
      this.resetAttributesList();
    },
    //是否筛选人员
    handleUserScreen(value) {
      this.form.isUserScreen = value;
      const body = value
      var frame = false;
      var bpmnList;
      this.bpmnElementPropertyList.forEach(item => {
        // 判断字段是否已经建立了
        if (item.$type === 'flowable:isUserScreen') {
          frame = true;
          bpmnList = item;
        }
      })
      // 已经存在了字段，即更新字段
      if (frame) {
        window.bpmnInstances.modeling.updateModdleProperties(this.bpmnElement,
          bpmnList, {body});
      }
      // 不存在
      else {
        const newPropertyObject = window.bpmnInstances.moddle.create(`${this.prefix}:IsUserScreen`, {body});
        // 新建一个属性字段的保存列表
        this.updateElementExtensions(newPropertyObject);
      }
      this.resetAttributesList();
    },
    //审批配置
    handleExamined(value) {
      this.form.isExamined = value;
      const body = value
      var frame = false;
      var bpmnList;
      this.bpmnElementPropertyList.forEach(item => {
        // 判断字段是否已经建立了
        if (item.$type === 'flowable:IsExamined') {
          frame = true;
          bpmnList = item;
        }
      })
      // 已经存在了字段，即更新字段
      if (frame) {
        window.bpmnInstances.modeling.updateModdleProperties(this.bpmnElement,
          bpmnList, {body});
      }
      // 不存在
      else {
        const newPropertyObject = window.bpmnInstances.moddle.create(`${this.prefix}:IsExamined`, {body});
        // 新建一个属性字段的保存列表
        this.updateElementExtensions(newPropertyObject);
      }
      this.resetAttributesList();
    },
    //交替配置
    handleRoleReplace(value) {
      this.form.isRoleReplace = value;
      const body = value
      var frame = false;
      var bpmnList;
      this.bpmnElementPropertyList.forEach(item => {
        // 判断字段是否已经建立了
        if (item.$type === 'flowable:IsRoleReplace') {
          frame = true;
          bpmnList = item;
        }
      })
      // 已经存在了字段，即更新字段
      if (frame) {
        window.bpmnInstances.modeling.updateModdleProperties(this.bpmnElement,
          bpmnList, {body});
      }
      // 不存在
      else {
        const newPropertyObject = window.bpmnInstances.moddle.create(`${this.prefix}:IsRoleReplace`, {body});
        // 新建一个属性字段的保存列表
        this.updateElementExtensions(newPropertyObject);
      }
      this.resetAttributesList();
    },
    //处理人为空跳过
    handleNullSkip(value) {
      this.form.isNullSkip = value;
      const body = value
      var frame = false;
      var bpmnList;
      this.bpmnElementPropertyList.forEach(item => {
        // 判断字段是否已经建立了
        if (item.$type === 'flowable:IsNullSkip') {
          frame = true;
          bpmnList = item;
        }
      })
      // 已经存在了字段，即更新字段
      if (frame) {
        window.bpmnInstances.modeling.updateModdleProperties(this.bpmnElement,
          bpmnList, {body});
      }
      // 不存在
      else {
        const newPropertyObject = window.bpmnInstances.moddle.create(`${this.prefix}:IsNullSkip`, {body});
        // 新建一个属性字段的保存列表
        this.updateElementExtensions(newPropertyObject);
      }
      this.resetAttributesList();
    },
    //处理人跳过设置
    handleSkip(value) {
      this.form.isSkip = value;
      const body = value
      var frame = false;
      var bpmnList;
      this.bpmnElementPropertyList.forEach(item => {
        // 判断字段是否已经建立了
        if (item.$type === 'flowable:IsSkip') {
          frame = true;
          bpmnList = item;
        }
      })
      // 已经存在了字段，即更新字段
      if (frame) {
        window.bpmnInstances.modeling.updateModdleProperties(this.bpmnElement,
          bpmnList, {body});
      }
      // 不存在
      else {
        const newPropertyObject = window.bpmnInstances.moddle.create(`${this.prefix}:IsSkip`, {body});
        // 新建一个属性字段的保存列表
        this.updateElementExtensions(newPropertyObject);
      }
      this.resetAttributesList();
    },

    // 节点选择变化  创建拓展属性标签   NodeType
    handleSelectNode(value) {
      const body = value
      var frame = false;
      var bpmnList;
      this.bpmnElementPropertyList.forEach(item => {
        // 判断字段是否已经建立了
        if (item.$type === 'flowable:NodeType') {
          frame = true;
          bpmnList = item;
        }
      })
      // 已经存在了字段，即更新字段
      if (frame) {
        window.bpmnInstances.modeling.updateModdleProperties(this.bpmnElement,
          bpmnList, {body});
      }
      // 不存在
      else {
        const newPropertyObject = window.bpmnInstances.moddle.create(`${this.prefix}:NodeType`, {body});
        // 新建一个属性字段的保存列表
        this.updateElementExtensions(newPropertyObject);
      }
      this.resetAttributesList();
    },

    // 可编辑开关变化  创建拓展属性标签 IsEditData
    handleChangeSwitch(value) {
      const body = value;
      var frame = false;
      var bpmnList;
      this.bpmnElementPropertyList.forEach(item => {
        // 判断字段是否已经建立了
        if (item.$type == 'flowable:IsEditData') {
          frame = true;
          bpmnList = item;
        }
      })
      // 已经存在了字段，即更新字段
      if (frame) {
        window.bpmnInstances.modeling.updateModdleProperties(this.bpmnElement,
          bpmnList, {body});
      }
      // 不存在
      else {
        const newPropertyObject = window.bpmnInstances.moddle.create(`${this.prefix}:IsEditData`, {body});
        // 新建一个属性字段的保存列表
        this.updateElementExtensions(newPropertyObject);
      }
      this.resetAttributesList();
    },

    // 初始化表单
    clearFormInfo() {
      this.isBpmn = true;
      // 身份存储表单
      this.form2 = {
        distribution: '2',
        onlyUser: [],   //仅仅分配一个人
        user: '',   //分配给发起人
        standUser: [],  // 候选人员
        standRole: [],   //候选角色
      };
      //固定值表单
      this.form1 = {
        user: {
          expression: '',
        },
        authorityList: [],
        standUser: [],   //候选人员
        roleUser: [],   // 候选角色
      };
      //表单
      this.form = {
        fixedForm: {
          user: {
            expression: '',
          },
          authorityList: [],
          standUser: [],
          roleUser: [],
        },
        standForm: {
          distribution: '2',
          onlyUser: [],
          standRole: [],
          standUser: [],
          user: '',
        },
        dataType: '0',
        node: '1',
        isEdit: false,
        stopTime: {
          value: {},
          name: '',
        },
        priority: '',
        isSkip: false,
        isRoleReplace: false,
        isExamined: false,
        isRevoke: false,
        isUserScreen: false,
        isOrgScreen: false,
        IsDeptOrOrg: '',
        IsNullSkip: false,
      };
      this.isSkip = false;
      this.IsNullSkip = false;
      this.isRoleReplace = false;
      this.isExamined = false;
      this.isRevoke = false;
      this.isUserScreen = false;
      this.isOrgScreen = false;
      this.IsDeptOrOrg = '';
      // 到期时间配置表单
      this.stopForm = {
        time: {
          id: '',
          name: '',
          num: '',
        }
      };
    },

    //选完人员后根据返回的数据进行操作
    getUser(row) {
      if (row.saveBtn) {
        this.isProperty = true;
        // 多个人员
        if (row.info == false) {
          this.form2.standUser = row.data;
          this.isForm2 = true;
          // 「身份存储」下的配置必须让 AssigneeType=1，否则重开设计器会按 assigneeType=0
          // 走"固定值"分支去读 candidateUsers，候选人员就不回显（用户反馈的根因）
          this.syncAssigneeType('1');
          // 立刻写回 bpmn 模型：不再只依赖 form/form2 的 watcher 链。
          // watcher 的触发顺序和 $nextTick 时序一旦有偏差，就会出现
          //「选完候选人界面显示了、但没写进模型，再点这个节点又空了」。
          this.$nextTick(() => {
            this.updateElementIdm(JSON.stringify(this.form2.standUser || []), 'idmCandidateUsers');
          });
        }
        //单个人员
        if (row.info == true) {
          this.form2.onlyUser = row.data;
          this.isForm2 = true;
          this.syncAssigneeType('1');
          this.$nextTick(() => {
            this.updateElementIdm(JSON.stringify(this.form2.onlyUser || []), 'idmAssignee');
          });
        }
      }
    },

    /**
     * 同步「固定值(0) / 身份存储(1)」标记到节点的 AssigneeType 扩展属性。
     *
     * 身份存储下的所有配置（候选人员 / 候选角色 / 分配人员）都依赖它：
     * 只写 idmCandidateUsers 而不同步 AssigneeType，重开设计器就会走固定值分支、不回显。
     */
    syncAssigneeType(value) {
      try {
        this.form.dataType = value;
        this.editableTabsValue = value;
        // 复用现成的写入逻辑：存在则更新 body，不存在则创建
        this.changeTabs();
      } catch (e) {
        console.warn('[UserTask] 同步 AssigneeType 失败：', e && e.message);
      }
    },

    // 选完角色后根据返回的数据进行操作
    getRole(row) {
      if (row.saveBtn) {
        this.form2.standRole = row.data;
        this.isForm2 = true;
        this.isProperty = true;
        // 候选角色同样属于「身份存储」，一并同步标记
        this.syncAssigneeType('1');
        // 同上：立刻写回，保证候选角色也不丢
        this.$nextTick(() => {
          this.updateElementIdm(JSON.stringify(this.form2.standRole || []), 'idmCandidateGroups');
        });
      }
    },

    // 选完分配人员后根据返回的数据进行操作
    getStandUser(row) {
      if (row.saveBtn) {
        let data = row.data;
        this.form1.user.expression = data[0].expression;
        this.form1.authorityList = row.data;
        this.isForm1 = true;
        this.isProperty = true;
      }
    },

    // 解析 idmCandidate* / idmAssignee 的 body 字符串
    //  - 正常情况：完整 JSON 数组 -> 解析回对象数组
    //  - 异常情况：内容被截断（历史数据被 Flowable 解析器按文本分片截断）-> 返回空数组
    // 注意两点：
    //  1) moddle 的 body 不保证一定是字符串：文本/CDATA 分段时可能是字符串数组，
    //     也可能被包成 { body: ... }，这里统一归一化后再判断，
    //     否则一个 typeof 判断就会把正常数据显示成空。
    //  2) 必须返回数组而不是 undefined，否则上层 `.length` 会抛异常、getArrToStr 也会炸。
    parseIdmBodyOrFallback(body) {
      if (body && typeof body === 'object') {
        if (Array.isArray(body)) {
          body = body.join('')
        } else if (typeof body.body === 'string') {
          body = body.body
        }
      }
      if (!body || typeof body !== 'string') return []
      const trimmed = body.trim()
      if (!trimmed) return []
      // 合法 JSON 数组必须以 [ 开头；被截断的历史数据常常以 , 开头，直接判定为坏数据
      if (!trimmed.startsWith('[')) {
        console.warn('[UserTask] 扩展元素内容疑似被截断，已忽略这段坏数据：', trimmed)
        return []
      }
      try {
        const parsed = JSON.parse(trimmed)
        return Array.isArray(parsed) ? parsed : []
      } catch (e) {
        console.warn('[UserTask] 扩展元素 JSON 解析失败，已忽略：', e.message, trimmed)
        return []
      }
    },

    // 将数组内的id拼接字符串   返回拼接好的字符串id  eg:1,2,3
    getArrToStr(arr, value) {
      var str = "";
      // 人员
      if (value == "USER") {
        arr.forEach(item => {
          str += item.userId + ',';
        })
      }
      // 候选角色
      if (value == "ROLE") {
        arr.forEach(item => {
          str += item.roleId + ',';
        })
      }
      // 手动输入的信息
      if (value == "FIXED") {
        arr.forEach(item => {
          str += item.name + ',';
        })
      }
      //去掉最后一个逗号(如果不需要去掉，就不用写)
      if (str.length > 0) {
        str = str.substr(0, str.length - 1);
      }
      return str;
    },

    //鼠标悬停触发方法
    MouseoverOnlyUser() {
      this.userStyle = "display:block";
    },
    // 鼠标离开触发
    MouseleaveOnlyUser() {
      this.userStyle = "display:none";
    },
    // 清空单个人选择
    clearOnlyUser() {
      this.form2.onlyUser = [];
      this.isProperty = true;
      this.removeAttributes('flowable:IdmCandidateUsers')
    },
    // 分配单个人
    handleOpenOnlyUser() {
      let users = [];
      users.push({
        id: useUserStore().id,
        name: useUserStore().name
      })
      //传给选择审核人的数据包
      this.selectUserData = {
        info: true,
        data: this.form2.onlyUser,
        dialogVisible: true,
        type: "doOperator",
        users: users
      };
    },

    // 候选人员输入框点击
    handleOpenStand() {
      let users = [];
      users.push({
        id: useUserStore().id,
        name: useUserStore().name
      })
      //传给选择审核人的数据包
      this.selectUserData = {
        info: false,
        data: this.form2.standUser,
        dialogVisible: true,
        type: "doOperator",
        users: users
      };
    },
    //鼠标悬停触发方法
    MouseoverUser() {
      this.userStyle = "display:block";
    },
    // 鼠标离开触发
    MouseleaveUser() {
      this.userStyle = "display:none";
    },
    // 候选人员清空按钮
    clearStandUser() {
      this.form2.standUser = [];
      this.isProperty = true;
      this.removeAttributes('flowable:IdmCandidateUsers')
    },

    // 清空属性 重新添加 标签
    removeAttributes(value) {
      //删除选择的数据
      for (let i = 0; i < this.bpmnElementPropertyList.length; i++) {
        if (this.bpmnElementPropertyList[i].$type == value) {
          this.bpmnElementPropertyList.splice(i, 1);
        }
      }
      // 暂存剩余的拓展属性内容
      const extensions = window.bpmnInstances.moddle.create("bpmn:ExtensionElements", {
        values: this.bpmnElementPropertyList
      });
      //更新拓展内容
      window.bpmnInstances.modeling.updateProperties(this.bpmnElement, {
        extensionElements: extensions
      })
      this.resetAttributesList();
    },

    //添加候选人员
    addStandUser() {
      this.isUser = true;
      this.$nextTick(_ => {
        this.$refs.standUserInput.$refs.input.focus();
      });
    },

    //候选人员输入框失去焦点事件
    blurStandUser() {
      this.isUser = false;
      if (this.standUserInput == undefined) {
        return;
      }
      this.form1.standUser.push({
        name: this.standUserInput
      })
      this.standUserInput = undefined;
    },

    //候选人员标签删除
    delStandUserTag(index) {
      this.form1.standUser.splice(index, 1);
    },

    //添加候选角色
    addRoleUser() {
      this.isRole = true;
      this.$nextTick(_ => {
        this.$refs.roleUserInput.$refs.input.focus();
      });
    },

    // 候选角色输入框点击
    handleOpenRole() {
      let users = [];
      users.push({
        id: useUserStore().id,
        name: useUserStore().name
      })
      //传给选择审核人的数据包
      this.selectRoleData = {
        data: this.form2.standRole,
        dialogVisible: true,
        type: "doOperator",
        users: users
      };
    },

    //鼠标悬停触发方法
    MouseoverRole() {
      this.roleStyle = "display:block";
    },

    // 鼠标离开触发
    MouseleaveRole() {
      this.roleStyle = "display:none";
    },
    // 候选角色清空按钮
    clearStandRole() {
      this.form2.standRole = [];
      this.isProperty = true;
      this.removeAttributes('flowable:IdmCandidateGroups')
    },

    //候选角色输入框失去焦点事件
    blurRoleUser() {
      this.isRole = false;
      if (this.roleUserInput == undefined) {
        return;
      }
      this.form1.roleUser.push({
        name: this.roleUserInput
      })
      this.roleUserInput = undefined;
    },

    //候选角色标签删除
    delRoleUserTag(index) {
      this.form1.roleUser.splice(index, 1);
    },

    // 固定值切换身份存储
    handleClickTabs(targetName, action) {
      // element-plus 的 tab-click 回调第一个参数是 TabsPaneContext（不再是 tab-pane 组件实例），
      // 面板名需从 props.name 读取，与原 element-ui 的 targetName.name 等价
      const paneName = targetName?.props?.name ?? targetName?.paneName;
      this.form.dataType = paneName;
      this.isProperty = false;
      if (this.isForm1 || this.isForm2) {
        this.$confirm('此操作将清除已选的数据, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.multiLoopType = 'Null';
          this.isSequential = false;
          this.form1 = {
            user: {
              expression: '',
            },
            authorityList: [],
            standUser: [],
            roleUser: [],
          };
          this.form2 = {
            distribution: '2',
            onlyUser: [],
            user: '',
            standUser: [],
            standRole: [],
          }
          this.isForm1 = false;
          this.isForm2 = false;
          this.form.dataType = paneName;
          // 情况form存放标签内的内容
          Object.keys(userTaskForm).forEach(key => {
            userTaskForm[key] = null
          })
          // 清空拓展标签内容
          const extensions = window.bpmnInstances.moddle.create("bpmn:ExtensionElements", {
            values: []
          });
          window.bpmnInstances.modeling.updateProperties(this.bpmnElement, {
            extensionElements: extensions
          })
          this.changeTabs();
        }).catch(action => {
          if (action == 'cancel') {
            if (paneName == '1') {
              this.editableTabsValue = '0';
              this.form.dataType = '0'
            } else {
              this.editableTabsValue = '1'
              this.form.dataType = '0'
            }
            this.changeTabs();
          }
        })
      } else {
        this.changeTabs();
      }
    },

    // 固定值/身份存储切换=>xml
    changeTabs() {
      var frame = false;
      var bpmnList;
      const body = this.editableTabsValue
      this.bpmnElementPropertyList.forEach(item => {
        // 判断字段是否已经建立了
        if (item.$type == 'flowable:AssigneeType') {
          frame = true;
          bpmnList = item;
        }
      })
      // 已经存在了字段，即更新字段
      if (frame) {
        window.bpmnInstances.modeling.updateModdleProperties(this.bpmnElement,
          bpmnList, {body});
      }
      // 不存在
      else {
        const newPropertyObject = window.bpmnInstances.moddle.create(`${this.prefix}:AssigneeType`, {body});
        this.updateElementExtensions(newPropertyObject);
      }
      this.resetAttributesList();
    },

    // 到期时间获焦事件
    handleStopTime() {
      this.stopTimeOpen = true;
      var stopTime = this.form.stopTime.name;
      if (!stopTime) {
        return;
      }
      var start = stopTime.charAt(0);  // P-->
      var end = stopTime.substr(-1)   // 获取是 天，小时，周，月  H,D,W,M
      //获取时间具体-->数字  eg:P10D  获取10
      var time = stopTime.substring(stopTime.lastIndexOf(start) + 1, stopTime.lastIndexOf(end));
      var dateTime = false;
      //判断是一天以内的时间，而不是周、月、天
      if (stopTime.includes("T")) {
        start = start + "T";
        time = time.substring(1)
        dateTime = true;
      }
      this.stopFormList.forEach(item => {
        // 先判断选择的时间    是 天，小时，周，月  H,D,W,M
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
          if (dateTime && end === 'M') {
            if (isStopTime) {
              this.stopForm.time.id = '5';
            }
          }
        }
      })
    },
    // 到期时间弹窗配置 确定按钮
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
      this.form.stopTime.value = this.stopForm.time;
      let day;
      if (this.stopForm.time.name === '小时') {
        day = 'H';
        this.form.stopTime.name = 'PT' + this.stopForm.time.num + day;
      } else if (this.stopForm.time.name === '天') {
        day = 'D'
        this.form.stopTime.name = 'P' + this.stopForm.time.num + day;
      } else if (this.stopForm.time.name === '周') {
        day = 'W'
        this.form.stopTime.name = 'P' + this.stopForm.time.num + day;
      } else if (this.stopForm.time.name === '月') {
        day = 'M'
        this.form.stopTime.name = 'P' + this.stopForm.time.num + day;
      } else if (this.stopForm.time.name === '分钟') {
        day = 'M'
        this.form.stopTime.name = 'PT' + this.stopForm.time.num + day;
      }
      this.cancel();
    },
    // 弹窗关闭按钮
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

    // 身份分配按钮
    handleStand() {
      let users = [];
      users.push({
        id: useUserStore().id,
        name: useUserStore().name
      })
      //传给选择审核人的数据包
      this.selectStandData = {
        authorityList: this.form1.authorityList,
        data: this.form1.user,
        dialogVisible: true,
        type: "doOperator",
        users: users
      };
    },

    // 分配选择器变化清空数据
    handleChangeOptions(val) {
      this.form2 = {
        distribution: val,
        onlyUser: [],
        user: '',
        standUser: [],
        standRole: [],
      }
      // 流程发起人
      if (val == '3') {
        this.form2.user = "${starterCode}";
      }
      //切换清空已选入的数据
      for (let i = 0; i < this.bpmnElementPropertyList.length; i++) {
        if (this.bpmnElementPropertyList[i].$type === 'flowable:IdmCandidateGroups' || this.bpmnElementPropertyList[i].$type ===
          'flowable:IdmCandidateUsers' || this.bpmnElementPropertyList[i].$type === 'flowable:IdmAssignee') {
          this.bpmnElementPropertyList.splice(i, 1);
        }
      }
      // 暂存剩余的拓展属性内容
      const extensions = window.bpmnInstances.moddle.create("bpmn:ExtensionElements", {
        values: this.bpmnElementPropertyList
      });
      //更新拓展内容
      window.bpmnInstances.modeling.updateProperties(this.bpmnElement, {
        extensionElements: extensions
      })
      this.resetAttributesList();
    },
    // 节点初始化
    resetTaskForm() {
      const bpmnElementObj = this.bpmnElement?.businessObject;
      if (!bpmnElementObj) {
        return;
      }
      // 不进行监测form的方法
      this.isBpmn = true
      this.resetAttributesList();
      this.clearFormInfo();
      // 优先级
      this.form.priority = bpmnElementObj["priority"] || 0;
      this.form.permissionList = [];
      // 到期时间
      this.form.stopTime.name = bpmnElementObj["dueDate"];
      //是否有拓展属性
      if (bpmnElementObj['extensionElements']) {
        // 是否显式带了 AssigneeType（身份存储 / 固定值 标记）
        let hasAssigneeType = false;
        // 是否带了身份存储专属的扩展属性
        let hasStandExt = false;
        bpmnElementObj['extensionElements'].values.forEach(item => {
          // 身份存储/固定值
          if (item.$type === 'flowable:AssigneeType') {
            this.dataType = item.body;
            hasAssigneeType = true;
          }
          if (item.$type === 'flowable:IdmCandidateUsers' || item.$type === 'flowable:IdmCandidateGroups'
            || item.$type === 'flowable:IdmAssignee') {
            hasStandExt = true;
          }
          // 可编辑回显
          if (item.$type === 'flowable:IsEditData') {
            // 保存的String需要转为boolean
            this.form.isEdit = item.body;
          }
          //节点选择
          if (item.$type === 'flowable:NodeType') {
            this.form.node = item.body;
          }
          //处理人自动跳过
          if (item.$type === 'flowable:IsSkip') {
            this.form.isSkip = item.body;
            this.isSkip = item.body;
          }
          //处理人为空跳过
          if (item.$type == 'flowable:IsNullSkip') {
            this.form.isNullSkip = item.body;
            this.isNullSkip = item.body;
          }
          //审批配置
          if (item.$type === 'flowable:IsExamined') {
            this.form.isExamined = item.body;
            this.isExamined = item.body;
          }
          //强制撤回
          if (item.$type === 'flowable:IsRevoke') {
            this.form.isRevoke = item.body;
            this.isRevoke = item.body;
          }
          //授权功能
          if (item.$type === 'flowable:IdmPermission') {
            this.form.permissionList = JSON.parse(item.body);
            this.permissionList = JSON.parse(item.body);
          }
          //人员筛选
          if (item.$type === 'flowable:IsUserScreen') {
            this.form.isUserScreen = item.body;
            this.isUserScreen = item.body;
          }
          //组织筛选
          if (item.$type === 'flowable:IsOrgScreen') {
            this.form.isOrgScreen = item.body;
            this.isOrgScreen = item.body;
          }
          if (item.$type === 'flowable:IsDeptOrOrg') {
            this.form.isDeptOrOrg = item.body;
            this.isDeptOrOrg = item.body;
          }
          //交替配置
          if (item.$type === 'flowable:IsRoleReplace') {
            this.form.isRoleReplace = item.body;
            this.isRoleReplace = item.body;
          }
        })
        // this.dataType 是跨节点复用的变量，节点没有 AssigneeType 时不能沿用上一个节点的值，
        // 否则回显会走错分支：比如上一个节点是「固定值」，本节点实际带着 idmCandidateUsers，
        // 却按固定值分支去读 candidateUsers 字符串，候选人就读不出来。
        // 这里按实际存在的扩展属性推断。
        if (!hasAssigneeType) {
          this.dataType = hasStandExt ? '1' : '0';
        }
      } else {
        if (1 === 1) {
          this.dataType = '0'
          this.form.isEdit = false;
          // 初始化默认添加一个区别  固定值和身份存储的标签拓展
          const body = this.dataType;
          const newPropertyObject = window.bpmnInstances.moddle.create(`${this.prefix}:AssigneeType`, {body});
          this.updateElementExtensions(newPropertyObject);
          this.resetAttributesList();
        }
        //默认   添加交替
        // if (1 === 1) {
        //   this.form.isRoleReplace = true;
        //   this.isRoleReplace = true
        //   this.handleRoleReplace(true);
        // }
        //默认 添加 按钮权限
        if (1 === 1) {
          this.changePermission(this.permissionList);
        }
      }

      // 标签 固定值/身份存储
      this.editableTabsValue = this.dataType;
      this.form.dataType = this.dataType;
      // 固定值
      if (this.dataType === '0') {
        // 分配人员 输入框
        this.form.fixedForm.user.expression = bpmnElementObj["assignee"] || "";
        this.bpmnElementPropertyList = bpmnElementObj['extensionElements'].values;
        if (this.bpmnElementPropertyList.length > 0) {
          this.bpmnElementPropertyList.forEach(item => {
            // 所选人员
            if (item.$type == 'flowable:IdmAuthority') {
              this.form.fixedForm.authorityList = JSON.parse(item.body);
            }
          })
        }
        //候选人员
        if (bpmnElementObj["candidateUsers"]) {
          bpmnElementObj["candidateUsers"].split(',').forEach(item => {
            this.form.fixedForm.standUser.push({
              name: item
            })
          })
        }
        // 候选角色
        if (bpmnElementObj["candidateGroups"]) {
          bpmnElementObj["candidateGroups"].split(',').forEach(item => {
            this.form.fixedForm.roleUser.push({
              name: item
            })
          })
        }
        this.form1 = this.form.fixedForm;
      }
      // 身份存储
      if (this.dataType === '1') {
        // 拓展属性
        this.bpmnElementPropertyList = bpmnElementObj['extensionElements'].values;
        if (this.bpmnElementPropertyList.length > 0) {
          this.bpmnElementPropertyList.forEach(item => {
            // 多个人员
            // 注意：只有在解析出「非空」数据时才设置 distribution 与表单数据。
            // 节点上可能残留空的 idmAssignee / idmCandidateUsers（历史数据或误写），
            // 如果无脑设置，空的那个会把「分配」方式改掉，导致另一个块被隐藏、看起来数据丢了。
            if (item.$type === 'flowable:IdmCandidateUsers') {
              const users = this.parseIdmBodyOrFallback(item.body)
              if (users.length > 0) {
                this.form.standForm.distribution = '2';
                this.form.standForm.standUser = users
              }
            }
            // 角色
            if (item.$type === 'flowable:IdmCandidateGroups') {
              const roles = this.parseIdmBodyOrFallback(item.body)
              if (roles.length > 0) {
                this.form.standForm.distribution = '2';
                this.form.standForm.standRole = roles
              }
            }
            // 单个人
            if (item.$type === 'flowable:IdmAssignee') {
              const only = this.parseIdmBodyOrFallback(item.body)
              if (only.length > 0) {
                this.form.standForm.distribution = '1';
                this.form.standForm.onlyUser = only
              }
            }
            // 流程发起人
            if (bpmnElementObj['assignee'] === "${starterCode}") {
              this.form.standForm.user = bpmnElementObj['assignee']
              this.form.standForm.distribution = '3';
            }
          })
        }
        this.form2 = this.form.standForm;
      }
      this.getElementLoop(bpmnElementObj);
    },


    //获取多实例xml回显
    getElementLoop(businessObject) {
      // 初始化结束 进行检测form
      this.isBpmn = false;
      if (!businessObject.loopCharacteristics) {
        this.multiLoopType = "Null";
        return;
      }
      //TODO nrOfInstances          一共多少实例
      //TODO nrOfCompletedInstances 已经完成审批数量(包含审批结果为通过和拒绝的)
      //TODO nrOfActiveInstances    还未完成审批数量
      this.isSequential = businessObject.loopCharacteristics.isSequential;
      if (businessObject.loopCharacteristics.completionCondition.body === '${nrOfCompletedInstances >= nrOfInstances}') {
        // SequentialMultiInstance 会签
        this.multiLoopType = "SequentialMultiInstance";
      } else if (businessObject.loopCharacteristics.completionCondition.body === '${nrOfCompletedInstances > 0}') {
        // ParallelMultiInstance 或签
        this.multiLoopType = "ParallelMultiInstance";
      }
    },

    // 多实例
    changeMultiLoopType() {
      // 取消多实例配置
      if (this.multiLoopType === "Null") {
        window.bpmnInstances.modeling.updateProperties(this.bpmnElement, {loopCharacteristics: null, assignee: null});
        return;
      }
      this.multiLoopInstance = markRaw(window.bpmnInstances.moddle.create("bpmn:MultiInstanceLoopCharacteristics", {isSequential: this.isSequential}));
      // 更新多实例配置
      if (this.form.dataType === '1') {
        window.bpmnInstances.modeling.updateProperties(this.bpmnElement, {
          loopCharacteristics: this.multiLoopInstance,
          assignee: '${assignee}'
        });
      } else {
        window.bpmnInstances.modeling.updateProperties(this.bpmnElement, {
          loopCharacteristics: this.multiLoopInstance,
          // assignee: '${assignee}'
        });
      }
      // 完成条件
      let completionCondition = null;
      //TODO nrOfInstances一共多少实例
      //TODO nrOfCompletedInstances 已经完成审批数量(包含审批结果为通过和拒绝的)
      //TODO  nrOfActiveInstances  还未完成审批数量
      // 会签
      if (this.multiLoopType === "SequentialMultiInstance") {
        completionCondition = window.bpmnInstances.moddle.create("bpmn:FormalExpression",
          {body: "${nrOfCompletedInstances >= nrOfInstances}"});
      }
      // 或签
      if (this.multiLoopType === "ParallelMultiInstance") {
        completionCondition = window.bpmnInstances.moddle.create("bpmn:FormalExpression",
          {body: "${nrOfCompletedInstances > 0}"});
      }
      var expression = null;
      if (this.bpmnElement.businessObject.assignee) {
        let a = this.bpmnElement.businessObject.assignee
        let b = a.indexOf("{");
        let c = a.indexOf("}")
        expression = a.substring(b + 1, c);
      }
      // 更新模块属性信息
      window.bpmnInstances.modeling.updateModdleProperties(this.bpmnElement, this.multiLoopInstance, {
        collection: '${multiInstanceHandler.getUserIds(execution)}',
        elementVariable: expression || 'assignee',
        completionCondition
      });
    },
  }
};
</script>

<style scoped lang="scss">
.el-row .el-radio-group {
  margin-bottom: 15px;

  .el-radio {
    line-height: 28px;
  }
}

.el-tag {
  margin-bottom: 10px;

  + .el-tag {
    margin-left: 10px;
  }
}

//只读输入框样式
:deep(.readonly_input) {
  .el-input__inner {
    cursor: pointer;
    border: 1px dashed #91a1b7;
  }
}

// element-ui 的字体图标类已不存在，element-plus 的 el-tag 关闭图标类名为 el-tag__close
:deep(.el-tag .el-tag__close) {
  float: right;
  top: 5px;
  right: 0;
}

//t弹窗样式
:deep(.el-dialog__body) {
  border-top: 1px solid #e9e9e9;
  border-bottom: 1px solid #e9e9e9;
}

.user_input {
  padding-right: 30px;
  cursor: pointer;
  height: auto;
  width: auto;
  margin-top: 2px;
  border: 1px dashed #91a1b7;
  line-height: 26px;
  border-radius: 4px;
  position: relative;
  padding-bottom: 2px;
  text-align: left;
  vertical-align: center;

  .remove_all_standUser {
    display: none;
    position: absolute;
    top: -1px;
    height: 100%;
    color: #C0C4CC;
    text-align: center;
    right: 5px;

    // 对应模板中的 circle-close-icon（原 element-ui 的字体图标写法）
    .circle-close-icon {
      color: #C0C4CC;
      font-size: 20px;
      transform: rotateZ(180deg);
      cursor: pointer;
      height: 50%;
      width: 25px;
      text-align: center;
      line-height: 50%;
      vertical-align: center;
    }
  }
}

//解决固定值身份存储   tab切换focus蓝色边框
:deep(.el-tabs__item:focus.is-active.is-focus:not(:active)) {
  -webkit-box-shadow: none;
  box-shadow: none;
}
</style>
