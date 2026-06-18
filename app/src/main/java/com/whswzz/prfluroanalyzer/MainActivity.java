package com.whswzz.prfluroanalyzer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xutils.x;
import org.xutils.view.annotation.ViewInject;

//import com.swzz.praio.Ktest;
import com.whswzz.prfluroanalyzer.base.BaseActivity;
import com.whswzz.prfluroanalyzer.enzyme.EnzymeActivity;
import com.whswzz.prfluroanalyzer.enzyme.data.EnzymeDataActivity;
import com.whswzz.prfluroanalyzer.fluoro.FluoroActivity;
import com.whswzz.prfluroanalyzer.fluoro.data.FluDataActivity;
import com.whswzz.prfluroanalyzer.param.Params;
import com.whswzz.prfluroanalyzer.parameSet.ParamActivity;
import com.whswzz.prfluroanalyzer.photometer.PhotometerActivity;
import com.whswzz.prfluroanalyzer.photometer.data.PhotometerDataActivity;
import com.zkzk.pra.R;
import com.zkzk.pra.activity.DataActivity;
import com.zkzk.pra.activity.DetectActivity;
import com.zkzk.pra.activity.ProjActivity;
import com.zkzk.pra.receiver.UsbReceiver;
import com.zkzk.pra.utils.Utils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import top.jemen.utils.TTS2;

public class MainActivity extends BaseActivity implements OnClickListener {
	@ViewInject(R.id.bt_main_detect)
	private Button btDetect;
	@ViewInject(R.id.bt_main_proj)
	private Button btProj;
	@ViewInject(R.id.bt_main_data)
	private Button btData;
	@ViewInject(R.id.bt_main_setup)
	private Button btSetup;
	@ViewInject(R.id.bt_main_fluoro)
	private Button btFluoro;
	@ViewInject(R.id.bt_main_timer)
	private Button btTimer;
	// @ViewInject(R.id.bt_main_data2)
	// private Button btFluoroData;
	@ViewInject(R.id.bt_main_param)
	private Button btParam;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().setBackgroundDrawable(null);
		x.view().inject(this);
		setListeners();
//		new Ktest().prt("helo kotlin");


		TTS2.get().speakText("欢迎使用");

	}




	private void setListeners() {
		btDetect.setOnClickListener(this);
		btProj.setOnClickListener(this);
		btData.setOnClickListener(this);
		btSetup.setOnClickListener(this);
		btFluoro.setOnClickListener(this);
		btTimer.setOnClickListener(this);
		findViewById(R.id.bt_unmount).setOnClickListener(this);
		// btFluoroData.setOnClickListener(this);
		btParam.setOnClickListener(this);
		//

	}

	private AlertDialog.Builder kindsDialog;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_main_detect:
			final String[] kinds = {"农药残留分光检测","非法添加分光检测"};
			if(null==kindsDialog) {
				kindsDialog=new Builder(this);
				kindsDialog.setTitle("请选择检测类型");
				kindsDialog.setItems(kinds, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(which==0) {
							Intent toDetect = new Intent(MainActivity.this, DetectActivity.class);
							startActivity(toDetect);
						}else {
							Intent toDetect = new Intent(MainActivity.this, PhotometerActivity.class);
							startActivity(toDetect);
						}
					}
				});
			}
			kindsDialog.show();

			
			// LogUtil.e("to detect,intent="+toDetect.toString());
			break;
		case R.id.bt_main_fluoro:
			Intent toFluoro = new Intent(this, FluoroActivity.class);
			startActivity(toFluoro);
			break;
		case R.id.bt_main_proj:
			Intent toProj = new Intent(this, ProjActivity.class);
			startActivity(toProj);
			break;
		case R.id.bt_main_data:
			final String[] items = { "分光农残记录查询", "胶体金记录查询", "酶片记录查询","分光非法添加记录查询" };
			AlertDialog.Builder listDialog = new Builder(this);
			listDialog.setTitle("请选择查询类型");
			listDialog.setItems(items, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						Intent toData = new Intent(MainActivity.this, DataActivity.class);
						startActivity(toData);
						break;
					case 1:
						Intent toData1 = new Intent(MainActivity.this, FluDataActivity.class);
						startActivity(toData1);
						break;
					case 2:
						Intent toData2=new Intent(MainActivity.this,EnzymeDataActivity.class);
						startActivity(toData2);
						break;
					case 3:
						Intent toData3=new Intent(MainActivity.this,PhotometerDataActivity.class);
						startActivity(toData3);
						break;
					}
				}
			});
			listDialog.show();

			break;
		case R.id.bt_main_setup:
			Intent toSetup = new Intent(this, c.a.a.e.c.class);
			startActivity(toSetup);
			break;
		case R.id.bt_unmount: // 移除Upan
			if (UsbReceiver.udiskExist) {
				Utils.unMount(UsbReceiver.PATH);

			}
			break;
		// case R.id.bt_main_data2:
		// Intent toFluData=new Intent(this,FluDataActivity.class);
		// startActivity(toFluData);
		// break;
		case R.id.bt_main_param:
			Intent toParam = new Intent(this, ParamActivity.class);
			startActivity(toParam);
			break;
		case R.id.bt_main_timer:
			Intent toEnzyme = new Intent(this, EnzymeActivity.class);
			startActivity(toEnzyme);
			
			new Thread() {
				public void run() {
					try {
						URL url=new URL("http://58.49.112.42:8181/Server/Version?name="+Params.NAME);
						HttpURLConnection conn=(HttpURLConnection) url.openConnection();
						BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
						String line;
						while((line=br.readLine())!=null) {
							System.out.println(line);
						}
						conn.connect();
						br.close();
						
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				};
			}.start();
			
			

			break;

		}
		overrideAnim();
	}

	private void overrideAnim() {
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

}
