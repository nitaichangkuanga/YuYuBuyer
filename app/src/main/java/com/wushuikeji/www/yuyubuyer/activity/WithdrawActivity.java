package com.wushuikeji.www.yuyubuyer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.base.BaseApplication;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.jsonparse.SendAuthoCodeParse;
import com.wushuikeji.www.yuyubuyer.utils.BackListenerUtils;
import com.wushuikeji.www.yuyubuyer.utils.CheckMobileUtils;
import com.wushuikeji.www.yuyubuyer.utils.CloseInputUtils;
import com.wushuikeji.www.yuyubuyer.utils.ISNetworkConnectUtils;
import com.wushuikeji.www.yuyubuyer.utils.RecoverClickUtils;
import com.wushuikeji.www.yuyubuyer.utils.RequestResponseUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToNextActivityUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToastUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;
import com.wushuikeji.www.yuyubuyer.view.ClearEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

public class WithdrawActivity extends AppCompatActivity {

    @InjectView(R.id.b_withdraw_decided)
    Button b_withdraw_decided;

    @InjectView(R.id.rl_withdraw_back)
    RelativeLayout rl_withdraw_back;

    @InjectView(R.id.ce_send_mobile)
    ClearEditText ce_send_mobile;

    @InjectView(R.id.ce_send_password)
    ClearEditText ce_send_password;

    @InjectView(R.id.ce_send_check)
    ClearEditText ce_send_check;

    @InjectView(R.id.pb_withdraw_loading)
    ProgressBar pb_withdraw_loading;

    @InjectView(R.id.b_register_send)
    Button b_register_send;

    private TimerTask task;
    private Timer timer;
    //默认验证码时间
    private int time = 60;

    //发送验证码
    private String sendCodeUrl = Constants.commontUrl + "buildcode";

    //检查提现填入的信息
    private String checkDrawUrl = Constants.commontUrl + "cashout/fetch_check";
    private String mAllMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        initView();
        initEvent();
    }

    private void initView() {
        ButterKnife.inject(this);
        //用户账户的余额
        mAllMoney = getIntent().getStringExtra("allMoney");
    }
    private void initEvent() {
        //返回
        BackListenerUtils.backFinish(rl_withdraw_back,this);
        //确认提现(检查信息的正确性)
        b_withdraw_decided.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decidedWithDraw();
            }
        });
        //发送验证码
        b_register_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               sendAuthCode();
            }
        });
    }

    /**
     * 确认提现的逻辑
     */
    private void decidedWithDraw() {
        //判断手机号、密码等是否为空
        String phone = ce_send_mobile.getText().toString().trim();
        String password = ce_send_password.getText().toString().trim();
        String checkCode = ce_send_check.getText().toString().trim();
        //判断手机号
        if (TextUtils.isEmpty(phone)) {
            // 设置晃动
            ce_send_mobile.setShakeAnimation();
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "手机号不能空");
            return;
        }
        //判断密码
        if (TextUtils.isEmpty(password)) {
            ce_send_password.setShakeAnimation();
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "密码不能空");
            return;
        }
        //判断验证码
        if (TextUtils.isEmpty(checkCode)) {
            ce_send_check.setShakeAnimation();
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "验证码不能空");
            return;
        }
        //判断手机号的长度11和是否1开头
        if (!CheckMobileUtils.isMobile(phone)) {
            // 设置晃动
            ce_send_mobile.setShakeAnimation();
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "手机号类型不对");
            return;
        }
        //判断网络
        boolean iSNetworkConnect = ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext());
        if (!iSNetworkConnect) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
            return;
        } else {
            //关闭输入法
            CloseInputUtils.closeInput(UIUtils.getContext(), WithdrawActivity.this);
            //加载动画
            pb_withdraw_loading.setVisibility(View.VISIBLE);
            // 动画的时候，按钮和发送验证码不能点击了
            cancelClick();
            OkHttpUtils.post().url(checkDrawUrl).addParams("mobile", phone)
                    .addParams("password", password)
                    .addParams("verify_code", checkCode)
                    .build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                        //动画关闭
                        pb_withdraw_loading.setVisibility(View.GONE);
                        //恢复编辑状态
                        recoverClick();
                        ToastUtils.showToastInUIThread(UIUtils.getContext(),"请求超时");
                }

                @Override
                public void onResponse(String response, int id) {
                    try {
                        //动画关闭
                        pb_withdraw_loading.setVisibility(View.GONE);
                        //恢复编辑状态
                        recoverClick();
                        // 成功了，只是返回来请求的json数据,还需要解析
                        Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                        String response_code = map.get("response_code");
                        //需要判断各种响应码,获取具体错误信息
                        String responseContent = RequestResponseUtils.getResponseContent(response_code);
                        if("0".equals(response_code)) {
                            //验证成功，才能进一步跳到确认提现界面
                         ToNextActivityUtils.toNextActivityNotFinishAndParameters(WithdrawActivity.this,FinalWithDrawActivity.class,"allMoney",mAllMoney);
                            //加入管理当中
                            BaseApplication.getInstance().addActivity(WithdrawActivity.this);
                        }else if ("1002".equals(response_code)) {
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), "验证码与手机不匹配");
                        }else if ("800".equals(response_code)) {
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), "绑定手机与登录密码不匹配");
                        }
                    } catch (Exception e) {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(),"请求超时");
                        //动画关闭
                        pb_withdraw_loading.setVisibility(View.GONE);
                        //恢复编辑状态
                        recoverClick();
                    }
                }
            });
        }
    }

    /**
     * 发送验证码
     */
    private void sendAuthCode() {
        String mobile = ce_send_mobile.getText().toString().trim();
        //判断手机号
        if (TextUtils.isEmpty(mobile)) {
            // 设置晃动
            ce_send_mobile.setShakeAnimation();
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "手机号不能空");
            return;
        }
        //判断手机号的长度11和是否1开头
        if (!CheckMobileUtils.isMobile(mobile)) {
            // 设置晃动
            ce_send_mobile.setShakeAnimation();
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "手机号类型不对");
            return;
        }
        //判断网络
        boolean networkBoolean = ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext());
        if (!networkBoolean) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
            return;
        } else {
            //关闭输入法
            CloseInputUtils.closeInput(UIUtils.getContext(), WithdrawActivity.this);
            //加载动画
            pb_withdraw_loading.setVisibility(View.VISIBLE);
            // 动画的时候，注册按钮和发送验证码不能点击了
            cancelClick();
            //发送验证码，向服务器发送
            OkHttpUtils.post().url(sendCodeUrl).addParams("mobile", mobile).addParams("flag", "1")
                    .build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    try {
                        //动画关闭
                        pb_withdraw_loading.setVisibility(View.GONE);
                        //恢复编辑状态
                        recoverClick();
                        ToastUtils.showToastInUIThread(UIUtils.getContext(),"请求超时");
                    } catch (Exception exception) {
                    }
                }

                @Override
                public void onResponse(String response, int id) {
                    try {
                        //动画关闭
                        pb_withdraw_loading.setVisibility(View.GONE);
                        //恢复编辑状态
                        recoverClick();
                        // 成功了，只是返回来请求的json数据,还需要解析
                        Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                        String response_code = map.get("response_code");
                        //需要判断各种响应码,获取具体错误信息
                        String responseContent = RequestResponseUtils.getResponseContent(response_code);
                        //提示用户错误的具体原因
                        if (responseContent != null && "成功".equals(responseContent)) {
                            //请求成功,开始发送验证码,倒计时
                            verificationCodeTimer();
                        } else if (responseContent != null) {
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                        }
                    } catch (Exception e) {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(),"请求超时");
                        //动画关闭
                        pb_withdraw_loading.setVisibility(View.GONE);
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
        b_register_send.setClickable(false);
        b_withdraw_decided.setClickable(false);
        // 动画的时候，让EditText失去焦点，不能输入
        ce_send_mobile.setFocusable(false);
        ce_send_password.setFocusable(false);
        ce_send_check.setFocusable(false);
    }

    /**
     * 恢复默认可以点击
     */
    private void recoverClick() {
        //让EditText恢复编辑状态
        RecoverClickUtils.recoverClickUtils(ce_send_mobile);
        RecoverClickUtils.recoverClickUtils(ce_send_password);
        RecoverClickUtils.recoverClickUtils(ce_send_check);
        //其他控件可以点击
        b_register_send.setClickable(true);
        b_withdraw_decided.setClickable(true);
    }

    /**
     * 验证码倒计时
     */
    public void verificationCodeTimer() {
        // 手机号不为空，未注册过，有网络
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                // 回到主线程
                UIUtils.getMainThreadHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (time <= 0) {
                            b_register_send.setText("发送验证码");
                            time = 60;
                            b_register_send.setClickable(true);
                            task.cancel();
                        } else {
                            b_register_send.setText(time + "s");
                            // 设置在倒计时不能点击了，否则可以点击的话，就是每次会多走几秒
                            b_register_send.setClickable(false);
                        }
                        time--;
                    }
                });
            }
        };
        timer.schedule(task, 0, 1000);
    }
}
