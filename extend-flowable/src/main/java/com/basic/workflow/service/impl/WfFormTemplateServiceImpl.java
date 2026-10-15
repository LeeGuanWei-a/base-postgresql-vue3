package com.basic.workflow.service.impl;

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
 * @author liangjiayong
 * @description TODO (流程表单模板Service业务层处理)
 * @date 2024-3-13 15:48
 */
@RequiredArgsConstructor
@Service
public class WfFormTemplateServiceImpl extends ServiceImpl<WfFormTemplateMapper, WfFormTemplate>
        implements IWfFormTemplateService {

    private final WfFormTemplateMapper baseMapper;

    /***
     * @description TODO (查询流程表单模板列表)
     * @params [formTemplate]
     * @return java.util.List<com.basic.workflow.domain.vo.WfFormTemplateVo>
     * @author LiangJiaYong
     * @date 2024-3-13 15:54
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfFormTemplateVo> queryPageList(WfFormTemplate formTemplate){
        return baseMapper.selectFormTemplateVoList(formTemplate);
    }

    /***
     * @description TODO (查询流程表单模板)
     * @params [formId]
     * @return com.basic.workflow.domain.vo.WfFormTemplateVo
     * @author LiangJiaYong
     * @date 2024-3-13 16:00
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public WfFormTemplateVo queryById(Long formId) {
        return baseMapper.selectVoById(formId);
    }

    /***
     * @description TODO (保存流程表单模板)
     * @params [formTemplate]
     * @return void
     * @author LiangJiaYong
     * @date 2024-3-13 16:06
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public WfFormTemplate saveFormTemplate(WfFormTemplate formTemplate){
        this.saveOrUpdate(formTemplate);
        return formTemplate;
    }

    /***
     * @description TODO (流程表单/删除表单模板)
     * @params [formIds]
     * @return java.lang.Boolean
     * @author LiangJiaYong
     * @date 2024-3-13 16:09
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public Boolean delFormTemplate(List<String> formIds) {
        if (CollectionUtils.isNotEmpty(formIds)) {
            for (String formId : formIds) {
                WfFormTemplate template = baseMapper.selectById(formId);
                template.setDelFlag("2");
                this.saveOrUpdate(template);
            }
        }
        return true;
    }
}
