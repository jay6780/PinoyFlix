package com.m.freemovie.adapter;


import android.view.View;
import android.widget.TextView;

import com.m.freemovie.R;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.Utils.base.BaseViewHolder;
import com.m.freemovie.mvp.Model.ClassBean.AniKoToWatchBean;
import com.m.freemovie.mvp.Model.ClassBean.AniNekoEpisodeBean;

public class AniKoToSourceAdapter extends BaseQuickAdapter<AniKoToWatchBean.EpisodesBean.ServersBean, BaseViewHolder> {
    private AniKoToSourceListener anikotoSourceListener;
    public interface AniKoToSourceListener{
        void getSrc(String videoUrl);
    }
    public AniKoToSourceAdapter(AniKoToSourceListener anikotoSourceListener) {
        super(R.layout.quality_item);
        this.anikotoSourceListener = anikotoSourceListener;
    }

    @Override
    protected void convert(BaseViewHolder helper, AniKoToWatchBean.EpisodesBean.ServersBean item) {
        TextView tv_quality  = helper.getView(R.id.tv_quality);
        tv_quality.setText(item.getName());

        helper.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anikotoSourceListener.getSrc(item.getVideoUrl());
            }
        });
    }

}