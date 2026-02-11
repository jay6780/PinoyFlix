package com.m.freemovie.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.m.freemovie.adapter.OtherMovieListAdapter;
import com.m.freemovie.databinding.ActivityOtherWebview2Binding;
import com.m.freemovie.mvp.Contract.OtherDownloadContract;
import com.m.freemovie.mvp.Contract.OthersContract;
import com.m.freemovie.mvp.Model.ClassBean.OtherBean;
import com.m.freemovie.mvp.Model.ClassBean.OthersDlBean;
import com.m.freemovie.mvp.Presenter.OtherDownloadPresenter;
import com.m.freemovie.mvp.Presenter.OthersPresenter;

import java.util.ArrayList;
import java.util.List;

public class OtherWebviewActivity extends AppCompatActivity
        implements View.OnClickListener, OtherMovieListAdapter.MovieIdListener, OthersContract.View, OtherDownloadContract.View  {
    private ActivityOtherWebview2Binding binding;
    private int type;
    private int page = 1;
    private boolean isNomore = false;
    private boolean isLoading = false;
    private String title,link,image;
    private OtherMovieListAdapter otherMovieListAdapter;
    private List<OtherBean.ResultsBean> movieList = new ArrayList<>();
    private OthersPresenter presenter;
    private OtherDownloadPresenter downloadPresenter;
    private boolean finishing = true;
    private int lastScroll;
    private KProgressHUD hud;
    private String videoUrl;
    private boolean isRotate = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityOtherWebview2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        type = getIntent().getIntExtra("type",0);
        title = getIntent().getStringExtra("title");
        link = getIntent().getStringExtra("link");
        image = getIntent().getStringExtra("image");
        defaultScreen();
        presenter = new OthersPresenter(this);
        downloadPresenter = new OtherDownloadPresenter(this);
        initRecyclerMovie();
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait");
        hud.show();

        binding.llReset.setOnClickListener(this);
        binding.expand.setOnClickListener(this);
        binding.btnBackFinish.setOnClickListener(this);

        binding.swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                movieList.clear();
                if(otherMovieListAdapter!=null){
                    otherMovieListAdapter.setNewData(movieList);
                }
                if(binding.llReset.getVisibility() == View.VISIBLE){
                    binding.llReset.setVisibility(View.GONE);
                }
                initApi();
            }
        });
        initApi();
        downloadPresenter.getLink(link);
        binding.llReset.setVisibility(View.GONE);
    }

    private void reset(){
        binding.rvMovielist.scrollToPosition(0);
        binding.llReset.setVisibility(View.GONE);
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
                return true;
            }
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }


    private void initRecyclerMovie() {
        otherMovieListAdapter = new OtherMovieListAdapter(this,type);
        binding.rvMovielist.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMovielist.setAdapter(otherMovieListAdapter);

        binding.rvMovielist.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && layoutManager != null) {
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                    int totalItemCount = layoutManager.getItemCount();
                    lastScroll = lastVisibleItemPosition;
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


    private void initGuide() {
        NewbieGuide.with(this)
                .setLabel("OtherVideo")
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
    private void initApi() {
        if(!isNetworkAvailable()){
            Toast.makeText(getApplicationContext(),"Please check internet and try again",Toast.LENGTH_SHORT).show();
            return;
        }
            switch (type) {
                case 0:
                    presenter.getHorrorPage(page);
                    break;

                case 1:
                    presenter.getCrimePage(page);
                    break;

                case 2:
                    presenter.getRomancePage(page);
                    break;

                case 3:
                    presenter.getHistoryPage(page);
                    break;

                case 4:
                    presenter.getActionPage(page);
                    break;

                case 5:
                    presenter.getDramaPage(page);
                    break;

                case 6:
                    presenter.getMovieSpeakKhmerPage(page);
                    break;

                case 7:
                    presenter.getFantasyPage(page);
                    break;

                case 8:
                    presenter.getVivamaxPage(page);
                    break;

                case 9:
                    presenter.getTvMoviePage(page);
                    break;

                case 10:
                    presenter.getDocumentaryPage(page);
                    break;

                case 11:
                    presenter.getMysteryPage(page);
                    break;

                case 12:
                    presenter.getAdventurePage(page);
                    break;

                case 13:
                    presenter.getComedyPage(page);
                    break;

                case 14:
                    presenter.getScienceFictionPage(page);
                    break;

                case 15:
                    presenter.getFamilyPage(page);
                    break;

                case 16:
                    presenter.getAnimationPage(page);
                    break;

                case 17:
                    presenter.getSciFiFantasyPage(page);
                    break;
        }
    }


    @SuppressWarnings("deprecation")
    @SuppressLint("MissingPermission")
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back_finish:
                if(finishing){
                    finish();
                }else{
                    defaultScreen();
                }
                break;
            case R.id.expand:
                if(isRotate){
                    portraitFull();
                }else{
                    rotateScreen();
                }
                break;
            case R.id.ll_reset:
                reset();
                break;
        }

    }

    @Override
    public void getMovieId(String id, String title,String link) {
        binding.webView.clearCache(true);
        downloadPresenter.getLink(link);
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



    private void rotateScreen() {
        finishing = false;
        isRotate = true;
        binding.expand.setVisibility(View.VISIBLE);
        binding.rvMovielist.setVisibility(View.INVISIBLE);
        binding.btnBackFinish.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        binding.rlWebview.setLayoutParams(params);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        binding.swipe.setEnabled(false);
        binding.expand.setImageResource(R.mipmap.rotate_screen);

        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(dip2px(30), dip2px(30));
        params2.addRule(RelativeLayout.ALIGN_PARENT_END);
        params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params2.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
        binding.expand.setLayoutParams(params2);
        params2.setMargins(0,0,15,20);
        new WindowUtils(this,true,false);
    }

    private void portraitFull(){
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding.expand.setVisibility(View.VISIBLE);
        binding.rvMovielist.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        binding.rlWebview.setLayoutParams(params);
        binding.swipe.setEnabled(true);

        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(dip2px(30), dip2px(30));
        params2.addRule(RelativeLayout.ALIGN_PARENT_END);
        params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params2.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
        params2.setMargins(0,0,15,20);
        binding.expand.setLayoutParams(params2);
        isRotate = false;
        new WindowUtils(this,true,false);
    }

    private void defaultScreen(){
        isRotate = false;
        finishing = true;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding.expand.setVisibility(View.VISIBLE);
        binding.rvMovielist.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, dip2px(250));
        binding.rlWebview.setLayoutParams(params);
        binding.swipe.setEnabled(true);

        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(dip2px(25), dip2px(25));
        params2.addRule(RelativeLayout.ALIGN_PARENT_END);
        params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params2.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
        params2.setMargins(0,0,10,10);
        binding.expand.setLayoutParams(params2);
        binding.expand.setImageResource(R.mipmap.expand);
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.llReset.setVisibility(View.GONE);
        } else {
            if (lastScroll > 5) {
                binding.llReset.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public void getDownloadSuccess(OthersDlBean othersDlBean) {
        if(othersDlBean !=null && othersDlBean.getResults() !=null){
            this.videoUrl = othersDlBean.getResults().getPlayer().getVideoUrl();
            initStart();
        }
    }

    private void initStart() {
        if (!isNetworkAvailable()) {
            binding.webView.setVisibility(View.GONE);
        } else {
            binding.webView.setVisibility(View.VISIBLE);
            setupWebView(videoUrl);
        }
    }
    @Override
    public void getSciFiFantasy(OtherBean otherBean) {
        fetList(otherBean);
    }

    @Override
    public void getThaiDrama(OtherBean otherBean) {
        fetList(otherBean);
    }

    @Override
    public void getCrime(OtherBean otherBean) {
        fetList(otherBean);
    }

    @Override
    public void getRomance(OtherBean otherBean) {
        fetList(otherBean);
    }

    @Override
    public void getHistory(OtherBean otherBean) {
        fetList(otherBean);
    }

    @Override
    public void getWar(OtherBean otherBean) {
        fetList(otherBean);
    }

    @Override
    public void getAction(OtherBean otherBean) {
        fetList(otherBean);
    }

    @Override
    public void getDrama(OtherBean otherBean) {
        fetList(otherBean);
    }

    @Override
    public void getMovieSpeakKhmer(OtherBean otherBean) {
        fetList(otherBean);
    }

    @Override
    public void getThriller(OtherBean otherBean) {
        fetList(otherBean);
    }

    @Override
    public void getFantasy(OtherBean otherBean) {
        fetList(otherBean);
    }

    @Override
    public void getMusic(OtherBean otherBean) {
        fetList(otherBean);
    }

    @Override
    public void getWarPolitics(OtherBean otherBean) {
        fetList(otherBean);
    }

    @Override
    public void getVivamax(OtherBean otherBean) {
        fetList(otherBean);
    }

    @Override
    public void getTvMovie(OtherBean otherBean) {
        fetList(otherBean);
    }

    @Override
    public void getDocumentary(OtherBean otherBean) {
        fetList(otherBean);
    }

    @Override
    public void getKoreaDrama(OtherBean otherBean) {
        fetList(otherBean);
    }

    @Override
    public void getMystery(OtherBean otherBean) {
        fetList(otherBean);
    }

    @Override
    public void getAdventure(OtherBean otherBean) {
        fetList(otherBean);
    }

    @Override
    public void getComedy(OtherBean otherBean) {
        fetList(otherBean);
    }

    @Override
    public void getChineseDrama(OtherBean otherBean) {
        fetList(otherBean);
    }

    @Override
    public void getScienceFiction(OtherBean otherBean) {
        fetList(otherBean);
    }

    @Override
    public void getFamily(OtherBean otherBean) {
        fetList(otherBean);
    }

    @Override
    public void getTvShows(OtherBean otherBean) {
        fetList(otherBean);
    }

    @Override
    public void getErotic(OtherBean otherBean) {
        fetList(otherBean);
    }

    @Override
    public void getMovie(OtherBean otherBean) {
        fetList(otherBean);
    }

    @Override
    public void getAnimation(OtherBean otherBean) {
        fetList(otherBean);
    }

    @Override
    public void getHorror(OtherBean otherBean) {
        fetList(otherBean);
    }

    @Override
    public void getAllMovies(OtherBean otherBean) {
        fetList(otherBean);
    }

    private void fetList(OtherBean otherBean){
        if(otherBean!=null && otherBean.getResults() != null){
            isLoading = false;
            if(!otherBean.getResults().isEmpty()) {
                binding.rvMovielist.setVisibility(View.VISIBLE);
                for(OtherBean.ResultsBean data : otherBean.getResults()){
                    //if same title remove
                    if(!data.getTitle().contains(title)){
                        movieList.add(data);
                    }
                }
                if(!movieList.isEmpty()){
                    otherMovieListAdapter.setNewData(movieList);
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