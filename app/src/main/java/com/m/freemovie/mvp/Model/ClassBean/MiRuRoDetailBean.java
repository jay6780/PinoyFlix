package com.m.freemovie.mvp.Model.ClassBean;

import java.util.List;

public class MiRuRoDetailBean {


    /**
     * author : yazky
     * title : Killed Again, Mr. Detective. Episode 11 English subbed
     * altTitles : Mata Korosarete Shimatta no desu ne, Tantei-sama | Matakoro | また殺されてしまったのですね、探偵様 | Killed Again, Mr. Detective.
     * cover : https://i2.wp.com/miruro.ro/wp-content/uploads/2026/04/mata-korosarete-shimatta-no-desu-ne-tantei-sama.jpg?resize=246,350
     * synopsis : Sakuya Otsuki is the son of a legendary detective, working to follow in his father’s footsteps with his assistant, Lilithea. However, something’s different about this high-school sleuth. Wherever he goes, he always manages to get himself entangled in his cases—as a murder victim! When Sakuya is tasked to infiltrate a luxury cruise ship, he finds himself killed once again. But every time he reopens his eyes, Lilithea is there by his side, ready to help him get to the bottom of the mystery…
     (Source: Yen Press)
     * genres : ["Mystery","Romance"]
     * status : Ongoing
     * studio : LIDENFILMS
     * released : Apr 3, 2026 to ?
     * totalEpisodes : 0
     * episodes : []
     */

    private String author;
    private String title;
    private String altTitles;
    private String cover;
    private String synopsis;
    private String status;
    private String studio;
    private String released;
    private int totalEpisodes;
    private List<String> genres;
    private List<?> episodes;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAltTitles() {
        return altTitles;
    }

    public void setAltTitles(String altTitles) {
        this.altTitles = altTitles;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStudio() {
        return studio;
    }

    public void setStudio(String studio) {
        this.studio = studio;
    }

    public String getReleased() {
        return released;
    }

    public void setReleased(String released) {
        this.released = released;
    }

    public int getTotalEpisodes() {
        return totalEpisodes;
    }

    public void setTotalEpisodes(int totalEpisodes) {
        this.totalEpisodes = totalEpisodes;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public List<?> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<?> episodes) {
        this.episodes = episodes;
    }
}
