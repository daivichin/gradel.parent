package com.gradel.parent.component.nodeflow;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.gradel.parent.common.util.util.StringUtils;
import com.gradel.parent.component.nodeflow.groovy.ExprSupport;
import com.gradel.parent.component.nodeflow.spi.NfComponent;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

/**
 * Nodeflow engine
 * 
 * @author sdeven.chen.dongwei@gmail.com
 * @date 2017年7月28日 上午9:54:00
 */
@Slf4j
public class Nodeflow {
	private static final String KEY_INDEXS = "indexs";
	private static final String KEY_NODES = "nodes";
	private static final String KEY_ID = "id";
	private static final String KEY_NEXT = "next";
	private static final String KEY_ERROR = "error";
	private static final String KEY_CONDITION = "condition";
	private static final String KEY_COMPONENT = "component";
	private static final String KEY_OPT = "opt";
	private static final String KEY_TYPE = "type";
	private static final String KEY_TYPE_NUMERIC = "NUMERIC";
	private static final String KEY_TYPE_LONG = "LONG";
	private static final String KEY_TYPE_INT = "INT";
	private static final String KEY_TYPE_DOUBLE = "DOUBLE";
	private static final String KEY_IDX = "idx";// index key
	private static final String KEY_NAME = "name";// index name
	private static final String KEY_MODE = "mode";// 1:input,2:output,3:input&output
	private static final String KEY_REGX = "regx";
	private static final String KEY_REGX_NAME = "regxName";
	private static final Map<String, NfComponent> INIT_COMPONENTS = new HashMap<String, NfComponent>();
	private static final Map<String, NfComponent> COMPONENTS = new HashMap<String, NfComponent>();
	static {
		ServiceLoader<NfComponent> services = ServiceLoader.load(NfComponent.class);
		for (NfComponent service : services) {
			INIT_COMPONENTS.put(service.spiId(), service);
		}
	}
	private JsonObject nf = null;
	private JsonArray indexs = null;
	private JsonArray nodes = null;
	private Map<String, Object> ctx;
	private String curNode = null;
	private Map<String, Object> output;
	private Map<String, Object> input;

	public Nodeflow(String nfjson) {
		Gson gson = new Gson();
		nf = gson.fromJson(nfjson, JsonObject.class);
		log.debug(nf.toString());
		indexs = nf.getAsJsonArray(KEY_INDEXS);
		nodes = nf.getAsJsonArray(KEY_NODES);
	}

	public static void register(NfComponent c) {
		if (componentExists(c)) {
			log.warn("The commponent[{}] register duplicated:{}", c.spiId(), c.getClass().getName());
		}
		COMPONENTS.put(c.spiId(), c);
	}

	public static boolean componentExists(NfComponent c) {
		return COMPONENTS.containsKey(c.spiId()) || INIT_COMPONENTS.containsKey(c.spiId());
	}

	public static NfComponent findComponent(String spiId) {
		NfComponent c = COMPONENTS.get(spiId);
		if (c == null) {
			c = INIT_COMPONENTS.get(spiId);
		}
		return c;
	}

	public void execute() throws NodeflowException {
		execute(new HashMap<String, Object>());
	}

	public void execute(Map<String, Object> ctx) throws NodeflowException {
		this.ctx = ctx;
		List<Map<String, Object>> list = fetchInputOutput(ctx);
		input = list.get(0);
		output = list.get(1);
		if (curNode == null || "".equals(curNode.trim())) {
			next(nodes.get(0).getAsJsonObject());
		} else {
			next(findNode(curNode, nodes));
		}
	}

	public Map<String, Object> getOutput() {
		if (output != null) {
			for (String key : output.keySet()) {
				output.put(key, ctx.get(key));
			}
		}
		return output;
	}

	public Map<String, Object> getInput() {
		return this.input;
	}

	private static int getAsInt(JsonObject obj, String key, int def) {
		JsonElement item = obj.get(key);
		return item == null||item.isJsonNull() ? def : item.getAsInt();
	}

	private static String getAsString(JsonObject obj, String key, String def) {
		JsonElement item = obj.get(key);
		return item == null||item.isJsonNull() ? def : item.getAsString();
	}
	
	private static Object parseType(String type, Object val) {
		switch(type.toUpperCase()) {
		case KEY_TYPE_INT:
			val = !StringUtils.isNotEmptyOrNvlObj(val)?0: Integer.parseInt(val.toString().trim());
			break;
		case KEY_TYPE_LONG:
			val = !StringUtils.isNotEmptyOrNvlObj(val)?0: Long.parseLong(val.toString().trim());
			break;
		case KEY_TYPE_DOUBLE:
			val = !StringUtils.isNotEmptyOrNvlObj(val)?0: Double.parseDouble(val.toString().trim());
			break;
		case KEY_TYPE_NUMERIC:
			val = !StringUtils.isNotEmptyOrNvlObj(val)?0:new BigDecimal(val.toString().trim());
			break;
		}
		return val;
	}

	public List<Map<String, Object>> fetchInputOutput(Map<String, Object> ctx) throws NodeflowException {
		Map<String, Object> input = new HashMap<String, Object>();
		Map<String, Object> output = new HashMap<String, Object>();
		for (int i = 0; indexs != null && i < indexs.size(); i++) {
			JsonObject indexItem = indexs.get(i).getAsJsonObject();
			int opt = getAsInt(indexItem, KEY_OPT, 0);
			int mode = getAsInt(indexItem, KEY_MODE, 1);
			String idx = indexItem.get(KEY_IDX).getAsString();
			String idxName = getAsString(indexItem, KEY_NAME, idx);
			String pattern = getAsString(indexItem, KEY_REGX, "");
			String type = getAsString(indexItem, KEY_TYPE, null);
			String patternName = getAsString(indexItem, KEY_REGX_NAME, idxName);
			Object val = ctx.get(idx);
			if(type!=null) {
				val = parseType(type, val);
			}
			if ((mode & 1) == 1) {
				if (opt == 0) {
					if (val == null || val.toString().trim().equals("")) {
						throw new IllegalArgumentException(idx + "-" + idxName);
					}
				}
				if (pattern != null && !pattern.equals("") && val != null && !val.toString().trim().equals("")) {
					if (!val.toString().matches(pattern)) {
						throw new IllegalArgumentException(idx + "-" + patternName);
					}
				}
				ctx.put(idx, val);
				input.put(idx, val);
			} else if ((mode & 2) == 2) {
				output.put(idx, val);
			}
		}
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list.add(input);
		list.add(output);
		return list;
	}

	private void next(JsonObject node) throws NodeflowException {
		String nodeId = node.get(KEY_ID).getAsString();
		log.debug("node:{}-{}", this, nodeId);
		if (nodeId != null) {
			curNode = nodeId;
		}
		JsonElement componentId = node.get(KEY_COMPONENT);
		if (componentId != null) {
			NfComponent cpt = findComponent(componentId.getAsString());
			if (cpt != null) {
				Type type = new TypeToken<Map<String, Object>>() {
				}.getType();
				Map<String, Object> nodemap = new Gson().fromJson(node, type);
				cpt.execute(nodemap, ctx);
			} else {
				throw new RuntimeException("Component unfound:" + componentId);
			}
		}
		JsonElement next = node.get(KEY_NEXT);
		String to = null;
		if (next != null && next.isJsonPrimitive()) {
			to = next.getAsString();
			next(findNode(to, nodes));
		} else if (next != null && next.isJsonArray()) {
			JsonArray nexts = next.getAsJsonArray();
			for (int i = 0; i < nexts.size(); i++) {
				JsonObject it = nexts.get(i).getAsJsonObject();
				JsonElement cond = it.get(KEY_CONDITION);
				String condition = null;
				if (cond!=null&&!"".equals((condition=cond.getAsString()).trim())) {
					if ((Boolean) ExprSupport.parseExpr(condition, ctx)) {
						to = it.get(KEY_NEXT).getAsString();
						next(findNode(to, nodes));
						break;
					} else {
						JsonElement errorEl = it.get(KEY_ERROR);
						if (errorEl != null) {
							throw new NodeflowException(errorEl.getAsString());
						}
					}
				} else {
					to = it.get(KEY_NEXT).getAsString();
					next(findNode(to, nodes));
					break;					
				}
			}
		}
	}

	private static JsonObject findNode(String id, JsonArray arr) {
		JsonObject item = null;
		for (int i = 0; i < arr.size(); i++) {
			item = arr.get(i).getAsJsonObject();
			if (id.equals(item.get(KEY_ID).getAsString())) {
				return item;
			}
		}
		return null;
	}

	public Map<String, Object> getCtx() {
		return ctx;
	}

	public String getCurNode() {
		return curNode;
	}

	public void setCurNode(String curNode) {
		this.curNode = curNode;
	}

	public static void clearComponents() {
		COMPONENTS.clear();
	}
}
