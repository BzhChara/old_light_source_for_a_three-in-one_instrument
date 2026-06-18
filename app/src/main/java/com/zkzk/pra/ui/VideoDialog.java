package com.zkzk.pra.ui;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.consts.Consts;
import com.zkzk.pra.R;
import top.jemen.utils.NetUtil;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import top.jemen.utils.LogUtil;
import android.widget.Toast;

public class VideoDialog extends BaseDialog implements android.view.View.OnClickListener {
	private SurfaceView sfv;// 能够播放图像的控件
	private SeekBar sb,sbVoice;// 进度条
	private String path="/sdcard/help_pra.mp4";// 本地文件路径
	private SurfaceHolder holder;
	private MediaPlayer player;// 媒体播放器
	private Timer timer;// 定时器
	private TimerTask task;// 定时器任务
	private Context mContext;
	private Button btPlay,btPause,btStop,btReplay;
	private LinearLayout llCtl;
	private float volume=0.3f;
	private LayoutParams lp;
	private Window window;
	private static int position=0;
	private static int duration=-1;
	public VideoDialog(Context context) {
		super(context,R.style.dialog);
		mContext = context;
	}
	
	public VideoDialog(Context context,String path) {
		this(context);
		this.path=path;
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.dialog_video);
		initView();
		setListeners();
//		LogUtil.d("progress="+position);
//		new Handler().postDelayed(new Runnable(		) {
//			@Override
//			public void run() {
//				play();	//放到holder的surfaceCreated回调中去。
//			}
//		}, 100);
	}
	
	
	
	private void setListeners() {
		btPause.setOnClickListener(this);
		btPlay.setOnClickListener(this);
		btReplay.setOnClickListener(this);
		btStop.setOnClickListener(this);
		sfv.setOnClickListener(this);
		sbVoice.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				volume=progress/100.f;
				if(null!=player)
					player.setVolume(volume,volume);
				MyApp.getApp().getPref().edit().putFloat(Consts.VOLUME, volume).commit();
			}
		});
	}

	// 初始化控件，并且为进度条和图像控件添加监听
		private void initView() {
			sfv = (SurfaceView) findViewById(R.id.sfv);
			sb = (SeekBar) findViewById(R.id.sb);
			sbVoice=(SeekBar) findViewById(R.id.sb_voice);
			sbVoice.setMax(100);
			volume=MyApp.getApp().getPref().getFloat(Consts.VOLUME, volume);
			sbVoice.setProgress((int)(volume*100));
			if(duration>0)	sb.setMax(duration);
			if(position>0)  sb.setProgress(position);
			btPlay=(Button) findViewById(R.id.bt_play);
			btPause=(Button) findViewById(R.id.bt_pause);
			btStop=(Button) findViewById(R.id.bt_stop);
			btReplay=(Button) findViewById(R.id.bt_replay);
			llCtl=(LinearLayout) findViewById(R.id.ll_ctl);
			btPlay.setEnabled(false);
			holder = sfv.getHolder();
//			holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//3.0以下的版本才涉及
			sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

				}
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {

				}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// 当进度条停止拖动的时候，把媒体播放器的进度跳转到进度条对应的进度
					position=seekBar.getProgress();//
					if (player != null) {
						player.seekTo(position);
					}
				}
			});

			holder.addCallback(new SurfaceHolder.Callback() {
				@Override
				public void surfaceCreated(SurfaceHolder holder) {
					// 为了避免图像控件还没有创建成功，用户就开始播放视频，造成程序异常，所以在创建成功后才使播放按钮可点击
					Log.d("zhangdi", "surfaceCreated");
					btPlay.setEnabled(true);
					play();
				}

				@Override
				public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
					Log.d("zhangdi", "surfaceChanged");
				}
				@Override
				public void surfaceDestroyed(SurfaceHolder holder) {
					// 当程序没有退出，但不在前台运行时，因为surfaceview很耗费空间，所以会自动销毁，
					// 这样就会出现当你再次点击进程序的时候点击播放按钮，声音继续播放，却没有图像
					// 为了避免这种不友好的问题，简单的解决方式就是只要surfaceview销毁，我就把媒体播放器等
					// 都销毁掉，这样每次进来都会重新播放，当然更好的做法是在这里再记录一下当前的播放位置，
					// 每次点击进来的时候把位置赋给媒体播放器，很简单加个全局变量就行了。
					Log.d("zhangdi", "surfaceDestroyed");
					if (player != null) {
						stop();
					}
				}
			});
			window = this.getWindow();
			lp = window.getAttributes();
		}

		private void play() {
			if(null==holder&&!holder.isCreating()) {
				return;
			}
			
			
			btPlay.setEnabled(false);// 在播放时不允许再点击播放按钮
			if (isPause) {// 如果是暂停状态下播放，直接start
				isPause = false;
				player.start();
				isPalying=true;
				return;
			}
			// path = Environment.getExternalStorageDirectory().getPath()+"/";
			File file = new File(path);
			if (!file.exists()) {// 判断需要播放的文件路径是否存在，不存在退出播放流程
				if(NetUtil.isConnected(MyApp.getApp())) {
					path="http://58.49.112.42:8181/PRA/help.mp4";
				}else {
					path="/system/media/help_pra.mp4";
					file = new File(path);
					if(!file.exists()) {
						Toast.makeText(mContext, "文件路径不存在", Toast.LENGTH_LONG).show();
						return;
					}
				}
				
			}
			
			try {
				if(null==player) {
					player = new MediaPlayer();
				}
				player.reset();
				player.setDataSource(path);
				

				player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {// 视频播放完成后，释放资源
						btPlay.setEnabled(true);
						stop();
						position=0;
					}
				});
				
				player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
					@Override
					public void onPrepared(MediaPlayer mp) {
						player.setDisplay(holder);// 将影像播放控件与媒体播放控件关联起来
						
						// 媒体播放器就绪后，设置进度条总长度，开启计时器不断更新进度条，播放视频
						duration=player.getDuration();
						sb.setMax(duration);
						sb.setProgress(position);
						timer = new Timer();
						task = new TimerTask(){
							@Override
							public void run() {
								if (player != null) {
									int time = player.getCurrentPosition();
									sb.setProgress(time);
								}
							}
						};
						timer.schedule(task, 0, 500);
						sb.setProgress(position);
						player.seekTo(position);
						player.setAudioStreamType(AudioManager.STREAM_MUSIC);
						player.start();
						player.setVolume(volume, volume);
						isPalying=true;
					}
				});

				player.prepareAsync();
			} catch (IOException e) {
				player.release();
			}
		}

		private boolean isPause;

		private void pause() {
			if (player != null && player.isPlaying()) {
				player.pause();
				position=player.getCurrentPosition();
				isPause = true;
				btPlay.setEnabled(true);
				isPalying=false;
			}
		}

		private void replay() {
			isPause = false;
			if (player != null) {
				stop();
				play();
			}
		}

		private void stop() {
			isPause = false;
			if (timer != null) {
				timer.cancel();
			}
			if (player != null) {
				position=player.getCurrentPosition();
				sb.setProgress(position);
				LogUtil.d("progress="+position);
				player.stop();
				player.release();
				player = null;
				btPlay.setEnabled(true);
				isPalying=false;
			}
		}

		
		@Override
		protected void onStop() {
			stop();
			super.onStop();
		}
		

		private boolean isPalying=false;
		@Override
		public void onClick(View v) {
			switch(v.getId()) {
//			case R.id.bt_play:
//				play();
//				break;
//			case R.id.bt_pause:
//				pause();
//				break;
//			case R.id.bt_stop:
//				stop();
//				break;
//			case R.id.bt_replay:
//				replay();
//				break;
			case R.id.sfv:
				if(isPalying)
					pause();
				else
					play();
				break;
			}
		}
		
		
		
		float x0 = 0,y0 = 0;	
		@Override
		public boolean dispatchTouchEvent(MotionEvent ev) {
			switch(ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				x0=ev.getX();
				y0=ev.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				float x = ev.getX();
				float y = ev.getY();
				if(Math.abs(x-x0)>10||Math.abs(y-y0)>10) {
					lp.x+=(int) (x-x0);
					lp.y+=(int) (y-y0);
					window.setAttributes(lp);
//					x0=x;	//因为直接平移了坐标系，所以x0与y0均不需要改变了。否则将导致抖动。
//					y0=y;
				}
				break;
			case MotionEvent.ACTION_UP:
				
				break;
			}
			return super.dispatchTouchEvent(ev);
		}
		
		

}
