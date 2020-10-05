package com.sky.lamp.ui.act;

import java.util.ArrayList;
import java.util.List;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.google.gson.Gson;
import com.sky.lamp.BaseActivity;
import com.sky.lamp.R;
import com.sky.lamp.bean.CommandLightMode;
import com.sky.lamp.bean.LightMode;
import com.sky.lamp.bean.LightModeInfo;
import com.sky.lamp.utils.RxSPUtilTool;
import com.sky.lamp.view.LightModeChartHelper;
import com.sky.lamp.view.TitleBar;
import com.vondear.rxtools.RxImageTool;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.addapp.pickers.picker.TimePicker;

public class CustomAct extends BaseActivity {
    @BindView(R.id.actionBar)
    TitleBar actionBar;
    @BindView(R.id.tv_startTime)
    TextView tvStartTime;
    @BindView(R.id.tv_endTime)
    TextView tvEndTime;
    @BindView(R.id.ll)
    LinearLayout ll;
    @BindView(R.id.chart)
    LineChart lineChart;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;

    private ArrayList<LightModeInfo> mModeList = new ArrayList();
    public static final float DEFAULT_BACKOFF_MULT = 1.0f;

    private LightModeChartHelper mChartHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);
        ButterKnife.bind(this);
        actionBar.setTitle("自定义");
        actionBar.initLeftImageView(this);
        this.mChartHelper = new LightModeChartHelper(lineChart, this);
        initSeekbar();
        initModel();
    }

    private void initModel() {
        String custom_model = getIntent().getStringExtra("custom_model");
        CommandLightMode commandLightMode;
        if (TextUtils.isEmpty(custom_model)) { // 自定义
            commandLightMode = new CommandLightMode();
        } else { //LSP 加载默认配置
            String json = RxSPUtilTool.readJSONCache(this, custom_model);

            if (!TextUtils.isEmpty(json)) { //存在缓存
                commandLightMode = new Gson().fromJson(json, CommandLightMode.class);
            } else {
                commandLightMode = new CommandLightMode();
            }
            //TODO 初始化缓存数据
        }

        initFirstModelData(commandLightMode);
        int index = 1;
        for (LightMode lightMode : commandLightMode.getParameters()) {
            final RadioButton radioButton = new RadioButton(this);
            radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            radioButton.setText(lightMode.getModeName());
            radioButton.setPadding(0, 5, 0, 5);
            radioButton.setGravity(17);
            radioButton.setTag("rb" + index);
            radioButton.setWidth(RxImageTool.dp2px(45));
            radioButton.setHeight(RxImageTool.dp2px(23));
            radioButton.setButtonDrawable(new ColorDrawable(0));
            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        radioButton.setTextColor(getResources().getColor(R.color.white));
                    } else {
                        radioButton.setTextColor(getResources().getColor(R.color.text_black));
                    }
                }
            });
            radioButton.setBackgroundResource(R.drawable.small_button_selector);
            radioGroup.addView(radioButton);
            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) radioButton.getLayoutParams();
            layoutParams.setMargins(10, 0, 10, 0);
            radioButton.setLayoutParams(layoutParams);
            index++;
        }
        ((RadioButton) radioGroup.findViewWithTag("rb1")).setChecked(true);
    }

    private void initFirstModelData(CommandLightMode commandLightMode) {
        if (commandLightMode.getParameters() == null) {
            commandLightMode.setParameters(new ArrayList<LightMode>());
            LightMode first = new LightMode();
            first.setIndex(1);
            first.setLight1Level(50);
            first.setLight2Level(50);
            first.setLight3Level(50);
            first.setLight4Level(50);
            first.setLight5Level(50);
            first.setLight6Level(50);
            first.setLight7Level(50);
            first.setModeName("模式" + first.getIndex());
            first.setStartTime("00:00");
            first.setStopTime("01:00");
            commandLightMode.getParameters().add(first);
            for (int i = 1; i <= 7; i++) {
                ((SeekBar) ll.findViewWithTag("index" + i)).setProgress(50);
            }
            tvStartTime.setText(first.getStartTime());
            tvEndTime.setText(first.getStopTime());
        }

    }

    private void initSeekbar() {
        for (int i = 1; i <= 7; i++) {
            View inflate = LayoutInflater.from(this).inflate(R.layout.item_seekbar, null);
            TextView leftTv;
            final TextView percentTv;
            final SeekBar seekbar;
            View rightTv;
            leftTv = inflate.findViewById(R.id.leftTv);
            percentTv = inflate.findViewById(R.id.percentTv);
            seekbar = inflate.findViewById(R.id.seekbar);
            seekbar.setTag("index" + i);
            rightTv = inflate.findViewById(R.id.rightTv);
            leftTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("CustomAct.onClick");
                    int progress = seekbar.getProgress();
                    progress = progress - 10;
                    seekbar.setProgress(Math.max(progress, 0));
                }
            });
            rightTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int progress = seekbar.getProgress();
                    progress = progress + 10;
                    seekbar.setProgress(Math.min(progress, 100));
                }
            });
            final int finalI = i;
            seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    percentTv.setText(progress + "%");
                    LightModeInfo mode = new LightModeInfo();
                    mode.setLight1Level(getProgress(1));
                    mode.setLight2Level(getProgress(2));
                    mode.setLight3Level(getProgress(3));
                    mode.setLight4Level(getProgress(4));
                    mode.setLight5Level(getProgress(5));
                    mode.setLight6Level(getProgress(6));
                    mode.setLight7Level(getProgress(7));

                    double total = (double) (
                            ((((((((float) getProgress(1))
                                          * 0.18f) + (
                                    ((float) getProgress(2))
                                            * 0.051f)) + (
                                    ((float) getProgress(3))
                                            * 0.3f)) + (
                                    ((float) getProgress(4))
                                            * 0.045f)) + (
                                    ((float) getProgress(5))
                                            * 0.048f)) + (
                                    ((float) getProgress(6))
                                            * 0.18f)) + (((float) getProgress(7))
                                                                 * 0.054f));

                    //                    if (total > 85.80000305175781d) {
                    //                        System.out.println("CustomAct.onProgressChanged
                    //                        "+total);
                    //                        int count = 0;
                    //                        return;
                    //                    }
                    List<Entry> valsCom1 = new ArrayList();
                    valsCom1.add(new Entry(0.0f, 0.0f));
                    valsCom1.add(new Entry(0.02f, ((float) getProgress(5)) * 0.04f));
                    valsCom1.add(new Entry(0.07f, ((float) getProgress(4)) * 0.1f));
                    valsCom1.add(new Entry(0.2f, ((float) getProgress(3)) * DEFAULT_BACKOFF_MULT));
                    valsCom1.add(new Entry(0.3f, ((float) getProgress(2)) * 0.13f));
                    valsCom1.add(new Entry(0.4f, ((float) getProgress(6)) * 0.16f));
                    valsCom1.add(new Entry(0.7f, ((float) getProgress(7)) * 0.13f));
                    valsCom1.add(new Entry(0.8f, 0.0f));
                    mChartHelper.setData(valsCom1);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            ll.addView(inflate);

        }
    }

    public int getProgress(int index) {
        return ((ProgressBar) ll.findViewWithTag("index" + index)).getProgress();
    }

    public void onTimePicker() {
        TimePicker picker = new TimePicker(this, TimePicker.HOUR_24);
        picker.setRangeStart(00, 0);//09:00
        picker.setRangeEnd(23, 59);//18:30
        picker.setTopLineVisible(false);
        picker.setLineVisible(false);
        picker.setOnTimePickListener(new TimePicker.OnTimePickListener() {
            @Override
            public void onTimePicked(String hour, String minute) {
                //                ToastUtils.showShort(hour + ":" + minute);
            }
        });
        picker.show();
    }

    @OnClick({R.id.tv_startTime, R.id.tv_endTime})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_startTime:
                onTimePicker();
                break;
            case R.id.tv_endTime:
                break;
        }
    }
}
