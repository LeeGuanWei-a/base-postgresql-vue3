package com.basic.flowable.flow;

import org.flowable.bpmn.model.AssociationDirection;
import org.flowable.bpmn.model.GraphicInfo;
import org.flowable.image.impl.DefaultProcessDiagramCanvas;
import org.flowable.image.util.ReflectUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

/**
 * @description 流程图绘制画布，重写连线绘制逻辑，为已走通连线着色高亮并补画箭头与默认流标记
 * @author yanbin
 * @date 2023/10/19
 */
public class CustomProcessDiagramCanvas extends DefaultProcessDiagramCanvas {
    //定义走过流程连线颜色为绿色
    protected static Color HIGHLIGHT_SequenceFlow_COLOR = Color.GREEN;
    //设置未走过流程的连接线颜色
    protected static Color CONNECTION_COLOR = Color.BLACK;
    //设置flows连接线字体颜色red
    protected static Color LABEL_COLOR = new Color(0, 0, 0);
    //高亮显示task框颜色
    protected static Color HIGHLIGHT_COLOR = Color.GREEN;
    protected static Color HIGHLIGHT_COLOR1 = Color.RED;

    /**
     * @description 按给定画布尺寸、原点偏移与字体参数创建流程图绘制画布，并立即初始化画布
     * @params [width, height, minX, minY, imageType, activityFontName, labelFontName, annotationFontName, customClassLoader] 画布宽度；画布高度；图形区域的最小X坐标；图形区域的最小Y坐标；图片格式，png 时使用带透明通道的图像；节点字体名；连线标签字体名；标注字体名；加载 Flowable 图标资源使用的类加载器
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public CustomProcessDiagramCanvas(int width, int height, int minX, int minY, String imageType, String activityFontName, String labelFontName, String annotationFontName, ClassLoader customClassLoader) {
        super(width, height, minX, minY, imageType, activityFontName, labelFontName, annotationFontName, customClassLoader);
        this.initialize(imageType);
    }

    /**
     * @description 重写绘制连线的方式，按是否走通、是否关联线、是否默认流选择画笔颜色与粗细，逐段绘制折线并补画箭头与默认流标记
     * @params [xPoints, yPoints, conditional, isDefault, connectionType, associationDirection, highLighted, scaleFactor] 连线各转折点X坐标；连线各转折点Y坐标；是否为条件分支连线；是否为默认连线；连线类型，association 表示关联线；关联线方向；连线是否已走通高亮；图形缩放比例
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void drawConnection(int[] xPoints, int[] yPoints, boolean conditional, boolean isDefault, String connectionType, AssociationDirection associationDirection, boolean highLighted, double scaleFactor) {
        Paint originalPaint = this.g.getPaint();
        Stroke originalStroke = this.g.getStroke();
        this.g.setPaint(CONNECTION_COLOR);
        if (connectionType.equals("association")) {
            this.g.setStroke(ASSOCIATION_STROKE);
        } else if (highLighted) {
            this.g.setPaint(HIGHLIGHT_SequenceFlow_COLOR);
            this.g.setStroke(HIGHLIGHT_FLOW_STROKE);
        }

        for (int i = 1; i < xPoints.length; ++i) {
            int sourceX = xPoints[i - 1];
            int sourceY = yPoints[i - 1];
            int targetX = xPoints[i];
            int targetY = yPoints[i];
            java.awt.geom.Line2D.Double line = new java.awt.geom.Line2D.Double((double) sourceX, (double) sourceY, (double) targetX, (double) targetY);
            this.g.draw(line);
        }

        java.awt.geom.Line2D.Double line;
        if (isDefault) {
            line = new java.awt.geom.Line2D.Double((double) xPoints[0], (double) yPoints[0], (double) xPoints[1], (double) yPoints[1]);
            this.drawDefaultSequenceFlowIndicator(line, scaleFactor);
        }

        if (conditional) {
            line = new java.awt.geom.Line2D.Double((double) xPoints[0], (double) yPoints[0], (double) xPoints[1], (double) yPoints[1]);
            this.drawConditionalSequenceFlowIndicator(line, scaleFactor);
        }

        if (associationDirection.equals(AssociationDirection.ONE) || associationDirection.equals(AssociationDirection.BOTH)) {
            line = new java.awt.geom.Line2D.Double((double) xPoints[xPoints.length - 2], (double) yPoints[xPoints.length - 2], (double) xPoints[xPoints.length - 1], (double) yPoints[xPoints.length - 1]);
            this.drawArrowHead(line, scaleFactor);
        }

        if (associationDirection.equals(AssociationDirection.BOTH)) {
            line = new java.awt.geom.Line2D.Double((double) xPoints[1], (double) yPoints[1], (double) xPoints[0], (double) yPoints[0]);
            this.drawArrowHead(line, scaleFactor);
        }

        this.g.setPaint(originalPaint);
        this.g.setStroke(originalStroke);
    }

    /**
     * @description 按图片格式创建画布图像与画笔，重置图标颜色、节点与连线字体大小，并加载 Flowable 各类型节点的图标资源
     * @params [imageType] 图片格式，png 时创建带透明通道的图像
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void initialize(String imageType) {
        if ("png".equalsIgnoreCase(imageType)) {
            this.processDiagram = new BufferedImage(this.canvasWidth, this.canvasHeight, 2);
        } else {
            this.processDiagram = new BufferedImage(this.canvasWidth, this.canvasHeight, 1);
        }

        this.g = this.processDiagram.createGraphics();
        if (!"png".equalsIgnoreCase(imageType)) {
            this.g.setBackground(new Color(255, 255, 255, 0));
            this.g.clearRect(0, 0, this.canvasWidth, this.canvasHeight);
        }

        this.g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //修改图标颜色，修改图标字体大小
        this.g.setPaint(Color.black);
        Font font = new Font(this.activityFontName, 10, 14);
        this.g.setFont(font);
        this.fontMetrics = this.g.getFontMetrics();
        //修改连接线字体大小
        LABEL_FONT = new Font(this.labelFontName, 10, 15);
        ANNOTATION_FONT = new Font(this.annotationFontName, 0, 11);

        try {
            USERTASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/flowable/icons/userTask.png", this.customClassLoader));
            SCRIPTTASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/flowable/icons/scriptTask.png", this.customClassLoader));
            SERVICETASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/flowable/icons/serviceTask.png", this.customClassLoader));
            RECEIVETASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/flowable/icons/receiveTask.png", this.customClassLoader));
            SENDTASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/flowable/icons/sendTask.png", this.customClassLoader));
            MANUALTASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/flowable/icons/manualTask.png", this.customClassLoader));
            BUSINESS_RULE_TASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/flowable/icons/businessRuleTask.png", this.customClassLoader));
            SHELL_TASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/flowable/icons/shellTask.png", this.customClassLoader));
            DMN_TASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/flowable/icons/dmnTask.png", this.customClassLoader));
            // PR2: Flowable 8.x 移除了 CAMEL_TASK_IMAGE / MULE_TASK_IMAGE 常量（参考父类 DefaultProcessDiagramCanvas）
            // CAMEL_TASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/flowable/icons/camelTask.png", this.customClassLoader));
            // MULE_TASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/flowable/icons/muleTask.png", this.customClassLoader));
            HTTP_TASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/flowable/icons/httpTask.png", this.customClassLoader));
            TIMER_IMAGE = ImageIO.read(ReflectUtil.getResource("org/flowable/icons/timer.png", this.customClassLoader));
            COMPENSATE_THROW_IMAGE = ImageIO.read(ReflectUtil.getResource("org/flowable/icons/compensate-throw.png", this.customClassLoader));
            COMPENSATE_CATCH_IMAGE = ImageIO.read(ReflectUtil.getResource("org/flowable/icons/compensate.png", this.customClassLoader));
            ERROR_THROW_IMAGE = ImageIO.read(ReflectUtil.getResource("org/flowable/icons/error-throw.png", this.customClassLoader));
            ERROR_CATCH_IMAGE = ImageIO.read(ReflectUtil.getResource("org/flowable/icons/error.png", this.customClassLoader));
            MESSAGE_THROW_IMAGE = ImageIO.read(ReflectUtil.getResource("org/flowable/icons/message-throw.png", this.customClassLoader));
            MESSAGE_CATCH_IMAGE = ImageIO.read(ReflectUtil.getResource("org/flowable/icons/message.png", this.customClassLoader));
            SIGNAL_THROW_IMAGE = ImageIO.read(ReflectUtil.getResource("org/flowable/icons/signal-throw.png", this.customClassLoader));
            SIGNAL_CATCH_IMAGE = ImageIO.read(ReflectUtil.getResource("org/flowable/icons/signal.png", this.customClassLoader));
        } catch (IOException var4) {
            LOGGER.warn("Could not load image for process diagram creation: {}", var4.getMessage());
        }

    }

    /**
     * @description 在给定图形位置上按固定换行宽度绘制连线标签文字，居中时按图形宽度做水平居中
     * @params [text, graphicInfo, centered] 标签文字内容；标签所在图形的坐标与尺寸信息；文字是否水平居中
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void drawLabel(String text, GraphicInfo graphicInfo, boolean centered) {
        float interline = 1.0f;

        // text
        if (text != null && text.length() > 0) {
            Paint originalPaint = g.getPaint();
            Font originalFont = g.getFont();

            g.setPaint(LABEL_COLOR);
            g.setFont(LABEL_FONT);

            int wrapWidth = 100;
            int textY = (int) graphicInfo.getY();

            // TODO use drawMultilineText()
            AttributedString as = new AttributedString(text);
            as.addAttribute(TextAttribute.FOREGROUND, g.getPaint());
            as.addAttribute(TextAttribute.FONT, g.getFont());
            AttributedCharacterIterator aci = as.getIterator();
            FontRenderContext frc = new FontRenderContext(null, true, false);
            LineBreakMeasurer lbm = new LineBreakMeasurer(aci, frc);

            while (lbm.getPosition() < text.length()) {
                TextLayout tl = lbm.nextLayout(wrapWidth);
                textY += tl.getAscent();

                Rectangle2D bb = tl.getBounds();
                double tX = graphicInfo.getX();

                if (centered) {
                    tX += (int) (graphicInfo.getWidth() / 2 - bb.getWidth() / 2);
                }
                tl.draw(g, (float) tX, textY);
                textY += tl.getDescent() + tl.getLeading() + (interline - 1.0f) * tl.getAscent();
            }

            // restore originals
            g.setFont(originalFont);
            g.setPaint(originalPaint);
        }
    }

    /**
     * @description 用绿色粗边框圆角矩形框出已完成的 task 节点
     * @params [x, y, width, height] 节点左上角X坐标；节点左上角Y坐标；节点宽度；节点高度
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void drawHighLight(int x, int y, int width, int height) {
        Paint originalPaint = g.getPaint();
        Stroke originalStroke = g.getStroke();

        g.setPaint(HIGHLIGHT_COLOR);
        g.setStroke(THICK_TASK_BORDER_STROKE);

        RoundRectangle2D rect = new RoundRectangle2D.Double(x, y, width, height, 20, 20);
        g.draw(rect);

        g.setPaint(originalPaint);
        g.setStroke(originalStroke);
    }

    /**
     * @description 用红色粗边框圆角矩形框出当前正在办理的 task 节点
     * @params [x, y, width, height] 节点左上角X坐标；节点左上角Y坐标；节点宽度；节点高度
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public void drawHighLightNow(int x, int y, int width, int height) {
        Paint originalPaint = g.getPaint();
        Stroke originalStroke = g.getStroke();

        g.setPaint(HIGHLIGHT_COLOR1);
        g.setStroke(THICK_TASK_BORDER_STROKE);

        RoundRectangle2D rect = new RoundRectangle2D.Double(x, y, width, height, 20, 20);
        g.draw(rect);

        g.setPaint(originalPaint);
        g.setStroke(originalStroke);
    }

    /**
     * @description 用绿色粗边框圆角矩形标出流程结束节点
     * @params [x, y, width, height] 节点左上角X坐标；节点左上角Y坐标；节点宽度；节点高度
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public void drawHighLightEnd(int x, int y, int width, int height) {
        Paint originalPaint = g.getPaint();
        Stroke originalStroke = g.getStroke();

        g.setPaint(HIGHLIGHT_COLOR);
        g.setStroke(THICK_TASK_BORDER_STROKE);

        RoundRectangle2D rect = new RoundRectangle2D.Double(x, y, width, height, 20, 20);
        g.draw(rect);

        g.setPaint(originalPaint);
        g.setStroke(originalStroke);
    }

    /**
     * @description 按图形坐标绘制 task 节点圆角矩形底框与边框，并在非缩放状态下居中绘制节点名称
     * @params [name, graphicInfo, thickBorder, scaleFactor] 节点显示名称；节点坐标与尺寸信息；是否使用加粗边框；图形缩放比例，仅 1.0 时绘制文字
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    protected void drawTask(String name, GraphicInfo graphicInfo, boolean thickBorder, double scaleFactor) {

        Paint originalPaint = g.getPaint();
        int x = (int) graphicInfo.getX();
        int y = (int) graphicInfo.getY();
        int width = (int) graphicInfo.getWidth();
        int height = (int) graphicInfo.getHeight();

        // Create a new gradient paint for every task box, gradient depends on x and y and is not relative
        g.setPaint(TASK_BOX_COLOR);

        int arcR = 6;
        if (thickBorder) {
            arcR = 3;
        }

        // shape
        RoundRectangle2D rect = new RoundRectangle2D.Double(x, y, width, height, arcR, arcR);
        g.fill(rect);
        g.setPaint(TASK_BORDER_COLOR);

        if (thickBorder) {
            Stroke originalStroke = g.getStroke();
            g.setStroke(THICK_TASK_BORDER_STROKE);
            g.draw(rect);
            g.setStroke(originalStroke);
        } else {
            g.draw(rect);
        }

        g.setPaint(originalPaint);
        // text
        if (scaleFactor == 1.0 && name != null && name.length() > 0) {
            int boxWidth = width - (2 * TEXT_PADDING);
            int boxHeight = height - 16 - ICON_PADDING - ICON_PADDING - MARKER_WIDTH - 2 - 2;
            int boxX = x + width / 2 - boxWidth / 2;
            int boxY = y + height / 2 - boxHeight / 2 + ICON_PADDING + ICON_PADDING - 2 - 2;

            drawMultilineCentredText(name, boxX, boxY, boxWidth, boxHeight);
        }
    }

    protected static Color EVENT_COLOR = new Color(255, 255, 255);

    /**
     * @description 用白色填充并描边绘制开始事件圆形，存在图标时按缩放比例居中贴图
     * @params [graphicInfo, image, scaleFactor] 开始事件坐标与尺寸信息；开始事件图标，为空时只画圆形；图形缩放比例
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void drawStartEvent(GraphicInfo graphicInfo, BufferedImage image, double scaleFactor) {
        Paint originalPaint = g.getPaint();
        g.setPaint(EVENT_COLOR);
        Ellipse2D circle = new Ellipse2D.Double(graphicInfo.getX(), graphicInfo.getY(),
                graphicInfo.getWidth(), graphicInfo.getHeight());
        g.fill(circle);
        g.setPaint(EVENT_BORDER_COLOR);
        g.draw(circle);
        g.setPaint(originalPaint);
        if (image != null) {
            // calculate coordinates to center image
            int imageX = (int) Math.round(graphicInfo.getX() + (graphicInfo.getWidth() / 2) - (image.getWidth() / (2 * scaleFactor)));
            int imageY = (int) Math.round(graphicInfo.getY() + (graphicInfo.getHeight() / 2) - (image.getHeight() / (2 * scaleFactor)));
            g.drawImage(image, imageX, imageY,
                    (int) (image.getWidth() / scaleFactor), (int) (image.getHeight() / scaleFactor), null);
        }

    }

    /**
     * @description 用白色填充并描边绘制无图标结束事件圆形，边框粗细随缩放比例调整
     * @params [graphicInfo, scaleFactor] 结束事件坐标与尺寸信息；图形缩放比例，1.0 时使用结束事件默认描边
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void drawNoneEndEvent(GraphicInfo graphicInfo, double scaleFactor) {
        Paint originalPaint = g.getPaint();
        Stroke originalStroke = g.getStroke();
        g.setPaint(EVENT_COLOR);
        Ellipse2D circle = new Ellipse2D.Double(graphicInfo.getX(), graphicInfo.getY(),
                graphicInfo.getWidth(), graphicInfo.getHeight());
        g.fill(circle);
        g.setPaint(EVENT_BORDER_COLOR);
//        g.setPaint(HIGHLIGHT_COLOR);
        if (scaleFactor == 1.0) {
            g.setStroke(END_EVENT_STROKE);
        } else {
            g.setStroke(new BasicStroke(2.0f));
        }
        g.draw(circle);
        g.setStroke(originalStroke);
        g.setPaint(originalPaint);
    }
}
