package com.wushuikeji.www.yuyubuyer.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.wushuikeji.www.yuyubuyer.constants.MyConstants;

/**
 * SharedPreference的工具类
 */
public class SpUtils {
    //存放String数据
    public static void putSpString(Context context,String key,String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MyConstants.SPNAME,Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key,value).commit();
    }
    //得到String数据
    public static String getSpString(Context context,String key,String defalValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MyConstants.SPNAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defalValue);
    }
    //存放boolean数据
    public static void putSpBoolean(Context context,String key,boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MyConstants.SPNAME,Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(key,value).commit();
    }
    //得到boolean数据
    public static boolean getSpBoolean(Context context,String key,boolean defalValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MyConstants.SPNAME,Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defalValue);
    }
    //存放int数据
    public static void putSpInt(Context context,String key,int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MyConstants.SPNAME,Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(key,value).commit();
    }
    //得到int数据
    public static int getSpInt(Context context,String key,int defalValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MyConstants.SPNAME,Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key,defalValue);
    }

    //存放float数据
    public static void putSpFloat(Context context,String key,float value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MyConstants.SPNAME,Context.MODE_PRIVATE);
        sharedPreferences.edit().putFloat(key,value).commit();
    }
    //得到float数据
    public static float getSpFloat(Context context,String key,float defalValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MyConstants.SPNAME,Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(key,defalValue);
    }

    //存放String数据（自命名）
    public static void putCacheString(String spName,Context context,String key,String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(spName,Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key,value).commit();
    }
    //得到String数据
    public static String getCacheString(String spName,Context context,String key,String defalValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(spName,Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defalValue);
    }

    //存放int数据（自命名）
    public static void putCacheInt(String spName,Context context,String key,int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(spName,Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(key,value).commit();
    }
    //得到String数据
    public static int getCacheInt(String spName,Context context,String key,int defalValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(spName,Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defalValue);
    }

    //存放boolean数据（自命名）
    public static void putCacheBoolean(String spName,Context context,String key,boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(spName,Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(key,value).commit();
    }
    //得到boolean数据
    public static boolean getCacheBoolean(String spName,Context context,String key,boolean defalValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(spName,Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defalValue);
    }

}
