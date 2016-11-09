package com.wushuikeji.www.yuyubuyer.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * @author Jack_chentao 切换Viewpager没有切换的时间
 */
public class ViewPagerWhichView extends ViewPager{

    public ViewPagerWhichView(Context context) {
        super(context);
    }

    public ViewPagerWhichView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item, false);
    }
}
