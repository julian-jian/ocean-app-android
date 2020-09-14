package com.sky.lamp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.sky.lamp.BaseActivity;
import com.sky.lamp.R;
import com.sky.lamp.http.AppService;
import com.sky.lamp.http.MyApi;
import com.sky.lamp.response.StudyDetailsResponse;
import com.sky.lamp.utils.MySubscriber;
import com.sky.lamp.view.TitleBar;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class StudyDetailsActivity extends BaseActivity {


    @BindView(R.id.actionBar)
    TitleBar actionBar;
    @BindView(R.id.webView)
    WebView webView;

    public static void startUI(Context context, String id) {
        Intent intent = new Intent(context, StudyDetailsActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);
        ButterKnife.bind(this);
        actionBar.initLeftImageView(this);
        actionBar.getTitleTextView().setText("详情");
        query();
    }

    private void query() {
        AppService.createApi(MyApi.class).articleDetail(getIntent().getStringExtra("id")).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new MySubscriber<StudyDetailsResponse>() {
            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onError(Throwable error) {
                super.onError(error);
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onNext(final StudyDetailsResponse response) {
                if (response.code == 200) {
                    actionBar.setTitle(response.data.a_title);
                    webView.loadDataWithBaseURL("about:blank", response.data.a_content, "text/html", "utf-8", null);
                }
            }
        });
    }
}
