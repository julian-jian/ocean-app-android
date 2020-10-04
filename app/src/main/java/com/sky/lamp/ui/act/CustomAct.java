package com.sky.lamp.ui.act;

import java.util.ArrayList;
import java.util.List;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.sky.lamp.BaseActivity;
import com.sky.lamp.R;
import com.sky.lamp.bean.LightModeInfo;
import com.sky.lamp.view.LightModeChartHelper;
import com.sky.lamp.view.TitleBar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;

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
    }

    private void initSeekbar() {
        for (int i = 1; i < 8; i++) {
            View inflate = LayoutInflater.from(this).inflate(R.layout.item_seekbar, null);
            TextView leftTv;
            final TextView percentTv;
            final SeekBar seekbar;
            View rightTv;
            leftTv = inflate.findViewById(R.id.leftTv);
            percentTv = inflate.findViewById(R.id.percentTv);
            seekbar = inflate.findViewById(R.id.seekbar);
            seekbar.setTag("index"+i);
            rightTv = inflate.findViewById(R.id.rightTv);
            leftTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("CustomAct.onClick");
                    int progress = seekbar.getProgress();
                    progress = progress - 10;
                    seekbar.setProgress(Math.max(progress,0));
                }
            });
            rightTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int progress = seekbar.getProgress();
                    progress = progress + 10;
                    seekbar.setProgress(Math.min(progress,100));
                }
            });
            final int finalI = i;
            seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    percentTv.setText(progress+"%");
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
//                        System.out.println("CustomAct.onProgressChanged "+total);
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

    public int getProgress(int i){
        return ((ProgressBar)ll.findViewWithTag("index"+ i)).getProgress();
    }


}
