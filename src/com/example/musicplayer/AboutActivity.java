package com.example.musicplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.*;
import android.widget.Toast;

import java.util.regex.Pattern;

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

        mWebView.addJavascriptInterface(new MyJavascriptInterface(), "myjsobject");

        mWebView.loadUrl("file:///android_asset/hello.html");

//        String html = "<html><body>A simple <b>WebView</b> demo</body></html>";
//        mWebView.loadData(html, "text/html", null);
    }

    private void initWebView() {
        mWebView = (WebView)findViewById(R.id.wv_browser);

        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setScrollbarFadingEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebView.setWebChromeClient(new MyWebChromeClient());
        mWebView.setWebViewClient(new MyWebViewClient());

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


    class MyJavascriptInterface {

        // Caution: If you've set your targetSdkVersion to 17 or higher,
        // you must add the @JavascriptInterface annotation to any method
        // that you want available your web page code (the method must also be public).
        // If you do not provide the annotation, then the method will not
        // accessible by your web page when running on Android 4.2 or higher.
        @JavascriptInterface
        public String getAndroidVersion() {
            return Build.VERSION.RELEASE;
        }

        @JavascriptInterface
        public void showToast(String msg) {
            Toast.makeText(AboutActivity.this, msg, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void openSettingsActivity() {
            startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
        }
    }

    class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            new AlertDialog.Builder(view.getContext())
                    .setMessage(message).setCancelable(true).show();
            result.confirm();
            return true;
        }
    }

    class MyWebViewClient extends WebViewClient {
        Pattern neteaseRegex = Pattern.compile("^http://.*163\\.com.*");

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (neteaseRegex.matcher(url).matches()) {
                // let the webview handle the request
                return false;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
