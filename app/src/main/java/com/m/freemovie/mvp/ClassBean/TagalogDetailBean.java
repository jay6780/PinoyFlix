package com.m.freemovie.mvp.ClassBean;

public class TagalogDetailBean {
    String EpisodeUrl;
    String Episode;
    String imageUrl;


    public TagalogDetailBean(String EpisodeUrl,String Episode,String imageUrl){
        this.EpisodeUrl = EpisodeUrl;
        this.Episode = Episode;
        this.imageUrl = imageUrl;
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
