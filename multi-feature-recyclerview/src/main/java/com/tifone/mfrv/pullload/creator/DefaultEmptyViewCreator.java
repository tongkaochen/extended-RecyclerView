package com.tifone.mfrv.pullload.creator;

import android.view.View;

import com.tifone.mfrv.R;
import com.tifone.mfrv.pullload.base.BaseViewCreator;

/**
 * Create by Tifone on 2018/5/29.
 */
public class DefaultEmptyViewCreator extends BaseViewCreator {
    @Override
    public int getLayoutId() {
        return R.layout.default_empty_view_layout;
    }

    @Override
    public void bindView(View root) {

    }
}
