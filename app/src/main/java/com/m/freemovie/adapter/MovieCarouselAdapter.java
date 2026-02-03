package com.m.freemovie.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.m.freemovie.Activity.ViewAllActivity;
import com.m.freemovie.R;
import com.m.freemovie.mvp.Model.ClassBean.MovieBean;
import com.zhpan.bannerview.BaseBannerAdapter;
import com.zhpan.bannerview.BaseViewHolder;

public class MovieCarouselAdapter extends BaseBannerAdapter<MovieBean.ResultsBean> {
    private Context context;
    public MovieCarouselAdapter(Context context){
        this.context = context;
    }

    @Override
    protected void bindData(BaseViewHolder<MovieBean.ResultsBean> holder, MovieBean.ResultsBean data, int position, int pageSize) {
        TextView tv_title = holder.findViewById(R.id.tv_title);
        TextView tv_description = holder.findViewById(R.id.tv_description);
        ImageView iv_thumb = holder.findViewById(R.id.iv_thumb);
        ImageView smallimg = holder.findViewById(R.id.smallimg);

        String posterPath = "https://image.tmdb.org/t/p/w500/"+data.getPoster_path();

        Glide.with(context)
                .asBitmap().
                load(posterPath)
                .placeholder(R.drawable.noimage)
                .into(iv_thumb);


        Glide.with(context)
                .asBitmap().
                load(posterPath)
                .placeholder(R.drawable.noimage)
                .into(smallimg);

        tv_title.setText(data.getTitle());
        tv_description.setText(data.getOverview());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewAllActivity.class);
                intent.putExtra("position",3);
                intent.putExtra("isTvSeries",false);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getLayoutId(int viewType) {

        return R.layout.item_movie_now;
    }
}