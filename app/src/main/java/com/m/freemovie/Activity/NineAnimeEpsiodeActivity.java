package com.m.freemovie.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.m.freemovie.R;
import com.m.freemovie.Utils.DbHelper.BookmarkDbHelper;
import com.m.freemovie.Utils.DbHelper.PinoyWatchHistoryHelper;
import com.m.freemovie.Utils.WindowUtils;
import com.m.freemovie.adapter.TagalogDetailAdapter;
import com.m.freemovie.databinding.ActivityNineAnimeEpsiodeBinding;
import com.m.freemovie.mvp.Contract.NineAnimeDetailContract;
import com.m.freemovie.mvp.Model.ClassBean.DetailBean;
import com.m.freemovie.mvp.Model.ClassBean.DownloadNineAnimeBean;
import com.m.freemovie.mvp.Model.ClassBean.NineAnimeEpisodeBean;
import com.m.freemovie.mvp.Model.ClassBean.TagalogDetailBean;
import com.m.freemovie.mvp.Presenter.NineAnimeDetailPresenter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NineAnimeEpsiodeActivity extends AppCompatActivity implements NineAnimeDetailContract.View,TagalogDetailAdapter.TagalogVideoPlayListerner {
    private ActivityNineAnimeEpsiodeBinding binding;
    private String videoId,title;
    private NineAnimeDetailPresenter presenter;
    private boolean finishing = true;
    private String videoUrl ="";
    private TagalogDetailAdapter episodeAdapter;
    private List<TagalogDetailBean> episodeBeanList = new ArrayList<>();
    private PinoyWatchHistoryHelper dbHelper;
    private String tempImage;
    private BookmarkDbHelper bookmarkDbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNineAnimeEpsiodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        new WindowUtils(this,true,false);
        title = getIntent().getStringExtra("title");
        videoId = getIntent().getStringExtra("videoId");
//        Log.d("SeasonList","ids"+" videoId: "+id + " SeasonId: "+seasonId);
        binding.titleName.setText(title);
        presenter = new NineAnimeDetailPresenter(this);
        dbHelper = new PinoyWatchHistoryHelper(this);
        bookmarkDbHelper = new BookmarkDbHelper(this);
        presenter.getDetails(videoId);
        binding.expand.setOnClickListener(view -> rotateScreen());
        setImageData(videoId);
        binding.llBookmark.setOnClickListener(view -> savedBook());
        binding.swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                episodeBeanList.clear();
                episodeAdapter.setNewData(new ArrayList<>());
                presenter.getDetails(videoId);
            }
        });
        binding.btnBackFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(finishing){
                    finish();
                }else{
                    defaultScreen();
                }
            }
        });

        binding.rvSeason.setLayoutManager(new LinearLayoutManager(this));
        episodeAdapter = new TagalogDetailAdapter(this);
        binding.rvSeason.setAdapter(episodeAdapter);

        episodeAdapter.setNewData(episodeBeanList);
    }


    private void rotateScreen() {
        finishing = false;
        binding.expand.setVisibility(View.INVISIBLE);
        binding.rvSeason.setVisibility(View.INVISIBLE);
        binding.btnBackFinish.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        binding.rlWebview.setLayoutParams(params);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        binding.llBookmark.setVisibility(View.GONE);
        binding.swipe.setEnabled(false);
        new WindowUtils(this,true,false);
    }


    private void defaultScreen(){
        finishing = true;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding.expand.setVisibility(View.VISIBLE);
        binding.rvSeason.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, dip2px(250));
        binding.rlWebview.setLayoutParams(params);
        binding.llBookmark.setVisibility(View.VISIBLE);
        binding.swipe.setEnabled(true);
        new WindowUtils(this,true,false);
    }
    public int dip2px(float dpValue) {
        final float scale = getResources(this).getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    public static Resources getResources(Context context) {
        return context.getResources();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (binding != null && binding.webView != null) {
            binding.webView.stopLoading();
            binding.webView.setWebChromeClient(null);
            binding.webView.setWebViewClient(null);
            binding.webView.destroy();
            binding.webView.clearCache(true);
            binding.webView.clearHistory();
            binding.webView.reload();
        }

        binding = null;
    }

    @Override
    protected void onPause() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onPause();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (binding != null && binding.webView != null) {
            try {
                binding.webView.clearCache(true);
                binding.webView.clearHistory();
                binding.webView.reload();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onBackPressed() {
        if(!finishing){
            defaultScreen();
        }else{
            super.onBackPressed();
            finish();
        }
    }


    private void savedBook() {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        DetailBean details = new DetailBean(videoId, timestamp, tempImage, title,"false");
        details.setVideoId(videoId);
        details.setTimeStamp(timestamp);
        bookmarkDbHelper.toggleBookmark(details, 6);
        setImageData(videoId);
    }
    private void setImageData(String videoId) {
        boolean isBookmarked = bookmarkDbHelper.isBookmarked(videoId);
        binding.ivHeart.setImageResource(!isBookmarked? R.mipmap.heart_no :R.mipmap.heart_yes);
    }
    @SuppressWarnings("deprecation")
    @SuppressLint("MissingPermission")
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void setupWebView(String videoUrl) {
        binding.webView.setWebViewClient(new CustomWebViewClient());
        binding.webView.setWebChromeClient(new CustomWebChromeClient());
        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setSupportZoom(false);
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
        webSettings.setUserAgentString(userAgent);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        binding.webView.requestFocusFromTouch();

        binding.webView.setFocusable(true);
        binding.webView.setFocusableInTouchMode(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            binding.webView.setWebContentsDebuggingEnabled(false);
        }

        String htmlContent = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "    <style>" +
                "        .video-player {" +
                "            position: fixed;" +
                "            top: 0;" +
                "            left: 0;" +
                "            width: 100%;" +
                "            height: 100%;" +
                "            border: none;" +
                "            object-fit: contain; /* Makes video fill while keeping aspect ratio */" +
                "            background-color: #000; /* Black background for letterboxing */" +
                "        }" +
                "    </style>" +
                "</head>" +
                "<body style=\"margin:0;padding:0;overflow:hidden;background:#000;\">" +
                "    <iframe src=\"" + videoUrl + "\"" +
                "            class=\"video-player\"" +
                "            allow=\"autoplay; encrypted-media; fullscreen\" " +
                "            allowfullscreen>" +
                "    </iframe>" +
                "</body>" +
                "</html>";

        binding.webView.loadDataWithBaseURL(
                "https://my.1anime.site/",
                htmlContent,
                "text/html",
                "UTF-8",
                null
        );

    }

    @Override
    public void showLoading() {
        binding.swipe.setRefreshing(true);
    }

    @Override
    public void showError(String error) {
        Toast.makeText(this,"Error fetching data: "+error,Toast.LENGTH_SHORT).show();
        if(binding.swipe !=null){
            binding.swipe.setRefreshing(false);
        }
    }

    @Override
    public void hideLoading() {
        if(binding.swipe !=null){
            binding.swipe.setRefreshing(false);
        }
    }


//    @Override
//    public void getTrack(DetailDownloadBean tagalogInfoBean) {
//        if(tagalogInfoBean !=null){
////            Log.d("VideoUrl","val: "+videoUrl);
//            videoUrl = tagalogInfoBean.getMetaframe();
//            initStart();
//        }
//
//    }

    private void initStart() {
        if (binding == null) return;

        if (!isNetworkAvailable()) {
            binding.webView.setVisibility(View.GONE);
            binding.tvSelect.setVisibility(View.VISIBLE);
            binding.tvSelect.setText("Please check your internet and try again");
        } else {
            binding.webView.setVisibility(View.VISIBLE);
            setupWebView(videoUrl);
        }
    }
    @Override
    public void getDetailData(NineAnimeEpisodeBean episodeBean) {
        if(episodeBean!=null && episodeBean.getResults() !=null) {
            for (NineAnimeEpisodeBean.ResultsBean.EpisodesBean data : episodeBean.getResults().getEpisodes()) {
                if (!episodeBean.getResults().getEpisodes().isEmpty()) {
                    TagalogDetailBean detailBean = new TagalogDetailBean(data.getLink(), data.getEpisode(),episodeBean.getResults().getImage());
                    boolean isWatched = dbHelper.isEpisodeWatched(data.getLink(), data.getEpisode());
                    tempImage = episodeBean.getResults().getImage();
                    detailBean.setWatched(isWatched);
                    episodeBeanList.add(detailBean);
                }else{
                    Toast.makeText(getApplicationContext(),"No episode available",Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
            episodeAdapter.setNewData(episodeBeanList);

        }
    }

    @Override
    public void getVideo(DownloadNineAnimeBean episodeBean) {
        if (episodeBean != null && episodeBean.getResults() !=null) {

            videoUrl = episodeBean.getResults().getIframeSrc();
//            Log.d("VideoUrl","val: "+videoUrl);
            initStart();
        }
    }

    @Override
    public void getVideoUrl(String videoUrl) {
        if(!videoUrl.isEmpty()){
//            Log.e("VideoSelect","val: "+videoUrl);
            presenter.getVideoUrl(videoUrl);
        }

    }


    private class CustomWebChromeClient extends WebChromeClient {
        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            return true;
        }
        @Override
        public Bitmap getDefaultVideoPoster() {
            return Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
        }
    }

    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            return handleUrlLoading(view, url);
        }
        private boolean handleUrlLoading(WebView view, String url) {
            if (url.contains(videoUrl)) {
                return false;
            } else {
                view.stopLoading();
                return true;
            }
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            binding.webView.setVisibility(View.VISIBLE);
            binding.tvSelect.setVisibility(View.GONE);
            binding.expand.setVisibility(View.VISIBLE);
            super.onPageFinished(view, url);
        }

    }

}