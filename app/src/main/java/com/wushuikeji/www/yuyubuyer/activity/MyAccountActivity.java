package com.wushuikeji.www.yuyubuyer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.jsonparse.ContentMoneyParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.SendAuthoCodeParse;
import com.wushuikeji.www.yuyubuyer.utils.BackListenerUtils;
import com.wushuikeji.www.yuyubuyer.utils.ISNetworkConnectUtils;
import com.wushuikeji.www.yuyubuyer.utils.SpUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToNextActivityUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToastUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

public class MyAccountActivity extends AppCompatActivity {

    @InjectView(R.id.b_account_recharge)
    Button b_account_recharge;

    @InjectView(R.id.b_account_withdraw)
    Button b_account_withdraw;

    @InjectView(R.id.rl_account_back)
    RelativeLayout rl_account_back;

    @InjectView(R.id.account_money)
    TextView account_money;

    public static MyAccountActivity mMyAccountActivity;

    private String MoneyUrl = Constants.commontUrl + "user/account_balance";
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        ButterKnife.inject(this);

        mMyAccountActivity = this;
        //得到用户ID
        mUserId = SpUtils.getCacheString(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.BUYERID, "");
    }

    private void initData() {
        //获取账户的余额
        if(!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(),"请开启网络");
            MyAccountActivity.this.finish();
        }else {
            requestNetworkGetMoney();
        }
    }

    private void initEvent() {
        //返回
        BackListenerUtils.backFinish(rl_account_back,this);
        //充值
        b_account_recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToNextActivityUtils.toNextAndNoFinishActivity(MyAccountActivity.this,RechargeActivity.class);
            }
        });
        //提现
        b_account_withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //将金额总数传递下去
                ToNextActivityUtils.toNextActivityNotFinishAndParameters(MyAccountActivity.this,WithdrawActivity.class,"allMoney",account_money.getText().toString());
            }
        });
    }

    /**
     * 获取余额
     */
    private void requestNetworkGetMoney() {
        OkHttpUtils.post().url(MoneyUrl).addParams("user_id", mUserId)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtils.showToastInUIThread(UIUtils.getContext(),"请求超时");
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                    String response_code = map.get("response_code");
                    if("0".equals(response_code)) {
                        //解析金额
                        Map<String, String> moneyMap = ContentMoneyParse.contentParse(response);
                        if(moneyMap != null) {
                            account_money.setText(moneyMap.get("count"));
                        }
                    }else if("400".equals(response_code)) {
                        //从没有充值过
                        account_money.setText("0");
                    }
                } catch (Exception e) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(),"请求超时");
                }
            }
        });
    }
    /**
     * 提供外部刷新用（重新获取余额）
     */
    public void reSetRefreshData() {
        requestNetworkGetMoney();
    }
}
