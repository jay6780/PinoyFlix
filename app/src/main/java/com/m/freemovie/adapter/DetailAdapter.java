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
import com.m.freemovie.mvp.ClassBean.DetailBean;

public class DetailAdapter extends BaseQuickAdapter<DetailBean, BaseViewHolder> {
    private boolean isTv = false;

    public DetailAdapter() {
        super(R.layout.view_all_item);
    }

    @Override

    protected void convert(BaseViewHolder helper, DetailBean item) {
        TextView tv_title  = helper.getView(R.id.tv_title);
        ImageView iv_thumb = helper.getView(R.id.iv_thumb);

        Glide.with(mContext)
                .asBitmap().
                load(item.getTempImage())
                .into(iv_thumb);

        tv_title.setText(item.getMovieName());

        helper.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, Details_activity.class);
                intent.putExtra("id",item.getVideoId());
                intent.putExtra("isTv",isTv);
                mContext.startActivity(intent);
            }
        });
    }

    public void isTv(boolean isTvSeries) {
        this.isTv = isTvSeries;
        notifyDataSetChanged();
    }
}