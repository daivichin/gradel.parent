package com.gradel.parent.component.nodeflow;

/**
 * Nodeflow exception
 * 
 * @author sdeven.chen.dongwei@gmail.com
 * @date 2017年8月18日 上午9:27:14
 */
public class NodeflowException extends Exception {
	private static final long serialVersionUID = 9222894717715477267L;
	private String[] params;

	public NodeflowException(String code, String... params) {
		super(code);
		this.params = params;
	}

	public NodeflowException(String code, Throwable t) {
		super(code, t);
	}

	public NodeflowException(String code, Throwable t, String... params) {
		super(code, t);
		this.params = params;
	}

	public String[] getParams() {
		return params;
	}

	public void setParams(String[] params) {
		this.params = params;
	}

}
