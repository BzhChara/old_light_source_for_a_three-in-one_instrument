package com.whswzz.prfluroanalyzer.settings.fragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xutils.x;
import org.xutils.view.annotation.ViewInject;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.param.Params;
import com.zkzk.pra.R;
import com.zkzk.pra.activity.WebActivity;
import top.jemen.utils.NetUtil;
import com.zkzk.pra.utils.ToastUtil;
import com.zkzk.pra.utils.Tools;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import top.jemen.utils.QVMProtect;

public class DatauploadFragment extends Fragment implements OnClickListener {
	private View root;
	@ViewInject(R.id.et_set_url)
	private EditText etUrl;
	@ViewInject(R.id.bt_laws)
	private Button btLaws;
	@ViewInject(R.id.bt_url_ok)
	private Button btOk;

	@ViewInject(R.id.bt_regist)
	private Button btRegist;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root=inflater.inflate(R.layout.fragment_dataupload, null);
		x.view().inject(this, root);
		init();
		setListeners();
		return root;
	}
	
	
	private void setListeners() {
		btLaws.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent toWeb=new Intent(getActivity(),WebActivity.class);
				toWeb.putExtra("url", "https://www.samr.gov.cn/zljds/zcfg/index.html");
				getActivity().startActivity(toWeb);
			}
		});
		
		btOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onOkClicked();
			}
		});
		btRegist.setOnClickListener(this);
	}

	@QVMProtect
	private void init() {
		String url= Params.UPLOAD_URL;
		if(null!=url&&!"".equals(url)) {
			etUrl.setText(url);
			etUrl.setSelection(url.length());
		}
		
		NetUtil.isConnected(MyApp.getApp());//测试，检测网络连接状态
		btLaws.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
		if(!Tools.getCurrentVersion(MyApp.getApp()).toLowerCase().contains("hb")){
			btRegist.setVisibility(View.GONE);
		}
	}


	/**
	 */
	public void onOkClicked() {
		String url=etUrl.getText().toString();
		String regEx = "^(http|https|ftp)\\://([a-zA-Z0-9\\.\\-]+(\\:[a-zA-Z0-9\\.&amp;%\\$\\-]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0-9\\-]+\\.)*[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,4})(\\:[0-9]+)?(/[^/][a-zA-Z0-9\\.\\,\\?\\'\\\\/\\+&amp;%\\$#\\=~_\\-@]*)*$";
		Pattern p = Pattern.compile(regEx);
		Matcher matcher = p.matcher(url);
		if(null!=url&&!"".equals(url)&&matcher.matches()) {
			Params.setURL(url);
			ToastUtil.showText("URL设置成功", Toast.LENGTH_SHORT);
		}else {
			ToastUtil.showText(R.string.url_wrong_format, Toast.LENGTH_SHORT);
		}
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.bt_regist:
				Intent intent=new Intent(getActivity(),WebActivity.class);
				intent.putExtra("url", "http://jg.hebny.cn:9089/reg/index");
				getActivity().startActivity(intent);

				break;
		}
	}
}
