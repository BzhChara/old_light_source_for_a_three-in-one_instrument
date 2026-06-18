package com.whswzz.prfluroanalyzer.photometer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.xutils.x;
import org.xutils.view.annotation.ViewInject;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.base.BaseActivity;
import com.whswzz.prfluroanalyzer.consts.Consts;
import com.whswzz.prfluroanalyzer.consts.Signal;
import com.whswzz.prfluroanalyzer.entity.AbsorbancyBin;
import com.whswzz.prfluroanalyzer.entity.IData;
import com.whswzz.prfluroanalyzer.model.DataModel;
import com.whswzz.prfluroanalyzer.param.Params;
import com.whswzz.prfluroanalyzer.photometer.entity.PhotometerData;
import com.whswzz.prfluroanalyzer.photometer.entity.PhotometerProj;
import com.whswzz.prfluroanalyzer.photometer.view.PhotometerView;
import com.whswzz.prfluroanalyzer.utils.PrinterJPW;
import com.zkzk.pra.R;
import com.zkzk.pra.entity.Project;
import com.zkzk.pra.ui.MyDialog;
import com.zkzk.pra.ui.MyDialog.BackListener;
import com.zkzk.pra.ui.ProDialog2;
import com.zkzk.pra.utils.ExceptionHandler;
import com.zkzk.pra.utils.TTS;
import com.zkzk.pra.utils.ToastUtil;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
//import android.media.effect.effects.StraightenEffect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import top.jemen.interfaces.ICallback;
import top.jemen.utils.LogUtil;
import top.jemen.utils.Tone;

import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * 检测界面
 * 自动选定通道，软件会根据各个通道吸光度的变化而自动选定通道，但是某些通道吸光度不稳会导致额外的选定。
 *
 * @author Jemen Chen
 */
public class PhotometerActivity extends BaseActivity implements OnClickListener {
    public static final boolean CYCLE = false; // 循环测试使用,需要测试时改为true
    private static final int PHOTOMETER_MODULES = DataModel.A; // 新分光板由A命令返回有效通道数据。
    private static final int CHANNEL_DISPLAY_ALL = 0;
    private static final int CHANNEL_DISPLAY_1_TO_12 = 1;
    private static final int CHANNEL_DISPLAY_13_TO_24 = 2;
    private static final int CHANNEL_DISPLAY_MODE = CHANNEL_DISPLAY_ALL;
    @ViewInject(R.id.ll_up)
    private LinearLayout llUP;
    @ViewInject(R.id.ll_down)
    private LinearLayout llDown;
    @ViewInject(R.id.bt_detect_reset)
    private Button btReset;
    @ViewInject(R.id.bt_detect_print)
    private Button btPrint;
    @ViewInject(R.id.bt_detect_start)
    private Button btStart;
    @ViewInject(R.id.cb_all)
    private CheckBox cbAll;
    @ViewInject(R.id.rg_detect_mode)
    private RadioGroup rgMode;

    @ViewInject(R.id.sp_photometer_type)
    private Spinner spType;
    @ViewInject(R.id.sp_photometer_light)
    private Spinner spLight;


    private List<PhotometerProj> projs = MyApp.getApp().getPhotoProjs();
    private PhotometerProj proj = projs.get(0);
    private Project projD = MyApp.getApp().getProjs().get(0);

    private PhotometerView[] pmViews = new PhotometerView[24];
    private int activeChannelStart = 0;
    private int activeChannelEnd = 12;



    @Subscribe(threadMode = ThreadMode.MainThread)
    public void getAbsorbancy(AbsorbancyBin ab) { //目前使用
        float[] fs = ab.getAbs();
        timers[ab.getChannel()]++;
        for (int i = 0; i < fs.length; i++) {
            int viewIndex = getPhotometerViewIndex(ab.getChannel(), i);
            if (!isActiveViewIndex(viewIndex)) {
                continue;
            }
            float[] kb = Params.getPhotometer(viewIndex, light);

            pmViews[viewIndex].setAbsorbance(timers[ab.getChannel()], fs[i] * kb[0] + kb[1]);
        }
        LogUtil.d("photometer receive:" + Arrays.toString(fs));
        LogUtil.d("timers:" + Arrays.toString(timers));

        if (state == 1 && timers[0] > 13) { //改成24个通道一起返回了，只用到了timers[0]
            int i;
            for (i = activeChannelStart; i < activeChannelEnd; i++) {
                if (pmViews[i].getAbsorbance() >= 0.05) { //复位完成之后，需要每个通道都接近于0
                    break;
                }
            }
            if (i == activeChannelEnd) {
                btStart.setEnabled(true);
                ToastUtil.showText("复位完成，请放入比色皿", Toast.LENGTH_LONG);
                state = 2;
                btReset.setEnabled(true);
                if (null != initDialog) {
                    initDialog.cancel();
                }
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void changeRef(Signal s) { //目前使用
        setCurrentRef(Params.getBaseAbsorbance(proj.getName()));
    }

    private void clearCurrentRef() {
        Params.setBaseAbsorbance(proj.getName(), 0);
        setCurrentRef(0);
    }

    private void setCurrentRef(float ref) {
        for (PhotometerView pm : pmViews)
            pm.setRef(ref);
        tvAbsorbancy.setText("当前对照值:" + String.format("%.3f", ref));
    }

    private int getPhotometerViewIndex(int channelGroup, int dataIndex) {
        return 12 * channelGroup + dataIndex;
    }

    private boolean isActiveViewIndex(int viewIndex) {
        return viewIndex >= activeChannelStart && viewIndex < activeChannelEnd && viewIndex < pmViews.length;
    }

    int[] timers = new int[2];
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case Consts.ABSORBANCY: //吸光度数据  ,没有使用这个传递数据了。   改用EventBus了
                    float[] fs = (float[]) msg.obj;
                    LogUtil.d("吸光度" + msg.arg1 + ",fs:" + Arrays.toString(fs));
                    timers[msg.arg1]++;
                    for (int i = 0; i < fs.length; i++) {
                        int viewIndex = getPhotometerViewIndex(msg.arg1, i);
                        if (!isActiveViewIndex(viewIndex)) {
                            continue;
                        }
                        pmViews[viewIndex].setAbsorbance(timers[msg.arg1], fs[i]);
                        if (i > 0) {
                            pmViews[viewIndex].setRef(fs[0]);
                        }
                    }
                    break;
                case Consts.DETECT_TIME:
                    timers[0] = 0;
                    timers[1] = 0;
                    proDialog.show();
                    proDialog.setTimer(msg.arg1);
                    state = 3;
                    if (msg.arg1 > 0) {
                        if (MyApp.getApp().isTtsOk()) {
                            TTS.stop();
                            TTS.speak(getResources().getString(R.string.start_detect));
                        }
                    }
                    break;
                case Consts.MSG_READ_END://消息来自DataModel
                    for (PhotometerView pm : pmViews)
                        pm.getEnd();
                    proDialog.dismiss();
                    if (CYCLE) {
                        handler.postDelayed(cyclicTest, 5000);
                        MyApp.getApp().lightenScreen();
                    }

                    if (MyApp.getApp().getPrintSet()&&rgMode.getCheckedRadioButtonId()==R.id.rb_contrast) {    //自动打印。
                        List<IData> datas = new ArrayList<>();
                        for (PhotometerView v : pmViews) {
                            if (v.isUse()) {
                                PhotometerData data = v.getData();
                                if (data == null || data.getResult() == null)
                                    continue;
                                datas.add(data);
                            }
                        }
                        PrinterJPW.print(datas, new ICallback() { // 此函数内部会开启新的线程以适配打印机的速度以防打印机缓存爆掉。
                            @Override
                            public void onSuccess(Object obj) {
                                btPrint.setEnabled(true);
                            }

                            @Override
                            public void onFailed(Object obj) {
                                btPrint.setEnabled(true);
                            }
                        });
                        btPrint.setEnabled(false);
                        break;
                    } else {
                        btPrint.setEnabled(true);
                    }
                    Tone.get().play(R.raw.detect_complete);
                    state = 0;

                    break;
                case Consts.SERIAL_ERROR:
                    ToastUtil.showText(R.string.e3, Toast.LENGTH_SHORT);
                    break;
                case Consts.SERIAL_OPEN_ERROR:
                    ToastUtil.showText(R.string.serial_port_open_failed, Toast.LENGTH_SHORT);
                    break;
                case Consts.SERIAL_IS_USING:
                    ToastUtil.showText(R.string.e4, Toast.LENGTH_SHORT);
                    break;
                case Consts.TIME_OUT:
                    proDialog.dismiss();
                    ToastUtil.showText(R.string.detect_time_out, Toast.LENGTH_SHORT);
                    break;

                default:
                    break;
            }

        }
    };

    private DataModel dataModel;
    // private LocalBroadcastManager bcManager;
    private TextView tvAbsorbancy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_photometer);
        x.view().inject(PhotometerActivity.this);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                init();
                setListeners();
                initProDialog();
                initSpinner();
            }

        }, 140);
        // IntentFilter filter = new IntentFilter(Consts.ACTION_DA0_CHANGED);
        // bcManager = LocalBroadcastManager.getInstance(DetectActivity.this); //
        // 暂时不用广播了,直接用函数回调.
        // bcManager.registerReceiver(receiver, filter);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    private void init() {
        //动态加载
        LayoutParams params = new LayoutParams(llUP.getWidth() / 13, // 为
                LayoutParams.MATCH_PARENT);
        for (int i = 0; i < 12; i++) {
            PhotometerView pr = new PhotometerView(this, null);
            pr.setLayoutParams(params);
//			if(i==0) {
//				pr.setChannel("对照");
//			}else {
            pr.setChannel("" + (i + 1));
//			}
            llUP.addView(pr);
            pmViews[i] = pr;
            // pr.reset();
            pr.setProj(proj);
        }
        llUP.getChildAt(0).setVisibility(View.VISIBLE);

        for (int i = 0; i < 12; i++) {
            PhotometerView pr = new PhotometerView(this, null);
            pr.setLayoutParams(params);
//			if(i==0) {
//				pr.setChannel("对照");
//			}else {
            pr.setChannel("" + (i + 13));
//			}
            llDown.addView(pr);
            pmViews[i + 12] = pr;
            // pr.reset();
            pr.setProj(proj);
        }

        llDown.getChildAt(0).setVisibility(View.VISIBLE);
        applyChannelDisplayMode();


        setMode();
        dataModel = DataModel.getInstance();
        tvAbsorbancy = (TextView) findViewById(R.id.tv_user);

        spLight.setEnabled(true);
    }


    private void initSpinner() {
        ArrayAdapter<PhotometerProj> aad = new ArrayAdapter<>(this, R.layout.my_spinner, projs);
        aad.setDropDownViewResource(R.layout.my_spinner_dropdown);
        spType.setAdapter(aad);
        spType.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 proj=projs.get(position);
                switch (proj.getName()) {
                    case "双氧水": // 默认六通道
//                    case "甲醛":
                    case "挥发性盐基氮":
                    case "农药残留":
                        light = 0;//410
                        break;
                    case "组胺":
                        light = 1;    //460
                        break;
                    default:
                        light = 2;  //520
                        break;

                    case "二氧化硫":
                    case "硼砂":
                    case "重金属汞":
                        light = 3;    //550
                        break;
                    case "蛋白质":
                    case "面中铝":
                        light = 4; //590
                        break;

                }
                spLight.setSelection(light);
                for (PhotometerView pv : pmViews) {
                    pv.setProj(proj);
                }
                btStart.setEnabled(false);
                setCurrentRef(Params.getBaseAbsorbance(proj.getName()));

            }
        });

        ArrayAdapter<String> ad = new ArrayAdapter<>(this, R.layout.my_spinner,Params.lights);
        ad.setDropDownViewResource(R.layout.my_spinner_dropdown);
        spLight.setAdapter(ad);
        spLight.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (lastSelectedLight != -1 && lastSelectedLight != position) {
                    clearCurrentRef();
                }
                light = position;
                lastSelectedLight = position;
            }
        });
    }

    private int light = 0;
    private int lastSelectedLight = -1;

    private void setListeners() {
        btReset.setOnClickListener(this);
        btPrint.setOnClickListener(this);
        btStart.setOnClickListener(this);
        cbAll.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (int i = activeChannelStart; i < activeChannelEnd; i++)
                    pmViews[i].setChecked(isChecked);
            }
        });
        rgMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setMode();
            }
        });

    }


    private Dialog initDialog;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_detect_reset:
                new MyDialog(this, new BackListener() {
                    @Override
                    public void back() {
                        DataModel.getInstance().startPhotometer(handler, light, PHOTOMETER_MODULES);
//					btStart.setEnabled(true);
                        resetAll();
                        state = 1;
                        initDialog = new AlertDialog.Builder(PhotometerActivity.this).setTitle("提示").setMessage("分光模块初始化中，请稍候").setCancelable(false).create();
                        initDialog.show();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (null != initDialog && initDialog.isShowing()) {
                                    initDialog.cancel();
                                    ToastUtil.showText("分光模块初始化不成功", Toast.LENGTH_SHORT);
                                }
                            }
                        }, 16000);
                    }
                }, "注意", "请取出所有比色皿并保持分光孔清洁，点击“确认”后将进行分光模块复位和校准").show();
                btPrint.setEnabled(false);
                btStart.setEnabled(false);


                break;
            case R.id.bt_detect_start:
                int sum = 0;
                for (PhotometerView pr : pmViews) {
//				pr.reset();
                    pr.setProj(proj);
                    if (pr.isUse()) {
                        sum++;
                    }
                }
                if (sum == 0) {
                    ToastUtil.showText("请选择使用的通道", Toast.LENGTH_SHORT);
                    return;
                }
                LogUtil.d(proj + "标曲：" + proj.getFunction());
                if (null == proj || null == proj.getFunction()) {
                    new MyDialog(this, new BackListener() {
                        @Override
                        public void back() {
                            detectStart();
                        }
                    }, "提示", "所选检测项目并未设定标曲，是否仍然继续检测？").show();

                } else {
                    detectStart();
                }


                break;
            case R.id.bt_detect_print:
                final List<IData> datas = new ArrayList<>();
                for (PhotometerView pr : pmViews) {
                    if (pr.isUse()) {
                        PhotometerData data = pr.getData();
                        if (data == null || data.getResult() == null)
                            continue;
                        datas.add(data);
                    }
                }
                if (datas.size() == 0) {
                    ToastUtil.showText("请选择需要打印的通道", Toast.LENGTH_SHORT);
                    return;
                }

                PrinterJPW.showPrintChoice(this, datas, v);
                break;

            default:
                super.onClick(v);
                break;
        }

    }

    private boolean detecting = false;

    private void detectStart() {
        btPrint.setEnabled(false);
//			DataModel.getInstance().detectData(handler, light, DataModel.AB,10*1000);  //之前便已经开始了采集
        detecting = true;
        new Thread() { //会导致一些内存泄漏。
            public void run() {
                int ms = 10000;
                Message msg = handler.obtainMessage();
                msg.what = Consts.DETECT_TIME;
                msg.arg1 = ms / 1000;
                msg.sendToTarget();
                while (ms > 0) {
                    SystemClock.sleep(50);
                    ms -= 50;
                    if (!detecting) {
                        return;
                    }
                }
                handler.sendEmptyMessage(Consts.MSG_READ_END);
                DataModel.getInstance().stopPhotometer(PHOTOMETER_MODULES);
            }

            ;
        }.start();
        btStart.setEnabled(false);
        state = 2;

        Tone.get().play(R.raw.start);
    }

    private int state = 0;

    private void resetAll() {
        for (PhotometerView pr : pmViews)
            // pr.reset();
            pr.clear();
        for (int i = 0; i < 2; i++) {  //其实是24个通道一起发，只用到了timers[0]
            timers[i] = 0;
        }
    }

    private void applyChannelDisplayMode() {
        switch (CHANNEL_DISPLAY_MODE) {
            case CHANNEL_DISPLAY_ALL:
                activeChannelStart = 0;
                activeChannelEnd = 24;
                llUP.setVisibility(View.VISIBLE);
                llDown.setVisibility(View.VISIBLE);
                break;
            case CHANNEL_DISPLAY_13_TO_24:
                activeChannelStart = 12;
                activeChannelEnd = 24;
                llUP.setVisibility(View.GONE);
                llDown.setVisibility(View.VISIBLE);
                break;
            case CHANNEL_DISPLAY_1_TO_12:
            default:
                activeChannelStart = 0;
                activeChannelEnd = 12;
                llUP.setVisibility(View.VISIBLE);
                llDown.setVisibility(View.GONE);
                break;
        }
        for (int i = 0; i < pmViews.length; i++) {
            if (!isActiveViewIndex(i)) {
                pmViews[i].setChecked(false);
            }
        }
        cbAll.setChecked(false);
    }

    ProDialog2 proDialog;

    private void initProDialog() {
        try {
            proDialog = new ProDialog2(this, new ProDialog2.CancelListener() {
                @Override
                public void onCancel() {
                    detecting = false;
                    DataModel.getInstance().stopPhotometer(PHOTOMETER_MODULES);
                    Tone.get().play(R.raw.cancel_succeed);
                    proDialog.dismiss();
                }
            });
            // proDialog.setTitle(R.string.detecting);
            // proDialog.setMessate(R.string.detecting_and_dont_operate);
            // proDialog.setProgress(10);
            proDialog.setCancelable(false);
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
    }

    /**
     * 获取需要使用的通道
     *
     * @return
     */
//	private int getAB() {
//		boolean a = false, b = false;
//		for (int i = pmViews.length - 1; i >= 0; i--) {
//			PhotometerView pr = pmViews[i];
//			if (pr.isUse()) {
//				if (i >= pmViews.length / 2) { // 需要使用B通道
//					b = true;
//					i = pmViews.length / 2;
//				} else { // 需要使用A通道
//					a = true;
//					break;
//				}
//			}
//		}
//		if (a && b) {
//			return DataModel.AB;
//		} else if (a) {
//			return DataModel.A;
//		} else if (b) {
//			return DataModel.B;
//		}
//		return -1;
//	}
    private void setMode() {
        switch (rgMode.getCheckedRadioButtonId()) {
            case R.id.rb_contrast:// 对照
                cbAll.setChecked(false);
                for (PhotometerView pr : pmViews)
                    pr.setMode(PhotometerView.MODE_CONSTRUCT);
                pmViews[activeChannelStart].setChecked(true);
                break;
            case R.id.rb_detect:// 检测
                for (PhotometerView pr : pmViews)
                    pr.setMode(PhotometerView.MODE_DETECT);
                break;
        }
    }


    private void isNullCheck() {
        for (PhotometerView pr : pmViews) {
            if (pr.isUse())
                return;
        }
        dataModel.stopPhotometer(PHOTOMETER_MODULES);
        proDialog.dismiss();
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        DataModel.getInstance().stopPhotometer(PHOTOMETER_MODULES); //换模块，改协议了。
        handler.removeCallbacksAndMessages(null);
        // bcManager.unregisterReceiver(receiver);
        if (null != proDialog) {
            proDialog.cancel();
        }
        for (PhotometerView prv : pmViews) {
            if (null != prv) {
                prv.destroy();
            }
        }
        super.onDestroy();
    }


    /**
     * 万次自动循环测试使用
     */
    private Runnable cyclicTest = new Runnable() {
        @Override
        public void run() {
            try {
                btStart.callOnClick();
            } catch (Exception e) {
                ExceptionHandler.handleException(e);
            }
        }
    };

}
