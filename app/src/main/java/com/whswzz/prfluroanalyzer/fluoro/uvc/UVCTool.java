package com.whswzz.prfluroanalyzer.fluoro.uvc;

import java.util.ArrayList;
import java.util.List;

import com.example.x6.gpioctl.GpioUtils;
import com.whswzz.prfluroanalyzer.fluoro.entity.FluData;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Style;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.ImageView;
import top.jemen.camera.UVC;
import top.jemen.interfaces.ICallback;
import top.jemen.utils.ColorUtil;
import top.jemen.utils.Curve;
import top.jemen.utils.CurveUtil;
import top.jemen.utils.LogUtil;
import top.jemen.utils.PermissionUtil;
import top.jemen.utils.threadpool.AsyncProcessor;

public class UVCTool {
	private static UVCTool tool;
	private GpioUtils gpio ;
	private int io ;//PL4 /7/6
	private Bitmap bmp;
	
	private byte[] LED_ON={(byte) 0xAA ,00, 01, 0x31, 00,(byte) 0,(byte) 1, (byte) 0xCC, 0x33, (byte) 0xC3, 0x3C};
	private byte[] LED_OFF={(byte) 0xAA ,00, 01, 0x31, 00,(byte) 0,(byte) 0, (byte) 0xCC, 0x33, (byte) 0xC3, 0x3C};
	private boolean LED=false;
	private List<Integer> fds=new ArrayList<>(2);
	
	
	float W=UVC.IMG_WIDTH,H= UVC.IMG_HEIGHT;
	float left=W*0.002f,top=H*0.025f,right=W-left*2,bottom=H-top*2; //外边框
	private UVCTool() {
		initLED();
		bmp = Bitmap.createBitmap(UVC.IMG_WIDTH, UVC.IMG_HEIGHT, Bitmap.Config.ARGB_8888);// 只允许RGBA_8888
		canvas = new Canvas(bmp);
		paint = new Paint();
		paint.setStyle(Style.STROKE);
		
	}
	Canvas canvas;
	Paint paint;
	public void init(final ICallback callback,final ImageView iv) {
		if(null!=iv) {
			canvas.drawColor(Color.WHITE);;
			paint.setColor(Color.BLACK);
			canvas.drawRect(left,top,right,bottom, paint);
			iv.setImageBitmap(bmp);
		}
		new AsyncTask<Void, Void, Integer>(){
			@Override
			protected Integer doInBackground(Void... params) {
				return initCam();
			}
			protected void onPostExecute(Integer  n) {
				if(n>0) {
					callback.onSuccess(null);
				}else {
					callback.onFailed(null);
				}
			};
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	public static synchronized UVCTool get() {
			if(null==tool) {
				tool=new UVCTool();
			}
			return tool;
	}
	
	
	public  void analyze(final int index,final ImageView iv, final Button btUVC,final FluData data) {
		btUVC.setEnabled(false);
		canvas.drawColor(Color.WHITE);;
		paint.setColor(Color.BLACK);
		paint.setStyle(Style.STROKE);
		canvas.drawRect(left,top,right,bottom, paint); //外边框
		iv.setImageBitmap(bmp);
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... arg0) {
				if(!LED) {
//					ComM.get().send(LED_ON );
					LED=true;
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
//				 GpioUtils.setGpioDirection(io, 0);
//				 GpioUtils.gpioSetValue(io, 1);
				
				for (int i = 0; i <1; i++) { // 单次采样的话存在滞后
					UVC.processCamera(fds.get(index));
				}
				UVC.pixeltobmp(bmp);// 640*480   旋转了180度 2022-10-19
				
				
				/**计算取景范围***********/
//				int left = 190*UVC.IMG_WIDTH/640, top = 200*UVC.IMG_HEIGHT/480, 
//				right = 485*UVC.IMG_WIDTH/640, bottom = 254*UVC.IMG_HEIGHT/480;
				
//				//插小条子测试用。
//				int left = 210*UVC.IMG_WIDTH/640, top = 210*UVC.IMG_HEIGHT/480, 
//				right = 485*UVC.IMG_WIDTH/640, bottom = 240*UVC.IMG_HEIGHT/480;
				
				//魏总第一版样机。
//				int left = 30*UVC.IMG_WIDTH/640, top = 220*UVC.IMG_HEIGHT/480, 
//						right = 485*UVC.IMG_WIDTH/640, bottom = 280*UVC.IMG_HEIGHT/480;
				//魏总第二版结构
				int left = 100*UVC.IMG_WIDTH/640, top = 200*UVC.IMG_HEIGHT/480, 
						right = left+480*UVC.IMG_WIDTH/640, bottom = top+70*UVC.IMG_HEIGHT/480;
				
				paint.setTextSize(20);
				
				paint.setColor(Color.WHITE);

				ArrayList<Float> values = new ArrayList<Float>();
				float min= ColorUtil.Value610(bmp.getPixel(left+10, top+10)),
						max=0;
				for (int i = left; i < right; i++) {
					float sum = 0;
					for (int j = top; j <= bottom; j++) {//画的绿色框会干扰的吧
						sum += ColorUtil.Value610(bmp.getPixel(i, j));
						sum += ColorUtil.Value610(bmp.getPixel(i - 1, j));
						sum += ColorUtil.Value610(bmp.getPixel(i + 1, j));
						sum += ColorUtil.Value610(bmp.getPixel(i - 2, j));
						sum += ColorUtil.Value610(bmp.getPixel(i + 2, j));
					}
					float a=sum / ((bottom - top) * 5);
					
					
					if(a>max) {
						max=a;
					}else if(a<min) {
						min=a;
					}
					values.add(a);
				}
				
//				ArrayList<Float> old=values;	//再进行下面的计算，相当于 
//				values=new ArrayList<>();
//				values.add((old.get(0)+old.get(1))/2);
//				values.add((old.get(0)+old.get(1)+old.get(2))/3);
//				for(int i=2;i<old.size()-1;i++) {
//					values.add((old.get(i-2)+old.get(i-1)+old.get(i)+old.get(i+1))/4f);
//				}
//				values.add((old.get(old.size()-2)+old.get(old.size()-1)+old.get(old.size()-2))/3);
				
				
				
				paint.setColor(Color.GREEN);
//				canvas.drawRect(left-20, top-10, right+20, bottom+20, paint);// 画个方框
				canvas.drawRect(left, top, right, bottom, paint);// 画个方框
				
//				Bitmap card = Bitmap.createBitmap(bmp, left, top, right - left, bottom - top);
				
				CurveUtil.showPeak(values, iv, canvas, paint,data);
				
				
//				c.drawBitmap(card, 1, 1, paint);
				LogUtil.d("doinbackground over");
				return null;
			}
			
			
			protected void onPostExecute(Void result) {
				iv.setImageBitmap(bmp);
				btUVC.setEnabled(true);
//				ComM.get().send(LED_OFF );
			};
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		
		
	}

	
	private Void calculate() {
		
		
		
		return null;
	}
	
	
	private int initCam() {
		for (int i = 0; i < 2; i++) {
			PermissionUtil.chmodPermisson("/dev/video" + i);
			int fd = UVC.prepareCamera(i); //1-31改造为返回fd

			LogUtil.d("fd=" + fd);
			if (fd> 0) {
				fds.add(fd);
			}
		}
		return fds.size();
	}
	
	
	
	
	private void initLED() {
		gpio = GpioUtils.getGpioUtils();
		io = gpio.getGpioPin('L',4);//PL4 /7/6 GND
	}
	
	public void release() {
		for(int fd:fds) {
			UVC.stopCamera(fd);//防止下次打开不行了。
		}
		fds.clear();
//		ComM.get().send(LED_OFF );
		
		
		LED=false;
//		bmp.recycle();
	}

	public void setBrightness(int i, int value) {
		UVC.setBrightness(fds.get(i),value);
	}
	public void setExposure(int i, int value) {
		UVC.setExposure(fds.get(i),value);
	}

	public void processCamera(int i) {
		UVC.processCamera(fds.get(0));
	}
	
	
	
}
