package com.wushuikeji.www.yuyubuyer.utils;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.wushuikeji.www.yuyubuyer.R;

/**
 * @author Jack_chentao
 * @time 2016/10/16 0016 下午 1:48.
 * @des ${TODO}
 */
public class GetNormalOptionsUtils {

    public static DisplayImageOptions getNormalOptionsUtils() {
        // 图片缓存 默认 等
        DisplayImageOptions optionsImag = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.mipmap.zanwutupian)
                .showImageOnLoading(R.mipmap.zanwutupian)
                .showImageOnFail(R.mipmap.zanwutupian).cacheInMemory(true).cacheOnDisk(true)
                .considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        return optionsImag;
    }
}
