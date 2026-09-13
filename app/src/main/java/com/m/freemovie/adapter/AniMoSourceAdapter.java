package com.m.freemovie.adapter;


import android.view.View;
import android.widget.TextView;

import com.m.freemovie.R;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.Utils.base.BaseViewHolder;
import com.m.freemovie.mvp.Model.ClassBean.AniMoTvEpisodeBean;

public class AniMoSourceAdapter extends BaseQuickAdapter<AniMoTvEpisodeBean.ResultsBean, BaseViewHolder> {
    private SrcListener srcListener;
    public interface SrcListener{
        void getSrc(String videoUrl);
    }
    public AniMoSourceAdapter(SrcListener srcListener) {
        super(R.layout.quality_item);
        this.srcListener = srcListener;
    }

    @Override
    protected void convert(BaseViewHolder helper, AniMoTvEpisodeBean.ResultsBean item) {
        TextView tv_quality  = helper.getView(R.id.tv_quality);
        tv_quality.setText(item.getName());

        helper.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                srcListener.getSrc(item.getUrl());
            }
        });
    }

}