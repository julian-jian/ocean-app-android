package com.emoiluj.doubleviewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import com.emoiluj.doubleviewpager.VerticalViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class DoubleViewPagerAdapter extends PagerAdapter{

    private Context mContext;
    private ArrayList<PagerAdapter> mAdapters;
    public HashMap map = new HashMap();

    public DoubleViewPagerAdapter(Context context, ArrayList<PagerAdapter> verticalAdapters){
        mContext = context;
        mAdapters = verticalAdapters;
        // mark is first or last page
        map.put(0, true);
        map.put(1, true);
        map.put(2, true);
        map.put(3, true);
        map.put(4, true);
        map.put(5, true);
        map.put(6, true);
        map.put(7, true);
    }


    @Override
    public int getCount() {
        return mAdapters.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        container.removeView((View) view);
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final VerticalViewPager childVP = new VerticalViewPager(mContext);
        childVP.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        childVP.setAdapter(mAdapters.get(position));
        childVP.setOnPageChangeListener(new VerticalViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                System.out.println("DoubleViewPagerAdapter.onPageScrolled");
            }

            @Override
            public void onPageSelected(int sub_position) {//up and down
                Log.e("sky", sub_position+"");
                /*if (mContext instanceof DetailsActivity) {
                    if (sub_position == 0 || sub_position == (mAdapters.get(position).getCount() - 1)) {//show
                        ((DetailsActivity) mContext).showBack();
                        map.put(position, true);
                        DoubleViewPager.canScrollHorizontal = true;
                    }else{
                        ((DetailsActivity) mContext).dismisBack();
                        map.put(position, false);
                        DoubleViewPager.canScrollHorizontal = false;
                    }
                }*/
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                System.out.println("DoubleViewPagerAdapter.onPageScrollStateChanged");
            }

            @Override
            public void onTouch() {
//                ((DetailsActivity) mContext).showBack();
            }
        });
        container.addView(childVP);
        return childVP;
    }

}