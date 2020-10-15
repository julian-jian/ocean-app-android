package com.sky.lamp.ui.act;

import java.util.HashMap;

import com.sky.lamp.BaseActivity;
import com.sky.lamp.R;
import com.sky.lamp.http.AppService;
import com.sky.lamp.http.MyApi;
import com.sky.lamp.response.BaseResponse;
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
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FindPwdAct extends BaseActivity {
    @BindView(R.id.actionBar)
    TitleBar actionBar;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.btn_find_pwd)
    Button btnFindPwd;
    @BindView(R.id.tv_go_login)
    TextView tvGoLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_find_pwd);
        ButterKnife.bind(this);
    }

    public void requestFindPwd() {
        final String email = etEmail.getText().toString();
        if (!TAStringUtils.isEmail(email)) {
            RxToast.showToastShort("邮箱格式不对");
            return;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("loginName", email);
        String strEntity = HttpUtil.getRequestString(map);
        RequestBody body =
                RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), strEntity);
        AppService.createApi(MyApi.class).findPwd(body).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(new MySubscriber<BaseResponse>() {
            @Override
            public void onStart() {
                super.onStart();
                showLoadingDialog();
            }

            @Override
            public void onError(Throwable error) {
                super.onError(error);
                dismissLoadingDialog();
            }

            @Override
            public void onCompleted() {
                dismissLoadingDialog();
            }

            @Override
            public void onNext(final BaseResponse response) {
                if (response.status == 0) {
                    RxToast.showToast("发送成功");
                } else {
                    RxToast.error(response.result);
                }
            }
        });
    }

    @OnClick({R.id.btn_find_pwd, R.id.tv_go_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_find_pwd:
                requestFindPwd();
                break;
            case R.id.tv_go_login:
                startActivity(new Intent(FindPwdAct.this, LoginAct.class));
                finish();
                break;
        }
    }
}
