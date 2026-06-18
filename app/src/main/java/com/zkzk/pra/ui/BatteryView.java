package com.zkzk.pra.ui;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.zkzk.pra.R;
import com.zkzk.pra.utils.Tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

@SuppressLint("DrawAllocation")
public class BatteryView extends View {
    private Context mContext;
    private float width;
    private float height;
    private Paint mPaint, textPaint;
    private float powerQuantity = 0.5f;// 电量
    private boolean isCharge = false, isAC = false, isErr = false; //充电和AC两个是独立的状态。
    private Bitmap bitmap;
    private Handler handler;
    private Runnable runnable;
    private Path path;
    private boolean empty, disappear = false;
    private RectF rectFrame;


    public BatteryView(Context context, Context mContext) {
        super(context);
        this.mContext = mContext;
        init();
    }


    public BatteryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public BatteryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        mPaint = new Paint();
        textPaint = new TextPaint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(18);
        textPaint.setTextAlign(Paint.Align.CENTER);
        bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.charge);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
        path = new Path();
        rectFrame = new RectF(0, 0, width * 0.88f, height);
        mPaint.setStrokeWidth(3);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 绘制界面
        super.onDraw(canvas);
        if (empty) {
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 1000);    //仅仅会导致重新绘制.
            if (disappear) {
                disappear = false;
                return;
            } else {
                disappear = true;
            }
        }

//		canvas.drawBitmap(bitmap, 0, 0, mPaint);
//		if(isCharge) {
//			Tools.drawImage(canvas, bitmap, 0, 0, (int)width,(int) height, 0, 0);	//此函数会对图片大小作调整
//		}else {


        if (powerQuantity >= 0 && powerQuantity <= 0.2 && !isCharge) {
            mPaint.setColor(Color.RED);
        } else {
            mPaint.setColor(Color.GREEN);
        }
        // 计算绘制电量的区域
        float right = width * (0.07f + 0.82f * powerQuantity);
        float left;
        if (Tools.isSMDK())
            left = width * 0.07f;
        else
            left = width * 0.05f;
        float top = height * 0.1f;
        float bottom = height * 0.95f;
        canvas.drawRect(left, top, right, bottom, mPaint);
        ;//绘制显示电量矩形


        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Style.STROKE);
        mPaint.setStrokeWidth(4);
//			canvas.drawRect(0, 0, width*0.9f, height, mPaint);	//电池体的矩形
        canvas.drawRoundRect(rectFrame, 8, 8, mPaint);
        mPaint.setStyle(Style.FILL);
        canvas.drawRect(width * 0.85f, height * 0.35f, width, height * 0.65f, mPaint);//正极奶头的矩形


		if(isErr){
			mPaint.setColor(Color.RED);
			mPaint.setStyle(Style.FILL_AND_STROKE);
			canvas.drawLine(width*0.1f,top,width*0.8f,bottom,mPaint);
			canvas.drawLine(width*0.1f,bottom,width*0.8f,top,mPaint);
		}

        if (isAC) {
            //绘制闪电符号
            path.reset();
            path.moveTo(width * 0.14f, height * 0.5f);
            path.lineTo(width * 0.504f, height * 0.75f);
            path.lineTo(width * 0.504f, height * 0.5f);
            path.lineTo(width * 0.760f, height * 0.5f);
            path.lineTo(width * 0.396f, height * 0.25f);
            path.lineTo(width * 0.396f, height * 0.5f);
            path.lineTo(width * 0.16f, height * 0.5f);
            mPaint.setColor(Color.WHITE);
            canvas.drawPath(path, mPaint);
            if (isCharge) {
                powerQuantity += 0.1;
                if (powerQuantity > 1.05) powerQuantity = 0;
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 300);
            }
        } else {
            //绘制百分比文本,因为电量显示不准确，后来就去掉了。
            //		if(powerQuantity>=0.01) {
            //			 Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
            //			 	top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
            //		         bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom
            //			canvas.drawText((int)(powerQuantity*100)+"%", width/2, height/2- top/2 - bottom/2, textPaint);
            //		}
        }
//		}
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 计算控件尺寸
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    public void refreshPower(int power) {
        if (power == 200) {
            isAC = true;
            empty = false;
            isErr = false;
            if (isCharge) return; //不用调用invalidate
            isCharge = true;
        } else if (power == 300) {
            isAC = true;
            isCharge = false;
            powerQuantity = 1;
            empty = false;
            isErr = false;
        } else if (power == -1 || power == -2) {
            isAC = false;
            isCharge = false;
            isErr = true;
        } else {
            isAC = false;
            isCharge = false;
            isErr = false;
            powerQuantity = power / 100f;
            if (powerQuantity > 1.0f)
                powerQuantity = 1.0f;

            if (powerQuantity <= 0.02) {
                if (empty) return;        //防止多次调用invalidate
                empty = true;        //如果再调用invalidate（）此将会导致开启线程，想办法阻止其开启多个线程

            } else {
                empty = false;
            }
        }
        invalidate();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        rectFrame.set(0, 0, w * 0.88f, h);
    }

}
