package com.whswzz.prfluroanalyzer.parameSet;

import java.util.HashMap;
import java.util.Map;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.utils.UiUtil;
import com.zkzk.pra.R;
import com.zkzk.pra.utils.ToastUtil;

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

import top.jemen.interfaces.ACallback;
import top.jemen.interfaces.ICallback;

public class LimitsFragment extends Fragment implements OnClickListener {
    private static final String UNIT_MG_KG = "mg/kg(ppm)";
    private static final String UNIT_UG_KG = "ug/kg(ppb)";
    private static final String[] LIMIT_UNITS = {UNIT_MG_KG, UNIT_UG_KG};
    private TextView tvProj, tvType;
    private EditText etLimit;
    private Spinner spUnit;
    private Button btLimit;
    private View root;
    private Map<String, Double> limits;
    private Map<String, String> limitUnits;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_limits, null);
        init();
        setListeners();
        return root;
    }

    private void init() {
        tvProj = (TextView) root.findViewById(R.id.tv_limits_proj);
        tvType = (TextView) root.findViewById(R.id.tv_limits_specimen);
        etLimit = (EditText) root.findViewById(R.id.et_limits_value);
        spUnit = (Spinner) root.findViewById(R.id.sp_limits_unit);
        btLimit = (Button) root.findViewById(R.id.bt_limits);

        limits = MyApp.getApp().getLimits();
        if (null == limits) {
            limits = new HashMap<>();
        }
        limitUnits = MyApp.getApp().getLimitUnits();

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(getActivity(), R.layout.my_spinner, LIMIT_UNITS);
        unitAdapter.setDropDownViewResource(R.layout.my_spinner_dropdown);
        spUnit.setAdapter(unitAdapter);
    }

    private void setListeners() {
        tvProj.setOnClickListener(this);
        tvType.setOnClickListener(this);
        btLimit.setOnClickListener(this);
    }

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
                final String key = proj + "-" + type;
                final String unit = spUnit.getSelectedItem().toString();
                limits.put(key, d);
                limitUnits.put(key, unit);
                MyApp.getApp().saveLimits(limits, new ICallback() {
                    @Override
                    public void onSuccess(Object obj) {
                        MyApp.getApp().saveLimitUnits(limitUnits, new ICallback() {
                            @Override
                            public void onSuccess(Object obj) {
                                ToastUtil.showText("保存成功", Toast.LENGTH_SHORT);
                            }

                            @Override
                            public void onFailed(Object obj) {
                                ToastUtil.showText("单位保存不成功", Toast.LENGTH_SHORT);
                            }
                        });
                    }

                    @Override
                    public void onFailed(Object obj) {
                        ToastUtil.showText("设置不成功", Toast.LENGTH_SHORT);
                    }
                });
                break;
        }
    }

    private void showLimit() {
        String proj = tvProj.getText().toString();
        String type = tvType.getText().toString();
        if (TextUtils.isEmpty(proj) || TextUtils.isEmpty(type)) {
            return;
        }
        String key = proj + "-" + type;
        Double limitValue = limits.get(key);
        if (null == limitValue) {
            etLimit.setText("");
            setLimitUnit(null);
            return;
        }
        etLimit.setText(String.format("%.3f", limitValue));
        setLimitUnit(limitUnits.get(key));
    }

    private void setLimitUnit(String unit) {
        if (TextUtils.isEmpty(unit)) {
            unit = UNIT_MG_KG;
        }
        for (int i = 0; i < LIMIT_UNITS.length; i++) {
            if (LIMIT_UNITS[i].equals(unit)) {
                spUnit.setSelection(i);
                return;
            }
        }
        spUnit.setSelection(0);
    }


}
