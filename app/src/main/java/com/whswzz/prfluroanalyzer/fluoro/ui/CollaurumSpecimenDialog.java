package com.whswzz.prfluroanalyzer.fluoro.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.MapView;
import com.example.pickerviewlibrary.picker.TeaPickerView;
import com.example.pickerviewlibrary.picker.entity.PickerData;
import com.example.pickerviewlibrary.picker.listener.OnPickerClickListener;
import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.consts.Consts;
import com.whswzz.prfluroanalyzer.entity.Organization;
import com.whswzz.prfluroanalyzer.entity.Source;
import com.whswzz.prfluroanalyzer.entity.Species;
import com.whswzz.prfluroanalyzer.fluoro.FluoroActivity;
import com.whswzz.prfluroanalyzer.fluoro.data.FluDataActivity;
import com.whswzz.prfluroanalyzer.fluoro.entity.FluData;
import com.whswzz.prfluroanalyzer.param.Params;
import com.whswzz.prfluroanalyzer.utils.PrinterJPW;
import com.zkzk.pra.R;
import com.zkzk.pra.activity.DetectActivity;
import com.zkzk.pra.entity.Data;
import com.zkzk.pra.utils.ListUtil;
import com.zkzk.pra.utils.Tools;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import top.jemen.interfaces.ICallback;
import top.jemen.ui.BaseDialog;
import top.jemen.utils.LogUtil;

public class CollaurumSpecimenDialog extends BaseDialog implements android.view.View.OnClickListener {
	private TextView tvChannel, tvInhabition, tvLimit, tvResult, tvSn, tvSpecimen, tvSpecimenName,
	tvSourceUnit, tvProj,tvProjName,tvReferenceBasis,
			tvUser;
	private EditText etSn;
	private EditText etSpecimen;
	private Spinner spSource, spUser;
	private Button btSave;
	private MapView mapView;
	private LocationClient locationClient;
	private String oldCode = MyApp.getApp().getPref().getString(Consts.KEY_CITYCODE, "218");

	private FluData data;
	public static final String COLON = "：";

	private Activity context;
	private TextView tvTime;

	private List<Source> sources;
	private List<Organization> users = MyApp.getApp().getOrganizations();
	private List<Species> species = MyApp.getApp().getLsSpecies();
	protected Source source;
	protected Organization user;
	private List<Species>  lsProjs=MyApp.getApp().getLsProjs();
	private ICallback callback;
	

	public CollaurumSpecimenDialog(Activity context, FluData data) {
		super(context, R.style.dialog);
		this.context = context;
		this.data = data;
	}
	public CollaurumSpecimenDialog(Activity context, FluData data,ICallback callback) {
		super(context, R.style.dialog);
		this.context = context;
		this.data = data;
		this.callback=callback;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.dialog_specimen_msg);
		initView();
		initValue();
		if (context instanceof FluoroActivity) { // 样品信息设置null == data.getResult()
			setListeners();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		tvResult.setText(getString(R.string.result) + COLON + data.getResult());
	}

	private void setListeners() {
		tvSpecimenName.setOnClickListener(this);
		btSave.setOnClickListener(this);
		tvProjName.setOnClickListener(this);
	}
	
	public void setViewVisiable(int id,int visiable) {
		findViewById(id).setVisibility(visiable);
	}
	

	/**
	 * 初始化控件
	 */
	private void initView() {
		tvChannel = (TextView) findViewById(R.id.tv_channel);
		tvProj = (TextView) findViewById(R.id.tv_proj);
		tvProjName=(TextView) findViewById(R.id.tv_proj_name);
		tvProjName.setText(data.getProj()+"");
	
		
		tvInhabition = (TextView) findViewById(R.id.tv_inhibition_ratio);
		tvLimit = (TextView) findViewById(R.id.tv_limit);

		tvResult = (TextView) findViewById(R.id.tv_result);
		etSn = (EditText) findViewById(R.id.et_sn);
		btSave = (Button) findViewById(R.id.bt_save);
		spSource = (Spinner) findViewById(R.id.sp_specimen_source);
		// mapView = (MapView) findViewById(R.id.mapView_dialog);
		// tvLocation = (TextView) findViewById(R.id.tv_location);
		tvTime = (TextView) findViewById(R.id.tv_time);
		tvSn = (TextView) findViewById(R.id.tv_sn);
		tvSpecimen = (TextView) findViewById(R.id.tv_specimen);
		tvSourceUnit = (TextView) findViewById(R.id.tv_source);

		tvSpecimenName = (TextView) findViewById(R.id.tv_specimen_name);
		etSpecimen=findViewById(R.id.et_specimen);
		spUser = (Spinner) findViewById(R.id.sp_user);
		tvUser = (TextView) findViewById(R.id.tv_user);

		tvReferenceBasis=(TextView) findViewById(R.id.tv_reference_basis);
//		tvReferenceBasis.append(Params.GB);
	}

	/**
	 * jemen：根据使用场景不同而加载不同的控件
	 */
	private void initValue() {
		tvChannel.setText(getString(R.string.channel_number) + COLON + data.getChannelNum());
		// map = mapView.getMap();
		if (context instanceof FluoroActivity) { // 样品信息输入,之前使用的null==data.getResult();
			// map.setMyLocationEnabled(true);
			etSn.setVisibility(View.VISIBLE);
			etSn.setText(Consts.SNDF.format(new Date()));
			if(!TextUtils.isEmpty(data.getSn())) {
				etSn.setText(data.getSn());//
			}else{
				if(!TextUtils.isEmpty(data.getChannel())) {
					etSn.setText(Consts.SNDF.format(new Date()) + data.getChannel());
				}else{
					etSn.setText(Consts.SNDFms.format(new Date()));
				}
			}
			
			spSource.setVisibility(View.VISIBLE);
			tvTime.setVisibility(View.GONE);
			sources = MyApp.getApp().getSources();
			if (null != sources) {
				// 建立Adapter并且绑定数据源*****************当需要动态增删时候使用
				ArrayAdapter<Source> sourceAdapter = new ArrayAdapter<Source>(context, R.layout.my_spinner, sources);
				sourceAdapter.setDropDownViewResource(R.layout.my_spinner_dropdown);
				// 绑定 Adapter到控件
				spSource.setAdapter(sourceAdapter);
				spSource.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
						source = sources.get(pos);
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}
				});

			}
			if (null != users) {
				// 建立Adapter并且绑定数据源*****************当需要动态增删时候使用
				ArrayAdapter<Organization> userAdapter = new ArrayAdapter<Organization>(context, R.layout.my_spinner,
						users);
				userAdapter.setDropDownViewResource(R.layout.my_spinner_dropdown);
				// 绑定 Adapter到控件
				spUser.setAdapter(userAdapter);
				int userI = MyApp.getApp().getPref().getInt(Consts.USER_INDEX, 0);
				spUser.setSelection(userI);
				spUser.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
						user = users.get(pos);
						MyApp.getApp().getPref().edit().putInt(Consts.USER_INDEX, pos).apply();
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}
				});
			}

			if(null!=data&&null!=data.getProj()){
				tvReferenceBasis.setText("参考标准："+ PrinterJPW.getGB(data.getProj()));

				String key=data.getProj()+"-"+data.getSpecimen();
				if(limits.containsKey(key)){
					tvLimit.setText(getString(R.string.limit_range) + COLON + String.format("%.3f",limits.get(key)));
				}

			}

		} else { // 检测结果展示
			// btSave.setVisibility(View.GONE);
			btSave.setText("打印");
			etSn.setVisibility(View.GONE);
			tvSpecimenName.setVisibility(View.GONE);
			spUser.setVisibility(View.GONE);
			spSource.setVisibility(View.GONE);
			tvProjName.setVisibility(View.GONE);
			tvProj.setText(getString(R.string.detect_proj) + COLON + data.getProj());

			tvSn.setText(getString(R.string.specimen_sn_) + data.getSn());
			tvSpecimen.setText(getString(R.string.specimen_name_) + data.getSpecimen());
			tvSourceUnit.setText(getString(R.string.customer_org_) + data.getSourceUnit());
			tvUser.setText("用户信息：" + data.getUserOrg());

			Calendar c = Calendar.getInstance(Tools.getTimeZone());
			c.setTimeInMillis(data.getTime());
			tvTime.setText(getString(R.string.detect_time) + "：" + DateFormat.format("yyyy-MM-dd k:mm", c));

		}

		// tvInhabition.setText(name+ String.format("%.1f", data.getInhibitionRatio() *
		// 100) + "%");
		tvLimit.setText(getString(R.string.limit_range) + COLON + data.getLimit());
		tvResult.setText(getString(R.string.result) + COLON + data.getResult());
		if (data.getC() != 0) {
			tvInhabition.setText("T/C:" + data.getT() / data.getC());
		} else {
			tvInhabition.setText("T/C:");
		}
	}

	private String getString(int id) {
		return getContext().getString(id);
	}

	@Override
	protected void onStop() {
		if (data.getResult() == null && null != locationClient) {// 样品信息设置。
			locationClient.stop(); //
		}
		if (null != mapView)
			mapView.onDestroy();
		mapView = null;
		super.onStop();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_save:
			if (context instanceof FluoroActivity) {
				// MyApplication.getApp().addData(data);
				data.setSn(etSn.getText().toString());

				String specimen=etSpecimen.getText().toString();
				if(TextUtils.isEmpty(specimen)){
					specimen=tvSpecimenName.getText().toString();
				}
				data.setSpecimen(specimen);

				data.setProj(tvProjName.getText().toString());
				if (null != source) {
					data.setSourceOrg(source.getUnit());
					data.setSourceAddr(source.getAddr());
					data.setSourceContact(source.getContact());
					data.setSourcePhone(source.getPhone());
					data.setSourceOrgCode(source.getCode());
					data.setSourceOrgType(source.getType());
				}
				if (null != user) {
					data.setWorkOrg(user.getName());
					data.setUserAddr(user.getAddr());
					data.setUserContact(user.getContact());
					data.setUserPhone(user.getPhone());
					data.setOperator(user.getOperator());
					data.setUsrCode(user.getCode());
					data.setToken(user.getToken());
				}
				LogUtil.d("user=	" +user);
				LogUtil.d("data=	" +data);
				MyApp.globalCurrentSn = data.getSn();
				dismiss();
				if(null!=callback) {
					callback.onSuccess(data);
				}
			} else {
				PrinterJPW.print(data);
			}

			break;
		case R.id.tv_specimen_name:
			if (species == null) {
				return;
			}
			if (null == specimenPickerView) {
				List<String> kinds = new ArrayList<>();
				Map<String, List<String>> map = new HashMap<>();
				for (Species sp : species) {
					String name = sp.getName();
					kinds.add(name);
					if (null != sp.getSubSpecies()) {
						List<String> subNames = ListUtil.getRootNames(sp.getSubSpecies());
						map.put(name, subNames);
					}
				}
				// 设置数据有多少层级
				PickerData data = new PickerData();
				data.setFirstDatas(kinds);
				data.setSecondDatas(map);
				data.setInitSelectText("请选择");
				specimenPickerView = new TeaPickerView((Activity) context, data);
				specimenPickerView.setScreenH(3).setDiscolourHook(true).setRadius(25).setContentLine(true).setRadius(25)
						.build();
				// 选择器点击事件
				specimenPickerView.setOnPickerClickListener(new OnPickerClickListener() {
					@Override
					public void OnPickerClick(PickerData pickerData) {
						Toast.makeText(context, pickerData.getFirstText() + ":" + pickerData.getSecondText() + ","
								+ pickerData.getThirdText(), Toast.LENGTH_SHORT).show();
						tvSpecimenName.setText(pickerData.getSecondText());
						specimenPickerView.dismiss();// 关闭选择器
						
						if(null!=limits) {
							String key=tvProjName.getText().toString()+"-"+tvSpecimenName.getText().toString();
							LogUtil.d(key);
							Double v=limits.get(key);
							if(null!=v) {
								tvLimit.setText("参考限值："+String.format("%.3f", v)+" mg/kg");
							}else {
								tvLimit.setText("参考限值：");
							}
						}
						
					}
				});
			}
			LogUtil.d("teaPicker=" + specimenPickerView + ",  v=" + v);
			specimenPickerView.showAsDropDown(v, 0, 0);

			break;
		case R.id.tv_proj_name:
			if (lsProjs == null) {
				return;
			}
			if (null == projPickerView) {
				List<String> kinds = new ArrayList<>();
				Map<String, List<String>> map = new HashMap<>();
				for (Species sp : lsProjs) {
					String name = sp.getName();
					kinds.add(name);
					if (null != sp.getSubSpecies()) {
						List<String> subNames = ListUtil.getRootNames(sp.getSubSpecies());
						map.put(name, subNames);
					}
				}
				// 设置数据有多少层级
				PickerData data = new PickerData();
				data.setFirstDatas(kinds);
				data.setSecondDatas(map);
				data.setInitSelectText("请选择");
				projPickerView = new TeaPickerView(context, data);
				projPickerView.setScreenH(2).setDiscolourHook(true).setRadius(25).setContentLine(true)
				.build();
				// 选择器点击事件
				projPickerView.setOnPickerClickListener(new OnPickerClickListener() {
					@Override
					public void OnPickerClick(PickerData pickerData) {
						Toast.makeText(context, pickerData.getFirstText() + ":" + pickerData.getSecondText() + ","
								+ pickerData.getThirdText(), Toast.LENGTH_SHORT).show();
						tvProjName.setText(pickerData.getSecondText());
						projPickerView.dismiss();// 关闭选择器
						if(null!=limits) {
							String key=tvProjName.getText().toString()+"-"+tvSpecimenName.getText().toString();
							LogUtil.d(key);
							Double v=limits.get(key);
							if(null!=v) {
								tvLimit.setText("参考限值："+String.format("%.3f", v)+" mg/kg");
								CollaurumSpecimenDialog.this.data.setLimit((float)(double)v);
							}else {
								tvLimit.setText("参考限值：");
							}
						}

						String kind=pickerData.getFirstText();
						if(kind.contains("农残")){
							tvReferenceBasis.setText(getString(R.string.reference_basis_)+Params.GB);
						}else if(kind.contains("兽残")){
							tvReferenceBasis.setText(getString(R.string.reference_basis_)+Params.GB_SC);
						}else{
							tvReferenceBasis.setText(R.string.reference_basis_);
						}
						
					}
				});
			}
			projPickerView.showAsDropDown(v, 0, 0);
			break;

		default:
			break;
		}

	}
	Map<String, Double> limits = MyApp.getApp().getLimits();
	private TeaPickerView specimenPickerView,projPickerView;
}
