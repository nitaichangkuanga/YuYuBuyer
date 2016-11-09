package com.wushuikeji.www.yuyubuyer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
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

public class ReportInputTextActivity extends AppCompatActivity {

    @InjectView(R.id.rl_reportInput_back)
    RelativeLayout rl_reportInput_back;

    @InjectView(R.id.et_reportInput_et)
    EditText et_reportInput_et;

    @InjectView(R.id.b_reportInput_submit)
    Button b_reportInput_submit;

    @InjectView(R.id.pb_reportInput_ProgressBar)
    ProgressBar pb_reportInput_ProgressBar;

    //举报的url
    private String reportUrl = Constants.commontUrl + "appeal/report";

    private String mUserId;
    private String mBuyerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_input_text);
        initView();
        initEvent();
    }
    private void initView() {
        ButterKnife.inject(this);
        //得到用户ID
        mUserId = SpUtils.getCacheString(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.BUYERID, "");
        //得到buyerId
        mBuyerId = getIntent().getStringExtra("BuyerId");
    }

    private void initEvent() {
        BackListenerUtils.backFinish(rl_reportInput_back,this);
        //提交
        b_reportInput_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String content = et_reportInput_et.getText().toString().trim();

                if(TextUtils.isEmpty(content)) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(),"请输入举报理由");
                    return;
                }else {
                    //请求网络
                    requestSubmitNetwork(content);
                }
            }
        });
    }

    /**
     * 举报
     */
    private void requestSubmitNetwork(String content) {
        //进度条展示
        pb_reportInput_ProgressBar.setVisibility(View.VISIBLE);

        OkHttpUtils.post().url(reportUrl).addParams("user_id", mUserId)
                .addParams("buyer_id", mUserId)
                .addParams("content",content)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                pb_reportInput_ProgressBar.setVisibility(View.GONE);
                ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    //先解析reponse_code,给客户提醒
                    Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                    String response_code = map.get("response_code");
                    //需要判断各种响应码,获取具体错误信息
                    String responseContent = RequestResponseUtils.getResponseContent(response_code);
                    if ("0".equals(response_code)) {
                        //成功
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), "提交成功");
                        ReportInputTextActivity.this.finish();
                    }else if (responseContent != null) {
                        //给出错误提示
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                    }
                    pb_reportInput_ProgressBar.setVisibility(View.GONE);
                } catch (Exception e) {
                    pb_reportInput_ProgressBar.setVisibility(View.GONE);
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                }
            }
        });
    }
}
