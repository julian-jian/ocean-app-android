package com.sky.lamp.ui;

import android.os.Bundle;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.sky.lamp.BaseActivity;
import com.sky.lamp.MyApplication;
import com.sky.lamp.R;
import com.sky.lamp.http.AppService;
import com.sky.lamp.http.MyApi;
import com.sky.lamp.response.UpdatePwdResponse;
import com.sky.lamp.utils.MySubscriber;
import com.sky.lamp.utils.RxSPUtilTool;
import com.sky.lamp.view.PasswordToggleEditText;
import com.sky.lamp.view.TitleBar;
import com.vondear.rxtools.view.RxToast;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EditPwdActivity extends BaseActivity {

    @BindView(R.id.actionBar)
    TitleBar actionBar;
    @BindView(R.id.oldPwdEt)
    PasswordToggleEditText oldPwdEt;
    @BindView(R.id.pwdEt)
    PasswordToggleEditText pwdEt;
    @BindView(R.id.pwdConfirmEt)
    PasswordToggleEditText pwdConfirmEt;
    @BindView(R.id.doneBtn)
    Button doneBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_edit_pwd);
        ButterKnife.bind(this);
        actionBar.initLeftImageView(this);
        actionBar.setTitle("修改密码");
    }

    @OnClick(R.id.doneBtn)
    public void onViewClicked() {
        String old = oldPwdEt.getText().toString();
        String newPwd = pwdEt.getText().toString();
        String pwdConfirmPwd = pwdConfirmEt.getText().toString();
        if (!newPwd.equals(pwdConfirmPwd)) {
            RxToast.showToast("二次密码不同");
            return;
        }
        AppService.createApi(MyApi.class).updatePassword(RxSPUtilTool.getString(MyApplication.getInstance(),"phone"),old,newPwd).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new MySubscriber<UpdatePwdResponse>() {
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
            public void onNext(final UpdatePwdResponse response) {
                if (response.code == 200) {
                    RxToast.showToast("密码更新成功");
                    finish();
                }else{
                    RxToast.showToast(response.message);
                }
            }
        });
    }

}
