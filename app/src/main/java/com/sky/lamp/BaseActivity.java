package com.sky.lamp;

import com.vondear.rxtools.view.dialog.RxDialogLoading;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by sky on 2017/12/17.
 */

public class BaseActivity extends AppCompatActivity {
    private RxDialogLoading rxDialogLoading;


    @Override
    protected void onDestroy() {
        dimissLoadingDialog();
        super.onDestroy();
    }

    public void showLoadingDialog(){
        if (rxDialogLoading == null) {
            rxDialogLoading = new RxDialogLoading(this);
            rxDialogLoading.show();
        }
    }

    public void dimissLoadingDialog() {
        if (rxDialogLoading != null && rxDialogLoading.isShowing()) {
            rxDialogLoading.dismiss();
            rxDialogLoading = null;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        if (Build.VERSION.SDK_INT >= 21) {
//            View decorView = getWindow().getDecorView();
//            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//            decorView.setSystemUiVisibility(option);
//            getWindow().setNavigationBarColor(Color.TRANSPARENT);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
//            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
//            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
//                //将侧边栏顶部延伸至status bar
//            }
//        }
    }
}
