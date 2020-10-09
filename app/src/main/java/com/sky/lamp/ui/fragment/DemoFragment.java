package com.sky.lamp.ui.fragment;

import static com.sky.lamp.utils.HexUtils.hexToByte;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.guo.duoduo.wifidetective.core.devicescan.IP_MAC;
import com.orhanobut.logger.Logger;
import com.sky.lamp.BaseFragment;
import com.sky.lamp.R;
import com.sky.lamp.bean.CommandLightMode;
import com.sky.lamp.bean.LightItemMode;
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

public class DemoFragment extends BaseFragment implements SocketResponseListener {
    @BindView(R.id.analogClock)
    MyAnalogClock analogClock;
    Unbinder unbinder;
    @BindView(R.id.btn_start)
    Button btnStart;
    CommandLightMode commandLightMode;
    private Handler mHandler = new Handler();
    Timer timer;
    private IP_MAC ipMac;
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

    @Subscribe
    public void refreshEvent(CommandLightMode commandLightMode) {
        this.commandLightMode = commandLightMode;
        socketConfig = new SocketConfig.Builder(getActivity().getApplicationContext())
                .setIp(ipMac.mIp)//ip
                .setPort(61818)//端口
                .setReadBufferSize(1024)//readBuffer
                .setIdleTimeOut(30)//客户端空闲时间,客户端在超过此时间内不向服务器发送数据,则视为idle状态,则进入心跳状态
                .setTimeOutCheckInterval(10)//客户端连接超时时间,超过此时间则视为连接超时
                .setRequestInterval(10)//请求超时间隔时间
                //                .setHeartbeatRequest("(1,1)\r\n")//与服务端约定的发送过去的心跳包
                //                .setHeartbeatResponse("(10,10)\r\n") //与服务端约定的接收到的心跳包
                .builder();
        ContentServiceHelper.bindService(getActivity(), socketConfig);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ConnectSuccessEvent event) {
        if (event.getConnectType() == Contants.CONNECT_SUCCESS_TYPE) {
            RxToast.showToast("建立连接成功");
        } else {
            RxToast.showToast("建立连接失败");
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
        if (timer != null) {
            timer.cancel();
        }
        ContentServiceHelper.unBindService(getActivity());

    }

    @OnClick(R.id.btn_start)
    public void onViewClicked() {
        startDemoTask();
    }

    private void startDemoTask() {
        if (commandLightMode == null) {
            return;
        }
        if (timer != null ) {
            timer.cancel();
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
                    int startHour = Integer.parseInt(startTime.split(":")[0]);
                    startCalendar.set(Calendar.HOUR, startHour);
                    int startMinute = Integer.parseInt(startTime.split(":")[1]);
                    startCalendar.set(Calendar.MINUTE, startMinute);

                    Calendar endCalendar = Calendar.getInstance();
                    int endHour = Integer.parseInt(endTime.split(":")[0]);
                    endCalendar.set(Calendar.HOUR, endHour);
                    int endMinute = Integer.parseInt(endTime.split(":")[1]);
                    endCalendar.set(Calendar.MINUTE, endMinute);

                    if (calendar.after(startCalendar) && calendar.before(endCalendar)) {
                        byte[] temp = new byte[] {
                                (byte) 0xaa, (byte) 0x0a, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                                (byte) 0x55};
                        // 应该不考虑时间
                        temp[2] = hexToByte(startHour);
                        temp[3] = hexToByte(startMinute);
                        temp[4] = hexToByte(endHour);
                        temp[5] = hexToByte(endMinute);
                        temp[6] = hexToByte(lightItemMode.getLight1Level());
                        temp[7] = hexToByte(lightItemMode.getLight2Level());
                        temp[8] = hexToByte(lightItemMode.getLight3Level());
                        temp[9] = hexToByte(lightItemMode.getLight4Level());
                        temp[10] = hexToByte(lightItemMode.getLight5Level());
                        temp[11] = hexToByte(lightItemMode.getLight6Level());
                        temp[12] = hexToByte(lightItemMode.getLight7Level());
                        temp[13] = hexToByte(0);
                        // 检验位
                        temp[14] = getVer(temp);
                        System.out.println("sendCommand success " + HexUtils.bytes2Hex(temp));
                        ContentServiceHelper.sendClientMsg(temp);
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
