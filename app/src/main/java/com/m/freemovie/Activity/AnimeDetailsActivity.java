package com.m.freemovie.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.m.freemovie.Utils.GlobalWindowUtils;
import com.m.freemovie.Utils.WindowUtils;
import com.m.freemovie.adapter.AnimeSeasonAdapter;
import com.m.freemovie.databinding.ActivityAnimeDetailsBinding;
import com.m.freemovie.mvp.Contract.AnimeDetailsContract;
import com.m.freemovie.mvp.Model.ClassBean.AniNeKoInfoBean;
import com.m.freemovie.mvp.Model.ClassBean.AnimeDetailsBean;
import com.m.freemovie.mvp.Model.ClassBean.AnimePaheDetailBean;
import com.m.freemovie.mvp.Model.ClassBean.AnimoDetailsBean;
import com.m.freemovie.mvp.Model.ClassBean.TagalogEpisodeBean;
import com.m.freemovie.mvp.Model.ClassBean.TagalogInfoBean;
import com.m.freemovie.mvp.Model.ClassBean.ZoRoDetailBean;
import com.m.freemovie.mvp.Presenter.AnimeDetailPresenter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class AnimeDetailsActivity extends AppCompatActivity implements AnimeDetailsContract.View {
    private ActivityAnimeDetailsBinding binding;
    private int apiPosition = 1;
    private AnimeDetailPresenter presenter;
    private String id;
    private String url = "";
    private String title;
    private AnimeSeasonAdapter animeSeasonAdapter;
    private List<AnimeDetailsBean> animeDetailsBeanList = new ArrayList<>();
    private String imageUrl;
    private String animeTitle;
    private String overView;
    private Random random;
    private KProgressHUD hud;
    private String TAG = "AnimeDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        new GlobalWindowUtils(this, false);
        binding = ActivityAnimeDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        apiPosition = getIntent().getIntExtra("apiPosition", 1);
        id = getIntent().getStringExtra("id");
        title = getIntent().getStringExtra("title");
        imageUrl = getIntent().getStringExtra("imageUrl");
        url = getIntent().getStringExtra("url");
        initRecycler();
        presenter = new AnimeDetailPresenter(this);
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait");
        if (isNetworkAvailable()) {
            new CountDownTimer(1500, 1000) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    initApi();
                }

            }.start();


        } else {
            Toast.makeText(getApplicationContext(), "Please check connection and try again", Toast.LENGTH_SHORT).show();
        }
        binding.ivBack.setOnClickListener(view -> onBackPressed());
    }

    private void initRecycler() {
        binding.rvSeasons.setVisibility(View.VISIBLE);
        animeSeasonAdapter = new AnimeSeasonAdapter(apiPosition);
        binding.rvSeasons.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSeasons.setAdapter(animeSeasonAdapter);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("MissingPermission")
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void initApi() {
        switch (apiPosition) {
            case 1:
                presenter.getZoroUrl(id);
                break;
            case 2:
                presenter.getDetailAnimePaHe(id);
                break;
            case 3:
            case 4:
                presenter.getListTv(id);
                break;
            case 5:
                presenter.getAniMoTvUrl(id);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (hud != null && hud.isShowing()) {
            hud.dismiss();
            hud = null;
        }
        super.onDestroy();
    }

    @Override
    public void showLoading() {
        try {
            hud.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void showError(String error) {
        Log.d("ErrorData", "val: " + error);
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
        finish();
    }


    @Override
    public void hideLoading() {
        try {
            if (hud != null && hud.isShowing()) {
                hud.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        new WindowUtils(this, false, false);
        if (animeSeasonAdapter != null) {
            animeSeasonAdapter.recount();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        new WindowUtils(this, false, false);
    }


    @Override
    public void getDetailData(AnimePaheDetailBean detailBean) {
        if (binding == null) return;
        if (detailBean != null && detailBean.getResults() != null) {
            this.imageUrl = detailBean.getResults().getPoster();
            this.animeTitle =  detailBean.getResults().getTitle();
            this.overView = detailBean.getResults().getSynopsis();
            Set<String> seenEpisodes = new HashSet<>();
            for (AnimePaheDetailBean.ResultsBean.EpisodeListBean data : detailBean.getResults().getEpisode_list()) {
                if (!data.getTitle().isEmpty()) {
                    String episode = data.getTitle();
                    if (!seenEpisodes.contains(episode)) {
                        seenEpisodes.add(episode);
                    }
                }
            }
            animeDetailsBeanList.add(new AnimeDetailsBean(id, imageUrl, animeTitle, seenEpisodes.size(), ""));
            animeSeasonAdapter.setNewData(animeDetailsBeanList);
            binding.tvDescription.setText(overView);
            binding.tvOriginal.setText(animeTitle);
            binding.tvTitle.setText(animeTitle);
            binding.language.setText("JP");
            random = new Random();
            int roll = random.nextInt(100000) + 1;
            binding.tvVote.setText(String.valueOf(roll));
            Double min = 0.0;
            Double max = 10.0;
            double x = (Math.random() * ((max - min) + 1)) + min;
            double xrounded = Math.round(x * 100.0) / 100.0;
            binding.tvRate.setText(String.valueOf(xrounded));
            try {
                Glide.with(this)
                        .asBitmap()
                        .load(imageUrl)
                        .into(binding.ivSmallimg);
                Glide.with(this)
                        .asBitmap()
                        .load(imageUrl)
                        .into(binding.ivBig);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getApplicationContext(),"Failed to fetch info",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void getTagalogEpisode(TagalogEpisodeBean tagalogEpisodeBean) {
        if (tagalogEpisodeBean != null && tagalogEpisodeBean.getResults() != null) {
            Set<String> seenEpisodes = new HashSet<>();
            for (TagalogEpisodeBean.ResultsBean data : tagalogEpisodeBean.getResults()) {
                if (!data.getEpisodes().isEmpty()) {
                    for (TagalogEpisodeBean.ResultsBean.EpisodesBean dataEpisode : data.getEpisodes()) {
                        String episode = dataEpisode.getEpisode();
                        if (!seenEpisodes.contains(episode)) {
                            seenEpisodes.add(episode);
                        }
                    }
                    animeDetailsBeanList.add(new AnimeDetailsBean(url, imageUrl, title, seenEpisodes.size(), ""));
                    animeSeasonAdapter.setNewData(animeDetailsBeanList);
                    binding.tvDescription.setText("N/A");
                    binding.tvOriginal.setText(title);
                    binding.tvTitle.setText(title);
                    binding.language.setText("TLD");
                    random = new Random();
                    int roll = random.nextInt(100000) + 1;
                    binding.tvVote.setText(String.valueOf(roll));
                    Double min = 0.0;
                    Double max = 10.0;
                    double x = (Math.random() * ((max - min) + 1)) + min;
                    double xrounded = Math.round(x * 100.0) / 100.0;
                    binding.tvRate.setText(String.valueOf(xrounded));
                    try {
                        Glide.with(this)
                                .asBitmap()
                                .load(imageUrl)
                                .into(binding.ivSmallimg);
                        Glide.with(this)
                                .asBitmap()
                                .load(imageUrl)
                                .into(binding.ivBig);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Episodes not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    @Override
    public void getInfoTagalog(TagalogInfoBean tagalogInfoBean) {
        if (binding == null) return;
        if (tagalogInfoBean != null && tagalogInfoBean.getResults() != null) {
            Set<String> seenEpisodes = new HashSet<>();
            this.imageUrl = tagalogInfoBean.getResults().getPoster();
            this.animeTitle = tagalogInfoBean.getResults().getTitle();
            this.overView = tagalogInfoBean.getResults().getSynopsis();
            for (TagalogInfoBean.ResultsBean.EpisodesBean data : tagalogInfoBean.getResults().getEpisodes()) {
                if (!tagalogInfoBean.getResults().getEpisodes().isEmpty()) {
                    String episode = data.getEpisode();
                    if (!seenEpisodes.contains(episode)) {
                        seenEpisodes.add(episode);
                    }
                }
            }
            animeDetailsBeanList.add(new AnimeDetailsBean(id, imageUrl, animeTitle, seenEpisodes.size(), ""));
            animeSeasonAdapter.setNewData(animeDetailsBeanList);
            binding.tvDescription.setText(overView);
            binding.tvOriginal.setText(animeTitle);
            binding.tvTitle.setText(animeTitle);
            binding.language.setText("TLD");
            random = new Random();
            int roll = random.nextInt(100000) + 1;
            binding.tvVote.setText(String.valueOf(roll));
            Double min = 0.0;
            Double max = 10.0;
            double x = (Math.random() * ((max - min) + 1)) + min;
            double xrounded = Math.round(x * 100.0) / 100.0;
            binding.tvRate.setText(String.valueOf(xrounded));
            try {
                Glide.with(this)
                        .asBitmap()
                        .load(imageUrl)
                        .into(binding.ivSmallimg);
                Glide.with(this)
                        .asBitmap()
                        .load(imageUrl)
                        .into(binding.ivBig);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void getZoroDetail(ZoRoDetailBean zoRoDetailBean) {
        if (binding == null) return;
        if (zoRoDetailBean != null && zoRoDetailBean.getEpisodes() != null) {
            Set<String> seenEpisodes = new HashSet<>();
            this.animeTitle = zoRoDetailBean.getTitle();
            this.overView = zoRoDetailBean.getDescription();
            for (ZoRoDetailBean.EpisodesBean data : zoRoDetailBean.getEpisodes()) {
                if (!zoRoDetailBean.getEpisodes().isEmpty()) {
                    String episode = data.getNumber();
                    if (!seenEpisodes.contains(episode)) {
                        seenEpisodes.add(episode);
                    }
                }
            }
            animeDetailsBeanList.add(new AnimeDetailsBean(id, imageUrl, animeTitle, seenEpisodes.size(), ""));
            animeSeasonAdapter.setNewData(animeDetailsBeanList);
            binding.tvDescription.setText(overView);
            binding.tvOriginal.setText(animeTitle);
            binding.tvTitle.setText(animeTitle);
            binding.language.setText("JP");
            random = new Random();
            int roll = random.nextInt(100000) + 1;
            binding.tvVote.setText(String.valueOf(roll));
            Double min = 0.0;
            Double max = 10.0;
            double x = (Math.random() * ((max - min) + 1)) + min;
            double xrounded = Math.round(x * 100.0) / 100.0;
            binding.tvRate.setText(String.valueOf(xrounded));
            try {
                Glide.with(this)
                        .asBitmap()
                        .load(imageUrl)
                        .into(binding.ivSmallimg);
                Glide.with(this)
                        .asBitmap()
                        .load(imageUrl)
                        .into(binding.ivBig);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void getAniNekoDetail(AniNeKoInfoBean aniNeKoInfoBean) {
        if (binding == null) return;
        if (aniNeKoInfoBean != null && aniNeKoInfoBean.getInfo() != null) {
            this.imageUrl = aniNeKoInfoBean.getInfo().getImage();
            this.animeTitle = aniNeKoInfoBean.getInfo().getTitle();
            this.overView = aniNeKoInfoBean.getInfo().getDescription();
            Set<String> seenEpisodes = new HashSet<>();
            for (AniNeKoInfoBean.InfoBean.EpisodesBean data : aniNeKoInfoBean.getInfo().getEpisodes()) {
                if (!data.getEpisodeTitle().isEmpty()) {
                    String episode = data.getEpisodeTitle();
                    if (!seenEpisodes.contains(episode)) {
                        seenEpisodes.add(episode);
                    }
                }
            }
            animeDetailsBeanList.add(new AnimeDetailsBean(id, imageUrl, animeTitle, seenEpisodes.size(), ""));
            animeSeasonAdapter.setNewData(animeDetailsBeanList);
            binding.tvDescription.setText(overView);
            binding.tvOriginal.setText(animeTitle);
            binding.tvTitle.setText(animeTitle);
            binding.language.setText("JP");
            random = new Random();
            int roll = random.nextInt(100000) + 1;
            binding.tvVote.setText(String.valueOf(roll));
            Double min = 0.0;
            Double max = 10.0;
            double x = (Math.random() * ((max - min) + 1)) + min;
            double xrounded = Math.round(x * 100.0) / 100.0;
            binding.tvRate.setText(String.valueOf(xrounded));
            try {
                Glide.with(this)
                        .asBitmap()
                        .load(imageUrl)
                        .into(binding.ivSmallimg);
                Glide.with(this)
                        .asBitmap()
                        .load(imageUrl)
                        .into(binding.ivBig);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getApplicationContext(),"Failed to fetch info",Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @Override
    public void getAniMoTvDetail(AnimoDetailsBean animoDetailsBean) {
        if (binding == null) return;
        if (animoDetailsBean != null && animoDetailsBean.getInfo() != null) {
            String tempImage = TextUtils.isEmpty(imageUrl)? animoDetailsBean.getInfo().getCover() : imageUrl;
            this.imageUrl = tempImage;
            this.animeTitle = animoDetailsBean.getInfo().getTitle();
            this.overView = animoDetailsBean.getInfo().getDescription();
            Set<String> seenEpisodes = new HashSet<>();
            for (AnimoDetailsBean.EpisodesBean.ResultsBean data : animoDetailsBean.getEpisodes().getResults()) {
                if (!data.getTitle().isEmpty()) {
                    String episode = data.getTitle();
                    if (!seenEpisodes.contains(episode)) {
                        seenEpisodes.add(episode);
                    }
                }
            }
            animeDetailsBeanList.add(new AnimeDetailsBean(id, imageUrl, animeTitle, seenEpisodes.size(), ""));
            animeSeasonAdapter.setNewData(animeDetailsBeanList);
            binding.tvDescription.setText(overView);
            binding.tvOriginal.setText(animeTitle);
            binding.tvTitle.setText(animeTitle);
            binding.language.setText("JP");
            random = new Random();
            int roll = random.nextInt(100000) + 1;
            binding.tvVote.setText(String.valueOf(roll));
            Double min = 0.0;
            Double max = 10.0;
            double x = (Math.random() * ((max - min) + 1)) + min;
            double xrounded = Math.round(x * 100.0) / 100.0;
            binding.tvRate.setText(String.valueOf(xrounded));
            try {
                Glide.with(this)
                        .asBitmap()
                        .load(imageUrl)
                        .into(binding.ivSmallimg);
                Glide.with(this)
                        .asBitmap()
                        .load(imageUrl)
                        .into(binding.ivBig);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getApplicationContext(),"Failed to fetch info",Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}