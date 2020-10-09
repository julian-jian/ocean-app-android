package com.sky.lamp.ui.act;

import static com.sky.lamp.ui.fragment.ModelInfoSettingFragment.KEY_SP_MODEL;

import java.io.Serializable;
import java.util.Set;

import com.google.gson.Gson;
import com.sky.lamp.BaseActivity;
import com.sky.lamp.R;
import com.sky.lamp.bean.CommandLightMode;
import com.sky.lamp.bean.LightItemMode;
import com.sky.lamp.bean.LightModelCache;
import com.sky.lamp.utils.RxSPUtilTool;
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
    private LightModelCache lightModelCache;

    public void startUI(Context context, String modelName) {
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
        String stringExtra = getIntent().getStringExtra(MODEL_NAME);
        if (TextUtils.isEmpty(stringExtra)) {
            finish();
            return;
        }
        String jsonCache = RxSPUtilTool.readJSONCache(this, KEY_SP_MODEL);
        lightModelCache = new Gson().fromJson(jsonCache, LightModelCache.class);
        Set<String> strings = lightModelCache.map.keySet();
        for (String key : strings) {

            if (stringExtra.equals(key.split("_")[0])) {
                if (key.split("_")[1].contains("custom")) { // 自定义
                    CommandLightMode commandLightMode = lightModelCache.map.get(key);
                    View inflate = LayoutInflater.from(SelectConfigAct.this)
                            .inflate(R.layout.select_config_item,
                                    llCustomConfigs);
                    TextView textView = inflate.findViewById(R.id.textView);
                    textView.setText(commandLightMode.modelName);
                    inflate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                } else { // 默认模式
                    CommandLightMode commandLightMode = lightModelCache.map.get(key);
                    View inflate = LayoutInflater.from(SelectConfigAct.this)
                            .inflate(R.layout.select_config_item,
                                    llDefaultConfigs);
                    TextView textView = inflate.findViewById(R.id.textView);
                    textView.setText(commandLightMode.modelName);
                    inflate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }

            }
        }
    }
}
