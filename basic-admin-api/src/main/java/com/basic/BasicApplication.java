package com.basic;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

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
        // 关闭 spring-boot-devtools 的 Restart 功能：
        // 1) devtools 自带独立的 RestartClassLoader，启动时会对比"基线 class"和当前 classpath，
        //    在多模块 incremental build 场景下（extend-flowable 等子模块 target 被 mvn clean 删掉后）
        //    经常抛出 NoClassDefFoundError: .../rollBack/RollbackOperateStrategy 之类的伪缺失；
        // 2) 流程引擎模块的 service 关系复杂，devtools 触发重启时再去 hot swap 容易破坏 ProcessEngine 状态。
        // 想恢复热部署：注释掉下面这行，并确保 application-dev.yml 中 spring.devtools.restart.enabled=true。
        System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(BasicApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  基础平台启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}
