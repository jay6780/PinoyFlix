package com.m.freemovie.adapter;


import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.m.freemovie.Activity.TagalogWebviewActivity;
import com.m.freemovie.R;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.Utils.base.BaseViewHolder;
import com.m.freemovie.mvp.Model.ClassBean.RevivalSeriesBean;

public class TvRevivalAdapter extends BaseQuickAdapter<RevivalSeriesBean.ResultsBean, BaseViewHolder> {
    boolean isMovie = false;
    public TvRevivalAdapter() {
        super(R.layout.view_all_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, RevivalSeriesBean.ResultsBean item) {
        TextView tv_title  = helper.getView(R.id.tv_title);
        ImageView iv_thumb = helper.getView(R.id.iv_thumb);


        Glide.with(mContext)
                .asBitmap()
                .load(item.getPoster())
                .placeholder(R.drawable.noimage)
                .into(iv_thumb);

        tv_title.setText(item.getTitle());

        helper.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, TagalogWebviewActivity.class);
                intent.putExtra("id",item.getLink());
                intent.putExtra("title",item.getTitle());
                intent.putExtra("image",item.getPoster());
                intent.putExtra("isMovie",isMovie);
                mContext.startActivity(intent);
            }

        });
    }

    public void isMovie(boolean isMovie) {
        this.isMovie = isMovie;
        notifyDataSetChanged();
    }
}