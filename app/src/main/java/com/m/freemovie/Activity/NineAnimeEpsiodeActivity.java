package com.m.freemovie.Activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

import com.m.freemovie.Utils.WindowUtils;
import com.m.freemovie.adapter.TagalogDetailAdapter;
import com.m.freemovie.databinding.ActivityNineAnimeEpsiodeBinding;
import com.m.freemovie.mvp.ClassBean.DownloadNineAnimeBean;
import com.m.freemovie.mvp.ClassBean.NineAnimeEpisodeBean;
import com.m.freemovie.mvp.ClassBean.TagalogDetailBean;
import com.m.freemovie.mvp.Contract.NineAnimeDetailContract;
import com.m.freemovie.mvp.Presenter.NineAnimeDetailPresenter;

import java.util.ArrayList;
import java.util.List;

public class NineAnimeEpsiodeActivity extends AppCompatActivity implements NineAnimeDetailContract.View,TagalogDetailAdapter.TagalogVideoPlayListerner {
    private ActivityNineAnimeEpsiodeBinding binding;
    private String videoId,title;
    private NineAnimeDetailPresenter presenter;
    private boolean finishing = true;
    private String videoUrl ="";
    private TagalogDetailAdapter episodeAdapter;
    private List<TagalogDetailBean> episodeBeanList = new ArrayList<>();
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
        presenter.getDetails(videoId);
        binding.expand.setOnClickListener(view -> rotateScreen());


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

        new WindowUtils(this,true,false);
    }


    private void defaultScreen(){
        finishing = true;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding.expand.setVisibility(View.VISIBLE);
        binding.rvSeason.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, dip2px(250));
        binding.rlWebview.setLayoutParams(params);
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
                "https://short.icu",
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
        Log.e("RetrofitError","val: "+error);
        new Handler().postDelayed(() -> {
            Toast.makeText(this,"Error fetching data: "+error,Toast.LENGTH_SHORT).show();
            binding.swipe.setRefreshing(false);
        }, 500);
    }

    @Override
    public void hideLoading() {
        new Handler().postDelayed(() -> {
            binding.swipe.setRefreshing(false);
        }, 500);
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
                    binding.rvSeason.setVisibility(View.VISIBLE);
                    episodeBeanList.add(new TagalogDetailBean(data.getLink(), data.getEpisode(),episodeBean.getResults().getImage()));
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
            blockAds(view);
            binding.webView.setVisibility(View.VISIBLE);
            binding.tvSelect.setVisibility(View.GONE);
            binding.expand.setVisibility(View.VISIBLE);
            super.onPageFinished(view, url);
        }

    }


    private void blockAds(WebView view) {
        String tags = view.getUrl();
        StringBuilder sb = new StringBuilder();
        sb.append("javascript: ");
        String[] allTag = tags.split(",");
        for (String tag : allTag) {
            String adTag = tag;
            if (adTag.trim().length() > 0) {
                adTag = adTag.trim();
                if (adTag.contains("#")) {
                    adTag = adTag.substring(adTag.indexOf("#") + 1);
                    sb.append("document.getElementById(\'").append(adTag).append("\').remove();");

                } else if (adTag.contains(".")) {
                    adTag = adTag.substring(adTag.indexOf(".") + 1);
                    sb.append("var esc=document.getElementsByClassName(\'").append(adTag).append("\');for (var i = esc.length - 1; i >= 0; i--){esc[i].remove();};");

                } else {
                    sb.append("var esc=document.getElementsByTagName(\'").append(adTag).append("\');for (var i = esc.length - 1; i >= 0; i--){esc[i].remove();};");
                }
            }
        }
    }

}