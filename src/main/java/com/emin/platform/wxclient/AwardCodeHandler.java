/**
 * 
 */
package com.emin.platform.wxclient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSONObject;

/**
 * @author jim.lee
 *
 */
public class AwardCodeHandler {

	private static Map<String,JSONObject> consumedAwardCodes = new ConcurrentHashMap<String,JSONObject>();
	
	public static synchronized void addCode(String code,JSONObject result) {
		consumedAwardCodes.put(code,result);
	}
	public static synchronized JSONObject getCode(String code) {
		return consumedAwardCodes.get(code);
	}
	public static synchronized boolean hasCode(String code) {
		return consumedAwardCodes.containsKey(code);
	}
	public static synchronized void removeCode(String code) {
		consumedAwardCodes.remove(code);
	}
}
