package com.m.freemovie.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.m.freemovie.Utils.WindowUtils;
import com.m.freemovie.adapter.AnimeSeasonAdapter;
import com.m.freemovie.databinding.ActivityAnimeDetailsBinding;
import com.m.freemovie.mvp.Model.ClassBean.AnimeDetailsBean;
import com.m.freemovie.mvp.Model.ClassBean.AnimePaheDetailBean;
import com.m.freemovie.mvp.Model.ClassBean.TagalogEpisodeBean;
import com.m.freemovie.mvp.Model.ClassBean.TagalogInfoBean;
import com.m.freemovie.mvp.Contract.AnimeDetailsContract;
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
    private String episodes;
    private String videoId;
    private String imageUrl;
    private String animeTitle;
    private String overView;
    private String airDate;
    private Random random;
    private KProgressHUD hud;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityAnimeDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        apiPosition = getIntent().getIntExtra("apiPosition",1);
        id = getIntent().getStringExtra("id");
        title = getIntent().getStringExtra("title");
        imageUrl = getIntent().getStringExtra("imageUrl");
        url = getIntent().getStringExtra("url");
        initRecycler();
        presenter = new AnimeDetailPresenter(this);
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait");
        initApi();
        binding.ivBack.setOnClickListener(view ->onBackPressed());
    }

    private void initRecycler() {
        binding.rvSeasons.setVisibility(View.VISIBLE);
        animeSeasonAdapter = new AnimeSeasonAdapter(apiPosition);
        binding.rvSeasons.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSeasons.setAdapter(animeSeasonAdapter);
    }


    private void initApi() {
        switch (apiPosition){
            case 1:
                url = "https://animepahe.si/anime/"+id;
                presenter.getDetailAnimePaHe(url);
                break;
            case 2:
                presenter.getUrl(url);
                break;
            case 3:
            case 4:
                presenter.getListTv(id);
                break;
        }
    }

    @Override
    public void showLoading() {
        hud.show();
    }

    @Override
    public void showError(String error) {
        Log.d("ErrorData", "val: " + error);
        Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
        finish();
    }


    @Override
    public void hideLoading() {
        if (hud != null && hud.isShowing()) {
            hud.dismiss();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        new WindowUtils(this,false,false);
        if(animeSeasonAdapter !=null){
            animeSeasonAdapter.recount();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        new WindowUtils(this,false,false);
    }
    private AnimePaheDetailBean detailBean;
    @Override
    public void getDetailData(AnimePaheDetailBean detailBean) {
        if(detailBean!=null && detailBean.getResults() !=null){
            try {
                this.videoId = detailBean.getResults().getId();
                this.imageUrl = detailBean.getResults().getPoster();
                this.animeTitle  = detailBean.getResults().getTitle();
                this.episodes = detailBean.getResults().getEpisodes();
                this.airDate = detailBean.getResults().getAired();
                animeDetailsBeanList.add(new AnimeDetailsBean(videoId,imageUrl,animeTitle,Integer.parseInt(episodes),airDate));
                this.detailBean = detailBean;
                detailsUis();
            }catch (Exception e){
                e.printStackTrace();
                this.episodes = "0";
                animeDetailsBeanList.add(new AnimeDetailsBean(videoId,imageUrl,animeTitle,Integer.parseInt(episodes),airDate));
                this.detailBean = detailBean;
                detailsUis();
            }
            animeSeasonAdapter.setNewData(animeDetailsBeanList);
        }
    }

    @Override
    public void getTagalogEpisode(TagalogEpisodeBean tagalogEpisodeBean) {
        if(tagalogEpisodeBean !=null && tagalogEpisodeBean.getResults() !=null){
            Set<String> seenEpisodes = new HashSet<>();
            for(TagalogEpisodeBean.ResultsBean data : tagalogEpisodeBean.getResults()){
                if(!data.getEpisodes().isEmpty()){
                    for(TagalogEpisodeBean.ResultsBean.EpisodesBean dataEpisode : data.getEpisodes()) {
                        String episode = dataEpisode.getEpisode();
                        if (!seenEpisodes.contains(episode)) {
                            seenEpisodes.add(episode);
                        }
                    }
                    animeDetailsBeanList.add(new AnimeDetailsBean(url,imageUrl,title,seenEpisodes.size(),""));
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
                    Glide.with(this)
                            .asBitmap()
                            .load(imageUrl)
                            .into(binding.ivSmallimg);
                    Glide.with(this)
                            .asBitmap()
                            .load(imageUrl)
                            .into(binding.ivBig);
                }else{
                    Toast.makeText(getApplicationContext(),"Episodes not found",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    @Override
    public void getInfoTagalog(TagalogInfoBean tagalogInfoBean) {
        if (binding == null) return;
        if(tagalogInfoBean != null && tagalogInfoBean.getResults() != null) {
            Set<String> seenEpisodes = new HashSet<>();
            this.imageUrl = tagalogInfoBean.getResults().getPoster();
            this.animeTitle  = tagalogInfoBean.getResults().getTitle();
            this.overView = tagalogInfoBean.getResults().getSynopsis();
            for (TagalogInfoBean.ResultsBean.EpisodesBean data : tagalogInfoBean.getResults().getEpisodes()) {
                if (!tagalogInfoBean.getResults().getEpisodes().isEmpty()) {
                    String episode = data.getEpisode();
                    if (!seenEpisodes.contains(episode)) {
                        seenEpisodes.add(episode);
                    }
                }
            }
            animeDetailsBeanList.add(new AnimeDetailsBean(id,imageUrl,animeTitle,seenEpisodes.size(),""));
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
            Glide.with(this)
                    .asBitmap()
                    .load(imageUrl)
                    .into(binding.ivSmallimg);
            Glide.with(this)
                    .asBitmap()
                    .load(imageUrl)
                    .into(binding.ivBig);
        }
    }

    private void detailsUis(){
        binding.tvDescription.setText(detailBean.getResults().getSynopsis());
        binding.tvOriginal.setText(detailBean.getResults().getTitle());
        binding.language.setText("JP");
        random = new Random();
        int roll = random.nextInt(100000) + 1;
        binding.tvVote.setText(String.valueOf(roll));
        binding.tvTitle.setText(detailBean.getResults().getTitle());
        Double min = 0.0;
        Double max = 10.0;
        double x = (Math.random() * ((max - min) + 1)) + min;
        double xrounded = Math.round(x * 100.0) / 100.0;
        binding.tvRate.setText(String.valueOf(xrounded));
        Glide.with(this)
                .asBitmap()
                .load(detailBean.getResults().getPoster())
                .into(binding.ivSmallimg);
        Glide.with(this)
                .asBitmap()
                .load(detailBean.getResults().getPoster())
                .into(binding.ivBig);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}