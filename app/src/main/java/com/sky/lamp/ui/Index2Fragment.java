package com.sky.lamp.ui;

import java.util.ArrayList;
import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.daimajia.swipe.SwipeLayout;
import com.event.NextStepEvent;
import com.guo.duoduo.wifidetective.core.devicescan.DeviceScanManager;
import com.guo.duoduo.wifidetective.core.devicescan.DeviceScanResult;
import com.guo.duoduo.wifidetective.core.devicescan.IP_MAC;
import com.sky.lamp.R;
import com.vondear.rxtools.view.RxToast;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class Index2Fragment extends DelayBaseFragment {
    @BindView(R.id.ll_bind_devices_list)
    LinearLayout llBindDevicesList;
    @BindView(R.id.ll_find_devices_list)
    LinearLayout llFindDevicesList;
    Unbinder unbinder;
    private DeviceScanManager deviceScanManager;
    private List<IP_MAC> mDeviceList = new ArrayList<IP_MAC>();
    @Override
    protected void showDelayData() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frament_index2, null);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);

        View inflate = initViews();
        llBindDevicesList.addView(inflate);
        deviceScanManager = new DeviceScanManager();
        startFindDevices();
        return view;
    }

    @NonNull
    private View initViews() {
        View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.item_device, null);
        SwipeLayout swipeLayout =  inflate.findViewById(R.id.swipeLayout);
        swipeLayout.findViewById(R.id.tv_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Index2Fragment.onClick");
            }
        });

        swipeLayout.findViewById(R.id.tv_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Index2Fragment.onClick");
            }
        });
        return inflate;
    }

    @Subscribe
    public void nextStepClick(NextStepEvent event) {
        IP_MAC select = null;
        for (IP_MAC ip_mac : mDeviceList) {
            if (ip_mac.mSelect) {
                select = ip_mac;
            }
        }
        if (select == null) {
            RxToast.showToast("请先选中设备");
        }
    }

    private void startFindDevices() {
        mDeviceList.clear();
        llFindDevicesList.removeAllViews();
        deviceScanManager.startScan(getActivity().getApplicationContext(), new DeviceScanResult() {
            @Override
            public void deviceScanResult(final IP_MAC ip_mac) {
                if (!mDeviceList.contains(ip_mac)) {
                    mDeviceList.add(ip_mac);
                    addDeviceView(ip_mac);
                }
            }
        });
    }

    private void addDeviceView(final IP_MAC ip_mac) {
        View inflate = LayoutInflater
                .from(getActivity()).inflate(R.layout.item_find_device, null);
        SwipeLayout swipeLayout = inflate.findViewById(R.id.swipeLayout);
        // 禁用左划
        swipeLayout.setLeftSwipeEnabled(false);
        swipeLayout.setRightSwipeEnabled(false);
        swipeLayout.setSwipeEnabled(false);
        TextView deviceName = inflate.findViewById(R.id.tv_name);
        TextView mac = inflate.findViewById(R.id.tv_mac);
        deviceName.setText(ip_mac.mDeviceName);
        mac.setText(ip_mac.mMac);
        AppCompatCheckBox checkBox = inflate.findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (!isChecked) {
                            return;
                        }
                        System.out.println("Index2Fragment.onCheckedChanged");
                        for (int i = 0; i < llFindDevicesList.getChildCount(); i++) {
                            View childAt = llFindDevicesList.getChildAt(i);
                            if (i != mDeviceList.indexOf(ip_mac)) {
                                AppCompatCheckBox checkBox =
                                        childAt.findViewById(R.id.checkbox);
                                checkBox.setChecked(false);
                            }
                        }
                    }
                });
        llFindDevicesList.addView(inflate);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
