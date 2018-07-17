package com.dldud.riceapp;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Privacy extends AppCompatActivity {

    ProgressDialog progressDialog = null;
    String privacyUrl = "http://52.78.18.156/public/privacy.php";
    WebView privacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);


        privacy = (WebView)findViewById(R.id.privacy);
        WebSettings webSettings = privacy.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);

        privacy.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                view.loadUrl(url);
                return true;
            }
        });
        privacy.loadUrl(privacyUrl);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(privacy.canGoBack()){
                privacy.goBack();
                return false;
            }
        }
        return super.onKeyDown(keyCode,event);
    }
}
