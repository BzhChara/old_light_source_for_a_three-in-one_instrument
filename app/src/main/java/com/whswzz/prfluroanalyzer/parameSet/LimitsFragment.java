package com.whswzz.prfluroanalyzer.parameSet;

import java.util.HashMap;
import java.util.Map;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.utils.LimitUnitUtil;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import top.jemen.interfaces.ACallback;
import top.jemen.interfaces.ICallback;

public class LimitsFragment extends Fragment implements OnClickListener, OnItemSelectedListener {
    private TextView tvProj, tvType;
    private EditText etLimit;
    private Spinner spUnit;
    private Button btLimit;
    private View root;
    private Map<String, Double> limits;
    private Map<String, String> limitUnits;
    private String currentUnit = LimitUnitUtil.UNIT_MG_KG;
    private boolean ignoreUnitChange;


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

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(getActivity(), R.layout.my_spinner, LimitUnitUtil.LIMIT_UNITS);
        unitAdapter.setDropDownViewResource(R.layout.my_spinner_dropdown);
        spUnit.setAdapter(unitAdapter);
        spUnit.setOnItemSelectedListener(this);
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
                final String key = proj + "-" + type;
                final String unit = spUnit.getSelectedItem().toString();
                final double storageValue = LimitUnitUtil.toMgKg(d, unit);
                if (storageValue < 0 || storageValue > 1000) {
                    ToastUtil.showText("请输入合理的参考限值", Toast.LENGTH_SHORT);
                    return;
                }
                limits.put(key, storageValue);
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
        String unit = LimitUnitUtil.normalizeUnit(limitUnits.get(key));
        setLimitUnit(unit);
        etLimit.setText(LimitUnitUtil.formatValue(limitValue, unit));
    }

    private void setLimitUnit(String unit) {
        unit = LimitUnitUtil.normalizeUnit(unit);
        currentUnit = unit;
        for (int i = 0; i < LimitUnitUtil.LIMIT_UNITS.length; i++) {
            if (LimitUnitUtil.LIMIT_UNITS[i].equals(unit)) {
                ignoreUnitChange = true;
                spUnit.setSelection(i);
                ignoreUnitChange = false;
                return;
            }
        }
        ignoreUnitChange = true;
        spUnit.setSelection(0);
        ignoreUnitChange = false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedUnit = LimitUnitUtil.normalizeUnit(parent.getItemAtPosition(position).toString());
        if (ignoreUnitChange || selectedUnit.equals(currentUnit)) {
            currentUnit = selectedUnit;
            return;
        }
        convertInputValue(currentUnit, selectedUnit);
        currentUnit = selectedUnit;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void convertInputValue(String oldUnit, String newUnit) {
        String limit = etLimit.getText().toString();
        if (TextUtils.isEmpty(limit)) {
            return;
        }
        try {
            double displayValue = Double.parseDouble(limit);
            double storageValue = LimitUnitUtil.toMgKg(displayValue, oldUnit);
            etLimit.setText(LimitUnitUtil.formatValue(storageValue, newUnit));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
