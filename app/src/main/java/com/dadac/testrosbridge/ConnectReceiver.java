package com.dadac.testrosbridge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;

public class ConnectReceiver extends BroadcastReceiver {

    private static final String TAG="NetReceiver";

    private static long WIFI_TIME=0;
    private static long ETHERNET_TIME=0;
    private static long NONE_TIME=0;

    private static int LAST_TYPE=-3;

    @Override
    public void onReceive(Context context, Intent intent) {
//        if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
//            long time=getTime();
//            if(time!=WIFI_TIME&&time!=ETHERNET_TIME&&time!=NONE_TIME){
//                final int netWorkState=getNetWorkState(context);
//                if(netWorkState==0&&LAST_TYPE!=0){
//                    Toast.makeText(context, "wifi", Toast.LENGTH_SHORT).show();
//                    WIFI_TIME=time;
//                    LAST_TYPE=netWorkState;
//                    //Logger.e("wifi"+time);
//                    startCount("wifi");
//                }else if(netWorkState==1&&LAST_TYPE!=1){
//                    ETHERNET_TIME=time;
//                    Toast.makeText(context, "数据网络", Toast.LENGTH_SHORT).show();
//                    LAST_TYPE=netWorkState;
//                    //Logger.e("数据网络"+time);
//                    startCount("数据网络");
//                }else if(netWorkState==-1&&LAST_TYPE!=-1){
//                    NONE_TIME=time;
//                    Toast.makeText(context, "无网络", Toast.LENGTH_SHORT).show();
//                    LAST_TYPE=netWorkState;
//                    //Logger.e("无网络"+time);
//                    //EventBus.getDefault().post("无网络");
//                    startCount("无网络");
//                }
//            }
//        }
    }

    MyThread myThread;
    private void startCount(String name){
       // Logger.e("网络改变:"+name);
        if(timeCounts==0){
            timeCounts=3;
            myThread=new MyThread();
            myThread.start();
        }else {
            timeCounts=3;
            if(myThread==null||myThread.isInterrupted()){
                myThread=new MyThread();
                myThread.start();
                //Logger.e("myThread.isInterrupted():");
            }
        }
    }

    private int timeCounts=0;
    public class MyThread extends Thread{
        @Override
        public void run() {
            //Logger.e("开始循环");
            while (timeCounts!=0){
                try {
                    Thread.sleep(1000);
                    timeCounts--;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            EventBus.getDefault().post("网络改变");
            timeCounts=0;
            myThread.interrupt();
            myThread=null;
        }
    }

    public long getTime(){
        SimpleDateFormat sDateFormat    =   new SimpleDateFormat("yyyyMMddhhmmss");
        String    date    =    sDateFormat.format(new    java.util.Date());
        return Long.valueOf(date);
    }

    private static final int NETWORK_NONE=-1; //无网络连接
    private static final int NETWORK_WIFI=0; //wifi
    private static final int NETWORK_MOBILE=1; //数据网络

    public static int getNetWorkState(Context context){
        ConnectivityManager connectivityManager=(ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo=connectivityManager.getActiveNetworkInfo();
        if(activeNetworkInfo!=null&&activeNetworkInfo.isConnected()){
            if(activeNetworkInfo.getType()==(ConnectivityManager.TYPE_WIFI)){
                return NETWORK_WIFI;
            }else if(activeNetworkInfo.getType()==(ConnectivityManager.TYPE_MOBILE)){
                return NETWORK_MOBILE;
            }
        }else{
            return NETWORK_NONE;
        }
        return NETWORK_NONE;
    }
}
