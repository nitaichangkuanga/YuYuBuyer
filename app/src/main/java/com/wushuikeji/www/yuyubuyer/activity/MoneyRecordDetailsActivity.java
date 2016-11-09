package com.wushuikeji.www.yuyubuyer.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.jsonparse.SendAuthoCodeParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.ToUpDetailsParse;
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

public class MoneyRecordDetailsActivity extends AppCompatActivity {

    @InjectView(R.id.rl_toUpRecordDetails_back)
    RelativeLayout rl_toUpRecordDetails_back;

    @InjectView(R.id.tv_toUpRecordDetails_status)
    TextView tv_toUpRecordDetails_status;

    @InjectView(R.id.tv_toUpRecordDetails_money)
    TextView tv_toUpRecordDetails_money;

    @InjectView(R.id.tv_toup_dateAndtime)
    TextView tv_toup_dateAndtime;

    @InjectView(R.id.tv_toupp_payType)
    TextView tv_toupp_payType;

    @InjectView(R.id.tv_toup_payNum)
    TextView tv_toup_payNum;

    @InjectView(R.id.tv_toup_orderNum)
    TextView tv_toup_orderNum;

    @InjectView(R.id.pb_toupDetails_loading)
    ProgressBar pb_toupDetails_loading;

    private String mToUpRecordId;

    //充值详情的地址
    private String upToDetailsUrl = Constants.commontUrl + "recharge/details";
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_up_record_details);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        ButterKnife.inject(this);
        mToUpRecordId = getIntent().getStringExtra("toUpRecordId");

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
        BackListenerUtils.backFinish(rl_toUpRecordDetails_back,this);
    }

    private void loadNetworkData() {
        pb_toupDetails_loading.setVisibility(View.VISIBLE);

        OkHttpUtils.post().url(upToDetailsUrl).addParams("user_id", mUserId)
                .addParams("recharge_id", mToUpRecordId)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                pb_toupDetails_loading.setVisibility(View.GONE);
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
                        Map<String, String> uptoMap = ToUpDetailsParse.upToDetailsParse(response);
                        if (uptoMap != null) {
                            //设置数据
                            setUptoDetailsData(uptoMap);
                        }
                    } else if (responseContent != null) {
                        //给出错误提示
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                    }
                    pb_toupDetails_loading.setVisibility(View.GONE);
                } catch (Exception e) {
                    pb_toupDetails_loading.setVisibility(View.GONE);
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                }
            }
        });
    }

    private void setUptoDetailsData(Map<String, String> uptoMap) {
        //收入(充值的数目)
        tv_toUpRecordDetails_money.setText(uptoMap.get("count"));

        //类型
        String status = uptoMap.get("status");
        if ("进行中".equals(status)) {
            tv_toUpRecordDetails_status.setTextColor(Color.parseColor("#F3Ab9E"));
        } else {
            tv_toUpRecordDetails_status.setTextColor(Color.parseColor("#B40808"));
        }
        tv_toUpRecordDetails_status.setText(status);

        //充值时间
        tv_toup_dateAndtime.setText(uptoMap.get("create_time"));

        //充值方式
        tv_toupp_payType.setText(uptoMap.get("pay_type"));

        //支付号
        tv_toup_payNum.setText(uptoMap.get("pay_no"));

        //订单号
        tv_toup_orderNum.setText(uptoMap.get("order_no"));
    }
}
