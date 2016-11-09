package com.wushuikeji.www.yuyubuyer.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.jsonparse.AccountSafetyParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.SendAuthoCodeParse;
import com.wushuikeji.www.yuyubuyer.utils.BackListenerUtils;
import com.wushuikeji.www.yuyubuyer.utils.CheckMobileUtils;
import com.wushuikeji.www.yuyubuyer.utils.CloseInputUtils;
import com.wushuikeji.www.yuyubuyer.utils.ISNetworkConnectUtils;
import com.wushuikeji.www.yuyubuyer.utils.RecoverClickUtils;
import com.wushuikeji.www.yuyubuyer.utils.RequestResponseUtils;
import com.wushuikeji.www.yuyubuyer.utils.SpUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToastUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;
import com.wushuikeji.www.yuyubuyer.view.ClearEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

public class AccountSafetyActivity extends AppCompatActivity {

    @InjectView(R.id.rl_accountSafety_back)
    RelativeLayout rl_accountSafety_back;

    //实名认证
    @InjectView(R.id.rl_accountSafety_authentication)
    RelativeLayout rl_accountSafety_authentication;

    @InjectView(R.id.tv_accountSafety_name)
    TextView tv_accountSafety_name;

    //绑定手机号
    @InjectView(R.id.rl_accountSafety_phone)
    RelativeLayout rl_accountSafety_phone;

    @InjectView(R.id.tv_accountSafety_pass)
    TextView tv_accountSafety_pass;

    @InjectView(R.id.tv_accountSafety_number)
    TextView tv_accountSafety_number;

    //密码
    @InjectView(R.id.rl_accountSafety_pass)
    RelativeLayout rl_accountSafety_pass;

    @InjectView(R.id.accontSafe_pb)
    ProgressBar accontSafe_pb;

    private String mUserId;

    private String accountSafeSubmitUrl = Constants.commontUrl + "auth/accreditation";

    //检查用户是否认证
    private String checkIsAuthenticationUrl = Constants.commontUrl + "auth/check";

    //记录是否已经认证
    private boolean isAuthentication;

    //设置新密码
    private String settingPasswordUrl = Constants.commontUrl + "forget";

    //发送验证码
    private String sendCodeUrl = Constants.commontUrl + "buildcode";
    private String mSaveUserPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_safety);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        ButterKnife.inject(this);
        //得到用户ID
        mUserId = SpUtils.getCacheString(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.BUYERID, "");
        String userPhone = SpUtils.getCacheString(MyConstants.USERPHONESPNAME, UIUtils.getContext(), MyConstants.USERPHONE, "");
        mSaveUserPassword = SpUtils.getCacheString(MyConstants.USERPHONESPNAME, UIUtils.getContext(), MyConstants.USERPASSWORDCOUNT,"");
        //設置默认手機號和密碼個數
        tv_accountSafety_number.setText(userPhone);
        //根据个数显示n个*
        if(!TextUtils.isEmpty(mSaveUserPassword)) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mSaveUserPassword.length(); i++) {
                sb.append("*");
            }
            tv_accountSafety_pass.setText(sb.toString());
        }
    }

    private void initData() {
        //进来请求网络，设置用户是否认证
        requestNetworkIsAuthentication();
    }

    private void initEvent() {
        //返回
        BackListenerUtils.backFinish(rl_accountSafety_back, this);
        //实名认证
        rl_accountSafety_authentication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //已经认证就不弹出
                if (!isAuthentication) {
                    //弹出自定义对话框
                    customAlertDialog();
                }
            }
        });
        //绑定手机号
        rl_accountSafety_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customPhoneDialog();
            }
        });
        //登录密码
        rl_accountSafety_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customPasswordDialog();
            }
        });
    }

    /**
     * 检查用户是否认证
     */
    private void requestNetworkIsAuthentication() {

        accontSafe_pb.setVisibility(View.VISIBLE);

        OkHttpUtils.post().url(checkIsAuthenticationUrl).addParams("user_id", mUserId)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                accontSafe_pb.setVisibility(View.GONE);
                ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
            }

            @Override
            public void onResponse(String response, int id) {

                Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                String response_code = map.get("response_code");
                //需要判断各种响应码,获取具体错误信息
                String responseContent = RequestResponseUtils.getResponseContent(response_code);
                if ("0".equals(response_code)) {
                    Map<String, String> accountSafetyMap = AccountSafetyParse.accountSafetyParse(response);
                    //判断是否认证过
                    isAuthentication(accountSafetyMap);
                }
                accontSafe_pb.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 判断使用是否实名认证过
     *
     * @param accountSafetyMap
     */
    private void isAuthentication(Map<String, String> accountSafetyMap) {

        String real_name = accountSafetyMap.get("real_name");

        String id_number = accountSafetyMap.get("id_number");

        if (!TextUtils.isEmpty(real_name) && !TextUtils.isEmpty(id_number)) {
            //认证过
            tv_accountSafety_name.setText("已认证");
            isAuthentication = true;
        } else {
            //未认证过
            tv_accountSafety_name.setText("未认证");
            isAuthentication = false;
        }
    }

    /**
     * 自定义密码的dialog
     */
    private void customPasswordDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(AccountSafetyActivity.this);
        View inflate = View.inflate(AccountSafetyActivity.this, R.layout.custom_passalert_dialog, null);
        final ClearEditText passOld_et = (ClearEditText) inflate.findViewById(R.id.et_pass_oldPassword);
        final ClearEditText passNew_et = (ClearEditText) inflate.findViewById(R.id.et_pass_newPassword);
        final ClearEditText passDecided_et = (ClearEditText) inflate.findViewById(R.id.et_pass_decidedPassword);
        //取消
        Button canleButton = (Button) inflate.findViewById(R.id.b_pass_canle);
        //提交
        Button submitButton = (Button) inflate.findViewById(R.id.b_pass_submit);
        //弹出输入法
        displayInput(passOld_et);
        mBuilder.setView(inflate);
        final AlertDialog alertDialog = mBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        canleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPass = passOld_et.getText().toString().trim();
                String newPass = passNew_et.getText().toString().trim();
                String decidePass = passDecided_et.getText().toString().trim();
                //手机号
                String userMobile = tv_accountSafety_number.getText().toString().trim();
                //请输入旧密码
                if (TextUtils.isEmpty(oldPass)) {
                    passOld_et.setShakeAnimation();
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "旧密码不能为空");
                    return;
                }
                //请输入新密码
                if (TextUtils.isEmpty(newPass)) {
                    passNew_et.setShakeAnimation();
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "新密码不能为空");
                    return;
                }
                //请确定输入新密码
                if (TextUtils.isEmpty(decidePass)) {
                    passDecided_et.setShakeAnimation();
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "确定新密码不能为空");
                    return;
                }
                //新密码的长度必须大于6位字符
                if (newPass.length() <= 6) {
                    passNew_et.setShakeAnimation();
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "新密码长度小于6");
                    return;
                }
                if (decidePass.length() <= 6) {
                    passDecided_et.setShakeAnimation();
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "确定密码长度小于6");
                    return;
                }
                //旧密码输入不正确
                if (!oldPass.equals(mSaveUserPassword)) {
                    passOld_et.setShakeAnimation();
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "旧密码输入不正确");
                    return;
                }
                //判断两次输入的密码是否一样
                if (!newPass.equals(decidePass)) {
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
                    CloseInputUtils.closeInput(UIUtils.getContext(), AccountSafetyActivity.this);
                    //提交设置新密码
                    requestModifiPassword(userMobile, alertDialog, newPass);
                }
            }
        });
    }

    /**
     * 请求修改密码
     */
    private void requestModifiPassword(String userMobile, final AlertDialog alertDialog, final String newPassword) {

        final String tempNewPassword = newPassword;
        //设置新的密码
        OkHttpUtils.post().url(settingPasswordUrl).addParams("mobile", userMobile)
                .addParams("password", newPassword)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
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
                        //设置密码成功
                        alertDialog.dismiss();
                        //设置新密码和保存
                        setNewPasswordAndSave(tempNewPassword);
                        ToastUtils.showToastInUIThread(UIUtils.getContext(),"修改成功");
                    } else if (responseContent != null) {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                    }
                } catch (Exception e) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                }
            }
        });
    }

    /**
     * 设置和保存新密码
     */
    private void setNewPasswordAndSave(String newPassword) {
        //根据个数显示n个*
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < newPassword.length(); i++) {
            sb.append("*");
        }
        tv_accountSafety_pass.setText(sb.toString());
        //保存
        SpUtils.putCacheString(MyConstants.USERPHONESPNAME, UIUtils.getContext(), MyConstants.USERPASSWORDCOUNT,newPassword);
    }

    /**
     * 自定义手机号的dialog
     */
    private void customPhoneDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(AccountSafetyActivity.this);
        View inflate = View.inflate(AccountSafetyActivity.this, R.layout.custom_phonealert_dialog, null);
        final ClearEditText phone_et = (ClearEditText) inflate.findViewById(R.id.ct_dialog_num);
        //取消
        Button canleButton = (Button) inflate.findViewById(R.id.b_phone_canle);
        //发送验证码
        Button sendButton = (Button) inflate.findViewById(R.id.b_phone_send);
        //弹出输入法
        displayInput(phone_et);
        mBuilder.setView(inflate);
        final AlertDialog alertDialog = mBuilder.create();

        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        canleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = phone_et.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    phone_et.setShakeAnimation();
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "手机号不能为空");
                    return;
                }
                //判断手机号的长度11和是否1开头
                if (!CheckMobileUtils.isMobile(phone)) {
                    // 设置晃动
                    phone_et.setShakeAnimation();
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
                    CloseInputUtils.closeInput(UIUtils.getContext(), AccountSafetyActivity.this);
                    //请求发送验证码
                    requestSendCode(phone, alertDialog);
                }
            }
        });
    }

    /**
     * 请求发送验证码
     */
    private void requestSendCode(final String mobile, final AlertDialog alertDialog) {
        //发送验证码，向服务器发送
        OkHttpUtils.post().url(sendCodeUrl).addParams("mobile", mobile).addParams("flag", "0")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                try {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                } catch (Exception exception) {
                }
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    // 成功了，只是返回来请求的json数据,还需要解析
                    Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                    String response_code = map.get("response_code");
                    //需要判断各种响应码,获取具体错误信息
                    String responseContent = RequestResponseUtils.getResponseContent(response_code);
                    //提示用户错误的具体原因
                    if ("0".equals(response_code)) {
                        //请求成功
                        alertDialog.dismiss();
                        //设置新手机号和保存（这功能就是个摆设）
                        //tv_accountSafety_number.setText(mobile);
                        //SpUtils.putCacheString(MyConstants.USERPHONESPNAME, UIUtils.getContext(), MyConstants.USERPHONE, mobile);
                    } else if (responseContent != null) {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                    }
                } catch (Exception e) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                }
            }
        });
    }

    /**
     * 自定义实名认证对话框
     */
    private void customAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AccountSafetyActivity.this);
        View inflate = View.inflate(AccountSafetyActivity.this, R.layout.custom_alert_dialog, null);
        //输入姓名
        final ClearEditText userNameET = (ClearEditText) inflate.findViewById(R.id.clearEt_dialog_userName);
        //输入身份证
        final ClearEditText cardET = (ClearEditText) inflate.findViewById(R.id.clearEt_dialog_idCard);
        //取消
        final Button canleButton = (Button) inflate.findViewById(R.id.b_alert_canle);
        //提交
        final Button submitButton = (Button) inflate.findViewById(R.id.b_alert_submit);
        //进度条
        final ProgressBar pb_alertDialog_progressBar = (ProgressBar) inflate.findViewById(R.id.pb_alertDialog_progressBar);
        //弹出输入法
        displayInput(userNameET);
        builder.setView(inflate);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        //取消
        canleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        //提交
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = userNameET.getText().toString().trim();
                String userCard = cardET.getText().toString().trim();
                if (TextUtils.isEmpty(userName)) {
                    userNameET.setShakeAnimation();
                    ToastUtils.showToastInUIThread(AccountSafetyActivity.this, "真实姓名不能为空");
                    return;
                }

                if (TextUtils.isEmpty(userCard)) {
                    cardET.setShakeAnimation();
                    ToastUtils.showToastInUIThread(AccountSafetyActivity.this, "身份证号不能为空");
                    return;
                }
                //判断网络
                boolean iSNetworkConnect = ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext());
                if (!iSNetworkConnect) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                    return;
                } else {
                    CloseInputUtils.closeInput(UIUtils.getContext(), AccountSafetyActivity.this);
                    //进度的时候，按钮不能点击了
                    canleButton.setClickable(false);
                    submitButton.setClickable(false);
                    userNameET.setFocusable(false);
                    cardET.setFocusable(false);
                    OkHttpUtils.post().url(accountSafeSubmitUrl).addParams("user_id", mUserId)
                            .addParams("real_name", userName)
                            .addParams("ID_number", userCard).build().execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            //恢复编辑状态
                            recoverClick(userNameET, cardET, canleButton, submitButton);
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            try {
                                //恢复编辑状态
                                recoverClick(userNameET, cardET, canleButton, submitButton);
                                // 成功了，只是返回来请求的json数据,还需要解析
                                Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                                String response_code = map.get("response_code");
                                //需要判断各种响应码,获取具体错误信息
                                String responseContent = RequestResponseUtils.getResponseContent(response_code);
                                if ("0".equals(response_code)) {
                                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "认证成功");
                                    alertDialog.dismiss();
                                    //保存一下用户的真实姓名，在查看个人信息里需要用，清除缓存不清理(永远保留)
//                                    SpUtils.putCacheString(MyConstants.REALUSERSPNAME,UIUtils.getContext(),MyConstants.REALUSER,"");
                                } else if (responseContent != null) {
                                    ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                                }
                            } catch (Exception e) {
                                ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                                //恢复编辑状态
                                recoverClick(userNameET, cardET, canleButton, submitButton);
                            }
                        }
                    });

                }
            }
        });
    }

    //Dialog中弹出输入法
    private void displayInput(final EditText et) {
        UIUtils.getMainThreadHandler().postDelayed(new Runnable() {
            public void run() {
                InputMethodManager inManager = (InputMethodManager) et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 200);
    }

    /**
     * 恢复默认可以点击
     */
    private void recoverClick(ClearEditText userNameEditText, ClearEditText userCardEditText, Button cancel, Button submit) {
        //让EditText恢复编辑状态
        RecoverClickUtils.recoverClickUtils(userNameEditText);
        RecoverClickUtils.recoverClickUtils(userCardEditText);
        //其他控件可以点击
        cancel.setClickable(true);
        submit.setClickable(true);
    }
}
