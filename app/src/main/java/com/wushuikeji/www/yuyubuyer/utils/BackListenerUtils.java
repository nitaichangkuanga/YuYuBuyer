package com.wushuikeji.www.yuyubuyer.utils;

import android.app.Activity;
import android.view.View;

/**
 * @author Jack_chentao
 * @time 2016/10/3 0003 下午 9:05.
 * @des ${TODO}
 */
public class BackListenerUtils {

    public static void backFinish(View view, final Activity activity) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
    }
}
