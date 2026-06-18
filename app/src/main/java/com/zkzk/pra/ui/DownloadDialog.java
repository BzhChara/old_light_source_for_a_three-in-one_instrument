package com.zkzk.pra.ui;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.zkzk.pra.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DownloadDialog extends BaseDialog {
    private OnCustomDialogListener customDialogListener;
	private Context mContext;
	private String title,msg;
	private TextView tvMsg,tvPercent;
	private  ProgressBar pb;
	private ImageButton ibClose;
	
    public DownloadDialog(Context context) {
    	super(context,R.style.dialog);
    	mContext=context;
	}
	public DownloadDialog(Context context, String title,String msg) {
        super(context,R.style.dialog);
        mContext=context;
        this.title=title;
        this.msg=msg;
    }
	
	public DownloadDialog(Context context,int titleId,int msgId) {
        super(context,R.style.dialog);
        mContext=context;
        this.title=MyApp.getApp().getString(titleId);
        this.msg=MyApp.getApp().getString(msgId);
    }
	
    public void setTitle(String title) {
    	this.title=title;
    }
    
    public void setMessate(String msg) {
    	this.msg=msg;
    	if(null!=tvMsg) tvMsg.setText(msg);
    }
    
    
    public void setProgress(int total,int current) {
    	pb.setMax(total);
    	pb.setProgress(current);
    	tvPercent.setText(current*100/total+"%");
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
        setContentView(R.layout.dialog_download);
        init();
        set();
        
    }
	private void set() {
		if(null!=msg)	tvMsg.setText(msg);
	}
	private void init() {
		tvMsg=(TextView) findViewById(R.id.tv_prodialog_msg);
		tvPercent=(TextView) findViewById(R.id.tv_timer);
		ibClose=(ImageButton) findViewById(R.id.ib_close);
		ibClose.setVisibility(View.INVISIBLE);//***********************底板没有升级，暂且不启用。
		pb=(ProgressBar) findViewById(R.id.pb);
		pb.setProgress(0);
	}
	

	public void setMessate(int msgId) {
		this.setMessate(mContext.getString(msgId));
	}
	
}

