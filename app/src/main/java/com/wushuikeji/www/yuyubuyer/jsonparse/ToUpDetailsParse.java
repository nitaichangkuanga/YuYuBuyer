package com.wushuikeji.www.yuyubuyer.jsonparse;

import android.text.TextUtils;

import com.wushuikeji.www.yuyubuyer.utils.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jack_chentao
 */
public class ToUpDetailsParse {

    public static Map<String, String> upToDetailsParse(String jsonString) {

        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject obj = new JSONObject(jsonString);

            JSONObject info = obj.optJSONObject("content");
            if (info == null) {
                return null;
            } else {
                //order_no
                String order_no = info.optString("order_no");
                if (order_no == null || order_no.equals("") || order_no.equals("null")) {
                    map.put("order_no", "");
                } else {
                    map.put("order_no", order_no);
                }

                //create_time
                String createTime = info.optString("create_time");
                if(!TextUtils.isEmpty(createTime)) {
                    String allTime = DateUtils.getDateToString(Long.parseLong(createTime));
                    if(!TextUtils.isEmpty(createTime)) {
                        map.put("create_time", allTime);
                    }else {
                        map.put("create_time", "");
                    }
                }else {
                    map.put("create_time", "");
                }

                //pay_no
                String pay_no = info.optString("pay_no");
                if (pay_no == null || pay_no.equals("") || pay_no.equals("null")) {
                    map.put("pay_no", "");
                } else {
                    map.put("pay_no", pay_no);
                }

                //pay_type
                String pay_type = info.optString("pay_type");
                if (pay_type == null || pay_type.equals("") || pay_type.equals("null")) {
                    map.put("pay_type", "");
                } else {
                    if("1".equals(pay_type)) {
                        map.put("pay_type", "微信");
                    }else if("2".equals(pay_type)) {
                        map.put("pay_type", "支付宝");
                    }
                }

                //status
                String status = info.optString("status");
                if (status == null || status.equals("") || status.equals("null")) {
                    map.put("status", "");
                } else {
                    if("1".equals(status)) {
                        map.put("status", "进行中");
                    }else if("2".equals(status)) {
                        map.put("status", "已支付");
                    }else if("3".equals(status)) {
                        map.put("status", "完成");
                    }
                }

                //count
                String count = info.optString("count");
                if (count == null || count.equals("") || count.equals("null")) {
                    map.put("count", "");
                } else {
                    map.put("count", "￥"+count);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }
}
