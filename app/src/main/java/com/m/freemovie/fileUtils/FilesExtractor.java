package com.m.freemovie.fileUtils;

import android.content.Context;
import android.os.Environment;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;

public class FilesExtractor {
    private final Context context;
    private ArrayList<VideoFile> videoFiles;
    private final ArrayList<String> defaultVExtensions = new ArrayList<>();

    public FilesExtractor(Context context) {
        this.context = context;
        defaultVExtensions.add(".mp4");
        defaultVExtensions.add(".mkv");
        defaultVExtensions.add(".webm");
        defaultVExtensions.add(".mpg");
        defaultVExtensions.add(".3gp");
        defaultVExtensions.add(".avi");
        defaultVExtensions.add(".tc");
    }
    public void addMoreExtensions(ArrayList<String> e){
        defaultVExtensions.addAll(e);
    }
    private ArrayList<String> getDcimPaths() {
        ArrayList<String> dcimPaths = new ArrayList<>();
        File[] rootsStorage = ContextCompat.getExternalFilesDirs(context, null);

        for (File file : rootsStorage) {
            String root = file.getAbsolutePath().replace("/Android/data/" + context.getPackageName() + "/files", "");
            String dcimPath = root + "/DCIM/Manyakol.com";
            File dcimDir = new File(dcimPath);
            if (dcimDir.exists() && dcimDir.isDirectory()) {
                dcimPaths.add(dcimPath);
            }
        }

        File primaryDcim = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Manyakol.com");
        if (primaryDcim.exists() && primaryDcim.isDirectory()) {
            String primaryPath = primaryDcim.getAbsolutePath();
            if (!dcimPaths.contains(primaryPath)) {
                dcimPaths.add(primaryPath);
            }
        }

        return dcimPaths;
    }

    private ArrayList<String> getExternalFiles() {
        ArrayList<String> videoPaths = new ArrayList<>();
        File[] rootsStorage = ContextCompat.getExternalFilesDirs(context, null);

        for (File file : rootsStorage) {
            if (file != null) {
                File videoDir = new File(file, "/Manyakol.com");
                if (videoDir.exists() && videoDir.isDirectory()) {
                    videoPaths.add(videoDir.getAbsolutePath());
                }
            }
        }

        return videoPaths;
    }

    private ArrayList<String> getInternalfiles() {
        ArrayList<String> videoPaths = new ArrayList<>();

        File internalDir = new File(context.getFilesDir(), "FreeMovie");

        if (internalDir.exists() && internalDir.isDirectory()) {
            videoPaths.add(internalDir.getAbsolutePath());
        }

        return videoPaths;
    }



    public ArrayList<VideoFile> listVideos(){
        videoFiles = new ArrayList<>();
        ArrayList<String> rootPaths = getInternalfiles();
        for (String path:rootPaths){
            getVideos(new File(path));
        }
        return videoFiles;
    }
    private void getVideos(File folder) {
        File[] folders1 = folder.listFiles();
        if (folders1!=null){
            for (File file :folders1){
                String name = file.getName();
                if (file.isDirectory()){
                    getVideos(file);
                }else {
                    int i = name.lastIndexOf(".");
                    String exe = i == -1 ? "" : name.substring(i);
                    if (defaultVExtensions.contains(exe)) videoFiles.add(new VideoFile(file.getName(),file.getAbsolutePath(),file.length(),file.lastModified()));
                }
            }
        }
    }
}