package com.whswzz.prfluroanalyzer.parameSet;

import java.util.LinkedList;
import java.util.List;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.entity.Source;
import com.zkzk.pra.R;
import com.zkzk.pra.utils.ListUtil;
import com.zkzk.pra.utils.ToastUtil;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import top.jemen.interfaces.ICallback;

public class SourcesFragment extends Fragment implements OnClickListener{
	private List<Source> sources;
	private View root;
	private EditText etOrg,etAddr,etContact,etPhone,etCode,etType;
	private Button btOk,btDelete;
	private Spinner spSources;
	private ArrayAdapter<Source> sourceAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sources=MyApp.getApp().getSources();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root=inflater.inflate(R.layout.fragment_sources, null);
		init();
		setListeners();
		return root;
	}
	private void init() {
		etOrg=(EditText) root.findViewById(R.id.et_source_org);
		etAddr=(EditText) root.findViewById(R.id.et_source_addr);
		etContact=(EditText) root.findViewById(R.id.et_source_contact);
		etPhone=(EditText) root.findViewById(R.id.et_source_phone);
		btOk=(Button)root.findViewById(R.id.bt_source_add);
		btDelete=(Button) root.findViewById(R.id.bt_sources_delete);
		spSources=(Spinner) root.findViewById(R.id.sp_sources_scan);
		etCode=(EditText) root.findViewById(R.id.et_source_org_code);
		etType=(EditText) root.findViewById(R.id.et_source_org_type);
		
		// 建立Adapter并且绑定数据源*****************当需要动态增删时候使用
		sourceAdapter = new ArrayAdapter<>(getActivity(), R.layout.my_spinner, sources);
//				new ArrayAdapter<String>(getActivity(), R.layout.my_spinner, sourcesNames);
		sourceAdapter.setDropDownViewResource(R.layout.my_spinner_dropdown);
		// 绑定 Adapter到控件
		spSources.setAdapter(sourceAdapter);
	}
	private Source source;
	private void setListeners() {
		btOk.setOnClickListener(this);
		btDelete.setOnClickListener(this);
		spSources.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				source=sources.get(pos);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});	
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.bt_source_add:
			String org=etOrg.getText().toString();
			String addr=etAddr.getText().toString();
			String contact=etContact.getText().toString();
			String phone=etPhone.getText().toString();
			String code=etCode.getText().toString();
			String type=etType.getText().toString();
			int t=0;
			if(!TextUtils.isEmpty(type)) {
				t=Integer.parseInt(type);
			}
			if(TextUtils.isEmpty(org)) {
				ToastUtil.showText("请填写样品来源信息", Toast.LENGTH_SHORT);
				return;
			}
			sources.add(new Source(org, addr, contact, phone,code,t));
			MyApp.getApp().saveSources(sources,new ICallback() {
				
				@Override
				public void onSuccess(Object obj) {
					ToastUtil.showText("新增样品来源信息成功", Toast.LENGTH_SHORT);
					sourceAdapter.notifyDataSetChanged();
				}
				
				@Override
				public void onFailed(Object obj) {
					ToastUtil.showText("新增样品来源信息失败", Toast.LENGTH_SHORT);
					
				}
			});
			break;
		case R.id.bt_sources_delete:
			if(null==source) {
				ToastUtil.showText("没有待删除的样品来源信息", Toast.LENGTH_SHORT);
				return;
			}
			sources.remove(source);
			MyApp.getApp().saveSources(sources,new ICallback() {
				@Override
				public void onSuccess(Object obj) {
					ToastUtil.showText("删除样品来源信息成功", Toast.LENGTH_SHORT);
					sourceAdapter.notifyDataSetChanged();
					if(sources.size()>0) {
						source=sources.get(sources.size()-1);
					}
				}
				
				@Override
				public void onFailed(Object obj) {
					ToastUtil.showText("删除样品来源信息失败", Toast.LENGTH_SHORT);
					
				}
			});
			break;
		}
	}
}
