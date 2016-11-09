package com.wushuikeji.www.yuyubuyer.utils;

import android.os.Handler;
import android.support.v4.view.ViewPager;

/**
 * @author Jack_chentao
 * @time 2016/10/10 0010 下午 12:02.
 * @des ${TODO}
 */
public class InfinitePlayUtils extends Handler implements Runnable{

    private ViewPager mViewPager;

    public InfinitePlayUtils(ViewPager mViewPager) {
        this.mViewPager = mViewPager;
    }

    //实现Viewpager的无限播放
    public void stopPlay() {
        removeCallbacksAndMessages(null);
    }

    public void startPlay() {
        stopPlay();
        postDelayed(this,10000);
    }

    @Override
    public void run() {
        //控制轮播图的显示
        mViewPager.setCurrentItem((mViewPager.getCurrentItem() + 1) % mViewPager.getAdapter().getCount());
        postDelayed(this,10000);
    }
}
