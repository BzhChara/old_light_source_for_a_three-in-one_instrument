package com.whswzz.prfluroanalyzer.fluoro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.xutils.x;
import org.xutils.view.annotation.ViewInject;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.app.Target;
import com.whswzz.prfluroanalyzer.base.BaseActivity;
import com.whswzz.prfluroanalyzer.consts.Consts;
import com.whswzz.prfluroanalyzer.entity.IData;
import com.whswzz.prfluroanalyzer.fluoro.dal.imp.XDao;
import com.whswzz.prfluroanalyzer.fluoro.entity.FluData;
import com.whswzz.prfluroanalyzer.fluoro.ui.CollaurumSpecimenDialog;
import com.whswzz.prfluroanalyzer.fluoro.ui.CollaurumView;
import com.whswzz.prfluroanalyzer.fluoro.uvc.CameraTool;
import com.whswzz.prfluroanalyzer.fluoro.uvc.CameraTool2;
import com.whswzz.prfluroanalyzer.fluoro.uvc.CameraTool2.CamreaCallback;
import com.whswzz.prfluroanalyzer.model.DataModel;
import com.whswzz.prfluroanalyzer.model.HttpModel;
import com.whswzz.prfluroanalyzer.param.Params;
import com.whswzz.prfluroanalyzer.utils.PrinterJPW;
import com.whswzz.prfluroanalyzer.utils.UiUtil;
import com.zkzk.pra.R;
import com.zkzk.pra.utils.ToastUtil;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import top.jemen.interfaces.ACallback;
import top.jemen.interfaces.ICallback;
import top.jemen.utils.LogUtil;
import top.jemen.utils.QRCodeUtil;
import top.jemen.utils.Tone;
import top.jemen.utils.CameraUtil.CameraPreview;
import top.jemen.utils.threadpool.AsyncProcessor;
import top.jemen.utils.ColorUtil;
import top.jemen.utils.CurveUtil;

public class FluoroActivity extends BaseActivity implements OnClickListener {

    @ViewInject(R.id.bt_start)
    private Button btStart;
    @ViewInject(R.id.iv_fluoro)
    private ImageView ivFluoro;

    private static final int W = 640, H = 480;
    private Paint bgPaint;
    private Paint paint;
    @ViewInject(R.id.sp_channels)
    private Spinner spChannels;
    @ViewInject(R.id.ll_collaurum_up)
    LinearLayout llUp;
    @ViewInject(R.id.ll_collaurum_down)
    LinearLayout llDown;

    @ViewInject(R.id.bt_upload)
    private Button btUpload;
    @ViewInject(R.id.bt_print)
    private Button btPrint;

    @ViewInject(R.id.cb_all)
    private CheckBox cbAll;
    @ViewInject(R.id.bt_all_msg)
    private Button btMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fluoro);
        x.view().inject(this);
        init();
        setListeners();
        // CameraTool.get().ini(ivFluoro);
        // addCollaurum(6);
        DataModel.getInstance().lightOn(Params.LIGHT);
        Tone.get().play(R.raw.choice_card_first);
        int spindex = MyApp.getApp().getPref().getInt(Consts.PROJ_INDEX, 0);
        spChannels.setSelection(spindex);
    }

    @Override
    protected void onDestroy() {
        DataModel.getInstance().lightOn(0);
        super.onDestroy();
    }

    private int lines = 2;
    private int chanelI = 0;

    private void init() {
        // bitmap = Bitmap.createBitmap(W, H, Config.ARGB_8888);
        // canvas = new Canvas(bitmap);
        bgPaint = new Paint();
        bgPaint.setColor(Color.BLACK);
        bgPaint.setStyle(Style.STROKE);
        bgPaint.setStrokeWidth(3);
        bgPaint.setAntiAlias(true);

        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);
        paint.setTextSize(88);

        btMsg.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        String[] channels = {"六联卡", "三联卡", "单卡", "十二联卡"};
        ArrayAdapter<String> aad = new ArrayAdapter<>(this, R.layout.my_spinner, channels);
        aad.setDropDownViewResource(R.layout.my_spinner_dropdown);
        spChannels.setAdapter(aad);
        spChannels.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                llUp.removeAllViews();
                llDown.removeAllViews();
                collaurums.clear();
                chanelI = position;
                switch (position) {
                    case 0: // 默认六通道
                        lines = 2;
                        addCollaurum(6, lines);
                        btMsg.setVisibility(View.VISIBLE);
                        break;
                    case 1:// 三通道
                        lines = 2;
                        addCollaurum(3, lines);
                        btMsg.setVisibility(View.GONE);
                        break;
                    case 2:// 单通道
                        lines = 2;
                        addCollaurum(1, lines);
                        btMsg.setVisibility(View.GONE);
                        break;
                    case 3: // 十二通道
                        lines = 3;
                        addCollaurum(12, lines);
                        btMsg.setVisibility(View.VISIBLE);
                        break;
                }
                LogUtil.d("list size:" + collaurums.size() + ",llUp size=" + llUp.getChildCount() + ",lldong size="
                        + llDown.getChildCount());

                MyApp.getApp().getPref().edit().putInt(Consts.PROJ_INDEX, position).apply();
            }
        });
    }

    private List<CollaurumView> collaurums = new ArrayList<>();
    private String[] projs6 = {"噻虫嗪", "腐霉利", "百菌清", "克百威", "多菌灵", "啶虫脒"};
    private String[] projs12 = {"克百威", "灭多威", "三唑磷", "水胺硫磷", "氟虫腈", "甲氰菊酯", "百菌清", "阿维菌素", "多菌灵", "吡唑醚菌酯", "灭蝇胺",
            "氯虫苯甲酰胺"};

    private void addCollaurum(int num, int lines) {

        for (int i = 0; i < num; i += lines - 1) {
            String[] channels = new String[lines - 1];
            for (int j = 0; j < lines - 1; j++) {
                channels[j] = "" + (1 + i + j);
            }
            LogUtil.d("channels=" + Arrays.toString(channels));
            CollaurumView llCollaurum = new CollaurumView(this, channels);
            if (llUp.getChildCount() < 3) {
                llUp.addView(llCollaurum);
            } else {
                llDown.addView(llCollaurum);
            }

            collaurums.add(llCollaurum);
            if (num == 6) {
                llCollaurum.setProj(projs6[i]);
                llCollaurum.recoverProjSize();
            } else if (num == 12) {
                llCollaurum.setProjSize(18);
                llCollaurum.setProj(projs12[i], projs12[i + 1]);
            } else {
                llCollaurum.recoverProjSize();
            }

        }
        if (llDown.getChildCount() == 0) {
            ((LinearLayout.LayoutParams) llDown.getLayoutParams()).weight = 0;
        } else {
            ((LinearLayout.LayoutParams) llDown.getLayoutParams()).weight = 1;
        }

    }

    private void setListeners() {
        btStart.setOnClickListener(this);
        btUpload.setOnClickListener(this);
        btMsg.setOnClickListener(this);
        btPrint.setOnClickListener(this);
        cbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (CollaurumView cv : collaurums) {
                    cv.setChecked(isChecked);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_start:
                btStart.setEnabled(false);
                spChannels.setEnabled(false);
                if (null != canvas) {
                    canvas.drawColor(Color.WHITE);
                    String text = "请稍候";
                    float tw = paint.measureText(text);
                    canvas.drawText("请稍候", (canvas.getWidth() - tw) / 2, canvas.getHeight() / 2, paint);
                }
                for (CollaurumView cv : collaurums) {
                    cv.reset();
                }

                CameraTool2.shotX(50, new CamreaCallback() { // 回调在主线程
                    // CameraTool2.shotXTimes(2,0, new CamreaCallback() {
                    @Override
                    public void onSuccess(final Bitmap[] bmps) {
                        Bitmap bmpx = bmps[bmps.length - 1];
                        int w = bmpx.getWidth(), h = bmpx.getHeight();
                        LogUtil.d("w,h:" + w + "," + h);

                        bmpBg = Bitmap.createBitmap(w, h, bmpx.getConfig());

                        ivFluoro.setVisibility(View.INVISIBLE);
                        canvas = new Canvas(bmpBg);
                        canvas.drawBitmap(bmpx, 0, 0, bgPaint); // 必须新建一个画上去，否则不能修改。
                        segment(bmps); // 切片

                        AsyncProcessor.executeTask(new Runnable() {
                            @Override
                            public void run() {
                                bgPaint.setTextSize(h / 15);
                                for (Bitmap bmp : bmps) {
                                    String qrmsg = QRCodeUtil.simpleDecode(bmp);
                                    if (null != qrmsg) {
                                        canvas.drawText(qrmsg, 10, h / 10, bgPaint);
                                        break;
                                    }
                                    LogUtil.d("qrmsg=" + qrmsg);
                                }
                            }
                        });


                        // String msg="";
                        // for(Bitmap b:bmps) {
                        // msg+=b+": w:"+b.getWidth()+" ,h:"+b.getHeight()+"\n";
                        // }
                        // LogUtil.d("result:"+msg);
                    }

                    @Override
                    public void onFailed(String obj) {
                        ToastUtil.showText("" + obj, Toast.LENGTH_SHORT);
                        Tone.get().play(R.raw.dingdong906);
                        btStart.setEnabled(true);
                        spChannels.setEnabled(true);
                    }
                });

                break;

            case R.id.bt_upload:
                v.setEnabled(false);
                List<IData> ls = new LinkedList<>();
                for (CollaurumView cv : collaurums) {
                    if(cv.getVisibility() == View.VISIBLE)
                     ls.addAll(cv.getCheckedData());
                }
                if (ls.size() < 1) {
                    ToastUtil.showText("请选择需要上传的通道并检测完成", Toast.LENGTH_SHORT);
                    return;
                }
                HttpModel.get().send(ls, new ICallback() {
                    @Override
                    public void onSuccess(Object obj) {
                        ToastUtil.showText("数据上传成功", Toast.LENGTH_SHORT);
                        v.setEnabled(true);
                    }

                    @Override
                    public void onFailed(Object obj) {
                        ToastUtil.showText("数据上传失败:" + obj , Toast.LENGTH_SHORT);
                        v.setEnabled(true);
                    }
                });

                break;
            case R.id.bt_print:
                ls = new LinkedList<>();
                for (CollaurumView cv : collaurums) {
                    ls.addAll(cv.getCheckedData());
                }
                if (ls.size() < 1) {
                    ToastUtil.showText("请选择需要打印的通道并检测完成", Toast.LENGTH_SHORT);
                    return;
                }
                btPrint.setEnabled(false);
                PrinterJPW.showPrintChoice(this, ls, btPrint);
                break;

            case R.id.ib_bottom_back:
            case R.id.ib_bottom_home:
                if (!btStart.isEnabled()) {
                    ToastUtil.showText("检测进行中，请稍候", Toast.LENGTH_SHORT);
                    return;
                }
                super.onClick(v);
                break;
            case R.id.bt_all_msg:
                if (null == allMsgDialog) {
                    FluData d = new FluData();
                    allMsgDialog = new CollaurumSpecimenDialog(this, d, new ICallback() {
                        @Override
                        public void onSuccess(Object obj) {
                            for (CollaurumView cv : collaurums) {
                                cv.setMsg((FluData) obj);
                            }
                        }

                        @Override
                        public void onFailed(Object obj) {
                        }
                    });
                }
                allMsgDialog.show();
                break;
            default:
                super.onClick(v);
        }
    }

    private CollaurumSpecimenDialog allMsgDialog;

    private Canvas canvas;
    Bitmap bmpBg;
    private float[][] borders = Params.getBorders();

    private void segment(final Bitmap[] bmps) {
        AsyncProcessor.executeTask(new Runnable() {
            @Override
            public void run() {
                int w = canvas.getWidth();
                int h = canvas.getHeight();
                // float l = 0.19f, t = 0.45f, dx = 0.11f, b = 0.69f,w=0.027; // 六连卡的默认位置
                // float l = 0.2879f, t = 0.4f, dx = 0.083f, b = 0.59f,lw=0.023f; // 六连卡的默认位置
                float l = borders[chanelI][0], t = borders[chanelI][1], dx = borders[chanelI][2],
                        b = borders[chanelI][3], lw = borders[chanelI][4]; // 六连卡的默认位置
                int size = collaurums.size();
                bgPaint.setColor(Color.RED);
                for (int i = 0; i < size; i++) { //各个通道
                    float left = w * (l + i * dx);
                    float top = h * t;
                    float right = w * (l + lw + i * dx);
                    float bottom = h * b;
                    canvas.drawRect(left, top, right, bottom, bgPaint);

                    ArrayList<Float> values = new ArrayList<Float>();
                    for (int x = (int) (top + 6); x < bottom - 6; x++) { //纵向采样
                        List<Float> row = new ArrayList<Float>();
                        for (int p = 0; p < bmps.length; p++) {
                            Bitmap bmp = bmps[p];
                            float sum = 0;
                            for (int y = (int) (left + 3); y < right - 3; y += 1) { //采样宽度

                                sum += ColorUtil.absorb530(bmp.getPixel(y, x)); // 注意xy反的。
                                sum += ColorUtil.absorb530(bmp.getPixel(y, x - 1)); // 注意xy反的。
                                sum += ColorUtil.absorb530(bmp.getPixel(y, x + 1)); // 注意xy反的。
                                sum += ColorUtil.absorb530(bmp.getPixel(y, x - 2)); // 注意xy反的。
                                sum += ColorUtil.absorb530(bmp.getPixel(y, x + 2)); // 注意xy反的。

                            }
//                            float a = sum / ((right - left - 6) / 5);
                            row.add(sum);
                        }
                        if(row.size()>1) {
                            Collections.sort(row);
                            float sum = 0;
                            for (int k = (int) (row.size() * 0.3); k < row.size() * 0.7; k++) {
                                sum += row.get(k);
                            }
                            values.add(sum);
                        }else if(row.size()==1){
                            values.add(row.get(0));
                        }
                    }

                    final Bitmap bmp = CurveUtil.showPeak(values, collaurums.get(i).getData(), i, lines);
                    if (null == bmp) {
                        LogUtil.d("绘制图谱失败");
                        continue;
                    }
                    final int finalI = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            collaurums.get(finalI).setBitmap(bmp);
                        }
                    });
                    collaurums.get(i).saveData();
                }
                Tone.get().play(R.raw.analyze_complete);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.d(Arrays.toString(borders[0]));
//						bmpBg = Bitmap.createBitmap(bmpBg, (int) (bmpBg.getWidth() * (borders[0][0] - 0.02)),
//								(int) (bmpBg.getHeight() * borders[0][1]),
//								(int) (bmpBg.getWidth() * (1 - borders[0][0] * 2)),
//								(int) (bmpBg.getHeight() * (borders[0][3] - borders[0][1]))); // 调试时候关着。

                        ivFluoro.setImageBitmap(bmpBg);
                        ivFluoro.setVisibility(View.VISIBLE);
                        btStart.setEnabled(true);
                        spChannels.setEnabled(true);
                    }
                });
            }

        });

    }

}
