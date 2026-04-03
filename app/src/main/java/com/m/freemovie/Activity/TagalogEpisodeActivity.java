package com.m.freemovie.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.LoudnessEnhancer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.core.Controller;
import com.app.hubert.guide.listener.OnGuideChangedListener;
import com.app.hubert.guide.model.GuidePage;
import com.app.hubert.guide.model.HighLight;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.m.freemovie.R;
import com.m.freemovie.Retrofit.AppConstant;
import com.m.freemovie.Utils.DbHelper.BookmarkDbHelper;
import com.m.freemovie.Utils.DbHelper.PinoyWatchHistoryHelper;
import com.m.freemovie.Utils.LinearLayoutManagerWithSmoothScroller;
import com.m.freemovie.Utils.WindowUtils;
import com.m.freemovie.adapter.TagalogEpisodeAdapter;
import com.m.freemovie.databinding.ActivityTagalogEpisodeBinding;
import com.m.freemovie.mvp.Model.ClassBean.DetailBean;
import com.m.freemovie.mvp.Model.ClassBean.DownloadPlayerListerner;
import com.m.freemovie.mvp.Model.ClassBean.TagalogEpisode;
import com.m.freemovie.mvp.Model.ClassBean.TagalogEpisodeBean;
import com.m.freemovie.mvp.Contract.TagalogEpisodeContract;
import com.m.freemovie.mvp.Presenter.TagalogEpisodePresenter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class TagalogEpisodeActivity extends AppCompatActivity implements TagalogEpisodeContract.View, TagalogEpisodeAdapter.VideoPlayListerner,View.OnClickListener, DownloadPlayerListerner {
    ActivityTagalogEpisodeBinding binding;
    private KProgressHUD hud;
    private TagalogEpisodePresenter tagalogEpisodePresenter;
    private String url,imageUrl;
    private List<TagalogEpisode> tagalogEpisodeList = new ArrayList<>();
    private TagalogEpisodeAdapter tagalogEpisodeAdapter;
    private Handler mSeekHandler = new Handler(Looper.getMainLooper());
    private Runnable mSeekRunnable;
    private int mDuration = 0;
    private boolean isSeekBarTracking = false;
    private Animation animRotate;
    private boolean isContinue = false;
    private boolean ispause = false;
    private String title;
    boolean isLandScape = false;
    boolean isFinish = false;
    private PinoyWatchHistoryHelper dbHelper;
    private int currentPosition = 0;
    private KProgressHUD downloadHud;
    private boolean isFirstTask = false;
    private BookmarkDbHelper bookmarkDbHelper;
    private String lastVideoUrl = "";
    private RelativeLayout.LayoutParams params;
    private LoudnessEnhancer booster;
    private final int[] gainValues = {-3000, -2000, -1000, 0, 1000, 2000};
    private final String[] labels = {"0%", "25%", "50%", "100%", "150%", "200%"};
    private int currentLevelIndex = 3;
    private CountDownTimer volumeTimer;
    private AudioManager audioManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        binding = ActivityTagalogEpisodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        url = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");
        imageUrl = getIntent().getStringExtra("imageUrl");
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait");
        tagalogEpisodePresenter = new TagalogEpisodePresenter(this);

        if(isNetworkAvailable()){
            tagalogEpisodePresenter.getUrl(url);
        }else{
            Toast.makeText(getApplicationContext(),"Please check internet and try again",Toast.LENGTH_SHORT).show();
        }


        binding.rvEpisode.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(this));
        tagalogEpisodeAdapter = new TagalogEpisodeAdapter(this,this);
        binding.rvEpisode.setAdapter(tagalogEpisodeAdapter);
        dbHelper = new PinoyWatchHistoryHelper(this);
        bookmarkDbHelper = new BookmarkDbHelper(this);
        List<View> viewList = new ArrayList<>();
        viewList.add(binding.fullWide);
        viewList.add(binding.player);
        viewList.add(binding.tenNegative);
        viewList.add(binding.tenPositive);
        viewList.add(binding.btnPlay);
        viewList.add(binding.btnBack);
        viewList.add(binding.llBookmark);
        for(View v : viewList){
            v.setOnClickListener(this);
        }

        initGuide();
        setImageData(url);

        binding.player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mDuration = mediaPlayer.getDuration();
                binding.seekBar.setMax(mDuration);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mediaPlayer.start();
                            binding.btnPlay.setVisibility(View.INVISIBLE);
                            binding.time.setVisibility(View.INVISIBLE);
                            binding.seekBar.setVisibility(View.INVISIBLE);
                            binding.fullWide.setVisibility(View.INVISIBLE);
                            binding.tenPositive.setVisibility(View.INVISIBLE);
                            binding.tenNegative.setVisibility(View.INVISIBLE);
                            setupSeekBar();
                            startSeekUpdates();
                            if(binding.player.isPlaying()){
                                hud.dismiss();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 1000);

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        binding.btnRefresh.setVisibility(View.VISIBLE);
                        binding.btnPlay.setVisibility(View.INVISIBLE);
                        binding.tenNegative.setVisibility(View.INVISIBLE);
                        binding.tenPositive.setVisibility(View.INVISIBLE);
                        binding.time.setVisibility(View.INVISIBLE);
                        binding.seekBar.setVisibility(View.INVISIBLE);
                        binding.fullWide.setVisibility(View.INVISIBLE);
                        isFinish = true;
                        binding.player.pause();
                        binding.btnRefresh.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                binding.btnRefresh.setVisibility(View.GONE);
                                isFinish = false;
                                mp.seekTo(0);
                                mp.start();
                            }
                        });
                    }
                });

                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        Toast.makeText(getApplicationContext(),"Can't play video restarting",Toast.LENGTH_SHORT).show();
                        if(!lastVideoUrl.isEmpty()){
                            binding.player.setVideoPath(String.valueOf(Uri.parse(lastVideoUrl)));
                        }
                        return true;
                    }
                });
                mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {
                        double ratio = percent / 100.0;
                        int bufferingLevel = (int) (mediaPlayer.getDuration() * ratio);
                        binding.seekBar.setSecondaryProgress(bufferingLevel);
                    }
                });
            }
        });

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


        initTopPadding(70);

        binding.llVolume.setEnabled(false);
        try {
            booster = new LoudnessEnhancer(0);
            booster.setEnabled(true);
            booster.setTargetGain(-1000);
            audioManager = (android.media.AudioManager) getSystemService(Context.AUDIO_SERVICE);
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
            public void onTick(long millisUntilFinished) {}
            public void onFinish() {
                binding.llVolume.setVisibility(View.GONE);
            }
        }.start();
    }


    private void initTopPadding(int topPadding) {
        ViewCompat.setOnApplyWindowInsetsListener(binding.llRoot, (v, windowInsets) -> {
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.topMargin = topPadding;
            v.setLayoutParams(mlp);
            return WindowInsetsCompat.CONSUMED;
        });
    }
    private void initGuide() {
        NewbieGuide.with(TagalogEpisodeActivity.this)
                .setLabel("tagalog_bookmark")
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


        @SuppressLint("ClickableViewAccessibility")
    private void setupSeekBar() {
        binding.player.setOnTouchListener(null);

        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && binding.player != null) {
                    binding.time.setText(formatTime(progress / 1000) + " / " + formatTime(mDuration / 1000));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekBarTracking = true;
                if (binding.player.isPlaying()) {
                    binding.player.pause();
                }
                mSeekHandler.removeCallbacks(mSeekRunnable);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeekBarTracking = false;
                if (binding.player != null) {
                    binding.player.seekTo(seekBar.getProgress());
                    binding.player.start();
                    binding.btnPlay.setVisibility(View.INVISIBLE);
                    seekBar.setVisibility(View.INVISIBLE);
                    binding.time.setVisibility(View.INVISIBLE);
                    binding.tenPositive.setVisibility(View.INVISIBLE);
                    binding.tenNegative.setVisibility(View.INVISIBLE);
                    binding.fullWide.setVisibility(isLandScape?View.GONE:View.VISIBLE);
                    startSeekUpdates();
                }
            }
        });
    }

    private void startSeekUpdates() {
        if (mSeekRunnable != null) {
            mSeekHandler.removeCallbacks(mSeekRunnable);
        }

        mSeekRunnable = new Runnable() {
            @Override
            public void run() {
                if (binding.player != null && binding.player.isPlaying() && !isSeekBarTracking) {
                    int currentPosition = binding.player.getCurrentPosition();
                    binding.seekBar.setProgress(currentPosition);
                    binding.time.setText(formatTime(currentPosition / 1000) + " / " + formatTime(mDuration / 1000));
                }
                mSeekHandler.postDelayed(this, 1000);
            }
        };
        mSeekHandler.post(mSeekRunnable);
    }

    private String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, secs);
        } else {
            return String.format("%02d:%02d", minutes, secs);
        }
    }


    @Override
    public void showLoading() {
        hud.show();
    }

    @Override
    public void showError(String error) {
        Log.d("ErrorData", "val: " + error);
        Toast.makeText(getApplicationContext(),"Series not found",Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void hideLoading() {
        if (hud != null && hud.isShowing()) {
            hud.dismiss();
        }
    }

    @Override
    public void getTagalogEpisode(TagalogEpisodeBean tagalogEpisodeBean) {
        if(tagalogEpisodeBean !=null && tagalogEpisodeBean.getResults() !=null){
            Set<String> seenEpisodes = new HashSet<>();
            String lastWatchedEpisodeNumber = null;
            int lastWatchedPosition = -1;
            for(TagalogEpisodeBean.ResultsBean data : tagalogEpisodeBean.getResults()){
                if(!data.getEpisodes().isEmpty()){
                    for(TagalogEpisodeBean.ResultsBean.EpisodesBean dataEpisode : data.getEpisodes()){
                        TagalogEpisode tagalogEpisode = new TagalogEpisode(dataEpisode.getEpisode(),imageUrl,dataEpisode.getVideoUrl());
                        String episode = dataEpisode.getEpisode();
                        if (!seenEpisodes.contains(episode)) {
                            seenEpisodes.add(episode);
                            boolean isWatched = dbHelper.isEpisodeWatched(url,episode);
                            tagalogEpisode.setWatched(isWatched);

                            tagalogEpisodeList.add(tagalogEpisode);
                            tagalogEpisode.setVideoId(url);

                            binding.episodeTxt.setText(tagalogEpisodeList.size() > 1? "Episode's" : "Episode");
                            if(isWatched){
                                lastWatchedPosition = tagalogEpisodeList.size() - 1;
                                lastWatchedEpisodeNumber = String.valueOf(episode);
                            }
                        }
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Episodes not found",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            tagalogEpisodeAdapter.setNewData(tagalogEpisodeList);

            if(lastWatchedPosition != -1 && lastWatchedEpisodeNumber != null){
                binding.rvEpisode.smoothScrollToPosition(lastWatchedPosition);
                Toast.makeText(getApplicationContext(),
                        "Last Episode watched: Episode " + lastWatchedEpisodeNumber,
                        Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void getVideoUrl(String videoUrl) {
//        Log.d("VideoUrl","val: "+videoUrl);
        if(videoUrl.isEmpty() || videoUrl == null){
            return;
        }
        lastVideoUrl = videoUrl;
        binding.player.setVideoPath(String.valueOf(Uri.parse(videoUrl)));
        isFinish = false;
        binding.btnRefresh.setVisibility(View.GONE);
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
        switch (view.getId()){
            case R.id.ll_bookmark:
                savedBook();
                break;
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.btn_play:
                play_pause();
                break;
            case R.id.player:
                if(isFinish){
                    return;
                }
                clickPause();
                break;
            case R.id.full_wide:
                isLandScape = true;
                binding.fullWide.setVisibility(View.GONE);
                new WindowUtils(this, true,true);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                binding.rvEpisode.setVisibility(View.GONE);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                initTopPadding(10);
                binding.relativeVideo.setLayoutParams(params);
                binding.llBookmark.setVisibility(View.GONE);
                break;
            case R.id.ten_negative:
                if (binding.player == null || mDuration == 0) return;
                animRotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_image_negative_360);
                binding.tenNegative.startAnimation(animRotate);
                int negativePosition = binding.player.getCurrentPosition();
                int negative_newPosition = negativePosition - 10000;
                if (negative_newPosition < 0) negative_newPosition = 0;

                binding.player.seekTo(negative_newPosition);
                binding.seekBar.setProgress(negative_newPosition);
                startSeekUpdates();
                break;
            case R.id.ten_positive:
                if (binding.player== null || mDuration == 0) return;
                animRotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_image_positive_360);
                binding.tenPositive.startAnimation(animRotate);

                int positivePosition = binding.player.getCurrentPosition();
                int positive_newPosition = positivePosition + 10000;
                if (positive_newPosition > mDuration) positive_newPosition = mDuration;
                binding.player.seekTo(positive_newPosition);
                binding.seekBar.setProgress(positive_newPosition);
                break;
        }

    }
    private void savedBook() {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        DetailBean details = new DetailBean(url, timestamp, imageUrl, title,"false");
        details.setVideoId(url);
        details.setTimeStamp(timestamp);
        bookmarkDbHelper.toggleBookmark(details, 3);
        setImageData(url);
    }
    private void setImageData(String videoId) {
        boolean isBookmarked = bookmarkDbHelper.isBookmarked(videoId);
        binding.ivHeart.setImageResource(!isBookmarked? R.mipmap.heart_no :R.mipmap.heart_yes);
    }


    private void play_pause() {
        if(binding.player.isPlaying()){
            binding.player.pause();
            binding.btnPlay.setImageResource(R.mipmap.play_white);
            isContinue = true;
        }else{
            binding.btnPlay.setImageResource(R.mipmap.pause_white);
            isContinue = false;
            binding.player.start();
        }
    }


    private void clickPause() {
        ispause = !ispause;
        if (ispause) {
            binding.time.setVisibility(View.VISIBLE);
            binding.seekBar.setVisibility(View.VISIBLE);
            binding.tenPositive.setVisibility(View.VISIBLE);
            binding.tenNegative.setVisibility(View.VISIBLE);
            binding.fullWide.setVisibility(View.VISIBLE);
            binding.btnPlay.setVisibility(View.VISIBLE);
            binding.fullWide.setVisibility(isLandScape?View.GONE:View.VISIBLE);
            binding.btnPlay.setImageResource(isContinue? R.mipmap.play_white:R.mipmap.pause_white);

        } else {
            binding.time.setVisibility(View.INVISIBLE);
            binding.seekBar.setVisibility(View.INVISIBLE);
            binding.btnPlay.setVisibility(View.INVISIBLE);
            binding.tenPositive.setVisibility(View.INVISIBLE);
            binding.tenNegative.setVisibility(View.INVISIBLE);
            binding.fullWide.setVisibility(View.INVISIBLE);
            ispause = false;
            startSeekUpdates();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        new WindowUtils(this,false,false);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(binding.btnRefresh !=null){
            binding.btnRefresh.setVisibility(View.GONE);
        }
        isFirstTask = false;
        if (binding.player != null && binding.player.isPlaying()) {
            currentPosition = binding.player.getCurrentPosition();
            binding.player.pause();
        }
        if (mSeekRunnable != null) {
            mSeekHandler.removeCallbacks(mSeekRunnable);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        isFirstTask = false;
        if(binding.btnRefresh !=null){
            binding.btnRefresh.setVisibility(View.GONE);
        }
        if (binding.player != null) {
            if (currentPosition > 0) {
                binding.player.seekTo(currentPosition);
            }
            if (!binding.player.isPlaying() && currentPosition > 0) {
                binding.player.start();
                startSeekUpdates();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            new WindowUtils(this,false,false);
            isLandScape = false;
            initTopPadding(70);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dip2px(250));
            binding.relativeVideo.setLayoutParams(params);
            binding.fullWide.setVisibility(isFinish?View.GONE:View.VISIBLE);
            binding.rvEpisode.setVisibility(View.VISIBLE);
            binding.llBookmark.setVisibility(View.VISIBLE);
            isFirstTask = false;
        }else{
            isFirstTask = false;
            super.onBackPressed();
            finish();
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
    public void getDownloadData(String videoUrl, String episode) {
        if(videoUrl == null || episode == null){
            return;
        }
        if(binding.player!=null){
            if(binding.player.isPlaying()){
                binding.player.pause();
            }
        }
        isContinue = true;
        binding.btnPlay.setImageResource(isContinue? R.mipmap.play_white:R.mipmap.pause_white);
        downloadVideo(videoUrl,episode);
    }

    private File getLocalFile(String episode) {
        String safeTitle = title.replaceAll("[^a-zA-Z0-9.-]", "_");
        String fileName = safeTitle +"_Episode_"+episode+"_"+".mp4";
        String dirName = safeTitle;

        File freeMovieDir = new File(getFilesDir(), "FreeMovie");
        if (!freeMovieDir.exists()) {
            freeMovieDir.mkdirs();
        }

        File movieDir = new File(freeMovieDir, dirName);
        if (!movieDir.exists()) {
            movieDir.mkdirs();
        }
        return new File(movieDir, fileName);
    }

    private void downloadVideo(String videoUrl, String episode) {
        isFirstTask = true;
        File outputFile = getLocalFile(episode);

        downloadHud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                .setLabel("Downloading: " + title + " Ep: " + episode)
                .setMaxProgress(100)
                .setCancellable(true);
        downloadHud.show();

        downloadHud.setCancellable(dialog -> {
            isFirstTask = false;
            if (outputFile.exists()) {
                outputFile.delete();
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

                okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder()
                        .url(videoUrl)
                        .get();

                if (existingLength > 0) {
                    requestBuilder.addHeader("Range", "bytes=" + existingLength + "-");
                }

                response = client.newCall(requestBuilder.build()).execute();
                int responseCode = response.code();
                boolean isResume = (responseCode == 206);

                if (response.isSuccessful() || isResume) {
                    inputStream = response.body().byteStream();
                    outputStream = new FileOutputStream(outputFile, isResume);

                    long contentLength = response.body().contentLength();
                    if (isResume) {
                        contentLength += existingLength;
                    }

                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    long totalBytesRead = existingLength;

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        if (!isFirstTask) break;

                        outputStream.write(buffer, 0, bytesRead);
                        totalBytesRead += bytesRead;

                        if (contentLength > 0) {
                            final int progress = (int) ((totalBytesRead * 100) / contentLength);
                            runOnUiThread(() -> downloadHud.setProgress(progress));
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
                        });
                    }
                } else {
                    throw new IOException("Server returned code: " + responseCode);
                }

            } catch (Exception e) {
                if (isFirstTask) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        if (downloadHud != null && downloadHud.isShowing()) {
                            downloadHud.dismiss();
                        }
                        isFirstTask = false;
                        Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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


}