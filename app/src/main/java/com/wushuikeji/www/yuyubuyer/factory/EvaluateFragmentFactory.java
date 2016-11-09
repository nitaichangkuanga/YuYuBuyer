package com.wushuikeji.www.yuyubuyer.factory;

import com.wushuikeji.www.yuyubuyer.base.BaseFragment;
import com.wushuikeji.www.yuyubuyer.fragment.BuyerEvaluateFragment;
import com.wushuikeji.www.yuyubuyer.fragment.OtherEvaluateFragment;
import com.wushuikeji.www.yuyubuyer.fragment.ShopEvaluateFragment;

/**
 * @des	Fragment工厂类
 */
public class EvaluateFragmentFactory {

	public static final int	FRAGMENT_SHOPEVALUATE = 0;
	public static final int	FRAGMENT_BUYEREVALUATE = 1;
	public static final int	FRAGMENT_OTHEREVALUATE = 2;

	public static BaseFragment getFragment(int position) {

		BaseFragment fragment = null;

		switch (position) {
		case FRAGMENT_SHOPEVALUATE:// 商铺评价
			fragment = new ShopEvaluateFragment();
			break;
		case FRAGMENT_BUYEREVALUATE:// BUYER评价
			fragment = new BuyerEvaluateFragment();
			break;
		case FRAGMENT_OTHEREVALUATE:// 对方评价
			fragment = new OtherEvaluateFragment();
			break;
		default:
			break;
		}
		return fragment;
	}
}
