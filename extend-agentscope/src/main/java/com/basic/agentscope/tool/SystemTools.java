package com.basic.agentscope.tool;

import io.agentscope.core.tool.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 通用工具集 —— 演示不依赖数据库的"纯函数型"工具。
 */
@Component
public class SystemTools {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取当前服务器时间。
     *
     * @return 当前时间字符串
     */
    @Tool(name = "current_datetime",
            description = "当用户询问『现在几点』『当前时间』『今天几号』时，调用本工具获取服务器当前时间。",
            readOnly = true)
    public String currentDatetime() {
        return LocalDateTime.now().format(FMT);
    }

    /**
     * 计算两数之间的差值。
     *
     * @param a 被减数
     * @param b 减数
     * @return 差值
     */
    @Tool(name = "subtract_numbers",
            description = "当需要进行两个数字的减法计算时，调用本工具。参数 a 是被减数，b 是减数。",
            readOnly = true)
    public Map<String, Object> subtractNumbers(double a, double b) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("a", a);
        result.put("b", b);
        result.put("result", a - b);
        return result;
    }

    /**
     * 回显一段文本，用于验证工具调用链路。
     *
     * @param text 要回显的文本
     * @return 回显结果
     */
    @Tool(name = "echo_text",
            description = "当用户明确要求『回显』『重复这句话』时，调用本工具原样返回参数 text 的内容。"
                    + "本工具主要用于验证工具调用链路是否通畅。",
            readOnly = true)
    public String echoText(String text) {
        return "ECHO: " + (text == null ? "" : text);
    }
}
