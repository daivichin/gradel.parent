package com.gradel.parent.component.nodeflow.groovy;

import java.lang.reflect.Method;

import groovy.lang.Script;

/**
 * 定义自己的表达式解析类
 * 
 * @author sdeven.chen.dongwei@gmail.com
 * @date 2017年7月14日 下午2:35:06
 */
public class MyBasicScript extends Script {
	@Override
	public Object run() {
		// show usage
		Method[] methods = MyBasicScript.class.getDeclaredMethods();
		StringBuilder sb = new StringBuilder();
		for (Method method : methods) {
			sb.append(method);
		}

		return sb.substring(0, sb.length() - 1);
	}

	public static Object nvl(Object str, Object val) {
		return str == null || "".equals(str) ? val : str;
	}

	public static boolean empty(Object str) {
		return str == null || "".equals(str.toString().trim());
	}

	public static double min(double... val) {
		double min = val[0];
		for (int i = 1; i < val.length; i++) {
			min = Math.min(min, val[i]);
		}
		return min;
	}

	public static double max(double... val) {
		double max = val[0];
		for (int i = 1; i < val.length; i++) {
			max = Math.max(max, val[i]);
		}
		return max;
	}
}
