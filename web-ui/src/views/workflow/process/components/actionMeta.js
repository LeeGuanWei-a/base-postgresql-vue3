/**
 * 审批动作元数据 —— 详情页与待办列表共用，避免按钮文案/说明两处维护。
 *
 * 后端在两处返回节点的「授权功能」：
 *   详情页：WfDetailVo.currentTask.permissionJson + sequentialMultiInstance
 *   待办列表：WfTaskVo.permissionJson + sequentialMultiInstance（每行都带）
 * 两者都是：从引擎缓存的 BpmnModel 读节点扩展，不产生额外库查询。
 *
 * 注意：这里**没有 copy（抄送）** —— 后端没有独立的抄送接口，
 * 抄送是随「同意/驳回」等动作一起触发的（WfTaskBo.copyUserIds），
 * 所以授权功能里的 copy 表示"该节点允许审批时抄送"，不是一个独立按钮。
 */
export const ACTION_META = {
  complete: {
    label: '同意',
    type: 'primary',
    permis: ['workflow:process:complete'],
    tip: '通过并流转到下一节点'
  },
  reject: {
    label: '驳回',
    type: 'danger',
    permis: ['workflow:process:reject'],
    tip: '直接结束流程（状态标记为「已驳回」），发起人需重新发起'
  },
  return: {
    label: '退回',
    type: 'warning',
    permis: ['workflow:process:return'],
    tip: '退回到指定节点，流程继续流转'
  },
  transfer: {
    label: '转办',
    type: 'primary',
    permis: ['workflow:process:transfer'],
    tip: '把任务转给他人处理，处理完不再回到自己'
  },
  delegate: {
    label: '委派',
    type: 'info',
    permis: ['workflow:process:delegate'],
    tip: '委派他人处理，处理完回到自己确认'
  }
}

/**
 * 组装某个节点可用的操作按钮。
 *
 * @param permissionJson         节点「授权功能」的原始 JSON 数组文本
 * @param sequentialMultiInstance 节点是否顺序会签
 * @returns {Array<{action:string,label:string,type:string,permis:string[],tip:string}>}
 */
export function buildActionButtons(permissionJson, sequentialMultiInstance) {
  const list = []
  let keys = []
  try {
    keys = JSON.parse(permissionJson || '[]')
  } catch (e) {
    keys = []
  }
  if (!Array.isArray(keys)) {
    keys = []
  }
  keys.forEach((k) => {
    const meta = ACTION_META[k]
    if (meta) {
      list.push({ action: k, ...meta })
    }
  })
  // 「会签回退」需同时满足两个条件：
  //   ① 节点是顺序会签（否则后端 mulitiSequentialUnComplete 会拒绝）
  //   ② 节点「授权功能」里勾选了它（勾了才表示业务上允许回退）
  if (sequentialMultiInstance && keys.indexOf('mulitiSequentialUnComplete') >= 0) {
    list.push({
      action: 'mulitiSequentialUnComplete',
      label: '会签回退',
      type: 'warning',
      permis: ['workflow:process:mulitiSequentialUnComplete'],
      tip: '顺序会签专用：回退到本节点上一位已处理的审核人，由他重新处理'
    })
  }
  return list
}
