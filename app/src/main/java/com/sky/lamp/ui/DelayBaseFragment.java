package com.sky.lamp.ui;

import com.sky.lamp.BaseActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

/**
 * Created by sky on 2015/4/21. 用于延迟加载网络中的数据，只有切换到本界面的时候才开始访问网络
 * 不适合第一个Fragment,如果是第一个的话，在初始化的时候就调用了showDelayData
 */
public abstract class DelayBaseFragment extends Fragment {
    private boolean hasFetchData;
    private boolean isViewPrepared;
    /**
     * 二次刷新
     */
    public boolean needRefresh = false;

    public void reset() {
        hasFetchData = false;
        needRefresh = false;
    }

    /**
     * 延迟进入该界面，所需加载的数据 比如query()
     */
    protected abstract void showDelayData();

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser || needRefresh) {
            lazyFetchDataIfPrepared();
            needRefresh = false;
        }
    }

    public BaseActivity getBaseActivity() {
        return (BaseActivity)getActivity();
    }

    /**
     * 懒加载的方式获取数据
     */
    private void lazyFetchDataIfPrepared() {
        if (getUserVisibleHint() && !hasFetchData && isViewPrepared) {
            //用户可见fragment && 没有加载过数据 && 视图已经准备完毕
            showDelayData();
            hasFetchData = true;
          /*  if (RestartUtils.hasWelcome) {
                showDelayData();
                hasFetchData = true;
            }*/
        }
    }

    View root;

   @Override
    public void onDestroyView() {
        Fragment parentFragment = getParentFragment();
        FragmentManager manager;
        if (parentFragment != null) {
            // If parent is another fragment, then this fragment is nested
            manager = parentFragment.getChildFragmentManager();
        } else {
            // This fragment is placed into activity
            manager = getActivity().getSupportFragmentManager();
        }
        if (!getActivity().isFinishing()) {
            manager.beginTransaction().remove(this).commitAllowingStateLoss();
        }

        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewPrepared = true;
        lazyFetchDataIfPrepared();
    }
}
