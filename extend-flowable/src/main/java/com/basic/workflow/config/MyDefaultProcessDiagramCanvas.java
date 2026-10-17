package com.basic.workflow.config;

import org.flowable.bpmn.model.AssociationDirection;
import org.flowable.image.impl.DefaultProcessDiagramCanvas;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;

/**
 * @description 流程图绘制画布，重写连线绘制以高亮已走通连线并补画箭头与条件流标记
 * @author yanbin
 * @date 2023/10/19
 */
public class MyDefaultProcessDiagramCanvas extends DefaultProcessDiagramCanvas {
    //设置高亮线的颜色  这里我设置成绿色
    protected static Color HIGHLIGHT_SEQUENCEFLOW_COLOR = Color.GREEN;

    /**
     * @description 按图片类型、坐标原点、各类字体名与自定义类加载器构造流程图绘制画布，内部转调父类初始化
     * @params [width, height, minX, minY, imageType, activityFontName, labelFontName, annotationFontName, customClassLoader] width 画布宽度；height 画布高度；minX 绘图区最小 X 坐标；minY 绘图区最小 Y 坐标；imageType 图片类型；activityFontName 节点字体名；labelFontName 连线标签字体名；annotationFontName 注释字体名；customClassLoader 自定义类加载器
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public MyDefaultProcessDiagramCanvas(int width, int height, int minX, int minY, String imageType, String activityFontName, String labelFontName, String annotationFontName, ClassLoader customClassLoader) {
        super(width, height, minX, minY, imageType, activityFontName, labelFontName, annotationFontName, customClassLoader);
    }

    /**
     * @description 按图片类型与坐标原点构造流程图绘制画布，内部转调父类构造
     * @params [width, height, minX, minY, imageType] width 画布宽度；height 画布高度；minX 绘图区最小 X 坐标；minY 绘图区最小 Y 坐标；imageType 图片类型
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public MyDefaultProcessDiagramCanvas(int width, int height, int minX, int minY, String imageType) {
        super(width, height, minX, minY, imageType);
    }


    /**
     * @description 用节点坐标逐段绘制连线，并按条件流、默认流、关联方向与高亮状态补画对应指示符
     * @params [xPoints, yPoints, conditional, isDefault, connectionType, associationDirection, highLighted, scaleFactor] xPoints 连线各折点 X 坐标；yPoints 连线各折点 Y 坐标；conditional 是否为条件流；isDefault 是否为默认流；connectionType 连线类型；associationDirection 关联方向；highLighted 是否高亮该连线；scaleFactor 绘制缩放比例
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void drawConnection(int[] xPoints, int[] yPoints, boolean conditional, boolean isDefault, String connectionType,
                               AssociationDirection associationDirection, boolean highLighted, double scaleFactor) {

        Paint originalPaint = g.getPaint();
        Stroke originalStroke = g.getStroke();

        g.setPaint(CONNECTION_COLOR);
        if (connectionType.equals("association")) {
            g.setStroke(ASSOCIATION_STROKE);
        } else if (highLighted) {
            //设置线的颜色
            g.setPaint(originalPaint);
            g.setStroke(HIGHLIGHT_FLOW_STROKE);
        }

        for (int i = 1; i < xPoints.length; i++) {
            Integer sourceX = xPoints[i - 1];
            Integer sourceY = yPoints[i - 1];
            Integer targetX = xPoints[i];
            Integer targetY = yPoints[i];
            Line2D.Double line = new Line2D.Double(sourceX, sourceY, targetX, targetY);
            g.draw(line);
        }

        if (isDefault) {
            Line2D.Double line = new Line2D.Double(xPoints[0], yPoints[0], xPoints[1], yPoints[1]);
            drawDefaultSequenceFlowIndicator(line, scaleFactor);
        }

        if (conditional) {
            Line2D.Double line = new Line2D.Double(xPoints[0], yPoints[0], xPoints[1], yPoints[1]);
            drawConditionalSequenceFlowIndicator(line, scaleFactor);
        }

        if (associationDirection == AssociationDirection.ONE || associationDirection == AssociationDirection.BOTH) {
            Line2D.Double line = new Line2D.Double(xPoints[xPoints.length - 2], yPoints[xPoints.length - 2], xPoints[xPoints.length - 1], yPoints[xPoints.length - 1]);
            drawArrowHead(line, scaleFactor);
        }
        if (associationDirection == AssociationDirection.BOTH) {
            Line2D.Double line = new Line2D.Double(xPoints[1], yPoints[1], xPoints[0], yPoints[0]);
            drawArrowHead(line, scaleFactor);
        }
        g.setPaint(originalPaint);
        g.setStroke(originalStroke);
    }

    /**
     * @description 用高亮颜色与粗边框在指定位置绘制圆角矩形，标记当前已流转到的节点
     * @params [x, y, width, height] x 节点左上角 X 坐标；y 节点左上角 Y 坐标；width 节点宽度；height 节点高度
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void drawHighLight(int x, int y, int width, int height) {
        Paint originalPaint = g.getPaint();
        Stroke originalStroke = g.getStroke();
        //设置高亮节点的颜色
        g.setPaint(HIGHLIGHT_COLOR);
        g.setStroke(THICK_TASK_BORDER_STROKE);

        RoundRectangle2D rect = new RoundRectangle2D.Double(x, y, width, height, 20, 20);
        g.draw(rect);

        g.setPaint(originalPaint);
        g.setStroke(originalStroke);
    }
}
