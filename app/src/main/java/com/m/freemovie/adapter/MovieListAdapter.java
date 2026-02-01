package com.m.freemovie.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.m.freemovie.Activity.Details_activity;
import com.m.freemovie.R;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.Utils.base.BaseViewHolder;
import com.m.freemovie.mvp.ClassBean.MovieBean;

public class MovieListAdapter extends BaseQuickAdapter<MovieBean.ResultsBean, BaseViewHolder> {
    private int apiPosition;
    private MovieIdListener movieIdListener;
    private  int lastPosition = -1;
    public interface MovieIdListener{
        void getMovieId(String id,String title,int position);
    }
    public MovieListAdapter(MovieIdListener movieIdListener) {
        super(R.layout.movie_watch_item);
        this.movieIdListener = movieIdListener;
    }
    @Override
    protected void convert(BaseViewHolder helper, MovieBean.ResultsBean item) {
        TextView tv_title  = helper.getView(R.id.tv_title);
        ImageView iv_thumb = helper.getView(R.id.iv_thumb);
        TextView tv_date = helper.getView(R.id.tv_date);
        TextView tv_popularity = helper.getView(R.id.tv_popularity);
        ConstraintLayout clSelect = helper.getView(R.id.Cl_select);
        String posterPath = "https://image.tmdb.org/t/p/w500/"+item.getPoster_path();

        if(lastPosition == (helper.getAdapterPosition())){
            clSelect.setBackgroundColor(Color.parseColor("#050E3C"));
        }else{
            clSelect.setBackgroundColor(Color.parseColor("#313647"));
        }


        Glide.with(mContext)
                .asBitmap().
                load(posterPath)
                .placeholder(R.drawable.noimage)
                .into(iv_thumb);

        tv_title.setText(item.getTitle());
        tv_date.setText("Release date: "+item.getRelease_date());
        tv_popularity.setText("Popularity: "+item.getPopularity());

        helper.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lastPosition == (helper.getAdapterPosition())){
                    lastPosition = -1;
                }else{
                    showDialog(item,helper);
                }
            }
        });
    }

    private void showDialog(MovieBean.ResultsBean item, BaseViewHolder helper) {

        String[] option = {"Player 1 ( Click again if not load )","Player 2" ,"View Details"};

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        movieIdListener.getMovieId(item.getId(),item.getTitle(),1);
                        lastPosition = (helper.getAdapterPosition());
                        notifyDataSetChanged();
                        break;
                    case 1:
                        movieIdListener.getMovieId(item.getId(),item.getTitle(),2);
                        lastPosition = (helper.getAdapterPosition());
                        notifyItemChanged(helper.getAdapterPosition());
                        notifyDataSetChanged();
                        break;
                    case 2:
                        Intent intent = new Intent(mContext, Details_activity.class);
                        intent.putExtra("id",item.getId());
                        intent.putExtra("position",1);
                        intent.putExtra("apiPosition",apiPosition);
                        mContext.startActivity(intent);
                        break;
                }
            }
        });
        builder.show();
    }

    public void setPosition(int type) {
        this.apiPosition = type;
        notifyDataSetChanged();
    }
}