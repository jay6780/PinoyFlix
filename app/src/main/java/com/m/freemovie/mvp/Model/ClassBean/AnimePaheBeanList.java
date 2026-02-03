package com.m.freemovie.mvp.Model.ClassBean;

public class AnimePaheBeanList {
    String EpisodeUrl;
    String Episode;
    String imageUrl;
    String session;
    boolean isWatched;
    private String videoId;

    public AnimePaheBeanList(String EpisodeUrl, String Episode, String imageUrl,String session){
        this.EpisodeUrl = EpisodeUrl;
        this.Episode = Episode;
        this.imageUrl = imageUrl;
        this.session = session;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getSession() {
        return session;
    }

    public void setEpisode(String episode) {
        Episode = episode;
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
