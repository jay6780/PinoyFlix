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
import com.m.freemovie.mvp.Model.ClassBean.AniNekoSearchBean;
import com.m.freemovie.mvp.Model.ClassBean.AnimePaheSearchBean;
import com.m.freemovie.mvp.Model.ClassBean.TagalogSearchBean;

public class TagalogSearchAdapter extends BaseQuickAdapter<AnimePaheSearchBean.DataBean, BaseViewHolder> {

    public TagalogSearchAdapter() {
        super(R.layout.view_all_item);
    }


    @Override

    protected void convert(BaseViewHolder helper, AnimePaheSearchBean.DataBean item) {
        TextView tv_title = helper.getView(R.id.tv_title);
        ImageView iv_thumb = helper.getView(R.id.iv_thumb);


        Glide.with(mContext)
                .asBitmap()
                .load(item.getAnime_image())
                .placeholder(R.drawable.noimage)
                .into(iv_thumb);

        tv_title.setText(item.getAnime_title());

        helper.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AnimeDetailsActivity.class);
                intent.putExtra("imageUrl", item.getAnime_image());
                intent.putExtra("id", item.getAnime_url());
                intent.putExtra("title", item.getAnime_title());
                intent.putExtra("apiPosition", 2);
                mContext.startActivity(intent);
            }
        });
    }

}