/**
 *  Copyright (c)  1993-2023 AutoNavi, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of AutoNavi, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with AutoNavi.
 */
package wang.yuchao.android.library.view.pullview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

import wang.yuchao.android.library.view.pullview.helper.PullView;

/**
 * ScrollView的下拉刷新控件<br>
 * 可以下拉刷新的ScrollView控件<br>
 * 注意：添加子View只能使用pullScrollView.getCenterView.addView(view);这样可以分离布局文件，增强重用性
 */
public class PullScrollView extends PullView<ScrollView> {

    public PullScrollView(Context context) {
        super(context);
    }

    public PullScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    public PullScrollView(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//    }

    @Override
    protected ScrollView initCenterView(Context context, AttributeSet attrs) {
        ScrollView scrollView = new ScrollView(context);
        return scrollView;
    }

    @Override
    protected boolean isReadyForPullDown() {
        return centerView.getScrollY() == 0;
    }

    @Override
    protected boolean isReadyForPullUp() {
        View scrollViewChild = centerView.getChildAt(0);
        if (null != scrollViewChild) {
            return centerView.getScrollY() >= (scrollViewChild.getHeight() - getHeight());
        }
        return false;
    }
}
