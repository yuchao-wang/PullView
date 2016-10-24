package wang.yuchao.android.library.view.pullview.sample;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import wang.yuchao.android.library.view.pullview.OnPullListener;
import wang.yuchao.android.library.view.pullview.PullListView;
import wang.yuchao.android.library.view.pullview.adapter.BaseListViewAdapter;
import wang.yuchao.android.library.view.pullview.adapter.BaseViewHolder;

public class MainActivity extends Activity {

    //默认当前列表有数据
    private PullListView pullListView;
    private ListView listView;
    private MyAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //主View初始化
        pullListView = (PullListView) findViewById(R.id.pull_list_view);

        //ListView添加HeaderView设置初始化
        listView = pullListView.getCenterView();
        listView.setDivider(new ColorDrawable(getResources().getColor(R.color.app_background)));
        listView.setDividerHeight(1);

        //隐藏滚动条
        listView.setFastScrollEnabled(false);
        listView.setVerticalScrollBarEnabled(false);
        listView.setScrollbarFadingEnabled(true);

        listAdapter = new MyAdapter();
        // 测试 : 添加header会不会影响position
        // TextView headerView = new TextView(this);
        // headerView.setText("哈哈哈，我是测试HeaderView");
        // listView.addHeaderView(headerView);
        listView.setAdapter(listAdapter);

        //设置监听
        pullListView.setOnPullListener(new OnPullListener() {
            @Override
            public void onPullDownToRefresh() {
                getList(true);
            }

            @Override
            public void onPullUpToLoadMore() {
                getList(false);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "点击" + position, Toast.LENGTH_SHORT).show();
            }
        });

        //第一次自动加载
        pullListView.setAutoPullDownToRefresh(0);

        // 测试局部更新
        findViewById(R.id.tv_hint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listAdapter.updateListViewItem(7, listView);
            }
        });
    }


    private void setPullView(boolean hasData, boolean hasMoreData, boolean showFooterView) {
        pullListView.showFooterView(showFooterView);
        pullListView.setHaveMoreData(hasMoreData);
    }

    public void getList(final boolean isRefresh) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                pullListView.pullDownToRefreshCompleted();
                pullListView.pullUpToLoadMoreCompleted();

                ArrayList<String> data = new ArrayList<String>();
                for (int i = 0; i < 10; i++) {
                    data.add("数据" + i);
                }

                if (isRefresh) {
                    listAdapter.updateAll(data);
                } else {
                    listAdapter.addAll(data);
                }

                setPullView(true, true, true);
            }
        }.execute();
    }

    private class MyAdapter extends BaseListViewAdapter<String, MyViewHolder> {

        @Override
        protected View initView(ViewGroup parent, int position, String s) {
            return LayoutInflater.from(MainActivity.this).inflate(R.layout.list_item, null, false);
        }

        @Override
        protected MyViewHolder initViewHolder(View view, int position, String s) {
            return new MyViewHolder(view, position, s);
        }

        @Override
        protected View showItemView(View view, int position, MyViewHolder viewHolder, String s) {
            Log.e("wang", "数据" + s);
            viewHolder.textView.setText("数据：" + s);
            return view;
        }
    }

    private class MyViewHolder extends BaseViewHolder {

        public TextView textView;

        public <T> MyViewHolder(View view, int position, T t) {
            super(view, position, t);

            this.textView = (TextView) view.findViewById(R.id.textview);
        }
    }
}
