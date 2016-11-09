package com.wushuikeji.www.yuyubuyer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.jsonparse.ContentSysMessDetailsParse;
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

public class SystemInfoDetailsActivity extends AppCompatActivity {

    @InjectView(R.id.rl_systemInfoDetails_back)
    RelativeLayout rl_systemInfoDetails_back;

    @InjectView(R.id.tv_sysytemInfoDetails_content)
    TextView tv_sysytemInfoDetails_content;

    @InjectView(R.id.tv_sysytemInfoDetails_dateAndTime)
    TextView tv_sysytemInfoDetails_dateAndTime;

    @InjectView(R.id.pb_systemDetails_loading)
    ProgressBar pb_systemDetails_loading;

    //系统消息详情
    private String sysMessDetailsUrl = Constants.commontUrl + "message/sys_msg_details";
    private String mMessageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_info_details);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        ButterKnife.inject(this);
        mMessageId = getIntent().getStringExtra("messageId");
    }

    private void initData() {
        //判断网络
        if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
            return;
        } else {
            //有网
            loadNetworkData();
        }
    }

    private void initEvent() {
        //返回
        BackListenerUtils.backFinish(rl_systemInfoDetails_back,this);
    }

    /**
     * 加载网络的数据
     */
    private void loadNetworkData() {

        //进度条展示
        pb_systemDetails_loading.setVisibility(View.VISIBLE);

        OkHttpUtils.post().url(sysMessDetailsUrl).addParams("message_id",mMessageId).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                pb_systemDetails_loading.setVisibility(View.GONE);
                ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");

            }

            @Override
            public void onResponse(String response, int id) {

                try {
                    // 成功了，只是返回来请求的json数据,还需要解析
                    Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                    String response_code = map.get("response_code");
                    //需要判断各种响应码,获取具体错误信息
                    String responseContent = RequestResponseUtils.getResponseContent(response_code);

                    if ("0".equals(response_code)) {
                        Map<String, String> sysMessDetailsMap = ContentSysMessDetailsParse.contentSysMessDetailsParse(response);
                        if(sysMessDetailsMap != null) {
                            //设置数据
                            tv_sysytemInfoDetails_content.setText(sysMessDetailsMap.get("content"));
                            tv_sysytemInfoDetails_dateAndTime.setText(sysMessDetailsMap.get("createTime"));
                        }
                    } else if (responseContent != null) {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                    }
                    //隐藏进度条
                    pb_systemDetails_loading.setVisibility(View.GONE);
                } catch (Exception e) {
                    pb_systemDetails_loading.setVisibility(View.GONE);
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                }
            }
        });
    }
}
