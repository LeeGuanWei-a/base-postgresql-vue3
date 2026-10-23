package com.basic.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.basic.workflow.domain.WfTaskForm;

import java.util.List;

/**
 * @description 娴佺▼娴佽浆琛ㄥ崟鐨勬湇鍔℃帴鍙ｏ紝鎸夋祦绋嬪疄渚嬩笌娲诲姩鑺傜偣瀛樺彇浠诲姟鍔炵悊杩囩▼涓彁浜ょ殑琛ㄥ崟鏁版嵁
 * @author yanbin
 * @date 2024/02/28
 */
public interface IWfTaskFormService extends IService<WfTaskForm> {

  /**
   * @return boolean 鍒犻櫎鎴愬姛杩斿洖 true
   * @description 鎸夋祦绋嬪疄渚嬩富閿壒閲忓垹闄よ瀹炰緥涓嬪悇鑺傜偣鐨勬祦绋嬭〃鍗?
   * @params [procInsIds] 娴佺▼瀹炰緥涓婚敭闆嗗悎
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
   */
  boolean delProcessForm(List<String> procInsIds);

  /**
   * @return WfTaskForm 寮€濮嬭妭鐐逛笂鐨勮〃鍗曞唴瀹癸紝涓嶅瓨鍦ㄦ椂杩斿洖 null
   * @description 鎸夋祦绋嬪疄渚嬩笌娲诲姩涓婚敭鏌ヨ寮€濮嬭妭鐐逛笂鐨勬祦绋嬭〃鍗曞唴瀹?
   * @params [procInsId, activityId] 娴佺▼瀹炰緥涓婚敭锛涙椿鍔ㄨ妭鐐逛富閿?
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
   */
  WfTaskForm selectTaskFormByProcInsIdAndActId(String procInsId, String activityId);

  /**
   * @return List<WfTaskForm> 鐢ㄦ埛鑺傜偣涓婂～鍐欑殑琛ㄥ崟鍐呭闆嗗悎
   * @description 鎸夋祦绋嬪疄渚嬩笌娲诲姩涓婚敭鏌ヨ鐢ㄦ埛鑺傜偣涓婄殑娴佺▼琛ㄥ崟鍐呭
   * @params [procInsId, activityId] 娴佺▼瀹炰緥涓婚敭锛涙椿鍔ㄨ妭鐐逛富閿?
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
   */
  List<WfTaskForm> getTaskFormByProcInsIdAndActIdToList(String procInsId, String activityId);

  /**
   * @return List<WfTaskForm> 鐢ㄦ埛鑺傜偣涓婂～鍐欑殑琛ㄥ崟鍐呭闆嗗悎
   * @description 鎸変换鍔′富閿笌娲诲姩涓婚敭鏌ヨ鐢ㄦ埛鑺傜偣涓婂～鍐欑殑琛ㄥ崟鍐呭
   * @params [taskId, activityId] 浠诲姟涓婚敭锛涙椿鍔ㄨ妭鐐逛富閿?
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
   */
  List<WfTaskForm> getTaskFormByTaskIdAndActIdToList(String taskId, String activityId);

  /**
   * @return Boolean 鍒犻櫎鎴愬姛杩斿洖 true
   * @description 鎸変换鍔′富閿垹闄よ浠诲姟濉啓鐨勮〃鍗曞唴瀹?
   * @params [taskId] 浠诲姟涓婚敭
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
   */
  Boolean delTaskFormByTaskId(String taskId);

  /**
   * @return Boolean 淇濆瓨鎴愬姛杩斿洖 true
   * @description 淇濆瓨浠诲姟濉啓鐨勮嚜瀹氫箟琛ㄥ崟鏁版嵁
   * @params [wfTaskForm] 寰呬繚瀛樼殑浠诲姟琛ㄥ崟鏁版嵁
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
   */
  Boolean saveProcessForm(WfTaskForm wfTaskForm);

  /**
   * @description 鐩存帴淇濆瓨鑷畾涔夎〃鍗曟暟鎹紝涓嶅仛棰濆鐨勫叧鑱斾笌鏍￠獙澶勭悊
   * @params [wfTaskForm] 寰呬繚瀛樼殑浠诲姟琛ㄥ崟鏁版嵁
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
   */
  void saveFormData(WfTaskForm wfTaskForm);

  /**
   * @return List<WfTaskForm> 璇ヨ妭鐐逛笂淇濆瓨鐨勮〃鍗曟暟鎹泦鍚?
   * @description 鎸夋祦绋嬪疄渚嬩笌娲诲姩涓婚敭鏌ヨ璇ヨ妭鐐逛笂淇濆瓨鐨勮〃鍗曟暟鎹?
   * @params [procInsId, activityId] 娴佺▼瀹炰緥涓婚敭锛涙椿鍔ㄨ妭鐐逛富閿?
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
   */
  List<WfTaskForm> selectFormJsonDataByProcInsIdAndActIdToList(String procInsId, String activityId);

  /**
   * @return List<WfTaskForm> 璇ヤ换鍔″湪鍚勮妭鐐逛笂淇濆瓨鐨勮〃鍗曟暟鎹泦鍚?
   * @description 鎸変换鍔′富閿笌娲诲姩涓婚敭鏌ヨ璇ヤ换鍔′繚瀛樼殑鍏ㄩ儴琛ㄥ崟鏁版嵁
   * @params [taskId, activityId] 浠诲姟涓婚敭锛涙椿鍔ㄨ妭鐐逛富閿?
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
   */
  List<WfTaskForm> selectFormJsonDataByTaskIdAndActIdToList(String taskId, String activityId);

  /**
   * @return boolean 鍒犻櫎鎴愬姛杩斿洖 true
   * @description 鎸夋祦绋嬪疄渚嬩笌娲诲姩涓婚敭鍒犻櫎璇ヨ妭鐐逛笂鐨勮〃鍗曞唴瀹?
   * @params [procInsId, activityId] 娴佺▼瀹炰緥涓婚敭锛涙椿鍔ㄨ妭鐐逛富閿?
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
   */
  boolean delProcessFormByProcInsIdAndActId(String procInsId, String activityId);

  /**
   * @return List<WfTaskForm> 璇ュ疄渚嬩笅鍚勮妭鐐逛繚瀛樼殑琛ㄥ崟鏁版嵁闆嗗悎
   * @description 鎸夋祦绋嬪疄渚嬩富閿煡璇㈠叾鍏ㄩ儴瀹℃壒琛ㄥ崟鏁版嵁
   * @params [procInsId] 娴佺▼瀹炰緥涓婚敭
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
   */
  List<WfTaskForm> selectFormDataByProcInsIdToList(String procInsId);

  /**
   * @return WfTaskForm 璇ヨ妭鐐圭殑琛ㄥ崟 JSON 鏁版嵁锛屼笉瀛樺湪鏃惰繑鍥?null
   * @description 鎸夋祦绋嬪疄渚嬩笌娲诲姩涓婚敭鏌ヨ鑺傜偣涓婄殑琛ㄥ崟 JSON
   * @params [procInsId, activityId] 娴佺▼瀹炰緥涓婚敭锛涙椿鍔ㄨ妭鐐逛富閿?
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
   */
  WfTaskForm selectFormJsonByProcInsIdAndActId(String procInsId, String activityId);

  /**
   * @return WfTaskForm 鍖归厤鐨勮〃鍗曟暟鎹紝鏈尮閰嶆椂杩斿洖 null
   * @description 鎸夊瓧娈靛悕銆佸瓧娈靛€间笌娲诲姩涓婚敭鍒楄〃鍙嶆煡琛ㄥ崟鏁版嵁锛屼富瑕佺敤浜庡彇鍏朵腑鐨勬祦绋嬪疄渚嬩富閿?
   * @params [field, fieldValue, activityIdList] 琛ㄥ崟瀛楁鍚嶏紱琛ㄥ崟瀛楁鍊硷紱寰呭尮閰嶇殑娲诲姩鑺傜偣涓婚敭鏁扮粍
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
   */
  WfTaskForm selectFormDataByFieldAndFieldValueAndActId(String field, String fieldValue, String[] activityIdList);
}
