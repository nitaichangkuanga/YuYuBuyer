package com.wushuikeji.www.yuyubuyer.jsonparse;

import com.wushuikeji.www.yuyubuyer.bean.jsonbean.ImageViewBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jack_chentao
 * @time 2016/10/13 0013 下午 1:56.
 * @des 解析图片
 */
public class ProductDetailsImageViewParse {

    public static List<ImageViewBean> imageViewParse(String jsonString) {

        List<ImageViewBean> list = new ArrayList<ImageViewBean>();
        try {
            JSONObject obj = new JSONObject(jsonString);
            JSONArray product_imagesArray = obj.getJSONArray("proImageInfo");
            if (product_imagesArray == null) {
                return null;
            } else {
                for (int i = 0; i < product_imagesArray.length(); i++) {
                    ImageViewBean imageViewBean = new ImageViewBean();
                    JSONObject item_obj = product_imagesArray.getJSONObject(i);
                    //图片的ID
                    String id = item_obj.optString("id");
                    if (id ==null || id.equals("")) {
                        imageViewBean.id = "";
                    }else {
                        imageViewBean.id = id;
                    }
                    //图片的url
                    String url = item_obj.optString("url");
                    if (url ==null || url.equals("")) {
                        imageViewBean.imageViewUrl = "";
                    }else {
                        imageViewBean.imageViewUrl = url;
                    }
                    list.add(imageViewBean);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
