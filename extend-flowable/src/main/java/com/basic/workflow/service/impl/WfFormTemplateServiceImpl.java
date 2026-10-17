package com.basic.workflow.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.basic.workflow.domain.WfFormTemplate;
import com.basic.workflow.domain.vo.WfFormTemplateVo;
import com.basic.workflow.mapper.WfFormTemplateMapper;
import com.basic.workflow.service.IWfFormTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description 流程表单模板的业务实现，提供模板列表与详情查询、保存及删除处理
 * @author liangjiayong
 * @date 2024-3-13 15:48
 */
@RequiredArgsConstructor
@Service
public class WfFormTemplateServiceImpl extends ServiceImpl<WfFormTemplateMapper, WfFormTemplate>
        implements IWfFormTemplateService {

    private final WfFormTemplateMapper baseMapper;

    /**
     * @return List<WfFormTemplateVo> 匹配条件的表单模板集合
     * @description 按条件查询流程表单模板列表
     * @params [formTemplate] 表单模板查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfFormTemplateVo> queryPageList(WfFormTemplate formTemplate){
        return baseMapper.selectFormTemplateVoList(formTemplate);
    }

    /**
     * @return WfFormTemplateVo 表单模板详情，不存在时返回 null
     * @description 按主键查询流程表单模板详情
     * @params [formId] 表单模板主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public WfFormTemplateVo queryById(String formId) {
        return baseMapper.selectVoById(formId);
    }

    /**
     * @return WfFormTemplate 保存后的表单模板对象
     * @description 新增或更新流程表单模板，主键为空时插入、否则更新
     * @params [formTemplate] 待保存的表单模板
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public WfFormTemplate saveFormTemplate(WfFormTemplate formTemplate){
        this.saveOrUpdate(formTemplate);
        return formTemplate;
    }

    /**
     * @return Boolean 执行完成即返回 true
     * @description 逐个把表单模板的删除标记置为已删除，完成逻辑删除
     * @params [formIds] 要删除的表单模板主键集合
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
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
