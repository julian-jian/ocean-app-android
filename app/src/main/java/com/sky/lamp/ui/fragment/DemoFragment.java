package com.sky.lamp.ui.fragment;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.sky.lamp.BaseFragment;
import com.sky.lamp.R;
import com.sky.lamp.bean.CommandLightMode;
import com.sky.lamp.bean.LightItemMode;
import com.sky.lamp.view.MyAnalogClock;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DemoFragment extends BaseFragment {
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
        EventBus.getDefault().register(this);
        return view;
    }

    @Subscribe
    public void refreshEvent(CommandLightMode commandLightMode) {
        this.commandLightMode = commandLightMode;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
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
        timer = new Timer();
        //时间命中了那个模式，就发送哪个模式
        final Calendar today = Calendar.getInstance();
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH));
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("DemoFragment.run " + Thread.currentThread());
                if (calendar.get(Calendar.DAY_OF_MONTH) != today.get(Calendar.DAY_OF_MONTH)) {
                    System.out.println("DemoFragment.run finish");
                    timer.cancel();
                    return;
                }

                List<LightItemMode> mParameters = commandLightMode.mParameters;
                for (LightItemMode lightItemMode : mParameters) {
                    String startTime = lightItemMode.getStartTime();
                    String endTime = lightItemMode.getStopTime();
                    Calendar startCalendar = Calendar.getInstance();
                    startCalendar.set(Calendar.HOUR, Integer.parseInt(startTime.split(":")[0]));
                    startCalendar.set(Calendar.MINUTE, Integer.parseInt(startTime.split(":")[1]));

                    Calendar endCalendar = Calendar.getInstance();
                    endCalendar.set(Calendar.HOUR, Integer.parseInt(endTime.split(":")[0]));
                    endCalendar.set(Calendar.MINUTE, Integer.parseInt(endTime.split(":")[1]));

                    if (calendar.after(startCalendar) && calendar.before(endCalendar)) {
                        System.out.println("sendCommand success");
                    }
                    break;
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        analogClock.refreshView(calendar.get(Calendar.HOUR),
                                calendar.get(Calendar.MINUTE));
                    }
                });
                // SEND COMMAND
                calendar.add(Calendar.MINUTE, 10);
                System.out.println("DemoFragment.run " + calendar.toString());
            }
        };
        timer.schedule(timerTask, 0L, 1000L);
    }
}
