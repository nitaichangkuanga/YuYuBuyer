package com.wushuikeji.www.yuyubuyer.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * @author Jack_chentao
 * @time 2016/10/3 0003 上午 1:12.
 * @des ${TODO}
 */
public class ConvertUtils {
    public static int dp2px(int dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
