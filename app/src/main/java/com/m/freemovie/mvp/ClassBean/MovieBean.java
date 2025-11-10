package com.m.freemovie.mvp.ClassBean;

import java.util.List;

public class MovieBean {
    private List<MovieList> result;

    public List<MovieList> getResult() {
        return result;
    }

    public void setResult(List<MovieList> result) {
        this.result = result;
    }

    public static class MovieList{
        private String imdb_id;
        private String tmdb_id;
        private String title;
        private String embed_url;
        private String embed_url_tmdb;
        private String quality;

        public String getImdb_id() {
            return imdb_id;
        }

        public void setImdb_id(String imdb_id) {
            this.imdb_id = imdb_id;
        }

        public String getTmdb_id() {
            return tmdb_id;
        }

        public void setTmdb_id(String tmdb_id) {
            this.tmdb_id = tmdb_id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getEmbed_url() {
            return embed_url;
        }

        public void setEmbed_url(String embed_url) {
            this.embed_url = embed_url;
        }

        public String getEmbed_url_tmdb() {
            return embed_url_tmdb;
        }

        public void setEmbed_url_tmdb(String embed_url_tmdb) {
            this.embed_url_tmdb = embed_url_tmdb;
        }

        public String getQuality() {
            return quality;
        }

        public void setQuality(String quality) {
            this.quality = quality;
        }
    }
}
