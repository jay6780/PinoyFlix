package com.m.freemovie.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.m.freemovie.R;
import com.m.freemovie.fileUtils.VideoFile;

import java.util.List;

public class VideoDownloadAdapter extends RecyclerView.Adapter<VideoDownloadAdapter.ViewHolder> {
    private List<VideoFile> videoFileList;
    private Context context;
    private int selectedposition = -1;
    private PlayPathListener playPathListener;
    public interface PlayPathListener{
        void getPath(String path,String title);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_thumb;
        TextView tv_name;
        RelativeLayout rl_select;

        public ViewHolder(View view) {
            super(view);
            iv_thumb = view.findViewById(R.id.iv_thumb);
            tv_name = view.findViewById(R.id.tv_name);
            rl_select = view.findViewById(R.id.rl_select);
        }
    }

    public VideoDownloadAdapter(Context context, List<VideoFile> videoFileList,PlayPathListener playPathListener) {
        this.context = context;
        this.videoFileList = videoFileList;
        this.playPathListener = playPathListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.download_video_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        VideoFile data = videoFileList.get(position);
        holder.tv_name.setText(data.getName());

        if(position == selectedposition){
            holder.rl_select.setBackgroundColor(Color.parseColor("#050E3C"));
        }else{
            holder.rl_select.setBackgroundColor(Color.parseColor("#313647"));
        }
        Glide.with(context)
                .asBitmap()
                .centerCrop()
                .load(data.getPath())
                .into(holder.iv_thumb);


         holder.itemView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if(position == selectedposition){
                     selectedposition = -1;
                     playPathListener.getPath("","");
                 }else{
                    selectedposition = position;
                     playPathListener.getPath(data.getPath(),data.getName());
                 }
                 notifyDataSetChanged();
             }
         });
    }



    @Override
    public int getItemCount() {
        return videoFileList.size();
    }
}