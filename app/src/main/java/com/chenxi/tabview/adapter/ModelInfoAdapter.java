package com.chenxi.tabview.adapter;

import com.sky.lamp.R;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class ModelInfoAdapter extends TabBaseAdapter {

    private Fragment[] fragmentArray;
    private FragmentManager fragmentManager;
    private int hasMsgIndex = 0;

    public void setHasMsgIndex(int hasMsgIndex) {
        this.hasMsgIndex = hasMsgIndex;
    }

    public ModelInfoAdapter(FragmentManager fragmentManager, Fragment[] fragmentArray) {
        this.fragmentManager = fragmentManager;
        this.fragmentArray = fragmentArray;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public int hasMsgIndex() {
        return hasMsgIndex;
    }


    @Override
    public String[] getTextArray() {
        return new String[]{"设置", "demo演示"};
    }

    @Override
    public Fragment[] getFragmentArray() {
        return fragmentArray;
    }

    @Override
    public int[] getIconImageArray() {
        return new int[]{R.drawable.modeinfo_setting_unselect,R.drawable.modelinfo_demo_unselect};
    }

    @Override
    public int[] getSelectedIconImageArray() {
        return new int[]{R.drawable.modeinfo_setting_select,R.drawable.modelinfo_demo_select};
    }

    @Override
    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }
}
