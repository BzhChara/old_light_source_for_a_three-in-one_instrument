package com.whswzz.prfluroanalyzer.enzyme;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;

import org.apache.poi.xssf.usermodel.TextVerticalOverflow;
import org.xutils.ex.DbException;

import com.google.zxing.datamatrix.DataMatrixReader;
import com.whswzz.prfluroanalyzer.base.BaseActivity;
import com.whswzz.prfluroanalyzer.consts.Consts;
import com.whswzz.prfluroanalyzer.entity.IData;
import com.whswzz.prfluroanalyzer.enzyme.entity.EnzymeData;
import com.whswzz.prfluroanalyzer.fluoro.dal.imp.XDao;
import com.whswzz.prfluroanalyzer.fluoro.entity.FluData;
import com.whswzz.prfluroanalyzer.model.DataModel;
import com.whswzz.prfluroanalyzer.model.HttpModel;
import com.whswzz.prfluroanalyzer.param.Params;
import com.whswzz.prfluroanalyzer.ui.EditDialog;
import com.whswzz.prfluroanalyzer.utils.PrinterJPW;
import com.zkzk.pra.R;
import com.zkzk.pra.ui.ProDialog2;
import com.zkzk.pra.ui.VideoDialog2;
import com.zkzk.pra.utils.ExceptionHandler;
import com.zkzk.pra.utils.ToastUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import top.jemen.interfaces.ACallback;
import top.jemen.interfaces.ICallback;
import top.jemen.utils.LogUtil;
import top.jemen.utils.Tone;

public class EnzymeActivity extends BaseActivity implements OnClickListener {
    private Button btReactStart, btColorate, btPrint, btUpload, btSave;
    private int N = 6;// 6通道的酶片孵育
    private EnzymeData[] datas = new EnzymeData[N];
    private LinearLayout llEnzymes;
    private EnzymeSpecimenDialog[] dialogs;
    private TextView tvTemp, tvReact, tvColorate;
    private int REACT_T = 600, COLORATE_T = 180;
    private int reactT = 0, colorateT = 0;
    private boolean alive = true;
    private TextView[] tvResults = new TextView[N];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_enzyme);
        init();
        setListeners();
        DataModel.getInstance().setEnzymeTemp(38);
        Params.TEMP_UPDATE = true;
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        alive = false;
        EventBus.getDefault().unregister(this);
        Params.TEMP_UPDATE = false;
        super.onDestroy();
    }


    private void init() {
        btReactStart = (Button) findViewById(R.id.bt_enzyme_react_start);
        btColorate = (Button) findViewById(R.id.bt_enzyme_colorate_start);

        btPrint = (Button) findViewById(R.id.bt_enzyme_print);
        btUpload = (Button) findViewById(R.id.bt_enzyme_upload);
        btSave = (Button) findViewById(R.id.bt_save);
        llEnzymes = (LinearLayout) findViewById(R.id.ll_enzymes);

        tvTemp = (TextView) findViewById(R.id.tv_temp);
        tvReact = (TextView) findViewById(R.id.tv_react_time);
        tvColorate = (TextView) findViewById(R.id.tv_colorate_time);

        datas = new EnzymeData[N];
        dialogs = new EnzymeSpecimenDialog[N];
        for (int i = 0; i < datas.length; i++) {
            datas[i] = new EnzymeData();
            datas[i].setChannel(String.valueOf(i + 1));


        }
        tvReact.setText("反应时间：" + REACT_T + "秒");
        ;
        tvColorate.setText("显色时间：" + COLORATE_T + "秒");
        tvTemp.setText("当前温度：" + Params.TEMP_ENZYME);
    }

    private void setListeners() {
        btReactStart.setOnClickListener(this);
        btColorate.setOnClickListener(this);
        btPrint.setOnClickListener(this);
        btUpload.setOnClickListener(this);
        btSave.setOnClickListener(this);
        tvReact.setOnClickListener(this);
        tvColorate.setOnClickListener(this);


        for (int i = 1; i < llEnzymes.getChildCount(); i++) {
            final int index = i - 1;
            View enzymeView = llEnzymes.getChildAt(i);
            TextView tvSpecimen = (TextView) enzymeView.findViewById(R.id.tv_enzyme_specimen);
            tvSpecimen.setTag(i - 1);
            ((TextView) enzymeView.findViewById(R.id.tv_channel_number)).setText("" + i);
            tvSpecimen.setOnClickListener(this);

            TextView tvResult = (TextView) enzymeView.findViewById(R.id.tv_result);
            tvResult.setTag(i - 1);
            tvResult.setOnClickListener(this);
            tvResults[i - 1] = tvResult;

            CheckBox cb = (CheckBox) enzymeView.findViewById(R.id.cb_use);
            cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    datas[index].setChecked(isChecked);
                }
            });

            TextView tvProj = (TextView) enzymeView.findViewById(R.id.tv_enzyme_proj);
            datas[i - 1].setProj(tvProj.getText().toString());
        }
    }

    @Override
    public void onClick(final View v) {
        EditDialog ed;

        switch (v.getId()) {
            case R.id.bt_enzyme_react_start:
                if (Params.TEMP_ENZYME < 37) {
                    ToastUtil.showText("请待温度上升到38度左右再进行操作", Toast.LENGTH_SHORT);
                    Tone.get().play(R.raw.dingdong906);
                    return;
                }
                for (int i = 0; i < N; i++) {
                    datas[i].setResult("");
                    tvResults[i].setText(datas[i].getResult());
                }
                reactT = REACT_T;
                new Thread() {
                    public void run() {
                        EventBus.getDefault().post(Enzyme.REACT_TIME);
                        Tone.get().play(R.raw.start);
                        while (reactT > 0) {
                            if (!alive) {
                                return;
                            }
                            SystemClock.sleep(1000);
                            reactT--;
                            EventBus.getDefault().post(Enzyme.REACT_TIME);
                        }
                        Tone.get().play(R.raw.react_end);
                    }

                    ;
                }.start();

                v.setEnabled(false);
                // new EnzymeDialog(this).show();

                break;
            case R.id.bt_enzyme_colorate_start:
                if (Params.TEMP_ENZYME < 37) {
                    ToastUtil.showText("请待温度上升到38度左右再进行操作", Toast.LENGTH_SHORT);
                    Tone.get().play(R.raw.dingdong906);
                    return;
                }
                if (reactT > 0) {
                    ToastUtil.showText("请待反应计时完成之后再进行反应计时", Toast.LENGTH_SHORT);
                    Tone.get().play(R.raw.dingdong906);
                    return;
                }
                v.setEnabled(false);

                colorateT = COLORATE_T;
                new Thread() {
                    public void run() {
                        EventBus.getDefault().post(Enzyme.COLORATE_TIME);
                        Tone.get().play(R.raw.start);
                        while (colorateT > 0) {
                            if (!alive) {
                                return;
                            }
                            SystemClock.sleep(1000);
                            colorateT--;
                            EventBus.getDefault().post(Enzyme.COLORATE_TIME);
                        }
                        Tone.get().play(R.raw.colorate_end);
                    }

                    ;
                }.start();

                break;
            case R.id.bt_enzyme_print:
                final List<IData> prints = new ArrayList<>();
                int sunm = 0;
                for (EnzymeData d : datas) {
                    if (d.isChecked()) {
                        prints.add(d);
                        if (!TextUtils.isEmpty(d.getResult())) {
                            sunm++;
                        }
                    }
                }

                if (prints.size() == 0) {
                    ToastUtil.showText("请选择需要打印的通道", Toast.LENGTH_SHORT);
                    return;
                }
                if (sunm == 0) {
                    ToastUtil.showText("请操作完成后并选择检测结果", Toast.LENGTH_SHORT);
                    return;
                }
                LogUtil.d("打印:" + prints);

                final String[] items = {"打印常规结果","打印承诺达标合格证（农产品生产者）","打印承诺达标合格证（农产品收购单位/个人）"};
                AlertDialog.Builder listDialog = new Builder(this);
                listDialog.setTitle("请选择打印类型");
                listDialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            PrinterJPW.print(prints, new ICallback() { // 此函数内部会开启新的线程以适配打印机的速度以防打印机缓存爆掉。
                                @Override
                                public void onSuccess(Object obj) {
                                    btPrint.setEnabled(true);
                                }

                                @Override
                                public void onFailed(Object obj) {
                                    btPrint.setEnabled(true);
                                    ToastUtil.showText("" + obj, Toast.LENGTH_SHORT);
                                }
                            });
                        } else if (which==1){
                            PrinterJPW.printCert(prints, new ICallback() { // 此函数内部会开启新的线程以适配打印机的速度以防打印机缓存爆掉。
                                @Override
                                public void onSuccess(Object obj) {
                                    btPrint.setEnabled(true);
                                }

                                @Override
                                public void onFailed(Object obj) {
                                    btPrint.setEnabled(true);
                                    ToastUtil.showText("" + obj, Toast.LENGTH_SHORT);
                                }
                            });
                        }else {
                            PrinterJPW.printCertPerson(prints, new ICallback() { // 此函数内部会开启新的线程以适配打印机的速度以防打印机缓存爆掉。
                                @Override
                                public void onSuccess(Object obj) {
                                    btPrint.setEnabled(true);
                                }

                                @Override
                                public void onFailed(Object obj) {
                                    btPrint.setEnabled(true);
                                    ToastUtil.showText("" + obj, Toast.LENGTH_SHORT);
                                }
                            });
                        }
                    }
                });
                listDialog.show();
                break;
            case R.id.bt_enzyme_upload:
                List<IData> ls = new ArrayList<>();
                int sum = 0;
                for (EnzymeData d : datas) {
                    if (d.isChecked()) {
                        ls.add(d);
                        if (!TextUtils.isEmpty(d.getResult())) {
                            sum++;
                        }
                    }
                }
                if (ls.size() == 0) {
                    ToastUtil.showText("请选择需要打上传的通道", Toast.LENGTH_SHORT);
                    return;
                }
                if (sum == 0) {
                    ToastUtil.showText("请操作完成后并选择检测结果", Toast.LENGTH_SHORT);
                    return;
                }
                LogUtil.d("上传:" + ls);
                HttpModel.get().send(ls, new ICallback() {
                    @Override
                    public void onSuccess(Object obj) {
                        ToastUtil.showText("上传失败：" + obj, Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onFailed(Object obj) {
                        ToastUtil.showText("上传成功" + obj, Toast.LENGTH_SHORT);
                    }
                });
                break;
            case R.id.tv_enzyme_specimen:
                final int x = (int) v.getTag();
                if (null == dialogs[x]) {
                    dialogs[x] = new EnzymeSpecimenDialog(this, datas[x], new ACallback() {
                        @Override
                        public void onSuccess(Object obj) {
                            TextView tv = (TextView) llEnzymes.getChildAt(x + 1).findViewById(R.id.tv_enzyme_specimen);
                            tv.setText(datas[x].getSpecimen() + "");
                        }
                    });
                }
                dialogs[x].show();
                break;
            case R.id.tv_result:
                int c = (int) v.getTag();
                if (results[0].equals(tvResults[c].getText().toString())) {
                    tvResults[c].setText(results[1]);
                    datas[c].setResult(results[1]);
                } else if (results[1].equals(tvResults[c].getText().toString())) {
                    tvResults[c].setText(results[2]);
                    datas[c].setResult(results[2]);
                } else if (results[2].equals(tvResults[c].getText().toString())) {
                    tvResults[c].setText(results[1]);
                    datas[c].setResult(results[1]);
                }
                break;
            case R.id.bt_save: // 有空了看移到子线程
                ls = new LinkedList<IData>();
                sum = 0;
                for (EnzymeData d : datas) {
                    if (d.isChecked()) {
                        ls.add(d);
                        if (!TextUtils.isEmpty(d.getResult())) {
                            sum++;
                        }
                    }
                }
                if (ls.size() == 0) {
                    ToastUtil.showText("请反应计时和显色计时完成后选择阴阳性再保存", Toast.LENGTH_SHORT);
                    return;
                }
                if (ls.size() == 0) {
                    ToastUtil.showText("请选择待保存的通道", Toast.LENGTH_SHORT);
                    return;
                }

                XDao.save(ls, new ICallback() {
                    @Override
                    public void onSuccess(Object obj) {
                        ToastUtil.showText("数据保存成功", Toast.LENGTH_SHORT);
                        v.setEnabled(true);
                    }

                    @Override
                    public void onFailed(Object obj) {
                        ToastUtil.showText("数据保存不成功：" + obj, Toast.LENGTH_SHORT);
                        v.setEnabled(true);
                    }
                });
                v.setEnabled(false);
                break;
            case R.id.ib_bottom_home:
            case R.id.ib_bottom_back:
                if (reactT > 0 || colorateT > 0) {
                    ToastUtil.showText("反应尚未完成", Toast.LENGTH_SHORT);
                } else {
                    super.onClick(v);
                }
                break;
            default:
                super.onClick(v);
                break;
            case R.id.tv_react_time:
                ed = new EditDialog(this, new EditDialog.EditListener() {
                    @Override
                    public void back(String text) {
                        if (TextUtils.isEmpty(text)) {
                            ToastUtil.showText("请输入反应时间，以秒为单位", Toast.LENGTH_LONG);
                            return;
                        }
                        try {
                            int x = Integer.parseInt(text);
                            LogUtil.d("x=" + x);
                            if (x < 10 || x > 2000) {
                                ToastUtil.showText("请输入正确的数值，以秒为单位", Toast.LENGTH_LONG);
                                return;
                            }
                            REACT_T = x;
                            tvReact.setText("反应时间：" + REACT_T + "秒");
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            ToastUtil.showText("请输入正确的反应时间，以秒为单位", Toast.LENGTH_LONG);
                        }

                    }
                }, "请输入", "反应时间（秒）");
                ed.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                ed.show();
                break;
            case R.id.tv_colorate_time:
                ed = new EditDialog(this, new EditDialog.EditListener() {
                    @Override
                    public void back(String text) {
                        if (TextUtils.isEmpty(text)) {
                            ToastUtil.showText("请输入显色时间，以秒为单位", Toast.LENGTH_LONG);
                            return;
                        }
                        try {
                            int x = Integer.parseInt(text);
                            if (x < 10 || x > 600) {
                                ToastUtil.showText("请输入正确的显色时间，以秒为单位", Toast.LENGTH_LONG);
                                return;
                            }
                            COLORATE_T = x;
                            tvColorate.setText("显色时间：" + COLORATE_T + "秒");
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            ToastUtil.showText("请输入正确的数值，以秒为单位", Toast.LENGTH_LONG);
                        }

                    }
                }, "请输入", "反应时间（秒）");
                ed.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                ed.show();
                ;
                break;
        }
    }

    final String[] results = {"请选择", "阴性", "阳性"};
//	private AlertDialog.Builder[] resultDialogs=new AlertDialog.Builder[N]; 

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void eventMessage(Enzyme enzyme) {
        switch (enzyme) {
            case TEMP:
                tvTemp.setText("当前温度：" + Params.TEMP_ENZYME + "℃");
                break;
            case REACT_TIME:
                tvReact.setText("反应剩余时间：" + reactT + "秒");
                if (reactT == 0) {
                    btReactStart.setEnabled(true);
                    tvReact.setText("反应时间：" + REACT_T + "秒");
                    ;
                }
                break;
            case COLORATE_TIME:
                tvColorate.setText("显色剩余时间：" + colorateT + "秒");
                if (colorateT == 0) {
                    btColorate.setEnabled(true);
                    tvColorate.setText("显色时间：" + COLORATE_T + "秒");
                    for (TextView tv : tvResults) {
                        tv.setText(results[0]);
                    }
                    for (EnzymeData d : datas) {
                        if (d.isChecked()) {
                            d.setTime(System.currentTimeMillis());
                        }
                    }
                }
                break;
            case ENZYME_COVER: //盖上盖子
                if(btColorate.isEnabled()){
                    btColorate.callOnClick();
                }

                break;
        }
    }

    ;


    public enum Enzyme {
        TEMP, REACT_TIME, COLORATE_TIME, ENZYME_COVER
    }

}
