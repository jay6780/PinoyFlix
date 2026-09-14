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
import com.m.freemovie.mvp.Model.ClassBean.PaheLatestBean;

public class HotAdapter extends BaseQuickAdapter<PaheLatestBean.DataBean, BaseViewHolder> {
    public HotAdapter() {
        super(R.layout.movie_item);
    }
    @Override
    protected void convert(BaseViewHolder helper, PaheLatestBean.DataBean item) {
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
                intent.putExtra("apiPosition",2);
                mContext.startActivity(intent);
            }
        });
    }
}