package com.basic.workflow.flow;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.GraphicInfo;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.SequenceFlow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 「加签 / 动态插入节点」时的极简 DI 布局。
 *
 * <p>背景：Flowable 8 移除了 {@code org.flowable.bpmn.BpmnAutoLayout}
 * （{@code flowable-bpmn-layout} 最后版本是 6.8.0），参考项目原来靠它给动态插入的
 * UserTask 补图形坐标。没有 DI 的节点在流程图里会被直接跳过 —— 流程能流转，但图上"少了一节"。</p>
 *
 * <p>这里不追求把整张图排得好看（那是真正的 AutoLayout，几千行），只做一件事：
 * <b>把新节点插到前驱节点右侧，并把右侧已有节点整体右移，避免重叠</b>；
 * 同时把这些被移动节点的连线 waypoint 一起平移，保持连线大致正确。</p>
 *
 * <p>只用于「派生流程定义」（加签/改图），不会影响用户在设计器里保存的原始模型。</p>
 *
 * @author basic
 */
public final class SimpleBpmnAutoLayout {

    private static final Logger log = LoggerFactory.getLogger(SimpleBpmnAutoLayout.class);

    /** 新节点默认尺寸（与 Flowable 设计器里 UserTask 默认大小一致） */
    private static final double NEW_WIDTH = 100.0;
    private static final double NEW_HEIGHT = 80.0;
    /** 节点之间的水平/垂直间距 */
    private static final double GAP = 60.0;

    /**
     * @description 工具类私有构造，禁止实例化
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private SimpleBpmnAutoLayout() {
    }

    /**
     * @description 给插在前驱节点后面的新节点补图形坐标，并把右侧已有节点整体右移后平移其连线 waypoint，避免图上重叠
     * @params [bpmnModel, anchor, newNode] 派生流程模型；前驱节点，新节点被插在它后面；新插入的节点
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static void layoutInjectedNode(BpmnModel bpmnModel, FlowElement anchor, FlowNode newNode) {
        if (bpmnModel == null || newNode == null || newNode.getId() == null) {
            return;
        }
        Map<String, GraphicInfo> locations = bpmnModel.getLocationMap();
        GraphicInfo anchorInfo = anchor == null ? null : locations.get(anchor.getId());
        if (anchorInfo == null) {
            // 原模型没有 DI（纯 XML 手工写的流程），随便放一个位置，保证节点能被画出来
            locations.put(newNode.getId(), buildGraphicInfo(newNode, 100.0, 100.0));
            log.debug("新节点 {} 的前驱没有 DI 坐标，使用默认位置", newNode.getId());
            return;
        }

        double newX = anchorInfo.getX() + anchorInfo.getWidth() + GAP;
        double newY = anchorInfo.getY();
        // 右侧整体右移的量：新节点宽度 + 间距
        double shift = NEW_WIDTH + GAP;

        // 1. 找出需要右移的节点（x 已经不早于新节点位置的）
        Set<String> shifted = new HashSet<>();
        for (Map.Entry<String, GraphicInfo> entry : new ArrayList<>(locations.entrySet())) {
            GraphicInfo info = entry.getValue();
            if (info == null) {
                continue;
            }
            if (info.getX() >= newX - 0.1) {
                info.setX(info.getX() + shift);
                shifted.add(entry.getKey());
            }
        }

        // 2. 连线 waypoint 跟着平移（源/目标任一端在移动集合里就整体平移）
        Map<String, List<GraphicInfo>> flowLocations = bpmnModel.getFlowLocationMap();
        Process process = bpmnModel.getMainProcess();
        if (process != null) {
            for (FlowElement element : process.getFlowElements()) {
                if (!(element instanceof SequenceFlow)) {
                    continue;
                }
                SequenceFlow flow = (SequenceFlow) element;
                List<GraphicInfo> points = flowLocations.get(flow.getId());
                if (points == null || points.isEmpty()) {
                    continue;
                }
                boolean moved = shifted.contains(flow.getSourceRef()) || shifted.contains(flow.getTargetRef());
                if (moved) {
                    for (GraphicInfo point : points) {
                        point.setX(point.getX() + shift);
                    }
                }
            }
        }

        // 3. 新节点自己的坐标
        locations.put(newNode.getId(), buildGraphicInfo(newNode, newX, newY));

        // 4. 和新节点相连的线：重算成「前驱右侧 → 新节点左侧」的直线
        if (process != null) {
            for (FlowElement element : process.getFlowElements()) {
                if (!(element instanceof SequenceFlow)) {
                    continue;
                }
                SequenceFlow flow = (SequenceFlow) element;
                boolean touchesNewNode = newNode.getId().equals(flow.getSourceRef())
                        || newNode.getId().equals(flow.getTargetRef());
                if (!touchesNewNode) {
                    continue;
                }
                GraphicInfo source = locations.get(flow.getSourceRef());
                GraphicInfo target = locations.get(flow.getTargetRef());
                if (source == null || target == null) {
                    continue;
                }
                List<GraphicInfo> points = new ArrayList<>(2);
                points.add(new GraphicInfo(source.getX() + source.getWidth(), source.getY() + source.getHeight() / 2));
                points.add(new GraphicInfo(target.getX(), target.getY() + target.getHeight() / 2));
                flowLocations.put(flow.getId(), points);
            }
        }

        log.debug("已为动态插入的节点 {} 补 DI：x={}, y={}, 右移节点 {} 个", newNode.getId(), newX, newY, shifted.size());
    }

    /**
     * @return GraphicInfo 带节点尺寸与所属元素的图形坐标信息
     * @description 按固定节点尺寸与给定左上角坐标构造图形坐标信息
     * @params [node, x, y] 目标节点；左上角横坐标；左上角纵坐标
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private static GraphicInfo buildGraphicInfo(FlowNode node, double x, double y) {
        GraphicInfo info = new GraphicInfo();
        info.setX(x);
        info.setY(y);
        info.setWidth(NEW_WIDTH);
        info.setHeight(NEW_HEIGHT);
        info.setElement(node);
        return info;
    }
}
