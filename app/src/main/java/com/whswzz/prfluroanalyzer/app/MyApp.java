package com.whswzz.prfluroanalyzer.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.xutils.x;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.SDKInitializer;
import com.whswzz.prfluroanalyzer.consts.Consts;
import com.whswzz.prfluroanalyzer.entity.Organization;
import com.whswzz.prfluroanalyzer.entity.Source;
import com.whswzz.prfluroanalyzer.entity.Species;
import com.whswzz.prfluroanalyzer.photometer.entity.PhotometerProj;
import com.whswzz.prfluroanalyzer.service.BatteryService2;
import com.zkzk.pra.R;
import com.zkzk.pra.biz.LocBiz;
import com.zkzk.pra.biz.UpdateBiz;
import com.zkzk.pra.dal.IRecordDao;
import com.zkzk.pra.dal.imp.RecordDaoImpl;
import com.zkzk.pra.db.DBOpenHelper;
import com.zkzk.pra.db.Database;
import com.zkzk.pra.entity.Data;
import com.zkzk.pra.entity.Location;
import com.zkzk.pra.entity.Project;
import com.zkzk.pra.entity.User;
import com.zkzk.pra.model.imp.FileUtil;
import com.zkzk.pra.parser.JemenParser;
import com.zkzk.pra.receiver.UsbReceiver;
import com.zkzk.pra.service.NetService;
import com.zkzk.pra.ui.BlackView;
import com.zkzk.pra.utils.ExceptionHandler;
import com.zkzk.pra.utils.MC;
import top.jemen.utils.NetUtil;
import com.zkzk.pra.utils.TTS;
import com.zkzk.pra.utils.Tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
//import android.support.multidex.MultiDexApplication;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;


import top.jemen.context.JemenApp;
import top.jemen.interfaces.ACallback;
import top.jemen.interfaces.ICallback;
import top.jemen.model.ComM;
import top.jemen.utils.LogUtil;
import top.jemen.utils.TTS2;
import top.jemen.utils.Tone;

/**
 * @author Jemen Chen 内蒙古设备需要启动蓝牙监听服务
 */
public class  MyApp extends JemenApp {
	public static User user;
	public static boolean isReleased = !Build.DEBUG;// !BuildConfig.DEBUG;//true;
	public static ArrayList<Activity> activityList = new ArrayList<Activity>();
	private static MyApp app = null;
	private static SharedPreferences pref;
	public static String apkServerUrl = "http://testapi.whnhs.com/index.php/";
	private boolean wifiEnabled = false;
	private RequestQueue queue;
	private boolean isChinese = true;
	private boolean autoPrint = false;
	private boolean printDetail = true;
	private long bootTime;
	private RecordDaoImpl dataDb;
	private volatile long dataNum;
	private List<Project> projects;
	private LinkedList<Data> records;// 用于保存本次开机后测试的数据，由用户来确认是不是需要保存到数据库，退出程序时不保存。
	private int batteryLevel; // 电池电量，0-100.
	public static int osV = android.os.Build.VERSION.SDK_INT;// 迅为核心板上面时15
	public static String cmdUrl;
	private boolean tts = false;
	private boolean voiceGuide = true;
	private InputMethodManager inputMethodManager;
	private Location location = new Location();
	// public boolean isShowResult=false;
	public static String globalCurrentSn = "";

	public void finish() {
		// 把所有的activity finish
		for (Activity activity : activityList) {
			activity.finish();
		}
		// 结束进程
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	public static MyApp getApp() {
		return app;
	}

	@Override
	public void onCreate() {
		// **********多进程执行处理（onCreate方法仅在主进程执行一次）
		String processName = Tools.getProcessName(this);
		if (!getPackageName().equals(processName)) {// 非主进程不执行onCreate里面操作
			return;
		}

		bootTime = System.currentTimeMillis();
		super.onCreate();
		app = this;
		// 使用百度定位，它会有个子进程

		// Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));//调试不让其重启
		x.Ext.init(this);
		SDKInitializer.initialize(this);// 在使用SDK各组件之前初始化context信息，传入ApplicationContext，百度地图的

		pref = PreferenceManager.getDefaultSharedPreferences(this);
		wifiEnabled = pref.getBoolean(Consts.KEY_WIFI, true);
		autoPrint = pref.getBoolean(Consts.KEY_PRINT_SET, autoPrint); // 自动打印设置
		printDetail = pref.getBoolean(Consts.KEY_PRINT_MSG, printDetail);
		isChinese = "zh".equals(Locale.getDefault().getLanguage());// 获取当前语言
		LogUtil.e("language=" + Locale.getDefault().getLanguage());
		// isChinese = pref.getBoolean(Consts.KEY_LANGUAGE,
		// true);//通过更改协同属性更改语言，不用自己保存变量了。
		queue = Volley.newRequestQueue(this);
		dataDb = RecordDaoImpl.getDao();
		initPorjs();
		Initer.initSpecies(new ACallback() {
			@Override
			public void onSuccess(Object obj) {
				lsSpecies=(List<Species>) obj;
			}
		});
		Initer.initSources(new ACallback() {
			@Override
			public void onSuccess(Object obj) {
				sources=(List<Source>) obj;
			}
		});
		Initer.initOrganizations(new ACallback() {
			@Override
			public void onSuccess(Object obj) {
				organizations=(List<Organization>) obj;
				LogUtil.d(organizations.toString());
			}
		});
		Initer.initPorjs(new ACallback() {
			@Override
			public void onSuccess(Object obj) {
				lsProjs=(List<Species>) obj;
			}
		});
		Initer.initLimts(new ACallback() {
			
			@Override
			public void onSuccess(Object obj) {
				limits=(Map<String, Double>) obj;
			}
		});
		Initer.initLimitUnits(new ACallback() {

			@Override
			public void onSuccess(Object obj) {
				limitUnits=(Map<String, String>) obj;
			}
		});
		Initer.initTCLimts(new ACallback() {

			@Override
			public void onSuccess(Object obj) {
				tcLimits=(Map<String, String>) obj;
			}
		});

		Initer.initBatchMap(new ACallback() {
			@Override
			public void onSuccess(Object obj) {
				batchMap=(Map<String, String>) obj;
			}
		});
		
		Initer.initPhotometerProj(new ACallback() {
			@Override
			public void onSuccess(Object obj) {
				photometerProjs=(List<PhotometerProj>) obj;
			}
		});
		Initer.initGBMap(new ACallback() {
			@Override
			public void onSuccess(Object obj) {
				gbMap=(Map<String, String>) obj;
			}
		});
		
		records = new LinkedList<Data>();
		countData(); // 数据库容量查询
		initWindowManager();// 定时黑屏使用
		startScreenTimer();// 频幕点亮计时

		Intent intent = new Intent(this, BatteryService2.class);
		startService(intent);

		if (voiceGuide = pref.getBoolean(Consts.VOICE_GUIDE, voiceGuide)) {
//			initTTS();
			Tone.get().play(R.raw.welcome_use);
		}
		if (pref.getBoolean(Consts.FIRST_ON, true)) {
			pref.edit().putBoolean(Consts.FIRST_ON, false);
			Tools.getJemenId();
			// 第一次开机可以进行一些设置，不过目前暂不需要。
		}

		Intent netServer = new Intent(this, NetService.class);
		startService(netServer); // 芯辰板子上面貌似这个接口做的有问题。

//		Intent startLocation = new Intent(this, LocationService.class);
//		startService(startLocation); // 芯辰板子上面貌似这个接口做的有问题。

		UsbReceiver.usbDetect();// 检测USB设备是否插入



		location.setLatitude(pref.getFloat(Consts.KEY_LATITUDE, 30.48542f));
		location.setLongitude(pref.getFloat(Consts.KEY_LONGITUDE, 114.276582f));
		location.setDescribe(pref.getString(Consts.KEY_DESCRIBE, null));
		clearCache();

		new LocBiz().scanMac(this); //上成仪器不再使用

//		if (isReleased) {
//			Settings.Secure.putInt(getContentResolver(), Settings.Secure.ADB_ENABLED, 0);
//		} else {
//			Settings.Secure.putInt(getContentResolver(), Settings.Secure.ADB_ENABLED, 1);
//
//		}

		if (Tools.isMaic()) {
			MC.hideNavigation(new ICallback() { // 貌似增加此处的更改之后，容易出现ANR
				@Override
				public void onSuccess(Object obj) {
					// changeDisplayDensity();
				}
				@Override
				public void onFailed(Object obj) {
					// changeDisplayDensity();
				}
			}, 1000);// 迈冲平板使用去掉底部栏。
		}
		ComM.get().start();
		// Settings.Global.putInt(getContentResolver(),Settings.Global.AUTO_TIME,1);
	}

	public long getDataNum() {
		return dataNum;
	}

	/**
	 * 晚点儿修理下。 待迁移。
	 */
	private void countData() {
		new AsyncTask<Void, Void, Long>() {
			@Override
			protected Long doInBackground(Void... params) {
				dataNum = dataDb.countData();
				return dataNum;
			}

			protected void onPostExecute(Long n) {
				dataNum = n;
			};

		}.execute();

		new Thread() {
			public void run() {
				SQLiteDatabase db = null;
				try {
					DBOpenHelper dbOpenHelper = new DBOpenHelper(app);
					db = dbOpenHelper.getReadableDatabase();
					String createIndex = "create index if not exists ia on " + Database.Data.TABLE_NAME + "("
							+ Database.Data.Columns.ID + "," + Database.Data.Columns.TIME + ","
							+ Database.Data.Columns.PROJ + "," + Database.Data.Columns.SPECIMEN + ")";
					db.execSQL(createIndex);// 创建索引

					Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + Database.Data.TABLE_NAME, null);// 查总数
					if (cursor != null && cursor.moveToFirst()) {
						dataNum = cursor.getLong(0);// 获取数据库中总数据条数
						Log.d("jemen", "dataNum=" + dataNum + ",count=" + cursor.getCount());
					}
					if (dataNum > 10000) {
						String sql = "select * from " + Database.Data.TABLE_NAME + " limit 1 ";
						Cursor cursor2 = db.rawQuery(sql, null);
						if (cursor2.moveToFirst()) {
							Log.d("jemen", "app中，the first id=" + cursor2.getInt(0) + ",count=" + cursor2.getCount());
							dataDb.delete(cursor2.getInt(0));
							dataNum--;
						}
						cursor2.close();
					}
					cursor.close();
					db.close();
				} catch (Exception e) {
					ExceptionHandler.handleException(e);
				} finally {
					if (null != db) {
						db.close();
						db = null;
					}
				}
			};

		}.start();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private void initPorjs() {
		new Thread() {
			public void run() {
				ObjectInputStream ois = null;
				try {
					FileInputStream is;
					if (isChinese)
						is = openFileInput(Consts.PROJS_FILE_CN);
					else
						is = openFileInput(Consts.PROJS_FILE_EN);
					ois = new ObjectInputStream(is);
					projects = (List<Project>) ois.readObject();
					is.close();
				} catch (Exception e) {
					ExceptionHandler.handleException(e);
					// createProjs();
				} finally {
					try {
						if (null != ois)
							ois.close();
						if (null == projects)
							createProjs();
					} catch (IOException e) {
						ExceptionHandler.handleException(e);
					}
					// if(null==projects) createProjs();
				}


			};
		}.start();
	}

	private void createProjs() {
		try {
			projects = new LinkedList<Project>();
			Log.d("jemen", "createProjs函数，对projs进行默认初始化");
			String[] projString = getResources().getStringArray(R.array.proj);
			for (int i = 0; i < projString.length; i++) {
				Project p = new Project(true, projString[i], "酶抑制分光光度", 0.5f, 0.618f);
				projects.add(p);
			}
			saveProjs();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public List<Project> getProjs() {
		byte times = 5;
		while (times > 0 && null == projects) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return projects;
	}

	public void saveProjs() {
		try {
			File f;
			if (isChinese)
				f = new File(getFilesDir(), Consts.PROJS_FILE_CN);
			else
				f = new File(getFilesDir(), Consts.PROJS_FILE_EN);
			// LogUtil.d("f path="+f.getAbsolutePath());
			if (f.exists()) {
				f.delete();
			}
			FileOutputStream os = openFileOutput(f.getName(), MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(projects);
			os.close();
			oos.close();
		} catch (FileNotFoundException e) {
			ExceptionHandler.handleException(e);
		} catch (IOException e) {
			ExceptionHandler.handleException(e);
		}
	}

	public void deleteProjs() {
		try {
			File fEN = new File(getFilesDir(), Consts.PROJS_FILE_EN);
			File fCN = new File(getFilesDir(), Consts.PROJS_FILE_CN);
			if (fEN.exists())
				fEN.delete();
			if (fCN.exists())
				fCN.delete();
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}

	public SharedPreferences getPref() {
		return pref;
	}

	public boolean getLanguage() {
		return isChinese;
	}

	public void setLanguage(boolean isChinese) {
		try {
			this.isChinese = isChinese;
			Resources resources = getResources();
			DisplayMetrics dm = resources.getDisplayMetrics();
			Configuration config = resources.getConfiguration();
			// 应用用户选择语言
			Locale locale = Locale.getDefault();
			if (isChinese) {
				locale = Locale.CHINA;
				// locale=new Locale("zh", locale.getCountry()); //英语，国家不变
			} else {
				// locale = Locale.ENGLISH;
				locale = new Locale("en", locale.getCountry()); // 英语，国家不变
			}
			config.locale = locale;
			resources.updateConfiguration(config, dm);
			Class<?> demo = Class.forName("com.android.internal.app.LocalePicker");
			Object obj = demo.newInstance();
			Method method[];
			method = demo.getDeclaredMethods();
			method[method.length - 1].invoke(demo, locale);

			// 修改语言会导致系统的默认输入法设置发生改变，重新设置。
			if (isChinese) {
				String dfInput = Settings.Secure.getString(getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
				LogUtil.e(dfInput);

				inputMethodManager = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
				List<InputMethodInfo> inputMethods = inputMethodManager.getInputMethodList();
				for (InputMethodInfo inp : inputMethods) {
					String id = inp.getId();
					if (id.contains("sogou") || id.contains("Sogou")) {
						inputMethodManager.setInputMethod(null, id); // 两个都有用,但有时候会失效
						inputMethodManager.setInputMethod(null, id); // 两个都有用,但有时候会失效
						Settings.Secure.putString(getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD, id);
						dfInput = Settings.Secure.getString(getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
						LogUtil.e(dfInput);

						if (!dfInput.contains("sogou")) {
							inputMethodManager.showInputMethodPicker();
						}

						break;
					}
					// LogUtil.d("id="+inp.getId());
				}
			}

		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}

		initPorjs();
	}

	public boolean getPrintSet() {
		return autoPrint;
	}

	public void setPrint(boolean isAutoPrint) {
		autoPrint = isAutoPrint;
		pref.edit().putBoolean(Consts.KEY_PRINT_SET, autoPrint).apply();;
	}

	public RequestQueue getQueue() {
		return queue;
	}

	public IRecordDao getDataDb() {
		return dataDb;
	}

	public long getBootTime() {
		return bootTime;
	}

	/**
	 * 将数据进行内存保存
	 * 
	 * @param data
	 */
	public void addRecord(Data data) {
		records.add(data);
	}

	/**
	 * 将数据保存到数据库,超过10万条将删除第一条。
	 * 
	 * @param data
	 */
	public void addData(Data data) {
		// records.add(data); //每次测试的结果都进行内存保存，只有当客户点击生成报告的才向数据库存，所以，不进行同时操作。
		new AsyncTask<Data, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Data... params) {
				try {
					if (params.length < 1 || null == params[0])
						return false;
					if (dataDb.insert(params[0]) > 0) {
						dataNum++;
						if (dataNum > 100000) { // 要求存储10万条
							dataDb.deletFirst();
						}
						return true;
					} else {
						LogUtil.d("insert failed,id=" + id);
					}
				} catch (Exception e) {
					ExceptionHandler.handleException(e);
				}
				return false;
			}

			protected void onPostExecute(Boolean result) {
				LogUtil.e("app,save data result:" + result);
				if (result) {
				} else {
				}
			};

		}.executeOnExecutor(jemenExecutor, data);

	}

	private final ThreadFactory sThreadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		public Thread newThread(Runnable r) {
			return new Thread(r, "jemen-AsyncTask #" + mCount.getAndIncrement());
		}
	};
	/**
	 * 给不需要与界面交互的线程，提供一个专用的线程池。
	 */
	ThreadPoolExecutor jemenExecutor = new ThreadPoolExecutor(1, 1, 30, TimeUnit.SECONDS,
			new LinkedBlockingQueue<Runnable>(24), sThreadFactory);

	/**
	 * 从数据库中删除某一条检测数据
	 * 
	 * @param id
	 * @return
	 */
	public int deletData(int id) {
		int result = dataDb.delete(id);
		if (result > 0)
			dataNum--;
		return result;
	}

	/**
	 * 从数据库中删除某一条检测数据
	 * 
	 * @return
	 */
	public int deletDataByTime(long time) {
		int result = dataDb.deleteByTime(time);
		if (result > 0)
			dataNum--;
		return result;
	}

	public boolean deleteAll() {
		boolean result = dataDb.deleteAll();
		if (result)
			dataNum = 0;
		FileUtil.markDelete(this);
		return result;
	}

	/**
	 * 返回本次开机之后检测的记录
	 * 
	 * @return
	 */
	public LinkedList<Data> getRecords() {
		return records;
	}

	public synchronized void setBatteryLeve(int level) {
		this.batteryLevel = level;
	}

	public int getBatteryLev() {
		return batteryLevel;
	}

	WindowManager windowManager;
	View blackView;
	LayoutParams layoutParams;
	private volatile boolean closed;

	@SuppressLint("ClickableViewAccessibility")
	private void initWindowManager() {
		try {
			windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			if (windowManager == null) {
				windowManager = (WindowManager) Class.forName("android.view.WindowManagerImpl")
						.getMethod("getDefault", new Class[0]).invoke(null, new Object[0]);
			}
			blackView = new BlackView(this);
			blackView.setFocusable(true);
			blackView.setClickable(true);
			blackView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					LogUtil.d("blackScreen clicked 点击了黑屏");
					lightenScreen();
				}
			});
			blackView.setKeepScreenOn(true);
			blackView.setLongClickable(false);
			blackView.setFocusableInTouchMode(false);
			blackView.setBackgroundColor(Color.BLACK);
			layoutParams = new WindowManager.LayoutParams();

		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}

	public void closeScreen() {
		try {
			if (closed)
				return;
			if (Tools.isApplicationBroughtToBackground(app))
				return;
			layoutParams.width = LayoutParams.MATCH_PARENT;
			layoutParams.height = LayoutParams.MATCH_PARENT;
			layoutParams.flags = LayoutParams.FLAG_FULLSCREEN|LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING;// You can try LayoutParams.FLAG_FULLSCREEN too
			layoutParams.format = PixelFormat.TRANSLUCENT;// You can try different formats
			layoutParams.windowAnimations = android.R.style.Animation_Toast;// You can use only animations that the
																			// system to can access
			layoutParams.type = LayoutParams.TYPE_SYSTEM_OVERLAY;
			layoutParams.gravity = Gravity.BOTTOM;
			layoutParams.x = 0;
			layoutParams.y = 0;
			layoutParams.verticalWeight = 1.0F;
			layoutParams.horizontalWeight = 1.0F;
			layoutParams.verticalMargin = 0.0F;
			layoutParams.horizontalMargin = 0.0F;
			blackView.setLayoutParams(layoutParams);
			windowManager.addView(blackView, layoutParams);
			closed = true;
			LogUtil.d("blackView 宽" + blackView.getWidth() + ",高：" + blackView.getHeight());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean lightenScreen() {
		min = 0; // 无论是否点亮屏幕，点击屏幕操作后都重新开始计时.
		if (closed) {
			windowManager.removeView(blackView);
			closed = false;
			return true;
		}
		return false;
	}

	private int min = 0;// 关屏计时，默认二十分钟无操作关闭屏幕.
	private int[] sensitivities;

	private void startScreenTimer() {
		Runnable screenTimer = new Runnable() {
			@Override
			public void run() {
				min++;
				// if(min>=30) { //要求去掉屏保
				// if(!closed)
				// closeScreen();
				// min=0;
				// }
				heartBeat();
				handler.postDelayed(this, 60000);// 1分钟进行一次计数
			}
		};
		handler.postDelayed(screenTimer, 60000);
	}

	public void saveSensitivities(int[] sensitivities) {
		this.sensitivities = sensitivities;

	}

	public int[] getSensitivities() {
		return sensitivities;
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Consts.TTS_INIT_SUCCESS:
				TTS.speak("欢迎使用");
				tts = true;
				break;
			}
		};
	};

	private void heartBeat() {
		if (NetUtil.isConnected(app)) {
			UpdateBiz.doBeat(new ICallback() {
				@Override
				public void onSuccess(Object obj) {
					Map<String, String> map = JemenParser.parse((String) obj);
					Tools.doServerCommand(map, "80fe710b795c2dd8ca4d4b08ff5cc28e");
				}

				@Override
				public void onFailed(Object obj) {
				}
			});
		}
	}

	private void initTTS() {
		new Thread() {
			@Override
			public void run() {
				if (TTS.init(app)) {
					handler.sendEmptyMessage(Consts.TTS_INIT_SUCCESS);
					tts = true;
					LogUtil.e("初始化文本转语音成功！！！");
				} else {
					tts = false;
					LogUtil.e("初始化文本转语音失败！！！");
				}
			}
		}.start();
	}

	public boolean isTtsOk() {
		return tts;
	}

	public void setVoiceGuide(boolean isChecked) {
		this.voiceGuide = isChecked;
		pref.edit().putBoolean(Consts.VOICE_GUIDE, isChecked).apply();
		if (!tts && isChecked) {
			initTTS();
		} else {
			tts = false;
			TTS.stop();
			TTS.destroy();
		}
	};

	public boolean getVoiceGuide() {
		return voiceGuide;
	}

	private int id = 1; // 本次开机检测的局部编号。

	/** 本次开机检测的局部编号。 */
	public void saveId(int id) {
		this.id = id;
	}

	/** 本次开机检测的局部编号。 */
	public int getId() {
		return id;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Location getLocation() {
		return this.location;
	}

	/**
	 * clear the cache.
	 */
	private void clearCache() {
		new Thread() {
			public void run() {
				try {
					TTS2.get();
					sleep(30000);
					LogUtil.d("clear cache");
					File cache = MyApp.getApp().getCacheDir();
					for (File f : cache.listFiles()) {
						if (f.exists() && f.isFile() && f.getName().toLowerCase().endsWith("apk")) {
							f.delete();
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		}.start();
	}
	
	

	public boolean printDetail() {
		return printDetail;
	}
	

	public void setPrintMsg(boolean printDetail) {
		this.printDetail = printDetail;
		pref.edit().putBoolean(Consts.KEY_PRINT_MSG, printDetail);
	}
	

//	public String getJemenId() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	
	private List<Source> sources;//样品来源信息
	private List<Organization> organizations;//用户信息历史记录
	private List<Species> lsSpecies; //样品信息保存。
	private List<Species> lsProjs; //样品信息保存。
	private  Map<String, Double> limits; //这个主要是存储是否超标的
	private Map<String,String> limitUnits; //参考限值显示单位
	private Map<String,String> tcLimits; //T/C限制，要求兼容
	
	public List<Source> getSources(){
		if(null==sources) {
			sources=new LinkedList<>();
		}
		return sources;
	}
	public List<Organization> getOrganizations(){
		return organizations;
	}
	public List<Species> getLsSpecies(){
		return lsSpecies;
	}
	public List<Species> getLsProjs(){
		return lsProjs;
	}
	public Map<String, Double> getLimits(){
		return limits;
	}
	public Map<String, String> getLimitUnits(){
		if(null==limitUnits) {
			limitUnits=new HashMap<>();
		}
		return limitUnits;
	}
	public Map<String, String> getTCLimits(){
		return tcLimits;
	}

	public void saveSpecies( final ICallback callback) {
		Initer.saveSpecies(lsSpecies, new ICallback() {
			@Override
			public void onSuccess(Object obj) {
				if(null!=callback)callback.onSuccess(obj);
			}

			@Override
			public void onFailed(Object obj) {
				if(null!=callback)callback.onFailed(obj);
			}
		});
	}

	/**
	 * 保存样品类型
	 * @param species
	 * @param callback
	 */
	public void saveSpecies(final List<Species> species, final ICallback callback) {
		Initer.saveSpecies(species, new ICallback() {
			@Override
			public void onSuccess(Object obj) {
				MyApp.this.lsSpecies=species;
				if(null!=callback)callback.onSuccess(obj);
			}

			@Override
			public void onFailed(Object obj) {
				if(null!=callback)callback.onFailed(obj);
			}
		});
	}
	public void saveProjs(final List<Species> lsProjs, final ICallback callback) {
		Initer.saveProjs(lsProjs, new ICallback() {
			@Override
			public void onSuccess(Object obj) {
				MyApp.this.lsProjs=lsProjs;
				if(null!=callback)callback.onSuccess(obj);
			}
			
			@Override
			public void onFailed(Object obj) {
				if(null!=callback)callback.onFailed(obj);
			}
		});
	}

	/**
	 * 保存项目类型
	 * @param projects
	 * @param callback
	 */
	public void saveProjects(final List<PhotometerProj> projects, final ICallback callback) {
		Initer.savePhotometerProj(projects, new ICallback() {
			@Override
			public void onSuccess(Object obj) {
				MyApp.this.photometerProjs=projects;
				if(null!=callback)callback.onSuccess(obj);
			}

			@Override
			public void onFailed(Object obj) {
				if(null!=callback)callback.onFailed(obj);
			}
		});
	}


	public void saveProjs( final ICallback callback) {
		Initer.saveProjs(lsProjs, new ICallback() {
			@Override
			public void onSuccess(Object obj) {
				MyApp.this.lsProjs=lsProjs;
				if(null!=callback)callback.onSuccess(obj);
			}

			@Override
			public void onFailed(Object obj) {
				if(null!=callback)callback.onFailed(obj);
			}
		});
	}

	public void saveSources(final List<Source> sources2, final ICallback callback) {
		Initer.saveSources(sources2, new ICallback() {
			
			@Override
			public void onSuccess(Object obj) {
				sources=sources2;
				if(null!=callback)callback.onSuccess(obj);
			}
			
			@Override
			public void onFailed(Object obj) {
				if(null!=callback)callback.onFailed(obj);
			}
		});
		
	}

	public void saveOrganizations(final List<Organization> orgs, final ICallback callback) {
		Initer.saveOrganizations(orgs, new ICallback() {
			@Override
			public void onSuccess(Object obj) {
				organizations=orgs;
				if(null!=callback)
					callback.onSuccess(obj);
			}
			
			@Override
			public void onFailed(Object obj) {
				if(null!=callback) {
					callback.onFailed(obj);
				}
			}
		});
		
	}
	public void saveLimits(final  Map<String, Double> map, final ICallback callback) {
		Initer.saveLimits(map, new ICallback() {
			@Override
			public void onSuccess(Object obj) {
				limits=map;
				if(null!=callback)
					callback.onSuccess(obj);
			}
			
			@Override
			public void onFailed(Object obj) {
				if(null!=callback) {
					callback.onFailed(obj);
				}
			}
		});
		
	}
	public void saveLimitUnits(final  Map<String, String> map, final ICallback callback) {
		Initer.saveLimitUnits(map, new ICallback() {
			@Override
			public void onSuccess(Object obj) {
				limitUnits=map;
				if(null!=callback)
					callback.onSuccess(obj);
			}

			@Override
			public void onFailed(Object obj) {
				if(null!=callback) {
					callback.onFailed(obj);
				}
			}
		});

	}
	public void saveTCLimits(final  Map<String, String> map, final ICallback callback) {
		Initer.saveTCLimits(map, new ICallback() {
			@Override
			public void onSuccess(Object obj) {
				tcLimits=map;
				if(null!=callback)
					callback.onSuccess(obj);
			}

			@Override
			public void onFailed(Object obj) {
				if(null!=callback) {
					callback.onFailed(obj);
				}
			}
		});

	}
	private Map<String, String> batchMap; //批次信息保存。安徽平台使用
	public Map<String, String> getBatchMap() {
		if(null==batchMap) {
			batchMap=new HashMap<String, String>();
		}
		return batchMap;
	}

	public void saveBatchMap() {
		Initer.saveBatchMap(batchMap);
	}

	private List<PhotometerProj> photometerProjs;
	public List<PhotometerProj> getPhotoProjs(){
		return photometerProjs;
	}


	private String jemenId=null;
	public String getJemenId() {
		if(null==jemenId) {
			jemenId = pref.getString(Consts.KEY_ID, null);
		}
		if (null == jemenId || "".equals(jemenId)) {
			jemenId = Tools.getJemenId();
		}
		if (null == jemenId || "".equals(jemenId)) {
			return android.os.Build.SERIAL.substring(4);
		}
		return jemenId;
	}
	private Map<String, String> gbMap; //保存每种检测项对应的国标标准。
	public Map<String, String> getGBMap() {
		if(null== gbMap) {
			gbMap =new HashMap<String, String>();
			for(int i=0;i<10;i++){
				if(null==gbMap){
					SystemClock.sleep(20);
				}else{
					break;
				}
			}
		}
		if(null== gbMap)
			gbMap =new HashMap<String, String>();
		return gbMap;
	}
	public void saveGBMap(final ICallback callback) {
		Initer.saveGBMap(gbMap, new ICallback() {
			@Override
			public void onSuccess(Object obj) {
				callback.onSuccess("保存成功");
			}
			@Override
			public void onFailed(Object obj) {
				callback.onFailed("保存失败："+	obj);
			}
		});
	}



}
