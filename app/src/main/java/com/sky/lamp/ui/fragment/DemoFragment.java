package com.sky.lamp.ui.fragment;

import static com.sky.lamp.utils.HexUtils.tenToHexByte;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.orhanobut.logger.Logger;
import com.sky.lamp.BaseFragment;
import com.sky.lamp.R;
import com.sky.lamp.bean.CommandLightMode;
import com.sky.lamp.bean.LightItemMode;
import com.sky.lamp.bean.ModelSelectBean;
import com.sky.lamp.ui.DelayBaseFragment;
import com.sky.lamp.ui.act.ModeInfoActivity;
import com.sky.lamp.utils.HexUtils;
import com.sky.lamp.view.MyAnalogClock;
import com.vondear.rxtools.view.RxToast;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import app.socketlib.com.library.ContentServiceHelper;
import app.socketlib.com.library.events.ConnectSuccessEvent;
import app.socketlib.com.library.listener.SocketResponseListener;
import app.socketlib.com.library.socket.SocketConfig;
import app.socketlib.com.library.utils.Contants;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DemoFragment extends DelayBaseFragment implements SocketResponseListener {
    @BindView(R.id.analogClock)
    MyAnalogClock analogClock;
    Unbinder unbinder;
    @BindView(R.id.btn_start)
    Button btnStart;
    CommandLightMode commandLightMode;
    private Handler mHandler = new Handler();
    Timer timer;
    SocketConfig socketConfig;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demo, null);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ConnectSuccessEvent event) {
        getBaseActivity().dismissLoadingDialog();
        if (event.getConnectType() == Contants.CONNECT_SUCCESS_TYPE) {
            RxToast.showToast("建立连接成功");
            final byte[] temp = new byte[] {
                    (byte) 0xaa, (byte) 0x09, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x55};
            Calendar calendar = Calendar.getInstance();
            // 应该不考虑时间
            temp[2] = tenToHexByte(calendar.get(Calendar.HOUR));
            temp[3] = tenToHexByte(calendar.get(Calendar.MINUTE));
            // 检验位
            temp[14] = getVer(temp);
            System.out.println("sendCommand success " + HexUtils.bytes2Hex(temp));
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ContentServiceHelper.sendClientMsg(temp);
                }
            }, 1000);

        } else {
            RxToast.showToast("建立连接失败");
        }
    }

    public void refreshData() {
        commandLightMode = ((ModeInfoActivity) getBaseActivity()).mCommandLightMode;
        socketConfig = new SocketConfig.Builder(getActivity().getApplicationContext())
                .setIp(ModelSelectBean.ip)//ip
                .setPort(61818)//端口
                .setReadBufferSize(1024)//readBuffer
                .setIdleTimeOut(30)//客户端空闲时间,客户端在超过此时间内不向服务器发送数据,则视为idle状态,则进入心跳状态
                .setTimeOutCheckInterval(60*10)//客户端连接超时时间,超过此时间则视为连接超时
                .setRequestInterval(10)//请求超时间隔时间
                //                .setHeartbeatRequest("(1,1)\r\n")//与服务端约定的发送过去的心跳包
                //                .setHeartbeatResponse("(10,10)\r\n") //与服务端约定的接收到的心跳包
                .builder();
        ContentServiceHelper.bindService(getActivity(), socketConfig);
        getBaseActivity().showLoadingDialog("正在建立连接...");
    }

    @Override
    protected void showDelayData() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
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
                        byte[] temp = new byte[] {
                                (byte) 0xaa, (byte) 0x0a, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                                (byte) 0x55};
                        // 应该不考虑时间
//                        temp[6] = twoToHexByte(lightItemMode.getLight1Level());
//                        temp[7] = twoToHexByte(lightItemMode.getLight2Level());
//                        temp[8] = twoToHexByte(lightItemMode.getLight3Level());
//                        temp[9] = twoToHexByte(lightItemMode.getLight4Level());
//                        temp[10] = twoToHexByte(lightItemMode.getLight5Level());
//                        temp[11] = twoToHexByte(lightItemMode.getLight6Level());
//                        temp[12] = twoToHexByte(lightItemMode.getLight7Level());
//                        temp[13] = twoToHexByte(0);

                        temp[6] =test();
                        temp[7] =test();
                        temp[8] =test();
                        temp[9] =test();
                        temp[10] =test();
                        temp[11] =test();
                        temp[12] =test();
                        temp[13] =test();

                        // 检验位
                        temp[14] = getVer(temp);

                        index++;
                        System.out.println("sendCommand success " + HexUtils.bytes2Hex(temp));
                        ContentServiceHelper.sendClientMsg(temp);

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
                clockCalendar.add(Calendar.MINUTE, 60);
                System.out.println("DemoFragment.run " + clockCalendar.toString());
            }
        };
        timer.schedule(timerTask, 0L, 10 * 1000L);
    }

    int index = 0;
    public byte test() {
        int process = new Random().nextInt(100);
        byte b = tenToHexByte(process);
        return b;

    }

    public byte getVer(byte[] temp) {
        byte[] verfi = Arrays.copyOf(temp, 14);
        String s1 = HexUtils.bytes2Hex(verfi);
        String s2 = HexUtils.makeChecksum(s1);
        String s3 = s2.substring(s2.length() - 2, s2.length());
        byte s4 = HexUtils.hexToByte(s3);
        return s4;
    }

    @Override
    public void socketMessageReceived(String msg) {
        Logger.d("socketMessageReceived() called with: msg = [" + msg + "]");
    }
}
