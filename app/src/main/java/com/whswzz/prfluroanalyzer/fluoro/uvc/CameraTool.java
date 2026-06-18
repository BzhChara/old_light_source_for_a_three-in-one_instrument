package com.whswzz.prfluroanalyzer.fluoro.uvc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.example.x6.gpioctl.GpioUtils;
import com.whswzz.prfluroanalyzer.fluoro.entity.FluData;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.ImageView;
import top.jemen.interfaces.ICallback;
import top.jemen.utils.ColorUtil;
import top.jemen.utils.CurveUtil;
import top.jemen.utils.LogUtil;
import top.jemen.utils.threadpool.AsyncProcessor;

@Deprecated
public class CameraTool {
	private static CameraTool tool;
	public static final int IMG_WIDTH = 640;// 640
	public static final int IMG_HEIGHT = 480;// 480
	private List<Camera> cameras = new LinkedList<>();
	private List<Bitmap> bitmaps = new LinkedList<>();// 画背景边框的。
	private List<Canvas> canvass = new LinkedList<Canvas>();
	private Paint paint;
	float left = IMG_WIDTH * 0.002f, top = IMG_HEIGHT * 0.025f, right = IMG_WIDTH - left * 2,
			bottom = IMG_HEIGHT - top * 2; // 外边框

	private CameraTool() {
		paint = new Paint();
		paint.setStyle(Style.STROKE);
	};

	public static CameraTool get() {
		if (null == tool) {
			synchronized (CameraTool.class) {
				if (null == tool) {
					tool = new CameraTool();
				}
			}
		}
		return tool;
	}

	public int ini(ImageView... ivs) {
		for (int i = 0; i < ivs.length; i++) {
			Bitmap bmp = Bitmap.createBitmap(IMG_WIDTH, IMG_HEIGHT, Config.ARGB_8888);
			bitmaps.add(bmp);
			Canvas canvas = new Canvas(bmp);
			canvass.add(canvas);
			canvas.drawColor(Color.WHITE);

			paint.setColor(Color.BLACK);
			canvas.drawRect(left, top, right, bottom, paint); // 外边框
			ivs[i].setImageBitmap(bitmaps.get(i));
		}
		int n = Camera.getNumberOfCameras();
		Log.d("jemen", "numberOfCameras=" + n);
		for (int i = cameras.size(); i < n && i < ivs.length; i++) {
			LogUtil.d("尝试打开摄像头" + i);
			try {
				final Camera c = Camera.open(i); // attempt to get a Camera instance
				if (null != c) {
					Parameters params = c.getParameters();
					params.setAutoWhiteBalanceLock(true); // 关闭自动白平衡吧。 //摄像头不支持该设置。
					params.setAutoExposureLock(true); // 摄像头也不支持。

					LogUtil.d("摄像头" + i + "是否支持自动白平衡锁：" + params.isAutoWhiteBalanceLockSupported());
					LogUtil.d("摄像头" + i + "是否支持自动曝光锁：" + params.isAutoExposureLockSupported());
					int min = params.getMinExposureCompensation();
					int max = params.getMaxExposureCompensation();
					LogUtil.d("曝光值范围：" + max + "    :   " + min);

					params.setExposureCompensation(min);// 曝光补偿 -3到3
					// params.setWhiteBalance(Parameters.WHITE_BALANCE_DAYLIGHT);

					params.setPictureSize(IMG_WIDTH, IMG_HEIGHT);
					params.setRotation(180);
					c.setParameters(params);
					c.setDisplayOrientation(180);
					// SurfaceView sv=new SurfaceView(MyApp.getApp());
					// c.setPreviewDisplay(sv.getHolder());
					cameras.add(c);
					// c.startPreview();
					// handler.postDelayed(new Runnable() {
					// @Override
					// public void run() {
					// c.startPreview();
					// }
					// }, 3000);

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return cameras.size();
	}

	public void release() {
		for (Camera c : cameras) {
			c.stopPreview();
			c.release();
		}
		cameras.clear();
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

		};
	};

	class Channel {
		ImageView iv;
		Button btUVC;
		FluData data;
	}

	public void analyze(final int index, final ImageView iv, final FluData data, final ICallback callback) {
		if (index >= cameras.size()) {
			LogUtil.e("摄像头编号超过所具有的个数");
			return;
		}
		final Canvas canvas = canvass.get(index);
		canvas.drawColor(Color.WHITE);
		paint.setColor(Color.BLACK);
		paint.setStyle(Style.STROKE);
		canvas.drawRect(left, top, right, bottom, paint); // 外边框
		final Bitmap bmp = bitmaps.get(index);
		iv.setImageBitmap(bmp);
		final Camera camera = cameras.get(index);

		try {
			camera.startPreview();
		} catch (Exception e) {
			LogUtil.e("预览出错，使用摄像头" + index);
			e.printStackTrace();
			camera.stopPreview();
			callback.onFailed(null);
			return;
		}
		// SystemClock.sleep(5000); //延迟时间没用，还是存在缓冲区里边。 自动对焦也是失败的。
		analyzePreview(index, iv, 100,data, callback, camera); // preview比takepicture略快。
//		camera.takePicture(null, null, new TackCallback(index, 100, iv, data, callback));
		// //连拍平均帧率7/8左右

	}

	private void analyzePreview(final int index, final ImageView iv,final int times, final FluData data, final ICallback callback,
			final Camera camera) {
		camera.setPreviewCallback(new PreviewCallback() { // 平均帧率10左右
			int i = 0;
			long lastT;
			int n = 30;
			Bitmap[] bmps = new Bitmap[times];;

			@Override
			public void onPreviewFrame(byte[] bs, Camera camera) { // 这里接收到的是YUV格式的。460800=640*480*3/2
				// LogUtil.d("previewcallback,datalength=" + bs.length);
				if (i == 0) {
					lastT = System.currentTimeMillis();
				} else {
					long t = System.currentTimeMillis();
					LogUtil.d("帧率" + 1000 / (t - lastT));
					lastT = t;
				}
				if (i++ < times - n) {
					return;
				} else if (i <= times) {
					Camera.Size size = camera.getParameters().getPreviewSize();// 获得预览图像设置的尺寸
					// 关于ImageFormat，目前只支持NV21和YUY2，其他的会报错
					YuvImage img = new YuvImage(bs, ImageFormat.NV21, size.width, size.height, null);
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					// 80表示转Jpeg的质量，最高100，会影响到预览性能，越高帧率越低
					img.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);
					bmps[i - 1] = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
					try {
						stream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return;
				}
				camera.stopPreview();

				resulveData(index, bmps, n, iv, data, callback);

			}
		});
	}

	class TackCallback implements PictureCallback { // 拍摄多找照片
		int index;
		private int times;
		final ImageView iv;
		FluData data;
		final Bitmap[] bmps;
		private ICallback callback;

		public TackCallback(int index, int times, ImageView iv, FluData data, ICallback callback) {
			super();
			this.index = index;
			this.times = times;
			this.iv = iv;
			this.data = data;
			this.callback = callback;
			bmps = new Bitmap[times];
			// n=times<=10?times:10; //最多n张照片合成
			n = times <= 30 ? times : 30; // 最多n张照片合成

		}

		int n; // 最多n张照片合成
		private int i = 0;
		private long lastT = 0;

		@Override
		public void onPictureTaken(byte[] bs, Camera camera) {
			LogUtil.d("onPictureTaken, thread=" + Thread.currentThread()); // 总是在主线程
			// if(i==0) {
			// lastT=System.currentTimeMillis();
			// }else {
			// long t=System.currentTimeMillis();
			// LogUtil.d("帧率"+1000/(t-lastT));
			// lastT=t;
			// }

			if (i >= times - n)
				bmps[i] = BitmapFactory.decodeByteArray(bs, 0, bs.length);
			if (++i < times) {
				camera.takePicture(null, null, TackCallback.this);
				return;
			}
			camera.stopPreview();
			// 生成合成的照片，比较耗时，不用了。
			// Bitmap bitmap = Bitmap.createBitmap(bmps[0].getWidth(), bmps[0].getHeight(),
			// bmps[0].getConfig());
			// for (int w = 0; w < bitmap.getWidth(); w++) { //图像合成过于耗时，还是不用了。
			// for (int h = 0; h < bitmap.getHeight(); h++) {
			// int r = 0, g = r, b = r;
			//
			// for (int p = 0; p < N; p++) {
			// r += bmps[i].getPixel(w, h) >> 16 & 0xff;
			// g += bmps[i].getPixel(w, h) >> 8 & 0xff;
			// b += bmps[i].getPixel(w, h) >> 0 & 0xff;
			// }
			// int color = 0xff000000 | (r / N << 16) | (g / N << 8) | (b / N);
			// bitmap.setPixel(w, h, color);
			// }
			// }
			resulveData(index, bmps, n, iv, data, callback);
		}

	}

	private void resulveData(int index, Bitmap[] bmps, int n, ImageView iv, FluData data, ICallback callback) {
		Bitmap bitmap = bmps[bmps.length - 1];
		Canvas canvas = canvass.get(index);
		canvas.drawBitmap(bitmap, 0, 0, paint);
		int left = 10, top = 190, w = 580, h = 90; // 内边框，取值范围。
		int right = (int) (left + w * IMG_WIDTH / 640f);
		Bitmap bmp = bitmaps.get(index);
		if (right > bmp.getWidth() - 3) {
			right = bmp.getWidth() - 3;
		}

		int v = Integer.MAX_VALUE, x = top;
		for (int i = top - 50; i < top + 40; i++) {
			int sum = 0;
			for (int j = left; j < right; j += 3) {
				int color = bmp.getPixel(j, i);
				sum += (color & 0xff) + (color >> 8 & 0xff) + (color >> 16 & 0xff);
			}
			if (sum < v) {
				v = sum;
				x = i;
			}
		}
		top = x + 10;
		LogUtil.d("top=" + top);
		left *= IMG_WIDTH / 640f;
		top *= IMG_HEIGHT / 480f;

		int bottom = (int) (top + h * IMG_HEIGHT / 480f);

		paint.setTextSize(20);

		paint.setColor(Color.WHITE);

		ArrayList<Float> values = new ArrayList<Float>();
		float min = ColorUtil.Value610(bmp.getPixel(left + 10, top + 10)), max = 0;
		for (int i = left; i < right; i++) {
			float sum = 0;

			for (int j = top; j <= bottom; j += 10) {// 画的绿色框会干扰的吧，改成每5行取一个点,h/5=18
				for (int p = bmps.length - 1; p >= 0 && p >= bmps.length - n; p--) {
					sum += ColorUtil.Value610(bmps[p].getPixel(i, j));
					sum += ColorUtil.Value610(bmps[p].getPixel(i - 1, j));
					sum += ColorUtil.Value610(bmps[p].getPixel(i + 1, j));
					sum += ColorUtil.Value610(bmps[p].getPixel(i - 2, j));
					sum += ColorUtil.Value610(bmps[p].getPixel(i + 2, j));
				}
			}
			float a = sum / ((bottom - top) / 10 * 5 * n);

			if (a > max) {
				max = a;
			} else if (a < min) {
				min = a;
			}
			values.add(a);
		}
		paint.setColor(Color.GREEN);
		// canvas.drawRect(left-20, top-10, right+20, bottom+20, paint);// 画个方框
		canvas.drawRect(left, top, right, bottom, paint);// 画个方框

		// Bitmap card = Bitmap.createBitmap(bmp, left, top, right - left, bottom -
		// top);

//		CurveUtil.showPeak(values, iv, canvas, paint, data);

		// c.drawBitmap(card, 1, 1, paint);
		LogUtil.d("doinbackground over");

		iv.setImageBitmap(bmp);
		// camera.release();
		for (Bitmap bm : bmps) {
			if (null != bm)
				bm.recycle();
		}
		callback.onSuccess(null);
	}

}
