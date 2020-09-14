package com.sky.lamp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.sky.lamp.MyApplication;
import com.sky.lamp.R;
import com.sky.lamp.utils.RxSPUtilTool;

/**
 * Created by zhangfy on 2018/8/1.
 */

public class MineFrament extends DelayBaseFragment {

    @BindView(R.id.logOutBtn)
    Button logOutBtn;
    @BindView(R.id.editPweRl)
    View editPweRll;
    @BindView(R.id.aboutRl)
    View aboutRl;
    @BindView(R.id.userNameTv)
    TextView userNameTv;
    Unbinder unbinder;

    @Override
    protected void showDelayData() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, null);
        unbinder = ButterKnife.bind(this, view);
        String name = RxSPUtilTool.getString(MyApplication.getInstance(), "name");
        if (TextUtils.isEmpty(name)) {
            name = "登录";
        }
        userNameTv.setText(name);
        logOutBtn = view.findViewById(R.id.logOutBtn);
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //CLEAR CACHE
                RxSPUtilTool.remove(MyApplication.getInstance(), "phone");
                getActivity().startActivity(new Intent(getActivity(), LoginAcitivty.class));
                getActivity().finish();
            }
        });
        editPweRll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(),EditPwdActivity.class));
            }
        });
        aboutRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(),AboutUsActivity.class));
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
