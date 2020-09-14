package com.chenxi.tabview.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import com.sky.lamp.R;


public class MainViewAdapter extends TabBaseAdapter {

    private Fragment[] fragmentArray;
    private FragmentManager fragmentManager;
    private int hasMsgIndex = 0;

    public void setHasMsgIndex(int hasMsgIndex) {
        this.hasMsgIndex = hasMsgIndex;
    }

    public MainViewAdapter(FragmentManager fragmentManager, Fragment[] fragmentArray) {
        this.fragmentManager = fragmentManager;
        this.fragmentArray = fragmentArray;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public int hasMsgIndex() {
        return hasMsgIndex;
    }


    @Override
    public String[] getTextArray() {
        return new String[]{"首页", "设备", "我的"};
    }

    @Override
    public Fragment[] getFragmentArray() {
        return fragmentArray;
    }

    @Override
    public int[] getIconImageArray() {
        return new int[]{R.drawable.home_normal,R.drawable.device_unselect,
                R.drawable.mine_normal};
    }

    @Override
    public int[] getSelectedIconImageArray() {
        return new int[]{R.drawable.home_selector, R.drawable.device_selector,
                R.drawable.mine_seletor};
    }

    @Override
    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }
}
