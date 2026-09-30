package com.basic.framework.config;

import com.basic.framework.jackson.BigNumberSerializer;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.StdScalarSerializer;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 只给 Web 用的默认 JsonMapper 加 BigNumberSerializer/ToStringSerializer Module。
 * 不激活 defaultTyping —— Web API 返回值是给前端 JS 消费的，不能包成 [类名, {...}] 数组。
 * Redis 序列化用的 ObjectMapper 在 RedisConfig 里单独构造（带 defaultTyping），
 * 不走 JsonMapperBuilderCustomizer 路径。
 */
@Configuration
public class BigNumberModuleConfig {

    /**
     * Jackson 3 等价于 Jackson 2 的 ToStringSerializer。
     */
    public static class ToStringSerializer extends StdScalarSerializer<Object> {
        public static final ToStringSerializer instance = new ToStringSerializer();

        protected ToStringSerializer() {
            super(Object.class);
        }

        @Override
        public void serialize(Object value, tools.jackson.core.JsonGenerator gen,
                              tools.jackson.databind.SerializationContext ctxt)
                throws tools.jackson.core.JacksonException {
            gen.writeString(value.toString());
        }
    }

    @Bean
    public JsonMapperBuilderCustomizer bigNumberJsonMapperBuilderCustomizer() {
        JacksonModule module = new SimpleModule()
                .addSerializer(Long.class, BigNumberSerializer.INSTANCE)
                .addSerializer(Long.TYPE, BigNumberSerializer.INSTANCE)
                .addSerializer(BigInteger.class, BigNumberSerializer.INSTANCE)
                .addSerializer(BigDecimal.class, ToStringSerializer.instance);
        return builder -> builder.addModule(module);
    }
}
