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
import com.m.freemovie.mvp.Model.ClassBean.AnimeItemBean;

public class ViewAllAnimeAdapter extends BaseQuickAdapter<AnimeItemBean, BaseViewHolder> {
    int position;
    public ViewAllAnimeAdapter(int position) {
        super(R.layout.view_all_item);
        this.position = position;
    }

    @Override

    protected void convert(BaseViewHolder helper, AnimeItemBean item) {
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
                Intent intent = null;
                switch (position){
                    case 1:
                        intent = new Intent(mContext, AnimeDetailsActivity.class);
                        intent.putExtra("apiPosition",1);
                        intent.putExtra("id",item.getId());
                        break;
                    case 2:
                        intent = new Intent(mContext, AnimeDetailsActivity.class);
                        intent.putExtra("imageUrl",item.getImage());
                        intent.putExtra("url",item.getId().trim());
                        intent.putExtra("title",item.getTitle());
                        intent.putExtra("apiPosition",2);
                        break;
                    case 3:
                        intent = new Intent(mContext, AnimeDetailsActivity.class);
                        intent.putExtra("apiPosition",3);
                        intent.putExtra("id",item.getId());
                        break;
                    case 4:
                        intent = new Intent(mContext, AnimeDetailsActivity.class);
                        intent.putExtra("apiPosition",4);
                        intent.putExtra("id",item.getId());
                        break;

                }
                mContext.startActivity(intent);
            }
        });
    }
}