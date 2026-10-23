package com.basic.agentscope.capability.rag;

import com.basic.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * RAG 能力专属控制器 —— 路径 {@code /agentscope/rag}。
 *
 * <p>补充两个知识库管理接口：动态灌入知识、查看知识清单。</p>
 */
@RestController
@RequestMapping("/agentscope/rag")
public class RagCapabilityController {

    @Autowired
    private KnowledgeService knowledgeService;

    /**
     * 新增一条知识。
     */
    @PostMapping("/ingest")
    public AjaxResult ingest(@RequestBody(required = false) RagKnowledgeRequest request) {
        if (request == null || request.getContent() == null || request.getContent().isBlank()) {
            return AjaxResult.error("content 不能为空");
        }
        String id = knowledgeService.addKnowledge(request.getTitle(), request.getContent(), request.getKeywords());
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", id);
        data.put("knowledgeCount", knowledgeService.count());
        return AjaxResult.success("知识已写入知识库", data);
    }

    /**
     * 列出全部知识。
     */
    @GetMapping("/list")
    public AjaxResult list() {
        List<Map<String, Object>> items = knowledgeService.listKnowledge();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("knowledgeCount", items.size());
        data.put("knowledge", items);
        return AjaxResult.success(data);
    }

    /**
     * 清空知识库。
     */
    @DeleteMapping("/clear")
    public AjaxResult clear() {
        knowledgeService.clear();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("knowledgeCount", knowledgeService.count());
        return AjaxResult.success("知识库已清空", data);
    }
}
