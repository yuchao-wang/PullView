package wang.yuchao.android.library.view.pullview.adapter;

import android.view.View;

public class BaseViewHolder {
    protected View view;
    protected int position;

    public <T> BaseViewHolder(View view, int position, T t) {
        this.view = view;
        this.position = position;
    }
}
