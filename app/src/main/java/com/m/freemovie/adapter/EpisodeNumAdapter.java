package com.m.freemovie.adapter;


import android.view.View;
import android.widget.TextView;

import com.m.freemovie.R;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.Utils.base.BaseViewHolder;
import com.m.freemovie.mvp.ClassBean.EpisodeBean;

public class EpisodeNumAdapter extends BaseQuickAdapter<EpisodeBean, BaseViewHolder> {

    public EpisodeNumAdapter() {
        super(R.layout.episode_item);
    }

    @Override

    protected void convert(BaseViewHolder helper, EpisodeBean item) {
        TextView tv_title  = helper.getView(R.id.tv_episode);


        tv_title.setText("Episode: "+item.getEpisodeNum());

        helper.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(mContext, Details_activity.class);
//                intent.putExtra("id",item.getVideoId());
//                mContext.startActivity(intent);
            }
        });
    }

}