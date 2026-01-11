package com.m.freemovie.adapter;

import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.m.freemovie.R;
import com.m.freemovie.Utils.DbHelper.PinoyWatchHistoryHelper;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.Utils.base.BaseViewHolder;
import com.m.freemovie.mvp.ClassBean.DownloadPlayerListerner;
import com.m.freemovie.mvp.ClassBean.TagalogEpisode;

public class TagalogEpisodeAdapter extends BaseQuickAdapter<TagalogEpisode, BaseViewHolder> {

    private PinoyWatchHistoryHelper dbHelper;
    private VideoPlayListerner videoPlayListerner;
    private  int lastPosition = -1;
    private DownloadPlayerListerner downloadPlayerListerner;


    public interface VideoPlayListerner{
        void getVideoUrl(String videoUrl);
    }
    public TagalogEpisodeAdapter(VideoPlayListerner videoPlayListerner,DownloadPlayerListerner downloadPlayerListerner) {
        super(R.layout.episode_item);
        this.videoPlayListerner = videoPlayListerner;
        this.downloadPlayerListerner = downloadPlayerListerner;
    }

    @Override
    protected void convert(BaseViewHolder helper, TagalogEpisode item) {
        if (dbHelper == null) {
            dbHelper = new PinoyWatchHistoryHelper(mContext);
        }
        TextView tv_season = helper.getView(R.id.tv_season);
        ImageView iv_thumb = helper.getView(R.id.iv_thumb);
        RelativeLayout rl_select = helper.getView(R.id.rl_select);
        TextView tv_watched = helper.getView(R.id.tv_watched);
        if(lastPosition == (helper.getAdapterPosition())){
            rl_select.setBackgroundColor(Color.parseColor("#050E3C"));
        }else{
            rl_select.setBackgroundColor(Color.parseColor("#313647"));
        }

        tv_season.setText("Episode: " + item.getEpisode());

        Glide.with(mContext)
                .asBitmap()
                .placeholder(R.drawable.noimage)
                .load(item.getImageUrl())
                .into(iv_thumb);

        tv_watched.setVisibility(item.isWatched() ? View.VISIBLE : View.GONE);


        helper.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lastPosition == (helper.getAdapterPosition())){
                    lastPosition = -1;
                    videoPlayListerner.getVideoUrl("");
                }else{
                    lastPosition = (helper.getAdapterPosition());
                    showOption(item);
                }
            }
        });
    }

    private void showOption(TagalogEpisode item) {
        String[] colors = {"Watch", "Download"};
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               switch (which){
                   case 0:
                       dbHelper.markEpisodeAsWatched(item.getVideoUrl(), item.getEpisode());
                       item.setWatched(true);
                       videoPlayListerner.getVideoUrl(item.getVideoUrl());
                       notifyDataSetChanged();
                       break;
                   case 1:
                       downloadPlayerListerner.getDownloadData(item.getVideoUrl(), item.getEpisode());
                       break;
               }
            }

        });
        builder.show();
    }

}