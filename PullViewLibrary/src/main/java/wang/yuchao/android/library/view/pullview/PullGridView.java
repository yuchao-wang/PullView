/**
 *  Copyright (c)  1993-2023 AutoNavi, Inc.
 *  All rights reserved.
 *  This software is the confidential and proprietary information of AutoNavi, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with AutoNavi.
 */
package wang.yuchao.android.library.view.pullview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.GridView;

import wang.yuchao.android.library.view.pullview.helper.FooterView;
import wang.yuchao.android.library.view.pullview.helper.LoadingView;
import wang.yuchao.android.library.view.pullview.helper.PullView;
import wang.yuchao.android.library.view.pullview.helper.State;

/**
 * GridView的下拉刷新，上拉加载更多控件
 */
public class PullGridView extends PullView<GridView> {

    private GridView gridView;

    private LoadingView footerView;

    public PullGridView(Context context) {
        super(context);
        setAutoLoadMoreEnabled(true);
    }

    public PullGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAutoLoadMoreEnabled(true);
    }

//    public PullGridView(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//        setAutoLoadMoreEnabled(true);
//    }

    @Override
    protected GridView initCenterView(Context context, AttributeSet attrs) {
        gridView = new GridView(context);
        gridView.setOnScrollListener(new OnScrollListener() {
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
        return gridView;
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

    @Override
    public LoadingView getFooterView() {
        if (isAutoLoadMoreEnabled()) {
            return footerView;
        }
        return super.getFooterView();
    }

    @Override
    protected boolean isReadyForPullDown() {
        Adapter adapter = gridView.getAdapter();
        if (null == adapter || adapter.isEmpty()) {
            return true;
        }
        int mostTop = (gridView.getChildCount() > 0) ? gridView.getChildAt(0).getTop() : 0;
        if (mostTop >= 0) {
            return true;
        }
        return false;
    }

    @Override
    protected boolean isReadyForPullUp() {
        Adapter adapter = gridView.getAdapter();
        if (null == adapter || adapter.isEmpty()) {
            return true;
        }
        int lastItemPosition = adapter.getCount() - 1;
        int lastVisiblePosition = gridView.getLastVisiblePosition();
        if (lastVisiblePosition >= lastItemPosition - 1) {
            int childIndex = lastVisiblePosition - gridView.getFirstVisiblePosition();
            int childCount = gridView.getChildCount();
            int index = Math.min(childIndex, childCount - 1);
            View lastVisibleChild = gridView.getChildAt(index);
            if (lastVisibleChild != null) {
                return lastVisibleChild.getBottom() <= gridView.getBottom();
            }
        }
        return false;
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
    public void setAutoLoadMoreEnabled(boolean autoLoadMoreEnabled) {
        super.setAutoLoadMoreEnabled(autoLoadMoreEnabled);
        if (autoLoadMoreEnabled && (null == footerView)) {// 设置Footer
            footerView = new FooterView(getContext());
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

}
