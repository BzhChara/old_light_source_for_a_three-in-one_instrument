package top.jemen.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import com.whswzz.prfluroanalyzer.model.Protocol;

//import android.util.ArraySet;
import de.greenrobot.event.EventBus;
import top.jemen.serial.Serial;
import top.jemen.utils.Arrays;
import top.jemen.utils.LogUtil;

public class ComM {
	private static ComM model;
	private boolean alive=true;
	private Serial serial = new Serial();
	ArrayList<Byte>[] ques;
	LinkedBlockingQueue<byte[]>[] blockQues;//=new LinkedBlockingQueue<>();
	private static final int SendT=10000;//间隔时间   小于3接收数据会出问题。
	private int comN=4;
	private static int[] fds;//= {-1,-1};//用几个串口就改几个吧.
//	private List<IWatcher> watchers=new LinkedList<IWatcher>();
	private Set<IWatcher> watchers;
	
	static {
		System.loadLibrary("JemenUVC");
	}
	
	public static interface IWatcher{
		void post(byte[] bs);
	}
	
	public void regist(IWatcher watcher	) {
		watchers.add(watcher);
	}
	public void unRegist(IWatcher watcher) {
		watchers.remove(watcher);
		LogUtil.d("解除注册："+watcher.getClass());
	}
	
	@SuppressWarnings("unchecked")
	private ComM() {
//		LogUtil.d("ComM 构造函数调用");
		ques=new ArrayList[comN];
		blockQues=new LinkedBlockingQueue[comN];
		fds=new int[comN];
		for(int i=0;i<comN;i++) {
			ques[i]=new ArrayList<Byte>();
			blockQues[i]=new LinkedBlockingQueue<>();
			fds[i]=-1;
		}
		watchers=new HashSet<>();
	}
	public static ComM get() {
		if(null==model) {
			synchronized (ComM.class) {
				if(null==model) {
					model=new ComM();
				}
			}
		}
		return model;
	}
	
	
	long lastT=System.nanoTime();
	public  int send( int comi,byte[] data) {
		if(null==data||data.length<1)	return -1;
		
		int fd=fds[comi];
		int result;
		if(comi==3) {
			Protocol.sum(data);
		}
		synchronized (ComM.class) {
			long t=System.nanoTime();
			if(t-lastT<SendT) {
				try {
					Thread.sleep((SendT-(t-lastT))/1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			result = serial.Write(fd, data, data.length);
			if(data.length<3||(data[3]!=6&&data[3]!=0X23&&data[3]!=8&&data[3]!=7)) {//
				LogUtil.d(comi+"send:"+Arrays.toHexString(data));
			}
			
			lastT=System.nanoTime();
		}
		
		return result;
	}
	
	
	/**
	 * 仅最后一个字节有效
	 * @param data
	 * @return
	 */
	public  int send(int[] data) {
		if(data==null||data.length<1)
			return -1;
		byte[] bs=new byte[data.length];
		for(int i=0;i<bs.length;i++) {
			bs[i]=(byte) data[i];
		}
		return send(bs);
	}
	
	public int send(byte[] bs) {
		return send(3,bs);
	}
	
	
	public void start() {
		for(int i=2;i<fds.length;i++) {
			if(fds[i]>=0) {
				continue;
			}
			alive=true;
			int t = 8;
			while ((fds[i] = serial.openBlock(i, 115200)) < 0 && t > 0) {//
				t--;
			}
			LogUtil.e("fds["+i+"]="+fds[i]+",t="+t);
			if(fds[i]<0) {
				continue;
			}
			new Thread(new Reader(i)).start();
			new TakeThread(i).start();
		}
		
		
	}
	
	
	public void close() {
		alive=false;
		for(int i=0;i<fds.length;i++) {
			if(fds[i]>0) {
				serial.Close(fds[i]);
			}
			fds[i]=-1;
		}
		
		
		
	}
	
	
	class Reader implements Runnable {
		int comi;
		
		public Reader(int comi) {
			this.comi=comi;
		}

		@Override
		public void run() {
			Thread.currentThread().setName("com read thread"+comi);
//			LogUtil.d("com read thread");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			if(fds[comi]<0) {
				while(alive&&(fds[comi] = serial.openBlock(comi, 115200)) < 0 ) {
					try {
						LogUtil.e("fd="+fds[comi]);
						Thread.sleep(300);
					} catch (InterruptedException e) {
					}
				}
			}
			
			while(alive) {
				byte[] bs=serial.Read(fds[comi]);
				if(null==bs) {
					LogUtil.e("bs=null");	//非阻塞状态会导致出问题.
					try {
						Thread.sleep(10);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					continue;
				}
				
				if(bs.length>3&&bs[3]!=6&&bs[3]!=8&&bs[3]!=7&&bs[3]!=0X23) {
					LogUtil.d("read:"+Arrays.toHexString(bs));
				}
				blockQues[comi].offer(bs);
			}
		}
	};
	
	
	
	
	private class TakeThread extends Thread	{
		int comi;
		
		public TakeThread(int comi) {
			this.comi = comi;
		}
		@Override
		public void run() {
			setName("COM tackThread");
			while(alive) {
				byte[] bs = null;
				try {
					bs = blockQues[comi].take();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(bs!=null) {
//					LogUtil.d("take:"+Arrays.toHexString(bs));
					if(!parseBS(comi,bs)) {
						parseQue(comi);
					}
				}
			}
		}
	}
	
	
	/**暂未启用。
	 * 单帧的完整数据直接解析不再依赖buffer
	 * @param bs
	 * @return
	 */
	protected boolean parseBS(int comi,byte[] bs) {
		if(null==bs) {
			return false;
		}
		/********************单帧解析*****************************/
//		if(bs.length>6&&bs[0]=)
		ArrayList<Byte> que=ques[comi];
		for(byte b:bs) {
			que.add(b);
		}
		return false;
	}
	
	
	/**
	 * 用que来解析
	 */
	protected void parseQue(int comi) {
		ArrayList<Byte> que=ques[comi];
		if(que.size()<7) {
			return;
		}
		int flag=0;
		for(int i=0;i+6<que.size();i++) {
			if(que.get(i)==(byte)0x5a&&que.get(i+1)==(byte)0xa5) {
				int len=que.get(i+4)&0xff;
				if(len<1||len>100) { //解析到的长度出错了,抛弃前面的。
					i+=4;
					continue;
				}
				
				if(que.size()<i+6+len) { //单条协议没有接收完整。
					break;
				}
//				LogUtil.d("解析到协议包，len="+len+",当前watcher数量："+watchers.size()
//				+",watchers:"+watchers.hashCode()+",ComM:"+this.hashCode());
				byte[] bs=new byte[6+len];
				for(int j=0;j<bs.length;j++) {
					bs[j]=que.get(i+j);
				}
				flag=i+bs.length;
				i+=bs.length;
				Protocol.parseBs(bs); //EvetnBus传递消息不靠谱
//				for(IWatcher watcher:watchers) {
//					watcher.post(bs);
//				}
				
				
				
			}//没有找到帧头的i+1
		}//the end of for circle
		for(int k=0;k<flag;k++) {
			que.remove(0);
		}
//		if(que.size()>0) {
//			LogUtil.e("que remain:"+que);
//			if(que.size()>1024) {
//				que.clear();
//			}
//		}
		
	}
	
	
	
	
	
}
