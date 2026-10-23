package com.basic.agentscope.capability;

import java.util.List;

/**
 * AgentScope 能力描述 —— 能力注册表的载体。
 *
 * <p>后端每实现一个 AgentScope 能力，就往注册表里放一条描述；
 * 前端通过 {@code GET /agentscope/capabilities} 拿到全量清单后渲染导航，
 * 因此<b>新增能力不需要改前端</b>。</p>
 *
 * @param id            能力唯一标识（前端路由 key），如 {@code tool}
 * @param name          能力中文名，如 {@code 工具调用}
 * @param category      分类 code：{@code core} / {@code external}
 * @param categoryLabel 分类中文名
 * @param summary       一句话说明（前端卡片描述）
 * @param icon          Element Plus 图标名
 * @param requires      外部依赖说明；无外部依赖为 {@code null}
 * @param enabled       当前是否可用（外部依赖未就绪时为 false）
 * @param supportsStream 是否支持 SSE 流式
 * @param chatEndpoint  聊天接口地址；为 {@code null} 表示走通用地址 {@code /agentscope/capability/{id}/chat}
 * @param detailEndpoint 状态/详情接口地址；无则 {@code null}
 * @param params        入参说明（前端表单据此渲染）
 */
public record CapabilityInfo(
        String id,
        String name,
        String category,
        String categoryLabel,
        String summary,
        String icon,
        String requires,
        boolean enabled,
        boolean supportsStream,
        String chatEndpoint,
        String detailEndpoint,
        List<ParamInfo> params) {

    /**
     * 单个入参说明。
     *
     * @param name     参数名
     * @param label    中文标签
     * @param type     类型：text / textarea / number / select / boolean
     * @param required 是否必填
     * @param defaultValue 默认值
     * @param options  select 类型的候选项
     */
    public record ParamInfo(
            String name,
            String label,
            String type,
            boolean required,
            String defaultValue,
            List<String> options) {

        public static ParamInfo text(String name, String label, String defaultValue) {
            return new ParamInfo(name, label, "text", false, defaultValue, null);
        }

        public static ParamInfo textarea(String name, String label, String defaultValue) {
            return new ParamInfo(name, label, "textarea", false, defaultValue, null);
        }

        public static ParamInfo required(String name, String label, String defaultValue) {
            return new ParamInfo(name, label, "text", true, defaultValue, null);
        }

        public static ParamInfo select(String name, String label, String defaultValue, List<String> options) {
            return new ParamInfo(name, label, "select", false, defaultValue, options);
        }
    }
}
