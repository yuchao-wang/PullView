package wang.yuchao.android.library.view.pullview.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * ListViewAdapter基类有两种用法：
 * <pre>
 *     1. 简单用法：不复用子布局ChooseAdapter（showItemView使用ViewHolder）
 *     2. 高级用法：复用子布局用法OrderListViewAdapter（showItemView不使用ViewHolder）
 *     3. 同一个列表，不同布局，子类可重写getItemType，子类也可在initView中判断
 * </pre>
 */
public abstract class BaseListViewAdapter<T, H extends BaseViewHolder> extends BaseAdapter {

    protected Context context;

    /**
     * 子类可以根据需求动态更改
     */
    protected ArrayList<T> dataSource = new ArrayList<T>();

    public BaseListViewAdapter() {
    }

    public BaseListViewAdapter(Context context) {
        this.context = context;
    }

    public ArrayList<T> getDataSource() {
        return dataSource;
    }

    @Override
    public int getCount() {
        return dataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return dataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public void add(T itemData) {
        if (itemData != null) {
            this.dataSource.add(itemData);
        }
        notifyDataSetChanged();
    }

    /**
     * 添加某一个item
     */
    public void add(int position, T itemData) {
        if (itemData != null && position >= 0 && position < dataSource.size()) {
            this.dataSource.add(position, itemData);
        }
        notifyDataSetChanged();
    }

    /**
     * 添加数据队列
     */
    public void addAll(ArrayList<T> dataSource) {
        if (dataSource != null) {
            this.dataSource.addAll(dataSource);
        }
        notifyDataSetChanged();
    }

    /**
     * 移除某一条数据
     */
    public void remove(int position) {
        if (position >= 0 && position < dataSource.size()) {
            this.dataSource.remove(position);
        }
        notifyDataSetChanged();
    }


    /**
     * 更新所有数据
     */
    public void updateAll(ArrayList<T> dataSource) {
        this.dataSource.clear();
        if (dataSource != null) {
            this.dataSource.addAll(dataSource);
        }
        notifyDataSetChanged();
    }

    /**
     * 更新某一个item
     */
    public void updateListViewItem(int position, ListView listView) {
        if (position >= 0 && position < dataSource.size()) {
            //只更新当前屏幕Item
            int firstVisiblePosition = listView.getFirstVisiblePosition();
            int lastVisiblePosition = listView.getLastVisiblePosition();
            Log.e("BaseListViewAdapter", "updateItem() -> position:" + position + " firstVisiblePosition" + firstVisiblePosition + " lastVisiblePosition" + lastVisiblePosition);
            if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
                View view = listView.getChildAt(position - firstVisiblePosition);
                if (view.getTag() instanceof BaseViewHolder) {
                    H holder = (H) view.getTag();
                    showItemView(view, position, holder, dataSource.get(position));
                }
            }
        }
    }

    /**
     * 布局初始化
     * <pre>
     * 1.若我们采用 convertView = inflater.inflate(R.layout.item_list, null);方式填充视图，item布局中的根视图的layout_XX属性会被忽略掉，然后设置成默认的包裹内容方式
     * 2.如果我们想保证item的视图中的参数不被改变，我们需要使用convertView = inflater.inflate(R.layout.item_list, parent,false);这种方式进行视图的填充
     * 3.除了使用这种方式，我们还可以设置item布局的根视图为包裹内容，然后设置内部控件的高度等属性，这样就不会修改显示方式了。
     * </pre>
     */
    protected abstract View initView(ViewGroup parent, int position, T t);

    /**
     * 初始化ViewHolder并绑定监听
     */
    protected abstract H initViewHolder(View view, int position, T t);

    /**
     * 只坐显示操作
     */
    protected abstract View showItemView(View view, int position, H viewHolder, T t);

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        H holder;
        if (view == null) {
            view = initView(parent, position, dataSource.get(position));
            holder = initViewHolder(view, position, dataSource.get(position));
            view.setTag(holder);
        } else {
            holder = (H) view.getTag();
        }
        return showItemView(view, position, holder, dataSource.get(position));
    }
}