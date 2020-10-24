package com.sky.lamp.ui.act;

import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.daimajia.swipe.SwipeLayout;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;
import com.sky.lamp.BaseActivity;
import com.sky.lamp.BuildConfig;
import com.sky.lamp.Constants;
import com.sky.lamp.MyApplication;
import com.sky.lamp.R;
import com.sky.lamp.bean.Device;
import com.sky.lamp.bean.ModelSelectBean;
import com.sky.lamp.bean.RenameMac;
import com.sky.lamp.dao.DaoManager;
import com.sky.lamp.http.AppService;
import com.sky.lamp.http.MyApi;
import com.sky.lamp.response.BaseResponse;
import com.sky.lamp.response.GetBindDeviceResponse;
import com.sky.lamp.utils.GsonImpl;
import com.sky.lamp.utils.HttpUtil;
import com.sky.lamp.utils.MySubscriber;
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
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class Index1SubActivity extends BaseActivity {

    @BindView(R.id.actionBar)
    TitleBar titleBar;
    @BindView(R.id.ll_bind_devices_list)
    LinearLayout llBindDevicesList;
    private HashMap<String, String> mLocalDeviceList = new HashMap<String, String>();
    private List<Device> mBindServerList = new ArrayList<>();
    private List<RenameMac> mRenameMacs = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_select_devices);
        ButterKnife.bind(this);
        initViews();
        startFindDevices();
        mRenameMacs =
                DaoManager.getInstance().getDaoSession().getRenameMacDao()
                        .loadAll();
        if (mRenameMacs == null) {
            mRenameMacs = new ArrayList<>();
        }
        showCache();
        requestBindDevice();
    }

    private void showCache() {
        String response = RxSPUtilTool.readJSONCache(MyApplication.getInstance(),"bindDevice");
        if (!TextUtils.isEmpty(response)) {
            Type type = new TypeToken<List<Device>>() {
            }.getType();
            mBindServerList = new GsonImpl().toList(response,
                    Device.class, type);
        }
        refreshBindDeviceViews();
    }

    private void initViews() {
        titleBar.setTitle("设备");
        titleBar.setRightText("下一步");
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
        boolean hasError = false;
        for (Device device : mBindServerList) {
            if (mLocalDeviceList.get(device.getDeviceSN()) == null && device.isChecked) {
                hasError = true;
            } else if( device.isChecked){
                ips.add(mLocalDeviceList.get(device.getDeviceSN()));
            }
        }
        if (hasError && !BuildConfig.DEBUG) {
            RxToast.showToast("所选设备包含未上线设备");
            return;
        }
        if (!MyApplication.getInstance().isLogin()) {
            RxToast.showToast("请先登录");
            startActivity(new Intent(this, LoginAct.class));
            return;
        }
        if (ips.size() == 0) {
            RxToast.showToast("未选中设备");
            return;
        }
        ModelSelectBean.ips = ips;
        Intent intent = new Intent(this, SelectConfigAct.class);
        startActivity(intent);
    }

    private void startFindDevices() {
        InetAddress ipv4 = IPTools.getLocalIPv4Address();
        if (ipv4 == null) {
            RxToast.showToast("请检查网络");
            return;
        }
        showLoadingDialog("局域网搜索中...");
        SubnetDevices.fromLocalAddress().findDevices(new SubnetDevices.OnSubnetDeviceFound() {
            @Override
            public void onDeviceFound(com.stealthcopter.networktools.subnet.Device device) {
                Logger.d(device.toString());
            }

            @Override
            public void onFinished(
                    final ArrayList<com.stealthcopter.networktools.subnet.Device> devicesFound) {
                mLocalDeviceList.clear();
                for (com.stealthcopter.networktools.subnet.Device device : devicesFound) {
                    mLocalDeviceList.put(device.mac, device.ip);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoadingDialog();
                        addBindViews(mBindServerList);
                    }
                });
            }
        });
    }

    public void unbindDeviceRequest(String deviceID) {
        String userId = RxSPUtilTool.getString(this, Constants.USER_ID);
        HashMap<String, Object> map = new HashMap<>();
        map.put("userID", userId);
        map.put("deviceID", deviceID);
        String strEntity = HttpUtil.getRequestString(map);
        RequestBody body = RequestBody
                .create(MediaType.parse("application/json;charset=UTF-8"), strEntity);
        AppService.createApi(MyApi.class).unBind(body).subscribeOn(Schedulers.io())
                .observeOn(
                        AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<BaseResponse>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoadingDialog();
                    }

                    @Override
                    public void onError(Throwable error) {
                        super.onError(error);
                        dismissLoadingDialog();
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
                        requestBindDevice();

                    }
                });
    }

    public void requestBindDevice() {
        String userId = RxSPUtilTool.getString(this, Constants.USER_ID);
        if (TextUtils.isEmpty(userId)) {
            Logger.d("not login");
            return;
        }
        final HashMap<String, Object> map = new HashMap<>();
        map.put("userID", userId);
        String strEntity = HttpUtil.getRequestString(map);
        RequestBody body = RequestBody
                .create(MediaType.parse("application/json;charset=UTF-8"), strEntity);
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
                    }

                    @Override
                    public void onNext(final GetBindDeviceResponse response) {
                        if (!response.isSuccess()) {
                            RxToast.showToast(response.result);
                            return;
                        }
                        Type type = new TypeToken<List<Device>>() {
                        }.getType();
                        mBindServerList = new GsonImpl().toList(response.result,
                                Device.class, type);
                        if (mBindServerList.size() > 0) {
                            RxSPUtilTool.putJSONCache(MyApplication.getInstance(),"bindDevice",response.result);
                        }
                        refreshBindDeviceViews();
                    }
                });
    }

    private void refreshBindDeviceViews() {
        for (Device device : mBindServerList) {
            device.setDeviceSN(device.getDeviceSN().toLowerCase());
        }
        addBindViews(mBindServerList);
    }

    private void addBindViews(List<Device> list) {
        llBindDevicesList.removeAllViews();
        for (final Device device : list) {
            final View inflate = LayoutInflater
                    .from(Index1SubActivity.this).inflate(R.layout.item_find_device,
                            null);
            SwipeLayout swipeLayout = inflate.findViewById(R.id.swipeLayout);
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
            ((TextView) swipeLayout.findViewById(R.id.tv_2)).setText("解除绑定");
            // 禁用左划
            TextView deviceName = inflate.findViewById(R.id.tv_item1);
            TextView tv2 = inflate.findViewById(R.id.tv_item2);
            String pre = "";
            for (RenameMac renameMac : mRenameMacs) {
                if (renameMac.mac.equals(device.getDeviceSN())) {
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
                deviceName.setText(device.getDeviceSN() + "(在线)");
            } else {
                deviceName.setText(device.getDeviceSN());
            }

            deviceName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            llBindDevicesList.addView(inflate);
        }
    }

}
