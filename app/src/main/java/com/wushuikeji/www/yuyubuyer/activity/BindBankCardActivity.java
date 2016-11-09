package com.wushuikeji.www.yuyubuyer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.adapter.BankListViewAdapter;
import com.wushuikeji.www.yuyubuyer.bean.BankBean;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.jsonparse.AccountSafetyParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.BankInfoParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.SendAuthoCodeParse;
import com.wushuikeji.www.yuyubuyer.utils.BackListenerUtils;
import com.wushuikeji.www.yuyubuyer.utils.ISNetworkConnectUtils;
import com.wushuikeji.www.yuyubuyer.utils.RequestResponseUtils;
import com.wushuikeji.www.yuyubuyer.utils.SpUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToNextActivityUtils;
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

public class BindBankCardActivity extends AppCompatActivity {

    @InjectView(R.id.bindBank_listView)
    ListView bindBank_listView;

    @InjectView(R.id.rl_bank_back)
    RelativeLayout rl_bank_back;

    @InjectView(R.id.pb_bindbank_pb)
    ProgressBar pb_bindbank_pb;

    private BankListViewAdapter mBankListViewAdapter;

    private List<BankBean> bankBeanList = new ArrayList<BankBean>();

    public static BindBankCardActivity mBindBankCardActivity;

    //检查用户是否认证
    private String checkIsAuthenticationUrl = Constants.commontUrl + "auth/check";
    private String mUserId;
    private RelativeLayout mRl_bank_addBank;

    //获取银行卡的列表的地址
    private String bankListUrl = Constants.commontUrl + "bank/list";
    //最大查询银行卡25张
    private int count = 25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_bank_card);
        initView();
        initData();
        initEvent();
    }
    private void initView() {
        ButterKnife.inject(this);
        //得到用户ID
        mUserId = SpUtils.getCacheString(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.BUYERID, "");

        mBindBankCardActivity = this;

        //将尾部布局蹦出来
        View footerView = View.inflate(UIUtils.getContext(), R.layout.item_listview_bankfoot, null);
        //只允许添加一次
        if (bindBank_listView.getFooterViewsCount() == 0) {
            bindBank_listView.addFooterView(footerView, null, true);
        }
        mBankListViewAdapter = new BankListViewAdapter(bankBeanList);
        bindBank_listView.setAdapter(mBankListViewAdapter);
        //尾部视图中的控件
        mRl_bank_addBank = (RelativeLayout) footerView.findViewById(R.id.rl_bank_addBank);
    }

    private void initData() {
        if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
            return;
        } else {
            //查询银行卡的列表
            loadNetworkData("0");
        }
    }

    private void initEvent() {
        BackListenerUtils.backFinish(rl_bank_back,this);
        //添加银行卡
        if(mRl_bank_addBank != null) {
            mRl_bank_addBank.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestNetworkCheckIsAuto();
                }
            });
        }
        //点击查看银行卡的详情
        bindBank_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToNextActivityUtils.toNextActivityNotFinishAndParameters(BindBankCardActivity.this, BankCardDetailsActivity.class,"bankId",bankBeanList.get(position).bank_id);
            }
        });
    }

    /**
     * 查询银行卡的列表
     */
    private void loadNetworkData(String offset) {

        pb_bindbank_pb.setVisibility(View.VISIBLE);

        OkHttpUtils.post().url(bankListUrl).addParams("user_id", mUserId)
                .addParams("offset", offset)
                .addParams("count", String.valueOf(count))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                pb_bindbank_pb.setVisibility(View.GONE);
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
                        //可以下一步解析
                        List<BankBean> tempBankBeanList = BankInfoParse.bankInfoParse(response);
                        if (tempBankBeanList != null) {
                            bankBeanList.clear();
                            bankBeanList.addAll(tempBankBeanList);
                            mBankListViewAdapter.notifyDataSetChanged();
                        }
                    }else if (responseContent != null) {
                        if(MyConstants.FOOTVIEWNAME.equals(responseContent)) {
                            //清空数据源
                            bankBeanList.clear();
                            if(mBankListViewAdapter != null) {
                                mBankListViewAdapter.notifyDataSetChanged();
                            }
                        }
                        //给出错误提示
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                    }
                    pb_bindbank_pb.setVisibility(View.GONE);
                } catch (Exception e) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                    pb_bindbank_pb.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 核对用户是否认证过
     */
    private void requestNetworkCheckIsAuto() {
        OkHttpUtils.post().url(checkIsAuthenticationUrl).addParams("user_id", mUserId)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
            }

            @Override
            public void onResponse(String response, int id) {

                Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                String response_code = map.get("response_code");
                if ("0".equals(response_code)) {
                    Map<String, String> accountSafetyMap = AccountSafetyParse.accountSafetyParse(response);
                    //判断是否认证过
                    String real_name = accountSafetyMap.get("real_name");

                    String id_number = accountSafetyMap.get("id_number");

                    if (!TextUtils.isEmpty(real_name) && !TextUtils.isEmpty(id_number)) {
                        //认证过,进入添加银行卡界面
                        ToNextActivityUtils.toNextActivityNotFinishAndParameters(BindBankCardActivity.this,AddBankActivity.class,"realName",real_name);
                    } else {
                        //未认证过
                        ToastUtils.showToastInUIThread(UIUtils.getContext(),"请您先到账户安全里进行实名认证");
                    }
                }
            }
        });
    }
    /**
     * 提供给外部刷新使用
     */
    public void refreshBankInfoData() {
        loadNetworkData("0");
    }
}
