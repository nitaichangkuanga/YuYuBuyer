package com.wushuikeji.www.yuyubuyer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.adapter.MyCollectViewPagerFragmentAdapter;
import com.wushuikeji.www.yuyubuyer.utils.BackListenerUtils;
import com.wushuikeji.www.yuyubuyer.utils.ChangeTextColorUtils;
import com.wushuikeji.www.yuyubuyer.view.NoScrollViewPager;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MyCollectActivity extends AppCompatActivity {
    //返回
    @InjectView(R.id.rl_myCollectActivity_back)
    RelativeLayout rl_myCollectActivity_back;

    @InjectView(R.id.vp_myCollectActivity_viewPager)
    NoScrollViewPager vp_myCollectActivity_viewPager;
    //装tab的
    @InjectView(R.id.ll_myCollectActivity_tabLinerLayout)
    LinearLayout ll_myCollectActivity_tabLinerLayout;

    @InjectView(R.id.tv_myCollectActivity_evaluateOne)
    TextView tv_myCollectActivity_evaluateOne;

    @InjectView(R.id.iv_myCollectActivity_lineOne)
    ImageView iv_myCollectActivity_lineOne;

    @InjectView(R.id.tv_myCollectActivity_evaluateTwo)
    TextView tv_myCollectActivity_evaluateTwo;

    @InjectView(R.id.iv_myCollectActivity_lineTwo)
    ImageView iv_myCollectActivity_lineTwo;

    @InjectView(R.id.myCollectActivity_rlOne)
    RelativeLayout myCollectActivity_rlOne;

    @InjectView(R.id.myCollectActivity_rlTwo)
    RelativeLayout myCollectActivity_rlTwo;

    private int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collect);
        initView();
        initData();
        initEvent();
    }
    private void initView() {
        ButterKnife.inject(this);
    }

    private void initData() {
        //用来区分有多少个fragment和tab
        int mytabCount = ll_myCollectActivity_tabLinerLayout.getChildCount();
        MyCollectViewPagerFragmentAdapter myCollectViewPagerFragmentAdapter = new MyCollectViewPagerFragmentAdapter(getSupportFragmentManager(),mytabCount);

        vp_myCollectActivity_viewPager.setAdapter(myCollectViewPagerFragmentAdapter);
        //设置默认选择商铺
        switchPage(0);
        //设置默认选择商铺，颜色变化
        changeTabColor(0);
    }

    /**
     * 切换viewPager
     */
    private void switchPage(int index) {
        vp_myCollectActivity_viewPager.setCurrentItem(index);//设置viewpager显示页面
    }

    /**
     * 统一改变颜色的方法
     */

    private void changeTabColor(int position) {
        switch (position) {
            case 0:
                //改变颜色
                ChangeTextColorUtils.changeShopEvaluateColor(tv_myCollectActivity_evaluateOne,iv_myCollectActivity_lineOne,tv_myCollectActivity_evaluateTwo,iv_myCollectActivity_lineTwo,null,null);
                break;
            case 1:
                ChangeTextColorUtils.changeBuyerEvaluateColor(tv_myCollectActivity_evaluateOne,iv_myCollectActivity_lineOne,tv_myCollectActivity_evaluateTwo,iv_myCollectActivity_lineTwo,null,null);
                break;
            default:
                break;
        }
    }
    private void initEvent() {
        //返回
        BackListenerUtils.backFinish(rl_myCollectActivity_back,this);

        //tab点击
        myCollectActivity_rlOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //改变颜色
                changeTabColor(0);
                switchPage(0);
            }
        });

        myCollectActivity_rlTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //改变颜色
                changeTabColor(1);
                switchPage(1);
            }
        });
    }
}
