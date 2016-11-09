package com.wushuikeji.www.yuyubuyer.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.adapter.SelectBusinessListViewAdapter;
import com.wushuikeji.www.yuyubuyer.base.BaseApplication;
import com.wushuikeji.www.yuyubuyer.bean.SelectCityBean;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.jsonparse.SelectCityParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.SendAuthoCodeParse;
import com.wushuikeji.www.yuyubuyer.utils.BackListenerUtils;
import com.wushuikeji.www.yuyubuyer.utils.ISNetworkConnectUtils;
import com.wushuikeji.www.yuyubuyer.utils.RequestResponseUtils;
import com.wushuikeji.www.yuyubuyer.utils.SpUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToastUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

public class SelectBusinessActivity extends AppCompatActivity {

    @InjectView(R.id.rl_selectBusiness_cancle)
    RelativeLayout rl_selectBusiness_cancle;

    @InjectView(R.id.lv_selectBusiness)
    ListView lv_selectBusiness_listView;

    @InjectView(R.id.rl_selectBusiness_back)
    RelativeLayout rl_selectBusiness_back;

    @InjectView(R.id.tv_selectBusiness_emptyView)
    TextView tv_selectBusiness_emptyView;

    @InjectView(R.id.pb_selectBusinessLoading_progressBar)
    ProgressBar pb_selectBusinessLoading_progressBar;

    private SelectBusinessListViewAdapter mSelectCityListViewAdapter;

    private List<SelectCityBean> mSelectBusinessList = new ArrayList<SelectCityBean>();
    private int mFragmentIndex;
    private String cityName;

    //获取商圈Url
    private String businessUrl = Constants.commontUrl + "area/cityarea";
    //sp的名字和城市名字一样，因为退出app都要清除
    private static final String CACHECITYNAME = "cacheCityName";
    private SharedPreferences mSharedPreferences;
    private SharedPreferences mBuyerSharedPreferences;
    private static final int LOADING = 0;
    private static final int SUCCESS = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pb_selectBusinessLoading_progressBar.setVisibility(View.VISIBLE);
                    tv_selectBusiness_emptyView.setVisibility(View.GONE);
                    lv_selectBusiness_listView.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    if (mSelectBusinessList.size() != 0) {
                        pb_selectBusinessLoading_progressBar.setVisibility(View.GONE);
                        tv_selectBusiness_emptyView.setVisibility(View.GONE);
                        lv_selectBusiness_listView.setVisibility(View.VISIBLE);

                        if (mSelectCityListViewAdapter != null) {
                            mSelectCityListViewAdapter.notifyDataSetChanged();
                        }
                    } else {
                        pb_selectBusinessLoading_progressBar.setVisibility(View.GONE);
                        tv_selectBusiness_emptyView.setVisibility(View.VISIBLE);
                        lv_selectBusiness_listView.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private String mCityId;
    private boolean mFromRegisterBoolean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_business);
        initView();
        initData();
        initEvent();
    }
    private void initView() {
        ButterKnife.inject(this);
        Intent intent = getIntent();
        //得到是从哪个fragment过来的
        mFragmentIndex = intent.getIntExtra("fragmentIndex", -1);
        //得到城市名
        cityName = intent.getStringExtra("cityName");
        //得到从商圈过来的城市的ID
        mCityId = intent.getStringExtra("cityId");
        //得到是否从注册界面过来的
        mFromRegisterBoolean = intent.getBooleanExtra("fromRegister",false);
        //进入主界面就必须删除之前的商铺缓存
        mSharedPreferences = getSharedPreferences(MyConstants.SHOPSPNAME, Context.MODE_PRIVATE);
        mBuyerSharedPreferences = getSharedPreferences(MyConstants.BUYERSPNAME, Context.MODE_PRIVATE);
        //统一管理
        BaseApplication.getInstance().addActivity(this);

        mSelectCityListViewAdapter = new SelectBusinessListViewAdapter(mSelectBusinessList);
        lv_selectBusiness_listView.setAdapter(mSelectCityListViewAdapter);
    }

    private void initData() {
        //判断网络
        boolean iSNetworkConnect = ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext());

        //先判断缓存中是否有数据
        String jsonString = SpUtils.getCacheString(CACHECITYNAME,UIUtils.getContext(), "businessJsonString", "");
        mSelectBusinessList.clear();
        if(TextUtils.isEmpty(jsonString)) {
            //为空，在需要判断网络,最后去解析
            if (!iSNetworkConnect) {
                ToastUtils.showToastInUIThread(UIUtils.getContext(),"您尚未开启网络");
                return;
            }else {
                //进度条展示
                mHandler.obtainMessage(LOADING).sendToTarget();
                //网络请求
                OkHttpUtils.get().url(businessUrl).addParams("city_id", mCityId).build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(),"请求超时");
                        //发送handler，隐藏进度条
                        mHandler.obtainMessage(SUCCESS).sendToTarget();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        //先解析reponse_code,给客户提醒
                        Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                        String response_code = map.get("response_code");
                        //需要判断各种响应码,获取具体错误信息
                        String responseContent = RequestResponseUtils.getResponseContent(response_code);
                        if("0".equals(response_code)) {
                            //单独保存好删除
                            SpUtils.putCacheString(CACHECITYNAME,UIUtils.getContext(),"businessJsonString",response);
                            //可以下一步解析商圈
                            List<SelectCityBean> selectList = SelectCityParse.selectCityParse(response);
                            for(SelectCityBean selectBusinessBean: selectList) {
                                SelectCityBean mSelectCityBean = new SelectCityBean();
                                mSelectCityBean.cityName = selectBusinessBean.cityName;
                                mSelectCityBean.id = selectBusinessBean.id;
                                mSelectBusinessList.add(mSelectCityBean);
                            }

                        }else if(responseContent != null) {
                            //给出错误提示
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                        }
                        //发送handler，隐藏进度条
                        mHandler.obtainMessage(SUCCESS).sendToTarget();
                    }
                });
            }
        }else {
            //有缓存，进行解析显示
            List<SelectCityBean> list = SelectCityParse.selectCityParse(jsonString);
            for(SelectCityBean selectBusinessBean: list) {
                SelectCityBean mSelectCityBean = new SelectCityBean();
                mSelectCityBean.cityName = selectBusinessBean.cityName;
                mSelectCityBean.id = selectBusinessBean.id;
                mSelectBusinessList.add(mSelectCityBean);
            }
            //发送handler，为了可以执行自动跳转的代码
            mHandler.obtainMessage(SUCCESS).sendToTarget();
        }
    }

    private void initEvent() {
        //取消(关闭自己和选择城市)
        rl_selectBusiness_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BaseApplication.getInstance().closeActivity();
            }
        });
        //返回
        BackListenerUtils.backFinish(rl_selectBusiness_back,this);

        //listView点击
        lv_selectBusiness_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //需要判断用户点了哪个商圈，因为在适配器里加了一个全城
                if(position == 0) {
                    //用户点了全城
                    //在判断需要跳转到哪，是主界面还是注册界面
                    if(mFromRegisterBoolean) {
                        //往回传值，一直到注册界面
                        Intent mIntent = getIntent();
                        //这个area_id,注册需要的,然而解析并没有这数据，这是默认添加的
                        mIntent.putExtra("area_id", "0");
                        mIntent.putExtra("cityName",cityName);
                        mIntent.putExtra("businessName","全城");
                        setResult(3,mIntent);
                        finish();
                    }else {
                        //主界面
                        Intent intent = new Intent();
                        intent.setClass(SelectBusinessActivity.this,MainActivity.class);
                        intent.putExtra("fragmentIndex",mFragmentIndex);
                        //设置目的，防止按返回键再次进入MainActivity
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("cityName",cityName);
                        intent.putExtra("businessName","全城");
                        intent.putExtra("area_id", "0");
                        startActivity(intent);
                        if(mFragmentIndex == 0) {
                            //退出之前，清空保存商铺的缓存，因为不清除，缓存都存在，那么商铺信息永远得不到刷新
                            mSharedPreferences.edit().clear().commit();
                        }
                        if(mFragmentIndex == 1) {
                            //退出之前，清空保存buyer的缓存，因为不清除，缓存都存在，那么商铺信息永远得不到刷新
                            mBuyerSharedPreferences.edit().clear().commit();
                        }
                        //关闭
                        BaseApplication.getInstance().closeActivity();
                    }
                }else {
                    //数据源的数据来自网络
                    //判断需要跳转到哪，是主界面还是注册界面
                    if(mFromRegisterBoolean) {
                        //往回传值，一直到注册界面
                        Intent mIntent = getIntent();
                        mIntent.putExtra("area_id", mSelectBusinessList.get(position-1).id);
                        mIntent.putExtra("cityName",cityName);
                        mIntent.putExtra("businessName",mSelectBusinessList.get(position-1).cityName);
                        setResult(3,mIntent);
                        finish();
                    }else {
                        //主界面
                        Intent intent = new Intent();
                        intent.setClass(SelectBusinessActivity.this,MainActivity.class);
                        intent.putExtra("fragmentIndex",mFragmentIndex);
                        //设置目的，防止按返回键再次进入MainActivity
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("cityName",cityName);
                        intent.putExtra("businessName",mSelectBusinessList.get(position-1).cityName);
                        intent.putExtra("area_id", mSelectBusinessList.get(position-1).id);
                        startActivity(intent);
                        if(mFragmentIndex == 0) {
                            //退出之前，清空保存商铺的缓存，因为不清除，缓存都存在，那么商铺信息永远得不到刷新
                            mSharedPreferences.edit().clear().commit();
                        }
                        if(mFragmentIndex == 1) {
                            //退出之前，清空保存buyer的缓存，因为不清除，缓存都存在，那么商铺信息永远得不到刷新
                            mBuyerSharedPreferences.edit().clear().commit();
                        }
                        BaseApplication.getInstance().closeActivity();
                    }
                }
            }
        });
    }
}
