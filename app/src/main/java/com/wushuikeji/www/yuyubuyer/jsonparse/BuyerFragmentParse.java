package com.wushuikeji.www.yuyubuyer.jsonparse;

import com.wushuikeji.www.yuyubuyer.bean.BuyerBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jack_chentao
 * @time 2016/10/13 0013 下午 1:56.
 * @des ${TODO}
 */
public class BuyerFragmentParse {
    public static List<BuyerBean> buyerFragmentParse(String jsonString) {

        List<BuyerBean> list = new ArrayList<BuyerBean>();

        try {
            JSONObject obj = new JSONObject(jsonString);

            JSONArray jsonArray = obj.optJSONArray("content");
            if (jsonArray == null) {
                return null;
            } else {
                for (int i = 0; i < jsonArray.length(); i++) {

                    BuyerBean mBuyerBean = new BuyerBean();

                    JSONObject item_obj = jsonArray.getJSONObject(i);

                    //buyer的状态
                    String user_status = item_obj.optString("user_status");
                    if (user_status ==null || user_status.equals("")) {
                        mBuyerBean.user_status = "";
                    }else {
                        mBuyerBean.user_status = user_status;
                    }
                    //buyer的sex
                    String sex = item_obj.optString("sex");
                    if (sex ==null || sex.equals("")) {
                        mBuyerBean.sex = "";
                    }else {
                        mBuyerBean.sex = sex;
                    }

                    //buyer的head_img
                    String head_img = item_obj.optString("head_img");
                    if (head_img ==null || head_img.equals("")) {
                        mBuyerBean.imgUrl = "";
                    }else {
                        mBuyerBean.imgUrl = head_img;
                    }

                    //buyer的mobile
                    String mobile = item_obj.optString("mobile");
                    if (mobile ==null || mobile.equals("")) {
                        mBuyerBean.mobile = "";
                    }else {
                        mBuyerBean.mobile = mobile;
                    }

                    //buyer的description
                    String description = item_obj.optString("description");
                    if (description ==null || description.equals("") || description.equals("null")) {
                        mBuyerBean.description = "";
                    }else {
                        mBuyerBean.description = description;
                    }

                    //buyer的grades
                    String grades = item_obj.optString("grades");
                    if (grades ==null || grades.equals("")) {
                        mBuyerBean.grades = "";
                    }else {
                        mBuyerBean.grades = grades;
                    }

                    //buyer的userId
                    String userId = item_obj.optString("userId");
                    if (userId ==null || userId.equals("")) {
                        mBuyerBean.userId = "";
                    }else {
                        mBuyerBean.userId = userId;
                    }
                    //buyer的is_video
                    String is_video = item_obj.optString("is_video");
                    if (is_video ==null || is_video.equals("")) {
                        mBuyerBean.is_video = "";
                    }else {
                        mBuyerBean.is_video = is_video;
                    }
                    //buyer的is_shopping
                    String is_shopping = item_obj.optString("is_shopping");
                    if (is_shopping ==null || is_shopping.equals("")) {
                        mBuyerBean.is_shopping = "";
                    }else {
                        mBuyerBean.is_shopping = is_shopping;
                    }
                    //buyer的juli
                    String distance = item_obj.optString("juli");
                    if (distance ==null || distance.equals("")) {
                        mBuyerBean.distance = "";
                    }else {
                        mBuyerBean.distance = distance;
                    }
                    //buyer的age
                    String age = item_obj.optString("age");
                    if (age ==null || age.equals("")) {
                        mBuyerBean.age = "";
                    }else {
                        mBuyerBean.age = age;
                    }
                    //buyer的username
                    String buyerName = item_obj.optString("username");
                    if (buyerName ==null || buyerName.equals("")) {
                        mBuyerBean.buyerName = "";
                    }else {
                        mBuyerBean.buyerName = buyerName;
                    }

                    list.add(mBuyerBean);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
