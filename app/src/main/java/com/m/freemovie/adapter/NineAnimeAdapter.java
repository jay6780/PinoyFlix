package com.m.freemovie.adapter;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.m.freemovie.Activity.NineAnimeEpsiodeActivity;
import com.m.freemovie.R;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.Utils.base.BaseViewHolder;
import com.m.freemovie.mvp.ClassBean.NineAnimeBean;

public class NineAnimeAdapter extends BaseQuickAdapter<NineAnimeBean.ResultsBean, BaseViewHolder> {
    public NineAnimeAdapter() {
        super(R.layout.movie_item);
    }
    @Override
    protected void convert(BaseViewHolder helper, NineAnimeBean.ResultsBean item) {
        TextView tv_title  = helper.getView(R.id.tv_title);
        ImageView iv_thumb = helper.getView(R.id.iv_thumb);



        Glide.with(mContext)
                .asBitmap().
                load(item.getImage())
                .into(iv_thumb);

        tv_title.setText(item.getTitle());

        helper.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, NineAnimeEpsiodeActivity.class);
                intent.putExtra("videoId",item.getLink());
                intent.putExtra("title",item.getTitle());
                mContext.startActivity(intent);
            }
        });
    }

}