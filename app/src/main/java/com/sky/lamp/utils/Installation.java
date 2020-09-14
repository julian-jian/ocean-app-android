package com.sky.lamp.utils;

import android.content.Context;
import android.text.TextUtils;
import com.sky.lamp.MyApplication;
import com.vondear.rxtools.RxDeviceTool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

public class Installation {
	private static String sID = null;
	private static final String INSTALLATION = "INSTALLATION";

	/**
	 * Used to Identify App Installations
	 * 
	 * @param context
	 * @return
	 */
	public synchronized static String id(Context context) {
		if (sID == null) {
			File installation = new File(context.getFilesDir(), INSTALLATION);
			try {
				if (!installation.exists())
					writeInstallationFile(installation);
				sID = readInstallationFile(installation);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return sID;
	}

	private static String readInstallationFile(File installation)
			throws IOException {
		RandomAccessFile f = new RandomAccessFile(installation, "r");
		byte[] bytes = new byte[(int) f.length()];
		f.readFully(bytes);
		f.close();
		return new String(bytes);
	}

	private static void writeInstallationFile(File installation)
			throws IOException {
        String id = RxDeviceTool.getIMEI(MyApplication.getInstance());
        if (TextUtils.isEmpty(id)) {
            id = UUID.randomUUID().toString();
        }
        FileOutputStream out = new FileOutputStream(installation);
		out.write(id.getBytes());
		out.close();
	}
}