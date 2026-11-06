package com.m.freemovie.Activity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.PictureInPictureParams;
import android.app.RemoteAction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Icon;
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
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
    private String title;
    private int lastScroll;
    private boolean isPictureMode = false;
    private DialogPlus dialog;
    private VideoUtils videoUtils;
    private SubtitleView subtitleView;
    private static final String ACTION_PIP_CONTROL = "com.m.freemovie.PIP_CONTROL";
    private static final String EXTRA_CONTROL_TYPE = "control_type";
    private static final int CONTROL_REWIND = 1;
    private static final int CONTROL_PLAY_PAUSE = 2;
    private static final int CONTROL_FORWARD = 3;
    private static final long SEEK_MS = 10_000;
    private BroadcastReceiver pipReceiver;

    private final List<String> filteredVideoUrls = Collections.synchronizedList(new ArrayList<>());
    private volatile boolean detailLoaded = false;
    private volatile boolean scrapeDone = false;
    private String downloadId;
    private String pendingDownloadLink;
    private PinoyMediaDetailBean lastDetailBean;

    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();

    private static final Pattern DOWNLOAD_PATTERN = Pattern.compile("https://pinoymoviepedia\\.ru/links/([a-zA-Z0-9]+)/");
    private static final Pattern DOWNLOAD_TABLE_PATTERN = Pattern.compile("<a href='https://pinoymoviepedia\\.ru/links/([a-zA-Z0-9]+)/' target='_blank'>Download</a>");
    private static final Pattern EMBED_IFRAME_PATTERN = Pattern.compile(
            "<iframe[^>]+src=[\"'](https?://[^\"']*(?:voe\\.sx|streamtape|dood|filemoon|mixdrop|embedsito|sbembed)[^\"']*)[\"']",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern EMBED_GENERIC_PATTERN = Pattern.compile(
            "<iframe[^>]+src=[\"'](https?://[^\"']+)[\"']",
            Pattern.CASE_INSENSITIVE
    );

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

        videoUtils.setPlaybackStateCallback(isPlaying -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                updatePipActions();
            }
        });
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
            registerPipReceiver();
            updatePipActions();

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
            unregisterPipReceiver();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private RemoteAction createPipAction(int iconRes, String title, int controlType) {
        Intent intent = new Intent(ACTION_PIP_CONTROL)
                .setPackage(getPackageName())
                .putExtra(EXTRA_CONTROL_TYPE, controlType);
        PendingIntent pi = PendingIntent.getBroadcast(
                this, controlType, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        return new RemoteAction(Icon.createWithResource(this, iconRes), title, title, pi);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private PictureInPictureParams buildPipParams() {
        boolean playing = videoUtils != null && videoUtils.isPlayerPlaying();

        List<RemoteAction> actions = new ArrayList<>();
        actions.add(createPipAction(android.R.drawable.ic_media_rew, "Rewind", CONTROL_REWIND));
        actions.add(createPipAction(
                playing ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play,
                playing ? "Pause" : "Play", CONTROL_PLAY_PAUSE));
        actions.add(createPipAction(android.R.drawable.ic_media_ff, "Forward", CONTROL_FORWARD));

        return new PictureInPictureParams.Builder()
                .setAspectRatio(new Rational(16, 9))
                .setActions(actions)
                .build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updatePipActions() {
        if (isInPictureInPictureMode()) {
            try {
                setPictureInPictureParams(buildPipParams());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void registerPipReceiver() {
        if (pipReceiver != null) return;
        pipReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null || !ACTION_PIP_CONTROL.equals(intent.getAction()) || videoUtils == null) return;
                switch (intent.getIntExtra(EXTRA_CONTROL_TYPE, 0)) {
                    case CONTROL_REWIND:
                        videoUtils.seekBackward(SEEK_MS);
                        break;
                    case CONTROL_PLAY_PAUSE:
                        videoUtils.togglePlayPause();
                        break;
                    case CONTROL_FORWARD:
                        videoUtils.seekForward(SEEK_MS);
                        break;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    updatePipActions();
                }
            }
        };
        ContextCompat.registerReceiver(this, pipReceiver,
                new IntentFilter(ACTION_PIP_CONTROL), ContextCompat.RECEIVER_NOT_EXPORTED);
    }

    private void unregisterPipReceiver() {
        if (pipReceiver != null) {
            try {
                unregisterReceiver(pipReceiver);
            } catch (Exception ignored) {
            }
            pipReceiver = null;
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (isPictureMode) {
            if (videoUtils != null) {
                videoUtils.releasePlayer();
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
        try {
            enterPictureInPictureMode(buildPipParams());
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
                detailLoaded = true;
                if (scrapeDone) {
                    rebuildDetailBeanList();
                    showSourceDialogIfReady();
                }
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
        detailLoaded = true;
        lastDetailBean = bean;

        if (bean != null && bean.getData() != null) {
            if (bean.getData().getTitle() != null) {
                title = bean.getData().getTitle();
            }
        }

        if (pendingDownloadLink == null) {
            pendingDownloadLink = !TextUtils.isEmpty(downloadId)
                    ? "https://pinoymoviepedia.ru/links/" + downloadId + "/"
                    : link;
        }

        if (scrapeDone) {
            rebuildDetailBeanList();
            showSourceDialogIfReady();
        }
    }

    private void rebuildDetailBeanList() {
        List<String> merged;
        synchronized (filteredVideoUrls) {
            merged = new ArrayList<>(filteredVideoUrls);
        }
        String dl = pendingDownloadLink != null
                ? pendingDownloadLink
                : (!TextUtils.isEmpty(downloadId)
                ? "https://pinoymoviepedia.ru/links/" + downloadId + "/"
                : link);
        pinoyRuDetailBeanList.clear();
        pinoyRuDetailBeanList.add(new PinoyRuDetailBean(merged, dl));
    }

    private void showSourceDialogIfReady() {
        List<String> urls = pinoyRuDetailBeanList.isEmpty()
                ? null
                : pinoyRuDetailBeanList.get(0).getVideoUrls();

        if (urls == null || urls.isEmpty()) {
            Toast.makeText(getApplicationContext(), "No video source found", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        if (lastDetailBean == null) {
            lastDetailBean = new PinoyMediaDetailBean();
        }

        dialog = new DialogSourceUtils().TagalogListSource(
                OtherWebviewActivity.this, this, lastDetailBean, pinoyRuDetailBeanList);
        dialog.show();
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
        this.link = link;
        this.downloadId = downloadId;
        this.title = null;
        this.lastDetailBean = null;

        synchronized (filteredVideoUrls) {
            filteredVideoUrls.clear();
        }
        detailLoaded = false;
        scrapeDone = false;
        pendingDownloadLink = null;
        pinoyRuDetailBeanList.clear();

        startScrape(link);
        pinoyPediaPresenter.getUrl(link);
    }

    private void startScrape(String pageUrl) {
        if (TextUtils.isEmpty(pageUrl)) {
            scrapeDone = true;
            return;
        }
        new Thread(() -> {
            try {
                FetchResult fr = fetchPageData(pageUrl);
                runOnUiThread(() -> {
                    if (!TextUtils.isEmpty(fr.downloadId)) {
                        downloadId = fr.downloadId;
                    }
                    synchronized (filteredVideoUrls) {
                        for (String u : fr.embedUrls) {
                            if (!filteredVideoUrls.contains(u)) filteredVideoUrls.add(u);
                        }
                    }
                    if (pendingDownloadLink == null) {
                        pendingDownloadLink = !TextUtils.isEmpty(downloadId)
                                ? "https://pinoymoviepedia.ru/links/" + downloadId + "/"
                                : pageUrl;
                    }
                    scrapeDone = true;
                    if (detailLoaded) {
                        rebuildDetailBeanList();
                        showSourceDialogIfReady();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    if (pendingDownloadLink == null) {
                        pendingDownloadLink = pageUrl;
                    }
                    scrapeDone = true;
                    if (detailLoaded) {
                        rebuildDetailBeanList();
                        showSourceDialogIfReady();
                    }
                });
            }
        }).start();
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
            if (lastScroll > 5 && isRotate) {
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

    private static final class FetchResult {
        String downloadId;
        List<String> embedUrls = new ArrayList<>();
    }

    private static FetchResult fetchPageData(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .build();

        FetchResult result = new FetchResult();
        try (Response response = OK_HTTP_CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) return result;

            String html = response.body().string();

            Matcher downloadMatcher = DOWNLOAD_TABLE_PATTERN.matcher(html);
            if (downloadMatcher.find()) {
                result.downloadId = downloadMatcher.group(1);
            } else {
                Matcher linkMatcher = DOWNLOAD_PATTERN.matcher(html);
                if (linkMatcher.find()) {
                    result.downloadId = linkMatcher.group(1);
                }
            }

            Matcher embedMatcher = EMBED_IFRAME_PATTERN.matcher(html);
            while (embedMatcher.find()) {
                String u = embedMatcher.group(1);
                if (!result.embedUrls.contains(u)) {
                    result.embedUrls.add(u);
                }
            }

            if (result.embedUrls.isEmpty()) {
                Matcher generic = EMBED_GENERIC_PATTERN.matcher(html);
                while (generic.find()) {
                    String u = generic.group(1);
                    if (!result.embedUrls.contains(u)) {
                        result.embedUrls.add(u);
                    }
                }
            }
        }
        return result;
    }
}