package com.m.freemovie.adapter;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.m.freemovie.R;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.Utils.base.BaseViewHolder;
import com.m.freemovie.mvp.ClassBean.DetailTvBean;
import com.m.freemovie.mvp.ClassBean.EpisodeBean;

public class SeasonsAdapter extends BaseQuickAdapter<DetailTvBean.SeasonsBean, BaseViewHolder> {

    public SeasonsAdapter() {
        super(R.layout.season_items);
    }

    @Override

    protected void convert(BaseViewHolder helper, DetailTvBean.SeasonsBean item) {
        TextView tv_season  = helper.getView(R.id.tv_season);
        TextView tv_date  = helper.getView(R.id.tv_date);
        TextView tv_episodeCount  = helper.getView(R.id.tv_episodeCount);

        ImageView iv_thumb = helper.getView(R.id.iv_thumb);

        tv_season.setText(item.getName());
        tv_date.setText("Release date: "+item.getAir_date());
        tv_episodeCount.setText("Number of episode: "+item.getEpisode_count());
        String posterPath = "https://image.tmdb.org/t/p/w500/"+item.getPoster_path();
        Glide.with(mContext)
                .asBitmap()
                .placeholder(R.drawable.noimage)
                .load(posterPath)
                .into(iv_thumb);

        helper.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(mContext, Details_activity.class);
//                intent.putExtra("id",item.getVideoId());
//                mContext.startActivity(intent);
            }
        });
    }

}