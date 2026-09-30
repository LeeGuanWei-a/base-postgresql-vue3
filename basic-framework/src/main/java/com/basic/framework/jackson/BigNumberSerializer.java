package com.basic.framework.jackson;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.annotation.JacksonStdImpl;

/**
 * 超出 JS 最大最小值 处理
 * Spring Boot 4 用 Jackson 3（tools.jackson.*），原 Jackson 2 的 NumberSerializer 已不存在，
 * 这里直接继承 ValueSerializer<Number>，完全自己写 serialize 逻辑。
 */
@JacksonStdImpl
public class BigNumberSerializer extends ValueSerializer<Number> {

    /**
     * 根据 JS Number.MAX_SAFE_INTEGER 与 Number.MIN_SAFE_INTEGER 得来
     */
    private static final long MAX_SAFE_INTEGER = 9007199254740991L;
    private static final long MIN_SAFE_INTEGER = -9007199254740991L;

    /**
     * 提供实例
     */
    public static final BigNumberSerializer INSTANCE = new BigNumberSerializer();

    @Override
    public void serialize(Number value, JsonGenerator gen, SerializationContext ctxt)
            throws JacksonException {
        // 超出范围 序列化为字符串
        if (value.longValue() > MIN_SAFE_INTEGER && value.longValue() < MAX_SAFE_INTEGER) {
            gen.writeNumber(value.longValue());
        } else {
            gen.writeString(value.toString());
        }
    }
}
