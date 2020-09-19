package com.gradel.parent.component.nodeflow.spi;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import java.util.Map;

/**
 * Nodeflow component interface
 * @author sdeven.chen.dongwei@gmail.com
 * @date 2017年7月28日 上午10:34:15
 */

//@ComponentScan(value = "com.annotation",useDefaultFilters = false,includeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,classes = {NfComponent.class})})
@ComponentScan(useDefaultFilters = false,includeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,classes = {NfComponent.class})})
public interface NfComponent {

	 void execute(Map<String,Object> node, Map<String, Object> ctx);

	 String spiId();
}
