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

public class MapActivity extends Activity implements View.OnClickListener {

    public WebView webview;
    public Button bt_refresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        bt_refresh=findViewById(R.id.bt_refresh);
        bt_refresh.setOnClickListener(this);
       // bt_refresh.setVisibility(View.GONE);
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
        webview.loadUrl("file:///android_asset/ros_web/navigation.html");
    }

    @Override
    public void onClick(View v) {
       //initService();
//       String svgString ="'<?xml version=\"1.0\" standalone=\"no\"?> <svg width=\"100%\" height=\"100%\" xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">  <text x=\"1089\" y=\"1025\" fill=\"red\">wayPoint_one</text> <rect x=\"1089\" y=\"1035\" height=\"10\" width=\"20\" stroke=\"blue\" stroke-width=\"1\" fill=\"red\" />  <rect x=\"1060\" y=\"1102\" height=\"10\" width=\"20\" stroke=\"blue\" stroke-width=\"1\" fill=\"green\" /> </svg>'";
//       String svgString ="'<?xml version=\"1.0\" standalone=\"no\"?><svg width=\"100%\" height=\"100%\" xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\"><text x=\"1089\" y=\"1025\" fill=\"red\">wayPoint_one</text><rect x=\"1089\" y=\"1035\" height=\"10\" width=\"20\" stroke=\"blue\" stroke-width=\"1\" fill=\"red\" /><rect x=\"1060\" y=\"1102\" height=\"10\" width=\"20\" stroke=\"blue\" stroke-width=\"1\" fill=\"green\" /></svg>'";
//        String jsMethod="drawSvg("+svgString+")";
//        webview.loadUrl("javascript:"+jsMethod);
        //webview.loadUrl("javascript:refresh()");
       //webview.loadUrl("javascript:sendGoalCallback(\'"+66666+"\')");
//        int a=30;
//        int b=80;
//        int with=10;
//        int height=8;
//        String c="\'站1\'";
//        String dd="drawSemantics("+a+","+b+","+with+","+height+","+c+")";
//        String ddd="drawLine("+a+","+b+","+with+","+height+","+c+")";
//        webview.loadUrl("javascript:"+dd);
//        webview.loadUrl("javascript:"+ddd);
//        webview.evaluateJavascript(dd,new ValueCallback<String>() {
//            @Override
//            public void onReceiveValue(String value) {
//                Toast.makeText(MapActivity.this,"js返回结果："+value,
//                        Toast.LENGTH_LONG).show();
//            }
//        });
//
//        String ddd="drawLine("+a+","+b+","+with+","+height+","+c+")";
//        webview.evaluateJavascript(ddd,new ValueCallback<String>() {
//            @Override
//            public void onReceiveValue(String value) {
//                Toast.makeText(MapActivity.this,"js返回结果："+value,
//                        Toast.LENGTH_LONG).show();
//            }
//        });
    }
}
