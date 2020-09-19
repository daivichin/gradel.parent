package com.gradel.parent.common.util.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * 字符串处理工具类
 * @author sdeven.chen.dongwei@gmail.com
 * @mender
 */
public class StringUtils {
	/**
	 * 字符串加密
	 * 
	 * @param str
	 * @return
	 */
	public static String getMd5(String str) {
		String returnStr = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			md.update(str.getBytes());
			byte ss[] = md.digest();
			returnStr = byte2String(ss);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return returnStr;
	}
	
	public static boolean isotEmpty(String str){
		boolean flag =false;
		if(str != null && !"".equals(str)){
			flag = true;
		}
		
		return flag;
	}

	/**
	 * 
	 * @param b
	 * @return
	 */
	public static String byte2String(byte[] b) {
		String hash = "";
		for (int i = 0; i < b.length; i++) {
			int temp;
			if (b[i] < 0) {
				temp = 256 + b[i];
			} else {
				temp = b[i];
			}
			if (temp < 16) {
				hash += "0";
			}
			hash += Integer.toString(temp, 16);
		}
		hash = hash.toUpperCase();
		return hash;
	}

	public static String baseString(BigInteger num, int base) {
		String str = "",
				digit = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ~!@#$%^&*()_+|?/.,':[]{}";
		if (num.shortValue() == 0) {
			return "";
		} else {
			BigInteger valueOf = BigInteger.valueOf(base);
			str = baseString(num.divide(valueOf), base);
			return str + digit.charAt(num.mod(valueOf).shortValue());
		}
	}

	public static void main(String[] args) {
		BigInteger num = BigInteger.valueOf(200000L);
		String str = baseString(num, 10);
		System.out.println(str);

		int i = 123;
		java.text.DecimalFormat df = new java.text.DecimalFormat("0000000000");
		System.out.println(df.format(i));
	}

	/**
	 * 判断字符串是否为空
	 * 
	 * @param str
	 *            字符串
	 * @return 是否为空
	 */
	public static boolean isEmpty(String str) {
		return (str == null || str.trim().length() == 0);
	}

	public static boolean isNotEmpty(String str) {
		return !StringUtils.isEmpty(str);
	}

	public static boolean isEmpty(Object str) {
		return (str == null || str.toString().trim().length() == 0);
	}

	/*
	 * public static boolean isNotBlank(String str) { return
	 * org.apache.commons.lang3.StringUtils.isNotBlank(str); }
	 */

	public static boolean isNotEmpty(Object str) {
		return !StringUtils.isEmpty(str);
	}

	public static boolean hasText(final String str) {
		return StringUtils.isNotEmpty(str);
	}

	/**
	 * 功能: 如果字符串为 null ,"","null"的各种变体 返回预设字符 作者: 王春阳 创建日期:2015-8-14
	 * 
	 * @param exp
	 * @param result
	 * @return
	 */
	public static String replaceNvl(String exp, String result) {
		if (null == exp || "".equals(exp) || "null".equalsIgnoreCase(exp)) {
			return result;
		} else {
			return exp;
		}
	}

	public static boolean isNotEmptyOrNvlStr(String str) {
		return !(str == null || str.trim().length() == 0 || "null".equalsIgnoreCase(str));
	}

	public static boolean isNotEmptyOrNvlObj(Object str) {
		return !(str == null || str.toString().trim().length() == 0 || "null".equalsIgnoreCase(str.toString()));
	}

	//正则所有方法
	public static boolean isNullOrNvlStr(String str) {
		return str == null || str.trim().length() == 0;
	}
	/**
	 * 功能:colume2property 比喻:TB_CUS_FARMER_MANAGE -->tbCusFarmerManage 作者: 王宝金
	 * 创建日期:2015-11-18
	 * 
	 * @param str
	 * @return
	 */
	public static String colume2property(String str) {
		if (StringUtils.isEmpty(str)) {
			return "";
		}
		str = str.toLowerCase();
		while (str.contains("_")) {
			int i = str.indexOf("_");
			if (i + 1 < str.length()) {
				char c = str.charAt(i + 1);
				String temp = (c + "").toUpperCase();
				str = str.replace("_" + c, temp);
			}
		}
		return str;
	}
   
	/**
	 * 生成主键
	 * @Title: getPrikeyUuid
	 * @return
	 * @return: String
	 */
	public static String getPrikeyUuid() {
		String str = UUID.randomUUID().toString();
		str = str.replace("-", "");
		return str;

	}

	public static String getExcelNumVal(String params,int start,int stop){
		if (params == null || "".equals(params.trim())) {
			return "";
		} else {
			return params.substring(start,stop);
		}
	}
}
