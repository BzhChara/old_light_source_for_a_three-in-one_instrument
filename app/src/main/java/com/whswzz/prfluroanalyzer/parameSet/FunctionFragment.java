package com.whswzz.prfluroanalyzer.parameSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.pickerviewlibrary.picker.TeaPickerView;
import com.example.pickerviewlibrary.picker.entity.PickerData;
import com.example.pickerviewlibrary.picker.listener.OnPickerClickListener;
import com.whswzz.prfluroanalyzer.app.Initer;
import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.entity.Species;
import com.whswzz.prfluroanalyzer.photometer.entity.Function;
import com.whswzz.prfluroanalyzer.photometer.entity.PhotometerProj;
import com.zkzk.pra.R;
import com.zkzk.pra.utils.ListUtil;
import com.zkzk.pra.utils.ToastUtil;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import top.jemen.interfaces.ICallback;
import top.jemen.utils.LogUtil;

public class FunctionFragment extends Fragment implements OnClickListener {
    private View root;
    private Button btOk, btAddProj,btDeleteProj;
    private Spinner spProjs, spFunctionType;
    private TextView tvSpecimens;
    private String specimen = "";
    private PhotometerProj proj;
    private LinearLayout llFunction, llkb;
    private ImageButton ibAdd;
    private LayoutInflater inflater;
    private List<Species> lsSpecies = MyApp.getApp().getLsSpecies();
    private EditText etUnit;
    private List<PhotometerProj> projs = MyApp.getApp().getPhotoProjs();
    private EditText etProj;
    private ArrayAdapter<String> projAdapter;
    private List<String> projects;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_function, null);
        this.inflater = inflater;
        init();
        setSpnners();
        setListeners();

        return root;
    }

    private void init() {
        btOk = (Button) root.findViewById(R.id.bt_function_ok);
        tvSpecimens = (TextView) root.findViewById(R.id.tv_function_specimen);
        spProjs = (Spinner) root.findViewById(R.id.sp_function_proj);
        spFunctionType = (Spinner) root.findViewById(R.id.sp_function_type);
        llFunction = (LinearLayout) root.findViewById(R.id.ll_kvs);
        llkb = (LinearLayout) root.findViewById(R.id.ll_kb);
        ibAdd = (ImageButton) root.findViewById(R.id.ib_function_add);
        etUnit = (EditText) root.findViewById(R.id.et_function_unit);
        addItems(2);

        btAddProj = root.findViewById(R.id.bt_add_proj);
        etProj = root.findViewById(R.id.et_proj);
        btDeleteProj=(Button) root.findViewById(R.id.bt_delete_proj);
        projects = ListUtil.getRootNamesProj(projs);
        // 建立Adapter并且绑定数据源*****************当需要动态增删时候使用
        projAdapter = new ArrayAdapter<String>(getActivity(), R.layout.my_spinner, projects);
        projAdapter.setDropDownViewResource(R.layout.my_spinner_dropdown);
//        // 绑定 Adapter到控件
        spProjs.setAdapter(projAdapter);

    }


    private void addItems(int x) {
        for (int i = 0; i < x; i++) {
            addItem();
        }
    }

    private void addItem() {
        View ll = inflater.inflate(R.layout.function_item, null);
        llFunction.addView(ll, llFunction.getChildCount() - 1);
        ll.findViewById(R.id.ib_function_item_delete).setOnClickListener(this);
    }


    private void setSpnners() {
        ArrayAdapter<PhotometerProj> aad = new ArrayAdapter<>(getActivity(), R.layout.my_spinner, projs);
        aad.setDropDownViewResource(R.layout.my_spinner_dropdown);
        spProjs.setAdapter(aad);
        spProjs.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                proj = projs.get(position);
                fillParam(); //函数里边再进行非空判断。
            }
        });
        String[] functiontypes = {"键值对", "K-B值"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(getActivity(), R.layout.my_spinner, functiontypes);
        typeAdapter.setDropDownViewResource(R.layout.my_spinner_dropdown);
        spFunctionType.setAdapter(typeAdapter);
        final View sv = root.findViewById(R.id.sv);
        spFunctionType.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    sv.setVisibility(View.VISIBLE);
                    llkb.setVisibility(View.GONE);
                } else if (position == 1) {
                    llkb.setVisibility(View.VISIBLE);
                    sv.setVisibility(View.GONE);
                }
            }
        });
    }


    protected void fillParam() {
        LogUtil.d("proj:" + proj + ", specimen:" + specimen + ",function:" + proj.getFunction(specimen));

        if (null == proj || proj.getFunction(specimen) == null) {
            llFunction.removeViews(0, llFunction.getChildCount() - 1);
            addItems(2);
            return;
        }


        double[][] ds = proj.getFunction(specimen).getParams();


        for (int i = 0; i < ds[0].length; i++) {
            if (i >= llFunction.getChildCount() - 1) {
                addItem();
            }
            View ll = llFunction.getChildAt(i);
            EditText etk = (EditText) ll.findViewById(R.id.et_function_item_ab);//吸光度
            etk.setText(String.format("%.4f", ds[0][i]));
            EditText etv = (EditText) ll.findViewById(R.id.et_function_item_con);
            etv.setText(String.format("%.4f", ds[1][i]));
        }
        for (int i = llFunction.getChildCount() - 2; i >= ds[0].length; i--) {
            llFunction.removeViewAt(i);
        }

    }

    private void setListeners() {
        btOk.setOnClickListener(this);
        ibAdd.setOnClickListener(this);
        tvSpecimens.setOnClickListener(this);
        btAddProj.setOnClickListener(this);
        btDeleteProj.setOnClickListener(this);
//        spProjs.setOnItemSelectedListener(new OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//                String kind = projects.get(pos);
//                proj = ListUtil.getProjects(projs,kind);
//                tvSpecimens.setText(ListUtil.listNamesProj(proj.getPhotometerProj()));
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_function_add:
                addItem();
                break;
            case R.id.tv_function_specimen:
                if (lsSpecies == null) {
                    return;
                }
                if (null == speciesPickerView) {
                    List<String> kinds = new ArrayList<>();
                    Map<String, List<String>> map = new HashMap<>();
                    for (Species sp : lsSpecies) {
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
                    speciesPickerView = new TeaPickerView(getActivity(), data);
                    speciesPickerView.setScreenH(3).setDiscolourHook(true).setRadius(25).setContentLine(true).setRadius(25)
                            .build();
                    // 选择器点击事件
                    speciesPickerView.setOnPickerClickListener(new OnPickerClickListener() {
                        @Override
                        public void OnPickerClick(PickerData pickerData) {
                            Toast.makeText(getActivity(), pickerData.getFirstText() + ":" + pickerData.getSecondText() + ","
                                    + pickerData.getThirdText(), Toast.LENGTH_SHORT).show();
                            specimen = pickerData.getSecondText();
                            tvSpecimens.setText(specimen);
                            speciesPickerView.dismiss();// 关闭选择器
                            fillParam();
                        }
                    });
                }
                speciesPickerView.showAsDropDown(v, 0, 0);
                break;
            case R.id.bt_function_ok:
                if (null == proj) {
                    ToastUtil.showText("请先选择检测项目", Toast.LENGTH_SHORT);
                    return;
                }
//			if(TextUtils.isEmpty(specimen)) {
//				ToastUtil.showText("请先选择样品类型", Toast.LENGTH_SHORT);
//				return;
//			}
                String unit = etUnit.getText().toString();
                if (TextUtils.isEmpty(unit)) {
                    ToastUtil.showText("请填写浓度单位", Toast.LENGTH_SHORT);
                    return;
                }

                Function f = new Function();
                f.setUnit(unit);
                if (llkb.getVisibility() == View.VISIBLE) {  //输入k b计算
                    EditText etK = (EditText) llkb.findViewById(R.id.et_function_k);
                    EditText etB = (EditText) llkb.findViewById(R.id.et_function_b);
                    String sk = etK.getText().toString();
                    String sb = etB.getText().toString();
                    if (TextUtils.isEmpty(sk) || TextUtils.isEmpty(sb)) {
                        ToastUtil.showText("数据不足请补充", Toast.LENGTH_SHORT);
                        return;
                    }
                    double k = Double.parseDouble(sk);
                    double b = Double.parseDouble(sb);
                    double[][] ds = new double[2][2];
                    ds[0] = new double[]{0, 10};
                    ds[1][0] = 0 * k + b;
                    ds[1][1] = 10 * k + b;
                    f.setParams(ds);
                } else {
                    if (llFunction.getChildCount() < 3) {
                        ToastUtil.showText("数据不足请补充", Toast.LENGTH_SHORT);
                        return;
                    }

                    int count = llFunction.getChildCount();
                    double[][] ds = new double[2][count - 1];
                    for (int i = 0; i < count - 1; i++) {
                        View ll = llFunction.getChildAt(i);
                        EditText etAbs = (EditText) ll.findViewById(R.id.et_function_item_ab);
                        EditText etC = (EditText) ll.findViewById(R.id.et_function_item_con);
                        String abs = etAbs.getText().toString();
                        String c = etC.getText().toString();
                        if (TextUtils.isEmpty(abs) || TextUtils.isEmpty(c)) {
                            ToastUtil.showText("请完整填写参数", Toast.LENGTH_SHORT);
                            return;
                        }
                        ds[0][i] = Double.parseDouble(abs);
                        ds[1][i] = Double.parseDouble(c);
                    }
                    //调整下排序
                    for (int i = ds[0].length - 1; i >= 0; i--) {
                        for (int j = 0; j < i; j++) {
                            if (ds[0][j] > ds[0][j + 1]) {
                                double t = ds[0][j];
                                ds[0][j] = ds[0][j + 1];
                                ds[0][j + 1] = t;
                                t = ds[1][j];
                                ds[1][j] = ds[1][j + 1];
                                ds[1][j + 1] = t;
                            }
                        }
                    }

                    f.setParams(ds);

                }
                LogUtil.d(proj.getName()+"-"+specimen+"  :f="+f);
                proj.addFunction(specimen, f);
                Initer.savePhotometerProj(projs, new ICallback() {
                    @Override
                    public void onSuccess(Object obj) {
                        ToastUtil.showText("保存成功", Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onFailed(Object obj) {
                        ToastUtil.showText("保存失败", Toast.LENGTH_SHORT);
                    }
                });

                break;
            case R.id.ib_function_item_delete:
                llFunction.removeView((View) v.getParent());
                break;
            case R.id.bt_add_proj:
				String proj1=etProj.getText().toString();
				if(TextUtils.isEmpty(proj1)){
					ToastUtil.showText("请输入项目名称", Toast.LENGTH_SHORT);
					return;
				}
                proj1=proj1.trim();
                if (null!=projs){
                    for(PhotometerProj p:projs){
                        if(proj1.equals(p.getName())){
                            ToastUtil.showText("项目名称重复", Toast.LENGTH_SHORT);
                            return;
                        }
                    }
                }

				PhotometerProj p=new PhotometerProj(proj1);
				projs.add(p);
				Initer.savePhotometerProj(projs, new ICallback() {
					@Override
					public void onSuccess(Object obj) {
						ToastUtil.showText("保存成功", Toast.LENGTH_SHORT);
						spProjs.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.my_spinner, projs));
					}

					@Override
					public void onFailed(Object obj) {
						ToastUtil.showText("保存不成功", Toast.LENGTH_SHORT);
					}
				});
                break;
            case R.id.bt_delete_proj:
                ListUtil.deleteProj(projs,proj);
                MyApp.getApp().saveProjects(projs, new ICallback() {
                    @Override
                    public void onSuccess(Object obj) {
                        ToastUtil.showText("删除项目成功", Toast.LENGTH_SHORT);
                        projects.remove(proj.getName());
                        projAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailed(Object obj) {
                        ToastUtil.showText("删除项目不成功", Toast.LENGTH_SHORT);
                    }
                });

                break;
        }
    }

    private TeaPickerView teaPickerView, speciesPickerView;
}
