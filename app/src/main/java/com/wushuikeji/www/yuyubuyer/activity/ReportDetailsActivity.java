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
import com.wushuikeji.www.yuyubuyer.jsonparse.ReportDetailsParse;
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

public class ReportDetailsActivity extends AppCompatActivity {

    @InjectView(R.id.rl_reportDetails_back)
    RelativeLayout rl_reportDetails_back;

    @InjectView(R.id.tv_reportDetails_reportUser)
    TextView tv_reportDetails_reportUser;

    @InjectView(R.id.tv_reportDetails_reportStatus)
    TextView tv_reportDetails_reportStatus;

    @InjectView(R.id.tv_reportDetails_reportTime)
    TextView tv_reportDetails_reportTime;

    @InjectView(R.id.tv_reportDetails_reportContent)
    TextView tv_reportDetails_reportContent;

    @InjectView(R.id.tv_reportDetails_resultTime)
    TextView tv_reportDetails_resultTime;

    @InjectView(R.id.tv_reportDetails_result)
    TextView tv_reportDetails_result;

    //订单详情
    @InjectView(R.id.rl_reportDetails_Six)
    RelativeLayout rl_reportDetails_Six;

    @InjectView(R.id.pb_reportDetails_ProgressBar)
    ProgressBar pb_reportDetails_ProgressBar;

    private String mReportId;

    //举报详情的地址
    private String reportDetailsUrl = Constants.commontUrl + "appeal/report_details";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_details);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        ButterKnife.inject(this);
        mReportId = getIntent().getStringExtra("reportId");
    }

    private void initData() {
        if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
            return;
        }else {
            loadNetworkData();
        }
    }

    private void initEvent() {
        BackListenerUtils.backFinish(rl_reportDetails_back,this);
        //订单详情
        rl_reportDetails_Six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void loadNetworkData() {
        pb_reportDetails_ProgressBar.setVisibility(View.VISIBLE);

        OkHttpUtils.post().url(reportDetailsUrl).addParams("report_id", mReportId)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                pb_reportDetails_ProgressBar.setVisibility(View.GONE);
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
                        Map<String, String> reportDetailsMap = ReportDetailsParse.reportDetailsParse(response);
                        //设置数据
                        setReportDetailsData(reportDetailsMap);
                    } else if (responseContent != null) {
                        //给出错误提示
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                    }
                    pb_reportDetails_ProgressBar.setVisibility(View.GONE);
                } catch (Exception e) {
                    pb_reportDetails_ProgressBar.setVisibility(View.GONE);
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                }
            }
        });
    }

    private void setReportDetailsData(Map<String, String> reportDetailsMap) {
        //谁举报的
        tv_reportDetails_reportUser.setText(reportDetailsMap.get("buyername"));
        //状态是什么
        String reportStatus = reportDetailsMap.get("status");
        if("1".equals(reportStatus)) {
            tv_reportDetails_reportStatus.setTextColor(Color.parseColor("#F3Ab9E"));
            tv_reportDetails_reportStatus.setText("处理中");
        }else if(("2".equals(reportStatus))){
            tv_reportDetails_reportStatus.setTextColor(Color.parseColor("#B40808"));
            tv_reportDetails_reportStatus.setText("已处理");
        }
        //举报时间
        tv_reportDetails_reportTime.setText(reportDetailsMap.get("create_time"));
        //举报理由
        tv_reportDetails_reportContent.setText(reportDetailsMap.get("content"));
        //处理时间（状态是处理中就不显示处理时间和处理结果）
        if("1".equals(reportStatus)) {
            tv_reportDetails_resultTime.setText("");
            tv_reportDetails_result.setText("");
        }else if(("2".equals(reportStatus))){
            tv_reportDetails_resultTime.setText(reportDetailsMap.get("update_time"));
            tv_reportDetails_result.setText(reportDetailsMap.get("举报成功"));
        }
    }
}
