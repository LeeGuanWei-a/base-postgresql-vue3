package com.basic.web.config;

import com.basic.common.config.BasicConfig;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * springdoc-openapi 接口配置（替代原 springfox Swagger 2）
 *
 * <p>访问地址：http://host:port/swagger-ui/index.html
 *
 * @author basic
 */
@Configuration
public class SwaggerConfig
{
    /** 系统基础配置 */
    @Autowired
    private BasicConfig basicConfig;

    /**
     * 创建 OpenAPI Bean
     */
    @Bean
    public OpenAPI customOpenApi()
    {
        return new OpenAPI()
                .info(new Info()
                        // 设置标题
                        .title("标题：基础平台管理系统_接口文档")
                        // 描述
                        .description("描述：用于管理集团旗下公司的人员信息,具体包括XXX,XXX模块...")
                        // 作者信息
                        .contact(new Contact().name(basicConfig.getName()))
                        // 版本
                        .version("版本号:" + basicConfig.getVersion()))
                // 设置安全模式，这里指定token通过Authorization头请求头传递
                .components(new Components()
                        .addSecuritySchemes("Authorization",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization")))
                .addSecurityItem(new SecurityRequirement().addList("Authorization"));
    }
}
