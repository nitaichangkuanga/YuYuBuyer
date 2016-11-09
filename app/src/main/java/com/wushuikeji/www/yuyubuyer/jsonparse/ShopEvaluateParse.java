package com.wushuikeji.www.yuyubuyer.jsonparse;

import android.text.TextUtils;

import com.wushuikeji.www.yuyubuyer.bean.ShopEvaluateBean;
import com.wushuikeji.www.yuyubuyer.utils.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jack_chentao
 * @time 2016/10/18 0018 下午 7:52.
 * @des ${TODO}
 */
public class ShopEvaluateParse {
    public static List<ShopEvaluateBean> shopEvaluateParse(String jsonString) {

        List<ShopEvaluateBean> list = new ArrayList<ShopEvaluateBean>();

        try {
            JSONObject obj = new JSONObject(jsonString);

            JSONArray jsonArray = obj.optJSONArray("content");
            if (jsonArray == null) {
                return null;
            } else {
                for (int i = 0; i < jsonArray.length(); i++) {

                    ShopEvaluateBean mShopEvaluateBean = new ShopEvaluateBean();

                    JSONObject item_obj = jsonArray.getJSONObject(i);
                    //商铺的ID
                    String shopId = item_obj.optString("shop_id");
                    if (shopId ==null || shopId.equals("")) {
                        mShopEvaluateBean.shopId = "";
                    }else {
                        mShopEvaluateBean.shopId = shopId;
                    }
                    //星的个数
                    String score = item_obj.optString("score");
                    if (score ==null || score.equals("")) {
                        mShopEvaluateBean.count = 0;
                    }else {
                        mShopEvaluateBean.count = Integer.parseInt(score);
                    }

                    //评价的ID
                    String appraisal_id = item_obj.optString("appraisal_id");
                    if (appraisal_id ==null || appraisal_id.equals("")) {
                        mShopEvaluateBean.appraisal_id = "";
                    }else {
                        mShopEvaluateBean.appraisal_id = appraisal_id;
                    }

                    //评价的内容
                    String content = item_obj.optString("content");
                    if (content ==null || content.equals("")) {
                        mShopEvaluateBean.evaluateContent = "";
                    }else {
                        mShopEvaluateBean.evaluateContent = content;
                    }

                    //谁评价的
                    String userName = item_obj.optString("username");
                    if (userName ==null || userName.equals("")) {
                        mShopEvaluateBean.userName = "";
                    }else {
                        mShopEvaluateBean.userName = userName;
                    }

                    //评价的时间戳
                    String createTime = item_obj.optString("createTime");
                    String allTime = DateUtils.getDateToString(Long.parseLong(createTime));
                    //切割 2016-10-19 18:00:00
                    if(!TextUtils.isEmpty(allTime)) {
                        String[] splitArray = allTime.split(" ");
                        mShopEvaluateBean.date = splitArray[0];
                        mShopEvaluateBean.time = splitArray[1];
                    }else {
                        mShopEvaluateBean.date = "";
                        mShopEvaluateBean.time = "";
                    }

                    list.add(mShopEvaluateBean);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
