package com.wushuikeji.www.yuyubuyer.mylibrary.swipemenu.interfaces;

import com.wushuikeji.www.yuyubuyer.mylibrary.swipemenu.bean.SwipeMenu;
import com.wushuikeji.www.yuyubuyer.mylibrary.swipemenu.view.SwipeMenuView;


public interface OnSwipeItemClickListener {

    void onItemClick(SwipeMenuView view, SwipeMenu menu, int index);
}