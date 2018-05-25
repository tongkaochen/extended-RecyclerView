package com.tifone.mfrv.pullload;

public final class PullLoadState {
    private static final int BASE = 1000;

    // 下拉刷新状态
    // 正常状态
    public static final int REFRESH_STATE_NORMAL = BASE + 1;
    // 下拉刷新
    public static final int REFRESH_STATE_PULL_DOWN_REFRESH = BASE + 2;
    // 松开刷新
    public static final int REFRESH_STATE_LOOSEN_REFRESH = BASE + 3;
    // 正在刷新
    public static final int REFRESH_STATE_REFRESHING = BASE + 4;
    // 刷新完成
    public static final int REFRESH_STATE_COMPLETE = BASE + 5;
    // 刷新失败
    public static final int REFRESH_STATE_FAIL = BASE + 6;

    // 上拉加载状态
    // normal
    public static final int LOAD_STATE_NORMAL = BASE + 21;
    // 上拉准备加载
    public static final int LOAD_STATE_PULL_UP_LOAD = BASE + 22;
    // 松开后加载
    public static final int LOAD_STATE_LOOSEN_LOAD = BASE + 23;
    // 正在加载
    public static final int LOAD_STATE_LOADING = BASE + 24;
    // 加载完成
    public static final int LOAD_STATE_COMPLETE = BASE + 25;
    // 加载失败
    public static final int LOAD_STATE_FAIL = BASE + 26;


    private PullLoadState(){}

}
