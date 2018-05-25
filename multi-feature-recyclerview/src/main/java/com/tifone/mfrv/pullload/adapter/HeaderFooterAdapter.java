package com.tifone.mfrv.pullload.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.tifone.mfrv.pullload.base.BaseViewCreator;

/**
 * 包含头部和尾部的adapter
 */
public abstract class HeaderFooterAdapter<T> extends MultiItemsAdapter<T> {
    private static final int BASE_KEY = CommonAdapter.BASE_VIEW_TYPE;
    private static final int HEADER_CREATOR_KEY = BASE_KEY;
    private static final int FOOTER_CREATOR_KEY = BASE_KEY * 2;
    private SparseArray<BaseViewCreator> mHeaderCreators;
    private SparseArray<BaseViewCreator> mFooterCreators;
    public HeaderFooterAdapter(Context context) {
        super(context);
        mHeaderCreators = new SparseArray<>();
        mFooterCreators = new SparseArray<>();
    }
    public void addHeaderCreator(BaseViewCreator creator) {
        int index = mHeaderCreators.indexOfValue(creator);
        if (index < 0) {
            mHeaderCreators.put(HEADER_CREATOR_KEY + creator.hashCode(), creator);
            notifyItemInserted(getHeaderCount());
        }
    }
    public void addFooterCreator(BaseViewCreator creator) {
        int index = mFooterCreators.indexOfValue(creator);
        if (index < 0) {
            mFooterCreators.put(FOOTER_CREATOR_KEY + creator.hashCode(), creator);
            notifyItemInserted(getItemCount());
        }
    }
    public void removeHeaderCreator(BaseViewCreator creator) {
        int index = mHeaderCreators.indexOfValue(creator);
        if (index >= 0) {
            mHeaderCreators.removeAt(index);
            notifyItemRemoved(index);
        }
    }
    public void removeFooterCreator(BaseViewCreator creator) {
        int index = mFooterCreators.indexOfValue(creator);
        if (index >= 0) {
            mFooterCreators.removeAt(index);
            notifyItemRemoved(getHeaderCount() + getContentItemCount() + index);
        }
    }
    private boolean isHeaderPos(int position) {
        return position < getHeaderCount();
    }

    private boolean isFooterPos(int position) {
        return position >= getContentItemCount() + getHeaderCount();
    }

    @NonNull
    @Override
    public CommonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mHeaderCreators.get(viewType) != null) {
            // 创建Header
            return new CommonViewHolder(mHeaderCreators.get(viewType).getView(parent.getContext(), parent), viewType);
        }
        if (mFooterCreators.get(viewType) != null) {
            // 创建Footer
            return new CommonViewHolder(mFooterCreators.get(viewType).getView(parent.getContext(), parent), viewType);
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull CommonViewHolder holder, int position) {
        if (isFooterPos(position) || isHeaderPos(position)) {
            return;
        }
        super.onBindViewHolder(holder, position - getHeaderCount());
    }

    private int getContentItemCount() {
        return super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderPos(position)) {
            return mHeaderCreators.keyAt(position);
        }
        if (isFooterPos(position)) {
            return mFooterCreators.keyAt(position - getContentItemCount() - getHeaderCount());
        }
        return super.getItemViewType(position);
    }

    private int getHeaderCount() {
        return mHeaderCreators.size();
    }
    private int getFooterCount() {
        return mFooterCreators.size();
    }

    /**
     * 总大小：内容长度 + 头部长度 + 尾部长度
     * @return item count
     */
    @Override
    public int getItemCount() {
        return getContentItemCount() + getHeaderCount() + getFooterCount();
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
}
