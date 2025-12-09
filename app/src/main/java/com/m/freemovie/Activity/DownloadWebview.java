package com.m.freemovie.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.m.freemovie.databinding.ActivityDownloadWebviewBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadWebview extends AppCompatActivity {
    private String downloadUrl;
    private ActivityDownloadWebviewBinding binding;
    private KProgressHUD hud;
    private KProgressHUD downloadHud;
    private String title;
    private boolean isFirstTask = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityDownloadWebviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnBack.setOnClickListener(view -> onBackPressed());
        binding.titleName.setText("Download video");
        binding.rotate.setVisibility(View.GONE);
        downloadUrl = getIntent().getStringExtra("DownloadUrl");
        title = getIntent().getStringExtra("title");
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait");

        if(!isNetworkAvailable()){
            binding.webView.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(),"Please check your internet and try again",Toast.LENGTH_SHORT).show();
        }else{
            hud.show();
            setupWebView(downloadUrl);
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
        setSettings(webSettings);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            binding.webView.setWebContentsDebuggingEnabled(false);
        }
        binding.webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                return true;
            }
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress > 80 && hud != null && hud.isShowing()) {
                    hud.dismiss();
                }
            }
        });


        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                blockAds(view);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                return handleUrlLoading(view, url);
            }
            private boolean handleUrlLoading(WebView view, String url) {
                if (url.contains("vidsrc") || url.contains("cardfightvanguard") || url.contains("workers.dev")) {
                    return false;
                } else {
                    view.stopLoading();
                    return true;
                }
            }

        });

        binding.webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String videoUrl, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                if (videoUrl.contains("vidsrc") || videoUrl.contains("cardfightvanguard") || videoUrl.contains("workers.dev")) {
                    if(isFirstTask){
                        Toast.makeText(getApplicationContext(),"Download in progress",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    downloadVideo(videoUrl);
                }
            }
        });
        binding.webView.loadUrl(videoUrl);

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

    private File getLocalFile() {
        String safeTitle = title.replaceAll("[^a-zA-Z0-9.-]", "_");
        String fileName = safeTitle + ".mp4";
        String dirName = safeTitle;

        File freeMovieDir = new File(getFilesDir(), "FreeMovie");
        if (!freeMovieDir.exists()) {
            freeMovieDir.mkdirs();
        }

        File movieDir = new File(freeMovieDir, dirName);
        if (!movieDir.exists()) {
            movieDir.mkdirs();
        }
        return new File(movieDir, fileName);
    }
    private void downloadVideo(String videoUrl) {
        isFirstTask = true;
        File outputFile = getLocalFile();
        downloadHud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                .setLabel("Downloading...")
                .setMaxProgress(100)
                .setCancellable(true);
        downloadHud.show();

        downloadHud.setCancellable(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                isFirstTask = false;
                if (outputFile.exists()) {
                    outputFile.delete();
                    Toast.makeText(getApplicationContext(), "Download cancelled", Toast.LENGTH_SHORT).show();
                }
            }
        });

        new Thread(() -> {
            FileOutputStream outputStream = null;
            InputStream inputStream = null;
            HttpURLConnection connection = null;

            try {
                URL url = new URL(videoUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(60000);
                connection.setReadTimeout(60000);
                connection.setInstanceFollowRedirects(true);


                long existingLength = 0;
                if (outputFile.exists()) {
                    existingLength = outputFile.length();
                    connection.setRequestProperty("Range", "bytes=" + existingLength + "-");
                }

                connection.connect();
                int responseCode = connection.getResponseCode();

                boolean isResume = (responseCode == HttpURLConnection.HTTP_PARTIAL);
                if (responseCode == HttpURLConnection.HTTP_OK || isResume) {
                    inputStream = connection.getInputStream();

                    if (isResume) {
                        outputStream = new FileOutputStream(outputFile, true);
                    } else {
                        outputStream = new FileOutputStream(outputFile);
                    }

                    int contentLength = connection.getContentLength();
                    if (isResume) {
                        contentLength += existingLength;
                    }

                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    long totalBytesRead = existingLength;

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        // Check if download was cancelled
                        if (!isFirstTask) {
                            break;
                        }

                        outputStream.write(buffer, 0, bytesRead);
                        totalBytesRead += bytesRead;

                        if (contentLength > 0) {
                            final int progress = (int) ((totalBytesRead * 100) / contentLength);
                            runOnUiThread(() -> downloadHud.setProgress(progress));
                        }
                    }

                    outputStream.close();
                    inputStream.close();

                    if (isFirstTask) {
                        runOnUiThread(() -> {
                            if (downloadHud != null && downloadHud.isShowing()) {
                                downloadHud.dismiss();
                            }
                            isFirstTask = false;
                            Toast.makeText(getApplicationContext(),
                                    "Download Complete! Saved to: " + outputFile.getAbsolutePath(),
                                    Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), Download_videoActivity.class));
                        });
                    }

                } else {
                    runOnUiThread(() -> {
                        if (downloadHud != null && downloadHud.isShowing()) {
                            downloadHud.dismiss();
                        }
                        isFirstTask = false;
                        Toast.makeText(getApplicationContext(), "Download Failed: HTTP " + responseCode,
                                Toast.LENGTH_SHORT).show();
                    });
                }

            } catch (Exception e) {
                // Only show error if not cancelled
                if (isFirstTask) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        if (downloadHud != null && downloadHud.isShowing()) {
                            downloadHud.dismiss();
                        }
                        isFirstTask = false;
                        Toast.makeText(getApplicationContext(),
                                "Download Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            } finally {
                try {
                    if (outputStream != null) outputStream.close();
                    if (inputStream != null) inputStream.close();
                    if (connection != null) connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    protected void onPause() {
        isFirstTask = false;
        super.onPause();

    }

    @Override
    protected void onResume() {
        isFirstTask = false;
        if(!isNetworkAvailable()){
            binding.webView.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(),"Please check your internet and try again",Toast.LENGTH_SHORT).show();
        }else{
            setupWebView(downloadUrl);
            binding.webView.setVisibility(View.VISIBLE);
        }
        super.onResume();

    }

    private void setSettings(WebSettings setting) {
        setting.setJavaScriptEnabled(true);
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
        setting.setAllowFileAccess(true);
        setting.setSupportZoom(true);
        setting.setBuiltInZoomControls(true);
        setting.setDisplayZoomControls(false);
        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        setting.setSupportMultipleWindows(false);
        String string = setting.getUserAgentString();
        setting.setUserAgentString(string + "androidapp-v1.4");
        setting.setGeolocationEnabled(true);
        setting.setGeolocationDatabasePath(getDir("geolocation", 0).getPath());
        setting.setSaveFormData(true);
        setting.setDomStorageEnabled(true);
        setting.setDatabaseEnabled(true);
        setting.setCacheMode(WebSettings.LOAD_DEFAULT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(binding.webView, true);
        }
        setting.setUseWideViewPort(true);
        setting.setTextZoom(Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("text_size", "100")));
    }

    @Override
    protected void onDestroy() {
        binding.webView.destroy();
        binding.webView.stopLoading();
        binding.webView.clearCache(true);
        binding.webView.clearHistory();
        binding.webView.clearFormData();
        if (hud != null && hud.isShowing()) {
            hud.dismiss();
        }
        hud = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isFirstTask = false;
        finish();
    }
}