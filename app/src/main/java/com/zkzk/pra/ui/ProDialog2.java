package com.zkzk.pra.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.zip.DataFormatException;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.zkzk.pra.R;
import com.zkzk.pra.utils.Tools;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import top.jemen.utils.LogUtil;

public class ProDialog2 extends BaseDialog {
	private TextView tvTimer,tvMsg;
	private LinearLayout llTimer;
	private int timer;
	private int total;	//计算剩余时间尚且需要。
	private RelativeLayout rlRoot;
	private Button btCancel;
	private CancelListener cancelListener;
	private ProView proV;
	private LayoutParams lp;
	private Window window;
    public ProDialog2(Context context) {
    	super(context,R.style.dialog);
	}
	public ProDialog2(Context context,CancelListener cancelListener) {
		super(context,R.style.dialog);
        this.cancelListener=cancelListener;
    }
	
    /**
     * 定义dialog的回调事件
     */
    public interface OnCustomDialogListener {
        void back();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_dialog2);
        init();
        set();
        setListeners();
    }
	private void setListeners() {
		btCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(null!=cancelListener)
					cancelListener.onCancel();
			}
		});
	}
	
	float x0 = 0,y0 = 0;	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch(event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			x0=event.getX();
			y0=event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			float x = event.getX();
			float y = event.getY();
			if(Math.abs(x-x0)>10||Math.abs(y-y0)>10) {
				lp.x=(int) (lp.x+x-x0);
				lp.y=(int) (lp.y+y-y0);
				window.setAttributes(lp);
//				x0=x;	//因为直接平移了坐标系，所以x0与y0均不需要改变了。否则将导致抖动。
//				y0=y;
			}
			break;
		case MotionEvent.ACTION_UP:
			
			break;
		}
		return super.onTouchEvent(event);
	}
	
	
	private void set() {

	}
	private void init() {
		llTimer=(LinearLayout) findViewById(R.id.ll_timer);
		tvTimer=(TextView) findViewById(R.id.tv_timer);
		tvMsg=(TextView) findViewById(R.id.tv_total_time);
		rlRoot=(RelativeLayout) findViewById(R.id.root);
		rlRoot.setVisibility(View.GONE);
		btCancel=(Button) findViewById(R.id.bt_cancel);
		proV=(ProView) findViewById(R.id.proV);
		mCalendar.setTimeZone(TimeZone.getTimeZone("GMT-12:00"));
		window = this.getWindow();
		lp = window.getAttributes();
		
//		window.setGravity(Gravity.LEFT);
		
	}
	
	public void setTimer(int t) {
		if(t>0) {
			this.timer=t;
			rlRoot.setVisibility(View.VISIBLE);
			llTimer.setVisibility(View.VISIBLE);
			mCalendar.setTimeInMillis(timer*1000);
			tvTimer.setText(DateFormat.format( "00：mm：ss",mCalendar));
			proV.setProgress(0);
			handler.removeCallbacks(runnable);
			handler.postDelayed(runnable,1000);
			total=t;
		}else {
			rlRoot.setVisibility(View.GONE);
			llTimer.setVisibility(View.GONE);
			this.dismiss();
		}
	}
	
	Handler handler=new Handler();
	private Calendar mCalendar=Calendar.getInstance();
	Runnable runnable=new Runnable() {
		@Override
		public void run() {
			if(timer>0) {
				mCalendar.setTimeInMillis(timer*1000);
				tvTimer.setText(DateFormat.format( "00：mm：ss",mCalendar));
				proV.setProgress(100*(total-timer)/total);
				timer--;
				handler.postDelayed(this, 999);
			}else {
				llTimer.setVisibility(View.GONE);
				rlRoot.setVisibility(View.INVISIBLE);
			}
		}
	};
	
	public interface CancelListener{
		void onCancel();
	}

	public void setMessate(String string) {
		tvMsg.setText(string);
	}
}

