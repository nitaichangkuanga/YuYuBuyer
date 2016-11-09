package com.wushuikeji.www.yuyubuyer.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import com.wushuikeji.www.yuyubuyer.base.BaseApplication;

public class UIUtils {
	/**得到上下文*/
	public static Context getContext() {
		return BaseApplication.getContext();
	}

	/**得到Resouces对象*/
	public static Resources getResources() {
		return getContext().getResources();
	}

	/**得到String.xml中的字符串*/
	public static String getString(int resId) {
		return getResources().getString(resId);
	}

	/**得到String.xml中的字符串数组*/
	public static String[] getStringArr(int resId) {
		return getResources().getStringArray(resId);
	}

	/**得到colors.xml中的颜色*/
	public static int getColor(int colorId) {
		return getResources().getColor(colorId);
	}

	/**得到应用程序的包名*/
	public static String getPackageName() {
		return getContext().getPackageName();
	}

	/**得到主线程id*/
	public static long getMainThreadid() {
		return BaseApplication.getMainTreadId();
	}

	/**得到主线程Handler*/
	public static Handler getMainThreadHandler() {
		return BaseApplication.getHandler();
	}

	/**安全的执行一个任务*/
	public static void postTaskSafely(Runnable task) {
		int curThreadId = android.os.Process.myTid();

		if (curThreadId == getMainThreadid()) {// 如果当前线程是主线程
			task.run();
		} else {// 如果当前线程不是主线程
			getMainThreadHandler().post(task);
		}

	}

}
