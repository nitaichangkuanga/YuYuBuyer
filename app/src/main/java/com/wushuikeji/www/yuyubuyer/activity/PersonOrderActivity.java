package com.wushuikeji.www.yuyubuyer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.jsonparse.PersonalOrderParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.SendAuthoCodeParse;
import com.wushuikeji.www.yuyubuyer.utils.BackListenerUtils;
import com.wushuikeji.www.yuyubuyer.utils.RequestResponseUtils;
import com.wushuikeji.www.yuyubuyer.utils.SpUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToastUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;
import com.wushuikeji.www.yuyubuyer.view.EaseSwitchButton;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

public class PersonOrderActivity extends AppCompatActivity {

    @InjectView(R.id.rl_personalOrder_back)
    RelativeLayout rl_personalOrder_back;

    //视频接单
    @InjectView(R.id.custom_switch_videoOrder)
    EaseSwitchButton custom_switch_videoOrder;

    //线下接单
    @InjectView(R.id.custom_switch_lineOrder)
    EaseSwitchButton custom_switch_lineOrder;

    @InjectView(R.id.ll_switch_videoll)
    LinearLayout ll_switch_videoll;

    @InjectView(R.id.ll_switch_linell)
    LinearLayout ll_switch_linell;

    @InjectView(R.id.rb_personOrder_onLine)
    RadioButton rb_personOrder_onLine;

    @InjectView(R.id.rb_personOrder_cloaking)
    RadioButton rb_personOrder_cloaking;

    @InjectView(R.id.rg_personOrder_RadioGroup)
    RadioGroup rg_personOrder_RadioGroup;

    @InjectView(R.id.pb_personalOrder_loading)
    ProgressBar pb_personalOrder_loading;

    private int lineIndex = 1;
    //更新用户状态的地址
    private String updateStatusUrl = Constants.commontUrl + "user/update_status";
    //请求用户状态的地址
    private String defaultStatusUrl = Constants.commontUrl + "user/status_info";
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_order);
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
        //默认在线
        rb_personOrder_onLine.setChecked(true);
        //进来请求网络，设置默认值
        requestNetworkSettingDefaultData();
    }

    private void initEvent() {
        //返回
        BackListenerUtils.backFinish(rl_personalOrder_back,this);
        //视频接单
        ll_switch_videoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (custom_switch_videoOrder.isSwitchOpen()) {
                    //显示关闭图标
                    requestNetwork("2","","");
                } else {
                    //显示打开图标
                    requestNetwork("1","","");
                }
            }
        });
        //线下接单
        ll_switch_linell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (custom_switch_lineOrder.isSwitchOpen()) {
                    //显示关闭图标
                    requestNetwork("","2","");
                } else {
                    //显示打开图标
                    requestNetwork("","1","");
                }
            }
        });
        //在线还是隐身
        rg_personOrder_RadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_personOrder_onLine://在线
                        //显示在线
                        requestNetwork("","","1");
                        break;
                    case R.id.rb_personOrder_cloaking://隐身
                        //显示隐身
                        requestNetwork("","","2");
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 视频接单请求网络
     */
    private void requestNetwork(String is_video, String is_shopping, final String status) {

        final String tempIs_video = is_video;
        final String tempIs_shopping = is_shopping;
        final String tempStatus = status;

        pb_personalOrder_loading.setVisibility(View.VISIBLE);

        OkHttpUtils.post().url(updateStatusUrl).addParams("user_id", mUserId)
                .addParams("is_video", is_video)
                .addParams("is_shopping", is_shopping)
                .addParams("status", status)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                pb_personalOrder_loading.setVisibility(View.GONE);
                ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
            }

            @Override
            public void onResponse(String response, int id) {

                Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                String response_code = map.get("response_code");
                //需要判断各种响应码,获取具体错误信息
                String responseContent = RequestResponseUtils.getResponseContent(response_code);
                if ("0".equals(response_code)) {
                    //修改成功，需要改变图片
                    if("2".equals(tempIs_video)) {
                        //显示不接受视频接单
                        custom_switch_videoOrder.closeSwitch();
                    }else if("1".equals(tempIs_video)) {
                        custom_switch_videoOrder.openSwitch();
                    }

                    if("2".equals(tempIs_shopping)) {
                        //显示不接受线下接单
                        custom_switch_lineOrder.closeSwitch();
                    }else if("1".equals(tempIs_shopping)) {
                        custom_switch_lineOrder.openSwitch();
                    }

                    if("2".equals(tempStatus)) {
                        //显示隐身
                        rb_personOrder_cloaking.setChecked(true);
                    }else if("1".equals(tempStatus)) {
                        //显示在线
                        rb_personOrder_onLine.setChecked(true);
                    }

                }else if (responseContent != null) {
                    //给出错误提示
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                }
                pb_personalOrder_loading.setVisibility(View.GONE);
            }
        });
    }
    /**
     * 请求网络设置默认值
     */
    private void requestNetworkSettingDefaultData() {

        pb_personalOrder_loading.setVisibility(View.VISIBLE);

        OkHttpUtils.post().url(defaultStatusUrl).addParams("user_id", mUserId)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                pb_personalOrder_loading.setVisibility(View.GONE);
                ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
            }

            @Override
            public void onResponse(String response, int id) {

                Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                String response_code = map.get("response_code");
                //需要判断各种响应码,获取具体错误信息
                String responseContent = RequestResponseUtils.getResponseContent(response_code);
                if ("0".equals(response_code)) {
                    Map<String, String> userOrderMap = PersonalOrderParse.personalOrderParse(response);
                    setDefaultData(userOrderMap);
                }else if (responseContent != null) {
                    //给出错误提示
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                }
                pb_personalOrder_loading.setVisibility(View.GONE);
            }
        });
    }

    private void setDefaultData(Map<String, String> userOrderMap) {
        //视频
        String is_video = userOrderMap.get("is_video");
        //线下
        String is_shopping = userOrderMap.get("is_shopping");
        //状态
        String status = userOrderMap.get("status");
        if(!TextUtils.isEmpty(is_video)) {
            if("1".equals(is_video)) {
                custom_switch_videoOrder.openSwitch();
            }else if("2".equals(is_video)) {
                custom_switch_videoOrder.closeSwitch();
            }
        }

        if(!TextUtils.isEmpty(is_shopping)) {
            if("1".equals(is_shopping)) {
                custom_switch_lineOrder.openSwitch();
            }else if("2".equals(is_shopping)) {
                custom_switch_lineOrder.closeSwitch();
            }
        }

        if(!TextUtils.isEmpty(status)) {
            if("1".equals(status)) {
                rb_personOrder_onLine.setChecked(true);
            }else if("2".equals(status)) {
                rb_personOrder_cloaking.setChecked(true);
            }
        }

    }
}
