package com.m.freemovie.adapter;

import android.widget.TextView;

import com.m.freemovie.R;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.Utils.base.BaseViewHolder;

public class RecentAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public RecentAdapter() {
        super(R.layout.recent_item);
    }
    @Override
    protected void convert(BaseViewHolder helper, String item) {
        TextView tv_title = helper.getView(R.id.tv_title);
        tv_title.setText(item);

        helper.addOnClickListener(R.id.ll_select);

    }
}