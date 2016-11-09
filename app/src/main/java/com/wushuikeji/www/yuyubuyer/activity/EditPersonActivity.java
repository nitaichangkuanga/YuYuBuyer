package com.wushuikeji.www.yuyubuyer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.jsonparse.RealNameParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.SendAuthoCodeParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.UserInfoDetailsParse;
import com.wushuikeji.www.yuyubuyer.utils.BackListenerUtils;
import com.wushuikeji.www.yuyubuyer.utils.GetNormalOptionsUtils;
import com.wushuikeji.www.yuyubuyer.utils.ISNetworkConnectUtils;
import com.wushuikeji.www.yuyubuyer.utils.RequestResponseUtils;
import com.wushuikeji.www.yuyubuyer.utils.SpUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToNextActivityUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToastUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

public class EditPersonActivity extends AppCompatActivity {

    @InjectView(R.id.tv_edit_edit)
    TextView tv_edit_edit;

    @InjectView(R.id.rl_edit_back)
    RelativeLayout rl_edit_back;

    @InjectView(R.id.iv_edit_img)
    ImageView iv_edit_img;

    @InjectView(R.id.tv_edit_name)
    TextView tv_edit_name;

    @InjectView(R.id.tv_editPerson_title)
    TextView tv_editPerson_title;

    @InjectView(R.id.tv_edit_userName)
    TextView tv_edit_userRealName;

    @InjectView(R.id.tv_edit_buyerId)
    TextView tv_edit_buyerId;

    @InjectView(R.id.tv_edit_sex)
    TextView tv_edit_sex;

    @InjectView(R.id.tv_edit_age)
    TextView tv_edit_age;

    @InjectView(R.id.tv_edit_signature)
    TextView tv_edit_signature;

    @InjectView(R.id.tv_edit_phone)
    TextView tv_edit_phone;

    @InjectView(R.id.tv_edit_city)
    TextView tv_edit_city;

    @InjectView(R.id.tv_realEdit_location)
    TextView tv_realEdit_location;

    public static EditPersonActivity mEditPersonActivity;

    //用户信息详情的地址
    private String userInfoDetailsUrl = Constants.commontUrl + "personal/info";
    //得到用户真实姓名的地址
    private String userRealNameUrl = Constants.commontUrl + "auth/check";

    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_person);
        initView();
        //加载认证的网络(加载一次就保存下来，省流量)
        requestAutoGetRealName();
        initData();
        initEvent();
    }

    private void initView() {
        ButterKnife.inject(this);

        mEditPersonActivity = this;
        //得到用户ID
        mUserId = SpUtils.getCacheString(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.BUYERID, "");
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
        //返回
        BackListenerUtils.backFinish(rl_edit_back,this);
        //编辑事件
        tv_edit_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //从编辑界面进来的(1)
                ToNextActivityUtils.toNextActivityNotFinishAndParameters(EditPersonActivity.this,RealEditActivity.class,"fromIndex","1");
            }
        });
    }

    private void loadNetworkData() {

        OkHttpUtils.get().url(userInfoDetailsUrl).addParams("user_id", mUserId)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
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
                        //可以下一步解析个人信息
                        Map<String, String> userInfoParseMap = UserInfoDetailsParse.userInfoDetailsParse(response);
                        //可以解析商圈
                        Map<String, String> cityMap = UserInfoDetailsParse.userInfoCityDetailsParse(response);
                        Map<String, String> locationMap = UserInfoDetailsParse.userInfoBusinessDetailsParse(response);
                        //设置用户信息
                        //设置标题
                        tv_editPerson_title.setText(userInfoParseMap.get("username"));
                        //昵称
                        tv_edit_name.setText(userInfoParseMap.get("username"));
                        //BUYER ID
                        tv_edit_buyerId.setText(userInfoParseMap.get("buyerId"));
                        //sex
                        String sex = userInfoParseMap.get("sex");
                        if("1".equals(sex)) {
                            tv_edit_sex.setText("男");
                        }else if("2".equals(sex)) {
                            tv_edit_sex.setText("女");
                        }
                        //age
                        tv_edit_age.setText(userInfoParseMap.get("age"));
                        //个性签名
                        tv_edit_signature.setText(userInfoParseMap.get("description"));
                        //phone
                        tv_edit_phone.setText(userInfoParseMap.get("mobile"));
                        //设置商圈
                        tv_edit_city.setText(cityMap.get("city"));
                        tv_realEdit_location.setText(locationMap.get("businessName"));
                        //头像
                        ImageLoader.getInstance().displayImage(userInfoParseMap.get("head_img"),iv_edit_img, GetNormalOptionsUtils.getNormalOptionsUtils());
                    } else if (responseContent != null) {
                        //给出错误提示
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                    }
                } catch (Exception e) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                }
            }
        });
    }

    /**
     * 为了得到真实的姓名
     */
    private void requestAutoGetRealName() {
        if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
            return;
        }else {
            String cacheRealName = SpUtils.getCacheString(MyConstants.REALUSERSPNAME, UIUtils.getContext(), MyConstants.REALUSER, "");
            if(!TextUtils.isEmpty(cacheRealName)) {//从缓存中取数据
                //设置数据
                tv_edit_userRealName.setText(cacheRealName);
            }else {
                OkHttpUtils.post().url(userRealNameUrl).addParams("user_id", mUserId)
                        .build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            //先解析reponse_code
                            Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                            String response_code = map.get("response_code");
                            if ("0".equals(response_code)) {
                                //可以解析商圈
                                Map<String, String> realNameMap = RealNameParse.realNameParse(response);
                                String real_name = realNameMap.get("real_name");
                                if(!TextUtils.isEmpty(real_name)) {
                                    //保存真实姓名，省流量
                                    SpUtils.putCacheString(MyConstants.REALUSERSPNAME,UIUtils.getContext(),MyConstants.REALUSER,real_name);
                                    //设置数据
                                    tv_edit_userRealName.setText(real_name);
                                }
                            }
                        } catch (Exception e) {
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                        }
                    }
                });
            }
        }
    }
    /**
     * 提供给外部刷新
     */
    public void refreshEditUserData() {
        requestAutoGetRealName();
        loadNetworkData();
    }
}
