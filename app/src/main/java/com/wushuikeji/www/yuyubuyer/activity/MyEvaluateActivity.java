package com.wushuikeji.www.yuyubuyer.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.adapter.EvaluateViewPagerFragmentAdapter;
import com.wushuikeji.www.yuyubuyer.utils.BackListenerUtils;
import com.wushuikeji.www.yuyubuyer.utils.ChangeTextColorUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MyEvaluateActivity extends FragmentActivity {

    @InjectView(R.id.rl_myEvaluate_back)
    RelativeLayout rl_myEvaluate_back;

    @InjectView(R.id.vp_myEvaluate_viewPager)
    ViewPager vp_myEvaluate_viewPager;

    @InjectView(R.id.ll_myEvaluate_tabLinerLayout)
    LinearLayout ll_myEvaluate_tabLinerLayout;

    @InjectView(R.id.tv_myEvaluate_evaluateOne)
    TextView tv_myEvaluate_evaluateOne;

    @InjectView(R.id.iv_myEvaluate_lineOne)
    ImageView iv_myEvaluate_lineOne;

    @InjectView(R.id.tv_myEvaluate_evaluateTwo)
    TextView tv_myEvaluate_evaluateTwo;

    @InjectView(R.id.iv_myEvaluate_lineTwo)
    ImageView iv_myEvaluate_lineTwo;

    @InjectView(R.id.tv_myEvaluate_evaluateThree)
    TextView tv_myEvaluate_evaluateThree;

    @InjectView(R.id.iv_myEvaluate_lineThree)
    ImageView iv_myEvaluate_lineThree;

    @InjectView(R.id.rlOne)
    RelativeLayout rlOne;

    @InjectView(R.id.rlTwo)
    RelativeLayout rlTwo;

    @InjectView(R.id.rlThree)
    RelativeLayout rlThree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_evaluate);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        ButterKnife.inject(this);
    }

    private void initData() {
        //用来区分有多少个fragment和tab
        int ll_myEvaluate_allCounts = ll_myEvaluate_tabLinerLayout.getChildCount();
        EvaluateViewPagerFragmentAdapter evaluateViewPagerFragmentAdapter = new EvaluateViewPagerFragmentAdapter(getSupportFragmentManager(),ll_myEvaluate_allCounts);

        vp_myEvaluate_viewPager.setAdapter(evaluateViewPagerFragmentAdapter);
        //设置默认选择商铺
        switchPage(0);
        //设置默认选择商铺，颜色变化
        changeTabColor(0);
    }

    /**
     * 切换viewPager
     */
    private void switchPage(int index) {
        vp_myEvaluate_viewPager.setCurrentItem(index);//设置viewpager显示页面
    }

    /**
     * 统一改变颜色的方法
     */

    private void changeTabColor(int position) {
        switch (position) {
            case 0:
                //改变颜色
                ChangeTextColorUtils.changeShopEvaluateColor(tv_myEvaluate_evaluateOne,iv_myEvaluate_lineOne,tv_myEvaluate_evaluateTwo,iv_myEvaluate_lineTwo,tv_myEvaluate_evaluateThree,iv_myEvaluate_lineThree);
                break;
            case 1:
                ChangeTextColorUtils.changeBuyerEvaluateColor(tv_myEvaluate_evaluateOne,iv_myEvaluate_lineOne,tv_myEvaluate_evaluateTwo,iv_myEvaluate_lineTwo,tv_myEvaluate_evaluateThree,iv_myEvaluate_lineThree);
                break;
            case 2:
                ChangeTextColorUtils.changeOtherEvaluateColor(tv_myEvaluate_evaluateOne,iv_myEvaluate_lineOne,tv_myEvaluate_evaluateTwo,iv_myEvaluate_lineTwo,tv_myEvaluate_evaluateThree,iv_myEvaluate_lineThree);
                break;
            default:
                break;
        }
    }

    private void initEvent() {
        //返回
        BackListenerUtils.backFinish(rl_myEvaluate_back,this);
        //ViewPager侧滑
        vp_myEvaluate_viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

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
        rlOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //改变颜色
                changeTabColor(0);
                switchPage(0);
            }
        });

        rlTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //改变颜色
                changeTabColor(1);
                switchPage(1);
            }
        });

        rlThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //改变颜色
                changeTabColor(2);
                switchPage(2);
            }
        });
    }
}
