package com.m.freemovie.mvp.Model.ClassBean;

public class AnimeDetailsBean {
    private String id;
    private String imageUrl;
    private String title;
    private int episodes;
    private String airDate;
    public AnimeDetailsBean(String id,String imageUrl, String title,int episodes,String airDate){
        this.id = id;
        this.imageUrl = imageUrl;
        this.title = title;
        this.episodes = episodes;
        this.airDate = airDate;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getEpisodes() {
        return episodes;
    }

    public void setEpisodes(int episodes) {
        this.episodes = episodes;
    }

    public String getAirDate() {
        return airDate;
    }
}
