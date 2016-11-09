package com.wushuikeji.www.yuyubuyer.utils;

import android.app.Activity;
import android.content.Intent;

import com.wushuikeji.www.yuyubuyer.constants.MyConstants;

/**
 * Created by Administrator on 2016/5/18 0018.
 * 进入下一个Activity
 */
public class ToNextActivityUtils {
    //关闭Activity
    public static void toNextActivity(Activity activity, Class nextActivity) {
        Intent intent = new Intent();
        intent.setClass(activity, nextActivity);
        activity.startActivity(intent);
        activity.finish();
    }

    //不关闭
    public static void toNextAndNoFinishActivity(Activity activity, Class nextActivity) {
        Intent intent = new Intent();
        intent.setClass(activity, nextActivity);
        activity.startActivity(intent);
    }
        //返回结果的并且不关闭的方法
    public static void getResultAndNoFinishActivity(Activity activity, Class nextActivity) {
        Intent intent = new Intent();
        intent.setClass(activity, nextActivity);
        activity.startActivityForResult(intent, MyConstants.REQUESTCODE);
    }
    //带一个int参数传递,并且关闭
    public static void toNextActivityAndParameters(Activity activity, Class nextActivity,int index) {
        Intent intent = new Intent();
        intent.setClass(activity, nextActivity);
        intent.putExtra("fragmentIndex",index);
        activity.startActivity(intent);
        activity.finish();
    }

    //带一个String参数传递
    public static void toNextActivityNotFinishAndParameters(Activity activity, Class nextActivity,String key,String value) {
        Intent intent = new Intent();
        intent.setClass(activity, nextActivity);
        intent.putExtra(key,value);
        activity.startActivity(intent);
    }

    //带两个String参数传递
    public static void toNextActivityNotFinishAndTwoParameters(Activity activity, Class nextActivity,String keyOne,String valueOne,String keyTwo,String valueTwo) {
        Intent intent = new Intent();
        intent.setClass(activity, nextActivity);
        intent.putExtra(keyOne,valueOne);
        intent.putExtra(keyTwo,valueTwo);
        activity.startActivity(intent);
    }
}
