package com.whswzz.prfluroanalyzer.fluoro.uvc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.whswzz.prfluroanalyzer.fluoro.entity.FluData;
import com.whswzz.prfluroanalyzer.fluoro.uvc.CameraTool.TackCallback;
import com.zkzk.pra.utils.ToastUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.os.SystemClock;
import android.view.SurfaceHolder;
import android.widget.ImageView;
import android.widget.Toast;
import top.jemen.interfaces.ICallback;
import top.jemen.utils.ColorUtil;
import top.jemen.utils.LogUtil;

/**
 * 仅在使用时候打开摄像头，使用完则关闭
 * @author Administrator
 *
 */
public class CameraTool2 {
//	public static void oneShot(final CamreaCallback callback) {
//		final Camera c=open();
//		if(c==null) {
//			callback.onFailed("打开摄像头出错");
//			return;
//		}
//		c.startPreview(); //经实验不用设置预览surface也可以。
//		SystemClock.sleep(500); //此USB摄像头监听focus无效，故采用延时来解决,至少300ms(第一次黑色)。
//		c.takePicture(null, null, new PictureCallback() {
//			@Override
//			public void onPictureTaken(byte[] data, Camera camera) {
//				LogUtil.d("data len="+data.length);
//				Bitmap[] bmps=new Bitmap[1];
//				 bmps[0]= BitmapFactory.decodeByteArray(data, 0,data.length);	
//				callback.onSuccess(bmps);
//				c.stopPreview();
//				c.release();
//			}
//		});
//	}
	private static Camera c;
	public static void shotX(int times,final CamreaCallback callback) {
//		final Camera c=open();
		if(c==null) {
			c=open();
			if(c==null) {
				callback.onFailed("打开摄像头出错");
				return;
			}
		}
	
		Parameters params = c.getParameters();
		LogUtil.d("w:"+params.getPictureSize().width+",h:"+params.getPictureSize().height);
		params.setExposureCompensation(0);// 曝光补偿 -3到3
		params.setAutoWhiteBalanceLock(true); // 关闭自动白平衡吧。 //摄像头不支持该设置。
		params.setAutoExposureLock(true); // 摄像头也不支持。
//		params.setRotation(180);
		c.setParameters(params);
		c.startPreview(); //经实验不用设置预览surface也可以。   A33屏 Android 4.4.4
		SystemClock.sleep(500); //此USB摄像头监听focus无效，故采用延时来解决,至少300ms(第一次黑色)。
		TackCallback tackcallback=new TackCallback(times, callback);
		c.takePicture(null, null, tackcallback);
	}
	
	private static Camera open() {
		Camera camera = null;
		for(int i=0;null==camera&&i<Camera.getNumberOfCameras();i++) {
			try {
				camera=Camera.open(i);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return camera;
	}
	
	
	public static class TackCallback implements PictureCallback { // 拍摄多找照片
		final Bitmap[] bmps;
		private CamreaCallback callback;
		private int aband=6;

		public TackCallback( int times,  CamreaCallback callback) {
			super();
			this.callback = callback;
			bmps = new Bitmap[times];
			n = times <= 100 ? times : 100; // 最多n张照片合成
			if(n<1) {
				n=1;
			}
			n+=aband;//前面放弃3张吧。
		}
		int n; // 最多n张照片合成
		private int i = 0;

		@Override
		public void onPictureTaken(byte[] bs, Camera camera) {
//			LogUtil.d("onPictureTaken, thread=" + Thread.currentThread()); // 总是在主线程
			i++;
			if(i<=aband) {
				camera.takePicture(null, null, TackCallback.this);
				return;
			}
			bmps[i-1-aband] = BitmapFactory.decodeByteArray(bs, 0, bs.length); //会撑破内存
			LogUtil.d("拍照次数："+i);
			if (i < n) {
				camera.takePicture(null, null, TackCallback.this);
				return;
			}
			camera.stopPreview();
			callback.onSuccess(bmps);
//			c.release();  //释放之后再打开就容易出错，所以就不释放了。
//			c=null;
		}
	}
	

	
	/**
	 * 多次采集合成,通过预览获取图片
	 * @param times 采集的次数
	 * @param off	偏移放弃前面的多少帧
	 * @param callback
	 */
	public static void shotXTimes(final int times,final int off,final CamreaCallback callback) {
		if(times<=0||off<0) {
			callback.onFailed("参数错误");
			return;
		}
		if(c==null) {
			c=open();
			if(c==null) {
				callback.onFailed("打开摄像头出错");
				return;
			}
		}
		c.startPreview(); //经实验不用设置预览surface也可以但是预览出现一两帧就可能自动停止了。。
		c.setPreviewCallback(new PreviewCallback() { // 平均帧率10左右
			int i = 0;
			long lastT;
			Bitmap[] bmps = new Bitmap[times];;
			@Override
			public void onPreviewFrame(byte[] bs, Camera camera) { // 这里接收到的是YUV格式的。460800=640*480*3/2
				// LogUtil.d("previewcallback,datalength=" + bs.length);
				LogUtil.d("i="+i+" ,off="+off);
				if (i == 0) {
					lastT = System.currentTimeMillis();
				} else {
					long t = System.currentTimeMillis();
					LogUtil.d("帧率" + 1000 / (t - lastT));
					lastT = t;
				}
			
				if (i++ <off) {
					return;
				} else if (i-off <= times) { //前面的++总会执行
					Camera.Size size = camera.getParameters().getPreviewSize();// 获得预览图像设置的尺寸
					// 关于ImageFormat，目前只支持NV21和YUY2，其他的会报错
					YuvImage img = new YuvImage(bs, ImageFormat.NV21, size.width, size.height, null);
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					// 80表示转Jpeg的质量，最高100，会影响到预览性能，越高帧率越低
					img.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);
					bmps[i-off - 1] = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
					try {
						stream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return;
				}
				LogUtil.d("结束预览并释放");
				camera.stopPreview();
//				camera.release();
				callback.onSuccess(bmps);

			}
		});
		
		
	}
	
	
	public static void startPreview(SurfaceHolder holder) {
		if(null==c) {
			c=open();
		}
		try {
			c.setPreviewDisplay(holder);
			c.startPreview();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void stopPreview() {
		c.stopPreview();
		try {
			c.setPreviewDisplay(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		c.release();
		c=null;
	}
	
	
	
	
	public static abstract class CamreaCallback{
		public  void onFailed(String msg) {};
		public void onSuccess(Bitmap[] bmps) {};
	}
	
	
	
	
	
	
}

