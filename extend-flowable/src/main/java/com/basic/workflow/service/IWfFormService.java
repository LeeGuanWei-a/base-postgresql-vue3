package com.basic.workflow.service;


import com.baomidou.mybatisplus.spring.service.IService;
import com.basic.workflow.domain.WfForm;
import com.basic.workflow.domain.bo.WfFormBo;
import com.basic.workflow.domain.vo.WfFormVo;

import java.util.List;

/**
 * @description 工作流表单管理的服务接口，提供流程表单的查询、保存与删除等基础操作
 * @author yanbin
 * @date 2023/11/3 10:37
 */
public interface IWfFormService extends IService<WfForm> {
    /**
     * @return WfFormVo 表单详情，不存在时返回 null
     * @description 按表单主键查询流程表单详情
     * @params [formId] 表单主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    WfFormVo queryById(String formId);

    /**
     * @return List<WfFormVo> 表单分页列表
     * @description 按业务条件分页查询流程表单列表
     * @params [bo] 表单查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfFormVo> queryPageList(WfFormBo bo);

    /**
     * @return List<WfFormVo> 匹配条件的表单集合
     * @description 按业务条件查询流程表单列表，不分页
     * @params [bo] 表单查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfFormVo> queryList(WfFormBo bo);


    /**
     * @return String 保存后的表单主键
     * @description 保存流程表单，按主键是否为空区分新增与更新并返回表单主键
     * @params [bo] 表单业务对象，携带表单名称、内容、标识等字段
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    String saveForm(WfFormBo bo);

    /**
     * @return Boolean 删除完成后返回 true
     * @description 按表单主键集合批量删除表单，表单已部署流程时抛出业务异常阻止删除
     * @params [formIds] 待删除的表单主键集合，为空时直接返回 true
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    Boolean delForm(List<String> formIds);

    /**
     * @return boolean 表单标识未被占用时返回 true
     * @description 校验表单标识是否已被其他表单占用，新增场景不排除任何表单
     * @params [code] 表单标识
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    boolean checkFormCodeUnique(String code);

    /**
     * @return boolean 排除自身后标识未被占用时返回 true
     * @description 校验表单标识是否重复，更新场景通过排除自身主键避免误判
     * @params [code, excludeFormId] 表单标识；需要排除的表单主键，新增时传 null
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    boolean checkFormCodeUnique(String code, String excludeFormId);

    /**
     * @return WfFormVo 表单内容，标识为空或表单不存在时抛出业务异常
     * @description 按表单标识查询流程表单内容
     * @params [code] 表单标识
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    WfFormVo getFormByCode(String code);

    /**
     * @return Object 组件字段的 label/value 选项集合
     * @description 按条件筛出指定表单的组件字段，输出 selectOption 所需的 label/value 结构
     * @params [wfFormVo] 表单筛选条件，需指定表单标识与目标组件字段
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    Object getVFormWidgetList(WfFormVo wfFormVo);
}
