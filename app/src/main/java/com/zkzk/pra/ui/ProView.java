package com.zkzk.pra.ui;

//import android.annotation.Widget;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

public class ProView extends View {
	private int progress;// 从0到100
	private int height;
	private int width;
	private int GRAY = 0XFF49464D; // 灰色
	private int CYAN = 0xff27A8AE; // 青色
	private Paint paint;
	private int pro=88;//百分制的进度。
	// private int paddingLeft;
	// private int paddingTop;
	// private int paddingRight;
	// private int paddingBottom;

	public ProView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ProView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ProView(Context context) {
		super(context);
		init();
	}

	private void init() {
		paint=new Paint();
		paint.setColor(CYAN);
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(30);
		paint.setStyle(Style.FILL);
		paint.setStrokeWidth(6);
		paint.setAntiAlias(true);
		
	}
	

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		width = getWidth();
		height = getHeight();
		float r = Math.min(width, height) / 2f;
		// paddingLeft = getPaddingLeft(); //为方便，就不去支持padding了。
		// paddingTop = getPaddingTop();
		// paddingRight = getPaddingRight();
		// paddingBottom = getPaddingBottom();
		paint.setStrokeWidth(6);
		for (int i = 0; i < 100; i += 5) {
			if(i>pro) {
				paint.setColor(GRAY);
			}else {
				paint.setColor(CYAN);
			}
			canvas.drawLine(r, 0, r, r /6, paint);
			canvas.rotate(18, r, r);
		}
		
		paint.setStrokeWidth(2);
		String text=pro+"%";
		float textH = paint.descent() - paint.ascent();
		float textW = paint.measureText(text) ;
		canvas.drawText(text, r, r+textH/4, paint);

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * 完成在wrp_content状态下给一个默认值。其实不用也还可以。
	 * 
	 * @param measureSpec
	 * @return
	 */
	private int measure(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = 100;
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}
	
	public void setProgress(int pro) {
		if(pro>100) {
			pro=100;
		}else if(pro<0) {
			pro=0;
		}
		this.pro=pro;
		invalidate();
	}
}
