package top.jemen.ui.view;

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

/**
 * 该方案基于UVC直接调用native函数的
 * @author Administrator
 *
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, Runnable {

	private static final boolean DEBUG = true;
	private static final String TAG="WebCam";
	protected Context context;
	private SurfaceHolder holder;
    Thread mainLoop = null;
	private Bitmap bmp=null;

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
    
	public CameraPreview(Context context) {
		super(context);
		this.context = context;
		if(DEBUG) Log.d(TAG,"CameraPreview constructed");
		setFocusable(true);
		
		holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);	
	}

	public CameraPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		if(DEBUG) Log.d(TAG,"CameraPreview constructed");
		setFocusable(true);
		
		holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);	
	}
	long t=0;
	
    @Override
    public void run() {
    	Thread.currentThread().setName("camera preview");
    	int x = 0;
    	t=System.currentTimeMillis();
    	int[] pps=new int[10];
        while (cameraExists&&!shouldStop) {
        	//obtaining display area to draw a large image
        	if(winWidth==0){
        		winWidth=this.getWidth();
        		winHeight=this.getHeight();

        		if(winWidth*3/4<=winHeight){
        			dw = 0;
        			dh = (winHeight-winWidth*3/4)/2;
        			rate = ((float)winWidth)/IMG_WIDTH;
        			rect = new Rect(dw,dh,dw+winWidth-1,dh+winWidth*3/4-1);
        		}else{
        			dw = (winWidth-winHeight*4/3)/2;
        			dh = 0;
        			rate = ((float)winHeight)/IMG_HEIGHT;
        			rect = new Rect(dw,dh,dw+winHeight*4/3 -1,dh+winHeight-1);
        		}
        	}
        	
        	// obtaining a camera image (pixel data are stored in an array in JNI).
//        	processCamera();
//        	// camera image to bmp
//        	pixeltobmp(bmp);
        	
        	UVCTool.get().processCamera(0);
        	UVC.pixeltobmp(bmp);
        	
            Canvas canvas = getHolder().lockCanvas();
            if (canvas != null)
            {
            	// draw camera bmp on canvas
            	long t1=System.currentTimeMillis();
            	canvas.drawColor(Color.BLACK);
            	canvas.drawBitmap(bmp,null,rect,null);
            	x++;
            	pps[x%10]=(int) (1000/(t1-t));
            	int p = 0;
            	for(int i:pps) {
            		p+=i;
            	}
           		canvas.drawText("   "+p/pps.length+"pps", 30,30, paint);
            	t=t1;
            	//直接在C层绘制多好。
            	
            	getHolder().unlockCanvasAndPost(canvas);
            }

            if(shouldStop){
            	shouldStop = false;  
            	break;
            }	        
        }
    }

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if(DEBUG) Log.d(TAG, "surfaceCreated");
		if(bmp==null){
			bmp = Bitmap.createBitmap(IMG_WIDTH, IMG_HEIGHT, Bitmap.Config.ARGB_8888);
		}
		// /dev/videox (x=cameraId + cameraBase) is used
//		int ret = prepareCameraWithBase(cameraId, cameraBase);
//		if(ret!=-1) cameraExists = true;
		
		UVCTool.get().init(new ACallback() {
			@Override
			public void onSuccess(Object obj) {
				cameraExists = true;
				new Thread(CameraPreview.this).start();
			}
		},null);
		
		
        mainLoop = new Thread(this);
        mainLoop.start();	
        paint=new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(30);
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if(DEBUG) Log.d(TAG, "surfaceChanged");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if(DEBUG) Log.d(TAG, "surfaceDestroyed");
		if(cameraExists){
			shouldStop = true;
			try{ 
				Thread.sleep(100); // wait for thread stopping
			}catch(Exception e){}
			UVCTool.get().release();
		}
	}   
}