package com.m.freemovie.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.core.Controller;
import com.app.hubert.guide.listener.OnGuideChangedListener;
import com.app.hubert.guide.model.GuidePage;
import com.app.hubert.guide.model.HighLight;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.m.freemovie.R;
import com.m.freemovie.Utils.WindowUtils;
import com.m.freemovie.adapter.MovieListAdapter;
import com.m.freemovie.databinding.ActivityVideoWebviewBinding;
import com.m.freemovie.mvp.Model.ClassBean.MovieBean;
import com.m.freemovie.mvp.Contract.MovieWatchListContract;
import com.m.freemovie.mvp.Presenter.MovieWatchListPresenter;

import java.util.ArrayList;
import java.util.List;


public class VideoWebviewActivity extends AppCompatActivity implements MovieWatchListContract.View, MovieListAdapter.MovieIdListener {
    private String title;
    private String videoId;
    private KProgressHUD hud;
    private ActivityVideoWebviewBinding binding;
    private int videoPosition, epNumber;
    private String videoUrl;
    private boolean finishing = true;
    private int apiPosition;
    private int page = 1;
    private MovieWatchListPresenter movieWatchListPresenter;
    private MovieListAdapter movieListAdapter;
    private List<MovieBean.ResultsBean> movieList = new ArrayList<>();
    private boolean isNomore = false;
    private boolean isLoading = false;
    private int lastScroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoWebviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        defaultScreen();
        title = getIntent().getStringExtra("title");
        videoId = getIntent().getStringExtra("videoId");
        videoPosition = getIntent().getIntExtra("videoPosition", 0);
        epNumber = getIntent().getIntExtra("epNumber", 0);
        apiPosition = getIntent().getIntExtra("apiPosition",1);
//        Log.d("ApiPosition","val: "+apiPosition);
        movieWatchListPresenter = new MovieWatchListPresenter(this);
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait");
        hud.show();
        binding.expand.setOnClickListener(view -> rotateScreen());
        if (videoId == null) {
            Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        initRecyclerMovie();
//        Log.d("VideoUrl","value: "+videoUrl);

        binding.swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                movieList.clear();
                if(movieListAdapter!=null){
                    movieListAdapter.setNewData(movieList);
                }
                if(binding.llReset.getVisibility() == View.VISIBLE){
                    binding.llReset.setVisibility(View.GONE);
                }
                initApi();
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
        switch (videoPosition){
            case 1:
                videoUrl ="https://player.videasy.net/movie/"+videoId;
                break;
            case 2:
                videoUrl = "https://vidrock.net/movie/"+ videoId;
                break;
        }
        initApi();
        initStart();
        binding.llReset.setVisibility(View.GONE);
        binding.llReset.setOnClickListener(view -> reset());
    }

    private void reset(){
        binding.rvMovielist.scrollToPosition(0);
        binding.llReset.setVisibility(View.GONE);
    }

    private void initGuide() {
        NewbieGuide.with(this)
                .setLabel("MovieListReset")
                .setOnGuideChangedListener(new OnGuideChangedListener() {
                    @Override
                    public void onShowed(Controller controller) {

                    }

                    @Override
                    public void onRemoved(Controller controller) {
                    }
                })
                .addGuidePage(GuidePage.newInstance()
                        .addHighLight(binding.llReset, HighLight.Shape.ROUND_RECTANGLE, 1)
                        .setLayoutRes(R.layout.ll_reset_guide)
                )
                .show();
    }
    private void initRecyclerMovie() {
        movieListAdapter = new MovieListAdapter(this);
        binding.rvMovielist.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMovielist.setAdapter(movieListAdapter);
        movieListAdapter.setPosition(apiPosition);

        binding.rvMovielist.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && layoutManager != null) {
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                    lastScroll = lastVisibleItemPosition;
                    int totalItemCount = layoutManager.getItemCount();
                    if (lastVisibleItemPosition > 10) {
                        binding.llReset.setVisibility(View.VISIBLE);
                        initGuide();
                    } else if (lastVisibleItemPosition == 0) {
                        binding.llReset.setVisibility(View.GONE);
                    }
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
        if(!isNetworkAvailable()){
            Toast.makeText(getApplicationContext(),"Please check internet and try again",Toast.LENGTH_SHORT).show();
            return;
        }
        switch (apiPosition){
            case 1:
                movieWatchListPresenter.getViewAll(getString(R.string.key),page,1);
                break;
            case 2:
                movieWatchListPresenter.getViewAll(getString(R.string.key),page,2);
                break;
            case 3:
                movieWatchListPresenter.getViewAll(getString(R.string.key),page,3);
                break;
            case 4:
                movieWatchListPresenter.getViewAll(getString(R.string.key),page,4);
                break;
        }
    }

    private void rotateScreen() {
        finishing = false;
        binding.expand.setVisibility(View.INVISIBLE);
        binding.rvMovielist.setVisibility(View.INVISIBLE);
        binding.btnBackFinish.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        binding.rlWebview.setLayoutParams(params);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        binding.swipe.setEnabled(false);

        new WindowUtils(this,true,false);
    }


    private void defaultScreen(){
        finishing = true;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding.expand.setVisibility(View.VISIBLE);
        binding.rvMovielist.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, dip2px(250));
        binding.rlWebview.setLayoutParams(params);
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

    private void initStart() {
        if (binding == null) return;
        if (!isNetworkAvailable()) {
            binding.webView.setVisibility(View.GONE);
        } else {
            binding.webView.setVisibility(View.VISIBLE);
            setupWebView(videoUrl);
        }
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
        binding.webView.setWebChromeClient(new CustomWebChromeClient() {
        });
        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setSupportZoom(false);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            binding.webView.setWebContentsDebuggingEnabled(false);
        }
        binding.webView.loadUrl(videoUrl);

    }

    @Override
    public void showLoading() {
        binding.swipe.setRefreshing(true);
    }

    @Override
    public void showError(String error) {
        Toast.makeText(this,"Error fetching data: "+error,Toast.LENGTH_SHORT).show();
        if(binding.swipe.isRefreshing()){
            binding.swipe.setRefreshing(false);
        }
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
    public void getViewAllResponse(MovieBean movieBean) {
        if(movieBean!=null && movieBean.getResults() != null){
            isLoading = false;
            if(!movieBean.getResults().isEmpty()) {
                binding.rvMovielist.setVisibility(View.VISIBLE);
                for(MovieBean.ResultsBean data : movieBean.getResults()){
                    //if same title remove
                    if(!data.getTitle().contains(title)){
                        movieList.add(data);
                    }
                }
                if(!movieList.isEmpty()){
                    movieListAdapter.setNewData(movieList);
                }else{
                    isNomore = true;
                    Toast.makeText(getApplicationContext(),"No more movies",Toast.LENGTH_SHORT).show();
                }
            }else{
                binding.rvMovielist.setVisibility(View.GONE);
            }
        }else{
            isNomore = true;
            Toast.makeText(getApplicationContext(),"No more movies",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void getMovieId(String id,String title, int position) {
        if(!isNetworkAvailable()){
            Toast.makeText(getApplicationContext(),"Please check internet and try again",Toast.LENGTH_SHORT).show();
            return;
        }
        this.title = title;
        this.videoId = id;
        binding.webView.clearCache(true);
        switch (position){
            case 1:
                videoUrl ="https://player.videasy.net/movie/"+id;
                setupWebView(videoUrl);
                break;
            case 2:
                videoUrl = "https://vidrock.net/movie/"+ id;
                setupWebView(videoUrl);
                break;
        }

    }

    private class CustomWebChromeClient extends WebChromeClient {
        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            return true;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if(!isNetworkAvailable()){
                hud.dismiss();
                return;
            }
            if (newProgress == 100) {
                binding.tvSelect.setVisibility(View.GONE);
                if (hud != null && hud.isShowing()) {
                    hud.dismiss();
                    hud = null;
                }
            }
        }
    }

    private class CustomWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            return handleUrlLoading(view, url);
        }
        private boolean handleUrlLoading(WebView view, String url) {
            if (url.contains(videoUrl)) {
                return false;
            } else if (url.contains("dl.vidsrc.vip")) {
//                Log.d("VideOUrl", "value: " + url);
                String downloadUrl = "https://dl.vidsrc.vip/movie/" + videoId;
                Intent intent = new Intent(getApplicationContext(), DownloadWebview.class);
                intent.putExtra("DownloadUrl", downloadUrl);
                intent.putExtra("EpisodeNum", "");
                intent.putExtra("title", title);
                startActivity(intent);
                return true;
            } else {
                return true;
            }
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int orientation = getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            binding.llReset.setVisibility(View.GONE);
        }else{
            if(lastScroll > 5){
                binding.llReset.setVisibility(View.VISIBLE);
            }
        }
        if (videoPosition == 6 && binding != null && binding.webView != null) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (binding != null && binding.webView != null) {
                        if (hud != null && hud.isShowing()) {
                            hud.dismiss();
                            hud = null;
                        }
                        binding.webView.clearCache(true);
                        binding.webView.stopLoading();
                    }
                }
            }, 500);
        }
    }


    @Override
    public void onBackPressed() {
        if(!finishing){
            defaultScreen();
        }else{
            super.onBackPressed();
            finish();
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
        }
    }
}
