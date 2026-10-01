package com.m.freemovie.mvp.Model.ClassBean;

import java.util.List;

public class MovieApiBean {

    /**
     * type : movie
     * count : 4
     * providers : [{"name":"Videasy","baseUrl":"https://player.videasy.ws/embed/movie","url":"https://player.videasy.ws/embed/movie"},{"name":"MoviesAPI","baseUrl":"https://moviesapi.to/movie","url":"https://moviesapi.to/movie"},{"name":"Vidrock","baseUrl":"https://vidrock.to/movie","url":"https://vidrock.to/movie"},{"name":"Vidfast","baseUrl":"https://vidfast.vc/movie","url":"https://vidfast.vc/movie"}]
     */

    private String type;
    private int count;
    private List<ProvidersBean> providers;
    private String downloadurl;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<ProvidersBean> getProviders() {
        return providers;
    }

    public void setProviders(List<ProvidersBean> providers) {
        this.providers = providers;
    }

    public String getDownloadurl() {
        return downloadurl;
    }

    public void setDownloadurl(String downloadurl) {
        this.downloadurl = downloadurl;
    }

    public static class ProvidersBean {
        /**
         * name : Videasy
         * baseUrl : https://player.videasy.ws/embed/movie
         * url : https://player.videasy.ws/embed/movie
         */

        private String name;
        private String baseUrl;
        private String url;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
