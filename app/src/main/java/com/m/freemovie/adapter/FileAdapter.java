package com.m.freemovie.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.core.Controller;
import com.app.hubert.guide.listener.OnGuideChangedListener;
import com.app.hubert.guide.model.GuidePage;
import com.app.hubert.guide.model.HighLight;
import com.bumptech.glide.Glide;
import com.m.freemovie.Activity.Download_videoActivity;
import com.m.freemovie.Activity.FullViewVideoActivity;
import com.m.freemovie.R;
import com.m.freemovie.Utils.MoveFileUtils;
import com.m.freemovie.fileUtils.FilesExtractor;
import com.m.freemovie.fileUtils.VideoFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {
    private List<VideoFile> videoFileList;
    private Context context;
    private boolean isShow = false;
    private DeleteListerner deleteListerner;
    private MoveFileListerner moveFileListerner;
    public interface DeleteListerner {
        void deletefiles(List<VideoFile> videoFiles);
    }

    public interface MoveFileListerner {
        void movefiles(boolean isMove);
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
        CardView highlight_card;

        public ViewHolder(View view) {
            super(view);
            checkBox = view.findViewById(R.id.checkbox_delete);
            image_thumb = view.findViewById(R.id.image_thumb);
            file_name = view.findViewById(R.id.file_name);
            highlight_card = view.findViewById(R.id.highlight_card);
        }
    }

    public FileAdapter(Context context, List<VideoFile> videoFileList, DeleteListerner deleteListerner,MoveFileListerner moveFileListerner) {
        this.context = context;
        this.videoFileList = videoFileList;
        this.deleteListerner = deleteListerner;
        this.moveFileListerner = moveFileListerner;
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

        if(context instanceof Activity && position == 0){
            NewbieGuide.with((Activity) context)
                    .setLabel("long_press")
                    .setOnGuideChangedListener(new OnGuideChangedListener() {
                        @Override
                        public void onShowed(Controller controller) {}

                        @Override
                        public void onRemoved(Controller controller) {}
                    })
                    .addGuidePage(GuidePage.newInstance()
                            .addHighLight(holder.highlight_card, HighLight.Shape.ROUND_RECTANGLE, 1)
                            .setLayoutRes(R.layout.long_press)
                    )
                    .show();
        }

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

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(context, R.style.AlertDialogTheme)
                        .setTitle("Move video")
                        .setMessage("Are you sure you want to move " + data.getName())
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            moveFiles(data);
                        })
                        .setNegativeButton(android.R.string.no, (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .create();

                alertDialog.setOnShowListener(dialog -> {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                });

                alertDialog.show();
                return true;
            }
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

    private void moveFiles(VideoFile data) {
        File filetoMove = new File(data.path);
        if ((filetoMove.exists())) {
            new MoveFileUtils(context, filetoMove, "Free Movies");
            moveFileListerner.movefiles(true);
        }
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