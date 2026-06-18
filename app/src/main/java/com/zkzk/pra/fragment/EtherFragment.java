//package com.zkzk.pra.fragment;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.net.HttpURLConnection;
//import java.net.Socket;
//import java.net.URL;
//import java.text.RuleBasedCollator;
//
//import com.zkzk.pra.R;
//import com.zkzk.pra.eth.EtherActivity;
//import com.zkzk.pra.ui.IPEditText;
//import com.zkzk.pra.utils.ExceptionHandler;
//import com.zkzk.pra.utils.ToastUtil;
//
//import android.R.string;
//import android.app.Fragment;
//import android.content.Context;
//import android.content.Intent;
//import android.net.ConnectivityManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.RadioGroup;
//import android.widget.RadioGroup.OnCheckedChangeListener;
//import top.jemen.utils.LogUtil;
//import android.widget.Toast;
///**在三星Exynos 4412平台可用，已移植busybox
// * 这个已经废弃了，代替的事EhternetFragment
// * @author Administrator
// *@deprecated
// */
//public class EtherFragment extends Fragment implements OnCheckedChangeListener {
//	private View root;
//	private IPEditText ipetIP, ipetMask, ipetGatway, ipetDNSfirst, ipetDNSStandby;
//	private RadioGroup rgIp, rgDNS;
//	private Button btOk;
//	private Handler handler=new Handler() {
//		public void handleMessage(android.os.Message msg) {
//			ToastUtil.showText((String)msg.obj, Toast.LENGTH_SHORT);
//			switch(msg.what) {
//			case  0://IP设置完成
//				String gateway = ipetGatway.getText();
//				setGateway(gateway);
//
//				break;
//			case 1://网关设置
//				String dns=ipetDNSfirst.getText();
//				setDns(dns);
//				break;
//			}
//		};
//	};
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		root = inflater.inflate(R.layout.fragment_eth, null);
//		init();
//		setListeners();
//		setDefault();
//		return root;
//	}
//
//	private void init() {
//		ipetIP = (IPEditText) root.findViewById(R.id.ipet_ip);
//
//		ipetMask = (IPEditText) root.findViewById(R.id.ipet_mask);
//
//		ipetGatway = (IPEditText) root.findViewById(R.id.ipet_gatway);
//
//		ipetDNSfirst = (IPEditText) root.findViewById(R.id.ipet_dns_first);
//
//		ipetDNSStandby = (IPEditText) root.findViewById(R.id.ipet_dns_second);
//		rgIp = (RadioGroup) root.findViewById(R.id.rg_ip);
//		rgDNS = (RadioGroup) root.findViewById(R.id.rg_dns);
//
//		btOk=(Button) root.findViewById(R.id.bt_ok);
//
////		View tv = root.findViewById(R.id.tv_eth_set);
////		tv.setOnClickListener(new OnClickListener() {//测试用，已废弃。
////			@Override
////			public void onClick(View arg0) {
////				Intent i=new Intent(getActivity(), EtherActivity.class);
////				startActivity(i);
////			}
////		});
//	}
//
//	private void setDefault() {
//		ipetIP.setText("192.168.1.130");
//		ipetMask.setText("255.255.255.0");
//		ipetGatway.setText("192.168.1.0");
//		ipetDNSfirst.setText("192.168.1.1");
//	}
//
//	private void setListeners() {
//		rgIp.setOnCheckedChangeListener(this);
//		rgDNS.setOnCheckedChangeListener(this);
//		btOk.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				onOkClicked();
//			}
//		});
//	}
//
//	public void onOkClicked() {
//		String ip = ipetIP.getText();
//		String mask=ipetMask.getText();
////		String gateway = ipetGatway.getText();
////		String dns=ipetDNSfirst.getText();
//		setIp(ip,mask);
////		setGateway(gateway);
////		setDns(dns);
//	}
//
//	private void setGateway(final String gateway) {
//		new Thread() {
//			public void run() {
//				try {
//					final String com = "route add default gw "+gateway+" dev eth0";
//					Process pro = Runtime.getRuntime().exec(com);	//在4.4系统中不需要su获取root权限。4.0无法实现。
//					int result = pro.waitFor();
//					Log.d("jemen", "setGateway result=" + result);
//					Message msg;
//					if(0==result) {
//						ToastUtil.showText("设置IP地址成功", Toast.LENGTH_SHORT);
//						msg=Message.obtain(handler, 1, "设置以太网默认网关地址成功");
//					}else {
//						msg=Message.obtain(handler, 1, "设置以太网默认网关地址失败");
//					}
//					msg.sendToTarget();
//				} catch (Exception e) {
//					ExceptionHandler.handleException(e);
//				}
//			};
//		}.start();
//	}
//
//
//	private void setDns(final String dns) {
//		new Thread() {
//			public void run() {
//				try {
//					final String com = "setprop net.dns1 "+dns;
//					Process pro = Runtime.getRuntime().exec(com);
//					int result = pro.waitFor();
//					Log.d("jemen", "setDns result=" + result);
//					Message msg;
//					if(0==result) {
//						msg=Message.obtain(handler, 2, "设置以太网DNS成功");
//					}else {
//						msg=Message.obtain(handler, 2, "设置以太网DNS失败");
//					}
//					msg.sendToTarget();
//				} catch (Exception e) {
//					ExceptionHandler.handleException(e);
//				}
//
//			};
//		}.start();
//	}
//
//
//	public static final String IP_ADDR = "localhost";// 服务器地址
//
//	private boolean setIp(final String ip,String mask) {
//		final String com = "ifconfig eth0 " + ip + " netmask "+mask+" up\n";
//		new Thread() {
//			public void run() {
//				try {
////					setIpRoot("192.168.0.254");	//在4.4版本中，没必要使用root权限，可以直接调用。
//					Process pro = Runtime.getRuntime().exec(com);
//					// sleep(500);
////					Process pro = Runtime.getRuntime().exec("netcfg eth0 dhcp");
//					int result = pro.waitFor();//waitFor会阻塞进程。
//					Log.d("jemen", "setIp result=" + result);
//					Message msg;
//					if(0==result) {
//						msg=Message.obtain(handler, 0, "设置以太网IP地址成功");
//					}else {
//						msg=Message.obtain(handler, 0, "设置以太网IP地址失败");
//					}
//					msg.sendToTarget();
//
////					Socket socket = new Socket(IP_ADDR, 8887);		//基于4.4系统不再需要进程间通信了。
////					System.out.println("创建：" + (null != socket));
////					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
////					bw.write("busybox ifconfig eth0 192.168.0.254\0\n");	//android4.4系统下不需要busybox
////					bw.flush();
////					BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
////					line = null;
////					while((line=br.readLine())!=null) {
////						 line=br.readLine();
////						 System.out.println(line);
////					}
////					bw.write("exit\n");
////					bw.close();
////					br.close();
//
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			};
//		}.start();
//
//		return true;
//	}
//
//	/**
//	 * 	经在android4.4品台测试，不需要root权限即可完成设置。
//	 * @param ip
//	 */
//	private void setIpRoot(String ip) {
//		String com = "busybox ifconfig eth0 " + ip;
//		DataOutputStream os = null;
//		try {
//			Process suProcess = Runtime.getRuntime().exec("su");
//			os = new DataOutputStream(suProcess.getOutputStream());
//			// Execute commands that require root access
//			os.writeBytes(com + "up\n");
//			os.flush();
//			os.writeBytes("exit\n");
//			os.flush();
//
//			LogUtil.d("执行su后result="+suProcess.waitFor());
//			BufferedReader bf = new BufferedReader(new InputStreamReader(suProcess.getInputStream(), "GBK"));
//			String line;
//			StringBuilder sb = new StringBuilder();
//			while ((line = bf.readLine()) != null) {
//				sb.append(line).append("\n");
//			}
//			bf.close();
//			LogUtil.d(sb.toString());
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally {
//			try {
//				os.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	@Override
//	public void onCheckedChanged(RadioGroup group, int checkedId) {
//		switch (checkedId) {
//		case R.id.rb_ip_auto:
//			ipetIP.setEnable(false);
//			ipetMask.setEnable(false);
//			ipetGatway.setEnable(false);
//			break;
//		case R.id.rb_ip_follow:
//			ipetIP.setEnable(true);
//			ipetMask.setEnable(true);
//			ipetGatway.setEnable(true);
//			break;
//		case R.id.rb_dns_auto:
//			ipetDNSfirst.setEnable(false);
//			ipetDNSStandby.setEnable(false);
//			break;
//		case R.id.rb_dns_follow:
//			ipetDNSfirst.setEnable(true);
//			ipetDNSfirst.setEnable(true);
//			break;
//		}
//
//	}
//}
