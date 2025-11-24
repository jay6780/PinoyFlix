package com.m.freemovie.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.m.freemovie.databinding.ActivityVideoWebviewBinding;


public class VideoWebviewActivity extends AppCompatActivity {
    private boolean isRotate = false;
    private boolean isVisible = false;
    private String title;
    private String videoId;
    private KProgressHUD hud;
    private ActivityVideoWebviewBinding binding;
    private int videoPosition;
    private String videoUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoWebviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        binding.btnBack.setOnClickListener(view -> onBackPressed());
        title = getIntent().getStringExtra("title");
        videoId = getIntent().getStringExtra("videoId");
        videoPosition = getIntent().getIntExtra("videoPosition",0);
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait");

        binding.titleName.setText(title);

        if(videoId == null){
            Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if(videoPosition == 1){
            videoUrl = "https://vidsrc-embed.ru/embed/movie?tmdb="+videoId;
        }else if(videoPosition == 2) {
            videoUrl = "https://vidrock.net/movie/"+ videoId;
        }
//        Log.d("VideoUrl","value: "+videoUrl);
        binding.rotate.setOnClickListener(view -> rotateScreen());

        initStart();
    }
    private void initStart(){
        if (!isNetworkAvailable()) {
            binding.webView.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Please check your internet and try again", Toast.LENGTH_SHORT).show();
        } else {
            setupWebView(videoUrl);
            binding.webView.setVisibility(View.VISIBLE);
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void setupWebView(String videoUrl) {
        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setSupportZoom(false);
        webSettings.setDomStorageEnabled(true);
        binding.webView.setWebChromeClient(new WebChromeClient());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            binding.webView.setWebContentsDebuggingEnabled(false);
        }

        binding.webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                return true;
            }
        });

        binding.webView.setWebViewClient(new WebViewClient() {
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
                String videoDomain = "";
                if (videoPosition == 1) {
                    videoDomain = "vidsrc-embed.ru";
                } else if (videoPosition == 2) {
                    videoDomain = "vidrock.net";
                }
                if (url.contains(videoDomain)) {
                    return false;
                } else if (url.contains("dl.vidsrc.vip")) {
//                    Log.d("VideOUrl", "value: " + url);
                    if (videoPosition == 2) {
                        String downloadUrl = "https://dl.vidsrc.vip/movie/" + videoId;
                        Intent intent = new Intent(getApplicationContext(), DownloadWebview.class);
                        intent.putExtra("DownloadUrl", downloadUrl);
                        intent.putExtra("title", title);
                        startActivity(intent);
                    }
                    return true;
                } else {
                    view.stopLoading();
                    return true;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                String videoDomain = "";
                if (videoPosition == 1){
                    videoDomain = "vidsrc-embed.ru";
                }else if(videoPosition == 2) {
                    videoDomain = "vidrock.net";
                }
                if (!url.contains(videoDomain)) {
                    view.stopLoading();
                }
                if(hud !=null && !hud.isShowing()){
                    hud.show();
                }
                super.onPageStarted(view, url, favicon);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(hud !=null && hud.isShowing()){
                hud.dismiss();
                }

            }
        });

        binding.webView.loadUrl(videoUrl);
    }

    private void rotateScreen() {
        isRotate = !isRotate;
        if(!isRotate){
            binding.rlTitle.setBackgroundColor(Color.parseColor("#313647"));
            binding.rlTitle.setVisibility(View.VISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else {
            binding.rlTitle.setBackgroundColor(Color.parseColor("#000000"));
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.rlTitle.setVisibility(View.GONE);
                    }
                }, 300);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            View decorView = getWindow().getDecorView();
            int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            decorView.setSystemUiVisibility(flags);
            setCutoutMode(WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES);
        }
    }
    private void setCutoutMode(int mode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.layoutInDisplayCutoutMode = mode;
            getWindow().setAttributes(params);
        }
    }

    @Override
    protected void onPause() {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            isRotate = false;
            binding.rlTitle.setVisibility(View.VISIBLE);
            binding.rlTitle.setBackgroundColor(Color.parseColor("#313647"));
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            setCutoutMode(WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT);
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (isRotate) {
            binding.rlTitle.setVisibility(View.VISIBLE);
            binding.rlTitle.setBackgroundColor(Color.parseColor("#313647"));
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            setCutoutMode(WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT);
            isRotate = false;
        } else {
            super.onBackPressed();
            finish();
        }
    }
}
