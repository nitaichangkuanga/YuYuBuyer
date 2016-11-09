package com.wushuikeji.www.yuyubuyer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.jsonparse.SendAuthoCodeParse;
import com.wushuikeji.www.yuyubuyer.utils.CheckMobileUtils;
import com.wushuikeji.www.yuyubuyer.utils.CloseInputUtils;
import com.wushuikeji.www.yuyubuyer.utils.ISNetworkConnectUtils;
import com.wushuikeji.www.yuyubuyer.utils.LoadingRoateAnimationUtils;
import com.wushuikeji.www.yuyubuyer.utils.RecoverClickUtils;
import com.wushuikeji.www.yuyubuyer.utils.RequestResponseUtils;
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

public class FindPasswordActivity extends AppCompatActivity implements View.OnClickListener{

    @InjectView(R.id.b_findPassword_next)
    Button b_findPassword_next;

    @InjectView(R.id.rv_findPassword_back)
    RelativeLayout rv_findPassword_back;

    @InjectView(R.id.b_findPassword_send)
    Button b_findPassword_send;

    @InjectView(R.id.clearEt_findPassword_phone)
    ClearEditText clearEt_findPassword_phone;

    @InjectView(R.id.clearEt_findPassword_code)
    ClearEditText clearEt_findPassword_code;

    @InjectView(R.id.iv_findpassword_loading)
    ImageView iv_findpassword_loading;

    //发送验证码
    private String sendCodeUrl = Constants.commontUrl + "buildcode";
    //验证码核对
    private String checkCodeUrl = Constants.commontUrl + "verifycode";

    private TimerTask task;
    private Timer timer;
    //默认验证码时间
    private int time = 60;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);
        initView();
        initEvent();
    }
    private void initView() {
        ButterKnife.inject(this);
    }

    private void initEvent() {

        b_findPassword_next.setOnClickListener(this);
        rv_findPassword_back.setOnClickListener(this);
        b_findPassword_send.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_findPassword_next://下一步
                //验证码的验证和进入下一步
                checkCodeIsSuccess();
                break;
            case R.id.rv_findPassword_back://返回
                finish();
                break;
            case R.id.b_findPassword_send://发送验证码
                sendAuthCode();
                break;
            default:
                break;
        }
    }

    /**
     * 核对验证码是否正确
     */
    private void checkCodeIsSuccess() {
        String mobile = clearEt_findPassword_phone.getText().toString().trim();
        String code = clearEt_findPassword_code.getText().toString().trim();
        //判断手机号
        if (TextUtils.isEmpty(mobile)) {
            // 设置晃动
            clearEt_findPassword_phone.setShakeAnimation();
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "手机号不能空");
            return;
        }
        //判断验证码
        if (TextUtils.isEmpty(code)) {
            // 设置晃动
            clearEt_findPassword_code.setShakeAnimation();
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "验证码不能空");
            return;
        }
        //判断手机号的长度11和是否1开头
        if (!CheckMobileUtils.isMobile(mobile)) {
            // 设置晃动
            clearEt_findPassword_phone.setShakeAnimation();
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
            CloseInputUtils.closeInput(UIUtils.getContext(), FindPasswordActivity.this);
            //加载动画
            iv_findpassword_loading.setVisibility(View.VISIBLE);
            LoadingRoateAnimationUtils.loadingGifPicture(iv_findpassword_loading,UIUtils.getContext());
            // 动画的时候，注册按钮和发送验证码不能点击了
            cancelClick();
            OkHttpUtils.post().url(checkCodeUrl).addParams("mobile", mobile)
                    .addParams("verify_code", code)
                    .build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    try {
                        //动画关闭
                        iv_findpassword_loading.setVisibility(View.GONE);
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
                        iv_findpassword_loading.setVisibility(View.GONE);
                        //恢复编辑状态
                        recoverClick();
                        // 成功了，只是返回来请求的json数据,还需要解析
                        Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                        String response_code = map.get("response_code");
                        //需要判断各种响应码,获取具体错误信息
                        String responseContent = RequestResponseUtils.getResponseContent(response_code);

                        if("0".equals(response_code)) {
                            //验证码成功，才能进一步设置密码
                            Intent intent = new Intent(FindPasswordActivity.this,SettingPasswordActivity.class);
                            //手机号传过去
                            intent.putExtra("mobile",clearEt_findPassword_phone.getText().toString());
                            FindPasswordActivity.this.startActivity(intent);
                        }else if (responseContent != null) {
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                        }
                    } catch (Exception e) {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(),"请求超时");
                        //动画关闭
                        iv_findpassword_loading.setVisibility(View.GONE);
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
        String mobile = clearEt_findPassword_phone.getText().toString().trim();
        //判断手机号
        if (TextUtils.isEmpty(mobile)) {
            // 设置晃动
            clearEt_findPassword_phone.setShakeAnimation();
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "手机号不能空");
            return;
        }
        //判断手机号的长度11和是否1开头
        if (!CheckMobileUtils.isMobile(mobile)) {
            // 设置晃动
            clearEt_findPassword_phone.setShakeAnimation();
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
            CloseInputUtils.closeInput(UIUtils.getContext(), FindPasswordActivity.this);
            //加载动画
            iv_findpassword_loading.setVisibility(View.VISIBLE);
            LoadingRoateAnimationUtils.loadingGifPicture(iv_findpassword_loading,UIUtils.getContext());
            // 动画的时候，注册按钮和发送验证码不能点击了
            cancelClick();
            //发送验证码，向服务器发送
            OkHttpUtils.post().url(sendCodeUrl).addParams("mobile", mobile).addParams("flag", "1")
                    .build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    try {
                        //动画关闭
                        iv_findpassword_loading.setVisibility(View.GONE);
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
                        iv_findpassword_loading.setVisibility(View.GONE);
                        //恢复编辑状态
                        recoverClick();
                        // 成功了，只是返回来请求的json数据,还需要解析
                        Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                        String response_code = map.get("response_code");
                        //需要判断各种响应码,获取具体错误信息
                        String responseContent = RequestResponseUtils.getResponseContent(response_code);
                        //提示用户错误的具体原因
                        if ("0".equals(response_code)) {
                            //请求成功,开始发送验证码,倒计时
                            verificationCodeTimer();
                        } else if (responseContent != null) {
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                        }
                    } catch (Exception e) {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(),"请求超时");
                        //动画关闭
                        iv_findpassword_loading.setVisibility(View.GONE);
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
        b_findPassword_next.setClickable(false);
        b_findPassword_send.setClickable(false);
        // 动画的时候，让EditText失去焦点，不能输入
        clearEt_findPassword_phone.setFocusable(false);
        clearEt_findPassword_code.setFocusable(false);
    }

    /**
     * 恢复默认可以点击
     */
    private void recoverClick() {
        //让EditText恢复编辑状态
        RecoverClickUtils.recoverClickUtils(clearEt_findPassword_phone);
        RecoverClickUtils.recoverClickUtils(clearEt_findPassword_code);
        //其他控件可以点击
        b_findPassword_next.setClickable(true);
        b_findPassword_send.setClickable(true);
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
                            b_findPassword_send.setText("发送验证码");
                            time = 60;
                            b_findPassword_send.setClickable(true);
                            task.cancel();
                        } else {
                            b_findPassword_send.setText(time + "s");
                            // 设置在倒计时不能点击了，否则可以点击的话，就是每次会多走几秒
                            b_findPassword_send.setClickable(false);
                        }
                        time--;
                    }
                });
            }
        };
        timer.schedule(task, 0, 1000);
    }
}
