package com.wushuikeji.www.yuyubuyer.factory;

import com.wushuikeji.www.yuyubuyer.base.BaseFragment;
import com.wushuikeji.www.yuyubuyer.fragment.MainOrderReceiverFragment;
import com.wushuikeji.www.yuyubuyer.fragment.MainOrderSendFragment;

/**
 * @des	Fragment工厂类
 */
public class MainOrderFragmentFactory {

	public static final int	FRAGMENT_MAINORDER_SEND = 0;
	public static final int	FRAGMENT_MAINORDER_RECEIVER = 1;

	public static BaseFragment getFragment(int position) {

		BaseFragment fragment = null;

		switch (position) {
		case FRAGMENT_MAINORDER_SEND:// 发单订单
			fragment = new MainOrderSendFragment();
			break;
		case FRAGMENT_MAINORDER_RECEIVER:// 接单订单
			fragment = new MainOrderReceiverFragment();
			break;
		default:
			break;
		}
		return fragment;
	}
}
