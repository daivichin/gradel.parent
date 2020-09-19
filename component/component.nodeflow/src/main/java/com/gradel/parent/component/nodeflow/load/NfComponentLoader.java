package com.gradel.parent.component.nodeflow.load;

import java.util.Collection;
import java.util.Map;

import com.gradel.parent.component.nodeflow.Nodeflow;
import com.gradel.parent.component.nodeflow.spi.NfComponent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Nodeflow comppent spring loader
 * 
 * @author sdeven.chen.dongwei@gmail.com
 * @date 2017年8月15日 下午3:44:35
 */
@Component
public class NfComponentLoader implements ApplicationListener<ContextRefreshedEvent> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.context.ApplicationListener#onApplicationEvent(org.
	 * springframework.context.ApplicationEvent)
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent cre) {
		ApplicationContext ctx = cre.getApplicationContext();
		if (ctx.getParent() == null) {
			Map<String, NfComponent> beansMap = ctx.getBeansOfType(NfComponent.class);
			Collection<NfComponent> list = beansMap.values();
			for (NfComponent component : list) {
				Nodeflow.register(component);
			}
		}
	}

}
