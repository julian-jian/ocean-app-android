package com.sky.lamp.ui.act;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.daimajia.swipe.SwipeLayout;
import com.orhanobut.logger.Logger;
import com.sky.lamp.BaseActivity;
import com.sky.lamp.Constants;
import com.sky.lamp.MyApplication;
import com.sky.lamp.R;
import com.sky.lamp.bean.BindDeviceBean;
import com.sky.lamp.bean.ModelSelectBean;
import com.sky.lamp.bean.RenameMac;
import com.sky.lamp.dao.DaoManager;
import com.sky.lamp.http.AppService;
import com.sky.lamp.http.MyApi;
import com.sky.lamp.response.BaseResponse;
import com.sky.lamp.utils.HttpUtil;
import com.sky.lamp.utils.RxSPUtilTool;
import com.sky.lamp.view.TitleBar;
import com.stealthcopter.networktools.IPTools;
import com.stealthcopter.networktools.SubnetDevices;
import com.vondear.rxtools.view.RxToast;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class Index1SubActivity extends BaseActivity {

    @BindView(R.id.actionBar)
    TitleBar titleBar;
    @BindView(R.id.ll_bind_devices_list)
    LinearLayout llBindDeviceViews;
    private HashMap<String, String> mLocalDeviceList = new HashMap<String, String>();
    private List<BindDeviceBean> mBindServerList = new ArrayList<>();
    private List<RenameMac> mRenameMacs = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_select_devices);
        ButterKnife.bind(this);
        initViews();
        mRenameMacs =
                DaoManager.getInstance().getDaoSession().getRenameMacDao()
                        .loadAll();
        if (mRenameMacs == null) {
            mRenameMacs = new ArrayList<>();
        }
        refreshBindData();
        startFindDevices();
    }

    private void refreshBindData() {
        //        String response = RxSPUtilTool.readJSONCache(MyApplication.getInstance(),
        //        "bindDevice");
        //        if (!TextUtils.isEmpty(response)) {
        //            Type type = new TypeToken<List<Device>>() {
        //            }.getType();
        //            mBindServerList = new GsonImpl().toList(response,
        //                    Device.class, type);
        //        }
        mBindServerList = MyApplication.getInstance().queryCurrentBindDevice();
        refreshBindDeviceViews();
    }

    private void initViews() {
        titleBar.setTitle("??????");
        titleBar.setRightText("?????????");
        titleBar.getRightTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextStepClick();
            }
        });
        titleBar.initLeftImageView(this);
    }

    public void nextStepClick() {
        List<String> ips = new ArrayList<>();
        boolean isCheck = false;
        for (BindDeviceBean device : mBindServerList) {
            if (device.isChecked) {
                isCheck = true;
                break;
            }
        }
        if (!isCheck) {
            RxToast.showToast("??????????????????");
            return;
        }

        boolean hasError = false;
        for (BindDeviceBean device : mBindServerList) {
            if (mLocalDeviceList.get(device.getDeviceSN()) == null && device.isChecked) {
                hasError = true;
            }
            if (device.isChecked && mLocalDeviceList.get(device.getDeviceSN()) != null) {
                ips.add(mLocalDeviceList.get(device.getDeviceSN()));
            }
        }
        if (hasError) {
            RxToast.showToast("?????????????????????????????????");
        }
        if (!MyApplication.getInstance().isLogin()) {
            RxToast.showToast("????????????");
            startActivity(new Intent(this, LoginAct.class));
            return;
        }
        ModelSelectBean.ips = ips;
        Intent intent = new Intent(this, SelectConfigAct.class);
        startActivity(intent);
    }

    private void startFindDevices() {
        InetAddress ipv4 = IPTools.getLocalIPv4Address();
        if (ipv4 == null) {
            RxToast.showToast("???????????????");
            return;
        }
        showLoadingDialog("??????????????????...");
        SubnetDevices.fromLocalAddress().setTimeOutMillis(5000)
                .findDevices(new SubnetDevices.OnSubnetDeviceFound() {
                    @Override
                    public void onDeviceFound(com.stealthcopter.networktools.subnet.Device device) {
                        Logger.d(device.toString());
                    }

                    @Override
                    public void onFinished(
                            final ArrayList<com.stealthcopter.networktools.subnet.Device> devicesFound) {
                        if (devicesFound != null) {
                            Logger.d("onFinished size = " + devicesFound.size());
                        } else {
                            Logger.d("onFinished size = " + 0);
                        }
                        mLocalDeviceList.clear();
                        for (com.stealthcopter.networktools.subnet.Device device : devicesFound) {
                            if (!device.ip.contains(":") && !TextUtils.isEmpty(device.mac)) {
                                // mac????????????
                                if (device.mac.toLowerCase().startsWith("8c:")) {
                                    mLocalDeviceList.put(device.mac.toLowerCase(), device.ip);
                                    // 8c??????8e
                                    String newMac = device.mac.replace("8c:", "8e:");
                                    mLocalDeviceList.put(newMac.toLowerCase(), device.ip);
                                } else if (device.mac.toLowerCase().startsWith("8e:")) {
                                    mLocalDeviceList.put(device.mac.toLowerCase(), device.ip);
                                    // 8e??????8c
                                    String newMac = device.mac.replace("8e:", "8c:");
                                    mLocalDeviceList.put(newMac.toLowerCase(), device.ip);
                                } else {
                                    mLocalDeviceList.put(device.mac.toLowerCase(), device.ip);
                                }
                            }
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismissLoadingDialog();
                                refreshBindDeviceViews();
                            }
                        });
                    }
                });
    }

    public static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            System.out.println(str.charAt(i));
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public void unbindDeviceRequest(String deviceID) {
        String userId = RxSPUtilTool.getString(this, Constants.USER_ID);
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
        refreshBindData();
        HashMap<String, Object> map = new HashMap<>();
        map.put("userID", userId);
        map.put("deviceID", deviceID);
        String strEntity = HttpUtil.getRequestString(map);
        RequestBody body = RequestBody
                .create(MediaType.parse("application/json;charset=UTF-8"), strEntity);
        AppService.createApi(MyApi.class).unBind(body).subscribeOn(Schedulers.io())
                .observeOn(
                        AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseResponse>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        //                        showLoadingDialog();
                    }

                    @Override
                    public void onError(Throwable error) {
                        //                        dismissLoadingDialog();
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onNext(final BaseResponse response) {
                        //                        if (!response.isSuccess()) {
                        //                            RxToast.showToast(response.result);
                        //                            return;
                        //                        }
                    }
                });
    }

    private void refreshBindDeviceViews() {
        for (BindDeviceBean device : mBindServerList) {
            device.deviceId = (device.deviceId.toLowerCase());
        }
        addBindViews(mBindServerList);
    }

    private void addBindViews(List<BindDeviceBean> list) {
        llBindDeviceViews.removeAllViews();
        for (final BindDeviceBean device : list) {
            final View inflate = LayoutInflater
                    .from(Index1SubActivity.this).inflate(R.layout.item_find_device,
                            null);
            SwipeLayout swipeLayout = inflate.findViewById(R.id.swipeLayout);
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
                            unbindDeviceRequest(device.getDeviceSN());
                        }
                    });
            CheckBox checkBox = swipeLayout.findViewById(R.id.checkbox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    device.isChecked = isChecked;
                }
            });
            ((TextView) swipeLayout.findViewById(R.id.tv_2)).setText("????????????");
            // ????????????
            TextView deviceName = inflate.findViewById(R.id.tv_item1);
            TextView tv2 = inflate.findViewById(R.id.tv_item2);
            String pre = "";
            for (RenameMac renameMac : mRenameMacs) {
                if (renameMac.mac.equals(device.getDeviceSN())) {
                    pre = renameMac.getName();
                    break;
                }
                if (renameMac.mac.replace("8c", "8e").equals(device.getDeviceSN())) {
                    pre = renameMac.getName();
                    break;
                }
            }
            if (TextUtils.isEmpty(pre)) {
                tv2.setVisibility(View.GONE);
            } else {
                tv2.setVisibility(View.VISIBLE);
                tv2.setText(pre);
            }
            if (mLocalDeviceList.get(device.getDeviceSN()) != null) {
                deviceName.setText(device.getDeviceSN() + "(??????)");
            } else {
                deviceName.setText(device.getDeviceSN());
            }

            deviceName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            llBindDeviceViews.addView(inflate);
        }
    }

}
