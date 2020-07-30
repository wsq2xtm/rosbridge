package com.dadac.testrosbridge;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.dadac.testrosbridge.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class KeyboardTelepRobotActivity extends Activity {

    public WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard_telep_robot);
        webview=findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webview.loadUrl("file:///android_asset/ros_web1/keyboardteleop.html");
//        if (webview.isHardwareAccelerated())
//            webview.setLayerType(View.LAYER_TYPE_HARDWARE,null);
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
//            webview.getSettings().setAllowUniversalAccessFromFileURLs(true);
//            webview.getSettings().setAllowFileAccessFromFileURLs(true);
//            webview.getSettings().setAllowContentAccess(true);
//            webview.getSettings().setAppCacheEnabled(true);
//        }
//        WebSettings websettings = webview.getSettings();
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
//        {
//            enablecrossdomain41();
//
//            websettings.setAllowUniversalAccessFromFileURLs(true);
//            websettings.setAllowFileAccessFromFileURLs(true);
//
//        } else
//        {
//            enablecrossdomain();
//        }
        //keyboardteleop  joint_state_publisher
    }
}
