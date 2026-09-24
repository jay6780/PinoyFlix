package com.m.freemovie.Activity;

import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.audiofx.LoudnessEnhancer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.Rational;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.ui.AspectRatioFrameLayout;
import androidx.media3.ui.SubtitleView;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.m.freemovie.R;
import com.m.freemovie.Retrofit.AppConstant;
import com.m.freemovie.Utils.DbHelper.WatchHistoryDBHelper;
import com.m.freemovie.Utils.GlobalWindowUtils;
import com.m.freemovie.Utils.LinearLayoutManagerWithSmoothScroller;
import com.m.freemovie.Utils.WindowUtils;
import com.m.freemovie.adapter.EpisodeAdapter;
import com.m.freemovie.databinding.ActivitySeasonListBinding;
import com.m.freemovie.mvp.Model.ClassBean.EpisodeBean;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@UnstableApi
public class SeasonListActivity extends AppCompatActivity implements EpisodeAdapter.SourceListener {
    private ActivitySeasonListBinding binding;
    private String title, id, thumbImage, seasonId, tvSeriesName;
    private int episodeCount, seasonNum;
    private EpisodeAdapter episodeAdapter;
    private List<EpisodeBean> episodeBeanList = new ArrayList<>();
    private WatchHistoryDBHelper dbHelper;
    private boolean finishing = true;
    private String videoUrl;
    private LoudnessEnhancer booster;
    private final int[] gainValues = {-3000, -2000, -1000, 0, 1000, 2000};
    private final String[] labels = {"0%", "25%", "50%", "100%", "150%", "200%"};
    private int currentLevelIndex = 3;
    private CountDownTimer volumeTimer;
    private AudioManager audioManager;
    private ExoPlayer exoPlayer;
    private DefaultTrackSelector trackSelector;
    private final List<String> discoveredSubtitleUrls = new ArrayList<>();
    private KProgressHUD hud;
    private WebView scraper;
    private final Handler scraperTimeoutHandler = new Handler(Looper.getMainLooper());
    private Runnable scraperTimeoutRunnable;
    private final Handler streamDebounceHandler = new Handler(Looper.getMainLooper());
    private Runnable triggerPlaybackRunnable;
    private String pendingStreamUrl;
    private java.util.Map<String, String> pendingStreamHeaders;
    private boolean hasStartedPlayback = false;
    private String player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySeasonListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.BLACK));
        getSupportActionBar().hide();
        new GlobalWindowUtils(this, false);
        dbHelper = new WatchHistoryDBHelper(this);
        defaultScreen();
        title = getIntent().getStringExtra("title");
        id = getIntent().getStringExtra("id");
        seasonId = getIntent().getStringExtra("seasonId");
        thumbImage = getIntent().getStringExtra("thumbImage");
        tvSeriesName = getIntent().getStringExtra("tvSeriesName");
        episodeCount = getIntent().getIntExtra("episodeCount", 0);
        seasonNum = getIntent().getIntExtra("seasonNum", 0);
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait");
//        Log.d("SeasonList","ids"+" videoId: "+id + " SeasonId: "+seasonId);

        binding.expand.setOnClickListener(view -> {
            if (finishing) {
                rotateScreen();
            }
        });
        binding.btnBackFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finishing) {
                    finish();
                } else {
                    defaultScreen();
                }
            }
        });
        binding.playerView.setOnClickListener(view -> hideControls());
        try {
            booster = new LoudnessEnhancer(0);
            booster.setEnabled(true);
            booster.setTargetGain(-1000);
            audioManager = (android.media.AudioManager) getSystemService(Context.AUDIO_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        binding.rvSeason.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(this));
        episodeAdapter = new EpisodeAdapter(this);
        binding.rvSeason.setAdapter(episodeAdapter);
        String lastWatchedEpisodeNumber = null;
        int lastWatchedPosition = -1;
        for (int i = 1; i <= episodeCount; i++) {
            EpisodeBean episode = new EpisodeBean(i, thumbImage, seasonNum, id, title, seasonId);
            boolean isWatched = dbHelper.isEpisodeWatched(id, seasonNum, i);
            episode.setWatched(isWatched);
            episodeBeanList.add(episode);
            if (isWatched) {
                lastWatchedPosition = episodeBeanList.size() - 1;
                lastWatchedEpisodeNumber = String.valueOf(i);
            }
        }

        binding.episodeTxt.setText(episodeCount > 1 ? "Episode's" : "Episode");
        episodeAdapter.setNewData(episodeBeanList);

        if (lastWatchedPosition != -1 && lastWatchedEpisodeNumber != null) {
            binding.rvSeason.smoothScrollToPosition(lastWatchedPosition);
            Toast.makeText(getApplicationContext(),
                    "Last Episode watched: Episode " + lastWatchedEpisodeNumber,
                    Toast.LENGTH_SHORT).show();
        }

        binding.llVolume.setEnabled(false);

        binding.volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && booster != null && audioManager != null) {
                    updateVolume(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (volumeTimer != null) volumeTimer.cancel();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                showVolumeUI();
            }
        });

    }

    private boolean isHide = false;

    private void hideControls() {
        isHide = !isHide;
        binding.btnBackFinish.setVisibility(isHide ? View.GONE : View.VISIBLE);
    }

    private int currentResizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM;

    public void toggleResizeMode() {
        if (binding.playerView == null) return;
        if (currentResizeMode == AspectRatioFrameLayout.RESIZE_MODE_ZOOM) {
            currentResizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL;
            Toast.makeText(this, "Stretch (Fill Screen)", Toast.LENGTH_SHORT).show();
        } else if (currentResizeMode == AspectRatioFrameLayout.RESIZE_MODE_FILL) {
            currentResizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT;
            Toast.makeText(this, "Original (Fit)", Toast.LENGTH_SHORT).show();
        } else {
            currentResizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM;
            Toast.makeText(this, "Full Screen (Zoom to Fill)", Toast.LENGTH_SHORT).show();
        }
        binding.playerView.setResizeMode(currentResizeMode);
    }

    private void rotateScreen() {
        finishing = false;
        binding.expand.setVisibility(View.GONE);
        binding.rvSeason.setVisibility(View.GONE);
        binding.btnBackFinish.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        binding.rlWebview.setLayoutParams(params);
        currentResizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM;
        if (binding.playerView != null) {
            binding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        binding.episodeTxt.setVisibility(View.GONE);
        new WindowUtils(this, true, false);
    }


    private void defaultScreen() {
        finishing = true;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding.expand.setVisibility(View.VISIBLE);
        binding.rvSeason.setVisibility(View.VISIBLE);
        binding.episodeTxt.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, dip2px(250));
        binding.rlWebview.setLayoutParams(params);
        if (binding.playerView != null) {
            binding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        }
        new WindowUtils(this, true, false);
    }

    private void resolveAndPlayStream(String webEmbedUrl) {
        discoveredSubtitleUrls.clear();
        pendingStreamUrl = null;
        pendingStreamHeaders = null;
        hasStartedPlayback = false;
        if (streamDebounceHandler != null) {
            streamDebounceHandler.removeCallbacksAndMessages(null);
        }
        if (scraperTimeoutHandler != null) {
            scraperTimeoutHandler.removeCallbacksAndMessages(null);
        }

        if (hud != null) {
            hud.setLabel("Finding video & subtitles...");
            if (!hud.isShowing()) {
                hud.show();
            }
        }

        if (isVideoStreamUrl(webEmbedUrl)) {
            playStream(webEmbedUrl, webEmbedUrl, null);
            return;
        }

        if (scraper == null) {
            scraper = new WebView(this);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            );
            scraper.setLayoutParams(lp);
            scraper.setAlpha(0.01f);
            scraper.setVisibility(View.VISIBLE);
            binding.rlWebview.addView(scraper, 0);
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

        try {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setAcceptThirdPartyCookies(scraper, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        scraper.setWebChromeClient(new WebChromeClient() {
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
                        schedulePlayback(streamUrl, webEmbedUrl, null);
                    }
                } else if (msg != null && msg.startsWith("EXTRACTED_TRACK_SRC:")) {
                    String trackUrl = msg.substring("EXTRACTED_TRACK_SRC:".length()).trim();
                    if (!trackUrl.isEmpty() && !discoveredSubtitleUrls.contains(trackUrl)) {
                        discoveredSubtitleUrls.add(trackUrl);
//                        Log.d("StreamScraper", "Extracted track subtitle URL: " + trackUrl);
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
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
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
//                    Log.d("StreamScraper", "Discovered subtitle URL: " + url);
                    if (hasStartedPlayback && exoPlayer != null) {
                        addSubtitleTrack(url);
                    }
                }

                if (isVideoStreamUrl(url)) {
                    java.util.Map<String, String> requestHeaders = request.getRequestHeaders();
                    schedulePlayback(url, webEmbedUrl, requestHeaders);
                }
                return super.shouldInterceptRequest(view, request);
            }
        });

        if (scraperTimeoutRunnable != null) {
            scraperTimeoutHandler.removeCallbacks(scraperTimeoutRunnable);
        }
        scraperTimeoutRunnable = () -> {
            if (hud != null && hud.isShowing()) {
                hud.dismiss();
            }
            Toast.makeText(getApplicationContext(), "Server response slow. Please select another server below.", Toast.LENGTH_SHORT).show();
        };
        scraperTimeoutHandler.postDelayed(scraperTimeoutRunnable, 20000);

        scraper.loadUrl(webEmbedUrl);
    }

    private void injectAutoplayScript(WebView view) {
        if (view == null) return;
        view.evaluateJavascript(
                "(function() {" +
                        "  var currentSource = '" + player + "-player';" +
                        "  function triggerPlay(w) {" +
                        "    if (!w) return;" +
                        "    try { w.postMessage({ source: currentSource, action: 'play' }, '*'); } catch(e){}" +
                        "    try { w.postMessage({ action: 'play' }, '*'); } catch(e){}" +
                        "    try { w.postMessage({ type: 'play' }, '*'); } catch(e){}" +
                        "    try { w.postMessage({ method: 'play' }, '*'); } catch(e){}" +
                        "    try { w.postMessage({ event: 'command', func: 'playVideo' }, '*'); } catch(e){}" +
                        "    try { w.postMessage('play', '*'); } catch(e){}" +
                        "  }" +
                        "  function checkDoc(doc) {" +
                        "    if (!doc) return;" +
                        "    try {" +
                        "      var trks = doc.querySelectorAll('track');" +
                        "      for (var t = 0; t < trks.length; t++) {" +
                        "        if (trks[t].src) console.log('EXTRACTED_TRACK_SRC:' + trks[t].src);" +
                        "      }" +
                        "      var vids = doc.querySelectorAll('video');" +
                        "      for (var j = 0; j < vids.length; j++) {" +
                        "        vids[j].muted = true;" +
                        "        vids[j].setAttribute('playsinline', '');" +
                        "        vids[j].play().catch(function(){});" +
                        "        if (vids[j].src && vids[j].src.indexOf('blob:') === -1) {" +
                        "          console.log('EXTRACTED_VIDEO_SRC:' + vids[j].src);" +
                        "        }" +
                        "        if (vids[j].currentSrc && vids[j].currentSrc.indexOf('blob:') === -1) {" +
                        "          console.log('EXTRACTED_VIDEO_SRC:' + vids[j].currentSrc);" +
                        "        }" +
                        "      }" +
                        "      var btns = doc.querySelectorAll('.play, .vjs-big-play-button, .jw-display-icon-display, button[aria-label*=\"play\" i], [class*=\"play-btn\" i], [class*=\"play_btn\" i], [id*=\"play\" i], [class*=\"player\" i] button, button');" +
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
                        "    var interval = setInterval(function() {" +
                        "      count++;" +
                        "      if (count > 50) { clearInterval(interval); return; }" +
                        "      try {" +
                        "        scanAll(window, document);" +
                        "      } catch(e) {}" +
                        "    }, 100);" +
                        "  }" +
                        "})();", null);
    }

    private void schedulePlayback(String url, String webEmbedUrl, java.util.Map<String, String> requestHeaders) {
        if (hasStartedPlayback) {
            return;
        }
        if (pendingStreamUrl == null || (!pendingStreamUrl.contains(".m3u8") && url.contains(".m3u8"))) {
            pendingStreamUrl = url;
            pendingStreamHeaders = requestHeaders;
//            Log.d("StreamScraper", "Found video stream candidate: " + url);

            runOnUiThread(() -> {
                if (hud != null && hud.isShowing()) {
                    hud.setLabel("Loading video & subtitles...");
                }
            });

            if (triggerPlaybackRunnable != null) {
                streamDebounceHandler.removeCallbacks(triggerPlaybackRunnable);
            }
            triggerPlaybackRunnable = () -> {
                if (pendingStreamUrl != null && !hasStartedPlayback) {
                    hasStartedPlayback = true;
                    if (scraperTimeoutRunnable != null) {
                        scraperTimeoutHandler.removeCallbacks(scraperTimeoutRunnable);
                    }
                    runOnUiThread(() -> {
                        if (hud != null && hud.isShowing()) {
                            hud.setLabel("Buffering video...");
                        }
                        playStream(pendingStreamUrl, webEmbedUrl, pendingStreamHeaders);
                    });
                }
            };
            streamDebounceHandler.postDelayed(triggerPlaybackRunnable, 400);
        }
    }

    private void addSubtitleTrack(String subUrl) {
        runOnUiThread(() -> {
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
//                Log.d("StreamPlayback", "Dynamically added subtitle track: " + label + " (" + subUrl + ")");
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
        releaseExoPlayerOnly();

        java.util.Map<String, String> defaultHeaders = new java.util.HashMap<>();
        if (interceptedHeaders != null) {
            for (java.util.Map.Entry<String, String> entry : interceptedHeaders.entrySet()) {
                String key = entry.getKey();
                if (key != null && !key.equalsIgnoreCase("Range")
                        && !key.equalsIgnoreCase("Host")
                        && !key.equalsIgnoreCase("Accept-Encoding")
                        && !key.equalsIgnoreCase("Content-Length")) {
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
        try {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.flush();
            String streamCookie = cookieManager.getCookie(streamUrl);
            String embedCookie = webEmbedUrl != null ? cookieManager.getCookie(webEmbedUrl) : null;
            String cookie = "";
            if (streamCookie != null && !streamCookie.isEmpty()) {
                cookie = streamCookie;
            }
            if (embedCookie != null && !embedCookie.isEmpty()) {
                cookie = cookie.isEmpty() ? embedCookie : (cookie + "; " + embedCookie);
            }
            if (!cookie.isEmpty() && !defaultHeaders.containsKey("Cookie") && !defaultHeaders.containsKey("cookie")) {
                defaultHeaders.put("Cookie", cookie);
            }
        } catch (Exception e) {
            Log.e("ExoPlayer", "Error obtaining cookies: ", e);
        }

        DefaultLoadControl loadControl = new DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                        3000,
                        30000,
                        500,
                        1000
                )
                .setPrioritizeTimeOverSizeThresholds(true)
                .build();

        DefaultHttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory()
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                .setDefaultRequestProperties(defaultHeaders)
                .setConnectTimeoutMs(8000)
                .setReadTimeoutMs(8000)
                .setAllowCrossProtocolRedirects(true);

        DefaultMediaSourceFactory mediaSourceFactory = new DefaultMediaSourceFactory(httpDataSourceFactory);

        trackSelector = new DefaultTrackSelector(this);
        DefaultTrackSelector.Parameters parameters = trackSelector.buildUponParameters()
                .setPreferredTextLanguage("en")
                .setSelectUndeterminedTextLanguage(true)
                .build();
        trackSelector.setParameters(parameters);

        exoPlayer = new ExoPlayer.Builder(this)
                .setMediaSourceFactory(mediaSourceFactory)
                .setTrackSelector(trackSelector)
                .setLoadControl(loadControl)
                .build();
        binding.playerView.setPlayer(exoPlayer);
        binding.playerView.setShowSubtitleButton(true);

        SubtitleView subtitleView = binding.playerView.getSubtitleView();
        if (subtitleView != null) {
            subtitleView.setVisibility(View.VISIBLE);
            subtitleView.setApplyEmbeddedFontSizes(false);
            subtitleView.setApplyEmbeddedStyles(false);
            subtitleView.setFixedTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
            subtitleView.setStyle(new androidx.media3.ui.CaptionStyleCompat(
                    Color.WHITE,
                    Color.argb(204, 0, 0, 0),
                    Color.TRANSPARENT,
                    androidx.media3.ui.CaptionStyleCompat.EDGE_TYPE_OUTLINE,
                    Color.BLACK,
                    null
            ));
        }

        binding.playerView.post(() -> {
            View subtitleBtn = binding.playerView.findViewById(androidx.media3.ui.R.id.exo_subtitle);
            if (subtitleBtn != null) {
                subtitleBtn.setOnClickListener(v -> showSubtitleDialog());
            }
        });

//        Log.d("StreamPlayback", "Playing stream: " + streamUrl);
//        Log.d("StreamPlayback", "Headers: " + defaultHeaders);

        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_READY) {
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
//                Log.d("ExoPlayerSubtitles", "onCues: " + (cueGroup != null ? cueGroup.cues.size() : 0));
            }

            @Override
            public void onTracksChanged(Tracks tracks) {
//                Log.d("ExoPlayerSubtitles", "Subtitles supported: " + tracks.isTypeSupported(C.TRACK_TYPE_TEXT) + ", selected: " + tracks.isTypeSelected(C.TRACK_TYPE_TEXT));
            }

            @Override
            public void onPlayerError(PlaybackException error) {
//                Log.e("ExoPlayer", "Playback error for " + streamUrl, error);
                if (hud != null && hud.isShowing()) {
                    hud.dismiss();
                }
                Toast.makeText(getApplicationContext(), "Playback error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
//            Log.d("StreamPlayback", "Attached " + subtitleConfigs.size() + " subtitles to MediaItem");
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
                scraper.evaluateJavascript(
                        "(function() {" +
                                "  try {" +
                                "    var vids = document.querySelectorAll('video');" +
                                "    for (var i = 0; i < vids.length; i++) {" +
                                "      vids[i].pause();" +
                                "      vids[i].muted = true;" +
                                "    }" +
                                "  } catch(e) {}" +
                                "})();", null);
                scraper.onPause();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        binding.playerView.setVisibility(View.VISIBLE);
        binding.tvSelect.setVisibility(View.GONE);
        exoPlayer.setMediaItem(mediaItemBuilder.build());
        exoPlayer.prepare();
        exoPlayer.setPlayWhenReady(true);
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
                || cleanUrl.contains("demo-video") || cleanUrl.contains("cdn")) {
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

    @androidx.annotation.OptIn(markerClass = androidx.media3.common.util.UnstableApi.class)
    private void showSubtitleDialog() {
        if (exoPlayer == null) {
            Toast.makeText(this, "Player is not ready yet", Toast.LENGTH_SHORT).show();
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

        if (displayNames.size() == 1) {
            new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                    .setTitle("Subtitles")
                    .setMessage("No subtitle tracks found in this video stream.\n\nSubtitles depend on the video server. If you need subtitles, please try selecting another server below (e.g. Player 2).")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        final int initialCheckedItem = checkedItem;
        String[] itemsArray = displayNames.toArray(new String[0]);

        new AlertDialog.Builder(this, R.style.AlertDialogTheme)
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
                        Toast.makeText(this, "Subtitles turned off", Toast.LENGTH_SHORT).show();
                    } else {
                        // User selected a specific subtitle language
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
                        Toast.makeText(this, "Selected: " + itemsArray[which], Toast.LENGTH_SHORT).show();
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
            if (binding != null && binding.playerView != null) {
                binding.playerView.setPlayer(null);
            }
            try {
                exoPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            exoPlayer = null;
        }
    }

    private void releasePlayer() {
        stopScraper();
        releaseExoPlayerOnly();
        videoUrl = "";
        if (binding != null && binding.playerView != null) {
            binding.playerView.setVisibility(View.GONE);
        }
        if (binding != null && binding.tvSelect != null) {
            binding.tvSelect.setVisibility(View.VISIBLE);
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


    public int dip2px(float dpValue) {
        final float scale = getResources(this).getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static Resources getResources(Context context) {
        return context.getResources();
    }

    @Override
    public void onBackPressed() {
        if (!finishing) {
            defaultScreen();
        } else {
            super.onBackPressed();
            if (dbHelper != null) {
                dbHelper.close();
            }
            releasePlayer();
            finish();
        }
    }

    @Override
    public void getId(String id, int position, int seasonNum, int epNumber) {
        releasePlayer();
        switch (position) {
            case 1:
                player = AppConstant.VIDROCK;
                videoUrl = "https://vidrock.to/tv/" + id + "/" + seasonNum + "/" + epNumber + "&download=false";
                break;
            case 2:
                player = AppConstant.MOVIESAPI;
                videoUrl = "https://moviesapi.to/tv/" + id + "/" + seasonNum + "/" + epNumber;
//                Log.d("VideoUrl","bal: "+videoUrl);
                break;
        }
        binding.tvSelect.setVisibility(View.GONE);
        resolveAndPlayStream(videoUrl);
    }


    private String getBaseUrl(String url) {
        try {
            URL parsedUrl = new URL(url);
            return parsedUrl.getProtocol() + "://" + parsedUrl.getHost() + "/";
        } catch (MalformedURLException e) {
            return "https://";
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);

        if (isInPictureInPictureMode) {
            binding.rvSeason.setVisibility(View.GONE);
            binding.expand.setVisibility(View.GONE);
            binding.btnBackFinish.setVisibility(View.GONE);
            binding.tvSelect.setVisibility(View.GONE);

            RelativeLayout.LayoutParams pipParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            binding.rlWebview.setLayoutParams(pipParams);

        } else {
            binding.rvSeason.setVisibility(View.VISIBLE);
            binding.tvSelect.setVisibility(binding.playerView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            binding.btnBackFinish.setVisibility(View.VISIBLE);
            if (finishing) {
                defaultScreen();
            } else {
                binding.expand.setVisibility(View.INVISIBLE);
                rotateScreen();
            }
        }
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                && !TextUtils.isEmpty(videoUrl)
                && binding.playerView.getVisibility() == View.VISIBLE) {
            enterPip();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void enterPip() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
            return;
        }

        Display d = getWindowManager().getDefaultDisplay();
        Point p = new Point();
        d.getSize(p);
        int width = p.x;
        int height = p.y;

        Rational ratio = new Rational(width, height);

        PictureInPictureParams.Builder pipBuilder = new PictureInPictureParams.Builder();
        pipBuilder.setAspectRatio(ratio);

        try {
            enterPictureInPictureMode(pipBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void updateVolume(int index) {
        if (index < 0 || index >= gainValues.length) return;

        currentLevelIndex = index;
        booster.setTargetGain(gainValues[index]);
        int maxSystemVolume = audioManager.getStreamMaxVolume(android.media.AudioManager.STREAM_MUSIC);
        int targetSystemVol = (index * maxSystemVolume) / 5;
        audioManager.setStreamVolume(android.media.AudioManager.STREAM_MUSIC, targetSystemVol, 0);
        binding.volumeSeekBar.setProgress(index);
        binding.volumeText.setText(labels[index]);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (booster == null) return super.onKeyDown(keyCode, event);
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                binding.llVolume.setVisibility(View.VISIBLE);
                if (currentLevelIndex < 5) updateVolume(currentLevelIndex + 1);
                showVolumeUI();
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                binding.llVolume.setVisibility(View.VISIBLE);
                if (currentLevelIndex > 0) updateVolume(currentLevelIndex - 1);
                showVolumeUI();
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    private void showVolumeUI() {
        binding.llVolume.setVisibility(View.VISIBLE);
        if (volumeTimer != null) volumeTimer.cancel();

        volumeTimer = new CountDownTimer(3500, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                binding.llVolume.setVisibility(View.GONE);
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isInPictureInPictureMode()) {
            return;
        }
        if (binding != null && binding.playerView != null) {
            binding.playerView.onPause();
        }
        if (exoPlayer != null) {
            exoPlayer.pause();
        }
        if (scraper != null) {
            scraper.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (binding != null && binding.playerView != null) {
            binding.playerView.onResume();
        }
        if (exoPlayer != null) {
            exoPlayer.play();
        }
        if (scraper != null) {
            scraper.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        if (streamDebounceHandler != null) {
            streamDebounceHandler.removeCallbacksAndMessages(null);
        }
        if (scraperTimeoutHandler != null) {
            scraperTimeoutHandler.removeCallbacksAndMessages(null);
        }
        if (scraper != null) {
            scraper.stopLoading();
            scraper.loadUrl("about:blank");
            scraper = null;
        }
        if (hud != null && hud.isShowing()) {
            hud.dismiss();
        }
        hud = null;
        releasePlayer();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (finishing) {
                rotateScreen();
            }
        } else {
            if (!finishing) {
                defaultScreen();
            }
        }
    }

}
