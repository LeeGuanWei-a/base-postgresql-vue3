package com.basic.framework.config;

import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.common.FontWrapper;
import cloud.tianai.captcha.generator.impl.MultiImageCaptchaGenerator;
import cloud.tianai.captcha.resource.DefaultBuiltInResources;
import cloud.tianai.captcha.resource.ResourceStore;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.impl.LocalMemoryResourceStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;

/**
 * tianai-captcha 1.5.0 资源初始化
 *
 * 启动时主动加载:
 *   1. SLIDER / ROTATE 模板(社区版 jar 内置, 路径 classpath:META-INF/cut-image/template)
 *   2. 各类型的背景图资源(社区版仅 1.jpg 一张, 用它作 SLIDER/ROTATE/WORD_IMAGE_CLICK 共用背景)
 *   3. WORD_IMAGE_CLICK 所需的中文字体
 *
 * 这样 4 种类型都能立即生成, 不需要用户再额外准备模板图.
 */
@Configuration
@Order(0)
public class TianaiCaptchaConfig
{
    private static final Logger log = LoggerFactory.getLogger(TianaiCaptchaConfig.class);

    private static final String TYPE_SLIDER = "SLIDER";
    private static final String TYPE_ROTATE = "ROTATE";
    private static final String TYPE_WORD_CLICK = "WORD_IMAGE_CLICK";

    private static final String DEFAULT_TEMPLATE_PATH = "classpath:META-INF/cut-image/template";
    private static final String DEFAULT_BACKGROUND_PATH = "classpath:META-INF/cut-image/resource";

    /**
     * 自定义 ResourceStore: 加载默认模板 + 4 种类型背景图
     */
    @Bean
    @Primary
    public ResourceStore tianaiResourceStore(ApplicationContext applicationContext) throws Exception
    {
        LocalMemoryResourceStore store = new LocalMemoryResourceStore();

        try
        {
            new DefaultBuiltInResources(DEFAULT_TEMPLATE_PATH).addDefaultTemplate(store);
            log.info("[tianai-captcha] default templates (SLIDER/ROTATE) loaded");
        }
        catch (Exception e)
        {
            log.warn("[tianai-captcha] failed to load default templates: {}", e.getMessage());
        }

        try
        {
            registerDefaultBackgrounds(store, applicationContext);
        }
        catch (Exception e)
        {
            log.warn("[tianai-captcha] failed to register default backgrounds: {}", e.getMessage());
        }

        return store;
    }

    private void registerDefaultBackgrounds(LocalMemoryResourceStore store, ApplicationContext applicationContext) throws Exception
    {
        org.springframework.core.io.Resource[] files = applicationContext.getResources(DEFAULT_BACKGROUND_PATH + "/*");
        int count = 0;
        for (org.springframework.core.io.Resource f : files)
        {
            String fileName = f.getFilename();
            if (fileName == null) continue;
            if (!fileName.toLowerCase().endsWith(".jpg")
                    && !fileName.toLowerCase().endsWith(".jpeg")
                    && !fileName.toLowerCase().endsWith(".png"))
            {
                continue;
            }
            // ClassPathResourceProvider 用 data.getData() 直接 getResourceAsStream, 不带 classpath: 前缀
            // 所以 data 就是纯 classpath 相对路径
            String pureClasspathPath = "META-INF/cut-image/resource/" + fileName;
            Resource bgResource = new Resource("classpath", pureClasspathPath, null, null);
            store.addResource(TYPE_SLIDER, bgResource);
            store.addResource(TYPE_ROTATE, bgResource);
            store.addResource(TYPE_WORD_CLICK, bgResource);
            count++;
        }
        log.info("[tianai-captcha] registered {} default background(s) for 3 captcha types", count);
    }

    /**
     * 覆盖 starter 默认的 SimpleImageCaptchaValidator
     * (starter 里是 @ConditionalOnMissingBean, 定义自己的 Bean 即可生效)
     */
    @Bean
    @Primary
    public cloud.tianai.captcha.validator.ImageCaptchaValidator tianaiImageCaptchaValidator()
    {
        return new TypeAwareImageCaptchaValidator();
    }

    /**
     * 启动后钩子: 把字体塞进 starter 的 MultiImageCaptchaGenerator
     * (WORD_IMAGE_CLICK 必须要 fonts 不为空, 否则报 "请设置字体包后调用 init()")
     */
    @Bean
    public SmartInitializingSingleton tianaiFontInjector(
            @Autowired(required = false) ImageCaptchaGenerator imageCaptchaGenerator)
    {
        return () -> {
            if (imageCaptchaGenerator instanceof MultiImageCaptchaGenerator)
            {
                MultiImageCaptchaGenerator multi = (MultiImageCaptchaGenerator) imageCaptchaGenerator;
                if (multi.getFontWrappers() == null || multi.getFontWrappers().isEmpty())
                {
                    multi.setFontWrappers(buildFontWrappers());
                }
            }
        };
    }

    private List<FontWrapper> buildFontWrappers()
    {
        List<FontWrapper> wrappers = new ArrayList<>();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] candidates = {"宋体", "SimSun", "Microsoft YaHei", "Heiti SC", "WenQuanYi Micro Hei",
                "Noto Sans CJK SC", "PingFang SC", "黑体", "Dialog", "SansSerif"};
        for (String name : candidates)
        {
            Font f = new Font(name, Font.PLAIN, 30);
            if (f.getFamily().equalsIgnoreCase(name) || ge.registerFont(f))
            {
                FontWrapper wrapper = new FontWrapper();
                wrapper.setFont(f);
                wrapper.setCurrentFontTopCoef(0f);
                wrappers.add(wrapper);
            }
        }
        if (wrappers.isEmpty())
        {
            Font f = new Font(Font.SERIF, Font.PLAIN, 30);
            FontWrapper wrapper = new FontWrapper();
            wrapper.setFont(f);
            wrapper.setCurrentFontTopCoef(0f);
            wrappers.add(wrapper);
        }
        log.info("[tianai-captcha] built {} font wrapper(s) for WORD_IMAGE_CLICK", wrappers.size());
        return wrappers;
    }
}

