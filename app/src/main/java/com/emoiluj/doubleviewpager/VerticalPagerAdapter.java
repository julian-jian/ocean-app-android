package com.emoiluj.doubleviewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class VerticalPagerAdapter extends PagerAdapter {

    private Context mContext;
    private int mChilds;

    public static final int M7 = 0;
    public static final int M7_PLUS = 1;
    public static final int JG2 = 2;
    public static final int JG3 = 3;
    public static final int S11S = 4;
    public static final int S11 = 5;
    public static final int F6 = 6;
    public static final int F205 = 7;
    private int[] nowImages;
    int mType;

    public VerticalPagerAdapter(Context c, int type) {
        mContext = c;
        mType = type;
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return nowImages.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LinearLayout linear = new LinearLayout(mContext);
        linear.setOrientation(LinearLayout.VERTICAL);
        linear.setGravity(Gravity.CENTER);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        linear.setLayoutParams(lp);

        TextView tvParent = new TextView(mContext);
        tvParent.setLayoutParams(lp);
        tvParent.setBackgroundResource(nowImages[position]);

        linear.addView(tvParent);

        container.addView(linear);
        return linear;
    }
}