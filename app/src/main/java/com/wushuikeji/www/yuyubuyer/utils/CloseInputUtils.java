package com.wushuikeji.www.yuyubuyer.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

/**
 * @author Jack_chentao
 * @time 2016/10/12 0012 下午 1:43.
 * @des ${TODO}
 */
public class CloseInputUtils {
    public static void closeInput(Context context,Activity activity) {
        // 不显示输入法
        try {
            ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(activity
                                    .getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
    }
}
