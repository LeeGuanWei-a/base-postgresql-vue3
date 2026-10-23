package com.basic.workflow.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.basic.workflow.domain.WfFormTemplate;
import com.basic.workflow.domain.vo.WfFormTemplateVo;

import java.util.List;

/**
 * @description 娴佺▼琛ㄥ崟妯℃澘鏈嶅姟鎺ュ彛锛屾彁渚涜〃鍗曟ā鏉跨殑澧炲垹鏀规煡涓庢壒閲忕淮鎶?
 * @author liangjiayong
 * @date 2024-3-13 15:48
 */
public interface IWfFormTemplateService extends IService<WfFormTemplate> {
    /**
     * @return WfFormTemplateVo 琛ㄥ崟妯℃澘瑙嗗浘瀵硅薄锛屾ā鏉夸笉瀛樺湪鏃惰繑鍥?null
     * @description 鎸変富閿煡璇㈡祦绋嬭〃鍗曟ā鏉垮苟杞崲鎴愯鍥惧璞?
     * @params [formId] 琛ㄥ崟妯℃澘涓婚敭
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    WfFormTemplateVo queryById(String formId);

    /**
     * @return List<WfFormTemplateVo> 琛ㄥ崟妯℃澘瑙嗗浘瀵硅薄鍒楄〃
     * @description 鎸夋煡璇㈡潯浠舵煡璇㈡祦绋嬭〃鍗曟ā鏉垮垪琛?
     * @params [formTemplate] 琛ㄥ崟妯℃澘鏌ヨ鏉′欢
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    List<WfFormTemplateVo> queryPageList(WfFormTemplate formTemplate);

    /**
     * @return WfFormTemplate 淇濆瓨鍚庣殑琛ㄥ崟妯℃澘瀵硅薄
     * @description 淇濆瓨娴佺▼琛ㄥ崟妯℃澘骞惰繑鍥炰繚瀛樺悗鐨勬ā鏉垮璞?
     * @params [formTemplate] 寰呬繚瀛樼殑琛ㄥ崟妯℃澘
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    public WfFormTemplate saveFormTemplate(WfFormTemplate formTemplate);

    /**
     * @return Boolean 鏄惁鍒犻櫎鎴愬姛
     * @description 鎸変富閿泦鍚堟壒閲忓垹闄ゆ祦绋嬭〃鍗曟ā鏉?
     * @params [formIds] 闇€瑕佸垹闄ょ殑琛ㄥ崟妯℃澘涓婚敭闆嗗悎
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    public Boolean delFormTemplate(List<String> formIds);
}
