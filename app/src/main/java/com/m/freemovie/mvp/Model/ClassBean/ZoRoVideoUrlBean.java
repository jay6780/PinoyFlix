package com.m.freemovie.mvp.Model.ClassBean;

import java.util.List;

public class ZoRoVideoUrlBean {

    /**
     * title : An Observation Log of My Fiancée Who Calls Herself a Villainess Episode 1
     * animeTitle : An Observation Log of My Fiancée Who Calls Herself a Villainess
     * episodeNumber : 1
     * type : TV Sub
     * releasedOn : April 3, 2026
     * postedBy : xorotv
     * image : https://zorotv.com.ro/wp-content/uploads/2026/04/jishou-akuyaku-reijou-na-konyakusha-no-kansatsu-kiroku.jpg
     * videoUrl : https://my.1anime.site/index.php?action=play&file=an-observation-log-of-my-fiance-who-calls-herself-a-villainess-episode-1.mp4
     * servers : [{"name":"HD-1","hash":"PGlmcmFtZSBuYW1lPSJteWlGcmFtZSIgd2lkdGg9IjYwMHB4IiBoZWlnaHQ9IjQwMHB4IiBzcmM9Imh0dHBzOi8vbXkuMWFuaW1lLnNpdGUvaW5kZXgucGhwP2FjdGlvbj1wbGF5JmZpbGU9YW4tb2JzZXJ2YXRpb24tbG9nLW9mLW15LWZpYW5jZS13aG8tY2FsbHMtaGVyc2VsZi1hLXZpbGxhaW5lc3MtZXBpc29kZS0xLm1wNCIgc2Nyb2xsaW5nPSJubyIgbWFyZ2lud2lkdGg9IjAiIG1hcmdpbmhlaWdodD0iMCIgc3R5bGU9ImJvcmRlcjowcHggbm9uZSAjZmZmZmZmOyI+PC9pZnJhbWU+"},{"name":"HD-2","hash":"PGlmcmFtZSBzcmM9Imh0dHBzOi8vdmlkdXAuc2l0ZS9wbGF5P2NkPTZ1TDBoYnNSTThQMUFzM1FoWHRldWx0ZVdqMjlsUkhPZnNUd1lzWnB1ZWpVcGIwTTlublVuSzF3RlJsKzVoVWw1ZG56VnhKVWVxbW82dTRmcDQ2QTN3SkR5b0d5THduczZoRDZyT3lvRUt0dFJtT3g5MkFlQ2Fxdmt4VTlhZTNHbi9KRit2MWM2Q3c2NVhOeFpKU0kmYXV0b3BsYXk9MSIgd2lkdGg9IjEwMCUiIHN0eWxlPSJhc3BlY3QtcmF0aW86MTYvOTsgYm9yZGVyOjA7IiBhbGxvd2Z1bGxzY3JlZW4gbG9hZGluZz0ibGF6eSI+PC9pZnJhbWU+"}]
     */

    private String title;
    private String animeTitle;
    private String episodeNumber;
    private String type;
    private String releasedOn;
    private String postedBy;
    private String image;
    private String videoUrl;
    private List<ServersBean> servers;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAnimeTitle() {
        return animeTitle;
    }

    public void setAnimeTitle(String animeTitle) {
        this.animeTitle = animeTitle;
    }

    public String getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(String episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReleasedOn() {
        return releasedOn;
    }

    public void setReleasedOn(String releasedOn) {
        this.releasedOn = releasedOn;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public List<ServersBean> getServers() {
        return servers;
    }

    public void setServers(List<ServersBean> servers) {
        this.servers = servers;
    }

    public static class ServersBean {
        /**
         * name : HD-1
         * hash : PGlmcmFtZSBuYW1lPSJteWlGcmFtZSIgd2lkdGg9IjYwMHB4IiBoZWlnaHQ9IjQwMHB4IiBzcmM9Imh0dHBzOi8vbXkuMWFuaW1lLnNpdGUvaW5kZXgucGhwP2FjdGlvbj1wbGF5JmZpbGU9YW4tb2JzZXJ2YXRpb24tbG9nLW9mLW15LWZpYW5jZS13aG8tY2FsbHMtaGVyc2VsZi1hLXZpbGxhaW5lc3MtZXBpc29kZS0xLm1wNCIgc2Nyb2xsaW5nPSJubyIgbWFyZ2lud2lkdGg9IjAiIG1hcmdpbmhlaWdodD0iMCIgc3R5bGU9ImJvcmRlcjowcHggbm9uZSAjZmZmZmZmOyI+PC9pZnJhbWU+
         */

        private String name;
        private String hash;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }
    }
}
