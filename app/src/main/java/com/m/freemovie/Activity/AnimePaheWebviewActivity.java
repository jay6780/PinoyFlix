package com.m.freemovie.Activity;

import android.annotation.SuppressLint;
import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
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
import android.util.Rational;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.m.freemovie.adapter.AniKoToSourceAdapter;
import com.m.freemovie.adapter.AnimePaheDetailAdapter;
import com.m.freemovie.adapter.QualityAdapter;
import com.m.freemovie.databinding.ActivityAnimePaheWebviewBinding;
import com.m.freemovie.mvp.Contract.AnimePaheDetailContract;
import com.m.freemovie.mvp.Model.ClassBean.AniKoToWatchBean;
import com.m.freemovie.mvp.Model.ClassBean.AniNeKoInfoBean;
import com.m.freemovie.mvp.Model.ClassBean.AniNekoEpisodeBean;
import com.m.freemovie.mvp.Model.ClassBean.AnimePaheBeanList;
import com.m.freemovie.mvp.Model.ClassBean.AnimePaheDownloadBean;
import com.m.freemovie.mvp.Model.ClassBean.DetailBean;
import com.m.freemovie.mvp.Model.ClassBean.ZoRoDetailBean;
import com.m.freemovie.mvp.Model.ClassBean.ZoRoVideoUrlBean;
import com.m.freemovie.mvp.Presenter.AnimePaheDetailPresenter;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AnimePaheWebviewActivity extends AppCompatActivity
        implements AnimePaheDetailContract.View, AnimePaheDetailAdapter.EpisodeListener, AdapterView.OnItemSelectedListener, QualityAdapter.SrcListener, AniKoToSourceAdapter.AniKoToSourceListener {
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
    private boolean isAniNeko;

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
        isAniNeko = getIntent().getBooleanExtra("isAniNeko", false);
//        Log.d("isAniNeko","val: "+isAniNeko);
//        Log.d("AnimeTitle","val: "+title);
        id = getIntent().getStringExtra("id");
//        Log.d("SeasonList","ids"+" videoId: "+id + " SeasonId: "+seasonId);
        bookmarkDbHelper = new BookmarkDbHelper(this);
        dbHelper = new PinoyWatchHistoryHelper(this);
        binding.expand.setOnClickListener(view -> rotateScreen());
        binding.llBookmark.setOnClickListener(view -> savedBook());
        detailPresenter = new AnimePaheDetailPresenter(this);
        spinnerTotalDbHelper = new SpinnerTotalDbHelper(this);

        if (id == null) {
            Toast.makeText(getApplicationContext(), "No Episode Available", Toast.LENGTH_SHORT).show();
            finish();
            return;

        }

        if (isNetworkAvailable()) {
            if (isAniNeko) {
                detailPresenter.getAniNekoUrl(id);
            } else {
                detailPresenter.getAniKoToID(id);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please check internet and try again", Toast.LENGTH_SHORT).show();
        }

        setImageData(id);
        SPUtils.getInstance().put(AppConstant.isShow, false);
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
                if (isAniNeko) {
                    detailPresenter.getAniNekoUrl(id);
                } else {
                    detailPresenter.getAniKoToID(id);
                }
                episodeBeanList.clear();
                episodeAdapter.setNewData(new ArrayList<>());
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

        binding.rvSeason.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(this));
        episodeAdapter = new AnimePaheDetailAdapter(this, isAniNeko);
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


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);

        if (isInPictureInPictureMode) {
            binding.rvSeason.setVisibility(View.GONE);
            binding.llBookmark.setVisibility(View.GONE);
            binding.expand.setVisibility(View.GONE);
            binding.btnBackFinish.setVisibility(View.GONE);
            binding.tvSelect.setVisibility(View.GONE);
            binding.rlOption.setVisibility(View.GONE);
            binding.swipe.setEnabled(false);

            RelativeLayout.LayoutParams pipParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            binding.rlWebview.setLayoutParams(pipParams);

        } else {
            binding.rvSeason.setVisibility(View.VISIBLE);
            binding.llBookmark.setVisibility(View.VISIBLE);
            binding.tvSelect.setVisibility(binding.webView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            binding.rlOption.setVisibility(View.VISIBLE);
            binding.swipe.setEnabled(finishing);
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
                && binding.webView.getVisibility() == View.VISIBLE) {
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
            if (binding != null && binding.webView != null) {
                binding.webView.stopLoading();
                binding.webView.setWebChromeClient(null);
                binding.webView.setWebViewClient(null);
                binding.webView.destroy();
                binding.webView.clearCache(true);
                binding.webView.clearHistory();
                binding.webView.reload();
            }
            super.onBackPressed();
            finish();
        }
    }


    private void savedBook() {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        DetailBean details = new DetailBean(id, timestamp, imageUrl, title, "false");
        details.setVideoId(id);
        details.setTimeStamp(timestamp);
        bookmarkDbHelper.toggleBookmark(details, isAniNeko ? 3 : 7);
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
        if (!isNetworkAvailable()) {
            Toast.makeText(getApplicationContext(), "Please check internet and try again", Toast.LENGTH_SHORT).show();
            return;
        }
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
        if(!isAniNeko){
            String html = "<!DOCTYPE html><html>" +
                    "<head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\">" +
                    "<style>body,html{margin:0;padding:0;width:100%;height:100%;background-color:#000;overflow:hidden;}" +
                    "iframe{border:none;width:100%;height:100%;}</style></head>" +
                    "<body><iframe src=\"" + videoUrl + "\" allow=\"autoplay; fullscreen\" allowfullscreen=\"true\"></iframe></body></html>";
            binding.webView.loadDataWithBaseURL("https://anikoto.cz/", html, "text/html", "UTF-8", null);
        }else{
            binding.webView.loadUrl(videoUrl);
        }


    }

    @Override
    public void getSrc(String videoUrl) {
        if (TextUtils.isEmpty(videoUrl)) {
            return;
        }
        clearCache();
        if (binding.webView.getVisibility() == View.GONE) {
            binding.webView.setVisibility(View.VISIBLE);
            binding.tvSelect.setVisibility(View.GONE);
            binding.expand.setVisibility(View.VISIBLE);
        }
        if (sourceDialog != null && sourceDialog.isShowing()) {
            dbHelper.markEpisodeAsWatched(animePaheBeanList.getVideoId(), animePaheBeanList.getEpisode());
            animePaheBeanList.setWatched(true);
            sourceDialog.dismiss();

        }
        if(anikoToSourceDialog !=null && anikoToSourceDialog.isShowing()){
            dbHelper.markEpisodeAsWatched(animePaheBeanList.getVideoId(), animePaheBeanList.getEpisode());
            animePaheBeanList.setWatched(true);
            anikoToSourceDialog.dismiss();
        }

        if(episodeAdapter !=null){
            episodeAdapter.notifyDataSetChanged();
        }

        this.videoUrl = videoUrl;
        setupWebView(videoUrl);
    }

    private void clearCache(){
        if (binding != null && binding.webView != null) {
            binding.webView.clearCache(true);
            binding.webView.clearHistory();
        }
    }

    private class CustomWebChromeClient extends WebChromeClient {
        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            return true;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (!isNetworkAvailable()) {
                return;
            }
            if (newProgress == 100) {
                binding.tvSelect.setVisibility(View.GONE);
            }
        }
    }

    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            return handleUrlLoading(view, url);
        }


        private boolean handleUrlLoading(WebView view, String url) {
//            Log.d("DownloadUrl","val: "+url);
            if (url.contains(videoUrl)) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

        }
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
                    AnimePaheBeanList detailBean = new AnimePaheBeanList(dataBean.getUrl(), String.valueOf(dataBean.getNumber()), imageUrl, "",new ArrayList<>());
                    boolean isWatched = dbHelper.isEpisodeWatched(id, String.valueOf(dataBean.getNumber()));
                    detailBean.setVideoId(id);
                    detailBean.setWatched(isWatched);

                    episodeBeanList.add(detailBean);
                }

//                Collections.sort(episodeBeanList, new Comparator<AnimePaheBeanList>() {
//                    @Override
//                    public int compare(AnimePaheBeanList v1, AnimePaheBeanList v2) {
//                        return Long.compare(Long.parseLong(v1.getEpisode()), Long.parseLong(v2.getEpisode()));
//                    }
//                });

                lastWatchedPosition = -1;
                for (int i = 0; i < episodeBeanList.size(); i++) {
                    if (episodeBeanList.get(i).isWatched()) {
                        lastWatchedPosition = i;
                    }
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

    @Override
    public void getZoRoVideo(ZoRoVideoUrlBean zoRoVideoUrlBean) {
        if (zoRoVideoUrlBean != null && zoRoVideoUrlBean.getVideoUrl() != null) {
            if (binding.webView.getVisibility() == View.GONE) {
                binding.webView.setVisibility(View.VISIBLE);
                binding.tvSelect.setVisibility(View.GONE);
                binding.expand.setVisibility(View.VISIBLE);
            }
            this.videoUrl = zoRoVideoUrlBean.getVideoUrl();
            setupWebView(zoRoVideoUrlBean.getVideoUrl());
        }

    }

    @Override
    public void getAniKoToEpisode(AniKoToWatchBean aniKoToWatchBean) {
        if (aniKoToWatchBean != null) {
            if (aniKoToWatchBean.getEpisodes().size() != 0) {

                for (AniKoToWatchBean.EpisodesBean dataBean : aniKoToWatchBean.getEpisodes()) {
                    if (dataBean.getServers().size() != 0) {
                        AnimePaheBeanList detailBean = new AnimePaheBeanList("", dataBean.getEpisodeNumber(), imageUrl, "",dataBean.getServers());
                        boolean isWatched = dbHelper.isEpisodeWatched(id, dataBean.getEpisodeNumber());
                        detailBean.setVideoId(id);
                        detailBean.setWatched(isWatched);
                        episodeBeanList.add(detailBean);
                    }
                }

//                Collections.sort(episodeBeanList, new Comparator<AnimePaheBeanList>() {
//                    @Override
//                    public int compare(AnimePaheBeanList v1, AnimePaheBeanList v2) {
//                        return Long.compare(Long.parseLong(v1.getEpisode()), Long.parseLong(v2.getEpisode()));
//                    }
//                });

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
    public void getAniNekoDetail(AniNeKoInfoBean aniNeKoInfoBean) {
        if (aniNeKoInfoBean != null) {
            if (aniNeKoInfoBean.getInfo().getEpisodes().size() != 0) {

                for (AniNeKoInfoBean.InfoBean.EpisodesBean dataBean : aniNeKoInfoBean.getInfo().getEpisodes()) {
                    AnimePaheBeanList detailBean = new AnimePaheBeanList(dataBean.getEpisodeUrl(), dataBean.getEpisodeTitle(), imageUrl, "",new ArrayList<>());
                    boolean isWatched = dbHelper.isEpisodeWatched(id, dataBean.getEpisodeTitle());
                    detailBean.setVideoId(id);
                    detailBean.setWatched(isWatched);
                    episodeBeanList.add(detailBean);
                }
            }

//                Collections.sort(episodeBeanList, new Comparator<AnimePaheBeanList>() {
//                    @Override
//                    public int compare(AnimePaheBeanList v1, AnimePaheBeanList v2) {
//                        return Long.compare(Long.parseLong(v1.getEpisode()), Long.parseLong(v2.getEpisode()));
//                    }
//                });

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

    @Override
    public void getAniNekoEpisode(AniNekoEpisodeBean aniNekoEpisodeBean) {
        if (aniNekoEpisodeBean != null && aniNekoEpisodeBean.getEpisode().getPlayer().getServers() != null) {

            List<AniNekoEpisodeBean.EpisodeBean.PlayerBean.ServersBeanX.ServerGroupsBean.ServersBean> allServers = new ArrayList<>();

            for (AniNekoEpisodeBean.EpisodeBean.PlayerBean.ServersBeanX serversBeanX
                    : aniNekoEpisodeBean.getEpisode().getPlayer().getServers()) {
                if (!serversBeanX.getServerGroups().isEmpty()) {
                    for (AniNekoEpisodeBean.EpisodeBean.PlayerBean.ServersBeanX.ServerGroupsBean group
                            : serversBeanX.getServerGroups()) {
                        allServers.addAll(group.getServers());
                    }
                }
            }

            if (!allServers.isEmpty()) {
                ShowDialog(allServers);
            } else {
                Toast.makeText(this, "No Server Data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private DialogPlus sourceDialog,anikoToSourceDialog;

    private void ShowDialog(List<AniNekoEpisodeBean.EpisodeBean.PlayerBean.ServersBeanX.ServerGroupsBean.ServersBean> stereamBeanList) {
        sourceDialog = DialogPlus.newDialog(AnimePaheWebviewActivity.this)
                .setContentHolder(new ViewHolder(R.layout.dialog_select_quality))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.CENTER)
                .setCancelable(true)
                .setPadding(10, 10, 10, 10)
                .create();

        View dialogView = sourceDialog.getHolderView();
        RecyclerView recyclerView = dialogView.findViewById(R.id.rv_quality);
        QualityAdapter adapter = new QualityAdapter(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.setNewData(stereamBeanList);
        sourceDialog.show();
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
    private AnimePaheBeanList animePaheBeanList;
    @Override
    public void getVideoUrl(List<AniKoToWatchBean.EpisodesBean.ServersBean> serversBeanList,String videoUrl,AnimePaheBeanList animePaheBeanList) {
        this.animePaheBeanList = animePaheBeanList;
//        Log.d("VideoUrl","val: "+videoUrl);
        if (isAniNeko) {
            if(videoUrl.isEmpty()){
                return;
            }
            detailPresenter.getAniNekoEpisodeURL(videoUrl);
        } else {
            if (serversBeanList.isEmpty()) {
                return;
            }
            showAnikotoSource(serversBeanList);
        }

    }

    private void showAnikotoSource(List<AniKoToWatchBean.EpisodesBean.ServersBean> serversBeansList) {
        anikoToSourceDialog = DialogPlus.newDialog(AnimePaheWebviewActivity.this)
                .setContentHolder(new ViewHolder(R.layout.dialog_select_quality))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.CENTER)
                .setCancelable(true)
                .setPadding(10, 10, 10, 10)
                .create();

        View dialogView = anikoToSourceDialog.getHolderView();
        RecyclerView recyclerView = dialogView.findViewById(R.id.rv_quality);
        AniKoToSourceAdapter adapter = new AniKoToSourceAdapter(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.setNewData(serversBeansList);
        anikoToSourceDialog.show();
    }
}