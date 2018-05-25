package com.tifone.mfrv.pullload.creator;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.tifone.mfrv.pullload.PullLoadState;
import com.tifone.mfrv.R;
import com.tifone.mfrv.pullload.base.PullRefreshViewCreator;

public class DefaultRefreshViewCreator extends PullRefreshViewCreator {
    private TextView mTextView;
    private ImageView mImageView;
    private int mLastStatus = -1;

    @Override
    public int getLayoutId() {
        return R.layout.default_refresh_layout;
    }

    @Override
    public void bindView(View root) {
        mTextView = root.findViewById(R.id.refresh_title);
        mImageView = root.findViewById(R.id.refresh_image);
    }
    

    @Override
    public void onStateChanged(int oldState, int newState) {
        mImageView.clearAnimation();
        switch (newState) {
            case PullLoadState.REFRESH_STATE_LOOSEN_REFRESH:
                mTextView.setText("松开后刷新");
                mImageView.setImageResource(R.drawable.ic_upward);
                break;
            case PullLoadState.REFRESH_STATE_PULL_DOWN_REFRESH:
                mTextView.setText("下拉刷新");
                mImageView.setImageResource(R.drawable.ic_downward);

                break;
            case PullLoadState.REFRESH_STATE_REFRESHING:
                mTextView.setText("正在刷新");
                mImageView.setImageResource(R.drawable.ic_loading);
                if (newState != mLastStatus) {
                    doAnimator(0, 360, -1);
                }
                break;
            case PullLoadState.REFRESH_STATE_NORMAL:
                mTextView.setText("准备就绪");
                mImageView.clearAnimation();
                break;
        }
        mLastStatus = newState;
    }
    private void doAnimator(float start, float end, int repeatCount) {
        /*ObjectAnimator animator = ObjectAnimator.ofFloat(mImageView, "rotation", start, end);
        animator.setDuration(800);
        animator.setRepeatCount(repeatCount);
        animator.start();*/
        RotateAnimation animation = new RotateAnimation(start, end,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setRepeatCount(repeatCount);
        animation.setDuration(800);
        mImageView.startAnimation(animation);
    }

    @Override
    public void onRefreshing() {

    }

    @Override
    public void onRefreshComplete() {
        mTextView.setText("刷新完成");
        mImageView.clearAnimation();
        mImageView.setImageResource(R.drawable.ic_success);
        Animation animation = AnimationUtils.loadAnimation(mImageView.getContext(), R.anim.image_scale_anim);
        animation.setDuration(500);
        mImageView.startAnimation(animation);
        mTextView.startAnimation(animation);
    }

    @Override
    public void onRefreshFailed() {

    }
}
