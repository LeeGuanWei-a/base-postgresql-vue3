package com.basic.workflow.customCache;

import org.flowable.common.engine.impl.persistence.deploy.DeploymentCache;
import org.flowable.engine.impl.persistence.deploy.ProcessDefinitionCacheEntry;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @description 自定义流程定义缓存，实现 DeploymentCache 接口，按流程定义ID存放并读取流程定义及其 BPMN 模型条目
 * @author yanbin
 * @date 2023/10/25
 */
@Component
public class CustomDeploymentCache implements DeploymentCache<ProcessDefinitionCacheEntry> {

    protected String id;
    protected ProcessDefinitionCacheEntry entry;

    /**
     * @return ProcessDefinitionCacheEntry 命中时返回缓存条目，未命中返回null
     * @description 按流程定义ID取缓存的流程定义条目
     * @params [id] 流程定义ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public ProcessDefinitionCacheEntry get(String id) {
        if (id.equals(this.id)) {
            return entry;
        }
        return null;
    }

    /**
     * @description 缓存单个流程定义条目，新写入会覆盖原有条目
     * @params [id, object] 流程定义ID；流程定义缓存条目
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void add(String id, ProcessDefinitionCacheEntry object) {
        this.id = id;
        this.entry = object;
    }

    /**
     * @description 按流程定义ID移除缓存，ID不匹配时不做处理
     * @params [id] 流程定义ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void remove(String id) {
        if (id.equals(this.id)) {
            this.id = null;
            this.entry = null;
        }
    }

    /**
     * @description 清空缓存中唯一的流程定义条目
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void clear() {
        this.id = null;
        this.entry = null;
    }

    /**
     * @return boolean 命中返回true，否则返回false
     * @description 判断给定流程定义ID是否已被缓存
     * @params [id] 流程定义ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public boolean contains(String id) {
        return id.equals(this.id);
    }

    /**
     * @return Collection<ProcessDefinitionCacheEntry> 单元素集合，缓存为空时返回空集合
     * @description 返回缓存中的全部流程定义条目
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public Collection<ProcessDefinitionCacheEntry> getAll() {
        if (entry != null) {
            return Collections.singletonList(entry);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * @return int 有缓存返回1，否则返回0
     * @description 返回当前缓存的流程定义条目数量
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public int size() {
        if (entry != null) {
            return 1;
        } else {
            return 0;
        }
    }

    // For testing purposes only
    /**
     * @return ProcessDefinition 流程定义，缓存为空时返回null
     * @description 取出缓存条目所对应的流程定义，仅供测试使用
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public ProcessDefinition getCachedProcessDefinition() {
        if (entry == null) {
            return null;
        }
        return entry.getProcessDefinition();
    }
}
