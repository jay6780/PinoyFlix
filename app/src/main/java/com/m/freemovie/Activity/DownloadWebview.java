package com.m.freemovie.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
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
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class DownloadWebview extends AppCompatActivity {
    private String downloadUrl;
    private ActivityDownloadWebviewBinding binding;
    private KProgressHUD hud;
    private KProgressHUD downloadHud;
    private String title,EpisodeNum;
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
        EpisodeNum = getIntent().getStringExtra("EpisodeNum");
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
        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                blockAds(view);
                hud.dismiss();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                return handleUrlLoading(view, url);
            }

            private boolean handleUrlLoading(WebView view, String url) {
//                Log.e("VideoSelect","val: "+url);
                if (isAllowedUrl(url)) {
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
//                Log.e("VideoSelect","val: "+videoUrl);
                if (isAllowedUrl(videoUrl)) {
                    if(isFirstTask){
                        Toast.makeText(getApplicationContext(),"Download in progress",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    downloadVideo(videoUrl);
                }else{
                    Toast.makeText(getApplicationContext(),"Video can't be downloaded",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        binding.webView.loadUrl(videoUrl);

    }

    private boolean isAllowedUrl(String url) {
        String[] allowedPatterns = {
                "^https?://vidsrc\\..*",
                "^https?://cardfightvanguard\\..*",
                "^https?://workers\\.dev.*",
                "^https?://pahe\\.win.*",
                "^https?://kwik\\.cx/f/.*",
                "^https?://vault-.*\\.kwik\\.cx.*",
                "^https?://vault-.*\\.uwucdn\\.top.*",
                "^https?://.*\\.mp4.*",
                "^https?://.*/mp4/.*",
        };

        for (String pattern : allowedPatterns) {
            if (url.matches(pattern)) {
                return true;
            }
        }
        return false;
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
        String Episode = EpisodeNum == null? "":" Ep_" + EpisodeNum;
        String fileName = safeTitle +"_Episode_"+Episode+"_"+".mp4";
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

        String Episode = EpisodeNum == null? "":" Ep: " + EpisodeNum;

        downloadHud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                .setLabel("Downloading: " + title + Episode)
                .setMaxProgress(100)
                .setCancellable(true);
        downloadHud.show();

        downloadHud.setCancellable(dialog -> {
            isFirstTask = false;
            if (outputFile.exists()) {
                outputFile.delete();
                Toast.makeText(getApplicationContext(), "Download cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        new Thread(() -> {
            FileOutputStream outputStream = null;
            InputStream inputStream = null;
            okhttp3.Response response = null;

            try {
                OkHttpClient client = getUnsafeOkHttpClient().build();

                long existingLength = 0;
                if (outputFile.exists()) {
                    existingLength = outputFile.length();
                }

                okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder()
                        .url(videoUrl)
                        .get();

                if (existingLength > 0) {
                    requestBuilder.addHeader("Range", "bytes=" + existingLength + "-");
                }

                response = client.newCall(requestBuilder.build()).execute();
                int responseCode = response.code();
                boolean isResume = (responseCode == 206);

                if (response.isSuccessful() || isResume) {
                    inputStream = response.body().byteStream();
                    outputStream = new FileOutputStream(outputFile, isResume);

                    long contentLength = response.body().contentLength();
                    if (isResume) {
                        contentLength += existingLength;
                    }

                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    long totalBytesRead = existingLength;

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        if (!isFirstTask) break;

                        outputStream.write(buffer, 0, bytesRead);
                        totalBytesRead += bytesRead;

                        if (contentLength > 0) {
                            final int progress = (int) ((totalBytesRead * 100) / contentLength);
                            runOnUiThread(() -> downloadHud.setProgress(progress));
                        }
                    }

                    outputStream.flush();

                    if (isFirstTask) {
                        runOnUiThread(() -> {
                            if (downloadHud != null && downloadHud.isShowing()) {
                                downloadHud.dismiss();
                            }
                            isFirstTask = false;
                            Toast.makeText(getApplicationContext(),
                                    "Download Complete!", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), Download_videoActivity.class));
                        });
                    }
                } else {
                    throw new IOException("Server returned code: " + responseCode);
                }

            } catch (Exception e) {
                if (isFirstTask) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        if (downloadHud != null && downloadHud.isShowing()) {
                            downloadHud.dismiss();
                        }
                        isFirstTask = false;
                        Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            } finally {
                try {
                    if (response != null) response.close();
                    if (outputStream != null) outputStream.close();
                    if (inputStream != null) inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static OkHttpClient.Builder getUnsafeOkHttpClient() {

        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String
                                authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String
                                authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();



            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(60, TimeUnit.SECONDS);
            builder.writeTimeout(60, TimeUnit.SECONDS);
            builder.readTimeout(120, TimeUnit.SECONDS);
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } }


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