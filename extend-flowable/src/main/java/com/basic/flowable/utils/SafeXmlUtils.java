package com.basic.flowable.utils;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;

/**
 * BPMN XML 安全解析工具。
 *
 * <p>BPMN XML 由前端设计器提交，属于外部输入，必须防 XXE（XML External Entity）：
 * 关闭 DTD / 外部实体 / 外部 DTD 加载，避免读本地文件或外连内网地址。</p>
 *
 * @author basic
 */
public final class SafeXmlUtils {

    /**
     * @description 工具类私有构造，禁止外部实例化
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private SafeXmlUtils() {
    }

    /**
     * @return XMLInputFactory 已关闭 DTD 与外部实体加载的解析器工厂
     * @description 新建 XML 解析工厂并逐项关闭 DTD、外部实体与外部 DTD 加载，防止 XXE 攻击，个别属性不被支持时静默跳过
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static XMLInputFactory createSafeXmlInputFactory() {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        setPropertyQuietly(factory, XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
        setPropertyQuietly(factory, XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
        setPropertyQuietly(factory, "javax.xml.stream.isSupportingExternalEntities", Boolean.FALSE);
        setPropertyQuietly(factory, "http://xml.org/sax/features/external-general-entities", Boolean.FALSE);
        setPropertyQuietly(factory, "http://xml.org/sax/features/external-parameter-entities", Boolean.FALSE);
        return factory;
    }

    /**
     * @return XMLStreamReader 基于安全工厂与 XML 文本构建的流式读取器
     * @description 用安全工厂把 XML 文本包装成 XMLStreamReader，初始化失败时抛出 IllegalArgumentException
     * @params [xml] 待解析的 BPMN XML 文本
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static XMLStreamReader createSafeXmlStreamReader(String xml) {
        try {
            return createSafeXmlInputFactory().createXMLStreamReader(new StringReader(xml));
        } catch (Exception e) {
            throw new IllegalArgumentException("XML 解析器初始化失败：" + e.getMessage(), e);
        }
    }

    /**
     * @description 给工厂设置 XML 属性，实现不支持该属性时静默忽略，不影响后续属性设置
     * @params [factory, name, value] 目标工厂；属性名；属性值
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private static void setPropertyQuietly(XMLInputFactory factory, String name, Object value) {
        try {
            factory.setProperty(name, value);
        } catch (IllegalArgumentException ignored) {
            // 该实现不支持此属性，跳过
        }
    }
}
