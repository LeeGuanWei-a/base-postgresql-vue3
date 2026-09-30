package com.basic;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

/**
 * 启动程序
 * 
 * @author basic
 */
//@ComponentScan({"com.basic.**"})
@SpringBootApplication(scanBasePackages = {"com.basic","com.core"},exclude = { DataSourceAutoConfiguration.class })
@MapperScan({"com.core.**.mapper"})
public class BasicApplication
{
    public static void main(String[] args)
    {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(BasicApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  基础平台启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}
