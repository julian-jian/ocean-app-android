package com.sky.lamp.ui.fragment;

import static com.sky.lamp.utils.HexUtils.tenToHexByte;

import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


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
import app.socketlib.com.library.ContentServiceHelper;
import app.socketlib.com.library.socket.SocketConfig;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DemoFragment extends DelayBaseFragment {
    @BindView(R.id.analogClock)
    MyAnalogClock analogClock;
    Unbinder unbinder;
    @BindView(R.id.btn_start)
    Button btnStart;
    CommandLightMode commandLightMode;
    private Handler mHandler = new Handler();
    Timer timer;
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
        try {
            ContentServiceHelper.unBindService(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
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
        final Calendar clockCalendar = Calendar.getInstance();
        clockCalendar.set(Calendar.HOUR, 0);
        clockCalendar.set(Calendar.MINUTE, 0);
        clockCalendar.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH));
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
                for (LightItemMode lightItemMode : mParameters) {
                    String startTime = lightItemMode.getStartTime();
                    String endTime = lightItemMode.getStopTime();
                    Calendar startCalendar = Calendar.getInstance();
                    int startHour = Integer.parseInt(startTime.split(":")[0]);
                    startCalendar.set(Calendar.HOUR, startHour);
                    int startMinute = Integer.parseInt(startTime.split(":")[1]);
                    startCalendar.set(Calendar.MINUTE, startMinute);

                    Calendar endCalendar = Calendar.getInstance();
                    int endHour = Integer.parseInt(endTime.split(":")[0]);
                    endCalendar.set(Calendar.HOUR, endHour);
                    int endMinute = Integer.parseInt(endTime.split(":")[1]);
                    endCalendar.set(Calendar.MINUTE, endMinute);
                    boolean hour =
                            clockCalendar.get(Calendar.HOUR) == startCalendar.get(Calendar.HOUR);
                    boolean minute =
                            clockCalendar.get(Calendar.MINUTE) == startCalendar.get(Calendar.MINUTE);
                    boolean isSame = hour && minute;
                    if ((clockCalendar.after(startCalendar) && clockCalendar.before(endCalendar))
                            || isSame) {
                        sendCommand(lightItemMode);

                    }
                    break;
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        analogClock.refreshView(clockCalendar.get(Calendar.HOUR),
                                clockCalendar.get(Calendar.MINUTE));
                    }
                });
                // SEND COMMAND
                clockCalendar.add(Calendar.MINUTE, 30);
                System.out.println("DemoFragment.run " + clockCalendar.toString());
            }
        };
        timer.schedule(timerTask, 0L, 5 * 1000L);
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

        //                        temp[6] =test();
        //                        temp[7] =test();
        //                        temp[8] =test();
        //                        temp[9] =test();
        //                        temp[10] =test();
        //                        temp[11] =test();
        //                        temp[12] =test();
        //                        temp[13] =test();

        // 检验位
        temp[14] = HexUtils.getVerifyCode(temp);
        index++;
        System.out.println("sendCommand success " + HexUtils.bytes2Hex(temp));
        ContentServiceHelper.sendClientMsg(temp);
    }

    int index = 0;
    public byte test() {
        int process = new Random().nextInt(100);
        byte b = tenToHexByte(process);
        return b;

    }

}
