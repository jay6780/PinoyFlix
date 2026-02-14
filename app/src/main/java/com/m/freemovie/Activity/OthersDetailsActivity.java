package com.m.freemovie.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.m.freemovie.R;
import com.m.freemovie.Retrofit.AppConstant;
import com.m.freemovie.Utils.DbHelper.BookmarkDbHelper2;
import com.m.freemovie.Utils.SPUtils;
import com.m.freemovie.Utils.WindowUtils;
import com.m.freemovie.databinding.ActivityOthersDetailsBinding;
import com.m.freemovie.mvp.Model.ClassBean.DetailBean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OthersDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityOthersDetailsBinding binding;
    private String TAG = "OthersDetailsActivity";
    private InterstitialAd mInterstitialAd;
    private String title,videoId,image,videoId2;
    private KProgressHUD hud;
    private BookmarkDbHelper2 bookmarkDbHelper;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOthersDetailsBinding.inflate(getLayoutInflater());
        title = getIntent().getStringExtra("title");
        videoId = getIntent().getStringExtra("videoId");
        videoId2 = getIntent().getStringExtra("videoId2");
        image = getIntent().getStringExtra("image");

//        Log.e("VideoId","1: "+videoId+"2: "+videoId2);

        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        new CountDownTimer(1500, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                initViews();
                hud.dismiss();

            }

        }.start();

        if(!SPUtils.getInstance().getBoolean(AppConstant.adOther)) {
            loadAd();
        }
        bookmarkDbHelper = new BookmarkDbHelper2(this);

        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait");
        hud.show();



    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (hud != null && hud.isShowing()) {
            hud.dismiss();
        }
    }

    @SuppressLint("MissingPermission")
    private void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, AppConstant.InterstitialId, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        if(!AppConstant.isAddFree){
                            mInterstitialAd = interstitialAd;
//                        Log.i(TAG, "Ad Loaded. Showing it now automatically...");
                            Toast.makeText(getApplicationContext(),"Ads incoming",Toast.LENGTH_SHORT).show();
                            mInterstitialAd.show(OthersDetailsActivity.this);
                            SPUtils.getInstance().put(AppConstant.adOther,true);
                        }

                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                Log.d(TAG, "Ad dismissed by user.");
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
//                                Log.e(TAG, "Ad failed to show: " + adError.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                        Log.e(TAG, "Ad failed to load: " + loadAdError.getMessage());
                    }
                });
    }

    private void initViews() {
        binding.tvWatch.setOnClickListener(this);
        binding.ivBack.setOnClickListener(this);
        binding.tvTitle.setText(title == null? "N/A": title);
        binding.tvOriginal.setText(title == null? "N/A": title);
        binding.tvDescription.setText("N/A");

        binding.tvInfo.setVisibility(View.GONE);
        Double min = 0.0;
        Double max = 10.0;
        double x = (Math.random() * ((max - min) + 1)) + min;
        double xrounded = Math.round(x * 100.0) / 100.0;
        binding.tvRate.setText(String.valueOf(xrounded));

        try {
            Glide.with(this)
                    .asBitmap()
                    .load(image)
                    .into(binding.ivSmallimg);
            Glide.with(this)
                    .asBitmap()
                    .load(image)
                    .into(binding.ivBig);
        }catch (Exception e){
            e.printStackTrace();
        }
        binding.ivBook.setOnClickListener(this);

        setImageData(videoId);
    }
    private void setImageData(String videoId) {
        boolean isBookmarked = bookmarkDbHelper.isBookmarked(videoId);
        binding.ivBook.setImageResource(isBookmarked ? R.drawable.booked : R.drawable.unbooked);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new WindowUtils(this,false,false);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_watch:
                String[] option = {"Player 1","Player 2"};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                TextView titleView = new TextView(this);
                titleView.setText("Select player");
                titleView.setTextColor(Color.BLACK);
                titleView.setPadding(40, 40, 40, 20);
                titleView.setTextSize(15);

                builder.setCustomTitle(titleView);

                builder.setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                startActivity(new Intent(OthersDetailsActivity.this, OtherWebviewActivity.class)
                                        .putExtra("videoId",videoId2)
                                        .putExtra("title",title)
                                        .putExtra("position",1));
                                break;
                            case 1:
                                startActivity(new Intent(OthersDetailsActivity.this, OtherWebviewActivity.class)
                                        .putExtra("videoId",videoId)
                                        .putExtra("title",title)
                                        .putExtra("position",2));
                                break;
                        }
                    }
                });
                builder.show();

                break;
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_book:
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
                DetailBean details = new DetailBean(videoId, timestamp, image, title,"false",videoId2);
                details.setVideoId(videoId);
                details.setTimeStamp(timestamp);
                bookmarkDbHelper.toggleBookmark(details, 8);
                setImageData(videoId);
                break;
        }

    }
}