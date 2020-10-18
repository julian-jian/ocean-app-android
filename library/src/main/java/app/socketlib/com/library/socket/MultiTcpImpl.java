package app.socketlib.com.library.socket;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.logging.Logger;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import android.util.Log;
import app.socketlib.com.library.events.ConnectClosedEvent;
import app.socketlib.com.library.events.ConnectFailEvent;
import app.socketlib.com.library.events.ConnectSuccessEvent;
import app.socketlib.com.library.utils.Bus;
import app.socketlib.com.library.utils.Contants;
import app.socketlib.com.library.utils.HexUtils;
import app.socketlib.com.library.utils.LogUtil;

public class MultiTcpImpl {

    private final int closeType;
    private SocketConfig mConfig;
    private NioSocketConnector mConnection;
    private IoSession mSession;
    private InetSocketAddress mAddress;
    LinkedList<byte[]> mCacheObjectList = new LinkedList<>();
    LinkedList<String> mCacheStringList = new LinkedList<>();

    private enum ConnectStatus {
        DISCONNECTED,//连接断开
        CONNECTED//连接成功
    }

    private MultiTcpImpl.ConnectStatus status = MultiTcpImpl.ConnectStatus.DISCONNECTED;

    public MultiTcpImpl.ConnectStatus getStatus() {
        return status;
    }

    public void setStatus(MultiTcpImpl.ConnectStatus status) {
        this.status = status;
    }

    public MultiTcpImpl(SocketConfig config, int closeType) {
        this.mConfig = config;
        this.closeType = closeType;
        init();
    }

    private void init() {
        mAddress = new InetSocketAddress(mConfig.getIp(), mConfig.getPort());
        mConnection = new NioSocketConnector();
        mConnection.getSessionConfig().setReadBufferSize(mConfig.getReadBufferSize());
        mConnection.getSessionConfig().setKeepAlive(true);//设置心跳
        //设置超过多长时间客户端进入IDLE状态
        mConnection.getSessionConfig().setBothIdleTime(mConfig.getIdleTimeOut());
        mConnection
                .setConnectTimeoutCheckInterval(mConfig.getConnetTimeOutCheckInterval());//设置连接超时时间
        LoggingFilter loggingFilter = new LoggingFilter("EventRecodingLogger");
        mConnection.getFilterChain().addLast("Logging", loggingFilter);
        mConnection.getFilterChain()
                .addLast("codec", new ProtocolCodecFilter(new MessageLineFactory()));
        mConnection.setDefaultRemoteAddress(mAddress);
        //设置心跳监听的handler
        KeepAliveRequestTimeoutHandler heartBeatHandler =
                new KeepAliveRequestTimeoutHandlerImpl(closeType);
        KeepAliveMessageFactory heartBeatFactory =
                new KeepAliveMessageFactoryImpm(mConfig.getHeartbeatRequest(),
                        mConfig.getHeartbeatResponse());
        //设置心跳
        KeepAliveFilter heartBeat =
                new KeepAliveFilter(heartBeatFactory, IdleStatus.BOTH_IDLE, heartBeatHandler);
        //是否回发
        heartBeat.setForwardEvent(false);
        //设置心跳间隔
        heartBeat.setRequestInterval(mConfig.getRequsetInterval());
        mConnection.getFilterChain().addLast("heartbeat", heartBeat);
        mConnection.setHandler(new MultiTcpImpl.DefaultIoHandler());

        mCacheObjectList.clear();
        mCacheStringList.clear();
    }

    /**
     * 与服务器连接
     *
     * @return
     */
    public void connnectToServer() {
        int count = 0; //连接达到10次,则不再重连
        if (null != mConnection) {
            while (getStatus() == MultiTcpImpl.ConnectStatus.DISCONNECTED) {
                if (mConnection == null) {
                    break;
                }
                try {
                    Thread.sleep(1000);
                    ConnectFuture future = mConnection.connect();
                    future.awaitUninterruptibly();// 等待连接创建成功
                    mSession = future.getSession();
                    if (mSession.isConnected()) {
                        setStatus(MultiTcpImpl.ConnectStatus.CONNECTED);
                        Bus.post(new ConnectSuccessEvent(Contants.CONNECT_SUCCESS_TYPE));
                        LogUtil.e("connnectToServer中,Socket连接成功!");
                        for (String string : mCacheStringList) {
                            send(string);
                        }

                        for (byte[] object : mCacheObjectList) {
                            send(object);
                        }
                        mCacheStringList.clear();
                        break;
                    }
                } catch (Exception e) {
                    count++;
                    LogUtil.e("connnectToServer中,Socket连接失败,每3秒连接一次!");
                    if (count == 7) {
                        Bus.post(new ConnectClosedEvent(closeType));
                        Bus.post(new ConnectFailEvent());
                    }
                }
            }
        }
    }

    public void send(String msg) {
        send(msg, false);
    }

    private void send(String msg, boolean isCache) {
        if (mSession != null && mSession.isConnected()) {
            LogUtil.i(
                    "sendCommand " + mConfig
                            .getIp() + " " + msg);
            mSession.write(msg);
        } else {
            if (!isCache) {
                mCacheStringList.add(msg);
            }
            LogUtil.e("send fail " + msg);
        }
    }

    public void send(byte[] msg) {
        send(msg, false);
    }

    private void send(byte[] msg, boolean isCache) {
        if (mSession != null && mSession.isConnected()) {
            LogUtil.i(
                    "sendCommand " + mConfig
                            .getIp() + " " + HexUtils.bytes2Hex(msg));
            mSession.write(IoBuffer.wrap(msg));
        } else {
            if (!isCache) {
                mCacheObjectList.add(msg);
            }
            LogUtil.e("send fail " + msg);
        }
    }

    /**
     * 断开连接
     */
    public void disConnect() {
        setStatus(MultiTcpImpl.ConnectStatus.DISCONNECTED);
        mConnection.getFilterChain().clear();
        mConnection.dispose();
        if (closeType == Contants.CONNECT_CLOSE_TYPE && mSession != null) {
            mSession.closeOnFlush();
        }
        mConnection = null;
        mSession = null;
        mAddress = null;
        mCacheObjectList.clear();
        mCacheStringList.clear();
    }

    /***
     * Socket的消息接收处理和各种连接状态的监听在这里
     */
    private class DefaultIoHandler extends IoHandlerAdapter {

        @Override
        public void sessionOpened(IoSession session) throws Exception {
            super.sessionOpened(session);
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            Log.d("zfy", "messageReceived() called with: session = [" + session + "], message = ["
                    + message + "]");

        }

        @Override
        public void sessionCreated(IoSession session) throws Exception {
            super.sessionCreated(session);
            LogUtil.i("sessionCreated() called with: session = [" + session + "]");
        }

        @Override
        public void sessionClosed(IoSession session) throws Exception {
            super.sessionClosed(session);
            LogUtil.e("session关闭,发送事件,重新连接");
            setStatus(MultiTcpImpl.ConnectStatus.DISCONNECTED);
            Bus.post(new ConnectClosedEvent(closeType));
        }

        @Override
        public void messageSent(IoSession session, Object message) throws Exception {
            super.messageSent(session, message);
        }

        @Override
        public void inputClosed(IoSession session) throws Exception {
            super.inputClosed(session);
            LogUtil.e("inputClosed,发送事件,重新连接");
            Bus.post(new ConnectClosedEvent(closeType));
        }

        @Override
        public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
            super.sessionIdle(session, status);
            if (null != session) {
                session.closeNow();
            }
        }

        @Override
        public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
            super.exceptionCaught(session, cause);
            LogUtil.e(
                    "exceptionCaught() called with: session = [" + session + "], cause = [" + cause
                            + "]");
        }
    }
}
