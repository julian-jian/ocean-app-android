package com.sky.lamp.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.sky.lamp.BaseActivity;
import com.sky.lamp.MainActivity;
import com.sky.lamp.MyApplication;
import com.sky.lamp.R;
import com.sky.lamp.http.AppService;
import com.sky.lamp.http.MyApi;
import com.sky.lamp.response.LoginResponse;
import com.sky.lamp.utils.MySubscriber;
import com.sky.lamp.utils.RxSPUtilTool;
import com.sky.lamp.view.TitleBar;
import com.vondear.rxtools.RxActivityTool;
import com.vondear.rxtools.view.RxToast;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zhangfy on 2018/8/2.
 */

public class LoginAcitivty extends BaseActivity {
    @BindView(R.id.userNameEt)
    EditText userNameEt;
    @BindView(R.id.pwdEt)
    EditText pwdEt;
    @BindView(R.id.loginBtn)
    Button loginBtn;
    @BindView(R.id.actionBar)
    TitleBar actionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        actionBar.setTitle("登录");

    }

    @OnClick({R.id.loginBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginBtn:
                login();
                break;
        }
    }

    private void login() {
        final String userName = userNameEt.getText().toString();
        final String pwd = pwdEt.getText().toString();
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(pwd)) {
            RxToast.error("账号或密码不能为空");
        } else {
            AppService.createApi(MyApi.class).login(userName, pwd).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new MySubscriber<LoginResponse>() {
                @Override
                public void onStart() {
                    super.onStart();
                    showLoadingDialog();
                }

                @Override
                public void onError(Throwable error) {
                    super.onError(error);
                    dimissLoadingDialog();
                }

                @Override
                public void onCompleted() {
                    dimissLoadingDialog();
                }

                @Override
                public void onNext(final LoginResponse response) {
                    if (response.code == 200) {
                        RxToast.showToast("登录成功");
                        RxSPUtilTool.putString(MyApplication.getInstance(), "name", userName);
                        //                        RxSPUtilTool.putString(MyApplication.getInstance(), "pwd", pwd);
                        RxActivityTool.skipActivityAndFinish(LoginAcitivty.this, MainActivity.class);
                    } else if (!TextUtils.isEmpty(response.message)) {
                        RxToast.error(response.message);
                    } else {
                        RxToast.error("账户未注册或密码错误");
                    }
                }
            });
        }
    }
}
