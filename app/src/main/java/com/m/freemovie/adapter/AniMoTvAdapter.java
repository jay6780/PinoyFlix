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
import com.m.freemovie.mvp.Model.ClassBean.AniNekoBean;
import com.m.freemovie.mvp.Model.ClassBean.AnimoPageBean;

public class AniMoTvAdapter extends BaseQuickAdapter<AnimoPageBean.ResultsBean, BaseViewHolder> {
    public AniMoTvAdapter() {
        super(R.layout.movie_item);
    }
    @Override
    protected void convert(BaseViewHolder helper, AnimoPageBean.ResultsBean item) {
        TextView tv_title  = helper.getView(R.id.tv_title);
        ImageView iv_thumb = helper.getView(R.id.iv_thumb);

        Glide.with(mContext)
                .asBitmap().
                load(item.getImage())
                .placeholder(R.drawable.noimage)
                .into(iv_thumb);

        tv_title.setText(item.getTitle());

        helper.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AnimeDetailsActivity.class);
                intent.putExtra("id",item.getUrl());
                intent.putExtra("imageUrl",item.getImage());
                intent.putExtra("apiPosition",5);
                mContext.startActivity(intent);
            }
        });
    }
}