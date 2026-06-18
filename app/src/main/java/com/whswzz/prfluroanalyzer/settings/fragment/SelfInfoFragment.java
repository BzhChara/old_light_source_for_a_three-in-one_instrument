package com.whswzz.prfluroanalyzer.settings.fragment;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import com.whswzz.prfluroanalyzer.A5InstructActivity;
import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.consts.Consts;
import com.whswzz.prfluroanalyzer.model.DataModel;
import com.whswzz.prfluroanalyzer.settings.ui.PicSegmentDialog;
import com.whswzz.prfluroanalyzer.settings.ui.PreviewDialog;
import com.whswzz.prfluroanalyzer.settings.ui.QRCodeTestDialog;
import com.zkzk.pra.R;
import com.zkzk.pra.activity.WebActivity;
import com.zkzk.pra.biz.UpdateBiz;
import com.zkzk.pra.entity.Location;
import com.zkzk.pra.entity.VersionEntity;
import com.zkzk.pra.ui.DownloadDialog;
import com.zkzk.pra.ui.MyDialog;
import com.zkzk.pra.utils.ExceptionHandler;
import top.jemen.utils.NetUtil;

import com.zkzk.pra.utils.ToastUtil;
import com.zkzk.pra.utils.Tools;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
//import android.webkit.FindActionModeCallback;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import top.jemen.utils.LogUtil;

public class SelfInfoFragment extends Fragment{
	private View root;
	private Button btMore, btUpdate,btFocus,btSegment,btInstructs;//,btCalibrate;
	private TextView tvVersion,tvLocation;
	private Context context;
	private TextView tvSerialNum;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.fragment_self_info, null);
		init();
		setListeners();
		initDownloadDialog() ;
		return root;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		DataModel.getInstance().lightOn(1);
	}
	@Override
	public void onStop() {
		DataModel.getInstance().lightOn(0);
		super.onStop();
	}
	

	private void init() {
		try {
			context = getActivity();
			btMore = (Button) root.findViewById(R.id.bt_more);
			tvVersion = (TextView) root.findViewById(R.id.tv_version);
			PackageManager packageManager = context.getPackageManager();
			String packageName = context.getPackageName();
			PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
			tvVersion.setText(packageInfo.versionName);
			btUpdate = (Button) root.findViewById(R.id.bt_upload);
			btUpdate.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
			btMore.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
//			btCalibrate=(Button) root.findViewById(R.id.bt_calibrate);
			SharedPreferences pref = MyApp.getApp().getPref();
//			String calibrate=getResources().getString(R.string.calibrate);
//			btCalibrate.setText(calibrate+"：k="+pref.getFloat(Consts.K, 1)+",incr="+pref.getFloat(Consts.INCR, 0));
			
			tvSerialNum = (TextView) root.findViewById(R.id.tv_serial);

			tvSerialNum.setText(MyApp.getApp().getJemenId());
			btFocus=(Button) root.findViewById(R.id.bt_fucus);
			btSegment=(Button) root.findViewById(R.id.bt_segment);
			btInstructs=root.findViewById(R.id.bt_instructs);

			tvLocation= (TextView) root.findViewById(R.id.tv_location);
			Location loc = MyApp.getApp().getLocation();


			tvLocation.setText(String.format("%.4f",loc.getLatitude())+","+String.format("%.4f",loc.getLongitude())+","+loc.getDescribe());


		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}
	private DownloadDialog downloadDialog;
	private void initDownloadDialog() {
		downloadDialog =new DownloadDialog(getActivity());
		downloadDialog.setTitle(R.string.upgrating);
		downloadDialog.setMessate(R.string.upgrating_wait);
		downloadDialog.setCancelable(false);
	}

	private void setListeners() {
		btUpdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UpdateBiz.getNewVersionInfo(handler);
				btUpdate.setEnabled(false);
			}
		});
		
		btMore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(NetUtil.isConnected(getActivity())) {
					Intent intent=new Intent(getActivity(),WebActivity.class);
					getActivity().startActivity(intent);
				}else {
					ToastUtil.showText(R.string.connect_internet_first, Toast.LENGTH_SHORT);				}
			}
		});
		
//		btCalibrate.setOnClickListener(this);
		btFocus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new PreviewDialog(context).show();
			}
		});
		btSegment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new PicSegmentDialog(context).show();
			}
		});
		
		btSegment.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				new QRCodeTestDialog(context).show();
				return true;
			}
		});

		btInstructs.setOnClickListener((View v)->{
			Intent intent=new Intent(getActivity(), A5InstructActivity.class);
			getActivity().startActivity(intent);
		});

	}
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(android.os.Message msg) {
			try {
				// 判断是那个消息
				int msgId = msg.what;
				Bundle bundle = msg.getData();
				switch (msgId) {
				case Consts.MSG_SHOW_VERSION:
					// 从消息中接收实体类
					final VersionEntity versionEntity = (VersionEntity) bundle.getSerializable("data");
					// 取本地版本号
					// String currentVersion = Tools
					// .getCurrentVersion(getActivity());
					int currentV = Tools.getCurrentV(getActivity());
					// 判断服务器的版本是不是最新的
					// double dNewVersion = Double.parseDouble(versionEntity
					// .getVersion());
					// double dCurrentVersion = Double.parseDouble(currentVersion);
					int newV = versionEntity.getVersionCode();
					LogUtil.d("newV=" + newV + ",currentV=" + currentV);
					if (newV > currentV) {
						// 显示dialog
						new MyDialog(getActivity(), new MyDialog.BackListener() {
							@Override
							public void back() { //
								UpdateBiz.downloadAPK2(handler, versionEntity.getApkUrl());
								downloadDialog.show();
							}
						},getString(R.string.notice), "发现新版本:" + versionEntity.getVersion() + "\n" + versionEntity.getChangeLog()
								+ "\n是否升级？").show();
						;
					} else {
						ToastUtil.showText("当前已是最新版本", Toast.LENGTH_SHORT);
					}
					break;
				case Consts.MSG_DOWNLOAD_PROGRESS:
					downloadDialog.setProgress(msg.arg1,msg.arg2);
					break;
				case Consts.MSG_INSTALL_APK:
					// 系统自带了一个安装apk的activity
//					downloadDialog.dismiss();
					LogUtil.d("开始安装");
					final String apkPath = bundle.getString("apkPath");
					File apkFile = new File(apkPath);
					setUpdateDir(apkFile);
					// Intent intent=new Intent(Intent.ACTION_VIEW);
					// Uri uri=Uri.fromFile(apkFile);
					// //mime
					// String type="application/vnd.android.package-archive";
					// intent.setDataAndType(uri, type);
					// Activity activity=getActivity();
					// activity.startActivity(intent);

					// 下面是直接调用接口安装的方法，可以安装系统应用.
					// Uri mPackageURI = Uri.fromFile(apkFile);
					// int installFlags = 0;
					//
					// PackageManager pm = MyApplication.getApp().getPackageManager();
					// try{
					// PackageInfo pi =
					// pm.getPackageInfo("com.zkzk.ecas",PackageManager.GET_UNINSTALLED_PACKAGES);
					// if(pi != null)
					// {
					//// installFlags |= PackageManager.REPLACE_EXISTING_PACKAGE;
					// }
					//
					// }catch (NameNotFoundException e){
					//
					// }
					// PackageInstallObserver observer = new PackageInstallObserver();
					// pm.installPackage(mPackageURI, observer, installFlags);

					// 进行静默安装
					LogUtil.d("show ProDialog");
					new Thread() {
						public void run() {
//							if(Tools.isSMDK()) {//讯为三星板子
								int result = installSlient(MyApp.getApp(), apkPath);
								Message msg1 = Message.obtain(SelfInfoFragment.this.handler);
								msg1.what = Consts.INSTALL_RESULT;
								msg1.arg1 = result;
								msg1.sendToTarget();
//							}else {		//这种方案可以较准确的获得安装失败的信息。
//								Tools.installApk(apkPath,new PackageInstallObserver() {
//									@Override
//									public void packageInstalled(String apackageName, int returnCode) throws RemoteException {
//										Message msg1 = Message.obtain(SelfInfoFragment.this.handler);
//										msg1.what = Consts.INSTALL_RESULT;
//										msg1.arg1 = returnCode;
//										msg1.sendToTarget();
//									}
//								});
//							}
							
							
						}
					}.start();

					break;
				case Consts.MSG_ERROR:
					ToastUtil.showText(getString(R.string.upgrage_failed) + msg.obj, Toast.LENGTH_SHORT);
					btUpdate.setEnabled(true);
					downloadDialog.dismiss();

				case Consts.INSTALL_RESULT:
					downloadDialog.dismiss();
//					switch(msg.arg1){
//					case PackageManager.INSTALL_SUCCEEDED:
//						LogUtil.d("更新成功");
//						ToastUtil.showText(R.string.update_succeed,Toast.LENGTH_SHORT);
//						break;
//					case PackageManager.INSTALL_FAILED_ALREADY_EXISTS:
//						ToastUtil.showText("程序已存在",Toast.LENGTH_SHORT);
//						break;
//					case PackageManager.INSTALL_FAILED_INSUFFICIENT_STORAGE:
//						ToastUtil.showText("存储控件不足，安装失败",Toast.LENGTH_SHORT);
//						break;
//					default:
//						LogUtil.d("安装失败");
//						ToastUtil.showText(R.string.update_failed,Toast.LENGTH_SHORT);
//					}
					break;
				default:
					break;
				}

			} catch (Exception e) {
				ExceptionHandler.handleException(e);
			}
		};
	};

	/**
	 * @param updateDir
	 *            就是可以执行的文件 void 修改文件的权限，可读、可写、可执行
	 * @date 2015年9月13日
	 * @author liuyonghong
	 */
	private void setUpdateDir(File updateDir) {
		try {
			Process p = Runtime.getRuntime().exec("chmod 777 " + updateDir);
			int status = p.waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * install slient
	 *
	 * @param context
	 * @param filePath
	 * @return 0 means normal, 1 means file not exist, 2 means other exception error
	 */
	public static int installSlient(Context context, String filePath) {
		File file = new File(filePath);
		if (filePath == null || filePath.length() == 0 || (file = new File(filePath)) == null || file.length() <= 0
				|| !file.exists() || !file.isFile()) {
			return 1;
		}

		String[] args = { "pm", "install", "-r", filePath };
		ProcessBuilder processBuilder = new ProcessBuilder(args);

		Process process = null;
		BufferedReader successResult = null;
		BufferedReader errorResult = null;
		StringBuilder successMsg = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();
		int result;
		try {
			process = processBuilder.start();
			successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
			errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String s;

			while ((s = successResult.readLine()) != null) {
				successMsg.append(s);
			}

			while ((s = errorResult.readLine()) != null) {
				errorMsg.append(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
			result = 2;
		} catch (Exception e) {
			e.printStackTrace();
			result = 2;
		} finally {
			try {
				if (successResult != null) {
					successResult.close();
				}
				if (errorResult != null) {
					errorResult.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (process != null) {
				process.destroy();
			}
		}

		// TODO should add memory is not enough here
		if (successMsg.toString().contains("Success") || successMsg.toString().contains("success")) {
			result = 0;
		} else {
			result = 2;
		}
		LogUtil.d( "successMsg:" + successMsg + ", ErrorMsg:" + errorMsg);
		return result;
	}

	
	
}
