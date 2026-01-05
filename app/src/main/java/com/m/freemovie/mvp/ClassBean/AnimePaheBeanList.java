package com.m.freemovie.mvp.ClassBean;

public class AnimePaheBeanList {
    String EpisodeUrl;
    String Episode;
    String imageUrl;
    String session;
    boolean isWatched;

    public AnimePaheBeanList(String EpisodeUrl, String Episode, String imageUrl,String session){
        this.EpisodeUrl = EpisodeUrl;
        this.Episode = Episode;
        this.imageUrl = imageUrl;
        this.session = session;
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
