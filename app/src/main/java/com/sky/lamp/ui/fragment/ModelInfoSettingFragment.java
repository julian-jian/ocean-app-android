package com.sky.lamp.ui.fragment;

import static com.sky.lamp.utils.HexUtils.tenToHexByte;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.orhanobut.logger.Logger;
import com.sky.lamp.BaseActivity;
import com.sky.lamp.BaseFragment;
import com.sky.lamp.R;
import com.sky.lamp.SocketManager;
import com.sky.lamp.bean.CommandLightMode;
import com.sky.lamp.bean.LightItemMode;
import com.sky.lamp.dao.DaoManager;
import com.sky.lamp.dao.LightItemModeDao;
import com.sky.lamp.ui.act.ModeInfoActivity;
import com.sky.lamp.utils.HexUtils;
import com.sky.lamp.view.LightModeChartHelper;
import com.vondear.rxtools.RxImageTool;
import com.vondear.rxtools.view.RxToast;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import app.socketlib.com.library.ContentServiceHelper;
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
    public static final float DEFAULT_BACKOFF_MULT = 1.0f;
    public static final int DEFAULT_PROGRESS = 50;

    private LightModeChartHelper mChartHelper;
    private CommandLightMode mCommandLightMode;
    private int mIndex;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frament_modelinfo, null);
        unbinder = ButterKnife.bind(this, view);
        this.mChartHelper = new LightModeChartHelper(lineChart, this.getActivity());
        readModelCache();
        initSeekbar();
        initModel();
        tvStartTime.setClickable(false);
        tvStartTime.setEnabled(false);
        return view;
    }

    private void readModelCache() {
        mCommandLightMode = ((ModeInfoActivity) getActivity()).mCommandLightMode;
        List<LightItemMode> mParameters = mCommandLightMode.getMParameters();
        System.out.println("ModelInfoSettingFragment.readModelCache");
    }

    private void initModel() {
        tvStartTime.setText(mCommandLightMode.mParameters.get(0).getStartTime());
        tvEndTime.setText(mCommandLightMode.mParameters.get(0).getStopTime());
        refreshModeItems();
    }

    private void refreshModeItems() {
        radioGroup.removeAllViews();
        for (int index = 0; index < mCommandLightMode.mParameters.size(); index++) {
            LightItemMode lightItemMode = mCommandLightMode.mParameters.get(index);
            final RadioButton radioButton = new RadioButton(getActivity());
            radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            radioButton.setText("模式" + (lightItemMode.getIndex() + 1));
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
                        mIndex = finalIndex;
                        initSeekbar();
                        refreshTime();
                        refreshChart();
                        System.out.println("ModelInfoSettingFragment.onCheckedChanged "+mIndex);
                    } else {
                        System.out.println("ModelInfoSettingFragment.onCheckedChanged "+isChecked);
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
            // 最后一项选中
            if (index == mCommandLightMode.mParameters.size() - 1) {
                radioButton.setChecked(true);
            }
        }
    }

    private void refreshTime() {
        tvStartTime.setText(mCommandLightMode.getMParameters().get(mIndex).getStartTime());
        tvEndTime.setText(mCommandLightMode.getMParameters().get(mIndex).getStopTime());
    }

    private int getSeekbarLayoutId(int index) {
        int id = 0;
        switch (index) {
            case 0:
                id = R.layout.item_seekbar_sub0;
                break;
            case 1:
                id = R.layout.item_seekbarsub_1;
                break;
            case 2:
                id = R.layout.item_seekbarsub_2;
                break;
            case 3:
                id = R.layout.item_seekbarsub_3;
                break;
            case 4:
                id = R.layout.item_seekbarsub_4;
                break;
            case 5:
                id = R.layout.item_seekbarsub_5;
                break;
            case 6:
                id = R.layout.item_seekbarsub_6;
                break;
        }
        return id;
    }

    private void initSeekbar() {
        llSeekbar.removeAllViews();
        for (int index = 0; index < 7; index++) {
            View inflate =
                    LayoutInflater.from(getActivity()).inflate(R.layout.item_seekbar, null);
            View inflate1 =
                    LayoutInflater.from(getActivity()).inflate(getSeekbarLayoutId(index), null);
            ((ViewGroup) inflate.findViewById(R.id.fl_sub_seebar)).addView(inflate1);
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
            seekbar.setProgress(getProgress(index, mIndex));
            percentTv.setText(seekbar.getProgress() + "%");
            final int finalI = index;
            seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    percentTv.setText(progress + "%");
                    refreshChart();
                    saveCurrentModel();
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
        float zoom = 1.3f;
        valsCom1.add(new Entry(0.02f, ((float) getProgress(4)) * 0.04f * zoom));
        valsCom1.add(new Entry(0.07f, ((float) getProgress(3)) * 0.1f * zoom));
        valsCom1.add(new Entry(0.2f, ((float) getProgress(2)) * DEFAULT_BACKOFF_MULT * zoom));
        valsCom1.add(new Entry(0.3f, ((float) getProgress(1)) * 0.13f * zoom) );
        valsCom1.add(new Entry(0.4f, ((float) getProgress(5)) * 0.16f * zoom));
        valsCom1.add(new Entry(0.7f, ((float) getProgress(6)) * 0.13f * zoom));
        valsCom1.add(new Entry(0.8f, 0.0f));
        mChartHelper.setData(valsCom1);
    }

    public int getProgress(int level, int modelIndex) {
        LightItemMode lightItemMode = mCommandLightMode.mParameters.get(modelIndex);
        int progress = 0;
        switch (level) {
            case 0:
                progress = lightItemMode.getLight1Level();
                break;
            case 1:
                progress = lightItemMode.getLight2Level();
                break;
            case 2:
                progress = lightItemMode.getLight3Level();
                break;
            case 3:
                progress = lightItemMode.getLight4Level();
                break;
            case 4:
                progress = lightItemMode.getLight5Level();
                break;
            case 5:
                progress = lightItemMode.getLight6Level();
                break;
            case 6:
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
                    } else {
                        int gap = Integer.valueOf(hour) * 60 + Integer.valueOf(minute) - Integer
                                .valueOf(startTime[0]) - Integer.valueOf(startTime[1]);
                        if (gap < 60) {
                            RxToast.showToast("时间设置错误,间隔不能低于1小时");
                            return;
                        }
                    }

                    String setStopTime = hour + ":" + minute;
                    if (setStopTime.equals(tvStartTime.getText().toString())) {
                        error = true;
                    }
                    if (error) {
                        RxToast.showToast("时间设置错误");
                        return;
                    } else {
                        mCommandLightMode.getMParameters().get(mIndex).setStopTime(setStopTime);
                        ((TextView) getView().findViewById(id)).setText(hour + ":" + minute);
                    }
                }
            }
        });
        picker.show();
        // 坑爹不会默认滚动过去
//        picker.setSelectedItem(Integer.valueOf(time[0]), Integer.valueOf(time[1]));
    }

    private void saveCurrentModel() {
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            return;
        }
        System.out.println("ModelInfoSettingFragment.saveCurrentModel");
        LightItemMode lightItemMode = mCommandLightMode.getMParameters().get(mIndex);
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
                delItemModel();
                break;
            case R.id.btn_add:
                if (mCommandLightMode.mParameters.size() >= 8) {
                    RxToast.showToast("模式个数已是最大");
                    return;
                }
                saveCurrentModel();
                addNewItemModel();
                break;
            case R.id.btn_save:
                saveCurrentModel();
                saveClick();
                break;
            case R.id.btn_send:
                if (!SocketManager.getInstance().isConnect()) {
                    RxToast.showToast("未建立连接成功，请先确认连接成功");

                } else {
                    btnSend.setEnabled(false);
                    sendAllModeCommand();
                }
                break;
            case R.id.tv_startTime: // 不允许设置开始时间
                break;
            case R.id.tv_endTime:
                onTimePicker(R.id.tv_endTime);
                break;
        }
    }

    private void sendAllModeCommand() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                saveCurrentModel();
                saveClick();
                showSendCommandLoading();
                for (int i = 0; i < 8; i++) {
                    if (i > mCommandLightMode.mParameters.size() - 1) {
                        sendEmptyMode(i);
                    } else {
                        sendUserModel(i);
                    }
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoading();
                    }
                });
            }
        }).start();
    }

    private void dismissLoading() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((BaseActivity) ModelInfoSettingFragment.this.getActivity())
                        .dismissLoadingDialog();
            }
        });
    }

    private void showSendCommandLoading() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((BaseActivity) ModelInfoSettingFragment.this.getActivity())
                        .showLoadingDialog("发送中...");
            }
        });
    }

    private void sendEmptyMode(int i) {
        LightItemMode lightItemMode = new LightItemMode();
        lightItemMode.startTime = "00:00";
        lightItemMode.stopTime = "00:00";
        lightItemMode.setLight1Level(0);
        lightItemMode.setLight2Level(0);
        lightItemMode.setLight3Level(0);
        lightItemMode.setLight4Level(0);
        lightItemMode.setLight5Level(0);
        lightItemMode.setLight6Level(0);
        lightItemMode.setLight7Level(0);
        sendCommand(i, lightItemMode);
        if (i == 6) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RxToast.showToast("设置完毕");
                    btnSend.setEnabled(true);
                }
            });
        } else {
            final int finalI1 = i;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RxToast.showToast("模式" + (finalI1 + 1) + "发送成功");
                }
            });
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendUserModel(int i) {
        LightItemMode lightItemMode =
                mCommandLightMode.mParameters.get(i);
        sendCommand(i, lightItemMode);
        final int finalI = i;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RxToast.showToast("模式" + (finalI + 1) + "发送成功");
            }
        });
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendCommand(int index, LightItemMode lightItemMode) {
        byte[] temp = new byte[] {
                (byte) 0xaa, (byte) 0x0a, (byte) 0x00, (byte) 0x00,
                (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00,
                (byte) 0x55};
        temp[1] = tenToHexByte(index + 1);
        temp[2] = tenToHexByte(lightItemMode.getStartHour());
        temp[3] = tenToHexByte(lightItemMode.getStartMinute());
        temp[4] = tenToHexByte(lightItemMode.getStopHour());
        temp[5] = tenToHexByte(lightItemMode.getStopMinute());
        temp[6] = tenToHexByte(lightItemMode.getLight1Level());
        temp[7] = tenToHexByte(lightItemMode.getLight2Level());
        temp[8] = tenToHexByte(lightItemMode.getLight3Level());
        temp[9] = tenToHexByte(lightItemMode.getLight4Level());
        temp[10] = tenToHexByte(lightItemMode.getLight5Level());
        temp[11] = tenToHexByte(lightItemMode.getLight6Level());
        temp[12] = tenToHexByte(lightItemMode.getLight7Level());
        temp[13] = tenToHexByte(0);
        temp[14] = HexUtils.getVerifyCode(temp);
        Logger.d("sendCommand success index = " + index + " " + HexUtils
                .bytes2Hex(temp));
        ContentServiceHelper.sendClientMsg(temp);
    }

    private void delItemModel() {
        if (mCommandLightMode.mParameters.size() < 2) {
            return;
        }
        mCommandLightMode.mParameters.remove(mCommandLightMode.mParameters.size() - 1);
        refreshModeItems();
        refreshProgress();
        refreshTime();
    }

    private void saveClick() {
        if (mCommandLightMode.getId() == 0) {
            DaoManager.getInstance().getDaoSession().getCommandLightModeDao()
                    .insert(mCommandLightMode);
        } else {
            DaoManager.getInstance().getDaoSession().getCommandLightModeDao()
                    .update(mCommandLightMode);
        }
        if (mCommandLightMode.getId() == null) {
            DaoManager.getInstance().getDaoSession().getCommandLightModeDao()
                    .insert(mCommandLightMode);
        }
        List<LightItemMode> parameters = mCommandLightMode.mParameters == null ?
                new ArrayList<LightItemMode>() : mCommandLightMode.mParameters;
        LightItemModeDao targetDao = DaoManager.getInstance().getDaoSession().getLightItemModeDao();
        List<LightItemMode> mParametersNew = targetDao
                ._queryCommandLightMode_MParameters(mCommandLightMode.id);
        // 删除老数据先
        DaoManager.getInstance().getDaoSession().getLightItemModeDao().deleteInTx(mParametersNew);
        mCommandLightMode.mParameters = parameters;
        // 保存新数据
        for (LightItemMode lightItemMode : mCommandLightMode.mParameters) {
            lightItemMode.setId(null);
            lightItemMode.setParent_id(mCommandLightMode.getId());
            DaoManager.getInstance().getDaoSession().getLightItemModeDao().insert(lightItemMode);
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RxToast.showToast("保存成功");
            }
        });

    }

    private void addNewItemModel() {
        LightItemMode lastItemModel;
        if (mCommandLightMode.mParameters.size() == 0) {
            lastItemModel = new LightItemMode();
            lastItemModel.setStopTime("00:00");
        } else {
            lastItemModel =
                    mCommandLightMode.mParameters.get(mCommandLightMode.mParameters.size() - 1);
        }
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
        lightItemMode.setStopTime("23:59");
        refreshProgress();
        mCommandLightMode.mParameters.add(lightItemMode);
        refreshModeItems();
    }

    private void refreshProgress() {
        for (int index = 0; index < 7; index++) {
            Drawable draw;
            SeekBar progressBar = (SeekBar) llSeekbar.findViewWithTag("index" + (index));
            switch (index) {
                case 0:
                    progressBar.setProgress(mCommandLightMode.getMParameters().get(mIndex).getLight1Level());
                    break;
                case 1:
                    progressBar.setProgress(mCommandLightMode.getMParameters().get(mIndex).getLight2Level());
                    break;
                case 2:
                    progressBar.setProgress(mCommandLightMode.getMParameters().get(mIndex).getLight3Level());
                    break;
                case 3:
                    progressBar.setProgress(mCommandLightMode.getMParameters().get(mIndex).getLight4Level());
                    break;
                case 4:
                    progressBar.setProgress(mCommandLightMode.getMParameters().get(mIndex).getLight5Level());
                    break;
                case 5:
                    progressBar.setProgress(mCommandLightMode.getMParameters().get(mIndex).getLight6Level());
                    break;
                case 6:
                    progressBar.setProgress(mCommandLightMode.getMParameters().get(mIndex).getLight7Level());
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
