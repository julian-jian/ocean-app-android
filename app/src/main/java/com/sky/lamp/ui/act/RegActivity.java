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
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.RequestBody;
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
                break;
            case R.id.btn_Reg:
                checkAndSubmit();
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
        HashMap<String, Object> map = new HashMap<>();
        map.put("loginName", email);
        map.put("loginPassword", pwd);
        String strEntity = HttpUtil.getRequestString(map);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        AppService.createApi(MyApi.class).reg(body).subscribeOn(Schedulers.io()).observeOn(
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
                    RxToast.showToast("注册成功");
                    startActivity(new Intent(RegActivity.this,LoginAct.class));
                    finish();
                }  else {
                    String errorMsg = TextUtils.isEmpty(response.result) ? "注册失败" : response.result;
                    RxToast.error(errorMsg);
                }
            }
        });
    }

}
