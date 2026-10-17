package com.basic.workflow.builder;

import lombok.Getter;
import lombok.Setter;
import org.flowable.bpmn.model.FlowElement;

import java.util.Map;

/**
 * @description 动态用户任务构建器，用于在流程运行期插入临时用户任务节点并设置办理人与参数
 * @author yanbin
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

    /**
     * @description 创建空的任务构建器，各属性留待后续链式赋值
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public DynamicUserTaskBuilder() {
    }

    /**
     * @description 以指定元素 ID 创建任务构建器，用于固定插入节点的元素 ID
     * @params [id] 新建节点的元素 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public DynamicUserTaskBuilder(String id) {
        this.id = id;
    }

    /**
     * @return DynamicUserTaskBuilder 当前构建器自身，支持链式调用
     * @description 设置节点元素 ID 并返回构建器自身
     * @params [id] 节点元素 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public DynamicUserTaskBuilder id(String id) {
        this.id = id;
        return this;
    }

    /**
     * @return DynamicUserTaskBuilder 当前构建器自身，支持链式调用
     * @description 设置节点名称并返回构建器自身
     * @params [name] 节点名称
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public DynamicUserTaskBuilder name(String name) {
        this.name = name;
        return this;
    }

    /**
     * @return DynamicUserTaskBuilder 当前构建器自身，支持链式调用
     * @description 设置节点处理人并返回构建器自身
     * @params [assignee] 处理人标识
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public DynamicUserTaskBuilder assignee(String assignee) {
        this.assignee = assignee;
        return this;
    }

    /**
     * @return String 形如 dynamicSubProcessN 的可用 ID
     * @description 生成未被占用的动态子流程 ID
     * @params [flowElementMap] 当前流程已存在的元素索引
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public String nextSubProcessId(Map<String, FlowElement> flowElementMap) {
        return nextId("dynamicSubProcess", flowElementMap);
    }

    /**
     * @return String 形如 dynamicTaskN 的可用 ID
     * @description 生成未被占用的动态任务节点 ID
     * @params [flowElementMap] 当前流程已存在的元素索引
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public String nextTaskId(Map<String, FlowElement> flowElementMap) {
        return nextId("dynamicTask", flowElementMap);
    }

    /**
     * @return String 形如 dynamicFlowN 的可用 ID
     * @description 生成未被占用的连线 ID
     * @params [flowElementMap] 当前流程已存在的元素索引
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public String nextFlowId(Map<String, FlowElement> flowElementMap) {
        return nextId("dynamicFlow", flowElementMap);
    }

    /**
     * @return String 形如 dynamicForkGatewayN 的可用 ID
     * @description 生成未被占用的分叉网关 ID
     * @params [flowElementMap] 当前流程已存在的元素索引
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public String nextForkGatewayId(Map<String, FlowElement> flowElementMap) {
        return nextId("dynamicForkGateway", flowElementMap);
    }

    /**
     * @return String 形如 dynamicJoinGatewayN 的可用 ID
     * @description 生成未被占用的汇聚网关 ID
     * @params [flowElementMap] 当前流程已存在的元素索引
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public String nextJoinGatewayId(Map<String, FlowElement> flowElementMap) {
        return nextId("dynamicJoinGateway", flowElementMap);
    }

    /**
     * @return String 形如 startEventN 的可用 ID
     * @description 生成未被占用的开始事件 ID
     * @params [flowElementMap] 当前流程已存在的元素索引
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public String nextStartEventId(Map<String, FlowElement> flowElementMap) {
        return nextId("startEvent", flowElementMap);
    }

    /**
     * @return String 形如 endEventN 的可用 ID
     * @description 生成未被占用的结束事件 ID
     * @params [flowElementMap] 当前流程已存在的元素索引
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public String nextEndEventId(Map<String, FlowElement> flowElementMap) {
        return nextId("endEvent", flowElementMap);
    }

    /**
     * @return String 首个未被占用的 ID，计数随查找过程递增因此不会重复
     * @description 以「前缀 + 自增计数」循环判重分配元素 ID，跳过流程中已被占用的编号
     * @params [prefix, flowElementMap] prefix 元素 ID 前缀，如 dynamicTask；flowElementMap 当前流程已存在的元素索引，用于判重
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
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
