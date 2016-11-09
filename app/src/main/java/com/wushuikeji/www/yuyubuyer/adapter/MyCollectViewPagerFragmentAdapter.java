package com.wushuikeji.www.yuyubuyer.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.wushuikeji.www.yuyubuyer.factory.MyCollectFragmentFactory;

/**
 * @author Jack_chentao
 * @time 2016/9/26 0026 下午 8:10.
 * @des ${TODO}
 */
public class MyCollectViewPagerFragmentAdapter extends FragmentPagerAdapter {

    private int mTabCount;

    public MyCollectViewPagerFragmentAdapter(FragmentManager fm, int mTabCount) {
        super(fm);
        this.mTabCount = mTabCount;
    }

    @Override
    public Fragment getItem(int position) {
        if(mTabCount > 0) {
            Fragment fragment = MyCollectFragmentFactory.getFragment(position);
            return fragment;
        }else {
            return null;
        }
    }

    @Override
    public int getCount() {
        if(mTabCount > 0) {
            return mTabCount;
        }else {
            return 0;
        }
    }
}
