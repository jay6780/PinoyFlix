package com.m.freemovie.adapter;


import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.m.freemovie.Activity.VideoWebviewActivity;
import com.m.freemovie.R;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.Utils.base.BaseViewHolder;
import com.m.freemovie.mvp.ClassBean.EpisodeBean;

public class EpisodeAdapter extends BaseQuickAdapter<EpisodeBean, BaseViewHolder> {

    public EpisodeAdapter() {
        super(R.layout.episode_item);
    }

    @Override

    protected void convert(BaseViewHolder helper, EpisodeBean item) {
        TextView tv_season  = helper.getView(R.id.tv_season);

        ImageView iv_thumb = helper.getView(R.id.iv_thumb);

        tv_season.setText("Episode: "+item.getEpisodeNum());
        Glide.with(mContext)
                .asBitmap()
                .placeholder(R.drawable.noimage)
                .load(item.getThumbImage())
                .into(iv_thumb);

        helper.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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