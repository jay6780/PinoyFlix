package com.m.freemovie.Utils;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.m.freemovie.R;
import com.m.freemovie.adapter.AllSourceAdapter;
import com.m.freemovie.mvp.Model.ClassBean.AllSourceBean;
import com.m.freemovie.mvp.Model.ClassBean.AniNekoEpisodeBean;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public class DialogSourceUtils {
    private List<AllSourceBean> allSourceBeans = new ArrayList<>();

    public DialogPlus ShowDialog(AllSourceAdapter.SrcListener srcListener, Activity activity, List<AniNekoEpisodeBean.EpisodeBean.PlayerBean.ServersBeanX.ServerGroupsBean.ServersBean> stereamBeanList) {
        DialogPlus sourceDialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.dialog_select_quality))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.CENTER)
                .setCancelable(true)
                .setPadding(10, 10, 10, 10)
                .create();

        View dialogView = sourceDialog.getHolderView();
        RecyclerView recyclerView = dialogView.findViewById(R.id.rv_quality);
        AllSourceAdapter adapter = new AllSourceAdapter(srcListener);

        for (AniNekoEpisodeBean.EpisodeBean.PlayerBean.ServersBeanX.ServerGroupsBean.ServersBean sourceBean : stereamBeanList) {
            allSourceBeans.add(new AllSourceBean(sourceBean.getText(), sourceBean.getVideoUrl()));
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(adapter);
        adapter.setNewData(allSourceBeans);
        sourceDialog.show();

        return sourceDialog;
    }

    public DialogPlus showAnikotoSource(AllSourceAdapter.SrcListener aniKoToSourceListener, Activity activity, List<AllSourceBean> allSourceBeanList) {
        DialogPlus anikoToSourceDialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.dialog_select_quality))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.CENTER)
                .setCancelable(true)
                .setPadding(10, 10, 10, 10)
                .create();

        View dialogView = anikoToSourceDialog.getHolderView();
        RecyclerView recyclerView = dialogView.findViewById(R.id.rv_quality);
        AllSourceAdapter adapter = new AllSourceAdapter(aniKoToSourceListener);

        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(adapter);
        adapter.setNewData(allSourceBeanList);
        anikoToSourceDialog.show();

        return anikoToSourceDialog;
    }

    public DialogPlus ShowAniMoAdapter(AllSourceAdapter.SrcListener srcListener, Activity activity, List<AllSourceBean> allSourceBeanList) {
        DialogPlus aniMoSourceDialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.dialog_select_quality))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.CENTER)
                .setCancelable(true)
                .setPadding(10, 10, 10, 10)
                .create();

        View dialogView = aniMoSourceDialog.getHolderView();
        RecyclerView recyclerView = dialogView.findViewById(R.id.rv_quality);
        AllSourceAdapter adapter = new AllSourceAdapter(srcListener);

        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(adapter);
        adapter.setNewData(allSourceBeanList);
        aniMoSourceDialog.show();

        return aniMoSourceDialog;
    }

    public DialogPlus ShowAnimePaHeSoruce(AllSourceAdapter.SrcListener srcListener, Activity activity, List<AllSourceBean> allSourceBeanList) {
        DialogPlus aniMoSourceDialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.dialog_select_quality))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.CENTER)
                .setCancelable(true)
                .setPadding(10, 10, 10, 10)
                .create();

        View dialogView = aniMoSourceDialog.getHolderView();
        RecyclerView recyclerView = dialogView.findViewById(R.id.rv_quality);
        AllSourceAdapter adapter = new AllSourceAdapter(srcListener);

        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(adapter);
        adapter.setNewData(allSourceBeanList);
        aniMoSourceDialog.show();

        return aniMoSourceDialog;
    }

}
