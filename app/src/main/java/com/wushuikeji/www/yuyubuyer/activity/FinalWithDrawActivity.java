package com.wushuikeji.www.yuyubuyer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.base.BaseApplication;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.jsonparse.SendAuthoCodeParse;
import com.wushuikeji.www.yuyubuyer.utils.BackListenerUtils;
import com.wushuikeji.www.yuyubuyer.utils.CloseInputUtils;
import com.wushuikeji.www.yuyubuyer.utils.ISNetworkConnectUtils;
import com.wushuikeji.www.yuyubuyer.utils.RecoverClickUtils;
import com.wushuikeji.www.yuyubuyer.utils.RequestResponseUtils;
import com.wushuikeji.www.yuyubuyer.utils.SpUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToastUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;
import com.wushuikeji.www.yuyubuyer.view.ClearEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.DecimalFormat;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

public class FinalWithDrawActivity extends AppCompatActivity {

    @InjectView(R.id.rl_finalWithdraw_back)
    RelativeLayout rl_finalWithdraw_back;

    @InjectView(R.id.tv_finalWithdraw_allMoney)
    TextView tv_finalWithdraw_allMoney;

    @InjectView(R.id.tv_finalWithdraw_afterMoney)
    TextView tv_finalWithdraw_afterMoney;

    @InjectView(R.id.ll_finalWithdraw_dao)
    LinearLayout ll_finalWithdraw_dao;

    @InjectView(R.id.ce_finalWithdraw_money)
    ClearEditText ce_finalWithdraw_money;

    @InjectView(R.id.b_finalWithdraw_up)
    Button b_finalWithdraw_up;

    @InjectView(R.id.pb_finalWithdraw_ProgressBar)
    ProgressBar pb_finalWithdraw_ProgressBar;

    @InjectView(R.id.tv_finalWithdraw_bankInfo)
    TextView tv_finalWithdraw_bankInfo;

    private String upMoneyUrl = Constants.commontUrl + "cashout/fetch";
    private String mUserId;
    private String mBankId = new String();
    private DecimalFormat mDf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_with_draw);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        ButterKnife.inject(this);
    }

    private void initData() {
        //得到用户ID
        mUserId = SpUtils.getCacheString(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.BUYERID, "");
        //所有的余额
        String allMoney = getIntent().getStringExtra("allMoney");
        tv_finalWithdraw_allMoney.setText(allMoney);
        mDf = new DecimalFormat("#.00");
    }

    private void initEvent() {
        BackListenerUtils.backFinish(rl_finalWithdraw_back,this);
        //提现
        b_finalWithdraw_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalUpMoney();
            }
        });
        //选择银行卡
        ll_finalWithdraw_dao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳到提现选择银行的界面
                Intent intent = new Intent(FinalWithDrawActivity.this,WithdrawBnakCardActivity.class);
                startActivityForResult(intent,1114);
            }
        });
        //用户选择金额，就必须算出扣除10%后的金额
        ce_finalWithdraw_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String inputMoney = ce_finalWithdraw_money.getText().toString().trim();
                String allMoney = tv_finalWithdraw_allMoney.getText().toString();
                if(TextUtils.isEmpty(inputMoney)) {
                    tv_finalWithdraw_afterMoney.setText("0.00");
                    return;
                }
                if("0".equals(inputMoney)) {
                    tv_finalWithdraw_afterMoney.setText("0.00");
                    return;
                }
                if(Double.parseDouble(inputMoney) > Double.parseDouble(allMoney)) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(),"金额不足,请重新输入");
                    return;
                }
                //扣除之后的钱
                double afterMoney = Double.parseDouble(inputMoney) - Double.parseDouble(inputMoney)*0.1;

                String format = mDf.format(afterMoney);
                tv_finalWithdraw_afterMoney.setText(format);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void finalUpMoney() {
        String money = ce_finalWithdraw_money.getText().toString().trim();
        //银行卡信息
        String bankCardInfo = tv_finalWithdraw_bankInfo.getText().toString();
        //判断金额
        if (TextUtils.isEmpty(money)) {
            // 设置晃动
            ce_finalWithdraw_money.setShakeAnimation();
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "提现金额不能空");
            return;
        }
        if ("0".equals(money)) {
            // 设置晃动
            ce_finalWithdraw_money.setShakeAnimation();
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "提现金额不能为0");
            return;
        }
        if (TextUtils.isEmpty(bankCardInfo)) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "请先选择一张银行卡");
            return;
        }
        //判断网络
        boolean iSNetworkConnect = ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext());
        if (!iSNetworkConnect) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
            return;
        } else {
            //关闭输入法
            CloseInputUtils.closeInput(UIUtils.getContext(), FinalWithDrawActivity.this);
            //加载动画
            pb_finalWithdraw_ProgressBar.setVisibility(View.VISIBLE);
            // 动画的时候，按钮和发送验证码不能点击了
            cancelClick();
            OkHttpUtils.post().url(upMoneyUrl).addParams("user_id", mUserId)
                    .addParams("bank_id", mBankId)
                    .addParams("count", money)
                    .build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    //动画关闭
                    pb_finalWithdraw_ProgressBar.setVisibility(View.GONE);
                    //恢复编辑状态
                    recoverClick();
                    ToastUtils.showToastInUIThread(UIUtils.getContext(),"请求超时");
                }

                @Override
                public void onResponse(String response, int id) {
                    try {
                        //动画关闭
                        pb_finalWithdraw_ProgressBar.setVisibility(View.GONE);
                        //恢复编辑状态
                        recoverClick();
                        // 成功了，只是返回来请求的json数据,还需要解析
                        Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                        String response_code = map.get("response_code");
                        //需要判断各种响应码,获取具体错误信息
                        String responseContent = RequestResponseUtils.getResponseContent(response_code);
                        if("0".equals(response_code)) {
                            //成功，关闭自己和提现的界面刷新余额界面
                            ToastUtils.showToastInUIThread(UIUtils.getContext(),"提现成功");
                            FinalWithDrawActivity.this.finish();
                            BaseApplication.getInstance().closeActivity();
                            MyAccountActivity.mMyAccountActivity.reSetRefreshData();
                        }else {
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                        }
                    } catch (Exception e) {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(),"请求超时");
                        //动画关闭
                        pb_finalWithdraw_ProgressBar.setVisibility(View.GONE);
                        //恢复编辑状态
                        recoverClick();
                    }
                }
            });
        }
    }
    /**
     * 动画的时候不能点击
     */
    private void cancelClick() {
        ll_finalWithdraw_dao.setClickable(false);
        b_finalWithdraw_up.setClickable(false);
        // 动画的时候，让EditText失去焦点，不能输入
        ce_finalWithdraw_money.setFocusable(false);
    }

    /**
     * 恢复默认可以点击
     */
    private void recoverClick() {
        //让EditText恢复编辑状态
        RecoverClickUtils.recoverClickUtils(ce_finalWithdraw_money);
        //其他控件可以点击
        ll_finalWithdraw_dao.setClickable(true);
        b_finalWithdraw_up.setClickable(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1114) {
            //得到返回来的银行卡信息
            if(data != null) {
                String bankInfo = data.getStringExtra("bankInfo");
                mBankId = data.getStringExtra("bankId");
                tv_finalWithdraw_bankInfo.setText(bankInfo);
            }
        }
    }
}
