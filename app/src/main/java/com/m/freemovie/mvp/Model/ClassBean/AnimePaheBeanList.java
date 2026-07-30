package com.m.freemovie.mvp.Model.ClassBean;

import java.util.List;

public class AnimePaheBeanList {
    String EpisodeUrl;
    String Episode;
    String imageUrl;
    String session;
    boolean isWatched;
    private String videoId;
    private List<AniKoToWatchBean.EpisodesBean.ServersBean> serversBeans;

    public AnimePaheBeanList(String EpisodeUrl, String Episode, String imageUrl, String session, List<AniKoToWatchBean.EpisodesBean.ServersBean> serversBeans) {
        this.EpisodeUrl = EpisodeUrl;
        this.Episode = Episode;
        this.imageUrl = imageUrl;
        this.session = session;
        this.serversBeans = serversBeans;
    }

    public List<AniKoToWatchBean.EpisodesBean.ServersBean> getServersBeans() {
        return serversBeans;
    }

    public void setEpisodeUrl(String episodeUrl) {
        EpisodeUrl = episodeUrl;
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
