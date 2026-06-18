package com.whswzz.prfluroanalyzer.enzyme;

import com.whswzz.prfluroanalyzer.model.DataModel;
import com.zkzk.pra.R;
import com.zkzk.pra.utils.ToastUtil;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import top.jemen.ui.BaseDialog;

public class EnzymeDialog extends BaseDialog implements android.view.View.OnClickListener{
	private Button btOk;
	private EditText etTime,etTemp;
	
	public EnzymeDialog(Context context) {
		super(context,R.style.dialog);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_enzyme);
		init();
		setListeners();
	}
	
	private void init() {
		btOk=(Button) findViewById(R.id.bt_ok);
		etTime=(EditText) findViewById(R.id.et_time);
		etTemp=(EditText) findViewById(R.id.et_temp);
	}
	
	
	private void setListeners() {
		btOk.setOnClickListener(this);
	}
	
	
	@Override
	public void onClick(View v) {
		String sTime = etTime.getText().toString();
		if(TextUtils.isEmpty(sTime)) {
			ToastUtil.showText("请输入设定的时间值", Toast.LENGTH_SHORT);
			return;
		}
		String sTemp=etTemp.getText().toString();
		if(TextUtils.isEmpty(sTemp)) {
			ToastUtil.showText("请输入设定的温度值", Toast.LENGTH_SHORT);
			return;
		}
		float time=Float.parseFloat(sTime);
		float temp=Float.parseFloat(sTemp);
		DataModel.getInstance().setEnzymeTemp(temp);
		this.cancel();
	}
}
