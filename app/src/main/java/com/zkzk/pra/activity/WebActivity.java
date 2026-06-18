package com.zkzk.pra.activity;

import java.net.URL;

import com.whswzz.prfluroanalyzer.base.BaseActivity;
import com.zkzk.pra.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 利用网站以展示更多的产品，但是因为部分产品以显鸿科技名义销售，
 * @author Administrator
 */
public class WebActivity extends BaseActivity {
	private WebView web;
	private JsInterface jsInterface;
	private ImageButton btBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);
		init();

		// 实例化接口JsInterface
		jsInterface = new JsInterface(web);
		// 初始化WebSetting
		initWebSetting();
		Intent intent=getIntent();
		String url=null;
		if(null!=intent&&(url=intent.getStringExtra("url"))!=null&&url.startsWith("http://")) {
			web.loadUrl(url);
		}else {
			web.loadUrl("http://http://www.apbios.cn");
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		setListeners();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initWebSetting() {
		WebSettings webSettings = web.getSettings();
		// 允许JS交互
		web.getSettings().setJavaScriptEnabled(true);
		// 设置JS的接口
		web.addJavascriptInterface(jsInterface, "jsInterface");
		web.getSettings().setUseWideViewPort(true); // 宽视口
		web.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);// 不加上，会显示白边(加了还是没有解决)

		webSettings.setUseWideViewPort(true); // 关键点
		webSettings.setAllowFileAccess(true); // 允许访问文件
		webSettings.setSupportZoom(true); // 支持缩放
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 不加载缓存内容
		web.setWebChromeClient(new WebChromeClient() {

		}); // 据说播放视屏需要添加


		web.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}

		});
		// mButton.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// int param = Integer.parseInt(mEditText.getText().toString());
		// jsInterface.java_call_Js(param);
		// param = 0;
		// }
		// });

	}

	private void init() {
		web = (WebView) findViewById(R.id.web);
		btBack = (ImageButton) findViewById(R.id.ib_bottom_back);
	}

	private void setListeners() {
		btBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (web.canGoBack()) {
					web.goBack();
					return;
				} else {
					finish();
				}
			}
		});
	}

	// 虽然命名为Interface，其实是伪接口，主要是为了方便理解及以后做抽象处理
	public class JsInterface {
		private WebView mWebView;

		// 构造方法，传入一个参数WebView
		public JsInterface(WebView webView) {
			this.mWebView = webView;
		}

		// 这个方法是js调用java
		public void js_call_java() {
			new Handler(Looper.getMainLooper()).post(new Runnable() {
				@Override
				public void run() {
					// 主线程更新UI
					Toast.makeText(mWebView.getContext(), "I'm a function in java", Toast.LENGTH_SHORT).show();
				}
			});

		}

		// 这个方法 是java调用js
		public void java_call_Js(int param) {
			// 这里调用html中的js代码的 java_call_Js 方法
			mWebView.loadUrl(String.format("javascript:java_call_Js(" + param + ")"));
		}
	}

}
