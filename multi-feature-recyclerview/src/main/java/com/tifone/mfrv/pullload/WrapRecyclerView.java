package com.tifone.mfrv.pullload;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.tifone.mfrv.pullload.adapter.WrapHeaderFooterAdapter;
import com.tifone.mfrv.pullload.base.BaseViewCreator;
import com.tifone.mfrv.pullload.creator.DefaultEmptyViewCreator;

public class WrapRecyclerView extends RecyclerView {
    private WrapHeaderFooterAdapter mWrapAdapter;
    private Adapter mContentAdapter;
    private static final String TAG = "tifone";
    private BaseViewCreator mEmptyViewCreator;

    public WrapRecyclerView(Context context) {
        this(context, null);
    }

    public WrapRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WrapRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    private void init() {

    }

    @Override
    public void setAdapter(Adapter adapter) {
        if(adapter == null) {
            throw new NullPointerException("RecyclerView adapter should not be null");
        }
        if (adapter instanceof WrapHeaderFooterAdapter) {
            mWrapAdapter = (WrapHeaderFooterAdapter) adapter;
        } else {
            mWrapAdapter = new WrapHeaderFooterAdapter(adapter);
            mContentAdapter = adapter;
        }
        super.setAdapter(mWrapAdapter);
        mWrapAdapter.adjustSpanSize(this);
        setEmptyViewCreator(new DefaultEmptyViewCreator());
    }
    public void addHeaderView(View view) {
        if (mWrapAdapter == null) {
            Log.w(TAG, "addHeaderView need to setAdapter first, return");
            return;
        }
        mWrapAdapter.addHeaderView(view);
    }
    public void addFooterView(View view) {
        if (mWrapAdapter == null) {
            Log.w(TAG, "addFooterView need to setAdapter first, return");
            return;
        }
        mWrapAdapter.addFooterView(view);
    }
    public int removeHeaderView(View view) {
        if (mWrapAdapter == null) {
            Log.w(TAG, "removeHeaderView need to setAdapter first, return");
            return -1;
        }
        return mWrapAdapter.removeHeader(view);
    }
    public int removeFooterView(View view) {
        if (mWrapAdapter == null) {
            Log.w(TAG, "removeFooterView need to setAdapter first, return");
            return -1;
        }
        return mWrapAdapter.removeFooter(view);
    }
    public void setEmptyViewCreator(@NonNull BaseViewCreator creator) {
        mEmptyViewCreator = creator;
        ViewGroup parent = (ViewGroup) getParent();
        View emptyView = mEmptyViewCreator.getView(getContext(), this);
        parent.addView(emptyView);
        mWrapAdapter.setEmptyView(emptyView);
    }
}
