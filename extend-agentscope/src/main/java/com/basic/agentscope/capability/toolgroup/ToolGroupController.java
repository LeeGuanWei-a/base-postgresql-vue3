package com.basic.agentscope.capability.toolgroup;

import com.basic.common.core.domain.AjaxResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * 工具分组开关控制器 —— 让"按组启用/停用"这件事可以被真正触发。
 *
 * <p>停用某组后，该组工具不会再出现在给模型的 JSON Schema 里
 * （{@code Toolkit#getToolSchemas()} 只返回当前生效的工具）。</p>
 */
@RestController
@RequestMapping("/agentscope/tool-group")
public class ToolGroupController {

    private final ToolGroupCapability capability;

    public ToolGroupController(ToolGroupCapability capability) {
        this.capability = capability;
    }

    /**
     * 启用 / 停用某个工具组。
     *
     * @param group   工具组名（platform / system）
     * @param enabled true=启用，false=停用
     * @return 切换结果 + 当前生效的工具名集合
     */
    @PostMapping("/toggle")
    public AjaxResult toggle(@RequestParam("group") String group,
                             @RequestParam("enabled") boolean enabled) {
        Set<String> tools = enabled ? capability.enableGroup(group) : capability.disableGroup(group);
        AjaxResult result = AjaxResult.success();
        result.put("group", group);
        result.put("enabled", enabled);
        result.put("enabledTools", tools);
        return result;
    }
}
