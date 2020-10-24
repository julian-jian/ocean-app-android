package com.sky.lamp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.sky.lamp.BaseActivity;
import com.sky.lamp.BaseFragment;
import com.sky.lamp.R;

/**
 * Created by sky on 2015/4/9.
 */
public class TitleBar extends LinearLayout {


    public View mRootView;

    private ImageView mLeftImageView;
    private ImageView mRightImageView;
    private TextView mTitleTextView;
    private TextView mRightTextView;
    private TextView mLeftTextView;

    public View getRootView() {
        return mRootView;
    }

    public void setRootView(View mRootView) {
        this.mRootView = mRootView;
    }

    public ImageView getLeftImageView() {
        return mLeftImageView;
    }

    public void setLeftImageView(ImageView mLeftImageView) {
        this.mLeftImageView = mLeftImageView;
    }

    public ImageView getRightImageView() {
        mRightImageView.setVisibility(View.VISIBLE);
        return mRightImageView;
    }

    public void setRightImageView(ImageView mRightImageView) {
        this.mRightImageView = mRightImageView;
    }

    public TextView getTitleTextView() {
        return mTitleTextView;
    }

    public void setTitleTextView(TextView mTitleTextView) {
        this.mTitleTextView = mTitleTextView;
    }

    public TextView getRightTextView() {
        return mRightTextView;
    }

    public void setRightTextView(TextView mRightTextView) {
        this.mRightTextView = mRightTextView;
    }

    public TextView getLeftTextView() {
        return mLeftTextView;
    }

    public void setLeftTextView(TextView mLeftTextView) {
        this.mLeftTextView = mLeftTextView;
    }


    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRootView = LayoutInflater.from(context).inflate(R.layout.title_bar, this);
        initViews();
    }

    private void initViews() {
        mLeftImageView = mRootView.findViewById(R.id.iv_left);
        mRightImageView = mRootView.findViewById(R.id.iv_right);
        mTitleTextView = mRootView.findViewById(R.id.tv_title);
        mRightTextView = mRootView.findViewById(R.id.tv_right);
        mLeftTextView = mRootView.findViewById(R.id.tv_Left);
    }

    public TitleBar setRightText(int resId) {
        mRightTextView.setText(resId);
        return this;
    }

    public TitleBar setRightText(CharSequence charSequence) {
        if (mRightTextView.getVisibility() == View.GONE) {
            mRightTextView.setVisibility(View.VISIBLE);
        }
        mRightTextView.setText(charSequence);
        return this;
    }

    public TitleBar setRightText(CharSequence charSequence, OnClickListener clickListener) {
        if (mRightTextView.getVisibility() == View.GONE) {
            mRightTextView.setVisibility(View.VISIBLE);
        }
        mRightTextView.setText(charSequence);
        mRightTextView.setOnClickListener(clickListener);
        return this;
    }

    public TitleBar setLeftText(CharSequence charSequence, OnClickListener clickListener) {
        mLeftTextView.setText(charSequence);
        mLeftTextView.setOnClickListener(clickListener);
        return this;
    }


    /**
     * finish
     * @param baseActivity
     * @return
     */
    public TitleBar initLeftImageView(final BaseActivity baseActivity) {
        mLeftImageView.setVisibility(View.VISIBLE);
        mLeftImageView.setImageResource(R.drawable.back);
        mLeftImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                baseActivity.finish();
            }
        });
        return this;
    }

    public TitleBar initLeftImageView(final BaseFragment taFragment) {
        mLeftImageView.setVisibility(View.VISIBLE);
        mLeftImageView.setImageResource(R.drawable.back);
        mLeftImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                taFragment.getActivity().finish();
            }
        });
        return this;
    }

    public TitleBar setRightImageView(int resId, OnClickListener clickListener) {
        if (mRightImageView.getVisibility() == View.GONE) {
            mRightImageView.setVisibility(View.VISIBLE);
        }
        mRightImageView.setImageResource(resId);
        if (clickListener != null) {
            mRightImageView.setOnClickListener(clickListener);
        }
        return this;
    }


    public TitleBar setLeftImageView(int resId, OnClickListener clickListener) {
        if (mLeftImageView != null) {
            mLeftImageView.setOnClickListener(clickListener);
        }
        mLeftImageView.setImageResource(resId);
        return this;
    }

    public TitleBar setTitle(int resId) {
        mTitleTextView.setText(resId);
        return this;
    }


    public TitleBar setTitle(String title) {
        mTitleTextView.setText(title);
        return this;
    }


}
