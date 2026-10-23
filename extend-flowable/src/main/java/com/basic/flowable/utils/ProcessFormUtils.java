package com.basic.flowable.utils;

import cn.hutool.core.convert.Convert;
import com.basic.flowable.core.FormConf;

import java.util.List;
import java.util.Map;

/**
 * @description 流程表单工具类，按字段 vModel 递归遍历表单配置并把表单数据回填到默认值
 * @author yanbin
 * @date 2024/1/8
 */
public class ProcessFormUtils {

    private static final String CONFIG = "__config__";
    private static final String MODEL = "__vModel__";

    /**
     * @description 逐个遍历表单配置中的表单项，把表单数据回填到对应表单项的默认值
     * @params [formConf, data] 表单配置信息；表单内容，key 为字段的 vModel 名称
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static void fillFormData(FormConf formConf, Map<String, Object> data) {
        for (Map<String, Object> field : formConf.getFields()) {
            recursiveFillField(field, data);
        }
    }

    /**
     * @description 递归遍历表单项含子表单，把表单数据按 vModel 回填到表单项的默认值中
     * @params [field, data] 单个表单项配置；表单数据，key 为字段的 vModel 名称
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @SuppressWarnings("unchecked")
    private static void recursiveFillField(final Map<String, Object> field, final Map<String, Object> data) {
        if (!field.containsKey(CONFIG)) {
            return;
        }
        Map<String, Object> configMap = (Map<String, Object>) field.get(CONFIG);
        if (configMap.containsKey("children")) {
            List<Map<String, Object>> childrens = (List<Map<String, Object>>) configMap.get("children");
            for (Map<String, Object> children : childrens) {
                recursiveFillField(children, data);
            }
        }
        String modelKey = Convert.toStr(field.get(MODEL));
        Object value = data.get(modelKey);
        if (value != null) {
            configMap.put("defaultValue", value);
        }
    }
}
