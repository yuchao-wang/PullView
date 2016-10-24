package wang.yuchao.android.library.view.pullview;

/**
 * 拉动View需要回调的接口【拉动view所触发的监听器】
 */
public interface OnPullListener {

    /** 下拉时需要回调的方法 */
    public void onPullDownToRefresh();

    /** 上拉加载更多时需要回调的方法 */
    public void onPullUpToLoadMore();

}