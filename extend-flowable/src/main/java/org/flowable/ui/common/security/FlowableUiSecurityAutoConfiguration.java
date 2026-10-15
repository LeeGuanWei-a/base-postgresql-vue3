package org.flowable.ui.common.security;

import org.flowable.spring.boot.FlowableSecurityAutoConfiguration;
import org.flowable.spring.boot.idm.IdmEngineServicesAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/** 说明：重构FlowableUiSecurity自动配置 From：www.fhadmin.org */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter({
  IdmEngineServicesAutoConfiguration.class,
})
@AutoConfigureBefore({
  FlowableSecurityAutoConfiguration.class,
  OAuth2ClientAutoConfiguration.class,
})
public class FlowableUiSecurityAutoConfiguration {}
