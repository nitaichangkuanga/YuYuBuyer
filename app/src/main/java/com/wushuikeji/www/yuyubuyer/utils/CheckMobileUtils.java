package com.wushuikeji.www.yuyubuyer.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jack_chentao
 * @time 2016/10/13 0013 上午 10:31.
 * @des ${TODO}
 */
public class CheckMobileUtils {
    // 判断手机格式是否正确 11位
    public static boolean isMobile(String str) {
        Pattern pattern = Pattern.compile("1[0-9]{10}");
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }
}
