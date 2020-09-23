package com.sky.lamp.ui.act;

import com.sky.lamp.R;
import com.sky.udp.SendUtil;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DebugAct extends Activity {
    @BindView(R.id.btn_send)
    Button btnSend;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_send)
    public void onViewClicked() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SendUtil.send("192.168.4.1",61818);
            }
        }).start();

    }
}
