package com.m.freemovie.Activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.m.freemovie.Utils.DbHelper.WatchHistoryDBHelper;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySeasonListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
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
        binding.titleName.setText(tvSeriesName);

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

    @Override
    public void getId(String id, int position, int seasonNum, int epNumber) {
        binding.webView.clearCache(true);
        switch (position) {
            case 1:
                videoUrl = "https://player.videasy.net/tv/"+id+"/"+seasonNum+"/"+ epNumber;
                binding.titleName.setVisibility(View.VISIBLE);
                setupWebView(videoUrl);
                break;
            case 2:
                videoUrl = "https://vidrock.net/tv/" + id + "/" + seasonNum + "/" + epNumber + "&download=false";
                binding.titleName.setVisibility(View.GONE);
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
                    view.stopLoading();
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