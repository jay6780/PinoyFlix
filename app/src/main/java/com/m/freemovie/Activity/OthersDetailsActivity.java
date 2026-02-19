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
import com.kaopiz.kprogresshud.KProgressHUD;
import com.m.freemovie.R;
import com.m.freemovie.Retrofit.AppConstant;
import com.m.freemovie.Utils.DbHelper.BookmarkDbHelper;
import com.m.freemovie.Utils.DbHelper.BookmarkDbHelper2;
import com.m.freemovie.Utils.SPUtils;
import com.m.freemovie.Utils.WindowUtils;
import com.m.freemovie.databinding.ActivityOthersDetailsBinding;
import com.m.freemovie.mvp.Model.ClassBean.DetailBean;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OthersDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityOthersDetailsBinding binding;
    private String TAG = "OthersDetailsActivity";
    private String title,image,videoId,videoId2;
    private KProgressHUD hud;
    private BookmarkDbHelper2 bookmarkDbHelper;
    private int type = 1;
    private String downloadId;
    private String link;

    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();

    private static final Pattern DOWNLOAD_PATTERN = Pattern.compile("https://pinoymoviepedia\\.ru/links/([a-zA-Z0-9]+)/");
    private static final Pattern DOWNLOAD_TABLE_PATTERN = Pattern.compile("<a href='https://pinoymoviepedia\\.ru/links/([a-zA-Z0-9]+)/' target='_blank'>Download</a>");


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOthersDetailsBinding.inflate(getLayoutInflater());
        title = getIntent().getStringExtra("title");
        link = getIntent().getStringExtra("link");
        videoId = getIntent().getStringExtra("videoId");
        videoId2 = getIntent().getStringExtra("videoId2");
        type = getIntent().getIntExtra("type", 1);
        image = getIntent().getStringExtra("image");

        if(link !=null){
            new Thread(() -> {
                try {
                    final String id = fetchDownloadId(link);
                    runOnUiThread(() -> {
                        downloadId = id;
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Toast.makeText(OthersDetailsActivity.this, "Failed to load download link", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();

        }


//        Log.e("DownloadLink","val: "+link);

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


        bookmarkDbHelper = new BookmarkDbHelper2(this);

        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait");
        hud.show();


    }


    private static String fetchDownloadId(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .build();

        try (Response response = OK_HTTP_CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) return null;

            String html = response.body().string();

            Matcher downloadMatcher = DOWNLOAD_TABLE_PATTERN.matcher(html);
            if (downloadMatcher.find()) {
                return downloadMatcher.group(1);
            }

            Matcher linkMatcher = DOWNLOAD_PATTERN.matcher(html);
            if (linkMatcher.find()) {
                return linkMatcher.group(1);
            }
        }
        return null;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (hud != null && hud.isShowing()) {
            hud.dismiss();
        }
    }

    private void initViews() {
        binding.tvWatch.setOnClickListener(this);
        binding.ivBack.setOnClickListener(this);
        binding.tvTitle.setText(title == null ? "N/A" : title);
        binding.tvOriginal.setText(title == null ? "N/A" : title);
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
        } catch (Exception e) {
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
        new WindowUtils(this, false, false);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_watch:
                String[] option = {"Player 1", "Player 2","Download"};
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
                                        .putExtra("videoId", videoId2)
                                        .putExtra("title", title)
                                        .putExtra("type", type)
                                        .putExtra("position", 1));
                                break;
                            case 1:
                                startActivity(new Intent(OthersDetailsActivity.this, OtherWebviewActivity.class)
                                        .putExtra("videoId", videoId)
                                        .putExtra("title", title)
                                        .putExtra("type", type)
                                        .putExtra("position", 2));
                                break;
                            case 2:
                                if(downloadId == null){
                                    Toast.makeText(getApplicationContext(),"No available links for download",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                String link = "https://pinoymoviepedia.ru/links/"+downloadId+"/";
                                Intent intent = new Intent(getApplicationContext(), DownloadWebview.class);
                                intent.putExtra("DownloadUrl", link);
                                intent.putExtra("EpisodeNum", "");
                                intent.putExtra("title", title);
                                startActivity(intent);
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

                String primary = videoId == null ? videoId2 : videoId;
                String secondary = videoId2 == null ? videoId : videoId2;
//                Log.d("VideoId","val1: "+primary);
//                Log.d("VideoId","val2: "+secondary);
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
                DetailBean details = new DetailBean(primary, timestamp, image, title, "false", secondary,link);
                details.setVideoId(primary);
                details.setTimeStamp(timestamp);
                bookmarkDbHelper.toggleBookmark(details, 8);
                setImageData(primary);
                break;
        }

    }
}