package com.m.freemovie.Activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.m.freemovie.R;


public class VideoWebviewActivity extends AppCompatActivity {
    private WebView webView;
    private ImageView btn_back,rotate;
    private boolean isRotate = false;
    private RelativeLayout rl_title;
    private boolean isVisible = false;
    private TextView title_name;
    private String title;
    private String videoId;
    private KProgressHUD hud;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_webview);
        getSupportActionBar().hide();
        webView = findViewById(R.id.webView);
        btn_back = findViewById(R.id.btn_back);
        title_name = findViewById(R.id.title_name);
        rl_title = findViewById(R.id.rl_title);
        rotate = findViewById(R.id.rotate);
        btn_back.setOnClickListener(view -> onBackPressed());
        title = getIntent().getStringExtra("title");
        videoId = getIntent().getStringExtra("videoId");
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait");

        title_name.setText(title);
        String videoUrl = "https://vidsrc-embed.ru/embed/movie?tmdb="+videoId;
//        Log.d("VideoUrl","value: "+videoUrl);
        rotate.setOnClickListener(view -> rotateScreen());

        if(!isNetworkAvailable()){
            webView.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(),"Please check your internet and try again",Toast.LENGTH_SHORT).show();
        }else{
            setupWebView(videoUrl);
            webView.setVisibility(View.VISIBLE);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void setupWebView(String videoUrl) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setSupportZoom(false);
        webSettings.setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                return handleUrlLoading(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return handleUrlLoading(view, url);
            }

            private boolean handleUrlLoading(WebView view, String url) {
                if (url.contains("vidsrc-embed.ru")) {
                    return false;
                } else {
                    view.stopLoading();
                    return true;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                if (!url.contains("vidsrc-embed.ru")) {
                    view.stopLoading();
                }
                super.onPageStarted(view, url, favicon);
                hud.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hud.dismiss();
            }
        });

        webView.loadUrl(videoUrl);
    }

    private void rotateScreen() {
        isRotate = !isRotate;
        if(!isRotate){
            rl_title.setBackgroundColor(Color.parseColor("#313647"));
            rl_title.setVisibility(View.VISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            isVisible = false;
        }else{
            if(!isVisible){
                rl_title.setBackgroundColor(Color.parseColor("#000000"));
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rl_title.setVisibility(isVisible ? View.VISIBLE : View.GONE);
                    }
                }, 300);
            }

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public void onBackPressed() {
        if (isRotate) {
            rl_title.setVisibility(View.VISIBLE);
            rl_title.setBackgroundColor(Color.parseColor("#313647"));
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            isRotate = false;
        } else {
            super.onBackPressed();
            finish();
        }
    }
}
