package com.whswzz.prfluroanalyzer.parameSet;

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
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.entity.Species;
import com.whswzz.prfluroanalyzer.param.Params;
import com.whswzz.prfluroanalyzer.utils.UiUtil;
import com.zkzk.pra.R;
import com.zkzk.pra.utils.ListUtil;
import com.zkzk.pra.utils.ToastUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import top.jemen.interfaces.ICallback;


public class GBFragment extends Fragment implements OnClickListener {
    //    private TextView tvProj;
    private Spinner spProj;
    private EditText etGB;
    private Button btOK;
    private View root;
    private Map<String, String> gbMap;
    private ImageButton ib;
    private List<Species> lsProjs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_gb, null);
        lsProjs = MyApp.getApp().getLsProjs();
        init();
        setListeners();
        return root;
    }

    private void init() {
//        tvProj = (TextView) root.findViewById(R.id.sp_proj);
        spProj = (Spinner) root.findViewById(R.id.sp_proj);
        etGB = (EditText) root.findViewById(R.id.et_gb);
        btOK = (Button) root.findViewById(R.id.bt_ok);
        ib=root.findViewById(R.id.ib_gb);
        gbMap = MyApp.getApp().getGBMap();
        List<String> kinds = ListUtil.getRootNames(lsProjs);
        // 建立Adapter并且绑定数据源*****************当需要动态增删时候使用
        ArrayAdapter<String> kindAdapter = new ArrayAdapter<String>(getActivity(), R.layout.my_spinner, kinds);
        kindAdapter.setDropDownViewResource(R.layout.my_spinner_dropdown);
        // 绑定 Adapter到控件
        spProj.setAdapter(kindAdapter);
    }


    private void setListeners() {
//        tvProj.setOnClickListener(this);
        btOK.setOnClickListener(this);
        ib.setOnClickListener(this);

        spProj.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                project=lsProjs.get(position);
                showGB();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private String[] gbs = {Params.GB, Params.GB_SC}; //总长5个字符不可更改
    private Species project;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_ok:
                if(null==project){
                    ToastUtil.showText("请先选择项目", Toast.LENGTH_SHORT);
                    return;
                }

                String gb = etGB.getText().toString();

                if (gb==null|| TextUtils.isEmpty(gb)) {
                    ToastUtil.showText("请先填写参考标准", Toast.LENGTH_SHORT);
                    return;
                }
                gbMap.put(project.getName(), gb);
                MyApp.getApp().saveGBMap( new ICallback() {
                    @Override
                    public void onSuccess(Object obj) {
                        ToastUtil.showText("保存成功", Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onFailed(Object obj) {
                        ToastUtil.showText("保存不成功", Toast.LENGTH_SHORT);
                    }
                });
                break;
            case R.id.ib_gb:
                UiUtil.show1Pickview(etGB, Arrays.asList(Params.GB, Params.GB_SC, Params.GB_SC2,"农业部235公告"),null);
                break;
        }
    }

    private void showGB() {
        if (gbMap.containsKey(project.getName())) {
            etGB.setText(gbMap.get(project.getName()));
        } else {
            etGB.setText("");
        }

    }

}
