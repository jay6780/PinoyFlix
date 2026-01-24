package com.m.freemovie.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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
import com.m.freemovie.R;
import com.m.freemovie.Retrofit.AppConstant;
import com.m.freemovie.Utils.DbHelper.BookmarkDbHelper;
import com.m.freemovie.Utils.DbHelper.PinoyWatchHistoryHelper;
import com.m.freemovie.Utils.LinearLayoutManagerWithSmoothScroller;
import com.m.freemovie.Utils.SPUtils;
import com.m.freemovie.Utils.WindowUtils;
import com.m.freemovie.adapter.AnimePaheDetailAdapter;
import com.m.freemovie.adapter.DownloadAdapter;
import com.m.freemovie.adapter.QualityAdapter;
import com.m.freemovie.databinding.ActivityAnimePaheWebviewBinding;
import com.m.freemovie.mvp.ClassBean.AnimePaheBeanList;
import com.m.freemovie.mvp.ClassBean.AnimePaheDetailBean;
import com.m.freemovie.mvp.ClassBean.AnimePaheDownloadBean;
import com.m.freemovie.mvp.ClassBean.AnimePaheEpisodeBean;
import com.m.freemovie.mvp.ClassBean.DetailBean;
import com.m.freemovie.mvp.Contract.AnimePaheDetailContract;
import com.m.freemovie.mvp.Presenter.AnimePaheDetailPresenter;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AnimePaheWebviewActivity extends AppCompatActivity
        implements AnimePaheDetailContract.View, AnimePaheDetailAdapter.EpisodeListener, QualityAdapter.SrcListener, DownloadAdapter.DownListerner {
    private ActivityAnimePaheWebviewBinding binding;
    private String id,title;
    private AnimePaheDetailAdapter episodeAdapter;
    private List<AnimePaheBeanList> episodeBeanList = new ArrayList<>();
    private boolean finishing = true;
    private String videoUrl ="";
    private boolean isError =  false;
    private PinoyWatchHistoryHelper dbHelper;
    private BookmarkDbHelper bookmarkDbHelper;
    private String animeId ="";
    private String image = "";
    private AnimePaheDetailPresenter detailPresenter;
    private String url = "";
    private List<AnimePaheDownloadBean.ResultsBean.StreamingBean> streamingBeanList = new ArrayList<>();
    private List<AnimePaheDownloadBean.ResultsBean.DownloadBean> downloadBeanList = new ArrayList<>();
    private int page = 1;
    private boolean isNomore = false;
    private boolean isLoading = false;
    private boolean isInit = true;
    private boolean isDownload = false;
    private String episode = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnimePaheWebviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        initGuide();
        new WindowUtils(this,true,false);
        title = getIntent().getStringExtra("title");
//        Log.d("AnimeTitle","val: "+title);
        id = getIntent().getStringExtra("id");
//        Log.d("SeasonList","ids"+" videoId: "+id + " SeasonId: "+seasonId);
        binding.titleName.setText(title);
        bookmarkDbHelper = new BookmarkDbHelper(this);
        dbHelper = new PinoyWatchHistoryHelper(this);
        binding.expand.setOnClickListener(view -> rotateScreen());
        binding.llBookmark.setOnClickListener(view -> savedBook());
        detailPresenter = new AnimePaheDetailPresenter(this);
        url = "https://animepahe.si/anime/"+id;
        detailPresenter.getDetailQuery(url);
        setImageData(id);
        SPUtils.getInstance().put(AppConstant.isShow, false);



        if(isInit){
            binding.swipe.setRefreshing(true);
        }
        binding.swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                isNomore = false;
                isInit = true;
                SPUtils.getInstance().put(AppConstant.isShow, false);
                detailPresenter.getDetailQuery(url);
                episodeBeanList.clear();
                episodeAdapter.setNewData(new ArrayList<>());
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

        binding.rvSeason.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(this));
        episodeAdapter = new AnimePaheDetailAdapter(this);
        binding.rvSeason.setAdapter(episodeAdapter);
        episodeAdapter.setNewData(episodeBeanList);
    }


    private void initGuide() {
        NewbieGuide.with(this)
                .setLabel("animepahe_bookmark")
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



    private void loadmore() {
        detailPresenter.getEpisodeQuery(animeId,page);
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
        DetailBean details = new DetailBean(id, timestamp, image, title,"false");
        details.setVideoId(id);
        details.setTimeStamp(timestamp);
        bookmarkDbHelper.toggleBookmark(details,7);
        setImageData(id);
    }
    private void setImageData(String videoId) {
        boolean isBookmarked = bookmarkDbHelper.isBookmarked(videoId);
        binding.ivHeart.setImageResource(!isBookmarked? R.mipmap.heart_no :R.mipmap.heart_yes);
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
                null,
                htmlContent,
                "text/html",
                "UTF-8",
                null
        );
    }

    @Override
    public void showLoading() {
        if(!isInit){
            binding.swipe.setRefreshing(true);
        }
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



    int pageSize = 0;
    boolean isPaging = false;
    @Override
    public void getDetailData(AnimePaheDetailBean detailBean) {
        if(detailBean!=null && detailBean.getResults()!=null){
            animeId = detailBean.getResults().getId();
            image = detailBean.getResults().getPoster();
            detailPresenter.getEpisodeQuery(animeId,page);
            try {
                pageSize = Integer.parseInt(detailBean.getResults().getEpisodes());
            }catch (Exception e){
                e.printStackTrace();
                pageSize = 1;
                page = 1;
                isPaging = true;
                openScroll();
            }
        }
    }


    private void openScroll(){
        binding.rvSeason.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && layoutManager != null) {
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                    int totalItemCount = layoutManager.getItemCount();
                    if (!episodeBeanList.isEmpty()) {
                        if (lastVisibleItemPosition >= totalItemCount - 1) {
                            if (isNomore) {
                                return;
                            }
                            isLoading = true;
                            isInit = false;
                            page++;
                            loadmore();
                        }
                    }
                }
            }
        });

    }
    int lastWatchedPosition = -1;
    @Override
    public void getEpisodes(AnimePaheEpisodeBean episodeBean) {
        if (binding == null) return;
        if (episodeBean != null && episodeBean.getResults() != null) {
            isLoading = false;
            isInit = false;
            if (episodeBean.getResults().getData() != null) {
                for (AnimePaheEpisodeBean.ResultsBean.DataBean dataBean : episodeBean.getResults().getData()) {
                    AnimePaheBeanList detailBean = new AnimePaheBeanList(String.valueOf(dataBean.getId()), String.valueOf(dataBean.getEpisode()), dataBean.getSnapshot(), dataBean.getSession());
                    boolean isWatched = dbHelper.isEpisodeWatched(String.valueOf(dataBean.getId()), String.valueOf(dataBean.getEpisode()));
                    detailBean.setWatched(isWatched);
                    episodeBeanList.add(detailBean);
                    if(isWatched){
                        lastWatchedPosition = episodeBeanList.size() - 1;
                    }
                }
                episodeAdapter.setNewData(episodeBeanList);
                if(!isPaging){
                    if (episodeBeanList.size() < pageSize) {
                        page++;
                        isInit = true;
                        detailPresenter.getEpisodeQuery(animeId, page);
                    }
                    if (lastWatchedPosition != -1) {
                        if(!SPUtils.getInstance().getBoolean(AppConstant.isShow)){
                            Toast.makeText(getApplicationContext(), "Continuing from last watched episode", Toast.LENGTH_SHORT).show();
                            SPUtils.getInstance().put(AppConstant.isShow, true);
                        }
                        binding.rvSeason.postDelayed(() -> {
                            binding.rvSeason.smoothScrollToPosition(lastWatchedPosition);

                        }, 300);
                    }
                }

                binding.episodeTxt.setText(episodeBeanList.size() > 1 ? "Episode's" : "Episode");

            } else {
                isNomore = true;

            }
        }
    }
    @Override
    public void getTrack(AnimePaheDownloadBean downloadBean) {
        if(downloadBean !=null && downloadBean.getResults()!=null ){
            if(!downloadBean.getResults().getStreaming().isEmpty()){
                streamingBeanList.clear();
                downloadBeanList.clear();
                for(AnimePaheDownloadBean.ResultsBean.StreamingBean streamingBean : downloadBean.getResults().getStreaming()){
                    streamingBeanList.add(streamingBean);
                }
                for(AnimePaheDownloadBean.ResultsBean.DownloadBean downloadBean1 : downloadBean.getResults().getDownload()){
                    downloadBeanList.add(downloadBean1);
                }
                if(isDownload){
                    showDownloadList(downloadBeanList);
                }else{
                    ShowDialog(streamingBeanList);
                }

            }
        }
    }

    private DialogPlus dialog,dldialog;
    private void ShowDialog(List<AnimePaheDownloadBean.ResultsBean.StreamingBean> streamingBeanList){
        dialog = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(R.layout.dialog_select_quality))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.CENTER)
                .setCancelable(true)
                .setPadding(10,10,10,10)
                .create();

        View dialogView = dialog.getHolderView();
        RecyclerView recyclerView = dialogView.findViewById(R.id.rv_quality);
        QualityAdapter adapter = new QualityAdapter(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setNewData(streamingBeanList);

        dialog.show();
    }

    private void showDownloadList(List<AnimePaheDownloadBean.ResultsBean.DownloadBean> downloadBeanList){
        dldialog = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(R.layout.dialog_select_quality))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.CENTER)
                .setCancelable(true)
                .setPadding(10,10,10,10)
                .create();

        View dialogView = dldialog.getHolderView();
        RecyclerView recyclerView = dialogView.findViewById(R.id.rv_quality);
        DownloadAdapter adapter = new DownloadAdapter(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setNewData(downloadBeanList);

        dldialog.show();
    }
    @Override
    public void getSrc(String videoUrl) {
        this.videoUrl = videoUrl;
        if(dialog !=null){
            dialog.dismiss();
        }
        initStart();
    }
    @Override
    public void getDownloadLink(String videoUrl) {
        if(dldialog !=null){
            isDownload = false;
            dldialog.dismiss();
        }
        Intent intent  = new Intent(getApplicationContext(), DownloadWebview.class);
        intent.putExtra("DownloadUrl", videoUrl);
        intent.putExtra("title", title);
        intent.putExtra("EpisodeNum",episode);
        startActivity(intent);
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
    public void getVideoUrl(String videoUrl,boolean isDownload,String episode) {
        this.isDownload = isDownload;
        this.episode = episode;
        if(!videoUrl.isEmpty()){
            isError = false;
            if(!streamingBeanList.isEmpty()){
                streamingBeanList.clear();
            }
            String url = "https://animepahe.si/play/"+animeId+"/"+videoUrl;
            detailPresenter.getTrackQuery(url);
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