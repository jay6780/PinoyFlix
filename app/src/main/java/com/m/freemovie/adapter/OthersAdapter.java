package com.m.freemovie.adapter;


import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.m.freemovie.Activity.OthersDetailsActivity;
import com.m.freemovie.R;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.Utils.base.BaseViewHolder;
import com.m.freemovie.mvp.Model.ClassBean.OtherBean;

public class OthersAdapter extends BaseQuickAdapter<OtherBean.ResultsBean, BaseViewHolder> {
    private int type;
    public OthersAdapter() {
        super(R.layout.view_all_item);
    }

    @Override

    protected void convert(BaseViewHolder helper, OtherBean.ResultsBean item) {
        TextView tv_title  = helper.getView(R.id.tv_title);
        ImageView iv_thumb = helper.getView(R.id.iv_thumb);


        Glide.with(mContext)
                .asBitmap()
                .load(item.getImg())
                .placeholder(R.drawable.noimage)
                .into(iv_thumb);

        tv_title.setText(item.getTitle());

        helper.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, OthersDetailsActivity.class);
                intent.putExtra("title",item.getTitle());
                intent.putExtra("link",item.getLink());
                intent.putExtra("image",item.getImg());
                intent.putExtra("type",type);
                mContext.startActivity(intent);
            }
        });
    }

    public void setType(int genrePosition) {
        this.type = genrePosition;
        notifyDataSetChanged();
    }
}