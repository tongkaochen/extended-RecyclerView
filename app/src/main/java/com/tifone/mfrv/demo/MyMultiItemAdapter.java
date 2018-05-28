package com.tifone.mfrv.demo;

import android.content.Context;
import android.util.Log;

import com.tifone.mfrv.pullload.adapter.CommonViewHolder;
import com.tifone.mfrv.pullload.adapter.MultiItemsAdapter;

public class MyMultiItemAdapter extends MultiItemsAdapter<String> {
    private static final int VIEW_TYPE_TEXT = 1000;
    private static final int VIEW_TYPE_IMAGE = 1001;
    public MyMultiItemAdapter(Context context) {
        super(context);
    }

    @Override
    public int setupItemViewType(int position) {
        Log.e("tifone", "position = " + position);
        if (position % 2 == 0) {
            return VIEW_TYPE_TEXT;
        }
        return VIEW_TYPE_IMAGE;
    }

    @Override
    public int getLayoutId(int viewType) {
        switch (viewType) {
            case VIEW_TYPE_TEXT:
                return R.layout.recycler_view_item;
            case VIEW_TYPE_IMAGE:
                return R.layout.recycler_view_item_image;
        }
        return R.layout.recycler_view_item;
    }

    @Override
    public void convertContent(CommonViewHolder holder, String s) {
        int viewType = holder.viewType;
        switch (viewType) {
            case VIEW_TYPE_TEXT:
                holder.setText(R.id.title, s);
        }
    }
}
