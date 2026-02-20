package com.m.freemovie.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.m.freemovie.R;
import com.m.freemovie.Utils.DbHelper.PinoyWatchHistoryHelper;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.Utils.base.BaseViewHolder;
import com.m.freemovie.mvp.Model.ClassBean.AnimePaheBeanList;

public class AnimePaheDetailAdapter extends BaseQuickAdapter<AnimePaheBeanList, BaseViewHolder> {

    private PinoyWatchHistoryHelper dbHelper;
    private EpisodeListener videoPlayListerner;
    private  int lastPosition = -1;
    public interface EpisodeListener{
        void getVideoUrl(String videoUrl,boolean isDownload,String episodeNum);
    }
    public AnimePaheDetailAdapter(EpisodeListener videoPlayListerner) {
        super(R.layout.episode_item);
        this.videoPlayListerner = videoPlayListerner;
    }

    @Override
    protected void convert(BaseViewHolder helper, AnimePaheBeanList item) {
        if (dbHelper == null) {
            dbHelper = new PinoyWatchHistoryHelper(mContext);
        }
        TextView tv_season = helper.getView(R.id.tv_season);
        ImageView iv_thumb = helper.getView(R.id.iv_thumb);
        ImageView iv_download = helper.getView(R.id.iv_download);
        iv_download.setVisibility(View.GONE);

        RelativeLayout rl_select = helper.getView(R.id.rl_select);
        TextView tv_watched = helper.getView(R.id.tv_watched);

        if(lastPosition == (helper.getAdapterPosition())){
            rl_select.setBackgroundColor(Color.parseColor("#050E3C"));
        }else{
            rl_select.setBackgroundColor(Color.parseColor("#313647"));
        }
        tv_watched.setVisibility(item.isWatched() ? View.VISIBLE : View.GONE);
        tv_season.setText("Episode: " + item.getEpisode());

        Glide.with(mContext)
                .asBitmap()
                .placeholder(R.drawable.noimage)
                .load(item.getImageUrl())
                .into(iv_thumb);



        iv_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        helper.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isNetworkAvailable()){
                    Toast.makeText(mContext,"Please check internet and try again",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(lastPosition == (helper.getAdapterPosition())){
                    lastPosition = -1;
                    videoPlayListerner.getVideoUrl("",false,"");
                }else{
                    showOption(item,helper);
                }
            }
        });
    }

    private void showOption(AnimePaheBeanList item,BaseViewHolder helper) {
        String[] colors = {"Watch", "Download"};
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        dbHelper.markEpisodeAsWatched(item.getVideoId(), item.getEpisode());
                        lastPosition = (helper.getAdapterPosition());
                        videoPlayListerner.getVideoUrl(item.getSession(),false,"");
                        item.setWatched(true);
                        notifyDataSetChanged();
                        break;
                    case 1:
                        videoPlayListerner.getVideoUrl(item.getSession(),true,item.getEpisode());
                        break;
                }
            }

        });
        builder.show();
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("MissingPermission")
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}