package com.sky.lamp.ui.act;

import com.sky.lamp.BaseActivity;
import com.sky.lamp.R;
import com.sky.lamp.view.TitleBar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.SeekBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomAct extends BaseActivity {
    @BindView(R.id.actionBar)
    TitleBar actionBar;
    @BindView(R.id.tv_startTime)
    TextView tvStartTime;
    @BindView(R.id.tv_endTime)
    TextView tvEndTime;
    @BindView(R.id.controller2)
    SeekBar controller2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);
        ButterKnife.bind(this);
        actionBar.setTitle("自定义");
        actionBar.initLeftImageView(this);
    }

}
