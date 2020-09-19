package com.gradel.parent.common.util.util;

import com.gradel.parent.common.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;

import com.alibaba.fastjson.JSON;

@Slf4j
public class CommHttpUtils {
	private static final String CHARSET_UTF_8 = "UTF-8";
	private static final String CONTENT_TYPE = "Content-Type";

	public static String remotePost(String url,Object data){
		HttpClient httpClient = new HttpClient();
		PostMethod post = new PostMethod(url);
		post.addRequestHeader(CONTENT_TYPE, "application/json;charset=utf-8");
		post.addRequestHeader("Accept", "application/json");
		httpClient.getParams().setContentCharset(CHARSET_UTF_8);
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(30000);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(30000);
		post.setRequestHeader(CONTENT_TYPE, "application/json");
		try {
			String requestStr = JSON.toJSONString(data);
			RequestEntity requestEntity =  new ByteArrayRequestEntity(requestStr.getBytes(CHARSET_UTF_8));
			post.setRequestEntity(requestEntity);
			httpClient.executeMethod(post);
			String resultStr = post.getResponseBodyAsString();
			return resultStr;
		} catch (Exception e) {
			throw new BusinessException("");
		}
	}
	
	public static String remotePut(String url,Object data){
		HttpClient httpClient = new HttpClient();
		PutMethod post = new PutMethod(url);
		post.addRequestHeader(CONTENT_TYPE, "application/json;charset=utf-8");
		post.addRequestHeader("Accept", "application/json");
		httpClient.getParams().setContentCharset(CHARSET_UTF_8);
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(30000);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(30000);
		post.setRequestHeader(CONTENT_TYPE, "application/json");
		try {
			String requestStr = JSON.toJSONString(data);
			RequestEntity requestEntity =  new ByteArrayRequestEntity(requestStr.getBytes(CHARSET_UTF_8));
			post.setRequestEntity(requestEntity);
			httpClient.executeMethod(post);
			String resultStr = post.getResponseBodyAsString();
			return resultStr;
		} catch (Exception e) {
			throw new BusinessException("");
		}
	}
	public static String remoteGet(String url){
		HttpClient httpClient = new HttpClient();
		GetMethod getmethod = new GetMethod(url);
		getmethod.addRequestHeader(CONTENT_TYPE, "application/json;charset=utf-8");
		getmethod.addRequestHeader("Accept", "application/json");
		httpClient.getParams().setContentCharset(CHARSET_UTF_8);
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(30000);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(30000);
		getmethod.setRequestHeader(CONTENT_TYPE, "application/json");
		try {
			httpClient.executeMethod(getmethod);
			String resultStr = getmethod.getResponseBodyAsString();
			return resultStr;
		} catch (Exception e) {
			throw new BusinessException("");
		}
	}
}
