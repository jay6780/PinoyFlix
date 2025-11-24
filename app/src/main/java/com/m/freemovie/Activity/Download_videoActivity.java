package com.m.freemovie.Activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.m.freemovie.R;
import com.m.freemovie.adapter.FileAdapter;
import com.m.freemovie.fileUtils.FilesExtractor;
import com.m.freemovie.fileUtils.VideoFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Download_videoActivity extends AppCompatActivity implements FileAdapter.DeleteListerner {
    private ImageView btn_back5;
    private LinearLayout ll_bg,ll_empty;
    private RecyclerView file_recycler;
    private FileAdapter fileAdapter;
    private ImageView delete_btn;
    private boolean isdelete = false;
    private TextView delete_now;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_video);
        getSupportActionBar().hide();
        initializeViews();
        setupTheme();
        setupFileList();
        setupDeleteButton();
    }

    private void initializeViews() {
        delete_btn = findViewById(R.id.delete_btn);
        delete_now = findViewById(R.id.delete_now);
        btn_back5 = findViewById(R.id.btn_back5);
        ll_bg = findViewById(R.id.ll_bg);
        file_recycler = findViewById(R.id.file_recycler);
        ll_empty = findViewById(R.id.ll_empty);
    }

    private void setupTheme() {
        btn_back5.setOnClickListener(view -> onBackPressed());
        delete_btn.setImageResource(R.mipmap.delete_white);
        ll_bg.setBackgroundColor(Color.parseColor("#313647"));
        btn_back5.setImageResource(R.mipmap.back_white);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setupFileList() {
        FilesExtractor filesExtractor = new FilesExtractor(Download_videoActivity.this);
        ArrayList<VideoFile> videoFiles = filesExtractor.listVideos();
        videoFiles.sort((v1, v2) -> Long.compare(v2.getLastModified(), v1.getLastModified()));
        file_recycler.setLayoutManager(new GridLayoutManager(this, 2));
        fileAdapter = new FileAdapter(this, videoFiles, this);
        file_recycler.setAdapter(fileAdapter);
        delete_btn.setVisibility(videoFiles.isEmpty() ? View.GONE : View.VISIBLE);

        if (!videoFiles.isEmpty()) {
            file_recycler.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
            file_recycler.post(() -> file_recycler.scrollToPosition(0));
        }else{
            file_recycler.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        }
    }

    private void setupDeleteButton() {
        delete_btn.setOnClickListener(view -> visibledelete());
    }

    private void visibledelete() {
        isdelete = !isdelete;
        if(isdelete){
            delete_now.setVisibility(View.VISIBLE);
            delete_btn.setImageResource(R.mipmap.eraser_white);
        }else{
            delete_now.setVisibility(View.GONE);
            delete_btn.setImageResource(R.mipmap.delete_white);
            selectedFiles.clear();
            delete_now.setText("Delete");
        }
        fileAdapter.showcheckBox(isdelete);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    private List<VideoFile> selectedFiles = new ArrayList<>();
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void deletefiles(List<VideoFile> videoFiles) {
        this.selectedFiles = videoFiles;
        delete_now.setText(videoFiles.isEmpty() ? "Delete" : "Delete (" + videoFiles.size() + ")");
        delete_now.setOnClickListener(view -> {
            if (selectedFiles != null && !selectedFiles.isEmpty()) {
                AlertDialog alertDialog = new AlertDialog.Builder(Download_videoActivity.this, R.style.AlertDialogTheme)
                        .setTitle("Delete Videos")
                        .setMessage("Are you sure you want to delete " + selectedFiles.size() + " selected videos?")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            boolean allDeleted = true;
                            for (VideoFile videoFile : selectedFiles) {
                                File fileToDelete = new File(videoFile.path);
                                if (!(fileToDelete.exists() && fileToDelete.delete())) {
                                    allDeleted = false;
                                }
                            }
                            if (allDeleted) {
                                FilesExtractor filesExtractor = new FilesExtractor(Download_videoActivity.this);
                                ArrayList<VideoFile> updatedVideoFiles = filesExtractor.listVideos();
                                updatedVideoFiles.sort((v1, v2) -> Long.compare(v2.getLastModified(), v1.getLastModified()));
                                fileAdapter.updateVideoFiles(updatedVideoFiles);
                                reSyncUi();
                            } else {
                                Toast.makeText(getApplicationContext(), "Some files couldn't be deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, (dialog, which) -> {
                            reSyncUi();
                            dialog.dismiss();
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .create();

                alertDialog.setOnShowListener(dialog -> {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                });

                alertDialog.show();

            } else {
                Toast.makeText(getApplicationContext(), "Select at least one video to delete", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void reSyncUi(){
        selectedFiles.clear();
        delete_now.setText("Delete");
        isdelete = false;
        delete_btn.setImageResource(R.mipmap.delete_white);
        fileAdapter.showcheckBox(false);
        delete_now.setVisibility(View.GONE);
        file_recycler.setVisibility(View.GONE);
        ll_empty.setVisibility(View.VISIBLE);
    }
}
