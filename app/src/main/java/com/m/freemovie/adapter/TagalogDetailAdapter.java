package com.m.freemovie.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.m.freemovie.R;
import com.m.freemovie.Utils.DbHelper.WatchHistoryDBHelper;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.Utils.base.BaseViewHolder;
import com.m.freemovie.mvp.ClassBean.TagalogDetailBean;

public class TagalogDetailAdapter extends BaseQuickAdapter<TagalogDetailBean, BaseViewHolder> {

    private WatchHistoryDBHelper dbHelper;
    private TagalogVideoPlayListerner videoPlayListerner;
    private  int lastPosition = -1;
    public interface TagalogVideoPlayListerner{
        void getVideoUrl(String videoUrl);
    }
    public TagalogDetailAdapter(TagalogVideoPlayListerner videoPlayListerner) {
        super(R.layout.episode_item);
        this.videoPlayListerner = videoPlayListerner;
    }

    @Override
    protected void convert(BaseViewHolder helper, TagalogDetailBean item) {
        if (dbHelper == null) {
            dbHelper = new WatchHistoryDBHelper(mContext);
        }
        TextView tv_season = helper.getView(R.id.tv_season);
        ImageView iv_thumb = helper.getView(R.id.iv_thumb);
        RelativeLayout rl_select = helper.getView(R.id.rl_select);

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



        helper.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lastPosition == (helper.getAdapterPosition())){
                    lastPosition = -1;
                    videoPlayListerner.getVideoUrl("");
                }else{
                    lastPosition = (helper.getAdapterPosition());
                    videoPlayListerner.getVideoUrl(item.getEpisodeUrl());
                }
                notifyDataSetChanged();
            }
        });
    }

}