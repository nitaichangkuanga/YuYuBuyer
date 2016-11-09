package com.wushuikeji.www.yuyubuyer.fragment;


import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.adapter.MainOrderViewPagerFragmentAdapter;
import com.wushuikeji.www.yuyubuyer.base.BaseFragment;
import com.wushuikeji.www.yuyubuyer.utils.ChangeTextColorUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class OrdersFragment extends BaseFragment {

    @InjectView(R.id.vp_mainOrder_viewPager)
    ViewPager vp_mainOrder_viewPager;

    @InjectView(R.id.ll_mainorder_tabLinerLayout)
    LinearLayout ll_mainorder_tabLinerLayout;

    @InjectView(R.id.tv_mainorder_evaluateOne)
    TextView tv_mainorder_evaluateOne;

    @InjectView(R.id.iv_mainorder_lineOne)
    ImageView iv_mainorder_lineOne;

    @InjectView(R.id.tv_mainorder_evaluateTwo)
    TextView tv_mainorder_evaluateTwo;

    @InjectView(R.id.iv_mainorder_lineTwo)
    ImageView iv_mainorder_lineTwo;

    @InjectView(R.id.mainorder_rlOne)
    RelativeLayout mainorder_rlOne;

    @InjectView(R.id.mainorder_rlTwo)
    RelativeLayout mainorder_rlTwo;

    private View mOrderFragmentView;

    @Override
    public View initView() {
        //需要展示的View
        if(mOrderFragmentView == null) {
            mOrderFragmentView = View.inflate(UIUtils.getContext(), R.layout.main_order, null);
        }

        //注解
        ButterKnife.inject(this, mOrderFragmentView);

        return mOrderFragmentView;
    }

    @Override
    public void initData() {
        super.initData();
        //用来区分有多少个fragment和tab
        int ll_mainorder_allCounts = ll_mainorder_tabLinerLayout.getChildCount();
        MainOrderViewPagerFragmentAdapter mainOrderViewPagerFragmentAdapter = new MainOrderViewPagerFragmentAdapter(getChildFragmentManager(),ll_mainorder_allCounts);

        vp_mainOrder_viewPager.setAdapter(mainOrderViewPagerFragmentAdapter);
        //设置默认选择商铺
        switchPage(0);
        //设置默认选择商铺，颜色变化
        changeTabColor(0);
    }

    /**
     * 切换viewPager
     */
    private void switchPage(int index) {
        vp_mainOrder_viewPager.setCurrentItem(index);//设置viewpager显示页面
    }

    /**
     * 统一改变颜色的方法
     */
    private void changeTabColor(int position) {
        switch (position) {
            case 0:
                //改变颜色
                ChangeTextColorUtils.changeShopEvaluateColor(tv_mainorder_evaluateOne,iv_mainorder_lineOne,tv_mainorder_evaluateTwo,iv_mainorder_lineTwo,null,null);
                break;
            case 1:
                ChangeTextColorUtils.changeBuyerEvaluateColor(tv_mainorder_evaluateOne,iv_mainorder_lineOne,tv_mainorder_evaluateTwo,iv_mainorder_lineTwo,null,null);
                break;
            default:
                break;
        }
    }

    @Override
    public void initEvent() {
        super.initEvent();
        //ViewPager侧滑
        vp_mainOrder_viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                //让tab按钮变色
                changeTabColor(position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }


            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        //tab点击
        mainorder_rlOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //改变颜色
                changeTabColor(0);
                switchPage(0);
            }
        });

        mainorder_rlTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //改变颜色
                changeTabColor(1);
                switchPage(1);
            }
        });
    }
}
