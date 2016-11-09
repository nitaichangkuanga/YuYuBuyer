package com.wushuikeji.www.yuyubuyer.jsonparse;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jack_chentao
 */
public class MyObjeceParse {

    public static Map<String, String> getAlipayParse(String jsonString) {

        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject obj = new JSONObject(jsonString);

            String conten = obj.getString( "content");
            JSONObject object = new JSONObject(conten);

            String sign = object.optString("sign");
            if(TextUtils.isEmpty(sign) || "null".equals(sign)) {
                map.put("sign", "");
            }else {
                map.put("sign", sign);
            }

            String url = object.optString("url");
            if(TextUtils.isEmpty(url) || "null".equals(url)) {
                map.put("url", "");
            }else {
                map.put("url", url);
            }
            String param = object.optString("param");
            if(TextUtils.isEmpty(param) || "null".equals(param)) {
                map.put("param", "");
            }else {
                map.put("param", param);
            }

            String out_trade_no = object.optString("out_trade_no");
            if(TextUtils.isEmpty(out_trade_no) || "null".equals(out_trade_no)) {
                map.put("out_trade_no", "");
            }else {
                map.put("out_trade_no", out_trade_no);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static Map<String, String> getWeiXinParse(String jsonString) {

        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject obj = new JSONObject(jsonString);

            String conten = obj.getString( "content");
            JSONObject object = new JSONObject(conten);

            String sign = object.optString("sign");
            if(TextUtils.isEmpty(sign) || "null".equals(sign)) {
                map.put("sign", "");
            }else {
                map.put("sign", sign);
            }

            String prepayid = object.optString("prepayid");
            if(TextUtils.isEmpty(prepayid) || "null".equals(prepayid)) {
                map.put("prepayid", "");
            }else {
                map.put("prepayid", prepayid);
            }

            String appid = object.optString("appid");
            if(TextUtils.isEmpty(appid) || "null".equals(appid)) {
                map.put("appid", "");
            }else {
                map.put("appid", appid);
            }

            String partnerid = object.optString("partnerid");
            if(TextUtils.isEmpty(partnerid) || "null".equals(partnerid)) {
                map.put("partnerid", "");
            }else {
                map.put("partnerid", partnerid);
            }

            String noncestr = object.optString("noncestr");
            if(TextUtils.isEmpty(noncestr) || "null".equals(noncestr)) {
                map.put("noncestr", "");
            }else {
                map.put("noncestr", noncestr);
            }

            String timestamp = object.optString("timestamp");
            if(TextUtils.isEmpty(timestamp) || "timestamp".equals(sign)) {
                map.put("timestamp", "");
            }else {
                map.put("timestamp", timestamp);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }
}
