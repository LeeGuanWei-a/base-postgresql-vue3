package com.basic.workflow.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.basic.common.exception.ServiceException;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @description 流程表单业务实现类，负责表单的查询、保存、表单字段解析与删除处理
 * @author yanbin
 * @date 2023/11/3 10:39
 */
@RequiredArgsConstructor
@Service
public class WfFormServiceImpl extends ServiceImpl<WfFormMapper, WfForm> implements IWfFormService {

    private final WfFormMapper baseMapper;
    private final IWfDeployFormService deployFormService;

    /**
     * @return WfFormVo 表单视图对象，主键为空或查不到时返回 null
     * @description 按表单主键查询流程表单详情
     * @params [formId] 表单主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public WfFormVo queryById(String formId) {
        if (StringUtils.isNotBlank(formId)) {
            return baseMapper.selectVoById(formId);
        }
        return null;
    }

    /**
     * @return List<WfFormVo> 表单视图列表，请求带分页参数时按分页返回
     * @description 按业务条件查询流程表单列表，请求中含分页参数时先开启分页
     * @params [bo] 表单业务查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @return List<WfFormVo> 按条件命中的表单视图列表
     * @description 按业务条件构建查询条件后查询流程表单列表，不分页
     * @params [bo] 表单业务查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfFormVo> queryList(WfFormBo bo) {
        LambdaQueryWrapper<WfForm> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * @return String 保存后的表单主键，新增时由主键生成策略补齐
     * @description 保存流程表单，按主键是否为空区分新增与更新
     * @params [bo] 表单业务对象，携带表单名称、内容、标识等字段
     * @author liguanwei
     * @date 2026/10/17 20:52
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
        if (StringUtils.isBlank(wfForm.getFormId())) {
            // 新增：主键由 MyBatis-Plus 的 ASSIGN_UUID 生成（wf_form.form_id 是 varchar(32)，不是自增数字）
            wfForm.setFormId(null);
        }
        this.saveOrUpdate(wfForm);
        return wfForm.getFormId();
    }

    /**
     * @return Boolean 删除完成后返回 true
     * @description 按表单主键集合批量删除表单，表单已部署流程时抛出业务异常阻止删除
     * @params [formIds] 待删除的表单主键集合，为空时直接返回 true
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delForm(List<String> formIds) {
        if (CollUtil.isEmpty(formIds)) {
            return true;
        }
        for (String formId : formIds) {
            if (Boolean.TRUE.equals(deployFormService.selectExistDeployForFormId(formId))) {
                throw new ServiceException("选中的表单已部署流程，请先解除关联！formId=" + formId);
            }
        }
        baseMapper.deleteByIds(formIds);
        return true;
    }

    /**
     * @return boolean 该 code 是否已被别的表单占用
     * @description 校验表单标识是否已被其他表单占用，新增场景不排除任何表单
     * @params [code] 表单标识
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public boolean checkFormCodeUnique(String code) {
        return checkFormCodeUnique(code, null);
    }

    /**
     * @return boolean 该 code 是否已被别的表单占用
     * @description 校验表单标识是否重复，更新场景通过排除自身主键避免误判
     * @params [code, excludeFormId] 表单标识；需要排除的表单主键（新增时传 null）
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public boolean checkFormCodeUnique(String code, String excludeFormId) {
        if (StringUtils.isBlank(code)) {
            return false;
        }
        LambdaQueryWrapper<WfForm> lqw = Wrappers.<WfForm>lambdaQuery()
                .eq(WfForm::getCode, code)
                .ne(StringUtils.isNotBlank(excludeFormId), WfForm::getFormId, excludeFormId);
        return baseMapper.selectCount(lqw) > 0;
    }

    /**
     * @return WfFormVo 表单视图对象
     * @description 按表单标识 code 查出表单主键后返回其视图详情，标识为空或表单不存在时抛出业务异常
     * @params [code] 表单标识
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public WfFormVo getFormByCode(String code) {
        if (StringUtils.isBlank(code)) {
            throw new ServiceException("表单标识不能为空");
        }
        WfForm wfForm = baseMapper.selectOne(Wrappers.<WfForm>lambdaQuery().eq(WfForm::getCode, code));
        if (wfForm == null) {
            throw new ServiceException("表单不存在，code=" + code);
        }
        return baseMapper.selectVoById(wfForm.getFormId());
    }

    /**
     * @return LambdaQueryWrapper<WfForm> 构建好的表单查询条件
     * @description 按表单名称模糊匹配与表单标识精确匹配构建查询条件，按创建时间倒序
     * @params [bo] 表单业务查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @return Object 选中组件的 label/value 集合，解析异常时返回空集合
     * @description 按条件筛出指定 code 的自定义表单组件字段，输出 selectOption 所需的 label/value 结构
     * @params [wfFormVo] 表单查询对象，携带 code 集合与需排除的字段
     * @author liguanwei
     * @date 2026/10/17 20:52
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
            // 排除的字段
            List<String> excludeFieldList = new ArrayList<>();
            if (StringUtils.isNotBlank(wfFormVo.getExcludeField())) {
                excludeFieldList = Arrays.asList(wfFormVo.getExcludeField().split(","));
            }
            WfFormVo query = new WfFormVo();
            query.setCodeList(codeList);
            List<WfForm> list = baseMapper.selectFormList(query);
            for (WfForm wfForm : list) {
                Map<String, Object> contentMap = JsonUtils.parseMap(wfForm.getContent());
                // 组件集合
                List<LinkedHashMap> widgetList = (List<LinkedHashMap>) contentMap.get("widgetList");
                getWidgetFieldList(widgetList, excludeFieldList, result);
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
        return result;
    }

    /**
     * @description 递归遍历表单组件树，筛出需排除字段之外的组件并收集其 label/value
     * @params [widgetList, excludeFieldList, result] 待遍历的组件集合；需排除的字段名；收集结果的容器
     * @author liguanwei
     * @date 2026/10/17 20:52
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
            // 组件 formItemFlag为true
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
            // 栅格
            else if (type.equals("grid")) {
                List<LinkedHashMap> cols = (List<LinkedHashMap>) map.get("cols");
                for (LinkedHashMap col : cols) {
                    getWidgetFieldList((List<LinkedHashMap>) col.get("widgetList"), excludeFieldList, result);
                }
            }
            // 列表
            else if (type.equals("table")) {
                List<LinkedHashMap> rows = (List<LinkedHashMap>) map.get("rows");
                for (LinkedHashMap row : rows) {
                    List<LinkedHashMap> cols = (List<LinkedHashMap>) row.get("cols");
                    for (LinkedHashMap col : cols) {
                        getWidgetFieldList((List<LinkedHashMap>) col.get("widgetList"), excludeFieldList, result);
                    }
                }
            }
            // tab
            else if (type.equals("tab")) {
                List<LinkedHashMap> tabs = (List<LinkedHashMap>) map.get("tabs");
                for (LinkedHashMap tab : tabs) {
                    getWidgetFieldList((List<LinkedHashMap>) tab.get("widgetList"), excludeFieldList, result);
                }
            }
            // 子表单
            else if (type.equals("sub-form")) {
                getWidgetFieldList((List<LinkedHashMap>) map.get("widgetList"), excludeFieldList, result);
            }
            // 容器
            else if (category.equals("container")) {
                getWidgetFieldList((List<LinkedHashMap>) map.get("widgetList"), excludeFieldList, result);
            }
        }
    }
}
