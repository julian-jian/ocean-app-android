package com.sky.lamp.ui.act;

import com.sky.lamp.BaseActivity;
import com.sky.lamp.MainActivity;
import com.sky.lamp.MyApplication;
import com.sky.lamp.R;
import com.sky.lamp.http.AppService;
import com.sky.lamp.http.MyApi;
import com.sky.lamp.response.LoginResponse;
import com.sky.lamp.response.RegResponse;
import com.sky.lamp.ui.LoginAcitivty;
import com.sky.lamp.utils.MySubscriber;
import com.sky.lamp.utils.RxSPUtilTool;
import com.sky.lamp.utils.TAStringUtils;
import com.sky.lamp.view.TitleBar;
import com.vondear.rxtools.RxActivityTool;
import com.vondear.rxtools.RxTool;
import com.vondear.rxtools.view.RxToast;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RegActivity extends BaseActivity {
    @BindView(R.id.actionBar)
    TitleBar actionBar;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.iv_pwd_show)
    ImageView ivPwdShow;
    @BindView(R.id.et_confirm)
    EditText etConfirm;
    @BindView(R.id.iv_pwd_confirm_show)
    ImageView ivPwdConfirmShow;
    @BindView(R.id.btn_Reg)
    Button btnReg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        ButterKnife.bind(this);
        actionBar.setTitle("注册");
        actionBar.initLeftImageView(this);

    }

    @OnClick({R.id.iv_pwd_show, R.id.iv_pwd_confirm_show, R.id.btn_Reg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_pwd_show:
                break;
            case R.id.iv_pwd_confirm_show:
                checkAndSubmit();
                break;
            case R.id.btn_Reg:
                break;
        }
    }

    private void checkAndSubmit() {
        String email = etEmail.getText().toString();
        String pwd = etPwd.getText().toString();
        String confirmPwd = etConfirm.getText().toString();
        if (!TAStringUtils.isEmail(email)) {
            RxToast.showToastShort("邮箱格式不对");
            return;
        }
        if (!pwd.equals(confirmPwd)) {
            RxToast.showToastShort("二次密码不一致");
            return;
        }

        AppService.createApi(MyApi.class).reg(email, pwd).subscribeOn(Schedulers.io()).observeOn(
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
//                if (response.code == 200) {
//                    RxToast.showToast("登录成功");
//                    RxSPUtilTool.putString(MyApplication.getInstance(), "name", userName);
//                    //                        RxSPUtilTool.putString(MyApplication.getInstance(), "pwd", pwd);
//                    RxActivityTool.skipActivityAndFinish(LoginAcitivty.this, MainActivity.class);
//                } else if (!TextUtils.isEmpty(response.message)) {
//                    RxToast.error(response.message);
//                } else {
//                    RxToast.error("账户未注册或密码错误");
//                }
            }
        });
    }
}
