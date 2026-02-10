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
import com.m.freemovie.Activity.AnimeDetailsActivity;
import com.m.freemovie.R;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.Utils.base.BaseViewHolder;
import com.m.freemovie.mvp.Model.ClassBean.RevivalSeriesBean;

import java.util.Random;

public class TagalogAnimeAdapter extends BaseQuickAdapter<RevivalSeriesBean.ResultsBean, BaseViewHolder> {
    private TagalogMovieListener movieIdListener;
    private  int lastPosition = -1;
    private Random random;
    public interface TagalogMovieListener{
        void getData(String videoUrl,String image,String title);
    }

    public TagalogAnimeAdapter(TagalogMovieListener movieIdListener) {
        super(R.layout.movie_watch_item);
        random = new Random();
        this.movieIdListener = movieIdListener;
    }
    @Override
    protected void convert(BaseViewHolder helper,RevivalSeriesBean.ResultsBean item) {
        TextView tv_title  = helper.getView(R.id.tv_title);
        ImageView iv_thumb = helper.getView(R.id.iv_thumb);
        TextView tv_date = helper.getView(R.id.tv_date);
        TextView tv_popularity = helper.getView(R.id.tv_popularity);
        ConstraintLayout clSelect = helper.getView(R.id.Cl_select);


        if(lastPosition == (helper.getAdapterPosition())){
            clSelect.setBackgroundColor(Color.parseColor("#050E3C"));
        }else{
            clSelect.setBackgroundColor(Color.parseColor("#313647"));
        }


        Glide.with(mContext)
                .asBitmap().
                load(item.getPoster())
                .placeholder(R.drawable.noimage)
                .into(iv_thumb);

        tv_title.setText(item.getTitle());
        tv_date.setText("Release date: "+item.getYear());
        int roll = random.nextInt(100000) + 1;
        String formattedRoll = String.format("%,d", roll);
        tv_popularity.setText("Popularity: " + formattedRoll);

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

    private void showDialog(RevivalSeriesBean.ResultsBean item, BaseViewHolder helper) {

        String[] option = {"Watch" ,"View Details"};

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        movieIdListener.getData(item.getLink(),item.getPoster(),item.getTitle());
                        lastPosition = (helper.getAdapterPosition());
                        notifyDataSetChanged();
                        break;
                    case 1:
                        Intent intent = new Intent(mContext, AnimeDetailsActivity.class);
                        intent.putExtra("id",item.getLink());
                        intent.putExtra("apiPosition",4);
                        mContext.startActivity(intent);
                        break;
                }
            }
        });
        builder.show();
    }
}