package top.jemen.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import org.xutils.common.Callback;
import org.xutils.common.task.PriorityExecutor;
import org.xutils.common.util.LogUtil;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.zkzk.pra.service.NetService;
import com.zkzk.pra.utils.CallBack;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.os.AsyncTask;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import org.xutils.x;
import org.xutils.http.RequestParams;



import top.jemen.interfaces.ICallback;

public class NetUtil {
	/**
	 * get the wifi signal strongth wifi强度，jemen
	 * 
	 * @param context
	 * @return
	 */
	public static void getSignalStrength(Context context, CallBack<String> cb) {
		WifiManager wifimng = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		int state = wifimng.getWifiState();
		if (WifiManager.WIFI_STATE_ENABLED != state)
			wifimng.setWifiEnabled(true);
		int rssi = wifimng.getConnectionInfo().getRssi();
		int ip = wifimng.getConnectionInfo().getIpAddress();
		int linkSpeed = wifimng.getConnectionInfo().getLinkSpeed();
		String mac = wifimng.getConnectionInfo().getMacAddress();
		String ssid = wifimng.getConnectionInfo().getSSID();
		int sigStrength = WifiManager.calculateSignalLevel(rssi, 101);
		cb.onSuccess("当前热点：" + ssid + "\n信号强度：" + sigStrength + "\nip=" + ip(ip) + "\nlinkSpeed=" + linkSpeed
				+ "Mbps\nmac:" + mac);
	}

	public static String ip(int i) {
		String s = (i & 0xff) + ".";
		s += (i << 16 >>> 24) + ".";
		s += (i >> 16 & 0xff) + ".";
		s += (i >>> 24);
		return s;

	}

	/**
	 * 获取LTE和wifi模式下的ip地址
	 */
	public static String getPhoneIp() {
		try {

			StringBuffer sb = new StringBuffer();

			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
						sb.append(inetAddress.getHostAddress().toString());
						// Log.d("jemen","sb="+sb);
						// sb.append("\n"+inetAddress.getHostName());
						// Log.d("jemen","sb="+sb);
					}

				}
			}
			LogUtil.d("ip:"+sb.toString());
			return sb.toString();
		} catch (Exception e) {
			// TODO: handle exception
		}

		return "";

	}

	public static void scanIp(final CallBack cb) {
		String localIp = getPhoneIp();
		Log.d("jemen", "localIp=" + localIp);
		final List<String> ips = new LinkedList<String>();
		String result = "";
		if (null == localIp || "" == localIp)
			return;
		String[] lip = localIp.split(".");
		final String top = localIp.substring(0, localIp.lastIndexOf(".") + 1);
		final Runtime runtime = Runtime.getRuntime();
		for (int i = 0; i <= 255; i++) {
			final String ip = top + i;
			new Thread() {
				public void run() {
					try {
						// windows系统不支持-c命令，linux支持，表示次数
						Process pro = runtime.exec("ping -c 2 -w 7 " + ip);
						int result = pro.waitFor();
						Log.d("jemen", ip + "result=" + result);
						if (result == 0) {
							ips.add(ip);
							Log.d("jemen", "add-ip=" + ip);
							cb.onSuccess(ips);
						}

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				};
			}.start();
		}
	}

	/**
	 * 获取wifi列表，用于刚打开wifi时候以一定的延迟来获取。
	 * 
	 * @param context
	 * @param cb
	 */
	public static void scanWifi(final Context context, final CallBack<List<ScanResult>> cb) {
		new AsyncTask<Integer, Integer, List<ScanResult>>() {
			@Override
			protected List<ScanResult> doInBackground(Integer... params) {
				List<ScanResult> list = null;
				try {
					Thread.sleep(1000);
					WifiManager wifimng = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
					list = wifimng.getScanResults();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return list;
			}

			protected void onPostExecute(List<ScanResult> list) {
				if (null == list) {
					cb.onFailed("获取wif列表失败");
				} else {
					cb.onSuccess(list);
				}
			};
		}.execute(0);
	}

	/**
	 * ��ûд��
	 * 
	 * @param context
	 * @param cb
	 */
	public static void p2p(Context context, final CallBack<String> cb) {
		WifiP2pManager p2p = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
		ActionListener listener = new ActionListener() {
			@Override
			public void onSuccess() {
				cb.onSuccess("");
			}

			@Override
			public void onFailure(int reason) {

			}
		};

		Looper srcLooper = Looper.getMainLooper();
		ChannelListener cl = new ChannelListener() {
			@Override
			public void onChannelDisconnected() {

			}
		};
		Channel c = p2p.initialize(context, srcLooper, cl);
		p2p.discoverPeers(c, listener);
	}

	/**
	 * @param strUrl
	 *            :不用了，连接到上面url中
	 * @return 返回结果
	 */
	public static String request(String strUrl) {
		BufferedReader reader = null;
		String result = null;
		StringBuffer sbf = new StringBuffer();

		try {
			URL url = new URL(strUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			// 填入apikey到HTTP header
			connection.setRequestProperty("apikey", "ba187b8dc33e77ec66a7089b007d0112");
			connection.connect();
			InputStream is = connection.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String strRead = null;
			while ((strRead = reader.readLine()) != null) {
				sbf.append(strRead);
				sbf.append("\r\n");
			}
			reader.close();
			result = sbf.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private static long lastT=0;
	private static boolean onLine=false;
	public static boolean isConnected() {
		return isConnected(MyApp.getApp());
	}
	public static boolean isConnected(Context context) {
		if(NetService.isConnected()) {
			return true;
		}
		
		long t=System.currentTimeMillis();
		if(t-lastT<10000) {
			lastT=t;
			return onLine;
		}
		onLine=false;
		if(null==context)
			context=MyApp.getApp();
		ConnectivityManager con = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
		boolean internet = con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
		if (internet) {
			// 执行相关操作
//			ToastUtil.showText("当前移动网络已连接！", Toast.LENGTH_LONG);
			onLine=true;
		} else if (wifi) {
//			ToastUtil.showText("当前WIFI已连接", Toast.LENGTH_LONG);
			onLine=true;
		} else {
//			ToastUtil.showText("网络尚未连接", Toast.LENGTH_LONG);
		}
		return onLine;
	}

	public static void downloadFile(String url,String filePath, final ICallback callback) {
		RequestParams params = new RequestParams(url);
		params.setSaveFilePath(filePath);
		params.setAutoResume(true);//断点续传
		params.setExecutor(new PriorityExecutor(2,false));//自定义线程池,有效的值范围[1, 3], 设置为3时, 可能阻塞图片加载.
		x.http().get(params, new Callback.CommonCallback<File>() {

			@Override
			public void onCancelled(CancelledException arg0) {
				callback.onFailed("cancelled,"+arg0.getMessage());
			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				callback.onFailed("cancelled,"+arg0.getMessage());
			}

			@Override
			public void onFinished() {
			}

			@Override
			public void onSuccess(File arg0) {
				callback.onSuccess(arg0);
			}


		});

	}
}
