package top.jemen.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.zkzk.pra.model.imp.NetModel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

/**
 * 本单例对象将对摄像头独占。
 * 
 * @author Administrator
 *
 */
public class CameraUtil {
	private static CameraUtil cameraUtil;
	private Camera camera;

	private Handler handler;// =new Handler(Looper.getMainLooper());
	private BlockingQueue<Runnable> que=new LinkedBlockingQueue<>();
	private CameraUtil() {
		// camera = getCameraInstance();
		// if(null==camera) {
		// LogUtil.e("camera=null");
		// return;
		// }
		// windowManager = (WindowManager)
		// MyApp.getApp().getSystemService(Context.WINDOW_SERVICE);
		//
		// final LayoutParams params = new LayoutParams();
		// params.width = 1;
		// params.height = 1;
		// params.alpha = 0;
		// params.type = LayoutParams.TYPE_SYSTEM_ALERT;
		// // 屏蔽点击事件
		// params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL |
		// LayoutParams.FLAG_NOT_FOCUSABLE
		// | LayoutParams.FLAG_NOT_TOUCHABLE;
		//
		// handler=new Handler(Looper.getMainLooper());
		// handler.post(new Runnable() {
		// @Override
		// public void run() {
		// LogUtil.d("add view thread="+Thread.currentThread());
		// mPreview = new CameraPreview(MyApp.getApp(), camera);// 显示出来后就会自动preview
		// windowManager.addView(mPreview, params);
		//
		// }
		// });
//		new Thread("tackpicture loop") {	//还是不占用主线程资源吧。
//			public void run() {
//				Looper.prepare();
				camera = getCameraInstance();
				if (null == camera) {
					LogUtil.e("camera=null");
					return;
				}
				windowManager = (WindowManager) MyApp.getApp().getSystemService(Context.WINDOW_SERVICE);

				final LayoutParams params = new LayoutParams();
				params.width = 1;
				params.height = 1;
				params.alpha = 0;
				params.type = LayoutParams.TYPE_SYSTEM_ALERT;
				// 屏蔽点击事件
				params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE
						| LayoutParams.FLAG_NOT_TOUCHABLE;
				handler = new Handler();
				LogUtil.d("add view thread=" + Thread.currentThread());
				mPreview = new CameraPreview(MyApp.getApp(), camera);// 显示出来后就会自动preview
				windowManager.addView(mPreview, params);
				new Thread("take picture") {
					public void run() {
						while(true) {
							try {
								que.take().run();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					};
				}.start();
//				Looper.loop();
//			};
//		}.start();
	}

	public static CameraUtil get() {
		if (null == cameraUtil) {
			synchronized (CameraUtil.class) {
				if (null == cameraUtil) {
					cameraUtil = new CameraUtil();
				}
			}
		}
		return cameraUtil;
	}

	private void release() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				windowManager.removeView(mPreview);
//				camera.stopPreview(); //这两个在destroySurface中
//				camera.release();
			}
		});
	}

	private Camera getCameraInstance() {
		int numberOfCameras = Camera.getNumberOfCameras();
		Log.d("jemen", "numberOfCameras=" + numberOfCameras);
		for (int i = 0; i < numberOfCameras; i++) {
			try {
				camera = Camera.open(i); // attempt to get a Camera instance
				if (null != camera) {
					break;
				}

			} catch (Exception e) {
//				Log.d("jemen", "open函数获取camera失败");
				continue;
			}
		}

		return camera; // returns nullif camera is unavailable
	}

	private CameraPreview mPreview;
	private WindowManager windowManager;
	public void takePictureAsync() {  //摄像头异常之后可能autofocus在主线程可能会导致ANR，给它用个独立的线程解决吧。
		que.offer(new Runnable() {
			@Override
			public void run() {
				takePicture();
			}
		});
	}
	
	private void takePicture() {
		LogUtil.d("tack picture");
		if (null == camera) {
			LogUtil.d("no camera\n");
			return;
		}

		// Parameters params=camera.getParameters();
		// params.setPictureFormat(ImageFormat.JPEG);
		// camera.setParameters(params);
		camera.startPreview();

//		SystemClock.sleep(3000);// 此处至少需要2秒时间！ //到surfacecreated需要2.2s,线分开调用不需延时了。
		// camera.takePicture(null, null, pictureCallback);//不需要自动对焦的话。但是需要一点儿延迟。
		
		LogUtil.d("start auto focus now");
		camera.autoFocus(new AutoFocusCallback() { // 无对焦情况，20ms
			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				Log.d("jemen", "onAutoFocus ,take picture");
				camera.takePicture(null, null, pictureCallback);
				// windowManager.removeView(mPreview);
			}
		});
	}
	
	private boolean isPreview=false;
	public void startPreview(final SurfaceView surfaceView) {
		try {
			camera.stopPreview();
//			camera.setPreviewDisplay(surfaceView.getHolder());
			camera.getParameters().setPreviewSize(surfaceView.getWidth(), surfaceView.getHeight());
			isPreview=true;
			camera.startPreview();
			SurfaceHolder holder = surfaceView.getHolder();
			holder.addCallback(new Callback() {
				@Override
				public void surfaceDestroyed(final SurfaceHolder holder) {
				}
				@Override
				public void surfaceCreated(final SurfaceHolder holder) {
					camera.setPreviewCallback(new PreviewCallback() {
						@Override
						public void onPreviewFrame(byte[] data, Camera camera) {
							LogUtil.d("camera preview callback,datasize is "+data.length+",thread="+Thread.currentThread());
							 // b. 获得摄像头预览Size
	                        Camera.Size size = camera.getParameters().getPreviewSize();
	                        try {
	                           // c. 创建YUV对象
	                            YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
	                            if (image != null) {

	                                // d. 存为BitMap对象
	                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
	                                image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);
	                                Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
	                                stream.close();
	                                Canvas canvas = holder.lockCanvas();
	    							Paint paint=new Paint();
	    							canvas.drawBitmap(bmp, 0, 0, paint);
	    							holder.unlockCanvasAndPost(canvas);
	                            }
	                        } catch (Exception ex) {
	                            Log.e("carson", "Error:" + ex.getMessage());
	                        }
							
						}
					});
				}
				
				@Override
				public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void stopPreview() {
		try {
			isPreview=false;
			camera.stopPreview();
			camera.setPreviewCallback(null);
//			camera.setPreviewDisplay(mPreview.getHolder());
		} catch (Exception e) {
			e.printStackTrace();
			// TODO Auto-generated catch block
		}
	}

	private PictureCallback pictureCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d("jemen", "byte[]length=" + data.length+"thread="+Thread.currentThread());
			Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

			ByteArrayOutputStream byStream = new ByteArrayOutputStream();// no need to close;

			bitmap.compress(Bitmap.CompressFormat.WEBP, 60, byStream); // not need high quality,30 is enough
			// 利用base64将字节数组转换成字符串
			byte[] byteArray = byStream.toByteArray();
			String imgString = Base64.encodeToString(byteArray, Base64.NO_WRAP);

//			NetModel.getModel().uploadLog(imgString, DateFormat.format("yy-MM-dd_HH-mm-ss", new Date()) + ".webp");

			// File pictureFile = FileUtil.getOutputMediaFile(FileUtil.MEDIA_TYPE_IMAGE);
			// if (pictureFile == null) {
			// return;
			// }
			// try {
			// FileOutputStream fos = new FileOutputStream(pictureFile);
			// fos.write(data);
			// fos.close();
			// camera.stopPreview();
			// Log.i("jemen", "pictureFiledata=" + data.length);
			// } catch (FileNotFoundException e) {
			// Log.i("jemen", "File notfound: " + e.getMessage());
			// } catch (IOException e) {
			// Log.i("jemen", "Erroraccessing file: " + e.getMessage());
			// }
			// windowManager.removeView(mPreview);
			if(!isPreview) {
				camera.stopPreview();
			}
		}

	};

	/** A safe way to get an instance of the Camera object. */

	/** A basic Camera preview class */

	public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
		private SurfaceHolder mHolder;
		private Camera mCamera;

		@SuppressWarnings({ "deprecation", "deprecation" })
		public CameraPreview(Context context, Camera camera) {
			super(context);
			mCamera = camera;
			mHolder = getHolder();
			mHolder.addCallback(this);
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		public void surfaceCreated(SurfaceHolder holder) {
			try {
				LogUtil.d("surface created");
				mCamera.setPreviewDisplay(holder);
				mCamera.setDisplayOrientation(0);
//				mCamera.startPreview();
			} catch (IOException e) {
				LogUtil.d("Errorsetting camera preview: " + e.getMessage());
			}

		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			mCamera.stopPreview();
//			mCamera.release(); //始终持有吧。
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
			if (mHolder.getSurface() == null) {
				return;
			}
			try {
				mCamera.stopPreview();
				// set previewsize and make any resize, rotate or
				// reformattingchanges here
				// start previewwith new settings
				mCamera.setPreviewDisplay(mHolder);
				mCamera.setDisplayOrientation(0);
				mCamera.startPreview();
			} catch (Exception e) {
				LogUtil.d("Errorstarting camera preview: " + e.getMessage());
			}

		}

	}

}
