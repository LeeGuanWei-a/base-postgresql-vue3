package com.basic.workflow.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
 * @description 娴佺▼琛ㄥ崟涓氬姟瀹炵幇绫伙紝璐熻矗琛ㄥ崟鐨勬煡璇€佷繚瀛樸€佽〃鍗曞瓧娈佃В鏋愪笌鍒犻櫎澶勭悊
 * @author yanbin
 * @date 2023/11/3 10:39
 */
@RequiredArgsConstructor
@Service
public class WfFormServiceImpl extends ServiceImpl<WfFormMapper, WfForm> implements IWfFormService {

    private final WfFormMapper baseMapper;
    private final IWfDeployFormService deployFormService;

    /**
     * @return WfFormVo 琛ㄥ崟瑙嗗浘瀵硅薄锛屼富閿负绌烘垨鏌ヤ笉鍒版椂杩斿洖 null
     * @description 鎸夎〃鍗曚富閿煡璇㈡祦绋嬭〃鍗曡鎯?
     * @params [formId] 琛ㄥ崟涓婚敭
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    @Override
    public WfFormVo queryById(String formId) {
        if (StringUtils.isNotBlank(formId)) {
            return baseMapper.selectVoById(formId);
        }
        return null;
    }

    /**
     * @return List<WfFormVo> 琛ㄥ崟瑙嗗浘鍒楄〃锛岃姹傚甫鍒嗛〉鍙傛暟鏃舵寜鍒嗛〉杩斿洖
     * @description 鎸変笟鍔℃潯浠舵煡璇㈡祦绋嬭〃鍗曞垪琛紝璇锋眰涓惈鍒嗛〉鍙傛暟鏃跺厛寮€鍚垎椤?
     * @params [bo] 琛ㄥ崟涓氬姟鏌ヨ鏉′欢
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
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
     * @return List<WfFormVo> 鎸夋潯浠跺懡涓殑琛ㄥ崟瑙嗗浘鍒楄〃
     * @description 鎸変笟鍔℃潯浠舵瀯寤烘煡璇㈡潯浠跺悗鏌ヨ娴佺▼琛ㄥ崟鍒楄〃锛屼笉鍒嗛〉
     * @params [bo] 琛ㄥ崟涓氬姟鏌ヨ鏉′欢
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    @Override
    public List<WfFormVo> queryList(WfFormBo bo) {
        LambdaQueryWrapper<WfForm> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * @return String 淇濆瓨鍚庣殑琛ㄥ崟涓婚敭锛屾柊澧炴椂鐢变富閿敓鎴愮瓥鐣ヨˉ榻?
     * @description 淇濆瓨娴佺▼琛ㄥ崟锛屾寜涓婚敭鏄惁涓虹┖鍖哄垎鏂板涓庢洿鏂?
     * @params [bo] 琛ㄥ崟涓氬姟瀵硅薄锛屾惡甯﹁〃鍗曞悕绉般€佸唴瀹广€佹爣璇嗙瓑瀛楁
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
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
            // 鏂板锛氫富閿敱 MyBatis-Plus 鐨?ASSIGN_UUID 鐢熸垚锛坵f_form.form_id 鏄?varchar(32)锛屼笉鏄嚜澧炴暟瀛楋級
            wfForm.setFormId(null);
        }
        this.saveOrUpdate(wfForm);
        return wfForm.getFormId();
    }

    /**
     * @return Boolean 鍒犻櫎瀹屾垚鍚庤繑鍥?true
     * @description 鎸夎〃鍗曚富閿泦鍚堟壒閲忓垹闄よ〃鍗曪紝琛ㄥ崟宸查儴缃叉祦绋嬫椂鎶涘嚭涓氬姟寮傚父闃绘鍒犻櫎
     * @params [formIds] 寰呭垹闄ょ殑琛ㄥ崟涓婚敭闆嗗悎锛屼负绌烘椂鐩存帴杩斿洖 true
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delForm(List<String> formIds) {
        if (CollUtil.isEmpty(formIds)) {
            return true;
        }
        for (String formId : formIds) {
            if (Boolean.TRUE.equals(deployFormService.selectExistDeployForFormId(formId))) {
                throw new ServiceException("閫変腑鐨勮〃鍗曞凡閮ㄧ讲娴佺▼锛岃鍏堣В闄ゅ叧鑱旓紒formId=" + formId);
            }
        }
        baseMapper.deleteByIds(formIds);
        return true;
    }

    /**
     * @return boolean 璇?code 鏄惁宸茶鍒殑琛ㄥ崟鍗犵敤
     * @description 鏍￠獙琛ㄥ崟鏍囪瘑鏄惁宸茶鍏朵粬琛ㄥ崟鍗犵敤锛屾柊澧炲満鏅笉鎺掗櫎浠讳綍琛ㄥ崟
     * @params [code] 琛ㄥ崟鏍囪瘑
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    @Override
    public boolean checkFormCodeUnique(String code) {
        return checkFormCodeUnique(code, null);
    }

    /**
     * @return boolean 璇?code 鏄惁宸茶鍒殑琛ㄥ崟鍗犵敤
     * @description 鏍￠獙琛ㄥ崟鏍囪瘑鏄惁閲嶅锛屾洿鏂板満鏅€氳繃鎺掗櫎鑷韩涓婚敭閬垮厤璇垽
     * @params [code, excludeFormId] 琛ㄥ崟鏍囪瘑锛涢渶瑕佹帓闄ょ殑琛ㄥ崟涓婚敭锛堟柊澧炴椂浼?null锛?
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
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
     * @return WfFormVo 琛ㄥ崟瑙嗗浘瀵硅薄
     * @description 鎸夎〃鍗曟爣璇?code 鏌ュ嚭琛ㄥ崟涓婚敭鍚庤繑鍥炲叾瑙嗗浘璇︽儏锛屾爣璇嗕负绌烘垨琛ㄥ崟涓嶅瓨鍦ㄦ椂鎶涘嚭涓氬姟寮傚父
     * @params [code] 琛ㄥ崟鏍囪瘑
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    @Override
    public WfFormVo getFormByCode(String code) {
        if (StringUtils.isBlank(code)) {
            throw new ServiceException("琛ㄥ崟鏍囪瘑涓嶈兘涓虹┖");
        }
        WfForm wfForm = baseMapper.selectOne(Wrappers.<WfForm>lambdaQuery().eq(WfForm::getCode, code));
        if (wfForm == null) {
            throw new ServiceException("琛ㄥ崟涓嶅瓨鍦紝code=" + code);
        }
        return baseMapper.selectVoById(wfForm.getFormId());
    }

    /**
     * @return LambdaQueryWrapper<WfForm> 鏋勫缓濂界殑琛ㄥ崟鏌ヨ鏉′欢
     * @description 鎸夎〃鍗曞悕绉版ā绯婂尮閰嶄笌琛ㄥ崟鏍囪瘑绮剧‘鍖归厤鏋勫缓鏌ヨ鏉′欢锛屾寜鍒涘缓鏃堕棿鍊掑簭
     * @params [bo] 琛ㄥ崟涓氬姟鏌ヨ鏉′欢
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
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
     * @return Object 閫変腑缁勪欢鐨?label/value 闆嗗悎锛岃В鏋愬紓甯告椂杩斿洖绌洪泦鍚?
     * @description 鎸夋潯浠剁瓫鍑烘寚瀹?code 鐨勮嚜瀹氫箟琛ㄥ崟缁勪欢瀛楁锛岃緭鍑?selectOption 鎵€闇€鐨?label/value 缁撴瀯
     * @params [wfFormVo] 琛ㄥ崟鏌ヨ瀵硅薄锛屾惡甯?code 闆嗗悎涓庨渶鎺掗櫎鐨勫瓧娈?
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
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
            // 鎺掗櫎鐨勫瓧娈?
            List<String> excludeFieldList = new ArrayList<>();
            if (StringUtils.isNotBlank(wfFormVo.getExcludeField())) {
                excludeFieldList = Arrays.asList(wfFormVo.getExcludeField().split(","));
            }
            WfFormVo query = new WfFormVo();
            query.setCodeList(codeList);
            List<WfForm> list = baseMapper.selectFormList(query);
            for (WfForm wfForm : list) {
                Map<String, Object> contentMap = JsonUtils.parseMap(wfForm.getContent());
                // 缁勪欢闆嗗悎
                List<LinkedHashMap> widgetList = (List<LinkedHashMap>) contentMap.get("widgetList");
                getWidgetFieldList(widgetList, excludeFieldList, result);
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
        return result;
    }

    /**
     * @description 閫掑綊閬嶅巻琛ㄥ崟缁勪欢鏍戯紝绛涘嚭闇€鎺掗櫎瀛楁涔嬪鐨勭粍浠跺苟鏀堕泦鍏?label/value
     * @params [widgetList, excludeFieldList, result] 寰呴亶鍘嗙殑缁勪欢闆嗗悎锛涢渶鎺掗櫎鐨勫瓧娈靛悕锛涙敹闆嗙粨鏋滅殑瀹瑰櫒
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
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
            // 缁勪欢 formItemFlag涓簍rue
            if (formItemFlag) {
                LinkedHashMap options = (LinkedHashMap) map.get("options");
                if (!excludeFieldList.contains(options.get("name"))) {
                    WfFormFieldVo wfFormFieldVo = new WfFormFieldVo();
                    String label = options.get("label").toString();
                    // 检查字符串是否为空，并且最后一个字符是否为中文冒号":"
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
            // 鏍呮牸
            else if (type.equals("grid")) {
                List<LinkedHashMap> cols = (List<LinkedHashMap>) map.get("cols");
                for (LinkedHashMap col : cols) {
                    getWidgetFieldList((List<LinkedHashMap>) col.get("widgetList"), excludeFieldList, result);
                }
            }
            // 鍒楄〃
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
            // 瀛愯〃鍗?
            else if (type.equals("sub-form")) {
                getWidgetFieldList((List<LinkedHashMap>) map.get("widgetList"), excludeFieldList, result);
            }
            // 瀹瑰櫒
            else if (category.equals("container")) {
                getWidgetFieldList((List<LinkedHashMap>) map.get("widgetList"), excludeFieldList, result);
            }
        }
    }
}
