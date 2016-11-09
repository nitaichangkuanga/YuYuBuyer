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

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

public class SettingPasswordActivity extends AppCompatActivity implements View.OnClickListener{

    @InjectView(R.id.b_settingpass_decided)
    Button b_settingpass_decided;

    @InjectView(R.id.rv_settingpass_back)
    RelativeLayout rv_settingpass_back;

    @InjectView(R.id.clearEt_settingPassword_newPass)
    ClearEditText clearEt_settingPassword_newPass;

    @InjectView(R.id.clearEt_settingPassword_decidedPass)
    ClearEditText clearEt_settingPassword_decidedPass;

    @InjectView(R.id.iv_settingPassword_loading)
    ImageView iv_settingPassword_loading;

    //设置新密码
    private String settingPasswordUrl = Constants.commontUrl + "forget";
    private String mMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_password);
        initView();
        initEvent();
    }

    private void initView() {
        ButterKnife.inject(this);
        //得到传过来的手机号
        mMobile = getIntent().getStringExtra("mobile");
    }

    private void initEvent() {
        b_settingpass_decided.setOnClickListener(this);
        rv_settingpass_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rv_settingpass_back://返回
                finish();
                break;
            case R.id.b_settingpass_decided://确认，回到登录界面
                checkPasswordIsSample();
                break;
            default:
                break;
        }

    }

    /**
     * 确认密码是否一样
     */
    private void checkPasswordIsSample() {
        String newPassword = clearEt_settingPassword_newPass.getText().toString().trim();
        String decidedPassword = clearEt_settingPassword_decidedPass.getText().toString().trim();
        //判断新密码
        if (TextUtils.isEmpty(newPassword)) {
            // 设置晃动
            clearEt_settingPassword_newPass.setShakeAnimation();
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "新密码不能为空");
            return;
        }
        //判断确认密码
        if (TextUtils.isEmpty(decidedPassword)) {
            clearEt_settingPassword_decidedPass.setShakeAnimation();
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "确认密码不能空");
            return;
        }
        //判断两次输入的密码是否一样
        if (!newPassword.equals(decidedPassword)) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "两次输入的密码不一致");
            return;
        }
        //判断网络
        boolean iSNetworkConnect = ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext());
        if (!iSNetworkConnect) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
            return;
        } else {
            //关闭输入法
            CloseInputUtils.closeInput(UIUtils.getContext(), SettingPasswordActivity.this);
            //加载动画
            iv_settingPassword_loading.setVisibility(View.VISIBLE);
            LoadingRoateAnimationUtils.loadingGifPicture(iv_settingPassword_loading,UIUtils.getContext());
            // 动画的时候，注册按钮和发送验证码不能点击了
            cancelClick();
            //设置新的密码
            OkHttpUtils.post().url(settingPasswordUrl).addParams("mobile",mMobile)
                    .addParams("password", clearEt_settingPassword_newPass.getText().toString())
                    .build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    try {
                        //动画关闭
                        iv_settingPassword_loading.setVisibility(View.GONE);
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
                        iv_settingPassword_loading.setVisibility(View.GONE);
                        //恢复编辑状态
                        recoverClick();
                        // 成功了，只是返回来请求的json数据,还需要解析
                        Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                        String response_code = map.get("response_code");
                        //需要判断各种响应码,获取具体错误信息
                        String responseContent = RequestResponseUtils.getResponseContent(response_code);

                        if("0".equals(response_code)) {
                            //设置密码成功
                            Intent intent = new Intent(SettingPasswordActivity.this,LoginActivity.class);
                            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                            SettingPasswordActivity.this.startActivity(intent);
                            SettingPasswordActivity.this.finish();
                        }else if (responseContent != null) {
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                        }
                    } catch (Exception e) {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(),"请求超时");
                        //动画关闭
                        iv_settingPassword_loading.setVisibility(View.GONE);
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
        b_settingpass_decided.setClickable(false);
        // 动画的时候，让EditText失去焦点，不能输入
        clearEt_settingPassword_newPass.setFocusable(false);
        clearEt_settingPassword_decidedPass.setFocusable(false);
    }
    /**
     * 恢复默认可以点击
     */
    private void recoverClick() {
        //让EditText恢复编辑状态
        RecoverClickUtils.recoverClickUtils(clearEt_settingPassword_newPass);
        RecoverClickUtils.recoverClickUtils(clearEt_settingPassword_decidedPass);
        //其他控件可以点击
        b_settingpass_decided.setClickable(true);
    }
}
