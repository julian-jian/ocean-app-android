package com.sky.lamp.ui.fragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.daimajia.swipe.SwipeLayout;
import com.event.NextStepEvent;
import com.google.gson.reflect.TypeToken;
import com.guo.duoduo.wifidetective.core.devicescan.DeviceScanManager;
import com.guo.duoduo.wifidetective.core.devicescan.DeviceScanResult;
import com.guo.duoduo.wifidetective.core.devicescan.IP_MAC;
import com.orhanobut.logger.Logger;
import com.sky.lamp.BaseActivity;
import com.sky.lamp.Constants;
import com.sky.lamp.bean.ModelBean;
import com.sky.lamp.bean.ModelSelectBean;
import com.sky.lamp.MyApplication;
import com.sky.lamp.R;
import com.sky.lamp.bean.Device;
import com.sky.lamp.http.AppService;
import com.sky.lamp.http.MyApi;
import com.sky.lamp.response.BaseResponse;
import com.sky.lamp.response.GetBindDeviceResponse;
import com.sky.lamp.ui.DelayBaseFragment;
import com.sky.lamp.ui.act.ConfigAct;
import com.sky.lamp.ui.act.LoginAct;
import com.sky.lamp.ui.act.SelectConfigAct;
import com.sky.lamp.utils.GsonImpl;
import com.sky.lamp.utils.HttpUtil;
import com.sky.lamp.utils.MySubscriber;
import com.sky.lamp.utils.RxSPUtilTool;
import com.vondear.rxtools.view.RxToast;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            queryBindDevice();
        }
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
                ((BaseActivity) getActivity()).showLoadingDialog("正在搜索局域网设备");
                startFindDevices();
            }
        });
        deviceScanManager = new DeviceScanManager();
        startFindDevices();
        queryBindDevice();
        return view;
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

    @Override
    public void onResume() {
        super.onResume();
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
        //        swipeLayout.setLeftSwipeEnabled(false);
        //        swipeLayout.setRightSwipeEnabled(false);
        //        swipeLayout.setSwipeEnabled(false);
        swipeLayout.findViewById(R.id.tv_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindDeviceRequest(ip_mac.mMac);
            }
        });
        TextView deviceName = inflate.findViewById(R.id.tv_name);
        TextView mac = inflate.findViewById(R.id.tv_mac);
        deviceName.setText(ip_mac.mIp);
        mac.setText(ip_mac.mMac);
        final int pos = mDeviceList.size() - 1;
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

    public void unbindDevice(String deviceID) {
        String userId = RxSPUtilTool.getString(getActivity(), Constants.USER_ID);
        HashMap<String, Object> map = new HashMap<>();
        map.put("userID", userId);
        map.put("deviceID", deviceID);
        String strEntity = HttpUtil.getRequestString(map);
        RequestBody body = RequestBody
                .create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        AppService.createApi(MyApi.class).unBind(body).subscribeOn(Schedulers.io())
                .observeOn(
                        AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<BaseResponse>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        getBaseActivity().showLoadingDialog();
                    }

                    @Override
                    public void onError(Throwable error) {
                        super.onError(error);
                        getBaseActivity().dismissLoadingDialog();
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onNext(final BaseResponse response) {
                        if (!response.isSuccess()) {
                            RxToast.showToast(response.result);
                            return;
                        }
                        queryBindDevice();

                    }
                });
    }

    public void queryBindDevice() {
        String userId = RxSPUtilTool.getString(getActivity(), Constants.USER_ID);
        if (TextUtils.isEmpty(userId)) {
            Logger.d("not login");
            return;
        }
        final HashMap<String, Object> map = new HashMap<>();
        map.put("userID", userId);
        String strEntity = HttpUtil.getRequestString(map);
        RequestBody body = RequestBody
                .create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        AppService.createApi(MyApi.class).getBindDevices(body).subscribeOn(Schedulers.io())
                .observeOn(
                        AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<GetBindDeviceResponse>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onError(Throwable error) {
                        //                        super.onError(error);
                    }

                    @Override
                    public void onCompleted() {
                        getBaseActivity().dismissLoadingDialog();
                    }

                    @Override
                    public void onNext(final GetBindDeviceResponse response) {
                        if (!response.isSuccess()) {
                            RxToast.showToast(response.result);
                            return;
                        }
                        Type type = new TypeToken<List<Device>>() {
                        }.getType();
                        List<Device> list = new GsonImpl().toList(response.result,
                                Device.class, type);
                        llBindDevicesList.removeAllViews();
                        for (final Device device : list) {
                            final View inflate = LayoutInflater
                                    .from(getActivity()).inflate(R.layout.item_find_device, null);
                            SwipeLayout swipeLayout = inflate.findViewById(R.id.swipeLayout);
                            swipeLayout.findViewById(R.id.checkbox).setVisibility(View.INVISIBLE);
                            swipeLayout.findViewById(R.id.tv_1)
                                    .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            RxToast.showToast("暂不支持");
                                        }
                                    });
                            swipeLayout.findViewById(R.id.tv_2)
                                    .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            unbindDevice(device.getDeviceSN());
                                        }
                                    });
                            ((TextView)swipeLayout.findViewById(R.id.tv_2)).setText("解除绑定");
                            // 禁用左划
                            TextView deviceName = inflate.findViewById(R.id.tv_name);
                            IP_MAC tmp = new IP_MAC("",device.getDeviceSN());
                            if (mDeviceList.contains(tmp)) {
                                deviceName.setText(device.getDeviceSN() + "(在线)");
                            } else {
                                deviceName.setText(device.getDeviceSN());
                            }
                            TextView mac = inflate.findViewById(R.id.tv_mac);
                            mac.setVisibility(View.GONE);

                            deviceName.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (TextUtils.isEmpty(ModelSelectBean.t1)) {
                                        RxToast.showToast("请选择模式");
                                        return;
                                    }
                                    String ip  = "";
                                    for (IP_MAC ipMac : mDeviceList) {
                                        if (ipMac.mMac.equals(device.getDeviceSN())) {
                                            ip = ipMac.mIp;
                                        }
                                    }
                                    if (TextUtils.isEmpty(ip)) {
                                        RxToast.showToast("设备未上线");
                                        return;
                                    }
                                    ModelSelectBean.deviceId = device.getDeviceSN();
                                    ModelSelectBean.ip = ip;
                                    SelectConfigAct.startUI(getActivity());
                                }
                            });
                            llBindDevicesList.addView(inflate);
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void bindDeviceRequest(String mac) {
        if (!MyApplication.getInstance().isLogin()) {
            RxToast.showToast("请先登录");
            return;
        }
        String userId = RxSPUtilTool.getString(getActivity(), Constants.USER_ID);
        HashMap<String, Object> map = new HashMap<>();
        map.put("deviceID", mac);
        map.put("userID", userId);
        String strEntity = HttpUtil.getRequestString(map);
        RequestBody body = RequestBody
                .create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        AppService.createApi(MyApi.class).bind(body).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(new MySubscriber<BaseResponse>() {
            @Override
            public void onStart() {
                super.onStart();
                getBaseActivity().showLoadingDialog();
            }

            @Override
            public void onError(Throwable error) {
                super.onError(error);
                getBaseActivity().dismissLoadingDialog();
            }

            @Override
            public void onCompleted() {
                getBaseActivity().dismissLoadingDialog();
            }

            @Override
            public void onNext(final BaseResponse response) {
                if (response.isSuccess()) {
                    RxToast.showToast("绑定成功");
                    queryBindDevice();
                } else {
                    RxToast.error(response.result);
                }
            }
        });
    }

}
