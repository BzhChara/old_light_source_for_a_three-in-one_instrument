//package com.whswzz.prfluroanalyzer.base;
//
//import java.io.DataOutputStream;
//import java.io.File;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.Map;
//
//import com.whswzz.prfluroanalyzer.MainActivity;
//import com.whswzz.prfluroanalyzer.app.MyApp;
//import com.whswzz.prfluroanalyzer.param.Params;
//import com.zkzk.pra.R;
//import com.zkzk.pra.consts.Consts;
//import com.zkzk.pra.ui.BaseDialog;
//import com.zkzk.pra.ui.BatteryView;
//import com.zkzk.pra.ui.VideoDialog;
//import com.zkzk.pra.ui.VideoDialog2;
//import com.zkzk.pra.utils.ExceptionHandler;
//import com.zkzk.pra.utils.ToastUtil;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.app.Dialog;
//import android.app.admin.DevicePolicyManager;
//import android.content.BroadcastReceiver;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.PixelFormat;
//import android.graphics.drawable.Drawable;
//import android.media.AudioRecord;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.net.wifi.WifiInfo;
//import android.net.wifi.WifiManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.PowerManager;
//import android.os.PowerManager.WakeLock;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.KeyEvent;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.View.OnLongClickListener;
//import android.view.Window;
//import android.view.WindowManager;
//import android.view.WindowManager.LayoutParams;
//import android.widget.Button;
//import android.widget.DigitalClock;
//import android.widget.FrameLayout;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//import de.greenrobot.event.EventBus;
//import de.greenrobot.event.Subscribe;
//import de.greenrobot.event.ThreadMode;
//import top.jemen.utils.LogUtil;
//
//@SuppressLint("InlinedApi")
//public class BaseActivity2 extends Activity {
//	protected TitleReceiver stateReceiver;
//	protected BatteryView battery;
//	private ImageView ivWifi;
//	private ImageButton ibBack,ibHome,ibOK;
//	private Button btHelp;
//	private TextView tvTimer;
//	public static final String ACTION_REQUEST_SHUTDOWN = "android.intent.action.ACTION_REQUEST_SHUTDOWN";
//
//	public static final String EXTRA_KEY_CONFIRM = "android.intent.extra.KEY_CONFIRM";
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		
//		// getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
//		// //不知哪句导致了按键触摸不灵敏
//		// int flag = getWindow().getDecorView().getSystemUiVisibility();
//		//// int fullScreen=View.SYSTEM_UI_FLAG_SHOW_FULLSCREEN;
//		// int fullScreen = 0x8;
//		// getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
//		// getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);//隐藏底部任务栏代码
//		// closeBar();
//		// new HomeKeyLocker().lock(this); //调用之后home键、back键，以及ListView的item点击都无效了。
//		Window window = getWindow();
//		// 隐藏标题栏
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		// 隐藏状态栏
//		// 定义全屏参数
//		int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
//		// 设置当前窗体为全屏显示
//		window.setFlags(flag, flag);
//		window.setBackgroundDrawable(null);
//		EventBus.getDefault().register(this);
//	}
//
//	// 设备管理者
//	private DevicePolicyManager mDevicePolicyManager;
//
//	// 关屏组件
//
//	private ComponentName mCompName;
//
//	@Override
//	protected void onStart() {
//		super.onStart();
//		try {
//			stateReceiver = new TitleReceiver();
//			IntentFilter filter = new IntentFilter();
//			// filter.addAction(Intent.ACTION_BATTERY_CHANGED);//如果用系统的广播action，接收器在注册的时候会收到一次0
//			filter.addAction(Intent.ACTION_BATTERY_CHANGED);// 如果用系统的广播action，接收器在注册的时候会收到一次0
//			filter.addAction(Consts.JEMEN_BATTERY_CHANGED);
//			filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
//			filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
//			filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
//			filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
//			registerReceiver(stateReceiver, filter);
//			
//			battery = (BatteryView) findViewById(R.id.battery);
//			ivWifi = (ImageView) findViewById(R.id.iv_title_wifi);
//			ibBack = (ImageButton) findViewById(R.id.ib_bottom_back);
//			ibHome=(ImageButton) findViewById(R.id.ib_bottom_home);
//			ibOK=(ImageButton) findViewById(R.id.ib_bottom_ok);
//			
//			
//			tvTimer=(TextView) findViewById(R.id.tv_title_timer);
//			if(Params.s<=0) {
//				tvTimer.setText("");
//			}else {
//				String text="酶片孵育剩余时间"+Params.s+"秒";
//				if(Params.TEMP_ENZYME!=-1) {
//					text+=" "+Params.TEMP_ENZYME+"℃";
//				}
//				tvTimer.setText(text);
//			}
//			battery.refreshPower(MyApp.getApp().getBatteryLev());
//			
//			ibBack.setOnClickListener(btListener);
//			ibHome.setOnClickListener(btListener);
//			ibOK.setOnClickListener(btListener);
//			
//			btHelp = (Button)findViewById(R.id.bt_help);
//			btHelp.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
//			btHelp.setOnClickListener(btListener);
//			
//			
//			
//			
//		} catch (Exception e) {
//			ExceptionHandler.handleException(e);
//		}
//	}
//
//	private void closeScreen(){
//		if (!mDevicePolicyManager.isAdminActive(mCompName)) {// 这一句一定要有...
//			Intent intent = new Intent();
//			// 指定动作
//			intent.setAction(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
//			// 指定给那个组件授权
//			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mCompName);
//			startActivity(intent);
//		} else {
//			// 立即关闭屏幕
//			mDevicePolicyManager.lockNow();
//			// devicePolicyManager.resetPassword("123321", 0);
//			Log.i("jemen", "具有权限,将进行锁屏....");
//			Log.i("jemen", "going to shutdown screen");
//		}
//		
//	}
//	@Subscribe(threadMode=ThreadMode.MainThread)
//	public void OnTimer(Integer s) {
//		if(s<=0) {
//			tvTimer.setText("");
//		}else {
//			String text="酶片孵育剩余时间"+Params.s+"秒";
//			if(Params.TEMP_ENZYME!=-1) {
//				text+=" "+Params.TEMP_ENZYME+"℃";
//			}
//			tvTimer.setText(text);
//		}
//	}
//	
//    /**
//     * 改变App当前Window亮度
//     *
//     * @param brightness
//     */
//    public void setBrightness(float brightness) {
//        Window window = this.getWindow();
//        WindowManager.LayoutParams lp = window.getAttributes();
//        if (brightness == -1) {
//            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
//        } else {
//            lp.screenBrightness = brightness;
//        }
//        window.setAttributes(lp);
//    }
//
//	private void turnOnScreen() {
//		PowerManager mPowerManager;
//		PowerManager.WakeLock mScreenLock;
//		mPowerManager = ((PowerManager) getSystemService(POWER_SERVICE));
//
//		mScreenLock = mPowerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP // 该flag使能屏幕关闭时，也能点亮屏幕（通常的wakelock只能维持屏幕处于一直开启状态，如果灭屏时，是不会自动点亮的）
//				| PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "screenOnWakeLock");
//		if (!mScreenLock.isHeld()) {
//			mScreenLock.acquire();
//		} else {
//			mScreenLock.release();
//		}
//	}
//	
//
////事件分发顺序 dispatchTouchEvent--->onInterceptTouchEvent--->onTouchEvent
//	@Override
//	public boolean dispatchTouchEvent(MotionEvent ev) {
//		
//		if(ev.getAction()==MotionEvent.ACTION_UP) {
//			if( MyApp.getApp().lightenScreen())//是否已经点亮在函数里边会进行判断。
//				return true;
//		}
//			
//		return super.dispatchTouchEvent(ev);
//	}
//	
//	OnClickListener btListener = new OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			LogUtil.d("点击顶部按钮");
//			switch(v.getId()) {
//			case R.id.ib_bottom_home:
////				if (this instanceof MainActivity) {
////					ToastUtil.showText(R.string.already_in_main, Toast.LENGTH_SHORT);
////					return;
////				}
////				finish();
//				Intent intent = new Intent(BaseActivity2.this, c.b.d.a.e.class);
//				startActivity(intent);
//				overridePendingTransition(0, 0);
//				break;
//			case R.id.ib_bottom_back:
////				if (BaseActivity2.this instanceof MainActivity) {
////					ToastUtil.showText(R.string.already_in_main, Toast.LENGTH_SHORT);
////					return;
////				}
//				finish();
//				overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
//				break;
//			case R.id.bt_help:
//				if(null==videoDialog) {
////					videoDialog=new VideoDialog(BaseActivity.this);
//					videoDialog=new VideoDialog2(BaseActivity2.this);
//				}
//				videoDialog.show();
//				break;
//			}
//			
//		}
//	};
//	private Dialog videoDialog;
//	@Override
//	protected void onStop() {
//		unregisterReceiver(stateReceiver);
//		super.onStop();
//	}
//
//	@Override
//	protected void onDestroy() {
//		EventBus.getDefault().unregister(this);
//		if(null!=videoDialog) {
//			videoDialog.cancel();
//		}
//		super.onDestroy();
//	}
//	
//	
//	
//	class TitleReceiver extends BroadcastReceiver {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			// Log.d("jemen", "title receiver收到广播" + intent.getAction());
//			// 判断它是否是为电量变化的Broadcast Action
//			String action = intent.getAction();
//			if (Consts.JEMEN_BATTERY_CHANGED.equals(intent.getAction())) {
//				// ToastUtil.showText("电量改变", Toast.LENGTH_SHORT);
//				// 获取当前电量
//				int level = intent.getIntExtra("level", MyApp.getApp().getBatteryLev());
//				LogUtil.d("power level=" + level);
//				MyApp.getApp().setBatteryLeve(level);
//				// //电量的总刻度
//				int scale = intent.getIntExtra("scale", 100);
//				// 把它转成百分比
//				// tv.setText("电池电量为"+((level*100)/scale)+"%");
//				battery.refreshPower(level);
//
//			}
//			if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)
//					|| WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
//				showSignal(context);
//
//			} else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
//				// System.out.println("网络状态改变");
//				NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
//				if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {// 如果断开连接
//					ivWifi.setImageResource(R.drawable.stat_sys_wifi_signal_0);
//				}
//			} else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
//				// WIFI开关
//				int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
//				WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//
//				if (wifiManager.isWifiEnabled()) {// 如果关闭
//					showSignal(context);
//				} else {
//					ivWifi.setImageResource(R.drawable.stat_sys_wifi_signal_0);
//				}
//			}
//
//		}
//
//		private void showSignal(Context context) {
//			if(null==ivWifi)	return;
//			int strength = getStrength(context);
//			// Log.d("jemen", "当前信号：" + strength);
//			switch (strength) {
//			case 0:
//				ivWifi.setImageResource(R.drawable.stat_sys_wifi_signal_0);
//				break;
//			case 1:
//				ivWifi.setImageResource(R.drawable.stat_sys_wifi_signal_1);
//				break;
//			case 2:
//				ivWifi.setImageResource(R.drawable.stat_sys_wifi_signal_2);
//				break;
//			case 3:
//				ivWifi.setImageResource(R.drawable.stat_sys_wifi_signal_3);
//				break;
//			case 4:
//				ivWifi.setImageResource(R.drawable.stat_sys_wifi_signal_4);
//				break;
//			}
//
//		}
//
//		public int getStrength(Context context) {
//			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//			WifiInfo info = wifiManager.getConnectionInfo();
//			if (info.getBSSID() != null) {
//				int strength = WifiManager.calculateSignalLevel(info.getRssi(), 5);
//				// 链接速度
//				// int speed = info.getLinkSpeed();
//				// // 链接速度单位
//				// String units = WifiInfo.LINK_SPEED_UNITS;
//				// // Wifi源名称
//				// String ssid = info.getSSID();
//				return strength;
//			}
//			return 0;
//		}
//
//	}
//
//	/**
//	 * 关闭Android导航栏，实现全屏 测试无用，且会导致屏幕点击不灵敏
//	 */
//	private void closeBar() {
//		try {
//			String command;
//			command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib service call activity 42 s16 com.android.systemui";
//			ArrayList<String> envlist = new ArrayList<String>();
//			Map<String, String> env = System.getenv();
//			for (String envName : env.keySet()) {
//				envlist.add(envName + "=" + env.get(envName));
//			}
//			String[] envp = envlist.toArray(new String[0]);
//			Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command }, envp);
//			proc.waitFor();
//
//			String ProcID = "79";
//			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//				ProcID = "42"; // ICS
//			// 需要root 权限
//			Process proc2 = Runtime.getRuntime()
//					.exec(new String[] { "su", "-c", "service call activity " + ProcID + " s16 com.android.systemui" }); // WAS
//			proc2.waitFor();
//
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
//		}
//	}
//
//	/**
//	 * 显示导航栏 已经废弃
//	 */
//	public static void showBar() {
//		try {
//			String command;
//			command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib am startservice -n com.android.systemui/.SystemUIService";
//			ArrayList<String> envlist = new ArrayList<String>();
//			Map<String, String> env = System.getenv();
//			for (String envName : env.keySet()) {
//				envlist.add(envName + "=" + env.get(envName));
//			}
//			String[] envp = envlist.toArray(new String[0]);
//			Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command }, envp);
//			proc.waitFor();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public boolean onKeyUp(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_MENU) {
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}
//
//}
