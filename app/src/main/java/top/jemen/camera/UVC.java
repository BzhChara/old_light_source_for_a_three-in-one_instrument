package top.jemen.camera;

import android.graphics.Bitmap;
import android.view.Surface;

public class UVC {
	/**支持的几种分辨率
	 * 320*240    640*480   800*600 1600*1200
	 */
	public static final int IMG_WIDTH = 640;
	public static final int IMG_HEIGHT = 480;
	
	// JNI functions
	private static native int prepareCamera(int videoid,int w,int h);
	public static int prepareCamera(int videoid) {
		return  prepareCamera(videoid,IMG_WIDTH,IMG_HEIGHT);
	}
	public static native void processCamera(int fd);

	/**
	 * 调用之后再次使用则需要重新prepare，否则出错。
	 */
	public static native void stopCamera(int fd);

	/**
	 * 里边旋转了180度
	 * @param bitmap
	 */
	public static native void pixeltobmp(Bitmap bitmap);
//	public static native void pixeltobmp(Object bitmap);

	public static native void drawPixels(Surface surface);
//	public static native void drawPixels(Object surface);
	
	public static native int setBrightness(int fd,int  value);
	public static native int setExposure(int fd,int  value);
	
	
	static {
		System.loadLibrary("JemenUVC");
	}
}


