package com.whswzz.prfluroanalyzer.fluoro.uvc;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.fluoro.entity.TC;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import top.jemen.utils.LogUtil;

public class History {
    private static List<TC> ls = new LinkedList<TC>();


    static {
        loadHistory();
    }
    public static TC historyRepare(TC o) {
        if (ls.size() < 5) {
            ls.add(o);
            saveHistory();
            return o;
        }
        List<TC> buf = new LinkedList<TC>();
        double threshold = o.t / o.c < 0.09 ? 0.1 : 0.05;
        for (TC tc : ls) {
            if (Math.abs(tc.t - o.t) < o.t * threshold && Math.abs(tc.c - o.c) < o.c * 0.1
                    && Math.abs(tc.t / tc.c - o.t / o.c) < o.t / o.c * threshold) {
                buf.add(tc);
            }
        }
        if (buf.size() < 3) {
            saveHistory();
            return o;
        }
        if (buf.size() >= 6) {
            Map<Double, TC> map = new TreeMap<Double, TC>();
            for (TC tc : buf) {
                map.put(Math.abs(tc.t - o.t) / o.t * 0.5 + Math.abs(tc.c - o.c) / o.c * 0.5, tc);
            }
            // LogUtil.d("treemap is:"+map);
            Collection<TC> vs = map.values();
            // LogUtil.d("teemap vs is:"+vs);
            for (TC tc : vs) {
                buf.add(tc);
                if (buf.size() >= 10 || buf.size() > vs.size() * 0.618) {
                    break;
                }
            }
        }

        float ac = 0, at = 0;
        for (TC tc : buf) {
            ac += tc.c;
            at += tc.t;
        }
        at /= buf.size();
        ac /= buf.size();
        float w = 0.6f;
        TC r = new TC(at * w + o.t * (1 - w), ac * w + o.c * (1 - w));
        LogUtil.d("历史矫正，原o=" + o.toString() + "，校正后：" + r);
        ls.add(o);
        if (ls.size() > 1000) {
            ls.remove(0);
        }
        saveHistory();
        return r;
    }

    static String path = MyApp.getApp().getFilesDir().getAbsolutePath() + "/.his";

    private static void loadHistory() {
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new FileInputStream(path));
            ls = (List<TC>) in.readObject();
        } catch (Exception e) {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private static void saveHistory() {
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(path));
            out.writeObject(ls);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }


    public static class TC implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        public float t;
        public float c;

        public TC(float t, float c) {
            super();
            this.t = t;
            this.c = c;
        }

        public String toString() {
            return "t:" + t + "\r c:" + c + " \n";

        }
    }



}


