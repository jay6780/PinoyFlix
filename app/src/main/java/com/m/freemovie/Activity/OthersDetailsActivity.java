package com.m.freemovie.Activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.m.freemovie.R;
import com.m.freemovie.Utils.DbHelper.BookmarkDbHelper2;
import com.m.freemovie.Utils.DialogSourceUtils;
import com.m.freemovie.Utils.GlobalWindowUtils;
import com.m.freemovie.Utils.WindowUtils;
import com.m.freemovie.databinding.ActivityOthersDetailsBinding;
import com.m.freemovie.mvp.Contract.PinoyPediaContract;
import com.m.freemovie.mvp.Model.ClassBean.DetailBean;
import com.m.freemovie.mvp.Model.ClassBean.PinoyMediaDetailBean;
import com.m.freemovie.mvp.Model.ClassBean.PinoyRuDetailBean;
import com.m.freemovie.mvp.Presenter.PinoyPediaPresenter;
import com.orhanobut.dialogplus.DialogPlus;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OthersDetailsActivity extends AppCompatActivity implements View.OnClickListener, PinoyPediaContract.View {
    private ActivityOthersDetailsBinding binding;
    private String TAG = "OthersDetailsActivity";
    private KProgressHUD hud;
    private BookmarkDbHelper2 bookmarkDbHelper;
    private int type = 1;
    private String downloadId;
    private String link, title, image;
    private String Image;
    private PinoyPediaPresenter presenter;
    private List<PinoyRuDetailBean> pinoyRuDetailBeanList = new ArrayList<>();
    private List<String> filteredVideoUrls = new ArrayList<>();

    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();

    private static final Pattern DOWNLOAD_PATTERN = Pattern.compile("https://pinoymoviepedia\\.ru/links/([a-zA-Z0-9]+)/");
    private static final Pattern DOWNLOAD_TABLE_PATTERN = Pattern.compile("<a href='https://pinoymoviepedia\\.ru/links/([a-zA-Z0-9]+)/' target='_blank'>Download</a>");


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new GlobalWindowUtils(this,false);
        binding = ActivityOthersDetailsBinding.inflate(getLayoutInflater());
        link = getIntent().getStringExtra("link");
        Image = getIntent().getStringExtra("Image");
        type = getIntent().getIntExtra("type", 1);
        presenter = new PinoyPediaPresenter(this);

//        Log.d("Image","val: "+Image);


        if (link != null) {
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
                presenter.getUrl(link);
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
        if (dialog !=null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    private void initViews() {
        binding.tvWatch.setOnClickListener(this);
        binding.ivBack.setOnClickListener(this);
        binding.ivBook.setOnClickListener(this);
        binding.language.setText("TLD");
        setImageData(link);
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
    protected void onPause() {
        super.onPause();
        if (dialog !=null && dialog.isShowing()){
            dialog.dismiss();
        }
    }



    private DialogPlus dialog;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_watch:
                if (pinoyRuDetailBeanList !=null && pinoyRuDetailBeanList.isEmpty()) {
                    return;
                }
                if(filteredVideoUrls !=null && filteredVideoUrls.isEmpty()){
                    Toast.makeText(getApplicationContext(), "No video source found", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                dialog = new DialogSourceUtils().TagalogWatchSource(OthersDetailsActivity.this,type,title,pinoyRuDetailBeanList);
                dialog.show();
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_book:
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
                DetailBean details = new DetailBean(link, timestamp, image, title, "false");
                details.setVideoId(link);
                details.setTimeStamp(timestamp);
                bookmarkDbHelper.toggleBookmark(details, 8);
                setImageData(link);
                break;
        }

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void showError(String error) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void getDetailSuccess(PinoyMediaDetailBean bean) {
        if (bean != null && bean.getResults() != null) {
            Double min = 0.0;
            Double max = 10.0;
            double x = (Math.random() * ((max - min) + 1)) + min;
            double xrounded = Math.round(x * 100.0) / 100.0;
            binding.tvRate.setText(String.valueOf(xrounded));
            title = bean.getResults().getTitle();
            binding.tvTitle.setText(title == null ? "N/A" : title);
            binding.tvOriginal.setText(bean.getResults().getOriginalTitle() == null ? title : bean.getResults().getOriginalTitle());
            binding.tvDescription.setText(bean.getResults().getSynopsis());
            binding.tvInfo.setVisibility(View.GONE);

            String link = "https://pinoymoviepedia.ru/links/" + downloadId + "/";
            pinoyRuDetailBeanList.clear();

            List<String> embedUrls = bean.getResults().getEmbedUrls();
            for (String videoUrl : embedUrls) {
                if (videoUrl.contains("voe.sx")) {
                    filteredVideoUrls.add(videoUrl);
                }
            }

            pinoyRuDetailBeanList.add(new PinoyRuDetailBean(embedUrls, link));
            if (!bean.getResults().getImages().isEmpty()) {
                for (PinoyMediaDetailBean.ResultsBean.ImagesBean detailBean : bean.getResults().getImages()) {
                    image = detailBean.getThumb();
                }
            } else {
                this.image = Image;
            }

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
        }
    }
}