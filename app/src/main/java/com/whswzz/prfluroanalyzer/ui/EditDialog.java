package com.whswzz.prfluroanalyzer.ui;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.zkzk.pra.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import top.jemen.ui.BaseDialog;

public class EditDialog extends BaseDialog {
    private Button cancelButton;
    private Button okButton;
    private EditListener editListener;
	private Context mContext;
	private String title;
	private TextView tvTitle,tvMsg;
	private String msg;
	private EditText et;
	private int etType=EditorInfo.TYPE_NULL;
	
    public EditDialog(Context context) {
    	super(context,R.style.dialog);
	}
	public EditDialog(Context context, EditListener customListener,String title,String msg) {
        super(context,R.style.dialog);
        mContext=context;
        editListener = customListener;
        this.title=title;
        this.msg=msg;
    }
	public EditDialog(Context context, EditListener customListener,int titleId,int msgId) {
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
//    public abstract class EditListener {
//       public abstract void back();
//       public void cancel() {};
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        
        setContentView(R.layout.dialog_edit);
        tvTitle = (TextView) findViewById(R.id.tv_dialog_title);
        tvMsg=(TextView) findViewById(R.id.tv_edit);
        cancelButton = (Button)findViewById(R.id.bt_dialog_cancel);
        okButton = (Button)findViewById(R.id.bt_dialog_ensure);
        et=(EditText) findViewById(R.id.et_edit);
        et.setInputType(etType);
        cancelButton.setOnClickListener(buttonDialogListener);
        okButton.setOnClickListener(buttonDialogListener);
        if(null!=title)
        	tvTitle.setText(title);
        if(null!=msg)
        	tvMsg.setText(msg);
        
    }

    
    /**
     * Set the type of the content with a constant as defined for {@link EditorInfo#inputType}. This
     * will take care of changing the key listener, by calling {@link #setKeyListener(KeyListener)},
     * to match the given content type.  If the given content type is {@link EditorInfo#TYPE_NULL}
     * then a soft keyboard will not be displayed for this text view.
     *
     * Note that the maximum number of displayed lines (see {@link #setMaxLines(int)}) will be
     * modified if you change the {@link EditorInfo#TYPE_TEXT_FLAG_MULTI_LINE} flag of the input
     * type.
     *
     * @see #getInputType()
     * @see #setRawInputType(int)
     * @see android.text.InputType
     * @attr ref android.R.styleable#TextView_inputType
     */
    public void setInputType(int type) {
    	if(null!=et) {
    		et.setInputType(type);
    	}else {
    		etType=type;
    	}
    }
    
    public EditDialog setEdit(String s) {
    	if(null!=s) {
    		et.setText(s);
    		et.setSelection(s.length());
    	}
    	return this;
    }
    
    private View.OnClickListener buttonDialogListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.bt_dialog_cancel) {
                cancel();// 自动调用dismiss();
            } else {
                if(null!=editListener) editListener.back(et.getText().toString());
                dismiss();
            }
        }
    };
    
    
   public interface EditListener{
    	public abstract void back(String text);
//        public void cancel() {} ;
    }
    

}