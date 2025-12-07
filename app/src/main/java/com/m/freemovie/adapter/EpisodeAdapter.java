package com.m.freemovie.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.m.freemovie.R;
import com.m.freemovie.Utils.DbHelper.WatchHistoryDBHelper;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.Utils.base.BaseViewHolder;
import com.m.freemovie.mvp.ClassBean.EpisodeBean;

public class EpisodeAdapter extends BaseQuickAdapter<EpisodeBean, BaseViewHolder> {

    private WatchHistoryDBHelper dbHelper;
    private SourceListener sourceListener;
    private  int lastPosition = -1;
    public interface SourceListener{
        void getId(String id,int position,int seasonNum,int epNumber);
    }
    public EpisodeAdapter(SourceListener sourceListener) {
        super(R.layout.episode_item);
        this.sourceListener = sourceListener;
    }

    @Override
    protected void convert(BaseViewHolder helper, EpisodeBean item) {
        if (dbHelper == null) {
            dbHelper = new WatchHistoryDBHelper(mContext);
        }
        TextView tv_season = helper.getView(R.id.tv_season);
        ImageView iv_thumb = helper.getView(R.id.iv_thumb);
        TextView tv_watched = helper.getView(R.id.tv_watched);
        RelativeLayout rl_select = helper.getView(R.id.rl_select);

        if(lastPosition == (helper.getAdapterPosition())){
            rl_select.setBackgroundColor(Color.parseColor("#050E3C"));
        }else{
            rl_select.setBackgroundColor(Color.parseColor("#313647"));
        }

        tv_season.setText("Episode: " + item.getEpisodeNum());

        Glide.with(mContext)
                .asBitmap()
                .placeholder(R.drawable.noimage)
                .load(item.getThumbImage())
                .into(iv_thumb);

        tv_watched.setVisibility(item.isWatched() ? View.VISIBLE : View.GONE);


        helper.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lastPosition == (helper.getAdapterPosition())){
                    lastPosition = -1;
                }else{
                    lastPosition = (helper.getAdapterPosition());
                    showVideoOptions(item,mContext,helper);
                }
                notifyDataSetChanged();
            }
        });
    }

    private void showVideoOptions(EpisodeBean item, Context mContext, BaseViewHolder helper) {
        String[] videoPlayer = {"Player 1", "Player 2"};

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        TextView titleView = new TextView(mContext);
        titleView.setText("Select player");
        titleView.setTextColor(Color.BLACK);
        titleView.setPadding(40, 40, 40, 20);
        titleView.setTextSize(15);

        builder.setCustomTitle(titleView);

        builder.setItems(videoPlayer, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        sourceListener.getId(item.getId(),1,item.getSeasonNum(),item.getEpisodeNum());
                        break;
                    case 1:
                        sourceListener.getId(item.getId(),2,item.getSeasonNum(),item.getEpisodeNum());
                        break;
                }
                dbHelper.markEpisodeAsWatched(item.getId(), item.getTitle(),
                        item.getSeasonNum(), item.getEpisodeNum(),item.getSeasonId());

                item.setWatched(true);
                notifyItemChanged(helper.getAdapterPosition());
            }
        });
        builder.show();
    }
}