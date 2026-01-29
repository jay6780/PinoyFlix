package com.m.freemovie.adapter;

import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.m.freemovie.Activity.AnimePaheWebviewActivity;
import com.m.freemovie.Activity.TagalogEpisodeActivity;
import com.m.freemovie.Activity.TagalogWebviewActivity;
import com.m.freemovie.R;
import com.m.freemovie.Utils.DbHelper.PinoyWatchHistoryHelper;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.Utils.base.BaseViewHolder;
import com.m.freemovie.mvp.ClassBean.AnimeDetailsBean;

public class AnimeSeasonAdapter extends BaseQuickAdapter<AnimeDetailsBean, BaseViewHolder> {
    private int position;
    private PinoyWatchHistoryHelper dbHelper;
    private Cursor cursor;
    private int watchedCount = 0;
    private int totalEpisodes = 0;
    private int progressPercentage = 0;
    private AnimeDetailsBean dataItem;

    public AnimeSeasonAdapter(int position) {
        super(R.layout.anime_season_item);
        this.position = position;
    }

    @Override
    protected void convert(BaseViewHolder helper, AnimeDetailsBean item) {
        TextView tv_episodeCount = helper.getView(R.id.tv_episodeCount);
        ProgressBar progressBar = helper.getView(R.id.progressBar);
        ImageView iv_thumb = helper.getView(R.id.iv_thumb);
        this.dataItem = item;

        if (dbHelper == null) {
            dbHelper = new PinoyWatchHistoryHelper(mContext);
        }

        cursor = dbHelper.getWatchedEpisodes(item.getId());
        watchedCount = cursor.getCount();
        totalEpisodes = item.getEpisodes();
        tv_episodeCount.setText(totalEpisodes == 0? "Episodes: N/A":"Episodes: " + watchedCount + "/" + totalEpisodes + " watched");

        if (totalEpisodes > 0) {
            progressPercentage = (watchedCount * 100) / totalEpisodes;
            progressBar.setProgress(progressPercentage);
        }
        Glide.with(mContext)
                .asBitmap()
                .placeholder(R.drawable.noimage)
                .load(item.getImageUrl())
                .into(iv_thumb);

        helper.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                switch (position){
                    case 1:
                        intent = new Intent(mContext, AnimePaheWebviewActivity.class);
                        intent.putExtra("id",item.getId());
                        intent.putExtra("title",item.getTitle());
                        break;
                    case 2:
                        intent = new Intent(mContext, TagalogEpisodeActivity.class);
                        intent.putExtra("imageUrl",item.getImageUrl());
                        intent.putExtra("url",item.getId().trim());
                        intent.putExtra("title",item.getTitle());
                        break;
                    case 3:
                        intent = new Intent(mContext, TagalogWebviewActivity.class);
                        intent.putExtra("id",item.getId());
                        intent.putExtra("title",item.getTitle());
                        intent.putExtra("image",item.getImageUrl());
                        intent.putExtra("isMovie", position != 3);
                        break;
                    case 4:
                        intent = new Intent(mContext, TagalogWebviewActivity.class);
                        intent.putExtra("id",item.getId());
                        intent.putExtra("title",item.getTitle());
                        intent.putExtra("image",item.getImageUrl());
                        intent.putExtra("isMovie", position == 4);
                        break;

                }
                mContext.startActivity(intent);
            }
        });
    }

    public void recount() {
        if(dataItem != null && dataItem.getId() !=null){
            cursor = dbHelper.getWatchedEpisodes(dataItem.getId());
            watchedCount = cursor.getCount();
            notifyDataSetChanged();
        }
    }
}