package com.sky.lamp.view;

import java.util.ArrayList;
import java.util.List;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.LineDataSet.Mode;
import com.sky.lamp.R;

import android.content.Context;
import android.os.Build.VERSION;
import android.support.v4.view.ViewCompat;
import android.util.Log;

public class LightModeChartHelper {
    private static final String TAG = "LightModeChartHelper";
    private Context mContext;
    private LineChart mLineChart;

    public LightModeChartHelper(LineChart lineChart, Context context) {
        this.mLineChart = lineChart;
        this.mContext = context;
        setChartStyle();
    }

    public void setChartStyle() {
        this.mLineChart.getXAxis().setPosition(XAxisPosition.BOTH_SIDED);
        this.mLineChart.getAxisLeft().setDrawGridLines(false);
        this.mLineChart.getAxisRight().setDrawGridLines(false);
        this.mLineChart.getXAxis().setDrawGridLines(false);
        this.mLineChart.getAxisLeft().setDrawLabels(false);
        this.mLineChart.getAxisRight().setDrawLabels(false);
        this.mLineChart.getXAxis().setDrawLabels(false);
        this.mLineChart.getAxisLeft().setEnabled(false);
        this.mLineChart.getAxisRight().setEnabled(false);
        this.mLineChart.getXAxis().setEnabled(false);
        this.mLineChart.getLegend().setEnabled(false);
        this.mLineChart.getDescription().setEnabled(false);
        YAxis left = this.mLineChart.getAxisLeft();
        left.setAxisMaximum(130.0f);
        left.setAxisMinimum(0.0f);
        YAxis right = this.mLineChart.getAxisRight();
        right.setAxisMaximum(130.0f);
        right.setAxisMinimum(0.0f);
        this.mLineChart.getXAxis().setAxisMinimum(0.0f);
        this.mLineChart.getXAxis().setAxisMaximum(0.8f);
        this.mLineChart.setScaleEnabled(false);
        this.mLineChart.setDragEnabled(false);
        this.mLineChart.setTouchEnabled(false);
        this.mLineChart.setPinchZoom(false);
        this.mLineChart.setBorderWidth(1);
        this.mLineChart.setBackgroundColor(0);
        this.mLineChart.setViewPortOffsets(10.0f, 0.0f, 10.0f, 0.0f);
    }

    public void setBackground(LineDataSet setCom1) {
        setCom1.setColor(ViewCompat.MEASURED_STATE_MASK);
        int currentVersion = VERSION.SDK_INT;
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("--currentVersion-->");
        stringBuilder.append(currentVersion);
        Log.d(str, stringBuilder.toString());
        if (currentVersion >= 18) {
            setCom1.setDrawFilled(true);
            setCom1.setFillDrawable(this.mContext.getResources().getDrawable(R.drawable.mode_bg));
        }
    }

    public void setData(List<Entry> valsCom1) {
        if (this.mLineChart.getData() == null || ((LineData) this.mLineChart.getData()).getDataSetCount() <= 0) {
            Log.d(TAG, "--set data, data not exist--");
            LineDataSet setCom1 = new LineDataSet(valsCom1, "COMPANY 1");
            setCom1.setAxisDependency(AxisDependency.LEFT);
            setCom1.setDrawValues(false);
            setCom1.setDrawCircles(false);
            setCom1.setMode(Mode.CUBIC_BEZIER);
            setCom1.setCubicIntensity(0.1f);
            setBackground(setCom1);
            List dataSets = new ArrayList();
            dataSets.add(setCom1);
            this.mLineChart.setData(new LineData(dataSets));
        } else {
            Log.d(TAG, "--set data, data exist--");
            ((LineDataSet) ((LineData) this.mLineChart.getData()).getDataSetByIndex(0)).setValues(valsCom1);
            ((LineData) this.mLineChart.getData()).notifyDataChanged();
            this.mLineChart.notifyDataSetChanged();
        }
        this.mLineChart.invalidate();
    }
}
