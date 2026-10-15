package com.basic.workflow.customCache;

import org.flowable.common.engine.impl.persistence.deploy.DeploymentCache;
import org.flowable.engine.impl.persistence.deploy.ProcessDefinitionCacheEntry;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author yanbin
 * @description TODO (通过定义proceDefId获取bpmn判断)
 * @date 2023/10/25
 */
@Component
public class CustomDeploymentCache implements DeploymentCache<ProcessDefinitionCacheEntry> {

    protected String id;
    protected ProcessDefinitionCacheEntry entry;

    @Override
    public ProcessDefinitionCacheEntry get(String id) {
        if (id.equals(this.id)) {
            return entry;
        }
        return null;
    }

    @Override
    public void add(String id, ProcessDefinitionCacheEntry object) {
        this.id = id;
        this.entry = object;
    }

    @Override
    public void remove(String id) {
        if (id.equals(this.id)) {
            this.id = null;
            this.entry = null;
        }
    }

    @Override
    public void clear() {
        this.id = null;
        this.entry = null;
    }

    @Override
    public boolean contains(String id) {
        return id.equals(this.id);
    }

    @Override
    public Collection<ProcessDefinitionCacheEntry> getAll() {
        if (entry != null) {
            return Collections.singletonList(entry);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public int size() {
        if (entry != null) {
            return 1;
        } else {
            return 0;
        }
    }

    // For testing purposes only
    public ProcessDefinition getCachedProcessDefinition() {
        if (entry == null) {
            return null;
        }
        return entry.getProcessDefinition();
    }
}
