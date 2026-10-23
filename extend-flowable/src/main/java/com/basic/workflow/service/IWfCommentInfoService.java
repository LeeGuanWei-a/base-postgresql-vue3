package com.basic.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.basic.workflow.domain.WfCommentInfo;

import java.util.List;

/**
 * @description 娴佺▼娴佽浆璁板綍鏈嶅姟鎺ュ彛锛岃礋璐ｅ鎵规剰瑙佺殑鍐欏叆銆佹煡璇笌鎵归噺娓呯悊
 * @author yanbin
 * @date 2024/01/17
 */
public interface IWfCommentInfoService extends IService<WfCommentInfo> {
    /**
     * @description 鎸変换鍔°€佸鐞嗕汉銆佺被鍨嬩笌鎰忚鍐呭琛ヤ竴鏉℃祦杞褰曪紝鐢ㄤ簬璁板綍瀹℃壒鍔ㄤ綔
     * @params [taskId, userId, type, message] 浠诲姟 ID锛涘鐞嗕汉 ID锛涙剰瑙佺被鍨嬶紱鎰忚鍐呭
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    void addCommentInfo(String taskId, String userId, String type, String message);

    /**
     * @description 鐩存帴鎶婁竴鏉℃剰瑙佸璞¤惤搴擄紝鐢ㄤ簬宸叉湁瀹屾暣鎰忚鏁版嵁鐨勫満鏅?
     * @params [wfCommentInfo] 寰呬繚瀛樼殑瀹℃壒鎰忚瀵硅薄
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    void addCommentInfo(WfCommentInfo wfCommentInfo);

    /**
     * @description 鎸夋祦绋嬪疄渚?ID 闆嗗悎鎵归噺鍒犻櫎杩欎簺瀹炰緥涓嬮仐鐣欑殑瀹℃壒鎰忚
     * @params [procInsIds] 瑕佹竻鐞嗘剰瑙佺殑娴佺▼瀹炰緥 ID 闆嗗悎
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    void delProcessComment(List<String> procInsIds);

    /**
     * @return List<String> 璇ヨ妭鐐逛笂鐨勫鎵规剰瑙佸唴瀹瑰垪琛?
     * @description 鎸夋祦绋嬪疄渚嬩笌娲诲姩鑺傜偣鏌ヨ鑺傜偣鐨勪换鍔″鎵硅褰曞唴瀹?
     * @params [procInsId, activityId] 娴佺▼瀹炰緥 ID锛涙椿鍔ㄨ妭鐐?ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    List<String> selectProcInsIdActivity(String procInsId, String activityId);

    /**
     * @return List<WfCommentInfo> 璇ユ祦绋嬪疄渚嬬殑鎰忚鍒楄〃锛屾棤璁板綍鏃朵负绌哄垪琛?
     * @description 鎸夋祦绋嬪疄渚?ID 鏌ヨ鍏跺巻鍙插鎵规剰瑙?
     * @params [procInsId] 娴佺▼瀹炰緥 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    List<WfCommentInfo> selectProcessCommentForProcInsId(String procInsId);

    /**
     * @description 鎸変换鍔?ID 涓庡鐞嗕汉鏀瑰啓璇ヤ换鍔＄殑鎰忚鍐呭
     * @params [taskId, currentUserId, comment] 浠诲姟 ID锛涘綋鍓嶅鐞嗕汉 ID锛涙柊鐨勬剰瑙佸唴瀹?
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    void updateTaskComment(String taskId, String currentUserId, String comment);

    /**
     * @description 鎸変换鍔?ID 鍒犻櫎璇ヤ换鍔＄殑瀹℃壒鎰忚
     * @params [taskId] 浠诲姟 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    void deleteToCommentForTaskId(String taskId);

    /**
     * @return List<WfCommentInfo> 璇ヤ换鍔＄殑鎰忚鍒楄〃锛屾棤璁板綍鏃朵负绌哄垪琛?
     * @description 鎸変换鍔?ID 鏌ヨ璇ヤ换鍔＄殑瀹℃壒鎰忚
     * @params [taskId] 浠诲姟 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    List<WfCommentInfo> selectTaskIdForComment(String taskId);
}
