package com.m.freemovie.mvp.Model.ClassBean;

public class TagalogDetailBean {
    String EpisodeUrl;
    String Episode;
    String imageUrl;
    String videoId;
    boolean isWatched;

    public TagalogDetailBean(String EpisodeUrl,String Episode,String imageUrl){
        this.EpisodeUrl = EpisodeUrl;
        this.Episode = Episode;
        this.imageUrl = imageUrl;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public boolean isWatched() {
        return isWatched;
    }

    public void setWatched(boolean watched) {
        isWatched = watched;
    }

    public String getEpisodeUrl() {
        return EpisodeUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getEpisode() {
        return Episode;
    }
}
