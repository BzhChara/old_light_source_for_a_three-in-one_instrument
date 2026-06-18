package com.zkzk.pra.ui;

import android.R;
import android.R.color;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import top.jemen.utils.LogUtil;

public class BlackView extends View{
	private Paint paint;
//	private static final String[] MARK= {"中科志康","科技创新","志在民康","zkzkbio.com"};
	private static final String[] MARK= {"武汉农科院","环境与安全研究所","研发制造","服务三农","wuhanagri.com"};
	private static final int[] COLORS= {Color.WHITE,Color.YELLOW,Color.RED,Color.BLUE,Color.GREEN,Color.CYAN
			,Color.MAGENTA, 0xFF33aadd,0xFF64A835,0xFF3677B1};//后两种是中科志康LOGO的颜色
	private Handler handler;
	private Runnable runnable;
	
	public BlackView(Context context) {
		super(context);
		paint=new Paint();
		paint.setTextSize(120);
		paint.setStyle(Style.FILL);
		paint.setTextAlign(Align.LEFT);
		paint.setAntiAlias(true);
		setLayerType(View.LAYER_TYPE_HARDWARE, null); //硬件加速
		handler=new Handler();
		runnable=new Runnable() {
			@Override
			public void run() {
				BlackView.this.postInvalidate();
			}
		};
	}
	int i;
	float x,y,w,h;
	private boolean xadd=true,yadd=true;
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.BLACK);
		String text=MARK[i%MARK.length];//不要在一次循环中让i自增两次。
		paint.setColor(COLORS[i++%COLORS.length]);
		float textH = paint.descent() - paint.ascent();
		float textW = paint.measureText(text) + 8;
		w=getWidth();h=getHeight();
//		LogUtil.d("blackview draw,width="+w+",height="+h);
		canvas.drawText(text, x, y, paint);
		FlowerTools.draw(canvas, paint, w-x, h-y,500);
//		BitmapDrawable drawable = (BitmapDrawable)getResources().getDrawable(R.drawable.ico);
//		canvas.drawBitmap(drawable.getBitmap(), w-x, h-y, paint);
		if(xadd) {
			x+=40;
			if(x>w-textW) {
				xadd=false;
				x-=x+textW-w;
			}
		}else {
			x-=40;
			if(x<0) {
				xadd=true;
				x+=-x;
			}
		}
		if(yadd) {
			y+=40;
			if(y>h) {
				yadd=false;
				y-=y-h;
			}
		}else {
			y-=40;
			if(y<textH) {
				yadd=true;
				y+=textH-y;
			}
		}
		handler.postDelayed(runnable, 3000);
	}
	
	
	
	
	
	
}
