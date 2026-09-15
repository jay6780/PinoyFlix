package com.m.freemovie.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.m.freemovie.Activity.Details_activity;
import com.m.freemovie.Activity.DownloadWebview;
import com.m.freemovie.Activity.VideoWebviewActivity;
import com.m.freemovie.R;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.adapter.AllSourceAdapter;
import com.m.freemovie.adapter.EpisodeAdapter;
import com.m.freemovie.adapter.MovieListAdapter;
import com.m.freemovie.adapter.PiNoyMediaAdapter;
import com.m.freemovie.adapter.PiNoyMediaListAdapter;
import com.m.freemovie.mvp.Model.ClassBean.AllSourceBean;
import com.m.freemovie.mvp.Model.ClassBean.AniNekoEpisodeBean;
import com.m.freemovie.mvp.Model.ClassBean.PinoyMediaDetailBean;
import com.m.freemovie.mvp.Model.ClassBean.PinoyRuDetailBean;
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
            allSourceBeans.add(new AllSourceBean(sourceBean.getText(), sourceBean.getVideoUrl(),""));
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

    public AlertDialog.Builder WatchSourceMovie(Activity activity,String title,String id,int apiPosition) {
        String[] option = {"Player 1", "Player 2", "Player 3", "Player 4", "Download"};
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        TextView titleView = new TextView(activity);
        titleView.setText("Select player");
        titleView.setTextColor(Color.BLACK);
        titleView.setPadding(40, 40, 40, 20);
        titleView.setTextSize(15);

        builder.setCustomTitle(titleView);

        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = null;
                switch (which) {
                    case 0:
                        intent = new Intent(activity, VideoWebviewActivity.class);
                        intent.putExtra("title", title);
                        intent.putExtra("videoPosition", 1);
                        intent.putExtra("videoId", id);
                        intent.putExtra("apiPosition", apiPosition);
                        break;
                    case 1:
                        intent = new Intent(activity, VideoWebviewActivity.class);
                        intent.putExtra("title", title);
                        intent.putExtra("videoPosition", 2);
                        intent.putExtra("videoId", id);
                        break;
                    case 2:
                        intent = new Intent(activity, VideoWebviewActivity.class);
                        intent.putExtra("title", title);
                        intent.putExtra("videoPosition", 3);
                        intent.putExtra("videoId", id);
                        break;
                    case 3:
                        intent = new Intent(activity, VideoWebviewActivity.class);
                        intent.putExtra("title", title);
                        intent.putExtra("videoPosition", 4);
                        intent.putExtra("videoId", id);
                        break;
                    case 4:
                        String downloadUrl = "https://vidvault.to/movie/" + id;
                        intent = new Intent(activity, DownloadWebview.class);
                        intent.putExtra("DownloadUrl", downloadUrl);
                        intent.putExtra("EpisodeNum", "");
                        intent.putExtra("title", title);
                        break;
                }
                activity.startActivity(intent);
            }
        });
        builder.show();

        return builder;
    }

    public AlertDialog.Builder MovieListSource (Context mContext, String id, String title, MovieListAdapter.MovieIdListener movieIdListener, int apiPosition){
        String[] option = {"Player 1","Player 2","Player 3","Player 4" ,"View Details","Download"};

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        movieIdListener.getMovieId(id,title,1);
                        break;
                    case 1:
                        movieIdListener.getMovieId(id,title,2);
                        break;
                    case 2:
                        movieIdListener.getMovieId(id,title,3);
                        break;
                    case 3:
                        movieIdListener.getMovieId(id,title,4);;
                        break;
                    case 4:
                        Intent intent = new Intent(mContext, Details_activity.class);
                        intent.putExtra("id",id);
                        intent.putExtra("position",1);
                        intent.putExtra("apiPosition",apiPosition);
                        mContext.startActivity(intent);
                        break;
                    case 5:
                        String downloadUrl = "https://vidvault.to/movie/" + id;
                        Intent download = new Intent(mContext, DownloadWebview.class);
                        download.putExtra("DownloadUrl", downloadUrl);
                        download.putExtra("EpisodeNum", "");
                        download.putExtra("title", title);
                        mContext.startActivity(download);
                        break;
                }
            }
        });
        builder.show();
        return builder;
    }

    public AlertDialog.Builder TvSeriesSource(Context mContext, String id, int seasonNum, int EpisodeNum, EpisodeAdapter.SourceListener sourceListener) {
        String[] videoPlayer = {"Player 1", "Player 2", "Player 3", "Player 4"};
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        TextView titleView = new TextView(mContext);
        titleView.setText("Select player");
        titleView.setTextColor(Color.BLACK);
        titleView.setPadding(40, 40, 40, 20);
        titleView.setTextSize(15);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });

        builder.setCustomTitle(titleView);

        builder.setItems(videoPlayer, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        sourceListener.getId(id, 1, seasonNum, EpisodeNum);
                        break;
                    case 1:
                        sourceListener.getId(id, 2, seasonNum, EpisodeNum);
                        break;
                    case 2:
                        sourceListener.getId(id, 3, seasonNum, EpisodeNum);
                        break;
                    case 3:
                        sourceListener.getId(id, 4, seasonNum, EpisodeNum);
                        break;
                }
            }
        });
        builder.show();
        return builder;
    }

    public DialogPlus TagalogWatchSource(Activity activity, int type, String title, List<PinoyRuDetailBean> pinoyRuDetailBeanList) {
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.dialog_ru_pinoy))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.CENTER)
                .setCancelable(true)
                .setPadding(10, 10, 10, 10)
                .create();

        View dialogView = dialog.getHolderView();
        RecyclerView recyclerView = dialogView.findViewById(R.id.rv_ru);
        PiNoyMediaAdapter dataAdapter = new PiNoyMediaAdapter();
        dataAdapter.setType(type);


        dataAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.tv_download) {
                    dialog.dismiss();
                    Intent intent = new Intent(activity, DownloadWebview.class);
                    intent.putExtra("DownloadUrl", dataAdapter.getData().get(position).getLink());
                    intent.putExtra("EpisodeNum", "");
                    intent.putExtra("title", title);
                    activity.startActivity(intent);
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(dataAdapter);
        dataAdapter.setNewData(pinoyRuDetailBeanList);
        dialog.show();
        return dialog;
    }

    public DialogPlus TagalogListSource(Activity activity, PiNoyMediaListAdapter.SourceListener sourceListener, PinoyMediaDetailBean bean, List<PinoyRuDetailBean> pinoyRuDetailBeanList) {
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.dialog_ru_pinoy))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.CENTER)
                .setCancelable(true)
                .setPadding(10, 10, 10, 10)
                .create();

        View dialogView = dialog.getHolderView();
        RecyclerView recyclerView = dialogView.findViewById(R.id.rv_ru);
        PiNoyMediaListAdapter dataAdapter = new PiNoyMediaListAdapter(sourceListener);


        dataAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.tv_download) {
                    Intent intent = new Intent(activity, DownloadWebview.class);
                    intent.putExtra("DownloadUrl", dataAdapter.getData().get(position).getLink());
                    intent.putExtra("EpisodeNum", "");
                    intent.putExtra("title", bean.getResults().getTitle());
                    activity.startActivity(intent);
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(dataAdapter);
        dataAdapter.setNewData(pinoyRuDetailBeanList);
        dialog.show();

        return dialog;
    }

}
