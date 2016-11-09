package com.wushuikeji.www.yuyubuyer.utils;

import android.app.Activity;
import android.text.TextUtils;

/**
 * @author Jack_chentao
 * @time 2016/10/15 0015 下午 10:15.
 * @des ${TODO}
 */
public class CheckLocationIsSuccessUtils {
    /**
     * 通过这个方法检查定位是否成功来判断用户是否拒绝了定位的权限
     */
    public static void checkLocationIsSuccess(String longitude, String latitude, Activity activity,String packageName) {

        if (!TextUtils.isEmpty(longitude) && !TextUtils.isEmpty(latitude)) {

            //定位失败
            if("4.9E-324".equals(longitude) || "4.9E-324".equals(latitude)) {
                //弹出对话框，提示用户开启定位权限
                ShowPermissionDialogUtils.showPermissionDialogUtils(activity, "读取位置信息权限", packageName);
            }
        }
    }
}
