package com.wushuikeji.www.yuyubuyer.utils;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;

/**
 * @author Jack_chentao
 * @time 2016/10/2 0002 下午 4:35.
 * @des ${TODO}
 */
public class PtrFrameRefreshUtils {

    public static void setRefreshParams(PtrClassicFrameLayout ptr) {
        //阻尼系数：默认1.7f，越大，感觉下拉时越吃力
        ptr.setResistance(1.7f);
        //触发刷新时移动的位置比例：默认，1.2f，移动达到头部高度1.2倍时可触发刷新操作。
        ptr.setRatioOfHeaderHeightToRefresh(1.2f);
        //回弹延时：默认 200ms，回弹到刷新高度所用时间
        ptr.setDurationToClose(200);
        //头部回弹时间：默认1000ms
        ptr.setDurationToCloseHeader(1000);
        //下拉刷新 / 释放刷新，默认为释放刷新
        ptr.setPullToRefresh(false);
        //刷新时保持头部：默认值 true.
        ptr.setKeepHeaderWhenRefresh(true);
    }
}
