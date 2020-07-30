package com.dadac.testrosbridge;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.dadac.testrosbridge.R;
/**
 * @author ethan.rublee@gmail.com (Ethan Rublee)
 * @author damonkohler@google.com (Damon Kohler)
 */
public class ControlRobotArmActivity extends Activity {

    public WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_robot_arm);
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
        webview.loadUrl("file:///android_asset/ros_web1/joint_state_publisher.html");
    }
}
