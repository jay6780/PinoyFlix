package com.m.freemovie.mvp.ClassBean;

public class TagalogEpisode {
    String episode;
    String imageUrl;
    String videoUrl;

    public TagalogEpisode(String episode,String imageUrl,String videoUrl){
        this.episode = episode;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
    }

    public String getEpisode() {
        return episode;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }
}
