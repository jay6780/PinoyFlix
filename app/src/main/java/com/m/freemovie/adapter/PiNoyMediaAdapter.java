package com.m.freemovie.adapter;


import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.m.freemovie.Activity.OtherWebviewActivity;
import com.m.freemovie.R;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.Utils.base.BaseViewHolder;
import com.m.freemovie.mvp.Model.ClassBean.PinoyRuDetailBean;

public class PiNoyMediaAdapter extends BaseQuickAdapter<PinoyRuDetailBean, BaseViewHolder> {
    private PinoyPlayerAdapter pinoyPlayerAdapter;
    private int type;

    public PiNoyMediaAdapter() {
        super(R.layout.player_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, PinoyRuDetailBean item) {
        RecyclerView rv_player = helper.getView(R.id.rv_player);
        TextView tv_download = helper.getView(R.id.tv_download);
        helper.addOnClickListener(R.id.tv_download);
        rv_player.setLayoutManager(new LinearLayoutManager(mContext));
        pinoyPlayerAdapter = new PinoyPlayerAdapter();
        pinoyPlayerAdapter.setNewData(item.getVideoUrls());
        rv_player.setAdapter(pinoyPlayerAdapter);

        pinoyPlayerAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.ll_select) {
                    mContext.startActivity(new Intent(mContext, OtherWebviewActivity.class)
                            .putExtra("videoUrl", pinoyPlayerAdapter.getData().get(position))
                            .putExtra("type", type));
                }
            }
        });
        tv_download.setText("Download");
    }

    public void setType(int type) {
        this.type = type;
        notifyDataSetChanged();
    }
}