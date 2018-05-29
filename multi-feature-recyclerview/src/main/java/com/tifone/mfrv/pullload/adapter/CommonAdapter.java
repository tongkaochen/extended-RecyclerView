package com.tifone.mfrv.pullload.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 单一类型的适配器
 * @param <T> bean类型
 */

public abstract class CommonAdapter<T> extends RecyclerView.Adapter<CommonViewHolder> {
    public static final int BASE_VIEW_TYPE = 1000;
    private List<T> mDataSet;
    private Context mContext;
    public CommonAdapter(Context context) {
        mContext = context;
        mDataSet = new ArrayList<>();
    }
    public void setDataSet(@NonNull List<T> dataSet) {
        mDataSet = dataSet;
        notifyDataSetChanged();
        //dataSet.clear();
    }
    public void addDataSet(@NonNull List<T> dataSet) {
        mDataSet.addAll(dataSet);
        notifyDataSetChanged();
        dataSet.clear();
    }

    @NonNull
    @Override
    public CommonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return CommonViewHolder.getHolder(mContext, parent, getLayoutId(viewType), viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull CommonViewHolder holder, int position) {
        convertContent(holder, mDataSet.get(position));
    }
    public <T> T getItem(int position) {
        return (T) mDataSet.get(position);
    }
    public List<T> getDataSet() {
        return mDataSet;
    }
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
    public abstract int getLayoutId(int viewType);
    public abstract void convertContent(CommonViewHolder holder, T t);
}
