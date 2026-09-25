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
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import androidx.media3.ui.AspectRatioFrameLayout;
import androidx.media3.ui.SubtitleView;

import com.m.freemovie.R;
import com.m.freemovie.Utils.DbHelper.WatchHistoryDBHelper;
import com.m.freemovie.Utils.GlobalWindowUtils;
import com.m.freemovie.Utils.LinearLayoutManagerWithSmoothScroller;
import com.m.freemovie.Utils.VideoUtils;
import com.m.freemovie.Utils.WindowUtils;
import com.m.freemovie.adapter.EpisodeAdapter;
import com.m.freemovie.databinding.ActivitySeasonListBinding;
import com.m.freemovie.mvp.Model.ClassBean.EpisodeBean;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@UnstableApi
public class SeasonListActivity extends AppCompatActivity implements EpisodeAdapter.SourceListener, View.OnClickListener {
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

    private VideoUtils videoUtils;
    private SubtitleView subtitleView;

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
//        Log.d("SeasonList","ids"+" videoId: "+id + " SeasonId: "+seasonId);
        binding.expand.setOnClickListener(this);
        binding.btnBackFinish.setOnClickListener(this);
        binding.playerView.setOnClickListener(this);


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

        subtitleView = binding.playerView.getSubtitleView();
        videoUtils = new VideoUtils(this, subtitleView, binding.playerView, binding.rlWebview, binding.tvSelect,binding.btnBackFinish);
    }

    private void rotateScreen() {
        finishing = false;
        binding.expand.setVisibility(View.GONE);
        binding.rvSeason.setVisibility(View.GONE);
        binding.btnBackFinish.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        binding.rlWebview.setLayoutParams(params);
        if (binding.playerView != null) {
            binding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        binding.episodeTxt.setVisibility(View.GONE);
        if (videoUtils != null) {
            videoUtils.adjustUi(18, false);
            videoUtils.applySubtitleBottomMargin();
        }
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
        if (videoUtils != null) {
            videoUtils.adjustUi(13, true);
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

    @Override
    public void onBackPressed() {
        if (!finishing) {
            defaultScreen();
        } else {
            super.onBackPressed();
            if (dbHelper != null) {
                dbHelper.close();
            }
            if (videoUtils != null) {
                videoUtils.releasePlayer();
            }
            finish();
        }
    }

    @Override
    public void getId(String id, int position, int seasonNum, int epNumber) {
        if (videoUtils != null) {
            videoUtils.releasePlayer();
        }
        switch (position) {
            case 1:
                videoUrl = "https://vidrock.to/tv/" + id + "/" + seasonNum + "/" + epNumber + "&download=false";
                break;
            case 2:
                videoUrl = "https://moviesapi.to/tv/" + id + "/" + seasonNum + "/" + epNumber;
//                Log.d("VideoUrl","bal: "+videoUrl);
                break;
        }
        binding.tvSelect.setVisibility(View.GONE);
        if (binding.playerView.getVisibility() == View.GONE) {
            binding.playerView.setVisibility(View.VISIBLE);
        }
        if (videoUtils != null) {
            videoUtils.resolveAndPlayStream(videoUrl);
        }
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
            if (subtitleView != null) {
                subtitleView.setFixedTextSize(TypedValue.COMPLEX_UNIT_SP, 5f);
            }
        } else {
            if (subtitleView != null) {
                subtitleView.setFixedTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
            }
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int orientation = getResources().getConfiguration().orientation;

        if (videoUtils != null) {
            videoUtils.adjustUi(18, false);
            videoUtils.applySubtitleBottomMargin();
        }

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.expand:
                if (finishing) {
                    rotateScreen();
                }
                break;
            case R.id.player_view:
                if (videoUtils != null) {
                    videoUtils.UiVisibility(binding.btnBackFinish);
                }
                break;
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
        }
    }
}
