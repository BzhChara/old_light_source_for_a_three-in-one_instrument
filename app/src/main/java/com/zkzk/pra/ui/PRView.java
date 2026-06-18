package com.zkzk.pra.ui;

import java.util.Arrays;
import java.util.List;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.consts.Consts;
import com.whswzz.prfluroanalyzer.entity.Organization;
import com.whswzz.prfluroanalyzer.param.Params;
import com.whswzz.prfluroanalyzer.settings.SetupActivity;
import com.zkzk.pra.R;
import com.zkzk.pra.activity.DetectActivity;
import com.zkzk.pra.entity.Data;
import com.zkzk.pra.entity.Location;
import com.zkzk.pra.entity.Project;
import com.zkzk.pra.utils.ExceptionHandler;
import com.zkzk.pra.utils.ToastUtil;

//import android.R.styleable;
import android.app.Dialog;
//import android.app.ResultInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.CursorJoiner.Result;
//import android.filterfw.core.StopWatchMap;
//import android.os.Broadcaster;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import top.jemen.utils.LogUtil;

import android.widget.Toast;

import android.view.View.OnClickListener;

public class PRView extends LinearLayout implements OnClickListener {
    public static final byte MODE_CONSTRUCT = 0;
    public static final byte MODE_DETECT = 1;
    protected static final int Data = 0;
    private Context context;
    private TextView tvAbsorbance, tvInhibition, tvResult, tvNum;
    private CheckBox cbUse;
    private String channel = "";
    private Data data;
    private View root;

    private float initialV = 0, finalV;// 开始检测第10s的吸光度与第310s的吸光度；
    private float da0 = 0.618f, dat;// 分别用以记录对照溶液的吸光度变化值和样品溶液的吸光度变化值
    private byte mode; //
    private float inhibition;    //抑制率
    //	private List<Project> projs;
    private Project proj;

    public PRView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        // 初始化界面
        try {
            root = LayoutInflater.from(context).inflate(R.layout.view_pr, this);
            // 自定义属性
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PRView);
            channel = typedArray.getString(R.styleable.PRView_channel_num);
            // 初始化函数
            init(context);
            setListeners();
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
    }

    private void init(final Context context) {
        tvAbsorbance = (TextView) findViewById(R.id.tv_od);
        tvInhibition = (TextView) findViewById(R.id.tv_inhibition_ratio);
        tvResult = (TextView) findViewById(R.id.tv_result);
        tvNum = (TextView) findViewById(R.id.tv_channel_number);
        cbUse = (CheckBox) findViewById(R.id.cb_use);
        tvNum.setText(channel);
        data = new Data();    //都在数据库保存，因为没有使用内存存储，所以一直使用同一个对象。
        data.setChannel(channel);
        SharedPreferences pref = MyApp.getApp().getPref();


        String operator = pref.getString(Consts.KEY_OPERATOR, null);
        data.setLocation(MyApp.getApp().getLocation());

//		projs = MyApplication.getApp().getProjs();
//		data.setProj(projs.get(0).getProj());
//		String[] types = getResources().getStringArray(R.array.type);
//		data.setSpecimen(types[0]);	//样品类型用户自己输入吧。
//		data.setLimit("0-50%");//初始的状态，设置proj后更新。
        List<Organization> users = MyApp.getApp().getOrganizations();
        if (null != users && users.size() > 0) {
            int i = pref.getInt(Consts.USER_INDEX, 0);
            data.setUser(users.get(i));
        }

    }

    public void setProj(Project proj) {
        this.proj = proj;
        data.setProj(proj.getProj());
        da0 = proj.getContrast();
        data.setLimit("0-" + String.format("%.1f", proj.getLimit() * 100) + "%");
    }

    public void destroy() {
        if (specimenDialog != null) {
            specimenDialog.cancel();
        }
    }

    private SpecimenDialog specimenDialog;

    private void setListeners() {
        root.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == specimenDialog) {
                    specimenDialog = new SpecimenDialog(context, data);
                } else {
                    specimenDialog.renewProj();
                }
                specimenDialog.show();
            }
        });

        cbUse.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LogUtil.d("check changed: " + isChecked);
                if (!isChecked) {
                    data.setChecked(false);
                    reset();
                } else {
                    data.setChecked(true);    //便于检测见面选择打印
                }
            }
        });
        tvNum.setOnClickListener(this);
        tvResult.setOnClickListener(this);
    }

    /**
     * 清空界面显示，以及预存的吸光度、da0等。
     * 与clear不同，clear将会也清空样品编号、样品名称、被检单位等。
     */
    public void reset() {
        // data=new Data();//没检测一次完了之后可重新创建对象，如果不做内存查询的话也可以不。
        resetData();
        tvAbsorbance.setText("");
        tvInhibition.setText("");
        tvResult.setText("");
    }

    /**
     * 清空界面显示、所有数据，包括样品编号、名称及送检单位等。
     */
    public void clear() {
        data.setAbsorbancy(0);
        data.setInhibitionRatio(0);
        data.setResult(null);
        data.setSn(null);
        data.setSpecimen(null);
        data.setSource(null);

        finalV = 0;
        dat = 0;
        tvAbsorbance.setText("");
        tvInhibition.setText("");
        tvResult.setText("");
    }

    private void resetData() {
        initialV = 0;
        finalV = 0;
        dat = 0;
        data.setAbsorbancy(0);
        data.setInhibitionRatio(0);
        data.setResult(null);
    }

    public String getTvAbsorbance() {
        return tvAbsorbance.getText().toString();
    }

    float[] buf = new float[3];
    int x = 0;

    /**
     * 设置吸光度
     *
     * @param num        采集数据的序号
     * @param absorbance 吸光度值
     */
    public void setAbsorbance(int num, float absorbance) {
        if (num > 11) { // 是否进行  不知板子还是模块问题，有异常值。此处过滤掉》
            if (!cbUse.isChecked())
                return;
        }
        if (!Params.showAbnormal()&&(absorbance < 0 || absorbance > 10) ){
            return;
        }

        buf[x % 3] = absorbance;
        x++;

//		if (absorbance < 0)
//			absorbance *= -1;

        String strAbsobancy = String.format("%.3f", absorbance);
        tvAbsorbance.setText(strAbsobancy);

        if (num == 10 || num == 11) {
            if (buf[0] > 0.02 && buf[1] > 0.02 && buf[2] > 0.02) {    //自动选定,这个度量需要慎重。
                cbUse.setChecked(true);
            } else if (!cbUse.isChecked()) {    //清空未选定通道的数据。
                setCbUse(false);
                reset();
            }
        } else if (num > 100) {
            finalV = (buf[0] + buf[1] + buf[2]) / 3;
            switch (mode) {
                case MODE_CONSTRUCT:
                    da0 = finalV - initialV; // 正常测试中模块出来就是相对值。用于当前模块可以直接用finalV,initV为0
                    LogUtil.d("da0=" + da0);
                    break;
                case MODE_DETECT:
                    dat = finalV - initialV;
                    if (da0 == 0)
                        break;
                    inhibition = (da0 - dat) / da0; // 计算抑制率,农药浓度越高，吸光度变化越小。

//				tvInhibition.setText(String.format("%.1f", inhibition*100)+"%");
                    if (inhibition > Consts.ALL) {
                        tvInhibition.setText("100%");
                    } else {
                        if (inhibition <=0) {
                            inhibition = (float) (0.1*Math.random());// (float) (0.09 * Math.random());
                        }
//                        tvInhibition.setText(String.format("%.2f", (inhibition * 100)) + "%");//rounding down ,for the 50% negative or positive
                    }
                    break;
            }
        }
    }

    public void getEnd() {
        if (!cbUse.isChecked()) // 是否进行
            return;

//		LogUtil.d("getEnd da0="+da0);
        switch (mode) {
            case MODE_CONSTRUCT:    //对照结束
                if (da0 < 0.2 || da0 > 10) {
                    ToastUtil.showText("对照度值不合理，请检查试剂及仪器。", Toast.LENGTH_LONG);
                    return;
                }
                MyApp.getApp().getPref().edit().putFloat(Consts.KEY_DA0, da0).commit();
                //通知已经改变对照值。
                ((DetectActivity) context).onDA0Changed(da0);
                break;
            case MODE_DETECT:
                if (inhibition < 0)
                    inhibition = 0;
                else if (inhibition > 1)
                    inhibition = 1;
                data.setTime(System.currentTimeMillis());
//			tvInhibition.setText(String.format("%.1f", inhibition*100)+"%");
                if (inhibition > Consts.ALL) {
                    tvInhibition.setText("100%");
                } else {
                    tvInhibition.setText(String.format("%.2f", (inhibition * 100)) + "%");//rounding down
                }

                data.setAbsorbancy(finalV);//吸光度
                data.setInhibitionRatio(inhibition); //抑制率
                if (inhibition >= proj.getLimit()) {
                    data.setResult(getString(R.string.positive));    //检测结果
                } else {
                    data.setResult(getString(R.string.negative));
                }

                tvResult.setText(data.getResult());
                MyApp.getApp().addData(data);//有空了再改进之。
                break;
        }
    }

    private String getString(int id) {
        return MyApp.getApp().getString(id);
    }

    /**
     * 暂不使用
     */
    public void calculate() {

    }

    public String getInhibition() {
        return tvInhibition.getText().toString();
    }

    public void setInhibition(String inhibition) {
        if (null != inhibition)
            this.tvInhibition.setText(inhibition);
        ;
    }

    public String getTvResult() {
        return tvResult.getText().toString();
    }

    public void setResult(String result) {
        this.tvResult.setText(result);
        ;
    }

    public String getNum() {
        return tvNum.getText().toString();
    }

    public void setTvNum(String num) {
        if (null != num)
            this.tvNum.setText(num);
        ;
    }

    public boolean isUse() {
        return cbUse.isChecked();
    }

    public void setCbUse(Boolean use) {
        this.cbUse.setChecked(use);
        ;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
        if (null != channel)
            tvNum.setText(channel);
        if (null != data) {
            data.setChannel(channel);
        }
    }

    public void setChecked(boolean checked) {
        cbUse.setChecked(checked);
//		if (!checked) {
//			reset();
//		}
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    /**
     * @param type 选择CONSTRUCT或DETECT
     */
    public void setMode(byte type) {
        this.mode = type;
    }

    public void setDA0(float da0) {
        this.da0 = da0;
//		LogUtil.d("设置da0为："+da0);
    }

    public void setLocation(Location location) {
        data.setLocation(location);
    }

    @Override
    public void onClick(View v) {
        if (cbUse.isChecked()) {
            cbUse.setChecked(false);
        } else {
            cbUse.setChecked(true);
        }

    }

}
