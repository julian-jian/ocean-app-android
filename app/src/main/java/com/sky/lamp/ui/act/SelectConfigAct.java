package com.sky.lamp.ui.act;

import org.greenrobot.greendao.query.QueryBuilder;

import com.orhanobut.logger.Logger;
import com.sky.lamp.BaseActivity;
import com.sky.lamp.MyApplication;
import com.sky.lamp.R;
import com.sky.lamp.bean.CommandLightMode;
import com.sky.lamp.bean.LightItemMode;
import com.sky.lamp.dao.CommandLightModeDao;
import com.sky.lamp.dao.DaoManager;
import com.sky.lamp.view.TitleBar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectConfigAct extends BaseActivity {
    @BindView(R.id.actionBar)
    TitleBar actionBar;
    @BindView(R.id.ll_default_configs)
    LinearLayout llDefaultConfigs;
    @BindView(R.id.ll_custom_configs)
    LinearLayout llCustomConfigs;
    public static final String MODEL_NAME = "modelName";
    String mModelName;

    public static void startUI(Context context, String modelName) {
        Intent intent = new Intent(context, SelectConfigAct.class);
        intent.putExtra(MODEL_NAME, modelName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_select_config);
        ButterKnife.bind(this);
        actionBar.setTitle("选择配置");
        actionBar.initLeftImageView(this);
        mModelName = getIntent().getStringExtra(MODEL_NAME);
        if (TextUtils.isEmpty(mModelName)) {
            finish();
            return;
        }
        initConfig();
    }

    private void initConfig() {
        if (!MyApplication.getInstance().isLogin()) {
            Logger.d("not login");
            return;
        }
        DaoManager.getInstance().getDaoSession().runInTx(new Runnable() {
            @Override
            public void run() {
                QueryBuilder<CommandLightMode> commandLightModeQueryBuilder =
                        DaoManager.getInstance().getDaoSession()
                                .queryBuilder(CommandLightMode.class);
                QueryBuilder<CommandLightMode> where =
                        commandLightModeQueryBuilder.where(CommandLightModeDao.Properties.MUserID
                                        .eq(MyApplication.getInstance().getUserId()),
                                CommandLightModeDao.Properties.ModelName.eq(mModelName));
                if (where.count() == 0) {
                    // 初始化默认配置
                    final CommandLightMode lps = new CommandLightMode();
                    lps.mUserID = MyApplication.getInstance().getUserId();
                    lps.modelName = "LPS";
                    DaoManager.getInstance().getDaoSession().insert(lps);

                    LightItemMode itemMode = new LightItemMode();
                    itemMode.setIndex(0);
                    itemMode.parent_id = lps.id;
                    DaoManager.getInstance().getDaoSession().insert(itemMode);
                    // add view
                    SelectConfigAct.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            View inflate = LayoutInflater.from(SelectConfigAct.this)
                                    .inflate(R.layout.select_config_item
                                            , llDefaultConfigs);
                            ((TextView)inflate.findViewById(R.id.textView)).setText(lps.modelName);
                        }
                    });
                }
            }
        });
    }
}
