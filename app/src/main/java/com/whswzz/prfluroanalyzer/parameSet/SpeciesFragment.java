package com.whswzz.prfluroanalyzer.parameSet;

import java.util.List;

import com.whswzz.prfluroanalyzer.app.Initer;
import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.entity.Source;
import com.whswzz.prfluroanalyzer.entity.Species;
import com.zkzk.pra.R;
import com.zkzk.pra.utils.ExceptionHandler;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import top.jemen.interfaces.ICallback;

public class SpeciesFragment extends Fragment implements OnClickListener{
	private List<Species> lsSpecies;
	private View root;
	private Spinner spKind;
//	private String kind;
	private List<String> kinds;
	private Button btKind,BtAdd,btKindDelete,btSpeciesDelete;
	private EditText etKind,etSpecies,etAdjust,etProject;
	private ArrayAdapter<String> kindAdapter;
	private TextView tvSpecies;
	Species sp;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		lsSpecies=MyApp.getApp().getLsSpecies();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root=inflater.inflate(R.layout.fragment_species, null);
		init();
		setListeners();
		return root;
	}
	private void init() {
		spKind=(Spinner) root.findViewById(R.id.spinner_species_kind);
		btKind=(Button) root.findViewById(R.id.bt_species_kind_add);
		BtAdd=(Button) root.findViewById(R.id.bt_species_add);
		btKindDelete=(Button) root.findViewById(R.id.bt_kind_delete);
		btSpeciesDelete = (Button) root.findViewById(R.id.bt_species_delete);
		
		etKind=(EditText) root.findViewById(R.id.et_species_kind);
		etSpecies=(EditText) root.findViewById(R.id.et_proj_name);
//		etAdjust=(EditText) root.findViewById(R.id.et_adjust_name);
//		etProject=(EditText) root.findViewById(R.id.et_project_name);
		
		kinds = ListUtil.getRootNames(lsSpecies);
		// 建立Adapter并且绑定数据源*****************当需要动态增删时候使用
		kindAdapter = new ArrayAdapter<String>(getActivity(), R.layout.my_spinner, kinds);
		kindAdapter.setDropDownViewResource(R.layout.my_spinner_dropdown);
		// 绑定 Adapter到控件
		spKind.setAdapter(kindAdapter);
		
		tvSpecies=(TextView) root.findViewById(R.id.tv_projs);
		
	}
	private void setListeners() {
		btKind.setOnClickListener(this);
		BtAdd.setOnClickListener(this);
		btKindDelete.setOnClickListener(this);
		btSpeciesDelete.setOnClickListener(this);
		spKind.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
					String kind = kinds.get(pos);
					 sp = ListUtil.getSpecies(lsSpecies,kind);
					 tvSpecies.setText(ListUtil.listNames(sp.getSubSpecies()));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.bt_species_kind_add:
			String text=etKind.getText().toString();
			if(TextUtils.isEmpty(text)) {
				ToastUtil.showText("请输入待增加的分类名", Toast.LENGTH_SHORT	);
				return;
			}
			text=text.trim();
			for(String s:kinds) {
				if(text.equals(s)) {
					ToastUtil.showText("该分类信息已经存在", Toast.LENGTH_SHORT);
					return;
				}
			}
			
			lsSpecies.add(new Species(text));
			final String s=text;
			MyApp.getApp().saveSpecies(lsSpecies, new ICallback() {
				@Override
				public void onSuccess(Object obj) {
					ToastUtil.showText("添加分类成功", Toast.LENGTH_SHORT);
					kinds.add(s);
					kindAdapter.notifyDataSetChanged();
				}
				
				@Override
				public void onFailed(Object obj) {
					ToastUtil.showText("添加新的分类不成功", Toast.LENGTH_SHORT);
				}
			});
			break;
		case R.id.bt_kind_delete:
			ListUtil.delete(lsSpecies,sp);
			MyApp.getApp().saveSpecies(lsSpecies, new ICallback() {
				@Override
				public void onSuccess(Object obj) {
					ToastUtil.showText("删除分类成功", Toast.LENGTH_SHORT);
					kinds.remove(sp.getName());
					kindAdapter.notifyDataSetChanged();
				}
				
				@Override
				public void onFailed(Object obj) {
					ToastUtil.showText("删除分类不成功", Toast.LENGTH_SHORT);
				}
			});
			
			break;
		case R.id.bt_species_add: //添加样品
			 text=etSpecies.getText().toString();
			if(TextUtils.isEmpty(text)) {
				ToastUtil.showText("请输入待增加的样品名称", Toast.LENGTH_SHORT	);
				return;
			}
			text=text.trim();
			List<Species> sbs = sp.getSubSpecies();
			if(null!=sbs) {
				for(Species subsp:sbs) {
					if(text.equals(subsp.getName())) {
						ToastUtil.showText("该样品信息已经存在", Toast.LENGTH_SHORT);
						return;
					}
				}
			}
			sp.addSubspecies(text);
			 tvSpecies.setText(ListUtil.listNames(sp.getSubSpecies()));
			MyApp.getApp().saveSpecies(lsSpecies,new ICallback() {
				@Override
				public void onSuccess(Object obj) {
					ToastUtil.showText("添加样品成功", Toast.LENGTH_SHORT);
					 tvSpecies.setText(ListUtil.listNames(sp.getSubSpecies()));
				}
				
				@Override
				public void onFailed(Object obj) {
					ToastUtil.showText("添加新的样品不成功", Toast.LENGTH_SHORT);
				}
			});
			break;
			case R.id.bt_species_delete:
				if (null == sp) {
					ToastUtil.showText("请先选择分类", Toast.LENGTH_SHORT);
					return;
				}
				if (null == sp.getSubSpecies() || sp.getSubSpecies().size() == 0) {
					ToastUtil.showText("该分类下没有样品信息", Toast.LENGTH_SHORT);
					return;
				}
				text = etSpecies.getText().toString();
				if (TextUtils.isEmpty(text)) {
					ToastUtil.showText("请输入待增加的样品名称", Toast.LENGTH_SHORT);
					return;
				}
				text = text.trim();
				sbs = sp.getSubSpecies();
				int index = -1;
				//不可用move
				if (null != sbs) {
					for (int i = 0; i < sbs.size(); i++) {
						if (text.equals(sbs.get(i).getName())) {
							index = i;
							break;
						}
					}
				}
				if (null == sbs || index == -1) {
					ToastUtil.showText("该样品信息不存在", Toast.LENGTH_SHORT);
					return;
				}
				sbs.remove(index);
				MyApp.getApp().saveSpecies( new ICallback() {
					@Override
					public void onSuccess(Object obj) {
						ToastUtil.showText("删除样品成功", Toast.LENGTH_SHORT);
						tvSpecies.setText(ListUtil.listNames(sp.getSubSpecies()));
					}

					@Override
					public void onFailed(Object obj) {
						ToastUtil.showText("删除样品不成功", Toast.LENGTH_SHORT);
					}
				});

				break;
//		case R.id.bt_adjust_add: //调整系数
//			text=etAdjust.getText().toString();
//			if(TextUtils.isEmpty(text)) {
//				ToastUtil.showText("请输入待调整的系数", Toast.LENGTH_SHORT);
//				return;
//			}
//			text=text.trim();
//			List<Species> ads = sp.getSubSpecies();
//			if(null!=ads) {
//				for(Species subsp:ads) {
//					if(text.equals(subsp.getName())) {
//						ToastUtil.showText("该系数信息已经存在", Toast.LENGTH_SHORT);
//						return;
//					}
//				}
//			}
//			sp.addSubspecies(text);
//			tvSpecies.setText(ListUtil.listNames(sp.getSubSpecies()));
//			MyApp.getApp().saveSpecies(lsSpecies,new ICallback() {
//				@Override
//				public void onSuccess(Object obj) {
//					ToastUtil.showText("添加系数成功", Toast.LENGTH_SHORT);
//					tvSpecies.setText(ListUtil.listNames(sp.getSubSpecies()));
//				}
//
//				@Override
//				public void onFailed(Object obj) {
//					ToastUtil.showText("添加新的系数不成功", Toast.LENGTH_SHORT);
//				}
//			});
//			break;
//		case R.id.bt_project_add: //添加项目
//			text=etProject.getText().toString();
//			if(TextUtils.isEmpty(text)) {
//				ToastUtil.showText("请输入待添加的项目", Toast.LENGTH_SHORT);
//				return;
//			}
//			text=text.trim();
//			List<Species> pros = sp.getSubSpecies();
//			if(null!=pros) {
//				for(Species subsp:pros) {
//					if(text.equals(subsp.getName())) {
//						ToastUtil.showText("该项目信息已经存在", Toast.LENGTH_SHORT);
//						return;
//					}
//				}
//			}
//			sp.addSubspecies(text);
//			tvSpecies.setText(ListUtil.listNames(sp.getSubSpecies()));
//			MyApp.getApp().saveSpecies(lsSpecies,new ICallback() {
//				@Override
//				public void onSuccess(Object obj) {
//					ToastUtil.showText("添加项目成功", Toast.LENGTH_SHORT);
//					tvSpecies.setText(ListUtil.listNames(sp.getSubSpecies()));
//				}
//
//				@Override
//				public void onFailed(Object obj) {
//					ToastUtil.showText("添加新的项目不成功", Toast.LENGTH_SHORT);
//				}
//			});
//			break;
		}
	}
}
