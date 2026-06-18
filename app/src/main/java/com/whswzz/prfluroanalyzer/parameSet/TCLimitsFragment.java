package com.whswzz.prfluroanalyzer.parameSet;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.utils.UiUtil;
import com.zkzk.pra.R;
import com.zkzk.pra.databinding.FragmentTcLimitsBinding;
import com.zkzk.pra.utils.ToastUtil;

import java.util.Map;

import top.jemen.interfaces.ACallback;
import top.jemen.interfaces.ICallback;
import com.whswzz.prfluroanalyzer.param.Params;

public class TCLimitsFragment extends Fragment implements OnClickListener {
    private TextView tvProj, tvType;
    private EditText etLimit;
    private Button btLimit;
    private View root;
    private Map<String, String> limits;
    private Spinner spSymble;
    private FragmentTcLimitsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding=FragmentTcLimitsBinding.inflate(inflater,container,false);
        root=binding.getRoot();
//        root = inflater.inflate(R.layout.fragment_tc_limits, null);
        init();
        setListeners();
        ((TextView)root.findViewById(R.id.tv_title)).setText("T/C判定限值设定");
        return root;
    }

    private void init() {
        tvProj = (TextView) root.findViewById(R.id.tv_limits_proj);
        tvType = (TextView) root.findViewById(R.id.tv_limits_specimen);
        etLimit = (EditText) root.findViewById(R.id.et_limits_value);
        btLimit = (Button) root.findViewById(R.id.bt_limits);
        spSymble=root.findViewById(R.id.sp_tc_limit);

        limits = MyApp.getApp().getTCLimits();

        ArrayAdapter<String> aad = new ArrayAdapter<>(getActivity(), R.layout.my_spinner, symbles);
        aad.setDropDownViewResource(R.layout.my_spinner_dropdown);
        spSymble.setAdapter(aad);

    }

    private void setListeners() {
        tvProj.setOnClickListener(this);
        tvType.setOnClickListener(this);
        btLimit.setOnClickListener(this);
        binding.btOkKb.setOnClickListener(this);

    }

    private void setSpinner(){

    }
    private  String[] symbles = { "  >  ", "  <= " }; //总长5个字符不可更改

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_limits_proj:

                UiUtil.show2Pickview((TextView) v, MyApp.getApp().getLsProjs(), new ACallback() {
                    @Override
                    public void onSuccess(Object obj) {
                        showLimit();
                    }
                });
                break;
            case R.id.tv_limits_specimen:
                UiUtil.show2Pickview((TextView) v, MyApp.getApp().getLsSpecies(), new ACallback() {
                    @Override
                    public void onSuccess(Object obj) {
                        showLimit();
                    }
                });
                break;
            case R.id.bt_limits:
                String proj = tvProj.getText().toString();
                String type = tvType.getText().toString();
                if (TextUtils.isEmpty(proj) || TextUtils.isEmpty(type)) {
                    ToastUtil.showText("请先选择检测项目和样品名称", Toast.LENGTH_SHORT);
                    return;
                }
                String limit = etLimit.getText().toString();
                if (TextUtils.isEmpty(limit)) {
                    ToastUtil.showText("请输入参考限值", Toast.LENGTH_SHORT);
                    return;
                }
                double d = -1;
                try {
                    d = Double.parseDouble(limit);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (d < 0 || d > 1000) {
                    ToastUtil.showText("请输入合理的参考限值", Toast.LENGTH_SHORT);
                    return;
                }
                String symble= (String) spSymble.getSelectedItem();
                limits.put(proj + "-" + type, symble+d);
                MyApp.getApp().saveTCLimits(limits, new ICallback() {
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
            case R.id.bt_ok_kb:
                String sk=binding.etK.getText().toString();
                String sb=binding.etB.getText().toString();
                if(TextUtils.isEmpty(sk)||TextUtils.isEmpty(sb)){
                    ToastUtil.showText("请输入校准系数",Toast.LENGTH_SHORT);
                    return;
                }
                float k,b;
                try{
                    k=Float.parseFloat(sk);
                    b=Float.parseFloat(sb);
                }catch (Exception e){
                    ToastUtil.showText("请输入正确的系数",Toast.LENGTH_SHORT);
                    return;
                }
                Params.setK(k);
                Params.setB(b);
                ToastUtil.showText("设置成功",Toast.LENGTH_LONG);

                break;
        }
    }

    private void showLimit() {
        String proj = tvProj.getText().toString();
        String type = tvType.getText().toString();
        if (TextUtils.isEmpty(proj) || TextUtils.isEmpty(type) ) {
            return;
        }
        String v= limits.get(proj + "-" + type);
        if(null==v){
            spSymble.setSelection(0);
            etLimit.setText("1");
        }else{
            if(v.contains(symbles[0])){
                spSymble.setSelection(0);
            }else{
                spSymble.setSelection(1);
            }
            if(v.length()>5) {
                etLimit.setText("" + v.substring(5));
            }else{{
                etLimit.setText("1");
            }}
        }
    }


}
