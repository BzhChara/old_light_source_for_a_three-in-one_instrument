package com.whswzz.prfluroanalyzer.fluoro.data;

import java.text.SimpleDateFormat;
import java.util.List;

import com.whswzz.prfluroanalyzer.fluoro.entity.FluData;
import com.whswzz.prfluroanalyzer.fluoro.entity.Hump;
import com.whswzz.prfluroanalyzer.param.Params;
import com.zkzk.pra.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import top.jemen.ui.BaseDialog;
import top.jemen.utils.Curve;
import top.jemen.utils.CurveUtil;
import top.jemen.utils.LogUtil;
import top.jemen.utils.QRCodeUtil;

public class OriginalDialog extends BaseDialog {
	private Context context;
	private ImageView iv;
	private int width = 800, height = 600;

	private FluData data;

	public OriginalDialog(Context context, FluData data, int width, int height) {
		super(context, R.style.dialog);
		this.context = context;
		this.data = data;
		this.width = width;
		this.height = height;
	}

	public OriginalDialog(Context context, FluData data) {
		super(context, R.style.dialog);
		this.context = context;
		this.data = data;
	}

	Bitmap bitmap;
	private Paint paint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		iv = new ImageView(context);
		LayoutParams params = new LayoutParams(width, height);
		iv.setLayoutParams(params);
		setContentView(iv);

		paint = new Paint();
		paint.setColor(Color.RED);
		paint.setStyle(Style.STROKE);
		paint.setAntiAlias(true);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (null == data || data.getValues() == null) {
			return;
		}
		bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawRGB(204, 233, 140);
		showPeak(data.getValues(), canvas, data);
		iv.setImageBitmap(bitmap);

	}
	
	
	
	
	
	private void showPeak(List<Float> values, Canvas canvas, final FluData data) {
		LogUtil.d("showPeak " + Thread.currentThread().getName());
		int size = values.size();
		LogUtil.d("size=" + size);
		int W =canvas.getWidth(), H = canvas.getHeight();
		canvas.drawColor(Color.WHITE);
		float left=W*0.002f,top=H*0.025f,right=W-left*2,bottom=H-top*2;
		paint.setTextSize(H*0.05F);
		
		Float min = values.get(0);
		Float max = min;
		for (Float x : values) {
			if (x > max) {
				max = x;
			} else if (x < min) {
				min = x;
			}
		}
		float ppp = (right-left) / values.size();// pixels per point

		float distance = (max - min) >CurveUtil.MIN_DISTANCE ? (max - min) : CurveUtil.MIN_DISTANCE;
		
		paint.setStyle(Style.STROKE);
		
		paint.setColor(Color.BLACK);
		canvas.drawRect(left, top, right, bottom, paint);
		paint.setColor(Color.RED);
		Path path = new Path();
		float y = (bottom - 50 - (bottom - top - 100) * (values.get(0) - min) / distance);
		path.moveTo(0, y);
		for (int i = 1; i < values.size(); i++) {
			float x = left+i * ppp ;
			y = (bottom - 50 - (bottom - top - 100) * (values.get(i) - min) / distance);
//			LogUtil.d("y" + i + "=" + y + ",   v=" + values.get(i));
			path.lineTo(x, y);
		}
		canvas.drawPath(path, paint);
		paint.setStyle(Style.FILL_AND_STROKE);
		
		Hump hump=data.getHump();
		int a=hump.getA(),b=hump.getB(),c=hump.getC(),ca=hump.getCa(),cb=hump.getCb(),cc=hump.getCc();
		float k=(values.get(c) - values.get(a))  / (c - a);
		float area = 0;
		paint.setStrokeWidth(3);
		for(int i=a;i<c;i++) { //画峰面积
			float vbi = values.get(a) + (i - a)*k;// B点基线
			area+= values.get(i) - vbi;
			final float yi = (int) (bottom - 50 - (bottom - top - 100) * (values.get(i) - min) / distance);
			final float ybi = (int) (bottom - 50 - (bottom - top - 100) * (vbi - min) / distance);
			canvas.drawLine(i * ppp, yi, i * ppp, ybi, paint);
		}
		
		 k=(values.get(cc) - values.get(ca))  / (cc - ca);
			float areaC = 0;
			for(int i=ca;i<cc;i++) {//画峰面积
				float vi = values.get(ca) + (i - ca)*k;// B点基线
				areaC+= values.get(i) - vi;
				final float yi = (int) (bottom - 50 - (bottom - top - 100) * (values.get(i) - min) / distance);
				final float ybi = (int) (bottom - 50 - (bottom - top - 100) * (vi - min) / distance);
				canvas.drawLine(i * ppp, yi, i * ppp, ybi, paint);
			}
			paint.setStrokeWidth(1);
			
			float textH = paint.descent() - paint.ascent();
			
			float vbb = values.get(a) + (b - a)*k;// B点基线
			
			float vcbb = values.get(a) +(cb - a)*k;// B点基线
			final float yb = (int) (bottom - 50 - (bottom - top - 100) * (values.get(b) - min) / distance);
			final float ycb = (int) (bottom - 50 - (bottom - top - 100) * (values.get(cb) - min) / distance);
			canvas.drawText(String.format("%.3f", data.getT()), b * ppp-30, yb - 13, paint);
			canvas.drawText(String.format("%.3f", data.getC()), cb * ppp-30, ycb - 13, paint);
			
			String result=data.getResult();
			float textW=paint.measureText(result);
			canvas.drawText(data.getResult(), right - textW, top + textH, paint);
			if(Params.DEBUG&&data.getC()!=0) {
				String textTC = "T/C : " + String.format("%.3f", data.getT()/data.getC());;
				canvas.drawText(textTC, left+10, top + textH, paint);
			}
	}
	
	
	
	
	
	
	
	
	
	
	
	

	@Override
	protected void onStop() {
		super.onStop();
		if (null != bitmap) {
			bitmap.recycle();
		}
	}

}
