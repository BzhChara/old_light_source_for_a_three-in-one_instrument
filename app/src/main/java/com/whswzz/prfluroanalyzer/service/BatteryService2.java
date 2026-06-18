package com.whswzz.prfluroanalyzer.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.extractor.CommandLineTextExtractor;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.consts.Consts;
import com.whswzz.prfluroanalyzer.consts.Instruct;
import com.whswzz.prfluroanalyzer.entity.PowerEntity;
import com.whswzz.prfluroanalyzer.param.Params;
import com.zkzk.pra.utils.ExceptionHandler;
import com.zkzk.pra.utils.Tools;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.Log;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import top.jemen.model.ComM;
import top.jemen.utils.LogUtil;

@SuppressLint("NewApi")
public class BatteryService2 extends IntentService {
    int oldState = -1;
    int[] levels = new int[8];
    int i = 0;// 用以标示level数组
    int level = -1;
    int old = level;
    UserHandle current = null;
    //	ArrayList<Byte> buf = new ArrayList<Byte>();// 緩存讀取的數據
    // Intent bcIntent=new
    // Intent(Intent.ACTION_BATTERY_CHANGED);//如果用系统的广播action，接收器在注册的时候会收到一次0
    Intent bcIntent = new Intent(Consts.JEMEN_BATTERY_CHANGED);

    public BatteryService2() {
        super("BatteryService2");
    }

    public BatteryService2(String name) {
        super(name);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            if (!Tools.isSMDK()) {
                Class cla = UserHandle.class;
                current = (UserHandle) cla.getField("CURRENT").get(cla);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void setIntentRedelivery(boolean enabled) {
        super.setIntentRedelivery(enabled);
        System.out.println("setIntentRedelivery");
    }

    private int x = 0;

    @Override
    protected void onHandleIntent(Intent intent) {
        LogUtil.d("BatteryService2 handleIntent");
        try {
            Thread.sleep(2);
            byte[] power = {(byte) 0xa5, 0x5a, 0, 0x06, 0x02, 0x00, 0x00, 0};
            Thread.sleep(2);
            // 大循环开始
            while (true) {
                if (Params.TEMP_UPDATE && (x++ % 3 == 0)) {
                    ComM.get().send(Instruct.TEMP_ENZYME); //会略微影响下定时的精度。
                } else {
                    ComM.get().send(power);// 阻塞方式此处会阻塞线程
                }
                SystemClock.sleep(1000);

            } // 最外层while循环，
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    /**
     * 读取电量
     *
     * @return
     */
    @Subscribe(threadMode = ThreadMode.BackgroundThread)
    public void readBettery(PowerEntity pe) {
//		LogUtil.d("收到电量数据"+pe);
        try {
            byte[] rcv = pe.getBs();// n.Read(fd); //用阻塞读取的话,其实循环不怎么必要。
            if (null == rcv) {
                return;
            }
            /**** 兼榮有多個0xAA的情況 */
            byte state = rcv[5];
            if (state != oldState) { // 状态变化
                oldState = state;
                switch (state) {
                    case 0x0: // 电量
                        level = rcv[6] & 0xff;
                        bcIntent.putExtra("level", level);
                        break;
                    case 0x01: // 电路故障
                        bcIntent.putExtra("level", -1);
                        break;
                    case 0x02: // 电池故障
                        bcIntent.putExtra("level", -2);
                        break;
                    case 0x10: // 正常充电
                        bcIntent.putExtra("level", 200);
                        break;
                    case 0x11: // 充电结束
                        bcIntent.putExtra("level", 300);
                        break;
                }
                if (null == current) {
                    sendBroadcast(bcIntent);
                }else {
                    sendBroadcastAsUser(bcIntent, current);
                }

            } else if (state == 0) { // 状态未改变并且状态位是0(电量上报)
                levels[i] = rcv[6] & 0xff;
                if (i == levels.length - 1) {
                    Arrays.sort(levels);
                    level = (levels[1] + levels[2] + levels[3] + levels[4] + levels[5] + levels[6]) / 6;// 取中间三数的平均值
                    if (level != old) {
                        old = level;
                        bcIntent.putExtra("level", level);
                        if (null == current)
                            sendBroadcast(bcIntent);
                        else
                            sendBroadcastAsUser(bcIntent, current);
                    }
                }
                i++;
                if (i >= levels.length)
                    i = 0;
            }
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
    }

    private void addAll(List<Byte> list, byte[] bytes) {
        for (byte b : bytes)
            list.add(b);
    }

    private static int skip = 0;

    public static void skip(int s) {
        if (s > skip && skip < 100) {
            skip = s;
        }
    }

}
