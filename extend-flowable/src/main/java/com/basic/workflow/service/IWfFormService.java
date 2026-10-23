package com.basic.workflow.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.basic.workflow.domain.WfForm;
import com.basic.workflow.domain.bo.WfFormBo;
import com.basic.workflow.domain.vo.WfFormVo;

import java.util.List;

/**
 * @description 宸ヤ綔娴佽〃鍗曠鐞嗙殑鏈嶅姟鎺ュ彛锛屾彁渚涙祦绋嬭〃鍗曠殑鏌ヨ銆佷繚瀛樹笌鍒犻櫎绛夊熀纭€鎿嶄綔
 * @author yanbin
 * @date 2023/11/3 10:37
 */
public interface IWfFormService extends IService<WfForm> {
    /**
     * @return WfFormVo 琛ㄥ崟璇︽儏锛屼笉瀛樺湪鏃惰繑鍥?null
     * @description 鎸夎〃鍗曚富閿煡璇㈡祦绋嬭〃鍗曡鎯?
     * @params [formId] 琛ㄥ崟涓婚敭
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    WfFormVo queryById(String formId);

    /**
     * @return List<WfFormVo> 琛ㄥ崟鍒嗛〉鍒楄〃
     * @description 鎸変笟鍔℃潯浠跺垎椤垫煡璇㈡祦绋嬭〃鍗曞垪琛?
     * @params [bo] 琛ㄥ崟鏌ヨ鏉′欢
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    List<WfFormVo> queryPageList(WfFormBo bo);

    /**
     * @return List<WfFormVo> 鍖归厤鏉′欢鐨勮〃鍗曢泦鍚?
     * @description 鎸変笟鍔℃潯浠舵煡璇㈡祦绋嬭〃鍗曞垪琛紝涓嶅垎椤?
     * @params [bo] 琛ㄥ崟鏌ヨ鏉′欢
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    List<WfFormVo> queryList(WfFormBo bo);


    /**
     * @return String 淇濆瓨鍚庣殑琛ㄥ崟涓婚敭
     * @description 淇濆瓨娴佺▼琛ㄥ崟锛屾寜涓婚敭鏄惁涓虹┖鍖哄垎鏂板涓庢洿鏂板苟杩斿洖琛ㄥ崟涓婚敭
     * @params [bo] 琛ㄥ崟涓氬姟瀵硅薄锛屾惡甯﹁〃鍗曞悕绉般€佸唴瀹广€佹爣璇嗙瓑瀛楁
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    String saveForm(WfFormBo bo);

    /**
     * @return Boolean 鍒犻櫎瀹屾垚鍚庤繑鍥?true
     * @description 鎸夎〃鍗曚富閿泦鍚堟壒閲忓垹闄よ〃鍗曪紝琛ㄥ崟宸查儴缃叉祦绋嬫椂鎶涘嚭涓氬姟寮傚父闃绘鍒犻櫎
     * @params [formIds] 寰呭垹闄ょ殑琛ㄥ崟涓婚敭闆嗗悎锛屼负绌烘椂鐩存帴杩斿洖 true
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    Boolean delForm(List<String> formIds);

    /**
     * @return boolean 琛ㄥ崟鏍囪瘑鏈鍗犵敤鏃惰繑鍥?true
     * @description 鏍￠獙琛ㄥ崟鏍囪瘑鏄惁宸茶鍏朵粬琛ㄥ崟鍗犵敤锛屾柊澧炲満鏅笉鎺掗櫎浠讳綍琛ㄥ崟
     * @params [code] 琛ㄥ崟鏍囪瘑
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    boolean checkFormCodeUnique(String code);

    /**
     * @return boolean 鎺掗櫎鑷韩鍚庢爣璇嗘湭琚崰鐢ㄦ椂杩斿洖 true
     * @description 鏍￠獙琛ㄥ崟鏍囪瘑鏄惁閲嶅锛屾洿鏂板満鏅€氳繃鎺掗櫎鑷韩涓婚敭閬垮厤璇垽
     * @params [code, excludeFormId] 琛ㄥ崟鏍囪瘑锛涢渶瑕佹帓闄ょ殑琛ㄥ崟涓婚敭锛屾柊澧炴椂浼?null
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    boolean checkFormCodeUnique(String code, String excludeFormId);

    /**
     * @return WfFormVo 琛ㄥ崟鍐呭锛屾爣璇嗕负绌烘垨琛ㄥ崟涓嶅瓨鍦ㄦ椂鎶涘嚭涓氬姟寮傚父
     * @description 鎸夎〃鍗曟爣璇嗘煡璇㈡祦绋嬭〃鍗曞唴瀹?
     * @params [code] 琛ㄥ崟鏍囪瘑
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    WfFormVo getFormByCode(String code);

    /**
     * @return Object 缁勪欢瀛楁鐨?label/value 閫夐」闆嗗悎
     * @description 鎸夋潯浠剁瓫鍑烘寚瀹氳〃鍗曠殑缁勪欢瀛楁锛岃緭鍑?selectOption 鎵€闇€鐨?label/value 缁撴瀯
     * @params [wfFormVo] 琛ㄥ崟绛涢€夋潯浠讹紝闇€鎸囧畾琛ㄥ崟鏍囪瘑涓庣洰鏍囩粍浠跺瓧娈?
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    Object getVFormWidgetList(WfFormVo wfFormVo);
}
