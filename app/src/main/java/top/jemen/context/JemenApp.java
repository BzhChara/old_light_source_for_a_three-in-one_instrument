package top.jemen.context;

import android.app.Application;

import androidx.multidex.MultiDexApplication;


/**
 * 作为MyApp的基类，用于初始化一些全局变量和组件
 */
public class JemenApp extends MultiDexApplication {
    private static JemenApp app;
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    public static JemenApp get() {
        return app;
    }
}
