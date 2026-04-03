package com.m.freemovie.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
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
    private String blockUrl ="";
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
        hud.show();

        if(!isNetworkAvailable()){
            binding.webView.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(),"Please check your internet and try again",Toast.LENGTH_SHORT).show();
        }else{
            hud.show();
            setupWebView(downloadUrl);
            binding.webView.setVisibility(View.VISIBLE);
        }
        binding.webContainer.setVisibility(View.VISIBLE);
    }
    @SuppressWarnings("deprecation")
    @SuppressLint("MissingPermission")
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

        binding.webView.setWebChromeClient(new CustomWebChromeClient() {
        });

        binding.webView.setWebViewClient(new WebViewClient() {

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString().toLowerCase();
                if (url.contains("adsystem") || url.contains("adservice") ||
                        url.contains("popads") || url.contains("onclickads") ||
                        url.contains("doublestack") || url.contains("propush")) {

                    return new WebResourceResponse("text/plain", "utf-8",
                            new java.io.ByteArrayInputStream("".getBytes()));
                }

                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hud.dismiss();
                view.loadUrl("javascript:(function() { " +
                        "var css = 'div[class*=\"modal\"], div[id*=\"modal\"], div[class*=\"popup\"], ' + " +
                        "          'div[class*=\"overlay\"], section[class*=\"modal\"], .animated.fadeIn { display: none !important; }';" +
                        "var head = document.getElementsByTagName('head')[0];" +
                        "var style = document.createElement('style');" +
                        "style.type = 'text/css';" +
                        "style.appendChild(document.createTextNode(css));" +
                        "head.appendChild(style);" +
                        "var checkInterval = setInterval(function() { " +
                        "  var elements = document.querySelectorAll(\"div[class*='modal'], div[id*='modal'], div[class*='popup']\");" +
                        "  for (var i = 0; i < elements.length; i++) { elements[i].remove(); }" +
                        "}, 1000);" +
                        "setTimeout(function() { clearInterval(checkInterval); }, 5000);" +
                        "})()");
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                return handleUrlLoading(view, url);
            }

            private boolean handleUrlLoading(WebView view, String url) {
                Log.e("VideoSelect","val: "+url);
                if (url.startsWith("intent://") || url.startsWith("market://") || !url.startsWith("http")) {
                    Log.e("VideoSelect", "BLOCKED EXTERNAL INTENT: " + url);
                    return true;
                }

                String path = url.toLowerCase();
                if (path.contains("api/users") || path.contains("invoke_layer")) {
                    return true;
                }

                if (isAllowedUrl(url)) {
                    return false;
                } else if (url.contains(blockUrl)) {
                    if(!blockUrl.isEmpty()){
                        view.stopLoading();
                        return true;
                    }else{
                        return false;
                    }
                }else{
                    blockUrl = url;
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
                    binding.webContainer.setVisibility(View.GONE);
                    downloadVideo(videoUrl);
                }else{
                    Toast.makeText(getApplicationContext(),"Video can't be downloaded",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        binding.webView.loadUrl(videoUrl);

    }

    private class CustomWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            result.cancel();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            result.cancel();
            return true;
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            result.cancel();
            return true;
        }
        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            return true;
        }

        @Override
        public Bitmap getDefaultVideoPoster() {
            return Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
        }


    }

    private boolean isAllowedUrl(String url) {
        String[] allowedPatterns = {
                "^https?://vidsrc\\..*",
                "^https?://vidvault\\.ru.*",
                "^https?://cardfightvanguard\\..*",
                "^https?://mkv.dl5cg77imb\\..*",
                "^https?://pahe\\.win.*",
                "^https?://kwik\\.cx/f/.*",
                "^https?://vault-.*\\.kwik\\.cx.*",
                "^https?://vault-.*\\.uwucdn\\.top.*",
                "^https?://.*\\.mp4.*",
                "^https?://.*/mp4/.*",
                "^https?://.*/mkv/.*",
                "^https?://.*\\.m3u8.*",
                "^https?://.*\\.ts.*",
                "^https?://.*/video/.*",
                "^https?://.*myvidplay.*",
                "^https?://.*dev.*",
                "^https?://.*mkv.*",
                "^https?://.*mixdrop.*",
                "^https?://.*m1xdrop.*",
                "^https?://.*voe.*",
                "^https?://.*.cloudatacdn.*",
                "^https?://.*/download/.*"
        };


        String lowerUrl = url.toLowerCase();
        if (lowerUrl.contains(".mp4") || lowerUrl.contains(".mkv") ||
                lowerUrl.contains(".m3u8") || lowerUrl.contains(".ts")
                || lowerUrl.contains(".dev")
                || lowerUrl.contains("myvidplay.com/download")
                || lowerUrl.contains(".cx")
                ||lowerUrl.contains("abstream.to")
                ||lowerUrl.contains("voe.sx")) {
            return true;
        }

        for (String pattern : allowedPatterns) {
            if (url.matches(pattern)) {
                return true;
            }else{
                blockUrl = url;
            }
        }
        return false;
    }

    private File getLocalFile() {
        String safeTitle = title.replaceAll("[^a-zA-Z0-9.-]", "_");

        String fileExtension = ".mp4";
        if (downloadUrl != null && downloadUrl.toLowerCase().contains(".mkv")) {
            fileExtension = ".mkv";
        }

        String fileName;
        if (EpisodeNum.isEmpty()) {
            fileName = safeTitle + fileExtension;
        } else {
            fileName = safeTitle + "_Episode_" + EpisodeNum + fileExtension;
        }

        File freeMovieDir = new File(getFilesDir(), "FreeMovie");
        if (!freeMovieDir.exists()) {
            freeMovieDir.mkdirs();
        }
        File movieDir = new File(freeMovieDir, safeTitle);
        if (!movieDir.exists()) {
            movieDir.mkdirs();
        }

        return new File(movieDir, fileName);
    }

    private void downloadVideo(String videoUrl) {
        isFirstTask = true;
        File outputFile = getLocalFile();
        String Episode = EpisodeNum.isEmpty()? "":" Ep: " + EpisodeNum ;

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
                binding.webContainer.setVisibility(View.VISIBLE);
                finish();
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

                String cookies = CookieManager.getInstance().getCookie(videoUrl);
                okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder()
                        .url(videoUrl)
                        .get()
                        .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                        .addHeader("Accept", "video/webm,video/mp4,video/*;q=0.9,application/ogg;q=0.7,audio/*;q=0.6,*/*;q=0.5")
                        .addHeader("Accept-Language", "en-US,en;q=0.9")
                        .addHeader("Accept-Encoding", "identity")
                        .addHeader("Connection", "keep-alive")
                        .addHeader("Range", "bytes=0-")
                        .addHeader("Referer", downloadUrl)
                        .addHeader("Sec-Fetch-Dest", "video")
                        .addHeader("Sec-Fetch-Mode", "no-cors")
                        .addHeader("Sec-Fetch-Site", "same-site")
                        .addHeader("Origin", getDomainFromUrl(downloadUrl));

                if (cookies != null && !cookies.isEmpty()) {
                    requestBuilder.addHeader("Cookie", cookies);
                }
                if (existingLength > 0) {
                    requestBuilder.header("Range", "bytes=" + existingLength + "-");
                }

                response = client.newCall(requestBuilder.build()).execute();
                int responseCode = response.code();
                if (responseCode == 403) {
                    throw new IOException("Access forbidden - Server rejected the request");
                }

                boolean isResume = (responseCode == 206 || responseCode == 200);

                if (response.isSuccessful() || isResume) {
                    inputStream = response.body().byteStream();
                    outputStream = new FileOutputStream(outputFile, existingLength > 0);

                    long contentLength = response.body().contentLength();
                    if (contentLength == -1) {
                        contentLength = response.header("Content-Length") != null ?
                                Long.parseLong(response.header("Content-Length")) : -1;
                    }

                    if (isResume && contentLength != -1) {
                        contentLength += existingLength;
                    }

                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    long totalBytesRead = existingLength;
                    long lastProgressUpdate = 0;

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        if (!isFirstTask) break;

                        outputStream.write(buffer, 0, bytesRead);
                        totalBytesRead += bytesRead;

                        if (contentLength > 0 && (totalBytesRead - lastProgressUpdate) > contentLength / 100) {
                            final int progress = (int) ((totalBytesRead * 100) / contentLength);
                            runOnUiThread(() -> {
                                if (downloadHud != null && downloadHud.isShowing()) {
                                    downloadHud.setProgress(Math.min(progress, 100));
                                }
                            });
                            lastProgressUpdate = totalBytesRead;
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
                            binding.webContainer.setVisibility(View.VISIBLE);
                            finish();
                        });
                    }
                } else {
                    throw new IOException("Server returned code: " + responseCode + " - " + response.message());
                }

            } catch (Exception e) {
                if (isFirstTask) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        if (downloadHud != null && downloadHud.isShowing()) {
                            downloadHud.dismiss();
                        }
                        isFirstTask = false;
                        String errorMsg = e.getMessage();
                        if (errorMsg != null && errorMsg.contains("403")) {
                            errorMsg = "Download failed: Access forbidden. The server rejected our request.";
                        }
                        Toast.makeText(getApplicationContext(),
                                "Error: " + (errorMsg != null ? errorMsg : "Unknown error"),
                                Toast.LENGTH_LONG).show();
                        finish();
                        binding.webContainer.setVisibility(View.VISIBLE);
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

    // Helper method to get domain from URL
    private String getDomainFromUrl(String url) {
        try {
            java.net.URL urlObj = new java.net.URL(url);
            String protocol = urlObj.getProtocol();
            String host = urlObj.getHost();
            int port = urlObj.getPort();

            if (port == -1) {
                return protocol + "://" + host;
            } else {
                return protocol + "://" + host + ":" + port;
            }
        } catch (Exception e) {
            return url;
        }
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

    private void setSettings(WebSettings webSettings) {
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setSupportZoom(false);
        webSettings.setSupportMultipleWindows(false); // Make sure this is false
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
//        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
//        webSettings.setUserAgentString(userAgent);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        binding.webView.requestFocusFromTouch();

        binding.webView.setFocusable(true);
        binding.webView.setFocusableInTouchMode(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(binding.webView, false);
        }
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
        if (binding.webView != null && binding.webView.canGoBack()) {
            binding.webView.goBack();
        }else{
            super.onBackPressed();
            isFirstTask = false;
            finish();
        }
    }
}