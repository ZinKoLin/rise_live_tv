package com.rise.live.Activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.rise.live.R;

public class Web2Activity extends AppCompatActivity {
    WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web2);

        webview = findViewById(R.id.webveiw3);
        webview.setWebViewClient(new WebViewClient());
        webview.setWebChromeClient(new FullScreenClient(Web2Activity.this) {
            @Override
            public void onHideCustomView() {

                hideFullScreen(webview);
            }

            @Override
            public Bitmap getDefaultVideoPoster() {

                return videoBitmap();
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                showFullScreen(view, callback);
            }
        });
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webview.loadUrl("https://socolive2.live");
    }

    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {

            webview.goBack();
        }
        else{
            super.onBackPressed();
        }

    }
}