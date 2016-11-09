package com.wushuikeji.www.yuyubuyer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import com.wushuikeji.www.yuyubuyer.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FriendInfoChatDetails extends AppCompatActivity {

    @InjectView(R.id.rl_chatDetails_back)
    RelativeLayout rl_chatDetails_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_info_chat_details);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        ButterKnife.inject(this);
    }
    private void initData() {
    }
    private void initEvent() {
    }
}
