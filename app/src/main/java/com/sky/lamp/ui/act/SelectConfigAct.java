package com.sky.lamp.ui.act;

import java.util.ArrayList;
import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.query.QueryBuilder;

import com.orhanobut.logger.Logger;
import com.sky.lamp.BaseActivity;
import com.sky.lamp.MyApplication;
import com.sky.lamp.R;
import com.sky.lamp.bean.CommandLightMode;
import com.sky.lamp.bean.LightItemMode;
import com.sky.lamp.bean.ModelSelectBean;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectConfigAct extends BaseActivity {
    @BindView(R.id.actionBar)
    TitleBar actionBar;
    @BindView(R.id.ll_default_configs)
    LinearLayout llDefaultConfigs;
    @BindView(R.id.ll_custom_configs)
    LinearLayout llCustomConfigs;

    public static final String MODEL_NAME = "modelName";
    @BindView(R.id.btn_add_config)
    Button btnAddConfig;
    @BindView(R.id.btn_reset)
    Button btnReset;

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
        llDefaultConfigs.removeAllViews();
        llCustomConfigs.removeAllViews();
        QueryBuilder<CommandLightMode> commandLightModeQueryBuilder =
                DaoManager.getInstance().getDaoSession()
                        .queryBuilder(CommandLightMode.class);
        QueryBuilder<CommandLightMode> where =
                commandLightModeQueryBuilder.where(CommandLightModeDao.Properties.MUserID
                                .eq(MyApplication.getInstance().getUserId()),
                        CommandLightModeDao.Properties.T1.eq(ModelSelectBean.t1));
        if (where.count() == 0) {
            // 初始化默认配置
            addDefaultView(initDefaultDb("LPS"), false);
            addDefaultView(initDefaultDb("SPS"), false);
            addDefaultView(initDefaultDb("LPS+SPS"), false);
        } else {
            for (CommandLightMode commandLightMode :where.list()) {
                commandLightMode.getMParameters();
                addDefaultView(commandLightMode,
                        commandLightMode.isCustom);
            }
        }
    }

    /**
     * LED -> LPS
     *
     * @param t2Name
     */
    private CommandLightMode initDefaultDb(String t2Name) {
        return initDefaultDb(t2Name,false);
    }

    private CommandLightMode initDefaultDb(String t2Name,boolean isCustom) {
        final CommandLightMode commandLightMode = new CommandLightMode();
        commandLightMode.mUserID = MyApplication.getInstance().getUserId();
        commandLightMode.mDeviceID = ModelSelectBean.deviceId;
        commandLightMode.modelName = t2Name;
        commandLightMode.t1 = ModelSelectBean.t1;
        commandLightMode.isCustom = isCustom;
        commandLightMode.mParameters = new ArrayList<>();
        DaoManager.getInstance().getDaoSession().insert(commandLightMode);

        LightItemMode itemMode = new LightItemMode();
        itemMode.setIndex(0);
        itemMode.parent_id = commandLightMode.id;
        DaoManager.getInstance().getDaoSession().insert(itemMode);
        commandLightMode.mParameters.add(itemMode);
        DaoManager.getInstance().getDaoSession().update(commandLightMode);
        return commandLightMode;
    }

    private void addDefaultView(final CommandLightMode commandLightMode, final boolean isCustom) {
        // add view
        SelectConfigAct.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View inflate = LayoutInflater.from(SelectConfigAct.this)
                        .inflate(R.layout.select_config_item
                                , llDefaultConfigs, false);
                inflate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // go custom activity
                        Intent intent = new Intent(SelectConfigAct.this, ModeInfoActivity.class);
                        EventBus.getDefault().postSticky(commandLightMode);
                        startActivity(intent);

                    }
                });
                ((TextView) inflate.findViewById(R.id.textView))
                        .setText(commandLightMode.modelName);
                if (isCustom) {
                    llCustomConfigs.addView(inflate);
                } else {
                    llDefaultConfigs.addView(inflate);
                }

            }
        });
    }

    @OnClick({R.id.btn_add_config, R.id.btn_reset})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_add_config:
                Intent intent = new Intent(SelectConfigAct.this, ModeInfoActivity.class);
                QueryBuilder<CommandLightMode> where =
                        DaoManager.getInstance().getDaoSession().getCommandLightModeDao()
                                .queryBuilder()
                                .where(CommandLightModeDao.Properties.T1.eq(ModelSelectBean.t1),
                                        CommandLightModeDao.Properties.ModelName.like("自定义%"));
                CommandLightMode commandLightMode =
                        initDefaultDb("自定义" + (where.list().size() + 1));
                addDefaultView(commandLightMode, true);
                EventBus.getDefault().postSticky(commandLightMode);
                startActivity(intent);
                break;
            case R.id.btn_reset:
                QueryBuilder<CommandLightMode> where1 =
                        DaoManager.getInstance().getDaoSession().getCommandLightModeDao()
                                .queryBuilder()
                                .where(CommandLightModeDao.Properties.T1.eq(ModelSelectBean.t1));
                List<CommandLightMode> list = where1.list();
                for (CommandLightMode lightMode : list) {
                    QueryBuilder<LightItemMode> light =
                            DaoManager.getInstance().getDaoSession().getLightItemModeDao()
                                    .queryBuilder()
                                    .where(
                                            LightItemModeDao.Properties.Parent_id.eq(lightMode.id));
                    DaoManager.getInstance().getDaoSession().getLightItemModeDao()
                            .deleteInTx(light.list());
                }
                DaoManager.getInstance().getDaoSession().getCommandLightModeDao().deleteInTx(list);
                initConfig();
                break;
        }
    }
}
