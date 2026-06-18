package com.zkzk.pra.ui;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.zkzk.pra.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MyDialog extends BaseDialog {
    private Button cancelButton;
    private Button okButton;
    private BackListener customDialogListener;
	private Context mContext;
	private String title;
	private TextView tvTitle,tvMsg;
	private String msg;
	
	
    public MyDialog(Context context) {
    	super(context,R.style.dialog);
	}
	public MyDialog(Context context, BackListener backListener,String title,String msg) {
        super(context,R.style.dialog);
        mContext=context;
        customDialogListener = backListener;
        this.title=title;
        this.msg=msg;
    }
	public MyDialog(Context context, BackListener customListener,int titleId,int msgId) {
		this(context,customListener,context.getString(titleId),context.getString(msgId));
    }
    public void setTitle(String title) {
    	tvTitle.setText(title);
    }
    
    public void setMessate(String msg) {
    	tvMsg.setText(msg);
    }
    
    /**
     * 定义dialog的回调事件
     */
    public interface BackListener {
        void back();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        
        setContentView(R.layout.my_dialog);
        tvTitle = (TextView) findViewById(R.id.tv_dialog_title);
        tvMsg=(TextView) findViewById(R.id.tv_dialog_msg);
        cancelButton = (Button)findViewById(R.id.bt_dialog_cancel);
        okButton = (Button)findViewById(R.id.bt_dialog_ensure);
        cancelButton.setOnClickListener(buttonDialogListener);
        okButton.setOnClickListener(buttonDialogListener);
        tvTitle.setText(title);
        tvMsg.setText(msg);
    }

    
    
    private View.OnClickListener buttonDialogListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.bt_dialog_cancel) {
                cancel();// 自动调用dismiss();
            } else {
            	dismiss();
                if(null!=customDialogListener) customDialogListener.back();
            }
        }
    };

}