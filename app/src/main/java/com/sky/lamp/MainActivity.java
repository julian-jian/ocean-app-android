package com.sky.lamp;

import com.chenxi.tabview.adapter.MainViewAdapter;
import com.chenxi.tabview.listener.OnTabSelectedListener;
import com.chenxi.tabview.widget.Tab;
import com.chenxi.tabview.widget.TabContainerView;
import com.githang.statusbar.StatusBarCompat;
import com.hacknife.immersive.Immersive;
import com.sky.lamp.ui.Index2Fragment;
import com.sky.lamp.ui.Index3Fragment;
import com.sky.lamp.ui.IndexFragment;
import com.sky.lamp.view.TitleBar;
import com.vondear.rxtools.view.RxToast;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
//        StatusBarCompat.setStatusBarColor(this, getResources().getColor(android.R.color.holo_red_dark));
        ButterKnife.bind(this);
        mainViewAdapter = new MainViewAdapter(getSupportFragmentManager(), new Fragment[]{new IndexFragment()
                , new Index2Fragment(),new Index3Fragment()});
        mainViewAdapter.setHasMsgIndex(3);
        tabContainer.setAdapter(mainViewAdapter);
        actionBar.getRootView().setVisibility(View.VISIBLE);
        actionBar.getTitleTextView().setText("首页");
        tabContainer.setOnTabSelectedListener(new OnTabSelectedListener() {
            /**
             * @param tab
             */
            @Override
            public void onTabSelected(Tab tab) {
                switch (tab.getIndex()) {
                    case 0:
                        actionBar.getRootView().setVisibility(View.GONE);
                        actionBar.getTitleTextView().setText("首页");
                        break;
                    case 1:
                        actionBar.getRootView().setVisibility(View.GONE);
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


   /* @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }*/

}
