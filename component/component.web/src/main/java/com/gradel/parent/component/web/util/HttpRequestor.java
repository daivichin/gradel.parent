package com.gradel.parent.component.web.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class HttpRequestor {
	private String proxyHost = null;
	private Integer proxyPort = null;

	/**
	 * Do GET request
	 *
	 */
	public String doGet(String url) {
		StringBuilder reponse = new StringBuilder();
		BufferedReader in = null;
		try {

			HttpURLConnection conn = openConnection(url);

			String charset = "utf-8";
			conn.setRequestProperty("Accept-Charset", charset);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			String line;

			if (conn.getResponseCode() >= 300) {
				throw new Exception("HTTP Request is not success, Response code is " + conn.getResponseCode());
			}

			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
			while ((line = in.readLine()) != null) {
				reponse.append(line);
			}
		} catch (Exception e) {
			log.error("doGet error", e);
		} finally {
			try {
				if(null != in) {
					in.close();
				}
			} catch (IOException e) {
				log.error("doGet error", e);
			}

		}

		return reponse.toString();
	}

	public InputStream doGetInputStream(String url) {
		try {
			HttpURLConnection conn = openConnection(url);
			//设置请求方式为"GET"
			conn.setRequestMethod("GET");
			//超时响应时间为5秒
			conn.setConnectTimeout(8000);
			//通过输入流获取图片数据
			return conn.getInputStream();
		}catch (Exception e) {
			log.error("doGet error", e);
			return null;
		}
	}

	 /**
     * post请求
     * @return
     */
	public String doPost(String url, JSONObject json, boolean isForm) {
		OutputStreamWriter out = null;
		BufferedReader in = null;
		StringBuilder response = new StringBuilder();
		try {
			String params = isForm ? this.genPostFormData(json) : json.toJSONString();
			HttpURLConnection conn =(HttpURLConnection) new URL(url).openConnection();

			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// POST方法
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept", "*/*");
			// 设置维持长连接
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 设置文件字符集:
			conn.setRequestProperty("Charset", StandardCharsets.UTF_8.name());
			// 设置文件长度
			conn.setRequestProperty("Content-Length", String.valueOf(params.getBytes().length));
			// 设置文件类型:
			conn.setRequestProperty("contentType", isForm ? "application/x-www-form-urlencoded" : "application/json");

			conn.connect();


			// 获取URLConnection对象对应的输出流
			out = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8);
			// 发送请求参数
			out.write(params);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
			String line;
			while ((line = in.readLine()) != null) {
				response.append(line);
			}
		} catch (Exception e) {
			log.error("doPost error", e);
		} finally{
			try{
				if(out!=null){
					out.close();
				}
				if(in!=null){
					in.close();
				}
			}
			catch(IOException ex){
				log.error("doPost error", ex);
			}
		}
		return response.toString();
	}

	private String genPostFormData(JSONObject json) {
		StringBuilder param = new StringBuilder();
		for (Map.Entry<String, Object> entry : json.entrySet()) {
			if(param.length()>0){
				param.append("&");
			}
			param.append(entry.getKey());
			param.append("=");
			param.append(entry.getValue());
		}
		return param.toString();
	}


	private HttpURLConnection openConnection(String url) throws IOException {
		URL localURL = new URL(url);
		URLConnection connection;
		if (proxyHost != null && proxyPort != null) {
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
			connection = localURL.openConnection(proxy);
		} else {
			connection = localURL.openConnection();
		}
		return (HttpURLConnection) connection;
	}
}