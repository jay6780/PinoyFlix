package com.m.freemovie.adapter;


import android.view.View;
import android.widget.TextView;

import com.m.freemovie.R;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.Utils.base.BaseViewHolder;
import com.m.freemovie.mvp.Model.ClassBean.AllSourceBean;
import com.m.freemovie.mvp.Model.ClassBean.AniMoTvEpisodeBean;

public class AllSourceAdapter extends BaseQuickAdapter<AllSourceBean, BaseViewHolder> {
    private SrcListener srcListener;
    public interface SrcListener{
        void getSrc(String videoUrl);
    }
    public AllSourceAdapter(SrcListener srcListener) {
        super(R.layout.quality_item);
        this.srcListener = srcListener;
    }

    @Override
    protected void convert(BaseViewHolder helper, AllSourceBean item) {
        TextView tv_quality  = helper.getView(R.id.tv_quality);
        tv_quality.setText(item.getQualityName());

        helper.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                srcListener.getSrc(item.getUrl());
            }
        });
    }

}