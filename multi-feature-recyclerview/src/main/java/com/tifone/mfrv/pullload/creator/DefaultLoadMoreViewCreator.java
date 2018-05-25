package com.tifone.mfrv.pullload.creator;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tifone.mfrv.pullload.PullLoadState;
import com.tifone.mfrv.R;
import com.tifone.mfrv.pullload.base.LoadMoreViewCreator;

public class DefaultLoadMoreViewCreator extends LoadMoreViewCreator {
    private ProgressBar mProgress;
    private TextView mTextView;

    @Override
    public int getLayoutId() {
        return R.layout.default_load_more_layout;
    }

    @Override
    public void bindView(View root) {
        mProgress = root.findViewById(R.id.load_more_progressbar);
        mTextView = root.findViewById(R.id.load_more_title);
    }

    @Override
    public void onStateChanged(int oldState, int newState) {
        switch (newState) {
            case PullLoadState.LOAD_STATE_NORMAL:
                mTextView.setText("加载更多");
                mProgress.setVisibility(View.GONE);
                break;
            case PullLoadState.LOAD_STATE_LOADING:
                mTextView.setText("正在加载");
                mProgress.setVisibility(View.VISIBLE);
                break;
            case PullLoadState.LOAD_STATE_LOOSEN_LOAD:
                mTextView.setText("松开后加载");
                mProgress.setVisibility(View.GONE);
                break;
            case PullLoadState.LOAD_STATE_PULL_UP_LOAD:
                mTextView.setText("上拉加载更多");
                mProgress.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onLoadComplete() {
        mTextView.setText("加载完成");
        Animation animation = AnimationUtils.loadAnimation(mTextView.getContext(), R.anim.image_scale_anim);
        animation.setDuration(500);
        mTextView.startAnimation(animation);
        mProgress.setVisibility(View.GONE);
    }

    @Override
    public void onLoadFailed() {

    }
}
