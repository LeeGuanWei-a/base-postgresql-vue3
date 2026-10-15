package com.basic.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.basic.common.utils.JsonUtils;
import com.basic.common.utils.PageUtils;
import com.basic.common.utils.ServletUtils;
import com.basic.common.utils.StringUtils;
import com.basic.workflow.domain.WfForm;
import com.basic.workflow.domain.bo.WfFormBo;
import com.basic.workflow.domain.vo.WfFormFieldVo;
import com.basic.workflow.domain.vo.WfFormVo;
import com.basic.workflow.mapper.WfFormMapper;
import com.basic.workflow.service.IWfDeployFormService;
import com.basic.workflow.service.IWfFormService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author yanbin
 * @description TODO (流程表单Service业务层处理)
 * @date 2023/11/3 10:39
 */
@RequiredArgsConstructor
@Service
public class WfFormServiceImpl extends ServiceImpl<WfFormMapper, WfForm> implements IWfFormService {

    private final WfFormMapper baseMapper;
    private final IWfDeployFormService deployFormService;

    /**
     * @return com.basic.workflow.domain.vo.WfFormVo
     * @description TODO (查询流程表单)
     * @params [formId]
     * @author yanbin
     * @date 2024/2/26 13:43
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public WfFormVo queryById(Long formId) {
        if (formId != null) {
            return baseMapper.selectVoById(String.valueOf(formId));
        }
        return null;
    }

    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfFormVo>
     * @description TODO (查询流程表单列表)
     * @params [bo, pageQuery]
     * @author yanbin
     * @date 2024/2/26 11:57
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfFormVo> queryPageList(WfFormBo bo) {
        String pageNum = ServletUtils.getParameter("pageNum");
        if (StringUtils.isNotBlank(pageNum)) {
            PageUtils.startPage();
        }
        List<WfFormVo> list = baseMapper.selectWfFormVoList(bo);
        return list;
    }

    /**
     * @return java.util.List<com.basic.workflow.domain.vo.WfFormVo>
     * @description TODO (查询流程表单列表)
     * @params [bo]
     * @author yanbin
     * @date 2024/2/26 13:48
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfFormVo> queryList(WfFormBo bo) {
        LambdaQueryWrapper<WfForm> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * @return boolean
     * @description TODO (保存流程表单)
     * @params [bo]
     * @author yanbin
     * @date 2024/2/26 11:05
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public String saveForm(WfFormBo bo) {
        WfForm wfForm = new WfForm();
        wfForm.setFormName(bo.getFormName());
        wfForm.setContent(bo.getContent());
        wfForm.setRemark(bo.getRemark());
        wfForm.setFormId(bo.getFormId());
        wfForm.setCode(bo.getCode());
        wfForm.setType(bo.getType());
        wfForm.setCategoryId(bo.getCategoryId());
        this.saveOrUpdate(wfForm);
        return wfForm.getFormId();
    }

    /**
     * @return java.lang.Boolean
     * @description TODO (流程表单/删除表单内容)
     * @params [formIds]
     * @author yanbin
     * @date 2024/2/26 16:20
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public Boolean delForm(List<String> formIds) {
        if (CollectionUtils.isNotEmpty(formIds)) {
            for (String formId : formIds) {
                Boolean frame = deployFormService.selectExistDeployForFormId(formId);
                if (frame) {
                    throw new RuntimeException("选中的表单已部署流程,请先解除关联！");
                } else {
                    baseMapper.deleteById(formId);
                }
            }
        }
        return true;
    }

    /**
     * @return boolean
     * @description TODO (检验表单标识是否存在)
     * @params [code]
     * @author yanbin
     * @date 2024/3/12 15:54
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public boolean checkFormCodeUnique(String code) {
        QueryWrapper<WfForm> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", code);
        WfForm wfForm = baseMapper.selectOne(queryWrapper);
        if (wfForm != null) {
            return true;
        }
        return false;
    }

    /**
     * @return com.basic.workflow.domain.vo.WfFormVo
     * @description TODO (根据code标识获取表单内容)
     * @params [code]
     * @author yanbin
     * @date 2024/3/12 16:00
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public WfFormVo getFormByCode(String code) {
        QueryWrapper<WfForm> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", code);
        WfForm wfForm = baseMapper.selectOne(queryWrapper);
        return baseMapper.selectVoById(wfForm.getFormId());
    }

    /**
     * @return com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.basic.workflow.domain.WfForm>
     * @description TODO (构建form查询bo)
     * @params [bo]
     * @author yanbin
     * @date 2024/2/27 9:38
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private LambdaQueryWrapper<WfForm> buildQueryWrapper(WfFormBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<WfForm> lqw = Wrappers.lambdaQuery();
        lqw.orderByDesc(WfForm::getCreateTime);
        lqw.like(StringUtils.isNotBlank(bo.getFormName()), WfForm::getFormName, bo.getFormName());
        if (StringUtils.isNotBlank(bo.getCode())) {
            lqw.eq(WfForm::getCode, bo.getCode());
        }
        return lqw;
    }

    /**
     * @return java.lang.Object
     * @description TODO (根据条件筛选指定code的自定义表单组件字段，返回需要的selectOption格式(“label”,“value”))
     * @params wfFormVo
     * @author yanbin
     * @date 2024/12/6 8:55
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public Object getVFormWidgetList(WfFormVo wfFormVo) {
        List<WfFormFieldVo> result = new ArrayList<>();
        try {
            String[] codeList = null;
            if (StringUtils.isNotBlank(wfFormVo.getCodes())) {
                if (wfFormVo.getCodes().contains(",")) {
                    codeList = wfFormVo.getCodes().split(",");
                } else {
                    codeList = new String[]{wfFormVo.getCodes()};
                }
            }
            //TODO 排除的字段
            List<String> excludeFieldList = new ArrayList<>();
            if (StringUtils.isNotBlank(wfFormVo.getExcludeField())) {
                excludeFieldList = Arrays.asList(wfFormVo.getExcludeField().split(","));
            }
            WfFormVo query = new WfFormVo();
            query.setCodeList(codeList);
            List<WfForm> list = baseMapper.selectFormList(query);
            for (WfForm wfForm : list) {
                Map<String, Object> contentMap = JsonUtils.parseMap(wfForm.getContent());
                //TODO 组件集合
                List<LinkedHashMap> widgetList = (List<LinkedHashMap>) contentMap.get("widgetList");
                getWidgetFieldList(widgetList, excludeFieldList, result);
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
        return result;
    }

    /**
     * @return void
     * @description TODO (筛选自定义组件)
     * @params widgetList
     * excludeFieldList
     * result
     * @author yanbin
     * @date 2024/12/6 10:08
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private void getWidgetFieldList(List<LinkedHashMap> widgetList, List<String> excludeFieldList, List<WfFormFieldVo> result) {
        for (LinkedHashMap map : widgetList) {
            String type = map.get("type").toString();
            Boolean formItemFlag = false;
            if (map.containsKey("formItemFlag")) {
                formItemFlag = Boolean.valueOf(map.get("formItemFlag").toString());
            }
            String category = "";
            if (map.containsKey("category")) {
                category = map.get("category").toString();
            }
            //TODO 组件 formItemFlag为true
            if (formItemFlag) {
                LinkedHashMap options = (LinkedHashMap) map.get("options");
                if (!excludeFieldList.contains(options.get("name"))) {
                    WfFormFieldVo wfFormFieldVo = new WfFormFieldVo();
                    String label = options.get("label").toString();
                    // 检查字符串是否为空，并且最后一个字符是否为“：”
                    if (!label.isEmpty() && (label.charAt(label.length() - 1) == '：' || label.charAt(label.length() - 1) == ':')) {
                        // 删除最后一个字符
                        label = label.substring(0, label.length() - 1);
                    }
                    wfFormFieldVo.setLabel(label);
                    wfFormFieldVo.setValue(options.get("name").toString());
                    if (!result.contains(wfFormFieldVo)) {
                        result.add(wfFormFieldVo);
                    }
                }
            }
            //TODO 栅格
            else if (type.equals("grid")) {
                List<LinkedHashMap> cols = (List<LinkedHashMap>) map.get("cols");
                for (LinkedHashMap col : cols) {
                    getWidgetFieldList((List<LinkedHashMap>) col.get("widgetList"), excludeFieldList, result);
                }
            }
            //TODO  列表
            else if (type.equals("table")) {
                List<LinkedHashMap> rows = (List<LinkedHashMap>) map.get("rows");
                for (LinkedHashMap row : rows) {
                    List<LinkedHashMap> cols = (List<LinkedHashMap>) row.get("cols");
                    for (LinkedHashMap col : cols) {
                        getWidgetFieldList((List<LinkedHashMap>) col.get("widgetList"), excludeFieldList, result);
                    }
                }
            }
            //TODO tab
            else if (type.equals("tab")) {
                List<LinkedHashMap> tabs = (List<LinkedHashMap>) map.get("tabs");
                for (LinkedHashMap tab : tabs) {
                    getWidgetFieldList((List<LinkedHashMap>) tab.get("widgetList"), excludeFieldList, result);
                }
            }
            //TODO 子表单
            else if (type.equals("sub-form")) {
                getWidgetFieldList((List<LinkedHashMap>) map.get("widgetList"), excludeFieldList, result);
            }
            //TODO 容器
            else if (category.equals("container")) {
                getWidgetFieldList((List<LinkedHashMap>) map.get("widgetList"), excludeFieldList, result);
            }
        }
    }
}
