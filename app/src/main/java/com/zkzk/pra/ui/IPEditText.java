package com.zkzk.pra.ui;

import java.util.Arrays;

import com.zkzk.pra.R;
import com.zkzk.pra.utils.ExceptionHandler;
import com.zkzk.pra.utils.ToastUtil;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView.BufferType;
import top.jemen.utils.LogUtil;
import android.widget.Toast;

public class IPEditText extends LinearLayout {
    //控件
    private EditText Edit1;
    private EditText Edit2;
    private EditText Edit3;
    private EditText Edit4;
    //文本
    private String text;
    private String text1;
    private String text2;
    private String text3;
    private String text4;

    public IPEditText(final Context context, AttributeSet attrs) {
        super(context, attrs);
        //初始化界面
        try {
			View view =LayoutInflater.from(context).inflate(R.layout.iptext, this);
			//绑定
			Edit1=(EditText)view.findViewById(R.id.edit1);
			Edit2=(EditText)view.findViewById(R.id.edit2);
			Edit3=(EditText)view.findViewById(R.id.edit3);
			Edit4=(EditText)view.findViewById(R.id.edit4);
			Edit1.setTextSize(25);
			Edit2.setTextSize(25);
			Edit3.setTextSize(25);
			Edit4.setTextSize(25);
			//初始化函数
			init(context);
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
    }

    private void init(final Context context) {
        try {
			/**
			 * 监听文本，得到ip段，自动进入下一个输入框
			 */
			Edit1.addTextChangedListener(new TextWatcher() {
			    @Override
			    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			    }
			    @Override
			    public void onTextChanged(CharSequence s, int start, int before, int count) {
			        text1 = s.toString().trim();
			        if (s.length() > 2 ) {
			            if (Double.parseDouble(text1) > 255) {
			            	
			                ToastUtil.showText( "请输入合法的ip地址",Toast.LENGTH_LONG);
			            }else{
			                Edit2.setFocusable(true);
			                Edit2.requestFocus();
			            }
			        }
			    }
			    @Override
			    public void afterTextChanged(Editable s) {
			    }
			});

			Edit2.addTextChangedListener(new TextWatcher() {
			    @Override
			    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			    }
			    @Override
			    public void onTextChanged(CharSequence s, int start, int before, int count) {
			        text2 = s.toString().trim();
			        if (s.length()> 2) {
			            if (Integer.parseInt(text2) > 255) {
			            	 ToastUtil.showText(  "请输入合法的ip地址",Toast.LENGTH_LONG);
			            }else{
			                Edit3.setFocusable(true);
			                Edit3.requestFocus();
			            }
			        }
			        /**
			         * 输入框为空，删除按键则返回上一个文本框
			         */
			        /*
			        if (start == 0 && s.length() == 0) {
			            Edit1.setFocusable(true);
			            Edit1.requestFocus();
			        }
			        */
			    }

			    @Override
			    public void afterTextChanged(Editable s) {

			    }
			});

			Edit3.addTextChangedListener(new TextWatcher() {
			    @Override
			    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			    }

			    @Override
			    public void onTextChanged(CharSequence s, int start, int before, int count) {
			        text3 = s.toString().trim();
			        if (s.length()> 2 ) {

			            if (Integer.parseInt(text3) > 255) {
			            	 ToastUtil.showText( "请输入合法的ip地址",Toast.LENGTH_LONG);
			            }else{
			                Edit4.setFocusable(true);
			                Edit4.requestFocus();
			            }
			        }
			        /**
			         * 当用户需要删除时,此时的EditText为空时,上一个EditText获得焦点
			         */
			        /*
			        if (start == 0 && s.length() == 0) {
			            Edit2.setFocusable(true);
			            Edit2.requestFocus();
			        }
			        */
			    }

			    @Override
			    public void afterTextChanged(Editable s) {

			    }
			});

			Edit4.addTextChangedListener(new TextWatcher() {
			    @Override
			    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			    }

			    @Override
			    public void onTextChanged(CharSequence s, int start, int before, int count) {
			        text4 = s.toString().trim();
			        if (s.length() > 2 ) {

			            if (Integer.parseInt(text4) > 255) {
			            	 ToastUtil.showText( "请输入合法的ip地址",Toast.LENGTH_LONG);
			            }
			        }
			        /**
			         * 当用户需要删除时,此时的EditText为空时,上一个EditText获得焦点
			         */
			        /*
			        if (start == 0 && s.length() == 0) {
			            Edit3.setFocusable(true);
			            Edit3.requestFocus();
			        }
			        */
			    }

			    @Override
			    public void afterTextChanged(Editable s) {

			    }
			});

			/**
			 *  监听控件，空值时del键返回上一输入框
			 */
			Edit2.setOnKeyListener(new OnKeyListener() {
			    @Override
			    public boolean onKey(View v, int keyCode, KeyEvent event) {
			        if(text2==null||text2.isEmpty()){
			            if (keyCode == KeyEvent.KEYCODE_DEL) {
			                try {
								Edit1.setFocusable(true);
								Edit1.requestFocus();
								int index=Edit1.getText().toString().length();
				                if(index>0)
				                	Edit1.setSelection(index);
							} catch (Exception e) {
								ExceptionHandler.handleException(e);
							}
			            }
			        }
			        return false;
			    }
			});
			Edit3.setOnKeyListener(new OnKeyListener() {
			    @Override
			    public boolean onKey(View v, int keyCode, KeyEvent event) {
			        if(text3==null||text3.isEmpty()){
			            if (keyCode == KeyEvent.KEYCODE_DEL) {
			                try {
								Edit2.setFocusable(true);
								Edit2.requestFocus();
								int index=Edit2.getText().toString().length();
								if(index>0)
									Edit2.setSelection(index);
							} catch (Exception e) {
								ExceptionHandler.handleException(e);
							}
			            }
			        }
			        return false;
			    }
			});
			Edit4.setOnKeyListener(new OnKeyListener() {
			    @Override
			    public boolean onKey(View v, int keyCode, KeyEvent event) {
			        if(text4==null||text4.isEmpty()){
			            if (keyCode == KeyEvent.KEYCODE_DEL) {
			                try {
								Edit3.setFocusable(true);
								Edit3.requestFocus();
								int index=Edit3.getText().toString().length();
								LogUtil.d("index="+index);
								if(index>0)
									Edit3.setSelection(index);
							} catch (Exception e) {
								ExceptionHandler.handleException(e);
							}
			            }
			        }
			        return false;
			    }
			});
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
    }

    /**
     *
     * 成员函数，返回整个ip地址,如果有一个字节为空则返回空串""
     */
    public String getText(){
        if (TextUtils.isEmpty(text1) || TextUtils.isEmpty(text2)
                || TextUtils.isEmpty(text3) || TextUtils.isEmpty(text4)) {
            text="";
        }else {
            text= text1 + "." + text2 + "." + text3 + "." + text4;
        }
        return text;
    }
    
    public void setText(String ipOrMask) {
    	if(TextUtils.isEmpty(ipOrMask))  return;
    	String[] ips=ipOrMask.split("\\.");	//正则表达式中.代表除\n和\r之外的任意字符，所以需要转义
    	
//    	LogUtil.d("set ip "+ipOrMask+Arrays.toString(ips));
    	if(ips.length>=1) {	Edit1.setText(ips[0]);
	    	if(ips.length>=2) {	Edit2.setText(ips[1]);
	    		if(ips.length>=3)	Edit3.setText(ips[2]);
	    			if(ips.length>=4)	Edit4.setText(ips[3]);
	    	}
    	}
    }
    
    public void setEnable(boolean enabled) {
    	Edit1.setEnabled(enabled);
    	Edit2.setEnabled(enabled);
    	Edit3.setEnabled(enabled);
    	Edit4.setEnabled(enabled);
    }

	public void setText(String ipOrMask, BufferType editable) {
    	if(TextUtils.isEmpty(ipOrMask))  return;
    	String[] ips=ipOrMask.split("\\.");	//正则表达式中.代表除\n和\r之外的任意字符，所以需要转义
    	if(ips.length>=1) {	Edit1.setText(ips[0],editable);
	    	if(ips.length>=2) {	Edit2.setText(ips[1],editable);
	    		if(ips.length>=3)	Edit3.setText(ips[2],editable);
	    			if(ips.length>=4)	Edit4.setText(ips[3],editable);
	    	}
    	}
		
	}

}