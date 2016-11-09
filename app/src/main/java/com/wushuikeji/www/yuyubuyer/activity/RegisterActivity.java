package com.wushuikeji.www.yuyubuyer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.jsonparse.SendAuthoCodeParse;
import com.wushuikeji.www.yuyubuyer.utils.CheckMobileUtils;
import com.wushuikeji.www.yuyubuyer.utils.CloseInputUtils;
import com.wushuikeji.www.yuyubuyer.utils.ISNetworkConnectUtils;
import com.wushuikeji.www.yuyubuyer.utils.LoadingRoateAnimationUtils;
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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    @InjectView(R.id.b_register_register)
    Button b_register_register;

    @InjectView(R.id.b_registerActivity_send)
    Button b_registerActivity_send;

    @InjectView(R.id.rl_register_back)
    RelativeLayout rl_register_back;

    @InjectView(R.id.et_register_phone)
    ClearEditText et_register_phone;

    @InjectView(R.id.et_register_password)
    ClearEditText et_register_password;

    @InjectView(R.id.et_register_decided)
    ClearEditText et_register_decided;

    @InjectView(R.id.et_register_code)
    ClearEditText et_register_code;

    @InjectView(R.id.tv_register_select)
    TextView tv_register_select;

    @InjectView(R.id.tv_register_business)
    TextView tv_register_business;

    @InjectView(R.id.tv_register_deal)
    TextView tv_register_deal;

    @InjectView(R.id.iv_register_loading)
    ImageView iv_register_loading;

    @InjectView(R.id.linearLayout_register_linearLayout)
    LinearLayout linearLayout_register_linearLayout;

    //注册
    private String registerUrl = Constants.commontUrl + "register";
    //发送验证码
    private String sendCodeUrl = Constants.commontUrl + "buildcode";
    //验证码核对
    private String checkCodeUrl = Constants.commontUrl + "verifycode";

    private TimerTask task;
    private Timer timer;
    //默认验证码时间
    private int time = 60;
    private String mPhone;
    private String mPassword;
    private String mDecidedPassword;
    private String mCode;
    private String mBusiness;
    private String mArea_id = new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        initEvent();
    }

    private void initView() {
        ButterKnife.inject(this);
    }

    private void initEvent() {

        b_register_register.setOnClickListener(this);
        rl_register_back.setOnClickListener(this);
        b_registerActivity_send.setOnClickListener(this);
        tv_register_deal.setOnClickListener(this);
        linearLayout_register_linearLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_register_register://注册
                registerButtonClick();
                break;
            case R.id.b_registerActivity_send://发送验证码
                sendAuthCode();
                break;
            case R.id.rl_register_back://返回
                finish();
                break;
            case R.id.tv_register_deal://查看协议
                ToNextActivityUtils.toNextAndNoFinishActivity(RegisterActivity.this,AgreementActivity.class);
                break;
            case R.id.linearLayout_register_linearLayout://选择所在的商圈
                Intent intent = new Intent(RegisterActivity.this,SelectCityActivity.class);
                intent.putExtra("fromRegister",true);
                startActivityForResult(intent,1);
                break;
            default:
                break;
        }
    }

    /**
     * 发送验证码
     */
    private void sendAuthCode() {
        String mobile = et_register_phone.getText().toString().trim();
        //判断手机号
        if (TextUtils.isEmpty(mobile)) {
            // 设置晃动
            et_register_phone.setShakeAnimation();
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "手机号不能空");
            return;
        }
        //判断手机号的长度11和是否1开头
        if (!CheckMobileUtils.isMobile(mobile)) {
            // 设置晃动
            et_register_phone.setShakeAnimation();
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
            CloseInputUtils.closeInput(UIUtils.getContext(), RegisterActivity.this);
            //加载动画
            iv_register_loading.setVisibility(View.VISIBLE);
            LoadingRoateAnimationUtils.loadingGifPicture(iv_register_loading,UIUtils.getContext());
            // 动画的时候，注册按钮和发送验证码不能点击了
            cancelClick();
            //发送验证码，向服务器发送
            OkHttpUtils.post().url(sendCodeUrl).addParams("mobile", mobile).addParams("flag", "0")
                    .build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    try {
                        //动画关闭
                        iv_register_loading.setVisibility(View.GONE);
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
                        iv_register_loading.setVisibility(View.GONE);
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
                        iv_register_loading.setVisibility(View.GONE);
                        //恢复编辑状态
                        recoverClick();
                    }
                }
            });
        }
    }

    /**
     * 注册
     */
    private void registerButtonClick() {
        //判断手机号、密码等是否为空
        mPhone = et_register_phone.getText().toString().trim();
        mPassword = et_register_password.getText().toString().trim();
        mDecidedPassword = et_register_decided.getText().toString().trim();
        mCode = et_register_code.getText().toString().trim();
        //选择的商圈
        mBusiness = tv_register_select.getText().toString().trim();

        //判断手机号
        if (TextUtils.isEmpty(mPhone)) {
            // 设置晃动
            et_register_phone.setShakeAnimation();
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "手机号不能空");
            return;
        }
        //判断密码
        if (TextUtils.isEmpty(mPassword)) {
            et_register_password.setShakeAnimation();
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "密码不能空");
            return;
        }
        //判断确认密码
        if (TextUtils.isEmpty(mDecidedPassword)) {
            // 设置晃动
            et_register_decided.setShakeAnimation();
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "确认密码不能空");
            return;
        }
        //判断验证码
        if (TextUtils.isEmpty(mCode)) {
            et_register_code.setShakeAnimation();
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "验证码不能空");
            return;
        }
        //判断手机号的长度11和是否1开头
        if (!CheckMobileUtils.isMobile(mPhone)) {
            // 设置晃动
            et_register_phone.setShakeAnimation();
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "手机号类型不对");
            return;
        }
        //判断密码的长度是否超过6位字符
        if (mPassword.length() > 6) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "密码长度超过6位");
            return;
        }
        //判断两次输入的密码是否一样
        if (!mPassword.equals(mDecidedPassword)) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "两次输入的密码不一致");
            return;
        }
        //判断是否选择了商圈
        if ("选择所在商圈".equals(mBusiness)) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "请选择所在商圈");
            return;
        }
        //判断网络
        boolean iSNetworkConnect = ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext());
        if (!iSNetworkConnect) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
            return;
        } else {
            //关闭输入法
            CloseInputUtils.closeInput(UIUtils.getContext(), RegisterActivity.this);
            //加载动画
            iv_register_loading.setVisibility(View.VISIBLE);
            LoadingRoateAnimationUtils.loadingGifPicture(iv_register_loading,UIUtils.getContext());
            // 动画的时候，注册按钮和发送验证码不能点击了
            cancelClick();
            //先核对验证码是否正确
            OkHttpUtils.post().url(checkCodeUrl).addParams("mobile", mPhone)
                    .addParams("verify_code", mCode)
                    .build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    try {
                        //动画关闭
                        iv_register_loading.setVisibility(View.GONE);
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
                        iv_register_loading.setVisibility(View.GONE);
                        //恢复编辑状态
                        recoverClick();
                        // 成功了，只是返回来请求的json数据,还需要解析
                        Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                        String response_code = map.get("response_code");
                        //需要判断各种响应码,获取具体错误信息
                        String responseContent = RequestResponseUtils.getResponseContent(response_code);

                        if("0".equals(response_code)) {
                            //验证码成功，才能进一步去注册
                            realRegister();
                        }else if (responseContent != null) {
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                        }
                    } catch (Exception e) {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(),"请求超时");
                        //动画关闭
                        iv_register_loading.setVisibility(View.GONE);
                        //恢复编辑状态
                        recoverClick();
                    }
                }
            });
        }
    }

    /**
     * 验证码正确之后的注册
     */
    private void realRegister() {
        //post请求网络，注册
        OkHttpUtils.post().url(registerUrl).addParams("mobile", mPhone)
                .addParams("password", mPassword)
                .addParams("verify_code", mCode)
                .addParams("area_id", mArea_id)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                try {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(),"请求超时");
                }catch(Exception ex) {}
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
                        //注册成功
                        ToastUtils.showToastInUIThread(UIUtils.getContext(),"恭喜您，注册成功");
                        RegisterActivity.this.finish();
                    }else if (responseContent != null) {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                    }
                } catch (Exception e) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(),"请求超时");
                }
            }
        });
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
                            b_registerActivity_send.setText("发送验证码");
                            time = 60;
                            b_registerActivity_send.setClickable(true);
                            task.cancel();
                        } else {
                            b_registerActivity_send.setText(time + "s");
                            // 设置在倒计时不能点击了，否则可以点击的话，就是每次会多走几秒
                            b_registerActivity_send.setClickable(false);
                        }
                        time--;
                    }
                });
            }
        };
        timer.schedule(task, 0, 1000);
    }
    /**
     * 恢复默认可以点击
     */
    private void recoverClick() {
        //让EditText恢复编辑状态
        RecoverClickUtils.recoverClickUtils(et_register_phone);
        RecoverClickUtils.recoverClickUtils(et_register_password);
        RecoverClickUtils.recoverClickUtils(et_register_decided);
        RecoverClickUtils.recoverClickUtils(et_register_code);
        //其他控件可以点击
        b_registerActivity_send.setClickable(true);
        b_register_register.setClickable(true);
        tv_register_deal.setClickable(true);
    }

    /**
     * 动画的时候不能点击
     */
    private void cancelClick() {
        b_registerActivity_send.setClickable(false);
        b_register_register.setClickable(false);
        tv_register_deal.setClickable(false);
        // 动画的时候，让EditText失去焦点，不能输入
        et_register_phone.setFocusable(false);
        et_register_password.setFocusable(false);
        et_register_decided.setFocusable(false);
        et_register_code.setFocusable(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == 22) {
            if(data != null) {
//                String area_id = data.getStringExtra("area_id");
                //得到城市名和商圈名
                String mCityName = data.getStringExtra("cityName");
                String mBusinessName = data.getStringExtra("businessName");
                mArea_id = data.getStringExtra("area_id");
                if(!TextUtils.isEmpty(mCityName)) {
                    tv_register_select.setText(mCityName);
                }
                tv_register_business.setText(mBusinessName);
            }
        }
    }
}
