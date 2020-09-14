package com.sky.lamp;

import android.support.v4.app.Fragment;

/**
 * Created by sky on 2016/1/4
 */
public class BaseFragment extends Fragment {


    public boolean isActivityValid() {
        return isAdded()&& getActivity()!=null;
    }

  /*  public void dismissLoading() {
        if (getView() != null) {//防止销毁的时候空指针
            View view = getView().findViewById(R.id.loadingLayout);
            if (view != null) {
                view.setVisibility(View.GONE);
            }
        }
    }
    public void showLoading() {
        if (getView() != null) {//防止销毁的时候空指针
            View view = getView().findViewById(R.id.loadingLayout);
            if (view != null) {
                view.setVisibility(View.VISIBLE);
            }
        }
    }*/
    /**
     * 得到根Fragment
     *
     * @return
     */
    public Fragment getRootFragment() {
        Fragment fragment = getParentFragment();
        while (fragment.getParentFragment() != null) {
            fragment = fragment.getParentFragment();
        }
        return fragment;

    }
}
