package com.whswzz.prfluroanalyzer.parameSet;

import java.util.LinkedList;
import java.util.List;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.entity.Organization;
import com.whswzz.prfluroanalyzer.model.HttpModel;
import com.whswzz.prfluroanalyzer.param.Params;
import com.zkzk.pra.R;
import top.jemen.utils.NetUtil;
import com.zkzk.pra.utils.ToastUtil;
import com.zkzk.pra.utils.Tools;

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

public class UsersFragment extends Fragment implements OnClickListener{
	private List<Organization> orgs;
	private View root;
	private EditText etName,etAddr,etContact,etPhone,etOperator,etCode,etToken;
	private Button btAdd,btDelete;
	private Spinner spUsers;
	private ArrayAdapter sourceAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		orgs=MyApp.getApp().getOrganizations();
		if(null==orgs) {
			orgs=new LinkedList<Organization>();
		}
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (Tools.getCurrentVersion(MyApp.getApp()).contains("sak")) {
			root=inflater.inflate(R.layout.fragment_users_sak, null); //主要是
		}else {
			root = inflater.inflate(R.layout.fragment_users, null);
		}

		init();
		setListeners();
		return root;
	}
	private void init() {
		etName=(EditText) root.findViewById(R.id.et_users_org);
		etAddr=(EditText) root.findViewById(R.id.et_users_addr);
		etContact=(EditText) root.findViewById(R.id.et_users_contact);
		etPhone=(EditText) root.findViewById(R.id.et_users_phone);
		etOperator=(EditText) root.findViewById(R.id.et_users_operator);
		etCode=(EditText) root.findViewById(R.id.et_users_code);
		etToken=(EditText) root.findViewById(R.id.et_users_token);
		btAdd=(Button) root.findViewById(R.id.bt_users_add);
		btDelete=(Button) root.findViewById(R.id.bt_users_delete);
		spUsers=(Spinner) root.findViewById(R.id.sp_users_scan);
		
		if(null!=orgs&&orgs.size()>0) {
			 user=orgs.get(0);
			showUser();
		}
		if(null!=orgs) {
			// 建立Adapter并且绑定数据源*****************当需要动态增删时候使用
			sourceAdapter = new ArrayAdapter<>(getActivity(), R.layout.my_spinner, orgs);
	//				new ArrayAdapter<String>(getActivity(), R.layout.my_spinner, sourcesNames);
			sourceAdapter.setDropDownViewResource(R.layout.my_spinner_dropdown);
			// 绑定 Adapter到控件
			spUsers.setAdapter(sourceAdapter);
		}
	}
	private void showUser() {
		if(null==user) {
			return;
		}
		etName.setText(""+user.getName());
		etAddr.setText(""+user.getAddr());
		etContact.setText(""+user.getContact());
		etPhone.setText(""+user.getPhone());
		etOperator.setText(""+user.getOperator());
		etCode.setText(""+user.getCode());
		etToken.setText(""+user.getToken());
	}
	
	private Organization user;;
	private void setListeners() {
		btAdd.setOnClickListener(this);
		btDelete.setOnClickListener(this);
		spUsers.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				user=orgs.get(pos);
				showUser();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});	
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.bt_users_add:
			String org=etName.getText().toString();
			String addr=etAddr.getText().toString();
			String contact=etContact.getText().toString();
			String phone=etPhone.getText().toString();
			String operator=etOperator.getText().toString();
			String code=etCode.getText().toString();
			String token=etToken.getText().toString();
			String version=Tools.getCurrentVersion(MyApp.getApp());;

			if(version.toLowerCase().contains("sak")){
				if (TextUtils.isEmpty(org)) {
					ToastUtil.showText("请填写机构名称", Toast.LENGTH_SHORT);
					return;
				}
				if (TextUtils.isEmpty(code)) {
					ToastUtil.showText("机构编号", Toast.LENGTH_SHORT);
					return;
				}
				if (TextUtils.isEmpty(token)) {
					ToastUtil.showText("请填写用户密钥", Toast.LENGTH_SHORT);
					return;
				}
			}else {
				if (TextUtils.isEmpty(org)) {
					ToastUtil.showText("请填写用户名信息/机构名称", Toast.LENGTH_SHORT);
					return;
				}
//				if (TextUtils.isEmpty(code)) {
//					ToastUtil.showText("请填写用户名代码或快检站编号", Toast.LENGTH_SHORT);
//					return;
//				}
				if (TextUtils.isEmpty(token)) {
					ToastUtil.showText("请填写检测站Token", Toast.LENGTH_SHORT);
					return;
				}
			}
			Params.SAK_KEY=token;
			int i=0;
			Organization u = null;
			for(;i<orgs.size();i++) {
				u=orgs.get(i);
				if(org.equals(u.getName())||token.equals(u.getToken())||code.equals(u.getCode())) {
					u.setName(org);
					u.setAddr(addr);
					u.setCode(code);
					u.setContact(contact);
					u.setOperator(operator);
					u.setPhone(phone);
					u.setToken(token);
					break;
				}
			}
			if(i==orgs.size()) {
				 u=new Organization(org, addr, contact, phone, operator, code, token);
				orgs.add(u);
			}
			MyApp.getApp().saveOrganizations(orgs,new ICallback() {
				@Override
				public void onSuccess(Object obj) {
					ToastUtil.showText("设置用户信息成功", Toast.LENGTH_SHORT);
				}
				
				@Override
				public void onFailed(Object obj) {
					ToastUtil.showText("设置用户信息失败", Toast.LENGTH_SHORT);
					
				}
			});
			if(NetUtil.isConnected()&& Tools.getCurrentVersion(null).contains("AH")) {
				((HttpModel)HttpModel.get()).getSourceAH(u);
			}
			break;
		case R.id.bt_users_delete:
			if(null==user) {
				ToastUtil.showText("没有待删除的用户信息", Toast.LENGTH_SHORT);
				return;
			}
			orgs.remove(user);
			MyApp.getApp().saveOrganizations(orgs, new ICallback() {
				@Override
				public void onSuccess(Object obj) {
					ToastUtil.showText("删除样品来源信息成功", Toast.LENGTH_SHORT);
					sourceAdapter.notifyDataSetChanged();
					if(orgs.size()>0) {
						user=orgs.get(orgs.size()-1);
						showUser();
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
