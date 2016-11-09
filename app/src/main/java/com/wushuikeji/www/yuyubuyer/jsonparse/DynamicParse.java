package com.wushuikeji.www.yuyubuyer.jsonparse;

import android.text.TextUtils;

import com.wushuikeji.www.yuyubuyer.bean.jsonbean.DynamicBean;
import com.wushuikeji.www.yuyubuyer.utils.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jack_chentao
 */
public class DynamicParse {
    public static List<DynamicBean> dynamicParse(String jsonString) {

        List<DynamicBean> list = new ArrayList<DynamicBean>();

        try {
            JSONObject obj = new JSONObject(jsonString);

            JSONArray jsonArray = obj.optJSONArray("content");
            if (jsonArray == null) {
                return null;
            } else {
                for (int i = 0; i < jsonArray.length(); i++) {

                    DynamicBean mDynamicBean = new DynamicBean();

                    JSONObject item_obj = jsonArray.getJSONObject(i);
                    //is_read
                    String is_read = item_obj.optString("is_read");
                    if (is_read ==null || is_read.equals("") || "null".equals(is_read)) {
                        mDynamicBean.is_read = "";
                    }else {
                        mDynamicBean.is_read = is_read;
                    }
                    //headImg
                    String headImg = item_obj.optString("headImg");
                    if (headImg ==null || headImg.equals("") || "null".equals(headImg)) {
                        mDynamicBean.imgUrl = "";
                    }else {
                        mDynamicBean.imgUrl = headImg;
                    }

                    //videoUrl
                    String videoUrl = item_obj.optString("videoUrl");
                    if (videoUrl ==null || videoUrl.equals("") || "null".equals(videoUrl)) {
                        mDynamicBean.videoUrl = "";
                    }else {
                        mDynamicBean.videoUrl = videoUrl;
                    }

                    //userName
                    String userName = item_obj.optString("username");
                    if (userName ==null || userName.equals("") || "null".equals(userName)) {
                        mDynamicBean.userName = "";
                    }else {
                        mDynamicBean.userName = userName;
                    }

                    //content
                    String content = item_obj.optString("content");
                    if (content ==null || content.equals("") || "null".equals(content)) {
                        mDynamicBean.content = "";
                    }else {
                        mDynamicBean.content = content;
                    }

                    //trendId
                    String trendId = item_obj.optString("trendId");
                    if (trendId ==null || trendId.equals("") || "null".equals(trendId)) {
                        mDynamicBean.trendId = "";
                    }else {
                        mDynamicBean.trendId = trendId;
                    }

                    //发表动态的时间戳
                    String createTime = item_obj.optString("createTime");
                    String allTime = DateUtils.getDateToStringTwo(Long.parseLong(createTime));
                    //切割 2016-10-19 18:00:00
                    if(!TextUtils.isEmpty(allTime)) {
                        String[] splitArray = allTime.split(" ");
                        mDynamicBean.date = splitArray[0];
                        mDynamicBean.time = splitArray[1];
                    }else {
                        mDynamicBean.date = "";
                        mDynamicBean.time = "";
                    }

                    list.add(mDynamicBean);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
