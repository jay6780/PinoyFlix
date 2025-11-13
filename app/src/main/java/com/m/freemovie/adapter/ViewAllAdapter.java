package com.m.freemovie.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.m.freemovie.Activity.Details_activity;
import com.m.freemovie.Activity.VideoWebviewActivity;
import com.m.freemovie.R;
import com.m.freemovie.mvp.ClassBean.MovieBean;

import java.util.List;

public class ViewAllAdapter extends RecyclerView.Adapter<ViewAllAdapter.ViewHolder> {
    private List<MovieBean.ResultsBean> movieBeanList;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        ImageView iv_thumb_image;
        public ViewHolder(View view) {
            super(view);
            tv_title = view.findViewById(R.id.tv_title);
            iv_thumb_image = view.findViewById(R.id.iv_thumb);

        }

    }

    public ViewAllAdapter(Context context, List<MovieBean.ResultsBean> movieBeanList) {
        this.context = context;
        this.movieBeanList = movieBeanList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.view_all_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        MovieBean.ResultsBean movieBean = movieBeanList.get(position);
        holder.tv_title.setText(movieBean.getTitle());

        String posterPath = "https://image.tmdb.org/t/p/w500/"+movieBean.getPoster_path();

        Glide.with(context)
                .asBitmap().
                load(posterPath)
                .centerCrop()
                .into(holder.iv_thumb_image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Details_activity.class);
                intent.putExtra("id",movieBean.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieBeanList.size();
    }
}