package com.wushuikeji.www.yuyubuyer.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.bean.BankBean;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Jack_chentao
 */
public class SelectBankCardListViewAdapter extends BaseAdapter {

    private List<BankBean> bankBeanList;

    public SelectBankCardListViewAdapter(List<BankBean> bankBeanList) {
        this.bankBeanList = bankBeanList;
    }

    @Override
    public int getCount() {
        if (bankBeanList != null && bankBeanList.size() > 0) {
            return bankBeanList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (bankBeanList != null && bankBeanList.size() > 0) {
            return bankBeanList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if (bankBeanList != null && bankBeanList.size() > 0) {
            return position;
        } else {
            return 0;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHold = null;
        if (convertView == null) {
            convertView = View.inflate(UIUtils.getContext(), R.layout.item_listview_selectbankcard, null);
            viewHold = new ViewHolder(convertView);
            convertView.setTag(viewHold);
        } else {
            viewHold = (ViewHolder) convertView.getTag();
        }
        //设置数据
        viewHold.bankCradbankType.setText(bankBeanList.get(position).bankType);
        viewHold.bankCradFooterNum.setText(bankBeanList.get(position).bankFooterNum);

        return convertView;
    }

    static class ViewHolder {

        @InjectView(R.id.bankCrad_itemListView_bankType)
        TextView bankCradbankType;

        @InjectView(R.id.bankCrad_itemListView_bankNum)
        TextView bankCradFooterNum;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
