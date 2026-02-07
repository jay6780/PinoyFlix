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
import com.m.freemovie.Activity.OthersDetailsActivity;
import com.m.freemovie.R;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.Utils.base.BaseViewHolder;
import com.m.freemovie.mvp.Model.ClassBean.OtherBean;

import java.util.Random;

public class OtherMovieListAdapter extends BaseQuickAdapter<OtherBean.ResultsBean, BaseViewHolder> {
    private MovieIdListener movieIdListener;
    private  int lastPosition = -1;
    private int type;
    public interface MovieIdListener{
        void getMovieId(String id,String title,String link);
    }
    public OtherMovieListAdapter(MovieIdListener movieIdListener,int type) {
        super(R.layout.movie_watch_item);
        this.movieIdListener = movieIdListener;
        this.type = type;
    }
    @Override
    protected void convert(BaseViewHolder helper,OtherBean.ResultsBean item) {
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
                load(item.getImg())
                .placeholder(R.drawable.noimage)
                .into(iv_thumb);

        tv_title.setText(item.getTitle());
        tv_date.setText("Release date: "+item.getYear());

        Random random = new Random();
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

    private void showDialog(OtherBean.ResultsBean item, BaseViewHolder helper) {

        String[] option = {"Watch" ,"View Details"};

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        movieIdListener.getMovieId(item.getLink(),item.getTitle(),item.getLink());
                        lastPosition = (helper.getAdapterPosition());
                        notifyDataSetChanged();
                        break;
                    case 1:
                        mContext.startActivity(new Intent(mContext, OthersDetailsActivity.class)
                                .putExtra("title",item.getTitle())
                                .putExtra("link",item.getLink())
                                .putExtra("image",item.getImg())
                                .putExtra("type",type));
                        break;
                }
            }
        });
        builder.show();
    }

}