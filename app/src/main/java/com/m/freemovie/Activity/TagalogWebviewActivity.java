package com.m.freemovie.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import com.m.freemovie.Utils.WindowUtils;
import com.m.freemovie.adapter.TagalogDetailAdapter;
import com.m.freemovie.databinding.ActivityTagalogWebviewBinding;
import com.m.freemovie.mvp.ClassBean.DetailDownloadBean;
import com.m.freemovie.mvp.ClassBean.TagalogDetailBean;
import com.m.freemovie.mvp.ClassBean.TagalogInfoBean;
import com.m.freemovie.mvp.Contract.RevivalContractDetail;
import com.m.freemovie.mvp.Contract.RevivalContractTrack;
import com.m.freemovie.mvp.Presenter.RevivalInfoDetailPresenter;
import com.m.freemovie.mvp.Presenter.RevivalTrackPresenter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TagalogWebviewActivity extends AppCompatActivity implements RevivalContractDetail.View, RevivalContractTrack.View, TagalogDetailAdapter.TagalogVideoPlayListerner {
    private ActivityTagalogWebviewBinding binding;
    private String id,title,image;
    private TagalogDetailAdapter episodeAdapter;
    private List<TagalogDetailBean> episodeBeanList = new ArrayList<>();
    private RevivalInfoDetailPresenter revivalInfoDetailPresenter;
    private RevivalTrackPresenter revivalTrackPresenter;
    private boolean finishing = true;
    private String videoUrl ="";
    private boolean isMovie;
    private boolean isError =  false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTagalogWebviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        new WindowUtils(this,true,false);
        title = getIntent().getStringExtra("title");
        isMovie = getIntent().getBooleanExtra("isMovie",false);
        id = getIntent().getStringExtra("id");
        image = getIntent().getStringExtra("image");
//        Log.d("SeasonList","ids"+" videoId: "+id + " SeasonId: "+seasonId);
        binding.titleName.setText(title);
        revivalInfoDetailPresenter = new RevivalInfoDetailPresenter(this);
        revivalTrackPresenter = new RevivalTrackPresenter(this);

        revivalInfoDetailPresenter.getListTv(id);
        binding.expand.setOnClickListener(view -> rotateScreen());


        binding.swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(isMovie){
                    revivalTrackPresenter.getTrackUrl(id);
                }else{
                    episodeBeanList.clear();
                    episodeAdapter.setNewData(new ArrayList<>());
                    revivalInfoDetailPresenter.getListTv(id);
                }
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

        isMovieVideo();


        binding.rvSeason.setLayoutManager(new LinearLayoutManager(this));
        episodeAdapter = new TagalogDetailAdapter(this);
        binding.rvSeason.setAdapter(episodeAdapter);

        episodeAdapter.setNewData(episodeBeanList);
    }

    private void isMovieVideo() {
        if(isMovie){
            revivalTrackPresenter.getTrackUrl(id);
            binding.tvEnjoy.setVisibility(View.VISIBLE);
            binding.rvSeason.setVisibility(View.GONE);
            binding.episodeTxt.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
            params.addRule(RelativeLayout.BELOW,binding.rlWebview.getId());
            params.setMargins(0,20,0,0);
            binding.tvEnjoy.setLayoutParams(params);
        }else{
            binding.tvEnjoy.setVisibility(View.GONE);
        }
        if(binding.tvEnjoy.getVisibility() == View.GONE){
            binding.rvSeason.setVisibility(View.VISIBLE);
            binding.episodeTxt.setVisibility(View.VISIBLE);
        }
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


    @Override
    public void getTrack(DetailDownloadBean tagalogInfoBean) {
        if(tagalogInfoBean !=null){
//            Log.d("VideoUrl","val: "+videoUrl);
            isError = false;
            videoUrl = tagalogInfoBean.getMetaframe();
            initStart();
        }

    }

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
    public void getInfoTagalog(TagalogInfoBean tagalogInfoBean) {
        if(tagalogInfoBean != null && tagalogInfoBean.getResults() != null) {
            Set<String> seenEpisodes = new HashSet<>();

            for (TagalogInfoBean.ResultsBean.EpisodesBean data : tagalogInfoBean.getResults().getEpisodes()) {
                if (!tagalogInfoBean.getResults().getEpisodes().isEmpty()) {
                    String episode = data.getEpisode();
                    if (!seenEpisodes.contains(episode)) {
                        seenEpisodes.add(episode);
                        episodeBeanList.add(new TagalogDetailBean(data.getEpisodeUrl(), episode, image));
                    }
                }
            }
            episodeAdapter.setNewData(episodeBeanList);
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
                    view.stopLoading();
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
            blockAds(view);
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