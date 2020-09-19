package com.gradel.parent.component.web.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.server.ServerWebExchange;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public class RequestUtil {
	public static String getFullRequestUrl(HttpServletRequest request) {
		StringBuffer requestURL = request.getRequestURL();
		String queryString = request.getQueryString();

		if (queryString == null) {
			return requestURL.toString();
		} else {
			return requestURL.append('?').append(queryString).toString();
		}
	}

	public static String getHeader(HttpServletRequest request, String name) {
		return request.getHeader(name);
	}
	public static String getParameter(HttpServletRequest request, String name) {
		return request.getParameter(name);
	}

	public static String getParameter(NativeWebRequest request, String name) {
		return request.getParameter(name);
	}
	public static MultiValueMap<String, MultipartFile> getMultiFiles(NativeWebRequest request) {
		MultiValueMap<String, MultipartFile> value = ((MultipartHttpServletRequest) request.getNativeRequest()).getMultiFileMap();
		return value;
	}
	public static Object getAttribute(HttpServletRequest request, String name) {
		return request.getAttribute(name);
	}

	public static Object getAttribute(NativeWebRequest request, String name, int scope) {
		Object value = request.getAttribute(name, scope);
		return value;
	}

	public static String getHeader(NativeWebRequest request, String name) {
		String value = "";
		try {			
			value = request.getHeader(name);
			if (null != value && !value.trim().isEmpty()) {
				value = URLDecoder.decode(value, StandardCharsets.UTF_8.name());
			}
		} catch (UnsupportedEncodingException e) {
			log.error("getHeader error", e);
		}
		return value;
	}
	public static String getIp(NativeWebRequest nativeReq) {
		HttpServletRequest request = nativeReq.getNativeRequest(HttpServletRequest.class);
		assert request != null;
		return getIp(request);
	}
	public static String getIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if(null != ip && !ip.trim().isEmpty() && !"unKnown".equalsIgnoreCase(ip)){
			//多次反向代理后会有多个ip值，第一个ip才是真实ip
			int index = ip.indexOf(",");
			if(index != -1){
				return ip.substring(0,index);
			}else{
				return ip;
			}
		}
		ip = request.getHeader("X-Real-IP");
		if(null != ip && !ip.trim().isEmpty() && !"unKnown".equalsIgnoreCase(ip)){
			return ip;
		}
		return request.getRemoteAddr();
	}
	
	public static Object setAttribute(HttpServletRequest request, String name, Object value) {
		Object oldValue = RequestUtil.getAttribute(request, name);
		request.setAttribute(name, value);
		return oldValue;
	}
	
	public static JSONObject getJsonRequestBody(HttpServletRequest request) {
		String xml = getXMLRequestBody(request);
		return getJsonRequestBodyFromXml(xml);
	}
	
	public static String getXMLRequestBody(HttpServletRequest request) {
		try {
			String xml = null;
			BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8.name()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while (null != (line = in.readLine())) {
				sb.append(line.trim());
			}
			xml = sb.toString();
			log.info("getXMLRequestBodyFrom xml:{}", xml);
			return xml;
		} catch (Exception e) {
			log.error("getXMLRequestBodyFrom error", e);
			return null;
		}
	}
	
	public static JSONObject getJsonRequestBodyFromXml(String xml) {
		JSONObject body = new JSONObject();
		try {
			Map<String, String> map = xmlToMap(xml);
			body.putAll(map);
		} catch (Exception e) {
			log.error("getRequestBodyFromXmlData error", e);
		}
		
		return body;
	}
	
	public static JSONObject getRequestBodyFromFormData(HttpServletRequest request, String... excludesURLDecode) {
		JSONObject body = new JSONObject();
		Enumeration<String> names = request.getParameterNames();
		List<String>  excludeList = null == excludesURLDecode ? new ArrayList<>() : Arrays.asList(excludesURLDecode);
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			String[] values = request.getParameterValues(name);
			Object v = null;
			try {
				if (null != values && 1 == values.length) {
					String value = values[0].trim();
					if (0 < value.length()) {
						if (!excludeList.contains(name)) {
							v = URLDecoder.decode(value, StandardCharsets.UTF_8.name());
						}
						else {
							v = value;
						}
					}
				}
				else if (null != values && 1 < values.length) {				
					JSONArray array = new JSONArray();
					for (String value : values) {
						String s = value.trim();
						if (0 < s.length()) {
							if (!excludeList.contains(name)) {
								array.add(URLDecoder.decode(value, StandardCharsets.UTF_8.name()));
							}
							else {
								array.add(value);
							}
						}
					}
					v = array;
				}
				if (null != v) {
					body.put(name, v);
				}
			}
			catch (Exception e) {
				log.error("getRequestBodyFromFormData error", e);
			}
		}
		return body;
	}

	public static String getPath(ServerHttpRequest request) {
		return request.getURI().getPath();
	}
	public static String getMethod(ServerHttpRequest request) {
		return request.getMethodValue();
	}

	public static String getIp(ServerHttpRequest request) {
		HttpHeaders headers = request.getHeaders();
		String ip = headers.getFirst("x-forwarded-for");
		if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
			// 多次反向代理后会有多个ip值，第一个ip才是真实ip
			if (ip.contains(",")) {
				ip = ip.split(",")[0];
			}
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = headers.getFirst("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = headers.getFirst("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = headers.getFirst("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = headers.getFirst("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = headers.getFirst("X-Real-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = Objects.requireNonNull(request.getRemoteAddress()).getAddress().getHostAddress();
		}
		return ip;
	}

	public static String getToken(ServerHttpRequest request) {
		return request.getHeaders().getFirst("Authorization");
	}

	public static ServerWebExchange addLocationHeader(ServerWebExchange exchange, String ip, Integer channel) {
		ServerHttpRequest newRequest =  exchange.getRequest().mutate()
				.header("ip",ip)
				.header("channel", String.valueOf(channel))
				.build();
		return exchange.mutate().request(newRequest).build();
	}

	public static String getHeader(ServerHttpRequest request, String name) {
		return request.getHeaders().getFirst(name);
	}
	public static List<String> getHeaderList(ServerHttpRequest request, String name) {
		return request.getHeaders().get(name);
	}
	public static List<String> getHeaderList(NativeWebRequest request, String name) {
		String[] vs = request.getHeaderValues(name);
		if (null != vs && vs.length > 0) {
			return Arrays.asList(vs);
		}
		return new ArrayList<>();
	}

	/**
	 * 实体类对象转URL参
	 * @param t 实体类对象
	 * @param callSuper 是否转换父类成员
	 * @param <T> 实体类泛型
	 * @return a=1&b=2
	 */
	public static <T> String entityToUrlParam(T t, boolean callSuper){
		// URL 参数存储器
		StringBuffer urlParam = new StringBuffer();
		//扩展转换父类成员功能
		entitySuperclassToUrlParam(t, t.getClass(),callSuper,urlParam);
		if(urlParam.length()>0){
			//去除最后一个&字符
			urlParam.deleteCharAt(urlParam.length() - 1);
		}
		return urlParam.toString();
	}

	/**
	 * 实体类对象转URL参
	 * @param t 实体类对象
	 * @param clazz 实体类类型
	 * @param callSuper 是否转换父类成员
	 * @param urlParam URL 参数存储器
	 * @param <T> 实体类泛型
	 * @return a=1&b=2
	 */
	@Deprecated
	public static <T> void entitySuperclassToUrlParam(T t, Class clazz, boolean callSuper, StringBuffer urlParam){
		//如果实体类对象为Object类型，则不处理
		if(!clazz.equals(Object.class)) {
			//获取实体类对象下的所有成员，并保存到 URL 参数存储器中
			Arrays.stream(clazz.getDeclaredFields()).forEach(field -> {
				//设置可以操作私有成员
				field.setAccessible(true);
				try {
					//获取成员值
					Object value = field.get(t);
					//成员值为 Null 时，则不处理
					if (Objects.nonNull(value)) {
						if (value.getClass().isArray()) {
							urlParam.append(field.getName()).append("=").append(StringUtils.join((Object[])value,",")).append("&");
						}else if( value instanceof List){
							urlParam.append(field.getName()).append("=").append(StringUtils.join((List)value,",")).append("&");
						}else{
							urlParam.append(field.getName()).append("=").append(value).append("&");
						}
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			});
			//是否转换父类成员
			if(callSuper){
				//获取父类类型
				clazz = clazz.getSuperclass();
				//递归调用，获取父类的处理结果
				entitySuperclassToUrlParam(t,clazz,callSuper,urlParam);
			}
		}
	}

    public static Map<String, String> xmlToMap(String strXML) throws Exception {
        Map<String, String> data = new HashMap<>();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder= documentBuilderFactory.newDocumentBuilder();
        InputStream stream = new ByteArrayInputStream(strXML.getBytes(StandardCharsets.UTF_8.name()));
        org.w3c.dom.Document doc = documentBuilder.parse(stream);
        doc.getDocumentElement().normalize();
        NodeList nodeList = doc.getDocumentElement().getChildNodes();
        for (int idx=0; idx<nodeList.getLength(); ++idx) {
            Node node = nodeList.item(idx);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                data.put(element.getNodeName(), element.getTextContent());
            }
        }
        try {
            stream.close();
        }
        catch (Exception e) {
        	log.error("xmlToMap error" ,e);
        }
        return data;
    }
}
