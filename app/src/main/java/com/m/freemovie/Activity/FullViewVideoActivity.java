package com.m.freemovie.Activity;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.m.freemovie.R;
import com.m.freemovie.Utils.GlobalWindowUtils;
import com.m.freemovie.Utils.WindowUtils;
import com.m.freemovie.Utils.views.FullScreenVideoView;

import java.util.ArrayList;
import java.util.List;

public class FullViewVideoActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView btn_back;
    private FullScreenVideoView mPlayerView;
    private TextView time,title;
    private String path;
    private SeekBar seekBar;
    private Handler mSeekHandler = new Handler(Looper.getMainLooper());
    private Runnable mSeekRunnable;
    private int mDuration = 0;
    private ImageView btn_play, download,ten_negative,ten_positive;
    private KProgressHUD hud;
    private boolean isSeekBarTracking = false;
    private boolean isVisible;
    private RelativeLayout relative_video;
    private boolean isCrop = false;
    private ImageView full_wide;
    private boolean ispause = false;
    private boolean isContinue = false;
    private ImageView rotate;
    private  Animation animRotate;
    private String videoTitle;
    private int currentPosition = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        new GlobalWindowUtils(this,false);
        setContentView(R.layout.activity_full_view_video);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        );
        path = getIntent().getStringExtra("videoURl");
        videoTitle = getIntent().getStringExtra("videoTitle");
//        Log.d("VideoUrl", "value: " + path);
        Log.d("videoTitle", "value: " + videoTitle);
        btn_back = findViewById(R.id.btn_back);
        mPlayerView = findViewById(R.id.player);
        btn_play = findViewById(R.id.btn_play);
        seekBar = findViewById(R.id.seekBar);
        title = findViewById(R.id.title);
        download = findViewById(R.id.download);
        ten_negative = findViewById(R.id.ten_negative);
        ten_positive = findViewById(R.id.ten_positive);
        full_wide = findViewById(R.id.full_wide);
        full_wide.setImageResource(R.mipmap.full_screen);
        relative_video = findViewById(R.id.relative_video);
        mPlayerView.setOnClickListener(view -> clickPause());
        rotate = findViewById(R.id.rotate);
        time = findViewById(R.id.time);
        btn_back = findViewById(R.id.btn_back);
        isVisible = getIntent().getBooleanExtra("isVisible", false);
        download.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait");
        full_wide.setVisibility(View.GONE);
        initLandScape();
        title.setVisibility(videoTitle !=null? View.VISIBLE : View.GONE);
        title.setText(videoTitle !=null ? videoTitle : "video.mp4");
        List<View> viewList = new ArrayList<>();
        viewList.add(full_wide);
        viewList.add(mPlayerView);
        viewList.add(ten_negative);
        viewList.add(ten_positive);
        viewList.add(btn_back);
        viewList.add(btn_play);
        for(View v : viewList){
            v.setOnClickListener(this);
        }

        if (path != null) {
            initShow();
            mPlayerView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mDuration = mediaPlayer.getDuration();
                    seekBar.setMax(mDuration);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mediaPlayer.start();
                                btn_play.setVisibility(View.INVISIBLE);
                                download.setVisibility(View.GONE);
                                btn_back.setVisibility(View.GONE);
                                time.setVisibility(View.INVISIBLE);
                                seekBar.setVisibility(View.INVISIBLE);
                                full_wide.setVisibility(View.INVISIBLE);
                                rotate.setVisibility(View.INVISIBLE);
                                title.setVisibility(View.INVISIBLE);
                                ten_positive.setVisibility(View.INVISIBLE);
                                ten_negative.setVisibility(View.INVISIBLE);
                                setupSeekBar();
                                startSeekUpdates();
                                if(mPlayerView.isPlaying()){
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
                            mp.seekTo(0);
                            mp.start();
                            btn_play.setVisibility(View.INVISIBLE);
                        }
                    });

                    mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mp, int what, int extra) {
                            Toast.makeText(getApplicationContext(),"Can't play video",Toast.LENGTH_SHORT).show();
                            finish();
                            return true;
                        }
                    });
                    mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                        @Override
                        public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {
                            double ratio = percent / 100.0;
                            int bufferingLevel = (int) (mediaPlayer.getDuration() * ratio);
                            seekBar.setSecondaryProgress(bufferingLevel);
                        }
                    });
                }
            });
            mPlayerView.setVideoPath(path);
        }


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
                clickPause();
                break;
            case R.id.full_wide:
                isCrop = !isCrop;
                seekBar.setVisibility(View.INVISIBLE);
                time.setVisibility(View.INVISIBLE);
                break;
            case R.id.ten_negative:
                if (mPlayerView == null || mDuration == 0) return;
                animRotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_image_negative_360);
                ten_negative.startAnimation(animRotate);

                int negativePosition = mPlayerView.getCurrentPosition();
                int negative_newPosition = negativePosition - 10000;
                if (negative_newPosition < 0) negative_newPosition = 0;

                mPlayerView.seekTo(negative_newPosition);
                seekBar.setProgress(negative_newPosition);
                startSeekUpdates();
                break;
            case R.id.ten_positive:
                if (mPlayerView == null || mDuration == 0) return;
                animRotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_image_positive_360);
                ten_positive.startAnimation(animRotate);

                int positivePosition = mPlayerView.getCurrentPosition();
                int positive_newPosition = positivePosition + 10000;
                if (positive_newPosition > mDuration) positive_newPosition = mDuration;
                mPlayerView.seekTo(positive_newPosition);
                seekBar.setProgress(positive_newPosition);
                startSeekUpdates();
                break;
        }

    }

    private void initLandScape() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        relative_video.setLayoutParams(params);
        new WindowUtils(this,true,false);
    }

    private void setCutoutMode(int mode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.layoutInDisplayCutoutMode = mode;
            getWindow().setAttributes(params);
        }
    }

    private void initShow() {
        hud.show();
    }

    private void play_pause() {
        if(mPlayerView.isPlaying()){
            mPlayerView.pause();
            btn_play.setImageResource(R.mipmap.play_white);
            isContinue = true;
        }else{
            btn_play.setImageResource(R.mipmap.pause_white);
            isContinue = false;
            mPlayerView.start();
        }
    }


    private void clickPause() {
        ispause = !ispause;
        if (ispause) {
            time.setVisibility(View.VISIBLE);
            seekBar.setVisibility(View.VISIBLE);
            rotate.setVisibility(View.VISIBLE);
            ten_positive.setVisibility(View.VISIBLE);
            ten_negative.setVisibility(View.VISIBLE);
            title.setVisibility(View.VISIBLE);
            if(isVisible){
                download.setVisibility(View.VISIBLE);
            }
            btn_back.setVisibility(View.VISIBLE);
            btn_play.setVisibility(View.VISIBLE);
            rotate.setVisibility(View.VISIBLE);
            btn_play.setImageResource(isContinue? R.mipmap.play_white:R.mipmap.pause_white);
        } else {
            download.setVisibility(View.GONE);
            btn_back.setVisibility(View.GONE);
            time.setVisibility(View.INVISIBLE);
            rotate.setVisibility(View.INVISIBLE);
            seekBar.setVisibility(View.INVISIBLE);
            rotate.setVisibility(View.INVISIBLE);
            btn_play.setVisibility(View.INVISIBLE);
            ten_positive.setVisibility(View.INVISIBLE);
            ten_negative.setVisibility(View.INVISIBLE);
            title.setVisibility(View.INVISIBLE);
            ten_positive.setVisibility(View.INVISIBLE);
            ten_negative.setVisibility(View.INVISIBLE);
            startSeekUpdates();
        }
    }



    @SuppressLint("ClickableViewAccessibility")
    private void setupSeekBar() {
        mPlayerView.setOnTouchListener(null);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mPlayerView != null) {
                    time.setText(formatTime(progress / 1000) + " / " + formatTime(mDuration / 1000));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekBarTracking = true;
                if (mPlayerView.isPlaying()) {
                    mPlayerView.pause();
                }
                mSeekHandler.removeCallbacks(mSeekRunnable);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeekBarTracking = false;
                if (mPlayerView != null) {
                    mPlayerView.seekTo(seekBar.getProgress());
                    mPlayerView.start();
                    download.setVisibility(View.GONE);
                    btn_back.setVisibility(View.GONE);
                    btn_play.setVisibility(View.INVISIBLE);
                    seekBar.setVisibility(View.INVISIBLE);
                    time.setVisibility(View.INVISIBLE);
                    rotate.setVisibility(View.INVISIBLE);
                    ten_positive.setVisibility(View.INVISIBLE);
                    ten_negative.setVisibility(View.INVISIBLE);
                    title.setVisibility(View.INVISIBLE);
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
                if (mPlayerView != null && mPlayerView.isPlaying() && !isSeekBarTracking) {
                    int currentPosition = mPlayerView.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    time.setText(formatTime(currentPosition / 1000) + " / " + formatTime(mDuration / 1000));
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
        if (mPlayerView!= null && mPlayerView.isPlaying()) {
            currentPosition = mPlayerView.getCurrentPosition();
            mPlayerView.pause();
        }
        if (mSeekRunnable != null) {
            mSeekHandler.removeCallbacks(mSeekRunnable);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mPlayerView != null) {
            if (currentPosition > 0) {
                mPlayerView.seekTo(currentPosition);
            }
            if (!mPlayerView.isPlaying() && currentPosition > 0) {
                mPlayerView.start();
                startSeekUpdates();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mPlayerView != null && mPlayerView.isPlaying()) {
            mPlayerView.stopPlayback();
        }
        super.onBackPressed();
        finish();
    }

}