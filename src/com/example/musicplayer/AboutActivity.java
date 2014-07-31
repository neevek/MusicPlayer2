package com.example.musicplayer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by neevek on 7/31/14.
 */
public class AboutActivity extends Activity {
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        initWebView();

        mWebView.loadUrl("file:///android_asset/hello.html");

//        String html = "<html><body>A simple <b>WebView</b> demo</body></html>";
//        mWebView.loadData(html, "text/html", null);
    }

    private void initWebView() {
        mWebView = (WebView)findViewById(R.id.wv_browser);

        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setScrollbarFadingEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

        WebSettings webSettings = mWebView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setSupportZoom(true);
//        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        webSettings.setAppCachePath(mWebView.getContext().getFilesDir() + "/webcache");
//        webSettings.setAppCacheEnabled(true);
//        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setDomStorageEnabled(true); // supports local storage
        webSettings.setDatabaseEnabled(true);   // supports local storage
        webSettings.setDatabasePath(mWebView.getContext().getFilesDir() + "/localstorage");
    }


}
