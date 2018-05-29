package com.tifone.mfrv.pullload.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseViewCreator {
    /**
     * 获取View
     * @param context 上下文
     * @param parent 父布局
     * @return 相应的view
     */
    private View mView;
    public @NonNull View getView(Context context, ViewGroup parent) {
        // 如果view已经初始化，直接返回
        if (mView == null) {
            mView = LayoutInflater.from(context).inflate(getLayoutId(), parent, false);
        }
        bindView(mView);
        return mView;
    }
    public abstract int getLayoutId();
    public abstract void bindView(View root);
}
