package com.basic.workflow.builder;

import lombok.Getter;
import lombok.Setter;
import org.flowable.bpmn.model.FlowElement;

import java.util.Map;

/**
 * @author yanbin
 * @description TODO (新增临时节点运用)
 * @date 2023/10/25
 */
public class DynamicUserTaskBuilder {

    @Setter
    @Getter
    protected String id;
    @Setter
    @Getter
    protected String name;
    @Setter
    @Getter
    protected String assignee;
    @Setter
    @Getter
    protected String dynamicTaskId;
    @Setter
    @Getter
    protected Map<String, Object> argument;

    protected int counter = 1;

    public DynamicUserTaskBuilder() {
    }

    public DynamicUserTaskBuilder(String id) {
        this.id = id;
    }

    public DynamicUserTaskBuilder id(String id) {
        this.id = id;
        return this;
    }

    public DynamicUserTaskBuilder name(String name) {
        this.name = name;
        return this;
    }

    public DynamicUserTaskBuilder assignee(String assignee) {
        this.assignee = assignee;
        return this;
    }

    public String nextSubProcessId(Map<String, FlowElement> flowElementMap) {
        return nextId("dynamicSubProcess", flowElementMap);
    }

    public String nextTaskId(Map<String, FlowElement> flowElementMap) {
        return nextId("dynamicTask", flowElementMap);
    }

    public String nextFlowId(Map<String, FlowElement> flowElementMap) {
        return nextId("dynamicFlow", flowElementMap);
    }

    public String nextForkGatewayId(Map<String, FlowElement> flowElementMap) {
        return nextId("dynamicForkGateway", flowElementMap);
    }

    public String nextJoinGatewayId(Map<String, FlowElement> flowElementMap) {
        return nextId("dynamicJoinGateway", flowElementMap);
    }

    public String nextStartEventId(Map<String, FlowElement> flowElementMap) {
        return nextId("startEvent", flowElementMap);
    }

    public String nextEndEventId(Map<String, FlowElement> flowElementMap) {
        return nextId("endEvent", flowElementMap);
    }

    protected String nextId(String prefix, Map<String, FlowElement> flowElementMap) {
        String nextId = null;
        boolean nextIdNotFound = true;
        while (nextIdNotFound) {
            if (!flowElementMap.containsKey(prefix + this.counter)) {
                nextId = prefix + this.counter;
                nextIdNotFound = false;
            }
            this.counter += 1;
        }
        return nextId;
    }
}
