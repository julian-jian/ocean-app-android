package com.sky.lamp.ui.act;

import java.net.BindException;
import java.util.ArrayList;

import com.github.mikephil.charting.charts.LineChart;
import com.sky.lamp.BaseActivity;
import com.sky.lamp.R;
import com.sky.lamp.bean.CommandLightMode;
import com.sky.lamp.bean.LightItemMode;
import com.sky.lamp.view.TitleBar;
import com.vondear.rxtools.RxImageTool;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
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

public class ZaoLangAct extends BaseActivity {
    @BindView(R.id.chart)
    LineChart chart;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.tv_startTime)
    TextView tvStartTime;
    @BindView(R.id.tv_endTime)
    TextView tvEndTime;
    @BindView(R.id.ll_seekbar)
    LinearLayout llSeekbar;
    @BindView(R.id.ll_seekbar2)
    LinearLayout ll_seekbar2;
    @BindView(R.id.btn_del)
    ImageView btnDel;
    @BindView(R.id.btn_add)
    ImageView btnAdd;
    @BindView(R.id.btn_save)
    ImageView btnSave;
    @BindView(R.id.btn_send)
    Button btnSend;
    @BindView(R.id.ll)
    LinearLayout ll;
    @BindView(R.id.actionBar)
    TitleBar actionBar;
    @BindView(R.id.tv_seekbar_desr)
    TextView tv_seekbar_desr;
    private int mIndex;
    boolean mIsZaolang;

    public static void startUi(Context context, boolean isZaolang) {
        Intent intent = new Intent(context, ZaoLangAct.class);
        intent.putExtra("zaolang", isZaolang);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zaolang);
        ButterKnife.bind(this);
        actionBar.initLeftImageView(this);
        mIsZaolang = getIntent().getBooleanExtra("zaolang", false);
        refreshModeItems();
        actionBar.setTitle(mIsZaolang ? "造浪" : "上水泵");
        tvStartTime.setText("00:00");
        tvEndTime.setText("00:00");
        initSeekbar(llSeekbar, 1);
        if (mIsZaolang) {
            initSeekbar(ll_seekbar2, 0);
        }else{
            tv_seekbar_desr.setText("上水泵强度");
            findViewById(R.id.ll_seekbar2_desr).setVisibility(View.INVISIBLE);
        }
    }

    private void refreshModeItems() {
        radioGroup.removeAllViews();
        DisplayMetrics me = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(me);
        CommandLightMode mCommandLightMode = new CommandLightMode();
        mCommandLightMode.mParameters = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            LightItemMode lightItemMode = new LightItemMode();
            lightItemMode.setIndex(i);
            mCommandLightMode.mParameters.add(lightItemMode);
        }
        int margin = ((mCommandLightMode.mParameters.size() - 1) * 10 + RxImageTool.dp2px(5));
        int mTimeSingleWidth =
                (me.widthPixels - margin) / mCommandLightMode.mParameters.size();
        for (int index = 0; index < mCommandLightMode.mParameters.size(); index++) {
            LightItemMode lightItemMode = mCommandLightMode.mParameters.get(index);
            final RadioButton radioButton = new RadioButton(this);
            radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            radioButton.setText("模式" + (lightItemMode.getIndex() + 1));
            radioButton.setPadding(0, 5, 0, 5);
            radioButton.setGravity(17);
            radioButton.setTag("rb" + index);
            radioButton.setId(index);
            radioButton.setWidth(mTimeSingleWidth);
            radioButton.setHeight(RxImageTool.dp2px(23));
            radioButton.setButtonDrawable(new ColorDrawable(0));
            radioButton.setLayoutParams(
                    new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
                            RxImageTool.dp2px(23), 1f));
            final int finalIndex = index;
            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        radioButton.setTextColor(getResources().getColor(R.color.white));
                        mIndex = finalIndex;
                    } else {
                        System.out
                                .println("ModelInfoSettingFragment.onCheckedChanged " + isChecked);
                        radioButton.setTextColor(getResources().getColor(R.color.text_black));
                    }
                }
            });
            radioButton.setBackgroundResource(R.drawable.small_button_selector);
            radioGroup.addView(radioButton);
            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) radioButton.getLayoutParams();
            layoutParams.setMargins(10, 0, 0, 0);
            radioButton.setLayoutParams(layoutParams);
            // 最后一项选中
            if (index == mCommandLightMode.mParameters.size() - 1) {
                radioButton.setChecked(true);
            }
        }
    }

    private void initSeekbar(LinearLayout seekBar, int seek_index) {
        seekBar.removeAllViews();
        for (int index = 0; index < 1; index++) {
            View inflate =
                    LayoutInflater.from(this).inflate(R.layout.item_zaolang_seekbar, llSeekbar,
                            false);
            View inflate1 =
                    LayoutInflater.from(this).inflate(getSeekbarLayoutId(seek_index), null);
            ((ViewGroup) inflate.findViewById(R.id.fl_sub_seebar)).addView(inflate1);
            ImageView leftTv;
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
                    int progress = seekbar.getProgress();
                    progress = progress - 1;
                    seekbar.setProgress(Math.max(progress, 0));
                }
            });
            rightTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int progress = seekbar.getProgress();
                    progress = progress + 1;
                    seekbar.setProgress(Math.min(progress, 100));
                }
            });
            percentTv.setText(seekbar.getProgress() + "%");
            final int finalI = index;
            seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    percentTv.setText(progress + "%");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            seekBar.addView(inflate);
        }
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

    public int getProgress(int index) {
        return ((ProgressBar) llSeekbar.findViewWithTag("index" + (index))).getProgress();
    }

}
