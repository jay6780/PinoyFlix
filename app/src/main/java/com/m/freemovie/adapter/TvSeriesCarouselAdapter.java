package com.m.freemovie.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.m.freemovie.Activity.ViewAllActivity;
import com.m.freemovie.R;
import com.m.freemovie.mvp.Model.ClassBean.TvSeriesBean;
import com.zhpan.bannerview.BaseBannerAdapter;
import com.zhpan.bannerview.BaseViewHolder;

public class TvSeriesCarouselAdapter extends BaseBannerAdapter<TvSeriesBean.ResultsBean> {
    private Context context;
    public TvSeriesCarouselAdapter(Context context){
        this.context = context;

    }
    @Override
    protected void bindData(BaseViewHolder<TvSeriesBean.ResultsBean> holder, TvSeriesBean.ResultsBean data, int position, int pageSize) {
        ImageView imageView = holder.findViewById(R.id.banner_image);
        String posterPath = "https://image.tmdb.org/t/p/w500/"+data.getPoster_path();
        Glide.with(context)
                .asBitmap()
                .load(posterPath)
                .thumbnail(0.1f)
                .placeholder(R.drawable.noimage)
                .into(imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewAllActivity.class);
                intent.putExtra("position",3);
                intent.putExtra("isTvSeries",true);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getLayoutId(int viewType) {

        return R.layout.item_net_image;
    }
}