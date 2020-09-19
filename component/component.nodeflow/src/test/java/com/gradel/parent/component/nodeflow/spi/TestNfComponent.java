package com.gradel.parent.component.nodeflow.spi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author sdeven.chen.dongwei@gmail.com
 * @date 2017年7月28日 下午5:16:46
 */
public class TestNfComponent implements NfComponent {
	private static final Logger logger = LoggerFactory.getLogger(TestNfComponent.class);


	public void execute(Map<String, Object> node, Map<String, Object> ctx) {
		logger.debug("this is test component.");
	}


	public String spiId() {
		return "test";
	}

}
