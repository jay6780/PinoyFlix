package com.m.freemovie.Activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
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
import com.m.freemovie.Utils.DbHelper.BookmarkDbHelper;
import com.m.freemovie.Utils.DbHelper.PinoyWatchHistoryHelper;
import com.m.freemovie.Utils.WindowUtils;
import com.m.freemovie.adapter.TagalogEpisodeAdapter;
import com.m.freemovie.databinding.ActivityTagalogEpisodeBinding;
import com.m.freemovie.mvp.ClassBean.DetailBean;
import com.m.freemovie.mvp.ClassBean.DownloadPlayerListerner;
import com.m.freemovie.mvp.ClassBean.TagalogEpisode;
import com.m.freemovie.mvp.ClassBean.TagalogEpisodeBean;
import com.m.freemovie.mvp.Contract.TagalogEpisodeContract;
import com.m.freemovie.mvp.Presenter.TagalogEpisodePresenter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        tagalogEpisodePresenter.getUrl(url);
        binding.rvEpisode.setLayoutManager(new LinearLayoutManager(this));
        tagalogEpisodeAdapter = new TagalogEpisodeAdapter(this,this);
        binding.rvEpisode.setAdapter(tagalogEpisodeAdapter);
        dbHelper = new PinoyWatchHistoryHelper(this);
        bookmarkDbHelper = new BookmarkDbHelper(this);
        binding.title.setText(title);
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
                        TagalogEpisode tagalogEpisode = new TagalogEpisode(dataEpisode.getEpisode(),imageUrl,dataEpisode.getVideoUrl());
                        boolean isWatched = dbHelper.isEpisodeWatched(dataEpisode.getVideoUrl(), dataEpisode.getEpisode());
                        tagalogEpisode.setWatched(isWatched);
                        tagalogEpisodeList.add(tagalogEpisode);
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
        if(videoUrl.isEmpty() || videoUrl == null){
            return;
        }
        binding.player.setVideoPath(String.valueOf(Uri.parse(videoUrl)));
        isFinish = false;
        binding.btnRefresh.setVisibility(View.GONE);
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
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                int marginPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        35,
                        getResources().getDisplayMetrics()
                );
                RelativeLayout.LayoutParams params1  = new RelativeLayout.LayoutParams(marginPx, marginPx);
                params1.setMargins(5,10,0,0);
                RelativeLayout.LayoutParams params2  = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params2.addRule(RelativeLayout.RIGHT_OF,binding.btnBack.getId());
                params2.setMargins(0,20,0,0);
                binding.title.setLayoutParams(params2);
                binding.btnBack.setLayoutParams(params1);
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
            int marginPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    300,
                    getResources().getDisplayMetrics()
            );
            int iconspx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    35,
                    getResources().getDisplayMetrics()
            );
            isLandScape = false;
            RelativeLayout.LayoutParams params1  = new RelativeLayout.LayoutParams(iconspx, iconspx);
            params1.setMargins(5,60,0,0);
            RelativeLayout.LayoutParams params2  = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params2.addRule(RelativeLayout.RIGHT_OF,binding.btnBack.getId());
            params2.setMargins(0,70,0,0);
            binding.title.setLayoutParams(params2);
            binding.btnBack.setLayoutParams(params1);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, marginPx);
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

    private void downloadVideo(String videoUrl,String episode) {
        isFirstTask = true;
        File outputFile = getLocalFile(episode);
        downloadHud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                .setLabel("Downloading...")
                .setMaxProgress(100)
                .setCancellable(true);
        downloadHud.show();

        downloadHud.setCancellable(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                isFirstTask = false;
                if (outputFile.exists()) {
                    outputFile.delete();
                    Toast.makeText(getApplicationContext(), "Download cancelled", Toast.LENGTH_SHORT).show();
                }
            }
        });

        new Thread(() -> {
            FileOutputStream outputStream = null;
            InputStream inputStream = null;
            HttpURLConnection connection = null;

            try {
                URL url = new URL(videoUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(60000);
                connection.setReadTimeout(60000);
                connection.setInstanceFollowRedirects(true);


                long existingLength = 0;
                if (outputFile.exists()) {
                    existingLength = outputFile.length();
                    connection.setRequestProperty("Range", "bytes=" + existingLength + "-");
                }

                connection.connect();
                int responseCode = connection.getResponseCode();

                boolean isResume = (responseCode == HttpURLConnection.HTTP_PARTIAL);
                if (responseCode == HttpURLConnection.HTTP_OK || isResume) {
                    inputStream = connection.getInputStream();

                    if (isResume) {
                        outputStream = new FileOutputStream(outputFile, true);
                    } else {
                        outputStream = new FileOutputStream(outputFile);
                    }

                    int contentLength = connection.getContentLength();
                    if (isResume) {
                        contentLength += existingLength;
                    }

                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    long totalBytesRead = existingLength;

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        // Check if download was cancelled
                        if (!isFirstTask) {
                            break;
                        }

                        outputStream.write(buffer, 0, bytesRead);
                        totalBytesRead += bytesRead;

                        if (contentLength > 0) {
                            final int progress = (int) ((totalBytesRead * 100) / contentLength);
                            runOnUiThread(() -> downloadHud.setProgress(progress));
                        }
                    }

                    outputStream.close();
                    inputStream.close();

                    if (isFirstTask) {
                        runOnUiThread(() -> {
                            if (downloadHud != null && downloadHud.isShowing()) {
                                downloadHud.dismiss();
                            }
                            isFirstTask = false;
                            Toast.makeText(getApplicationContext(),
                                    "Download Complete! Saved to: " + outputFile.getAbsolutePath(),
                                    Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), Download_videoActivity.class));
                        });
                    }

                } else {
                    runOnUiThread(() -> {
                        if (downloadHud != null && downloadHud.isShowing()) {
                            downloadHud.dismiss();
                        }
                        isFirstTask = false;
                        Toast.makeText(getApplicationContext(), "Download Failed: HTTP " + responseCode,
                                Toast.LENGTH_SHORT).show();
                    });
                }

            } catch (Exception e) {
                // Only show error if not cancelled
                if (isFirstTask) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        if (downloadHud != null && downloadHud.isShowing()) {
                            downloadHud.dismiss();
                        }
                        isFirstTask = false;
                        Toast.makeText(getApplicationContext(),
                                "Download Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            } finally {
                try {
                    if (outputStream != null) outputStream.close();
                    if (inputStream != null) inputStream.close();
                    if (connection != null) connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}