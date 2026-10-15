package com.basic.flowable.config;


import com.basic.flowable.listener.GlobalEventListener;
import lombok.AllArgsConstructor;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.engine.RuntimeService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author yanbin
 * @description TODO (flowable全局监听配置)
 * @date 2023/10/19
 */
@Configuration
@AllArgsConstructor
public class GlobalEventListenerConfig implements ApplicationListener<ContextRefreshedEvent> {

	private final GlobalEventListener globalEventListener;
	private final RuntimeService runtimeService;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		// 流程正常结束
		runtimeService.addEventListener(globalEventListener, FlowableEngineEventType.PROCESS_COMPLETED);
	}
}
