package com.m.freemovie.adapter;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.m.freemovie.Activity.VideoWebviewActivity;
import com.m.freemovie.R;
import com.m.freemovie.Utils.DbHelper.WatchHistoryDBHelper;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.Utils.base.BaseViewHolder;
import com.m.freemovie.mvp.ClassBean.EpisodeBean;

public class EpisodeAdapter extends BaseQuickAdapter<EpisodeBean, BaseViewHolder> {

    private WatchHistoryDBHelper dbHelper;

    public EpisodeAdapter() {
        super(R.layout.episode_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, EpisodeBean item) {
        if (dbHelper == null) {
            dbHelper = new WatchHistoryDBHelper(mContext);
        }
        TextView tv_season = helper.getView(R.id.tv_season);
        ImageView iv_thumb = helper.getView(R.id.iv_thumb);
        TextView tv_watched = helper.getView(R.id.tv_watched);

        tv_season.setText("Episode: " + item.getEpisodeNum());

        Glide.with(mContext)
                .asBitmap()
                .placeholder(R.drawable.noimage)
                .load(item.getThumbImage())
                .into(iv_thumb);

        tv_watched.setVisibility(item.isWatched() ? View.VISIBLE : View.GONE);


        helper.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelper.markEpisodeAsWatched(item.getId(), item.getTitle(),
                        item.getSeasonNum(), item.getEpisodeNum(),item.getSeasonId());

                item.setWatched(true);
                notifyItemChanged(helper.getAdapterPosition());

                Intent intent = new Intent(mContext, VideoWebviewActivity.class);
                intent.putExtra("title", item.getTitle());
                intent.putExtra("seasonNum", item.getSeasonNum());
                intent.putExtra("videoPosition", 3);
                intent.putExtra("videoId", item.getId());
                intent.putExtra("epNumber", item.getEpisodeNum());
                mContext.startActivity(intent);
            }
        });
    }
}