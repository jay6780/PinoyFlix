package com.m.freemovie.adapter;


import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.m.freemovie.Activity.TagalogEpisodeActivity;
import com.m.freemovie.R;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.Utils.base.BaseViewHolder;
import com.m.freemovie.mvp.Model.ClassBean.TagalogBean;

public class TagalogSeriesAdapter extends BaseQuickAdapter<TagalogBean.ResultsBean, BaseViewHolder> {

    public TagalogSeriesAdapter() {
        super(R.layout.view_all_item);
    }


    @Override

    protected void convert(BaseViewHolder helper, TagalogBean.ResultsBean item) {
        TextView tv_title  = helper.getView(R.id.tv_title);
        ImageView iv_thumb = helper.getView(R.id.iv_thumb);


        Glide.with(mContext)
                .asBitmap()
                .load(item.getImage())
                .placeholder(R.drawable.noimage)
                .into(iv_thumb);

        tv_title.setText(item.getTitle());

        helper.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, TagalogEpisodeActivity.class);
                intent.putExtra("imageUrl",item.getImage());
                intent.putExtra("url",item.getLink().trim());
                intent.putExtra("title",item.getTitle());
                mContext.startActivity(intent);
            }
        });
    }

}