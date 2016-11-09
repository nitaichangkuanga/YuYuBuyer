package com.wushuikeji.www.yuyubuyer.fragment;


import android.view.View;
import android.widget.RelativeLayout;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.activity.DynamicListActivity;
import com.wushuikeji.www.yuyubuyer.activity.FriendInfoActivity;
import com.wushuikeji.www.yuyubuyer.activity.SystemInfoActivity;
import com.wushuikeji.www.yuyubuyer.base.BaseFragment;
import com.wushuikeji.www.yuyubuyer.utils.ToNextActivityUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NewsFragment extends BaseFragment {

    @InjectView(R.id.rl_news_friendNews)
    RelativeLayout rl_news_friendNews;

    @InjectView(R.id.rl_news_comment)
    RelativeLayout rl_news_comment;

    @InjectView(R.id.rl_news_systemInfo)
    RelativeLayout rl_news_systemInfo;

    private View mNewsFragmentView;

    @Override
    public View initView() {
        //需要展示的PersonalView
//        mNewsFragmentView = View.inflate(UIUtils.getContext(), R.layout.main_news, null);
        if (mNewsFragmentView == null) {
            mNewsFragmentView = View.inflate(UIUtils.getContext(), R.layout.main_news, null);
        }
        //注解
        ButterKnife.inject(this, mNewsFragmentView);
        return mNewsFragmentView;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {
        super.initEvent();
        //好友消息
        rl_news_friendNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToNextActivityUtils.toNextAndNoFinishActivity(getActivity(), FriendInfoActivity.class);
            }
        });
        //动态评论
        rl_news_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToNextActivityUtils.toNextAndNoFinishActivity(getActivity(), DynamicListActivity.class);
            }
        });
        //系统消息
        rl_news_systemInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToNextActivityUtils.toNextAndNoFinishActivity(getActivity(), SystemInfoActivity.class);
            }
        });
    }
}
