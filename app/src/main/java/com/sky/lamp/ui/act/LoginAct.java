package com.sky.lamp.ui.act;

import java.util.HashMap;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.sky.lamp.BaseActivity;
import com.sky.lamp.BuildConfig;
import com.sky.lamp.Constants;
import com.sky.lamp.MainActivity;
import com.sky.lamp.R;
import com.sky.lamp.event.RegSuccessEvent;
import com.sky.lamp.http.AppService;
import com.sky.lamp.http.MyApi;
import com.sky.lamp.response.LoginResponse;
import com.sky.lamp.utils.HttpUtil;
import com.sky.lamp.utils.MySubscriber;
import com.sky.lamp.utils.RxSPUtilTool;
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
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        setDefaultStatusColor();
        actionBar.setTitle("登录");
        actionBar.initLeftImageView(this);
        actionBar.setRightText("跳过 >>");
        actionBar.getRightTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginAct.this, MainActivity.class);
                intent.putExtra("skip",true);
                startActivity(intent);
                finish();
            }
        });
        if (BuildConfig.DEBUG) {
            etEmail.setText("1234@qq.com");
            etPwd.setText("123456");
        }
    }

    @OnClick({R.id.btn_login, R.id.tv_reg, R.id.tv_forgetPwd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                loginClick();
                break;
            case R.id.tv_reg:
                Intent intent = new Intent(this,RegActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_forgetPwd:
                finish();
                break;
        }
    }

    private void loginClick() {
        final String email = etEmail.getText().toString();
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
                AndroidSchedulers.mainThread()).subscribe(new MySubscriber<LoginResponse>() {
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
            public void onNext(final LoginResponse response) {
                if (response.status == 0) {
                    RxToast.showToast("登录成功");
                    EventBus.getDefault().postSticky(new LoginResponse());
                    RxSPUtilTool.putString(LoginAct.this, Constants.USERNAME,email);
                    RxSPUtilTool.putString(LoginAct.this,Constants.USER_ID,response.userID);
                    startActivity(new Intent(LoginAct.this, MainActivity.class));
                    finish();
                }  else {
                    RxToast.error("登录失败");
                }
            }
        });
    }
    @Subscribe
    public void onRegSuccess(RegSuccessEvent regSuccessEvent){
        finish();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
