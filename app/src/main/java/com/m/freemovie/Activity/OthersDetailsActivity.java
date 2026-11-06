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
import java.util.Collections;
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
    private KProgressHUD hud;
    private BookmarkDbHelper2 bookmarkDbHelper;
    private int type = 1;
    private String downloadId;
    private String link, title, image;
    private String Image;
    private PinoyPediaPresenter presenter;
    private List<PinoyRuDetailBean> pinoyRuDetailBeanList = new ArrayList<>();
    private final List<String> filteredVideoUrls = Collections.synchronizedList(new ArrayList<>());

    private volatile boolean detailLoaded = false;
    private volatile boolean scrapeDone = false;
    private String pendingDownloadLink;

    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();

    private static final Pattern DOWNLOAD_PATTERN = Pattern.compile("https://pinoymoviepedia\\.ru/links/([a-zA-Z0-9]+)/");
    private static final Pattern DOWNLOAD_TABLE_PATTERN = Pattern.compile("<a href='https://pinoymoviepedia\\.ru/links/([a-zA-Z0-9]+)/' target='_blank'>Download</a>");
    private static final Pattern EMBED_IFRAME_PATTERN = Pattern.compile(
            "<iframe[^>]+src=[\"'](https?://[^\"']*(?:voe\\.sx|streamtape|dood|filemoon|mixdrop|embedsito|sbembed)[^\"']*)[\"']",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern EMBED_GENERIC_PATTERN = Pattern.compile(
            "<iframe[^>]+src=[\"'](https?://[^\"']+)[\"']",
            Pattern.CASE_INSENSITIVE
    );

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new GlobalWindowUtils(this, false);
        binding = ActivityOthersDetailsBinding.inflate(getLayoutInflater());
        link = getIntent().getStringExtra("link");
        Image = getIntent().getStringExtra("Image");
        type = getIntent().getIntExtra("type", 1);
        presenter = new PinoyPediaPresenter(this);

        if (link == null || link.isEmpty()) {
            Toast.makeText(this, "Invalid link", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        new Thread(() -> {
            try {
                FetchResult fr = fetchPageData(link);
                runOnUiThread(() -> {
                    downloadId = fr.downloadId;
                    synchronized (filteredVideoUrls) {
                        for (String u : fr.embedUrls) {
                            if (!filteredVideoUrls.contains(u)) filteredVideoUrls.add(u);
                        }
                    }
                    scrapeDone = true;
                    if (detailLoaded) {
                        rebuildDetailBeanList();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    scrapeDone = true;
                    if (detailLoaded) {
                        rebuildDetailBeanList();
                    }
                });
            }
        }).start();

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

    private void rebuildDetailBeanList() {
        List<String> merged;
        synchronized (filteredVideoUrls) {
            merged = new ArrayList<>(filteredVideoUrls);
        }
        String dl = pendingDownloadLink != null
                ? pendingDownloadLink
                : "https://pinoymoviepedia.ru/links/" + downloadId + "/";
        pinoyRuDetailBeanList.clear();
        pinoyRuDetailBeanList.add(new PinoyRuDetailBean(merged, dl));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (hud != null && hud.isShowing()) {
            hud.dismiss();
        }
        if (dialog != null && dialog.isShowing()) {
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
        if (dialog != null && dialog.isShowing()) {
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
                if (!detailLoaded || !scrapeDone) {
                    Toast.makeText(this, "Still loading sources...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pinoyRuDetailBeanList.isEmpty()) {
                    Toast.makeText(this, "No video source found", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<String> urlsToCheck = pinoyRuDetailBeanList.get(0).getVideoUrls();
                if (urlsToCheck == null || urlsToCheck.isEmpty()) {
                    Toast.makeText(this, "No video source found", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog = new DialogSourceUtils().TagalogWatchSource(OthersDetailsActivity.this, type, title, pinoyRuDetailBeanList);
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
        detailLoaded = true;
        rebuildDetailBeanList();
    }

    @Override
    public void hideLoading() {
    }

    @Override
    public void getDetailSuccess(PinoyMediaDetailBean bean) {
        detailLoaded = true;
        if (bean == null || bean.getData() == null) {
            rebuildDetailBeanList();
            return;
        }

        Double min = 0.0;
        Double max = 10.0;
        double x = (Math.random() * ((max - min) + 1)) + min;
        double xrounded = Math.round(x * 100.0) / 100.0;
        binding.tvRate.setText(String.valueOf(xrounded));

        title = bean.getData().getTitle();
        binding.tvTitle.setText(title == null ? "N/A" : title);
        binding.tvOriginal.setText(bean.getData().getTitle() == null ? title : bean.getData().getTitle());
        binding.tvDescription.setText(bean.getData().getSynopsis());
        binding.tvInfo.setVisibility(View.GONE);

        pendingDownloadLink = "https://pinoymoviepedia.ru/links/" + downloadId + "/";
        rebuildDetailBeanList();

        if (bean.getData().getPoster() != null && !bean.getData().getPoster().isEmpty()) {
            image = bean.getData().getPoster();
        } else {
            this.image = Image;
        }

        try {
            Glide.with(this).asBitmap().load(image).into(binding.ivSmallimg);
            Glide.with(this).asBitmap().load(image).into(binding.ivBig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final class FetchResult {
        String downloadId;
        List<String> embedUrls = new ArrayList<>();
    }

    private static FetchResult fetchPageData(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .build();

        FetchResult result = new FetchResult();
        try (Response response = OK_HTTP_CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) return result;

            String html = response.body().string();

            Matcher downloadMatcher = DOWNLOAD_TABLE_PATTERN.matcher(html);
            if (downloadMatcher.find()) {
                result.downloadId = downloadMatcher.group(1);
            } else {
                Matcher linkMatcher = DOWNLOAD_PATTERN.matcher(html);
                if (linkMatcher.find()) {
                    result.downloadId = linkMatcher.group(1);
                }
            }

            Matcher embedMatcher = EMBED_IFRAME_PATTERN.matcher(html);
            while (embedMatcher.find()) {
                String u = embedMatcher.group(1);
                if (!result.embedUrls.contains(u)) {
                    result.embedUrls.add(u);
                }
            }

            if (result.embedUrls.isEmpty()) {
                Matcher generic = EMBED_GENERIC_PATTERN.matcher(html);
                while (generic.find()) {
                    String u = generic.group(1);
                    if (!result.embedUrls.contains(u)) {
                        result.embedUrls.add(u);
                    }
                }
            }
        }
        return result;
    }
}