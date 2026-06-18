package com.whswzz.prfluroanalyzer.settings.ui;

import java.io.ByteArrayOutputStream;

import com.zkzk.pra.ui.BaseDialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.Paint.Style;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import top.jemen.ui.view.NV21ToBitmap;
import top.jemen.utils.LogUtil;
import top.jemen.utils.QRCodeUtil;

public class QRCodeTestDialog extends BaseDialog{
	private Context context;
	public QRCodeTestDialog(Context context) {
		super(context);
		this.context=context;
	}
	
	private Camera camera;
	private ImageView iv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		 iv=new ImageView(context);
//		LayoutParams params=new LayoutParams(600, 600);
//		iv.setLayoutParams(params);
		iv.setScaleType(ScaleType.FIT_XY);
		iv.setBackgroundColor(Color.RED);
		LayoutParams p=new LayoutParams(800, 600);
		setContentView(iv,p);
		nv2bmp=new NV21ToBitmap(context);
		paint=new Paint();
		paint.setColor(Color.RED);
		paint.setStyle(Style.STROKE);
		paint.setTextSize(26);;
	}
	private NV21ToBitmap nv2bmp;
	private Paint paint;
	@Override
	public void show() {
		super.show();
		camera=Camera.open();
		if(null==camera) {
			LogUtil.e("打开摄像头失败");
			return;
		}
		camera.setPreviewCallback(new PreviewCallback() {
	
			int x=0;
			@Override
			public void onPreviewFrame(byte[] data, Camera camera) {
				if(x++%5!=0) {
					return;
				}
				
				Camera.Size previewSize=camera.getParameters().getPreviewSize();
//				YuvImage image = new YuvImage(data,ImageFormat.NV21,previewSize.width,previewSize.height,null);
//				ByteArrayOutputStream stream = new ByteArrayOutputStream();
//				image.compressToJpeg(new Rect(0,0,previewSize.width,previewSize.height),80,stream);
//				Bitmap bmp=BitmapFactory.decodeByteArray(stream.toByteArray(),0,stream.size());
				Bitmap bmp=nv2bmp.nv21ToBitmap(data, previewSize.width, previewSize.height);
//				LogUtil.d("bmp w:"+bmp.getWidth()+",h:"+bmp.getHeight());
				
				Canvas canvas=new Canvas(bmp);
				float l,t,b;
				l=previewSize.width*0.3f; 
				t=previewSize.height*0.2f;
				float r = previewSize.width*0.8f;
				b=previewSize.height*0.85f;
				canvas.drawRect(l,t, r,b, paint);
				Bitmap bmp1=Bitmap.createBitmap(bmp,(int)l, (int)t,(int)(r-l)	,(int)(b-t));
				
				String msg=QRCodeUtil.simpleDecode(bmp1);
//				String msg=QRCodeUtil.complexDecode(bmp1);
				canvas.drawText("识别："+msg, l, b+20, paint);
				iv.setImageBitmap(bmp);
				iv.postInvalidate();
				
			}
		});
		camera.startPreview();
	}
	
	@Override
	public void dismiss() {
		super.dismiss();
		if(null!=camera) {
			camera.stopPreview();
			camera.release();
			camera=null;
		}
	}

}
