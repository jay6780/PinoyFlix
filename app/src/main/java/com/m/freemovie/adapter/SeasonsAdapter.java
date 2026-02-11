package com.m.freemovie.adapter;

import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.m.freemovie.Activity.SeasonListActivity;
import com.m.freemovie.R;
import com.m.freemovie.Utils.DbHelper.WatchHistoryDBHelper;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.Utils.base.BaseViewHolder;
import com.m.freemovie.mvp.Model.ClassBean.DetailTvBean;

public class SeasonsAdapter extends BaseQuickAdapter<DetailTvBean.SeasonsBean, BaseViewHolder> {
    private WatchHistoryDBHelper dbHelper;
    private Cursor cursor;
    private int watchedCount = 0;
    private int totalEpisodes = 0;
    private int progressPercentage = 0;
    private DetailTvBean.SeasonsBean dataItem;

    public SeasonsAdapter() {
        super(R.layout.season_items);
    }

    @Override
    protected void convert(BaseViewHolder helper, DetailTvBean.SeasonsBean item) {
        TextView tv_season = helper.getView(R.id.tv_season);
        TextView tv_date = helper.getView(R.id.tv_date);
        TextView tv_episodeCount = helper.getView(R.id.tv_episodeCount);
        ProgressBar progressBar = helper.getView(R.id.progressBar);
        ImageView iv_thumb = helper.getView(R.id.iv_thumb);
        this.dataItem = item;

        tv_season.setText(item.getName());
        tv_date.setVisibility(item.getAir_date() == null ? View.GONE : View.VISIBLE);
        tv_date.setText("Release date: " + item.getAir_date());

        if (dbHelper == null) {
            dbHelper = new WatchHistoryDBHelper(mContext);
        }

        cursor = dbHelper.getWatchedEpisodes(item.getId());
        watchedCount = cursor.getCount();
        totalEpisodes = item.getEpisode_count();
        tv_episodeCount.setText("Episodes: " + watchedCount + "/" + totalEpisodes + " watched");

        if (totalEpisodes > 0) {
            progressPercentage = (watchedCount * 100) / totalEpisodes;
            progressBar.setProgress(progressPercentage);
        }
        String posterPath = "http://image.tmdb.org/t/p/w500/" + item.getPoster_path();
        Glide.with(mContext)
                .asBitmap()
                .placeholder(R.drawable.noimage)
                .load(posterPath)
                .into(iv_thumb);

        helper.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SeasonListActivity.class);
                intent.putExtra("title", item.getName());
                intent.putExtra("tvSeriesName", item.getTvSeriesName());
                intent.putExtra("seasonNum", item.getSeason_number());
                intent.putExtra("id", item.getVideoId());
                intent.putExtra("seasonId", item.getId());
                intent.putExtra("episodeCount", item.getEpisode_count());
                intent.putExtra("thumbImage", posterPath);
                mContext.startActivity(intent);
            }
        });
    }

    public void recount() {
        cursor = dbHelper.getWatchedEpisodes(dataItem.getId());
        watchedCount = cursor.getCount();
        notifyDataSetChanged();
    }
}