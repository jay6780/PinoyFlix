package com.m.freemovie.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.core.Controller;
import com.app.hubert.guide.listener.OnGuideChangedListener;
import com.app.hubert.guide.model.GuidePage;
import com.app.hubert.guide.model.HighLight;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.m.freemovie.R;
import com.m.freemovie.Retrofit.AppConstant;
import com.m.freemovie.Utils.DbHelper.BookmarkDbHelper;
import com.m.freemovie.Utils.DbHelper.PinoyWatchHistoryHelper;
import com.m.freemovie.Utils.LinearLayoutManagerWithSmoothScroller;
import com.m.freemovie.Utils.WindowUtils;
import com.m.freemovie.adapter.TagalogAnimeAdapter;
import com.m.freemovie.adapter.TagalogDetailAdapter;
import com.m.freemovie.databinding.ActivityTagalogWebviewBinding;
import com.m.freemovie.mvp.Contract.RevivalContractDetail;
import com.m.freemovie.mvp.Contract.RevivalContractMovies;
import com.m.freemovie.mvp.Contract.RevivalContractTrack;
import com.m.freemovie.mvp.Model.ClassBean.DetailBean;
import com.m.freemovie.mvp.Model.ClassBean.DetailDownloadBean;
import com.m.freemovie.mvp.Model.ClassBean.RevivalSeriesBean;
import com.m.freemovie.mvp.Model.ClassBean.TagalogDetailBean;
import com.m.freemovie.mvp.Model.ClassBean.TagalogInfoBean;
import com.m.freemovie.mvp.Presenter.RevivalInfoDetailPresenter;
import com.m.freemovie.mvp.Presenter.RevivalMoviesPresenter;
import com.m.freemovie.mvp.Presenter.RevivalTrackPresenter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TagalogWebviewActivity extends AppCompatActivity
        implements RevivalContractDetail.View, RevivalContractTrack.View,
        TagalogDetailAdapter.TagalogVideoPlayListerner, RevivalContractMovies.View, TagalogAnimeAdapter.TagalogMovieListener {
    private ActivityTagalogWebviewBinding binding;
    private String id,title,image;
    private TagalogDetailAdapter episodeAdapter;
    private List<TagalogDetailBean> episodeBeanList = new ArrayList<>();
    private RevivalInfoDetailPresenter revivalInfoDetailPresenter;
    private RevivalTrackPresenter revivalTrackPresenter;
    private RevivalMoviesPresenter moviesPresenter;
    private boolean finishing = true;
    private String videoUrl ="";
    private boolean isMovie;
    private boolean isError =  false;
    private PinoyWatchHistoryHelper dbHelper;
    private BookmarkDbHelper bookmarkDbHelper;
    private AdView adView;
    private RelativeLayout.LayoutParams params;
    private int page = 1;
    private TagalogAnimeAdapter tagalogAnimeAdapter;
    private List<RevivalSeriesBean.ResultsBean> movieList = new ArrayList<>();
    private boolean isNomore = false;
    private boolean isLoading = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTagalogWebviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        new WindowUtils(this, true, false);
        title = getIntent().getStringExtra("title");
        isMovie = getIntent().getBooleanExtra("isMovie", false);
        id = getIntent().getStringExtra("id");
        image = getIntent().getStringExtra("image");

//        Log.d("SeasonList","ids"+" videoId: "+id + " SeasonId: "+seasonId);
        revivalInfoDetailPresenter = new RevivalInfoDetailPresenter(this);
        revivalTrackPresenter = new RevivalTrackPresenter(this);
        bookmarkDbHelper = new BookmarkDbHelper(this);
        dbHelper = new PinoyWatchHistoryHelper(this);
        moviesPresenter = new RevivalMoviesPresenter(this);
        binding.expand.setOnClickListener(view -> rotateScreen());
        binding.llBookmark.setOnClickListener(view -> savedOption());
        setImageData(id);

        binding.swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isNetworkAvailable()) {
                    Toast.makeText(getApplicationContext(), "Please check internet and try again", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isMovie) {
                    page = 1;
                    movieList.clear();
                    tagalogAnimeAdapter.setNewData(movieList);
                    moviesPresenter.getMovieList(page);
                } else {
                    episodeBeanList.clear();
                    episodeAdapter.setNewData(new ArrayList<>());
                    revivalInfoDetailPresenter.getListTv(id);
                }
            }
        });
        binding.btnBackFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finishing) {
                    finish();
                } else {
                    defaultScreen();
                }
            }
        });

        if (isNetworkAvailable()) {
            isMovieVideo();
        } else {
            Toast.makeText(getApplicationContext(), "Please check internet and try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void savedOption() {
        savedBook();
    }

    @SuppressLint("MissingPermission")
    private void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView = new AdView(TagalogWebviewActivity.this);
        adView.setAdUnitId(getString(R.string.banner_adId));
        adView.setAdSize(AdSize.BANNER);
        binding.adTvSeries.removeAllViews();
        binding.adTvSeries.addView(adView);

        adView.loadAd(adRequest);
        if (adView != null) {
            adView.setAdListener(
                    new AdListener() {
                        @Override
                        public void onAdClicked() {
                        }

                        @Override
                        public void onAdClosed() {
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                            loadAdsFailed();
                        }

                        @Override
                        public void onAdImpression() {
                        }

                        @Override
                        public void onAdLoaded() {
                            loadAdsSuccess();
                        }

                        @Override
                        public void onAdOpened() {
                        }
                    });
        }
    }

    private void loadAdsFailed(){
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.BELOW,binding.episodeTxt.getId());
        if(isMovie){
            binding.rvMovies.setLayoutParams(params);
        }else{
            binding.rvSeason.setLayoutParams(params);

        }
        binding.adTvSeries.setVisibility(View.GONE);
        binding.llAds.setVisibility(View.GONE);
    }

    private void loadAdsSuccess(){
        binding.adTvSeries.setVisibility(View.VISIBLE);
        binding.llAds.setVisibility(View.VISIBLE);
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ABOVE,binding.llAds.getId());
        params.addRule(RelativeLayout.BELOW,binding.episodeTxt.getId());
        if(isMovie){
            binding.rvMovies.setLayoutParams(params);
        }else{
            binding.rvSeason.setLayoutParams(params);

        }


        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                loadAdsFailed();
            }

        }.start();
    }

    private void initGuide(String label) {
        NewbieGuide.with(this)
                .setLabel(label)
                .setOnGuideChangedListener(new OnGuideChangedListener() {
                    @Override
                    public void onShowed(Controller controller) {

                    }

                    @Override
                    public void onRemoved(Controller controller) {
                    }
                })
                .addGuidePage(GuidePage.newInstance()
                        .addHighLight(binding.llBookmark, HighLight.Shape.ROUND_RECTANGLE, 1)
                        .setLayoutRes(R.layout.bookmark_highlight)
                )
                .show();
    }
    private void isMovieVideo() {
        if(!AppConstant.isAddFree){
            loadAd();
        }else{
            loadAdsFailed();
        }
        binding.episodeTxt.setVisibility(View.VISIBLE);
        if(isMovie){
            if(isMovie){
                moviesPresenter.getMovieList(page);
                revivalTrackPresenter.getTrackUrl(id);
            }
            binding.episodeTxt.setText("Other movie");
            binding.rvSeason.setVisibility(View.GONE);
            binding.rvMovies.setVisibility(View.VISIBLE);
            initGuide("tagalog_movie");
            initMovieRecycler();
        }else{
            binding.rvSeason.setVisibility(View.VISIBLE);
            binding.rvMovies.setVisibility(View.GONE);
            initGuide("tagalog_series");
            revivalInfoDetailPresenter.getListTv(id);
            initEpisodeRecycler();
        }
    }
    private void initEpisodeRecycler() {
        binding.rvSeason.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(this));
        episodeAdapter = new TagalogDetailAdapter(this);
        binding.rvSeason.setAdapter(episodeAdapter);
    }

    private void initMovieRecycler() {
        binding.rvMovies.setLayoutManager(new LinearLayoutManager(this));
        tagalogAnimeAdapter = new TagalogAnimeAdapter(this);
        binding.rvMovies.setAdapter(tagalogAnimeAdapter);
        binding.rvMovies.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && layoutManager != null) {
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                    int totalItemCount = layoutManager.getItemCount();
                    if (!movieList.isEmpty()) {
                        if (lastVisibleItemPosition >= totalItemCount - 1) {
                            if (isNomore) {
                                return;
                            }
                            isLoading = true;
                            page++;
                            initApi();
                        }
                    }
                }
            }
        });
    }

    private void initApi() {
        moviesPresenter.getMovieList(page);
    }


    private void rotateScreen() {
        finishing = false;
        binding.expand.setVisibility(View.INVISIBLE);
        if(isMovie){
            binding.rvMovies.setVisibility(View.INVISIBLE);
        }else{
            binding.rvSeason.setVisibility(View.INVISIBLE);

        }
        binding.btnBackFinish.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        binding.rlWebview.setLayoutParams(params);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        binding.llBookmark.setVisibility(View.GONE);
        binding.swipe.setEnabled(false);
        if(!isMovie){
            loadAdsFailed();
        }
        new WindowUtils(this,true,false);
    }


    private void defaultScreen(){
        finishing = true;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding.expand.setVisibility(View.VISIBLE);
        if(isMovie){
            binding.rvMovies.setVisibility(View.VISIBLE);
        }else{
            binding.rvSeason.setVisibility(View.VISIBLE);

        }
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
    public void onBackPressed() {
        if(!finishing){
            defaultScreen();
        }else{
            super.onBackPressed();
            if(binding.swipe != null &&binding.swipe.isRefreshing()){
                binding.swipe.setRefreshing(false);
            }
            if (binding != null && binding.webView != null) {
                binding.webView.stopLoading();
                binding.webView.setWebChromeClient(null);
                binding.webView.setWebViewClient(null);
                binding.webView.destroy();
                binding.webView.clearCache(true);
                binding.webView.clearHistory();
                binding.webView.reload();
            }
            finish();
        }
    }


    private void savedBook() {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        String type;
        if(isMovie){
            type = "true";
        }else{
            type = "false";
        }
        DetailBean details = new DetailBean(id, timestamp, image, title,type);
        details.setVideoId(id);
        details.setTimeStamp(timestamp);
        bookmarkDbHelper.toggleBookmark(details, isMovie? 5:4);
        setImageData(id);
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
        binding.webView.setWebChromeClient(new CustomWebChromeClient(){});
        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setSupportZoom(false);
//        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
//        webSettings.setUserAgentString(userAgent);
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
                "https://abysscdn.com/",
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
        binding.swipe.setRefreshing(false);
    }


    @Override
    public void hideLoading() {
        try {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.swipe.setRefreshing(false);
                }
            }, 500);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void getMovies(RevivalSeriesBean revivalSeriesBean) {
        if(revivalSeriesBean !=null && revivalSeriesBean.getResults()!=null){
            isLoading = false;
            if(!revivalSeriesBean.getResults().isEmpty()) {
                tagalogAnimeAdapter.setNewData(revivalSeriesBean.getResults());
            }else{
                isNomore = true;
                Toast.makeText(getApplicationContext(),"No more data",Toast.LENGTH_SHORT).show();
            }
        }else{
            isNomore = true;
            Toast.makeText(getApplicationContext(),"No more data",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void getTrack(DetailDownloadBean tagalogInfoBean) {
        if(tagalogInfoBean !=null){
//            Log.d("VideoUrl","val: "+videoUrl);
            isError = false;
            videoUrl = tagalogInfoBean.getMetaframe();
            setupWebView(videoUrl);
        }

    }

    @Override
    public void getInfoTagalog(TagalogInfoBean tagalogInfoBean) {
        if (binding == null) return;
        if(tagalogInfoBean != null && tagalogInfoBean.getResults() != null) {
            Set<String> seenEpisodes = new HashSet<>();
            String lastWatchedEpisodeNumber = null;
            int lastWatchedPosition = -1;
            for (TagalogInfoBean.ResultsBean.EpisodesBean data : tagalogInfoBean.getResults().getEpisodes()) {
                if (!tagalogInfoBean.getResults().getEpisodes().isEmpty()) {
                    String episode = data.getEpisode();
                    if (!seenEpisodes.contains(episode)) {
                        seenEpisodes.add(episode);
                        TagalogDetailBean detailBean = new TagalogDetailBean(data.getEpisodeUrl(), episode, image);
                        boolean isWatched = dbHelper.isEpisodeWatched(id, episode);
                        detailBean.setWatched(isWatched);

                        episodeBeanList.add(detailBean);
                        detailBean.setVideoId(id);
                        if(isWatched){
                            lastWatchedPosition = episodeBeanList.size() - 1;
                            lastWatchedEpisodeNumber = episode;
                        }
                    }
                }
            }
            episodeAdapter.setNewData(episodeBeanList);

            if(!isMovie){
                binding.episodeTxt.setText(episodeBeanList.size() > 1? "Episode's" : "Episode");
            }
            if(lastWatchedPosition != -1 && lastWatchedEpisodeNumber != null){
                binding.rvSeason.smoothScrollToPosition(lastWatchedPosition);
                Toast.makeText(getApplicationContext(),
                        "Last Episode watched: Episode " + lastWatchedEpisodeNumber,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void getVideoUrl(String videoUrl) {
//        Log.e("VideoSelect","val: "+videoUrl);
        if(!videoUrl.isEmpty()){
            isError = false;
            revivalTrackPresenter.getTrackUrl(videoUrl);
        }
    }

    @Override
    public void getData(String videoUrl, String image, String title) {
        if(!videoUrl.isEmpty()){
            this.image = image;
            this.title = title;
            this.id = videoUrl;
            isError = false;
            revivalTrackPresenter.getTrackUrl(videoUrl);
            setImageData(videoUrl);
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
            try {
                if (url.contains(videoUrl)) {
                    return false;
                } else {
                    return true;
                }
            }catch (Exception e){
                e.printStackTrace();
                isError = true;
                binding.tvSelect.setVisibility(View.VISIBLE);
                binding.tvSelect.setText("video can't play");
                binding.webView.setVisibility(View.GONE);
                view.clearCache(true);
            }
            return false;
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            if(isError){
                binding.webView.setVisibility(View.GONE);
                return;
            }
            binding.webView.setVisibility(View.VISIBLE);
            binding.tvSelect.setVisibility(View.GONE);
            binding.expand.setVisibility(View.VISIBLE);
            super.onPageFinished(view, url);
        }

    }


}