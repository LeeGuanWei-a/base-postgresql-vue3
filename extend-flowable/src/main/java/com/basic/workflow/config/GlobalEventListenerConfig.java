package com.basic.workflow.config;


import com.basic.flowable.listener.GlobalEventListener;
import lombok.AllArgsConstructor;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.engine.RuntimeService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @description 流程全局事件监听配置，容器启动完成后把全局监听器注册到流程结束事件上
 * @author yanbin
 * @date 2023/10/19
 */
@Configuration
@AllArgsConstructor
public class GlobalEventListenerConfig implements ApplicationListener<ContextRefreshedEvent> {

	private final GlobalEventListener globalEventListener;
	private final RuntimeService runtimeService;

	/**
	 * @description 容器刷新完成后把全局监听器注册到流程正常结束事件上
	 * @params [event] 容器刷新事件
	 * @author liguanwei
	 * @date 2026/10/17 20:52
	 * @remark (改代码时写明修改时间修改人修改原因)
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		// 流程正常结束
		runtimeService.addEventListener(globalEventListener, FlowableEngineEventType.PROCESS_COMPLETED);
	}
}
