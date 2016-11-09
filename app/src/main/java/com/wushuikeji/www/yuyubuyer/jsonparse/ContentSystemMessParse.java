package com.wushuikeji.www.yuyubuyer.jsonparse;

import android.text.TextUtils;

import com.wushuikeji.www.yuyubuyer.bean.SystemInfoBean;
import com.wushuikeji.www.yuyubuyer.utils.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jack_chentao
 */
public class ContentSystemMessParse {
    public static List<SystemInfoBean> systemMessParse(String jsonString) {

        List<SystemInfoBean> list = new ArrayList<SystemInfoBean>();

        try {
            JSONObject obj = new JSONObject(jsonString);

            JSONArray jsonArray = obj.optJSONArray("content");
            if (jsonArray == null) {
                return null;
            } else {
                for (int i = 0; i < jsonArray.length(); i++) {

                    SystemInfoBean mSystemInfoBean = new SystemInfoBean();

                    JSONObject item_obj = jsonArray.getJSONObject(i);

                    //content
                    String content = item_obj.optString("content");
                    if (content ==null || content.equals("") || "null".equals(content)) {
                        mSystemInfoBean.content = "";
                    }else {
                        mSystemInfoBean.content = content;
                    }

                    //create_time
                    String createTime = item_obj.optString("create_time");
                    if(!TextUtils.isEmpty(createTime)) {
                        String allTime = DateUtils.getDateToString(Long.parseLong(createTime));
                        if(!TextUtils.isEmpty(createTime)) {
                            mSystemInfoBean.dateAndTime = allTime;
                        }else {
                            mSystemInfoBean.dateAndTime = "";
                        }
                    }else {
                        mSystemInfoBean.dateAndTime = "";
                    }

                    //is_read
                    String isRead = item_obj.optString("is_read");
                    if (isRead ==null || isRead.equals("") || "null".equals(isRead)) {
                        mSystemInfoBean.isRead = "";
                    }else {
                        mSystemInfoBean.isRead = isRead;
                    }

                    //id
                    String id = item_obj.optString("id");
                    if (id ==null || id.equals("") || "null".equals(id)) {
                        mSystemInfoBean.sysMessId = "";
                    }else {
                        mSystemInfoBean.sysMessId = id;
                    }

                    list.add(mSystemInfoBean);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
