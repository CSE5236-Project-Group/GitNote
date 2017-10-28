package com.project.cse5326.gitnote.Github.Auth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.HttpAuthHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.project.cse5326.gitnote.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AuthActivity extends AppCompatActivity{

    public static final String KEY_URL = "url";
    public static final String KEY_CODE = "code";

    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.webview) WebView webView;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Sign in Github");

        progressBar.setMax(100);

        // Start Webview

        webView.getSettings().setJavaScriptEnabled(true);
//        CookieManager.getInstance().setAcceptCookie(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i("LOGIN", url);
               if (url.startsWith(Auth.REDIRECT_URI)) {
                   Uri uri = Uri.parse(url);
                   Intent intent = new Intent();
                   intent.putExtra(KEY_CODE, uri.getQueryParameter(KEY_CODE));
                   Log.i("code", uri.getQueryParameter(KEY_CODE));
                   setResult(RESULT_OK, intent);
                   finish();
               }
               return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(0);
            }
        });

        // progress bar
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
            }
        });

        String url = getIntent().getStringExtra(KEY_URL);
        webView.loadUrl(url);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
