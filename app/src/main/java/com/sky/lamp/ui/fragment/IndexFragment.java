package com.sky.lamp.ui.fragment;

import com.sky.lamp.R;
import com.sky.lamp.adapter.ProductListAdapter;
import com.sky.lamp.ui.DelayBaseFragment;
import com.vondear.rxtools.view.RxToast;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class IndexFragment extends DelayBaseFragment {
    @BindView(R.id.ll_led)
    LinearLayout llLed;
    @BindView(R.id.ll_zaolang)
    LinearLayout llZaolang;
    @BindView(R.id.ll_shuibang)
    LinearLayout llShuibang;
    @BindView(R.id.ll_lps)
    LinearLayout llLps;
    @BindView(R.id.ll_sps)
    LinearLayout llSps;
    @BindView(R.id.ll_lps_sps)
    LinearLayout llLpsSps;
    Unbinder unbinder;
    private ProductListAdapter productListAdapter;

    @Override
    protected void showDelayData() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frament_index1, null);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.ll_led, R.id.ll_zaolang, R.id.ll_shuibang, R.id.ll_lps, R.id.ll_sps,
            R.id.ll_lps_sps})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_led:
                break;
            case R.id.ll_zaolang:
                RxToast.showToast("该功能正在开发");
            case R.id.ll_shuibang:
                RxToast.showToast("该功能正在开发");
                break;
            case R.id.ll_lps:
                break;
            case R.id.ll_sps:
                break;
            case R.id.ll_lps_sps:
                break;
        }
    }
}
