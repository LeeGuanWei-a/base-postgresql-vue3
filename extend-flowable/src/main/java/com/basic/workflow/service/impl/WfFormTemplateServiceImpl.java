package com.basic.workflow.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.basic.workflow.domain.WfFormTemplate;
import com.basic.workflow.domain.vo.WfFormTemplateVo;
import com.basic.workflow.mapper.WfFormTemplateMapper;
import com.basic.workflow.service.IWfFormTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description 娴佺▼琛ㄥ崟妯℃澘鐨勪笟鍔″疄鐜帮紝鎻愪緵妯℃澘鍒楄〃涓庤鎯呮煡璇€佷繚瀛樺強鍒犻櫎澶勭悊
 * @author liangjiayong
 * @date 2024-3-13 15:48
 */
@RequiredArgsConstructor
@Service
public class WfFormTemplateServiceImpl extends ServiceImpl<WfFormTemplateMapper, WfFormTemplate>
        implements IWfFormTemplateService {

    private final WfFormTemplateMapper baseMapper;

    /**
     * @return List<WfFormTemplateVo> 鍖归厤鏉′欢鐨勮〃鍗曟ā鏉块泦鍚?
     * @description 鎸夋潯浠舵煡璇㈡祦绋嬭〃鍗曟ā鏉垮垪琛?
     * @params [formTemplate] 琛ㄥ崟妯℃澘鏌ヨ鏉′欢
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    @Override
    public List<WfFormTemplateVo> queryPageList(WfFormTemplate formTemplate){
        return baseMapper.selectFormTemplateVoList(formTemplate);
    }

    /**
     * @return WfFormTemplateVo 琛ㄥ崟妯℃澘璇︽儏锛屼笉瀛樺湪鏃惰繑鍥?null
     * @description 鎸変富閿煡璇㈡祦绋嬭〃鍗曟ā鏉胯鎯?
     * @params [formId] 琛ㄥ崟妯℃澘涓婚敭
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    @Override
    public WfFormTemplateVo queryById(String formId) {
        return baseMapper.selectVoById(formId);
    }

    /**
     * @return WfFormTemplate 淇濆瓨鍚庣殑琛ㄥ崟妯℃澘瀵硅薄
     * @description 鏂板鎴栨洿鏂版祦绋嬭〃鍗曟ā鏉匡紝涓婚敭涓虹┖鏃舵彃鍏ャ€佸惁鍒欐洿鏂?
     * @params [formTemplate] 寰呬繚瀛樼殑琛ㄥ崟妯℃澘
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    @Override
    public WfFormTemplate saveFormTemplate(WfFormTemplate formTemplate){
        this.saveOrUpdate(formTemplate);
        return formTemplate;
    }

    /**
     * @return Boolean 鎵ц瀹屾垚鍗宠繑鍥?true
     * @description 閫愪釜鎶婅〃鍗曟ā鏉跨殑鍒犻櫎鏍囪缃负宸插垹闄わ紝瀹屾垚閫昏緫鍒犻櫎
     * @params [formIds] 瑕佸垹闄ょ殑琛ㄥ崟妯℃澘涓婚敭闆嗗悎
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    @Override
    public Boolean delFormTemplate(List<String> formIds) {
        if (CollUtil.isNotEmpty(formIds)) {
            for (String formId : formIds) {
                WfFormTemplate template = baseMapper.selectById(formId);
                template.setDelFlag("2");
                this.saveOrUpdate(template);
            }
        }
        return true;
    }
}
