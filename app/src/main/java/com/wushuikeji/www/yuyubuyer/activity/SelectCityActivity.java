package com.wushuikeji.www.yuyubuyer.activity;

import android.content.Intent;
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
import com.wushuikeji.www.yuyubuyer.adapter.SelectCityListViewAdapter;
import com.wushuikeji.www.yuyubuyer.base.BaseApplication;
import com.wushuikeji.www.yuyubuyer.bean.SelectCityBean;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
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

public class SelectCityActivity extends AppCompatActivity {

    @InjectView(R.id.rl_select_cancleRl)
    RelativeLayout rl_select_cancleRl;

    @InjectView(R.id.lv_select_listView)
    ListView lv_select_listView;

    //空视图
    @InjectView(R.id.tv_selectCity_emptyView)
    TextView tv_selectCity_emptyView;

    //进度条
    @InjectView(R.id.pb_selectCityLoading_progressBar)
    ProgressBar pb_selectCityLoading_progressBar;


    private SelectCityListViewAdapter mSelectCityListViewAdapter;

    private List<SelectCityBean> mSelectCityList = new ArrayList<SelectCityBean>();
    private int mFragmentIndex;
    private int mClickLocation;
    private String mFragmentCityName;
    //记录从选择商圈进来的城市ID
    private String businessId;

    //获取城市Url
    private String cityUrl = Constants.commontUrl + "area/citylist";
    //sp的名字
    private static final String CACHECITYNAME = "cacheCityName";

    private static final int LOADING = 0;
    private static final int SUCCESS = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pb_selectCityLoading_progressBar.setVisibility(View.VISIBLE);
                    tv_selectCity_emptyView.setVisibility(View.GONE);
                    lv_select_listView.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    if (mSelectCityList.size() != 0) {
                        pb_selectCityLoading_progressBar.setVisibility(View.GONE);
                        tv_selectCity_emptyView.setVisibility(View.GONE);
                        lv_select_listView.setVisibility(View.VISIBLE);
                        if (mSelectCityListViewAdapter != null) {
                            mSelectCityListViewAdapter.notifyDataSetChanged();
                        }
                    } else {
                        pb_selectCityLoading_progressBar.setVisibility(View.GONE);
                        tv_selectCity_emptyView.setVisibility(View.VISIBLE);
                        lv_select_listView.setVisibility(View.GONE);
                    }

                    //判断是否需要自动跳转，点击哪个TextView过来的
                    if(mClickLocation == 1) {
                        // TODO 需要自动跳转.跳转之前肯定得知道从哪个城市跳转，这样才知道商圈的数据
                        UIUtils.getMainThreadHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //得先得到城市的ID
                                if(mSelectCityList != null && mSelectCityList.size() > 0) {
                                    for(SelectCityBean cityBean : mSelectCityList) {
                                        if(mFragmentCityName.equals(cityBean.cityName)) {
                                            businessId = cityBean.id;
                                            break;
                                        }
                                    }
                                }
                                Intent intent = new Intent(SelectCityActivity.this, SelectBusinessActivity.class);
                                intent.putExtra("fragmentIndex",mFragmentIndex);
                                intent.putExtra("cityName",mFragmentCityName);
                                intent.putExtra("cityId",businessId);
                                startActivity(intent);
                            }
                        }, 300);
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private boolean mFromRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        ButterKnife.inject(this);

        //得到是从哪个fragment过来的
        Intent intent = getIntent();
        mFragmentIndex = intent.getIntExtra("fragmentIndex", -1);
        mFragmentCityName = intent.getStringExtra("fragmentCityName");
        mClickLocation = intent.getIntExtra("clickLocation", -1);
        //得到是不是从注册界面过来的
        mFromRegister = intent.getBooleanExtra("fromRegister", false);

        //添加到统一管理
        BaseApplication.getInstance().addActivity(this);

        mSelectCityListViewAdapter = new SelectCityListViewAdapter(mSelectCityList);
        lv_select_listView.setAdapter(mSelectCityListViewAdapter);
    }

    private void initData() {
        //判断网络
        boolean iSNetworkConnect = ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext());

        //先判断缓存中是否有数据
        String jsonString = SpUtils.getCacheString(CACHECITYNAME,UIUtils.getContext(), "cityJsonString", "");
        mSelectCityList.clear();
        if(TextUtils.isEmpty(jsonString)) {
            //为空，在需要判断网络,再去解析
            if (!iSNetworkConnect) {
                ToastUtils.showToastInUIThread(UIUtils.getContext(),"您尚未开启网络");
                return;
            }else {
                //进度条展示
                mHandler.obtainMessage(LOADING).sendToTarget();
                //网络请求
                OkHttpUtils.get().url(cityUrl).build().execute(new StringCallback() {
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
                            SpUtils.putCacheString(CACHECITYNAME,UIUtils.getContext(),"cityJsonString",response);
                            //可以下一步解析城市
                            List<SelectCityBean> tempList = SelectCityParse.selectCityParse(response);
                            for(SelectCityBean selectCityBean: tempList) {
                                SelectCityBean mSelectCityBean = new SelectCityBean();
                                mSelectCityBean.cityName = selectCityBean.cityName;
                                mSelectCityBean.id = selectCityBean.id;
                                mSelectCityList.add(mSelectCityBean);
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
            for(SelectCityBean selectCityBean: list) {
                SelectCityBean mSelectCityBean = new SelectCityBean();
                mSelectCityBean.cityName = selectCityBean.cityName;
                mSelectCityBean.id = selectCityBean.id;
                mSelectCityList.add(mSelectCityBean);
            }
            //发送handler，为了可以执行自动跳转的代码
            mHandler.obtainMessage(SUCCESS).sendToTarget();
        }
    }

    private void initEvent() {
        //取消
        BackListenerUtils.backFinish(rl_select_cancleRl,this);
        //listView点击
        lv_select_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(mFromRegister) {
                    //从注册界面过来
                    Intent mIntent = new Intent(SelectCityActivity.this, SelectBusinessActivity.class);
                    mIntent.putExtra("fromRegister",mFromRegister);
                    mIntent.putExtra("cityId",mSelectCityList.get(position).id);
                    mIntent.putExtra("cityName",mSelectCityList.get(position).cityName);
                    startActivityForResult(mIntent,2);
                }else {
                    Intent intent = new Intent(SelectCityActivity.this, SelectBusinessActivity.class);
                    intent.putExtra("fragmentIndex",mFragmentIndex);
                    intent.putExtra("cityName",mSelectCityList.get(position).cityName);
                    intent.putExtra("cityId",mSelectCityList.get(position).id);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2 && resultCode == 3) {
            if(data != null) {
                setResult(22,data);
                finish();
            }
        }
    }
}
