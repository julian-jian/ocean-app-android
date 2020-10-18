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
import com.guo.duoduo.wifidetective.core.devicescan.IP_MAC;
import com.orhanobut.logger.Logger;
import com.sky.lamp.BaseActivity;
import com.sky.lamp.Constants;
import com.sky.lamp.bean.ModelSelectBean;
import com.sky.lamp.MyApplication;
import com.sky.lamp.R;
import com.sky.lamp.bean.Device;
import com.sky.lamp.bean.RenameMac;
import com.sky.lamp.dao.DaoManager;
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
import com.stealthcopter.networktools.SubnetDevices;
import com.vondear.rxtools.view.RxToast;
import com.vondear.rxtools.view.dialog.RxDialogEditSureCancel;

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
    private List<IP_MAC> mDeviceList = new ArrayList<IP_MAC>();
    private RxDialogEditSureCancel rxDialogLoading;
    private List<RenameMac> renameMacs;

    ArrayList<com.stealthcopter.networktools.subnet.Device> mDevicesFound = new ArrayList<>();

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
        startFindDevices();
        queryBindDevice();
        renameMacs =
                DaoManager.getInstance().getDaoSession().getRenameMacDao()
                        .loadAll();
        if (renameMacs == null) {
            renameMacs = new ArrayList<>();
        }
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
        SubnetDevices.fromLocalAddress().findDevices(new SubnetDevices.OnSubnetDeviceFound() {
            @Override
            public void onDeviceFound(com.stealthcopter.networktools.subnet.Device device) {
                Logger.d(device.toString());
            }

            @Override
            public void onFinished(
                    final ArrayList<com.stealthcopter.networktools.subnet.Device> devicesFound) {
                mDevicesFound = devicesFound;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((BaseActivity) getActivity()).dismissLoadingDialog();
                        for (com.stealthcopter.networktools.subnet.Device device : devicesFound) {
                            IP_MAC ipMac = new IP_MAC(device.ip, device.mac);
                            if (!TextUtils.isEmpty(device.mac) && !mDeviceList.contains(ipMac)) {
                                mDeviceList.add(ipMac);
                                addFindDeviceView(ipMac);
                            }
                        }
                        queryBindDevice();
                    }
                });
            }
        });
    }

    private void addFindDeviceView(final IP_MAC ip_mac) {
        View inflate = LayoutInflater
                .from(getActivity()).inflate(R.layout.item_find_device2, null);
        SwipeLayout swipeLayout = inflate.findViewById(R.id.swipeLayout);

        swipeLayout.findViewById(R.id.tv_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindDeviceRequest(ip_mac.mMac);
            }
        });
        TextView deviceName = inflate.findViewById(R.id.tv_item1);
        TextView mac = inflate.findViewById(R.id.tv_item2);
        RenameMac renameMac = new RenameMac();
        renameMac.mac = ip_mac.mMac;
        if (renameMacs.contains(renameMac)) {
            RenameMac renameMac1 = renameMacs.get(renameMacs.indexOf(renameMac));
            deviceName.setText("(" + renameMac1.name + ")" + ip_mac.mIp);
        } else {
            deviceName.setText(ip_mac.mIp);
        }
        mac.setText(ip_mac.mMac);
        swipeLayout.findViewById(R.id.tv_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRenameDialog(ip_mac.mMac);
            }
        });
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
                            ((TextView) swipeLayout.findViewById(R.id.tv_2)).setText("解除绑定");
                            // 禁用左划
                            TextView deviceName = inflate.findViewById(R.id.tv_item1);
                            IP_MAC tmp = new IP_MAC("", device.getDeviceSN());
                            if (mDeviceList.contains(tmp)) {
                                deviceName.setText(device.getDeviceSN() + "(在线)");
                            } else {
                                deviceName.setText(device.getDeviceSN());
                            }
                            TextView mac = inflate.findViewById(R.id.tv_item2);
                            mac.setVisibility(View.GONE);

                            deviceName.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (TextUtils.isEmpty(ModelSelectBean.t1)) {
                                        RxToast.showToast("请选择模式");
                                        return;
                                    }
                                    String ip = "";
                                    for (IP_MAC ipMac : mDeviceList) {
                                        if (ipMac.mMac.toLowerCase()
                                                .equals(device.getDeviceSN().toLowerCase())) {
                                            ip = ipMac.mIp;
                                        }
                                    }
//                                    if (TextUtils.isEmpty(ip)) {
//                                        RxToast.showToast("设备未上线");
//                                        return;
//                                    }
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

    public void showRenameDialog(final String mac) {
        rxDialogLoading = new RxDialogEditSureCancel(getActivity());
        rxDialogLoading.setTitle("重命名");
        rxDialogLoading.getCancelView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rxDialogLoading.dismiss();
            }
        });
        rxDialogLoading.getSureView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = rxDialogLoading.getEditText().getText().toString();
                if (!TextUtils.isEmpty(name)) {
                    List<RenameMac> renameMacs =
                            DaoManager.getInstance().getDaoSession().getRenameMacDao()
                                    .loadAll();
                    boolean findDevice = false;
                    for (RenameMac renameMac : renameMacs) {
                        if (renameMac.mac.equals(mac)) {
                            renameMac.name = name;
                            findDevice = true;
                            DaoManager.getInstance().getDaoSession().update(renameMac);
                            break;
                        }
                    }
                    if (!findDevice) {
                        RenameMac renameMac = new RenameMac();
                        renameMac.mac = mac;
                        renameMac.name = name;
                        DaoManager.getInstance().getDaoSession().insert(renameMac);
                    }
                    llFindDevicesList.removeAllViews();
                    for (com.stealthcopter.networktools.subnet.Device device : mDevicesFound) {
                        IP_MAC ipMac = new IP_MAC(device.ip, device.mac);
                        if (!TextUtils.isEmpty(device.mac)) {
                            addFindDeviceView(ipMac);
                        }
                    }
                    rxDialogLoading.dismiss();
                }
            }
        });
        rxDialogLoading.show();
    }

}
