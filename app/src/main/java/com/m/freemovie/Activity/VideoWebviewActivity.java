package com.m.freemovie.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
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
import com.m.freemovie.Utils.WindowUtils;
import com.m.freemovie.databinding.ActivityVideoWebviewBinding;


public class VideoWebviewActivity extends AppCompatActivity {
    private boolean isRotate = false;
    private String title;
    private String videoId;
    private KProgressHUD hud;
    private ActivityVideoWebviewBinding binding;
    private int videoPosition, epNumber;
    private String videoUrl;
    private int seasonNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoWebviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        binding.btnBack.setOnClickListener(view -> onBackPressed());
        title = getIntent().getStringExtra("title");
        videoId = getIntent().getStringExtra("videoId");
        videoPosition = getIntent().getIntExtra("videoPosition", 0);
        seasonNum = getIntent().getIntExtra("seasonNum", 1);
        epNumber = getIntent().getIntExtra("epNumber", 0);
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait");
        hud.show();
        binding.titleName.setText(title);

        if (videoId == null) {
            Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        switch (videoPosition){
            case 1:
                videoUrl = "https://vidsrc-embed.ru/embed/movie?tmdb=" + videoId;
                break;
            case 2:
                videoUrl = "https://vidrock.net/movie/"+ videoId;
                break;
            case 3:
                videoUrl = "https://vidlink.pro/tv/"+videoId+"/"+seasonNum+"/"+epNumber;
                break;
            case 4:
                videoUrl = "https://vidlink.pro/movie/"+videoId;
                break;
            case 5:
                videoUrl = "https://vidrock.net/tv/"+videoId+"/"+seasonNum+"/"+epNumber;
                break;
        }


        Log.d("VideoUrl","value: "+videoUrl);
        binding.rotate.setOnClickListener(view -> rotateScreen());

        initStart();
    }

    private void initStart() {
        if (binding == null) return;

        if (!isNetworkAvailable()) {
            binding.webView.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Please check your internet and try again", Toast.LENGTH_SHORT).show();
        } else {
            binding.webView.setVisibility(View.VISIBLE);
            setupWebView(videoUrl);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void setupWebView(String videoUrl) {
        binding.webView.setWebViewClient(new CustomWebViewClient());
        binding.webView.setWebChromeClient(new CustomWebChromeClient(){});
        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setSupportZoom(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            binding.webView.setWebContentsDebuggingEnabled(false);
        }
        binding.webView.loadUrl(videoUrl);

    }

    private class CustomWebChromeClient extends WebChromeClient {
        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            return true;
        }
        @Override
        public Bitmap getDefaultVideoPoster() {
            return Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                if (hud != null && hud.isShowing()) {
                    hud.dismiss();
                    hud = null;
                    blockAds(view);
                }
            }
        }
    }

    private class CustomWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            return handleUrlLoading(view, url);
        }
        private boolean handleUrlLoading(WebView view, String url) {
            String videoDomain = "";
            switch (videoPosition){
                case 1:
                    videoDomain = "vidsrc";
                    break;
                case 2:
                case 5:
                    videoDomain = "vidrock";
                    break;
                case 3:
                case 4:
                    videoDomain = "vidlink";
                    break;
            }

            if (url.contains(videoDomain)) {
                return false;
            } else if (url.contains("dl.vidsrc.vip")) {
//                Log.d("VideOUrl", "value: " + url);
                    if (videoPosition == 2) {
                        String downloadUrl = "https://dl.vidsrc.vip/movie/" + videoId;
                        Intent intent  = new Intent(getApplicationContext(), DownloadWebview.class);
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
    }


    private void blockAds(WebView view) {
        String tags = view.getUrl();
        StringBuilder sb = new StringBuilder();
        sb.append("javascript: ");
        String[] allTag = tags.split(",");
        for (String tag : allTag) {
            String adTag = tag;
            if (adTag.trim().length() > 0) {
                adTag = adTag.trim();
                if (adTag.contains("#")) {
                    adTag = adTag.substring(adTag.indexOf("#") + 1);
                    sb.append("document.getElementById(\'").append(adTag).append("\').remove();");

                } else if (adTag.contains(".")) {
                    adTag = adTag.substring(adTag.indexOf(".") + 1);
                    sb.append("var esc=document.getElementsByClassName(\'").append(adTag).append("\');for (var i = esc.length - 1; i >= 0; i--){esc[i].remove();};");

                } else {
                    sb.append("var esc=document.getElementsByTagName(\'").append(adTag).append("\');for (var i = esc.length - 1; i >= 0; i--){esc[i].remove();};");
                }
            }
        }
    }


    private void rotateScreen() {
        isRotate = !isRotate;
        if(!isRotate){
            binding.rlTitle.setBackgroundColor(Color.parseColor("#313647"));
            binding.rlTitle.setVisibility(View.VISIBLE);
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
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        }
        new WindowUtils(this, true,true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (videoPosition == 6 && binding != null && binding.webView != null) {
            new Handler().postDelayed(() -> {
                if (binding != null && binding.webView != null) {
                    if (hud != null && hud.isShowing()) {
                        hud.dismiss();
                        hud = null;
                    }
                    binding.webView.clearCache(true);
                    binding.webView.stopLoading();
                }
            }, 300);
        }
    }

    @Override
    protected void onDestroy() {
        if (hud != null && hud.isShowing()) {
            hud.dismiss();
            hud = null;
        }


        if (binding != null && binding.webView != null) {
            binding.webView.stopLoading();
            binding.webView.setWebChromeClient(null);
            binding.webView.setWebViewClient(null);
            binding.webView.destroy();
        }

        binding = null;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (binding != null && binding.rlTitle != null) {
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                isRotate = false;
                binding.rlTitle.setVisibility(View.VISIBLE);
                binding.rlTitle.setBackgroundColor(Color.parseColor("#313647"));
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                new WindowUtils(this, true,true);
            }
        }
        super.onPause();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (binding != null && binding.webView != null) {
            try {
                binding.webView.clearCache(true);
                binding.webView.clearHistory();
                binding.webView.reload();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onBackPressed() {
        if (isRotate && binding != null && binding.rlTitle != null) {
            binding.rlTitle.setVisibility(View.VISIBLE);
            binding.rlTitle.setBackgroundColor(Color.parseColor("#313647"));
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            new WindowUtils(this, true,true);
            isRotate = false;
        } else {
            super.onBackPressed();
            finish();
        }
    }
}
