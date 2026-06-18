package com.zkzk.pra.ui;
import com.zkzk.pra.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PswDialog extends BaseDialog {
    private Button cancelButton;
    private Button okButton;
    private EditText pswEdit;
    private OnCustomDialogListener customDialogListener;
	private Context mContext;
	private String title,ensure;

    public PswDialog(Context context, OnCustomDialogListener customListener,String title) {
        super(context,R.style.dialog);
        mContext=context;
        customDialogListener = customListener;
        this.title=title;
    }
    
    public PswDialog(Context context, OnCustomDialogListener customListener,String title,String ensure) {
        super(context,R.style.dialog);
        mContext=context;
        customDialogListener = customListener;
        this.title=title;
        this.ensure=ensure;
    }

    /**
     * 定义dialog的回调事件
     */
    public interface OnCustomDialogListener {
        void back(String str);
    }

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_config_dialog);
        
//        setTitle("输入密码");
        TextView tvTitle = (TextView) findViewById(R.id.tv_dialog_wifi_title);
        pswEdit = (EditText)findViewById(R.id.et_dialog_wifi_psw);
        cancelButton = (Button)findViewById(R.id.bt_dialog_wifi_cancel);
        okButton = (Button)findViewById(R.id.bt_dialog_wifi_join);
        cancelButton.setOnClickListener(buttonDialogListener);
        okButton.setOnClickListener(buttonDialogListener);
        tvTitle.setText("请输入\""+title+"\"的密码");
        if(null!=ensure) {
        	okButton.setText(ensure);
        }
        
    }

    /**
     * should be called after the dialog showed.
     * @param s
     */
    public void setPswHint(String s) {
    	if(null!=pswEdit)
    	pswEdit.setHint(s);
    }
    
    private View.OnClickListener buttonDialogListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.bt_dialog_wifi_cancel) {
                pswEdit = null;
//                customDialogListener.back(null);
                cancel();// 自动调用dismiss();
            } else {
                customDialogListener.back(pswEdit.getText().toString());
                dismiss();
            }
        }
    };

    
    
    
    
}