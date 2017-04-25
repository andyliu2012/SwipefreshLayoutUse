package com.example.admin.swipefreshlayoutuse;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ListView;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**自定义View继承SwipeRefreshLayout，添加上拉加载更多的布局属性
 * Created by admin on 2017/4/24.
 */

public class SwipeRefreshView extends SwipeRefreshLayout {
    private final int mScaledTouchSlop;
    private  boolean isLoading;
    private View footview;
    private RecyclerView mRecyclerView;
    private OnLoadListener mOnLoadListener;


    public SwipeRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        footview = View.inflate(context, R.layout.view_footer,null);
        mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        System.out.println("====" + mScaledTouchSlop);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // 获取RecyclerView,设置RecyclerView的布局位置
        if (mRecyclerView == null) {
            // 判断容器有多少个孩子
            if (getChildCount() > 0) {
                // 判断第一个孩子是不是RecyclerView
                if (getChildAt(0) instanceof RecyclerView) {
                    // 创建RecyclerView对象
                    mRecyclerView = (RecyclerView) getChildAt(0);

                    // 设置ListView的滑动监听
                    setRecyclerViewOnScroll();
                }
            }
        }
    }

    private void setRecyclerViewOnScroll() {
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                // 移动过程中判断时候能下拉加载更多
                if (canLoadMore()) {
                    // 加载数据
                    loadData();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }
    private float mDownY, mUpY;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 移动的起点
                mDownY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                // 移动过程中判断时候能下拉加载更多
                if (canLoadMore()) {
                    // 加载数据
                    loadData();
                }

                break;
            case MotionEvent.ACTION_UP:
                // 移动的终点
                mUpY = ev.getY();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
    /**
     * 判断是否满足加载更多条件
     *
     * @return
     */
    private boolean canLoadMore() {


        // 1. 是上拉状态
        boolean condition1 = (mDownY - mUpY) >= mScaledTouchSlop;
        if (condition1) {
            System.out.println("是上拉状态");
        }

        // 2. 当前页面可见的item是最后一个条目
        boolean condition2 = false;
        if (mRecyclerView != null && mRecyclerView.getAdapter() != null) {
            RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
            LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
            //获取最后一个可见view的位置
            int lastItemPosition = linearManager.findLastVisibleItemPosition();
            condition2 = lastItemPosition== (mRecyclerView.getAdapter().getItemCount() - 1);
        }

        if (condition2) {
            System.out.println("是最后一个条目");
        }
        // 3. 正在加载状态
        boolean condition3 = !isLoading;
        if (condition3) {
            System.out.println("不是正在加载状态");
        }
        return condition1 && condition2 && condition3;
    }

    /**
     * 处理加载数据的逻辑
     */
    private void loadData() {
        System.out.println("加载数据...");
        if (mOnLoadListener != null) {
            // 设置加载状态，让布局显示出来
            setLoading(true);
            mOnLoadListener.onLoad();
        }

    }
    /**
     * 设置加载状态，是否加载传入boolean值进行判断
     *
     * @param loading
     */
    public void setLoading(boolean loading) {
        // 修改当前的状态
        isLoading = loading;
        if (isLoading) {
            // 显示布局
         //   mRecyclerView.addFooterView(mFooterView);
       //     mRecyclerView.add
        } else {
            // 隐藏布局
       //     mRecyclerView.removeFooterView(mFooterView);

            // 重置滑动的坐标
            mDownY = 0;
            mUpY = 0;
        }
    }
    /**
     * 上拉加载的接口回调
     */

    public interface OnLoadListener {
        void onLoad();
    }

    public void setOnLoadListener(OnLoadListener listener) {
        this.mOnLoadListener = listener;
    }
}
