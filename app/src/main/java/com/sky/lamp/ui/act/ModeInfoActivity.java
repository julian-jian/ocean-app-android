package com.sky.lamp.ui.act;

import com.chenxi.tabview.adapter.MainViewAdapter;
import com.chenxi.tabview.adapter.ModelInfoAdapter;
import com.chenxi.tabview.listener.OnTabSelectedListener;
import com.chenxi.tabview.widget.Tab;
import com.chenxi.tabview.widget.TabContainerView;
import com.sky.lamp.BaseActivity;
import com.sky.lamp.R;
import com.sky.lamp.ui.fragment.Index2Fragment;
import com.sky.lamp.ui.fragment.Index3Fragment;
import com.sky.lamp.ui.fragment.ModelInfoSettingFragment;
import com.sky.lamp.view.TitleBar;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mainViewAdapter =
                new ModelInfoAdapter(getSupportFragmentManager(),
                        new Fragment[] {new ModelInfoSettingFragment()
                                , new Index2Fragment()});
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
                        break;
                }
            }
        });
    }

    public void refreshTitle(String text) {
        actionBar.getTitleTextView().setText(text);
    }
}
