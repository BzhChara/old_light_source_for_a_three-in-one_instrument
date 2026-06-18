package com.zkzk.pra.ui;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import com.zkzk.pra.utils.Tools;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.TextView;

public class Clock extends TextView {
    Calendar mCalendar;
    public final static String m12 = "h:mm:ss aa";
    public final static String m24 = "k:mm:ss";
    private Runnable mTicker;
    private Handler mHandler;

    private boolean mTickerStopped = false;

    String mFormat="k:mm:ss";
    
    
	public Clock(Context context, AttributeSet attrs) {
		super(context, attrs);
		initClock(context);
	}

	public Clock(Context context) {
		super(context);
		initClock(context);
	}
	
	
	public void stop() {
		
	}
	
	 private void initClock(Context context) {
	        Resources r = context.getResources();

	        if (mCalendar == null) {
//	            mCalendar = Calendar.getInstance();//没有生效。
//	        	mCalendar=Calendar.getInstance(Locale.CHINA);//并没有生效
	            mCalendar=Calendar.getInstance(Tools.getTimeZone());
	        }
	    }

	    @Override
	    protected void onAttachedToWindow() {
	        mTickerStopped = false;
	        super.onAttachedToWindow();
	        mHandler = new Handler();

	        /**
	         * requests a tick on the next hard-second boundary
	         */
	        mTicker = new Runnable() {
	                public void run() {
	                    if (mTickerStopped) return;
	                    mCalendar.setTimeInMillis(System.currentTimeMillis());
	                    setText(DateFormat.format(mFormat, mCalendar));
	                    invalidate();
	                    long now = SystemClock.uptimeMillis();
	                    long next = now + (1000 - now % 1000);
	                    mHandler.postAtTime(mTicker, next);
	                }
	            };
	        mTicker.run();
	    }

	    @Override
	    protected void onDetachedFromWindow() {
	        super.onDetachedFromWindow();
	        mTickerStopped = true;
	    }

	    /**
	     * Pulls 12/24 mode from system settings
	     */
	    private boolean get24HourMode() {
	        return android.text.format.DateFormat.is24HourFormat(getContext());
	    }

	    private void setFormat() {
	        if (get24HourMode()) {
	            mFormat = m24;
	        } else {
	            mFormat = m12;
	        }
	    }
}
