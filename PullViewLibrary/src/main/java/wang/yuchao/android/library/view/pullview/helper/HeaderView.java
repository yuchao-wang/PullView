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
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import wang.yuchao.android.library.view.pullview.R;

/**
 * HeaderView
 */
public class HeaderView extends LoadingView {

    /**
     * 头部图片[本应用指的是arrow]
     */
    private ImageView headerArrow;
    /**
     * 头部ProgressBar
     */
    private ProgressBar headerProgressBar;
    /**
     * 头部提示文字
     */
    private TextView headerHintText;
    /**
     * 头部时间文字
     */
    private TextView headerTimeText;
    /**
     * 箭头转动到上面的动画
     */
    private Animation animationUp;
    /**
     * 箭头转动到下面的动画
     */
    private Animation animationDown;

    public HeaderView(Context context) {
        super(context);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected View initContentView(Context context, AttributeSet attrs) {
        //这里的view就是headerView
        view = LayoutInflater.from(context).inflate(R.layout.lib_pullview_header, null);
        headerArrow = (ImageView) view.findViewById(R.id.lib_pullview_header_arrow);
        headerHintText = (TextView) view.findViewById(R.id.lib_pullview_header_hint_text);
        headerProgressBar = (ProgressBar) view.findViewById(R.id.lib_pullview_header_progressbar);
        headerTimeText = (TextView) view.findViewById(R.id.lib_pullview_header_time_text);

        animationUp = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);//顺时针180度
        animationUp.setInterpolator(new LinearInterpolator());
        animationUp.setDuration(DURATION);
        animationUp.setFillAfter(true);

        animationDown = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);//逆时针180度
        animationDown.setInterpolator(new LinearInterpolator());
        animationDown.setDuration(DURATION);
        animationDown.setFillAfter(true);

        return view;
    }

    @Override
    public void setLastUpdateText(String text) {
        headerTimeText.setVisibility(TextUtils.isEmpty(text) ? View.INVISIBLE : View.VISIBLE);
        headerTimeText.setText(text);
    }

    @Override
    public int getContentSize() {
        if (null != view) {
            return view.getHeight();
        }
        return (int) (getResources().getDisplayMetrics().density * 60);
    }

    @Override
    protected void onStateChanged(State curState) {
        headerArrow.setVisibility(View.VISIBLE);
        headerProgressBar.setVisibility(View.INVISIBLE);
        super.onStateChanged(curState);
    }

    @Override
    protected void onInit() {
        headerArrow.clearAnimation();
        headerHintText.setText(PullString.PULL_DOWN_TO_REFRESH);
    }

    @Override
    protected void onPull() {
        if (State.RELEASE == getPreState()) {
            //如果上一个state是CAN_RELEASE，说明是下拉又上拉
            headerArrow.clearAnimation();
            headerArrow.startAnimation(animationDown);
        }
        headerHintText.setText(PullString.PULL_DOWN_TO_REFRESH);
    }

    @Override
    protected void onRelease() {
        headerArrow.clearAnimation();
        headerArrow.startAnimation(animationUp);
        headerHintText.setText(PullString.RELEASE_TO_REFRESH);
    }

    @Override
    protected void onLoading() {
        headerArrow.clearAnimation();
        headerArrow.setVisibility(View.INVISIBLE);
        headerProgressBar.setVisibility(View.VISIBLE);
        headerHintText.setText(PullString.REFRESHING);
    }

    @Override
    protected void onNoMoreData() {
    }

}
