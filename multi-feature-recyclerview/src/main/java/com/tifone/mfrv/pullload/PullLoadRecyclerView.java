package com.tifone.mfrv.pullload;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.tifone.mfrv.pullload.base.LoadMoreViewCreator;
import com.tifone.mfrv.pullload.base.PullRefreshViewCreator;
import com.tifone.mfrv.pullload.creator.DefaultLoadMoreViewCreator;
import com.tifone.mfrv.pullload.creator.DefaultRefreshViewCreator;

import java.util.ArrayList;
import java.util.List;

public class PullLoadRecyclerView extends WrapRecyclerView {
    private static final String TAG = "tifone";

    // 下拉刷新状态
    // 正常状态
    public static final int REFRESH_STATE_NORMAL = PullLoadState.REFRESH_STATE_NORMAL;
    // 下拉刷新
    public static final int REFRESH_STATE_PULL_DOWN_REFRESH = PullLoadState.REFRESH_STATE_PULL_DOWN_REFRESH;
    // 松开刷新
    public static final int REFRESH_STATE_LOOSEN_REFRESH = PullLoadState.REFRESH_STATE_LOOSEN_REFRESH;
    // 正在刷新
    public static final int REFRESH_STATE_REFRESHING = PullLoadState.REFRESH_STATE_REFRESHING;
    // 刷新完成
    public static final int REFRESH_STATE_COMPLETE = PullLoadState.REFRESH_STATE_COMPLETE;
    // 刷新失败
    public static final int REFRESH_STATE_FAIL = PullLoadState.REFRESH_STATE_FAIL;

    // 上拉加载状态
    // normal
    public static final int LOAD_STATE_NORMAL = PullLoadState.LOAD_STATE_NORMAL;
    // 上拉准备加载
    public static final int LOAD_STATE_PULL_UP_LOAD = PullLoadState.LOAD_STATE_PULL_UP_LOAD;
    // 松开后加载
    public static final int LOAD_STATE_LOOSEN_LOAD = PullLoadState.LOAD_STATE_LOOSEN_LOAD;
    // 正在加载
    public static final int LOAD_STATE_LOADING = PullLoadState.LOAD_STATE_LOADING;
    // 加载完成
    public static final int LOAD_STATE_COMPLETE = PullLoadState.LOAD_STATE_COMPLETE;
    // 加载失败
    public static final int LOAD_STATE_FAIL = PullLoadState.LOAD_STATE_FAIL;

    // 向上拖拽一定距离后开始加载
    public static final String LOAD_BEHAVIOR_STYLE_DRAG = "load_behavior_style_drag";
    // 到达底部直接加载
    public static final String LOAD_BEHAVIOR_STYLE_NORMAL = "load_behavior_style_normal";

    private LoadMoreViewCreator mLoadMoreCreator;
    private PullRefreshViewCreator mPullRefreshCreator;
    private int mFingerDownY = -1;
    private float mDragIndex = 0.35f;
    private OnRefreshListener mPullRefreshListener;
    private OnLoadListener mLoadListener;
    private View mLoadMoreView;
    private View mPullRefreshView;
    private int mLoadMoreViewHeight;
    private boolean mLoadViewDrag;
    private int mTotalItemCounts;
    private int mLayoutManagerItemCounts;
    private boolean mCurrentDrag;
    private int mPullRefreshState;
    private int mOldPullRefreshState = -1;
    private int mLoadMoreState;
    private int mOldLoadMoreState = -1;
    private int mRefreshViewHeight;
    private String mLoadBehavior = LOAD_BEHAVIOR_STYLE_DRAG;

    public void setOnRefreshListener(OnRefreshListener listener) {
        mPullRefreshListener = listener;
    }

    public void setOnLoadListener(OnLoadListener listener) {
        mLoadListener = listener;
    }

    public interface OnLoadListener {
        void onLoadStarted();
    }

    public interface OnRefreshListener {
        void onRefreshStarted();
    }

    public PullLoadRecyclerView(Context context) {
        this(context, null);
    }

    public PullLoadRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullLoadRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        // 默认的ViewCreator
        mLoadMoreCreator = new DefaultLoadMoreViewCreator();
        mPullRefreshCreator = new DefaultRefreshViewCreator();
        // 默认的布局管理器
        setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        setRefreshView();
        setLoadMoreView();
    }

    /**
     * 设置上拉加载的ViewCreator
     *
     * @param creator 其他继承自LoadMoreViewCreator的扩展creator
     */
    public void setLoadMoreCreator(@NonNull LoadMoreViewCreator creator) {
        // 移除已有的LoadMoreView
        removeFooterView(mLoadMoreView);
        mLoadMoreCreator = creator;
        // 加入新的LoadMoreView
        setLoadMoreView();
    }

    /**
     * 设置下拉刷新的ViewCreator
     *
     * @param creator 其他继承自PullRefreshViewCreator的扩展creator
     */
    public void setRefreshViewCreator(@NonNull PullRefreshViewCreator creator) {
        // 移除久的PullRefreshView
        removeHeaderView(mPullRefreshView);
        mPullRefreshCreator = creator;
        // 加入新的PullRefreshView
        setRefreshView();
    }

    /**
     * 执行刷新动作
     */
    public void notifyDoRefresh() {
        setPullRefreshState(REFRESH_STATE_REFRESHING);
        doRefreshViewAnimator(mPullRefreshView.getLayoutParams().height, mRefreshViewHeight);
    }

    /**
     * 数据已加载
     */
    public void notifyLoadComplete() {
        setLoadMoreState(LOAD_STATE_COMPLETE);
    }

    /**
     * 数据已加载
     */
    public void notifyRefreshCompleted() {
        setPullRefreshState(REFRESH_STATE_COMPLETE);
    }

    /**
     * 数据加载失败
     */
    public void notifyRefreshFailed() {
        setPullRefreshState(REFRESH_STATE_FAIL);
    }

    /**
     * 数据加载失败
     */
    public void notifyLoadFailed() {
        setPullRefreshState(LOAD_STATE_FAIL);
    }

    /**
     * 停止加载数据
     */
    public void notifyRefreshStopped() {
        setPullRefreshState(REFRESH_STATE_NORMAL);
        resetPullRefreshView();
    }

    /**
     * 停止数据加载
     */
    public void notifyLoadStopped() {
        setLoadMoreState(LOAD_STATE_NORMAL);
        resetLoadMoreView();
    }

    /**
     * 设置上拉加载的行为
     *
     * @param behavior {@value LOAD_BEHAVIOR_STYLE_DRAG} {@value LOAD_BEHAVIOR_STYLE_NORMAL}
     */
    public void setLoadMoreBehavior(String behavior) {
        List<String> targets = new ArrayList<>();
        targets.add(LOAD_BEHAVIOR_STYLE_DRAG);
        targets.add(LOAD_BEHAVIOR_STYLE_NORMAL);
        if (checkAvailable(behavior, targets)) {
            mLoadBehavior = behavior;
        }
        targets.clear();
    }

    private boolean isDragBehavior() {
        return TextUtils.equals(mLoadBehavior, LOAD_BEHAVIOR_STYLE_DRAG);
    }

    private boolean checkAvailable(String behavior, List<String> targets) {
        if (TextUtils.isEmpty(behavior)) {
            return false;
        }
        for (String target : targets) {
            if (TextUtils.equals(behavior, target)) {
                return true;
            }
        }
        return false;
    }

    private void setRefreshView() {
        mPullRefreshView = mPullRefreshCreator.getView(getContext(), this);
        addHeaderView(mPullRefreshView);
    }

    private void setLoadMoreView() {
        mLoadMoreView = mLoadMoreCreator.getView(getContext(), this);
        addFooterView(mLoadMoreView);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            // 当RecyclerView的Item有注册OnClick事件时，在onTouchEvent中不能正确得到ACTION_DOWN
            case MotionEvent.ACTION_DOWN:
                if (mFingerDownY == -1) {
                    mFingerDownY = (int) e.getRawY();
                }
                break;
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                // 获取手指拖拽的距离
                int distanceY = (int) ((e.getRawY() - mFingerDownY) * mDragIndex);
                logger("distanceY = " + distanceY);
                logger("mFingerDownY = " + mFingerDownY);
                // 上拉加载逻辑, 向上拖拽一定距离，释放后开始加载
                if (distanceY < 0) {
                    if (isDragBehavior() && isLoadMoreAvailable()) {

                        resolveDragToLoadMore(Math.abs(distanceY));
                        return false;
                    }
                }

                // 未到达顶部
                if (!isScrollToTop() || mPullRefreshState == REFRESH_STATE_REFRESHING) {
                    return super.onTouchEvent(e);
                }

                // 下拉刷新逻辑
                if (distanceY > 0) {
                    setPullRefreshViewHeight(distanceY);
                    updateRefreshStatus(distanceY);
                    mCurrentDrag = true;
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                mFingerDownY = -1;
                if (mCurrentDrag) {
                    restorePullRefreshView();
                }
                if (mLoadViewDrag) {
                    restoreLoadMoreView();
                }
                break;
        }

        return super.onTouchEvent(e);
    }

    private void resolveDragToLoadMore(int distance) {
        setLoadMoreViewHeight(distance);
        updateLoadMoreStatus(distance);
        mLoadViewDrag = true;
    }

    private void setPullRefreshState(int state) {
        if (mPullRefreshState == state) {
            return;
        }
        mOldPullRefreshState = mPullRefreshState;
        mPullRefreshState = state;
        onPullRefreshChanged();
    }

    private void onPullRefreshChanged() {
        switch (mPullRefreshState) {
            case REFRESH_STATE_NORMAL:
                break;
            case REFRESH_STATE_LOOSEN_REFRESH:
                break;
            case REFRESH_STATE_PULL_DOWN_REFRESH:
                break;
            case REFRESH_STATE_REFRESHING:
                mPullRefreshCreator.onRefreshing();
                if (isNotNull(mPullRefreshListener)) {
                    mPullRefreshListener.onRefreshStarted();
                }
                break;
            case REFRESH_STATE_COMPLETE:
                mPullRefreshCreator.onRefreshComplete();
                resetPullRefreshViewDelayed();
                break;
            case REFRESH_STATE_FAIL:
                mPullRefreshCreator.onRefreshFailed();
                break;
            default:
                Log.w(TAG, "State " + mPullRefreshState + " is not define");
        }
        mPullRefreshCreator.onStateChanged(mOldPullRefreshState, mPullRefreshState);
    }

    // Refresh view
    private void setLoadMoreState(int state) {
        if (mLoadMoreState == state) {
            return;
        }
        mOldLoadMoreState = mLoadMoreState;
        mLoadMoreState = state;
        onLoadStateChanged();
    }

    private boolean isNotNull(Object object) {
        return object != null;
    }

    private void onLoadStateChanged() {
        switch (mLoadMoreState) {
            case LOAD_STATE_NORMAL:
                break;
            case LOAD_STATE_LOOSEN_LOAD:
                break;
            case LOAD_STATE_PULL_UP_LOAD:
                break;
            case LOAD_STATE_LOADING:
                mLoadMoreCreator.onLoading();
                doLoadMoreAnimator(mLoadMoreView.getLayoutParams().height, mLoadMoreViewHeight);
                if (isNotNull(mLoadListener)) {
                    mLoadListener.onLoadStarted();
                }
                break;
            case LOAD_STATE_COMPLETE:
                mLoadMoreCreator.onLoadComplete();
                resetLoadMoreViewDelayed();
                break;
            case LOAD_STATE_FAIL:
                mLoadMoreCreator.onLoadFailed();
                break;
            default:
                Log.w(TAG, "State " + mLoadMoreState + " is not define");
        }
        mLoadMoreCreator.onStateChanged(mOldLoadMoreState, mLoadMoreState);
    }

    private void restoreLoadMoreView() {
        int currentHeight = mLoadMoreView.getLayoutParams().height;
        if (mLoadMoreState == LOAD_STATE_LOOSEN_LOAD) {
            setLoadMoreState(LOAD_STATE_LOADING);
        } else {
            setLoadMoreState(LOAD_STATE_NORMAL);
            doLoadMoreAnimator(currentHeight, 0);
        }
        mLoadViewDrag = false;
    }

    private void resetLoadMoreViewDelayed() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                resetLoadMoreView();
            }
        }, 500);
    }

    private void resetLoadMoreView() {
        doLoadMoreAnimator(mLoadMoreView.getLayoutParams().height, 0);
        setLoadMoreState(LOAD_STATE_NORMAL);
    }

    private void restorePullRefreshView() {
        int currentHeight = mPullRefreshView.getLayoutParams().height;
        int finalHeight = 0;
        if (mPullRefreshState == REFRESH_STATE_LOOSEN_REFRESH) {
            finalHeight = mRefreshViewHeight;
            setPullRefreshState(REFRESH_STATE_REFRESHING);
        }
        int distance = currentHeight - finalHeight;
        Log.e("tifone", "distance = " + distance);
        doRefreshViewAnimator(currentHeight, finalHeight);
        mCurrentDrag = false;
    }

    private void doRefreshViewAnimator(int from, int to) {
        ValueAnimator animator = ObjectAnimator.ofInt(from,
                to).setDuration(Math.abs(from - to));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int height = (int) animation.getAnimatedValue();
                setPullRefreshViewHeight(height);
            }
        });
        animator.start();
    }

    private void resetPullRefreshViewDelayed() {
        logger("resetRefreshDelayed");
        postDelayed(new Runnable() {
            @Override
            public void run() {
                resetPullRefreshView();
            }
        }, 500);
    }

    private void resetPullRefreshView() {
        logger("resetRefresh");
        doRefreshViewAnimator(mPullRefreshView.getLayoutParams().height, 0);
        setPullRefreshState(REFRESH_STATE_NORMAL);
    }

    private void setLoadMoreViewHeight(int height) {
        logger("setLoadMoreViewHeight = " + height);
        ViewGroup.LayoutParams params = mLoadMoreView.getLayoutParams();
        params.height = height;
        mLoadMoreView.setLayoutParams(params);
    }

    private void doLoadMoreAnimator(int from, int to) {
        logger("from = " + from + " to = " + to);
        ValueAnimator animator = ObjectAnimator.ofInt(from,
                to).setDuration(Math.abs(from - to));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int height = (int) animation.getAnimatedValue();
                setLoadMoreViewHeight(height);
            }
        });
        animator.start();
    }

    private void setPullRefreshViewHeight(int height) {
        ViewGroup.LayoutParams params = mPullRefreshView.getLayoutParams();
        if (height < 0) {
            height = 0;
        }
        params.height = height;
        mPullRefreshView.setLayoutParams(params);
    }

    /**
     * 根据View的高度更新LoadMore的状态
     *
     * @param height LoadMoreView的高度
     */
    private void updateLoadMoreStatus(int height) {
        if (height <= 0) {
            setLoadMoreState(LOAD_STATE_NORMAL);
        } else if (height < mLoadMoreViewHeight) {
            setLoadMoreState(LOAD_STATE_PULL_UP_LOAD);
        } else {
            setLoadMoreState(LOAD_STATE_LOOSEN_LOAD);
        }
    }

    /**
     * 根据View的高度更新RefreshView的状态
     *
     * @param height RefreshView的高度
     */
    private void updateRefreshStatus(int height) {
        if (height <= 0) {
            setPullRefreshState(REFRESH_STATE_NORMAL);
        } else if (height < mRefreshViewHeight * 2) {
            setPullRefreshState(REFRESH_STATE_PULL_DOWN_REFRESH);
        } else {
            setPullRefreshState(REFRESH_STATE_LOOSEN_REFRESH);
        }
    }

    private boolean isChildrenFullScreen() {
        int totalItemCount = getAdapter().getItemCount();
        int layoutManagerChildCount = getLayoutManager().getChildCount();
        return totalItemCount > layoutManagerChildCount;
    }

    private void logger(String msg) {
        Log.e("tifone", msg);
    }

    private boolean isScrollToBottom() {
        LinearLayoutManager llm = (LinearLayoutManager) getLayoutManager();
        return llm.findLastCompletelyVisibleItemPosition() >= getAdapter().getItemCount() - 1;
    }

    private boolean isScrollToTop() {
        LinearLayoutManager llm = (LinearLayoutManager) getLayoutManager();
        return llm.findFirstCompletelyVisibleItemPosition() < 1;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            if (mPullRefreshView != null && mRefreshViewHeight <= 0) {
                mRefreshViewHeight = mPullRefreshView.getMeasuredHeight();
                if (mRefreshViewHeight > 0) {
                    // 隐藏头部刷新的view
                    setPullRefreshViewHeight(0);
                    setPullRefreshState(REFRESH_STATE_NORMAL);
                }
            }
            initLoadMoreViewOriginHeight();
        }
    }

    private boolean isLoadMoreAvailable() {
        return isScrollToBottom() && isChildrenFullScreen() && mLoadMoreState != LOAD_STATE_LOADING;
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        // 获取Load more view的实际高度，只有当View加到RecyclerView中才能得到正确的值
        initLoadMoreViewOriginHeight();
        logger("onScrollStateChanged = " + state);
        if (isDragBehavior()) {
            return;
        }
        if (mLoadMoreView.getLayoutParams().height == 0 && isChildrenFullScreen()) {
            // 显示LoadMoreView
            setLoadMoreViewHeight(mLoadMoreViewHeight);
        }
        if (isLoadMoreAvailable()) {
            setLoadMoreState(LOAD_STATE_LOADING);
        }
    }


    private void initLoadMoreViewOriginHeight() {
        if (mLoadMoreView != null && mLoadMoreViewHeight <= 0) {
            mLoadMoreViewHeight = mLoadMoreView.getMeasuredHeight();
            if (mLoadMoreViewHeight > 0 && (isDragBehavior() || !isChildrenFullScreen())) {
                setLoadMoreViewHeight(0);
            }
        }
    }
}
