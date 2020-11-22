package com.sky.lamp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.chenxi.tabview.adapter.MainViewAdapter;
import com.chenxi.tabview.listener.OnTabSelectedListener;
import com.chenxi.tabview.widget.Tab;
import com.chenxi.tabview.widget.TabContainerView;
import com.event.NextStepEvent;
import com.githang.statusbar.StatusBarCompat;
import com.sky.lamp.event.LoginOutEvent;
import com.sky.lamp.ui.act.LoginAct;
import com.sky.lamp.ui.fragment.Index2Fragment;
import com.sky.lamp.ui.fragment.Index3Fragment;
import com.sky.lamp.ui.fragment.IndexFragment;
import com.sky.lamp.view.TitleBar;
import com.vondear.rxtools.view.RxToast;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity {

    @BindView(R.id.actionBar)
    TitleBar actionBar;
    @BindView(R.id.tab_container)
    TabContainerView tabContainer;
    @BindView(R.id.mainLayout)
    LinearLayout mainLayout;
    MainViewAdapter mainViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);
        boolean skip = getIntent().getBooleanExtra("skip", false);
        if (!MyApplication.getInstance().isLogin() && !skip) {
            startActivity(new Intent(this, LoginAct.class));
            finish();
            return;
        }
        changeStatusColor(0);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mainViewAdapter =
                new MainViewAdapter(getSupportFragmentManager(), new Fragment[] {new IndexFragment()
                        , new Index2Fragment(), new Index3Fragment()});
        mainViewAdapter.setHasMsgIndex(3);
        tabContainer.setAdapter(mainViewAdapter);
        actionBar.getRootView().setVisibility(View.VISIBLE);
        actionBar.getTitleTextView().setText(R.string.reefSun);
        tabContainer.setOnTabSelectedListener(new OnTabSelectedListener() {
            /**
             * @param tab
             */
            @Override
            public void onTabSelected(Tab tab) {
                changeStatusColor(tab.getIndex());
                switch (tab.getIndex()) {
                    case 0:
                        actionBar.getRootView().setVisibility(View.GONE);
                        actionBar.getTitleTextView().setText(R.string.reefSun);
                        actionBar.setRightText("");
                        break;
                    case 1:
                        actionBar.getRootView().setVisibility(View.VISIBLE);
                        actionBar.getTitleTextView().setText("设备");
                        actionBar.setRightText("下一步");
                        actionBar.getRightTextView().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EventBus.getDefault().postSticky(new NextStepEvent());
                            }
                        });
                        break;
                    case 2:
                        actionBar.setRightText("");
                        actionBar.getRootView().setVisibility(View.GONE);
                        actionBar.getTitleTextView().setText("我的");
                        break;
                }
            }
        });
        actionBar.getRootView().setVisibility(View.GONE);
        methodRequiresTwoPermission();

    }

    private void changeStatusColor(int index) {

        if (index == 2) {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.mine_blue));
        }else if (index == 0) {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.index1));
        } else {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(android.R.color.white));
        }
    }

    public void tabSelect(int index) {
        tabContainer.setCurrentItem(index);
    }

    //退出时的时间
    private long mExitTime;

    //对返回键进行监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            RxToast.showToast("再按一次退出应用");
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }

    @Subscribe
    public void onLoginOut(LoginOutEvent event) {
        startActivity(new Intent(this, LoginAct.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private int RC_CAMERA_AND_LOCATION = 0;

    private void methodRequiresTwoPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // 已经有权限
        } else {
            // 没有权限，现在请求他们
            //只有用户首次安装时拒绝了权限，才会在下次申请时弹出 "此app需要xxx权限"提示框
            EasyPermissions.requestPermissions(this, "此app需要获取授权", RC_CAMERA_AND_LOCATION,
                    perms);
        }
    }

    public static class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            System.out.println("MyHandler.handleMessage");
        }
    }


    public String getFromAssets(String fileName) throws IOException {
        InputStreamReader inputReader =
                new InputStreamReader(getResources().getAssets().open(fileName), "GB2312");
        BufferedReader bufReader = new BufferedReader(inputReader);
        String line = "";
        String Result = "";
        while ((line = bufReader.readLine()) != null) {
            Result += line;
        }
        return Result;
    }
}
