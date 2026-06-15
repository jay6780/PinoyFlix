package com.m.freemovie.Activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.audiofx.LoudnessEnhancer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.m.freemovie.Utils.DbHelper.WatchHistoryDBHelper;
import com.m.freemovie.Utils.GlobalWindowUtils;
import com.m.freemovie.Utils.LinearLayoutManagerWithSmoothScroller;
import com.m.freemovie.Utils.WindowUtils;
import com.m.freemovie.adapter.EpisodeAdapter;
import com.m.freemovie.databinding.ActivitySeasonListBinding;
import com.m.freemovie.mvp.Model.ClassBean.EpisodeBean;

import java.util.ArrayList;
import java.util.List;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySeasonListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        new GlobalWindowUtils(this,false);
        dbHelper = new WatchHistoryDBHelper(this);
        new WindowUtils(this, true, false);
        title = getIntent().getStringExtra("title");
        id = getIntent().getStringExtra("id");
        seasonId = getIntent().getStringExtra("seasonId");
        thumbImage = getIntent().getStringExtra("thumbImage");
        tvSeriesName = getIntent().getStringExtra("tvSeriesName");
        episodeCount = getIntent().getIntExtra("episodeCount", 0);
        seasonNum = getIntent().getIntExtra("seasonNum", 0);
//        Log.d("SeasonList","ids"+" videoId: "+id + " SeasonId: "+seasonId);

        binding.expand.setOnClickListener(view -> rotateScreen());
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

    private void rotateScreen() {
        finishing = false;
        binding.expand.setVisibility(View.INVISIBLE);
        binding.rvSeason.setVisibility(View.INVISIBLE);
        binding.btnBackFinish.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        binding.rlWebview.setLayoutParams(params);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
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
        new WindowUtils(this, true, false);
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
            if (binding != null && binding.webView != null) {
                binding.webView.stopLoading();
                binding.webView.setWebChromeClient(null);
                binding.webView.setWebViewClient(null);
                binding.webView.destroy();
                binding.webView.clearCache(true);
                binding.webView.clearHistory();
                binding.webView.reload();
            }
            finish();
        }
    }
    private int videoPosition;
    @Override
    public void getId(String id, int position, int seasonNum, int epNumber) {
        binding.webView.clearCache(true);
        switch (position) {
            case 1:
                videoPosition = 1;
                videoUrl = "https://player.videasy.to/tv/"+id+"/"+seasonNum+"/"+ epNumber;
                setupWebView(videoUrl);
                break;
            case 2:
                videoPosition = 2;
                videoUrl = "https://vidrock.ru/tv/" + id + "/" + seasonNum + "/" + epNumber + "&download=false";
                setupWebView(videoUrl);
                break;
            case 3:
                videoPosition = 3;
                videoUrl = "https://vidfast.pro/tv/"+id+"/"+seasonNum+"/"+ epNumber;
                setupWebView(videoUrl);
                break;
        }
    }


    private void setupWebView(String videoUrl) {
        binding.webView.setWebViewClient(new CustomWebViewClient());
        binding.webView.setWebChromeClient(new CustomWebChromeClient() {
        });
        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setSupportZoom(false);

        webSettings.setMediaPlaybackRequiresUserGesture(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            binding.webView.setWebContentsDebuggingEnabled(false);
        }
        if(videoPosition == 3){
            String htmlContent = "<!DOCTYPE html>" +
                    "<html>" +
                    "<head>" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                    "    <style>" +
                    "        .video-player {" +
                    "            position: fixed;" +
                    "            top: 0;" +
                    "            left: 0;" +
                    "            width: 100%;" +
                    "            height: 100%;" +
                    "            border: none;" +
                    "            object-fit: contain; /* Makes video fill while keeping aspect ratio */" +
                    "            background-color: #000; /* Black background for letterboxing */" +
                    "        }" +
                    "    </style>" +
                    "</head>" +
                    "<body style=\"margin:0;padding:0;overflow:hidden;background:#000;\">" +
                    "    <iframe src=\"" + videoUrl + "\"" +
                    "            class=\"video-player\"" +
                    "            allow=\"autoplay; encrypted-media; fullscreen\" " +
                    "            allowfullscreen>" +
                    "    </iframe>" +
                    "</body>" +
                    "</html>";

            binding.webView.loadDataWithBaseURL(
                    null,
                    htmlContent,
                    "text/html",
                    "UTF-8",
                    null
            );
        }else{
            binding.webView.loadUrl(videoUrl);
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
            public void onTick(long millisUntilFinished) {}
            public void onFinish() {
                binding.llVolume.setVisibility(View.GONE);
            }
        }.start();
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
    }

    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            return handleUrlLoading(view, url);
        }

        private boolean handleUrlLoading(WebView view, String url) {
            try {
                if (url.contains(videoUrl)) {
                    return false;
                } else {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            binding.webView.setVisibility(View.VISIBLE);
            binding.tvSelect.setVisibility(View.GONE);
            binding.expand.setVisibility(View.VISIBLE);
            super.onPageFinished(view, url);
        }

    }
}