/**
 * Copyright (c)  1993-2023 AutoNavi, Inc.
 * All rights reserved.
 * <p/>
 * This software is the confidential and proprietary information of AutoNavi,
 * Inc. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with AutoNavi.
 */
package wang.yuchao.android.library.view.pullview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.ListView;

import wang.yuchao.android.library.view.pullview.helper.FooterView;
import wang.yuchao.android.library.view.pullview.helper.LoadingView;
import wang.yuchao.android.library.view.pullview.helper.PullView;
import wang.yuchao.android.library.view.pullview.helper.State;

/**
 * ListView的下拉刷新，上拉加载更多控件<br>
 */
public class PullListView extends PullView<ListView> {

    private ListView listView;
    /**
     * 滑动到底部自动加载的FooterView
     */
    private LoadingView footerView;

    public PullListView(Context context) {
        super(context, null);
        setAutoLoadMoreEnabled(true);
    }

    public PullListView(Context context, AttributeSet attrs) {
        //   this(context, attrs, 0);
        super(context, attrs);
        setAutoLoadMoreEnabled(true);
    }

    /* public PullListView(Context context, AttributeSet attrs, int defStyle) {
         super(context, attrs, defStyle);
         setAutoLoadMoreEnabled(true);//ListView,GridView有上拉加载,ScrollView和WebView没有上拉加载
     }
 */
    @Override
    protected ListView initCenterView(Context context, AttributeSet attrs) {
        this.listView = new ListView(context);
        listView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (isAutoLoadMoreEnabled() && hasMoreData()) {
                    if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
                            || scrollState == OnScrollListener.SCROLL_STATE_FLING) {
                        if (isReadyForPullUp()) {
                            startLoadingMore();
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
            }
        });
        return listView;
    }

    @Override
    protected boolean isReadyForPullDown() {
        Adapter adapter = listView.getAdapter();
        if (null == adapter || adapter.isEmpty()) {
            return true;
        }
        int mostTop = (listView.getChildCount() > 0) ? listView.getChildAt(0).getTop() : 0;
        if (mostTop >= 0) {
            return true;
        }
        return false;
    }

    @Override
    protected boolean isReadyForPullUp() {
        Adapter adapter = listView.getAdapter();
        if (null == adapter || adapter.isEmpty()) {
            return true;
        }
        int lastItemPosition = adapter.getCount() - 1;
        int lastVisiblePosition = listView.getLastVisiblePosition();
        if (lastVisiblePosition >= lastItemPosition - 1) {
            int childIndex = lastVisiblePosition - listView.getFirstVisiblePosition();
            int childCount = listView.getChildCount();
            int index = Math.min(childIndex, childCount - 1);
            View lastVisibleChild = listView.getChildAt(index);
            if (lastVisibleChild != null) {
                return lastVisibleChild.getBottom() <= listView.getBottom();
            }
        }
        return false;
    }

    @Override
    public LoadingView getFooterView() {
        if (isAutoLoadMoreEnabled()) {
            return footerView;
        }
        return super.getFooterView();
    }

    /**
     * 设置是否有更多数据的标志
     *
     * @param haveMoreData true表示还有更多的数据，false表示没有更多数据了
     */
    public void setHaveMoreData(boolean haveMoreData) {
        if (haveMoreData) {
            if (null != footerView) {
                footerView.setState(State.INIT);
            }
            LoadingView footerLoadingLayout = getFooterView();
            if (null != footerLoadingLayout) {
                footerLoadingLayout.setState(State.INIT);
            }
        } else {
            if (null != footerView) {
                footerView.setState(State.NO_MORE_DATA);
            }
            LoadingView footerLoadingLayout = getFooterView();
            if (null != footerLoadingLayout) {
                footerLoadingLayout.setState(State.NO_MORE_DATA);
            }
        }
    }

    /**
     * 是否显示根布局
     */
    public void showFooterView(boolean show) {
        if (footerView != null) {
            footerView.show(show);
        }
    }

    /**
     * 表示是否还有更多数据
     *
     * @return true表示还有更多数据
     */
    private boolean hasMoreData() {
        if ((null != footerView) && (footerView.getState() == State.NO_MORE_DATA)) {
            return false;
        }
        return true;
    }

    @Override
    protected void startLoadingMore() {
        super.startLoadingMore();
        if (null != footerView) {
            footerView.setState(State.LOADING);
        }
    }

    @Override
    public void pullUpToLoadMoreCompleted() {
        super.pullUpToLoadMoreCompleted();
        if (null != footerView) {
            footerView.setState(State.INIT);
        }
    }

    @Override
    protected void setAutoLoadMoreEnabled(boolean autoLoadMoreEnabled) {
        super.setAutoLoadMoreEnabled(autoLoadMoreEnabled);
        if (autoLoadMoreEnabled) {
            if (null == footerView) {
                footerView = new FooterView(getContext());
            }
            if (null == footerView.getParent()) {
                listView.addFooterView(footerView, null, false);
            }
            footerView.show(true);
        } else {
            if (null != footerView) {
                footerView.show(false);
            }
        }
    }
}
