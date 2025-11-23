package com.m.freemovie.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.m.freemovie.R;
import com.m.freemovie.Utils.views.FullScreenVideoView;

public class FullViewVideoActivity extends AppCompatActivity {
    private ImageView btn_back;
    private FullScreenVideoView mPlayerView;
    private TextView time;
    private String path;
    private SeekBar seekBar;
    private Handler mSeekHandler = new Handler(Looper.getMainLooper());
    private Runnable mSeekRunnable;
    private int mDuration = 0;
    private ImageView btn_play, download;
    private KProgressHUD hud;
    private boolean isSeekBarTracking = false;
    private boolean isVisible;
    private RelativeLayout relative_video;
    private boolean isCrop = false;
    private ImageView full_wide;
    private boolean ispause = false;
    private boolean isContinue = false;
    private ImageView rotate;
    private boolean isRotate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_full_view_video);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        );
        path = getIntent().getStringExtra("videoURl");
//        Log.d("VideoUrl", "value: " + path);
        btn_back = findViewById(R.id.btn_back);
        mPlayerView = findViewById(R.id.player);
        btn_play = findViewById(R.id.btn_play);
        btn_back.setOnClickListener(view -> onBackPressed());
        btn_play.setOnClickListener(view -> play_pause());
        seekBar = findViewById(R.id.seekBar);
        download = findViewById(R.id.download);
        full_wide = findViewById(R.id.full_wide);
        full_wide.setImageResource(R.mipmap.full_screen);
        relative_video = findViewById(R.id.relative_video);
        mPlayerView.setOnClickListener(view -> clickPause());
        rotate = findViewById(R.id.rotate);
        time = findViewById(R.id.time);
        btn_back = findViewById(R.id.btn_back);
        isVisible = getIntent().getBooleanExtra("isVisible", false);
        download.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        rotate.setOnClickListener(v -> rotateScreen());
        videoOrientation(true);
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait");
        full_wide.setVisibility(View.GONE);
        full_wide.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View view) {
                isCrop = !isCrop;
                seekBar.setVisibility(View.INVISIBLE);
                time.setVisibility(View.INVISIBLE);
                videoOrientation(isCrop);
            }
        });

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

    private void initShow() {
        hud.show();
    }

    private void rotateScreen() {
        isRotate = !isRotate;
        if (isRotate) {
            isCrop = false;
            ((Activity) mPlayerView.getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            isCrop = true;
            ((Activity) mPlayerView.getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @SuppressLint("WrongConstant")
    private void videoOrientation(boolean isCrop){
        int marginPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                20,
                getResources().getDisplayMetrics()
        );

        if(isCrop) {
            full_wide.setImageResource(R.mipmap.full_screen);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            );
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                lp.layoutInDisplayCutoutMode =
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            }
            getWindow().setAttributes(lp);
            setButtonMargins(25);
            Toast.makeText(getApplicationContext(), "Crop", Toast.LENGTH_SHORT).show();

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            );
            params.setMargins(marginPx, marginPx, marginPx, marginPx);
            params.addRule(RelativeLayout.ABOVE, time.getId());
            relative_video.setLayoutParams(params);
            RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(
                    marginPx,
                    marginPx
            );
            imageParams.setMargins(0, 0, 10, 20);
            imageParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
            imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            full_wide.setImageResource(R.mipmap.full_screen);
            full_wide.setLayoutParams(imageParams);
        }
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

    private void setButtonMargins(int marginTopDp) {
        int marginTopPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                marginTopDp,
                getResources().getDisplayMetrics()
        );

        RelativeLayout.LayoutParams backParams = (RelativeLayout.LayoutParams) btn_back.getLayoutParams();
        backParams.topMargin = marginTopPx;
        btn_back.setLayoutParams(backParams);

        RelativeLayout.LayoutParams downloadParams = (RelativeLayout.LayoutParams) download.getLayoutParams();
        downloadParams.topMargin = marginTopPx;
        download.setLayoutParams(downloadParams);
    }

    private void clickPause() {
        ispause = !ispause;
        if (ispause) {
            time.setVisibility(View.VISIBLE);
            seekBar.setVisibility(View.VISIBLE);
            rotate.setVisibility(View.VISIBLE);
            if(isVisible){
                download.setVisibility(View.VISIBLE);
            }
            btn_back.setVisibility(View.VISIBLE);
            btn_play.setVisibility(View.VISIBLE);
            rotate.setVisibility(View.VISIBLE);
            btn_play.setImageResource(isContinue? R.mipmap.play_white:R.mipmap.pause_white);
            new Handler().postDelayed(() -> {
                if (btn_play.getVisibility() == View.VISIBLE) {
                    download.setVisibility(View.GONE);
                    btn_back.setVisibility(View.GONE);
                    time.setVisibility(View.INVISIBLE);
                    rotate.setVisibility(View.INVISIBLE);
                    seekBar.setVisibility(View.INVISIBLE);
                    btn_play.setVisibility(View.INVISIBLE);
                }
            }, 5000);
        } else {
            download.setVisibility(View.GONE);
            btn_back.setVisibility(View.GONE);
            time.setVisibility(View.INVISIBLE);
            rotate.setVisibility(View.INVISIBLE);
            seekBar.setVisibility(View.INVISIBLE);
            rotate.setVisibility(View.INVISIBLE);
            btn_play.setVisibility(View.INVISIBLE);
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

                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    if(isCrop) {
                        params.addRule(RelativeLayout.ABOVE, time.getId());
                    }
                    relative_video.setLayoutParams(params);
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
        if (mPlayerView != null) {
            mPlayerView.pause();
            btn_play.setVisibility(View.VISIBLE);
        }
        if (mSeekRunnable != null) {
            mSeekHandler.removeCallbacks(mSeekRunnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPlayerView != null && mPlayerView.isPlaying()) {
            startSeekUpdates();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSeekRunnable != null) {
            mSeekHandler.removeCallbacks(mSeekRunnable);
        }
        if (mPlayerView != null) {
            mPlayerView.stopPlayback();
        }

        if (mSeekRunnable != null) {
            mSeekHandler.removeCallbacks(mSeekRunnable);
        }
        if (mPlayerView != null) {
            mPlayerView.stopPlayback();
        }
    }

    @Override
    public void onBackPressed() {
        if (mPlayerView != null && mPlayerView.isPlaying()) {
            mPlayerView.pause();
            mPlayerView.stopPlayback();
        }
        super.onBackPressed();
        finish();
    }
}