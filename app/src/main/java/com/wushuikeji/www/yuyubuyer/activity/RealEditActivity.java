package com.wushuikeji.www.yuyubuyer.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.base.BaseApplication;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.jsonparse.ContentParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.SendAuthoCodeParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.UserInfoDetailsParse;
import com.wushuikeji.www.yuyubuyer.utils.BackListenerUtils;
import com.wushuikeji.www.yuyubuyer.utils.GetNormalOptionsUtils;
import com.wushuikeji.www.yuyubuyer.utils.ISNetworkConnectUtils;
import com.wushuikeji.www.yuyubuyer.utils.RequestResponseUtils;
import com.wushuikeji.www.yuyubuyer.utils.SpUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToastUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;
import com.wushuikeji.www.yuyubuyer.view.ClearEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

public class RealEditActivity extends AppCompatActivity {

    @InjectView(R.id.rl_realEdit_back)
    RelativeLayout rl_realEdit_back;

    @InjectView(R.id.iv_back)
    ImageView iv_back;

    @InjectView(R.id.rb_realEdit_manSex)
    RadioButton rb_realEdit_manSex;

    @InjectView(R.id.rl_realEdit_touxiang)
    RelativeLayout rl_realEdit_touxiang;

    @InjectView(R.id.rl_realEdit_sexRelativeLayout)
    RelativeLayout rl_realEdit_sexRelativeLayout;

    @InjectView(R.id.iv_realEdit_userImg)
    ImageView iv_realEdit_userImg;

    @InjectView(R.id.rl_realEdit_niCheng)
    RelativeLayout rl_realEdit_niCheng;

    @InjectView(R.id.rl_realEdit_age)
    RelativeLayout rl_realEdit_age;

    @InjectView(R.id.rl_realEdit_moko)
    RelativeLayout rl_realEdit_moko;

    @InjectView(R.id.tv_realEdit_name)
    TextView tv_realEdit_name;

    @InjectView(R.id.tv_realEdit_ageTwo)
    TextView tv_realEdit_ageTwo;

    @InjectView(R.id.tv_realEdit_signatureTwo)
    TextView tv_realEdit_signatureTwo;

    @InjectView(R.id.rl_realEdit_myBusiness)
    RelativeLayout rl_realEdit_myBusiness;

    @InjectView(R.id.tv_realEdit_city)
    TextView tv_realEdit_city;

    @InjectView(R.id.tv_realEdit_business)
    TextView tv_realEdit_business;

    @InjectView(R.id.rl_realEdit_decided)
    RelativeLayout rl_realEdit_decided;

    @InjectView(R.id.pb_realEdit_pb)
    ProgressBar pb_realEdit_pb;

    @InjectView(R.id.rg_realEdit_RadioGroup)
    RadioGroup rg_realEdit_RadioGroup;

    @InjectView(R.id.tv_realEdit_one)
    TextView tv_realEdit_one;
    //用来占位代替男和女
    @InjectView(R.id.tv_realEdit_finalSex)
    TextView tv_realEdit_finalSex;

    @InjectView(R.id.tv_realEdit_two)
    TextView tv_realEdit_two;

    @InjectView(R.id.tv_realEdit_three)
    TextView tv_realEdit_three;

    @InjectView(R.id.tv_realEdit_four)
    TextView tv_realEdit_four;

    private PopupWindow mPopupWindow;

    //最后的提交
    private String modificationUrl = Constants.commontUrl + "personal/edit";
    //上传图片
    private String uploadHeadimgUrl = Constants.commontUrl + "personal/upload_headimg";
    //请求用户详情信息的url
    private String userInfoUrl = Constants.commontUrl + "personal/info";

    private String mUserId;

    private int sexIndex = 1;

    private int picIndex;//0默认图片库
    private String mPicturePath;
    private boolean isSelectPic;
    private String mHead_img;
    private String mArea_id = new String();
    private String mFromIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_edit);
        initView();
        initData();
        initEvent();

    }

    private void initView() {
        ButterKnife.inject(this);

        //判断从哪里进来的(可能从三个不同地方近来)
        mFromIndex = getIntent().getStringExtra("fromIndex");
        //默认男生
        rb_realEdit_manSex.setChecked(true);
        //得到用户ID
        mUserId = SpUtils.getCacheString(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.BUYERID, "");

        boolean isSettingUserInfo = SpUtils.getCacheBoolean(MyConstants.SETTINGSPNAME, UIUtils.getContext(), MyConstants.ISSETTINGUSERINFO, false);
        if (isSettingUserInfo) {
            //隐藏星星
            tv_realEdit_one.setVisibility(View.GONE);
            tv_realEdit_two.setVisibility(View.GONE);
            tv_realEdit_three.setVisibility(View.GONE);
            tv_realEdit_four.setVisibility(View.GONE);
            //显示返回键
            rl_realEdit_back.setVisibility(View.VISIBLE);
            iv_back.setVisibility(View.VISIBLE);
            //隐藏RadioGroup
            rg_realEdit_RadioGroup.setVisibility(View.GONE);
            tv_realEdit_finalSex.setVisibility(View.VISIBLE);
        } else {
            tv_realEdit_one.setVisibility(View.VISIBLE);
            tv_realEdit_two.setVisibility(View.VISIBLE);
            tv_realEdit_three.setVisibility(View.VISIBLE);
            tv_realEdit_four.setVisibility(View.VISIBLE);
            rg_realEdit_RadioGroup.setVisibility(View.VISIBLE);
            //隐藏返回键
            rl_realEdit_back.setVisibility(View.GONE);
            iv_back.setVisibility(View.GONE);
            //显示一个TextView来显示男和女
            tv_realEdit_finalSex.setVisibility(View.GONE);
        }
    }

    private void initData() {
        //判断网络
        boolean iSNetworkConnect = ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext());
        if (!iSNetworkConnect) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
            return;
        } else {
            requestNetworkUserInfoData();
        }
    }

    private void initEvent() {
        //返回
        BackListenerUtils.backFinish(rl_realEdit_back, this);
        //头像
        rl_realEdit_touxiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出pop
                setPop();
            }
        });
        //昵称
        rl_realEdit_niCheng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customNiChengDialog();
            }
        });
        //年龄
        rl_realEdit_age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAgeDialog();
            }
        });
        //个性签名
        rl_realEdit_moko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customMokoDialog();
            }
        });
        //我的商圈
        rl_realEdit_myBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //与注册一样
                Intent intent = new Intent(RealEditActivity.this, SelectCityActivity.class);
                intent.putExtra("fromRegister", true);
                startActivityForResult(intent, 1);
            }
        });
        //确定
        rl_realEdit_decided.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断网络
                boolean iSNetworkConnect = ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext());
                if (!iSNetworkConnect) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                    return;
                }
                    // else if (TextUtils.isEmpty(mHead_img)) {
//                   //根据mHead_img返回给我的信息来判断是否有头像
//                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请选择头像");
//                    return;
//                }
                    else {
                    requestNetwork();
                }
            }
        });
        //性别
        rg_realEdit_RadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_realEdit_manSex:
                        sexIndex = 1;
                        break;
                    case R.id.rb_realEdit_girlSex:
                        sexIndex = 2;
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 请求网络提交
     */
    private void requestNetwork() {
        File picFile = getPicFile();
        if (picFile != null) {
            //上传头像并且提交（选择了头像才走的）
            uploadFile(uploadHeadimgUrl, picFile);
        }else {
            //没有选择头像也可以上传
            //进度条
            pb_realEdit_pb.setVisibility(View.VISIBLE);
            //关闭点击
            cancelClick();
            OkHttpUtils.post().url(modificationUrl).addParams("user_id", mUserId)
                    .addParams("username", tv_realEdit_name.getText().toString().trim())
                    .addParams("sex", String.valueOf(sexIndex))
                    .addParams("age", tv_realEdit_ageTwo.getText().toString().trim())
                    .addParams("description", tv_realEdit_signatureTwo.getText().toString().trim())
                    .addParams("area_id",mArea_id)
                    .build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    pb_realEdit_pb.setVisibility(View.GONE);
                    //恢复编辑状态
                    recoverClick();
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                }

                @Override
                public void onResponse(String response, int id) {
                    operationResult(response);
                }
            });
        }
    }

    /**
     * 一进来就初始化数据
     */
    private void requestNetworkUserInfoData() {
        //进度条
        pb_realEdit_pb.setVisibility(View.VISIBLE);
        //关闭点击
        cancelClick();
        OkHttpUtils.get().url(userInfoUrl).addParams("user_id", mUserId)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                pb_realEdit_pb.setVisibility(View.GONE);
                //恢复编辑状态
                recoverClick();
                ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    pb_realEdit_pb.setVisibility(View.GONE);
                    //恢复编辑状态
                    recoverClick();
                    // 成功了，只是返回来请求的json数据,还需要解析
                    Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                    String response_code = map.get("response_code");
                    //需要判断各种响应码,获取具体错误信息
                    String responseContent = RequestResponseUtils.getResponseContent(response_code);

                    if ("0".equals(response_code)) {
                        //成功
                        Map<String, String> userInfoParseMap = UserInfoDetailsParse.userInfoDetailsParse(response);

                        Map<String, String> userInfoCityParseMap = UserInfoDetailsParse.userInfoCityDetailsParse(response);

                        Map<String, String> userInfoBusinessParseMap = UserInfoDetailsParse.userInfoBusinessDetailsParse(response);

                        if (userInfoParseMap != null) {
                            setUserInfoParseData(userInfoParseMap);
                        }
                        //设置城市和商圈
                        String city = userInfoCityParseMap.get("city");
                        String businessName = userInfoBusinessParseMap.get("businessName");
                        if (!TextUtils.isEmpty(city)) {
                            tv_realEdit_city.setText(city);
                        }
                        if (!TextUtils.isEmpty(businessName)) {
                            tv_realEdit_business.setText(businessName);
                        }
                    } else if (responseContent != null) {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                    }
                } catch (Exception e) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                    //动画关闭
                    pb_realEdit_pb.setVisibility(View.GONE);
                    //恢复编辑状态
                    recoverClick();
                }
            }
        });
    }

    /**
     * 上传单个文件并提交
     */
    public void uploadFile(String url, File file) {
        //进度条
        pb_realEdit_pb.setVisibility(View.VISIBLE);

        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", mUserId);

        OkHttpUtils.post()
                .addFile("file", "head.jpg", file)
                .url(url)//
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), "上传图片失败");
                        pb_realEdit_pb.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        // 成功了上传图片
                        Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                        String response_code = map.get("response_code");
                        if (!"0".equals(response_code)) {
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), "上传图片失败");
                            pb_realEdit_pb.setVisibility(View.GONE);
                        } else {
//                            解析得到头像url
                            Map<String, String> contentParseMap = ContentParse.contentParse(response);

                            //保存头像url，视频聊天的时候需要展示头像
                            SpUtils.putSpString(UIUtils.getContext(),MyConstants.HEADIMGURL,contentParseMap.get("content"));
                            //上传成功在请求
                            //关闭点击
                            cancelClick();
                            OkHttpUtils.post().url(modificationUrl).addParams("user_id", mUserId)
                                    .addParams("head_img",contentParseMap.get("content"))
                                    .addParams("username", tv_realEdit_name.getText().toString().trim())
                                    .addParams("sex", String.valueOf(sexIndex))
                                    .addParams("age", tv_realEdit_ageTwo.getText().toString().trim())
                                    .addParams("description", tv_realEdit_signatureTwo.getText().toString().trim())
                                    .addParams("area_id",mArea_id)
                                    .build().execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    pb_realEdit_pb.setVisibility(View.GONE);
                                    //恢复编辑状态
                                    recoverClick();
                                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    operationResult(response);
                                }
                            });
                        }
                    }
                });
    }

    /**
     * 处理返回的结果
     */
    private void operationResult(String response) {
        try {
            pb_realEdit_pb.setVisibility(View.GONE);
            //恢复编辑状态
            recoverClick();
            // 成功了，只是返回来请求的json数据,还需要解析
            Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
            String response_code = map.get("response_code");
            //需要判断各种响应码,获取具体错误信息
            String responseContent = RequestResponseUtils.getResponseContent(response_code);

            if ("0".equals(response_code)) {
                //成功
                ToastUtils.showToastInUIThread(UIUtils.getContext(), "保存成功");
                //保存用户的昵称
                SpUtils.putCacheString(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.USERNAME, tv_realEdit_name.getText().toString().trim());

                if (!SpUtils.getCacheBoolean(MyConstants.SETTINGSPNAME, UIUtils.getContext(), MyConstants.ISSETTINGUSERINFO, false)) {
                    //进入主界面
                    Intent intent = new Intent(RealEditActivity.this, MainActivity.class);
                    intent.putExtra("fragmentIndex", 0);
                    //设置目的，防止按返回键再次进入MainActivity
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    RealEditActivity.this.startActivity(intent);
                }else if("1".equals(mFromIndex)) {
                    //中间还有一个界面，需要关闭自己并且刷新上一个界面
                    EditPersonActivity.mEditPersonActivity.refreshEditUserData();
                }
                //保存那个已经设置了个人信息（必须放到判断的后面）
                SpUtils.putCacheBoolean(MyConstants.SETTINGSPNAME, UIUtils.getContext(), MyConstants.ISSETTINGUSERINFO, true);

                //都需要进行关闭(相当于用户设置过信息，就关闭)
                RealEditActivity.this.finish();
            } else if (responseContent != null) {
                ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
            }
        } catch (Exception e) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
            //动画关闭
            pb_realEdit_pb.setVisibility(View.GONE);
            //恢复编辑状态
            recoverClick();
        }
    }

    /**
     * 恢复点击
     */
    private void recoverClick() {
        rl_realEdit_touxiang.setClickable(true);
        rl_realEdit_niCheng.setClickable(true);
        rl_realEdit_sexRelativeLayout.setClickable(true);
        rl_realEdit_age.setClickable(true);
        rl_realEdit_moko.setClickable(true);
        rl_realEdit_myBusiness.setClickable(true);
    }

    /**
     * 关闭点击
     */
    private void cancelClick() {
        rl_realEdit_touxiang.setClickable(false);
        rl_realEdit_niCheng.setClickable(false);
        rl_realEdit_sexRelativeLayout.setClickable(false);
        rl_realEdit_age.setClickable(false);
        rl_realEdit_moko.setClickable(false);
        rl_realEdit_myBusiness.setClickable(false);
    }

    /**
     * 弹出签名
     */
    private void customMokoDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(RealEditActivity.this);
        View inflate = View.inflate(RealEditActivity.this, R.layout.custom_mokoalert_dialog, null);
        final ClearEditText mokoCedit = (ClearEditText) inflate.findViewById(R.id.ct_moko_name);
        mokoCedit.setText(tv_realEdit_signatureTwo.getText().toString().trim());
        mokoCedit.setSelection(tv_realEdit_signatureTwo.getText().toString().length());
        //取消
        Button canleButton = (Button) inflate.findViewById(R.id.b_moko_canle);
        //确定
        Button decidedButton = (Button) inflate.findViewById(R.id.b_moko_decided);
        //弹出输入法
        displayInput(mokoCedit);
        mBuilder.setView(inflate);
        final AlertDialog alertDialog = mBuilder.create();
        alertDialog.show();

        canleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        decidedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String moko = mokoCedit.getText().toString().trim();
                if(!TextUtils.isEmpty(moko)) {
                    alertDialog.dismiss();
                    tv_realEdit_signatureTwo.setText(moko);
                }else {
                    mokoCedit.setShakeAnimation();
                    ToastUtils.showToastInUIThread(UIUtils.getContext(),"内容不能为空");
                    return;
                }
            }
        });
    }

    /**
     * 弹出年龄
     */
    private void customAgeDialog() {
        Calendar calendar = Calendar.getInstance();
        final int yearNow = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        final int monthNow = month + 1;
        final int dayOfMonthNow = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dDialog = new DatePickerDialog(RealEditActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // String dateString = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        int age = yearNow - year;
                        if (monthNow <= (monthOfYear + 1)) {
                            if (monthNow == (monthOfYear + 1)) {
                                // monthNow==monthBirth
                                if (dayOfMonthNow < dayOfMonth) {
                                    age--;
                                }
                            } else {
                                // monthNow>monthBirth
                                age--;
                            }
                        }
                        if (age <= 0) {
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), "请输入合法的年龄");
                            tv_realEdit_ageTwo.setText(String.valueOf(18));
                            return;
                        } else {
                            tv_realEdit_ageTwo.setText(String.valueOf(age));
                        }
                    }
                }, yearNow, month, dayOfMonthNow);
        dDialog.show();
    }

    /**
     * 弹出对话框昵称
     */
    private void customNiChengDialog() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(RealEditActivity.this);
        View inflate = View.inflate(RealEditActivity.this, R.layout.custom_nichengalert_dialog, null);
        final ClearEditText nameCedit = (ClearEditText) inflate.findViewById(R.id.ct_niCheng_name);
        nameCedit.setText(tv_realEdit_name.getText().toString().trim());
        nameCedit.setSelection(tv_realEdit_name.getText().toString().length());
        //取消
        Button canleButton = (Button) inflate.findViewById(R.id.b_niCheng_canle);
        //确定
        Button decidedButton = (Button) inflate.findViewById(R.id.b_niCheng_decided);
        //弹出输入法
        displayInput(nameCedit);
        mBuilder.setView(inflate);
        final AlertDialog alertDialog = mBuilder.create();
        alertDialog.show();

        canleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        decidedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameCedit.getText().toString().trim();
                if(!TextUtils.isEmpty(name)) {
                    alertDialog.dismiss();
                    tv_realEdit_name.setText(name);
                }else {
                    nameCedit.setShakeAnimation();
                    ToastUtils.showToastInUIThread(UIUtils.getContext(),"内容不能为空");
                    return;
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
     * 设置头像pop
     */
    private void setPop() {
        View popupwindowView = getLayoutInflater().inflate(R.layout.edit_pic_pop, null);
        //图片库
        TextView mPicKu = (TextView) popupwindowView.findViewById(R.id.tv_popupwindow_picKu);
        //拍照
        TextView mPic = (TextView) popupwindowView.findViewById(R.id.tv_popupwindow_pic);
        //取消
        TextView mTv_cancel = (TextView) popupwindowView.findViewById(R.id.tv_popupwindow_cancel);

        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(popupwindowView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        } else {
            // 设置一个透明的背景，不然无法实现点击弹框外，弹框消失
            //popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popupwindow));
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            // 设置点击弹框外部，弹框消失
            mPopupWindow.setOutsideTouchable(true);
            // 设置焦点
            mPopupWindow.setFocusable(true);
            // 设置所在布局
            // x和y的单位是像素
            mPopupWindow.showAtLocation(popupwindowView, Gravity.BOTTOM, 0, 15);
        }
        //取消
        mTv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
            }
        });
        //拍照
        mPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 23);
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
            }
        });
        //图库
        mPicKu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                } else {
                    intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                }
                startActivityForResult(intent, 33);
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //系统照相
        if (resultCode == Activity.RESULT_OK && requestCode == 23) {
            String sdStatus = Environment.getExternalStorageState();
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                ToastUtils.showToastInUIThread(UIUtils.getContext(), "SD卡不可用");
                return;
            }
            //            String name = new DateFormat().format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
            if (data != null) {
                Bundle bundle = data.getExtras();
                // 获取相机返回的数据，并转换为Bitmap图片格式
                Bitmap mBitmap = (Bitmap) bundle.get("data");
                FileOutputStream b = null;
                //为什么不能直接保存在系统相册位置呢?
                File file = new File(Environment.getExternalStorageDirectory(), "myImage");
                if (!file.exists()) {
                    file.mkdirs();// 创建文件夹
                }
                String fileName = file.getAbsolutePath() + File.separator + "head.jpg";

                try {
                    b = new FileOutputStream(fileName);
                    mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (b != null) {
                            b.flush();
                            b.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (mBitmap != null) {
                    picIndex = 1;
                    isSelectPic = true;
                    iv_realEdit_userImg.setImageBitmap(mBitmap);// 将图片显示在ImageView里
                }
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == 33) {
            //图库
            try {
                //                Bitmap bm = null;
                // 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
                //ContentResolver resolver = getContentResolver();

                if (data != null) {
                    Uri selectedImage = data.getData();

                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        mPicturePath = cursor.getString(columnIndex);
                        cursor.close();
                        cursor = null;

                        if (mPicturePath == null || mPicturePath.equals("null")) {
                            ToastUtils.showToastInUIThread(UIUtils.getContext(),"图片路径有问题");
                            return;
                        }
                        if(mPicturePath != null) {
                            picIndex = 0;
                            isSelectPic = true;
                            //Bitmap bitmap = BitmapFactory.decodeFile(mPicturePath);
                            //iv_realEdit_userImg.setImageBitmap(bitmap);
                            //显示内容提供者的图片
                            ImageLoader.getInstance().displayImage(String.valueOf(selectedImage), iv_realEdit_userImg, GetNormalOptionsUtils.getNormalOptionsUtils());
                            //显示内容提供者的图片
                            ImageLoader.getInstance().displayImage(String.valueOf(selectedImage), iv_realEdit_userImg, GetNormalOptionsUtils.getNormalOptionsUtils());
                        }
                    }else {
                        //不得已才走这，因为图片存的好大
                        ContentResolver resolver = getContentResolver();
                        // 获得图片的uri
                        Uri mMOriginalUri = data.getData();
                        Bitmap mBitmap = MediaStore.Images.Media.getBitmap(resolver, mMOriginalUri);
                        FileOutputStream b = null;
                        //为什么不能直接保存在系统相册位置呢?
                        File file = new File(Environment.getExternalStorageDirectory(), "myImage");
                        if (!file.exists()) {
                            file.mkdirs();// 创建文件夹
                        }
                        String fileName = file.getAbsolutePath() + File.separator + "head.jpg";
                        try {
                            b = new FileOutputStream(fileName);
                            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (b != null) {
                                    b.flush();
                                    b.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (mBitmap != null) {
                            picIndex = 1;
                            isSelectPic = true;
                            // iv_realEdit_userImg.setImageBitmap(ThumbnailUtils.extractThumbnail(mBitmap, 100, 100));
                            //iv_realEdit_userImg.setImageBitmap(ThumbnailUtils.extractThumbnail(mBitmap, 100, 100));
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
        if (requestCode == 1 && resultCode == 22) {
            if (data != null) {
                //得到城市名和商圈名
                String mCityName = data.getStringExtra("cityName");
                String mBusinessName = data.getStringExtra("businessName");
                mArea_id = data.getStringExtra("area_id");
                if (!TextUtils.isEmpty(mCityName)) {
                    tv_realEdit_city.setText(mCityName);
                }
                if (!TextUtils.isEmpty(mBusinessName)) {
                    tv_realEdit_business.setText(mBusinessName);
                }
            }
        }
    }

    //重写返回键（如果第一次进来，没按确定，返回键直接退出app）
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && !SpUtils.getCacheBoolean(MyConstants.SETTINGSPNAME, UIUtils.getContext(), MyConstants.ISSETTINGUSERINFO, false)) {
            //退出app
            BaseApplication.getInstance().closeActivity();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 得到图片的地址file
     */
    public File getPicFile() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "SD卡不可用");
            return null;
        }
        File file = null;
        if(picIndex == 0 && !TextUtils.isEmpty(mPicturePath)) {//图库
            file = new File(mPicturePath);
        }else if(picIndex == 1) {//拍照
            File fileDir = new File(Environment.getExternalStorageDirectory(), "myImage");
            file = new File(fileDir, "head.jpg");
            if (!file.exists()) {
                ToastUtils.showToastInUIThread(UIUtils.getContext(), "图片不存在，请重试");
                return null;
            }
        }
        return file;
    }

    /**
     * 设置解析得到yonghu信息的数据
     */
    private void setUserInfoParseData(Map<String, String> buyerInfoMap) {
        //设置数据(username sex age head_img status description followInfo fanInfo friendInfo orderInfo)
        //用户的昵称
        tv_realEdit_name.setText(buyerInfoMap.get("username"));
        //用户的性别
        String sex = buyerInfoMap.get("sex");
        if (!TextUtils.isEmpty(sex)) {
            choiceBuyerSex(Integer.parseInt(sex));
        }
        //age
        tv_realEdit_ageTwo.setText(buyerInfoMap.get("age"));

        //buyer的头像
        mHead_img = buyerInfoMap.get("head_img");
        if(!TextUtils.isEmpty(mHead_img)) {
            ImageLoader.getInstance().displayImage(mHead_img, iv_realEdit_userImg, GetNormalOptionsUtils.getNormalOptionsUtils());
        }
        //保存头像url，视频聊天的时候需要展示头像
        SpUtils.putSpString(UIUtils.getContext(),MyConstants.HEADIMGURL,mHead_img);
        //个性签名
        String description = buyerInfoMap.get("description");
        if (!TextUtils.isEmpty(description)) {
            tv_realEdit_signatureTwo.setText(description);
        }
    }

    /**
     * 选择性别 1:男 2:女
     */
    private void choiceBuyerSex(int index) {
        switch (index) {
            case 1:
                tv_realEdit_finalSex.setText("男");
                break;
            case 2:
                tv_realEdit_finalSex.setText("女");
                break;
            default:
                break;
        }
    }
}
