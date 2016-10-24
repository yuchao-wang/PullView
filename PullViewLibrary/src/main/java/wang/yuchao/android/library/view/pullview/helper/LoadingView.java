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
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * 下拉刷新或者上拉加载Layout
 */
public abstract class LoadingView extends FrameLayout {

    /** 当前的状态 */
    private State mCurState = State.NULL;
    /** 前一个状态 */
    private State mPreState = State.NULL;
    /** 布局 */
    protected View view;
    /** 任何一个动画需要的时间 */
    protected static final int DURATION = 250;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /** 默认构造方法 */
    public LoadingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        view = initContentView(context, attrs);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        addView(view, params);
    }

    /**
     * 显示或隐藏这个布局
     * @param isShow 是否显示标志
     */
    public void show(boolean isShow) {
        if (isShow == (View.VISIBLE == getVisibility())) {
            return;
        }
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (null != params) {
            if (isShow) {
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            } else {
                params.height = 0;
            }
            setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
        }
    }

    /**
     * 获取the mCurState。
     * @return 当前状态
     */
    public State getCurState() {
        return mCurState;
    }

    /**
     * 获取the mPreState。
     * @return 前一个状态
     */
    public State getPreState() {
        return mPreState;
    }

    /**
     * 设置状态
     * @param state 状态
     */
    public void setState(State state) {
        if (mCurState != state) {
            mPreState = mCurState;
            mCurState = state;
            onStateChanged(state);
        }
    }

    /**
     * 设置最后更新的文字(仅用于下拉刷新的提示)
     * @param text 更新内容
     */
    public abstract void setLastUpdateText(String text);

    /**
     * 当状态改变时调用
     * @param curState 当前状态
     */
    protected void onStateChanged(State curState) {
        switch (curState) {
            case INIT:
                onInit();
                break;
            case PULL:
                onPull();
                break;
            case RELEASE:
                onRelease();
                break;
            case LOADING:
                onLoading();
                break;
            case NO_MORE_DATA:
                onNoMoreData();
                break;
            default:
                break;
        }
    }

    public State getState() {
        return mCurState;
    }

    /*
     * =======================下面都是HeaderView和FooterView子类需要重写的方法==================
     * =======
     */

    /**
     * LoadingView的初始化
     * @param context
     * @param attrs
     * @return LoadingView
     */
    protected abstract View initContentView(Context context, AttributeSet attrs);

    /**
     * 得到当前Layout的内容大小，它将作为一个刷新的临界点
     * @return 布局的高度
     */
    public abstract int getContentSize();

    /** 初始化状态时[即可以下拉刷新或者上拉加载]回调 */
    protected abstract void onInit();

    /** 拖动过程中回调 */
    protected abstract void onPull();

    /** 释放的时候回调 */
    protected abstract void onRelease();

    /** 正在加载的时候回调 */
    protected abstract void onLoading();

    /** 没有更多数据回调 */
    protected abstract void onNoMoreData();
}
