package com.m.freemovie.adapter;


import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.m.freemovie.Activity.Details_activity;
import com.m.freemovie.R;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.Utils.base.BaseViewHolder;
import com.m.freemovie.mvp.ClassBean.MovieBean;

public class ViewAllAdapter extends BaseQuickAdapter<MovieBean.ResultsBean, BaseViewHolder> {
    int apiPosition;
    public ViewAllAdapter() {
        super(R.layout.view_all_item);
    }
    private int position ;

    @Override

    protected void convert(BaseViewHolder helper, MovieBean.ResultsBean item) {
        TextView tv_title  = helper.getView(R.id.tv_title);
        ImageView iv_thumb = helper.getView(R.id.iv_thumb);


        String posterPath = "https://image.tmdb.org/t/p/w500/"+item.getPoster_path();

        Glide.with(mContext)
                .asBitmap()
                .load(posterPath)
                .placeholder(R.drawable.noimage)
                .into(iv_thumb);

        tv_title.setText(position == 2? item.getOriginal_name() : item.getTitle());

        helper.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, Details_activity.class);
                intent.putExtra("id",item.getId());
                intent.putExtra("position",position);
                intent.putExtra("apiPosition",apiPosition);
                mContext.startActivity(intent);
            }
        });
    }

    public void isTvSeries(int position ) {
        this.position = position;
        notifyDataSetChanged();
    }

    public void setApiPosition(int type) {
        this.apiPosition = type;
        notifyDataSetChanged();
    }
}