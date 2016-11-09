package com.wushuikeji.www.yuyubuyer.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * @author Jack_chentao
 * @time 2016/9/26 0026 下午 3:02.
 * @des ${TODO}
 */
public class GuideViewPagerAdapter extends PagerAdapter{
    private List<ImageView> countsList;

    public GuideViewPagerAdapter(List<ImageView> countsList) {
        this.countsList = countsList;
    }

    @Override
    public int getCount() {
        if(countsList != null && countsList.size()>0) {
            return countsList.size();
        }else {
            return 0;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // 获取View
        ImageView child = countsList.get(position);
        // 添加View
        container.addView(child);
        return child;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);// 从Viewpager中移除
    }
}
