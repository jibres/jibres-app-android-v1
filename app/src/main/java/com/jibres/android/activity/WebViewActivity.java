package com.jibres.android.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.jibres.android.JibresApplication;
import com.jibres.android.R;
import com.jibres.android.activity.intro.IntroActivity;
import com.jibres.android.api.Api;
import com.jibres.android.managers.AppManager;
import com.jibres.android.managers.UrlManager;
import com.jibres.android.utility.SecretReadFile;
import com.jibres.android.weight.BottomSheetFragment;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import im.delight.android.webview.AdvancedWebView;

public class WebViewActivity extends AppCompatActivity implements AdvancedWebView.Listener {

    String TAG = "amingoli";
    boolean bottomSheetIsShow = false;
    private AdvancedWebView mWebView;
    private ProgressBar progress;
    String url = "https://jibres.com/dashboard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Log.d(TAG, "onCreate: "+send_headers());

        url = getIntent().getStringExtra("url");

        progress = findViewById(R.id.progress);
        mWebView = findViewById(R.id.webview);
        progress.setVisibility(View.VISIBLE);
        mWebView.setVisibility(View.VISIBLE);
        mWebView.setListener(this, this);
        mWebView.loadUrl(url,send_headers());

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "shouldOverrideUrlLoading: "+url);
                if (!isNetworkConnected()){
                    showBottomSheetDialogFragment();
                }
                if (url.startsWith("jibres")){
                    Intent intent = null;
                    switch (url){
                        case "jibres://intro":
                            intent = new Intent(WebViewActivity.this,IntroActivity.class);
                            break;
                        case "jibres://test":
                            intent = new Intent(WebViewActivity.this,MainActivity.class);
                            break;
                        case "jibres://language":
                            view.loadUrl(UrlManager.language(getApplication()));
                            break;
                        default:
                            if (url.startsWith("jibres://language/")){
                                String language = url.replace("jibres://language/","");
                                AppManager.get(getApplicationContext()).setAppLanguage(language);
                                progress.setVisibility(View.VISIBLE);
                                Log.d(TAG, "Change Language: "+AppManager.getAppLanguage(getApplication()));
                                Api.endPoint(getApplicationContext(), status
                                        -> Api.android(getApplicationContext(), status1
                                        -> {
                                    view.loadUrl(UrlManager.dashboard(getApplication()));
                                }));

                            }
                            break;
                    }
                    if (intent!=null){
                        startActivity(intent);
                    }
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        // ...
    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
        // ...
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        mWebView.onPause();
        // ...
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mWebView.onDestroy();
        // ...
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        mWebView.onActivityResult(requestCode, resultCode, intent);
        Log.d(TAG, "onActivityResult: "+requestCode+"\n"+resultCode+"\n"+intent);
        // ...
    }

    @Override
    public void onBackPressed() {
        if (!mWebView.onBackPressed()) { return; }
        // ...
        super.onBackPressed();
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        progress.setVisibility(View.VISIBLE);
        if (url.equals(UrlManager.dashboard(this))){
            mWebView.clearHistory();
        }
        if (!isNetworkConnected()) showBottomSheetDialogFragment();
        Log.d(TAG, "onPageStarted: "+url);
    }

    @Override
    public void onPageFinished(String url) {
        Log.d(TAG, "onPageFinished: "+url);
        progress.setVisibility(View.GONE);
        if (isNetworkConnected())
            if (mWebView.getVisibility() != View.VISIBLE) mWebView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        if (description.endsWith("net::ERR_INTERNET_DISCONNECTED")){
            showBottomSheetDialogFragment();
        }
        Log.d(TAG, "onPageError: "+errorCode+"\n"+description+"\n"+failingUrl);
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) { }

    @Override
    public void onExternalPageRequest(String url) { }


    public void showBottomSheetDialogFragment() {
        if (!bottomSheetIsShow){
            progress.setVisibility(View.GONE);
            bottomSheetIsShow = true;
            mWebView.stopLoading();
            mWebView.setVisibility(View.GONE);
            BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
            bottomSheetFragment.setCancelable(false);
            bottomSheetFragment.setListener(()
                    -> {
                bottomSheetIsShow = false;
                mWebView.reload();
                bottomSheetFragment.dismiss();
            });
            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }


    private Map<String,String> send_headers(){
        Map<String,String> send_headers = new HashMap<>();
        send_headers.put("x-app-request", "android");
        send_headers.put("store", SecretReadFile.store(this));
        send_headers.put("versionCode",String.valueOf(AppManager.versionCode));
        send_headers.put("versionName",AppManager.versionName);
        send_headers.put("device",AppManager.getAppLanguage(getApplicationContext()));
        return send_headers;
    }
}