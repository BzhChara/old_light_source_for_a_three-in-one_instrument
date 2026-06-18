package com.whswzz.prfluroanalyzer.settings;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.base.BaseActivity;
import com.whswzz.prfluroanalyzer.settings.fragment.DatauploadFragment;
import com.whswzz.prfluroanalyzer.settings.fragment.SelfInfoFragment;
import com.whswzz.prfluroanalyzer.settings.fragment.SysParamFragment;
import com.whswzz.prfluroanalyzer.settings.fragment.WifiFragment;
import com.zkzk.pra.R;
//import com.zkzk.pra.eth.EthernetFragment;
//import com.zkzk.pra.fragment.EtherFragment;
import com.whswzz.prfluroanalyzer.settings.fragment.NormalSetFragment;
import com.zkzk.pra.utils.ExceptionHandler;
import com.zkzk.pra.utils.TTS;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class SetupActivity extends BaseActivity {
	private RadioGroup rgSet;
	private int currentRbId=-1;
	private Fragment normalFragment,wifiFragment,dataUploadFragment,selfInfoFragment,sysparamFragment;//,ethFragment;
//	private EtherFragment ethFragment;
//	private EthernetFragment eth2Fragment;
//	DatauploadFragment dataUploadFragment;
	private Drawable rightDrawable;
	
	private RadioButton[] rbs;
	private Drawable[] drawLeft;
	private Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup);
		getWindow().setBackgroundDrawable(null);	
	/**Jemen:该飞思卡尔的芯片主频较低并且界面背景图片太大，为减少界面切换的时间，不得已延迟加载一些内容。**/	
		handler=new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				init();
				getFragmentManager().beginTransaction().replace(R.id.fl_set, normalFragment).commit();
				setListeners();	
			}
		}, 100);

	}
	
	
	@Override
	protected void onDestroy() {
		handler.removeCallbacksAndMessages(null);
		super.onDestroy();
	}
	
	private void init() {
		try {
			rgSet=(RadioGroup) findViewById(R.id.rg_setup);
			normalFragment=new NormalSetFragment();
			wifiFragment=new WifiFragment();
			dataUploadFragment=new DatauploadFragment();
			selfInfoFragment=new SelfInfoFragment();
			sysparamFragment = new SysParamFragment();
			
			rightDrawable = getResources().getDrawable(R.drawable.arrow);
			rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());

			rbs = new RadioButton[5];
			rbs[0] = (RadioButton) findViewById(R.id.rb_species);
			rbs[1] = (RadioButton) findViewById(R.id.rb_sources);
			rbs[2] = (RadioButton) findViewById(R.id.rb_data_upload);
			rbs[3] = (RadioButton) findViewById(R.id.rb_sys_param);
			rbs[4] = (RadioButton) findViewById(R.id.rb_self_info);

			drawLeft = new Drawable[5];
			drawLeft[0] = getResources().getDrawable(R.drawable.normal_set);
			drawLeft[1] = getResources().getDrawable(R.drawable.wifi_set);
			drawLeft[2] = getResources().getDrawable(R.drawable.upload);
			drawLeft[3] = getResources().getDrawable(R.drawable.self_info);
			drawLeft[4] = getResources().getDrawable(R.drawable.self_info);

			for (Drawable dr : drawLeft) {
				dr.setBounds(0, 0, dr.getMinimumWidth(), dr.getMinimumHeight());
			}



		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}
	private void setListeners() {
		rgSet.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			RadioButton rb;
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
//				Log.d("jemen","checkChanged");
				for(int i=0;i<rbs.length;i++) {	//for wipe the arrow right
					rb=rbs[i];
					rb.setCompoundDrawables(drawLeft[i], null, null, null);
					rb.setPadding(80, 0, 60, 0);
				}
				
				switch(checkedId) {
				case R.id.rb_species:
					getFragmentManager().beginTransaction().replace(R.id.fl_set,normalFragment).commit();
					if(MyApp.getApp().isTtsOk()) {
						TTS.stop();
						TTS.speak(getResources().getString(R.string.change_sys_language_will_lead_to_reboot));
					}
					rb=(RadioButton) findViewById(R.id.rb_species);
					rb.setCompoundDrawables(drawLeft[0], null, rightDrawable, null);
					break;
				case R.id.rb_sources:
					getFragmentManager().beginTransaction().replace(R.id.fl_set,wifiFragment).commit();
					if(MyApp.getApp().isTtsOk()) {
						TTS.stop();
						TTS.speak(getResources().getString(R.string.need_link_wifi));
					}
					rb=(RadioButton) findViewById(R.id.rb_sources);
					rb.setCompoundDrawables(drawLeft[1], null, rightDrawable, null);
					break;
				case R.id.rb_data_upload:
					getFragmentManager().beginTransaction().replace(R.id.fl_set,dataUploadFragment).commit();
					if(MyApp.getApp().isTtsOk()) {
						TTS.stop();
						TTS.speak(getResources().getString(R.string.fill_in_url));
					}
					 rb=(RadioButton) findViewById(R.id.rb_data_upload);
					rb.setCompoundDrawables(drawLeft[2], null, rightDrawable, null);
					break;
					case R.id.rb_sys_param:
						getFragmentManager().beginTransaction().replace(R.id.fl_set, sysparamFragment).commit();
						rb = (RadioButton) findViewById(R.id.rb_sys_param);
						rb.setCompoundDrawables(drawLeft[3], null, rightDrawable, null);
						break;
				case R.id.rb_self_info:
					getFragmentManager().beginTransaction().replace(R.id.fl_set,selfInfoFragment).commit();
					if(MyApp.getApp().isTtsOk()) {
						TTS.stop();
						TTS.speak(getResources().getString(R.string.click_version_update_to_check_new_version));
					}
					 rb=(RadioButton) findViewById(R.id.rb_self_info);
						rb.setCompoundDrawables(drawLeft[3], null, rightDrawable, null);
					break;
				}
				
				rb.setPadding(80, 0, 15, 0);
			}
			
		});
		
		
	}
	


}
