package com.m.freemovie.Activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.m.freemovie.R;
import com.m.freemovie.Utils.DbHelper.WatchHistoryDBHelper;
import com.m.freemovie.adapter.EpisodeAdapter;
import com.m.freemovie.databinding.ActivitySeasonListBinding;
import com.m.freemovie.mvp.ClassBean.EpisodeBean;

import java.util.ArrayList;
import java.util.List;
public class SeasonListActivity extends AppCompatActivity {
    private ActivitySeasonListBinding binding;
    private String title,id,thumbImage,seasonId;
    private int episodeCount,seasonNum;
    private EpisodeAdapter episodeAdapter;
    private List<EpisodeBean> episodeBeanList = new ArrayList<>();
    private WatchHistoryDBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySeasonListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        // Initialize database helper
        dbHelper = new WatchHistoryDBHelper(this);

        title = getIntent().getStringExtra("title");
        id = getIntent().getStringExtra("id");
        seasonId = getIntent().getStringExtra("seasonId");
        thumbImage = getIntent().getStringExtra("thumbImage");
        episodeCount = getIntent().getIntExtra("episodeCount",0);
        seasonNum = getIntent().getIntExtra("seasonNum",0);
        Log.d("SeasonList","ids"+" videoId: "+id + " SeasonId: "+seasonId);

        binding.title.setText(title);
        binding.btnBack5.setImageResource(R.mipmap.back_white);
        binding.btnBack5.setOnClickListener(view ->onBackPressed());
        binding.rvSeason.setLayoutManager(new LinearLayoutManager(this));
        episodeAdapter = new EpisodeAdapter();
        binding.rvSeason.setAdapter(episodeAdapter);

        for (int i = 1; i <= episodeCount; i++) {
            EpisodeBean episode = new EpisodeBean(i,thumbImage,seasonNum,id,title,seasonId);
            boolean isWatched = dbHelper.isEpisodeWatched(id, seasonNum, i);
            episode.setWatched(isWatched);
            episodeBeanList.add(episode);
        }
        episodeAdapter.setNewData(episodeBeanList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}