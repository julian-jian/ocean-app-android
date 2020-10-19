package com.sky.lamp.ui.fragment;

import static com.sky.lamp.utils.HexUtils.tenToHexByte;

import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.orhanobut.logger.Logger;
import com.sky.lamp.BuildConfig;
import com.sky.lamp.R;
import com.sky.lamp.bean.CommandLightMode;
import com.sky.lamp.bean.LightItemMode;
import com.sky.lamp.ui.DelayBaseFragment;
import com.sky.lamp.ui.act.ModeInfoActivity;
import com.sky.lamp.utils.HexUtils;
import com.sky.lamp.view.MyAnalogClock;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import app.socketlib.com.library.socket.MultiTcpManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.addapp.pickers.util.DateUtils;

public class DemoFragment extends DelayBaseFragment {
    @BindView(R.id.analogClock)
    MyAnalogClock analogClock;
    Unbinder unbinder;
    @BindView(R.id.btn_start)
    Button btnStart;
    CommandLightMode commandLightMode;
    private Handler mHandler = new Handler();
    Timer timer;
    public static final String TAG = DemoFragment.class.getSimpleName();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demo, null);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    public void refreshData() {
        commandLightMode = ((ModeInfoActivity) getBaseActivity()).mCommandLightMode;
    }

    @Override
    protected void showDelayData() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (timer != null) {
            timer.cancel();
        }
    }

    @OnClick(R.id.btn_start)
    public void onViewClicked() {
        startDemoTask();
    }

    private void startDemoTask() {
        if (commandLightMode == null) {
            return;
        }
        if (timer != null) {
            timer.cancel();
        }
        mHandler.removeCallbacksAndMessages(null);
        timer = new Timer();
        //时间命中了那个模式，就发送哪个模式
        final Calendar today = Calendar.getInstance();
        today.setTime(DateUtils.parseHourDate("00:00"));

        final Calendar clockCalendar = Calendar.getInstance();
        clockCalendar.setTime(DateUtils.parseHourDate("00:00"));

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("DemoFragment.run " + Thread.currentThread());
                if (clockCalendar.get(Calendar.DAY_OF_MONTH) != today.get(Calendar.DAY_OF_MONTH)) {
                    System.out.println("DemoFragment.run finish");
                    timer.cancel();
                    return;
                }

                List<LightItemMode> mParameters = commandLightMode.mParameters;
                LightItemMode holdItemMode = null;
                holdItemMode = getHoldItemMode(mParameters, holdItemMode, clockCalendar);
                if (holdItemMode != null) {
                    Logger.i("当前时间 "+DateUtils.formatDate(clockCalendar.getTime(),"HH:mm")+" 命中 "+holdItemMode);
                    sendCommand(holdItemMode);
                } else {
                    sendEmptyCommand();
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        analogClock.refreshView(clockCalendar.get(Calendar.HOUR_OF_DAY),
                                clockCalendar.get(Calendar.MINUTE));
                    }
                });
                // SEND COMMAND
                clockCalendar.add(Calendar.MINUTE, 20);
                System.out.println("DemoFragment.run " + clockCalendar.toString());
            }
        };
//        timer.schedule(timerTask, 0L, 5 * 1000L);
        timer.schedule(timerTask, 0L, BuildConfig.DEBUG ? 1000 : 5 * 1000L);
    }

    private LightItemMode getHoldItemMode(List<LightItemMode> mParameters,
                                          LightItemMode holdItemMode, Calendar clockCalendar) {
        for (LightItemMode lightItemMode : mParameters) {
            String startTime = lightItemMode.getStartTime();
            String endTime = lightItemMode.getStopTime();

            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(DateUtils.parseHourDate(startTime));

            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(DateUtils.parseHourDate(endTime));

            // 区间范围内
            if (clockCalendar.getTimeInMillis() <= endCalendar.getTimeInMillis()
                    && clockCalendar.getTimeInMillis() >= startCalendar.getTimeInMillis()) {
                holdItemMode = lightItemMode;
                break;
            }
        }
        return holdItemMode;
    }

    private void sendEmptyCommand() {
        Logger.w("没有符合的模式 ");
        LightItemMode lightItemMode = new LightItemMode();
        lightItemMode.setStartTime("00:00");
        lightItemMode.setStopTime("00:00");
        lightItemMode.setLight1Level(0);
        lightItemMode.setLight2Level(0);
        lightItemMode.setLight3Level(0);
        lightItemMode.setLight4Level(0);
        lightItemMode.setLight5Level(0);
        lightItemMode.setLight6Level(0);
        lightItemMode.setLight7Level(0);
        sendCommand(lightItemMode);
    }

    private void sendCommand(LightItemMode lightItemMode) {
        byte[] temp = new byte[] {
                (byte) 0xaa, (byte) 0x0a, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x55};
        // 应该不考虑时间
        temp[6] = HexUtils.tenToHexByte(lightItemMode.getLight1Level());
        temp[7] = tenToHexByte(lightItemMode.getLight2Level());
        temp[8] = tenToHexByte(lightItemMode.getLight3Level());
        temp[9] = tenToHexByte(lightItemMode.getLight4Level());
        temp[10] = tenToHexByte(lightItemMode.getLight5Level());
        temp[11] = tenToHexByte(lightItemMode.getLight6Level());
        temp[12] = tenToHexByte(lightItemMode.getLight7Level());
        temp[13] = tenToHexByte(0);

        // 检验位
        temp[14] = HexUtils.getVerifyCode(temp);
        index++;
        System.out.println("sendCommand success " + HexUtils.bytes2Hex(temp) + " " + lightItemMode);
        MultiTcpManager.getInstance().send(temp);

    }

    int index = 0;

    public byte test() {
        int process = new Random().nextInt(100);
        byte b = tenToHexByte(process);
        return b;

    }

}
