package com.m.freemovie.adapter;


import android.widget.ImageView;
import android.widget.TextView;

import com.m.freemovie.R;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.Utils.base.BaseViewHolder;
import com.m.freemovie.mvp.Model.ClassBean.OptionBean;

public class OptionAdapter extends BaseQuickAdapter<OptionBean, BaseViewHolder> {
    public OptionAdapter() {
        super(R.layout.item_options);
    }

    @Override

    protected void convert(BaseViewHolder helper, OptionBean item) {
        helper.addOnClickListener(R.id.ll_file);
        ImageView image = helper.getView(R.id.image);
        TextView name = helper.getView(R.id.name);

        if(item.getName().contains("file")){
            image.setImageResource(R.mipmap.media_white);
            name.setText("Download videos");
        }else if(item.getName().contains("guide")){
            image.setImageResource(R.mipmap.reset_guide);
            name.setText("Reset guide");
        }
    }

}