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
import android.media.MediaPlayer;
import android.media.audiofx.LoudnessEnhancer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Rational;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.m.freemovie.R;
import com.m.freemovie.Utils.GlobalWindowUtils;
import com.m.freemovie.Utils.WindowUtils;
import com.m.freemovie.adapter.VideoDownloadAdapter;
import com.m.freemovie.databinding.ActivityDownloadVideoViewBinding;
import com.m.freemovie.fileUtils.FilesExtractor;
import com.m.freemovie.fileUtils.VideoFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DownloadVideoViewActivity extends AppCompatActivity implements View.OnClickListener, VideoDownloadAdapter.PlayPathListener {
    ActivityDownloadVideoViewBinding binding;
    private Handler mSeekHandler = new Handler(Looper.getMainLooper());
    private Runnable mSeekRunnable;
    private int mDuration = 0;
    private boolean isSeekBarTracking = false;
    private Animation animRotate;
    private boolean isContinue = false;
    private boolean ispause = false;
    boolean isLandScape = false;
    boolean isFinish = false;
    private String title,videopath;
    private int currentPosition = 0;
    private VideoDownloadAdapter videoDownloadAdapter;
    private List<VideoFile> videoFileList = new ArrayList<>();
    private LoudnessEnhancer booster;
    private final int[] gainValues = {-3000, -2000, -1000, 0, 1000, 2000};
    private final String[] labels = {"0%", "25%", "50%", "100%", "150%", "200%"};
    private int currentLevelIndex = 3;
    private CountDownTimer volumeTimer;
    private AudioManager audioManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityDownloadVideoViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final int restoredPosition = savedInstanceState != null
                ? savedInstanceState.getInt("saved_position", 0)
                : 0;

        title = getIntent().getStringExtra("title");
        videopath = getIntent().getStringExtra("videopath");
        binding.title.setText(title);
        new GlobalWindowUtils(this,false);
        new WindowUtils(this, true, false);
        List<View> viewList = new ArrayList<>();
        viewList.add(binding.fullWide);
        viewList.add(binding.player);
        viewList.add(binding.tenNegative);
        viewList.add(binding.tenPositive);
        viewList.add(binding.btnPlay);
        viewList.add(binding.btnBack);
        viewList.add(binding.title);
        for(View v : viewList){
            v.setOnClickListener(this);
        }

        binding.downloadTxt.setVisibility(View.VISIBLE);
        binding.player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mDuration = mediaPlayer.getDuration();
                binding.seekBar.setMax(mDuration);
                if (restoredPosition > 0) {
                    mediaPlayer.seekTo(restoredPosition);
                }

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
                        binding.player.setVideoPath(String.valueOf(Uri.parse(videopath)));
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
        binding.player.setVideoPath(videopath);
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


    private void setupFileList() {
        FilesExtractor filesExtractor = new FilesExtractor(DownloadVideoViewActivity.this);
        ArrayList<VideoFile> videoFiles = filesExtractor.listVideos();
        Collections.sort(videoFiles, new Comparator<VideoFile>() {
            @Override
            public int compare(VideoFile v1, VideoFile v2) {
                return Long.compare(v2.getLastModified(), v1.getLastModified());
            }
        });
        if(videoFiles.size() == 1){
            videoFileList.addAll(videoFiles);
        }else{
            for(VideoFile data : videoFiles){
                String name = data.getName();
//            Log.e("Listnames","val: "+name);
                if(!name.equals(title)){
                    videoFileList.add(data);
                }
            }
        }

        binding.downloadTxt.setText(videoFiles.size() > 2? "Other video's": "Download video");
        videoDownloadAdapter = new VideoDownloadAdapter(this,videoFileList,this);
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
            public void onTick(long millisUntilFinished) {}
            public void onFinish() {
                binding.llVolume.setVisibility(View.GONE);
            }
        }.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title:
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
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                binding.rvDownloadvideo.setVisibility(View.GONE);
                binding.downloadTxt.setVisibility(View.GONE);
                new WindowUtils(this,true,false);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                binding.relativeVideo.setLayoutParams(params);
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
            binding.title.setVisibility(View.VISIBLE);
            binding.fullWide.setVisibility(isLandScape?View.GONE:View.VISIBLE);
            binding.btnPlay.setImageResource(isContinue? R.mipmap.play_white:R.mipmap.pause_white);

        } else {
            binding.time.setVisibility(View.INVISIBLE);
            binding.seekBar.setVisibility(View.INVISIBLE);
            binding.btnPlay.setVisibility(View.INVISIBLE);
            binding.tenPositive.setVisibility(View.INVISIBLE);
            binding.title.setVisibility(View.GONE);
            binding.tenNegative.setVisibility(View.INVISIBLE);
            binding.fullWide.setVisibility(View.INVISIBLE);
            ispause = false;
            startSeekUpdates();
        }
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
    protected void onPause() {
        super.onPause();
        if (binding.btnRefresh != null) {
            binding.btnRefresh.setVisibility(View.GONE);
        }
        boolean inPip = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isInPictureInPictureMode();
        if (binding.player != null) {
            currentPosition = binding.player.getCurrentPosition();
            if (!inPip && binding.player.isPlaying()) {
                binding.player.pause();
            }
        }
        if (mSeekRunnable != null) {
            mSeekHandler.removeCallbacks(mSeekRunnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (binding.btnRefresh != null) {
            binding.btnRefresh.setVisibility(View.GONE);
        }
        if (binding.player != null && currentPosition > 0) {
            binding.player.seekTo(currentPosition);
            if (!binding.player.isPlaying()) {
                binding.player.start();
                startSeekUpdates();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            new WindowUtils(this,true,false);
            isLandScape = false;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dip2px(250));
            binding.relativeVideo.setLayoutParams(params);
            binding.fullWide.setVisibility(isFinish?View.GONE:View.VISIBLE);
            binding.rvDownloadvideo.setVisibility(View.VISIBLE);
            binding.downloadTxt.setVisibility(View.VISIBLE);
        }else{
            super.onBackPressed();
            finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        if (isInPictureInPictureMode) {
            binding.rvDownloadvideo.setVisibility(View.GONE);
            binding.downloadTxt.setVisibility(View.GONE);
            binding.btnBack.setVisibility(View.GONE);
            binding.title.setVisibility(View.GONE);
            binding.fullWide.setVisibility(View.GONE);
            binding.time.setVisibility(View.GONE);
            binding.tenNegative.setVisibility(View.GONE);
            binding.tenPositive.setVisibility(View.GONE);
            binding.seekBar.setVisibility(View.GONE);
            binding.btnPlay.setVisibility(View.GONE);
        } else {
            binding.rvDownloadvideo.setVisibility(View.VISIBLE);
            binding.downloadTxt.setVisibility(View.VISIBLE);
            binding.btnBack.setVisibility(View.VISIBLE);
            binding.title.setVisibility(View.VISIBLE);
            binding.fullWide.setVisibility(View.VISIBLE);
            binding.time.setVisibility(View.VISIBLE);
            binding.tenNegative.setVisibility(View.VISIBLE);
            binding.tenPositive.setVisibility(View.VISIBLE);
            binding.seekBar.setVisibility(View.VISIBLE);
            binding.btnPlay.setVisibility(View.VISIBLE);
        }
    }
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            if(binding.player.isPlaying()){
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    enterPip();
                }
            }
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
    public void getPath(String path,String title) {
        if(path.isEmpty()){
            return;
        }
        binding.title.setText(title);
        binding.player.setVideoPath(path);
    }
}