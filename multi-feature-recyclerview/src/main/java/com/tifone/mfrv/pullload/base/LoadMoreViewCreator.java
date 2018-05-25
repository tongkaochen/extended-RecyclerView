package com.tifone.mfrv.pullload.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public abstract class LoadMoreViewCreator extends BaseViewCreator{

    /**
     * 视图状态更改
     * @param oldState 上拉加载的状态
     * @param newState 上拉加载的状态
     */
    public abstract void onStateChanged(int oldState, int newState);

    /**
     * 正在刷新
     */
    public abstract void onLoading();

    /**
     * 刷新完成
     */
    public abstract void onLoadComplete();

    /**
     * 刷新失败
     */
    public abstract void onLoadFailed();

}
