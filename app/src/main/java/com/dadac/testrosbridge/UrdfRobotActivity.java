package com.dadac.testrosbridge;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.dadac.testrosbridge.utils.JsInterface;
import com.example.dadac.testrosbridge.R;

public class UrdfRobotActivity extends Activity implements View.OnClickListener {

    public WebView webview;
    public Button bt_refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard_telep_robot);
        bt_refresh=findViewById(R.id.bt_refresh);
        bt_refresh.setOnClickListener(this);
        bt_refresh.setVisibility(View.GONE);
        webview=findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        //JS调用原生方法
        webview.addJavascriptInterface(new JsInterface(),"android");
        //原生调用js
        webview.setWebChromeClient(new WebChromeClient());

        webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webview.loadUrl("file:///android_asset/ros_web1/urdf.html");
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

    @Override
    public void onClick(View v) {
        webview.loadUrl("javascript:refresh()");
       // webview.loadUrl("javascript:alertMessage(\""+"66666"+"\")");
//        webview.evaluateJavascript("sum(1,2)",new ValueCallback<String>() {
//            @Override
//            public void onReceiveValue(String value) {
//                Toast.makeText(UrdfRobotActivity.this,"js返回结果："+value,
//                        Toast.LENGTH_LONG).show();
//            }
//        });
    }
}
