package com.sky.lamp.ui;

import org.greenrobot.eventbus.EventBus;

import com.sky.lamp.R;
import com.sky.lamp.adapter.ProductListAdapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Index2Fragment extends DelayBaseFragment {
    private ProductListAdapter productListAdapter;

    @Override
    protected void showDelayData() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frament_index1, null);
        return view;
    }

}
