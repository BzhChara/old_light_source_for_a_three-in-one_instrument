package com.whswzz.prfluroanalyzer.settings.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.whswzz.prfluroanalyzer.param.Params;
import com.zkzk.pra.R;
import com.zkzk.pra.utils.ToastUtil;



public class SysParamFragment extends Fragment implements OnClickListener, AdapterView.OnItemSelectedListener {
    private EditText etPhotometerK,etPhotometerB;
    private Button btLite;
    private View root;
    private Spinner spChannel, spLite;
    private Switch swShowAbnormal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_sysparam, null);
        init();
        setListeners();
        return root;
    }

    private void init() {
        spChannel = (Spinner) root.findViewById(R.id.sp_channel);
        String[] channels = new String[25];
        for (int i = 0; i < channels.length; i++) {
            channels[i] = "通道 " + (i + 1);
        }
        channels[24]="全部";
        ArrayAdapter<String> channelAdapter = new ArrayAdapter<String>(getActivity(), R.layout.my_spinner, channels);
        channelAdapter.setDropDownViewResource(R.layout.my_spinner_dropdown);
        spChannel.setAdapter(channelAdapter);
        spChannel.setSelection(0, true);

//        etLite = (EditText) root.findViewById(R.id.et_lite);
        etPhotometerK= (EditText) root.findViewById(R.id.et_photometer_k);
        etPhotometerB= (EditText) root.findViewById(R.id.et_photometer_b);

        btLite = (Button) root.findViewById(R.id.bt_photometer);
        spLite = (Spinner) root.findViewById(R.id.sp_lite);
        String[] lights = new String[]{"410nm", "460nm", "520nm", "550nm", "590nm", "630nm","全部"};
        ArrayAdapter<String> liteAdapter = new ArrayAdapter<String>(getActivity(), R.layout.my_spinner, lights);
        liteAdapter.setDropDownViewResource(R.layout.my_spinner_dropdown);
        spLite.setAdapter(liteAdapter);
        spLite.setSelection(0, true);

        swShowAbnormal=root.findViewById(R.id.sw_show_abnormal);
        swShowAbnormal.setChecked(Params.showAbnormal());

    }


    private void setListeners() {
        btLite.setOnClickListener(this);
        spChannel.setOnItemSelectedListener(this);
        spLite.setOnItemSelectedListener(this);
        swShowAbnormal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Params.setShowAbnormal(isChecked);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_photometer:
                float k,b;
                try {
                    k = Float.parseFloat(etPhotometerK.getText().toString());
                    b = Float.parseFloat(etPhotometerB.getText().toString());
                } catch (Exception e) {
                    ToastUtil.showText("请输入正确的参数值", Toast.LENGTH_SHORT);
                    return;
                }
                Params.savePhotometer(spChannel.getSelectedItemPosition(), spLite.getSelectedItemPosition(), k, b);
                ToastUtil.showText("参数设置成功", Toast.LENGTH_SHORT);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.sp_lite:
            case R.id.sp_channel:
                if (spChannel.getSelectedItem().toString().equals("全部") || spLite.getSelectedItem().toString().equals("全部")) {
                    etPhotometerK.setText( "");
                    etPhotometerB.setText( "");

                }else {
                    float[] kb = Params.getPhotometer(spChannel.getSelectedItemPosition(), spLite.getSelectedItemPosition());
                    etPhotometerK.setText(kb[0] + "");
                    etPhotometerB.setText(kb[1] + "");
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
