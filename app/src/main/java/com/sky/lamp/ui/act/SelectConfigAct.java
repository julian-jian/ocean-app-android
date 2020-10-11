package com.sky.lamp.ui.act;

import java.util.ArrayList;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.query.QueryBuilder;

import com.orhanobut.logger.Logger;
import com.sky.lamp.BaseActivity;
import com.sky.lamp.bean.ModelSelectBean;
import com.sky.lamp.MyApplication;
import com.sky.lamp.R;
import com.sky.lamp.bean.CommandLightMode;
import com.sky.lamp.bean.LightItemMode;
import com.sky.lamp.dao.CommandLightModeDao;
import com.sky.lamp.dao.DaoManager;
import com.sky.lamp.dao.LightItemModeDao;
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

    public static void startUI(Context context) {
        Intent intent = new Intent(context, SelectConfigAct.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_select_config);
        ButterKnife.bind(this);
        actionBar.setTitle("选择配置");
        actionBar.initLeftImageView(this);
        if (TextUtils.isEmpty(ModelSelectBean.t1)) {
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
                                CommandLightModeDao.Properties.T1.eq(ModelSelectBean.t1));
                if (where.count() == 0) {
                    // 初始化默认配置
                    initDefaultDb("LPS");
                    initDefaultDb("SPS");
                    initDefaultDb("LPS+SPS");
                } else {
                    for (CommandLightMode commandLightMode : where.list()) {
                        //TODO 为啥不能一起关联？
                        QueryBuilder<LightItemMode> where1 =
                                DaoManager.getInstance().getDaoSession()
                                        .queryBuilder(LightItemMode.class)
                                        .where(LightItemModeDao.Properties.Parent_id
                                                .eq(commandLightMode.id));
                        commandLightMode.mParameters = new ArrayList<>();
                        commandLightMode.mParameters.addAll(where1.list());
                        addDefaultView(commandLightMode);
                    }
                }
            }
        });
    }

    /**
     * LED -> LPS
     * @param t2Name
     */
    private void initDefaultDb(String t2Name) {
        final CommandLightMode commandLightMode = new CommandLightMode();
        commandLightMode.mUserID = MyApplication.getInstance().getUserId();
        commandLightMode.mDeviceID = ModelSelectBean.deviceId;
        commandLightMode.modelName = t2Name;
        commandLightMode.t1 = ModelSelectBean.t1;
        commandLightMode.mParameters = new ArrayList<>();
        DaoManager.getInstance().getDaoSession().insert(commandLightMode);

        LightItemMode itemMode = new LightItemMode();
        itemMode.setIndex(0);
        itemMode.parent_id = commandLightMode.id;
        DaoManager.getInstance().getDaoSession().insert(itemMode);
        commandLightMode.mParameters.add(itemMode);
        DaoManager.getInstance().getDaoSession().update(commandLightMode);
        addDefaultView(commandLightMode);
        // 如何自动关联？？
    }

    private void addDefaultView(final CommandLightMode lps) {
        // add view
        SelectConfigAct.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View inflate = LayoutInflater.from(SelectConfigAct.this)
                        .inflate(R.layout.select_config_item
                                , llDefaultConfigs);
                inflate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // go custom activity
                        Intent intent = new Intent(SelectConfigAct.this,ModeInfoActivity.class);
                        EventBus.getDefault().postSticky(lps);
                        startActivity(intent);

                    }
                });
                ((TextView) inflate.findViewById(R.id.textView)).setText(lps.modelName);
            }
        });
    }
}
