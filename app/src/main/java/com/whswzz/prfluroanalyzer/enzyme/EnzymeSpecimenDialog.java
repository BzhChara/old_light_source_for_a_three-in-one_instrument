package com.whswzz.prfluroanalyzer.enzyme;

import java.text.SimpleDateFormat;
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
import com.whswzz.prfluroanalyzer.enzyme.entity.EnzymeData;
import com.whswzz.prfluroanalyzer.fluoro.FluoroActivity;
import com.whswzz.prfluroanalyzer.fluoro.entity.FluData;
import com.whswzz.prfluroanalyzer.param.Params;
import com.whswzz.prfluroanalyzer.utils.PrinterJPW;
import com.zkzk.pra.R;
import com.zkzk.pra.activity.DetectActivity;
import com.zkzk.pra.entity.Data;
import com.zkzk.pra.utils.ListUtil;
import com.zkzk.pra.utils.Tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
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

public class EnzymeSpecimenDialog extends BaseDialog implements android.view.View.OnClickListener {
	private TextView  tvChannel, tvResult, tvSn, tvSpecimen,tvSpecimenName, tvSource,tvReferenceBasis,
			tvProj,tvUser;
	private EditText etSn;
	private EditText etSpecimen;
	private Spinner spSource,spUser;//,spResult;
	private Button btSave;
	private MapView mapView;
	private LocationClient locationClient;
	private String oldCode = MyApp.getApp().getPref().getString(Consts.KEY_CITYCODE, "218");

	private EnzymeData data;
	public static final String COLON = "：";

	private Activity context;
	private TextView tvTime;

	private List<Source> sources ;
	private List<Organization> users=MyApp.getApp().getOrganizations();
	private List<Species> species = MyApp.getApp().getLsSpecies();
	protected Source source;
	protected Organization user;
	
	private ICallback callback;
	
	public EnzymeSpecimenDialog(Activity context, EnzymeData data,ICallback callback) {
		super(context, R.style.dialog);
		this.context = context;
		this.data = data;
		this.callback=callback;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.dialog_specimen_enzyme);
		initView();
		initValue();
		
			setListeners();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	private void setListeners() {
		btSave.setOnClickListener(this);
		if (context instanceof EnzymeActivity) { // 样品信息设置null == data.getResult()
			tvSpecimenName.setOnClickListener(this);
		}

	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		tvChannel = (TextView) findViewById(R.id.tv_channel);
		tvProj = (TextView) findViewById(R.id.tv_proj);
		tvResult = (TextView) findViewById(R.id.tv_result);
		etSn = (EditText) findViewById(R.id.et_sn);
		btSave = (Button) findViewById(R.id.bt_save);
		spSource = (Spinner) findViewById(R.id.sp_specimen_source);
		// mapView = (MapView) findViewById(R.id.mapView_dialog);
		// tvLocation = (TextView) findViewById(R.id.tv_location);
		tvTime = (TextView) findViewById(R.id.tv_time);
		tvSn = (TextView) findViewById(R.id.tv_sn);
		tvSpecimen = (TextView) findViewById(R.id.tv_specimen);
		tvSource = (TextView) findViewById(R.id.tv_source);

		
		tvSpecimenName = (TextView) findViewById(R.id.tv_specimen_name);
		etSpecimen=findViewById(R.id.et_specimen);

		spUser=(Spinner) findViewById(R.id.sp_user);
		tvUser=(TextView) findViewById(R.id.tv_user);
		
		
//		spResult=(Spinner) findViewById(R.id.sp_result);
		
		tvReferenceBasis=(TextView) findViewById(R.id.tv_reference_basis);
		tvReferenceBasis.append(Params.GBT);
		
	}
	
	/**
	 * jemen：根据使用场景不同而加载不同的控件
	 */
	private void initValue() {
		tvChannel.setText(getString(R.string.channel_number) + COLON + data.getChannelNum());
		tvProj.setText(getString(R.string.detect_proj) + COLON + data.getProj());
		
		// map = mapView.getMap();
		if (context instanceof EnzymeActivity) { // 样品信息输入,之前使用的null==data.getResult();
			// map.setMyLocationEnabled(true);
			etSn.setVisibility(View.VISIBLE);
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
			if(null!=users) {
				// 建立Adapter并且绑定数据源*****************当需要动态增删时候使用
				ArrayAdapter<Organization> userAdapter = new ArrayAdapter<Organization>(context, R.layout.my_spinner, users);
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
			// 建立Adapter并且绑定数据源*****************当需要动态增删时候使用
			final String[] results= {"阴性","阳性"};
			ArrayAdapter<String> resultAdapter = new ArrayAdapter<String>(context, R.layout.my_spinner, results);
			resultAdapter.setDropDownViewResource(R.layout.my_spinner_dropdown);
			// 绑定 Adapter到控件
//			spResult.setAdapter(resultAdapter);
//			spResult.setOnItemSelectedListener(new OnItemSelectedListener() {
//				@Override
//				public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//					data.setResult(results[pos]);
//				}
//				@Override
//				public void onNothingSelected(AdapterView<?> parent) {
//				}
//			});
			
			
		} else { // 检测结果展示
//			btSave.setVisibility(View.GONE);
			btSave.setText("打印");
			etSn.setVisibility(View.GONE);
			tvSpecimenName.setVisibility(View.GONE);
			spUser.setVisibility(View.GONE);
			spSource.setVisibility(View.GONE);
			
			tvSn.setText(getString(R.string.specimen_sn_) + data.getSn());
			tvSpecimen.setText(getString(R.string.specimen_name_) + data.getSpecimen());
			tvSource.setText(getString(R.string.customer_org_) + data.getSourceUnit());
			tvUser.setText("用户信息："+data.getUserOrg());
			
			
			Calendar c = Calendar.getInstance(Tools.getTimeZone());
			c.setTimeInMillis(data.getTime());
			tvTime.setText(getString(R.string.detect_time) + "：" + DateFormat.format("yyyy-MM-dd k:mm", c));
//			spResult.setVisibility(View.GONE);
			tvResult.setText(getString(R.string.result) + COLON + data.getResult());
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
			if (context instanceof EnzymeActivity) { 
				// MyApplication.getApp().addData(data);
				data.setSn(etSn.getText().toString());
				String specimen=etSpecimen.getText().toString();
				if(TextUtils.isEmpty(specimen)){
					specimen=tvSpecimenName.getText().toString();
				}
				data.setSpecimen(specimen);
				if(null!=source) {
					data.setSourceOrg(source.getUnit());
					data.setSourceAddr(source.getAddr());
					data.setSourceContact(source.getContact());
					data.setSourcePhone(source.getPhone());
					data.setSourceOrgCode(source.getCode());
					data.setSourceOrgType(source.getType());
				}
				if(null!=user) {
					data.setWorkOrg(user.getName());
					data.setUserAddr(user.getAddr());
					data.setUserContact(user.getContact());
					data.setUserPhone(user.getPhone());
					data.setOperator(user.getOperator());
					data.setCode(user.getCode());
					data.setToken(user.getToken());
				}
				data.setTime(System.currentTimeMillis());;
				data.setTemp(Params.TEMP_ENZYME);
				dismiss();
				if(null!=callback) {
					callback.onSuccess(data);
				}
			}else {
				final Button btPrint = (Button) v;
				
				PrinterJPW.showPrintChoice(context,data,btPrint);
			}
				
			
			break;
		case R.id.tv_specimen_name:
			if (species == null) {
				return;
			}
			if (null == teaPickerView) {
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
				teaPickerView = new TeaPickerView((Activity) context, data);
				teaPickerView.setScreenH(3).setDiscolourHook(true).setRadius(25).setContentLine(true).setRadius(25)
						.build();
				// 选择器点击事件
				teaPickerView.setOnPickerClickListener(new OnPickerClickListener() {
					@Override
					public void OnPickerClick(PickerData pickerData) {
						Toast.makeText(context, pickerData.getFirstText() + ":" + pickerData.getSecondText() + ","
								+ pickerData.getThirdText(), Toast.LENGTH_SHORT).show();
						tvSpecimenName.setText(pickerData.getSecondText());
						teaPickerView.dismiss();// 关闭选择器
					}
				});
			}
//			LogUtil.d("teaPicker="+teaPickerView+",  v="+v);
			teaPickerView.showAsDropDown(v, 0, 0);

			break;

		default:
			break;
		}

	}

	

	private TeaPickerView teaPickerView;
}
