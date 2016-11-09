package com.wushuikeji.www.yuyubuyer.jsonparse;

import android.text.TextUtils;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.bean.AllEvaluateBean;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.utils.DateUtils;
import com.wushuikeji.www.yuyubuyer.utils.SpUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;

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
public class AllEvaluateParse {
    public static List<AllEvaluateBean> evaluateParse(String jsonString,boolean isDisplayShopImg) {

        List<AllEvaluateBean> list = new ArrayList<AllEvaluateBean>();

        try {
            JSONObject obj = new JSONObject(jsonString);

            JSONArray jsonArray = obj.optJSONArray("content");
            if (jsonArray == null) {
                return null;
            } else {
                for (int i = 0; i < jsonArray.length(); i++) {

                    AllEvaluateBean mAllEvaluateBean = new AllEvaluateBean();

                    JSONObject item_obj = jsonArray.getJSONObject(i);
                    //商铺的ID
                    String shopId = item_obj.optString("shop_id");
                    if (shopId ==null || shopId.equals("")) {
                        mAllEvaluateBean.shopId = "";
                    }else {
                        mAllEvaluateBean.shopId = shopId;
                    }

                    //商铺的名字
                    String shop_name = item_obj.optString("shop_name");
                    if (shop_name ==null || shop_name.equals("")) {
                        mAllEvaluateBean.shopName = "";
                    }else {
                        mAllEvaluateBean.shopName = shop_name;
                    }

                    //星的个数
                    String score = item_obj.optString("score");
                    if (score ==null || score.equals("")) {
                        mAllEvaluateBean.count = 0;
                    }else {
                        mAllEvaluateBean.count = Integer.parseInt(score);
                    }

                    //评价的ID
                    String appraisal_id = item_obj.optString("appraisal_id");
                    if (appraisal_id ==null || appraisal_id.equals("")) {
                        mAllEvaluateBean.appraisal_id = "";
                    }else {
                        mAllEvaluateBean.appraisal_id = appraisal_id;
                    }

                    //评价的内容
                    String content = item_obj.optString("content");
                    if (content ==null || content.equals("")) {
                        mAllEvaluateBean.evaluateContent = "";
                    }else {
                        mAllEvaluateBean.evaluateContent = content;
                    }

                    //用户的别名
                    String mUserName = SpUtils.getCacheString(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.USERNAME, "");
                    if (TextUtils.isEmpty(mUserName)) {
                        mAllEvaluateBean.userName = "";
                    }else {
                        mAllEvaluateBean.userName = mUserName;
                    }

                    if(isDisplayShopImg) {
                        //显示的商铺图片
                        mAllEvaluateBean.type = String.valueOf(R.mipmap.shop_shop);
                    }else {
                        mAllEvaluateBean.type = "";
                    }


                    //评价的时间戳
                    String createTime = item_obj.optString("createTime");
                    String allTime = DateUtils.getDateToString(Long.parseLong(createTime));
                    //切割 2016-10-19 18:00:00
                    if(!TextUtils.isEmpty(allTime)) {
                        String[] splitArray = allTime.split(" ");
                        mAllEvaluateBean.date = splitArray[0];
                        mAllEvaluateBean.time = splitArray[1];
                    }else {
                        mAllEvaluateBean.date = "";
                        mAllEvaluateBean.time = "";
                    }

                    list.add(mAllEvaluateBean);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
