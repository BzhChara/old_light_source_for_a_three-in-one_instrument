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

public class ProjsFragment extends Fragment implements OnClickListener{
	private List<Species> lsProjs;
	private View root;
	private Spinner spKind;
//	private String kind;
	private List<String> kinds;
	private Button btKind,BtAdd,btKindDelete,btProjDelete,EtAdd,ProAdd;
	private EditText etKind,etSpecies,etAdjust,etProject;
	private ArrayAdapter<String> kindAdapter;
	private TextView tvSpecies;
	Species sp;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		lsProjs=MyApp.getApp().getLsProjs();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root=inflater.inflate(R.layout.fragment_projs, null);
		init();
		setListeners();
		return root;
	}
	private void init() {
		etKind=(EditText) root.findViewById(R.id.et_projs_kind);
		btKindDelete=(Button) root.findViewById(R.id.bt_proj_kind_delete);
		
		spKind=(Spinner) root.findViewById(R.id.spinner_projs_kind);
		btKind=(Button) root.findViewById(R.id.bt_projs_kind_add);
		BtAdd=(Button) root.findViewById(R.id.bt_projs_add);
		btProjDelete = root.findViewById(R.id.bt_projs_delete);
		EtAdd=(Button) root.findViewById(R.id.bt_adjust_add);
		ProAdd=(Button) root.findViewById(R.id.bt_project_add);
		
		
		etSpecies=(EditText) root.findViewById(R.id.et_proj_name);
		etAdjust=(EditText) root.findViewById(R.id.et_adjust_name);
		etProject=(EditText) root.findViewById(R.id.et_project_name);

		
		kinds = ListUtil.getRootNames(lsProjs);
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
		btProjDelete.setOnClickListener(this);
		EtAdd.setOnClickListener(this);
		ProAdd.setOnClickListener(this);
		btKindDelete.setOnClickListener(this);
		
		spKind.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
					String kind = kinds.get(pos);
					 sp = ListUtil.getSpecies(lsProjs,kind);
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
		case R.id.bt_projs_kind_add:
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
			
			lsProjs.add(new Species(text));
			final String s=text;
			MyApp.getApp().saveProjs(lsProjs, new ICallback() {
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
		case R.id.bt_proj_kind_delete:
			ListUtil.delete(lsProjs,sp);
			MyApp.getApp().saveProjs(lsProjs, new ICallback() {
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
		case R.id.bt_projs_add: //添加样品//检测项目
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
			MyApp.getApp().saveProjs(lsProjs,new ICallback() {
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
			case R.id.bt_projs_delete: //删除样品
				if (null == sp) {
					ToastUtil.showText("请先选择分类", Toast.LENGTH_SHORT);
					return;
				}
				text = etSpecies.getText().toString();
				if (TextUtils.isEmpty(text)) {
					ToastUtil.showText("请输入待删除的项目名称", Toast.LENGTH_SHORT);
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
					ToastUtil.showText("该检测项目信息不存在", Toast.LENGTH_SHORT);
					return;
				}
				sbs.remove(index);
				MyApp.getApp().saveProjs( new ICallback() {
					@Override
					public void onSuccess(Object obj) {
						ToastUtil.showText("删除检测项目成功", Toast.LENGTH_SHORT);
						tvSpecies.setText(ListUtil.listNames(sp.getSubSpecies()));
					}

					@Override
					public void onFailed(Object obj) {
						ToastUtil.showText("删除检测项目不成功", Toast.LENGTH_SHORT);
					}
				});
				break;
		case R.id.bt_adjust_add: //调整系数
			text=etAdjust.getText().toString();
			if(TextUtils.isEmpty(text)) {
				ToastUtil.showText("请输入需要调整的系数", Toast.LENGTH_SHORT);
				return;
			}
			text=text.trim();
			List<Species> ads = sp.getSubSpecies();
			if(null!=ads) {
				for(Species subsp:ads) {
					if(text.equals(subsp.getName())) {
						ToastUtil.showText("该系数已经存在", Toast.LENGTH_SHORT);
						return;
					}
				}
			}
			sp.addSubspecies(text);
			tvSpecies.setText(ListUtil.listNames(sp.getSubSpecies()));
			MyApp.getApp().saveProjs(lsProjs,new ICallback() {
				@Override
				public void onSuccess(Object obj) {
					ToastUtil.showText("添加系数成功", Toast.LENGTH_SHORT);
					tvSpecies.setText(ListUtil.listNames(sp.getSubSpecies()));
				}

				@Override
				public void onFailed(Object obj) {
					ToastUtil.showText("添加新的系数不成功", Toast.LENGTH_SHORT);
				}
			});
			break;
		case R.id.bt_project_add: //添加项目
			text=etProject.getText().toString();
			if(TextUtils.isEmpty(text)) {
				ToastUtil.showText("请输入需要新增的项目", Toast.LENGTH_SHORT);
				return;
			}
			text=text.trim();
			List<Species> proj = sp.getSubSpecies();
			if(null!=proj) {
				for(Species subsp:proj) {
					if(text.equals(subsp.getName())) {
						ToastUtil.showText("该项目已经存在", Toast.LENGTH_SHORT);
						return;
					}
				}
			}
			sp.addSubspecies(text);
			tvSpecies.setText(ListUtil.listNames(sp.getSubSpecies()));
			MyApp.getApp().saveProjs(lsProjs,new ICallback() {
				@Override
				public void onSuccess(Object obj) {
					ToastUtil.showText("新增项目成功", Toast.LENGTH_SHORT);
					tvSpecies.setText(ListUtil.listNames(sp.getSubSpecies()));
				}

				@Override
				public void onFailed(Object obj) {
					ToastUtil.showText("添加新的项目不成功", Toast.LENGTH_SHORT);
				}
			});
			break;
		}
	}
}
