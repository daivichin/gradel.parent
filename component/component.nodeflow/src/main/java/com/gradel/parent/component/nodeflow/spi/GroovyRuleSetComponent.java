package com.gradel.parent.component.nodeflow.spi;

import com.gradel.parent.component.nodeflow.groovy.ExprSupport;

import java.util.ArrayList;
import java.util.Map;

/**
 * Groovy 规则执行组件
 * @author sdeven.chen.dongwei@gmail.com
 * @date 2017年7月28日 下午1:52:34
 */
public class GroovyRuleSetComponent implements NfComponent {
	private static final String KEY_RULESET = "ruleset";

	/* 
	 * @see com.gradel.parent.component.nodeflow.spi.NfComponent#execute(java.util.Map, java.util.Map)
	 */
	@Override
	public void execute(Map<String, Object> node, Map<String, Object> ctx) {
		ArrayList<String> ruleset = (ArrayList<String>)node.get(KEY_RULESET);
		StringBuffer buf = new StringBuffer();
		for(int i=0;i<ruleset.size();i++) {
			if(i!=0) {
				buf.append(";");
			}
			buf.append(ruleset.get(i));
		}
		ExprSupport.parseExpr(buf.toString(), ctx);
	}

	/* 
	 * @see com.gradel.parent.component.nodeflow.spi.NfComponent#spiId()
	 */
	@Override
	public String spiId() {
		return KEY_RULESET;
	}

}
