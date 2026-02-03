package com.m.freemovie.adapter;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.m.freemovie.Activity.AnimeDetailsActivity;
import com.m.freemovie.R;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.Utils.base.BaseViewHolder;
import com.m.freemovie.mvp.Model.ClassBean.RevivalSeriesBean;

public class AnimeMovieAdapter extends BaseQuickAdapter<RevivalSeriesBean.ResultsBean, BaseViewHolder> {
    boolean isMovie = false;
    public AnimeMovieAdapter() {
        super(R.layout.movie_item);
    }
    @Override
    protected void convert(BaseViewHolder helper, RevivalSeriesBean.ResultsBean item) {
        TextView tv_title  = helper.getView(R.id.tv_title);
        ImageView iv_thumb = helper.getView(R.id.iv_thumb);

        Glide.with(mContext)
                .asBitmap().
                load(item.getPoster())
                .placeholder(R.drawable.noimage)
                .into(iv_thumb);

        tv_title.setText(item.getTitle());

        helper.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view   ) {
                Intent intent = new Intent(mContext, AnimeDetailsActivity.class);
                intent.putExtra("id",item.getLink());
                intent.putExtra("apiPosition",4);
                mContext.startActivity(intent);
            }

        });
    }

    public void isMovie(boolean isMovie) {
        this.isMovie = isMovie;
        notifyDataSetChanged();
    }
}