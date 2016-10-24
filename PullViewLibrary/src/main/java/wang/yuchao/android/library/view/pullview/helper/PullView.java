/**
 * Copyright (c)  1993-2023 AutoNavi, Inc.
 * All rights reserved.
 * This software is the confidential and proprietary information of AutoNavi,
 * Inc. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with AutoNavi.
 */
package wang.yuchao.android.library.view.pullview.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.text.SimpleDateFormat;
import java.util.Date;

import wang.yuchao.android.library.view.pullview.OnPullListener;

/**
 * 下拉刷新，上拉加载更多控件
 */
public abstract class PullView<V extends View> extends LinearLayout {

    /* =====================外部不可使用的私有属性======================== */
    /**
     * 动画时间
     */
    private static int DURATION = 250;
    /**
     * 阻尼系数
     */
    private static float OFFSET_RADIO = 2.5f;
    /**
     * 可以下拉刷新的View
     */
    protected V centerView;
    /**
     * 移动点的保护范围值
     */
    private int touchSlop;
    /**
     * 中间View的包装布局
     */
    private FrameLayout centerViewWrapper;
    /**
     * HeaderView的高度
     */
    private int headerHeight;
    /**
     * FooterView的高度
     */
    private int footerHeight;
    /**
     * 上一次移动的点
     */
    private float lastMotionY = -1;

    /* =====================外部可使用的私有属性======================== */
    /**
     * 滑动runnable
     */
    private SmoothScrollRunnable smoothScrollRunnable;
    /**
     * 下拉的状态
     */
    private State pullDownState = State.NULL;
    /**
     * 上拉的状态
     */
    private State pullUpState = State.NULL;
    /**
     * 下拉刷新的布局
     */
    private LoadingView headerView;
    /**
     * 上拉加载更多的布局
     */
    private LoadingView footerView;
    /**
     * 是否截断touch事件
     */
    private boolean interceptTouchEvent = true;
    /**
     * 滑动到底部自动加载,默认不可用
     */
    private boolean autoLoadMoreEnabled = false;
    /**
     * 消费touch事件
     */
    private boolean handleTouchEvent = false;
    /**
     * 下拉回调监听器
     */
    private OnPullListener pullListener;
    /**
     * 最近一次更新文字
     */
    private String lastUpdateText = null;

    /**
     * PullView构造方法
     *
     * @param context 上下文
     */
    public PullView(Context context) {
        this(context, null);
    }

    /**
     * PullView构造方法
     *
     * @param context 上下文
     * @param attrs   属性
     */
    public PullView(Context context, AttributeSet attrs) {
        //  this(context, attrs, 0);
        super(context, attrs);
        init(context, attrs);
    }

//    /**
//     * PullView构造方法
//     * @param context 上下文
//     * @param attrs 属性
//     * @param defStyle style
//     */
//    @SuppressLint("NewApi")
//    public PullView(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//        init(context, attrs);
//    }

    /**
     * 初始化
     *
     * @param context 上下文
     * @param attrs   配置
     */
    private void init(Context context, AttributeSet attrs) {
        setOrientation(LinearLayout.VERTICAL);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        headerView = new HeaderView(context, attrs);
        footerView = new FooterView(context, attrs);
        centerView = initCenterView(context, attrs);

        if (null == centerView) {
            throw new NullPointerException("center view can not be null.");
        }

        addCenterView(context, centerView);
        addFooterViewAndHeaderView(context);

        // 得到Header的高度，这个高度需要用这种方式得到，在onLayout方法里面得到的高度始终是0
        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                updatePaddingSize();
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    /**
     * 更新padding的大小
     */
    private void updatePaddingSize() {
        // 得到header和footer的内容高度，它将会作为拖动刷新的一个临界值，如果拖动距离大于这个高度
        // 然后再松开手，就会触发刷新操作
        int headerViewHeight = (null != headerView) ? headerView.getContentSize() : 0;
        int footerViewHeight = (null != footerView) ? footerView.getContentSize() : 0;
        if (headerViewHeight < 0) {
            headerViewHeight = 0;
        }
        if (footerViewHeight < 0) {
            footerViewHeight = 0;
        }
        headerHeight = headerViewHeight;
        footerHeight = footerViewHeight;
        // 这里得到Header和Footer的高度，设置的padding的top和bottom就应该是header和footer的高度
        // 因为header和footer是完全看不见的
        headerViewHeight = (null != headerView) ? headerView.getMeasuredHeight() : 0;
        footerViewHeight = (null != footerView) ? footerView.getMeasuredHeight() : 0;
        if (0 == footerViewHeight) {
            footerViewHeight = footerHeight;
        }
        int pLeft = getPaddingLeft();
        int pTop = getPaddingTop();
        int pRight = getPaddingRight();
        int pBottom = getPaddingBottom();
        pTop = -headerViewHeight;
        pBottom = -footerViewHeight;
        setPadding(pLeft, pTop, pRight, pBottom);
    }

    /**
     * 添加FooterView和HeaderView
     *
     * @param context 上下文
     */
    private void addFooterViewAndHeaderView(Context context) {
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (null != headerView) {
            if (this == headerView.getParent()) {
                removeView(headerView);
            }
            addView(headerView, 0, params);
        }
        if (null != footerView) {
            if (this == footerView.getParent()) {
                removeView(footerView);
            }
            addView(footerView, -1, params);
        }
    }

    /**
     * 添加中间的View
     *
     * @param context    上下文
     * @param centerView 中间控件
     */
    private void addCenterView(Context context, V centerView) {
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        // 创建一个包装容器
        centerViewWrapper = new FrameLayout(context);
        centerViewWrapper.addView(centerView, width, height);
        // 这里把centerView的高度设置为一个很小的值，它的高度最终会在onSizeChanged()方法中设置为MATCH_PARENT
        // 这样做的原因是，如果此是它的height是MATCH_PARENT，那么footer得到的高度就是0，所以，我们先设置高度很小
        // 我们就可以得到header和footer的正常高度，当onSizeChanged后，Refresh view的高度又会变为正常。
        height = 10;
        addView(centerViewWrapper, new LayoutParams(width, height));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // We need to update the header/footer when our size changes
        updatePaddingSize();
        // 设置刷新View的大小
        updateViewSize(w, h);
        post(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        });
    }

    @Override
    public void setOrientation(int orientation) {
        if (LinearLayout.VERTICAL != orientation) {
            throw new IllegalArgumentException("This class only supports VERTICAL orientation.");
        }
        // Only support vertical orientation
        super.setOrientation(orientation);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!isInterceptTouchEvent()) {
            return false;
        }
        int action = event.getAction();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            setHandleTouchEvent(false);
            return false;
        }
        if (action != MotionEvent.ACTION_DOWN && isHandleTouchEvent()) {
            return true;
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastMotionY = event.getY();
                setHandleTouchEvent(false);
                break;

            case MotionEvent.ACTION_MOVE:
                float deltaY = event.getY() - lastMotionY;
                float absDiff = Math.abs(deltaY);
                // 这里有三个条件：
                // 1，位移差大于mTouchSlop，这是为了防止快速拖动引发刷新
                // 2，isPullRefreshing()，如果当前正在下拉刷新的话，是允许向上滑动，并把刷新的HeaderView挤上去
                // 3，isPullLoading()，理由与第2条相同
                if (absDiff > touchSlop || isRefreshing() || isLoadingMore()) {
                    lastMotionY = event.getY();
                    // 第一个显示出来，Header已经显示或拉下
                    if (isReadyForPullDown()) {
                        // 1，Math.abs(getScrollY()) > 0：表示当前滑动的偏移量的绝对值大于0，表示当前HeaderView滑出来了或完全
                        // 不可见，存在这样一种case，当正在刷新时并且RefreshableView已经滑到顶部，向上滑动，那么我们期望的结果是
                        // 依然能向上滑动，直到HeaderView完全不可见
                        // 2，deltaY > 0.5f：表示下拉的值大于0.5f
                        setHandleTouchEvent((Math.abs(getScrollY()) > 0 || deltaY > 0.5f));
                        // 如果截断事件，我们则仍然把这个事件交给刷新View去处理，典型的情况是让ListView/GridView将按下
                        // Child的Selector隐藏
                        if (isHandleTouchEvent()) {
                            centerView.onTouchEvent(event);
                        }
                    }
                }
                break;

            default:
                break;
        }
        return isHandleTouchEvent();
    }

    /**
     * 是否正在刷新
     */
    private boolean isRefreshing() {
        return (pullDownState == State.LOADING);
    }

    /**
     * 是否正在加载更多
     */
    private boolean isLoadingMore() {
        return (pullUpState == State.LOADING);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean handled = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastMotionY = ev.getY();
                setHandleTouchEvent(false);
                break;

            case MotionEvent.ACTION_MOVE:
                float deltaY = ev.getY() - lastMotionY;
                lastMotionY = ev.getY();
                if (isReadyForPullDown()) {
                    pullHeaderLayout(deltaY / OFFSET_RADIO);
                    handled = true;
                } else {
                    setHandleTouchEvent(false);
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (handleTouchEvent) {
                    setHandleTouchEvent(false);
                    // 当第一个显示出来时
                    if (isReadyForPullDown()) {
                        // 调用刷新
                        if (pullDownState == State.RELEASE) {
                            startRefreshing();
                            handled = true;
                        }
                        resetHeaderView();
                    } else if (isReadyForPullUp()) {
                        // 加载更多

                        resetFooterView();
                    }
                }
                break;
            default:
                break;
        }
        return handled;
    }

    /**
     * 重置HeaderView
     */
    protected void resetHeaderView() {
        int scrollY = Math.abs(getScrollY());
        boolean refreshing = isRefreshing();
        if (refreshing && scrollY <= headerHeight) {
            smoothScrollTo(0, DURATION, 0);
            return;
        }
        if (refreshing) {
            smoothScrollTo(-headerHeight, DURATION, 0);
        } else {
            smoothScrollTo(0, DURATION, 0);
        }
    }

    /**
     * 重置FooterView
     */
    protected void resetFooterView() {
        int scrollY = Math.abs(getScrollY());
        boolean isPullLoadingMore = isLoadingMore();
        if (isPullLoadingMore && scrollY <= footerHeight) {
            smoothScrollTo(0, DURATION, 0);
            return;
        }
        if (isPullLoadingMore) {
            smoothScrollTo(footerHeight, DURATION, 0);
        } else {
            smoothScrollTo(0, DURATION, 0);
        }
    }

    /**
     * 平滑滚动
     *
     * @param newScrollValue 滚动的值
     * @param duration       滚动时候
     * @param delayMillis    延迟时间，0代表不延迟
     */
    private void smoothScrollTo(int newScrollValue, long duration, long delayMillis) {
        if (null != smoothScrollRunnable) {
            smoothScrollRunnable.stop();
        }

        int oldScrollValue = this.getScrollY();
        boolean post = (oldScrollValue != newScrollValue);
        if (post) {
            smoothScrollRunnable = new SmoothScrollRunnable(oldScrollValue, newScrollValue,
                    duration);
            if (delayMillis > 0) {
                postDelayed(smoothScrollRunnable, delayMillis);
            } else {
                post(smoothScrollRunnable);
            }
        }
    }

    /**
     * 开始刷新，当下拉松开后被调用
     */
    protected void startRefreshing() {
        // 如果正在刷新
        if (isRefreshing()) {
            return;
        }
        pullDownState = State.LOADING;
        if (null != headerView) {
            headerView.setState(State.LOADING);
        }
        if (null != pullListener) {
            pullListener.onPullDownToRefresh();
        }
    }

    /**
     * 开始加载更多，上拉松开后调用
     */
    protected void startLoadingMore() {
        // 如果正在加载
        if (isLoadingMore()) {
            return;
        }
        pullUpState = State.LOADING;
        if (null != footerView) {
            footerView.setState(State.LOADING);
        }
        if (null != pullListener) {
            pullListener.onPullUpToLoadMore();
        }
    }

    /**
     * 开始刷新，通常用于调用者主动刷新，典型的情况是进入界面，开始主动刷新，这个刷新并不是由用户拉动引起的
     *
     * @param delayMillis 延迟时间
     */
    public void setAutoPullDownToRefresh(long delayMillis) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                int newScrollValue = -headerHeight;
                startRefreshing();
                smoothScrollTo(newScrollValue, DURATION, 0);
            }
        }, delayMillis);
    }

    /**
     * 拉动Header Layout时调用
     *
     * @param delta 移动的距离
     */
    private void pullHeaderLayout(float delta) {
        // 向上滑动，并且当前scrollY为0时，不滑动
        int oldScrollY = getScrollY();
        if (delta < 0 && (oldScrollY - delta) >= 0) {
            scrollTo(0, 0);
            return;
        }
        // 向下滑动布局
        scrollBy(0, -(int) delta);
        // 未处于刷新状态，更新箭头
        int scrollY = Math.abs(getScrollY());
        if (!isRefreshing()) {
            if (scrollY > headerHeight) {
                pullDownState = State.RELEASE;
            } else {
                pullDownState = State.PULL;
            }
            headerView.setState(pullDownState);
        }
    }

    /**
     * 更新View的大小
     *
     * @param w 宽度
     * @param h 高度
     */
    private void updateViewSize(int w, int h) {
        if (null != centerViewWrapper) {
            LayoutParams lp = (LayoutParams) centerViewWrapper
                    .getLayoutParams();
            if (lp.height != h) {
                lp.height = h;
                centerViewWrapper.requestLayout();
            }
        }
    }

    /**
     * 上拉加载更多完成
     */
    public void pullUpToLoadMoreCompleted() {
        if (isLoadingMore()) {
            pullUpState = State.INIT;
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    setInterceptTouchEvent(true);
                    footerView.setState(State.INIT);
                }
            }, DURATION);
            resetFooterView();
            setInterceptTouchEvent(false);
        }
    }

    /**
     * 下拉刷新完成
     */
    @SuppressLint("SimpleDateFormat")
    public void pullDownToRefreshCompleted() {
        if (isRefreshing()) {
            pullDownState = State.INIT;
            // 回滚动有一个时间，我们在回滚完成后再设置状态为normal
            // 在将LoadingLayout的状态设置为normal之前，我们应该禁止
            // 截断Touch事件，因为设里有一个post状态，如果有post的Runnable
            // 未被执行时，用户再一次发起下拉刷新，如果正在刷新时，这个Runnable
            // 再次被执行到，那么就会把正在刷新的状态改为正常状态，这就不符合期望
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    setInterceptTouchEvent(true);
                    headerView.setState(State.INIT);
                }
            }, DURATION);
            resetHeaderView();
            setInterceptTouchEvent(false);
        }
        if (headerView != null) {
            if (getLastUpdateText() == null) {
                headerView.setLastUpdateText(PullString.LAST_UPDATE_TIME
                        + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            } else {
                headerView.setLastUpdateText(getLastUpdateText());
            }
        }
    }

    /**
     * 得到HeaderView
     */
    public LoadingView getHeaderView() {
        return headerView;
    }

    /**
     * 得到FooterView
     */
    public LoadingView getFooterView() {
        return footerView;
    }

    /**
     * 获取the centerView
     */
    public V getCenterView() {
        return centerView;
    }

    /**
     * 是否截断touch事件
     */
    private boolean isInterceptTouchEvent() {
        return interceptTouchEvent;
    }

    /**
     * 设置是否截断touch事件
     */
    private void setInterceptTouchEvent(boolean interceptTouchEvent) {
        this.interceptTouchEvent = interceptTouchEvent;
    }

    /**
     * 设置拉动监听器
     */
    public void setOnPullListener(OnPullListener pullListener) {
        this.pullListener = pullListener;
    }

    /**
     * 滑动到底部自动加载更多是否可用
     */
    public boolean isAutoLoadMoreEnabled() {
        return autoLoadMoreEnabled;
    }

    /**
     * 设置滑动到底部自动加载更多是否可用
     */
    protected void setAutoLoadMoreEnabled(boolean autoLoadMoreEnabled) {
        this.autoLoadMoreEnabled = autoLoadMoreEnabled;
    }

    /**
     * 是否消费了Touch事件
     */
    private boolean isHandleTouchEvent() {
        return handleTouchEvent;
    }

    /**
     * 设置是否消费了Touch事件
     */
    private void setHandleTouchEvent(boolean handleTouchEvent) {
        this.handleTouchEvent = handleTouchEvent;
    }

    /**
     * 得到最后一次文字
     */
    private String getLastUpdateText() {
        return lastUpdateText;
    }

    /**
     * 设置最后一次更新文字,设定一次即可
     */
    public void setLastUpdateText(String lastUpdateText) {
        this.lastUpdateText = lastUpdateText;
    }

    /**
     * 初始化CenterView（子类进行重写）
     *
     * @param context 上下文
     * @param attrs   属性
     * @return View CenterView
     */
    protected abstract V initCenterView(Context context, AttributeSet attrs);

    /* ======================子类需要重写的方法==================== */

    /**
     * 判断刷新的View是否滑动到顶部
     *
     * @return true表示已经滑动到顶部，否则false
     */
    protected abstract boolean isReadyForPullDown();

    /**
     * 判断刷新的View是否滑动到底
     *
     * @return true表示已经滑动到底部，否则false
     */
    protected abstract boolean isReadyForPullUp();

    class SmoothScrollRunnable implements Runnable {
        /**
         * 动画效果
         */
        private Interpolator mInterpolator;
        /**
         * 结束Y
         */
        private int mScrollToY;
        /**
         * 开始Y
         */
        private int mScrollFromY;
        /**
         * 滑动时间
         */
        private long mDuration;
        /**
         * 是否继续运行
         */
        private boolean mContinueRunning = true;
        /**
         * 开始时刻
         */
        private long mStartTime = -1;
        /**
         * 当前Y
         */
        private int mCurrentY = -1;

        /**
         * 构造方法
         *
         * @param fromY    开始Y
         * @param toY      结束Y
         * @param duration 动画时间
         */
        public SmoothScrollRunnable(int fromY, int toY, long duration) {
            mScrollFromY = fromY;
            mScrollToY = toY;
            mDuration = duration;
            mInterpolator = new DecelerateInterpolator();
        }

        @Override
        public void run() {
            if (mDuration <= 0) {
                scrollTo(0, mScrollToY);
                return;
            }
            if (mStartTime == -1) {
                mStartTime = System.currentTimeMillis();
            } else {
                long oneSecond = 1000; // SUPPRESS CHECKSTYLE
                long normalizedTime = (oneSecond * (System.currentTimeMillis() - mStartTime))
                        / mDuration;
                normalizedTime = Math.max(Math.min(normalizedTime, oneSecond), 0);

                int deltaY = Math.round((mScrollFromY - mScrollToY)
                        * mInterpolator.getInterpolation(normalizedTime / (float) oneSecond));
                mCurrentY = mScrollFromY - deltaY;
                scrollTo(0, mCurrentY);
            }
            if (mContinueRunning && mScrollToY != mCurrentY) {
                PullView.this.postDelayed(this, 16);// SUPPRESS CHECKSTYLE
            }
        }

        /**
         * 停止滑动
         */
        public void stop() {
            mContinueRunning = false;
            removeCallbacks(this);
        }
    }
}
