package com.sky.lamp.ui.fragment;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.daimajia.swipe.SwipeLayout;
import com.event.NextStepEvent;
import com.guo.duoduo.wifidetective.core.devicescan.IP_MAC;
import com.orhanobut.logger.Logger;
import com.sky.lamp.BaseActivity;
import com.sky.lamp.Constants;
import com.sky.lamp.bean.BindDeviceBean;
import com.sky.lamp.MyApplication;
import com.sky.lamp.R;
import com.sky.lamp.bean.RenameMac;
import com.sky.lamp.dao.DaoManager;
import com.sky.lamp.event.RefreshBindDeviceDeviceEvent;
import com.sky.lamp.http.AppService;
import com.sky.lamp.http.MyApi;
import com.sky.lamp.response.BaseResponse;
import com.sky.lamp.ui.DelayBaseFragment;
import com.sky.lamp.ui.act.ConfigAct;
import com.sky.lamp.ui.act.LoginAct;
import com.sky.lamp.utils.HttpUtil;
import com.sky.lamp.utils.RxSPUtilTool;
import com.stealthcopter.networktools.IPTools;
import com.stealthcopter.networktools.SubnetDevices;
import com.vondear.rxtools.view.RxToast;
import com.vondear.rxtools.view.dialog.RxDialogEditSureCancel;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.util.Log;
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
import rx.Subscriber;
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
            requestBindDevice();
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
                ((BaseActivity) getActivity()).showLoadingDialog("???????????????????????????");
                startFindDevices();
            }
        });
        startFindDevices();
        showCache();
        requestBindDevice();
        renameMacs =
                DaoManager.getInstance().getDaoSession().getRenameMacDao()
                        .loadAll();
        if (renameMacs == null) {
            renameMacs = new ArrayList<>();
        }
        return view;
    }

    private void showCache() {
        List<BindDeviceBean> bindDeviceBeans = queryBindFromDatabase();
        refreshBindViews(bindDeviceBeans);
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
            RxToast.showToast("??????????????????");
            return;
        }
        if (!MyApplication.getInstance().isLogin()) {
            RxToast.showToast("????????????");
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
        InetAddress ipv4 = IPTools.getLocalIPv4Address();
        if (ipv4 == null) {
            RxToast.showToast("???????????????");
            return;
        }
        mDeviceList.clear();
        llFindDevicesList.removeAllViews();
        try {
            SubnetDevices.fromLocalAddress().findDevices(new SubnetDevices.OnSubnetDeviceFound() {
                @Override
                public void onDeviceFound(com.stealthcopter.networktools.subnet.Device device) {
                    Logger.d(device.toString());
                }

                @Override
                public void onFinished(
                        final ArrayList<com.stealthcopter.networktools.subnet.Device> devicesFound) {
                    mDevicesFound = devicesFound;
                    Logger.d("onFinished "+Thread.currentThread());
                    if (getActivity() == null) {
                        return;
                    }
                    List list = new ArrayList();
                    // ????????????????????????
                    for (com.stealthcopter.networktools.subnet.Device device : mDevicesFound) {
                        boolean device1 = isDevice(device.ip);
                        if (!device1) {
                            list.add(device);
                        }
                    }
                    mDevicesFound.removeAll(list);
                    if (getActivity() == null) {
                        return;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isAdded()) {
                                return;
                            }
                            ((BaseActivity) getActivity()).dismissLoadingDialog();
                            for (com.stealthcopter.networktools.subnet.Device device :
                                    devicesFound) {
                                IP_MAC ipMac = new IP_MAC(device.ip, device.mac);
                                if (!TextUtils.isEmpty(device.mac) && !mDeviceList
                                        .contains(ipMac)) {
                                    mDeviceList.add(ipMac);
                                    addFindDeviceView(ipMac);
                                }
                            }
                            requestBindDevice();
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

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

        RenameMac renameMac2 = new RenameMac();
        renameMac2.mac = ip_mac.mMac.replace("8c","8e");
        if (renameMacs.contains(renameMac)) { // ?????????
            RenameMac renameMac1 = renameMacs.get(renameMacs.indexOf(renameMac));
            deviceName.setText("(" + renameMac1.name + ")" + ip_mac.mIp);
        } else if (renameMacs.contains(renameMac2)) {
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
        if (TextUtils.isEmpty(userId)) {
            RxToast.showToast("????????????");
            return;
        }
        List<BindDeviceBean> bindDeviceBeans =
                DaoManager.getInstance().getDaoSession().getBindDeviceBeanDao().loadAll();
        BindDeviceBean del = null;
        for (BindDeviceBean bindDeviceBean : bindDeviceBeans) {
            if (bindDeviceBean.deviceId.equals(deviceID) && bindDeviceBean.userId.equals(userId)) {
                DaoManager.getInstance().getDaoSession().getBindDeviceBeanDao().delete(bindDeviceBean);
                del = bindDeviceBean;
                break;
            }
        }
        bindDeviceBeans.remove(del);
        refreshBindViews(bindDeviceBeans);
        HashMap<String, Object> map = new HashMap<>();
        map.put("userID", userId);
        map.put("deviceID", deviceID);
        String strEntity = HttpUtil.getRequestString(map);
        RequestBody body = RequestBody
                .create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        AppService.createApi(MyApi.class).unBind(body).subscribeOn(Schedulers.io())
                .observeOn(
                        AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseResponse>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }


                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(final BaseResponse response) {
//                        if (!response.isSuccess()) {
//                            RxToast.showToast(response.result);
//                            return;
//                        }
//                        requestBindDevice();

                    }
                });
    }

    public void requestBindDevice() {
        requestBindDevice(null);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void requestBindDevice(RefreshBindDeviceDeviceEvent refreshBindDeviceDeviceEvent) {
        showCache();
//        final HashMap<String, Object> map = new HashMap<>();
//        map.put("userID", userId);
//        String strEntity = HttpUtil.getRequestString(map);
//        RequestBody body = RequestBody
//                .create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
//        AppService.createApi(MyApi.class).getBindDevices(body).subscribeOn(Schedulers.io())
//                .observeOn(
//                        AndroidSchedulers.mainThread())
//                .subscribe(new MySubscriber<GetBindDeviceResponse>() {
//                    @Override
//                    public void onStart() {
//                        super.onStart();
//                    }
//
//                    @Override
//                    public void onError(Throwable error) {
//                        //                        super.onError(error);
//                    }
//
//                    @Override
//                    public void onCompleted() {
//                        getBaseActivity().dismissLoadingDialog();
//                    }
//
//                    @Override
//                    public void onNext(final GetBindDeviceResponse response) {
//                        if (!response.isSuccess()) {
//                            RxToast.showToast(response.result);
//                            return;
//                        }
////                        Type type = new TypeToken<List<Device>>() {
////                        }.getType();
////                        List<Device> list = new GsonImpl().toList(response.result,
////                                Device.class, type);
////                        if (list.size() > 0) {
////                            RxSPUtilTool.putJSONCache(MyApplication.getInstance(), "bindDevice",
////                                    response.result);
////                        }
////                        refreshBindViews(list);
//                    }
//                });
    }

    private void refreshBindViews(List<BindDeviceBean> list) {
        llBindDevicesList.removeAllViews();
        for (final BindDeviceBean device : list) {
            final View inflate = LayoutInflater
                    .from(getActivity()).inflate(R.layout.item_find_device, null);
            SwipeLayout swipeLayout = inflate.findViewById(R.id.swipeLayout);
            swipeLayout.findViewById(R.id.checkbox).setVisibility(View.INVISIBLE);
            swipeLayout.findViewById(R.id.tv_1)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RxToast.showToast("????????????");
                        }
                    });
            swipeLayout.findViewById(R.id.tv_2)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            unbindDevice(device.deviceId);
                        }
                    });
            ((TextView) swipeLayout.findViewById(R.id.tv_2)).setText("????????????");
            // ????????????
            TextView deviceName = inflate.findViewById(R.id.tv_item1);
            IP_MAC tmp = new IP_MAC("", device.deviceId);
            if (mDeviceList.contains(tmp)) {
                deviceName.setText(device.deviceId + "(??????)");
            } else {
                deviceName.setText(device.deviceId);
            }
            TextView mac = inflate.findViewById(R.id.tv_item2);
            mac.setVisibility(View.GONE);

//            deviceName.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    if (TextUtils.isEmpty(ModelSelectBean.t1)) {
//                        RxToast.showToast("???????????????");
//                        return;
//                    }
//                    String ip = "";
//                    for (IP_MAC ipMac : mDeviceList) {
//                        if (ipMac.mMac.toLowerCase()
//                                .equals(device.getDeviceSN().toLowerCase())) {
//                            ip = ipMac.mIp;
//                        }
//                    }
//                    //                                    if (TextUtils.isEmpty(ip)) {
//                    //                                        RxToast.showToast("???????????????");
//                    //                                        return;
//                    //                                    }
//                    ModelSelectBean.deviceId = device.getDeviceSN();
//                    ModelSelectBean.ip = ip;
//                    SelectConfigAct.startUI(getActivity());
//                }
//            });
            llBindDevicesList.addView(inflate);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void bindDeviceRequest(String mac) {
        if (!MyApplication.getInstance().isLogin()) {
            RxToast.showToast("????????????");
            return;
        }
        String userId = RxSPUtilTool.getString(getActivity(), Constants.USER_ID);
        List<BindDeviceBean> bindDeviceBeans = queryBindFromDatabase();
        BindDeviceBean tmp = null;
        for (BindDeviceBean bindDeviceBean : bindDeviceBeans) {
            if (bindDeviceBean.deviceId.equals(mac) && bindDeviceBean.userId.equals(userId)) {
                tmp = bindDeviceBean;
            }
        }
        if (tmp == null && !TextUtils.isEmpty(mac)) {
            tmp = new BindDeviceBean();
            tmp.userId = userId;
            tmp.deviceId = mac;
            DaoManager.getInstance().getDaoSession().getBindDeviceBeanDao().insert(tmp);
            RxToast.showToast("????????????");
        } else {
            RxToast.showToast("?????????");
        }
        refreshBindViews(queryBindFromDatabase());
        HashMap<String, Object> map = new HashMap<>();
        map.put("deviceID", mac);
        map.put("userID", userId);
        String strEntity = HttpUtil.getRequestString(map);
        RequestBody body = RequestBody
                .create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        AppService.createApi(MyApi.class).bind(body).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(new Subscriber<BaseResponse>() {
            @Override
            public void onStart() {
                super.onStart();
//                getBaseActivity().showLoadingDialog();
            }

            @Override
            public void onError(Throwable error) {
//                getBaseActivity().dismissLoadingDialog();
            }

            @Override
            public void onCompleted() {
//                getBaseActivity().dismissLoadingDialog();
            }

            @Override
            public void onNext(final BaseResponse response) {
//                if (response.isSuccess()) {
//                    RxToast.showToast("????????????");
//                    requestBindDevice();
//                } else {
//                    RxToast.error(response.result);
//                }
            }
        });
    }

    private List<BindDeviceBean> queryBindFromDatabase() {
        return MyApplication.getInstance().queryCurrentBindDevice();

    }

    public void showRenameDialog(final String mac) {
        rxDialogLoading = new RxDialogEditSureCancel(getActivity());
        rxDialogLoading.setTitle("?????????");
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
                    updateOrInsertRename(name, mac);
                    refreshLocalDeviceViews();
                    rxDialogLoading.dismiss();
                }
            }
        });
        rxDialogLoading.show();
    }

    private void refreshLocalDeviceViews() {
        llFindDevicesList.removeAllViews();
        for (com.stealthcopter.networktools.subnet.Device device : mDevicesFound) {
            IP_MAC ipMac = new IP_MAC(device.ip, device.mac);
            if (!TextUtils.isEmpty(device.mac)) {
                addFindDeviceView(ipMac);
            }
        }
    }

    public boolean isDevice(String ip) {
        boolean conn = false;
        Socket soc = null;
        try {
            soc = new Socket();
            soc.connect(new InetSocketAddress(ip, 61818),2000);
            //??????socket??????????????????
            conn = soc.isConnected();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (soc != null && soc.isConnected()) {
                try {
                    soc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d("SOCKET", "conn ip" +ip +" "+conn);
        return conn;
    }

    private void updateOrInsertRename(String name, String mac) {
        boolean findDevice = false;
        renameMacs = DaoManager.getInstance().getDaoSession().getRenameMacDao().loadAll();
        for (RenameMac renameMac : renameMacs) {
            if (renameMac.mac.equals(mac)) {
                renameMac.name = name;
                findDevice = true;
                DaoManager.getInstance().getDaoSession().getRenameMacDao().update(renameMac);
                break;
            }
        }
        if (!findDevice) {
            RenameMac renameMac = new RenameMac();
            renameMac.mac = mac;
            renameMac.name = name;
            DaoManager.getInstance().getDaoSession().getRenameMacDao().insert(renameMac);
            renameMacs = DaoManager.getInstance().getDaoSession().getRenameMacDao().loadAll();
        }
    }

}
