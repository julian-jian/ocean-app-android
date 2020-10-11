package com.sky.lamp.ui.act;

import org.greenrobot.eventbus.EventBus;

import com.chenxi.tabview.adapter.ModelInfoAdapter;
import com.chenxi.tabview.listener.OnTabSelectedListener;
import com.chenxi.tabview.widget.Tab;
import com.chenxi.tabview.widget.TabContainerView;
import com.sky.lamp.BaseActivity;
import com.sky.lamp.R;
import com.sky.lamp.bean.CommandLightMode;
import com.sky.lamp.event.DemoShowEvent;
import com.sky.lamp.ui.fragment.DemoFragment;
import com.sky.lamp.ui.fragment.ModelInfoSettingFragment;
import com.sky.lamp.view.TitleBar;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ModeInfoActivity extends BaseActivity {
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
        //TODO 还是用db读
        CommandLightMode stickyEvent = EventBus.getDefault().getStickyEvent(CommandLightMode.class);
        if (stickyEvent == null) {
            finish();
            return;
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
                        break;
                    case 1:
                        actionBar.setTitle("demo演示");
                        EventBus.getDefault().post(mCommandLightMode);
                        demoFragment.refreshData();
                        break;
                }
            }
        });
    }

    public void refreshTitle(String text) {
        actionBar.getTitleTextView().setText(text);
    }
}
