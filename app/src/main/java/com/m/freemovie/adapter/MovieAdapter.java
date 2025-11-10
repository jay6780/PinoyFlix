package com.m.freemovie.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.m.freemovie.Activity.VideoWebviewActivity;
import com.m.freemovie.R;
import com.m.freemovie.mvp.ClassBean.MovieBean;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    private List<MovieBean.MovieList> movieBeanList;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        public ViewHolder(View view) {
            super(view);
            tv_title = view.findViewById(R.id.tv_title);

        }

    }

    public MovieAdapter(Context context,List<MovieBean.MovieList> movieBeanList) {
        this.context = context;
        this.movieBeanList = movieBeanList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.movie_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        MovieBean.MovieList movieBean = movieBeanList.get(position);
        holder.tv_title.setText(movieBean.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, VideoWebviewActivity.class);
                intent.putExtra("title",movieBean.getTitle());
                intent.putExtra("videoUrl",movieBean.getEmbed_url_tmdb());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieBeanList.size();
    }
}