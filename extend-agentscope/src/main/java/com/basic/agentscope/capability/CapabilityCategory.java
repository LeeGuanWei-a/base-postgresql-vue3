package com.basic.agentscope.capability;

/**
 * 能力分类。
 */
public enum CapabilityCategory {

    /** 核心能力：本地即可跑通，无外部基础设施依赖 */
    CORE("核心能力", "core"),

    /** 外部依赖型：需要 MCP Server / IM 凭证 / Docker / 调度器等，默认关闭，留开关 */
    EXTERNAL("外部依赖型", "external");

    private final String label;
    private final String code;

    CapabilityCategory(String label, String code) {
        this.label = label;
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public String getCode() {
        return code;
    }
}
