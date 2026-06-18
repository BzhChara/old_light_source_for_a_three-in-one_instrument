package top.jemen.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.whswzz.prfluroanalyzer.app.MyApp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

import top.jemen.Consts;
import top.jemen.interfaces.ICallback;

public class TTS2 {
	private Context context= MyApp.getApp();
	private static TTS2 singleton;
	private TextToSpeech tts; // TTS对象
	private boolean isOk = false;
	private float pitch=0.9f;
	private float speeckRate=0.85f;
			;




	public static TTS2 get( ) {
		if (singleton == null) {
			synchronized (TTS2.class) {
				if (singleton == null) {
					singleton = new TTS2();
				}
			}
		}
		return singleton;
	}

	public TTS2() {
		// 获取TTS引擎的package信息
		PackageManager packageManager = context.getPackageManager();
		List<ResolveInfo> list = packageManager.queryIntentActivities(
				new Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA), PackageManager.MATCH_DEFAULT_ONLY);
		if (list.size() == 0) {
			LogUtil.d("没有找到TTS引擎");
			return;
		}

		for (ResolveInfo info : list) {
			Log.d("jemen", "TTS引擎包名：" + info.activityInfo.packageName);
		}
		// 引擎包名：com.svox.pico TTS
		// 引擎包名：com.google.android.tts
		// 引擎包名：com.iflytek.speechcloud //讯飞

		int x = list.size() - 1;
		initTTS(list, x);

	}

	private void initTTS(final List<ResolveInfo> list, final int x) {
		tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int i) {
				if (i == TextToSpeech.SUCCESS) {

					// 判断是否支持下面两种语言（英文/中文）
					int result1 = tts.setLanguage(Locale.US);
					int result2 = tts.setLanguage(Locale.SIMPLIFIED_CHINESE);
					boolean a = (result1 == TextToSpeech.LANG_MISSING_DATA
							|| result1 == TextToSpeech.LANG_NOT_SUPPORTED);
					boolean b = (result2 == TextToSpeech.LANG_MISSING_DATA
							|| result2 == TextToSpeech.LANG_NOT_SUPPORTED);
					if (!b) {
						Log.d("jemen", list.get(x).activityInfo.packageName+"US是否支持？--》" + !a + "\nzh-CN是否支持？--》" + !b);
						tts.setPitch(pitch);// 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
						tts.setSpeechRate(speeckRate);
						isOk = true;
					} else {
						if (x > 0) {
							initTTS(list, x - 1);
						}
					}
				} else {
					Log.d("jemen", "播报引擎加载失败");
					if (x > 0) {
						initTTS(list, x - 1);
					}
				}
			}
		}, list.get(x).activityInfo.packageName);
	}

	public void speakText(String text) {
			speakText(text, TextToSpeech.QUEUE_FLUSH);
	}

	public void speakText(String text, int queueMode) {
		if (tts != null&&isOk) {
			tts.speak(text, queueMode, null);
		}
	}
	public boolean isOk() {
		return isOk;
	}
	
	

	public void close() {
		if (tts != null) {
			tts.stop();
			tts.shutdown();
			tts = null;
			singleton = null;
		}
	}

	public void stop() {
		if (tts != null) {
			tts.stop();
		}
	}
	
	
	
	
	
	/**
	 * 联网时候检查googletts引擎
	 */
	public void checkDownloadTTS() {
		if(isOk) {
			return;
		}
		PackageManager packageManager = context.getPackageManager();
		List<ResolveInfo> list = packageManager.queryIntentActivities(
				new Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA), PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo info : list) {//// 引擎包名：com.google.android.tts  com.iflytek.speechcloud //讯飞
			if(info.activityInfo.packageName.contains("google")||info.activityInfo.packageName.contains("iflytek")) {
				return;
			}
		}
		
		String cacheRoot = MyApp.getApp().getFilesDir().getAbsolutePath();
		final String filePath = cacheRoot + "/googletts.apk";
		File file = new File(filePath);
		if(file.exists()) {
			file.delete();
		}
		LogUtil.d("下载谷歌引擎");
//		NetUtil.downloadFile("http://qnhas.jemen.top/app/tts-google.apk",filePath,new ICallback() {
		NetUtil.downloadFile("http://qnhas.jemen.top/app/tts-xunfeiold.apk",filePath,new ICallback() {
			@Override
			public void onSuccess(Object obj) {
				if(isOk){
					return;
				}
				installEngine(filePath);
			}
			
			@Override
			public void onFailed(Object obj) {
				LogUtil.d("download google tts engine failed:"+obj);
			}
		});
	}
	
	
	public void installEngine(String apkPath) {
		installAPK25(apkPath, new ICallback() {
			@Override
			public void onSuccess(Object obj) {
				LogUtil.d("谷歌引擎安装成功");
				tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
					@Override
					public void onInit(int i) {
						if (i == TextToSpeech.SUCCESS) {
							// 判断是否支持下面两种语言（英文/中文）
							int result2 = tts.setLanguage(Locale.SIMPLIFIED_CHINESE);
							boolean b = (result2 == TextToSpeech.LANG_MISSING_DATA
									|| result2 == TextToSpeech.LANG_NOT_SUPPORTED);
							if (!b) {
								tts.setPitch(pitch);// 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
								tts.setSpeechRate(speeckRate);
								isOk = true;
								LogUtil.d("tts初始化成功，并且支持中文");
							} 
						} else {
							Log.d("jemen", "播报引擎加载失败");
						}
					}
				}, "com.google.android.tts");
			}
			
			@Override
			public void onFailed(Object obj) {
				LogUtil.d("谷歌引擎安装失败");
			}
		});
		
		
	}
	
	
	// 翻译并执行相应的adb命令
			public static String exec(String command) {
				Process process = null;
				DataOutputStream os = null;
				BufferedReader br=null;
				String r=null;
				try {
					process = Runtime.getRuntime().exec("su");
					os = new DataOutputStream(process.getOutputStream());
					os.writeBytes(command + "\n");
					os.writeBytes("exit\n");
					os.flush();
					Log.d("bootUtil", "======000==writeSuccess======");
					process.waitFor();
					 br=new BufferedReader(new InputStreamReader(process.getInputStream()));
					StringBuilder sb=new StringBuilder();
					String line;
					while((line=br.readLine())!=null) {
						sb.append(line).append("\n");
					}
					
					r=sb.toString();
				} catch (Exception e) {
					e.printStackTrace();
					try {
						if (os != null) {
							os.close();
						}
						if(null!=br) {
							br.close();
						}
						if (process != null) {
							process.destroy();
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				return r;
			}
			public static void installAPK25(final String path,final ICallback callback) {
				if(null==path) {
					callback.onFailed("path is null");
				}
				if(new File(path).length()<1000) {
					callback.onFailed("file is to small,may be error");
				}
				new Thread() {
					public void run() {
						try {
							boolean isOk=false;
							
							exec("chmod 777 "+path);
							final String r=exec("pm install -r "+path);
							Consts.HANDER.post(new Runnable() {
								@Override
								public void run() {
									if(r!=null&&r.toLowerCase().contains("success")) {
										callback.onSuccess("install success");
									}else {
										callback.onSuccess("install failed");
									}
								}
							});
						} catch (Exception  e) {
							e.printStackTrace();
							callback.onFailed(e.getMessage());
						}
						
					};
					
				}.start();
			}

	
}
