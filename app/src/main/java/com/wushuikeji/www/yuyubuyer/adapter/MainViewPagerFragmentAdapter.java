package com.wushuikeji.www.yuyubuyer.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.wushuikeji.www.yuyubuyer.factory.FragmentFactory;

/**
 * @author Jack_chentao
 * @time 2016/9/26 0026 下午 8:10.
 * @des ${TODO}
 */
public class MainViewPagerFragmentAdapter extends FragmentPagerAdapter {

    private int mTabCount;
    private String cityName;
    private String businessName;
    private String area_id;


    public MainViewPagerFragmentAdapter(FragmentManager fm,int mTabCount,String cityName,String businessName,String area_id) {
        super(fm);
        this.mTabCount = mTabCount;
        this.cityName = cityName;
        this.businessName = businessName;
        this.area_id = area_id;
    }

    @Override
    public Fragment getItem(int position) {
        if(mTabCount > 0) {
            Fragment fragment = FragmentFactory.getFragment(position,cityName,businessName,area_id);
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
