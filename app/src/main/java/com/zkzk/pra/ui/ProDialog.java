package com.zkzk.pra.ui;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.zkzk.pra.R;
import com.zkzk.pra.activity.DataActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import top.jemen.utils.LogUtil;

public class ProDialog extends BaseDialog {
    private OnCustomDialogListener customDialogListener;
	private Context mContext;
	private TextView tvTimer;
	private  ProgressBar pb;
	private int progress;
	private LinearLayout llTimer;
	private int timer;
	private int total;	//计算剩余时间尚且需要。
	private RelativeLayout rlRoot;
	private Button btCancel;
	private CancelListener cancelListener;
    public ProDialog(Context context) {
    	super(context,R.style.dialog);
    	mContext=context;
	}
	public ProDialog(Context context,CancelListener cancelListener) {
		super(context,R.style.dialog);
        mContext=context;
        this.cancelListener=cancelListener;
    }
	
    
    
    public ProDialog(CancelListener cancelListener, Activity activity, int alertId, int msgId) {
    	super(activity,R.style.dialog);
		
	}
	public void setProgress(int progress) {
    	this.progress=progress;
    	if(null!=pb) pb.setProgress(progress);
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
        setContentView(R.layout.progress_dialog);
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
	private void set() {

	}
	private void init() {
		llTimer=(LinearLayout) findViewById(R.id.ll_timer);
		tvTimer=(TextView) findViewById(R.id.tv_timer);
		rlRoot=(RelativeLayout) findViewById(R.id.root);
		pb=(ProgressBar) findViewById(R.id.pb);
		rlRoot.setVisibility(View.GONE);
		pb.setProgress(0);
		btCancel=(Button) findViewById(R.id.bt_cancel);
	}
	
	public void setTimer(int t) {
		if(t>0) {
			this.timer=t;
			rlRoot.setVisibility(View.VISIBLE);
			llTimer.setVisibility(View.VISIBLE);
			tvTimer.setText(timer+"s");
			handler.removeCallbacks(runnable);
			handler.postDelayed(runnable,1000);
			pb.setProgress(0);
			pb.setMax(t);
			total=t;
		}else {
			rlRoot.setVisibility(View.GONE);
			llTimer.setVisibility(View.GONE);
			this.dismiss();
		}
	}
	
	Handler handler=new Handler();
	Runnable runnable=new Runnable() {
		@Override
		public void run() {
			if(timer>0) {
				tvTimer.setText(timer+"s");
				pb.setProgress(total-timer);
				timer--;
				handler.postDelayed(this, 1000);
			}else {
				llTimer.setVisibility(View.GONE);
				rlRoot.setVisibility(View.INVISIBLE);
			}
		}
	};

	
	public interface CancelListener{
		void onCancel();
	}

}

