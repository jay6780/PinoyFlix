package com.m.freemovie.mvp.ClassBean;

public class EpisodeBean {
    private int episodeNum;
    private int SeasonNum;
    private String thumbImage;
    private String id;
    private String title;
    private boolean isWatched;
    private String seasonId;

    public EpisodeBean(int episodeNum, String thumbImage, int SeasonNum, String id, String title,String seasonId){
        this.episodeNum = episodeNum;
        this.thumbImage = thumbImage;
        this.id = id;
        this.SeasonNum = SeasonNum;
        this.title = title;
        this.isWatched = false;
        this.seasonId = seasonId;
    }
    public boolean isWatched() {
        return isWatched;
    }

    public void setWatched(boolean watched) {
        isWatched = watched;
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

    public String getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(String seasonId) {
        this.seasonId = seasonId;
    }
}