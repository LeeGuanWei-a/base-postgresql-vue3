package com.basic.framework.config;

import cloud.tianai.captcha.resource.DefaultBuiltInResources;
import cloud.tianai.captcha.resource.ResourceStore;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.impl.LocalMemoryResourceStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

/**
 * tianai-captcha 1.5.2 资源初始化
 *
 * 启动时主动加载:
 *   1. SLIDER / ROTATE 模板(社区版 jar 内置, 路径 classpath:META-INF/cut-image/template)
 *   2. 各类型的背景图资源(社区版仅 1.jpg 一张, 用它作 SLIDER/ROTATE/WORD_IMAGE_CLICK 共用背景)
 *   3. WORD_IMAGE_CLICK 所需的中文字体 (1.5.2 改为通过 ResourceStore 注入, FontCache 会自动监听)
 *
 * 1.5.x 行为变更: MultiImageCaptchaGenerator 不再暴露 getFontWrappers/setFontWrappers,
 *   字体改成作为 ResourceStore 中 type="font" 的资源注入, FontCache 监听 onAddResource 事件.
 */
@Configuration
@Order(0)
public class TianaiCaptchaConfig
{
    private static final Logger log = LoggerFactory.getLogger(TianaiCaptchaConfig.class);

    private static final String TYPE_SLIDER = "SLIDER";
    private static final String TYPE_ROTATE = "ROTATE";
    private static final String TYPE_WORD_CLICK = "WORD_IMAGE_CLICK";

    // 1.5.2 中字体作为普通资源注册,type 固定为 "font"
    private static final String FONT_TYPE = "font";

    private static final String DEFAULT_TEMPLATE_PATH = "classpath:META-INF/cut-image/template";
    private static final String DEFAULT_BACKGROUND_PATH = "classpath:META-INF/cut-image/resource";

    /**
     * 自定义 ResourceStore: 加载默认模板 + 4 种类型背景图 + 字体
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

        try
        {
            registerDefaultFonts(store);
        }
        catch (Exception e)
        {
            log.warn("[tianai-captcha] failed to register default fonts: {}", e.getMessage());
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
     * 1.5.2 字体注册: 通过 store.addResource("font", resource) 注入, FontCache 监听器会自动构建字体映射.
     *
     * 注意: 字体 data 必须是 TTF/TTC 实际文件路径(走 ClassPathResourceProvider / FileResourceProvider),
     *   tianai 内部用 Font.createFont(Font.TRUETYPE_FONT, InputStream) 加载.
     *   "system" provider 是 1.5.0 的写法, 1.5.2 已不再支持.
     *
     * tag 必须设为 "default" —— StandardWordClickImageCaptchaGenerator 通过
     *   requiredRandomGetResource("font", "default") 查找字体, 写死 "default".
     */
    private void registerDefaultFonts(LocalMemoryResourceStore store)
    {
        // 优先使用 tianai 1.5.2 自带的 SIMSUN.TTC (META-INF/fonts/SIMSUN.TTC, 中文字符集).
        // 这是社区版唯一内置的字体, 也是 WORD_IMAGE_CLICK 生成中文点选提示的核心依赖.
        // Resource 4-arg ctor: (type, data, tag, tip). tag="default" 满足 StandardWordClickImageCaptchaGenerator 查找.
        Resource simsun = new Resource("classpath", "META-INF/fonts/SIMSUN.TTC", "default", null);
        store.addResource(FONT_TYPE, simsun);
        log.info("[tianai-captcha] registered classpath:META-INF/fonts/SIMSUN.TTC for WORD_IMAGE_CLICK (tag=default)");
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
}
