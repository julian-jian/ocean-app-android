package com.sky.lamp;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.chenxi.tabview.adapter.MainViewAdapter;
import com.chenxi.tabview.listener.OnTabSelectedListener;
import com.chenxi.tabview.widget.Tab;
import com.chenxi.tabview.widget.TabContainerView;
import com.githang.statusbar.StatusBarCompat;
import com.sky.lamp.event.LoginOutEvent;
import com.sky.lamp.ui.Index2Fragment;
import com.sky.lamp.ui.Index3Fragment;
import com.sky.lamp.ui.IndexFragment;
import com.sky.lamp.ui.act.LoginAct;
import com.sky.lamp.utils.RxSPUtilTool;
import com.sky.lamp.view.TitleBar;
import com.vondear.rxtools.view.RxToast;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

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
            startActivity(new Intent(this,LoginAct.class));
            finish();
            return;
        }
        changeStatusColor(0);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mainViewAdapter = new MainViewAdapter(getSupportFragmentManager(), new Fragment[]{new IndexFragment()
                , new Index2Fragment(),new Index3Fragment()});
        mainViewAdapter.setHasMsgIndex(3);
        tabContainer.setAdapter(mainViewAdapter);
        actionBar.getRootView().setVisibility(View.VISIBLE);
//        actionBar.initLeftImageView(this);
        actionBar.getTitleTextView().setText("ReeSun LED");
        tabContainer.setOnTabSelectedListener(new OnTabSelectedListener() {
            /**
             * @param tab
             */
            @Override
            public void onTabSelected(Tab tab) {
                changeStatusColor(tab.getIndex());
                switch (tab.getIndex()) {
                    case 0:
                        actionBar.getRootView().setVisibility(View.VISIBLE);
                        actionBar.getTitleTextView().setText("ReeSun LED");
                        break;
                    case 1:
                        actionBar.getRootView().setVisibility(View.VISIBLE);
                        actionBar.getTitleTextView().setText("设备");
                        break;
                    case 2:
                        actionBar.getRootView().setVisibility(View.GONE);
                        actionBar.getTitleTextView().setText("我的");
                        break;
                }
            }
        });
//        RxPermissions rxPermissions=new RxPermissions(this);
//        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CALL_PHONE,Manifest.permission.INTERNET).subscribe(new Consumer<Boolean>() {
//            @Override
//            public void accept(Boolean aBoolean) throws Exception {
//                if (aBoolean){
//                    //申请的权限全部允许
//                }else{
//                    //只要有一个权限被拒绝，就会执行
//                    Toast.makeText(MainActivity.this, "未授权权限，部分功能不能使用", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    private void changeStatusColor(int index) {
        if (index == 2) {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.mine_blue));
        } else {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(android.R.color.white));
        }
    }

    public void tabSelect(int index){
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

}
