package com.tifone.mfrv.pullload.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class CommonViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> mViewList;
    private View mItemView;
    public int viewType;
    public CommonViewHolder(View itemView, int viewType) {
        super(itemView);
        mItemView = itemView;
        mViewList = new SparseArray<>();
        this.viewType = viewType;
    }
    public static CommonViewHolder getHolder(Context context, ViewGroup parent, int layoutId, int viewType) {
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new CommonViewHolder(view, viewType);
    }
    public <T extends View> T getView(int resId) {
        View view = mViewList.get(resId);
        if (null == view) {
            view = itemView.findViewById(resId);
            mViewList.put(resId, view);
        }
        return (T) view;
    }
    // 为TextView设置文本
    public void setText(int resId, String content) {
        TextView textView = getView(resId);
        if (textView == null) {
            return;
        }
        textView.setText(content);
    }
    // 为ImageView设置图片
    public void setImageResource(int resId, int resourceId) {
        ImageView imageView = getView(resId);
        if (imageView == null) {
            return;
        }
        imageView.setImageResource(resourceId);
    }

    // 设置点击事件
    public void setOnClickListener(int resId, View.OnClickListener listener) {
        View view = getView(resId);
        if (view == null) {
            return;
        }
        view.setOnClickListener(listener);
    }
}
