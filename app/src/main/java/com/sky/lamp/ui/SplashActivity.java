package com.sky.lamp.ui;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.text.TextUtils;

import com.sky.lamp.BaseActivity;
import com.sky.lamp.BuildConfig;
import com.sky.lamp.MainActivity;
import com.sky.lamp.R;
import com.sky.lamp.utils.RxSPUtilTool;
import com.vondear.rxtools.RxActivityTool;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (BuildConfig.DEBUG) {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(RxSPUtilTool.getString(SplashActivity.this, "name"))) {//已登录
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                } else {
                    RxActivityTool.skipActivity(SplashActivity.this, LoginAcitivty.class);
                }
            }
        }, 2000);
    }
}
