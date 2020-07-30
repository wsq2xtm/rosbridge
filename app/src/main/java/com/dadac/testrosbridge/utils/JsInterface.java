package com.dadac.testrosbridge.utils;

import android.webkit.JavascriptInterface;

import com.orhanobut.logger.Logger;

public class JsInterface {

    @JavascriptInterface
    public String getMasterIp(){
        return "192.168.3.12";
    }

    @JavascriptInterface
    public void postGoal(String goalJson){
        Logger.e("android接收到数据：："+goalJson);
    }
}
