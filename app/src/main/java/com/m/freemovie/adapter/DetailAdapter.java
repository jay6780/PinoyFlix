package com.m.freemovie.adapter;


import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.m.freemovie.Activity.AnimePaheWebviewActivity;
import com.m.freemovie.Activity.Details_activity;
import com.m.freemovie.Activity.NineAnimeEpsiodeActivity;
import com.m.freemovie.Activity.TagalogEpisodeActivity;
import com.m.freemovie.Activity.TagalogWebviewActivity;
import com.m.freemovie.R;
import com.m.freemovie.Retrofit.AppConstant;
import com.m.freemovie.Utils.SPUtils;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.Utils.base.BaseViewHolder;
import com.m.freemovie.mvp.ClassBean.DetailBean;

public class DetailAdapter extends BaseQuickAdapter<DetailBean, BaseViewHolder> {
    private int position = 1;

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
                Intent intent = null;
                switch (position){
                    case 1:
                    case 2:
                        intent = new Intent(mContext, Details_activity.class);
                        intent.putExtra("id",item.getVideoId());
                        intent.putExtra("position",position);
                        break;
                    case 3:
                        intent = new Intent(mContext, TagalogEpisodeActivity.class);
                        intent.putExtra("imageUrl",item.getTempImage());
                        intent.putExtra("url",item.getVideoId());
                        intent.putExtra("title",item.getMovieName());
                        break;
                    case 4:
                    case 5:
                        intent = new Intent(mContext, TagalogWebviewActivity.class);
                        intent.putExtra("id",item.getVideoId());
                        intent.putExtra("title",item.getMovieName());
                        intent.putExtra("image",item.getTempImage());
                        intent.putExtra("isMovie",item.getIsMovie().equals("true")? true:false);
                        break;

                    case 6:
                        intent = new Intent(mContext, NineAnimeEpsiodeActivity.class);
                        intent.putExtra("videoId",item.getVideoId());
                        intent.putExtra("title",item.getMovieName());
                        break;
                    case 7:
                        intent = new Intent(mContext, AnimePaheWebviewActivity.class);
                        intent.putExtra("id",item.getVideoId());
                        intent.putExtra("title",item.getMovieName());
                        break;

                }
                mContext.startActivity(intent);
            }

        });
    }

    public void isTv(int position) {
        this.position = position;
        notifyDataSetChanged();
    }
}