package com.m.freemovie.Activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.m.freemovie.R;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityDownloadVideoViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        title = getIntent().getStringExtra("title");
        videopath = getIntent().getStringExtra("videopath");
        binding.title.setText(title);

        List<View> viewList = new ArrayList<>();
        viewList.add(binding.fullWide);
        viewList.add(binding.player);
        viewList.add(binding.tenNegative);
        viewList.add(binding.tenPositive);
        viewList.add(binding.btnPlay);
        viewList.add(binding.btnBack);
        for(View v : viewList){
            v.setOnClickListener(this);
        }

        binding.downloadTxt.setVisibility(View.VISIBLE);
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
        initTopPadding(70);
        setupFileList();
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


    private void initTopPadding(int topPadding) {
        ViewCompat.setOnApplyWindowInsetsListener(binding.llRoot, (v, windowInsets) -> {
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.topMargin = topPadding;
            v.setLayoutParams(mlp);
            return WindowInsetsCompat.CONSUMED;
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
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
                binding.rvDownloadvideo.setVisibility(View.GONE);
                binding.downloadTxt.setVisibility(View.GONE);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                initTopPadding(10);
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
            int marginPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    300,
                    getResources().getDisplayMetrics()
            );

            isLandScape = false;
            initTopPadding(70);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, marginPx);
            binding.relativeVideo.setLayoutParams(params);
            binding.fullWide.setVisibility(isFinish?View.GONE:View.VISIBLE);
            binding.rvDownloadvideo.setVisibility(View.VISIBLE);
            binding.downloadTxt.setVisibility(View.VISIBLE);
        }else{
            super.onBackPressed();
            finish();
        }
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