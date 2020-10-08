package app.socketlib.com.library.socket;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * author：JianFeng
 * date：2017/8/17 19:45
 * description：数据解码器,当字符为\n时,一次订阅完成
 */
public class MessageLineCumulativeDecoder extends CumulativeProtocolDecoder {
    @Override
    protected boolean doDecode(IoSession ioSession, IoBuffer in, ProtocolDecoderOutput protocolDecoderOutput) throws Exception {
        int startPosition = in.position();
        System.out.println("MessageLineCumulativeDecoder.doDecode " + new String(in.array()));
        String bytesToHexString = bytesToHexString(in.array());
        while (in.hasRemaining()) {
            byte b = in.get();
            if (b == '\n') {//读取到\n时候认为一行已经读取完毕
                int currentPosition = in.position();
                int limit = in.limit();
                in.position(startPosition);
                in.limit(limit);
                IoBuffer buffer = in.slice();
                byte[] bytes = new byte[buffer.limit()];
                buffer.get(bytes);
                String message = new String(bytes);
                protocolDecoderOutput.write(message);
                in.position(currentPosition);
                in.limit(limit);
                return true;
            }
        }
        in.position(startPosition);
        System.out.println("MessageLineCumulativeDecoder.doDecode " + false);
        return false;
    }


    private static String hexString = "0123456789ABCDEF";
    public static String decode(String bytes) throws UnsupportedEncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length() / 2);
        // 将每2位16进制整数组装成一个字节
        for (int i = 0; i < bytes.length(); i += 2)
            baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
                    .indexOf(bytes.charAt(i + 1))));
        return new String (new String(baos.toByteArray(),"gb2312").getBytes("utf-8"),"utf-8");
    }


    public String bytesToHexString(byte[] bArr) {
        StringBuffer sb = new StringBuffer(bArr.length);
        String sTmp;

        for (int i = 0; i < bArr.length; i++) {
            sTmp = Integer.toHexString(0xFF & bArr[i]);
            if (sTmp.length() < 2)
                sb.append(0);
            sb.append(sTmp.toUpperCase());
        }

        return sb.toString();
    }
}
