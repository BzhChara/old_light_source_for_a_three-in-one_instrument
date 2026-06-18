package com.whswzz.prfluroanalyzer.settings.fragment;

import java.util.LinkedList;
import java.util.List;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.consts.Consts;
import com.zkzk.pra.R;
import com.zkzk.pra.adapter.WifiAdapter;
import com.zkzk.pra.ui.PswDialog;
import com.zkzk.pra.ui.PswDialog.OnCustomDialogListener;
import com.zkzk.pra.utils.ToastUtil;
import com.zkzk.pra.utils.WifiAdmin;
import com.zkzk.pra.utils.WifiAdmin.WifiCipherType;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import top.jemen.utils.LogUtil;
import android.widget.Switch;
import android.widget.Toast;

public class WifiFragment extends Fragment {
	private View root;
	private Switch switchWifi;
	private ListView lvWifi;
	private List<ScanResult> wifiHosts;
	// private WifiManager wifiMng;
	private BaseAdapter adapter;
	private WifiAdmin wiFiAdmin;
	public static final String TAG = "jemen";


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			root = inflater.inflate(R.layout.fragment_wifi, null);
			
		init();
		setAdapters();
		setListeners();
		return root;
	}

//	@Override
//	public void onStart() {
//		super.onStart();
//		if (wiFiAdmin.isWifiConnect()) {
//			Toast.makeText(MyApplication.getApp(), "wifi已连接", Toast.LENGTH_SHORT).show();
//		}
//	}

	private void init() {
		switchWifi = (Switch) root.findViewById(R.id.switch_wifi);
		lvWifi = (ListView) root.findViewById(R.id.lv_set_wifi);
		wiFiAdmin = new WifiAdmin(this.getActivity());
		// wifiMng = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
		// wifiMng.getConfiguredNetworks();
		registerBroadcast();
	}

	private void setAdapters() {
		wifiHosts = new LinkedList<ScanResult>();
		adapter = new WifiAdapter(getActivity(), wifiHosts);
		lvWifi.setAdapter(adapter);
		if (wiFiAdmin.isWifiEnabled()) {
			switchWifi.setChecked(true);
			List<ScanResult> results = wiFiAdmin.getWifiList();// wifiMng.getScanResults();
			wifiHosts.addAll(results);
		} else {
			switchWifi.setChecked(false);
		}
//		switchWifi.setEnabled(false);//有的板子没有wifi功能则不让该开关工作。
	}

	private void setListeners() {
		switchWifi.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
//					new MyDialog(getActivity(), null, "提示", "当前设备型号不具备wifi功能").show();
//					switchWifi.setChecked(false);
					wiFiAdmin.openWifi();
					wiFiAdmin.startScan();
				} else {
					// wifiMng.setWifiEnabled(false);
					wiFiAdmin.closeWifi();
					wifiHosts.clear();
					adapter.notifyDataSetChanged();
				}
			}
		});
		registWifiItemListener();
	}

	/**
	 * 注册广播
	 */
	private void registerBroadcast() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		getActivity().registerReceiver(mReceiver, filter);
	}

	/**
	 * 广播接收，监听网络
	 */
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		private boolean isDisConnected;
		private boolean isConnecting;

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			// wifi已成功扫描到可用wifi。
			if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
					|| action.equals(WifiManager.RSSI_CHANGED_ACTION)) {
				// Log.d(Consts.TAG, "接收到SCAN_RESULTS_AVAILABLE_ACTION||RSSI_CHANGED_ACTION");
				List<ScanResult> mScanResults = wiFiAdmin.getWifiList();// wifiMng.getScanResults();
				wifiHosts.clear();
				wifiHosts.addAll(mScanResults);
				adapter.notifyDataSetChanged();
				if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) && wiFiAdmin.isWifiConnect()) {
//					Toast.makeText(getActivity(), "wifi已连接", Toast.LENGTH_SHORT).show();
					ToastUtil.showText("wifi已连接", Toast.LENGTH_SHORT);
				}
				// Log.d(Consts.TAG, "mScanResults.size()===" + mScanResults.size());
			} else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
				int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
				switch (wifiState) {
				case WifiManager.WIFI_STATE_ENABLED:
					// wifiMng.startScan(); //<<<这里
					wiFiAdmin.startScan();
					switchWifi.setChecked(true);
					String sMac=MyApp.getApp().getPref().getString(Consts.KEY_ID, null);
					if(null==sMac) {
						sMac = wiFiAdmin.getMacAddress();
						if(null!=sMac) {
							sMac = sMac.replace(":", "");
							MyApp.getApp().getPref().edit().putString(Consts.KEY_ID, sMac).commit();
						}
					}
					break;
				case WifiManager.WIFI_STATE_DISABLED:
					wifiHosts.clear();
					adapter.notifyDataSetChanged();
					if (switchWifi.isChecked()) {
						switchWifi.setChecked(false);
					}
					break;
				case WifiManager.WIFI_STATE_ENABLING:
					Log.d(TAG, "wifi正在启用");
//					Toast.makeText(getActivity(), "wifi正在启用", Toast.LENGTH_SHORT).show();
					ToastUtil.showText("wifi正在启用", Toast.LENGTH_SHORT);
					break;
				case WifiManager.WIFI_STATE_DISABLING:
					ToastUtil.showText("wifi正在关闭", Toast.LENGTH_SHORT);
//					Toast.makeText(getActivity(), "wifi正在关闭", Toast.LENGTH_SHORT).show();
					break;
				}

			}
			// ********************下面引用一段在好例子网上找到的比较好的处理密码验证错误的代码?
			if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {// wifi连接上与否
				Log.d(TAG, "网络已经改变");
				NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
					if (!isDisConnected) {
						Log.d(TAG, "wifi已经断开");
						isDisConnected = true;

					}
				} else if (info.getState().equals(NetworkInfo.State.CONNECTING)) {
					if (!isConnecting) {
						Log.d(TAG, "正在连接...");
						isConnecting = true;
					}
				} else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
					WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
					WifiInfo wifiInfo = wifiManager.getConnectionInfo();
					Log.d(TAG, "连接到网络：" + wifiInfo.getBSSID());
				}

			} else if (action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
				int error = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 0);
				switch (error) {
				case WifiManager.ERROR_AUTHENTICATING:
					Log.d("jemen", "密码认证错误Code为：" + error);
//					Toast.makeText(getActivity(), "wifi密码认证错误！", Toast.LENGTH_SHORT).show();
					ToastUtil.showText("wifi密码认证错误！", Toast.LENGTH_SHORT);
					break;

				default:
					break;
				}

			}
		}
	};

	private void registWifiItemListener() {
		wiFiAdmin.getConfiguration();
		lvWifi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			String wifiItemSSID = null;
			public void onItemClick(android.widget.AdapterView<?> parent, android.view.View view, int position,
					long id) {
				// Log.d(Consts.TAG, "BSSID:" + wifiHosts.get(position).BSSID);
				final ScanResult result = wifiHosts.get(position);
				wifiItemSSID =result.SSID;
//				final int wifiItemId = wiFiAdmin.IsConfiguration("\"" + wifiHosts.get(position).SSID + "\"");
				final WifiConfiguration wifiConfiguration = wiFiAdmin.IsExsits(wifiItemSSID);
				if (null!=wifiConfiguration) {
					// 连接已保存密码的WiFi
					if (wiFiAdmin.isWifiConnect()) {
						// Toast.makeText(MyApplication.getApp(), "正在断开……", Toast.LENGTH_SHORT).show();
						AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_LIGHT);
						builder.setTitle("提示").setMessage("当前网络：" + wifiItemSSID)
						.setPositiveButton("断开",
								new OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										wiFiAdmin.disConnectionWifi(wifiConfiguration.networkId);
									}
								});
						builder.setNegativeButton("取消", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						});
						builder.setNeutralButton("忘记", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								
								wiFiAdmin.disConnectionWifi(wifiConfiguration.networkId);
								wiFiAdmin.rmConfiguration(wifiConfiguration.networkId);
							}
						});
						builder.create().show();
					} else {
						// Toast.makeText(getActivity(), "正在连接……", Toast.LENGTH_SHORT).show();
						AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_LIGHT);
						builder.setTitle("提示").setMessage("当前网络：" + wifiItemSSID)
						.setPositiveButton("连接",
								new OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										if (wiFiAdmin.Connect(wifiConfiguration)) {
											Log.d("jemen", "wifi connect return true");
										} else {
											Log.d("jemen", "wifi connect return false");
										}
									}
								});

						builder.setNegativeButton("取消", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						});
						builder.setNeutralButton("忘记", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								wiFiAdmin.disConnectionWifi(wifiConfiguration.networkId);
								wiFiAdmin.rmConfiguration(wifiConfiguration.networkId);
							}
						});
						builder.create().show();
					}
				} else {
					// 没有配置好信息，配置
					WifiCipherType type=null;
					String capability=result.capabilities.toUpperCase();
					if (capability.contains("WPA")) {
						type=WifiCipherType.WIFICIPHER_WPA;
					} else if (capability.contains("WEP")) {
						type=WifiCipherType.WIFICIPHER_WPA;
					}
					if(null==type) {
						if (wiFiAdmin.Connect(wifiItemSSID, null,WifiCipherType.WIFICIPHER_NOPASS)) {
							ToastUtil.showText(R.string.wifi_is_starting, Toast.LENGTH_SHORT);
						} else {
							ToastUtil.showText(R.string.wifi_connect_failed, Toast.LENGTH_SHORT);
						}
						return;
					}
					final WifiCipherType finaltype=type;
					PswDialog pswDialog = new PswDialog(WifiFragment.this.getActivity(),
							new OnCustomDialogListener() {
								@Override
								public void back(String str) {
									if (str != null && !"".equals(str)) {
										wiFiAdmin.getConfiguration();// 添加了配置信息，要重新得到配置信息
										if (wiFiAdmin.Connect(wifiItemSSID, str,finaltype)) {
											ToastUtil.showText(R.string.wifi_is_starting, Toast.LENGTH_SHORT);
										} else {
											ToastUtil.showText(R.string.wifi_connect_failed, Toast.LENGTH_SHORT);
										}
									} else {//真正没有密码的wifi就不会弹出对话框。
										ToastUtil.showText(R.string.password_can_not_be_null, Toast.LENGTH_SHORT);
									}
								}
							}, wifiItemSSID);
					pswDialog.show();
				}
			}
		});
	}

	
	@Override
	public void onDestroyView() {
		getActivity().unregisterReceiver(mReceiver);
		super.onDestroyView();
	}

}
