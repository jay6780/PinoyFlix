package com.m.freemovie.mvp.ClassBean;

public class EpisodeBean {
    private int episodeNum;
    private int SeasonNum;
    private String thumbImage;
    private String id;
    private String title;

    public EpisodeBean(int episodeNum,String thumbImage,int SeasonNum,String id,String title){
        this.episodeNum = episodeNum;
        this.thumbImage = thumbImage;
        this.id = id;
        this.SeasonNum = SeasonNum;
        this.title = title;
    }

    public int getEpisodeNum() {
        return episodeNum;
    }

    public void setEpisodeNum(int episodeNum) {
        this.episodeNum = episodeNum;
    }

    public String getThumbImage() {
        return thumbImage;
    }

    public void setThumbImage(String thumbImage) {
        this.thumbImage = thumbImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSeasonNum() {
        return SeasonNum;
    }

    public void setSeasonNum(int seasonNum) {
        SeasonNum = seasonNum;
    }
}
