package com.wushuikeji.www.yuyubuyer.utils;

import android.graphics.Color;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Jack_chentao
 * @time 2016/10/6 0006 下午 12:00.
 * @des ${TODO}
 */
public class ChangeTextColorUtils {

    /**
     * 改变shopEvaluate颜色
     */
    public static void changeShopEvaluateColor(TextView tv1, ImageView iv1,TextView tv2, ImageView iv2,TextView tv3, ImageView iv3) {
        tv1.setTextColor(Color.parseColor("#B40808"));
        iv1.setBackgroundColor(Color.parseColor("#B40808"));

        tv2.setTextColor(Color.parseColor("#B9B9B9"));
        iv2.setBackgroundColor(Color.parseColor("#B9B9B9"));
        if(tv3 != null && iv3 != null) {
            tv3.setTextColor(Color.parseColor("#B9B9B9"));
            iv3.setBackgroundColor(Color.parseColor("#B9B9B9"));
        }
    }

    /**
     * 改变BUYEREvaluate的颜色
     */
    public static void changeBuyerEvaluateColor(TextView tv1, ImageView iv1,TextView tv2, ImageView iv2,TextView tv3, ImageView iv3) {
        tv1.setTextColor(Color.parseColor("#B9B9B9"));
        iv1.setBackgroundColor(Color.parseColor("#B9B9B9"));

        tv2.setTextColor(Color.parseColor("#B40808"));
        iv2.setBackgroundColor(Color.parseColor("#B40808"));

        if(tv3 != null && iv3 != null) {
            tv3.setTextColor(Color.parseColor("#B9B9B9"));
            iv3.setBackgroundColor(Color.parseColor("#B9B9B9"));
        }
    }

    /**
     * 改变OtherEvaluate的颜色
     */
    public static void changeOtherEvaluateColor(TextView tv1, ImageView iv1,TextView tv2, ImageView iv2,TextView tv3, ImageView iv3) {
        tv1.setTextColor(Color.parseColor("#B9B9B9"));
        iv1.setBackgroundColor(Color.parseColor("#B9B9B9"));

        tv2.setTextColor(Color.parseColor("#B9B9B9"));
        iv2.setBackgroundColor(Color.parseColor("#B9B9B9"));

        if(tv3 != null && iv3 != null) {
            tv3.setTextColor(Color.parseColor("#B40808"));
            iv3.setBackgroundColor(Color.parseColor("#B40808"));
        }
    }
}
