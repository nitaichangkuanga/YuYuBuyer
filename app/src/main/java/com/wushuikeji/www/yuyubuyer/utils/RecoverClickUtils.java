package com.wushuikeji.www.yuyubuyer.utils;

import android.widget.EditText;

import com.wushuikeji.www.yuyubuyer.view.ClearEditText;

/**
 * @author Jack_chentao
 * @time 2016/10/12 0012 下午 4:41.
 * @des ${TODO}
 */
public class RecoverClickUtils {
    public static void recoverClickUtils(ClearEditText clearEditText) {
        //让EditText恢复编辑状态
        clearEditText.setFocusable(true);
        clearEditText.setFocusableInTouchMode(true);
        clearEditText.requestFocus();
        clearEditText.requestFocusFromTouch();
    }

    public static void recoverClick(EditText editText) {
        //让EditText恢复编辑状态
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        editText.requestFocusFromTouch();
    }
}
