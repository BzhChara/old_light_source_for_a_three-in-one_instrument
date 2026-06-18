package com.zkzk.pra.utils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.baidu.tts.auth.AuthInfo;
import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.whswzz.prfluroanalyzer.app.MyApp;

import android.annotation.SuppressLint;
/* 
* 默认TEMP_DIR = "/sdcard/baiduTTS"; // 重要！请手动将assets目录下的3个dat 文件复制到该目录
* 确保 TEXT_FILENAME 和 MODEL_FILENAME 存在
* Created by fujiayi on 2017/9/14.
*/
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import top.jemen.utils.LogUtil;

public class TTS {
	private static String TEXT = "这里是武汉市农业科学院环境与安全研究所，正在进行语音合成测试。";


    // ================== 初始化参数设置开始 ==========================
    /**
     * 发布时请替换成自己申请的appId appKey 和 secretKey。注意如果需要离线合成功能,请在您申请的应用中填写包名。
     * 本demo的包名是com.baidu.tts.sample，定义在build.gradle中。
     */
    protected static String appId = "11106947";

    protected static String appKey = "wHQZdEF0RdMvw0yz4jTMvsc0";

    protected static String secretKey = "4e6894874a8a1c4518a14f9490fde695";

    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    private static TtsMode ttsMode = TtsMode.MIX;

    // ================选择TtsMode.ONLINE  不需要设置以下参数; 选择TtsMode.MIX 需要设置下面2个离线资源文件的路径
	private static  String TEMP_DIR = "/data/ECAS/"; // 重要！请手动将assets目录下的3个dat 文件复制到该目录

    // 请确保该PATH下有这个文件
    private static  String TEXT_FILENAME = TEMP_DIR + "/" + "bd_etts_text.dat";

    // 请确保该PATH下有这个文件 ，m15是离线男声
    private static  String MODEL_FILENAME =
            TEMP_DIR + "/" + "bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat";

    // ===============初始化参数设置完毕，更多合成参数请至getParams()方法中设置 =================

    protected static SpeechSynthesizer mSpeechSynthesizer;



    protected static Handler mainHandler;
	public static boolean init(Context context) {
		return initTTs(context);
	}
	
	public static void destroy() {
		if (mSpeechSynthesizer != null) {
            mSpeechSynthesizer.stop();
            mSpeechSynthesizer.release();
            mSpeechSynthesizer = null;
        }
	}
	

	/**
     * 注意此处为了说明流程，故意在UI线程中调用。
     * 实际集成中，该方法一定在新线程中调用，并且该线程不能结束。具体可以参考NonBlockSyntherizer的写法
     */
    private static boolean initTTs(Context context) {
        try {
			LoggerProxy.printable(true); // 日志打印在logcat中
			boolean isMix = ttsMode.equals(TtsMode.MIX);
			boolean isSuccess;
			if (isMix) {
			    // 检查2个离线资源是否可读
			    isSuccess = checkOfflineResources(context);
			    Log.e("jemen","检查离线资源结果="+isSuccess);
			    
			    if (!isSuccess) {
			    	Log.e("jemen","检查离线资源失败");
			        return false;
			    } else {
			        print("离线资源存在并且可读, 目录：" + TEMP_DIR);
			    }
			}
			// 日志更新在UI中，可以换成MessageListener，在logcat中查看日志
			SpeechSynthesizerListener listener = new MessageListener();

			// 1. 获取实例
			mSpeechSynthesizer = SpeechSynthesizer.getInstance();
			if(mSpeechSynthesizer==null)	return false;
			mSpeechSynthesizer.setContext(context);

			// 2. 设置listener
			mSpeechSynthesizer.setSpeechSynthesizerListener(listener);

			// 3. 设置appId，appKey.secretKey
			int result = mSpeechSynthesizer.setAppId(appId);
			checkResult(result, "setAppId");
			result = mSpeechSynthesizer.setApiKey(appKey, secretKey);
			checkResult(result, "setApiKey");

			// 4. 支持离线的话，需要设置离线模型
			if (isMix) {
			    // 检查离线授权文件是否下载成功，离线授权文件联网时SDK自动下载管理，有效期3年，3年后的最后一个月自动更新。
			    isSuccess = checkAuth();
			    if (!isSuccess) {
			        return false;
			    }
			    // 文本模型文件路径 (离线引擎使用)， 注意TEXT_FILENAME必须存在并且可读
			    mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, TEXT_FILENAME);
			    // 声学模型文件路径 (离线引擎使用)， 注意TEXT_FILENAME必须存在并且可读
			    mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, MODEL_FILENAME);
			}

			// 5. 以下setParam 参数选填。不填写则默认值生效
			// 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
			mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
			// 设置合成的音量，0-9 ，默认 5
			mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "8");
			// 设置合成的语速，0-9 ，默认 5
			mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");
			// 设置合成的语调，0-9 ，默认 5
			mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");

			mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
			// 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
			// MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
			// MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
			// MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
			// MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线

			mSpeechSynthesizer.setAudioStreamType(AudioManager.MODE_IN_CALL);

			// x. 额外 ： 自动so文件是否复制正确及上面设置的参数
			Map<String, String> params = new HashMap<>();
			// 复制下上面的 mSpeechSynthesizer.setParam参数
			// 上线时请删除AutoCheck的调用
			if (isMix) {
			    params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, TEXT_FILENAME);
			    params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, MODEL_FILENAME);
			}
			InitConfig initConfig =  new InitConfig(appId, appKey, secretKey, ttsMode, params, listener);
			LogUtil.d("下面创建开启loop和创建handler");
			Looper.prepare();
		
			Handler handler=new Handler() {
			    /**
			     * 开新线程检查，成功后回调
			     */
			    public void handleMessage(Message msg) {
			        if (msg.what == 100) {
			            AutoCheck autoCheck = (AutoCheck) msg.obj;
			            synchronized (autoCheck) {
			                String message = autoCheck.obtainDebugMessage();
			                print(message); // 可以用下面一行替代，在logcat中查看代码
			                // Log.w("AutoCheckMessage", message);
			            }
			        }
			    }

			};
			AutoCheck.getInstance(MyApp.getApp()).check(initConfig, handler);
//			Looper.loop();//加上此句则线程死循环了。
			LogUtil.d("初始化tts,mSpeechSynthesizer.initTts");
			// 6. 初始化
			result = mSpeechSynthesizer.initTts(ttsMode);
			checkResult(result, "initTts");
			if(result==0||result==-204)		//-204仅在线初始化成功
				return true;//0代表成功。
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
        return false;
    }

    /**
     * 检查appId ak sk 是否填写正确，另外检查官网应用内设置的包名是否与运行时的包名一致。
     *
     * @return
     */
    private static boolean checkAuth() {
        AuthInfo authInfo = mSpeechSynthesizer.auth(ttsMode);
        if (!authInfo.isSuccess()) {
            // 离线授权需要网站上的应用填写包名。本demo的包名是com.baidu.tts.sample，定义在build.gradle中
            String errorMsg = authInfo.getTtsError().getDetailMessage();
            print("【error】鉴权失败 errorMsg=" + errorMsg);
            return false;
        } else {
            print("验证通过，离线正式授权文件存在。");
            return true;
        }
    }
    

    /**
     * 检查 TEXT_FILENAME, MODEL_FILENAME 这2个文件是否存在，不存在请自行从assets目录里手动复制
     *
     * @return
     */
    private static boolean checkOfflineResources(Context context) {
    	TEMP_DIR=context.getFilesDir().getAbsolutePath();
        TEXT_FILENAME = TEMP_DIR + "/" + "bd_etts_text.dat";

        MODEL_FILENAME =TEMP_DIR + "/" + "bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat";
        
    	
        String[] filenames = {TEXT_FILENAME, MODEL_FILENAME};
        for (String path : filenames) {
            File f = new File(path);
		    if(!f.getParentFile().exists()) {
		    	f.mkdirs();
		    }
		    if(!f.exists()) {
		    	try {
					f.createNewFile();
					InputStream is = context.getAssets().open(path.substring(path.lastIndexOf('/')+1));
					FileOutputStream out=new FileOutputStream(path);
					byte[] buf=new byte[1024];
					int len=0;
					while((len=is.read(buf))>0) {
						out.write(buf,0,len);
					}
					is.close();
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
		    }else if(f.length()<1024) {
		    	
		    }
            
            if (!f.canRead()) {
                print("[ERROR] 文件不存在或者不可读取，请从assets目录复制同名文件到：" + path);
                print("[ERROR] 初始化失败！！！");
                return false;
            }
        }
        return true;
    }
    public static void speak(String text) {
        /* 以下参数每次合成时都可以修改
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
         *  设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "5"); 设置合成的音量，0-9 ，默认 5
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5"); 设置合成的语速，0-9 ，默认 5
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5"); 设置合成的语调，0-9 ，默认 5
         *
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
         *  MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
         *  MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
         *  MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
         *  MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
         */

        if (mSpeechSynthesizer == null) {
            print("[ERROR], 初始化失败");
            return;
        }
        int result = mSpeechSynthesizer.speak(text);
        print("合成并播放 ");
        checkResult(result, "speak");
        
    }

    public static void stop() {
    	try {
			if(mSpeechSynthesizer!=null) {
			    print("停止合成引擎");
			    int result = mSpeechSynthesizer.stop();
			    checkResult(result, "stop");
			}
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
    }

	
	
	
	
    private static void checkResult(int result, String method) {
        if (result != 0) {
            print("error code :" + result + " method:" + method + ", 错误码文档:http://yuyin.baidu.com/docs/tts/122 ");
        }
    }

	private static void print(String msg) {
		LogUtil.d(msg);
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
