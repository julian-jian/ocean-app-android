package com.sky.lamp.ui.act;

import java.lang.ref.WeakReference;

import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.guo.duoduo.wifidetective.core.wifiscan.WiFiBroadcastReceiver;
import com.guo.duoduo.wifidetective.entity.RouterList;
import com.guo.duoduo.wifidetective.util.Constant;
import com.guo.duoduo.wifidetective.util.NetworkUtil;
import com.guo.duoduo.wifidetective.util.ToastUtils;
import com.sky.lamp.BaseActivity;
import com.sky.lamp.R;
import com.sky.lamp.adapter.WifiListAdapter;
import com.sky.lamp.response.WifiResponse;
import com.sky.lamp.view.TitleBar;

import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        ButterKnife.bind(this);
        actionBar.setTitle("配置设置");
        actionBar.initLeftImageView(this);
        mWiFiScanHandler = new WiFiScanHandler(this);

        adapter = new WifiListAdapter(WifiListAdapter.ProductViewHolder.class);
        final LRecyclerViewAdapter lRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        recyclerListView.setAdapter(lRecyclerViewAdapter);
    }

    @OnClick({R.id.btn_search, R.id.btn_send_pwd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_search:
                initWiFi();
                break;
            case R.id.btn_send_pwd:
                break;
        }
    }

    private void initWiFi()
    {
        mWifiManager = NetworkUtil.getWifiManager(getApplicationContext());
        if (mWifiManager == null)
        {
            ToastUtils.showTextToast(getApplicationContext(), R.string.net_error);
            finish();
            return;
        }

        mWiFiBroadcastReceiver = new WiFiBroadcastReceiver(mWifiManager, mWiFiScanHandler);
        IntentFilter intentFilter = new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(mWiFiBroadcastReceiver, intentFilter);

        boolean success = mWifiManager.startScan();
        System.out.println("ConfigAct.initWiFi "+success);
    }

    public static class WiFiScanHandler extends android.os.Handler
    {

        private WeakReference<ConfigAct> mWifiScanActivity;

        public WiFiScanHandler(ConfigAct activity)
        {
            mWifiScanActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg)
        {
            ConfigAct activity = mWifiScanActivity.get();
            if (activity == null)
                return;

            switch (msg.what)
            {
                case Constant.MSG.WIFI_SCAN_RESULT :
                {
                    if (msg.obj != null)
                    {   activity.adapter.clear();
                        SparseArray<RouterList> routerListSparseArray = (SparseArray<RouterList>) msg.obj;
                        for (int i = 0; i < routerListSparseArray.size(); i++)
                        {

                            WifiResponse.DataBean data = new WifiResponse.DataBean();
                            data.name = routerListSparseArray.get(0).mRouterList.get(i).mSsid;
                            data.mac = routerListSparseArray.get(0).mRouterList.get(i).mMac;
                            activity.adapter.add(data);
                        }
                    }
//                    activity.mWiFiScanAdapter.notifyDataSetChanged();
                    activity.mWifiManager.startScan();//处理后冲洗扫描
                    break;
                }
            }

        }
    }
}
