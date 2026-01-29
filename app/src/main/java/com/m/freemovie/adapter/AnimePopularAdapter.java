package com.m.freemovie.adapter;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.m.freemovie.Activity.AnimeDetailsActivity;
import com.m.freemovie.Activity.TagalogWebviewActivity;
import com.m.freemovie.R;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.Utils.base.BaseViewHolder;
import com.m.freemovie.mvp.ClassBean.RevivalSeriesBean;

public class AnimePopularAdapter extends BaseQuickAdapter<RevivalSeriesBean.ResultsBean, BaseViewHolder> {
    boolean isMovie = false;
    public AnimePopularAdapter() {
        super(R.layout.item_movie_now);
    }
    @Override
    protected void convert(BaseViewHolder helper, RevivalSeriesBean.ResultsBean item) {
        TextView tv_title = helper.getView(R.id.tv_title);
        TextView tv_description = helper.getView(R.id.tv_description);
        ImageView iv_thumb = helper.getView(R.id.iv_thumb);
        ImageView smallimg = helper.getView(R.id.smallimg);


        Glide.with(mContext)
                .asBitmap().
                load(item.getPoster())
                .placeholder(R.drawable.noimage)
                .into(iv_thumb);


        Glide.with(mContext)
                .asBitmap().
                load(item.getPoster())
                .placeholder(R.drawable.noimage)
                .into(smallimg);

        tv_title.setText(item.getTitle());
        tv_description.setText(item.getDescription());

        helper.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view   ) {
                Intent intent = new Intent(mContext, AnimeDetailsActivity.class);
                intent.putExtra("id",item.getLink());
                intent.putExtra("apiPosition",3);
                mContext.startActivity(intent);
            }

        });
    }

    public void isMovie(boolean isMovie) {
        this.isMovie = isMovie;
        notifyDataSetChanged();
    }
}