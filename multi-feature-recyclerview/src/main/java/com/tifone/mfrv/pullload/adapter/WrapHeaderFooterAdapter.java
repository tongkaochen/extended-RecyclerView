package com.tifone.mfrv.pullload.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * 往已有的adapter中加入头部和尾部
 */
public class WrapHeaderFooterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int BASE_VIEW_TYPE = 1000;
    private static final int HEADER_VIEW_TYPE = BASE_VIEW_TYPE;
    private static final int FOOTER_VIEW_TYPE = BASE_VIEW_TYPE + 1;
    private RecyclerView.Adapter mInnerAdapter;
    private SparseArray<View> mHeaderViews = new SparseArray<>();
    private SparseArray<View> mFooterViews = new SparseArray<>();
    private RecyclerView.AdapterDataObserver mDataObserver;

    public WrapHeaderFooterAdapter(@NonNull RecyclerView.Adapter adapter) {
        mInnerAdapter = adapter;
        mDataObserver = new ChildrenAdapterDataObserver(this);
    }

    public int getHeaderSize() {
        return mHeaderViews.size();
    }

    public int getFooterSize() {
        return mFooterViews.size();
    }

    public void addHeaderView(View view) {
        if (mHeaderViews.indexOfValue(view) < 0) {
            mHeaderViews.put(HEADER_VIEW_TYPE + view.hashCode(), view);
            notifyItemInserted(getHeaderSize());
        }
    }

    public void addFooterView(View view) {
        if (mFooterViews.indexOfValue(view) < 0) {
            mFooterViews.put(FOOTER_VIEW_TYPE + view.hashCode(), view);
            notifyItemInserted(getContentItemsSize() + getHeaderSize() + getFooterSize());
        }
    }

    public int removeHeader(View view) {
        int index = mHeaderViews.indexOfValue(view);
        if (index < 0) {
            return index;
        }
        mHeaderViews.removeAt(index);
        notifyItemRemoved(index);
        return index;
    }

    public int removeFooter(View view) {
        int index = mFooterViews.indexOfValue(view);
        if (index < 0) {
            return index;
        }
        mFooterViews.removeAt(index);
        notifyItemRemoved(getHeaderSize() + getContentItemsSize() + index);
        return index;
    }

    private boolean isHeaderPos(int position) {
        return position < getHeaderSize();
    }

    private boolean isFooterPos(int position) {
        return position >= getHeaderSize() + getContentItemsSize();
    }

    private int getContentItemsSize() {
        return mInnerAdapter.getItemCount();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderViews.get(viewType) != null) {
            // Header
            return new CommonViewHolder(mHeaderViews.get(viewType), viewType);
        }
        if (mFooterViews.get(viewType) != null) {
            // Footer
            return new CommonViewHolder(mFooterViews.get(viewType), viewType);
        }
        // content
        return mInnerAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isHeaderPos(position) || isFooterPos(position)) {
            return;
        }
        // 子adapter的position要剔除Header的大小
        mInnerAdapter.onBindViewHolder(holder, position - getHeaderSize());
    }

    @Override
    public int getItemCount() {
        return getContentItemsSize() + getHeaderSize() + getFooterSize();
    }

    @Override
    public int getItemViewType(int position) {
        // wrapper adapter只处理Header和Footer相关的view，其他的view 交给子Adapter来实现
        if (isHeaderPos(position)) {
            // 返回mHeaderViews的id，以便在onCreateViewHolder中根据id来过相对应的view
            return mHeaderViews.keyAt(position);
        } else if (isFooterPos(position)) {
            // 底部的计算开始位置是子adapter内容的数量与头部数量之和
            return mFooterViews.keyAt(position - getContentItemsSize() - getHeaderSize());
        }
        // 非Header和Footer，交给子Adapter处理
        return mInnerAdapter.getItemViewType(position - getHeaderSize());
    }

    /**
     * 解决GridLayoutManager添加的头部和底部不占一行的问题
     *
     * @param recyclerView
     */
    public void adjustSpanSize(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            final GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    boolean isHeaderOrFooter = isHeaderPos(position) || isFooterPos(position);
                    return isHeaderOrFooter ? layoutManager.getSpanCount() : 1;
                }
            });
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mInnerAdapter.registerAdapterDataObserver(mDataObserver);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mInnerAdapter.unregisterAdapterDataObserver(mDataObserver);
    }

    /**
     * 解决mInnerAdapter调用notifyXXX方法不生效的问题
     */
    class ChildrenAdapterDataObserver extends RecyclerView.AdapterDataObserver {
        WrapHeaderFooterAdapter mWrapAdapter;
        private ChildrenAdapterDataObserver(WrapHeaderFooterAdapter wrapAdapter) {
            super();
            mWrapAdapter = wrapAdapter;
        }
        @Override
        public void onChanged() {
            Log.e("tifone", "dataset changed");
            mWrapAdapter.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mWrapAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    }
}
