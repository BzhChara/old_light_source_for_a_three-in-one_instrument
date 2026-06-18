package org.xutils;

import android.os.Looper;

/**
 * this class is used to collect the ANR message.
 * @author Jemen Chen
 *
 */
public class ANRException extends RuntimeException {
	public ANRException() {
		super("jemen，主线程超过设定时间未响应，处理下吧啊！！");
		Thread mainThread = Looper.getMainLooper().getThread();
		setStackTrace(mainThread.getStackTrace());
	}
}