package com.m.freemovie.mvp.Model.ClassBean;

import java.util.List;

public class ZoRoDetailBean {

    /**
     * title : A Misanthrope Teaches a Class for Demi-Humans
     * status : Ongoing
     * type : TV
     * postedBy : xorotv
     * releasedOn : 2026-01-13T21:58:58+00:00
     * updatedOn : April 5, 2026
     * description : Watch streaming A Misanthrope Teaches a Class for Demi-Humans English Subbed on Zoro TV. You can also download free A Misanthrope Teaches a Class for Demi-Humans Eng Sub, don't forget to watch online streaming of various quality 720P 360P 240P 480P according to your connection to save internet quota, A Misanthrope Teaches a Class for Demi-Humans on Zoro TV MP4 MKV hardsub softsub English subbed is already contained in the video.
     * synopsis : Rei Hitoma, a self-proclaimed misanthrope, takes a teaching job in the mountains hoping for peace but his new students aren’t ordinary kids. They’re demi-humans striving to become fully human: a mermaid, a werewolf, a rabbit, and a bird, all under care. Tasked with teaching them about humanity, Rei finds himself learning valuable lessons too. This heartfelt story blends humor, fantasy, and personal growth in a school unlike an.
     (Source: Yen Press)
     * episodes : [{"number":"13","url":"https://zorotv.com.ro/a-misanthrope-teaches-a-class-for-demi-humans-episode-13-2/"},{"number":"12","url":"https://zorotv.com.ro/a-misanthrope-teaches-a-class-for-demi-humans-episode-12/"},{"number":"11","url":"https://zorotv.com.ro/a-misanthrope-teaches-a-class-for-demi-humans-episode-11/"},{"number":"10","url":"https://zorotv.com.ro/a-misanthrope-teaches-a-class-for-demi-humans-episode-10/"},{"number":"9","url":"https://zorotv.com.ro/a-misanthrope-teaches-a-class-for-demi-humans-episode-9/"},{"number":"8","url":"https://zorotv.com.ro/a-misanthrope-teaches-a-class-for-demi-humans-episode-8/"},{"number":"7","url":"https://zorotv.com.ro/a-misanthrope-teaches-a-class-for-demi-humans-episode-7/"},{"number":"6","url":"https://zorotv.com.ro/a-misanthrope-teaches-a-class-for-demi-humans-episode-6/"},{"number":"5","url":"https://zorotv.com.ro/a-misanthrope-teaches-a-class-for-demi-humans-episode-5/"},{"number":"4","url":"https://zorotv.com.ro/a-misanthrope-teaches-a-class-for-demi-humans-episode-4/"},{"number":"3","url":"https://zorotv.com.ro/a-misanthrope-teaches-a-class-for-demi-humans-episode-13/"},{"number":"2","url":"https://zorotv.com.ro/a-misanthrope-teaches-a-class-for-demi-humans-episode-2/"},{"number":"1","url":"https://zorotv.com.ro/a-misanthrope-teaches-a-class-for-demi-humans-episode-1/"}]
     */

    private String title;
    private String status;
    private String type;
    private String postedBy;
    private String releasedOn;
    private String updatedOn;
    private String description;
    private String synopsis;
    private List<EpisodesBean> episodes;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getReleasedOn() {
        return releasedOn;
    }

    public void setReleasedOn(String releasedOn) {
        this.releasedOn = releasedOn;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public List<EpisodesBean> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<EpisodesBean> episodes) {
        this.episodes = episodes;
    }

    public static class EpisodesBean {
        /**
         * number : 13
         * url : https://zorotv.com.ro/a-misanthrope-teaches-a-class-for-demi-humans-episode-13-2/
         */

        private String number;
        private String url;

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
