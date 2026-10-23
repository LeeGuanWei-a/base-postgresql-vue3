package com.basic.agentscope.capability.model;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 多厂商模型清单 —— 描述"同一套 AgentScope 代码如何切换到不同厂商的 LLM"。
 *
 * <p>AgentScope 的 OpenAI 扩展（{@code agentscope-extensions-model-openai}）实现的是
 * <b>OpenAI 兼容协议</b>：请求统一发往 {@code POST {base-url}/chat/completions}。
 * 因此国内绝大多数厂商（DeepSeek / 通义千问 / Kimi / 智谱）以及本地 Ollama
 * 都能<b>零代码</b>接入，只需要改三个配置项：</p>
 * <pre>
 * agentscope.openai.base-url   # 服务地址
 * agentscope.openai.api-key    # 鉴权 Key（Ollama 等本地服务可留空）
 * agentscope.openai.model-name # 模型名
 * </pre>
 *
 * <p>本类只保存"配置示例"（不含真实密钥），供前端展示与演示切换。</p>
 */
@Component
public class ModelProviderRegistry {

    /**
     * 一个厂商的配置说明。
     *
     * @param id          厂商标识
     * @param name        厂商中文名
     * @param baseUrl     OpenAI 兼容服务地址
     * @param modelName   默认模型名
     * @param apiKeyEnv   存放 API Key 的环境变量名
     * @param baseUrlEnv  存放服务地址的环境变量名
     * @param modelEnv    存放模型名的环境变量名
     * @param configItems 需要改动的配置项（yml）
     * @param note        备注
     */
    public record ProviderSpec(
            String id,
            String name,
            String baseUrl,
            String modelName,
            String apiKeyEnv,
            String baseUrlEnv,
            String modelEnv,
            List<String> configItems,
            String note) {
    }

    /** 需要改动的三个配置项（所有 OpenAI 兼容厂商都一样） */
    private static final List<String> CONFIG_ITEMS = List.of(
            "agentscope.openai.base-url",
            "agentscope.openai.api-key",
            "agentscope.openai.model-name");

    private static final List<ProviderSpec> PROVIDERS = List.of(
            new ProviderSpec("openai", "OpenAI",
                    "https://api.openai.com/v1", "gpt-4o-mini",
                    "OPENAI_API_KEY", "OPENAI_BASE_URL", "OPENAI_MODEL",
                    CONFIG_ITEMS,
                    "官方 OpenAI，OpenAI 兼容协议的基准实现"),
            new ProviderSpec("deepseek", "DeepSeek 深度求索",
                    "https://api.deepseek.com/v1", "deepseek-chat",
                    "DEEPSEEK_API_KEY", "DEEPSEEK_BASE_URL", "DEEPSEEK_MODEL",
                    CONFIG_ITEMS,
                    "OpenAI 兼容协议，base-url 换成 DeepSeek 官方地址即可"),
            new ProviderSpec("qwen", "通义千问 DashScope",
                    "https://dashscope.aliyuncs.com/compatible-mode/v1", "qwen-plus",
                    "DASHSCOPE_API_KEY", "DASHSCOPE_BASE_URL", "DASHSCOPE_MODEL",
                    CONFIG_ITEMS,
                    "阿里云百炼的 OpenAI 兼容模式，模型名形如 qwen-plus / qwen-max"),
            new ProviderSpec("kimi", "Kimi 月之暗面",
                    "https://api.moonshot.cn/v1", "moonshot-v1-8k",
                    "MOONSHOT_API_KEY", "MOONSHOT_BASE_URL", "MOONSHOT_MODEL",
                    CONFIG_ITEMS,
                    "Moonshot 开放平台，OpenAI 兼容协议"),
            new ProviderSpec("zhipu", "智谱 GLM",
                    "https://open.bigmodel.cn/api/paas/v4", "glm-4-flash",
                    "ZHIPUAI_API_KEY", "ZHIPUAI_BASE_URL", "ZHIPUAI_MODEL",
                    CONFIG_ITEMS,
                    "智谱开放平台 v4 接口，OpenAI 兼容协议"),
            new ProviderSpec("ollama", "Ollama 本地模型",
                    "http://localhost:11434/v1", "qwen2.5:7b",
                    "(本地无需 Key)", "OLLAMA_BASE_URL", "OLLAMA_MODEL",
                    CONFIG_ITEMS,
                    "本地推理，api-key 留空即可，适合内网离线部署"));

    /**
     * 全部厂商。
     */
    public List<ProviderSpec> all() {
        return PROVIDERS;
    }

    /**
     * 按标识取厂商；找不到时回落到 openai。
     */
    public ProviderSpec get(String id) {
        if (id != null) {
            for (ProviderSpec spec : PROVIDERS) {
                if (spec.id().equalsIgnoreCase(id.trim())) {
                    return spec;
                }
            }
        }
        return PROVIDERS.get(0);
    }

    /**
     * 厂商标识列表。
     */
    public List<String> ids() {
        return PROVIDERS.stream().map(ProviderSpec::id).toList();
    }

    /**
     * 从环境变量读取该厂商的 API Key（读不到返回 null）。
     */
    public String resolveApiKey(ProviderSpec spec) {
        if (spec == null || spec.apiKeyEnv() == null || spec.apiKeyEnv().startsWith("(")) {
            return null;
        }
        String key = System.getenv(spec.apiKeyEnv());
        return (key == null || key.isBlank()) ? null : key;
    }

    /**
     * 厂商配置示例（供前端展示）。
     */
    public List<Map<String, Object>> toDetailList() {
        List<Map<String, Object>> list = new java.util.ArrayList<>();
        for (ProviderSpec spec : PROVIDERS) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("provider", spec.id());
            item.put("name", spec.name());
            item.put("baseUrl", spec.baseUrl());
            item.put("modelName", spec.modelName());
            item.put("apiKeyEnv", spec.apiKeyEnv());
            item.put("baseUrlEnv", spec.baseUrlEnv());
            item.put("modelEnv", spec.modelEnv());
            item.put("configItems", spec.configItems());
            item.put("note", spec.note());
            item.put("hasApiKey", resolveApiKey(spec) != null);
            list.add(item);
        }
        return list;
    }
}
