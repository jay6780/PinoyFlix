package com.m.freemovie.Activity;

import android.annotation.SuppressLint;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Rational;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.ui.PlayerView;
import androidx.media3.ui.SubtitleView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.core.Controller;
import com.app.hubert.guide.listener.OnGuideChangedListener;
import com.app.hubert.guide.model.GuidePage;
import com.app.hubert.guide.model.HighLight;
import com.m.freemovie.R;
import com.m.freemovie.Utils.DialogSourceUtils;
import com.m.freemovie.Utils.GlobalWindowUtils;
import com.m.freemovie.Utils.VideoUtils;
import com.m.freemovie.Utils.WindowUtils;
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@UnstableApi
public class OtherWebviewActivity extends AppCompatActivity
        implements View.OnClickListener, PinoyRuMovieAllContract.View, MovieRuListAdapter.MovieIdListener, PinoyPediaContract.View, PiNoyMediaListAdapter.SourceListener {
    private ActivityOtherWebview2Binding binding;
    private int page = 1;
    private boolean isNomore = false;
    private boolean isLoading = false;
    private MovieRuListAdapter movieAdapter;
    private List<PinoyRuBean> movieList = new ArrayList<>();
    private boolean finishing = true;
    private String videoUrl;
    private int position;
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
    private VideoUtils videoUtils;
    private SubtitleView subtitleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtherWebview2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.BLACK));
        getSupportActionBar().hide();
        new GlobalWindowUtils(this, false);
        defaultScreen();
        videoUrl = getIntent().getStringExtra("videoUrl");
        position = getIntent().getIntExtra("position", 1);
        type = getIntent().getIntExtra("type", 1);
//        Log.d("Type", "val: " + type);
//        Log.d("PlayerDomain", "val: " + player);

        binding.llReset.setVisibility(View.GONE);
        initRecyclerMovie();
        binding.llReset.setOnClickListener(this);
        binding.expand.setOnClickListener(this);
        binding.btnBackFinish.setOnClickListener(this);
        binding.playerView.setOnClickListener(this);

        presenter = new PinoyRuAllPresenter(this);
        pinoyPediaPresenter = new PinoyPediaPresenter(this);
        initApi();
        subtitleView = binding.playerView.getSubtitleView();
        videoUtils = new VideoUtils(this, subtitleView, binding.playerView, binding.rlWebview, binding.tvSelect, binding.btnBackFinish);
        initStart();

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
        subtitleView();
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


    private void subtitleView() {
        subtitleView = binding.playerView.getSubtitleView();
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

    private void reset() {
        binding.rvMovielist.scrollToPosition(0);
        binding.llReset.setVisibility(View.GONE);
        if (!binding.swipe.isEnabled()) {
            binding.swipe.setEnabled(true);
        }
    }


    private void rotateScreen() {
        finishing = false;
        isRotate = true;
        binding.expand.setVisibility(View.VISIBLE);
        binding.rvMovielist.setVisibility(View.GONE);
        binding.episodeTxt.setVisibility(View.GONE);
        binding.llReset.setVisibility(View.GONE);
        binding.btnBackFinish.setVisibility(View.VISIBLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        binding.swipe.setEnabled(false);
        binding.expand.setImageResource(R.mipmap.rotate_screen);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        binding.rlWebview.setLayoutParams(params);

        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(dip2px(30), dip2px(30));
        params2.addRule(RelativeLayout.ALIGN_PARENT_END);
        params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params2.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        binding.expand.setLayoutParams(params2);
        params2.setMargins(0, 0, 50, 35);

        if (videoUtils != null) {
            videoUtils.adjustUi(18, false);
            videoUtils.applySubtitleBottomMargin();
        }

        new WindowUtils(this, true, false);
    }

    private void portraitFull() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        finishing = false;
        isRotate = false;

        binding.expand.setVisibility(View.VISIBLE);
        binding.rvMovielist.setVisibility(View.GONE);
        binding.episodeTxt.setVisibility(View.GONE);
        binding.llReset.setVisibility(View.GONE);
        binding.btnBackFinish.setVisibility(View.VISIBLE);
        binding.swipe.setEnabled(false);

        RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        binding.rlWebview.setLayoutParams(rlParams);


        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(dip2px(30), dip2px(30));
        params2.addRule(RelativeLayout.ALIGN_PARENT_END);
        params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params2.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        params2.setMargins(0, 0, 15, 20);
        binding.expand.setLayoutParams(params2);
        binding.expand.setImageResource(R.mipmap.rotate_screen);

        if (videoUtils != null) {
            videoUtils.adjustUi(15, false);
            videoUtils.applySubtitleBottomMargin();
        }

        new WindowUtils(this, true, false);
    }

    private void defaultScreen() {
        isRotate = false;
        finishing = true;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding.expand.setVisibility(View.VISIBLE);
        binding.rvMovielist.setVisibility(View.VISIBLE);
        binding.episodeTxt.setVisibility(View.VISIBLE);
        binding.swipe.setEnabled(true);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                dip2px(250)
        );
        binding.rlWebview.setLayoutParams(params);

        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(dip2px(25), dip2px(25));
        params2.addRule(RelativeLayout.ALIGN_PARENT_END);
        params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params2.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        params2.setMargins(0, 0, 10, 10);
        binding.expand.setLayoutParams(params2);
        binding.expand.setImageResource(R.mipmap.expand);
        if (videoUtils != null) {
            videoUtils.adjustUi(13, false);
            videoUtils.applySubtitleBottomMargin();
        }

        new WindowUtils(this, true, false);
    }


    public int dip2px(float dpValue) {
        final float scale = getResources(this).getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static Resources getResources(Context context) {
        return context.getResources();
    }

    private void initStart() {
        if (!isNetworkAvailable()) {
            binding.playerView.setVisibility(View.GONE);
            binding.tvSelect.setVisibility(View.VISIBLE);
        } else {
            if (videoUrl != null && !videoUrl.isEmpty()) {
                binding.playerView.setVisibility(View.VISIBLE);
                binding.tvSelect.setVisibility(View.GONE);
                if (videoUtils != null) {
                    videoUtils.resolveAndPlayStream(videoUrl);
                }
            }
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
            if (videoUtils != null) {
                videoUtils.adjustUi(5, false);
            }
            RelativeLayout.LayoutParams pipParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            binding.rlWebview.setLayoutParams(pipParams);

        } else {
            if (videoUtils != null) {
                videoUtils.adjustUi(13, false);
            }
            binding.rvMovielist.setVisibility(View.VISIBLE);
            binding.tvSelect.setVisibility(binding.playerView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            binding.swipe.setEnabled(finishing);
            binding.btnBackFinish.setVisibility(View.VISIBLE);
            isPictureMode = false;
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

    @SuppressWarnings("deprecation")
    @SuppressLint("MissingPermission")
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private String getBaseUrl(String url) {
        try {
            URL parsedUrl = new URL(url);
            return parsedUrl.getProtocol() + "://" + parsedUrl.getHost() + "/";
        } catch (MalformedURLException e) {
            return "https://";
        }
    }

    @Override
    public void showLoading() {
        if (!binding.swipe.isEnabled()) {
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
            List<String> filteredVideoUrls = new ArrayList<>();
            for (String videoUrl : embedUrls) {
                if (videoUrl.contains("voe.sx")) {
                    filteredVideoUrls.add(videoUrl);
                }
            }

            if (filteredVideoUrls.isEmpty()) {
                Toast.makeText(getApplicationContext(), "No video source found", Toast.LENGTH_SHORT).show();
                return;
            }

            pinoyRuDetailBeanList.add(new PinoyRuDetailBean(filteredVideoUrls, link));
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
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (videoUtils != null) {
            videoUtils.releasePlayer();
        }
        this.videoUrl = url;
        if (videoUtils != null) {
            binding.tvSelect.setVisibility(View.GONE);
            binding.playerView.setVisibility(View.VISIBLE);
            videoUtils.resolveAndPlayStream(url);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back_finish:
                if (finishing) {
                    if (videoUtils != null) {
                        videoUtils.releasePlayer();
                    }
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
            case R.id.player_view:
                binding.playerView.setControllerVisibilityListener(new PlayerView.ControllerVisibilityListener() {
                    @Override
                    public void onVisibilityChanged(int visibility) {
                        binding.btnBackFinish.setVisibility(visibility);
                        binding.expand.setVisibility(visibility);
                    }
                });
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int orientation = getResources().getConfiguration().orientation;
        if (videoUtils != null) {
            videoUtils.applySubtitleBottomMargin();
        }
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.llReset.setVisibility(View.GONE);
            if (finishing && !isPictureMode) {
                rotateScreen();
            }
        } else {
            if (isRotate && !finishing && !isPictureMode) {
                defaultScreen();
            }
            if (lastScroll > 5) {
                binding.llReset.setVisibility(isPictureMode ? View.GONE : View.VISIBLE);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isInPictureInPictureMode()) {
            return;
        }
        if (videoUtils != null) {
            videoUtils.pausePlayer();
            videoUtils.onPause();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (videoUtils != null) {
            videoUtils.playResume();
        }
    }

    @Override
    protected void onDestroy() {
        if (videoUtils != null) {
            videoUtils.DestroyPlayer();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!finishing) {
            defaultScreen();
        } else {
            super.onBackPressed();
            finish();
            if (binding.swipe != null && binding.swipe.isRefreshing()) {
                binding.swipe.setRefreshing(false);
            }
            if (videoUtils != null) {
                videoUtils.releasePlayer();
            }

        }
    }
}