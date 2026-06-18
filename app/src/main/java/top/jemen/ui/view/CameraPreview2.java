package top.jemen.ui.view;

import java.io.IOException;

import com.whswzz.prfluroanalyzer.fluoro.uvc.CameraTool2;
import com.whswzz.prfluroanalyzer.fluoro.uvc.UVCTool;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import top.jemen.camera.UVC;
import top.jemen.interfaces.ACallback;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;

/**
 * 调用系统API 进行预览。
 * @author Administrator
 *
 */
public class CameraPreview2 extends SurfaceView implements SurfaceHolder.Callback {

	private static final boolean DEBUG = true;
	private static final String TAG="WebCam";
	protected Context context;
	private SurfaceHolder holder;

	private boolean cameraExists=false;
	private boolean shouldStop=false;
	
	static final int IMG_WIDTH=640;
	static final int IMG_HEIGHT=480;

	// The following variables are used to draw camera images.
    private int winWidth=0;
    private int winHeight=0;
    private Rect rect;
    private int dw, dh;
    private float rate;
    private Paint paint;
    private Camera camera;
    
	public CameraPreview2(Context context) {
		super(context);
		this.context = context;
		if(DEBUG) Log.d(TAG,"CameraPreview constructed");
		setFocusable(true);
		
		holder = getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);	
		holder.addCallback(this);
	}

	public CameraPreview2(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		if(DEBUG) Log.d(TAG,"CameraPreview constructed");
		setFocusable(true);
		
		holder = getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);	
		holder.addCallback(this);
		
	}
	
	long t=0;
	

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
//		if(DEBUG) Log.d(TAG, "surfaceCreated");
//		for(int i=0;i<Camera.getNumberOfCameras();i++) {
//			try {
//				camera=Camera.open();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			if(null!=camera) {
//				break;
//			}
//		}
//		if(null==camera) {
//			return;
//		}
//		try {
//			camera.setPreviewDisplay(holder);
//			camera.startPreview();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		CameraTool2.startPreview(holder);
		
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if(DEBUG) Log.d(TAG, "surfaceChanged");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if(DEBUG) Log.d(TAG, "surfaceDestroyed");
//		if(null!=camera) {
//			camera.stopPreview();
//			camera.release();
//		}
		CameraTool2.stopPreview();
	}
}