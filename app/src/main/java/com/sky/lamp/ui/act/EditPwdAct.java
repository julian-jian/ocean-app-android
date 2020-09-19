package com.sky.lamp.ui.act;

import java.util.HashMap;

import com.sky.lamp.BaseActivity;
import com.sky.lamp.Constants;
import com.sky.lamp.R;
import com.sky.lamp.http.AppService;
import com.sky.lamp.http.MyApi;
import com.sky.lamp.response.LoginResponse;
import com.sky.lamp.utils.HttpUtil;
import com.sky.lamp.utils.MySubscriber;
import com.sky.lamp.utils.RxSPUtilTool;
import com.sky.lamp.utils.TAStringUtils;
import com.sky.lamp.view.TitleBar;
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
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EditPwdAct extends BaseActivity {
    @BindView(R.id.actionBar)
    TitleBar actionBar;
    @BindView(R.id.et_old_pwd)
    EditText etOldPwd;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.iv_pwd_show)
    ImageView ivPwdShow;
    @BindView(R.id.et_confirm)
    EditText etConfirm;
    @BindView(R.id.iv_pwd_confirm_show)
    ImageView ivPwdConfirmShow;
    @BindView(R.id.btn_edit_pwd)
    Button btnEditPwd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd);
        ButterKnife.bind(this);
        actionBar.initLeftImageView(this);
        actionBar.setTitle("修改密码");
    }

    @OnClick({R.id.iv_pwd_show, R.id.btn_edit_pwd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_pwd_show:
                break;
            case R.id.btn_edit_pwd:
                checkAndSubmit();
                break;
        }
    }

    private void checkAndSubmit() {
        String email = RxSPUtilTool.getString(this, Constants.USERNAME);
        String pwd = etPwd.getText().toString();
        String confirmPwd = etConfirm.getText().toString();
        if (!TAStringUtils.isEmail(email)) {
            RxToast.showToastShort("邮箱格式不对");
            return;
        }

        if (TextUtils.isEmpty(pwd)) {
            RxToast.showToastShort("密码不能为空");
            return;
        }

        if (!pwd.equals(confirmPwd)) {
            RxToast.showToastShort("二次密码不一致");
            return;
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("loginName", email);
        map.put("loginPassword", etOldPwd.getText().toString());
        map.put("newLoginPassword", etPwd.getText().toString());
        String strEntity = HttpUtil.getRequestString(map);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        AppService.createApi(MyApi.class).editPwd(body).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(new MySubscriber<LoginResponse>() {
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
                if (response.status == Constants.SUCCESS) {
                    RxToast.showToast("修改密码成功");
                    RxSPUtilTool.putString(EditPwdAct.this,Constants.USER_ID,response.userID);
                }  else {
                    RxToast.error("操作失败");
                }
            }
        });
    }
}
