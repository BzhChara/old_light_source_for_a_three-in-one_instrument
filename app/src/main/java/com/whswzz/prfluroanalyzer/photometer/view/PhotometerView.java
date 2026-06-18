package com.whswzz.prfluroanalyzer.photometer.view;

import java.util.Arrays;
import java.util.List;

import org.xutils.ex.DbException;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.consts.Consts;
import com.whswzz.prfluroanalyzer.consts.Signal;
import com.whswzz.prfluroanalyzer.entity.Organization;
import com.whswzz.prfluroanalyzer.fluoro.dal.imp.XDao;
import com.whswzz.prfluroanalyzer.param.Params;
import com.whswzz.prfluroanalyzer.photometer.entity.Function;
import com.whswzz.prfluroanalyzer.photometer.entity.PhotometerData;
import com.whswzz.prfluroanalyzer.photometer.entity.PhotometerProj;
import com.zkzk.pra.R;
import com.zkzk.pra.utils.ExceptionHandler;

//import android.R.styleable;
import android.app.Activity;
//import android.app.ResultInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
//import android.filterfw.core.StopWatchMap;
//import android.os.Broadcaster;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import top.jemen.interfaces.ACallback;
import top.jemen.utils.LogUtil;

import android.view.View.OnClickListener;

public class PhotometerView extends LinearLayout implements OnClickListener {
    public static final byte MODE_CONSTRUCT = 0;
    public static final byte MODE_DETECT = 1;
    protected static final int Data = 0;
    private Context context;
    private TextView tvAbsorbance, tvSpecimen, tvResult, tvNum;
    private CheckBox cbUse;
    private String channel = "";
    private PhotometerData data;
    private View root;

    private float initialV = 0, finalV;// 开始检测第10s的吸光度与第310s的吸光度；
    private float ref=0;

    private byte mode = MODE_DETECT; //
    //	private List<PhotometerProj> projs;
    private PhotometerProj proj;

    public PhotometerView(final Context context, AttributeSet attrs) {
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
        tvSpecimen = (TextView) findViewById(R.id.tv_inhibition_ratio);
        tvResult = (TextView) findViewById(R.id.tv_result);
        tvNum = (TextView) findViewById(R.id.tv_channel_number);
        cbUse = (CheckBox) findViewById(R.id.cb_use);
        tvNum.setText(channel);
        data = new PhotometerData();    //都在数据库保存，因为没有使用内存存储，所以一直使用同一个对象。
        data.setChannel(channel);
        SharedPreferences pref = MyApp.getApp().getPref();


        String operator = pref.getString(Consts.KEY_OPERATOR, null);

//		projs = MyApplication.getApp().getProjs();
//		data.setProj(projs.get(0).getProj());
//		String[] types = getResources().getStringArray(R.array.type);
//		data.setSpecimen(types[0]);	//样品类型用户自己输入吧。
//		data.setLimit("0-50%");//初始的状态，设置proj后更新。
        List<Organization> users = MyApp.getApp().getOrganizations();
        if (null != users && users.size() > 0) {
            int i = pref.getInt(Consts.USER_INDEX, 0);
            Organization u = users.get(i);
            data.setUserAddr(u.getAddr());
            data.setUserContact(u.getContact());
            data.setUserOrg(u.getName());
            data.setUserPhone(u.getPhone());
            data.setUsrCode(u.getCode());
        }

    }

    public void setProj(PhotometerProj proj) {
        this.proj = proj;
        data.setProj(proj.getName());
    }

    public void setRef(float ref) {
        this.ref = ref;
    }

    public void destroy() {
        if (specimenDialog != null) {
            specimenDialog.cancel();
        }
    }

    private PhotometerSpecimenDialog specimenDialog;

    private void setListeners() {
        root.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == specimenDialog) {
                    specimenDialog = new PhotometerSpecimenDialog((Activity) context, data, new ACallback() {
                        @Override
                        public void onSuccess(Object obj) {
                            tvSpecimen.setText(data.getSpecimen());
                        }
                    });
                } else { //外面可能更改proj的
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
        tvSpecimen.setText("");
        tvResult.setText("");
    }

    /**
     * 清空界面显示、所有数据，包括样品编号、名称及送检单位等。
     */
    public void clear() {
        data.setResult(null);
        data.setSn(null);
        data.setSpecimen(null);

        finalV = 0;
        tvAbsorbance.setText("");
        tvSpecimen.setText("");
        tvResult.setText("");
    }

    private void resetData() {
        initialV = 0;
        finalV = 0;
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
        if (!Params.showAbnormal()&&(absorbance < 0 || absorbance > 10) ){
            return;
        }
        buf[x % 3] = absorbance;
        x++;
//		if (absorbance < 0) //现在的模块不显示负数了好像
//			absorbance *= -1;


        if (num > buf.length) {
            float[] copy = Arrays.copyOf(buf, buf.length);
            Arrays.sort(copy);
            finalV = copy[copy.length / 2];
            // finalV =(buf[0]+buf[1]+buf[2])/3;
        } else {
            finalV = absorbance;
        }

        switch (mode) {
            case MODE_CONSTRUCT:
                tvAbsorbance.setText(String.format("%.3f", finalV));
                break;
            case MODE_DETECT:
                tvAbsorbance.setText(String.format("%.3f", getDisplayAbsorbance()));
                if ("对照".equals(channel)) {
                    return;
                }

                break;
        }
    }

    private float getDisplayAbsorbance() {
        float absorbance = finalV - ref;
        return absorbance < 0 ? 0 : absorbance;
    }

    public float getAbsorbance() {
        return finalV;
    }

    public void getEnd() {
        if (!cbUse.isChecked()) // 是否进行
            return;

//		LogUtil.d("getEnd da0="+da0);
        switch (mode) {
            case MODE_CONSTRUCT:    //对照结束
//                MyApp.getApp().getPref().edit().putFloat(Consts.KEY_REF + proj + "-" + data.getSpecimen(), finalV).apply();
                Params.setBaseAbsorbance(proj.getName(),finalV);
                EventBus.getDefault().post(Signal.PHOTOMETER_REF_CHANGE);

                break;
            case MODE_DETECT:
                if ("对照".equals(channel)) {
                    return;
                }
                float displayAbsorbance = getDisplayAbsorbance();
                data.setAbsorbancy(displayAbsorbance);//吸光度
                LogUtil.d("proj=" + proj + ",specimen=" + data.getSpecimen());
                if (null == proj || null == proj.getFunction(data.getSpecimen())) {
                    data.setResult("未设置标曲");
                } else {
                    Function f = proj.getFunction(data.getSpecimen());
                    LogUtil.d(proj.getName() + "-" + data.getSpecimen() + "  -function=" + f.toString());
                    double[][] ps = f.getParams();
                    int i = 0;
                    for (; i < ps[0].length; i++) {
                        if (ps[0][i] > displayAbsorbance) { //第一行标识吸光度。
                            break;
                        }
                    }
                    if (i == 0 || i >= ps[0].length) { //
                        data.setResult("超标曲范围");
                    } else {
                        LogUtil.d("计算浓度:" + Arrays.toString(ps[0]));
                        LogUtil.d("计算浓度:" + Arrays.toString(ps[1]));

                        double c = ps[1][i - 1] + (ps[1][i] - ps[1][i - 1]) / (ps[0][i] - ps[0][i - 1]) * (displayAbsorbance - ps[0][i - 1]);
                        data.setResult(String.format("%.3f", c) + f.getUnit());
                    }
                }


                data.setTime(System.currentTimeMillis());
                tvResult.setText(data.getResult());
                try {
                    XDao.getDb().save(data);
                } catch (DbException e) {
                    e.printStackTrace();
                }
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
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
        if (null != channel) {
            if ("对照".equals(channel)) {
                setChecked(true);
                cbUse.setVisibility(View.GONE);
                tvNum.setTextSize(25);
                tvNum.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            }
            tvNum.setText(channel);
        }
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

    public PhotometerData getData() {
        return data;
    }

    public void setData(PhotometerData data) {
        this.data = data;
    }

    /**
     * @param type 选择CONSTRUCT或DETECT
     */
    public void setMode(byte type) {
        this.mode = type;
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
