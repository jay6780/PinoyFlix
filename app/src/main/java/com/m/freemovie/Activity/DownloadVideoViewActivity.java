package com.m.freemovie.Activity;

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
import android.graphics.Point;
import android.graphics.drawable.Icon;
import android.media.AudioManager;
import android.media.audiofx.LoudnessEnhancer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Rational;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.ui.AspectRatioFrameLayout;
import androidx.media3.ui.SubtitleView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.m.freemovie.R;
import com.m.freemovie.Utils.GlobalWindowUtils;
import com.m.freemovie.Utils.VideoUtils;
import com.m.freemovie.Utils.WindowUtils;
import com.m.freemovie.adapter.VideoDownloadAdapter;
import com.m.freemovie.databinding.ActivityDownloadVideoViewBinding;
import com.m.freemovie.fileUtils.FilesExtractor;
import com.m.freemovie.fileUtils.VideoFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@UnstableApi
public class DownloadVideoViewActivity extends AppCompatActivity implements View.OnClickListener, VideoDownloadAdapter.PlayPathListener {
    ActivityDownloadVideoViewBinding binding;
    private String title, videopath;
    private VideoDownloadAdapter videoDownloadAdapter;
    private List<VideoFile> videoFileList = new ArrayList<>();
    private LoudnessEnhancer booster;
    private final int[] gainValues = {-3000, -2000, -1000, 0, 1000, 2000};
    private final String[] labels = {"0%", "25%", "50%", "100%", "150%", "200%"};
    private int currentLevelIndex = 3;
    private CountDownTimer volumeTimer;
    private AudioManager audioManager;
    private VideoUtils videoUtils;
    private SubtitleView subtitleView;
    private static final String ACTION_PIP_CONTROL = "com.m.freemovie.PIP_CONTROL";
    private static final String EXTRA_CONTROL_TYPE = "control_type";
    private static final int CONTROL_REWIND = 1;
    private static final int CONTROL_PLAY_PAUSE = 2;
    private static final int CONTROL_FORWARD = 3;
    private static final long SEEK_MS = 10_000;
    private BroadcastReceiver pipReceiver;
    private boolean finishing = true;
    private boolean isPictureMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityDownloadVideoViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        title = getIntent().getStringExtra("title");
        videopath = getIntent().getStringExtra("videopath");
        new GlobalWindowUtils(this, false);
        new WindowUtils(this, true, false);
        setupFileList();
        binding.llVolume.setEnabled(false);
        try {
            booster = new LoudnessEnhancer(0);
            booster.setEnabled(true);
            booster.setTargetGain(-1000);
            audioManager = (android.media.AudioManager) getSystemService(Context.AUDIO_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        subtitleView = binding.playerView.getSubtitleView();
        videoUtils = new VideoUtils(this, subtitleView, binding.playerView, binding.rlLocalVideo, binding.tvSelect, binding.btnBackFinish);
        startPlayVideo(videopath);
        videoUtils.setPlaybackStateCallback(isPlaying -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                updatePipActions();
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

        binding.expand.setOnClickListener(this);
        binding.btnBackFinish.setOnClickListener(this);
        binding.playerView.setOnClickListener(this);
    }

    private void startPlayVideo(String videoPath) {
        binding.tvSelect.setVisibility(View.GONE);
        if(videoUtils !=null){
            videoUtils.playLocalVideo(videoPath);
        }
    }


    private void setupFileList() {
        FilesExtractor filesExtractor = new FilesExtractor(DownloadVideoViewActivity.this);
        ArrayList<VideoFile> videoFiles = filesExtractor.listVideos();
        Collections.sort(videoFiles, new Comparator<VideoFile>() {
            @Override
            public int compare(VideoFile v1, VideoFile v2) {
                return Long.compare(v2.getLastModified(), v1.getLastModified());
            }
        });
        if (videoFiles.size() == 1) {
            videoFileList.addAll(videoFiles);
        } else {
            for (VideoFile data : videoFiles) {
                String name = data.getName();
//            Log.e("Listnames","val: "+name);
                if (!name.equals(title)) {
                    videoFileList.add(data);
                }
            }
        }

        binding.downloadTxt.setText(videoFiles.size() > 2 ? "Other video's" : "Download video");
        videoDownloadAdapter = new VideoDownloadAdapter(this, videoFileList, this);
        binding.rvDownloadvideo.setLayoutManager(new LinearLayoutManager(this));
        binding.rvDownloadvideo.setAdapter(videoDownloadAdapter);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.expand:
                if (finishing) {
                    rotateScreen();
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
            case R.id.player_view:
                if (videoUtils != null) {
                    videoUtils.UiVisibility(binding.btnBackFinish);
                }
                break;
        }

    }

    private void rotateScreen() {
        finishing = false;
        binding.expand.setVisibility(View.GONE);
        binding.rvDownloadvideo.setVisibility(View.GONE);
        binding.btnBackFinish.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        binding.rlLocalVideo.setLayoutParams(params);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

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
        binding.rvDownloadvideo.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, dip2px(250));
        binding.rlLocalVideo.setLayoutParams(params);
        binding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        if (videoUtils != null) {
            videoUtils.adjustUi(13, true);
            videoUtils.applySubtitleBottomMargin();
        }
        new WindowUtils(this, true, false);
    }


    @Override
    public void onBackPressed() {
        if (!finishing) {
            defaultScreen();
        } else {
            super.onBackPressed();
            finish();
            if (videoUtils != null) {
                videoUtils.releasePlayer();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);

        if (isInPictureInPictureMode) {
            binding.rvDownloadvideo.setVisibility(View.GONE);
            binding.expand.setVisibility(View.GONE);
            binding.btnBackFinish.setVisibility(View.GONE);
            binding.tvSelect.setVisibility(View.GONE);
            isPictureMode = true;
            if (videoUtils != null) {
                videoUtils.adjustUi(5, false);
            }
            RelativeLayout.LayoutParams pipParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            binding.rlLocalVideo.setLayoutParams(pipParams);
            registerPipReceiver();
            updatePipActions();

        } else {
            if (videoUtils != null) {
                videoUtils.adjustUi(13, true);
            }
            binding.rvDownloadvideo.setVisibility(View.VISIBLE);
            binding.tvSelect.setVisibility(binding.playerView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
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
                if (intent == null || !ACTION_PIP_CONTROL.equals(intent.getAction()) || videoUtils == null)
                    return;
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


    public int dip2px(float dpValue) {
        final float scale = getResources(this).getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static Resources getResources(Context context) {
        return context.getResources();
    }

    @Override
    public void getPath(String path, String title) {
        if (path.isEmpty()) {
            return;
        }
        startPlayVideo(path);
    }

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

}