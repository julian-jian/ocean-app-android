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
import com.sky.lamp.BaseActivity;
import com.sky.lamp.MyApplication;
import com.sky.lamp.R;
import com.sky.lamp.ui.act.ConfigAct;
import com.sky.lamp.ui.act.LoginAct;
import com.vondear.rxtools.view.RxToast;

import android.content.Intent;
import android.os.Bundle;
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
    @BindView(R.id.ll_search)
    LinearLayout llSearch;
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
        llSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseActivity) getActivity()).showLoadingDialog("正在搜索");
                startFindDevices();
            }
        });
        initBindViews();
        deviceScanManager = new DeviceScanManager();
        startFindDevices();
        return view;
    }

    private void initBindViews() {
        View inflate = LayoutInflater
                .from(getActivity()).inflate(R.layout.item_find_device, null);
        SwipeLayout swipeLayout = inflate.findViewById(R.id.swipeLayout);
        swipeLayout.findViewById(R.id.checkbox).setVisibility(View.INVISIBLE);
        swipeLayout.findViewById(R.id.tv_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxToast.showToast("重命名");
            }
        });
        swipeLayout.findViewById(R.id.tv_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxToast.showToast("解绑设备");
            }
        });
        // 禁用左划
        TextView deviceName = inflate.findViewById(R.id.tv_name);
        TextView mac = inflate.findViewById(R.id.tv_mac);
        deviceName.setText("test");
        mac.setText("testMac");
        llBindDevicesList.addView(inflate);
    }

    @Subscribe
    public void nextStepClick(NextStepEvent event) {
        IP_MAC selectDevice = null;
        for (IP_MAC ip_mac : mDeviceList) {
            if (ip_mac.mSelect) {
                selectDevice = ip_mac;
            }
        }
        if (selectDevice == null) {
            RxToast.showToast("请先选中设备");
            return;
        }
        if (!MyApplication.getInstance().isLogin()) {
            RxToast.showToast("请先登录");
            startActivity(new Intent(getActivity(), LoginAct.class));
            return;
        }
        Intent intent = new Intent(getActivity(), ConfigAct.class);
        intent.putExtra("device", selectDevice);
        startActivity(intent);
    }

    private void startFindDevices() {
        mDeviceList.clear();
        llFindDevicesList.removeAllViews();
        deviceScanManager.startScan(getActivity().getApplicationContext(), new DeviceScanResult() {
            @Override
            public void deviceScanResult(final IP_MAC ip_mac) {
                ((BaseActivity) getActivity()).dismissLoadingDialog();
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
        final int pos = mDeviceList.size();
        AppCompatCheckBox checkBox = inflate.findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (!isChecked) {
                            mDeviceList.get(pos).mSelect = false;
                            return;
                        }
                        for (int i = 0; i < llFindDevicesList.getChildCount(); i++) {
                            View childAt = llFindDevicesList.getChildAt(i);
                            AppCompatCheckBox checkBox =
                                    childAt.findViewById(R.id.checkbox);
                            if (i != mDeviceList.indexOf(ip_mac)) {
                                checkBox.setChecked(false);
                            } else {
                                mDeviceList.get(pos).mSelect = true;
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
