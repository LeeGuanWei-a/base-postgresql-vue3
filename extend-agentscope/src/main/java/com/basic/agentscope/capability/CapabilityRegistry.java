package com.basic.agentscope.capability;

import com.basic.common.core.domain.AjaxResult;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 能力注册表 —— 收集所有 {@link CapabilityProvider} 提供的 AgentScope 能力。
 *
 * <p>新增一个能力只需：</p>
 * <ol>
 *   <li>写一个实现 {@link CapabilityProvider} 的 {@code @Configuration}</li>
 *   <li>在其中声明该能力的 {@code ReActAgent} Bean</li>
 *   <li>前端即可自动发现（前端导航由 {@code GET /agentscope/capabilities} 驱动）</li>
 * </ol>
 */
@Component
public class CapabilityRegistry implements InitializingBean {

    @Autowired
    private List<CapabilityProvider> providers;

    private final Map<String, CapabilityInfo> byId = new LinkedHashMap<>();

    private final Map<String, CapabilityProvider> providerById = new LinkedHashMap<>();

    @Override
    public void afterPropertiesSet() {
        for (CapabilityProvider provider : providers) {
            CapabilityInfo info = provider.descriptor();
            if (info == null || info.id() == null) {
                continue;
            }
            byId.put(info.id(), info);
            providerById.put(info.id(), provider);
        }
        List<CapabilityInfo> sorted = new ArrayList<>(byId.values());
        sorted.sort(Comparator.comparing(CapabilityInfo::category).thenComparing(CapabilityInfo::id));
        Map<String, CapabilityInfo> ordered = new LinkedHashMap<>();
        for (CapabilityInfo info : sorted) {
            ordered.put(info.id(), info);
        }
        byId.clear();
        byId.putAll(ordered);
    }

    /**
     * 全量能力清单（前端导航用）。
     */
    public List<CapabilityInfo> list() {
        return new ArrayList<>(byId.values());
    }

    /**
     * 按 id 取单个能力描述。
     */
    public CapabilityInfo get(String id) {
        return byId.get(id);
    }

    /**
     * 按 id 取能力提供者。
     */
    public CapabilityProvider getProvider(String id) {
        return providerById.get(id);
    }

    public boolean contains(String id) {
        return byId.containsKey(id);
    }

    /**
     * 供 {@code CapabilityController} 校验能力 id 是否存在。
     */
    public AjaxResult notFound(String id) {
        return AjaxResult.error("未知能力：" + id);
    }
}
