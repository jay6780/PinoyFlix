package com.m.freemovie.adapter;


import android.view.View;
import android.widget.TextView;

import com.m.freemovie.R;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.Utils.base.BaseViewHolder;
import com.m.freemovie.mvp.Model.ClassBean.AnimePaheDownloadBean;

public class DownloadAdapter extends BaseQuickAdapter<AnimePaheDownloadBean.ResultsBean.DownloadBean, BaseViewHolder> {
    private DownListerner downListerner;
    public interface DownListerner{
        void getDownloadLink(String videoUrl);
    }
    public DownloadAdapter(DownListerner downListerner) {
        super(R.layout.quality_item);
        this.downListerner = downListerner;
    }

    @Override
    protected void convert(BaseViewHolder helper, AnimePaheDownloadBean.ResultsBean.DownloadBean item) {
        TextView tv_quality  = helper.getView(R.id.tv_quality);
        tv_quality.setText(item.getText());

        helper.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downListerner.getDownloadLink(item.getHref());
            }
        });
    }

}