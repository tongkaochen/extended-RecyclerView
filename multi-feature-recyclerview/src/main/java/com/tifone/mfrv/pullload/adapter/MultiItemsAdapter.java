package com.tifone.mfrv.pullload.adapter;

import android.content.Context;

public abstract class MultiItemsAdapter<T> extends CommonAdapter<T> {

    public MultiItemsAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemViewType(int position) {
        return setupItemViewType(position);
    }
    public abstract int setupItemViewType(int position);
}
