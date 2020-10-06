package com.sky.lamp.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.google.gson.Gson;
import com.sky.lamp.BaseFragment;
import com.sky.lamp.R;
import com.sky.lamp.bean.CommandLightMode;
import com.sky.lamp.bean.LightItemMode;
import com.sky.lamp.bean.LightModelCache;
import com.sky.lamp.ui.act.ModeInfoActivity;
import com.sky.lamp.utils.RxSPUtilTool;
import com.sky.lamp.view.LightModeChartHelper;
import com.vondear.rxtools.RxImageTool;
import com.vondear.rxtools.view.RxToast;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.addapp.pickers.picker.TimePicker;

public class ModelInfoSettingFragment extends BaseFragment {
    Unbinder unbinder;
    @BindView(R.id.tv_startTime)
    TextView tvStartTime;
    @BindView(R.id.tv_endTime)
    TextView tvEndTime;
    @BindView(R.id.chart)
    LineChart lineChart;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.ll_seekbar)
    LinearLayout llSeekbar;
    @BindView(R.id.btn_del)
    ImageView btnDel;
    @BindView(R.id.btn_add)
    ImageView btnAdd;
    @BindView(R.id.btn_save)
    ImageView btnSave;
    @BindView(R.id.btn_send)
    Button btnSend;
    // 如果为空的，就是new
    private String modelName;
    public static final String KEY_SP_MODEL = "models";
    public static final float DEFAULT_BACKOFF_MULT = 1.0f;
    public static final int DEFAULT_PROGRESS = 50;

    private LightModeChartHelper mChartHelper;
    private LightModelCache lightModelCache;
    private CommandLightMode commandLightMode;
    private LightItemMode currentItemModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frament_modelinfo, null);
        unbinder = ButterKnife.bind(this, view);
        modelName = getActivity().getIntent().getStringExtra("model_name");
        this.mChartHelper = new LightModeChartHelper(lineChart, this.getActivity());
        readModelCache();
        initSeekbar();
        initModel();
        tvStartTime.setClickable(false);
        tvStartTime.setEnabled(false);
        return view;
    }

    private void readModelCache() {
        String jsonCache = RxSPUtilTool.readJSONCache(getActivity(), KEY_SP_MODEL);
        if (TextUtils.isEmpty(jsonCache)) {
            lightModelCache = new LightModelCache();
        } else {
            lightModelCache = new Gson().fromJson(jsonCache, LightModelCache.class);
        }
        commandLightMode = lightModelCache.map.get(modelName);
        if (commandLightMode == null) {
            commandLightMode = new CommandLightMode();
            commandLightMode.mParameters = new ArrayList<LightItemMode>();
            ((ModeInfoActivity) getActivity()).refreshTitle("自定义");
        } else {
            ((ModeInfoActivity) getActivity()).refreshTitle(modelName);
        }
        LightItemMode lightItemMode = new LightItemMode();
        lightItemMode.setIndex(1);
        lightItemMode.setModeName("模式" + lightItemMode.getIndex());
        lightItemMode.setLight1Level(DEFAULT_PROGRESS);
        lightItemMode.setLight2Level(DEFAULT_PROGRESS);
        lightItemMode.setLight3Level(DEFAULT_PROGRESS);
        lightItemMode.setLight4Level(DEFAULT_PROGRESS);
        lightItemMode.setLight5Level(DEFAULT_PROGRESS);
        lightItemMode.setLight6Level(DEFAULT_PROGRESS);
        lightItemMode.setLight7Level(DEFAULT_PROGRESS);
        lightItemMode.setStartTime("00:00");
        lightItemMode.setStopTime("01:00");
        commandLightMode.mParameters.add(lightItemMode);
    }

    private void initModel() {
        currentItemModel =
                commandLightMode.mParameters.get(commandLightMode.mParameters.size() - 1);
        refreshModeItems();
        tvStartTime.setText(commandLightMode.mParameters.get(0).getStartTime());
        tvEndTime.setText(commandLightMode.mParameters.get(0).getStopTime());
        ((RadioButton) radioGroup.findViewWithTag("rb0")).setChecked(true);
    }

    private void refreshModeItems() {
        radioGroup.removeAllViews();
        for (int index = 0; index < commandLightMode.mParameters.size(); index++) {
            LightItemMode lightItemMode = commandLightMode.mParameters.get(index);
            final RadioButton radioButton = new RadioButton(getActivity());
            radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            radioButton.setText(lightItemMode.getModeName());
            radioButton.setPadding(0, 5, 0, 5);
            radioButton.setGravity(17);
            radioButton.setTag("rb" + index);
            radioButton.setId(index);
            radioButton.setWidth(RxImageTool.dp2px(45));
            radioButton.setHeight(RxImageTool.dp2px(23));
            radioButton.setButtonDrawable(new ColorDrawable(0));
            final int finalIndex = index;
            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        radioButton.setTextColor(getResources().getColor(R.color.white));
                        saveCurrentModel();
                        LightItemMode selectItemModel =
                                commandLightMode.mParameters.get(finalIndex);
                        currentItemModel = selectItemModel;
                        refreshProgress();
                        refreshTime();
                        refreshChart();
                        refreshTime();
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
        }
    }

    private void refreshTime() {
        tvStartTime.setText(currentItemModel.getStartTime());
        tvEndTime.setText(currentItemModel.getStopTime());
    }

    private void initSeekbar() {
        for (int index = 0; index < 7; index++) {
            View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.item_seekbar, null);
            TextView leftTv;
            final TextView percentTv;
            final SeekBar seekbar;
            View rightTv;
            leftTv = inflate.findViewById(R.id.leftTv);
            percentTv = inflate.findViewById(R.id.percentTv);
            seekbar = inflate.findViewById(R.id.seekbar);
            seekbar.setTag("index" + (index));// TAG 0-6
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
            seekbar.setProgress(getProgress(index + 1, 0));
            final int finalI = index;
            seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    percentTv.setText(progress + "%");
                    refreshChart();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            llSeekbar.addView(inflate);
        }
        refreshChart();
    }

    private void refreshChart() {
        double total = (double) (
                ((((((((float) getProgress(0))
                              * 0.18f) + (
                        ((float) getProgress(1))
                                * 0.051f)) + (
                        ((float) getProgress(2))
                                * 0.3f)) + (
                        ((float) getProgress(3))
                                * 0.045f)) + (
                        ((float) getProgress(4))
                                * 0.048f)) + (
                        ((float) getProgress(5))
                                * 0.18f)) + (((float) getProgress(6))
                                                     * 0.054f));

        //                    if (total > 85.80000305175781d) {
        //                        System.out.println("CustomAct.onProgressChanged
        //                        "+total);
        //                        int count = 0;
        //                        return;
        //                    }
        List<Entry> valsCom1 = new ArrayList();
        valsCom1.add(new Entry(0.0f, 0.0f));
        valsCom1.add(new Entry(0.02f, ((float) getProgress(4)) * 0.04f));
        valsCom1.add(new Entry(0.07f, ((float) getProgress(3)) * 0.1f));
        valsCom1.add(new Entry(0.2f, ((float) getProgress(2)) * DEFAULT_BACKOFF_MULT));
        valsCom1.add(new Entry(0.3f, ((float) getProgress(1)) * 0.13f));
        valsCom1.add(new Entry(0.4f, ((float) getProgress(5)) * 0.16f));
        valsCom1.add(new Entry(0.7f, ((float) getProgress(6)) * 0.13f));
        valsCom1.add(new Entry(0.8f, 0.0f));
        mChartHelper.setData(valsCom1);
    }

    public int getProgress(int level, int modelIndex) {
        LightItemMode lightItemMode = commandLightMode.mParameters.get(modelIndex);
        int progress = 0;
        switch (level) {
            case 1:
                progress = lightItemMode.getLight1Level();
                break;
            case 2:
                progress = lightItemMode.getLight2Level();
                break;
            case 3:
                progress = lightItemMode.getLight3Level();
                break;
            case 4:
                progress = lightItemMode.getLight4Level();
                break;
            case 5:
                progress = lightItemMode.getLight5Level();
                break;
            case 6:
                progress = lightItemMode.getLight6Level();
                break;
            case 7:
                progress = lightItemMode.getLight7Level();
                break;
        }
        return progress;
    }

    public int getProgress(int index) {
        return ((ProgressBar) llSeekbar.findViewWithTag("index" + (index))).getProgress();
    }

    public void onTimePicker(final int id) {

        final TimePicker picker = new TimePicker(this.getActivity(), TimePicker.HOUR_24);
        picker.setRangeStart(00, 0);//09:00
        picker.setRangeEnd(23, 59);//18:30
        picker.setTopLineVisible(false);
        picker.setLineVisible(false);
        String time[] = id == R.id.tv_startTime ? tvStartTime.getText().toString().split(":") :
                tvEndTime.getText().toString().split(":");
        picker.setSelectedItem(Integer.valueOf(time[0]), Integer.valueOf(time[1]));
        picker.setOnTimePickListener(new TimePicker.OnTimePickListener() {
            @Override
            public void onTimePicked(String hour, String minute) {
                hour = String.format("%02d", Integer.valueOf(hour));
                minute = String.format("%02d", Integer.valueOf(minute));
                if (id == R.id.tv_endTime) {
                    String startTime[] = tvStartTime.getText().toString().split(":");
                    boolean error = false;
                    if (Integer.valueOf(startTime[0]) > Integer.valueOf(hour)) {
                        error = true;
                    } else if (Integer.valueOf(startTime[0]) == Integer.valueOf(hour)) {
                        if (Integer.valueOf(startTime[1]) > Integer.valueOf(minute)) {
                            error = true;
                        }
                    }
                    String setStopTime = hour + ":" + minute;
                    if (setStopTime.equals(tvStartTime.getText().toString())) {
                        error = true;
                    }
                    if (error) {
                        RxToast.showToast("时间设置错误");
                        return;
                    }
                }

                ((TextView) getView().findViewById(id)).setText(hour + ":" + minute);
            }
        });
        picker.show();
    }

    private void saveCurrentModel() {
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            return;
        }
        RadioButton selectBt = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
        String tag = (String) selectBt.getTag();
        int index = Integer.valueOf(tag.replace("rb", "").toString());

        LightItemMode lightItemMode = commandLightMode.mParameters.get(index);
        lightItemMode.setStartTime(tvStartTime.getText().toString());
        lightItemMode.setStopTime(tvEndTime.getText().toString());
        lightItemMode.setLight1Level(getProgress(0));
        lightItemMode.setLight2Level(getProgress(1));
        lightItemMode.setLight3Level(getProgress(2));
        lightItemMode.setLight4Level(getProgress(3));
        lightItemMode.setLight5Level(getProgress(4));
        lightItemMode.setLight6Level(getProgress(5));
        lightItemMode.setLight7Level(getProgress(6));
    }

    @OnClick({R.id.btn_del, R.id.btn_add, R.id.btn_save, R.id.btn_send, R.id.tv_startTime,
            R.id.tv_endTime})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_del:
                break;
            case R.id.btn_add:
                saveCurrentModel();
                addNewItemModel();
                break;
            case R.id.btn_save:
                saveClick();
                break;
            case R.id.btn_send:
                break;
            case R.id.tv_startTime:
                //                onTimePicker(R.id.tv_startTime);
                break;
            case R.id.tv_endTime:
                onTimePicker(R.id.tv_endTime);
                break;
        }
    }

    private void saveClick() {
        if (TextUtils.isEmpty(commandLightMode.modelName)) {
            for (int index = 1; ; index++) {
                CommandLightMode commandLightMode = lightModelCache.map.get("自定义" + index);
                if (commandLightMode == null) {
                    commandLightMode.modelName = "自定义" + index;
                    break;
                }
            }
        }
        lightModelCache.map.put(commandLightMode.modelName, commandLightMode);
        RxSPUtilTool.putJSONCache(getActivity(), KEY_SP_MODEL, new Gson().toJson(lightModelCache));
        RxToast.showToast("保存成功");
    }

    private void addNewItemModel() {
        LightItemMode lastItemModel =
                commandLightMode.mParameters.get(commandLightMode.mParameters.size() - 1);
        LightItemMode lightItemMode = new LightItemMode();
        lightItemMode.setIndex(lastItemModel.getIndex() + 1);
        lightItemMode.setModeName("模式" + lightItemMode.getIndex());
        lightItemMode.setLight1Level(DEFAULT_PROGRESS);
        lightItemMode.setLight2Level(DEFAULT_PROGRESS);
        lightItemMode.setLight3Level(DEFAULT_PROGRESS);
        lightItemMode.setLight4Level(DEFAULT_PROGRESS);
        lightItemMode.setLight5Level(DEFAULT_PROGRESS);
        lightItemMode.setLight6Level(DEFAULT_PROGRESS);
        lightItemMode.setLight7Level(DEFAULT_PROGRESS);
        lightItemMode.setStartTime(lastItemModel.getStopTime());
        lightItemMode.setStopTime("00:00");
        currentItemModel = lastItemModel;
        refreshProgress();
        commandLightMode.mParameters.add(lightItemMode);
        refreshModeItems();
        ((RadioButton) radioGroup.findViewWithTag("rb" + (commandLightMode.mParameters.size() - 1)))
                .setChecked(true);
    }

    private void refreshProgress() {
        for (int index = 0; index < 7; index++) {
            ProgressBar progressBar = (ProgressBar) llSeekbar.findViewWithTag("index" + (index));
            switch (index) {
                case 0:
                    progressBar.setProgressDrawable(
                            getResources().getDrawable(R.drawable.bg_process0));
                    progressBar.setProgress(currentItemModel.getLight1Level());
                    break;
                case 1:
                    progressBar.setProgressDrawable(
                            getResources().getDrawable(R.drawable.bg_process1));
                    progressBar.setProgress(currentItemModel.getLight2Level());
                    break;
                case 2:
                    progressBar.setProgressDrawable(
                            getResources().getDrawable(R.drawable.bg_process2));
                    progressBar.setProgress(currentItemModel.getLight3Level());
                    break;
                case 3:
                    progressBar.setProgressDrawable(
                            getResources().getDrawable(R.drawable.bg_process3));
                    progressBar.setProgress(currentItemModel.getLight4Level());
                    break;
                case 4:
                    progressBar.setProgressDrawable(
                            getResources().getDrawable(R.drawable.bg_process4));
                    progressBar.setProgress(currentItemModel.getLight5Level());
                    break;
                case 5:
                    progressBar.setProgressDrawable(
                            getResources().getDrawable(R.drawable.bg_process5));
                    progressBar.setProgress(currentItemModel.getLight6Level());
                    break;
                case 6:
                    progressBar.setProgressDrawable(
                            getResources().getDrawable(R.drawable.bg_process6));
                    progressBar.setProgress(currentItemModel.getLight7Level());
                    break;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
