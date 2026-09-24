package com.m.freemovie.Activity;

import android.annotation.SuppressLint;
import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.Intent;
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
import com.kaopiz.kprogresshud.KProgressHUD;
import com.m.freemovie.R;
import com.m.freemovie.Utils.DialogSourceUtils;
import com.m.freemovie.Utils.GlobalWindowUtils;
import com.m.freemovie.Utils.WindowUtils;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.adapter.MovieRuListAdapter;
import com.m.freemovie.adapter.PiNoyMediaListAdapter;
import com.m.freemovie.databinding.ActivityOtherWebview2Binding;
import com.m.freemovie.mvp.Contract.PinoyPediaContract;
import com.m.freemovie.mvp.Contract.PinoyRuMovieAllContract;
import com.m.freemovie.mvp.Model.ClassBean.PinoyMediaDetailBean;
import com.m.freemovie.mvp.Model.ClassBean.PinoyMovieRuBean;
import com.m.freemovie.mvp.Model.ClassBean.PinoyRuBean;
import com.m.freemovie.mvp.Model.ClassBean.PinoyRuDetailBean;
import com.m.freemovie.mvp.Presenter.PinoyPediaPresenter;
import com.m.freemovie.mvp.Presenter.PinoyRuAllPresenter;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OtherWebviewActivity extends AppCompatActivity
        implements View.OnClickListener, PinoyRuMovieAllContract.View, MovieRuListAdapter.MovieIdListener, PinoyPediaContract.View, PiNoyMediaListAdapter.SourceListener {
    private ActivityOtherWebview2Binding binding;
    private int page = 1;
    private boolean isNomore = false;
    private boolean isLoading = false;
    private MovieRuListAdapter movieAdapter;
    private List<PinoyRuBean> movieList = new ArrayList<>();
    private boolean finishing = true;
    private KProgressHUD hud;
    private String videoUrl;
    private int position;
    private RelativeLayout.LayoutParams params, params1;
    private boolean isRotate = false;
    private LoudnessEnhancer booster;
    private final int[] gainValues = {-3000, -2000, -1000, 0, 1000, 2000};
    private final String[] labels = {"0%", "25%", "50%", "100%", "150%", "200%"};
    private int currentLevelIndex = 3;
    private CountDownTimer volumeTimer;
    private AudioManager audioManager;
    private PinoyRuAllPresenter presenter;
    private int type = 1;
    private int perPage = 10;
    private PinoyPediaPresenter pinoyPediaPresenter;
    private List<PinoyRuDetailBean> pinoyRuDetailBeanList = new ArrayList<>();
    private String link;
    private int lastScroll;
    private boolean isPictureMode = false;
    private DialogPlus dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new GlobalWindowUtils(this,false);
        getSupportActionBar().hide();
        binding = ActivityOtherWebview2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        videoUrl = getIntent().getStringExtra("videoUrl");
        position = getIntent().getIntExtra("position", 1);
        type = getIntent().getIntExtra("type", 1);
        Log.d("Type", "val: " + type);
        defaultScreen();
        binding.llReset.setVisibility(View.GONE);
        initRecyclerMovie();
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait");
        hud.show();

        binding.llReset.setOnClickListener(this);
        binding.expand.setOnClickListener(this);
        binding.btnBackFinish.setOnClickListener(this);

//        Log.d("videoUrl: ",videoUrl);
        setupWebView(videoUrl);
        presenter = new PinoyRuAllPresenter(this);
        pinoyPediaPresenter = new PinoyPediaPresenter(this);
        initApi();
        binding.swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                perPage = 10;
                movieList.clear();
                if (movieAdapter != null) {
                    movieAdapter.setNewData(movieList);
                }
                if (binding.llReset.getVisibility() == View.VISIBLE) {
                    binding.llReset.setVisibility(View.GONE);
                }
                initApi();
            }
        });

        binding.llReset.setVisibility(View.GONE);


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
    }

    private void reset() {
        binding.rvMovielist.scrollToPosition(0);
        binding.llReset.setVisibility(View.GONE);
        if(!binding.swipe.isEnabled()){
            binding.swipe.setEnabled(true);
        }
    }


    private void setupWebView(String videoUrl) {
        if (!isNetworkAvailable()) {
            Toast.makeText(getApplicationContext(), "Please check internet and try again", Toast.LENGTH_SHORT).show();
            return;
        }
        binding.webView.setVisibility(View.VISIBLE);
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
        String baseUrl = getBaseUrl(videoUrl);
        String html = "<!DOCTYPE html><html>" +
                "<head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\">" +
                "<style>body,html{margin:0;padding:0;width:100%;height:100%;background-color:#000;overflow:hidden;}" +
                "iframe{border:none;width:100%;height:100%;}</style></head>" +
                "<body><iframe src=\"" + videoUrl + "\" allow=\"autoplay; fullscreen\" allowfullscreen=\"true\"></iframe></body></html>";
        binding.webView.loadDataWithBaseURL(baseUrl, html, "text/html", "UTF-8", null);


    }

    private String getBaseUrl(String url) {
        try {
            URL parsedUrl = new URL(url);
            return parsedUrl.getProtocol() + "://" + parsedUrl.getHost() + "/";
        } catch (MalformedURLException e) {
            return "https://";
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
                binding.swipe.setEnabled(false);
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


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);

        if (isInPictureInPictureMode) {
            binding.rvMovielist.setVisibility(View.GONE);
            binding.expand.setVisibility(View.GONE);
            binding.btnBackFinish.setVisibility(View.GONE);
            binding.tvSelect.setVisibility(View.GONE);
            binding.swipe.setEnabled(false);
            isPictureMode = true;

            RelativeLayout.LayoutParams pipParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            binding.rlWebview.setLayoutParams(pipParams);

        } else {
            isPictureMode = false;
            binding.rvMovielist.setVisibility(View.VISIBLE);
            binding.tvSelect.setVisibility(binding.webView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
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
    public void showLoading() {
        if(!binding.swipe.isEnabled()){
            binding.swipe.setEnabled(true);
        }
        binding.swipe.setRefreshing(true);
    }

    @Override
    public void showError(String error) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Error fetching data: " + error, Toast.LENGTH_SHORT).show();
                binding.swipe.setRefreshing(false);
            }
        }, 500);
    }

    @Override
    public void hideLoading() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.swipe.setRefreshing(false);
            }
        }, 500);
    }

    @Override
    public void getDetailSuccess(PinoyMediaDetailBean bean) {
        if (bean != null && bean.getResults() != null) {
            pinoyRuDetailBeanList.clear();
            List<String> embedUrls = bean.getResults().getEmbedUrls();
            pinoyRuDetailBeanList.add(new PinoyRuDetailBean(embedUrls, link));
            if (pinoyRuDetailBeanList.isEmpty()) {
                return;
            }
            dialog = new DialogSourceUtils().TagalogListSource(OtherWebviewActivity.this, this, bean, pinoyRuDetailBeanList);
            dialog.show();
        }
    }


    @Override
    public void getMovieList(List<PinoyMovieRuBean> bean) {
        if (bean != null) {
            isLoading = false;
            for (PinoyMovieRuBean data : bean) {
                movieList.add(new PinoyRuBean(data.getLink(), data.getTitle().getRendered(), data.getId()));
            }
            if (!movieList.isEmpty()) {
                movieAdapter.setNewData(movieList);
            } else {
                Toast.makeText(getApplicationContext(), "No data", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void getActionList(List<PinoyMovieRuBean> bean) {
        if (bean != null) {
            isLoading = false;
            for (PinoyMovieRuBean data : bean) {
                movieList.add(new PinoyRuBean(data.getLink(), data.getTitle().getRendered(), data.getId()));
            }
            if (!movieList.isEmpty()) {
                movieAdapter.setNewData(movieList);
            } else {
                Toast.makeText(getApplicationContext(), "No data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void getRomanceList(List<PinoyMovieRuBean> bean) {
        if (bean != null) {
            isLoading = false;
            for (PinoyMovieRuBean data : bean) {
                movieList.add(new PinoyRuBean(data.getLink(), data.getTitle().getRendered(), data.getId()));
            }
            if (!movieList.isEmpty()) {
                movieAdapter.setNewData(movieList);
            } else {
                Toast.makeText(getApplicationContext(), "No data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void getComedyList(List<PinoyMovieRuBean> bean) {
        if (bean != null) {
            isLoading = false;
            for (PinoyMovieRuBean data : bean) {
                movieList.add(new PinoyRuBean(data.getLink(), data.getTitle().getRendered(), data.getId()));
            }
            if (!movieList.isEmpty()) {
                movieAdapter.setNewData(movieList);
            } else {
                Toast.makeText(getApplicationContext(), "No data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void getMovieId(String link, String downloadId) {
        pinoyPediaPresenter.getUrl(link);
        this.link = downloadId;
    }

    @Override
    public void getVideoUrl(String url) {
//        Log.d("OtherVideoUrl","val: "+url);
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        clearCache();
        setupWebView(url);
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
                hud.dismiss();
                return;
            }
            if (newProgress == 100) {
                binding.tvSelect.setVisibility(View.GONE);
                if (hud != null && hud.isShowing()) {
                    hud.dismiss();
                    hud = null;
                }
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

    private void initRecyclerMovie() {
        movieAdapter = new MovieRuListAdapter(this);
        binding.rvMovielist.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMovielist.setAdapter(movieAdapter);
        binding.rvMovielist.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && layoutManager != null) {
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                    int totalItemCount = layoutManager.getItemCount();
                    lastScroll = lastVisibleItemPosition;
                    if (lastVisibleItemPosition > 10) {
                        int orientation = getResources().getConfiguration().orientation;
                        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                            binding.llReset.setVisibility(View.VISIBLE);
                        }
                        initGuide();
                    } else if (lastVisibleItemPosition == 0) {
                        binding.llReset.setVisibility(View.GONE);
                    }
                    if (!movieList.isEmpty()) {
                        if (lastVisibleItemPosition >= totalItemCount - 1) {
                            if (isNomore) {
                                return;
                            }
                            isLoading = true;
                            page++;
                            initApi();
                        }
                    }
                }
            }
        });
    }

    private void initApi() {
        switch (type) {
            case 1:
                presenter.getActionPageQuery("26", perPage, page);
                break;
            case 2:
                presenter.getRomanceQuery("52", perPage, page);
                break;
            case 3:
                presenter.getComedyQuery("15", perPage, page);
                break;
            case 4:
                presenter.getPage(page);
                break;
        }
    }


    private void initGuide() {
        NewbieGuide.with(this)
                .setLabel("OtherVideo")
                .setOnGuideChangedListener(new OnGuideChangedListener() {
                    @Override
                    public void onShowed(Controller controller) {

                    }

                    @Override
                    public void onRemoved(Controller controller) {
                    }
                })
                .addGuidePage(GuidePage.newInstance()
                        .addHighLight(binding.llReset, HighLight.Shape.ROUND_RECTANGLE, 1)
                        .setLayoutRes(R.layout.ll_reset_guide)
                )
                .show();
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("MissingPermission")
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back_finish:
                if (finishing) {
                    finish();
                } else {
                    defaultScreen();
                }
                break;
            case R.id.expand:
                if (isRotate) {
                    portraitFull();
                } else {
                    rotateScreen();
                }
                break;
            case R.id.ll_reset:
                reset();
                break;
        }

    }



    private void rotateScreen() {
        finishing = false;
        isRotate = true;
        binding.expand.setVisibility(View.VISIBLE);
        binding.rvMovielist.setVisibility(View.INVISIBLE);
        binding.btnBackFinish.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        binding.rlWebview.setLayoutParams(params);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        binding.swipe.setEnabled(false);
        binding.expand.setImageResource(R.mipmap.rotate_screen);

        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(dip2px(30), dip2px(30));
        params2.addRule(RelativeLayout.ALIGN_PARENT_END);
        params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params2.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        binding.expand.setLayoutParams(params2);
        params2.setMargins(0, 0, 50, 35);
        new WindowUtils(this, true, false);
    }

    private void portraitFull() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding.expand.setVisibility(View.VISIBLE);
        binding.rvMovielist.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        binding.rlWebview.setLayoutParams(params);
        binding.swipe.setEnabled(false);
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(dip2px(30), dip2px(30));
        params2.addRule(RelativeLayout.ALIGN_PARENT_END);
        params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params2.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        params2.setMargins(0, 0, 15, 20);
        binding.expand.setLayoutParams(params2);
        isRotate = false;
        new WindowUtils(this, true, false);
    }

    private void defaultScreen() {
        isRotate = false;
        finishing = true;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding.expand.setVisibility(View.VISIBLE);
        binding.rvMovielist.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, dip2px(250));
        binding.rlWebview.setLayoutParams(params);
        binding.swipe.setEnabled(true);

        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(dip2px(25), dip2px(25));
        params2.addRule(RelativeLayout.ALIGN_PARENT_END);
        params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params2.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        params2.setMargins(0, 0, 10, 10);
        binding.expand.setLayoutParams(params2);
        binding.expand.setImageResource(R.mipmap.expand);
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.llReset.setVisibility(View.GONE);
        } else {
            if (lastScroll > 5) {
                binding.llReset.setVisibility(isPictureMode ? View.GONE : View.VISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!finishing) {
            defaultScreen();
        } else {
            if (binding.swipe != null && binding.swipe.isRefreshing()) {
                binding.swipe.setRefreshing(false);
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
            super.onBackPressed();
            finish();
        }
    }
}