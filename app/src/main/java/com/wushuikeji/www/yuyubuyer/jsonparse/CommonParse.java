package com.wushuikeji.www.yuyubuyer.jsonparse;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

/**
 * resultMap装结果的map
 * jsonObject new出来
 */
public class CommonParse {

	public static Map<String, Object> commonParse(Map<String, Object> resultMap, JSONObject jsonObject, String wantKey) {
		try {
			Iterator<String> it = jsonObject.keys();
			while (it.hasNext()) {
				String key = it.next();
				if(wantKey == null){
					Object value = jsonObject.get(key);
					resultMap.put(key, value);
					try {
						if(value != null){
							JSONObject temp = (JSONObject) value;
							commonParse(resultMap, temp, null);
						}
					} catch (Exception e) {
					}
				}else{
					if(wantKey.equals(key)){
						Object value = jsonObject.get(key);
						resultMap.put(key, value);
						try {
							if(value != null){
								JSONObject temp = (JSONObject) value;
								commonParse(resultMap, temp, null);
							}
						} catch (Exception e) {
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}
}
