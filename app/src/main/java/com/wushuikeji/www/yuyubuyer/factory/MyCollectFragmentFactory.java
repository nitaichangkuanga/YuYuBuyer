package com.wushuikeji.www.yuyubuyer.factory;

import com.wushuikeji.www.yuyubuyer.base.BaseFragment;
import com.wushuikeji.www.yuyubuyer.fragment.GoodCollectFragment;
import com.wushuikeji.www.yuyubuyer.fragment.ShopCollectFragment;

/**
 * @des	Fragment工厂类
 */
public class MyCollectFragmentFactory {

	public static final int	FRAGMENT_SHOPCOLLECT = 0;
	public static final int	FRAGMENT_GOODCOLLECT = 1;

	public static BaseFragment getFragment(int position) {

		BaseFragment fragment = null;

		switch (position) {
		case FRAGMENT_SHOPCOLLECT:// 商铺收藏
			fragment = new ShopCollectFragment();
			break;
		case FRAGMENT_GOODCOLLECT:// 商品收藏
			fragment = new GoodCollectFragment();
			break;
		default:
			break;
		}
		return fragment;
	}
}
