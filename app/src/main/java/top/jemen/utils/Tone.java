package top.jemen.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import com.whswzz.prfluroanalyzer.app.MyApp;

import android.app.Service;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import top.jemen.utils.threadpool.AsyncProcessor;

public class Tone {
	Map<Integer, Integer> map;
	SoundPool soundPool; 
	private static Tone tone;
	private Tone() {
		soundPool = new SoundPool(10, AudioManager.STREAM_NOTIFICATION, 0);
		map=new HashMap<Integer, Integer>();
		LogUtil.d("map="+map);
		
		AudioManager audio = (AudioManager) MyApp.getApp().getSystemService(Service.AUDIO_SERVICE);
		int max = audio.getStreamMaxVolume( AudioManager.STREAM_SYSTEM );
		audio.setStreamVolume( AudioManager.STREAM_SYSTEM, (int) (max*0.92), AudioManager.FLAG_PLAY_SOUND );
		
//		audio.setStreamVolume( AudioManager.STREAM_NOTIFICATION,
//				(int) (max*0.92),AudioManager.FLAG_PLAY_SOUND
//                        |AudioManager.FLAG_SHOW_UI);
	}
	
	public static Tone get() {
		if(null==tone) {
			tone=new Tone();
		}
		return tone;
	}
	
	public void play(final int rawId) {
		if(!MyApp.getApp().getVoiceGuide()) {
			return;
		}
		try {
			AsyncProcessor.executeTask(new Runnable() {
				@Override
				public void run() {
					LogUtil.d("map=" + map);
					final Integer soundId = map.get(rawId);
					if (soundId != null) {
						soundPool.play(soundId, 1, 1, 0, 0, 1);//也有可能比较耗时，固亦放在子线程了。
					} else {
						soundPool.load(MyApp.getApp(), rawId, 1);
						soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
							@Override
							public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
								map.put(rawId, sampleId);
								soundPool.play(sampleId, 1, 1, 0, 0, 1);
							}
						});
					}
				}
			});

		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}

		
	}
}
