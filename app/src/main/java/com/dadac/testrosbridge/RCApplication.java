package com.dadac.testrosbridge;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.BuildConfig;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.rosbridge.utils.RosbridgeUtils;

/**
 * @ Create by dadac on 2018/10/8.
 * @Function:
 * @Return:
 */
public class RCApplication extends Application {


    public static Context mContext;
    public RosbridgeUtils rosbridgeUtils=RosbridgeUtils.getInstance();

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        initCrashAndLog();
        mContext=getApplicationContext();
        handleCamera();
    }

    private static RCApplication app;
    public static RCApplication getInstance() {
        return app;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void handleCamera(){
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    private void initCrashAndLog(){
        //默认初始化
        //Logger.addLogAdapter(new AndroidLogAdapter());
        //修改全局的TAG
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .tag("ros_container")
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
        //保存本地文件
        //Logger.addLogAdapter(new DiskLogAdapter());
        //如果，我们希望在Debug环境下输出Log，而正式上线之后不输出Log日志
        // ，那么可以通过重写isLoggable方法，很方便的进行控制
        Logger.addLogAdapter(new AndroidLogAdapter() {
            @Override public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }
        });
    }
}



