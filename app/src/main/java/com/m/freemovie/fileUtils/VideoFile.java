package com.m.freemovie.fileUtils;

public class VideoFile {
    public String title, path;
    public long size, lastModified;
    private boolean selected = false;

    public VideoFile(String title, String path, long size, long lastModified) {
        this.title = title;
        this.path = path;
        this.size = size;
        this.lastModified = lastModified;
    }

    public long getSize() {
        return size;
    }

    public String getPath() {
        return path;
    }

    public long getLastModified() {
        return lastModified;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}