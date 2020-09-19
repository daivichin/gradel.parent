package com.gradel.parent.common.rocketmq.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author sdeven.chen.dongwei@gmail.com
 */
@Configuration
public class MQConfig {
	@Bean
	public MQProperties mqProperties() {
		return new MQProperties();
	}

	@Bean
	public MQProducer mqProducer() {
		return new MQProducer();
	}

	@Bean
	public RocketMQAnnotationScan mqAnnotation() {
		return new RocketMQAnnotationScan();
	}
}
