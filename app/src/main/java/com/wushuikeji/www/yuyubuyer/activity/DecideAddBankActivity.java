package com.wushuikeji.www.yuyubuyer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.base.BaseApplication;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.jsonparse.ContentParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.SendAuthoCodeParse;
import com.wushuikeji.www.yuyubuyer.utils.BackListenerUtils;
import com.wushuikeji.www.yuyubuyer.utils.RequestResponseUtils;
import com.wushuikeji.www.yuyubuyer.utils.SpUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToastUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

public class DecideAddBankActivity extends AppCompatActivity {

    @InjectView(R.id.rl_decidedAddBank_back)
    RelativeLayout rl_decidedAddBank_back;

    @InjectView(R.id.ct_decidedAddBank_card)
    TextView tv_decidedAddBank_card;

    @InjectView(R.id.ct_decidedAddBank_bankType)
    TextView tv_decidedAddBank_bankType;

    @InjectView(R.id.b_decidedAddBank_submit)
    Button b_decidedAddBank_submit;

    @InjectView(R.id.pb_decideAddBank_loading)
    ProgressBar pb_decideAddBank_loading;

    //查询卡的类型
    private String cardTypeAndBankUrl = Constants.commontUrl + "bank/bankname";
    //添加银行卡
    private String addBankUrl = Constants.commontUrl + "bank/add";
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decide_add_bank);
        initView();
        initData();
        initEvent();
    }
    private void initView() {
        ButterKnife.inject(this);
        //得到用户ID
        mUserId = SpUtils.getCacheString(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.BUYERID, "");
    }

    private void initData() {
        //显示银行卡号
        String cardNum = getIntent().getStringExtra("cardNum");
        tv_decidedAddBank_card.setText(cardNum);
        //请求获取卡号的所属银行和类型
        requestNetworkGetCardType(cardNum);
    }

    private void initEvent() {
        BackListenerUtils.backFinish(rl_decidedAddBank_back,this);
        //提交添加银行卡
        b_decidedAddBank_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String cardNum = tv_decidedAddBank_card.getText().toString().trim();
                String bankType = tv_decidedAddBank_bankType.getText().toString().trim();

                if (!TextUtils.isEmpty(cardNum) && !TextUtils.isEmpty(bankType)) {
                    pb_decideAddBank_loading.setVisibility(View.VISIBLE);

                    OkHttpUtils.post().url(addBankUrl).addParams("user_id", mUserId)
                            .addParams("card_number", cardNum)
                            .addParams("bank_name", bankType)
                            .build().execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            pb_decideAddBank_loading.setVisibility(View.GONE);
                            ToastUtils.showToastInUIThread(UIUtils.getContext(),"请求超时");
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            try {
                                // 成功了，只是返回来请求的json数据,还需要解析
                                Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                                String response_code = map.get("response_code");
                                //需要判断各种响应码,获取具体错误信息
                                String responseContent = RequestResponseUtils.getResponseContent(response_code);

                                if("0".equals(response_code)) {
                                    ToastUtils.showToastInUIThread(UIUtils.getContext(),"成功添加银行卡");
                                    //关闭自己和它上一个的activity
                                    DecideAddBankActivity.this.finish();
                                    BaseApplication.getInstance().closeActivity();
                                    //刷新绑定银行卡的界面
                                    BindBankCardActivity.mBindBankCardActivity.refreshBankInfoData();
                                }else {
                                    ToastUtils.showToastInUIThread(UIUtils.getContext(),responseContent);
                                }
                                pb_decideAddBank_loading.setVisibility(View.GONE);
                            } catch (Exception e) {
                                pb_decideAddBank_loading.setVisibility(View.GONE);
                                ToastUtils.showToastInUIThread(UIUtils.getContext(),"请求超时");
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 获取卡的类型和银行
     */
    private void requestNetworkGetCardType(String cardNumber) {
        OkHttpUtils.post().url(cardTypeAndBankUrl).addParams("card_number", cardNumber)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(),"请求超时");
                    //回退
                    DecideAddBankActivity.this.finish();
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    // 成功了，只是返回来请求的json数据,还需要解析
                    Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                    String response_code = map.get("response_code");
                    //需要判断各种响应码,获取具体错误信息
                    String responseContent = RequestResponseUtils.getResponseContent(response_code);

                    if("0".equals(response_code)) {
                        //解析
                        Map<String, String> bankInfoMap = ContentParse.contentParse(response);
                        String bankInfo = bankInfoMap.get("content");//包括银行卡所属行和卡的类型
                        if(!TextUtils.isEmpty(bankInfo)) {
                            tv_decidedAddBank_bankType.setText(bankInfo);
                        }
                    }else {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(),"请检查银行卡号是否正确");
                        //回退
                        DecideAddBankActivity.this.finish();
                    }
                } catch (Exception e) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(),"请求超时");
                    //回退
                    DecideAddBankActivity.this.finish();
                }
            }
        });
    }
}
