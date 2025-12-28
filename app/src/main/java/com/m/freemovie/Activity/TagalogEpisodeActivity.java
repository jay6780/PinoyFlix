package com.m.freemovie.Activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.m.freemovie.R;
import com.m.freemovie.Utils.WindowUtils;
import com.m.freemovie.adapter.TagalogEpisodeAdapter;
import com.m.freemovie.databinding.ActivityTagalogEpisodeBinding;
import com.m.freemovie.mvp.ClassBean.TagalogEpisode;
import com.m.freemovie.mvp.ClassBean.TagalogEpisodeBean;
import com.m.freemovie.mvp.Contract.TagalogEpisodeContract;
import com.m.freemovie.mvp.Presenter.TagalogEpisodePresenter;

import java.util.ArrayList;
import java.util.List;

public class TagalogEpisodeActivity extends AppCompatActivity implements TagalogEpisodeContract.View, TagalogEpisodeAdapter.VideoPlayListerner,View.OnClickListener {
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        tagalogEpisodePresenter.getUrl(url);
        binding.rvEpisode.setLayoutManager(new LinearLayoutManager(this));
        tagalogEpisodeAdapter = new TagalogEpisodeAdapter(this);
        binding.rvEpisode.setAdapter(tagalogEpisodeAdapter);
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
                        mp.seekTo(0);
                        mp.start();
                        binding.btnPlay.setVisibility(View.INVISIBLE);
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
                        binding.seekBar.setSecondaryProgress(bufferingLevel);
                    }
                });
            }
        });

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
            for(TagalogEpisodeBean.ResultsBean data : tagalogEpisodeBean.getResults()){
                if(!data.getEpisodes().isEmpty()){
                    for(TagalogEpisodeBean.ResultsBean.EpisodesBean dataEpisode : data.getEpisodes()){
                        tagalogEpisodeList.add(new TagalogEpisode(dataEpisode.getEpisode(),imageUrl,dataEpisode.getVideoUrl()));
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Episodes not found",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            tagalogEpisodeAdapter.setNewData(tagalogEpisodeList);
        }
    }

    @Override
    public void getVideoUrl(String videoUrl) {
//        Log.d("VideoUrl","val: "+videoUrl);
        if(!videoUrl.isEmpty()){
            binding.player.setVideoPath(videoUrl);
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
                isLandScape = true;
                binding.fullWide.setVisibility(View.GONE);
                new WindowUtils(this, true,true);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                binding.rvEpisode.setVisibility(View.GONE);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                int marginPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        30,
                        getResources().getDisplayMetrics()
                );
                RelativeLayout.LayoutParams params1  = new RelativeLayout.LayoutParams(marginPx, marginPx);
                params1.setMargins(5,10,0,0);
                RelativeLayout.LayoutParams params2  = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                RelativeLayout.LayoutParams params3  = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params2.addRule(RelativeLayout.RIGHT_OF,binding.btnBack.getId());
                params2.setMargins(0,15,0,0);
                params3.setMargins(0,0,0,20);
                params3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                binding.seekBar.setLayoutParams(params3);
                binding.title.setLayoutParams(params2);
                binding.btnBack.setLayoutParams(params1);
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
        if (binding.player != null) {
            binding.player.pause();
        }
        if (mSeekRunnable != null) {
            mSeekHandler.removeCallbacks(mSeekRunnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (binding.player != null && binding.player.isPlaying()) {
            startSeekUpdates();
        }
    }

    @Override
    public void onBackPressed() {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            new WindowUtils(this,false,false);
            int marginPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    330,
                    getResources().getDisplayMetrics()
            );
            int iconspx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    30,
                    getResources().getDisplayMetrics()
            );
            isLandScape = false;
            RelativeLayout.LayoutParams params1  = new RelativeLayout.LayoutParams(iconspx, iconspx);
            params1.setMargins(5,60,0,0);
            RelativeLayout.LayoutParams params2  = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params2.addRule(RelativeLayout.RIGHT_OF,binding.btnBack.getId());
            params2.setMargins(0,70,0,0);

            ViewGroup parent = (ViewGroup) binding.seekBar.getParent();
            parent.removeView(binding.seekBar);

            RelativeLayout.LayoutParams seekbarParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            seekbarParams.addRule(RelativeLayout.ABOVE, R.id.full_wide);
            binding.seekBar.setLayoutParams(seekbarParams);
            parent.addView(binding.seekBar);
            binding.title.setLayoutParams(params2);
            binding.btnBack.setLayoutParams(params1);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, marginPx);
            binding.relativeVideo.setLayoutParams(params);
            binding.fullWide.setVisibility(View.VISIBLE);
            binding.rvEpisode.setVisibility(View.VISIBLE);
        }else{
            super.onBackPressed();
            finish();
        }
    }
}