package com.m.freemovie.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.audiofx.LoudnessEnhancer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.core.Controller;
import com.app.hubert.guide.listener.OnGuideChangedListener;
import com.app.hubert.guide.model.GuidePage;
import com.app.hubert.guide.model.HighLight;
import com.m.freemovie.R;
import com.m.freemovie.Retrofit.AppConstant;
import com.m.freemovie.Utils.DbHelper.BookmarkDbHelper;
import com.m.freemovie.Utils.DbHelper.PinoyWatchHistoryHelper;
import com.m.freemovie.Utils.DbHelper.SpinnerTotalDbHelper;
import com.m.freemovie.Utils.GlobalWindowUtils;
import com.m.freemovie.Utils.LinearLayoutManagerWithSmoothScroller;
import com.m.freemovie.Utils.SPUtils;
import com.m.freemovie.Utils.WindowUtils;
import com.m.freemovie.adapter.AnimePaheDetailAdapter;
import com.m.freemovie.databinding.ActivityAnimePaheWebviewBinding;
import com.m.freemovie.mvp.Contract.AnimePaheDetailContract;
import com.m.freemovie.mvp.Model.ClassBean.AnimePaheBeanList;
import com.m.freemovie.mvp.Model.ClassBean.AnimePaheDownloadBean;
import com.m.freemovie.mvp.Model.ClassBean.DetailBean;
import com.m.freemovie.mvp.Model.ClassBean.ZoRoDetailBean;
import com.m.freemovie.mvp.Model.ClassBean.ZoRoVideoUrlBean;
import com.m.freemovie.mvp.Presenter.AnimePaheDetailPresenter;

import org.mozilla.geckoview.AllowOrDeny;
import org.mozilla.geckoview.GeckoResult;
import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoSessionSettings;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AnimePaheWebviewActivity extends AppCompatActivity
        implements AnimePaheDetailContract.View, AnimePaheDetailAdapter.EpisodeListener, AdapterView.OnItemSelectedListener {
    private ActivityAnimePaheWebviewBinding binding;
    private String id, title;
    private AnimePaheDetailAdapter episodeAdapter;
    private List<AnimePaheBeanList> episodeBeanList = new ArrayList<>();
    private boolean finishing = true;
    private String videoUrl = "";
    private PinoyWatchHistoryHelper dbHelper;
    private BookmarkDbHelper bookmarkDbHelper;
    private AnimePaheDetailPresenter detailPresenter;
    private List<AnimePaheDownloadBean.ResultsBean.StreamingBean> streamingBeanList = new ArrayList<>();
    private List<AnimePaheDownloadBean.ResultsBean.DownloadBean> downloadBeanList = new ArrayList<>();
    private int page = 1;
    private boolean isNomore = false;
    private boolean isInit = true;
    private RelativeLayout.LayoutParams params;
    private SpinnerTotalDbHelper spinnerTotalDbHelper;
    private int totalPages = 0;
    private List<String> spinnerItems = new ArrayList<>();
    private LoudnessEnhancer booster;
    private final int[] gainValues = {-3000, -2000, -1000, 0, 1000, 2000};
    private final String[] labels = {"0%", "25%", "50%", "100%", "150%", "200%"};
    private int currentLevelIndex = 3;
    private CountDownTimer volumeTimer;
    private AudioManager audioManager;
    private String imageUrl;
    private GeckoRuntime geckoRuntime;
    private GeckoSession geckoSession;
    private Handler touchWatchdog = new Handler(Looper.getMainLooper());
    private Runnable freezeDetector;
    private boolean isPageLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnimePaheWebviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        new GlobalWindowUtils(this, false);
        initGuide();
        new WindowUtils(this, true, false);
        title = getIntent().getStringExtra("title");
        imageUrl = getIntent().getStringExtra("imageUrl");
//        Log.d("AnimeTitle","val: "+title);
        id = getIntent().getStringExtra("id");
//        Log.d("SeasonList","ids"+" videoId: "+id + " SeasonId: "+seasonId);
        bookmarkDbHelper = new BookmarkDbHelper(this);
        dbHelper = new PinoyWatchHistoryHelper(this);
        binding.expand.setOnClickListener(view -> rotateScreen());
        binding.llBookmark.setOnClickListener(view -> savedBook());
        detailPresenter = new AnimePaheDetailPresenter(this);
        spinnerTotalDbHelper = new SpinnerTotalDbHelper(this);
        if (isNetworkAvailable()) {
            detailPresenter.getZoroUrl(id);
        } else {
            Toast.makeText(getApplicationContext(), "Please check internet and try again", Toast.LENGTH_SHORT).show();
        }

        setImageData(id);
        SPUtils.getInstance().put(AppConstant.isShow, false);

        geckoRuntime = GeckoRuntime.getDefault(this);
        initializeSpinnerItems();

        if (isInit) {
            binding.swipe.setRefreshing(true);
        }
        binding.swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isNetworkAvailable()) {
                    Toast.makeText(getApplicationContext(), "Please check internet and try again", Toast.LENGTH_SHORT).show();
                    return;
                }
                page = 1;
                isNomore = false;
                isInit = true;
                SPUtils.getInstance().put(AppConstant.isShow, false);
                detailPresenter.getZoroUrl(id);
                episodeBeanList.clear();
                episodeAdapter.setNewData(new ArrayList<>());
            }
        });
        binding.btnBackFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finishing) {
                    shutdownGeckoSession();
                    finish();
                } else {
                    defaultScreen();
                }
            }
        });

        binding.rvSeason.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(this));
        episodeAdapter = new AnimePaheDetailAdapter(this);
        binding.rvSeason.setAdapter(episodeAdapter);
        episodeAdapter.setNewData(episodeBeanList);

        try {
            booster = new LoudnessEnhancer(0);
            booster.setEnabled(true);
            booster.setTargetGain(-1000);
            audioManager = (android.media.AudioManager) getSystemService(Context.AUDIO_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }


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


        binding.llVolume.setEnabled(false);
    }

    private void initializeSpinnerItems() {
        spinnerItems.clear();

        totalPages = spinnerTotalDbHelper.getTotalPages(id) == 0 ? 5 : spinnerTotalDbHelper.getTotalPages(id);
        for (int i = 1; i <= totalPages; i++) {
            spinnerItems.add(String.valueOf(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, spinnerItems);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        binding.spinner.setAdapter(adapter);
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
                binding.swipe.setEnabled(false);
                if (currentLevelIndex < 5) updateVolume(currentLevelIndex + 1);
                showVolumeUI();
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                binding.llVolume.setVisibility(View.VISIBLE);
                if (currentLevelIndex > 0) updateVolume(currentLevelIndex - 1);
                showVolumeUI();
                binding.swipe.setEnabled(false);
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


    private void loadAdsFailed() {
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.BELOW, binding.rlOption.getId());
        binding.rvSeason.setLayoutParams(params);
    }

    private void initGuide() {
        NewbieGuide.with(this)
                .setLabel("animepahe_bookmark")
                .setOnGuideChangedListener(new OnGuideChangedListener() {
                    @Override
                    public void onShowed(Controller controller) {

                    }

                    @Override
                    public void onRemoved(Controller controller) {
                    }
                })
                .addGuidePage(GuidePage.newInstance()
                        .addHighLight(binding.llBookmark, HighLight.Shape.ROUND_RECTANGLE, 1)
                        .setLayoutRes(R.layout.bookmark_highlight)
                )
                .show();

    }


    private void rotateScreen() {
        finishing = false;
        binding.expand.setVisibility(View.INVISIBLE);
        binding.rvSeason.setVisibility(View.INVISIBLE);
        binding.btnBackFinish.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        binding.rlWebview.setLayoutParams(params);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        binding.llBookmark.setVisibility(View.GONE);
        binding.swipe.setEnabled(false);
        loadAdsFailed();
        new WindowUtils(this, true, false);
    }


    private void defaultScreen() {
        finishing = true;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding.expand.setVisibility(View.VISIBLE);
        binding.rvSeason.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, dip2px(250));
        binding.rlWebview.setLayoutParams(params);
        binding.llBookmark.setVisibility(View.VISIBLE);
        binding.swipe.setEnabled(true);
        new WindowUtils(this, true, false);
    }

    public int dip2px(float dpValue) {
        final float scale = getResources(this).getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static Resources getResources(Context context) {
        return context.getResources();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onBackPressed() {
        if (!finishing) {
            defaultScreen();
        } else {
            super.onBackPressed();
            shutdownGeckoSession();
            finish();
        }
    }


    private void shutdownGeckoSession() {
        if (geckoSession != null) {
            try {
                geckoSession.stop();
                geckoSession.setNavigationDelegate(null);
                geckoSession.setProgressDelegate(null);
                geckoSession.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                geckoSession = null;
            }
        }
    }

    private void savedBook() {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        DetailBean details = new DetailBean(id, timestamp, imageUrl, title, "false");
        details.setVideoId(id);
        details.setTimeStamp(timestamp);
        bookmarkDbHelper.toggleBookmark(details, 7);
        setImageData(id);
    }

    private void setImageData(String videoId) {
        boolean isBookmarked = bookmarkDbHelper.isBookmarked(videoId);
        binding.ivHeart.setImageResource(!isBookmarked ? R.mipmap.heart_no : R.mipmap.heart_yes);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("MissingPermission")
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void setupWebView(String videoUrl) {
        this.videoUrl = videoUrl;
        org.mozilla.geckoview.GeckoView geckoView = findViewById(R.id.webView);

        if (geckoSession == null) {
            GeckoSessionSettings settings = new GeckoSessionSettings.Builder()
                    .usePrivateMode(false)
                    .allowJavascript(true)
                    .useTrackingProtection(true)
                    .build();

            geckoSession = new GeckoSession(settings);
            geckoSession.open(geckoRuntime);

            geckoView.setSession(geckoSession);
        }
        geckoSession.setContentDelegate(new CustomContentDelegate());
        geckoSession.setNavigationDelegate(new CustomNavigationDelegate());
        geckoSession.setProgressDelegate(new CustomProgressDelegate());
        geckoSession.setPromptDelegate(new CustomPromptDelegate());
        geckoSession.loadUri(videoUrl);
    }

    @Override
    public void showLoading() {
        if (!binding.swipe.isEnabled()) {
            binding.swipe.setEnabled(true);
        }
        if (!isInit) {
            binding.swipe.setRefreshing(true);
        }
    }

    @Override
    public void showError(String error) {
        Toast.makeText(this, "Error fetching data: " + error, Toast.LENGTH_SHORT).show();
        if (binding.swipe.isRefreshing()) {
            binding.swipe.setRefreshing(false);
        }
    }

    @Override
    public void hideLoading() {
        try {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.swipe.setRefreshing(false);
                }
            }, 500);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    int lastWatchedPosition = -1;

    @Override
    public void getZoroDetail(ZoRoDetailBean zoRoDetailBean) {
        if (zoRoDetailBean != null) {
            if (zoRoDetailBean.getEpisodes() != null) {

                for (ZoRoDetailBean.EpisodesBean dataBean : zoRoDetailBean.getEpisodes()) {
                    AnimePaheBeanList detailBean = new AnimePaheBeanList(dataBean.getUrl(), String.valueOf(dataBean.getNumber()), imageUrl, "");
                    boolean isWatched = dbHelper.isEpisodeWatched(id, String.valueOf(dataBean.getNumber()));
                    detailBean.setVideoId(id);
                    detailBean.setWatched(isWatched);

                    episodeBeanList.add(detailBean);
                }

                Collections.sort(episodeBeanList, new Comparator<AnimePaheBeanList>() {
                    @Override
                    public int compare(AnimePaheBeanList v1, AnimePaheBeanList v2) {
                        return Long.compare(Long.parseLong(v1.getEpisode()), Long.parseLong(v2.getEpisode()));
                    }
                });

                lastWatchedPosition = -1;
                for (int i = 0; i < episodeBeanList.size(); i++) {
                    if (episodeBeanList.get(i).isWatched()) {
                        lastWatchedPosition = i;
                    }
                }

                episodeAdapter.setNewData(episodeBeanList);

                if (lastWatchedPosition != -1) {
                    binding.rvSeason.postDelayed(() -> {
                        if (binding.rvSeason.getLayoutManager() != null) {
                            Toast.makeText(getApplicationContext(), "Continuing from last watched episode", Toast.LENGTH_SHORT).show();
                            binding.rvSeason.smoothScrollToPosition(lastWatchedPosition);
                        }
                    }, 300);
                }

                binding.episodeTxt.setText(episodeBeanList.size() > 1 ? "Episode's" : "Episode");

            } else {
                isNomore = true;
            }
        }
    }

    @Override
    public void getZoRoVideo(ZoRoVideoUrlBean zoRoVideoUrlBean) {
        if (zoRoVideoUrlBean != null && zoRoVideoUrlBean.getVideoUrl() != null) {
            if (binding.webView.getVisibility() == View.GONE) {
                binding.webView.setVisibility(View.VISIBLE);
                binding.tvSelect.setVisibility(View.GONE);
                binding.expand.setVisibility(View.VISIBLE);
            }
            setupWebView(zoRoVideoUrlBean.getVideoUrl());
        }

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        if (isNomore) {
            Toast.makeText(getApplicationContext(), "No more data, refreshing...", Toast.LENGTH_SHORT).show();
            isNomore = false;
            page = 1;
            episodeBeanList.clear();
            episodeAdapter.setNewData(episodeBeanList);
            binding.spinner.setSelection(0);
            return;
        }

        page = position + 1;
        if (position == spinnerItems.size() - 1) {
            totalPages++;
            spinnerTotalDbHelper.saveOrUpdateShowPages(id, totalPages, page);
            spinnerItems.add(String.valueOf(totalPages));
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) binding.spinner.getAdapter();
            adapter.notifyDataSetChanged();
        }

        episodeBeanList.clear();
        binding.rvSeason.scrollToPosition(0);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void getVideoUrl(String videoUrl) {
        if (TextUtils.isEmpty(videoUrl)) {
            return;
        }
//        Log.d("VideoUrl","val: "+videoUrl);
        detailPresenter.getZoRoVideoUrl(videoUrl);
    }

    private String url;
    private class CustomNavigationDelegate implements GeckoSession.NavigationDelegate {

        @Nullable
        @Override
        public GeckoResult<AllowOrDeny> onLoadRequest(
                @NonNull GeckoSession session,
                @NonNull LoadRequest request) {

            String uri = request.uri;
            url = uri;

            if (isAdUrl(uri)) {
                session.stop();
            }
            if (uri.equals("about:blank")) {
                session.stop();
            }

            if (videoUrl != null && !videoUrl.isEmpty()) {
                try {
                    java.net.URI mainUri = new java.net.URI(videoUrl);
                    java.net.URI reqUri = new java.net.URI(uri);
                    String mainHost = mainUri.getHost();
                    String reqHost = reqUri.getHost();
                    if (reqHost != null && mainHost != null && reqHost.endsWith(mainHost)) {
                        return GeckoResult.fromValue(AllowOrDeny.ALLOW);
                    }
                    if (uri.startsWith("data:") || uri.startsWith("blob:")) {
                        return GeckoResult.fromValue(AllowOrDeny.ALLOW);
                    }
                } catch (Exception e) {
//                    Log.e("NavDelegate", "URI parse error: " + e.getMessage());
                }
            }

            if (uri.equals(videoUrl)) {
                return GeckoResult.fromValue(AllowOrDeny.ALLOW);
            }else{
                session.stop();
            }
            return GeckoResult.fromValue(AllowOrDeny.DENY);
        }

        @Nullable
        @Override
        public GeckoResult<AllowOrDeny> onSubframeLoadRequest(
                @NonNull GeckoSession session,
                @NonNull LoadRequest request) {

            String uri = request.uri;
            if (uri.equals("about:blank")) {
                session.stop();
            }
            if (isAdUrl(uri)) {
                session.stop();
            }
            return GeckoResult.fromValue(AllowOrDeny.ALLOW);
        }
    }

    private boolean isAdUrl(String uri) {
        if (uri == null) return true;

        String[] adDomains = {
                "ak.itponytaa.com",
                "071kk.com/clicks",
                "bingoplus.com",
                "www.okbet.com",
                "064kk.com/clicks",
                "1xlite-",
                "b7510.com",
                "clicks",
                "064kk.com",
                "doubleclick.net",
                "googlesyndication.com",
                "adservice.google.com",
                "ads.yahoo.com",
                "amazon-adsystem.com",
                "outbrain.com",
                "taboola.com",
                "popads.net",
                "popcash.net",
                "trafficjunky.com",
                "exoclick.com",
                "juicyads.com",
                "adsterra.com",
                "propellerads.com"
        };

        for (String domain : adDomains) {
            if (uri.contains(domain)) return true;
        }
        return false;
    }

    private class CustomPromptDelegate implements GeckoSession.PromptDelegate {

        @Nullable
        @Override
        public GeckoResult<PromptResponse> onAlertPrompt(@NonNull GeckoSession session, @NonNull AlertPrompt prompt) {
            return GeckoResult.fromValue(prompt.dismiss());
        }

        @Nullable
        @Override
        public GeckoResult<PromptResponse> onButtonPrompt(@NonNull GeckoSession session, @NonNull ButtonPrompt prompt) {
            return GeckoResult.fromValue(prompt.dismiss());
        }

        @Nullable
        @Override
        public GeckoResult<PromptResponse> onTextPrompt(@NonNull GeckoSession session, @NonNull TextPrompt prompt) {
            return GeckoResult.fromValue(prompt.dismiss());
        }

        @Nullable
        @Override
        public GeckoResult<PromptResponse> onBeforeUnloadPrompt(@NonNull GeckoSession session, @NonNull BeforeUnloadPrompt prompt) {
            return GeckoResult.fromValue(prompt.dismiss());
        }
    }


    private class CustomProgressDelegate implements GeckoSession.ProgressDelegate {
        @Override
        public void onPageStop(@NonNull GeckoSession session, boolean success) {
            if(success){
                if(!url.equals(videoUrl)){
                    session.stop();
                }

            }
        }
    }


    private class CustomContentDelegate implements GeckoSession.ContentDelegate {
        @Override
        public void onFirstContentfulPaint(@NonNull GeckoSession session) {
        }
    }
}