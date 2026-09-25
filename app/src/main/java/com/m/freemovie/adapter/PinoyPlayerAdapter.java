package com.m.freemovie.adapter;


import android.util.Log;
import android.widget.TextView;

import com.m.freemovie.R;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.Utils.base.BaseViewHolder;

public class PinoyPlayerAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public PinoyPlayerAdapter() {
        super(R.layout.quality_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        TextView tv_quality = helper.getView(R.id.tv_quality);
        int position = helper.getAdapterPosition();
        tv_quality.setText("Player: " + (position + 1));
//        Log.d("videoUrls: ", "val: " + item);
        helper.addOnClickListener(R.id.ll_select);
    }

}