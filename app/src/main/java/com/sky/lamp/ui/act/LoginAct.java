package com.sky.lamp.ui.act;

import java.util.HashMap;

import com.sky.lamp.BaseActivity;
import com.sky.lamp.R;
import com.sky.lamp.http.AppService;
import com.sky.lamp.http.MyApi;
import com.sky.lamp.response.RegResponse;
import com.sky.lamp.utils.HttpUtil;
import com.sky.lamp.utils.MySubscriber;
import com.sky.lamp.utils.TAStringUtils;
import com.sky.lamp.view.TitleBar;
import com.vondear.rxtools.view.RxToast;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginAct extends BaseActivity {
    @BindView(R.id.actionBar)
    TitleBar actionBar;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.iv_pwd_show)
    ImageView ivPwdShow;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_reg)
    TextView tvReg;
    @BindView(R.id.tv_forgetPwd)
    TextView tvForgetPwd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.iv_pwd_show, R.id.btn_login, R.id.tv_reg, R.id.tv_forgetPwd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_pwd_show:
                break;
            case R.id.btn_login:
                loginClick();
                break;
            case R.id.tv_reg:
                Intent intent = new Intent(this,RegActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_forgetPwd:
                break;
        }
    }

    private void loginClick() {
        String email = etEmail.getText().toString();
        String pwd = etPwd.getText().toString();
        if (!TAStringUtils.isEmail(email)) {
            RxToast.showToastShort("邮箱格式不对");
            return;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("loginName", email);
        map.put("loginPassword", pwd);
        String strEntity = HttpUtil.getRequestString(map);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        AppService.createApi(MyApi.class).login(body).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(new MySubscriber<RegResponse>() {
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
            public void onNext(final RegResponse response) {
                if (response.status == 0) {
                    RxToast.showToast("登录成功");
                }  else {
                    RxToast.error("登录失败");
                }
            }
        });
    }
}
