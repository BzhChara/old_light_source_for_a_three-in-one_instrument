package com.zkzk.pra.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.xutils.x;
import org.xutils.view.annotation.ViewInject;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.base.BaseActivity;
import com.whswzz.prfluroanalyzer.consts.Consts;
import com.whswzz.prfluroanalyzer.entity.AbsorbancyBin;
import com.whswzz.prfluroanalyzer.entity.IData;
import com.whswzz.prfluroanalyzer.model.DataModel;
import com.whswzz.prfluroanalyzer.param.Params;
import com.whswzz.prfluroanalyzer.utils.PrinterJPW;
import com.zkzk.pra.R;
import com.zkzk.pra.entity.Data;
import com.zkzk.pra.entity.Project;
import com.zkzk.pra.ui.PRView;
import com.zkzk.pra.ui.PhotometerView;
import com.zkzk.pra.ui.ProDialog2;
import com.zkzk.pra.utils.ExceptionHandler;
import com.zkzk.pra.utils.TTS;
import com.zkzk.pra.utils.ToastUtil;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
//import android.media.effect.effects.StraightenEffect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import top.jemen.interfaces.ICallback;
import top.jemen.utils.LogUtil;
import top.jemen.utils.Tone;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 检测界面
 * 自动选定通道，软件会根据各个通道吸光度的变化而自动选定通道，但是某些通道吸光度不稳会导致额外的选定。
 * @author Jemen Chen
 *
 */
public class DetectActivity extends BaseActivity implements OnClickListener {
	public static final boolean CYCLE = false; // 循环测试使用,需要测试时改为true
	@ViewInject(R.id.ll_up)
	private LinearLayout llUP;
	@ViewInject(R.id.ll_down)
	private LinearLayout llDown;
	@ViewInject(R.id.bt_detect_reset)
	private Button btReset;
	@ViewInject(R.id.bt_detect_print)
	private Button btPrint;
	@ViewInject(R.id.bt_detect_start)
	private Button btStart;
	@ViewInject(R.id.cb_all)
	private CheckBox cbAll;
	@ViewInject(R.id.rg_detect_mode)
	private RadioGroup rgMode;

	private Project proj = MyApp.getApp().getProjs().get(0);
	
	private PRView[] prViews = new PRView[24];

	@Subscribe(threadMode=ThreadMode.MainThread)
	public void getAbsorbancy(AbsorbancyBin ab) {
		float[] fs=ab.getAbs();
		timers[ab.getChannel()]++;
		for(int i=0;i<fs.length;i++) {
			float[] kb= Params.getPhotometer(12 * ab.getChannel() + i,DataModel.PR);

			prViews[12 * ab.getChannel() + i].setAbsorbance(timers[ab.getChannel()], fs[i]*kb[0]+kb[1]);
		}
//		LogUtil.d("PR receive:"+Arrays.toString(fs));

	}
	int[] timers=new int[2];
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(android.os.Message msg) { //现在用哪一个？
			switch (msg.what) {
			case Consts.ABSORBANCY: //吸光度数据
				float[] fs=(float[]) msg.obj;
				LogUtil.d("吸光度"+msg.arg1+",fs:"+Arrays.toString(fs));
				timers[msg.arg1]++;
				for(int i=0;i<fs.length;i++) {
					float[] kb= Params.getPhotometer(12*msg.arg1+i + i,DataModel.PR); //农残灯光是定值

					prViews[12*msg.arg1+i].setAbsorbance(timers[msg.arg1], fs[i]*kb[0]+kb[1]);
				}
				break;
			case Consts.DETECT_TIME:
				timers[0]=0;
				timers[1]=0;
				proDialog.show();
				proDialog.setTimer(msg.arg1);
				if (msg.arg1 > 0) {
					if (MyApp.getApp().isTtsOk()) {
						TTS.stop();
						TTS.speak(getResources().getString(R.string.start_detect));
					}
				}
				break;
			case Consts.MSG_READ_END://消息来自DataModel
				for (PRView pr : prViews)
					pr.getEnd();
				proDialog.dismiss();
				if(CYCLE) {
					handler.postDelayed(cyclicTest, 5000);
					MyApp.getApp().lightenScreen();
				}
				if(MyApp.getApp().getPrintSet()) {	//自动打印。
					List<IData> datas = new ArrayList<>();
					for (PRView pr : prViews) {
						if (pr.isUse()) {
							Data data = pr.getData();
							if (data == null || data.getResult() == null)
								continue;
							datas.add(data);
						}
					}
					PrinterJPW.print(datas, new ICallback() { // 此函数内部会开启新的线程以适配打印机的速度以防打印机缓存爆掉。
						@Override
						public void onSuccess(Object obj) {
							btPrint.setEnabled(true);
						}

						@Override
						public void onFailed(Object obj) {
							btPrint.setEnabled(true);
						}
					});
					btPrint.setEnabled(false);
					break;
				}else {
					btPrint.setEnabled(true);
				}
				Tone.get().play(R.raw.detect_complete);
				break;
			case Consts.SERIAL_ERROR:
				ToastUtil.showText(R.string.e3, Toast.LENGTH_SHORT);
				break;
			case Consts.SERIAL_OPEN_ERROR:
				ToastUtil.showText(R.string.serial_port_open_failed, Toast.LENGTH_SHORT);
				break;
			case Consts.SERIAL_IS_USING:
				ToastUtil.showText(R.string.e4, Toast.LENGTH_SHORT);
				break;
			case Consts.TIME_OUT:
				proDialog.dismiss();
				ToastUtil.showText(R.string.detect_time_out, Toast.LENGTH_SHORT);
				break;
			default:
				break;
			}
		}
	};

	private DataModel dataModel;
	// private LocalBroadcastManager bcManager;
	private TextView tvAbsorbancy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detect);
		x.view().inject(DetectActivity.this);
		// 到底用不用动态加载？
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				init();
				setListeners();
				initProDialog();
			}
		}, 140);
		// IntentFilter filter = new IntentFilter(Consts.ACTION_DA0_CHANGED);
		// bcManager = LocalBroadcastManager.getInstance(DetectActivity.this); //
		// 暂时不用广播了,直接用函数回调.
		// bcManager.registerReceiver(receiver, filter);
		if(!EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().register(this);
		}
	}
	

	private void init() {
		//动态加载
		LayoutParams params = new LayoutParams(llUP.getWidth() / 13, // 为
				LayoutParams.MATCH_PARENT);
		for (int i = 0; i < 12; i++) {
			PRView pr = new PRView(this, null);
			pr.setLayoutParams(params);
			pr.setChannel("" + (i + 1));
			llUP.addView(pr);
			prViews[i] = pr;
			// pr.reset();
			pr.setProj(proj);
		}
		llUP.getChildAt(0).setVisibility(View.VISIBLE);
		
		for (int i = 0; i < 12; i++) {
			PRView pr = new PRView(this, null);
			pr.setLayoutParams(params);
			pr.setChannel("" + (i + 13));
			llDown.addView(pr);
			prViews[i + 12] = pr;
			// pr.reset();
			pr.setProj(proj);
		}
		
		llDown.getChildAt(0).setVisibility(View.VISIBLE);

//		for(int i=0;i<12;i++) {
//			llUP.addView(prViews[i]);
//			llDown.addView(prViews[i+12]);
//		}
		
		//非动态加载
//		int count = llUP.getChildCount();
//		// float da0 = MyApplication.getApp().getPref().getFloat(Consts.KEY_DA0,
//		// 0.618f);
//		// da0 = proj.getContrast();
//		for (int i = 1; i < count; i++) {
//			PRView pr = (PRView) llUP.getChildAt(i);
//			prViews[i - 1] = pr;
//			pr.reset();
//			// pr.setDA0(da0); //proj对象中已经包含了对照值。
//			pr.setProj(proj);
//			// pr.setChecked(true);
//
//		}
//		for (int i = 1; i < count; i++) {
//			PRView pr = (PRView) llDown.getChildAt(i);
//			prViews[12 + i - 1] = pr;
//			pr.reset();
//			// pr.setDA0(da0);
//			pr.setProj(proj);
//			// pr.setChecked(true);
//		}
		
		
		setMode();
		dataModel = DataModel.getInstance();
		tvAbsorbancy = (TextView) findViewById(R.id.tv_user);
		tvAbsorbancy.setText("当前对照值:" + String.format("%.3f", proj.getContrast()));
	}

	private void setListeners() {
		btReset.setOnClickListener(this);
		btPrint.setOnClickListener(this);
		btStart.setOnClickListener(this);
		cbAll.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				for (PRView pr : prViews)
					pr.setChecked(isChecked);
			}
		});
		rgMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				setMode();
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_detect_reset:
			btPrint.setEnabled(false);
			dataModel.stopPhotometer(DataModel.A);
			resetAll();
			break;
		case R.id.bt_detect_print:
			final List<IData> datas = new ArrayList<>();
			for (PRView pr : prViews) {
				if (pr.isUse()) {
					Data data = pr.getData();
					if (data == null || data.getResult() == null)
						continue;
					datas.add(data);
				}
			}
			if(datas.size()==0) {
				ToastUtil.showText("请选择需要打印的通道", Toast.LENGTH_SHORT);
				return;
			}
			final String[] items = {"打印常规结果","打印承诺达标合格证（农产品生产者）","打印承诺达标合格证（农产品收购单位/个人）"};
			AlertDialog.Builder listDialog=new Builder(this);
			listDialog.setTitle("请选择打印类型");
			listDialog.setItems(items, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(which==0) {
						PrinterJPW.print(datas, new ICallback() { // 此函数内部会开启新的线程以适配打印机的速度以防打印机缓存爆掉。
							@Override
							public void onSuccess(Object obj) {
								btPrint.setEnabled(true);
							}

							@Override
							public void onFailed(Object obj) {
								btPrint.setEnabled(true);
							}
						});
					}else if (which==1){
						PrinterJPW.printCert(datas, new ICallback() { // 此函数内部会开启新的线程以适配打印机的速度以防打印机缓存爆掉。
							@Override
							public void onSuccess(Object obj) {
								btPrint.setEnabled(true);
							}

							@Override
							public void onFailed(Object obj) {
								btPrint.setEnabled(true);
							}
						});
					}else {
						PrinterJPW.printCertPerson(datas, new ICallback() { // 此函数内部会开启新的线程以适配打印机的速度以防打印机缓存爆掉。
							@Override
							public void onSuccess(Object obj) {
								btPrint.setEnabled(true);
							}

							@Override
							public void onFailed(Object obj) {
								btPrint.setEnabled(true);
							}
						});
					}
				}
			});
			listDialog.show();
			btPrint.setEnabled(false);
			break;
		case R.id.bt_detect_start:
			int sum = 0;
			for (PRView pr: prViews) {
				if (pr.isUse()) {
					sum++;
				}
			}
			if (sum == 0) {
				ToastUtil.showText("请选择使用的通道", Toast.LENGTH_SHORT);
				return;
			}

			for (PRView pr : prViews) {
				// pr.setChecked(true);
				pr.reset();
				// pr.setDA0(da0);
				pr.setProj(proj);
			}
			btPrint.setEnabled(false);
			DataModel.getInstance().detectData(handler, DataModel.PR, DataModel.A);
			Tone.get().play(R.raw.start);
			break;
			default:
				super.onClick(v);
				break;
		}
	}

	private void resetAll() {
		for (PRView pr : prViews)
			// pr.reset();
			pr.clear();
	}

	ProDialog2 proDialog;

	private void initProDialog() {
		try {
			proDialog = new ProDialog2(this, new ProDialog2.CancelListener() {
				@Override
				public void onCancel() {
					DataModel.getInstance().stopPhotometer(DataModel.A);
					Tone.get().play(R.raw.cancel_succeed);
					proDialog.dismiss();
				}
			});
			// proDialog.setTitle(R.string.detecting);
			// proDialog.setMessate(R.string.detecting_and_dont_operate);
			// proDialog.setProgress(10);
			proDialog.setCancelable(false);
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}

	/**
	 * 获取需要使用的通道
	 * 
	 * @return
	 */
//	private int getAB() {
//		boolean a = false, b = false;
//		for (int i = prViews.length - 1; i >= 0; i--) {
//			PRView pr = prViews[i];
//			if (pr.isUse()) {
//				if (i >= prViews.length / 2) { // 需要使用B通道
//					b = true;
//					i = prViews.length / 2;
//				} else { // 需要使用A通道
//					a = true;
//					break;
//				}
//			}
//		}
//		if (a && b) {
//			return DataModel.AB;
//		} else if (a) {
//			return DataModel.A;
//		} else if (b) {
//			return DataModel.B;
//		}
//		return -1;
//	}

	private void setMode() {
		switch (rgMode.getCheckedRadioButtonId()) {
		case R.id.rb_contrast:// 对照
			cbAll.setChecked(false);
			for (PRView pr : prViews)
				pr.setMode(PhotometerView.MODE_CONSTRUCT);
			prViews[0].setChecked(true);
			break;
		case R.id.rb_detect:// 检测
			for (PRView pr : prViews)
				pr.setMode(PhotometerView.MODE_DETECT);
			break;
		}
	}

	/************** 最终没有使用广播方案 ***************/
	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()) {
			case Consts.ACTION_DA0_CHANGED:
				float da0 = intent.getFloatExtra(Consts.KEY_DA0, -1);
				if (da0 > 0 && da0 < 1) {
					for (PRView pr : prViews)
						pr.setDA0(da0);
				}
				break;
			}
		}
	};

	public void onDA0Changed(float da0) {
		try {
			if (da0 > -10 && da0 < 10) {
				ToastUtil.showText("对照值已改变：" + String.format("%.3f", da0), Toast.LENGTH_SHORT);
				tvAbsorbancy.setText("当前对照值:" + String.format("%.3f", da0));
				proj.setContrast(da0);
				MyApp.getApp().saveProjs();
				for (PRView pr : prViews) {
					// pr.setDA0(da0);
					pr.setProj(proj);
				}
			}
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}

	private void isNullCheck() {
		for (PRView pr : prViews) {
			if (pr.isUse())
				return;
		}
		dataModel.stopPhotometer(DataModel.A);
		proDialog.dismiss();
	}

	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		DataModel.getInstance().stopPhotometer(DataModel.A);
		handler.removeCallbacksAndMessages(null);
		// bcManager.unregisterReceiver(receiver);
		if(null!=proDialog) {
			proDialog.cancel();
		}
		for(PRView prv:prViews) {
			if(null!=prv) {
				prv.destroy();
			}
		}
		
		super.onDestroy();

	}

	
	
	/**
	 * 万次自动循环测试使用
	 */
	private Runnable cyclicTest = new Runnable() {
		@Override
		public void run() {
			try {
				btStart.callOnClick();
			} catch (Exception e) {
				ExceptionHandler.handleException(e);
			}
		}
	};
	
}
