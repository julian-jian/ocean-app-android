package com.sky.lamp.ui.act;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.guo.duoduo.wifidetective.core.devicescan.IP_MAC;
import com.guo.duoduo.wifidetective.core.wifiscan.WiFiBroadcastReceiver;
import com.guo.duoduo.wifidetective.entity.RouterInfo;
import com.guo.duoduo.wifidetective.entity.RouterList;
import com.guo.duoduo.wifidetective.util.Constant;
import com.guo.duoduo.wifidetective.util.NetworkUtil;
import com.guo.duoduo.wifidetective.util.ToastUtils;
import com.sky.lamp.BaseActivity;
import com.sky.lamp.Constants;
import com.sky.lamp.MyApplication;
import com.sky.lamp.R;
import com.sky.lamp.adapter.WifiListAdapter;
import com.sky.lamp.http.AppService;
import com.sky.lamp.http.MyApi;
import com.sky.lamp.response.BaseResponse;
import com.sky.lamp.response.WifiResponse;
import com.sky.lamp.utils.HttpUtil;
import com.sky.lamp.utils.MySubscriber;
import com.sky.lamp.utils.RxSPUtilTool;
import com.sky.lamp.view.TitleBar;
import com.vondear.rxtools.view.RxToast;

import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ConfigAct extends BaseActivity {
    @BindView(R.id.actionBar)
    TitleBar actionBar;
    @BindView(R.id.et_wifi_name)
    EditText etWifiName;
    @BindView(R.id.et_wifi_pwd)
    EditText etWifiPwd;
    @BindView(R.id.iv_pwd_show)
    ImageView ivPwdShow;
    @BindView(R.id.btn_search)
    Button btnSearch;
    @BindView(R.id.btn_send_pwd)
    Button btnSendPwd;
    @BindView(R.id.recyclerListView)
    LRecyclerView recyclerListView;
    private WifiListAdapter adapter;
    private WiFiBroadcastReceiver mWiFiBroadcastReceiver = null;
    private WifiManager mWifiManager;
    private WiFiScanHandler mWiFiScanHandler;
    private IP_MAC ipMac;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        ButterKnife.bind(this);
        actionBar.setTitle("配置设置");
        actionBar.initLeftImageView(this);
        ipMac = (IP_MAC) getIntent().getSerializableExtra("device");
        mWiFiScanHandler = new WiFiScanHandler(this);

        adapter = new WifiListAdapter(WifiListAdapter.ProductViewHolder.class);
        final LRecyclerViewAdapter lRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        recyclerListView.setAdapter(lRecyclerViewAdapter);

        recyclerListView.setLayoutManager(new LinearLayoutManager(this));
        DividerDecoration divider =
                new DividerDecoration.Builder(this).setHeight(R.dimen.default_divider_height)
                        //                .setPadding(R.dimen.default_divider_padding)
                        .setColorResource(R.color.divide).build();
        recyclerListView.addItemDecoration(divider);
        recyclerListView.setLoadMoreEnabled(false);
        lRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                RouterInfo info = adapter.getItems().get(position);
                etWifiName.setText(info.mSsid);
            }
        });
        showLoadingDialog("正在搜索");
        initWiFi();
    }

    @OnClick({R.id.btn_search, R.id.btn_send_pwd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_search:
                showLoadingDialog("正在搜索");
                initWiFi();
                break;
            case R.id.btn_send_pwd:
                sendWifiPwdToDevice();
                break;
        }
    }

    private void sendWifiPwdToDevice() {

    }

    private void initWiFi() {
        mWifiManager = NetworkUtil.getWifiManager(getApplicationContext());
        if (mWifiManager == null) {
            ToastUtils.showTextToast(getApplicationContext(), R.string.net_error);
            finish();
            return;
        }

        mWiFiBroadcastReceiver = new WiFiBroadcastReceiver(mWifiManager, mWiFiScanHandler);
        IntentFilter intentFilter = new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(mWiFiBroadcastReceiver, intentFilter);

        boolean success = mWifiManager.startScan();
        System.out.println("ConfigAct.initWiFi " + success);
    }

    @Override
    protected void onDestroy() {
        try {
            if (mWiFiBroadcastReceiver != null) {
                unregisterReceiver(mWiFiBroadcastReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    public void bindRequest() {
        if (!MyApplication.getInstance().isLogin()) {
            RxToast.showToast("请先登录");
            return;
        }
        String userId = RxSPUtilTool.getString(this, Constants.USER_ID);
        HashMap<String, Object> map = new HashMap<>();
        map.put("deviceID", ipMac.mMac);
        map.put("userID", userId);
        String strEntity = HttpUtil.getRequestString(map);
        RequestBody body = RequestBody
                .create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        AppService.createApi(MyApi.class).bind(body).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(new MySubscriber<BaseResponse>() {
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
                dismissLoadingDialog();
            }

            @Override
            public void onNext(final BaseResponse response) {
                if (response.isSuccess()) {
                    RxToast.showToast("绑定成功");
                    finish();
                } else {
                    RxToast.error(response.result);
                }
            }
        });
    }

    public static class WiFiScanHandler extends android.os.Handler {

        private WeakReference<ConfigAct> mWifiScanActivity;

        public WiFiScanHandler(ConfigAct activity) {
            mWifiScanActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ConfigAct activity = mWifiScanActivity.get();
            if (activity == null) {
                return;
            }

            switch (msg.what) {
                case Constant.MSG.WIFI_SCAN_RESULT: {
                    activity.dismissLoadingDialog();
                    if (msg.obj != null) {
                        activity.adapter.clear();
                        SparseArray<RouterList> routerListSparseArray =
                                (SparseArray<RouterList>) msg.obj;
                        for (int i = 0; i < routerListSparseArray.size(); i++) {

                            WifiResponse.DataBean data = new WifiResponse.DataBean();
                            List<RouterInfo> mRouterList =
                                    routerListSparseArray.valueAt(i).mRouterList;
                            activity.adapter.addAll(mRouterList);
                        }
                    }
                    //                    activity.mWiFiScanAdapter.notifyDataSetChanged();
                    //                    activity.mWifiManager.startScan();//处理后冲洗扫描
                    break;
                }
            }

        }
    }
}
