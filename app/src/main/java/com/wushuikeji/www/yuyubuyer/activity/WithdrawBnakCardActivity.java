package com.wushuikeji.www.yuyubuyer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.adapter.SelectBankCardListViewAdapter;
import com.wushuikeji.www.yuyubuyer.bean.BankBean;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.jsonparse.BankInfoParse;
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

public class WithdrawBnakCardActivity extends AppCompatActivity {

    @InjectView(R.id.rl_withdrawBankCard_back)
    RelativeLayout rl_withdrawBankCard_back;

    @InjectView(R.id.withdrawBankCard_listView)
    ListView withdrawBankCard_listView;

    @InjectView(R.id.pb_withdrawBankCard_loading)
    ProgressBar pb_withdrawBankCard_loading;

    private SelectBankCardListViewAdapter mSelectBankCardListViewAdapter;
    private List<BankBean> bankBeanList = new ArrayList<BankBean>();

    //最大查询银行卡25张
    private int count = 25;

    //获取银行卡的列表的地址
    private String bankListUrl = Constants.commontUrl + "bank/list";
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_bnak_card);
        initView();
        initData();
        initEvent();
    }
    private void initView() {
        ButterKnife.inject(this);

        //得到用户ID
        mUserId = SpUtils.getCacheString(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.BUYERID, "");

        mSelectBankCardListViewAdapter = new SelectBankCardListViewAdapter(bankBeanList);
        withdrawBankCard_listView.setAdapter(mSelectBankCardListViewAdapter);
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
        BackListenerUtils.backFinish(rl_withdrawBankCard_back,this);
        //listView的点击
        withdrawBankCard_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = getIntent();
                intent.putExtra("bankId", bankBeanList.get(position).bank_id);
                intent.putExtra("bankInfo", bankBeanList.get(position).bankType+"("+bankBeanList.get(position).bankFooterNum+")");
                setResult(1111,intent);
                WithdrawBnakCardActivity.this.finish();
            }
        });
    }

    /**
     * 查询银行卡的列表
     */
    private void loadNetworkData(String offset) {

        pb_withdrawBankCard_loading.setVisibility(View.VISIBLE);

        OkHttpUtils.post().url(bankListUrl).addParams("user_id", mUserId)
                .addParams("offset", offset)
                .addParams("count", String.valueOf(count))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                pb_withdrawBankCard_loading.setVisibility(View.GONE);
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
                            mSelectBankCardListViewAdapter.notifyDataSetChanged();
                        }
                    }else if (responseContent != null) {
                        if(MyConstants.FOOTVIEWNAME.equals(responseContent)) {
                            //清空数据源
                            bankBeanList.clear();
                            if(mSelectBankCardListViewAdapter != null) {
                                mSelectBankCardListViewAdapter.notifyDataSetChanged();
                            }
                        }
                        //给出错误提示
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                    }
                    pb_withdrawBankCard_loading.setVisibility(View.GONE);
                } catch (Exception e) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                    pb_withdrawBankCard_loading.setVisibility(View.GONE);
                }
            }
        });
    }

}
