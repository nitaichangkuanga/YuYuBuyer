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
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.jsonparse.BankInfoDetailsParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.SendAuthoCodeParse;
import com.wushuikeji.www.yuyubuyer.utils.BackListenerUtils;
import com.wushuikeji.www.yuyubuyer.utils.ISNetworkConnectUtils;
import com.wushuikeji.www.yuyubuyer.utils.RequestResponseUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToastUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

public class BankCardDetailsActivity extends AppCompatActivity {

    @InjectView(R.id.rl_bankDetails_back)
    RelativeLayout rl_bankDetails_back;

    @InjectView(R.id.b_bankDetails_relieve)
    Button b_bankDetails_relieve;

    @InjectView(R.id.pb_bankDetails_loading)
    ProgressBar pb_bankDetails_loading;

    @InjectView(R.id.bankDetails_bankType)
    TextView bankDetails_bankType;

    @InjectView(R.id.bankDetails_bankNum)
    TextView bankDetails_bankNum;

    @InjectView(R.id.bankDetails_realName)
    TextView bankDetails_realName;

    @InjectView(R.id.bankDetails_realIdCard)
    TextView bankDetails_realIdCard;

    //获取银行卡的列表的地址
    private String bankDetailsUrl = Constants.commontUrl + "bank/details";

    //请求解除bank的绑定
    private String relieveBankUrl = Constants.commontUrl + "bank/delete";
    private String mBankId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card_details);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        ButterKnife.inject(this);
        mBankId = getIntent().getStringExtra("bankId");
    }

    private void initData() {
        if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
            return;
        } else {
            //查询银行卡的详情列表
            loadNetworkBankDetails();
        }
    }

    private void initEvent() {
        BackListenerUtils.backFinish(rl_bankDetails_back, this);
        //解除绑定
        b_bankDetails_relieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                    return;
                } else {
                    //请求解除绑定银行卡
                    requestRelieveBindBank();
                }
            }
        });
    }

    /**
     * 请求解除银行卡的绑定
     */
    private void requestRelieveBindBank() {

        pb_bankDetails_loading.setVisibility(View.VISIBLE);

        OkHttpUtils.post().url(relieveBankUrl).addParams("bank_id", mBankId)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                pb_bankDetails_loading.setVisibility(View.GONE);
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
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), "解除绑定成功");
                        //关闭自己和刷新绑定银行卡列表
                        BankCardDetailsActivity.this.finish();
                        BindBankCardActivity.mBindBankCardActivity.refreshBankInfoData();
                    } else if (responseContent != null) {
                        //给出错误提示
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                    }
                    pb_bankDetails_loading.setVisibility(View.GONE);
                } catch (Exception e) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                    pb_bankDetails_loading.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 查询bank详情
     */
    private void loadNetworkBankDetails() {

        pb_bankDetails_loading.setVisibility(View.VISIBLE);

        OkHttpUtils.post().url(bankDetailsUrl).addParams("bank_id", mBankId)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                pb_bankDetails_loading.setVisibility(View.GONE);
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
                        Map<String, String> bankDetailsMap = BankInfoDetailsParse.bankInfoDetailsParse(response);
                        setBankDetailsParseData(bankDetailsMap);
                    } else if (responseContent != null) {
                        //给出错误提示
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                    }
                    pb_bankDetails_loading.setVisibility(View.GONE);
                } catch (Exception e) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                    pb_bankDetails_loading.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 设置数据
     *
     * @param bankDetailsMap
     */
    private void setBankDetailsParseData(Map<String, String> bankDetailsMap) {
        bankDetails_bankType.setText(bankDetailsMap.get("bank_name"));
        bankDetails_bankNum.setText(bankDetailsMap.get("footerNum"));
        bankDetails_realName.setText(bankDetailsMap.get("real_name"));
        String idCard = bankDetailsMap.get("ID_number");
        if (!TextUtils.isEmpty(idCard)) {
            //前3位后4位显示，中间11位显示*
            //切割前3位
            String beforeIdCrad = idCard.substring(0, 3);
            //切割后4位
            String afterIdCrad = idCard.substring(idCard.length() - 4, idCard.length());
            bankDetails_realIdCard.setText(beforeIdCrad + "***********" + afterIdCrad);
        }
    }
}
