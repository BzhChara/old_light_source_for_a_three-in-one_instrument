package com.whswzz.prfluroanalyzer.parameSet;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import top.jemen.interfaces.ACallback;
import top.jemen.interfaces.ICallback;

public class LimitsFragment extends Fragment implements OnClickListener {
    private TextView tvProj, tvType;
    private EditText etLimit;
    private Button btLimit;
    private View root;
    private Map<String, Double> limits;


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
        btLimit = (Button) root.findViewById(R.id.bt_limits);

        limits = MyApp.getApp().getLimits();
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
                limits.put(proj + "-" + type, d);
                MyApp.getApp().saveLimits(limits, new ICallback() {
                    @Override
                    public void onSuccess(Object obj) {
                        ToastUtil.showText("保存成功", Toast.LENGTH_SHORT);
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
        if (TextUtils.isEmpty(proj) || TextUtils.isEmpty(type) || limits.get(proj + "-" + type) == null) {
            return;
        }
        etLimit.setText(String.format("%.3f", limits.get(proj + "-" + type)));
    }


}
