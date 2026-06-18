package com.zkzk.pra.ui;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Environment;
import android.os.Handler;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.WindowManager.LayoutParams;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import top.jemen.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import org.xutils.x;
import org.xutils.view.annotation.ViewInject;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.consts.Consts;
import com.zkzk.pra.R;
import top.jemen.utils.NetUtil;


/**
 * @author Jemen Chen
 *
 */
public class VideoDialog2 extends BaseDialog implements android.view.View.OnClickListener {
	private static final String TAG = "tag";
	private  Context mContext;
    @ViewInject(R.id.texture_view)
    TextureView textureView;
    @ViewInject(R.id.bt_play)
    private Button btPlay;
    @ViewInject(R.id.bt_pause)
    private Button btPause;
    @ViewInject(R.id.sb_voice)
	private SeekBar sbVoice;
    @ViewInject(R.id.sb)
    private SeekBar sb;
    
    private String filePath= Environment.getExternalStorageDirectory().getPath() +"/help_ghma.mp4";// 本地文件路径
	private String path= filePath;
	private float volume=0.3f;
	private LayoutParams lp;
	private Window window;
	private static int position=0;
	private static int duration=-1;
	private MediaPlayer player;
	private Timer timer;// 定时器
	private TimerTask task;// 定时器任务
	private View root;
	
	
	public VideoDialog2(Context context){
		super(context,R.style.dialog);
		this.mContext=context;
	}

    public VideoDialog2(Context context, boolean cancelable, OnCancelListener cancelListener) {
    	super(context,R.style.dialog);
		this.mContext=context;
	}



    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root=LayoutInflater.from(mContext).inflate(R.layout.dialog_video2, null);
        setContentView(root);
        x.view().inject(this, root);
       
        initTextureView();
        textureView.setSurfaceTextureListener(surfaceTextureListener);
        setListeners();
        window = this.getWindow();
		lp = window.getAttributes();
		sbVoice.setProgress((int)(volume*100));
		
    }
	
    @Override
    protected void onStart() {
    	super.onStart();
    	 initMediaPlayer();
    }
    
	@Override
	protected void onStop() {
		stop();//will rease mediaplayer
		super.onStop();
	}
    
    

    private void setListeners() {
    	btPause.setOnClickListener(this);
    	btPlay.setOnClickListener(this);
    	textureView.setOnClickListener(this);
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
	}
    float scale=1.25f;
	private void initTextureView() {
        textureView.setAlpha(1.0f);
        if(scale>1) {
        	scale=0.8f;
        }else {
        	scale=1.25f;
        }
//        textureView.setScaleX(scale);
//        textureView.setScaleY(scale);
        android.view.ViewGroup.LayoutParams params = textureView.getLayoutParams();
        params.width*=scale;
        params.height*=scale;
        textureView.setLayoutParams(params);
        textureView.requestLayout();
        root.requestLayout();
    }

	private boolean isPalying=false;
	@Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.bt_play:
                Toast.makeText(mContext,"开始播放", Toast.LENGTH_SHORT).show();
                    play();
                break;
            case R.id.bt_pause:
                pause();
                break;
            case R.id.texture_view:
            	if(isPalying)
					pause();
				else
					play();
				break;
        }
    }

    private void vertial() {
        textureView.setRotation(0);
    }


    private void play() {
    	LogUtil.d("play");
		btPlay.setEnabled(false);// 在播放时不允许再点击播放按钮
		if (isPause) {// 如果是暂停状态下播放，直接start
			isPause = false;
			player.start();
			isPalying=true;
			return;
		}
		// path = Environment.getExternalStorageDirectory().getPath()+"/";
		File file = new File(path);
		if (!file.exists()||file.length()<1024*1024) {// 判断需要播放的文件路径是否存在，不存在退出播放流程
			if(NetUtil.isConnected(MyApp.getApp())) {
//				path="http://qnhas.jemen.top/media/praio.mp4";
//				DownloadBiz.download(null, path, filePath);
			}else {
				path="/system/media/praio_help.mp4";
				file = new File(path);
				if(!file.exists()) {
					Toast.makeText(mContext, "文件不存在", Toast.LENGTH_LONG).show();
					return;
				}
			}
			
		}
		
		try {
			if(null==player) {
				player = getMediaPlayer(getContext());
			}else {
				player.stop();
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
    private void restart() {
        if (player == null){
            return;
        }
        player.stop();
        play();
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
    private void initMediaPlayer() {
        AudioManager mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
        int mVolumn = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (player == null) {
            player = getMediaPlayer(getContext());
            player.setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                }
            });
            
            player.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
//                    Toast.makeText(mContext,"播放完成！继续从头播放", Toast.LENGTH_SHORT).show();
                    restart();
                }
            });
            
            player.setOnErrorListener(new OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    return false;
                }
            });
            
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setOnSeekCompleteListener(new OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {

                }
            });
            player.setVolume(mVolumn, mVolumn);

        } else {
            player.reset();
        }
    }


    
    private SurfaceTextureListener surfaceTextureListener = new SurfaceTextureListener() {

		@Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
			
            if (player == null){
                return;
            }
            Surface sf = new Surface(surface);//每次Dialog重新显示后surface是不一样的，每次都需要重新set
            player.setSurface(sf);
            play();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            stop();
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    
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
//				x0=x;	//因为直接平移了坐标系，所以x0与y0均不需要改变了。否则将导致抖动。
//				y0=y;
			}
			break;
		case MotionEvent.ACTION_UP:
			long t=System.currentTimeMillis();
			if(t-lastTouch<300) {
//				ToastUtil.showText("双击了哦", Toast.LENGTH_SHORT);
				initTextureView();
			}else {
				lastTouch=t;
			}
			break;
		}
		return super.dispatchTouchEvent(ev);
	}
	
	long lastTouch=0;
    
	
	
	
	
	
	
	
	
	/**不适用也无甚影响。
	 * 反射调用隐藏API已避免提示MediaPlayer: Should have subtitle controller already set
	 * @param context
	 * @return
	 */
	private MediaPlayer getMediaPlayer(Context context) {
        MediaPlayer mediaplayer = new MediaPlayer();
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
            return mediaplayer;
        }
        try {
            Class<?> cMediaTimeProvider = Class.forName("android.media.MediaTimeProvider");
            Class<?> cSubtitleController = Class.forName("android.media.SubtitleController");
            Class<?> iSubtitleControllerAnchor = Class.forName("android.media.SubtitleController$Anchor");
            Class<?> iSubtitleControllerListener = Class.forName("android.media.SubtitleController$Listener");
            Constructor constructor = cSubtitleController.getConstructor(
                    new Class[]{Context.class, cMediaTimeProvider, iSubtitleControllerListener});
            Object subtitleInstance = constructor.newInstance(context, null, null);
            Field f = cSubtitleController.getDeclaredField("mHandler");
            f.setAccessible(true);
            try {
                f.set(subtitleInstance, new Handler());
            } catch (IllegalAccessException e) {
                return mediaplayer;
            } finally {
                f.setAccessible(false);
            }
            Method setsubtitleanchor = mediaplayer.getClass().getMethod("setSubtitleAnchor",
                    cSubtitleController, iSubtitleControllerAnchor);
            setsubtitleanchor.invoke(mediaplayer, subtitleInstance, null);
        } catch (Exception e) {
            LogUtil.d("getMediaPlayer crash ,exception = "+e);
        }
        return mediaplayer;
    }

    
}
