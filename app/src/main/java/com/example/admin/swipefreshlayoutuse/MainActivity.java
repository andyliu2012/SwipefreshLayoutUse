package com.example.admin.swipefreshlayoutuse;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private List<String> data = new ArrayList<>();
    public boolean isLoading;
    private RefreshRecyclerAdapter adapter = new RefreshRecyclerAdapter(this, data);
    private Handler handler = new Handler();
    private Toolbar toolbar;
    int topcount = 1;
    int footcount = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.SwipeRefreshLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.notice);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initData();
        initView();


    }

    private void initView() {

        //设置加载进度的颜色变化值
        swipeRefreshLayout.setColorSchemeResources(R.color.blueStatus,R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        //设置一进入开始刷新
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        //设置下拉刷新的监听器
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addTopNewData();
                    }
                }, 2000);
            }
        });
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        //通过recyclerView的onscrolllistener的监听来实现上拉加载更多的功能
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            //滚动的三种状态包括SCROLL_STATE_IDEL 离开状态 SCROLL_STATE_DRAGGING 手指触摸 SCROLL_STATE_SETLING 加速滑动的时候
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d("test", "StateChanged = " + newState);


            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d("test", "onScrolled");
                 // 获取最后一个可见条目
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == adapter.getItemCount()) {
                    Log.d("test", "loading executed");
                    //获取刷新状态
                    boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                    if (isRefreshing) {
                        adapter.notifyItemRemoved(adapter.getItemCount());
                        return;
                    }
                    if (!isLoading) {
                        isLoading = true;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                addNewData();
                                Log.d("test", "load more completed");
                                isLoading = false;
                            }
                        }, 1000);
                    }
                }
            }
        });

        //添加点击事件
        adapter.setOnItemClickListener(new RefreshRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d("test", "item position = " + position);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    /**
     * 下拉加载更多
     */
    private void addTopNewData() {

        for (int i = topcount; i < topcount +6; i++) {
            data.add(0,"下拉加载的第"+i+"条数据");

        }
        topcount += 6;
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }


    public void initData() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, 1500);

    }

    /**
     * 获取测试数据
     */
    private void getData() {
        for (int i =0; i <10; i++) {
            data.add(i,"第"+i+"条数据");
        }
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
       // adapter.notifyItemRemoved(adapter.getItemCount());
    }

    /**
     * 上拉加载更多
     */
    public void addNewData() {

        for (int i = footcount; i < footcount+ 6; i++) {
            data.add(data.size(),"上拉加载的第"+i+"条数据");
        }
        footcount += 6;
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
        //adapter.notifyItemRemoved(adapter.getItemCount());
    }

}
