package com.wushuikeji.www.yuyubuyer.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.jsonparse.SendAuthoCodeParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.SubmitDetailsParse;
import com.wushuikeji.www.yuyubuyer.utils.BackListenerUtils;
import com.wushuikeji.www.yuyubuyer.utils.ISNetworkConnectUtils;
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

public class SubmitRecordDetailsActivity extends AppCompatActivity {

    @InjectView(R.id.rl_submitRecordDetails_back)
    RelativeLayout rl_submitRecordDetails_back;

    @InjectView(R.id.tv_submit_money)
    TextView tv_submit_money;

    @InjectView(R.id.tv_submit_type)
    TextView tv_submit_type;

    @InjectView(R.id.tv_submit_dateAndtime)
    TextView tv_submit_dateAndtime;

    @InjectView(R.id.tv_submit_bank)
    TextView tv_submit_bank;

    @InjectView(R.id.tv_submit_bankCard)
    TextView tv_submit_bankCard;

    @InjectView(R.id.pb_submitDetails_loading)
    ProgressBar pb_submitDetails_loading;

    private String mSubmitRecordId;
    private String mUserId;

    //提现详情的地址
    private String submitDetailsUrl = Constants.commontUrl + "cashout/details";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_record_details);
        initView();
        initData();
        initEvent();
    }
    private void initView() {
        ButterKnife.inject(this);
        mSubmitRecordId = getIntent().getStringExtra("submitRecordId");
        //得到用户ID
        mUserId = SpUtils.getCacheString(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.BUYERID, "");
    }

    private void initData() {
        if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
            return;
        } else {
            loadNetworkData();
        }
    }

    private void initEvent() {
        BackListenerUtils.backFinish(rl_submitRecordDetails_back,this);
    }

    private void loadNetworkData() {
        pb_submitDetails_loading.setVisibility(View.VISIBLE);

        OkHttpUtils.post().url(submitDetailsUrl).addParams("user_id", mUserId)
                .addParams("cashout_id", mSubmitRecordId)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                pb_submitDetails_loading.setVisibility(View.GONE);
                ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    //先解析reponse_code
                    Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                    String response_code = map.get("response_code");
                    //需要判断各种响应码,获取具体错误信息
                    String responseContent = RequestResponseUtils.getResponseContent(response_code);
                    if ("0".equals(response_code)) {
                        //可以下一步解析
                        Map<String, String> submitMap = SubmitDetailsParse.submitDetailsParse(response);
                        if (submitMap != null) {
                            //设置数据
                            setSubmitDetailsData(submitMap);
                        }
                    } else if (responseContent != null) {
                        //给出错误提示
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                    }
                    pb_submitDetails_loading.setVisibility(View.GONE);
                } catch (Exception e) {
                    pb_submitDetails_loading.setVisibility(View.GONE);
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                }
            }
        });
    }

    private void setSubmitDetailsData(Map<String, String> submitMap) {
        //提现金额
        tv_submit_money.setText(submitMap.get("count"));

        //类型
        String status = submitMap.get("status");
        if ("审核中".equals(status)) {
            tv_submit_type.setTextColor(Color.parseColor("#F3Ab9E"));
        } else {
            tv_submit_type.setTextColor(Color.parseColor("#B40808"));
        }
        tv_submit_type.setText(status);

        //提现时间
        tv_submit_dateAndtime.setText(submitMap.get("create_time"));

        //到账银行
        tv_submit_bank.setText(submitMap.get("bank_name"));

        //到账卡号(只留前四位和后四位) 11个*
        String cardNumber = submitMap.get("card_number");
        if(!TextUtils.isEmpty(cardNumber) && cardNumber.length() > 4) {
            String beforeNum = cardNumber.substring(0, 4);
            String afterNum = cardNumber.substring(cardNumber.length() - 4, cardNumber.length());
            tv_submit_bankCard.setText(beforeNum + "***********" +afterNum);
        }else {
            tv_submit_bankCard.setText("");
        }
    }
}
