package top.jemen.utils;

import java.util.List;
import java.util.Locale;

import com.whswzz.prfluroanalyzer.app.Build;

import android.text.TextUtils;
import android.util.Log;

public class LogUtil {
	
	
	
	
	
	public static void d(String s) {
		if(Build.DEBUG)
			Log.d(generateTag(),s);


	}
	
	public static void d2(String s) {
		if(Build.DEBUG)
			Log.d(generateTag(),s);
	}
	
	public static void i(String s) {
		if(Build.DEBUG)
			Log.i("jemen",s);
	}

	public static void e(String s) {
		if(Build.DEBUG)
			Log.e("jemen",s);
	}

	
	@SuppressWarnings("null")
	public static void printBuf(List<Byte> buf) {
		if(!Build.DEBUG||null!=buf) {
			return;
		}
		StringBuffer sb = new StringBuffer("printBuf["); // 为调试打印用
		for (byte b : buf) {
			String s = Integer.toHexString(b);
			if (b < 0)
				s = s.substring(6);
			sb.append(s + ",");
		}
		sb.append("]");
		Log.d("jemen",sb.toString());
	}
	
	
	   private static String generateTag() {
	        StackTraceElement caller = new Throwable().getStackTrace()[2];
	        String tag = "%s.%s(L:%d)";
	        String callerClazzName = caller.getClassName();
	        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
	        tag = String.format(Locale.getDefault(), tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
	        return tag;
	    }
}
