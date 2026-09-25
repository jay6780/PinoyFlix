package com.m.freemovie.Utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AlertDialog;
import androidx.media3.common.C;
import androidx.media3.common.Format;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.TrackSelectionOverride;
import androidx.media3.common.Tracks;
import androidx.media3.common.text.CueGroup;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.datasource.HttpDataSource;
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.exoplayer.upstream.DefaultLoadErrorHandlingPolicy;
import androidx.media3.ui.AspectRatioFrameLayout;
import androidx.media3.ui.PlayerView;
import androidx.media3.ui.SubtitleView;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.m.freemovie.R;

import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@UnstableApi
public class VideoUtils {
    private Activity activity;
    private ExoPlayer exoPlayer;
    private DefaultTrackSelector trackSelector;
    private final List<String> discoveredSubtitleUrls = new ArrayList<>();
    private WebView scraper;
    private final Handler scraperTimeoutHandler = new Handler(Looper.getMainLooper());
    private Runnable scraperTimeoutRunnable;
    private final Handler streamDebounceHandler = new Handler(Looper.getMainLooper());
    private Runnable triggerPlaybackRunnable;
    private String pendingStreamUrl;
    private java.util.Map<String, String> pendingStreamHeaders;
    private boolean hasStartedPlayback = false;
    private String player;
    private SubtitleView subtitleView;
    private String currentLoadedUrl;
    private KProgressHUD hud;
    private String videoUrl;
    private PlayerView playerView;
    private RelativeLayout relativeLayout;
    private TextView textView;
    private boolean isRelease = false;
    private ImageView imageView;

    public VideoUtils(Activity activity, SubtitleView subtitleView, PlayerView playerView, RelativeLayout relativeLayout, TextView textView,ImageView imageView) {
        this.activity = activity;
        this.playerView = playerView;
        this.subtitleView = subtitleView;
        this.relativeLayout = relativeLayout;
        this.textView = textView;
        this.imageView = imageView;
        hud = KProgressHUD.create(activity)
                .setCancellable(true)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if(exoPlayer == null){
                            releasePlayer();
                        }
                    }
                });


        subtitleView = playerView.getSubtitleView();
        if (subtitleView != null) {
            subtitleView.setVisibility(View.VISIBLE);
            subtitleView.setApplyEmbeddedFontSizes(false);
            subtitleView.setApplyEmbeddedStyles(false);
            subtitleView.setFixedTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
            subtitleView.setStyle(new androidx.media3.ui.CaptionStyleCompat(
                    Color.WHITE,
                    Color.argb(204, 0, 0, 0),
                    Color.TRANSPARENT,
                    androidx.media3.ui.CaptionStyleCompat.EDGE_TYPE_OUTLINE,
                    Color.BLACK,
                    null
            ));
        }
    }

    private boolean isAdOrJunkUrl(String url) {
        if (url == null) return false;
        String lower = url.toLowerCase();
        return lower.contains("google-analytics")
                || lower.contains("googlesyndication")
                || lower.contains("doubleclick")
                || lower.contains("adservice")
                || lower.contains("adnxs")
                || lower.contains("popads")
                || lower.contains("propeller")
                || lower.contains("adsterra")
                || lower.contains("exoclick")
                || lower.contains("trafficjunky")
                || lower.contains("histats")
                || lower.contains("llvpn.com")
                || lower.contains("creative-sb1")
                || lower.contains("juicyads")
                || lower.contains("adnium")
                || lower.contains("tsyndicate")
                || lower.contains("adsco.re")
                || lower.contains("googletagmanager")
                || lower.contains("cloudflareinsights")
                || lower.contains("/beacon")
                || lower.contains("test-videos.co.uk")
                || lower.contains("bigbuckbunny")
                || lower.contains("big_buck_bunny")
                || lower.contains("sample-videos")
                || lower.contains("demo-video")
                || lower.contains("w3schools")
                || lower.contains("gtv-videos-bucket")
                || lower.contains("sample.mp4")
                || lower.contains("dummy.mp4")
                || lower.contains("test.mp4")
                || lower.contains("10s_1mb")
                || lower.endsWith(".gif")
                || lower.endsWith(".svg")
                || lower.endsWith(".ico")
                || lower.endsWith(".woff")
                || lower.endsWith(".woff2")
                || lower.endsWith(".ttf");
    }

    private boolean isVideoStreamUrl(String url) {
        if (url == null) return false;
        String lower = url.toLowerCase();
        String cleanUrl = lower.split("\\?")[0];
        if (cleanUrl.endsWith(".js") || cleanUrl.endsWith(".css") || cleanUrl.endsWith(".html")
                || cleanUrl.endsWith(".htm") || cleanUrl.endsWith(".json") || cleanUrl.endsWith(".jpg")
                || cleanUrl.endsWith(".jpeg") || cleanUrl.endsWith(".png") || cleanUrl.endsWith(".webp")
                || cleanUrl.endsWith(".gif") || cleanUrl.endsWith(".svg") || cleanUrl.endsWith(".ico")
                || cleanUrl.endsWith(".txt") || cleanUrl.endsWith(".vtt") || cleanUrl.endsWith(".srt")
                || cleanUrl.endsWith(".woff") || cleanUrl.endsWith(".woff2") || cleanUrl.endsWith(".ttf")
                || cleanUrl.endsWith(".ts") || cleanUrl.endsWith(".m4s") || cleanUrl.endsWith(".key")
                || cleanUrl.endsWith(".aac") || cleanUrl.endsWith(".mp3")
                || lower.contains(".ts?") || lower.contains(".m4s?") || lower.contains(".key?")) {
            return false;
        }

        if (lower.contains("google-analytics") || lower.contains("doubleclick")
                || lower.contains("adnxs") || lower.contains("/beacon") || lower.contains("/analytics")
                || cleanUrl.contains("demo-video")
                || lower.contains("test-videos.co.uk")
                || lower.contains("bigbuckbunny")
                || lower.contains("big_buck_bunny")
                || lower.contains("sample-videos")
                || lower.contains("w3schools")
                || lower.contains("gtv-videos-bucket")
                || lower.contains("sample.mp4")
                || lower.contains("dummy.mp4")
                || lower.contains("test.mp4")
                || lower.contains("10s_1mb")) {
            return false;
        }
        return cleanUrl.endsWith(".m3u8")
                || cleanUrl.endsWith(".mpd")
                || cleanUrl.endsWith(".mp4")
                || cleanUrl.endsWith(".mkv")
                || cleanUrl.endsWith(".webm")
                || lower.contains(".m3u8")
                || lower.contains(".mpd")
                || lower.contains("/hls/")
                || lower.contains("/playlist")
                || lower.contains("/manifest")
                || lower.contains(".mp4?")
                || lower.contains(".mkv?")
                || lower.contains(".webm?");
    }

    private boolean isSubtitleUrl(String url) {
        if (url == null) return false;
        String lower = url.toLowerCase();
        String cleanUrl = lower.split("\\?")[0];
        if (isAdOrJunkUrl(url)) return false;
        return cleanUrl.endsWith(".vtt") || cleanUrl.endsWith(".srt")
                || cleanUrl.endsWith(".ass") || cleanUrl.endsWith(".sub")
                || lower.contains(".vtt") || lower.contains(".srt")
                || lower.contains("/subtitles/") || lower.contains("/captions/")
                || lower.contains("sub.vtt");
    }

    public void resolveAndPlayStream(String videoUrl) {
        isRelease = false;
        this.videoUrl = videoUrl;
        discoveredSubtitleUrls.clear();
        pendingStreamUrl = null;
        pendingStreamHeaders = null;
        hasStartedPlayback = false;
        if (streamDebounceHandler != null) {
            streamDebounceHandler.removeCallbacksAndMessages(null);
        }
        if (hud != null && !isRelease) {
            hud.setLabel("Finding video & subtitles...");
            if (!hud.isShowing()) {
                hud.show();
            }
        }

        if (isVideoStreamUrl(videoUrl)) {
            verifyVideoStatusAndPlay(videoUrl, videoUrl, null);
            return;
        }

        currentLoadedUrl = videoUrl;
        player = extractDomain(videoUrl);

        if (scraper == null) {
            scraper = new WebView(activity);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            );
            scraper.setLayoutParams(lp);
            scraper.setAlpha(0.01f);
            scraper.setVisibility(View.VISIBLE);
            relativeLayout.addView(scraper, 0);
        } else {
            scraper.stopLoading();
            scraper.loadUrl("about:blank");
        }
        scraper.onResume();

        WebSettings settings = scraper.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36");
        settings.setSupportMultipleWindows(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);

        try {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setAcceptThirdPartyCookies(scraper, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        scraper.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                return false;
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress >= 20) {
                    injectAutoplayScript(view);
                }
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                String msg = consoleMessage.message();
                if (msg != null && msg.startsWith("EXTRACTED_VIDEO_SRC:")) {
                    String streamUrl = msg.substring("EXTRACTED_VIDEO_SRC:".length()).trim();
                    if (isVideoStreamUrl(streamUrl)) {
                        String activeEmbed = (currentLoadedUrl != null && !currentLoadedUrl.isEmpty()) ? currentLoadedUrl : videoUrl;
                        schedulePlayback(streamUrl, activeEmbed, null);
                    }
                } else if (msg != null && msg.startsWith("EXTRACTED_TRACK_SRC:")) {
                    String trackUrl = msg.substring("EXTRACTED_TRACK_SRC:".length()).trim();
                    if (!trackUrl.isEmpty() && !discoveredSubtitleUrls.contains(trackUrl)) {
                        discoveredSubtitleUrls.add(trackUrl);
                        if (hasStartedPlayback && exoPlayer != null) {
                            addSubtitleTrack(trackUrl);
                        }
                    }
                }
                return true;
            }
        });

        scraper.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (request == null || request.getUrl() == null) {
                    return false;
                }
                String url = request.getUrl().toString();
                String scheme = request.getUrl().getScheme();
                if (scheme == null || (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https"))) {
                    return true;
                }

                if (isAdOrJunkUrl(url)) {
                    return true;
                }

                if (isVideoStreamUrl(url)) {
                    String activeEmbed = (currentLoadedUrl != null && !currentLoadedUrl.isEmpty()) ? currentLoadedUrl : videoUrl;
                    schedulePlayback(url, activeEmbed, null);
                    return true;
                }

                currentLoadedUrl = url;
                String redirectedHost = extractDomain(url);
                if (redirectedHost != null && !redirectedHost.isEmpty()) {
                    player = redirectedHost;
                }

                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!isVideoStreamUrl(url)) {
                    currentLoadedUrl = url;
                    String finishedHost = extractDomain(url);
                    if (finishedHost != null && !finishedHost.isEmpty()) {
                        player = finishedHost;
                    }
                }
//                Log.d("ScraperDomain", "Page finished on: " + url + " (domain: " + player + ")");
                injectAutoplayScript(view);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();

                if (isAdOrJunkUrl(url)) {
                    return new WebResourceResponse("text/plain", "UTF-8", new ByteArrayInputStream(new byte[0]));
                }

                if (isSubtitleUrl(url) && !discoveredSubtitleUrls.contains(url)) {
                    discoveredSubtitleUrls.add(url);
                    if (hasStartedPlayback && exoPlayer != null) {
                        addSubtitleTrack(url);
                    }
                }

                if (isVideoStreamUrl(url)) {
                    java.util.Map<String, String> requestHeaders = request.getRequestHeaders();
                    String activeEmbed = (currentLoadedUrl != null && !currentLoadedUrl.isEmpty()) ? currentLoadedUrl : videoUrl;
                    schedulePlayback(url, activeEmbed, requestHeaders);
                }
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                if (request != null && request.isForMainFrame()) {
                    int statusCode = errorResponse != null ? errorResponse.getStatusCode() : -1;
                    if (statusCode == 404 || statusCode >= 500) {
                        activity.runOnUiThread(() -> {
                            if (!hasStartedPlayback) {
                                if (hud != null && hud.isShowing()) {
                                    hud.dismiss();
                                }
                                String msg = (statusCode == 404)
                                        ? "Video page not found"
                                        : "Server error";
                                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, android.webkit.WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (request != null && request.isForMainFrame()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && error != null) {
                        int errCode = error.getErrorCode();
                        if (errCode == ERROR_HOST_LOOKUP || errCode == ERROR_CONNECT || errCode == ERROR_TIMEOUT) {
                            activity.runOnUiThread(() -> {
                                if (!hasStartedPlayback) {
                                    if (hud != null && hud.isShowing()) {
                                        hud.dismiss();
                                    }
                                    Toast.makeText(activity, "Failed to connect to server. Please select another server.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            }
        });

        if (scraperTimeoutRunnable != null) {
            scraperTimeoutHandler.removeCallbacks(scraperTimeoutRunnable);
        }
        scraperTimeoutRunnable = () -> {
            if (!hasStartedPlayback && pendingStreamUrl == null) {
                if (hud != null && hud.isShowing()) {
                    hud.dismiss();
                }
                playerView.setVisibility(View.VISIBLE);
                Toast.makeText(activity, "Server response slow. Please select another server.", Toast.LENGTH_SHORT).show();
            }
        };
        scraperTimeoutHandler.postDelayed(scraperTimeoutRunnable, 45000);

        scraper.loadUrl(videoUrl);
    }

    private String extractDomain(String url) {
        if (url == null || url.trim().isEmpty()) {
            return "";
        }
        try {
            Uri uri = Uri.parse(url);
            String host = uri.getHost();
            if (host != null) {
                if (host.startsWith("www.")) {
                    host = host.substring(4);
                }
                return host;
            }
        } catch (Exception e) {
            try {
                URL parsedUrl = new URL(url);
                String host = parsedUrl.getHost();
                if (host != null) {
                    if (host.startsWith("www.")) {
                        host = host.substring(4);
                    }
                    return host;
                }
            } catch (Exception ignored) {
            }
        }
        return "";
    }

    private void injectAutoplayScript(WebView view) {
        if (view == null) return;
        String currentUrl = view.getUrl();
        String currentHost = extractDomain(currentUrl);
        String activePlayer = (currentHost != null && !currentHost.isEmpty()) ? currentHost : player;
        String shortPlayer = (activePlayer != null && activePlayer.contains(".")) ? activePlayer.substring(0, activePlayer.indexOf(".")) : (activePlayer != null ? activePlayer : "");

        view.evaluateJavascript(
                "(function() {" +
                        "  try { window.open = function() { return null; }; } catch(e){}" +
                        "  var currentSource = '" + (activePlayer != null ? activePlayer : "") + "-player';" +
                        "  var shortSource = '" + shortPlayer + "-player';" +
                        "  function triggerPlay(w) {" +
                        "    if (!w) return;" +
                        "    try { w.postMessage({ source: currentSource, action: 'play' }, '*'); } catch(e){}" +
                        "    try { w.postMessage({ source: shortSource, action: 'play' }, '*'); } catch(e){}" +
                        "    try { w.postMessage({ action: 'play' }, '*'); } catch(e){}" +
                        "    try { w.postMessage({ type: 'play' }, '*'); } catch(e){}" +
                        "    try { w.postMessage({ method: 'play' }, '*'); } catch(e){}" +
                        "    try { w.postMessage({ event: 'command', func: 'playVideo' }, '*'); } catch(e){}" +
                        "    try { w.postMessage('play', '*'); } catch(e){}" +
                        "  }" +
                        "  function checkDoc(doc) {" +
                        "    if (!doc) return;" +
                        "    try {" +
                        "      var closeBtns = doc.querySelectorAll('.close, .btn-close, [class*=\"modal\" i] button, [class*=\"overlay\" i] button, [aria-label*=\"close\" i], .close-modal');" +
                        "      for (var c = 0; c < closeBtns.length; c++) {" +
                        "        try { closeBtns[c].click(); } catch(e){}" +
                        "      }" +
                        "      var overlays = doc.querySelectorAll('[id*=\"overlay\" i], [class*=\"overlay\" i], [id*=\"pop\" i], [class*=\"popbox\" i], [class*=\"popup\" i]');" +
                        "      for (var o = 0; o < overlays.length; o++) {" +
                        "        try {" +
                        "          if (overlays[o].tagName !== 'VIDEO' && overlays[o].tagName !== 'BODY') {" +
                        "            if (overlays[o].style) {" +
                        "              overlays[o].style.display = 'none';" +
                        "              overlays[o].style.pointerEvents = 'none';" +
                        "            }" +
                        "          }" +
                        "        } catch(e){}" +
                        "      }" +
                        "      function isGoodStream(u) {" +
                        "        if (!u || typeof u !== 'string') return false;" +
                        "        var l = u.toLowerCase();" +
                        "        if (l.indexOf('test-videos') !== -1 || l.indexOf('bigbuck') !== -1 || l.indexOf('sample') !== -1 || l.indexOf('demo-video') !== -1 || l.indexOf('w3schools') !== -1 || l.indexOf('10s_1mb') !== -1) return false;" +
                        "        return true;" +
                        "      }" +
                        "      try {" +
                        "        if (typeof sources !== 'undefined' && sources) {" +
                        "          if (sources.hls && isGoodStream(sources.hls)) console.log('EXTRACTED_VIDEO_SRC:' + sources.hls);" +
                        "          if (sources.mp4 && isGoodStream(sources.mp4)) console.log('EXTRACTED_VIDEO_SRC:' + sources.mp4);" +
                        "        }" +
                        "      } catch(e){}" +
                        "      try {" +
                        "        if (window.sources) {" +
                        "          if (window.sources.hls && isGoodStream(window.sources.hls)) console.log('EXTRACTED_VIDEO_SRC:' + window.sources.hls);" +
                        "          if (window.sources.mp4 && isGoodStream(window.sources.mp4)) console.log('EXTRACTED_VIDEO_SRC:' + window.sources.mp4);" +
                        "        }" +
                        "      } catch(e){}" +
                        "      try {" +
                        "        if (typeof jwplayer === 'function') {" +
                        "          var jw = jwplayer();" +
                        "          if (jw && typeof jw.getPlaylist === 'function') {" +
                        "            var pl = jw.getPlaylist();" +
                        "            if (pl && pl[0]) {" +
                        "              if (pl[0].sources) {" +
                        "                for (var p = 0; p < pl[0].sources.length; p++) {" +
                        "                  if (pl[0].sources[p].file && isGoodStream(pl[0].sources[p].file)) {" +
                        "                    console.log('EXTRACTED_VIDEO_SRC:' + pl[0].sources[p].file);" +
                        "                  }" +
                        "                }" +
                        "              }" +
                        "              if (pl[0].file && isGoodStream(pl[0].file)) {" +
                        "                console.log('EXTRACTED_VIDEO_SRC:' + pl[0].file);" +
                        "              }" +
                        "            }" +
                        "          }" +
                        "        }" +
                        "      } catch(e){}" +
                        "      var scripts = doc.querySelectorAll('script');" +
                        "      for (var s = 0; s < scripts.length; s++) {" +
                        "        var txt = scripts[s].textContent || scripts[s].innerText;" +
                        "        if (txt) {" +
                        "          var hlsMatch = txt.match(/['\"]hls['\"]\\s*:\\s*['\"]([^'\"]+)['\"]/i);" +
                        "          if (hlsMatch && hlsMatch[1] && isGoodStream(hlsMatch[1])) console.log('EXTRACTED_VIDEO_SRC:' + hlsMatch[1]);" +
                        "          var mp4Match = txt.match(/['\"]mp4['\"]\\s*:\\s*['\"]([^'\"]+)['\"]/i);" +
                        "          if (mp4Match && mp4Match[1] && isGoodStream(mp4Match[1])) console.log('EXTRACTED_VIDEO_SRC:' + mp4Match[1]);" +
                        "          var m3u8Match = txt.match(/['\"](https?:\\/\\/[^'\"]+\\.m3u8[^'\"]*)['\"]/i);" +
                        "          if (m3u8Match && m3u8Match[1] && isGoodStream(m3u8Match[1])) console.log('EXTRACTED_VIDEO_SRC:' + m3u8Match[1]);" +
                        "          var b64Match = txt.match(/atob\\(['\"]([A-Za-z0-9+/=]{20,})['\"]\\)/);" +
                        "          if (b64Match && b64Match[1]) {" +
                        "            try {" +
                        "              var dec = atob(b64Match[1]);" +
                        "              if (dec && dec.indexOf('http') !== -1 && isGoodStream(dec)) console.log('EXTRACTED_VIDEO_SRC:' + dec);" +
                        "            } catch(e){}" +
                        "          }" +
                        "          var b64Urls = txt.match(/['\"](aHR0c[A-Za-z0-9+/=]{20,})['\"]/g);" +
                        "          if (b64Urls) {" +
                        "            for (var b = 0; b < b64Urls.length; b++) {" +
                        "              try {" +
                        "                var rawB64 = b64Urls[b].replace(/['\"]/g, '');" +
                        "                var dec2 = atob(rawB64);" +
                        "                if (dec2 && (dec2.indexOf('.m3u8') !== -1 || dec2.indexOf('.mp4') !== -1) && isGoodStream(dec2)) {" +
                        "                  console.log('EXTRACTED_VIDEO_SRC:' + dec2);" +
                        "                }" +
                        "              } catch(e){}" +
                        "            }" +
                        "          }" +
                        "        }" +
                        "      }" +
                        "      var trks = doc.querySelectorAll('track');" +
                        "      for (var t = 0; t < trks.length; t++) {" +
                        "        if (trks[t].src) console.log('EXTRACTED_TRACK_SRC:' + trks[t].src);" +
                        "      }" +
                        "      var vids = doc.querySelectorAll('video, source');" +
                        "      for (var j = 0; j < vids.length; j++) {" +
                        "        if (vids[j].tagName === 'VIDEO') {" +
                        "          vids[j].muted = true;" +
                        "          vids[j].setAttribute('playsinline', '');" +
                        "          try { vids[j].play().catch(function(){}); } catch(e){}" +
                        "        }" +
                        "        var src1 = vids[j].src || vids[j].getAttribute('src');" +
                        "        if (src1 && src1.indexOf('blob:') === -1 && isGoodStream(src1)) {" +
                        "          console.log('EXTRACTED_VIDEO_SRC:' + src1);" +
                        "        }" +
                        "        if (vids[j].currentSrc && vids[j].currentSrc.indexOf('blob:') === -1 && isGoodStream(vids[j].currentSrc)) {" +
                        "          console.log('EXTRACTED_VIDEO_SRC:' + vids[j].currentSrc);" +
                        "        }" +
                        "      }" +
                        "      var btns = doc.querySelectorAll('.play, .vjs-big-play-button, .jw-display-icon-display, button[aria-label*=\"play\" i], [class*=\"play-btn\" i], [class*=\"play_btn\" i], [id*=\"play\" i], [class*=\"player\" i] button');" +
                        "      for (var k = 0; k < btns.length; k++) {" +
                        "        try { btns[k].click(); } catch(e){}" +
                        "      }" +
                        "    } catch(e) {}" +
                        "  }" +
                        "  function scanAll(w, doc) {" +
                        "    if (!doc) return;" +
                        "    triggerPlay(w);" +
                        "    checkDoc(doc);" +
                        "    var ifrs = doc.querySelectorAll('iframe');" +
                        "    for (var i = 0; i < ifrs.length; i++) {" +
                        "      try {" +
                        "        triggerPlay(ifrs[i].contentWindow);" +
                        "        if (ifrs[i].contentDocument) {" +
                        "          scanAll(ifrs[i].contentWindow, ifrs[i].contentDocument);" +
                        "        }" +
                        "      } catch(e) {}" +
                        "    }" +
                        "  }" +
                        "  scanAll(window, document);" +
                        "  if (!window.__playScanStarted) {" +
                        "    window.__playScanStarted = true;" +
                        "    var count = 0;" +
                        "    window.__playInterval = setInterval(function() {" +
                        "      count++;" +
                        "      if (count > 8 || window.__playStopped) { clearInterval(window.__playInterval); return; }" +
                        "      try {" +
                        "        scanAll(window, document);" +
                        "      } catch(e) {}" +
                        "    }, 600);" +
                        "  }" +
                        "})();", null);
    }

    private void schedulePlayback(String url, String webEmbedUrl, java.util.Map<String, String> requestHeaders) {
        if (!isVideoStreamUrl(url)) {
            return;
        }
        boolean isCurrentM3u8 = pendingStreamUrl != null && pendingStreamUrl.contains(".m3u8");
        boolean isNewM3u8 = url != null && url.contains(".m3u8");

        if (hasStartedPlayback) {
            if (isCurrentM3u8) {
                return;
            }
            if (!isNewM3u8) {
                return;
            }
//            Log.d("SchedulePlayback", "Upgrading from MP4 to full M3U8 stream: " + url);
        }

        if (pendingStreamUrl == null || (!isCurrentM3u8 && isNewM3u8)) {
            pendingStreamUrl = url;
            pendingStreamHeaders = requestHeaders;

            if (scraperTimeoutRunnable != null) {
                scraperTimeoutHandler.removeCallbacks(scraperTimeoutRunnable);
            }

            activity.runOnUiThread(() -> {
                if (scraper != null && isNewM3u8) {
                    try {
                        scraper.evaluateJavascript(
                                "(function() {" +
                                        "  window.__playStopped = true;" +
                                        "  if (window.__playInterval) clearInterval(window.__playInterval);" +
                                        "})();", null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (hud != null && hud.isShowing() && !isRelease) {
                    hud.setLabel("Checking video stream...");
                }

            });

            if (triggerPlaybackRunnable != null) {
                streamDebounceHandler.removeCallbacks(triggerPlaybackRunnable);
            }
            triggerPlaybackRunnable = () -> {
                if (pendingStreamUrl != null) {
                    hasStartedPlayback = true;
                    if (scraperTimeoutRunnable != null) {
                        scraperTimeoutHandler.removeCallbacks(scraperTimeoutRunnable);
                    }
                    verifyVideoStatusAndPlay(pendingStreamUrl, webEmbedUrl, pendingStreamHeaders);
                }
            };
            long debounceDelay = isNewM3u8 ? 300 : 1500;
            streamDebounceHandler.postDelayed(triggerPlaybackRunnable, debounceDelay);
        }
    }

    private String getMergedCookies(String... urls) {
        try {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.flush();
            java.util.Map<String, String> cookieMap = new java.util.LinkedHashMap<>();
            for (String u : urls) {
                if (u == null || u.isEmpty()) continue;
                String c = cookieManager.getCookie(u);
                if (c != null) {
                    for (String part : c.split(";")) {
                        String[] kv = part.trim().split("=", 2);
                        if (kv.length == 2 && !kv[0].trim().isEmpty()) {
                            cookieMap.put(kv[0].trim(), kv[1].trim());
                        }
                    }
                }
            }
            if (cookieMap.isEmpty()) return null;
            StringBuilder sb = new StringBuilder();
            for (java.util.Map.Entry<String, String> entry : cookieMap.entrySet()) {
                if (sb.length() > 0) sb.append("; ");
                sb.append(entry.getKey()).append("=").append(entry.getValue());
            }
            return sb.toString();
        } catch (Exception e) {
//            Log.e("CookieHelper", "Error merging cookies: ", e);
            return null;
        }
    }

    private void verifyVideoStatusAndPlay(String streamUrl, String webEmbedUrl, java.util.Map<String, String> requestHeaders) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URI uri = new URI(streamUrl);
                URL url = uri.toURL();
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36");

                String embedOrigin = getBaseUrl(webEmbedUrl != null ? webEmbedUrl : streamUrl);
                String origin = embedOrigin.endsWith("/") ? embedOrigin.substring(0, embedOrigin.length() - 1) : embedOrigin;
                conn.setRequestProperty("Referer", webEmbedUrl != null ? webEmbedUrl : embedOrigin);
                conn.setRequestProperty("Origin", origin);

                String cookieHeader = getMergedCookies(videoUrl, webEmbedUrl, currentLoadedUrl, streamUrl);
                if (cookieHeader != null) {
                    conn.setRequestProperty("Cookie", cookieHeader);
                }

                conn.setRequestProperty("Range", "bytes=0-1024");
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(8000);
                conn.setInstanceFollowRedirects(true);

                int responseCode = conn.getResponseCode();
//                Log.d("VideoStatus", "Checked URI status for " + streamUrl + " -> Response Code: " + responseCode);

                if (responseCode == 429) {
//                    Log.w("VideoStatus", "Server returned 429 Too Many Requests, backing off for 2s...");
                    activity.runOnUiThread(() -> {
                        if (hud != null && hud.isShowing() && !isRelease) {
                                hud.setLabel("Server busy (429), retrying...");
                        }
                    });
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ignored) {
                    }
                } else if (responseCode == 404 || responseCode == 410 || responseCode >= 500) {
//                    Log.e("VideoStatus", "Video stream returned error status code: " + responseCode);
                    activity.runOnUiThread(() -> {
                        if (hud != null && hud.isShowing()) {
                            hud.dismiss();
                        }
                        String msg = (responseCode == 404 || responseCode == 410)
                                ? "Video not found."
                                : "Server error";
                        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                    });
                    hasStartedPlayback = false;
                    pendingStreamUrl = null;
                    return;
                }
            } catch (Exception e) {
//                Log.w("VideoStatus", "URI status check exception: " + e.getMessage());
                if (e instanceof java.io.FileNotFoundException || (e.getMessage() != null && e.getMessage().contains("404"))) {
                    activity.runOnUiThread(() -> {
                        if (hud != null && hud.isShowing()) {
                            hud.dismiss();
                        }
                        Toast.makeText(activity, "Video not found (404). Please select another server.", Toast.LENGTH_SHORT).show();
                    });
                    hasStartedPlayback = false;
                    pendingStreamUrl = null;
                    return;
                }
            } finally {
                if (conn != null) {
                    try {
                        conn.disconnect();
                    } catch (Exception ignored) {
                    }
                }
            }
            if (isRelease) return;
            activity.runOnUiThread(() -> {
                if (hud != null && hud.isShowing() && !isRelease) {
                     hud.setLabel("Buffering video...");
                }
                playStream(streamUrl, webEmbedUrl, requestHeaders);
            });
        }).start();
    }

    private void addSubtitleTrack(String subUrl) {
        activity.runOnUiThread(() -> {
            if (exoPlayer == null || subUrl == null) return;
            MediaItem currentItem = exoPlayer.getCurrentMediaItem();
            if (currentItem != null && currentItem.localConfiguration != null) {
                List<MediaItem.SubtitleConfiguration> existingConfigs = currentItem.localConfiguration.subtitleConfigurations;
                for (MediaItem.SubtitleConfiguration c : existingConfigs) {
                    if (c.uri.toString().equalsIgnoreCase(subUrl)) {
                        return;
                    }
                }
                List<MediaItem.SubtitleConfiguration> newConfigs = new ArrayList<>(existingConfigs);
                String subLower = subUrl.toLowerCase().split("\\?")[0];
                String mimeType = subLower.endsWith(".srt") ? MimeTypes.APPLICATION_SUBRIP : MimeTypes.TEXT_VTT;
                String label = extractSubtitleLabel(subUrl, newConfigs.size() + 1);
                String lang = extractLanguageCode(subUrl);
                MediaItem.SubtitleConfiguration newSub = new MediaItem.SubtitleConfiguration.Builder(Uri.parse(subUrl))
                        .setMimeType(mimeType)
                        .setLanguage(lang)
                        .setLabel(label)
                        .build();
                newConfigs.add(newSub);

                long currentPos = exoPlayer.getCurrentPosition();
                boolean isPlaying = exoPlayer.getPlayWhenReady();
                MediaItem updatedItem = currentItem.buildUpon()
                        .setSubtitleConfigurations(newConfigs)
                        .build();
                exoPlayer.setMediaItem(updatedItem, currentPos);
                exoPlayer.setPlayWhenReady(isPlaying);
            }
        });
    }

    private String extractSubtitleLabel(String url, int index) {
        if (url == null) return "Subtitle " + index;
        String lower = url.toLowerCase();
        if (lower.contains("eng") || lower.contains("english")) return "English";
        if (lower.contains("spa") || lower.contains("spanish") || lower.contains("espanol"))
            return "Spanish";
        if (lower.contains("tag") || lower.contains("fil") || lower.contains("tagalog") || lower.contains("filipino"))
            return "Filipino";
        if (lower.contains("fre") || lower.contains("french") || lower.contains("fra"))
            return "French";
        if (lower.contains("ger") || lower.contains("german") || lower.contains("deu"))
            return "German";
        if (lower.contains("ind") || lower.contains("indonesian")) return "Indonesian";
        if (lower.contains("jap") || lower.contains("japanese") || lower.contains("jpn"))
            return "Japanese";
        if (lower.contains("kor") || lower.contains("korean")) return "Korean";
        if (lower.contains("chi") || lower.contains("chinese") || lower.contains("zho"))
            return "Chinese";
        if (lower.contains("ara") || lower.contains("arabic")) return "Arabic";
        if (lower.contains("por") || lower.contains("portuguese")) return "Portuguese";
        if (lower.contains("rus") || lower.contains("russian")) return "Russian";
        if (lower.contains("ita") || lower.contains("italian")) return "Italian";
        return "Subtitle " + index;
    }

    private String extractLanguageCode(String url) {
        if (url == null) return "und";
        String lower = url.toLowerCase();
        if (lower.contains("eng") || lower.contains("english")) return "en";
        if (lower.contains("spa") || lower.contains("spanish") || lower.contains("espanol"))
            return "es";
        if (lower.contains("tag") || lower.contains("fil") || lower.contains("tagalog") || lower.contains("filipino"))
            return "tl";
        if (lower.contains("fre") || lower.contains("french") || lower.contains("fra")) return "fr";
        if (lower.contains("ger") || lower.contains("german") || lower.contains("deu")) return "de";
        if (lower.contains("ind") || lower.contains("indonesian")) return "id";
        if (lower.contains("jap") || lower.contains("japanese") || lower.contains("jpn"))
            return "ja";
        if (lower.contains("kor") || lower.contains("korean")) return "ko";
        if (lower.contains("chi") || lower.contains("chinese") || lower.contains("zho"))
            return "zh";
        if (lower.contains("ara") || lower.contains("arabic")) return "ar";
        if (lower.contains("por") || lower.contains("portuguese")) return "pt";
        if (lower.contains("rus") || lower.contains("russian")) return "ru";
        if (lower.contains("ita") || lower.contains("italian")) return "it";
        return "und";
    }

    @androidx.annotation.OptIn(markerClass = androidx.media3.common.util.UnstableApi.class)
    private void playStream(String streamUrl, String webEmbedUrl, java.util.Map<String, String> interceptedHeaders) {
        if (isRelease) return;
        releaseExoPlayerOnly();
        if (hud != null && !isRelease) {
            hud.setLabel("Buffering video...");
            if (!hud.isShowing()) {
                hud.show();
            }
        }

        java.util.Map<String, String> defaultHeaders = new java.util.HashMap<>();
        if (interceptedHeaders != null) {
            for (java.util.Map.Entry<String, String> entry : interceptedHeaders.entrySet()) {
                String key = entry.getKey();
                if (key != null
                        && !key.equalsIgnoreCase("Range")
                        && !key.equalsIgnoreCase("Host")
                        && !key.equalsIgnoreCase("Accept-Encoding")
                        && !key.equalsIgnoreCase("Content-Length")
                        && !key.equalsIgnoreCase("X-Requested-With")
                        && !key.toLowerCase().startsWith("sec-fetch-")
                        && !key.toLowerCase().startsWith("sec-ch-")) {
                    defaultHeaders.put(key, entry.getValue());
                }
            }
        }

        String embedOrigin = getBaseUrl(webEmbedUrl != null ? webEmbedUrl : streamUrl);
        String origin = embedOrigin.endsWith("/") ? embedOrigin.substring(0, embedOrigin.length() - 1) : embedOrigin;

        if (!defaultHeaders.containsKey("Referer") && !defaultHeaders.containsKey("referer")) {
            defaultHeaders.put("Referer", webEmbedUrl != null ? webEmbedUrl : embedOrigin);
        }
        if (!defaultHeaders.containsKey("Origin") && !defaultHeaders.containsKey("origin")) {
            defaultHeaders.put("Origin", origin);
        }
        if (!defaultHeaders.containsKey("Cookie") && !defaultHeaders.containsKey("cookie")) {
            String cookieHeader = getMergedCookies(videoUrl, webEmbedUrl, currentLoadedUrl, streamUrl);
            if (cookieHeader != null) {
                defaultHeaders.put("Cookie", cookieHeader);
            }
        }

        DefaultLoadControl loadControl = new DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                        15000,
                        50000,
                        1500,
                        2500
                )
                .setPrioritizeTimeOverSizeThresholds(true)
                .build();

        DefaultHttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory()
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                .setDefaultRequestProperties(defaultHeaders)
                .setConnectTimeoutMs(15000)
                .setReadTimeoutMs(15000)
                .setAllowCrossProtocolRedirects(true);

        DefaultLoadErrorHandlingPolicy loadErrorHandlingPolicy = new DefaultLoadErrorHandlingPolicy(4) {
            @Override
            public int getMinimumLoadableRetryCount(int dataType) {
                return 4;
            }

            @Override
            public long getRetryDelayMsFor(LoadErrorInfo loadErrorInfo) {
                if (loadErrorInfo.exception instanceof HttpDataSource.InvalidResponseCodeException) {
                    HttpDataSource.InvalidResponseCodeException httpError =
                            (HttpDataSource.InvalidResponseCodeException) loadErrorInfo.exception;
                    if (httpError.responseCode == 404 || httpError.responseCode == 410) {
                        return C.TIME_UNSET;
                    }
                    if (httpError.responseCode == 429) {
                        return 1500;
                    }
                }
                return super.getRetryDelayMsFor(loadErrorInfo);
            }
        };

        DefaultMediaSourceFactory mediaSourceFactory = new DefaultMediaSourceFactory(httpDataSourceFactory)
                .setLoadErrorHandlingPolicy(loadErrorHandlingPolicy);

        trackSelector = new DefaultTrackSelector(activity);
        DefaultTrackSelector.Parameters parameters = trackSelector.buildUponParameters()
                .setPreferredTextLanguage("en")
                .setSelectUndeterminedTextLanguage(true)
                .build();
        trackSelector.setParameters(parameters);

        exoPlayer = new ExoPlayer.Builder(activity)
                .setMediaSourceFactory(mediaSourceFactory)
                .setTrackSelector(trackSelector)
                .setLoadControl(loadControl)
                .build();
        playerView.setPlayer(exoPlayer);
        playerView.setShowSubtitleButton(true);
        if (subtitleView != null) {
            subtitleView.post(this::applySubtitleBottomMargin);
        }
        playerView.post(() -> {
            View subtitleBtn = playerView.findViewById(androidx.media3.ui.R.id.exo_subtitle);
            if (subtitleBtn != null) {
                subtitleBtn.setOnClickListener(v -> showSubtitleDialog());
            }
        });

        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_READY) {
                    if (exoPlayer != null && exoPlayer.getPlayWhenReady()) {
                        if (hud != null && hud.isShowing()) {
                            hud.dismiss();
                        }
                    }
                }
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying) {
                    if (hud != null && hud.isShowing()) {
                        hud.dismiss();
                    }
                }
            }

            @Override
            public void onRenderedFirstFrame() {
                if (hud != null && hud.isShowing()) {
                    hud.dismiss();
                }
            }

            @Override
            public void onCues(CueGroup cueGroup) {
            }

            @Override
            public void onTracksChanged(Tracks tracks) {
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                if (hud != null && hud.isShowing()) {
                    hud.dismiss();
                }
                String msg = "Playback error";
                Throwable cause = error.getCause();
                if (cause instanceof HttpDataSource.InvalidResponseCodeException) {
                    HttpDataSource.InvalidResponseCodeException httpError =
                            (HttpDataSource.InvalidResponseCodeException) cause;
                    if (httpError.responseCode == 404 || httpError.responseCode == 410) {
                        msg = "Video not found (404). Please select another server.";
                    } else {
                        msg = "Server error (" + httpError.responseCode + "). Please select another server.";
                    }
                } else if (error.errorCode == PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND) {
                    msg = "Video not found (404). Please select another server.";
                } else if (error.getMessage() != null) {
                    if (error.getMessage().contains("404")) {
                        msg = "Video not found (404). Please select another server.";
                    } else if (error.getMessage().contains("Response code")) {
                        msg = error.getMessage() + ". Please select another server.";
                    } else {
                        msg = "Playback error: " + error.getMessage();
                    }
                }
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            }
        });

        MediaItem.Builder mediaItemBuilder = new MediaItem.Builder().setUri(streamUrl);
        if (!discoveredSubtitleUrls.isEmpty()) {
            List<MediaItem.SubtitleConfiguration> subtitleConfigs = new ArrayList<>();
            boolean hasDefault = false;
            for (int i = 0; i < discoveredSubtitleUrls.size(); i++) {
                String subUrl = discoveredSubtitleUrls.get(i);
                String subLower = subUrl.toLowerCase().split("\\?")[0];
                String mimeType = subLower.endsWith(".srt") ? MimeTypes.APPLICATION_SUBRIP : MimeTypes.TEXT_VTT;
                String label = extractSubtitleLabel(subUrl, i + 1);
                String lang = extractLanguageCode(subUrl);

                MediaItem.SubtitleConfiguration.Builder subConfigBuilder = new MediaItem.SubtitleConfiguration.Builder(Uri.parse(subUrl))
                        .setMimeType(mimeType)
                        .setLanguage(lang)
                        .setLabel(label);

                if (!hasDefault && (lang.equals("en") || i == 0)) {
                    subConfigBuilder.setSelectionFlags(C.SELECTION_FLAG_DEFAULT);
                    hasDefault = true;
                }
                subtitleConfigs.add(subConfigBuilder.build());
            }
            mediaItemBuilder.setSubtitleConfigurations(subtitleConfigs);
        }

        String cleanUrl = streamUrl.toLowerCase().split("\\?")[0];
        String lowerUrl = streamUrl.toLowerCase();
        if (cleanUrl.endsWith(".m3u8") || lowerUrl.contains(".m3u8") || lowerUrl.contains("/hls/")) {
            mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_M3U8);
        } else if (cleanUrl.endsWith(".mpd") || lowerUrl.contains(".mpd")) {
            mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_MPD);
        } else if (cleanUrl.endsWith(".mp4") || lowerUrl.contains(".mp4?")) {
            mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_MP4);
        } else if (cleanUrl.endsWith(".mkv") || lowerUrl.contains(".mkv?")) {
            mediaItemBuilder.setMimeType(MimeTypes.VIDEO_MATROSKA);
        } else if (cleanUrl.endsWith(".webm") || lowerUrl.contains(".webm?")) {
            mediaItemBuilder.setMimeType(MimeTypes.VIDEO_WEBM);
        }

        if (scraper != null) {
            try {
                scraper.stopLoading();
                scraper.evaluateJavascript(
                        "(function() {" +
                                "  window.__playStopped = true;" +
                                "  if (window.__playInterval) clearInterval(window.__playInterval);" +
                                "  try {" +
                                "    var vids = document.querySelectorAll('video');" +
                                "    for (var i = 0; i < vids.length; i++) {" +
                                "      vids[i].pause();" +
                                "      vids[i].src = '';" +
                                "      vids[i].removeAttribute('src');" +
                                "      vids[i].load();" +
                                "    }" +
                                "  } catch(e) {}" +
                                "})();", null);
                scraper.onPause();
                scraper.loadUrl("about:blank");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        exoPlayer.setMediaItem(mediaItemBuilder.build());
        exoPlayer.prepare();
        exoPlayer.setPlayWhenReady(true);
    }


    private String getBaseUrl(String url) {
        try {
            URL parsedUrl = new URL(url);
            return parsedUrl.getProtocol() + "://" + parsedUrl.getHost() + "/";
        } catch (MalformedURLException e) {
            return "https://";
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    public void applySubtitleBottomMargin() {
        if (playerView == null || playerView == null) return;
        subtitleView = playerView.getSubtitleView();
        if (subtitleView == null) return;
        ViewGroup.LayoutParams lp = subtitleView.getLayoutParams();
        if (!(lp instanceof ViewGroup.MarginLayoutParams)) return;
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;

        int orientation = activity.getResources().getConfiguration().orientation;
        int extraDp = (orientation == Configuration.ORIENTATION_LANDSCAPE) ? 56 : 0;
        int extraPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, extraDp, activity.getResources().getDisplayMetrics());
        mlp.bottomMargin = extraPx;
        subtitleView.setLayoutParams(mlp);
    }

    @androidx.annotation.OptIn(markerClass = androidx.media3.common.util.UnstableApi.class)
    private void showSubtitleDialog() {
        if (exoPlayer == null) {
            Toast.makeText(activity, "Player is not ready yet", Toast.LENGTH_SHORT).show();
            return;
        }

        Tracks tracks = exoPlayer.getCurrentTracks();
        List<String> displayNames = new ArrayList<>();
        List<Tracks.Group> textGroups = new ArrayList<>();
        List<Integer> trackIndices = new ArrayList<>();

        displayNames.add("Off (Turn off subtitles)");
        textGroups.add(null);
        trackIndices.add(-1);

        int checkedItem = 0;

        for (Tracks.Group group : tracks.getGroups()) {
            if (group.getType() == C.TRACK_TYPE_TEXT) {
                for (int i = 0; i < group.length; i++) {
                    Format format = group.getTrackFormat(i);
                    String name = format.label;
                    if (TextUtils.isEmpty(name)) {
                        if (!TextUtils.isEmpty(format.language) && !format.language.equals("und")) {
                            Locale locale = new Locale(format.language);
                            name = locale.getDisplayLanguage();
                        }
                    }
                    if (TextUtils.isEmpty(name)) {
                        name = "Subtitle " + displayNames.size();
                    }

                    boolean isSelected = group.isTrackSelected(i);
                    if (isSelected) {
                        checkedItem = displayNames.size();
                    }

                    displayNames.add(name);
                    textGroups.add(group);
                    trackIndices.add(i);
                }
            }
        }

        if (displayNames.size() == 1) { // Only "Off" is in the list
            new AlertDialog.Builder(activity, R.style.AlertDialogTheme)
                    .setTitle("Subtitles")
                    .setMessage("No subtitle tracks found in this video stream.\n\nSubtitles depend on the video server. If you need subtitles, please try selecting another server below (e.g. Player 2).")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        final int initialCheckedItem = checkedItem;
        String[] itemsArray = displayNames.toArray(new String[0]);

        new AlertDialog.Builder(activity, R.style.AlertDialogTheme)
                .setTitle("Select Subtitle Language")
                .setSingleChoiceItems(itemsArray, initialCheckedItem, (dialog, which) -> {
                    if (exoPlayer == null) {
                        dialog.dismiss();
                        return;
                    }
                    if (which == 0) {
                        // User selected "Off"
                        exoPlayer.setTrackSelectionParameters(
                                exoPlayer.getTrackSelectionParameters()
                                        .buildUpon()
                                        .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, true)
                                        .clearOverridesOfType(C.TRACK_TYPE_TEXT)
                                        .build()
                        );
                        Toast.makeText(activity, "Subtitles turned off", Toast.LENGTH_SHORT).show();
                    } else {
                        Tracks.Group targetGroup = textGroups.get(which);
                        int targetIndex = trackIndices.get(which);
                        TrackSelectionOverride override = new TrackSelectionOverride(
                                targetGroup.getMediaTrackGroup(),
                                targetIndex
                        );
                        exoPlayer.setTrackSelectionParameters(
                                exoPlayer.getTrackSelectionParameters()
                                        .buildUpon()
                                        .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, false)
                                        .clearOverridesOfType(C.TRACK_TYPE_TEXT)
                                        .addOverride(override)
                                        .build()
                        );
                        Toast.makeText(activity, "Selected: " + itemsArray[which], Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void releaseExoPlayerOnly() {
        if (exoPlayer != null) {
            try {
                exoPlayer.setPlayWhenReady(false);
                exoPlayer.stop();
                exoPlayer.clearMediaItems();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (playerView != null) {
                playerView.setPlayer(null);
            }
            try {
                exoPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            exoPlayer = null;
        }
    }

    public void releasePlayer() {
        stopScraper();
        releaseExoPlayerOnly();
        isRelease = true;
        if(hud !=null && hud.isShowing()){
            hud.dismiss();
        }
        videoUrl = "";
        if (playerView != null) {
            playerView.setVisibility(View.GONE);
        }
        if (textView != null) {
            textView.setVisibility(View.VISIBLE);
        }
        if(imageView !=null){
            imageView.setVisibility(View.VISIBLE);
        }
        if (scraper != null) {
            scraper.clearCache(true);
        }
        trackSelector = null;
        discoveredSubtitleUrls.clear();
        pendingStreamUrl = null;
        pendingStreamHeaders = null;
        hasStartedPlayback = false;
    }


    private void stopScraper() {
        if (streamDebounceHandler != null) {
            streamDebounceHandler.removeCallbacksAndMessages(null);
        }
        if (scraperTimeoutHandler != null) {
            scraperTimeoutHandler.removeCallbacksAndMessages(null);
        }
        if (scraper != null) {
            try {
                scraper.stopLoading();
                scraper.evaluateJavascript(
                        "(function() {" +
                                "  try {" +
                                "    var vids = document.querySelectorAll('video');" +
                                "    for (var i = 0; i < vids.length; i++) {" +
                                "      vids[i].pause();" +
                                "      vids[i].src = '';" +
                                "      vids[i].muted = true;" +
                                "    }" +
                                "    var ifrs = document.querySelectorAll('iframe');" +
                                "    for (var j = 0; j < ifrs.length; j++) {" +
                                "      ifrs[j].src = 'about:blank';" +
                                "    }" +
                                "  } catch(e) {}" +
                                "})();", null);
                scraper.onPause();
                scraper.loadUrl("about:blank");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onPause() {
        if (scraper != null && exoPlayer != null) {
            scraper.onPause();
            exoPlayer.pause();
        }
    }

    public void pausePlayer() {
        if (playerView != null) {
            playerView.onPause();
        }
    }

    public void playResume() {
        if (exoPlayer != null) {
            exoPlayer.play();
        }
        if (scraper != null) {
            scraper.onResume();
        }
        if (playerView != null) {
            playerView.onResume();
        }
    }

    public void DestroyPlayer() {
        if (streamDebounceHandler != null) {
            streamDebounceHandler.removeCallbacksAndMessages(null);
        }
        if (scraperTimeoutRunnable != null) {
            scraperTimeoutHandler.removeCallbacks(scraperTimeoutRunnable);
        }
        if (scraper != null) {
            scraper.stopLoading();
            scraper.loadUrl("about:blank");
            scraper = null;
        }
        if (hud != null && hud.isShowing()) {
            hud.dismiss();
        }
        if (scraper != null) {
            scraper.stopLoading();
        }
        hud = null;
        releasePlayer();
    }

    public void adjustUi(float textSize, boolean isFit) {
        playerView.setResizeMode(isFit ? AspectRatioFrameLayout.RESIZE_MODE_FIT : AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
        subtitleView.setFixedTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

    public void UiVisibility(ImageView imageView) {
        playerView.setControllerVisibilityListener(new PlayerView.ControllerVisibilityListener() {
            @Override
            public void onVisibilityChanged(int visibility) {
                imageView.setVisibility(visibility);
            }
        });
    }
}
