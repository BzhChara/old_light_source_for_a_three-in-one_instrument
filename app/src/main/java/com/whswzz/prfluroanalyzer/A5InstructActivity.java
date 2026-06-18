package com.whswzz.prfluroanalyzer;


import com.whswzz.prfluroanalyzer.base.BaseActivity;
import com.whswzz.prfluroanalyzer.service.BatteryService2;
import com.zkzk.pra.R;
import com.zkzk.pra.utils.ToastUtil;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import top.jemen.model.ComM;
import top.jemen.utils.Arrays;
import top.jemen.utils.LogUtil;

public class A5InstructActivity extends BaseActivity implements OnClickListener {
	private TextView tvMsg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		initAndSet();
		LogUtil.d("TestActivity onCreate over");
		
//		ComM.get().regist(this);
		EventBus.getDefault().register(this);
		
		BatteryService2.skip(100);
		
		
	}
	@Override
	protected void onDestroy() {
//		ComM.get().unRegist(this);
		EventBus.getDefault().unregister(this);
		
		super.onDestroy();
	}
	
	
	
	
	private LinearLayout root;
	private void initAndSet() {
		root=(LinearLayout) findViewById(R.id.root);
		tvMsg=(TextView) findViewById(R.id.tv_msg);
		tvMsg.setMovementMethod( ScrollingMovementMethod.getInstance());
				
		tvMsg.setOnClickListener(this);
	
		addItem("握手", 0, 0, 2, 0);
		addItem("电池电量采集", 0x00, 0x06, 2, 0);
		addItem("板内温度采集", 0x00, 0x07, 2, 0);
		addItem("板外温度采集", 0, 0x08, 2, 0);
		addItem("板子版本号", 0, 0x0b, 2, 0);
		addItem("1-12通道开启", 1, 0x50, 2,1 );
		addItem("1-12通道关闭", 1, 0x50, 2,0 );
		addItem("12-24通道开启", 2, 0x51, 2,1 );
		addItem("12-24通道关闭", 2, 0x51, 2,0 );
		
		addItem("1-12通道温度设置", 1, 0x52, 2, 37);
		addItem("12-24通道温度设置", 2, 0x53, 2, 37);
		
		addItem("设置酶片加热温度", 3, 0x54, 2, 37);
		addItem("读取酶片加热温度", 3, 0x55, 2, 0);
		
		addItem("采集1-12通道吸光度", 1, 0x56, 2, 0);
		addItem("采集12-24通道吸光度", 1, 0x567, 2, 0);
		
		addItem("胶体金光源开", 0,0x58, 2, 1);
		addItem("胶体金光源关", 0,0x58, 2,0);

		addItem("1-12通道温度读取", 0,0x59, 0,0);
		addItem("霉片合盖查询", 0,0x5A, 0,0);
		addItem("13-24通道温度读取", 0,0x5b, 0,0);
		
		setAllListener(findViewById(R.id.root));
		
	}
	
	
		
		
	
	//ComM.get().send(new byte[] {(byte) 0xAA ,00, 01, 0x23, (byte) index,(byte) 255,(byte) 255, (byte) 0xCC, 0x33, (byte) 0xC3, 0x3C});
	private void addItem(String title,int channel,int command,int len,int...params) {
		LinearLayout ll=new LinearLayout(this);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		ll.setLayoutParams(lp);
		
		
		TextView tv=new TextView(this);
		tv.setText(title);
		ll.addView(tv);
		
		EditText etIndex=new EditText(this);
		etIndex.setText(Integer.toHexString(channel));
		ll.addView(etIndex);
		
		EditText etCommand=new EditText(this);
		etCommand.setText(Integer.toHexString(command));
		ll.addView(etCommand);
		
		EditText etLen=new EditText(this);
		etLen.setText(Integer.toHexString(len));
		ll.addView(etLen);
		
		LayoutParams lpet=new LayoutParams(0, LayoutParams.MATCH_PARENT);
		lpet.weight=1;
		lpet.gravity=Gravity.CENTER;
		for(int i=0;null!=params&&i<params.length;i++) {
			EditText et=new EditText(this);
			et.setText(""+params[i]);
			et.setLayoutParams(lpet);
			et.setTextSize(23);
			et.setLayoutParams(lpet);
			ll.addView(et);
		}
		Button bt=new Button(this);
		bt.setText("发送");
//		bt.setOnClickListener(this);
		ll.addView(bt);;
	
		root.addView(ll);
	}
	
	
	

	/**
	 * 不带中括号
	 * @param bs
	 * @return
	 */
	public static String toHexText(byte[] bs) {
        if (bs.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(bs.length * 6);
        String b=Integer.toHexString(bs[0]);
        if(b.length()>2) {
        	b=b.substring(b.length()-2);
        }
        sb.append(b);
        for (int i = 1; i < bs.length; i++) {
            sb.append(" ");
            b=Integer.toHexString(bs[i]);
            if(b.length()>2) {
            	b=b.substring(b.length()-2);
            }
            sb.append(b);
        }
        return sb.toString();
	}


	
	
	protected void setAllListener(View v){
        if (null == v) {
            return;
        }
        if(v instanceof ViewGroup){
            ViewGroup vg= (ViewGroup) v;
            for(int i=0;i<vg.getChildCount();i++  ) {
                setAllListener(vg.getChildAt(i));
            }
            return;
        }else if(v instanceof Button){
            v.setOnClickListener(this);
        }
        if(v instanceof TextView) {
        	((TextView) v).setTextSize(22);
        }

    }
	
	
	private long lastT=0;
	@Override
	public void onClick(View v) {
		BatteryService2.skip(100);
		if(v.getId()==R.id.tv_msg) {
			long t=System.currentTimeMillis();
			if(t-lastT<400) {
				tvMsg.setText("");
			}
			lastT=t;
			return;
		}
		
		byte[] data=getEtValue(v);
		if(null!=data) {
			LogUtil.d(Arrays.toHexString(data));
			ComM.get().send(data);
			showMsg("send:"+Arrays.toHexString(data)+"\n");
		}
		
	}
	

	
	 private byte[] getEtValue(View view) {
	        ViewParent p = view.getParent();
	        LogUtil.d("viewparent=" + p);
	        if (!(p instanceof ViewGroup)) {
	            LogUtil.d("不是 ViewGroup");
	            return null;
	        }
	        ViewGroup vg = (ViewGroup) p;
	        
	        byte[] bs=new byte[6+(vg.getChildCount()-5)*2]; //别忘了button也是一个child
	        
			bs[0]=(byte) 0xa5;
			bs[1]=0x5a;
	        
	        for (int i = 1; i<=3&&i < vg.getChildCount(); i++) {
	            if (vg.getChildAt(i) instanceof EditText) {
	                String s = ((EditText) vg.getChildAt(i)).getText().toString();
	                if(null==s){
	                	ToastUtil.showText("请输入待发送数据", Toast.LENGTH_SHORT);
	                    return null;
	                }
	                try {
						bs[2+i-1]=(byte) Integer.parseInt(s,16); //bs[2]通道号开始
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
	            }
	        }
	        
	        for (int i = 4; i < vg.getChildCount(); i++) {
	            if (vg.getChildAt(i) instanceof EditText) {
	                String s = ((EditText) vg.getChildAt(i)).getText().toString();
	                if(null==s){
	                	ToastUtil.showText("请输入待发送数据", Toast.LENGTH_SHORT);
	                    return null;
	                }
	                int v=Integer.parseInt(s);
	                bs[5+(i-4)*2]=(byte) (v>>8&0xff);
	                bs[5+(i-4)*2+1]=(byte) (v&0xff);
	            }
	        }
	        int sum =bs[0];
	        for(int i=1;i<bs.length-1;i++) {
	        	sum+=bs[i];
	        }
	        bs[bs.length-1]=(byte) sum;
	        
	        
	        return bs;
	    }

	
	
	
	
	
	
	@Subscribe(threadMode = ThreadMode.MainThread)
	public void post(byte[] bs) {
		if(bs==null||bs.length<5) {
			return;
		}
		
		showMsg("receive:"+Arrays.toHexString(bs)+"\n"); //收发的头部是一样的。
		
	}
	
	
	private void showMsg(final String msg) {
		
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(tvMsg.getLineCount()>150) {
					String text=tvMsg.getText().toString();
					int index=0;
					for(int i=0;i<150;i++) {
						index=text.lastIndexOf("\n", index);
					}
					if(index>=0) {
						tvMsg.setText(text.substring(index)+"\n"+msg);
						return;
					}
				}
				tvMsg.append(msg);
				int offset = tvMsg.getLineCount() * tvMsg.getLineHeight();
		        if (offset > tvMsg.getHeight()) {
		        	tvMsg.scrollTo( 0, offset - tvMsg.getHeight() );
		        }
			}
		});
		
	}
	
	
}
