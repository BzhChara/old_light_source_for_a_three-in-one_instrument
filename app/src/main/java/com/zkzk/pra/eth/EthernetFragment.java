///*
// * Copyright (C) 2013-2014 Freescale Semiconductor, Inc.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.zkzk.pra.eth;
//
//import android.os.Bundle;
//
//import org.xutils.x;
//
//import com.whswzz.prfluroanalyzer.app.MyApp;
//import com.zkzk.pra.R;
//
//import android.app.Activity;
//import android.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.util.Log;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//import android.widget.Button;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;
//import android.content.DialogInterface;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.net.NetworkInfo.State;
//import android.content.BroadcastReceiver;
//import android.view.View.OnClickListener;
//import android.text.method.ScrollingMovementMethod;
//import android.view.Window;
//import android.view.WindowManager;
//import android.content.IntentFilter;
//import android.content.Intent;
//
//public class EthernetFragment extends Fragment {
//    private EthernetEnabler mEthEnabler;
//    private EthernetConfigDialog mEthConfigDialog;
//    private Button mBtnConfig;
//    private Button mBtnCheck;
//    private EthernetDevInfo  mSaveConfig;
//    private ConnectivityManager  mConnMgr;
//    private String TAG = "EthernetMainActivity";
//    private static String Mode_dhcp = "dhcp";
//    private boolean shareprefences_flag = false;
//    private boolean first_run = true;
//    public static final String FIRST_RUN = "ethernet";
//    private Button mBtnAdvanced;
//    private EthernetAdvDialog mEthAdvancedDialog;
//    private final BroadcastReceiver mEthernetReceiver = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//            if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){
//                NetworkInfo info =
//                    intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
//                if (info != null) {
//                    Log.i(TAG,"getState()="+info.getState() + "getType()=" +
//                            info.getType());
//                    if (info.getType() == ConnectivityManager.TYPE_ETHERNET) {
//                        if (info.getState() == State.DISCONNECTED)
//                            mConnMgr.setGlobalProxy(null);
//                        if (info.getState() == State.CONNECTED)
//                            mEthEnabler.getManager().initProxy();
//                    }
//                }
//            }
//        }
//    };
//	private View root;
//	private IntentFilter filter;
///**
// * It's not useable for sun8i
// */
//	public EthernetFragment() {
////		SharedPreferences sp = getActivity().getSharedPreferences("ethernet", Context.MODE_WORLD_WRITEABLE);
//        mEthEnabler = new EthernetEnabler(MyApp.getApp());
//        filter = new IntentFilter();
//        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//	}
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//    	root=inflater.inflate(R.layout.fragment_ethernet2, null);
////		x.view().inject(this, root);
//		init();
////		LogUtil.e("EthernetFragment onCreateView 执行");
//		return root;
//    }
//
//
//
//
//    private void init() {
//         addListenerOnBtnConfig();
//         addListenerOnBtnCheck();
//         addListenerOnBtnAdvanced();
//         mConnMgr = (ConnectivityManager)MyApp.getApp().getSystemService(Context.CONNECTIVITY_SERVICE);
//         getActivity().registerReceiver(mEthernetReceiver, filter);
//	}
//
//
//
//
//
//    public void addListenerOnBtnConfig() {
//        mBtnConfig = (Button) root.findViewById(R.id.btnConfig);
//
//        mBtnConfig.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mEthConfigDialog = new EthernetConfigDialog(getActivity(), mEthEnabler);
//                mEthEnabler.setConfigDialog(mEthConfigDialog);
//                mEthConfigDialog.show();
//            }
//        });
//    }
//
//    public void addListenerOnBtnCheck() {
//        mBtnConfig = (Button) root.findViewById(R.id.btnCheck);
//
//        mBtnConfig.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                TextView text = (TextView) root.findViewById(R.id.tvConfig);
//                text.setMovementMethod(ScrollingMovementMethod.getInstance());
//                mSaveConfig = mEthEnabler.getManager().getSavedConfig();
//                if (mSaveConfig != null) {
//                    final String config_detail = "IP Mode       : " + mEthEnabler.getManager().getSharedPreMode() + "\n"
//                            + "IP Address    : " +  mEthEnabler.getManager().getSharedPreIpAddress() + "\n"
//                            + "DNS Address   : " + mEthEnabler.getManager().getSharedPreDnsAddress() + "\n"
//                            + "Proxy Address : " + mEthEnabler.getManager().getSharedPreProxyAddress() + "\n"
//                            + "Proxy Port    : " + mEthEnabler.getManager().getSharedPreProxyPort() + "\n";
//                    text.setText(config_detail);
//                }
//            }
//        });
//    }
//
//    public void addListenerOnBtnAdvanced() {
//        mBtnAdvanced = (Button) root.findViewById(R.id.btnAdvanced);
//        mBtnAdvanced.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mSaveConfig = mEthEnabler.getManager().getSavedConfig();
//                if (mSaveConfig != null) {
//                    mEthAdvancedDialog = new EthernetAdvDialog(getActivity(),mEthEnabler);
//                    mEthEnabler.setmEthAdvancedDialog(mEthAdvancedDialog);
//                    mEthAdvancedDialog.show();
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onDestroyView() {
//    	Log.i(TAG, "onStop() will force clear global proxy set by ethernet");
//    	if(null!=mConnMgr)	mConnMgr.setGlobalProxy(null);
//    	getActivity().unregisterReceiver(mEthernetReceiver);
//    	super.onDestroyView();
//    }
//}
