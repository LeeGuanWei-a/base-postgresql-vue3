package com.basic.flowable.flow;

import cn.hutool.core.util.ObjectUtil;

import com.basic.common.exception.ServiceException;
import com.basic.flowable.utils.ModelUtils;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.*;
import org.flowable.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.flowable.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.flowable.task.api.history.HistoricTaskInstance;

import java.util.*;

/**
 * @description Flowable 流程工具类，解析 BPMN 模型中的节点与连线，并处理网关、多实例等流程元素
 * @author yanbin
 * @date 2023/10/19
 */
@Slf4j
public class FlowableUtils {

    /**
     * @return List<SequenceFlow> 该节点的入口连线集合，节点类型不受支持时返回 null
     * @description 按流程元素的具体类型取出该节点的入口连线
     * @params [source] 起始流程元素
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static List<SequenceFlow> getElementIncomingFlows(FlowElement source) {
        List<SequenceFlow> sequenceFlows = null;
        if (source instanceof FlowNode) {
            sequenceFlows = ((FlowNode) source).getIncomingFlows();
        } else if (source instanceof Gateway) {
            sequenceFlows = ((Gateway) source).getIncomingFlows();
        } else if (source instanceof SubProcess) {
            sequenceFlows = ((SubProcess) source).getIncomingFlows();
        } else if (source instanceof StartEvent) {
            sequenceFlows = ((StartEvent) source).getIncomingFlows();
        } else if (source instanceof EndEvent) {
            sequenceFlows = ((EndEvent) source).getIncomingFlows();
        }
        return sequenceFlows;
    }

    /**
     * @return List<SequenceFlow> 该节点的出口连线集合，节点类型不受支持时返回 null
     * @description 按流程元素的具体类型取出该节点的出口连线
     * @params [source] 起始流程元素
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static List<SequenceFlow> getElementOutgoingFlows(FlowElement source) {
        List<SequenceFlow> sequenceFlows = null;
        if (source instanceof FlowNode) {
            sequenceFlows = ((FlowNode) source).getOutgoingFlows();
        } else if (source instanceof Gateway) {
            sequenceFlows = ((Gateway) source).getOutgoingFlows();
        } else if (source instanceof SubProcess) {
            sequenceFlows = ((SubProcess) source).getOutgoingFlows();
        } else if (source instanceof StartEvent) {
            sequenceFlows = ((StartEvent) source).getOutgoingFlows();
        } else if (source instanceof EndEvent) {
            sequenceFlows = ((EndEvent) source).getOutgoingFlows();
        }
        return sequenceFlows;
    }

    /**
     * @return Collection<FlowElement> 包含各级子流程节点的全部流程元素
     * @description 递归收集全部流程元素，进入子流程时继续深入取子流程内的节点
     * @params [flowElements, allElements] 当前层级的流程元素集合；已累积的流程元素集合，为空时新建
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static Collection<FlowElement> getAllElements(Collection<FlowElement> flowElements, Collection<FlowElement> allElements) {
        allElements = allElements == null ? new ArrayList<>() : allElements;

        for (FlowElement flowElement : flowElements) {
            allElements.add(flowElement);
            if (flowElement instanceof SubProcess) {
                // 继续深入子流程，进一步获取子流程
                allElements = FlowableUtils.getAllElements(((SubProcess) flowElement).getFlowElements(), allElements);
            }
        }
        return allElements;
    }

    /**
     * @return List<UserTask> 沿入口连线向前递归找到的全部上级用户任务节点
     * @description 沿入口连线向前逐级查找上级用户任务节点，遇到子流程时追加子流程内的用户任务，已走过的连线不再重复走
     * @params [source, hasSequenceFlow, userTaskList] 起始节点；已走过的连线ID集合，用于防止重复与死循环；已找到的用户任务节点，为空时新建
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static List<UserTask> iteratorFindParentUserTasks(FlowElement source, Set<String> hasSequenceFlow, List<UserTask> userTaskList) {
        userTaskList = userTaskList == null ? new ArrayList<>() : userTaskList;
        hasSequenceFlow = hasSequenceFlow == null ? new HashSet<>() : hasSequenceFlow;

        // 如果该节点为开始节点，且存在上级子节点，则顺着上级子节点继续迭代
        if (source instanceof StartEvent && source.getSubProcess() != null) {
            userTaskList = iteratorFindParentUserTasks(source.getSubProcess(), hasSequenceFlow, userTaskList);
        }

        // 根据类型，获取入口连线
        List<SequenceFlow> sequenceFlows = getElementIncomingFlows(source);

        if (sequenceFlows != null) {
            // 循环找到目标元素
            for (SequenceFlow sequenceFlow : sequenceFlows) {
                // 如果发现连线重复，说明循环了，跳过这个循环
                if (hasSequenceFlow.contains(sequenceFlow.getId())) {
                    continue;
                }
                // 添加已经走过的连线
                hasSequenceFlow.add(sequenceFlow.getId());
                // 类型为用户节点，则新增父级节点
                if (sequenceFlow.getSourceFlowElement() instanceof UserTask) {
                    userTaskList.add((UserTask) sequenceFlow.getSourceFlowElement());
                    continue;
                }
                // 类型为子流程，则添加子流程开始节点出口处相连的节点
                if (sequenceFlow.getSourceFlowElement() instanceof SubProcess) {
                    // 获取子流程用户任务节点
                    List<UserTask> childUserTaskList = findChildProcessUserTasks((StartEvent) ((SubProcess) sequenceFlow.getSourceFlowElement()).getFlowElements().toArray()[0], null, null);
                    // 如果找到节点，则说明该线路找到节点，不继续向下找，反之继续
                    if (childUserTaskList != null && childUserTaskList.size() > 0) {
                        userTaskList.addAll(childUserTaskList);
                        continue;
                    }
                }
                // 继续迭代
                userTaskList = iteratorFindParentUserTasks(sequenceFlow.getSourceFlowElement(), hasSequenceFlow, userTaskList);
            }
        }
        return userTaskList;
    }

    /**
     * @return List<UserTask> 沿出口连线向后递归找到的、处于运行中的下级用户任务节点
     * @description 沿出口连线向后逐级查找正在运行的子级用户任务节点，遇到子流程时从子流程第一个节点继续找，已走过的连线不再重复走
     * @params [source, runTaskKeyList, hasSequenceFlow, userTaskList] 起始节点；正在运行的任务节点KEY集合，用于校验节点是否在运行中；已走过的连线ID集合，用于防止重复与死循环；需要撤回的用户任务列表，为空时新建
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static List<UserTask> iteratorFindChildUserTasks(FlowElement source, List<String> runTaskKeyList, Set<String> hasSequenceFlow, List<UserTask> userTaskList) {
        hasSequenceFlow = hasSequenceFlow == null ? new HashSet<>() : hasSequenceFlow;
        userTaskList = userTaskList == null ? new ArrayList<>() : userTaskList;

        // 如果该节点为开始节点，且存在上级子节点，则顺着上级子节点继续迭代
        if (source instanceof StartEvent && source.getSubProcess() != null) {
            userTaskList = iteratorFindChildUserTasks(source.getSubProcess(), runTaskKeyList, hasSequenceFlow, userTaskList);
        }

        // 根据类型，获取出口连线
        List<SequenceFlow> sequenceFlows = getElementOutgoingFlows(source);

        if (sequenceFlows != null) {
            // 循环找到目标元素
            for (SequenceFlow sequenceFlow : sequenceFlows) {
                // 如果发现连线重复，说明循环了，跳过这个循环
                if (hasSequenceFlow.contains(sequenceFlow.getId())) {
                    continue;
                }
                // 添加已经走过的连线
                hasSequenceFlow.add(sequenceFlow.getId());
                // 如果为用户任务类型，且任务节点的 Key 正在运行的任务中存在，添加
                if (sequenceFlow.getTargetFlowElement() instanceof UserTask && runTaskKeyList.contains((sequenceFlow.getTargetFlowElement()).getId())) {
                    userTaskList.add((UserTask) sequenceFlow.getTargetFlowElement());
                    continue;
                }
                // 如果节点为子流程节点情况，则从节点中的第一个节点开始获取
                if (sequenceFlow.getTargetFlowElement() instanceof SubProcess) {
                    List<UserTask> childUserTaskList = iteratorFindChildUserTasks((FlowElement) (((SubProcess) sequenceFlow.getTargetFlowElement()).getFlowElements().toArray()[0]), runTaskKeyList, hasSequenceFlow, null);
                    // 如果找到节点，则说明该线路找到节点，不继续向下找，反之继续
                    if (childUserTaskList != null && childUserTaskList.size() > 0) {
                        userTaskList.addAll(childUserTaskList);
                        continue;
                    }
                }
                // 继续迭代
                userTaskList = iteratorFindChildUserTasks(sequenceFlow.getTargetFlowElement(), runTaskKeyList, hasSequenceFlow, userTaskList);
            }
        }
        return userTaskList;
    }

    /**
     * @return List<UserTask> 从子流程内找到的用户任务节点集合
     * @description 沿出口连线递归遍历子流程内部的用户任务节点，遇到嵌套子流程时继续深入
     * @params [source, hasSequenceFlow, userTaskList] 起始节点；已走过的连线ID集合，用于防止重复与死循环；需要撤回的用户任务列表，为空时新建
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static List<UserTask> findChildProcessUserTasks(FlowElement source, Set<String> hasSequenceFlow, List<UserTask> userTaskList) {
        hasSequenceFlow = hasSequenceFlow == null ? new HashSet<>() : hasSequenceFlow;
        userTaskList = userTaskList == null ? new ArrayList<>() : userTaskList;

        // 根据类型，获取出口连线
        List<SequenceFlow> sequenceFlows = getElementOutgoingFlows(source);

        if (sequenceFlows != null) {
            // 循环找到目标元素
            for (SequenceFlow sequenceFlow : sequenceFlows) {
                // 如果发现连线重复，说明循环了，跳过这个循环
                if (hasSequenceFlow.contains(sequenceFlow.getId())) {
                    continue;
                }
                // 添加已经走过的连线
                hasSequenceFlow.add(sequenceFlow.getId());
                // 如果为用户任务类型，且任务节点的 Key 正在运行的任务中存在，添加
                if (sequenceFlow.getTargetFlowElement() instanceof UserTask) {
                    userTaskList.add((UserTask) sequenceFlow.getTargetFlowElement());
                    continue;
                }
                // 如果节点为子流程节点情况，则从节点中的第一个节点开始获取
                if (sequenceFlow.getTargetFlowElement() instanceof SubProcess) {
                    List<UserTask> childUserTaskList = findChildProcessUserTasks((FlowElement) (((SubProcess) sequenceFlow.getTargetFlowElement()).getFlowElements().toArray()[0]), hasSequenceFlow, null);
                    // 如果找到节点，则说明该线路找到节点，不继续向下找，反之继续
                    if (childUserTaskList != null && childUserTaskList.size() > 0) {
                        userTaskList.addAll(childUserTaskList);
                        continue;
                    }
                }
                // 继续迭代
                userTaskList = findChildProcessUserTasks(sequenceFlow.getTargetFlowElement(), hasSequenceFlow, userTaskList);
            }
        }
        return userTaskList;
    }

    /**
     * @return Set<String> 判定为脏数据的节点ID集合
     * @description 从后向前寻路，把经过的回退目标点沿途的节点都标记为脏线路节点，用于清洗回退产生的历史数据
     * @params [source, passRoads, hasSequenceFlow, targets, dirtyRoads] 起始节点；已经过的点集合；已走过的连线ID集合，用于防止重复与死循环；脏线路的目标终点ID集合；确定为脏数据的点集合，为空时新建
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static Set<String> iteratorFindDirtyRoads(FlowElement source, List<String> passRoads, Set<String> hasSequenceFlow, List<String> targets, Set<String> dirtyRoads) {
        passRoads = passRoads == null ? new ArrayList<>() : passRoads;
        dirtyRoads = dirtyRoads == null ? new HashSet<>() : dirtyRoads;
        hasSequenceFlow = hasSequenceFlow == null ? new HashSet<>() : hasSequenceFlow;

        // 如果该节点为开始节点，且存在上级子节点，则顺着上级子节点继续迭代
        if (source instanceof StartEvent && source.getSubProcess() != null) {
            dirtyRoads = iteratorFindDirtyRoads(source.getSubProcess(), passRoads, hasSequenceFlow, targets, dirtyRoads);
        }

        // 根据类型，获取入口连线
        List<SequenceFlow> sequenceFlows = getElementIncomingFlows(source);

        if (sequenceFlows != null) {
            // 循环找到目标元素
            for (SequenceFlow sequenceFlow : sequenceFlows) {
                // 如果发现连线重复，说明循环了，跳过这个循环
                if (hasSequenceFlow.contains(sequenceFlow.getId())) {
                    continue;
                }
                // 添加已经走过的连线
                hasSequenceFlow.add(sequenceFlow.getId());
                // 新增经过的路线
                passRoads.add(sequenceFlow.getSourceFlowElement().getId());
                // 如果此点为目标点，确定经过的路线为脏线路，添加点到脏线路中，然后找下个连线
                if (targets.contains(sequenceFlow.getSourceFlowElement().getId())) {
                    dirtyRoads.addAll(passRoads);
                    continue;
                }
                // 如果该节点为开始节点，且存在上级子节点，则顺着上级子节点继续迭代
                if (sequenceFlow.getSourceFlowElement() instanceof SubProcess) {
                    dirtyRoads = findChildProcessAllDirtyRoad((StartEvent) ((SubProcess) sequenceFlow.getSourceFlowElement()).getFlowElements().toArray()[0], null, dirtyRoads);
                    // 是否存在子流程上，true 是，false 否
                    Boolean isInChildProcess = dirtyTargetInChildProcess((StartEvent) ((SubProcess) sequenceFlow.getSourceFlowElement()).getFlowElements().toArray()[0], null, targets, null);
                    if (isInChildProcess) {
                        // 已在子流程上找到，该路线结束
                        continue;
                    }
                }
                // 继续迭代
                dirtyRoads = iteratorFindDirtyRoads(sequenceFlow.getSourceFlowElement(), passRoads, hasSequenceFlow, targets, dirtyRoads);
            }
        }
        return dirtyRoads;
    }

    /**
     * @return Set<String> 子流程内被标记为脏数据的节点ID集合
     * @description 递归遍历子流程内的所有节点并全部标记为脏路线，因为回退到子流程时子流程内的节点都需重新走一遍
     * @params [source, hasSequenceFlow, dirtyRoads] 起始节点；已走过的连线ID集合，用于防止重复；确定为脏数据的点集合，为空时新建
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static Set<String> findChildProcessAllDirtyRoad(FlowElement source, Set<String> hasSequenceFlow, Set<String> dirtyRoads) {
        hasSequenceFlow = hasSequenceFlow == null ? new HashSet<>() : hasSequenceFlow;
        dirtyRoads = dirtyRoads == null ? new HashSet<>() : dirtyRoads;

        // 根据类型，获取出口连线
        List<SequenceFlow> sequenceFlows = getElementOutgoingFlows(source);

        if (sequenceFlows != null) {
            // 循环找到目标元素
            for (SequenceFlow sequenceFlow : sequenceFlows) {
                // 如果发现连线重复，说明循环了，跳过这个循环
                if (hasSequenceFlow.contains(sequenceFlow.getId())) {
                    continue;
                }
                // 添加已经走过的连线
                hasSequenceFlow.add(sequenceFlow.getId());
                // 添加脏路线
                dirtyRoads.add(sequenceFlow.getTargetFlowElement().getId());
                // 如果节点为子流程节点情况，则从节点中的第一个节点开始获取
                if (sequenceFlow.getTargetFlowElement() instanceof SubProcess) {
                    dirtyRoads = findChildProcessAllDirtyRoad((FlowElement) (((SubProcess) sequenceFlow.getTargetFlowElement()).getFlowElements().toArray()[0]), hasSequenceFlow, dirtyRoads);
                }
                // 继续迭代
                dirtyRoads = findChildProcessAllDirtyRoad(sequenceFlow.getTargetFlowElement(), hasSequenceFlow, dirtyRoads);
            }
        }
        return dirtyRoads;
    }

    /**
     * @return Boolean 脏路线终点是否落在子流程上，true 是，false 否
     * @description 沿出口连线递归判断脏路线的目标节点是否位于子流程内部，只要命中一个即结束判断
     * @params [source, hasSequenceFlow, targets, inChildProcess] 起始节点；已走过的连线ID集合，用于防止重复；需要判断的脏路线节点ID集合；是否已判定在子流程上，为空时按 false 处理
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static Boolean dirtyTargetInChildProcess(FlowElement source, Set<String> hasSequenceFlow, List<String> targets, Boolean inChildProcess) {
        hasSequenceFlow = hasSequenceFlow == null ? new HashSet<>() : hasSequenceFlow;
        inChildProcess = inChildProcess == null ? false : inChildProcess;

        // 根据类型，获取出口连线
        List<SequenceFlow> sequenceFlows = getElementOutgoingFlows(source);

        if (sequenceFlows != null && !inChildProcess) {
            // 循环找到目标元素
            for (SequenceFlow sequenceFlow : sequenceFlows) {
                // 如果发现连线重复，说明循环了，跳过这个循环
                if (hasSequenceFlow.contains(sequenceFlow.getId())) {
                    continue;
                }
                // 添加已经走过的连线
                hasSequenceFlow.add(sequenceFlow.getId());
                // 如果发现目标点在子流程上存在，说明只到子流程为止
                if (targets.contains(sequenceFlow.getTargetFlowElement().getId())) {
                    inChildProcess = true;
                    break;
                }
                // 如果节点为子流程节点情况，则从节点中的第一个节点开始获取
                if (sequenceFlow.getTargetFlowElement() instanceof SubProcess) {
                    inChildProcess = dirtyTargetInChildProcess((FlowElement) (((SubProcess) sequenceFlow.getTargetFlowElement()).getFlowElements().toArray()[0]), hasSequenceFlow, targets, inChildProcess);
                }
                // 继续迭代
                inChildProcess = dirtyTargetInChildProcess(sequenceFlow.getTargetFlowElement(), hasSequenceFlow, targets, inChildProcess);
            }
        }
        return inChildProcess;
    }

    /**
     * @return Boolean 目标节点相对当前节点是否串行，true 串行，false 并行
     * @description 从后向前递归扫描入口连线，判断目标节点相对当前节点是否串行，遇到开始事件或已判定并行即提前结束
     * @params [source, targetKsy, hasSequenceFlow, isSequential] 起始节点；目标节点KEY；已走过的连线ID集合，用于防止重复；是否串行，为空时按 true 处理
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static Boolean iteratorCheckSequentialReferTarget(FlowElement source, String targetKsy, Set<String> hasSequenceFlow, Boolean isSequential) {
        isSequential = isSequential == null ? true : isSequential;
        hasSequenceFlow = hasSequenceFlow == null ? new HashSet<>() : hasSequenceFlow;

        // 如果该节点为开始节点，且存在上级子节点，则顺着上级子节点继续迭代
        if (source instanceof StartEvent && source.getSubProcess() != null) {
            isSequential = iteratorCheckSequentialReferTarget(source.getSubProcess(), targetKsy, hasSequenceFlow, isSequential);
        }

        // 根据类型，获取入口连线
        List<SequenceFlow> sequenceFlows = getElementIncomingFlows(source);

        if (sequenceFlows != null) {
            // 循环找到目标元素
            for (SequenceFlow sequenceFlow : sequenceFlows) {
                // 如果发现连线重复，说明循环了，跳过这个循环
                if (hasSequenceFlow.contains(sequenceFlow.getId())) {
                    continue;
                }
                // 添加已经走过的连线
                hasSequenceFlow.add(sequenceFlow.getId());
                // 如果目标节点已被判断为并行，后面都不需要执行，直接返回
                if (isSequential == false) {
                    break;
                }
                // 这条线路存在目标节点，这条线路完成，进入下个线路
                if (targetKsy.equals(sequenceFlow.getSourceFlowElement().getId())) {
                    continue;
                }
                if (sequenceFlow.getSourceFlowElement() instanceof StartEvent) {
                    isSequential = false;
                    break;
                }
                // 否则就继续迭代
                isSequential = iteratorCheckSequentialReferTarget(sequenceFlow.getSourceFlowElement(), targetKsy, hasSequenceFlow, isSequential);
            }
        }
        return isSequential;
    }

    /**
     * @return List<List<UserTask>> 从当前节点回退到开始节点经过的所有用户任务路线
     * @description 从后向前寻路，收集从当前节点回溯到无入口连线节点的全部路线，每条路线按用户任务排列
     * @params [source, passRoads, hasSequenceFlow, roads] 起始节点；已经过的用户任务点集合；已走过的连线ID集合，用于防止重复；已收集到的路线集合，为空时新建
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static List<List<UserTask>> findRoad(FlowElement source, List<UserTask> passRoads, Set<String> hasSequenceFlow, List<List<UserTask>> roads) {
        passRoads = passRoads == null ? new ArrayList<>() : passRoads;
        roads = roads == null ? new ArrayList<>() : roads;
        hasSequenceFlow = hasSequenceFlow == null ? new HashSet<>() : hasSequenceFlow;

        // 如果该节点为开始节点，且存在上级子节点，则顺着上级子节点继续迭代
        if (source instanceof StartEvent && source.getSubProcess() != null) {
            roads = findRoad(source.getSubProcess(), passRoads, hasSequenceFlow, roads);
        }

        // 根据类型，获取入口连线
        List<SequenceFlow> sequenceFlows = getElementIncomingFlows(source);

        if (sequenceFlows != null && sequenceFlows.size() != 0) {
            for (SequenceFlow sequenceFlow : sequenceFlows) {
                // 如果发现连线重复，说明循环了，跳过这个循环
                if (hasSequenceFlow.contains(sequenceFlow.getId())) {
                    continue;
                }
                // 添加已经走过的连线
                hasSequenceFlow.add(sequenceFlow.getId());
                // 添加经过路线
                if (sequenceFlow.getSourceFlowElement() instanceof UserTask) {
                    passRoads.add((UserTask) sequenceFlow.getSourceFlowElement());
                }
                // 继续迭代
                roads = findRoad(sequenceFlow.getSourceFlowElement(), passRoads, hasSequenceFlow, roads);
            }
        } else {
            // 添加路线
            roads.add(passRoads);
        }
        return roads;
    }

    /**
     * @return List<String> 清洗后的历史任务节点KEY列表，已剔除回退、驳回产生的脏数据
     * @description 用栈按时间倒序回放历史任务实例，结合删除原因识别回退与驳回产生的脏线路并剔除，会签节点按组特殊处理
     * @params [allElements, historicTaskInstanceList] 流程的全部节点信息；历史任务实例列表，需按开始时间升序传入
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static List<String> historicTaskInstanceClean(Collection<FlowElement> allElements, List<HistoricTaskInstance> historicTaskInstanceList) {
        // 会签节点收集
        List<String> multiTask = new ArrayList<>();
        allElements.forEach(flowElement -> {
            if (flowElement instanceof UserTask) {
                // 如果该节点的行为为会签行为，说明该节点为会签节点
                if (((UserTask) flowElement).getBehavior() instanceof ParallelMultiInstanceBehavior || ((UserTask) flowElement).getBehavior() instanceof SequentialMultiInstanceBehavior) {
                    multiTask.add(flowElement.getId());
                }
            }
        });
        // 循环放入栈，栈 LIFO：后进先出
        Stack<HistoricTaskInstance> stack = new Stack<>();
        historicTaskInstanceList.forEach(item -> stack.push(item));
        // 清洗后的历史任务实例
        List<String> lastHistoricTaskInstanceList = new ArrayList<>();
        // 网关存在可能只走了部分分支情况，且还存在跳转废弃数据以及其他分支数据的干扰，因此需要对历史节点数据进行清洗
        // 临时用户任务 key
        StringBuilder userTaskKey = null;
        // 临时被删掉的任务 key，存在并行情况
        List<String> deleteKeyList = new ArrayList<>();
        // 临时脏数据线路
        List<Set<String>> dirtyDataLineList = new ArrayList<>();
        // 由某个点跳到会签点,此时出现多个会签实例对应 1 个跳转情况，需要把这些连续脏数据都找到
        // 会签特殊处理下标
        int multiIndex = -1;
        // 会签特殊处理 key
        StringBuilder multiKey = null;
        // 会签特殊处理操作标识
        boolean multiOpera = false;
        while (!stack.empty()) {
            // 从这里开始 userTaskKey 都还是上个栈的 key
            // 是否是脏数据线路上的点
            final boolean[] isDirtyData = {false};
            for (Set<String> oldDirtyDataLine : dirtyDataLineList) {
                if (oldDirtyDataLine.contains(stack.peek().getTaskDefinitionKey())) {
                    isDirtyData[0] = true;
                }
            }
            // 删除原因不为空，说明从这条数据开始回跳或者回退的
            // MI_END：会签完成后，其他未签到节点的删除原因，不在处理范围内
            if (stack.peek().getDeleteReason() != null && !stack.peek().getDeleteReason().equals("MI_END")) {
                // 可以理解为脏线路起点
                String dirtyPoint = "";
                if (stack.peek().getDeleteReason().indexOf("Change activity to ") >= 0) {
                    dirtyPoint = stack.peek().getDeleteReason().replace("Change activity to ", "");
                }
                // 会签回退删除原因有点不同
                if (stack.peek().getDeleteReason().indexOf("Change parent activity to ") >= 0) {
                    dirtyPoint = stack.peek().getDeleteReason().replace("Change parent activity to ", "");
                }
                FlowElement dirtyTask = null;
                // 获取变更节点的对应的入口处连线
                // 如果是网关并行回退情况，会变成两条脏数据路线，效果一样
                for (FlowElement flowElement : allElements) {
                    if (flowElement.getId().equals(stack.peek().getTaskDefinitionKey())) {
                        dirtyTask = flowElement;
                    }
                }
                // 获取脏数据线路
                Set<String> dirtyDataLine = FlowableUtils.iteratorFindDirtyRoads(dirtyTask, null, null, Arrays.asList(dirtyPoint.split(",")), null);
                // 自己本身也是脏线路上的点，加进去
                dirtyDataLine.add(stack.peek().getTaskDefinitionKey());
                log.info(stack.peek().getTaskDefinitionKey() + "点脏路线集合：" + dirtyDataLine);
                // 是全新的需要添加的脏线路
                boolean isNewDirtyData = true;
                for (int i = 0; i < dirtyDataLineList.size(); i++) {
                    // 如果发现他的上个节点在脏线路内，说明这个点可能是并行的节点，或者连续驳回
                    // 这时，都以之前的脏线路节点为标准，只需合并脏线路即可，也就是路线补全
                    if (dirtyDataLineList.get(i).contains(userTaskKey.toString())) {
                        isNewDirtyData = false;
                        dirtyDataLineList.get(i).addAll(dirtyDataLine);
                    }
                }
                // 已确定时全新的脏线路
                if (isNewDirtyData) {
                    // deleteKey 单一路线驳回到并行，这种同时生成多个新实例记录情况，这时 deleteKey 其实是由多个值组成
                    // 按照逻辑，回退后立刻生成的实例记录就是回退的记录
                    // 至于驳回所生成的 Key，直接从删除原因中获取，因为存在驳回到并行的情况
                    deleteKeyList.add(dirtyPoint + ",");
                    dirtyDataLineList.add(dirtyDataLine);
                }
                // 添加后，现在这个点变成脏线路上的点了
                isDirtyData[0] = true;
            }
            // 如果不是脏线路上的点，说明是有效数据，添加历史实例 Key
            if (!isDirtyData[0]) {
                lastHistoricTaskInstanceList.add(stack.peek().getTaskDefinitionKey());
            }
            // 校验脏线路是否结束
            for (int i = 0; i < deleteKeyList.size(); i++) {
                // 如果发现脏数据属于会签，记录下下标与对应 Key，以备后续比对，会签脏数据范畴开始
                if (multiKey == null && multiTask.contains(stack.peek().getTaskDefinitionKey())
                        && deleteKeyList.get(i).contains(stack.peek().getTaskDefinitionKey())) {
                    multiIndex = i;
                    multiKey = new StringBuilder(stack.peek().getTaskDefinitionKey());
                }
                // 会签脏数据处理，节点退回会签清空
                // 如果在会签脏数据范畴中发现 Key改变，说明会签脏数据在上个节点就结束了，可以把会签脏数据删掉
                if (multiKey != null && !multiKey.toString().equals(stack.peek().getTaskDefinitionKey())) {
                    deleteKeyList.set(multiIndex, deleteKeyList.get(multiIndex).replace(stack.peek().getTaskDefinitionKey() + ",", ""));
                    multiKey = null;
                    // 结束进行下校验删除
                    multiOpera = true;
                }
                // 其他脏数据处理
                // 发现该路线最后一条脏数据，说明这条脏数据线路处理完了，删除脏数据信息
                // 脏数据产生的新实例中是否包含这条数据
                if (multiKey == null && deleteKeyList.get(i).contains(stack.peek().getTaskDefinitionKey())) {
                    // 删除匹配到的部分
                    deleteKeyList.set(i, deleteKeyList.get(i).replace(stack.peek().getTaskDefinitionKey() + ",", ""));
                }
                // 如果每组中的元素都以匹配过，说明脏数据结束
                if ("".equals(deleteKeyList.get(i))) {
                    // 同时删除脏数据
                    deleteKeyList.remove(i);
                    dirtyDataLineList.remove(i);
                    break;
                }
            }
            // 会签数据处理需要在循环外处理，否则可能导致溢出
            // 会签的数据肯定是之前放进去的所以理论上不会溢出，但还是校验下
            if (multiOpera && deleteKeyList.size() > multiIndex && "".equals(deleteKeyList.get(multiIndex))) {
                // 同时删除脏数据
                deleteKeyList.remove(multiIndex);
                dirtyDataLineList.remove(multiIndex);
                multiIndex = -1;
                multiOpera = false;
            }
            // pop() 方法与 peek() 方法不同，在返回值的同时，会把值从栈中移除
            // 保存新的 userTaskKey 在下个循环中使用
            userTaskKey = new StringBuilder(stack.pop().getTaskDefinitionKey());
        }
        log.info("清洗后的历史节点数据：" + lastHistoricTaskInstanceList);
        return lastHistoricTaskInstanceList;
    }

    /**
     * @return Set<String> 流程中未走通（被驳回、撤回或未完成）的用户任务节点ID集合
     * @description 深度优先遍历流程中的用户任务节点，对比已完成连线与已完成任务，筛出未走通的用户任务节点
     * @params [bpmnModel, unfinishedTaskSet, finishedSequenceFlowSet, finishedTaskSet] 流程模型，为空时抛出业务异常；未结束的任务节点ID集合；已完成的连线ID集合；已完成的任务节点ID集合
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static Set<String> dfsFindRejects(BpmnModel bpmnModel, Set<String> unfinishedTaskSet, Set<String> finishedSequenceFlowSet, Set<String> finishedTaskSet) {
        if (ObjectUtil.isNull(bpmnModel)) {
            throw new ServiceException("流程模型不存在");
        }
        Collection<FlowElement> allElements = getAllElements(bpmnModel.getMainProcess().getFlowElements(), null);
        Set<String> rejectedSet = new HashSet<>();
        EndEvent endEvent = ModelUtils.getEndEvent(bpmnModel);
        String endActivityId = endEvent.getId();
        for (FlowElement flowElement : allElements) {
            // 用户节点且未结束元素 && unfinishedTaskSet.contains(flowElement.getId())
            if (flowElement instanceof UserTask) {
                List<String> rejects = new ArrayList<>();
                Boolean frame = false;
                // 用户节点且未结束元素
                if (flowElement instanceof UserTask && unfinishedTaskSet.contains(flowElement.getId())) {
                    List<String> hasSequenceFlow = iteratorFindFinishes(flowElement, null);
                    rejects = iteratorFindRejects(flowElement, finishedSequenceFlowSet, finishedTaskSet, hasSequenceFlow, null);
                    frame = true;
                }
                // 处理撤回事件
                //获取进线
                List<SequenceFlow> incomingFlows = getElementIncomingFlows(flowElement);
                Boolean isIncom = false;
                for (SequenceFlow incomingFlow : incomingFlows) {
                    if (finishedSequenceFlowSet.contains(incomingFlow.getId())) {
                        isIncom = true;
                    }
                }
                //获取出线
                List<SequenceFlow> outgoingFlows = getElementOutgoingFlows(flowElement);
                boolean isOut = false;
                for (SequenceFlow outgoingFlow : outgoingFlows) {
                    if (finishedSequenceFlowSet.contains(outgoingFlow.getId())) {
                        isOut = true;
                    }
                }
                //节点未完成，进线完成，出线未完成  撤回节点
                if (!finishedTaskSet.contains(flowElement.getId())
                        && isIncom
                        && !isOut
                        && !frame) {
                    rejects.add(flowElement.getId());
                }
                //节点完成  进线完成，出线未完成，且结束节点完成  // 拒绝任务
                if (isIncom && !isOut && finishedTaskSet.contains(flowElement.getId())
                        && finishedTaskSet.contains(endActivityId)) {
                    rejects.add(flowElement.getId());
                }

                rejectedSet.addAll(rejects);
            }

        }
        return rejectedSet;
    }

    /**
     * @return List<String> 向前找到的已完成连线ID列表
     * @description 沿入口连线向前递归收集已走过的连线ID，遇到子流程时额外收集子流程内第一个节点的入口连线
     * @params [source, hasSequenceFlow] 起始节点；已走过的连线ID集合，为空时新建
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static List<String> iteratorFindFinishes(FlowElement source, List<String> hasSequenceFlow) {
        hasSequenceFlow = hasSequenceFlow == null ? new ArrayList<>() : hasSequenceFlow;

        // 根据类型，获取入口连线
        List<SequenceFlow> sequenceFlows = getElementIncomingFlows(source);

        if (sequenceFlows != null) {
            // 循环找到目标元素
            for (SequenceFlow sequenceFlow : sequenceFlows) {
                // 如果发现连线重复，说明循环了，跳过这个循环
                if (hasSequenceFlow.contains(sequenceFlow.getId())) {
                    continue;
                }
                // 添加已经走过的连线
                hasSequenceFlow.add(sequenceFlow.getId());
                FlowElement finishedElement = sequenceFlow.getSourceFlowElement();
                // 类型为子流程，则添加子流程开始节点出口处相连的节点
                if (finishedElement instanceof SubProcess) {
                    FlowElement firstElement = (StartEvent) ((SubProcess) finishedElement).getFlowElements().toArray()[0];
                    // 获取子流程的连线
                    hasSequenceFlow.addAll(iteratorFindFinishes(firstElement, null));
                }
                // 继续迭代
                hasSequenceFlow = iteratorFindFinishes(finishedElement, hasSequenceFlow);
            }
        }
        return hasSequenceFlow;
    }

    /**
     * @return List<String> 向后收集到的未完成节点与未完成连线的ID列表
     * @description 沿出口连线向后递归收集未完成的节点与连线，遇到子流程时先进入子流程内部查找
     * @params [source, finishedSequenceFlowSet, finishedTaskSet, hasSequenceFlow, rejectedList] 起始节点；已完成的连线ID集合；已完成的任务节点ID集合；已走过的连线ID集合，用于防止重复；未通过的元素ID列表，为空时新建
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static List<String> iteratorFindRejects(FlowElement source, Set<String> finishedSequenceFlowSet, Set<String> finishedTaskSet
            , List<String> hasSequenceFlow, List<String> rejectedList) {
        hasSequenceFlow = hasSequenceFlow == null ? new ArrayList<>() : hasSequenceFlow;
        rejectedList = rejectedList == null ? new ArrayList<>() : rejectedList;

        // 根据类型，获取出口连线
        List<SequenceFlow> sequenceFlows = getElementOutgoingFlows(source);

        if (sequenceFlows != null) {
            // 循环找到目标元素
            for (SequenceFlow sequenceFlow : sequenceFlows) {
                // 如果发现连线重复，说明循环了，跳过这个循环
                if (hasSequenceFlow.contains(sequenceFlow.getId())) {
                    continue;
                }
                // 添加已经走过的连线
                hasSequenceFlow.add(sequenceFlow.getId());
                FlowElement targetElement = sequenceFlow.getTargetFlowElement();
                // 添加未完成的节点
                if (finishedTaskSet.contains(targetElement.getId())) {
                    rejectedList.add(targetElement.getId());
                }
                // 添加未完成的连线
                if (finishedSequenceFlowSet.contains(sequenceFlow.getId())) {
                    rejectedList.add(sequenceFlow.getId());
                }
                // 如果节点为子流程节点情况，则从节点中的第一个节点开始获取
                if (targetElement instanceof SubProcess) {
                    FlowElement firstElement = (FlowElement) (((SubProcess) targetElement).getFlowElements().toArray()[0]);
                    List<String> childList = iteratorFindRejects(firstElement, finishedSequenceFlowSet, finishedTaskSet, hasSequenceFlow, null);
                    // 如果找到节点，则说明该线路找到节点，不继续向下找，反之继续
                    if (childList != null && childList.size() > 0) {
                        rejectedList.addAll(childList);
                        continue;
                    }
                }
                // 继续迭代
                rejectedList = iteratorFindRejects(targetElement, finishedSequenceFlowSet, finishedTaskSet, hasSequenceFlow, rejectedList);
            }
        }
        return rejectedList;
    }

}
