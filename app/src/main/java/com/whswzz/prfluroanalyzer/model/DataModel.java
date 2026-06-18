package com.whswzz.prfluroanalyzer.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.consts.Consts;
import com.whswzz.prfluroanalyzer.consts.Instruct;
import com.whswzz.prfluroanalyzer.param.Params;
import com.zkzk.pra.R;
import com.zkzk.pra.utils.ToastUtil;

import android.media.ToneGenerator;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import top.jemen.model.ComM;
import top.jemen.model.ComM.IWatcher;
import top.jemen.utils.Arrays;
import top.jemen.utils.LogUtil;
import top.jemen.utils.Tone;
import top.jemen.utils.threadpool.AsyncProcessor;

public class DataModel{// implements IWatcher  改用回Eventbus了 
	private static DataModel model;
	private int timeout = 600000;
	private boolean working=true;
	public static final byte PR=0; 
	public static final byte A=1;//0x01;
//	public static final byte B=2;//0x0010
//	public static final byte AB=3;
	
	// private boolean twice=false,second=false;
	private DataModel() {
		// int fd=n.Open(3,115200);
		// Log.d("jemen","open="+fd);
		// n=new Native();
		EventBus.getDefault().register(this);
	}

	public static DataModel getInstance() {
		if (null == model) {
			synchronized (DataModel.class) {
				if(null==model) {
					model = new DataModel();
				}
			}
			
		}
		return model;
	}
	/**
	 * 停止采集吸光度数据
	 * AB  A通道或者B通道,0代表A，1代表B,3代表AB两个通道
	 */
	private void stop(int ab) {
		working=false;
		for(int i=0;i<2;i++) {
			if((ab>>i&1)==1) {
				cmd(1+i,0x50+i,0);
				SystemClock.sleep(300);
			}
		}
		release();
	}
	
	
	Map<Integer,byte[]> map=new HashMap<>();
	
	private Handler handler;
	
	
	public void detectData(final Handler handler,final int proj,final int ab) {
		detectData(handler, proj, ab,181000);
	}
	
	/**
	 * @param handler
	 * @param proj	可选00-05将使用不同的波长
	 * @param ab	通道号可选00和01，分别代表A通道和B通道。3代表两个通道
	 */
	public void detectData(final Handler handler,final int proj,final int ab,final int ms) {
		this.handler=handler;
		new Thread() {
			@Override
			public void run() {
				try {
					int t = 30; // 执行某个操作的次数
//					EventBus.getDefault().register(DataModel.this);
//					ComM.get().regist(DataModel.this);
//					map.clear();
//					LogUtil.d("握手");
//					t = 3;
//					while (t > 0) {
//						t--;
//						cmd( 0, 0, 0); //握手;// 握手
//						if (waitAck(0)) {
//							break;
//						}
//					}
//					if (t<0) {
//						LogUtil.d("串口握手交互出错");
//						handler.sendEmptyMessage(Consts.SERIAL_ERROR);
//					release();
//						return;
//					}
					LogUtil.d("设置参数");		//不同检测物用不同的波长和温度,proj代表波长目前用0,0C即温度为25+12=37度。
					
					for(int i=0;i<2;i++) {
						if((ab>>i&1)!=0) {
							t = 3;
							map.remove(0x52+i);
							while (t > 0) {
								t--;
								cmd( 1+i, 0x52+i,37); //设置温度,扩大10倍，原模块协议是Note：温度 0x01 为 25 度，0x02 为 26 度，以此类推，最高到 60 度
								if (waitAck(0x52+i)) {
									break;
								}
							}
							if (t<=0) { // 如果等待应答不成功
								LogUtil.d("串口设置参数交互出错");
								handler.sendEmptyMessage(Consts.SERIAL_ERROR);
								release();
								return;
							}
						}
					}
					
					working=true;
					Message msg = handler.obtainMessage();
					msg.what=Consts.DETECT_TIME;
					msg.arg1=ms/1000;
					msg.sendToTarget();
					for(int i=0;i<2;i++) {
						if((ab>>i&1)!=0) {
							cmd(1+i,0x50+i,proj<<8|0x01); //开始检测，1-8位标识开关，9-16位标识波长 ,该指令没有回应。
							SystemClock.sleep(200);
						}
					}
					for(int i=0;i<ms/500;i++){
						if(!working){
							return;
						}
						SystemClock.sleep(500);
					}
					handler.sendEmptyMessage(Consts.MSG_READ_END);
//					EventBus.getDefault().unregister(DataModel.this);
					DataModel.this.stop(ab);
					
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(Consts.SERIAL_ERROR);
				} finally {
				}

			}// run函数结束
		}.start();
	}
	
	public void startPhotometer(final Handler handler,final int proj,final int ab) {
		this.handler=handler;
		new Thread() {
			@Override
			public void run() {
				try {
					int t = 30; // 执行某个操作的次数
					for(int i=0;i<2;i++) {
						if((ab>>i&1)!=0) {
							cmd(1+i,0x50+i,proj<<8|0x01); //开始检测，1-8位标识开关，9-16位标识波长 ,该指令没有回应。
							SystemClock.sleep(200);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(Consts.SERIAL_ERROR);
				} 
			}// run函数结束
		}.start();
	}
	public void stopPhotometer(final int ab) {
		AsyncProcessor.executeTask(new Runnable() {
			@Override
			public void run() {
				stop(ab);
				release();
			}
		});
	}
	


	private void release() {
		handler=null;
	}
	
	
	/**
	 * 发送串口指令
	 * @param channel	通道号，可填0，1,2
	 * @param func		功能位
	 * @param data		数据位，占两个字节
	 * @return
	 */
	private int cmd( int channel, int func,int data) {
		int result=-1;
		try {
			byte[] buf = { (byte) 0Xa5, 0X5a, 0X00, 0X00, 0x02, 0X00, (byte) 0X00, 0X00 };
			buf[2] = (byte) channel;
			buf[3] = (byte) func;
			buf[5]=(byte) (data>>8);
			buf[6]=(byte) data;
					
			result = ComM.get().send( buf);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 检测串口的回应，检测成功则返回true，否则返回false
	 * 
	 * @return
	 */
	private boolean waitAck( int func) {
		try {
			for(int i=0;i<20;i++) {
				SystemClock.sleep(20);
				byte[] bs=map.remove(func);
				if(bs!=null) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	@Subscribe(threadMode=ThreadMode.PostThread)
	 public void recieveBytes(byte[] bs) {
		 map.put(bs[3]&0xff, bs);
		 LogUtil.d("datamodel receive:"+bs);
	 }
//	@Override
//	public void post(byte[] bs) {
//		LogUtil.d("datamodel receive:"+top.jemen.utils.Arrays.toHexString(bs));
//		Message msg;
//		// TODO Auto-generated method stub
//		switch(bs[3]) {
//			case 0x00: //握手信号
//			case 0x52://1-12通道温度设置
//			case 0x53://13-24通道温度设置
//			case 0x54:
//				map.put(bs[3]&0xff, bs);
//				break;
//			case 0x56:
//			case 0x57:
//				float[] fs=new float[12];
//				for(int i=0;i<fs.length;i++) {
//					int bits=(bs[5+i*4]&0xff)<<0|(bs[6+i*4]&0xff)<<8|(bs[7+i*4]&0xff)<<16|(bs[8+i*4]&0xff)<<24;
//					fs[i]=Float.intBitsToFloat(bits);
//				}
//				if(null==handler) {
//					return;
//				}
//				msg=handler.obtainMessage();
//				msg.what=Consts.ABSORBANCY;//吸光度
//				msg.arg1=bs[3]-0x56;
//
//				msg.obj=fs;
//				msg.sendToTarget();
//				break;
//
//		}
//	}


	public void lightOn(final int v) {
		AsyncProcessor.executeTask(new Runnable() {
			@Override
			public void run() {
				int t=3;
				map.remove(0x58);
				while (t > 0) {
					t--;
					cmd( 0,0x58,v); //设置温度
					if (waitAck(0x58)) {
						break;
					}
				}				
			}
		});
		
	}
	private Thread timer;
	public void setEnzymeTemp(final double temp) {
		if(null!=timer) {
			return;
		}
		timer=new Thread() {
			public void run() {
				int t=3;
				map.remove(0x54);
				while (t > 0) {
					t--;
					cmd( 3,0x54,(int) (temp*10)); //设置温度
					if (waitAck(0x54)) {
						break;
					}
				}
				while(Params.TEMP_ENZYME<temp) {
					EventBus.getDefault().post(Params.s);
					ComM.get().send(Instruct.TEMP_ENZYME); //会略微影响下定时的精度。
					Params.s--;
					SystemClock.sleep(999);
				}
				Tone.get().play(R.raw.temp38);
				timer=null;
			};
		};
		timer.start();
	}
	

	
	

	
	
}
