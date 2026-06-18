package com.zkzk.pra.adapter;

import java.util.List;
import java.util.zip.Inflater;

import org.xutils.x;
import org.xutils.view.annotation.ViewInject;

import com.zkzk.pra.R;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WifiAdapter extends BaseAdapter{
	private Context context;
	List<ScanResult> results;
	private LayoutInflater inflater;
	
	
	
	public WifiAdapter(Context context, List<ScanResult> results) {
		super();
		this.context = context;
		this.results = results;
		inflater=LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		if(null!=results)
		return results.size();
		else return 0;
	}

	@Override
	public Object getItem(int position) {
		if(position<0||position>=results.size())	return null;
		return results.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_wifi_info, null);
			holder = new ViewHolder();
			x.view().inject(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ScanResult result=results.get(position);
		holder.tvWifiName.setText(result.SSID);
		String capabilities=result.capabilities;
		
//		Log.d("jemen","ScanResult.capabilities="+capabilities);
		if(null!=capabilities&&(capabilities.contains("WPA") || capabilities.contains("wpa")
				||capabilities.contains("WEP") || capabilities.contains("wep")
				||capabilities.contains("AP"))) {
			switch(WifiManager.calculateSignalLevel(result.level, 4)) {
			case 0:holder.ivSignal.setImageResource(R.drawable.settings_locked_signal_level_0);
				break;
			case 1:
				holder.ivSignal.setImageResource(R.drawable.settings_locked_signal_level_1);
				break;
			case 2:
				holder.ivSignal.setImageResource(R.drawable.settings_locked_signal_level_2);
				break;
			case 3:
				holder.ivSignal.setImageResource(R.drawable.settings_locked_signal_level_3);
				break;
			}
		}else {
		switch(WifiManager.calculateSignalLevel(result.level, 4)) {
			case 0:holder.ivSignal.setImageResource(R.drawable.settings_signal_level_0);
				break;
			case 1:
				holder.ivSignal.setImageResource(R.drawable.settings_signal_level_1);
				break;
			case 2:
				holder.ivSignal.setImageResource(R.drawable.settings_signal_level_2);
				break;
			case 3:
				holder.ivSignal.setImageResource(R.drawable.settings_signal_level_3);
				break;
			}
		}
		return convertView;
	}
	
	private class ViewHolder{
		@ViewInject(R.id.itv_tem_wifi_name)
		private TextView tvWifiName;
		@ViewInject(R.id.iv_wifi_signal)
		private ImageView ivSignal;
	}

}
