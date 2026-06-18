package com.zkzk.pra.ui;

import com.zkzk.pra.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import top.jemen.utils.QRCodeUtil;

public class QRCodeDialog extends BaseDialog{
	private String title;
	private String msg;
	private ImageView ivQrcode;
	
	public QRCodeDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public QRCodeDialog(Context context, int theme) {
		super(context, R.style.dialog);
	}

	public QRCodeDialog(Context context) {
		super(context);
	}

	public QRCodeDialog(Context context, String title, String msg) {
		super(context,R.style.dialog);
		this.title = title;
		this.msg = msg;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_qrcode);
		ivQrcode=(ImageView) findViewById(R.id.iv_qr);
		
	}
	
	private void drawQRCode() {
		if(!TextUtils.isEmpty(msg)) {
			Bitmap qr=QRCodeUtil.getCodeBitmap(msg,500, 500);
			if(null!=qr)
				ivQrcode.setImageBitmap(qr);
		}
	}
	
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
		if(isShowing()) {
			drawQRCode();
		}
	}

	@Override
	public void show() {
		super.show();
		drawQRCode();
	}
}
