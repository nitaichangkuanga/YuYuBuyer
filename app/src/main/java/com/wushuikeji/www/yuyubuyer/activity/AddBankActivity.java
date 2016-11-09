package com.wushuikeji.www.yuyubuyer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.base.BaseApplication;
import com.wushuikeji.www.yuyubuyer.utils.BackListenerUtils;
import com.wushuikeji.www.yuyubuyer.utils.CloseInputUtils;
import com.wushuikeji.www.yuyubuyer.utils.ISNetworkConnectUtils;
import com.wushuikeji.www.yuyubuyer.utils.RecoverClickUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToNextActivityUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToastUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;
import com.wushuikeji.www.yuyubuyer.view.ClearEditText;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AddBankActivity extends AppCompatActivity {

    @InjectView(R.id.rl_addBank_back)
    RelativeLayout rl_addBank_back;

    @InjectView(R.id.b_addBank_submit)
    Button b_addBank_submit;

    @InjectView(R.id.ct_addBank_userName)
    ClearEditText ct_addBank_userName;

    @InjectView(R.id.ct_addBank_cardNumber)
    ClearEditText ct_addBank_cardNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        ButterKnife.inject(this);
    }

    private void initData() {
        String realName = getIntent().getStringExtra("realName");
        if(!TextUtils.isEmpty(realName)) {
            ct_addBank_userName.setText(realName);
            //让输入框不能输入
            ct_addBank_userName.setFocusable(false);
        }else {
            //可以输入
            RecoverClickUtils.recoverClickUtils(ct_addBank_userName);
        }
    }

    private void initEvent() {
        //返回
        BackListenerUtils.backFinish(rl_addBank_back,this);
        //提交
        b_addBank_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断卡号是否为空
                String cardNum = ct_addBank_cardNumber.getText().toString().trim();
                if (TextUtils.isEmpty(cardNum)) {
                    // 设置晃动
                    ct_addBank_cardNumber.setShakeAnimation();
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "卡号不能为空");
                    return;
                }
                //判断网络
                boolean iSNetworkConnect = ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext());
                if (!iSNetworkConnect) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                    return;
                }else {
                    //将银行卡号传递过去
                    //关闭输入法
                    CloseInputUtils.closeInput(UIUtils.getContext(), AddBankActivity.this);
                    //添加activity统一管理
                    BaseApplication.getInstance().addActivity(AddBankActivity.this);
                    ToNextActivityUtils.toNextActivityNotFinishAndParameters(AddBankActivity.this,DecideAddBankActivity.class,"cardNum",cardNum);
                }
            }
        });
    }
}
