/**
 *  Copyright (c)  1993-2023 AutoNavi, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of AutoNavi, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with AutoNavi.
 */
package wang.yuchao.android.library.view.pullview.helper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import wang.yuchao.android.library.view.pullview.R;


/**
 * FooterView
 */
public class FooterView extends LoadingView {

    /**
     * 尾部ProgressBar
     */
    private ProgressBar footerProgressBar;
    /**
     * 尾部提示文字
     */
    private TextView footerHintText;

    public FooterView(Context context) {
        super(context);
    }

    public FooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FooterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected View initContentView(Context context, AttributeSet attrs) {
        // 这里view就是footerview
        view = LayoutInflater.from(context).inflate(R.layout.lib_pullview_footer, null);
        footerProgressBar = (ProgressBar) view.findViewById(R.id.lib_pullview_footer_progressbar);
        footerHintText = (TextView) view.findViewById(R.id.lib_pullview_footer_text);
        setState(State.INIT);
        return view;
    }

    @Override
    public int getContentSize() {
        if (null != view) {
            return view.getHeight();
        }
        return (int) (getResources().getDisplayMetrics().density * 40);
    }

    @Override
    protected void onStateChanged(State curState) {
        //初始化状态
        footerProgressBar.setVisibility(View.INVISIBLE);
        footerHintText.setVisibility(View.VISIBLE);
        //状态发生改变
        super.onStateChanged(curState);
    }

    @Override
    protected void onInit() {
        footerHintText.setText(PullString.PULL_UP_TO_LOAD_MORE);
    }

    @Override
    protected void onPull() {
        footerHintText.setText(PullString.PULL_UP_TO_LOAD_MORE);
    }

    @Override
    protected void onRelease() {
        footerHintText.setText(PullString.RELEASE_TO_LOAD_MORE);
    }

    @Override
    protected void onLoading() {
        footerHintText.setText(PullString.LOADING);
        footerProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void setLastUpdateText(String text) {
    }

    @Override
    protected void onNoMoreData() {
        footerHintText.setText(PullString.NO_MORE_DATA);
    }
}
