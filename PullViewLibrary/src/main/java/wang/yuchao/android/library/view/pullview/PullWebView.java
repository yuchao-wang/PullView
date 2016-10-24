/**
 * Copyright (c)  1993-2023 AutoNavi, Inc.
 * All rights reserved.
 * This software is the confidential and proprietary information of AutoNavi,
 * Inc. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with AutoNavi.
 */
package wang.yuchao.android.library.view.pullview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

import wang.yuchao.android.library.view.pullview.helper.PullView;

/**
 * WebView的下拉刷新控件
 */
@SuppressLint("FloatMath")
public class PullWebView extends PullView<WebView> {
    public PullWebView(Context context) {
        super(context);
    }

    public PullWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    public PullWebView(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//    }

    @Override
    protected WebView initCenterView(Context context, AttributeSet attrs) {
        WebView webView = new WebView(context);
        return webView;
    }

    @Override
    protected boolean isReadyForPullDown() {
        return centerView.getScrollY() == 0;
    }

    @Override
    protected boolean isReadyForPullUp() {
        float exactContentHeight = (float) Math.floor(centerView.getContentHeight()
                * centerView.getScale());
        return centerView.getScrollY() >= (exactContentHeight - centerView.getHeight());
    }

}
