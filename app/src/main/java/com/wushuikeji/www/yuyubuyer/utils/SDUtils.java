package com.wushuikeji.www.yuyubuyer.utils;

/**
 * @author Jack_chentao
 * @time 2016/10/7 0007 下午 5:47.
 * @des ${TODO}
 */
public class SDUtils {
    /**
     * 检查SD是否存在
     * @return
     */
    public static boolean isSdcardExist() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

}
