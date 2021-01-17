package com.sky.lamp.ui.act;

import java.util.ArrayList;
import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.query.QueryBuilder;

import com.daimajia.swipe.SwipeLayout;
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
import com.vondear.rxtools.view.dialog.RxDialogEditSureCancel;

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

    private RxDialogEditSureCancel rxDialogLoading;

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
            addConfigView(initDefaultDb("LPS"), false);
            addConfigView(initDefaultDb("SPS"), false);
            addConfigView(initDefaultDb("LPS+SPS"), false);
        } else {
            for (CommandLightMode commandLightMode : where.list()) {
                commandLightMode.getMParameters();
                addConfigView(commandLightMode,commandLightMode.isCustom);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initConfig();
    }

    /**
     * LED -> LPS
     *
     * @param t2Name
     */
    private CommandLightMode initDefaultDb(String t2Name) {
        return initDefaultDb(t2Name, false);
    }

    private CommandLightMode initDefaultDb(String t2Name, boolean isCustom) {
        final CommandLightMode commandLightMode = new CommandLightMode();
        commandLightMode.mUserID = MyApplication.getInstance().getUserId();
        commandLightMode.mDeviceID = ModelSelectBean.deviceId;
        commandLightMode.modelName = t2Name;
        commandLightMode.t1 = ModelSelectBean.t1;
        commandLightMode.isCustom = isCustom;
        commandLightMode.mParameters = new ArrayList<>();
        DaoManager.getInstance().getDaoSession().insert(commandLightMode);
        for (int i = 0; i < 5; i++) {
            LightItemMode itemMode = new LightItemMode();
            itemMode.setIndex(i);
            itemMode.setStartTime("0" + (i + 5) + ":00");
            if (i == 4) {
                itemMode.setStopTime("10:00");
            }else{
                itemMode.setStopTime("0" + (i + 6) + ":00");
            }
            switch (t2Name) {
                case "LPS":
                    itemMode.setLight1Level(5);
                    itemMode.setLight2Level(80);
                    itemMode.setLight3Level(80);
                    itemMode.setLight4Level(30);
                    itemMode.setLight5Level(30);
                    itemMode.setLight6Level(10);
                    itemMode.setLight7Level(10);
                    break;
                case "SPS":
                    itemMode.setLight1Level(80);
                    itemMode.setLight2Level(80);
                    itemMode.setLight3Level(80);
                    itemMode.setLight4Level(80);
                    itemMode.setLight5Level(80);
                    itemMode.setLight6Level(20);
                    itemMode.setLight7Level(20);
                    break;
                case "LPS+SPS":
                    itemMode.setLight1Level(15);
                    itemMode.setLight2Level(80);
                    itemMode.setLight3Level(80);
                    itemMode.setLight4Level(30);
                    itemMode.setLight5Level(30);
                    itemMode.setLight6Level(10);
                    itemMode.setLight7Level(10);
                    break;
            }
            itemMode.parent_id = commandLightMode.id;
            DaoManager.getInstance().getDaoSession().insert(itemMode);
            commandLightMode.mParameters.add(itemMode);
        }
        DaoManager.getInstance().getDaoSession().update(commandLightMode);
        return commandLightMode;
    }

    private void addConfigView(final CommandLightMode commandLightMode, final boolean isCustom) {
        // add view
        SelectConfigAct.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View inflate = LayoutInflater.from(SelectConfigAct.this)
                        .inflate(R.layout.select_config_item
                                , llDefaultConfigs, false);
                SwipeLayout swipeRefreshLayout = inflate.findViewById(R.id.swipeLayout);

                TextView textView = ((TextView) inflate.findViewById(R.id.textView));
                textView.setText(commandLightMode.modelName);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SelectConfigAct.this, ModeInfoActivity.class);
                        EventBus.getDefault().postSticky(commandLightMode);
                        startActivity(intent);
                    }
                });
                if (isCustom) {
                    llCustomConfigs.addView(inflate);
                    TextView renameTv = inflate.findViewById(R.id.tv_1);
                    renameTv.setVisibility(View.VISIBLE);
                    renameTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showRenameDialog(commandLightMode);
                        }
                    });

                    TextView delTv = inflate.findViewById(R.id.tv_2);
                    delTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DaoManager.getInstance().getDaoSession()
                                    .getCommandLightModeDao().delete(commandLightMode);
                            initConfig();
                        }
                    });
                } else {
                    TextView renameTv = inflate.findViewById(R.id.tv_1);
                    swipeRefreshLayout.setSwipeEnabled(false);
                    llDefaultConfigs.addView(inflate);
                }

            }
        });
    }

    public void showRenameDialog(final CommandLightMode commandLightMode) {
        rxDialogLoading = new RxDialogEditSureCancel(this);
        rxDialogLoading.setTitle("重命名");
        rxDialogLoading.getCancelView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rxDialogLoading.dismiss();
            }
        });
        rxDialogLoading.getSureView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = rxDialogLoading.getEditText().getText().toString();
                if (!TextUtils.isEmpty(name)) {
                    commandLightMode.modelName = name;
                    DaoManager.getInstance().getDaoSession().getCommandLightModeDao()
                            .update(commandLightMode);
                    initConfig();
                    rxDialogLoading.dismiss();
                }
            }
        });
        rxDialogLoading.show();
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
                        initDefaultDb("自定义" + (where.list().size() + 1),true);
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
