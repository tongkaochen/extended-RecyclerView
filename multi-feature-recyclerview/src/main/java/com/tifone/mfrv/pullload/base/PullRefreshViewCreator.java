package com.tifone.mfrv.pullload.base;

public abstract class PullRefreshViewCreator extends BaseViewCreator {

    /**
     * 视图状态更改
     * @param oldState 下拉刷新的状态
     * @param newState 下拉刷新的状态
     */
    public abstract void onStateChanged(int oldState, int newState);

    /**
     * 正在刷新
     */
    public abstract void onRefreshing();

    /**
     * 刷新完成
     */
    public abstract void onRefreshComplete();

    /**
     * 刷新失败
     */
    public abstract void onRefreshFailed();

}
