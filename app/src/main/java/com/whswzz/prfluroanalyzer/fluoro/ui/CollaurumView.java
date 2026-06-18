package com.whswzz.prfluroanalyzer.fluoro.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.consts.Consts;
import com.whswzz.prfluroanalyzer.fluoro.dal.imp.XDao;
import com.whswzz.prfluroanalyzer.fluoro.entity.FluData;
import com.whswzz.prfluroanalyzer.model.HttpModel;
import com.whswzz.prfluroanalyzer.settings.SetupActivity;
import com.whswzz.prfluroanalyzer.utils.PrinterJPW;
import com.zkzk.pra.R;
import com.zkzk.pra.activity.DetectActivity;
import com.zkzk.pra.entity.Data;
import com.zkzk.pra.entity.Location;
import com.zkzk.pra.entity.Project;
import com.zkzk.pra.model.imp.NetModel;
import com.zkzk.pra.ui.SpecimenDialog;
import com.zkzk.pra.utils.ExceptionHandler;
import com.zkzk.pra.utils.ToastUtil;

//import android.R.styleable;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
//import android.app.ResultInfo;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.CursorJoiner.Result;
//import android.filterfw.core.StopWatchMap;
import android.graphics.Bitmap;
import android.graphics.Color;
//import android.os.Broadcaster;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import top.jemen.interfaces.ACallback;
import top.jemen.interfaces.ICallback;
import top.jemen.utils.LogUtil;
import android.widget.Toast;

import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class CollaurumView extends FrameLayout implements OnClickListener {
	private Context context;
	private FluData[] datas;
	private View root;
	private TextView[] tvChannels, tvMsgs,tvResults;
	private ImageView iv;
	private String[] channels;
	private LinearLayout[] lls;
	private int size;
//	private Button[] btPrints;
//	private Button[] btUploads;
	private CheckBox[] cbUses;
	
	public CollaurumView(final Context context, String... channel) {
		super(context);
		this.context = context;
		this.channels=channel;
		LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(0,LayoutParams.WRAP_CONTENT);
		params.weight=1;
		this.setLayoutParams(params);
		// 初始化界面
		try {
			root = LayoutInflater.from(context).inflate(R.layout.view_collaurum ,this);
			// 初始化函数
			init(context);
			setListeners();
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
		LogUtil.d("channels="+Arrays.toString(channel));
		
	}

	private void init(final Context context) {
		if(null==channels) {
			return;
		}
		
		LinearLayout llChannels=(LinearLayout) root.findViewById(R.id.ll_collaurum_channels);
		
		int n=channels.length;
		lls=new LinearLayout[n];
		tvMsgs=new TextView[n];
		tvChannels=new TextView[n];
		tvResults=new TextView[n];
//		btPrints=new Button[n];
//		btUploads=new Button[n];
		cbUses=new CheckBox[n];
		for(int i=0;i<n;i++) {
			lls[i]=(LinearLayout) LayoutInflater.from(context).inflate(R.layout.ll_collaurum_channel, null);
			tvMsgs[i]=(TextView) lls[i].findViewById(R.id.tv_collaurum_specimen);
			tvResults[i]=(TextView) lls[i].findViewById(R.id.tv_collaurum_result);
			tvChannels[i]=(TextView) lls[i].findViewById(R.id.tv_collaurum_channel);
			tvChannels[i].setText(channels[i]);
			llChannels.addView(lls[i]);
			
			cbUses[i]=(CheckBox) lls[i].findViewById(R.id.cb_collaurum_use);
			
//			btPrints[i]=(Button) lls[i].findViewById(R.id.bt_collaurum_print);
//			btPrints[i].setVisibility(View.INVISIBLE);
//			btUploads[i]=(Button) lls[i].findViewById(R.id.bt_collaurum_upload);
//			btUploads[i].setVisibility(View.INVISIBLE);
			if(i==0) {
				msgTextSize=tvMsgs[i].getTextSize();
			}
		}
		
		

		
		datas=new FluData[n];
		specimenDialogs=new CollaurumSpecimenDialog[n];
		
		
		
		
		iv=(ImageView) root.findViewById(R.id.iv_hump);
		
		for(int i=0;i<n;i++) {
			datas[i] = new FluData();
			datas[i].SetChannelNum(channels[i]);
			
		}
		
		
		
		
	}
	
	private float msgTextSize;
	public void setProjSize(float size) {
		for(TextView tv:tvMsgs) {
			tv.setTextSize(size);
		}
	}
	public void recoverProjSize() {
		for(TextView tv:tvMsgs) {
			tv.setTextSize(msgTextSize);
		}
	}
	

	
	public void destroy() {
		for(int i=0;i<specimenDialogs.length;i++) {
			if(specimenDialogs[i]!=null) {
				specimenDialogs[i].cancel();
			}
		}
	}
	private Dialog[] specimenDialogs;
	private void setListeners() {
		for(int i=0;i<channels.length;i++) {
			lls[i].setTag(i);
			lls[i].setOnClickListener(this);
//			btPrints[i].setTag(i);
//			btPrints[i].setOnClickListener(this);
//			btUploads[i].setTag(i);
//			btUploads[i].setOnClickListener(this);
			
			
		}

	}

	/**清空界面显示，以及预存的吸光度、da0等。
	 *与clear不同，clear将会也清空样品编号、样品名称、被检单位等。
	 */
	public void reset() {
		resetData();
		clear();
	}

	/**
	 * 清空界面显示、所有数据，包括样品编号、名称及送检单位等。
	 */
	public void clear() {
		for(int i=0;i<tvResults.length;i++) {
			tvResults[i].setText("检测结果");
//			btPrints[i].setVisibility(View.INVISIBLE);
//			btUploads[i].setVisibility(View.INVISIBLE);
		}
		iv.setImageDrawable(context.getResources().getDrawable(R.drawable.shape_ract));
	}
	
	private void resetData() {
		// data=new Data();//没检测一次完了之后可重新创建对象，如果不做内存查询的话也可以不。
		for(int i=0;i<datas.length;i++) {
			datas[i].setResult(null);
			datas[i].setT(-1);
			datas[i].setC(-1);
			datas[i].setHump(null);
			datas[i].setUpLoded(false);
			datas[i].setValues(null);
			datas[i].setSn(null);
		}
	}


	private String getString(int id) {
		return MyApp.getApp().getString(id);
	}

	/**
	 * 暂不使用
	 */
	public void calculate() {

	}


	public String[] getChannel() {
		return channels;
	}



	public FluData[] getData() {
		return datas;
	}

//	public void setData(FluData... data) {
//		this.datas = data;
//	}
	public void setMsg(FluData model) {
		if(null==model) {
			return;
		}
		for(FluData data:datas) {
			data.setSn(model.getSn());
			data.setSpecimen(model.getSpecimen());
			data.setSourceAddr(model.getSourceAddr());
			data.setSourceContact(model.getSourceContact());
			data.setSourceOrg(model.getSourceOrg());
			data.setSourceOrgCode(model.getSourceCode());
			data.setSourceOrgType(model.getSourceOrgType());
			data.setSourcePhone(model.getSourcePhone());
			
			data.setUserAddr(model.getUserAddr());
			data.setUserContact(model.getUserContact());
			data.setUserOrg(model.getUserOrg());
			data.setUserPhone(model.getUserPhone());
			data.setUsrCode(model.getUserCode());
			data.setOperator(model.getOperator());
			data.setToken(model.getToken());
		}
	}


	@Override
	public void onClick(final View v) {
		final int i=(int) v.getTag();
		switch(v.getId()) {
		case R.id.ll_collaurum_channel:
			if(null==specimenDialogs[i]) {
				specimenDialogs[i]=new CollaurumSpecimenDialog((Activity) context, datas[i],new ACallback() {
					@Override
					public void onSuccess(Object obj) {
						tvMsgs[i].setText(datas[i].getProj()+"");
					}
				});
			}
			specimenDialogs[i].show();
			
			break;
//		case R.id.bt_collaurum_print:
//			final String[] items = {"打印常规结果","打印承诺达标合格证" };
//			AlertDialog.Builder listDialog=new Builder(context);
//			listDialog.setTitle("请选择打印类型");
//			listDialog.setItems(items, new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					if(which==0) {
//						PrinterJPW.print(datas[i], new ICallback() { // 此函数内部会开启新的线程以适配打印机的速度以防打印机缓存爆掉。
//							@Override
//							public void onSuccess(Object obj) {
//								btPrints[i].setEnabled(true);
//							}
//
//							@Override
//							public void onFailed(Object obj) {
//								btPrints[i].setEnabled(true);
//							}
//						});
//					}else {
//						PrinterJPW.printCert(datas[i], new ICallback() { // 此函数内部会开启新的线程以适配打印机的速度以防打印机缓存爆掉。
//							@Override
//							public void onSuccess(Object obj) {
//								btPrints[i].setEnabled(true);
//							}
//
//							@Override
//							public void onFailed(Object obj) {
//								btPrints[i].setEnabled(true);
//							}
//						});
//					}
//				}
//			});
//			listDialog.show();
//			btPrints[i].setEnabled(false);
//			break;
//		case R.id.bt_collaurum_upload:
//			v.setEnabled(false);
//			HttpModel.get().send2AH(datas[i],new ICallback() {
//				
//				@Override
//				public void onSuccess(Object obj) {
//					ToastUtil.showText(R.string.data_upload_succed, Toast.LENGTH_SHORT);
//					v.setEnabled(true);					
//				}
//				
//				@Override
//				public void onFailed(Object obj) {
//					ToastUtil.showText(getString(R.string.data_upload_failed) + "," + (String) obj, Toast.LENGTH_SHORT);
//					v.setEnabled(true);
//				}
//			});
//			break;
		}
	}

	
	/**
	 * 在主线程调用
	 * @param bmp
	 */
	public void setBitmap(Bitmap bmp) {
		iv.setImageBitmap(bmp);
		for(int i=0;i<datas.length;i++) {
			tvResults[i].setText(datas[i].getResult());
//			btPrints[i].setVisibility(View.VISIBLE);
//			btUploads[i].setVisibility(View.VISIBLE);
		}
	}

	public void saveData() {
		for(int i=0;i<datas.length;i++) {
			datas[i].setTime(System.currentTimeMillis());
			if(datas[i].getSn()==null){
				datas[i].setSn((Consts.SNDF.format(new Date()) + i));
			}
			XDao.insertToDataBase(datas[i]);
		}
	}

	public void setProj(String...projs) {
		if(null==projs) {
			return;
		}
		for(int i=0;i<projs.length&&i<datas.length;i++) {
			datas[i].setProj(projs[i]);
			tvMsgs[i].setText(projs[i]);
		}
	}

	public void setChecked(boolean isChecked) {
		for(CheckBox cv:cbUses){
			cv.setChecked(isChecked);
		}
		
	}
	public List<FluData> getCheckedData(){
		List<FluData> ls=new ArrayList<>();
		for(int i=0;i<cbUses.length;i++){
			if(cbUses[i].isChecked()&&datas[i].getResult()!=null){
				ls.add(datas[i]);
			}
		}
		return ls;
	}
}
