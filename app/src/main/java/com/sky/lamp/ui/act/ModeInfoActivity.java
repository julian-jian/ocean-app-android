package com.sky.lamp.ui.act;

import java.util.ArrayList;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.chenxi.tabview.adapter.ModelInfoAdapter;
import com.chenxi.tabview.listener.OnTabSelectedListener;
import com.chenxi.tabview.widget.Tab;
import com.chenxi.tabview.widget.TabContainerView;
import com.orhanobut.logger.Logger;
import com.sky.lamp.BaseActivity;
import com.sky.lamp.R;
import com.sky.lamp.SocketManager;
import com.sky.lamp.bean.CommandLightMode;
import com.sky.lamp.bean.ModelSelectBean;
import com.sky.lamp.ui.fragment.DemoFragment;
import com.sky.lamp.ui.fragment.ModelInfoSettingFragment;
import com.sky.lamp.view.TitleBar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.LinearLayout;
import app.socketlib.com.library.events.ConnectSuccessEvent;
import app.socketlib.com.library.listener.SocketResponseListener;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ModeInfoActivity extends BaseActivity implements SocketResponseListener {
    @BindView(R.id.actionBar)
    TitleBar actionBar;
    @BindView(R.id.tab_container)
    TabContainerView tabContainer;
    @BindView(R.id.mainLayout)
    LinearLayout mainLayout;
    ModelInfoAdapter mainViewAdapter;
    public CommandLightMode mCommandLightMode;
    DemoFragment demoFragment = new DemoFragment();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final CommandLightMode stickyEvent =
                EventBus.getDefault().getStickyEvent(CommandLightMode.class);
        if (stickyEvent == null) {
            finish();
            return;
        }
        if (stickyEvent.getId() > 0) { // 读取子列表
            stickyEvent.getMParameters();
        } else { // 自定义
            stickyEvent.mParameters = new ArrayList<>();
        }
        mCommandLightMode = stickyEvent;
        ButterKnife.bind(this);
        mainViewAdapter =
                new ModelInfoAdapter(getSupportFragmentManager(),
                        new Fragment[] {new ModelInfoSettingFragment()
                                , demoFragment});
        tabContainer.setAdapter(mainViewAdapter);
        actionBar.getRootView().setVisibility(View.VISIBLE);
        actionBar.initLeftImageView(this);

        tabContainer.setOnTabSelectedListener(new OnTabSelectedListener() {
            /**
             * @param tab
             */
            @Override
            public void onTabSelected(Tab tab) {
                switch (tab.getIndex()) {
                    case 0:
                        refreshTitle(stickyEvent.t1 + "-" + stickyEvent.modelName);
                        break;
                    case 1:
                        actionBar.setTitle("demo演示");
                        EventBus.getDefault().post(mCommandLightMode);
                        demoFragment.refreshData();
                        break;
                }
            }
        });
        refreshTitle(stickyEvent.t1 + "-" + stickyEvent.modelName);
        EventBus.getDefault().register(this);

        SocketManager.getInstance().bindSocket(ModelSelectBean.ip);
        showLoadingDialog("正在建立连接...");
    }

    public void refreshTitle(String text) {
        actionBar.getTitleTextView().setText(text);
    }

    @Override
    protected void onDestroy() {
        SocketManager.getInstance().unbindSocket();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ConnectSuccessEvent event) {
        this.dismissLoadingDialog();
    }

    @Override
    public void socketMessageReceived(String msg) {
        Logger.d("socketMessageReceived() called with: msg = [" + msg + "]");
    }
}
