package com.sky.lamp.ui;

import com.sky.lamp.BaseActivity;
import com.sky.lamp.R;
import com.sky.lamp.view.TitleBar;

import android.os.Bundle;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutUsActivity extends BaseActivity {
    @BindView(R.id.webView)
    com.tencent.smtt.sdk.WebView webView;
    @BindView(R.id.actionBar)
    TitleBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        actionBar.initLeftImageView(this);
        actionBar.setTitle("关于我们");
        webView.loadUrl("http://transform.suoteng.net/index/about");
//        webView.setCurWebUrl()
////                .addJavascriptInterface(new CommWebView.JSCallJava(), "NativeObj")
//                .startCallback(new WebViewCallback() {
//                    @Override
//                    public void onStart() {
//                    }
//
//                    @Override
//                    public void onProgress(int curProgress) {
//                    }
//
//                    @Override
//                    public void onError(int errorCode, String description, String failingUrl) {
//                        super.onError(errorCode, description, failingUrl);
//                    }
//                });
    }

}
