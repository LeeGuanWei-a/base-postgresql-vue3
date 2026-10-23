package com.basic.workflow.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.basic.common.exception.ServiceException;
import com.basic.common.utils.StringUtils;
import com.basic.workflow.domain.WfTaskForm;
import com.basic.workflow.mapper.WfTaskFormMapper;
import com.basic.workflow.service.IWfTaskFormService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @description 娴佺▼娴佽浆琛ㄥ崟涓氬姟瀹炵幇绫伙紝淇濆瓨骞舵煡璇㈠悇娴佺▼瀹炰緥鍦ㄤ换鍔¤妭鐐规彁浜ょ殑琛ㄥ崟鏁版嵁
 * @author yanbin
 * @date 2024/02/28
 */
@Slf4j
@Service
public class WfTaskFormServiceImpl extends ServiceImpl<WfTaskFormMapper, WfTaskForm> implements
    IWfTaskFormService {

  /**
   * @return boolean 鍒犻櫎缁撴灉锛屾祦绋嬪疄渚?ID 闆嗗悎涓虹┖鏃剁洿鎺ヨ繑鍥?true
   * @description 鎸夋祦绋嬪疄渚?ID 闆嗗悎鎵归噺鍒犻櫎娴佺▼琛ㄥ崟锛屽叆鍙備负绌烘椂涓嶅仛鍒犻櫎鐩存帴杩斿洖
   * @params [procInsIds] 瑕佹竻鐞嗚〃鍗曠殑娴佺▼瀹炰緥 ID 闆嗗悎
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean delProcessForm(List<String> procInsIds) {
    if (procInsIds == null || procInsIds.isEmpty()) {
      return true;
    }
    return this.remove(Wrappers.<WfTaskForm>lambdaQuery()
        .in(WfTaskForm::getProcInsId, procInsIds));
  }

  /**
   * @return WfTaskForm 璇ヨ妭鐐规渶鏂颁竴鏉℃祦绋嬭〃鍗曪紝鏃犺褰曟椂涓?null
   * @description 鎸夋祦绋嬪疄渚嬩笌娲诲姩鑺傜偣鍙栨渶鏂颁竴鏉℃祦绋嬭〃鍗曞唴瀹?
   * @params [procInsId, activityId] 娴佺▼瀹炰緥 ID锛涙椿鍔ㄨ妭鐐?ID
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
   */
  @Override
  public WfTaskForm selectTaskFormByProcInsIdAndActId(String procInsId, String activityId) {
    return this.getOne(Wrappers.<WfTaskForm>lambdaQuery()
        .eq(WfTaskForm::getProcInsId, procInsId)
        .eq(WfTaskForm::getActivityId, activityId)
        .orderByDesc(WfTaskForm::getCreateTime)
        .last("limit 1"), false);
  }

  /**
   * 鏍规嵁娴佺▼瀹炰緥 procInsId 鍜屾椿鍔?activityId 鑾峰彇娴佺▼琛ㄥ崟鍐呭 ---- 鐢ㄦ埛鑺傜偣 List銆?
   */
  @Override
  public List<WfTaskForm> getTaskFormByProcInsIdAndActIdToList(String procInsId,
      String activityId) {
    return baseMapper.selectFormJsonDataByProcInsIdAndActIdToList(procInsId, activityId);
  }

  /**
   * 鏍规嵁娴佺▼浠诲姟 taskId 鍜屾椿鍔?activityId 鑾峰彇娴佺▼琛ㄥ崟鍐呭 ---- 鐢ㄦ埛鑺傜偣 List銆?
   */
  @Override
  public List<WfTaskForm> getTaskFormByTaskIdAndActIdToList(String taskId, String activityId) {
    if (StringUtils.isBlank(taskId)) {
      return null;
    }
    return baseMapper.selectFormJsonDataByTaskIdAndActIdToList(taskId, activityId);
  }

  /**
   * @return Boolean 鍒犻櫎缁撴灉锛屼换鍔?ID 涓虹┖鏃剁洿鎺ヨ繑鍥?true
   * @description 鎸変换鍔?ID 鍒犻櫎璇ヤ换鍔℃彁浜ょ殑琛ㄥ崟鍐呭
   * @params [taskId] 浠诲姟 ID
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public Boolean delTaskFormByTaskId(String taskId) {
    if (StringUtils.isBlank(taskId)) {
      return true;
    }
    return this.remove(Wrappers.<WfTaskForm>lambdaQuery().eq(WfTaskForm::getTaskId, taskId));
  }

  /**
   * @return Boolean 淇濆瓨缁撴灉锛岃〃鍗曞唴瀹逛负绌烘椂璺宠繃淇濆瓨骞惰繑鍥?true
   * @description 淇濆瓨涓€娆¤〃鍗曟彁浜わ紝鍏堟寜浠诲姟鎴栨寜瀹炰緥鍔犺妭鐐规竻鎺夊巻鍙叉彁浜ゅ啀鍐欏叆涓€琛岋紝瀹炵幇鍚屼换鍔℃垨鍚岃妭鐐圭殑瑕嗙洊
   * @params [wfTaskForm] 琛ㄥ崟鎻愪氦鏁版嵁锛屽惈娴佺▼瀹炰緥 ID銆佽妭鐐?ID銆佸彲閫変换鍔?ID 涓庤〃鍗?JSON 鍐呭
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public Boolean saveProcessForm(WfTaskForm wfTaskForm) {
    if (wfTaskForm == null || StringUtils.isBlank(wfTaskForm.getProcInsId())) {
      throw new ServiceException("鍙傛暟鏈夎锛氭祦绋嬪疄渚婭D涓嶈兘涓虹┖");
    }
    if (StringUtils.isBlank(wfTaskForm.getActivityId())) {
      throw new ServiceException("鍙傛暟鏈夎锛氭祦绋嬭妭鐐笽D涓嶈兘涓虹┖");
    }
    if (StringUtils.isBlank(wfTaskForm.getFormContent())) {
      log.debug("鑺傜偣 {} 鏈鏈彁浜よ〃鍗曟暟鎹紝璺宠繃淇濆瓨", wfTaskForm.getActivityId());
      return true;
    }
    if (StringUtils.isNotBlank(wfTaskForm.getTaskId())) {
      delTaskFormByTaskId(wfTaskForm.getTaskId());
    } else {
      delProcessFormByProcInsIdAndActId(wfTaskForm.getProcInsId(), wfTaskForm.getActivityId());
    }
    saveFormData(wfTaskForm);
    return true;
  }

  /**
   * @return boolean 鍒犻櫎缁撴灉
   * @description 鎸夋祦绋嬪疄渚?ID 涓庢椿鍔ㄨ妭鐐?ID 鍒犻櫎璇ヨ妭鐐规彁浜ょ殑琛ㄥ崟鍐呭
   * @params [procInsId, activityId] 娴佺▼瀹炰緥 ID锛涙椿鍔ㄨ妭鐐?ID
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean delProcessFormByProcInsIdAndActId(String procInsId, String activityId) {
    return this.remove(Wrappers.<WfTaskForm>lambdaQuery()
        .eq(WfTaskForm::getProcInsId, procInsId)
        .eq(WfTaskForm::getActivityId, activityId));
  }

  /**
   * @return List<WfTaskForm> 璇ユ祦绋嬪疄渚嬬殑瀹℃壒琛ㄥ崟鏁版嵁鍒楄〃锛屾棤璁板綍鏃朵负绌哄垪琛?
   * @description 鎸夋祦绋嬪疄渚?ID 鏌ヨ娴佺▼瀹℃壒琛ㄥ崟鏁版嵁
   * @params [procInsId] 娴佺▼瀹炰緥 ID
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
   */
  @Override
  public List<WfTaskForm> selectFormDataByProcInsIdToList(String procInsId) {
    return baseMapper.selectFormDataByProcInsIdToList(procInsId);
  }

  /**
   * @description 鎶婃湰娆℃彁浜ょ殑琛ㄥ崟 JSON 鍘熸牱鍐欐垚涓€琛岃褰曪紝鍐呭涓虹┖鏃朵笉钀藉簱
   * @params [wfTaskForm] 琛ㄥ崟鎻愪氦鏁版嵁锛屼粠涓彇琛ㄥ崟 ID銆佸疄渚嬨€佷换鍔°€佽妭鐐广€佸鐞嗕汉涓?JSON 鍐呭
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
   */
  @Override
  public void saveFormData(WfTaskForm wfTaskForm) {
    String content = wfTaskForm.getFormContent();
    if (StringUtils.isBlank(content)) {
      return;
    }
    WfTaskForm entity = new WfTaskForm();
    entity.setFormId(wfTaskForm.getFormId());
    entity.setProcInsId(wfTaskForm.getProcInsId());
    entity.setTaskId(wfTaskForm.getTaskId());
    entity.setActivityId(wfTaskForm.getActivityId());
    entity.setUserId(wfTaskForm.getUserId());
    entity.setFormContent(content);
    this.save(entity);
  }

  /**
   * @return List<WfTaskForm> 璇ヨ妭鐐圭殑琛ㄥ崟 JSON 鏁版嵁鍒楄〃锛屾棤璁板綍鏃朵负绌哄垪琛?
   * @description 鎸夋祦绋嬪疄渚?ID 涓庢椿鍔ㄨ妭鐐?ID 鏌ヨ娴佺▼琛ㄥ崟 JSON 鏁版嵁
   * @params [procInsId, activityId] 娴佺▼瀹炰緥 ID锛涙椿鍔ㄨ妭鐐?ID
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
   */
  @Override
  public List<WfTaskForm> selectFormJsonDataByProcInsIdAndActIdToList(String procInsId,
      String activityId) {
    return baseMapper.selectFormJsonDataByProcInsIdAndActIdToList(procInsId, activityId);
  }

  /**
   * @return List<WfTaskForm> 璇ヤ换鍔″湪鎸囧畾鑺傜偣涓婄殑琛ㄥ崟 JSON 鏁版嵁鍒楄〃锛屾棤璁板綍鏃朵负绌哄垪琛?
   * @description 鎸変换鍔?ID 涓庢椿鍔ㄨ妭鐐?ID 鏌ヨ璇ヤ换鍔″湪鍚勮妭鐐规彁浜よ繃鐨勮〃鍗?JSON 鏁版嵁
   * @params [taskId, activityId] 浠诲姟 ID锛涙椿鍔ㄨ妭鐐?ID
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
   */
  @Override
  public List<WfTaskForm> selectFormJsonDataByTaskIdAndActIdToList(String taskId,
      String activityId) {
    return baseMapper.selectFormJsonDataByTaskIdAndActIdToList(taskId, activityId);
  }

  /**
   * @return WfTaskForm 璇ヨ妭鐐逛笂鐨勮〃鍗?JSON 鏁版嵁锛屾棤璁板綍鏃朵负 null
   * @description 鎸夋祦绋嬪疄渚?ID 涓庢椿鍔ㄨ妭鐐?ID 鍙栧崟鏉¤〃鍗?JSON
   * @params [procInsId, activityId] 娴佺▼瀹炰緥 ID锛涙椿鍔ㄨ妭鐐?ID
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
   */
  @Override
  public WfTaskForm selectFormJsonByProcInsIdAndActId(String procInsId, String activityId) {
    return baseMapper.selectFormJsonByProcInsIdAndActId(procInsId, activityId);
  }

  /**
   * @return WfTaskForm 鍛戒腑鐨勮〃鍗曡褰曪紝涓昏鍙栧叾娴佺▼瀹炰緥 ID锛屾湭鍛戒腑鏃朵负 null
   * @description 鎸夊瓧娈靛悕銆佸瓧娈靛€间笌鑺傜偣 ID 闆嗗悎鍙嶆煡琛ㄥ崟璁板綍锛屽瓧娈靛€间粠琛ㄥ崟 JSON 鍐呭涓彇鍊?
   * @params [field, fieldValue, activityIdList] 瀛楁鍚嶏紱瀛楁鍊硷紱鍙備笌鍖归厤鐨勬椿鍔ㄨ妭鐐?ID 鏁扮粍
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
   */
  @Override
  public WfTaskForm selectFormDataByFieldAndFieldValueAndActId(String field, String fieldValue,
      String[] activityIdList) {
    return baseMapper.selectFormDataByFieldAndFieldValueAndActId(field, fieldValue, activityIdList);
  }
}
