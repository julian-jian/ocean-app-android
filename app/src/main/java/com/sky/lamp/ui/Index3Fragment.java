package com.sky.lamp.ui;

import static com.sky.lamp.Constants.USERNAME;
import static com.sky.lamp.Constants.USER_ID;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.sky.lamp.MyApplication;
import com.sky.lamp.R;
import com.sky.lamp.event.LoginOutEvent;
import com.sky.lamp.response.LoginResponse;
import com.sky.lamp.ui.act.EditPwdAct;
import com.sky.lamp.ui.act.LoginAct;
import com.sky.lamp.utils.RxSPUtilTool;
import com.vondear.rxtools.view.RxToast;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class Index3Fragment extends DelayBaseFragment {
    @BindView(R.id.touxiang)
    ImageView touxiang;
    @BindView(R.id.tv_account)
    TextView tvAccount;
    @BindView(R.id.edit_pwd)
    LinearLayout editPwd;
    @BindView(R.id.login_out)
    LinearLayout loginOut;
    Unbinder unbinder;

    @Override
    protected void showDelayData() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frament_index3, null);
        unbinder = ButterKnife.bind(this, view);
        refreshText();
        EventBus.getDefault().register(this);
        return view;
    }

    private void refreshText() {
        String email = RxSPUtilTool.getString(getActivity(), USERNAME);
        String desr = TextUtils.isEmpty(email) ? "未登录" : "账号: " + email;
        tvAccount.setText(desr);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }
    @Subscribe
    public void onLoginSuccess(LoginResponse loginResponse) {
        refreshText();
    }

    @OnClick({R.id.edit_pwd, R.id.login_out,R.id.touxiang})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.edit_pwd:
                if (MyApplication.getInstance().isLogin()) {
                    startActivity(new Intent(getActivity(), EditPwdAct.class));
                } else {
                    RxToast.showToast("请先登录");
                }
                break;
            case R.id.login_out:
                if (MyApplication.getInstance().isLogin()) {
                    RxSPUtilTool.remove(getActivity(), USERNAME);
                    RxSPUtilTool.remove(getActivity(), USER_ID);
                    EventBus.getDefault().postSticky(new LoginOutEvent());
                } else {
                    RxToast.showToast("请先登录");
                }
                break;
            case R.id.touxiang:
                if (!MyApplication.getInstance().isLogin()) {
                    Intent intent = new Intent(getActivity(),LoginAct.class);
                    startActivity(intent);
                }
                break;
        }
    }
}
