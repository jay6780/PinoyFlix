package com.m.freemovie.mvp.Model.ClassBean;

public class PinoyAllBean {
    private String link;
    private String title;
    private String id;
    private String thumbnailUrl;
    private String videoId,videoIdSecond;
    private boolean isThumbnailLoaded;
    private int type;

    public PinoyAllBean(String link, String title, String id,int type) {
        this.link = link;
        this.title = title;
        this.id = id;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }


    public String getVideoIdSecond() {
        return videoIdSecond;
    }

    public void setVideoIdSecond(String videoIdSecond) {
        this.videoIdSecond = videoIdSecond;
    }


    public boolean isThumbnailLoaded() {
        return isThumbnailLoaded;
    }

    public void setThumbnailLoaded(boolean thumbnailLoaded) {
        isThumbnailLoaded = thumbnailLoaded;
    }


}