package com.m.freemovie.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.m.freemovie.Activity.FullViewVideoActivity;
import com.m.freemovie.R;
import com.m.freemovie.fileUtils.VideoFile;

import java.util.ArrayList;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {
    private List<VideoFile> videoFileList;
    private Context context;
    private boolean isShow = false;
    private DeleteListerner deleteListerner;

    public interface DeleteListerner {
        void deletefiles(List<VideoFile> videoFiles);
    }

    public void showcheckBox(boolean isdelete) {
        isShow = isdelete;
        if (!isShow) {
            for (VideoFile videoFile : videoFileList) {
                videoFile.setSelected(false);
            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image_thumb;
        TextView file_name;
        CheckBox checkBox;

        public ViewHolder(View view) {
            super(view);
            checkBox = view.findViewById(R.id.checkbox_delete);
            image_thumb = view.findViewById(R.id.image_thumb);
            file_name = view.findViewById(R.id.file_name);
        }
    }

    public FileAdapter(Context context, List<VideoFile> videoFileList, DeleteListerner deleteListerner) {
        this.context = context;
        this.videoFileList = videoFileList;
        this.deleteListerner = deleteListerner;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        VideoFile data = videoFileList.get(position);
        holder.file_name.setText(data.getName());
        holder.checkBox.setVisibility(isShow ? View.VISIBLE : View.GONE);

        Glide.with(context)
                .asBitmap()
                .centerCrop()
                .load(data.getPath())
                .into(holder.image_thumb);

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(data.isSelected());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            data.setSelected(isChecked);
            List<VideoFile> selectedFiles = new ArrayList<>();
            for (VideoFile file : videoFileList) {
                if (file.isSelected()) {
                    selectedFiles.add(file);
                }
            }
            deleteListerner.deletefiles(selectedFiles);
        });

        holder.itemView.setOnClickListener(view -> {
            if (isShow) {
                boolean newState = !data.isSelected();
                data.setSelected(newState);
                holder.checkBox.setChecked(newState);
                List<VideoFile> selectedFiles = new ArrayList<>();
                for (VideoFile file : videoFileList) {
                    if (file.isSelected()) {
                        selectedFiles.add(file);
                    }
                }
                deleteListerner.deletefiles(selectedFiles);

            } else {
                Intent viewFullvideo = new Intent(context, FullViewVideoActivity.class);
                viewFullvideo.putExtra("videoURl", data.getPath());
                viewFullvideo.putExtra("isVisible", false);
                viewFullvideo.putExtra("videoTitle",data.getName());
                context.startActivity(viewFullvideo);
            }
        });
    }

    public void updateVideoFiles(List<VideoFile> newVideoFiles) {
        videoFileList = newVideoFiles;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return videoFileList.size();
    }
}